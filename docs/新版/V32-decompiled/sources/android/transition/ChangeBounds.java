package android.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.RectEvaluator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.transition.Transition;
import android.view.View;
import android.view.ViewGroup;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ChangeBounds extends Transition {
    private static final String LOG_TAG = "ChangeBounds";
    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";
    private static final String PROPNAME_PARENT = "android:changeBounds:parent";
    private static final String PROPNAME_WINDOW_X = "android:changeBounds:windowX";
    private static final String PROPNAME_WINDOW_Y = "android:changeBounds:windowY";
    private static final String[] sTransitionProperties = {PROPNAME_BOUNDS, PROPNAME_PARENT, PROPNAME_WINDOW_X, PROPNAME_WINDOW_Y};
    private static RectEvaluator sRectEvaluator = new RectEvaluator();
    int[] tempLocation = new int[2];
    boolean mResizeClip = false;
    boolean mReparent = false;

    @Override // android.transition.Transition
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    public void setResizeClip(boolean z) {
        this.mResizeClip = z;
    }

    public void setReparent(boolean z) {
        this.mReparent = z;
    }

    private void captureValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        transitionValues.values.put(PROPNAME_BOUNDS, new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
        transitionValues.values.put(PROPNAME_PARENT, transitionValues.view.getParent());
        transitionValues.view.getLocationInWindow(this.tempLocation);
        transitionValues.values.put(PROPNAME_WINDOW_X, Integer.valueOf(this.tempLocation[0]));
        transitionValues.values.put(PROPNAME_WINDOW_Y, Integer.valueOf(this.tempLocation[1]));
    }

    @Override // android.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // android.transition.Transition
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // android.transition.Transition
    public Animator createAnimator(final ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
        char c;
        int i;
        boolean z;
        char c2;
        char c3;
        int i2;
        if (transitionValues == null || transitionValues2 == null) {
            return null;
        }
        Map<String, Object> map = transitionValues.values;
        Map<String, Object> map2 = transitionValues2.values;
        ViewGroup viewGroup2 = (ViewGroup) map.get(PROPNAME_PARENT);
        ViewGroup viewGroup3 = (ViewGroup) map2.get(PROPNAME_PARENT);
        if (viewGroup2 == null || viewGroup3 == null) {
            return null;
        }
        final View view = transitionValues2.view;
        int i3 = 0;
        boolean z2 = viewGroup2 == viewGroup3 || viewGroup2.getId() == viewGroup3.getId();
        if (!this.mReparent || z2) {
            Rect rect = (Rect) transitionValues.values.get(PROPNAME_BOUNDS);
            Rect rect2 = (Rect) transitionValues2.values.get(PROPNAME_BOUNDS);
            int i4 = rect.left;
            int i5 = rect2.left;
            int i6 = rect.top;
            int i7 = rect2.top;
            int i8 = rect.right;
            int i9 = rect2.right;
            int i10 = rect.bottom;
            int i11 = rect2.bottom;
            int i12 = i8 - i4;
            int i13 = i10 - i6;
            int i14 = i9 - i5;
            int i15 = i11 - i7;
            if (i12 != 0 && i13 != 0 && i14 != 0 && i15 != 0) {
                int i16 = i4 != i5 ? 1 : 0;
                if (i6 != i7) {
                    i16++;
                }
                if (i8 != i9) {
                    i16++;
                }
                if (i10 != i11) {
                    i16++;
                }
                i3 = i16;
            }
            if (i3 <= 0) {
                return null;
            }
            if (!this.mResizeClip) {
                PropertyValuesHolder[] propertyValuesHolderArr = new PropertyValuesHolder[i3];
                if (i4 != i5) {
                    view.setLeft(i4);
                }
                if (i6 != i7) {
                    view.setTop(i6);
                }
                if (i8 != i9) {
                    view.setRight(i8);
                }
                if (i10 != i11) {
                    view.setBottom(i10);
                }
                if (i4 != i5) {
                    c2 = 0;
                    c3 = 1;
                    propertyValuesHolderArr[0] = PropertyValuesHolder.ofInt("left", i4, i5);
                    i2 = 1;
                } else {
                    c2 = 0;
                    c3 = 1;
                    i2 = 0;
                }
                if (i6 != i7) {
                    int[] iArr = new int[2];
                    iArr[c2] = i6;
                    iArr[c3] = i7;
                    propertyValuesHolderArr[i2] = PropertyValuesHolder.ofInt("top", iArr);
                    i2++;
                }
                if (i8 != i9) {
                    int[] iArr2 = new int[2];
                    iArr2[c2] = i8;
                    iArr2[c3] = i9;
                    propertyValuesHolderArr[i2] = PropertyValuesHolder.ofInt("right", iArr2);
                    i2++;
                }
                if (i10 != i11) {
                    int[] iArr3 = new int[2];
                    iArr3[c2] = i10;
                    iArr3[c3] = i11;
                    propertyValuesHolderArr[i2] = PropertyValuesHolder.ofInt("bottom", iArr3);
                }
                ObjectAnimator objectAnimatorOfPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(view, propertyValuesHolderArr);
                if (view.getParent() instanceof ViewGroup) {
                    final ViewGroup viewGroup4 = (ViewGroup) view.getParent();
                    viewGroup4.suppressLayout(true);
                    addListener(new Transition.TransitionListenerAdapter() { // from class: android.transition.ChangeBounds.1
                        boolean mCanceled = false;

                        @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                        public void onTransitionCancel(Transition transition) {
                            viewGroup4.suppressLayout(false);
                            this.mCanceled = true;
                        }

                        @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                        public void onTransitionEnd(Transition transition) {
                            if (this.mCanceled) {
                                return;
                            }
                            viewGroup4.suppressLayout(false);
                        }

                        @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                        public void onTransitionPause(Transition transition) {
                            viewGroup4.suppressLayout(false);
                        }

                        @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                        public void onTransitionResume(Transition transition) {
                            viewGroup4.suppressLayout(true);
                        }
                    });
                }
                return objectAnimatorOfPropertyValuesHolder;
            }
            if (i12 != i14) {
                view.setRight(Math.max(i12, i14) + i5);
            }
            if (i13 != i15) {
                view.setBottom(Math.max(i13, i15) + i7);
            }
            if (i4 != i5) {
                view.setTranslationX(i4 - i5);
            }
            if (i6 != i7) {
                view.setTranslationY(i6 - i7);
            }
            float f = i5 - i4;
            float f2 = i7 - i6;
            int i17 = i14 - i12;
            int i18 = i15 - i13;
            int i19 = f != 0.0f ? 1 : 0;
            if (f2 != 0.0f) {
                i19++;
            }
            if (i17 != 0 || i18 != 0) {
                i19++;
            }
            PropertyValuesHolder[] propertyValuesHolderArr2 = new PropertyValuesHolder[i19];
            if (f != 0.0f) {
                c = 0;
                propertyValuesHolderArr2[0] = PropertyValuesHolder.ofFloat("translationX", view.getTranslationX(), 0.0f);
                i = 1;
            } else {
                c = 0;
                i = 0;
            }
            if (f2 != 0.0f) {
                float[] fArr = new float[2];
                fArr[c] = view.getTranslationY();
                fArr[1] = 0.0f;
                propertyValuesHolderArr2[i] = PropertyValuesHolder.ofFloat("translationY", fArr);
                i++;
            }
            if (i17 == 0 && i18 == 0) {
                z = true;
            } else {
                z = true;
                propertyValuesHolderArr2[i] = PropertyValuesHolder.ofObject("clipBounds", sRectEvaluator, new Rect(0, 0, i12, i13), new Rect(0, 0, i14, i15));
            }
            ObjectAnimator objectAnimatorOfPropertyValuesHolder2 = ObjectAnimator.ofPropertyValuesHolder(view, propertyValuesHolderArr2);
            if (view.getParent() instanceof ViewGroup) {
                final ViewGroup viewGroup5 = (ViewGroup) view.getParent();
                viewGroup5.suppressLayout(z);
                addListener(new Transition.TransitionListenerAdapter() { // from class: android.transition.ChangeBounds.2
                    boolean mCanceled = false;

                    @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                    public void onTransitionCancel(Transition transition) {
                        viewGroup5.suppressLayout(false);
                        this.mCanceled = true;
                    }

                    @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                    public void onTransitionEnd(Transition transition) {
                        if (this.mCanceled) {
                            return;
                        }
                        viewGroup5.suppressLayout(false);
                    }

                    @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                    public void onTransitionPause(Transition transition) {
                        viewGroup5.suppressLayout(false);
                    }

                    @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                    public void onTransitionResume(Transition transition) {
                        viewGroup5.suppressLayout(true);
                    }
                });
            }
            objectAnimatorOfPropertyValuesHolder2.addListener(new AnimatorListenerAdapter() { // from class: android.transition.ChangeBounds.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    view.setClipBounds(null);
                }
            });
            return objectAnimatorOfPropertyValuesHolder2;
        }
        int iIntValue = ((Integer) transitionValues.values.get(PROPNAME_WINDOW_X)).intValue();
        int iIntValue2 = ((Integer) transitionValues.values.get(PROPNAME_WINDOW_Y)).intValue();
        int iIntValue3 = ((Integer) transitionValues2.values.get(PROPNAME_WINDOW_X)).intValue();
        int iIntValue4 = ((Integer) transitionValues2.values.get(PROPNAME_WINDOW_Y)).intValue();
        if (iIntValue == iIntValue3 && iIntValue2 == iIntValue4) {
            return null;
        }
        viewGroup.getLocationInWindow(this.tempLocation);
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmapCreateBitmap));
        final BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmapCreateBitmap);
        view.setVisibility(4);
        viewGroup.getOverlay().add(bitmapDrawable);
        int[] iArr4 = this.tempLocation;
        Rect rect3 = new Rect(iIntValue - iArr4[0], iIntValue2 - iArr4[1], (iIntValue - iArr4[0]) + view.getWidth(), (iIntValue2 - this.tempLocation[1]) + view.getHeight());
        int[] iArr5 = this.tempLocation;
        ObjectAnimator objectAnimatorOfObject = ObjectAnimator.ofObject(bitmapDrawable, "bounds", sRectEvaluator, rect3, new Rect(iIntValue3 - iArr5[0], iIntValue4 - iArr5[1], (iIntValue3 - iArr5[0]) + view.getWidth(), (iIntValue4 - this.tempLocation[1]) + view.getHeight()));
        objectAnimatorOfObject.addListener(new AnimatorListenerAdapter() { // from class: android.transition.ChangeBounds.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                viewGroup.getOverlay().remove(bitmapDrawable);
                view.setVisibility(0);
            }
        });
        return objectAnimatorOfObject;
    }
}
