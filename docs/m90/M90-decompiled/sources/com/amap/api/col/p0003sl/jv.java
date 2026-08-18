package com.amap.api.col.p0003sl;

import cn.yunzhisheng.asr.JniUscClient;
import com.amap.api.col.p0003sl.lb;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: compiled from: LogUpdateRequest.java */
/* JADX INFO: loaded from: classes2.dex */
public final class jv extends in {
    private byte[] a;
    private String b;

    @Override // com.amap.api.col.p0003sl.lb
    public final Map<String, String> getParams() {
        return null;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final boolean isHostToIP() {
        return false;
    }

    public jv(byte[] bArr, String str) {
        this.b = "1";
        this.a = (byte[]) bArr.clone();
        this.b = str;
        setDegradeAbility(lb.a.SINGLE);
        setHttpProtocol(lb.c.HTTP);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final Map<String, String> getRequestHead() {
        HashMap map = new HashMap();
        map.put("Content-Type", "application/zip");
        map.put("Content-Length", String.valueOf(this.a.length));
        return map;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        String strC = it.c(jh.b);
        byte[] bArrA = it.a(jh.a);
        byte[] bArr = new byte[bArrA.length + 50];
        System.arraycopy(this.a, 0, bArr, 0, 50);
        System.arraycopy(bArrA, 0, bArr, 50, bArrA.length);
        return String.format(strC, "1", this.b, "1", JniUscClient.q, io.a(bArr));
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final byte[] getEntityBytes() {
        return this.a;
    }
}
