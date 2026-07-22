package com.lianhexinye.gpsserialport;

/* JADX INFO: loaded from: classes2.dex */
public class GPSSerialPort {
    public native int close();

    public native int open(String str, int i);

    public native String read();

    public native int write(String str);
}
