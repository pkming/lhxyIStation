# IStationDevice Android 11 新壳

这是 Android 11 版的 M90 终端复刻工程。

当前只保留最常用入口：构建、配置和真机联调文档。

## 先看这些

1. [docs/长期说明/真机联调必要项.md](./docs/长期说明/真机联调必要项.md)
2. [config/README.md](./config/README.md)
3. [docs/长期说明/Tinker热更新说明.md](./docs/长期说明/Tinker热更新说明.md)
4. [docs/长期说明/配置管理.md](./docs/长期说明/配置管理.md)
5. [docs/长期说明/调试与工作流.md](./docs/长期说明/调试与工作流.md)
6. [docs/README.md](./docs/README.md)

## 构建

```bash
sh apk.sh rebuild
```

如果这次构建出来的完整包就是设备要安装的基线包，直接执行：

```bash
sh apk.sh rebuild --pin-base
```

## 热更新补丁

日常继续发 patch，直接执行：

```bash
sh apk.sh update
```

这条命令会默认使用 `build/tinker/baselines/latest-base.apk` 作为基线包。
这条命令会默认使用 `apk/base/latest-base.apk` 作为基线包。

先准备当前设备已安装的基线 APK，然后执行：

```bash
sh apk.sh update --old-apk /path/to/base.apk
```

上面的 `/path/to/base.apk` 是占位符，要替换成真实文件路径。

如果你刚打完 release，先把这次设备要安装的完整包固化成单独基线文件，再拿它出后续 patch：

```bash
sh apk.sh rebuild --pin-base
sh apk.sh update
```

默认会继续把 patch 和 manifest 自动上传到 OSS。

默认目录规则已经改成：

1. 归档 patch：`hotfix/android11/releases/<tinkerId>/<patchVersion>/patch_signed.apk`
2. 归档 manifest：`hotfix/android11/releases/<tinkerId>/<patchVersion>/manifest.json`
3. 版本索引：`hotfix/android11/releases/<tinkerId>/index.json`
4. 线上指针：`hotfix/android11/manifest.json`

脚本会先上传归档 patch / manifest / index，最后才覆盖线上 `manifest.json`。

如果这次只想本地生成、不上传 OSS，可以加：

```bash
sh apk.sh update --skip-upload
```

如果已经有新 APK，不想让脚本再自动打包，可以直接：

```bash
sh apk.sh update --old-apk /path/to/base.apk --new-apk /path/to/new.apk --skip-build
```

说明：

1. `base.apk` 必须是设备当前安装的那一版 release 包。
2. 第一次立基线，建议用 `sh apk.sh rebuild --pin-base`，后续就能直接 `sh apk.sh update`。
3. 这版基线包必须是补了 `TINKER_ID` 之后重新构建安装的包。
4. 脚本会先把 old/new apk 快照到 `build/tinker/apk-inputs/`，避免 `assembleRelease` 刷新 `app/build/outputs/apk/release/` 时互相覆盖。
5. `rebuild` 会把完整包复制到 `apk/release/`，`rebuild --pin-base` 会额外复制到 `apk/base/`，方便后续直接找基线包。
6. 脚本会在 `build/hotfix/<patchVersion>/` 生成 patch 和 `manifest.json`，并默认自动上传到 OSS。
7. 自动上传优先读取 `config/oss-config.local.properties`。
8. 连续出第 2 个、第 3 个 patch 时，`--old-apk` 仍然要一直指向设备最初安装的那版基线 APK，不要改成后续新打出来的 APK。
9. 不传 `--patch-version` 时，脚本会按同一 `tinkerId` 自动递增生成 `<tinkerId>-p001 / p002 / p003 ...`。
10. 如果要把设备或模拟器重置回“干净基线”再测热更新，不能只做 `adb install -r` 覆盖安装；它会保留应用数据里的 Tinker 补丁目录，启动时仍可能自动加载旧 patch。要先执行 `adb uninstall com.lhxy.istationdevice.android11`，或者至少 `adb shell pm clear com.lhxy.istationdevice.android11`，再安装基线包。

## 配置优先级

运行时固定按下面顺序取配置：

1. `terminal-config.runtime.json`
2. `terminal-config.local.json`
3. `terminal-config.template.json`

真机联调优先改 `config/terminal-config.local.json` 或设备上的 runtime 配置，不要直接改页面代码或硬编码口位。

## 关键目录

- `app/`：应用入口和正式页面
- `modules/`：业务、协议和 M90 设备适配层
- `config/`：模板、本地覆盖和运行期配置说明
- `docs/`：联调、配置、架构和历史记录
