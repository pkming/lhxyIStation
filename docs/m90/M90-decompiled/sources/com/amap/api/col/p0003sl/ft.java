package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;

/* JADX INFO: compiled from: DriveRouteSearchHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ft extends fh<RouteSearch.DriveRouteQuery, DriveRouteResult> {
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final /* synthetic */ Object a(String str) throws AMapException {
        return c(str);
    }

    public ft(Context context, RouteSearch.DriveRouteQuery driveRouteQuery) {
        super(context, driveRouteQuery);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("key=").append(ig.f(this.e));
        if (((RouteSearch.DriveRouteQuery) this.b).getFromAndTo() != null) {
            stringBuffer.append("&origin=").append(fp.a(((RouteSearch.DriveRouteQuery) this.b).getFromAndTo().getFrom()));
            if (!fx.i(((RouteSearch.DriveRouteQuery) this.b).getFromAndTo().getStartPoiID())) {
                stringBuffer.append("&originid=").append(((RouteSearch.DriveRouteQuery) this.b).getFromAndTo().getStartPoiID());
            }
            stringBuffer.append("&destination=").append(fp.a(((RouteSearch.DriveRouteQuery) this.b).getFromAndTo().getTo()));
            if (!fx.i(((RouteSearch.DriveRouteQuery) this.b).getFromAndTo().getDestinationPoiID())) {
                stringBuffer.append("&destinationid=").append(((RouteSearch.DriveRouteQuery) this.b).getFromAndTo().getDestinationPoiID());
            }
            if (!fx.i(((RouteSearch.DriveRouteQuery) this.b).getFromAndTo().getOriginType())) {
                stringBuffer.append("&origintype=").append(((RouteSearch.DriveRouteQuery) this.b).getFromAndTo().getOriginType());
            }
            if (!fx.i(((RouteSearch.DriveRouteQuery) this.b).getFromAndTo().getDestinationType())) {
                stringBuffer.append("&destinationtype=").append(((RouteSearch.DriveRouteQuery) this.b).getFromAndTo().getDestinationType());
            }
            if (!fx.i(((RouteSearch.DriveRouteQuery) this.b).getFromAndTo().getPlateProvince())) {
                stringBuffer.append("&province=").append(((RouteSearch.DriveRouteQuery) this.b).getFromAndTo().getPlateProvince());
            }
            if (!fx.i(((RouteSearch.DriveRouteQuery) this.b).getFromAndTo().getPlateNumber())) {
                stringBuffer.append("&number=").append(((RouteSearch.DriveRouteQuery) this.b).getFromAndTo().getPlateNumber());
            }
        }
        stringBuffer.append("&strategy=").append(new StringBuilder().append(((RouteSearch.DriveRouteQuery) this.b).getMode()).toString());
        if (!TextUtils.isEmpty(((RouteSearch.DriveRouteQuery) this.b).getExtensions())) {
            stringBuffer.append("&extensions=").append(((RouteSearch.DriveRouteQuery) this.b).getExtensions());
        } else {
            stringBuffer.append("&extensions=base");
        }
        stringBuffer.append("&ferry=").append(!((RouteSearch.DriveRouteQuery) this.b).isUseFerry() ? 1 : 0);
        stringBuffer.append("&cartype=").append(new StringBuilder().append(((RouteSearch.DriveRouteQuery) this.b).getCarType()).toString());
        if (((RouteSearch.DriveRouteQuery) this.b).hasPassPoint()) {
            stringBuffer.append("&waypoints=").append(((RouteSearch.DriveRouteQuery) this.b).getPassedPointStr());
        }
        if (((RouteSearch.DriveRouteQuery) this.b).hasAvoidpolygons()) {
            stringBuffer.append("&avoidpolygons=").append(((RouteSearch.DriveRouteQuery) this.b).getAvoidpolygonsStr());
        }
        if (((RouteSearch.DriveRouteQuery) this.b).hasAvoidRoad()) {
            stringBuffer.append("&avoidroad=").append(b(((RouteSearch.DriveRouteQuery) this.b).getAvoidRoad()));
        }
        stringBuffer.append("&output=json");
        stringBuffer.append("&geometry=false");
        if (((RouteSearch.DriveRouteQuery) this.b).getExclude() != null) {
            stringBuffer.append("&exclude=").append(((RouteSearch.DriveRouteQuery) this.b).getExclude());
        }
        return stringBuffer.toString();
    }

    private static DriveRouteResult c(String str) throws AMapException {
        return fx.c(str);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.a() + "/direction/driving?";
    }
}
