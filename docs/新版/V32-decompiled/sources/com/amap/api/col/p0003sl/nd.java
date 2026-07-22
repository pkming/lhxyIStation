package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: TWifiInfo.java */
/* JADX INFO: loaded from: classes2.dex */
public final class nd extends oc {
    public static int a(ob obVar, boolean z, long j, short s, int i, short s2, short s3) {
        obVar.b(6);
        a(obVar, j);
        a(obVar, i);
        c(obVar, s3);
        b(obVar, s2);
        a(obVar, s);
        a(obVar, z);
        return a(obVar);
    }

    private static void a(ob obVar, boolean z) {
        obVar.a(z);
    }

    private static void a(ob obVar, long j) {
        obVar.a(1, j);
    }

    private static void a(ob obVar, short s) {
        obVar.a(2, s);
    }

    private static void a(ob obVar, int i) {
        obVar.b(3, i);
    }

    private static void b(ob obVar, short s) {
        obVar.a(4, s);
    }

    private static void c(ob obVar, short s) {
        obVar.a(5, s);
    }

    private static int a(ob obVar) {
        return obVar.b();
    }
}
