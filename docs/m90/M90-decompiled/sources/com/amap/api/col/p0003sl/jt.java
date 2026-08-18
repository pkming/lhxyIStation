package com.amap.api.col.p0003sl;

import java.lang.Thread;

/* JADX INFO: compiled from: BasicLogHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public class jt {
    protected static jt a;
    protected Thread.UncaughtExceptionHandler b;
    protected boolean c = true;

    protected void a() {
    }

    protected void a(is isVar, String str, String str2) {
    }

    protected void a(is isVar, boolean z) {
    }

    protected void a(Throwable th, int i, String str, String str2) {
    }

    public static void a(Throwable th, String str, String str2) {
        jt jtVar = a;
        if (jtVar != null) {
            jtVar.a(th, 1, str, str2);
        }
    }
}
