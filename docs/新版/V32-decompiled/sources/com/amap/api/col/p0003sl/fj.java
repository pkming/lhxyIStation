package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.route.BusRouteResultV2;
import com.amap.api.services.route.RouteSearchV2;

/* JADX INFO: compiled from: BusRouteSearchHandlerV2.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fj extends fh<RouteSearchV2.BusRouteQuery, BusRouteResultV2> {
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final /* synthetic */ Object a(String str) throws AMapException {
        return c(str);
    }

    public fj(Context context, RouteSearchV2.BusRouteQuery busRouteQuery) {
        super(context, busRouteQuery);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("key=").append(ig.f(this.e));
        stringBuffer.append("&origin=").append(fp.a(((RouteSearchV2.BusRouteQuery) this.b).getFromAndTo().getFrom()));
        stringBuffer.append("&destination=").append(fp.a(((RouteSearchV2.BusRouteQuery) this.b).getFromAndTo().getTo()));
        String city = ((RouteSearchV2.BusRouteQuery) this.b).getCity();
        if (!fx.i(city)) {
            city = b(city);
            stringBuffer.append("&city1=").append(city);
        }
        if (!fx.i(((RouteSearchV2.BusRouteQuery) this.b).getCity())) {
            stringBuffer.append("&city2=").append(b(city));
        }
        stringBuffer.append("&strategy=").append(new StringBuilder().append(((RouteSearchV2.BusRouteQuery) this.b).getMode()).toString());
        stringBuffer.append("&nightflag=").append(((RouteSearchV2.BusRouteQuery) this.b).getNightFlag());
        stringBuffer.append("&show_fields=").append(fp.a(((RouteSearchV2.BusRouteQuery) this.b).getShowFields()));
        String originPoiId = ((RouteSearchV2.BusRouteQuery) this.b).getOriginPoiId();
        if (!TextUtils.isEmpty(originPoiId)) {
            stringBuffer.append("&originpoi=").append(originPoiId);
        }
        String destinationPoiId = ((RouteSearchV2.BusRouteQuery) this.b).getDestinationPoiId();
        if (!TextUtils.isEmpty(destinationPoiId)) {
            stringBuffer.append("&destinationpoi=").append(destinationPoiId);
        }
        String ad1 = ((RouteSearchV2.BusRouteQuery) this.b).getAd1();
        if (!TextUtils.isEmpty(ad1)) {
            stringBuffer.append("&ad1=").append(ad1);
        }
        String ad2 = ((RouteSearchV2.BusRouteQuery) this.b).getAd2();
        if (!TextUtils.isEmpty(ad2)) {
            stringBuffer.append("&ad2=").append(ad2);
        }
        String date = ((RouteSearchV2.BusRouteQuery) this.b).getDate();
        if (!TextUtils.isEmpty(date)) {
            stringBuffer.append("&date=").append(date);
        }
        String time = ((RouteSearchV2.BusRouteQuery) this.b).getTime();
        if (!TextUtils.isEmpty(time)) {
            stringBuffer.append("&time=").append(time);
        }
        stringBuffer.append("&AlternativeRoute=").append(((RouteSearchV2.BusRouteQuery) this.b).getAlternativeRoute());
        stringBuffer.append("&multiexport=").append(((RouteSearchV2.BusRouteQuery) this.b).getMultiExport());
        stringBuffer.append("&max_trans=").append(((RouteSearchV2.BusRouteQuery) this.b).getMaxTrans());
        stringBuffer.append("&output=json");
        return stringBuffer.toString();
    }

    private static BusRouteResultV2 c(String str) throws AMapException {
        return fx.b(str);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.c() + "/direction/transit/integrated?";
    }
}
