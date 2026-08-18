package com.amap.api.maps.model.particle;

/* JADX INFO: loaded from: classes2.dex */
public abstract class ColorGenerate {
    protected final int TYPE_DEFAULT = -1;
    protected final int TYPE_RANDOMCOLORBETWEENTWOCONSTANTS = 0;
    protected int type = -1;

    public abstract float[] getColor();
}
