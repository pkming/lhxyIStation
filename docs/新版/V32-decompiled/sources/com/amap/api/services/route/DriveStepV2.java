package com.amap.api.services.route;

import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.services.core.LatLonPoint;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class DriveStepV2 implements Parcelable {
    public static final Parcelable.Creator<DriveStepV2> CREATOR = new Parcelable.Creator<DriveStepV2>() { // from class: com.amap.api.services.route.DriveStepV2.1
        @Override // android.os.Parcelable.Creator
        public final /* bridge */ /* synthetic */ DriveStepV2[] newArray(int i) {
            return null;
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ DriveStepV2 createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        private static DriveStepV2 a(Parcel parcel) {
            return new DriveStepV2(parcel);
        }
    };
    private String a;
    private String b;
    private String c;
    private List<LatLonPoint> d;
    private List<RouteSearchCity> e;
    private List<TMC> f;
    private int g;
    private Cost h;
    private Navi i;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Navi getNavi() {
        return this.i;
    }

    public void setNavi(Navi navi) {
        this.i = navi;
    }

    public Cost getCostDetail() {
        return this.h;
    }

    public void setCostDetail(Cost cost) {
        this.h = cost;
    }

    public int getStepDistance() {
        return this.g;
    }

    public void setStepDistance(int i) {
        this.g = i;
    }

    public String getInstruction() {
        return this.a;
    }

    public void setInstruction(String str) {
        this.a = str;
    }

    public String getOrientation() {
        return this.b;
    }

    public void setOrientation(String str) {
        this.b = str;
    }

    public String getRoad() {
        return this.c;
    }

    public void setRoad(String str) {
        this.c = str;
    }

    public List<LatLonPoint> getPolyline() {
        return this.d;
    }

    public void setPolyline(List<LatLonPoint> list) {
        this.d = list;
    }

    public List<RouteSearchCity> getRouteSearchCityList() {
        return this.e;
    }

    public void setRouteSearchCityList(List<RouteSearchCity> list) {
        this.e = list;
    }

    public List<TMC> getTMCs() {
        return this.f;
    }

    public void setTMCs(List<TMC> list) {
        this.f = list;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.a);
        parcel.writeString(this.b);
        parcel.writeString(this.c);
        parcel.writeTypedList(this.d);
        parcel.writeTypedList(this.e);
        parcel.writeTypedList(this.f);
    }

    public DriveStepV2(Parcel parcel) {
        this.d = new ArrayList();
        this.e = new ArrayList();
        this.f = new ArrayList();
        this.g = -1;
        this.a = parcel.readString();
        this.b = parcel.readString();
        this.c = parcel.readString();
        this.d = parcel.createTypedArrayList(LatLonPoint.CREATOR);
        this.e = parcel.createTypedArrayList(RouteSearchCity.CREATOR);
        this.f = parcel.createTypedArrayList(TMC.CREATOR);
    }

    public DriveStepV2() {
        this.d = new ArrayList();
        this.e = new ArrayList();
        this.f = new ArrayList();
        this.g = -1;
    }
}
