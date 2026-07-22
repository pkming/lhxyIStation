package com.amap.api.services.route;

import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.services.core.LatLonPoint;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class TaxiItemV2 implements Parcelable {
    public static final Parcelable.Creator<TaxiItemV2> CREATOR = new Parcelable.Creator<TaxiItemV2>() { // from class: com.amap.api.services.route.TaxiItemV2.1
        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ TaxiItemV2 createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ TaxiItemV2[] newArray(int i) {
            return a(i);
        }

        private static TaxiItemV2 a(Parcel parcel) {
            return new TaxiItemV2(parcel);
        }

        private static TaxiItemV2[] a(int i) {
            return new TaxiItemV2[i];
        }
    };
    private LatLonPoint a;
    private LatLonPoint b;
    private float c;
    private float d;
    private String e;
    private String f;
    private float g;
    private List<LatLonPoint> h;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public TaxiItemV2() {
        this.h = new ArrayList();
    }

    public LatLonPoint getOrigin() {
        return this.a;
    }

    public LatLonPoint getDestination() {
        return this.b;
    }

    public float getDistance() {
        return this.c;
    }

    public float getDuration() {
        return this.d;
    }

    public String getmSname() {
        return this.e;
    }

    public String getmTname() {
        return this.f;
    }

    public void setOrigin(LatLonPoint latLonPoint) {
        this.a = latLonPoint;
    }

    public void setDestination(LatLonPoint latLonPoint) {
        this.b = latLonPoint;
    }

    public void setDistance(float f) {
        this.c = f;
    }

    public void setDuration(float f) {
        this.d = f;
    }

    public void setSname(String str) {
        this.e = str;
    }

    public void setTname(String str) {
        this.f = str;
    }

    public float getPrice() {
        return this.g;
    }

    public void setPrice(float f) {
        this.g = f;
    }

    public List<LatLonPoint> getPolyline() {
        return this.h;
    }

    public void setPolyline(List<LatLonPoint> list) {
        this.h = list;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.a, i);
        parcel.writeParcelable(this.b, i);
        parcel.writeFloat(this.c);
        parcel.writeFloat(this.d);
        parcel.writeString(this.e);
        parcel.writeString(this.f);
        parcel.writeFloat(this.g);
        parcel.writeTypedList(this.h);
    }

    protected TaxiItemV2(Parcel parcel) {
        this.h = new ArrayList();
        this.a = (LatLonPoint) parcel.readParcelable(LatLonPoint.class.getClassLoader());
        this.b = (LatLonPoint) parcel.readParcelable(LatLonPoint.class.getClassLoader());
        this.c = parcel.readFloat();
        this.d = parcel.readFloat();
        this.e = parcel.readString();
        this.f = parcel.readString();
        this.g = parcel.readFloat();
        this.h = parcel.createTypedArrayList(LatLonPoint.CREATOR);
    }
}
