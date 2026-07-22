package com.amap.api.offlineservice;

import android.view.View;
import android.widget.RelativeLayout;
import com.amap.api.maps.offlinemap.OfflineMapActivity;

/* JADX INFO: compiled from: ServiceModule.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class a {
    protected OfflineMapActivity a = null;

    public abstract void a(View view);

    public abstract void b();

    public boolean c() {
        return true;
    }

    public abstract RelativeLayout d();

    public abstract void e();

    public final void a(OfflineMapActivity offlineMapActivity) {
        this.a = offlineMapActivity;
    }

    public final void a() {
        this.a.showScr();
    }

    public final int a(float f) {
        return this.a != null ? (int) ((f * (r0.getResources().getDisplayMetrics().densityDpi / 160.0f)) + 0.5f) : (int) f;
    }
}
