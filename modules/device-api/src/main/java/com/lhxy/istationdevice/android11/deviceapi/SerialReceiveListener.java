package com.lhxy.istationdevice.android11.deviceapi;

/**
 * 串口收包监听器
 */
public interface SerialReceiveListener {
    /**
     * 收到一段串口原始字节。
     */
    void onReceive(String portName, byte[] payload);
}
