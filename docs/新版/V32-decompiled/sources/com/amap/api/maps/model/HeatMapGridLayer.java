package com.amap.api.maps.model;

import android.text.TextUtils;
import com.amap.api.maps.interfaces.IGlOverlayLayer;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes2.dex */
public class HeatMapGridLayer extends BaseOverlay {
    private WeakReference<IGlOverlayLayer> glOverlayLayerRef;
    private HeatMapGridLayerOptions options;

    public HeatMapGridLayer(IGlOverlayLayer iGlOverlayLayer, HeatMapGridLayerOptions heatMapGridLayerOptions, String str) {
        super(str);
        this.glOverlayLayerRef = new WeakReference<>(iGlOverlayLayer);
        this.options = heatMapGridLayerOptions;
        a();
    }

    public void destroy() {
        try {
            IGlOverlayLayer iGlOverlayLayer = this.glOverlayLayerRef.get();
            if (iGlOverlayLayer != null) {
                iGlOverlayLayer.removeOverlay(this.overlayName);
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public String getId() {
        try {
            return this.overlayName;
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    public void setZIndex(float f) {
        try {
            HeatMapGridLayerOptions heatMapGridLayerOptions = this.options;
            if (heatMapGridLayerOptions != null) {
                heatMapGridLayerOptions.zIndex(f);
                a();
            }
        } catch (Throwable unused) {
        }
    }

    public float getZIndex() {
        try {
            HeatMapGridLayerOptions heatMapGridLayerOptions = this.options;
            if (heatMapGridLayerOptions != null) {
                return heatMapGridLayerOptions.getZIndex();
            }
            return 0.0f;
        } catch (Throwable unused) {
            return 0.0f;
        }
    }

    public void setVisible(boolean z) {
        try {
            HeatMapGridLayerOptions heatMapGridLayerOptions = this.options;
            if (heatMapGridLayerOptions != null) {
                heatMapGridLayerOptions.visible(z);
                a();
            }
        } catch (Throwable unused) {
        }
    }

    public boolean isVisible() {
        try {
            HeatMapGridLayerOptions heatMapGridLayerOptions = this.options;
            if (heatMapGridLayerOptions != null) {
                return heatMapGridLayerOptions.isVisible();
            }
            return false;
        } catch (Throwable unused) {
            return false;
        }
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof HeatMapGridLayer)) {
            try {
                if (super.equals(obj)) {
                    return true;
                }
                return ((HeatMapGridLayer) obj).getId().equals(getId());
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        return false;
    }

    public int hashCode() {
        try {
            return super.hashCode();
        } catch (Throwable unused) {
            return 0;
        }
    }

    public HeatMapGridLayerOptions getOptions() {
        try {
            return this.options;
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    public void setOptions(HeatMapGridLayerOptions heatMapGridLayerOptions) {
        try {
            this.options = heatMapGridLayerOptions;
            a();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private void a() {
        try {
            IGlOverlayLayer iGlOverlayLayer = this.glOverlayLayerRef.get();
            if (TextUtils.isEmpty(this.overlayName) || iGlOverlayLayer == null) {
                return;
            }
            iGlOverlayLayer.updateOption(this.overlayName, this.options);
        } catch (Throwable unused) {
        }
    }
}
