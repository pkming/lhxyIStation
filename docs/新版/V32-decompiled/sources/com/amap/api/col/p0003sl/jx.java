package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Build;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: compiled from: ErrorLogManager.java */
/* JADX INFO: loaded from: classes2.dex */
public class jx {
    private static WeakReference<lf> a = null;
    private static boolean b = true;
    private static WeakReference<lz> c = null;
    private static WeakReference<lz> d = null;
    private static String[] e = new String[10];
    private static int f = 0;
    private static boolean g = false;
    private static int h;
    private static is i;

    /* JADX INFO: compiled from: ErrorLogManager.java */
    public interface a {
        void a(int i);
    }

    private static boolean a(is isVar) {
        return isVar != null && isVar.e();
    }

    private static void a(Context context, is isVar, int i2, String str, String str2) {
        String str3;
        String strA = lm.a();
        String strA2 = lm.a(context, isVar);
        ig.a(context);
        String strA3 = lm.a(strA2, strA, i2, str, str2);
        if (strA3 == null || "".equals(strA3)) {
            return;
        }
        String strC = io.c(str2);
        if (i2 == 1) {
            str3 = ju.b;
        } else if (i2 == 2) {
            str3 = ju.d;
        } else if (i2 != 0) {
            return;
        } else {
            str3 = ju.c;
        }
        String str4 = str3;
        lf lfVarA = lm.a(a);
        lm.a(context, lfVarA, str4, 1000, 4096000, "1");
        if (lfVarA.e == null) {
            lfVarA.e = new kl(new km(new ko(new kp())));
        }
        try {
            lg.a(strC, it.a(strA3.replaceAll("\n", "<br/>")), lfVarA);
        } catch (Throwable unused) {
        }
    }

    static void a(Context context) {
        String strA;
        is isVar;
        List<is> listA = ju.a();
        if (listA == null || listA.size() == 0 || (strA = a(listA)) == null || "".equals(strA) || (isVar = i) == null) {
            return;
        }
        a(context, isVar, 2, "ANR", strA);
    }

    public static void a(Context context, is isVar, String str, int i2, String str2, String str3) {
        if (str2 == null || "".equals(str2)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (str2 != null) {
            sb.append("class:").append(str2);
        }
        if (str3 != null) {
            sb.append(" method:").append(str3).append("$<br/>");
        }
        sb.append(str);
        a(context, isVar, i2, str2, sb.toString());
    }

    public static void a(Context context, Throwable th, int i2, String str, String str2) {
        String strA = it.a(th);
        is isVarA = a(strA);
        if (a(isVarA)) {
            String strReplaceAll = strA.replaceAll("\n", "<br/>");
            String string = th.toString();
            if (string == null || "".equals(string)) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            if (str != null) {
                sb.append("class:").append(str);
            }
            if (str2 != null) {
                sb.append(" method:").append(str2).append("$<br/>");
            }
            sb.append(strReplaceAll);
            a(context, isVarA, i2, string, sb.toString());
        }
    }

    static void a(is isVar, Context context, String str, String str2) {
        if (!a(isVar) || str == null || "".equals(str)) {
            return;
        }
        a(context, isVar, 1, str, str2);
    }

    public static void a(final Context context, final is isVar, final String str, final lf lfVar, final String str2) {
        try {
            mc.a().a(new md() { // from class: com.amap.api.col.3sl.jx.1
                @Override // com.amap.api.col.p0003sl.md
                public final void runTask() {
                    String strA = jx.a(context, isVar, str, str2);
                    if (lfVar.e == null) {
                        lfVar.e = new kl(new km(new ko(new kp())));
                    }
                    try {
                        lg.a(io.c(strA), it.a(strA), lfVar);
                    } catch (Throwable unused) {
                    }
                }
            });
        } catch (Throwable unused) {
        }
    }

    static void b(Context context) {
        lx lxVar = new lx(b);
        b = false;
        a(context, lxVar, ju.c);
    }

    static void c(Context context) {
        WeakReference<lz> weakReference = c;
        if (weakReference == null || weakReference.get() == null) {
            c = new WeakReference<>(new ly(context, 3600000, "hKey", new ma(context, false)));
        }
        a(context, c.get(), ju.d);
    }

    static void d(Context context) {
        WeakReference<lz> weakReference = d;
        if (weakReference == null || weakReference.get() == null) {
            d = new WeakReference<>(new ly(context, 3600000, "gKey", new ma(context, false)));
        }
        a(context, d.get(), ju.b);
    }

    private static void a(final Context context, final lz lzVar, final String str) {
        mc.a().a(new md() { // from class: com.amap.api.col.3sl.jx.2
            @Override // com.amap.api.col.p0003sl.md
            public final void runTask() {
                try {
                    synchronized (jx.class) {
                        lf lfVarA = lm.a(jx.a);
                        lm.a(context, lfVarA, str, 1000, 4096000, "1");
                        lfVarA.f = lzVar;
                        if (lfVarA.g == null) {
                            lfVarA.g = new lq(new lp(context, new lu(), new km(new ko(new kp())), "QImtleSI6IiVzIiwicGxhdGZvcm0iOiJhbmRyb2lkIiwiZGl1IjoiJXMiLCJhZGl1IjoiJXMiLCJwa2ciOiIlcyIsIm1vZGVsIjoiJXMiLCJhcHBuYW1lIjoiJXMiLCJhcHB2ZXJzaW9uIjoiJXMiLCJzeXN2ZXJzaW9uIjoiJXMi", ig.f(context), ik.k(), ik.p(context), ig.c(context), Build.MODEL, ig.b(context), ig.d(context), Build.VERSION.RELEASE));
                        }
                        lfVarA.h = 3600000;
                        lg.a(lfVarA);
                    }
                } catch (Throwable th) {
                    jw.c(th, "lg", "pul");
                }
            }
        });
    }

    public static void a(final Context context, final lf lfVar, final a aVar) {
        try {
            mc.a().a(new md() { // from class: com.amap.api.col.3sl.jx.3
                @Override // com.amap.api.col.p0003sl.md
                public final void runTask() {
                    try {
                        synchronized (jx.class) {
                            if (lfVar.g == null) {
                                lfVar.g = new lq(new lp(context, new lu(), new km(new ko(new kp())), "QImtleSI6IiVzIiwicGxhdGZvcm0iOiJhbmRyb2lkIiwiZGl1IjoiJXMiLCJhZGl1IjoiJXMiLCJwa2ciOiIlcyIsIm1vZGVsIjoiJXMiLCJhcHBuYW1lIjoiJXMiLCJhcHB2ZXJzaW9uIjoiJXMiLCJzeXN2ZXJzaW9uIjoiJXMi", ig.f(context), ik.k(), ik.p(context), ig.c(context), Build.MODEL, ig.b(context), ig.d(context), Build.VERSION.RELEASE));
                            }
                            int iA = lg.a(lfVar);
                            a aVar2 = aVar;
                            if (aVar2 != null) {
                                aVar2.a(iA);
                            }
                        }
                    } catch (Throwable th) {
                        jw.c(th, "lg", "pul");
                    }
                }
            });
        } catch (Throwable unused) {
        }
    }

    private static is a(String str) {
        List<is> listA = ju.a();
        if (listA == null) {
            listA = new ArrayList();
        }
        if (str != null && !"".equals(str)) {
            for (is isVar : listA) {
                if (ju.a(isVar.f(), str)) {
                    return isVar;
                }
            }
            if (str.contains("com.amap.api.col")) {
                try {
                    return it.a();
                } catch (Cif e2) {
                    e2.printStackTrace();
                }
            }
            if (str.contains("com.amap.co") || str.contains("com.amap.opensdk.co") || str.contains("com.amap.location")) {
                try {
                    is isVarB = it.b();
                    isVarB.a(true);
                    return isVarB;
                } catch (Cif e3) {
                    e3.printStackTrace();
                }
            }
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:90:0x00fa  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x00ff A[RETURN] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:87:0x00f3 -> B:107:0x00f6). Please report as a decompilation issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.String a(java.util.List<com.amap.api.col.p0003sl.is> r11) {
        /*
            Method dump skipped, instruction units count: 256
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.jx.a(java.util.List):java.lang.String");
    }

    private static String b() {
        StringBuilder sb = new StringBuilder();
        try {
            for (int i2 = f; i2 < 10 && i2 <= 9; i2++) {
                sb.append(e[i2]);
            }
            for (int i3 = 0; i3 < f; i3++) {
                sb.append(e[i3]);
            }
        } catch (Throwable th) {
            jw.c(th, "alg", "gLI");
        }
        return sb.toString();
    }

    static /* synthetic */ String a(Context context, is isVar, String str, String str2) {
        String strA = lm.a();
        String strA2 = lm.a(context, isVar);
        ig.a(context);
        return lm.a(strA2, strA, str, str2);
    }
}
