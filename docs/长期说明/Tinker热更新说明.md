# Tinker 热更新说明

当前 Android 11 新壳已经接入 Tinker 热更新运行时，并把入口放到文件管理页：

- 设置 -> 文件管理
- “导出日志”下面新增“检查更新”按钮

当前为了测试提速，热更新检查已经先写死：

- 固定 manifest 路径：`hotfix/android11/manifest.json`
- 设备端优先用代码里的测试 OSS 配置
- 当前不是“必须先改配置文件”才能点通

点击后流程固定如下：

1. 读取 `assets/oss-config.properties`
2. 拉取热更新 manifest
3. 校验当前安装包版本是否匹配补丁目标版本
4. 下载 patch APK 到应用私有目录
5. 校验大小和 MD5
6. 调用 Tinker 下发补丁
7. 等待应用下次冷启动生效

## 1. 运行配置

热更新继续复用 OSS 配置文件：

- `config/oss-config.template.properties`
- `config/oss-config.local.properties`

新增字段：

- `hotUpdateEnabled=false`
- `hotUpdateManifestUrl=`
- `hotUpdateManifestObjectKey=`
- `hotUpdateTimeoutMillis=30000`

说明：

1. 如果补丁 manifest 是公网 URL，直接填 `hotUpdateManifestUrl`。
2. 如果 manifest 放在 OSS 私有桶，填 `hotUpdateManifestObjectKey`，应用会用现有 `bucket / endpoint / accessKeyId / accessKeySecret` 做签名 GET。
3. `hotUpdateEnabled=true` 但既没配 URL 也没配 object key 时，按钮会直接报“热更新未配置”。

但当前测试包先不走上面这套动态配置，直接走固定路径：

- `hotfix/android11/manifest.json`

## 2. Manifest 格式

当前 manifest 约定 JSON 结构如下：

```json
{
  "enabled": true,
  "patchVersion": "20260514-001",
  "targetVersionCode": 1,
  "targetVersionName": "0.1.0",
  "patchObjectKey": "hotfix/android11/patch-20260514-001.apk",
  "patchMd5": "0123456789abcdef0123456789abcdef",
  "patchSizeBytes": 123456,
  "releaseNotes": "修复真机联调问题"
}
```

也支持下面这些别名：

- `baseVersionCode` 等价于 `targetVersionCode`
- `baseVersionName` 等价于 `targetVersionName`
- `version` 等价于 `patchVersion`
- `patchUrl` 可替代 `patchObjectKey`

规则：

1. `targetVersionCode/baseVersionCode` 必须和设备当前安装包版本一致，否则不会下发。
2. `patchVersion` 用来判断“是不是已经下发过这版补丁”，不要留空。
3. `patchMd5` 和 `patchSizeBytes` 虽然是可选，但真机上建议都带，避免坏包直接下发。

## 3. 设备侧行为

按钮点击成功后，不会静默重装 APK，而是：

1. 把补丁下载到应用私有目录 `files/hot-update/`
2. 调用 Tinker 的 `onReceiveUpgradePatch`
3. 在首页提示区写入进度和结果
4. 返回“已下发热更新补丁 / 重启应用后生效”

如果同一 `patchVersion` 已经下发过，再次点击会直接返回“当前已是最新热更新”。

## 4. 限制

Tinker 本身的限制仍然成立：

1. 不能改 AndroidManifest。
2. 不适合新增导出组件或改应用入口。
3. 当前仓库只接了设备端“检查并应用补丁”链，补丁包生成仍由发布侧按 Tinker 标准流程产出。

## 5. 验证方式

### 一键生成补丁

仓库现在已经补了发布侧一键命令：

```bash
sh apk.sh update --old-apk /path/to/base.apk
```

如果要先构建完整包，并把这次完整包固化成默认基线：

```bash
sh apk.sh rebuild --pin-base
```

后续继续出 patch，直接：

```bash
sh apk.sh update
```

`apk.sh update` 会在没显式传 `--old-apk` 时，默认读取 `apk/base/latest-base.apk`。

默认行为：

1. 如果没传 `--new-apk`，自动执行 `:app:assembleRelease`。
2. 自动下载 `tinker-patch-cli-1.9.15.2.jar` 到 `build/tinker/`。
3. 不传 `--patch-version` 时，自动递增生成 `<tinkerId>-pNNN` 形式的补丁版本号。
4. 在 `build/hotfix/<patchVersion>/` 生成 patch 包、`manifest.json`、`index.json` 和当次使用的 `tinker_config.xml`。
5. 默认先上传归档产物，最后再覆盖线上 manifest 指针。

常用补充参数：

- `--new-apk /path/to/new.apk`：直接指定新包
- `--skip-build`：配合 `--new-apk` 跳过 release 构建
- `--patch-version 20260514183000`：自定义补丁版本号
- `--patch-object-key hotfix/android11/20260514183000/patch_signed_7zip.apk`：直接写设备端要读的 OSS object key
- `--release-notes "修复真机联调问题"`
- `--skip-upload`：只在本地生成，不上传 OSS

注意：

1. 基线包必须是这次补了 `TINKER_ID` 之后重新构建并安装的 release 包，否则 patch 无法正确匹配。
2. 设备端当前固定从 `hotfix/android11/manifest.json` 读取 manifest，所以默认上传会最后一步才覆盖这个 object key。
3. 默认 OSS 版本结构是：`hotfix/android11/releases/<tinkerId>/<patchVersion>/...`，同一基线的 patch 会按 `p001 / p002 / p003` 递增。
4. 自动上传优先读取 `config/oss-config.local.properties`，没有它才会回退模板文件。
5. 连续出多个 patch 时，`--old-apk` 要始终保持为设备最初安装的那版完整基线包，不能每次都换成最新 release APK；否则后续 patch 很容易打在错误基线上。
6. 脚本会先把 old/new apk 快照到 `build/tinker/apk-inputs/`，避免 `assembleRelease` 刷新 `app/build/outputs/apk/release/` 时把基线包覆盖掉。

本轮代码层已验证：

```bash
JAVA_TOOL_OPTIONS='-Dhttp.proxyHost= -Dhttp.proxyPort=0 -Dhttps.proxyHost= -Dhttps.proxyPort=0 -DsocksProxyHost= -DsocksProxyPort=0 -Djava.net.useSystemProxies=false' ./gradlew :app:assembleDebug
```

如果本机 Maven 下载被系统代理拦住，继续沿用上面的 `JAVA_TOOL_OPTIONS` 即可。