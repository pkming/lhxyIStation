package com.unisound.sdk;

/* JADX INFO: loaded from: classes2.dex */
class bz extends aa {
    private String n;
    private boolean o;
    private String p;
    private String q;

    public bz() {
        this.n = "search";
        this.o = true;
        this.p = "json";
        this.q = m;
    }

    public bz(String str, String str2) {
        super(str, str2);
        this.n = "search";
        this.o = true;
        this.p = "json";
        this.q = m;
    }

    public void a(bz bzVar) {
        super.a((aa) bzVar);
        this.n = bzVar.t();
        this.o = bzVar.v();
        this.q = bzVar.s();
        this.p = bzVar.u();
    }

    public void a(boolean z) {
        this.o = z;
    }

    public boolean a(String str, int i) {
        if (str == null) {
            return false;
        }
        this.q = "http://" + str + ":" + i + "/service/iss";
        return true;
    }

    public void q(String str) {
        this.q = str;
    }

    public boolean r(String str) {
        return false;
    }

    public String s() {
        return this.q;
    }

    public void s(String str) {
        this.n = str;
    }

    public String t() {
        return this.n;
    }

    public void t(String str) {
        this.p = str;
    }

    public String u() {
        return this.p;
    }

    public boolean v() {
        return this.o;
    }

    public bz w() {
        return this;
    }

    public String x() {
        return "returnType=" + u() + ";city=" + j() + ";gps=" + f() + ";time=" + k() + ";scenario=" + n() + ";screen=" + o() + ";dpi=" + p() + ";history=" + i() + ";udid=" + e() + ";ver=" + d() + ";appver=" + g() + ";";
    }
}
