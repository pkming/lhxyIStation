package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: RootTUploadData.java */
/* JADX INFO: loaded from: classes2.dex */
public final class mq extends oc {
    public static int a(ob obVar, int i, int i2, int i3) {
        obVar.b(3);
        c(obVar, i3);
        b(obVar, i2);
        a(obVar, i);
        return a(obVar);
    }

    private static void a(ob obVar, int i) {
        obVar.b(0, i);
    }

    public static int a(ob obVar, byte[] bArr) {
        obVar.a(1, bArr.length, 1);
        for (int length = bArr.length - 1; length >= 0; length--) {
            obVar.a(bArr[length]);
        }
        return obVar.a();
    }

    private static void b(ob obVar, int i) {
        obVar.b(1, i);
    }

    public static int b(ob obVar, byte[] bArr) {
        obVar.a(1, bArr.length, 1);
        for (int length = bArr.length - 1; length >= 0; length--) {
            obVar.a(bArr[length]);
        }
        return obVar.a();
    }

    private static void c(ob obVar, int i) {
        obVar.b(2, i);
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
