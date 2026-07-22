package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.interfaces.IDistanceSearch;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;

/* JADX INFO: compiled from: DistanceSearchCore.java */
/* JADX INFO: loaded from: classes2.dex */
public class hl implements IDistanceSearch {
    private static final String a = "hl";
    private Context b;
    private Handler c;
    private DistanceSearch.OnDistanceSearchListener d;

    public hl(Context context) throws AMapException {
        iq iqVarA = ip.a(context, fo.a(false));
        if (iqVarA.a != ip.c.SuccessCode) {
            throw new AMapException(iqVarA.b, 1, iqVarA.b, iqVarA.a.a());
        }
        this.b = context.getApplicationContext();
        this.c = ga.a();
    }

    @Override // com.amap.api.services.interfaces.IDistanceSearch
    public DistanceResult calculateRouteDistance(DistanceSearch.DistanceQuery distanceQuery) throws AMapException {
        try {
            fy.a(this.b);
            if (distanceQuery == null) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if (a(distanceQuery)) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            DistanceSearch.DistanceQuery distanceQueryM56clone = distanceQuery.m56clone();
            DistanceResult distanceResultD = new fq(this.b, distanceQueryM56clone).d();
            if (distanceResultD != null) {
                distanceResultD.setDistanceQuery(distanceQueryM56clone);
            }
            return distanceResultD;
        } catch (AMapException e) {
            fp.a(e, a, "calculateWalkRoute");
            throw e;
        }
    }

    private static boolean a(DistanceSearch.DistanceQuery distanceQuery) {
        return distanceQuery.getDestination() == null || distanceQuery.getOrigins() == null || distanceQuery.getOrigins().size() <= 0;
    }

    @Override // com.amap.api.services.interfaces.IDistanceSearch
    public void calculateRouteDistanceAsyn(final DistanceSearch.DistanceQuery distanceQuery) {
        gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hl.1
            @Override // java.lang.Runnable
            public final void run() {
                Message messageObtainMessage = ga.a().obtainMessage();
                messageObtainMessage.what = 400;
                messageObtainMessage.arg1 = 16;
                Bundle bundle = new Bundle();
                DistanceResult distanceResultCalculateRouteDistance = null;
                try {
                    try {
                        distanceResultCalculateRouteDistance = hl.this.calculateRouteDistance(distanceQuery);
                        bundle.putInt("errorCode", 1000);
                    } catch (AMapException e) {
                        bundle.putInt("errorCode", e.getErrorCode());
                    }
                } finally {
                    messageObtainMessage.obj = hl.this.d;
                    bundle.putParcelable("result", distanceResultCalculateRouteDistance);
                    messageObtainMessage.setData(bundle);
                    hl.this.c.sendMessage(messageObtainMessage);
                }
            }
        });
    }

    @Override // com.amap.api.services.interfaces.IDistanceSearch
    public void setDistanceSearchListener(DistanceSearch.OnDistanceSearchListener onDistanceSearchListener) {
        this.d = onDistanceSearchListener;
    }
}
