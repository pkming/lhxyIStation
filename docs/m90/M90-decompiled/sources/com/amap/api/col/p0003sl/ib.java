package com.amap.api.col.p0003sl;

import android.app.backup.FullBackup;
import android.content.Context;
import android.os.Handler;
import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.TraceLocation;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: TraceHandlerAbstract.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ib extends hz<List<TraceLocation>, List<LatLng>> implements Runnable {
    private List<TraceLocation> f;
    private Handler g;
    private int h;
    private int i;
    private String j;

    @Override // com.amap.api.col.p0003sl.lb
    public final boolean isSupportIPV6() {
        return true;
    }

    @Override // com.amap.api.col.p0003sl.hz, com.amap.api.col.p0003sl.hy
    protected final /* synthetic */ Object a(String str) throws hx {
        return b(str);
    }

    public ib(Context context, Handler handler, List<TraceLocation> list, String str, int i, int i2) {
        super(context, list);
        this.g = null;
        this.h = 0;
        this.i = 0;
        this.f = list;
        this.g = handler;
        this.i = i;
        this.h = i2;
        this.j = str;
    }

    @Override // com.amap.api.col.p0003sl.hz, com.amap.api.col.p0003sl.hy
    protected final String c() {
        long time;
        JSONArray jSONArray = new JSONArray();
        long j = 0;
        for (int i = 0; i < this.f.size(); i++) {
            TraceLocation traceLocation = this.f.get(i);
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("x", traceLocation.getLongitude());
                jSONObject.put("y", traceLocation.getLatitude());
                jSONObject.put("ag", (int) traceLocation.getBearing());
                time = traceLocation.getTime();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (i == 0) {
                if (time == 0) {
                    time = (System.currentTimeMillis() - 10000) / 1000;
                }
                jSONObject.put("tm", time / 1000);
            } else {
                if (time != 0) {
                    long j2 = time - j;
                    if (j2 < 1000) {
                        jSONObject.put("tm", 1);
                    } else {
                        jSONObject.put("tm", j2 / 1000);
                    }
                } else {
                    jSONObject.put("tm", 1);
                }
                jSONArray.put(jSONObject);
            }
            j = time;
            jSONObject.put(FullBackup.SHAREDPREFS_TREE_TOKEN, (int) traceLocation.getSpeed());
            jSONArray.put(jSONObject);
        }
        this.d = getURL() + "&" + jSONArray.toString();
        return jSONArray.toString();
    }

    private static List<LatLng> b(String str) throws hx {
        JSONObject jSONObject;
        JSONArray jSONArrayOptJSONArray;
        ArrayList arrayList = new ArrayList();
        try {
            jSONObject = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            th.printStackTrace();
        }
        if (jSONObject.has("data") && (jSONArrayOptJSONArray = jSONObject.optJSONObject("data").optJSONArray("points")) != null && jSONArrayOptJSONArray.length() != 0) {
            for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
                JSONObject jSONObjectOptJSONObject = jSONArrayOptJSONArray.optJSONObject(i);
                arrayList.add(new LatLng(Double.parseDouble(jSONObjectOptJSONObject.optString("y")), Double.parseDouble(jSONObjectOptJSONObject.optString("x"))));
            }
            return arrayList;
        }
        return arrayList;
    }

    @Override // java.lang.Runnable
    public final void run() {
        new ArrayList();
        try {
            try {
                id.a().a(this.j, this.h, d());
                id.a().a(this.j).a(this.g);
            } catch (hx e) {
                id.a();
                id.a(this.g, this.i, e.a());
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        String str = "key=" + ig.f(this.c);
        String strA = ij.a();
        return "http://restsdk.amap.com/v4/grasproad/driving?" + str + "&ts=".concat(String.valueOf(strA)) + "&scode=".concat(String.valueOf(ij.a(this.c, strA, str)));
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getIPV6URL() {
        return dx.a(getURL());
    }
}
