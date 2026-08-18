package com.amap.api.col.p0003sl;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
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
import android.util.TimedRemoteCaller;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.DPoint;
import com.autonavi.aps.amapapi.utils.b;
import com.autonavi.aps.amapapi.utils.c;
import com.autonavi.aps.amapapi.utils.d;
import com.autonavi.aps.amapapi.utils.e;
import com.autonavi.aps.amapapi.utils.f;
import com.autonavi.aps.amapapi.utils.i;
import com.autonavi.aps.amapapi.utils.j;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: compiled from: GpsLocation.java */
/* JADX INFO: loaded from: classes2.dex */
public final class h {
    static AMapLocation j = null;
    static long k = 0;
    static Object l = new Object();
    static long q = 0;
    static boolean t = false;
    static boolean u = false;
    public static volatile AMapLocation y;
    private GnssStatus.Callback F;
    Handler a;
    LocationManager b;
    AMapLocationClientOption c;
    com.autonavi.aps.amapapi.filters.a f;
    private Context z;
    private long A = 0;
    long d = 0;
    boolean e = false;
    private int B = 0;
    int g = 240;
    int h = 80;
    AMapLocation i = null;
    long m = 0;
    float n = 0.0f;
    Object o = new Object();
    Object p = new Object();
    private int C = 0;
    private GpsStatus D = null;
    private GpsStatus.Listener E = null;
    AMapLocationClientOption.GeoLanguage r = AMapLocationClientOption.GeoLanguage.DEFAULT;
    boolean s = true;
    long v = 0;
    int w = 0;
    LocationListener x = null;
    private String G = null;
    private boolean H = false;
    private int I = 0;
    private boolean J = false;

    public h(Context context, Handler handler) {
        this.f = null;
        this.z = context;
        this.a = handler;
        try {
            this.b = (LocationManager) context.getSystemService("location");
        } catch (Throwable th) {
            b.a(th, "GpsLocation", "<init>");
        }
        this.f = new com.autonavi.aps.amapapi.filters.a();
    }

    public final void a(AMapLocationClientOption aMapLocationClientOption) {
        this.c = aMapLocationClientOption;
        if (aMapLocationClientOption == null) {
            this.c = new AMapLocationClientOption();
        }
        try {
            q = i.a(this.z, "pref", "lagt", q);
        } catch (Throwable unused) {
        }
        i();
    }

    public final void b(AMapLocationClientOption aMapLocationClientOption) {
        Handler handler;
        if (aMapLocationClientOption == null) {
            aMapLocationClientOption = new AMapLocationClientOption();
        }
        this.c = aMapLocationClientOption;
        if (aMapLocationClientOption.getLocationMode() != AMapLocationClientOption.AMapLocationMode.Device_Sensors && (handler = this.a) != null) {
            handler.removeMessages(8);
        }
        if (this.r != this.c.getGeoLanguage()) {
            synchronized (this.o) {
                y = null;
            }
        }
        this.r = this.c.getGeoLanguage();
    }

    public final void a() {
        LocationManager locationManager = this.b;
        if (locationManager == null) {
            return;
        }
        try {
            LocationListener locationListener = this.x;
            if (locationListener != null) {
                locationManager.removeUpdates(locationListener);
                ((a) this.x).a();
                this.x = null;
            }
        } catch (Throwable unused) {
        }
        try {
            GpsStatus.Listener listener = this.E;
            if (listener != null) {
                this.b.removeGpsStatusListener(listener);
            }
        } catch (Throwable unused2) {
        }
        try {
            GnssStatus.Callback callback = this.F;
            if (callback != null) {
                this.b.unregisterGnssStatusCallback(callback);
            }
        } catch (Throwable unused3) {
        }
        try {
            Handler handler = this.a;
            if (handler != null) {
                handler.removeMessages(8);
            }
        } catch (Throwable unused4) {
        }
        this.C = 0;
        this.A = 0L;
        this.v = 0L;
        this.d = 0L;
        this.B = 0;
        this.w = 0;
        this.f.a();
        this.i = null;
        this.m = 0L;
        this.n = 0.0f;
        this.G = null;
        this.J = false;
    }

    private void i() {
        if (this.b == null) {
            return;
        }
        try {
            n();
            this.s = true;
            Looper looperMyLooper = Looper.myLooper();
            if (looperMyLooper == null) {
                looperMyLooper = this.z.getMainLooper();
            }
            Looper looper = looperMyLooper;
            this.A = j.b();
            if (a(this.b)) {
                try {
                    if (j.a() - q >= 259200000) {
                        if (j.c(this.z, "WYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19MT0NBVElPTl9FWFRSQV9DT01NQU5EUw==")) {
                            this.b.sendExtraCommand("gps", "force_xtra_injection", null);
                            q = j.a();
                            SharedPreferences.Editor editorA = i.a(this.z, "pref");
                            i.a(editorA, "lagt", q);
                            i.a(editorA);
                            Object[] objArr = new Object[1];
                            d.a();
                        } else {
                            b.a(new Exception("n_alec"), "OPENSDK_GL", "rlu_n_alec");
                        }
                    }
                } catch (Throwable th) {
                    Object[] objArr2 = new Object[1];
                    String str = "GpsLocation | sendExtraCommand error: " + th.getMessage();
                    d.a();
                }
                if (this.x == null) {
                    this.x = new a(this);
                }
                if (this.c.getLocationMode().equals(AMapLocationClientOption.AMapLocationMode.Device_Sensors) && this.c.getDeviceModeDistanceFilter() > 0.0f) {
                    this.b.requestLocationUpdates("gps", this.c.getInterval(), this.c.getDeviceModeDistanceFilter(), this.x, looper);
                } else {
                    this.b.requestLocationUpdates("gps", 900L, 0.0f, this.x, looper);
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    GnssStatus.Callback callback = new GnssStatus.Callback() { // from class: com.amap.api.col.3sl.h.1
                        @Override // android.location.GnssStatus.Callback
                        public final void onStarted() {
                            h.j();
                        }

                        @Override // android.location.GnssStatus.Callback
                        public final void onStopped() {
                            h.this.k();
                        }

                        @Override // android.location.GnssStatus.Callback
                        public final void onFirstFix(int i) {
                            h.l();
                        }

                        @Override // android.location.GnssStatus.Callback
                        public final void onSatelliteStatusChanged(GnssStatus gnssStatus) {
                            h.this.a(gnssStatus);
                        }
                    };
                    this.F = callback;
                    this.b.registerGnssStatusCallback(callback);
                } else {
                    GpsStatus.Listener listener = new GpsStatus.Listener() { // from class: com.amap.api.col.3sl.h.2
                        @Override // android.location.GpsStatus.Listener
                        public final void onGpsStatusChanged(int i) {
                            try {
                                if (h.this.b == null) {
                                    return;
                                }
                                h hVar = h.this;
                                hVar.D = hVar.b.getGpsStatus(h.this.D);
                                if (i == 1) {
                                    h.j();
                                    return;
                                }
                                if (i == 2) {
                                    h.this.k();
                                } else if (i == 3) {
                                    h.l();
                                } else {
                                    if (i != 4) {
                                        return;
                                    }
                                    h.this.m();
                                }
                            } catch (Throwable th2) {
                                Object[] objArr3 = new Object[1];
                                String str2 = "GpsLocation | onGpsStatusChanged error: " + th2.getMessage();
                                d.a();
                                b.a(th2, "GpsLocation", "onGpsStatusChanged");
                            }
                        }
                    };
                    this.E = listener;
                    this.b.addGpsStatusListener(listener);
                    Object[] objArr3 = new Object[1];
                    d.a();
                }
                a(8, 14, "no enough satellites#1401", this.c.getHttpTimeOut());
                return;
            }
            Object[] objArr4 = new Object[1];
            d.a();
            a(8, 14, "no gps provider#1402", 0L);
        } catch (SecurityException e) {
            Object[] objArr5 = new Object[1];
            d.a();
            this.s = false;
            com.autonavi.aps.amapapi.utils.h.a((String) null, 2121);
            a(2, 12, e.getMessage() + "#1201", 0L);
        } catch (Throwable th2) {
            Object[] objArr6 = new Object[1];
            String str2 = "GpsLocation | requestLocationUpdates error: " + th2.getMessage();
            d.a();
            b.a(th2, "GpsLocation", "requestLocationUpdates part2");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void j() {
        Object[] objArr = new Object[1];
        d.a();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void k() {
        Object[] objArr = new Object[1];
        d.a();
        this.C = 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void l() {
        Object[] objArr = new Object[1];
        d.a();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void m() {
        Iterable<GpsSatellite> satellites;
        int i = 0;
        try {
            GpsStatus gpsStatus = this.D;
            if (gpsStatus != null && (satellites = gpsStatus.getSatellites()) != null) {
                Iterator<GpsSatellite> it = satellites.iterator();
                int maxSatellites = this.D.getMaxSatellites();
                while (it.hasNext() && i < maxSatellites) {
                    if (it.next().usedInFix()) {
                        i++;
                    }
                }
            }
        } catch (Throwable th) {
            b.a(th, "GpsLocation", "GPS_EVENT_SATELLITE_STATUS");
        }
        this.C = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(GnssStatus gnssStatus) {
        int i = 0;
        if (gnssStatus != null) {
            try {
                if (Build.VERSION.SDK_INT >= 24) {
                    int satelliteCount = gnssStatus.getSatelliteCount();
                    int i2 = 0;
                    while (i < satelliteCount) {
                        try {
                            if (gnssStatus.usedInFix(i)) {
                                i2++;
                            }
                            i++;
                        } catch (Throwable th) {
                            th = th;
                            i = i2;
                            b.a(th, "GpsLocation_Gnss", "GPS_EVENT_SATELLITE_STATUS");
                            this.C = i;
                        }
                    }
                    i = i2;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }
        this.C = i;
    }

    private static boolean a(LocationManager locationManager) {
        try {
            if (t) {
                return u;
            }
            List<String> allProviders = locationManager.getAllProviders();
            if (allProviders != null && allProviders.size() > 0) {
                u = allProviders.contains("gps");
            } else {
                u = false;
            }
            t = true;
            return u;
        } catch (Throwable th) {
            Object[] objArr = new Object[1];
            String str = "GpsLocation | hasProvider error: " + th.getMessage();
            d.a();
            return u;
        }
    }

    private void a(int i, int i2, String str, long j2) {
        try {
            if (this.a == null || this.c.getLocationMode() != AMapLocationClientOption.AMapLocationMode.Device_Sensors) {
                return;
            }
            Message messageObtain = Message.obtain();
            AMapLocation aMapLocation = new AMapLocation("");
            aMapLocation.setProvider("gps");
            aMapLocation.setErrorCode(i2);
            aMapLocation.setLocationDetail(str);
            aMapLocation.setLocationType(1);
            messageObtain.obj = aMapLocation;
            messageObtain.what = i;
            this.a.sendMessageDelayed(messageObtain, j2);
        } catch (Throwable unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(Location location) {
        Handler handler = this.a;
        if (handler != null) {
            handler.removeMessages(8);
        }
        if (location == null) {
            return;
        }
        try {
            AMapLocation aMapLocation = new AMapLocation(location);
            if (j.a(aMapLocation)) {
                aMapLocation.setProvider("gps");
                aMapLocation.setLocationType(1);
                if (!this.e && j.a(aMapLocation)) {
                    com.autonavi.aps.amapapi.utils.h.a(this.z, j.b() - this.A, b.a(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                    this.e = true;
                }
                if (j.a(aMapLocation, this.C)) {
                    aMapLocation.setMock(true);
                    aMapLocation.setTrustedLevel(4);
                    if (!this.c.isMockEnable()) {
                        int i = this.w;
                        if (i > 3) {
                            com.autonavi.aps.amapapi.utils.h.a((String) null, 2152);
                            aMapLocation.setErrorCode(15);
                            aMapLocation.setLocationDetail("GpsLocation has been mocked!#1501");
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
                        this.w = i + 1;
                        return;
                    }
                } else {
                    this.w = 0;
                }
                aMapLocation.setSatellites(this.C);
                e(aMapLocation);
                f(aMapLocation);
                h(aMapLocation);
                AMapLocation aMapLocationG = g(aMapLocation);
                a(aMapLocationG);
                b(aMapLocationG);
                synchronized (this.o) {
                    a(aMapLocationG, y);
                }
                try {
                    if (j.a(aMapLocationG)) {
                        if (this.i != null) {
                            this.m = location.getTime() - this.i.getTime();
                            this.n = j.a(this.i, aMapLocationG);
                        }
                        synchronized (this.p) {
                            this.i = aMapLocationG.m31clone();
                        }
                        this.G = null;
                        this.H = false;
                        this.I = 0;
                    }
                } catch (Throwable th) {
                    b.a(th, "GpsLocation", "onLocationChangedLast");
                }
                c(aMapLocationG);
            }
        } catch (Throwable th2) {
            b.a(th2, "GpsLocation", "onLocationChanged");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(String str) {
        try {
            if ("gps".equalsIgnoreCase(str)) {
                this.d = 0L;
                this.C = 0;
            }
        } catch (Throwable unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(int i) {
        if (i == 0) {
            try {
                this.d = 0L;
                this.C = 0;
            } catch (Throwable unused) {
            }
        }
    }

    /* JADX INFO: compiled from: GpsLocation.java */
    static class a implements LocationListener {
        private h a;

        @Override // android.location.LocationListener
        public final void onProviderEnabled(String str) {
        }

        a(h hVar) {
            this.a = hVar;
        }

        final void a() {
            this.a = null;
        }

        @Override // android.location.LocationListener
        public final void onLocationChanged(Location location) {
            try {
                Object[] objArr = new Object[2];
                String str = "tid=" + Thread.currentThread().getId();
                d.a();
                h hVar = this.a;
                if (hVar != null) {
                    hVar.a(location);
                }
            } catch (Throwable unused) {
            }
        }

        @Override // android.location.LocationListener
        public final void onProviderDisabled(String str) {
            try {
                h hVar = this.a;
                if (hVar != null) {
                    hVar.a(str);
                }
            } catch (Throwable unused) {
            }
        }

        @Override // android.location.LocationListener
        public final void onStatusChanged(String str, int i, Bundle bundle) {
            try {
                h hVar = this.a;
                if (hVar != null) {
                    hVar.a(i);
                }
            } catch (Throwable unused) {
            }
        }
    }

    private void a(AMapLocation aMapLocation) {
        if (j.a(aMapLocation)) {
            this.d = j.b();
            synchronized (l) {
                k = j.b();
                j = aMapLocation.m31clone();
            }
            this.B++;
        }
    }

    private void b(AMapLocation aMapLocation) {
        if (j.a(aMapLocation) && this.a != null) {
            long jB = j.b();
            if (this.c.getInterval() <= 8000 || jB - this.v > this.c.getInterval() - 8000) {
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", aMapLocation.getLatitude());
                bundle.putDouble("lon", aMapLocation.getLongitude());
                bundle.putFloat("radius", aMapLocation.getAccuracy());
                bundle.putLong("time", aMapLocation.getTime());
                Message messageObtain = Message.obtain();
                messageObtain.setData(bundle);
                messageObtain.what = 5;
                synchronized (this.o) {
                    if (y == null || j.a(aMapLocation, y) > this.h) {
                        this.a.sendMessage(messageObtain);
                    }
                }
            }
        }
    }

    private void c(AMapLocation aMapLocation) {
        if (aMapLocation.getErrorCode() != 15 || AMapLocationClientOption.AMapLocationMode.Device_Sensors.equals(this.c.getLocationMode())) {
            if (this.c.getLocationMode().equals(AMapLocationClientOption.AMapLocationMode.Device_Sensors) && this.c.getDeviceModeDistanceFilter() > 0.0f) {
                d(aMapLocation);
            } else if (j.b() - this.v >= this.c.getInterval() - 200) {
                this.v = j.b();
                d(aMapLocation);
            }
        }
    }

    private void d(AMapLocation aMapLocation) {
        if (this.a != null) {
            Message messageObtain = Message.obtain();
            messageObtain.obj = aMapLocation;
            messageObtain.what = 2;
            this.a.sendMessage(messageObtain);
        }
    }

    public final boolean b() {
        return j.b() - this.d <= 2800;
    }

    private void e(AMapLocation aMapLocation) {
        try {
            if (b.a(aMapLocation.getLatitude(), aMapLocation.getLongitude()) && this.c.isOffset()) {
                DPoint dPointA = e.a(this.z, new DPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                aMapLocation.setLatitude(dPointA.getLatitude());
                aMapLocation.setLongitude(dPointA.getLongitude());
                aMapLocation.setOffset(this.c.isOffset());
                aMapLocation.setCoordType(AMapLocation.COORD_TYPE_GCJ02);
                return;
            }
            aMapLocation.setOffset(false);
            aMapLocation.setCoordType(AMapLocation.COORD_TYPE_WGS84);
        } catch (Throwable unused) {
            aMapLocation.setOffset(false);
            aMapLocation.setCoordType(AMapLocation.COORD_TYPE_WGS84);
        }
    }

    private void f(AMapLocation aMapLocation) {
        try {
            int i = this.C;
            if (i >= 4) {
                aMapLocation.setGpsAccuracyStatus(1);
            } else if (i == 0) {
                aMapLocation.setGpsAccuracyStatus(-1);
            } else {
                aMapLocation.setGpsAccuracyStatus(0);
            }
        } catch (Throwable unused) {
        }
    }

    private AMapLocation g(AMapLocation aMapLocation) {
        if (!j.a(aMapLocation) || this.B < 3) {
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
                synchronized (this.o) {
                    y = aMapLocation;
                }
            } catch (Throwable th) {
                b.a(th, "GpsLocation", "setLastGeoLocation");
            }
        }
    }

    private void a(AMapLocation aMapLocation, AMapLocation aMapLocation2) {
        if (aMapLocation2 == null || !this.c.isNeedAddress() || j.a(aMapLocation, aMapLocation2) >= this.g) {
            return;
        }
        b.a(aMapLocation, aMapLocation2);
    }

    public final void c() {
        this.w = 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:48:0x0098  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final com.amap.api.location.AMapLocation a(com.amap.api.location.AMapLocation r17, java.lang.String r18) {
        /*
            Method dump skipped, instruction units count: 229
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.h.a(com.amap.api.location.AMapLocation, java.lang.String):com.amap.api.location.AMapLocation");
    }

    private boolean b(String str) {
        try {
            ArrayList<String> arrayListB = j.b(str);
            ArrayList<String> arrayListB2 = j.b(this.G);
            if (arrayListB.size() < 8 || arrayListB2.size() < 8) {
                return false;
            }
            return j.a(this.G, str);
        } catch (Throwable unused) {
            return false;
        }
    }

    public final int d() {
        LocationManager locationManager = this.b;
        if (locationManager == null || !a(locationManager)) {
            return 1;
        }
        if (Build.VERSION.SDK_INT < 19) {
            if (!this.b.isProviderEnabled("gps")) {
                return 2;
            }
        } else {
            int i = Settings.Secure.getInt(this.z.getContentResolver(), Settings.Secure.LOCATION_MODE, 0);
            if (i == 0) {
                return 2;
            }
            if (i == 2) {
                return 3;
            }
        }
        return !this.s ? 4 : 0;
    }

    public final int e() {
        return this.C;
    }

    private void n() {
        if (j.b() - k > TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS || !j.a(j)) {
            return;
        }
        if (this.c.isMockEnable() || !j.isMock()) {
            this.d = j.b();
            c(j);
        }
    }

    private static boolean o() {
        try {
            return ((Boolean) f.a(it.c("KY29tLmFtYXAuYXBpLm5hdmkuQU1hcE5hdmk="), it.c("UaXNOYXZpU3RhcnRlZA=="), (Object[]) null, (Class<?>[]) null)).booleanValue();
        } catch (Throwable unused) {
            return false;
        }
    }

    private AMapLocation p() {
        float f;
        float f2;
        try {
            if (j.a(this.i) && com.autonavi.aps.amapapi.utils.a.k() && o()) {
                JSONObject jSONObject = new JSONObject((String) f.a(it.c("KY29tLmFtYXAuYXBpLm5hdmkuQU1hcE5hdmk="), it.c("UZ2V0TmF2aUxvY2F0aW9u"), (Object[]) null, (Class<?>[]) null));
                long jOptLong = jSONObject.optLong("time");
                if (!this.J) {
                    this.J = true;
                    com.autonavi.aps.amapapi.utils.h.a("useNaviLoc", "use NaviLoc");
                }
                if (j.a() - jOptLong <= 5500) {
                    double dOptDouble = jSONObject.optDouble("lat", 0.0d);
                    double dOptDouble2 = jSONObject.optDouble("lng", 0.0d);
                    float f3 = 0.0f;
                    try {
                        f = Float.parseFloat(jSONObject.optString("accuracy", "0"));
                    } catch (NumberFormatException unused) {
                        f = 0.0f;
                    }
                    double dOptDouble3 = jSONObject.optDouble("altitude", 0.0d);
                    try {
                        f2 = Float.parseFloat(jSONObject.optString("bearing", "0"));
                    } catch (NumberFormatException unused2) {
                        f2 = 0.0f;
                    }
                    try {
                        f3 = (Float.parseFloat(jSONObject.optString("speed", "0")) * 10.0f) / 36.0f;
                    } catch (NumberFormatException unused3) {
                    }
                    AMapLocation aMapLocation = new AMapLocation("lbs");
                    aMapLocation.setLocationType(9);
                    aMapLocation.setLatitude(dOptDouble);
                    aMapLocation.setLongitude(dOptDouble2);
                    aMapLocation.setAccuracy(f);
                    aMapLocation.setAltitude(dOptDouble3);
                    aMapLocation.setBearing(f2);
                    aMapLocation.setSpeed(f3);
                    aMapLocation.setTime(jOptLong);
                    aMapLocation.setCoordType(AMapLocation.COORD_TYPE_GCJ02);
                    if (j.a(aMapLocation, this.i) <= 300.0f) {
                        synchronized (this.p) {
                            this.i.setLongitude(dOptDouble2);
                            this.i.setLatitude(dOptDouble);
                            this.i.setAccuracy(f);
                            this.i.setBearing(f2);
                            this.i.setSpeed(f3);
                            this.i.setTime(jOptLong);
                            this.i.setCoordType(AMapLocation.COORD_TYPE_GCJ02);
                        }
                        return aMapLocation;
                    }
                }
            }
        } catch (Throwable unused4) {
        }
        return null;
    }

    public final boolean f() {
        AMapLocationClientOption aMapLocationClientOption = this.c;
        return (aMapLocationClientOption == null || aMapLocationClientOption.isOnceLocation() || j.b() - this.d <= 300000) ? false : true;
    }

    private static void h(AMapLocation aMapLocation) {
        if (j.a(aMapLocation) && com.autonavi.aps.amapapi.utils.a.s()) {
            long time = aMapLocation.getTime();
            long jCurrentTimeMillis = System.currentTimeMillis();
            long jA = c.a(time, jCurrentTimeMillis, com.autonavi.aps.amapapi.utils.a.t());
            if (jA != time) {
                aMapLocation.setTime(jA);
                com.autonavi.aps.amapapi.utils.h.a(time, jCurrentTimeMillis);
            }
        }
    }
}
