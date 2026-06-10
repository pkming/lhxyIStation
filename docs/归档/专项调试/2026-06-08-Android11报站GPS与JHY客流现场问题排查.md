# 2026-06-08 Android11 报站 GPS 与 JHY 客流现场问题排查

## 背景

现场日志：

- `logs/android11-test-log-20260608-124326463.txt`

测试反馈：

1. 报站站点进程没有变化。
2. 客流接口 RS485-2 没有数据，测试怀疑可能跟报站进程有关。
3. 首页 DVR/视频区域和左侧按键操作卡顿。

当前配置要点：

- GPS 串口：`gps -> ttyS5 @115200`
- 屏显串口：`rs485_1 -> ttyS7 @38400`
- 客流串口：`rs485_2 -> ttyS9 @9600`
- RS485-2 协议：`JHY`
- 报站 GPS 口：`gps`
- 报站默认屏显口：`rs485_1`

## 快速结论

这次不要把两个现象直接合并成一个问题。

1. RS485-2 客流没数据，日志证据指向 JHY 串口打开失败，主要问题是 JNI 符号不匹配或 JHY native 串口链路不可用。
2. 报站站点进程不变化，日志里没有有效 GPS 输入和自动报站事件。更像是 GPS 没收到有效定位、线路资源未匹配、或现场没有触发自动进出站条件。
3. 两者在业务链路上有关联：手动/自动切站后会触发 JHY 当前客流查询。但 JHY 失败不能直接解释 GPS 没有原始数据。
4. 如果怀疑 JHY native 卡住底层串口环境，使用“一键现场 A/B 自检并上传”：主进程先跑 shared 基线，独立 `:jhy_probe` 进程再探测 JHY native。
5. 首页卡顿是第三条线，日志证据指向 Camera2 预览打开/关闭占用主线程，不是 JHY 死循环。

## 日志证据

### JHY / RS485-2

关键日志：

```text
JHY config ready port=ttyS9 baud=9600 transport=native
JHY open requested port=ttyS9 baud=9600 transport=native
native open request /dev/ttyS9 @9600
No implementation found for int ... M90NativeJhySerialPort.open(java.lang.String, int)
native open failed on /dev/ttyS9: JNI unavailable, disable native JHY and fallback to SerialPortAdapter
JHY query reopening closed port ttyS9
JHY query dropped, port still not open: ttyS9
```

缺失日志：

```text
JHY_CURRENT_COUNT -> 63 00 01 28 D7 0D
JHY_CURRENT_COUNT <- ...
```

解释：

- JHY 客流模块启动后会主动查询一次当前客流。
- 这份日志里查询任务启动了，但端口没有打开成功。
- 因为端口未打开，查询被丢弃，未发出 JHY 查询帧。
- 所以 RS485-2 没数据不是“只因为站点没变没有触发”，而是 JHY 自身启动查询已经失败。

对应代码：

- `modules/domain/src/main/java/com/lhxy/istationdevice/android11/domain/passenger/JhyPassengerCounterMonitor.java`
- `updateConfig()`：配置生效后打开 RS485-2 并发起启动查询。
- `performScheduledRequest()`：端口未打开时重试，超过次数后 `JHY query dropped`。
- `sendCurrentCount()`：只有端口打开后才会发 `JHY_CURRENT_COUNT ->`。

### GPS / 自动报站

存在日志：

```text
已绑定 GPS 串口监听: gps/ttyS5
stty applied /dev/ttyS5 @115200
已启动 GPS 自动报站轮询
DVR_GPS_REPORT via rs232_1 -> ...
GPS (gps) -> attached=是 | channel=gps / ttyS5 | fixValid=否
```

缺失日志：

```text
gps raw sample ...
gps-fix
GPS auto report -> ...
station-snapshot
fixValid=是
```

解释：

- GPS 模块绑定了 `ttyS5`，并尝试配置 `115200`。
- 自动报站轮询已启动。
- DVR GPS 周期上报有发包，但这只是把当前 GPS 状态发给 DVR，不代表 GPS 输入有效。
- 没有 `gps raw sample`，说明本日志里没有看到 GPS 串口收到原始 NMEA 数据。
- 状态里 `fixValid=否`，说明没有有效定位快照。

对应代码：

- `modules/domain/src/main/java/com/lhxy/istationdevice/android11/domain/gps/GpsSerialMonitor.java`
- `buildListener()`：收到 GPS 串口数据后会记录 `gps raw sample`，解析出快照后会记录 `gps-fix`。
- `modules/domain/src/main/java/com/lhxy/istationdevice/android11/domain/module/StationBusinessModule.java`
- `evaluateAutoGpsReport()`：没有 GPS snapshot、没有线路、没有自动报站事件时会直接返回。

### SELinux 提示

日志里有：

```text
avc: denied ... /dev/ttyS5 ... permissive=1
```

解释：

- `permissive=1` 通常表示策略命中但不强制拦截。
- 不能仅凭这条断定 GPS 打不开。
- 仍需结合是否出现 `real open /dev/ttyS5`、`gps raw sample`、实际设备口号和波特率判断。

### 首页 Camera/DVR 卡顿

关键日志：

```text
首页监控模式=DVR / cameraMode=REAL / gpioMode=REAL / primary=io1 / secondary=io2 / defaultCamera=av_out
首页监控预览已打开 mode=DVR / camera=av_out / size=638x700
camera preview started: av_out -> 100 / surface=638x700
camera closed: av_out
Skipped 349 frames! The application may be doing too much work on its main thread.
Skipped 114 frames! The application may be doing too much work on its main thread.
Skipped 60 frames! The application may be doing too much work on its main thread.
Skipped 97 frames! The application may be doing too much work on its main thread.
Long monitor contention with owner main ... CameraDeviceImpl.close() ... for 763ms
```

解释：

- 卡顿发生在 JHY 查询失败之后几秒，时间上不吻合“JHY native 死循环卡住主线程”。
- 卡顿时间段与首页 Camera/DVR 预览 open、preview started、close 高度重合。
- `CameraDeviceImpl.close()` 在主线程附近出现 763ms 锁竞争，是实打实的 UI 卡顿证据。
- 这条线的直接根因是 Camera2 生命周期操作和首页 UI 线程耦合过紧。

对应代码：

- `modules/device-m90/src/main/java/com/lhxy/istationdevice/android11/devicem90/M90RealCameraAdapter.java`
- 原问题点：`openCamera(..., context.getMainExecutor(), ...)`、`createCaptureSession(..., mainHandler)`、同步 `cameraDevice.close()`。
- `app/src/main/java/com/lhxy/istationdevice/android11/app/home/LegacyMainActivity.java`
- 原问题点：首页 1 秒刷新、Surface 事件和监控模式切换可能重复投递 open/close；DVR 触摸和业务动作同步跑在 UI 事件链路里。

## 业务链路关系

### 报站触发客流

手动推进站点：

- `StationBusinessModule.advanceStation()`
- 站点状态推进后调用 `requestPassengerCounterForArrival(...)`
- 最终调用 `passengerCounterMonitor.requestCurrentCountAfterStationDisplay(...)`

自动报站进出站：

- `StationBusinessModule.evaluateAutoGpsReport()`
- 自动报站引擎产出站点事件后调用 `stationState.recordAutoStation(...)`
- 更新屏显后调用 `requestPassengerCounterForAutoStation(...)`

所以测试说“RS485-2 没数据可能跟报站进程有关”有一部分合理性：报站切站会触发客流查询。

但这次日志里 JHY 启动查询本身已经失败，所以不能只按报站问题查 RS485-2。

## JNI 问题说明

这份日志出现两个 JNI 符号不匹配：

```text
No implementation found for void ... M90I2CPort.RfidInit()
No implementation found for int ... M90NativeJhySerialPort.open(java.lang.String, int)
```

现场反编译和 vendor `.so` 导出符号显示，原厂库绑定的是原始包名：

```text
com.lianhexinye.iicport.I2CPort
com.lianhexinye.jhyserialport.JHYSerialPort
```

如果 Java 类迁移到新包名：

```text
com.lhxy.istationdevice.android11.devicem90.M90I2CPort
com.lhxy.istationdevice.android11.devicem90.M90NativeJhySerialPort
```

JNI 就会找不到实现。

已做过的桥接方向：

- 保留原厂包名的 Java native 入口类。
- 新代码用 wrapper 调原厂包名类。
- 避免直接把 native 方法声明放在新包名下。

相关文件：

- `modules/device-m90/src/main/java/com/lianhexinye/iicport/I2CPort.java`
- `modules/device-m90/src/main/java/com/lianhexinye/jhyserialport/JHYSerialPort.java`
- `modules/device-m90/src/main/java/com/lhxy/istationdevice/android11/devicem90/M90I2CPort.java`
- `modules/device-m90/src/main/java/com/lhxy/istationdevice/android11/devicem90/M90NativeJhySerialPort.java`

## JHY native 是否可能卡住 GPS

当前日志不像是 JHY native 在主线程死循环：

- JHY open 在 `M90ManagedJhySerialPortAdapter` 的单线程 executor 中执行。
- GPS 普通串口也有自己的 session executor 和读线程。
- 日志里 JHY native 很快抛出 `UnsatisfiedLinkError`，不是长时间卡在 native open。

但仍不能完全排除：

- 原厂 JHY native 库加载后影响底层串口驱动。
- JHY native 读循环占用系统资源。
- 原厂串口库内部有全局状态，影响其他 `/dev/ttySx`。

因此保留一个验证策略：一个包内完成 shared 与 native 的 A/B 探测，native 部分放到独立进程，避免污染主进程。

## 一键 A/B 验证策略

### 目标

确认 GPS 不出数据是否与 JHY native 运行链路有关。

### 做法

主进程运行时构造 `JhyPassengerCounterMonitor` 时仍不传 `M90ManagedJhySerialPortAdapter`：

```java
new JhyPassengerCounterMonitor(serialPortAdapter, null)
```

这样主业务链路的 JHY 会走共享串口适配器：

```text
transport=shared
```

主进程不初始化 JHY native adapter，也不会触发 `System.loadLibrary("jhy_serial_port")`。

点击调试页按钮后，流程会分两段：

1. `AB_PHASE_SHARED_START`：主进程跑 shared 基线，绑定 GPS、跑业务模块、主动请求 JHY 当前客流，等待 10 秒收回包。
2. `AB_PHASE_NATIVE_START`：启动 `DebugJhyNativeProbeService`，该 Service 运行在独立 `:jhy_probe` 进程，只负责加载 JHY native、打开 `rs485_2/ttyS9`、发送当前客流查询帧、等待回包并写结果文件。
3. 主进程读取 native 探测结果，写入 `AB_DIAGNOSTIC_SUMMARY`，然后导出并上传调试包。

验证包位置：

```text
app/build/outputs/apk/debug/app-debug.apk
```

### 判断标准

安装验证包后，点击：

```text
一键现场 A/B 自检并上传
```

如果 shared 阶段出现：

```text
gps raw sample ...
gps-fix
fixValid=是
```

说明主进程 shared 链路下 GPS 能恢复，JHY native 链路影响 GPS/串口环境的可能性升高。

如果 shared 阶段仍然没有：

```text
gps raw sample
```

说明 GPS 问题不应继续优先归因给 JHY native，转查 GPS 口号、波特率、硬件输入、天线和线路匹配。

如果 native 阶段出现：

```text
AB_PHASE_NATIVE_TIMEOUT
AB_PHASE_NATIVE_OPEN_TIMEOUT
nativeSupported=false
open=false
sent=false
received=false
```

说明原厂 JHY native 链路自身不可用或风险较高。

如果 shared 阶段 `JHY_CURRENT_COUNT ->/<-` 正常，而 native 阶段失败，基本可以优先废弃 JHY native，保留 shared 串口链路。

## 复测 Checklist

### 0. 一键现场 A/B 自检

调试页已增加按钮：

```text
一键现场 A/B 自检并上传
```

测试安装验证包后，进入调试工具页，点击该按钮即可。按钮流程会自动执行：

1. 重新加载当前运行配置并同步到共享运行时。
2. 绑定默认 GPS 串口监听。
3. 执行终端自检。
4. 批量执行全部业务模块样例。
5. 主进程 shared 链路主动请求一次 JHY 当前客流。
6. 等待 10 秒收集 GPS/JHY/Socket/串口回包日志。
7. 独立 `:jhy_probe` 进程探测 JHY native open/send/recv。
8. 汇总 shared 与 native 结果。
9. 导出调试包，并在 OSS 配置可用时自动上传。

页面提示 `现场 A/B 自检完成，调试包已生成/上传` 后，本轮测试结束。页面会显示本地调试包路径和 OSS 上传状态。

一键诊断日志关键字：

```text
FIELD_DIAGNOSTIC_START
FIELD_DIAGNOSTIC_CONFIG_READY
FIELD_DIAGNOSTIC_GPS_BOUND
FIELD_DIAGNOSTIC_SELF_CHECK
FIELD_DIAGNOSTIC_JHY_REQUESTED
FIELD_DIAGNOSTIC_WAIT_RX
FIELD_DIAGNOSTIC_SNAPSHOT
AB_DIAGNOSTIC_START
AB_PHASE_SHARED_START
AB_PHASE_SHARED_DONE
AB_PHASE_NATIVE_START_REQUEST
AB_PHASE_NATIVE_START
AB_PHASE_NATIVE_TX
AB_PHASE_NATIVE_RX
AB_PHASE_NATIVE_RESULT
AB_PHASE_NATIVE_DONE
AB_DIAGNOSTIC_SUMMARY
FIELD_DIAGNOSTIC_EXPORT_DONE
FIELD_DIAGNOSTIC_FAILED
```

开发拿到日志后优先搜索：

```bash
rg -n "FIELD_DIAGNOSTIC|AB_PHASE|AB_DIAGNOSTIC|gps raw sample|gps-fix|fixValid|JHY_CURRENT_COUNT|transport=|Skipped .*frames|CameraDeviceImpl.close" logs/xxx.txt
```

注意：主业务进程仍是禁用 JHY native、JHY 走 shared 串口链路的验证模式。native 只在独立 `:jhy_probe` 进程里短时探测，用来做对比证据。

### 1. 基础配置

- 确认 GPS 配置：`gps -> ttyS5 @115200`
- 确认 RS485-2 配置：`rs485_2 -> ttyS9 @9600`
- 确认 RS485-2Protocol：`JHY`
- 确认报站资源已导入，当前线路和方向正确。
- 确认现场 GPS 天线可用，设备处于可搜星环境。

### 2. GPS 日志

优先搜索：

```text
GpsSerialMonitor
gps raw sample
gps-fix
fixValid
GPS auto report
station-snapshot
```

判断：

- 有 `gps raw sample`：串口有原始数据。
- 有 `gps-fix` 且 `fixValid=是`：GPS 定位有效。
- 有 `GPS auto report ->`：自动报站引擎已产生动作。
- 没有 `gps raw sample`：先查 ttyS5、115200、接线、GPS 模块输出。
- 有 raw 但 `fixValid=否`：查 NMEA 语句是否有效、搜星、时间、定位状态位。
- `fixValid=是` 但站点不变：查线路资源、站点经纬度、方向、进出站半径阈值。

### 3. JHY 日志

优先搜索：

```text
JhyPassengerCounter
M90JhySerial
JHY_CURRENT_COUNT
ttyS9
transport=
No implementation found
```

判断：

- `transport=native`：走原厂 JHY native。
- `transport=shared`：走普通串口适配器。
- 有 `No implementation found`：JNI 符号仍不匹配。
- 有 `JHY_CURRENT_COUNT ->`：查询帧已发出。
- 有 `JHY_CURRENT_COUNT <-`：收到并解析到客流返回。
- 有 `->` 无 `<-`：查 RS485-2 接线、A/B、设备地址、波特率、协议。
- 无 `->` 且 `port still not open`：查串口打开失败、权限、节点、native 或 shared 链路。

### 4. 报站链路

优先搜索：

```text
StationBusinessModule
报站动作入口
GPS auto report ->
station-snapshot
display-current
JHY query scheduled on manual arrival
JHY query scheduled on auto-station
```

判断：

- 手动推进站点应出现报站动作入口、屏显发送、站点快照、JHY 查询调度。
- 自动报站应先有 GPS 有效定位，再有自动报站事件。
- 没有 GPS snapshot 时，自动报站不会推进站点。

## 后续建议

### 短期

1. 用“一键现场 A/B 自检并上传”做现场验证，不再人工打两个包反复测。
2. 新日志导出后先判断 shared 阶段有没有 `gps raw sample`。
3. 再看 native 阶段是否 `open/send/recv` 正常，是否出现 timeout。
4. 如果 shared 恢复而 native 异常，优先长期保留 shared 串口链路，或向原厂索取匹配 Android11/M90 的 JHY SDK。
5. 如果 shared 和 native 都没恢复，继续按 GPS 口号/波特率/硬件输入、RS485-2 接线和客流设备协议查。
6. 首页卡顿复测时搜索 `Skipped .* frames`、`Long monitor contention`、`CameraDeviceImpl.close`，观察是否明显减少。

### 已做卡顿修复方向

1. `M90RealCameraAdapter` 增加 `HandlerThread("m90-camera")`。
2. Camera2 `openCamera` 回调改到 camera 专用线程，不再使用 `context.getMainExecutor()`。
3. `createCaptureSession`、`setRepeatingRequest`、`closeSession`、`cameraDevice.close()` 统一落到 camera 专用线程串行执行。
4. 首页监控预览增加 `opening/opened` 状态和 600ms 防抖，减少重复 open/close。
5. 首页 DVR 触摸、报站按键、司机动作改为投递到 `legacy-home-action` 后台线程，结果再回主线程刷新 UI。

修复后期望：

- 首页触摸和左侧按键不再被 Camera2 close 阻塞。
- 新日志中 `Skipped xxx frames` 数量和次数明显下降。
- 若仍有 `Long monitor contention ... CameraDeviceImpl.close`，也应发生在 `m90-camera` 线程，不应阻塞 `main`。

### 中期

给自动报站静默返回点补诊断日志：

- `snapshot == null`
- `flowResult.hasRoute() == false`
- `event.isNone()`
- GPS 有效但未触发站点事件

这样下次日志能直接说明自动报站卡在哪一步，不用靠缺失日志反推。

### 长期

1. 对原厂 JNI 类保持原始包名，不要迁移 native 方法声明的包名。
2. 对所有串口 native 调用保留 shared 串口 fallback。
3. JHY native 探测长期保留独立进程隔离，避免主进程被 native 库加载和读循环污染。
4. 每次现场包保留构建说明，标记是否启用 JHY native。

## 常用搜索命令

```bash
rg -n "No implementation found|native open failed|JHY|ttyS9|JHY_CURRENT_COUNT" logs/android11-test-log-20260608-124326463.txt
rg -n "FIELD_DIAGNOSTIC|AB_PHASE|AB_DIAGNOSTIC|native probe|jhy_probe" logs/android11-test-log-20260608-124326463.txt
rg -n "GpsSerialMonitor|gps raw sample|gps-fix|fixValid|GPS auto report|station-snapshot" logs/android11-test-log-20260608-124326463.txt
rg -n "StationBusinessModule|报站动作入口|display-current|JHY query scheduled" logs/android11-test-log-20260608-124326463.txt
rg -n "Skipped .*frames|Long monitor contention|CameraDeviceImpl.close|camera preview started|首页监控预览" logs/android11-test-log-20260608-124326463.txt
```

## 当前文件改动提醒

当前验证方向里，主运行时仍然禁用 JHY native：

```java
new JhyPassengerCounterMonitor(serialPortAdapter, null)
```

JHY native 只通过调试页的 `DebugJhyNativeProbeService` 在独立 `:jhy_probe` 进程里探测。后续如果要把主业务恢复为原厂 JHY native，需要改回传入 `M90ManagedJhySerialPortAdapter`，但恢复前应确认 JNI 桥接包名、原厂 so、线程行为和 A/B 结果都稳定。
