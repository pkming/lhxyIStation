package com.lhxy.istationdevice.android11.domain.gps;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialReceiveListener;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;
import com.lhxy.istationdevice.android11.protocol.gps.GpsStreamParser;

import java.util.List;

/**
 * GPS 串口监视器
 * <p>
 * 统一把 GPS 串口原始字节、组句和定位快照日志收口在这里。
 */
public final class GpsSerialMonitor {
    private static final String TAG = "GpsSerialMonitor";

    private final GpsStreamParser streamParser = new GpsStreamParser();
    private volatile String attachedChannelKey;
    private volatile String attachedPortName;
    private volatile GpsFixSnapshot latestSnapshot;

    /**
     * 绑定 GPS 串口监听。
     */
    public void attach(SerialPortAdapter serialPortAdapter, ShellConfig.SerialChannel serialChannel, String traceId) {
        if (attachedPortName != null && !attachedPortName.equals(serialChannel.getPortName())) {
            serialPortAdapter.removeReceiveListener(attachedPortName);
        }
        streamParser.reset();
        latestSnapshot = null;
        attachedChannelKey = serialChannel.getKey();
        attachedPortName = serialChannel.getPortName();
        serialPortAdapter.setReceiveListener(serialChannel.getPortName(), buildListener(traceId));
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "已绑定 GPS 串口监听: " + serialChannel.getKey() + "/" + serialChannel.getPortName(),
                traceId
        );
    }

    /**
     * 解绑 GPS 串口监听。
     */
    public void detach(SerialPortAdapter serialPortAdapter, String portName, String traceId) {
        if (attachedPortName != null && !attachedPortName.equals(portName)) {
            serialPortAdapter.removeReceiveListener(attachedPortName);
        }
        serialPortAdapter.removeReceiveListener(portName);
        streamParser.reset();
        attachedChannelKey = null;
        attachedPortName = null;
        latestSnapshot = null;
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "已解绑 GPS 串口监听: " + portName, traceId);
    }

    /**
     * 当前最新的一份定位快照。
     */
    public GpsFixSnapshot getLatestSnapshot() {
        return latestSnapshot;
    }

    /**
     * 当前是否已经绑定串口监听。
     */
    public boolean isAttached() {
        return attachedPortName != null && !attachedPortName.trim().isEmpty();
    }

    /**
     * 当前绑定的串口 key。
     */
    public String getAttachedChannelKey() {
        return attachedChannelKey;
    }

    /**
     * 当前绑定的串口设备名。
     */
    public String getAttachedPortName() {
        return attachedPortName;
    }

    /**
     * 返回当前 GPS 监听状态，统一给首页、调试页和导出包复用。
     */
    public String describeStatus() {
        StringBuilder builder = new StringBuilder("GPS 监听:");
        if (isAttached()) {
            builder.append("\n- 已绑定 ").append(valueOrDash(attachedChannelKey))
                    .append(" / ").append(valueOrDash(attachedPortName));
        } else {
            builder.append("\n- 当前还没绑定监听");
        }

        if (latestSnapshot == null) {
            builder.append("\n- 还没有收到定位数据");
        } else {
            builder.append("\n").append(latestSnapshot.describe());
        }
        return builder.toString();
    }

    private SerialReceiveListener buildListener(String traceId) {
        return (portName, payload) -> {
            AppLogCenter.log(
                    LogCategory.PROTOCOL_RX,
                    LogLevel.DEBUG,
                    TAG,
                    "gps raw " + portName + " -> " + Hexs.toHex(payload),
                    traceId + "-gps-raw"
            );
            List<GpsFixSnapshot> snapshots = streamParser.accept(payload);
            for (GpsFixSnapshot snapshot : snapshots) {
                latestSnapshot = snapshot;
                AppLogCenter.log(
                        LogCategory.BIZ,
                        LogLevel.INFO,
                        TAG,
                        snapshot.describe(),
                        traceId + "-gps-fix"
                );
            }
        };
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
