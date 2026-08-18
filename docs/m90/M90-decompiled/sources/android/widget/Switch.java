package android.widget;

import android.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.AllCapsTransformationMethod;
import android.text.method.TransformationMethod2;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/* JADX INFO: loaded from: classes.dex */
public class Switch extends CompoundButton {
    private static final int[] CHECKED_STATE_SET = {R.attr.state_checked};
    private static final int MONOSPACE = 3;
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int TOUCH_MODE_DOWN = 1;
    private static final int TOUCH_MODE_DRAGGING = 2;
    private static final int TOUCH_MODE_IDLE = 0;
    private int mMinFlingVelocity;
    private Layout mOffLayout;
    private Layout mOnLayout;
    private int mSwitchBottom;
    private int mSwitchHeight;
    private int mSwitchLeft;
    private int mSwitchMinWidth;
    private int mSwitchPadding;
    private int mSwitchRight;
    private int mSwitchTop;
    private TransformationMethod2 mSwitchTransformationMethod;
    private int mSwitchWidth;
    private final Rect mTempRect;
    private ColorStateList mTextColors;
    private CharSequence mTextOff;
    private CharSequence mTextOn;
    private TextPaint mTextPaint;
    private Drawable mThumbDrawable;
    private float mThumbPosition;
    private int mThumbTextPadding;
    private int mThumbWidth;
    private int mTouchMode;
    private int mTouchSlop;
    private float mTouchX;
    private float mTouchY;
    private Drawable mTrackDrawable;
    private VelocityTracker mVelocityTracker;

    public Switch(Context context) {
        this(context, null);
    }

    public Switch(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16843807);
    }

    public Switch(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mTempRect = new Rect();
        this.mTextPaint = new TextPaint(1);
        Resources resources = getResources();
        this.mTextPaint.density = resources.getDisplayMetrics().density;
        this.mTextPaint.setCompatibilityScaling(resources.getCompatibilityInfo().applicationScale);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, com.android.internal.R.styleable.Switch, i, 0);
        this.mThumbDrawable = typedArrayObtainStyledAttributes.getDrawable(2);
        this.mTrackDrawable = typedArrayObtainStyledAttributes.getDrawable(4);
        this.mTextOn = typedArrayObtainStyledAttributes.getText(0);
        this.mTextOff = typedArrayObtainStyledAttributes.getText(1);
        this.mThumbTextPadding = typedArrayObtainStyledAttributes.getDimensionPixelSize(7, 0);
        this.mSwitchMinWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(5, 0);
        this.mSwitchPadding = typedArrayObtainStyledAttributes.getDimensionPixelSize(6, 0);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(3, 0);
        if (resourceId != 0) {
            setSwitchTextAppearance(context, resourceId);
        }
        typedArrayObtainStyledAttributes.recycle();
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        refreshDrawableState();
        setChecked(isChecked());
    }

    public void setSwitchTextAppearance(Context context, int i) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(i, com.android.internal.R.styleable.TextAppearance);
        ColorStateList colorStateList = typedArrayObtainStyledAttributes.getColorStateList(3);
        if (colorStateList != null) {
            this.mTextColors = colorStateList;
        } else {
            this.mTextColors = getTextColors();
        }
        int dimensionPixelSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(0, 0);
        if (dimensionPixelSize != 0) {
            float f = dimensionPixelSize;
            if (f != this.mTextPaint.getTextSize()) {
                this.mTextPaint.setTextSize(f);
                requestLayout();
            }
        }
        setSwitchTypefaceByIndex(typedArrayObtainStyledAttributes.getInt(1, -1), typedArrayObtainStyledAttributes.getInt(2, -1));
        if (typedArrayObtainStyledAttributes.getBoolean(11, false)) {
            AllCapsTransformationMethod allCapsTransformationMethod = new AllCapsTransformationMethod(getContext());
            this.mSwitchTransformationMethod = allCapsTransformationMethod;
            allCapsTransformationMethod.setLengthChangesAllowed(true);
        } else {
            this.mSwitchTransformationMethod = null;
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    private void setSwitchTypefaceByIndex(int i, int i2) {
        Typeface typeface;
        if (i == 1) {
            typeface = Typeface.SANS_SERIF;
        } else if (i == 2) {
            typeface = Typeface.SERIF;
        } else {
            typeface = i != 3 ? null : Typeface.MONOSPACE;
        }
        setSwitchTypeface(typeface, i2);
    }

    public void setSwitchTypeface(Typeface typeface, int i) {
        Typeface typefaceCreate;
        if (i > 0) {
            if (typeface == null) {
                typefaceCreate = Typeface.defaultFromStyle(i);
            } else {
                typefaceCreate = Typeface.create(typeface, i);
            }
            setSwitchTypeface(typefaceCreate);
            int i2 = (~(typefaceCreate != null ? typefaceCreate.getStyle() : 0)) & i;
            this.mTextPaint.setFakeBoldText((i2 & 1) != 0);
            this.mTextPaint.setTextSkewX((i2 & 2) != 0 ? -0.25f : 0.0f);
            return;
        }
        this.mTextPaint.setFakeBoldText(false);
        this.mTextPaint.setTextSkewX(0.0f);
        setSwitchTypeface(typeface);
    }

    public void setSwitchTypeface(Typeface typeface) {
        if (this.mTextPaint.getTypeface() != typeface) {
            this.mTextPaint.setTypeface(typeface);
            requestLayout();
            invalidate();
        }
    }

    public void setSwitchPadding(int i) {
        this.mSwitchPadding = i;
        requestLayout();
    }

    public int getSwitchPadding() {
        return this.mSwitchPadding;
    }

    public void setSwitchMinWidth(int i) {
        this.mSwitchMinWidth = i;
        requestLayout();
    }

    public int getSwitchMinWidth() {
        return this.mSwitchMinWidth;
    }

    public void setThumbTextPadding(int i) {
        this.mThumbTextPadding = i;
        requestLayout();
    }

    public int getThumbTextPadding() {
        return this.mThumbTextPadding;
    }

    public void setTrackDrawable(Drawable drawable) {
        this.mTrackDrawable = drawable;
        requestLayout();
    }

    public void setTrackResource(int i) {
        setTrackDrawable(getContext().getResources().getDrawable(i));
    }

    public Drawable getTrackDrawable() {
        return this.mTrackDrawable;
    }

    public void setThumbDrawable(Drawable drawable) {
        this.mThumbDrawable = drawable;
        requestLayout();
    }

    public void setThumbResource(int i) {
        setThumbDrawable(getContext().getResources().getDrawable(i));
    }

    public Drawable getThumbDrawable() {
        return this.mThumbDrawable;
    }

    public CharSequence getTextOn() {
        return this.mTextOn;
    }

    public void setTextOn(CharSequence charSequence) {
        this.mTextOn = charSequence;
        requestLayout();
    }

    public CharSequence getTextOff() {
        return this.mTextOff;
    }

    public void setTextOff(CharSequence charSequence) {
        this.mTextOff = charSequence;
        requestLayout();
    }

    @Override // android.widget.TextView, android.view.View
    public void onMeasure(int i, int i2) {
        if (this.mOnLayout == null) {
            this.mOnLayout = makeLayout(this.mTextOn);
        }
        if (this.mOffLayout == null) {
            this.mOffLayout = makeLayout(this.mTextOff);
        }
        this.mTrackDrawable.getPadding(this.mTempRect);
        int iMax = Math.max(this.mOnLayout.getWidth(), this.mOffLayout.getWidth());
        int iMax2 = Math.max(this.mSwitchMinWidth, (iMax * 2) + (this.mThumbTextPadding * 4) + this.mTempRect.left + this.mTempRect.right);
        int intrinsicHeight = this.mTrackDrawable.getIntrinsicHeight();
        this.mThumbWidth = iMax + (this.mThumbTextPadding * 2);
        this.mSwitchWidth = iMax2;
        this.mSwitchHeight = intrinsicHeight;
        super.onMeasure(i, i2);
        if (getMeasuredHeight() < intrinsicHeight) {
            setMeasuredDimension(getMeasuredWidthAndState(), intrinsicHeight);
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onPopulateAccessibilityEvent(accessibilityEvent);
        Layout layout = isChecked() ? this.mOnLayout : this.mOffLayout;
        if (layout == null || TextUtils.isEmpty(layout.getText())) {
            return;
        }
        accessibilityEvent.getText().add(layout.getText());
    }

    private Layout makeLayout(CharSequence charSequence) {
        TransformationMethod2 transformationMethod2 = this.mSwitchTransformationMethod;
        if (transformationMethod2 != null) {
            charSequence = transformationMethod2.getTransformation(charSequence, this);
        }
        return new StaticLayout(charSequence, this.mTextPaint, (int) Math.ceil(Layout.getDesiredWidth(r1, r2)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
    }

    private boolean hitThumb(float f, float f2) {
        this.mThumbDrawable.getPadding(this.mTempRect);
        int i = this.mSwitchTop;
        int i2 = this.mTouchSlop;
        int i3 = i - i2;
        int i4 = (this.mSwitchLeft + ((int) (this.mThumbPosition + 0.5f))) - i2;
        int i5 = this.mThumbWidth + i4 + this.mTempRect.left + this.mTempRect.right;
        int i6 = this.mTouchSlop;
        return f > ((float) i4) && f < ((float) (i5 + i6)) && f2 > ((float) i3) && f2 < ((float) (this.mSwitchBottom + i6));
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x0079  */
    @Override // android.widget.TextView, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r7) {
        /*
            r6 = this;
            android.view.VelocityTracker r0 = r6.mVelocityTracker
            r0.addMovement(r7)
            int r0 = r7.getActionMasked()
            r1 = 1
            if (r0 == 0) goto L8a
            r2 = 2
            if (r0 == r1) goto L79
            if (r0 == r2) goto L16
            r3 = 3
            if (r0 == r3) goto L79
            goto La4
        L16:
            int r0 = r6.mTouchMode
            if (r0 == r1) goto L45
            if (r0 == r2) goto L1e
            goto La4
        L1e:
            float r7 = r7.getX()
            float r0 = r6.mTouchX
            float r0 = r7 - r0
            r2 = 0
            float r3 = r6.mThumbPosition
            float r3 = r3 + r0
            int r0 = r6.getThumbScrollRange()
            float r0 = (float) r0
            float r0 = java.lang.Math.min(r3, r0)
            float r0 = java.lang.Math.max(r2, r0)
            float r2 = r6.mThumbPosition
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 == 0) goto L44
            r6.mThumbPosition = r0
            r6.mTouchX = r7
            r6.invalidate()
        L44:
            return r1
        L45:
            float r0 = r7.getX()
            float r3 = r7.getY()
            float r4 = r6.mTouchX
            float r4 = r0 - r4
            float r4 = java.lang.Math.abs(r4)
            int r5 = r6.mTouchSlop
            float r5 = (float) r5
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 > 0) goto L6b
            float r4 = r6.mTouchY
            float r4 = r3 - r4
            float r4 = java.lang.Math.abs(r4)
            int r5 = r6.mTouchSlop
            float r5 = (float) r5
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 <= 0) goto La4
        L6b:
            r6.mTouchMode = r2
            android.view.ViewParent r7 = r6.getParent()
            r7.requestDisallowInterceptTouchEvent(r1)
            r6.mTouchX = r0
            r6.mTouchY = r3
            return r1
        L79:
            int r0 = r6.mTouchMode
            if (r0 != r2) goto L81
            r6.stopDrag(r7)
            return r1
        L81:
            r0 = 0
            r6.mTouchMode = r0
            android.view.VelocityTracker r0 = r6.mVelocityTracker
            r0.clear()
            goto La4
        L8a:
            float r0 = r7.getX()
            float r2 = r7.getY()
            boolean r3 = r6.isEnabled()
            if (r3 == 0) goto La4
            boolean r3 = r6.hitThumb(r0, r2)
            if (r3 == 0) goto La4
            r6.mTouchMode = r1
            r6.mTouchX = r0
            r6.mTouchY = r2
        La4:
            boolean r7 = super.onTouchEvent(r7)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.Switch.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void cancelSuperTouch(MotionEvent motionEvent) {
        MotionEvent motionEventObtain = MotionEvent.obtain(motionEvent);
        motionEventObtain.setAction(3);
        super.onTouchEvent(motionEventObtain);
        motionEventObtain.recycle();
    }

    private void stopDrag(MotionEvent motionEvent) {
        boolean targetCheckedState = false;
        this.mTouchMode = 0;
        boolean z = motionEvent.getAction() == 1 && isEnabled();
        cancelSuperTouch(motionEvent);
        if (z) {
            this.mVelocityTracker.computeCurrentVelocity(1000);
            float xVelocity = this.mVelocityTracker.getXVelocity();
            if (Math.abs(xVelocity) > this.mMinFlingVelocity) {
                if (!isLayoutRtl() ? xVelocity > 0.0f : xVelocity < 0.0f) {
                    targetCheckedState = true;
                }
            } else {
                targetCheckedState = getTargetCheckedState();
            }
            animateThumbToCheckedState(targetCheckedState);
            return;
        }
        animateThumbToCheckedState(isChecked());
    }

    private void animateThumbToCheckedState(boolean z) {
        setChecked(z);
    }

    private boolean getTargetCheckedState() {
        return isLayoutRtl() ? this.mThumbPosition <= ((float) (getThumbScrollRange() / 2)) : this.mThumbPosition >= ((float) (getThumbScrollRange() / 2));
    }

    private void setThumbPosition(boolean z) {
        if (isLayoutRtl()) {
            this.mThumbPosition = z ? 0.0f : getThumbScrollRange();
        } else {
            this.mThumbPosition = z ? getThumbScrollRange() : 0.0f;
        }
    }

    @Override // android.widget.CompoundButton, android.widget.Checkable
    public void setChecked(boolean z) {
        super.setChecked(z);
        setThumbPosition(isChecked());
        invalidate();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int width;
        int paddingLeft;
        int i5;
        int paddingTop;
        int height;
        super.onLayout(z, i, i2, i3, i4);
        setThumbPosition(isChecked());
        if (isLayoutRtl()) {
            paddingLeft = getPaddingLeft();
            width = this.mSwitchWidth + paddingLeft;
        } else {
            width = getWidth() - getPaddingRight();
            paddingLeft = width - this.mSwitchWidth;
        }
        int gravity = getGravity() & 112;
        if (gravity == 16) {
            int paddingTop2 = ((getPaddingTop() + getHeight()) - getPaddingBottom()) / 2;
            i5 = this.mSwitchHeight;
            paddingTop = paddingTop2 - (i5 / 2);
        } else if (gravity != 80) {
            paddingTop = getPaddingTop();
            i5 = this.mSwitchHeight;
        } else {
            height = getHeight() - getPaddingBottom();
            paddingTop = height - this.mSwitchHeight;
            this.mSwitchLeft = paddingLeft;
            this.mSwitchTop = paddingTop;
            this.mSwitchBottom = height;
            this.mSwitchRight = width;
        }
        height = i5 + paddingTop;
        this.mSwitchLeft = paddingLeft;
        this.mSwitchTop = paddingTop;
        this.mSwitchBottom = height;
        this.mSwitchRight = width;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int i = this.mSwitchLeft;
        int i2 = this.mSwitchTop;
        int i3 = this.mSwitchRight;
        int i4 = this.mSwitchBottom;
        this.mTrackDrawable.setBounds(i, i2, i3, i4);
        this.mTrackDrawable.draw(canvas);
        canvas.save();
        this.mTrackDrawable.getPadding(this.mTempRect);
        int i5 = i + this.mTempRect.left;
        int i6 = this.mTempRect.top + i2;
        int i7 = i3 - this.mTempRect.right;
        int i8 = i4 - this.mTempRect.bottom;
        canvas.clipRect(i5, i2, i7, i4);
        this.mThumbDrawable.getPadding(this.mTempRect);
        int i9 = (int) (this.mThumbPosition + 0.5f);
        this.mThumbDrawable.setBounds((i5 - this.mTempRect.left) + i9, i2, i5 + i9 + this.mThumbWidth + this.mTempRect.right, i4);
        this.mThumbDrawable.draw(canvas);
        ColorStateList colorStateList = this.mTextColors;
        if (colorStateList != null) {
            this.mTextPaint.setColor(colorStateList.getColorForState(getDrawableState(), this.mTextColors.getDefaultColor()));
        }
        this.mTextPaint.drawableState = getDrawableState();
        Layout layout = getTargetCheckedState() ? this.mOnLayout : this.mOffLayout;
        if (layout != null) {
            canvas.translate(((r6 + r0) / 2) - (layout.getWidth() / 2), ((i6 + i8) / 2) - (layout.getHeight() / 2));
            layout.draw(canvas);
        }
        canvas.restore();
    }

    @Override // android.widget.CompoundButton, android.widget.TextView
    public int getCompoundPaddingLeft() {
        if (!isLayoutRtl()) {
            return super.getCompoundPaddingLeft();
        }
        int compoundPaddingLeft = super.getCompoundPaddingLeft() + this.mSwitchWidth;
        return !TextUtils.isEmpty(getText()) ? compoundPaddingLeft + this.mSwitchPadding : compoundPaddingLeft;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView
    public int getCompoundPaddingRight() {
        if (isLayoutRtl()) {
            return super.getCompoundPaddingRight();
        }
        int compoundPaddingRight = super.getCompoundPaddingRight() + this.mSwitchWidth;
        return !TextUtils.isEmpty(getText()) ? compoundPaddingRight + this.mSwitchPadding : compoundPaddingRight;
    }

    private int getThumbScrollRange() {
        Drawable drawable = this.mTrackDrawable;
        if (drawable == null) {
            return 0;
        }
        drawable.getPadding(this.mTempRect);
        return ((this.mSwitchWidth - this.mThumbWidth) - this.mTempRect.left) - this.mTempRect.right;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected int[] onCreateDrawableState(int i) {
        int[] iArrOnCreateDrawableState = super.onCreateDrawableState(i + 1);
        if (isChecked()) {
            mergeDrawableStates(iArrOnCreateDrawableState, CHECKED_STATE_SET);
        }
        return iArrOnCreateDrawableState;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] drawableState = getDrawableState();
        Drawable drawable = this.mThumbDrawable;
        if (drawable != null) {
            drawable.setState(drawableState);
        }
        Drawable drawable2 = this.mTrackDrawable;
        if (drawable2 != null) {
            drawable2.setState(drawableState);
        }
        invalidate();
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mThumbDrawable || drawable == this.mTrackDrawable;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        this.mThumbDrawable.jumpToCurrentState();
        this.mTrackDrawable.jumpToCurrentState();
    }

    @Override // android.widget.CompoundButton, android.widget.Button, android.widget.TextView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(Switch.class.getName());
    }

    @Override // android.widget.CompoundButton, android.widget.Button, android.widget.TextView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(Switch.class.getName());
        CharSequence charSequence = isChecked() ? this.mTextOn : this.mTextOff;
        if (TextUtils.isEmpty(charSequence)) {
            return;
        }
        CharSequence text = accessibilityNodeInfo.getText();
        if (TextUtils.isEmpty(text)) {
            accessibilityNodeInfo.setText(charSequence);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(text).append(' ').append(charSequence);
        accessibilityNodeInfo.setText(sb);
    }
}
