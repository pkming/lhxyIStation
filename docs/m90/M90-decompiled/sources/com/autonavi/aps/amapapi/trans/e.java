package com.autonavi.aps.amapapi.trans;

import android.content.Context;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.ig;
import com.amap.api.col.p0003sl.lc;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.services.district.DistrictSearchQuery;
import com.autonavi.aps.amapapi.utils.h;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: compiled from: Parser.java */
/* JADX INFO: loaded from: classes2.dex */
public final class e {
    private StringBuilder a = new StringBuilder();
    private AMapLocationClientOption b = new AMapLocationClientOption();

    public final void a(AMapLocationClientOption aMapLocationClientOption) {
        if (aMapLocationClientOption == null) {
            this.b = new AMapLocationClientOption();
        } else {
            this.b = aMapLocationClientOption;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:116:0x0291  */
    /* JADX WARN: Removed duplicated region for block: B:120:0x029d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final com.autonavi.aps.amapapi.model.a a(com.autonavi.aps.amapapi.model.a r22, byte[] r23, com.autonavi.aps.amapapi.a r24) {
        /*
            Method dump skipped, instruction units count: 686
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.autonavi.aps.amapapi.trans.e.a(com.autonavi.aps.amapapi.model.a, byte[], com.autonavi.aps.amapapi.a):com.autonavi.aps.amapapi.model.a");
    }

    private void a(com.autonavi.aps.amapapi.model.a aVar, String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(str)) {
            sb.append(str).append(" ");
        }
        if (!TextUtils.isEmpty(str2)) {
            a(str, str2, sb);
        }
        if (!TextUtils.isEmpty(str3)) {
            sb.append(str3).append(" ");
        }
        if (!TextUtils.isEmpty(str4)) {
            sb.append(str4).append(" ");
        }
        if (!TextUtils.isEmpty(str5)) {
            sb.append(str5).append(" ");
        }
        if (!TextUtils.isEmpty(str6)) {
            a(str7, str6, sb, aVar);
        }
        Bundle bundle = new Bundle();
        bundle.putString("citycode", aVar.getCityCode());
        bundle.putString("desc", sb.toString());
        bundle.putString("adcode", aVar.getAdCode());
        aVar.setExtras(bundle);
        aVar.g(sb.toString());
        String adCode = aVar.getAdCode();
        if (adCode != null && adCode.trim().length() > 0 && this.b.getGeoLanguage() != AMapLocationClientOption.GeoLanguage.EN) {
            aVar.setAddress(sb.toString().replace(" ", ""));
        } else {
            aVar.setAddress(sb.toString());
        }
    }

    private void a(String str, String str2, StringBuilder sb) {
        if (this.b.getGeoLanguage() == AMapLocationClientOption.GeoLanguage.EN) {
            if (str2.equals(str)) {
                return;
            }
            sb.append(str2).append(" ");
        } else {
            if (str.contains("市") && str.equals(str2)) {
                return;
            }
            sb.append(str2).append(" ");
        }
    }

    private void a(String str, String str2, StringBuilder sb, com.autonavi.aps.amapapi.model.a aVar) {
        if (!TextUtils.isEmpty(str) && this.b.getGeoLanguage() != AMapLocationClientOption.GeoLanguage.EN) {
            sb.append("靠近");
            sb.append(str2).append(" ");
            aVar.setDescription("在" + str2 + "附近");
        } else {
            sb.append("Near ".concat(String.valueOf(str2)));
            aVar.setDescription("Near ".concat(String.valueOf(str2)));
        }
    }

    public final com.autonavi.aps.amapapi.model.a a(String str) {
        String str2;
        try {
            com.autonavi.aps.amapapi.model.a aVar = new com.autonavi.aps.amapapi.model.a("");
            JSONObject jSONObjectOptJSONObject = new JSONObject(str).optJSONObject("regeocode");
            JSONObject jSONObjectOptJSONObject2 = jSONObjectOptJSONObject.optJSONObject("addressComponent");
            aVar.setCountry(b(jSONObjectOptJSONObject2.optString("country")));
            String strB = b(jSONObjectOptJSONObject2.optString(DistrictSearchQuery.KEYWORDS_PROVINCE));
            aVar.setProvince(strB);
            String strB2 = b(jSONObjectOptJSONObject2.optString("citycode"));
            aVar.setCityCode(strB2);
            String strOptString = jSONObjectOptJSONObject2.optString("city");
            if (strB2.endsWith("010") || strB2.endsWith("021") || strB2.endsWith("022") || strB2.endsWith("023")) {
                if (strB != null && strB.length() > 0) {
                    aVar.setCity(strB);
                    strOptString = strB;
                }
            } else {
                strOptString = b(strOptString);
                aVar.setCity(strOptString);
            }
            if (TextUtils.isEmpty(strOptString)) {
                aVar.setCity(strB);
                strOptString = strB;
            }
            String strB3 = b(jSONObjectOptJSONObject2.optString(DistrictSearchQuery.KEYWORDS_DISTRICT));
            aVar.setDistrict(strB3);
            String strB4 = b(jSONObjectOptJSONObject2.optString("adcode"));
            aVar.setAdCode(strB4);
            JSONObject jSONObjectOptJSONObject3 = jSONObjectOptJSONObject2.optJSONObject("streetNumber");
            String strB5 = b(jSONObjectOptJSONObject3.optString("street"));
            aVar.setStreet(strB5);
            aVar.setRoad(strB5);
            String strB6 = b(jSONObjectOptJSONObject3.optString("number"));
            aVar.setNumber(strB6);
            JSONArray jSONArrayOptJSONArray = jSONObjectOptJSONObject.optJSONArray("pois");
            if (jSONArrayOptJSONArray.length() > 0) {
                String strB7 = b(jSONArrayOptJSONArray.getJSONObject(0).optString("name"));
                aVar.setPoiName(strB7);
                str2 = strB7;
            } else {
                str2 = null;
            }
            JSONArray jSONArrayOptJSONArray2 = jSONObjectOptJSONObject.optJSONArray("aois");
            if (jSONArrayOptJSONArray2.length() > 0) {
                aVar.setAoiName(b(jSONArrayOptJSONArray2.getJSONObject(0).optString("name")));
            }
            a(aVar, strB, strOptString, strB3, strB5, strB6, str2, strB4);
            return aVar;
        } catch (Throwable unused) {
            return null;
        }
    }

    private static String b(String str) {
        return "[]".equals(str) ? "" : str;
    }

    public final com.autonavi.aps.amapapi.model.a a(String str, Context context, lc lcVar, com.autonavi.aps.amapapi.a aVar) {
        com.autonavi.aps.amapapi.model.a aVar2 = new com.autonavi.aps.amapapi.model.a("");
        aVar2.setErrorCode(7);
        StringBuffer stringBuffer = new StringBuffer();
        try {
            stringBuffer.append("#SHA1AndPackage#").append(ig.e(context));
            String str2 = lcVar.b.get("gsid").get(0);
            if (!TextUtils.isEmpty(str2)) {
                stringBuffer.append("#gsid#").append(str2);
            }
            String str3 = lcVar.c;
            if (!TextUtils.isEmpty(str3)) {
                stringBuffer.append("#csid#".concat(String.valueOf(str3)));
            }
        } catch (Throwable unused) {
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (!jSONObject.has("status") || !jSONObject.has(DocumentsContract.EXTRA_INFO)) {
                aVar.f("#0702");
                this.a.append("json is error:").append(str).append(stringBuffer).append("#0702");
            }
            String string = jSONObject.getString("status");
            String string2 = jSONObject.getString(DocumentsContract.EXTRA_INFO);
            String string3 = jSONObject.getString("infocode");
            if ("0".equals(string)) {
                aVar.f("#0701");
                this.a.append("auth fail:").append(string2).append(stringBuffer).append("#0701");
                h.a(lcVar.d, string3, string2);
            }
        } catch (Throwable th) {
            aVar.f("#0703");
            this.a.append("json exception error:").append(th.getMessage()).append(stringBuffer).append("#0703");
            com.autonavi.aps.amapapi.utils.b.a(th, "parser", "paseAuthFailurJson");
        }
        aVar2.setLocationDetail(this.a.toString());
        if (this.a.length() > 0) {
            StringBuilder sb = this.a;
            sb.delete(0, sb.length());
        }
        return aVar2;
    }

    private static void a(com.autonavi.aps.amapapi.model.a aVar, short s) {
        if (!"-1".equals(aVar.d())) {
            if (s == -1) {
                s = 0;
            } else if (s == 0) {
                s = -1;
            }
            aVar.setConScenario(s);
            return;
        }
        if (s == 101) {
            s = 100;
        }
        aVar.setConScenario(s);
    }
}
