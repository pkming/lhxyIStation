package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.WeatherSearchQuery;

/* JADX INFO: compiled from: WeatherForecastHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class hd extends hf<WeatherSearchQuery, LocalWeatherForecast> {
    private LocalWeatherForecast g;

    @Override // com.amap.api.col.p0003sl.hf, com.amap.api.col.p0003sl.lb
    public final /* bridge */ /* synthetic */ String getURL() {
        return super.getURL();
    }

    public hd(Context context, WeatherSearchQuery weatherSearchQuery) {
        super(context, weatherSearchQuery);
        this.g = new LocalWeatherForecast();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("output=json");
        String city = ((WeatherSearchQuery) this.b).getCity();
        if (!fx.i(city)) {
            stringBuffer.append("&city=").append(b(city));
        }
        stringBuffer.append("&extensions=all");
        stringBuffer.append("&key=" + ig.f(this.e));
        return stringBuffer.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    /* JADX INFO: renamed from: c, reason: merged with bridge method [inline-methods] */
    public LocalWeatherForecast a(String str) throws AMapException {
        LocalWeatherForecast localWeatherForecastH = fx.h(str);
        this.g = localWeatherForecastH;
        return localWeatherForecastH;
    }
}
