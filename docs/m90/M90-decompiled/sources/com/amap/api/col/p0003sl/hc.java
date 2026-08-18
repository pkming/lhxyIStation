package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.route.RouteSearchV2;
import com.amap.api.services.route.WalkRouteResultV2;

/* JADX INFO: compiled from: WalkRouteSearchHandlerV2.java */
/* JADX INFO: loaded from: classes2.dex */
public final class hc extends fh<RouteSearchV2.WalkRouteQuery, WalkRouteResultV2> {
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final /* synthetic */ Object a(String str) throws AMapException {
        return c(str);
    }

    public hc(Context context, RouteSearchV2.WalkRouteQuery walkRouteQuery) {
        super(context, walkRouteQuery);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("key=").append(ig.f(this.e));
        stringBuffer.append("&origin=").append(fp.a(((RouteSearchV2.WalkRouteQuery) this.b).getFromAndTo().getFrom()));
        stringBuffer.append("&destination=").append(fp.a(((RouteSearchV2.WalkRouteQuery) this.b).getFromAndTo().getTo()));
        stringBuffer.append("&output=json");
        stringBuffer.append("&isindoor=").append(((RouteSearchV2.WalkRouteQuery) this.b).isIndoor() ? 1 : 0);
        stringBuffer.append("&alternative_route=").append(((RouteSearchV2.WalkRouteQuery) this.b).getAlternativeRoute());
        stringBuffer.append("&show_fields=").append(fp.a(((RouteSearchV2.WalkRouteQuery) this.b).getShowFields()));
        return stringBuffer.toString();
    }

    private static WalkRouteResultV2 c(String str) throws AMapException {
        return fx.f(str);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.c() + "/direction/walking?";
    }
}
