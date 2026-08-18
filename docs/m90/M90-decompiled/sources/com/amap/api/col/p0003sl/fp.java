package com.amap.api.col.p0003sl;

import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.lianhexinye.m90.gps.Function;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: CoreUtil.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fp {
    public static boolean a(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static void b(String str) throws AMapException {
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.has("errcode")) {
                a(jSONObject.getInt("errcode"), jSONObject.getString("errmsg"));
                return;
            }
            if (jSONObject.has("status")) {
                String string = jSONObject.getString("status");
                if (string.equals("1")) {
                    return;
                }
                if (string.equals("0") && !jSONObject.has("infocode")) {
                    throw new AMapException(AMapException.AMAP_CLIENT_UNKNOWN_ERROR);
                }
                int i = jSONObject.getInt("infocode");
                if (string.equals("0")) {
                    a(i, jSONObject.getString(DocumentsContract.EXTRA_INFO));
                }
            }
        } catch (JSONException e) {
            a(e, "CoreUtil", "paseAuthFailurJson");
            throw new AMapException("协议解析错误 - ProtocolException");
        }
    }

    public static void c(String str) throws AMapException {
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.has("errcode")) {
                a(jSONObject.getInt("errcode"), jSONObject.getString("errmsg"));
                return;
            }
            if (jSONObject.has("status")) {
                if (jSONObject.optInt("status") == 0) {
                    if (!jSONObject.has("infocode")) {
                        throw new AMapException(AMapException.AMAP_CLIENT_UNKNOWN_ERROR);
                    }
                    a(jSONObject.getInt("infocode"), jSONObject.getString(DocumentsContract.EXTRA_INFO));
                }
                int iOptInt = jSONObject.optInt("code");
                if (iOptInt == 0) {
                    return;
                }
                String strOptString = jSONObject.optString("message");
                throw new AMapException(strOptString, 2, strOptString, Integer.parseInt("1".concat(String.valueOf(iOptInt))));
            }
        } catch (JSONException e) {
            a(e, "CoreUtil", "paseAuthFailurJson");
            throw new AMapException("协议解析错误 - ProtocolException");
        }
    }

    private static void a(int i, String str) throws JSONException, AMapException {
        if (i != 0) {
            if (i == 22000) {
                throw new AMapException(AMapException.AMAP_SERVICE_TABLEID_NOT_EXIST, 2, str);
            }
            if (i == 32200) {
                throw new AMapException(AMapException.AMAP_NEARBY_INVALID_USERID, 2, str);
            }
            if (i != 32201) {
                switch (i) {
                    case 10000:
                        return;
                    case 10001:
                        throw new AMapException(AMapException.AMAP_INVALID_USER_KEY, 2, str);
                    case 10002:
                        throw new AMapException(AMapException.AMAP_SERVICE_NOT_AVAILBALE, 2, str);
                    case 10003:
                        throw new AMapException(AMapException.AMAP_DAILY_QUERY_OVER_LIMIT, 2, str);
                    case KeyEvent.KEYCODE_ZOOM /* 10004 */:
                        throw new AMapException(AMapException.AMAP_ACCESS_TOO_FREQUENT, 2, str);
                    case KeyEvent.KEYCODE_HELP /* 10005 */:
                        throw new AMapException(AMapException.AMAP_INVALID_USER_IP, 2, str);
                    case KeyEvent.KEYCODE_FAVOURITE /* 10006 */:
                        throw new AMapException(AMapException.AMAP_INVALID_USER_DOMAIN, 2, str);
                    case KeyEvent.KEYCODE_LOOP /* 10007 */:
                        throw new AMapException("用户签名未通过", 2, str);
                    case KeyEvent.KEYCODE_EXPAND /* 10008 */:
                        throw new AMapException(AMapException.AMAP_INVALID_USER_SCODE, 2, str);
                    case KeyEvent.KEYCODE_MOUSE /* 10009 */:
                        throw new AMapException(AMapException.AMAP_USERKEY_PLAT_NOMATCH, 2, str);
                    case KeyEvent.KEYCODE_MOVIE /* 10010 */:
                        throw new AMapException(AMapException.AMAP_IP_QUERY_OVER_LIMIT, 2, str);
                    case KeyEvent.KEYCODE_APPS /* 10011 */:
                        throw new AMapException(AMapException.AMAP_NOT_SUPPORT_HTTPS, 2, str);
                    case KeyEvent.KEYCODE_BROWSER /* 10012 */:
                        throw new AMapException(AMapException.AMAP_INSUFFICIENT_PRIVILEGES, 2, str);
                    case KeyEvent.KEYCODE_SCREENSHOT /* 10013 */:
                        throw new AMapException(AMapException.AMAP_USER_KEY_RECYCLED, 2, str);
                    default:
                        switch (i) {
                            case 20000:
                                throw new AMapException(AMapException.AMAP_SERVICE_INVALID_PARAMS, 2, str);
                            case 20001:
                                throw new AMapException(AMapException.AMAP_SERVICE_MISSING_REQUIRED_PARAMS, 2, str);
                            case 20002:
                                throw new AMapException(AMapException.AMAP_SERVICE_ILLEGAL_REQUEST, 2, str);
                            case 20003:
                                throw new AMapException(AMapException.AMAP_SERVICE_UNKNOWN_ERROR, 2, str);
                            default:
                                switch (i) {
                                    case 20800:
                                        throw new AMapException(AMapException.AMAP_ROUTE_OUT_OF_SERVICE, 2, str);
                                    case 20801:
                                        throw new AMapException(AMapException.AMAP_ROUTE_NO_ROADS_NEARBY, 2, str);
                                    case 20802:
                                        throw new AMapException(AMapException.AMAP_ROUTE_FAIL, 2, str);
                                    case 20803:
                                        throw new AMapException(AMapException.AMAP_OVER_DIRECTION_RANGE, 2, str);
                                    default:
                                        switch (i) {
                                            case Window.PROGRESS_SECONDARY_END /* 30000 */:
                                                throw new AMapException(AMapException.AMAP_ENGINE_RESPONSE_ERROR, 2, str);
                                            case 30001:
                                                throw new AMapException(AMapException.AMAP_ENGINE_RESPONSE_DATA_ERROR, 2, str);
                                            case 30002:
                                                throw new AMapException(AMapException.AMAP_ENGINE_CONNECT_TIMEOUT, 2, str);
                                            case 30003:
                                                throw new AMapException(AMapException.AMAP_ENGINE_RETURN_TIMEOUT, 2, str);
                                            default:
                                                switch (i) {
                                                    case 32000:
                                                        throw new AMapException(AMapException.AMAP_ENGINE_TABLEID_NOT_EXIST, 2, str);
                                                    case 32001:
                                                        throw new AMapException(AMapException.AMAP_ID_NOT_EXIST, 2, str);
                                                    case 32002:
                                                        throw new AMapException(AMapException.AMAP_SERVICE_MAINTENANCE, 2, str);
                                                    default:
                                                        if (!TextUtils.isEmpty(str) && i > 0) {
                                                            throw new AMapException(str, 2, str, i);
                                                        }
                                                        throw new AMapException(str, 2, str);
                                                }
                                        }
                                }
                        }
                }
            }
            throw new AMapException(AMapException.AMAP_NEARBY_KEY_NOT_BIND, 2, str);
        }
    }

    public static double a(double d) {
        return Double.parseDouble(new DecimalFormat("0.000000", new DecimalFormatSymbols(Locale.US)).format(d));
    }

    public static String a(LatLonPoint latLonPoint) {
        if (latLonPoint == null) {
            return "";
        }
        return a(latLonPoint.getLongitude()) + "," + a(latLonPoint.getLatitude());
    }

    public static Date d(String str) {
        if (str == null || str.trim().equals("")) {
            return null;
        }
        try {
            return new SimpleDateFormat("HHmm").parse(str);
        } catch (ParseException e) {
            a(e, "CoreUtil", "parseString2Time");
            return null;
        }
    }

    public static String a(Date date) {
        return date != null ? new SimpleDateFormat(Function.FORMAT_TIME).format(date) : "";
    }

    public static Date e(String str) {
        if (str == null || str.trim().equals("")) {
            return null;
        }
        try {
            return new SimpleDateFormat(Function.FORMAT_TIME).parse(str);
        } catch (ParseException e) {
            a(e, "CoreUtil", "parseTime");
            return null;
        }
    }

    public static String a(List<LatLonPoint> list) {
        return a(list, ";");
    }

    public static String a(List<LatLonPoint> list, String str) {
        if (list == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            LatLonPoint latLonPoint = list.get(i);
            if (latLonPoint != null) {
                stringBuffer.append(a(latLonPoint.getLongitude())).append(",").append(a(latLonPoint.getLatitude()));
                stringBuffer.append(str);
            }
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        return stringBuffer.toString();
    }

    public static void a(Throwable th, String str, String str2) {
        try {
            jw jwVarE = jw.e();
            if (jwVarE != null) {
                jwVarE.b(th, str, str2);
            }
            th.printStackTrace();
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
    }

    public static float a(LatLonPoint latLonPoint, LatLonPoint latLonPoint2) {
        if (latLonPoint == null || latLonPoint2 == null) {
            return 0.0f;
        }
        try {
            double longitude = latLonPoint.getLongitude();
            double d = longitude * 0.01745329251994329d;
            double latitude = latLonPoint.getLatitude() * 0.01745329251994329d;
            double longitude2 = latLonPoint2.getLongitude() * 0.01745329251994329d;
            double latitude2 = latLonPoint2.getLatitude() * 0.01745329251994329d;
            double dSin = Math.sin(d);
            double dSin2 = Math.sin(latitude);
            double dCos = Math.cos(d);
            double dCos2 = Math.cos(latitude);
            double dSin3 = Math.sin(longitude2);
            double dSin4 = Math.sin(latitude2);
            double dCos3 = Math.cos(longitude2);
            double dCos4 = Math.cos(latitude2);
            double[] dArr = {dCos * dCos2, dCos2 * dSin, dSin2};
            double[] dArr2 = {dCos3 * dCos4, dCos4 * dSin3, dSin4};
            return (float) (Math.asin(Math.sqrt((((dArr[0] - dArr2[0]) * (dArr[0] - dArr2[0])) + ((dArr[1] - dArr2[1]) * (dArr[1] - dArr2[1]))) + ((dArr[2] - dArr2[2]) * (dArr[2] - dArr2[2]))) / 2.0d) * 1.27420015798544E7d);
        } catch (Throwable th) {
            th.printStackTrace();
            return 0.0f;
        }
    }

    public static double b(List<LatLonPoint> list) {
        double latitude = 0.0d;
        if (list == null || list.size() < 3) {
            return 0.0d;
        }
        int size = list.size();
        int i = 0;
        while (i < size) {
            LatLonPoint latLonPoint = list.get(i);
            i++;
            LatLonPoint latLonPoint2 = list.get(i % size);
            double longitude = latLonPoint.getLongitude() * 111319.49079327357d * Math.cos(latLonPoint.getLatitude() * 0.017453292519943295d);
            double latitude2 = latLonPoint.getLatitude() * 111319.49079327357d;
            latitude += (longitude * (latLonPoint2.getLatitude() * 111319.49079327357d)) - (((latLonPoint2.getLongitude() * 111319.49079327357d) * Math.cos(latLonPoint2.getLatitude() * 0.017453292519943295d)) * latitude2);
        }
        return Math.abs(latitude / 2.0d);
    }

    public static String a(int i) {
        StringBuilder sb = new StringBuilder();
        if ((i & 1) != 0) {
            sb.append("cost,");
        }
        if ((i & 2) != 0) {
            sb.append("tmcs,");
        }
        if ((i & 4) != 0) {
            sb.append("navi,");
        }
        if ((i & 8) != 0) {
            sb.append("cities,");
        }
        if ((i & 16) != 0) {
            sb.append("polyline,");
        }
        if ((i & 32) != 0) {
            sb.append("elec_consume_info,");
        }
        if ((i & 64) != 0) {
            sb.append("charge_station_info,");
        }
        sb.replace(sb.length() - 1, sb.length(), "");
        return sb.toString();
    }
}
