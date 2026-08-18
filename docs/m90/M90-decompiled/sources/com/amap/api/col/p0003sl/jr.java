package com.amap.api.col.p0003sl;

import android.os.Build;
import android.text.format.DateFormat;

/* JADX INFO: compiled from: Rom.java */
/* JADX INFO: loaded from: classes2.dex */
public enum jr {
    MIUI(it.c("IeGlhb21p")),
    Flyme(it.c("IbWVpenU")),
    RH(it.c("IaHVhd2Vp")),
    ColorOS(it.c("Ib3Bwbw")),
    FuntouchOS(it.c("Idml2bw")),
    SmartisanOS(it.c("Mc21hcnRpc2Fu")),
    AmigoOS(it.c("IYW1pZ28")),
    EUI(it.c("IbGV0dg")),
    Sense(it.c("EaHRj")),
    LG(it.c("EbGdl")),
    Google(it.c("IZ29vZ2xl")),
    NubiaUI(it.c("IbnViaWE")),
    Other("");

    private String n;
    private int o;
    private String p;
    private String q;
    private String r = Build.MANUFACTURER;

    jr(String str) {
        this.n = str;
    }

    public final String a() {
        return this.n;
    }

    public final void a(int i) {
        this.o = i;
    }

    public final String b() {
        return this.p;
    }

    public final void a(String str) {
        this.p = str;
    }

    public final void b(String str) {
        this.q = str;
    }

    @Override // java.lang.Enum
    public final String toString() {
        return "ROM{name='" + name() + DateFormat.QUOTE + ",versionCode=" + this.o + ", versionName='" + this.q + DateFormat.QUOTE + ",ma=" + this.n + DateFormat.QUOTE + ",manufacturer=" + this.r + DateFormat.QUOTE + '}';
    }
}
