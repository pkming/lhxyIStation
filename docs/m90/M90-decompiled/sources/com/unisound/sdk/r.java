package com.unisound.sdk;

import android.os.Message;
import cn.yunzhisheng.asr.VAD;

/* JADX INFO: loaded from: classes2.dex */
class r implements am {
    final /* synthetic */ m a;

    private r(m mVar) {
        this.a = mVar;
    }

    /* synthetic */ r(m mVar, n nVar) {
        this(mVar);
    }

    @Override // com.unisound.sdk.am
    public void a() throws Throwable {
        this.a.i.b();
        this.a.k.c();
        this.a.m();
    }

    @Override // com.unisound.sdk.am
    public void a(int i) {
        this.a.i.a(i);
        this.a.a(i);
    }

    @Override // com.unisound.common.aj
    public void a(int i, int i2, Object obj) {
        this.a.a(i, i2, obj);
    }

    @Override // com.unisound.sdk.cf
    public void a(VAD vad) {
        com.unisound.common.r.c("FixRecognizerInterface onVADTimeout");
        this.a.i.e();
        this.a.a(vad);
        if (this.a.i.a() || this.a.h.a() || this.a.j.a()) {
            return;
        }
        this.a.stop();
    }

    @Override // com.unisound.sdk.am
    public void a(String str, boolean z) {
        this.a.i.a(str, z);
        this.a.b(str, z);
    }

    @Override // com.unisound.sdk.am
    public void a(String str, boolean z, int i) {
        this.a.a(str, z, i);
    }

    @Override // com.unisound.common.v
    public boolean a(Message message) {
        return this.a.a(message);
    }

    @Override // com.unisound.sdk.am
    public void b() {
        this.a.n();
    }

    @Override // com.unisound.sdk.cf
    public void b(int i) {
        this.a.i.b(i);
        this.a.b(i);
        this.a.k.a(i);
    }

    @Override // com.unisound.sdk.cf
    public void b(boolean z, byte[] bArr, int i, int i2) throws Throwable {
        this.a.a(z, bArr, i, i2);
    }

    @Override // com.unisound.sdk.am
    public void c() {
        this.a.i.c();
        this.a.k.b();
        this.a.o();
    }

    @Override // com.unisound.sdk.am
    public void c(int i) {
    }

    @Override // com.unisound.sdk.am
    public void d() {
        this.a.p();
    }

    @Override // com.unisound.sdk.am
    public void e() {
        this.a.q();
    }

    @Override // com.unisound.sdk.cf
    public void m() {
        this.a.i.d();
        this.a.j();
    }

    @Override // com.unisound.sdk.cf
    public void n() {
        this.a.k();
    }
}
