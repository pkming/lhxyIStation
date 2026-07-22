package com.amap.api.location;

import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.district.DistrictSearchQuery;
import com.autonavi.aps.amapapi.utils.b;
import com.autonavi.aps.amapapi.utils.j;
import com.unisound.common.r;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class AMapLocation extends Location implements Parcelable, Cloneable {
    public static final String COORD_TYPE_GCJ02 = "GCJ02";
    public static final String COORD_TYPE_WGS84 = "WGS84";
    public static final Parcelable.Creator<AMapLocation> CREATOR = new Parcelable.Creator<AMapLocation>() { // from class: com.amap.api.location.AMapLocation.1
        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ AMapLocation createFromParcel(Parcel parcel) {
            return a(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ AMapLocation[] newArray(int i) {
            return a(i);
        }

        private static AMapLocation a(Parcel parcel) {
            AMapLocation aMapLocation = new AMapLocation(Location.CREATOR.createFromParcel(parcel));
            aMapLocation.h = parcel.readString();
            aMapLocation.i = parcel.readString();
            aMapLocation.B = parcel.readString();
            aMapLocation.a = parcel.readString();
            aMapLocation.e = parcel.readString();
            aMapLocation.g = parcel.readString();
            aMapLocation.k = parcel.readString();
            aMapLocation.f = parcel.readString();
            aMapLocation.p = parcel.readInt();
            aMapLocation.q = parcel.readString();
            aMapLocation.b = parcel.readString();
            aMapLocation.F = parcel.readInt() != 0;
            aMapLocation.o = parcel.readInt() != 0;
            aMapLocation.t = parcel.readDouble();
            aMapLocation.r = parcel.readString();
            aMapLocation.s = parcel.readInt();
            aMapLocation.u = parcel.readDouble();
            aMapLocation.D = parcel.readInt() != 0;
            aMapLocation.n = parcel.readString();
            aMapLocation.j = parcel.readString();
            aMapLocation.d = parcel.readString();
            aMapLocation.l = parcel.readString();
            aMapLocation.A = parcel.readInt();
            aMapLocation.C = parcel.readInt();
            aMapLocation.m = parcel.readString();
            aMapLocation.E = parcel.readString();
            aMapLocation.G = parcel.readString();
            aMapLocation.H = parcel.readInt();
            aMapLocation.I = parcel.readInt();
            return aMapLocation;
        }

        private static AMapLocation[] a(int i) {
            return new AMapLocation[i];
        }
    };
    public static final int ERROR_CODE_AIRPLANEMODE_WIFIOFF = 18;
    public static final int ERROR_CODE_FAILURE_AUTH = 7;
    public static final int ERROR_CODE_FAILURE_CELL = 11;
    public static final int ERROR_CODE_FAILURE_COARSE_LOCATION = 20;
    public static final int ERROR_CODE_FAILURE_CONNECTION = 4;
    public static final int ERROR_CODE_FAILURE_INIT = 9;
    public static final int ERROR_CODE_FAILURE_LOCATION = 6;
    public static final int ERROR_CODE_FAILURE_LOCATION_PARAMETER = 3;
    public static final int ERROR_CODE_FAILURE_LOCATION_PERMISSION = 12;
    public static final int ERROR_CODE_FAILURE_NOENOUGHSATELLITES = 14;
    public static final int ERROR_CODE_FAILURE_NOWIFIANDAP = 13;
    public static final int ERROR_CODE_FAILURE_PARSER = 5;
    public static final int ERROR_CODE_FAILURE_SIMULATION_LOCATION = 15;
    public static final int ERROR_CODE_FAILURE_WIFI_INFO = 2;
    public static final int ERROR_CODE_INVALID_PARAMETER = 1;
    public static final int ERROR_CODE_NOCGI_WIFIOFF = 19;
    public static final int ERROR_CODE_NO_COMPENSATION_CACHE = 33;
    public static final int ERROR_CODE_SERVICE_FAIL = 10;
    public static final int ERROR_CODE_UNKNOWN = 8;
    public static final int GPS_ACCURACY_BAD = 0;
    public static final int GPS_ACCURACY_GOOD = 1;
    public static final int GPS_ACCURACY_UNKNOWN = -1;
    public static final int LOCATION_COMPENSATION = 10;
    public static final int LOCATION_SUCCESS = 0;
    public static final int LOCATION_TYPE_AMAP = 7;
    public static final int LOCATION_TYPE_CELL = 6;
    public static final int LOCATION_TYPE_COARSE_LOCATION = 11;
    public static final int LOCATION_TYPE_FAST = 3;
    public static final int LOCATION_TYPE_FIX_CACHE = 4;
    public static final int LOCATION_TYPE_GPS = 1;
    public static final int LOCATION_TYPE_LAST_LOCATION_CACHE = 9;
    public static final int LOCATION_TYPE_OFFLINE = 8;
    public static final int LOCATION_TYPE_SAME_REQ = 2;
    public static final int LOCATION_TYPE_WIFI = 5;
    public static final int TRUSTED_LEVEL_BAD = 4;
    public static final int TRUSTED_LEVEL_HIGH = 1;
    public static final int TRUSTED_LEVEL_LOW = 3;
    public static final int TRUSTED_LEVEL_NORMAL = 2;
    private int A;
    private String B;
    private int C;
    private boolean D;
    private String E;
    private boolean F;
    private String G;
    private int H;
    private int I;
    protected String a;
    protected String b;
    AMapLocationQualityReport c;
    private String d;
    private String e;
    private String f;
    private String g;
    private String h;
    private String i;
    private String j;
    private String k;
    private String l;
    private String m;
    private String n;
    private boolean o;
    private int p;
    private String q;
    private String r;
    private int s;
    private double t;
    private double u;
    private double v;
    private float w;
    private float x;
    private Bundle y;
    private String z;

    @Override // android.location.Location, android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getGpsAccuracyStatus() {
        return this.C;
    }

    public void setGpsAccuracyStatus(int i) {
        this.C = i;
    }

    public AMapLocation(String str) {
        super(str);
        this.d = "";
        this.e = "";
        this.f = "";
        this.g = "";
        this.h = "";
        this.i = "";
        this.j = "";
        this.k = "";
        this.l = "";
        this.m = "";
        this.n = "";
        this.o = true;
        this.p = 0;
        this.q = r.C;
        this.r = "";
        this.s = 0;
        this.t = 0.0d;
        this.u = 0.0d;
        this.v = 0.0d;
        this.w = 0.0f;
        this.x = 0.0f;
        this.y = null;
        this.A = 0;
        this.B = "";
        this.C = -1;
        this.D = false;
        this.E = "";
        this.F = false;
        this.a = "";
        this.b = "";
        this.c = new AMapLocationQualityReport();
        this.G = COORD_TYPE_GCJ02;
        this.H = 1;
        this.z = str;
    }

    public AMapLocation(Location location) {
        super(location);
        this.d = "";
        this.e = "";
        this.f = "";
        this.g = "";
        this.h = "";
        this.i = "";
        this.j = "";
        this.k = "";
        this.l = "";
        this.m = "";
        this.n = "";
        this.o = true;
        this.p = 0;
        this.q = r.C;
        this.r = "";
        this.s = 0;
        this.t = 0.0d;
        this.u = 0.0d;
        this.v = 0.0d;
        this.w = 0.0f;
        this.x = 0.0f;
        this.y = null;
        this.A = 0;
        this.B = "";
        this.C = -1;
        this.D = false;
        this.E = "";
        this.F = false;
        this.a = "";
        this.b = "";
        this.c = new AMapLocationQualityReport();
        this.G = COORD_TYPE_GCJ02;
        this.H = 1;
        this.t = location.getLatitude();
        this.u = location.getLongitude();
        this.v = location.getAltitude();
        this.x = location.getBearing();
        this.w = location.getSpeed();
        this.z = location.getProvider();
        this.y = location.getExtras() != null ? new Bundle(location.getExtras()) : null;
    }

    public int getLocationType() {
        return this.s;
    }

    public void setLocationType(int i) {
        this.s = i;
    }

    public String getLocationDetail() {
        return this.r;
    }

    public void setLocationDetail(String str) {
        this.r = str;
    }

    public int getErrorCode() {
        return this.p;
    }

    public void setErrorCode(int i) {
        if (this.p != 0) {
            return;
        }
        this.q = j.a(i);
        this.p = i;
    }

    public String getErrorInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.q);
        if (this.p != 0) {
            sb.append(" 请到http://lbs.amap.com/api/android-location-sdk/guide/utilities/errorcode/查看错误码说明");
            sb.append(",错误详细信息:" + this.r);
        }
        return sb.toString();
    }

    public void setErrorInfo(String str) {
        this.q = str;
    }

    public String getCountry() {
        return this.k;
    }

    public void setCountry(String str) {
        this.k = str;
    }

    public String getRoad() {
        return this.l;
    }

    public void setRoad(String str) {
        this.l = str;
    }

    public String getAddress() {
        return this.i;
    }

    public void setAddress(String str) {
        this.i = str;
    }

    public String getProvince() {
        return this.d;
    }

    public void setProvince(String str) {
        this.d = str;
    }

    public String getCity() {
        return this.e;
    }

    public void setCity(String str) {
        this.e = str;
    }

    public String getDistrict() {
        return this.f;
    }

    public void setDistrict(String str) {
        this.f = str;
    }

    public String getCityCode() {
        return this.g;
    }

    public void setCityCode(String str) {
        this.g = str;
    }

    public String getAdCode() {
        return this.h;
    }

    public void setAdCode(String str) {
        this.h = str;
    }

    public String getPoiName() {
        return this.j;
    }

    public void setPoiName(String str) {
        this.j = str;
    }

    @Override // android.location.Location
    public double getLatitude() {
        return this.t;
    }

    @Override // android.location.Location
    public void setLatitude(double d) {
        this.t = d;
    }

    @Override // android.location.Location
    public double getLongitude() {
        return this.u;
    }

    @Override // android.location.Location
    public void setLongitude(double d) {
        this.u = d;
    }

    public int getSatellites() {
        return this.A;
    }

    public void setSatellites(int i) {
        this.A = i;
    }

    public String getStreet() {
        return this.m;
    }

    public void setStreet(String str) {
        this.m = str;
    }

    public String getStreetNum() {
        return this.n;
    }

    public void setNumber(String str) {
        this.n = str;
    }

    public void setOffset(boolean z) {
        this.o = z;
    }

    public boolean isOffset() {
        return this.o;
    }

    public String getAoiName() {
        return this.B;
    }

    public void setAoiName(String str) {
        this.B = str;
    }

    public String getBuildingId() {
        return this.a;
    }

    public void setBuildingId(String str) {
        this.a = str;
    }

    public String getFloor() {
        return this.b;
    }

    public boolean isFixLastLocation() {
        return this.F;
    }

    public void setFixLastLocation(boolean z) {
        this.F = z;
    }

    public void setFloor(String str) {
        if (!TextUtils.isEmpty(str)) {
            str = str.replace("F", "");
            try {
                Integer.parseInt(str);
            } catch (Throwable th) {
                b.a(th, "AmapLoc", "setFloor");
                str = null;
            }
        }
        this.b = str;
    }

    public boolean isMock() {
        return this.D;
    }

    public void setMock(boolean z) {
        this.D = z;
    }

    public String getDescription() {
        return this.E;
    }

    public void setDescription(String str) {
        this.E = str;
    }

    @Override // android.location.Location
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            stringBuffer.append("latitude=" + this.t + "#");
            stringBuffer.append("longitude=" + this.u + "#");
            stringBuffer.append("province=" + this.d + "#");
            stringBuffer.append("coordType=" + this.G + "#");
            stringBuffer.append("city=" + this.e + "#");
            stringBuffer.append("district=" + this.f + "#");
            stringBuffer.append("cityCode=" + this.g + "#");
            stringBuffer.append("adCode=" + this.h + "#");
            stringBuffer.append("address=" + this.i + "#");
            stringBuffer.append("country=" + this.k + "#");
            stringBuffer.append("road=" + this.l + "#");
            stringBuffer.append("poiName=" + this.j + "#");
            stringBuffer.append("street=" + this.m + "#");
            stringBuffer.append("streetNum=" + this.n + "#");
            stringBuffer.append("aoiName=" + this.B + "#");
            stringBuffer.append("poiid=" + this.a + "#");
            stringBuffer.append("floor=" + this.b + "#");
            stringBuffer.append("errorCode=" + this.p + "#");
            stringBuffer.append("errorInfo=" + this.q + "#");
            stringBuffer.append("locationDetail=" + this.r + "#");
            stringBuffer.append("description=" + this.E + "#");
            stringBuffer.append("locationType=" + this.s + "#");
            stringBuffer.append("conScenario=" + this.I);
        } catch (Throwable unused) {
        }
        return stringBuffer.toString();
    }

    public String toStr() {
        return toStr(1);
    }

    public String toStr(int i) {
        JSONObject json;
        try {
            json = toJson(i);
        } catch (Throwable th) {
            b.a(th, "AMapLocation", "toStr part2");
            json = null;
        }
        if (json == null) {
            return null;
        }
        return json.toString();
    }

    @Override // android.location.Location
    public float getAccuracy() {
        return super.getAccuracy();
    }

    @Override // android.location.Location
    public void setBearing(float f) {
        super.setBearing(f);
        while (f < 0.0f) {
            f += 360.0f;
        }
        while (f >= 360.0f) {
            f -= 360.0f;
        }
        this.x = f;
    }

    @Override // android.location.Location
    public float getBearing() {
        return this.x;
    }

    @Override // android.location.Location
    public void setAltitude(double d) {
        super.setAltitude(d);
        this.v = d;
    }

    @Override // android.location.Location
    public double getAltitude() {
        return this.v;
    }

    @Override // android.location.Location
    public void setSpeed(float f) {
        super.setSpeed(f);
        this.w = f;
    }

    @Override // android.location.Location
    public float getSpeed() {
        return this.w;
    }

    @Override // android.location.Location
    public void setProvider(String str) {
        super.setProvider(str);
        this.z = str;
    }

    @Override // android.location.Location
    public String getProvider() {
        return this.z;
    }

    @Override // android.location.Location
    public void setExtras(Bundle bundle) {
        super.setExtras(bundle);
        this.y = bundle == null ? null : new Bundle(bundle);
    }

    @Override // android.location.Location
    public Bundle getExtras() {
        return this.y;
    }

    public JSONObject toJson(int i) {
        try {
            JSONObject jSONObject = new JSONObject();
            if (i == 1) {
                try {
                    jSONObject.put("altitude", getAltitude());
                    jSONObject.put("speed", getSpeed());
                    jSONObject.put("bearing", getBearing());
                } catch (Throwable unused) {
                }
                jSONObject.put("citycode", this.g);
                jSONObject.put("adcode", this.h);
                jSONObject.put("country", this.k);
                jSONObject.put(DistrictSearchQuery.KEYWORDS_PROVINCE, this.d);
                jSONObject.put("city", this.e);
                jSONObject.put(DistrictSearchQuery.KEYWORDS_DISTRICT, this.f);
                jSONObject.put("road", this.l);
                jSONObject.put("street", this.m);
                jSONObject.put("number", this.n);
                jSONObject.put("poiname", this.j);
                jSONObject.put("errorCode", this.p);
                jSONObject.put(MyLocationStyle.ERROR_INFO, this.q);
                jSONObject.put(MyLocationStyle.LOCATION_TYPE, this.s);
                jSONObject.put("locationDetail", this.r);
                jSONObject.put("aoiname", this.B);
                jSONObject.put("address", this.i);
                jSONObject.put("poiid", this.a);
                jSONObject.put("floor", this.b);
                jSONObject.put("description", this.E);
            } else {
                if (i != 2) {
                    if (i != 3) {
                        return jSONObject;
                    }
                }
                jSONObject.put("provider", getProvider());
                jSONObject.put("lon", getLongitude());
                jSONObject.put("lat", getLatitude());
                jSONObject.put("accuracy", getAccuracy());
                jSONObject.put("isOffset", this.o);
                jSONObject.put("isFixLastLocation", this.F);
                jSONObject.put("coordType", this.G);
                return jSONObject;
            }
            jSONObject.put("time", getTime());
            jSONObject.put("provider", getProvider());
            jSONObject.put("lon", getLongitude());
            jSONObject.put("lat", getLatitude());
            jSONObject.put("accuracy", getAccuracy());
            jSONObject.put("isOffset", this.o);
            jSONObject.put("isFixLastLocation", this.F);
            jSONObject.put("coordType", this.G);
            return jSONObject;
        } catch (Throwable th) {
            b.a(th, "AmapLoc", "toStr");
            return null;
        }
    }

    @Override // android.location.Location, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        try {
            super.writeToParcel(parcel, i);
            parcel.writeString(this.h);
            parcel.writeString(this.i);
            parcel.writeString(this.B);
            parcel.writeString(this.a);
            parcel.writeString(this.e);
            parcel.writeString(this.g);
            parcel.writeString(this.k);
            parcel.writeString(this.f);
            parcel.writeInt(this.p);
            parcel.writeString(this.q);
            parcel.writeString(this.b);
            int i2 = 1;
            parcel.writeInt(this.F ? 1 : 0);
            parcel.writeInt(this.o ? 1 : 0);
            parcel.writeDouble(this.t);
            parcel.writeString(this.r);
            parcel.writeInt(this.s);
            parcel.writeDouble(this.u);
            if (!this.D) {
                i2 = 0;
            }
            parcel.writeInt(i2);
            parcel.writeString(this.n);
            parcel.writeString(this.j);
            parcel.writeString(this.d);
            parcel.writeString(this.l);
            parcel.writeInt(this.A);
            parcel.writeInt(this.C);
            parcel.writeString(this.m);
            parcel.writeString(this.E);
            parcel.writeString(this.G);
            parcel.writeInt(this.H);
            parcel.writeInt(this.I);
        } catch (Throwable th) {
            b.a(th, "AMapLocation", "writeToParcel");
        }
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public AMapLocation m31clone() {
        try {
            super.clone();
        } catch (Throwable unused) {
        }
        AMapLocation aMapLocation = new AMapLocation(this);
        try {
            aMapLocation.setLatitude(this.t);
            aMapLocation.setLongitude(this.u);
            aMapLocation.setAdCode(this.h);
            aMapLocation.setAddress(this.i);
            aMapLocation.setAoiName(this.B);
            aMapLocation.setBuildingId(this.a);
            aMapLocation.setCity(this.e);
            aMapLocation.setCityCode(this.g);
            aMapLocation.setCountry(this.k);
            aMapLocation.setDistrict(this.f);
            aMapLocation.setErrorCode(this.p);
            aMapLocation.setErrorInfo(this.q);
            aMapLocation.setFloor(this.b);
            aMapLocation.setFixLastLocation(this.F);
            aMapLocation.setOffset(this.o);
            aMapLocation.setLocationDetail(this.r);
            aMapLocation.setLocationType(this.s);
            aMapLocation.setMock(this.D);
            aMapLocation.setNumber(this.n);
            aMapLocation.setPoiName(this.j);
            aMapLocation.setProvince(this.d);
            aMapLocation.setRoad(this.l);
            aMapLocation.setSatellites(this.A);
            aMapLocation.setGpsAccuracyStatus(this.C);
            aMapLocation.setStreet(this.m);
            aMapLocation.setDescription(this.E);
            aMapLocation.setExtras(getExtras());
            AMapLocationQualityReport aMapLocationQualityReport = this.c;
            if (aMapLocationQualityReport != null) {
                aMapLocation.setLocationQualityReport(aMapLocationQualityReport.m33clone());
            }
            aMapLocation.setCoordType(this.G);
            aMapLocation.setTrustedLevel(this.H);
            aMapLocation.setConScenario(this.I);
        } catch (Throwable th) {
            b.a(th, "AMapLocation", "clone");
        }
        return aMapLocation;
    }

    public AMapLocationQualityReport getLocationQualityReport() {
        return this.c;
    }

    public void setLocationQualityReport(AMapLocationQualityReport aMapLocationQualityReport) {
        if (aMapLocationQualityReport == null) {
            return;
        }
        this.c = aMapLocationQualityReport;
    }

    public String getCoordType() {
        return this.G;
    }

    public void setCoordType(String str) {
        this.G = str;
    }

    public void setTrustedLevel(int i) {
        this.H = i;
    }

    public int getTrustedLevel() {
        return this.H;
    }

    public int getConScenario() {
        return this.I;
    }

    public void setConScenario(int i) {
        this.I = i;
    }
}
