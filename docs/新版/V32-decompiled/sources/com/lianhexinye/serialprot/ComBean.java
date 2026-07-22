package com.lianhexinye.serialprot;

import java.text.SimpleDateFormat;
import java.util.Date;

/* JADX INFO: loaded from: classes2.dex */
public class ComBean {
    public byte[] bRec;
    public String sComPort;
    public String sRecTime;

    public ComBean(String str, byte[] bArr, int i) {
        this.bRec = null;
        this.sComPort = "";
        this.sRecTime = "";
        this.sComPort = str;
        this.bRec = new byte[i];
        for (int i2 = 0; i2 < i; i2++) {
            this.bRec[i2] = bArr[i2];
        }
        this.sRecTime = new SimpleDateFormat("hh:mm:ss").format(new Date());
    }

    public ComBean(byte[] bArr, int i) {
        this.bRec = null;
        this.sComPort = "";
        this.sRecTime = "";
        this.bRec = new byte[i];
        for (int i2 = 0; i2 < i; i2++) {
            this.bRec[i2] = bArr[i2];
        }
    }
}
