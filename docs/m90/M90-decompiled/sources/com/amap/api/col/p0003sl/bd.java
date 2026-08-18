package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Bundle;
import com.amap.api.col.p0003sl.bs;
import com.amap.api.col.p0003sl.by;
import java.io.IOException;

/* JADX INFO: compiled from: OfflineMapDownloadTask.java */
/* JADX INFO: loaded from: classes2.dex */
public final class bd extends md implements bs.a {
    private bs a;
    private bu b;
    private bx c;
    private Context d;
    private Bundle e;
    private boolean g;

    private bd(bx bxVar, Context context) {
        this.e = new Bundle();
        this.g = false;
        this.c = bxVar;
        this.d = context;
    }

    public bd(bx bxVar, Context context, byte b) {
        this(bxVar, context);
    }

    @Override // com.amap.api.col.p0003sl.md
    public final void runTask() {
        if (this.c.u()) {
            this.c.a(by.a.file_io_exception);
            return;
        }
        try {
            e();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public final void a() {
        this.g = true;
        bs bsVar = this.a;
        if (bsVar != null) {
            bsVar.b();
        } else {
            cancelTask();
        }
        bu buVar = this.b;
        if (buVar != null) {
            buVar.a();
        }
    }

    private String d() {
        return dx.c(this.d);
    }

    private void e() throws IOException {
        bs bsVar = new bs(new bt(this.c.getUrl(), d(), this.c.v(), this.c.w()), this.c.getUrl(), this.d, this.c);
        this.a = bsVar;
        bsVar.a(this);
        bx bxVar = this.c;
        this.b = new bu(bxVar, bxVar);
        if (this.g) {
            return;
        }
        this.a.a();
    }

    public final void b() {
        Bundle bundle = this.e;
        if (bundle != null) {
            bundle.clear();
            this.e = null;
        }
    }

    @Override // com.amap.api.col.3sl.bs.a
    public final void c() {
        bu buVar = this.b;
        if (buVar != null) {
            buVar.b();
        }
    }
}
