package com.amap.api.services.routepoisearch;

import com.amap.api.col.p0003sl.fp;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.routepoisearch.RoutePOISearch;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class RoutePOISearchQuery implements Cloneable {
    private LatLonPoint a;
    private LatLonPoint b;
    private int c;
    private RoutePOISearch.RoutePOISearchType d;
    private int e;
    private List<LatLonPoint> f;
    private String g;

    public RoutePOISearchQuery(LatLonPoint latLonPoint, LatLonPoint latLonPoint2, int i, RoutePOISearch.RoutePOISearchType routePOISearchType, int i2) {
        this.e = 250;
        this.a = latLonPoint;
        this.b = latLonPoint2;
        this.c = i;
        this.d = routePOISearchType;
        this.e = i2;
    }

    public RoutePOISearchQuery(List<LatLonPoint> list, RoutePOISearch.RoutePOISearchType routePOISearchType, int i) {
        this.e = 250;
        this.f = list;
        this.d = routePOISearchType;
        this.e = i;
    }

    public LatLonPoint getFrom() {
        return this.a;
    }

    public LatLonPoint getTo() {
        return this.b;
    }

    public int getMode() {
        return this.c;
    }

    public RoutePOISearch.RoutePOISearchType getSearchType() {
        return this.d;
    }

    public int getRange() {
        return this.e;
    }

    public List<LatLonPoint> getPolylines() {
        return this.f;
    }

    public void setChannel(String str) {
        this.g = str;
    }

    public String getChannel() {
        return this.g;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public RoutePOISearchQuery m69clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            fp.a(e, "RoutePOISearchQuery", "RoutePOISearchQueryclone");
        }
        List<LatLonPoint> list = this.f;
        if (list != null && list.size() > 0) {
            RoutePOISearchQuery routePOISearchQuery = new RoutePOISearchQuery(this.f, this.d, this.e);
            routePOISearchQuery.setChannel(this.g);
            return routePOISearchQuery;
        }
        RoutePOISearchQuery routePOISearchQuery2 = new RoutePOISearchQuery(this.a, this.b, this.c, this.d, this.e);
        routePOISearchQuery2.setChannel(this.g);
        return routePOISearchQuery2;
    }
}
