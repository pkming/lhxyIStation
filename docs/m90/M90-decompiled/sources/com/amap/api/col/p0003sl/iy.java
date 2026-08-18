package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import org.json.JSONObject;

/* JADX INFO: compiled from: AAIDManager.java */
/* JADX INFO: loaded from: classes2.dex */
public class iy {
    private static iy a = null;
    private static boolean b = false;
    private static boolean c = false;
    private Context d;

    private iy(Context context) {
        this.d = context;
    }

    public static iy a(Context context) {
        if (a == null) {
            synchronized (iy.class) {
                if (a == null) {
                    a = new iy(context);
                }
            }
        }
        return a;
    }

    public final String a() {
        String strC = "";
        try {
            if (iv.d) {
                strC = iu.c(this.d);
                long jD = iu.d(this.d);
                long jElapsedRealtime = SystemClock.elapsedRealtime();
                if (TextUtils.isEmpty(strC)) {
                    mc.a().a(new md() { // from class: com.amap.api.col.3sl.iy.1
                        @Override // com.amap.api.col.p0003sl.md
                        public final void runTask() {
                            iy.this.b();
                        }
                    });
                } else if (jElapsedRealtime - jD > iv.b) {
                    mc.a().a(new md() { // from class: com.amap.api.col.3sl.iy.2
                        @Override // com.amap.api.col.p0003sl.md
                        public final void runTask() {
                            iy.this.c();
                        }
                    });
                }
            }
        } catch (Throwable unused) {
        }
        return strC;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b() {
        try {
            if (!b) {
                b = true;
                iw iwVar = new iw(this.d);
                new ku();
                lc lcVarA = ku.a(iwVar);
                if (lcVarA != null) {
                    JSONObject jSONObject = new JSONObject(it.a(ix.a(lcVarA.a, it.c("YWDR1a2R2WEd0M3RXdHRocg==").getBytes())));
                    if (jSONObject.optBoolean("suc")) {
                        iu.f(this.d, iwVar.a);
                        iu.g(this.d, iwVar.b);
                        iu.h(this.d, iwVar.c);
                        iu.i(this.d, iwVar.d);
                        iu.j(this.d, iwVar.e);
                        iu.k(this.d, iwVar.f);
                        iu.l(this.d, iwVar.g);
                        iu.b(this.d, iwVar.i);
                        iu.m(this.d, iwVar.h);
                        iu.a(this.d, SystemClock.elapsedRealtime());
                        String strOptString = jSONObject.optString("aaid", "");
                        String strOptString2 = jSONObject.optString("resetToken", "");
                        String strOptString3 = jSONObject.optString("uabc", "");
                        if (!TextUtils.isEmpty(strOptString)) {
                            iu.c(this.d, strOptString);
                        }
                        if (!TextUtils.isEmpty(strOptString2)) {
                            iu.e(this.d, strOptString2);
                        }
                        if (!TextUtils.isEmpty(strOptString3)) {
                            iu.d(this.d, strOptString3);
                        }
                    }
                }
                b = false;
                return;
            }
            b = false;
        } catch (Throwable unused) {
            b = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c() {
        try {
            if (!c) {
                c = true;
                iz izVar = new iz(this.d);
                new ku();
                lc lcVarA = ku.a(izVar);
                if (lcVarA != null) {
                    JSONObject jSONObject = new JSONObject(it.a(ix.a(lcVarA.a, it.c("YWDR1a2R2WEd0M3RXdHRocg==").getBytes())));
                    if (jSONObject.optBoolean("suc")) {
                        iu.f(this.d, izVar.a);
                        iu.g(this.d, izVar.b);
                        iu.h(this.d, izVar.c);
                        iu.i(this.d, izVar.d);
                        iu.j(this.d, izVar.e);
                        iu.k(this.d, izVar.f);
                        iu.l(this.d, izVar.g);
                        iu.b(this.d, izVar.i);
                        iu.m(this.d, izVar.h);
                        iu.a(this.d, SystemClock.elapsedRealtime());
                        String strOptString = jSONObject.optString("aaid", "");
                        String strOptString2 = jSONObject.optString("resetToken", "");
                        String strOptString3 = jSONObject.optString("uabc", "");
                        if (!TextUtils.isEmpty(strOptString)) {
                            iu.c(this.d, strOptString);
                        }
                        if (!TextUtils.isEmpty(strOptString2)) {
                            iu.e(this.d, strOptString2);
                        }
                        if (!TextUtils.isEmpty(strOptString3)) {
                            iu.d(this.d, strOptString3);
                        }
                    }
                }
                c = false;
                return;
            }
            c = false;
        } catch (Throwable unused) {
            c = false;
        }
    }
}
