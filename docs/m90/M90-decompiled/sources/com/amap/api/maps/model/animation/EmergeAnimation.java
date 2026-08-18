package com.amap.api.maps.model.animation;

import com.amap.api.maps.model.LatLng;
import com.autonavi.amap.mapcore.animation.GLEmergeAnimation;

/* JADX INFO: loaded from: classes2.dex */
public class EmergeAnimation extends Animation {
    @Override // com.amap.api.maps.model.animation.Animation
    protected String getAnimationType() {
        return "EmergeAnimation";
    }

    public EmergeAnimation(LatLng latLng) {
        this.glAnimation = new GLEmergeAnimation(latLng);
    }
}
