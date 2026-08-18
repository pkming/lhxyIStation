package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.amap.api.col.p0003sl.ga;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.interfaces.IWeatherSearch;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.unisound.client.SpeechConstants;

/* JADX INFO: compiled from: WeatherSearchCore.java */
/* JADX INFO: loaded from: classes2.dex */
public final class hw implements IWeatherSearch {
    private Context a;
    private WeatherSearchQuery b;
    private WeatherSearch.OnWeatherSearchListener c;
    private LocalWeatherLiveResult d;
    private LocalWeatherForecastResult e;
    private Handler f;

    public hw(Context context) throws AMapException {
        this.f = null;
        iq iqVarA = ip.a(context, fo.a(false));
        if (iqVarA.a != ip.c.SuccessCode) {
            throw new AMapException(iqVarA.b, 1, iqVarA.b, iqVarA.a.a());
        }
        this.a = context.getApplicationContext();
        this.f = ga.a();
    }

    @Override // com.amap.api.services.interfaces.IWeatherSearch
    public final WeatherSearchQuery getQuery() {
        return this.b;
    }

    @Override // com.amap.api.services.interfaces.IWeatherSearch
    public final void setQuery(WeatherSearchQuery weatherSearchQuery) {
        this.b = weatherSearchQuery;
    }

    @Override // com.amap.api.services.interfaces.IWeatherSearch
    public final void searchWeatherAsyn() {
        try {
            gz.a().a(new Runnable() { // from class: com.amap.api.col.3sl.hw.1
                @Override // java.lang.Runnable
                public final void run() {
                    Message messageObtainMessage = ga.a().obtainMessage();
                    messageObtainMessage.arg1 = 13;
                    Bundle bundle = new Bundle();
                    if (hw.this.b == null) {
                        try {
                            throw new AMapException("无效的参数 - IllegalArgumentException");
                        } catch (AMapException e) {
                            fp.a(e, "WeatherSearch", "searchWeatherAsyn");
                            return;
                        }
                    }
                    if (hw.this.b.getType() == 1) {
                        try {
                            try {
                                hw hwVar = hw.this;
                                hwVar.d = hwVar.a();
                                bundle.putInt("errorCode", 1000);
                                return;
                            } finally {
                                ga.o oVar = new ga.o();
                                messageObtainMessage.what = SpeechConstants.ASR_NLU_ERROR;
                                oVar.b = hw.this.c;
                                oVar.a = hw.this.d;
                                messageObtainMessage.obj = oVar;
                                messageObtainMessage.setData(bundle);
                                hw.this.f.sendMessage(messageObtainMessage);
                            }
                        } catch (AMapException e2) {
                            bundle.putInt("errorCode", e2.getErrorCode());
                            fp.a(e2, "WeatherSearch", "searchWeatherAsyn");
                            return;
                        } catch (Throwable th) {
                            fp.a(th, "WeatherSearch", "searchWeatherAnsyThrowable");
                            return;
                        }
                    }
                    if (hw.this.b.getType() == 2) {
                        try {
                            try {
                                hw hwVar2 = hw.this;
                                hwVar2.e = hwVar2.b();
                                bundle.putInt("errorCode", 1000);
                            } finally {
                                ga.n nVar = new ga.n();
                                messageObtainMessage.what = SpeechConstants.ASR_ERROR_LOADMODEL_FAIL;
                                nVar.b = hw.this.c;
                                nVar.a = hw.this.e;
                                messageObtainMessage.obj = nVar;
                                messageObtainMessage.setData(bundle);
                                hw.this.f.sendMessage(messageObtainMessage);
                            }
                        } catch (AMapException e3) {
                            bundle.putInt("errorCode", e3.getErrorCode());
                            fp.a(e3, "WeatherSearch", "searchWeatherAsyn");
                        } catch (Throwable th2) {
                            fp.a(th2, "WeatherSearch", "searchWeatherAnsyThrowable");
                        }
                    }
                }
            });
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public LocalWeatherLiveResult a() throws AMapException {
        fy.a(this.a);
        if (this.b == null) {
            throw new AMapException("无效的参数 - IllegalArgumentException");
        }
        he heVar = new he(this.a, this.b);
        return LocalWeatherLiveResult.createPagedResult(heVar.f(), heVar.d());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public LocalWeatherForecastResult b() throws AMapException {
        fy.a(this.a);
        if (this.b == null) {
            throw new AMapException("无效的参数 - IllegalArgumentException");
        }
        hd hdVar = new hd(this.a, this.b);
        return LocalWeatherForecastResult.createPagedResult(hdVar.f(), hdVar.d());
    }

    @Override // com.amap.api.services.interfaces.IWeatherSearch
    public final void setOnWeatherSearchListener(WeatherSearch.OnWeatherSearchListener onWeatherSearchListener) {
        this.c = onWeatherSearchListener;
    }
}
