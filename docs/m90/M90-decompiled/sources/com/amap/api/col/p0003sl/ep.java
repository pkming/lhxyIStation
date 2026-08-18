package com.amap.api.col.p0003sl;

import com.amap.api.maps.interfaces.IGlOverlayLayer;
import com.amap.api.maps.model.BaseOverlay;
import java.lang.ref.WeakReference;

/* JADX INFO: compiled from: ContourLineOverlay.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ep extends BaseOverlay {
    private eo a;
    private WeakReference<IGlOverlayLayer> b;

    public ep(IGlOverlayLayer iGlOverlayLayer, eo eoVar, String str) {
        super(str);
        this.b = new WeakReference<>(iGlOverlayLayer);
        this.a = eoVar;
    }
}
