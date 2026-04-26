package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortConfig;
import com.lhxy.istationdevice.android11.deviceapi.SerialReceiveListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * M90 真串口适配器
 * <p>
 * 当前先落一个可直接访问 `/dev/ttySx` 的基础实现：
 * 能开口、发包、起读线程。
 * 波特率切换这件事后面仍然要接厂商 JNI / SDK。
 */
public final class M90RealSerialPortAdapter implements SerialPortAdapter {
    private static final String TAG = "M90RealSerial";
    private static final long RX_LOG_INTERVAL_MS = 10_000L;
    private static final int INITIAL_RX_LOG_LIMIT = 3;

    private final Map<String, SerialSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, SerialReceiveListener> listeners = new ConcurrentHashMap<>();

    /**
     * 打开真实串口。
     */
    @Override
    public void open(SerialPortConfig config, String traceId) {
        String portPath = normalizePortPath(config.getPortName());
        SerialSession session = sessions.computeIfAbsent(portPath, key -> new SerialSession());
        session.executor.execute(() -> openInternal(session, portPath, config, traceId));
    }

    /**
     * 关闭真实串口。
     */
    @Override
    public void close(String portName, String traceId) {
        String portPath = normalizePortPath(portName);
        SerialSession session = sessions.get(portPath);
        if (session == null) {
            return;
        }
        session.executor.execute(() -> {
            session.closeQuietly();
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "real close " + portPath, traceId);
        });
    }

    /**
     * 判断真实串口是否已打开。
     */
    @Override
    public boolean isOpen(String portName) {
        SerialSession session = sessions.get(normalizePortPath(portName));
        return session != null && session.isOpen();
    }

    /**
     * 发送真实串口数据。
     */
    @Override
    public void send(String portName, byte[] payload, String traceId) {
        String portPath = normalizePortPath(portName);
        SerialSession session = sessions.get(portPath);
        if (session == null) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "real send skipped, no session for " + portPath, traceId);
            return;
        }
        session.executor.execute(() -> {
            try {
                if (!session.isOpen()) {
                    AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "real send skipped, port not open: " + portPath, traceId);
                    return;
                }
                session.outputStream.write(payload);
                session.outputStream.flush();
                AppLogCenter.log(LogCategory.PROTOCOL_TX, LogLevel.DEBUG, TAG, "real send on " + portPath + ": " + Hexs.toHex(payload), traceId);
            } catch (Exception e) {
                session.closeQuietly();
                AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "real send failed on " + portPath + ": " + e.getMessage(), traceId);
            }
        });
    }

    @Override
    public void setReceiveListener(String portName, SerialReceiveListener listener) {
        String portPath = normalizePortPath(portName);
        if (listener == null) {
            listeners.remove(portPath);
            return;
        }
        listeners.put(portPath, listener);
    }

    @Override
    public void removeReceiveListener(String portName) {
        listeners.remove(normalizePortPath(portName));
    }

    private void openInternal(SerialSession session, String portPath, SerialPortConfig config, String traceId) {
        try {
            session.closeQuietly();
            File deviceFile = new File(portPath);
            if (!deviceFile.exists()) {
                throw new IllegalStateException("串口节点不存在: " + portPath);
            }

            configurePort(portPath, config.getBaudRate(), traceId);
            RandomAccessFile randomAccessFile = new RandomAccessFile(deviceFile, "rw");
            FileInputStream inputStream = new FileInputStream(randomAccessFile.getFD());
            FileOutputStream outputStream = new FileOutputStream(randomAccessFile.getFD());

            session.randomAccessFile = randomAccessFile;
            session.inputStream = inputStream;
            session.outputStream = outputStream;
            session.portPath = portPath;
            session.baudRate = config.getBaudRate();

            AppLogCenter.log(
                    LogCategory.DEVICE,
                    LogLevel.INFO,
                    TAG,
                    "real open " + portPath + " @" + config.getBaudRate(),
                    traceId
            );
            session.startReadLoop(traceId);
        } catch (Exception e) {
            session.closeQuietly();
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "real open failed on " + portPath + ": " + e.getMessage(), traceId);
        }
    }

    private String normalizePortPath(String portName) {
        if (portName == null || portName.trim().isEmpty()) {
            return "/dev/unknown";
        }
        String trimmed = portName.trim();
        return trimmed.startsWith("/") ? trimmed : "/dev/" + trimmed;
    }

    private void configurePort(String portPath, int baudRate, String traceId) {
        Process process = null;
        try {
            process = new ProcessBuilder(
                    "stty",
                    "-F",
                    portPath,
                    String.valueOf(baudRate),
                    "raw",
                    "-echo",
                    "-echoe",
                    "-echok",
                    "-icanon",
                    "min",
                    "1",
                    "time",
                    "0"
            ).redirectErrorStream(true).start();
            if (!process.waitFor(2, TimeUnit.SECONDS)) {
                process.destroy();
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "stty timeout on " + portPath + " @" + baudRate, traceId);
                return;
            }
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "stty applied " + portPath + " @" + baudRate, traceId);
            } else {
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "stty failed on " + portPath + " @" + baudRate + ", exit=" + exitCode, traceId);
            }
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "stty unavailable on " + portPath + ": " + e.getMessage(), traceId);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private final class SerialSession {
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private volatile RandomAccessFile randomAccessFile;
        private volatile FileInputStream inputStream;
        private volatile FileOutputStream outputStream;
        private volatile String portPath;
        private volatile int baudRate;
        private volatile Thread readerThread;
        private long receiveCount;
        private long lastReceiveLogTimeMs;

        private boolean isOpen() {
            return randomAccessFile != null;
        }

        /**
         * 启动收包线程。
         */
        private void startReadLoop(String traceId) {
            String currentPortPath = portPath;
            FileInputStream currentInputStream = inputStream;
            if (currentPortPath == null || currentInputStream == null) {
                return;
            }

            Thread thread = new Thread(() -> {
                byte[] buffer = new byte[2048];
                String readTraceId = traceId + "-rx";
                try {
                    while (isOpen() && currentPortPath.equals(portPath) && currentInputStream == inputStream) {
                        int length = currentInputStream.read(buffer);
                        if (length < 0) {
                            AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "real serial eof on " + currentPortPath, readTraceId);
                            break;
                        }
                        if (length == 0) {
                            continue;
                        }

                        byte[] payload = new byte[length];
                        System.arraycopy(buffer, 0, payload, 0, length);
                        if (shouldLogReceive()) {
                            AppLogCenter.log(
                                    LogCategory.PROTOCOL_RX,
                                    LogLevel.DEBUG,
                                    TAG,
                                    "real recv sample on " + currentPortPath
                                            + " @" + baudRate
                                            + " packet=" + receiveCount
                                            + " bytes=" + payload.length
                                            + ": " + Hexs.toHex(payload),
                                    readTraceId
                            );
                        }
                        SerialReceiveListener listener = listeners.get(currentPortPath);
                        if (listener != null) {
                            listener.onReceive(currentPortPath, payload.clone());
                        }
                    }
                } catch (Exception e) {
                    if (currentPortPath.equals(portPath)) {
                        AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "real recv failed on " + currentPortPath + ": " + e.getMessage(), readTraceId);
                    }
                } finally {
                    if (currentPortPath.equals(portPath)) {
                        closeQuietly();
                    }
                }
            }, "serial-rx-" + currentPortPath.replace("/", "_"));
            thread.setDaemon(true);
            readerThread = thread;
            thread.start();
        }

        private boolean shouldLogReceive() {
            receiveCount++;
            long now = System.currentTimeMillis();
            if (receiveCount <= INITIAL_RX_LOG_LIMIT || now - lastReceiveLogTimeMs >= RX_LOG_INTERVAL_MS) {
                lastReceiveLogTimeMs = now;
                return true;
            }
            return false;
        }

        private void closeQuietly() {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception ignore) {
                // ignore
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception ignore) {
                // ignore
            }
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (Exception ignore) {
                // ignore
            }
            readerThread = null;
            receiveCount = 0;
            lastReceiveLogTimeMs = 0;
            randomAccessFile = null;
            inputStream = null;
            outputStream = null;
            portPath = null;
            baudRate = 0;
        }
    }
}
