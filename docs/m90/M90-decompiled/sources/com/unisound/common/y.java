package com.unisound.common;

import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class y {
    private static final String a = "v";
    private static final String b = "ak";
    private static final String c = "udid";
    private static final String d = "curw";
    private static final String e = "neww";
    private static final String f = "reqID";
    private String g = "1.0";
    private String h;
    private String i;
    private Set<String> j;
    private Set<String> k;
    private String l;

    public String a() {
        return this.g;
    }

    public void a(String str) {
        this.g = str;
    }

    public void a(Set<String> set) {
        this.j = set;
    }

    public String b() {
        return this.h;
    }

    public void b(String str) {
        this.h = str;
    }

    public void b(Set<String> set) {
        this.k = set;
    }

    public String c() {
        return this.i;
    }

    public void c(String str) {
        this.i = str;
        this.l = t.a(str + String.valueOf(System.currentTimeMillis()));
    }

    public Set<String> d() {
        return this.j;
    }

    public Set<String> e() {
        return this.k;
    }

    public String f() {
        return "v:" + this.g + " ; " + b + ":" + this.h + " ; udid:" + this.i + " ; " + d + ":" + this.j.toString() + " ; " + e + ":" + this.k.toString() + " ; " + f + ":" + this.l;
    }

    public String g() {
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        Set<String> set = this.k;
        if (set != null) {
            for (Object obj : set.toArray()) {
                jSONArray.put(obj.toString());
            }
        }
        JSONArray jSONArray2 = new JSONArray();
        Set<String> set2 = this.j;
        if (set2 != null) {
            for (Object obj2 : set2.toArray()) {
                jSONArray2.put(obj2.toString());
            }
        }
        try {
            jSONObject.put(a, this.g);
            jSONObject.put(b, this.h);
            jSONObject.put("udid", this.i);
            jSONObject.put(f, this.l);
            jSONObject.put(d, jSONArray2);
            jSONObject.put(e, jSONArray);
        } catch (JSONException e2) {
            r.e("OneshotVO JSONException");
            e2.printStackTrace();
        }
        return jSONObject.toString();
    }
}
