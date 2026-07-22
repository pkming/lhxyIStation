package com.unisound.sdk;

/* JADX INFO: loaded from: classes2.dex */
public class bt {
    bu a;
    private Object[] c;
    private boolean d = false;
    bv b = null;

    public bt(bu buVar) {
        this.a = buVar;
    }

    public void a() {
        b();
        bv bvVar = new bv(this, this.a);
        this.b = bvVar;
        Object[] objArr = this.c;
        if (objArr != null) {
            bvVar.execute(objArr);
        }
    }

    public void a(Object... objArr) {
        this.c = objArr;
    }

    public void b() {
        bv bvVar = this.b;
        if (bvVar != null) {
            bvVar.a();
            this.b = null;
        }
    }

    public boolean c() {
        return this.d;
    }
}
