package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: AMapRecallLogUpdateStrategy.java */
/* JADX INFO: loaded from: classes2.dex */
public final class je extends lz {
    private static int g = 10000000;
    protected int a;
    protected long b;
    private boolean d;
    private boolean e;
    private int f;
    private long h;

    @Override // com.amap.api.col.p0003sl.lz
    public final int a() {
        return 320000;
    }

    public je(boolean z, lz lzVar, long j, int i) {
        super(lzVar);
        this.d = false;
        this.e = false;
        this.f = g;
        this.h = 0L;
        this.d = z;
        this.a = 600000;
        this.h = j;
        this.f = i;
    }

    public final void a(boolean z) {
        this.e = z;
    }

    public final void a(int i) {
        if (i <= 0) {
            return;
        }
        this.h += (long) i;
    }

    public final long b() {
        return this.h;
    }

    @Override // com.amap.api.col.p0003sl.lz
    protected final boolean c() {
        if (this.e && this.h <= this.f) {
            return true;
        }
        if (!this.d || this.h >= this.f) {
            return false;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis - this.b < this.a) {
            return false;
        }
        this.b = jCurrentTimeMillis;
        return true;
    }
}
