package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.amap.api.services.interfaces.IDistrictSearch;
import java.util.HashMap;

/* JADX INFO: compiled from: DistrictSearchCore.java */
/* JADX INFO: loaded from: classes2.dex */
public final class hm implements IDistrictSearch {
    private static HashMap<Integer, DistrictResult> f;
    private Context a;
    private DistrictSearchQuery b;
    private DistrictSearch.OnDistrictSearchListener c;
    private DistrictSearchQuery d;
    private int e;
    private Handler g;

    public hm(Context context) throws AMapException {
        iq iqVarA = ip.a(context, fo.a(false));
        if (iqVarA.a != ip.c.SuccessCode) {
            throw new AMapException(iqVarA.b, 1, iqVarA.b, iqVarA.a.a());
        }
        this.a = context.getApplicationContext();
        this.g = ga.a();
    }

    private void a(DistrictResult districtResult) {
        int i;
        f = new HashMap<>();
        DistrictSearchQuery districtSearchQuery = this.b;
        if (districtSearchQuery == null || districtResult == null || (i = this.e) <= 0 || i <= districtSearchQuery.getPageNum()) {
            return;
        }
        f.put(Integer.valueOf(this.b.getPageNum()), districtResult);
    }

    @Override // com.amap.api.services.interfaces.IDistrictSearch
    public final DistrictSearchQuery getQuery() {
        return this.b;
    }

    @Override // com.amap.api.services.interfaces.IDistrictSearch
    public final void setQuery(DistrictSearchQuery districtSearchQuery) {
        this.b = districtSearchQuery;
    }

    private boolean a() {
        return this.b != null;
    }

    private DistrictResult a(int i) throws AMapException {
        if (!b(i)) {
            throw new AMapException("无效的参数 - IllegalArgumentException");
        }
        return f.get(Integer.valueOf(i));
    }

    private boolean b(int i) {
        return i < this.e && i >= 0;
    }

    @Override // com.amap.api.services.interfaces.IDistrictSearch
    public final DistrictResult searchDistrict() throws AMapException {
        DistrictResult districtResultA;
        int i;
        try {
            DistrictResult districtResult = new DistrictResult();
            fy.a(this.a);
            if (!a()) {
                this.b = new DistrictSearchQuery();
            }
            districtResult.setQuery(this.b.m51clone());
            if (!this.b.weakEquals(this.d)) {
                this.e = 0;
                this.d = this.b.m51clone();
                HashMap<Integer, DistrictResult> map = f;
                if (map != null) {
                    map.clear();
                }
            }
            if (this.e == 0) {
                districtResultA = new fr(this.a, this.b.m51clone()).d();
                if (districtResultA == null) {
                    return districtResultA;
                }
                this.e = districtResultA.getPageCount();
                a(districtResultA);
            } else {
                districtResultA = a(this.b.getPageNum());
                if (districtResultA == null) {
                    districtResultA = new fr(this.a, this.b.m51clone()).d();
                    DistrictSearchQuery districtSearchQuery = this.b;
                    if (districtSearchQuery != null && districtResultA != null && (i = this.e) > 0 && i > districtSearchQuery.getPageNum()) {
                        f.put(Integer.valueOf(this.b.getPageNum()), districtResultA);
                    }
                }
            }
            return districtResultA;
        } catch (AMapException e) {
            fp.a(e, "DistrictSearch", "searchDistrict");
            throw e;
        }
    }

    @Override // com.amap.api.services.interfaces.IDistrictSearch
    public final void searchDistrictAsyn() {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hm.1
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    DistrictResult districtResult = new DistrictResult();
                    districtResult.setQuery(hm.this.b);
                    try {
                        try {
                            districtResult = hm.this.searchDistrict();
                            if (districtResult != null) {
                                districtResult.setAMapException(new AMapException());
                            }
                        } finally {
                            messageObtainMessage.arg1 = 4;
                            messageObtainMessage.obj = hm.this.c;
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("result", districtResult);
                            messageObtainMessage.setData(bundle);
                            if (hm.this.g != null) {
                                hm.this.g.sendMessage(messageObtainMessage);
                            }
                        }
                    } catch (AMapException e) {
                        districtResult.setAMapException(e);
                        messageObtainMessage.arg1 = 4;
                        messageObtainMessage.obj = hm.this.c;
                        Bundle bundle2 = new Bundle();
                        bundle2.putParcelable("result", districtResult);
                        messageObtainMessage.setData(bundle2);
                        if (hm.this.g != null) {
                            hm.this.g.sendMessage(messageObtainMessage);
                        }
                    } catch (Throwable th) {
                        fp.a(th, "DistrictSearch", "searchDistrictAnsyThrowable");
                        messageObtainMessage.arg1 = 4;
                        messageObtainMessage.obj = hm.this.c;
                        Bundle bundle3 = new Bundle();
                        bundle3.putParcelable("result", districtResult);
                        messageObtainMessage.setData(bundle3);
                        if (hm.this.g != null) {
                            hm.this.g.sendMessage(messageObtainMessage);
                        }
                    }
                }
            });
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override // com.amap.api.services.interfaces.IDistrictSearch
    public final void searchDistrictAnsy() {
        searchDistrictAsyn();
    }

    @Override // com.amap.api.services.interfaces.IDistrictSearch
    public final void setOnDistrictSearchListener(DistrictSearch.OnDistrictSearchListener onDistrictSearchListener) {
        this.c = onDistrictSearchListener;
    }
}
