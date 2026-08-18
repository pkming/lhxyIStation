package com.amap.api.col.p0003sl;

import android.text.format.DateFormat;
import java.io.Serializable;

/* JADX INFO: compiled from: AmapCellCdma.java */
/* JADX INFO: loaded from: classes2.dex */
public final class nl extends nk implements Serializable {
    public int j;
    public int k;
    public int l;
    public int m;
    public int n;

    public nl() {
        this.j = 0;
        this.k = 0;
        this.l = 0;
    }

    public nl(boolean z, boolean z2) {
        super(z, z2);
        this.j = 0;
        this.k = 0;
        this.l = 0;
    }

    @Override // com.amap.api.col.p0003sl.nk
    /* JADX INFO: renamed from: a */
    public final nk clone() {
        nl nlVar = new nl(this.h, this.i);
        nlVar.a(this);
        nlVar.j = this.j;
        nlVar.k = this.k;
        nlVar.l = this.l;
        nlVar.m = this.m;
        nlVar.n = this.n;
        return nlVar;
    }

    @Override // com.amap.api.col.p0003sl.nk
    public final String toString() {
        return "AmapCellCdma{sid=" + this.j + ", nid=" + this.k + ", bid=" + this.l + ", latitude=" + this.m + ", longitude=" + this.n + ", mcc='" + this.a + DateFormat.QUOTE + ", mnc='" + this.b + DateFormat.QUOTE + ", signalStrength=" + this.c + ", asuLevel=" + this.d + ", lastUpdateSystemMills=" + this.e + ", lastUpdateUtcMills=" + this.f + ", age=" + this.g + ", main=" + this.h + ", newApi=" + this.i + '}';
    }
}
