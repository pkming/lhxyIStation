package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import android.view.HardwareRenderer;
import com.amap.api.col.p0003sl.go;
import com.amap.api.col.p0003sl.gq;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItemV2;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.poisearch.PoiResultV2;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: PoiSearchKeywordsHandlerV2.java */
/* JADX INFO: loaded from: classes2.dex */
public final class gi extends ge<gm, PoiResultV2> {
    private int g;
    private boolean h;

    private static String b(boolean z) {
        return z ? "distance" : "weight";
    }

    public gi(Context context, gm gmVar) {
        super(context, gmVar);
        this.g = 0;
        this.h = false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        String str = fo.c() + "/place";
        if (((gm) this.b).b == null) {
            return str + "/text?";
        }
        if (!((gm) this.b).b.getShape().equals("Bound")) {
            return (((gm) this.b).b.getShape().equals("Rectangle") || ((gm) this.b).b.getShape().equals("Polygon")) ? str + "/polygon?" : str;
        }
        String str2 = str + "/around?";
        this.h = true;
        return str2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    /* JADX INFO: renamed from: d, reason: merged with bridge method [inline-methods] */
    public PoiResultV2 a(String str) throws AMapException {
        ArrayList<PoiItemV2> arrayList = new ArrayList<>();
        if (str == null) {
            return PoiResultV2.createPagedResult(((gm) this.b).a, ((gm) this.b).b, this.g, arrayList);
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            this.g = jSONObject.optInt(HardwareRenderer.OVERDRAW_PROPERTY_COUNT);
            arrayList = fx.d(jSONObject);
        } catch (JSONException e) {
            fp.a(e, "PoiSearchKeywordHandler", "paseJSONJSONException");
        } catch (Exception e2) {
            fp.a(e2, "PoiSearchKeywordHandler", "paseJSONException");
        }
        return PoiResultV2.createPagedResult(((gm) this.b).a, ((gm) this.b).b, this.g, arrayList);
    }

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        return a(true);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private String a(boolean z) {
        List<LatLonPoint> polyGonList;
        StringBuilder sb = new StringBuilder();
        sb.append("output=json");
        if (((gm) this.b).b != null) {
            if (((gm) this.b).b.getShape().equals("Bound")) {
                if (z) {
                    sb.append("&location=").append(fp.a(((gm) this.b).b.getCenter().getLongitude()) + "," + fp.a(((gm) this.b).b.getCenter().getLatitude()));
                }
                sb.append("&radius=").append(((gm) this.b).b.getRange());
                sb.append("&sortrule=").append(b(((gm) this.b).b.isDistanceSort()));
            } else if (((gm) this.b).b.getShape().equals("Rectangle")) {
                LatLonPoint lowerLeft = ((gm) this.b).b.getLowerLeft();
                LatLonPoint upperRight = ((gm) this.b).b.getUpperRight();
                sb.append("&polygon=" + fp.a(lowerLeft.getLongitude()) + "," + fp.a(lowerLeft.getLatitude()) + ";" + fp.a(upperRight.getLongitude()) + "," + fp.a(upperRight.getLatitude()));
            } else if (((gm) this.b).b.getShape().equals("Polygon") && (polyGonList = ((gm) this.b).b.getPolyGonList()) != null && polyGonList.size() > 0) {
                sb.append("&polygon=" + fp.a(polyGonList));
            }
        }
        String city = ((gm) this.b).a.getCity();
        if (!c(city)) {
            sb.append("&region=").append(b(city));
        }
        String strB = b(((gm) this.b).a.getQueryString());
        if (!c(strB)) {
            sb.append("&keywords=").append(strB);
        }
        sb.append("&page_size=").append(((gm) this.b).a.getPageSize());
        sb.append("&page_num=").append(((gm) this.b).a.getPageNum());
        String building = ((gm) this.b).a.getBuilding();
        if (building != null && building.trim().length() > 0) {
            sb.append("&building=").append(((gm) this.b).a.getBuilding());
        }
        String strB2 = b(((gm) this.b).a.getCategory());
        if (!c(strB2)) {
            sb.append("&types=").append(strB2);
        }
        String strA = a(((gm) this.b).a.getShowFields());
        if (strA != null) {
            sb.append("&show_fields=").append(strA);
        }
        sb.append("&key=").append(ig.f(this.e));
        if (((gm) this.b).a.getCityLimit()) {
            sb.append("&citylimit=true");
        } else {
            sb.append("&citylimit=false");
        }
        if (this.h) {
            if (((gm) this.b).a.isSpecial()) {
                sb.append("&special=1");
            } else {
                sb.append("&special=0");
            }
        }
        String channel = ((gm) this.b).a.getChannel();
        if (!TextUtils.isEmpty(channel)) {
            sb.append("&channel=").append(channel);
        }
        String premium = ((gm) this.b).a.getPremium();
        if (!TextUtils.isEmpty(premium)) {
            sb.append("&permium=").append(premium);
        }
        if (((gm) this.b).b == null && ((gm) this.b).a.getLocation() != null) {
            sb.append("&sortrule=").append(b(((gm) this.b).a.isDistanceSort()));
            sb.append("&location=").append(fp.a(((gm) this.b).a.getLocation().getLongitude()) + "," + fp.a(((gm) this.b).a.getLocation().getLatitude()));
        }
        return sb.toString();
    }

    private static gq f() {
        gp gpVarA = go.a().a("regeo");
        if (gpVarA == null) {
            return null;
        }
        return (gq) gpVarA;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fg
    protected final go.b e() {
        go.b bVar = new go.b();
        if (this.h) {
            gq gqVarF = f();
            double dA = gqVarF != null ? gqVarF.a() : 0.0d;
            StringBuilder sb = new StringBuilder();
            sb.append(getURL());
            sb.append(a(false));
            sb.append("language=").append(ServiceSettings.getInstance().getLanguage());
            bVar.a = sb.toString();
            if (((gm) this.b).b.getShape().equals("Bound")) {
                bVar.b = new gq.a(fp.a(((gm) this.b).b.getCenter().getLatitude()), fp.a(((gm) this.b).b.getCenter().getLongitude()), dA);
            }
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(getURL());
            sb2.append(c());
            sb2.append("language=").append(ServiceSettings.getInstance().getLanguage());
            bVar.a = sb2.toString();
        }
        return bVar;
    }
}
