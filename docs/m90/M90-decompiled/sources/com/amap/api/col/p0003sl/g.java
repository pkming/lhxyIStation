package com.amap.api.col.p0003sl;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.DPoint;
import com.autonavi.aps.amapapi.utils.b;
import com.autonavi.aps.amapapi.utils.c;
import com.autonavi.aps.amapapi.utils.d;
import com.autonavi.aps.amapapi.utils.e;
import com.autonavi.aps.amapapi.utils.f;
import com.autonavi.aps.amapapi.utils.h;
import com.autonavi.aps.amapapi.utils.i;
import com.autonavi.aps.amapapi.utils.j;
import java.util.List;

/* JADX INFO: compiled from: CoarseLocation.java */
/* JADX INFO: loaded from: classes2.dex */
public final class g {
    public static volatile AMapLocation a = null;
    private static String b = "CoarseLocation";
    private static long q = 0;
    private static boolean r = false;
    private static boolean s = false;
    private static boolean t = false;
    private static boolean u = false;
    private com.autonavi.aps.amapapi.filters.a f;
    private Handler j;
    private Context k;
    private LocationManager n;
    private AMapLocationClientOption o;
    private long c = 0;
    private boolean d = false;
    private int e = 0;
    private int g = 240;
    private int h = 80;
    private int i = 0;
    private long l = 0;
    private int m = 0;
    private Object p = new Object();
    private boolean v = true;
    private AMapLocationClientOption.GeoLanguage w = AMapLocationClientOption.GeoLanguage.DEFAULT;
    private LocationListener x = null;

    public g(Context context, Handler handler) {
        this.f = null;
        this.k = context;
        this.j = handler;
        try {
            this.n = (LocationManager) context.getSystemService("location");
        } catch (Throwable th) {
            b.a(th, b, "<init>");
        }
        this.f = new com.autonavi.aps.amapapi.filters.a();
    }

    public final void a(AMapLocationClientOption aMapLocationClientOption) {
        this.o = aMapLocationClientOption;
        if (aMapLocationClientOption == null) {
            this.o = new AMapLocationClientOption();
        }
        Object[] objArr = new Object[2];
        String str = "option: " + this.o.toString();
        d.a();
        if (!this.o.isOnceLocation()) {
            e();
        } else if (!c()) {
            d();
        } else {
            try {
                q = i.a(this.k, "pref", "lagt", q);
            } catch (Throwable unused) {
            }
            f();
        }
    }

    private boolean c() {
        boolean zBooleanValue;
        try {
            if (j.c() >= 28) {
                if (this.n == null) {
                    this.n = (LocationManager) this.k.getApplicationContext().getSystemService("location");
                }
                zBooleanValue = ((Boolean) f.a(this.n, "isLocationEnabled", new Object[0])).booleanValue();
            } else {
                zBooleanValue = true;
            }
        } catch (Throwable unused) {
            zBooleanValue = true;
        }
        try {
            if (j.c() >= 24 && j.c() < 28) {
                if (Settings.Secure.getInt(this.k.getContentResolver(), Settings.Secure.LOCATION_MODE, 0) == 0) {
                    return false;
                }
            }
        } catch (Throwable unused2) {
            Object[] objArr = new Object[1];
            d.a();
        }
        return zBooleanValue;
    }

    private void d() {
        c(a(12, "定位服务没有开启，请在设置中打开定位服务开关#1206"));
    }

    private void e() {
        c(a(20, "模糊权限下不支持连续定位#2006"));
    }

    private static com.autonavi.aps.amapapi.model.a a(int i, String str) {
        com.autonavi.aps.amapapi.model.a aVar = new com.autonavi.aps.amapapi.model.a("");
        aVar.setErrorCode(i);
        aVar.setLocationDetail(str);
        return aVar;
    }

    public final void b(AMapLocationClientOption aMapLocationClientOption) {
        if (aMapLocationClientOption == null) {
            aMapLocationClientOption = new AMapLocationClientOption();
        }
        this.o = aMapLocationClientOption;
        Object[] objArr = new Object[2];
        String str = "option: " + this.o.toString();
        d.a();
        this.j.removeMessages(100);
        if (this.w != this.o.getGeoLanguage()) {
            synchronized (this.p) {
                a = null;
            }
        }
        this.w = this.o.getGeoLanguage();
    }

    public final void a() {
        Object[] objArr = new Object[1];
        d.a();
        LocationManager locationManager = this.n;
        if (locationManager == null) {
            return;
        }
        try {
            LocationListener locationListener = this.x;
            if (locationListener != null) {
                locationManager.removeUpdates(locationListener);
                ((a) this.x).a();
                this.x = null;
                Object[] objArr2 = new Object[1];
                d.a();
            }
        } catch (Throwable th) {
            Object[] objArr3 = new Object[1];
            String str = "CoarseLocation | removeUpdates error " + th.getMessage();
            d.a();
        }
        try {
            Handler handler = this.j;
            if (handler != null) {
                handler.removeMessages(100);
            }
        } catch (Throwable unused) {
        }
        this.i = 0;
        this.c = 0L;
        this.l = 0L;
        this.e = 0;
        this.m = 0;
        this.f.a();
    }

    private void f() {
        if (this.n == null) {
            return;
        }
        try {
            this.v = true;
            Looper looperMyLooper = Looper.myLooper();
            if (looperMyLooper == null) {
                looperMyLooper = this.k.getMainLooper();
            }
            this.c = j.b();
            if (b(this.n)) {
                if (this.x == null) {
                    this.x = new a(this);
                }
                this.n.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, this.o.getInterval(), this.o.getDeviceModeDistanceFilter(), this.x, looperMyLooper);
            }
            if (a(this.n)) {
                try {
                    if (j.a() - q >= 259200000) {
                        if (j.c(this.k, "WYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19MT0NBVElPTl9FWFRSQV9DT01NQU5EUw==")) {
                            this.n.sendExtraCommand("gps", "force_xtra_injection", null);
                            q = j.a();
                            SharedPreferences.Editor editorA = i.a(this.k, "pref");
                            i.a(editorA, "lagt", q);
                            i.a(editorA);
                            Object[] objArr = new Object[1];
                            d.a();
                        } else {
                            b.a(new Exception("n_alec"), "OPENSDK_CL", "rlu_n_alec");
                        }
                    }
                } catch (Throwable th) {
                    Object[] objArr2 = new Object[1];
                    String str = "CoarseLocation | sendExtraCommand error: " + th.getMessage();
                    d.a();
                }
                if (this.x == null) {
                    this.x = new a(this);
                }
                this.n.requestLocationUpdates("gps", this.o.getInterval(), this.o.getDeviceModeDistanceFilter(), this.x, looperMyLooper);
                Object[] objArr3 = new Object[1];
                d.a();
            }
            if (s || u) {
                a(100, "系统返回定位结果超时#2002", this.o.getHttpTimeOut());
            }
            if (s || u) {
                return;
            }
            Object[] objArr4 = new Object[1];
            d.a();
            a(100, "系统定位当前不可用#2003", 0L);
        } catch (SecurityException e) {
            Object[] objArr5 = new Object[1];
            d.a();
            this.v = false;
            h.a((String) null, 2121);
            a(101, e.getMessage() + "#2004", 0L);
        } catch (Throwable th2) {
            Object[] objArr6 = new Object[1];
            String str2 = "CoarseLocation | requestLocationUpdates error: " + th2.getMessage();
            d.a();
            b.a(th2, "CoarseLocation", "requestLocationUpdates part2");
        }
    }

    private static boolean a(LocationManager locationManager) {
        try {
            if (r) {
                return s;
            }
            List<String> allProviders = locationManager.getAllProviders();
            if (allProviders != null && allProviders.size() > 0) {
                s = allProviders.contains("gps");
            } else {
                s = false;
            }
            r = true;
            return s;
        } catch (Throwable th) {
            Object[] objArr = new Object[1];
            String str = "CoarseLocation | hasProvider error: " + th.getMessage();
            d.a();
            return s;
        }
    }

    private static boolean b(LocationManager locationManager) {
        try {
            if (t) {
                return u;
            }
            boolean zIsProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            u = zIsProviderEnabled;
            t = true;
            return zIsProviderEnabled;
        } catch (Throwable th) {
            Object[] objArr = new Object[1];
            String str = "CoarseLocation | hasProvider error: " + th.getMessage();
            d.a();
            return u;
        }
    }

    private void a(int i, String str, long j) {
        try {
            if (this.j != null) {
                Message messageObtain = Message.obtain();
                AMapLocation aMapLocation = new AMapLocation("");
                aMapLocation.setErrorCode(20);
                aMapLocation.setLocationDetail(str);
                aMapLocation.setLocationType(11);
                messageObtain.obj = aMapLocation;
                messageObtain.what = i;
                this.j.sendMessageDelayed(messageObtain, j);
            }
        } catch (Throwable unused) {
            d.b();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(Location location) {
        Handler handler = this.j;
        if (handler != null) {
            handler.removeMessages(100);
        }
        if (location == null) {
            return;
        }
        try {
            AMapLocation aMapLocation = new AMapLocation(location);
            if (j.a(aMapLocation)) {
                if ("gps".equals(location.getProvider())) {
                    aMapLocation.setProvider("gps_coarse");
                } else {
                    aMapLocation.setProvider("network_coarse");
                }
                aMapLocation.setLocationType(11);
                if (!this.d && j.a(aMapLocation)) {
                    h.b(this.k, j.b() - this.c, b.a(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                    this.d = true;
                }
                Boolean bool = Boolean.FALSE;
                if (Build.VERSION.SDK_INT >= 18) {
                    try {
                        Boolean bool2 = (Boolean) f.a(location, "isFromMockProvider", new Object[0]);
                        try {
                            Object[] objArr = new Object[1];
                            "CoarseLocation | isFromMock=".concat(String.valueOf(bool2));
                            d.a();
                        } catch (Throwable unused) {
                        }
                        bool = bool2;
                    } catch (Throwable unused2) {
                    }
                }
                if (bool.booleanValue()) {
                    aMapLocation.setMock(true);
                    aMapLocation.setTrustedLevel(4);
                    if (!this.o.isMockEnable()) {
                        int i = this.m;
                        if (i > 3) {
                            h.a((String) null, 2152);
                            aMapLocation.setErrorCode(15);
                            aMapLocation.setLocationDetail("CoarseLocation has been mocked!#2007");
                            aMapLocation.setLatitude(0.0d);
                            aMapLocation.setLongitude(0.0d);
                            aMapLocation.setAltitude(0.0d);
                            aMapLocation.setSpeed(0.0f);
                            aMapLocation.setAccuracy(0.0f);
                            aMapLocation.setBearing(0.0f);
                            aMapLocation.setExtras(null);
                            c(aMapLocation);
                            return;
                        }
                        this.m = i + 1;
                        return;
                    }
                } else {
                    this.m = 0;
                }
                int iB = b(location);
                this.i = iB;
                aMapLocation.setSatellites(iB);
                e(aMapLocation);
                g(aMapLocation);
                AMapLocation aMapLocationF = f(aMapLocation);
                a(aMapLocationF);
                b(aMapLocationF);
                synchronized (this.p) {
                    a(aMapLocationF, a);
                }
                c(aMapLocationF);
            }
        } catch (Throwable th) {
            b.a(th, "CoarseLocation", "onLocationChanged");
        }
    }

    private static int b(Location location) {
        Bundle extras = location.getExtras();
        int i = extras != null ? extras.getInt("satellites") : 0;
        d.b();
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void g() {
        try {
            Object[] objArr = new Object[1];
            d.a();
            this.i = 0;
        } catch (Throwable unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(int i) {
        if (i == 0) {
            try {
                Object[] objArr = new Object[1];
                d.a();
                this.i = 0;
            } catch (Throwable unused) {
            }
        }
    }

    /* JADX INFO: compiled from: CoarseLocation.java */
    static class a implements LocationListener {
        private g a;

        a(g gVar) {
            this.a = gVar;
        }

        final void a() {
            this.a = null;
        }

        @Override // android.location.LocationListener
        public final void onLocationChanged(Location location) {
            try {
                g gVar = this.a;
                if (gVar != null) {
                    gVar.a(location);
                }
            } catch (Throwable unused) {
            }
        }

        @Override // android.location.LocationListener
        public final void onProviderDisabled(String str) {
            try {
                g gVar = this.a;
                if (gVar != null) {
                    gVar.g();
                }
            } catch (Throwable unused) {
            }
        }

        @Override // android.location.LocationListener
        public final void onProviderEnabled(String str) {
            if ("gps".equalsIgnoreCase(str)) {
                Object[] objArr = new Object[1];
                d.a();
            }
        }

        @Override // android.location.LocationListener
        public final void onStatusChanged(String str, int i, Bundle bundle) {
            try {
                g gVar = this.a;
                if (gVar != null) {
                    gVar.a(i);
                }
            } catch (Throwable unused) {
            }
        }
    }

    private void a(AMapLocation aMapLocation) {
        if (j.a(aMapLocation)) {
            this.e++;
        }
    }

    private void b(AMapLocation aMapLocation) {
        if (j.a(aMapLocation) && this.j != null) {
            long jB = j.b();
            if (this.o.getInterval() <= 8000 || jB - this.l > this.o.getInterval() - 8000) {
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", aMapLocation.getLatitude());
                bundle.putDouble("lon", aMapLocation.getLongitude());
                bundle.putFloat("radius", aMapLocation.getAccuracy());
                bundle.putLong("time", aMapLocation.getTime());
                Message messageObtain = Message.obtain();
                messageObtain.setData(bundle);
                messageObtain.what = 102;
                synchronized (this.p) {
                    if (a == null || j.a(aMapLocation, a) > this.h) {
                        this.j.sendMessage(messageObtain);
                    }
                }
            }
        }
    }

    private void c(AMapLocation aMapLocation) {
        if (this.o.getLocationMode().equals(AMapLocationClientOption.AMapLocationMode.Device_Sensors) && this.o.getDeviceModeDistanceFilter() > 0.0f) {
            d(aMapLocation);
        } else if (j.b() - this.l >= this.o.getInterval() - 200) {
            this.l = j.b();
            d(aMapLocation);
        }
    }

    private void d(AMapLocation aMapLocation) {
        if (this.j != null) {
            Object[] objArr = new Object[1];
            d.a();
            Message messageObtain = Message.obtain();
            messageObtain.obj = aMapLocation;
            messageObtain.what = 101;
            this.j.sendMessage(messageObtain);
        }
    }

    private void e(AMapLocation aMapLocation) {
        try {
            if (b.a(aMapLocation.getLatitude(), aMapLocation.getLongitude()) && this.o.isOffset()) {
                DPoint dPointA = e.a(this.k, new DPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                aMapLocation.setLatitude(dPointA.getLatitude());
                aMapLocation.setLongitude(dPointA.getLongitude());
                aMapLocation.setOffset(this.o.isOffset());
                aMapLocation.setCoordType(AMapLocation.COORD_TYPE_GCJ02);
                return;
            }
            aMapLocation.setOffset(false);
            aMapLocation.setCoordType(AMapLocation.COORD_TYPE_WGS84);
        } catch (Throwable th) {
            aMapLocation.setOffset(false);
            aMapLocation.setCoordType(AMapLocation.COORD_TYPE_WGS84);
            Object[] objArr = new Object[1];
            String str = "CoarseLocation | offset error: " + th.getMessage();
            d.a();
        }
    }

    private AMapLocation f(AMapLocation aMapLocation) {
        if (!j.a(aMapLocation) || this.e < 3) {
            return aMapLocation;
        }
        if (aMapLocation.getAccuracy() < 0.0f || aMapLocation.getAccuracy() == Float.MAX_VALUE) {
            aMapLocation.setAccuracy(0.0f);
        }
        if (aMapLocation.getSpeed() < 0.0f || aMapLocation.getSpeed() == Float.MAX_VALUE) {
            aMapLocation.setSpeed(0.0f);
        }
        return this.f.a(aMapLocation);
    }

    public final void a(Bundle bundle) {
        if (bundle != null) {
            try {
                bundle.setClassLoader(AMapLocation.class.getClassLoader());
                this.g = bundle.getInt("I_MAX_GEO_DIS");
                this.h = bundle.getInt("I_MIN_GEO_DIS");
                AMapLocation aMapLocation = (AMapLocation) bundle.getParcelable("loc");
                if (TextUtils.isEmpty(aMapLocation.getAdCode())) {
                    return;
                }
                synchronized (this.p) {
                    a = aMapLocation;
                }
            } catch (Throwable th) {
                b.a(th, "CoarseLocation", "setLastGeoLocation");
            }
        }
    }

    private void a(AMapLocation aMapLocation, AMapLocation aMapLocation2) {
        if (aMapLocation2 == null || !this.o.isNeedAddress() || j.a(aMapLocation, aMapLocation2) >= this.g) {
            return;
        }
        b.a(aMapLocation, aMapLocation2);
    }

    public final int b() {
        LocationManager locationManager = this.n;
        if (locationManager == null || !a(locationManager)) {
            return 1;
        }
        if (Build.VERSION.SDK_INT < 19) {
            if (!this.n.isProviderEnabled("gps")) {
                return 2;
            }
        } else {
            int i = Settings.Secure.getInt(this.k.getContentResolver(), Settings.Secure.LOCATION_MODE, 0);
            if (i == 0) {
                return 2;
            }
            if (i == 2) {
                return 3;
            }
        }
        return !this.v ? 4 : 0;
    }

    private static void g(AMapLocation aMapLocation) {
        if (j.a(aMapLocation) && com.autonavi.aps.amapapi.utils.a.s()) {
            long time = aMapLocation.getTime();
            long jCurrentTimeMillis = System.currentTimeMillis();
            long jA = c.a(time, jCurrentTimeMillis, com.autonavi.aps.amapapi.utils.a.t());
            if (jA != time) {
                aMapLocation.setTime(jA);
                h.a(time, jCurrentTimeMillis);
            }
        }
    }
}
