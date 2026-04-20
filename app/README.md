# app

这里放新壳应用入口。

当前约束：

- 先做页面和导航骨架。
- 不在这里直接写串口、GPIO、Camera、RFID 细节。
- 调试入口可以从这里进，但调试逻辑本身放到 `modules/debug-tools`。
