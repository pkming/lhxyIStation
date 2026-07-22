package com.unisound.sdk;

import com.unisound.client.ErrorCode;

/* JADX INFO: loaded from: classes2.dex */
class av implements bn {
    final /* synthetic */ au a;

    av(au auVar) {
        this.a = auVar;
    }

    @Override // com.unisound.sdk.bn
    public void a() {
        au.sendEmptyMsg(this.a.n, 102);
        this.a.h = 2501;
    }

    @Override // com.unisound.sdk.bn
    public void a(int i) {
        au.sendMsg(this.a.n, 210, ErrorCode.toJsonMessage(i));
    }

    @Override // com.unisound.sdk.bn
    public void a(byte[] bArr, int i) {
        byte[] bArr2 = new byte[i];
        System.arraycopy(bArr, 0, bArr2, 0, i);
        if (this.a.e != null) {
            this.a.e.a(bArr2);
        }
    }

    @Override // com.unisound.sdk.bn
    public void b() {
        if (this.a.e != null) {
            this.a.e.h();
            au.sendEmptyMsg(this.a.n, 103);
        }
    }

    @Override // com.unisound.sdk.bn
    public void c() {
        au.sendEmptyMsg(this.a.n, 112);
    }
}
