package com.unisound.sdk;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;
import cn.yunzhisheng.asr.VAD;
import com.unisound.client.ErrorCode;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class aj extends com.unisound.common.u implements ag, ap, ce {
    private static final int A = 16;
    private static final int B = 21;
    private static final int C = 22;
    private static final int D = 23;
    private static final int E = 24;
    public static int a = 0;
    public static int b = 0;
    private static final int r = 1;
    private static final int s = 2;
    private static final int t = 3;
    private static final int u = 5;
    private static final int v = 11;
    private static final int w = 12;
    private static final int x = 13;
    private static final int y = 14;
    private static final int z = 15;
    protected com.unisound.common.an c;
    private com.unisound.common.am d;
    private at e;
    private at f;
    private ai g;
    private w h;
    private z i;
    private an j;
    private String k;
    private boolean l;
    private Context m;
    private x n;
    private Looper o;
    private ac p;
    private ab q;

    static {
        System.loadLibrary("uscasr");
        a = 60000;
        b = 10000;
    }

    public aj(Context context, an anVar) {
        this.d = new com.unisound.common.am();
        this.e = null;
        this.f = null;
        this.g = null;
        this.h = null;
        this.i = null;
        this.j = null;
        this.k = "";
        this.l = true;
        this.n = null;
        this.p = new ak(this);
        this.c = null;
        this.m = context;
        com.unisound.common.k.a(context);
        this.j = anVar;
    }

    public aj(Context context, an anVar, Looper looper) {
        super(looper);
        this.d = new com.unisound.common.am();
        this.e = null;
        this.f = null;
        this.g = null;
        this.h = null;
        this.i = null;
        this.j = null;
        this.k = "";
        this.l = true;
        this.n = null;
        this.p = new ak(this);
        this.c = null;
        this.m = context;
        com.unisound.common.k.a(context);
        this.j = anVar;
        this.o = looper;
        this.q = new ab(this.p, looper);
    }

    private void d(String str) {
        com.unisound.common.r.c("Before startRecognition :cancelRecognition()");
        d(false);
        this.j.aM = this.d.a();
        this.j.aP = com.unisound.common.k.b();
        this.e = this.f;
        this.c = null;
        this.l = false;
        this.k = "";
        ai aiVar = new ai(this.j, this.m, str);
        this.g = aiVar;
        aiVar.a(this);
        this.g.setName("usc_net_thread");
        if (!this.j.y()) {
            this.g.start();
        }
        com.unisound.common.r.c("Recognizer:: recognitionThread start");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d(boolean z2) {
        ai aiVar = this.g;
        if (aiVar == null || aiVar.d()) {
            return;
        }
        this.g.c();
    }

    private void f(int i) {
        if (i == 0) {
            this.c = new com.unisound.common.an(this.j.aO, this.d.a());
        }
        this.l = true;
        this.k = "";
        ai aiVar = this.g;
        if (aiVar != null) {
            this.k = aiVar.e();
        }
        if (this.q.b()) {
            return;
        }
        this.q.e();
        z zVar = this.i;
        if (zVar != null) {
            zVar.b(i);
        }
    }

    private void r() {
        com.unisound.common.r.c("Recognizer stopRecording");
        this.l = true;
        w wVar = this.h;
        if (wVar != null) {
            wVar.d();
        }
    }

    private void s() {
        this.l = true;
        ai aiVar = this.g;
        if (aiVar == null || aiVar.a()) {
            return;
        }
        this.g.b();
        this.q.c();
    }

    private void t() {
        w wVar = this.h;
        if (wVar != null) {
            wVar.g();
        }
    }

    public String a() {
        return com.unisound.common.af.a();
    }

    @Override // com.unisound.sdk.ag
    public void a(int i) {
        r();
        sendMessage(13, Integer.valueOf(i));
    }

    @Override // com.unisound.common.aj
    public void a(int i, int i2, Object obj) {
        z zVar = this.i;
        if (zVar != null) {
            zVar.a(i, i2, obj);
        }
    }

    public void a(SparseArray<List<String>> sparseArray) {
        cd cdVar = new cd();
        cdVar.a(this);
        cdVar.a(this.j.ab(), sparseArray);
    }

    @Override // com.unisound.sdk.cf
    public void a(VAD vad) {
        sendMessage(21);
    }

    public void a(at atVar) {
        this.f = atVar;
        this.e = atVar;
    }

    public void a(w wVar, boolean z2, String str, x xVar) {
        this.q.d();
        d(str);
        if (z2) {
            this.n = xVar;
            this.h = wVar;
            wVar.start();
        }
    }

    public void a(z zVar) {
        this.i = zVar;
    }

    public void a(String str) {
        cd cdVar = new cd();
        cdVar.a(this);
        cdVar.a(this.j.ab(), str);
    }

    public void a(String str, int i) {
        this.j.a(new String(str), i);
    }

    @Override // com.unisound.sdk.ag
    public void a(String str, boolean z2) {
        ah ahVar = new ah();
        ahVar.a = str;
        ahVar.b = z2;
        sendMessage(11, ahVar);
    }

    @Override // com.unisound.sdk.ag
    public void a(String str, boolean z2, int i) {
    }

    public void a(List<byte[]> list, String str) {
        d(str);
        this.g.a(list);
        this.g.b();
    }

    public void a(Map<Integer, List<String>> map) {
        cd cdVar = new cd();
        cdVar.a(this);
        cdVar.a(this.j.ab(), map);
    }

    public void a(boolean z2) {
        this.j.n(z2);
    }

    @Override // com.unisound.sdk.ap
    public void a(boolean z2, byte[] bArr, int i, int i2) {
        x xVar = this.n;
        if (xVar != null) {
            xVar.a(bArr);
        }
    }

    @Override // com.unisound.common.u
    public boolean a(Message message) {
        synchronized (this.o) {
            int i = message.what;
            if (i != 1) {
                if (i == 2) {
                    s();
                    z zVar = this.i;
                    if (zVar != null) {
                        zVar.e();
                    }
                } else if (i == 3) {
                    d(true);
                    f(ErrorCode.RECORDING_EXCEPTION);
                } else if (i != 5) {
                    switch (i) {
                        case 11:
                            if (this.i != null) {
                                ah ahVar = (ah) message.obj;
                                this.i.a(ahVar.a, ahVar.b);
                            }
                            break;
                        case 12:
                            f(0);
                            break;
                        case 13:
                            t();
                            f(((Integer) message.obj).intValue());
                            break;
                        case 14:
                            com.unisound.common.r.c("recognizer cancel");
                            break;
                        case 15:
                            t();
                            f(ErrorCode.RECOGNITION_EXCEPTION);
                            break;
                        case 16:
                            c((String) message.obj);
                            break;
                        default:
                            switch (i) {
                                case 21:
                                    z zVar2 = this.i;
                                    if (zVar2 != null) {
                                        zVar2.b();
                                    }
                                    break;
                                case 22:
                                    if (this.i != null) {
                                        this.i.a(((Integer) message.obj).intValue());
                                    }
                                    break;
                                case 23:
                                    c(true);
                                    com.unisound.common.r.c("max_speech_timeout cancel()");
                                    f(ErrorCode.ASRCLIENT_MAX_SPEECH_TIMEOUT);
                                    break;
                                case 24:
                                    if (this.i != null) {
                                        this.i.c(((Integer) message.obj).intValue());
                                    }
                                    break;
                                default:
                                    return false;
                            }
                            break;
                    }
                } else {
                    e();
                }
            } else if (((Boolean) message.obj).booleanValue()) {
                z zVar3 = this.i;
                if (zVar3 != null) {
                    zVar3.c();
                }
            } else {
                f(ErrorCode.FAILED_START_RECORDING);
                com.unisound.common.r.c("startRecognition Error:cancelRecognition()");
                d(true);
            }
            return true;
        }
    }

    public int b() {
        return this.q.a();
    }

    @Override // com.unisound.sdk.cf
    public void b(int i) {
        sendMessage(22, Integer.valueOf(i));
    }

    @Override // com.unisound.sdk.ag
    public void b(String str) {
        sendMessage(16, str);
    }

    @Override // com.unisound.sdk.ap
    public void b(boolean z2) {
        sendMessage(1, Boolean.valueOf(z2));
    }

    @Override // com.unisound.sdk.cf
    public void b(boolean z2, byte[] bArr, int i, int i2) {
        if (!this.j.y() || this.j.z()) {
            ai aiVar = this.g;
            if (aiVar != null && z2) {
                aiVar.a(bArr);
            }
            z zVar = this.i;
            if (zVar != null) {
                zVar.a(z2, bArr, i, i2);
            }
        }
    }

    public void c() {
        ai aiVar = this.g;
        if (aiVar != null) {
            aiVar.start();
        }
    }

    public void c(int i) {
        int i2 = a;
        if (i > i2 || i < (i2 = b)) {
            i = i2;
        }
        this.q.a(i);
    }

    protected void c(String str) {
        at atVar = this.e;
        if (atVar != null) {
            atVar.a(str);
        }
    }

    public void c(boolean z2) {
        this.e = null;
        this.l = true;
        this.q.e();
        t();
        d(z2);
        com.unisound.common.r.c("Recognizer: cancelRecognition()");
        removeSendMessage();
    }

    public void d() {
        r();
        s();
    }

    public void d(int i) {
        this.j.aT = i;
    }

    protected void e() {
        z zVar = this.i;
        if (zVar != null) {
            zVar.f();
        }
    }

    @Override // com.unisound.sdk.ce
    public void e(int i) {
        sendMessage(24, Integer.valueOf(i));
    }

    public String f() {
        return this.k;
    }

    public boolean g() {
        return this.l;
    }

    @Override // com.unisound.sdk.ag
    public void h() {
        sendMessage(12);
    }

    @Override // com.unisound.sdk.ap
    public void i() {
        this.h = null;
        sendMessage(2);
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
    }

    public com.unisound.common.am o() {
        return this.d;
    }

    public com.unisound.common.an p() {
        return this.c;
    }

    public String q() {
        ai aiVar = this.g;
        return aiVar != null ? aiVar.g() : "";
    }
}
