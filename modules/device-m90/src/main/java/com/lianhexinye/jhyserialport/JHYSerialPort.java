package com.lianhexinye.jhyserialport;

/**
 * JNI entrypoint name must match the vendor libjhy_serial_port.so exports.
 */
public final class JHYSerialPort {
    public native int close();

    public native int open(String portName, int baudRate);

    public native byte[] read();

    public native int write(byte[] payload);
}
