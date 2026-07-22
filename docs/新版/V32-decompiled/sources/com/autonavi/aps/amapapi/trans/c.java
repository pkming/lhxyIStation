package com.autonavi.aps.amapapi.trans;

import android.content.Context;
import android.provider.MediaStore;
import android.security.Credentials;
import com.amap.api.col.p0003sl.ig;
import com.amap.api.col.p0003sl.ij;
import com.amap.api.col.p0003sl.im;
import com.amap.api.col.p0003sl.ir;
import com.amap.api.col.p0003sl.it;
import com.amap.api.col.p0003sl.ku;
import com.amap.api.col.p0003sl.lb;
import com.amap.api.col.p0003sl.lc;
import com.amap.api.maps.model.amap3dmodeltile.AMap3DTileBuildType;
import com.autonavi.aps.amapapi.utils.j;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: compiled from: LocNetManager.java */
/* JADX INFO: loaded from: classes2.dex */
public final class c {
    private static c b;
    ku a;
    private Context c;
    private int d = com.autonavi.aps.amapapi.utils.b.i;
    private boolean e = false;
    private int f = 0;

    private c(Context context) {
        this.a = null;
        this.c = null;
        try {
            im.a().a(context);
        } catch (Throwable unused) {
        }
        this.c = context;
        this.a = ku.a();
    }

    public static c a(Context context) {
        if (b == null) {
            b = new c(context);
        }
        return b;
    }

    public final void a(long j, boolean z, int i) {
        try {
            this.e = z;
            this.d = Long.valueOf(j).intValue();
            this.f = i;
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "LocNetManager", "setOption");
        }
    }

    public final d a(Context context, byte[] bArr, String str, String str2, boolean z) {
        try {
            HashMap map = new HashMap(16);
            d dVar = new d(context, com.autonavi.aps.amapapi.utils.b.c());
            try {
                map.put("Content-Type", "application/octet-stream");
                map.put("Accept-Encoding", "gzip");
                map.put("gzipped", "1");
                map.put("Connection", "Keep-Alive");
                map.put("User-Agent", "AMAP_Location_SDK_Android 6.4.3");
                map.put(Credentials.EXTRA_PUBLIC_KEY, ig.f(context));
                map.put("enginever", com.autonavi.aps.amapapi.utils.b.a);
                String strA = ij.a();
                String strA2 = ij.a(context, strA, "key=" + ig.f(context));
                map.put(SPUserInfoUtils.TS, strA);
                map.put("scode", strA2);
                if (Double.valueOf(com.autonavi.aps.amapapi.utils.b.a).doubleValue() >= 5.3d) {
                    map.put("aps_s_src", "openapi");
                }
                map.put("encr", "1");
                dVar.b(map);
                String str3 = z ? "loc" : "locf";
                dVar.b(true);
                dVar.a(String.format(Locale.US, "platform=Android&sdkversion=%s&product=%s&loc_channel=%s", "6.4.3", str3, 3));
                dVar.a(z);
                dVar.b(str);
                dVar.c(str2);
                dVar.c(j.a(bArr));
                dVar.setProxy(ir.a(context));
                HashMap map2 = new HashMap(16);
                map2.put(MediaStore.EXTRA_OUTPUT, "bin");
                map2.put("policy", AMap3DTileBuildType.AIRPORT_TERMINAL);
                int i = this.f;
                if (i == 0) {
                    map2.remove("custom");
                } else if (i == 1) {
                    map2.put("custom", "language:cn");
                } else if (i == 2) {
                    map2.put("custom", "language:en");
                } else {
                    map2.remove("custom");
                }
                dVar.a(map2);
                dVar.setConnectionTimeout(this.d);
                dVar.setSoTimeout(this.d);
                if (!this.e) {
                    return dVar;
                }
                dVar.setHttpProtocol(lb.c.HTTPS);
                return dVar;
            } catch (Throwable unused) {
                return dVar;
            }
        } catch (Throwable unused2) {
            return null;
        }
    }

    public final lc a(d dVar) throws Throwable {
        if (this.e) {
            dVar.setHttpProtocol(lb.c.HTTPS);
        }
        return ku.a(dVar);
    }

    public final String a(Context context, double d, double d2) {
        try {
            HashMap map = new HashMap(16);
            d dVar = new d(context, com.autonavi.aps.amapapi.utils.b.c());
            map.clear();
            map.put("Content-Type", "application/x-www-form-urlencoded");
            map.put("Connection", "Keep-Alive");
            map.put("User-Agent", "AMAP_Location_SDK_Android 6.4.3");
            HashMap map2 = new HashMap(16);
            map2.put("custom", "26260A1F00020002");
            map2.put("key", ig.f(context));
            int i = this.f;
            if (i == 0) {
                map2.remove("language");
            } else if (i == 1) {
                map2.put("language", "zh-CN");
            } else if (i == 2) {
                map2.put("language", "en");
            } else {
                map2.remove("language");
            }
            map2.put("curLocationType", j.m(this.c) ? "coarseLoc" : "fineLoc");
            String strA = ij.a();
            String strA2 = ij.a(context, strA, it.b(map2));
            map2.put(SPUserInfoUtils.TS, strA);
            map2.put("scode", strA2);
            dVar.b(("output=json&radius=1000&extensions=all&location=" + d2 + "," + d).getBytes("UTF-8"));
            dVar.b(false);
            dVar.a(true);
            dVar.a(String.format(Locale.US, "platform=Android&sdkversion=%s&product=%s&loc_channel=%s", "6.4.3", "loc", 3));
            dVar.a(map2);
            dVar.b(map);
            dVar.setProxy(ir.a(context));
            dVar.setConnectionTimeout(com.autonavi.aps.amapapi.utils.b.i);
            dVar.setSoTimeout(com.autonavi.aps.amapapi.utils.b.i);
            try {
                dVar.c("http://dualstack-arestapi.amap.com/v3/geocode/regeo");
                dVar.b("http://restsdk.amap.com/v3/geocode/regeo");
                if (this.e) {
                    dVar.setHttpProtocol(lb.c.HTTPS);
                }
                return new String(ku.a(dVar).a, "utf-8");
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "LocNetManager", "post");
                return null;
            }
        } catch (Throwable unused) {
            return null;
        }
    }
}
