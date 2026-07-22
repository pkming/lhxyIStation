package com.unisound.sdk;

import cn.yunzhisheng.asr.VAD;
import cn.yunzhisheng.asrfix.JniAsrFix;

/* JADX INFO: loaded from: classes2.dex */
public class h extends i {
    VAD a;
    x b;
    boolean c;
    boolean d;
    boolean e;
    private int o;

    public h(JniAsrFix jniAsrFix, String str, u uVar) {
        super(jniAsrFix, str, uVar);
        this.c = false;
        this.d = false;
        this.e = false;
        this.o = 300;
    }

    public h(JniAsrFix jniAsrFix, String str, u uVar, x xVar) {
        super(jniAsrFix, str, uVar);
        this.c = false;
        this.d = false;
        this.e = false;
        this.o = 300;
        this.a = xVar.d;
        this.b = xVar;
    }

    private void b(String str, boolean z) {
        if (!this.j.w() || j.d(str) < this.j.A()) {
            return;
        }
        String strE = j.e(str);
        this.j.c(strE);
        this.j.d(strE);
        int iL = this.k.l();
        int iQ = this.k.q();
        int i = iQ - iL;
        com.unisound.common.r.c("utteranceEndTime = " + iQ);
        com.unisound.common.r.c("utteranceStartTime = " + iL);
        com.unisound.common.r.c("utteranceTime = " + i);
        if (this.j.y()) {
            this.j.k(iL - this.o);
            this.j.l(this.o + i);
            this.j.j(true);
            this.j.c(this.j.i());
            this.j.g(false);
            this.j.p(this.j.Q());
        }
        if (this.c) {
            return;
        }
        a(str, z, i);
        this.c = true;
    }

    @Override // com.unisound.sdk.i
    protected boolean a() {
        if (this.g.isEmpty()) {
            return (this.b.e.isEmpty() || this.b.c()) ? false : true;
        }
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:123:0x0222 A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0208 A[Catch: Exception -> 0x0228, all -> 0x02ca, TryCatch #1 {Exception -> 0x0228, blocks: (B:29:0x010c, B:31:0x011a, B:33:0x011d, B:35:0x0123, B:37:0x0129, B:38:0x012d, B:40:0x013e, B:42:0x014c, B:43:0x0155, B:45:0x017c, B:47:0x0184, B:49:0x01a2, B:51:0x01a8, B:57:0x01c4, B:58:0x01d9, B:70:0x0204, B:72:0x0208, B:73:0x020a, B:79:0x0213, B:52:0x01af, B:54:0x01ba, B:56:0x01c2, B:60:0x01df, B:63:0x01e8, B:67:0x01f6), top: B:117:0x010c, outer: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x0218 A[Catch: all -> 0x02ca, TryCatch #2 {, blocks: (B:8:0x0013, B:10:0x001b, B:12:0x0023, B:14:0x0033, B:15:0x006e, B:17:0x0070, B:18:0x0089, B:25:0x0100, B:26:0x0105, B:29:0x010c, B:31:0x011a, B:33:0x011d, B:35:0x0123, B:37:0x0129, B:38:0x012d, B:40:0x013e, B:42:0x014c, B:43:0x0155, B:45:0x017c, B:47:0x0184, B:49:0x01a2, B:51:0x01a8, B:57:0x01c4, B:58:0x01d9, B:70:0x0204, B:72:0x0208, B:73:0x020a, B:79:0x0213, B:52:0x01af, B:54:0x01ba, B:56:0x01c2, B:60:0x01df, B:63:0x01e8, B:67:0x01f6, B:68:0x0202, B:80:0x0214, B:82:0x0218, B:84:0x021c, B:86:0x0222, B:89:0x022c, B:91:0x025c, B:93:0x0265, B:96:0x026f, B:98:0x027a, B:100:0x0282, B:102:0x0289, B:101:0x0286, B:103:0x028b, B:105:0x0293, B:107:0x029b, B:109:0x02a3, B:110:0x02c8, B:88:0x0229, B:19:0x008d, B:21:0x009d, B:22:0x00d8, B:24:0x00da), top: B:118:0x0013, inners: #1 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void b() {
        /*
            Method dump skipped, instruction units count: 717
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.unisound.sdk.h.b():void");
    }

    @Override // com.unisound.sdk.i
    public void c() {
        super.c();
    }

    @Override // com.unisound.sdk.i
    public void d() {
        super.d();
    }

    @Override // com.unisound.sdk.i
    protected void e() {
        super.e();
    }

    @Override // com.unisound.sdk.i, java.lang.Thread, java.lang.Runnable
    public void run() {
        this.j.j(false);
        this.d = false;
        com.unisound.common.r.g("FixRecognitionThread start");
        if (k() || this.k == null) {
            return;
        }
        this.e = false;
        boolean z = true;
        while (!k() && ((!this.l || a()) && !this.e)) {
            if (!z) {
                this.m = false;
                synchronized (this.b) {
                    this.a.l = true;
                }
            }
            if (this.j.y() && this.j.z()) {
                this.d = true;
            }
            b();
            if (z) {
                z = false;
            }
            if (((!this.j.y() || !this.j.z()) && !this.j.w()) || this.d) {
                break;
            }
        }
        l();
        this.g.clear();
        com.unisound.common.r.g("FixRecognitionThread stop");
    }
}
