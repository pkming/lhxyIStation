package com.amap.api.col.p0003sl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.amap.api.col.p0003sl.ef;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.interfaces.IGlOverlayLayer;
import com.amap.api.maps.model.BasePointOverlay;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Marker;
import com.autonavi.amap.mapcore.IPoint;
import com.autonavi.base.ae.gmap.GLMapState;
import com.autonavi.base.ae.gmap.listener.AMapWidgetListener;
import com.autonavi.base.amap.api.mapcore.BaseOverlayImp;
import com.autonavi.base.amap.api.mapcore.IAMapDelegate;
import com.autonavi.base.amap.api.mapcore.IGLSurfaceView;
import com.autonavi.base.amap.mapcore.FPoint;
import com.autonavi.base.amap.mapcore.MapConfig;

/* JADX INFO: compiled from: MapOverlayViewGroup.java */
/* JADX INFO: loaded from: classes2.dex */
public final class eh extends ViewGroup implements ei {
    ej a;
    av b;
    private IAMapDelegate c;
    private IGlOverlayLayer d;
    private Context e;
    private el f;
    private eg g;
    private ee h;
    private ek i;
    private ed j;
    private ef k;
    private em l;
    private View m;
    private BasePointOverlay n;
    private Drawable o;
    private boolean p;
    private View q;
    private boolean r;
    private boolean s;
    private boolean t;

    @Override // com.autonavi.base.amap.api.mapcore.infowindow.IInfoWindowAction
    public final boolean isInfoWindowShown() {
        return false;
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final View j() {
        return this;
    }

    static /* synthetic */ View f(eh ehVar) {
        ehVar.m = null;
        return null;
    }

    public eh(Context context, IAMapDelegate iAMapDelegate, IGlOverlayLayer iGlOverlayLayer) {
        super(context);
        this.o = null;
        int i = 1;
        this.p = true;
        this.s = true;
        this.t = true;
        try {
            this.d = iGlOverlayLayer;
            this.c = iAMapDelegate;
            this.e = context;
            this.a = new ej();
            this.j = new ed(context);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
            if (this.c.getGLMapView() != null) {
                addView(this.c.getGLMapView(), 0, layoutParams);
            } else {
                i = 0;
            }
            addView(this.j, i, layoutParams);
            if (this.s) {
                return;
            }
            a(context);
        } catch (Throwable th) {
            th.printStackTrace();
            dx.a(th);
        }
    }

    private void a(Context context) {
        el elVar = new el(context);
        this.f = elVar;
        elVar.c(this.t);
        this.i = new ek(context, this.c);
        this.k = new ef(context);
        this.l = new em(context, this.c);
        this.g = new eg(context, this.c);
        this.h = new ee(context, this.c);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
        addView(this.f, layoutParams);
        addView(this.i, layoutParams);
        addView(this.k, new ViewGroup.LayoutParams(-2, -2));
        addView(this.l, new a(new FPoint(0.0f, 0.0f), 83));
        addView(this.g, new a(FPoint.obtain(0.0f, 0.0f), 83));
        addView(this.h, new a(FPoint.obtain(0.0f, 0.0f), 51));
        this.h.setVisibility(8);
        this.c.setMapWidgetListener(new AMapWidgetListener() { // from class: com.amap.api.col.3sl.eh.1
            @Override // com.autonavi.base.ae.gmap.listener.AMapWidgetListener
            public final void setFrontViewVisibility(boolean z) {
            }

            @Override // com.autonavi.base.ae.gmap.listener.AMapWidgetListener
            public final void invalidateScaleView() {
                if (eh.this.i == null) {
                    return;
                }
                eh.this.i.post(new Runnable() { // from class: com.amap.api.col.3sl.eh.1.1
                    @Override // java.lang.Runnable
                    public final void run() {
                        eh.this.i.b();
                    }
                });
            }

            @Override // com.autonavi.base.ae.gmap.listener.AMapWidgetListener
            public final void invalidateCompassView() {
                if (eh.this.h == null) {
                    return;
                }
                eh.this.h.post(new Runnable() { // from class: com.amap.api.col.3sl.eh.1.2
                    @Override // java.lang.Runnable
                    public final void run() {
                        eh.this.h.b();
                    }
                });
            }

            @Override // com.autonavi.base.ae.gmap.listener.AMapWidgetListener
            public final void invalidateZoomController(final float f) {
                if (eh.this.l == null) {
                    return;
                }
                eh.this.l.post(new Runnable() { // from class: com.amap.api.col.3sl.eh.1.3
                    @Override // java.lang.Runnable
                    public final void run() {
                        eh.this.l.a(f);
                    }
                });
            }
        });
        try {
            if (this.c.getUiSettings().isMyLocationButtonEnabled()) {
                return;
            }
            this.g.setVisibility(8);
        } catch (Throwable th) {
            jw.c(th, "AMapDelegateImpGLSurfaceView", "locationView gone");
            th.printStackTrace();
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void a(Boolean bool) {
        ef efVar = this.k;
        if (efVar == null) {
            this.a.a(this, bool);
        } else if (efVar != null && bool.booleanValue() && this.c.canShowIndoorSwitch()) {
            this.k.a(true);
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void b(Boolean bool) {
        em emVar = this.l;
        if (emVar == null) {
            this.a.a(this, bool);
        } else {
            emVar.a(bool.booleanValue());
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void c(Boolean bool) {
        if (this.g == null) {
            this.a.a(this, bool);
        } else if (bool.booleanValue()) {
            this.g.setVisibility(0);
        } else {
            this.g.setVisibility(8);
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void d(Boolean bool) {
        ee eeVar = this.h;
        if (eeVar == null) {
            this.a.a(this, bool);
        } else {
            eeVar.a(bool.booleanValue());
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void e(Boolean bool) {
        ek ekVar = this.i;
        if (ekVar == null) {
            this.a.a(this, bool);
        } else {
            ekVar.a(bool.booleanValue());
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void f(Boolean bool) {
        el elVar = this.f;
        if (elVar == null) {
            this.a.a(this, bool);
        } else {
            elVar.setVisibility(bool.booleanValue() ? 0 : 8);
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void a(String str, Boolean bool, Integer num) {
        if (this.f == null) {
            this.a.a(this, str, bool, num);
            return;
        }
        if (num.intValue() == 2) {
            this.f.b(bool.booleanValue());
        } else {
            if (TextUtils.isEmpty(str)) {
                return;
            }
            this.f.a(str, num.intValue());
            this.f.d(bool.booleanValue());
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void a(Float f) {
        em emVar = this.l;
        if (emVar == null) {
            this.a.a(this, f);
        } else if (emVar != null) {
            emVar.a(f.floatValue());
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void a(Integer num) {
        em emVar = this.l;
        if (emVar == null) {
            this.a.a(this, num);
        } else if (emVar != null) {
            emVar.a(num.intValue());
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void b(Integer num) {
        el elVar = this.f;
        if (elVar == null) {
            this.a.a(this, num);
        } else if (elVar != null) {
            elVar.a(num.intValue());
            this.f.postInvalidate();
            k();
        }
    }

    private void k() {
        ek ekVar = this.i;
        if (ekVar == null) {
            this.a.a(this, new Object[0]);
        } else {
            if (ekVar == null || ekVar.getVisibility() != 0) {
                return;
            }
            this.i.postInvalidate();
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void c(Integer num) {
        el elVar = this.f;
        if (elVar == null) {
            this.a.a(this, num);
        } else if (elVar != null) {
            elVar.b(num.intValue());
            k();
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void d(Integer num) {
        el elVar = this.f;
        if (elVar == null) {
            this.a.a(this, num);
        } else if (elVar != null) {
            elVar.c(num.intValue());
            k();
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final float a(int i) {
        if (this.f == null) {
            return 0.0f;
        }
        k();
        return this.f.d(i);
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void a(Integer num, Float f) {
        el elVar = this.f;
        if (elVar == null) {
            this.a.a(this, num, f);
        } else if (elVar != null) {
            elVar.a(num.intValue(), f.floatValue());
            k();
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.infowindow.IInfoWindowAction
    public final void setInfoWindowAdapterManager(av avVar) {
        this.b = avVar;
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final Point a() {
        el elVar = this.f;
        if (elVar == null) {
            return null;
        }
        return elVar.b();
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void g(Boolean bool) {
        el elVar = this.f;
        if (elVar == null) {
            this.a.a(this, bool);
            return;
        }
        if (elVar != null && bool.booleanValue()) {
            this.f.a(true);
            return;
        }
        el elVar2 = this.f;
        if (elVar2 != null) {
            elVar2.a(false);
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final boolean b() {
        el elVar = this.f;
        if (elVar != null) {
            return elVar.d();
        }
        return false;
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void c() {
        el elVar = this.f;
        if (elVar == null) {
            this.a.a(this, new Object[0]);
        } else if (elVar != null) {
            elVar.c();
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final ed d() {
        return this.j;
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final ef e() {
        return this.k;
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final el f() {
        return this.f;
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void a(CameraPosition cameraPosition) {
        if (this.f == null) {
            this.a.a(this, cameraPosition);
            return;
        }
        if (this.c.getUiSettings().isLogoEnable()) {
            if (MapsInitializer.isLoadWorldGridMap() && cameraPosition.zoom >= 6.0f && !dq.a(cameraPosition.target.latitude, cameraPosition.target.longitude)) {
                this.f.setVisibility(8);
            } else if (this.c.getMaskLayerType() == -1) {
                this.f.setVisibility(0);
            }
        }
    }

    private void l() {
        em emVar = this.l;
        if (emVar != null) {
            emVar.a();
        }
        ek ekVar = this.i;
        if (ekVar != null) {
            ekVar.a();
        }
        el elVar = this.f;
        if (elVar != null) {
            elVar.a();
        }
        eg egVar = this.g;
        if (egVar != null) {
            egVar.a();
        }
        ee eeVar = this.h;
        if (eeVar != null) {
            eeVar.a();
        }
        ef efVar = this.k;
        if (efVar != null) {
            efVar.a();
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void a(boolean z) {
        el elVar = this.f;
        if (elVar != null) {
            elVar.c(z);
        }
        this.t = z;
    }

    /* JADX INFO: compiled from: MapOverlayViewGroup.java */
    public static class a extends ViewGroup.LayoutParams {
        public FPoint a;
        public boolean b;
        public int c;
        public int d;
        public int e;

        public a(FPoint fPoint, int i) {
            this(-2, -2, fPoint.x, fPoint.y, 0, 0, i);
        }

        public a(int i, int i2, float f, float f2, int i3, int i4, int i5) {
            super(i, i2);
            FPoint fPoint = new FPoint();
            this.a = fPoint;
            this.b = false;
            this.c = 0;
            this.d = 0;
            this.e = 51;
            fPoint.x = f;
            this.a.y = f2;
            this.c = i3;
            this.d = i4;
            this.e = i5;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public final void onLayout(boolean z, int i, int i2, int i3, int i4) {
        try {
            int childCount = getChildCount();
            for (int i5 = 0; i5 < childCount; i5++) {
                View childAt = getChildAt(i5);
                if (childAt != null) {
                    if (childAt.getLayoutParams() instanceof a) {
                        a(childAt, (a) childAt.getLayoutParams());
                    } else {
                        a(childAt, childAt.getLayoutParams());
                    }
                }
            }
            el elVar = this.f;
            if (elVar != null) {
                elVar.c();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private void a(View view, ViewGroup.LayoutParams layoutParams) {
        int[] iArr = new int[2];
        a(view, layoutParams.width, layoutParams.height, iArr);
        if (view instanceof ef) {
            a(view, iArr[0], iArr[1], 20, (this.c.getWaterMarkerPositon().y - 80) - iArr[1], 51);
        } else {
            a(view, iArr[0], iArr[1], 0, 0, 51);
        }
    }

    private void a(View view, a aVar) {
        int[] iArr = new int[2];
        a(view, aVar.width, aVar.height, iArr);
        if (view instanceof em) {
            a(view, iArr[0], iArr[1], getWidth() - iArr[0], getHeight(), aVar.e);
            return;
        }
        if (view instanceof eg) {
            a(view, iArr[0], iArr[1], getWidth() - iArr[0], iArr[1], aVar.e);
            return;
        }
        if (view instanceof ee) {
            a(view, iArr[0], iArr[1], 0, 0, aVar.e);
            return;
        }
        if (aVar.a != null) {
            IPoint iPointObtain = IPoint.obtain();
            MapConfig mapConfig = this.c.getMapConfig();
            GLMapState mapProjection = this.c.getMapProjection();
            if (mapConfig != null && mapProjection != null) {
                iPointObtain.x = (int) aVar.a.x;
                iPointObtain.y = (int) aVar.a.y;
            }
            iPointObtain.x += aVar.c;
            iPointObtain.y += aVar.d;
            a(view, iArr[0], iArr[1], iPointObtain.x, iPointObtain.y, aVar.e);
            iPointObtain.recycle();
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.infowindow.IInfoWindowAction
    public final void showInfoWindow(BasePointOverlay basePointOverlay) {
        if (basePointOverlay == null) {
            return;
        }
        try {
            av avVar = this.b;
            if (!(avVar != null && avVar.a() && basePointOverlay.getTitle() == null && basePointOverlay.getSnippet() == null) && basePointOverlay.isInfoWindowEnable()) {
                BasePointOverlay basePointOverlay2 = this.n;
                if (basePointOverlay2 != null && !basePointOverlay2.getId().equals(basePointOverlay.getId())) {
                    hideInfoWindow();
                }
                if (this.b != null) {
                    this.n = basePointOverlay;
                    this.r = true;
                    this.d.getNativeProperties(basePointOverlay.getId(), "setInfoWindowShown", new Object[]{Boolean.TRUE});
                }
            }
        } catch (Throwable unused) {
        }
    }

    @Override // com.autonavi.base.amap.api.mapcore.infowindow.IInfoWindowAction
    public final void showInfoWindow(BaseOverlayImp baseOverlayImp) {
        if (baseOverlayImp == null) {
            return;
        }
        try {
            av avVar = this.b;
            if (!(avVar != null && avVar.a() && baseOverlayImp.getTitle() == null && baseOverlayImp.getSnippet() == null) && baseOverlayImp.isInfoWindowEnable()) {
                BasePointOverlay basePointOverlay = this.n;
                if (basePointOverlay != null && !basePointOverlay.getId().equals(baseOverlayImp.getId())) {
                    hideInfoWindow();
                }
                if (this.b != null) {
                    baseOverlayImp.setInfoWindowShown(true);
                    this.r = true;
                }
            }
        } catch (Throwable unused) {
        }
    }

    private View a(BasePointOverlay basePointOverlay) throws RemoteException {
        View viewA;
        View viewA2;
        View viewA3 = null;
        if (basePointOverlay instanceof Marker) {
            try {
                if (this.o == null) {
                    this.o = dm.a(this.e, "infowindow_bg.9.png");
                }
            } catch (Throwable th) {
                jw.c(th, "MapOverlayViewGroup", "showInfoWindow decodeDrawableFromAsset");
                th.printStackTrace();
            }
            try {
                if (this.r) {
                    viewA = this.b.a(basePointOverlay);
                    if (viewA == null) {
                        try {
                            viewA = this.b.b(basePointOverlay);
                        } catch (Throwable th2) {
                            th = th2;
                            viewA3 = viewA;
                            jw.c(th, "MapOverlayViewGroup", "getInfoWindow or getInfoContents");
                            th.printStackTrace();
                        }
                    }
                    this.q = viewA;
                    this.r = false;
                } else {
                    viewA = this.q;
                }
                if (viewA != null) {
                    viewA3 = viewA;
                } else {
                    if (!this.b.a()) {
                        return null;
                    }
                    viewA3 = this.b.a(basePointOverlay);
                }
                if (viewA3 != null && viewA3.getBackground() == null) {
                    viewA3.setBackground(this.o);
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } else {
            try {
                if (this.o == null) {
                    this.o = dm.a(this.e, "infowindow_bg.9.png");
                }
            } catch (Throwable th4) {
                jw.c(th4, "MapOverlayViewGroup", "showInfoWindow decodeDrawableFromAsset");
                th4.printStackTrace();
            }
            try {
                if (this.r) {
                    viewA2 = this.b.a(basePointOverlay);
                    if (viewA2 == null) {
                        try {
                            viewA2 = this.b.b(basePointOverlay);
                        } catch (Throwable th5) {
                            th = th5;
                            viewA3 = viewA2;
                            jw.c(th, "MapOverlayViewGroup", "getInfoWindow or getInfoContents");
                            th.printStackTrace();
                            return viewA3;
                        }
                    }
                    this.q = viewA2;
                    this.r = false;
                } else {
                    viewA2 = this.q;
                }
                if (viewA2 != null) {
                    viewA3 = viewA2;
                } else {
                    if (!this.b.a()) {
                        return null;
                    }
                    viewA3 = this.b.a(basePointOverlay);
                }
                if (viewA3.getBackground() == null) {
                    viewA3.setBackground(this.o);
                }
                return viewA3;
            } catch (Throwable th6) {
                th = th6;
            }
        }
        return viewA3;
    }

    @Override // com.autonavi.base.amap.api.mapcore.infowindow.IInfoWindowAction
    public final void redrawInfoWindow() {
        try {
            BasePointOverlay basePointOverlay = this.n;
            if (basePointOverlay != null && this.d.checkInBounds(basePointOverlay.getId())) {
                if (this.p) {
                    FPoint fPointObtain = FPoint.obtain();
                    this.d.getMarkerInfoWindowOffset(this.n.getId(), fPointObtain);
                    int i = (int) fPointObtain.x;
                    int i2 = (int) (fPointObtain.y + 2.0f);
                    fPointObtain.recycle();
                    View viewA = a(this.n);
                    if (viewA == null) {
                        View view = this.m;
                        if (view == null || view.getVisibility() != 0) {
                            return;
                        }
                        hideInfoWindow();
                        return;
                    }
                    FPoint fPointObtain2 = FPoint.obtain();
                    this.d.getOverlayScreenPos(this.n.getId(), fPointObtain2);
                    a(viewA, (int) fPointObtain2.x, (int) fPointObtain2.y, i, i2);
                    View view2 = this.m;
                    if (view2 != null) {
                        a aVar = (a) view2.getLayoutParams();
                        if (aVar != null) {
                            aVar.a = FPoint.obtain(fPointObtain2.x, fPointObtain2.y);
                            aVar.c = i;
                            aVar.d = i2;
                        }
                        onLayout(false, 0, 0, 0, 0);
                        if (this.b.a()) {
                            this.b.a(this.n.getTitle(), this.n.getSnippet());
                        }
                        if (this.m.getVisibility() == 8) {
                            this.m.setVisibility(0);
                        }
                    }
                    fPointObtain2.recycle();
                    return;
                }
                return;
            }
            View view3 = this.m;
            if (view3 == null || view3.getVisibility() != 0) {
                return;
            }
            this.m.setVisibility(8);
        } catch (Throwable th) {
            jw.c(th, "MapOverlayViewGroup", "redrawInfoWindow");
            dx.a(th);
        }
    }

    private void a(View view, int i, int i2, int i3, int i4) throws RemoteException {
        int i5;
        int i6;
        if (view == null) {
            return;
        }
        View view2 = this.m;
        if (view2 != null) {
            if (view == view2) {
                return;
            }
            view2.clearFocus();
            removeView(this.m);
        }
        this.m = view;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        this.m.setDrawingCacheEnabled(true);
        this.m.setDrawingCacheQuality(0);
        if (layoutParams != null) {
            int i7 = layoutParams.width;
            i6 = layoutParams.height;
            i5 = i7;
        } else {
            i5 = -2;
            i6 = -2;
        }
        addView(this.m, new a(i5, i6, i, i2, i3, i4, 81));
    }

    @Override // com.autonavi.base.amap.api.mapcore.infowindow.IInfoWindowAction
    public final void hideInfoWindow() {
        try {
            IAMapDelegate iAMapDelegate = this.c;
            if (iAMapDelegate == null || iAMapDelegate.getMainHandler() == null) {
                return;
            }
            this.c.getMainHandler().post(new Runnable() { // from class: com.amap.api.col.3sl.eh.2
                @Override // java.lang.Runnable
                public final void run() {
                    if (eh.this.m != null) {
                        eh.this.m.clearFocus();
                        eh ehVar = eh.this;
                        ehVar.removeView(ehVar.m);
                        dx.a(eh.this.m.getBackground());
                        dx.a(eh.this.o);
                        eh.f(eh.this);
                    }
                }
            });
            BasePointOverlay basePointOverlay = this.n;
            if (basePointOverlay != null) {
                this.d.getNativeProperties(basePointOverlay.getId(), "setInfoWindowShown", new Object[]{Boolean.FALSE});
            }
            this.n = null;
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private void a(View view, int i, int i2, int[] iArr) {
        View view2;
        if ((view instanceof ListView) && (view2 = (View) view.getParent()) != null) {
            iArr[0] = view2.getWidth();
            iArr[1] = view2.getHeight();
        }
        if (i <= 0 || i2 <= 0) {
            view.measure(0, 0);
        }
        if (i == -2) {
            iArr[0] = view.getMeasuredWidth();
        } else if (i == -1) {
            iArr[0] = getMeasuredWidth();
        } else {
            iArr[0] = i;
        }
        if (i2 == -2) {
            iArr[1] = view.getMeasuredHeight();
        } else if (i2 == -1) {
            iArr[1] = getMeasuredHeight();
        } else {
            iArr[1] = i2;
        }
    }

    private void a(View view, int i, int i2, int i3, int i4, int i5) {
        int i6;
        int i7 = i5 & 7;
        int i8 = i5 & 112;
        if (i7 == 5) {
            i3 -= i;
        } else if (i7 == 1) {
            i3 -= i / 2;
        }
        if (i8 == 80) {
            i4 -= i2;
        } else {
            if (i8 == 17) {
                i6 = i2 / 2;
            } else if (i8 == 16) {
                i4 /= 2;
                i6 = i2 / 2;
            }
            i4 -= i6;
        }
        view.layout(i3, i4, i3 + i, i4 + i2);
        if (view instanceof IGLSurfaceView) {
            this.c.changeSize(i, i2);
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void g() {
        hideInfoWindow();
        dx.a(this.o);
        l();
        removeAllViews();
        this.q = null;
    }

    @Override // com.autonavi.base.amap.api.mapcore.infowindow.IInfoWindowAction
    public final boolean onInfoWindowTap(MotionEvent motionEvent) {
        return (this.m == null || this.n == null || !dx.a(new Rect(this.m.getLeft(), this.m.getTop(), this.m.getRight(), this.m.getBottom()), (int) motionEvent.getX(), (int) motionEvent.getY())) ? false : true;
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void a(Canvas canvas) {
        Bitmap drawingCache;
        View view = this.m;
        if (view == null || this.n == null || (drawingCache = view.getDrawingCache(true)) == null) {
            return;
        }
        canvas.drawBitmap(drawingCache, this.m.getLeft(), this.m.getTop(), new Paint());
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void a(ef.a aVar) {
        ef efVar = this.k;
        if (efVar == null) {
            this.a.a(this, aVar);
        } else {
            efVar.a(aVar);
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void h() {
        ee eeVar = this.h;
        if (eeVar == null) {
            this.a.a(this, new Object[0]);
        } else {
            eeVar.b();
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void h(Boolean bool) {
        eg egVar = this.g;
        if (egVar == null) {
            this.a.a(this, bool);
        } else {
            egVar.a(bool.booleanValue());
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void i(Boolean bool) {
        ef efVar = this.k;
        if (efVar == null) {
            this.a.a(this, bool);
        } else {
            efVar.a(bool.booleanValue());
        }
    }

    @Override // com.amap.api.col.p0003sl.ei
    public final void i() {
        Context context;
        if (!this.s || (context = this.e) == null) {
            return;
        }
        a(context);
        ej ejVar = this.a;
        if (ejVar != null) {
            ejVar.a();
        }
    }
}
