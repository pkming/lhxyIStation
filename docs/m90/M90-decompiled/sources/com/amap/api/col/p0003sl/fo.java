package com.amap.api.col.p0003sl;

import com.amap.api.col.p0003sl.is;
import com.amap.api.services.core.ServiceSettings;

/* JADX INFO: compiled from: ConfigableConst.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fo {
    public static final String[] a = {"com.amap.api.services", "com.amap.api.search.admic"};

    public static String a() {
        return ServiceSettings.getInstance().getProtocol() == 1 ? "http://restsdk.amap.com/v3" : "https://restsdk.amap.com/v3";
    }

    public static String b() {
        return ServiceSettings.getInstance().getProtocol() == 1 ? "http://restsdk.amap.com/v4" : "https://restsdk.amap.com/v4";
    }

    public static String c() {
        return ServiceSettings.getInstance().getProtocol() == 1 ? "http://restsdk.amap.com/v5" : "https://restsdk.amap.com/v5";
    }

    public static String d() {
        return ServiceSettings.getInstance().getProtocol() == 1 ? "http://yuntuapi.amap.com" : "https://yuntuapi.amap.com";
    }

    public static String e() {
        return ServiceSettings.getInstance().getProtocol() == 1 ? "http://restsdk.amap.com/rest/me/cpoint" : "https://restsdk.amap.com/rest/me/cpoint";
    }

    public static String f() {
        return ServiceSettings.getInstance().getProtocol() == 1 ? "http://apistore.amap.com" : "https://apistore.amap.com";
    }

    public static is a(boolean z) {
        try {
            return new is.a("sea", "9.7.1", "AMAP SDK Android Search 9.7.1").a(a).a(z).a("9.7.1").a();
        } catch (Cif e) {
            fp.a(e, "ConfigableConst", "getSDKInfo");
            return null;
        }
    }

    public static String g() {
        return ServiceSettings.getInstance().getProtocol() == 1 ? "http://m5.amap.com/ws/mapapi/shortaddress/transform" : "https://m5.amap.com/ws/mapapi/shortaddress/transform";
    }
}
