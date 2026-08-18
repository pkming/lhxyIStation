package com.amap.api.maps.model.amap3dmodeltile;

import android.os.RemoteException;
import android.text.TextUtils;
import com.amap.api.maps.interfaces.IGlOverlayLayer;
import com.amap.api.maps.model.BaseOverlay;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes2.dex */
public final class AMap3DModelTileOverlay extends BaseOverlay {
    private WeakReference<IGlOverlayLayer> glOverlayLayerRef;
    private AMap3DModelTileOverlayOptions mAMap3DModelTileOverlayOptions;

    public AMap3DModelTileOverlay(IGlOverlayLayer iGlOverlayLayer, AMap3DModelTileOverlayOptions aMap3DModelTileOverlayOptions, String str) {
        super(str);
        this.glOverlayLayerRef = new WeakReference<>(iGlOverlayLayer);
        this.mAMap3DModelTileOverlayOptions = aMap3DModelTileOverlayOptions;
        if (aMap3DModelTileOverlayOptions != null) {
            aMap3DModelTileOverlayOptions.getTileProviderInner().init(iGlOverlayLayer, str);
        }
    }

    public final void remove() {
        try {
            IGlOverlayLayer iGlOverlayLayer = this.glOverlayLayerRef.get();
            if (iGlOverlayLayer != null) {
                iGlOverlayLayer.removeOverlay(this.overlayName);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public final void setZIndex(float f) {
        this.mAMap3DModelTileOverlayOptions.setZIndex(f);
        a();
    }

    public final float getZIndex() {
        return this.mAMap3DModelTileOverlayOptions.getZIndex();
    }

    public final void setVisible(boolean z) {
        this.mAMap3DModelTileOverlayOptions.setVisible(z);
        a();
    }

    public final boolean visible() {
        return this.mAMap3DModelTileOverlayOptions.visible();
    }

    private void a() {
        try {
            IGlOverlayLayer iGlOverlayLayer = this.glOverlayLayerRef.get();
            if (TextUtils.isEmpty(this.overlayName) || iGlOverlayLayer == null) {
                return;
            }
            iGlOverlayLayer.updateOption(this.overlayName, this.mAMap3DModelTileOverlayOptions);
        } catch (Throwable unused) {
        }
    }
}
