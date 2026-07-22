package com.lianhexinye.canport;

/* JADX INFO: loaded from: classes2.dex */
public class CANPort {
    public static native void closeCan();

    public static native int openCan(String str);

    public static native String receivedData();

    public static native int sendData(int[] iArr);

    static {
        System.loadLibrary("can-port");
    }
}
