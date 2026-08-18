package android.view;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class ViewPropertyAnimator {
    private static final int ALPHA = 512;
    private static final int NONE = 0;
    private static final int ROTATION = 16;
    private static final int ROTATION_X = 32;
    private static final int ROTATION_Y = 64;
    private static final int SCALE_X = 4;
    private static final int SCALE_Y = 8;
    private static final int TRANSFORM_MASK = 511;
    private static final int TRANSLATION_X = 1;
    private static final int TRANSLATION_Y = 2;
    private static final int X = 128;
    private static final int Y = 256;
    private HashMap<Animator, Runnable> mAnimatorCleanupMap;
    private HashMap<Animator, Runnable> mAnimatorOnEndMap;
    private HashMap<Animator, Runnable> mAnimatorOnStartMap;
    private HashMap<Animator, Runnable> mAnimatorSetupMap;
    private long mDuration;
    private TimeInterpolator mInterpolator;
    private Runnable mPendingCleanupAction;
    private Runnable mPendingOnEndAction;
    private Runnable mPendingOnStartAction;
    private Runnable mPendingSetupAction;
    private ValueAnimator mTempValueAnimator;
    private final View mView;
    private boolean mDurationSet = false;
    private long mStartDelay = 0;
    private boolean mStartDelaySet = false;
    private boolean mInterpolatorSet = false;
    private Animator.AnimatorListener mListener = null;
    private ValueAnimator.AnimatorUpdateListener mUpdateListener = null;
    private AnimatorEventListener mAnimatorEventListener = new AnimatorEventListener();
    ArrayList<NameValuesHolder> mPendingAnimations = new ArrayList<>();
    private Runnable mAnimationStarter = new Runnable() { // from class: android.view.ViewPropertyAnimator.1
        @Override // java.lang.Runnable
        public void run() {
            ViewPropertyAnimator.this.startAnimation();
        }
    };
    private HashMap<Animator, PropertyBundle> mAnimatorMap = new HashMap<>();

    private static class PropertyBundle {
        ArrayList<NameValuesHolder> mNameValuesHolder;
        int mPropertyMask;

        PropertyBundle(int i, ArrayList<NameValuesHolder> arrayList) {
            this.mPropertyMask = i;
            this.mNameValuesHolder = arrayList;
        }

        boolean cancel(int i) {
            ArrayList<NameValuesHolder> arrayList;
            if ((this.mPropertyMask & i) != 0 && (arrayList = this.mNameValuesHolder) != null) {
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    if (this.mNameValuesHolder.get(i2).mNameConstant == i) {
                        this.mNameValuesHolder.remove(i2);
                        this.mPropertyMask = (~i) & this.mPropertyMask;
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private static class NameValuesHolder {
        float mDeltaValue;
        float mFromValue;
        int mNameConstant;

        NameValuesHolder(int i, float f, float f2) {
            this.mNameConstant = i;
            this.mFromValue = f;
            this.mDeltaValue = f2;
        }
    }

    ViewPropertyAnimator(View view) {
        this.mView = view;
        view.ensureTransformationInfo();
    }

    public ViewPropertyAnimator setDuration(long j) {
        if (j < 0) {
            throw new IllegalArgumentException("Animators cannot have negative duration: " + j);
        }
        this.mDurationSet = true;
        this.mDuration = j;
        return this;
    }

    public long getDuration() {
        if (this.mDurationSet) {
            return this.mDuration;
        }
        if (this.mTempValueAnimator == null) {
            this.mTempValueAnimator = new ValueAnimator();
        }
        return this.mTempValueAnimator.getDuration();
    }

    public long getStartDelay() {
        if (this.mStartDelaySet) {
            return this.mStartDelay;
        }
        return 0L;
    }

    public ViewPropertyAnimator setStartDelay(long j) {
        if (j < 0) {
            throw new IllegalArgumentException("Animators cannot have negative duration: " + j);
        }
        this.mStartDelaySet = true;
        this.mStartDelay = j;
        return this;
    }

    public ViewPropertyAnimator setInterpolator(TimeInterpolator timeInterpolator) {
        this.mInterpolatorSet = true;
        this.mInterpolator = timeInterpolator;
        return this;
    }

    public TimeInterpolator getInterpolator() {
        if (this.mInterpolatorSet) {
            return this.mInterpolator;
        }
        if (this.mTempValueAnimator == null) {
            this.mTempValueAnimator = new ValueAnimator();
        }
        return this.mTempValueAnimator.getInterpolator();
    }

    public ViewPropertyAnimator setListener(Animator.AnimatorListener animatorListener) {
        this.mListener = animatorListener;
        return this;
    }

    public ViewPropertyAnimator setUpdateListener(ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
        this.mUpdateListener = animatorUpdateListener;
        return this;
    }

    public void start() {
        this.mView.removeCallbacks(this.mAnimationStarter);
        startAnimation();
    }

    public void cancel() {
        if (this.mAnimatorMap.size() > 0) {
            Iterator it = ((HashMap) this.mAnimatorMap.clone()).keySet().iterator();
            while (it.hasNext()) {
                ((Animator) it.next()).cancel();
            }
        }
        this.mPendingAnimations.clear();
        this.mView.removeCallbacks(this.mAnimationStarter);
    }

    public ViewPropertyAnimator x(float f) {
        animateProperty(128, f);
        return this;
    }

    public ViewPropertyAnimator xBy(float f) {
        animatePropertyBy(128, f);
        return this;
    }

    public ViewPropertyAnimator y(float f) {
        animateProperty(256, f);
        return this;
    }

    public ViewPropertyAnimator yBy(float f) {
        animatePropertyBy(256, f);
        return this;
    }

    public ViewPropertyAnimator rotation(float f) {
        animateProperty(16, f);
        return this;
    }

    public ViewPropertyAnimator rotationBy(float f) {
        animatePropertyBy(16, f);
        return this;
    }

    public ViewPropertyAnimator rotationX(float f) {
        animateProperty(32, f);
        return this;
    }

    public ViewPropertyAnimator rotationXBy(float f) {
        animatePropertyBy(32, f);
        return this;
    }

    public ViewPropertyAnimator rotationY(float f) {
        animateProperty(64, f);
        return this;
    }

    public ViewPropertyAnimator rotationYBy(float f) {
        animatePropertyBy(64, f);
        return this;
    }

    public ViewPropertyAnimator translationX(float f) {
        animateProperty(1, f);
        return this;
    }

    public ViewPropertyAnimator translationXBy(float f) {
        animatePropertyBy(1, f);
        return this;
    }

    public ViewPropertyAnimator translationY(float f) {
        animateProperty(2, f);
        return this;
    }

    public ViewPropertyAnimator translationYBy(float f) {
        animatePropertyBy(2, f);
        return this;
    }

    public ViewPropertyAnimator scaleX(float f) {
        animateProperty(4, f);
        return this;
    }

    public ViewPropertyAnimator scaleXBy(float f) {
        animatePropertyBy(4, f);
        return this;
    }

    public ViewPropertyAnimator scaleY(float f) {
        animateProperty(8, f);
        return this;
    }

    public ViewPropertyAnimator scaleYBy(float f) {
        animatePropertyBy(8, f);
        return this;
    }

    public ViewPropertyAnimator alpha(float f) {
        animateProperty(512, f);
        return this;
    }

    public ViewPropertyAnimator alphaBy(float f) {
        animatePropertyBy(512, f);
        return this;
    }

    public ViewPropertyAnimator withLayer() {
        this.mPendingSetupAction = new Runnable() { // from class: android.view.ViewPropertyAnimator.2
            @Override // java.lang.Runnable
            public void run() {
                ViewPropertyAnimator.this.mView.setLayerType(2, null);
                if (ViewPropertyAnimator.this.mView.isAttachedToWindow()) {
                    ViewPropertyAnimator.this.mView.buildLayer();
                }
            }
        };
        final int layerType = this.mView.getLayerType();
        this.mPendingCleanupAction = new Runnable() { // from class: android.view.ViewPropertyAnimator.3
            @Override // java.lang.Runnable
            public void run() {
                ViewPropertyAnimator.this.mView.setLayerType(layerType, null);
            }
        };
        if (this.mAnimatorSetupMap == null) {
            this.mAnimatorSetupMap = new HashMap<>();
        }
        if (this.mAnimatorCleanupMap == null) {
            this.mAnimatorCleanupMap = new HashMap<>();
        }
        return this;
    }

    public ViewPropertyAnimator withStartAction(Runnable runnable) {
        this.mPendingOnStartAction = runnable;
        if (runnable != null && this.mAnimatorOnStartMap == null) {
            this.mAnimatorOnStartMap = new HashMap<>();
        }
        return this;
    }

    public ViewPropertyAnimator withEndAction(Runnable runnable) {
        this.mPendingOnEndAction = runnable;
        if (runnable != null && this.mAnimatorOnEndMap == null) {
            this.mAnimatorOnEndMap = new HashMap<>();
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startAnimation() {
        this.mView.setHasTransientState(true);
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(1.0f);
        ArrayList arrayList = (ArrayList) this.mPendingAnimations.clone();
        this.mPendingAnimations.clear();
        int size = arrayList.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            i |= ((NameValuesHolder) arrayList.get(i2)).mNameConstant;
        }
        this.mAnimatorMap.put(valueAnimatorOfFloat, new PropertyBundle(i, arrayList));
        Runnable runnable = this.mPendingSetupAction;
        if (runnable != null) {
            this.mAnimatorSetupMap.put(valueAnimatorOfFloat, runnable);
            this.mPendingSetupAction = null;
        }
        Runnable runnable2 = this.mPendingCleanupAction;
        if (runnable2 != null) {
            this.mAnimatorCleanupMap.put(valueAnimatorOfFloat, runnable2);
            this.mPendingCleanupAction = null;
        }
        Runnable runnable3 = this.mPendingOnStartAction;
        if (runnable3 != null) {
            this.mAnimatorOnStartMap.put(valueAnimatorOfFloat, runnable3);
            this.mPendingOnStartAction = null;
        }
        Runnable runnable4 = this.mPendingOnEndAction;
        if (runnable4 != null) {
            this.mAnimatorOnEndMap.put(valueAnimatorOfFloat, runnable4);
            this.mPendingOnEndAction = null;
        }
        valueAnimatorOfFloat.addUpdateListener(this.mAnimatorEventListener);
        valueAnimatorOfFloat.addListener(this.mAnimatorEventListener);
        if (this.mStartDelaySet) {
            valueAnimatorOfFloat.setStartDelay(this.mStartDelay);
        }
        if (this.mDurationSet) {
            valueAnimatorOfFloat.setDuration(this.mDuration);
        }
        if (this.mInterpolatorSet) {
            valueAnimatorOfFloat.setInterpolator(this.mInterpolator);
        }
        valueAnimatorOfFloat.start();
    }

    private void animateProperty(int i, float f) {
        float value = getValue(i);
        animatePropertyBy(i, value, f - value);
    }

    private void animatePropertyBy(int i, float f) {
        animatePropertyBy(i, getValue(i), f);
    }

    private void animatePropertyBy(int i, float f, float f2) {
        if (this.mAnimatorMap.size() > 0) {
            Animator animator = null;
            Iterator<Animator> it = this.mAnimatorMap.keySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Animator next = it.next();
                PropertyBundle propertyBundle = this.mAnimatorMap.get(next);
                if (propertyBundle.cancel(i) && propertyBundle.mPropertyMask == 0) {
                    animator = next;
                    break;
                }
            }
            if (animator != null) {
                animator.cancel();
            }
        }
        this.mPendingAnimations.add(new NameValuesHolder(i, f, f2));
        this.mView.removeCallbacks(this.mAnimationStarter);
        this.mView.postOnAnimation(this.mAnimationStarter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setValue(int i, float f) {
        View.TransformationInfo transformationInfo = this.mView.mTransformationInfo;
        DisplayList displayList = this.mView.mDisplayList;
        if (i == 1) {
            transformationInfo.mTranslationX = f;
            if (displayList != null) {
                displayList.setTranslationX(f);
                return;
            }
            return;
        }
        if (i == 2) {
            transformationInfo.mTranslationY = f;
            if (displayList != null) {
                displayList.setTranslationY(f);
                return;
            }
            return;
        }
        if (i == 4) {
            transformationInfo.mScaleX = f;
            if (displayList != null) {
                displayList.setScaleX(f);
                return;
            }
            return;
        }
        if (i == 8) {
            transformationInfo.mScaleY = f;
            if (displayList != null) {
                displayList.setScaleY(f);
                return;
            }
            return;
        }
        if (i == 16) {
            transformationInfo.mRotation = f;
            if (displayList != null) {
                displayList.setRotation(f);
                return;
            }
            return;
        }
        if (i == 32) {
            transformationInfo.mRotationX = f;
            if (displayList != null) {
                displayList.setRotationX(f);
                return;
            }
            return;
        }
        if (i == 64) {
            transformationInfo.mRotationY = f;
            if (displayList != null) {
                displayList.setRotationY(f);
                return;
            }
            return;
        }
        if (i == 128) {
            transformationInfo.mTranslationX = f - this.mView.mLeft;
            if (displayList != null) {
                displayList.setTranslationX(f - this.mView.mLeft);
                return;
            }
            return;
        }
        if (i == 256) {
            transformationInfo.mTranslationY = f - this.mView.mTop;
            if (displayList != null) {
                displayList.setTranslationY(f - this.mView.mTop);
                return;
            }
            return;
        }
        if (i != 512) {
            return;
        }
        transformationInfo.mAlpha = f;
        if (displayList != null) {
            displayList.setAlpha(f);
        }
    }

    private float getValue(int i) {
        float f;
        float f2;
        View.TransformationInfo transformationInfo = this.mView.mTransformationInfo;
        if (i == 1) {
            return transformationInfo.mTranslationX;
        }
        if (i == 2) {
            return transformationInfo.mTranslationY;
        }
        if (i == 4) {
            return transformationInfo.mScaleX;
        }
        if (i == 8) {
            return transformationInfo.mScaleY;
        }
        if (i == 16) {
            return transformationInfo.mRotation;
        }
        if (i == 32) {
            return transformationInfo.mRotationX;
        }
        if (i == 64) {
            return transformationInfo.mRotationY;
        }
        if (i == 128) {
            f = this.mView.mLeft;
            f2 = transformationInfo.mTranslationX;
        } else {
            if (i != 256) {
                if (i != 512) {
                    return 0.0f;
                }
                return transformationInfo.mAlpha;
            }
            f = this.mView.mTop;
            f2 = transformationInfo.mTranslationY;
        }
        return f + f2;
    }

    private class AnimatorEventListener implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
        private AnimatorEventListener() {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            if (ViewPropertyAnimator.this.mAnimatorSetupMap != null) {
                Runnable runnable = (Runnable) ViewPropertyAnimator.this.mAnimatorSetupMap.get(animator);
                if (runnable != null) {
                    runnable.run();
                }
                ViewPropertyAnimator.this.mAnimatorSetupMap.remove(animator);
            }
            if (ViewPropertyAnimator.this.mAnimatorOnStartMap != null) {
                Runnable runnable2 = (Runnable) ViewPropertyAnimator.this.mAnimatorOnStartMap.get(animator);
                if (runnable2 != null) {
                    runnable2.run();
                }
                ViewPropertyAnimator.this.mAnimatorOnStartMap.remove(animator);
            }
            if (ViewPropertyAnimator.this.mListener != null) {
                ViewPropertyAnimator.this.mListener.onAnimationStart(animator);
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
            if (ViewPropertyAnimator.this.mListener != null) {
                ViewPropertyAnimator.this.mListener.onAnimationCancel(animator);
            }
            if (ViewPropertyAnimator.this.mAnimatorOnEndMap != null) {
                ViewPropertyAnimator.this.mAnimatorOnEndMap.remove(animator);
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
            if (ViewPropertyAnimator.this.mListener != null) {
                ViewPropertyAnimator.this.mListener.onAnimationRepeat(animator);
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            ViewPropertyAnimator.this.mView.setHasTransientState(false);
            if (ViewPropertyAnimator.this.mListener != null) {
                ViewPropertyAnimator.this.mListener.onAnimationEnd(animator);
            }
            if (ViewPropertyAnimator.this.mAnimatorOnEndMap != null) {
                Runnable runnable = (Runnable) ViewPropertyAnimator.this.mAnimatorOnEndMap.get(animator);
                if (runnable != null) {
                    runnable.run();
                }
                ViewPropertyAnimator.this.mAnimatorOnEndMap.remove(animator);
            }
            if (ViewPropertyAnimator.this.mAnimatorCleanupMap != null) {
                Runnable runnable2 = (Runnable) ViewPropertyAnimator.this.mAnimatorCleanupMap.get(animator);
                if (runnable2 != null) {
                    runnable2.run();
                }
                ViewPropertyAnimator.this.mAnimatorCleanupMap.remove(animator);
            }
            ViewPropertyAnimator.this.mAnimatorMap.remove(animator);
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            boolean alphaNoInvalidation;
            PropertyBundle propertyBundle = (PropertyBundle) ViewPropertyAnimator.this.mAnimatorMap.get(valueAnimator);
            if (propertyBundle == null) {
                return;
            }
            boolean z = ViewPropertyAnimator.this.mView.mDisplayList != null;
            if (!z) {
                ViewPropertyAnimator.this.mView.invalidateParentCaches();
            }
            float animatedFraction = valueAnimator.getAnimatedFraction();
            int i = propertyBundle.mPropertyMask & 511;
            if (i != 0) {
                ViewPropertyAnimator.this.mView.invalidateViewProperty(false, false);
            }
            ArrayList<NameValuesHolder> arrayList = propertyBundle.mNameValuesHolder;
            if (arrayList != null) {
                int size = arrayList.size();
                alphaNoInvalidation = false;
                for (int i2 = 0; i2 < size; i2++) {
                    NameValuesHolder nameValuesHolder = arrayList.get(i2);
                    float f = nameValuesHolder.mFromValue + (nameValuesHolder.mDeltaValue * animatedFraction);
                    if (nameValuesHolder.mNameConstant == 512) {
                        alphaNoInvalidation = ViewPropertyAnimator.this.mView.setAlphaNoInvalidation(f);
                    } else {
                        ViewPropertyAnimator.this.setValue(nameValuesHolder.mNameConstant, f);
                    }
                }
            } else {
                alphaNoInvalidation = false;
            }
            if (i != 0) {
                ViewPropertyAnimator.this.mView.mTransformationInfo.mMatrixDirty = true;
                if (!z) {
                    ViewPropertyAnimator.this.mView.mPrivateFlags |= 32;
                }
            }
            if (alphaNoInvalidation) {
                ViewPropertyAnimator.this.mView.invalidate(true);
            } else {
                ViewPropertyAnimator.this.mView.invalidateViewProperty(false, false);
            }
            if (ViewPropertyAnimator.this.mUpdateListener != null) {
                ViewPropertyAnimator.this.mUpdateListener.onAnimationUpdate(valueAnimator);
            }
        }
    }
}
