# 配置目录

这里专门放新壳的运行配置，不要再把这些值散到代码里：

- 串口口位
- 波特率
- 调度 host / port
- socket mode
- GPIO 路径
- Camera 通道映射
- RFID 文件 / 命令桥接
- RFID I2C-3 桥接参数
- LocationManager 开关与 provider
- CAN can0/can1 桥接参数
- ttyS0 Keyboard 串口入口
- system ops 命令模板
- 调试页默认走哪条链路

## 用法

1. 默认模板：`terminal-config.template.json`
2. 本地覆盖：`terminal-config.local.json`
3. 运行期覆盖：`terminal-config.runtime.json`

构建时会优先打包 `terminal-config.local.json`。  
如果本地覆盖文件不存在，就自动回退到模板文件。

应用第一次启动时，会在应用目录自动落一份运行期配置文件：

- `Android/data/<包名>/files/config/terminal-config.runtime.json`

后面直接替换这份文件，再到调试页点“刷新”，就能重新加载。

## 约定

- 模板文件放仓库里，作为当前标准配置。
- 本地覆盖文件只放本机，不进仓库。
- 运行期覆盖文件由应用启动时自动生成，后面联调优先改它。
- socket 需要走真连接时，把对应配置的 `mode` 改成 `real`。
- GPIO / Camera / RFID / SystemOps 也都按同样规则切 `mode=real`。GPIO 默认路径已按 M90 旧实现落到 `/proc/rp_gpio/gpio...`，真机测试时重点确认节点是否存在和读写权限。
- LocationManager / CAN / Keyboard 也进入配置模型；它们默认是 `stub`，真机参数确认后再切 `real`。
- 需要改口位、host、port，先改这里，再改文档，不要直接改页面代码。

## 扩展硬件接入口

本轮已把 AL-M90 开发指南中还没落到配置层的能力补进模板和自检：

- `serial.keyboard` -> `ttyS0`，承接按键串口协议入口。
- `serial.debug` -> `ttyS2`，承接 debug 口位说明。
- `gpio.io3/io4/io5`、`gpio.shouting_inner/shouting_outer`、`gpio.headphone_detect_power/headphone_detect`，承接 IO3/IO4/IO5、喊话器和耳机检测 GPIO；路径按 M90 的 `/proc/rp_gpio/gpio1d2/gpio1d3/gpio1d4/gpio3a3/gpio3a4/gpio3a6/gpio1b0` 预置。
- `rfid.i2cDevicePath` / `rfid.i2cAddress`，承接 I2C-3 RFID 参数。真实读卡当前仍建议先配 `readCommand` 或文件桥接，厂商 SDK 到位后再替 `M90RealRfidAdapter`。
- `location`，承接 Android `LocationManager` 标准定位入口；当前 GPS 业务主链仍走 `serial.gps=ttyS5`。
- `can.channels.can0/can1`，承接 CAN 通道参数；真实收发可后续按 JNI、SocketCAN 或命令桥接填充。

这些能力会出现在终端自检和 `device_expansion` 模块状态里。默认模板保持 `stub`，避免没有真机节点时启动报错。

## 首页视频模式配置

首页右侧视频区现在按下面顺序决定显示什么画面：

1. 如果 `debugReplay.monitorPrimaryGpioKey` 和 `debugReplay.monitorSecondaryGpioKey` 都有值，就优先按这两个 GPIO 读值组合切换首页模式。
2. 如果这两个 key 任意一个为空，就回退到 `debugReplay.cameraChannelKey`。

当前首页模式映射约定如下：

- 主 GPIO=1，副 GPIO=0：倒车
- 主 GPIO=0，副 GPIO=1：中门
- 主 GPIO=0，副 GPIO=0：倒车优先
- 其他组合：DVR

当前首页预览通道映射如下：

- 中门：`middle_door`
- 倒车：`reverse`
- 倒车优先：当前先落到 `reverse`
- DVR：`debugReplay.cameraChannelKey`

首页右侧司机/提示区当前还依赖下面这些配置和共享状态：

- `basicSetup.other.vehicleNumber`：首页车号 `tvCarNumber`
- `basicSetup.other.shoutingPrimaryGpioKey` + `basicSetup.other.shoutingSecondaryGpioKey`：首页喊话状态 GPIO 输入；默认按 M90 旧实现使用 `shouting_outer` + `shouting_inner`，也就是 GPIO3_A4 + GPIO3_A3。

首页喊话状态映射约定如下：

- 主 GPIO=1，副 GPIO=0：SPK_OUT...
- 主 GPIO=0，副 GPIO=1：SPK_IN...
- 主 GPIO=0，副 GPIO=0：SPK_IN_OUT...
- 主 GPIO=1，副 GPIO=1：空白

首页只有在 `gpio.mode=real` 时才读取喊话 GPIO；stub 模式会回退显示语音通话页连接态写入的 `IP PHONE...` 状态，避免默认值误显示喊话状态。

首页提示区 `tvInfoTips` 当前走共享页面状态：

1. 文件管理页导入/导出/升级开始时会写入加载提示。
2. 导入/导出失败、日志导出、APK 升级这些动作会写入固定 operation 文案，而不是回填任意执行摘要。
3. 当前还没有把旧 M90 的 FTP/APK 实时下载进度恢复到新壳后台服务。
4. 首页前台时已经会监听这份共享状态；后续如果补 FTP/APK 后台服务，直接写共享 operation/progress 即可，不需要再给首页单独加轮询。
5. 这份共享状态已经下沉到 `modules/core`，业务模块可以直接写；不要再回到页面层代发首页提示。

当前已接通的 operation/progress 口径：

- `4` -> `APK is upgrading...`
- `12` -> `Download M90 APK,process：xx%`
- `13` -> `Download SourceFile,process：xx%`
- `14` -> `UPLOAD LOG procerr：xx%`

当前升级模块已经直接接通：

1. `upgrade` 模块样例会回放升级协议，并同时启动 `12 -> 4` 的 APK 下载/升级提示链。
2. 文件管理页“升级”动作会调用 `upgrade/install_local_apk`，由模块自己写首页提示链，不再由页面层手动写 `APK is upgrading...`。

系统安装命令当前继续复用 `systemOps.silentInstallCommand`，并使用 `%value%` 占位符承接 APK 绝对路径。

示例：

- `pm install -r %value%`
- `am startservice ... --es apkPath %value%`

如果当前还没确认 M90 真机对应 pin：

1. 先保持 `monitorPrimaryGpioKey` / `monitorSecondaryGpioKey` 为空。
2. 先用 `cameraChannelKey` 控制首页默认视频模式。
3. pin 对上后，再只改配置，不改页面代码。
