package com.amap.api.col.p0003sl;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;
import com.amap.api.col.p0003sl.ef;
import com.amap.api.maps.model.CameraPosition;
import com.autonavi.base.amap.api.mapcore.infowindow.IInfoWindowAction;

/* JADX INFO: compiled from: MapOverlayViewGroupBase.java */
/* JADX INFO: loaded from: classes2.dex */
public interface ei extends IInfoWindowAction {
    float a(int i);

    Point a();

    void a(Canvas canvas);

    void a(ef.a aVar);

    void a(CameraPosition cameraPosition);

    void a(Boolean bool);

    void a(Float f);

    void a(Integer num);

    void a(Integer num, Float f);

    void a(String str, Boolean bool, Integer num);

    void a(boolean z);

    void b(Boolean bool);

    void b(Integer num);

    boolean b();

    void c();

    void c(Boolean bool);

    void c(Integer num);

    ed d();

    void d(Boolean bool);

    void d(Integer num);

    ef e();

    void e(Boolean bool);

    el f();

    void f(Boolean bool);

    void g();

    void g(Boolean bool);

    void h();

    void h(Boolean bool);

    void i();

    void i(Boolean bool);

    View j();
}
