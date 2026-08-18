package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Build;
import org.json.JSONObject;

/* JADX INFO: compiled from: AAIDRemapRequest.java */
/* JADX INFO: loaded from: classes2.dex */
public final class iz extends jb {
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

    public iz(Context context) {
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
            jSONObject.put("method", "remap");
            jSONObject.put("package_name", ig.c(this.k));
            jSONObject.put("model", Build.MODEL);
            jSONObject.put("os_version", Build.VERSION.RELEASE);
            jSONObject.put("os_type", "Android");
            jSONObject.put("sdk_version", "4.3.11");
            String strA = ix.a();
            this.a = strA;
            jSONObject.put("t1", strA);
            jSONObject.put("old_t1", iu.g(this.k));
            String strB = ix.b();
            this.b = strB;
            jSONObject.put("t2", strB);
            jSONObject.put("old_t2", iu.h(this.k));
            String strC = ix.c();
            this.c = strC;
            jSONObject.put("t3", strC);
            jSONObject.put("old_t3", iu.i(this.k));
            String strD = ix.d();
            this.d = strD;
            jSONObject.put("s1", strD);
            jSONObject.put("old_s1", iu.j(this.k));
            String strE = ix.e();
            this.e = strE;
            jSONObject.put("s2", strE);
            jSONObject.put("old_s2", iu.k(this.k));
            String strF = ix.f();
            this.f = strF;
            jSONObject.put("s3", strF);
            jSONObject.put("old_s3", iu.l(this.k));
            String strG = ix.g();
            this.g = strG;
            jSONObject.put("s4", strG);
            jSONObject.put("old_s4", iu.m(this.k));
            jSONObject.put("uuid", ix.a(this.k));
            jSONObject.put("android_id", ik.g());
            jSONObject.put("hostname", ix.h());
            String strT = ik.t(this.k);
            this.h = strT;
            jSONObject.put("gaid", strT);
            jSONObject.put("old_gaid", iu.n(this.k));
            String strE2 = ik.e(this.k);
            this.i = strE2;
            jSONObject.put("oaid", strE2);
            jSONObject.put("old_oaid", iu.b(this.k));
            jSONObject.put("aaid", iu.c(this.k));
            jSONObject.put("resetToken", iu.f(this.k));
            jSONObject.put("uabc", iu.e(this.k));
            this.j = ix.a(it.d(jSONObject.toString().getBytes("utf-8")), it.c("YWDR1a2R2WEd0M3RXdHRocg==").getBytes());
        } catch (Throwable unused) {
        }
        return this.j;
    }
}
