package com.lhxy.istationdevice.android11.domain.gps;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialReceiveListener;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;
import com.lhxy.istationdevice.android11.protocol.gps.GpsStreamParser;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.LongSupplier;

/**
 * GPS 串口监视器
 * <p>
 * 统一把 GPS 串口原始字节、组句和定位快照日志收口在这里。
 */
public final class GpsSerialMonitor {
    private static final String TAG = "GpsSerialMonitor";
    private static final long RAW_LOG_INTERVAL_MS = 10_000L;
    private static final long FIX_LOG_INTERVAL_MS = 10_000L;
    private static final int INITIAL_RAW_LOG_LIMIT = 3;
    private static final long RAW_DATA_TIMEOUT_MS = 3_000L;
    private static final long VALID_FIX_TIMEOUT_MS = 5_000L;

    private final GpsStreamParser streamParser = new GpsStreamParser();
    private final CopyOnWriteArrayList<SnapshotListener> snapshotListeners = new CopyOnWriteArrayList<>();
    private final LongSupplier clock;
    private volatile String attachedChannelKey;
    private volatile String attachedPortName;
    private volatile GpsFixSnapshot latestSnapshot;
    private volatile GpsFixSnapshot latestUsableSnapshot;
    private volatile SerialPortAdapter attachedAdapter;
    private volatile boolean syntheticSnapshotActive;
    private volatile long lastRawReceiveTimeMs;
    private volatile long lastValidFixTimeMs;
    private long rawPacketCount;
    private long lastRawLogTimeMs;
    private long lastFixLogTimeMs;
    private GpsFixSnapshot lastLoggedSnapshot;

    public GpsSerialMonitor() {
        this(System::currentTimeMillis);
    }

    GpsSerialMonitor(LongSupplier clock) {
        this.clock = clock == null ? System::currentTimeMillis : clock;
    }

    /**
     * 绑定 GPS 串口监听。
     */
    public void attach(SerialPortAdapter serialPortAdapter, ShellConfig.SerialChannel serialChannel, String traceId) {
        if (attachedPortName != null && !attachedPortName.equals(serialChannel.getPortName())) {
            serialPortAdapter.removeReceiveListener(attachedPortName);
        }
        streamParser.reset();
        latestSnapshot = null;
        latestUsableSnapshot = null;
        syntheticSnapshotActive = false;
        lastRawReceiveTimeMs = 0L;
        lastValidFixTimeMs = 0L;
        resetLogState();
        attachedChannelKey = serialChannel.getKey();
        attachedPortName = serialChannel.getPortName();
        attachedAdapter = serialPortAdapter;
        serialPortAdapter.setReceiveListener(serialChannel.getPortName(), buildListener(traceId));
        safeLog(
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
        resetLogState();
        attachedChannelKey = null;
        attachedPortName = null;
        attachedAdapter = null;
        latestSnapshot = null;
        latestUsableSnapshot = null;
        syntheticSnapshotActive = false;
        lastRawReceiveTimeMs = 0L;
        lastValidFixTimeMs = 0L;
        safeLog(LogCategory.BIZ, LogLevel.INFO, TAG, "已解绑 GPS 串口监听: " + portName, traceId);
    }

    /**
     * 当前最新的一份定位快照。
     */
    public GpsFixSnapshot getLatestSnapshot() {
        GpsConnectionState state = getConnectionState();
        if (state == GpsConnectionState.DISCONNECTED
                || state == GpsConnectionState.RECONNECTING
                || state == GpsConnectionState.EXPIRED) {
            return null;
        }
        GpsFixSnapshot usableSnapshot = latestUsableSnapshot;
        return usableSnapshot == null ? latestSnapshot : usableSnapshot;
    }

    /**
     * Publishes a controlled fix for field testing when the physical receiver has no fix.
     * This follows the same listener path as a real NMEA fix, so position reports
     * and automatic station logic remain exercised end to end.
     */
    public void publishSyntheticSnapshot(GpsFixSnapshot snapshot, String traceId) {
        if (snapshot == null) {
            return;
        }
        long now = clock.getAsLong();
        syntheticSnapshotActive = true;
        lastRawReceiveTimeMs = now;
        latestSnapshot = snapshot;
        if (hasUsableCoordinates(snapshot) && snapshot.isValid()) {
            latestUsableSnapshot = snapshot;
        }
        if (isAuthoritativeValidFix(snapshot)) {
            lastValidFixTimeMs = now;
        }
        logFixIfNeeded(snapshot, traceId == null ? "gps-synthetic" : traceId);
        notifySnapshotListeners(snapshot);
    }

    /**
     * Stops treating the last published snapshot as a synthetic source.
     * The snapshot itself remains available briefly so the final station event
     * can finish while the real receiver resumes normal ownership.
     */
    public void finishSyntheticSnapshot() {
        syntheticSnapshotActive = false;
    }

    public GpsConnectionState getConnectionState() {
        String portName = attachedPortName;
        SerialPortAdapter adapter = attachedAdapter;
        if (portName == null || adapter == null) {
            return GpsConnectionState.DISCONNECTED;
        }
        if (!adapter.isOpen(portName)) {
            return GpsConnectionState.RECONNECTING;
        }
        if (syntheticSnapshotActive && latestSnapshot != null && latestSnapshot.isValid()) {
            return GpsConnectionState.FIXED;
        }
        long now = clock.getAsLong();
        if (lastRawReceiveTimeMs <= 0L) {
            return GpsConnectionState.SEARCHING;
        }
        if (now - lastRawReceiveTimeMs > RAW_DATA_TIMEOUT_MS) {
            return GpsConnectionState.RECONNECTING;
        }
        if (lastValidFixTimeMs > 0L && now - lastValidFixTimeMs <= VALID_FIX_TIMEOUT_MS) {
            return GpsConnectionState.FIXED;
        }
        if (lastValidFixTimeMs > 0L) {
            return GpsConnectionState.EXPIRED;
        }
        return GpsConnectionState.SEARCHING;
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

    public void addSnapshotListener(SnapshotListener listener) {
        if (listener == null) {
            return;
        }
        snapshotListeners.addIfAbsent(listener);
    }

    public void removeSnapshotListener(SnapshotListener listener) {
        if (listener == null) {
            return;
        }
        snapshotListeners.remove(listener);
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
        builder.append("\n- state=").append(getConnectionState());
        return builder.toString();
    }

    private SerialReceiveListener buildListener(String traceId) {
        return (portName, payload) -> {
            lastRawReceiveTimeMs = clock.getAsLong();
            logRawSampleIfNeeded(portName, payload, traceId);
            List<GpsFixSnapshot> snapshots = streamParser.accept(payload);
            for (GpsFixSnapshot snapshot : snapshots) {
                // A physical receiver can keep emitting no-fix sentences while
                // the explicit fixed-route replay is being exercised. Those
                // sentences must not replace or expire the controlled position.
                if (syntheticSnapshotActive) {
                    continue;
                }
                latestSnapshot = snapshot;
                if (hasUsableCoordinates(snapshot) && snapshot.isValid()) {
                    latestUsableSnapshot = snapshot;
                }
                if (isAuthoritativeValidFix(snapshot)) {
                    lastValidFixTimeMs = clock.getAsLong();
                }
                logFixIfNeeded(snapshot, traceId);
                GpsFixSnapshot displaySnapshot = latestUsableSnapshot == null ? snapshot : latestUsableSnapshot;
                notifySnapshotListeners(displaySnapshot);
            }
        };
    }

    private boolean hasUsableCoordinates(GpsFixSnapshot snapshot) {
        return snapshot != null
                && snapshot.getLatitudeDecimal() != null
                && !snapshot.getLatitudeDecimal().trim().isEmpty()
                && snapshot.getLongitudeDecimal() != null
                && !snapshot.getLongitudeDecimal().trim().isEmpty();
    }

    private boolean isAuthoritativeValidFix(GpsFixSnapshot snapshot) {
        if (snapshot == null || !snapshot.isValid() || snapshot.getSourceSentence() == null) {
            return false;
        }
        String sentence = snapshot.getSourceSentence();
        return sentence.startsWith("$GPRMC") || sentence.startsWith("$GNRMC")
                || sentence.startsWith("$BDRMC") || sentence.startsWith("$GPGGA")
                || sentence.startsWith("$GNGGA") || sentence.startsWith("$BDGGA");
    }

    private void notifySnapshotListeners(GpsFixSnapshot snapshot) {
        for (SnapshotListener listener : snapshotListeners) {
            try {
                listener.onSnapshot(snapshot);
            } catch (RuntimeException ignored) {
                // 不中断 GPS 解析，但必须记录：这个监听器很可能就是自动报站的触发回调，
                // 静默吞掉会造成“有定位却不报站、日志毫无痕迹”的黑洞。
                safeLog(LogCategory.ERROR, LogLevel.WARN, "GpsSerialMonitor",
                        "GPS快照监听器抛异常(自动报站回调可能受影响): " + ignored, "gps-snapshot-listener");
            }
        }
    }

    private void resetLogState() {
        rawPacketCount = 0;
        lastRawLogTimeMs = 0;
        lastFixLogTimeMs = 0;
        lastLoggedSnapshot = null;
    }

    private void logRawSampleIfNeeded(String portName, byte[] payload, String traceId) {
        rawPacketCount++;
        long now = System.currentTimeMillis();
        boolean initialSample = rawPacketCount <= INITIAL_RAW_LOG_LIMIT;
        boolean waitingForFix = latestSnapshot == null || !latestSnapshot.isValid();
        boolean intervalReached = now - lastRawLogTimeMs >= RAW_LOG_INTERVAL_MS;
        if (!initialSample && !(waitingForFix && intervalReached)) {
            return;
        }
        lastRawLogTimeMs = now;
        safeLog(
                LogCategory.PROTOCOL_RX,
                LogLevel.DEBUG,
                TAG,
                "gps raw sample " + portName
                        + " packet=" + rawPacketCount
                        + " bytes=" + (payload == null ? 0 : payload.length)
                        + " ascii=\"" + previewAscii(payload) + "\"",
                traceId + "-gps-raw"
        );
    }

    private void logFixIfNeeded(GpsFixSnapshot snapshot, String traceId) {
        if (snapshot == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (!shouldLogFix(snapshot, now)) {
            return;
        }
        lastFixLogTimeMs = now;
        lastLoggedSnapshot = snapshot;
        safeLog(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                snapshot.describeSummary(),
                traceId + "-gps-fix"
        );
    }

    private boolean shouldLogFix(GpsFixSnapshot snapshot, long now) {
        if (lastLoggedSnapshot == null) {
            return true;
        }
        if (lastLoggedSnapshot.isValid() != snapshot.isValid()) {
            return true;
        }
        if (lastLoggedSnapshot.getFixQuality() != snapshot.getFixQuality()) {
            return true;
        }
        if (lastLoggedSnapshot.getFixType() != snapshot.getFixType()) {
            return true;
        }
        if (Math.abs(lastLoggedSnapshot.getUsedSatellites() - snapshot.getUsedSatellites()) >= 2) {
            return true;
        }
        return now - lastFixLogTimeMs >= FIX_LOG_INTERVAL_MS;
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private void safeLog(LogCategory category, LogLevel level, String tag, String message, String traceId) {
        try {
            AppLogCenter.log(category, level, tag, message, traceId);
        } catch (RuntimeException ignored) {
            // Android logging is unavailable in local JVM tests; GPS state must remain operational.
        }
    }

    private String previewAscii(byte[] payload) {
        if (payload == null || payload.length == 0) {
            return "";
        }
        String text = new String(payload, StandardCharsets.US_ASCII)
                .replace("\r", "\\r")
                .replace("\n", "\\n");
        int maxLength = 160;
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }

    public interface SnapshotListener {
        void onSnapshot(GpsFixSnapshot snapshot);
    }

    public enum GpsConnectionState {
        DISCONNECTED,
        RECONNECTING,
        SEARCHING,
        FIXED,
        EXPIRED
    }
}
