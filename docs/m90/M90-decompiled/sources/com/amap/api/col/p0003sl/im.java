package com.amap.api.col.p0003sl;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

/* JADX INFO: compiled from: HttpsDecisionUtil.java */
/* JADX INFO: loaded from: classes2.dex */
public final class im {
    private volatile b a = new b(0);
    private kj b = new kj("HttpsDecisionUtil");

    /* JADX INFO: compiled from: HttpsDecisionUtil.java */
    private static class a {
        static im a = new im();
    }

    public static im a() {
        return a.a;
    }

    public final void a(Context context) {
        if (this.a == null) {
            this.a = new b((byte) 0);
        }
        this.a.a(kj.a(context, "open_common", "a3", true));
        this.a.a(context);
        ji.a(context).a();
    }

    public final void a(boolean z) {
        if (this.a == null) {
            this.a = new b((byte) 0);
        }
        this.a.b(z);
    }

    final void a(Context context, boolean z) {
        if (this.a == null) {
            this.a = new b((byte) 0);
        }
        b(context, z);
        this.a.a(z);
    }

    public final boolean b() {
        if (this.a == null) {
            this.a = new b((byte) 0);
        }
        return this.a.a();
    }

    private static void b(Context context, boolean z) {
        SharedPreferences.Editor editorA = kj.a(context, "open_common");
        kj.a(editorA, "a3", z);
        kj.a(editorA);
    }

    /* JADX INFO: compiled from: HttpsDecisionUtil.java */
    private static class b {
        protected boolean a;
        private int b;
        private final boolean c;
        private boolean d;

        private b() {
            this.b = 0;
            this.a = true;
            this.c = true;
            this.d = false;
        }

        /* synthetic */ b(byte b) {
            this();
        }

        public final void a(Context context) {
            if (context != null && this.b <= 0 && Build.VERSION.SDK_INT >= 4) {
                this.b = context.getApplicationContext().getApplicationInfo().targetSdkVersion;
            }
        }

        public final void a(boolean z) {
            this.a = z;
        }

        public final void b(boolean z) {
            this.d = z;
        }

        /* JADX WARN: Removed duplicated region for block: B:19:0x0022  */
        /* JADX WARN: Removed duplicated region for block: B:23:0x0029  */
        /* JADX WARN: Removed duplicated region for block: B:26:0x002d A[RETURN] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public final boolean a() {
            /*
                r5 = this;
                boolean r0 = r5.d
                r1 = 1
                if (r0 != 0) goto L2e
                int r0 = android.os.Build.VERSION.SDK_INT
                r2 = 28
                r3 = 0
                if (r0 < r2) goto Le
                r0 = r1
                goto Lf
            Le:
                r0 = r3
            Lf:
                boolean r4 = r5.a
                if (r4 == 0) goto L22
                int r4 = r5.b
                if (r4 > 0) goto L18
                r4 = r2
            L18:
                if (r4 < r2) goto L1c
                r2 = r1
                goto L1d
            L1c:
                r2 = r3
            L1d:
                if (r2 == 0) goto L20
                goto L22
            L20:
                r2 = r3
                goto L23
            L22:
                r2 = r1
            L23:
                if (r0 == 0) goto L29
                if (r2 == 0) goto L29
                r0 = r1
                goto L2a
            L29:
                r0 = r3
            L2a:
                if (r0 == 0) goto L2d
                goto L2e
            L2d:
                return r3
            L2e:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.3sl.im.b.a():boolean");
        }
    }

    public static String a(String str) {
        if (TextUtils.isEmpty(str) || str.startsWith("https")) {
            return str;
        }
        try {
            Uri.Builder builderBuildUpon = Uri.parse(str).buildUpon();
            builderBuildUpon.scheme("https");
            return builderBuildUpon.build().toString();
        } catch (Throwable unused) {
            return str;
        }
    }

    private static boolean c() {
        return Build.VERSION.SDK_INT == 19;
    }

    public final boolean b(boolean z) {
        if (c()) {
            return false;
        }
        return z || b();
    }

    public static void b(Context context) {
        b(context, true);
    }
}
