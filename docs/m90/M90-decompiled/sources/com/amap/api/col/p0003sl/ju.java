package com.amap.api.col.p0003sl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.RejectedExecutionException;

/* JADX INFO: compiled from: Log.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ju {
    public static final String a = "/a/";
    static final String b = "b";
    static final String c = "c";
    static final String d = "d";
    public static String e = "s";
    public static final String f = "g";
    public static final String g = "h";
    public static final String h = "e";
    public static final String i = "f";
    public static final String j = "j";
    public static final String k = "k";
    private static long l;
    private static Vector<is> m = new Vector<>();

    public static String a(Context context, String str) {
        return context.getSharedPreferences("AMSKLG_CFG", 0).getString(str, "");
    }

    public static void a(Context context, String str, String str2) {
        SharedPreferences.Editor editorEdit = context.getSharedPreferences("AMSKLG_CFG", 0).edit();
        editorEdit.putString(str, str2);
        editorEdit.apply();
    }

    public static void b(Context context, String str) {
        SharedPreferences.Editor editorEdit = context.getSharedPreferences("AMSKLG_CFG", 0).edit();
        editorEdit.remove(str);
        editorEdit.apply();
    }

    public static String c(Context context, String str) {
        return context.getFilesDir().getAbsolutePath() + a + str;
    }

    public static void a(final Context context) {
        try {
            if (System.currentTimeMillis() - l < 60000) {
                return;
            }
            l = System.currentTimeMillis();
            mc.a().a(new md() { // from class: com.amap.api.col.3sl.ju.1
                @Override // com.amap.api.col.p0003sl.md
                public final void runTask() {
                    try {
                        jx.b(context);
                        jx.d(context);
                        jx.c(context);
                        ll.a(context);
                        lj.a(context);
                    } catch (RejectedExecutionException unused) {
                    } catch (Throwable th) {
                        jw.c(th, "Lg", "proL");
                    }
                }
            });
        } catch (Throwable th) {
            jw.c(th, "Lg", "proL");
        }
    }

    public static void a(is isVar) {
        try {
            synchronized (Looper.getMainLooper()) {
                if (isVar == null) {
                    return;
                }
                if (m.contains(isVar)) {
                    return;
                }
                m.add(isVar);
            }
        } catch (Throwable unused) {
        }
    }

    static List<is> a() {
        Vector<is> vector;
        try {
            synchronized (Looper.getMainLooper()) {
                vector = m;
            }
            return vector;
        } catch (Throwable th) {
            th.printStackTrace();
            return m;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x0035, code lost:
    
        r1 = r7.length;
        r2 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0037, code lost:
    
        if (r2 >= r1) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0043, code lost:
    
        if (b(r6, r7[r2].trim()) == false) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0045, code lost:
    
        return true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0046, code lost:
    
        r2 = r2 + 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static boolean a(java.lang.String[] r6, java.lang.String r7) {
        /*
            r0 = 0
            if (r6 == 0) goto L4d
            if (r7 != 0) goto L6
            goto L4d
        L6:
            java.lang.String r1 = "\n"
            java.lang.String[] r7 = r7.split(r1)     // Catch: java.lang.Throwable -> L49
            int r1 = r7.length     // Catch: java.lang.Throwable -> L49
            r2 = r0
        Le:
            r3 = 1
            if (r2 >= r1) goto L35
            r4 = r7[r2]     // Catch: java.lang.Throwable -> L49
            java.lang.String r4 = r4.trim()     // Catch: java.lang.Throwable -> L49
            boolean r5 = android.text.TextUtils.isEmpty(r4)     // Catch: java.lang.Throwable -> L49
            if (r5 != 0) goto L2e
            java.lang.String r5 = "at "
            boolean r5 = r4.startsWith(r5)     // Catch: java.lang.Throwable -> L49
            if (r5 == 0) goto L2e
            java.lang.String r5 = "uncaughtException"
            boolean r4 = r4.contains(r5)     // Catch: java.lang.Throwable -> L49
            if (r4 == 0) goto L2e
            goto L2f
        L2e:
            r3 = r0
        L2f:
            if (r3 == 0) goto L32
            return r0
        L32:
            int r2 = r2 + 1
            goto Le
        L35:
            int r1 = r7.length     // Catch: java.lang.Throwable -> L49
            r2 = r0
        L37:
            if (r2 >= r1) goto L4d
            r4 = r7[r2]     // Catch: java.lang.Throwable -> L49
            java.lang.String r4 = r4.trim()     // Catch: java.lang.Throwable -> L49
            boolean r4 = b(r6, r4)     // Catch: java.lang.Throwable -> L49
            if (r4 == 0) goto L46
            return r3
        L46:
            int r2 = r2 + 1
            goto L37
        L49:
            r6 = move-exception
            r6.printStackTrace()
        L4d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.ju.a(java.lang.String[], java.lang.String):boolean");
    }

    static boolean b(String[] strArr, String str) {
        if (strArr != null && str != null) {
            try {
                for (String str2 : strArr) {
                    str = str.trim();
                    if (str.startsWith("at ") && str.contains(str2 + ".") && str.endsWith(")") && !str.contains("uncaughtException")) {
                        return true;
                    }
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        return false;
    }
}
