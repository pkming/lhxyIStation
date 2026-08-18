package com.unisound.sdk;

import android.os.Handler;
import android.os.Looper;
import android.view.Window;

/* JADX INFO: loaded from: classes2.dex */
public class ab extends Handler implements Runnable {
    private int a;
    private ac b;
    private boolean c;
    private boolean d;

    public ab(ac acVar, Looper looper) {
        super(looper);
        this.a = Window.PROGRESS_SECONDARY_END;
        this.c = false;
        this.d = false;
        this.b = acVar;
    }

    public int a() {
        return this.a;
    }

    public void a(int i) {
        this.a = i;
    }

    public boolean b() {
        return this.c;
    }

    public void c() {
        e();
        postDelayed(this, this.a);
        this.c = false;
        this.d = true;
        com.unisound.common.r.a("OnTimer:start");
    }

    public void d() {
        e();
    }

    public void e() {
        if (this.d) {
            removeCallbacks(this);
            this.d = false;
        }
        this.c = false;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.c = true;
        if (this.d) {
            this.b.a();
        }
    }
}
