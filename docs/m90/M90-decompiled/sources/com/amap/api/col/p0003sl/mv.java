package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: TData.java */
/* JADX INFO: loaded from: classes2.dex */
public final class mv extends oc {
    public static int a(ob obVar, byte b, int i) {
        obVar.b(2);
        a(obVar, i);
        a(obVar, b);
        return a(obVar);
    }

    private static void a(ob obVar, byte b) {
        obVar.a(0, b);
    }

    private static void a(ob obVar, int i) {
        obVar.b(1, i);
    }

    public static int a(ob obVar, byte[] bArr) {
        obVar.a(1, bArr.length, 1);
        for (int length = bArr.length - 1; length >= 0; length--) {
            obVar.a(bArr[length]);
        }
        return obVar.a();
    }

    private static int a(ob obVar) {
        return obVar.b();
    }
}
