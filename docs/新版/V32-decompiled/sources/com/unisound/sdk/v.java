package com.unisound.sdk;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public class v extends w {
    private static final int f = 50;
    private BlockingQueue<byte[]> e;

    public v(cn.yunzhisheng.asr.a aVar, ap apVar) {
        super(aVar, apVar, true);
        this.e = new LinkedBlockingQueue();
    }

    @Override // com.unisound.sdk.w
    protected boolean a() {
        this.e.clear();
        return true;
    }

    @Override // com.unisound.sdk.w
    protected void b() {
    }

    @Override // com.unisound.sdk.w
    protected byte[] c() throws InterruptedException {
        byte[] bArrPoll;
        do {
            bArrPoll = null;
            if (e() || f()) {
                break;
            }
            try {
                bArrPoll = this.e.poll(50L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (bArrPoll == null);
        return bArrPoll;
    }
}
