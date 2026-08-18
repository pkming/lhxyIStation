package com.amap.api.col.p0003sl;

import com.amap.api.col.p0003sl.md;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* JADX INFO: compiled from: BasePool.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class me {
    protected ThreadPoolExecutor a;
    private ConcurrentHashMap<md, Future<?>> c = new ConcurrentHashMap<>();
    protected md.a b = new md.a() { // from class: com.amap.api.col.3sl.me.1
        @Override // com.amap.api.col.3sl.md.a
        public final void a(md mdVar) {
            me.this.a(mdVar, false);
        }

        @Override // com.amap.api.col.3sl.md.a
        public final void b(md mdVar) {
            me.this.a(mdVar, true);
        }
    };

    private synchronized boolean b(md mdVar) {
        boolean zContainsKey;
        try {
            zContainsKey = this.c.containsKey(mdVar);
        } catch (Throwable th) {
            jw.c(th, "TPool", "contain");
            th.printStackTrace();
            zContainsKey = false;
        }
        return zContainsKey;
    }

    private synchronized void a(md mdVar, Future<?> future) {
        try {
            this.c.put(mdVar, future);
        } catch (Throwable th) {
            jw.c(th, "TPool", "addQueue");
            th.printStackTrace();
        }
    }

    protected final synchronized void a(md mdVar, boolean z) {
        try {
            Future<?> futureRemove = this.c.remove(mdVar);
            if (z && futureRemove != null) {
                futureRemove.cancel(true);
            }
        } catch (Throwable th) {
            jw.c(th, "TPool", "removeQueue");
            th.printStackTrace();
        }
    }

    public final void a(md mdVar) {
        ThreadPoolExecutor threadPoolExecutor;
        if (b(mdVar) || (threadPoolExecutor = this.a) == null || threadPoolExecutor.isShutdown()) {
            return;
        }
        mdVar.f = this.b;
        try {
            Future<?> futureSubmit = this.a.submit(mdVar);
            if (futureSubmit == null) {
                return;
            }
            a(mdVar, futureSubmit);
        } catch (RejectedExecutionException e) {
            jw.c(e, "TPool", "addTask");
        }
    }

    public final Executor d() {
        return this.a;
    }

    public final void a(long j, TimeUnit timeUnit) {
        try {
            ThreadPoolExecutor threadPoolExecutor = this.a;
            if (threadPoolExecutor != null) {
                threadPoolExecutor.awaitTermination(j, timeUnit);
            }
        } catch (InterruptedException unused) {
        }
    }

    public final void e() {
        try {
            Iterator<Map.Entry<md, Future<?>>> it = this.c.entrySet().iterator();
            while (it.hasNext()) {
                Future<?> future = this.c.get(it.next().getKey());
                if (future != null) {
                    try {
                        future.cancel(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            this.c.clear();
        } catch (Throwable th) {
            jw.c(th, "TPool", "destroy");
            th.printStackTrace();
        }
        ThreadPoolExecutor threadPoolExecutor = this.a;
        if (threadPoolExecutor != null) {
            threadPoolExecutor.shutdown();
        }
    }
}
