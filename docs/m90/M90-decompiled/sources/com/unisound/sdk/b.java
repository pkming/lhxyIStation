package com.unisound.sdk;

/* JADX INFO: loaded from: classes2.dex */
public class b {
    public static final String a = "x-wav";
    public static final String b = "pcm";
    public static final String c = "opus";
    public static final int d = 16;
    public static final int e = 16000;
    private String f = a;
    private String g = b;
    private String h = "opus";
    private int i = 16;
    private int j = 16000;
    private boolean k = true;

    public String a() {
        return this.h;
    }

    public void a(boolean z) {
        this.k = z;
    }

    public String b() {
        return "audio/" + this.f;
    }

    public boolean c() {
        return this.k;
    }
}
