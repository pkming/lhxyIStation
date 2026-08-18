package com.amap.api.maps.model.animation;

import com.amap.api.maps.model.LatLng;
import com.autonavi.amap.mapcore.animation.GLTranslateAnimation;

/* JADX INFO: loaded from: classes2.dex */
public class TranslateAnimation extends Animation {
    private double x;
    private double y;

    @Override // com.amap.api.maps.model.animation.Animation
    protected String getAnimationType() {
        return "TranslateAnimation";
    }

    public TranslateAnimation(LatLng latLng) {
        this.glAnimation = new GLTranslateAnimation(latLng);
        this.x = latLng.latitude;
        this.y = latLng.longitude;
    }
}
