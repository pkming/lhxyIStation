package com.unisound.sdk;

/* JADX INFO: loaded from: classes2.dex */
public class bx {
    public String a(aa aaVar) {
        return bs.a(aaVar.a(), aaVar.l(), aaVar.b(), aaVar.c(), aaVar.e(), aaVar.g(), aaVar.f(), aaVar.d(), aaVar.h(), aaVar.i(), aaVar.j(), aaVar.k(), aaVar.m(), aaVar.n(), aaVar.o(), aaVar.p(), aaVar.q(), aaVar.r());
    }

    public String b(aa aaVar) {
        String strA = a(aaVar);
        String strA2 = com.unisound.common.n.a(strA);
        com.unisound.common.r.c("nlu url: " + strA);
        return strA2;
    }

    public String c(aa aaVar) {
        String strA = a(aaVar);
        String strA2 = com.unisound.common.n.a(strA, "");
        com.unisound.common.r.c("nlu url: " + strA);
        return strA2;
    }
}
