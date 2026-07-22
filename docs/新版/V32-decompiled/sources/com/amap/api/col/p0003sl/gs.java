package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.go;
import com.amap.api.col.p0003sl.gq;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: ReverseGeocodingHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class gs extends fh<RegeocodeQuery, RegeocodeAddress> {
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final /* synthetic */ Object a(String str) throws AMapException {
        return c(str);
    }

    public gs(Context context, RegeocodeQuery regeocodeQuery) {
        super(context, regeocodeQuery);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.a() + "/geocode/regeo?";
    }

    private static RegeocodeAddress c(String str) throws AMapException {
        RegeocodeAddress regeocodeAddress = new RegeocodeAddress();
        try {
            JSONObject jSONObjectOptJSONObject = new JSONObject(str).optJSONObject("regeocode");
            if (jSONObjectOptJSONObject == null) {
                return regeocodeAddress;
            }
            regeocodeAddress.setFormatAddress(fx.a(jSONObjectOptJSONObject, "formatted_address"));
            JSONObject jSONObjectOptJSONObject2 = jSONObjectOptJSONObject.optJSONObject("addressComponent");
            if (jSONObjectOptJSONObject2 != null) {
                fx.a(jSONObjectOptJSONObject2, regeocodeAddress);
            }
            regeocodeAddress.setPois(fx.c(jSONObjectOptJSONObject));
            JSONArray jSONArrayOptJSONArray = jSONObjectOptJSONObject.optJSONArray("roads");
            if (jSONArrayOptJSONArray != null) {
                fx.b(jSONArrayOptJSONArray, regeocodeAddress);
            }
            JSONArray jSONArrayOptJSONArray2 = jSONObjectOptJSONObject.optJSONArray("roadinters");
            if (jSONArrayOptJSONArray2 != null) {
                fx.a(jSONArrayOptJSONArray2, regeocodeAddress);
            }
            JSONArray jSONArrayOptJSONArray3 = jSONObjectOptJSONObject.optJSONArray("aois");
            if (jSONArrayOptJSONArray3 != null) {
                fx.c(jSONArrayOptJSONArray3, regeocodeAddress);
            }
        } catch (JSONException e) {
            fp.a(e, "ReverseGeocodingHandler", "paseJSON");
        }
        return regeocodeAddress;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private String a(boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("output=json&location=");
        if (z) {
            sb.append(fp.a(((RegeocodeQuery) this.b).getPoint().getLongitude())).append(",").append(fp.a(((RegeocodeQuery) this.b).getPoint().getLatitude()));
        }
        if (!TextUtils.isEmpty(((RegeocodeQuery) this.b).getPoiType())) {
            sb.append("&poitype=").append(((RegeocodeQuery) this.b).getPoiType());
        }
        if (!TextUtils.isEmpty(((RegeocodeQuery) this.b).getMode())) {
            sb.append("&mode=").append(((RegeocodeQuery) this.b).getMode());
        }
        if (!TextUtils.isEmpty(((RegeocodeQuery) this.b).getExtensions())) {
            sb.append("&extensions=").append(((RegeocodeQuery) this.b).getExtensions());
        } else {
            sb.append("&extensions=base");
        }
        sb.append("&radius=").append((int) ((RegeocodeQuery) this.b).getRadius());
        sb.append("&coordsys=").append(((RegeocodeQuery) this.b).getLatLonType());
        sb.append("&key=").append(ig.f(this.e));
        return sb.toString();
    }

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        return a(true);
    }

    private static gq f() {
        gp gpVarA = go.a().a("regeo");
        if (gpVarA == null) {
            return null;
        }
        return (gq) gpVarA;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fg
    protected final go.b e() {
        gq gqVarF = f();
        double dA = gqVarF != null ? gqVarF.a() : 0.0d;
        go.b bVar = new go.b();
        StringBuilder sb = new StringBuilder();
        sb.append(getURL());
        sb.append(a(false));
        sb.append("language=").append(ServiceSettings.getInstance().getLanguage());
        bVar.a = sb.toString();
        if (this.b != 0 && ((RegeocodeQuery) this.b).getPoint() != null) {
            bVar.b = new gq.a(((RegeocodeQuery) this.b).getPoint().getLatitude(), ((RegeocodeQuery) this.b).getPoint().getLongitude(), dA);
        }
        return bVar;
    }
}
