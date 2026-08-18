package com.amap.api.services.route;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class Railway implements Parcelable {
    public static final Parcelable.Creator<Railway> CREATOR = new Parcelable.Creator<Railway>() { // from class: com.amap.api.services.route.Railway.1
        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ Railway createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ Railway[] newArray(int i) {
            return a(i);
        }

        private static Railway a(Parcel parcel) {
            return new Railway(parcel);
        }

        private static Railway[] a(int i) {
            return new Railway[i];
        }
    };
    private String a;
    private String b;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Railway() {
    }

    public String getID() {
        return this.a;
    }

    public String getName() {
        return this.b;
    }

    public void setID(String str) {
        this.a = str;
    }

    public void setName(String str) {
        this.b = str;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.a);
        parcel.writeString(this.b);
    }

    protected Railway(Parcel parcel) {
        this.a = parcel.readString();
        this.b = parcel.readString();
    }
}
