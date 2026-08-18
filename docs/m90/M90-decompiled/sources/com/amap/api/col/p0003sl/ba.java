package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.col.p0003sl.ih;
import com.amap.api.maps.AMapException;
import java.util.Hashtable;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: compiled from: OfflineInitHandlerAbstract.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ba extends bw<String, az> {
    @Override // com.amap.api.col.p0003sl.bw
    protected final /* synthetic */ az a(JSONObject jSONObject) throws AMapException {
        return b(jSONObject);
    }

    public ba(Context context, String str) {
        super(context, str);
    }

    @Override // com.amap.api.col.p0003sl.bw
    protected final JSONObject a(ih.b bVar) {
        if (bVar == null || bVar.f == null) {
            return null;
        }
        return bVar.f.optJSONObject("016");
    }

    @Override // com.amap.api.col.p0003sl.bw
    protected final String a() {
        return "016";
    }

    @Override // com.amap.api.col.p0003sl.bw
    protected final Map<String, String> b() {
        Hashtable hashtable = new Hashtable(16);
        hashtable.put("mapver", this.a);
        return hashtable;
    }

    private static az b(JSONObject jSONObject) throws AMapException {
        az azVar = new az();
        try {
            String strOptString = jSONObject.optString("update", "");
            if (strOptString.equals("0")) {
                azVar.a(false);
            } else if (strOptString.equals("1")) {
                azVar.a(true);
            }
            azVar.a(jSONObject.optString("version", ""));
        } catch (Throwable th) {
            jw.c(th, "OfflineInitHandlerAbstract", "loadData parseJson");
        }
        return azVar;
    }
}
