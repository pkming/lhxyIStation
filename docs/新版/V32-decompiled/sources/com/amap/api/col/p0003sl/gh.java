package com.amap.api.col.p0003sl;

import android.content.Context;
import android.view.HardwareRenderer;
import com.amap.api.col.p0003sl.go;
import com.amap.api.col.p0003sl.gq;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: PoiSearchKeywordsHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class gh extends ge<gl, PoiResult> {
    private int g;
    private boolean h;
    private List<String> i;
    private List<SuggestionCity> j;

    private static String b(boolean z) {
        return z ? "distance" : "weight";
    }

    public gh(Context context, gl glVar) {
        super(context, glVar);
        this.g = 0;
        this.h = false;
        this.i = new ArrayList();
        this.j = new ArrayList();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        String str = fo.a() + "/place";
        if (((gl) this.b).b == null) {
            return str + "/text?";
        }
        if (!((gl) this.b).b.getShape().equals("Bound")) {
            return (((gl) this.b).b.getShape().equals("Rectangle") || ((gl) this.b).b.getShape().equals("Polygon")) ? str + "/polygon?" : str;
        }
        String str2 = str + "/around?";
        this.h = true;
        return str2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    /* JADX INFO: renamed from: d, reason: merged with bridge method [inline-methods] */
    public PoiResult a(String str) throws AMapException {
        JSONObject jSONObject;
        ArrayList<PoiItem> arrayList = new ArrayList<>();
        if (str == null) {
            return PoiResult.createPagedResult(((gl) this.b).a, ((gl) this.b).b, this.i, this.j, ((gl) this.b).a.getPageSize(), this.g, arrayList);
        }
        try {
            jSONObject = new JSONObject(str);
            this.g = jSONObject.optInt(HardwareRenderer.OVERDRAW_PROPERTY_COUNT);
            arrayList = fx.c(jSONObject);
        } catch (JSONException e) {
            fp.a(e, "PoiSearchKeywordHandler", "paseJSONJSONException");
        } catch (Exception e2) {
            fp.a(e2, "PoiSearchKeywordHandler", "paseJSONException");
        }
        if (!jSONObject.has("suggestion")) {
            return PoiResult.createPagedResult(((gl) this.b).a, ((gl) this.b).b, this.i, this.j, ((gl) this.b).a.getPageSize(), this.g, arrayList);
        }
        JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("suggestion");
        if (jSONObjectOptJSONObject == null) {
            return PoiResult.createPagedResult(((gl) this.b).a, ((gl) this.b).b, this.i, this.j, ((gl) this.b).a.getPageSize(), this.g, arrayList);
        }
        this.j = fx.a(jSONObjectOptJSONObject);
        this.i = fx.b(jSONObjectOptJSONObject);
        return PoiResult.createPagedResult(((gl) this.b).a, ((gl) this.b).b, this.i, this.j, ((gl) this.b).a.getPageSize(), this.g, arrayList);
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
        if (((gl) this.b).b != null) {
            if (((gl) this.b).b.getShape().equals("Bound")) {
                if (z) {
                    sb.append("&location=").append(fp.a(((gl) this.b).b.getCenter().getLongitude()) + "," + fp.a(((gl) this.b).b.getCenter().getLatitude()));
                }
                sb.append("&radius=").append(((gl) this.b).b.getRange());
                sb.append("&sortrule=").append(b(((gl) this.b).b.isDistanceSort()));
            } else if (((gl) this.b).b.getShape().equals("Rectangle")) {
                LatLonPoint lowerLeft = ((gl) this.b).b.getLowerLeft();
                LatLonPoint upperRight = ((gl) this.b).b.getUpperRight();
                sb.append("&polygon=" + fp.a(lowerLeft.getLongitude()) + "," + fp.a(lowerLeft.getLatitude()) + ";" + fp.a(upperRight.getLongitude()) + "," + fp.a(upperRight.getLatitude()));
            } else if (((gl) this.b).b.getShape().equals("Polygon") && (polyGonList = ((gl) this.b).b.getPolyGonList()) != null && polyGonList.size() > 0) {
                sb.append("&polygon=" + fp.a(polyGonList));
            }
        }
        String city = ((gl) this.b).a.getCity();
        if (!c(city)) {
            sb.append("&city=").append(b(city));
        }
        String strB = b(((gl) this.b).a.getQueryString());
        if (!c(strB)) {
            sb.append("&keywords=").append(strB);
        }
        sb.append("&offset=").append(((gl) this.b).a.getPageSize());
        sb.append("&page=").append(((gl) this.b).a.getPageNum());
        String building = ((gl) this.b).a.getBuilding();
        if (building != null && building.trim().length() > 0) {
            sb.append("&building=").append(((gl) this.b).a.getBuilding());
        }
        String strB2 = b(((gl) this.b).a.getCategory());
        if (!c(strB2)) {
            sb.append("&types=").append(strB2);
        }
        if (!c(((gl) this.b).a.getExtensions())) {
            sb.append("&extensions=").append(((gl) this.b).a.getExtensions());
        } else {
            sb.append("&extensions=base");
        }
        sb.append("&key=").append(ig.f(this.e));
        if (((gl) this.b).a.getCityLimit()) {
            sb.append("&citylimit=true");
        } else {
            sb.append("&citylimit=false");
        }
        if (((gl) this.b).a.isRequireSubPois()) {
            sb.append("&children=1");
        } else {
            sb.append("&children=0");
        }
        if (this.h) {
            if (((gl) this.b).a.isSpecial()) {
                sb.append("&special=1");
            } else {
                sb.append("&special=0");
            }
        }
        if (((gl) this.b).b == null && ((gl) this.b).a.getLocation() != null) {
            sb.append("&sortrule=").append(b(((gl) this.b).a.isDistanceSort()));
            sb.append("&location=").append(fp.a(((gl) this.b).a.getLocation().getLongitude()) + "," + fp.a(((gl) this.b).a.getLocation().getLatitude()));
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
            if (((gl) this.b).b.getShape().equals("Bound")) {
                bVar.b = new gq.a(fp.a(((gl) this.b).b.getCenter().getLatitude()), fp.a(((gl) this.b).b.getCenter().getLongitude()), dA);
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
