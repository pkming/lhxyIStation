package com.unisound.sdk;

/* JADX INFO: loaded from: classes2.dex */
class cj extends Thread {
    final /* synthetic */ ci a;

    cj(ci ciVar) {
        this.a = ciVar;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        ci ciVar = this.a;
        ciVar.a(ciVar.p, this.a.q);
    }
}
