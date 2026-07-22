package com.amap.api.col.p0003sl;

import android.text.format.DateFormat;
import java.io.Serializable;

/* JADX INFO: compiled from: AmapCellLte.java */
/* JADX INFO: loaded from: classes2.dex */
public final class nn extends nk implements Serializable {
    public int j;
    public int k;
    public int l;
    public int m;
    public int n;

    public nn() {
        this.j = 0;
        this.k = 0;
        this.l = Integer.MAX_VALUE;
        this.m = Integer.MAX_VALUE;
        this.n = Integer.MAX_VALUE;
    }

    public nn(boolean z) {
        super(z, true);
        this.j = 0;
        this.k = 0;
        this.l = Integer.MAX_VALUE;
        this.m = Integer.MAX_VALUE;
        this.n = Integer.MAX_VALUE;
    }

    @Override // com.amap.api.col.p0003sl.nk
    /* JADX INFO: renamed from: a */
    public final nk clone() {
        nn nnVar = new nn(this.h);
        nnVar.a(this);
        nnVar.j = this.j;
        nnVar.k = this.k;
        nnVar.l = this.l;
        nnVar.m = this.m;
        nnVar.n = this.n;
        return nnVar;
    }

    @Override // com.amap.api.col.p0003sl.nk
    public final String toString() {
        return "AmapCellLte{tac=" + this.j + ", ci=" + this.k + ", pci=" + this.l + ", earfcn=" + this.m + ", timingAdvance=" + this.n + ", mcc='" + this.a + DateFormat.QUOTE + ", mnc='" + this.b + DateFormat.QUOTE + ", signalStrength=" + this.c + ", asuLevel=" + this.d + ", lastUpdateSystemMills=" + this.e + ", lastUpdateUtcMills=" + this.f + ", age=" + this.g + ", main=" + this.h + ", newApi=" + this.i + '}';
    }
}
