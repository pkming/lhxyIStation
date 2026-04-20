package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SocketEndpointConfig;
import com.lhxy.istationdevice.android11.deviceapi.SocketReceiveListener;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * M90 Socket stub 适配器
 * <p>
 * 真机 Socket 还没接之前，先用它把日志链路和调试页跑通。
 */
public final class M90StubSocketClientAdapter implements SocketClientAdapter {
    private static final String TAG = "M90StubSocket";
    private final Set<String> connectedChannels = ConcurrentHashMap.newKeySet();
    private final Map<String, SocketReceiveListener> listeners = new ConcurrentHashMap<>();

    /**
     * 建立 stub 连接。
     */
    @Override
    public void connect(SocketEndpointConfig config, String traceId) {
        connectedChannels.add(config.getChannelName());
        AppLogCenter.log(
                LogCategory.DEVICE,
                LogLevel.INFO,
                TAG,
                "stub connect " + config.getChannelName() + " -> " + config.getHost() + ":" + config.getPort(),
                traceId
        );
    }

    /**
     * 关闭 stub 连接。
     */
    @Override
    public void disconnect(String channelName, String traceId) {
        connectedChannels.remove(channelName);
        AppLogCenter.log(LogCategory.DEVICE, LogLevel.INFO, TAG, "stub disconnect " + channelName, traceId);
    }

    /**
     * 判断 stub 通道是否已连接。
     */
    @Override
    public boolean isConnected(String channelName) {
        return connectedChannels.contains(channelName);
    }

    /**
     * 发送 stub 数据。
     */
    @Override
    public void send(String channelName, byte[] payload, String traceId) {
        AppLogCenter.log(
                LogCategory.PROTOCOL_TX,
                LogLevel.DEBUG,
                TAG,
                "stub socket send on " + channelName + ": " + Hexs.toHex(payload),
                traceId
        );
        SocketReceiveListener listener = listeners.get(channelName);
        if (listener != null) {
            byte[] echoPayload = payload == null ? new byte[0] : payload.clone();
            AppLogCenter.log(
                    LogCategory.PROTOCOL_RX,
                    LogLevel.DEBUG,
                    TAG,
                    "stub socket recv echo on " + channelName + ": " + Hexs.toHex(echoPayload),
                    traceId + "-stub-rx"
            );
            listener.onSocketReceive(channelName, echoPayload);
        }
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
}
