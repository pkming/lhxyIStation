package com.unisound.sdk;

/* JADX INFO: loaded from: classes2.dex */
public class a {
    private com.unisound.common.b a = null;
    private com.unisound.common.d b = null;

    public void a(int i) {
        com.unisound.common.b bVar = this.a;
        if (bVar != null) {
            bVar.b(i);
        }
    }

    public void a(com.unisound.common.b bVar) {
        this.a = bVar;
        if (bVar != null) {
            bVar.a(this.b);
        }
    }

    public void a(com.unisound.common.d dVar) {
        com.unisound.common.b bVar = this.a;
        if (bVar != null) {
            bVar.a(dVar);
        }
        this.b = dVar;
    }

    public void a(String str, boolean z) {
        com.unisound.common.b bVar = this.a;
        if (bVar != null) {
            bVar.a(str, z);
        }
    }

    public boolean a() {
        return this.a != null;
    }

    public void b() {
        com.unisound.common.b bVar = this.a;
        if (bVar != null) {
            bVar.f();
        }
    }

    public void b(int i) {
        com.unisound.common.b bVar = this.a;
        if (bVar != null) {
            bVar.a(i);
        }
    }

    public void c() {
        com.unisound.common.b bVar = this.a;
        if (bVar != null) {
            bVar.g();
        }
    }

    public void d() {
        com.unisound.common.b bVar = this.a;
        if (bVar != null) {
            bVar.h();
        }
    }

    public void e() {
        com.unisound.common.b bVar = this.a;
        if (bVar != null) {
            bVar.d();
        }
    }

    public void f() {
        com.unisound.common.b bVar = this.a;
        if (bVar != null) {
            bVar.i();
        }
    }

    public void g() {
        com.unisound.common.b bVar = this.a;
        if (bVar != null) {
            bVar.e();
        }
    }
}
