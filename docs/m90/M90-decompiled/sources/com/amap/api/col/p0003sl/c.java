package com.amap.api.col.p0003sl;

import android.app.Instrumentation;
import android.os.Bundle;
import com.amap.api.fence.DistrictItem;
import com.amap.api.fence.GeoFence;
import com.amap.api.fence.PoiItem;
import com.amap.api.location.DPoint;
import com.autonavi.aps.amapapi.utils.j;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: compiled from: GeoFenceSearchResultParser.java */
/* JADX INFO: loaded from: classes2.dex */
public final class c {
    private static long a;

    public static int a(String str, List<GeoFence> list, Bundle bundle) {
        JSONArray jSONArrayOptJSONArray;
        int i;
        try {
            JSONObject jSONObject = new JSONObject(str);
            char c = 0;
            int iOptInt = jSONObject.optInt("status", 0);
            int iOptInt2 = jSONObject.optInt("infocode", 0);
            if (iOptInt == 1 && (jSONArrayOptJSONArray = jSONObject.optJSONArray("pois")) != null) {
                int i2 = 0;
                while (i2 < jSONArrayOptJSONArray.length()) {
                    GeoFence geoFence = new GeoFence();
                    PoiItem poiItem = new PoiItem();
                    JSONObject jSONObject2 = jSONArrayOptJSONArray.getJSONObject(i2);
                    poiItem.setPoiId(jSONObject2.optString(Instrumentation.REPORT_KEY_IDENTIFIER));
                    poiItem.setPoiName(jSONObject2.optString("name"));
                    poiItem.setPoiType(jSONObject2.optString("type"));
                    poiItem.setTypeCode(jSONObject2.optString("typecode"));
                    poiItem.setAddress(jSONObject2.optString("address"));
                    String strOptString = jSONObject2.optString("location");
                    if (strOptString != null) {
                        String[] strArrSplit = strOptString.split(",");
                        poiItem.setLongitude(Double.parseDouble(strArrSplit[c]));
                        poiItem.setLatitude(Double.parseDouble(strArrSplit[1]));
                        List<List<DPoint>> arrayList = new ArrayList<>();
                        ArrayList arrayList2 = new ArrayList();
                        i = iOptInt2;
                        DPoint dPoint = new DPoint(poiItem.getLatitude(), poiItem.getLongitude());
                        arrayList2.add(dPoint);
                        arrayList.add(arrayList2);
                        geoFence.setPointList(arrayList);
                        geoFence.setCenter(dPoint);
                    } else {
                        i = iOptInt2;
                    }
                    poiItem.setTel(jSONObject2.optString("tel"));
                    poiItem.setProvince(jSONObject2.optString("pname"));
                    poiItem.setCity(jSONObject2.optString("cityname"));
                    poiItem.setAdname(jSONObject2.optString("adname"));
                    geoFence.setPoiItem(poiItem);
                    geoFence.setFenceId(new StringBuilder().append(a()).toString());
                    if (bundle != null) {
                        geoFence.setCustomId(bundle.getString(GeoFence.BUNDLE_KEY_CUSTOMID));
                        geoFence.setPendingIntentAction(bundle.getString("pendingIntentAction"));
                        geoFence.setType(2);
                        geoFence.setRadius(bundle.getFloat("fenceRadius"));
                        geoFence.setExpiration(bundle.getLong("expiration"));
                        geoFence.setActivatesAction(bundle.getInt("activatesAction", 1));
                    }
                    if (list != null) {
                        list.add(geoFence);
                    }
                    i2++;
                    iOptInt2 = i;
                    c = 0;
                }
            }
            return iOptInt2;
        } catch (Throwable unused) {
            return 5;
        }
    }

    public static int b(String str, List<GeoFence> list, Bundle bundle) {
        return a(str, list, bundle);
    }

    public final int c(String str, List<GeoFence> list, Bundle bundle) {
        JSONArray jSONArrayOptJSONArray;
        ArrayList arrayList;
        String str2;
        int i;
        String str3;
        String str4;
        float f;
        long j;
        boolean z;
        long j2;
        int i2;
        String str5;
        try {
            JSONObject jSONObject = new JSONObject(str);
            int iOptInt = jSONObject.optInt("status", 0);
            int iOptInt2 = jSONObject.optInt("infocode", 0);
            String string = bundle.getString(GeoFence.BUNDLE_KEY_CUSTOMID);
            String string2 = bundle.getString("pendingIntentAction");
            float f2 = bundle.getFloat("fenceRadius");
            long j3 = bundle.getLong("expiration");
            int i3 = bundle.getInt("activatesAction", 1);
            if (iOptInt == 1 && (jSONArrayOptJSONArray = jSONObject.optJSONArray("districts")) != null) {
                int i4 = 0;
                while (i4 < jSONArrayOptJSONArray.length()) {
                    ArrayList arrayList2 = new ArrayList();
                    ArrayList arrayList3 = new ArrayList();
                    GeoFence geoFence = new GeoFence();
                    JSONObject jSONObject2 = jSONArrayOptJSONArray.getJSONObject(i4);
                    String strOptString = jSONObject2.optString("citycode");
                    String strOptString2 = jSONObject2.optString("adcode");
                    String strOptString3 = jSONObject2.optString("name");
                    JSONArray jSONArray = jSONArrayOptJSONArray;
                    String string3 = jSONObject2.getString("center");
                    int i5 = iOptInt2;
                    DPoint dPoint = new DPoint();
                    int i6 = i4;
                    String str6 = ",";
                    if (string3 != null) {
                        String[] strArrSplit = string3.split(",");
                        arrayList = arrayList2;
                        str2 = strOptString3;
                        dPoint.setLatitude(Double.parseDouble(strArrSplit[1]));
                        dPoint.setLongitude(Double.parseDouble(strArrSplit[0]));
                        geoFence.setCenter(dPoint);
                    } else {
                        arrayList = arrayList2;
                        str2 = strOptString3;
                    }
                    geoFence.setCustomId(string);
                    geoFence.setPendingIntentAction(string2);
                    geoFence.setType(3);
                    geoFence.setRadius(f2);
                    geoFence.setExpiration(j3);
                    geoFence.setActivatesAction(i3);
                    geoFence.setFenceId(new StringBuilder().append(a()).toString());
                    String strOptString4 = jSONObject2.optString("polyline");
                    if (strOptString4 != null) {
                        String[] strArrSplit2 = strOptString4.split("\\|");
                        int length = strArrSplit2.length;
                        i = i3;
                        float fMin = Float.MAX_VALUE;
                        float fMax = Float.MIN_VALUE;
                        int i7 = 0;
                        while (i7 < length) {
                            String str7 = string;
                            String str8 = strArrSplit2[i7];
                            String[] strArr = strArrSplit2;
                            DistrictItem districtItem = new DistrictItem();
                            String str9 = string2;
                            List<DPoint> arrayList4 = new ArrayList<>();
                            districtItem.setCitycode(strOptString);
                            districtItem.setAdcode(strOptString2);
                            String str10 = strOptString2;
                            String str11 = str2;
                            districtItem.setDistrictName(str11);
                            str2 = str11;
                            String[] strArrSplit3 = str8.split(";");
                            float f3 = f2;
                            int i8 = 0;
                            while (i8 < strArrSplit3.length) {
                                String[] strArrSplit4 = strArrSplit3[i8].split(str6);
                                String str12 = str6;
                                String[] strArr2 = strArrSplit3;
                                if (strArrSplit4.length > 1) {
                                    String str13 = strArrSplit4[1];
                                    String str14 = strArrSplit4[0];
                                    j2 = j3;
                                    double d = Double.parseDouble(str13);
                                    i2 = length;
                                    str5 = strOptString;
                                    arrayList4.add(new DPoint(d, Double.parseDouble(str14)));
                                } else {
                                    j2 = j3;
                                    i2 = length;
                                    str5 = strOptString;
                                }
                                i8++;
                                strOptString = str5;
                                str6 = str12;
                                strArrSplit3 = strArr2;
                                j3 = j2;
                                length = i2;
                            }
                            String str15 = str6;
                            long j4 = j3;
                            int i9 = length;
                            String str16 = strOptString;
                            if (arrayList4.size() > 100.0f) {
                                try {
                                    arrayList4 = a(arrayList4, 100.0f);
                                } catch (Throwable unused) {
                                    return 5;
                                }
                            }
                            arrayList3.add(arrayList4);
                            districtItem.setPolyline(arrayList4);
                            ArrayList arrayList5 = arrayList;
                            arrayList5.add(districtItem);
                            fMax = Math.max(fMax, a.b(dPoint, arrayList4));
                            fMin = Math.min(fMin, a.a(dPoint, arrayList4));
                            i7++;
                            strOptString = str16;
                            arrayList = arrayList5;
                            string = str7;
                            strArrSplit2 = strArr;
                            string2 = str9;
                            strOptString2 = str10;
                            f2 = f3;
                            str6 = str15;
                            j3 = j4;
                            length = i9;
                        }
                        z = false;
                        str3 = string;
                        str4 = string2;
                        f = f2;
                        j = j3;
                        geoFence.setMaxDis2Center(fMax);
                        geoFence.setMinDis2Center(fMin);
                        geoFence.setDistrictItemList(arrayList);
                        geoFence.setPointList(arrayList3);
                        list.add(geoFence);
                    } else {
                        i = i3;
                        str3 = string;
                        str4 = string2;
                        f = f2;
                        j = j3;
                        z = false;
                    }
                    i4 = i6 + 1;
                    jSONArrayOptJSONArray = jSONArray;
                    iOptInt2 = i5;
                    i3 = i;
                    string = str3;
                    string2 = str4;
                    f2 = f;
                    j3 = j;
                }
            }
            return iOptInt2;
        } catch (Throwable unused2) {
        }
    }

    public static synchronized long a() {
        long jB = j.b();
        long j = a;
        if (jB > j) {
            a = jB;
        } else {
            a = j + 1;
        }
        return a;
    }

    private List<DPoint> a(List<DPoint> list, float f) {
        if (list == null) {
            return null;
        }
        if (list.size() <= 2) {
            return list;
        }
        double d = 0.0d;
        ArrayList arrayList = new ArrayList();
        DPoint dPoint = list.get(0);
        DPoint dPoint2 = list.get(list.size() - 1);
        int i = 0;
        for (int i2 = 1; i2 < list.size() - 1; i2++) {
            double dA = a(list.get(i2), dPoint, dPoint2);
            if (dA > d) {
                i = i2;
                d = dA;
            }
        }
        if (d < f) {
            arrayList.add(dPoint);
            arrayList.add(dPoint2);
            return arrayList;
        }
        List<DPoint> listA = a(list.subList(0, i + 1), f);
        List<DPoint> listA2 = a(list.subList(i, list.size()), f);
        arrayList.addAll(listA);
        arrayList.remove(arrayList.size() - 1);
        arrayList.addAll(listA2);
        return arrayList;
    }

    private static double a(DPoint dPoint, DPoint dPoint2, DPoint dPoint3) {
        double longitude;
        double latitude;
        double longitude2 = dPoint.getLongitude() - dPoint2.getLongitude();
        double latitude2 = dPoint.getLatitude() - dPoint2.getLatitude();
        double longitude3 = dPoint3.getLongitude() - dPoint2.getLongitude();
        double latitude3 = dPoint3.getLatitude() - dPoint2.getLatitude();
        double d = ((longitude2 * longitude3) + (latitude2 * latitude3)) / ((longitude3 * longitude3) + (latitude3 * latitude3));
        boolean z = dPoint2.getLongitude() == dPoint3.getLongitude() && dPoint2.getLatitude() == dPoint3.getLatitude();
        if (d < 0.0d || z) {
            longitude = dPoint2.getLongitude();
            latitude = dPoint2.getLatitude();
        } else if (d > 1.0d) {
            longitude = dPoint3.getLongitude();
            latitude = dPoint3.getLatitude();
        } else {
            double longitude4 = dPoint2.getLongitude() + (longitude3 * d);
            latitude = dPoint2.getLatitude() + (d * latitude3);
            longitude = longitude4;
        }
        return j.a(new DPoint(dPoint.getLatitude(), dPoint.getLongitude()), new DPoint(latitude, longitude));
    }
}
