package com.amap.api.col.p0003sl;

import com.amap.api.col.p0003sl.lb;
import java.util.Map;

/* JADX INFO: compiled from: ADIURequest.java */
/* JADX INFO: loaded from: classes2.dex */
public final class kt extends lb {
    private byte[] a;
    private Map<String, String> b;

    @Override // com.amap.api.col.p0003sl.lb
    public final Map<String, String> getRequestHead() {
        return null;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return "https://adiu.amap.com/ws/device/adius";
    }

    public kt(byte[] bArr, Map<String, String> map) {
        this.a = bArr;
        this.b = map;
        setDegradeAbility(lb.a.SINGLE);
        setHttpProtocol(lb.c.HTTPS);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final Map<String, String> getParams() {
        return this.b;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final byte[] getEntityBytes() {
        return this.a;
    }
}
