# IStationDevice Android 11 新壳

这个目录是 Android 11 新项目复刻工程。

当前路线已经固定：

- 正式 UI 以旧 `4.4` 项目为唯一基线
- 新项目继续保留现在的新架构
- 做法是`旧 UI 壳还原 + 新逻辑接入 + 旧逻辑对照补齐`
- debug 页面继续保留，但只走 debug 路径

## 先看哪些文档

1. [docs/项目架构.md](./docs/项目架构.md)
2. [docs/调试与工作流.md](./docs/调试与工作流.md)
3. [docs/配置管理.md](./docs/配置管理.md)
4. [docs/需求变更记录.md](./docs/需求变更记录.md)
5. [docs/新壳与旧4.4差异记录.md](./docs/新壳与旧4.4差异记录.md)
6. [docs/UI与业务核对清单.md](./docs/UI与业务核对清单.md)
7. [docs/推进步骤.md](./docs/推进步骤.md)
8. [docs/新会话接手说明.md](./docs/新会话接手说明.md)

## 目录说明

```text
IStationDevice-Android11/
├── app/                # 新壳入口、页面、导航、调试入口
├── docs/               # 架构、工作流、变更记录
├── config/             # 运行配置模板和本地覆盖配置
├── modules/
│   ├── core/           # 通用能力、基础设施、日志、错误模型
│   ├── domain/         # 业务编排、配置模型、用例
│   ├── protocol/       # 各类协议编解码和会话封装
│   ├── device-api/     # 串口/Socket/GPIO/Camera/RFID/SystemOps 抽象接口
│   ├── device-m90/     # M90 终端适配实现
│   ├── runtime/        # 共享运行时、跨页面设备状态
│   └── debug-tools/    # 调试页、报文回放、串口测试工具
└── scripts/            # 构建辅助、日志整理、报文转换脚本
```

## 当前阶段

- 文档、目录、Gradle 骨架都已经起好。
- 当前 `:app:assembleDebug` 已通过。
- 正式 launcher 已经切到 `LegacyMainActivity`，旧版首页壳是当前正式入口。
- `MainActivity / ModuleCenterActivity` 继续保留，但已经降成过渡业务页，不再是正式首页入口。
- 调试页和业务模块页继续保留，但准备收成 debug 环境入口。
- 首批旧协议已经迁入 `modules/protocol`，目前有通达 / LHXY / 恒舞样例。
- 已补 `SocketClientAdapter`，`JT808 / AL808` 现在已经有最小公共打包内核和样例报文。
- `JT808 / AL808` 已继续迁入：
  - 报站信息
  - 通用应答
  - 违规信息
  - 司机考勤
  - 校时应答
  - 查询终端属性应答
  - 请求消息
  - 升级通知
  - 线路切换信息
  - AL808 切换线路应答
- 已单独补对讲包工厂
- 已补 GPS 纯解析器：
  - `RMC`
  - `GGA`
  - 串流分帧缓冲
  - 串流定位快照合成
  - 保留旧项目里会用到的经纬度、海拔、卫星数、速度、方向字段
- 已补 `GpsSerialMonitor`
  - 后面把 `ttyS5` 打开并绑定监听后，就能直接产出定位快照日志
- 配置已经单独收口到 `config/`，构建时会自动打包进 assets。
- 应用启动时会自动落一份运行期配置，可直接替换后热刷新。
- 首页和调试页已经切到共享运行时，不再各自 new 一套设备适配器。
- `app` 现在除了正式业务首页，还补了业务模块页，六个模块都有独立入口。
- 旧版首页 / 登录 / 菜单三页骨架已经迁入新壳：
  - launcher 已切到旧版首页骨架
  - 首页 -> 登录 -> 菜单 这条正式导航主链已恢复
  - 菜单八宫格样式已恢复，子页面后续逐页接入
- 菜单页第一批八个正式子页面骨架已经接上：
  - 线路选择
  - 站点学习
  - 文件管理
  - 系统设置
  - 语音通话
  - 调度中心
  - 信息浏览
  - 系统信息
- 当前菜单点击已经不再是占位 toast，而是会进入对应正式页面骨架。
- `Dispatch / BasicSetup / SiteCollection / SystemInfo` 这四类页面的右侧内容区已经开始挂旧 `f_*.xml` 骨架，不再只是文字占位。
- 为了让旧 Fragment 布局直接复用，已经补了：
  - `LegacyLayoutFragment`
  - `CustomScrollView`
  - `CompanyEdittext`
- 调度中心右侧内容区已经开始挂首批真实逻辑：
  - `f_attendance` 已接签到模块状态和签到/签退动作
  - `f_dispatch` 已接调度模块动作
- 系统信息右侧内容区也已经开始挂首批真实状态：
  - `f_versioninfo` 已接设备型号、软件版本和配置版本
  - `f_networkinfo` 已接 GPS、LAN、WIFI、4G、服务器和 Socket 连接状态
- 系统设置右侧内容区已经从纯骨架推进到“旧布局 + 新配置入口”：
  - `f_newspaper_setup` 已接默认音量、播报和确认动作
  - `f_network` 已接当前 Socket 配置展示
  - `f_serial_port` 已接当前串口配置展示和测试动作
  - `f_tts_setup / f_language / f_other_setup / f_wireless_setup` 已接默认状态和确认入口
- 站点学习右侧内容区也已经开始挂首批真实逻辑：
  - `f_site_collection` 已接线路/站点占位选择、GPS 当前值显示和“学习下一项”动作
  - `f_other_collection` 已接属性占位选择、GPS 当前值显示和“学习下一项”动作
- 视频中心现在已经能进入旧版 `act_video_monitor` 正式页：
  - `LegacyVideoMonitorActivity` 已接回旧键盘区和监控页壳
  - 页面进出时会联动默认 Camera 开关链路
  - DVR 串口键位和触摸协议后面继续补
- 业务模块页现在已经不是简单入口页，而是模块操作台：
  - 每块都能看状态
  - 每块都能直接做当前最常用的一组动作
- 六个业务模块已经挂进统一模块 hub：
  - `dispatch`
  - `station`
  - `signin`
  - `camera-dvr`
  - `upgrade`
  - `file`
- 串口和 socket 现在都已经支持按配置切 `stub / real`。
- 串口抽象现在已经支持收包监听，后面 GPS / 485 回包 / 外设状态都能直接挂监听。
- Socket 抽象现在也已经支持收包监听，后面平台回包和调度指令也能统一挂解析器。
- `debugReplay` 现在已经补了 GPS 默认串口 key，调试页绑定 GPS 监听不再靠手选口位。
- 应用启动时会自动给默认 `JT808 / AL808` 通道挂协议监听。
- `GPIO / Camera / RFID / SystemOps` 也已经纳入统一配置和共享 runtime。
- 调试页现在能直接做三种回放：
  - 屏显协议
  - 808 / AL808
  - 全量混合样例
- 首页和调试页现在都能直接跑业务模块样例：
  - 跑当前模块
  - 跑全部模块
  - 看模块状态
  - 看最近一次模块执行结果
- 业务模块页现在也能直接做这些事：
  - 单独执行调度 / 报站 / 签到 / 摄像头-DVR / 升级 / 文件模块样例
  - 调度全协议回放
  - 绑定 GPS 监听
  - 读取一次卡号
  - 打开 / 关闭默认 Camera
  - 同步系统时间
  - 导出调试包
  - 重置运行配置
  - 执行全部模块样例
  - 查看模块详细状态
- `socket.mode=real` 时已经能记连接、发送和接收日志。
- 调试页已经支持：
  - 手工输入 HEX
  - 打开 / 关闭选中串口
  - 连接 / 断开选中 Socket
  - 绑定 / 解绑 GPS 监听
  - 选串口 / 选 Socket 后手工发送
  - 解析一段 808 / AL808 HEX
  - 解析一段 GPS NMEA
  - 直接看当前通道状态
  - 读 / 写选中 GPIO
  - 打开 / 关闭选中 Camera
  - 读一次 RFID
  - 请求同步系统时间
  - 请求重启
  - 看到底座状态摘要
  - 看到业务模块状态摘要
  - 导出调试包
  - 导出 GPS 监听状态
  - 导出 Socket 协议监听状态
  - 导出底座状态、业务模块状态和增强版终端自检
  - 看到当前配置来源和运行期文件位置
  - 一键把运行期配置重置到打包值
  - 看到 Socket 监听绑在哪条调度通道、最近一帧解析到了什么
- `device-m90` 已补 `stub / real / managed` 三层底座入口：
  - 串口
  - Socket
  - GPIO
  - Camera
  - RFID
  - SystemOps
- 真机联调还没系统展开，当前 `real` 入口主要先把权限、路径、命令和连接链路立住。
- 业务主链已经先按模块挂出入口，后面改动直接落在对应模块里，不再散进页面。
- 页面层现在开始只负责触发模块动作：
  - `dispatch.replay_all`
  - `dispatch.join_operation / leave_operation`
  - `station.bind_gps`
  - `station.advance_station`
  - `signin.read_card`
  - `camera-dvr.open_default_camera / close_default_camera`
  - `upgrade.sync_system_time`
  - `file.export_bundle / reset_runtime_config`
- 模块内部开始保存自己的最近业务状态，不再只靠页面临时拼提示文案。
- `dispatch / station / signin` 已开始有独立状态对象：
  - 调度计划、运营加入状态
  - 线路、本站/下站、GPS 口和定位快照
  - 司机、卡号、签到/签退状态
- 首页的调度 / 报站 / 签到卡片已经开始直接读取这些状态对象，
  后面接真实收包和状态机时不需要再改首页结构。
- 首页六个业务按钮现在不再直接跑样例，而是进入对应正式业务页：
  - 调度中心
  - 报站中心
  - 签到中心
  - 视频中心
  - 系统中心
  - 文件中心
- 正式业务页也不再只是“执行模块”：
  - 调度页补了确认调度、执行发车
  - 报站页补了重复报站、停止报站
  - 签到页补了手动签退
- 当前这版已经重新执行一次 `:app:assembleDebug`，APK 构建通过。
- 首页当前结构已经往旧终端主界面靠：
  - 上面先看运营状态
  - 中间直接做业务动作
  - 调试入口单独放到 debug 面板
  - 业务模块页和调试页后面只在 debug 环境保留快捷入口
