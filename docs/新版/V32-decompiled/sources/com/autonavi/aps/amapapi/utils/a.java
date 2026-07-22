package com.autonavi.aps.amapapi.utils;

import android.app.backup.FullBackup;
import android.content.Context;
import android.content.SharedPreferences;
import android.mtp.MtpConstants;
import android.os.Build;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.ih;
import com.amap.api.col.p0003sl.is;
import com.amap.api.col.p0003sl.it;
import com.amap.api.col.p0003sl.jw;
import com.amap.api.col.p0003sl.lj;
import com.amap.api.col.p0003sl.ll;
import java.util.ArrayList;
import org.apache.tools.ant.types.selectors.DepthSelector;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: compiled from: AuthUtil.java */
/* JADX INFO: loaded from: classes2.dex */
public final class a {
    private static volatile boolean i = false;
    private static boolean j = true;
    private static int k = 1000;
    private static int l = 200;
    private static boolean m = false;
    private static int n = 20;
    private static int o = 0;
    private static volatile int p = 0;
    private static boolean q = true;
    private static boolean r = false;
    private static int s = -1;
    private static long t;
    private static ArrayList<String> u = new ArrayList<>();
    private static ArrayList<String> v = new ArrayList<>();
    private static volatile boolean w = false;
    private static boolean x = true;
    private static long y = 300000;
    private static boolean z = false;
    private static double A = 0.618d;
    private static boolean B = true;
    private static int C = 80;
    private static int D = 5;
    static long a = 3600000;
    private static boolean E = false;
    private static boolean F = true;
    private static boolean G = false;
    public static volatile long b = 0;
    static boolean c = false;
    private static boolean H = true;
    private static long I = -1;
    private static boolean J = true;
    private static int K = 1;
    private static boolean L = false;
    private static int M = 5;
    private static boolean N = false;
    private static String O = "CMjAzLjEwNy4xLjEvMTU0MDgxL2Q";
    private static long P = 0;
    public static boolean d = false;
    public static boolean e = false;
    public static int f = MtpConstants.DEVICE_PROPERTY_UNDEFINED;
    public static int g = 10800000;
    public static boolean h = false;

    public static void a(final Context context) {
        if (i) {
            return;
        }
        i = true;
        ih.a(context, b.c(), b.d(), new ih.a() { // from class: com.autonavi.aps.amapapi.utils.a.1
            @Override // com.amap.api.col.3sl.ih.a
            public final void a(ih.b bVar) {
                a.a(context, bVar);
            }
        });
    }

    public static boolean a() {
        return j;
    }

    public static int b() {
        return l;
    }

    public static int c() {
        if (p < 0) {
            p = 0;
        }
        return p;
    }

    private static void a(ih.b bVar, SharedPreferences.Editor editor) {
        try {
            ih.b.a aVar = bVar.g;
            if (aVar != null) {
                boolean z2 = aVar.a;
                j = z2;
                i.a(editor, "exception", z2);
                JSONObject jSONObject = aVar.c;
                if (jSONObject != null) {
                    k = jSONObject.optInt("fn", k);
                    int iOptInt = jSONObject.optInt("mpn", l);
                    l = iOptInt;
                    if (iOptInt > 500) {
                        l = 500;
                    }
                    if (l < 30) {
                        l = 30;
                    }
                    m = ih.a(jSONObject.optString("igu"), false);
                    n = jSONObject.optInt("ms", n);
                    p = jSONObject.optInt("rot", 0);
                    o = jSONObject.optInt("pms", 0);
                }
                lj.a(k, m, n, o);
                ll.a(m, o);
                i.a(editor, "fn", k);
                i.a(editor, "mpn", l);
                i.a(editor, "igu", m);
                i.a(editor, "ms", n);
                i.a(editor, "rot", p);
                i.a(editor, "pms", o);
            }
        } catch (Throwable th) {
            b.a(th, "AuthUtil", "loadConfigDataUploadException");
        }
    }

    private static void a(JSONObject jSONObject, SharedPreferences.Editor editor) {
        try {
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("11G");
            if (jSONObjectOptJSONObject != null) {
                boolean zA = ih.a(jSONObjectOptJSONObject.optString("able"), true);
                x = zA;
                if (zA) {
                    y = jSONObjectOptJSONObject.optInt(FullBackup.CACHE_TREE_TOKEN, 300) * 1000;
                }
                z = ih.a(jSONObjectOptJSONObject.optString("fa"), false);
                A = Math.min(1.0d, Math.max(0.2d, jSONObjectOptJSONObject.optDouble("ms", 0.618d)));
                i.a(editor, "ca", x);
                i.a(editor, "ct", y);
                i.a(editor, "11G_fa", z);
                i.a(editor, "11G_ms", String.valueOf(A));
            }
        } catch (Throwable th) {
            b.a(th, "AuthUtil", "loadConfigDataCacheAble");
        }
    }

    static boolean a(Context context, ih.b bVar) {
        SharedPreferences.Editor editorA;
        try {
            editorA = i.a(context, "pref");
        } catch (Throwable unused) {
            editorA = null;
        }
        try {
            a(bVar, editorA);
            c(context);
            JSONObject jSONObject = bVar.f;
            if (jSONObject == null) {
                if (editorA != null) {
                    try {
                        i.a(editorA);
                    } catch (Throwable unused2) {
                    }
                }
                return true;
            }
            a(context, jSONObject, editorA);
            a(jSONObject, editorA);
            d(jSONObject, editorA);
            f(jSONObject, editorA);
            h(jSONObject, editorA);
            g(jSONObject, editorA);
            i(jSONObject, editorA);
            b(jSONObject, editorA);
            c(jSONObject, editorA);
            if (editorA != null) {
                try {
                    i.a(editorA);
                } catch (Throwable unused3) {
                }
            }
            return true;
        } catch (Throwable unused4) {
            if (editorA == null) {
                return false;
            }
            try {
                i.a(editorA);
                return false;
            } catch (Throwable unused5) {
                return false;
            }
        }
    }

    private static void b(JSONObject jSONObject, SharedPreferences.Editor editor) {
        if (jSONObject == null) {
            return;
        }
        try {
            JSONObject jSONObject2 = jSONObject.getJSONObject("197");
            if (jSONObject2 != null) {
                boolean zA = ih.a(jSONObject2.optString("able"), false);
                i.a(editor, "197a", zA);
                if (zA) {
                    i.a(editor, "197dv", jSONObject2.optString("sv", ""));
                    i.a(editor, "197tv", jSONObject2.optString("tv", ""));
                } else {
                    i.a(editor, "197dv", "");
                    i.a(editor, "197tv", "");
                }
            }
        } catch (Throwable unused) {
        }
    }

    private static void c(JSONObject jSONObject, SharedPreferences.Editor editor) {
        if (jSONObject == null) {
            return;
        }
        try {
            JSONObject jSONObject2 = jSONObject.getJSONObject("1A6");
            if (jSONObject2 != null) {
                boolean zA = ih.a(jSONObject2.optString("ic"), false);
                i.a(editor, "1A6", zA);
                h = zA;
            }
        } catch (Throwable unused) {
        }
    }

    public static void b(Context context) {
        if (w) {
            return;
        }
        w = true;
        try {
            j = i.a(context, "pref", "exception", j);
            c(context);
        } catch (Throwable th) {
            b.a(th, "AuthUtil", "loadLastAbleState p1");
        }
        try {
            k = i.a(context, "pref", "fn", k);
            l = i.a(context, "pref", "mpn", l);
            m = i.a(context, "pref", "igu", m);
            n = i.a(context, "pref", "ms", n);
            p = i.a(context, "pref", "rot", 0);
            int iA = i.a(context, "pref", "pms", 0);
            o = iA;
            lj.a(k, m, n, iA);
            ll.a(m, o);
        } catch (Throwable th2) {
            b.a(th2, "AuthUtil", "loadLastAbleState p2");
        }
        try {
            x = i.a(context, "pref", "ca", x);
            y = i.a(context, "pref", "ct", y);
            z = i.a(context, "pref", "11G_fa", z);
            double dDoubleValue = Double.valueOf(i.a(context, "pref", "11G_ms", String.valueOf(A))).doubleValue();
            A = dDoubleValue;
            A = Math.max(0.2d, dDoubleValue);
        } catch (Throwable th3) {
            b.a(th3, "AuthUtil", "loadLastAbleState p3");
        }
        try {
            c = i.a(context, "pref", "fr", c);
        } catch (Throwable th4) {
            b.a(th4, "AuthUtil", "loadLastAbleState p4");
        }
        try {
            H = i.a(context, "pref", "asw", H);
        } catch (Throwable th5) {
            b.a(th5, "AuthUtil", "loadLastAbleState p5");
        }
        try {
            I = i.a(context, "pref", "awsi", I);
        } catch (Throwable th6) {
            b.a(th6, "AuthUtil", "loadLastAbleState p6");
        }
        try {
            J = i.a(context, "pref", "15ua", J);
            K = i.a(context, "pref", "15un", K);
            P = i.a(context, "pref", "15ust", P);
        } catch (Throwable th7) {
            b.a(th7, "AuthUtil", "loadLastAbleState p7");
        }
        try {
            L = i.a(context, "pref", "ok9", L);
            M = i.a(context, "pref", "ok10", M);
            O = i.a(context, "pref", "ok11", O);
        } catch (Throwable th8) {
            b.a(th8, "AuthUtil", "loadLastAbleState p8");
        }
        try {
            d = i.a(context, "pref", "17ya", false);
            e = i.a(context, "pref", "17ym", false);
            g = i.a(context, "pref", "17yi", 2) * 60 * 60 * 1000;
            f = i.a(context, "pref", "17yx", 100) * 1024;
        } catch (Throwable th9) {
            b.a(th9, "AuthUtil", "loadLastAbleState p9");
        }
        try {
            b = j.b();
            a = i.a(context, "pref", "13S_at", a);
            F = i.a(context, "pref", "13S_nla", F);
            B = i.a(context, "pref", "13J_able", B);
            C = i.a(context, "pref", "13J_c", C);
            D = i.a(context, "pref", "13J_t", D);
        } catch (Throwable th10) {
            b.a(th10, "AuthUtil", "loadLastAbleState p10");
        }
        ih.b(context);
        try {
            String strA = i.a(context, "pref", "13S_mlpl", (String) null);
            if (!TextUtils.isEmpty(strA)) {
                G = a(context, new JSONArray(it.c(strA)));
            }
        } catch (Throwable th11) {
            b.a(th11, "AuthUtil", "loadLastAbleState p11");
        }
        try {
            boolean zA = i.a(context, "pref", "197a", false);
            String strA2 = i.a(context, "pref", "197dv", "");
            String strA3 = i.a(context, "pref", "197tv", "");
            if (zA && b.a.equals(strA2)) {
                for (String str : b.b) {
                    if (str.equals(strA3)) {
                        b.a = strA3;
                    }
                }
            }
        } catch (Throwable th12) {
            b.a(th12, "AuthUtil", "loadLastAbleState p12");
        }
        try {
            h = i.a(context, "pref", "1A6", h);
        } catch (Throwable th13) {
            b.a(th13, "AuthUtil", "loadSdkEnableConfig p13");
        }
    }

    public static long d() {
        return y;
    }

    public static boolean e() {
        return x;
    }

    public static boolean a(long j2) {
        if (!x) {
            return false;
        }
        long jA = j.a() - j2;
        long j3 = y;
        return j3 < 0 || jA < j3;
    }

    public static boolean f() {
        return z;
    }

    public static double g() {
        return A;
    }

    public static void c(Context context) {
        try {
            is isVarC = b.c();
            isVarC.a(j);
            jw.a(context, isVarC);
        } catch (Throwable unused) {
        }
    }

    public static boolean h() {
        return B;
    }

    public static int i() {
        return C;
    }

    public static int j() {
        return D;
    }

    private static void d(JSONObject jSONObject, SharedPreferences.Editor editor) {
        try {
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("13J");
            if (jSONObjectOptJSONObject != null) {
                boolean zA = ih.a(jSONObjectOptJSONObject.optString("able"), true);
                B = zA;
                if (zA) {
                    C = jSONObjectOptJSONObject.optInt(FullBackup.CACHE_TREE_TOKEN, C);
                    D = jSONObjectOptJSONObject.optInt("t", D);
                }
                i.a(editor, "13J_able", B);
                i.a(editor, "13J_c", C);
                i.a(editor, "13J_t", D);
            }
        } catch (Throwable th) {
            b.a(th, "AuthUtil", "loadConfigDataGpsGeoAble");
        }
    }

    public static boolean k() {
        return F;
    }

    public static boolean l() {
        return G;
    }

    private static void a(Context context, JSONObject jSONObject, SharedPreferences.Editor editor) {
        try {
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("13S");
            if (jSONObjectOptJSONObject != null) {
                try {
                    long jOptInt = jSONObjectOptJSONObject.optInt("at", 123) * 60 * 1000;
                    a = jOptInt;
                    i.a(editor, "13S_at", jOptInt);
                } catch (Throwable th) {
                    b.a(th, "AuthUtil", "requestSdkAuthInterval");
                }
                e(jSONObjectOptJSONObject, editor);
                try {
                    boolean zA = ih.a(jSONObjectOptJSONObject.optString("nla"), true);
                    F = zA;
                    i.a(editor, "13S_nla", zA);
                } catch (Throwable unused) {
                }
                try {
                    boolean zA2 = ih.a(jSONObjectOptJSONObject.optString("asw"), true);
                    H = zA2;
                    i.a(editor, "asw", zA2);
                } catch (Throwable unused2) {
                }
                try {
                    JSONArray jSONArrayOptJSONArray = jSONObjectOptJSONObject.optJSONArray("mlpl");
                    if (jSONArrayOptJSONArray != null && jSONArrayOptJSONArray.length() > 0 && context != null) {
                        i.a(editor, "13S_mlpl", it.b(jSONArrayOptJSONArray.toString()));
                        G = a(context, jSONArrayOptJSONArray);
                    } else {
                        G = false;
                        i.a(editor, "13S_mlpl");
                    }
                } catch (Throwable unused3) {
                }
            }
        } catch (Throwable th2) {
            b.a(th2, "AuthUtil", "loadConfigAbleStatus");
        }
    }

    private static boolean a(Context context, JSONArray jSONArray) {
        if (jSONArray != null) {
            try {
                if (jSONArray.length() > 0 && context != null) {
                    for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                        if (j.b(context, jSONArray.getString(i2))) {
                            return true;
                        }
                    }
                }
            } catch (Throwable unused) {
            }
        }
        return false;
    }

    public static boolean m() {
        return c;
    }

    private static void e(JSONObject jSONObject, SharedPreferences.Editor editor) {
        if (jSONObject == null) {
            return;
        }
        try {
            boolean zA = ih.a(jSONObject.optString("re"), false);
            c = zA;
            i.a(editor, "fr", zA);
        } catch (Throwable th) {
            b.a(th, "AuthUtil", "checkReLocationAble");
        }
    }

    public static boolean n() {
        return H;
    }

    public static long o() {
        return I;
    }

    private static void f(JSONObject jSONObject, SharedPreferences.Editor editor) {
        JSONArray jSONArrayOptJSONArray;
        try {
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("15O");
            if (jSONObjectOptJSONObject != null) {
                if (ih.a(jSONObjectOptJSONObject.optString("able"), false) && ((jSONArrayOptJSONArray = jSONObjectOptJSONObject.optJSONArray("fl")) == null || jSONArrayOptJSONArray.length() <= 0 || jSONArrayOptJSONArray.toString().contains(Build.MANUFACTURER))) {
                    I = jSONObjectOptJSONObject.optInt("iv", 30) * 1000;
                } else {
                    I = -1L;
                }
                i.a(editor, "awsi", I);
            }
        } catch (Throwable unused) {
        }
    }

    public static boolean p() {
        return N;
    }

    public static boolean q() {
        return L;
    }

    public static String r() {
        return it.c(O);
    }

    private static void g(JSONObject jSONObject, SharedPreferences.Editor editor) {
        if (jSONObject == null) {
            return;
        }
        try {
            JSONObject jSONObject2 = jSONObject.getJSONObject("17Y");
            if (jSONObject2 != null) {
                boolean zA = ih.a(jSONObject2.optString("able"), false);
                d = zA;
                i.a(editor, "17ya", zA);
                boolean zA2 = ih.a(jSONObject2.optString("mup"), false);
                e = zA2;
                i.a(editor, "17ym", zA2);
                int iOptInt = jSONObject2.optInt(DepthSelector.MAX_KEY, 20);
                if (iOptInt > 0) {
                    i.a(editor, "17yx", iOptInt);
                    f = iOptInt * 1024;
                }
                int iOptInt2 = jSONObject2.optInt("inv", 3);
                if (iOptInt2 > 0) {
                    i.a(editor, "17yi", iOptInt2);
                    g = iOptInt2 * 60 * 60 * 1000;
                }
            }
        } catch (Throwable unused) {
        }
    }

    private static void h(JSONObject jSONObject, SharedPreferences.Editor editor) {
        try {
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("15U");
            if (jSONObjectOptJSONObject != null) {
                boolean zA = ih.a(jSONObjectOptJSONObject.optString("able"), true);
                int iOptInt = jSONObjectOptJSONObject.optInt("yn", K);
                P = jSONObjectOptJSONObject.optLong("sysTime", P);
                i.a(editor, "15ua", zA);
                i.a(editor, "15un", iOptInt);
                i.a(editor, "15ust", P);
            }
        } catch (Throwable unused) {
        }
    }

    private static void i(JSONObject jSONObject, SharedPreferences.Editor editor) {
        int i2;
        if (jSONObject == null) {
            return;
        }
        try {
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("17J");
            if (jSONObjectOptJSONObject != null) {
                boolean zA = ih.a(jSONObjectOptJSONObject.optString("able"), false);
                L = zA;
                i.a(editor, "ok9", zA);
                if (zA) {
                    String strOptString = jSONObjectOptJSONObject.optString("auth");
                    String strOptString2 = jSONObjectOptJSONObject.optString("ht");
                    O = strOptString2;
                    i.a(editor, "ok11", strOptString2);
                    ih.a(strOptString, false);
                    N = ih.a(jSONObjectOptJSONObject.optString("nr"), false);
                    String strOptString3 = jSONObjectOptJSONObject.optString("tm");
                    if (TextUtils.isEmpty(strOptString3) || (i2 = Integer.parseInt(strOptString3)) <= 0 || i2 >= 20) {
                        return;
                    }
                    M = i2;
                    i.a(editor, "ok10", i2);
                }
            }
        } catch (Throwable unused) {
        }
    }

    public static boolean s() {
        return J && K > 0;
    }

    public static int t() {
        return K;
    }

    public static long u() {
        return P;
    }
}
