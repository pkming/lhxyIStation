package com.amap.api.services.route;

import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.services.route.DistanceSearch;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class DistanceResult implements Parcelable {
    public static final Parcelable.Creator<DistanceResult> CREATOR = new Parcelable.Creator<DistanceResult>() { // from class: com.amap.api.services.route.DistanceResult.1
        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ DistanceResult createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ DistanceResult[] newArray(int i) {
            return a(i);
        }

        private static DistanceResult a(Parcel parcel) {
            return new DistanceResult(parcel);
        }

        private static DistanceResult[] a(int i) {
            return new DistanceResult[i];
        }
    };
    private DistanceSearch.DistanceQuery a;
    private List<DistanceItem> b;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DistanceResult() {
        this.b = null;
    }

    protected DistanceResult(Parcel parcel) {
        this.b = null;
        this.b = parcel.createTypedArrayList(DistanceItem.CREATOR);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(this.b);
    }

    public void setDistanceQuery(DistanceSearch.DistanceQuery distanceQuery) {
        this.a = distanceQuery;
    }

    public DistanceSearch.DistanceQuery getDistanceQuery() {
        return this.a;
    }

    public List<DistanceItem> getDistanceResults() {
        return this.b;
    }

    public void setDistanceResults(List<DistanceItem> list) {
        this.b = list;
    }
}
