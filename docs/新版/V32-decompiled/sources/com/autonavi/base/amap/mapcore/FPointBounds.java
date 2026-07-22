package com.autonavi.base.amap.mapcore;

/* JADX INFO: loaded from: classes2.dex */
public class FPointBounds {
    private final int mVersionCode;
    public final FPoint northeast;
    public final FPoint southwest;

    FPointBounds(int i, FPoint fPoint, FPoint fPoint2) {
        this.mVersionCode = i;
        this.southwest = fPoint;
        this.northeast = fPoint2;
    }

    public FPointBounds(FPoint fPoint, FPoint fPoint2) {
        this(1, fPoint, fPoint2);
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean contains(FPoint fPoint) {
        return containsy((double) fPoint.y) && containsx((double) fPoint.x);
    }

    public boolean contains(FPointBounds fPointBounds) {
        return fPointBounds != null && contains(fPointBounds.southwest) && contains(fPointBounds.northeast);
    }

    public boolean intersects(FPointBounds fPointBounds) {
        if (fPointBounds == null) {
            return false;
        }
        return intersect(fPointBounds) || fPointBounds.intersect(this);
    }

    private boolean intersect(FPointBounds fPointBounds) {
        FPoint fPoint;
        if (fPointBounds != null && (fPoint = fPointBounds.northeast) != null && fPointBounds.southwest != null && this.northeast != null && this.southwest != null) {
            double d = ((fPoint.x + fPointBounds.southwest.x) - this.northeast.x) - this.southwest.x;
            double d2 = ((this.northeast.x - this.southwest.x) + fPointBounds.northeast.x) - this.southwest.x;
            double d3 = ((fPointBounds.northeast.y + fPointBounds.southwest.y) - this.northeast.y) - this.southwest.y;
            double d4 = ((this.northeast.y - this.southwest.y) + fPointBounds.northeast.y) - fPointBounds.southwest.y;
            if (Math.abs(d) < d2 && Math.abs(d3) < d4) {
                return true;
            }
        }
        return false;
    }

    private boolean containsy(double d) {
        return ((double) this.southwest.y) <= d && d <= ((double) this.northeast.y);
    }

    private boolean containsx(double d) {
        return this.southwest.x <= this.northeast.x ? ((double) this.southwest.x) <= d && d <= ((double) this.northeast.x) : ((double) this.southwest.x) <= d || d <= ((double) this.northeast.x);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FPointBounds)) {
            return false;
        }
        FPointBounds fPointBounds = (FPointBounds) obj;
        return this.southwest.equals(fPointBounds.southwest) && this.northeast.equals(fPointBounds.northeast);
    }

    public String toString() {
        return "southwest = (" + this.southwest.x + "," + this.southwest.y + ") northeast = (" + this.northeast.x + "," + this.northeast.y + ")";
    }

    public static final class Builder {
        private float mSouth = Float.POSITIVE_INFINITY;
        private float mNorth = Float.NEGATIVE_INFINITY;
        private float mWest = Float.POSITIVE_INFINITY;
        private float mEast = Float.NEGATIVE_INFINITY;

        public final Builder include(FPoint fPoint) {
            this.mSouth = Math.min(this.mSouth, fPoint.y);
            this.mNorth = Math.max(this.mNorth, fPoint.y);
            this.mWest = Math.min(this.mWest, fPoint.x);
            this.mEast = Math.max(this.mEast, fPoint.x);
            return this;
        }

        private boolean containsx(double d) {
            float f = this.mWest;
            float f2 = this.mEast;
            return f <= f2 ? ((double) f) <= d && d <= ((double) f2) : ((double) f) <= d || d <= ((double) f2);
        }

        public final FPointBounds build() {
            return new FPointBounds(FPoint.obtain(this.mWest, this.mSouth), FPoint.obtain(this.mEast, this.mNorth));
        }
    }
}
