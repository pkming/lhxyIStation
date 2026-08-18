package com.amap.api.maps.model;

import android.text.TextUtils;
import com.amap.api.maps.interfaces.IGlOverlayLayer;
import java.lang.ref.WeakReference;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public final class Circle extends BaseOverlay {
    WeakReference<IGlOverlayLayer> a;
    private CircleOptions options;

    public Circle(IGlOverlayLayer iGlOverlayLayer, CircleOptions circleOptions, String str) {
        super(str);
        this.a = new WeakReference<>(iGlOverlayLayer);
        this.options = circleOptions;
    }

    public final void remove() {
        try {
            IGlOverlayLayer iGlOverlayLayer = this.a.get();
            if (iGlOverlayLayer != null) {
                iGlOverlayLayer.removeOverlay(this.overlayName);
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public final String getId() {
        try {
            return this.overlayName;
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    public final void setCenter(LatLng latLng) {
        try {
            CircleOptions circleOptions = this.options;
            if (circleOptions != null) {
                circleOptions.center(latLng);
                a();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public final LatLng getCenter() {
        try {
            CircleOptions circleOptions = this.options;
            if (circleOptions != null) {
                return circleOptions.getCenter();
            }
            return null;
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    public final void setRadius(double d) {
        try {
            CircleOptions circleOptions = this.options;
            if (circleOptions != null) {
                circleOptions.radius(d);
                a();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public final double getRadius() {
        try {
            CircleOptions circleOptions = this.options;
            if (circleOptions != null) {
                return circleOptions.getRadius();
            }
            return 0.0d;
        } catch (Throwable th) {
            th.printStackTrace();
            return 0.0d;
        }
    }

    public final void setStrokeWidth(float f) {
        try {
            CircleOptions circleOptions = this.options;
            if (circleOptions != null) {
                circleOptions.strokeWidth(f);
                a();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public final float getStrokeWidth() {
        try {
            CircleOptions circleOptions = this.options;
            if (circleOptions != null) {
                return circleOptions.getStrokeWidth();
            }
            return 0.0f;
        } catch (Throwable th) {
            th.printStackTrace();
            return 0.0f;
        }
    }

    public final void setStrokeColor(int i) {
        try {
            CircleOptions circleOptions = this.options;
            if (circleOptions != null) {
                circleOptions.strokeColor(i);
                a();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public final int getStrokeColor() {
        try {
            CircleOptions circleOptions = this.options;
            if (circleOptions != null) {
                return circleOptions.getStrokeColor();
            }
            return 0;
        } catch (Throwable th) {
            th.printStackTrace();
            return 0;
        }
    }

    public final void setFillColor(int i) {
        try {
            CircleOptions circleOptions = this.options;
            if (circleOptions != null) {
                circleOptions.fillColor(i);
                a();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public final int getFillColor() {
        try {
            CircleOptions circleOptions = this.options;
            if (circleOptions != null) {
                return circleOptions.getFillColor();
            }
            return 0;
        } catch (Throwable th) {
            th.printStackTrace();
            return 0;
        }
    }

    public final void setZIndex(float f) {
        try {
            CircleOptions circleOptions = this.options;
            if (circleOptions != null) {
                circleOptions.zIndex(f);
                a();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public final float getZIndex() {
        try {
            CircleOptions circleOptions = this.options;
            if (circleOptions != null) {
                return circleOptions.getZIndex();
            }
            return 0.0f;
        } catch (Throwable th) {
            th.printStackTrace();
            return 0.0f;
        }
    }

    public final void setVisible(boolean z) {
        try {
            CircleOptions circleOptions = this.options;
            if (circleOptions != null) {
                circleOptions.visible(z);
                a();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public final boolean isVisible() {
        try {
            CircleOptions circleOptions = this.options;
            if (circleOptions != null) {
                return circleOptions.isVisible();
            }
            return false;
        } catch (Throwable th) {
            th.printStackTrace();
            return false;
        }
    }

    public final boolean equals(Object obj) {
        if (obj != null && (obj instanceof Circle)) {
            try {
                if (super.equals(obj)) {
                    return true;
                }
                return ((Circle) obj).getId() == getId();
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        return false;
    }

    public final int hashCode() {
        try {
            return super.hashCode();
        } catch (Throwable th) {
            th.printStackTrace();
            return 0;
        }
    }

    public final boolean contains(LatLng latLng) {
        try {
            IGlOverlayLayer iGlOverlayLayer = this.a.get();
            if (iGlOverlayLayer != null) {
                return iGlOverlayLayer.IsCircleContainPoint(this.options, latLng);
            }
            return false;
        } catch (Throwable th) {
            th.printStackTrace();
            return false;
        }
    }

    public final void setHoleOptions(List<BaseHoleOptions> list) {
        if (list != null) {
            try {
                synchronized (list) {
                    this.options.getHoleOptions().clear();
                    this.options.addHoles(list);
                    a();
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    public final List<BaseHoleOptions> getHoleOptions() {
        try {
            CircleOptions circleOptions = this.options;
            if (circleOptions != null) {
                return circleOptions.getHoleOptions();
            }
            return null;
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    public final void setStrokeDottedLineType(int i) {
        try {
            CircleOptions circleOptions = this.options;
            if (circleOptions != null) {
                circleOptions.setStrokeDottedLineType(i);
                a();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public final int getStrokeDottedLineType() {
        try {
            CircleOptions circleOptions = this.options;
            if (circleOptions != null) {
                return circleOptions.getStrokeDottedLineType();
            }
            return -1;
        } catch (Throwable th) {
            th.printStackTrace();
            return -1;
        }
    }

    private void a() {
        try {
            IGlOverlayLayer iGlOverlayLayer = this.a.get();
            if (TextUtils.isEmpty(this.overlayName) || iGlOverlayLayer == null) {
                return;
            }
            iGlOverlayLayer.updateOption(this.overlayName, this.options);
        } catch (Throwable unused) {
        }
    }
}
