package com.amap.api.services.interfaces;

import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusLineSearch;
import com.amap.api.services.core.AMapException;

/* JADX INFO: loaded from: classes2.dex */
public interface IBusLineSearch {
    BusLineQuery getQuery();

    BusLineResult searchBusLine() throws AMapException;

    void searchBusLineAsyn();

    void setOnBusLineSearchListener(BusLineSearch.OnBusLineSearchListener onBusLineSearchListener);

    void setQuery(BusLineQuery busLineQuery);
}
