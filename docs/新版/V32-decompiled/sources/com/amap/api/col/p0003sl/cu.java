package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.col.p0003sl.ct;
import com.amap.api.maps.MapsInitializer;
import com.autonavi.base.amap.mapcore.FileUtil;

/* JADX INFO: compiled from: CustomStyleTextureTask.java */
/* JADX INFO: loaded from: classes2.dex */
public final class cu extends md {
    private Context a;
    private ct b;
    private da c;
    private a d;

    /* JADX INFO: compiled from: CustomStyleTextureTask.java */
    public interface a {
        void a(String str, da daVar);
    }

    public cu(Context context) {
        this.a = context;
        if (this.b == null) {
            this.b = new ct(this.a, "");
        }
    }

    public final void a(String str) {
        ct ctVar = this.b;
        if (ctVar != null) {
            ctVar.b(str);
        }
    }

    @Override // com.amap.api.col.p0003sl.md
    public final void runTask() {
        try {
            if (MapsInitializer.getNetWorkEnable()) {
                ct ctVar = this.b;
                if (ctVar != null) {
                    ct.a aVarD = ctVar.d();
                    String str = null;
                    if (aVarD != null && aVarD.a != null) {
                        str = a(this.a) + "/custom_texture_data";
                        a(str, aVarD.a);
                    }
                    a aVar = this.d;
                    if (aVar != null) {
                        aVar.a(str, this.c);
                    }
                }
                jw.a(this.a, dx.a());
            }
        } catch (Throwable th) {
            jw.c(th, "CustomStyleTask", "download customStyle");
            th.printStackTrace();
        }
    }

    private static void a(String str, byte[] bArr) throws Throwable {
        FileUtil.writeDatasToFile(str, bArr);
    }

    private static String a(Context context) {
        return FileUtil.getMapBaseStorage(context);
    }

    public final void a() {
        this.a = null;
        if (this.b != null) {
            this.b = null;
        }
    }

    public final void b() {
        dv.a().a(this);
    }

    public final void a(a aVar) {
        this.d = aVar;
    }

    public final void a(da daVar) {
        this.c = daVar;
    }
}
