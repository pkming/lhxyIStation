package com.amap.api.col.p0003sl;

import android.text.format.DateFormat;
import java.io.Serializable;

/* JADX INFO: compiled from: AmapCellGsm.java */
/* JADX INFO: loaded from: classes2.dex */
public final class nm extends nk implements Serializable {
    public int j;
    public int k;
    public int l;
    public int m;
    public int n;
    public int o;

    public nm() {
        this.j = 0;
        this.k = 0;
        this.l = Integer.MAX_VALUE;
        this.m = Integer.MAX_VALUE;
        this.n = Integer.MAX_VALUE;
        this.o = Integer.MAX_VALUE;
    }

    public nm(boolean z, boolean z2) {
        super(z, z2);
        this.j = 0;
        this.k = 0;
        this.l = Integer.MAX_VALUE;
        this.m = Integer.MAX_VALUE;
        this.n = Integer.MAX_VALUE;
        this.o = Integer.MAX_VALUE;
    }

    @Override // com.amap.api.col.p0003sl.nk
    /* JADX INFO: renamed from: a */
    public final nk clone() {
        nm nmVar = new nm(this.h, this.i);
        nmVar.a(this);
        nmVar.j = this.j;
        nmVar.k = this.k;
        nmVar.l = this.l;
        nmVar.m = this.m;
        nmVar.n = this.n;
        nmVar.o = this.o;
        return nmVar;
    }

    @Override // com.amap.api.col.p0003sl.nk
    public final String toString() {
        return "AmapCellGsm{lac=" + this.j + ", cid=" + this.k + ", psc=" + this.l + ", arfcn=" + this.m + ", bsic=" + this.n + ", timingAdvance=" + this.o + ", mcc='" + this.a + DateFormat.QUOTE + ", mnc='" + this.b + DateFormat.QUOTE + ", signalStrength=" + this.c + ", asuLevel=" + this.d + ", lastUpdateSystemMills=" + this.e + ", lastUpdateUtcMills=" + this.f + ", age=" + this.g + ", main=" + this.h + ", newApi=" + this.i + '}';
    }
}
