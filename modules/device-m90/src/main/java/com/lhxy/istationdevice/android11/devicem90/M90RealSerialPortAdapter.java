package com.lhxy.istationdevice.android11.devicem90;

import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortConfig;
import com.lhxy.istationdevice.android11.deviceapi.SerialReceiveListener;

import java.io.FileDescriptor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
    private static final long[] RECONNECT_DELAYS_MS = {1_000L, 2_000L, 5_000L, 10_000L};

    private final Map<String, SerialSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, SerialReceiveListener> listeners = new ConcurrentHashMap<>();

    /**
     * 打开真实串口。
     */
    @Override
    public void open(SerialPortConfig config, String traceId) {
        String portPath = normalizePortPath(config.getPortName());
        SerialSession session = sessions.computeIfAbsent(portPath, key -> new SerialSession());
        session.executor.execute(() -> {
            session.desiredOpen = true;
            session.desiredPortPath = portPath;
            session.desiredConfig = config;
            session.desiredTraceId = traceId;
            session.reconnectAttempt = 0;
            openInternal(session, portPath, config, traceId);
        });
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
            session.desiredOpen = false;
            session.closeActiveQuietly();
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
                int offset = 0;
                while (offset < payload.length) {
                    offset += Os.write(session.fileDescriptor, payload, offset, payload.length - offset);
                }
                AppLogCenter.log(LogCategory.PROTOCOL_TX, LogLevel.DEBUG, TAG, "real send on " + portPath + ": " + Hexs.toHex(payload), traceId);
            } catch (Exception e) {
                session.handleTransportFailure(traceId + "-send-failure");
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
        if (!session.desiredOpen) {
            return;
        }
        try {
            session.closeActiveQuietly();
            if (!new java.io.File(portPath).exists()) {
                throw new IllegalStateException("串口节点不存在: " + portPath);
            }

            configurePort(portPath, config.getBaudRate(), traceId);
            session.fileDescriptor = Os.open(
                    portPath,
                    OsConstants.O_RDWR | OsConstants.O_NOCTTY | OsConstants.O_NONBLOCK,
                    0
            );
            // Avoid a blocking device open, then restore normal blocking reads for the M90 UART driver.
            Os.fcntlInt(session.fileDescriptor, OsConstants.F_SETFL, 0);
            session.portPath = portPath;
            session.baudRate = config.getBaudRate();
            session.generation++;
            session.reconnectAttempt = 0;

            AppLogCenter.log(
                    LogCategory.DEVICE,
                    LogLevel.INFO,
                    TAG,
                    "real open " + portPath + " @" + config.getBaudRate(),
                    traceId
            );
            session.startReadLoop(traceId);
        } catch (Exception e) {
            session.closeActiveQuietly();
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "real open failed on " + portPath + ": " + e.getMessage(), traceId);
            session.scheduleReconnect();
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
                    "cs8",
                    "-parenb",
                    "-cstopb",
                    "-crtscts",
                    "-ixon",
                    "-ixoff",
                    "-ixany",
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
        private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        private volatile FileDescriptor fileDescriptor;
        private volatile String portPath;
        private volatile int baudRate;
        private volatile Thread readerThread;
        private volatile boolean desiredOpen;
        private volatile String desiredPortPath;
        private volatile SerialPortConfig desiredConfig;
        private volatile String desiredTraceId;
        private volatile long generation;
        private int reconnectAttempt;
        private long receiveCount;

        private boolean isOpen() {
            return fileDescriptor != null && fileDescriptor.valid();
        }

        /**
         * 启动收包线程。
         */
        private void startReadLoop(String traceId) {
            String currentPortPath = portPath;
            FileDescriptor currentFileDescriptor = fileDescriptor;
            long currentGeneration = generation;
            if (currentPortPath == null || currentFileDescriptor == null) {
                // 读线程没起来 → “打开了却一直收不到数据”，之前无日志。
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG,
                        "读线程未启动: port或inputStream为空 traceId=" + traceId, traceId);
                return;
            }

            Thread thread = new Thread(() -> {
                byte[] buffer = new byte[2048];
                String readTraceId = traceId + "-rx";
                // RX 日志节流：GPS 115200 会每秒收几百个小包，逐包写日志会刷爆会话日志、拖慢整机(菜单卡顿)。
                // 这里同一端口每秒最多打一条(带被抑制的条数/字节汇总)；数据流不变，listener 照收每个包。
                long lastRxLogMs = 0;
                int suppressedRxLogs = 0;
                long suppressedRxBytes = 0;
                try {
                    while (isCurrentSession(currentFileDescriptor, currentGeneration)) {
                        int length;
                        try {
                            length = Os.read(currentFileDescriptor, buffer, 0, buffer.length);
                        } catch (ErrnoException e) {
                            if (e.errno == OsConstants.EAGAIN) {
                                Thread.sleep(20L);
                                continue;
                            }
                            throw e;
                        }
                        if (length == 0) {
                            AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "real serial eof on " + currentPortPath, readTraceId);
                            break;
                        }

                        byte[] payload = new byte[length];
                        System.arraycopy(buffer, 0, payload, 0, length);
                        receiveCount++;
                        long nowMs = System.currentTimeMillis();
                        if (nowMs - lastRxLogMs >= 1000) {
                            AppLogCenter.log(
                                LogCategory.PROTOCOL_RX,
                                LogLevel.DEBUG,
                                TAG,
                                "real recv on " + currentPortPath
                                    + " @" + baudRate
                                    + " packet=" + receiveCount
                                    + " bytes=" + payload.length
                                    + (suppressedRxLogs > 0 ? " (近1s抑制" + suppressedRxLogs + "条/" + suppressedRxBytes + "字节)" : "")
                                    + ": " + Hexs.toHex(payload),
                                readTraceId
                            );
                            lastRxLogMs = nowMs;
                            suppressedRxLogs = 0;
                            suppressedRxBytes = 0;
                        } else {
                            suppressedRxLogs++;
                            suppressedRxBytes += payload.length;
                        }
                        SerialReceiveListener listener = listeners.get(currentPortPath);
                        if (listener != null) {
                            listener.onReceive(currentPortPath, payload.clone());
                        }
                    }
                } catch (Exception e) {
                    if (isCurrentSession(currentFileDescriptor, currentGeneration)) {
                        AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "real recv failed on " + currentPortPath + ": " + e.getMessage(), readTraceId);
                    }
                } finally {
                    executor.execute(() -> handleReaderStopped(currentFileDescriptor, currentGeneration, readTraceId));
                }
            }, "serial-rx-" + currentPortPath.replace("/", "_"));
            thread.setDaemon(true);
            readerThread = thread;
            thread.start();
        }

        private boolean isCurrentSession(FileDescriptor descriptor, long sessionGeneration) {
            return desiredOpen && descriptor != null && descriptor == fileDescriptor
                    && sessionGeneration == generation && descriptor.valid();
        }

        private void handleReaderStopped(FileDescriptor descriptor, long sessionGeneration, String traceId) {
            if (descriptor != fileDescriptor || sessionGeneration != generation) {
                return;
            }
            closeActiveQuietly();
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.WARN, TAG,
                    "real serial reader stopped, scheduling reconnect " + desiredPortPath, traceId);
            scheduleReconnect();
        }

        private void handleTransportFailure(String traceId) {
            closeActiveQuietly();
            scheduleReconnect();
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.WARN, TAG,
                    "real serial transport failed, scheduling reconnect " + desiredPortPath, traceId);
        }

        private void scheduleReconnect() {
            if (!desiredOpen || desiredConfig == null || desiredPortPath == null) {
                return;
            }
            int delayIndex = Math.min(reconnectAttempt, RECONNECT_DELAYS_MS.length - 1);
            long delayMs = RECONNECT_DELAYS_MS[delayIndex];
            reconnectAttempt++;
            String reconnectTraceId = (desiredTraceId == null ? "serial" : desiredTraceId)
                    + "-reconnect-" + reconnectAttempt;
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG,
                    "real reconnect scheduled " + desiredPortPath
                            + " attempt=" + reconnectAttempt + " delayMs=" + delayMs,
                    reconnectTraceId);
            executor.schedule(() -> {
                if (desiredOpen && !isOpen()) {
                    openInternal(this, desiredPortPath, desiredConfig, reconnectTraceId);
                }
            }, delayMs, TimeUnit.MILLISECONDS);
        }

        private void closeActiveQuietly() {
            generation++;
            try {
                if (fileDescriptor != null && fileDescriptor.valid()) {
                    Os.close(fileDescriptor);
                }
            } catch (Exception ignore) {
                // ignore
            }
            readerThread = null;
            receiveCount = 0;
            fileDescriptor = null;
            portPath = null;
            baudRate = 0;
        }
    }
}
