package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: InputtipsHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fw extends fh<InputtipsQuery, ArrayList<Tip>> {
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final /* synthetic */ Object a(String str) throws AMapException {
        return c(str);
    }

    public fw(Context context, InputtipsQuery inputtipsQuery) {
        super(context, inputtipsQuery);
    }

    private static ArrayList<Tip> c(String str) throws AMapException {
        try {
            return fx.j(new JSONObject(str));
        } catch (JSONException e) {
            fp.a(e, "InputtipsHandler", "paseJSON");
            return null;
        }
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.a() + "/assistant/inputtips?";
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.amap.api.col.p0003sl.fh, com.amap.api.col.p0003sl.fg
    protected final String c() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("output=json");
        String strB = b(((InputtipsQuery) this.b).getKeyword());
        if (!TextUtils.isEmpty(strB)) {
            stringBuffer.append("&keywords=").append(strB);
        }
        String city = ((InputtipsQuery) this.b).getCity();
        if (!fx.i(city)) {
            stringBuffer.append("&city=").append(b(city));
        }
        String type = ((InputtipsQuery) this.b).getType();
        if (!fx.i(type)) {
            stringBuffer.append("&type=").append(b(type));
        }
        if (((InputtipsQuery) this.b).getCityLimit()) {
            stringBuffer.append("&citylimit=true");
        } else {
            stringBuffer.append("&citylimit=false");
        }
        LatLonPoint location = ((InputtipsQuery) this.b).getLocation();
        if (location != null) {
            stringBuffer.append("&location=").append(location.getLongitude()).append(",").append(location.getLatitude());
        }
        stringBuffer.append("&key=").append(ig.f(this.e));
        return stringBuffer.toString();
    }
}
