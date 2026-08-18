package com.unisound.common;

/* JADX INFO: loaded from: classes2.dex */
public class an {
    private static final int c = 1;
    private static final int d = 2;
    int a;
    int b;

    public an() {
        this.a = 0;
        this.b = 0;
    }

    public an(int i, int i2) {
        this.a = 0;
        this.b = 0;
        this.a = i2;
        this.b = i;
    }

    public void a(int i) {
        this.b = i;
    }

    public boolean a() {
        return (this.a & 1) != 0;
    }

    public ao b() {
        int i = this.b;
        return (i & 1) != 0 ? ao.MAN : (i & 2) != 0 ? ao.FEMALE : ao.UNKOWN;
    }
}
