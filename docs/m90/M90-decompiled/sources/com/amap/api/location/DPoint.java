package com.amap.api.location;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class DPoint implements Parcelable {
    public static final Parcelable.Creator<DPoint> CREATOR = new Parcelable.Creator<DPoint>() { // from class: com.amap.api.location.DPoint.1
        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ DPoint createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ DPoint[] newArray(int i) {
            return a(i);
        }

        private static DPoint a(Parcel parcel) {
            return new DPoint(parcel);
        }

        private static DPoint[] a(int i) {
            return new DPoint[i];
        }
    };
    private double a;
    private double b;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DPoint() {
        this.a = 0.0d;
        this.b = 0.0d;
    }

    public DPoint(double d, double d2) {
        this.a = 0.0d;
        this.b = 0.0d;
        d2 = d2 > 180.0d ? 180.0d : d2;
        d2 = d2 < -180.0d ? -180.0d : d2;
        d = d > 90.0d ? 90.0d : d;
        d = d < -90.0d ? -90.0d : d;
        this.a = d2;
        this.b = d;
    }

    public double getLongitude() {
        return this.a;
    }

    public void setLongitude(double d) {
        if (d > 180.0d) {
            d = 180.0d;
        }
        if (d < -180.0d) {
            d = -180.0d;
        }
        this.a = d;
    }

    public double getLatitude() {
        return this.b;
    }

    public void setLatitude(double d) {
        if (d > 90.0d) {
            d = 90.0d;
        }
        if (d < -90.0d) {
            d = -90.0d;
        }
        this.b = d;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DPoint)) {
            return false;
        }
        DPoint dPoint = (DPoint) obj;
        return this.b == dPoint.b && this.a == dPoint.a;
    }

    public int hashCode() {
        return Double.valueOf((this.b + this.a) * 1000000.0d).intValue();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(this.a);
        parcel.writeDouble(this.b);
    }

    protected DPoint(Parcel parcel) {
        this.a = 0.0d;
        this.b = 0.0d;
        this.a = parcel.readDouble();
        this.b = parcel.readDouble();
    }
}
