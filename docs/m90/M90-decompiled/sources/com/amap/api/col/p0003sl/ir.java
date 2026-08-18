package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Build;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;

/* JADX INFO: compiled from: ProxyUtil.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ir {
    public static Proxy a(Context context) {
        Proxy proxyB;
        try {
            if (Build.VERSION.SDK_INT >= 11) {
                proxyB = a(context, new URI("http://restsdk.amap.com"));
            } else {
                proxyB = b(context);
            }
            return proxyB;
        } catch (Throwable th) {
            jw.c(th, "pu", "gp");
            return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:101:0x0147  */
    /* JADX WARN: Removed duplicated region for block: B:115:0x0165 A[Catch: all -> 0x0161, TRY_LEAVE, TryCatch #0 {all -> 0x0161, blocks: (B:108:0x0156, B:115:0x0165), top: B:127:0x0156 }] */
    /* JADX WARN: Removed duplicated region for block: B:127:0x0156 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:131:0x00be A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:133:0x014b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:136:0x00d9 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00ad A[PHI: r11
      0x00ad: PHI (r11v31 java.lang.String) = (r11v27 java.lang.String), (r11v36 java.lang.String) binds: [B:48:0x00ab, B:29:0x0076] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:50:0x00b0 A[PHI: r10 r11
      0x00b0: PHI (r10v11 int) = (r10v10 int), (r10v13 int) binds: [B:48:0x00ab, B:29:0x0076] A[DONT_GENERATE, DONT_INLINE]
      0x00b0: PHI (r11v32 java.lang.String) = (r11v27 java.lang.String), (r11v36 java.lang.String) binds: [B:48:0x00ab, B:29:0x0076] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00b9  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x00f4 A[Catch: all -> 0x017a, TryCatch #12 {all -> 0x017a, blocks: (B:66:0x00cf, B:75:0x00e9, B:77:0x00f4, B:79:0x0108, B:81:0x010e, B:86:0x011c, B:89:0x0125, B:91:0x012b, B:93:0x0131, B:98:0x013f), top: B:135:0x0028 }] */
    /* JADX WARN: Type inference failed for: r10v1 */
    /* JADX WARN: Type inference failed for: r10v15 */
    /* JADX WARN: Type inference failed for: r10v16 */
    /* JADX WARN: Type inference failed for: r10v17 */
    /* JADX WARN: Type inference failed for: r10v18 */
    /* JADX WARN: Type inference failed for: r10v19 */
    /* JADX WARN: Type inference failed for: r10v2 */
    /* JADX WARN: Type inference failed for: r10v3 */
    /* JADX WARN: Type inference failed for: r10v4 */
    /* JADX WARN: Type inference failed for: r10v7, types: [int] */
    /* JADX WARN: Type inference failed for: r10v8 */
    /* JADX WARN: Type inference failed for: r10v9 */
    /* JADX WARN: Type inference failed for: r18v0 */
    /* JADX WARN: Type inference failed for: r18v1 */
    /* JADX WARN: Type inference failed for: r18v10 */
    /* JADX WARN: Type inference failed for: r18v11 */
    /* JADX WARN: Type inference failed for: r18v2 */
    /* JADX WARN: Type inference failed for: r18v3 */
    /* JADX WARN: Type inference failed for: r18v4 */
    /* JADX WARN: Type inference failed for: r18v5 */
    /* JADX WARN: Type inference failed for: r18v6 */
    /* JADX WARN: Type inference failed for: r18v7 */
    /* JADX WARN: Type inference failed for: r18v8 */
    /* JADX WARN: Type inference failed for: r18v9 */
    /* JADX WARN: Type inference failed for: r9v0, types: [android.content.ContentResolver, android.database.Cursor] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.net.Proxy b(android.content.Context r19) {
        /*
            Method dump skipped, instruction units count: 393
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.ir.b(android.content.Context):java.net.Proxy");
    }

    private static String a(String str) {
        return it.c(str);
    }

    private static String a() {
        String defaultHost;
        try {
            defaultHost = android.net.Proxy.getDefaultHost();
        } catch (Throwable th) {
            jw.c(th, "pu", "gdh");
            defaultHost = null;
        }
        return defaultHost == null ? "null" : defaultHost;
    }

    private static Proxy a(Context context, URI uri) {
        Proxy proxy;
        if (c(context)) {
            try {
                List<Proxy> listSelect = ProxySelector.getDefault().select(uri);
                if (listSelect == null || listSelect.isEmpty() || (proxy = listSelect.get(0)) == null) {
                    return null;
                }
                if (proxy.type() == Proxy.Type.DIRECT) {
                    return null;
                }
                return proxy;
            } catch (Throwable th) {
                jw.c(th, "pu", "gpsc");
            }
        }
        return null;
    }

    private static boolean c(Context context) {
        return ik.j(context) == 0;
    }

    private static int b() {
        try {
            return android.net.Proxy.getDefaultPort();
        } catch (Throwable th) {
            jw.c(th, "pu", "gdp");
            return -1;
        }
    }
}
