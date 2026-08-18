package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.R;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbsSeekBar extends ProgressBar {
    private static final int NO_ALPHA = 255;
    private float mDisabledAlpha;
    private boolean mIsDragging;
    boolean mIsUserSeekable;
    private int mKeyProgressIncrement;
    private int mScaledTouchSlop;
    private Drawable mThumb;
    private int mThumbOffset;
    private float mTouchDownX;
    float mTouchProgressOffset;

    void onKeyChange() {
    }

    public AbsSeekBar(Context context) {
        super(context);
        this.mIsUserSeekable = true;
        this.mKeyProgressIncrement = 1;
    }

    public AbsSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsUserSeekable = true;
        this.mKeyProgressIncrement = 1;
    }

    public AbsSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsUserSeekable = true;
        this.mKeyProgressIncrement = 1;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SeekBar, i, 0);
        setThumb(typedArrayObtainStyledAttributes.getDrawable(0));
        setThumbOffset(typedArrayObtainStyledAttributes.getDimensionPixelOffset(1, getThumbOffset()));
        typedArrayObtainStyledAttributes.recycle();
        TypedArray typedArrayObtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, R.styleable.Theme, 0, 0);
        this.mDisabledAlpha = typedArrayObtainStyledAttributes2.getFloat(3, 0.5f);
        typedArrayObtainStyledAttributes2.recycle();
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setThumb(Drawable drawable) {
        boolean z;
        Drawable drawable2 = this.mThumb;
        if (drawable2 == null || drawable == drawable2) {
            z = false;
        } else {
            drawable2.setCallback(null);
            z = true;
        }
        if (drawable != null) {
            drawable.setCallback(this);
            if (canResolveLayoutDirection()) {
                drawable.setLayoutDirection(getLayoutDirection());
            }
            this.mThumbOffset = drawable.getIntrinsicWidth() / 2;
            if (z && (drawable.getIntrinsicWidth() != this.mThumb.getIntrinsicWidth() || drawable.getIntrinsicHeight() != this.mThumb.getIntrinsicHeight())) {
                requestLayout();
            }
        }
        this.mThumb = drawable;
        invalidate();
        if (z) {
            updateThumbPos(getWidth(), getHeight());
            if (drawable == null || !drawable.isStateful()) {
                return;
            }
            drawable.setState(getDrawableState());
        }
    }

    public Drawable getThumb() {
        return this.mThumb;
    }

    public int getThumbOffset() {
        return this.mThumbOffset;
    }

    public void setThumbOffset(int i) {
        this.mThumbOffset = i;
        invalidate();
    }

    public void setKeyProgressIncrement(int i) {
        if (i < 0) {
            i = -i;
        }
        this.mKeyProgressIncrement = i;
    }

    public int getKeyProgressIncrement() {
        return this.mKeyProgressIncrement;
    }

    @Override // android.widget.ProgressBar
    public synchronized void setMax(int i) {
        super.setMax(i);
        if (this.mKeyProgressIncrement == 0 || getMax() / this.mKeyProgressIncrement > 20) {
            setKeyProgressIncrement(Math.max(1, Math.round(getMax() / 20.0f)));
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return drawable == this.mThumb || super.verifyDrawable(drawable);
    }

    @Override // android.widget.ProgressBar, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.mThumb;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable progressDrawable = getProgressDrawable();
        if (progressDrawable != null) {
            progressDrawable.setAlpha(isEnabled() ? 255 : (int) (this.mDisabledAlpha * 255.0f));
        }
        Drawable drawable = this.mThumb;
        if (drawable == null || !drawable.isStateful()) {
            return;
        }
        this.mThumb.setState(getDrawableState());
    }

    @Override // android.widget.ProgressBar
    void onProgressRefresh(float f, boolean z) {
        super.onProgressRefresh(f, z);
        Drawable drawable = this.mThumb;
        if (drawable != null) {
            setThumbPos(getWidth(), drawable, f, Integer.MIN_VALUE);
            invalidate();
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        updateThumbPos(i, i2);
    }

    private void updateThumbPos(int i, int i2) {
        Drawable currentDrawable = getCurrentDrawable();
        Drawable drawable = this.mThumb;
        int intrinsicHeight = drawable == null ? 0 : drawable.getIntrinsicHeight();
        int iMin = Math.min(this.mMaxHeight, (i2 - this.mPaddingTop) - this.mPaddingBottom);
        int max = getMax();
        float progress = max > 0 ? getProgress() / max : 0.0f;
        if (intrinsicHeight > iMin) {
            if (drawable != null) {
                setThumbPos(i, drawable, progress, 0);
            }
            int i3 = (intrinsicHeight - iMin) / 2;
            if (currentDrawable != null) {
                currentDrawable.setBounds(0, i3, (i - this.mPaddingRight) - this.mPaddingLeft, ((i2 - this.mPaddingBottom) - i3) - this.mPaddingTop);
                return;
            }
            return;
        }
        if (currentDrawable != null) {
            currentDrawable.setBounds(0, 0, (i - this.mPaddingRight) - this.mPaddingLeft, (i2 - this.mPaddingBottom) - this.mPaddingTop);
        }
        int i4 = (iMin - intrinsicHeight) / 2;
        if (drawable != null) {
            setThumbPos(i, drawable, progress, i4);
        }
    }

    private void setThumbPos(int i, Drawable drawable, float f, int i2) {
        int i3;
        int i4 = (i - this.mPaddingLeft) - this.mPaddingRight;
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        int i5 = (i4 - intrinsicWidth) + (this.mThumbOffset * 2);
        int i6 = (int) (f * i5);
        if (i2 == Integer.MIN_VALUE) {
            Rect bounds = drawable.getBounds();
            int i7 = bounds.top;
            i3 = bounds.bottom;
            i2 = i7;
        } else {
            i3 = intrinsicHeight + i2;
        }
        if (isLayoutRtl() && this.mMirrorForRtl) {
            i6 = i5 - i6;
        }
        drawable.setBounds(i6, i2, intrinsicWidth + i6, i3);
    }

    @Override // android.widget.ProgressBar, android.view.View
    public void onResolveDrawables(int i) {
        super.onResolveDrawables(i);
        Drawable drawable = this.mThumb;
        if (drawable != null) {
            drawable.setLayoutDirection(i);
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mThumb != null) {
            canvas.save();
            canvas.translate(this.mPaddingLeft - this.mThumbOffset, this.mPaddingTop);
            this.mThumb.draw(canvas);
            canvas.restore();
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected synchronized void onMeasure(int i, int i2) {
        int iMax;
        int iMax2;
        Drawable currentDrawable = getCurrentDrawable();
        Drawable drawable = this.mThumb;
        int intrinsicHeight = drawable == null ? 0 : drawable.getIntrinsicHeight();
        if (currentDrawable != null) {
            iMax2 = Math.max(this.mMinWidth, Math.min(this.mMaxWidth, currentDrawable.getIntrinsicWidth()));
            iMax = Math.max(intrinsicHeight, Math.max(this.mMinHeight, Math.min(this.mMaxHeight, currentDrawable.getIntrinsicHeight())));
        } else {
            iMax = 0;
            iMax2 = 0;
        }
        setMeasuredDimension(resolveSizeAndState(iMax2 + this.mPaddingLeft + this.mPaddingRight, i, 0), resolveSizeAndState(iMax + this.mPaddingTop + this.mPaddingBottom, i2, 0));
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.mIsUserSeekable || !isEnabled()) {
            return false;
        }
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action == 1) {
                if (this.mIsDragging) {
                    trackTouchEvent(motionEvent);
                    onStopTrackingTouch();
                    setPressed(false);
                } else {
                    onStartTrackingTouch();
                    trackTouchEvent(motionEvent);
                    onStopTrackingTouch();
                }
                invalidate();
            } else if (action != 2) {
                if (action == 3) {
                    if (this.mIsDragging) {
                        onStopTrackingTouch();
                        setPressed(false);
                    }
                    invalidate();
                }
            } else if (this.mIsDragging) {
                trackTouchEvent(motionEvent);
            } else if (Math.abs(motionEvent.getX() - this.mTouchDownX) > this.mScaledTouchSlop) {
                setPressed(true);
                Drawable drawable = this.mThumb;
                if (drawable != null) {
                    invalidate(drawable.getBounds());
                }
                onStartTrackingTouch();
                trackTouchEvent(motionEvent);
                attemptClaimDrag();
            }
        } else if (isInScrollingContainer()) {
            this.mTouchDownX = motionEvent.getX();
        } else {
            setPressed(true);
            Drawable drawable2 = this.mThumb;
            if (drawable2 != null) {
                invalidate(drawable2.getBounds());
            }
            onStartTrackingTouch();
            trackTouchEvent(motionEvent);
            attemptClaimDrag();
        }
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0038  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void trackTouchEvent(android.view.MotionEvent r6) {
        /*
            r5 = this;
            int r0 = r5.getWidth()
            int r1 = r5.mPaddingLeft
            int r1 = r0 - r1
            int r2 = r5.mPaddingRight
            int r1 = r1 - r2
            float r6 = r6.getX()
            int r6 = (int) r6
            boolean r2 = r5.isLayoutRtl()
            r3 = 1065353216(0x3f800000, float:1.0)
            r4 = 0
            if (r2 == 0) goto L34
            boolean r2 = r5.mMirrorForRtl
            if (r2 == 0) goto L34
            int r2 = r5.mPaddingRight
            int r0 = r0 - r2
            if (r6 <= r0) goto L23
            goto L38
        L23:
            int r0 = r5.mPaddingLeft
            if (r6 >= r0) goto L28
            goto L49
        L28:
            int r6 = r1 - r6
            int r0 = r5.mPaddingLeft
            int r6 = r6 + r0
            float r6 = (float) r6
            float r0 = (float) r1
            float r3 = r6 / r0
            float r4 = r5.mTouchProgressOffset
            goto L49
        L34:
            int r2 = r5.mPaddingLeft
            if (r6 >= r2) goto L3a
        L38:
            r3 = r4
            goto L49
        L3a:
            int r2 = r5.mPaddingRight
            int r0 = r0 - r2
            if (r6 <= r0) goto L40
            goto L49
        L40:
            int r0 = r5.mPaddingLeft
            int r6 = r6 - r0
            float r6 = (float) r6
            float r0 = (float) r1
            float r3 = r6 / r0
            float r4 = r5.mTouchProgressOffset
        L49:
            int r6 = r5.getMax()
            float r6 = (float) r6
            float r3 = r3 * r6
            float r4 = r4 + r3
            int r6 = (int) r4
            r0 = 1
            r5.setProgress(r6, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsSeekBar.trackTouchEvent(android.view.MotionEvent):void");
    }

    private void attemptClaimDrag() {
        if (this.mParent != null) {
            this.mParent.requestDisallowInterceptTouchEvent(true);
        }
    }

    void onStartTrackingTouch() {
        this.mIsDragging = true;
    }

    void onStopTrackingTouch() {
        this.mIsDragging = false;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (isEnabled()) {
            int progress = getProgress();
            if (i != 21) {
                if (i == 22 && progress < getMax()) {
                    setProgress(progress + this.mKeyProgressIncrement, true);
                    onKeyChange();
                    return true;
                }
            } else if (progress > 0) {
                setProgress(progress - this.mKeyProgressIncrement, true);
                onKeyChange();
                return true;
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.widget.ProgressBar, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(AbsSeekBar.class.getName());
    }

    @Override // android.widget.ProgressBar, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(AbsSeekBar.class.getName());
        if (isEnabled()) {
            int progress = getProgress();
            if (progress > 0) {
                accessibilityNodeInfo.addAction(8192);
            }
            if (progress < getMax()) {
                accessibilityNodeInfo.addAction(4096);
            }
        }
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        if (super.performAccessibilityAction(i, bundle)) {
            return true;
        }
        if (!isEnabled()) {
            return false;
        }
        int progress = getProgress();
        int iMax = Math.max(1, Math.round(getMax() / 5.0f));
        if (i == 4096) {
            if (progress >= getMax()) {
                return false;
            }
            setProgress(progress + iMax, true);
            onKeyChange();
            return true;
        }
        if (i != 8192 || progress <= 0) {
            return false;
        }
        setProgress(progress - iMax, true);
        onKeyChange();
        return true;
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        int max = getMax();
        float progress = max > 0 ? getProgress() / max : 0.0f;
        Drawable drawable = this.mThumb;
        if (drawable != null) {
            setThumbPos(getWidth(), drawable, progress, Integer.MIN_VALUE);
            invalidate();
        }
    }
}
