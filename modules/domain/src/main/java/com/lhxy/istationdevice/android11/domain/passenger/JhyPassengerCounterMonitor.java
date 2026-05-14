package com.lhxy.istationdevice.android11.domain.passenger;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

import java.util.Arrays;

public final class JhyPassengerCounterMonitor {
    private static final String TAG = "JhyPassengerCounter";
    private static final String SERIAL_KEY = "rs485_2";
    private static final String PROTOCOL_NAME = "JHY";
    private static final long REQUEST_THROTTLE_MS = 800L;
    private static final int MAX_PENDING_BYTES = 512;

    private final SerialPortAdapter serialPortAdapter;
    private final Object bufferLock = new Object();
    private volatile JhyPassengerCounterState state = JhyPassengerCounterState.empty();
    private volatile String activePortName = "";
    private volatile long lastRequestTimeMs;
    private byte[] pending = new byte[0];

    public JhyPassengerCounterMonitor(SerialPortAdapter serialPortAdapter) {
        this.serialPortAdapter = serialPortAdapter;
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
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "JHY 串口未配置: " + e.getMessage(), "jhy-passenger-config");
            return;
        }
        String portName = channel.getPortName();
        if (!portName.equals(activePortName)) {
            stop("jhy-passenger-switch");
            activePortName = portName;
        }
        if (!serialPortAdapter.isOpen(portName)) {
            serialPortAdapter.open(channel.toSerialPortConfig(), "jhy-passenger-open");
        }
        serialPortAdapter.setReceiveListener(portName, this::onReceive);
        requestCurrentCount("jhy-passenger-start");
    }

    public JhyPassengerCounterState getState() {
        return state;
    }

    public void requestCurrentCount(String traceId) {
        String portName = activePortName;
        if (portName == null || portName.trim().isEmpty() || !serialPortAdapter.isOpen(portName)) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastRequestTimeMs < REQUEST_THROTTLE_MS) {
            return;
        }
        lastRequestTimeMs = now;
        byte[] payload = JhyPassengerCounterProtocol.createCurrentCount();
        AppLogCenter.log(LogCategory.PROTOCOL_TX, LogLevel.DEBUG, TAG, "JHY_CURRENT_COUNT -> " + Hexs.toHex(payload), traceId);
        serialPortAdapter.send(portName, payload, traceId);
    }

    public void stop(String traceId) {
        String portName = activePortName;
        if (portName == null || portName.trim().isEmpty()) {
            return;
        }
        serialPortAdapter.removeReceiveListener(portName);
        activePortName = "";
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
        if (!isSamePort(portName, activePortName) || payload == null || payload.length == 0) {
            return;
        }
        synchronized (bufferLock) {
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
        while (pending.length >= JhyPassengerCounterProtocol.CURRENT_COUNT_FRAME_SIZE) {
            int start = findFrameStart(pending);
            if (start < 0) {
                pending = new byte[0];
                return;
            }
            if (start > 0) {
                pending = Arrays.copyOfRange(pending, start, pending.length);
            }
            if (pending.length < JhyPassengerCounterProtocol.CURRENT_COUNT_FRAME_SIZE) {
                return;
            }
            byte[] frame = Arrays.copyOfRange(pending, 0, JhyPassengerCounterProtocol.CURRENT_COUNT_FRAME_SIZE);
            JhyPassengerCounterState parsed = JhyPassengerCounterProtocol.parseCurrentCountFrame(frame);
            if (parsed == null) {
                pending = Arrays.copyOfRange(pending, 1, pending.length);
                continue;
            }
            state = parsed;
            AppLogCenter.log(
                    LogCategory.PROTOCOL_RX,
                    LogLevel.DEBUG,
                    TAG,
                    "JHY_CURRENT_COUNT <- " + Hexs.toHex(frame)
                            + " / FIN=" + parsed.getFrontIn()
                            + " FOUT=" + parsed.getFrontOut()
                            + " BIN=" + parsed.getBackIn()
                            + " BOUT=" + parsed.getBackOut()
                            + " ALL=" + parsed.getTotal(),
                    "jhy-passenger-rx-" + portName
            );
            pending = Arrays.copyOfRange(pending, JhyPassengerCounterProtocol.CURRENT_COUNT_FRAME_SIZE, pending.length);
        }
    }

    private int findFrameStart(byte[] buffer) {
        for (int i = 0; i + 3 < buffer.length; i++) {
            if ((buffer[i] & 0xFF) == 0x63
                    && (buffer[i + 1] & 0xFF) == 0x00
                    && (buffer[i + 2] & 0xFF) == 0x11
                    && (buffer[i + 3] & 0xFF) == 0x28) {
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
}