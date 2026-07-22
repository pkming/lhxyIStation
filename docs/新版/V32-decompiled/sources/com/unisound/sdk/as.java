package com.unisound.sdk;

import com.amap.api.services.core.AMapException;
import com.unisound.client.IAudioSource;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class as extends w {
    protected static as e = null;
    protected static final int f = 2400;
    private static Object g = new Object();
    private IAudioSource h;
    private byte[] i;

    public as(cn.yunzhisheng.asr.a aVar, ap apVar, IAudioSource iAudioSource) {
        super(aVar, apVar);
        this.h = null;
        this.h = iAudioSource;
        e = this;
    }

    public static void n() {
        as asVar = e;
        if (asVar != null) {
            asVar.k();
        }
    }

    @Override // com.unisound.sdk.w
    protected boolean a() {
        this.i = new byte[AMapException.CODE_AMAP_SERVICE_INVALID_PARAMS];
        if (this.h.openAudioIn() != 0) {
            return false;
        }
        b(true);
        c(true);
        return true;
    }

    @Override // com.unisound.sdk.w
    protected void b() {
        synchronized (g) {
            b(false);
            this.h.closeAudioIn();
            this.i = null;
            if (e == this) {
                e = null;
            }
        }
    }

    @Override // com.unisound.sdk.w
    protected byte[] c() {
        com.unisound.common.r.f("Record Read     ");
        IAudioSource iAudioSource = this.h;
        byte[] bArr = this.i;
        int data = iAudioSource.readData(bArr, bArr.length);
        if (data > 0) {
            return Arrays.copyOfRange(this.i, 0, data);
        }
        if (data == -9) {
            this.a = true;
            d();
            com.unisound.common.r.c("RecordingThread", "stop signal received");
            return Arrays.copyOfRange(this.d, 0, 1);
        }
        if (data >= 0) {
            return null;
        }
        this.a = true;
        d();
        i();
        return Arrays.copyOfRange(this.d, 0, 1);
    }
}
