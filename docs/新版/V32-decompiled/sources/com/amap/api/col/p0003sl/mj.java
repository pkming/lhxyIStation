package com.amap.api.col.p0003sl;

import java.util.List;

/* JADX INFO: compiled from: UploadBufferBuilder.java */
/* JADX INFO: loaded from: classes2.dex */
public final class mj extends mg {
    private static mj b = new mj();

    private static String a(String str) {
        return str == null ? "" : str;
    }

    public static mj b() {
        return b;
    }

    private mj() {
        super(5120);
    }

    public final byte[] a(byte[] bArr, byte[] bArr2, List<? extends mn> list) {
        if (list == null) {
            return null;
        }
        try {
            int size = list.size();
            if (size <= 0 || bArr == null) {
                return null;
            }
            a();
            int iA = mq.a((ob) this.a, bArr);
            int[] iArr = new int[size];
            for (int i = 0; i < size; i++) {
                mn mnVar = list.get(i);
                iArr[i] = mv.a(this.a, (byte) mnVar.a(), mv.a(this.a, mnVar.b()));
            }
            this.a.c(mq.a(this.a, iA, bArr2 != null ? mq.b(this.a, bArr2) : 0, mq.a(this.a, iArr)));
            return this.a.c();
        } catch (Throwable th) {
            nu.a(th);
            return null;
        }
    }

    public final byte[] c() {
        super.a();
        try {
            this.a.c(nt.a(this.a, ns.a(), this.a.a(ns.f()), this.a.a(ns.c()), (byte) ns.m(), this.a.a(ns.i()), this.a.a(ns.h()), this.a.a(a(ns.g())), this.a.a(a(ns.j())), nr.a(ns.n()), this.a.a(ns.l()), this.a.a(ns.k()), this.a.a(ns.d()), this.a.a(ns.e())));
            return this.a.c();
        } catch (Exception e) {
            nu.a(e);
            return null;
        }
    }
}
