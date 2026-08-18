package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;
import com.android.internal.R;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
@RemoteViews.RemoteView
public class FrameLayout extends ViewGroup {
    private static final int DEFAULT_CHILD_GRAVITY = 8388659;

    @ViewDebug.ExportedProperty(category = "drawing")
    private Drawable mForeground;
    boolean mForegroundBoundsChanged;

    @ViewDebug.ExportedProperty(category = "drawing")
    private int mForegroundGravity;

    @ViewDebug.ExportedProperty(category = "drawing")
    protected boolean mForegroundInPadding;

    @ViewDebug.ExportedProperty(category = "padding")
    private int mForegroundPaddingBottom;

    @ViewDebug.ExportedProperty(category = "padding")
    private int mForegroundPaddingLeft;

    @ViewDebug.ExportedProperty(category = "padding")
    private int mForegroundPaddingRight;

    @ViewDebug.ExportedProperty(category = "padding")
    private int mForegroundPaddingTop;
    private final ArrayList<View> mMatchParentChildren;

    @ViewDebug.ExportedProperty(category = "measurement")
    boolean mMeasureAllChildren;
    private final Rect mOverlayBounds;
    private final Rect mSelfBounds;

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public FrameLayout(Context context) {
        super(context);
        this.mMeasureAllChildren = false;
        this.mForegroundPaddingLeft = 0;
        this.mForegroundPaddingTop = 0;
        this.mForegroundPaddingRight = 0;
        this.mForegroundPaddingBottom = 0;
        this.mSelfBounds = new Rect();
        this.mOverlayBounds = new Rect();
        this.mForegroundGravity = 119;
        this.mForegroundInPadding = true;
        this.mForegroundBoundsChanged = false;
        this.mMatchParentChildren = new ArrayList<>(1);
    }

    public FrameLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMeasureAllChildren = false;
        this.mForegroundPaddingLeft = 0;
        this.mForegroundPaddingTop = 0;
        this.mForegroundPaddingRight = 0;
        this.mForegroundPaddingBottom = 0;
        this.mSelfBounds = new Rect();
        this.mOverlayBounds = new Rect();
        this.mForegroundGravity = 119;
        this.mForegroundInPadding = true;
        this.mForegroundBoundsChanged = false;
        this.mMatchParentChildren = new ArrayList<>(1);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.FrameLayout, i, 0);
        this.mForegroundGravity = typedArrayObtainStyledAttributes.getInt(2, this.mForegroundGravity);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(0);
        if (drawable != null) {
            setForeground(drawable);
        }
        if (typedArrayObtainStyledAttributes.getBoolean(1, false)) {
            setMeasureAllChildren(true);
        }
        this.mForegroundInPadding = typedArrayObtainStyledAttributes.getBoolean(3, true);
        typedArrayObtainStyledAttributes.recycle();
    }

    public int getForegroundGravity() {
        return this.mForegroundGravity;
    }

    @RemotableViewMethod
    public void setForegroundGravity(int i) {
        if (this.mForegroundGravity != i) {
            if ((8388615 & i) == 0) {
                i |= 8388611;
            }
            if ((i & 112) == 0) {
                i |= 48;
            }
            this.mForegroundGravity = i;
            if (i == 119 && this.mForeground != null) {
                Rect rect = new Rect();
                if (this.mForeground.getPadding(rect)) {
                    this.mForegroundPaddingLeft = rect.left;
                    this.mForegroundPaddingTop = rect.top;
                    this.mForegroundPaddingRight = rect.right;
                    this.mForegroundPaddingBottom = rect.bottom;
                }
            } else {
                this.mForegroundPaddingLeft = 0;
                this.mForegroundPaddingTop = 0;
                this.mForegroundPaddingRight = 0;
                this.mForegroundPaddingBottom = 0;
            }
            requestLayout();
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mForeground;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.mForeground;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.mForeground;
        if (drawable == null || !drawable.isStateful()) {
            return;
        }
        this.mForeground.setState(getDrawableState());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -1);
    }

    public void setForeground(Drawable drawable) {
        Drawable drawable2 = this.mForeground;
        if (drawable2 != drawable) {
            if (drawable2 != null) {
                drawable2.setCallback(null);
                unscheduleDrawable(this.mForeground);
            }
            this.mForeground = drawable;
            this.mForegroundPaddingLeft = 0;
            this.mForegroundPaddingTop = 0;
            this.mForegroundPaddingRight = 0;
            this.mForegroundPaddingBottom = 0;
            if (drawable != null) {
                setWillNotDraw(false);
                drawable.setCallback(this);
                if (drawable.isStateful()) {
                    drawable.setState(getDrawableState());
                }
                if (this.mForegroundGravity == 119) {
                    Rect rect = new Rect();
                    if (drawable.getPadding(rect)) {
                        this.mForegroundPaddingLeft = rect.left;
                        this.mForegroundPaddingTop = rect.top;
                        this.mForegroundPaddingRight = rect.right;
                        this.mForegroundPaddingBottom = rect.bottom;
                    }
                }
            } else {
                setWillNotDraw(true);
            }
            requestLayout();
            invalidate();
        }
    }

    public Drawable getForeground() {
        return this.mForeground;
    }

    int getPaddingLeftWithForeground() {
        return this.mForegroundInPadding ? Math.max(this.mPaddingLeft, this.mForegroundPaddingLeft) : this.mPaddingLeft + this.mForegroundPaddingLeft;
    }

    int getPaddingRightWithForeground() {
        return this.mForegroundInPadding ? Math.max(this.mPaddingRight, this.mForegroundPaddingRight) : this.mPaddingRight + this.mForegroundPaddingRight;
    }

    private int getPaddingTopWithForeground() {
        return this.mForegroundInPadding ? Math.max(this.mPaddingTop, this.mForegroundPaddingTop) : this.mPaddingTop + this.mForegroundPaddingTop;
    }

    private int getPaddingBottomWithForeground() {
        return this.mForegroundInPadding ? Math.max(this.mPaddingBottom, this.mForegroundPaddingBottom) : this.mPaddingBottom + this.mForegroundPaddingBottom;
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int childMeasureSpec;
        int childMeasureSpec2;
        int childCount = getChildCount();
        boolean z = (View.MeasureSpec.getMode(i) == 1073741824 && View.MeasureSpec.getMode(i2) == 1073741824) ? false : true;
        this.mMatchParentChildren.clear();
        int iCombineMeasuredStates = 0;
        int iMax = 0;
        int iMax2 = 0;
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (this.mMeasureAllChildren || childAt.getVisibility() != 8) {
                measureChildWithMargins(childAt, i, 0, i2, 0);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                iMax2 = Math.max(iMax2, childAt.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin);
                iMax = Math.max(iMax, childAt.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin);
                iCombineMeasuredStates = combineMeasuredStates(iCombineMeasuredStates, childAt.getMeasuredState());
                if (z && (layoutParams.width == -1 || layoutParams.height == -1)) {
                    this.mMatchParentChildren.add(childAt);
                }
            }
        }
        int i4 = iCombineMeasuredStates;
        int paddingLeftWithForeground = iMax2 + getPaddingLeftWithForeground() + getPaddingRightWithForeground();
        int iMax3 = Math.max(iMax + getPaddingTopWithForeground() + getPaddingBottomWithForeground(), getSuggestedMinimumHeight());
        int iMax4 = Math.max(paddingLeftWithForeground, getSuggestedMinimumWidth());
        Drawable foreground = getForeground();
        if (foreground != null) {
            iMax3 = Math.max(iMax3, foreground.getMinimumHeight());
            iMax4 = Math.max(iMax4, foreground.getMinimumWidth());
        }
        setMeasuredDimension(resolveSizeAndState(iMax4, i, i4), resolveSizeAndState(iMax3, i2, i4 << 16));
        int size = this.mMatchParentChildren.size();
        if (size > 1) {
            for (int i5 = 0; i5 < size; i5++) {
                View view = this.mMatchParentChildren.get(i5);
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                if (marginLayoutParams.width == -1) {
                    childMeasureSpec = View.MeasureSpec.makeMeasureSpec((((getMeasuredWidth() - getPaddingLeftWithForeground()) - getPaddingRightWithForeground()) - marginLayoutParams.leftMargin) - marginLayoutParams.rightMargin, 1073741824);
                } else {
                    childMeasureSpec = getChildMeasureSpec(i, getPaddingLeftWithForeground() + getPaddingRightWithForeground() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin, marginLayoutParams.width);
                }
                if (marginLayoutParams.height == -1) {
                    childMeasureSpec2 = View.MeasureSpec.makeMeasureSpec((((getMeasuredHeight() - getPaddingTopWithForeground()) - getPaddingBottomWithForeground()) - marginLayoutParams.topMargin) - marginLayoutParams.bottomMargin, 1073741824);
                } else {
                    childMeasureSpec2 = getChildMeasureSpec(i2, getPaddingTopWithForeground() + getPaddingBottomWithForeground() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin, marginLayoutParams.height);
                }
                view.measure(childMeasureSpec, childMeasureSpec2);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        layoutChildren(i, i2, i3, i4, false);
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x006d  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0081  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void layoutChildren(int r11, int r12, int r13, int r14, boolean r15) {
        /*
            r10 = this;
            int r0 = r10.getChildCount()
            int r1 = r10.getPaddingLeftWithForeground()
            int r13 = r13 - r11
            int r11 = r10.getPaddingRightWithForeground()
            int r13 = r13 - r11
            int r11 = r10.getPaddingTopWithForeground()
            int r14 = r14 - r12
            int r12 = r10.getPaddingBottomWithForeground()
            int r14 = r14 - r12
            r12 = 1
            r10.mForegroundBoundsChanged = r12
            r2 = 0
        L1c:
            if (r2 >= r0) goto L96
            android.view.View r3 = r10.getChildAt(r2)
            int r4 = r3.getVisibility()
            r5 = 8
            if (r4 == r5) goto L93
            android.view.ViewGroup$LayoutParams r4 = r3.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
            int r5 = r3.getMeasuredWidth()
            int r6 = r3.getMeasuredHeight()
            int r7 = r4.gravity
            r8 = -1
            if (r7 != r8) goto L40
            r7 = 8388659(0x800033, float:1.1755015E-38)
        L40:
            int r8 = r10.getLayoutDirection()
            int r8 = android.view.Gravity.getAbsoluteGravity(r7, r8)
            r7 = r7 & 112(0x70, float:1.57E-43)
            r8 = r8 & 7
            if (r8 == r12) goto L5d
            r9 = 5
            if (r8 == r9) goto L52
            goto L59
        L52:
            if (r15 != 0) goto L59
            int r8 = r13 - r5
            int r9 = r4.rightMargin
            goto L68
        L59:
            int r8 = r4.leftMargin
            int r8 = r8 + r1
            goto L69
        L5d:
            int r8 = r13 - r1
            int r8 = r8 - r5
            int r8 = r8 / 2
            int r8 = r8 + r1
            int r9 = r4.leftMargin
            int r8 = r8 + r9
            int r9 = r4.rightMargin
        L68:
            int r8 = r8 - r9
        L69:
            r9 = 16
            if (r7 == r9) goto L81
            r9 = 48
            if (r7 == r9) goto L7d
            r9 = 80
            if (r7 == r9) goto L78
            int r4 = r4.topMargin
            goto L7f
        L78:
            int r7 = r14 - r6
            int r4 = r4.bottomMargin
            goto L8c
        L7d:
            int r4 = r4.topMargin
        L7f:
            int r4 = r4 + r11
            goto L8e
        L81:
            int r7 = r14 - r11
            int r7 = r7 - r6
            int r7 = r7 / 2
            int r7 = r7 + r11
            int r9 = r4.topMargin
            int r7 = r7 + r9
            int r4 = r4.bottomMargin
        L8c:
            int r4 = r7 - r4
        L8e:
            int r5 = r5 + r8
            int r6 = r6 + r4
            r3.layout(r8, r4, r5, r6)
        L93:
            int r2 = r2 + 1
            goto L1c
        L96:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.FrameLayout.layoutChildren(int, int, int, int, boolean):void");
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mForegroundBoundsChanged = true;
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Drawable drawable = this.mForeground;
        if (drawable != null) {
            if (this.mForegroundBoundsChanged) {
                this.mForegroundBoundsChanged = false;
                Rect rect = this.mSelfBounds;
                Rect rect2 = this.mOverlayBounds;
                int i = this.mRight - this.mLeft;
                int i2 = this.mBottom - this.mTop;
                if (this.mForegroundInPadding) {
                    rect.set(0, 0, i, i2);
                } else {
                    rect.set(this.mPaddingLeft, this.mPaddingTop, i - this.mPaddingRight, i2 - this.mPaddingBottom);
                }
                Gravity.apply(this.mForegroundGravity, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), rect, rect2, getLayoutDirection());
                drawable.setBounds(rect2);
            }
            drawable.draw(canvas);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean gatherTransparentRegion(Region region) {
        Drawable drawable;
        boolean zGatherTransparentRegion = super.gatherTransparentRegion(region);
        if (region != null && (drawable = this.mForeground) != null) {
            applyDrawableToTransparentRegion(drawable, region);
        }
        return zGatherTransparentRegion;
    }

    @RemotableViewMethod
    public void setMeasureAllChildren(boolean z) {
        this.mMeasureAllChildren = z;
    }

    @Deprecated
    public boolean getConsiderGoneChildrenWhenMeasuring() {
        return getMeasureAllChildren();
    }

    public boolean getMeasureAllChildren() {
        return this.mMeasureAllChildren;
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(FrameLayout.class.getName());
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(FrameLayout.class.getName());
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public int gravity;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.gravity = -1;
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.FrameLayout_Layout);
            this.gravity = typedArrayObtainStyledAttributes.getInt(0, -1);
            typedArrayObtainStyledAttributes.recycle();
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.gravity = -1;
        }

        public LayoutParams(int i, int i2, int i3) {
            super(i, i2);
            this.gravity = -1;
            this.gravity = i3;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.gravity = -1;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.gravity = -1;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((ViewGroup.MarginLayoutParams) layoutParams);
            this.gravity = -1;
            this.gravity = layoutParams.gravity;
        }
    }
}
