package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: DTInfo.java */
/* JADX INFO: loaded from: classes2.dex */
@kg(a = "update_item")
public class bl {

    @kh(a = "localPath", b = 6)
    protected String h;

    @kh(a = "mCompleteCode", b = 2)
    protected int j;

    @kh(a = "mState", b = 2)
    public int l;

    @kh(a = "title", b = 6)
    protected String a = null;

    @kh(a = "url", b = 6)
    protected String b = null;

    @kh(a = "mAdcode", b = 6)
    protected String c = null;

    @kh(a = "fileName", b = 6)
    protected String d = null;

    @kh(a = "version", b = 6)
    protected String e = "";

    @kh(a = "lLocalLength", b = 5)
    protected long f = 0;

    @kh(a = "lRemoteLength", b = 5)
    protected long g = 0;

    @kh(a = "isProvince", b = 2)
    protected int i = 0;

    @kh(a = "mCityCode", b = 6)
    protected String k = "";

    @kh(a = "mPinyin", b = 6)
    public String m = "";

    public final String c() {
        return this.a;
    }

    public final String d() {
        return this.e;
    }

    public final String e() {
        return this.c;
    }

    public final void c(String str) {
        this.c = str;
    }

    public final String f() {
        return this.b;
    }

    public final int g() {
        return this.j;
    }

    public final void d(String str) {
        this.k = str;
    }

    public final String h() {
        return this.m;
    }

    public static String e(String str) {
        return "mAdcode='" + str + "'";
    }

    public static String f(String str) {
        return "mPinyin='" + str + "'";
    }
}
