package com.amap.api.col.p0003sl;

import android.content.Context;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import java.util.Hashtable;
import java.util.Map;

/* JADX INFO: compiled from: AuthRequest.java */
/* JADX INFO: loaded from: classes2.dex */
public final class s extends hy<String, a> {
    private boolean f;
    private int[] g;

    /* JADX INFO: compiled from: AuthRequest.java */
    public static class a {
        public String b;
        public String c;
        public int a = -1;
        public boolean d = false;
    }

    @Override // com.amap.api.col.p0003sl.hy
    protected final String c() {
        return null;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final boolean isSupportIPV6() {
        return true;
    }

    public s(Context context, String str) {
        super(context, str);
        this.f = true;
        this.g = new int[]{10000, 0, 10018, 10019, 10020, 10021, 10022, 10023};
        this.d = "/feedback";
        this.isPostFlag = false;
        this.f = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0039, code lost:
    
        r1.d = true;
     */
    @Override // com.amap.api.col.p0003sl.hy
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public com.amap.api.col.3sl.s.a a(java.lang.String r6) throws com.amap.api.col.p0003sl.hx {
        /*
            r5 = this;
            java.lang.String r0 = "errcode"
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch: java.lang.Throwable -> L41
            r1.<init>(r6)     // Catch: java.lang.Throwable -> L41
            r6 = -1
            boolean r2 = r1.has(r0)     // Catch: java.lang.Throwable -> L41
            java.lang.String r3 = ""
            if (r2 == 0) goto L21
            int r6 = r1.optInt(r0)     // Catch: java.lang.Throwable -> L41
            java.lang.String r0 = "errmsg"
            java.lang.String r3 = r1.optString(r0)     // Catch: java.lang.Throwable -> L41
            java.lang.String r0 = "errdetail"
            java.lang.String r0 = r1.optString(r0)     // Catch: java.lang.Throwable -> L41
            goto L22
        L21:
            r0 = r3
        L22:
            com.amap.api.col.3sl.s$a r1 = new com.amap.api.col.3sl.s$a     // Catch: java.lang.Throwable -> L41
            r1.<init>()     // Catch: java.lang.Throwable -> L41
            r1.a = r6     // Catch: java.lang.Throwable -> L41
            r1.b = r3     // Catch: java.lang.Throwable -> L41
            r1.c = r0     // Catch: java.lang.Throwable -> L41
            r0 = 0
            r1.d = r0     // Catch: java.lang.Throwable -> L41
            int[] r2 = r5.g     // Catch: java.lang.Throwable -> L41
            int r3 = r2.length     // Catch: java.lang.Throwable -> L41
        L33:
            if (r0 >= r3) goto L40
            r4 = r2[r0]     // Catch: java.lang.Throwable -> L41
            if (r4 != r6) goto L3d
            r6 = 1
            r1.d = r6     // Catch: java.lang.Throwable -> L41
            goto L40
        L3d:
            int r0 = r0 + 1
            goto L33
        L40:
            return r1
        L41:
            r6 = move-exception
            r6.printStackTrace()
            r6 = 0
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.s.a(java.lang.String):com.amap.api.col.3sl.s$a");
    }

    @Override // com.amap.api.col.p0003sl.db, com.amap.api.col.p0003sl.lb
    public final Map<String, String> getParams() {
        Hashtable hashtable = new Hashtable(16);
        hashtable.put("key", ig.f(this.c));
        if (this.f) {
            hashtable.put("pname", "3dmap");
        }
        String strA = ij.a();
        String strA2 = ij.a(this.c, strA, it.b(hashtable));
        hashtable.put(SPUserInfoUtils.TS, strA);
        hashtable.put("scode", strA2);
        return hashtable;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return "http://restsdk.amap.com/v4" + this.d;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getIPV6URL() {
        return dx.a(getURL());
    }
}
