package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.route.RideRouteResultV2;
import com.amap.api.services.route.RouteSearchV2;

/* JADX INFO: compiled from: RideRouteSearchHandlerV2.java */
/* JADX INFO: loaded from: classes2.dex */
public final class gu extends fh<RouteSearchV2.RideRouteQuery, RideRouteResultV2> {
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final /* synthetic */ Object a(String str) throws AMapException {
        return c(str);
    }

    public gu(Context context, RouteSearchV2.RideRouteQuery rideRouteQuery) {
        super(context, rideRouteQuery);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("key=").append(ig.f(this.e));
        stringBuffer.append("&origin=").append(fp.a(((RouteSearchV2.RideRouteQuery) this.b).getFromAndTo().getFrom()));
        stringBuffer.append("&destination=").append(fp.a(((RouteSearchV2.RideRouteQuery) this.b).getFromAndTo().getTo()));
        stringBuffer.append("&alternative_route=").append(((RouteSearchV2.RideRouteQuery) this.b).getAlternativeRoute());
        stringBuffer.append("&output=json");
        stringBuffer.append("&show_fields=").append(fp.a(((RouteSearchV2.RideRouteQuery) this.b).getShowFields()));
        return stringBuffer.toString();
    }

    private static RideRouteResultV2 c(String str) throws AMapException {
        return fx.k(str);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.c() + "/direction/bicycling?";
    }
}
