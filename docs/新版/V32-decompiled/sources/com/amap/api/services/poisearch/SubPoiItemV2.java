package com.amap.api.services.poisearch;

import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.services.core.LatLonPoint;

/* JADX INFO: loaded from: classes2.dex */
public class SubPoiItemV2 implements Parcelable {
    public static final Parcelable.Creator<SubPoiItemV2> CREATOR = new Parcelable.Creator<SubPoiItemV2>() { // from class: com.amap.api.services.poisearch.SubPoiItemV2.1
        @Override // android.os.Parcelable.Creator
        public final /* bridge */ /* synthetic */ SubPoiItemV2[] newArray(int i) {
            return null;
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ SubPoiItemV2 createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        private static SubPoiItemV2 a(Parcel parcel) {
            return new SubPoiItemV2(parcel);
        }
    };
    private String a;
    private String b;
    private LatLonPoint c;
    private String d;
    private String e;
    private String f;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public SubPoiItemV2(String str, LatLonPoint latLonPoint, String str2, String str3) {
        this.a = str;
        this.c = latLonPoint;
        this.b = str2;
        this.d = str3;
    }

    public SubPoiItemV2(Parcel parcel) {
        this.a = parcel.readString();
        this.b = parcel.readString();
        this.c = (LatLonPoint) parcel.readValue(LatLonPoint.class.getClassLoader());
        this.d = parcel.readString();
        this.e = parcel.readString();
        this.f = parcel.readString();
    }

    public String getPoiId() {
        return this.a;
    }

    public void setPoiId(String str) {
        this.a = str;
    }

    public String getTitle() {
        return this.b;
    }

    public void setTitle(String str) {
        this.b = str;
    }

    public LatLonPoint getLatLonPoint() {
        return this.c;
    }

    public void setLatLonPoint(LatLonPoint latLonPoint) {
        this.c = latLonPoint;
    }

    public String getSnippet() {
        return this.d;
    }

    public void setSnippet(String str) {
        this.d = str;
    }

    public String getSubTypeDes() {
        return this.e;
    }

    public void setSubTypeDes(String str) {
        this.e = str;
    }

    public String getTypeCode() {
        return this.f;
    }

    public void setTypeCode(String str) {
        this.f = str;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.a);
        parcel.writeString(this.b);
        parcel.writeValue(this.c);
        parcel.writeString(this.d);
        parcel.writeString(this.e);
        parcel.writeString(this.f);
    }
}
