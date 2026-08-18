package com.amap.api.col.p0003sl;

import android.content.Context;
import android.provider.CallLog;
import android.provider.Contacts;
import android.text.TextUtils;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: compiled from: GeoFenceNetManager.java */
/* JADX INFO: loaded from: classes2.dex */
public final class b {
    ku a;

    public b(Context context) {
        this.a = null;
        try {
            im.a().a(context);
        } catch (Throwable unused) {
        }
        this.a = ku.a();
    }

    public final String a(Context context, String str, String str2, String str3, String str4, String str5) {
        Map<String, String> mapB = b(context, str2, str3, str4, str5, null, null, null);
        mapB.put("children", "1");
        mapB.put("page", "1");
        mapB.put(Contacts.People.Extensions.CONTENT_DIRECTORY, "base");
        return a(context, str, mapB);
    }

    public final String a(Context context, String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        Map<String, String> mapB = b(context, str2, str3, null, str4, str5, str6, str7);
        mapB.put("children", "1");
        mapB.put("page", "1");
        mapB.put(Contacts.People.Extensions.CONTENT_DIRECTORY, "base");
        return a(context, str, mapB);
    }

    public final String a(Context context, String str, String str2) {
        Map<String, String> mapB = b(context, str2, null, null, null, null, null, null);
        mapB.put(Contacts.People.Extensions.CONTENT_DIRECTORY, "all");
        mapB.put("subdistrict", "0");
        return a(context, str, mapB);
    }

    private String a(Context context, String str, Map<String, String> map) {
        try {
            HashMap map2 = new HashMap(16);
            com.autonavi.aps.amapapi.trans.b bVar = new com.autonavi.aps.amapapi.trans.b();
            map2.clear();
            map2.put("Content-Type", "application/x-www-form-urlencoded");
            map2.put("Connection", "Keep-Alive");
            map2.put("User-Agent", "AMAP_Location_SDK_Android 6.4.3");
            String strA = ij.a();
            String strA2 = ij.a(context, strA, it.b(map));
            map.put(SPUserInfoUtils.TS, strA);
            map.put("scode", strA2);
            bVar.b(map);
            bVar.a(map2);
            bVar.a(str);
            bVar.setProxy(ir.a(context));
            bVar.setConnectionTimeout(com.autonavi.aps.amapapi.utils.b.i);
            bVar.setSoTimeout(com.autonavi.aps.amapapi.utils.b.i);
            try {
                return new String(ku.a(bVar).a, "utf-8");
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceNetManager", "post");
                return null;
            }
        } catch (Throwable unused) {
            return null;
        }
    }

    private static Map<String, String> b(Context context, String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        HashMap map = new HashMap(16);
        map.put("key", ig.f(context));
        if (!TextUtils.isEmpty(str)) {
            map.put("keywords", str);
        }
        if (!TextUtils.isEmpty(str2)) {
            map.put("types", str2);
        }
        if (!TextUtils.isEmpty(str5) && !TextUtils.isEmpty(str6)) {
            map.put("location", str6 + "," + str5);
        }
        if (!TextUtils.isEmpty(str3)) {
            map.put("city", str3);
        }
        if (!TextUtils.isEmpty(str4)) {
            map.put(CallLog.Calls.OFFSET_PARAM_KEY, str4);
        }
        if (!TextUtils.isEmpty(str7)) {
            map.put("radius", str7);
        }
        return map;
    }
}
