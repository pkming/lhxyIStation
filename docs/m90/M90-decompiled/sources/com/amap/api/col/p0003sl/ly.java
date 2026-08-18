package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;

/* JADX INFO: compiled from: TimeUpdateStrategy.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ly extends lz {
    protected int a;
    protected long b;
    private String d;
    private Context e;

    public ly(Context context, int i, String str, lz lzVar) {
        super(lzVar);
        this.a = i;
        this.d = str;
        this.e = context;
    }

    @Override // com.amap.api.col.p0003sl.lz
    protected final boolean c() {
        if (this.b == 0) {
            String strA = ju.a(this.e, this.d);
            this.b = TextUtils.isEmpty(strA) ? 0L : Long.parseLong(strA);
        }
        return System.currentTimeMillis() - this.b >= ((long) this.a);
    }

    @Override // com.amap.api.col.p0003sl.lz
    public final void a_(boolean z) {
        super.a_(z);
        if (z) {
            String str = this.d;
            long jCurrentTimeMillis = System.currentTimeMillis();
            this.b = jCurrentTimeMillis;
            ju.a(this.e, str, String.valueOf(jCurrentTimeMillis));
        }
    }
}
