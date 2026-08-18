package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: TCellInfo.java */
/* JADX INFO: loaded from: classes2.dex */
public final class mu extends oc {
    public static int a(ob obVar, byte b, byte b2, short s, byte b3, int i) {
        obVar.b(5);
        a(obVar, i);
        a(obVar, s);
        c(obVar, b3);
        b(obVar, b2);
        a(obVar, b);
        return a(obVar);
    }

    private static void a(ob obVar, byte b) {
        obVar.a(0, b);
    }

    private static void b(ob obVar, byte b) {
        obVar.a(1, b);
    }

    private static void a(ob obVar, short s) {
        obVar.a(2, s);
    }

    private static void c(ob obVar, byte b) {
        obVar.a(3, b);
    }

    private static void a(ob obVar, int i) {
        obVar.b(4, i);
    }

    private static int a(ob obVar) {
        return obVar.b();
    }
}
