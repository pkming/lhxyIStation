package com.lhxy.istationdevice.android11.deviceapi;

/**
 * 串口抽象接口
 */
public interface SerialPortAdapter {
    /**
     * 打开串口。
     */
    void open(SerialPortConfig config, String traceId);

    /**
     * 关闭串口。
     */
    void close(String portName, String traceId);

    /**
     * 判断串口是否已打开。
     */
    boolean isOpen(String portName);

    /**
     * 发送数据。
     */
    void send(String portName, byte[] payload, String traceId);

    /**
     * 绑定收包监听。
     */
    void setReceiveListener(String portName, SerialReceiveListener listener);

    /**
     * 移除收包监听。
     */
    void removeReceiveListener(String portName);
}
