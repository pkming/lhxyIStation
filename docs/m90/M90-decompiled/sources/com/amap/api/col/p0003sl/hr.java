package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.amap.api.col.p0003sl.ga;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItemV2;
import com.amap.api.services.interfaces.IPoiSearchV2;
import com.amap.api.services.poisearch.PoiResultV2;
import com.amap.api.services.poisearch.PoiSearchV2;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: compiled from: PoiSearchCoreV2.java */
/* JADX INFO: loaded from: classes2.dex */
public final class hr implements IPoiSearchV2 {
    private static HashMap<Integer, PoiResultV2> i;
    private PoiSearchV2.SearchBound a;
    private PoiSearchV2.Query b;
    private Context c;
    private PoiSearchV2.OnPoiSearchListener d;
    private String e = "zh-CN";
    private PoiSearchV2.Query f;
    private PoiSearchV2.SearchBound g;
    private int h;
    private Handler j;

    public hr(Context context, PoiSearchV2.Query query) throws AMapException {
        this.j = null;
        iq iqVarA = ip.a(context, fo.a(false));
        if (iqVarA.a != ip.c.SuccessCode) {
            throw new AMapException(iqVarA.b, 1, iqVarA.b, iqVarA.a.a());
        }
        this.c = context.getApplicationContext();
        setQuery(query);
        this.j = ga.a();
    }

    @Override // com.amap.api.services.interfaces.IPoiSearchV2
    public final void setOnPoiSearchListener(PoiSearchV2.OnPoiSearchListener onPoiSearchListener) {
        this.d = onPoiSearchListener;
    }

    @Override // com.amap.api.services.interfaces.IPoiSearchV2
    public final void setLanguage(String str) {
        if ("en".equals(str)) {
            this.e = "en";
        } else {
            this.e = "zh-CN";
        }
    }

    @Override // com.amap.api.services.interfaces.IPoiSearchV2
    public final String getLanguage() {
        return this.e;
    }

    private boolean a() {
        PoiSearchV2.Query query = this.b;
        if (query == null) {
            return false;
        }
        return (fp.a(query.getQueryString()) && fp.a(this.b.getCategory())) ? false : true;
    }

    private boolean b() {
        PoiSearchV2.SearchBound bound = getBound();
        return bound != null && bound.getShape().equals("Bound");
    }

    private boolean c() {
        PoiSearchV2.SearchBound bound = getBound();
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

    @Override // com.amap.api.services.interfaces.IPoiSearchV2
    public final PoiResultV2 searchPOI() throws AMapException {
        try {
            fy.a(this.c);
            if (!b() && !a()) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if (!c()) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            PoiSearchV2.Query query = this.b;
            if (query == null) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            if ((!query.queryEquals(this.f) && this.a == null) || (!this.b.queryEquals(this.f) && !this.a.equals(this.g))) {
                this.h = 0;
                this.f = this.b.m54clone();
                PoiSearchV2.SearchBound searchBound = this.a;
                if (searchBound != null) {
                    this.g = searchBound.m55clone();
                }
                HashMap<Integer, PoiResultV2> map = i;
                if (map != null) {
                    map.clear();
                }
            }
            PoiSearchV2.SearchBound searchBound2 = this.a;
            PoiSearchV2.SearchBound searchBoundClone = searchBound2 != null ? searchBound2.m55clone() : null;
            gr.a().a(this.b.getQueryString());
            this.b.setPageNum(gr.a().k(this.b.getPageNum()));
            this.b.setPageSize(gr.a().l(this.b.getPageSize()));
            if (this.h == 0) {
                PoiResultV2 poiResultV2D = new gi(this.c, new gm(this.b.m54clone(), searchBoundClone)).d();
                a(poiResultV2D);
                return poiResultV2D;
            }
            PoiResultV2 poiResultV2A = a(this.b.getPageNum());
            if (poiResultV2A != null) {
                return poiResultV2A;
            }
            PoiResultV2 poiResultV2D2 = new gi(this.c, new gm(this.b.m54clone(), searchBoundClone)).d();
            i.put(Integer.valueOf(this.b.getPageNum()), poiResultV2D2);
            return poiResultV2D2;
        } catch (AMapException e) {
            fp.a(e, "PoiSearch", "searchPOI");
            throw new AMapException(e.getErrorMessage());
        }
    }

    @Override // com.amap.api.services.interfaces.IPoiSearchV2
    public final void searchPOIAsyn() {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hr.1
                @Override // java.lang.Runnable
                public final void run() {
                    ga.k kVar;
                    Message messageObtainMessage = hr.this.j.obtainMessage();
                    messageObtainMessage.arg1 = 19;
                    messageObtainMessage.what = 603;
                    Bundle bundle = new Bundle();
                    PoiResultV2 poiResultV2SearchPOI = null;
                    try {
                        try {
                            poiResultV2SearchPOI = hr.this.searchPOI();
                            bundle.putInt("errorCode", 1000);
                            kVar = new ga.k();
                        } catch (AMapException e) {
                            bundle.putInt("errorCode", e.getErrorCode());
                            kVar = new ga.k();
                        }
                        kVar.b = hr.this.d;
                        kVar.a = poiResultV2SearchPOI;
                        messageObtainMessage.obj = kVar;
                        messageObtainMessage.setData(bundle);
                        hr.this.j.sendMessage(messageObtainMessage);
                    } catch (Throwable th) {
                        ga.k kVar2 = new ga.k();
                        kVar2.b = hr.this.d;
                        kVar2.a = poiResultV2SearchPOI;
                        messageObtainMessage.obj = kVar2;
                        messageObtainMessage.setData(bundle);
                        hr.this.j.sendMessage(messageObtainMessage);
                        throw th;
                    }
                }
            });
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override // com.amap.api.services.interfaces.IPoiSearchV2
    public final PoiItemV2 searchPOIId(String str) throws AMapException {
        fy.a(this.c);
        PoiSearchV2.Query query = this.b;
        return new gg(this.c, str, query != null ? query.m54clone() : null).d();
    }

    @Override // com.amap.api.services.interfaces.IPoiSearchV2
    public final void searchPOIIdAsyn(final String str) {
        gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hr.2
            @Override // java.lang.Runnable
            public final void run() {
                ga.i iVar;
                Message messageObtainMessage = ga.a().obtainMessage();
                messageObtainMessage.arg1 = 19;
                messageObtainMessage.what = 604;
                Bundle bundle = new Bundle();
                PoiItemV2 poiItemV2SearchPOIId = null;
                try {
                    try {
                        poiItemV2SearchPOIId = hr.this.searchPOIId(str);
                        bundle.putInt("errorCode", 1000);
                        iVar = new ga.i();
                    } catch (AMapException e) {
                        fp.a(e, "PoiSearch", "searchPOIIdAsyn");
                        bundle.putInt("errorCode", e.getErrorCode());
                        iVar = new ga.i();
                    }
                    iVar.b = hr.this.d;
                    iVar.a = poiItemV2SearchPOIId;
                    messageObtainMessage.obj = iVar;
                    messageObtainMessage.setData(bundle);
                    hr.this.j.sendMessage(messageObtainMessage);
                } catch (Throwable th) {
                    ga.i iVar2 = new ga.i();
                    iVar2.b = hr.this.d;
                    iVar2.a = poiItemV2SearchPOIId;
                    messageObtainMessage.obj = iVar2;
                    messageObtainMessage.setData(bundle);
                    hr.this.j.sendMessage(messageObtainMessage);
                    throw th;
                }
            }
        });
    }

    @Override // com.amap.api.services.interfaces.IPoiSearchV2
    public final void setQuery(PoiSearchV2.Query query) {
        this.b = query;
    }

    @Override // com.amap.api.services.interfaces.IPoiSearchV2
    public final void setBound(PoiSearchV2.SearchBound searchBound) {
        this.a = searchBound;
    }

    @Override // com.amap.api.services.interfaces.IPoiSearchV2
    public final PoiSearchV2.Query getQuery() {
        return this.b;
    }

    @Override // com.amap.api.services.interfaces.IPoiSearchV2
    public final PoiSearchV2.SearchBound getBound() {
        return this.a;
    }

    private void a(PoiResultV2 poiResultV2) {
        int i2;
        i = new HashMap<>();
        PoiSearchV2.Query query = this.b;
        if (query == null || poiResultV2 == null || (i2 = this.h) <= 0 || i2 <= query.getPageNum()) {
            return;
        }
        i.put(Integer.valueOf(this.b.getPageNum()), poiResultV2);
    }

    private PoiResultV2 a(int i2) {
        if (!b(i2)) {
            throw new IllegalArgumentException("page out of range");
        }
        return i.get(Integer.valueOf(i2));
    }

    private boolean b(int i2) {
        return i2 <= this.h && i2 >= 0;
    }
}
