package com.autonavi.aps.amapapi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.SparseArray;
import com.amap.api.col.p0003sl.Cif;
import com.amap.api.col.p0003sl.jw;
import com.amap.api.col.p0003sl.li;
import com.amap.api.col.p0003sl.lj;
import com.amap.api.col.p0003sl.lk;
import com.amap.api.col.p0003sl.ll;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClientOption;
import com.unisound.client.SpeechConstants;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: compiled from: ReportUtil.java */
/* JADX INFO: loaded from: classes2.dex */
public final class h {
    public SparseArray<Long> a = new SparseArray<>();
    public int b = -1;
    public long c = 0;
    String[] d = {"ol", "cl", "gl", "ha", "bs", "ds"};
    public int e = -1;
    public long f = -1;
    private static List<lk> i = new ArrayList();
    private static JSONArray j = null;
    static AMapLocation g = null;
    static boolean h = false;

    private static String a(int i2) {
        if (i2 == 2011) {
            return "ContextIsNull";
        }
        if (i2 == 2031) {
            return "CreateApsReqException";
        }
        if (i2 == 2041) {
            return "ResponseResultIsNull";
        }
        if (i2 == 2081) {
            return "LocalLocException";
        }
        if (i2 == 2091) {
            return "InitException";
        }
        if (i2 == 2111) {
            return "ErrorCgiInfo";
        }
        if (i2 == 2121) {
            return "NotLocPermission";
        }
        if (i2 == 2141) {
            return "NoEnoughStatellites";
        }
        if (i2 == 2021) {
            return "OnlyMainWifi";
        }
        if (i2 == 2022) {
            return "OnlyOneWifiButNotMain";
        }
        if (i2 == 2061) {
            return "ServerRetypeError";
        }
        if (i2 == 2062) {
            return "ServerLocFail";
        }
        switch (i2) {
            case 2051:
                return "NeedLoginNetWork\t";
            case 2052:
                return "MaybeIntercepted";
            case 2053:
                return "DecryptResponseException";
            case 2054:
                return "ParserDataException";
            default:
                switch (i2) {
                    case 2101:
                        return "BindAPSServiceException";
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_START /* 2102 */:
                        return "AuthClientScodeFail";
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_END /* 2103 */:
                        return "NotConfigAPSService";
                    default:
                        switch (i2) {
                            case 2131:
                                return "NoCgiOAndWifiInfo";
                            case 2132:
                                return "AirPlaneModeAndWifiOff";
                            case 2133:
                                return "NoCgiAndWifiOff";
                            default:
                                switch (i2) {
                                    case 2151:
                                        return "MaybeMockNetLoc";
                                    case 2152:
                                        return "MaybeMockGPSLoc";
                                    case 2153:
                                        return "UNSUPPORT_COARSE_LBSLOC";
                                    case 2154:
                                        return "UNSUPPORT_CONTINUE_LOC";
                                    default:
                                        return "";
                                }
                        }
                }
        }
    }

    private static boolean a(AMapLocation aMapLocation) {
        return j.a(aMapLocation) ? !b.a(aMapLocation.getLatitude(), aMapLocation.getLongitude()) : "http://abroad.apilocate.amap.com/mobile/binary".equals(b.c);
    }

    public static void a(Context context, AMapLocation aMapLocation, com.autonavi.aps.amapapi.a aVar) {
        int i2;
        if (aMapLocation == null) {
            return;
        }
        try {
            if (!"gps".equalsIgnoreCase(aMapLocation.getProvider()) && aMapLocation.getLocationType() != 1) {
                String str = a(aMapLocation) ? "abroad" : "domestic";
                String str2 = "cache";
                if (aMapLocation.getErrorCode() != 0) {
                    int errorCode = aMapLocation.getErrorCode();
                    if (errorCode == 4 || errorCode == 5 || errorCode == 6 || errorCode == 11) {
                        str2 = "net";
                    }
                    i2 = 0;
                } else {
                    int locationType = aMapLocation.getLocationType();
                    if (locationType == 5 || locationType == 6) {
                        str2 = "net";
                    }
                    i2 = 1;
                }
                a(context, "O016", str2, str, i2, aMapLocation.getErrorCode(), aVar);
            }
        } catch (Throwable th) {
            b.a(th, "ReportUtil", "reportBatting");
        }
    }

    public static void a(Context context, long j2, boolean z) {
        if (context != null) {
            try {
                if (a.a()) {
                    a(context, j2, z, "O015");
                }
            } catch (Throwable th) {
                b.a(th, "ReportUtil", "reportGPSLocUseTime");
            }
        }
    }

    public static void b(Context context, long j2, boolean z) {
        if (context != null) {
            try {
                if (a.a()) {
                    a(context, j2, z, "O024");
                }
            } catch (Throwable th) {
                b.a(th, "ReportUtil", "reportCoarseLocUseTime");
            }
        }
    }

    private static void a(Context context, long j2, boolean z, String str) {
        a(context, str, !z ? "abroad" : "domestic", Long.valueOf(j2).intValue());
    }

    private static void a(Context context, String str, String str2, String str3, int i2, int i3, com.autonavi.aps.amapapi.a aVar) {
        if (context != null) {
            try {
                if (a.a()) {
                    JSONObject jSONObject = new JSONObject();
                    if (!TextUtils.isEmpty(str2)) {
                        jSONObject.put("param_string_first", str2);
                    }
                    if (!TextUtils.isEmpty(str3)) {
                        jSONObject.put("param_string_second", str3);
                    }
                    if (i2 != Integer.MAX_VALUE) {
                        jSONObject.put("param_int_first", i2);
                    }
                    if (i3 != Integer.MAX_VALUE) {
                        jSONObject.put("param_int_second", i3);
                    }
                    if (aVar != null) {
                        if (!TextUtils.isEmpty(aVar.d())) {
                            jSONObject.put("dns", aVar.d());
                        }
                        if (!TextUtils.isEmpty(aVar.e())) {
                            jSONObject.put("domain", aVar.e());
                        }
                        if (!TextUtils.isEmpty(aVar.f())) {
                            jSONObject.put("type", aVar.f());
                        }
                        if (!TextUtils.isEmpty(aVar.g())) {
                            jSONObject.put("reason", aVar.g());
                        }
                        if (!TextUtils.isEmpty(aVar.c())) {
                            jSONObject.put("ip", aVar.c());
                        }
                        if (!TextUtils.isEmpty(aVar.b())) {
                            jSONObject.put("stack", aVar.b());
                        }
                        if (aVar.h() > 0) {
                            jSONObject.put("ctime", String.valueOf(aVar.h()));
                        }
                        if (aVar.a() > 0) {
                            jSONObject.put("ntime", String.valueOf(aVar.a()));
                        }
                    }
                    a(context, str, jSONObject);
                }
            } catch (Throwable th) {
                b.a(th, "ReportUtil", "applyStatisticsEx");
            }
        }
    }

    private static void a(Context context, String str, String str2, int i2) {
        if (context != null) {
            try {
                if (a.a()) {
                    JSONObject jSONObject = new JSONObject();
                    if (!TextUtils.isEmpty(str2)) {
                        jSONObject.put("param_string_first", str2);
                    }
                    if (!TextUtils.isEmpty(null)) {
                        jSONObject.put("param_string_second", (Object) null);
                    }
                    if (i2 != Integer.MAX_VALUE) {
                        jSONObject.put("param_int_first", i2);
                    }
                    a(context, str, jSONObject);
                }
            } catch (Throwable th) {
                b.a(th, "ReportUtil", "applyStatisticsEx");
            }
        }
    }

    public static synchronized void a(Context context, String str, JSONObject jSONObject) {
        if (context != null) {
            try {
                if (a.a()) {
                    lk lkVar = new lk(context, "loc", "6.4.3", str);
                    if (jSONObject != null) {
                        lkVar.a(jSONObject.toString());
                    }
                    i.add(lkVar);
                    if (i.size() >= 30) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.addAll(i);
                        ll.b(arrayList, context);
                        i.clear();
                    }
                }
            } catch (Throwable th) {
                b.a(th, "ReportUtil", "applyStatistics");
            }
        }
    }

    public static synchronized void a(Context context) {
        if (context != null) {
            try {
                if (a.a()) {
                    List<lk> list = i;
                    if (list != null && list.size() > 0) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.addAll(i);
                        ll.b(arrayList, context);
                        i.clear();
                    }
                    f(context);
                }
            } catch (Throwable th) {
                b.a(th, "ReportUtil", "destroy");
            }
        }
    }

    public static void a(String str, String str2) {
        try {
            jw.b(b.c(), str2, str);
        } catch (Throwable th) {
            b.a(th, "ReportUtil", "reportLog");
        }
    }

    public final void a(Context context, int i2) {
        try {
            int i3 = this.b;
            if (i3 == i2) {
                return;
            }
            if (i3 != -1 && i3 != i2) {
                this.a.append(this.b, Long.valueOf((j.b() - this.c) + this.a.get(this.b, 0L).longValue()));
            }
            this.c = j.b() - i.a(context, "pref1", this.d[i2], 0L);
            this.b = i2;
        } catch (Throwable th) {
            b.a(th, "ReportUtil", "setLocationType");
        }
    }

    public final void b(Context context) {
        try {
            long jB = j.b() - this.c;
            int i2 = this.b;
            if (i2 != -1) {
                this.a.append(this.b, Long.valueOf(jB + this.a.get(i2, 0L).longValue()));
            }
            long jB2 = j.b() - this.f;
            int i3 = this.e;
            if (i3 != -1) {
                this.a.append(this.e, Long.valueOf(jB2 + this.a.get(i3, 0L).longValue()));
            }
            SharedPreferences.Editor editorA = i.a(context, "pref1");
            for (int i4 = 0; i4 < this.d.length; i4++) {
                long jLongValue = this.a.get(i4, 0L).longValue();
                if (jLongValue > 0 && jLongValue > i.a(context, "pref1", this.d[i4], 0L)) {
                    i.a(editorA, this.d[i4], jLongValue);
                }
            }
            i.a(editorA);
        } catch (Throwable th) {
            b.a(th, "ReportUtil", "saveLocationTypeAndMode");
        }
    }

    /* JADX INFO: renamed from: com.autonavi.aps.amapapi.utils.h$1, reason: invalid class name */
    /* JADX INFO: compiled from: ReportUtil.java */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] a;

        static {
            int[] iArr = new int[AMapLocationClientOption.AMapLocationMode.values().length];
            a = iArr;
            try {
                iArr[AMapLocationClientOption.AMapLocationMode.Battery_Saving.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                a[AMapLocationClientOption.AMapLocationMode.Device_Sensors.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                a[AMapLocationClientOption.AMapLocationMode.Hight_Accuracy.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public final void a(Context context, AMapLocationClientOption aMapLocationClientOption) {
        try {
            int i2 = AnonymousClass1.a[aMapLocationClientOption.getLocationMode().ordinal()];
            int i3 = 3;
            if (i2 == 1) {
                i3 = 4;
            } else if (i2 == 2) {
                i3 = 5;
            } else if (i2 != 3) {
                i3 = -1;
            }
            int i4 = this.e;
            if (i4 == i3) {
                return;
            }
            if (i4 != -1 && i4 != i3) {
                this.a.append(this.e, Long.valueOf((j.b() - this.f) + this.a.get(this.e, 0L).longValue()));
            }
            this.f = j.b() - i.a(context, "pref1", this.d[i3], 0L);
            this.e = i3;
        } catch (Throwable th) {
            b.a(th, "ReportUtil", "setLocationMode");
        }
    }

    public final int c(Context context) {
        try {
            long jA = i.a(context, "pref1", this.d[2], 0L);
            long jA2 = i.a(context, "pref1", this.d[0], 0L);
            long jA3 = i.a(context, "pref1", this.d[1], 0L);
            if (jA == 0 && jA2 == 0 && jA3 == 0) {
                return -1;
            }
            long j2 = jA2 - jA;
            long j3 = jA3 - jA;
            return jA > j2 ? jA > j3 ? 2 : 1 : j2 > j3 ? 0 : 1;
        } catch (Throwable unused) {
            return -1;
        }
    }

    public final int d(Context context) {
        try {
            long jA = i.a(context, "pref1", this.d[3], 0L);
            long jA2 = i.a(context, "pref1", this.d[4], 0L);
            long jA3 = i.a(context, "pref1", this.d[5], 0L);
            if (jA == 0 && jA2 == 0 && jA3 == 0) {
                return -1;
            }
            return jA > jA2 ? jA > jA3 ? 3 : 5 : jA2 > jA3 ? 4 : 5;
        } catch (Throwable unused) {
            return -1;
        }
    }

    public final void e(Context context) {
        try {
            SharedPreferences.Editor editorA = i.a(context, "pref1");
            int i2 = 0;
            while (true) {
                String[] strArr = this.d;
                if (i2 < strArr.length) {
                    i.a(editorA, strArr[i2], 0L);
                    i2++;
                } else {
                    i.a(editorA);
                    return;
                }
            }
        } catch (Throwable unused) {
        }
    }

    public static void a(Context context, int i2, int i3, long j2, long j3) {
        if (i2 == -1 || i3 == -1) {
            return;
        }
        try {
            a(context, "O012", i2, i3, j2, j3);
        } catch (Throwable th) {
            b.a(th, "ReportUtil", "reportServiceAliveTime");
        }
    }

    private static void a(Context context, String str, int i2, int i3, long j2, long j3) {
        if (context != null) {
            try {
                if (a.a()) {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("param_int_first", i2);
                    jSONObject.put("param_int_second", i3);
                    jSONObject.put("param_long_first", j2);
                    jSONObject.put("param_long_second", j3);
                    a(context, str, jSONObject);
                }
            } catch (Throwable th) {
                b.a(th, "ReportUtil", "applyStatisticsEx");
            }
        }
    }

    public static synchronized void a(Context context, AMapLocation aMapLocation) {
        int i2;
        try {
            if (j.a(aMapLocation)) {
                int locationType = aMapLocation.getLocationType();
                int i3 = 0;
                if (locationType == 1) {
                    i2 = i3;
                    i3 = 1;
                } else if (locationType == 2 || locationType == 4) {
                    i2 = 1;
                    i3 = 1;
                } else {
                    if (locationType == 11) {
                        i2 = 4;
                    } else if (locationType == 8) {
                        i3 = 3;
                        i2 = i3;
                    } else if (locationType != 9) {
                        i2 = 0;
                    } else {
                        i2 = 2;
                    }
                    i3 = 1;
                }
                if (i3 != 0) {
                    int iC = a.c();
                    if (iC != 0) {
                        if (i2 == 0 || i2 == 4) {
                            if (iC == 2) {
                                return;
                            }
                        } else if (iC == 1) {
                            return;
                        }
                    }
                    if (j == null) {
                        j = new JSONArray();
                    }
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("lon", j.b(aMapLocation.getLongitude()));
                    jSONObject.put("lat", j.b(aMapLocation.getLatitude()));
                    jSONObject.put("type", i2);
                    jSONObject.put("timestamp", j.a());
                    if (aMapLocation.getCoordType().equalsIgnoreCase(AMapLocation.COORD_TYPE_WGS84)) {
                        jSONObject.put("coordType", 1);
                    } else {
                        jSONObject.put("coordType", 2);
                    }
                    if (i2 == 0) {
                        JSONObject jSONObject2 = new JSONObject();
                        jSONObject2.put("accuracy", j.c(aMapLocation.getAccuracy()));
                        jSONObject2.put("altitude", j.c(aMapLocation.getAltitude()));
                        jSONObject2.put("bearing", j.c(aMapLocation.getBearing()));
                        jSONObject2.put("speed", j.c(aMapLocation.getSpeed()));
                        jSONObject.put("extension", jSONObject2);
                    }
                    JSONArray jSONArrayPut = j.put(jSONObject);
                    j = jSONArrayPut;
                    if (jSONArrayPut.length() >= a.b()) {
                        f(context);
                    }
                }
            }
        } catch (Throwable th) {
            b.a(th, "ReportUtil", "recordOfflineLocLog");
        }
    }

    private static void f(Context context) {
        try {
            JSONArray jSONArray = j;
            if (jSONArray == null || jSONArray.length() <= 0) {
                return;
            }
            lj.a(new li(context, b.c(), j.toString()), context);
            j = null;
        } catch (Throwable th) {
            b.a(th, "ReportUtil", "writeOfflineLocLog");
        }
    }

    public static void a(String str, int i2) {
        a(str, String.valueOf(i2), a(i2));
    }

    public static void a(String str, String str2, String str3) {
        try {
            jw.a(b.c(), "/mobile/binary", str3, str, str2);
        } catch (Throwable unused) {
        }
    }

    public static void a(String str, Throwable th) {
        try {
            if (th instanceof Cif) {
                jw.a(b.c(), str, (Cif) th);
            }
        } catch (Throwable unused) {
        }
    }

    public static void a(AMapLocation aMapLocation, AMapLocation aMapLocation2) {
        try {
            if (g == null) {
                if (!j.a(aMapLocation)) {
                    g = aMapLocation2;
                    return;
                }
                g = aMapLocation.m31clone();
            }
            if (j.a(g) && j.a(aMapLocation2)) {
                AMapLocation aMapLocationM31clone = aMapLocation2.m31clone();
                if (g.getLocationType() != 1 && g.getLocationType() != 9 && !"gps".equalsIgnoreCase(g.getProvider()) && g.getLocationType() != 7 && aMapLocationM31clone.getLocationType() != 1 && aMapLocationM31clone.getLocationType() != 9 && !"gps".equalsIgnoreCase(aMapLocationM31clone.getProvider()) && aMapLocationM31clone.getLocationType() != 7) {
                    long jAbs = Math.abs(aMapLocationM31clone.getTime() - g.getTime()) / 1000;
                    if (jAbs <= 0) {
                        jAbs = 1;
                    }
                    if (jAbs <= 1800) {
                        float fA = j.a(g, aMapLocationM31clone);
                        float f = fA / jAbs;
                        if (fA > 30000.0f && f > 1000.0f) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(g.getLatitude()).append(",");
                            sb.append(g.getLongitude()).append(",");
                            sb.append(g.getAccuracy()).append(",");
                            sb.append(g.getLocationType()).append(",");
                            if (aMapLocation.getTime() != 0) {
                                sb.append(j.a(g.getTime(), "yyyyMMdd_HH:mm:ss:SS"));
                            } else {
                                sb.append(g.getTime());
                            }
                            sb.append("#");
                            sb.append(aMapLocationM31clone.getLatitude()).append(",");
                            sb.append(aMapLocationM31clone.getLongitude()).append(",");
                            sb.append(aMapLocationM31clone.getAccuracy()).append(",");
                            sb.append(aMapLocationM31clone.getLocationType()).append(",");
                            if (aMapLocationM31clone.getTime() != 0) {
                                sb.append(j.a(aMapLocationM31clone.getTime(), "yyyyMMdd_HH:mm:ss:SS"));
                            } else {
                                sb.append(aMapLocationM31clone.getTime());
                            }
                            a("bigshiftstatistics", sb.toString());
                            sb.delete(0, sb.length());
                        }
                    }
                }
                g = aMapLocationM31clone;
            }
        } catch (Throwable unused) {
        }
    }

    public static void a(long j2, long j3) {
        try {
            if (h) {
                return;
            }
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("gpsTime:").append(j.a(j2, "yyyy-MM-dd HH:mm:ss.SSS")).append(",");
            stringBuffer.append("sysTime:").append(j.a(j3, "yyyy-MM-dd HH:mm:ss.SSS")).append(",");
            long jU = a.u();
            stringBuffer.append("serverTime:").append(0 != jU ? j.a(jU, "yyyy-MM-dd HH:mm:ss.SSS") : "0");
            a("checkgpstime", stringBuffer.toString());
            if (0 != jU && Math.abs(j2 - jU) < 31536000000L) {
                stringBuffer.append(", correctError");
                a("checkgpstimeerror", stringBuffer.toString());
            }
            stringBuffer.delete(0, stringBuffer.length());
            h = true;
        } catch (Throwable unused) {
        }
    }
}
