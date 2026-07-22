package com.amap.api.col.p0003sl;

import android.content.Context;

/* JADX INFO: compiled from: OfflineLocEntity.java */
/* JADX INFO: loaded from: classes2.dex */
public final class li {
    private Context a;
    private is b;
    private String c;

    public li(Context context, is isVar, String str) {
        this.a = context.getApplicationContext();
        this.b = isVar;
        this.c = str;
    }

    final byte[] a() {
        return it.a(a(this.a, this.b, this.c));
    }

    private static String a(Context context, is isVar, String str) {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("\"sdkversion\":\"").append(isVar.c()).append("\",\"product\":\"").append(isVar.a()).append("\",\"nt\":\"").append(ik.c(context)).append("\",\"details\":").append(str);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return sb.toString();
    }
}
