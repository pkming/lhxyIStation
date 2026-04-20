# modules

这里按职责拆模块，不按“谁顺手就塞哪”来放。

- `core`：通用基础设施
- `domain`：业务编排
- `protocol`：协议编解码
- `device-api`：硬件抽象接口
- `device-m90`：M90 具体实现
- `runtime`：共享运行时和跨页面设备状态
- `debug-tools`：调试能力

后面加新模块，先更新 `docs/项目架构.md`，再加目录。
