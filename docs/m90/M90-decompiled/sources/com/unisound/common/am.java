package com.unisound.common;

/* JADX INFO: loaded from: classes2.dex */
public class am {
    static final int a = 1;
    int b = 0;

    public int a() {
        return this.b;
    }

    public void a(boolean z) {
        this.b = z ? this.b | 1 : this.b & (-2);
    }

    public boolean b() {
        return (this.b & 1) != 0;
    }
}
