package com.autonavi.aps.amapapi.restruct;

import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiSsid;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.amap.api.col.p0003sl.nr;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

/* JADX INFO: compiled from: WifiManagerWrapper.java */
/* JADX INFO: loaded from: classes2.dex */
public final class k {
    static long d;
    static long e;
    static long f;
    public static long g;
    static long h;
    public static HashMap<String, Long> v = new HashMap<>(36);
    public static long w = 0;
    static int x = 0;
    public static long z = 0;
    private com.autonavi.aps.amapapi.c E;
    WifiManager a;
    Context i;
    i t;
    ArrayList<nr> b = new ArrayList<>();
    ArrayList<nr> c = new ArrayList<>();
    boolean j = false;
    StringBuilder k = null;
    boolean l = true;
    boolean m = true;
    boolean n = true;
    private volatile j B = null;
    String o = null;
    TreeMap<Integer, nr> p = null;
    public boolean q = true;
    public boolean r = true;
    public boolean s = false;
    private String C = "";
    long u = 0;
    ConnectivityManager y = null;
    private long D = 30000;
    volatile boolean A = false;

    public k(Context context, WifiManager wifiManager, Handler handler) {
        this.a = wifiManager;
        this.i = context;
        i iVar = new i(context, "wifiAgee", handler);
        this.t = iVar;
        iVar.a();
    }

    public final ArrayList<nr> a() {
        if (!this.s) {
            return this.c;
        }
        b(true);
        return this.c;
    }

    private List<nr> r() {
        List<ScanResult> scanResults;
        if (this.a != null) {
            try {
                if (com.autonavi.aps.amapapi.utils.j.c(this.i, "EYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19XSUZJX1NUQVRF")) {
                    scanResults = this.a.getScanResults();
                } else {
                    com.autonavi.aps.amapapi.utils.b.a(new Exception("gst_n_aws"), "OPENSDK_WMW", "gsr_n_aws");
                    scanResults = null;
                }
                if (Build.VERSION.SDK_INT >= 17) {
                    HashMap<String, Long> map = new HashMap<>(36);
                    if (scanResults != null) {
                        for (ScanResult scanResult : scanResults) {
                            map.put(scanResult.BSSID, Long.valueOf(scanResult.timestamp));
                        }
                    }
                    if (v.isEmpty() || !v.equals(map)) {
                        v = map;
                        w = com.autonavi.aps.amapapi.utils.j.b();
                    }
                } else {
                    w = com.autonavi.aps.amapapi.utils.j.b();
                }
                this.o = null;
                ArrayList arrayList = new ArrayList();
                this.C = "";
                this.B = m();
                if (a(this.B)) {
                    this.C = this.B.a();
                }
                if (scanResults != null && scanResults.size() > 0) {
                    int size = scanResults.size();
                    for (int i = 0; i < size; i++) {
                        ScanResult scanResult2 = scanResults.get(i);
                        nr nrVar = new nr(!TextUtils.isEmpty(this.C) && this.C.equals(scanResult2.BSSID));
                        nrVar.b = scanResult2.SSID;
                        nrVar.d = scanResult2.frequency;
                        nrVar.e = scanResult2.timestamp;
                        nrVar.a = nr.a(scanResult2.BSSID);
                        nrVar.c = (short) scanResult2.level;
                        if (Build.VERSION.SDK_INT >= 17) {
                            nrVar.g = (short) ((SystemClock.elapsedRealtime() - (scanResult2.timestamp / 1000)) / 1000);
                            if (nrVar.g < 0) {
                                nrVar.g = (short) 0;
                            }
                        }
                        nrVar.f = com.autonavi.aps.amapapi.utils.j.b();
                        arrayList.add(nrVar);
                    }
                }
                this.t.a((List) arrayList);
                return arrayList;
            } catch (SecurityException e2) {
                this.o = e2.getMessage();
            } catch (Throwable th) {
                this.o = null;
                com.autonavi.aps.amapapi.utils.b.a(th, "WifiManagerWrapper", "getScanResults");
            }
        }
        return null;
    }

    public static long b() {
        return ((com.autonavi.aps.amapapi.utils.j.b() - w) / 1000) + 1;
    }

    public final WifiInfo c() {
        try {
            if (this.a == null) {
                return null;
            }
            if (com.autonavi.aps.amapapi.utils.j.c(this.i, "EYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19XSUZJX1NUQVRF")) {
                return this.a.getConnectionInfo();
            }
            com.autonavi.aps.amapapi.utils.b.a(new Exception("gci_n_aws"), "OPENSDK_WMW", "gci_n_aws");
            return null;
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "WifiManagerWrapper", "getConnectionInfo");
            return null;
        }
    }

    private int s() {
        WifiManager wifiManager = this.a;
        if (wifiManager != null) {
            return wifiManager.getWifiState();
        }
        return 4;
    }

    private boolean t() {
        long jB = com.autonavi.aps.amapapi.utils.j.b() - d;
        if (jB < 4900) {
            return false;
        }
        if (u() && jB < 9900) {
            return false;
        }
        if (x > 1) {
            long jO = this.D;
            if (jO == 30000) {
                jO = com.autonavi.aps.amapapi.utils.a.o() != -1 ? com.autonavi.aps.amapapi.utils.a.o() : 30000L;
            }
            if (Build.VERSION.SDK_INT >= 28 && jB < jO) {
                return false;
            }
        }
        if (this.a != null) {
            d = com.autonavi.aps.amapapi.utils.j.b();
            int i = x;
            if (i < 2) {
                x = i + 1;
            }
            if (com.autonavi.aps.amapapi.utils.j.c(this.i, "WYW5kcm9pZC5wZXJtaXNzaW9uLkNIQU5HRV9XSUZJX1NUQVRF")) {
                return this.a.startScan();
            }
            com.autonavi.aps.amapapi.utils.b.a(new Exception("n_cws"), "OPENSDK_WMW", "wfs_n_cws");
        }
        return false;
    }

    public final boolean a(ConnectivityManager connectivityManager) {
        try {
            if (com.autonavi.aps.amapapi.utils.j.a(connectivityManager.getActiveNetworkInfo()) == 1) {
                return a(m());
            }
            return false;
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "WifiManagerWrapper", "wifiAccess");
            return false;
        }
    }

    private boolean u() {
        if (this.y == null) {
            this.y = (ConnectivityManager) com.autonavi.aps.amapapi.utils.j.a(this.i, Context.CONNECTIVITY_SERVICE);
        }
        return a(this.y);
    }

    private boolean v() {
        if (this.a == null) {
            return false;
        }
        return com.autonavi.aps.amapapi.utils.j.g(this.i);
    }

    public final void a(boolean z2) {
        Context context = this.i;
        if (!com.autonavi.aps.amapapi.utils.a.n() || !this.n || this.a == null || context == null || !z2 || com.autonavi.aps.amapapi.utils.j.c() <= 17) {
            return;
        }
        ContentResolver contentResolver = context.getContentResolver();
        try {
            if (((Integer) com.autonavi.aps.amapapi.utils.f.a("android.provider.Settings$Global", "getInt", new Object[]{contentResolver, Settings.Global.WIFI_SCAN_ALWAYS_AVAILABLE}, (Class<?>[]) new Class[]{ContentResolver.class, String.class})).intValue() == 0) {
                com.autonavi.aps.amapapi.utils.f.a("android.provider.Settings$Global", "putInt", new Object[]{contentResolver, Settings.Global.WIFI_SCAN_ALWAYS_AVAILABLE, 1}, (Class<?>[]) new Class[]{ContentResolver.class, String.class, Integer.TYPE});
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "WifiManagerWrapper", "enableWifiAlwaysScan");
        }
    }

    public final boolean a(j jVar) {
        if (jVar == null) {
            return false;
        }
        boolean zD = jVar.d();
        if (!zD && v()) {
            g();
        }
        return zD;
    }

    public final String d() {
        return this.o;
    }

    private void d(boolean z2) {
        ArrayList<nr> arrayList = this.b;
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        if (com.autonavi.aps.amapapi.utils.j.b() - g > 3600000) {
            g();
        }
        if (this.p == null) {
            this.p = new TreeMap<>(Collections.reverseOrder());
        }
        this.p.clear();
        if (this.s && z2) {
            try {
                this.c.clear();
            } catch (Throwable unused) {
            }
        }
        int size = this.b.size();
        this.u = 0L;
        for (int i = 0; i < size; i++) {
            nr nrVar = this.b.get(i);
            if (nrVar.h) {
                this.u = nrVar.f;
            }
            if (com.autonavi.aps.amapapi.utils.j.a(nrVar != null ? nr.a(nrVar.a) : "") && (size <= 20 || a(nrVar.c))) {
                if (this.s && z2) {
                    this.c.add(nrVar);
                }
                if (!TextUtils.isEmpty(nrVar.b)) {
                    if (!WifiSsid.NONE.equals(nrVar.b)) {
                        nrVar.b = String.valueOf(i);
                    }
                } else {
                    nrVar.b = "unkwn";
                }
                this.p.put(Integer.valueOf((nrVar.c * 25) + i), nrVar);
            }
        }
        this.b.clear();
        Iterator<nr> it = this.p.values().iterator();
        while (it.hasNext()) {
            this.b.add(it.next());
        }
        this.p.clear();
    }

    public final ArrayList<nr> e() {
        if (this.b == null) {
            return null;
        }
        ArrayList<nr> arrayList = new ArrayList<>();
        if (!this.b.isEmpty()) {
            arrayList.addAll(this.b);
        }
        return arrayList;
    }

    public final void b(boolean z2) {
        if (z2) {
            w();
        } else {
            x();
        }
        boolean z3 = false;
        if (this.A) {
            this.A = false;
            z();
        }
        y();
        if (com.autonavi.aps.amapapi.utils.j.b() - g > 20000) {
            this.b.clear();
        }
        e = com.autonavi.aps.amapapi.utils.j.b();
        if (this.b.isEmpty()) {
            g = com.autonavi.aps.amapapi.utils.j.b();
            List<nr> listR = r();
            if (listR != null) {
                this.b.addAll(listR);
                z3 = true;
            }
        }
        d(z3);
    }

    public final void f() {
        try {
            this.s = true;
            List<nr> listR = r();
            if (listR != null) {
                this.b.clear();
                this.b.addAll(listR);
            }
            d(true);
        } catch (Throwable unused) {
        }
    }

    private void w() {
        if (B()) {
            long jB = com.autonavi.aps.amapapi.utils.j.b();
            if (jB - e >= 10000) {
                this.b.clear();
                h = g;
            }
            x();
            if (jB - e >= 10000) {
                for (int i = 20; i > 0 && g == h; i--) {
                    try {
                        Thread.sleep(150L);
                    } catch (Throwable unused) {
                    }
                }
            }
        }
    }

    public final void a(boolean z2, boolean z3, boolean z4, long j) {
        this.l = z2;
        this.m = z3;
        this.n = z4;
        if (j < 10000) {
            this.D = 10000L;
        } else {
            this.D = j;
        }
    }

    public final void g() {
        this.B = null;
        this.b.clear();
    }

    private void x() {
        if (B()) {
            try {
                if (t()) {
                    f = com.autonavi.aps.amapapi.utils.j.b();
                }
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "WifiManager", "wifiScan");
            }
        }
    }

    private void y() {
        if (h != g) {
            List<nr> listR = null;
            try {
                listR = r();
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "WifiManager", "updateScanResult");
            }
            h = g;
            if (listR != null) {
                this.b.clear();
                this.b.addAll(listR);
            } else {
                this.b.clear();
            }
        }
    }

    public final void a(com.autonavi.aps.amapapi.c cVar) {
        this.E = cVar;
    }

    public final void h() {
        z = System.currentTimeMillis();
        com.autonavi.aps.amapapi.c cVar = this.E;
        if (cVar != null) {
            cVar.b();
        }
    }

    public final void i() {
        if (this.a != null && com.autonavi.aps.amapapi.utils.j.b() - g > 4900) {
            g = com.autonavi.aps.amapapi.utils.j.b();
        }
    }

    private void z() {
        int iS;
        try {
            if (this.a == null) {
                return;
            }
            try {
                iS = s();
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "OPENSDK_WMW", "cwsc");
                iS = 4;
            }
            if (this.b == null) {
                this.b = new ArrayList<>();
            }
            if (iS == 0 || iS == 1 || iS == 4) {
                g();
            }
        } catch (Throwable unused) {
        }
    }

    public final void j() {
        if (this.a == null) {
            return;
        }
        this.A = true;
    }

    private static boolean a(int i) {
        int iCalculateSignalLevel = 20;
        try {
            iCalculateSignalLevel = WifiManager.calculateSignalLevel(i, 20);
        } catch (ArithmeticException e2) {
            com.autonavi.aps.amapapi.utils.b.a(e2, "Aps", "wifiSigFine");
        }
        return iCalculateSignalLevel > 0;
    }

    public final boolean k() {
        return this.q;
    }

    public final boolean l() {
        return this.r;
    }

    private void A() {
        try {
            if (com.autonavi.aps.amapapi.utils.j.c(this.i, "EYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19XSUZJX1NUQVRF")) {
                this.r = this.a.isWifiEnabled();
            }
        } catch (Throwable unused) {
            com.autonavi.aps.amapapi.utils.d.b();
        }
    }

    private boolean B() {
        this.q = v();
        A();
        if (this.q && this.l) {
            if (f == 0) {
                return true;
            }
            if (com.autonavi.aps.amapapi.utils.j.b() - f >= 4900 && com.autonavi.aps.amapapi.utils.j.b() - g >= 1500) {
                int i = ((com.autonavi.aps.amapapi.utils.j.b() - g) > 4900L ? 1 : ((com.autonavi.aps.amapapi.utils.j.b() - g) == 4900L ? 0 : -1));
                return true;
            }
        }
        return false;
    }

    public final j m() {
        A();
        if (!l()) {
            return null;
        }
        if (this.B == null) {
            Log.w("SystemApiWrapper", "getwifiAccess " + this.B);
            this.B = new j(c());
        }
        return this.B;
    }

    public final boolean n() {
        return this.j;
    }

    public final String o() {
        boolean z2;
        String str;
        StringBuilder sb = this.k;
        if (sb == null) {
            this.k = new StringBuilder(700);
        } else {
            sb.delete(0, sb.length());
        }
        this.j = false;
        int size = this.b.size();
        int i = 0;
        boolean z3 = false;
        boolean z4 = false;
        while (i < size) {
            String strA = nr.a(this.b.get(i).a);
            if (!this.m && !WifiSsid.NONE.equals(this.b.get(i).b)) {
                z3 = true;
            }
            if (TextUtils.isEmpty(this.C) || !this.C.equals(strA)) {
                z2 = z4;
                str = "nb";
            } else {
                str = "access";
                z2 = true;
            }
            this.k.append(String.format(Locale.US, "#%s,%s", strA, str));
            i++;
            z4 = z2;
        }
        if (this.b.size() == 0) {
            z3 = true;
        }
        if (!this.m && !z3) {
            this.j = true;
        }
        if (!z4 && !TextUtils.isEmpty(this.C)) {
            this.k.append("#").append(this.C);
            this.k.append(",access");
        }
        return this.k.toString();
    }

    public final void c(boolean z2) {
        g();
        this.b.clear();
        this.t.a(z2);
    }

    public static String p() {
        return String.valueOf(com.autonavi.aps.amapapi.utils.j.b() - g);
    }

    public final long q() {
        return this.u;
    }
}
