package com.amap.api.col.p0003sl;

import com.amap.api.col.p0003sl.mb;
import java.util.concurrent.BlockingQueue;

/* JADX INFO: compiled from: ThreadUtilPoolFactory.java */
/* JADX INFO: loaded from: classes2.dex */
public final class dw {
    public static mc a(String str) {
        return mc.a(new mb.a().a(str).b());
    }

    public static mc a(int i, BlockingQueue<Runnable> blockingQueue, String str) {
        return mc.a(new mb.a().a().a(i).a(blockingQueue).a(str).b());
    }
}
