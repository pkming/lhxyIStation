package com.lhxy.istationdevice.android11.devicem90;

final class M90NativeJhySerialPort {
    static final boolean LIBRARY_LOADED = loadLibrary();

    private static boolean loadLibrary() {
        try {
            System.loadLibrary("jhy_serial_port");
            return true;
        } catch (Throwable ignore) {
            return false;
        }
    }

    native int open(String portName, int baudRate);

    native byte[] read();

    native int write(byte[] payload);

    native int close();
}