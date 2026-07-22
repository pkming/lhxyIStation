package com.autonavi.aps.amapapi.restruct;

import android.Manifest;
import android.content.Context;
import android.net.http.Headers;
import android.os.Build;
import android.os.Handler;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthNr;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.mc;
import com.amap.api.col.p0003sl.nk;
import com.amap.api.col.p0003sl.nl;
import com.amap.api.col.p0003sl.nm;
import com.amap.api.col.p0003sl.nn;
import com.amap.api.col.p0003sl.no;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: compiled from: CgiManager.java */
/* JADX INFO: loaded from: classes2.dex */
public final class e {
    TelephonyManager b;
    SignalStrength d;
    private Context h;
    private c m;
    private TelephonyManager.CellInfoCallback q;
    private com.autonavi.aps.amapapi.c w;
    private boolean i = false;
    private boolean j = false;
    ArrayList<d> a = new ArrayList<>();
    private String k = null;
    private ArrayList<d> l = new ArrayList<>();
    private long n = 0;
    PhoneStateListener c = null;
    private boolean o = false;
    private Object p = new Object();
    private boolean r = false;
    boolean e = false;
    StringBuilder f = null;
    private String s = null;
    private String t = null;
    String g = null;
    private volatile boolean u = true;
    private volatile boolean v = true;

    public static boolean a(int i) {
        return i > 0 && i <= 15;
    }

    private static int b(int i) {
        return (i * 2) - 113;
    }

    static /* synthetic */ boolean d(e eVar) {
        eVar.r = true;
        return true;
    }

    public e(Context context, Handler handler) {
        this.b = null;
        this.m = null;
        this.h = context;
        if (this.b == null) {
            this.b = (TelephonyManager) com.autonavi.aps.amapapi.utils.j.a(context, "phone");
        }
        o();
        c cVar = new c(context, "cellAge", handler);
        this.m = cVar;
        cVar.a();
    }

    public final List<nk> a() {
        ArrayList arrayList = new ArrayList();
        List<CellInfo> listS = s();
        if (Build.VERSION.SDK_INT >= 17 && listS != null) {
            for (CellInfo cellInfo : listS) {
                if (cellInfo instanceof CellInfoCdma) {
                    CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfo;
                    CellIdentityCdma cellIdentity = cellInfoCdma.getCellIdentity();
                    nl nlVar = new nl(cellInfo.isRegistered(), true);
                    nlVar.m = cellIdentity.getLatitude();
                    nlVar.n = cellIdentity.getLongitude();
                    nlVar.j = cellIdentity.getSystemId();
                    nlVar.k = cellIdentity.getNetworkId();
                    nlVar.l = cellIdentity.getBasestationId();
                    nlVar.d = cellInfoCdma.getCellSignalStrength().getAsuLevel();
                    nlVar.c = cellInfoCdma.getCellSignalStrength().getCdmaDbm();
                    arrayList.add(nlVar);
                } else if (cellInfo instanceof CellInfoGsm) {
                    CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                    CellIdentityGsm cellIdentity2 = cellInfoGsm.getCellIdentity();
                    nm nmVar = new nm(cellInfo.isRegistered(), true);
                    nmVar.a = String.valueOf(cellIdentity2.getMcc());
                    nmVar.b = String.valueOf(cellIdentity2.getMnc());
                    nmVar.j = cellIdentity2.getLac();
                    nmVar.k = cellIdentity2.getCid();
                    nmVar.c = cellInfoGsm.getCellSignalStrength().getDbm();
                    nmVar.d = cellInfoGsm.getCellSignalStrength().getAsuLevel();
                    if (Build.VERSION.SDK_INT >= 24) {
                        nmVar.m = cellIdentity2.getArfcn();
                        nmVar.n = cellIdentity2.getBsic();
                    }
                    arrayList.add(nmVar);
                } else if (cellInfo instanceof CellInfoLte) {
                    CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                    CellIdentityLte cellIdentity3 = cellInfoLte.getCellIdentity();
                    nn nnVar = new nn(cellInfo.isRegistered());
                    nnVar.a = String.valueOf(cellIdentity3.getMcc());
                    nnVar.b = String.valueOf(cellIdentity3.getMnc());
                    nnVar.l = cellIdentity3.getPci();
                    nnVar.d = cellInfoLte.getCellSignalStrength().getAsuLevel();
                    nnVar.k = cellIdentity3.getCi();
                    nnVar.j = cellIdentity3.getTac();
                    nnVar.n = cellInfoLte.getCellSignalStrength().getTimingAdvance();
                    nnVar.c = cellInfoLte.getCellSignalStrength().getDbm();
                    if (Build.VERSION.SDK_INT >= 24) {
                        nnVar.m = cellIdentity3.getEarfcn();
                    }
                    arrayList.add(nnVar);
                } else if (Build.VERSION.SDK_INT >= 18 && (cellInfo instanceof CellInfoWcdma)) {
                    CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
                    CellIdentityWcdma cellIdentity4 = cellInfoWcdma.getCellIdentity();
                    no noVar = new no(cellInfo.isRegistered(), true);
                    noVar.a = String.valueOf(cellIdentity4.getMcc());
                    noVar.b = String.valueOf(cellIdentity4.getMnc());
                    noVar.j = cellIdentity4.getLac();
                    noVar.k = cellIdentity4.getCid();
                    noVar.l = cellIdentity4.getPsc();
                    noVar.d = cellInfoWcdma.getCellSignalStrength().getAsuLevel();
                    noVar.c = cellInfoWcdma.getCellSignalStrength().getDbm();
                    if (Build.VERSION.SDK_INT >= 24) {
                        noVar.m = cellIdentity4.getUarfcn();
                    }
                    arrayList.add(noVar);
                }
            }
        }
        return arrayList;
    }

    public final void a(boolean z, boolean z2) {
        try {
            this.e = com.autonavi.aps.amapapi.utils.j.a(this.h);
            if (t()) {
                b(z, z2);
                a(u());
                a(v());
            }
            if (this.e) {
                j();
            }
        } catch (SecurityException e) {
            this.g = e.getMessage();
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "CgiManager", Headers.REFRESH);
        }
    }

    private void o() {
        if (this.b == null) {
            return;
        }
        p();
    }

    public final void b() {
        boolean z = false;
        try {
            if (Build.VERSION.SDK_INT >= 31) {
                String str = this.h.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == 0 ? "hasFineLocPerm" : "hasNoFineLocPerm";
                String str2 = this.h.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == 0 ? "hasReadPhoneStatePerm" : "hasNoReadPhoneStatePerm";
                boolean z2 = true;
                if (!TextUtils.isEmpty(this.t) && !this.t.equals(str)) {
                    z = true;
                }
                if (TextUtils.isEmpty(this.s) || this.s.equals(str2)) {
                    z2 = z;
                }
                if (z2) {
                    com.autonavi.aps.amapapi.utils.d.b();
                    p();
                }
            }
        } catch (Throwable unused) {
            com.autonavi.aps.amapapi.utils.d.b();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0039 A[Catch: Exception -> 0x0099, TryCatch #0 {Exception -> 0x0099, blocks: (B:2:0x0000, B:4:0x0004, B:5:0x000b, B:8:0x001b, B:10:0x0023, B:14:0x0033, B:16:0x0039, B:18:0x003d, B:22:0x004c, B:28:0x005a, B:29:0x005c, B:33:0x0066, B:37:0x006c, B:38:0x008a, B:39:0x008f, B:41:0x0093, B:11:0x0029, B:12:0x002f), top: B:46:0x0000 }] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0093 A[Catch: Exception -> 0x0099, TRY_LEAVE, TryCatch #0 {Exception -> 0x0099, blocks: (B:2:0x0000, B:4:0x0004, B:5:0x000b, B:8:0x001b, B:10:0x0023, B:14:0x0033, B:16:0x0039, B:18:0x003d, B:22:0x004c, B:28:0x005a, B:29:0x005c, B:33:0x0066, B:37:0x006c, B:38:0x008a, B:39:0x008f, B:41:0x0093, B:11:0x0029, B:12:0x002f), top: B:46:0x0000 }] */
    /* JADX WARN: Removed duplicated region for block: B:48:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void p() {
        /*
            r8 = this;
            android.telephony.PhoneStateListener r0 = r8.c     // Catch: java.lang.Exception -> L99
            if (r0 != 0) goto Lb
            com.autonavi.aps.amapapi.restruct.e$b r0 = new com.autonavi.aps.amapapi.restruct.e$b     // Catch: java.lang.Exception -> L99
            r0.<init>()     // Catch: java.lang.Exception -> L99
            r8.c = r0     // Catch: java.lang.Exception -> L99
        Lb:
            r0 = 320(0x140, float:4.48E-43)
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch: java.lang.Exception -> L99
            java.lang.String r2 = "hasFineLocPerm"
            java.lang.String r3 = "hasNoFineLocPerm"
            java.lang.String r4 = "android.permission.ACCESS_FINE_LOCATION"
            r5 = 336(0x150, float:4.71E-43)
            r6 = 31
            if (r1 < r6) goto L2f
            android.content.Context r1 = r8.h     // Catch: java.lang.Exception -> L99
            int r1 = r1.checkSelfPermission(r4)     // Catch: java.lang.Exception -> L99
            if (r1 != 0) goto L29
            r8.t = r2     // Catch: java.lang.Exception -> L99
            com.autonavi.aps.amapapi.utils.d.b()     // Catch: java.lang.Exception -> L99
            goto L32
        L29:
            r8.t = r3     // Catch: java.lang.Exception -> L99
            com.autonavi.aps.amapapi.utils.d.b()     // Catch: java.lang.Exception -> L99
            goto L33
        L2f:
            com.autonavi.aps.amapapi.utils.d.b()     // Catch: java.lang.Exception -> L99
        L32:
            r0 = r5
        L33:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch: java.lang.Exception -> L99
            r5 = 17
            if (r1 < r5) goto L8f
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch: java.lang.Exception -> L99
            if (r1 < r6) goto L8a
            android.content.Context r1 = r8.h     // Catch: java.lang.Exception -> L99
            java.lang.String r5 = "android.permission.READ_PHONE_STATE"
            int r1 = r1.checkSelfPermission(r5)     // Catch: java.lang.Exception -> L99
            r5 = 1
            r6 = 0
            if (r1 != 0) goto L4b
            r1 = r5
            goto L4c
        L4b:
            r1 = r6
        L4c:
            android.content.Context r7 = r8.h     // Catch: java.lang.Exception -> L99
            int r4 = r7.checkSelfPermission(r4)     // Catch: java.lang.Exception -> L99
            if (r4 != 0) goto L55
            goto L56
        L55:
            r5 = r6
        L56:
            if (r1 == 0) goto L5c
            if (r5 == 0) goto L5c
            r0 = r0 | 1024(0x400, float:1.435E-42)
        L5c:
            com.autonavi.aps.amapapi.utils.d.b()     // Catch: java.lang.Exception -> L99
            if (r1 == 0) goto L64
            java.lang.String r1 = "hasReadPhoneStatePerm"
            goto L66
        L64:
            java.lang.String r1 = "hasNoReadPhoneStatePerm"
        L66:
            r8.s = r1     // Catch: java.lang.Exception -> L99
            if (r5 == 0) goto L6b
            goto L6c
        L6b:
            r2 = r3
        L6c:
            r8.t = r2     // Catch: java.lang.Exception -> L99
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L99
            java.lang.String r2 = "CgiManager | mLFLPerm = "
            r1.<init>(r2)     // Catch: java.lang.Exception -> L99
            java.lang.String r2 = r8.t     // Catch: java.lang.Exception -> L99
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Exception -> L99
            java.lang.String r2 = ";mLRPSPerm = "
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Exception -> L99
            java.lang.String r2 = r8.s     // Catch: java.lang.Exception -> L99
            r1.append(r2)     // Catch: java.lang.Exception -> L99
            com.autonavi.aps.amapapi.utils.d.b()     // Catch: java.lang.Exception -> L99
            goto L8f
        L8a:
            com.autonavi.aps.amapapi.utils.d.b()     // Catch: java.lang.Exception -> L99
            r0 = r0 | 1024(0x400, float:1.435E-42)
        L8f:
            android.telephony.PhoneStateListener r1 = r8.c     // Catch: java.lang.Exception -> L99
            if (r1 == 0) goto L98
            android.telephony.TelephonyManager r2 = r8.b     // Catch: java.lang.Exception -> L99
            r2.listen(r1, r0)     // Catch: java.lang.Exception -> L99
        L98:
            return
        L99:
            r0 = move-exception
            r0.printStackTrace()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.autonavi.aps.amapapi.restruct.e.p():void");
    }

    public final synchronized ArrayList<d> c() {
        ArrayList<d> arrayList;
        arrayList = new ArrayList<>();
        ArrayList<d> arrayList2 = this.a;
        if (arrayList2 != null) {
            Iterator<d> it = arrayList2.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next().clone());
            }
        }
        return arrayList;
    }

    public final synchronized ArrayList<d> d() {
        ArrayList<d> arrayList;
        arrayList = new ArrayList<>();
        ArrayList<d> arrayList2 = this.l;
        if (arrayList2 != null) {
            Iterator<d> it = arrayList2.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next().clone());
            }
        }
        return arrayList;
    }

    public final synchronized d e() {
        if (this.e) {
            return null;
        }
        ArrayList<d> arrayList = this.a;
        if (arrayList.size() <= 0) {
            return null;
        }
        return arrayList.get(0).clone();
    }

    public final synchronized d f() {
        if (this.e) {
            return null;
        }
        ArrayList<d> arrayList = this.l;
        if (arrayList.size() <= 0) {
            return null;
        }
        for (d dVar : arrayList) {
            if (dVar.n) {
                return dVar.clone();
            }
        }
        return arrayList.get(0).clone();
    }

    public final int g() {
        return q() | (this.i ? 4 : 0) | (this.j ? 8 : 0);
    }

    private int q() {
        d dVarE = e();
        if (dVarE != null) {
            return dVarE.l;
        }
        return 0;
    }

    public final int h() {
        return q() & 3;
    }

    private CellLocation r() {
        TelephonyManager telephonyManager = this.b;
        if (telephonyManager != null) {
            try {
                CellLocation cellLocation = telephonyManager.getCellLocation();
                Object[] objArr = new Object[2];
                com.autonavi.aps.amapapi.utils.d.a();
                this.g = null;
                return cellLocation;
            } catch (SecurityException e) {
                this.g = e.getMessage();
            } catch (Throwable th) {
                this.g = null;
                com.autonavi.aps.amapapi.utils.b.a(th, "CgiManager", "getCellLocation");
            }
        }
        return null;
    }

    private List<CellInfo> s() {
        TelephonyManager telephonyManager = this.b;
        if (telephonyManager == null) {
            return null;
        }
        List<CellInfo> allCellInfo = telephonyManager.getAllCellInfo();
        Object[] objArr = new Object[2];
        com.autonavi.aps.amapapi.utils.d.a();
        return allCellInfo;
    }

    public final TelephonyManager i() {
        return this.b;
    }

    private boolean t() {
        return !this.e && com.autonavi.aps.amapapi.utils.j.b() - this.n >= 45000;
    }

    public final void a(boolean z) {
        PhoneStateListener phoneStateListener;
        this.m.a(z);
        this.n = 0L;
        synchronized (this.p) {
            this.o = true;
        }
        TelephonyManager telephonyManager = this.b;
        if (telephonyManager != null && (phoneStateListener = this.c) != null) {
            try {
                telephonyManager.listen(phoneStateListener, 0);
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "CgiManager", "destroy");
            }
        }
        this.c = null;
        this.d = null;
        this.b = null;
    }

    public final void b(boolean z) {
        this.u = z;
    }

    public final void c(boolean z) {
        this.v = z;
    }

    /* JADX INFO: compiled from: CgiManager.java */
    class a extends TelephonyManager.CellInfoCallback {
        a() {
        }

        @Override // android.telephony.TelephonyManager.CellInfoCallback
        public final void onCellInfo(List<CellInfo> list) {
            try {
                Object[] objArr = new Object[2];
                com.autonavi.aps.amapapi.utils.d.a();
                Object[] objArr2 = new Object[2];
                String str = "noLocReqCgiEnable:" + e.this.v + " isStartLocation:" + e.this.u;
                com.autonavi.aps.amapapi.utils.d.a();
                if ((e.this.v || e.this.u) && com.autonavi.aps.amapapi.utils.j.b() - e.this.n >= 500) {
                    e.d(e.this);
                    e.this.a(e.this.u());
                    e.this.a(list);
                    e.this.n = com.autonavi.aps.amapapi.utils.j.b();
                }
            } catch (SecurityException e) {
                e.this.g = e.getMessage();
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "Cgi", "cellInfo");
            }
        }
    }

    private void b(boolean z, boolean z2) {
        if (!this.e && this.b != null && Build.VERSION.SDK_INT >= 29 && this.h.getApplicationInfo().targetSdkVersion >= 29) {
            if (this.q == null) {
                this.q = new a();
            }
            try {
                this.b.requestCellInfoUpdate(mc.a().d(), this.q);
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "Cgi", "refreshCgi");
            }
            if (z2 || z) {
                for (int i = 0; !this.r && i < 20; i++) {
                    try {
                        Thread.sleep(5L);
                    } catch (Throwable unused) {
                    }
                }
            }
        }
        this.j = false;
        TelephonyManager telephonyManager = this.b;
        if (telephonyManager != null) {
            String networkOperator = telephonyManager.getNetworkOperator();
            this.k = networkOperator;
            if (!TextUtils.isEmpty(networkOperator)) {
                this.j = true;
            }
        }
        this.n = com.autonavi.aps.amapapi.utils.j.b();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CellLocation u() {
        if (this.b == null) {
            return null;
        }
        return r();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void a(CellLocation cellLocation) {
        String[] strArrA = com.autonavi.aps.amapapi.utils.j.a(this.b);
        this.a.clear();
        if (cellLocation instanceof GsmCellLocation) {
            GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
            d dVar = new d(1, true);
            dVar.a = com.autonavi.aps.amapapi.utils.j.e(strArrA[0]);
            dVar.b = com.autonavi.aps.amapapi.utils.j.e(strArrA[1]);
            dVar.c = gsmCellLocation.getLac();
            dVar.d = gsmCellLocation.getCid();
            SignalStrength signalStrength = this.d;
            if (signalStrength != null) {
                int gsmSignalStrength = signalStrength.getGsmSignalStrength();
                dVar.s = gsmSignalStrength == 99 ? Integer.MAX_VALUE : b(gsmSignalStrength);
            }
            dVar.r = false;
            this.m.a(dVar);
            this.a.add(dVar);
            return;
        }
        if (cellLocation instanceof CdmaCellLocation) {
            CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) cellLocation;
            d dVar2 = new d(2, true);
            dVar2.a = Integer.parseInt(strArrA[0]);
            dVar2.b = Integer.parseInt(strArrA[1]);
            dVar2.f = cdmaCellLocation.getBaseStationLatitude();
            dVar2.g = cdmaCellLocation.getBaseStationLongitude();
            dVar2.h = cdmaCellLocation.getSystemId();
            dVar2.i = cdmaCellLocation.getNetworkId();
            dVar2.j = cdmaCellLocation.getBaseStationId();
            SignalStrength signalStrength2 = this.d;
            if (signalStrength2 != null) {
                dVar2.s = signalStrength2.getCdmaDbm();
            }
            dVar2.r = false;
            this.m.a(dVar2);
            this.a.add(dVar2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<CellInfo> v() {
        List<CellInfo> listS;
        try {
            if (com.autonavi.aps.amapapi.utils.j.c() < 18 || this.b == null) {
                return null;
            }
            try {
                listS = s();
                try {
                    this.g = null;
                } catch (SecurityException e) {
                    e = e;
                    this.g = e.getMessage();
                }
            } catch (SecurityException e2) {
                e = e2;
                listS = null;
            }
            return listS;
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "Cgi", "getNewCells");
            return null;
        }
    }

    final synchronized void a(List<CellInfo> list) {
        ArrayList<d> arrayList = this.l;
        if (arrayList != null) {
            arrayList.clear();
        }
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                CellInfo cellInfo = list.get(i);
                if (cellInfo != null) {
                    d dVarA = null;
                    boolean zIsRegistered = cellInfo.isRegistered();
                    if (cellInfo instanceof CellInfoCdma) {
                        dVarA = a((CellInfoCdma) cellInfo, zIsRegistered);
                    } else if (cellInfo instanceof CellInfoGsm) {
                        dVarA = a((CellInfoGsm) cellInfo, zIsRegistered);
                    } else if (cellInfo instanceof CellInfoWcdma) {
                        dVarA = a((CellInfoWcdma) cellInfo, zIsRegistered);
                    } else if (cellInfo instanceof CellInfoLte) {
                        dVarA = a((CellInfoLte) cellInfo, zIsRegistered);
                    } else if (Build.VERSION.SDK_INT >= 29 && (cellInfo instanceof CellInfoNr)) {
                        dVarA = a((CellInfoNr) cellInfo, zIsRegistered);
                    }
                    if (dVarA != null) {
                        this.m.a(dVarA);
                        dVarA.m = (short) Math.min(65535L, this.m.e(dVarA));
                        dVarA.r = true;
                        this.l.add(dVarA);
                    }
                }
            }
            this.i = false;
            ArrayList<d> arrayList2 = this.l;
            if (arrayList2 != null && arrayList2.size() > 0) {
                this.i = true;
            }
        }
    }

    final synchronized void j() {
        this.g = null;
        this.a.clear();
        this.l.clear();
        this.i = false;
        this.j = false;
    }

    public final String k() {
        return this.g;
    }

    public final String l() {
        return this.k;
    }

    private static d a(CellInfoGsm cellInfoGsm, boolean z) {
        if (cellInfoGsm == null || cellInfoGsm.getCellIdentity() == null) {
            return null;
        }
        CellIdentityGsm cellIdentity = cellInfoGsm.getCellIdentity();
        d dVarA = a(1, z, cellIdentity.getMcc(), cellIdentity.getMnc(), cellIdentity.getLac(), cellIdentity.getCid(), cellInfoGsm.getCellSignalStrength().getDbm());
        dVarA.o = cellInfoGsm.getCellIdentity().getBsic();
        dVarA.p = cellInfoGsm.getCellIdentity().getArfcn();
        dVarA.q = cellInfoGsm.getCellSignalStrength().getTimingAdvance();
        dVarA.s = cellInfoGsm.getCellSignalStrength().getDbm();
        return dVarA;
    }

    private static d a(CellInfoWcdma cellInfoWcdma, boolean z) {
        if (cellInfoWcdma == null || cellInfoWcdma.getCellIdentity() == null) {
            return null;
        }
        CellIdentityWcdma cellIdentity = cellInfoWcdma.getCellIdentity();
        d dVarA = a(4, z, cellIdentity.getMcc(), cellIdentity.getMnc(), cellIdentity.getLac(), cellIdentity.getCid(), cellInfoWcdma.getCellSignalStrength().getDbm());
        dVarA.o = cellIdentity.getPsc();
        dVarA.p = cellInfoWcdma.getCellIdentity().getUarfcn();
        dVarA.s = cellInfoWcdma.getCellSignalStrength().getDbm();
        return dVarA;
    }

    private static d a(CellInfoLte cellInfoLte, boolean z) {
        if (cellInfoLte == null || cellInfoLte.getCellIdentity() == null) {
            return null;
        }
        CellIdentityLte cellIdentity = cellInfoLte.getCellIdentity();
        d dVarA = a(3, z, cellIdentity.getMcc(), cellIdentity.getMnc(), cellIdentity.getTac(), cellIdentity.getCi(), cellInfoLte.getCellSignalStrength().getDbm());
        dVarA.o = cellIdentity.getPci();
        if (Build.VERSION.SDK_INT >= 24) {
            dVarA.p = cellIdentity.getEarfcn();
        }
        dVarA.q = cellInfoLte.getCellSignalStrength().getTimingAdvance();
        dVarA.s = cellInfoLte.getCellSignalStrength().getDbm();
        return dVarA;
    }

    private static d a(CellInfoNr cellInfoNr, boolean z) {
        int i;
        if (cellInfoNr == null || cellInfoNr.getCellIdentity() == null) {
            return null;
        }
        CellIdentityNr cellIdentityNr = (CellIdentityNr) cellInfoNr.getCellIdentity();
        int tac = cellIdentityNr.getTac();
        int i2 = 0;
        if (tac == Integer.MAX_VALUE && "HUAWEI".equals(Build.MANUFACTURER)) {
            try {
                tac = com.autonavi.aps.amapapi.utils.f.b(cellIdentityNr, "getHwTac", new Object[0]);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        long nci = cellIdentityNr.getNci();
        try {
            i = Integer.parseInt(cellIdentityNr.getMccString());
            try {
                i2 = Integer.parseInt(cellIdentityNr.getMncString());
            } catch (Throwable th2) {
                th = th2;
                th.printStackTrace();
            }
        } catch (Throwable th3) {
            th = th3;
            i = 0;
        }
        d dVarA = a(5, z, i, i2, cellIdentityNr.getTac(), 0, ((CellSignalStrengthNr) cellInfoNr.getCellSignalStrength()).getSsRsrp());
        dVarA.e = nci;
        if (tac > 16777215) {
            dVarA.c = 65535;
        } else if (tac > 65535) {
            dVarA.c = 65535;
            dVarA.q = tac;
        } else {
            dVarA.c = tac;
        }
        dVarA.o = cellIdentityNr.getPci();
        dVarA.p = cellIdentityNr.getNrarfcn();
        dVarA.s = cellInfoNr.getCellSignalStrength().getDbm();
        return dVarA;
    }

    private d a(CellInfoCdma cellInfoCdma, boolean z) {
        int i;
        if (cellInfoCdma != null && cellInfoCdma.getCellIdentity() != null) {
            CellIdentityCdma cellIdentity = cellInfoCdma.getCellIdentity();
            if (cellIdentity.getSystemId() > 0 && cellIdentity.getNetworkId() >= 0 && cellIdentity.getBasestationId() >= 0) {
                CellIdentityCdma cellIdentity2 = cellInfoCdma.getCellIdentity();
                String[] strArrA = com.autonavi.aps.amapapi.utils.j.a(this.b);
                int i2 = 0;
                try {
                    i = Integer.parseInt(strArrA[0]);
                    try {
                        i2 = Integer.parseInt(strArrA[1]);
                    } catch (Throwable unused) {
                    }
                } catch (Throwable unused2) {
                    i = 0;
                }
                d dVarA = a(2, z, i, i2, 0, 0, cellInfoCdma.getCellSignalStrength().getCdmaDbm());
                dVarA.h = cellIdentity2.getSystemId();
                dVarA.i = cellIdentity2.getNetworkId();
                dVarA.j = cellIdentity2.getBasestationId();
                dVarA.f = cellIdentity2.getLatitude();
                dVarA.g = cellIdentity2.getLongitude();
                dVarA.s = cellInfoCdma.getCellSignalStrength().getCdmaDbm();
                return dVarA;
            }
        }
        return null;
    }

    private static d a(int i, boolean z, int i2, int i3, int i4, int i5, int i6) {
        d dVar = new d(i, z);
        dVar.a = i2;
        dVar.b = i3;
        dVar.c = i4;
        dVar.d = i5;
        dVar.k = i6;
        return dVar;
    }

    public final synchronized String m() {
        if (this.e) {
            j();
        }
        StringBuilder sb = this.f;
        if (sb == null) {
            this.f = new StringBuilder();
        } else {
            sb.delete(0, sb.length());
        }
        if (h() == 1) {
            for (int i = 1; i < this.a.size(); i++) {
                this.f.append("#").append(this.a.get(i).b);
                this.f.append("|").append(this.a.get(i).c);
                this.f.append("|").append(this.a.get(i).d);
            }
        }
        for (int i2 = 1; i2 < this.l.size(); i2++) {
            d dVar = this.l.get(i2);
            if (dVar.l == 1 || dVar.l == 3 || dVar.l == 4 || dVar.l == 5) {
                this.f.append("#").append(dVar.l);
                this.f.append("|").append(dVar.a);
                this.f.append("|").append(dVar.b);
                this.f.append("|").append(dVar.c);
                this.f.append("|").append(dVar.a());
            } else if (dVar.l == 2) {
                this.f.append("#").append(dVar.l);
                this.f.append("|").append(dVar.a);
                this.f.append("|").append(dVar.h);
                this.f.append("|").append(dVar.i);
                this.f.append("|").append(dVar.j);
            }
        }
        if (this.f.length() > 0) {
            this.f.deleteCharAt(0);
        }
        return this.f.toString();
    }

    public final boolean n() {
        try {
            TelephonyManager telephonyManager = this.b;
            if (telephonyManager != null) {
                if (!TextUtils.isEmpty(telephonyManager.getSimOperator())) {
                    return true;
                }
                if (!TextUtils.isEmpty(this.b.getSimCountryIso())) {
                    return true;
                }
            }
        } catch (Throwable unused) {
        }
        try {
            int iA = com.autonavi.aps.amapapi.utils.j.a(com.autonavi.aps.amapapi.utils.j.c(this.h));
            return iA == 0 || iA == 4 || iA == 2 || iA == 5 || iA == 3;
        } catch (Throwable unused2) {
            return false;
        }
    }

    public final void a(com.autonavi.aps.amapapi.c cVar) {
        this.w = cVar;
    }

    /* JADX INFO: compiled from: CgiManager.java */
    class b extends PhoneStateListener {
        b() {
        }

        @Override // android.telephony.PhoneStateListener
        public final void onCellLocationChanged(CellLocation cellLocation) {
            Object[] objArr = new Object[2];
            com.autonavi.aps.amapapi.utils.d.a();
            Object[] objArr2 = new Object[2];
            String str = "noLocReqCgiEnable:" + e.this.v + " isStartLocation:" + e.this.u;
            com.autonavi.aps.amapapi.utils.d.a();
            if ((e.this.v || e.this.u) && com.autonavi.aps.amapapi.utils.j.b() - e.this.n >= 500) {
                try {
                    e.this.a(cellLocation);
                    e.this.a(e.this.v());
                    e.this.n = com.autonavi.aps.amapapi.utils.j.b();
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        }

        @Override // android.telephony.PhoneStateListener
        public final void onDataConnectionStateChanged(int i) {
            super.onDataConnectionStateChanged(i);
            Object[] objArr = new Object[2];
            com.autonavi.aps.amapapi.utils.d.a();
        }

        @Override // android.telephony.PhoneStateListener
        public final void onSignalStrengthChanged(int i) {
            super.onSignalStrengthChanged(i);
            Object[] objArr = new Object[2];
            com.autonavi.aps.amapapi.utils.d.a();
        }

        @Override // android.telephony.PhoneStateListener
        public final void onSignalStrengthsChanged(SignalStrength signalStrength) {
            Object[] objArr = new Object[2];
            com.autonavi.aps.amapapi.utils.d.a();
            Object[] objArr2 = new Object[2];
            String str = "noLocReqCgiEnable:" + e.this.v + " isStartLocation:" + e.this.u;
            com.autonavi.aps.amapapi.utils.d.a();
            if (signalStrength == null) {
                return;
            }
            e.this.d = signalStrength;
            if (e.this.v || e.this.u) {
                try {
                    if (e.this.w != null) {
                        e.this.w.c();
                    }
                } catch (Throwable unused) {
                }
            }
        }

        @Override // android.telephony.PhoneStateListener
        public final void onCellInfoChanged(List<CellInfo> list) {
            try {
                Object[] objArr = new Object[2];
                com.autonavi.aps.amapapi.utils.d.a();
                Object[] objArr2 = new Object[2];
                String str = "noLocReqCgiEnable:" + e.this.v + " isStartLocation:" + e.this.u;
                com.autonavi.aps.amapapi.utils.d.a();
                if (e.this.v || e.this.u) {
                    if (e.this.w != null) {
                        e.this.w.c();
                    }
                    if (com.autonavi.aps.amapapi.utils.j.b() - e.this.n < 500) {
                        return;
                    }
                    e.this.a(e.this.u());
                    e.this.a(list);
                    e.this.n = com.autonavi.aps.amapapi.utils.j.b();
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }

        @Override // android.telephony.PhoneStateListener
        public final void onServiceStateChanged(ServiceState serviceState) {
            try {
                Object[] objArr = new Object[2];
                com.autonavi.aps.amapapi.utils.d.a();
                Object[] objArr2 = new Object[2];
                String str = "noLocReqCgiEnable:" + e.this.v + " isStartLocation:" + e.this.u;
                com.autonavi.aps.amapapi.utils.d.a();
                if (e.this.v || e.this.u) {
                    int state = serviceState.getState();
                    if (state == 0) {
                        e.this.a(false, false);
                    } else {
                        if (state != 1) {
                            return;
                        }
                        e.this.j();
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }
}
