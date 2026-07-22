package com.wuxiaolong.androidutils.library;

import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
public class LogUtil {
    private static final boolean LOG = true;
    private static final String TAG = "wxl";

    public static void i(String str) {
        Log.i("wxl", str);
    }

    public static void i(String str, String str2) {
        Log.i(str, str2);
    }

    public static void d(String str) {
        Log.d("wxl", str);
    }

    public static void d(String str, String str2) {
        Log.d(str, str2);
    }

    public static void w(String str) {
        Log.w("wxl", str);
    }

    public static void w(String str, String str2) {
        Log.w(str, str2);
    }

    public static void v(String str) {
        Log.v("wxl", str);
    }

    public static void v(String str, String str2) {
        Log.v(str, str2);
    }

    public static void e(String str) {
        Log.e("wxl", str);
    }

    public static void e(String str, String str2) {
        Log.e(str, str2);
    }
}
