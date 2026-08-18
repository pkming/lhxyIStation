package com.autonavi.amap.mapcore;

import com.autonavi.base.ae.gmap.maploader.Pools;

/* JADX INFO: loaded from: classes2.dex */
public class DPoint {
    private static final Pools.SynchronizedPool<DPoint> M_POOL = new Pools.SynchronizedPool<>(32);
    public double x;
    public double y;

    public static DPoint obtain() {
        DPoint dPointAcquire = M_POOL.acquire();
        if (dPointAcquire == null) {
            return new DPoint();
        }
        dPointAcquire.set(0.0d, 0.0d);
        return dPointAcquire;
    }

    public static DPoint obtain(double d, double d2) {
        DPoint dPointAcquire = M_POOL.acquire();
        if (dPointAcquire == null) {
            return new DPoint(d, d2);
        }
        dPointAcquire.set(d, d2);
        return dPointAcquire;
    }

    public void recycle() {
        M_POOL.release(this);
    }

    public DPoint() {
    }

    public DPoint(double d, double d2) {
        this.x = d;
        this.y = d2;
    }

    private void set(double d, double d2) {
        this.x = d;
        this.y = d2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DPoint)) {
            return false;
        }
        DPoint dPoint = (DPoint) obj;
        return Double.doubleToLongBits(this.x) == Double.doubleToLongBits(dPoint.x) && Double.doubleToLongBits(this.y) == Double.doubleToLongBits(dPoint.y);
    }
}
