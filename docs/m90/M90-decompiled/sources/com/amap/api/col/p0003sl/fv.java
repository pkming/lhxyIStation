package com.amap.api.col.p0003sl;

import android.content.Context;
import android.view.HardwareRenderer;
import com.amap.api.col.p0003sl.go;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: GeocodingHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fv extends fh<GeocodeQuery, ArrayList<GeocodeAddress>> {
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final /* synthetic */ Object a(String str) throws AMapException {
        return c(str);
    }

    public fv(Context context, GeocodeQuery geocodeQuery) {
        super(context, geocodeQuery);
    }

    private static ArrayList<GeocodeAddress> c(String str) throws AMapException {
        ArrayList<GeocodeAddress> arrayList = new ArrayList<>();
        try {
            JSONObject jSONObject = new JSONObject(str);
            return (jSONObject.has(HardwareRenderer.OVERDRAW_PROPERTY_COUNT) && jSONObject.getInt(HardwareRenderer.OVERDRAW_PROPERTY_COUNT) > 0) ? fx.i(jSONObject) : arrayList;
        } catch (JSONException e) {
            fp.a(e, "GeocodingHandler", "paseJSONJSONException");
            return arrayList;
        } catch (Exception e2) {
            fp.a(e2, "GeocodingHandler", "paseJSONException");
            return arrayList;
        }
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.a() + "/geocode/geo?";
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("output=json&address=").append(b(((GeocodeQuery) this.b).getLocationName()));
        String city = ((GeocodeQuery) this.b).getCity();
        if (!fx.i(city)) {
            stringBuffer.append("&city=").append(b(city));
        }
        if (!fx.i(((GeocodeQuery) this.b).getCountry())) {
            stringBuffer.append("&country=").append(b(((GeocodeQuery) this.b).getCountry()));
        }
        stringBuffer.append("&key=" + ig.f(this.e));
        return stringBuffer.toString();
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
