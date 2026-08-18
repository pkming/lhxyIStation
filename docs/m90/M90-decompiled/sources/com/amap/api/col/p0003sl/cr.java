package com.amap.api.col.p0003sl;

import android.content.Context;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* JADX INFO: compiled from: CustomStyleRequest.java */
/* JADX INFO: loaded from: classes2.dex */
public final class cr extends hy<String, a> {
    private String f;
    private String g;
    private String h;
    private final String i;
    private boolean j;
    private String k;

    /* JADX INFO: compiled from: CustomStyleRequest.java */
    public static class a {
        public byte[] a;
        public int b = -1;
        public String c = null;
        public boolean d = false;
    }

    @Override // com.amap.api.col.p0003sl.hy
    protected final /* bridge */ /* synthetic */ a a(String str) throws hx {
        return null;
    }

    @Override // com.amap.api.col.p0003sl.hy
    protected final String c() {
        return null;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final boolean isSupportIPV6() {
        return true;
    }

    public cr(Context context, String str) {
        super(context, str);
        this.g = "1.0";
        this.h = "0";
        this.i = "lastModified";
        this.j = false;
        this.k = null;
        this.d = "/map/styles";
        this.e = true;
    }

    public cr(Context context, String str, boolean z) {
        super(context, str);
        this.g = "1.0";
        this.h = "0";
        this.i = "lastModified";
        this.j = false;
        this.k = null;
        this.j = z;
        if (z) {
            this.d = "/sdk/map/styles";
            this.isPostFlag = false;
        } else {
            this.d = "/map/styles";
        }
        this.e = true;
    }

    public final void b(String str) {
        this.k = str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Override // com.amap.api.col.p0003sl.hy
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public a a(lc lcVar) throws hx {
        List<String> list;
        if (lcVar == null) {
            return null;
        }
        a aVarA = a(lcVar.a);
        aVarA.d = aVarA.a != null;
        if (lcVar.b == null || !lcVar.b.containsKey("lastModified") || (list = lcVar.b.get("lastModified")) == null || list.size() <= 0) {
            return aVarA;
        }
        aVarA.c = list.get(0);
        return aVarA;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Override // com.amap.api.col.p0003sl.hy
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public a a(byte[] bArr) throws hx {
        a aVar = new a();
        aVar.a = bArr;
        if (this.j && bArr != null) {
            if (bArr.length == 0) {
                aVar.a = null;
            } else if (aVar.a.length <= 1024) {
                try {
                    if (new String(bArr, "utf-8").contains("errcode")) {
                        aVar.a = null;
                    }
                } catch (Exception e) {
                    jw.c(e, "CustomStyleRequest", "loadData");
                }
            }
        }
        return aVar;
    }

    @Override // com.amap.api.col.p0003sl.hy, com.amap.api.col.p0003sl.lb
    public final Map<String, String> getRequestHead() {
        is isVarA = dx.a();
        String strB = isVarA != null ? isVarA.b() : null;
        Hashtable hashtable = new Hashtable(16);
        hashtable.put("User-Agent", w.c);
        hashtable.put("Accept-Encoding", "gzip");
        hashtable.put("platinfo", String.format(Locale.US, "platform=Android&sdkversion=%s&product=%s", strB, "3dmap"));
        hashtable.put("x-INFO", ij.a(this.c));
        hashtable.put("key", ig.f(this.c));
        hashtable.put("logversion", "2.1");
        return hashtable;
    }

    @Override // com.amap.api.col.p0003sl.db, com.amap.api.col.p0003sl.lb
    public final Map<String, String> getParams() {
        Hashtable hashtable = new Hashtable(16);
        hashtable.put("key", ig.f(this.c));
        if (!this.j) {
            hashtable.put(MediaStore.EXTRA_OUTPUT, "bin");
        } else {
            hashtable.put("sdkType", this.k);
        }
        hashtable.put("styleid", this.f);
        hashtable.put(ContactsContract.PresenceColumns.PROTOCOL, this.g);
        hashtable.put("ispublic", "1");
        hashtable.put("lastModified", this.h);
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

    public final void c(String str) {
        this.f = str;
    }

    public final void d(String str) {
        this.h = str;
    }
}
