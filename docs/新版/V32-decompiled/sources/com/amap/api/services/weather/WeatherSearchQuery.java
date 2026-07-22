package com.amap.api.services.weather;

import com.amap.api.col.p0003sl.fp;

/* JADX INFO: loaded from: classes2.dex */
public class WeatherSearchQuery implements Cloneable {
    public static final int WEATHER_TYPE_FORECAST = 2;
    public static final int WEATHER_TYPE_LIVE = 1;
    private String a;
    private int b;

    public WeatherSearchQuery(String str, int i) {
        this.b = 1;
        this.a = str;
        this.b = i;
    }

    public WeatherSearchQuery() {
        this.b = 1;
    }

    public String getCity() {
        return this.a;
    }

    public int getType() {
        return this.b;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public WeatherSearchQuery m70clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            fp.a(e, "WeatherSearchQuery", "clone");
        }
        return new WeatherSearchQuery(this.a, this.b);
    }
}
