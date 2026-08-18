package com.autonavi.aps.amapapi.restruct;

import android.content.Context;
import android.os.Handler;
import com.amap.api.col.p0003sl.nr;

/* JADX INFO: compiled from: WifiAgeEstimator.java */
/* JADX INFO: loaded from: classes2.dex */
public final class i extends a<nr> {
    @Override // com.autonavi.aps.amapapi.restruct.a
    final /* bridge */ /* synthetic */ void a(nr nrVar, long j) {
        a2(nrVar, j);
    }

    @Override // com.autonavi.aps.amapapi.restruct.a
    public final /* synthetic */ String b(nr nrVar) {
        return a(nrVar);
    }

    @Override // com.autonavi.aps.amapapi.restruct.a
    final /* synthetic */ int c(nr nrVar) {
        return b2(nrVar);
    }

    @Override // com.autonavi.aps.amapapi.restruct.a
    final /* synthetic */ long d(nr nrVar) {
        return c2(nrVar);
    }

    public i(Context context, String str, Handler handler) {
        super(context, str, handler);
    }

    private static String a(nr nrVar) {
        return nrVar == null ? "" : nrVar.a();
    }

    /* JADX INFO: renamed from: b, reason: avoid collision after fix types in other method */
    private static int b2(nr nrVar) {
        if (nrVar == null) {
            return -113;
        }
        return nrVar.c;
    }

    /* JADX INFO: renamed from: c, reason: avoid collision after fix types in other method */
    private static long c2(nr nrVar) {
        if (nrVar == null) {
            return 0L;
        }
        return nrVar.f;
    }

    @Override // com.autonavi.aps.amapapi.restruct.a
    final long b() {
        return com.autonavi.aps.amapapi.config.a.e;
    }

    @Override // com.autonavi.aps.amapapi.restruct.a
    final long c() {
        return com.autonavi.aps.amapapi.config.a.f;
    }

    /* JADX INFO: renamed from: a, reason: avoid collision after fix types in other method */
    private static void a2(nr nrVar, long j) {
        if (nrVar != null) {
            nrVar.f = j;
        }
    }
}
