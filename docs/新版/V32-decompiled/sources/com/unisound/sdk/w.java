package com.unisound.sdk;

import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public abstract class w extends Thread {
    private static boolean g = false;
    private static boolean h = false;
    protected ap b;
    protected cn.yunzhisheng.asr.a c;
    protected volatile boolean a = false;
    protected byte[] d = {100};
    private boolean e = false;
    private ArrayList<byte[]> f = new ArrayList<>();

    public w(cn.yunzhisheng.asr.a aVar, ap apVar) {
        this.b = null;
        this.c = null;
        this.c = aVar;
        this.b = apVar;
    }

    public w(cn.yunzhisheng.asr.a aVar, ap apVar, boolean z) {
        this.b = null;
        this.c = null;
        this.c = aVar;
        this.b = apVar;
    }

    public static void b(boolean z) {
        g = z;
    }

    public static void c(boolean z) {
        h = z;
    }

    public static boolean l() {
        return g;
    }

    public static boolean m() {
        return h;
    }

    protected void a(boolean z) {
        ap apVar = this.b;
        if (apVar != null) {
            apVar.b(z);
        }
    }

    protected void a(boolean z, byte[] bArr, int i, int i2) {
        ap apVar = this.b;
        if (apVar != null) {
            apVar.a(z, bArr, i, i2);
        }
    }

    protected void a(byte[] bArr) {
        this.f.add(bArr);
        int iV = this.c.v();
        int length = 0;
        for (int size = this.f.size() - 1; size >= 0; size--) {
            length += this.f.get(size).length;
            if (length >= iV) {
                this.e = true;
                return;
            }
        }
    }

    protected abstract boolean a();

    protected abstract void b();

    protected abstract byte[] c();

    public void d() {
        if (e()) {
            return;
        }
        com.unisound.common.r.c("InputSourceThread::stopRecording");
        this.a = true;
    }

    public boolean e() {
        return this.a;
    }

    public boolean f() {
        return this.b == null;
    }

    public void g() {
        if (f()) {
            return;
        }
        d();
        com.unisound.common.r.c("InputSourceThread::cancel");
        this.b = null;
    }

    protected void h() {
        ap apVar = this.b;
        if (apVar != null) {
            apVar.i();
        }
    }

    protected void i() {
        ap apVar = this.b;
        if (apVar != null) {
            apVar.j();
        }
    }

    public boolean j() {
        return this.a;
    }

    public void k() {
        g();
        if (isAlive()) {
            try {
                join(4000L);
                com.unisound.common.r.c("InputSourceThread::waitEnd()");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        com.unisound.common.r.g("Recording InputSource Thread start");
        boolean z = true;
        try {
            try {
                if (a()) {
                    a(true);
                    while (!e() && !f()) {
                        byte[] bArrC = c();
                        if (bArrC != null) {
                            if (!this.e) {
                                a(bArrC);
                            }
                            if (this.e) {
                                a(true, bArrC, 0, bArrC.length);
                            }
                        }
                    }
                } else {
                    a(false);
                }
                b();
                com.unisound.common.r.c("InputSourceThread::VAD destory");
                this.f.clear();
                z = false;
            } catch (Exception e) {
                e.printStackTrace();
                b();
                com.unisound.common.r.c("InputSourceThread::VAD destory");
                this.f.clear();
            }
            if (z) {
                i();
            } else {
                h();
                com.unisound.common.r.c("recording stopped");
            }
            com.unisound.common.r.g("Recording InputSource Thread stop");
        } catch (Throwable th) {
            b();
            com.unisound.common.r.c("InputSourceThread::VAD destory");
            this.f.clear();
            throw th;
        }
    }
}
