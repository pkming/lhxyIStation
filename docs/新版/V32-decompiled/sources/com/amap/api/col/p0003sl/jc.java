package com.amap.api.col.p0003sl;

import android.provider.DocumentsContract;
import android.text.TextUtils;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: compiled from: AMapLogEntity.java */
/* JADX INFO: loaded from: classes2.dex */
public final class jc {
    public static int a = 1;
    public static int b = 2;
    private String c;
    private int d;
    private long e = System.currentTimeMillis();
    private String f;

    private jc(int i, String str, String str2) {
        this.c = str2;
        this.d = i;
        this.f = str;
    }

    public static jc a(String str, String str2) {
        return new jc(a, str, str2);
    }

    public static jc b(String str, String str2) {
        return new jc(b, str, str2);
    }

    public final int a() {
        return this.d;
    }

    public final String b() {
        new JSONObject();
        return this.c;
    }

    private String d() {
        return this.f;
    }

    public final String c() {
        return a(this.d);
    }

    public static String a(int i) {
        return i == b ? "error" : DocumentsContract.EXTRA_INFO;
    }

    public static boolean a(jc jcVar) {
        return (jcVar == null || TextUtils.isEmpty(jcVar.b())) ? false : true;
    }

    private static String b(jc jcVar) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(DocumentsContract.EXTRA_INFO, jcVar.b());
            jSONObject.put("session", jcVar.d());
            jSONObject.put("timestamp", jcVar.e);
            return jSONObject.toString();
        } catch (Throwable unused) {
            return "";
        }
    }

    public static String a(List<jc> list) {
        if (list != null) {
            try {
                if (list.size() != 0) {
                    JSONArray jSONArray = new JSONArray();
                    Iterator<jc> it = list.iterator();
                    while (it.hasNext()) {
                        String strB = b(it.next());
                        if (!TextUtils.isEmpty(strB)) {
                            jSONArray.put(strB);
                        }
                    }
                    return jSONArray.toString();
                }
            } catch (Throwable unused) {
            }
        }
        return "";
    }
}
