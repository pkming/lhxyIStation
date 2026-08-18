package com.amap.api.services.route;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class WalkPath extends Path implements Parcelable {
    public static final Parcelable.Creator<WalkPath> CREATOR = new Parcelable.Creator<WalkPath>() { // from class: com.amap.api.services.route.WalkPath.1
        @Override // android.os.Parcelable.Creator
        public final /* bridge */ /* synthetic */ WalkPath[] newArray(int i) {
            return null;
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ WalkPath createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        private static WalkPath a(Parcel parcel) {
            return new WalkPath(parcel);
        }
    };
    private List<WalkStep> a;

    @Override // com.amap.api.services.route.Path, android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public List<WalkStep> getSteps() {
        return this.a;
    }

    public void setSteps(List<WalkStep> list) {
        this.a = list;
    }

    @Override // com.amap.api.services.route.Path, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeTypedList(this.a);
    }

    public WalkPath(Parcel parcel) {
        super(parcel);
        this.a = new ArrayList();
        this.a = parcel.createTypedArrayList(WalkStep.CREATOR);
    }

    public WalkPath() {
        this.a = new ArrayList();
    }
}
