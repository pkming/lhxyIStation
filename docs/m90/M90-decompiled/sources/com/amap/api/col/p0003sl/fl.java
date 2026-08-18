package com.amap.api.col.p0003sl;

import android.app.Instrumentation;
import android.content.Context;
import android.provider.DocumentsContract;
import android.view.HardwareRenderer;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.tools.ant.taskdefs.optional.j2ee.HotDeploymentTool;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: CloudHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class fl<T, V> extends fh<T, V> {
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.lb
    public byte[] getEntityBytes() {
        return null;
    }

    public fl(Context context, T t) {
        super(context, t);
        this.a = false;
    }

    protected static JSONArray a(JSONObject jSONObject) {
        JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("data");
        if (jSONObjectOptJSONObject != null) {
            return jSONObjectOptJSONObject.optJSONArray(HotDeploymentTool.ACTION_LIST);
        }
        return null;
    }

    protected static int b(JSONObject jSONObject) {
        JSONObject jSONObjectOptJSONObject;
        JSONObject jSONObjectOptJSONObject2 = jSONObject.optJSONObject("data");
        if (jSONObjectOptJSONObject2 == null || (jSONObjectOptJSONObject = jSONObjectOptJSONObject2.optJSONObject(DocumentsContract.EXTRA_INFO)) == null) {
            return 0;
        }
        return jSONObjectOptJSONObject.optInt(HardwareRenderer.OVERDRAW_PROPERTY_COUNT);
    }

    protected static CloudItemDetail c(JSONObject jSONObject) throws JSONException {
        CloudItemDetail cloudItemDetail = new CloudItemDetail(fx.a(jSONObject, Instrumentation.REPORT_KEY_IDENTIFIER), new LatLonPoint(jSONObject.optDouble("point_y"), jSONObject.optDouble("point_x")), fx.a(jSONObject, "title"), fx.a(jSONObject, "address"));
        cloudItemDetail.setCreatetime(fx.a(jSONObject, "gmt_create"));
        cloudItemDetail.setUpdatetime(fx.a(jSONObject, "gmt_modified"));
        if (jSONObject.has("_distance")) {
            String strOptString = jSONObject.optString("_distance");
            if (!c(strOptString)) {
                cloudItemDetail.setDistance(Integer.parseInt(strOptString));
            }
        }
        return cloudItemDetail;
    }

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg, com.amap.api.col.p0003sl.lb
    public Map<String, String> getRequestHead() {
        HashMap map = new HashMap();
        map.put("Content-Type", "application/x-www-form-urlencoded");
        map.put("Accept-Encoding", "gzip");
        map.put("User-Agent", "AMAP SDK Android Search 9.7.1");
        map.put("X-INFO", ij.b(this.e));
        map.put("platinfo", String.format("platform=Android&sdkversion=%s&product=%s", "9.7.1", "cloud"));
        map.put("logversion", "2.1");
        return map;
    }

    protected static void a(CloudItem cloudItem, JSONObject jSONObject) {
        Iterator<String> itKeys = jSONObject.keys();
        HashMap<String, String> map = new HashMap<>();
        if (itKeys == null) {
            return;
        }
        while (itKeys.hasNext()) {
            String next = itKeys.next();
            if (next != null) {
                map.put(next.toString(), jSONObject.optString(next.toString()));
            }
        }
        cloudItem.setCustomfield(map);
    }

    private static boolean c(String str) {
        return str == null || str.equals("") || str.equals("[]");
    }

    @Override // com.amap.api.col.p0003sl.fg
    protected final V a(byte[] bArr) throws AMapException {
        String str;
        try {
            str = new String(bArr, "utf-8");
        } catch (Exception e) {
            fp.a(e, "ProtocalHandler", "loadData");
            str = null;
        }
        if (str == null || str.equals("")) {
            return null;
        }
        fp.c(str);
        return a(str);
    }
}
