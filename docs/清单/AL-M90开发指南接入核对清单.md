# AL-M90 开发指南接入核对清单

核对来源：`docs/文档/_converted/AL-M90_RK3568_android11_开发指南-20240624.txt`

补充协议来源：

- `docs/文档/公交平台-第三方平台对接协议文档.docx`
- `docs/文档/JTT 808-2019.PDF.pdf`
- `docs/文档/LHXY-TD-LED2协议-20230613.docx`

核对时间：2026-05-13

结论先行：这里还没有“全收口”。这份开发指南是 AL-M90 的硬件与系统接口能力清单；当前项目只对 M90 业务主链里已经用到的部分能力形成软件闭合。M90 旧实现已确认 GPIO 走 `/proc/rp_gpio/gpio...` 节点，本项目已把 IO1~IO5、内音/外音/内喇叭、喊话内外、耳机检测控制/输入的 `valuePath` 按旧实现落入配置和 fallback；但 `LocationManager`、I2C-3 RFID、CAN、ttyS0 按键、IO3/IO4/IO5 通用业务、耳机检测业务仍未闭合。

本表按三个层级核对：

1. 配置接入口：是否已经进入 `ShellConfig`、模板、自检、模块状态。
2. 业务链闭合：是否有页面/模块真实消费，并能走完软件流程。
3. 真机闭合：是否已确认节点、权限、电平、协议帧或 SDK，并能在 M90 实机上验证。

只有三层都通过，才算全收口。

## 逐项核对

| 开发指南能力 | 配置接入口 | 业务链闭合 | 真机闭合 | 严格结论 |
| --- | --- | --- | --- | --- |
| 通讯 / 4G / 网口 / Wi-Fi | Socket 配置、Manifest 网络权限已接 | JT808/AL808 调度链已消费 Socket；`JTT 808-2019.PDF` 的基础帧格式、转义、XOR 校验、部分终端上行消息已有实现；未管理 4G/Wi-Fi/网口硬件；`公交平台-第三方平台对接协议文档` 的 WebService/JSON 基础数据接口未接入 | 待真机网络配置、host/port、链路稳定性验证；JT/T 808-2019 版本标识、10 字节终端手机号、分包、加密、完整平台下行命令未闭合；第三方平台 WebService 需另建 HTTP/SOAP 客户端和登录态 | 部分收口：终端调度 Socket 和部分 JT808/M90 扩展报文可用，JT/T 808-2019 全量协议、通信模块硬件管理和第三方平台 WebService 未收口 |
| BD/GPS - LocationManager | 已补 `LocationConfig`、`ACCESS_FINE_LOCATION`、自检、`device_expansion` | 未并入 `GpsSerialMonitor`，未参与首页/报站/站点学习 | 未做运行时权限申请、provider 可用性和定位源仲裁真机验证 | 未收口：只是接入口 |
| BD/GPS - 串口 GPS | `serial.gps=ttyS5@115200` 已接 | `GpsSerialMonitor`、`GpsBusinessModule`、首页、站点学习、自动报站、校时已消费 | 需切 `real` 后验证 `/dev/ttyS5` 权限、NMEA 稳定性、`stty` | 软件主链基本闭合，真机待收口 |
| 串口映射 | ttyS0/ttyS2/ttyS3/ttyS4/ttyS5/ttyS7/ttyS9 已进模板 | GPS、RS232-1/DVR、RS485-1/2 屏显、RS485-2/JHY 客流已有消费；ttyS0/ttyS2/RS232-2 未形成独立业务 | 待逐口权限、波特率和外设协议验证，尤其 RS485-2 的 JHY 回包 | 部分收口：口位全配置，主用业务链已扩到 JHY 客流，仍需真机闭合 |
| RS232-1 / DVR | `rs232_1=ttyS3` 已接 | `DvrSerialDispatchUseCase`、`DvrSerialMonitor`、考勤、GPS 上报、首页 DVR 状态可消费 | 待 DVR 握手、回包、波特率、真实协议回归 | 部分收口：软件链已接，真机未闭合 |
| RS485 屏显/外设 | RS485-1/RS485-2 已进配置和设置页；两路都可分别选择屏显协议；屏显协议可选 `通达/TD/LHXY/恒舞/LED导程牌` | `LHXY-TD-LED2协议-20230613` 对应的 `BB 10 LEN CMD ... SUM 55` 通达/TD 帧已有部分实现：广播线路状态 `0x01`、线路 `0x02`、外设广告 `0x04`、车内屏 `0x03`、报站 `0x08`、侧牌站点 `0x11`；报站链路已按 RS485-1/RS485-2 两路协议配置分别发送，线路同步时也会补发 `0x01` 状态帧；调度中心外设机务页已能按当前配置向通达/TD 屏发送广告帧 | 默认配置 `rs485Protocol=无`、`rs4852Protocol=无`，需页面/配置切到 `通达` 或 `TD`；宣传/广告 `0x05`、中英文线路 `0x10` 仍未覆盖；真机仍需核 ttyS7/ttyS9、38400/9600 波特率和屏端回显 | 部分收口：LED2 通达主报站帧、`0x04` 广告帧和双路选择/发送已接，协议全量和真机口位未收口 |
| GPIO IO1~IO5 | IO1~IO5 已进模板、自检、`M90IoMap`，`valuePath` 已按 M90 `/proc/rp_gpio/gpio1d0~gpio1d4` 落配置 | IO1/IO2 已用于首页 DVR 自动切换；IO3/IO4/IO5 未发现 M90 业务消费证据，也未形成新壳业务消费 | 待真机确认 `/proc/rp_gpio/gpio1d0~gpio1d4` 是否存在、权限、电平和默认高低 | 部分收口：IO1/IO2 软件消费和路径已接，IO3~IO5 仅通用读写/自检 |
| 内音 / 外音 / 内喇叭 | `inner_audio`、`outer_audio`、`inner_speaker` 已接，路径按 M90 `gpio1b1/gpio1b2/gpio0d6` 落配置 | `LegacyStationAudioUseCase` 会按 MP3/TTS 播放阶段写 GPIO 并调音量 | 待真机确认 `/proc/rp_gpio/gpio1b1/gpio1b2/gpio0d6` 权限、功放电平和实际效果 | 软件链基本闭合，真机待收口 |
| 喊话器内/外、耳机检测 | `GPIO3_A3/GPIO3_A4/GPIO3_A6/GPIO1_B0` 已进模板、自检、`M90IoMap`，路径按 M90 `gpio3a3/gpio3a4/gpio3a6/gpio1b0` 落配置；`GPIO3_A6` 默认值已按硬件说明调整为高 | M90 旧版有 `gpio3a4/gpio3a3` 轮询喊话状态；新壳首页已在 `gpio.mode=real` 时按 `shouting_outer/shouting_inner` 显示 `SPK_OUT/SPK_IN/SPK_IN_OUT`；语音通话页接通时已联动拉起 `shouting_inner/shouting_outer`，断开后恢复；耳机检测业务状态仍未闭合 | 待真机确认节点、权限、电平语义、录音链和耳机检测触发方式，尤其 `GPIO1_B0` 插拔高低电平 | 部分收口：喊话 GPIO 使能链已接，耳机检测状态机未收口 |
| 按键 / ttyS0 串口协议 | `serial.keyboard=ttyS0`、`KeyboardConfig`、自检已接 | 未接按键串口监听、协议解析、页面按键消费 | 待样本帧、波特率、按键码映射 | 未收口：只是接入口 |
| 刷卡 / RFID / I2C-3 | `RfidConfig` 已补 I2C-3 字段；RFID adapter 已有 stub/real，并补了设备层 `waitCardRemoved` 等待离卡语义 | 签到/考勤可读 `RfidAdapter`；调度中心刷卡测试已按设备层等待离卡节奏轮询；真实读取仍是文件/命令/mock 桥接 | 待 I2C 地址、厂商 SDK 或命令、真卡读取验证 | 部分收口：签到/刷卡测试链闭合，I2C-3 未收口 |
| TTS / 讯飞离线包 | TTS 设置已接 | `LegacyStationAudioUseCase` 使用 Android `TextToSpeech` 并按配置开关/音量回退播报 | 待确认系统默认引擎是否为讯飞、离线包是否可用 | 部分收口：应用侧 TTS 闭合，讯飞引擎未锁定 |
| 模拟摄像头 / 4 路 AHD/CVBS | `CameraConfig` 四路通道、Manifest CAMERA 已接 | 首页和视频监控页可走 `openPreview`；自动切换可读 IO1/IO2 | 待 cameraId 与 CAMERA0~3/100~103 对照、AHD/CVBS 实机画面验证 | 部分收口：软件预览链已接，通道真值待收口 |
| CAN can0/can1 / JNI | `CanConfig`、模板、自检、`M90CanMap`、`device_expansion` 已接 | 未接 JNI/SocketCAN/命令收发适配，未接业务消费 | 待厂商 JNI、SocketCAN 节点或命令桥接 | 未收口：只是接入口 |
| 系统签名 | `config/signing.local.properties`、`config/signing/` 已有 | 打包签名属于构建链，不是运行时业务 | 待正式系统签名文件和 release 打包验证 | 独立构建项，不能算设备接入收口 |

## GPIO 功能分配补记

按当前 M90 基线代码、模板配置和 `M90IoMap`，这批 GPIO 的整理可以先按下面这张表理解：

| 功能 | GPIO 引脚 | 当前项目 key | 当前备注 |
| --- | --- | --- | --- |
| IO1 | `GPIO1_D0` | `io1` | 通用 IO；M90 现有业务里被当作倒车触发输入使用 |
| IO2 | `GPIO1_D1` | `io2` | 通用 IO；M90 现有业务里被当作中门触发输入使用 |
| IO3 | `GPIO1_D2` | `io3` | 通用 IO；当前只到通用读写/自检 |
| IO4 | `GPIO1_D3` | `io4` | 通用 IO；配置默认值记为 `1`，当前只能解释为软件默认/回退值，不能直接当成硬件上电默认高已被证实 |
| IO5 | `GPIO1_D4` | `io5` | 通用 IO；配置默认值记为 `0`，当前只能解释为软件默认/回退值，不能直接当成硬件上电默认低已被证实 |
| 内音 | `GPIO1_B1` | `inner_audio` | 音频路径控制 GPIO，不是音频数据线 |
| 外音 | `GPIO1_B2` | `outer_audio` | 音频路径控制 GPIO，不是音频数据线 |
| 内喇叭 | `GPIO0_D6` | `inner_speaker` | 音频路径控制 GPIO，不是音频数据线 |
| 喊话器内 | `GPIO3_A3` | `shouting_inner` | 内部喊话控制 |
| 喊话器外 | `GPIO3_A4` | `shouting_outer` | 外部喊话控制 |
| 耳机检测控制 | `GPIO3_A6` | `headphone_detect_power` | 按当前硬件说明已把默认值改为高；具体业务电平语义待真机确认 |
| 耳机检测 IO | `GPIO1_B0` | `headphone_detect` | 当前更像耳机插入检测输入脚，具体有效电平待真机确认 |

基于这张表，当前项目对 IO 的接入结论要收紧成下面这句：

- 现在已经可以介入“通用 GPIO 配置 + 统一读写层 + 已知业务消费”这一层。
- 不能说已经把 IO1~IO5 的全部业务语义都介入完了；当前只有 IO1/IO2 已在现有业务里形成消费，IO3/IO4/IO5 仍主要停留在通用读写和自检层。
- `IO4=默认高`、`IO5=默认低` 目前在代码里体现为 `defaultValue`，也就是配置默认/读失败回退值；它不是已经被真机证明的“硬件上电默认电平”。
- `GPIO3_A6` 和 `GPIO1_B0` 当前已能纳入配置和读写，但“一个是检测使能，一个是检测输入”仍属于高概率解释，不应在没有真机验证前写死成最终硬件结论。

落到当前工程里，GPIO 不需要走 Linux 旧式 `export/direction/value` 那套 sysfs；M90 已确认走 `/proc/rp_gpio/gpio...` 节点。本项目现在已经具备两层可直接介入的能力：

- 配置层：`config/terminal-config.template.json` 和 `ShellConfigLoader.createDefault()` 已把这批 pin 的 `pinId/valuePath/defaultValue` 落好。
- 运行时层：`ShellRuntime.get().getGpioAdapter().read/write(...)` 已能通过 `M90RealGpioAdapter` 直接读写 `/proc/rp_gpio/gpio...`。

所以如果你问“现在能不能介入 IO”，答案是：

- 能，前提是你说的“介入”是把这批 pin 纳入当前项目的统一 GPIO 读写和已知业务链。
- 还不能把它说成“IO 整块已经全收口”；因为 IO3/IO4/IO5 的业务语义、耳机检测状态机、以及 IO4/IO5 默认电平的硬件真值还没被真机坐实。

## 剩余收口总览

按运行时设备/协议能力计算，当前 14 项里没有全量真机闭合项；其中 4 项属于软件主链基本闭合、等待真机验证，7 项属于部分接入但还缺协议/业务/真机闭合，3 项仍只是配置接入口。另有“系统签名”是独立构建项，不计入运行时设备接入数量。

- 软件主链基本闭合、主要剩真机验证：串口 GPS、RS232-1/DVR、内音/外音/内喇叭、模拟摄像头。
- 部分接入、仍有明显缺口：通讯/JT808/第三方平台、串口映射、RS485 屏显、GPIO IO1~IO5、喊话器/耳机检测、RFID/I2C-3、TTS/讯飞离线包。
- 只有配置接入口、未形成业务闭合：LocationManager、ttyS0 按键串口协议、CAN can0/can1。

## 未全收口项

当前协议文档存在，但运行时没有接入的项：

- `公交平台-第三方平台对接协议文档`：文档内容是 WebService/JSON 基础数据管理接口，包含登录 `0xB001`、组织架构 `0xB002`、`REGKEY`、`Base64/GZip PARAM` 等；当前代码未发现 `MSGTYPE/REGKEY/ServicePort` 相关客户端实现。

当前协议文档存在，且已经部分接入但没有全量收口的项：

- `JTT 808-2019.PDF`：现有代码已接 0x7E 包裹、0x7D 转义、XOR 校验、消息 ID、消息体长度、终端号、流水号等基础帧，并覆盖注册 `0x0100`、鉴权 `0x0102`、心跳 `0x0002`、定位 `0x0200`、通用应答 `0x0001` 及 M90 扩展的报站/考勤/请求/升级等部分消息；但不是 JT/T 808-2019 全量实现。

当前只有“配置接入口”但没有业务链闭合的项：

- `LocationManager` 标准定位
- ttyS0 按键串口协议
- CAN can0/can1
- IO3/IO4/IO5 通用 IO 检测
- 耳机检测 GPIO 业务状态

当前业务链已有，但真机仍未闭合的项：

- 串口 GPS `/dev/ttyS5`
- RS232-1 / DVR
- GPIO IO1/IO2 视频联动，需 `gpio.mode=real` 并确认 `/proc/rp_gpio/gpio1d0/gpio1d1`
- RS485 屏显，其中 `LHXY-TD-LED2` 的通达/TD 主帧已部分接入，协议全量和真机口位未闭合
- 内音 / 外音 / 内喇叭
- 喊话器内/外 GPIO 状态显示，需 `gpio.mode=real` 并确认 `/proc/rp_gpio/gpio3a4/gpio3a3`
- RFID 签到桥接，尤其 I2C-3 真读卡
- TTS，尤其讯飞离线引擎确认
- 四路模拟摄像头通道

## 关联协议文档核对

### 公交平台-第三方平台对接协议文档

这份文档当前不能算已接入。它描述的是基础数据管理 WebService 接口，默认端口 `6006`，请求形态是 JSON 包裹 `MSGTYPE/TRANSNO/REGKEY/PARAM`，登录为 `0xB001`，组织架构为 `0xB002`，`PARAM` 还涉及 UTF-8、Base64 和 GZip。当前项目已接的是终端侧 JT808/AL808 Socket 调度链，不是这份第三方平台 WebService 基础数据链。

现有代码里没有找到 `MSGTYPE`、`REGKEY`、`ServicePort`、`0xB001`、`0xB002` 的运行时实现；因此它应标为“协议文档已入库，运行时未接入”。

### JTT 808-2019.PDF

这份文档不能算全量对齐。当前项目的 `Jt808Codec`、`Jt808FrameDecoder`、`Jt808FrameStreamParser` 和 `Jt808FrameInspector` 已经覆盖 JT808 基础帧骨架：`0x7E` 帧头帧尾、`0x7D 0x01/0x02` 转义、消息 ID、消息体属性低 10 位长度、6 字节 BCD 终端号、流水号和 XOR 校验。

当前 `Jt808LegacyMessages` 已能生成部分终端上行或应答消息：终端通用应答 `0x0001`、心跳 `0x0002`、注册 `0x0100`、鉴权 `0x0102`、定位 `0x0200`、终端属性应答 `0x0107`，以及 M90/AL808 扩展的报站 `0x0B02`、违规 `0x0B04`、司机考勤 `0x0B05`、校时应答 `0x0B06`、请求消息 `0x0B09`、升级通知 `0x0B0A`、线路切换 `0x0B0E/0xDB0E`。`UpgradeBusinessModule` 还会监听平台下发的 `0x8B0A` 下载命令并交给下载代理。

没有收口的部分也很明确：当前编码和解码仍按 12 字节老头部处理，终端号固定 6 字节 BCD；没有解析或生成 2019 版协议版本标识、10 字节终端手机号、分包封装项、加密位和完整消息体属性；平台下行命令只实质消费了 `0x8B0A`，`0x8001/0x8100/0x8103` 等只在调试摘要里登记，未形成完整业务状态机。因此它应标为“JT808 基础帧 + M90 扩展部分接入，JT/T 808-2019 全量未收口”。

### LHXY-TD-LED2协议-20230613

这份文档不是完全未接。文档里的帧格式是 `0xBB + ADDR + LEN + CMD + DATA + SUML + SUMH + 0x55`，地址 `0x10`，常见命令包括广播线路状态 `0x01`、线路字符 `0x02`、车内屏字符 `0x03`、报站 `0x08`、侧牌站点 `0x11`。

当前 `TongDaDisplayProtocol` 已实现并可通过 `LegacyStationDisplayUseCase` 走 RS485 发送的部分包括：线路字符 `0x02`、车内屏字符 `0x03`、报站 `0x08`、侧牌站点 `0x11`。配置协议名切到 `通达` 或 `TD` 时会选择这套生成器。

还没有全收口的部分包括：默认模板里 `rs485Protocol` 仍是 `无`；宣传/广告 `0x05` 和中英文线路 `0x10` 未实现；文档写的是 `38400bps`，当前 RS485 模板是 `9600`，需要按屏实物确认；真机还要确认实际走 `ttyS7` 还是 `ttyS9`。

## 当前最容易误判的点

1. “配置里有节点”不等于“真机已经能收发”。比如 RS485-2、IO3/IO4、喊话 GPIO、RFID I2C、CAN 现在已进入配置/自检/模块状态，但真实业务还要看协议、SDK 和节点权限。
2. “有 real adapter”不等于“真机可用”。串口依赖 `/dev/ttySx` 权限和 `stty`，GPIO 依赖 `valuePath`，Camera 依赖正确 cameraId，RFID 目前还不是 I2C。
3. 开发指南里的标准 Android 接口和旧 M90 业务实现要分开看。GPS 当前走串口 NMEA，不走 LocationManager；TTS 走系统 TextToSpeech，不直接绑定讯飞 SDK。

## 建议后续核对顺序

1. 先核当前已在 M90 主链使用的能力：网络 Socket、GPS 串口、RS232-1/DVR、IO1/IO2 摄像头切换、内外音/内喇叭、Camera 预览、RFID 签到桥接。
2. 再核“配置已有但真机参数未定”的能力：GPIO `valuePath`、cameraId、RFID real 读取命令、串口 real 权限。
3. 再按真机证据逐个实装真实收发：LocationManager 快照源、I2C-3 RFID 读卡、CAN/JNI 或 SocketCAN、ttyS0 按键帧、喊话/耳机检测状态机。