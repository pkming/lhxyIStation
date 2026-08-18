package com.amap.api.services.busline;

import com.amap.api.services.core.SuggestionCity;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public final class BusLineResult {
    private int a;
    private ArrayList<BusLineItem> b;
    private BusLineQuery c;
    private List<String> d;
    private List<SuggestionCity> e;

    public static BusLineResult createPagedResult(BusLineQuery busLineQuery, int i, List<SuggestionCity> list, List<String> list2, ArrayList<BusLineItem> arrayList) {
        return new BusLineResult(busLineQuery, i, list, list2, arrayList);
    }

    private BusLineResult(BusLineQuery busLineQuery, int i, List<SuggestionCity> list, List<String> list2, ArrayList<BusLineItem> arrayList) {
        this.b = new ArrayList<>();
        this.d = new ArrayList();
        this.e = new ArrayList();
        this.c = busLineQuery;
        this.a = a(i);
        this.e = list;
        this.d = list2;
        this.b = arrayList;
    }

    private int a(int i) {
        int pageSize = ((i + r0) - 1) / this.c.getPageSize();
        if (pageSize > 30) {
            return 30;
        }
        return pageSize;
    }

    public final int getPageCount() {
        return this.a;
    }

    public final BusLineQuery getQuery() {
        return this.c;
    }

    public final List<String> getSearchSuggestionKeywords() {
        return this.d;
    }

    public final List<SuggestionCity> getSearchSuggestionCities() {
        return this.e;
    }

    public final List<BusLineItem> getBusLines() {
        return this.b;
    }
}
