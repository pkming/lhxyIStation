package com.amap.api.col.p0003sl;

import android.os.SystemClock;
import com.amap.api.col.p0003sl.mk;
import java.util.List;

/* JADX INFO: compiled from: FpsCollector.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ml {
    private static volatile ml g;
    private static Object h = new Object();
    private long c;
    private nq d;
    private nq f = new nq();
    private mk a = new mk();
    private mm b = new mm();
    private mh e = new mh();

    /* JADX INFO: compiled from: FpsCollector.java */
    public static class a {
        public nq a;
        public List<nr> b;
        public long c;
        public long d;
        public boolean e;
        public long f;
        public byte g;
        public String h;
        public List<nk> i;
        public boolean j;
    }

    public static ml a() {
        if (g == null) {
            synchronized (h) {
                if (g == null) {
                    g = new ml();
                }
            }
        }
        return g;
    }

    private ml() {
    }

    public final mn a(a aVar) {
        mn mnVar = null;
        if (aVar == null) {
            return null;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        if (this.d == null || aVar.a.a(this.d) >= 10.0d) {
            mk.a aVarA = this.a.a(aVar.a, aVar.j, aVar.g, aVar.h, aVar.i);
            List<nr> listA = this.b.a(aVar.a, aVar.b, aVar.e, aVar.d, jCurrentTimeMillis);
            if (aVarA != null || listA != null) {
                ni.a(this.f, aVar.a, aVar.f, jCurrentTimeMillis);
                mnVar = new mn(0, this.e.a(this.f, aVarA, aVar.c, listA));
            }
            this.d = aVar.a;
            this.c = jElapsedRealtime;
        }
        return mnVar;
    }
}
