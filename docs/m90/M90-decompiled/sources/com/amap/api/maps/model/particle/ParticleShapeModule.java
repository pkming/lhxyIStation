package com.amap.api.maps.model.particle;

/* JADX INFO: loaded from: classes2.dex */
public abstract class ParticleShapeModule {
    protected final int TYPE_DEFAULT = -1;
    protected final int TYPE_SINGLEPOINT = 0;
    protected final int TYPE_RECT = 1;
    protected int type = -1;
    protected boolean isUseRatio = false;

    public abstract float[] getPoint();

    public boolean isUseRatio() {
        return this.isUseRatio;
    }
}
