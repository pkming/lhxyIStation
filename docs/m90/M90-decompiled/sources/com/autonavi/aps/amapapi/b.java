package com.autonavi.aps.amapapi;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.Settings;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.Cif;
import com.amap.api.col.p0003sl.ih;
import com.amap.api.col.p0003sl.ik;
import com.amap.api.col.p0003sl.it;
import com.amap.api.col.p0003sl.jw;
import com.amap.api.col.p0003sl.lc;
import com.amap.api.col.p0003sl.nr;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClientOption;
import com.autonavi.aps.amapapi.restruct.e;
import com.autonavi.aps.amapapi.restruct.g;
import com.autonavi.aps.amapapi.restruct.k;
import com.autonavi.aps.amapapi.trans.f;
import com.autonavi.aps.amapapi.utils.h;
import com.autonavi.aps.amapapi.utils.j;
import com.unisound.client.SpeechConstants;
import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: compiled from: Aps.java */
/* JADX INFO: loaded from: classes2.dex */
public final class b {
    static int A = -1;
    private static boolean K = false;
    boolean F;
    private Handler N;
    private g O;
    private String P;
    private c R;
    public static String[] D = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    public static String E = "android.permission.ACCESS_BACKGROUND_LOCATION";
    private static volatile boolean Q = false;
    Context a = null;
    ConnectivityManager b = null;
    k c = null;
    e d = null;
    com.autonavi.aps.amapapi.storage.a e = null;
    com.autonavi.aps.amapapi.trans.e f = null;
    ArrayList<nr> g = new ArrayList<>();
    a h = null;
    AMapLocationClientOption i = new AMapLocationClientOption();
    com.autonavi.aps.amapapi.model.a j = null;
    long k = 0;
    private int I = 0;
    f l = null;
    boolean m = false;
    private String J = null;
    com.autonavi.aps.amapapi.trans.c n = null;
    StringBuilder o = new StringBuilder();
    boolean p = true;
    boolean q = true;
    AMapLocationClientOption.GeoLanguage r = AMapLocationClientOption.GeoLanguage.DEFAULT;
    boolean s = true;
    boolean t = false;
    private String L = null;
    StringBuilder u = null;
    boolean v = false;
    public boolean w = false;
    int x = 12;
    private boolean M = true;
    com.autonavi.aps.amapapi.restruct.b y = null;
    boolean z = false;
    com.autonavi.aps.amapapi.filters.a B = null;
    String C = null;
    IntentFilter G = null;
    LocationManager H = null;

    public b(boolean z) {
        this.F = false;
        this.F = z;
    }

    public final void a(Handler handler) {
        this.N = handler;
    }

    public final void a(Context context) {
        try {
            if (this.a != null) {
                return;
            }
            this.B = new com.autonavi.aps.amapapi.filters.a();
            Context applicationContext = context.getApplicationContext();
            this.a = applicationContext;
            j.b(applicationContext);
            if (this.c == null) {
                this.c = new k(this.a, (WifiManager) j.a(this.a, "wifi"), this.N);
            }
            if (this.d == null) {
                this.d = new e(this.a, this.N);
            }
            this.O = new g(context, this.N);
            if (this.e == null) {
                this.e = new com.autonavi.aps.amapapi.storage.a();
            }
            if (this.f == null) {
                this.f = new com.autonavi.aps.amapapi.trans.e();
            }
        } catch (Throwable th) {
            th.printStackTrace();
            com.autonavi.aps.amapapi.utils.b.a(th, "Aps", "initBase");
        }
    }

    public final void a() {
        e eVar = this.d;
        if (eVar != null) {
            eVar.b();
        }
    }

    public final void b() {
        this.n = com.autonavi.aps.amapapi.trans.c.a(this.a);
        i();
        if (this.b == null) {
            this.b = (ConnectivityManager) j.a(this.a, Context.CONNECTIVITY_SERVICE);
        }
        if (this.l == null) {
            this.l = new f();
        }
    }

    private void i() {
        if (this.n != null) {
            try {
                if (this.i == null) {
                    this.i = new AMapLocationClientOption();
                }
                this.n.a(this.i.getHttpTimeOut(), this.i.getLocationProtocol().equals(AMapLocationClientOption.AMapLocationProtocol.HTTPS), j());
            } catch (Throwable unused) {
            }
        }
    }

    /* JADX INFO: renamed from: com.autonavi.aps.amapapi.b$1, reason: invalid class name */
    /* JADX INFO: compiled from: Aps.java */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] a;

        static {
            int[] iArr = new int[AMapLocationClientOption.GeoLanguage.values().length];
            a = iArr;
            try {
                iArr[AMapLocationClientOption.GeoLanguage.DEFAULT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                a[AMapLocationClientOption.GeoLanguage.ZH.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                a[AMapLocationClientOption.GeoLanguage.EN.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    private int j() {
        int i;
        if (this.i.getGeoLanguage() != null && (i = AnonymousClass1.a[this.i.getGeoLanguage().ordinal()]) != 1) {
            if (i == 2) {
                return 1;
            }
            if (i == 3) {
                return 2;
            }
        }
        return 0;
    }

    public final void c() {
        if (this.y == null) {
            this.y = new com.autonavi.aps.amapapi.restruct.b(this.a);
        }
        m();
        k kVar = this.c;
        if (kVar != null) {
            kVar.b(false);
            this.g = this.c.e();
        }
        e eVar = this.d;
        if (eVar != null) {
            eVar.a(false, q());
        }
        this.e.a(this.a);
        b(this.a);
        this.w = true;
    }

    public final void a(AMapLocationClientOption aMapLocationClientOption) {
        this.i = aMapLocationClientOption;
        if (aMapLocationClientOption == null) {
            this.i = new AMapLocationClientOption();
        }
        k kVar = this.c;
        if (kVar != null) {
            this.i.isWifiActiveScan();
            kVar.a(this.i.isWifiScan(), this.i.isMockEnable(), AMapLocationClientOption.isOpenAlwaysScanWifi(), aMapLocationClientOption.getScanWifiInterval());
        }
        i();
        com.autonavi.aps.amapapi.storage.a aVar = this.e;
        if (aVar != null) {
            aVar.a(this.i);
        }
        com.autonavi.aps.amapapi.trans.e eVar = this.f;
        if (eVar != null) {
            eVar.a(this.i);
        }
        e eVar2 = this.d;
        if (eVar2 != null) {
            eVar2.c(this.i.isNoLocReqCgiEnable());
        }
        k();
    }

    private void k() {
        boolean zIsNeedAddress;
        boolean z;
        boolean zIsOffset;
        AMapLocationClientOption.GeoLanguage geoLanguage = AMapLocationClientOption.GeoLanguage.DEFAULT;
        boolean zIsLocationCacheEnable = true;
        try {
            geoLanguage = this.i.getGeoLanguage();
            zIsNeedAddress = this.i.isNeedAddress();
            try {
                zIsOffset = this.i.isOffset();
            } catch (Throwable unused) {
                z = true;
            }
        } catch (Throwable unused2) {
            zIsNeedAddress = true;
            z = true;
        }
        try {
            zIsLocationCacheEnable = this.i.isLocationCacheEnable();
            this.t = this.i.isOnceLocationLatest();
            this.z = this.i.isSensorEnable();
            if (zIsOffset != this.q || zIsNeedAddress != this.p || zIsLocationCacheEnable != this.s || geoLanguage != this.r) {
                s();
            }
        } catch (Throwable unused3) {
            z = zIsLocationCacheEnable;
            zIsLocationCacheEnable = zIsOffset;
            boolean z2 = z;
            zIsOffset = zIsLocationCacheEnable;
            zIsLocationCacheEnable = z2;
        }
        this.q = zIsOffset;
        this.p = zIsNeedAddress;
        this.s = zIsLocationCacheEnable;
        this.r = geoLanguage;
    }

    public final void d() {
        if (this.o.length() > 0) {
            StringBuilder sb = this.o;
            sb.delete(0, sb.length());
        }
    }

    public final com.autonavi.aps.amapapi.model.a a(com.autonavi.aps.amapapi.a aVar) throws Throwable {
        k kVar;
        com.autonavi.aps.amapapi.restruct.b bVar;
        k kVar2;
        d();
        aVar.e("conitue");
        if (this.a == null) {
            aVar.f("#0101");
            this.o.append("context is null#0101");
            return a(1, this.o.toString());
        }
        int i = this.I + 1;
        this.I = i;
        if (i == 1) {
            p();
        }
        if (a(this.k) && j.a(this.j)) {
            if (this.s && com.autonavi.aps.amapapi.utils.a.a(this.j.getTime())) {
                this.j.setLocationType(2);
            }
            return this.j;
        }
        com.autonavi.aps.amapapi.restruct.b bVar2 = this.y;
        if (bVar2 != null) {
            if (this.z) {
                bVar2.a();
            } else {
                bVar2.b();
            }
        }
        try {
            boolean z = this.i.isOnceLocationLatest() || !this.i.isOnceLocation();
            k kVar3 = this.c;
            if (kVar3 != null) {
                kVar3.b(z);
                this.g = this.c.e();
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "Aps", "getLocation getScanResultsParam");
        }
        try {
            e eVar = this.d;
            if (eVar != null) {
                eVar.a(false, q());
            }
        } catch (Throwable th2) {
            com.autonavi.aps.amapapi.utils.b.a(th2, "Aps", "getLocation getCgiListParam");
        }
        String strC = c(aVar);
        this.L = strC;
        if (TextUtils.isEmpty(strC)) {
            return a(this.x, this.o.toString());
        }
        this.u = a(this.u);
        k kVar4 = this.c;
        if (kVar4 != null && kVar4.n()) {
            com.autonavi.aps.amapapi.model.a aVarA = a(15, "networkLocation has been mocked!#1502");
            aVar.f("#1502");
            aVarA.setMock(true);
            aVarA.setTrustedLevel(4);
            return aVarA;
        }
        boolean zO = o();
        e eVar2 = this.d;
        com.autonavi.aps.amapapi.model.a aVarA2 = null;
        com.autonavi.aps.amapapi.model.a aVarA3 = (eVar2 == null || (kVar2 = this.c) == null) ? null : this.e.a(eVar2, zO, this.j, kVar2, this.u, this.L, this.a, false);
        if (j.a(aVarA3)) {
            aVarA3.setTrustedLevel(2);
            d(aVarA3);
        } else {
            aVarA3 = b(true, aVar);
            if (j.a(aVarA3)) {
                aVarA3.e(CallLog.Calls.NEW);
                this.e.a(this.u.toString());
                e eVar3 = this.d;
                if (eVar3 != null) {
                    this.e.a(eVar3.e());
                }
                d(aVarA3);
            } else {
                e eVar4 = this.d;
                if (eVar4 != null && (kVar = this.c) != null) {
                    aVarA2 = this.e.a(eVar4, false, this.j, kVar, this.u, this.L, this.a, true);
                }
                if (j.a(aVarA2)) {
                    aVar.f("#0001");
                    aVarA2.setTrustedLevel(2);
                    d(aVarA2);
                    aVarA3 = aVarA2;
                }
            }
        }
        try {
            if (this.c != null && aVarA3 != null) {
                long jB = k.b();
                if (jB <= 15) {
                    aVarA3.setTrustedLevel(1);
                } else if (jB <= 120) {
                    aVarA3.setTrustedLevel(2);
                } else if (jB <= 600) {
                    aVarA3.setTrustedLevel(3);
                } else {
                    aVarA3.setTrustedLevel(4);
                }
            }
        } catch (Throwable unused) {
        }
        this.e.a(this.L, this.u, aVarA3, this.a, true);
        j.a(aVarA3);
        StringBuilder sb = this.u;
        sb.delete(0, sb.length());
        if (aVarA3 != null) {
            if (this.z && (bVar = this.y) != null) {
                aVarA3.setAltitude(bVar.c());
                aVarA3.setBearing(this.y.d());
                aVarA3.setSpeed((float) this.y.e());
            } else {
                aVarA3.setAltitude(0.0d);
                aVarA3.setBearing(0.0f);
                aVarA3.setSpeed(0.0f);
            }
        }
        d(aVarA3);
        return this.j;
    }

    public final com.autonavi.aps.amapapi.model.a a(com.autonavi.aps.amapapi.model.a aVar) {
        this.B.a(this.s);
        return this.B.a(aVar);
    }

    public final void e() {
        this.C = null;
        this.v = false;
        this.w = false;
        com.autonavi.aps.amapapi.storage.a aVar = this.e;
        if (aVar != null) {
            aVar.b(this.a);
        }
        com.autonavi.aps.amapapi.filters.a aVar2 = this.B;
        if (aVar2 != null) {
            aVar2.a();
        }
        if (this.f != null) {
            this.f = null;
        }
        g gVar = this.O;
        if (gVar != null) {
            gVar.a(this.F);
        }
        l();
        ArrayList<nr> arrayList = this.g;
        if (arrayList != null) {
            arrayList.clear();
        }
        com.autonavi.aps.amapapi.restruct.b bVar = this.y;
        if (bVar != null) {
            bVar.f();
        }
        this.j = null;
        this.a = null;
        this.u = null;
        this.H = null;
    }

    private void l() {
        a aVar;
        try {
            Context context = this.a;
            if (context != null && (aVar = this.h) != null) {
                context.unregisterReceiver(aVar);
            }
        } finally {
            try {
            } finally {
            }
        }
        e eVar = this.d;
        if (eVar != null) {
            eVar.a(this.F);
        }
        k kVar = this.c;
        if (kVar != null) {
            kVar.c(this.F);
        }
    }

    public final void a(boolean z) {
        e eVar = this.d;
        if (eVar != null) {
            eVar.b(z);
        }
    }

    private void m() {
        try {
            if (this.h == null) {
                this.h = new a();
            }
            if (this.G == null) {
                IntentFilter intentFilter = new IntentFilter();
                this.G = intentFilter;
                intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
                this.G.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            }
            this.a.registerReceiver(this.h, this.G);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "Aps", "initBroadcastListener");
        }
    }

    private boolean a(long j) {
        if (!this.M) {
            this.M = true;
            return false;
        }
        if (j.b() - j < 800) {
            if ((j.a(this.j) ? j.a() - this.j.getTime() : 0L) <= 10000) {
                return true;
            }
        }
        return false;
    }

    private String c(com.autonavi.aps.amapapi.a aVar) {
        e eVar = this.d;
        String string = "";
        if (eVar == null || this.c == null) {
            return "";
        }
        int iH = eVar.h();
        com.autonavi.aps.amapapi.restruct.d dVarE = this.d.e();
        com.autonavi.aps.amapapi.restruct.d dVarF = this.d.f();
        ArrayList<nr> arrayList = this.g;
        boolean z = arrayList == null || arrayList.isEmpty();
        if (dVarE == null && dVarF == null && z) {
            if (this.b == null) {
                this.b = (ConnectivityManager) j.a(this.a, Context.CONNECTIVITY_SERVICE);
            }
            if (j.c() >= 31) {
                if (j.a(this.a) && !this.c.l()) {
                    this.x = 18;
                    this.o.append("飞行模式下关闭了WIFI开关，请关闭飞行模式或者打开WIFI开关#1802");
                    h.a((String) null, 2132);
                    aVar.f("#1802");
                    return "";
                }
            } else if (j.a(this.a) && !this.c.k()) {
                this.x = 18;
                this.o.append("飞行模式下关闭了WIFI开关，请关闭飞行模式或者打开WIFI开关#1801");
                h.a((String) null, 2132);
                aVar.f("#1801");
                return "";
            }
            if (j.c() >= 28) {
                if (this.H == null) {
                    this.H = (LocationManager) this.a.getApplicationContext().getSystemService("location");
                }
                if (!((Boolean) com.autonavi.aps.amapapi.utils.f.a(this.H, "isLocationEnabled", new Object[0])).booleanValue()) {
                    this.x = 12;
                    this.o.append("定位服务没有开启，请在设置中打开定位服务开关#1206");
                    aVar.f("#1206");
                    h.a((String) null, 2121);
                    return "";
                }
            }
            if (!j.e(this.a)) {
                this.x = 12;
                this.o.append("定位权限被禁用,请授予应用定位权限#1201");
                aVar.f("#1201");
                h.a((String) null, 2121);
                return "";
            }
            if (j.c() >= 24 && j.c() < 28 && Settings.Secure.getInt(this.a.getContentResolver(), Settings.Secure.LOCATION_MODE, 0) == 0) {
                this.x = 12;
                aVar.f("#1206");
                this.o.append("定位服务没有开启，请在设置中打开定位服务开关#1206");
                h.a((String) null, 2121);
                return "";
            }
            String strK = this.d.k();
            String strD = this.c.d();
            if (this.c.a(this.b) && strD != null) {
                this.x = 12;
                aVar.f("#1202");
                this.o.append("获取基站与获取WIFI的权限都被禁用，请在安全软件中打开应用的定位权限#1202");
                h.a((String) null, 2121);
                return "";
            }
            if (strK != null) {
                this.x = 12;
                if (!this.c.k()) {
                    aVar.f("#1204");
                    this.o.append("WIFI开关关闭，并且获取基站权限被禁用，请在安全软件中打开应用的定位权限或者打开WIFI开关#1204");
                } else {
                    aVar.f("#1205");
                    this.o.append("获取的WIFI列表为空，并且获取基站权限被禁用，请在安全软件中打开应用的定位权限#1205");
                }
                h.a((String) null, 2121);
                return "";
            }
            if (!this.c.k() && !this.d.n()) {
                this.x = 19;
                aVar.f("#1901");
                this.o.append("没有检查到SIM卡，并且WIFI开关关闭，请打开WIFI开关或者插入SIM卡#1901");
                h.a((String) null, 2133);
                return "";
            }
            if (!this.c.k()) {
                aVar.f("#1301");
                this.o.append("获取到的基站为空，并且关闭了WIFI开关，请您打开WIFI开关再发起定位#1301");
            } else {
                aVar.f("#1302");
                if (this.c.c() != null) {
                    this.o.append("获取到的基站和WIFI信息均为空，请检查是否授予APP定位权限");
                    if (!j.f(this.a)) {
                        this.o.append("或后台运行没有后台定位权限");
                    }
                    this.o.append("#1302");
                } else {
                    this.o.append("获取到的基站和WIFI信息均为空，请移动到有WIFI的区域，若确定当前区域有WIFI，请检查是否授予APP定位权限");
                    if (!j.f(this.a)) {
                        this.o.append("或后台运行没有后台定位权限");
                    }
                    this.o.append("#1302");
                }
            }
            this.x = 13;
            h.a((String) null, 2131);
            return "";
        }
        boolean zA = this.c.a(this.c.m());
        if (iH == 0) {
            boolean z2 = !this.g.isEmpty() || zA;
            boolean z3 = dVarF != null;
            if (!z3) {
                if (zA && this.g.isEmpty()) {
                    this.x = 2;
                    aVar.f("#0201");
                    this.o.append("当前基站为伪基站，并且WIFI权限被禁用，请在安全软件中打开应用的定位权限#0201");
                    h.a((String) null, 2021);
                    return "";
                }
                if (this.g.size() == 1) {
                    this.x = 2;
                    if (!zA) {
                        aVar.f("#0202");
                        this.o.append("当前基站为伪基站，并且搜到的WIFI数量不足，请移动到WIFI比较丰富的区域#0202");
                        h.a((String) null, 2022);
                        return "";
                    }
                    if (this.g.get(0).h) {
                        aVar.f("#0202");
                        this.o.append("当前基站为伪基站，并且搜到的WIFI数量不足，请移动到WIFI比较丰富的区域#0202");
                        h.a((String) null, 2021);
                        return "";
                    }
                }
            }
            String str = String.format(Locale.US, "#%s#", LocationManager.NETWORK_PROVIDER);
            if (z3) {
                StringBuilder sb = new StringBuilder();
                sb.append(dVarF.b());
                String str2 = (!this.g.isEmpty() || zA) ? "cgiwifi" : "cgi";
                sb.append(LocationManager.NETWORK_PROVIDER).append("#");
                sb.append(str2);
                string = sb.toString();
            } else if (z2) {
                string = str + "wifi";
            } else {
                this.x = 2;
                if (!this.c.k()) {
                    aVar.f("#0203");
                    this.o.append("当前基站为伪基站,并且关闭了WIFI开关，请在设置中打开WIFI开关#0203");
                } else {
                    aVar.f("#0204");
                    this.o.append("当前基站为伪基站,并且没有搜索到WIFI，请移动到WIFI比较丰富的区域#0204");
                }
                h.a((String) null, 2022);
            }
        } else if (iH != 1) {
            if (iH != 2) {
                this.x = 11;
                h.a((String) null, SpeechConstants.TTS_EVENT_STOP);
                aVar.f("#1101");
                this.o.append("get cgi failure#1101");
            } else if (dVarE != null) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(dVarE.a).append("#");
                sb2.append(dVarE.b).append("#");
                sb2.append(dVarE.h).append("#");
                sb2.append(dVarE.i).append("#");
                sb2.append(dVarE.j).append("#");
                sb2.append(LocationManager.NETWORK_PROVIDER).append("#");
                sb2.append((!this.g.isEmpty() || zA) ? "cgiwifi" : "cgi");
                string = sb2.toString();
            }
        } else if (dVarE != null) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(dVarE.a).append("#");
            sb3.append(dVarE.b).append("#");
            sb3.append(dVarE.c).append("#");
            sb3.append(dVarE.d).append("#");
            sb3.append(LocationManager.NETWORK_PROVIDER).append("#");
            sb3.append((!this.g.isEmpty() || zA) ? "cgiwifi" : "cgi");
            string = sb3.toString();
        }
        if (TextUtils.isEmpty(string)) {
            return string;
        }
        if (!string.startsWith("#")) {
            string = "#" + string;
        }
        return j.e() + string;
    }

    private StringBuilder a(StringBuilder sb) {
        if (sb == null) {
            sb = new StringBuilder(700);
        } else {
            sb.delete(0, sb.length());
        }
        e eVar = this.d;
        if (eVar != null && this.c != null) {
            sb.append(eVar.m());
            sb.append(this.c.o());
        }
        return sb;
    }

    private byte[] n() throws Throwable {
        if (this.l == null) {
            this.l = new f();
        }
        if (this.i == null) {
            this.i = new AMapLocationClientOption();
        }
        if (this.d != null && this.c != null) {
            this.l.a(this.a, this.i.isNeedAddress(), this.i.isOffset(), this.d, this.c, this.b, this.C, this.O);
        }
        return this.l.a();
    }

    /* JADX INFO: compiled from: Aps.java */
    class a extends BroadcastReceiver {
        a() {
        }

        @Override // android.content.BroadcastReceiver
        public final void onReceive(Context context, Intent intent) {
            if (context == null || intent == null) {
                return;
            }
            try {
                String action = intent.getAction();
                if (TextUtils.isEmpty(action)) {
                    return;
                }
                if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    if (b.this.c != null) {
                        b.this.c.i();
                    }
                    try {
                        if (intent.getExtras() == null || !intent.getExtras().getBoolean("resultsUpdated", true) || b.this.c == null) {
                            return;
                        }
                        b.this.c.h();
                        return;
                    } catch (Throwable unused) {
                        return;
                    }
                }
                if (!action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION) || b.this.c == null) {
                    return;
                }
                b.this.c.j();
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "Aps", "onReceive");
            }
        }
    }

    private com.autonavi.aps.amapapi.model.a a(com.autonavi.aps.amapapi.model.a aVar, lc lcVar, com.autonavi.aps.amapapi.a aVar2) {
        if (lcVar != null) {
            try {
                if (lcVar.a != null && lcVar.a.length != 0) {
                    com.autonavi.aps.amapapi.trans.e eVar = new com.autonavi.aps.amapapi.trans.e();
                    String str = new String(lcVar.a, "UTF-8");
                    if (str.contains("\"status\":\"0\"")) {
                        com.autonavi.aps.amapapi.model.a aVarA = eVar.a(str, this.a, lcVar, aVar2);
                        aVarA.h(this.u.toString());
                        return aVarA;
                    }
                    if (!str.contains("</body></html>")) {
                        return null;
                    }
                    aVar.setErrorCode(5);
                    k kVar = this.c;
                    if (kVar != null && kVar.a(this.b)) {
                        aVar2.f("#0501");
                        this.o.append("您连接的是一个需要登录的网络，请确认已经登入网络#0501");
                        h.a((String) null, 2051);
                    } else {
                        aVar2.f("#0502");
                        this.o.append("请求可能被劫持了#0502");
                        h.a((String) null, 2052);
                    }
                    aVar.setLocationDetail(this.o.toString());
                    return aVar;
                }
            } catch (Throwable th) {
                aVar.setErrorCode(4);
                com.autonavi.aps.amapapi.utils.b.a(th, "Aps", "checkResponseEntity");
                aVar2.f("#0403");
                this.o.append("check response exception ex is" + th.getMessage() + "#0403");
                aVar.setLocationDetail(this.o.toString());
                return aVar;
            }
        }
        aVar.setErrorCode(4);
        this.o.append("网络异常,请求异常#0403");
        aVar2.f("#0403");
        aVar.h(this.u.toString());
        aVar.setLocationDetail(this.o.toString());
        if (lcVar != null) {
            h.a(lcVar.d, 2041);
        }
        return aVar;
    }

    private static void c(com.autonavi.aps.amapapi.model.a aVar) {
        if (aVar.getErrorCode() == 0 && aVar.getLocationType() == 0) {
            if ("-5".equals(aVar.d()) || "1".equals(aVar.d()) || "2".equals(aVar.d()) || "14".equals(aVar.d()) || "24".equals(aVar.d()) || "-1".equals(aVar.d())) {
                aVar.setLocationType(5);
            } else {
                aVar.setLocationType(6);
            }
        }
    }

    private com.autonavi.aps.amapapi.model.a b(boolean z, com.autonavi.aps.amapapi.a aVar) {
        String str;
        try {
            if (TextUtils.isEmpty(this.P)) {
                this.P = it.b(ik.a(this.a) + "," + ik.f(this.a));
            }
            this.o.append("#id:").append(this.P);
        } catch (Throwable unused) {
        }
        com.autonavi.aps.amapapi.model.a aVar2 = new com.autonavi.aps.amapapi.model.a("");
        try {
            byte[] bArrN = n();
            long jB = j.b();
            this.k = jB;
            aVar.a(jB);
            try {
                com.autonavi.aps.amapapi.utils.b.c(this.a);
                com.autonavi.aps.amapapi.trans.d dVarA = this.n.a(this.a, bArrN, com.autonavi.aps.amapapi.utils.b.a(), com.autonavi.aps.amapapi.utils.b.b(), z);
                dVarA.getURL();
                String ipv6url = dVarA.getIPV6URL();
                ih.a(this.a);
                boolean z2 = !TextUtils.isEmpty(ipv6url) && ipv6url.contains("dualstack");
                int i = com.autonavi.aps.amapapi.trans.a.a;
                if (ih.a() && ih.c() && z2) {
                    i = com.autonavi.aps.amapapi.trans.a.b;
                }
                String strA = ih.b() ? null : com.autonavi.aps.amapapi.trans.a.a(this.a).a(dVarA, i);
                aVar.a(i == com.autonavi.aps.amapapi.trans.a.b ? "v6" : "v4");
                lc lcVarA = this.n.a(dVarA);
                long jB2 = j.b();
                if (!TextUtils.isEmpty(strA)) {
                    if (!lcVarA.f) {
                        com.autonavi.aps.amapapi.trans.a.a(this.a).a(true, i);
                    } else {
                        com.autonavi.aps.amapapi.trans.a.a(this.a).a(false, i);
                        com.autonavi.aps.amapapi.trans.a.a(this.a).a(i);
                    }
                }
                if (lcVarA != null && !TextUtils.isEmpty(strA)) {
                    if (!lcVarA.f) {
                        aVar.b(strA);
                        aVar.c("SUCCESS");
                    } else {
                        aVar.b(strA);
                        aVar.c("FAIL");
                        aVar.d("SUCCESS");
                    }
                } else {
                    aVar.d("SUCCESS");
                }
                c cVar = this.R;
                if (cVar != null) {
                    cVar.d();
                }
                aVar.b(jB2);
                if (lcVarA != null) {
                    if (!TextUtils.isEmpty(lcVarA.c)) {
                        this.o.append("#csid:" + lcVarA.c);
                    }
                    str = lcVarA.d;
                    aVar2.h(this.u.toString());
                } else {
                    str = "";
                }
                com.autonavi.aps.amapapi.model.a aVarA = a(aVar2, lcVarA, aVar);
                if (aVarA != null) {
                    return aVarA;
                }
                byte[] bArrB = com.autonavi.aps.amapapi.security.a.b(lcVarA.a);
                if (bArrB == null) {
                    aVar2.setErrorCode(5);
                    aVar.f("#0503");
                    this.o.append("解密数据失败#0503");
                    aVar2.setLocationDetail(this.o.toString());
                    h.a(str, 2053);
                    return aVar2;
                }
                com.autonavi.aps.amapapi.model.a aVarA2 = this.f.a(aVar2, bArrB, aVar);
                if (!j.a(aVarA2)) {
                    String strB = aVarA2.b();
                    this.J = strB;
                    if (!TextUtils.isEmpty(strB)) {
                        h.a(str, 2062);
                    } else {
                        h.a(str, 2061);
                    }
                    aVarA2.setErrorCode(6);
                    aVar.f("#0601");
                    this.o.append("location faile retype:" + aVarA2.d() + " rdesc:" + (TextUtils.isEmpty(this.J) ? "" : this.J) + "#0601");
                    aVarA2.h(this.u.toString());
                    aVarA2.setLocationDetail(this.o.toString());
                    return aVarA2;
                }
                c(aVarA2);
                aVarA2.setOffset(this.q);
                aVarA2.a(this.p);
                aVarA2.f(String.valueOf(this.r));
                aVarA2.e(CallLog.Calls.NEW);
                aVarA2.setLocationDetail(this.o.toString());
                this.C = aVarA2.a();
                return aVarA2;
            } catch (Throwable th) {
                j.b();
                aVar.d("FAIL");
                com.autonavi.aps.amapapi.trans.a.a(this.a).a(false, com.autonavi.aps.amapapi.trans.a.a);
                com.autonavi.aps.amapapi.utils.b.a(th, "Aps", "getApsLoc req");
                h.a("/mobile/binary", th);
                if (!j.d(this.a)) {
                    aVar.f("#0401");
                    this.o.append("网络异常，未连接到网络，请连接网络#0401");
                } else if (th instanceof Cif) {
                    Cif cif = th;
                    if (cif.a().contains("网络异常状态码")) {
                        aVar.f("#0404");
                        this.o.append("网络异常，状态码错误#0404").append(cif.f());
                    } else if (cif.f() == 23 || Math.abs((j.b() - this.k) - this.i.getHttpTimeOut()) < 500) {
                        aVar.f("#0402");
                        this.o.append("网络异常，连接超时#0402");
                    } else {
                        aVar.f("#0403," + th.getMessage());
                        this.o.append("网络异常,请求异常#0403");
                    }
                } else {
                    aVar.f("#0403," + th.getMessage());
                    this.o.append("网络异常,请求异常#0403");
                }
                com.autonavi.aps.amapapi.model.a aVarA3 = a(4, this.o.toString());
                aVarA3.h(this.u.toString());
                return aVarA3;
            }
        } catch (Throwable th2) {
            aVar.f("#0301");
            this.o.append("get parames error:" + th2.getMessage() + "#0301");
            h.a((String) null, SpeechConstants.TTS_KEY_BACKEND_MODEL_PATH);
            com.autonavi.aps.amapapi.model.a aVarA4 = a(3, this.o.toString());
            aVarA4.h(this.u.toString());
            return aVarA4;
        }
    }

    private boolean o() {
        return this.k == 0 || j.b() - this.k > 20000;
    }

    private void b(Context context) {
        try {
            if (context.checkCallingOrSelfPermission(it.c("EYW5kcm9pZC5wZXJtaXNzaW9uLldSSVRFX1NFQ1VSRV9TRVRUSU5HUw==")) == 0) {
                this.m = true;
            }
        } catch (Throwable unused) {
        }
    }

    private void p() {
        k kVar = this.c;
        if (kVar == null) {
            return;
        }
        kVar.a(this.m);
    }

    public final void f() {
        c cVar = this.R;
        if (cVar != null) {
            cVar.d();
        }
    }

    public final void b(com.autonavi.aps.amapapi.a aVar) {
        try {
            if (this.v) {
                return;
            }
            r();
            if (this.t) {
                m();
            }
            k kVar = this.c;
            if (kVar != null) {
                kVar.b(this.t);
                this.g = this.c.e();
            }
            e eVar = this.d;
            if (eVar != null) {
                eVar.a(true, q());
            }
            String strC = c(aVar);
            this.L = strC;
            if (!TextUtils.isEmpty(strC)) {
                this.u = a(this.u);
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "Aps", "initFirstLocateParam");
        }
        this.v = true;
    }

    private boolean q() {
        k kVar = this.c;
        if (kVar != null) {
            this.g = kVar.e();
        }
        ArrayList<nr> arrayList = this.g;
        return arrayList == null || arrayList.size() <= 0;
    }

    private void r() {
        if (this.L != null) {
            this.L = null;
        }
        StringBuilder sb = this.u;
        if (sb != null) {
            sb.delete(0, sb.length());
        }
    }

    public final com.autonavi.aps.amapapi.model.a b(boolean z) {
        k kVar = this.c;
        if (kVar != null && kVar.n()) {
            return a(15, "networkLocation has been mocked!#1502");
        }
        if (TextUtils.isEmpty(this.L)) {
            return a(this.x, this.o.toString());
        }
        com.autonavi.aps.amapapi.model.a aVarA = this.e.a(this.a, this.L, this.u, true, z);
        if (j.a(aVarA)) {
            d(aVarA);
        }
        return aVarA;
    }

    private void d(com.autonavi.aps.amapapi.model.a aVar) {
        if (aVar != null) {
            this.j = aVar;
        }
    }

    public final com.autonavi.aps.amapapi.model.a a(boolean z, com.autonavi.aps.amapapi.a aVar) {
        if (z) {
            aVar.e("statics");
        } else {
            aVar.e("first");
        }
        if (this.a == null) {
            aVar.f("#0101");
            this.o.append("context is null#0101");
            h.a((String) null, 2011);
            return a(1, this.o.toString());
        }
        k kVar = this.c;
        if (kVar != null && kVar.n()) {
            aVar.f("#1502");
            return a(15, "networkLocation has been mocked!#1502");
        }
        b();
        if (TextUtils.isEmpty(this.L)) {
            return a(this.x, this.o.toString());
        }
        com.autonavi.aps.amapapi.model.a aVarB = b(z, aVar);
        if (j.a(aVarB) && !Q) {
            this.e.a(this.u.toString());
            e eVar = this.d;
            if (eVar != null) {
                this.e.a(eVar.e());
            }
            d(aVarB);
        }
        Q = true;
        return aVarB;
    }

    public final void b(com.autonavi.aps.amapapi.model.a aVar) {
        if (j.a(aVar)) {
            this.e.a(this.L, this.u, aVar, this.a, true);
        }
    }

    public final void g() {
        k kVar;
        try {
            if (this.a == null) {
                return;
            }
            if (this.R == null) {
                this.R = new c(this.a);
            }
            e eVar = this.d;
            if (eVar == null || (kVar = this.c) == null) {
                return;
            }
            this.R.a(eVar, kVar, this.N);
        } catch (Throwable th) {
            jw.c(th, "as", "stc");
        }
    }

    public final void h() {
        c cVar = this.R;
        if (cVar != null) {
            cVar.a();
        }
    }

    private void s() {
        try {
            com.autonavi.aps.amapapi.storage.a aVar = this.e;
            if (aVar != null) {
                aVar.a();
            }
            d(null);
            this.M = false;
            com.autonavi.aps.amapapi.filters.a aVar2 = this.B;
            if (aVar2 != null) {
                aVar2.a();
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "Aps", "cleanCache");
        }
    }

    public final com.autonavi.aps.amapapi.model.a a(double d, double d2) {
        try {
            String strA = this.n.a(this.a, d, d2);
            if (!strA.contains("\"status\":\"1\"")) {
                return null;
            }
            com.autonavi.aps.amapapi.model.a aVarA = this.f.a(strA);
            aVarA.setLatitude(d);
            aVarA.setLongitude(d2);
            return aVarA;
        } catch (Throwable unused) {
            return null;
        }
    }

    public final void a(AMapLocation aMapLocation) {
        if (aMapLocation.getErrorCode() != 0) {
            return;
        }
        com.autonavi.aps.amapapi.restruct.f fVar = new com.autonavi.aps.amapapi.restruct.f();
        fVar.a = aMapLocation.getLocationType();
        fVar.d = aMapLocation.getTime();
        fVar.e = (int) aMapLocation.getAccuracy();
        fVar.b = aMapLocation.getLatitude();
        fVar.c = aMapLocation.getLongitude();
        if (aMapLocation.getLocationType() == 1) {
            this.O.a(fVar);
        }
    }

    public final void a(com.autonavi.aps.amapapi.model.a aVar, int i) {
        if (aVar != null && aVar.getErrorCode() == 0) {
            com.autonavi.aps.amapapi.restruct.f fVar = new com.autonavi.aps.amapapi.restruct.f();
            fVar.d = aVar.getTime();
            fVar.e = (int) aVar.getAccuracy();
            fVar.b = aVar.getLatitude();
            fVar.c = aVar.getLongitude();
            fVar.a = i;
            fVar.g = Integer.parseInt(aVar.d());
            fVar.h = aVar.l();
            this.O.b(fVar);
        }
    }

    private static com.autonavi.aps.amapapi.model.a a(int i, String str) {
        com.autonavi.aps.amapapi.model.a aVar = new com.autonavi.aps.amapapi.model.a("");
        aVar.setErrorCode(i);
        aVar.setLocationDetail(str);
        if (i == 15) {
            h.a((String) null, 2151);
        }
        return aVar;
    }
}
