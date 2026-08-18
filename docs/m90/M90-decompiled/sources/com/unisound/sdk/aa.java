package com.unisound.sdk;

import java.text.SimpleDateFormat;
import org.apache.tools.ant.taskdefs.optional.ejb.EjbJar;

/* JADX INFO: loaded from: classes2.dex */
public class aa {
    public static final String a = "filterName";
    public static final String b = "returnType";
    public static final String c = "city";
    public static final String d = "gps";
    public static final String e = "time";
    public static final String f = "scenario";
    public static final String g = "screen";
    public static final String h = "dpi";
    public static final String i = "history";
    public static final String j = "udid";
    public static final String k = "ver";
    public static final String l = "appver";
    public static String m = "http://scv2.hivoice.cn/service/iss";
    private String A;
    private String B;
    private String C;
    private String D;
    private final String n;
    private String o;
    private String p;
    private String q;
    private String r;
    private String s;
    private String t;
    private String u;
    private String v;
    private String w;
    private String x;
    private String y;
    private String z;

    public aa() {
        this.n = "iss.getTalk";
        this.o = EjbJar.CMPVersion.CMP2_0;
        this.p = "";
        this.q = "";
        this.r = "";
        this.s = "";
        this.t = "";
        this.u = "";
        this.v = a(System.currentTimeMillis());
        this.w = "";
        this.x = "";
        this.y = "";
        this.z = "";
        this.A = "";
        this.B = "";
        this.C = "";
        this.D = "";
    }

    public aa(String str, String str2) {
        this.n = "iss.getTalk";
        this.o = EjbJar.CMPVersion.CMP2_0;
        this.p = "";
        this.q = "";
        this.r = "";
        this.s = "";
        this.t = "";
        this.u = "";
        this.v = a(System.currentTimeMillis());
        this.w = "";
        this.x = "";
        this.y = "";
        this.z = "";
        this.A = "";
        this.B = "";
        this.C = "";
        this.D = "";
        this.p = str;
        this.q = str2;
    }

    public aa(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13) {
        this.n = "iss.getTalk";
        this.o = EjbJar.CMPVersion.CMP2_0;
        this.p = "";
        this.q = "";
        this.r = "";
        this.s = "";
        this.t = "";
        this.u = "";
        this.v = a(System.currentTimeMillis());
        this.w = "";
        this.x = "";
        this.y = "";
        this.z = "";
        this.A = "";
        this.B = "";
        this.C = "";
        this.D = "";
        this.p = str;
        this.q = str2;
        this.s = str3;
        this.r = str4;
        this.t = str5;
        this.w = str6;
        this.x = str7;
        this.u = str8;
        this.v = str9;
        this.y = str10;
        this.z = str11;
        this.A = str12;
        this.B = str13;
    }

    public static String a(long j2) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(j2));
    }

    public String a() {
        return m;
    }

    public void a(aa aaVar) {
        this.p = aaVar.b();
        this.r = aaVar.g();
        this.u = aaVar.j();
        this.B = aaVar.p();
        this.t = aaVar.f();
        this.x = aaVar.i();
        this.C = aaVar.q();
        this.z = aaVar.n();
        this.A = aaVar.o();
        this.q = aaVar.c();
        this.w = aaVar.h();
        this.v = aaVar.k();
        this.s = aaVar.e();
        this.o = aaVar.d();
        this.r = aaVar.g();
        this.D = aaVar.r();
        this.y = aaVar.m();
    }

    public void a(String str) {
        m = str;
    }

    public String b() {
        return this.p;
    }

    public void b(long j2) {
        this.v = a(j2);
    }

    public void b(String str) {
        this.p = str;
    }

    public String c() {
        return this.q;
    }

    public void c(String str) {
        this.q = str;
    }

    public String d() {
        return this.o;
    }

    public void d(String str) {
        this.o = str;
    }

    public String e() {
        return this.s;
    }

    public void e(String str) {
        this.s = str;
    }

    public String f() {
        return this.t;
    }

    public void f(String str) {
        this.t = str;
    }

    public String g() {
        return this.r;
    }

    public void g(String str) {
        this.r = str;
    }

    public String h() {
        return this.w;
    }

    public void h(String str) {
        this.w = str;
    }

    public String i() {
        return this.x;
    }

    public void i(String str) {
        this.x = str;
    }

    public String j() {
        return this.u;
    }

    public void j(String str) {
        this.u = str;
    }

    public String k() {
        return this.v;
    }

    public void k(String str) {
        this.y = str;
    }

    public String l() {
        return "iss.getTalk";
    }

    public void l(String str) {
        this.z = str;
    }

    public String m() {
        return this.y;
    }

    public void m(String str) {
        this.A = str;
    }

    public String n() {
        return this.z;
    }

    public void n(String str) {
        this.B = str;
    }

    public String o() {
        return this.A;
    }

    public void o(String str) {
        this.C = str;
    }

    public String p() {
        return this.B;
    }

    public void p(String str) {
        this.D = str;
    }

    public String q() {
        return this.C;
    }

    public String r() {
        return this.D;
    }
}
