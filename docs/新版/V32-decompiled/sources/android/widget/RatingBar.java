package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.R;

/* JADX INFO: loaded from: classes.dex */
public class RatingBar extends AbsSeekBar {
    private int mNumStars;
    private OnRatingBarChangeListener mOnRatingBarChangeListener;
    private int mProgressOnStartTracking;

    public interface OnRatingBarChangeListener {
        void onRatingChanged(RatingBar ratingBar, float f, boolean z);
    }

    public RatingBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mNumStars = 5;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.RatingBar, i, 0);
        int i2 = typedArrayObtainStyledAttributes.getInt(0, this.mNumStars);
        setIsIndicator(typedArrayObtainStyledAttributes.getBoolean(3, !this.mIsUserSeekable));
        float f = typedArrayObtainStyledAttributes.getFloat(1, -1.0f);
        float f2 = typedArrayObtainStyledAttributes.getFloat(2, -1.0f);
        typedArrayObtainStyledAttributes.recycle();
        if (i2 > 0 && i2 != this.mNumStars) {
            setNumStars(i2);
        }
        if (f2 >= 0.0f) {
            setStepSize(f2);
        } else {
            setStepSize(0.5f);
        }
        if (f >= 0.0f) {
            setRating(f);
        }
        this.mTouchProgressOffset = 1.1f;
    }

    public RatingBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, android.R.attr.ratingBarStyle);
    }

    public RatingBar(Context context) {
        this(context, null);
    }

    public void setOnRatingBarChangeListener(OnRatingBarChangeListener onRatingBarChangeListener) {
        this.mOnRatingBarChangeListener = onRatingBarChangeListener;
    }

    public OnRatingBarChangeListener getOnRatingBarChangeListener() {
        return this.mOnRatingBarChangeListener;
    }

    public void setIsIndicator(boolean z) {
        this.mIsUserSeekable = !z;
        setFocusable(!z);
    }

    public boolean isIndicator() {
        return !this.mIsUserSeekable;
    }

    public void setNumStars(int i) {
        if (i <= 0) {
            return;
        }
        this.mNumStars = i;
        requestLayout();
    }

    public int getNumStars() {
        return this.mNumStars;
    }

    public void setRating(float f) {
        setProgress(Math.round(f * getProgressPerStar()));
    }

    public float getRating() {
        return getProgress() / getProgressPerStar();
    }

    public void setStepSize(float f) {
        if (f <= 0.0f) {
            return;
        }
        float f2 = this.mNumStars / f;
        setMax((int) f2);
        setProgress((int) ((f2 / getMax()) * getProgress()));
    }

    public float getStepSize() {
        return getNumStars() / getMax();
    }

    private float getProgressPerStar() {
        if (this.mNumStars > 0) {
            return (getMax() * 1.0f) / this.mNumStars;
        }
        return 1.0f;
    }

    @Override // android.widget.ProgressBar
    Shape getDrawableShape() {
        return new RectShape();
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar
    void onProgressRefresh(float f, boolean z) {
        super.onProgressRefresh(f, z);
        updateSecondaryProgress(getProgress());
        if (z) {
            return;
        }
        dispatchRatingChange(false);
    }

    private void updateSecondaryProgress(int i) {
        float progressPerStar = getProgressPerStar();
        if (progressPerStar > 0.0f) {
            setSecondaryProgress((int) (Math.ceil(i / progressPerStar) * ((double) progressPerStar)));
        }
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected synchronized void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mSampleTile != null) {
            setMeasuredDimension(resolveSizeAndState(this.mSampleTile.getWidth() * this.mNumStars, i, 0), getMeasuredHeight());
        }
    }

    @Override // android.widget.AbsSeekBar
    void onStartTrackingTouch() {
        this.mProgressOnStartTracking = getProgress();
        super.onStartTrackingTouch();
    }

    @Override // android.widget.AbsSeekBar
    void onStopTrackingTouch() {
        super.onStopTrackingTouch();
        if (getProgress() != this.mProgressOnStartTracking) {
            dispatchRatingChange(true);
        }
    }

    @Override // android.widget.AbsSeekBar
    void onKeyChange() {
        super.onKeyChange();
        dispatchRatingChange(true);
    }

    void dispatchRatingChange(boolean z) {
        OnRatingBarChangeListener onRatingBarChangeListener = this.mOnRatingBarChangeListener;
        if (onRatingBarChangeListener != null) {
            onRatingBarChangeListener.onRatingChanged(this, getRating(), z);
        }
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar
    public synchronized void setMax(int i) {
        if (i <= 0) {
            return;
        }
        super.setMax(i);
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(RatingBar.class.getName());
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(RatingBar.class.getName());
    }
}
