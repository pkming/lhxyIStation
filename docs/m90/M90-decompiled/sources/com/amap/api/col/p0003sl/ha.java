package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.TruckRouteRestult;

/* JADX INFO: compiled from: TruckRouteSearchHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ha extends fh<RouteSearch.TruckRouteQuery, TruckRouteRestult> {
    private final String g;
    private final String h;
    private final String i;

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final /* synthetic */ Object a(String str) throws AMapException {
        return c(str);
    }

    public ha(Context context, RouteSearch.TruckRouteQuery truckRouteQuery) {
        super(context, truckRouteQuery);
        this.g = "/direction/truck?";
        this.h = "|";
        this.i = ",";
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("key=").append(ig.f(this.e));
        if (((RouteSearch.TruckRouteQuery) this.b).getFromAndTo() != null) {
            stringBuffer.append("&origin=").append(fp.a(((RouteSearch.TruckRouteQuery) this.b).getFromAndTo().getFrom()));
            if (!fx.i(((RouteSearch.TruckRouteQuery) this.b).getFromAndTo().getStartPoiID())) {
                stringBuffer.append("&originid=").append(((RouteSearch.TruckRouteQuery) this.b).getFromAndTo().getStartPoiID());
            }
            stringBuffer.append("&destination=").append(fp.a(((RouteSearch.TruckRouteQuery) this.b).getFromAndTo().getTo()));
            if (!fx.i(((RouteSearch.TruckRouteQuery) this.b).getFromAndTo().getDestinationPoiID())) {
                stringBuffer.append("&destinationid=").append(((RouteSearch.TruckRouteQuery) this.b).getFromAndTo().getDestinationPoiID());
            }
            if (!fx.i(((RouteSearch.TruckRouteQuery) this.b).getFromAndTo().getOriginType())) {
                stringBuffer.append("&origintype=").append(((RouteSearch.TruckRouteQuery) this.b).getFromAndTo().getOriginType());
            }
            if (!fx.i(((RouteSearch.TruckRouteQuery) this.b).getFromAndTo().getDestinationType())) {
                stringBuffer.append("&destinationtype=").append(((RouteSearch.TruckRouteQuery) this.b).getFromAndTo().getDestinationType());
            }
            if (!fx.i(((RouteSearch.TruckRouteQuery) this.b).getFromAndTo().getPlateProvince())) {
                stringBuffer.append("&province=").append(((RouteSearch.TruckRouteQuery) this.b).getFromAndTo().getPlateProvince());
            }
            if (!fx.i(((RouteSearch.TruckRouteQuery) this.b).getFromAndTo().getPlateNumber())) {
                stringBuffer.append("&number=").append(((RouteSearch.TruckRouteQuery) this.b).getFromAndTo().getPlateNumber());
            }
        }
        stringBuffer.append("&strategy=").append(((RouteSearch.TruckRouteQuery) this.b).getMode());
        if (((RouteSearch.TruckRouteQuery) this.b).hasPassPoint()) {
            stringBuffer.append("&waypoints=").append(((RouteSearch.TruckRouteQuery) this.b).getPassedPointStr());
        }
        stringBuffer.append("&size=").append(((RouteSearch.TruckRouteQuery) this.b).getTruckSize());
        stringBuffer.append("&height=").append(((RouteSearch.TruckRouteQuery) this.b).getTruckHeight());
        stringBuffer.append("&width=").append(((RouteSearch.TruckRouteQuery) this.b).getTruckWidth());
        stringBuffer.append("&load=").append(((RouteSearch.TruckRouteQuery) this.b).getTruckLoad());
        stringBuffer.append("&weight=").append(((RouteSearch.TruckRouteQuery) this.b).getTruckWeight());
        stringBuffer.append("&axis=").append(((RouteSearch.TruckRouteQuery) this.b).getTruckAxis());
        if (!TextUtils.isEmpty(((RouteSearch.TruckRouteQuery) this.b).getExtensions())) {
            stringBuffer.append("&extensions=").append(((RouteSearch.TruckRouteQuery) this.b).getExtensions());
        } else {
            stringBuffer.append("&extensions=base");
        }
        stringBuffer.append("&output=json");
        return stringBuffer.toString();
    }

    private static TruckRouteRestult c(String str) throws AMapException {
        return fx.m(str);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.b() + "/direction/truck?";
    }
}
