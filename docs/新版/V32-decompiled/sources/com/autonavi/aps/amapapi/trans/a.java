package com.autonavi.aps.amapapi.trans;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.KeyChain;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.ih;
import com.autonavi.aps.amapapi.utils.i;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;

/* JADX INFO: compiled from: AMapDnsManager.java */
/* JADX INFO: loaded from: classes2.dex */
public final class a {
    public static int a = 1;
    public static int b = 2;
    private static a e;
    private Context j;
    private String k;
    private long c = 0;
    private boolean d = false;
    private ArrayList<String> f = new ArrayList<>();
    private com.autonavi.aps.amapapi.d g = new com.autonavi.aps.amapapi.d();
    private com.autonavi.aps.amapapi.d h = new com.autonavi.aps.amapapi.d();
    private long i = 120000;
    private boolean l = false;

    public static synchronized a a(Context context) {
        if (e == null) {
            e = new a(context);
        }
        return e;
    }

    private a(Context context) {
        this.j = context;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public com.autonavi.aps.amapapi.d b(int i) {
        if (i == b) {
            return this.h;
        }
        return this.g;
    }

    private static String c(int i) {
        return i == b ? "last_ip_6" : "last_ip_4";
    }

    public final String a(d dVar, int i) {
        try {
            if (com.autonavi.aps.amapapi.utils.a.q() && dVar != null) {
                String url = dVar.getURL();
                String host = new URL(url).getHost();
                if (!"http://abroad.apilocate.amap.com/mobile/binary".equals(url) && !"abroad.apilocate.amap.com".equals(host)) {
                    String str = "apilocate.amap.com".equalsIgnoreCase(host) ? "httpdns.apilocate.amap.com" : host;
                    if (!ih.g(str)) {
                        return null;
                    }
                    String strE = e(i);
                    if (!TextUtils.isEmpty(strE)) {
                        dVar.c(url.replace(host, strE));
                        dVar.getRequestHead().put(KeyChain.EXTRA_HOST, str);
                        dVar.d(str);
                        dVar.setIPV6Request(i == b);
                        return strE;
                    }
                }
            }
        } catch (Throwable unused) {
        }
        return null;
    }

    public final void a(int i) {
        if (!b(i).e()) {
            this.f.add(b(i).b());
            d(i);
            b(true, i);
            return;
        }
        d(i);
    }

    public final void a(boolean z, int i) {
        b(i).b(z);
        if (z) {
            String strC = b(i).c();
            String strB = b(i).b();
            if (TextUtils.isEmpty(strB) || strB.equals(strC)) {
                return;
            }
            SharedPreferences.Editor editorA = i.a(this.j, "cbG9jaXA");
            i.a(editorA, c(i), strB);
            i.a(editorA);
        }
    }

    private void d(int i) {
        if (b(i).d()) {
            SharedPreferences.Editor editorA = i.a(this.j, "cbG9jaXA");
            i.a(editorA, c(i));
            i.a(editorA);
            b(i).a(false);
        }
    }

    private String e(int i) {
        String str;
        int i2 = 0;
        b(false, i);
        String[] strArrA = b(i).a();
        if (strArrA != null && strArrA.length > 0) {
            int length = strArrA.length;
            while (true) {
                if (i2 >= length) {
                    str = null;
                    break;
                }
                str = strArrA[i2];
                if (!this.f.contains(str)) {
                    break;
                }
                i2++;
            }
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            b(i).a(str);
            return str;
        }
        g(i);
        return b(i).b();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void f(int i) {
        if (b(i).a() == null || b(i).a().length <= 0) {
            return;
        }
        String str = b(i).a()[0];
        if (str.equals(this.k) || this.f.contains(str)) {
            return;
        }
        this.k = str;
        SharedPreferences.Editor editorA = i.a(this.j, "cbG9jaXA");
        i.a(editorA, c(i), str);
        i.a(editorA);
    }

    private void g(int i) {
        String strA = i.a(this.j, "cbG9jaXA", c(i), (String) null);
        if (TextUtils.isEmpty(strA) || this.f.contains(strA)) {
            return;
        }
        b(i).a(strA);
        b(i).b(strA);
        b(i).a(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean b(String[] strArr, String[] strArr2) {
        if (strArr == null || strArr.length == 0 || strArr2 == null || strArr2.length == 0 || strArr.length != strArr2.length) {
            return false;
        }
        int length = strArr.length;
        for (int i = 0; i < length; i++) {
            if (!strArr[i].equals(strArr2[i])) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0017 A[Catch: all -> 0x0094, TRY_LEAVE, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x0009, B:10:0x000f, B:12:0x0017, B:21:0x0031, B:23:0x004b, B:24:0x0086), top: B:30:0x0003 }] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x004b A[Catch: all -> 0x0094, LOOP:0: B:22:0x0049->B:23:0x004b, LOOP_END, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x0009, B:10:0x000f, B:12:0x0017, B:21:0x0031, B:23:0x004b, B:24:0x0086), top: B:30:0x0003 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private synchronized void b(boolean r9, final int r10) {
        /*
            r8 = this;
            monitor-enter(r8)
            if (r9 != 0) goto Lf
            boolean r9 = com.autonavi.aps.amapapi.utils.a.p()     // Catch: java.lang.Throwable -> L94
            if (r9 != 0) goto Lf
            boolean r9 = r8.l     // Catch: java.lang.Throwable -> L94
            if (r9 == 0) goto Lf
            monitor-exit(r8)
            return
        Lf:
            long r0 = r8.c     // Catch: java.lang.Throwable -> L94
            r2 = 0
            int r9 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r9 == 0) goto L31
            long r0 = java.lang.System.currentTimeMillis()     // Catch: java.lang.Throwable -> L94
            long r2 = r8.c     // Catch: java.lang.Throwable -> L94
            long r4 = r0 - r2
            long r6 = r8.i     // Catch: java.lang.Throwable -> L94
            int r9 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r9 >= 0) goto L27
            monitor-exit(r8)
            return
        L27:
            long r0 = r0 - r2
            r2 = 60000(0xea60, double:2.9644E-319)
            int r9 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r9 >= 0) goto L31
            monitor-exit(r8)
            return
        L31:
            long r0 = java.lang.System.currentTimeMillis()     // Catch: java.lang.Throwable -> L94
            r8.c = r0     // Catch: java.lang.Throwable -> L94
            r9 = 1
            r8.l = r9     // Catch: java.lang.Throwable -> L94
            java.lang.Thread r9 = java.lang.Thread.currentThread()     // Catch: java.lang.Throwable -> L94
            java.lang.StackTraceElement[] r9 = r9.getStackTrace()     // Catch: java.lang.Throwable -> L94
            java.lang.StringBuffer r0 = new java.lang.StringBuffer     // Catch: java.lang.Throwable -> L94
            r0.<init>()     // Catch: java.lang.Throwable -> L94
            int r1 = r9.length     // Catch: java.lang.Throwable -> L94
            r2 = 0
        L49:
            if (r2 >= r1) goto L86
            r3 = r9[r2]     // Catch: java.lang.Throwable -> L94
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L94
            r4.<init>()     // Catch: java.lang.Throwable -> L94
            java.lang.String r5 = r3.getClassName()     // Catch: java.lang.Throwable -> L94
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch: java.lang.Throwable -> L94
            java.lang.String r5 = "("
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch: java.lang.Throwable -> L94
            java.lang.String r5 = r3.getMethodName()     // Catch: java.lang.Throwable -> L94
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch: java.lang.Throwable -> L94
            java.lang.String r5 = ":"
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch: java.lang.Throwable -> L94
            int r3 = r3.getLineNumber()     // Catch: java.lang.Throwable -> L94
            java.lang.StringBuilder r3 = r4.append(r3)     // Catch: java.lang.Throwable -> L94
            java.lang.String r4 = "),"
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.Throwable -> L94
            java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L94
            r0.append(r3)     // Catch: java.lang.Throwable -> L94
            int r2 = r2 + 1
            goto L49
        L86:
            com.amap.api.col.3sl.mc r9 = com.amap.api.col.p0003sl.mc.a()     // Catch: java.lang.Throwable -> L94
            com.autonavi.aps.amapapi.trans.a$1 r0 = new com.autonavi.aps.amapapi.trans.a$1     // Catch: java.lang.Throwable -> L94
            r0.<init>()     // Catch: java.lang.Throwable -> L94
            r9.a(r0)     // Catch: java.lang.Throwable -> L94
            monitor-exit(r8)
            return
        L94:
            r9 = move-exception
            monitor-exit(r8)
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.autonavi.aps.amapapi.trans.a.b(boolean, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String[] b(JSONArray jSONArray, int i) throws JSONException {
        if (jSONArray == null || jSONArray.length() == 0) {
            return new String[0];
        }
        int length = jSONArray.length();
        String[] strArr = new String[length];
        for (int i2 = 0; i2 < length; i2++) {
            String string = jSONArray.getString(i2);
            if (!TextUtils.isEmpty(string)) {
                if (i == b) {
                    string = "[" + string + "]";
                }
                strArr[i2] = string;
            }
        }
        return strArr;
    }
}
