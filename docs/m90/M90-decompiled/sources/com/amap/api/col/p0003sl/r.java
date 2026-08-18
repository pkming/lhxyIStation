package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.amap.api.col.p0003sl.s;
import com.autonavi.base.amap.api.mapcore.IAMapDelegate;
import com.autonavi.base.amap.mapcore.MapConfig;
import java.lang.ref.WeakReference;

/* JADX INFO: compiled from: AuthProTask.java */
/* JADX INFO: loaded from: classes2.dex */
public final class r extends Thread {
    private static int c = 0;
    private static int d = 3;
    private static long e = 30000;
    private static boolean g = false;
    private WeakReference<Context> a;
    private IAMapDelegate b;
    private a f = null;
    private Handler h = new Handler(Looper.getMainLooper()) { // from class: com.amap.api.col.3sl.r.1
        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            super.handleMessage(message);
            if (r.g) {
                return;
            }
            if (r.this.f == null) {
                r.this.f = new a(r.this.b, r.this.a == null ? null : (Context) r.this.a.get());
            }
            dv.a().a(r.this.f);
        }
    };

    static /* synthetic */ int b() {
        int i = c;
        c = i + 1;
        return i;
    }

    static /* synthetic */ boolean e() {
        g = true;
        return true;
    }

    public r(Context context, IAMapDelegate iAMapDelegate) {
        this.a = null;
        if (context != null) {
            this.a = new WeakReference<>(context);
        }
        this.b = iAMapDelegate;
        f();
    }

    private static void f() {
        c = 0;
        g = false;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public final void run() {
        try {
            g();
        } catch (Throwable th) {
            jw.c(th, "AMapDelegateImpGLSurfaceView", "mVerfy");
            th.printStackTrace();
            dz.b(dy.e, "auth pro exception " + th.getMessage());
        }
    }

    private void g() {
        if (g) {
            return;
        }
        int i = 0;
        while (i <= d) {
            i++;
            this.h.sendEmptyMessageDelayed(0, ((long) i) * e);
        }
    }

    @Override // java.lang.Thread
    public final void interrupt() {
        this.b = null;
        this.a = null;
        Handler handler = this.h;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        this.h = null;
        this.f = null;
        f();
        super.interrupt();
    }

    /* JADX INFO: compiled from: AuthProTask.java */
    static class a extends md {
        private WeakReference<IAMapDelegate> a;
        private WeakReference<Context> b;
        private s c;

        public a(IAMapDelegate iAMapDelegate, Context context) {
            this.a = null;
            this.b = null;
            this.a = new WeakReference<>(iAMapDelegate);
            if (context != null) {
                this.b = new WeakReference<>(context);
            }
        }

        @Override // com.amap.api.col.p0003sl.md
        public final void runTask() {
            s.a aVarD;
            WeakReference<Context> weakReference;
            try {
                if (r.g) {
                    return;
                }
                if (this.c == null && (weakReference = this.b) != null && weakReference.get() != null) {
                    this.c = new s(this.b.get(), "");
                }
                r.b();
                if (r.c > r.d) {
                    r.e();
                    a();
                    return;
                }
                s sVar = this.c;
                if (sVar == null || (aVarD = sVar.d()) == null) {
                    return;
                }
                if (!aVarD.d) {
                    a();
                }
                r.e();
            } catch (Throwable th) {
                jw.c(th, "authForPro", "loadConfigData_uploadException");
                dz.b(dy.e, "auth exception get data " + th.getMessage());
            }
        }

        private void a() {
            final IAMapDelegate iAMapDelegate;
            WeakReference<IAMapDelegate> weakReference = this.a;
            if (weakReference == null || weakReference.get() == null || (iAMapDelegate = this.a.get()) == null || iAMapDelegate.getMapConfig() == null) {
                return;
            }
            iAMapDelegate.queueEvent(new Runnable() { // from class: com.amap.api.col.3sl.r.a.1
                @Override // java.lang.Runnable
                public final void run() {
                    IAMapDelegate iAMapDelegate2 = iAMapDelegate;
                    if (iAMapDelegate2 == null || iAMapDelegate2.getMapConfig() == null) {
                        return;
                    }
                    MapConfig mapConfig = iAMapDelegate.getMapConfig();
                    mapConfig.setProFunctionAuthEnable(false);
                    if (mapConfig.isUseProFunction()) {
                        iAMapDelegate.setMapCustomEnable(mapConfig.isCustomStyleEnable(), true);
                        iAMapDelegate.reloadMapCustomStyle();
                        dd.a(a.this.b == null ? null : (Context) a.this.b.get(), "鉴权失败，当前key没有自定义纹理的使用权限，自定义纹理相关内容，将不会呈现！");
                    }
                }
            });
        }
    }
}
