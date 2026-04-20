package com.lhxy.istationdevice.android11.deviceapi;

/**
 * Socket 抽象接口
 */
public interface SocketClientAdapter {
    /**
     * 建立连接。
     */
    void connect(SocketEndpointConfig config, String traceId);

    /**
     * 关闭连接。
     */
    void disconnect(String channelName, String traceId);

    /**
     * 判断通道是否已连接。
     */
    boolean isConnected(String channelName);

    /**
     * 发送数据。
     */
    void send(String channelName, byte[] payload, String traceId);

    /**
     * 设置通道收包监听。
     */
    void setReceiveListener(String channelName, SocketReceiveListener listener);

    /**
     * 移除通道收包监听。
     */
    void removeReceiveListener(String channelName);
}
