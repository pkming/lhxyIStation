package com.amap.api.fence;

import android.app.PendingIntent;
import android.content.Context;
import com.amap.api.col.p0003sl.a;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.DPoint;
import com.autonavi.aps.amapapi.utils.b;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class GeoFenceClient {
    public static final int GEOFENCE_IN = 1;
    public static final int GEOFENCE_OUT = 2;
    public static final int GEOFENCE_STAYED = 4;
    Context a;
    a b;

    public GeoFenceClient(Context context) {
        this.a = null;
        this.b = null;
        try {
            if (context == null) {
                throw new IllegalArgumentException("Context参数不能为null");
            }
            Context applicationContext = context.getApplicationContext();
            this.a = applicationContext;
            this.b = a(applicationContext);
        } catch (Throwable th) {
            b.a(th, "GeoFenceClient", "<init>");
        }
    }

    private static a a(Context context) {
        return new a(context);
    }

    public PendingIntent createPendingIntent(String str) {
        try {
            return this.b.a(str);
        } catch (Throwable th) {
            b.a(th, "GeoFenceClient", "creatPendingIntent");
            return null;
        }
    }

    public void setActivateAction(int i) {
        try {
            this.b.a(i);
        } catch (Throwable th) {
            b.a(th, "GeoFenceClient", "setActivatesAction");
        }
    }

    public void setGeoFenceListener(GeoFenceListener geoFenceListener) {
        try {
            this.b.a(geoFenceListener);
        } catch (Throwable th) {
            b.a(th, "GeoFenceClient", "setGeoFenceListener");
        }
    }

    public void setLocationClientOption(AMapLocationClientOption aMapLocationClientOption) {
        try {
            this.b.a(aMapLocationClientOption);
        } catch (Throwable th) {
            b.a(th, "GeoFenceClient", "setGeoFenceListener");
        }
    }

    public void addGeoFence(DPoint dPoint, float f, String str) {
        try {
            this.b.a(dPoint, f, str);
        } catch (Throwable th) {
            b.a(th, "GeoFenceClient", "addGeoFence round");
        }
    }

    public void addGeoFence(List<DPoint> list, String str) {
        try {
            this.b.a(list, str);
        } catch (Throwable th) {
            b.a(th, "GeoFenceClient", "addGeoFence polygon");
        }
    }

    public void addGeoFence(String str, String str2, DPoint dPoint, float f, int i, String str3) {
        try {
            this.b.a(str, str2, dPoint, f, i, str3);
        } catch (Throwable th) {
            b.a(th, "GeoFenceClient", "addGeoFence searche");
        }
    }

    public void addGeoFence(String str, String str2, String str3, int i, String str4) {
        try {
            this.b.a(str, str2, str3, i, str4);
        } catch (Throwable th) {
            b.a(th, "GeoFenceClient", "addGeoFence searche");
        }
    }

    public void addGeoFence(String str, String str2) {
        try {
            this.b.a(str, str2);
        } catch (Throwable th) {
            b.a(th, "GeoFenceClient", "addGeoFence district");
        }
    }

    public void removeGeoFence() {
        try {
            this.b.a();
        } catch (Throwable th) {
            b.a(th, "GeoFenceClient", "removeGeoFence");
        }
    }

    public boolean removeGeoFence(GeoFence geoFence) {
        try {
            return this.b.a(geoFence);
        } catch (Throwable th) {
            b.a(th, "GeoFenceClient", "removeGeoFence1");
            return false;
        }
    }

    public List<GeoFence> getAllGeoFence() {
        ArrayList arrayList = new ArrayList();
        try {
            return this.b.b();
        } catch (Throwable th) {
            b.a(th, "GeoFenceClient", "getGeoFenceList");
            return arrayList;
        }
    }

    public void setGeoFenceAble(String str, boolean z) {
        try {
            this.b.a(str, z);
        } catch (Throwable th) {
            b.a(th, "GeoFenceClient", "setGeoFenceAble");
        }
    }

    public void pauseGeoFence() {
        try {
            this.b.f();
        } catch (Throwable th) {
            b.a(th, "GeoFenceClient", "pauseGeoFence");
        }
    }

    public void resumeGeoFence() {
        try {
            this.b.h();
        } catch (Throwable th) {
            b.a(th, "GeoFenceClient", "resumeGeoFence");
        }
    }

    public boolean isPause() {
        try {
            return this.b.i();
        } catch (Throwable th) {
            b.a(th, "GeoFenceClient", "isPause");
            return true;
        }
    }
}
