package com.lianhexinye.iicport;

/* JADX INFO: loaded from: classes2.dex */
public class I2CPort {
    public native void RfidClose();

    public native byte[] RfidGetId(byte b);

    public native void RfidInit();

    public native void RfidSetID(byte b, byte[] bArr);

    public native void RfidWaitCardOff();

    static {
        System.loadLibrary("i2c-port");
    }
}
