package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.util.Pair;
import com.amap.api.col.p0003sl.ih;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.interfaces.IGlOverlayLayer;
import com.autonavi.base.amap.api.mapcore.IAMapDelegate;
import com.autonavi.base.amap.mapcore.tools.GLFileUtil;
import java.lang.ref.WeakReference;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: compiled from: AuthTask.java */
/* JADX INFO: loaded from: classes2.dex */
public final class t extends Thread {
    WeakReference<IAMapDelegate> a;
    private Context b;

    public t(Context context, IAMapDelegate iAMapDelegate) {
        this.a = null;
        this.b = context;
        this.a = new WeakReference<>(iAMapDelegate);
    }

    /* JADX WARN: Removed duplicated region for block: B:73:0x01a2  */
    @Override // java.lang.Thread, java.lang.Runnable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void run() {
        /*
            Method dump skipped, instruction units count: 766
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.t.run():void");
    }

    private static void a(JSONObject jSONObject) {
        if (jSONObject == null) {
            return;
        }
        try {
            dz.a(jSONObject.optJSONObject("17E"));
        } catch (Throwable unused) {
        }
    }

    private void b(JSONObject jSONObject) {
        IAMapDelegate iAMapDelegate;
        IAMapDelegate iAMapDelegate2;
        IAMapDelegate iAMapDelegate3;
        if (jSONObject == null) {
            return;
        }
        try {
            x.a().a(jSONObject.optJSONObject("1A1"));
            if (!x.a().a("feature_terrain") && (iAMapDelegate3 = this.a.get()) != null) {
                iAMapDelegate3.setTerrainAuth(false);
                dx.a(new AMapException(AMapException.FEATURE_TERRAIN_NOT_SUPPORT));
            }
            if (!x.a().a("feature_gltf") && (iAMapDelegate2 = this.a.get()) != null) {
                IGlOverlayLayer glOverlayLayer = iAMapDelegate2.getGlOverlayLayer();
                if (glOverlayLayer != null) {
                    glOverlayLayer.clearOverlayByType("GLTFOVERLAY");
                }
                dx.a(new AMapException(AMapException.FEATURE_GLTF_NOT_SUPPORT));
            }
            if (x.a().a("feature_mvt") || (iAMapDelegate = this.a.get()) == null) {
                return;
            }
            IGlOverlayLayer glOverlayLayer2 = iAMapDelegate.getGlOverlayLayer();
            if (glOverlayLayer2 != null) {
                glOverlayLayer2.clearOverlayByType("MVTTILEOVERLAY");
            }
            dx.a(new AMapException(AMapException.FEATURE_MVT_NOT_SUPPORT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void c(JSONObject jSONObject) {
        if (jSONObject == null) {
            return;
        }
        try {
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("16G");
            boolean zA = ih.a(jSONObjectOptJSONObject.optString("able", ""), false);
            boolean zA2 = ih.a(jSONObjectOptJSONObject.optString("removeCache", ""), false);
            boolean zA3 = ih.a(jSONObjectOptJSONObject.optString("uploadInfo", ""), false);
            Cdo.a(zA);
            Cdo.b(zA2);
            Cdo.c(zA3);
        } catch (Throwable unused) {
        }
    }

    private static void a(Context context, is isVar, JSONObject jSONObject) {
        if (jSONObject == null) {
            return;
        }
        try {
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("16V");
            boolean zA = ih.a(jSONObjectOptJSONObject.optString("di", ""), false);
            String strOptString = jSONObjectOptJSONObject.optString("dis", "");
            boolean zA2 = ih.a(jSONObjectOptJSONObject.optString("able", ""), false);
            boolean zA3 = ih.a(jSONObjectOptJSONObject.optString("isFilter", ""), true);
            if (!zA || it.e(strOptString)) {
                jy.a(isVar).a(context, zA2, zA3);
            }
        } catch (Throwable unused) {
        }
    }

    private void d(JSONObject jSONObject) {
        if (jSONObject == null) {
            return;
        }
        try {
            dn.a(this.b, "amap_param", "overlay_use_old_type", Boolean.valueOf(ih.a(jSONObject.optJSONObject("17W").optString("able", ""), false) ? false : true));
        } catch (Throwable unused) {
        }
    }

    private void a(ih.b.a aVar) {
        if (aVar != null) {
            try {
                dt.a(this.b, "maploc", "ue", Boolean.valueOf(aVar.a));
                JSONObject jSONObject = aVar.c;
                int iOptInt = jSONObject.optInt("fn", 1000);
                int iOptInt2 = jSONObject.optInt("mpn", 0);
                if (iOptInt2 > 500) {
                    iOptInt2 = 500;
                }
                if (iOptInt2 < 30) {
                    iOptInt2 = 30;
                }
                lj.a(iOptInt, ih.a(jSONObject.optString("igu"), false));
                dt.a(this.b, "maploc", "opn", Integer.valueOf(iOptInt2));
            } catch (Throwable th) {
                jw.c(th, "AuthUtil", "loadConfigDataUploadException");
            }
        }
    }

    @Override // java.lang.Thread
    public final void interrupt() {
        super.interrupt();
    }

    private Pair<JSONObject, ih.b.a> a(StringBuilder sb) {
        String string;
        JSONObject jSONObject;
        ih.b.a aVar;
        WeakReference<IAMapDelegate> weakReference;
        try {
            long jLongValue = dn.a(this.b, "cloud_config_pull", "cloud_config_pull_timestamp", (Long) 0L).longValue();
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (Math.abs(jCurrentTimeMillis - jLongValue) >= 86400000) {
                string = sb.toString();
                string.replaceAll(";;", ";");
                dn.a(this.b, "cloud_config_pull", "cloud_config_pull_timestamp", (Object) new Long(jCurrentTimeMillis));
            } else {
                string = "";
            }
            ih.b bVarA = ih.a(this.b, dx.a(), string, (Map<String, String>) null);
            if (ih.a != 1 && string != "" && bVarA != null && (weakReference = this.a) != null && weakReference.get() != null) {
                Message messageObtainMessage = this.a.get().getMainHandler().obtainMessage();
                messageObtainMessage.what = 2;
                if (bVarA.c != null) {
                    messageObtainMessage.obj = bVarA.c;
                }
                this.a.get().getMainHandler().sendMessage(messageObtainMessage);
            }
            String str = GLFileUtil.getCacheDir(this.b).getAbsolutePath() + "/authCustomConfigName";
            if (!TextUtils.isEmpty(string) && bVarA != null && bVarA.f != null) {
                jSONObject = bVarA.f;
                GLFileUtil.writeDatasToFile(str, bVarA.f.toString().getBytes());
            } else {
                jSONObject = new JSONObject(new String(GLFileUtil.readFileContents(str)));
            }
            String str2 = GLFileUtil.getCacheDir(this.b).getAbsolutePath() + "/authLogConfigName";
            if (!TextUtils.isEmpty(string) && bVarA != null && bVarA.g != null) {
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("IsExceptionUpdate", bVarA.g.a);
                jSONObject2.put("mOfflineLoc", bVarA.g.c);
                GLFileUtil.writeDatasToFile(str2, jSONObject2.toString().getBytes());
                aVar = bVarA.g;
            } else {
                byte[] fileContents = GLFileUtil.readFileContents(str2);
                ih.b.a aVar2 = new ih.b.a();
                JSONObject jSONObject3 = new JSONObject(new String(fileContents));
                aVar2.a = jSONObject3.getBoolean("IsExceptionUpdate");
                if (jSONObject3.has("mOfflineLoc")) {
                    aVar2.c = jSONObject3.getJSONObject("mOfflineLoc");
                }
                aVar = aVar2;
            }
            return new Pair<>(jSONObject, aVar);
        } catch (Throwable unused) {
            return null;
        }
    }
}
