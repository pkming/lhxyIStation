package com.amap.api.services.route;

import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearchV2;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class RideRouteResultV2 extends RouteResult implements Parcelable {
    public static final Parcelable.Creator<RideRouteResultV2> CREATOR = new Parcelable.Creator<RideRouteResultV2>() { // from class: com.amap.api.services.route.RideRouteResultV2.1
        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ RideRouteResultV2 createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ RideRouteResultV2[] newArray(int i) {
            return a(i);
        }

        private static RideRouteResultV2 a(Parcel parcel) {
            return new RideRouteResultV2(parcel);
        }

        private static RideRouteResultV2[] a(int i) {
            return new RideRouteResultV2[i];
        }
    };
    private List<RidePath> a;
    private RouteSearchV2.RideRouteQuery b;

    @Override // com.amap.api.services.route.RouteResult, android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public List<RidePath> getPaths() {
        return this.a;
    }

    public void setPaths(List<RidePath> list) {
        this.a = list;
    }

    public RouteSearchV2.RideRouteQuery getRideQuery() {
        return this.b;
    }

    public void setRideQuery(RouteSearchV2.RideRouteQuery rideRouteQuery) {
        this.b = rideRouteQuery;
    }

    @Override // com.amap.api.services.route.RouteResult, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeTypedList(this.a);
        parcel.writeParcelable(this.b, i);
    }

    public RideRouteResultV2(Parcel parcel) {
        super(parcel);
        this.a = new ArrayList();
        this.a = parcel.createTypedArrayList(RidePath.CREATOR);
        this.b = (RouteSearchV2.RideRouteQuery) parcel.readParcelable(RouteSearch.RideRouteQuery.class.getClassLoader());
    }

    public RideRouteResultV2() {
        this.a = new ArrayList();
    }
}
