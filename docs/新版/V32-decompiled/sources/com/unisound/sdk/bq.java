package com.unisound.sdk;

/* JADX INFO: loaded from: classes2.dex */
class bq implements by {
    final /* synthetic */ bp a;

    bq(bp bpVar) {
        this.a = bpVar;
    }

    @Override // com.unisound.sdk.by
    public void a(int i) {
        this.a.sendMessage(101, Integer.valueOf(i));
    }

    @Override // com.unisound.sdk.by
    public void a(String str) {
        this.a.sendMessage(100, str);
    }
}
