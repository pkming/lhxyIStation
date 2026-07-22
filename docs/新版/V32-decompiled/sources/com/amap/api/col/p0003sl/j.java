package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import com.amap.api.location.AMapLocation;
import com.autonavi.aps.amapapi.security.a;
import com.autonavi.aps.amapapi.storage.b;
import com.autonavi.aps.amapapi.storage.c;
import java.util.List;
import org.apache.tools.ant.taskdefs.WaitFor;
import org.json.JSONObject;

/* JADX INFO: compiled from: LastLocationManager.java */
/* JADX INFO: loaded from: classes2.dex */
public final class j {
    static b b;
    static kf e;
    static long g;
    String a = null;
    b c = null;
    b d = null;
    long f = 0;
    boolean h = false;
    private Context i;

    public j(Context context) {
        this.i = context.getApplicationContext();
    }

    public final void a() {
        if (this.h) {
            return;
        }
        try {
            if (this.a == null) {
                this.a = a.a("MD5", ik.k());
            }
            if (e == null) {
                e = new kf(this.i, kf.a((Class<? extends ke>) c.class));
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "LastLocationManager", "<init>:DBOperation");
        }
        this.h = true;
    }

    public final boolean a(AMapLocation aMapLocation, String str) {
        if (this.i != null && aMapLocation != null && com.autonavi.aps.amapapi.utils.j.a(aMapLocation) && aMapLocation.getLocationType() != 2 && !aMapLocation.isMock() && !aMapLocation.isFixLastLocation()) {
            b bVar = new b();
            bVar.a(aMapLocation);
            if (aMapLocation.getLocationType() == 1) {
                bVar.a((String) null);
            } else {
                bVar.a(str);
            }
            try {
                b = bVar;
                g = com.autonavi.aps.amapapi.utils.j.b();
                this.c = bVar;
                b bVar2 = this.d;
                if (bVar2 != null && com.autonavi.aps.amapapi.utils.j.a(bVar2.a(), bVar.a()) <= 500.0f) {
                    return false;
                }
                if (com.autonavi.aps.amapapi.utils.j.b() - this.f > 30000) {
                    return true;
                }
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "LastLocationManager", "setLastFix");
            }
        }
        return false;
    }

    public final AMapLocation b() {
        e();
        b bVar = b;
        if (bVar != null && com.autonavi.aps.amapapi.utils.j.a(bVar.a())) {
            return b.a();
        }
        return null;
    }

    public final AMapLocation a(AMapLocation aMapLocation, String str, long j) {
        b bVar;
        if (aMapLocation == null || aMapLocation.getErrorCode() == 0 || aMapLocation.getLocationType() == 1 || aMapLocation.getErrorCode() == 7) {
            return aMapLocation;
        }
        try {
            e();
            bVar = b;
        } catch (Throwable th) {
            th = th;
        }
        if (bVar != null && bVar.a() != null) {
            boolean zA = false;
            if (TextUtils.isEmpty(str)) {
                long jB = com.autonavi.aps.amapapi.utils.j.b() - b.d();
                if (jB >= 0 && jB <= j) {
                    zA = true;
                }
                aMapLocation.setTrustedLevel(3);
            } else {
                zA = com.autonavi.aps.amapapi.utils.j.a(b.b(), str);
                aMapLocation.setTrustedLevel(2);
            }
            if (!zA) {
                return aMapLocation;
            }
            AMapLocation aMapLocationA = b.a();
            try {
                aMapLocationA.setLocationType(9);
                aMapLocationA.setFixLastLocation(true);
                aMapLocationA.setLocationDetail(aMapLocation.getLocationDetail());
                return aMapLocationA;
            } catch (Throwable th2) {
                th = th2;
                aMapLocation = aMapLocationA;
            }
            com.autonavi.aps.amapapi.utils.b.a(th, "LastLocationManager", "fixLastLocation");
            return aMapLocation;
        }
        return aMapLocation;
    }

    public final void c() {
        try {
            d();
            this.f = 0L;
            this.h = false;
            this.c = null;
            this.d = null;
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "LastLocationManager", "destroy");
        }
    }

    public final void d() {
        b bVar;
        String strB;
        try {
            a();
            b bVar2 = this.c;
            if (bVar2 != null && com.autonavi.aps.amapapi.utils.j.a(bVar2.a()) && e != null && (bVar = this.c) != this.d && bVar.d() == 0) {
                String str = this.c.a().toStr();
                String strB2 = this.c.b();
                this.d = this.c;
                if (TextUtils.isEmpty(str)) {
                    strB = null;
                } else {
                    String strB3 = il.b(a.a(str.getBytes("UTF-8"), this.a));
                    strB = TextUtils.isEmpty(strB2) ? null : il.b(a.a(strB2.getBytes("UTF-8"), this.a));
                    str = strB3;
                }
                if (TextUtils.isEmpty(str)) {
                    return;
                }
                b bVar3 = new b();
                bVar3.b(str);
                bVar3.a(com.autonavi.aps.amapapi.utils.j.b());
                bVar3.a(strB);
                e.a(bVar3, "_id=1");
                this.f = com.autonavi.aps.amapapi.utils.j.b();
                b bVar4 = b;
                if (bVar4 != null) {
                    bVar4.a(com.autonavi.aps.amapapi.utils.j.b());
                }
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "LastLocationManager", "saveLastFix");
        }
    }

    private void e() {
        if (b == null || com.autonavi.aps.amapapi.utils.j.b() - g > WaitFor.DEFAULT_MAX_WAIT_MILLIS) {
            b bVarF = f();
            g = com.autonavi.aps.amapapi.utils.j.b();
            if (bVarF == null || !com.autonavi.aps.amapapi.utils.j.a(bVarF.a())) {
                return;
            }
            b = bVarF;
        }
    }

    private b f() {
        Throwable th;
        b bVar;
        kf kfVar;
        byte[] bArrB;
        byte[] bArrB2;
        String str = null;
        if (this.i == null) {
            return null;
        }
        a();
        try {
            kfVar = e;
        } catch (Throwable th2) {
            th = th2;
            bVar = null;
        }
        if (kfVar == null) {
            return null;
        }
        List listB = kfVar.b("_id=1", b.class);
        if (listB == null || listB.size() <= 0) {
            bVar = null;
        } else {
            bVar = (b) listB.get(0);
            try {
                byte[] bArrB3 = il.b(bVar.c());
                String str2 = (bArrB3 == null || bArrB3.length <= 0 || (bArrB2 = a.b(bArrB3, this.a)) == null || bArrB2.length <= 0) ? null : new String(bArrB2, "UTF-8");
                byte[] bArrB4 = il.b(bVar.b());
                if (bArrB4 != null && bArrB4.length > 0 && (bArrB = a.b(bArrB4, this.a)) != null && bArrB.length > 0) {
                    str = new String(bArrB, "UTF-8");
                }
                bVar.a(str);
                str = str2;
            } catch (Throwable th3) {
                th = th3;
                com.autonavi.aps.amapapi.utils.b.a(th, "LastLocationManager", "readLastFix");
            }
        }
        if (!TextUtils.isEmpty(str)) {
            AMapLocation aMapLocation = new AMapLocation("");
            com.autonavi.aps.amapapi.utils.b.a(aMapLocation, new JSONObject(str));
            if (com.autonavi.aps.amapapi.utils.j.b(aMapLocation)) {
                bVar.a(aMapLocation);
            }
        }
        return bVar;
        com.autonavi.aps.amapapi.utils.b.a(th, "LastLocationManager", "readLastFix");
        return bVar;
    }
}
