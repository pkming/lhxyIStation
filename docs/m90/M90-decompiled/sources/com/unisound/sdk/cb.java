package com.unisound.sdk;

import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class cb {
    private String a;
    private String b = "";
    private String c = "";
    private String d = "";

    public cb(String str) {
        this.a = "";
        this.a = str;
        b(str);
    }

    private boolean a(String str) {
        return str == null || str.trim().length() == 0;
    }

    private boolean b(String str) {
        if (!a(str)) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                this.d = jSONObject.has("history") ? jSONObject.getString("history") : "";
                this.c = jSONObject.has("text") ? jSONObject.getString("text") : "";
                if (jSONObject.has(g.i)) {
                    JSONObject jSONObject2 = jSONObject.getJSONObject(g.i);
                    this.b = jSONObject2.has("text") ? jSONObject2.getString("text") : "";
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String a() {
        return this.a;
    }

    public String b() {
        return this.b;
    }

    public String c() {
        return this.d;
    }

    public String d() {
        return this.c;
    }
}
