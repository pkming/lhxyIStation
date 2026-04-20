package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SocketEndpointConfig;
import com.lhxy.istationdevice.android11.deviceapi.SocketReceiveListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * M90 真 Socket 适配器
 * <p>
 * 用标准 Socket 建链，真正的 JT808 / AL808 联调走这里。
 */
public final class M90RealSocketClientAdapter implements SocketClientAdapter {
    private static final String TAG = "M90RealSocket";
    private static final int CONNECT_TIMEOUT_MS = 5000;
    private final Map<String, SocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, SocketReceiveListener> listeners = new ConcurrentHashMap<>();

    /**
     * 建立真实连接。
     */
    @Override
    public void connect(SocketEndpointConfig config, String traceId) {
        SocketSession session = sessions.computeIfAbsent(config.getChannelName(), key -> new SocketSession());
        session.executor.execute(() -> connectInternal(session, config, traceId));
    }

    /**
     * 关闭真实连接。
     */
    @Override
    public void disconnect(String channelName, String traceId) {
        SocketSession session = sessions.get(channelName);
        if (session == null) {
            return;
        }
        session.executor.execute(() -> {
            session.closeQuietly();
            AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "real disconnect " + channelName, traceId);
        });
    }

    /**
     * 判断真实连接是否已建立。
     */
    @Override
    public boolean isConnected(String channelName) {
        SocketSession session = sessions.get(channelName);
        return session != null && session.isConnected();
    }

    /**
     * 发送真实数据。
     */
    @Override
    public void send(String channelName, byte[] payload, String traceId) {
        SocketSession session = sessions.get(channelName);
        if (session == null) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "real send skipped, no session for " + channelName, traceId);
            return;
        }
        session.executor.execute(() -> {
            try {
                if (!session.isConnected()) {
                    AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "real send skipped, channel not connected: " + channelName, traceId);
                    return;
                }
                session.outputStream.write(payload);
                session.outputStream.flush();
                AppLogCenter.log(LogCategory.PROTOCOL_TX, LogLevel.DEBUG, TAG, "real socket send on " + channelName + ": " + Hexs.toHex(payload), traceId);
            } catch (Exception e) {
                session.closeQuietly();
                AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "real send failed on " + channelName + ": " + e.getMessage(), traceId);
            }
        });
    }

    @Override
    public void setReceiveListener(String channelName, SocketReceiveListener listener) {
        if (listener == null) {
            listeners.remove(channelName);
            return;
        }
        listeners.put(channelName, listener);
    }

    @Override
    public void removeReceiveListener(String channelName) {
        listeners.remove(channelName);
    }

    private void connectInternal(SocketSession session, SocketEndpointConfig config, String traceId) {
        try {
            session.closeQuietly();
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(config.getHost(), config.getPort()), CONNECT_TIMEOUT_MS);
            socket.setTcpNoDelay(true);
            socket.setKeepAlive(true);
            session.socket = socket;
            session.inputStream = socket.getInputStream();
            session.outputStream = socket.getOutputStream();
            session.endpointConfig = config;
            AppLogCenter.log(
                    LogCategory.DEVICE,
                    LogLevel.INFO,
                    TAG,
                    "real connect " + config.getChannelName() + " -> " + config.getHost() + ":" + config.getPort(),
                    traceId
            );
            session.startReadLoop(traceId);
        } catch (Exception e) {
            session.closeQuietly();
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "real connect failed on " + config.getChannelName() + ": " + e.getMessage(), traceId);
        }
    }

    private final class SocketSession {
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private volatile Socket socket;
        private volatile InputStream inputStream;
        private volatile OutputStream outputStream;
        private volatile SocketEndpointConfig endpointConfig;
        private volatile Thread readerThread;

        private boolean isConnected() {
            Socket currentSocket = socket;
            return currentSocket != null && currentSocket.isConnected() && !currentSocket.isClosed();
        }

        /**
         * 启动读循环，把回包直接打到协议接收日志。
         */
        private void startReadLoop(String traceId) {
            Socket currentSocket = socket;
            InputStream currentInputStream = inputStream;
            SocketEndpointConfig currentConfig = endpointConfig;
            if (currentSocket == null || currentInputStream == null || currentConfig == null) {
                return;
            }

            Thread thread = new Thread(() -> {
                byte[] buffer = new byte[2048];
                String readTraceId = traceId + "-rx";
                try {
                    while (isConnected() && currentSocket == socket && currentInputStream == inputStream) {
                        int length = currentInputStream.read(buffer);
                        if (length < 0) {
                            AppLogCenter.log(
                                    LogCategory.DEVICE,
                                    LogLevel.INFO,
                                    TAG,
                                    "real socket eof on " + currentConfig.getChannelName(),
                                    readTraceId
                            );
                            break;
                        }
                        if (length == 0) {
                            continue;
                        }

                        byte[] payload = new byte[length];
                        System.arraycopy(buffer, 0, payload, 0, length);
                        AppLogCenter.log(
                                LogCategory.PROTOCOL_RX,
                                LogLevel.DEBUG,
                                TAG,
                                "real socket recv on " + currentConfig.getChannelName() + ": " + Hexs.toHex(payload),
                                readTraceId
                        );
                        SocketReceiveListener listener = listeners.get(currentConfig.getChannelName());
                        if (listener != null) {
                            listener.onSocketReceive(currentConfig.getChannelName(), payload.clone());
                        }
                    }
                } catch (Exception e) {
                    if (currentSocket == socket) {
                        AppLogCenter.log(
                                LogCategory.ERROR,
                                LogLevel.WARN,
                                TAG,
                                "real socket recv failed on " + currentConfig.getChannelName() + ": " + e.getMessage(),
                                readTraceId
                        );
                    }
                } finally {
                    if (currentSocket == socket) {
                        closeQuietly();
                    }
                }
            }, "socket-rx-" + currentConfig.getChannelName());
            thread.setDaemon(true);
            readerThread = thread;
            thread.start();
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
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception ignore) {
                // ignore
            }
            readerThread = null;
            inputStream = null;
            outputStream = null;
            socket = null;
            endpointConfig = null;
        }
    }
}
