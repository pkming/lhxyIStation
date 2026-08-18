package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.nearby.NearbyInfo;
import com.amap.api.services.nearby.NearbySearch;
import com.amap.api.services.nearby.NearbySearchResult;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: NearbySearchHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class gc extends fh<NearbySearch.NearbyQuery, NearbySearchResult> {
    private Context g;
    private NearbySearch.NearbyQuery h;

    public gc(Context context, NearbySearch.NearbyQuery nearbyQuery) {
        super(context, nearbyQuery);
        this.g = context;
        this.h = nearbyQuery;
    }

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("key=").append(ig.f(this.g));
        LatLonPoint centerPoint = this.h.getCenterPoint();
        if (centerPoint != null) {
            stringBuffer.append("&center=").append(centerPoint.getLongitude()).append(",").append(centerPoint.getLatitude());
        }
        stringBuffer.append("&radius=").append(this.h.getRadius());
        stringBuffer.append("&limit=30");
        stringBuffer.append("&searchtype=").append(this.h.getType());
        stringBuffer.append("&timerange=").append(this.h.getTimeRange());
        return stringBuffer.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    /* JADX INFO: renamed from: c, reason: merged with bridge method [inline-methods] */
    public NearbySearchResult a(String str) throws AMapException {
        try {
            JSONObject jSONObject = new JSONObject(str);
            boolean z = true;
            if (this.h.getType() != 1) {
                z = false;
            }
            ArrayList<NearbyInfo> arrayListA = fx.a(jSONObject, z);
            NearbySearchResult nearbySearchResult = new NearbySearchResult();
            nearbySearchResult.setNearbyInfoList(arrayListA);
            return nearbySearchResult;
        } catch (JSONException e) {
            fp.a(e, "NearbySearchHandler", "paseJSON");
            return null;
        }
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.d() + "/nearby/around";
    }
}
