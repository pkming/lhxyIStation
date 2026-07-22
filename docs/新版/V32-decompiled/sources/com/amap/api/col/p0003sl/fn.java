package com.amap.api.col.p0003sl;

import android.content.Context;
import android.provider.MediaStore;
import android.text.TextUtils;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: CloudSearchKeywordsHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fn extends fl<CloudSearch.Query, CloudResult> {
    private int g;

    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        return null;
    }

    public fn(Context context, CloudSearch.Query query) {
        super(context, query);
        this.g = 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        String str = fo.e() + "/datasearch";
        String shape = ((CloudSearch.Query) this.b).getBound().getShape();
        if (shape.equals("Bound")) {
            return str + "/around";
        }
        if (shape.equals("Polygon") || shape.equals("Rectangle")) {
            return str + "/polygon";
        }
        return shape.equals(CloudSearch.SearchBound.LOCAL_SHAPE) ? str + "/local" : str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    /* JADX INFO: renamed from: c, reason: merged with bridge method [inline-methods] */
    public CloudResult a(String str) throws AMapException {
        ArrayList<CloudItem> arrayListD = null;
        if (str == null || str.equals("")) {
            return CloudResult.createPagedResult((CloudSearch.Query) this.b, this.g, ((CloudSearch.Query) this.b).getBound(), ((CloudSearch.Query) this.b).getPageSize(), null);
        }
        try {
            arrayListD = d(new JSONObject(str));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return CloudResult.createPagedResult((CloudSearch.Query) this.b, this.g, ((CloudSearch.Query) this.b).getBound(), ((CloudSearch.Query) this.b).getPageSize(), arrayListD);
    }

    private ArrayList<CloudItem> d(JSONObject jSONObject) throws JSONException {
        ArrayList<CloudItem> arrayList = new ArrayList<>();
        JSONArray jSONArrayA = a(jSONObject);
        if (jSONArrayA == null) {
            return arrayList;
        }
        this.g = b(jSONObject);
        for (int i = 0; i < jSONArrayA.length(); i++) {
            JSONObject jSONObjectOptJSONObject = jSONArrayA.optJSONObject(i);
            CloudItemDetail cloudItemDetailC = c(jSONObjectOptJSONObject);
            a(cloudItemDetailC, jSONObjectOptJSONObject);
            arrayList.add(cloudItemDetailC);
        }
        return arrayList;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg, com.amap.api.col.p0003sl.lb
    public final Map<String, String> getParams() {
        Hashtable hashtable = new Hashtable(16);
        hashtable.put("key", ig.f(this.e));
        hashtable.put(MediaStore.EXTRA_OUTPUT, "json");
        if (((CloudSearch.Query) this.b).getBound() != null) {
            if (((CloudSearch.Query) this.b).getBound().getShape().equals("Bound")) {
                hashtable.put("center", fp.a(((CloudSearch.Query) this.b).getBound().getCenter().getLongitude()) + "," + fp.a(((CloudSearch.Query) this.b).getBound().getCenter().getLatitude()));
                hashtable.put("radius", new StringBuilder().append(((CloudSearch.Query) this.b).getBound().getRange()).toString());
            } else if (((CloudSearch.Query) this.b).getBound().getShape().equals("Rectangle")) {
                LatLonPoint lowerLeft = ((CloudSearch.Query) this.b).getBound().getLowerLeft();
                LatLonPoint upperRight = ((CloudSearch.Query) this.b).getBound().getUpperRight();
                double dA = fp.a(lowerLeft.getLatitude());
                hashtable.put("polygon", fp.a(lowerLeft.getLongitude()) + "," + dA + ";" + fp.a(upperRight.getLongitude()) + "," + fp.a(upperRight.getLatitude()));
            } else if (((CloudSearch.Query) this.b).getBound().getShape().equals("Polygon")) {
                List<LatLonPoint> polyGonList = ((CloudSearch.Query) this.b).getBound().getPolyGonList();
                if (polyGonList != null && polyGonList.size() > 0) {
                    hashtable.put("polygon", fp.a(polyGonList, ";"));
                }
            } else if (((CloudSearch.Query) this.b).getBound().getShape().equals(CloudSearch.SearchBound.LOCAL_SHAPE)) {
                hashtable.put("city", ((CloudSearch.Query) this.b).getBound().getCity());
            }
        }
        hashtable.put("layerId", ((CloudSearch.Query) this.b).getTableID());
        if (!fp.a(f())) {
            hashtable.put("sortrule", f());
        }
        String strG = g();
        if (!fp.a(strG)) {
            hashtable.put("filter", strG);
        }
        String queryString = ((CloudSearch.Query) this.b).getQueryString();
        if (queryString != null && !"".equals(queryString)) {
            hashtable.put("keywords", queryString);
        } else {
            hashtable.put("keywords", "");
        }
        hashtable.put("pageSize", new StringBuilder().append(((CloudSearch.Query) this.b).getPageSize()).toString());
        hashtable.put("pageNum", new StringBuilder().append(((CloudSearch.Query) this.b).getPageNum()).toString());
        String strA = ij.a();
        String strA2 = ij.a(this.e, strA, a(hashtable));
        hashtable.put(SPUserInfoUtils.TS, strA);
        hashtable.put("scode", strA2);
        return hashtable;
    }

    private static String d(String str) {
        return str != null ? str.replace("&&", "%26%26") : str;
    }

    private static String e(String str) {
        return str != null ? str.replace("%26%26", "&&") : str;
    }

    private static String a(Map<String, String> map) {
        return f(b(map));
    }

    private static String f(String str) {
        try {
            if (TextUtils.isEmpty(str)) {
                return "";
            }
            str = d(str);
            String[] strArrSplit = str.split("&");
            Arrays.sort(strArrSplit);
            StringBuffer stringBuffer = new StringBuffer();
            for (String str2 : strArrSplit) {
                stringBuffer.append(str2);
                stringBuffer.append("&");
            }
            String strE = e(stringBuffer.toString());
            if (strE.length() > 1) {
                return (String) strE.subSequence(0, strE.length() - 1);
            }
        } catch (Throwable th) {
            jt.a(th, "ut", "sPa");
        }
        return str;
    }

    private static String b(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
        }
        return sb.toString();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private String f() {
        return ((CloudSearch.Query) this.b).getSortingrules() != null ? ((CloudSearch.Query) this.b).getSortingrules().toString() : "";
    }

    /* JADX WARN: Multi-variable type inference failed */
    private String g() {
        StringBuffer stringBuffer = new StringBuffer();
        String filterString = ((CloudSearch.Query) this.b).getFilterString();
        String filterNumString = ((CloudSearch.Query) this.b).getFilterNumString();
        stringBuffer.append(filterString);
        if (!fp.a(filterString) && !fp.a(filterNumString)) {
            stringBuffer.append("&&");
        }
        stringBuffer.append(filterNumString);
        return stringBuffer.toString();
    }
}
