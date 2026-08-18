package android.transition;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/* JADX INFO: loaded from: classes.dex */
public class Slide extends Visibility {
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();

    @Override // android.transition.Visibility
    public Animator onAppear(ViewGroup viewGroup, TransitionValues transitionValues, int i, TransitionValues transitionValues2, int i2) {
        View view = transitionValues2 != null ? transitionValues2.view : null;
        view.setTranslationY(view.getHeight() * (-2));
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.getHeight() * (-2), 0.0f);
        objectAnimatorOfFloat.setInterpolator(sDecelerator);
        return objectAnimatorOfFloat;
    }

    @Override // android.transition.Visibility
    public Animator onDisappear(ViewGroup viewGroup, TransitionValues transitionValues, int i, TransitionValues transitionValues2, int i2) {
        View view = transitionValues != null ? transitionValues.view : null;
        view.setTranslationY(0.0f);
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0.0f, view.getHeight() * (-2));
        objectAnimatorOfFloat.setInterpolator(sAccelerator);
        return objectAnimatorOfFloat;
    }
}
