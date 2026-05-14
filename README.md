# IStationDevice Android 11 新壳

这是 Android 11 版的 M90 终端复刻工程。

当前只保留最常用入口：构建、配置和真机联调文档。

## 先看这些

1. [docs/长期说明/真机联调必要项.md](./docs/长期说明/真机联调必要项.md)
2. [config/README.md](./config/README.md)
3. [docs/长期说明/配置管理.md](./docs/长期说明/配置管理.md)
4. [docs/长期说明/调试与工作流.md](./docs/长期说明/调试与工作流.md)
5. [docs/README.md](./docs/README.md)

## 构建

```bash
./gradlew assembleRelease
```

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
