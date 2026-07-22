package com.lianhexinye.gpsserialport;

/**
 * V32 原生 GPS 串口库 JNI 接口（对齐 libgps_serial_port.so）。
 * <p>
 * 包名与类名必须与 V32 完全一致，否则 JNI 函数名解析失败。
 */
public class GPSSerialPort {
    public native int close();

    public native int open(String str, int i);

    public native String read();

    public native int write(String str);
}
