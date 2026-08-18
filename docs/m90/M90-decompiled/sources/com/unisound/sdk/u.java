package com.unisound.sdk;

import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class u extends an {
    private List<Integer> aY;
    private int aV = 3;
    private int aW = 3;
    private int aX = 3;
    private String aZ = null;
    private String ba = "";

    public String O() {
        return this.aZ;
    }

    public String P() {
        return this.ba;
    }

    public int Q() {
        return this.aV;
    }

    public List<Integer> R() {
        return this.aY;
    }

    public int S() {
        return this.aW;
    }

    public int T() {
        return this.aX;
    }

    public void a(String str, boolean z) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("grammaTag", str);
            jSONObject.put("loadGrammaSuccess", z);
            this.ba = jSONObject.toString().replaceAll("/", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void a(List<Integer> list) {
        this.aY = list;
    }

    public void e(String str) {
        this.aZ = str;
    }

    public boolean n(int i) {
        if (this.aV == i) {
            return true;
        }
        List<Integer> list = this.aY;
        if (list == null) {
            return false;
        }
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            if (i == it.next().intValue()) {
                this.aV = i;
                return true;
            }
        }
        return false;
    }

    public boolean o(int i) {
        if (this.aW == i) {
            return true;
        }
        List<Integer> list = this.aY;
        if (list == null) {
            return false;
        }
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            if (i == it.next().intValue()) {
                this.aW = i;
                return true;
            }
        }
        return false;
    }

    public void p(int i) {
        this.aX = i;
    }
}
