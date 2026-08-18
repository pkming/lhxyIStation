package com.amap.api.services.route;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class DrivePathV2 extends Path implements Parcelable {
    public static final Parcelable.Creator<DrivePathV2> CREATOR = new Parcelable.Creator<DrivePathV2>() { // from class: com.amap.api.services.route.DrivePathV2.1
        @Override // android.os.Parcelable.Creator
        public final /* bridge */ /* synthetic */ DrivePathV2[] newArray(int i) {
            return null;
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ DrivePathV2 createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        private static DrivePathV2 a(Parcel parcel) {
            return new DrivePathV2(parcel);
        }
    };
    private String a;
    private List<DriveStepV2> b;
    private int c;
    private Cost d;
    private ElecConsumeInfo e;
    private List<ChargeStationInfo> f;

    @Override // com.amap.api.services.route.Path, android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ElecConsumeInfo getElecConsumeInfo() {
        return this.e;
    }

    public void setElecConsumeInfo(ElecConsumeInfo elecConsumeInfo) {
        this.e = elecConsumeInfo;
    }

    public List<ChargeStationInfo> getChargeStationInfo() {
        return this.f;
    }

    public void setChargeStationInfo(List<ChargeStationInfo> list) {
        this.f = list;
    }

    public Cost getCost() {
        return this.d;
    }

    public void setCost(Cost cost) {
        this.d = cost;
    }

    public String getStrategy() {
        return this.a;
    }

    public void setStrategy(String str) {
        this.a = str;
    }

    public List<DriveStepV2> getSteps() {
        return this.b;
    }

    public void setSteps(List<DriveStepV2> list) {
        this.b = list;
    }

    public int getRestriction() {
        return this.c;
    }

    public void setRestriction(int i) {
        this.c = i;
    }

    @Override // com.amap.api.services.route.Path, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeTypedList(this.b);
    }

    public DrivePathV2(Parcel parcel) {
        super(parcel);
        this.b = new ArrayList();
        this.f = new ArrayList();
        this.a = parcel.readString();
        this.b = parcel.createTypedArrayList(DriveStepV2.CREATOR);
    }

    public DrivePathV2() {
        this.b = new ArrayList();
        this.f = new ArrayList();
    }
}
