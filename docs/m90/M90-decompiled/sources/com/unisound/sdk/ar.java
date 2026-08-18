package com.unisound.sdk;

/* JADX INFO: loaded from: classes2.dex */
public class ar {
    private int a = 0;
    private int b = -1;
    private boolean c = false;

    public void a() {
        this.a = 0;
    }

    public void a(int i) {
        a();
        if (i == -1) {
            this.c = false;
        } else {
            this.c = true;
            this.b = i * 32;
        }
    }

    public boolean a(boolean z, int i) {
        if (!z || !this.c) {
            return false;
        }
        int i2 = this.a + i;
        this.a = i2;
        return i2 > this.b;
    }
}
