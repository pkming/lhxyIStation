package com.autonavi.amap.mapcore.animation;

import android.net.LinkQualityInfo;
import android.view.animation.Interpolator;
import com.amap.api.maps.model.animation.Animation;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class GLAnimationSet extends GLAnimation {
    private static final int PROPERTY_CHANGE_BOUNDS_MASK = 128;
    private static final int PROPERTY_DURATION_MASK = 32;
    private static final int PROPERTY_FILL_AFTER_MASK = 1;
    private static final int PROPERTY_FILL_BEFORE_MASK = 2;
    private static final int PROPERTY_MORPH_MATRIX_MASK = 64;
    private static final int PROPERTY_REPEAT_MODE_MASK = 4;
    private static final int PROPERTY_SHARE_INTERPOLATOR_MASK = 16;
    private static final int PROPERTY_START_OFFSET_MASK = 8;
    private boolean mDirty;
    private boolean mHasAlpha;
    private long mLastEnd;
    private int mFlags = 0;
    private ArrayList<GLAnimation> mAnimations = new ArrayList<>();
    private GLTransformation mTempTransformation = new GLTransformation();

    public GLAnimationSet(boolean z) {
        setFlag(16, z);
        init();
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    public void initialize() {
        boolean z;
        boolean z2;
        super.initialize();
        int i = this.mFlags;
        boolean z3 = (i & 32) == 32;
        boolean z4 = (i & 1) == 1;
        boolean z5 = (i & 2) == 2;
        boolean z6 = (i & 4) == 4;
        boolean z7 = (i & 16) == 16;
        boolean z8 = (i & 8) == 8;
        if (z7) {
            ensureInterpolator();
        }
        ArrayList<GLAnimation> arrayList = this.mAnimations;
        int size = arrayList.size();
        long j = this.mDuration;
        boolean z9 = this.mFillAfter;
        boolean z10 = this.mFillBefore;
        int i2 = this.mRepeatMode;
        Interpolator interpolator = this.mInterpolator;
        boolean z11 = z8;
        long j2 = this.mStartOffset;
        int i3 = 0;
        while (i3 < size) {
            ArrayList<GLAnimation> arrayList2 = arrayList;
            GLAnimation gLAnimation = arrayList.get(i3);
            if (z3) {
                gLAnimation.setDuration(j);
            }
            if (z4) {
                gLAnimation.setFillAfter(z9);
            }
            if (z5) {
                gLAnimation.setFillBefore(z10);
            }
            if (z6) {
                gLAnimation.setRepeatMode(i2);
            }
            if (z7) {
                gLAnimation.setInterpolator(interpolator);
            }
            if (z11) {
                z = z3;
                z2 = z4;
                gLAnimation.setStartOffset(gLAnimation.getStartOffset() + j2);
            } else {
                z = z3;
                z2 = z4;
            }
            gLAnimation.initialize();
            i3++;
            z3 = z;
            arrayList = arrayList2;
            z4 = z2;
        }
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    /* JADX INFO: renamed from: clone */
    public GLAnimationSet mo71clone() throws CloneNotSupportedException {
        GLAnimationSet gLAnimationSet = (GLAnimationSet) super.mo71clone();
        gLAnimationSet.mTempTransformation = new GLTransformation();
        gLAnimationSet.mAnimations = new ArrayList<>();
        int size = this.mAnimations.size();
        ArrayList<GLAnimation> arrayList = this.mAnimations;
        for (int i = 0; i < size; i++) {
            gLAnimationSet.mAnimations.add(arrayList.get(i).mo71clone());
        }
        return gLAnimationSet;
    }

    private void setFlag(int i, boolean z) {
        if (z) {
            this.mFlags = i | this.mFlags;
        } else {
            this.mFlags = (~i) & this.mFlags;
        }
    }

    private void init() {
        this.mStartTime = 0L;
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    public void setFillAfter(boolean z) {
        this.mFlags |= 1;
        super.setFillAfter(z);
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    public void setFillBefore(boolean z) {
        this.mFlags |= 2;
        super.setFillBefore(z);
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    public void setRepeatMode(int i) {
        this.mFlags |= 4;
        super.setRepeatMode(i);
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    public void setStartOffset(long j) {
        this.mFlags |= 8;
        super.setStartOffset(j);
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    public boolean hasAlpha() {
        if (this.mDirty) {
            int i = 0;
            this.mHasAlpha = false;
            this.mDirty = false;
            int size = this.mAnimations.size();
            ArrayList<GLAnimation> arrayList = this.mAnimations;
            while (true) {
                if (i >= size) {
                    break;
                }
                if (arrayList.get(i).hasAlpha()) {
                    this.mHasAlpha = true;
                    break;
                }
                i++;
            }
        }
        return this.mHasAlpha;
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    public void setDuration(long j) {
        this.mFlags |= 32;
        super.setDuration(j);
        this.mLastEnd = this.mStartOffset + this.mDuration;
    }

    public void addAnimation(Animation animation) {
        this.mAnimations.add(animation.glAnimation);
        if (((this.mFlags & 64) == 0) && animation.glAnimation.willChangeTransformationMatrix()) {
            this.mFlags |= 64;
        }
        if (((this.mFlags & 128) == 0) && animation.glAnimation.willChangeBounds()) {
            this.mFlags |= 128;
        }
        if ((this.mFlags & 32) == 32) {
            this.mLastEnd = this.mStartOffset + this.mDuration;
        } else if (this.mAnimations.size() == 1) {
            this.mDuration = animation.glAnimation.getStartOffset() + animation.glAnimation.getDuration();
            this.mLastEnd = this.mStartOffset + this.mDuration;
        } else {
            long jMax = Math.max(this.mLastEnd, animation.glAnimation.getStartOffset() + animation.glAnimation.getDuration());
            this.mLastEnd = jMax;
            this.mDuration = jMax - this.mStartOffset;
        }
        this.mDirty = true;
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    public void setStartTime(long j) {
        super.setStartTime(j);
        int size = this.mAnimations.size();
        ArrayList<GLAnimation> arrayList = this.mAnimations;
        for (int i = 0; i < size; i++) {
            arrayList.get(i).setStartTime(j);
        }
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    public long getStartTime() {
        int size = this.mAnimations.size();
        ArrayList<GLAnimation> arrayList = this.mAnimations;
        long jMin = LinkQualityInfo.UNKNOWN_LONG;
        for (int i = 0; i < size; i++) {
            jMin = Math.min(jMin, arrayList.get(i).getStartTime());
        }
        return jMin;
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    public void restrictDuration(long j) {
        super.restrictDuration(j);
        ArrayList<GLAnimation> arrayList = this.mAnimations;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList.get(i).restrictDuration(j);
        }
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    public long getDuration() {
        ArrayList<GLAnimation> arrayList = this.mAnimations;
        int size = arrayList.size();
        if ((this.mFlags & 32) == 32) {
            return this.mDuration;
        }
        long jMax = 0;
        for (int i = 0; i < size; i++) {
            jMax = Math.max(jMax, arrayList.get(i).getDuration());
        }
        return jMax;
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    public long computeDurationHint() {
        int size = this.mAnimations.size();
        ArrayList<GLAnimation> arrayList = this.mAnimations;
        long j = 0;
        for (int i = size - 1; i >= 0; i--) {
            long jComputeDurationHint = arrayList.get(i).computeDurationHint();
            if (jComputeDurationHint > j) {
                j = jComputeDurationHint;
            }
        }
        return j;
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    public boolean getTransformation(long j, GLTransformation gLTransformation) {
        if (!this.mInitialized) {
            initialize();
        }
        int size = this.mAnimations.size();
        ArrayList<GLAnimation> arrayList = this.mAnimations;
        GLTransformation gLTransformation2 = this.mTempTransformation;
        gLTransformation.clear();
        boolean z = true;
        boolean z2 = false;
        boolean z3 = false;
        for (int i = size - 1; i >= 0; i--) {
            GLAnimation gLAnimation = arrayList.get(i);
            gLTransformation2.clear();
            z3 = gLAnimation.getTransformation(j, gLTransformation, getScaleFactor()) || z3;
            z2 = z2 || gLAnimation.hasStarted();
            z = gLAnimation.hasEnded() && z;
        }
        if (z2) {
            try {
                if (!this.mStarted) {
                    if (this.mListener != null) {
                        this.mListener.onAnimationStart();
                    }
                    this.mStarted = true;
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        if (z != this.mEnded) {
            if (this.mListener != null) {
                this.mListener.onAnimationEnd();
            }
            this.mEnded = z;
        }
        return z3;
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    public void scaleCurrentDuration(float f) {
        ArrayList<GLAnimation> arrayList = this.mAnimations;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList.get(i).scaleCurrentDuration(f);
        }
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    public void reset() {
        super.reset();
    }

    public List<GLAnimation> getAnimations() {
        return this.mAnimations;
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    public boolean willChangeTransformationMatrix() {
        return (this.mFlags & 64) == 64;
    }

    @Override // com.autonavi.amap.mapcore.animation.GLAnimation
    public boolean willChangeBounds() {
        return (this.mFlags & 128) == 128;
    }

    public void cleanAnimation() {
        this.mAnimations.clear();
    }
}
