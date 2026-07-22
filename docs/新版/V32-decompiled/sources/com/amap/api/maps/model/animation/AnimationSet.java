package com.amap.api.maps.model.animation;

import com.autonavi.amap.mapcore.animation.GLAnimationSet;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class AnimationSet extends Animation {
    private List<Animation> mAnimations = new ArrayList();
    private boolean shareInterpolator;

    @Override // com.amap.api.maps.model.animation.Animation
    protected String getAnimationType() {
        return "AnimationSet";
    }

    public AnimationSet(boolean z) {
        this.shareInterpolator = false;
        this.glAnimation = new GLAnimationSet(z);
        this.shareInterpolator = z;
    }

    public void addAnimation(Animation animation) {
        ((GLAnimationSet) this.glAnimation).addAnimation(animation);
        this.mAnimations.add(animation);
    }

    public void cleanAnimation() {
        ((GLAnimationSet) this.glAnimation).cleanAnimation();
        this.mAnimations.clear();
    }
}
