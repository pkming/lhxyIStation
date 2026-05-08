# GPS 迁移路线图

这份文档只说明一件事：GPS 功能完整迁移时，旧项目能力要怎么拆到新壳模块里。

先统一口径，避免继续混用历史基线：

1. GPS 当前唯一正式基线就是 `M90`
	- 自动报站
	- 友情提醒
	- 校时
	- 站点学习
	- 上报链
	- 串口口位和波特率
	- M90 当前 GPS 模组行为
	- 现机有效定位、触发节奏和硬件约束
2. 历史 `4.4` 资料只保留考古和辅助参考作用
3. 所以后面 GPS 迁移直接以 `M90` 当前可验证行为为准

## 当前整理结果

本轮已经把 GPS 从“报站模块附属能力”里先抽出一个独立落点：

- `modules/domain/.../domain/module/GpsBusinessModule.java`
- `modules/domain/.../domain/module/state/GpsState.java`

这个模块当前负责：

1. GPS 串口绑定
2. 当前定位状态汇总
3. 当前线路资源扫描
4. `L1` 基线扫描
5. 自动报站判定入口（诊断态）
6. GPS 校时入口（第一版，按旧 M90 首次有效定位规则）
7. 完整迁移缺口汇总

它现在不是完整 GPS 业务，只是完整迁移前的固定入口。

补一条当前已核实的基线事实：

- Android11 当前 GPS 核心口径继续以 M90 为准，不再回看 4.4 页面直连链。
- 串口/NMEA 解析对应 M90 `GPSMonitor` 的 `RMC / GGA / GSA` 句型。
- 自动报站判定对应 M90 `MainPresenter.getGpsRStationInfo()` 三分支规则；当前已修正为“对开线 5 个唯一站号后才触发自动切向”，并去掉了 M90 主链里不存在的海拔拦截。
- 友情提醒判定也按 M90 口径收回：`*Remind.csv` 当前真实列是“编号 / 提醒语音 / 经度 / 纬度 / 站前里程 / 外音开否”，提醒命中只看最近点 + `里程 + 20m`，不额外套角度门槛。
- 友情提醒当前已补进/出路口状态：`StationState` 现在会保存当前路口限速，首页限速显示会在“进路口”期间优先使用友情提醒里的路口限速，离开后再回落到站点限速。
- 友情提醒当前也已接回 M90 的 JT808 路口信息上报：enter 事件会写入路口到达时间并发送 `generateCrossInfo` 等价包，leave 事件会复用同一到达时间并补发离开时间；当前本地缺少独立 `crossCode` 时，先用提醒编号兼容填充 `crossNumber`。
- 当前仓库里的 `source/SourceFile/Bus/*Remind.csv` 仍然全部只有 6 列简版，不包含 M90 的 `UID / CrossInNotice / CrossOutNotice / SpeedLimit / CrossType` 等扩展字段；代码侧现已兼容 13 列完整版，一旦后续资源切回 M90 表头，就会优先读取真实 `crossCode` 并直接用于路口信息上报，不需要再改逻辑。
- 超速提醒当前也已跟着 M90 首页限速口径切换：友情提醒生效期间，`StationBusinessModule.handleSpeedWarning()` 会优先使用 `StationState.activeCrossSpeedLimit` 作为限速阈值，不再只看静态站点限速。
- 超速上报当前已补回“路口超速”这一支：友情提醒生效期间，只有车速严格大于当前限速时才会进入超速态，而且 GPS 有效数据还要先稳定 3 秒，随后 `StationBusinessModule` 才会发送等价于 M90 `generateOverspeedInfo(OverSpeedType=22)` 的 `C8` 包，并复用当前路口的 `crossCode / crossType / reminderNo`；重复触发节流也已收回到 M90 主链的 2.5 秒口径，不再按 Android11 之前的 1 秒频率连发。但“站点超速”两支 (`20/21`) 仍暂缺，因为当前仓库的站点 CSV 只有 16 列，缺少 M90 所需的 `UID / InSpeed limit / Speed limit` 双限速与站点编号字段，暂时无法无偏差复刻。
- GPS 校时条件与 M90 `MainActivity` 保持一致：首次有效定位、日期年份后缀不少于 `19`、按 `date + time + 8h` 组本地时间。

## 旧项目 GPS 功能拆分

旧项目里 GPS 实际上分成 5 层：

1. 串口监听与 NMEA 解析
2. 实时定位快照
3. 线路与提醒资源装载
4. 自动报站 / 友情提醒判定
5. 校时、学习落库、平台上报

## M90 执行主链

按 M90 旧代码当前可对出来的执行顺序，GPS 主链是：

1. GPS 串口持续进原始 NMEA 数据。
2. 监听层把 `RMC / GGA / GSA` 组句并合成实时定位快照。
3. 业务层按当前线路和方向装载 `Bus/*.csv` 站点与提醒资源。
4. 自动判定层对实时快照做最近站点匹配，产出：
	- 进站
	- 出站
	- 友情提醒
	- 自动切向
5. 副链同时消费同一份 GPS 数据：
	- 页面刷新
	- 超速语音
	- GPS 校时
	- 站点学习
	- 平台或串口 GPS 上报

Android11 当前这轮已把第 1 到第 4 步的核心编排开始收回 `domain/gps`，不再只挂在 `StationBusinessModule` 里。

## 新壳目标落点

### 1. GPS 基础层

落在：

- `modules/protocol/.../protocol/gps`
- `modules/domain/.../domain/gps/GpsSerialMonitor.java`

职责：

1. NMEA 分帧
2. `RMC / GGA / GSA` 解析
3. `GpsFixSnapshot` 合成
4. 串口监听状态输出

### 2. GPS 资源层

落在：

- `LegacyGpsRouteCatalog`
- `LegacyGpsRouteResource`

职责：

1. 读取旧 `Bus/*.csv`
2. 装载站点与友情提醒点
3. 按线路和方向提供同一份资源模型

### 3. GPS 业务层

当前落点：

- `GpsBusinessModule`
- `LegacyGpsFlowUseCase`

后续继续补：

1. 自动报站状态机入口从诊断态继续收成真实业务入口
2. 友情提醒触发入口
3. GPS 校时入口
4. 站点学习落库入口

### 4. 报站联动层

当前仍在：

- `StationBusinessModule`

后续目标：

1. GPS 模块负责产出判定结果
2. 报站模块只消费 GPS 业务结果和站态变更
3. 避免 GPS 判定逻辑继续膨胀在报站模块里

## 分阶段迁移顺序

### 第一阶段

目标：基础链稳定。

包括：

1. 串口监听
2. 定位快照
3. 路线装载
4. DVR GPS 上报

当前状态：已完成。

### 第二阶段

目标：自动 GPS 报站完整恢复。

包括：

1. 对开 / 环线 / 防反三支逻辑核对
2. 最近站点匹配
3. 进站 / 出站判定
4. 方向切换

当前状态：第一版已接，仍需真机按旧逻辑逐项对齐。

### 第三阶段

目标：友情提醒完整消费。

包括：

1. 提醒点坐标匹配
2. 角度过滤
3. 站前里程过滤
4. 提醒语音触发

当前状态：未完整迁移。

### 第四阶段

目标：GPS 外围功能补齐。

包括：

1. GPS 校时
2. 站点学习真实落库
3. 网络 GPS 上报等价恢复

当前状态：

1. GPS 校时已接第一版，当前按旧 M90 一次性有效定位规则执行。
2. 站点学习真实落库未迁移。
3. 网络 GPS 上报等价恢复未迁移。

## 继续开发时的原则

1. GPS 判定逻辑优先落 `GpsBusinessModule` 或 `domain/gps`，不要继续堆进页面。
2. 报站模块只接收 GPS 结果，不再承担全部 GPS 细节。
3. 真机联调统一以 `L1` 为第一条完整闭环基线。