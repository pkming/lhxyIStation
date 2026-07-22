package com.lhxy.istationdevice.android11.devicem90;

/**
 * 封装 V32 原生 GPS 串口库（libgps_serial_port.so）。
 * <p>
 * 对标 {@link M90NativeJhySerialPort}，只负责加载库与代理调用。
 * read() 返回的 String 由 {@link M90GpsNativeSerialPortAdapter} 转换为 byte[]。
 */
final class M90NativeGpsSerialPort {
    static final boolean LIBRARY_LOADED = loadLibrary();

    private static boolean loadLibrary() {
        try {
            System.loadLibrary("gps_serial_port");
            return true;
        } catch (Throwable ignore) {
            return false;
        }
    }

    private final com.lianhexinye.gpsserialport.GPSSerialPort delegate =
            new com.lianhexinye.gpsserialport.GPSSerialPort();

    int open(String portPath, int baudRate) {
        return delegate.open(portPath, baudRate);
    }

    /** 返回原始字符串；NMEA 语句全为 ASCII，用 ISO-8859-1 转 byte[] 无损。 */
    String read() {
        return delegate.read();
    }

    int write(String data) {
        return delegate.write(data);
    }

    int close() {
        return delegate.close();
    }
}
