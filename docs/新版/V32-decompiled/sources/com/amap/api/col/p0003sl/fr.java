package com.amap.api.col.p0003sl;

import android.content.Context;
import android.view.HardwareRenderer;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearchQuery;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: DistrictServerHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fr extends fh<DistrictSearchQuery, DistrictResult> {
    public fr(Context context, DistrictSearchQuery districtSearchQuery) {
        super(context, districtSearchQuery);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("output=json");
        stringBuffer.append("&page=").append(((DistrictSearchQuery) this.b).getPageNum());
        stringBuffer.append("&offset=").append(((DistrictSearchQuery) this.b).getPageSize());
        if (((DistrictSearchQuery) this.b).isShowBoundary()) {
            stringBuffer.append("&extensions=all");
        } else {
            stringBuffer.append("&extensions=base");
        }
        if (((DistrictSearchQuery) this.b).checkKeyWords()) {
            stringBuffer.append("&keywords=").append(b(((DistrictSearchQuery) this.b).getKeywords()));
        }
        stringBuffer.append("&key=" + ig.f(this.e));
        stringBuffer.append("&subdistrict=" + String.valueOf(((DistrictSearchQuery) this.b).getSubDistrict()));
        return stringBuffer.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    /* JADX INFO: renamed from: c, reason: merged with bridge method [inline-methods] */
    public DistrictResult a(String str) throws AMapException {
        ArrayList arrayList = new ArrayList();
        DistrictResult districtResult = new DistrictResult((DistrictSearchQuery) this.b, arrayList);
        try {
            JSONObject jSONObject = new JSONObject(str);
            districtResult.setPageCount(jSONObject.optInt(HardwareRenderer.OVERDRAW_PROPERTY_COUNT));
            JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("districts");
            if (jSONArrayOptJSONArray == null) {
                return districtResult;
            }
            fx.a(jSONArrayOptJSONArray, arrayList, null);
        } catch (JSONException e) {
            fp.a(e, "DistrictServerHandler", "paseJSONJSONException");
        } catch (Exception e2) {
            fp.a(e2, "DistrictServerHandler", "paseJSONException");
        }
        return districtResult;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.a() + "/config/district?";
    }
}
