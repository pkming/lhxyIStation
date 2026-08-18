package com.amap.api.maps.model.animation;

import android.view.animation.Interpolator;
import com.amap.api.col.p0003sl.dv;
import com.amap.api.col.p0003sl.md;
import com.amap.api.maps.model.BaseOptions;
import com.autonavi.amap.mapcore.animation.GLAnimation;

/* JADX INFO: loaded from: classes2.dex */
public abstract class Animation {
    public static final int FILL_MODE_BACKWARDS = 1;
    public static final int FILL_MODE_FORWARDS = 0;
    public static final int INFINITE = -1;
    public static final int RESTART = 1;
    public static final int REVERSE = 2;
    protected String animationType;
    public GLAnimation glAnimation;
    private AnimationListener mListener;
    private int fillMode = 0;
    private long duration = 500;
    private Interpolator interpolator = null;
    private boolean mFillBefore = true;
    private boolean mFillAfter = false;
    private boolean mFillEnabled = false;
    private int mRepeatCount = 0;
    private int mRepeatMode = 1;
    private AnimationUpdateFlags updateFlags = new AnimationUpdateFlags();

    public interface AnimationListener {
        void onAnimationEnd();

        void onAnimationStart();
    }

    protected abstract String getAnimationType();

    public Animation() {
        this.animationType = "typeAnimtionBase";
        this.glAnimation = null;
        this.glAnimation = new GLAnimation();
        this.animationType = getAnimationType();
    }

    public void setAnimationListener(AnimationListener animationListener) {
        this.mListener = new a(this, animationListener, (byte) 0);
        this.glAnimation.setAnimationListener(animationListener);
        this.updateFlags.mListenerUpdate = true;
    }

    public void setDuration(long j) {
        this.duration = j;
        GLAnimation gLAnimation = this.glAnimation;
        if (gLAnimation != null) {
            gLAnimation.setDuration(j);
        }
        this.updateFlags.durationUpdate = true;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        GLAnimation gLAnimation = this.glAnimation;
        if (gLAnimation != null) {
            gLAnimation.setInterpolator(interpolator);
        }
        this.updateFlags.interpolatorUpdate = true;
    }

    public void setFillMode(int i) {
        this.fillMode = i;
        if (i == 0) {
            b(true);
            c(false);
            a(false);
        } else {
            b(false);
            a(true);
            c(true);
        }
        this.updateFlags.fillModeUpdate = true;
    }

    public int getFillMode() {
        return this.fillMode;
    }

    private void a(boolean z) {
        this.mFillEnabled = z;
        GLAnimation gLAnimation = this.glAnimation;
        if (gLAnimation != null) {
            gLAnimation.setFillEnabled(z);
        }
        this.updateFlags.mFillEnabledUpdate = true;
    }

    private void b(boolean z) {
        this.mFillAfter = z;
        GLAnimation gLAnimation = this.glAnimation;
        if (gLAnimation != null) {
            gLAnimation.setFillAfter(z);
        }
        this.updateFlags.mFillAfterUpdate = true;
    }

    private void c(boolean z) {
        this.mFillBefore = z;
        GLAnimation gLAnimation = this.glAnimation;
        if (gLAnimation != null) {
            gLAnimation.setFillBefore(z);
        }
        this.updateFlags.mFillBeforeUpdate = true;
    }

    public void setRepeatCount(int i) {
        this.mRepeatCount = i;
        GLAnimation gLAnimation = this.glAnimation;
        if (gLAnimation != null) {
            gLAnimation.setRepeatCount(i);
        }
        this.updateFlags.mRepeatCountUpdate = true;
    }

    public void setRepeatMode(int i) {
        this.mRepeatMode = i;
        GLAnimation gLAnimation = this.glAnimation;
        if (gLAnimation != null) {
            gLAnimation.setRepeatMode(i);
        }
        this.updateFlags.mRepeatModeUpdate = true;
    }

    public int getRepeatMode() {
        GLAnimation gLAnimation = this.glAnimation;
        if (gLAnimation != null) {
            return gLAnimation.getRepeatMode();
        }
        return 1;
    }

    public int getRepeatCount() {
        GLAnimation gLAnimation = this.glAnimation;
        if (gLAnimation != null) {
            return gLAnimation.getRepeatCount();
        }
        return 0;
    }

    public void resetUpdateFlags() {
        this.updateFlags.reset();
    }

    public AnimationUpdateFlags getUpdateFlags() {
        return this.updateFlags;
    }

    protected static class AnimationUpdateFlags extends BaseOptions.BaseUpdateFlags {
        protected boolean mListenerUpdate = false;
        protected boolean durationUpdate = false;
        protected boolean interpolatorUpdate = false;
        protected boolean fillModeUpdate = false;
        protected boolean mFillEnabledUpdate = false;
        protected boolean mFillAfterUpdate = false;
        protected boolean mFillBeforeUpdate = false;
        protected boolean mRepeatCountUpdate = false;
        protected boolean mRepeatModeUpdate = false;

        protected AnimationUpdateFlags() {
        }

        @Override // com.amap.api.maps.model.BaseOptions.BaseUpdateFlags
        public void reset() {
            super.reset();
            this.mListenerUpdate = false;
            this.durationUpdate = false;
            this.interpolatorUpdate = false;
            this.fillModeUpdate = false;
            this.mFillEnabledUpdate = false;
            this.mFillAfterUpdate = false;
            this.mFillBeforeUpdate = false;
            this.mRepeatCountUpdate = false;
            this.mRepeatModeUpdate = false;
        }
    }

    private class a implements AnimationListener {
        private final md b;
        private final md c;

        /* synthetic */ a(Animation animation, AnimationListener animationListener, byte b) {
            this(animationListener);
        }

        private a(final AnimationListener animationListener) {
            this.b = new md() { // from class: com.amap.api.maps.model.animation.Animation.a.1
                @Override // com.amap.api.col.p0003sl.md
                public final void runTask() {
                    try {
                        AnimationListener animationListener2 = animationListener;
                        if (animationListener2 != null) {
                            animationListener2.onAnimationStart();
                        }
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            };
            this.c = new md() { // from class: com.amap.api.maps.model.animation.Animation.a.2
                @Override // com.amap.api.col.p0003sl.md
                public final void runTask() {
                    try {
                        AnimationListener animationListener2 = animationListener;
                        if (animationListener2 != null) {
                            animationListener2.onAnimationEnd();
                        }
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            };
        }

        @Override // com.amap.api.maps.model.animation.Animation.AnimationListener
        public final void onAnimationStart() {
            dv.a().a(this.b);
        }

        @Override // com.amap.api.maps.model.animation.Animation.AnimationListener
        public final void onAnimationEnd() {
            dv.a().a(this.c);
        }
    }
}
