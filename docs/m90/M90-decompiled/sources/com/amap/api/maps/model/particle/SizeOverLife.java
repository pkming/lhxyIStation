package com.amap.api.maps.model.particle;

/* JADX INFO: loaded from: classes2.dex */
public abstract class SizeOverLife {
    protected final int TYPE_DEFAULT = -1;
    protected final int TYPE_CURVESIZEOVERLIFE = 0;
    protected int type = -1;
    public final int DEFAULT_SIZE = 0;

    public abstract float getSizeX(float f);

    public abstract float getSizeY(float f);

    public abstract float getSizeZ(float f);
}
