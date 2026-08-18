package com.amap.api.location;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import com.amap.api.col.p0003sl.d;
import com.amap.api.col.p0003sl.ik;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.col.p0003sl.iq;
import com.amap.api.col.p0003sl.ku;
import com.autonavi.aps.amapapi.utils.b;
import com.autonavi.aps.amapapi.utils.h;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class AMapLocationClient {
    Context a;
    d b;

    public String getVersion() {
        return "6.4.3";
    }

    public AMapLocationClient(Context context) throws Exception {
        a(context);
        try {
            if (context == null) {
                throw new IllegalArgumentException("Context参数不能为null");
            }
            Context applicationContext = context.getApplicationContext();
            this.a = applicationContext;
            com.autonavi.aps.amapapi.utils.d.a(applicationContext);
            this.b = new d(context, null, null);
        } catch (Throwable th) {
            b.a(th, "AMClt", "ne1");
        }
    }

    public AMapLocationClient(Context context, Intent intent) throws Exception {
        a(context);
        try {
            if (context == null) {
                throw new IllegalArgumentException("Context参数不能为null");
            }
            this.a = context.getApplicationContext();
            this.b = new d(this.a, intent, null);
        } catch (Throwable th) {
            b.a(th, "AMClt", "ne2");
        }
    }

    public AMapLocationClient(Looper looper, Context context) throws Exception {
        a(context);
        try {
            if (context == null) {
                throw new IllegalArgumentException("Context参数不能为null");
            }
            this.a = context.getApplicationContext();
            this.b = new d(this.a, null, looper);
        } catch (Throwable th) {
            b.a(th, "AMClt", "ne3");
        }
    }

    private static void a(Context context) throws Exception {
        iq iqVarA = ip.a(context, b.c());
        if (iqVarA.a == ip.c.SuccessCode) {
            return;
        }
        Log.e("AMapLocationClient", iqVarA.b);
        throw new Exception(iqVarA.b);
    }

    public void setLocationOption(AMapLocationClientOption aMapLocationClientOption) {
        try {
            if (aMapLocationClientOption == null) {
                throw new IllegalArgumentException("LocationManagerOption参数不能为null");
            }
            d dVar = this.b;
            if (dVar != null) {
                dVar.a(aMapLocationClientOption);
            }
            if (aMapLocationClientOption.b) {
                aMapLocationClientOption.b = false;
                JSONObject jSONObject = new JSONObject();
                if (!TextUtils.isEmpty(aMapLocationClientOption.c)) {
                    jSONObject.put("amap_loc_scenes_type", aMapLocationClientOption.c);
                }
                h.a(this.a, "O019", jSONObject);
            }
        } catch (Throwable th) {
            b.a(th, "AMClt", "sLocnO");
        }
    }

    public void setLocationListener(AMapLocationListener aMapLocationListener) {
        try {
            if (aMapLocationListener == null) {
                throw new IllegalArgumentException("listener参数不能为null");
            }
            d dVar = this.b;
            if (dVar != null) {
                dVar.a(aMapLocationListener);
            }
        } catch (Throwable th) {
            b.a(th, "AMClt", "sLocL");
        }
    }

    public void startLocation() {
        try {
            d dVar = this.b;
            if (dVar != null) {
                dVar.b();
            }
        } catch (Throwable th) {
            b.a(th, "AMClt", "stl");
        }
    }

    public void stopLocation() {
        try {
            d dVar = this.b;
            if (dVar != null) {
                dVar.c();
            }
        } catch (Throwable th) {
            b.a(th, "AMClt", "stl");
        }
    }

    public AMapLocation getLastKnownLocation() {
        try {
            d dVar = this.b;
            if (dVar != null) {
                return dVar.e();
            }
            return null;
        } catch (Throwable th) {
            b.a(th, "AMClt", "gLastL");
            return null;
        }
    }

    public void startAssistantLocation(WebView webView) {
        try {
            d dVar = this.b;
            if (dVar != null) {
                dVar.a(webView);
            }
        } catch (Throwable th) {
            b.a(th, "AMClt", "sttAssL1");
        }
    }

    public void stopAssistantLocation() {
        try {
            d dVar = this.b;
            if (dVar != null) {
                dVar.f();
            }
        } catch (Throwable th) {
            b.a(th, "AMClt", "stAssL");
        }
    }

    public static void updatePrivacyShow(Context context, boolean z, boolean z2) {
        ip.a(context, z, z2, b.c());
    }

    public static void updatePrivacyAgree(Context context, boolean z) {
        ip.a(context, z, b.c());
    }

    public static void setApiKey(String str) {
        try {
            AMapLocationClientOption.a = str;
        } catch (Throwable th) {
            b.a(th, "AMClt", "sKey");
        }
    }

    public boolean isStarted() {
        try {
            d dVar = this.b;
            if (dVar != null) {
                return dVar.a();
            }
            return false;
        } catch (Throwable th) {
            b.a(th, "AMClt", "isS");
            return false;
        }
    }

    public void unRegisterLocationListener(AMapLocationListener aMapLocationListener) {
        try {
            d dVar = this.b;
            if (dVar != null) {
                dVar.b(aMapLocationListener);
            }
        } catch (Throwable th) {
            b.a(th, "AMClt", "unRL");
        }
    }

    public void onDestroy() {
        try {
            d dVar = this.b;
            if (dVar != null) {
                dVar.d();
            }
        } catch (Throwable th) {
            b.a(th, "AMClt", "onDy");
        }
    }

    public void enableBackgroundLocation(int i, Notification notification) {
        try {
            d dVar = this.b;
            if (dVar != null) {
                dVar.a(i, notification);
            }
        } catch (Throwable th) {
            b.a(th, "AMClt", "eBackL");
        }
    }

    public void disableBackgroundLocation(boolean z) {
        try {
            d dVar = this.b;
            if (dVar != null) {
                dVar.a(z);
            }
        } catch (Throwable th) {
            b.a(th, "AMClt", "dBackL");
        }
    }

    public static String getDeviceId(Context context) {
        return ik.q(context);
    }

    public static void setHost(String str) {
        if (TextUtils.isEmpty(str)) {
            ku.a = -1;
            ku.b = "";
        } else {
            ku.a = 1;
            ku.b = str;
        }
    }
}
