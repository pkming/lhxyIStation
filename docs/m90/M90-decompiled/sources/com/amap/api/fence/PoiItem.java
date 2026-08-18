package com.amap.api.fence;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class PoiItem implements Parcelable {
    public static final Parcelable.Creator<PoiItem> CREATOR = new Parcelable.Creator<PoiItem>() { // from class: com.amap.api.fence.PoiItem.1
        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ PoiItem createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ PoiItem[] newArray(int i) {
            return a(i);
        }

        private static PoiItem a(Parcel parcel) {
            return new PoiItem(parcel);
        }

        private static PoiItem[] a(int i) {
            return new PoiItem[i];
        }
    };
    private String a;
    private String b;
    private String c;
    private String d;
    private String e;
    private double f;
    private double g;
    private String h;
    private String i;
    private String j;
    private String k;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public double getLatitude() {
        return this.f;
    }

    public void setLatitude(double d) {
        this.f = d;
    }

    public double getLongitude() {
        return this.g;
    }

    public void setLongitude(double d) {
        this.g = d;
    }

    public String getPoiId() {
        return this.b;
    }

    public void setPoiId(String str) {
        this.b = str;
    }

    public String getPoiType() {
        return this.c;
    }

    public void setPoiType(String str) {
        this.c = str;
    }

    public String getTypeCode() {
        return this.d;
    }

    public void setTypeCode(String str) {
        this.d = str;
    }

    public String getAddress() {
        return this.e;
    }

    public void setAddress(String str) {
        this.e = str;
    }

    public String getTel() {
        return this.h;
    }

    public void setTel(String str) {
        this.h = str;
    }

    public String getProvince() {
        return this.i;
    }

    public void setProvince(String str) {
        this.i = str;
    }

    public String getCity() {
        return this.j;
    }

    public void setCity(String str) {
        this.j = str;
    }

    public String getAdname() {
        return this.k;
    }

    public String getPoiName() {
        return this.a;
    }

    public void setPoiName(String str) {
        this.a = str;
    }

    public void setAdname(String str) {
        this.k = str;
    }

    public static Parcelable.Creator<PoiItem> getCreator() {
        return CREATOR;
    }

    public PoiItem() {
        this.a = "";
        this.b = "";
        this.c = "";
        this.d = "";
        this.e = "";
        this.f = 0.0d;
        this.g = 0.0d;
        this.h = "";
        this.i = "";
        this.j = "";
        this.k = "";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.a);
        parcel.writeString(this.b);
        parcel.writeString(this.c);
        parcel.writeString(this.d);
        parcel.writeString(this.e);
        parcel.writeDouble(this.f);
        parcel.writeDouble(this.g);
        parcel.writeString(this.h);
        parcel.writeString(this.i);
        parcel.writeString(this.j);
        parcel.writeString(this.k);
    }

    protected PoiItem(Parcel parcel) {
        this.a = "";
        this.b = "";
        this.c = "";
        this.d = "";
        this.e = "";
        this.f = 0.0d;
        this.g = 0.0d;
        this.h = "";
        this.i = "";
        this.j = "";
        this.k = "";
        this.a = parcel.readString();
        this.b = parcel.readString();
        this.c = parcel.readString();
        this.d = parcel.readString();
        this.e = parcel.readString();
        this.f = parcel.readDouble();
        this.g = parcel.readDouble();
        this.h = parcel.readString();
        this.i = parcel.readString();
        this.j = parcel.readString();
        this.k = parcel.readString();
    }
}
