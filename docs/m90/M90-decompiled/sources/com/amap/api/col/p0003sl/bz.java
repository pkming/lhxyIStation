package com.amap.api.col.p0003sl;

import java.util.Hashtable;
import java.util.Map;

/* JADX INFO: compiled from: OfflineDownloadRequest.java */
/* JADX INFO: loaded from: classes2.dex */
public final class bz extends db {
    private String a;

    @Override // com.amap.api.col.p0003sl.db, com.amap.api.col.p0003sl.lb
    public final Map<String, String> getParams() {
        return null;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final boolean isSupportIPV6() {
        return false;
    }

    public bz(String str) {
        this.a = str;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final Map<String, String> getRequestHead() {
        Hashtable hashtable = new Hashtable(32);
        hashtable.put("User-Agent", "MAC=channel:amapapi");
        return hashtable;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return this.a;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getIPV6URL() {
        return getURL();
    }
}
