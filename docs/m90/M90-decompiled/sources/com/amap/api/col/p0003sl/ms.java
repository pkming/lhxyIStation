package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: TCell.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ms extends oc {
    public static int a(ob obVar, int i, byte b, int i2, int i3) {
        obVar.b(4);
        c(obVar, i3);
        b(obVar, i2);
        a(obVar, i);
        a(obVar, b);
        return a(obVar);
    }

    private static void a(ob obVar, int i) {
        obVar.b(0, i);
    }

    private static void a(ob obVar, byte b) {
        obVar.a(1, b);
    }

    private static void b(ob obVar, int i) {
        obVar.b(2, i);
    }

    public static int a(ob obVar, int[] iArr) {
        obVar.a(4, iArr.length, 4);
        for (int length = iArr.length - 1; length >= 0; length--) {
            obVar.a(iArr[length]);
        }
        return obVar.a();
    }

    private static void c(ob obVar, int i) {
        obVar.b(3, i);
    }

    public static int b(ob obVar, int[] iArr) {
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
