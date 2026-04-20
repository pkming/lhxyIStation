package com.lhxy.istationdevice.android11.deviceapi;

/**
 * Socket 端点配置
 */
public final class SocketEndpointConfig {
    private final String channelName;
    private final String host;
    private final int port;
    private final SocketMode mode;

    /**
     * @param channelName 通道名
     * @param host 服务端地址
     * @param port 服务端端口
     * @param mode 发送模式
     */
    public SocketEndpointConfig(String channelName, String host, int port, SocketMode mode) {
        this.channelName = channelName;
        this.host = host;
        this.port = port;
        this.mode = mode == null ? SocketMode.STUB : mode;
    }

    /**
     * 通道名。
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * 服务端地址。
     */
    public String getHost() {
        return host;
    }

    /**
     * 服务端端口。
     */
    public int getPort() {
        return port;
    }

    /**
     * 发送模式。
     */
    public SocketMode getMode() {
        return mode;
    }
}
