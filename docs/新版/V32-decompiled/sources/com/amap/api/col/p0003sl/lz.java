package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: UpdateStrategy.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class lz {
    lz c;

    protected abstract boolean c();

    public lz() {
    }

    public lz(lz lzVar) {
        this.c = lzVar;
    }

    public void a_(boolean z) {
        lz lzVar = this.c;
        if (lzVar != null) {
            lzVar.a_(z);
        }
    }

    public int a() {
        lz lzVar = this.c;
        return Math.min(Integer.MAX_VALUE, lzVar != null ? lzVar.a() : Integer.MAX_VALUE);
    }

    public void a_(int i) {
        lz lzVar = this.c;
        if (lzVar != null) {
            lzVar.a_(i);
        }
    }

    public final boolean d() {
        lz lzVar = this.c;
        if (lzVar != null ? lzVar.d() : true) {
            return c();
        }
        return false;
    }
}
