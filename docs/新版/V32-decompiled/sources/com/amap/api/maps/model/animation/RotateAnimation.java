package com.amap.api.maps.model.animation;

import com.autonavi.amap.mapcore.animation.GLRotateAnimation;

/* JADX INFO: loaded from: classes2.dex */
public class RotateAnimation extends Animation {
    private float mFromDegrees;
    private float mToDegrees;

    @Override // com.amap.api.maps.model.animation.Animation
    protected String getAnimationType() {
        return "RotateAnimation";
    }

    public RotateAnimation(float f, float f2, float f3, float f4, float f5) {
        this.mFromDegrees = 0.0f;
        this.mToDegrees = 1.0f;
        this.glAnimation = new GLRotateAnimation(f, f2, f3, f4, f5);
        this.mFromDegrees = f;
        this.mToDegrees = f2;
    }

    public RotateAnimation(float f, float f2) {
        this.mFromDegrees = 0.0f;
        this.mToDegrees = 1.0f;
        this.glAnimation = new GLRotateAnimation(f, f2, 0.0f, 0.0f, 0.0f);
        this.mFromDegrees = f;
        this.mToDegrees = f2;
    }
}
