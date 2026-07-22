package com.autonavi.aps.amapapi.storage;

import android.app.backup.FullBackup;
import com.amap.api.col.p0003sl.kg;
import com.amap.api.col.p0003sl.kh;
import com.amap.api.location.AMapLocation;

/* JADX INFO: compiled from: LastLocationInfo.java */
/* JADX INFO: loaded from: classes2.dex */
@kg(a = FullBackup.CACHE_TREE_TOKEN)
public class b {

    @kh(a = "a2", b = 6)
    private String a;

    @kh(a = "a3", b = 5)
    private long b;

    @kh(a = "a4", b = 6)
    private String c;
    private AMapLocation d;

    public final AMapLocation a() {
        return this.d;
    }

    public final void a(AMapLocation aMapLocation) {
        this.d = aMapLocation;
    }

    public final String b() {
        return this.c;
    }

    public final void a(String str) {
        this.c = str;
    }

    public final String c() {
        return this.a;
    }

    public final void b(String str) {
        this.a = str;
    }

    public final long d() {
        return this.b;
    }

    public final void a(long j) {
        this.b = j;
    }
}
