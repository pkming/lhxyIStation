package com.lhxy.istationdevice.android11.deviceapi;

/**
 * JHY 客流专用串口抽象。
 */
public interface JhySerialPortAdapter {
    /**
     * 当前设备是否支持 JHY native 串口。
     */
    boolean isSupported();

    /**
     * 打开 JHY 串口并绑定收包监听。
     */
    void open(String portName, int baudRate, SerialReceiveListener listener, String traceId);

    /**
     * 关闭 JHY 串口。
     */
    void close(String portName, String traceId);

    /**
     * 当前端口是否已打开。
     */
    boolean isOpen(String portName);

    /**
     * 发送 JHY 报文。
     */
    void send(String portName, byte[] payload, String traceId);
}