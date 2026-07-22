package com.amap.api.col.p0003sl;

import java.io.Serializable;

/* JADX INFO: compiled from: AmapCell.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class nk implements Serializable {
    public String a;
    public String b;
    public int c;
    public int d;
    public long e;
    public long f;
    public int g;
    public boolean h;
    public boolean i;

    @Override // 
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public abstract nk clone();

    public nk() {
        this.a = "";
        this.b = "";
        this.c = 99;
        this.d = Integer.MAX_VALUE;
        this.e = 0L;
        this.f = 0L;
        this.g = 0;
        this.i = true;
    }

    public nk(boolean z, boolean z2) {
        this.a = "";
        this.b = "";
        this.c = 99;
        this.d = Integer.MAX_VALUE;
        this.e = 0L;
        this.f = 0L;
        this.g = 0;
        this.i = true;
        this.h = z;
        this.i = z2;
    }

    public final int b() {
        return a(this.a);
    }

    public final int c() {
        return a(this.b);
    }

    private static int a(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            nu.a(e);
            return 0;
        }
    }

    public final void a(nk nkVar) {
        this.a = nkVar.a;
        this.b = nkVar.b;
        this.c = nkVar.c;
        this.d = nkVar.d;
        this.e = nkVar.e;
        this.f = nkVar.f;
        this.g = nkVar.g;
        this.h = nkVar.h;
        this.i = nkVar.i;
    }

    public String toString() {
        return "AmapCell{mcc=" + this.a + ", mnc=" + this.b + ", signalStrength=" + this.c + ", asulevel=" + this.d + ", lastUpdateSystemMills=" + this.e + ", lastUpdateUtcMills=" + this.f + ", age=" + this.g + ", main=" + this.h + ", newapi=" + this.i + '}';
    }
}
