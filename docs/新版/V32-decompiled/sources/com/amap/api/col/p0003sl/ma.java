package com.amap.api.col.p0003sl;

import android.content.Context;

/* JADX INFO: compiled from: WiFiUplateStrategy.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ma extends lz {
    private Context a;
    private boolean b;

    public ma(Context context, boolean z) {
        this.b = false;
        this.a = context;
        this.b = z;
    }

    @Override // com.amap.api.col.p0003sl.lz
    protected final boolean c() {
        return ik.j(this.a) == 1 || this.b;
    }
}
