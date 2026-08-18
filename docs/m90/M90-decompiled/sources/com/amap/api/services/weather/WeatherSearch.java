package com.amap.api.services.weather;

import android.content.Context;
import com.amap.api.col.p0003sl.hw;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.interfaces.IWeatherSearch;

/* JADX INFO: loaded from: classes2.dex */
public class WeatherSearch {
    private IWeatherSearch a;

    public interface OnWeatherSearchListener {
        void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i);

        void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i);
    }

    public WeatherSearch(Context context) throws AMapException {
        this.a = null;
        if (0 == 0) {
            try {
                this.a = new hw(context);
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof AMapException) {
                    throw ((AMapException) e);
                }
            }
        }
    }

    public WeatherSearchQuery getQuery() {
        IWeatherSearch iWeatherSearch = this.a;
        if (iWeatherSearch != null) {
            return iWeatherSearch.getQuery();
        }
        return null;
    }

    public void setQuery(WeatherSearchQuery weatherSearchQuery) {
        IWeatherSearch iWeatherSearch = this.a;
        if (iWeatherSearch != null) {
            iWeatherSearch.setQuery(weatherSearchQuery);
        }
    }

    public void searchWeatherAsyn() {
        IWeatherSearch iWeatherSearch = this.a;
        if (iWeatherSearch != null) {
            iWeatherSearch.searchWeatherAsyn();
        }
    }

    public void setOnWeatherSearchListener(OnWeatherSearchListener onWeatherSearchListener) {
        IWeatherSearch iWeatherSearch = this.a;
        if (iWeatherSearch != null) {
            iWeatherSearch.setOnWeatherSearchListener(onWeatherSearchListener);
        }
    }
}
