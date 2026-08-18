package com.amap.api.col.p0003sl;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.DPoint;
import com.autonavi.aps.amapapi.utils.h;
import com.autonavi.aps.amapapi.utils.j;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.util.FileUtils;
import org.json.JSONObject;

/* JADX INFO: compiled from: GeoFenceManager.java */
/* JADX INFO: loaded from: classes2.dex */
public final class a {
    private static boolean A = false;
    Context b;
    h a = null;
    PendingIntent c = null;
    String d = null;
    GeoFenceListener e = null;
    private Object z = new Object();
    volatile int f = 1;
    ArrayList<GeoFence> g = new ArrayList<>();
    c h = null;
    Object i = new Object();
    Object j = new Object();
    HandlerC0011a k = null;
    b l = null;
    volatile boolean m = false;
    volatile boolean n = false;
    volatile boolean o = false;
    com.amap.api.col.p0003sl.b p = null;
    com.amap.api.col.p0003sl.c q = null;
    AMapLocationClient r = null;
    volatile AMapLocation s = null;
    long t = 0;
    AMapLocationClientOption u = null;
    int v = 0;
    AMapLocationListener w = new AMapLocationListener() { // from class: com.amap.api.col.3sl.a.1
        /* JADX WARN: Removed duplicated region for block: B:17:0x005e A[Catch: all -> 0x00a0, TryCatch #0 {all -> 0x00a0, blocks: (B:2:0x0000, B:5:0x0007, B:8:0x000e, B:10:0x001b, B:12:0x0025, B:17:0x005e, B:19:0x0069, B:21:0x0074, B:22:0x0086, B:24:0x0094, B:13:0x0035), top: B:27:0x0000 }] */
        /* JADX WARN: Removed duplicated region for block: B:19:0x0069 A[Catch: all -> 0x00a0, TryCatch #0 {all -> 0x00a0, blocks: (B:2:0x0000, B:5:0x0007, B:8:0x000e, B:10:0x001b, B:12:0x0025, B:17:0x005e, B:19:0x0069, B:21:0x0074, B:22:0x0086, B:24:0x0094, B:13:0x0035), top: B:27:0x0000 }] */
        @Override // com.amap.api.location.AMapLocationListener
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public final void onLocationChanged(com.amap.api.location.AMapLocation r14) {
            /*
                r13 = this;
                com.amap.api.col.3sl.a r0 = com.amap.api.col.p0003sl.a.this     // Catch: java.lang.Throwable -> La0
                boolean r0 = r0.y     // Catch: java.lang.Throwable -> La0
                if (r0 == 0) goto L7
                return
            L7:
                com.amap.api.col.3sl.a r0 = com.amap.api.col.p0003sl.a.this     // Catch: java.lang.Throwable -> La0
                boolean r0 = r0.o     // Catch: java.lang.Throwable -> La0
                if (r0 != 0) goto Le
                return
            Le:
                com.amap.api.col.3sl.a r0 = com.amap.api.col.p0003sl.a.this     // Catch: java.lang.Throwable -> La0
                r0.s = r14     // Catch: java.lang.Throwable -> La0
                r0 = 0
                r2 = 0
                r3 = 8
                r4 = 1
                r5 = 0
                if (r14 == 0) goto L5a
                int r6 = r14.getErrorCode()     // Catch: java.lang.Throwable -> La0
                int r7 = r14.getErrorCode()     // Catch: java.lang.Throwable -> La0
                if (r7 != 0) goto L35
                com.amap.api.col.3sl.a r14 = com.amap.api.col.p0003sl.a.this     // Catch: java.lang.Throwable -> La0
                long r7 = com.autonavi.aps.amapapi.utils.j.b()     // Catch: java.lang.Throwable -> La0
                r14.t = r7     // Catch: java.lang.Throwable -> La0
                com.amap.api.col.3sl.a r14 = com.amap.api.col.p0003sl.a.this     // Catch: java.lang.Throwable -> La0
                r7 = 5
                r14.a(r7, r2, r0)     // Catch: java.lang.Throwable -> La0
                r14 = r4
                goto L5c
            L35:
                java.lang.String r7 = "定位失败"
                int r8 = r14.getErrorCode()     // Catch: java.lang.Throwable -> La0
                java.lang.String r9 = r14.getErrorInfo()     // Catch: java.lang.Throwable -> La0
                java.lang.String[] r10 = new java.lang.String[r4]     // Catch: java.lang.Throwable -> La0
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> La0
                java.lang.String r12 = "locationDetail:"
                r11.<init>(r12)     // Catch: java.lang.Throwable -> La0
                java.lang.String r14 = r14.getLocationDetail()     // Catch: java.lang.Throwable -> La0
                java.lang.StringBuilder r14 = r11.append(r14)     // Catch: java.lang.Throwable -> La0
                java.lang.String r14 = r14.toString()     // Catch: java.lang.Throwable -> La0
                r10[r5] = r14     // Catch: java.lang.Throwable -> La0
                com.amap.api.col.p0003sl.a.a(r7, r8, r9, r10)     // Catch: java.lang.Throwable -> La0
                goto L5b
            L5a:
                r6 = r3
            L5b:
                r14 = r5
            L5c:
                if (r14 == 0) goto L69
                com.amap.api.col.3sl.a r14 = com.amap.api.col.p0003sl.a.this     // Catch: java.lang.Throwable -> La0
                r14.v = r5     // Catch: java.lang.Throwable -> La0
                com.amap.api.col.3sl.a r14 = com.amap.api.col.p0003sl.a.this     // Catch: java.lang.Throwable -> La0
                r3 = 6
                r14.a(r3, r2, r0)     // Catch: java.lang.Throwable -> La0
                return
            L69:
                android.os.Bundle r14 = new android.os.Bundle     // Catch: java.lang.Throwable -> La0
                r14.<init>()     // Catch: java.lang.Throwable -> La0
                com.amap.api.col.3sl.a r0 = com.amap.api.col.p0003sl.a.this     // Catch: java.lang.Throwable -> La0
                boolean r0 = r0.m     // Catch: java.lang.Throwable -> La0
                if (r0 != 0) goto L86
                com.amap.api.col.3sl.a r0 = com.amap.api.col.p0003sl.a.this     // Catch: java.lang.Throwable -> La0
                r1 = 7
                r0.b(r1)     // Catch: java.lang.Throwable -> La0
                java.lang.String r0 = "interval"
                r1 = 2000(0x7d0, double:9.88E-321)
                r14.putLong(r0, r1)     // Catch: java.lang.Throwable -> La0
                com.amap.api.col.3sl.a r0 = com.amap.api.col.p0003sl.a.this     // Catch: java.lang.Throwable -> La0
                r0.a(r3, r14, r1)     // Catch: java.lang.Throwable -> La0
            L86:
                com.amap.api.col.3sl.a r0 = com.amap.api.col.p0003sl.a.this     // Catch: java.lang.Throwable -> La0
                int r1 = r0.v     // Catch: java.lang.Throwable -> La0
                int r1 = r1 + r4
                r0.v = r1     // Catch: java.lang.Throwable -> La0
                com.amap.api.col.3sl.a r0 = com.amap.api.col.p0003sl.a.this     // Catch: java.lang.Throwable -> La0
                int r0 = r0.v     // Catch: java.lang.Throwable -> La0
                r1 = 3
                if (r0 < r1) goto La0
                java.lang.String r0 = "location_errorcode"
                r14.putInt(r0, r6)     // Catch: java.lang.Throwable -> La0
                com.amap.api.col.3sl.a r0 = com.amap.api.col.p0003sl.a.this     // Catch: java.lang.Throwable -> La0
                r1 = 1002(0x3ea, float:1.404E-42)
                r0.a(r1, r14)     // Catch: java.lang.Throwable -> La0
            La0:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.a.AnonymousClass1.onLocationChanged(com.amap.api.location.AMapLocation):void");
        }
    };
    final int x = 3;
    volatile boolean y = false;

    public a(Context context) {
        this.b = null;
        try {
            this.b = context.getApplicationContext();
            j();
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManger", "<init>");
        }
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:14:0x002b -> B:38:0x0030). Please report as a decompilation issue!!! */
    private void j() {
        if (!this.o) {
            this.o = true;
        }
        if (this.n) {
            return;
        }
        try {
            if (Looper.myLooper() == null) {
                this.h = new c(this.b.getMainLooper());
            } else {
                this.h = new c();
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManger", "init 1");
        }
        try {
            b bVar = new b("fenceActionThread");
            this.l = bVar;
            bVar.setPriority(5);
            this.l.start();
            this.k = new HandlerC0011a(this.l.getLooper());
        } catch (Throwable th2) {
            com.autonavi.aps.amapapi.utils.b.a(th2, "GeoFenceManger", "init 2");
        }
        try {
            this.p = new com.amap.api.col.p0003sl.b(this.b);
            this.q = new com.amap.api.col.p0003sl.c();
            this.u = new AMapLocationClientOption();
            AMapLocationClient aMapLocationClient = new AMapLocationClient(this.b);
            this.r = aMapLocationClient;
            aMapLocationClient.setLocationListener(this.w);
            if (this.a == null) {
                this.a = new h();
            }
        } catch (Throwable th3) {
            com.autonavi.aps.amapapi.utils.b.a(th3, "GeoFenceManger", "initBase");
        }
        this.n = true;
        try {
            String str = this.d;
            if (str != null && this.c == null) {
                a(str);
            }
        } catch (Throwable th4) {
            com.autonavi.aps.amapapi.utils.b.a(th4, "GeoFenceManger", "init 4");
        }
        if (A) {
            return;
        }
        A = true;
        h.a(this.b, "O020", (JSONObject) null);
    }

    /* JADX INFO: compiled from: GeoFenceManager.java */
    static class b extends HandlerThread {
        public b(String str) {
            super(str);
        }

        @Override // android.os.HandlerThread, java.lang.Thread, java.lang.Runnable
        public final void run() {
            try {
                super.run();
            } catch (Throwable unused) {
            }
        }
    }

    /* JADX INFO: renamed from: com.amap.api.col.3sl.a$a, reason: collision with other inner class name */
    /* JADX INFO: compiled from: GeoFenceManager.java */
    class HandlerC0011a extends Handler {
        public HandlerC0011a(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            try {
                switch (message.what) {
                    case 0:
                        a.this.b(message.getData());
                        break;
                    case 1:
                        a.this.c(message.getData());
                        break;
                    case 2:
                        a.this.e(message.getData());
                        break;
                    case 3:
                        a.this.d(message.getData());
                        break;
                    case 4:
                        a.this.f(message.getData());
                        break;
                    case 5:
                        a.this.e();
                        break;
                    case 6:
                        a aVar = a.this;
                        aVar.a(aVar.s);
                        break;
                    case 7:
                        a.this.d();
                        break;
                    case 8:
                        a.this.j(message.getData());
                        break;
                    case 9:
                        a.this.a(message.getData());
                        break;
                    case 10:
                        a.this.c();
                        break;
                    case 11:
                        a.this.h(message.getData());
                        break;
                    case 12:
                        a.this.g(message.getData());
                        break;
                    case 13:
                        a.this.g();
                        break;
                }
            } catch (Throwable unused) {
            }
        }
    }

    /* JADX INFO: compiled from: GeoFenceManager.java */
    class c extends Handler {
        public c(Looper looper) {
            super(looper);
        }

        public c() {
        }

        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            try {
                Bundle data = message.getData();
                switch (message.what) {
                    case 1000:
                        a.this.i(data);
                        return;
                    case 1001:
                        try {
                            a.this.b((GeoFence) data.getParcelable("geoFence"));
                            return;
                        } catch (Throwable th) {
                            th.printStackTrace();
                            return;
                        }
                    case 1002:
                        try {
                            a.this.c(data.getInt(GeoFence.BUNDLE_KEY_LOCERRORCODE));
                            return;
                        } catch (Throwable th2) {
                            th2.printStackTrace();
                            return;
                        }
                    default:
                        return;
                }
            } catch (Throwable unused) {
            }
        }
    }

    public final PendingIntent a(String str) {
        ArrayList<GeoFence> arrayList;
        synchronized (this.z) {
            try {
                Intent intent = new Intent(str);
                intent.setPackage(ig.c(this.b));
                if (Build.VERSION.SDK_INT >= 31 && this.b.getApplicationInfo().targetSdkVersion >= 31) {
                    this.c = PendingIntent.getBroadcast(this.b, 0, intent, 33554432);
                } else {
                    this.c = PendingIntent.getBroadcast(this.b, 0, intent, 0);
                }
                this.d = str;
                arrayList = this.g;
            } finally {
            }
            if (arrayList != null && !arrayList.isEmpty()) {
                for (GeoFence geoFence : this.g) {
                    geoFence.setPendingIntent(this.c);
                    geoFence.setPendingIntentAction(this.d);
                }
            }
        }
        return this.c;
    }

    public final void a(int i) {
        try {
            j();
            if (i > 7 || i <= 0) {
                i = 1;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("activatesAction", i);
            a(9, bundle, 0L);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "setActivateAction");
        }
    }

    final void a(Bundle bundle) {
        int i = 1;
        if (bundle != null) {
            try {
                i = bundle.getInt("activatesAction", 1);
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "doSetActivatesAction");
                return;
            }
        }
        if (this.f != i) {
            ArrayList<GeoFence> arrayList = this.g;
            if (arrayList != null && !arrayList.isEmpty()) {
                for (GeoFence geoFence : this.g) {
                    geoFence.setStatus(0);
                    geoFence.setEnterTime(-1L);
                }
            }
            n();
        }
        this.f = i;
    }

    public final void a(GeoFenceListener geoFenceListener) {
        try {
            this.e = geoFenceListener;
        } catch (Throwable unused) {
        }
    }

    public final void a(AMapLocationClientOption aMapLocationClientOption) {
        try {
            this.u = aMapLocationClientOption.m32clone();
        } catch (Throwable unused) {
        }
    }

    public final void a(DPoint dPoint, float f, String str) {
        try {
            j();
            Bundle bundle = new Bundle();
            bundle.putParcelable("centerPoint", dPoint);
            bundle.putFloat("fenceRadius", f);
            bundle.putString(GeoFence.BUNDLE_KEY_CUSTOMID, str);
            a(0, bundle, 0L);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "addRoundGeoFence");
        }
    }

    final void b(Bundle bundle) {
        String string;
        try {
            ArrayList<? extends Parcelable> arrayList = new ArrayList<>();
            String str = "";
            int iC = 1;
            if (bundle == null || bundle.isEmpty()) {
                string = str;
            } else {
                DPoint dPoint = (DPoint) bundle.getParcelable("centerPoint");
                string = bundle.getString(GeoFence.BUNDLE_KEY_CUSTOMID);
                if (dPoint == null) {
                    str = string;
                    string = str;
                } else if (dPoint.getLatitude() > 90.0d || dPoint.getLatitude() < -90.0d || dPoint.getLongitude() > 180.0d || dPoint.getLongitude() < -180.0d) {
                    a("添加围栏失败", 1, "经纬度错误，传入的纬度：" + dPoint.getLatitude() + "传入的经度:" + dPoint.getLongitude(), new String[0]);
                } else {
                    GeoFence geoFenceA = a(bundle, false);
                    iC = c(geoFenceA);
                    if (iC == 0) {
                        arrayList.add(geoFenceA);
                    }
                }
            }
            Bundle bundle2 = new Bundle();
            bundle2.putInt("errorCode", iC);
            bundle2.putParcelableArrayList("resultList", arrayList);
            bundle2.putString(GeoFence.BUNDLE_KEY_CUSTOMID, string);
            a(1000, bundle2);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "doAddGeoFenceRound");
        }
    }

    public final void a(List<DPoint> list, String str) {
        try {
            j();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("pointList", new ArrayList<>(list));
            bundle.putString(GeoFence.BUNDLE_KEY_CUSTOMID, str);
            a(1, bundle, 0L);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "addPolygonGeoFence");
        }
    }

    final void c(Bundle bundle) {
        GeoFence geoFenceA;
        try {
            ArrayList<? extends Parcelable> arrayList = new ArrayList<>();
            String str = "";
            int iC = 1;
            if (bundle != null && !bundle.isEmpty()) {
                ArrayList parcelableArrayList = bundle.getParcelableArrayList("pointList");
                String string = bundle.getString(GeoFence.BUNDLE_KEY_CUSTOMID);
                if (parcelableArrayList != null && parcelableArrayList.size() > 2 && (iC = c((geoFenceA = a(bundle, true)))) == 0) {
                    arrayList.add(geoFenceA);
                }
                str = string;
            }
            Bundle bundle2 = new Bundle();
            bundle2.putString(GeoFence.BUNDLE_KEY_CUSTOMID, str);
            bundle2.putInt("errorCode", iC);
            bundle2.putParcelableArrayList("resultList", arrayList);
            a(1000, bundle2);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "doAddGeoFencePolygon");
        }
    }

    private GeoFence a(Bundle bundle, boolean z) {
        GeoFence geoFence = new GeoFence();
        ArrayList arrayList = new ArrayList();
        DPoint dPoint = new DPoint();
        if (z) {
            geoFence.setType(1);
            arrayList = bundle.getParcelableArrayList("pointList");
            if (arrayList != null) {
                dPoint = b(arrayList);
            }
            geoFence.setMaxDis2Center(b(dPoint, arrayList));
            geoFence.setMinDis2Center(a(dPoint, arrayList));
        } else {
            geoFence.setType(0);
            dPoint = (DPoint) bundle.getParcelable("centerPoint");
            if (dPoint != null) {
                arrayList.add(dPoint);
            }
            float f = bundle.getFloat("fenceRadius", 1000.0f);
            float f2 = f > 0.0f ? f : 1000.0f;
            geoFence.setRadius(f2);
            geoFence.setMinDis2Center(f2);
            geoFence.setMaxDis2Center(f2);
        }
        geoFence.setActivatesAction(this.f);
        geoFence.setCustomId(bundle.getString(GeoFence.BUNDLE_KEY_CUSTOMID));
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(arrayList);
        geoFence.setPointList(arrayList2);
        geoFence.setCenter(dPoint);
        geoFence.setPendingIntentAction(this.d);
        geoFence.setExpiration(-1L);
        geoFence.setPendingIntent(this.c);
        geoFence.setFenceId(new StringBuilder().append(com.amap.api.col.p0003sl.c.a()).toString());
        h hVar = this.a;
        if (hVar != null) {
            hVar.a(this.b, 2);
        }
        return geoFence;
    }

    public final void a(String str, String str2, DPoint dPoint, float f, int i, String str3) {
        try {
            j();
            if (f <= 0.0f || f > 50000.0f) {
                f = 3000.0f;
            }
            if (i <= 0) {
                i = 10;
            }
            if (i > 25) {
                i = 25;
            }
            Bundle bundle = new Bundle();
            bundle.putString("keyWords", str);
            bundle.putString("poiType", str2);
            bundle.putParcelable("centerPoint", dPoint);
            bundle.putFloat("aroundRadius", f);
            bundle.putInt("searchSize", i);
            bundle.putString(GeoFence.BUNDLE_KEY_CUSTOMID, str3);
            a(3, bundle, 0L);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "addNearbyGeoFence");
        }
    }

    final void d(Bundle bundle) {
        b(2, bundle);
    }

    public final void a(String str, String str2, String str3, int i, String str4) {
        try {
            j();
            if (i <= 0) {
                i = 10;
            }
            if (i > 25) {
                i = 25;
            }
            Bundle bundle = new Bundle();
            bundle.putString("keyWords", str);
            bundle.putString("poiType", str2);
            bundle.putString("city", str3);
            bundle.putInt("searchSize", i);
            bundle.putString(GeoFence.BUNDLE_KEY_CUSTOMID, str4);
            a(2, bundle, 0L);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "addKeywordGeoFence");
        }
    }

    final void e(Bundle bundle) {
        b(1, bundle);
    }

    public final void a(String str, String str2) {
        try {
            j();
            Bundle bundle = new Bundle();
            bundle.putString("keyWords", str);
            bundle.putString(GeoFence.BUNDLE_KEY_CUSTOMID, str2);
            a(4, bundle, 0L);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "addDistricetGeoFence");
        }
    }

    final void f(Bundle bundle) {
        b(3, bundle);
    }

    private static boolean a(int i, String str, String str2, DPoint dPoint) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (i != 1) {
            if (i == 2) {
                if (dPoint == null) {
                    return false;
                }
                if (dPoint.getLatitude() > 90.0d || dPoint.getLatitude() < -90.0d || dPoint.getLongitude() > 180.0d || dPoint.getLongitude() < -180.0d) {
                    a("添加围栏失败", 0, "经纬度错误，传入的纬度：" + dPoint.getLatitude() + "传入的经度:" + dPoint.getLongitude(), new String[0]);
                    return false;
                }
            }
        } else if (TextUtils.isEmpty(str2)) {
            return false;
        }
        return true;
    }

    private void b(int i, Bundle bundle) {
        String str;
        int iA;
        String str2;
        int i2;
        String str3;
        String strA;
        int iD;
        Bundle bundle2 = new Bundle();
        try {
            ArrayList<? extends Parcelable> arrayList = new ArrayList<>();
            if (bundle == null || bundle.isEmpty()) {
                str2 = "errorCode";
                i2 = 1;
            } else {
                List<GeoFence> arrayList2 = new ArrayList<>();
                String string = bundle.getString(GeoFence.BUNDLE_KEY_CUSTOMID);
                String string2 = bundle.getString("keyWords");
                String string3 = bundle.getString("city");
                String string4 = bundle.getString("poiType");
                DPoint dPoint = (DPoint) bundle.getParcelable("centerPoint");
                int i3 = bundle.getInt("searchSize", 10);
                float f = bundle.getFloat("aroundRadius", 3000.0f);
                if (a(i, string2, string4, dPoint)) {
                    Bundle bundle3 = new Bundle();
                    bundle3.putString(GeoFence.BUNDLE_KEY_CUSTOMID, string);
                    bundle3.putString("pendingIntentAction", this.d);
                    str3 = GeoFence.BUNDLE_KEY_CUSTOMID;
                    str2 = "errorCode";
                    try {
                        bundle3.putLong("expiration", -1L);
                        bundle3.putInt("activatesAction", this.f);
                        if (i == 1) {
                            bundle3.putFloat("fenceRadius", 1000.0f);
                            strA = this.p.a(this.b, "http://restsdk.amap.com/v3/place/text?", string2, string4, string3, String.valueOf(i3));
                        } else if (i == 2) {
                            double dB = j.b(dPoint.getLatitude());
                            double dB2 = j.b(dPoint.getLongitude());
                            int iIntValue = Float.valueOf(f).intValue();
                            bundle3.putFloat("fenceRadius", 200.0f);
                            strA = this.p.a(this.b, "http://restsdk.amap.com/v3/place/around?", string2, string4, String.valueOf(i3), String.valueOf(dB), String.valueOf(dB2), String.valueOf(iIntValue));
                        } else {
                            strA = i != 3 ? null : this.p.a(this.b, "http://restsdk.amap.com/v3/config/district?", string2);
                        }
                        if (strA != null) {
                            int iA2 = 1 == i ? com.amap.api.col.p0003sl.c.a(strA, arrayList2, bundle3) : 0;
                            if (2 == i) {
                                iA2 = com.amap.api.col.p0003sl.c.b(strA, arrayList2, bundle3);
                            }
                            if (3 == i) {
                                iA2 = this.q.c(strA, arrayList2, bundle3);
                            }
                            if (iA2 == 10000) {
                                if (arrayList2.isEmpty()) {
                                    iD = 16;
                                } else {
                                    iA = a(arrayList2);
                                    if (iA == 0) {
                                        try {
                                            arrayList.addAll(arrayList2);
                                        } catch (Throwable th) {
                                            th = th;
                                            str = str2;
                                            try {
                                                com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "doAddGeoFenceNearby");
                                                bundle2.putInt(str, 8);
                                                a(1000, bundle2);
                                                return;
                                            } catch (Throwable th2) {
                                                bundle2.putInt(str, iA);
                                                a(1000, bundle2);
                                                throw th2;
                                            }
                                        }
                                    }
                                }
                            } else {
                                iD = d(iA2);
                            }
                            iA = iD;
                        } else {
                            iA = 4;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        str = str2;
                        iA = 0;
                        com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "doAddGeoFenceNearby");
                        bundle2.putInt(str, 8);
                        a(1000, bundle2);
                        return;
                    }
                } else {
                    str3 = GeoFence.BUNDLE_KEY_CUSTOMID;
                    str2 = "errorCode";
                    iA = 1;
                }
                bundle2.putString(str3, string);
                bundle2.putParcelableArrayList("resultList", arrayList);
                i2 = iA;
            }
            bundle2.putInt(str2, i2);
            a(1000, bundle2);
        } catch (Throwable th4) {
            th = th4;
            str = "errorCode";
        }
    }

    public final void a() {
        try {
            this.o = false;
            a(10, (Bundle) null, 0L);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "removeGeoFence");
        }
    }

    public final boolean a(GeoFence geoFence) {
        try {
            ArrayList<GeoFence> arrayList = this.g;
            if (arrayList != null && !arrayList.isEmpty()) {
                if (!this.g.contains(geoFence)) {
                    return false;
                }
                if (this.g.size() == 1) {
                    this.o = false;
                }
                Bundle bundle = new Bundle();
                bundle.putParcelable("fc", geoFence);
                a(11, bundle, 0L);
                return true;
            }
            this.o = false;
            a(10, (Bundle) null, 0L);
            return true;
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "removeGeoFence(GeoFence)");
            return false;
        }
    }

    public final void a(String str, boolean z) {
        try {
            j();
            Bundle bundle = new Bundle();
            bundle.putString("fid", str);
            bundle.putBoolean("ab", z);
            a(12, bundle, 0L);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "setGeoFenceAble");
        }
    }

    final void g(Bundle bundle) {
        if (bundle != null) {
            try {
                if (bundle.isEmpty()) {
                    return;
                }
                String string = bundle.getString("fid");
                if (TextUtils.isEmpty(string)) {
                    return;
                }
                boolean z = bundle.getBoolean("ab", true);
                ArrayList<GeoFence> arrayList = this.g;
                if (arrayList != null && !arrayList.isEmpty()) {
                    Iterator<GeoFence> it = this.g.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        GeoFence next = it.next();
                        if (next.getFenceId().equals(string)) {
                            next.setAble(z);
                            break;
                        }
                    }
                }
                if (!z) {
                    if (k()) {
                        g();
                        return;
                    }
                    return;
                }
                n();
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "doSetGeoFenceAble");
            }
        }
    }

    private boolean k() {
        ArrayList<GeoFence> arrayList = this.g;
        if (arrayList == null || arrayList.isEmpty()) {
            return true;
        }
        Iterator<GeoFence> it = this.g.iterator();
        while (it.hasNext()) {
            if (it.next().isAble()) {
                return false;
            }
        }
        return true;
    }

    public final List<GeoFence> b() {
        try {
            if (this.g == null) {
                this.g = new ArrayList<>();
            }
            return (ArrayList) this.g.clone();
        } catch (Throwable unused) {
            return new ArrayList();
        }
    }

    final void h(Bundle bundle) {
        try {
            if (this.g != null) {
                GeoFence geoFence = (GeoFence) bundle.getParcelable("fc");
                if (this.g.contains(geoFence)) {
                    this.g.remove(geoFence);
                }
                if (this.g.size() <= 0) {
                    c();
                } else {
                    n();
                }
            }
        } catch (Throwable unused) {
        }
    }

    final void c() {
        try {
            if (!this.n) {
                return;
            }
            ArrayList<GeoFence> arrayList = this.g;
            if (arrayList != null) {
                arrayList.clear();
                this.g = null;
            }
            if (this.o) {
                return;
            }
            m();
            AMapLocationClient aMapLocationClient = this.r;
            if (aMapLocationClient != null) {
                aMapLocationClient.stopLocation();
                this.r.onDestroy();
            }
            this.r = null;
            if (this.l != null) {
                if (Build.VERSION.SDK_INT >= 18) {
                    this.l.quitSafely();
                } else {
                    this.l.quit();
                }
            }
            this.l = null;
            this.p = null;
            synchronized (this.z) {
                PendingIntent pendingIntent = this.c;
                if (pendingIntent != null) {
                    pendingIntent.cancel();
                }
                this.c = null;
            }
            l();
            h hVar = this.a;
            if (hVar != null) {
                hVar.b(this.b);
            }
        } catch (Throwable unused) {
        }
        this.m = false;
        this.n = false;
    }

    private void l() {
        try {
            synchronized (this.j) {
                c cVar = this.h;
                if (cVar != null) {
                    cVar.removeCallbacksAndMessages(null);
                }
                this.h = null;
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "destroyResultHandler");
        }
    }

    private int c(GeoFence geoFence) {
        try {
            if (this.g == null) {
                this.g = new ArrayList<>();
            }
            if (this.g.contains(geoFence)) {
                return 17;
            }
            this.g.add(geoFence);
            return 0;
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "addGeoFence2List");
            a("添加围栏失败", 8, th.getMessage(), new String[0]);
            return 8;
        }
    }

    private int a(List<GeoFence> list) {
        try {
            if (this.g == null) {
                this.g = new ArrayList<>();
            }
            Iterator<GeoFence> it = list.iterator();
            while (it.hasNext()) {
                c(it.next());
            }
            return 0;
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "addGeoFenceList");
            a("添加围栏失败", 8, th.getMessage(), new String[0]);
            return 8;
        }
    }

    final void a(int i, Bundle bundle, long j) {
        try {
            synchronized (this.i) {
                HandlerC0011a handlerC0011a = this.k;
                if (handlerC0011a != null) {
                    Message messageObtainMessage = handlerC0011a.obtainMessage();
                    messageObtainMessage.what = i;
                    messageObtainMessage.setData(bundle);
                    this.k.sendMessageDelayed(messageObtainMessage, j);
                }
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "sendActionHandlerMessage");
        }
    }

    final void b(int i) {
        try {
            synchronized (this.i) {
                HandlerC0011a handlerC0011a = this.k;
                if (handlerC0011a != null) {
                    handlerC0011a.removeMessages(i);
                }
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "removeActionHandlerMessage");
        }
    }

    private void m() {
        try {
            synchronized (this.i) {
                HandlerC0011a handlerC0011a = this.k;
                if (handlerC0011a != null) {
                    handlerC0011a.removeCallbacksAndMessages(null);
                }
                this.k = null;
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "destroyActionHandler");
        }
    }

    final void a(int i, Bundle bundle) {
        try {
            synchronized (this.j) {
                c cVar = this.h;
                if (cVar != null) {
                    Message messageObtainMessage = cVar.obtainMessage();
                    messageObtainMessage.what = i;
                    messageObtainMessage.setData(bundle);
                    this.h.sendMessage(messageObtainMessage);
                }
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "sendResultHandlerMessage");
        }
    }

    final void i(Bundle bundle) {
        if (bundle != null) {
            try {
                if (bundle.isEmpty()) {
                    return;
                }
                int i = bundle.getInt("errorCode");
                ArrayList parcelableArrayList = bundle.getParcelableArrayList("resultList");
                if (parcelableArrayList == null) {
                    parcelableArrayList = new ArrayList();
                }
                String string = bundle.getString(GeoFence.BUNDLE_KEY_CUSTOMID);
                if (string == null) {
                    string = "";
                }
                GeoFenceListener geoFenceListener = this.e;
                if (geoFenceListener != null) {
                    geoFenceListener.onGeoFenceCreateFinished((ArrayList) parcelableArrayList.clone(), i, string);
                }
                if (i == 0) {
                    n();
                }
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "resultAddGeoFenceFinished");
            }
        }
    }

    private void n() {
        if (this.y || this.k == null) {
            return;
        }
        if (p()) {
            a(6, (Bundle) null, 0L);
            a(5, (Bundle) null, 0L);
        } else {
            b(7);
            a(7, (Bundle) null, 0L);
        }
    }

    private static Bundle a(GeoFence geoFence, String str, String str2, int i, int i2) {
        Bundle bundle = new Bundle();
        if (str == null) {
            str = "";
        }
        bundle.putString(GeoFence.BUNDLE_KEY_FENCEID, str);
        bundle.putString(GeoFence.BUNDLE_KEY_CUSTOMID, str2);
        bundle.putInt("event", i);
        bundle.putInt(GeoFence.BUNDLE_KEY_LOCERRORCODE, i2);
        bundle.putParcelable(GeoFence.BUNDLE_KEY_FENCE, geoFence);
        return bundle;
    }

    final void c(int i) {
        try {
            if (this.b != null) {
                synchronized (this.z) {
                    if (this.c == null) {
                        return;
                    }
                    Intent intent = new Intent();
                    intent.putExtras(a((GeoFence) null, (String) null, (String) null, 4, i));
                    this.c.send(this.b, 0, intent);
                }
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "resultRemindLocationError");
        }
    }

    final void b(GeoFence geoFence) {
        try {
            synchronized (this.z) {
                if (this.b != null) {
                    if (this.c == null && geoFence.getPendingIntent() == null) {
                        return;
                    }
                    Intent intent = new Intent();
                    intent.putExtras(a(geoFence, geoFence.getFenceId(), geoFence.getCustomId(), geoFence.getStatus(), 0));
                    String str = this.d;
                    if (str != null) {
                        intent.setAction(str);
                    }
                    intent.setPackage(ig.c(this.b));
                    if (geoFence.getPendingIntent() != null) {
                        geoFence.getPendingIntent().send(this.b, 0, intent);
                    } else {
                        this.c.send(this.b, 0, intent);
                    }
                }
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "resultTriggerGeoFence");
        }
    }

    final void a(AMapLocation aMapLocation) {
        ArrayList<GeoFence> arrayList;
        try {
            if (this.y || (arrayList = this.g) == null || arrayList.isEmpty() || aMapLocation == null || aMapLocation.getErrorCode() != 0) {
                return;
            }
            for (GeoFence geoFence : this.g) {
                if (geoFence.isAble() && b(aMapLocation, geoFence) && a(geoFence, this.f)) {
                    geoFence.setCurrentLocation(aMapLocation);
                    d(geoFence);
                }
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "doCheckFence");
        }
    }

    private void d(GeoFence geoFence) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("geoFence", geoFence);
        a(1001, bundle);
    }

    final void d() {
        try {
            if (this.r != null) {
                o();
                this.u.setLocationCacheEnable(true);
                this.u.setNeedAddress(false);
                this.u.setOnceLocation(true);
                this.r.setLocationOption(this.u);
                this.r.startLocation();
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "doStartOnceLocation");
        }
    }

    private void o() {
        try {
            if (this.m) {
                b(8);
            }
            AMapLocationClient aMapLocationClient = this.r;
            if (aMapLocationClient != null) {
                aMapLocationClient.stopLocation();
            }
            this.m = false;
        } catch (Throwable unused) {
        }
    }

    final void j(Bundle bundle) {
        try {
            if (this.r != null) {
                long j = FileUtils.FAT_FILE_TIMESTAMP_GRANULARITY;
                if (bundle != null && !bundle.isEmpty()) {
                    j = bundle.getLong("interval", FileUtils.FAT_FILE_TIMESTAMP_GRANULARITY);
                }
                this.u.setOnceLocation(false);
                this.u.setInterval(j);
                this.u.setLocationCacheEnable(true);
                this.u.setNeedAddress(false);
                this.r.setLocationOption(this.u);
                if (this.m) {
                    return;
                }
                this.r.stopLocation();
                this.r.startLocation();
                this.m = true;
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "doStartContinueLocation");
        }
    }

    private boolean p() {
        return this.s != null && j.a(this.s) && j.b() - this.t < 10000;
    }

    final void e() {
        try {
            if (!this.y && j.a(this.s)) {
                float fA = a(this.s, this.g);
                if (fA == Float.MAX_VALUE) {
                    return;
                }
                if (fA < 1000.0f) {
                    b(7);
                    Bundle bundle = new Bundle();
                    bundle.putLong("interval", FileUtils.FAT_FILE_TIMESTAMP_GRANULARITY);
                    a(8, bundle, 500L);
                    return;
                }
                if (fA < 5000.0f) {
                    o();
                    b(7);
                    a(7, (Bundle) null, 10000L);
                } else {
                    o();
                    b(7);
                    a(7, (Bundle) null, (long) (((fA - 4000.0f) / 100.0f) * 1000.0f));
                }
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "doCheckLocationPolicy");
        }
    }

    private static DPoint b(List<DPoint> list) {
        DPoint dPoint = new DPoint();
        if (list == null) {
            return dPoint;
        }
        try {
            double latitude = 0.0d;
            double longitude = 0.0d;
            for (DPoint dPoint2 : list) {
                latitude += dPoint2.getLatitude();
                longitude += dPoint2.getLongitude();
            }
            return new DPoint(j.b(latitude / ((double) list.size())), j.b(longitude / ((double) list.size())));
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceUtil", "getPolygonCenter");
            return dPoint;
        }
    }

    private static float a(AMapLocation aMapLocation, List<GeoFence> list) {
        float fMin = Float.MAX_VALUE;
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0 && list != null && !list.isEmpty()) {
            DPoint dPoint = new DPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            for (GeoFence geoFence : list) {
                if (geoFence.isAble()) {
                    float fA = j.a(dPoint, geoFence.getCenter());
                    if (fA > geoFence.getMinDis2Center() && fA < geoFence.getMaxDis2Center()) {
                        return 0.0f;
                    }
                    if (fA > geoFence.getMaxDis2Center()) {
                        fMin = Math.min(fMin, fA - geoFence.getMaxDis2Center());
                    }
                    if (fA < geoFence.getMinDis2Center()) {
                        fMin = Math.min(fMin, geoFence.getMinDis2Center() - fA);
                    }
                }
            }
        }
        return fMin;
    }

    static float a(DPoint dPoint, List<DPoint> list) {
        float fMin = Float.MAX_VALUE;
        if (dPoint != null && list != null && !list.isEmpty()) {
            Iterator<DPoint> it = list.iterator();
            while (it.hasNext()) {
                fMin = Math.min(fMin, j.a(dPoint, it.next()));
            }
        }
        return fMin;
    }

    static float b(DPoint dPoint, List<DPoint> list) {
        float fMax = Float.MIN_VALUE;
        if (dPoint != null && list != null && !list.isEmpty()) {
            Iterator<DPoint> it = list.iterator();
            while (it.hasNext()) {
                fMax = Math.max(fMax, j.a(dPoint, it.next()));
            }
        }
        return fMax;
    }

    private static boolean a(AMapLocation aMapLocation, DPoint dPoint, float f) {
        return j.a(new double[]{dPoint.getLatitude(), dPoint.getLongitude(), aMapLocation.getLatitude(), aMapLocation.getLongitude()}) <= f;
    }

    private static boolean b(AMapLocation aMapLocation, List<DPoint> list) {
        if (list.size() < 3) {
            return false;
        }
        return com.autonavi.aps.amapapi.utils.b.a(new DPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude()), list);
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0029 A[Catch: all -> 0x0056, TryCatch #0 {all -> 0x0056, blocks: (B:3:0x0002, B:6:0x000a, B:8:0x0010, B:10:0x001a, B:18:0x0029, B:19:0x0031, B:21:0x0037, B:24:0x0045), top: B:31:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0045 A[Catch: all -> 0x0056, TRY_LEAVE, TryCatch #0 {all -> 0x0056, blocks: (B:3:0x0002, B:6:0x000a, B:8:0x0010, B:10:0x001a, B:18:0x0029, B:19:0x0031, B:21:0x0037, B:24:0x0045), top: B:31:0x0002 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean a(com.amap.api.location.AMapLocation r4, com.amap.api.fence.GeoFence r5) {
        /*
            r0 = 1
            r1 = 0
            boolean r2 = com.autonavi.aps.amapapi.utils.j.a(r4)     // Catch: java.lang.Throwable -> L56
            if (r2 == 0) goto L54
            if (r5 == 0) goto L54
            java.util.List r2 = r5.getPointList()     // Catch: java.lang.Throwable -> L56
            if (r2 == 0) goto L54
            java.util.List r2 = r5.getPointList()     // Catch: java.lang.Throwable -> L56
            boolean r2 = r2.isEmpty()     // Catch: java.lang.Throwable -> L56
            if (r2 != 0) goto L54
            int r2 = r5.getType()     // Catch: java.lang.Throwable -> L56
            if (r2 == 0) goto L45
            if (r2 == r0) goto L29
            r3 = 2
            if (r2 == r3) goto L45
            r3 = 3
            if (r2 == r3) goto L29
            goto L54
        L29:
            java.util.List r5 = r5.getPointList()     // Catch: java.lang.Throwable -> L56
            java.util.Iterator r5 = r5.iterator()     // Catch: java.lang.Throwable -> L56
        L31:
            boolean r2 = r5.hasNext()     // Catch: java.lang.Throwable -> L56
            if (r2 == 0) goto L54
            java.lang.Object r2 = r5.next()     // Catch: java.lang.Throwable -> L56
            java.util.List r2 = (java.util.List) r2     // Catch: java.lang.Throwable -> L56
            boolean r2 = b(r4, r2)     // Catch: java.lang.Throwable -> L56
            if (r2 == 0) goto L31
            r1 = r0
            goto L31
        L45:
            com.amap.api.location.DPoint r2 = r5.getCenter()     // Catch: java.lang.Throwable -> L56
            float r5 = r5.getRadius()     // Catch: java.lang.Throwable -> L56
            boolean r4 = a(r4, r2, r5)     // Catch: java.lang.Throwable -> L56
            if (r4 == 0) goto L54
            goto L5f
        L54:
            r0 = r1
            goto L5f
        L56:
            r4 = move-exception
            r0 = r1
            java.lang.String r5 = "Utils"
            java.lang.String r1 = "isInGeoFence"
            com.autonavi.aps.amapapi.utils.b.a(r4, r5, r1)
        L5f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.a.a(com.amap.api.location.AMapLocation, com.amap.api.fence.GeoFence):boolean");
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x004e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean b(com.amap.api.location.AMapLocation r7, com.amap.api.fence.GeoFence r8) {
        /*
            r0 = 1
            r1 = 0
            boolean r7 = a(r7, r8)     // Catch: java.lang.Throwable -> L50
            r2 = -1
            if (r7 == 0) goto L3e
            long r4 = r8.getEnterTime()     // Catch: java.lang.Throwable -> L50
            int r7 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r7 != 0) goto L23
            int r7 = r8.getStatus()     // Catch: java.lang.Throwable -> L50
            if (r7 == r0) goto L4e
            long r2 = com.autonavi.aps.amapapi.utils.j.b()     // Catch: java.lang.Throwable -> L50
            r8.setEnterTime(r2)     // Catch: java.lang.Throwable -> L50
            r8.setStatus(r0)     // Catch: java.lang.Throwable -> L50
            goto L59
        L23:
            int r7 = r8.getStatus()     // Catch: java.lang.Throwable -> L50
            r2 = 3
            if (r7 == r2) goto L4e
            long r3 = com.autonavi.aps.amapapi.utils.j.b()     // Catch: java.lang.Throwable -> L50
            long r5 = r8.getEnterTime()     // Catch: java.lang.Throwable -> L50
            long r3 = r3 - r5
            r5 = 600000(0x927c0, double:2.964394E-318)
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 <= 0) goto L4e
            r8.setStatus(r2)     // Catch: java.lang.Throwable -> L50
            goto L59
        L3e:
            int r7 = r8.getStatus()     // Catch: java.lang.Throwable -> L50
            r4 = 2
            if (r7 == r4) goto L4e
            r8.setStatus(r4)     // Catch: java.lang.Throwable -> L4c
            r8.setEnterTime(r2)     // Catch: java.lang.Throwable -> L4c
            goto L59
        L4c:
            r7 = move-exception
            goto L52
        L4e:
            r0 = r1
            goto L59
        L50:
            r7 = move-exception
            r0 = r1
        L52:
            java.lang.String r8 = "Utils"
            java.lang.String r1 = "isFenceStatusChanged"
            com.autonavi.aps.amapapi.utils.b.a(r7, r8, r1)
        L59:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.a.b(com.amap.api.location.AMapLocation, com.amap.api.fence.GeoFence):boolean");
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x0030  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean a(com.amap.api.fence.GeoFence r4, int r5) {
        /*
            r0 = r5 & 1
            r1 = 1
            r2 = 0
            if (r0 != r1) goto L10
            int r0 = r4.getStatus()     // Catch: java.lang.Throwable -> Le
            if (r0 != r1) goto L10
            r2 = r1
            goto L10
        Le:
            r4 = move-exception
            goto L28
        L10:
            r0 = r5 & 2
            r3 = 2
            if (r0 != r3) goto L1c
            int r0 = r4.getStatus()     // Catch: java.lang.Throwable -> Le
            if (r0 != r3) goto L1c
            r2 = r1
        L1c:
            r0 = 4
            r5 = r5 & r0
            if (r5 != r0) goto L30
            int r4 = r4.getStatus()     // Catch: java.lang.Throwable -> Le
            r5 = 3
            if (r4 != r5) goto L30
            goto L31
        L28:
            java.lang.String r5 = "Utils"
            java.lang.String r0 = "remindStatus"
            com.autonavi.aps.amapapi.utils.b.a(r4, r5, r0)
            goto L32
        L30:
            r1 = r2
        L31:
            r2 = r1
        L32:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.a.a(com.amap.api.fence.GeoFence, int):boolean");
    }

    private static int d(int i) {
        if (i != 1 && i != 7 && i != 4 && i != 5 && i != 16 && i != 17) {
            switch (i) {
                case 10000:
                    i = 0;
                    break;
                case 10001:
                case 10002:
                case KeyEvent.KEYCODE_LOOP /* 10007 */:
                case KeyEvent.KEYCODE_EXPAND /* 10008 */:
                case KeyEvent.KEYCODE_MOUSE /* 10009 */:
                case KeyEvent.KEYCODE_BROWSER /* 10012 */:
                case KeyEvent.KEYCODE_SCREENSHOT /* 10013 */:
                    i = 7;
                    break;
                case 10003:
                case KeyEvent.KEYCODE_ZOOM /* 10004 */:
                case KeyEvent.KEYCODE_HELP /* 10005 */:
                case KeyEvent.KEYCODE_FAVOURITE /* 10006 */:
                case KeyEvent.KEYCODE_MOVIE /* 10010 */:
                case KeyEvent.KEYCODE_APPS /* 10011 */:
                case KeyEvent.KEYCODE_SPEECH_RECOGNITION /* 10014 */:
                case KeyEvent.KEYCODE_FILE_LOCK /* 10015 */:
                case 10016:
                case 10017:
                    i = 4;
                    break;
                default:
                    switch (i) {
                        case 20000:
                        case 20001:
                        case 20002:
                            i = 1;
                            break;
                        case 20003:
                        default:
                            i = 8;
                            break;
                    }
                    break;
            }
        }
        if (i != 0) {
            a("添加围栏失败", i, "searchErrCode is ".concat(String.valueOf(i)), new String[0]);
        }
        return i;
    }

    static void a(String str, int i, String str2, String... strArr) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("===========================================\n");
        stringBuffer.append("              " + str + "                ").append("\n");
        stringBuffer.append("-------------------------------------------\n");
        stringBuffer.append("errorCode:".concat(String.valueOf(i))).append("\n");
        stringBuffer.append("错误信息:".concat(String.valueOf(str2))).append("\n");
        if (strArr.length > 0) {
            for (String str3 : strArr) {
                stringBuffer.append(str3).append("\n");
            }
        }
        stringBuffer.append("===========================================\n");
        Log.i("fenceErrLog", stringBuffer.toString());
    }

    public final void f() {
        try {
            j();
            this.y = true;
            a(13, (Bundle) null, 0L);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "pauseGeoFence");
        }
    }

    final void g() {
        try {
            b(7);
            b(8);
            AMapLocationClient aMapLocationClient = this.r;
            if (aMapLocationClient != null) {
                aMapLocationClient.stopLocation();
            }
            this.m = false;
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "doPauseGeoFence");
        }
    }

    public final void h() {
        try {
            j();
            if (this.y) {
                this.y = false;
                n();
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "GeoFenceManager", "resumeGeoFence");
        }
    }

    public final boolean i() {
        return this.y;
    }
}
