package com.amap.api.col.p0003sl;

import android.content.Context;

/* JADX INFO: compiled from: WeatherSearchHandler.java */
/* JADX INFO: loaded from: classes2.dex */
abstract class hf<T, V> extends fh<T, V> {
    public hf(Context context, T t) {
        super(context, t);
    }

    public final T f() {
        return this.b;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public String getURL() {
        return fo.a() + "/weather/weatherInfo?";
    }
}
