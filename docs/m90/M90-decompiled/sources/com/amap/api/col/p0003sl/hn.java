package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.amap.api.col.p0003sl.ga;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.interfaces.IGeocodeSearch;
import java.util.List;

/* JADX INFO: compiled from: GeocodeSearchCore.java */
/* JADX INFO: loaded from: classes2.dex */
public final class hn implements IGeocodeSearch {
    private Context a;
    private GeocodeSearch.OnGeocodeSearchListener b;
    private Handler c;

    public hn(Context context) throws AMapException {
        iq iqVarA = ip.a(context, fo.a(false));
        if (iqVarA.a != ip.c.SuccessCode) {
            throw new AMapException(iqVarA.b, 1, iqVarA.b, iqVarA.a.a());
        }
        this.a = context.getApplicationContext();
        this.c = ga.a();
    }

    @Override // com.amap.api.services.interfaces.IGeocodeSearch
    public final RegeocodeAddress getFromLocation(RegeocodeQuery regeocodeQuery) throws AMapException {
        try {
            fy.a(this.a);
            if (!a(regeocodeQuery)) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            return new gs(this.a, regeocodeQuery).d();
        } catch (AMapException e) {
            fp.a(e, "GeocodeSearch", "getFromLocationAsyn");
            throw e;
        }
    }

    @Override // com.amap.api.services.interfaces.IGeocodeSearch
    public final List<GeocodeAddress> getFromLocationName(GeocodeQuery geocodeQuery) throws AMapException {
        try {
            fy.a(this.a);
            if (geocodeQuery == null) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            return new fv(this.a, geocodeQuery).d();
        } catch (AMapException e) {
            fp.a(e, "GeocodeSearch", "getFromLocationName");
            throw e;
        }
    }

    @Override // com.amap.api.services.interfaces.IGeocodeSearch
    public final void setOnGeocodeSearchListener(GeocodeSearch.OnGeocodeSearchListener onGeocodeSearchListener) {
        this.b = onGeocodeSearchListener;
    }

    @Override // com.amap.api.services.interfaces.IGeocodeSearch
    public final void getFromLocationAsyn(final RegeocodeQuery regeocodeQuery) {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hn.1
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    try {
                        try {
                            messageObtainMessage.arg1 = 2;
                            messageObtainMessage.what = 201;
                            ga.l lVar = new ga.l();
                            lVar.b = hn.this.b;
                            messageObtainMessage.obj = lVar;
                            lVar.a = new RegeocodeResult(regeocodeQuery, hn.this.getFromLocation(regeocodeQuery));
                            messageObtainMessage.arg2 = 1000;
                        } catch (AMapException e) {
                            messageObtainMessage.arg2 = e.getErrorCode();
                        }
                    } finally {
                        hn.this.c.sendMessage(messageObtainMessage);
                    }
                }
            });
        } catch (Throwable th) {
            fp.a(th, "GeocodeSearch", "getFromLocationAsyn_threadcreate");
        }
    }

    @Override // com.amap.api.services.interfaces.IGeocodeSearch
    public final void getFromLocationNameAsyn(final GeocodeQuery geocodeQuery) {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hn.2
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    try {
                        try {
                            messageObtainMessage.what = 200;
                            messageObtainMessage.arg1 = 2;
                            messageObtainMessage.arg2 = 1000;
                            ga.f fVar = new ga.f();
                            fVar.b = hn.this.b;
                            messageObtainMessage.obj = fVar;
                            fVar.a = new GeocodeResult(geocodeQuery, hn.this.getFromLocationName(geocodeQuery));
                        } catch (AMapException e) {
                            messageObtainMessage.arg2 = e.getErrorCode();
                        }
                    } finally {
                        hn.this.c.sendMessage(messageObtainMessage);
                    }
                }
            });
        } catch (Throwable th) {
            fp.a(th, "GeocodeSearch", "getFromLocationNameAsynThrowable");
        }
    }

    private static boolean a(RegeocodeQuery regeocodeQuery) {
        return (regeocodeQuery == null || regeocodeQuery.getPoint() == null || regeocodeQuery.getLatLonType() == null) ? false : true;
    }
}
