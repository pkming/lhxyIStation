package android.widget;

import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;

/* JADX INFO: loaded from: classes.dex */
public class TabWidget extends LinearLayout implements View.OnFocusChangeListener {
    private final Rect mBounds;
    private boolean mDrawBottomStrips;
    private int[] mImposedTabWidths;
    private int mImposedTabsHeight;
    private Drawable mLeftStrip;
    private Drawable mRightStrip;
    private int mSelectedTab;
    private OnTabSelectionChanged mSelectionChangedListener;
    private boolean mStripMoved;

    interface OnTabSelectionChanged {
        void onTabSelectionChanged(int i, boolean z);
    }

    public TabWidget(Context context) {
        this(context, null);
    }

    public TabWidget(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.tabWidgetStyle);
    }

    public TabWidget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSelectedTab = -1;
        this.mDrawBottomStrips = true;
        this.mBounds = new Rect();
        this.mImposedTabsHeight = -1;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, com.android.internal.R.styleable.TabWidget, i, 0);
        setStripEnabled(typedArrayObtainStyledAttributes.getBoolean(3, true));
        setLeftStripDrawable(typedArrayObtainStyledAttributes.getDrawable(1));
        setRightStripDrawable(typedArrayObtainStyledAttributes.getDrawable(2));
        typedArrayObtainStyledAttributes.recycle();
        initTabWidget();
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        this.mStripMoved = true;
        super.onSizeChanged(i, i2, i3, i4);
    }

    @Override // android.view.ViewGroup
    protected int getChildDrawingOrder(int i, int i2) {
        int i3 = this.mSelectedTab;
        return i3 == -1 ? i2 : i2 == i + (-1) ? i3 : i2 >= i3 ? i2 + 1 : i2;
    }

    private void initTabWidget() {
        setChildrenDrawingOrderEnabled(true);
        Context context = this.mContext;
        Resources resources = context.getResources();
        if (context.getApplicationInfo().targetSdkVersion <= 4) {
            if (this.mLeftStrip == null) {
                this.mLeftStrip = resources.getDrawable(17303061);
            }
            if (this.mRightStrip == null) {
                this.mRightStrip = resources.getDrawable(17303063);
            }
        } else {
            if (this.mLeftStrip == null) {
                this.mLeftStrip = resources.getDrawable(17303060);
            }
            if (this.mRightStrip == null) {
                this.mRightStrip = resources.getDrawable(17303062);
            }
        }
        setFocusable(true);
        setOnFocusChangeListener(this);
    }

    @Override // android.widget.LinearLayout
    void measureChildBeforeLayout(View view, int i, int i2, int i3, int i4, int i5) {
        if (!isMeasureWithLargestChildEnabled() && this.mImposedTabsHeight >= 0) {
            i2 = View.MeasureSpec.makeMeasureSpec(this.mImposedTabWidths[i] + i3, 1073741824);
            i4 = View.MeasureSpec.makeMeasureSpec(this.mImposedTabsHeight, 1073741824);
        }
        super.measureChildBeforeLayout(view, i, i2, i3, i4, i5);
    }

    @Override // android.widget.LinearLayout
    void measureHorizontal(int i, int i2) {
        if (View.MeasureSpec.getMode(i) == 0) {
            super.measureHorizontal(i, i2);
            return;
        }
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        this.mImposedTabsHeight = -1;
        super.measureHorizontal(iMakeMeasureSpec, i2);
        int measuredWidth = getMeasuredWidth() - View.MeasureSpec.getSize(i);
        if (measuredWidth > 0) {
            int childCount = getChildCount();
            int i3 = 0;
            for (int i4 = 0; i4 < childCount; i4++) {
                if (getChildAt(i4).getVisibility() != 8) {
                    i3++;
                }
            }
            if (i3 > 0) {
                int[] iArr = this.mImposedTabWidths;
                if (iArr == null || iArr.length != childCount) {
                    this.mImposedTabWidths = new int[childCount];
                }
                for (int i5 = 0; i5 < childCount; i5++) {
                    View childAt = getChildAt(i5);
                    if (childAt.getVisibility() != 8) {
                        int measuredWidth2 = childAt.getMeasuredWidth();
                        int iMax = Math.max(0, measuredWidth2 - (measuredWidth / i3));
                        this.mImposedTabWidths[i5] = iMax;
                        measuredWidth -= measuredWidth2 - iMax;
                        i3--;
                        this.mImposedTabsHeight = Math.max(this.mImposedTabsHeight, childAt.getMeasuredHeight());
                    }
                }
            }
        }
        super.measureHorizontal(i, i2);
    }

    public View getChildTabViewAt(int i) {
        return getChildAt(i);
    }

    public int getTabCount() {
        return getChildCount();
    }

    @Override // android.widget.LinearLayout
    public void setDividerDrawable(Drawable drawable) {
        super.setDividerDrawable(drawable);
    }

    public void setDividerDrawable(int i) {
        setDividerDrawable(getResources().getDrawable(i));
    }

    public void setLeftStripDrawable(Drawable drawable) {
        this.mLeftStrip = drawable;
        requestLayout();
        invalidate();
    }

    public void setLeftStripDrawable(int i) {
        setLeftStripDrawable(getResources().getDrawable(i));
    }

    public void setRightStripDrawable(Drawable drawable) {
        this.mRightStrip = drawable;
        requestLayout();
        invalidate();
    }

    public void setRightStripDrawable(int i) {
        setRightStripDrawable(getResources().getDrawable(i));
    }

    public void setStripEnabled(boolean z) {
        this.mDrawBottomStrips = z;
        invalidate();
    }

    public boolean isStripEnabled() {
        return this.mDrawBottomStrips;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void childDrawableStateChanged(View view) {
        if (getTabCount() > 0 && view == getChildTabViewAt(this.mSelectedTab)) {
            invalidate();
        }
        super.childDrawableStateChanged(view);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (getTabCount() != 0 && this.mDrawBottomStrips) {
            View childTabViewAt = getChildTabViewAt(this.mSelectedTab);
            Drawable drawable = this.mLeftStrip;
            Drawable drawable2 = this.mRightStrip;
            drawable.setState(childTabViewAt.getDrawableState());
            drawable2.setState(childTabViewAt.getDrawableState());
            if (this.mStripMoved) {
                Rect rect = this.mBounds;
                rect.left = childTabViewAt.getLeft();
                rect.right = childTabViewAt.getRight();
                int height = getHeight();
                drawable.setBounds(Math.min(0, rect.left - drawable.getIntrinsicWidth()), height - drawable.getIntrinsicHeight(), rect.left, height);
                drawable2.setBounds(rect.right, height - drawable2.getIntrinsicHeight(), Math.max(getWidth(), rect.right + drawable2.getIntrinsicWidth()), height);
                this.mStripMoved = false;
            }
            drawable.draw(canvas);
            drawable2.draw(canvas);
        }
    }

    public void setCurrentTab(int i) {
        int i2;
        if (i < 0 || i >= getTabCount() || i == (i2 = this.mSelectedTab)) {
            return;
        }
        if (i2 != -1) {
            getChildTabViewAt(i2).setSelected(false);
        }
        this.mSelectedTab = i;
        getChildTabViewAt(i).setSelected(true);
        this.mStripMoved = true;
        if (isShown()) {
            sendAccessibilityEvent(4);
        }
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        View childTabViewAt;
        onPopulateAccessibilityEvent(accessibilityEvent);
        int i = this.mSelectedTab;
        if (i == -1 || (childTabViewAt = getChildTabViewAt(i)) == null || childTabViewAt.getVisibility() != 0) {
            return false;
        }
        return childTabViewAt.dispatchPopulateAccessibilityEvent(accessibilityEvent);
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(TabWidget.class.getName());
        accessibilityEvent.setItemCount(getTabCount());
        accessibilityEvent.setCurrentItemIndex(this.mSelectedTab);
    }

    @Override // android.view.View, android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEventUnchecked(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() == 8 && isFocused()) {
            accessibilityEvent.recycle();
        } else {
            super.sendAccessibilityEventUnchecked(accessibilityEvent);
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(TabWidget.class.getName());
    }

    public void focusCurrentTab(int i) {
        int i2 = this.mSelectedTab;
        setCurrentTab(i);
        if (i2 != i) {
            getChildTabViewAt(i).requestFocus();
        }
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        int tabCount = getTabCount();
        for (int i = 0; i < tabCount; i++) {
            getChildTabViewAt(i).setEnabled(z);
        }
    }

    @Override // android.view.ViewGroup
    public void addView(View view) {
        if (view.getLayoutParams() == null) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -1, 1.0f);
            layoutParams.setMargins(0, 0, 0, 0);
            view.setLayoutParams(layoutParams);
        }
        view.setFocusable(true);
        view.setClickable(true);
        super.addView(view);
        view.setOnClickListener(new TabClickListener(getTabCount() - 1));
        view.setOnFocusChangeListener(this);
    }

    @Override // android.view.ViewGroup
    public void removeAllViews() {
        super.removeAllViews();
        this.mSelectedTab = -1;
    }

    void setTabSelectionListener(OnTabSelectionChanged onTabSelectionChanged) {
        this.mSelectionChangedListener = onTabSelectionChanged;
    }

    @Override // android.view.View.OnFocusChangeListener
    public void onFocusChange(View view, boolean z) {
        if (view == this && z && getTabCount() > 0) {
            getChildTabViewAt(this.mSelectedTab).requestFocus();
            return;
        }
        if (z) {
            int tabCount = getTabCount();
            for (int i = 0; i < tabCount; i++) {
                if (getChildTabViewAt(i) == view) {
                    setCurrentTab(i);
                    this.mSelectionChangedListener.onTabSelectionChanged(i, false);
                    if (isShown()) {
                        sendAccessibilityEvent(8);
                        return;
                    }
                    return;
                }
            }
        }
    }

    private class TabClickListener implements View.OnClickListener {
        private final int mTabIndex;

        private TabClickListener(int i) {
            this.mTabIndex = i;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            TabWidget.this.mSelectionChangedListener.onTabSelectionChanged(this.mTabIndex, true);
        }
    }
}
