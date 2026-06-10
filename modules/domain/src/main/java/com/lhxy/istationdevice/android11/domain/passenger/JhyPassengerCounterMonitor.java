package com.lhxy.istationdevice.android11.domain.passenger;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.JhySerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class JhyPassengerCounterMonitor {
    private static final String TAG = "JhyPassengerCounter";
    private static final String SERIAL_KEY = "rs485_2";
    private static final String PROTOCOL_NAME = "JHY";
    private static final long REQUEST_THROTTLE_MS = 800L;
    private static final long REQUEST_REOPEN_DELAY_MS = 180L;
    private static final int MAX_REQUEST_OPEN_ATTEMPTS = 3;
    private static final int MAX_PENDING_BYTES = 512;
    private static final int LOG_HEX_PREVIEW_BYTES = 48;

    private final SerialPortAdapter serialPortAdapter;
    private final JhySerialPortAdapter jhySerialPortAdapter;
    private final Object bufferLock = new Object();
    private final Object requestLock = new Object();
    private final ScheduledExecutorService requestExecutor;
    private final long requestThrottleMs;
    private final long requestReopenDelayMs;
    private volatile JhyPassengerCounterState state = JhyPassengerCounterState.empty();
    private volatile ShellConfig.SerialChannel activeChannel;
    private volatile String activePortName = "";
    private volatile StateListener stateListener;
    private volatile long lastRequestTimeMs;
    private byte[] pending = new byte[0];
    private long requestSequence;
    private ScheduledFuture<?> pendingRequestFuture;

    public interface StateListener {
        void onPassengerCounterStateChanged(JhyPassengerCounterState state);
    }

    public JhyPassengerCounterMonitor(SerialPortAdapter serialPortAdapter) {
        this(serialPortAdapter, null, REQUEST_THROTTLE_MS, REQUEST_REOPEN_DELAY_MS);
    }

    public JhyPassengerCounterMonitor(SerialPortAdapter serialPortAdapter, JhySerialPortAdapter jhySerialPortAdapter) {
        this(serialPortAdapter, jhySerialPortAdapter, REQUEST_THROTTLE_MS, REQUEST_REOPEN_DELAY_MS);
    }

    JhyPassengerCounterMonitor(
            SerialPortAdapter serialPortAdapter,
            JhySerialPortAdapter jhySerialPortAdapter,
            long requestThrottleMs,
            long requestReopenDelayMs
    ) {
        this.serialPortAdapter = serialPortAdapter;
        this.jhySerialPortAdapter = jhySerialPortAdapter;
        this.requestThrottleMs = Math.max(0L, requestThrottleMs);
        this.requestReopenDelayMs = Math.max(0L, requestReopenDelayMs);
        this.requestExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "jhy-passenger-request");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void updateConfig(ShellConfig shellConfig) {
        if (!isJhyEnabled(shellConfig)) {
            stop("jhy-passenger-disable");
            state = JhyPassengerCounterState.empty();
            return;
        }
        ShellConfig.SerialChannel channel;
        try {
            channel = shellConfig.requireSerialChannel(SERIAL_KEY);
        } catch (IllegalArgumentException e) {
            log(LogCategory.ERROR, LogLevel.WARN, "JHY 串口未配置: " + e.getMessage(), "jhy-passenger-config");
            return;
        }
        String portName = channel.getPortName();
        if (!portName.equals(activePortName)) {
            stop("jhy-passenger-switch");
            activePortName = portName;
        }
        activeChannel = channel;
        log(
            LogCategory.BIZ,
            LogLevel.INFO,
            "JHY config ready port=" + normalizePortName(portName)
                + " baud=" + channel.getBaudRate()
                + " transport=" + activeTransportName(),
            "jhy-passenger-config"
        );
        openActivePort(channel, "jhy-passenger-open");
        requestCurrentCount("jhy-passenger-start");
    }

    public JhyPassengerCounterState getState() {
        return state;
    }

    public void setStateListener(StateListener listener) {
        stateListener = listener;
    }

    public void requestCurrentCount(String traceId) {
        requestCurrentCount(traceId, 0L);
    }

    public void requestCurrentCountAfterStationDisplay(String traceId) {
        requestCurrentCount(traceId, requestThrottleMs);
    }

    private void requestCurrentCount(String traceId, long minDelayMs) {
        synchronized (requestLock) {
            log(
                LogCategory.BIZ,
                LogLevel.DEBUG,
                "JHY query requested port=" + normalizePortName(activePortName)
                    + " minDelay=" + minDelayMs
                    + "ms transport=" + activeTransportName(),
                traceId
            );
            scheduleRequestLocked(traceId, minDelayMs, 1);
        }
    }

    public void stop(String traceId) {
        String portName = activePortName;
        cancelPendingRequest();
        if (portName == null || portName.trim().isEmpty()) {
            activeChannel = null;
            return;
        }
        detachActivePort(portName, traceId);
        activePortName = "";
        activeChannel = null;
        lastRequestTimeMs = 0L;
        synchronized (bufferLock) {
            pending = new byte[0];
        }
        AppLogCenter.log(LogCategory.BIZ, LogLevel.DEBUG, TAG, "JHY passenger monitor stopped", traceId);
    }

    private boolean isJhyEnabled(ShellConfig shellConfig) {
        return shellConfig != null
                && shellConfig.getBasicSetupConfig() != null
                && shellConfig.getBasicSetupConfig().getSerialSettings() != null
                && PROTOCOL_NAME.equalsIgnoreCase(shellConfig.getBasicSetupConfig().getSerialSettings().getRs4852Protocol().trim());
    }

    private void onReceive(String portName, byte[] payload) {
        if (payload == null || payload.length == 0) {
            log(LogCategory.PROTOCOL_RX, LogLevel.DEBUG, "JHY RX ignored empty payload", "jhy-passenger-rx-empty");
            return;
        }
        if (!isSamePort(portName, activePortName)) {
            log(
                LogCategory.PROTOCOL_RX,
                LogLevel.WARN,
                "JHY RX ignored, port mismatch actual=" + normalizePortName(portName)
                    + " active=" + normalizePortName(activePortName)
                    + " bytes=" + payload.length
                    + " hex=" + summarizeHex(payload),
                "jhy-passenger-rx-mismatch"
            );
            return;
        }
        synchronized (bufferLock) {
            log(
                LogCategory.PROTOCOL_RX,
                LogLevel.DEBUG,
                "JHY RX chunk accepted port=" + normalizePortName(portName)
                    + " bytes=" + payload.length
                    + " pendingBefore=" + pending.length
                    + " hex=" + summarizeHex(payload),
                "jhy-passenger-rx-chunk"
            );
            appendPending(payload);
            parsePending(portName);
        }
    }

    private void appendPending(byte[] payload) {
        int newLength = Math.min(pending.length + payload.length, MAX_PENDING_BYTES);
        byte[] merged = new byte[newLength];
        int pendingCopyStart = Math.max(0, pending.length - Math.max(0, newLength - payload.length));
        int pendingCopyLength = pending.length - pendingCopyStart;
        if (pendingCopyLength > 0) {
            System.arraycopy(pending, pendingCopyStart, merged, 0, pendingCopyLength);
        }
        int payloadCopyLength = Math.min(payload.length, newLength - pendingCopyLength);
        System.arraycopy(payload, payload.length - payloadCopyLength, merged, pendingCopyLength, payloadCopyLength);
        pending = merged;
    }

    private void parsePending(String portName) {
        while (pending.length >= JhyPassengerCounterProtocol.MIN_CURRENT_COUNT_FRAME_SIZE) {
            int start = findFrameStart(pending);
            if (start < 0) {
                log(
                        LogCategory.PROTOCOL_RX,
                        LogLevel.WARN,
                        "JHY RX drop pending without frame head bytes=" + pending.length
                                + " hex=" + summarizeHex(pending),
                        "jhy-passenger-rx-drop-" + normalizePortName(portName)
                );
                pending = new byte[0];
                return;
            }
            if (start > 0) {
                log(
                        LogCategory.PROTOCOL_RX,
                        LogLevel.DEBUG,
                        "JHY RX skip leading bytes=" + start
                                + " pending=" + summarizeHex(pending),
                        "jhy-passenger-rx-resync-" + normalizePortName(portName)
                );
                pending = Arrays.copyOfRange(pending, start, pending.length);
            }
            int frameSize = JhyPassengerCounterProtocol.currentCountFrameSize(pending, 0);
            if (frameSize < 0) {
                log(
                        LogCategory.PROTOCOL_RX,
                        LogLevel.WARN,
                        "JHY RX unknown current-count head " + summarizeHex(pending),
                        "jhy-passenger-rx-unknown-" + normalizePortName(portName)
                );
                pending = Arrays.copyOfRange(pending, 1, pending.length);
                continue;
            }
            if (pending.length < frameSize) {
                return;
            }
            byte[] frame = Arrays.copyOfRange(pending, 0, frameSize);
            JhyPassengerCounterState parsed = JhyPassengerCounterProtocol.parseCurrentCountFrame(frame);
            if (parsed == null) {
                log(
                        LogCategory.PROTOCOL_RX,
                        LogLevel.WARN,
                        "JHY RX invalid current-count frame " + summarizeHex(frame),
                        "jhy-passenger-rx-invalid-" + normalizePortName(portName)
                );
                pending = Arrays.copyOfRange(pending, 1, pending.length);
                continue;
            }
            cancelPendingRequest();
            state = parsed;
            notifyStateChanged(parsed);
            log(
                    LogCategory.PROTOCOL_RX,
                    LogLevel.DEBUG,
                    "JHY_CURRENT_COUNT <- " + Hexs.toHex(frame)
                            + " / FIN=" + parsed.getFrontIn()
                            + " FOUT=" + parsed.getFrontOut()
                            + " BIN=" + parsed.getBackIn()
                            + " BOUT=" + parsed.getBackOut()
                            + " ALL=" + parsed.getTotal(),
                    "jhy-passenger-rx-" + portName
            );
            pending = Arrays.copyOfRange(pending, frameSize, pending.length);
        }
    }

    private int findFrameStart(byte[] buffer) {
        for (int i = 0; i + 3 < buffer.length; i++) {
            if (JhyPassengerCounterProtocol.currentCountFrameSize(buffer, i) > 0) {
                return i;
            }
        }
        return -1;
    }

    private boolean isSamePort(String receivedPortName, String configuredPortName) {
        if (receivedPortName == null || configuredPortName == null) {
            return false;
        }
        return normalizePortName(receivedPortName).equals(normalizePortName(configuredPortName));
    }

    private String normalizePortName(String portName) {
        String trimmed = portName.trim();
        return trimmed.startsWith("/dev/") ? trimmed.substring("/dev/".length()) : trimmed;
    }

    private void scheduleRequestLocked(String traceId, long minDelayMs, int openAttempt) {
        cancelPendingRequestLocked();
        long delayMs = Math.max(minDelayMs, remainingThrottleMs());
        long sequence = ++requestSequence;
        log(
                LogCategory.BIZ,
                LogLevel.DEBUG,
                "JHY query scheduled seq=" + sequence
                        + " delay=" + delayMs
                        + "ms minDelay=" + minDelayMs
                        + "ms openAttempt=" + openAttempt
                        + " port=" + normalizePortName(activePortName)
                        + " transport=" + activeTransportName(),
                traceId
        );
        pendingRequestFuture = requestExecutor.schedule(
                () -> performScheduledRequest(sequence, traceId, openAttempt),
                delayMs,
                TimeUnit.MILLISECONDS
        );
    }

    private void performScheduledRequest(long sequence, String traceId, int openAttempt) {
        String portName = activePortName;
        ShellConfig.SerialChannel channel = activeChannel;
        if (portName == null || portName.trim().isEmpty() || channel == null) {
            log(LogCategory.ERROR, LogLevel.WARN, "JHY query skipped, active port/channel missing", traceId);
            return;
        }
        synchronized (requestLock) {
            if (sequence != requestSequence) {
                log(LogCategory.BIZ, LogLevel.DEBUG, "JHY query skipped, stale seq=" + sequence + " current=" + requestSequence, traceId);
                return;
            }
            pendingRequestFuture = null;
        }
        log(
                LogCategory.BIZ,
                LogLevel.DEBUG,
                "JHY query executing seq=" + sequence
                        + " port=" + normalizePortName(portName)
                        + " openAttempt=" + openAttempt
                        + " transport=" + activeTransportName(),
                traceId
        );
        if (!isActivePortOpen(portName)) {
            log(LogCategory.BIZ, LogLevel.WARN, "JHY query reopening closed port " + normalizePortName(portName), traceId);
            openActivePort(channel, traceId + "-open");
            if (openAttempt < MAX_REQUEST_OPEN_ATTEMPTS) {
                synchronized (requestLock) {
                    if (sequence != requestSequence) {
                        return;
                    }
                    scheduleRequestLocked(traceId, requestReopenDelayMs, openAttempt + 1);
                }
            } else {
                log(
                        LogCategory.ERROR,
                        LogLevel.WARN,
                        "JHY query dropped, port still not open: " + portName,
                        traceId
                );
            }
            return;
        }
        long throttleDelayMs = remainingThrottleMs();
        if (throttleDelayMs > 0L) {
            log(LogCategory.BIZ, LogLevel.DEBUG, "JHY query throttled for " + throttleDelayMs + "ms", traceId);
            synchronized (requestLock) {
                if (sequence != requestSequence) {
                    return;
                }
                scheduleRequestLocked(traceId, throttleDelayMs, openAttempt);
            }
            return;
        }
        lastRequestTimeMs = System.currentTimeMillis();
        byte[] payload = JhyPassengerCounterProtocol.createCurrentCount();
        log(
            LogCategory.PROTOCOL_TX,
            LogLevel.DEBUG,
            "JHY_CURRENT_COUNT -> " + Hexs.toHex(payload)
                + " / port=" + normalizePortName(portName)
                + " transport=" + activeTransportName(),
            traceId
        );
        sendCurrentCount(portName, payload, traceId);
    }

    private boolean useNativeJhyPort() {
        return jhySerialPortAdapter != null && jhySerialPortAdapter.isSupported();
    }

    private boolean isActivePortOpen(String portName) {
        return useNativeJhyPort()
                ? jhySerialPortAdapter.isOpen(portName)
                : serialPortAdapter.isOpen(portName);
    }

    private void openActivePort(ShellConfig.SerialChannel channel, String traceId) {
        if (channel == null) {
            return;
        }
        String portName = channel.getPortName();
        log(
                LogCategory.DEVICE,
                LogLevel.INFO,
                "JHY open requested port=" + normalizePortName(portName)
                        + " baud=" + channel.getBaudRate()
                        + " transport=" + activeTransportName(),
                traceId
        );
        if (useNativeJhyPort()) {
            jhySerialPortAdapter.open(portName, channel.getBaudRate(), this::onReceive, traceId);
            return;
        }
        if (!serialPortAdapter.isOpen(portName)) {
            serialPortAdapter.open(channel.toSerialPortConfig(), traceId);
        }
        serialPortAdapter.setReceiveListener(portName, this::onReceive);
    }

    private void detachActivePort(String portName, String traceId) {
        log(LogCategory.DEVICE, LogLevel.INFO, "JHY close requested port=" + normalizePortName(portName) + " transport=" + activeTransportName(), traceId);
        if (useNativeJhyPort()) {
            jhySerialPortAdapter.close(portName, traceId);
            return;
        }
        serialPortAdapter.removeReceiveListener(portName);
    }

    private void sendCurrentCount(String portName, byte[] payload, String traceId) {
        log(
                LogCategory.PROTOCOL_TX,
                LogLevel.DEBUG,
                "JHY send dispatch port=" + normalizePortName(portName)
                        + " bytes=" + payload.length
                        + " transport=" + activeTransportName(),
                traceId
        );
        if (useNativeJhyPort()) {
            jhySerialPortAdapter.send(portName, payload, traceId);
            return;
        }
        serialPortAdapter.send(portName, payload, traceId);
    }

    private long remainingThrottleMs() {
        long waitMs = lastRequestTimeMs + requestThrottleMs - System.currentTimeMillis();
        return Math.max(waitMs, 0L);
    }

    private void cancelPendingRequest() {
        synchronized (requestLock) {
            cancelPendingRequestLocked();
        }
    }

    private void cancelPendingRequestLocked() {
        if (pendingRequestFuture != null) {
            pendingRequestFuture.cancel(false);
            pendingRequestFuture = null;
        }
        requestSequence++;
    }

    private String activeTransportName() {
        return useNativeJhyPort() ? "native" : "shared";
    }

    private String summarizeHex(byte[] payload) {
        if (payload == null || payload.length == 0) {
            return "-";
        }
        int previewLength = Math.min(payload.length, LOG_HEX_PREVIEW_BYTES);
        byte[] preview = Arrays.copyOf(payload, previewLength);
        String hex = Hexs.toHex(preview);
        if (previewLength >= payload.length) {
            return hex;
        }
        return hex + "...(" + payload.length + " bytes)";
    }

    private void notifyStateChanged(JhyPassengerCounterState parsed) {
        StateListener listener = stateListener;
        if (listener == null) {
            return;
        }
        try {
            listener.onPassengerCounterStateChanged(parsed);
        } catch (RuntimeException e) {
            log(LogCategory.ERROR, LogLevel.WARN, "JHY state listener failed: " + e.getMessage(), "jhy-passenger-state-listener");
        }
    }

    private void log(LogCategory category, LogLevel level, String message, String traceId) {
        try {
            AppLogCenter.log(category, level, TAG, message, traceId);
        } catch (RuntimeException ignore) {
            // Keep the serial path alive even if logging is unavailable in local tests.
        }
    }
}
