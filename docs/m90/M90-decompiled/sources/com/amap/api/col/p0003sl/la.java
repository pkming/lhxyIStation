package com.amap.api.col.p0003sl;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import com.amap.api.col.p0003sl.lb;
import com.amap.api.col.p0003sl.mb;
import com.amap.api.maps.AMapException;
import java.util.Map;

/* JADX INFO: compiled from: NetManger.java */
/* JADX INFO: loaded from: classes2.dex */
public final class la extends ku {
    private static la f;
    private mc g;
    private Handler h;

    public static la b() {
        return a(true);
    }

    public static la c() {
        return a(false);
    }

    private static synchronized la a(boolean z) {
        try {
            la laVar = f;
            if (laVar == null) {
                f = new la(z);
            } else if (z && laVar.g == null) {
                laVar.g = mc.a(new mb.a().a("amap-netmanger-threadpool-%d").b());
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return f;
    }

    private la(boolean z) {
        if (z) {
            try {
                this.g = mc.a(new mb.a().a("amap-netmanger-threadpool-%d").b());
            } catch (Throwable th) {
                jw.c(th, "NetManger", "NetManger1");
                th.printStackTrace();
                return;
            }
        }
        if (Looper.myLooper() == null) {
            this.h = new a(Looper.getMainLooper(), (byte) 0);
        } else {
            this.h = new a();
        }
    }

    @Deprecated
    public static Map<String, String> d(lb lbVar, boolean z) throws Cif {
        d(lbVar);
        lbVar.setHttpProtocol(z ? lb.c.HTTPS : lb.c.HTTP);
        Map<String, String> mapA = null;
        long jElapsedRealtime = 0;
        boolean z2 = false;
        if (b(lbVar)) {
            boolean zC = c(lbVar);
            try {
                jElapsedRealtime = SystemClock.elapsedRealtime();
                mapA = a(lbVar, a(lbVar, zC), c(lbVar, zC));
            } catch (Cif e) {
                if (!zC) {
                    throw e;
                }
                z2 = true;
            }
        }
        if (mapA != null) {
            return mapA;
        }
        try {
            return a(lbVar, b(lbVar, z2), a(lbVar, jElapsedRealtime));
        } catch (Cif e2) {
            throw e2;
        }
    }

    private static Map<String, String> a(lb lbVar, lb.b bVar, int i) throws Cif {
        try {
            d(lbVar);
            lbVar.setDegradeType(bVar);
            lbVar.setReal_max_timeout(i);
            return new ky().a(lbVar);
        } catch (Cif e) {
            throw e;
        } catch (Throwable th) {
            th.printStackTrace();
            throw new Cif(AMapException.ERROR_UNKNOWN);
        }
    }

    @Deprecated
    private static lc e(lb lbVar, boolean z) throws Cif {
        d(lbVar);
        lbVar.setHttpProtocol(z ? lb.c.HTTPS : lb.c.HTTP);
        lc lcVarB = null;
        long jElapsedRealtime = 0;
        boolean z2 = false;
        if (b(lbVar)) {
            boolean zC = c(lbVar);
            try {
                jElapsedRealtime = SystemClock.elapsedRealtime();
                lcVarB = b(lbVar, a(lbVar, zC), c(lbVar, zC));
            } catch (Cif e) {
                if ((e.f() == 21 && lbVar.getDegradeAbility() == lb.a.INTERRUPT_IO) || !zC) {
                    throw e;
                }
                z2 = true;
            }
        }
        if (lcVarB != null && lcVarB.a != null && lcVarB.a.length > 0) {
            return lcVarB;
        }
        try {
            return b(lbVar, b(lbVar, z2), a(lbVar, jElapsedRealtime));
        } catch (Cif e2) {
            throw e2;
        }
    }

    public static lc e(lb lbVar) throws Cif {
        return e(lbVar, lbVar.isHttps());
    }

    private static lc b(lb lbVar, lb.b bVar, int i) throws Cif {
        try {
            d(lbVar);
            lbVar.setDegradeType(bVar);
            lbVar.setReal_max_timeout(i);
            return new ky().b(lbVar);
        } catch (Cif e) {
            throw e;
        } catch (Throwable th) {
            th.printStackTrace();
            throw new Cif(AMapException.ERROR_UNKNOWN);
        }
    }

    /* JADX INFO: compiled from: NetManger.java */
    static class a extends Handler {
        /* synthetic */ a(Looper looper, byte b) {
            this(looper);
        }

        private a(Looper looper) {
            super(looper);
        }

        public a() {
        }

        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            try {
                int i = message.what;
                if (i == 0) {
                    Object obj = message.obj;
                } else {
                    if (i != 1) {
                        return;
                    }
                    Object obj2 = message.obj;
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }
}
