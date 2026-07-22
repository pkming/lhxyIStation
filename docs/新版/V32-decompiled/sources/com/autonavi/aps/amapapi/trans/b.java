package com.autonavi.aps.amapapi.trans;

import android.text.TextUtils;
import com.amap.api.col.p0003sl.in;
import java.util.Map;

/* JADX INFO: compiled from: HttpRequest.java */
/* JADX INFO: loaded from: classes2.dex */
public final class b extends in {
    Map<String, String> a = null;
    Map<String, String> b = null;
    String c = "";
    byte[] d = null;
    private String e = null;

    public final void a(Map<String, String> map) {
        this.a = map;
    }

    public final void b(Map<String, String> map) {
        this.b = map;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final Map<String, String> getRequestHead() {
        return this.a;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final Map<String, String> getParams() {
        return this.b;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return this.c;
    }

    public final void a(String str) {
        this.c = str;
    }

    public final void a(byte[] bArr) {
        this.d = bArr;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final byte[] getEntityBytes() {
        return this.d;
    }

    public final void b(String str) {
        this.e = str;
    }

    @Override // com.amap.api.col.p0003sl.in, com.amap.api.col.p0003sl.lb
    public final String getIPV6URL() {
        if (!TextUtils.isEmpty(this.e)) {
            return this.e;
        }
        return super.getIPV6URL();
    }
}
