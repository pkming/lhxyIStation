package com.amap.api.maps.model;

import android.graphics.Bitmap;
import com.autonavi.base.ae.gmap.gloverlay.AVectorCrossAttr;

/* JADX INFO: loaded from: classes2.dex */
public class CrossOverlayOptions {
    AVectorCrossAttr a = null;
    private Bitmap bitmapDescriptor = null;

    public AVectorCrossAttr getAttribute() {
        return this.a;
    }

    public CrossOverlayOptions setAttribute(AVectorCrossAttr aVectorCrossAttr) {
        this.a = aVectorCrossAttr;
        return this;
    }

    public CrossOverlayOptions setRes(Bitmap bitmap) {
        this.bitmapDescriptor = bitmap;
        return this;
    }

    public Bitmap getRes() {
        return this.bitmapDescriptor;
    }
}
