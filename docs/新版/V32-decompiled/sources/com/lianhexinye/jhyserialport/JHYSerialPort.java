package com.lianhexinye.jhyserialport;

/* JADX INFO: loaded from: classes2.dex */
public class JHYSerialPort {
    public native int close();

    public native int open(String str, int i);

    public native byte[] read();

    public native int write(byte[] bArr);
}
