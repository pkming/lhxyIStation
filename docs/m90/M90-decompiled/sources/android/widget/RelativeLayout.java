package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Pools;
import android.util.SparseArray;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;
import com.android.internal.R;
import com.google.android.material.badge.BadgeDrawable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/* JADX INFO: loaded from: classes.dex */
@RemoteViews.RemoteView
public class RelativeLayout extends ViewGroup {
    public static final int ABOVE = 2;
    public static final int ALIGN_BASELINE = 4;
    public static final int ALIGN_BOTTOM = 8;
    public static final int ALIGN_END = 19;
    public static final int ALIGN_LEFT = 5;
    public static final int ALIGN_PARENT_BOTTOM = 12;
    public static final int ALIGN_PARENT_END = 21;
    public static final int ALIGN_PARENT_LEFT = 9;
    public static final int ALIGN_PARENT_RIGHT = 11;
    public static final int ALIGN_PARENT_START = 20;
    public static final int ALIGN_PARENT_TOP = 10;
    public static final int ALIGN_RIGHT = 7;
    public static final int ALIGN_START = 18;
    public static final int ALIGN_TOP = 6;
    public static final int BELOW = 3;
    public static final int CENTER_HORIZONTAL = 14;
    public static final int CENTER_IN_PARENT = 13;
    public static final int CENTER_VERTICAL = 15;
    private static final int DEFAULT_WIDTH = 65536;
    public static final int END_OF = 17;
    public static final int LEFT_OF = 0;
    public static final int RIGHT_OF = 1;
    public static final int START_OF = 16;
    public static final int TRUE = -1;
    private static final int VERB_COUNT = 22;
    private boolean mAllowBrokenMeasureSpecs;
    private View mBaselineView;
    private final Rect mContentBounds;
    private boolean mDirtyHierarchy;
    private final DependencyGraph mGraph;
    private int mGravity;
    private boolean mHasBaselineAlignedChild;
    private int mIgnoreGravity;
    private boolean mMeasureVerticalWithPaddingMargin;
    private final Rect mSelfBounds;
    private View[] mSortedHorizontalChildren;
    private View[] mSortedVerticalChildren;
    private SortedSet<View> mTopToBottomLeftToRightSet;
    private static final int[] RULES_VERTICAL = {2, 3, 4, 6, 8};
    private static final int[] RULES_HORIZONTAL = {0, 1, 5, 7, 16, 17, 18, 19};

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public RelativeLayout(Context context) {
        super(context);
        this.mBaselineView = null;
        this.mGravity = BadgeDrawable.TOP_START;
        this.mContentBounds = new Rect();
        this.mSelfBounds = new Rect();
        this.mTopToBottomLeftToRightSet = null;
        this.mGraph = new DependencyGraph();
        this.mAllowBrokenMeasureSpecs = false;
        this.mMeasureVerticalWithPaddingMargin = false;
        queryCompatibilityModes(context);
    }

    public RelativeLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mBaselineView = null;
        this.mGravity = BadgeDrawable.TOP_START;
        this.mContentBounds = new Rect();
        this.mSelfBounds = new Rect();
        this.mTopToBottomLeftToRightSet = null;
        this.mGraph = new DependencyGraph();
        this.mAllowBrokenMeasureSpecs = false;
        this.mMeasureVerticalWithPaddingMargin = false;
        initFromAttributes(context, attributeSet);
        queryCompatibilityModes(context);
    }

    public RelativeLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mBaselineView = null;
        this.mGravity = BadgeDrawable.TOP_START;
        this.mContentBounds = new Rect();
        this.mSelfBounds = new Rect();
        this.mTopToBottomLeftToRightSet = null;
        this.mGraph = new DependencyGraph();
        this.mAllowBrokenMeasureSpecs = false;
        this.mMeasureVerticalWithPaddingMargin = false;
        initFromAttributes(context, attributeSet);
        queryCompatibilityModes(context);
    }

    private void initFromAttributes(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.RelativeLayout);
        this.mIgnoreGravity = typedArrayObtainStyledAttributes.getResourceId(1, -1);
        this.mGravity = typedArrayObtainStyledAttributes.getInt(0, this.mGravity);
        typedArrayObtainStyledAttributes.recycle();
    }

    private void queryCompatibilityModes(Context context) {
        int i = context.getApplicationInfo().targetSdkVersion;
        this.mAllowBrokenMeasureSpecs = i <= 17;
        this.mMeasureVerticalWithPaddingMargin = i >= 18;
    }

    @RemotableViewMethod
    public void setIgnoreGravity(int i) {
        this.mIgnoreGravity = i;
    }

    public int getGravity() {
        return this.mGravity;
    }

    @RemotableViewMethod
    public void setGravity(int i) {
        if (this.mGravity != i) {
            if ((8388615 & i) == 0) {
                i |= 8388611;
            }
            if ((i & 112) == 0) {
                i |= 48;
            }
            this.mGravity = i;
            requestLayout();
        }
    }

    @RemotableViewMethod
    public void setHorizontalGravity(int i) {
        int i2 = i & 8388615;
        int i3 = this.mGravity;
        if ((8388615 & i3) != i2) {
            this.mGravity = i2 | ((-8388616) & i3);
            requestLayout();
        }
    }

    @RemotableViewMethod
    public void setVerticalGravity(int i) {
        int i2 = i & 112;
        int i3 = this.mGravity;
        if ((i3 & 112) != i2) {
            this.mGravity = i2 | (i3 & (-113));
            requestLayout();
        }
    }

    @Override // android.view.View
    public int getBaseline() {
        View view = this.mBaselineView;
        return view != null ? view.getBaseline() : super.getBaseline();
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        super.requestLayout();
        this.mDirtyHierarchy = true;
    }

    private void sortChildren() {
        int childCount = getChildCount();
        View[] viewArr = this.mSortedVerticalChildren;
        if (viewArr == null || viewArr.length != childCount) {
            this.mSortedVerticalChildren = new View[childCount];
        }
        View[] viewArr2 = this.mSortedHorizontalChildren;
        if (viewArr2 == null || viewArr2.length != childCount) {
            this.mSortedHorizontalChildren = new View[childCount];
        }
        DependencyGraph dependencyGraph = this.mGraph;
        dependencyGraph.clear();
        for (int i = 0; i < childCount; i++) {
            dependencyGraph.add(getChildAt(i));
        }
        dependencyGraph.getSortedViews(this.mSortedVerticalChildren, RULES_VERTICAL);
        dependencyGraph.getSortedViews(this.mSortedHorizontalChildren, RULES_HORIZONTAL);
    }

    /* JADX WARN: Removed duplicated region for block: B:73:0x012a  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x0146  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x0164  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onMeasure(int r27, int r28) {
        /*
            Method dump skipped, instruction units count: 901
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.RelativeLayout.onMeasure(int, int):void");
    }

    private void alignBaseline(View view, LayoutParams layoutParams) {
        LayoutParams relatedViewParams;
        int[] rules = layoutParams.getRules(getLayoutDirection());
        int relatedViewBaseline = getRelatedViewBaseline(rules, 4);
        if (relatedViewBaseline != -1 && (relatedViewParams = getRelatedViewParams(rules, 4)) != null) {
            int i = relatedViewParams.mTop + relatedViewBaseline;
            int baseline = view.getBaseline();
            if (baseline != -1) {
                i -= baseline;
            }
            int i2 = layoutParams.mBottom - layoutParams.mTop;
            layoutParams.mTop = i;
            layoutParams.mBottom = layoutParams.mTop + i2;
        }
        View view2 = this.mBaselineView;
        if (view2 == null) {
            this.mBaselineView = view;
            return;
        }
        LayoutParams layoutParams2 = (LayoutParams) view2.getLayoutParams();
        if (layoutParams.mTop < layoutParams2.mTop || (layoutParams.mTop == layoutParams2.mTop && layoutParams.mLeft < layoutParams2.mLeft)) {
            this.mBaselineView = view;
        }
    }

    private void measureChild(View view, LayoutParams layoutParams, int i, int i2) {
        view.measure(getChildMeasureSpec(layoutParams.mLeft, layoutParams.mRight, layoutParams.width, layoutParams.leftMargin, layoutParams.rightMargin, this.mPaddingLeft, this.mPaddingRight, i), getChildMeasureSpec(layoutParams.mTop, layoutParams.mBottom, layoutParams.height, layoutParams.topMargin, layoutParams.bottomMargin, this.mPaddingTop, this.mPaddingBottom, i2));
    }

    private void measureChildHorizontal(View view, LayoutParams layoutParams, int i, int i2) {
        int iMakeMeasureSpec;
        int childMeasureSpec = getChildMeasureSpec(layoutParams.mLeft, layoutParams.mRight, layoutParams.width, layoutParams.leftMargin, layoutParams.rightMargin, this.mPaddingLeft, this.mPaddingRight, i);
        int iMax = this.mMeasureVerticalWithPaddingMargin ? Math.max(0, (((i2 - this.mPaddingTop) - this.mPaddingBottom) - layoutParams.topMargin) - layoutParams.bottomMargin) : i2;
        if (i2 < 0 && !this.mAllowBrokenMeasureSpecs) {
            if (layoutParams.height >= 0) {
                iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.height, 1073741824);
            } else {
                iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
            }
        } else if (layoutParams.width == -1) {
            iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(iMax, 1073741824);
        } else {
            iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(iMax, Integer.MIN_VALUE);
        }
        view.measure(childMeasureSpec, iMakeMeasureSpec);
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x0025 A[PHI: r0
      0x0025: PHI (r0v4 int) = (r0v0 int), (r0v2 int), (r0v0 int) binds: [B:25:0x0031, B:30:0x0039, B:19:0x0023] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int getChildMeasureSpec(int r4, int r5, int r6, int r7, int r8, int r9, int r10, int r11) {
        /*
            r3 = this;
            r0 = 1073741824(0x40000000, float:2.0)
            r1 = 0
            if (r11 >= 0) goto L15
            boolean r2 = r3.mAllowBrokenMeasureSpecs
            if (r2 != 0) goto L15
            if (r6 < 0) goto L10
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r6, r0)
            return r4
        L10:
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r1)
            return r4
        L15:
            if (r4 >= 0) goto L19
            int r9 = r9 + r7
            goto L1a
        L19:
            r9 = r4
        L1a:
            if (r5 >= 0) goto L1f
            int r11 = r11 - r10
            int r11 = r11 - r8
            goto L20
        L1f:
            r11 = r5
        L20:
            int r11 = r11 - r9
            if (r4 < 0) goto L27
            if (r5 < 0) goto L27
        L25:
            r6 = r11
            goto L3e
        L27:
            if (r6 < 0) goto L30
            if (r11 < 0) goto L3e
            int r6 = java.lang.Math.min(r11, r6)
            goto L3e
        L30:
            r4 = -1
            if (r6 != r4) goto L34
            goto L25
        L34:
            r4 = -2
            if (r6 != r4) goto L3c
            if (r11 < 0) goto L3c
            r0 = -2147483648(0xffffffff80000000, float:-0.0)
            goto L25
        L3c:
            r6 = r1
            r0 = r6
        L3e:
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r6, r0)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.RelativeLayout.getChildMeasureSpec(int, int, int, int, int, int, int, int):int");
    }

    private boolean positionChildHorizontal(View view, LayoutParams layoutParams, int i, boolean z) {
        int[] rules = layoutParams.getRules(getLayoutDirection());
        if (layoutParams.mLeft >= 0 || layoutParams.mRight < 0) {
            if (layoutParams.mLeft < 0 || layoutParams.mRight >= 0) {
                if (layoutParams.mLeft < 0 && layoutParams.mRight < 0) {
                    if (rules[13] != 0 || rules[14] != 0) {
                        if (!z) {
                            centerHorizontal(view, layoutParams, i);
                        } else {
                            layoutParams.mLeft = this.mPaddingLeft + layoutParams.leftMargin;
                            layoutParams.mRight = layoutParams.mLeft + view.getMeasuredWidth();
                        }
                        return true;
                    }
                    if (isLayoutRtl()) {
                        layoutParams.mRight = (i - this.mPaddingRight) - layoutParams.rightMargin;
                        layoutParams.mLeft = layoutParams.mRight - view.getMeasuredWidth();
                    } else {
                        layoutParams.mLeft = this.mPaddingLeft + layoutParams.leftMargin;
                        layoutParams.mRight = layoutParams.mLeft + view.getMeasuredWidth();
                    }
                }
            } else {
                layoutParams.mRight = layoutParams.mLeft + view.getMeasuredWidth();
            }
        } else {
            layoutParams.mLeft = layoutParams.mRight - view.getMeasuredWidth();
        }
        return rules[21] != 0;
    }

    private boolean positionChildVertical(View view, LayoutParams layoutParams, int i, boolean z) {
        int[] rules = layoutParams.getRules();
        if (layoutParams.mTop >= 0 || layoutParams.mBottom < 0) {
            if (layoutParams.mTop < 0 || layoutParams.mBottom >= 0) {
                if (layoutParams.mTop < 0 && layoutParams.mBottom < 0) {
                    if (rules[13] != 0 || rules[15] != 0) {
                        if (!z) {
                            centerVertical(view, layoutParams, i);
                        } else {
                            layoutParams.mTop = this.mPaddingTop + layoutParams.topMargin;
                            layoutParams.mBottom = layoutParams.mTop + view.getMeasuredHeight();
                        }
                        return true;
                    }
                    layoutParams.mTop = this.mPaddingTop + layoutParams.topMargin;
                    layoutParams.mBottom = layoutParams.mTop + view.getMeasuredHeight();
                }
            } else {
                layoutParams.mBottom = layoutParams.mTop + view.getMeasuredHeight();
            }
        } else {
            layoutParams.mTop = layoutParams.mBottom - view.getMeasuredHeight();
        }
        return rules[12] != 0;
    }

    private void applyHorizontalSizeRules(LayoutParams layoutParams, int i, int[] iArr) {
        layoutParams.mLeft = -1;
        layoutParams.mRight = -1;
        LayoutParams relatedViewParams = getRelatedViewParams(iArr, 0);
        if (relatedViewParams == null) {
            if (layoutParams.alignWithParent && iArr[0] != 0 && i >= 0) {
                layoutParams.mRight = (i - this.mPaddingRight) - layoutParams.rightMargin;
            }
        } else {
            layoutParams.mRight = relatedViewParams.mLeft - (relatedViewParams.leftMargin + layoutParams.rightMargin);
        }
        LayoutParams relatedViewParams2 = getRelatedViewParams(iArr, 1);
        if (relatedViewParams2 == null) {
            if (layoutParams.alignWithParent && iArr[1] != 0) {
                layoutParams.mLeft = this.mPaddingLeft + layoutParams.leftMargin;
            }
        } else {
            layoutParams.mLeft = relatedViewParams2.mRight + relatedViewParams2.rightMargin + layoutParams.leftMargin;
        }
        LayoutParams relatedViewParams3 = getRelatedViewParams(iArr, 5);
        if (relatedViewParams3 == null) {
            if (layoutParams.alignWithParent && iArr[5] != 0) {
                layoutParams.mLeft = this.mPaddingLeft + layoutParams.leftMargin;
            }
        } else {
            layoutParams.mLeft = relatedViewParams3.mLeft + layoutParams.leftMargin;
        }
        LayoutParams relatedViewParams4 = getRelatedViewParams(iArr, 7);
        if (relatedViewParams4 == null) {
            if (layoutParams.alignWithParent && iArr[7] != 0 && i >= 0) {
                layoutParams.mRight = (i - this.mPaddingRight) - layoutParams.rightMargin;
            }
        } else {
            layoutParams.mRight = relatedViewParams4.mRight - layoutParams.rightMargin;
        }
        if (iArr[9] != 0) {
            layoutParams.mLeft = this.mPaddingLeft + layoutParams.leftMargin;
        }
        if (iArr[11] == 0 || i < 0) {
            return;
        }
        layoutParams.mRight = (i - this.mPaddingRight) - layoutParams.rightMargin;
    }

    private void applyVerticalSizeRules(LayoutParams layoutParams, int i) {
        int[] rules = layoutParams.getRules();
        layoutParams.mTop = -1;
        layoutParams.mBottom = -1;
        LayoutParams relatedViewParams = getRelatedViewParams(rules, 2);
        if (relatedViewParams == null) {
            if (layoutParams.alignWithParent && rules[2] != 0 && i >= 0) {
                layoutParams.mBottom = (i - this.mPaddingBottom) - layoutParams.bottomMargin;
            }
        } else {
            layoutParams.mBottom = relatedViewParams.mTop - (relatedViewParams.topMargin + layoutParams.bottomMargin);
        }
        LayoutParams relatedViewParams2 = getRelatedViewParams(rules, 3);
        if (relatedViewParams2 == null) {
            if (layoutParams.alignWithParent && rules[3] != 0) {
                layoutParams.mTop = this.mPaddingTop + layoutParams.topMargin;
            }
        } else {
            layoutParams.mTop = relatedViewParams2.mBottom + relatedViewParams2.bottomMargin + layoutParams.topMargin;
        }
        LayoutParams relatedViewParams3 = getRelatedViewParams(rules, 6);
        if (relatedViewParams3 == null) {
            if (layoutParams.alignWithParent && rules[6] != 0) {
                layoutParams.mTop = this.mPaddingTop + layoutParams.topMargin;
            }
        } else {
            layoutParams.mTop = relatedViewParams3.mTop + layoutParams.topMargin;
        }
        LayoutParams relatedViewParams4 = getRelatedViewParams(rules, 8);
        if (relatedViewParams4 == null) {
            if (layoutParams.alignWithParent && rules[8] != 0 && i >= 0) {
                layoutParams.mBottom = (i - this.mPaddingBottom) - layoutParams.bottomMargin;
            }
        } else {
            layoutParams.mBottom = relatedViewParams4.mBottom - layoutParams.bottomMargin;
        }
        if (rules[10] != 0) {
            layoutParams.mTop = this.mPaddingTop + layoutParams.topMargin;
        }
        if (rules[12] != 0 && i >= 0) {
            layoutParams.mBottom = (i - this.mPaddingBottom) - layoutParams.bottomMargin;
        }
        if (rules[4] != 0) {
            this.mHasBaselineAlignedChild = true;
        }
    }

    private View getRelatedView(int[] iArr, int i) {
        DependencyGraph.Node node;
        int i2 = iArr[i];
        if (i2 == 0 || (node = (DependencyGraph.Node) this.mGraph.mKeyNodes.get(i2)) == null) {
            return null;
        }
        View view = node.view;
        while (view.getVisibility() == 8) {
            DependencyGraph.Node node2 = (DependencyGraph.Node) this.mGraph.mKeyNodes.get(((LayoutParams) view.getLayoutParams()).getRules(view.getLayoutDirection())[i]);
            if (node2 == null) {
                return null;
            }
            view = node2.view;
        }
        return view;
    }

    private LayoutParams getRelatedViewParams(int[] iArr, int i) {
        View relatedView = getRelatedView(iArr, i);
        if (relatedView == null || !(relatedView.getLayoutParams() instanceof LayoutParams)) {
            return null;
        }
        return (LayoutParams) relatedView.getLayoutParams();
    }

    private int getRelatedViewBaseline(int[] iArr, int i) {
        View relatedView = getRelatedView(iArr, i);
        if (relatedView != null) {
            return relatedView.getBaseline();
        }
        return -1;
    }

    private static void centerHorizontal(View view, LayoutParams layoutParams, int i) {
        int measuredWidth = view.getMeasuredWidth();
        int i2 = (i - measuredWidth) / 2;
        layoutParams.mLeft = i2;
        layoutParams.mRight = i2 + measuredWidth;
    }

    private static void centerVertical(View view, LayoutParams layoutParams, int i) {
        int measuredHeight = view.getMeasuredHeight();
        int i2 = (i - measuredHeight) / 2;
        layoutParams.mTop = i2;
        layoutParams.mBottom = i2 + measuredHeight;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                childAt.layout(layoutParams.mLeft, layoutParams.mTop, layoutParams.mRight, layoutParams.mBottom);
            }
        }
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
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
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (this.mTopToBottomLeftToRightSet == null) {
            this.mTopToBottomLeftToRightSet = new TreeSet(new TopToBottomLeftToRightComparator());
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            this.mTopToBottomLeftToRightSet.add(getChildAt(i));
        }
        for (View view : this.mTopToBottomLeftToRightSet) {
            if (view.getVisibility() == 0 && view.dispatchPopulateAccessibilityEvent(accessibilityEvent)) {
                this.mTopToBottomLeftToRightSet.clear();
                return true;
            }
        }
        this.mTopToBottomLeftToRightSet.clear();
        return false;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(RelativeLayout.class.getName());
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(RelativeLayout.class.getName());
    }

    private class TopToBottomLeftToRightComparator implements Comparator<View> {
        private TopToBottomLeftToRightComparator() {
        }

        @Override // java.util.Comparator
        public int compare(View view, View view2) {
            int top = view.getTop() - view2.getTop();
            if (top != 0) {
                return top;
            }
            int left = view.getLeft() - view2.getLeft();
            if (left != 0) {
                return left;
            }
            int height = view.getHeight() - view2.getHeight();
            if (height != 0) {
                return height;
            }
            int width = view.getWidth() - view2.getWidth();
            if (width != 0) {
                return width;
            }
            return 0;
        }
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        @ViewDebug.ExportedProperty(category = "layout")
        public boolean alignWithParent;
        private int mBottom;
        private int mEnd;
        private int[] mInitialRules;
        private boolean mIsRtlCompatibilityMode;
        private int mLeft;
        private int mRight;

        @ViewDebug.ExportedProperty(category = "layout", indexMapping = {@ViewDebug.IntToString(from = 2, to = "above"), @ViewDebug.IntToString(from = 4, to = "alignBaseline"), @ViewDebug.IntToString(from = 8, to = "alignBottom"), @ViewDebug.IntToString(from = 5, to = "alignLeft"), @ViewDebug.IntToString(from = 12, to = "alignParentBottom"), @ViewDebug.IntToString(from = 9, to = "alignParentLeft"), @ViewDebug.IntToString(from = 11, to = "alignParentRight"), @ViewDebug.IntToString(from = 10, to = "alignParentTop"), @ViewDebug.IntToString(from = 7, to = "alignRight"), @ViewDebug.IntToString(from = 6, to = "alignTop"), @ViewDebug.IntToString(from = 3, to = "below"), @ViewDebug.IntToString(from = 14, to = "centerHorizontal"), @ViewDebug.IntToString(from = 13, to = "center"), @ViewDebug.IntToString(from = 15, to = "centerVertical"), @ViewDebug.IntToString(from = 0, to = "leftOf"), @ViewDebug.IntToString(from = 1, to = "rightOf"), @ViewDebug.IntToString(from = 18, to = "alignStart"), @ViewDebug.IntToString(from = 19, to = "alignEnd"), @ViewDebug.IntToString(from = 20, to = "alignParentStart"), @ViewDebug.IntToString(from = 21, to = "alignParentEnd"), @ViewDebug.IntToString(from = 16, to = "startOf"), @ViewDebug.IntToString(from = 17, to = "endOf")}, mapping = {@ViewDebug.IntToString(from = -1, to = "true"), @ViewDebug.IntToString(from = 0, to = "false/NO_ID")}, resolveId = true)
        private int[] mRules;
        private boolean mRulesChanged;
        private int mStart;
        private int mTop;

        static /* synthetic */ int access$112(LayoutParams layoutParams, int i) {
            int i2 = layoutParams.mLeft + i;
            layoutParams.mLeft = i2;
            return i2;
        }

        static /* synthetic */ int access$120(LayoutParams layoutParams, int i) {
            int i2 = layoutParams.mLeft - i;
            layoutParams.mLeft = i2;
            return i2;
        }

        static /* synthetic */ int access$212(LayoutParams layoutParams, int i) {
            int i2 = layoutParams.mRight + i;
            layoutParams.mRight = i2;
            return i2;
        }

        static /* synthetic */ int access$220(LayoutParams layoutParams, int i) {
            int i2 = layoutParams.mRight - i;
            layoutParams.mRight = i2;
            return i2;
        }

        static /* synthetic */ int access$312(LayoutParams layoutParams, int i) {
            int i2 = layoutParams.mBottom + i;
            layoutParams.mBottom = i2;
            return i2;
        }

        static /* synthetic */ int access$412(LayoutParams layoutParams, int i) {
            int i2 = layoutParams.mTop + i;
            layoutParams.mTop = i2;
            return i2;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.mRules = new int[22];
            this.mInitialRules = new int[22];
            this.mStart = Integer.MIN_VALUE;
            this.mEnd = Integer.MIN_VALUE;
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.RelativeLayout_Layout);
            this.mIsRtlCompatibilityMode = context.getApplicationInfo().targetSdkVersion < 17 || !context.getApplicationInfo().hasRtlSupport();
            int[] iArr = this.mRules;
            int[] iArr2 = this.mInitialRules;
            int indexCount = typedArrayObtainStyledAttributes.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                int index = typedArrayObtainStyledAttributes.getIndex(i);
                switch (index) {
                    case 0:
                        iArr[0] = typedArrayObtainStyledAttributes.getResourceId(index, 0);
                        break;
                    case 1:
                        iArr[1] = typedArrayObtainStyledAttributes.getResourceId(index, 0);
                        break;
                    case 2:
                        iArr[2] = typedArrayObtainStyledAttributes.getResourceId(index, 0);
                        break;
                    case 3:
                        iArr[3] = typedArrayObtainStyledAttributes.getResourceId(index, 0);
                        break;
                    case 4:
                        iArr[4] = typedArrayObtainStyledAttributes.getResourceId(index, 0);
                        break;
                    case 5:
                        iArr[5] = typedArrayObtainStyledAttributes.getResourceId(index, 0);
                        break;
                    case 6:
                        iArr[6] = typedArrayObtainStyledAttributes.getResourceId(index, 0);
                        break;
                    case 7:
                        iArr[7] = typedArrayObtainStyledAttributes.getResourceId(index, 0);
                        break;
                    case 8:
                        iArr[8] = typedArrayObtainStyledAttributes.getResourceId(index, 0);
                        break;
                    case 9:
                        iArr[9] = typedArrayObtainStyledAttributes.getBoolean(index, false) ? -1 : 0;
                        break;
                    case 10:
                        iArr[10] = typedArrayObtainStyledAttributes.getBoolean(index, false) ? -1 : 0;
                        break;
                    case 11:
                        iArr[11] = typedArrayObtainStyledAttributes.getBoolean(index, false) ? -1 : 0;
                        break;
                    case 12:
                        iArr[12] = typedArrayObtainStyledAttributes.getBoolean(index, false) ? -1 : 0;
                        break;
                    case 13:
                        iArr[13] = typedArrayObtainStyledAttributes.getBoolean(index, false) ? -1 : 0;
                        break;
                    case 14:
                        iArr[14] = typedArrayObtainStyledAttributes.getBoolean(index, false) ? -1 : 0;
                        break;
                    case 15:
                        iArr[15] = typedArrayObtainStyledAttributes.getBoolean(index, false) ? -1 : 0;
                        break;
                    case 16:
                        this.alignWithParent = typedArrayObtainStyledAttributes.getBoolean(index, false);
                        break;
                    case 17:
                        iArr[16] = typedArrayObtainStyledAttributes.getResourceId(index, 0);
                        break;
                    case 18:
                        iArr[17] = typedArrayObtainStyledAttributes.getResourceId(index, 0);
                        break;
                    case 19:
                        iArr[18] = typedArrayObtainStyledAttributes.getResourceId(index, 0);
                        break;
                    case 20:
                        iArr[19] = typedArrayObtainStyledAttributes.getResourceId(index, 0);
                        break;
                    case 21:
                        iArr[20] = typedArrayObtainStyledAttributes.getBoolean(index, false) ? -1 : 0;
                        break;
                    case 22:
                        iArr[21] = typedArrayObtainStyledAttributes.getBoolean(index, false) ? -1 : 0;
                        break;
                }
            }
            this.mRulesChanged = true;
            System.arraycopy(iArr, 0, iArr2, 0, 22);
            typedArrayObtainStyledAttributes.recycle();
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.mRules = new int[22];
            this.mInitialRules = new int[22];
            this.mStart = Integer.MIN_VALUE;
            this.mEnd = Integer.MIN_VALUE;
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.mRules = new int[22];
            this.mInitialRules = new int[22];
            this.mStart = Integer.MIN_VALUE;
            this.mEnd = Integer.MIN_VALUE;
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.mRules = new int[22];
            this.mInitialRules = new int[22];
            this.mStart = Integer.MIN_VALUE;
            this.mEnd = Integer.MIN_VALUE;
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((ViewGroup.MarginLayoutParams) layoutParams);
            int[] iArr = new int[22];
            this.mRules = iArr;
            this.mInitialRules = new int[22];
            this.mStart = Integer.MIN_VALUE;
            this.mEnd = Integer.MIN_VALUE;
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
            this.mIsRtlCompatibilityMode = layoutParams.mIsRtlCompatibilityMode;
            this.mRulesChanged = layoutParams.mRulesChanged;
            this.alignWithParent = layoutParams.alignWithParent;
            System.arraycopy(layoutParams.mRules, 0, iArr, 0, 22);
            System.arraycopy(layoutParams.mInitialRules, 0, this.mInitialRules, 0, 22);
        }

        @Override // android.view.ViewGroup.LayoutParams
        public String debug(String str) {
            return str + "ViewGroup.LayoutParams={ width=" + sizeToString(this.width) + ", height=" + sizeToString(this.height) + " }";
        }

        public void addRule(int i) {
            this.mRules[i] = -1;
            this.mInitialRules[i] = -1;
            this.mRulesChanged = true;
        }

        public void addRule(int i, int i2) {
            this.mRules[i] = i2;
            this.mInitialRules[i] = i2;
            this.mRulesChanged = true;
        }

        public void removeRule(int i) {
            this.mRules[i] = 0;
            this.mInitialRules[i] = 0;
            this.mRulesChanged = true;
        }

        private boolean hasRelativeRules() {
            int[] iArr = this.mInitialRules;
            return (iArr[16] == 0 && iArr[17] == 0 && iArr[18] == 0 && iArr[19] == 0 && iArr[20] == 0 && iArr[21] == 0) ? false : true;
        }

        private void resolveRules(int i) {
            char c = i == 1 ? (char) 1 : (char) 0;
            System.arraycopy(this.mInitialRules, 0, this.mRules, 0, 22);
            if (this.mIsRtlCompatibilityMode) {
                int[] iArr = this.mRules;
                if (iArr[18] != 0) {
                    if (iArr[5] == 0) {
                        iArr[5] = iArr[18];
                    }
                    iArr[18] = 0;
                }
                if (iArr[19] != 0) {
                    if (iArr[7] == 0) {
                        iArr[7] = iArr[19];
                    }
                    iArr[19] = 0;
                }
                if (iArr[16] != 0) {
                    if (iArr[0] == 0) {
                        iArr[0] = iArr[16];
                    }
                    iArr[16] = 0;
                }
                if (iArr[17] != 0) {
                    if (iArr[1] == 0) {
                        iArr[1] = iArr[17];
                    }
                    iArr[17] = 0;
                }
                if (iArr[20] != 0) {
                    if (iArr[9] == 0) {
                        iArr[9] = iArr[20];
                    }
                    iArr[20] = 0;
                }
                if (iArr[11] == 0) {
                    if (iArr[11] == 0) {
                        iArr[11] = iArr[21];
                    }
                    iArr[21] = 0;
                }
            } else {
                int[] iArr2 = this.mRules;
                if ((iArr2[18] != 0 || iArr2[19] != 0) && (iArr2[5] != 0 || iArr2[7] != 0)) {
                    iArr2[5] = 0;
                    iArr2[7] = 0;
                }
                if (iArr2[18] != 0) {
                    iArr2[c != 0 ? (char) 7 : (char) 5] = iArr2[18];
                    iArr2[18] = 0;
                }
                if (iArr2[19] != 0) {
                    iArr2[c != 0 ? (char) 5 : (char) 7] = iArr2[19];
                    iArr2[19] = 0;
                }
                if ((iArr2[16] != 0 || iArr2[17] != 0) && (iArr2[0] != 0 || iArr2[1] != 0)) {
                    iArr2[0] = 0;
                    iArr2[1] = 0;
                }
                if (iArr2[16] != 0) {
                    iArr2[c] = iArr2[16];
                    iArr2[16] = 0;
                }
                if (iArr2[17] != 0) {
                    iArr2[c ^ 1] = iArr2[17];
                    iArr2[17] = 0;
                }
                if ((iArr2[20] != 0 || iArr2[21] != 0) && (iArr2[9] != 0 || iArr2[11] != 0)) {
                    iArr2[9] = 0;
                    iArr2[11] = 0;
                }
                if (iArr2[20] != 0) {
                    iArr2[c != 0 ? (char) 11 : '\t'] = iArr2[20];
                    iArr2[20] = 0;
                }
                if (iArr2[21] != 0) {
                    iArr2[c == 0 ? (char) 11 : '\t'] = iArr2[21];
                    iArr2[21] = 0;
                }
            }
            this.mRulesChanged = false;
        }

        public int[] getRules(int i) {
            if (hasRelativeRules() && (this.mRulesChanged || i != getLayoutDirection())) {
                resolveRules(i);
                if (i != getLayoutDirection()) {
                    setLayoutDirection(i);
                }
            }
            return this.mRules;
        }

        public int[] getRules() {
            return this.mRules;
        }

        @Override // android.view.ViewGroup.MarginLayoutParams, android.view.ViewGroup.LayoutParams
        public void resolveLayoutDirection(int i) {
            if (isLayoutRtl()) {
                int i2 = this.mStart;
                if (i2 != Integer.MIN_VALUE) {
                    this.mRight = i2;
                }
                int i3 = this.mEnd;
                if (i3 != Integer.MIN_VALUE) {
                    this.mLeft = i3;
                }
            } else {
                int i4 = this.mStart;
                if (i4 != Integer.MIN_VALUE) {
                    this.mLeft = i4;
                }
                int i5 = this.mEnd;
                if (i5 != Integer.MIN_VALUE) {
                    this.mRight = i5;
                }
            }
            if (hasRelativeRules() && i != getLayoutDirection()) {
                resolveRules(i);
            }
            super.resolveLayoutDirection(i);
        }
    }

    private static class DependencyGraph {
        private SparseArray<Node> mKeyNodes;
        private ArrayList<Node> mNodes;
        private ArrayDeque<Node> mRoots;

        private DependencyGraph() {
            this.mNodes = new ArrayList<>();
            this.mKeyNodes = new SparseArray<>();
            this.mRoots = new ArrayDeque<>();
        }

        void clear() {
            ArrayList<Node> arrayList = this.mNodes;
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                arrayList.get(i).release();
            }
            arrayList.clear();
            this.mKeyNodes.clear();
            this.mRoots.clear();
        }

        void add(View view) {
            int id = view.getId();
            Node nodeAcquire = Node.acquire(view);
            if (id != -1) {
                this.mKeyNodes.put(id, nodeAcquire);
            }
            this.mNodes.add(nodeAcquire);
        }

        void getSortedViews(View[] viewArr, int... iArr) {
            ArrayDeque<Node> arrayDequeFindRoots = findRoots(iArr);
            int i = 0;
            while (true) {
                Node nodePollLast = arrayDequeFindRoots.pollLast();
                if (nodePollLast == null) {
                    break;
                }
                View view = nodePollLast.view;
                int id = view.getId();
                int i2 = i + 1;
                viewArr[i] = view;
                ArrayMap<Node, DependencyGraph> arrayMap = nodePollLast.dependents;
                int size = arrayMap.size();
                for (int i3 = 0; i3 < size; i3++) {
                    Node nodeKeyAt = arrayMap.keyAt(i3);
                    SparseArray<Node> sparseArray = nodeKeyAt.dependencies;
                    sparseArray.remove(id);
                    if (sparseArray.size() == 0) {
                        arrayDequeFindRoots.add(nodeKeyAt);
                    }
                }
                i = i2;
            }
            if (i < viewArr.length) {
                throw new IllegalStateException("Circular dependencies cannot exist in RelativeLayout");
            }
        }

        private ArrayDeque<Node> findRoots(int[] iArr) {
            Node node;
            SparseArray<Node> sparseArray = this.mKeyNodes;
            ArrayList<Node> arrayList = this.mNodes;
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                Node node2 = arrayList.get(i);
                node2.dependents.clear();
                node2.dependencies.clear();
            }
            for (int i2 = 0; i2 < size; i2++) {
                Node node3 = arrayList.get(i2);
                int[] iArr2 = ((LayoutParams) node3.view.getLayoutParams()).mRules;
                for (int i3 : iArr) {
                    int i4 = iArr2[i3];
                    if (i4 > 0 && (node = sparseArray.get(i4)) != null && node != node3) {
                        node.dependents.put(node3, this);
                        node3.dependencies.put(i4, node);
                    }
                }
            }
            ArrayDeque<Node> arrayDeque = this.mRoots;
            arrayDeque.clear();
            for (int i5 = 0; i5 < size; i5++) {
                Node node4 = arrayList.get(i5);
                if (node4.dependencies.size() == 0) {
                    arrayDeque.addLast(node4);
                }
            }
            return arrayDeque;
        }

        static class Node {
            private static final int POOL_LIMIT = 100;
            private static final Pools.SynchronizedPool<Node> sPool = new Pools.SynchronizedPool<>(100);
            View view;
            final ArrayMap<Node, DependencyGraph> dependents = new ArrayMap<>();
            final SparseArray<Node> dependencies = new SparseArray<>();

            Node() {
            }

            static Node acquire(View view) {
                Node nodeAcquire = sPool.acquire();
                if (nodeAcquire == null) {
                    nodeAcquire = new Node();
                }
                nodeAcquire.view = view;
                return nodeAcquire;
            }

            void release() {
                this.view = null;
                this.dependents.clear();
                this.dependencies.clear();
                sPool.release(this);
            }
        }
    }
}
