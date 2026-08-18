package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: TWifi.java */
/* JADX INFO: loaded from: classes2.dex */
public final class nc extends oc {
    public static int a(ob obVar, int i) {
        obVar.b(1);
        b(obVar, i);
        return a(obVar);
    }

    private static void b(ob obVar, int i) {
        obVar.b(0, i);
    }

    public static int a(ob obVar, int[] iArr) {
        obVar.a(4, iArr.length, 4);
        for (int length = iArr.length - 1; length >= 0; length--) {
            obVar.a(iArr[length]);
        }
        return obVar.a();
    }

    private static int a(ob obVar) {
        return obVar.b();
    }
}
