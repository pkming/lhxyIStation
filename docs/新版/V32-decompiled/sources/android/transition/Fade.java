package android.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/* JADX INFO: loaded from: classes.dex */
public class Fade extends Visibility {
    private static boolean DBG = false;
    public static final int IN = 1;
    private static final String LOG_TAG = "Fade";
    public static final int OUT = 2;
    private static final String PROPNAME_SCREEN_X = "android:fade:screenX";
    private static final String PROPNAME_SCREEN_Y = "android:fade:screenY";
    private int mFadingMode;

    public Fade() {
        this(3);
    }

    public Fade(int i) {
        this.mFadingMode = i;
    }

    private Animator createAnimation(View view, float f, float f2, AnimatorListenerAdapter animatorListenerAdapter) {
        if (f == f2) {
            if (animatorListenerAdapter != null) {
                animatorListenerAdapter.onAnimationEnd(null);
            }
            return null;
        }
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(view, "transitionAlpha", f, f2);
        if (DBG) {
            Log.d(LOG_TAG, "Created animator " + objectAnimatorOfFloat);
        }
        if (animatorListenerAdapter != null) {
            objectAnimatorOfFloat.addListener(animatorListenerAdapter);
            objectAnimatorOfFloat.addPauseListener(animatorListenerAdapter);
        }
        return objectAnimatorOfFloat;
    }

    private void captureValues(TransitionValues transitionValues) {
        int[] iArr = new int[2];
        transitionValues.view.getLocationOnScreen(iArr);
        transitionValues.values.put(PROPNAME_SCREEN_X, Integer.valueOf(iArr[0]));
        transitionValues.values.put(PROPNAME_SCREEN_Y, Integer.valueOf(iArr[1]));
    }

    @Override // android.transition.Visibility, android.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        captureValues(transitionValues);
    }

    @Override // android.transition.Visibility
    public Animator onAppear(ViewGroup viewGroup, TransitionValues transitionValues, int i, TransitionValues transitionValues2, int i2) {
        if ((this.mFadingMode & 1) != 1 || transitionValues2 == null) {
            return null;
        }
        final View view = transitionValues2.view;
        if (DBG) {
            Log.d(LOG_TAG, "Fade.onAppear: startView, startVis, endView, endVis = " + (transitionValues != null ? transitionValues.view : null) + ", " + i + ", " + view + ", " + i2);
        }
        view.setTransitionAlpha(0.0f);
        addListener(new Transition.TransitionListenerAdapter() { // from class: android.transition.Fade.1
            boolean mCanceled = false;
            float mPausedAlpha;

            @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionCancel(Transition transition) {
                view.setTransitionAlpha(1.0f);
                this.mCanceled = true;
            }

            @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionEnd(Transition transition) {
                if (this.mCanceled) {
                    return;
                }
                view.setTransitionAlpha(1.0f);
            }

            @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionPause(Transition transition) {
                this.mPausedAlpha = view.getTransitionAlpha();
                view.setTransitionAlpha(1.0f);
            }

            @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionResume(Transition transition) {
                view.setTransitionAlpha(this.mPausedAlpha);
            }
        });
        return createAnimation(view, 0.0f, 1.0f, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:47:0x00b0  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x0101  */
    @Override // android.transition.Visibility
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.animation.Animator onDisappear(final android.view.ViewGroup r15, android.transition.TransitionValues r16, int r17, android.transition.TransitionValues r18, final int r19) {
        /*
            Method dump skipped, instruction units count: 281
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.transition.Fade.onDisappear(android.view.ViewGroup, android.transition.TransitionValues, int, android.transition.TransitionValues, int):android.animation.Animator");
    }
}
