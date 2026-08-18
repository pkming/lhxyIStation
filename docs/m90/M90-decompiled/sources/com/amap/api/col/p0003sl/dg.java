package com.amap.api.col.p0003sl;

import com.autonavi.amap.mapcore.DPoint;

/* JADX INFO: compiled from: Bounds.java */
/* JADX INFO: loaded from: classes2.dex */
public final class dg {
    public final double a;
    public final double b;
    public final double c;
    public final double d;
    public final double e;
    public final double f;

    public dg(double d, double d2, double d3, double d4) {
        this.a = d;
        this.b = d3;
        this.c = d2;
        this.d = d4;
        this.e = (d + d2) / 2.0d;
        this.f = (d3 + d4) / 2.0d;
    }

    public final boolean a(double d, double d2) {
        return this.a <= d && d <= this.c && this.b <= d2 && d2 <= this.d;
    }

    public final boolean a(DPoint dPoint) {
        return a(dPoint.x, dPoint.y);
    }

    private boolean a(double d, double d2, double d3, double d4) {
        return d < this.c && this.a < d2 && d3 < this.d && this.b < d4;
    }

    public final boolean a(dg dgVar) {
        return a(dgVar.a, dgVar.c, dgVar.b, dgVar.d);
    }

    public final boolean b(dg dgVar) {
        return dgVar.a >= this.a && dgVar.c <= this.c && dgVar.b >= this.b && dgVar.d <= this.d;
    }
}
