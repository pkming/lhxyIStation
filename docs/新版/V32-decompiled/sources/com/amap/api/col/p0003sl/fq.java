package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import java.util.List;

/* JADX INFO: compiled from: DistanceSearchHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fq extends fh<DistanceSearch.DistanceQuery, DistanceResult> {
    private final String g;
    private final String h;
    private final String i;

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final /* synthetic */ Object a(String str) throws AMapException {
        return c(str);
    }

    public fq(Context context, DistanceSearch.DistanceQuery distanceQuery) {
        super(context, distanceQuery);
        this.g = "/distance?";
        this.h = "|";
        this.i = ",";
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("key=").append(ig.f(this.e));
        List<LatLonPoint> origins = ((DistanceSearch.DistanceQuery) this.b).getOrigins();
        if (origins != null && origins.size() > 0) {
            stringBuffer.append("&origins=");
            int size = origins.size();
            for (int i = 0; i < size; i++) {
                LatLonPoint latLonPoint = origins.get(i);
                if (latLonPoint != null) {
                    double dA = fp.a(latLonPoint.getLatitude());
                    stringBuffer.append(fp.a(latLonPoint.getLongitude()));
                    stringBuffer.append(",");
                    stringBuffer.append(dA);
                    if (i < size) {
                        stringBuffer.append("|");
                    }
                }
            }
        }
        LatLonPoint destination = ((DistanceSearch.DistanceQuery) this.b).getDestination();
        if (destination != null) {
            double dA2 = fp.a(destination.getLatitude());
            double dA3 = fp.a(destination.getLongitude());
            stringBuffer.append("&destination=");
            stringBuffer.append(dA3);
            stringBuffer.append(",");
            stringBuffer.append(dA2);
        }
        stringBuffer.append("&type=").append(((DistanceSearch.DistanceQuery) this.b).getType());
        if (!TextUtils.isEmpty(((DistanceSearch.DistanceQuery) this.b).getExtensions())) {
            stringBuffer.append("&extensions=").append(((DistanceSearch.DistanceQuery) this.b).getExtensions());
        } else {
            stringBuffer.append("&extensions=base");
        }
        stringBuffer.append("&output=json");
        if (((DistanceSearch.DistanceQuery) this.b).getType() == 1) {
            stringBuffer.append("&strategy=").append(((DistanceSearch.DistanceQuery) this.b).getMode());
        }
        return stringBuffer.toString();
    }

    private static DistanceResult c(String str) throws AMapException {
        return fx.l(str);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.a() + "/distance?";
    }
}
