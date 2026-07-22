package com.amap.apis.utils.core.api;

import com.amap.api.col.p0003sl.ik;

/* JADX INFO: loaded from: classes2.dex */
public class AMapUtilCoreApi {
    private static NetProxy a;

    public static void setNetProxy(NetProxy netProxy) {
        a = netProxy;
    }

    public static NetProxy getNetProxy() {
        return a;
    }

    public static void setCollectInfoEnable(boolean z) {
        ik.a(z);
    }
}
