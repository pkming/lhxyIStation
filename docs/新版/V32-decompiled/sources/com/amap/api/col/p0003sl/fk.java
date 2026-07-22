package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import android.view.HardwareRenderer;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusStationQuery;
import com.amap.api.services.busline.BusStationResult;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.SuggestionCity;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: compiled from: BusSearchServerHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fk<T> extends fh<T, Object> {
    private int g;
    private List<String> h;
    private List<SuggestionCity> i;

    public fk(Context context, T t) {
        super(context, t);
        this.g = 0;
        this.h = new ArrayList();
        this.i = new ArrayList();
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        String str;
        if (!(this.b instanceof BusLineQuery)) {
            str = "stopname";
        } else if (((BusLineQuery) this.b).getCategory() == BusLineQuery.SearchType.BY_LINE_ID) {
            str = "lineid";
        } else {
            str = ((BusLineQuery) this.b).getCategory() == BusLineQuery.SearchType.BY_LINE_NAME ? "linename" : "";
        }
        return fo.a() + "/bus/" + str + "?";
    }

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final Object a(String str) throws AMapException {
        try {
            JSONObject jSONObject = new JSONObject(str);
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("suggestion");
            if (jSONObjectOptJSONObject != null) {
                this.i = fx.a(jSONObjectOptJSONObject);
                this.h = fx.b(jSONObjectOptJSONObject);
            }
            this.g = jSONObject.optInt(HardwareRenderer.OVERDRAW_PROPERTY_COUNT);
            if (this.b instanceof BusLineQuery) {
                return BusLineResult.createPagedResult((BusLineQuery) this.b, this.g, this.i, this.h, fx.h(jSONObject));
            }
            return BusStationResult.createPagedResult((BusStationQuery) this.b, this.g, this.i, this.h, fx.g(jSONObject));
        } catch (Exception e) {
            fp.a(e, "BusSearchServerHandler", "paseJSON");
            return null;
        }
    }

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuilder sb = new StringBuilder();
        sb.append("output=json");
        if (this.b instanceof BusLineQuery) {
            BusLineQuery busLineQuery = (BusLineQuery) this.b;
            if (!TextUtils.isEmpty(busLineQuery.getExtensions())) {
                sb.append("&extensions=").append(busLineQuery.getExtensions());
            } else {
                sb.append("&extensions=base");
            }
            if (busLineQuery.getCategory() == BusLineQuery.SearchType.BY_LINE_ID) {
                sb.append("&id=").append(b(((BusLineQuery) this.b).getQueryString()));
            } else {
                String city = busLineQuery.getCity();
                if (!fx.i(city)) {
                    sb.append("&city=").append(b(city));
                }
                sb.append("&keywords=" + b(busLineQuery.getQueryString()));
                sb.append("&offset=" + busLineQuery.getPageSize());
                sb.append("&page=" + busLineQuery.getPageNumber());
            }
        } else {
            BusStationQuery busStationQuery = (BusStationQuery) this.b;
            String city2 = busStationQuery.getCity();
            if (!fx.i(city2)) {
                sb.append("&city=").append(b(city2));
            }
            sb.append("&keywords=" + b(busStationQuery.getQueryString()));
            sb.append("&offset=" + busStationQuery.getPageSize());
            sb.append("&page=" + busStationQuery.getPageNumber());
        }
        sb.append("&key=" + ig.f(this.e));
        return sb.toString();
    }
}
