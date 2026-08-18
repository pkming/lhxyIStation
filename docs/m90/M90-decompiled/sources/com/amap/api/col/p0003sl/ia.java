package com.amap.api.col.p0003sl;

import android.provider.DocumentsContract;
import android.view.KeyEvent;
import android.view.Window;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: CoreUtil.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ia {
    private static String[] a = {"com.amap.api.trace", "com.amap.api.trace.core"};

    public static void a(String str) throws hx {
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.has("errcode")) {
                a(jSONObject.getInt("errcode"), jSONObject.getString("errmsg"));
                return;
            }
            if (jSONObject.has("status") && jSONObject.has("infocode")) {
                String string = jSONObject.getString("status");
                int i = jSONObject.getInt("infocode");
                if ("1".equals(string)) {
                    return;
                }
                String string2 = jSONObject.getString(DocumentsContract.EXTRA_INFO);
                if ("0".equals(string)) {
                    a(i, string2);
                }
            }
        } catch (JSONException unused) {
            throw new hx("协议解析错误 - ProtocolException");
        }
    }

    private static void a(int i, String str) throws hx {
        if (i != 0) {
            switch (i) {
                case 10000:
                    return;
                case 10001:
                    throw new hx(AMapException.AMAP_INVALID_USER_KEY);
                case 10002:
                    throw new hx(AMapException.AMAP_SERVICE_NOT_AVAILBALE);
                case 10003:
                    throw new hx(AMapException.AMAP_DAILY_QUERY_OVER_LIMIT);
                case KeyEvent.KEYCODE_ZOOM /* 10004 */:
                    throw new hx(AMapException.AMAP_ACCESS_TOO_FREQUENT);
                case KeyEvent.KEYCODE_HELP /* 10005 */:
                    throw new hx(AMapException.AMAP_INVALID_USER_IP);
                case KeyEvent.KEYCODE_FAVOURITE /* 10006 */:
                    throw new hx(AMapException.AMAP_INVALID_USER_DOMAIN);
                case KeyEvent.KEYCODE_LOOP /* 10007 */:
                    throw new hx("用户签名未通过");
                case KeyEvent.KEYCODE_EXPAND /* 10008 */:
                    throw new hx(AMapException.AMAP_INVALID_USER_SCODE);
                case KeyEvent.KEYCODE_MOUSE /* 10009 */:
                    throw new hx(AMapException.AMAP_USERKEY_PLAT_NOMATCH);
                case KeyEvent.KEYCODE_MOVIE /* 10010 */:
                    throw new hx(AMapException.AMAP_IP_QUERY_OVER_LIMIT);
                case KeyEvent.KEYCODE_APPS /* 10011 */:
                    throw new hx(AMapException.AMAP_NOT_SUPPORT_HTTPS);
                case KeyEvent.KEYCODE_BROWSER /* 10012 */:
                    throw new hx(AMapException.AMAP_INSUFFICIENT_PRIVILEGES);
                case KeyEvent.KEYCODE_SCREENSHOT /* 10013 */:
                    throw new hx(AMapException.AMAP_USER_KEY_RECYCLED);
                default:
                    switch (i) {
                        case 20000:
                            throw new hx(AMapException.AMAP_SERVICE_INVALID_PARAMS);
                        case 20001:
                            throw new hx(AMapException.AMAP_SERVICE_MISSING_REQUIRED_PARAMS);
                        case 20002:
                            throw new hx(AMapException.AMAP_SERVICE_ILLEGAL_REQUEST);
                        case 20003:
                            throw new hx(AMapException.AMAP_SERVICE_UNKNOWN_ERROR);
                        default:
                            switch (i) {
                                case Window.PROGRESS_SECONDARY_END /* 30000 */:
                                    throw new hx(AMapException.AMAP_ENGINE_RESPONSE_ERROR);
                                case 30001:
                                    throw new hx(AMapException.AMAP_ENGINE_RESPONSE_DATA_ERROR);
                                case 30002:
                                    throw new hx(AMapException.AMAP_ENGINE_CONNECT_TIMEOUT);
                                case 30003:
                                    throw new hx(AMapException.AMAP_ENGINE_RETURN_TIMEOUT);
                                default:
                                    throw new hx(str);
                            }
                    }
            }
        }
    }

    public static int a(List<LatLng> list) {
        int i = 0;
        if (list == null || list.size() == 0) {
            return 0;
        }
        int iCalculateLineDistance = 0;
        while (i < list.size() - 1) {
            LatLng latLng = list.get(i);
            i++;
            LatLng latLng2 = list.get(i);
            if (latLng == null || latLng2 == null) {
                break;
            }
            iCalculateLineDistance = (int) (iCalculateLineDistance + AMapUtils.calculateLineDistance(latLng, latLng2));
        }
        return iCalculateLineDistance;
    }
}
