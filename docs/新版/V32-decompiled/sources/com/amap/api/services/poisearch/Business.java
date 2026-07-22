package com.amap.api.services.poisearch;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class Business implements Parcelable {
    public static final Parcelable.Creator<Business> CREATOR = new Parcelable.Creator<Business>() { // from class: com.amap.api.services.poisearch.Business.1
        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ Business createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ Business[] newArray(int i) {
            return a(i);
        }

        private static Business a(Parcel parcel) {
            return new Business(parcel);
        }

        private static Business[] a(int i) {
            return new Business[i];
        }
    };
    private String a;
    private String b;
    private String c;
    private String d;
    private String e;
    private String f;
    private String g;
    private String h;
    private String i;
    private String j;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Business(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9) {
        this.a = str;
        this.b = str2;
        this.c = str3;
        this.d = str4;
        this.e = str5;
        this.f = str6;
        this.g = str7;
        this.h = str8;
        this.i = str9;
    }

    public String getBusinessArea() {
        return this.a;
    }

    public String getOpentimeToday() {
        return this.b;
    }

    public String getOpentimeWeek() {
        return this.c;
    }

    public String getTel() {
        return this.d;
    }

    public String getTag() {
        return this.e;
    }

    public String getmRating() {
        return this.f;
    }

    public String getCost() {
        return this.g;
    }

    public String getParkingType() {
        return this.h;
    }

    public String getAlias() {
        return this.i;
    }

    public String getCPID() {
        return this.j;
    }

    public void setCPID(String str) {
        this.j = str;
    }

    protected Business(Parcel parcel) {
        this.a = parcel.readString();
        this.b = parcel.readString();
        this.c = parcel.readString();
        this.d = parcel.readString();
        this.e = parcel.readString();
        this.f = parcel.readString();
        this.g = parcel.readString();
        this.h = parcel.readString();
        this.i = parcel.readString();
        this.j = parcel.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.a);
        parcel.writeString(this.b);
        parcel.writeString(this.c);
        parcel.writeString(this.d);
        parcel.writeString(this.e);
        parcel.writeString(this.f);
        parcel.writeString(this.g);
        parcel.writeString(this.h);
        parcel.writeString(this.i);
        parcel.writeString(this.j);
    }
}
