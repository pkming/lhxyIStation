package com.unisound.sdk;

import cn.yunzhisheng.asrfix.JniAsrFix;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public class i extends Thread {
    protected static long f = 30;
    protected u j;
    protected JniAsrFix k;
    protected String n;
    protected BlockingQueue<byte[]> g = new LinkedBlockingQueue();
    protected af h = null;
    protected ag i = null;
    protected boolean l = false;
    protected boolean m = false;

    public i(JniAsrFix jniAsrFix, String str, u uVar) {
        this.j = null;
        this.k = null;
        this.n = "";
        this.k = jniAsrFix;
        this.n = str;
        this.j = uVar;
    }

    protected void a(int i) {
        c("doRecognitionMaxSpeechTimeout=" + i);
        ag agVar = this.i;
        if (agVar != null) {
            agVar.a(i);
        }
    }

    protected void a(int i, int i2, Object obj) {
        ag agVar = this.i;
        if (agVar != null) {
            agVar.a(i, i2, obj);
        }
    }

    public void a(af afVar) {
        this.h = afVar;
    }

    public void a(ag agVar) {
        this.i = agVar;
    }

    protected void a(String str) {
        com.unisound.common.r.b("RecognitionThreadInterface:" + str);
    }

    protected void a(String str, boolean z) {
        c("doRecognitionResult partial=" + str);
        ag agVar = this.i;
        if (agVar != null) {
            agVar.a(str, true);
        }
    }

    protected void a(String str, boolean z, int i) {
        c("doRecognitionResult partial=" + str);
        ag agVar = this.i;
        if (agVar != null) {
            agVar.a(str, true, i);
        }
    }

    public void a(boolean z) {
        if (z) {
            c();
        }
        if (isAlive()) {
            try {
                join(39000L);
                com.unisound.common.r.c(getName() + "waitEnd()");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void a(byte[] bArr) {
        this.g.add(bArr);
    }

    protected boolean a() {
        return !this.g.isEmpty();
    }

    protected void b(String str) {
        com.unisound.common.r.e("RecognitionThreadInterface:" + str);
    }

    public void c() {
        if (k()) {
            return;
        }
        com.unisound.common.r.c("RecognitionThreadInterface::cancel");
        this.i = null;
        this.l = true;
    }

    protected void c(String str) {
        com.unisound.common.r.c("RecognitionThreadInterface:" + str);
    }

    public void d() {
        if (this.l) {
            return;
        }
        com.unisound.common.r.c("RecognitionThreadInterface::stopRecognition");
        this.l = true;
    }

    protected void e() {
    }

    public void f() {
        this.h = null;
        this.i = null;
    }

    public boolean g() {
        return this.l;
    }

    protected byte[] h() {
        return this.g.poll(f, TimeUnit.MILLISECONDS);
    }

    protected void i() {
        a("doRecognitionMaxSpeechTimeout");
        ag agVar = this.i;
        if (agVar != null) {
            agVar.l();
        }
    }

    protected void j() {
        c("onRecognitionVADTimeout");
        ag agVar = this.i;
        if (agVar != null) {
            agVar.k();
        }
    }

    protected boolean k() {
        JniAsrFix jniAsrFix = this.k;
        return (jniAsrFix != null && jniAsrFix.c()) || this.i == null;
    }

    protected void l() {
        ag agVar = this.i;
        if (agVar != null) {
            agVar.h();
        }
    }

    protected int m() {
        af afVar = this.h;
        if (afVar != null) {
            return afVar.a();
        }
        return 0;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
    }

    @Override // java.lang.Thread
    public void start() {
        super.start();
    }
}
