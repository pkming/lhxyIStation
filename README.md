# IStationDevice Android 11

M90 公交报站终端在 Android 11 上的复刻工程（马来西亚 MLXY 现场部署，行为以最新现场版 **V32** 为准）。

## 模块结构

| 模块 | 职责 |
|---|---|
| `app` | 应用入口、旧版页面（首页/线路/站点学习/设置/调度…） |
| `modules/core` | 日志中心等基础设施 |
| `modules/domain` | 业务用例：报站音频、屏显、GPS/自动报站、调度、签到、升级、配置 |
| `modules/protocol` | JT808/AL808、通达屏显等协议编解码 |
| `modules/device-api` / `device-m90` | 设备抽象层 + M90 真机适配（串口/GPIO/RFID/客流…） |
| `modules/runtime` | 适配器与用例的组装、配置应用 |
| `modules/debug-tools` | 联调工具 |

## 构建 / 出包

```bash
sh apk.sh rebuild            # 编译 release，产物在 apk/release/
sh apk.sh newbase [版本号]    # 一键升版本 → 编全量 → 固定为新基线(apk/base/)
.\apk.bat newbase [版本号]    # Windows 等价命令，无需 Git Bash
sh apk.sh update             # 基于最新基线出热更补丁(默认上传 OSS)
sh apk.sh update --skip-upload   # 只本地生成、不上传
```

- 第一次立基线：`sh apk.sh rebuild --pin-base`。
- 热更基线默认取 `apk/base/latest-base.apk`；连续出多个 patch 时基线始终指向**设备最初安装的那版**，不要换成新 APK。
- 补丁 manifest 带基线 APK 的 MD5，设备指纹不符会直接拒绝补丁。
- 重置设备再测热更：先 `adb uninstall`（或 `pm clear`）再装基线，否则会残留旧 patch。

详见 [docs/长期说明/Tinker热更新说明.md](./docs/长期说明/Tinker热更新说明.md)。

## 配置

运行时按优先级取配置：`terminal-config.runtime.json` → `terminal-config.local.json` → `terminal-config.template.json`。

真机联调改 `config/terminal-config.local.json` 或设备上的 runtime 配置，**不要硬编码口位**。详见 [config/README.md](./config/README.md)。

## 端口约定（对齐 V32）

| 节点 | 用途 |
|---|---|
| `ttyS5` | GPS |
| `ttyS7` | RS485-1 报站屏（通达 TD） |
| `ttyS9` | RS485-2 客流计数器（JHY，协议选 JHY 才启用） |
| `ttyS3` | DVR |

## 先看这些

- [真机联调必要项](./docs/长期说明/真机联调必要项.md)
- [配置管理](./docs/长期说明/配置管理.md) ｜ [调试与工作流](./docs/长期说明/调试与工作流.md)
- [docs/README.md](./docs/README.md)

## Release 签名

- 签名文件：`config/signing/lhxy-istation-release.jks`
- Key Alias：`lhxy-istation-release`
- Store Password：`LhxyIStation-Release-2026!M90`
- Key Password：`LhxyIStation-Release-2026!M90`

首次改用此签名安装时，如果设备上存在使用其他证书签名的同包名应用，需要先卸载旧应用。
