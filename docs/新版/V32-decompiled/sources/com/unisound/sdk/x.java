package com.unisound.sdk;

import android.content.Context;
import cn.yunzhisheng.asr.VAD;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public class x extends Thread {
    protected static long g = 30;
    protected volatile boolean a;
    protected ap b;
    protected cn.yunzhisheng.asr.a c;
    protected VAD d;
    protected BlockingQueue<byte[]> e;
    byte[] f;
    private Context h;

    public x(Context context, cn.yunzhisheng.asr.a aVar, ap apVar) {
        this.a = false;
        this.b = null;
        this.c = null;
        this.d = null;
        this.e = new LinkedBlockingQueue();
        this.h = context;
        this.c = aVar;
        this.b = apVar;
        com.unisound.common.r.c("InputVadThread::VAD new");
        VAD vad = new VAD(aVar, apVar);
        this.d = vad;
        vad.b();
    }

    public x(Context context, cn.yunzhisheng.asr.a aVar, ap apVar, boolean z) {
        this.a = false;
        this.b = null;
        this.c = null;
        this.d = null;
        this.e = new LinkedBlockingQueue();
        this.h = context;
        this.c = aVar;
        this.b = apVar;
    }

    public void a(boolean z) {
        this.d.l = z;
    }

    public void a(byte[] bArr) {
        this.e.add(bArr);
    }

    public boolean a() {
        return this.d.l;
    }

    public void b() {
        if (c()) {
            return;
        }
        com.unisound.common.r.c("InputVadThread::stopVad");
        this.a = true;
    }

    public boolean c() {
        return this.a;
    }

    public boolean d() {
        return this.b == null;
    }

    public void e() {
        if (d()) {
            return;
        }
        b();
        com.unisound.common.r.c("InputVadThread::cancel");
        this.e.clear();
        this.b = null;
    }

    public boolean f() {
        return this.a;
    }

    public void g() {
        e();
        if (isAlive()) {
            try {
                join(4000L);
                com.unisound.common.r.c("InputVadThread::waitEnd()");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        int iJ;
        com.unisound.common.r.g("InputVadThread start");
        while (true) {
            try {
                try {
                    if ((c() && this.e.isEmpty()) || d()) {
                        break;
                    }
                    if (this.c.w() && !a()) {
                        Thread.sleep(1L);
                    } else if (this.c.y() && this.c.z() && this.d.h.size() > 0) {
                        if (this.c.N() == 0) {
                            iJ = this.c.aj - this.c.K();
                            cn.yunzhisheng.asr.a aVar = this.c;
                            aVar.l(aVar.aj);
                        } else {
                            iJ = this.c.J();
                            this.c.k(0);
                        }
                        if (iJ < 0) {
                            byte[] bArrA = com.unisound.common.i.a(this.h);
                            this.d.a(true, bArrA, 0, bArrA.length);
                        } else if (iJ > 0) {
                            this.d.b(iJ);
                        }
                        if (this.d.h.size() > 0) {
                            byte[] bArrRemove = this.d.h.remove(0);
                            this.d.a(true, bArrRemove, 0, bArrRemove.length);
                        }
                    } else if (this.c.z() || this.d.g.size() <= 0 || !this.d.i) {
                        byte[] bArrPoll = this.e.poll(g, TimeUnit.MILLISECONDS);
                        this.f = bArrPoll;
                        if (bArrPoll == null) {
                            Thread.sleep(20L);
                        } else {
                            if (bArrPoll.length == 1 && bArrPoll[0] == 100) {
                                e();
                                break;
                            }
                            this.d.a(bArrPoll, 0, bArrPoll.length);
                        }
                    } else {
                        byte[] bArrRemove2 = this.d.g.remove(0);
                        VAD vad = this.d;
                        vad.a(vad.i, bArrRemove2, 0, bArrRemove2.length);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Throwable th) {
                com.unisound.common.r.c("InputVadThread::VAD destory");
                this.d.d();
                this.e.clear();
                throw th;
            }
        }
        if (this.b != null) {
            this.d.e();
        }
        com.unisound.common.r.c("InputVadThread::VAD destory");
        this.d.d();
        this.e.clear();
        com.unisound.common.r.g("InputVadThread stop");
    }
}
