package com.amap.api.col.p0003sl;

import android.content.Context;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/* JADX INFO: compiled from: AbstractBasicLbsRestHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class hz<T, V> extends hy<T, V> {
    @Override // com.amap.api.col.p0003sl.hy
    protected abstract V a(String str) throws hx;

    @Override // com.amap.api.col.p0003sl.hy
    protected abstract String c();

    @Override // com.amap.api.col.p0003sl.db, com.amap.api.col.p0003sl.lb
    public Map<String, String> getParams() {
        return null;
    }

    public hz(Context context, T t) {
        super(context, t);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public byte[] getEntityBytes() {
        try {
            return c().getBytes("utf-8");
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    @Override // com.amap.api.col.p0003sl.hy, com.amap.api.col.p0003sl.lb
    public Map<String, String> getRequestHead() {
        HashMap map = new HashMap(16);
        map.put("Content-Type", " application/json");
        map.put("Accept-Encoding", "gzip");
        map.put("User-Agent", "AMAP SDK Android Trace 10.0.600");
        map.put("x-INFO", ij.b(this.c));
        map.put("platinfo", String.format(Locale.US, "platform=Android&sdkversion=%s&product=%s", "10.0.600", "trace"));
        map.put("logversion", "2.1");
        return map;
    }
}
