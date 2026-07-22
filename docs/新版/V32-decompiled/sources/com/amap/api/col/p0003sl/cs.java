package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.col.p0003sl.cr;
import com.amap.api.maps.MapsInitializer;
import com.autonavi.base.amap.api.mapcore.IAMapDelegate;
import com.autonavi.base.amap.mapcore.FileUtil;
import java.io.File;
import java.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: CustomStyleTask.java */
/* JADX INFO: loaded from: classes2.dex */
public final class cs extends md {
    private Context a;
    private IAMapDelegate b;
    private cr c;
    private String d;
    private String e;
    private String g;
    private a h;
    private int i;

    /* JADX INFO: compiled from: CustomStyleTask.java */
    public interface a {
        void a(byte[] bArr, int i);

        void b(byte[] bArr, int i);
    }

    public cs(Context context, a aVar, int i, String str) {
        this.d = null;
        this.e = null;
        this.g = null;
        this.i = 0;
        this.a = context;
        this.h = aVar;
        this.i = i;
        if (this.c == null) {
            this.c = new cr(this.a, "", i != 0);
        }
        this.c.b(str);
        this.d = i + (str == null ? "" : str) + ".amapstyle";
        this.e = context.getCacheDir().getPath();
    }

    public cs(Context context, IAMapDelegate iAMapDelegate) {
        this.d = null;
        this.e = null;
        this.g = null;
        this.i = 0;
        this.a = context;
        this.b = iAMapDelegate;
        if (this.c == null) {
            this.c = new cr(this.a, "");
        }
    }

    public final void a(String str) {
        cr crVar = this.c;
        if (crVar != null) {
            crVar.c(str);
        }
        this.g = str;
    }

    private byte[] b(String str) {
        if (str == null || this.e == null) {
            return null;
        }
        return FileUtil.readFileContents(this.e + File.separator + str);
    }

    private void a(String str, byte[] bArr) {
        if (str == null || bArr == null || this.e == null) {
            return;
        }
        FileUtil.saveFileContents(this.e + File.separator + str, bArr);
    }

    private String c(String str) {
        if (str == null) {
            return null;
        }
        Object objB = dt.b(this.a, "amap_style_config", "lastModified".concat(String.valueOf(str)), "");
        if (!(objB instanceof String) || objB == "") {
            return null;
        }
        return (String) objB;
    }

    private void a(String str, String str2) {
        if (str == null || str2 == null) {
            return;
        }
        dt.a(this.a, "amap_style_config", "lastModified".concat(String.valueOf(str)), str2);
    }

    @Override // com.amap.api.col.p0003sl.md
    public final void runTask() {
        try {
            if (MapsInitializer.getNetWorkEnable()) {
                if (this.c != null) {
                    String str = this.g + this.d;
                    String strC = c(str);
                    if (strC != null) {
                        this.c.d(strC);
                    }
                    byte[] bArrB = b(str);
                    a aVar = this.h;
                    if (aVar != null && bArrB != null) {
                        aVar.a(bArrB, this.i);
                    }
                    cr.a aVarD = this.c.d();
                    if (aVarD != null && aVarD.a != null) {
                        JSONObject jSONObject = null;
                        try {
                            jSONObject = new JSONObject(new String(aVarD.a));
                        } catch (JSONException unused) {
                        }
                        if (jSONObject == null) {
                            if (this.h != null) {
                                if (!Arrays.equals(aVarD.a, bArrB)) {
                                    this.h.b(aVarD.a, this.i);
                                }
                            } else {
                                IAMapDelegate iAMapDelegate = this.b;
                                if (iAMapDelegate != null) {
                                    iAMapDelegate.setCustomMapStyle(iAMapDelegate.getMapConfig().isCustomStyleEnable(), aVarD.a);
                                }
                            }
                            a(str, aVarD.a);
                            a(str, aVarD.c);
                        }
                    }
                }
                jw.a(this.a, dx.a());
                IAMapDelegate iAMapDelegate2 = this.b;
                if (iAMapDelegate2 != null) {
                    iAMapDelegate2.setRunLowFrame(false);
                }
            }
        } catch (Throwable th) {
            jw.c(th, "CustomStyleTask", "download customStyle");
            th.printStackTrace();
        }
    }

    public final void a() {
        this.a = null;
        if (this.c != null) {
            this.c = null;
        }
    }

    public final void b() {
        dv.a().a(this);
    }
}
