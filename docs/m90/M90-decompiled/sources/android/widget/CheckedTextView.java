package android.widget;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewDebug;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/* JADX INFO: loaded from: classes.dex */
public class CheckedTextView extends TextView implements Checkable {
    private static final int[] CHECKED_STATE_SET = {R.attr.state_checked};
    private int mBasePadding;
    private Drawable mCheckMarkDrawable;
    private int mCheckMarkResource;
    private int mCheckMarkWidth;
    private boolean mChecked;
    private boolean mNeedRequestlayout;

    public CheckedTextView(Context context) {
        this(context, null);
    }

    public CheckedTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.checkedTextViewStyle);
    }

    public CheckedTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, com.android.internal.R.styleable.CheckedTextView, i, 0);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(1);
        if (drawable != null) {
            setCheckMarkDrawable(drawable);
        }
        setChecked(typedArrayObtainStyledAttributes.getBoolean(0, false));
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // android.widget.Checkable
    public void toggle() {
        setChecked(!this.mChecked);
    }

    @Override // android.widget.Checkable
    @ViewDebug.ExportedProperty
    public boolean isChecked() {
        return this.mChecked;
    }

    @Override // android.widget.Checkable
    public void setChecked(boolean z) {
        if (this.mChecked != z) {
            this.mChecked = z;
            refreshDrawableState();
            notifyViewAccessibilityStateChangedIfNeeded(0);
        }
    }

    public void setCheckMarkDrawable(int i) {
        if (i == 0 || i != this.mCheckMarkResource) {
            this.mCheckMarkResource = i;
            setCheckMarkDrawable(i != 0 ? getResources().getDrawable(this.mCheckMarkResource) : null);
        }
    }

    public void setCheckMarkDrawable(Drawable drawable) {
        Drawable drawable2 = this.mCheckMarkDrawable;
        if (drawable2 != null) {
            drawable2.setCallback(null);
            unscheduleDrawable(this.mCheckMarkDrawable);
        }
        this.mNeedRequestlayout = drawable != this.mCheckMarkDrawable;
        if (drawable != null) {
            drawable.setCallback(this);
            drawable.setVisible(getVisibility() == 0, false);
            drawable.setState(CHECKED_STATE_SET);
            setMinHeight(drawable.getIntrinsicHeight());
            this.mCheckMarkWidth = drawable.getIntrinsicWidth();
            drawable.setState(getDrawableState());
        } else {
            this.mCheckMarkWidth = 0;
        }
        this.mCheckMarkDrawable = drawable;
        resolvePadding();
    }

    public Drawable getCheckMarkDrawable() {
        return this.mCheckMarkDrawable;
    }

    @Override // android.view.View
    protected void internalSetPadding(int i, int i2, int i3, int i4) {
        super.internalSetPadding(i, i2, i3, i4);
        setBasePadding(isLayoutRtl());
    }

    @Override // android.widget.TextView, android.view.View
    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        updatePadding();
    }

    private void updatePadding() {
        resetPaddingToInitialValues();
        int i = this.mCheckMarkDrawable != null ? this.mCheckMarkWidth + this.mBasePadding : this.mBasePadding;
        if (isLayoutRtl()) {
            this.mNeedRequestlayout |= this.mPaddingLeft != i;
            this.mPaddingLeft = i;
        } else {
            this.mNeedRequestlayout |= this.mPaddingRight != i;
            this.mPaddingRight = i;
        }
        if (this.mNeedRequestlayout) {
            requestLayout();
            this.mNeedRequestlayout = false;
        }
    }

    private void setBasePadding(boolean z) {
        if (z) {
            this.mBasePadding = this.mPaddingLeft;
        } else {
            this.mBasePadding = this.mPaddingRight;
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        int i;
        int i2;
        super.onDraw(canvas);
        Drawable drawable = this.mCheckMarkDrawable;
        if (drawable != null) {
            int gravity = getGravity() & 112;
            int intrinsicHeight = drawable.getIntrinsicHeight();
            int height = 0;
            if (gravity == 16) {
                height = (getHeight() - intrinsicHeight) / 2;
            } else if (gravity == 80) {
                height = getHeight() - intrinsicHeight;
            }
            boolean zIsLayoutRtl = isLayoutRtl();
            int width = getWidth();
            int i3 = intrinsicHeight + height;
            if (zIsLayoutRtl) {
                i2 = this.mBasePadding;
                i = this.mCheckMarkWidth + i2;
            } else {
                i = width - this.mBasePadding;
                i2 = i - this.mCheckMarkWidth;
            }
            drawable.setBounds(this.mScrollX + i2, height, this.mScrollX + i, i3);
            drawable.draw(canvas);
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected int[] onCreateDrawableState(int i) {
        int[] iArrOnCreateDrawableState = super.onCreateDrawableState(i + 1);
        if (isChecked()) {
            mergeDrawableStates(iArrOnCreateDrawableState, CHECKED_STATE_SET);
        }
        return iArrOnCreateDrawableState;
    }

    @Override // android.widget.TextView, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mCheckMarkDrawable != null) {
            this.mCheckMarkDrawable.setState(getDrawableState());
            invalidate();
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(CheckedTextView.class.getName());
        accessibilityEvent.setChecked(this.mChecked);
    }

    @Override // android.widget.TextView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(CheckedTextView.class.getName());
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(this.mChecked);
    }
}
