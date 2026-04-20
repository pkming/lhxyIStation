# 配置目录

这里专门放新壳的运行配置，不要再把这些值散到代码里：

- 串口口位
- 波特率
- 调度 host / port
- socket mode
- GPIO 路径
- Camera 通道映射
- RFID 文件 / 命令桥接
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
- GPIO / Camera / RFID / SystemOps 也都按同样规则切 `mode=real`。
- 需要改口位、host、port，先改这里，再改文档，不要直接改页面代码。
