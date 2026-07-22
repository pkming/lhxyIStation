package com.amap.api.col.p0003sl;

import android.text.format.DateFormat;
import java.io.Serializable;

/* JADX INFO: compiled from: AmapCellWcdma.java */
/* JADX INFO: loaded from: classes2.dex */
public final class no extends nk implements Serializable {
    public int j;
    public int k;
    public int l;
    public int m;

    public no() {
        this.j = 0;
        this.k = 0;
        this.l = Integer.MAX_VALUE;
        this.m = Integer.MAX_VALUE;
    }

    public no(boolean z, boolean z2) {
        super(z, z2);
        this.j = 0;
        this.k = 0;
        this.l = Integer.MAX_VALUE;
        this.m = Integer.MAX_VALUE;
    }

    @Override // com.amap.api.col.p0003sl.nk
    /* JADX INFO: renamed from: a */
    public final nk clone() {
        no noVar = new no(this.h, this.i);
        noVar.a(this);
        noVar.j = this.j;
        noVar.k = this.k;
        noVar.l = this.l;
        noVar.m = this.m;
        return noVar;
    }

    @Override // com.amap.api.col.p0003sl.nk
    public final String toString() {
        return "AmapCellWcdma{lac=" + this.j + ", cid=" + this.k + ", psc=" + this.l + ", uarfcn=" + this.m + ", mcc='" + this.a + DateFormat.QUOTE + ", mnc='" + this.b + DateFormat.QUOTE + ", signalStrength=" + this.c + ", asuLevel=" + this.d + ", lastUpdateSystemMills=" + this.e + ", lastUpdateUtcMills=" + this.f + ", age=" + this.g + ", main=" + this.h + ", newApi=" + this.i + '}';
    }
}
