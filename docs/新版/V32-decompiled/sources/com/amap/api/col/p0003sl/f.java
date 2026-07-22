package com.amap.api.col.p0003sl;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import com.autonavi.aps.amapapi.utils.b;
import com.autonavi.aps.amapapi.utils.j;

/* JADX INFO: compiled from: ApsServiceCore.java */
/* JADX INFO: loaded from: classes2.dex */
public final class f {
    e a;
    Context b;
    Messenger c = null;

    public f(Context context) {
        this.a = null;
        this.b = null;
        this.b = context.getApplicationContext();
        this.a = new e(this.b);
    }

    public final IBinder a(Intent intent) {
        this.a.b(intent);
        this.a.a(intent);
        Messenger messenger = new Messenger(this.a.b());
        this.c = messenger;
        return messenger.getBinder();
    }

    public final void a() {
        try {
            e.e();
            this.a.j = j.b();
            this.a.k = j.a();
            this.a.a();
        } catch (Throwable th) {
            b.a(th, "ApsServiceCore", "onCreate");
        }
    }

    public final int b() {
        e eVar = this.a;
        return (eVar == null || eVar.n.isSelfStartServiceEnable()) ? 3 : 2;
    }

    public final void c() {
        try {
            e eVar = this.a;
            if (eVar != null) {
                eVar.b().sendEmptyMessage(11);
            }
        } catch (Throwable th) {
            b.a(th, "ApsServiceCore", "onDestroy");
        }
    }
}
