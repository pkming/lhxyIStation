package com.amap.api.col.p0003sl;

import java.util.HashMap;

/* JADX INFO: compiled from: RequestCacheControl.java */
/* JADX INFO: loaded from: classes2.dex */
public class go {
    private static volatile go a;
    private HashMap<String, gp> b = new HashMap<>();

    public static go a() {
        if (a == null) {
            synchronized (go.class) {
                if (a == null) {
                    a = new go();
                }
            }
        }
        return a;
    }

    public final synchronized gp a(String str) {
        return this.b.get(str);
    }

    public final synchronized void a(String str, gp gpVar) {
        this.b.put(str, gpVar);
    }

    public final c a(b bVar) {
        c cVarA;
        if (bVar == null) {
            return null;
        }
        for (gp gpVar : this.b.values()) {
            if (gpVar != null && (cVarA = gpVar.a(bVar)) != null) {
                return cVarA;
            }
        }
        return null;
    }

    public final void a(b bVar, Object obj) {
        for (gp gpVar : this.b.values()) {
            if (gpVar != null) {
                gpVar.a(bVar, obj);
            }
        }
    }

    public final boolean b(b bVar) {
        if (bVar == null) {
            return false;
        }
        for (gp gpVar : this.b.values()) {
            if (gpVar != null && gpVar.b(bVar)) {
                return true;
            }
        }
        return false;
    }

    public final void a(a aVar) {
        if (aVar == null) {
            return;
        }
        for (gp gpVar : this.b.values()) {
            if (gpVar != null) {
                gpVar.a(aVar);
            }
        }
    }

    public final void a(String str, a aVar) {
        gp gpVar;
        if (str == null || aVar == null || (gpVar = this.b.get(str)) == null) {
            return;
        }
        gpVar.a(aVar);
    }

    /* JADX INFO: compiled from: RequestCacheControl.java */
    static class c {
        Object a;
        boolean b;

        public c(Object obj, boolean z) {
            this.a = obj;
            this.b = z;
        }
    }

    /* JADX INFO: compiled from: RequestCacheControl.java */
    static class b {
        String a;
        Object b;

        b() {
        }

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && getClass() == obj.getClass()) {
                b bVar = (b) obj;
                String str = this.a;
                if (str == null) {
                    return bVar.a == null && this.b == bVar.b;
                }
                if (str.equals(bVar.a) && this.b == bVar.b) {
                    return true;
                }
            }
            return false;
        }

        public final int hashCode() {
            String str = this.a;
            int iHashCode = ((str == null ? 0 : str.hashCode()) + 31) * 31;
            Object obj = this.b;
            return iHashCode + (obj != null ? obj.hashCode() : 0);
        }
    }

    /* JADX INFO: compiled from: RequestCacheControl.java */
    static class a {
        private boolean a = true;
        private long b = 86400;
        private int c = 10;
        private double d = 0.0d;

        a() {
        }

        public final boolean a() {
            return this.a;
        }

        public final void a(boolean z) {
            this.a = z;
        }

        public final long b() {
            return this.b;
        }

        public final void a(long j) {
            this.b = j;
        }

        public final int c() {
            return this.c;
        }

        public final void a(int i) {
            this.c = i;
        }

        public final double d() {
            return this.d;
        }

        public final void a(double d) {
            this.d = d;
        }
    }
}
