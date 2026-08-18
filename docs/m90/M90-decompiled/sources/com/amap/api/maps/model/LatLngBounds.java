package com.amap.api.maps.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.amap.api.col.p0003sl.dx;

/* JADX INFO: loaded from: classes2.dex */
public final class LatLngBounds implements Parcelable {
    private static final String CLASSNAME = "LatLngBounds";
    public static final LatLngBoundsCreator CREATOR = new LatLngBoundsCreator();
    private final int mVersionCode;
    public final LatLng northeast;
    public final LatLng southwest;

    /* JADX INFO: Access modifiers changed from: private */
    public static double c(double d, double d2) {
        return ((d - d2) + 360.0d) % 360.0d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double d(double d, double d2) {
        return ((d2 - d) + 360.0d) % 360.0d;
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    LatLngBounds(int i, LatLng latLng, LatLng latLng2) {
        boolean z;
        try {
        } catch (Throwable th) {
            Log.e(CLASSNAME, "the structure parameters are illegal!");
            th.printStackTrace();
            z = false;
        }
        if (latLng == null) {
            throw new RuntimeRemoteException("null southwest");
        }
        if (latLng2 == null) {
            throw new RuntimeRemoteException("null northeast");
        }
        if (latLng2.latitude < latLng.latitude) {
            throw new RuntimeRemoteException("southern latitude exceeds northern latitude (" + latLng.latitude + " > " + latLng2.latitude + ")");
        }
        z = true;
        this.mVersionCode = z ? i : 0;
        this.southwest = z ? latLng : null;
        this.northeast = z ? latLng2 : null;
    }

    public LatLngBounds(LatLng latLng, LatLng latLng2) {
        this(1, latLng, latLng2);
    }

    final int a() {
        return this.mVersionCode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public final boolean contains(LatLng latLng) {
        if (latLng == null) {
            return false;
        }
        if (this.northeast != null && this.southwest != null) {
            return a(latLng.latitude) && b(latLng.longitude);
        }
        Log.e(CLASSNAME, "current LatLngBounds is invalid, please check the structure parameters are legal");
        return false;
    }

    public final boolean contains(LatLngBounds latLngBounds) {
        return latLngBounds != null && contains(latLngBounds.southwest) && contains(latLngBounds.northeast);
    }

    public final boolean intersects(LatLngBounds latLngBounds) {
        if (latLngBounds == null) {
            return false;
        }
        if (this.northeast != null && this.southwest != null) {
            return a(latLngBounds) || latLngBounds.a(this);
        }
        Log.e(CLASSNAME, "current LatLngBounds is invalid, please check the structure parameters are legal");
        return false;
    }

    private boolean a(LatLngBounds latLngBounds) {
        LatLng latLng;
        if (latLngBounds != null && (latLng = latLngBounds.northeast) != null && latLngBounds.southwest != null) {
            double d = ((latLng.longitude + latLngBounds.southwest.longitude) - this.northeast.longitude) - this.southwest.longitude;
            double d2 = ((this.northeast.longitude - this.southwest.longitude) + latLngBounds.northeast.longitude) - latLngBounds.southwest.longitude;
            double d3 = ((latLngBounds.northeast.latitude + latLngBounds.southwest.latitude) - this.northeast.latitude) - this.southwest.latitude;
            double d4 = ((this.northeast.latitude - this.southwest.latitude) + latLngBounds.northeast.latitude) - latLngBounds.southwest.latitude;
            if (Math.abs(d) < d2 && Math.abs(d3) < d4) {
                return true;
            }
        }
        return false;
    }

    public final LatLngBounds including(LatLng latLng) {
        LatLng latLng2;
        double d;
        if (latLng == null) {
            return this;
        }
        if (this.northeast == null || (latLng2 = this.southwest) == null) {
            Log.e(CLASSNAME, "current LatLngBounds is invalid, please check the structure parameters are legal");
            return this;
        }
        double dMin = Math.min(latLng2.latitude, latLng.latitude);
        double dMax = Math.max(this.northeast.latitude, latLng.latitude);
        double d2 = this.northeast.longitude;
        double d3 = this.southwest.longitude;
        double d4 = latLng.longitude;
        if (b(d4)) {
            d = d2;
        } else if (c(d3, d4) < d(d2, d4)) {
            d3 = d4;
            d = d2;
        } else {
            d = d4;
        }
        try {
            return new LatLngBounds(new LatLng(dMin, d3, false), new LatLng(dMax, d, false));
        } catch (Throwable th) {
            th.printStackTrace();
            return this;
        }
    }

    private boolean a(double d) {
        return this.southwest.latitude <= d && d <= this.northeast.latitude;
    }

    private boolean b(double d) {
        return this.southwest.longitude <= this.northeast.longitude ? this.southwest.longitude <= d && d <= this.northeast.longitude : this.southwest.longitude <= d || d <= this.northeast.longitude;
    }

    public final int hashCode() {
        return dx.a(new Object[]{this.southwest, this.northeast});
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LatLngBounds)) {
            return false;
        }
        LatLngBounds latLngBounds = (LatLngBounds) obj;
        return this.southwest.equals(latLngBounds.southwest) && this.northeast.equals(latLngBounds.northeast);
    }

    public final String toString() {
        return dx.a(dx.a("southwest", this.southwest), dx.a("northeast", this.northeast));
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        LatLngBoundsCreator.a(this, parcel, i);
    }

    public static final class Builder {
        private double mSouth = Double.POSITIVE_INFINITY;
        private double mNorth = Double.NEGATIVE_INFINITY;
        private double mWest = Double.NaN;
        private double mEast = Double.NaN;

        public final Builder include(LatLng latLng) {
            if (latLng == null) {
                return this;
            }
            this.mSouth = Math.min(this.mSouth, latLng.latitude);
            this.mNorth = Math.max(this.mNorth, latLng.latitude);
            double d = latLng.longitude;
            if (Double.isNaN(this.mWest)) {
                this.mWest = d;
            } else {
                if (!a(d)) {
                    if (LatLngBounds.c(this.mWest, d) < LatLngBounds.d(this.mEast, d)) {
                        this.mWest = d;
                    }
                }
                return this;
            }
            this.mEast = d;
            return this;
        }

        private boolean a(double d) {
            double d2 = this.mWest;
            double d3 = this.mEast;
            return d2 <= d3 ? d2 <= d && d <= d3 : d2 <= d || d <= d3;
        }

        public final LatLngBounds build() {
            if (Double.isNaN(this.mWest)) {
                Log.w(LatLngBounds.CLASSNAME, "no included points");
                return null;
            }
            double d = this.mWest;
            double d2 = this.mEast;
            if (d > d2) {
                this.mWest = d2;
                this.mEast = d;
            }
            double d3 = this.mSouth;
            double d4 = this.mNorth;
            if (d3 > d4) {
                this.mSouth = d4;
                this.mNorth = d3;
            }
            return new LatLngBounds(new LatLng(this.mSouth, this.mWest, false), new LatLng(this.mNorth, this.mEast, false));
        }
    }
}
