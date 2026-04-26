# DVR 调试说明

这份文档只解决一个问题：

- 现在 DVR 已经接到项目里了，现场怎么判断串口数据流是不是正常。

---

## 1. 先看当前链路

当前 DVR 相关链路分 4 层：

1. 配置层
   - `terminal-config.runtime.json`
2. 串口层
   - `RS232-1 -> ttyS3 -> 9600 -> real`
3. 业务发送层
   - `DvrSerialDispatchUseCase`
4. 业务接收层
   - `DvrSerialMonitor`

页面入口：

1. 旧首页：`LegacyMainActivity`
   - 有 `DVR` 按钮
   - 首页 `DVR` 状态看的是串口握手在线状态
2. 新首页：`MainActivity`
   - 没有单独的 `DVR` 按钮
   - 从 `视频 / DVR` 进入
3. 实际视频页：`LegacyVideoMonitorActivity`
   - 打开页面会开默认摄像头
   - 点击按键会发 `DVR_KEY_EVENT`
   - 点击预览区会发 `DVR_TOUCH_EVENT`

---

## 2. 调试前先核对配置

运行期配置文件：

`/storage/emulated/0/Android/data/com.lhxy.istationdevice.android11/files/config/terminal-config.runtime.json`

至少确认这几项：

```json
{
  "serial": {
    "rs232_1": {
      "portName": "ttyS3",
      "baudRate": 9600,
      "mode": "real"
    }
  },
  "basicSetup": {
    "serialSettings": {
      "rs2321Protocol": "DVR"
    },
    "protocolLinkageSettings": {
      "dispatchOwner": "serial_rs232_1"
    }
  }
}
```

重点是 2 个开关：

1. `rs2321Protocol = DVR`
2. `dispatchOwner = serial_rs232_1`

少任意一个，很多业务帧都不会真正走 DVR 串口。

---

## 3. 现场先按这个顺序测

不要一上来混着测，按 4 步走：

1. 先看握手是否正常
2. 再看页面按键 / 触摸是否能发帧
3. 再看 GPS / 报站 / 调度 / 签到这些业务帧是否能发出去
4. 最后看 DVR 反向回包是否能被解析

---

## 4. 先看握手

关键代码：

- `modules/domain/.../dvr/DvrSerialMonitor.java`

当前逻辑：

1. 绑定 `RS232-1`
2. 周期发送握手帧：`55 01 00 00 56 AA`
3. 收到 `0x02` 空载荷应答后标记 `DVR 在线`
4. 超时则标记离线

### 正常日志应该看到

```text
已绑定 DVR 串口监听: rs232_1/ttyS3
DVR 在线
```

如果一直没有 `DVR 在线`：

1. 先查口位是不是 `ttyS3`
2. 再查波特率是不是 `9600`
3. 再查接线和 DVR 端协议是否真的是这套握手
4. 再看串口有没有任何 RX 数据

---

## 5. 怎么看日志

先确认设备在线：

```powershell
adb devices
```

推荐直接开这组日志：

```powershell
adb logcat | findstr "DvrSerialMonitor DvrSerialDispatch M90RealSerial LegacyVideoMonitorActivity CameraDvrBusinessModule DispatchBusinessModule StationBusinessModule SignInBusinessModule"
```

如果只想看握手和收发：

```powershell
adb logcat | findstr "DvrSerialMonitor DvrSerialDispatch M90RealSerial"
```

如果只想看视频页按键 / 触摸：

```powershell
adb logcat | findstr "LegacyVideoMonitorActivity DvrSerialDispatch"
```

---

## 6. 怎么测发送链路

### 6.1 键盘帧

进入：

- 旧首页 `DVR`
- 或新首页 `视频 / DVR`

然后点击页面里的：

- 数字键 `0-9`
- `UP / DOWN / LEFT / RIGHT`
- `M / ENT / ESC / DEL`

期望日志：

```text
LegacyVideoMonitorActivity ... 已发送 DVR 键码
DvrSerialDispatch ... DVR_KEY_EVENT via rs232_1 -> ...
```

### 6.2 触摸帧

在视频预览区域点击、拖动、抬起。

期望日志：

```text
LegacyVideoMonitorActivity ... touch phase=down / raw=(x,y) / scaled=(x,y)
DvrSerialDispatch ... DVR_TOUCH_EVENT via rs232_1 -> ...
```

说明：

1. 页面把触摸坐标换算成 `1280 x 800`
2. `down/move` 发按下态
3. `up` 发抬起态

### 6.3 GPS / 站点帧

触发方式：

1. 绑定 GPS
2. 推进站点
3. 重复报站

期望日志：

```text
DvrSerialDispatch ... DVR_GPS_REPORT ...
DvrSerialDispatch ... DVR_SITE_INFO ...
```

### 6.4 调度帧

触发方式：

1. 确认调度
2. 发车
3. 确认公告

期望日志：

```text
DvrSerialDispatch ... DVR_DISPATCH_REPLY ...
DvrSerialDispatch ... DVR_START_BUS ...
DvrSerialDispatch ... DVR_LOWER_REPLY ...
```

### 6.5 司机签到帧

触发方式：

1. 读卡签到
2. 手动签退

期望日志：

```text
DvrSerialDispatch ... DVR_DRIVER_ATTENDANCE ...
```

---

## 7. 怎么看接收链路

关键代码：

- `modules/domain/.../dvr/DvrSerialMonitor.java`

当前已经会解析：

1. 握手应答 `0x02`
2. 司机考勤应答 `0x0E`
3. 调度下发 `0x0F`
4. 公告下发 `0x12`

期望日志类似：

```text
DVR 在线
DVR 司机考勤应答 ...
DVR 调度下发 ...
DVR 公告下发 ...
```

如果只有发送、没有任何业务回包：

1. 先别怀疑 Android 端
2. 先确认 DVR 设备端是否真的会主动回这些帧
3. 再确认接线是否双向
4. 再抓 `M90RealSerial` 原始收包看是否有字节回来

---

## 8. 什么叫“数据流正常”

最小通过标准可以定成这 6 条：

1. `ttyS3 @ 9600` 能正常打开
2. `DvrSerialMonitor` 显示已绑定
3. 能看到握手，且出现 `DVR 在线`
4. 在视频页按键时能看到 `DVR_KEY_EVENT`
5. 在预览区触摸时能看到 `DVR_TOUCH_EVENT`
6. 做业务动作时能看到对应的业务帧发出

如果再多一条：

7. DVR 端能回 `0x0E / 0x0F / 0x12` 这些业务帧

那整条 DVR 串口链路就基本闭环了。

---

## 9. 现场常见判断

### 9.1 有摄像头画面，但没有 DVR 在线

说明：

- 摄像头链路和 DVR 串口链路不是一回事

优先查：

1. `rs2321Protocol`
2. `dispatchOwner`
3. `ttyS3`
4. `9600`
5. DVR 接线

### 9.2 有 `DVR_KEY_EVENT`，但 DVR 无反应

优先查：

1. DVR 是否要求先握手在线
2. 键码是否和旧项目一致
3. 串口单向还是双向
4. DVR 端是否有更严格的状态机

### 9.3 有 `DVR_TOUCH_EVENT`，但触摸位置不对

优先查：

1. 坐标系是否就是 `1280x800`
2. 是否要交换 X/Y
3. 是否要镜像
4. 是否要求别的字节序

### 9.4 有业务发送，没有业务回包

优先查：

1. DVR 端是否真的支持回包
2. 业务是否需要设备端先进入某个页面
3. 线是否只接了 TX 没接 RX

---

## 10. 当前代码里可直接对照的文件

1. `modules/domain/src/main/java/com/lhxy/istationdevice/android11/domain/dvr/DvrSerialMonitor.java`
2. `modules/domain/src/main/java/com/lhxy/istationdevice/android11/domain/dispatch/DvrSerialDispatchUseCase.java`
3. `modules/domain/src/main/java/com/lhxy/istationdevice/android11/domain/module/CameraDvrBusinessModule.java`
4. `modules/domain/src/main/java/com/lhxy/istationdevice/android11/domain/module/StationBusinessModule.java`
5. `modules/domain/src/main/java/com/lhxy/istationdevice/android11/domain/module/DispatchBusinessModule.java`
6. `modules/domain/src/main/java/com/lhxy/istationdevice/android11/domain/module/SignInBusinessModule.java`
7. `app/src/main/java/com/lhxy/istationdevice/android11/app/LegacyVideoMonitorActivity.java`

---

## 11. 建议的现场调试动作

建议你就按这一套跑：

1. 先把配置改成 `RS232-1 = DVR / dispatchOwner = serial_rs232_1`
2. 开 `adb logcat`
3. 进入 `视频 / DVR`
4. 看是否出现 `DVR 在线`
5. 按一个方向键，看是否出现 `DVR_KEY_EVENT`
6. 点一下预览区，看是否出现 `DVR_TOUCH_EVENT`
7. 再做一次 GPS / 报站 / 发车 / 签到动作
8. 对照日志确认每个动作都发了正确类型的帧

这样我们就能很快判断问题到底卡在：

1. 配置
2. 串口
3. 页面动作
4. 业务发送
5. 设备回包

