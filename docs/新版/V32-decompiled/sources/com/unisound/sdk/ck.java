package com.unisound.sdk;

import com.unisound.client.ErrorCode;
import com.unisound.client.SpeechConstants;
import com.unisound.client.VoicePrintRecognizerListener;
import com.unisound.common.VoiceprintResult;

/* JADX INFO: loaded from: classes2.dex */
class ck implements z {
    final /* synthetic */ ci a;

    ck(ci ciVar) {
        this.a = ciVar;
    }

    @Override // com.unisound.sdk.d
    public void a() {
        this.a.m.c();
        this.a.c.b();
        this.a.h.onEvent(4104, (int) System.currentTimeMillis());
    }

    @Override // com.unisound.sdk.d
    public void a(int i) {
        this.a.m.b(i);
        this.a.c.a(i);
        this.a.C = i;
        this.a.h.onEvent(SpeechConstants.VPR_EVENT_VOLUME_UPDATED, (int) System.currentTimeMillis());
    }

    @Override // com.unisound.common.aj
    public void a(int i, int i2, Object obj) {
        this.a.a(i, i2, obj);
    }

    @Override // com.unisound.sdk.ad
    public void a(String str, boolean z) {
        this.a.m.a(str, z);
        com.unisound.common.r.c("VoicePrintRecognizerInterface", "onResult : result = " + str + " , isLast = " + z);
        if (z) {
            str = this.a.a(str);
        }
        VoiceprintResult voiceprintResult = new VoiceprintResult(str);
        if (voiceprintResult.getStatus() == 1 || voiceprintResult.getStatus() == 200) {
            this.a.h.onResult(voiceprintResult.getStatus(), voiceprintResult);
            return;
        }
        VoicePrintRecognizerListener voicePrintRecognizerListener = this.a.h;
        int status = voiceprintResult.getStatus();
        ErrorCode errorCode = this.a.b;
        voicePrintRecognizerListener.onError(status, ErrorCode.toMessage(voiceprintResult.getStatus()));
    }

    @Override // com.unisound.sdk.z
    public void a(boolean z, byte[] bArr, int i, int i2) throws Throwable {
        this.a.a(z, bArr, i, i2);
    }

    @Override // com.unisound.sdk.d
    public void b() {
        this.a.m.e();
        this.a.d.b();
        if (!this.a.m.a() && !this.a.d.a()) {
            this.a.stop();
        }
        this.a.h.onEvent(SpeechConstants.VPR_VAD_TIMEOUT, (int) System.currentTimeMillis());
    }

    @Override // com.unisound.sdk.z
    public void b(int i) {
        this.a.l = com.unisound.common.al.idle;
        this.a.m.a(i);
        if (i != 0) {
            VoicePrintRecognizerListener voicePrintRecognizerListener = this.a.h;
            ErrorCode errorCode = this.a.b;
            voicePrintRecognizerListener.onError(SpeechConstants.VPR_ERROR, ErrorCode.toMessage(i));
        }
        this.a.h.onEvent(4104, (int) System.currentTimeMillis());
    }

    @Override // com.unisound.sdk.z
    public void c() {
        this.a.m.c();
        this.a.c.b();
        this.a.h.onEvent(SpeechConstants.VPR_EVENT_RECORDING_START, (int) System.currentTimeMillis());
    }

    @Override // com.unisound.sdk.z
    public void c(int i) {
    }

    @Override // com.unisound.sdk.z
    public void d() {
    }

    @Override // com.unisound.sdk.z
    public void e() throws Throwable {
        this.a.m.b();
        this.a.c.c();
        this.a.e();
        this.a.h.onEvent(SpeechConstants.VPR_EVENT_RECORDING_STOP, (int) System.currentTimeMillis());
    }

    @Override // com.unisound.sdk.z
    public void f() {
        this.a.m.d();
        this.a.h.onEvent(SpeechConstants.VPR_EVENT_SPEECH_START, (int) System.currentTimeMillis());
    }
}
