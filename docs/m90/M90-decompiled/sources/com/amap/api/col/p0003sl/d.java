package com.amap.api.col.p0003sl;

import android.app.Application;
import android.app.Notification;
import android.app.backup.FullBackup;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import androidx.core.view.PointerIconCompat;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.amap.api.location.APSService;
import com.amap.api.location.UmidtokenInfo;
import com.autonavi.aps.amapapi.utils.f;
import com.autonavi.aps.amapapi.utils.g;
import com.autonavi.aps.amapapi.utils.h;
import com.autonavi.aps.amapapi.utils.j;
import com.unisound.client.SpeechConstants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: compiled from: AmapLocationManager.java */
/* JADX INFO: loaded from: classes2.dex */
public final class d {
    private static boolean H = true;
    private static boolean J = false;
    private static AtomicBoolean K = new AtomicBoolean(false);
    public static volatile boolean g = false;
    private Context D;
    private g E;
    com.autonavi.aps.amapapi.model.a a;
    public c c;
    j k;
    Intent n;
    AMapLocationClientOption b = new AMapLocationClientOption();
    h d = null;
    private boolean F = false;
    private volatile boolean G = false;
    ArrayList<AMapLocationListener> e = new ArrayList<>();
    boolean f = false;
    public boolean h = true;
    public boolean i = true;
    public boolean j = true;
    Messenger l = null;
    Messenger m = null;
    int o = 0;
    private boolean I = true;
    b p = null;
    boolean q = false;
    AMapLocationClientOption.AMapLocationMode r = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy;
    Object s = new Object();
    h t = null;
    boolean u = false;
    e v = null;
    private AMapLocationClientOption L = new AMapLocationClientOption();
    private i M = null;
    String w = null;
    private ServiceConnection N = new ServiceConnection() { // from class: com.amap.api.col.3sl.d.2
        @Override // android.content.ServiceConnection
        public final void onServiceDisconnected(ComponentName componentName) {
            d.this.l = null;
            d.this.F = false;
        }

        @Override // android.content.ServiceConnection
        public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                d.this.l = new Messenger(iBinder);
                d.this.F = true;
                d.this.u = true;
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "onServiceConnected");
            }
        }
    };
    AMapLocationQualityReport x = null;
    boolean y = false;
    boolean z = false;
    private volatile boolean O = false;
    a A = null;
    String B = null;
    boolean C = false;

    public d(Context context, Intent intent, Looper looper) {
        this.n = null;
        this.D = context;
        this.n = intent;
        b(looper);
    }

    public final boolean a() {
        return this.F;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(int i, Object obj, long j) {
        synchronized (this.s) {
            if (this.A != null) {
                Message messageObtain = Message.obtain();
                messageObtain.what = i;
                if (obj instanceof Bundle) {
                    messageObtain.setData((Bundle) obj);
                } else {
                    messageObtain.obj = obj;
                }
                this.A.sendMessageDelayed(messageObtain, j);
            }
        }
    }

    private void h() {
        synchronized (this.s) {
            a aVar = this.A;
            if (aVar != null) {
                aVar.removeCallbacksAndMessages(null);
            }
            this.A = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(int i) {
        synchronized (this.s) {
            a aVar = this.A;
            if (aVar != null) {
                aVar.removeMessages(i);
            }
        }
    }

    private a a(Looper looper) {
        a aVar;
        synchronized (this.s) {
            aVar = new a(looper);
            this.A = aVar;
        }
        return aVar;
    }

    public final void a(AMapLocationClientOption aMapLocationClientOption) {
        try {
            this.L = aMapLocationClientOption.m32clone();
            a(PointerIconCompat.TYPE_ZOOM_IN, aMapLocationClientOption.m32clone(), 0L);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "setLocationOption");
        }
    }

    public final void a(AMapLocationListener aMapLocationListener) {
        try {
            a(1002, aMapLocationListener, 0L);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "setLocationListener");
        }
    }

    public final void b(AMapLocationListener aMapLocationListener) {
        try {
            a(1005, aMapLocationListener, 0L);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "unRegisterLocationListener");
        }
    }

    public final void b() {
        c cVar;
        try {
            if (this.L.getCacheCallBack() && (cVar = this.c) != null) {
                cVar.sendEmptyMessageDelayed(13, this.L.getCacheCallBackTime());
            }
        } catch (Throwable unused) {
        }
        try {
            a(1003, (Object) null, 0L);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "startLocation");
        }
    }

    public final void c() {
        try {
            a(1004, (Object) null, 0L);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "stopLocation");
        }
    }

    public final void d() {
        try {
            i iVar = this.M;
            if (iVar != null) {
                iVar.b();
                this.M = null;
            }
            a(1011, (Object) null, 0L);
            this.q = true;
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "onDestroy");
        }
    }

    public final AMapLocation e() {
        AMapLocation aMapLocationB = null;
        try {
            j jVar = this.k;
            if (jVar != null && (aMapLocationB = jVar.b()) != null) {
                aMapLocationB.setTrustedLevel(3);
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "getLastKnownLocation");
        }
        return aMapLocationB;
    }

    public final void a(WebView webView) {
        if (this.M == null) {
            this.M = new i(this.D, webView);
        }
        this.M.a();
    }

    public final void f() {
        try {
            i iVar = this.M;
            if (iVar != null) {
                iVar.b();
                this.M = null;
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "stopAssistantLocation");
        }
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:11:0x0029 -> B:29:0x002e). Please report as a decompilation issue!!! */
    private void b(Looper looper) {
        try {
            if (looper == null) {
                if (Looper.myLooper() == null) {
                    this.c = new c(this.D.getMainLooper());
                } else {
                    this.c = new c();
                }
            } else {
                this.c = new c(looper);
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "init 1");
        }
        try {
            try {
                this.k = new j(this.D);
            } catch (Throwable th2) {
                com.autonavi.aps.amapapi.utils.b.a(th2, "ALManager", "init 2");
            }
            b bVar = new b("amapLocManagerThread", this);
            this.p = bVar;
            bVar.setPriority(5);
            this.p.start();
            this.A = a(this.p.getLooper());
        } catch (Throwable th3) {
            com.autonavi.aps.amapapi.utils.b.a(th3, "ALManager", "init 5");
        }
        try {
            this.d = new h(this.D, this.c);
            this.E = new g(this.D, this.c);
        } catch (Throwable th4) {
            com.autonavi.aps.amapapi.utils.b.a(th4, "ALManager", "init 3");
        }
        if (this.t == null) {
            this.t = new h();
        }
        a(this.D);
    }

    private static void a(final Context context) {
        if (K.compareAndSet(false, true)) {
            mc.a().a(new md() { // from class: com.amap.api.col.3sl.d.1
                @Override // com.amap.api.col.p0003sl.md
                public final void runTask() {
                    ik.l();
                    ik.a(context);
                    ik.f(context);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(int i, Bundle bundle) {
        if (bundle == null) {
            try {
                bundle = new Bundle();
            } catch (Throwable th) {
                boolean z = (th instanceof IllegalStateException) && th.getMessage().contains("sending message to a Handler on a dead thread");
                if ((th instanceof RemoteException) || z) {
                    this.l = null;
                    this.F = false;
                }
                com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "sendLocMessage");
                return;
            }
        }
        if (TextUtils.isEmpty(this.w)) {
            this.w = com.autonavi.aps.amapapi.utils.b.b(this.D);
        }
        bundle.putString(FullBackup.CACHE_TREE_TOKEN, this.w);
        Message messageObtain = Message.obtain();
        messageObtain.what = i;
        messageObtain.setData(bundle);
        messageObtain.replyTo = this.m;
        Messenger messenger = this.l;
        if (messenger != null) {
            messenger.send(messageObtain);
        }
    }

    private boolean i() {
        boolean z = false;
        int i = 0;
        while (this.l == null) {
            try {
                Thread.sleep(100L);
                i++;
                if (i >= 50) {
                    break;
                }
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "checkAPSManager");
            }
        }
        if (this.l == null) {
            Message messageObtain = Message.obtain();
            Bundle bundle = new Bundle();
            AMapLocation aMapLocation = new AMapLocation("");
            aMapLocation.setErrorCode(10);
            if (!j.k(this.D.getApplicationContext())) {
                aMapLocation.setLocationDetail("请检查配置文件是否配置服务，并且manifest中service标签是否配置在application标签内#1003");
            } else {
                aMapLocation.setLocationDetail("启动ApsServcie失败#1001");
            }
            bundle.putParcelable("loc", aMapLocation);
            messageObtain.setData(bundle);
            messageObtain.what = 1;
            this.c.sendMessage(messageObtain);
        } else {
            z = true;
        }
        if (!z) {
            if (!j.k(this.D.getApplicationContext())) {
                h.a((String) null, SpeechConstants.TTS_EVENT_SYNTHESIZER_END);
            } else {
                h.a((String) null, 2101);
            }
        }
        return z;
    }

    private void a(Intent intent) {
        try {
            this.D.bindService(intent, this.N, 1);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "startServiceImpl");
        }
    }

    /* JADX INFO: compiled from: AmapLocationManager.java */
    public class c extends Handler {
        public c(Looper looper) {
            super(looper);
        }

        public c() {
        }

        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            try {
                super.handleMessage(message);
                if (d.this.q) {
                    return;
                }
                int i = message.what;
                if (i == 1) {
                    Message messageObtainMessage = d.this.A.obtainMessage();
                    messageObtainMessage.what = 11;
                    messageObtainMessage.setData(message.getData());
                    d.this.A.sendMessage(messageObtainMessage);
                    return;
                }
                if (i != 2) {
                    if (i == 13) {
                        if (d.this.a != null) {
                            d dVar = d.this;
                            dVar.a(dVar.a);
                            return;
                        } else {
                            AMapLocation aMapLocation = new AMapLocation("LBS");
                            aMapLocation.setErrorCode(33);
                            d.this.a(aMapLocation);
                            return;
                        }
                    }
                    switch (i) {
                        case 5:
                            Bundle data = message.getData();
                            data.putBundle("optBundle", com.autonavi.aps.amapapi.utils.b.a(d.this.b));
                            d.this.a(10, data);
                            return;
                        case 6:
                            Bundle data2 = message.getData();
                            if (d.this.d != null) {
                                d.this.d.a(data2);
                                return;
                            }
                            return;
                        case 7:
                            d.this.I = message.getData().getBoolean("ngpsAble");
                            return;
                        case 8:
                            h.a((String) null, 2141);
                            break;
                        case 9:
                            boolean unused = d.J = message.getData().getBoolean("installMockApp");
                            return;
                        case 10:
                            d.this.a((AMapLocation) message.obj);
                            return;
                        default:
                            switch (i) {
                                case 100:
                                    h.a((String) null, 2155);
                                    break;
                                case 101:
                                    break;
                                case 102:
                                    Bundle data3 = message.getData();
                                    data3.putBundle("optBundle", com.autonavi.aps.amapapi.utils.b.a(d.this.b));
                                    d.this.a(15, data3);
                                    return;
                                case 103:
                                    Bundle data4 = message.getData();
                                    if (d.this.E != null) {
                                        d.this.E.a(data4);
                                        return;
                                    }
                                    return;
                                default:
                                    return;
                            }
                            Message messageObtain = Message.obtain();
                            messageObtain.what = 1028;
                            messageObtain.obj = message.obj;
                            d.this.A.sendMessage(messageObtain);
                            if (d.this.L == null || !d.this.L.getCacheCallBack() || d.this.c == null) {
                                return;
                            }
                            d.this.c.removeMessages(13);
                            return;
                    }
                }
                Message messageObtain2 = Message.obtain();
                messageObtain2.what = 12;
                messageObtain2.obj = message.obj;
                d.this.A.sendMessage(messageObtain2);
                if (d.this.L == null || !d.this.L.getCacheCallBack() || d.this.c == null) {
                    return;
                }
                d.this.c.removeMessages(13);
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "AmapLocationManager$MainHandler", 0 == 0 ? "handleMessage" : null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(AMapLocation aMapLocation) {
        try {
            if (aMapLocation.getErrorCode() != 0) {
                aMapLocation.setLocationType(0);
            }
            if (aMapLocation.getErrorCode() == 0) {
                double latitude = aMapLocation.getLatitude();
                double longitude = aMapLocation.getLongitude();
                if ((latitude == 0.0d && longitude == 0.0d) || latitude < -90.0d || latitude > 90.0d || longitude < -180.0d || longitude > 180.0d) {
                    h.a("errorLatLng", aMapLocation.toStr());
                    aMapLocation.setLocationType(0);
                    aMapLocation.setErrorCode(8);
                    aMapLocation.setLocationDetail("LatLng is error#0802");
                }
            }
            if ("gps".equalsIgnoreCase(aMapLocation.getProvider()) || !this.d.b()) {
                aMapLocation.setAltitude(j.c(aMapLocation.getAltitude()));
                aMapLocation.setBearing(j.a(aMapLocation.getBearing()));
                aMapLocation.setSpeed(j.a(aMapLocation.getSpeed()));
                c(aMapLocation);
                b(aMapLocation);
                Iterator<AMapLocationListener> it = this.e.iterator();
                while (it.hasNext()) {
                    try {
                        it.next().onLocationChanged(aMapLocation);
                    } catch (Throwable unused) {
                    }
                }
            }
        } catch (Throwable unused2) {
        }
    }

    private static void b(AMapLocation aMapLocation) {
        if (aMapLocation == null) {
            return;
        }
        try {
            if (2 == aMapLocation.getLocationType() || 4 == aMapLocation.getLocationType()) {
                long time = aMapLocation.getTime();
                long jCurrentTimeMillis = System.currentTimeMillis();
                if (jCurrentTimeMillis > time) {
                    aMapLocation.setTime(jCurrentTimeMillis);
                }
            }
        } catch (Throwable unused) {
        }
    }

    private void c(AMapLocation aMapLocation) {
        StringBuilder sb;
        if (aMapLocation != null) {
            try {
                String locationDetail = aMapLocation.getLocationDetail();
                if (TextUtils.isEmpty(locationDetail)) {
                    sb = new StringBuilder();
                } else {
                    sb = new StringBuilder(locationDetail);
                }
                boolean zC = j.c(this.D, "EYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19XSUZJX1NUQVRF");
                boolean zC2 = j.c(this.D, "WYW5kcm9pZC5wZXJtaXNzaW9uLkNIQU5HRV9XSUZJX1NUQVRF");
                boolean zC3 = j.c(this.D, "WYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19MT0NBVElPTl9FWFRSQV9DT01NQU5EUw==");
                boolean zC4 = j.c(this.D, "EYW5kcm9pZC5wZXJtaXNzaW9uLlJFQURfUEhPTkVfU1RBVEU=");
                boolean zC5 = j.c(this.D, "EYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19GSU5FX0xPQ0FUSU9O");
                boolean zC6 = j.c(this.D, "EYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19DT0FSU0VfTE9DQVRJT04=");
                sb.append(zC ? "#pm1" : "#pm0");
                String str = "1";
                sb.append(zC2 ? "1" : "0");
                sb.append(zC3 ? "1" : "0");
                sb.append(zC4 ? "1" : "0");
                sb.append(zC5 ? "1" : "0");
                if (!zC6) {
                    str = "0";
                }
                sb.append(str);
                aMapLocation.setLocationDetail(sb.toString());
            } catch (Throwable unused) {
                com.autonavi.aps.amapapi.utils.d.b();
            }
        }
    }

    private void d(AMapLocation aMapLocation) {
        Message messageObtainMessage = this.c.obtainMessage();
        messageObtainMessage.what = 10;
        messageObtainMessage.obj = aMapLocation;
        this.c.sendMessage(messageObtainMessage);
    }

    private synchronized void e(AMapLocation aMapLocation) {
        if (aMapLocation == null) {
            try {
                aMapLocation = new AMapLocation("");
                aMapLocation.setErrorCode(8);
                aMapLocation.setLocationDetail("coarse amapLocation is null#2005");
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "handlerCoarseLocation part2");
                return;
            }
        }
        if (this.x == null) {
            this.x = new AMapLocationQualityReport();
        }
        this.x.setLocationMode(this.b.getLocationMode());
        if (this.E != null) {
            this.x.setGPSSatellites(aMapLocation.getSatellites());
            this.x.setGpsStatus(this.E.b());
        }
        this.x.setWifiAble(j.g(this.D));
        this.x.setNetworkType(j.h(this.D));
        this.x.setNetUseTime(0L);
        this.x.setInstallHighDangerMockApp(J);
        aMapLocation.setLocationQualityReport(this.x);
        try {
            if (this.G) {
                h.a(this.D, aMapLocation);
                d(aMapLocation.m31clone());
                g.a(this.D).a(aMapLocation);
                g.a(this.D).b();
            }
        } catch (Throwable th2) {
            com.autonavi.aps.amapapi.utils.b.a(th2, "ALManager", "handlerCoarseLocation part");
        }
        if (this.q) {
            return;
        }
        if (this.E != null) {
            l();
        }
        a(14, (Bundle) null);
    }

    private synchronized void a(AMapLocation aMapLocation, com.autonavi.aps.amapapi.a aVar) {
        if (aMapLocation == null) {
            try {
                aMapLocation = new AMapLocation("");
                aMapLocation.setErrorCode(8);
                aMapLocation.setLocationDetail("amapLocation is null#0801");
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "handlerLocation part3");
                return;
            }
        }
        if (!"gps".equalsIgnoreCase(aMapLocation.getProvider())) {
            aMapLocation.setProvider("lbs");
        }
        if (this.x == null) {
            this.x = new AMapLocationQualityReport();
        }
        this.x.setLocationMode(this.b.getLocationMode());
        h hVar = this.d;
        if (hVar != null) {
            this.x.setGPSSatellites(hVar.e());
            this.x.setGpsStatus(this.d.d());
        }
        this.x.setWifiAble(j.g(this.D));
        this.x.setNetworkType(j.h(this.D));
        if (aMapLocation.getLocationType() == 1 || "gps".equalsIgnoreCase(aMapLocation.getProvider())) {
            this.x.setNetUseTime(0L);
        }
        if (aVar != null) {
            this.x.setNetUseTime(aVar.a());
        }
        this.x.setInstallHighDangerMockApp(J);
        aMapLocation.setLocationQualityReport(this.x);
        try {
            if (this.G) {
                a(aMapLocation, this.B);
                if (aVar != null) {
                    aVar.d(j.b());
                }
                h.a(this.D, aMapLocation, aVar);
                h.a(this.D, aMapLocation);
                d(aMapLocation.m31clone());
                g.a(this.D).a(aMapLocation);
                g.a(this.D).b();
            }
        } catch (Throwable th2) {
            com.autonavi.aps.amapapi.utils.b.a(th2, "ALManager", "handlerLocation part2");
        }
        if (this.q) {
            return;
        }
        if (this.b.isOnceLocation()) {
            l();
            a(14, (Bundle) null);
        }
    }

    private void a(AMapLocation aMapLocation, String str) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("loc", aMapLocation);
        bundle.putString("lastLocNb", str);
        a(1014, bundle, 0L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(Message message) {
        try {
            Bundle data = message.getData();
            AMapLocation aMapLocation = (AMapLocation) data.getParcelable("loc");
            String string = data.getString("lastLocNb");
            f(aMapLocation);
            if (this.k.a(aMapLocation, string)) {
                this.k.d();
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "doSaveLastLocation");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c(AMapLocationListener aMapLocationListener) {
        if (aMapLocationListener == null) {
            throw new IllegalArgumentException("listener参数不能为null");
        }
        if (this.e == null) {
            this.e = new ArrayList<>();
        }
        if (this.e.contains(aMapLocationListener)) {
            return;
        }
        this.e.add(aMapLocationListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void j() {
        if ((Build.VERSION.SDK_INT < 29 && Build.VERSION.SDK_INT >= 23 && !j.c(this.D, "EYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19DT0FSU0VfTE9DQVRJT04=") && !j.c(this.D, "EYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19GSU5FX0xPQ0FUSU9O")) || ((Build.VERSION.SDK_INT < 31 && Build.VERSION.SDK_INT >= 29 && this.D.getApplicationInfo().targetSdkVersion >= 29 && !j.c(this.D, "EYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19GSU5FX0xPQ0FUSU9O")) || ((Build.VERSION.SDK_INT < 31 && Build.VERSION.SDK_INT >= 29 && this.D.getApplicationInfo().targetSdkVersion < 29 && !j.c(this.D, "EYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19DT0FSU0VfTE9DQVRJT04=") && !j.c(this.D, "EYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19GSU5FX0xPQ0FUSU9O")) || (Build.VERSION.SDK_INT >= 31 && !j.c(this.D, "EYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19DT0FSU0VfTE9DQVRJT04=") && !j.c(this.D, "EYW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19GSU5FX0xPQ0FUSU9O"))))) {
            k();
            return;
        }
        if (this.b == null) {
            this.b = new AMapLocationClientOption();
        }
        if (this.G) {
            return;
        }
        this.G = true;
        long gpsFirstTimeout = 0;
        a(1029, (Object) null, 0L);
        int i = AnonymousClass3.a[this.b.getLocationMode().ordinal()];
        if (i == 1) {
            a(1027, (Object) null, 0L);
            a(PointerIconCompat.TYPE_TOP_LEFT_DIAGONAL_DOUBLE_ARROW, (Object) null, 0L);
            a(1016, (Object) null, 0L);
            return;
        }
        if (i == 2) {
            if (j.m(this.D)) {
                a(1016);
                a(PointerIconCompat.TYPE_TOP_LEFT_DIAGONAL_DOUBLE_ARROW, (Object) null, 0L);
                a(1026, (Object) null, 0L);
                return;
            } else {
                a(1016);
                a(1027, (Object) null, 0L);
                a(1015, (Object) null, 0L);
                return;
            }
        }
        if (i == 3) {
            if (j.m(this.D)) {
                a(1016);
                a(PointerIconCompat.TYPE_TOP_LEFT_DIAGONAL_DOUBLE_ARROW, (Object) null, 0L);
                a(1026, (Object) null, 0L);
            } else {
                a(1027, (Object) null, 0L);
                a(1015, (Object) null, 0L);
                if (this.b.isGpsFirst() && this.b.isOnceLocation()) {
                    gpsFirstTimeout = this.b.getGpsFirstTimeout();
                }
                a(1016, (Object) null, gpsFirstTimeout);
            }
        }
    }

    /* JADX INFO: renamed from: com.amap.api.col.3sl.d$3, reason: invalid class name */
    /* JADX INFO: compiled from: AmapLocationManager.java */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] a;

        static {
            int[] iArr = new int[AMapLocationClientOption.AMapLocationMode.values().length];
            a = iArr;
            try {
                iArr[AMapLocationClientOption.AMapLocationMode.Battery_Saving.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                a[AMapLocationClientOption.AMapLocationMode.Device_Sensors.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                a[AMapLocationClientOption.AMapLocationMode.Hight_Accuracy.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    private void k() {
        AMapLocation aMapLocation = new AMapLocation("");
        aMapLocation.setErrorCode(12);
        aMapLocation.setLocationDetail("定位权限被禁用,请授予应用定位权限 #1201");
        if (this.x == null) {
            this.x = new AMapLocationQualityReport();
        }
        AMapLocationQualityReport aMapLocationQualityReport = new AMapLocationQualityReport();
        this.x = aMapLocationQualityReport;
        aMapLocationQualityReport.setGpsStatus(4);
        this.x.setGPSSatellites(0);
        this.x.setLocationMode(this.b.getLocationMode());
        this.x.setWifiAble(j.g(this.D));
        this.x.setNetworkType(j.h(this.D));
        this.x.setNetUseTime(0L);
        aMapLocation.setLocationQualityReport(this.x);
        h.a((String) null, 2121);
        d(aMapLocation);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void l() {
        try {
            a(1025);
            h hVar = this.d;
            if (hVar != null) {
                hVar.a();
            }
            g gVar = this.E;
            if (gVar != null) {
                gVar.a();
            }
            a(1016);
            a(1030, (Object) null, 0L);
            this.G = false;
            this.o = 0;
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "stopLocation");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d(AMapLocationListener aMapLocationListener) {
        if (!this.e.isEmpty() && this.e.contains(aMapLocationListener)) {
            this.e.remove(aMapLocationListener);
        }
        if (this.e.isEmpty()) {
            l();
        }
    }

    final void g() {
        a(12, (Bundle) null);
        this.h = true;
        this.i = true;
        this.j = true;
        this.F = false;
        this.u = false;
        l();
        h hVar = this.t;
        if (hVar != null) {
            hVar.b(this.D);
        }
        g.a(this.D).a();
        h.a(this.D);
        e eVar = this.v;
        if (eVar != null) {
            eVar.b().sendEmptyMessage(11);
        } else {
            ServiceConnection serviceConnection = this.N;
            if (serviceConnection != null) {
                this.D.unbindService(serviceConnection);
            }
        }
        try {
            if (this.C) {
                this.D.stopService(q());
            }
        } catch (Throwable unused) {
        }
        this.C = false;
        ArrayList<AMapLocationListener> arrayList = this.e;
        if (arrayList != null) {
            arrayList.clear();
            this.e = null;
        }
        this.N = null;
        h();
        if (this.p != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                try {
                    f.a(this.p, (Class<?>) HandlerThread.class, "quitSafely", new Object[0]);
                } catch (Throwable unused2) {
                    this.p.quit();
                }
            } else {
                this.p.quit();
            }
        }
        this.p = null;
        c cVar = this.c;
        if (cVar != null) {
            cVar.removeCallbacksAndMessages(null);
        }
        j jVar = this.k;
        if (jVar != null) {
            jVar.c();
            this.k = null;
        }
    }

    private void m() {
        com.autonavi.aps.amapapi.model.a aVarB = b(new com.autonavi.aps.amapapi.b(true));
        if (i()) {
            Bundle bundle = new Bundle();
            String str = (aVarB == null || !(aVarB.getLocationType() == 2 || aVarB.getLocationType() == 4)) ? "0" : "1";
            bundle.putBundle("optBundle", com.autonavi.aps.amapapi.utils.b.a(this.b));
            bundle.putString("isCacheLoc", str);
            a(0, bundle);
            if (this.G) {
                a(13, (Bundle) null);
            }
        }
    }

    private void a(com.autonavi.aps.amapapi.b bVar, com.autonavi.aps.amapapi.a aVar) {
        try {
            bVar.a(this.D);
            bVar.a(this.b);
            bVar.b(aVar);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "initApsBase");
        }
    }

    private com.autonavi.aps.amapapi.model.a a(com.autonavi.aps.amapapi.b bVar, boolean z) {
        if (!this.b.isLocationCacheEnable()) {
            return null;
        }
        try {
            return bVar.b(z);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "doFirstCacheLoc");
            return null;
        }
    }

    private static void a(com.autonavi.aps.amapapi.b bVar) {
        try {
            bVar.d();
            bVar.a(new AMapLocationClientOption().setNeedAddress(false));
            bVar.a(true, new com.autonavi.aps.amapapi.a());
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "apsLocation:doFirstNetLocate 2");
        }
    }

    private static void a(com.autonavi.aps.amapapi.b bVar, com.autonavi.aps.amapapi.model.a aVar) {
        if (aVar != null) {
            try {
                if (aVar.getErrorCode() == 0) {
                    bVar.b(aVar);
                }
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "apsLocation:doFirstAddCache");
            }
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(23:0|2|(12:121|3|104|4|(1:6)|117|10|(1:12)|16|17|116|18)|(5:20|(1:36)(2:22|(3:25|(2:27|(1:31))|36)(1:24))|100|87|93)(1:32)|37|(5:(1:40)(1:41)|114|42|(2:44|(1:46))|47)(1:53)|(3:112|55|56)(1:59)|110|60|(1:64)|122|68|(1:72)|102|73|(1:75)|76|(1:78)|(1:86)|100|87|93|(3:(0)|(1:107)|(1:120))) */
    /* JADX WARN: Can't wrap try/catch for region: R(34:0|2|121|3|104|4|(1:6)|117|10|(1:12)|16|17|116|18|(5:20|(1:36)(2:22|(3:25|(2:27|(1:31))|36)(1:24))|100|87|93)(1:32)|37|(5:(1:40)(1:41)|114|42|(2:44|(1:46))|47)(1:53)|(3:112|55|56)(1:59)|110|60|(1:64)|122|68|(1:72)|102|73|(1:75)|76|(1:78)|(1:86)|100|87|93|(3:(0)|(1:107)|(1:120))) */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x00e7, code lost:
    
        r8 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x00e8, code lost:
    
        com.autonavi.aps.amapapi.utils.b.a(r8, "ALManager", "fixLastLocation");
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x0121, code lost:
    
        r1 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x0122, code lost:
    
        com.autonavi.aps.amapapi.utils.b.a(r1, "ALManager", "apsLocation:callback");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private com.autonavi.aps.amapapi.model.a b(com.autonavi.aps.amapapi.b r14) {
        /*
            Method dump skipped, instruction units count: 327
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.d.b(com.autonavi.aps.amapapi.b):com.autonavi.aps.amapapi.model.a");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void n() {
        try {
            if (H || (!this.u && !this.O)) {
                H = false;
                this.O = true;
                m();
            } else {
                try {
                    if (this.u && !a() && !this.z) {
                        this.z = true;
                        p();
                    }
                } catch (Throwable th) {
                    this.z = true;
                    com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "doLBSLocation reStartService");
                }
                if (i()) {
                    this.z = false;
                    Bundle bundle = new Bundle();
                    bundle.putBundle("optBundle", com.autonavi.aps.amapapi.utils.b.a(this.b));
                    bundle.putString("d", UmidtokenInfo.getUmidtoken());
                    if (!this.d.b()) {
                        a(1, bundle);
                    }
                }
            }
        } catch (Throwable th2) {
            try {
                com.autonavi.aps.amapapi.utils.b.a(th2, "ALManager", "doLBSLocation");
                try {
                    if (this.b.isOnceLocation()) {
                        return;
                    }
                    o();
                } catch (Throwable unused) {
                }
            } finally {
                try {
                    if (!this.b.isOnceLocation()) {
                        o();
                    }
                } catch (Throwable unused2) {
                }
            }
        }
    }

    private void o() {
        if (this.b.getLocationMode() != AMapLocationClientOption.AMapLocationMode.Device_Sensors) {
            a(1016, (Object) null, this.b.getInterval() >= 1000 ? this.b.getInterval() : 1000L);
        }
    }

    /* JADX INFO: compiled from: AmapLocationManager.java */
    static class b extends HandlerThread {
        d a;

        public b(String str, d dVar) {
            super(str);
            this.a = null;
            this.a = dVar;
        }

        @Override // android.os.HandlerThread
        protected final void onLooperPrepared() {
            try {
                this.a.k.a();
                g.a(this.a.D);
                this.a.p();
                d dVar = this.a;
                if (dVar != null && dVar.D != null) {
                    com.autonavi.aps.amapapi.utils.a.b(this.a.D);
                    com.autonavi.aps.amapapi.utils.a.a(this.a.D);
                }
                super.onLooperPrepared();
            } catch (Throwable unused) {
            }
        }

        @Override // android.os.HandlerThread, java.lang.Thread, java.lang.Runnable
        public final void run() {
            try {
                super.run();
            } catch (Throwable unused) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void p() {
        try {
            if (this.m == null) {
                this.m = new Messenger(this.c);
            }
            a(q());
        } catch (Throwable unused) {
        }
    }

    private Intent q() {
        String strF;
        if (this.n == null) {
            this.n = new Intent(this.D, (Class<?>) APSService.class);
        }
        try {
            if (!TextUtils.isEmpty(AMapLocationClientOption.getAPIKEY())) {
                strF = AMapLocationClientOption.getAPIKEY();
            } else {
                strF = ig.f(this.D);
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "startServiceImpl p2");
            strF = "";
        }
        this.n.putExtra(FullBackup.APK_TREE_TOKEN, strF);
        this.n.putExtra("b", ig.c(this.D));
        this.n.putExtra("d", UmidtokenInfo.getUmidtoken());
        return this.n;
    }

    /* JADX INFO: compiled from: AmapLocationManager.java */
    public class a extends Handler {
        public a(Looper looper) {
            super(looper);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v0 */
        /* JADX WARN: Type inference failed for: r0v1 */
        /* JADX WARN: Type inference failed for: r0v2, types: [java.lang.String] */
        /* JADX WARN: Type inference failed for: r0v3, types: [java.lang.String] */
        /* JADX WARN: Type inference failed for: r0v7 */
        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            ?? r0 = 0;
            try {
                super.handleMessage(message);
                if (com.autonavi.aps.amapapi.utils.a.h) {
                    Log.e("AMapLocationClient", "SERVICE_NOT_AVAILABLE");
                    return;
                }
                int i = message.what;
                if (i == 11) {
                    d.this.a(message.getData());
                    return;
                }
                if (i == 12) {
                    d.this.b(message);
                    return;
                }
                if (i != 1011) {
                    try {
                        switch (i) {
                            case 1002:
                                d.this.c((AMapLocationListener) message.obj);
                                break;
                            case 1003:
                                d.this.j();
                                d.this.a(13, (Bundle) null);
                                break;
                            case 1004:
                                d.this.l();
                                d.this.a(14, (Bundle) null);
                                break;
                            case 1005:
                                d.this.d((AMapLocationListener) message.obj);
                                break;
                            default:
                                switch (i) {
                                    case 1014:
                                        d.this.a(message);
                                        break;
                                    case 1015:
                                        d.this.d.a(d.this.b);
                                        d.this.a(1025, (Object) null, 300000L);
                                        break;
                                    case 1016:
                                        if (j.m(d.this.D)) {
                                            Object[] objArr = new Object[1];
                                            com.autonavi.aps.amapapi.utils.d.a();
                                            d.this.r();
                                        } else if (d.this.d.b()) {
                                            d.this.a(1016, (Object) null, 1000L);
                                        } else {
                                            d.this.n();
                                        }
                                        break;
                                    case PointerIconCompat.TYPE_TOP_LEFT_DIAGONAL_DOUBLE_ARROW /* 1017 */:
                                        d.this.d.a();
                                        d.this.a(1025);
                                        break;
                                    case PointerIconCompat.TYPE_ZOOM_IN /* 1018 */:
                                        d.this.b = (AMapLocationClientOption) message.obj;
                                        if (d.this.b != null) {
                                            d.this.s();
                                        }
                                        break;
                                    default:
                                        switch (i) {
                                            case 1023:
                                                d.this.c(message);
                                                break;
                                            case 1024:
                                                d.this.d(message);
                                                break;
                                            case 1025:
                                                if (d.this.d.f()) {
                                                    d.this.d.a();
                                                    d.this.d.a(d.this.b);
                                                }
                                                d.this.a(1025, (Object) null, 300000L);
                                                break;
                                            case 1026:
                                                com.autonavi.aps.amapapi.utils.d.b();
                                                d.this.E.a(d.this.b);
                                                break;
                                            case 1027:
                                                d.this.E.a();
                                                break;
                                            case 1028:
                                                d.this.g((AMapLocation) message.obj);
                                                break;
                                            case 1029:
                                                Bundle bundle = new Bundle();
                                                bundle.putString("objHash", Integer.toString(System.identityHashCode(this)));
                                                d.this.a(16, bundle);
                                                break;
                                            case 1030:
                                                Bundle bundle2 = new Bundle();
                                                bundle2.putString("objHash", Integer.toString(System.identityHashCode(this)));
                                                d.this.a(17, bundle2);
                                                break;
                                        }
                                        break;
                                }
                                break;
                        }
                    } catch (Throwable th) {
                        r0 = message;
                        th = th;
                        if (r0 == 0) {
                            r0 = "handleMessage";
                        }
                        com.autonavi.aps.amapapi.utils.b.a(th, "AMapLocationManage$MHandlerr", r0);
                        return;
                    }
                    return;
                }
                d.this.a(14, (Bundle) null);
                d.this.g();
            } catch (Throwable th2) {
                th = th2;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void r() {
        try {
            StringBuilder sb = new StringBuilder();
            new com.autonavi.aps.amapapi.a().f("#2001");
            sb.append("模糊权限下不支持低功耗定位#2001");
            h.a((String) null, 2153);
            com.autonavi.aps.amapapi.model.a aVar = new com.autonavi.aps.amapapi.model.a("");
            aVar.setErrorCode(20);
            aVar.setLocationDetail(sb.toString());
            g(aVar);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "apsLocation:callback");
        }
    }

    private void f(AMapLocation aMapLocation) {
        if (aMapLocation == null) {
            return;
        }
        AMapLocation aMapLocationA = null;
        try {
            if (j.b == null) {
                j jVar = this.k;
                if (jVar != null) {
                    aMapLocationA = jVar.b();
                }
            } else {
                aMapLocationA = j.b.a();
            }
            h.a(aMapLocationA, aMapLocation);
        } catch (Throwable unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void s() {
        this.d.b(this.b);
        this.E.b(this.b);
        if (this.G && !this.b.getLocationMode().equals(this.r)) {
            l();
            j();
        }
        this.r = this.b.getLocationMode();
        if (this.t != null) {
            if (this.b.isOnceLocation()) {
                this.t.a(this.D, 0);
            } else {
                this.t.a(this.D, 1);
            }
            this.t.a(this.D, this.b);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void g(AMapLocation aMapLocation) {
        try {
            if (this.i && this.l != null) {
                Bundle bundle = new Bundle();
                bundle.putBundle("optBundle", com.autonavi.aps.amapapi.utils.b.a(this.b));
                a(0, bundle);
                if (this.G) {
                    a(13, (Bundle) null);
                }
                this.i = false;
            }
            e(aMapLocation);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "resultGpsLocationSuccess");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(Bundle bundle) {
        com.autonavi.aps.amapapi.a aVar;
        AMapLocation aMapLocation;
        h hVar;
        AMapLocation aMapLocationA = null;
        if (bundle != null) {
            try {
                bundle.setClassLoader(AMapLocation.class.getClassLoader());
                aMapLocation = (AMapLocation) bundle.getParcelable("loc");
                this.B = bundle.getString("nb");
                aVar = (com.autonavi.aps.amapapi.a) bundle.getParcelable("statics");
                if (aMapLocation != null) {
                    try {
                        if (aMapLocation.getErrorCode() == 0 && (hVar = this.d) != null) {
                            hVar.c();
                            if (!TextUtils.isEmpty(aMapLocation.getAdCode())) {
                                h.y = aMapLocation;
                            }
                        }
                    } catch (Throwable th) {
                        th = th;
                        com.autonavi.aps.amapapi.utils.b.a(th, "AmapLocationManager", "resultLbsLocationSuccess");
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                aVar = null;
                com.autonavi.aps.amapapi.utils.b.a(th, "AmapLocationManager", "resultLbsLocationSuccess");
            }
        } else {
            aVar = null;
            aMapLocation = null;
        }
        h hVar2 = this.d;
        aMapLocationA = hVar2 != null ? hVar2.a(aMapLocation, this.B) : aMapLocation;
        a(aMapLocationA, aVar);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b(Message message) {
        try {
            AMapLocation aMapLocation = (AMapLocation) message.obj;
            if (this.h && this.l != null) {
                Bundle bundle = new Bundle();
                bundle.putBundle("optBundle", com.autonavi.aps.amapapi.utils.b.a(this.b));
                a(0, bundle);
                if (this.G) {
                    a(13, (Bundle) null);
                }
                this.h = false;
            }
            a(aMapLocation, (com.autonavi.aps.amapapi.a) null);
            a(1025);
            a(1025, (Object) null, 300000L);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "resultGpsLocationSuccess");
        }
    }

    public final void a(int i, Notification notification) {
        if (i == 0 || notification == null) {
            return;
        }
        try {
            if (this.j && this.l != null) {
                Bundle bundle = new Bundle();
                bundle.putBundle("optBundle", com.autonavi.aps.amapapi.utils.b.a(this.b));
                a(0, bundle);
                this.j = false;
            }
            Bundle bundle2 = new Bundle();
            bundle2.putInt("i", i);
            bundle2.putParcelable(ju.g, notification);
            a(1023, bundle2, 0L);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "disableBackgroundLocation");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c(Message message) {
        if (message == null) {
            return;
        }
        try {
            Bundle data = message.getData();
            if (data == null) {
                return;
            }
            int i = data.getInt("i", 0);
            Notification notification = (Notification) data.getParcelable(ju.g);
            Intent intentQ = q();
            intentQ.putExtra("i", i);
            intentQ.putExtra(ju.g, notification);
            intentQ.putExtra(ju.f, 1);
            a(intentQ, true);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "doEnableBackgroundLocation");
        }
    }

    public final void a(boolean z) {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean("j", z);
            a(1024, bundle, 0L);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "disableBackgroundLocation");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d(Message message) {
        if (message == null) {
            return;
        }
        try {
            Bundle data = message.getData();
            if (data == null) {
                return;
            }
            boolean z = data.getBoolean("j", true);
            Intent intentQ = q();
            intentQ.putExtra("j", z);
            intentQ.putExtra(ju.f, 2);
            a(intentQ, false);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "ALManager", "doDisableBackgroundLocation");
        }
    }

    private boolean t() {
        if (j.j(this.D)) {
            int iB = -1;
            try {
                iB = f.b(((Application) this.D.getApplicationContext()).getBaseContext(), "checkSelfPermission", "android.permission.FOREGROUND_SERVICE");
            } catch (Throwable unused) {
            }
            if (iB != 0) {
                return false;
            }
        }
        return true;
    }

    private void a(Intent intent, boolean z) {
        if (this.D != null) {
            if (Build.VERSION.SDK_INT >= 26 && z) {
                if (!t()) {
                    Log.e("amapapi", "-------------调用后台定位服务，缺少权限：android.permission.FOREGROUND_SERVICE--------------");
                    return;
                } else {
                    try {
                        this.D.getClass().getMethod("startForegroundService", Intent.class).invoke(this.D, intent);
                    } catch (Throwable unused) {
                        this.D.startService(intent);
                    }
                }
            } else {
                this.D.startService(intent);
            }
            this.C = true;
        }
    }
}
