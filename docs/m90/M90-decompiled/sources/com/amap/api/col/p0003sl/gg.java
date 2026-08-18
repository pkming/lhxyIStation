package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.go;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItemV2;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.poisearch.PoiSearchV2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: PoiSearchIdHandlerV2.java */
/* JADX INFO: loaded from: classes2.dex */
public final class gg extends ge<String, PoiItemV2> {
    private PoiSearchV2.Query g;

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    public final /* synthetic */ Object a(String str) throws AMapException {
        return d(str);
    }

    public gg(Context context, String str, PoiSearchV2.Query query) {
        super(context, str);
        this.g = null;
        this.g = query;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.c() + "/place/detail?";
    }

    private static PoiItemV2 d(String str) throws AMapException {
        try {
            return a(new JSONObject(str));
        } catch (JSONException e) {
            fp.a(e, "PoiSearchIdHandlerV2", "paseJSONJSONException");
            return null;
        } catch (Exception e2) {
            fp.a(e2, "PoiSearchIdHandlerV2", "paseJSONException");
            return null;
        }
    }

    private static PoiItemV2 a(JSONObject jSONObject) throws JSONException {
        JSONObject jSONObjectOptJSONObject;
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("pois");
        if (jSONArrayOptJSONArray == null || jSONArrayOptJSONArray.length() <= 0 || (jSONObjectOptJSONObject = jSONArrayOptJSONArray.optJSONObject(0)) == null) {
            return null;
        }
        return fx.f(jSONObjectOptJSONObject);
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
        PoiSearchV2.Query query = this.g;
        String strA = (query == null || query.getShowFields() == null) ? null : a(this.g.getShowFields());
        if (strA != null) {
            sb.append("&show_fields=").append(strA);
        }
        sb.append("&key=" + ig.f(this.e));
        String channel = this.g.getChannel();
        if (!TextUtils.isEmpty(channel)) {
            sb.append("&channel=").append(channel);
        }
        String premium = this.g.getPremium();
        if (!TextUtils.isEmpty(premium)) {
            sb.append("&permium=").append(premium);
        }
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
