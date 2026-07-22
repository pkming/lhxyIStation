package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.district.DistrictSearchQuery;
import com.autonavi.aps.amapapi.utils.b;
import org.json.JSONObject;

/* JADX INFO: compiled from: H5LocationClient.java */
/* JADX INFO: loaded from: classes2.dex */
public final class i {
    a c;
    private Context d;
    private WebView f;
    Object a = new Object();
    private AMapLocationClient e = null;
    private String g = "AMap.Geolocation.cbk";
    AMapLocationClientOption b = null;
    private volatile boolean h = false;

    public i(Context context, WebView webView) {
        this.f = null;
        this.c = null;
        this.d = context.getApplicationContext();
        this.f = webView;
        this.c = new a();
    }

    public final void a() {
        if (this.f == null || this.d == null || Build.VERSION.SDK_INT < 17 || this.h) {
            return;
        }
        try {
            this.f.getSettings().setJavaScriptEnabled(true);
            this.f.addJavascriptInterface(this, "AMapAndroidLoc");
            if (!TextUtils.isEmpty(this.f.getUrl())) {
                this.f.reload();
            }
            if (this.e == null) {
                AMapLocationClient aMapLocationClient = new AMapLocationClient(this.d);
                this.e = aMapLocationClient;
                aMapLocationClient.setLocationListener(this.c);
            }
            this.h = true;
        } catch (Throwable unused) {
        }
    }

    public final void b() {
        synchronized (this.a) {
            this.h = false;
            AMapLocationClient aMapLocationClient = this.e;
            if (aMapLocationClient != null) {
                aMapLocationClient.unRegisterLocationListener(this.c);
                this.e.stopLocation();
                this.e.onDestroy();
                this.e = null;
            }
            this.b = null;
        }
    }

    private void a(String str) {
        boolean z;
        boolean z2;
        if (this.b == null) {
            this.b = new AMapLocationClientOption();
        }
        int iOptInt = 5;
        long jOptLong = 30000;
        boolean z3 = true;
        try {
            JSONObject jSONObject = new JSONObject(str);
            jOptLong = jSONObject.optLong("to", 30000L);
            z = jSONObject.optInt("useGPS", 1) == 1;
            try {
                z2 = jSONObject.optInt("watch", 0) == 1;
                try {
                    iOptInt = jSONObject.optInt("interval", 5);
                    String strOptString = jSONObject.optString("callback", null);
                    if (!TextUtils.isEmpty(strOptString)) {
                        this.g = strOptString;
                    } else {
                        this.g = "AMap.Geolocation.cbk";
                    }
                } catch (Throwable unused) {
                }
            } catch (Throwable unused2) {
                z2 = false;
            }
        } catch (Throwable unused3) {
            z = false;
            z2 = false;
        }
        try {
            this.b.setHttpTimeOut(jOptLong);
            if (z) {
                this.b.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            } else {
                this.b.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
            }
            AMapLocationClientOption aMapLocationClientOption = this.b;
            if (z2) {
                z3 = false;
            }
            aMapLocationClientOption.setOnceLocation(z3);
            if (z2) {
                this.b.setInterval(iOptInt * 1000);
            }
        } catch (Throwable unused4) {
        }
    }

    @JavascriptInterface
    public final void getLocation(String str) {
        synchronized (this.a) {
            if (this.h) {
                a(str);
                AMapLocationClient aMapLocationClient = this.e;
                if (aMapLocationClient != null) {
                    aMapLocationClient.setLocationOption(this.b);
                    this.e.stopLocation();
                    this.e.startLocation();
                }
            }
        }
    }

    @JavascriptInterface
    public final void stopLocation() {
        AMapLocationClient aMapLocationClient;
        if (this.h && (aMapLocationClient = this.e) != null) {
            aMapLocationClient.stopLocation();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b(final String str) {
        try {
            if (this.f != null) {
                if (Build.VERSION.SDK_INT >= 19) {
                    this.f.evaluateJavascript("javascript:" + this.g + "('" + str + "')", new ValueCallback<String>() { // from class: com.amap.api.col.3sl.i.1
                        @Override // android.webkit.ValueCallback
                        public final /* bridge */ /* synthetic */ void onReceiveValue(String str2) {
                        }
                    });
                } else {
                    this.f.post(new Runnable() { // from class: com.amap.api.col.3sl.i.2
                        @Override // java.lang.Runnable
                        public final void run() {
                            i.this.f.loadUrl("javascript:" + i.this.g + "('" + str + "')");
                        }
                    });
                }
            }
        } catch (Throwable th) {
            b.a(th, "H5LocationClient", "callbackJs()");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String b(AMapLocation aMapLocation) {
        JSONObject jSONObject = new JSONObject();
        try {
            if (aMapLocation == null) {
                jSONObject.put("errorCode", -1);
                jSONObject.put(MyLocationStyle.ERROR_INFO, "unknownError");
            } else if (aMapLocation.getErrorCode() == 0) {
                jSONObject.put("errorCode", 0);
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("x", aMapLocation.getLongitude());
                jSONObject2.put("y", aMapLocation.getLatitude());
                jSONObject2.put("precision", aMapLocation.getAccuracy());
                jSONObject2.put("type", aMapLocation.getLocationType());
                jSONObject2.put("country", aMapLocation.getCountry());
                jSONObject2.put(DistrictSearchQuery.KEYWORDS_PROVINCE, aMapLocation.getProvince());
                jSONObject2.put("city", aMapLocation.getCity());
                jSONObject2.put("cityCode", aMapLocation.getCityCode());
                jSONObject2.put(DistrictSearchQuery.KEYWORDS_DISTRICT, aMapLocation.getDistrict());
                jSONObject2.put("adCode", aMapLocation.getAdCode());
                jSONObject2.put("street", aMapLocation.getStreet());
                jSONObject2.put("streetNum", aMapLocation.getStreetNum());
                jSONObject2.put("floor", aMapLocation.getFloor());
                jSONObject2.put("address", aMapLocation.getAddress());
                jSONObject.put("result", jSONObject2);
            } else {
                jSONObject.put("errorCode", aMapLocation.getErrorCode());
                jSONObject.put(MyLocationStyle.ERROR_INFO, aMapLocation.getErrorInfo());
                jSONObject.put("locationDetail", aMapLocation.getLocationDetail());
            }
        } catch (Throwable unused) {
        }
        return jSONObject.toString();
    }

    /* JADX INFO: compiled from: H5LocationClient.java */
    class a implements AMapLocationListener {
        a() {
        }

        @Override // com.amap.api.location.AMapLocationListener
        public final void onLocationChanged(AMapLocation aMapLocation) {
            if (i.this.h) {
                i.this.b(i.b(aMapLocation));
            }
        }
    }
}
