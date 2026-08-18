package com.unisound.sdk;

import cn.yunzhisheng.tts.offline.lib.YzsTts;
import com.unisound.client.ErrorCode;

/* JADX INFO: loaded from: classes2.dex */
public class bi extends bh {
    private String c;
    private YzsTts d;
    private bn e;
    private bk f;
    private boolean g;

    public bi(String str, bk bkVar) {
        super(bkVar.q().booleanValue(), bkVar.l());
        this.d = YzsTts.b();
        this.g = false;
        this.c = str;
        this.f = bkVar;
    }

    private void a(float f) {
        this.d.a(f);
    }

    private void a(Boolean bool) {
        this.d.b(bool);
    }

    private void a(byte[] bArr, int i) {
        bn bnVar = this.e;
        if (bnVar != null) {
            bnVar.a(bArr, i);
        }
    }

    private void b(float f) {
        this.d.b(f);
    }

    private void c(float f) {
        this.d.d(f);
    }

    private void c(int i) {
        bn bnVar = this.e;
        if (bnVar != null) {
            bnVar.a(i);
        }
    }

    private void d(float f) {
        this.d.c(f);
    }

    private void d(int i) {
        this.d.a(i);
    }

    private void e(int i) {
        this.d.b(i);
    }

    private void f(int i) {
    }

    private boolean g(int i) {
        return this.d.d(i);
    }

    private void i() {
        bn bnVar = this.e;
        if (bnVar != null) {
            bnVar.a();
        }
    }

    private void j() {
        bn bnVar = this.e;
        if (bnVar != null) {
            bnVar.b();
        }
    }

    private void k() {
        bn bnVar = this.e;
        if (bnVar != null) {
            bnVar.c();
        }
    }

    protected void a(bk bkVar) {
        this.f = bkVar;
    }

    public void a(bn bnVar) {
        this.e = bnVar;
    }

    @Override // com.unisound.sdk.bh
    public void b() {
        super.b();
        YzsTts yzsTts = this.d;
        if (yzsTts != null) {
            yzsTts.e();
        }
    }

    public void b(int i) {
        if (isAlive()) {
            YzsTts yzsTts = this.d;
            if (yzsTts != null) {
                yzsTts.e();
            }
            try {
                super.join(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void b(boolean z) {
        this.g = z;
    }

    public void g() {
        YzsTts yzsTts = this.d;
        if (yzsTts != null) {
            yzsTts.c();
            this.d = null;
        }
        k();
    }

    public void h() {
        a((bn) null);
    }

    @Override // com.unisound.common.f, java.lang.Thread, java.lang.Runnable
    public void run() {
        super.run();
        com.unisound.common.r.b("TTSOfflineSynthesizerThread run(): synthesizer begin");
        try {
        } catch (Exception e) {
            e.printStackTrace();
            com.unisound.common.r.e("TTSOfflineSynthesizerThread run(): Exception error");
        }
        if (this.d.a() == 0) {
            c(ErrorCode.TTS_ERROR_OFFLINE_ENGINE_NOT_INIT);
            com.unisound.common.r.e("TTSOfflineSynthesizerThread run(): 离线tts引擎未初始化，请确认执行init并接收init回调！ ");
            return;
        }
        int iP = this.f.p();
        if (iP != 0) {
            com.unisound.common.r.b("TTSOfflineSynthesizerThread run(): _LogLevel=" + iP);
            a(iP);
        }
        float fT = this.f.t();
        if (fT != 50.0f) {
            com.unisound.common.r.b("TTSOfflineSynthesizerThread run(): _VoiceSpeed=" + fT);
            b(fT);
        }
        float fU = this.f.u();
        if (fU != 50.0f) {
            com.unisound.common.r.b("TTSOfflineSynthesizerThread run(): _VoicePitch=" + fU);
            c(fU);
        }
        float fV = this.f.v();
        if (fV != 50.0f) {
            com.unisound.common.r.b("TTSOfflineSynthesizerThread run(): _VoiceVolume=" + fV);
            d(fV);
        }
        boolean zBooleanValue = this.f.m().booleanValue();
        if (zBooleanValue) {
            com.unisound.common.r.b("TTSOfflineSynthesizerThread run(): _ReadEnglishInPinyin=" + zBooleanValue);
            a(Boolean.valueOf(zBooleanValue));
        }
        int iN = this.f.n();
        if (iN != 100) {
            com.unisound.common.r.b("TTSOfflineSynthesizerThread run(): _FrontSilence=" + iN);
            d(iN);
        }
        int iO = this.f.o();
        if (iO != 100) {
            com.unisound.common.r.b("TTSOfflineSynthesizerThread run(): _BackSilence=" + iO);
            e(iO);
        }
        YzsTts yzsTts = this.d;
        if (yzsTts.setText(yzsTts.a(), this.c) != 0) {
            c(ErrorCode.TTS_ERROR_OFFLINE_SYNTHESIZER_SET_TEXT);
            com.unisound.common.r.e("TTSOfflineSynthesizerThread run(): setText error ");
            return;
        }
        byte[] bArr = new byte[6400];
        this.d.a((Boolean) true);
        i();
        int iA = 1;
        while (iA != 0 && !a()) {
            com.unisound.common.r.f("TTSOfflineSythesizer run : receiveSamples before");
            YzsTts yzsTts2 = this.d;
            iA = yzsTts2.a(yzsTts2.a(), bArr);
            com.unisound.common.r.f("TTSOfflineSythesizer run : receiveSamples after");
            if (iA > 1) {
                a(bArr, iA);
            }
            while (!a() && iA != 0 && this.g) {
                Thread.sleep(50L);
            }
        }
        j();
        this.d.a((Boolean) false);
        com.unisound.common.r.b("TTSOfflineSynthesizerThread run(): synthesizer end");
    }
}
