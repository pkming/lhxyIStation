package android.animation;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class LayoutTransition {
    private static TimeInterpolator ACCEL_DECEL_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    public static final int APPEARING = 2;
    public static final int CHANGE_APPEARING = 0;
    public static final int CHANGE_DISAPPEARING = 1;
    public static final int CHANGING = 4;
    private static TimeInterpolator DECEL_INTERPOLATOR = null;
    private static long DEFAULT_DURATION = 300;
    public static final int DISAPPEARING = 3;
    private static final int FLAG_APPEARING = 1;
    private static final int FLAG_CHANGE_APPEARING = 4;
    private static final int FLAG_CHANGE_DISAPPEARING = 8;
    private static final int FLAG_CHANGING = 16;
    private static final int FLAG_DISAPPEARING = 2;
    private static ObjectAnimator defaultChange;
    private static ObjectAnimator defaultChangeIn;
    private static ObjectAnimator defaultChangeOut;
    private static ObjectAnimator defaultFadeIn;
    private static ObjectAnimator defaultFadeOut;
    private static TimeInterpolator sAppearingInterpolator;
    private static TimeInterpolator sChangingAppearingInterpolator;
    private static TimeInterpolator sChangingDisappearingInterpolator;
    private static TimeInterpolator sChangingInterpolator;
    private static TimeInterpolator sDisappearingInterpolator;
    private final LinkedHashMap<View, Animator> currentAppearingAnimations;
    private final LinkedHashMap<View, Animator> currentChangingAnimations;
    private final LinkedHashMap<View, Animator> currentDisappearingAnimations;
    private final HashMap<View, View.OnLayoutChangeListener> layoutChangeListenerMap;
    private boolean mAnimateParentHierarchy;
    private Animator mAppearingAnim;
    private long mAppearingDelay;
    private long mAppearingDuration;
    private TimeInterpolator mAppearingInterpolator;
    private Animator mChangingAnim;
    private Animator mChangingAppearingAnim;
    private long mChangingAppearingDelay;
    private long mChangingAppearingDuration;
    private TimeInterpolator mChangingAppearingInterpolator;
    private long mChangingAppearingStagger;
    private long mChangingDelay;
    private Animator mChangingDisappearingAnim;
    private long mChangingDisappearingDelay;
    private long mChangingDisappearingDuration;
    private TimeInterpolator mChangingDisappearingInterpolator;
    private long mChangingDisappearingStagger;
    private long mChangingDuration;
    private TimeInterpolator mChangingInterpolator;
    private long mChangingStagger;
    private Animator mDisappearingAnim;
    private long mDisappearingDelay;
    private long mDisappearingDuration;
    private TimeInterpolator mDisappearingInterpolator;
    private ArrayList<TransitionListener> mListeners;
    private int mTransitionTypes;
    private final HashMap<View, Animator> pendingAnimations;
    private long staggerDelay;

    public interface TransitionListener {
        void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i);

        void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i);
    }

    static /* synthetic */ long access$314(LayoutTransition layoutTransition, long j) {
        long j2 = layoutTransition.staggerDelay + j;
        layoutTransition.staggerDelay = j2;
        return j2;
    }

    static {
        DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
        DECEL_INTERPOLATOR = decelerateInterpolator;
        TimeInterpolator timeInterpolator = ACCEL_DECEL_INTERPOLATOR;
        sAppearingInterpolator = timeInterpolator;
        sDisappearingInterpolator = timeInterpolator;
        sChangingAppearingInterpolator = decelerateInterpolator;
        sChangingDisappearingInterpolator = decelerateInterpolator;
        sChangingInterpolator = decelerateInterpolator;
    }

    public LayoutTransition() {
        this.mDisappearingAnim = null;
        this.mAppearingAnim = null;
        this.mChangingAppearingAnim = null;
        this.mChangingDisappearingAnim = null;
        this.mChangingAnim = null;
        long j = DEFAULT_DURATION;
        this.mChangingAppearingDuration = j;
        this.mChangingDisappearingDuration = j;
        this.mChangingDuration = j;
        this.mAppearingDuration = j;
        this.mDisappearingDuration = j;
        this.mAppearingDelay = j;
        this.mDisappearingDelay = 0L;
        this.mChangingAppearingDelay = 0L;
        this.mChangingDisappearingDelay = j;
        this.mChangingDelay = 0L;
        this.mChangingAppearingStagger = 0L;
        this.mChangingDisappearingStagger = 0L;
        this.mChangingStagger = 0L;
        this.mAppearingInterpolator = sAppearingInterpolator;
        this.mDisappearingInterpolator = sDisappearingInterpolator;
        this.mChangingAppearingInterpolator = sChangingAppearingInterpolator;
        this.mChangingDisappearingInterpolator = sChangingDisappearingInterpolator;
        this.mChangingInterpolator = sChangingInterpolator;
        this.pendingAnimations = new HashMap<>();
        this.currentChangingAnimations = new LinkedHashMap<>();
        this.currentAppearingAnimations = new LinkedHashMap<>();
        this.currentDisappearingAnimations = new LinkedHashMap<>();
        this.layoutChangeListenerMap = new HashMap<>();
        this.mTransitionTypes = 15;
        this.mAnimateParentHierarchy = true;
        if (defaultChangeIn == null) {
            ObjectAnimator objectAnimatorOfPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder((Object) null, PropertyValuesHolder.ofInt("left", 0, 1), PropertyValuesHolder.ofInt("top", 0, 1), PropertyValuesHolder.ofInt("right", 0, 1), PropertyValuesHolder.ofInt("bottom", 0, 1), PropertyValuesHolder.ofInt("scrollX", 0, 1), PropertyValuesHolder.ofInt("scrollY", 0, 1));
            defaultChangeIn = objectAnimatorOfPropertyValuesHolder;
            objectAnimatorOfPropertyValuesHolder.setDuration(DEFAULT_DURATION);
            defaultChangeIn.setStartDelay(this.mChangingAppearingDelay);
            defaultChangeIn.setInterpolator(this.mChangingAppearingInterpolator);
            ObjectAnimator objectAnimatorMo0clone = defaultChangeIn.mo0clone();
            defaultChangeOut = objectAnimatorMo0clone;
            objectAnimatorMo0clone.setStartDelay(this.mChangingDisappearingDelay);
            defaultChangeOut.setInterpolator(this.mChangingDisappearingInterpolator);
            ObjectAnimator objectAnimatorMo0clone2 = defaultChangeIn.mo0clone();
            defaultChange = objectAnimatorMo0clone2;
            objectAnimatorMo0clone2.setStartDelay(this.mChangingDelay);
            defaultChange.setInterpolator(this.mChangingInterpolator);
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat((Object) null, "alpha", 0.0f, 1.0f);
            defaultFadeIn = objectAnimatorOfFloat;
            objectAnimatorOfFloat.setDuration(DEFAULT_DURATION);
            defaultFadeIn.setStartDelay(this.mAppearingDelay);
            defaultFadeIn.setInterpolator(this.mAppearingInterpolator);
            ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat((Object) null, "alpha", 1.0f, 0.0f);
            defaultFadeOut = objectAnimatorOfFloat2;
            objectAnimatorOfFloat2.setDuration(DEFAULT_DURATION);
            defaultFadeOut.setStartDelay(this.mDisappearingDelay);
            defaultFadeOut.setInterpolator(this.mDisappearingInterpolator);
        }
        this.mChangingAppearingAnim = defaultChangeIn;
        this.mChangingDisappearingAnim = defaultChangeOut;
        this.mChangingAnim = defaultChange;
        this.mAppearingAnim = defaultFadeIn;
        this.mDisappearingAnim = defaultFadeOut;
    }

    public void setDuration(long j) {
        this.mChangingAppearingDuration = j;
        this.mChangingDisappearingDuration = j;
        this.mChangingDuration = j;
        this.mAppearingDuration = j;
        this.mDisappearingDuration = j;
    }

    public void enableTransitionType(int i) {
        if (i == 0) {
            this.mTransitionTypes |= 4;
            return;
        }
        if (i == 1) {
            this.mTransitionTypes |= 8;
            return;
        }
        if (i == 2) {
            this.mTransitionTypes |= 1;
        } else if (i == 3) {
            this.mTransitionTypes |= 2;
        } else {
            if (i != 4) {
                return;
            }
            this.mTransitionTypes |= 16;
        }
    }

    public void disableTransitionType(int i) {
        if (i == 0) {
            this.mTransitionTypes &= -5;
            return;
        }
        if (i == 1) {
            this.mTransitionTypes &= -9;
            return;
        }
        if (i == 2) {
            this.mTransitionTypes &= -2;
        } else if (i == 3) {
            this.mTransitionTypes &= -3;
        } else {
            if (i != 4) {
                return;
            }
            this.mTransitionTypes &= -17;
        }
    }

    public boolean isTransitionTypeEnabled(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? i == 4 && (this.mTransitionTypes & 16) == 16 : (this.mTransitionTypes & 2) == 2 : (this.mTransitionTypes & 1) == 1 : (this.mTransitionTypes & 8) == 8 : (this.mTransitionTypes & 4) == 4;
    }

    public void setStartDelay(int i, long j) {
        if (i == 0) {
            this.mChangingAppearingDelay = j;
            return;
        }
        if (i == 1) {
            this.mChangingDisappearingDelay = j;
            return;
        }
        if (i == 2) {
            this.mAppearingDelay = j;
        } else if (i == 3) {
            this.mDisappearingDelay = j;
        } else {
            if (i != 4) {
                return;
            }
            this.mChangingDelay = j;
        }
    }

    public long getStartDelay(int i) {
        if (i == 0) {
            return this.mChangingAppearingDelay;
        }
        if (i == 1) {
            return this.mChangingDisappearingDelay;
        }
        if (i == 2) {
            return this.mAppearingDelay;
        }
        if (i == 3) {
            return this.mDisappearingDelay;
        }
        if (i != 4) {
            return 0L;
        }
        return this.mChangingDelay;
    }

    public void setDuration(int i, long j) {
        if (i == 0) {
            this.mChangingAppearingDuration = j;
            return;
        }
        if (i == 1) {
            this.mChangingDisappearingDuration = j;
            return;
        }
        if (i == 2) {
            this.mAppearingDuration = j;
        } else if (i == 3) {
            this.mDisappearingDuration = j;
        } else {
            if (i != 4) {
                return;
            }
            this.mChangingDuration = j;
        }
    }

    public long getDuration(int i) {
        if (i == 0) {
            return this.mChangingAppearingDuration;
        }
        if (i == 1) {
            return this.mChangingDisappearingDuration;
        }
        if (i == 2) {
            return this.mAppearingDuration;
        }
        if (i == 3) {
            return this.mDisappearingDuration;
        }
        if (i != 4) {
            return 0L;
        }
        return this.mChangingDuration;
    }

    public void setStagger(int i, long j) {
        if (i == 0) {
            this.mChangingAppearingStagger = j;
        } else if (i == 1) {
            this.mChangingDisappearingStagger = j;
        } else {
            if (i != 4) {
                return;
            }
            this.mChangingStagger = j;
        }
    }

    public long getStagger(int i) {
        if (i == 0) {
            return this.mChangingAppearingStagger;
        }
        if (i == 1) {
            return this.mChangingDisappearingStagger;
        }
        if (i != 4) {
            return 0L;
        }
        return this.mChangingStagger;
    }

    public void setInterpolator(int i, TimeInterpolator timeInterpolator) {
        if (i == 0) {
            this.mChangingAppearingInterpolator = timeInterpolator;
            return;
        }
        if (i == 1) {
            this.mChangingDisappearingInterpolator = timeInterpolator;
            return;
        }
        if (i == 2) {
            this.mAppearingInterpolator = timeInterpolator;
        } else if (i == 3) {
            this.mDisappearingInterpolator = timeInterpolator;
        } else {
            if (i != 4) {
                return;
            }
            this.mChangingInterpolator = timeInterpolator;
        }
    }

    public TimeInterpolator getInterpolator(int i) {
        if (i == 0) {
            return this.mChangingAppearingInterpolator;
        }
        if (i == 1) {
            return this.mChangingDisappearingInterpolator;
        }
        if (i == 2) {
            return this.mAppearingInterpolator;
        }
        if (i == 3) {
            return this.mDisappearingInterpolator;
        }
        if (i != 4) {
            return null;
        }
        return this.mChangingInterpolator;
    }

    public void setAnimator(int i, Animator animator) {
        if (i == 0) {
            this.mChangingAppearingAnim = animator;
            return;
        }
        if (i == 1) {
            this.mChangingDisappearingAnim = animator;
            return;
        }
        if (i == 2) {
            this.mAppearingAnim = animator;
        } else if (i == 3) {
            this.mDisappearingAnim = animator;
        } else {
            if (i != 4) {
                return;
            }
            this.mChangingAnim = animator;
        }
    }

    public Animator getAnimator(int i) {
        if (i == 0) {
            return this.mChangingAppearingAnim;
        }
        if (i == 1) {
            return this.mChangingDisappearingAnim;
        }
        if (i == 2) {
            return this.mAppearingAnim;
        }
        if (i == 3) {
            return this.mDisappearingAnim;
        }
        if (i != 4) {
            return null;
        }
        return this.mChangingAnim;
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x002f A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0030  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void runChangeTransition(final android.view.ViewGroup r20, android.view.View r21, int r22) {
        /*
            r19 = this;
            r7 = r19
            r8 = r20
            r9 = r22
            r0 = 2
            r1 = 0
            r10 = 0
            if (r9 == r0) goto L24
            r0 = 3
            if (r9 == r0) goto L1d
            r0 = 4
            if (r9 == r0) goto L16
            r12 = r1
            r11 = r10
            r14 = r11
            goto L2d
        L16:
            android.animation.Animator r0 = r7.mChangingAnim
            long r3 = r7.mChangingDuration
            android.animation.ObjectAnimator r5 = android.animation.LayoutTransition.defaultChange
            goto L2a
        L1d:
            android.animation.Animator r0 = r7.mChangingDisappearingAnim
            long r3 = r7.mChangingDisappearingDuration
            android.animation.ObjectAnimator r5 = android.animation.LayoutTransition.defaultChangeOut
            goto L2a
        L24:
            android.animation.Animator r0 = r7.mChangingAppearingAnim
            long r3 = r7.mChangingAppearingDuration
            android.animation.ObjectAnimator r5 = android.animation.LayoutTransition.defaultChangeIn
        L2a:
            r11 = r0
            r12 = r3
            r14 = r5
        L2d:
            if (r11 != 0) goto L30
            return
        L30:
            r7.staggerDelay = r1
            android.view.ViewTreeObserver r15 = r20.getViewTreeObserver()
            boolean r0 = r15.isAlive()
            if (r0 != 0) goto L3d
            return
        L3d:
            int r6 = r20.getChildCount()
            r0 = 0
            r4 = r0
        L43:
            if (r4 >= r6) goto L6a
            android.view.View r5 = r8.getChildAt(r4)
            r3 = r21
            if (r5 == r3) goto L61
            r0 = r19
            r1 = r20
            r2 = r22
            r3 = r11
            r16 = r4
            r17 = r5
            r4 = r12
            r18 = r6
            r6 = r17
            r0.setupChangeAnimation(r1, r2, r3, r4, r6)
            goto L65
        L61:
            r16 = r4
            r18 = r6
        L65:
            int r4 = r16 + 1
            r6 = r18
            goto L43
        L6a:
            boolean r0 = r7.mAnimateParentHierarchy
            if (r0 == 0) goto L8a
            r6 = r8
        L6f:
            if (r6 == 0) goto L8a
            android.view.ViewParent r0 = r6.getParent()
            boolean r1 = r0 instanceof android.view.ViewGroup
            if (r1 == 0) goto L88
            r11 = r0
            android.view.ViewGroup r11 = (android.view.ViewGroup) r11
            r0 = r19
            r1 = r11
            r2 = r22
            r3 = r14
            r4 = r12
            r0.setupChangeAnimation(r1, r2, r3, r4, r6)
            r6 = r11
            goto L6f
        L88:
            r6 = r10
            goto L6f
        L8a:
            android.animation.LayoutTransition$1 r0 = new android.animation.LayoutTransition$1
            r0.<init>()
            r15.addOnPreDrawListener(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.animation.LayoutTransition.runChangeTransition(android.view.ViewGroup, android.view.View, int):void");
    }

    public void setAnimateParentHierarchy(boolean z) {
        this.mAnimateParentHierarchy = z;
    }

    private void setupChangeAnimation(final ViewGroup viewGroup, final int i, Animator animator, final long j, final View view) {
        if (this.layoutChangeListenerMap.get(view) != null) {
            return;
        }
        if (view.getWidth() == 0 && view.getHeight() == 0) {
            return;
        }
        final Animator animatorMo0clone = animator.mo0clone();
        animatorMo0clone.setTarget(view);
        animatorMo0clone.setupStartValues();
        Animator animator2 = this.pendingAnimations.get(view);
        if (animator2 != null) {
            animator2.cancel();
            this.pendingAnimations.remove(view);
        }
        this.pendingAnimations.put(view, animatorMo0clone);
        ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(100 + j);
        duration.addListener(new AnimatorListenerAdapter() { // from class: android.animation.LayoutTransition.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator3) {
                LayoutTransition.this.pendingAnimations.remove(view);
            }
        });
        duration.start();
        final View.OnLayoutChangeListener onLayoutChangeListener = new View.OnLayoutChangeListener() { // from class: android.animation.LayoutTransition.3
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View view2, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
                animatorMo0clone.setupEndValues();
                Animator animator3 = animatorMo0clone;
                if (animator3 instanceof ValueAnimator) {
                    boolean z = false;
                    for (PropertyValuesHolder propertyValuesHolder : ((ValueAnimator) animator3).getValues()) {
                        KeyframeSet keyframeSet = propertyValuesHolder.mKeyframeSet;
                        if (keyframeSet.mFirstKeyframe == null || keyframeSet.mLastKeyframe == null || !keyframeSet.mFirstKeyframe.getValue().equals(keyframeSet.mLastKeyframe.getValue())) {
                            z = true;
                        }
                    }
                    if (!z) {
                        return;
                    }
                }
                long j2 = 0;
                int i10 = i;
                if (i10 == 2) {
                    j2 = LayoutTransition.this.mChangingAppearingDelay + LayoutTransition.this.staggerDelay;
                    LayoutTransition layoutTransition = LayoutTransition.this;
                    LayoutTransition.access$314(layoutTransition, layoutTransition.mChangingAppearingStagger);
                    if (LayoutTransition.this.mChangingAppearingInterpolator != LayoutTransition.sChangingAppearingInterpolator) {
                        animatorMo0clone.setInterpolator(LayoutTransition.this.mChangingAppearingInterpolator);
                    }
                } else if (i10 == 3) {
                    j2 = LayoutTransition.this.mChangingDisappearingDelay + LayoutTransition.this.staggerDelay;
                    LayoutTransition layoutTransition2 = LayoutTransition.this;
                    LayoutTransition.access$314(layoutTransition2, layoutTransition2.mChangingDisappearingStagger);
                    if (LayoutTransition.this.mChangingDisappearingInterpolator != LayoutTransition.sChangingDisappearingInterpolator) {
                        animatorMo0clone.setInterpolator(LayoutTransition.this.mChangingDisappearingInterpolator);
                    }
                } else if (i10 == 4) {
                    j2 = LayoutTransition.this.mChangingDelay + LayoutTransition.this.staggerDelay;
                    LayoutTransition layoutTransition3 = LayoutTransition.this;
                    LayoutTransition.access$314(layoutTransition3, layoutTransition3.mChangingStagger);
                    if (LayoutTransition.this.mChangingInterpolator != LayoutTransition.sChangingInterpolator) {
                        animatorMo0clone.setInterpolator(LayoutTransition.this.mChangingInterpolator);
                    }
                }
                animatorMo0clone.setStartDelay(j2);
                animatorMo0clone.setDuration(j);
                Animator animator4 = (Animator) LayoutTransition.this.currentChangingAnimations.get(view);
                if (animator4 != null) {
                    animator4.cancel();
                }
                if (((Animator) LayoutTransition.this.pendingAnimations.get(view)) != null) {
                    LayoutTransition.this.pendingAnimations.remove(view);
                }
                LayoutTransition.this.currentChangingAnimations.put(view, animatorMo0clone);
                viewGroup.requestTransitionStart(LayoutTransition.this);
                view.removeOnLayoutChangeListener(this);
                LayoutTransition.this.layoutChangeListenerMap.remove(view);
            }
        };
        animatorMo0clone.addListener(new AnimatorListenerAdapter() { // from class: android.animation.LayoutTransition.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator3) {
                if (LayoutTransition.this.hasListeners()) {
                    for (TransitionListener transitionListener : (ArrayList) LayoutTransition.this.mListeners.clone()) {
                        LayoutTransition layoutTransition = LayoutTransition.this;
                        ViewGroup viewGroup2 = viewGroup;
                        View view2 = view;
                        int i2 = i;
                        transitionListener.startTransition(layoutTransition, viewGroup2, view2, i2 == 2 ? 0 : i2 == 3 ? 1 : 4);
                    }
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator3) {
                view.removeOnLayoutChangeListener(onLayoutChangeListener);
                LayoutTransition.this.layoutChangeListenerMap.remove(view);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator3) {
                LayoutTransition.this.currentChangingAnimations.remove(view);
                if (LayoutTransition.this.hasListeners()) {
                    for (TransitionListener transitionListener : (ArrayList) LayoutTransition.this.mListeners.clone()) {
                        LayoutTransition layoutTransition = LayoutTransition.this;
                        ViewGroup viewGroup2 = viewGroup;
                        View view2 = view;
                        int i2 = i;
                        transitionListener.endTransition(layoutTransition, viewGroup2, view2, i2 == 2 ? 0 : i2 == 3 ? 1 : 4);
                    }
                }
            }
        });
        view.addOnLayoutChangeListener(onLayoutChangeListener);
        this.layoutChangeListenerMap.put(view, onLayoutChangeListener);
    }

    public void startChangingAnimations() {
        for (Animator animator : ((LinkedHashMap) this.currentChangingAnimations.clone()).values()) {
            if (animator instanceof ObjectAnimator) {
                ((ObjectAnimator) animator).setCurrentPlayTime(0L);
            }
            animator.start();
        }
    }

    public void endChangingAnimations() {
        for (Animator animator : ((LinkedHashMap) this.currentChangingAnimations.clone()).values()) {
            animator.start();
            animator.end();
        }
        this.currentChangingAnimations.clear();
    }

    public boolean isChangingLayout() {
        return this.currentChangingAnimations.size() > 0;
    }

    public boolean isRunning() {
        return this.currentChangingAnimations.size() > 0 || this.currentAppearingAnimations.size() > 0 || this.currentDisappearingAnimations.size() > 0;
    }

    public void cancel() {
        if (this.currentChangingAnimations.size() > 0) {
            Iterator it = ((LinkedHashMap) this.currentChangingAnimations.clone()).values().iterator();
            while (it.hasNext()) {
                ((Animator) it.next()).cancel();
            }
            this.currentChangingAnimations.clear();
        }
        if (this.currentAppearingAnimations.size() > 0) {
            Iterator it2 = ((LinkedHashMap) this.currentAppearingAnimations.clone()).values().iterator();
            while (it2.hasNext()) {
                ((Animator) it2.next()).end();
            }
            this.currentAppearingAnimations.clear();
        }
        if (this.currentDisappearingAnimations.size() > 0) {
            Iterator it3 = ((LinkedHashMap) this.currentDisappearingAnimations.clone()).values().iterator();
            while (it3.hasNext()) {
                ((Animator) it3.next()).end();
            }
            this.currentDisappearingAnimations.clear();
        }
    }

    public void cancel(int i) {
        if (i != 0 && i != 1) {
            if (i == 2) {
                if (this.currentAppearingAnimations.size() > 0) {
                    Iterator it = ((LinkedHashMap) this.currentAppearingAnimations.clone()).values().iterator();
                    while (it.hasNext()) {
                        ((Animator) it.next()).end();
                    }
                    this.currentAppearingAnimations.clear();
                    return;
                }
                return;
            }
            if (i == 3) {
                if (this.currentDisappearingAnimations.size() > 0) {
                    Iterator it2 = ((LinkedHashMap) this.currentDisappearingAnimations.clone()).values().iterator();
                    while (it2.hasNext()) {
                        ((Animator) it2.next()).end();
                    }
                    this.currentDisappearingAnimations.clear();
                    return;
                }
                return;
            }
            if (i != 4) {
                return;
            }
        }
        if (this.currentChangingAnimations.size() > 0) {
            Iterator it3 = ((LinkedHashMap) this.currentChangingAnimations.clone()).values().iterator();
            while (it3.hasNext()) {
                ((Animator) it3.next()).cancel();
            }
            this.currentChangingAnimations.clear();
        }
    }

    private void runAppearingTransition(final ViewGroup viewGroup, final View view) {
        Animator animator = this.currentDisappearingAnimations.get(view);
        if (animator != null) {
            animator.cancel();
        }
        Animator animator2 = this.mAppearingAnim;
        if (animator2 == null) {
            if (hasListeners()) {
                Iterator it = ((ArrayList) this.mListeners.clone()).iterator();
                while (it.hasNext()) {
                    ((TransitionListener) it.next()).endTransition(this, viewGroup, view, 2);
                }
                return;
            }
            return;
        }
        Animator animatorMo0clone = animator2.mo0clone();
        animatorMo0clone.setTarget(view);
        animatorMo0clone.setStartDelay(this.mAppearingDelay);
        animatorMo0clone.setDuration(this.mAppearingDuration);
        TimeInterpolator timeInterpolator = this.mAppearingInterpolator;
        if (timeInterpolator != sAppearingInterpolator) {
            animatorMo0clone.setInterpolator(timeInterpolator);
        }
        if (animatorMo0clone instanceof ObjectAnimator) {
            ((ObjectAnimator) animatorMo0clone).setCurrentPlayTime(0L);
        }
        animatorMo0clone.addListener(new AnimatorListenerAdapter() { // from class: android.animation.LayoutTransition.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator3) {
                LayoutTransition.this.currentAppearingAnimations.remove(view);
                if (LayoutTransition.this.hasListeners()) {
                    Iterator it2 = ((ArrayList) LayoutTransition.this.mListeners.clone()).iterator();
                    while (it2.hasNext()) {
                        ((TransitionListener) it2.next()).endTransition(LayoutTransition.this, viewGroup, view, 2);
                    }
                }
            }
        });
        this.currentAppearingAnimations.put(view, animatorMo0clone);
        animatorMo0clone.start();
    }

    private void runDisappearingTransition(final ViewGroup viewGroup, final View view) {
        Animator animator = this.currentAppearingAnimations.get(view);
        if (animator != null) {
            animator.cancel();
        }
        Animator animator2 = this.mDisappearingAnim;
        if (animator2 == null) {
            if (hasListeners()) {
                Iterator it = ((ArrayList) this.mListeners.clone()).iterator();
                while (it.hasNext()) {
                    ((TransitionListener) it.next()).endTransition(this, viewGroup, view, 3);
                }
                return;
            }
            return;
        }
        Animator animatorMo0clone = animator2.mo0clone();
        animatorMo0clone.setStartDelay(this.mDisappearingDelay);
        animatorMo0clone.setDuration(this.mDisappearingDuration);
        TimeInterpolator timeInterpolator = this.mDisappearingInterpolator;
        if (timeInterpolator != sDisappearingInterpolator) {
            animatorMo0clone.setInterpolator(timeInterpolator);
        }
        animatorMo0clone.setTarget(view);
        final float alpha = view.getAlpha();
        animatorMo0clone.addListener(new AnimatorListenerAdapter() { // from class: android.animation.LayoutTransition.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator3) {
                LayoutTransition.this.currentDisappearingAnimations.remove(view);
                view.setAlpha(alpha);
                if (LayoutTransition.this.hasListeners()) {
                    Iterator it2 = ((ArrayList) LayoutTransition.this.mListeners.clone()).iterator();
                    while (it2.hasNext()) {
                        ((TransitionListener) it2.next()).endTransition(LayoutTransition.this, viewGroup, view, 3);
                    }
                }
            }
        });
        if (animatorMo0clone instanceof ObjectAnimator) {
            ((ObjectAnimator) animatorMo0clone).setCurrentPlayTime(0L);
        }
        this.currentDisappearingAnimations.put(view, animatorMo0clone);
        animatorMo0clone.start();
    }

    private void addChild(ViewGroup viewGroup, View view, boolean z) {
        if (viewGroup.getWindowVisibility() != 0) {
            return;
        }
        if ((this.mTransitionTypes & 1) == 1) {
            cancel(3);
        }
        if (z && (this.mTransitionTypes & 4) == 4) {
            cancel(0);
            cancel(4);
        }
        if (hasListeners() && (this.mTransitionTypes & 1) == 1) {
            Iterator it = ((ArrayList) this.mListeners.clone()).iterator();
            while (it.hasNext()) {
                ((TransitionListener) it.next()).startTransition(this, viewGroup, view, 2);
            }
        }
        if (z && (this.mTransitionTypes & 4) == 4) {
            runChangeTransition(viewGroup, view, 2);
        }
        if ((this.mTransitionTypes & 1) == 1) {
            runAppearingTransition(viewGroup, view);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasListeners() {
        ArrayList<TransitionListener> arrayList = this.mListeners;
        return arrayList != null && arrayList.size() > 0;
    }

    public void layoutChange(ViewGroup viewGroup) {
        if (viewGroup.getWindowVisibility() == 0 && (this.mTransitionTypes & 16) == 16 && !isRunning()) {
            runChangeTransition(viewGroup, null, 4);
        }
    }

    public void addChild(ViewGroup viewGroup, View view) {
        addChild(viewGroup, view, true);
    }

    @Deprecated
    public void showChild(ViewGroup viewGroup, View view) {
        addChild(viewGroup, view, true);
    }

    public void showChild(ViewGroup viewGroup, View view, int i) {
        addChild(viewGroup, view, i == 8);
    }

    private void removeChild(ViewGroup viewGroup, View view, boolean z) {
        if (viewGroup.getWindowVisibility() != 0) {
            return;
        }
        if ((this.mTransitionTypes & 2) == 2) {
            cancel(2);
        }
        if (z && (this.mTransitionTypes & 8) == 8) {
            cancel(1);
            cancel(4);
        }
        if (hasListeners() && (this.mTransitionTypes & 2) == 2) {
            Iterator it = ((ArrayList) this.mListeners.clone()).iterator();
            while (it.hasNext()) {
                ((TransitionListener) it.next()).startTransition(this, viewGroup, view, 3);
            }
        }
        if (z && (this.mTransitionTypes & 8) == 8) {
            runChangeTransition(viewGroup, view, 3);
        }
        if ((this.mTransitionTypes & 2) == 2) {
            runDisappearingTransition(viewGroup, view);
        }
    }

    public void removeChild(ViewGroup viewGroup, View view) {
        removeChild(viewGroup, view, true);
    }

    @Deprecated
    public void hideChild(ViewGroup viewGroup, View view) {
        removeChild(viewGroup, view, true);
    }

    public void hideChild(ViewGroup viewGroup, View view, int i) {
        removeChild(viewGroup, view, i == 8);
    }

    public void addTransitionListener(TransitionListener transitionListener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        this.mListeners.add(transitionListener);
    }

    public void removeTransitionListener(TransitionListener transitionListener) {
        ArrayList<TransitionListener> arrayList = this.mListeners;
        if (arrayList == null) {
            return;
        }
        arrayList.remove(transitionListener);
    }

    public List<TransitionListener> getTransitionListeners() {
        return this.mListeners;
    }
}
