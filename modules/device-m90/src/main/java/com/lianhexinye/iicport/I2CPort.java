package com.lianhexinye.iicport;

/**
 * JNI entrypoint name must match the vendor libi2c-port.so exports.
 */
public final class I2CPort {
    public native void RfidClose();

    public native byte[] RfidGetId(byte channel);

    public native void RfidInit();

    public native void RfidSetID(byte channel, byte[] value);

    public native void RfidWaitCardOff();
}
