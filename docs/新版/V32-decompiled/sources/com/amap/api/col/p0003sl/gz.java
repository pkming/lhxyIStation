package com.amap.api.col.p0003sl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* JADX INFO: compiled from: ThreadPoolExecutorUtil.java */
/* JADX INFO: loaded from: classes2.dex */
public class gz {
    private static volatile gz c;
    private BlockingQueue<Runnable> a = new LinkedBlockingQueue();
    private ExecutorService b;

    public static gz a() {
        if (c == null) {
            synchronized (gz.class) {
                if (c == null) {
                    c = new gz();
                }
            }
        }
        return c;
    }

    public static void b() {
        if (c != null) {
            synchronized (gz.class) {
                if (c != null) {
                    c.b.shutdownNow();
                    c.b = null;
                    c = null;
                }
            }
        }
    }

    private gz() {
        this.b = null;
        int iAvailableProcessors = Runtime.getRuntime().availableProcessors();
        this.b = new ThreadPoolExecutor(iAvailableProcessors, iAvailableProcessors * 2, 1L, TimeUnit.SECONDS, this.a, new ThreadPoolExecutor.AbortPolicy());
    }

    public final void a(Runnable runnable) {
        ExecutorService executorService = this.b;
        if (executorService != null) {
            executorService.execute(runnable);
        }
    }
}
