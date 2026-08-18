package com.amap.api.col.p0003sl;

import android.content.Context;

/* JADX INFO: compiled from: AdiuManager.java */
/* JADX INFO: loaded from: classes2.dex */
public class ji {
    private static ji a;
    private final Context b;
    private final String c = jp.a(it.c("RYW1hcF9kZXZpY2VfYWRpdQ"));

    private ji(Context context) {
        this.b = context.getApplicationContext();
    }

    public static ji a(Context context) {
        if (a == null) {
            synchronized (ji.class) {
                if (a == null) {
                    a = new ji(context);
                }
            }
        }
        return a;
    }

    public final synchronized void a() {
        try {
            if (ik.c() == null) {
                ik.a(jm.a());
            }
        } catch (Throwable unused) {
        }
    }

    public final void a(String str) {
        jj.a(this.b).a(this.c);
        jj.a(this.b).b(str);
    }
}
