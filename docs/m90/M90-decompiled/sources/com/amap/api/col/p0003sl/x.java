package com.amap.api.col.p0003sl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONObject;

/* JADX INFO: compiled from: FeatureManager.java */
/* JADX INFO: loaded from: classes2.dex */
public final class x {
    private Map<String, Boolean> a;
    private AtomicBoolean b;

    /* synthetic */ x(byte b) {
        this();
    }

    /* JADX INFO: compiled from: FeatureManager.java */
    private static class a {
        private static x a = new x(0);
    }

    private x() {
        this.a = new ConcurrentHashMap();
        this.b = new AtomicBoolean(false);
        c();
    }

    public static x a() {
        return a.a;
    }

    private void c() {
        this.a.put("feature_mvt", Boolean.TRUE);
        this.a.put("feature_gltf", Boolean.FALSE);
        this.a.put("feature_terrain", Boolean.FALSE);
    }

    public final void a(JSONObject jSONObject) {
        if (jSONObject == null) {
            return;
        }
        String strOptString = jSONObject.optString("mvt_able");
        ih.a(strOptString, true);
        this.a.put("feature_mvt", Boolean.valueOf(ih.a(strOptString, true)));
        this.a.put("feature_gltf", Boolean.valueOf(ih.a(jSONObject.optString("gltf_able"), false)));
        this.a.put("feature_terrain", Boolean.valueOf(ih.a(jSONObject.optString("terrain_able"), false)));
        this.b.set(true);
    }

    public final boolean a(String str) {
        if (this.a.containsKey(str)) {
            return this.a.get(str).booleanValue();
        }
        return false;
    }

    public final boolean b() {
        return this.b.get();
    }
}
