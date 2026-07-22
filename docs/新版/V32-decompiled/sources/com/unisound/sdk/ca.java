package com.unisound.sdk;

import com.unisound.client.ErrorCode;

/* JADX INFO: loaded from: classes2.dex */
public class ca extends Thread {
    public static int a = 153600;
    private String b = "";
    private boolean c = false;
    private by d;
    private bz e;

    public ca(bz bzVar) {
        this.e = bzVar;
    }

    private void b(int i) {
        by byVar = this.d;
        if (byVar != null) {
            byVar.a(i);
        }
    }

    private void b(String str) {
        by byVar = this.d;
        if (byVar != null) {
            byVar.a(str);
        }
    }

    private boolean c(String str) {
        return str == null || str.length() == 0;
    }

    private aa e() {
        if (this.e == null) {
            return null;
        }
        aa aaVar = new aa(this.e.b(), this.e.c());
        aaVar.g(this.e.g());
        return aaVar;
    }

    private String f() {
        if (c(this.b)) {
            return null;
        }
        com.unisound.common.r.c("NLU processing begin");
        aa aaVarE = e();
        aaVarE.a(this.e.s());
        aaVarE.i(this.e.i());
        aaVarE.j(this.e.j());
        aaVarE.f(this.e.f());
        aaVarE.l(this.e.n());
        aaVarE.b(System.currentTimeMillis());
        aaVarE.p(this.e.r());
        aaVarE.e(com.unisound.common.k.x);
        aaVarE.d(this.e.d());
        aaVarE.h(this.b);
        return new bx().c(aaVarE);
    }

    public void a() {
        this.c = true;
    }

    public void a(int i) {
        c();
        if (isAlive()) {
            try {
                join(i);
                com.unisound.common.r.c("USCNluThread::waitEnd()");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void a(by byVar) {
        this.d = byVar;
    }

    public void a(String str) {
        this.b = str;
    }

    public boolean b() {
        return this.c;
    }

    public void c() {
        a();
        this.d = null;
    }

    public boolean d() {
        return this.d == null;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        int i;
        super.run();
        if (this.c) {
            return;
        }
        String strF = f();
        if (strF == null) {
            i = ErrorCode.NLU_REQUEST_EMPTY;
        } else if (strF.equals("{}")) {
            i = ErrorCode.NLU_SERVER_ERROR;
        } else {
            b(strF);
            i = 0;
        }
        b(i);
    }
}
