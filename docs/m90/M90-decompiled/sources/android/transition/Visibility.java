package android.transition;

import android.animation.Animator;
import android.view.View;
import android.view.ViewGroup;

/* JADX INFO: loaded from: classes.dex */
public abstract class Visibility extends Transition {
    private static final String PROPNAME_VISIBILITY = "android:visibility:visibility";
    private static final String PROPNAME_PARENT = "android:visibility:parent";
    private static final String[] sTransitionProperties = {PROPNAME_VISIBILITY, PROPNAME_PARENT};

    public Animator onAppear(ViewGroup viewGroup, TransitionValues transitionValues, int i, TransitionValues transitionValues2, int i2) {
        return null;
    }

    public Animator onDisappear(ViewGroup viewGroup, TransitionValues transitionValues, int i, TransitionValues transitionValues2, int i2) {
        return null;
    }

    private static class VisibilityInfo {
        ViewGroup endParent;
        int endVisibility;
        boolean fadeIn;
        ViewGroup startParent;
        int startVisibility;
        boolean visibilityChange;

        private VisibilityInfo() {
        }
    }

    @Override // android.transition.Transition
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    private void captureValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_VISIBILITY, Integer.valueOf(transitionValues.view.getVisibility()));
        transitionValues.values.put(PROPNAME_PARENT, transitionValues.view.getParent());
    }

    @Override // android.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // android.transition.Transition
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    public boolean isVisible(TransitionValues transitionValues) {
        if (transitionValues == null) {
            return false;
        }
        return ((Integer) transitionValues.values.get(PROPNAME_VISIBILITY)).intValue() == 0 && ((View) transitionValues.values.get(PROPNAME_PARENT)) != null;
    }

    private VisibilityInfo getVisibilityChangeInfo(TransitionValues transitionValues, TransitionValues transitionValues2) {
        VisibilityInfo visibilityInfo = new VisibilityInfo();
        visibilityInfo.visibilityChange = false;
        visibilityInfo.fadeIn = false;
        if (transitionValues != null) {
            visibilityInfo.startVisibility = ((Integer) transitionValues.values.get(PROPNAME_VISIBILITY)).intValue();
            visibilityInfo.startParent = (ViewGroup) transitionValues.values.get(PROPNAME_PARENT);
        } else {
            visibilityInfo.startVisibility = -1;
            visibilityInfo.startParent = null;
        }
        if (transitionValues2 != null) {
            visibilityInfo.endVisibility = ((Integer) transitionValues2.values.get(PROPNAME_VISIBILITY)).intValue();
            visibilityInfo.endParent = (ViewGroup) transitionValues2.values.get(PROPNAME_PARENT);
        } else {
            visibilityInfo.endVisibility = -1;
            visibilityInfo.endParent = null;
        }
        if (transitionValues != null && transitionValues2 != null) {
            if (visibilityInfo.startVisibility == visibilityInfo.endVisibility && visibilityInfo.startParent == visibilityInfo.endParent) {
                return visibilityInfo;
            }
            if (visibilityInfo.startVisibility != visibilityInfo.endVisibility) {
                if (visibilityInfo.startVisibility == 0) {
                    visibilityInfo.fadeIn = false;
                    visibilityInfo.visibilityChange = true;
                } else if (visibilityInfo.endVisibility == 0) {
                    visibilityInfo.fadeIn = true;
                    visibilityInfo.visibilityChange = true;
                }
            } else if (visibilityInfo.startParent != visibilityInfo.endParent) {
                if (visibilityInfo.endParent == null) {
                    visibilityInfo.fadeIn = false;
                    visibilityInfo.visibilityChange = true;
                } else if (visibilityInfo.startParent == null) {
                    visibilityInfo.fadeIn = true;
                    visibilityInfo.visibilityChange = true;
                }
            }
        }
        if (transitionValues == null) {
            visibilityInfo.fadeIn = true;
            visibilityInfo.visibilityChange = true;
        } else if (transitionValues2 == null) {
            visibilityInfo.fadeIn = false;
            visibilityInfo.visibilityChange = true;
        }
        return visibilityInfo;
    }

    @Override // android.transition.Transition
    public Animator createAnimator(ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
        VisibilityInfo visibilityChangeInfo = getVisibilityChangeInfo(transitionValues, transitionValues2);
        if (visibilityChangeInfo.visibilityChange) {
            boolean z = false;
            if (this.mTargets.size() > 0 || this.mTargetIds.size() > 0) {
                View view = transitionValues != null ? transitionValues.view : null;
                View view2 = transitionValues2 != null ? transitionValues2.view : null;
                int id = view != null ? view.getId() : -1;
                int id2 = view2 != null ? view2.getId() : -1;
                if (isValidTarget(view, id) || isValidTarget(view2, id2)) {
                    z = true;
                }
            }
            if (z || visibilityChangeInfo.startParent != null || visibilityChangeInfo.endParent != null) {
                if (visibilityChangeInfo.fadeIn) {
                    return onAppear(viewGroup, transitionValues, visibilityChangeInfo.startVisibility, transitionValues2, visibilityChangeInfo.endVisibility);
                }
                return onDisappear(viewGroup, transitionValues, visibilityChangeInfo.startVisibility, transitionValues2, visibilityChangeInfo.endVisibility);
            }
        }
        return null;
    }
}
