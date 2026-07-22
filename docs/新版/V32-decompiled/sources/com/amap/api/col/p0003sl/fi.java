package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.RouteSearch;

/* JADX INFO: compiled from: BusRouteSearchHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fi extends fh<RouteSearch.BusRouteQuery, BusRouteResult> {
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final /* synthetic */ Object a(String str) throws AMapException {
        return c(str);
    }

    public fi(Context context, RouteSearch.BusRouteQuery busRouteQuery) {
        super(context, busRouteQuery);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("key=").append(ig.f(this.e));
        stringBuffer.append("&origin=").append(fp.a(((RouteSearch.BusRouteQuery) this.b).getFromAndTo().getFrom()));
        stringBuffer.append("&destination=").append(fp.a(((RouteSearch.BusRouteQuery) this.b).getFromAndTo().getTo()));
        String city = ((RouteSearch.BusRouteQuery) this.b).getCity();
        if (!fx.i(city)) {
            city = b(city);
            stringBuffer.append("&city=").append(city);
        }
        if (!fx.i(((RouteSearch.BusRouteQuery) this.b).getCity())) {
            stringBuffer.append("&cityd=").append(b(city));
        }
        stringBuffer.append("&strategy=").append(new StringBuilder().append(((RouteSearch.BusRouteQuery) this.b).getMode()).toString());
        stringBuffer.append("&nightflag=").append(((RouteSearch.BusRouteQuery) this.b).getNightFlag());
        if (!TextUtils.isEmpty(((RouteSearch.BusRouteQuery) this.b).getExtensions())) {
            stringBuffer.append("&extensions=").append(((RouteSearch.BusRouteQuery) this.b).getExtensions());
        } else {
            stringBuffer.append("&extensions=base");
        }
        stringBuffer.append("&output=json");
        return stringBuffer.toString();
    }

    private static BusRouteResult c(String str) throws AMapException {
        return fx.a(str);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.a() + "/direction/transit/integrated?";
    }
}
