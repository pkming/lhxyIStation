package com.amap.api.services.auto;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class Meta implements Parcelable {
    public static final Parcelable.Creator<Meta> CREATOR = new Parcelable.Creator<Meta>() { // from class: com.amap.api.services.auto.Meta.1
        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ Meta createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ Meta[] newArray(int i) {
            return a(i);
        }

        private static Meta a(Parcel parcel) {
            return new Meta(parcel);
        }

        private static Meta[] a(int i) {
            return new Meta[i];
        }
    };
    public String listBizType;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Meta() {
    }

    protected Meta(Parcel parcel) {
        this.listBizType = parcel.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.listBizType);
    }
}
