package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: GlShaderManager.java */
/* JADX INFO: loaded from: classes2.dex */
public final class cq {
    private a a;
    private b b;
    private b c;

    /* JADX INFO: compiled from: GlShaderManager.java */
    public static class a extends cp {
    }

    /* JADX INFO: compiled from: GlShaderManager.java */
    public static class b extends cp {
    }

    public final synchronized void a() {
        a aVar = this.a;
        if (aVar != null) {
            aVar.a();
            this.a = null;
        }
        b bVar = this.b;
        if (bVar != null) {
            bVar.a();
            this.b = null;
        }
        b bVar2 = this.c;
        if (bVar2 != null) {
            bVar2.a();
            this.c = null;
        }
    }
}
