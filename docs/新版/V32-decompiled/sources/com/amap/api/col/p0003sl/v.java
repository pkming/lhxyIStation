package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import com.autonavi.base.amap.api.mapcore.IAMapDelegate;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: AuthTerrainTask.java */
/* JADX INFO: loaded from: classes2.dex */
public final class v {
    private Context a;
    private WeakReference<IAMapDelegate> b;
    private HandlerThread c;
    private Handler d;
    private a e;
    private final Runnable f = new Runnable() { // from class: com.amap.api.col.3sl.v.1
        @Override // java.lang.Runnable
        public final void run() {
            if (v.this.e == null) {
                v.this.e = new a(v.this.a, v.this);
            }
            dv.a().a(v.this.e);
        }
    };
    private final Runnable g = new Runnable() { // from class: com.amap.api.col.3sl.v.2
        @Override // java.lang.Runnable
        public final void run() {
            IAMapDelegate iAMapDelegate = (IAMapDelegate) v.this.b.get();
            if (iAMapDelegate != null) {
                iAMapDelegate.setTerrainAuth(false);
            }
            dd.a(v.this.a, "地形图鉴权失败，当前key没有地形图的使用权限，地形图，将不会呈现！");
        }
    };

    public v(Context context, IAMapDelegate iAMapDelegate) {
        this.a = context.getApplicationContext();
        this.b = new WeakReference<>(iAMapDelegate);
        b();
    }

    private void b() {
        if (this.c == null) {
            HandlerThread handlerThread = new HandlerThread("terrain_auth");
            this.c = handlerThread;
            handlerThread.start();
            this.d = new Handler(this.c.getLooper());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c() {
        Handler handler = this.d;
        if (handler != null) {
            handler.postDelayed(this.g, 1000L);
        }
    }

    public final void a(long j) {
        Handler handler = this.d;
        if (handler != null) {
            handler.postDelayed(this.f, j);
        }
    }

    public final void a() {
        Handler handler = this.d;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            this.d = null;
        }
        HandlerThread handlerThread = this.c;
        if (handlerThread != null) {
            handlerThread.quitSafely();
            this.c = null;
        }
    }

    /* JADX INFO: compiled from: AuthTerrainTask.java */
    private static class a extends md {
        private Context a;
        private v b;
        private b c;

        public a(Context context, v vVar) {
            this.a = context;
            this.b = vVar;
            this.c = new b(this.a, "");
        }

        @Override // com.amap.api.col.p0003sl.md
        public final void runTask() {
            try {
                c cVarD = this.c.d();
                if (cVarD == null) {
                    this.b.a(30000L);
                } else {
                    if (cVarD.d) {
                        return;
                    }
                    this.b.c();
                }
            } catch (hx e) {
                e.printStackTrace();
                this.b.a(30000L);
            }
        }
    }

    /* JADX INFO: compiled from: AuthTerrainTask.java */
    private static class c {
        public String a;
        public String b;
        public String c;
        public boolean d;

        private c() {
            this.d = false;
        }

        /* synthetic */ c(byte b) {
            this();
        }
    }

    /* JADX INFO: compiled from: AuthTerrainTask.java */
    private static class b extends hy<String, c> {
        private boolean f;

        @Override // com.amap.api.col.p0003sl.hy
        protected final String c() {
            return null;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public final boolean isSupportIPV6() {
            return true;
        }

        @Override // com.amap.api.col.p0003sl.hy
        protected final /* synthetic */ c a(String str) throws hx {
            return b(str);
        }

        @Override // com.amap.api.col.p0003sl.hy
        protected final /* synthetic */ c a(byte[] bArr) throws hx {
            return b(bArr);
        }

        public b(Context context, String str) {
            super(context, str);
            this.f = true;
            this.d = "/rest/feedback/terrain";
            this.isPostFlag = false;
            this.f = true;
        }

        private static c b(byte[] bArr) throws hx {
            String str;
            try {
                str = new String(bArr, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
                str = null;
            }
            if (str == null || "".equals(str)) {
                return null;
            }
            return b(str);
        }

        private static c b(String str) throws hx {
            try {
                JSONObject jSONObject = new JSONObject(str);
                String strOptString = jSONObject.optString(DocumentsContract.EXTRA_INFO);
                String strOptString2 = jSONObject.optString("infocode");
                String strOptString3 = jSONObject.optString("status");
                boolean z = false;
                z = false;
                c cVar = new c(z ? (byte) 1 : (byte) 0);
                cVar.a = strOptString;
                cVar.b = strOptString2;
                cVar.c = strOptString3;
                if (!TextUtils.isEmpty(strOptString2) && TextUtils.equals(strOptString2, "10000")) {
                    z = true;
                }
                cVar.d = z;
                return cVar;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override // com.amap.api.col.p0003sl.db, com.amap.api.col.p0003sl.lb
        public final Map<String, String> getParams() {
            Hashtable hashtable = new Hashtable(16);
            hashtable.put("key", ig.f(this.c));
            if (this.f) {
                hashtable.put("pname", "3dmap");
            }
            String strA = ij.a();
            String strA2 = ij.a(this.c, strA, it.b(hashtable));
            hashtable.put(SPUserInfoUtils.TS, strA);
            hashtable.put("scode", strA2);
            return hashtable;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public final String getURL() {
            return "http://restsdk.amap.com" + this.d;
        }

        @Override // com.amap.api.col.p0003sl.lb
        public final String getIPV6URL() {
            return dx.a(getURL());
        }
    }
}
