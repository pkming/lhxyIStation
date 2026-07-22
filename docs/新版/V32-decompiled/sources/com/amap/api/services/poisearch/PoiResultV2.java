package com.amap.api.services.poisearch;

import com.amap.api.services.core.PoiItemV2;
import com.amap.api.services.poisearch.PoiSearchV2;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class PoiResultV2 {
    private int a;
    private ArrayList<PoiItemV2> b;
    private PoiSearchV2.Query c;
    private PoiSearchV2.SearchBound d;

    public static PoiResultV2 createPagedResult(PoiSearchV2.Query query, PoiSearchV2.SearchBound searchBound, int i, ArrayList<PoiItemV2> arrayList) {
        return new PoiResultV2(query, searchBound, i, arrayList);
    }

    private PoiResultV2(PoiSearchV2.Query query, PoiSearchV2.SearchBound searchBound, int i, ArrayList<PoiItemV2> arrayList) {
        this.b = new ArrayList<>();
        this.c = query;
        this.d = searchBound;
        this.a = i;
        this.b = arrayList;
    }

    public int getCount() {
        return this.a;
    }

    public PoiSearchV2.Query getQuery() {
        return this.c;
    }

    public PoiSearchV2.SearchBound getBound() {
        return this.d;
    }

    public ArrayList<PoiItemV2> getPois() {
        return this.b;
    }
}
