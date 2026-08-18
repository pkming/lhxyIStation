package com.amap.api.maps.model;

import android.text.TextUtils;
import com.amap.api.maps.interfaces.IGlOverlayLayer;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes2.dex */
public class GLTFOverlay extends BaseOverlay {
    private WeakReference<IGlOverlayLayer> glOverlayLayerRef;
    private GLTFOverlayOptions mOptions;

    public GLTFOverlay(IGlOverlayLayer iGlOverlayLayer, GLTFOverlayOptions gLTFOverlayOptions, String str) {
        super(str);
        this.glOverlayLayerRef = new WeakReference<>(iGlOverlayLayer);
        this.mOptions = gLTFOverlayOptions;
    }

    public void remove() {
        try {
            IGlOverlayLayer iGlOverlayLayer = this.glOverlayLayerRef.get();
            if (iGlOverlayLayer != null) {
                iGlOverlayLayer.removeOverlay(this.overlayName);
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public GLTFOverlayOptions getOptions() {
        return this.mOptions.m37clone();
    }

    public String getId() {
        try {
            return this.overlayName;
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    public int hashCode() {
        return super.hashCode();
    }

    public void setLatLng(LatLng latLng) {
        try {
            GLTFOverlayOptions gLTFOverlayOptions = this.mOptions;
            if (gLTFOverlayOptions != null) {
                gLTFOverlayOptions.latLng(latLng);
                a();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LatLng getLatlng() {
        GLTFOverlayOptions gLTFOverlayOptions = this.mOptions;
        if (gLTFOverlayOptions != null) {
            return gLTFOverlayOptions.getLatLng();
        }
        return null;
    }

    public void setDraggable(boolean z) {
        GLTFOverlayOptions gLTFOverlayOptions = this.mOptions;
        if (gLTFOverlayOptions != null) {
            gLTFOverlayOptions.setDraggable(z);
        }
    }

    public boolean isDraggable() {
        GLTFOverlayOptions gLTFOverlayOptions = this.mOptions;
        if (gLTFOverlayOptions != null) {
            return gLTFOverlayOptions.isDraggable();
        }
        return true;
    }

    public void setClickable(boolean z) {
        GLTFOverlayOptions gLTFOverlayOptions = this.mOptions;
        if (gLTFOverlayOptions != null) {
            gLTFOverlayOptions.setClickable(z);
        }
    }

    public boolean isClickable() {
        GLTFOverlayOptions gLTFOverlayOptions = this.mOptions;
        if (gLTFOverlayOptions != null) {
            return gLTFOverlayOptions.isClickable();
        }
        return false;
    }

    public void tapClick() {
        GLTFOverlayOptions gLTFOverlayOptions = this.mOptions;
        if (gLTFOverlayOptions != null) {
            gLTFOverlayOptions.tapClick();
        }
        try {
            a("tapClick");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isInfoWindowShow() {
        GLTFOverlayOptions gLTFOverlayOptions = this.mOptions;
        if (gLTFOverlayOptions != null) {
            return gLTFOverlayOptions.isInfoWindowShow();
        }
        return false;
    }

    public void setInfoWindowView(BitmapDescriptor bitmapDescriptor) {
        GLTFOverlayOptions gLTFOverlayOptions = this.mOptions;
        if (gLTFOverlayOptions != null) {
            gLTFOverlayOptions.infoWindowView(bitmapDescriptor);
            a();
        }
    }

    public BitmapDescriptor getInfoWindowView() {
        GLTFOverlayOptions gLTFOverlayOptions = this.mOptions;
        if (gLTFOverlayOptions != null) {
            return gLTFOverlayOptions.getInfoWindowView();
        }
        return null;
    }

    public void setCurrentAnimationIndex(int i) {
        GLTFOverlayOptions gLTFOverlayOptions = this.mOptions;
        if (gLTFOverlayOptions != null) {
            gLTFOverlayOptions.setCurrentAnimationIndex(i);
            a();
        }
    }

    public int getCurrentAnimationIndex() {
        GLTFOverlayOptions gLTFOverlayOptions = this.mOptions;
        if (gLTFOverlayOptions == null) {
            return -1;
        }
        gLTFOverlayOptions.getCurrentAnimationIndex();
        return -1;
    }

    public boolean isAnimated() {
        Object objA = a("isAnimated");
        if (objA instanceof Boolean) {
            return ((Boolean) objA).booleanValue();
        }
        return false;
    }

    public void setZIndex(float f) {
        GLTFOverlayOptions gLTFOverlayOptions = this.mOptions;
        if (gLTFOverlayOptions != null) {
            gLTFOverlayOptions.setZIndex(f);
            a();
        }
    }

    public float getZIndex() {
        GLTFOverlayOptions gLTFOverlayOptions = this.mOptions;
        if (gLTFOverlayOptions != null) {
            return gLTFOverlayOptions.getZIndex();
        }
        return 0.0f;
    }

    private Object a(String str) {
        try {
            IGlOverlayLayer iGlOverlayLayer = this.glOverlayLayerRef.get();
            if (!TextUtils.isEmpty(this.overlayName) && iGlOverlayLayer != null) {
                return iGlOverlayLayer.getNativeProperties(this.overlayName, str, null);
            }
        } catch (Throwable unused) {
        }
        return null;
    }

    private void a() {
        try {
            IGlOverlayLayer iGlOverlayLayer = this.glOverlayLayerRef.get();
            if (TextUtils.isEmpty(this.overlayName) || iGlOverlayLayer == null) {
                return;
            }
            iGlOverlayLayer.updateOption(this.overlayName, this.mOptions);
        } catch (Throwable unused) {
        }
    }
}
