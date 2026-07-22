package com.amap.api.col.p0003sl;

import com.amap.api.col.p0003sl.mb;
import java.lang.Thread;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* JADX INFO: compiled from: ThreadPool.java */
/* JADX INFO: loaded from: classes2.dex */
public final class mc extends me {
    private static Thread.UncaughtExceptionHandler c = new Thread.UncaughtExceptionHandler() { // from class: com.amap.api.col.3sl.mc.1
        @Override // java.lang.Thread.UncaughtExceptionHandler
        public final void uncaughtException(Thread thread, Throwable th) {
            jw.c(th, "TPool", "ThreadPool");
        }
    };
    private static mc d = new mc(new mb.a().a(c).a("amap-global-threadPool").b());

    public static mc a() {
        return d;
    }

    public static mc a(mb mbVar) {
        return new mc(mbVar);
    }

    private mc(mb mbVar) {
        try {
            this.a = new ThreadPoolExecutor(mbVar.a(), mbVar.b(), mbVar.d(), TimeUnit.SECONDS, mbVar.c(), mbVar);
            this.a.allowCoreThreadTimeOut(true);
        } catch (Throwable th) {
            jw.c(th, "TPool", "ThreadPool");
            th.printStackTrace();
        }
    }

    @Deprecated
    public static synchronized mc b() {
        if (d == null) {
            d = new mc(new mb.a().a(c).b());
        }
        return d;
    }

    @Deprecated
    public static mc c() {
        return new mc(new mb.a().a(c).b());
    }
}
