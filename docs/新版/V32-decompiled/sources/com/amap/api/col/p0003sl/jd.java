package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.jx;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: compiled from: AMapLogManager.java */
/* JADX INFO: loaded from: classes2.dex */
public final class jd {
    private Context a;
    private is b;
    private boolean c = true;
    private boolean d = false;
    private boolean e = true;
    private boolean f = false;
    private List<String> g = new ArrayList();
    private jf h = new jf((byte) 0);
    private jf i = new jf();
    private jx.a j = new jx.a() { // from class: com.amap.api.col.3sl.jd.1
        @Override // com.amap.api.col.3sl.jx.a
        public final void a(int i) {
            if (i > 0 && jd.a(jd.this) != null) {
                ((je) jd.this.c().f).a(i);
                jd.a(jd.this, "error", String.valueOf(((je) jd.this.c().f).b()));
                jd.a(jd.this).postDelayed(new Runnable() { // from class: com.amap.api.col.3sl.jd.1.1
                    @Override // java.lang.Runnable
                    public final void run() {
                        jd.this.c(false);
                    }
                }, 660000L);
            }
        }
    };
    private jx.a k = new jx.a() { // from class: com.amap.api.col.3sl.jd.2
        @Override // com.amap.api.col.3sl.jx.a
        public final void a(int i) {
            if (i <= 0) {
                return;
            }
            ((je) jd.this.e().f).a(i);
            jd.a(jd.this, DocumentsContract.EXTRA_INFO, String.valueOf(((je) jd.this.e().f).b()));
            if (jd.a(jd.this) == null) {
                return;
            }
            jd.a(jd.this).postDelayed(new Runnable() { // from class: com.amap.api.col.3sl.jd.2.1
                @Override // java.lang.Runnable
                public final void run() {
                    jd.this.d(false);
                }
            }, 660000L);
        }
    };
    private Handler l = null;
    private lf m = null;
    private lf n = null;

    /* JADX INFO: compiled from: AMapLogManager.java */
    static class a {
        public static Map<String, jd> a = new HashMap();
    }

    public static jd a(is isVar) {
        if (isVar == null || TextUtils.isEmpty(isVar.a())) {
            return null;
        }
        if (a.a.get(isVar.a()) == null) {
            a.a.put(isVar.a(), new jd(isVar));
        }
        return a.a.get(isVar.a());
    }

    private jd(is isVar) {
        this.b = isVar;
    }

    public final void a(Context context) {
        this.a = context.getApplicationContext();
    }

    public final void a(boolean z, boolean z2, boolean z3, boolean z4, List<String> list) {
        this.c = z;
        this.d = z2;
        this.e = z3;
        this.f = z4;
        this.g = list;
        d();
        f();
    }

    public final void a(boolean z) {
        if (b()) {
            b(z);
        }
    }

    public final void a(jc jcVar) {
        if (b() && this.c && jc.a(jcVar)) {
            boolean z = true;
            if (jcVar != null) {
                List<String> list = this.g;
                if (list == null || list.size() == 0) {
                    z = false;
                } else {
                    for (int i = 0; i < this.g.size(); i++) {
                        if (!TextUtils.isEmpty(this.g.get(i)) && jcVar.b().contains(this.g.get(i))) {
                            break;
                        }
                    }
                    z = false;
                }
            }
            if (z) {
                return;
            }
            if (this.e || jcVar.a() != jc.a) {
                jf jfVarB = b(jcVar.a());
                if (jfVarB.a(jcVar.b())) {
                    String strA = jc.a(jfVarB.a());
                    if (this.a == null || TextUtils.isEmpty(strA) || "[]".equals(strA)) {
                        return;
                    }
                    jx.a(this.a, this.b, jcVar.c(), c(jcVar.a()), strA);
                    b(false);
                    jfVarB.b();
                }
                jfVarB.a(jcVar);
            }
        }
    }

    public final void a() {
        if (b()) {
            a(jc.b);
            a(jc.a);
        }
    }

    private void a(int i) {
        Context context;
        jf jfVarB = b(i);
        String strA = jc.a(jfVarB.a());
        if (TextUtils.isEmpty(strA) || "[]".equals(strA) || (context = this.a) == null) {
            return;
        }
        jx.a(context, this.b, jc.a(i), c(i), strA);
        jfVarB.b();
    }

    private boolean b() {
        return this.a != null;
    }

    private jf b(int i) {
        if (i == jc.b) {
            return this.i;
        }
        return this.h;
    }

    private void b(boolean z) {
        c(z);
        d(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c(boolean z) {
        lf lfVarC = c(jc.b);
        if (z) {
            ((je) lfVarC.f).a(z);
        }
        Context context = this.a;
        if (context == null) {
            return;
        }
        jx.a(context, lfVarC, this.j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d(boolean z) {
        lf lfVarC = c(jc.a);
        if (z) {
            ((je) lfVarC.f).a(z);
        }
        Context context = this.a;
        if (context == null) {
            return;
        }
        jx.a(context, lfVarC, this.k);
    }

    private lf c(int i) {
        if (i == jc.b) {
            if (this.n == null) {
                this.n = c();
            }
            return this.n;
        }
        if (this.m == null) {
            this.m = e();
        }
        return this.m;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public lf c() {
        lf lfVar = this.n;
        if (lfVar != null) {
            return lfVar;
        }
        d();
        return this.n;
    }

    private lf d() {
        if (this.a == null) {
            return null;
        }
        lf lfVar = new lf();
        this.n = lfVar;
        lfVar.a = h();
        this.n.b = 512000000L;
        this.n.d = 12500;
        this.n.c = "1";
        this.n.h = -1;
        this.n.i = "elkey";
        long jA = a("error");
        this.n.f = new je(true, new ma(this.a, this.d), jA, 10000000);
        this.n.g = null;
        return this.n;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public lf e() {
        lf lfVar = this.m;
        if (lfVar != null) {
            return lfVar;
        }
        f();
        return this.m;
    }

    private long a(String str) {
        try {
            return Long.parseLong(jg.a(this.b).a(this.a, "", "", new SimpleDateFormat("yyyyMMdd").format(new Date()) + str));
        } catch (Throwable unused) {
            return 0L;
        }
    }

    private lf f() {
        if (this.a == null) {
            return null;
        }
        lf lfVar = new lf();
        this.m = lfVar;
        lfVar.a = g();
        this.m.b = 512000000L;
        this.m.d = 12500;
        this.m.c = "1";
        this.m.h = -1;
        this.m.i = "inlkey";
        long jA = a(DocumentsContract.EXTRA_INFO);
        this.m.f = new je(this.f, new ma(this.a, this.d), jA, 30000000);
        this.m.g = null;
        return this.m;
    }

    private String g() {
        Context context = this.a;
        if (context == null) {
            return null;
        }
        return a(context, "CAF9B6B99962BF5C2264824231D7A40C", this.b);
    }

    private String h() {
        Context context = this.a;
        if (context == null) {
            return null;
        }
        return a(context, "CB5E100E5A9A3E7F6D1FD97512215282", this.b);
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0019  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.String a(android.content.Context r2, java.lang.String r3, com.amap.api.col.p0003sl.is r4) {
        /*
            r0 = 0
            if (r2 != 0) goto L4
            return r0
        L4:
            if (r4 == 0) goto L19
            java.lang.String r1 = r4.a()     // Catch: java.lang.Throwable -> L4a
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch: java.lang.Throwable -> L4a
            if (r1 != 0) goto L19
            java.lang.String r4 = r4.a()     // Catch: java.lang.Throwable -> L4a
            java.lang.String r4 = com.amap.api.col.p0003sl.io.b(r4)     // Catch: java.lang.Throwable -> L4a
            goto L1b
        L19:
            java.lang.String r4 = "a"
        L1b:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L4a
            r1.<init>()     // Catch: java.lang.Throwable -> L4a
            java.io.File r2 = r2.getFilesDir()     // Catch: java.lang.Throwable -> L4a
            java.lang.String r2 = r2.getAbsolutePath()     // Catch: java.lang.Throwable -> L4a
            r1.append(r2)     // Catch: java.lang.Throwable -> L4a
            java.lang.String r2 = java.io.File.separator     // Catch: java.lang.Throwable -> L4a
            r1.append(r2)     // Catch: java.lang.Throwable -> L4a
            java.lang.String r2 = "EBDEC84EF205FEA2DF0719DEB822869E"
            r1.append(r2)     // Catch: java.lang.Throwable -> L4a
            java.lang.String r2 = java.io.File.separator     // Catch: java.lang.Throwable -> L4a
            r1.append(r2)     // Catch: java.lang.Throwable -> L4a
            r1.append(r3)     // Catch: java.lang.Throwable -> L4a
            java.lang.String r2 = java.io.File.separator     // Catch: java.lang.Throwable -> L4a
            r1.append(r2)     // Catch: java.lang.Throwable -> L4a
            r1.append(r4)     // Catch: java.lang.Throwable -> L4a
            java.lang.String r2 = r1.toString()     // Catch: java.lang.Throwable -> L4a
            return r2
        L4a:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.jd.a(android.content.Context, java.lang.String, com.amap.api.col.3sl.is):java.lang.String");
    }

    static /* synthetic */ Handler a(jd jdVar) {
        Context context = jdVar.a;
        if (context == null || context == null) {
            return null;
        }
        if (jdVar.l == null) {
            jdVar.l = new Handler(jdVar.a.getMainLooper());
        }
        return jdVar.l;
    }

    static /* synthetic */ void a(jd jdVar, String str, String str2) {
        try {
            jg.a(jdVar.b).a(jdVar.a, "", "", new SimpleDateFormat("yyyyMMdd").format(new Date()) + str, str2);
        } catch (Throwable unused) {
        }
    }
}
