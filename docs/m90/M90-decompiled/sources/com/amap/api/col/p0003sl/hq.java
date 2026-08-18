package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.amap.api.col.p0003sl.ga;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.interfaces.IPoiSearch;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: compiled from: PoiSearchCore.java */
/* JADX INFO: loaded from: classes2.dex */
public final class hq implements IPoiSearch {
    private static HashMap<Integer, PoiResult> i;
    private PoiSearch.SearchBound a;
    private PoiSearch.Query b;
    private Context c;
    private PoiSearch.OnPoiSearchListener d;
    private String e = "zh-CN";
    private PoiSearch.Query f;
    private PoiSearch.SearchBound g;
    private int h;
    private Handler j;

    public hq(Context context, PoiSearch.Query query) throws AMapException {
        this.j = null;
        iq iqVarA = ip.a(context, fo.a(false));
        if (iqVarA.a != ip.c.SuccessCode) {
            throw new AMapException(iqVarA.b, 1, iqVarA.b, iqVarA.a.a());
        }
        this.c = context.getApplicationContext();
        setQuery(query);
        this.j = ga.a();
    }

    @Override // com.amap.api.services.interfaces.IPoiSearch
    public final void setOnPoiSearchListener(PoiSearch.OnPoiSearchListener onPoiSearchListener) {
        this.d = onPoiSearchListener;
    }

    @Override // com.amap.api.services.interfaces.IPoiSearch
    public final void setLanguage(String str) {
        if ("en".equals(str)) {
            this.e = "en";
        } else {
            this.e = "zh-CN";
        }
    }

    @Override // com.amap.api.services.interfaces.IPoiSearch
    public final String getLanguage() {
        return this.e;
    }

    private boolean a() {
        PoiSearch.Query query = this.b;
        if (query == null) {
            return false;
        }
        return (fp.a(query.getQueryString()) && fp.a(this.b.getCategory())) ? false : true;
    }

    private boolean b() {
        PoiSearch.SearchBound bound = getBound();
        return bound != null && bound.getShape().equals("Bound");
    }

    private boolean c() {
        PoiSearch.SearchBound bound = getBound();
        if (bound == null) {
            return true;
        }
        if (bound.getShape().equals("Bound")) {
            return bound.getCenter() != null;
        }
        if (bound.getShape().equals("Polygon")) {
            List<LatLonPoint> polyGonList = bound.getPolyGonList();
            if (polyGonList == null || polyGonList.size() == 0) {
                return false;
            }
            for (int i2 = 0; i2 < polyGonList.size(); i2++) {
                if (polyGonList.get(i2) == null) {
                    return false;
                }
            }
            return true;
        }
        if (!bound.getShape().equals("Rectangle")) {
            return true;
        }
        LatLonPoint lowerLeft = bound.getLowerLeft();
        LatLonPoint upperRight = bound.getUpperRight();
        return lowerLeft != null && upperRight != null && lowerLeft.getLatitude() < upperRight.getLatitude() && lowerLeft.getLongitude() < upperRight.getLongitude();
    }

    @Override // com.amap.api.services.interfaces.IPoiSearch
    public final PoiResult searchPOI() throws AMapException {
        try {
            fy.a(this.c);
            if (!b() && !a()) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if (!c()) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            PoiSearch.Query query = this.b;
            if (query == null) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if ((!query.queryEquals(this.f) && this.a == null) || (!this.b.queryEquals(this.f) && !this.a.equals(this.g))) {
                this.h = 0;
                this.f = this.b.m52clone();
                PoiSearch.SearchBound searchBound = this.a;
                if (searchBound != null) {
                    this.g = searchBound.m53clone();
                }
                HashMap<Integer, PoiResult> map = i;
                if (map != null) {
                    map.clear();
                }
            }
            PoiSearch.SearchBound searchBound2 = this.a;
            PoiSearch.SearchBound searchBoundClone = searchBound2 != null ? searchBound2.m53clone() : null;
            gr.a().a(this.b.getQueryString());
            this.b.setPageNum(gr.a().k(this.b.getPageNum()));
            this.b.setPageSize(gr.a().l(this.b.getPageSize()));
            if (this.h == 0) {
                PoiResult poiResultD = new gh(this.c, new gl(this.b.m52clone(), searchBoundClone)).d();
                a(poiResultD);
                return poiResultD;
            }
            PoiResult poiResultA = a(this.b.getPageNum());
            if (poiResultA != null) {
                return poiResultA;
            }
            PoiResult poiResultD2 = new gh(this.c, new gl(this.b.m52clone(), searchBoundClone)).d();
            i.put(Integer.valueOf(this.b.getPageNum()), poiResultD2);
            return poiResultD2;
        } catch (AMapException e) {
            fp.a(e, "PoiSearch", "searchPOI");
            throw new AMapException(e.getErrorMessage());
        }
    }

    @Override // com.amap.api.services.interfaces.IPoiSearch
    public final void searchPOIAsyn() {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hq.1
                @Override // java.lang.Runnable
                public final void run() {
                    ga.j jVar;
                    Message messageObtainMessage = hq.this.j.obtainMessage();
                    messageObtainMessage.arg1 = 6;
                    messageObtainMessage.what = 600;
                    Bundle bundle = new Bundle();
                    PoiResult poiResultSearchPOI = null;
                    try {
                        try {
                            poiResultSearchPOI = hq.this.searchPOI();
                            bundle.putInt("errorCode", 1000);
                            jVar = new ga.j();
                        } catch (AMapException e) {
                            bundle.putInt("errorCode", e.getErrorCode());
                            jVar = new ga.j();
                        }
                        jVar.b = hq.this.d;
                        jVar.a = poiResultSearchPOI;
                        messageObtainMessage.obj = jVar;
                        messageObtainMessage.setData(bundle);
                        hq.this.j.sendMessage(messageObtainMessage);
                    } catch (Throwable th) {
                        ga.j jVar2 = new ga.j();
                        jVar2.b = hq.this.d;
                        jVar2.a = poiResultSearchPOI;
                        messageObtainMessage.obj = jVar2;
                        messageObtainMessage.setData(bundle);
                        hq.this.j.sendMessage(messageObtainMessage);
                        throw th;
                    }
                }
            });
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override // com.amap.api.services.interfaces.IPoiSearch
    public final PoiItem searchPOIId(String str) throws AMapException {
        fy.a(this.c);
        PoiSearch.Query query = this.b;
        return new gf(this.c, str, query != null ? query.m52clone() : null).d();
    }

    @Override // com.amap.api.services.interfaces.IPoiSearch
    public final void searchPOIIdAsyn(final String str) {
        gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hq.2
            @Override // java.lang.Runnable
            public final void run() {
                ga.h hVar;
                Message messageObtainMessage = ga.a().obtainMessage();
                messageObtainMessage.arg1 = 6;
                messageObtainMessage.what = 602;
                Bundle bundle = new Bundle();
                PoiItem poiItemSearchPOIId = null;
                try {
                    try {
                        poiItemSearchPOIId = hq.this.searchPOIId(str);
                        bundle.putInt("errorCode", 1000);
                        hVar = new ga.h();
                    } catch (AMapException e) {
                        fp.a(e, "PoiSearch", "searchPOIIdAsyn");
                        bundle.putInt("errorCode", e.getErrorCode());
                        hVar = new ga.h();
                    }
                    hVar.b = hq.this.d;
                    hVar.a = poiItemSearchPOIId;
                    messageObtainMessage.obj = hVar;
                    messageObtainMessage.setData(bundle);
                    hq.this.j.sendMessage(messageObtainMessage);
                } catch (Throwable th) {
                    ga.h hVar2 = new ga.h();
                    hVar2.b = hq.this.d;
                    hVar2.a = poiItemSearchPOIId;
                    messageObtainMessage.obj = hVar2;
                    messageObtainMessage.setData(bundle);
                    hq.this.j.sendMessage(messageObtainMessage);
                    throw th;
                }
            }
        });
    }

    @Override // com.amap.api.services.interfaces.IPoiSearch
    public final void setQuery(PoiSearch.Query query) {
        this.b = query;
    }

    @Override // com.amap.api.services.interfaces.IPoiSearch
    public final void setBound(PoiSearch.SearchBound searchBound) {
        this.a = searchBound;
    }

    @Override // com.amap.api.services.interfaces.IPoiSearch
    public final PoiSearch.Query getQuery() {
        return this.b;
    }

    @Override // com.amap.api.services.interfaces.IPoiSearch
    public final PoiSearch.SearchBound getBound() {
        return this.a;
    }

    private void a(PoiResult poiResult) {
        int i2;
        i = new HashMap<>();
        PoiSearch.Query query = this.b;
        if (query == null || poiResult == null || (i2 = this.h) <= 0 || i2 <= query.getPageNum()) {
            return;
        }
        i.put(Integer.valueOf(this.b.getPageNum()), poiResult);
    }

    private PoiResult a(int i2) {
        if (!b(i2)) {
            throw new IllegalArgumentException("page out of range");
        }
        return i.get(Integer.valueOf(i2));
    }

    private boolean b(int i2) {
        return i2 <= this.h && i2 >= 0;
    }
}
