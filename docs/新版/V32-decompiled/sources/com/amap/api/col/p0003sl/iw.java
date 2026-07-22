package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Build;
import com.unisound.common.r;
import org.json.JSONObject;

/* JADX INFO: compiled from: AAIDCreateRequest.java */
/* JADX INFO: loaded from: classes2.dex */
public final class iw extends jb {
    public String a;
    public String b;
    public String c;
    public String d;
    public String e;
    public String f;
    public String g;
    public String h;
    public String i;
    protected byte[] j;

    public iw(Context context) {
        super(context);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final byte[] getEntityBytes() {
        byte[] bArr = this.j;
        if (bArr != null) {
            return bArr;
        }
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("method", r.s);
            jSONObject.put("package_name", ig.c(this.k));
            jSONObject.put("model", Build.MODEL);
            jSONObject.put("os_version", Build.VERSION.RELEASE);
            jSONObject.put("os_type", "Android");
            jSONObject.put("sdk_version", "4.3.11");
            String strA = ix.a();
            this.a = strA;
            jSONObject.put("t1", strA);
            String strB = ix.b();
            this.b = strB;
            jSONObject.put("t2", strB);
            String strC = ix.c();
            this.c = strC;
            jSONObject.put("t3", strC);
            String strD = ix.d();
            this.d = strD;
            jSONObject.put("s1", strD);
            String strE = ix.e();
            this.e = strE;
            jSONObject.put("s2", strE);
            String strF = ix.f();
            this.f = strF;
            jSONObject.put("s3", strF);
            String strG = ix.g();
            this.g = strG;
            jSONObject.put("s4", strG);
            jSONObject.put("uuid", ix.a(this.k));
            jSONObject.put("android_id", ik.g());
            jSONObject.put("hostname", ix.h());
            String strT = ik.t(this.k);
            this.h = strT;
            jSONObject.put("gaid", strT);
            String strE2 = ik.e(this.k);
            this.i = strE2;
            jSONObject.put("oaid", strE2);
            this.j = ix.a(it.d(jSONObject.toString().getBytes("utf-8")), it.c("YWDR1a2R2WEd0M3RXdHRocg==").getBytes());
        } catch (Throwable unused) {
        }
        return this.j;
    }
}
