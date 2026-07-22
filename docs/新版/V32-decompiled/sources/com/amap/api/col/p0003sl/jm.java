package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.ik;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: NetReuestParam.java */
/* JADX INFO: loaded from: classes2.dex */
public final class jm {
    private static final String a = it.c("SRFZHZUVZT3BOa0ZiemZRQQ");
    private static final String b = it.c("FbGJzX3Nkaw");
    private static final String c = it.c("SWjJuYVh2eEMwSzVmNklFSmh0UXpVb2xtOVM4eU9Ua3E");
    private static final String d = it.c("FQU5EU0RLMTA");
    private static final String e = it.c("FMTAw");
    private static boolean f = false;
    private String g = "";

    public static ik.a a() {
        return new ik.a() { // from class: com.amap.api.col.3sl.jm.1
            private jm a = new jm();

            @Override // com.amap.api.col.3sl.ik.a
            public final lb a(byte[] bArr, Map<String, String> map) {
                return new kt(bArr, map);
            }

            @Override // com.amap.api.col.3sl.ik.a
            public final String a() {
                return jm.c();
            }

            @Override // com.amap.api.col.3sl.ik.a
            public final String a(Context context, String str) {
                return jm.a(context, str);
            }

            @Override // com.amap.api.col.3sl.ik.a
            public final Map<String, String> b() {
                return this.a.b();
            }

            @Override // com.amap.api.col.3sl.ik.a
            public final String a(String str, String str2, String str3, String str4) {
                return this.a.a(str, str2, str3, str4);
            }
        };
    }

    public static String a(Context context, String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.optInt(it.c("UY29kZQ")) != 1) {
                return "";
            }
            String strOptString = new JSONObject(jSONObject.optString(it.c("FZGF0YQ"))).optString(it.c("FYWRpdQ"));
            if (TextUtils.isEmpty(strOptString)) {
                return "";
            }
            jn.a(strOptString);
            ji.a(context).a(strOptString);
            return strOptString;
        } catch (JSONException e2) {
            e2.printStackTrace();
            return "";
        }
    }

    public final synchronized Map<String, String> b() {
        if (f) {
            return null;
        }
        f = true;
        HashMap map = new HashMap();
        map.put(it.c("FZW50"), it.c("FMg"));
        StringBuilder sb = new StringBuilder();
        StringBuilder sbAppend = sb.append(it.c("SY2hhbm5lbD0"));
        String str = b;
        StringBuilder sbAppend2 = sbAppend.append(str).append(it.c("SJmRpdj0"));
        String str2 = d;
        sbAppend2.append(str2);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(str).append(str2).append(it.c("FQA")).append(c);
        sb.append(it.c("FJnNpZ249")).append(jp.a(stringBuffer.toString()).toUpperCase(Locale.US));
        sb.append(it.c("SJm91dHB1dD1qc29u") + "\u0000");
        map.put(it.c("FaW4"), jk.a(kq.a(sb.toString().getBytes(), a.getBytes())));
        map.put(it.c("Sa2V5dA"), e);
        return map;
    }

    private String d() {
        if (!TextUtils.isEmpty(this.g)) {
            return this.g;
        }
        String strA = il.a("TUpJaVFGNk5LXHtSX1ZwQlRiV1VVZmtYWU1haV1hYWHCiXJtZcKLdmp8wpFewo1/wphwwoFzZmR8aWp6X2k6XsKDwoF+WGbChGdAScKLwoVXfmNxYEvCjcKLSG7CjGNvwoZtVFZ7WMKXYMKfwo5dZcKHfzZXUG85X0hNOVJrb2U8ZlJGW8KCe8KOV8KQWllrcGrCjcKIT25lUHPCicKGVsKKeG5fwp56XsKbc8KJbUVYR0pqU09gfE5/WT5YeHNAwoDCh1Z4V8KQT3JQYmxQbcKYwpFxdG/Ci3rCmMKQwop+YVbCmWFxwpxBdW07Zjp/ODlAbcKEY1pQwoJowohbV1VmV1laWmtcYGbClXfCk2NvesKdwohdWFnCol/CjWTCmMKicG1ENnAvPFtpcXtfclhfXsKAwolgRWNbS29OwpFafV3CkMKLTcKCwolrU3DCmGnCmX9wdsKPcXDCg3LCnFpGcDVTeTxNWW07bXJePVRfQn3ChGNraFhbwpNcwpXChMKNaFVjeVF8wojChm9YbmvChGDCmHvChGVQWjo0Z3o9djleOztWcVxSfWE9woLChkZdcGTCgVzCjMKUVE12wpV5bcKVwprCnntZworCgsKfwpHCksKnwpHClURURW9YaDtwXU1bck5YX3hSVFZUYlxKWFlua1xeYm9jU8KDa3ZrwpZ5am9Za3jCknR3fA");
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < strA.length(); i++) {
            stringBuffer.append((char) (strA.charAt(i) - (i % 48)));
        }
        String string = stringBuffer.toString();
        StringBuffer stringBuffer2 = new StringBuffer();
        for (int i2 = 0; i2 < string.length() / 2; i2++) {
            stringBuffer2.append((char) ((string.charAt(i2) + string.charAt((string.length() - 1) - i2)) / 2));
        }
        String string2 = stringBuffer2.toString();
        this.g = string2;
        return string2;
    }

    public final String a(String str, String str2, String str3, String str4) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(it.c("LdGlk"), str);
            jSONObject.put(it.c("FZGl1"), str2);
            jSONObject.put(it.c("AZGl1Mg"), str3);
            jSONObject.put(it.c("EZGl1Mw"), str4);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        String string = jSONObject.toString();
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        String strA = jp.a();
        if (!TextUtils.isEmpty(strA)) {
            String strA2 = jk.a(kq.a((string + "\u0000").getBytes(), strA.getBytes()));
            if (!TextUtils.isEmpty(strA2)) {
                try {
                    return it.c("Fa2V5PQ") + URLEncoder.encode(jk.a(jo.a(strA.getBytes("utf-8"), jo.a(d())))) + it.c("SJmRhdGE9") + URLEncoder.encode(strA2);
                } catch (Throwable th2) {
                    th2.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String c() {
        return jn.a();
    }
}
