package com.autonavi.amap.mapcore.animation;

import com.amap.api.maps.model.LatLng;

/* JADX INFO: loaded from: classes2.dex */
public class GLEmergeAnimation extends GLAnimation {
    public LatLng mStartPoint;

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    protected void applyTransformation(float f, GLTransformation gLTransformation) {
    }

    public GLEmergeAnimation(LatLng latLng) {
        this.mStartPoint = latLng;
    }
}
