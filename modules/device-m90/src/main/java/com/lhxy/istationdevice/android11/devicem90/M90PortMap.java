package com.lhxy.istationdevice.android11.devicem90;

/**
 * M90 串口口位说明
 */
public final class M90PortMap {
    public static final String KEYBOARD = "ttyS0";
    public static final String DEBUG = "ttyS2";
    public static final String RS232_1 = "ttyS3";
    public static final String RS232_2 = "ttyS4";
    public static final String GPS = "ttyS5";
    public static final String RS485_1 = "ttyS7";
    public static final String RS485_2 = "ttyS9";

    private M90PortMap() {
    }

    /**
     * 输出 M90 串口摘要。
     */
    public static String describe() {
        return "M90 串口预设：GPS=" + GPS
                + "，DVR/RS232-1=" + RS232_1
                + "，RS232-2=" + RS232_2
                + "，RS485-1=" + RS485_1
                + "，RS485-2=" + RS485_2;
    }
}
