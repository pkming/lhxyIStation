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

    private final com.lianhexinye.jhyserialport.JHYSerialPort delegate = new com.lianhexinye.jhyserialport.JHYSerialPort();

    int open(String portName, int baudRate) {
        return delegate.open(portName, baudRate);
    }

    byte[] read() {
        return delegate.read();
    }

    int write(byte[] payload) {
        return delegate.write(payload);
    }

    int close() {
        return delegate.close();
    }
}
