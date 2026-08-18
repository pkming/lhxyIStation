package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Looper;
import java.lang.Thread;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: compiled from: SDKLogHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class jw extends jt implements Thread.UncaughtExceptionHandler {
    private static ExecutorService e;
    private static WeakReference<Context> g;
    private Context d;
    private static Set<Integer> f = Collections.synchronizedSet(new HashSet());
    private static final ThreadFactory h = new ThreadFactory() { // from class: com.amap.api.col.3sl.jw.2
        private final AtomicInteger a = new AtomicInteger(1);

        @Override // java.util.concurrent.ThreadFactory
        public final Thread newThread(Runnable runnable) {
            return new Thread(runnable, "pama#" + this.a.getAndIncrement()) { // from class: com.amap.api.col.3sl.jw.2.1
                @Override // java.lang.Thread, java.lang.Runnable
                public final void run() {
                    try {
                        super.run();
                    } catch (Throwable unused) {
                    }
                }
            };
        }
    };

    public static void a(Context context) {
        if (context == null) {
            return;
        }
        try {
            g = new WeakReference<>(context.getApplicationContext());
        } catch (Throwable unused) {
        }
    }

    public static synchronized jw a(Context context, is isVar) throws Cif {
        try {
            if (isVar == null) {
                throw new Cif("sdk info is null");
            }
            if (isVar.a() == null || "".equals(isVar.a())) {
                throw new Cif("sdk name is invalid");
            }
            try {
            } catch (Throwable th) {
                th.printStackTrace();
            }
            if (!f.add(Integer.valueOf(isVar.hashCode()))) {
                return (jw) jt.a;
            }
            if (jt.a == null) {
                jt.a = new jw(context);
            } else {
                jt.a.c = false;
            }
            jt.a.a(isVar, jt.a.c);
            return (jw) jt.a;
        } catch (Throwable th2) {
            throw th2;
        }
    }

    public static synchronized void b() {
        try {
            ExecutorService executorService = e;
            if (executorService != null) {
                executorService.shutdown();
            }
            kr.a();
        } catch (Throwable th) {
            th.printStackTrace();
        }
        try {
            if (jt.a != null && Thread.getDefaultUncaughtExceptionHandler() == jt.a && jt.a.b != null) {
                Thread.setDefaultUncaughtExceptionHandler(jt.a.b);
            }
            jt.a = null;
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
    }

    @Override // com.amap.api.col.p0003sl.jt
    protected final void a(is isVar, String str, String str2) {
        jx.a(isVar, this.d, str2, str);
    }

    @Override // com.amap.api.col.p0003sl.jt
    protected final void a(Throwable th, int i, String str, String str2) {
        jx.a(this.d, th, i, str, str2);
    }

    public static void a(Context context, is isVar, String str, String str2, String str3) {
        jx.a(context, isVar, str, 0, str2, str3);
    }

    public static void b(Context context, is isVar, String str, String str2, String str3) {
        jx.a(context, isVar, str, 1, str2, str3);
    }

    @Override // com.amap.api.col.p0003sl.jt
    protected final void a() {
        ju.a(this.d);
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public final void uncaughtException(Thread thread, Throwable th) {
        if (th == null) {
            return;
        }
        a(th, 0, null, null);
        if (this.b != null) {
            try {
                Thread.setDefaultUncaughtExceptionHandler(this.b);
            } catch (Throwable unused) {
            }
            this.b.uncaughtException(thread, th);
        }
    }

    @Override // com.amap.api.col.p0003sl.jt
    protected final void a(final is isVar, final boolean z) {
        try {
            mc.a().a(new md() { // from class: com.amap.api.col.3sl.jw.1
                @Override // com.amap.api.col.p0003sl.md
                public final void runTask() {
                    try {
                        synchronized (Looper.getMainLooper()) {
                            ju.a(isVar);
                        }
                        if (z) {
                            jx.a(jw.this.d);
                        }
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            });
        } catch (RejectedExecutionException unused) {
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public final void b(Throwable th, String str, String str2) {
        if (th == null) {
            return;
        }
        try {
            a(th, 1, str, str2);
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
    }

    private jw(Context context) {
        this.d = context;
        try {
            this.b = Thread.getDefaultUncaughtExceptionHandler();
            if (this.b == null) {
                Thread.setDefaultUncaughtExceptionHandler(this);
                this.c = true;
                return;
            }
            String string = this.b.toString();
            if (!string.startsWith("com.amap.apis.utils.core.dynamiccore") && (string.indexOf("com.amap.api") != -1 || string.indexOf("com.loc") != -1)) {
                this.c = false;
            } else {
                Thread.setDefaultUncaughtExceptionHandler(this);
                this.c = true;
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public static void c() {
        WeakReference<Context> weakReference = g;
        if (weakReference != null && weakReference.get() != null) {
            ju.a(g.get());
        } else if (jt.a != null) {
            jt.a.a();
        }
    }

    public static void c(Throwable th, String str, String str2) {
        try {
            if (jt.a != null) {
                jt.a.a(th, 1, str, str2);
            }
        } catch (Throwable unused) {
        }
    }

    public static void a(is isVar, String str, String str2, String str3, String str4) {
        a(isVar, str, str2, str3, "", str4);
    }

    public static void a(is isVar, String str, String str2, String str3, String str4, String str5) {
        try {
            if (jt.a != null) {
                StringBuilder sb = new StringBuilder("path:");
                sb.append(str).append(",type:").append(str2).append(",gsid:").append(str3).append(",csid:").append(str4).append(",code:").append(str5);
                jt.a.a(isVar, sb.toString(), "networkError");
            }
        } catch (Throwable unused) {
        }
    }

    public static void b(is isVar, String str, String str2) {
        try {
            if (jt.a != null) {
                jt.a.a(isVar, str, str2);
            }
        } catch (Throwable unused) {
        }
    }

    public static void a(is isVar, String str, Cif cif) {
        if (cif != null) {
            a(isVar, str, cif.c(), cif.d(), cif.e(), cif.b());
        }
    }

    @Deprecated
    public static synchronized ExecutorService d() {
        try {
            ExecutorService executorService = e;
            if (executorService == null || executorService.isShutdown()) {
                e = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(256), h);
            }
        } catch (Throwable unused) {
        }
        return e;
    }

    public static synchronized jw e() {
        return (jw) jt.a;
    }
}
