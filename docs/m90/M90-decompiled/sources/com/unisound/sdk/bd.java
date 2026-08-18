package com.unisound.sdk;

/* JADX INFO: loaded from: classes2.dex */
class bd extends Thread {
    final /* synthetic */ bb a;

    bd(bb bbVar) {
        this.a = bbVar;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        bb bbVar = this.a;
        bbVar.h(bbVar.y, this.a.z);
    }
}
