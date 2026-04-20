package com.lhxy.istationdevice.android11.domain;

import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SystemOps;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.gps.GpsSerialMonitor;
import com.lhxy.istationdevice.android11.domain.socket.Jt808SocketMonitor;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

/**
 * 首页摘要用例
 */
public final class ShellOverviewUseCase {
    /**
     * 构建首页固定摘要文案。
     */
    public String buildHomeSummary() {
        return "当前阶段：先把底座接完整，再接真机业务\n"
                + "\n"
                + "模块分法：\n"
                + "- app：页面和导航\n"
                + "- core：日志、工具、基础设施\n"
                + "- domain：业务编排\n"
                + "- protocol：协议编解码\n"
                + "- device-api：串口 / socket / GPIO 等抽象接口\n"
                + "- device-m90：M90 适配\n"
                + "- runtime：共享运行时和跨页面设备状态\n"
                + "- debug-tools：调试入口和报文工具\n"
                + "\n"
                + "业务模块：\n"
                + "- dispatch：调度\n"
                + "- station：报站\n"
                + "- signin：签到\n"
                + "- camera-dvr：摄像头 / DVR\n"
                + "- upgrade：升级\n"
                + "- file：文件导入导出\n"
                + "\n"
                + "这版先验证四件事：\n"
                + "1. 模块边界能不能站住\n"
                + "2. 屏显、808、GPS 解析能不能走通日志链\n"
                + "3. GPIO / Camera / RFID / SystemOps 能不能统一纳入 runtime\n"
                + "4. 六个业务模块能不能统一挂接和回放";
    }

    /**
     * 构建当前共享运行时摘要。
     */
    public String buildRuntimeSummary(
            ShellConfig shellConfig,
            SerialPortAdapter serialPortAdapter,
            SocketClientAdapter socketClientAdapter,
            GpsSerialMonitor gpsSerialMonitor,
            Jt808SocketMonitor jt808SocketMonitor,
            SystemOps systemOps
    ) {
        if (shellConfig == null) {
            return "运行状态:\n- 当前还没拿到配置";
        }

        StringBuilder builder = new StringBuilder("运行状态:");
        appendSerialStatus(builder, "屏显串口", shellConfig, shellConfig.getDebugReplay().getDisplaySerialKey(), serialPortAdapter);
        appendSerialStatus(builder, "GPS 串口", shellConfig, shellConfig.getDebugReplay().getGpsSerialKey(), serialPortAdapter);
        appendSocketStatus(builder, "JT808", shellConfig, shellConfig.getDebugReplay().getJt808SocketKey(), socketClientAdapter);
        appendSocketStatus(builder, "AL808", shellConfig, shellConfig.getDebugReplay().getAl808SocketKey(), socketClientAdapter);

        if (gpsSerialMonitor == null) {
            builder.append("\n- GPS 监听 -> 当前没有监视器实例");
            builder.append("\n- GPS 最新 -> 当前没有定位数据");
        } else {
            builder.append("\n- GPS 监听 -> ")
                    .append(gpsSerialMonitor.isAttached()
                            ? valueOrDash(gpsSerialMonitor.getAttachedChannelKey()) + "/" + valueOrDash(gpsSerialMonitor.getAttachedPortName())
                            : "未绑定");
            GpsFixSnapshot latestSnapshot = gpsSerialMonitor.getLatestSnapshot();
            builder.append("\n- GPS 最新 -> ")
                    .append(latestSnapshot == null
                            ? "还没有定位数据"
                            : valueOrDash(latestSnapshot.getLatitudeDecimal())
                            + ", "
                            + valueOrDash(latestSnapshot.getLongitudeDecimal())
                            + " / speed="
                            + valueOrDash(latestSnapshot.getSpeedKnots())
                            + "kn / sat="
                            + latestSnapshot.getUsedSatellites());
        }

        builder.append("\n- 静默安装 -> ").append(systemOps != null && systemOps.supportsSilentInstall() ? "支持" : "未接真机");
        builder.append("\n- 重启/校时 -> ").append(systemOps == null ? "当前没有系统能力实现" : "已封装，等真机实现");
        if (jt808SocketMonitor != null) {
            builder.append("\n").append(jt808SocketMonitor.describeStatus());
        }
        return builder.toString();
    }

    private void appendSerialStatus(
            StringBuilder builder,
            String label,
            ShellConfig shellConfig,
            String channelKey,
            SerialPortAdapter serialPortAdapter
    ) {
        try {
            ShellConfig.SerialChannel serialChannel = shellConfig.requireSerialChannel(channelKey);
            builder.append("\n- ").append(label).append(" ").append(serialChannel.getKey())
                    .append(" -> ")
                    .append(serialPortAdapter != null && serialPortAdapter.isOpen(serialChannel.getPortName()) ? "已打开" : "未打开");
        } catch (Exception e) {
            builder.append("\n- ").append(label).append(" -> 配置缺失");
        }
    }

    private void appendSocketStatus(
            StringBuilder builder,
            String label,
            ShellConfig shellConfig,
            String channelKey,
            SocketClientAdapter socketClientAdapter
    ) {
        try {
            ShellConfig.SocketChannel socketChannel = shellConfig.requireSocketChannel(channelKey);
            builder.append("\n- ").append(label).append(" ").append(socketChannel.getKey())
                    .append(" -> ")
                    .append(socketClientAdapter != null && socketClientAdapter.isConnected(socketChannel.getChannelName()) ? "已连接" : "未连接");
        } catch (Exception e) {
            builder.append("\n- ").append(label).append(" -> 配置缺失");
        }
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
