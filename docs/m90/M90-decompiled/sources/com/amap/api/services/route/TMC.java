package com.amap.api.services.route;

import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.services.core.LatLonPoint;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class TMC implements Parcelable {
    public static final Parcelable.Creator<TMC> CREATOR = new Parcelable.Creator<TMC>() { // from class: com.amap.api.services.route.TMC.1
        @Override // android.os.Parcelable.Creator
        public final /* bridge */ /* synthetic */ TMC[] newArray(int i) {
            return null;
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ TMC createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        private static TMC a(Parcel parcel) {
            return new TMC(parcel);
        }
    };
    private int a;
    private String b;
    private List<LatLonPoint> c;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getDistance() {
        return this.a;
    }

    public String getStatus() {
        return this.b;
    }

    public void setDistance(int i) {
        this.a = i;
    }

    public void setStatus(String str) {
        this.b = str;
    }

    public List<LatLonPoint> getPolyline() {
        return this.c;
    }

    public void setPolyline(List<LatLonPoint> list) {
        this.c = list;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.a);
        parcel.writeString(this.b);
        parcel.writeTypedList(this.c);
    }

    public TMC(Parcel parcel) {
        this.c = new ArrayList();
        this.a = parcel.readInt();
        this.b = parcel.readString();
        this.c = parcel.createTypedArrayList(LatLonPoint.CREATOR);
    }

    public TMC() {
        this.c = new ArrayList();
    }
}
