package com.unisound.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class o {
    public static final String a = "partial";
    public static final String b = "full";
    public static final String c = "change";

    /* JADX WARN: Can't wrap try/catch for region: R(12:3|(1:5)(1:(1:10)(2:11|(1:13)(10:14|(1:16)|(1:18)|(1:20)|(1:22)|(1:24)|(1:26)|(1:28)|31|32)))|35|6|(0)|(0)|(0)|(0)|(0)|(0)|31|32) */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0051, code lost:
    
        r3.printStackTrace();
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0012, code lost:
    
        r3 = move-exception;
     */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0028 A[Catch: JSONException -> 0x0012, TryCatch #0 {JSONException -> 0x0012, blocks: (B:6:0x000e, B:18:0x0028, B:20:0x002f, B:22:0x0036, B:24:0x003d, B:26:0x0044, B:28:0x004b), top: B:35:0x000e }] */
    /* JADX WARN: Removed duplicated region for block: B:20:0x002f A[Catch: JSONException -> 0x0012, TryCatch #0 {JSONException -> 0x0012, blocks: (B:6:0x000e, B:18:0x0028, B:20:0x002f, B:22:0x0036, B:24:0x003d, B:26:0x0044, B:28:0x004b), top: B:35:0x000e }] */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0036 A[Catch: JSONException -> 0x0012, TryCatch #0 {JSONException -> 0x0012, blocks: (B:6:0x000e, B:18:0x0028, B:20:0x002f, B:22:0x0036, B:24:0x003d, B:26:0x0044, B:28:0x004b), top: B:35:0x000e }] */
    /* JADX WARN: Removed duplicated region for block: B:24:0x003d A[Catch: JSONException -> 0x0012, TryCatch #0 {JSONException -> 0x0012, blocks: (B:6:0x000e, B:18:0x0028, B:20:0x002f, B:22:0x0036, B:24:0x003d, B:26:0x0044, B:28:0x004b), top: B:35:0x000e }] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0044 A[Catch: JSONException -> 0x0012, TryCatch #0 {JSONException -> 0x0012, blocks: (B:6:0x000e, B:18:0x0028, B:20:0x002f, B:22:0x0036, B:24:0x003d, B:26:0x0044, B:28:0x004b), top: B:35:0x000e }] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x004b A[Catch: JSONException -> 0x0012, TRY_LEAVE, TryCatch #0 {JSONException -> 0x0012, blocks: (B:6:0x000e, B:18:0x0028, B:20:0x002f, B:22:0x0036, B:24:0x003d, B:26:0x0044, B:28:0x004b), top: B:35:0x000e }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String a(int r3, java.lang.String r4, java.lang.String r5, java.lang.String r6, java.lang.Object r7, java.lang.Object r8, java.lang.Object r9) {
        /*
            if (r5 == 0) goto L61
            org.json.JSONObject r0 = new org.json.JSONObject
            r0.<init>()
            r1 = 1
            java.lang.String r2 = "engine_mode"
            if (r3 != r1) goto L14
            java.lang.String r3 = "net"
        Le:
            r0.put(r2, r3)     // Catch: org.json.JSONException -> L12
            goto L26
        L12:
            r3 = move-exception
            goto L51
        L14:
            if (r3 != 0) goto L19
            java.lang.String r3 = "mix"
            goto Le
        L19:
            r1 = 2
            if (r3 != r1) goto L1f
            java.lang.String r3 = "local"
            goto Le
        L1f:
            r1 = 1000(0x3e8, float:1.401E-42)
            if (r3 != r1) goto L26
            java.lang.String r3 = "wakeup"
            goto Le
        L26:
            if (r4 == 0) goto L2d
            java.lang.String r3 = "result_type"
            r0.put(r3, r4)     // Catch: org.json.JSONException -> L12
        L2d:
            if (r7 == 0) goto L34
            java.lang.String r3 = "last_result"
            r0.put(r3, r7)     // Catch: org.json.JSONException -> L12
        L34:
            if (r5 == 0) goto L3b
            java.lang.String r3 = "recognition_result"
            r0.put(r3, r5)     // Catch: org.json.JSONException -> L12
        L3b:
            if (r6 == 0) goto L42
            java.lang.String r3 = "sessionID"
            r0.put(r3, r6)     // Catch: org.json.JSONException -> L12
        L42:
            if (r8 == 0) goto L49
            java.lang.String r3 = "score"
            r0.put(r3, r8)     // Catch: org.json.JSONException -> L12
        L49:
            if (r9 == 0) goto L54
            java.lang.String r3 = "utteranceTime"
            r0.put(r3, r9)     // Catch: org.json.JSONException -> L12
            goto L54
        L51:
            r3.printStackTrace()
        L54:
            java.lang.String r3 = r0.toString()
            java.lang.String r4 = "\\/"
            java.lang.String r5 = "/"
            java.lang.String r3 = r3.replace(r4, r5)
            return r3
        L61:
            java.lang.String r3 = ""
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.unisound.common.o.a(int, java.lang.String, java.lang.String, java.lang.String, java.lang.Object, java.lang.Object, java.lang.Object):java.lang.String");
    }

    public static String a(ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<String> arrayList3) {
        JSONObject jSONObject = new JSONObject();
        if (arrayList != null) {
            new JSONObject();
            JSONArray jSONArray = new JSONArray();
            for (int i = 0; i < arrayList.size(); i++) {
                try {
                    jSONArray.put(new JSONObject(arrayList.get(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            jSONObject.put("local_asr", jSONArray);
        }
        if (arrayList2 != null) {
            new JSONObject();
            JSONArray jSONArray2 = new JSONArray();
            for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                try {
                    jSONArray2.put(new JSONObject(arrayList2.get(i2)));
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
            }
            jSONObject.put("net_asr", jSONArray2);
        }
        if (arrayList3 != null && arrayList3.size() > 0) {
            new JSONObject();
            JSONArray jSONArray3 = new JSONArray();
            for (int i3 = 0; i3 < arrayList3.size(); i3++) {
                try {
                    if (arrayList3.get(i3) != null) {
                        jSONArray3.put(new JSONObject(arrayList3.get(i3)));
                    }
                } catch (JSONException e3) {
                    e3.printStackTrace();
                }
            }
            jSONObject.put("net_nlu", jSONArray3);
        }
        return jSONObject.toString().replace("\\/", "/");
    }

    public static Map<Integer, Object> a(String str, Map<String, Integer> map) {
        HashMap map2 = new HashMap();
        try {
            JSONObject jSONObject = new JSONObject(str);
            for (String str2 : map.keySet()) {
                map2.put(map.get(str2), jSONObject.opt(str2));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map2;
    }
}
