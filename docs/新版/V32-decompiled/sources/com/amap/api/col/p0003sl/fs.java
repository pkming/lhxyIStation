package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.route.DriveRoutePlanResult;
import com.amap.api.services.route.RouteSearch;

/* JADX INFO: compiled from: DrivePlanSearchHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fs extends fh<RouteSearch.DrivePlanQuery, DriveRoutePlanResult> {
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final /* synthetic */ Object a(String str) throws AMapException {
        return c(str);
    }

    public fs(Context context, RouteSearch.DrivePlanQuery drivePlanQuery) {
        super(context, drivePlanQuery);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("key=").append(ig.f(this.e));
        if (((RouteSearch.DrivePlanQuery) this.b).getFromAndTo() != null) {
            stringBuffer.append("&origin=").append(fp.a(((RouteSearch.DrivePlanQuery) this.b).getFromAndTo().getFrom()));
            if (!fx.i(((RouteSearch.DrivePlanQuery) this.b).getFromAndTo().getStartPoiID())) {
                stringBuffer.append("&originid=").append(((RouteSearch.DrivePlanQuery) this.b).getFromAndTo().getStartPoiID());
            }
            stringBuffer.append("&destination=").append(fp.a(((RouteSearch.DrivePlanQuery) this.b).getFromAndTo().getTo()));
            if (!fx.i(((RouteSearch.DrivePlanQuery) this.b).getFromAndTo().getDestinationPoiID())) {
                stringBuffer.append("&destinationid=").append(((RouteSearch.DrivePlanQuery) this.b).getFromAndTo().getDestinationPoiID());
            }
            if (!fx.i(((RouteSearch.DrivePlanQuery) this.b).getFromAndTo().getOriginType())) {
                stringBuffer.append("&origintype=").append(((RouteSearch.DrivePlanQuery) this.b).getFromAndTo().getOriginType());
            }
            if (!fx.i(((RouteSearch.DrivePlanQuery) this.b).getFromAndTo().getDestinationType())) {
                stringBuffer.append("&destinationtype=").append(((RouteSearch.DrivePlanQuery) this.b).getFromAndTo().getDestinationType());
            }
            if (!fx.i(((RouteSearch.DrivePlanQuery) this.b).getFromAndTo().getPlateProvince())) {
                stringBuffer.append("&province=").append(((RouteSearch.DrivePlanQuery) this.b).getFromAndTo().getPlateProvince());
            }
            if (!fx.i(((RouteSearch.DrivePlanQuery) this.b).getFromAndTo().getPlateNumber())) {
                stringBuffer.append("&number=").append(((RouteSearch.DrivePlanQuery) this.b).getFromAndTo().getPlateNumber());
            }
        }
        if (((RouteSearch.DrivePlanQuery) this.b).getDestParentPoiID() != null) {
            stringBuffer.append("&parentid=").append(((RouteSearch.DrivePlanQuery) this.b).getDestParentPoiID());
        }
        stringBuffer.append("&strategy=").append(new StringBuilder().append(((RouteSearch.DrivePlanQuery) this.b).getMode()).toString());
        stringBuffer.append("&cartype=").append(new StringBuilder().append(((RouteSearch.DrivePlanQuery) this.b).getCarType()).toString());
        stringBuffer.append("&firsttime=").append(new StringBuilder().append(((RouteSearch.DrivePlanQuery) this.b).getFirstTime()).toString());
        stringBuffer.append("&interval=").append(new StringBuilder().append(((RouteSearch.DrivePlanQuery) this.b).getInterval()).toString());
        stringBuffer.append("&count=").append(new StringBuilder().append(((RouteSearch.DrivePlanQuery) this.b).getCount()).toString());
        return stringBuffer.toString();
    }

    private static DriveRoutePlanResult c(String str) throws AMapException {
        return fx.n(str);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.b() + "/etd/driving?";
    }
}
