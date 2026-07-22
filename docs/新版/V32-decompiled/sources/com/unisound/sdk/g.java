package com.unisound.sdk;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class g {
    public static final int a = 8000;
    public static final int b = 16000;
    public static final int c = 80000;
    public static final String d = "far";
    public static final String e = "near";
    public static final String f = "8k";
    public static final String g = "16k";
    public static final String h = "16kto8k";
    public static final String i = "general";
    public static final String j = "poi";
    public static final String k = "food";
    public static final String l = "medical";
    public static final String m = "movietv";
    public static final String n = "textFormat";
    public static final String o = "en";
    public static final String p = "co";
    public static final String q = "cn";
    private static final String r = "modelType";
    private static final String s = "subModel";
    private static final String t = "voiceField";
    private static final String u = "lang";
    private static final String v = "sampleRate";
    private static final String w = "oneshot";
    private static final String x = "oneshot_key";
    private static final String y = "alread_awpe";
    private String E;
    private Map<String, String> z = new HashMap();
    private StringBuffer A = new StringBuffer();
    private boolean B = true;
    private int C = 16000;
    private boolean D = true;
    private boolean F = true;

    public g() {
        d("near");
        a(16000);
        d(false);
        f("");
        g("json");
    }

    private void g() {
        this.B = true;
    }

    private void h() {
        if (this.B) {
            this.B = false;
            StringBuffer stringBuffer = this.A;
            stringBuffer.delete(0, stringBuffer.length());
            for (String str : this.z.keySet()) {
                this.A.append(str).append(":");
                this.A.append(this.z.get(str)).append("\n");
            }
        }
    }

    public void a(String str) {
        this.z.put(u, str);
        g();
    }

    public void a(boolean z) {
        this.D = z;
    }

    public boolean a() {
        return this.D;
    }

    public boolean a(int i2) {
        if (i2 == 8000) {
            b();
            return true;
        }
        if (i2 == 16000) {
            d();
            return true;
        }
        if (i2 != 80000) {
            com.unisound.common.r.e(toString() + ".setSampleRate param error " + i2);
            return false;
        }
        c();
        return true;
    }

    public void b() {
        e(h);
        this.C = 8000;
    }

    public void b(boolean z) {
        this.F = z;
    }

    public boolean b(String str) {
        this.z.put(r, str);
        g();
        return true;
    }

    public void c() {
        e(f);
        this.C = c;
    }

    public boolean c(String str) {
        this.z.put(s, str);
        g();
        return true;
    }

    public boolean c(boolean z) {
        return this.F;
    }

    public void d() {
        e(g);
        this.C = 16000;
    }

    public void d(String str) {
        this.z.put(t, str);
        g();
    }

    public void d(boolean z) {
        this.z.put(w, String.valueOf(z));
        g();
    }

    public void e() {
        StringBuffer stringBuffer = this.A;
        stringBuffer.delete(0, stringBuffer.length());
        this.z.clear();
    }

    public void e(String str) {
        this.z.put(v, str);
        g();
    }

    public void e(boolean z) {
        this.z.put(y, String.valueOf(z));
        g();
    }

    public int f() {
        return this.C;
    }

    public void f(String str) {
        this.z.put(x, str);
        this.E = str;
        g();
    }

    public void g(String str) {
        this.z.put(n, str);
        g();
    }

    public String toString() {
        h();
        return this.A.toString();
    }
}
