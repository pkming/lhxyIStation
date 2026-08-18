package com.amap.api.services.poisearch;

import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.services.core.LatLonPoint;

/* JADX INFO: loaded from: classes2.dex */
public class PoiNavi implements Parcelable {
    public static final Parcelable.Creator<PoiNavi> CREATOR = new Parcelable.Creator<PoiNavi>() { // from class: com.amap.api.services.poisearch.PoiNavi.1
        @Override // android.os.Parcelable.Creator
        public final /* bridge */ /* synthetic */ PoiNavi[] newArray(int i) {
            return null;
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ PoiNavi createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        private static PoiNavi a(Parcel parcel) {
            return new PoiNavi(parcel);
        }
    };
    private String a;
    private LatLonPoint b;
    private LatLonPoint c;
    private String d;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public PoiNavi() {
    }

    public PoiNavi(String str, LatLonPoint latLonPoint, LatLonPoint latLonPoint2, String str2) {
        this.a = str;
        this.b = latLonPoint;
        this.c = latLonPoint2;
        this.d = str2;
    }

    public String getNaviPoiID() {
        return this.a;
    }

    public void setNaviPoiID(String str) {
        this.a = str;
    }

    public LatLonPoint getEnter() {
        return this.b;
    }

    public void setEnter(LatLonPoint latLonPoint) {
        this.b = latLonPoint;
    }

    public LatLonPoint getExit() {
        return this.c;
    }

    public void setExit(LatLonPoint latLonPoint) {
        this.c = latLonPoint;
    }

    public String getGridCode() {
        return this.d;
    }

    public void setGridCode(String str) {
        this.d = str;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.a);
        parcel.writeValue(this.b);
        parcel.writeValue(this.c);
        parcel.writeString(this.d);
    }

    public PoiNavi(Parcel parcel) {
        this.a = parcel.readString();
        this.b = (LatLonPoint) parcel.readValue(LatLonPoint.class.getClassLoader());
        this.c = (LatLonPoint) parcel.readValue(LatLonPoint.class.getClassLoader());
        this.d = parcel.readString();
    }
}
