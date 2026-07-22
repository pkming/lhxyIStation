package com.autonavi.base.ae.gmap.gloverlay;

import android.util.Pair;
import com.amap.api.col.p0003sl.eq;
import com.amap.api.col.p0003sl.er;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.RouteOverlay;
import com.autonavi.base.ae.gmap.gloverlay.GLOverlay;
import com.autonavi.base.amap.api.mapcore.IAMapDelegate;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class BaseRouteOverlay extends GLOverlay implements IRouteOverlayInner {
    private static native void nativeAddRouteItem(long j, int i, er[] erVarArr, int i2, eq eqVar, int[] iArr);

    private static native void nativeAddRouteName(long j);

    private static native void nativeRemoveRouteName(long j, long j2);

    private static native void nativeSetArrow3DTexture(long j, int i);

    private static native void nativeSetArrowFlow(long j, boolean z);

    private static native void nativeSetCar2DPosition(long j, int i, float f);

    private static native void nativeSetCar3DPosition(long j, int i, float f);

    private static native void nativeSetHighlightParam(long j, int[] iArr);

    private static native void nativeSetHighlightType(long j, int i);

    private static native void nativeSetLine2DWidth(long j, int i, int i2);

    private static native void nativeSetLineWidthScale(long j, float f);

    private static native void nativeSetRouteItemParam(long j, er erVar);

    private static native void nativeSetSelectStatus(long j, boolean z);

    private static native void nativeSetShowArrow(long j, boolean z);

    private static native void nativeSetShowNaviRouteNameCountMap(long j, int[] iArr, int[] iArr2);

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public Pair<Float, Float> getDisplayRange() {
        return null;
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public int getOverlayProperty() {
        return 0;
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void remove() {
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setDisplayRange(float f, float f2) {
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setOverlayProperty(int i) {
    }

    public BaseRouteOverlay(final int i, IAMapDelegate iAMapDelegate, int i2) {
        super(i, iAMapDelegate, i2);
        if (this.mGLMapView == null || this.mGLMapView.getGLMapEngine() == null) {
            return;
        }
        this.mGLMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.BaseRouteOverlay.1
            @Override // java.lang.Runnable
            public void run() {
                BaseRouteOverlay baseRouteOverlay = BaseRouteOverlay.this;
                baseRouteOverlay.mNativeInstance = baseRouteOverlay.mGLMapView.getGLMapEngine().createOverlay(i, GLOverlay.EAMapOverlayTpye.AMAPROUTE_OVERLAY.getId());
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.GLOverlay
    void releaseInstance() {
        long j = this.mNativeInstance;
        this.mNativeInstance = 0L;
        this.mGLMapView.getGLMapEngine().destroyOverlay(this.mEngineID, j);
        super.releaseInstance();
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void addRouteItem(int i, er[] erVarArr, int i2, eq eqVar, int[] iArr) {
        int iCreateOverlayTexture;
        int iCreateOverlayTexture2;
        int iCreateOverlayTexture3;
        if (this.mNativeInstance == 0) {
            return;
        }
        for (int i3 = 0; i3 < erVarArr.length; i3++) {
            if (erVarArr[i3] == null) {
                return;
            }
            er erVar = erVarArr[i3];
            if (erVar.a != null && (iCreateOverlayTexture3 = this.mGLMapView.createOverlayTexture(this.mEngineID, erVar.a.getBitmap())) >= 0) {
                erVar.b = iCreateOverlayTexture3;
            }
            if (erVar.c != null && (iCreateOverlayTexture2 = this.mGLMapView.createOverlayTexture(this.mEngineID, erVar.c.getBitmap())) >= 0) {
                erVar.d = iCreateOverlayTexture2;
            }
            if (erVar.e != null && (iCreateOverlayTexture = this.mGLMapView.createOverlayTexture(this.mEngineID, erVar.e.getBitmap())) >= 0) {
                erVar.f = iCreateOverlayTexture;
            }
        }
        nativeAddRouteItem(this.mNativeInstance, i, erVarArr, i2, eqVar, iArr);
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setCar2DPosition(int i, float f) {
        if (this.mNativeInstance == 0) {
            return;
        }
        nativeSetCar2DPosition(this.mNativeInstance, i, f);
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setCar3DPosition(int i, float f) {
        if (this.mNativeInstance == 0) {
            return;
        }
        nativeSetCar3DPosition(this.mNativeInstance, i, f);
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void addRouteName() {
        if (this.mNativeInstance == 0) {
            return;
        }
        nativeAddRouteName(this.mNativeInstance);
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void removeRouteName() {
        if (this.mNativeInstance == 0) {
            return;
        }
        nativeRemoveRouteName(this.mNativeInstance, this.mGLMapView.getGLMapEngine().getNativeInstance());
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setLineWidthScale(float f) {
        if (this.mNativeInstance == 0) {
            return;
        }
        nativeSetLineWidthScale(this.mNativeInstance, f);
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setLine2DWidth(int i, int i2) {
        if (this.mNativeInstance == 0) {
            return;
        }
        nativeSetLine2DWidth(this.mNativeInstance, i, i2);
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setShowArrow(boolean z) {
        if (this.mNativeInstance == 0) {
            return;
        }
        nativeSetShowArrow(this.mNativeInstance, z);
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setArrow3DTexture(BitmapDescriptor bitmapDescriptor) {
        int iCreateOverlayTexture;
        if (this.mNativeInstance == 0 || bitmapDescriptor == null || bitmapDescriptor.getBitmap() == null || (iCreateOverlayTexture = this.mGLMapView.createOverlayTexture(this.mEngineID, bitmapDescriptor.getBitmap())) < 0) {
            return;
        }
        nativeSetArrow3DTexture(this.mNativeInstance, iCreateOverlayTexture);
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setRouteItemParam(er erVar) {
        if (this.mNativeInstance == 0) {
            return;
        }
        nativeSetRouteItemParam(this.mNativeInstance, erVar);
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setHighlightType(int i) {
        if (this.mNativeInstance == 0) {
            return;
        }
        nativeSetHighlightType(this.mNativeInstance, i);
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setHighlightParam(RouteOverlay.RouteOverlayHighLightParam routeOverlayHighLightParam) {
        if (this.mNativeInstance == 0) {
            return;
        }
        nativeSetHighlightParam(this.mNativeInstance, new int[]{routeOverlayHighLightParam.fillColorHightLight, routeOverlayHighLightParam.borderColorHightLight, routeOverlayHighLightParam.fillColorNormal, routeOverlayHighLightParam.borderColorNormal, routeOverlayHighLightParam.arrowColorNormal});
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setSelectStatus(boolean z) {
        if (this.mNativeInstance == 0) {
            return;
        }
        nativeSetSelectStatus(this.mNativeInstance, z);
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setShowNaviRouteNameCountMap(Map<Integer, Integer> map) {
        if (this.mNativeInstance == 0) {
            return;
        }
        int[] iArr = new int[map.size()];
        int[] iArr2 = new int[map.size()];
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            iArr[i] = entry.getKey().intValue();
            iArr2[i] = entry.getValue().intValue();
            i++;
        }
        nativeSetShowNaviRouteNameCountMap(this.mNativeInstance, iArr, iArr2);
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setArrowFlow(boolean z) {
        if (this.mNativeInstance == 0) {
            return;
        }
        nativeSetArrowFlow(this.mNativeInstance, z);
    }
}
