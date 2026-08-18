package com.amap.api.col.p0003sl;

import android.text.TextUtils;
import java.util.Vector;

/* JADX INFO: compiled from: LogMemCacher.java */
/* JADX INFO: loaded from: classes2.dex */
public final class jf {
    private static int b = 100;
    private static int d = 10000;
    private Vector<jc> a;
    private int c;
    private int e;

    public jf() {
        this.c = b;
        this.e = 0;
        this.c = 10;
        this.a = new Vector<>();
    }

    public jf(byte b2) {
        this.c = b;
        this.e = 0;
        this.a = new Vector<>();
    }

    public final synchronized boolean a(String str) {
        if (str == null) {
            return false;
        }
        if (this.a.size() >= this.c) {
            return true;
        }
        return this.e + str.getBytes().length > d;
    }

    public final Vector<jc> a() {
        return this.a;
    }

    public final synchronized void b() {
        this.a.clear();
        this.e = 0;
    }

    public final synchronized void a(jc jcVar) {
        if (jcVar != null) {
            if (!TextUtils.isEmpty(jcVar.b())) {
                this.a.add(jcVar);
                this.e += jcVar.b().getBytes().length;
            }
        }
    }
}
