package com.autonavi.aps.amapapi.trans;

import android.content.Context;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.is;
import com.amap.api.col.p0003sl.kv;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/* JADX INFO: compiled from: LocationRequest.java */
/* JADX INFO: loaded from: classes2.dex */
public final class d extends kv {
    Map<String, String> d;
    String e;
    String f;
    byte[] g;
    byte[] h;
    boolean i;
    String j;
    Map<String, String> k;
    boolean p;
    private String q;

    @Override // com.amap.api.col.p0003sl.lb
    public final String getSDKName() {
        return "loc";
    }

    public final void b(byte[] bArr) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
            if (bArr != null) {
                try {
                    byteArrayOutputStream2.write(a(bArr));
                    byteArrayOutputStream2.write(bArr);
                } catch (Throwable th) {
                    th = th;
                    byteArrayOutputStream = byteArrayOutputStream2;
                    try {
                        th.printStackTrace();
                        if (byteArrayOutputStream != null) {
                            try {
                                byteArrayOutputStream.close();
                                return;
                            } catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                        return;
                    } catch (Throwable th2) {
                        if (byteArrayOutputStream != null) {
                            try {
                                byteArrayOutputStream.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        }
                        throw th2;
                    }
                }
            }
            this.h = byteArrayOutputStream2.toByteArray();
            try {
                byteArrayOutputStream2.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    @Override // com.amap.api.col.p0003sl.kv, com.amap.api.col.p0003sl.lb
    public final Map<String, String> getParams() {
        return this.k;
    }

    public final void a(Map<String, String> map) {
        this.k = map;
    }

    public final void a(String str) {
        this.j = str;
    }

    @Override // com.amap.api.col.p0003sl.kv
    public final boolean f() {
        return this.i;
    }

    public final void a(boolean z) {
        this.i = z;
    }

    public final void c(byte[] bArr) {
        this.g = bArr;
    }

    public final void b(String str) {
        this.e = str;
    }

    public final void c(String str) {
        this.f = str;
    }

    public final void b(Map<String, String> map) {
        this.d = map;
    }

    public d(Context context, is isVar) {
        super(context, isVar);
        this.d = null;
        this.q = "";
        this.e = "";
        this.f = "";
        this.g = null;
        this.h = null;
        this.i = false;
        this.j = null;
        this.k = null;
        this.p = false;
    }

    @Override // com.amap.api.col.p0003sl.kv
    public final byte[] c() {
        return this.g;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final Map<String, String> getRequestHead() {
        return this.d;
    }

    @Override // com.amap.api.col.p0003sl.kv
    public final byte[] d() {
        return this.h;
    }

    @Override // com.amap.api.col.p0003sl.kv
    public final String g() {
        return this.j;
    }

    public final void b(boolean z) {
        this.p = z;
    }

    @Override // com.amap.api.col.p0003sl.kv
    protected final boolean h() {
        return this.p;
    }

    public final void d(String str) {
        if (!TextUtils.isEmpty(str)) {
            this.q = str;
        } else {
            this.q = "";
        }
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getIPDNSName() {
        return this.q;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return this.e;
    }

    @Override // com.amap.api.col.p0003sl.in, com.amap.api.col.p0003sl.lb
    public final String getIPV6URL() {
        return this.f;
    }
}
