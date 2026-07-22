package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.amap.api.col.p0003sl.ga;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.services.busline.BusStationQuery;
import com.amap.api.services.busline.BusStationResult;
import com.amap.api.services.busline.BusStationSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.interfaces.IBusStationSearch;
import java.util.ArrayList;

/* JADX INFO: compiled from: BusStationSearchCore.java */
/* JADX INFO: loaded from: classes2.dex */
public final class hj implements IBusStationSearch {
    private Context a;
    private BusStationSearch.OnBusStationSearchListener b;
    private BusStationQuery c;
    private BusStationQuery d;
    private ArrayList<BusStationResult> e = new ArrayList<>();
    private int f;
    private Handler g;

    public hj(Context context, BusStationQuery busStationQuery) throws AMapException {
        iq iqVarA = ip.a(context, fo.a(false));
        if (iqVarA.a != ip.c.SuccessCode) {
            throw new AMapException(iqVarA.b, 1, iqVarA.b, iqVarA.a.a());
        }
        this.a = context.getApplicationContext();
        this.c = busStationQuery;
        this.g = ga.a();
    }

    @Override // com.amap.api.services.interfaces.IBusStationSearch
    public final BusStationResult searchBusStation() throws AMapException {
        try {
            fy.a(this.a);
            if (!a()) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if (!this.c.weakEquals(this.d)) {
                this.d = this.c.m48clone();
                this.f = 0;
                ArrayList<BusStationResult> arrayList = this.e;
                if (arrayList != null) {
                    arrayList.clear();
                }
            }
            if (this.f == 0) {
                BusStationResult busStationResult = (BusStationResult) new fk(this.a, this.c).d();
                this.f = busStationResult.getPageCount();
                a(busStationResult);
                return busStationResult;
            }
            BusStationResult busStationResultB = b(this.c.getPageNumber());
            if (busStationResultB != null) {
                return busStationResultB;
            }
            BusStationResult busStationResult2 = (BusStationResult) new fk(this.a, this.c).d();
            this.e.set(this.c.getPageNumber(), busStationResult2);
            return busStationResult2;
        } catch (AMapException e) {
            fp.a(e, "BusStationSearch", "searchBusStation");
            throw new AMapException(e.getErrorMessage());
        } catch (Throwable th) {
            fp.a(th, "BusStationSearch", "searchBusStation");
            return null;
        }
    }

    private void a(BusStationResult busStationResult) {
        int i;
        this.e = new ArrayList<>();
        int i2 = 0;
        while (true) {
            i = this.f;
            if (i2 > i) {
                break;
            }
            this.e.add(null);
            i2++;
        }
        if (i > 0) {
            this.e.set(this.c.getPageNumber(), busStationResult);
        }
    }

    private boolean a(int i) {
        return i <= this.f && i >= 0;
    }

    private BusStationResult b(int i) {
        if (!a(i)) {
            throw new IllegalArgumentException("page out of range");
        }
        return this.e.get(i);
    }

    @Override // com.amap.api.services.interfaces.IBusStationSearch
    public final void setOnBusStationSearchListener(BusStationSearch.OnBusStationSearchListener onBusStationSearchListener) {
        this.b = onBusStationSearchListener;
    }

    @Override // com.amap.api.services.interfaces.IBusStationSearch
    public final void searchBusStationAsyn() {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hj.1
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    try {
                        try {
                            messageObtainMessage.arg1 = 7;
                            ga.c cVar = new ga.c();
                            cVar.b = hj.this.b;
                            messageObtainMessage.obj = cVar;
                            BusStationResult busStationResultSearchBusStation = hj.this.searchBusStation();
                            messageObtainMessage.what = 1000;
                            cVar.a = busStationResultSearchBusStation;
                        } catch (AMapException e) {
                            messageObtainMessage.what = e.getErrorCode();
                        }
                    } finally {
                        hj.this.g.sendMessage(messageObtainMessage);
                    }
                }
            });
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override // com.amap.api.services.interfaces.IBusStationSearch
    public final void setQuery(BusStationQuery busStationQuery) {
        if (busStationQuery.weakEquals(this.c)) {
            return;
        }
        this.c = busStationQuery;
    }

    @Override // com.amap.api.services.interfaces.IBusStationSearch
    public final BusStationQuery getQuery() {
        return this.c;
    }

    private boolean a() {
        BusStationQuery busStationQuery = this.c;
        return (busStationQuery == null || fp.a(busStationQuery.getQueryString())) ? false : true;
    }
}
