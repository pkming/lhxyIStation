package com.amap.api.services.routepoisearch;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class RoutePOISearchResult {
    private List<RoutePOIItem> a;
    private RoutePOISearchQuery b;

    public RoutePOISearchResult(ArrayList<RoutePOIItem> arrayList, RoutePOISearchQuery routePOISearchQuery) {
        this.a = new ArrayList();
        this.a = arrayList;
        this.b = routePOISearchQuery;
    }

    public List<RoutePOIItem> getRoutePois() {
        return this.a;
    }

    public RoutePOISearchQuery getQuery() {
        return this.b;
    }
}
