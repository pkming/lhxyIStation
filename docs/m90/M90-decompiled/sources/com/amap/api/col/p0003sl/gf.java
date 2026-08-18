package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.col.p0003sl.go;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.poisearch.PoiSearch;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: PoiSearchIdHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class gf extends ge<String, PoiItem> {
    private PoiSearch.Query g;

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    public final /* synthetic */ Object a(String str) throws AMapException {
        return d(str);
    }

    public gf(Context context, String str, PoiSearch.Query query) {
        super(context, str);
        this.g = null;
        this.g = query;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.a() + "/place/detail?";
    }

    private static PoiItem d(String str) throws AMapException {
        try {
            return a(new JSONObject(str));
        } catch (JSONException e) {
            fp.a(e, "PoiSearchIdHandler", "paseJSONJSONException");
            return null;
        } catch (Exception e2) {
            fp.a(e2, "PoiSearchIdHandler", "paseJSONException");
            return null;
        }
    }

    private static PoiItem a(JSONObject jSONObject) throws JSONException {
        JSONObject jSONObjectOptJSONObject;
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("pois");
        if (jSONArrayOptJSONArray == null || jSONArrayOptJSONArray.length() <= 0 || (jSONObjectOptJSONObject = jSONArrayOptJSONArray.optJSONObject(0)) == null) {
            return null;
        }
        return fx.e(jSONObjectOptJSONObject);
    }

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        return f();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private String f() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append((String) this.b);
        sb.append("&output=json");
        PoiSearch.Query query = this.g;
        if (query != null && !c(query.getExtensions())) {
            sb.append("&extensions=").append(this.g.getExtensions());
        } else {
            sb.append("&extensions=base");
        }
        sb.append("&children=1");
        sb.append("&key=" + ig.f(this.e));
        return sb.toString();
    }

    @Override // com.amap.api.col.p0003sl.fg
    protected final go.b e() {
        go.b bVar = new go.b();
        StringBuilder sb = new StringBuilder();
        sb.append(getURL());
        sb.append(c());
        sb.append("language=").append(ServiceSettings.getInstance().getLanguage());
        bVar.a = sb.toString();
        return bVar;
    }
}
