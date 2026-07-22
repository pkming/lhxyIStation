package com.amap.api.col.p0003sl;

import java.io.File;

/* JADX INFO: compiled from: FileNumUpdateStrategy.java */
/* JADX INFO: loaded from: classes2.dex */
public final class lv extends lz {
    private int a;
    private String b;

    public lv(String str, lz lzVar) {
        super(lzVar);
        this.a = 30;
        this.b = str;
    }

    @Override // com.amap.api.col.p0003sl.lz
    protected final boolean c() {
        return a(this.b) >= this.a;
    }

    private static int a(String str) {
        try {
            File file = new File(str);
            if (file.exists()) {
                return file.list().length;
            }
            return 0;
        } catch (Throwable th) {
            jw.c(th, "fus", "gfn");
            return 0;
        }
    }
}
