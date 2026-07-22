package com.unisound.common;

import android.os.Process;

/* JADX INFO: loaded from: classes2.dex */
public class f extends Thread {
    protected int a = 0;

    public f(boolean z) {
        int i;
        if (z) {
            setPriority(10);
            i = -19;
        } else {
            setPriority(5);
            i = -16;
        }
        Process.setThreadPriority(i);
    }

    public void a(int i) {
        this.a = i;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
    }
}
