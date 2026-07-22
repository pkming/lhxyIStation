package com.amap.api.col.p0003sl;

import android.os.SystemClock;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.lb;
import com.amap.api.maps.AMapException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/* JADX INFO: compiled from: BaseNetManager.java */
/* JADX INFO: loaded from: classes2.dex */
public class ku {
    public static int a = 0;
    public static String b = "";
    public static HashMap<String, String> c;
    public static HashMap<String, String> d;
    public static HashMap<String, String> e;
    private static ku f;

    /* JADX INFO: compiled from: BaseNetManager.java */
    public interface a {
        URLConnection a();
    }

    public ku() {
        ih.e();
    }

    public static ku a() {
        if (f == null) {
            f = new ku();
        }
        return f;
    }

    @Deprecated
    private static lc d(lb lbVar, boolean z) throws Cif {
        d(lbVar);
        lbVar.setHttpProtocol(z ? lb.c.HTTPS : lb.c.HTTP);
        lc lcVarA = null;
        long jElapsedRealtime = 0;
        boolean z2 = false;
        if (b(lbVar)) {
            boolean zC = c(lbVar);
            try {
                jElapsedRealtime = SystemClock.elapsedRealtime();
                lcVarA = a(lbVar, a(lbVar, zC), c(lbVar, zC));
            } catch (Cif e2) {
                if ((e2.f() == 21 && lbVar.getDegradeAbility() == lb.a.INTERRUPT_IO) || !zC) {
                    throw e2;
                }
                z2 = true;
            }
        }
        if (lcVarA != null && lcVarA.a != null && lcVarA.a.length > 0) {
            return lcVarA;
        }
        try {
            return a(lbVar, b(lbVar, z2), a(lbVar, jElapsedRealtime));
        } catch (Cif e3) {
            throw e3;
        }
    }

    public static lc a(lb lbVar) throws Cif {
        return d(lbVar, lbVar.isHttps());
    }

    private static lc a(lb lbVar, lb.b bVar, int i) throws Cif {
        try {
            d(lbVar);
            lbVar.setDegradeType(bVar);
            lbVar.setReal_max_timeout(i);
            return new ky().c(lbVar);
        } catch (Cif e2) {
            throw e2;
        } catch (Throwable th) {
            th.printStackTrace();
            throw new Cif(AMapException.ERROR_UNKNOWN);
        }
    }

    protected static lb.b a(lb lbVar, boolean z) {
        if (lbVar.getDegradeAbility() == lb.a.FIX) {
            return lb.b.FIX_NONDEGRADE;
        }
        if (lbVar.getDegradeAbility() == lb.a.SINGLE) {
            return lb.b.NEVER_GRADE;
        }
        return z ? lb.b.FIRST_NONDEGRADE : lb.b.NEVER_GRADE;
    }

    protected static lb.b b(lb lbVar, boolean z) {
        return lbVar.getDegradeAbility() == lb.a.FIX ? z ? lb.b.FIX_DEGRADE_BYERROR : lb.b.FIX_DEGRADE_ONLY : z ? lb.b.DEGRADE_BYERROR : lb.b.DEGRADE_ONLY;
    }

    protected static boolean b(lb lbVar) throws Cif {
        d(lbVar);
        try {
            String ipv6url = lbVar.getIPV6URL();
            if (TextUtils.isEmpty(ipv6url)) {
                return false;
            }
            String host = new URL(ipv6url).getHost();
            if (!TextUtils.isEmpty(lbVar.getIPDNSName())) {
                host = lbVar.getIPDNSName();
            }
            return ih.g(host);
        } catch (Throwable unused) {
            return true;
        }
    }

    protected static boolean c(lb lbVar) throws Cif {
        d(lbVar);
        try {
            if (!b(lbVar)) {
                return true;
            }
            if (lbVar.getURL().equals(lbVar.getIPV6URL()) || lbVar.getDegradeAbility() == lb.a.SINGLE) {
                return false;
            }
            if (!ih.h) {
                return false;
            }
        } catch (Throwable unused) {
        }
        return true;
    }

    protected static int c(lb lbVar, boolean z) {
        try {
            d(lbVar);
            int conntectionTimeout = lbVar.getConntectionTimeout();
            int i = ih.e;
            if (lbVar.getDegradeAbility() != lb.a.FIX) {
                if (lbVar.getDegradeAbility() != lb.a.SINGLE && conntectionTimeout >= i && z) {
                    return i;
                }
            }
            return conntectionTimeout;
        } catch (Throwable unused) {
            return 5000;
        }
    }

    protected static int a(lb lbVar, long j) {
        try {
            d(lbVar);
            long jElapsedRealtime = 0;
            if (j != 0) {
                jElapsedRealtime = SystemClock.elapsedRealtime() - j;
            }
            int conntectionTimeout = lbVar.getConntectionTimeout();
            if (lbVar.getDegradeAbility() != lb.a.FIX && lbVar.getDegradeAbility() != lb.a.SINGLE) {
                long j2 = conntectionTimeout;
                if (jElapsedRealtime < j2) {
                    long j3 = j2 - jElapsedRealtime;
                    if (j3 >= 1000) {
                        return (int) j3;
                    }
                }
                return Math.min(1000, lbVar.getConntectionTimeout());
            }
            return conntectionTimeout;
        } catch (Throwable unused) {
            return 5000;
        }
    }

    protected static void d(lb lbVar) throws Cif {
        if (lbVar == null) {
            throw new Cif("requeust is null");
        }
        if (lbVar.getURL() == null || "".equals(lbVar.getURL())) {
            throw new Cif("request url is empty");
        }
    }
}
