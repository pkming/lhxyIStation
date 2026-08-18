package com.amap.api.col.p0003sl;

import android.hardware.Camera;
import android.nfc.cardemulation.CardEmulation;
import android.os.BatteryManager;
import android.text.TextUtils;
import com.amap.api.maps.AMap;
import com.amap.api.services.district.DistrictSearchQuery;

/* JADX INFO: compiled from: StyleItemAdaptor.java */
/* JADX INFO: loaded from: classes2.dex */
public final class cy {
    public static final int[][] a = {new int[]{0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, new int[]{12}, new int[]{1}, new int[]{13}, new int[]{14}, new int[]{15, 16}, new int[]{17, 18, 19, 20, 21, 26, 27, 28}, new int[]{22, 23}, new int[]{24, 25}, new int[]{39, 40, 41}, new int[]{29, 30, 31}, new int[]{32, 33, 34, 35, 36, 37, 38}};
    public static final String[] b = {"land", "water", "green", "building", "highway", "arterial", AMap.LOCAL, "railway", "subway", "boundary", "poilabel", "districtlable"};
    public static final String[][] c = {new String[]{"land", "edu", "public", "traffic", "scenicSpot", "culture", BatteryManager.EXTRA_HEALTH, Camera.Parameters.SCENE_MODE_SPORTS, "business", "parkingLot", "subway"}, new String[]{"water"}, new String[]{"green"}, new String[]{"buildings"}, new String[]{"highWay"}, new String[]{"ringRoad", "nationalRoad"}, new String[]{"provincialRoad", "secondaryRoad", "levelThreeRoad", "levelFourRoad", "roadsBeingBuilt", "overPass", "underPass", CardEmulation.CATEGORY_OTHER}, new String[]{"railway", "highSpeedRailway"}, new String[]{"subwayline", "subwayBeingBuilt"}, new String[]{"China", "foreign", "provincial"}, new String[]{"guideBoards", "pois", "aois"}, new String[]{"continent", "country", DistrictSearchQuery.KEYWORDS_PROVINCE, "city", DistrictSearchQuery.KEYWORDS_DISTRICT, "town", "village"}};
    public static final String[] d = {"regions", "water", "regions", "buildings", "roads", "roads", "roads", "roads", "roads", "borders", "labels", "labels"};

    public static String[] a(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        int i = -1;
        int i2 = 0;
        while (true) {
            String[] strArr = b;
            if (i2 >= strArr.length) {
                break;
            }
            if (strArr[i2].equals(str)) {
                i = i2;
                break;
            }
            i2++;
        }
        if (i >= 0) {
            return c[i];
        }
        return null;
    }

    public static String b(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        int i = -1;
        int i2 = 0;
        while (true) {
            String[] strArr = b;
            if (i2 >= strArr.length) {
                break;
            }
            if (strArr[i2].equals(str)) {
                i = i2;
                break;
            }
            i2++;
        }
        if (i >= 0) {
            return d[i];
        }
        return null;
    }
}
