package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.go;
import com.amap.api.col.p0003sl.ih;
import com.amap.api.services.core.AMapException;
import org.json.JSONObject;

/* JADX INFO: compiled from: ManifestConfig.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fy {
    public static is a;
    private static fy b;
    private static Context c;
    private a d;
    private HandlerThread e = new HandlerThread("manifestThread") { // from class: com.amap.api.col.3sl.fy.1
        @Override // android.os.HandlerThread, java.lang.Thread, java.lang.Runnable
        public final void run() {
            Thread.currentThread().setName("ManifestConfigThread");
            is isVarA = fo.a(false);
            fy.c(fy.c);
            ih.a(fy.c, isVarA, "11K;001;184;185", new ih.a() { // from class: com.amap.api.col.3sl.fy.1.1
                @Override // com.amap.api.col.3sl.ih.a
                public final void a(ih.b bVar) {
                    a aVar;
                    JSONObject jSONObjectOptJSONObject;
                    JSONObject jSONObjectOptJSONObject2;
                    Message message = new Message();
                    if (bVar != null) {
                        try {
                            if (bVar.g != null) {
                                message.obj = new fz(bVar.g.b, bVar.g.a);
                            }
                        } catch (Throwable th) {
                            try {
                                fp.a(th, "ManifestConfig", "run");
                                if (aVar == null) {
                                    return;
                                }
                            } finally {
                                message.what = 3;
                                if (fy.this.d != null) {
                                    fy.this.d.sendMessage(message);
                                }
                            }
                        }
                    }
                    if (bVar != null && bVar.f != null && (jSONObjectOptJSONObject2 = bVar.f.optJSONObject("184")) != null) {
                        fy.d(jSONObjectOptJSONObject2);
                        gx.a(fy.c, "amap_search", "cache_control", jSONObjectOptJSONObject2.toString());
                    }
                    if (bVar != null && bVar.f != null && (jSONObjectOptJSONObject = bVar.f.optJSONObject("185")) != null) {
                        fy.c(jSONObjectOptJSONObject);
                        gx.a(fy.c, "amap_search", "parm_control", jSONObjectOptJSONObject.toString());
                    }
                    message.what = 3;
                    if (fy.this.d == null) {
                    }
                }
            });
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public static void c(Context context) {
        try {
            String str = (String) gx.b(context, "amap_search", "cache_control", "");
            if (!TextUtils.isEmpty(str)) {
                d(new JSONObject(str));
            }
            String str2 = (String) gx.b(context, "amap_search", "parm_control", "");
            if (TextUtils.isEmpty(str2)) {
                return;
            }
            c(new JSONObject(str2));
        } catch (Throwable th) {
            fp.a(th, "ManifestConfig", "ManifestConfig-readAuthFromCache");
        }
    }

    private fy(Context context) {
        c = context;
        a = fo.a(false);
        try {
            b();
            this.d = new a(Looper.getMainLooper());
            this.e.start();
        } catch (Throwable th) {
            fp.a(th, "ManifestConfig", "ManifestConfig");
        }
    }

    private static void b() {
        gn.a();
    }

    public static fy a(Context context) {
        if (b == null) {
            b = new fy(context);
        }
        return b;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void c(JSONObject jSONObject) {
        if (jSONObject != null) {
            try {
                boolean zA = ih.a(jSONObject.optString("passAreaAble"), true);
                boolean zA2 = ih.a(jSONObject.optString("truckAble"), true);
                boolean zA3 = ih.a(jSONObject.optString("poiPageAble"), true);
                boolean zA4 = ih.a(jSONObject.optString("rideAble"), true);
                boolean zA5 = ih.a(jSONObject.optString("walkAble"), true);
                boolean zA6 = ih.a(jSONObject.optString("passPointAble"), true);
                boolean zA7 = ih.a(jSONObject.optString("keyWordLenAble"), true);
                int iOptInt = jSONObject.optInt("poiPageMaxSize", 25);
                int iOptInt2 = jSONObject.optInt("passAreaMaxCount", 100);
                int iOptInt3 = jSONObject.optInt("walkMaxLength", 100);
                int iOptInt4 = jSONObject.optInt("passPointMaxCount", 6);
                int iOptInt5 = jSONObject.optInt("poiPageMaxNum", 100);
                int iOptInt6 = jSONObject.optInt("truckMaxLength", 5000);
                int iOptInt7 = jSONObject.optInt("rideMaxLength", AMapException.CODE_AMAP_SERVICE_INVALID_PARAMS);
                int iOptInt8 = jSONObject.optInt("passAreaMaxArea", 100000000);
                int iOptInt9 = jSONObject.optInt("passAreaPointCount", 16);
                int iOptInt10 = jSONObject.optInt("keyWordLenMaxNum", 100);
                gr.a().a(zA);
                gr.a().c(iOptInt2);
                gr.a().i(iOptInt8);
                gr.a().j(iOptInt9);
                gr.a().b(zA2);
                gr.a().g(iOptInt6);
                gr.a().c(zA3);
                gr.a().f(iOptInt5);
                gr.a().a(iOptInt);
                gr.a().b(iOptInt10);
                gr.a().g(zA7);
                gr.a().d(zA4);
                gr.a().h(iOptInt7);
                gr.a().e(zA5);
                gr.a().d(iOptInt3);
                gr.a().f(zA6);
                gr.a().e(iOptInt4);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void d(JSONObject jSONObject) {
        if (jSONObject != null) {
            try {
                if (jSONObject.has("able")) {
                    go.a aVarA = a(jSONObject, true, (go.a) null);
                    go.a().a(aVarA);
                    if (aVarA.a()) {
                        a("regeo", jSONObject, aVarA);
                        a("geo", jSONObject, aVarA);
                        a("placeText", jSONObject, aVarA);
                        a("placeAround", jSONObject, aVarA);
                    }
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    private static void a(String str, JSONObject jSONObject, go.a aVar) {
        if (jSONObject != null && jSONObject.has(str)) {
            go.a().a(str, a(jSONObject.optJSONObject(str), false, aVar));
        }
    }

    private static go.a a(JSONObject jSONObject, boolean z, go.a aVar) {
        boolean zOptBoolean;
        go.a aVar2 = null;
        if (jSONObject == null) {
            return null;
        }
        try {
            go.a aVar3 = new go.a();
            try {
                if (z) {
                    zOptBoolean = ih.a(jSONObject.optString("able"), aVar == null || aVar.a());
                } else {
                    zOptBoolean = jSONObject.optBoolean("able", aVar == null || aVar.a());
                }
                int iOptInt = jSONObject.optInt("timeoffset", aVar != null ? (int) aVar.b() : 86400);
                int iOptInt2 = jSONObject.optInt("num", aVar != null ? aVar.c() : 10);
                double dOptDouble = jSONObject.optDouble("limitDistance", aVar != null ? aVar.d() : 0.0d);
                aVar3.a(zOptBoolean);
                aVar3.a(iOptInt);
                aVar3.a(iOptInt2);
                aVar3.a(dOptDouble);
                return aVar3;
            } catch (Throwable th) {
                th = th;
                aVar2 = aVar3;
                th.printStackTrace();
                return aVar2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    /* JADX INFO: compiled from: ManifestConfig.java */
    class a extends Handler {
        String a;

        public a(Looper looper) {
            super(looper);
            this.a = "handleMessage";
        }

        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            if (message != null && message.what == 3) {
                try {
                    fz fzVar = (fz) message.obj;
                    if (fzVar == null) {
                        fzVar = new fz(false, false);
                    }
                    jw.a(fy.c, fo.a(fzVar.a()));
                    fy.a = fo.a(fzVar.a());
                } catch (Throwable th) {
                    fp.a(th, "ManifestConfig", this.a);
                }
            }
        }
    }
}
