package com.unisound.common;

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class VoiceprintResult {
    private String a;
    private int b = 0;
    private String c = "";
    private float d = 0.0f;

    public VoiceprintResult(String str) {
        this.a = str;
        a(str);
    }

    private boolean a(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        r.c(str);
        try {
            JSONObject jSONObject = new JSONObject(b(str));
            this.b = Integer.valueOf(jSONObject.has("status") ? jSONObject.getInt("status") : 0).intValue();
            this.c = jSONObject.has("username") ? jSONObject.getString("username") : "";
            this.d = Float.valueOf(jSONObject.has("score") ? jSONObject.getString("score") : "0").floatValue();
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String b(String str) {
        if (!str.contains("}{")) {
            return str;
        }
        String strSubstring = str.substring(str.indexOf("}") + 1);
        r.e("jsonStr: " + strSubstring);
        return strSubstring;
    }

    public float getScore() {
        return this.d;
    }

    public int getStatus() {
        return this.b;
    }

    public String getString() {
        return this.a;
    }

    public String getUserName() {
        return this.c;
    }
}
