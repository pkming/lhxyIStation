package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.route.DriveRouteResultV2;
import com.amap.api.services.route.RouteSearchV2;

/* JADX INFO: compiled from: DriveRouteSearchHandlerV2.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fu extends fh<RouteSearchV2.DriveRouteQuery, DriveRouteResultV2> {
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final /* synthetic */ Object a(String str) throws AMapException {
        return c(str);
    }

    public fu(Context context, RouteSearchV2.DriveRouteQuery driveRouteQuery) {
        super(context, driveRouteQuery);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("key=").append(ig.f(this.e));
        if (((RouteSearchV2.DriveRouteQuery) this.b).getFromAndTo() != null) {
            stringBuffer.append("&origin=").append(fp.a(((RouteSearchV2.DriveRouteQuery) this.b).getFromAndTo().getFrom()));
            if (!fx.i(((RouteSearchV2.DriveRouteQuery) this.b).getFromAndTo().getStartPoiID())) {
                stringBuffer.append("&origin_id=").append(((RouteSearchV2.DriveRouteQuery) this.b).getFromAndTo().getStartPoiID());
            }
            stringBuffer.append("&destination=").append(fp.a(((RouteSearchV2.DriveRouteQuery) this.b).getFromAndTo().getTo()));
            if (!fx.i(((RouteSearchV2.DriveRouteQuery) this.b).getFromAndTo().getDestinationPoiID())) {
                stringBuffer.append("&destination_id=").append(((RouteSearchV2.DriveRouteQuery) this.b).getFromAndTo().getDestinationPoiID());
            }
            if (!fx.i(((RouteSearchV2.DriveRouteQuery) this.b).getFromAndTo().getOriginType())) {
                stringBuffer.append("&origin_type=").append(((RouteSearchV2.DriveRouteQuery) this.b).getFromAndTo().getOriginType());
            }
            if (!fx.i(((RouteSearchV2.DriveRouteQuery) this.b).getFromAndTo().getPlateNumber())) {
                stringBuffer.append("&plate=").append(((RouteSearchV2.DriveRouteQuery) this.b).getFromAndTo().getPlateNumber());
            }
        }
        stringBuffer.append("&strategy=").append(new StringBuilder().append(((RouteSearchV2.DriveRouteQuery) this.b).getMode().getValue()).toString());
        stringBuffer.append("&show_fields=").append(fp.a(((RouteSearchV2.DriveRouteQuery) this.b).getShowFields()));
        RouteSearchV2.NewEnergy newEnergy = ((RouteSearchV2.DriveRouteQuery) this.b).getNewEnergy();
        if (newEnergy != null) {
            stringBuffer.append(newEnergy.buildParam());
            stringBuffer.append("&force_new_version=true");
        }
        stringBuffer.append("&ferry=").append(!((RouteSearchV2.DriveRouteQuery) this.b).isUseFerry() ? 1 : 0);
        stringBuffer.append("&cartype=").append(new StringBuilder().append(((RouteSearchV2.DriveRouteQuery) this.b).getCarType()).toString());
        if (((RouteSearchV2.DriveRouteQuery) this.b).hasPassPoint()) {
            stringBuffer.append("&waypoints=").append(((RouteSearchV2.DriveRouteQuery) this.b).getPassedPointStr());
        }
        if (((RouteSearchV2.DriveRouteQuery) this.b).hasAvoidpolygons()) {
            stringBuffer.append("&avoidpolygons=").append(((RouteSearchV2.DriveRouteQuery) this.b).getAvoidpolygonsStr());
        }
        if (((RouteSearchV2.DriveRouteQuery) this.b).hasAvoidRoad()) {
            stringBuffer.append("&avoidroad=").append(b(((RouteSearchV2.DriveRouteQuery) this.b).getAvoidRoad()));
        }
        stringBuffer.append("&output=json");
        stringBuffer.append("&geometry=false");
        if (((RouteSearchV2.DriveRouteQuery) this.b).getExclude() != null) {
            stringBuffer.append("&exclude=").append(((RouteSearchV2.DriveRouteQuery) this.b).getExclude());
        }
        return stringBuffer.toString();
    }

    private static DriveRouteResultV2 c(String str) throws AMapException {
        return fx.d(str);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.c() + "/direction/driving?";
    }
}
