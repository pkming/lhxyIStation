package com.lhxy.istationdevice.android11.devicem90;

import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SocketEndpointConfig;
import com.lhxy.istationdevice.android11.deviceapi.SocketMode;
import com.lhxy.istationdevice.android11.deviceapi.SocketReceiveListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * M90 Socket 管理适配器
 * <p>
 * 根据配置里的 mode 决定走 stub 还是真连接。
 */
public final class M90ManagedSocketClientAdapter implements SocketClientAdapter {
    private final M90StubSocketClientAdapter stubAdapter = new M90StubSocketClientAdapter();
    private final M90RealSocketClientAdapter realAdapter = new M90RealSocketClientAdapter();
    private final Map<String, SocketMode> channelModes = new ConcurrentHashMap<>();

    /**
     * 建立连接。
     */
    @Override
    public void connect(SocketEndpointConfig config, String traceId) {
        channelModes.put(config.getChannelName(), config.getMode());
        delegate(config.getMode()).connect(config, traceId);
    }

    /**
     * 关闭连接。
     */
    @Override
    public void disconnect(String channelName, String traceId) {
        SocketMode mode = channelModes.remove(channelName);
        if (mode == null) {
            stubAdapter.disconnect(channelName, traceId);
            realAdapter.disconnect(channelName, traceId);
            return;
        }
        delegate(mode).disconnect(channelName, traceId);
    }

    /**
     * 判断连接状态。
     */
    @Override
    public boolean isConnected(String channelName) {
        SocketMode mode = channelModes.get(channelName);
        if (mode == null) {
            return stubAdapter.isConnected(channelName) || realAdapter.isConnected(channelName);
        }
        return delegate(mode).isConnected(channelName);
    }

    /**
     * 发送数据。
     */
    @Override
    public void send(String channelName, byte[] payload, String traceId) {
        SocketMode mode = channelModes.get(channelName);
        delegate(mode == null ? SocketMode.STUB : mode).send(channelName, payload, traceId);
    }

    @Override
    public void setReceiveListener(String channelName, SocketReceiveListener listener) {
        stubAdapter.setReceiveListener(channelName, listener);
        realAdapter.setReceiveListener(channelName, listener);
    }

    @Override
    public void removeReceiveListener(String channelName) {
        stubAdapter.removeReceiveListener(channelName);
        realAdapter.removeReceiveListener(channelName);
    }

    private SocketClientAdapter delegate(SocketMode mode) {
        return mode == SocketMode.REAL ? realAdapter : stubAdapter;
    }
}
