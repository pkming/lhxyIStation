package com.autonavi.aps.amapapi.restruct;

/* JADX INFO: compiled from: Cgi.java */
/* JADX INFO: loaded from: classes2.dex */
public final class d {
    public int l;
    public boolean n;
    public int a = 0;
    public int b = 0;
    public int c = 0;
    public int d = 0;
    public long e = 0;
    public int f = 0;
    public int g = 0;
    public int h = 0;
    public int i = 0;
    public int j = 0;
    public int k = -113;
    public short m = 0;
    public int o = 32767;
    public int p = Integer.MAX_VALUE;
    public int q = Integer.MAX_VALUE;
    public boolean r = true;
    public int s = 99;
    public long t = 0;

    public d(int i, boolean z) {
        this.l = 0;
        this.n = false;
        this.l = i;
        this.n = z;
    }

    public final boolean equals(Object obj) {
        if (obj != null && (obj instanceof d)) {
            d dVar = (d) obj;
            int i = dVar.l;
            if (i != 1) {
                return i != 2 ? i != 3 ? i != 4 ? i == 5 && this.l == 5 && dVar.c == this.c && dVar.e == this.e && dVar.q == this.q : this.l == 4 && dVar.c == this.c && dVar.d == this.d && dVar.b == this.b : this.l == 3 && dVar.c == this.c && dVar.d == this.d && dVar.b == this.b : this.l == 2 && dVar.j == this.j && dVar.i == this.i && dVar.h == this.h;
            }
            if (this.l == 1 && dVar.c == this.c && dVar.d == this.d && dVar.b == this.b) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        int iHashCode;
        int iHashCode2;
        int iHashCode3 = String.valueOf(this.l).hashCode();
        if (this.l == 2) {
            iHashCode = String.valueOf(this.j).hashCode() + String.valueOf(this.i).hashCode();
            iHashCode2 = String.valueOf(this.h).hashCode();
        } else {
            iHashCode = String.valueOf(this.c).hashCode() + String.valueOf(this.d).hashCode();
            iHashCode2 = String.valueOf(this.b).hashCode();
        }
        return iHashCode3 + iHashCode + iHashCode2;
    }

    public final long a() {
        if (this.l == 5) {
            return this.e;
        }
        return this.d;
    }

    public final String b() {
        int i = this.l;
        if (i != 1) {
            if (i == 2) {
                return f();
            }
            if (i != 3 && i != 4 && i != 5) {
                return null;
            }
        }
        return e();
    }

    private String e() {
        int i = this.l;
        return this.l + "#" + this.a + "#" + this.b + "#0#" + a();
    }

    private String f() {
        return this.l + "#" + this.h + "#" + this.i + "#" + this.j;
    }

    public final String c() {
        String strB = b();
        if (strB == null || strB.length() <= 0) {
            return "";
        }
        return (this.r ? 1 : 0) + "#" + strB;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX INFO: renamed from: d, reason: merged with bridge method [inline-methods] */
    public final d clone() {
        d dVar = new d(this.l, this.n);
        dVar.a = this.a;
        dVar.b = this.b;
        dVar.c = this.c;
        dVar.d = this.d;
        dVar.e = this.e;
        dVar.f = this.f;
        dVar.g = this.g;
        dVar.h = this.h;
        dVar.i = this.i;
        dVar.j = this.j;
        dVar.k = this.k;
        dVar.m = this.m;
        dVar.o = this.o;
        dVar.p = this.p;
        dVar.q = this.q;
        dVar.r = this.r;
        dVar.s = this.s;
        dVar.t = this.t;
        return dVar;
    }
}
