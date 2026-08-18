package com.amap.api.maps.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class CircleHoleOptions extends BaseHoleOptions implements Parcelable {
    public static final Parcelable.Creator<CircleHoleOptions> CREATOR = new Parcelable.Creator<CircleHoleOptions>() { // from class: com.amap.api.maps.model.CircleHoleOptions.1
        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ CircleHoleOptions createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ CircleHoleOptions[] newArray(int i) {
            return a(i);
        }

        private static CircleHoleOptions a(Parcel parcel) {
            return new CircleHoleOptions(parcel);
        }

        private static CircleHoleOptions[] a(int i) {
            return new CircleHoleOptions[i];
        }
    };
    private LatLng point;
    private double radius;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public CircleHoleOptions() {
        this.point = null;
        this.radius = 0.0d;
        this.isPolygonHoleOptions = false;
    }

    public CircleHoleOptions center(LatLng latLng) {
        this.point = latLng;
        return this;
    }

    public CircleHoleOptions radius(double d) {
        this.radius = d;
        return this;
    }

    public LatLng getCenter() {
        return this.point;
    }

    public double getRadius() {
        return this.radius;
    }

    protected CircleHoleOptions(Parcel parcel) {
        this.point = null;
        this.radius = 0.0d;
        Bundle bundle = parcel.readBundle();
        this.point = new LatLng(bundle.getDouble("lat"), bundle.getDouble("lng"));
        this.radius = parcel.readDouble();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        LatLng latLng = this.point;
        if (latLng != null) {
            bundle.putDouble("lat", latLng.latitude);
            bundle.putDouble("lng", this.point.longitude);
        }
        parcel.writeBundle(bundle);
        parcel.writeDouble(this.radius);
    }
}
