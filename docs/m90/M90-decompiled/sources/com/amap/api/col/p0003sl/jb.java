package com.amap.api.col.p0003sl;

import android.content.Context;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: compiled from: BaseAAIDRequest.java */
/* JADX INFO: loaded from: classes2.dex */
public class jb extends in {
    public Context k;

    @Override // com.amap.api.col.p0003sl.lb
    public String getSDKName() {
        return "core";
    }

    public jb(Context context) {
        this.k = context;
        setConnectionTimeout(5000);
        setSoTimeout(5000);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public Map<String, String> getRequestHead() {
        HashMap map = new HashMap();
        map.put("Content-Type", "application/json");
        map.put("Accept-Encoding", "gzip");
        map.put("User-Agent", "AMAP SDK Android core 4.3.11");
        map.put("platinfo", String.format("platform=Android&sdkversion=%s&product=%s", "4.3.11", "core"));
        map.put("logversion", "2.1");
        return map;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public Map<String, String> getParams() {
        HashMap map = new HashMap();
        map.put("key", ig.f(this.k));
        String strA = ij.a();
        String strA2 = ij.a(this.k, strA, it.b(map));
        map.put(SPUserInfoUtils.TS, strA);
        map.put("scode", strA2);
        return map;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public String getURL() {
        return im.a().b() ? "https://restapi.amap.com/rest/aaid/get" : "http://restapi.amap.com/rest/aaid/get";
    }
}
