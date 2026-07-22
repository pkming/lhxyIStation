package com.autonavi.aps.amapapi;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import com.amap.api.col.p0003sl.ig;
import com.amap.api.col.p0003sl.ik;
import com.amap.api.col.p0003sl.il;
import com.amap.api.col.p0003sl.it;
import com.amap.api.col.p0003sl.ju;
import com.amap.api.col.p0003sl.jw;
import com.amap.api.col.p0003sl.kk;
import com.amap.api.col.p0003sl.kr;
import com.amap.api.col.p0003sl.ku;
import com.amap.api.col.p0003sl.lc;
import com.amap.api.col.p0003sl.lf;
import com.amap.api.col.p0003sl.lg;
import com.amap.api.col.p0003sl.lm;
import com.amap.api.col.p0003sl.lw;
import com.amap.api.col.p0003sl.ly;
import com.amap.api.col.p0003sl.mc;
import com.amap.api.col.p0003sl.md;
import com.amap.api.col.p0003sl.ml;
import com.amap.api.col.p0003sl.mn;
import com.amap.api.col.p0003sl.nh;
import com.amap.api.col.p0003sl.nk;
import com.amap.api.col.p0003sl.nq;
import com.amap.api.col.p0003sl.nr;
import com.amap.api.col.p0003sl.nv;
import com.amap.api.col.p0003sl.nw;
import com.amap.api.col.p0003sl.nx;
import com.autonavi.aps.amapapi.restruct.e;
import com.autonavi.aps.amapapi.restruct.h;
import com.autonavi.aps.amapapi.restruct.k;
import com.autonavi.aps.amapapi.utils.j;
import com.unisound.common.r;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.crypto.KeyGenerator;

/* JADX INFO: compiled from: CollectionManager.java */
/* JADX INFO: loaded from: classes2.dex */
public final class c implements nx {
    private static long k;
    Context a;
    nh d;
    lf e;
    private Handler g;
    private LocationManager h;
    private a i;
    private ArrayList<mn> f = new ArrayList<>();
    k b = null;
    e c = null;
    private volatile boolean j = false;

    private static byte[] b(int i) {
        return new byte[]{(byte) ((i >> 24) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 8) & 255), (byte) (i & 255)};
    }

    private static byte[] c(int i) {
        return new byte[]{(byte) ((i & 65280) >> 8), (byte) (i & 255)};
    }

    static /* synthetic */ byte[] f() {
        return a(128);
    }

    c(Context context) {
        this.a = null;
        this.a = context;
        lf lfVar = new lf();
        this.e = lfVar;
        lm.a(this.a, lfVar, ju.k, 100, 1024000, "0");
        lf lfVar2 = this.e;
        int i = com.autonavi.aps.amapapi.utils.a.g;
        boolean z = com.autonavi.aps.amapapi.utils.a.e;
        int i2 = com.autonavi.aps.amapapi.utils.a.f;
        lfVar2.f = new ly(context, i, "kKey", new lw(context, z, i2, i2 * 10, "carrierLocKey"));
        this.e.e = new kk();
    }

    /* JADX INFO: compiled from: CollectionManager.java */
    static class a implements LocationListener {
        private c a;

        @Override // android.location.LocationListener
        public final void onProviderDisabled(String str) {
        }

        @Override // android.location.LocationListener
        public final void onProviderEnabled(String str) {
        }

        @Override // android.location.LocationListener
        public final void onStatusChanged(String str, int i, Bundle bundle) {
        }

        a(c cVar) {
            this.a = cVar;
        }

        final void a(c cVar) {
            this.a = cVar;
        }

        final void a() {
            this.a = null;
        }

        @Override // android.location.LocationListener
        public final void onLocationChanged(Location location) {
            try {
                c cVar = this.a;
                if (cVar != null) {
                    cVar.a(location);
                }
            } catch (Throwable unused) {
            }
        }
    }

    final void a() {
        LocationManager locationManager;
        if (j.m(this.a)) {
            Object[] objArr = new Object[1];
            com.autonavi.aps.amapapi.utils.d.a();
            return;
        }
        try {
            a aVar = this.i;
            if (aVar != null && (locationManager = this.h) != null) {
                locationManager.removeUpdates(aVar);
            }
            a aVar2 = this.i;
            if (aVar2 != null) {
                aVar2.a();
            }
            if (this.j) {
                g();
                this.b.a((c) null);
                this.c.a((c) null);
                this.c = null;
                this.b = null;
                this.g = null;
                this.j = false;
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "clm", "stc");
        }
    }

    public final void a(e eVar, k kVar, Handler handler) {
        LocationManager locationManager;
        Object[] objArr = new Object[1];
        com.autonavi.aps.amapapi.utils.d.a();
        if (this.j || eVar == null || kVar == null || handler == null) {
            return;
        }
        if (j.m(this.a)) {
            Object[] objArr2 = new Object[1];
            com.autonavi.aps.amapapi.utils.d.a();
            return;
        }
        this.j = true;
        this.c = eVar;
        this.b = kVar;
        kVar.a(this);
        this.c.a(this);
        this.g = handler;
        try {
            if (this.h == null && handler != null) {
                this.h = (LocationManager) this.a.getSystemService("location");
            }
            if (this.i == null) {
                this.i = new a(this);
            }
            this.i.a(this);
            a aVar = this.i;
            if (aVar != null && (locationManager = this.h) != null) {
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000L, -1.0f, aVar);
            }
            if (this.d == null) {
                nh nhVar = new nh("6.4.3", ig.f(this.a), "S128DF1572465B890OE3F7A13167KLEI", ig.c(this.a), this);
                this.d = nhVar;
                nhVar.a(ik.k()).b(ik.f(this.a)).c(ik.a(this.a)).d(ik.e(this.a)).e(ik.n()).f(ik.f()).g(Build.MODEL).h(Build.MANUFACTURER).i(Build.BRAND).a(Build.VERSION.SDK_INT).j(Build.VERSION.RELEASE).a(nr.a(ik.h())).k(ik.h());
                nh.b();
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "col", r.r);
        }
    }

    public final void b() {
        try {
            Object[] objArr = new Object[1];
            com.autonavi.aps.amapapi.utils.d.a();
            Handler handler = this.g;
            if (handler != null) {
                handler.post(new Runnable() { // from class: com.autonavi.aps.amapapi.c.1
                    @Override // java.lang.Runnable
                    public final void run() {
                        try {
                            if (c.this.d == null || c.this.b == null) {
                                return;
                            }
                            nh.b(c.this.b.a());
                        } catch (Throwable th) {
                            com.autonavi.aps.amapapi.utils.b.a(th, "cl", "upwr");
                        }
                    }
                });
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "cl", "upw");
        }
    }

    public final void c() {
        e eVar;
        try {
            Object[] objArr = new Object[1];
            com.autonavi.aps.amapapi.utils.d.a();
            if (this.d == null || (eVar = this.c) == null) {
                return;
            }
            nh.a(eVar.a());
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "cl", "upc");
        }
    }

    /* JADX INFO: compiled from: CollectionManager.java */
    class b extends md {
        private int b;
        private Location c;

        b(int i) {
            this.b = 0;
            this.b = i;
        }

        b(c cVar, Location location) {
            this(1);
            this.c = location;
        }

        private void a() {
            try {
                Object[] objArr = new Object[1];
                com.autonavi.aps.amapapi.utils.d.a();
                if (this.c != null && c.this.j) {
                    if (j.m(c.this.a)) {
                        Object[] objArr2 = new Object[1];
                        com.autonavi.aps.amapapi.utils.d.a();
                        return;
                    }
                    Bundle extras = this.c.getExtras();
                    int i = extras != null ? extras.getInt("satellites") : 0;
                    if (j.a(this.c, i)) {
                        return;
                    }
                    if (c.this.b != null && !c.this.b.s) {
                        c.this.b.f();
                    }
                    ArrayList<nr> arrayListA = c.this.b.a();
                    List<nk> listA = c.this.c.a();
                    ml.a aVar = new ml.a();
                    nq nqVar = new nq();
                    nqVar.i = this.c.getAccuracy();
                    nqVar.f = this.c.getAltitude();
                    nqVar.d = this.c.getLatitude();
                    nqVar.h = this.c.getBearing();
                    nqVar.e = this.c.getLongitude();
                    nqVar.j = this.c.isFromMockProvider();
                    nqVar.a = this.c.getProvider();
                    nqVar.g = this.c.getSpeed();
                    nqVar.l = (byte) i;
                    nqVar.b = System.currentTimeMillis();
                    nqVar.c = this.c.getTime();
                    nqVar.k = this.c.getTime();
                    aVar.a = nqVar;
                    aVar.b = arrayListA;
                    WifiInfo wifiInfoC = c.this.b.c();
                    if (wifiInfoC != null) {
                        aVar.c = nr.a(h.a(wifiInfoC));
                    }
                    aVar.d = k.z;
                    aVar.f = this.c.getTime();
                    aVar.g = (byte) ik.i(c.this.a);
                    aVar.h = ik.n(c.this.a);
                    aVar.e = c.this.b.k();
                    aVar.j = j.a(c.this.a);
                    aVar.i = listA;
                    mn mnVarA = nh.a(aVar);
                    if (mnVarA == null) {
                        return;
                    }
                    synchronized (c.this.f) {
                        c.this.f.add(mnVarA);
                        if (c.this.f.size() >= 5) {
                            c.this.e();
                        }
                    }
                    c.this.d();
                }
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "cl", "coll");
            }
        }

        private void b() {
            Object[] objArr = new Object[1];
            com.autonavi.aps.amapapi.utils.d.a();
            if (!j.m(c.this.a)) {
                kr krVarA = null;
                try {
                    long unused = c.k = System.currentTimeMillis();
                    if (c.this.e.f.d()) {
                        krVarA = kr.a(new File(c.this.e.a), c.this.e.b);
                        ArrayList arrayList = new ArrayList();
                        byte[] bArrF = c.f();
                        if (bArrF != null) {
                            List listB = c.b(krVarA, c.this.e, arrayList, bArrF);
                            if (listB != null && listB.size() != 0) {
                                c.this.e.f.a_(true);
                                if (nh.a(it.b(nh.a(com.autonavi.aps.amapapi.security.a.a(bArrF), il.b(bArrF, nh.a(), it.c()), listB)))) {
                                    c.b(krVarA, arrayList);
                                }
                            }
                            try {
                                krVarA.close();
                                return;
                            } catch (Throwable unused2) {
                                return;
                            }
                        }
                        try {
                            krVarA.close();
                            return;
                        } catch (Throwable unused3) {
                            return;
                        }
                    }
                    if (krVarA != null) {
                        try {
                            krVarA.close();
                            return;
                        } catch (Throwable unused4) {
                            return;
                        }
                    }
                    return;
                } catch (Throwable th) {
                    try {
                        jw.c(th, "leg", "uts");
                        if (krVarA != null) {
                            try {
                                krVarA.close();
                                return;
                            } catch (Throwable unused5) {
                                return;
                            }
                        }
                        return;
                    } catch (Throwable th2) {
                        if (krVarA != null) {
                            try {
                                krVarA.close();
                            } catch (Throwable unused6) {
                            }
                        }
                        throw th2;
                    }
                }
            }
            Object[] objArr2 = new Object[1];
            com.autonavi.aps.amapapi.utils.d.a();
        }

        @Override // com.amap.api.col.p0003sl.md
        public final void runTask() {
            int i = this.b;
            if (i == 1) {
                a();
            } else if (i == 2) {
                b();
            } else if (i == 3) {
                c.this.g();
            }
        }
    }

    public final void a(Location location) {
        try {
            Handler handler = this.g;
            if (handler != null) {
                handler.post(new b(this, location));
            }
        } catch (Throwable th) {
            jw.c(th, "cl", "olcc");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void g() {
        try {
            Object[] objArr = new Object[1];
            com.autonavi.aps.amapapi.utils.d.a();
            if (j.m(this.a)) {
                Object[] objArr2 = new Object[1];
                com.autonavi.aps.amapapi.utils.d.a();
                return;
            }
            ArrayList<mn> arrayList = this.f;
            if (arrayList != null && arrayList.size() != 0) {
                ArrayList<mn> arrayList2 = new ArrayList();
                synchronized (this.f) {
                    arrayList2.addAll(this.f);
                    this.f.clear();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] bArrA = a(256);
                if (bArrA == null) {
                    return;
                }
                byteArrayOutputStream.write(c(bArrA.length));
                byteArrayOutputStream.write(bArrA);
                for (mn mnVar : arrayList2) {
                    byte[] bArrB = mnVar.b();
                    if (bArrB.length >= 10 && bArrB.length <= 65535) {
                        byte[] bArrB2 = il.b(bArrA, bArrB, it.c());
                        byteArrayOutputStream.write(c(bArrB2.length));
                        byteArrayOutputStream.write(bArrB2);
                        byteArrayOutputStream.write(b(mnVar.a()));
                    }
                }
                lg.a(Long.toString(System.currentTimeMillis()), byteArrayOutputStream.toByteArray(), this.e);
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "clm", "wtD");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void b(kr krVar, List<String> list) {
        if (krVar != null) {
            try {
                Iterator<String> it = list.iterator();
                while (it.hasNext()) {
                    krVar.c(it.next());
                }
                krVar.close();
            } catch (Throwable th) {
                jw.c(th, "aps", "dlo");
            }
        }
    }

    public final void d() {
        try {
            if (j.m(this.a)) {
                Object[] objArr = new Object[1];
                com.autonavi.aps.amapapi.utils.d.a();
            } else {
                if (System.currentTimeMillis() - k < 60000) {
                    return;
                }
                mc.a().a(new b(2));
            }
        } catch (Throwable unused) {
        }
    }

    public final void e() {
        try {
            mc.a().a(new b(3));
        } catch (Throwable unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x00de, code lost:
    
        if (r7 == null) goto L61;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x00e0, code lost:
    
        r7.close();
     */
    /* JADX WARN: Removed duplicated region for block: B:100:0x00d7 A[EXC_TOP_SPLITTER, PHI: r5 r9 r16
      0x00d7: PHI (r5v3 int) = (r5v4 int), (r5v5 int) binds: [B:68:0x00ff, B:55:0x00d5] A[DONT_GENERATE, DONT_INLINE]
      0x00d7: PHI (r9v1 com.amap.api.col.3sl.kr$b) = (r9v2 com.amap.api.col.3sl.kr$b), (r9v3 com.amap.api.col.3sl.kr$b) binds: [B:68:0x00ff, B:55:0x00d5] A[DONT_GENERATE, DONT_INLINE]
      0x00d7: PHI (r16v3 java.lang.String[]) = (r16v4 java.lang.String[]), (r16v6 java.lang.String[]) binds: [B:68:0x00ff, B:55:0x00d5] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:118:0x00fc A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:126:0x010c A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:90:0x003b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.util.List<com.amap.api.col.p0003sl.mn> b(com.amap.api.col.p0003sl.kr r17, com.amap.api.col.p0003sl.lf r18, java.util.List<java.lang.String> r19, byte[] r20) {
        /*
            Method dump skipped, instruction units count: 285
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.autonavi.aps.amapapi.c.b(com.amap.api.col.3sl.kr, com.amap.api.col.3sl.lf, java.util.List, byte[]):java.util.List");
    }

    @Override // com.amap.api.col.p0003sl.nx
    public final nw a(nv nvVar) {
        try {
            com.autonavi.aps.amapapi.trans.b bVar = new com.autonavi.aps.amapapi.trans.b();
            bVar.a(nvVar.b);
            bVar.a(nvVar.a);
            bVar.a(nvVar.d);
            ku.a();
            lc lcVarA = ku.a(bVar);
            nw nwVar = new nw();
            nwVar.c = lcVarA.a;
            nwVar.b = lcVarA.b;
            nwVar.a = 200;
            return nwVar;
        } catch (Throwable unused) {
            return null;
        }
    }

    private static byte[] a(int i) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            if (keyGenerator == null) {
                return null;
            }
            keyGenerator.init(i);
            return keyGenerator.generateKey().getEncoded();
        } catch (Throwable unused) {
            return null;
        }
    }

    private static int a(byte[] bArr) {
        return ((bArr[0] & 255) << 24) | (bArr[3] & 255) | ((bArr[2] & 255) << 8) | ((bArr[1] & 255) << 16);
    }
}
