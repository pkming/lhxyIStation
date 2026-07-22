package com.unisound.sdk;

import com.unisound.client.ErrorCode;
import com.unisound.client.SpeechConstants;

/* JADX INFO: loaded from: classes2.dex */
class aw implements bm {
    final /* synthetic */ au a;

    aw(au auVar) {
        this.a = auVar;
    }

    @Override // com.unisound.sdk.bm
    public void a() {
        au.sendEmptyMsg(this.a.n, 104);
    }

    @Override // com.unisound.sdk.bm
    public void a(int i) {
        au.sendEmptyMsg(this.a.n, 107);
        this.a.h = SpeechConstants.TTS_STATUS_END;
    }

    @Override // com.unisound.sdk.bm
    public void a(boolean z) {
        if (this.a.c != null) {
            this.a.c.b(z);
        }
    }

    @Override // com.unisound.sdk.bm
    public void b() {
        au.sendEmptyMsg(this.a.n, 105);
    }

    @Override // com.unisound.sdk.bm
    public void b(int i) {
        au.sendMsg(this.a.n, 212, ErrorCode.toJsonMessage(i));
    }

    @Override // com.unisound.sdk.bm
    public void c() {
        au.sendEmptyMsg(this.a.n, 106);
        this.a.h = SpeechConstants.TTS_STATUS_PLAYING;
    }

    @Override // com.unisound.sdk.bm
    public void d() {
        au.sendEmptyMsg(this.a.n, 110);
    }

    @Override // com.unisound.sdk.bm
    public void e() {
        au.sendEmptyMsg(this.a.n, 108);
        this.a.h = SpeechConstants.TTS_STATUS_PAUSE;
    }

    @Override // com.unisound.sdk.bm
    public void f() {
        au.sendEmptyMsg(this.a.n, 109);
    }

    @Override // com.unisound.sdk.bm
    public void g() {
        au.sendEmptyMsg(this.a.n, 111);
    }
}
