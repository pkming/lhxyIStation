package com.amap.api.col.p0003sl;

import android.app.Instrumentation;
import android.content.Context;
import android.provider.MediaStore;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.core.AMapException;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import java.util.Hashtable;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: CloudSearchIdHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fm extends fl<gk, CloudItemDetail> {
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        return null;
    }

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    public final /* synthetic */ Object a(String str) throws AMapException {
        return c(str);
    }

    public fm(Context context, gk gkVar) {
        super(context, gkVar);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.e() + "/datasearch/id";
    }

    private static CloudItemDetail c(String str) throws AMapException {
        if (str == null || str.equals("")) {
            return null;
        }
        try {
            return d(new JSONObject(str));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    private static CloudItemDetail d(JSONObject jSONObject) throws JSONException {
        JSONArray jSONArrayA = a(jSONObject);
        if (jSONArrayA == null || jSONArrayA.length() <= 0) {
            return null;
        }
        JSONObject jSONObject2 = jSONArrayA.getJSONObject(0);
        CloudItemDetail cloudItemDetailC = c(jSONObject2);
        a(cloudItemDetailC, jSONObject2);
        return cloudItemDetailC;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg, com.amap.api.col.p0003sl.lb
    public final Map<String, String> getParams() {
        Hashtable hashtable = new Hashtable(16);
        hashtable.put("key", ig.f(this.e));
        hashtable.put("layerId", ((gk) this.b).a);
        hashtable.put(MediaStore.EXTRA_OUTPUT, "json");
        hashtable.put(Instrumentation.REPORT_KEY_IDENTIFIER, ((gk) this.b).b);
        String strA = ij.a();
        String strA2 = ij.a(this.e, strA, it.b(hashtable));
        hashtable.put(SPUserInfoUtils.TS, strA);
        hashtable.put("scode", strA2);
        return hashtable;
    }
}
