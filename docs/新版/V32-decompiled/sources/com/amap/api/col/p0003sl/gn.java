package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: RequestCacheConfig.java */
/* JADX INFO: loaded from: classes2.dex */
final class gn {
    private static volatile boolean a = false;

    public static synchronized void a() {
        if (!a) {
            go.a().a("regeo", new gq("/geocode/regeo"));
            go.a().a("placeAround", new gq("/place/around"));
            go.a().a("placeText", new gp("/place/text"));
            go.a().a("geo", new gp("/geocode/geo"));
            a = true;
        }
    }
}
