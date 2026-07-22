package com.amap.api.services.route;

import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.services.core.LatLonPoint;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class DrivePlanStep implements Parcelable {
    public static final Parcelable.Creator<DrivePlanStep> CREATOR = new Parcelable.Creator<DrivePlanStep>() { // from class: com.amap.api.services.route.DrivePlanStep.1
        @Override // android.os.Parcelable.Creator
        public final /* bridge */ /* synthetic */ DrivePlanStep[] newArray(int i) {
            return null;
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ DrivePlanStep createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        private static DrivePlanStep a(Parcel parcel) {
            return new DrivePlanStep(parcel);
        }
    };
    private String a;
    private String b;
    private float c;
    private boolean d;
    private List<LatLonPoint> e;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getRoad() {
        return this.a;
    }

    public void setAdCode(String str) {
        this.b = str;
    }

    public String getAdCode() {
        return this.b;
    }

    public void setRoad(String str) {
        this.a = str;
    }

    public float getDistance() {
        return this.c;
    }

    public void setDistance(float f) {
        this.c = f;
    }

    public boolean getToll() {
        return this.d;
    }

    public void setToll(boolean z) {
        this.d = z;
    }

    public List<LatLonPoint> getPolyline() {
        return this.e;
    }

    public void setPolyline(List<LatLonPoint> list) {
        this.e = list;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.a);
        parcel.writeString(this.b);
        parcel.writeFloat(this.c);
        parcel.writeInt(this.d ? 1 : 0);
        parcel.writeFloat(this.c);
        parcel.writeTypedList(this.e);
    }

    public DrivePlanStep(Parcel parcel) {
        this.e = new ArrayList();
        this.a = parcel.readString();
        this.b = parcel.readString();
        this.c = parcel.readFloat();
        this.d = parcel.readInt() == 1;
        this.c = parcel.readFloat();
        this.e = parcel.createTypedArrayList(LatLonPoint.CREATOR);
    }

    public DrivePlanStep() {
        this.e = new ArrayList();
    }
}
