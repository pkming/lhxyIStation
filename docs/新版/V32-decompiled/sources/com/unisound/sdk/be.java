package com.unisound.sdk;

import com.unisound.client.ErrorCode;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
class be implements z {
    final /* synthetic */ bb a;

    be(bb bbVar) {
        this.a = bbVar;
    }

    @Override // com.unisound.sdk.d
    public void a() {
    }

    @Override // com.unisound.sdk.d
    public void a(int i) {
    }

    @Override // com.unisound.common.aj
    public void a(int i, int i2, Object obj) {
    }

    @Override // com.unisound.sdk.ad
    public void a(String str, boolean z) {
        int i;
        String strG;
        String strAk;
        Boolean boolValueOf;
        Object obj;
        Object obj2;
        String str2;
        this.a.ao = true;
        if (this.a.A != null) {
            ArrayList arrayList = new ArrayList();
            com.unisound.common.r.b("SpeechUnderstanderInterface : onResult -> result = " + str);
            if (str.contains("-changeable-")) {
                String strReplaceAll = str.replaceAll("-changeable-", "");
                i = this.a.H;
                strG = this.a.g(strReplaceAll, "asr_recongize");
                strAk = this.a.b.ak();
                boolValueOf = Boolean.valueOf(z);
                obj = null;
                obj2 = null;
                str2 = com.unisound.common.o.c;
            } else {
                this.a.P.append(this.a.g(str, "asr_recongize"));
                i = this.a.H;
                strG = this.a.g(str, "asr_recongize");
                strAk = this.a.b.ak();
                boolValueOf = Boolean.valueOf(z);
                obj = null;
                obj2 = null;
                str2 = "partial";
            }
            arrayList.add(com.unisound.common.o.a(i, str2, strG, strAk, boolValueOf, obj, obj2));
            this.a.ah.obtainMessage(1201, com.unisound.common.o.a(null, arrayList, null)).sendToTarget();
            if (z) {
                this.a.w.add(com.unisound.common.o.a(this.a.H, "full", new String(this.a.P), this.a.b.ak(), Boolean.valueOf(z), null, null));
                this.a.P.delete(0, this.a.P.length());
                if (this.a.B.v()) {
                    String strA = this.a.o(str).a();
                    this.a.x.add(strA);
                    if (strA == null) {
                        this.a.k(ErrorCode.ASR_SDK_NO_NLURESULT_ERROR);
                    }
                }
                this.a.ah.obtainMessage(1201, com.unisound.common.o.a(null, this.a.w, this.a.x)).sendToTarget();
                this.a.b.b(System.currentTimeMillis());
            }
        }
        this.a.ao = false;
    }

    @Override // com.unisound.sdk.z
    public void a(boolean z, byte[] bArr, int i, int i2) {
    }

    @Override // com.unisound.sdk.d
    public void b() {
    }

    @Override // com.unisound.sdk.z
    public void b(int i) {
        this.a.ao = true;
        bb bbVar = this.a;
        bbVar.G = bbVar.o.f();
        if (i != 0) {
            this.a.b.s(i);
            this.a.k(i);
            if (1 == this.a.H || this.a.F) {
                this.a.e.a(false);
            }
        }
        if (this.a.A != null) {
            this.a.E = true;
            this.a.ah.sendEmptyMessage(7);
            this.a.I();
        }
        com.unisound.common.r.a(this.a.l, i != 0, this.a.y, this.a.G, i, ErrorCode.toMessage(i));
        this.a.ao = false;
    }

    @Override // com.unisound.sdk.z
    public void c() {
    }

    @Override // com.unisound.sdk.z
    public void c(int i) {
        this.a.f(i);
    }

    @Override // com.unisound.sdk.z
    public void d() {
    }

    @Override // com.unisound.sdk.z
    public void e() {
    }

    @Override // com.unisound.sdk.z
    public void f() {
    }
}
