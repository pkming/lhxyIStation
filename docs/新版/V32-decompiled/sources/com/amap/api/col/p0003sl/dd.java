package com.amap.api.col.p0003sl;

import android.content.Context;
import android.util.Log;

/* JADX INFO: compiled from: AuthLogUtil.java */
/* JADX INFO: loaded from: classes2.dex */
public final class dd {
    static String a;

    static {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 80; i++) {
            sb.append("=");
        }
        a = sb.toString();
    }

    public static void a() {
        c(a);
        c("当前使用的自定义地图样式文件和目前版本不匹配，请到官网(lbs.amap.com)更新新版样式文件");
        c(a);
    }

    public static void a(String str) {
        c(a);
        c(str);
        c(a);
    }

    public static void a(Context context, String str) {
        c(a);
        if (context != null) {
            b("key:" + ig.f(context));
        }
        c(str);
        c(a);
    }

    private static void b(String str) {
        if (str.length() < 78) {
            StringBuilder sb = new StringBuilder();
            sb.append("|").append(str);
            for (int i = 0; i < 78 - str.length(); i++) {
                sb.append(" ");
            }
            sb.append("|");
            c(sb.toString());
            return;
        }
        c("|" + str.substring(0, 78) + "|");
        b(str.substring(78));
    }

    private static void c(String str) {
        Log.i("authErrLog", str);
    }
}
