package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.col.p0003sl.ih;
import com.amap.api.maps.AMapException;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: compiled from: AbstractProtocalHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class bw<T, V> {
    protected T a;
    protected int b = 3;
    protected Context c;

    protected abstract V a(JSONObject jSONObject) throws AMapException;

    protected abstract String a();

    protected abstract JSONObject a(ih.b bVar);

    protected abstract Map<String, String> b();

    public bw(Context context, T t) {
        a(context, t);
    }

    private void a(Context context, T t) {
        this.c = context;
        this.a = t;
    }

    public final V c() throws AMapException {
        if (this.a != null) {
            return d();
        }
        return null;
    }

    private V d() throws AMapException {
        int i;
        String str;
        AMapException aMapException;
        int i2 = 0;
        V vA = null;
        ih.b bVarA = null;
        while (i2 < this.b) {
            try {
                bVarA = ih.a(this.c, dx.a(), a(), b());
                vA = a(a(bVarA));
                i2 = this.b;
            } finally {
                if (i2 < i) {
                    continue;
                }
            }
        }
        return vA;
    }
}
