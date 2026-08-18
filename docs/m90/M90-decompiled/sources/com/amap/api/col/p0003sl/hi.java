package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.amap.api.col.p0003sl.ga;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusLineSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.interfaces.IBusLineSearch;
import java.util.ArrayList;

/* JADX INFO: compiled from: BusLineSearchCore.java */
/* JADX INFO: loaded from: classes2.dex */
public final class hi implements IBusLineSearch {
    private Context a;
    private BusLineSearch.OnBusLineSearchListener b;
    private BusLineQuery c;
    private BusLineQuery d;
    private int e;
    private ArrayList<BusLineResult> f = new ArrayList<>();
    private Handler g;

    public hi(Context context, BusLineQuery busLineQuery) throws AMapException {
        this.g = null;
        iq iqVarA = ip.a(context, fo.a(false));
        if (iqVarA.a != ip.c.SuccessCode) {
            throw new AMapException(iqVarA.b, 1, iqVarA.b, iqVarA.a.a());
        }
        this.a = context.getApplicationContext();
        this.c = busLineQuery;
        if (busLineQuery != null) {
            this.d = busLineQuery.m47clone();
        }
        this.g = ga.a();
    }

    @Override // com.amap.api.services.interfaces.IBusLineSearch
    public final BusLineResult searchBusLine() throws AMapException {
        try {
            fy.a(this.a);
            if (this.d == null || !a()) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if (!this.c.weakEquals(this.d)) {
                this.d = this.c.m47clone();
                this.e = 0;
                ArrayList<BusLineResult> arrayList = this.f;
                if (arrayList != null) {
                    arrayList.clear();
                }
            }
            if (this.e == 0) {
                BusLineResult busLineResult = (BusLineResult) new fk(this.a, this.c.m47clone()).d();
                a(busLineResult);
                return busLineResult;
            }
            BusLineResult busLineResultB = b(this.c.getPageNumber());
            if (busLineResultB != null) {
                return busLineResultB;
            }
            BusLineResult busLineResult2 = (BusLineResult) new fk(this.a, this.c).d();
            this.f.set(this.c.getPageNumber(), busLineResult2);
            return busLineResult2;
        } catch (AMapException e) {
            fp.a(e, "BusLineSearch", "searchBusLine");
            throw new AMapException(e.getErrorMessage());
        }
    }

    private void a(BusLineResult busLineResult) {
        int i;
        this.f = new ArrayList<>();
        int i2 = 0;
        while (true) {
            i = this.e;
            if (i2 >= i) {
                break;
            }
            this.f.add(null);
            i2++;
        }
        if (i < 0 || !a(this.c.getPageNumber())) {
            return;
        }
        this.f.set(this.c.getPageNumber(), busLineResult);
    }

    private boolean a(int i) {
        return i < this.e && i >= 0;
    }

    private BusLineResult b(int i) {
        if (!a(i)) {
            throw new IllegalArgumentException("page out of range");
        }
        return this.f.get(i);
    }

    @Override // com.amap.api.services.interfaces.IBusLineSearch
    public final void setOnBusLineSearchListener(BusLineSearch.OnBusLineSearchListener onBusLineSearchListener) {
        this.b = onBusLineSearchListener;
    }

    @Override // com.amap.api.services.interfaces.IBusLineSearch
    public final void searchBusLineAsyn() {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hi.1
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    try {
                        try {
                            messageObtainMessage.arg1 = 3;
                            messageObtainMessage.what = 1000;
                            ga.b bVar = new ga.b();
                            messageObtainMessage.obj = bVar;
                            bVar.b = hi.this.b;
                            bVar.a = hi.this.searchBusLine();
                        } catch (AMapException e) {
                            messageObtainMessage.what = e.getErrorCode();
                        }
                    } finally {
                        hi.this.g.sendMessage(messageObtainMessage);
                    }
                }
            });
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override // com.amap.api.services.interfaces.IBusLineSearch
    public final void setQuery(BusLineQuery busLineQuery) {
        if (this.c.weakEquals(busLineQuery)) {
            return;
        }
        this.c = busLineQuery;
        this.d = busLineQuery.m47clone();
    }

    @Override // com.amap.api.services.interfaces.IBusLineSearch
    public final BusLineQuery getQuery() {
        return this.c;
    }

    private boolean a() {
        BusLineQuery busLineQuery = this.c;
        return (busLineQuery == null || fp.a(busLineQuery.getQueryString())) ? false : true;
    }
}
