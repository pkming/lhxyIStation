package com.amap.api.col.p0003sl;

import android.content.Context;
import android.util.Log;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.PolylineOptions;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.json.JSONObject;

/* JADX INFO: compiled from: LinkLogManager.java */
/* JADX INFO: loaded from: classes2.dex */
public final class dz {
    private static Map<String, ea> a = new ConcurrentHashMap();
    private static String b = "";

    public static void a(Context context) {
        if (context == null) {
            return;
        }
        try {
            b();
            jd.a(dx.a()).a(context.getApplicationContext());
        } catch (Throwable unused) {
        }
    }

    private static void b() {
        try {
            a.put("overlay", new ec());
            a.put(SQLExec.DelimiterType.NORMAL, new eb());
        } catch (Throwable unused) {
        }
    }

    public static void a(String str, String str2) {
        a(0, SQLExec.DelimiterType.NORMAL, b, str, str2);
    }

    public static void b(String str, String str2) {
        a(1, SQLExec.DelimiterType.NORMAL, b, str, str2);
    }

    public static void c(String str, String str2) {
        a(0, "overlay", b, str, str2);
    }

    private static void d(String str, String str2) {
        a(1, "overlay", b, str, str2);
    }

    private static void a(int i, String str, String str2, String str3, String str4) {
        Map<String, ea> map;
        ea eaVar;
        try {
            String str5 = str3 + str4;
            if (dy.b) {
                a(i, str2, str5);
            }
            if (!dy.a || (map = a) == null || (eaVar = map.get(str)) == null) {
                return;
            }
            eaVar.a(i, str2, str5);
        } catch (Throwable unused) {
        }
    }

    public static void a(JSONObject jSONObject) {
        if (jSONObject == null) {
            return;
        }
        try {
            boolean zA = ih.a(jSONObject.optString("able", ""), false);
            boolean zA2 = ih.a(jSONObject.optString("mobile", ""), false);
            boolean zA3 = ih.a(jSONObject.optString("debugupload", ""), false);
            boolean zA4 = ih.a(jSONObject.optString("debugwrite", ""), false);
            boolean zA5 = ih.a(jSONObject.optString("forcedUpload", ""), false);
            dy.a = zA;
            boolean zA6 = ih.a(jSONObject.optString("di", ""), false);
            String strOptString = jSONObject.optString("dis", "");
            if (!zA6 || it.e(strOptString)) {
                jd.a(dx.a()).a(zA, zA2, zA4, zA3, Arrays.asList(jSONObject.optString("filter", "").split("&")));
                if (zA5) {
                    jd.a(dx.a()).a(zA5);
                }
            }
        } catch (Throwable unused) {
        }
    }

    public static void a() {
        try {
            if (dy.a) {
                Iterator<Map.Entry<String, ea>> it = a.entrySet().iterator();
                while (it.hasNext()) {
                    it.next().getValue().a();
                }
            }
        } catch (Throwable unused) {
        }
    }

    private static void a(int i, String str, String str2) {
        if (i == 0) {
            Log.i("linklog", str + " " + str2);
        } else {
            Log.e("linklog", str + " " + str2);
        }
    }

    public static void a(String str, String str2, MarkerOptions markerOptions) {
        if (markerOptions != null) {
            d(str, str2 + " " + markerOptions.getPosition() + " " + markerOptions.getIcons());
        } else {
            d(str, str2);
        }
    }

    public static void a(String str, String str2, List<MarkerOptions> list) {
        if (list != null) {
            Iterator<MarkerOptions> it = list.iterator();
            while (it.hasNext()) {
                a(str, str2, it.next());
            }
        }
    }

    public static void a(String str, String str2, PolylineOptions polylineOptions) {
        if (polylineOptions != null) {
            StringBuilder sb = new StringBuilder();
            List<LatLng> points = polylineOptions.getPoints();
            if (points != null) {
                sb.append("points size =").append(points.size());
            }
            sb.append(";width=").append(polylineOptions.getWidth());
            sb.append(";color=").append(polylineOptions.getColor());
            sb.append(";visible=").append(polylineOptions.isVisible());
            d(str, str2 + " " + sb.toString());
            return;
        }
        d(str, str2);
    }

    public static void a(String str, String str2, PolygonOptions polygonOptions) {
        if (polygonOptions != null) {
            StringBuilder sb = new StringBuilder();
            List<LatLng> points = polygonOptions.getPoints();
            if (points != null) {
                sb.append("points size =").append(points.size());
            }
            sb.append(";width=").append(polygonOptions.getStrokeWidth());
            sb.append(";fillColor=").append(polygonOptions.getFillColor());
            sb.append(";strokeColor=").append(polygonOptions.getStrokeColor());
            sb.append(";visible=").append(polygonOptions.isVisible());
            d(str, str2 + " " + sb.toString());
            return;
        }
        d(str, str2);
    }
}
