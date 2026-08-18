package com.unisound.sdk;

import android.os.Looper;
import android.os.Message;
import cn.yunzhisheng.asr.VAD;
import cn.yunzhisheng.asrfix.JniAsrFix;
import com.unisound.client.ErrorCode;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class k extends com.unisound.common.u implements ag, ap {
    public static final int a = 50;
    private static final int c = 1;
    private static final int d = 2;
    private static final int e = 3;
    private static final int f = 4;
    private static final int g = 5;
    private static final int h = 6;
    private static final int i = 11;
    private static final int j = 12;
    private static final int k = 13;
    private static final int l = 14;
    private static final int m = 15;
    private static final int n = 20;
    private static final int o = 21;
    private static final int p = 22;
    private static final int q = 23;
    private static final int r = 24;
    private static JniAsrFix x;
    private boolean A;
    private Looper B;
    am b;
    private w s;
    private x t;
    private af u;
    private i v;
    private ae w;
    private u y;
    private boolean z;

    static {
        System.loadLibrary("uscasr");
    }

    public k() {
        this.s = null;
        this.t = null;
        this.u = null;
        this.v = null;
        this.w = new ae();
        this.y = null;
        this.z = true;
        this.A = false;
    }

    public k(Looper looper) {
        super(looper);
        this.s = null;
        this.t = null;
        this.u = null;
        this.v = null;
        this.w = new ae();
        this.y = null;
        this.z = true;
        this.A = false;
        this.B = looper;
    }

    private void A() {
        am amVar = this.b;
        if (amVar != null) {
            amVar.d();
        }
    }

    private void c(String str) {
        com.unisound.common.r.e(str);
    }

    private void d(String str) {
        com.unisound.common.r.c(str);
    }

    private void e(int i2) {
        am amVar = this.b;
        if (amVar != null) {
            amVar.a(i2);
        }
    }

    private void e(boolean z) {
        i iVar = this.v;
        if (iVar != null) {
            iVar.a(z);
            this.v = null;
        }
    }

    private void z() {
        w wVar = this.s;
        if (wVar != null) {
            wVar.d();
        }
    }

    public int a(String str, String str2, String str3) {
        return x.a(str, str2, this.y, str3);
    }

    public String a(String str, String str2) {
        return x.a(str, str2);
    }

    public void a() {
        w wVar = this.s;
        if (wVar != null) {
            wVar.k();
            this.s = null;
        }
    }

    @Override // com.unisound.sdk.ag
    public void a(int i2) {
        w wVar = this.s;
        if (wVar != null) {
            wVar.d();
        }
        sendMessage(13, Integer.valueOf(i2));
    }

    @Override // com.unisound.common.aj
    public void a(int i2, int i3, Object obj) {
        am amVar = this.b;
        if (amVar != null) {
            amVar.a(i2, i3, obj);
        }
    }

    @Override // com.unisound.sdk.cf
    public void a(VAD vad) {
        w wVar = this.s;
        if (wVar == null || !wVar.e()) {
            if (this.y.w()) {
                this.v.e();
            } else {
                sendMessage(21, vad);
            }
        }
    }

    public void a(af afVar) {
        this.u = afVar;
    }

    public void a(am amVar) {
        this.b = amVar;
        setMessageLisenter(amVar);
    }

    public void a(s sVar) {
        x.a(sVar);
    }

    public void a(u uVar) {
        this.y = uVar;
    }

    public void a(Boolean bool) {
        x.a(bool);
    }

    protected void a(String str) {
        com.unisound.common.r.b(str);
    }

    public void a(String str, w wVar, x xVar) {
        i iVar = this.v;
        if (iVar != null) {
            iVar.a(true);
            this.v = null;
        }
        this.w.a();
        if (this.z) {
            if (!this.A) {
                sendMessage(13, Integer.valueOf(ErrorCode.ASR_SDK_FIX_RECOGNIZER_NO_INIT));
                return;
            }
            if (x.h()) {
                h hVar = new h(x, str, this.y, xVar);
                this.v = hVar;
                hVar.a(this);
                this.v.a(this.u);
                this.v.setName("usc_fix_thread");
                this.v.setPriority(10);
                this.v.start();
                com.unisound.common.r.f("Recognition Thread Start");
            } else {
                sendMessage(13, Integer.valueOf(ErrorCode.ASR_SDK_FIX_RECOGNIZER_NO_INIT));
                if (this.b == null) {
                    return;
                }
            }
        }
        this.t = xVar;
        xVar.setName("usc_vad_thread");
        this.t.setPriority(10);
        this.t.start();
        this.s = wVar;
        wVar.setName("usc_record_thread");
        this.s.start();
        com.unisound.common.r.f("Recording Thread Start");
        am amVar = this.b;
        if (amVar != null) {
            amVar.e();
        }
    }

    @Override // com.unisound.sdk.ag
    public void a(String str, boolean z) {
        ah ahVar = new ah();
        ahVar.a = str;
        ahVar.b = z;
        sendMessage(11, ahVar);
    }

    @Override // com.unisound.sdk.ag
    public void a(String str, boolean z, int i2) {
        ah ahVar = new ah();
        ahVar.a = str;
        ahVar.b = z;
        ahVar.c = i2;
        sendMessage(20, ahVar);
    }

    public void a(boolean z) {
        JniAsrFix jniAsrFix;
        if (z && (jniAsrFix = x) != null) {
            jniAsrFix.b();
        }
        com.unisound.common.r.e("FixRecognizer Cancel and wait end +" + (this.v == null));
        a();
        x xVar = this.t;
        if (xVar != null) {
            xVar.g();
            this.t = null;
        }
        i iVar = this.v;
        if (iVar != null) {
            iVar.a(true);
            this.v = null;
        }
        removeSendMessage();
        removeMessages(11);
    }

    @Override // com.unisound.sdk.ap
    public void a(boolean z, byte[] bArr, int i2, int i3) {
        this.t.a(bArr);
    }

    @Override // com.unisound.common.u
    public boolean a(Message message) {
        synchronized (this.B) {
            int i2 = message.what;
            switch (i2) {
                case 1:
                    if (!((Boolean) message.obj).booleanValue()) {
                        e();
                        e(ErrorCode.FAILED_START_RECORDING);
                    } else {
                        am amVar = this.b;
                        if (amVar != null) {
                            amVar.c();
                        }
                    }
                    break;
                case 2:
                    am amVar2 = this.b;
                    if (amVar2 != null) {
                        amVar2.a();
                    }
                    f();
                    break;
                case 3:
                    e();
                    e(ErrorCode.RECORDING_EXCEPTION);
                    break;
                case 4:
                    A();
                    break;
                case 5:
                    c();
                    break;
                case 6:
                    d();
                    break;
                default:
                    switch (i2) {
                        case 11:
                            if (this.b != null) {
                                ah ahVar = (ah) message.obj;
                                this.b.a(ahVar.a, ahVar.b);
                            }
                            break;
                        case 12:
                            e(0);
                            break;
                        case 13:
                            e(((Integer) message.obj).intValue());
                            break;
                        case 14:
                            am amVar3 = this.b;
                            if (amVar3 != null) {
                                amVar3.b();
                            }
                            break;
                        case 15:
                            z();
                            e(ErrorCode.RECOGNITION_EXCEPTION);
                            break;
                        default:
                            switch (i2) {
                                case 20:
                                    if (this.b != null) {
                                        ah ahVar2 = (ah) message.obj;
                                        this.b.a(ahVar2.a, ahVar2.b, ahVar2.c);
                                    }
                                    break;
                                case 21:
                                    am amVar4 = this.b;
                                    if (amVar4 != null) {
                                        amVar4.a((VAD) message.obj);
                                    }
                                    break;
                                case 22:
                                    if (this.b != null) {
                                        this.b.b(((Integer) message.obj).intValue());
                                    }
                                    break;
                                case 23:
                                    e();
                                    z();
                                    e(ErrorCode.ASR_FIXENGINE_MAX_SPEECH_TIMEOUT);
                                    break;
                                case 24:
                                    if (this.b != null) {
                                        ((Integer) message.obj).intValue();
                                    }
                                    break;
                                default:
                                    return false;
                            }
                            break;
                    }
                    break;
            }
            return true;
        }
    }

    public boolean a(String str, String str2, boolean z, String str3, String str4) {
        if (x.h() && !z) {
            a(str2, str3, str4);
        } else if ("init_asr" == str3) {
            int iA = x.a(str, str2, str3, this.y);
            if (iA != 0) {
                c("jac.init path=" + str + ":" + str2 + " error:" + iA);
                return false;
            }
            x.a(0, 1);
            x.a(1, 3000);
            x.a(6, 8);
            x.a(9, this.y.ab());
        } else {
            com.unisound.common.r.e("FixRecognizer Engine is not init, wrong cmd=" + str3);
            sendMessage(13, Integer.valueOf(ErrorCode.ASR_SDK_FIX_RECOGNIZER_NO_INIT));
        }
        return true;
    }

    public void b() {
        i iVar;
        z();
        if (this.s == null && (iVar = this.v) != null && !iVar.g()) {
            sendMessage(2);
        }
        x xVar = this.t;
        if (xVar != null) {
            xVar.b();
        }
    }

    @Override // com.unisound.sdk.cf
    public void b(int i2) {
        sendMessage(22, Integer.valueOf(i2));
    }

    @Override // com.unisound.sdk.ag
    public void b(String str) {
    }

    @Override // com.unisound.sdk.ap
    public void b(boolean z) {
        this.w.a();
        sendMessage(1, Boolean.valueOf(z));
    }

    @Override // com.unisound.sdk.cf
    public void b(boolean z, byte[] bArr, int i2, int i3) {
        if (this.w.a(bArr, i2, i3)) {
            sendMessage(4);
        }
        boolean z2 = false;
        if (z && this.w.b() && this.v != null && bArr != null && bArr.length > 0) {
            synchronized (this.t) {
                this.v.a(bArr);
                this.t.a(false);
            }
        }
        am amVar = this.b;
        if (amVar != null) {
            if (!ae.a) {
                amVar.b(z, bArr, i2, i3);
                return;
            }
            if (this.w.b() && z) {
                z2 = true;
            }
            amVar.b(z2, bArr, i2, i3);
        }
    }

    public int c(int i2) {
        JniAsrFix jniAsrFix = x;
        if (jniAsrFix != null) {
            return jniAsrFix.b(i2);
        }
        return -1;
    }

    protected void c() {
        am amVar = this.b;
        if (amVar != null) {
            amVar.m();
        }
    }

    public void c(boolean z) {
        this.z = z;
    }

    public int d(int i2) {
        JniAsrFix jniAsrFix = x;
        if (jniAsrFix != null) {
            return jniAsrFix.c(i2);
        }
        return -1;
    }

    protected void d() {
        am amVar = this.b;
        if (amVar != null) {
            amVar.n();
        }
    }

    public void d(boolean z) {
        this.A = z;
    }

    protected void e() {
        i iVar = this.v;
        if (iVar != null) {
            iVar.c();
        }
    }

    public void f() {
        i iVar = this.v;
        if (iVar != null) {
            iVar.d();
        }
    }

    protected void g() {
        w wVar = this.s;
        if (wVar != null) {
            wVar.g();
        }
    }

    @Override // com.unisound.sdk.ag
    public void h() {
        sendMessage(12);
    }

    @Override // com.unisound.sdk.ap
    public void i() {
        sendMessage(2);
        this.s = null;
    }

    @Override // com.unisound.sdk.ap
    public void j() {
        sendMessage(3);
    }

    @Override // com.unisound.sdk.ag
    public void k() {
    }

    @Override // com.unisound.sdk.ag
    public void l() {
        sendMessage(23);
    }

    @Override // com.unisound.sdk.cf
    public void m() {
        sendMessage(5);
    }

    @Override // com.unisound.sdk.cf
    public void n() {
        sendMessage(6);
    }

    public void o() {
        synchronized (x) {
            w wVar = this.s;
            if (wVar != null) {
                wVar.d();
            }
            x.d();
            w wVar2 = this.s;
            if (wVar2 != null) {
                wVar2.k();
            }
            x xVar = this.t;
            if (xVar != null) {
                xVar.g();
            }
            e(true);
            x.i();
            com.unisound.common.r.c("jac.unLoad();");
        }
    }

    public boolean p() {
        return x.h();
    }

    public boolean q() {
        i iVar = this.v;
        if (iVar != null) {
            return iVar.isAlive();
        }
        return false;
    }

    public void r() {
        x = JniAsrFix.a();
    }

    public int s() {
        JniAsrFix jniAsrFix = x;
        if (jniAsrFix != null) {
            return jniAsrFix.k();
        }
        return 1;
    }

    public List<Integer> t() {
        JniAsrFix jniAsrFix = x;
        if (jniAsrFix != null) {
            return jniAsrFix.m();
        }
        return null;
    }

    public int u() {
        JniAsrFix jniAsrFix = x;
        if (jniAsrFix != null) {
            return jniAsrFix.n();
        }
        return -1;
    }

    public String v() {
        JniAsrFix jniAsrFix = x;
        return jniAsrFix != null ? jniAsrFix.o() : "";
    }

    public String w() {
        JniAsrFix jniAsrFix = x;
        return jniAsrFix != null ? jniAsrFix.p() : "";
    }

    public int x() {
        JniAsrFix jniAsrFix = x;
        if (jniAsrFix != null) {
            return jniAsrFix.r();
        }
        return -1;
    }

    public int y() {
        JniAsrFix jniAsrFix = x;
        if (jniAsrFix != null) {
            return jniAsrFix.s();
        }
        return -1;
    }
}
