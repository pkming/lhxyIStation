package com.amap.api.services.route;

import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.services.core.LatLonPoint;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class DriveStep implements Parcelable {
    public static final Parcelable.Creator<DriveStep> CREATOR = new Parcelable.Creator<DriveStep>() { // from class: com.amap.api.services.route.DriveStep.1
        @Override // android.os.Parcelable.Creator
        public final /* bridge */ /* synthetic */ DriveStep[] newArray(int i) {
            return null;
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ DriveStep createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        private static DriveStep a(Parcel parcel) {
            return new DriveStep(parcel);
        }
    };
    private String a;
    private String b;
    private String c;
    private float d;
    private float e;
    private float f;
    private String g;
    private float h;
    private List<LatLonPoint> i;
    private String j;
    private String k;
    private List<RouteSearchCity> l;
    private List<TMC> m;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
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

    public float getDistance() {
        return this.d;
    }

    public void setDistance(float f) {
        this.d = f;
    }

    public float getTolls() {
        return this.e;
    }

    public void setTolls(float f) {
        this.e = f;
    }

    public float getTollDistance() {
        return this.f;
    }

    public void setTollDistance(float f) {
        this.f = f;
    }

    public String getTollRoad() {
        return this.g;
    }

    public void setTollRoad(String str) {
        this.g = str;
    }

    public float getDuration() {
        return this.h;
    }

    public void setDuration(float f) {
        this.h = f;
    }

    public List<LatLonPoint> getPolyline() {
        return this.i;
    }

    public void setPolyline(List<LatLonPoint> list) {
        this.i = list;
    }

    public String getAction() {
        return this.j;
    }

    public void setAction(String str) {
        this.j = str;
    }

    public String getAssistantAction() {
        return this.k;
    }

    public void setAssistantAction(String str) {
        this.k = str;
    }

    public List<RouteSearchCity> getRouteSearchCityList() {
        return this.l;
    }

    public void setRouteSearchCityList(List<RouteSearchCity> list) {
        this.l = list;
    }

    public List<TMC> getTMCs() {
        return this.m;
    }

    public void setTMCs(List<TMC> list) {
        this.m = list;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.a);
        parcel.writeString(this.b);
        parcel.writeString(this.c);
        parcel.writeFloat(this.d);
        parcel.writeFloat(this.e);
        parcel.writeFloat(this.f);
        parcel.writeString(this.g);
        parcel.writeFloat(this.h);
        parcel.writeTypedList(this.i);
        parcel.writeString(this.j);
        parcel.writeString(this.k);
        parcel.writeTypedList(this.l);
        parcel.writeTypedList(this.m);
    }

    public DriveStep(Parcel parcel) {
        this.i = new ArrayList();
        this.l = new ArrayList();
        this.m = new ArrayList();
        this.a = parcel.readString();
        this.b = parcel.readString();
        this.c = parcel.readString();
        this.d = parcel.readFloat();
        this.e = parcel.readFloat();
        this.f = parcel.readFloat();
        this.g = parcel.readString();
        this.h = parcel.readFloat();
        this.i = parcel.createTypedArrayList(LatLonPoint.CREATOR);
        this.j = parcel.readString();
        this.k = parcel.readString();
        this.l = parcel.createTypedArrayList(RouteSearchCity.CREATOR);
        this.m = parcel.createTypedArrayList(TMC.CREATOR);
    }

    public DriveStep() {
        this.i = new ArrayList();
        this.l = new ArrayList();
        this.m = new ArrayList();
    }
}
