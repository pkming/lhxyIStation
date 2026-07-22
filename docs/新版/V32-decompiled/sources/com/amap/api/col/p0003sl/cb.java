package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: AbstractCityStateImp.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class cb implements cf {
    protected int a;
    protected ax b;

    public cb(int i, ax axVar) {
        this.a = i;
        this.b = axVar;
    }

    public final int b() {
        return this.a;
    }

    public final boolean a(cb cbVar) {
        return cbVar.b() == b();
    }

    public final void b(cb cbVar) {
        new StringBuilder().append(b()).append(" ==> ").append(cbVar.b()).append("   ").append(getClass()).append("==>").append(cbVar.getClass());
    }

    public void c() {
        new StringBuilder("Wrong call start()  State: ").append(b()).append("  ").append(getClass());
    }

    public void d() {
        new StringBuilder("Wrong call continueDownload()  State: ").append(b()).append("  ").append(getClass());
    }

    public void e() {
        new StringBuilder("Wrong call pause()  State: ").append(b()).append("  ").append(getClass());
    }

    public void a() {
        new StringBuilder("Wrong call delete()  State: ").append(b()).append("  ").append(getClass());
    }

    public void a(int i) {
        new StringBuilder("Wrong call fail()  State: ").append(b()).append("  ").append(getClass());
    }

    public void f() {
        new StringBuilder("Wrong call hasNew()  State: ").append(b()).append("  ").append(getClass());
    }

    public void g() {
        new StringBuilder("Wrong call complete()  State: ").append(b()).append("  ").append(getClass());
    }
}
