package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: TMainCellCDMAHistory.java */
/* JADX INFO: loaded from: classes2.dex */
public final class mz extends oc {
    public static int a(ob obVar, int i, int i2, int i3, short s) {
        obVar.b(5);
        c(obVar, i3);
        b(obVar, i2);
        a(obVar, i);
        a(obVar, s);
        a(obVar);
        return b(obVar);
    }

    private static void a(ob obVar) {
        obVar.a(0, (byte) 2);
    }

    private static void a(ob obVar, int i) {
        obVar.a(1, i);
    }

    private static void b(ob obVar, int i) {
        obVar.a(2, i);
    }

    private static void c(ob obVar, int i) {
        obVar.a(3, i);
    }

    private static void a(ob obVar, short s) {
        obVar.a(4, s);
    }

    private static int b(ob obVar) {
        return obVar.b();
    }
}
