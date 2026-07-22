package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.services.core.AMapException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: ShareUrlSearchHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class gw extends fg<String, String> {
    private String g;

    @Override // com.amap.api.col.p0003sl.fg
    protected final String c() {
        return null;
    }

    @Override // com.amap.api.col.p0003sl.fg
    protected final /* synthetic */ String a(String str) throws AMapException {
        return b(str);
    }

    public gw(Context context, String str) {
        super(context, str);
        this.g = str;
    }

    @Override // com.amap.api.col.p0003sl.fg, com.amap.api.col.p0003sl.lb
    public final Map<String, String> getParams() {
        byte[] bArrA;
        StringBuilder sb = new StringBuilder();
        sb.append("channel=open_api&flag=1").append("&address=" + URLEncoder.encode(this.g));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("open_api1").append(this.g).append("@8UbJH6N2szojnTHONAWzB6K7N1kaj7Y0iUMarxac");
        sb.append("&sign=").append(io.b(stringBuffer.toString()).toUpperCase(Locale.US));
        sb.append("&output=json");
        try {
            bArrA = hg.a(sb.toString().getBytes("utf-8"), "Yaynpa84IKOfasFx".getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            fp.a(e, "ShareUrlSearchHandler", "getParams");
            bArrA = null;
        }
        HashMap map = new HashMap();
        map.put("ent", "2");
        map.put("in", il.b(bArrA));
        map.put("keyt", "openapi");
        return map;
    }

    private static String b(String str) throws AMapException {
        try {
            JSONObject jSONObject = new JSONObject(str);
            String strA = fx.a(jSONObject, "code");
            String strA2 = fx.a(jSONObject, "message");
            if ("1".equals(strA)) {
                return fx.a(jSONObject, "transfer_url");
            }
            if ("0".equals(strA)) {
                throw new AMapException(AMapException.AMAP_SERVICE_UNKNOWN_ERROR, 0, strA2);
            }
            if ("2".equals(strA)) {
                throw new AMapException(AMapException.AMAP_SHARE_FAILURE, 0, strA2);
            }
            if ("3".equals(strA)) {
                throw new AMapException(AMapException.AMAP_SERVICE_INVALID_PARAMS, 0, strA2);
            }
            if ("4".equals(strA)) {
                throw new AMapException("用户签名未通过", 0, strA2);
            }
            if ("5".equals(strA)) {
                throw new AMapException(AMapException.AMAP_SHARE_LICENSE_IS_EXPIRED, 0, strA2);
            }
            return null;
        } catch (JSONException e) {
            fp.a(e, "ShareUrlSearchHandler", "paseJSON");
            return null;
        }
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final String getURL() {
        return fo.g();
    }
}
