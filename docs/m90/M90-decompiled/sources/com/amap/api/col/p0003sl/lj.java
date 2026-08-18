package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import java.lang.ref.WeakReference;

/* JADX INFO: compiled from: OfflineLocManager.java */
/* JADX INFO: loaded from: classes2.dex */
public class lj {
    static int a = 1000;
    static boolean b = false;
    static int c = 20;
    static int d = 0;
    private static WeakReference<lf> e = null;
    private static int f = 10;

    @Deprecated
    public static synchronized void a(int i, boolean z) {
        a = i;
        b = z;
    }

    public static synchronized void a(int i, boolean z, int i2, int i3) {
        a = i;
        b = z;
        if (i2 < 10 || i2 > 100) {
            i2 = 20;
        }
        c = i2;
        if (i2 / 5 > f) {
            f = i2 / 5;
        }
        d = i3;
    }

    /* JADX INFO: compiled from: OfflineLocManager.java */
    static class a extends md {
        private int a;
        private Context b;
        private li c;

        a(Context context, int i) {
            this.b = context;
            this.a = i;
        }

        a(Context context, li liVar) {
            this(context, 1);
            this.c = liVar;
        }

        @Override // com.amap.api.col.p0003sl.md
        public final void runTask() {
            int i = this.a;
            if (i == 1) {
                try {
                    synchronized (lj.class) {
                        String string = Long.toString(System.currentTimeMillis());
                        lf lfVarA = lm.a(lj.e);
                        lm.a(this.b, lfVarA, ju.i, lj.a, 2097152, "6");
                        if (lfVarA.e == null) {
                            lfVarA.e = new km(new ko(new kp(new ko())));
                        }
                        lg.a(string, this.c.a(), lfVarA);
                    }
                    return;
                } catch (Throwable th) {
                    jw.c(th, "ofm", "aple");
                    return;
                }
            }
            if (i == 2) {
                try {
                    lf lfVarA2 = lm.a(lj.e);
                    lm.a(this.b, lfVarA2, ju.i, lj.a, 2097152, "6");
                    lfVarA2.h = 14400000;
                    if (lfVarA2.g == null) {
                        lfVarA2.g = new lq(new lp(this.b, new lu(), new km(new ko(new kp())), new String(jh.a()), ig.f(this.b), ik.k(), ik.h(), ik.f(this.b), ik.a(), Build.MANUFACTURER, Build.DEVICE, ik.n(), ig.c(this.b), Build.MODEL, ig.d(this.b), ig.b(this.b), ik.e(this.b), ik.a(this.b), String.valueOf(Build.VERSION.SDK_INT), iy.a(this.b).a()));
                    }
                    if (TextUtils.isEmpty(lfVarA2.i)) {
                        lfVarA2.i = "fKey";
                    }
                    lfVarA2.f = new ly(this.b, lfVarA2.h, lfVarA2.i, new lw(this.b, lj.b, lj.f * 1024, lj.c * 1024, "offLocKey", lj.d * 1024));
                    lg.a(lfVarA2);
                } catch (Throwable th2) {
                    jw.c(th2, "ofm", "uold");
                }
            }
        }
    }

    public static synchronized void a(li liVar, Context context) {
        mc.a().a(new a(context, liVar));
    }

    public static void a(Context context) {
        mc.a().a(new a(context, 2));
    }
}
