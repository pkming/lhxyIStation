package com.unisound.sdk;

import java.util.HashMap;

/* JADX INFO: loaded from: classes2.dex */
public class bs {
    public static String a(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18) {
        HashMap map = new HashMap();
        map.put(bo.c, str3);
        map.put("method", str2);
        map.put("ver", str8);
        map.put("udid", str5);
        map.put("gps", str7);
        map.put("appver", str6);
        map.put("text", str9);
        map.put("history", str10);
        map.put("city", str11);
        map.put("time", str12);
        map.put(bo.m, str13);
        map.put("scenario", str14);
        map.put("screen", str15);
        map.put("dpi", str16);
        map.put(bo.q, str17);
        map.put(bo.r, str18);
        map.put(bo.j, com.unisound.common.ab.a(map, str4));
        return com.unisound.common.ab.a(str, map);
    }
}
