package com.lhxy.istationdevice.android11.deviceapi;

/**
 * 串口配置
 */
public final class SerialPortConfig {
    private final String portName;
    private final int baudRate;
    private final SerialMode mode;

    /**
     * @param portName 串口设备名
     * @param baudRate 波特率
     */
    public SerialPortConfig(String portName, int baudRate) {
        this(portName, baudRate, SerialMode.STUB);
    }

    /**
     * @param portName 串口设备名
     * @param baudRate 波特率
     * @param mode 串口模式
     */
    public SerialPortConfig(String portName, int baudRate, SerialMode mode) {
        this.portName = portName;
        this.baudRate = baudRate;
        this.mode = mode == null ? SerialMode.STUB : mode;
    }

    /**
     * 串口设备名。
     */
    public String getPortName() {
        return portName;
    }

    /**
     * 波特率。
     */
    public int getBaudRate() {
        return baudRate;
    }

    /**
     * 串口模式。
     */
    public SerialMode getMode() {
        return mode;
    }
}
