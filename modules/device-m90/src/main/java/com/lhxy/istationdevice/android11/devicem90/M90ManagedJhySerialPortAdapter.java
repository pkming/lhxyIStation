package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.JhySerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialReceiveListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 复用 M90 原生 JHY 串口库的专用适配器。
 */
public final class M90ManagedJhySerialPortAdapter implements JhySerialPortAdapter {
    private static final String TAG = "M90JhySerial";

    private final Map<String, JhySession> sessions = new ConcurrentHashMap<>();
    private volatile boolean nativeAvailable = M90NativeJhySerialPort.LIBRARY_LOADED;

    @Override
    public boolean isSupported() {
        return nativeAvailable;
    }

    @Override
    public void open(String portName, int baudRate, SerialReceiveListener listener, String traceId) {
        String portPath = normalizePortPath(portName);
        if (!isSupported()) {
            log(LogCategory.ERROR, LogLevel.WARN, "JHY native library unavailable", traceId);
            return;
        }
        log(LogCategory.DEVICE, LogLevel.INFO, "native open request " + portPath + " @" + baudRate, traceId);
        JhySession session = sessions.computeIfAbsent(portPath, key -> new JhySession());
        session.listener = listener;
        session.executor.execute(() -> openInternal(session, portPath, baudRate, traceId));
    }

    @Override
    public void close(String portName, String traceId) {
        JhySession session = sessions.get(normalizePortPath(portName));
        if (session == null) {
            return;
        }
        session.executor.execute(() -> {
            session.closeQuietly();
            log(LogCategory.DEVICE, LogLevel.INFO, "native close " + normalizePortPath(portName), traceId);
        });
    }

    @Override
    public boolean isOpen(String portName) {
        JhySession session = sessions.get(normalizePortPath(portName));
        return session != null && session.isOpen();
    }

    @Override
    public void send(String portName, byte[] payload, String traceId) {
        String portPath = normalizePortPath(portName);
        JhySession session = sessions.get(portPath);
        if (session == null) {
            log(LogCategory.ERROR, LogLevel.WARN, "native send skipped, no session for " + portPath, traceId);
            return;
        }
        session.executor.execute(() -> {
            M90NativeJhySerialPort currentPort = session.port;
            if (currentPort == null) {
                log(LogCategory.ERROR, LogLevel.WARN, "native send skipped, port not open: " + portPath, traceId);
                return;
            }
            try {
                int result = currentPort.write(payload);
                if (result < 0) {
                    throw new IllegalStateException("write result=" + result);
                }
                log(LogCategory.PROTOCOL_TX, LogLevel.DEBUG, "native send on " + portPath + ": " + Hexs.toHex(payload), traceId);
            } catch (Throwable e) {
                session.closeQuietly();
                handleNativeFailure("native send failed on " + portPath, e, traceId);
            }
        });
    }

    private void openInternal(JhySession session, String portPath, int baudRate, String traceId) {
        if (session.isOpen() && portPath.equals(session.portPath) && baudRate == session.baudRate) {
            log(LogCategory.DEVICE, LogLevel.DEBUG, "native open reused existing session " + portPath + " @" + baudRate, traceId);
            return;
        }
        session.closeQuietly();
        try {
            M90NativeJhySerialPort port = new M90NativeJhySerialPort();
            int result = port.open(portPath, baudRate);
            if (result < 0) {
                throw new IllegalStateException("open result=" + result);
            }
            session.port = port;
            session.portPath = portPath;
            session.baudRate = baudRate;
            log(LogCategory.DEVICE, LogLevel.INFO, "native open " + portPath + " @" + baudRate, traceId);
            session.startReadLoop(traceId);
        } catch (Throwable e) {
            session.closeQuietly();
            handleNativeFailure("native open failed on " + portPath, e, traceId);
        }
    }

    private void handleNativeFailure(String message, Throwable throwable, String traceId) {
        if (throwable instanceof UnsatisfiedLinkError) {
            nativeAvailable = false;
            log(
                    LogCategory.ERROR,
                    LogLevel.ERROR,
                    message + ": JNI unavailable, disable native JHY and fallback to SerialPortAdapter / " + throwable.getMessage(),
                    traceId
            );
            return;
        }
        log(LogCategory.ERROR, LogLevel.ERROR, message + ": " + throwable.getMessage(), traceId);
    }

    private String normalizePortPath(String portName) {
        if (portName == null || portName.trim().isEmpty()) {
            return "/dev/unknown";
        }
        String trimmed = portName.trim();
        return trimmed.startsWith("/") ? trimmed : "/dev/" + trimmed;
    }

    private void log(LogCategory category, LogLevel level, String message, String traceId) {
        AppLogCenter.log(category, level, TAG, message, traceId);
    }

    private final class JhySession {
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private volatile M90NativeJhySerialPort port;
        private volatile String portPath;
        private volatile int baudRate;
        private volatile Thread readerThread;
        private volatile SerialReceiveListener listener;

        private boolean isOpen() {
            return port != null;
        }

        private void startReadLoop(String traceId) {
            M90NativeJhySerialPort currentPort = port;
            String currentPortPath = portPath;
            if (currentPort == null || currentPortPath == null) {
                return;
            }
            log(LogCategory.DEVICE, LogLevel.INFO, "native read loop start " + currentPortPath + " @" + baudRate, traceId);
            Thread thread = new Thread(() -> {
                String readTraceId = traceId + "-rx";
                try {
                    while (currentPort == port && currentPortPath.equals(portPath)) {
                        byte[] payload = currentPort.read();
                        if (payload == null || payload.length == 0) {
                            if (currentPort != port || !currentPortPath.equals(portPath)) {
                                break;
                            }
                            continue;
                        }
                        log(
                                LogCategory.PROTOCOL_RX,
                                LogLevel.DEBUG,
                                "native recv on " + currentPortPath + " @" + baudRate + ": " + Hexs.toHex(payload),
                                readTraceId
                        );
                        SerialReceiveListener currentListener = listener;
                        if (currentListener != null) {
                            currentListener.onReceive(currentPortPath, payload.clone());
                        } else {
                            log(LogCategory.ERROR, LogLevel.WARN, "native recv dropped, listener missing on " + currentPortPath, readTraceId);
                        }
                    }
                } catch (Throwable e) {
                    if (currentPort == port && currentPortPath.equals(portPath)) {
                        handleNativeFailure("native recv failed on " + currentPortPath, e, readTraceId);
                    }
                } finally {
                    log(LogCategory.DEVICE, LogLevel.INFO, "native read loop stop " + currentPortPath, readTraceId);
                    if (currentPort == port && currentPortPath.equals(portPath)) {
                        closeQuietly();
                    }
                }
            }, "jhy-native-rx-" + currentPortPath.replace('/', '_'));
            thread.setDaemon(true);
            readerThread = thread;
            thread.start();
        }

        private void closeQuietly() {
            M90NativeJhySerialPort currentPort = port;
            port = null;
            portPath = null;
            baudRate = 0;
            readerThread = null;
            if (currentPort == null) {
                return;
            }
            try {
                currentPort.close();
            } catch (Throwable ignore) {
                // ignore
            }
        }
    }
}
