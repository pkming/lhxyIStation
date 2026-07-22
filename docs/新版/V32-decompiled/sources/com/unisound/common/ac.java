package com.unisound.common;

/* JADX INFO: loaded from: classes2.dex */
public class ac {
    public static final int a = -1;
    int b;
    String c;
    private boolean d = false;

    public ac(int i, String str) {
        this.b = -1;
        this.c = "";
        this.b = i;
        this.c = str;
    }

    public void a(ac acVar) {
        this.b = acVar.b;
        this.c = acVar.c;
        this.d = acVar.d;
    }

    void a(String str) {
        this.c = str;
    }

    public void a(boolean z) {
        this.d = z;
    }

    public boolean a() {
        return this.d;
    }

    public String b() {
        return this.c;
    }

    public int c() {
        return this.b;
    }
}
