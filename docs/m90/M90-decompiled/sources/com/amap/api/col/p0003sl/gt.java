package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;

/* JADX INFO: compiled from: RideRouteSearchHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class gt extends fh<RouteSearch.RideRouteQuery, RideRouteResult> {
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final /* synthetic */ Object a(String str) throws AMapException {
        return c(str);
    }

    public gt(Context context, RouteSearch.RideRouteQuery rideRouteQuery) {
        super(context, rideRouteQuery);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("key=").append(ig.f(this.e));
        stringBuffer.append("&origin=").append(fp.a(((RouteSearch.RideRouteQuery) this.b).getFromAndTo().getFrom()));
        stringBuffer.append("&destination=").append(fp.a(((RouteSearch.RideRouteQuery) this.b).getFromAndTo().getTo()));
        stringBuffer.append("&output=json");
        stringBuffer.append("&geometry=false");
        if (!TextUtils.isEmpty(((RouteSearch.RideRouteQuery) this.b).getExtensions())) {
            stringBuffer.append("&extensions=").append(((RouteSearch.RideRouteQuery) this.b).getExtensions());
        } else {
            stringBuffer.append("&extensions=base");
        }
        return stringBuffer.toString();
    }

    private static RideRouteResult c(String str) throws AMapException {
        return fx.j(str);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.b() + "/direction/bicycling?";
    }
}
