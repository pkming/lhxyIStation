package com.amap.api.col.p0003sl;

import android.util.Base64;
import java.nio.charset.StandardCharsets;

/* JADX INFO: compiled from: CollectionUploader.java */
/* JADX INFO: loaded from: classes2.dex */
public final class mo {
    public static boolean a(byte[] bArr) {
        if (bArr == null) {
            return false;
        }
        byte[] bArr2 = null;
        try {
            nv nvVar = new nv();
            nvVar.b.put("Content-Type", "application/octet-stream");
            nvVar.b.put("aps_c_src", Base64.encodeToString(nv.a().getBytes(), 2));
            nvVar.b.put("aps_c_key", Base64.encodeToString(nv.b().getBytes(), 2));
            nvVar.d = bArr;
            if (mf.a) {
                nvVar.a = "http://cgicol.amap.com/collection/collectData?src=baseCol&ver=v74&";
            } else {
                nvVar.a = (mf.b ? "https://" : "http://") + "cgicol.amap.com/collection/collectData?src=baseCol&ver=v74&";
            }
            nw nwVarA = nj.b().a(nvVar);
            if (nwVarA != null && nwVarA.a == 200) {
                bArr2 = nwVarA.c;
            }
            if (bArr2 != null) {
                return "true".equals(new String(bArr2, StandardCharsets.UTF_8));
            }
            return false;
        } catch (Exception e) {
            nu.a(e);
            return false;
        }
    }
}
