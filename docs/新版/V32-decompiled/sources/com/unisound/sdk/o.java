package com.unisound.sdk;

/* JADX INFO: loaded from: classes2.dex */
class o implements com.unisound.common.d {
    final /* synthetic */ m a;

    o(m mVar) {
        this.a = mVar;
    }

    @Override // com.unisound.common.d
    public void a() {
        this.a.start();
    }

    @Override // com.unisound.common.d
    public void b() {
        this.a.stop();
    }

    @Override // com.unisound.common.d
    public void c() {
        this.a.cancel();
    }
}
