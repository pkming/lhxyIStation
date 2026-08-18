package com.amap.api.col.p0003sl;

import android.content.Context;
import java.lang.ref.WeakReference;

/* JADX INFO: compiled from: MarkInfoManager.java */
/* JADX INFO: loaded from: classes2.dex */
public class lh {
    static WeakReference<lf> a;

    public static void a(final String str, final Context context) {
        jw.d().submit(new Runnable() { // from class: com.amap.api.col.3sl.lh.1
            @Override // java.lang.Runnable
            public final void run() {
                synchronized (lh.class) {
                    try {
                        String strA = io.a(it.a(str));
                        lf lfVarA = lm.a(lh.a);
                        lm.a(context, lfVarA, ju.j, 50, 102400, "10");
                        if (lfVarA.e == null) {
                            lfVarA.e = new km(new kp(new ko()));
                        }
                        lg.a(strA, it.a(" \"timestamp\":\"" + it.a(System.currentTimeMillis(), "yyyyMMdd HH:mm:ss") + "\",\"details\":" + str), lfVarA);
                    } finally {
                    }
                }
            }
        });
    }
}
