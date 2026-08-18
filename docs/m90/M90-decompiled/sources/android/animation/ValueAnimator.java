package android.animation;

import android.animation.Animator;
import android.os.Looper;
import android.os.Trace;
import android.util.AndroidRuntimeException;
import android.view.Choreographer;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class ValueAnimator extends Animator {
    public static final int INFINITE = -1;
    public static final int RESTART = 1;
    public static final int REVERSE = 2;
    static final int RUNNING = 1;
    static final int SEEKED = 2;
    static final int STOPPED = 0;
    protected static ThreadLocal<AnimationHandler> sAnimationHandler = new ThreadLocal<>();
    private static final TimeInterpolator sDefaultInterpolator = new AccelerateDecelerateInterpolator();
    private static float sDurationScale = 1.0f;
    private long mDelayStartTime;
    private long mPauseTime;
    long mStartTime;
    PropertyValuesHolder[] mValues;
    HashMap<String, PropertyValuesHolder> mValuesMap;
    long mSeekTime = -1;
    private boolean mResumed = false;
    private boolean mPlayingBackwards = false;
    private int mCurrentIteration = 0;
    private float mCurrentFraction = 0.0f;
    private boolean mStartedDelay = false;
    int mPlayingState = 0;
    private boolean mRunning = false;
    private boolean mStarted = false;
    private boolean mStartListenersCalled = false;
    boolean mInitialized = false;
    private long mDuration = (long) (sDurationScale * 300.0f);
    private long mUnscaledDuration = 300;
    private long mStartDelay = 0;
    private long mUnscaledStartDelay = 0;
    private int mRepeatCount = 0;
    private int mRepeatMode = 1;
    private TimeInterpolator mInterpolator = sDefaultInterpolator;
    private ArrayList<AnimatorUpdateListener> mUpdateListeners = null;

    public interface AnimatorUpdateListener {
        void onAnimationUpdate(ValueAnimator valueAnimator);
    }

    String getNameForTrace() {
        return "animator";
    }

    public static void setDurationScale(float f) {
        sDurationScale = f;
    }

    public static float getDurationScale() {
        return sDurationScale;
    }

    public static ValueAnimator ofInt(int... iArr) {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setIntValues(iArr);
        return valueAnimator;
    }

    public static ValueAnimator ofFloat(float... fArr) {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setFloatValues(fArr);
        return valueAnimator;
    }

    public static ValueAnimator ofPropertyValuesHolder(PropertyValuesHolder... propertyValuesHolderArr) {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setValues(propertyValuesHolderArr);
        return valueAnimator;
    }

    public static ValueAnimator ofObject(TypeEvaluator typeEvaluator, Object... objArr) {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setObjectValues(objArr);
        valueAnimator.setEvaluator(typeEvaluator);
        return valueAnimator;
    }

    public void setIntValues(int... iArr) {
        if (iArr == null || iArr.length == 0) {
            return;
        }
        PropertyValuesHolder[] propertyValuesHolderArr = this.mValues;
        if (propertyValuesHolderArr == null || propertyValuesHolderArr.length == 0) {
            setValues(PropertyValuesHolder.ofInt("", iArr));
        } else {
            propertyValuesHolderArr[0].setIntValues(iArr);
        }
        this.mInitialized = false;
    }

    public void setFloatValues(float... fArr) {
        if (fArr == null || fArr.length == 0) {
            return;
        }
        PropertyValuesHolder[] propertyValuesHolderArr = this.mValues;
        if (propertyValuesHolderArr == null || propertyValuesHolderArr.length == 0) {
            setValues(PropertyValuesHolder.ofFloat("", fArr));
        } else {
            propertyValuesHolderArr[0].setFloatValues(fArr);
        }
        this.mInitialized = false;
    }

    public void setObjectValues(Object... objArr) {
        if (objArr == null || objArr.length == 0) {
            return;
        }
        PropertyValuesHolder[] propertyValuesHolderArr = this.mValues;
        if (propertyValuesHolderArr == null || propertyValuesHolderArr.length == 0) {
            setValues(PropertyValuesHolder.ofObject("", (TypeEvaluator) null, objArr));
        } else {
            propertyValuesHolderArr[0].setObjectValues(objArr);
        }
        this.mInitialized = false;
    }

    public void setValues(PropertyValuesHolder... propertyValuesHolderArr) {
        int length = propertyValuesHolderArr.length;
        this.mValues = propertyValuesHolderArr;
        this.mValuesMap = new HashMap<>(length);
        for (PropertyValuesHolder propertyValuesHolder : propertyValuesHolderArr) {
            this.mValuesMap.put(propertyValuesHolder.getPropertyName(), propertyValuesHolder);
        }
        this.mInitialized = false;
    }

    public PropertyValuesHolder[] getValues() {
        return this.mValues;
    }

    void initAnimation() {
        if (this.mInitialized) {
            return;
        }
        int length = this.mValues.length;
        for (int i = 0; i < length; i++) {
            this.mValues[i].init();
        }
        this.mInitialized = true;
    }

    @Override // android.animation.Animator
    public ValueAnimator setDuration(long j) {
        if (j < 0) {
            throw new IllegalArgumentException("Animators cannot have negative duration: " + j);
        }
        this.mUnscaledDuration = j;
        this.mDuration = (long) (j * sDurationScale);
        return this;
    }

    @Override // android.animation.Animator
    public long getDuration() {
        return this.mUnscaledDuration;
    }

    public void setCurrentPlayTime(long j) {
        initAnimation();
        long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
        if (this.mPlayingState != 1) {
            this.mSeekTime = j;
            this.mPlayingState = 2;
        }
        this.mStartTime = jCurrentAnimationTimeMillis - j;
        doAnimationFrame(jCurrentAnimationTimeMillis);
    }

    public long getCurrentPlayTime() {
        if (!this.mInitialized || this.mPlayingState == 0) {
            return 0L;
        }
        return AnimationUtils.currentAnimationTimeMillis() - this.mStartTime;
    }

    protected static class AnimationHandler implements Runnable {
        private boolean mAnimationScheduled;
        protected final ArrayList<ValueAnimator> mAnimations;
        private final Choreographer mChoreographer;
        protected final ArrayList<ValueAnimator> mDelayedAnims;
        private final ArrayList<ValueAnimator> mEndingAnims;
        protected final ArrayList<ValueAnimator> mPendingAnimations;
        private final ArrayList<ValueAnimator> mReadyAnims;
        private final ArrayList<ValueAnimator> mTmpAnimations;

        private AnimationHandler() {
            this.mAnimations = new ArrayList<>();
            this.mTmpAnimations = new ArrayList<>();
            this.mPendingAnimations = new ArrayList<>();
            this.mDelayedAnims = new ArrayList<>();
            this.mEndingAnims = new ArrayList<>();
            this.mReadyAnims = new ArrayList<>();
            this.mChoreographer = Choreographer.getInstance();
        }

        public void start() {
            scheduleAnimation();
        }

        private void doAnimationFrame(long j) {
            int i;
            while (true) {
                i = 0;
                if (this.mPendingAnimations.size() <= 0) {
                    break;
                }
                ArrayList arrayList = (ArrayList) this.mPendingAnimations.clone();
                this.mPendingAnimations.clear();
                int size = arrayList.size();
                while (i < size) {
                    ValueAnimator valueAnimator = (ValueAnimator) arrayList.get(i);
                    if (valueAnimator.mStartDelay == 0) {
                        valueAnimator.startAnimation(this);
                    } else {
                        this.mDelayedAnims.add(valueAnimator);
                    }
                    i++;
                }
            }
            int size2 = this.mDelayedAnims.size();
            for (int i2 = 0; i2 < size2; i2++) {
                ValueAnimator valueAnimator2 = this.mDelayedAnims.get(i2);
                if (valueAnimator2.delayedAnimationFrame(j)) {
                    this.mReadyAnims.add(valueAnimator2);
                }
            }
            int size3 = this.mReadyAnims.size();
            if (size3 > 0) {
                for (int i3 = 0; i3 < size3; i3++) {
                    ValueAnimator valueAnimator3 = this.mReadyAnims.get(i3);
                    valueAnimator3.startAnimation(this);
                    valueAnimator3.mRunning = true;
                    this.mDelayedAnims.remove(valueAnimator3);
                }
                this.mReadyAnims.clear();
            }
            int size4 = this.mAnimations.size();
            for (int i4 = 0; i4 < size4; i4++) {
                this.mTmpAnimations.add(this.mAnimations.get(i4));
            }
            for (int i5 = 0; i5 < size4; i5++) {
                ValueAnimator valueAnimator4 = this.mTmpAnimations.get(i5);
                if (this.mAnimations.contains(valueAnimator4) && valueAnimator4.doAnimationFrame(j)) {
                    this.mEndingAnims.add(valueAnimator4);
                }
            }
            this.mTmpAnimations.clear();
            if (this.mEndingAnims.size() > 0) {
                while (i < this.mEndingAnims.size()) {
                    this.mEndingAnims.get(i).endAnimation(this);
                    i++;
                }
                this.mEndingAnims.clear();
            }
            if (this.mAnimations.isEmpty() && this.mDelayedAnims.isEmpty()) {
                return;
            }
            scheduleAnimation();
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mAnimationScheduled = false;
            doAnimationFrame(this.mChoreographer.getFrameTime());
        }

        private void scheduleAnimation() {
            if (this.mAnimationScheduled) {
                return;
            }
            this.mChoreographer.postCallback(1, this, null);
            this.mAnimationScheduled = true;
        }
    }

    @Override // android.animation.Animator
    public long getStartDelay() {
        return this.mUnscaledStartDelay;
    }

    @Override // android.animation.Animator
    public void setStartDelay(long j) {
        this.mStartDelay = (long) (j * sDurationScale);
        this.mUnscaledStartDelay = j;
    }

    public static long getFrameDelay() {
        return Choreographer.getFrameDelay();
    }

    public static void setFrameDelay(long j) {
        Choreographer.setFrameDelay(j);
    }

    public Object getAnimatedValue() {
        PropertyValuesHolder[] propertyValuesHolderArr = this.mValues;
        if (propertyValuesHolderArr == null || propertyValuesHolderArr.length <= 0) {
            return null;
        }
        return propertyValuesHolderArr[0].getAnimatedValue();
    }

    public Object getAnimatedValue(String str) {
        PropertyValuesHolder propertyValuesHolder = this.mValuesMap.get(str);
        if (propertyValuesHolder != null) {
            return propertyValuesHolder.getAnimatedValue();
        }
        return null;
    }

    public void setRepeatCount(int i) {
        this.mRepeatCount = i;
    }

    public int getRepeatCount() {
        return this.mRepeatCount;
    }

    public void setRepeatMode(int i) {
        this.mRepeatMode = i;
    }

    public int getRepeatMode() {
        return this.mRepeatMode;
    }

    public void addUpdateListener(AnimatorUpdateListener animatorUpdateListener) {
        if (this.mUpdateListeners == null) {
            this.mUpdateListeners = new ArrayList<>();
        }
        this.mUpdateListeners.add(animatorUpdateListener);
    }

    public void removeAllUpdateListeners() {
        ArrayList<AnimatorUpdateListener> arrayList = this.mUpdateListeners;
        if (arrayList == null) {
            return;
        }
        arrayList.clear();
        this.mUpdateListeners = null;
    }

    public void removeUpdateListener(AnimatorUpdateListener animatorUpdateListener) {
        ArrayList<AnimatorUpdateListener> arrayList = this.mUpdateListeners;
        if (arrayList == null) {
            return;
        }
        arrayList.remove(animatorUpdateListener);
        if (this.mUpdateListeners.size() == 0) {
            this.mUpdateListeners = null;
        }
    }

    @Override // android.animation.Animator
    public void setInterpolator(TimeInterpolator timeInterpolator) {
        if (timeInterpolator != null) {
            this.mInterpolator = timeInterpolator;
        } else {
            this.mInterpolator = new LinearInterpolator();
        }
    }

    @Override // android.animation.Animator
    public TimeInterpolator getInterpolator() {
        return this.mInterpolator;
    }

    public void setEvaluator(TypeEvaluator typeEvaluator) {
        PropertyValuesHolder[] propertyValuesHolderArr;
        if (typeEvaluator == null || (propertyValuesHolderArr = this.mValues) == null || propertyValuesHolderArr.length <= 0) {
            return;
        }
        propertyValuesHolderArr[0].setEvaluator(typeEvaluator);
    }

    private void notifyStartListeners() {
        if (this.mListeners != null && !this.mStartListenersCalled) {
            ArrayList arrayList = (ArrayList) this.mListeners.clone();
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                ((Animator.AnimatorListener) arrayList.get(i)).onAnimationStart(this);
            }
        }
        this.mStartListenersCalled = true;
    }

    private void start(boolean z) {
        if (Looper.myLooper() == null) {
            throw new AndroidRuntimeException("Animators may only be run on Looper threads");
        }
        this.mPlayingBackwards = z;
        this.mCurrentIteration = 0;
        this.mPlayingState = 0;
        this.mStarted = true;
        this.mStartedDelay = false;
        this.mPaused = false;
        AnimationHandler orCreateAnimationHandler = getOrCreateAnimationHandler();
        orCreateAnimationHandler.mPendingAnimations.add(this);
        if (this.mStartDelay == 0) {
            setCurrentPlayTime(0L);
            this.mPlayingState = 0;
            this.mRunning = true;
            notifyStartListeners();
        }
        orCreateAnimationHandler.start();
    }

    @Override // android.animation.Animator
    public void start() {
        start(false);
    }

    @Override // android.animation.Animator
    public void cancel() {
        AnimationHandler orCreateAnimationHandler = getOrCreateAnimationHandler();
        if (this.mPlayingState != 0 || orCreateAnimationHandler.mPendingAnimations.contains(this) || orCreateAnimationHandler.mDelayedAnims.contains(this)) {
            if ((this.mStarted || this.mRunning) && this.mListeners != null) {
                if (!this.mRunning) {
                    notifyStartListeners();
                }
                Iterator it = ((ArrayList) this.mListeners.clone()).iterator();
                while (it.hasNext()) {
                    ((Animator.AnimatorListener) it.next()).onAnimationCancel(this);
                }
            }
            endAnimation(orCreateAnimationHandler);
        }
    }

    @Override // android.animation.Animator
    public void end() {
        AnimationHandler orCreateAnimationHandler = getOrCreateAnimationHandler();
        if (!orCreateAnimationHandler.mAnimations.contains(this) && !orCreateAnimationHandler.mPendingAnimations.contains(this)) {
            this.mStartedDelay = false;
            startAnimation(orCreateAnimationHandler);
            this.mStarted = true;
        } else if (!this.mInitialized) {
            initAnimation();
        }
        animateValue(this.mPlayingBackwards ? 0.0f : 1.0f);
        endAnimation(orCreateAnimationHandler);
    }

    @Override // android.animation.Animator
    public void resume() {
        if (this.mPaused) {
            this.mResumed = true;
        }
        super.resume();
    }

    @Override // android.animation.Animator
    public void pause() {
        boolean z = this.mPaused;
        super.pause();
        if (z || !this.mPaused) {
            return;
        }
        this.mPauseTime = -1L;
        this.mResumed = false;
    }

    @Override // android.animation.Animator
    public boolean isRunning() {
        return this.mPlayingState == 1 || this.mRunning;
    }

    @Override // android.animation.Animator
    public boolean isStarted() {
        return this.mStarted;
    }

    public void reverse() {
        this.mPlayingBackwards = !this.mPlayingBackwards;
        if (this.mPlayingState == 1) {
            long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
            this.mStartTime = jCurrentAnimationTimeMillis - (this.mDuration - (jCurrentAnimationTimeMillis - this.mStartTime));
        } else if (this.mStarted) {
            end();
        } else {
            start(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void endAnimation(AnimationHandler animationHandler) {
        animationHandler.mAnimations.remove(this);
        animationHandler.mPendingAnimations.remove(this);
        animationHandler.mDelayedAnims.remove(this);
        this.mPlayingState = 0;
        this.mPaused = false;
        if ((this.mStarted || this.mRunning) && this.mListeners != null) {
            if (!this.mRunning) {
                notifyStartListeners();
            }
            ArrayList arrayList = (ArrayList) this.mListeners.clone();
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                ((Animator.AnimatorListener) arrayList.get(i)).onAnimationEnd(this);
            }
        }
        this.mRunning = false;
        this.mStarted = false;
        this.mStartListenersCalled = false;
        this.mPlayingBackwards = false;
        if (Trace.isTagEnabled(8L)) {
            Trace.asyncTraceEnd(8L, getNameForTrace(), System.identityHashCode(this));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startAnimation(AnimationHandler animationHandler) {
        if (Trace.isTagEnabled(8L)) {
            Trace.asyncTraceBegin(8L, getNameForTrace(), System.identityHashCode(this));
        }
        initAnimation();
        animationHandler.mAnimations.add(this);
        if (this.mStartDelay <= 0 || this.mListeners == null) {
            return;
        }
        notifyStartListeners();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean delayedAnimationFrame(long j) {
        if (!this.mStartedDelay) {
            this.mStartedDelay = true;
            this.mDelayStartTime = j;
        } else {
            if (this.mPaused) {
                if (this.mPauseTime < 0) {
                    this.mPauseTime = j;
                }
                return false;
            }
            if (this.mResumed) {
                this.mResumed = false;
                long j2 = this.mPauseTime;
                if (j2 > 0) {
                    this.mDelayStartTime += j - j2;
                }
            }
            long j3 = j - this.mDelayStartTime;
            long j4 = this.mStartDelay;
            if (j3 > j4) {
                this.mStartTime = j - (j3 - j4);
                this.mPlayingState = 1;
                return true;
            }
        }
        return false;
    }

    boolean animationFrame(long j) {
        int i = this.mPlayingState;
        boolean z = false;
        if (i == 1 || i == 2) {
            long j2 = this.mDuration;
            float fMin = j2 > 0 ? (j - this.mStartTime) / j2 : 1.0f;
            if (fMin >= 1.0f) {
                int i2 = this.mCurrentIteration;
                int i3 = this.mRepeatCount;
                if (i2 < i3 || i3 == -1) {
                    if (this.mListeners != null) {
                        int size = this.mListeners.size();
                        for (int i4 = 0; i4 < size; i4++) {
                            this.mListeners.get(i4).onAnimationRepeat(this);
                        }
                    }
                    if (this.mRepeatMode == 2) {
                        this.mPlayingBackwards = !this.mPlayingBackwards;
                    }
                    this.mCurrentIteration += (int) fMin;
                    fMin %= 1.0f;
                    this.mStartTime += this.mDuration;
                } else {
                    fMin = Math.min(fMin, 1.0f);
                    z = true;
                }
            }
            if (this.mPlayingBackwards) {
                fMin = 1.0f - fMin;
            }
            animateValue(fMin);
        }
        return z;
    }

    final boolean doAnimationFrame(long j) {
        if (this.mPlayingState == 0) {
            this.mPlayingState = 1;
            long j2 = this.mSeekTime;
            if (j2 < 0) {
                this.mStartTime = j;
            } else {
                this.mStartTime = j - j2;
                this.mSeekTime = -1L;
            }
        }
        if (this.mPaused) {
            if (this.mPauseTime < 0) {
                this.mPauseTime = j;
            }
            return false;
        }
        if (this.mResumed) {
            this.mResumed = false;
            long j3 = this.mPauseTime;
            if (j3 > 0) {
                this.mStartTime += j - j3;
            }
        }
        return animationFrame(Math.max(j, this.mStartTime));
    }

    public float getAnimatedFraction() {
        return this.mCurrentFraction;
    }

    void animateValue(float f) {
        float interpolation = this.mInterpolator.getInterpolation(f);
        this.mCurrentFraction = interpolation;
        int length = this.mValues.length;
        for (int i = 0; i < length; i++) {
            this.mValues[i].calculateValue(interpolation);
        }
        ArrayList<AnimatorUpdateListener> arrayList = this.mUpdateListeners;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i2 = 0; i2 < size; i2++) {
                this.mUpdateListeners.get(i2).onAnimationUpdate(this);
            }
        }
    }

    @Override // android.animation.Animator
    /* JADX INFO: renamed from: clone */
    public ValueAnimator mo0clone() {
        ValueAnimator valueAnimator = (ValueAnimator) super.mo0clone();
        ArrayList<AnimatorUpdateListener> arrayList = this.mUpdateListeners;
        if (arrayList != null) {
            valueAnimator.mUpdateListeners = new ArrayList<>();
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                valueAnimator.mUpdateListeners.add(arrayList.get(i));
            }
        }
        valueAnimator.mSeekTime = -1L;
        valueAnimator.mPlayingBackwards = false;
        valueAnimator.mCurrentIteration = 0;
        valueAnimator.mInitialized = false;
        valueAnimator.mPlayingState = 0;
        valueAnimator.mStartedDelay = false;
        PropertyValuesHolder[] propertyValuesHolderArr = this.mValues;
        if (propertyValuesHolderArr != null) {
            int length = propertyValuesHolderArr.length;
            valueAnimator.mValues = new PropertyValuesHolder[length];
            valueAnimator.mValuesMap = new HashMap<>(length);
            for (int i2 = 0; i2 < length; i2++) {
                PropertyValuesHolder propertyValuesHolderMo4clone = propertyValuesHolderArr[i2].mo4clone();
                valueAnimator.mValues[i2] = propertyValuesHolderMo4clone;
                valueAnimator.mValuesMap.put(propertyValuesHolderMo4clone.getPropertyName(), propertyValuesHolderMo4clone);
            }
        }
        return valueAnimator;
    }

    public static int getCurrentAnimationsCount() {
        AnimationHandler animationHandler = sAnimationHandler.get();
        if (animationHandler != null) {
            return animationHandler.mAnimations.size();
        }
        return 0;
    }

    public static void clearAllAnimations() {
        AnimationHandler animationHandler = sAnimationHandler.get();
        if (animationHandler != null) {
            animationHandler.mAnimations.clear();
            animationHandler.mPendingAnimations.clear();
            animationHandler.mDelayedAnims.clear();
        }
    }

    private static AnimationHandler getOrCreateAnimationHandler() {
        AnimationHandler animationHandler = sAnimationHandler.get();
        if (animationHandler != null) {
            return animationHandler;
        }
        AnimationHandler animationHandler2 = new AnimationHandler();
        sAnimationHandler.set(animationHandler2);
        return animationHandler2;
    }

    public String toString() {
        String str = "ValueAnimator@" + Integer.toHexString(hashCode());
        if (this.mValues != null) {
            for (int i = 0; i < this.mValues.length; i++) {
                str = str + "\n    " + this.mValues[i].toString();
            }
        }
        return str;
    }
}
