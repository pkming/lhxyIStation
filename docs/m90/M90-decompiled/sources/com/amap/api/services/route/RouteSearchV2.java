package com.amap.api.services.route;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.amap.api.col.p0003sl.fp;
import com.amap.api.col.p0003sl.hu;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.interfaces.IRouteSearchV2;
import com.amap.api.services.route.RouteSearch;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class RouteSearchV2 {
    private IRouteSearchV2 a;

    public static class BusMode {
        public static final int BUS_COMFORTABLE = 4;
        public static final int BUS_DEFAULT = 0;
        public static final int BUS_LEASE_CHANGE = 2;
        public static final int BUS_LEASE_WALK = 3;
        public static final int BUS_NO_SUBWAY = 5;
        public static final int BUS_SAVE_MONEY = 1;
        public static final int BUS_SUBWAY = 6;
        public static final int BUS_SUBWAY_FIRST = 7;
        public static final int BUS_WASTE_LESS = 8;
    }

    public interface OnRoutePlanSearchListener {
        void onDriveRoutePlanSearched(DriveRoutePlanResult driveRoutePlanResult, int i);
    }

    public interface OnRouteSearchListener {
        void onBusRouteSearched(BusRouteResultV2 busRouteResultV2, int i);

        void onDriveRouteSearched(DriveRouteResultV2 driveRouteResultV2, int i);

        void onRideRouteSearched(RideRouteResultV2 rideRouteResultV2, int i);

        void onWalkRouteSearched(WalkRouteResultV2 walkRouteResultV2, int i);
    }

    public interface OnTruckRouteSearchListener {
        void onTruckRouteSearched(TruckRouteRestult truckRouteRestult, int i);
    }

    public static class ShowFields {
        public static final int ALL = -1;
        public static final int CHARGE_STATION_INFO = 64;
        public static final int CITIES = 8;
        public static final int COST = 1;
        public static final int ELEC_COSUME_INFO = 32;
        public static final int NAVI = 4;
        public static final int POLINE = 16;
        public static final int TMCS = 2;
    }

    public enum DrivingStrategy {
        DEFAULT(32),
        AVOID_CONGESTION(33),
        HIGHWAY_PRIORITY(34),
        AVOID_HIGHWAY(35),
        LESS_CHARGE(36),
        ROAD_PRIORITY(37),
        SPEED_PRIORITY(38),
        AVOID_CONGESTION_HIGHWAY_PRIORITY(39),
        AVOID_CONGESTION_AVOID_HIGHWAY(40),
        AVOID_CONGESTION_LESS_CHARGE(41),
        LESS_CHARGE_AVOID_HIGHWAY(42),
        AVOID_CONGESTION_LESS_CHARGE_AVOID_HIGHWAY(43),
        AVOID_CONGESTION_ROAD_PRIORITY(44),
        AVOID_CONGESTION_SPEED_PRIORITY(45);

        int a;

        DrivingStrategy(int i) {
            this.a = i;
        }

        public final int getValue() {
            return this.a;
        }

        public static DrivingStrategy fromValue(int i) {
            return values()[i - 32];
        }
    }

    public RouteSearchV2(Context context) throws AMapException {
        if (this.a == null) {
            try {
                this.a = new hu(context);
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof AMapException) {
                    throw ((AMapException) e);
                }
            }
        }
    }

    public void setRouteSearchListener(OnRouteSearchListener onRouteSearchListener) {
        IRouteSearchV2 iRouteSearchV2 = this.a;
        if (iRouteSearchV2 != null) {
            iRouteSearchV2.setRouteSearchListener(onRouteSearchListener);
        }
    }

    public DriveRouteResultV2 calculateDriveRoute(DriveRouteQuery driveRouteQuery) throws AMapException {
        IRouteSearchV2 iRouteSearchV2 = this.a;
        if (iRouteSearchV2 != null) {
            return iRouteSearchV2.calculateDriveRoute(driveRouteQuery);
        }
        return null;
    }

    public void calculateDriveRouteAsyn(DriveRouteQuery driveRouteQuery) {
        IRouteSearchV2 iRouteSearchV2 = this.a;
        if (iRouteSearchV2 != null) {
            iRouteSearchV2.calculateDriveRouteAsyn(driveRouteQuery);
        }
    }

    public WalkRouteResultV2 calculateWalkRoute(WalkRouteQuery walkRouteQuery) throws AMapException {
        IRouteSearchV2 iRouteSearchV2 = this.a;
        if (iRouteSearchV2 != null) {
            return iRouteSearchV2.calculateWalkRoute(walkRouteQuery);
        }
        return null;
    }

    public void calculateWalkRouteAsyn(WalkRouteQuery walkRouteQuery) {
        IRouteSearchV2 iRouteSearchV2 = this.a;
        if (iRouteSearchV2 != null) {
            iRouteSearchV2.calculateWalkRouteAsyn(walkRouteQuery);
        }
    }

    public void calculateRideRouteAsyn(RideRouteQuery rideRouteQuery) {
        IRouteSearchV2 iRouteSearchV2 = this.a;
        if (iRouteSearchV2 != null) {
            iRouteSearchV2.calculateRideRouteAsyn(rideRouteQuery);
        }
    }

    public RideRouteResultV2 calculateRideRoute(RideRouteQuery rideRouteQuery) throws AMapException {
        IRouteSearchV2 iRouteSearchV2 = this.a;
        if (iRouteSearchV2 != null) {
            return iRouteSearchV2.calculateRideRoute(rideRouteQuery);
        }
        return null;
    }

    public BusRouteResultV2 calculateBusRoute(BusRouteQuery busRouteQuery) throws AMapException {
        IRouteSearchV2 iRouteSearchV2 = this.a;
        if (iRouteSearchV2 != null) {
            return iRouteSearchV2.calculateBusRoute(busRouteQuery);
        }
        return null;
    }

    public void calculateBusRouteAsyn(BusRouteQuery busRouteQuery) {
        IRouteSearchV2 iRouteSearchV2 = this.a;
        if (iRouteSearchV2 != null) {
            iRouteSearchV2.calculateBusRouteAsyn(busRouteQuery);
        }
    }

    public static class FromAndTo implements Parcelable, Cloneable {
        public static final Parcelable.Creator<FromAndTo> CREATOR = new Parcelable.Creator<FromAndTo>() { // from class: com.amap.api.services.route.RouteSearchV2.FromAndTo.1
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

        public String getPlateNumber() {
            return this.g;
        }

        public void setPlateNumber(String str) {
            this.g = str;
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
        public FromAndTo m66clone() {
            try {
                super.clone();
            } catch (CloneNotSupportedException e) {
                fp.a(e, "RouteSearchV2", "FromAndToclone");
            }
            FromAndTo fromAndTo = new FromAndTo(this.a, this.b);
            fromAndTo.setStartPoiID(this.c);
            fromAndTo.setDestinationPoiID(this.d);
            fromAndTo.setOriginType(this.e);
            fromAndTo.setDestinationType(this.f);
            return fromAndTo;
        }
    }

    public static class SpeedCost {
        private int a;
        private float b;

        public int getSpeed() {
            return this.a;
        }

        public void setSpeed(int i) {
            this.a = i;
        }

        public float getValue() {
            return this.b;
        }

        public void setValue(float f) {
            this.b = f;
        }
    }

    public static class CurveCost {
        private float a;
        private float b;

        public float getAccess() {
            return this.a;
        }

        public void setAccess(float f) {
            this.a = f;
        }

        public float getValue() {
            return this.b;
        }

        public void setValue(float f) {
            this.b = f;
        }
    }

    public static class SlopeCost {
        private float a;
        private float b;

        public float getUp() {
            return this.a;
        }

        public void setUp(float f) {
            this.a = f;
        }

        public float getDown() {
            return this.b;
        }

        public void setDown(float f) {
            this.b = f;
        }
    }

    public static class TransCost {
        private float a;
        private float b;

        public float getAccess() {
            return this.a;
        }

        public void setAccess(float f) {
            this.a = f;
        }

        public float getDecess() {
            return this.b;
        }

        public void setDecess(float f) {
            this.b = f;
        }
    }

    public static class PowerTrainLoss {
        private int a;
        private float b;
        private int c;
        private int d;

        public int getPowerDemand() {
            return this.a;
        }

        public void setPowerDemand(int i) {
            this.a = i;
        }

        public float getPowerDemandValue() {
            return this.b;
        }

        public void setPowerDemandValue(float f) {
            this.b = f;
        }

        public int getSpeed() {
            return this.c;
        }

        public void setSpeed(int i) {
            this.c = i;
        }

        public int getSpeedValue() {
            return this.d;
        }

        public void setSpeedValue(int i) {
            this.d = i;
        }
    }

    public static class CustomCostMode {
        private List<SpeedCost> a;
        private CurveCost b;
        private SlopeCost c;
        private float d;
        private TransCost e;
        private float f;
        private PowerTrainLoss g;

        public List<SpeedCost> getSpeedCosts() {
            return this.a;
        }

        public void setSpeedCosts(List<SpeedCost> list) {
            this.a = list;
        }

        public CurveCost getCurveCost() {
            return this.b;
        }

        public void setCurveCost(CurveCost curveCost) {
            this.b = curveCost;
        }

        public SlopeCost getSlopeCost() {
            return this.c;
        }

        public void setSlopeCost(SlopeCost slopeCost) {
            this.c = slopeCost;
        }

        public float getAuxCost() {
            return this.d;
        }

        public void setAuxCost(float f) {
            this.d = f;
        }

        public TransCost getTransCost() {
            return this.e;
        }

        public void setTransCost(TransCost transCost) {
            this.e = transCost;
        }

        public float getFerryCost() {
            return this.f;
        }

        public void setFerryCost(float f) {
            this.f = f;
        }

        public PowerTrainLoss getPowerTrainLosses() {
            return this.g;
        }

        public void setPowerTrainLosses(PowerTrainLoss powerTrainLoss) {
            this.g = powerTrainLoss;
        }

        public String toJson() {
            try {
                JSONObject jSONObject = new JSONObject();
                JSONArray jSONArray = new JSONArray();
                List<SpeedCost> list = this.a;
                if (list != null) {
                    for (SpeedCost speedCost : list) {
                        JSONObject jSONObject2 = new JSONObject();
                        jSONObject2.put("speed", speedCost.getSpeed());
                        jSONObject2.put("value", speedCost.getValue());
                        jSONArray.put(jSONObject2);
                    }
                    jSONObject.put("speed_cost", jSONArray);
                }
                if (this.b != null) {
                    JSONObject jSONObject3 = new JSONObject();
                    jSONObject3.put("access", this.b.getAccess());
                    jSONObject3.put("value", this.b.getValue());
                    jSONObject.put("curve_cost", jSONObject3);
                }
                if (this.c != null) {
                    JSONObject jSONObject4 = new JSONObject();
                    jSONObject4.put("up", this.c.getUp());
                    jSONObject4.put("down", this.c.getDown());
                    jSONObject.put("slope_cost", jSONObject4);
                }
                jSONObject.put("aux_cost", this.d);
                if (this.e != null) {
                    JSONObject jSONObject5 = new JSONObject();
                    jSONObject5.put("access", this.e.getAccess());
                    jSONObject5.put("decess", this.e.getDecess());
                    jSONObject.put("trans_cost", jSONObject5);
                }
                jSONObject.put("ferry_cost", this.f);
                if (this.g != null) {
                    JSONArray jSONArray2 = new JSONArray();
                    JSONObject jSONObject6 = new JSONObject();
                    jSONObject6.put("powerdemand", this.g.getPowerDemand());
                    jSONObject6.put("value", this.g.getPowerDemandValue());
                    JSONObject jSONObject7 = new JSONObject();
                    jSONObject7.put("speed", this.g.getSpeed());
                    jSONObject7.put("value", this.g.getSpeedValue());
                    jSONArray2.put(jSONObject6);
                    jSONArray2.put(jSONObject7);
                    jSONObject.put("powertrain_loss", jSONArray2);
                }
                return jSONObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class NewEnergy {
        private String a;
        private CustomCostMode b;
        private String i;
        private float c = -1.0f;
        private float d = -1.0f;
        private float e = 1.5f;
        private float f = 100.0f;
        private float g = 0.0f;
        private float h = 0.0f;
        private int j = 0;

        public String getKey() {
            return this.a;
        }

        public void setKey(String str) {
            this.a = str;
        }

        public CustomCostMode getCustomCostMode() {
            return this.b;
        }

        public void setCustomCostMode(CustomCostMode customCostMode) {
            this.b = customCostMode;
        }

        public float getMaxVehicleCharge() {
            return this.c;
        }

        public void setMaxVehicleCharge(float f) {
            this.c = f;
        }

        public float getVehicleCharge() {
            return this.d;
        }

        public void setVehicleCharge(float f) {
            this.d = f;
        }

        public float getLoad() {
            return this.e;
        }

        public void setLoad(float f) {
            this.e = f;
        }

        public float getLeavingPercent() {
            return this.f;
        }

        public void setLeavingPercent(float f) {
            this.f = f;
        }

        public float getArrivingPercent() {
            return this.g;
        }

        public void setArrivingPercent(float f) {
            this.g = f;
        }

        public float getDestinationArrivingPercent() {
            return this.h;
        }

        public void setDestinationArrivingPercent(float f) {
            this.h = f;
        }

        public String getCustomChargingArguments() {
            return this.i;
        }

        public void setCustomChargingArguments(String str) {
            this.i = str;
        }

        public int getWaypointsArrivingPercent() {
            return this.j;
        }

        public void setWaypointsArrivingPercent(int i) {
            this.j = i;
        }

        public String buildParam() {
            StringBuilder sb = new StringBuilder();
            if (this.a != null) {
                sb.append("&key=").append(this.a);
            }
            if (this.b != null) {
                sb.append("&custom_cost_mode=").append(this.b.toJson());
            }
            if (this.c > 0.0f) {
                sb.append("&max_vehicle_charge=").append(this.c);
            }
            if (this.d > 0.0f) {
                sb.append("&vehicle_charge=").append(this.d);
            }
            sb.append("&load=").append(this.e);
            sb.append("&leaving_percent=").append(this.f);
            sb.append("&arriving_percent=").append(this.g);
            sb.append("&destination_arriving_percent=").append(this.h);
            if (this.i != null) {
                sb.append("&custom_charging_arguments=").append(this.i);
            }
            if (this.j > 0) {
                sb.append("&waypoints_arriving_percent=").append(this.j);
            }
            return sb.toString();
        }
    }

    public class AlternativeRoute {
        public static final int ALTERNATIVE_ROUTE_ONE = 1;
        public static final int ALTERNATIVE_ROUTE_THREE = 3;
        public static final int ALTERNATIVE_ROUTE_TWO = 2;

        public AlternativeRoute() {
        }
    }

    public static class WalkRouteQuery implements Parcelable, Cloneable {
        public static final Parcelable.Creator<WalkRouteQuery> CREATOR = new Parcelable.Creator<WalkRouteQuery>() { // from class: com.amap.api.services.route.RouteSearchV2.WalkRouteQuery.1
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
        private boolean c;
        private int d;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public WalkRouteQuery(FromAndTo fromAndTo) {
            this.b = 1;
            this.c = false;
            this.d = 1;
            this.a = fromAndTo;
        }

        public FromAndTo getFromAndTo() {
            return this.a;
        }

        public boolean isIndoor() {
            return this.c;
        }

        public void setIndoor(boolean z) {
            this.c = z;
        }

        public int getAlternativeRoute() {
            return this.d;
        }

        public void setAlternativeRoute(int i) {
            this.d = i;
        }

        public int getShowFields() {
            return this.b;
        }

        public void setShowFields(int i) {
            this.b = i;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(this.a, i);
            parcel.writeBooleanArray(new boolean[]{this.c});
            parcel.writeInt(this.d);
            parcel.writeInt(this.b);
        }

        public WalkRouteQuery(Parcel parcel) {
            this.b = 1;
            this.c = false;
            this.d = 1;
            this.a = (FromAndTo) parcel.readParcelable(FromAndTo.class.getClassLoader());
            boolean[] zArr = new boolean[1];
            parcel.readBooleanArray(zArr);
            this.c = zArr[0];
            this.d = parcel.readInt();
            this.b = parcel.readInt();
        }

        public WalkRouteQuery() {
            this.b = 1;
            this.c = false;
            this.d = 1;
        }

        public int hashCode() {
            return (((((this.a.hashCode() * 31) + this.b) * 31) + (this.c ? 1 : 0)) * 31) + this.d;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            WalkRouteQuery walkRouteQuery = (WalkRouteQuery) obj;
            if (this.b == walkRouteQuery.b && this.c == walkRouteQuery.c && this.d == walkRouteQuery.d) {
                return this.a.equals(walkRouteQuery.a);
            }
            return false;
        }

        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public WalkRouteQuery m68clone() {
            try {
                super.clone();
            } catch (CloneNotSupportedException e) {
                fp.a(e, "RouteSearchV2", "WalkRouteQueryclone");
            }
            WalkRouteQuery walkRouteQuery = new WalkRouteQuery(this.a);
            walkRouteQuery.setShowFields(this.b);
            walkRouteQuery.setIndoor(this.c);
            walkRouteQuery.setAlternativeRoute(this.d);
            return walkRouteQuery;
        }
    }

    public static class DriveRouteQuery implements Parcelable, Cloneable {
        public static final Parcelable.Creator<DriveRouteQuery> CREATOR = new Parcelable.Creator<DriveRouteQuery>() { // from class: com.amap.api.services.route.RouteSearchV2.DriveRouteQuery.1
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
        private NewEnergy b;
        private int c;
        private List<LatLonPoint> d;
        private List<List<LatLonPoint>> e;
        private String f;
        private boolean g;
        private int h;
        private String i;
        private int j;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public DriveRouteQuery(FromAndTo fromAndTo, DrivingStrategy drivingStrategy, List<LatLonPoint> list, List<List<LatLonPoint>> list2, String str) {
            this.c = DrivingStrategy.DEFAULT.getValue();
            this.g = true;
            this.h = 0;
            this.i = null;
            this.j = 1;
            this.a = fromAndTo;
            this.c = drivingStrategy.getValue();
            this.d = list;
            this.e = list2;
            this.f = str;
        }

        public NewEnergy getNewEnergy() {
            return this.b;
        }

        public void setNewEnergy(NewEnergy newEnergy) {
            this.b = newEnergy;
        }

        public FromAndTo getFromAndTo() {
            return this.a;
        }

        public DrivingStrategy getMode() {
            return DrivingStrategy.fromValue(this.c);
        }

        public int getCarType() {
            return this.h;
        }

        public List<LatLonPoint> getPassedByPoints() {
            return this.d;
        }

        public List<List<LatLonPoint>> getAvoidpolygons() {
            return this.e;
        }

        public String getAvoidRoad() {
            return this.f;
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

        public boolean hasPassPoint() {
            return !fp.a(getPassedPointStr());
        }

        public String getAvoidpolygonsStr() {
            StringBuffer stringBuffer = new StringBuffer();
            List<List<LatLonPoint>> list = this.e;
            if (list == null || list.size() == 0) {
                return null;
            }
            for (int i = 0; i < this.e.size(); i++) {
                List<LatLonPoint> list2 = this.e.get(i);
                for (int i2 = 0; i2 < list2.size(); i2++) {
                    LatLonPoint latLonPoint = list2.get(i2);
                    stringBuffer.append(latLonPoint.getLongitude());
                    stringBuffer.append(",");
                    stringBuffer.append(latLonPoint.getLatitude());
                    if (i2 < list2.size() - 1) {
                        stringBuffer.append(";");
                    }
                }
                if (i < this.e.size() - 1) {
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
            return this.i;
        }

        public void setExclude(String str) {
            this.i = str;
        }

        public int getShowFields() {
            return this.j;
        }

        public void setShowFields(int i) {
            this.j = i;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(this.a, i);
            parcel.writeInt(this.c);
            parcel.writeTypedList(this.d);
            List<List<LatLonPoint>> list = this.e;
            if (list == null) {
                parcel.writeInt(0);
            } else {
                parcel.writeInt(list.size());
                Iterator<List<LatLonPoint>> it = this.e.iterator();
                while (it.hasNext()) {
                    parcel.writeTypedList(it.next());
                }
            }
            parcel.writeString(this.f);
            parcel.writeInt(this.g ? 1 : 0);
            parcel.writeInt(this.h);
            parcel.writeString(this.i);
            parcel.writeInt(this.j);
        }

        public DriveRouteQuery(Parcel parcel) {
            this.c = DrivingStrategy.DEFAULT.getValue();
            this.g = true;
            this.h = 0;
            this.i = null;
            this.j = 1;
            this.a = (FromAndTo) parcel.readParcelable(FromAndTo.class.getClassLoader());
            this.c = parcel.readInt();
            this.d = parcel.createTypedArrayList(LatLonPoint.CREATOR);
            int i = parcel.readInt();
            if (i == 0) {
                this.e = null;
            } else {
                this.e = new ArrayList();
            }
            for (int i2 = 0; i2 < i; i2++) {
                this.e.add(parcel.createTypedArrayList(LatLonPoint.CREATOR));
            }
            this.f = parcel.readString();
            this.g = parcel.readInt() == 1;
            this.h = parcel.readInt();
            this.i = parcel.readString();
            this.j = parcel.readInt();
        }

        public DriveRouteQuery() {
            this.c = DrivingStrategy.DEFAULT.getValue();
            this.g = true;
            this.h = 0;
            this.i = null;
            this.j = 1;
        }

        public int hashCode() {
            String str = this.f;
            int iHashCode = ((str == null ? 0 : str.hashCode()) + 31) * 31;
            List<List<LatLonPoint>> list = this.e;
            int iHashCode2 = (iHashCode + (list == null ? 0 : list.hashCode())) * 31;
            FromAndTo fromAndTo = this.a;
            int iHashCode3 = (((iHashCode2 + (fromAndTo == null ? 0 : fromAndTo.hashCode())) * 31) + this.c) * 31;
            List<LatLonPoint> list2 = this.d;
            return ((iHashCode3 + (list2 != null ? list2.hashCode() : 0)) * 31) + this.h;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            DriveRouteQuery driveRouteQuery = (DriveRouteQuery) obj;
            String str = this.f;
            if (str == null) {
                if (driveRouteQuery.f != null) {
                    return false;
                }
            } else if (!str.equals(driveRouteQuery.f)) {
                return false;
            }
            List<List<LatLonPoint>> list = this.e;
            if (list == null) {
                if (driveRouteQuery.e != null) {
                    return false;
                }
            } else if (!list.equals(driveRouteQuery.e)) {
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
            if (this.c != driveRouteQuery.c) {
                return false;
            }
            List<LatLonPoint> list2 = this.d;
            if (list2 == null) {
                if (driveRouteQuery.d != null) {
                    return false;
                }
            } else if (!list2.equals(driveRouteQuery.d) || this.g != driveRouteQuery.isUseFerry() || this.h != driveRouteQuery.h || this.j != driveRouteQuery.j) {
                return false;
            }
            return true;
        }

        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public DriveRouteQuery m65clone() {
            try {
                super.clone();
            } catch (CloneNotSupportedException e) {
                fp.a(e, "RouteSearchV2", "DriveRouteQueryclone");
            }
            DriveRouteQuery driveRouteQuery = new DriveRouteQuery(this.a, DrivingStrategy.fromValue(this.c), this.d, this.e, this.f);
            driveRouteQuery.setUseFerry(this.g);
            driveRouteQuery.setCarType(this.h);
            driveRouteQuery.setExclude(this.i);
            driveRouteQuery.setShowFields(this.j);
            driveRouteQuery.setNewEnergy(this.b);
            return driveRouteQuery;
        }

        public boolean isUseFerry() {
            return this.g;
        }

        public void setUseFerry(boolean z) {
            this.g = z;
        }

        public void setCarType(int i) {
            this.h = i;
        }
    }

    public static class RideRouteQuery implements Parcelable, Cloneable {
        public static final Parcelable.Creator<RideRouteQuery> CREATOR = new Parcelable.Creator<RideRouteQuery>() { // from class: com.amap.api.services.route.RouteSearchV2.RideRouteQuery.1
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
        private int c;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public RideRouteQuery(FromAndTo fromAndTo) {
            this.b = 1;
            this.c = 1;
            this.a = fromAndTo;
        }

        public FromAndTo getFromAndTo() {
            return this.a;
        }

        public int getShowFields() {
            return this.b;
        }

        public void setShowFields(int i) {
            this.b = i;
        }

        public int getAlternativeRoute() {
            return this.c;
        }

        public void setAlternativeRoute(int i) {
            this.c = i;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(this.a, i);
            parcel.writeInt(this.c);
            parcel.writeInt(this.b);
        }

        public RideRouteQuery(Parcel parcel) {
            this.b = 1;
            this.c = 1;
            this.a = (FromAndTo) parcel.readParcelable(RouteSearch.FromAndTo.class.getClassLoader());
            this.c = parcel.readInt();
            this.b = parcel.readInt();
        }

        public RideRouteQuery() {
            this.b = 1;
            this.c = 1;
        }

        public int hashCode() {
            FromAndTo fromAndTo = this.a;
            return (((((fromAndTo == null ? 0 : fromAndTo.hashCode()) + 31) * 31) + this.b) * 31) + this.c;
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
            return this.b == rideRouteQuery.b && this.c == rideRouteQuery.c;
        }

        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public RideRouteQuery m67clone() {
            try {
                super.clone();
            } catch (CloneNotSupportedException e) {
                fp.a(e, "RouteSearchV2", "RideRouteQueryclone");
            }
            RideRouteQuery rideRouteQuery = new RideRouteQuery(this.a);
            rideRouteQuery.setShowFields(this.b);
            rideRouteQuery.setAlternativeRoute(this.c);
            return rideRouteQuery;
        }
    }

    public static class BusRouteQuery implements Parcelable, Cloneable {
        public static final Parcelable.Creator<BusRouteQuery> CREATOR = new Parcelable.Creator<BusRouteQuery>() { // from class: com.amap.api.services.route.RouteSearchV2.BusRouteQuery.1
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
        private String e;
        private String f;
        private int g;
        private String h;
        private String i;
        private String j;
        private String k;
        private int l;
        private int m;
        private int n;
        private int o;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public BusRouteQuery(FromAndTo fromAndTo, int i, String str, int i2) {
            this.b = 0;
            this.g = 0;
            this.l = 5;
            this.m = 0;
            this.n = 4;
            this.o = 1;
            this.a = fromAndTo;
            this.b = i;
            this.c = str;
            this.g = i2;
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
            return this.g;
        }

        public String getCityd() {
            return this.d;
        }

        public void setCityd(String str) {
            this.d = str;
        }

        public int getShowFields() {
            return this.o;
        }

        public void setShowFields(int i) {
            this.o = i;
        }

        public String getDate() {
            return this.e;
        }

        public void setDate(String str) {
            this.e = str;
        }

        public String getTime() {
            return this.f;
        }

        public void setTime(String str) {
            this.f = str;
        }

        public String getOriginPoiId() {
            return this.h;
        }

        public void setOriginPoiId(String str) {
            this.h = str;
        }

        public String getDestinationPoiId() {
            return this.i;
        }

        public void setDestinationPoiId(String str) {
            this.i = str;
        }

        public String getAd1() {
            return this.j;
        }

        public void setAd1(String str) {
            this.j = str;
        }

        public String getAd2() {
            return this.k;
        }

        public void setAd2(String str) {
            this.k = str;
        }

        public int getAlternativeRoute() {
            return this.l;
        }

        public void setAlternativeRoute(int i) {
            this.l = i;
        }

        public int getMultiExport() {
            return this.m;
        }

        public void setMultiExport(int i) {
            this.m = i;
        }

        public int getMaxTrans() {
            return this.n;
        }

        public void setMaxTrans(int i) {
            this.n = i;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(this.a, i);
            parcel.writeInt(this.b);
            parcel.writeString(this.c);
            parcel.writeInt(this.g);
            parcel.writeString(this.d);
            parcel.writeInt(this.o);
            parcel.writeString(this.h);
            parcel.writeString(this.i);
            parcel.writeString(this.j);
            parcel.writeString(this.k);
            parcel.writeInt(this.l);
            parcel.writeInt(this.n);
            parcel.writeInt(this.m);
            parcel.writeString(this.e);
            parcel.writeString(this.f);
        }

        public BusRouteQuery(Parcel parcel) {
            this.b = 0;
            this.g = 0;
            this.l = 5;
            this.m = 0;
            this.n = 4;
            this.o = 1;
            this.a = (FromAndTo) parcel.readParcelable(FromAndTo.class.getClassLoader());
            this.b = parcel.readInt();
            this.c = parcel.readString();
            this.g = parcel.readInt();
            this.d = parcel.readString();
            this.o = parcel.readInt();
            this.h = parcel.readString();
            this.i = parcel.readString();
            this.e = parcel.readString();
            this.f = parcel.readString();
            this.n = parcel.readInt();
            this.m = parcel.readInt();
            this.l = parcel.readInt();
            this.j = parcel.readString();
            this.k = parcel.readString();
        }

        public BusRouteQuery() {
            this.b = 0;
            this.g = 0;
            this.l = 5;
            this.m = 0;
            this.n = 4;
            this.o = 1;
        }

        public int hashCode() {
            return (((((((((((((((((((((((((((this.a.hashCode() * 31) + this.b) * 31) + this.c.hashCode()) * 31) + this.d.hashCode()) * 31) + this.e.hashCode()) * 31) + this.f.hashCode()) * 31) + this.g) * 31) + this.h.hashCode()) * 31) + this.i.hashCode()) * 31) + this.j.hashCode()) * 31) + this.k.hashCode()) * 31) + this.l) * 31) + this.m) * 31) + this.n) * 31) + this.o;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            BusRouteQuery busRouteQuery = (BusRouteQuery) obj;
            if (this.b == busRouteQuery.b && this.g == busRouteQuery.g && this.h.equals(busRouteQuery.h) && this.i.equals(busRouteQuery.i) && this.l == busRouteQuery.l && this.m == busRouteQuery.m && this.n == busRouteQuery.n && this.o == busRouteQuery.o && this.a.equals(busRouteQuery.a) && this.c.equals(busRouteQuery.c) && this.d.equals(busRouteQuery.d) && this.e.equals(busRouteQuery.e) && this.f.equals(busRouteQuery.f) && this.j.equals(busRouteQuery.j)) {
                return this.k.equals(busRouteQuery.k);
            }
            return false;
        }

        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public BusRouteQuery m64clone() {
            try {
                super.clone();
            } catch (CloneNotSupportedException e) {
                fp.a(e, "RouteSearchV2", "BusRouteQueryclone");
            }
            BusRouteQuery busRouteQuery = new BusRouteQuery(this.a, this.b, this.c, this.g);
            busRouteQuery.setCityd(this.d);
            busRouteQuery.setShowFields(this.o);
            busRouteQuery.setDate(this.e);
            busRouteQuery.setTime(this.f);
            busRouteQuery.setAd1(this.j);
            busRouteQuery.setAd2(this.k);
            busRouteQuery.setOriginPoiId(this.h);
            busRouteQuery.setDestinationPoiId(this.i);
            busRouteQuery.setMaxTrans(this.n);
            busRouteQuery.setMultiExport(this.m);
            busRouteQuery.setAlternativeRoute(this.l);
            return busRouteQuery;
        }
    }
}
