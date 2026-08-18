package com.unisound.sdk;

import cn.yunzhisheng.asrfix.JniAsrFix;

/* JADX INFO: loaded from: classes2.dex */
public class f {
    public static final int a = 0;
    public static final int b = -1;
    private static y d;
    private long c = 0;
    private JniAsrFix e;

    public int a(String str, String str2) {
        return this.e.a(this.c, str, str2);
    }

    public int a(String str, String str2, String str3) {
        return this.e.a(this.c, str, str2, str3);
    }

    public int a(String str, String str2, String str3, String str4) {
        if (this.c == 0) {
            com.unisound.common.r.c("compile  compileUserData fail handle=0");
        }
        int iPartialCompileUserData = this.e.partialCompileUserData(this.c, str, str2, str3, str4, str);
        if (iPartialCompileUserData == 0) {
            com.unisound.common.r.c("compile  compileUserData ok");
            return iPartialCompileUserData;
        }
        if (iPartialCompileUserData == -10) {
            com.unisound.common.r.e("compile compileUserData partialfile error, autofix ok");
            return 0;
        }
        com.unisound.common.r.e("compile  compileUserData fail code = " + iPartialCompileUserData);
        return iPartialCompileUserData;
    }

    public int a(String str, String str2, String str3, String str4, String str5) {
        return this.e.a(str, str2, str3, str4, str5, str);
    }

    public void a(y yVar) {
        d = yVar;
    }

    public boolean a() {
        return this.c != 0;
    }

    public boolean a(String str) {
        JniAsrFix jniAsrFixA = JniAsrFix.a();
        this.e = jniAsrFixA;
        this.c = jniAsrFixA.initUserDataCompiler(str);
        com.unisound.common.r.c("compile  initUserDataCompiler handle=" + this.c);
        long j = this.c;
        return j != 0 || j == 0;
    }

    public int b(String str) {
        return this.e.unloadGrammar(str);
    }

    public void b() {
        if (this.c != 0) {
            com.unisound.common.r.c("compile  destroyUserDataCompiler");
            this.e.destroyUserDataCompiler(this.c);
            this.c = 0L;
        }
    }
}
