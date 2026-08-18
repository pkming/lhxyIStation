package com.amap.api.maps.model.animation;

import com.autonavi.amap.mapcore.animation.GLScaleAnimation;

/* JADX INFO: loaded from: classes2.dex */
public class ScaleAnimation extends Animation {
    private float mFromX;
    private float mFromY;
    private float mPivotX;
    private float mPivotY;
    private float mToX;
    private float mToY;

    @Override // com.amap.api.maps.model.animation.Animation
    protected String getAnimationType() {
        return "ScaleAnimation";
    }

    public ScaleAnimation(float f, float f2, float f3, float f4) {
        this.glAnimation = new GLScaleAnimation(f, f2, f3, f4);
        this.mFromX = f;
        this.mToX = f2;
        this.mFromY = f3;
        this.mToY = f4;
        this.mPivotX = 0.0f;
        this.mPivotY = 0.0f;
        setFillMode(1);
    }
}
