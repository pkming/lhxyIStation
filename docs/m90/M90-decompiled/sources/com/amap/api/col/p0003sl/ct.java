package com.amap.api.col.p0003sl;

import android.content.Context;
import android.provider.MediaStore;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: compiled from: CustomStyleTextureRequest.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ct extends hy<String, a> {

    /* JADX INFO: compiled from: CustomStyleTextureRequest.java */
    public static class a {
        public byte[] a;
        public int b = -1;
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

    @Override // com.amap.api.col.p0003sl.hy
    protected final /* synthetic */ a a(byte[] bArr) throws hx {
        return b(bArr);
    }

    public final void b(String str) {
        this.d = str;
    }

    public ct(Context context, String str) {
        super(context, str);
        this.d = "/map/styles";
    }

    private static a b(byte[] bArr) throws hx {
        a aVar = new a();
        aVar.a = bArr;
        return aVar;
    }

    @Override // com.amap.api.col.p0003sl.db, com.amap.api.col.p0003sl.lb
    public final Map<String, String> getParams() {
        HashMap map = new HashMap(16);
        map.put("key", ig.f(this.c));
        map.put(MediaStore.EXTRA_OUTPUT, "bin");
        String strA = ij.a();
        String strA2 = ij.a(this.c, strA, it.b(map));
        map.put(SPUserInfoUtils.TS, strA);
        map.put("scode", strA2);
        return map;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return this.d;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getIPV6URL() {
        return dx.a(getURL());
    }
}
