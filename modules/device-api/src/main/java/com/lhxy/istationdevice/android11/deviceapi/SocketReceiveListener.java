package com.lhxy.istationdevice.android11.deviceapi;

/**
 * Socket 收包监听
 * <p>
 * 统一给 JT808 / AL808 调度通道挂收包解析器，避免把回包处理继续写进设备适配器。
 */
public interface SocketReceiveListener {
    /**
     * 收到一段原始字节。
     *
     * @param channelName 当前通道名
     * @param payload     原始字节
     */
    void onSocketReceive(String channelName, byte[] payload);
}
