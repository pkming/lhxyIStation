# scripts

这里留给后面的辅助脚本。

根目录现在补了统一入口：

```bash
sh apk.sh rebuild
sh apk.sh update
```

推荐流程：

```bash
sh apk.sh rebuild --pin-base
sh apk.sh update
```

其中：

- `rebuild` 会执行 `./gradlew assembleRelease`，并把完整包复制到 `apk/release/`
- `rebuild --pin-base` 会额外把这版完整包固化到 `apk/base/latest-base.apk`
- `update` 会默认读取 `apk/base/latest-base.apk`，再透传给 `scripts/create_tinker_patch.sh`

当前已补一条热更新补丁生成命令：

```bash
./scripts/create_tinker_patch.sh --old-apk /path/to/base.apk
```

注意：`/path/to/base.apk` 只是占位符，必须替换成真实路径。

如果设备当前安装的完整包就是你刚打出来的 release，先把它固化成单独基线文件，再出后续 patch：

```bash
base_apk="build/tinker/baselines/base-0.1.0.apk"
mkdir -p "$(dirname "$base_apk")"
cp "app/build/outputs/apk/release/$(grep -E '"outputFile"' app/build/outputs/apk/release/output-metadata.json | head -n 1 | sed -E 's/.*"outputFile"[[:space:]]*:[[:space:]]*"([^"]+)".*/\1/')" "$base_apk"
./scripts/create_tinker_patch.sh --old-apk "$base_apk"
```

这个写法只适合“设备当前安装的基线包刚好就是这次 release 输出”的场景。
如果已经基于这版包连续发过 patch，后续继续出第 2 个、第 3 个 patch 时，`--old-apk` 仍然要固定指向设备最初安装的那版完整基线包，不能切成后续新 APK。
脚本内部也会再把 old/new apk 快照到 `build/tinker/apk-inputs/`，避免 `assembleRelease` 刷新 release 目录时互相覆盖。

默认会自动打 release、下载 Tinker CLI，并在 `build/hotfix/<patchVersion>/` 生成：

- patch APK
- `manifest.json`
- `index.json`
- `tinker_config.xml`

同时会默认自动上传：

- patch APK -> `hotfix/android11/releases/<tinkerId>/<patchVersion>/patch_signed.apk`
- `manifest.json` -> `hotfix/android11/releases/<tinkerId>/<patchVersion>/manifest.json`
- `index.json` -> `hotfix/android11/releases/<tinkerId>/index.json`
- `manifest.json` -> `hotfix/android11/manifest.json`

上传顺序固定是：

1. 先上传归档 patch
2. 再上传归档 manifest
3. 再上传 index.json
4. 最后覆盖线上 manifest 指针

不传 `--patch-version` 时，脚本会按同一 `tinkerId` 自动生成：

- `0.1.0-p001`
- `0.1.0-p002`
- `0.1.0-p003`

如果这次只想本地生成，不上传 OSS：

```bash
./scripts/create_tinker_patch.sh --old-apk "$old_apk" --skip-upload
```

预期会放：

- 报文格式转换
- 协议回放
- 日志整理
- 导出问题包

脚本也要按职责归类，不要把一次性调试命令散在各处。
