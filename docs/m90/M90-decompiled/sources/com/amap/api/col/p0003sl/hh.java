package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.amap.api.col.p0003sl.ga;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.services.auto.AutoTChargeStationResult;
import com.amap.api.services.auto.AutoTSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.interfaces.IAutoTSearch;

/* JADX INFO: compiled from: AutoTSearchCore.java */
/* JADX INFO: loaded from: classes2.dex */
public final class hh implements IAutoTSearch {
    private Context a;
    private Handler b;
    private AutoTSearch.Query c;
    private AutoTSearch.OnChargeStationListener d;

    public hh(Context context) throws AMapException {
        this.b = null;
        iq iqVarA = ip.a(context, fo.a(false));
        if (iqVarA.a != ip.c.SuccessCode) {
            throw new AMapException(iqVarA.b, 1, iqVarA.b, iqVarA.a.a());
        }
        this.a = context.getApplicationContext();
        this.b = ga.a();
    }

    @Override // com.amap.api.services.interfaces.IAutoTSearch
    public final void setQuery(AutoTSearch.Query query) {
        this.c = query;
    }

    @Override // com.amap.api.services.interfaces.IAutoTSearch
    public final void setChargeStationListener(AutoTSearch.OnChargeStationListener onChargeStationListener) {
        this.d = onChargeStationListener;
    }

    @Override // com.amap.api.services.interfaces.IAutoTSearch
    public final AutoTChargeStationResult searchChargeStation() throws AMapException {
        try {
            fy.a(this.a);
            if (this.c == null) {
                throw new AMapException("无效的参数 - IllegalArgumentException");
            }
            return new fd(this.a, this.c.m46clone()).d();
        } catch (AMapException e) {
            throw new AMapException(e.getMessage());
        }
    }

    @Override // com.amap.api.services.interfaces.IAutoTSearch
    public final void searchChargeStationAsync() {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hh.1
                @Override // java.lang.Runnable
                public final void run() {
                    ga.a aVar;
                    Message messageObtainMessage = hh.this.b.obtainMessage();
                    messageObtainMessage.arg1 = 20;
                    messageObtainMessage.what = 600;
                    Bundle bundle = new Bundle();
                    AutoTChargeStationResult autoTChargeStationResultSearchChargeStation = null;
                    try {
                        try {
                            autoTChargeStationResultSearchChargeStation = hh.this.searchChargeStation();
                            bundle.putInt("errorCode", 1000);
                            aVar = new ga.a();
                        } catch (AMapException e) {
                            bundle.putInt("errorCode", e.getErrorCode());
                            aVar = new ga.a();
                        }
                        aVar.b = hh.this.d;
                        aVar.a = autoTChargeStationResultSearchChargeStation;
                        messageObtainMessage.obj = aVar;
                        messageObtainMessage.setData(bundle);
                        hh.this.b.sendMessage(messageObtainMessage);
                    } catch (Throwable th) {
                        ga.a aVar2 = new ga.a();
                        aVar2.b = hh.this.d;
                        aVar2.a = autoTChargeStationResultSearchChargeStation;
                        messageObtainMessage.obj = aVar2;
                        messageObtainMessage.setData(bundle);
                        hh.this.b.sendMessage(messageObtainMessage);
                        throw th;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
