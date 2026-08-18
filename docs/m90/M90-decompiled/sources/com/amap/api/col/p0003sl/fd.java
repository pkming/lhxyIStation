package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import com.amap.api.services.auto.AutoTChargeStationResult;
import com.amap.api.services.auto.AutoTSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import java.util.HashMap;
import org.json.JSONObject;

/* JADX INFO: compiled from: AutoTPoiSearchHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fd extends fh<AutoTSearch.Query, AutoTChargeStationResult> {
    private fc g;

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final /* synthetic */ Object a(String str) throws AMapException {
        return c(str);
    }

    public fd(Context context, AutoTSearch.Query query) {
        super(context, query);
        this.g = null;
        this.g = new fc(context);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuilder sb = new StringBuilder(this.g.a());
        String adCode = ((AutoTSearch.Query) this.b).getAdCode();
        if (!TextUtils.isEmpty(adCode)) {
            sb.append("&adcode=").append(b(adCode));
        }
        String city = ((AutoTSearch.Query) this.b).getCity();
        if (!TextUtils.isEmpty(city)) {
            sb.append("&city=").append(b(city));
        }
        String dataType = ((AutoTSearch.Query) this.b).getDataType();
        if (!TextUtils.isEmpty(dataType)) {
            sb.append("&data_type=").append(b(dataType));
        }
        String geoObj = ((AutoTSearch.Query) this.b).getGeoObj();
        if (!TextUtils.isEmpty(geoObj)) {
            sb.append("&geoobj=").append(b(geoObj));
        }
        String keywords = ((AutoTSearch.Query) this.b).getKeywords();
        if (!TextUtils.isEmpty(keywords)) {
            sb.append("&keywords=").append(b(keywords));
        }
        sb.append("&pagenum=").append(((AutoTSearch.Query) this.b).getPageNum());
        sb.append("&pagesize=").append(((AutoTSearch.Query) this.b).getPageSize());
        sb.append("&qii=").append(((AutoTSearch.Query) this.b).isQii());
        String queryType = ((AutoTSearch.Query) this.b).getQueryType();
        if (!TextUtils.isEmpty(queryType)) {
            sb.append("&query_type=").append(b(queryType));
        }
        sb.append("&range=").append(((AutoTSearch.Query) this.b).getRange());
        LatLonPoint latLonPoint = ((AutoTSearch.Query) this.b).getLatLonPoint();
        if (latLonPoint != null) {
            sb.append("&longitude=").append(latLonPoint.getLongitude());
            sb.append("&latitude=").append(latLonPoint.getLatitude());
        }
        String userLoc = ((AutoTSearch.Query) this.b).getUserLoc();
        if (!TextUtils.isEmpty(userLoc)) {
            sb.append("&user_loc=").append(b(userLoc));
        }
        String userCity = ((AutoTSearch.Query) this.b).getUserCity();
        if (!TextUtils.isEmpty(userCity)) {
            sb.append("&user_city=").append(b(userCity));
        }
        AutoTSearch.FilterBox filterBox = ((AutoTSearch.Query) this.b).getFilterBox();
        if (filterBox != null) {
            String retainState = filterBox.getRetainState();
            if (!TextUtils.isEmpty(retainState)) {
                sb.append("&retain_state=").append(b(retainState));
            }
            String checkedLevel = filterBox.getCheckedLevel();
            if (!TextUtils.isEmpty(checkedLevel)) {
                sb.append("&checked_level=").append(b(checkedLevel));
            }
            String classifyV2Data = filterBox.getClassifyV2Data();
            if (!TextUtils.isEmpty(classifyV2Data)) {
                sb.append("&classify_v2_data=").append(b(classifyV2Data));
            }
            String classifyV2Level2Data = filterBox.getClassifyV2Level2Data();
            if (!TextUtils.isEmpty(classifyV2Level2Data)) {
                sb.append("&classify_v2_level2_data=").append(b(classifyV2Level2Data));
            }
            String classifyV2Level3Data = filterBox.getClassifyV2Level3Data();
            if (!TextUtils.isEmpty(classifyV2Level3Data)) {
                sb.append("&classify_v2_level3_data=").append(b(classifyV2Level3Data));
            }
        }
        return sb.toString();
    }

    private static AutoTChargeStationResult c(String str) throws AMapException {
        try {
            return fe.a(new JSONObject(str));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        try {
            String strA = ff.a(new HashMap(), ((AutoTSearch.Query) this.b).getAccessKey());
            return fo.f() + "/ws/mapapi/poi/infolite/auto?" + strA + "&Signature=" + ff.a("POST", strA, ((AutoTSearch.Query) this.b).getSecretKey());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
