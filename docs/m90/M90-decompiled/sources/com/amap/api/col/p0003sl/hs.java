package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.amap.api.col.p0003sl.ga;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.interfaces.IRoutePOISearch;
import com.amap.api.services.routepoisearch.RoutePOISearch;
import com.amap.api.services.routepoisearch.RoutePOISearchQuery;
import com.amap.api.services.routepoisearch.RoutePOISearchResult;

/* JADX INFO: compiled from: RoutePOISearchCore.java */
/* JADX INFO: loaded from: classes2.dex */
public final class hs implements IRoutePOISearch {
    private RoutePOISearchQuery a;
    private Context b;
    private RoutePOISearch.OnRoutePOISearchListener c;
    private Handler d;

    public hs(Context context, RoutePOISearchQuery routePOISearchQuery) throws AMapException {
        this.d = null;
        iq iqVarA = ip.a(context, fo.a(false));
        if (iqVarA.a != ip.c.SuccessCode) {
            throw new AMapException(iqVarA.b, 1, iqVarA.b, iqVarA.a.a());
        }
        this.b = context;
        this.a = routePOISearchQuery;
        this.d = ga.a();
    }

    @Override // com.amap.api.services.interfaces.IRoutePOISearch
    public final void setRoutePOISearchListener(RoutePOISearch.OnRoutePOISearchListener onRoutePOISearchListener) {
        this.c = onRoutePOISearchListener;
    }

    @Override // com.amap.api.services.interfaces.IRoutePOISearch
    public final void searchRoutePOIAsyn() {
        gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hs.1
            @Override // java.lang.Runnable
            public final void run() {
                ga.m mVar;
                Message messageObtainMessage = hs.this.d.obtainMessage();
                messageObtainMessage.arg1 = 14;
                Bundle bundle = new Bundle();
                RoutePOISearchResult routePOISearchResultSearchRoutePOI = null;
                try {
                    try {
                        routePOISearchResultSearchRoutePOI = hs.this.searchRoutePOI();
                        bundle.putInt("errorCode", 1000);
                        mVar = new ga.m();
                    } catch (AMapException e) {
                        bundle.putInt("errorCode", e.getErrorCode());
                        mVar = new ga.m();
                    }
                    mVar.b = hs.this.c;
                    mVar.a = routePOISearchResultSearchRoutePOI;
                    messageObtainMessage.obj = mVar;
                    messageObtainMessage.setData(bundle);
                    hs.this.d.sendMessage(messageObtainMessage);
                } catch (Throwable th) {
                    ga.m mVar2 = new ga.m();
                    mVar2.b = hs.this.c;
                    mVar2.a = routePOISearchResultSearchRoutePOI;
                    messageObtainMessage.obj = mVar2;
                    messageObtainMessage.setData(bundle);
                    hs.this.d.sendMessage(messageObtainMessage);
                    throw th;
                }
            }
        });
    }

    @Override // com.amap.api.services.interfaces.IRoutePOISearch
    public final void setQuery(RoutePOISearchQuery routePOISearchQuery) {
        this.a = routePOISearchQuery;
    }

    @Override // com.amap.api.services.interfaces.IRoutePOISearch
    public final RoutePOISearchQuery getQuery() {
        return this.a;
    }

    @Override // com.amap.api.services.interfaces.IRoutePOISearch
    public final RoutePOISearchResult searchRoutePOI() throws AMapException {
        try {
            fy.a(this.b);
            if (!a()) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            return new gv(this.b, this.a.m69clone()).d();
        } catch (AMapException e) {
            fp.a(e, "RoutePOISearchCore", "searchRoutePOI");
            throw e;
        }
    }

    private boolean a() {
        RoutePOISearchQuery routePOISearchQuery = this.a;
        if (routePOISearchQuery == null || routePOISearchQuery.getSearchType() == null) {
            return false;
        }
        return (this.a.getFrom() == null && this.a.getTo() == null && this.a.getPolylines() == null) ? false : true;
    }
}
