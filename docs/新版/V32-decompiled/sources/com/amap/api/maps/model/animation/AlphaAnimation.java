package com.amap.api.maps.model.animation;

import com.autonavi.amap.mapcore.animation.GLAlphaAnimation;

/* JADX INFO: loaded from: classes2.dex */
public class AlphaAnimation extends Animation {
    private float mFromAlpha;
    private float mToAlpha;

    @Override // com.amap.api.maps.model.animation.Animation
    protected String getAnimationType() {
        return "AlphaAnimation";
    }

    public AlphaAnimation(float f, float f2) {
        this.mFromAlpha = 0.0f;
        this.mToAlpha = 1.0f;
        this.glAnimation = new GLAlphaAnimation(f, f2);
        this.mFromAlpha = f;
        this.mToAlpha = f2;
    }
}
