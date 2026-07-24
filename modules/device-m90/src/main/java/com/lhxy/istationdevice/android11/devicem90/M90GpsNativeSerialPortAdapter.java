package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortConfig;
import com.lhxy.istationdevice.android11.deviceapi.SerialReceiveListener;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * GPS 专用串口适配器——复用 V32 原生 libgps_serial_port.so。
 * <p>
 * 与 {@link M90ManagedJhySerialPortAdapter} 模式一致：
 * <ul>
 *   <li>open/close/read 走原生 JNI，不走 RandomAccessFile，彻底规避 O_NONBLOCK 阻塞问题。</li>
 *   <li>实现通用 {@link SerialPortAdapter} 接口，{@link com.lhxy.istationdevice.android11.domain.module.GpsBusinessModule}
 *       无需感知底层实现差异。</li>
 *   <li>read() 返回 String（NMEA 全为 ASCII），转 ISO-8859-1 byte[] 后喂给 GpsSerialMonitor，与 V32 行为一致。</li>
 * </ul>
 */
public final class M90GpsNativeSerialPortAdapter implements SerialPortAdapter {
    private static final String TAG = "M90GpsNativeSerial";
    private static final long RAW_LOG_INTERVAL_MS = 10_000L;
    private static final int INITIAL_RAW_LOG_LIMIT = 3;
    private static final int RAW_LOG_PREVIEW_LENGTH = 160;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean nativeAvailable = M90NativeGpsSerialPort.LIBRARY_LOADED;

    private volatile M90NativeGpsSerialPort port;
    private volatile String openedPortPath;
    private volatile int openedBaudRate;
    private volatile Thread readerThread;
    private volatile SerialReceiveListener receiveListener;

    public boolean isNativeAvailable() {
        return nativeAvailable;
    }

    @Override
    public void open(SerialPortConfig config, String traceId) {
        if (!nativeAvailable) {
            log(LogCategory.ERROR, LogLevel.WARN, "GPS native library unavailable, open skipped", traceId);
            return;
        }
        String portPath = normalizePortPath(config.getPortName());
        log(LogCategory.DEVICE, LogLevel.INFO, "native open request " + portPath + " @" + config.getBaudRate(), traceId);
        executor.execute(() -> openInternal(portPath, config.getBaudRate(), traceId));
    }

    @Override
    public void close(String portName, String traceId) {
        executor.execute(() -> {
            closeQuietly();
            log(LogCategory.DEVICE, LogLevel.INFO, "native close " + normalizePortPath(portName), traceId);
        });
    }

    @Override
    public boolean isOpen(String portName) {
        return port != null;
    }

    @Override
    public void send(String portName, byte[] payload, String traceId) {
        // GPS 串口只收不发，保留此方法供接口兼容
        M90NativeGpsSerialPort currentPort = port;
        if (currentPort == null) {
            return;
        }
        executor.execute(() -> {
            try {
                currentPort.write(new String(payload, StandardCharsets.ISO_8859_1));
            } catch (Throwable e) {
                log(LogCategory.ERROR, LogLevel.WARN, "native gps send failed: " + e.getMessage(), traceId);
            }
        });
    }

    @Override
    public void setReceiveListener(String portName, SerialReceiveListener listener) {
        receiveListener = listener;
    }

    @Override
    public void removeReceiveListener(String portName) {
        receiveListener = null;
    }

    // -------------------------------------------------------------------------

    private void openInternal(String portPath, int baudRate, String traceId) {
        if (port != null && portPath.equals(openedPortPath) && baudRate == openedBaudRate) {
            log(LogCategory.DEVICE, LogLevel.DEBUG, "native open reused " + portPath + " @" + baudRate, traceId);
            return;
        }
        closeQuietly();
        try {
            M90NativeGpsSerialPort newPort = new M90NativeGpsSerialPort();
            int result = newPort.open(portPath, baudRate);
            if (result < 0) {
                throw new IllegalStateException("open result=" + result);
            }
            port = newPort;
            openedPortPath = portPath;
            openedBaudRate = baudRate;
            log(LogCategory.DEVICE, LogLevel.INFO, "native open " + portPath + " @" + baudRate, traceId);
            startReadLoop(traceId);
        } catch (Throwable e) {
            closeQuietly();
            handleNativeFailure("native open failed on " + portPath, e, traceId);
        }
    }

    private void startReadLoop(String traceId) {
        M90NativeGpsSerialPort currentPort = port;
        String currentPortPath = openedPortPath;
        if (currentPort == null || currentPortPath == null) {
            return;
        }
        log(LogCategory.DEVICE, LogLevel.INFO, "native gps read loop start " + currentPortPath, traceId);
        Thread thread = new Thread(() -> {
            String readTraceId = traceId + "-rx";
            long rawPacketCount = 0L;
            long lastRawLogTimeMs = 0L;
            try {
                while (currentPort == port && currentPortPath.equals(openedPortPath)) {
                    String raw = currentPort.read();
                    if (raw == null || raw.isEmpty()) {
                        if (currentPort != port || !currentPortPath.equals(openedPortPath)) {
                            break;
                        }
                        continue;
                    }
                    // NMEA 语句全为 ASCII，ISO-8859-1 转换无损
                    byte[] payload = raw.getBytes(StandardCharsets.ISO_8859_1);
                    rawPacketCount++;
                    long now = System.currentTimeMillis();
                    if (rawPacketCount <= INITIAL_RAW_LOG_LIMIT || now - lastRawLogTimeMs >= RAW_LOG_INTERVAL_MS) {
                        lastRawLogTimeMs = now;
                        log(LogCategory.PROTOCOL_RX, LogLevel.DEBUG,
                                "native gps recv sample on " + currentPortPath
                                        + " packet=" + rawPacketCount
                                        + " bytes=" + payload.length
                                        + " ascii=\"" + previewAscii(raw) + "\"",
                                readTraceId);
                    }
                    SerialReceiveListener listener = receiveListener;
                    if (listener != null) {
                        listener.onReceive(currentPortPath, payload);
                    } else {
                        log(LogCategory.ERROR, LogLevel.WARN,
                                "native gps recv dropped, listener missing on " + currentPortPath, readTraceId);
                    }
                }
            } catch (Throwable e) {
                if (currentPort == port && currentPortPath.equals(openedPortPath)) {
                    handleNativeFailure("native gps recv failed on " + currentPortPath, e, readTraceId);
                }
            } finally {
                log(LogCategory.DEVICE, LogLevel.INFO, "native gps read loop stop " + currentPortPath, readTraceId);
                if (currentPort == port && currentPortPath.equals(openedPortPath)) {
                    closeQuietly();
                }
            }
        }, "gps-native-rx-" + currentPortPath.replace('/', '_'));
        thread.setDaemon(true);
        readerThread = thread;
        thread.start();
    }

    private void closeQuietly() {
        M90NativeGpsSerialPort currentPort = port;
        port = null;
        openedPortPath = null;
        openedBaudRate = 0;
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

    private void handleNativeFailure(String message, Throwable throwable, String traceId) {
        if (throwable instanceof UnsatisfiedLinkError) {
            nativeAvailable = false;
            log(LogCategory.ERROR, LogLevel.ERROR,
                    message + ": JNI unavailable, GPS native adapter disabled / " + throwable.getMessage(), traceId);
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

    private String previewAscii(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        String text = raw.replace("\r", "\\r").replace("\n", "\\n");
        return text.length() <= RAW_LOG_PREVIEW_LENGTH ? text : text.substring(0, RAW_LOG_PREVIEW_LENGTH) + "...";
    }

    private void log(LogCategory category, LogLevel level, String message, String traceId) {
        AppLogCenter.log(category, level, TAG, message, traceId);
    }
}
