package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: ThreadUtil.java */
/* JADX INFO: loaded from: classes2.dex */
public class dv {
    private static volatile dv b;
    private mc a;

    public static dv a() {
        if (b == null) {
            synchronized (dv.class) {
                if (b == null) {
                    b = new dv();
                }
            }
        }
        return b;
    }

    public static void b() {
        if (b != null) {
            try {
                if (b.a != null) {
                    b.a.e();
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
            b.a = null;
            b = null;
        }
    }

    private dv() {
        this.a = null;
        this.a = dw.a("AMapThreadUtil");
    }

    public final void a(md mdVar) {
        try {
            this.a.a(mdVar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void b(md mdVar) {
        if (mdVar != null) {
            try {
                mdVar.cancelTask();
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }
}
