# GPS 迁移路线图

这份文档只说明一件事：GPS 功能完整迁移时，旧项目能力要怎么拆到新壳模块里。

## 当前整理结果

本轮已经把 GPS 从“报站模块附属能力”里先抽出一个独立落点：

- `modules/domain/.../domain/module/GpsBusinessModule.java`
- `modules/domain/.../domain/module/state/GpsState.java`

这个模块当前负责：

1. GPS 串口绑定
2. 当前定位状态汇总
3. 当前线路资源扫描
4. `L1` 基线扫描
5. 完整迁移缺口汇总

它现在不是完整 GPS 业务，只是完整迁移前的固定入口。

## 旧项目 GPS 功能拆分

旧项目里 GPS 实际上分成 5 层：

1. 串口监听与 NMEA 解析
2. 实时定位快照
3. 线路与提醒资源装载
4. 自动报站 / 友情提醒判定
5. 校时、学习落库、平台上报

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

后续继续补：

1. 自动报站状态机入口
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

当前状态：未迁移。

## 继续开发时的原则

1. GPS 判定逻辑优先落 `GpsBusinessModule` 或 `domain/gps`，不要继续堆进页面。
2. 报站模块只接收 GPS 结果，不再承担全部 GPS 细节。
3. 真机联调统一以 `L1` 为第一条完整闭环基线。