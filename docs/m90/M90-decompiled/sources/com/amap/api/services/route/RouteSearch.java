package com.amap.api.services.route;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.col.p0003sl.fp;
import com.amap.api.col.p0003sl.ht;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.interfaces.IRouteSearch;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class RouteSearch {
    public static final int BUS_COMFORTABLE = 4;
    public static final int BUS_DEFAULT = 0;
    public static final int BUS_LEASE_CHANGE = 2;
    public static final int BUS_LEASE_WALK = 3;
    public static final int BUS_NO_SUBWAY = 5;
    public static final int BUS_SAVE_MONEY = 1;
    public static final int BusComfortable = 4;
    public static final int BusDefault = 0;
    public static final int BusLeaseChange = 2;
    public static final int BusLeaseWalk = 3;
    public static final int BusNoSubway = 5;
    public static final int BusSaveMoney = 1;
    public static final int DRIVEING_PLAN_AVOID_CONGESTION_CHOICE_HIGHWAY = 9;
    public static final int DRIVEING_PLAN_AVOID_CONGESTION_FASTEST_SAVE_MONEY = 11;
    public static final int DRIVEING_PLAN_AVOID_CONGESTION_NO_HIGHWAY = 4;
    public static final int DRIVEING_PLAN_AVOID_CONGESTION_SAVE_MONEY = 6;
    public static final int DRIVEING_PLAN_AVOID_CONGESTION_SAVE_MONEY_NO_HIGHWAY = 7;
    public static final int DRIVEING_PLAN_CHOICE_HIGHWAY = 8;
    public static final int DRIVEING_PLAN_DEFAULT = 1;
    public static final int DRIVEING_PLAN_FASTEST_SHORTEST = 10;
    public static final int DRIVEING_PLAN_NO_HIGHWAY = 2;
    public static final int DRIVEING_PLAN_SAVE_MONEY = 3;
    public static final int DRIVEING_PLAN_SAVE_MONEY_NO_HIGHWAY = 5;
    public static final String DRIVING_EXCLUDE_FERRY = "ferry";
    public static final String DRIVING_EXCLUDE_MOTORWAY = "motorway";
    public static final String DRIVING_EXCLUDE_TOLL = "toll";
    public static final int DRIVING_MULTI_CHOICE_AVOID_CONGESTION = 12;
    public static final int DRIVING_MULTI_CHOICE_AVOID_CONGESTION_NO_HIGHWAY = 15;
    public static final int DRIVING_MULTI_CHOICE_AVOID_CONGESTION_NO_HIGHWAY_SAVE_MONEY = 18;
    public static final int DRIVING_MULTI_CHOICE_AVOID_CONGESTION_SAVE_MONEY = 17;
    public static final int DRIVING_MULTI_CHOICE_HIGHWAY = 19;
    public static final int DRIVING_MULTI_CHOICE_HIGHWAY_AVOID_CONGESTION = 20;
    public static final int DRIVING_MULTI_CHOICE_NO_HIGHWAY = 13;
    public static final int DRIVING_MULTI_CHOICE_SAVE_MONEY = 14;
    public static final int DRIVING_MULTI_CHOICE_SAVE_MONEY_NO_HIGHWAY = 16;
    public static final int DRIVING_MULTI_STRATEGY_FASTEST_SAVE_MONEY_SHORTEST = 5;
    public static final int DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST = 11;
    public static final int DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST_AVOID_CONGESTION = 10;
    public static final int DRIVING_NORMAL_CAR = 0;
    public static final int DRIVING_PLUGIN_HYBRID_CAR = 2;
    public static final int DRIVING_PURE_ELECTRIC_VEHICLE = 1;
    public static final int DRIVING_SINGLE_AVOID_CONGESTION = 4;
    public static final int DRIVING_SINGLE_DEFAULT = 0;
    public static final int DRIVING_SINGLE_NO_EXPRESSWAYS = 3;
    public static final int DRIVING_SINGLE_NO_HIGHWAY = 6;
    public static final int DRIVING_SINGLE_NO_HIGHWAY_SAVE_MONEY = 7;
    public static final int DRIVING_SINGLE_NO_HIGHWAY_SAVE_MONEY_AVOID_CONGESTION = 9;
    public static final int DRIVING_SINGLE_SAVE_MONEY = 1;
    public static final int DRIVING_SINGLE_SAVE_MONEY_AVOID_CONGESTION = 8;
    public static final int DRIVING_SINGLE_SHORTEST = 2;
    public static final int DrivingAvoidCongestion = 4;
    public static final int DrivingDefault = 0;
    public static final int DrivingMultiStrategy = 5;
    public static final int DrivingNoExpressways = 3;
    public static final int DrivingNoHighAvoidCongestionSaveMoney = 9;
    public static final int DrivingNoHighWay = 6;
    public static final int DrivingNoHighWaySaveMoney = 7;
    public static final int DrivingSaveMoney = 1;
    public static final int DrivingSaveMoneyAvoidCongestion = 8;
    public static final int DrivingShortDistance = 2;
    public static final String EXTENSIONS_ALL = "all";
    public static final String EXTENSIONS_BASE = "base";
    public static final int RIDING_DEFAULT = 0;
    public static final int RIDING_FAST = 2;
    public static final int RIDING_RECOMMEND = 1;
    public static final int RidingDefault = 0;
    public static final int RidingFast = 2;
    public static final int RidingRecommend = 1;
    public static final int TRUCK_AVOID_CONGESTION = 1;
    public static final int TRUCK_AVOID_CONGESTION_CHOICE_HIGHWAY = 9;
    public static final int TRUCK_AVOID_CONGESTION_NO_HIGHWAY = 4;
    public static final int TRUCK_AVOID_CONGESTION__SAVE_MONEY = 6;
    public static final int TRUCK_AVOID_CONGESTION__SAVE_MONEY_NO_HIGHWAY = 7;
    public static final int TRUCK_CHOICE_HIGHWAY = 8;
    public static final int TRUCK_NO_HIGHWAY = 2;
    public static final int TRUCK_SAVE_MONEY = 3;
    public static final int TRUCK_SAVE_MONEY_NO_HIGHWAY = 5;
    public static final int TRUCK_SIZE_HEAVY = 4;
    public static final int TRUCK_SIZE_LIGHT = 2;
    public static final int TRUCK_SIZE_MEDIUM = 3;
    public static final int TRUCK_SIZE_MINI = 1;
    public static final int WALK_DEFAULT = 0;
    public static final int WALK_MULTI_PATH = 1;
    public static final int WalkDefault = 0;
    public static final int WalkMultipath = 1;
    private IRouteSearch a;

    public interface OnRoutePlanSearchListener {
        void onDriveRoutePlanSearched(DriveRoutePlanResult driveRoutePlanResult, int i);
    }

    public interface OnRouteSearchListener {
        void onBusRouteSearched(BusRouteResult busRouteResult, int i);

        void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i);

        void onRideRouteSearched(RideRouteResult rideRouteResult, int i);

        void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i);
    }

    public interface OnTruckRouteSearchListener {
        void onTruckRouteSearched(TruckRouteRestult truckRouteRestult, int i);
    }

    public RouteSearch(Context context) throws AMapException {
        if (this.a == null) {
            try {
                this.a = new ht(context);
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof AMapException) {
                    throw ((AMapException) e);
                }
            }
        }
    }

    public void setRouteSearchListener(OnRouteSearchListener onRouteSearchListener) {
        IRouteSearch iRouteSearch = this.a;
        if (iRouteSearch != null) {
            iRouteSearch.setRouteSearchListener(onRouteSearchListener);
        }
    }

    public void setOnTruckRouteSearchListener(OnTruckRouteSearchListener onTruckRouteSearchListener) {
        IRouteSearch iRouteSearch = this.a;
        if (iRouteSearch != null) {
            iRouteSearch.setOnTruckRouteSearchListener(onTruckRouteSearchListener);
        }
    }

    public void setOnRoutePlanSearchListener(OnRoutePlanSearchListener onRoutePlanSearchListener) {
        IRouteSearch iRouteSearch = this.a;
        if (iRouteSearch != null) {
            iRouteSearch.setOnRoutePlanSearchListener(onRoutePlanSearchListener);
        }
    }

    public WalkRouteResult calculateWalkRoute(WalkRouteQuery walkRouteQuery) throws AMapException {
        IRouteSearch iRouteSearch = this.a;
        if (iRouteSearch != null) {
            return iRouteSearch.calculateWalkRoute(walkRouteQuery);
        }
        return null;
    }

    public void calculateWalkRouteAsyn(WalkRouteQuery walkRouteQuery) {
        IRouteSearch iRouteSearch = this.a;
        if (iRouteSearch != null) {
            iRouteSearch.calculateWalkRouteAsyn(walkRouteQuery);
        }
    }

    public BusRouteResult calculateBusRoute(BusRouteQuery busRouteQuery) throws AMapException {
        IRouteSearch iRouteSearch = this.a;
        if (iRouteSearch != null) {
            return iRouteSearch.calculateBusRoute(busRouteQuery);
        }
        return null;
    }

    public void calculateBusRouteAsyn(BusRouteQuery busRouteQuery) {
        IRouteSearch iRouteSearch = this.a;
        if (iRouteSearch != null) {
            iRouteSearch.calculateBusRouteAsyn(busRouteQuery);
        }
    }

    public DriveRouteResult calculateDriveRoute(DriveRouteQuery driveRouteQuery) throws AMapException {
        IRouteSearch iRouteSearch = this.a;
        if (iRouteSearch != null) {
            return iRouteSearch.calculateDriveRoute(driveRouteQuery);
        }
        return null;
    }

    public void calculateDriveRouteAsyn(DriveRouteQuery driveRouteQuery) {
        IRouteSearch iRouteSearch = this.a;
        if (iRouteSearch != null) {
            iRouteSearch.calculateDriveRouteAsyn(driveRouteQuery);
        }
    }

    public void calculateRideRouteAsyn(RideRouteQuery rideRouteQuery) {
        IRouteSearch iRouteSearch = this.a;
        if (iRouteSearch != null) {
            iRouteSearch.calculateRideRouteAsyn(rideRouteQuery);
        }
    }

    public RideRouteResult calculateRideRoute(RideRouteQuery rideRouteQuery) throws AMapException {
        IRouteSearch iRouteSearch = this.a;
        if (iRouteSearch != null) {
            return iRouteSearch.calculateRideRoute(rideRouteQuery);
        }
        return null;
    }

    public TruckRouteRestult calculateTruckRoute(TruckRouteQuery truckRouteQuery) throws AMapException {
        IRouteSearch iRouteSearch = this.a;
        if (iRouteSearch != null) {
            return iRouteSearch.calculateTruckRoute(truckRouteQuery);
        }
        return null;
    }

    public void calculateTruckRouteAsyn(TruckRouteQuery truckRouteQuery) {
        IRouteSearch iRouteSearch = this.a;
        if (iRouteSearch != null) {
            iRouteSearch.calculateTruckRouteAsyn(truckRouteQuery);
        }
    }

    public DriveRoutePlanResult calculateDrivePlan(DrivePlanQuery drivePlanQuery) throws AMapException {
        IRouteSearch iRouteSearch = this.a;
        if (iRouteSearch != null) {
            return iRouteSearch.calculateDrivePlan(drivePlanQuery);
        }
        return null;
    }

    public void calculateDrivePlanAsyn(DrivePlanQuery drivePlanQuery) {
        IRouteSearch iRouteSearch = this.a;
        if (iRouteSearch != null) {
            iRouteSearch.calculateDrivePlanAsyn(drivePlanQuery);
        }
    }

    public static class FromAndTo implements Parcelable, Cloneable {
        public static final Parcelable.Creator<FromAndTo> CREATOR = new Parcelable.Creator<FromAndTo>() { // from class: com.amap.api.services.route.RouteSearch.FromAndTo.1
            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ FromAndTo createFromParcel(Parcel parcel) {
                return a(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ FromAndTo[] newArray(int i) {
                return a(i);
            }

            private static FromAndTo a(Parcel parcel) {
                return new FromAndTo(parcel);
            }

            private static FromAndTo[] a(int i) {
                return new FromAndTo[i];
            }
        };
        private LatLonPoint a;
        private LatLonPoint b;
        private String c;
        private String d;
        private String e;
        private String f;
        private String g;
        private String h;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public FromAndTo(LatLonPoint latLonPoint, LatLonPoint latLonPoint2) {
            this.a = latLonPoint;
            this.b = latLonPoint2;
        }

        public LatLonPoint getFrom() {
            return this.a;
        }

        public LatLonPoint getTo() {
            return this.b;
        }

        public String getStartPoiID() {
            return this.c;
        }

        public void setStartPoiID(String str) {
            this.c = str;
        }

        public String getDestinationPoiID() {
            return this.d;
        }

        public void setDestinationPoiID(String str) {
            this.d = str;
        }

        public String getOriginType() {
            return this.e;
        }

        public void setOriginType(String str) {
            this.e = str;
        }

        public String getDestinationType() {
            return this.f;
        }

        public void setDestinationType(String str) {
            this.f = str;
        }

        public String getPlateProvince() {
            return this.g;
        }

        public void setPlateProvince(String str) {
            this.g = str;
        }

        public String getPlateNumber() {
            return this.h;
        }

        public void setPlateNumber(String str) {
            this.h = str;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(this.a, i);
            parcel.writeParcelable(this.b, i);
            parcel.writeString(this.c);
            parcel.writeString(this.d);
            parcel.writeString(this.e);
            parcel.writeString(this.f);
        }

        public FromAndTo(Parcel parcel) {
            this.a = (LatLonPoint) parcel.readParcelable(LatLonPoint.class.getClassLoader());
            this.b = (LatLonPoint) parcel.readParcelable(LatLonPoint.class.getClassLoader());
            this.c = parcel.readString();
            this.d = parcel.readString();
            this.e = parcel.readString();
            this.f = parcel.readString();
        }

        public FromAndTo() {
        }

        public int hashCode() {
            String str = this.d;
            int iHashCode = ((str == null ? 0 : str.hashCode()) + 31) * 31;
            LatLonPoint latLonPoint = this.a;
            int iHashCode2 = (iHashCode + (latLonPoint == null ? 0 : latLonPoint.hashCode())) * 31;
            String str2 = this.c;
            int iHashCode3 = (iHashCode2 + (str2 == null ? 0 : str2.hashCode())) * 31;
            LatLonPoint latLonPoint2 = this.b;
            int iHashCode4 = (iHashCode3 + (latLonPoint2 == null ? 0 : latLonPoint2.hashCode())) * 31;
            String str3 = this.e;
            int iHashCode5 = (iHashCode4 + (str3 == null ? 0 : str3.hashCode())) * 31;
            String str4 = this.f;
            return iHashCode5 + (str4 != null ? str4.hashCode() : 0);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            FromAndTo fromAndTo = (FromAndTo) obj;
            String str = this.d;
            if (str == null) {
                if (fromAndTo.d != null) {
                    return false;
                }
            } else if (!str.equals(fromAndTo.d)) {
                return false;
            }
            LatLonPoint latLonPoint = this.a;
            if (latLonPoint == null) {
                if (fromAndTo.a != null) {
                    return false;
                }
            } else if (!latLonPoint.equals(fromAndTo.a)) {
                return false;
            }
            String str2 = this.c;
            if (str2 == null) {
                if (fromAndTo.c != null) {
                    return false;
                }
            } else if (!str2.equals(fromAndTo.c)) {
                return false;
            }
            LatLonPoint latLonPoint2 = this.b;
            if (latLonPoint2 == null) {
                if (fromAndTo.b != null) {
                    return false;
                }
            } else if (!latLonPoint2.equals(fromAndTo.b)) {
                return false;
            }
            String str3 = this.e;
            if (str3 == null) {
                if (fromAndTo.e != null) {
                    return false;
                }
            } else if (!str3.equals(fromAndTo.e)) {
                return false;
            }
            String str4 = this.f;
            if (str4 == null) {
                if (fromAndTo.f != null) {
                    return false;
                }
            } else if (!str4.equals(fromAndTo.f)) {
                return false;
            }
            return true;
        }

        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public FromAndTo m60clone() {
            try {
                super.clone();
            } catch (CloneNotSupportedException e) {
                fp.a(e, "RouteSearch", "FromAndToclone");
            }
            FromAndTo fromAndTo = new FromAndTo(this.a, this.b);
            fromAndTo.setStartPoiID(this.c);
            fromAndTo.setDestinationPoiID(this.d);
            fromAndTo.setOriginType(this.e);
            fromAndTo.setDestinationType(this.f);
            return fromAndTo;
        }
    }

    public static class BusRouteQuery implements Parcelable, Cloneable {
        public static final Parcelable.Creator<BusRouteQuery> CREATOR = new Parcelable.Creator<BusRouteQuery>() { // from class: com.amap.api.services.route.RouteSearch.BusRouteQuery.1
            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ BusRouteQuery createFromParcel(Parcel parcel) {
                return a(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ BusRouteQuery[] newArray(int i) {
                return a(i);
            }

            private static BusRouteQuery a(Parcel parcel) {
                return new BusRouteQuery(parcel);
            }

            private static BusRouteQuery[] a(int i) {
                return new BusRouteQuery[i];
            }
        };
        private FromAndTo a;
        private int b;
        private String c;
        private String d;
        private int e;
        private String f;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public BusRouteQuery(FromAndTo fromAndTo, int i, String str, int i2) {
            this.f = "base";
            this.a = fromAndTo;
            this.b = i;
            this.c = str;
            this.e = i2;
        }

        public FromAndTo getFromAndTo() {
            return this.a;
        }

        public int getMode() {
            return this.b;
        }

        public String getCity() {
            return this.c;
        }

        public int getNightFlag() {
            return this.e;
        }

        public String getCityd() {
            return this.d;
        }

        public void setCityd(String str) {
            this.d = str;
        }

        public String getExtensions() {
            return this.f;
        }

        public void setExtensions(String str) {
            this.f = str;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(this.a, i);
            parcel.writeInt(this.b);
            parcel.writeString(this.c);
            parcel.writeInt(this.e);
            parcel.writeString(this.d);
            parcel.writeString(this.f);
        }

        public BusRouteQuery(Parcel parcel) {
            this.f = "base";
            this.a = (FromAndTo) parcel.readParcelable(FromAndTo.class.getClassLoader());
            this.b = parcel.readInt();
            this.c = parcel.readString();
            this.e = parcel.readInt();
            this.d = parcel.readString();
            this.f = parcel.readString();
        }

        public BusRouteQuery() {
            this.f = "base";
        }

        public int hashCode() {
            String str = this.c;
            int iHashCode = ((str == null ? 0 : str.hashCode()) + 31) * 31;
            FromAndTo fromAndTo = this.a;
            int iHashCode2 = (((((iHashCode + (fromAndTo == null ? 0 : fromAndTo.hashCode())) * 31) + this.b) * 31) + this.e) * 31;
            String str2 = this.d;
            return iHashCode2 + (str2 != null ? str2.hashCode() : 0);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            BusRouteQuery busRouteQuery = (BusRouteQuery) obj;
            String str = this.c;
            if (str == null) {
                if (busRouteQuery.c != null) {
                    return false;
                }
            } else if (!str.equals(busRouteQuery.c)) {
                return false;
            }
            String str2 = this.d;
            if (str2 == null) {
                if (busRouteQuery.d != null) {
                    return false;
                }
            } else if (!str2.equals(busRouteQuery.d)) {
                return false;
            }
            String str3 = this.f;
            if (str3 == null) {
                if (busRouteQuery.f != null) {
                    return false;
                }
            } else if (!str3.equals(busRouteQuery.f)) {
                return false;
            }
            FromAndTo fromAndTo = this.a;
            if (fromAndTo == null) {
                if (busRouteQuery.a != null) {
                    return false;
                }
            } else if (!fromAndTo.equals(busRouteQuery.a)) {
                return false;
            }
            return this.b == busRouteQuery.b && this.e == busRouteQuery.e;
        }

        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public BusRouteQuery m57clone() {
            try {
                super.clone();
            } catch (CloneNotSupportedException e) {
                fp.a(e, "RouteSearch", "BusRouteQueryclone");
            }
            BusRouteQuery busRouteQuery = new BusRouteQuery(this.a, this.b, this.c, this.e);
            busRouteQuery.setCityd(this.d);
            busRouteQuery.setExtensions(this.f);
            return busRouteQuery;
        }
    }

    public static class WalkRouteQuery implements Parcelable, Cloneable {
        public static final Parcelable.Creator<WalkRouteQuery> CREATOR = new Parcelable.Creator<WalkRouteQuery>() { // from class: com.amap.api.services.route.RouteSearch.WalkRouteQuery.1
            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ WalkRouteQuery createFromParcel(Parcel parcel) {
                return a(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ WalkRouteQuery[] newArray(int i) {
                return a(i);
            }

            private static WalkRouteQuery a(Parcel parcel) {
                return new WalkRouteQuery(parcel);
            }

            private static WalkRouteQuery[] a(int i) {
                return new WalkRouteQuery[i];
            }
        };
        private FromAndTo a;
        private int b;
        private String c;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public WalkRouteQuery(FromAndTo fromAndTo, int i) {
            this.c = "base";
            this.a = fromAndTo;
            this.b = i;
        }

        public WalkRouteQuery(FromAndTo fromAndTo) {
            this.c = "base";
            this.a = fromAndTo;
        }

        public FromAndTo getFromAndTo() {
            return this.a;
        }

        public int getMode() {
            return this.b;
        }

        public String getExtensions() {
            return this.c;
        }

        public void setExtensions(String str) {
            this.c = str;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(this.a, i);
            parcel.writeInt(this.b);
            parcel.writeString(this.c);
        }

        public WalkRouteQuery(Parcel parcel) {
            this.c = "base";
            this.a = (FromAndTo) parcel.readParcelable(FromAndTo.class.getClassLoader());
            this.b = parcel.readInt();
            this.c = parcel.readString();
        }

        public WalkRouteQuery() {
            this.c = "base";
        }

        public int hashCode() {
            FromAndTo fromAndTo = this.a;
            return (((fromAndTo == null ? 0 : fromAndTo.hashCode()) + 31) * 31) + this.b;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            WalkRouteQuery walkRouteQuery = (WalkRouteQuery) obj;
            FromAndTo fromAndTo = this.a;
            if (fromAndTo == null) {
                if (walkRouteQuery.a != null) {
                    return false;
                }
            } else if (!fromAndTo.equals(walkRouteQuery.a)) {
                return false;
            }
            String str = this.c;
            if (str == null) {
                if (walkRouteQuery.c != null) {
                    return false;
                }
            } else if (!str.equals(walkRouteQuery.c)) {
                return false;
            }
            return this.b == walkRouteQuery.b;
        }

        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public WalkRouteQuery m63clone() {
            try {
                super.clone();
            } catch (CloneNotSupportedException e) {
                fp.a(e, "RouteSearch", "WalkRouteQueryclone");
            }
            WalkRouteQuery walkRouteQuery = new WalkRouteQuery(this.a);
            walkRouteQuery.setExtensions(this.c);
            return walkRouteQuery;
        }
    }

    public static class DriveRouteQuery implements Parcelable, Cloneable {
        public static final Parcelable.Creator<DriveRouteQuery> CREATOR = new Parcelable.Creator<DriveRouteQuery>() { // from class: com.amap.api.services.route.RouteSearch.DriveRouteQuery.1
            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ DriveRouteQuery createFromParcel(Parcel parcel) {
                return a(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ DriveRouteQuery[] newArray(int i) {
                return a(i);
            }

            private static DriveRouteQuery a(Parcel parcel) {
                return new DriveRouteQuery(parcel);
            }

            private static DriveRouteQuery[] a(int i) {
                return new DriveRouteQuery[i];
            }
        };
        private FromAndTo a;
        private int b;
        private List<LatLonPoint> c;
        private List<List<LatLonPoint>> d;
        private String e;
        private boolean f;
        private int g;
        private String h;
        private String i;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public DriveRouteQuery(FromAndTo fromAndTo, int i, List<LatLonPoint> list, List<List<LatLonPoint>> list2, String str) {
            this.f = true;
            this.g = 0;
            this.h = null;
            this.i = "base";
            this.a = fromAndTo;
            this.b = i;
            this.c = list;
            this.d = list2;
            this.e = str;
        }

        public FromAndTo getFromAndTo() {
            return this.a;
        }

        public int getMode() {
            return this.b;
        }

        public int getCarType() {
            return this.g;
        }

        public List<LatLonPoint> getPassedByPoints() {
            return this.c;
        }

        public List<List<LatLonPoint>> getAvoidpolygons() {
            return this.d;
        }

        public String getAvoidRoad() {
            return this.e;
        }

        public String getPassedPointStr() {
            StringBuffer stringBuffer = new StringBuffer();
            List<LatLonPoint> list = this.c;
            if (list == null || list.size() == 0) {
                return null;
            }
            for (int i = 0; i < this.c.size(); i++) {
                LatLonPoint latLonPoint = this.c.get(i);
                stringBuffer.append(latLonPoint.getLongitude());
                stringBuffer.append(",");
                stringBuffer.append(latLonPoint.getLatitude());
                if (i < this.c.size() - 1) {
                    stringBuffer.append(";");
                }
            }
            return stringBuffer.toString();
        }

        public boolean hasPassPoint() {
            return !fp.a(getPassedPointStr());
        }

        public String getAvoidpolygonsStr() {
            StringBuffer stringBuffer = new StringBuffer();
            List<List<LatLonPoint>> list = this.d;
            if (list == null || list.size() == 0) {
                return null;
            }
            for (int i = 0; i < this.d.size(); i++) {
                List<LatLonPoint> list2 = this.d.get(i);
                for (int i2 = 0; i2 < list2.size(); i2++) {
                    LatLonPoint latLonPoint = list2.get(i2);
                    stringBuffer.append(latLonPoint.getLongitude());
                    stringBuffer.append(",");
                    stringBuffer.append(latLonPoint.getLatitude());
                    if (i2 < list2.size() - 1) {
                        stringBuffer.append(";");
                    }
                }
                if (i < this.d.size() - 1) {
                    stringBuffer.append("|");
                }
            }
            return stringBuffer.toString();
        }

        public boolean hasAvoidpolygons() {
            return !fp.a(getAvoidpolygonsStr());
        }

        public boolean hasAvoidRoad() {
            return !fp.a(getAvoidRoad());
        }

        public String getExclude() {
            return this.h;
        }

        public void setExclude(String str) {
            this.h = str;
        }

        public String getExtensions() {
            return this.i;
        }

        public void setExtensions(String str) {
            this.i = str;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(this.a, i);
            parcel.writeInt(this.b);
            parcel.writeTypedList(this.c);
            List<List<LatLonPoint>> list = this.d;
            if (list == null) {
                parcel.writeInt(0);
            } else {
                parcel.writeInt(list.size());
                Iterator<List<LatLonPoint>> it = this.d.iterator();
                while (it.hasNext()) {
                    parcel.writeTypedList(it.next());
                }
            }
            parcel.writeString(this.e);
            parcel.writeInt(this.f ? 1 : 0);
            parcel.writeInt(this.g);
            parcel.writeString(this.h);
            parcel.writeString(this.i);
        }

        public DriveRouteQuery(Parcel parcel) {
            this.f = true;
            this.g = 0;
            this.h = null;
            this.i = "base";
            this.a = (FromAndTo) parcel.readParcelable(FromAndTo.class.getClassLoader());
            this.b = parcel.readInt();
            this.c = parcel.createTypedArrayList(LatLonPoint.CREATOR);
            int i = parcel.readInt();
            if (i == 0) {
                this.d = null;
            } else {
                this.d = new ArrayList();
            }
            for (int i2 = 0; i2 < i; i2++) {
                this.d.add(parcel.createTypedArrayList(LatLonPoint.CREATOR));
            }
            this.e = parcel.readString();
            this.f = parcel.readInt() == 1;
            this.g = parcel.readInt();
            this.h = parcel.readString();
            this.i = parcel.readString();
        }

        public DriveRouteQuery() {
            this.f = true;
            this.g = 0;
            this.h = null;
            this.i = "base";
        }

        public int hashCode() {
            String str = this.e;
            int iHashCode = ((str == null ? 0 : str.hashCode()) + 31) * 31;
            List<List<LatLonPoint>> list = this.d;
            int iHashCode2 = (iHashCode + (list == null ? 0 : list.hashCode())) * 31;
            FromAndTo fromAndTo = this.a;
            int iHashCode3 = (((iHashCode2 + (fromAndTo == null ? 0 : fromAndTo.hashCode())) * 31) + this.b) * 31;
            List<LatLonPoint> list2 = this.c;
            return ((iHashCode3 + (list2 != null ? list2.hashCode() : 0)) * 31) + this.g;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            DriveRouteQuery driveRouteQuery = (DriveRouteQuery) obj;
            String str = this.e;
            if (str == null) {
                if (driveRouteQuery.e != null) {
                    return false;
                }
            } else if (!str.equals(driveRouteQuery.e)) {
                return false;
            }
            List<List<LatLonPoint>> list = this.d;
            if (list == null) {
                if (driveRouteQuery.d != null) {
                    return false;
                }
            } else if (!list.equals(driveRouteQuery.d)) {
                return false;
            }
            FromAndTo fromAndTo = this.a;
            if (fromAndTo == null) {
                if (driveRouteQuery.a != null) {
                    return false;
                }
            } else if (!fromAndTo.equals(driveRouteQuery.a)) {
                return false;
            }
            if (this.b != driveRouteQuery.b) {
                return false;
            }
            List<LatLonPoint> list2 = this.c;
            if (list2 == null) {
                if (driveRouteQuery.c != null) {
                    return false;
                }
            } else if (!list2.equals(driveRouteQuery.c) || this.f != driveRouteQuery.isUseFerry() || this.g != driveRouteQuery.g) {
                return false;
            }
            String str2 = this.i;
            if (str2 == null) {
                if (driveRouteQuery.i != null) {
                    return false;
                }
            } else if (!str2.equals(driveRouteQuery.i)) {
                return false;
            }
            return true;
        }

        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public DriveRouteQuery m59clone() {
            try {
                super.clone();
            } catch (CloneNotSupportedException e) {
                fp.a(e, "RouteSearch", "DriveRouteQueryclone");
            }
            DriveRouteQuery driveRouteQuery = new DriveRouteQuery(this.a, this.b, this.c, this.d, this.e);
            driveRouteQuery.setUseFerry(this.f);
            driveRouteQuery.setCarType(this.g);
            driveRouteQuery.setExclude(this.h);
            driveRouteQuery.setExtensions(this.i);
            return driveRouteQuery;
        }

        public boolean isUseFerry() {
            return this.f;
        }

        public void setUseFerry(boolean z) {
            this.f = z;
        }

        public void setCarType(int i) {
            this.g = i;
        }
    }

    public static class RideRouteQuery implements Parcelable, Cloneable {
        public static final Parcelable.Creator<RideRouteQuery> CREATOR = new Parcelable.Creator<RideRouteQuery>() { // from class: com.amap.api.services.route.RouteSearch.RideRouteQuery.1
            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ RideRouteQuery createFromParcel(Parcel parcel) {
                return a(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ RideRouteQuery[] newArray(int i) {
                return a(i);
            }

            private static RideRouteQuery a(Parcel parcel) {
                return new RideRouteQuery(parcel);
            }

            private static RideRouteQuery[] a(int i) {
                return new RideRouteQuery[i];
            }
        };
        private FromAndTo a;
        private int b;
        private String c;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public RideRouteQuery(FromAndTo fromAndTo, int i) {
            this.c = "base";
            this.a = fromAndTo;
            this.b = i;
        }

        public RideRouteQuery(FromAndTo fromAndTo) {
            this.c = "base";
            this.a = fromAndTo;
        }

        public FromAndTo getFromAndTo() {
            return this.a;
        }

        public int getMode() {
            return this.b;
        }

        public String getExtensions() {
            return this.c;
        }

        public void setExtensions(String str) {
            this.c = str;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(this.a, i);
            parcel.writeInt(this.b);
            parcel.writeString(this.c);
        }

        public RideRouteQuery(Parcel parcel) {
            this.c = "base";
            this.a = (FromAndTo) parcel.readParcelable(FromAndTo.class.getClassLoader());
            this.b = parcel.readInt();
            this.c = parcel.readString();
        }

        public RideRouteQuery() {
            this.c = "base";
        }

        public int hashCode() {
            FromAndTo fromAndTo = this.a;
            return (((fromAndTo == null ? 0 : fromAndTo.hashCode()) + 31) * 31) + this.b;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            RideRouteQuery rideRouteQuery = (RideRouteQuery) obj;
            FromAndTo fromAndTo = this.a;
            if (fromAndTo == null) {
                if (rideRouteQuery.a != null) {
                    return false;
                }
            } else if (!fromAndTo.equals(rideRouteQuery.a)) {
                return false;
            }
            return this.b == rideRouteQuery.b;
        }

        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public RideRouteQuery m61clone() {
            try {
                super.clone();
            } catch (CloneNotSupportedException e) {
                fp.a(e, "RouteSearch", "RideRouteQueryclone");
            }
            RideRouteQuery rideRouteQuery = new RideRouteQuery(this.a);
            rideRouteQuery.setExtensions(this.c);
            return rideRouteQuery;
        }
    }

    public static class TruckRouteQuery implements Parcelable, Cloneable {
        public static final Parcelable.Creator<TruckRouteQuery> CREATOR = new Parcelable.Creator<TruckRouteQuery>() { // from class: com.amap.api.services.route.RouteSearch.TruckRouteQuery.1
            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ TruckRouteQuery createFromParcel(Parcel parcel) {
                return a(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ TruckRouteQuery[] newArray(int i) {
                return a(i);
            }

            private static TruckRouteQuery a(Parcel parcel) {
                return new TruckRouteQuery(parcel);
            }

            private static TruckRouteQuery[] a(int i) {
                return new TruckRouteQuery[i];
            }
        };
        private FromAndTo a;
        private int b;
        private int c;
        private List<LatLonPoint> d;
        private float e;
        private float f;
        private float g;
        private float h;
        private float i;
        private String j;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public TruckRouteQuery(FromAndTo fromAndTo, int i, List<LatLonPoint> list, int i2) {
            this.b = 2;
            this.j = "base";
            this.a = fromAndTo;
            this.c = i;
            this.d = list;
            this.b = i2;
        }

        protected TruckRouteQuery(Parcel parcel) {
            this.b = 2;
            this.j = "base";
            this.a = (FromAndTo) parcel.readParcelable(FromAndTo.class.getClassLoader());
            this.b = parcel.readInt();
            this.c = parcel.readInt();
            this.d = parcel.createTypedArrayList(LatLonPoint.CREATOR);
            this.e = parcel.readFloat();
            this.f = parcel.readFloat();
            this.g = parcel.readFloat();
            this.h = parcel.readFloat();
            this.i = parcel.readFloat();
            this.j = parcel.readString();
        }

        public void setMode(int i) {
            this.c = i;
        }

        public void setTruckSize(int i) {
            this.b = i;
        }

        public void setTruckHeight(float f) {
            this.e = f;
        }

        public void setTruckWidth(float f) {
            this.f = f;
        }

        public void setTruckLoad(float f) {
            this.g = f;
        }

        public void setTruckWeight(float f) {
            this.h = f;
        }

        public void setTruckAxis(float f) {
            this.i = f;
        }

        public FromAndTo getFromAndTo() {
            return this.a;
        }

        public int getMode() {
            return this.c;
        }

        public String getExtensions() {
            return this.j;
        }

        public void setExtensions(String str) {
            this.j = str;
        }

        public boolean hasPassPoint() {
            return !fp.a(getPassedPointStr());
        }

        public List<LatLonPoint> getPassedByPoints() {
            return this.d;
        }

        public String getPassedPointStr() {
            StringBuffer stringBuffer = new StringBuffer();
            List<LatLonPoint> list = this.d;
            if (list == null || list.size() == 0) {
                return null;
            }
            for (int i = 0; i < this.d.size(); i++) {
                LatLonPoint latLonPoint = this.d.get(i);
                stringBuffer.append(latLonPoint.getLongitude());
                stringBuffer.append(",");
                stringBuffer.append(latLonPoint.getLatitude());
                if (i < this.d.size() - 1) {
                    stringBuffer.append(";");
                }
            }
            return stringBuffer.toString();
        }

        public int getTruckSize() {
            return this.b;
        }

        public float getTruckHeight() {
            return this.e;
        }

        public float getTruckWidth() {
            return this.f;
        }

        public float getTruckLoad() {
            return this.g;
        }

        public float getTruckWeight() {
            return this.h;
        }

        public float getTruckAxis() {
            return this.i;
        }

        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public TruckRouteQuery m62clone() {
            try {
                super.clone();
            } catch (CloneNotSupportedException e) {
                fp.a(e, "RouteSearch", "TruckRouteQueryclone");
            }
            TruckRouteQuery truckRouteQuery = new TruckRouteQuery(this.a, this.c, this.d, this.b);
            truckRouteQuery.setExtensions(this.j);
            return truckRouteQuery;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(this.a, i);
            parcel.writeInt(this.b);
            parcel.writeInt(this.c);
            parcel.writeTypedList(this.d);
            parcel.writeFloat(this.e);
            parcel.writeFloat(this.f);
            parcel.writeFloat(this.g);
            parcel.writeFloat(this.h);
            parcel.writeFloat(this.i);
            parcel.writeString(this.j);
        }
    }

    public static class DrivePlanQuery implements Parcelable, Cloneable {
        public static final Parcelable.Creator<DrivePlanQuery> CREATOR = new Parcelable.Creator<DrivePlanQuery>() { // from class: com.amap.api.services.route.RouteSearch.DrivePlanQuery.1
            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ DrivePlanQuery createFromParcel(Parcel parcel) {
                return a(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public final /* synthetic */ DrivePlanQuery[] newArray(int i) {
                return a(i);
            }

            private static DrivePlanQuery a(Parcel parcel) {
                return new DrivePlanQuery(parcel);
            }

            private static DrivePlanQuery[] a(int i) {
                return new DrivePlanQuery[i];
            }
        };
        private FromAndTo a;
        private String b;
        private int c;
        private int d;
        private int e;
        private int f;
        private int g;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public DrivePlanQuery(FromAndTo fromAndTo, int i, int i2, int i3) {
            this.c = 1;
            this.d = 0;
            this.e = 0;
            this.f = 0;
            this.g = 48;
            this.a = fromAndTo;
            this.e = i;
            this.f = i2;
            this.g = i3;
        }

        public FromAndTo getFromAndTo() {
            return this.a;
        }

        public String getDestParentPoiID() {
            return this.b;
        }

        public int getMode() {
            return this.c;
        }

        public int getCarType() {
            return this.d;
        }

        public int getFirstTime() {
            return this.e;
        }

        public int getInterval() {
            return this.f;
        }

        public int getCount() {
            return this.g;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(this.a, i);
            parcel.writeString(this.b);
            parcel.writeInt(this.c);
            parcel.writeInt(this.d);
            parcel.writeInt(this.e);
            parcel.writeInt(this.f);
            parcel.writeInt(this.g);
        }

        public DrivePlanQuery() {
            this.c = 1;
            this.d = 0;
            this.e = 0;
            this.f = 0;
            this.g = 48;
        }

        protected DrivePlanQuery(Parcel parcel) {
            this.c = 1;
            this.d = 0;
            this.e = 0;
            this.f = 0;
            this.g = 48;
            this.a = (FromAndTo) parcel.readParcelable(FromAndTo.class.getClassLoader());
            this.b = parcel.readString();
            this.c = parcel.readInt();
            this.d = parcel.readInt();
            this.e = parcel.readInt();
            this.f = parcel.readInt();
            this.g = parcel.readInt();
        }

        public int hashCode() {
            FromAndTo fromAndTo = this.a;
            int iHashCode = ((fromAndTo == null ? 0 : fromAndTo.hashCode()) + 31) * 31;
            String str = this.b;
            return ((((((((((iHashCode + (str != null ? str.hashCode() : 0)) * 31) + this.c) * 31) + this.d) * 31) + this.e) * 31) + this.f) * 31) + this.g;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            DrivePlanQuery drivePlanQuery = (DrivePlanQuery) obj;
            FromAndTo fromAndTo = this.a;
            if (fromAndTo == null) {
                if (drivePlanQuery.a != null) {
                    return false;
                }
            } else if (!fromAndTo.equals(drivePlanQuery.a)) {
                return false;
            }
            String str = this.b;
            if (str == null) {
                if (drivePlanQuery.b != null) {
                    return false;
                }
            } else if (!str.equals(drivePlanQuery.b)) {
                return false;
            }
            return this.c == drivePlanQuery.c && this.d == drivePlanQuery.d && this.e == drivePlanQuery.e && this.f == drivePlanQuery.f && this.g == drivePlanQuery.g;
        }

        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public DrivePlanQuery m58clone() {
            try {
                super.clone();
            } catch (CloneNotSupportedException e) {
                fp.a(e, "RouteSearch", "DriveRouteQueryclone");
            }
            DrivePlanQuery drivePlanQuery = new DrivePlanQuery(this.a, this.e, this.f, this.g);
            drivePlanQuery.setDestParentPoiID(this.b);
            drivePlanQuery.setMode(this.c);
            drivePlanQuery.setCarType(this.d);
            return drivePlanQuery;
        }

        public void setDestParentPoiID(String str) {
            this.b = str;
        }

        public void setMode(int i) {
            this.c = i;
        }

        public void setCarType(int i) {
            this.d = i;
        }
    }
}
