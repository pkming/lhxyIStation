package com.autonavi.base.ae.gmap.gloverlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;
import com.amap.api.col.p0003sl.eq;
import com.amap.api.col.p0003sl.er;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.RouteOverlay;
import com.autonavi.amap.mapcore.interfaces.IAMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class RouteOverlayInner extends BaseMapOverlay<BaseRouteOverlay, Object> implements IRouteOverlayInner {
    private float maxDisplayLevel;
    private float minDisplayLevel;
    private int property;

    @Override // com.autonavi.base.ae.gmap.gloverlay.BaseMapOverlay
    public void addItem(Object obj) {
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.BaseMapOverlay
    public void resumeMarker(Bitmap bitmap) {
    }

    public RouteOverlayInner(int i, Context context, IAMap iAMap) {
        super(i, context, iAMap);
        this.minDisplayLevel = 3.0f;
        this.maxDisplayLevel = 20.0f;
        this.property = 1;
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void addRouteItem(final int i, final er[] erVarArr, final int i2, final eq eqVar, final int[] iArr) {
        if (this.mMapView == null) {
            return;
        }
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.1
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).addRouteItem(i, erVarArr, i2, eqVar, iArr);
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setCar2DPosition(final int i, final float f) {
        if (this.mMapView == null) {
            return;
        }
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.2
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setCar2DPosition(i, f);
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setCar3DPosition(final int i, final float f) {
        if (this.mMapView == null) {
            return;
        }
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.3
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setCar3DPosition(i, f);
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void addRouteName() {
        if (this.mMapView == null) {
            return;
        }
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.4
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).addRouteName();
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void removeRouteName() {
        if (this.mMapView == null) {
            return;
        }
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.5
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).removeRouteName();
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setLineWidthScale(final float f) {
        if (this.mMapView == null) {
            return;
        }
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.6
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setLineWidthScale(f);
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setLine2DWidth(final int i, final int i2) {
        if (this.mMapView == null) {
            return;
        }
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.7
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setLine2DWidth(i, i2);
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setShowArrow(final boolean z) {
        if (this.mMapView == null) {
            return;
        }
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.8
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setShowArrow(z);
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setArrow3DTexture(final BitmapDescriptor bitmapDescriptor) {
        if (this.mMapView == null) {
            return;
        }
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.9
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setArrow3DTexture(bitmapDescriptor);
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setRouteItemParam(final er erVar) {
        if (this.mMapView == null) {
            return;
        }
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.10
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setRouteItemParam(erVar);
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setHighlightType(final int i) {
        if (this.mMapView == null) {
            return;
        }
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.11
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setHighlightType(i);
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setHighlightParam(final RouteOverlay.RouteOverlayHighLightParam routeOverlayHighLightParam) {
        if (this.mMapView == null) {
            return;
        }
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.12
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setHighlightParam(routeOverlayHighLightParam);
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setSelectStatus(final boolean z) {
        if (this.mMapView == null) {
            return;
        }
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.13
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setSelectStatus(z);
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setShowNaviRouteNameCountMap(final Map<Integer, Integer> map) {
        if (this.mMapView == null) {
            return;
        }
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.14
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setShowNaviRouteNameCountMap(map);
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setArrowFlow(final boolean z) {
        if (this.mMapView == null) {
            return;
        }
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.15
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setArrowFlow(z);
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void remove() {
        if (this.mMapView == null) {
            return;
        }
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.16
            @Override // java.lang.Runnable
            public void run() {
                RouteOverlayInner.this.releaseInstance();
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.BaseMapOverlay
    protected void iniGLOverlay() {
        if (this.mMapView != null) {
            this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.17
                @Override // java.lang.Runnable
                public void run() {
                    RouteOverlayInner.this.mGLOverlay = new BaseRouteOverlay(RouteOverlayInner.this.mEngineID, RouteOverlayInner.this.mMapView, hashCode());
                    ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setMinDisplayLevel(3.0f);
                    ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setMaxDisplayLevel(20.0f);
                    ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setOverlayPriorityOnly(1);
                }
            });
        }
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.BaseMapOverlay, com.autonavi.amap.mapcore.interfaces.ICrossVectorOverlay
    public void setVisible(final boolean z) {
        if (this.mMapView == null) {
            return;
        }
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.18
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setVisible(z);
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setDisplayRange(final float f, final float f2) {
        if (this.mMapView == null) {
            return;
        }
        this.minDisplayLevel = f;
        this.maxDisplayLevel = f2;
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.19
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setMinDisplayLevel(f);
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setMaxDisplayLevel(f2);
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public Pair<Float, Float> getDisplayRange() {
        return new Pair<>(Float.valueOf(this.minDisplayLevel), Float.valueOf(this.maxDisplayLevel));
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public void setOverlayProperty(final int i) {
        if (this.mMapView == null) {
            return;
        }
        this.property = i;
        this.mMapView.queueEvent(new Runnable() { // from class: com.autonavi.base.ae.gmap.gloverlay.RouteOverlayInner.20
            @Override // java.lang.Runnable
            public void run() {
                ((BaseRouteOverlay) RouteOverlayInner.this.mGLOverlay).setOverlayPriorityOnly(i);
            }
        });
    }

    @Override // com.autonavi.base.ae.gmap.gloverlay.IRouteOverlayInner
    public int getOverlayProperty() {
        return this.property;
    }
}
