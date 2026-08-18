package com.amap.api.services.poisearch;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class IndoorDataV2 implements Parcelable {
    public static final Parcelable.Creator<IndoorDataV2> CREATOR = new Parcelable.Creator<IndoorDataV2>() { // from class: com.amap.api.services.poisearch.IndoorDataV2.1
        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ IndoorDataV2 createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ IndoorDataV2[] newArray(int i) {
            return a(i);
        }

        private static IndoorDataV2 a(Parcel parcel) {
            return new IndoorDataV2(parcel);
        }

        private static IndoorDataV2[] a(int i) {
            return new IndoorDataV2[i];
        }
    };
    private boolean a;
    private String b;
    private int c;
    private String d;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public IndoorDataV2(boolean z, String str, int i, String str2) {
        this.a = z;
        this.b = str;
        this.c = i;
        this.d = str2;
    }

    public void setIndoorMap(boolean z) {
        this.a = z;
    }

    public boolean isIndoorMap() {
        return this.a;
    }

    public String getPoiId() {
        return this.b;
    }

    public void setPoiId(String str) {
        this.b = str;
    }

    public int getFloor() {
        return this.c;
    }

    public void setFloor(int i) {
        this.c = i;
    }

    public String getFloorName() {
        return this.d;
    }

    public void setFloorName(String str) {
        this.d = str;
    }

    protected IndoorDataV2(Parcel parcel) {
        boolean[] zArr = new boolean[1];
        parcel.readBooleanArray(zArr);
        this.a = zArr[0];
        this.b = parcel.readString();
        this.c = parcel.readInt();
        this.d = parcel.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBooleanArray(new boolean[]{this.a});
        parcel.writeString(this.b);
        parcel.writeInt(this.c);
        parcel.writeString(this.d);
    }
}
