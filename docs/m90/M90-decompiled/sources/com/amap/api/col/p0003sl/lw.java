package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;

/* JADX INFO: compiled from: MobileUpdateStrategy.java */
/* JADX INFO: loaded from: classes2.dex */
public final class lw extends lz {
    private Context b;
    private boolean d;
    private int e;
    private int f;
    private String a = "iKey";
    private int g = 0;

    public lw(Context context, boolean z, int i, int i2, String str) {
        a(context, z, i, i2, str, 0);
    }

    public lw(Context context, boolean z, int i, int i2, String str, int i3) {
        a(context, z, i, i2, str, i3);
    }

    private void a(Context context, boolean z, int i, int i2, String str, int i3) {
        this.b = context;
        this.d = z;
        this.e = i;
        this.f = i2;
        this.a = str;
        this.g = i3;
    }

    @Override // com.amap.api.col.p0003sl.lz
    protected final boolean c() {
        if (ik.j(this.b) == 1) {
            return true;
        }
        if (!this.d) {
            return false;
        }
        String strA = ju.a(this.b, this.a);
        if (TextUtils.isEmpty(strA)) {
            return true;
        }
        String[] strArrSplit = strA.split("\\|");
        if (strArrSplit != null && strArrSplit.length >= 2) {
            return !it.a(System.currentTimeMillis(), "yyyyMMdd").equals(strArrSplit[0]) || Integer.parseInt(strArrSplit[1]) < this.f;
        }
        ju.b(this.b, this.a);
        return true;
    }

    @Override // com.amap.api.col.p0003sl.lz
    public final int a() {
        int i;
        int i2 = Integer.MAX_VALUE;
        if ((ik.j(this.b) != 1 && (i = this.e) > 0) || ((i = this.g) > 0 && i < Integer.MAX_VALUE)) {
            i2 = i;
        }
        return this.c != null ? Math.max(i2, this.c.a()) : i2;
    }

    @Override // com.amap.api.col.p0003sl.lz
    public final void a_(int i) {
        if (ik.j(this.b) == 1) {
            return;
        }
        String strA = it.a(System.currentTimeMillis(), "yyyyMMdd");
        String strA2 = ju.a(this.b, this.a);
        if (!TextUtils.isEmpty(strA2)) {
            String[] strArrSplit = strA2.split("\\|");
            if (strArrSplit == null || strArrSplit.length < 2) {
                ju.b(this.b, this.a);
            } else if (strA.equals(strArrSplit[0])) {
                i += Integer.parseInt(strArrSplit[1]);
            }
        }
        ju.a(this.b, this.a, strA + "|" + i);
    }
}
