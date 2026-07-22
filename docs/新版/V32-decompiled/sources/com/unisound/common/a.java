package com.unisound.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

/* JADX INFO: loaded from: classes2.dex */
public class a {
    private String a;
    private int b;
    private String d;
    private int e;
    private boolean g;
    private String c = "";
    private boolean f = false;

    public a(a aVar) {
        this.a = "117.121.55.35";
        this.b = 80;
        this.d = "117.121.55.35";
        this.e = 80;
        this.g = true;
        this.a = aVar.a;
        this.b = aVar.b;
        this.d = aVar.d;
        this.e = aVar.e;
        this.g = true;
    }

    public a(String str, int i, String str2, int i2) {
        this.a = "117.121.55.35";
        this.b = 80;
        this.d = "117.121.55.35";
        this.e = 80;
        this.g = true;
        this.a = str;
        this.b = i;
        this.e = i2;
        this.d = str2;
        this.g = true;
    }

    private void f() {
        if (this.f) {
            return;
        }
        try {
            this.c = InetAddress.getByName(this.a).getHostAddress();
            this.f = true;
        } catch (UnknownHostException unused) {
            r.e("InetAddress.getByName fail");
        }
    }

    public String a() {
        f();
        return this.f ? this.c : this.d;
    }

    public void a(int i) {
        this.e = i;
        this.g = true;
    }

    public void a(a aVar) {
        this.a = aVar.a;
        this.b = aVar.b;
        this.d = aVar.d;
        this.e = aVar.e;
        this.f = false;
        this.g = true;
    }

    public void a(String str) {
        this.d = str;
        this.g = true;
    }

    public void a(boolean z) {
        this.g = z;
    }

    public String b() {
        return this.a;
    }

    public void b(int i) {
        this.b = i;
    }

    public void b(String str) {
        this.a = str;
        this.f = false;
        this.g = true;
    }

    public int c() {
        return this.b;
    }

    public boolean c(String str) {
        if (str == null) {
            return false;
        }
        String[] strArrSplit = str.split(":");
        if (strArrSplit.length != 2 || strArrSplit[0].length() == 0) {
            return false;
        }
        try {
            short sIntValue = (short) Integer.valueOf(strArrSplit[1]).intValue();
            this.b = sIntValue;
            this.a = strArrSplit[0];
            this.d = strArrSplit[0];
            this.e = sIntValue;
            this.g = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean d() {
        return this.g;
    }

    public void e() {
        this.f = false;
    }
}
