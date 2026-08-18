package com.amap.api.col.p0003sl;

import com.amap.api.col.p0003sl.go;
import com.amap.api.services.core.LatLonPoint;
import java.util.Iterator;
import java.util.LinkedHashMap;

/* JADX INFO: compiled from: RequestCacheWorkerCheckDistance.java */
/* JADX INFO: loaded from: classes2.dex */
final class gq extends gp {
    private double a;

    public gq(String... strArr) {
        super(strArr);
        this.a = 0.0d;
        this.a = 0.0d;
    }

    @Override // com.amap.api.col.p0003sl.gp
    protected final boolean a(LinkedHashMap<go.b, Object> linkedHashMap, go.b bVar) {
        if (linkedHashMap != null && bVar != null) {
            if (bVar.b == null) {
                return super.a(linkedHashMap, bVar);
            }
            for (go.b bVar2 : linkedHashMap.keySet()) {
                if (bVar2 != null && bVar2.a != null && bVar2.a.equals(bVar.a) && (bVar2.b instanceof a) && ((a) bVar2.b).a(bVar.b)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override // com.amap.api.col.p0003sl.gp
    protected final Object b(LinkedHashMap<go.b, Object> linkedHashMap, go.b bVar) {
        if (linkedHashMap != null && bVar != null) {
            if (bVar.b == null) {
                return super.b(linkedHashMap, bVar);
            }
            for (go.b bVar2 : linkedHashMap.keySet()) {
                if (bVar2 != null && bVar2.a != null && bVar2.a.equals(bVar.a) && (bVar2.b instanceof a) && ((a) bVar2.b).a(bVar.b)) {
                    return linkedHashMap.get(bVar2);
                }
            }
        }
        return null;
    }

    @Override // com.amap.api.col.p0003sl.gp
    protected final Object c(LinkedHashMap<go.b, Object> linkedHashMap, go.b bVar) {
        go.b next;
        if (linkedHashMap != null && bVar != null) {
            if (bVar.b == null) {
                return super.c(linkedHashMap, bVar);
            }
            Iterator<go.b> it = linkedHashMap.keySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    next = null;
                    break;
                }
                next = it.next();
                if (next != null && next.a != null && next.a.equals(bVar.a) && (next.b instanceof a) && ((a) next.b).a(bVar.b)) {
                    break;
                }
            }
            if (next != null) {
                return linkedHashMap.remove(next);
            }
        }
        return null;
    }

    public final double a() {
        return this.a;
    }

    @Override // com.amap.api.col.p0003sl.gp
    public final void a(go.a aVar) {
        super.a(aVar);
        if (aVar != null) {
            this.a = aVar.d();
        }
    }

    /* JADX INFO: compiled from: RequestCacheWorkerCheckDistance.java */
    static class a {
        LatLonPoint a;
        double b;

        public a(double d, double d2, double d3) {
            this.a = null;
            this.b = 0.0d;
            this.a = new LatLonPoint(d, d2);
            this.b = d3;
        }

        public final boolean a(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && getClass() == obj.getClass()) {
                LatLonPoint latLonPoint = this.a;
                a aVar = (a) obj;
                if (latLonPoint == aVar.a) {
                    return true;
                }
                if (latLonPoint != null && fp.a(latLonPoint, r3) <= aVar.b) {
                    return true;
                }
            }
            return false;
        }
    }
}
