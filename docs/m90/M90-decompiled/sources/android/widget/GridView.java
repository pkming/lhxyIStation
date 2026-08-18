package android.widget;

import android.R;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Trace;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.RemotableViewMethod;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.GridLayoutAnimationController;
import android.widget.AbsListView;
import android.widget.RemoteViews;

/* JADX INFO: loaded from: classes.dex */
@RemoteViews.RemoteView
public class GridView extends AbsListView {
    public static final int AUTO_FIT = -1;
    public static final int NO_STRETCH = 0;
    public static final int STRETCH_COLUMN_WIDTH = 2;
    public static final int STRETCH_SPACING = 1;
    public static final int STRETCH_SPACING_UNIFORM = 3;
    private int mColumnWidth;
    private int mGravity;
    private int mHorizontalSpacing;
    private int mNumColumns;
    private View mReferenceView;
    private View mReferenceViewInSelectedRow;
    private int mRequestedColumnWidth;
    private int mRequestedHorizontalSpacing;
    private int mRequestedNumColumns;
    private int mStretchMode;
    private final Rect mTempRect;
    private int mVerticalSpacing;

    private int getTopSelectionPixel(int i, int i2, int i3) {
        return i3 > 0 ? i + i2 : i;
    }

    public GridView(Context context) {
        this(context, null);
    }

    public GridView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.gridViewStyle);
    }

    public GridView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mNumColumns = -1;
        this.mHorizontalSpacing = 0;
        this.mVerticalSpacing = 0;
        this.mStretchMode = 2;
        this.mReferenceView = null;
        this.mReferenceViewInSelectedRow = null;
        this.mGravity = 8388611;
        this.mTempRect = new Rect();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, com.android.internal.R.styleable.GridView, i, 0);
        setHorizontalSpacing(typedArrayObtainStyledAttributes.getDimensionPixelOffset(1, 0));
        setVerticalSpacing(typedArrayObtainStyledAttributes.getDimensionPixelOffset(2, 0));
        int i2 = typedArrayObtainStyledAttributes.getInt(3, 2);
        if (i2 >= 0) {
            setStretchMode(i2);
        }
        int dimensionPixelOffset = typedArrayObtainStyledAttributes.getDimensionPixelOffset(4, -1);
        if (dimensionPixelOffset > 0) {
            setColumnWidth(dimensionPixelOffset);
        }
        setNumColumns(typedArrayObtainStyledAttributes.getInt(5, 1));
        int i3 = typedArrayObtainStyledAttributes.getInt(0, -1);
        if (i3 >= 0) {
            setGravity(i3);
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // android.widget.AdapterView
    public ListAdapter getAdapter() {
        return this.mAdapter;
    }

    @Override // android.widget.AbsListView
    @RemotableViewMethod
    public void setRemoteViewsAdapter(Intent intent) {
        super.setRemoteViewsAdapter(intent);
    }

    @Override // android.widget.AbsListView, android.widget.AdapterView
    public void setAdapter(ListAdapter listAdapter) {
        int iLookForSelectablePosition;
        if (this.mAdapter != null && this.mDataSetObserver != null) {
            this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
        }
        resetList();
        this.mRecycler.clear();
        this.mAdapter = listAdapter;
        this.mOldSelectedPosition = -1;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        super.setAdapter(listAdapter);
        if (this.mAdapter != null) {
            this.mOldItemCount = this.mItemCount;
            this.mItemCount = this.mAdapter.getCount();
            this.mDataChanged = true;
            checkFocus();
            this.mDataSetObserver = new AbsListView.AdapterDataSetObserver();
            this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
            this.mRecycler.setViewTypeCount(this.mAdapter.getViewTypeCount());
            if (this.mStackFromBottom) {
                iLookForSelectablePosition = lookForSelectablePosition(this.mItemCount - 1, false);
            } else {
                iLookForSelectablePosition = lookForSelectablePosition(0, true);
            }
            setSelectedPositionInt(iLookForSelectablePosition);
            setNextSelectedPositionInt(iLookForSelectablePosition);
            checkSelectionChanged();
        } else {
            checkFocus();
            checkSelectionChanged();
        }
        requestLayout();
    }

    @Override // android.widget.AdapterView
    int lookForSelectablePosition(int i, boolean z) {
        if (this.mAdapter == null || isInTouchMode() || i < 0 || i >= this.mItemCount) {
            return -1;
        }
        return i;
    }

    @Override // android.widget.AbsListView
    void fillGap(boolean z) {
        int i = this.mNumColumns;
        int i2 = this.mVerticalSpacing;
        int childCount = getChildCount();
        if (z) {
            int listPaddingTop = (this.mGroupFlags & 34) == 34 ? getListPaddingTop() : 0;
            if (childCount > 0) {
                listPaddingTop = getChildAt(childCount - 1).getBottom() + i2;
            }
            int i3 = this.mFirstPosition + childCount;
            if (this.mStackFromBottom) {
                i3 += i - 1;
            }
            fillDown(i3, listPaddingTop);
            correctTooHigh(i, i2, getChildCount());
            return;
        }
        int top = childCount > 0 ? getChildAt(0).getTop() - i2 : getHeight() - ((this.mGroupFlags & 34) == 34 ? getListPaddingBottom() : 0);
        int i4 = this.mFirstPosition;
        fillUp(!this.mStackFromBottom ? i4 - i : i4 - 1, top);
        correctTooLow(i, i2, getChildCount());
    }

    private View fillDown(int i, int i2) {
        int i3 = this.mBottom - this.mTop;
        View view = null;
        if ((this.mGroupFlags & 34) == 34) {
            i3 -= this.mListPadding.bottom;
        }
        while (i2 < i3 && i < this.mItemCount) {
            View viewMakeRow = makeRow(i, i2, true);
            if (viewMakeRow != null) {
                view = viewMakeRow;
            }
            i2 = this.mReferenceView.getBottom() + this.mVerticalSpacing;
            i += this.mNumColumns;
        }
        setVisibleRangeHint(this.mFirstPosition, (this.mFirstPosition + getChildCount()) - 1);
        return view;
    }

    private View makeRow(int i, int i2, boolean z) {
        int width;
        int iMin;
        int i3;
        int i4 = this.mColumnWidth;
        int i5 = this.mHorizontalSpacing;
        boolean zIsLayoutRtl = isLayoutRtl();
        boolean z2 = false;
        if (zIsLayoutRtl) {
            width = ((getWidth() - this.mListPadding.right) - i4) - (this.mStretchMode == 3 ? i5 : 0);
        } else {
            width = this.mListPadding.left + (this.mStretchMode == 3 ? i5 : 0);
        }
        if (!this.mStackFromBottom) {
            i3 = i;
            iMin = Math.min(i + this.mNumColumns, this.mItemCount);
        } else {
            int i6 = i + 1;
            int iMax = Math.max(0, (i - this.mNumColumns) + 1);
            int i7 = i6 - iMax;
            int i8 = this.mNumColumns;
            if (i7 < i8) {
                width += (zIsLayoutRtl ? -1 : 1) * (i8 - i7) * (i4 + i5);
            }
            iMin = i6;
            i3 = iMax;
        }
        boolean zShouldShowSelector = shouldShowSelector();
        boolean z3 = touchModeDrawsInPressedState();
        int i9 = this.mSelectedPosition;
        View viewMakeAndAddView = null;
        View view = null;
        int i10 = width;
        int i11 = i3;
        while (i11 < iMin) {
            boolean z4 = i11 == i9 ? true : z2;
            int i12 = i11;
            int i13 = i9;
            viewMakeAndAddView = makeAndAddView(i11, i2, z, i10, z4, z ? -1 : i11 - i3);
            i10 += (zIsLayoutRtl ? -1 : 1) * i4;
            if (i12 < iMin - 1) {
                i10 += i5;
            }
            if (z4 && (zShouldShowSelector || z3)) {
                view = viewMakeAndAddView;
            }
            i11 = i12 + 1;
            i9 = i13;
            z2 = false;
        }
        this.mReferenceView = viewMakeAndAddView;
        if (view != null) {
            this.mReferenceViewInSelectedRow = viewMakeAndAddView;
        }
        return view;
    }

    private View fillUp(int i, int i2) {
        View view = null;
        int i3 = (this.mGroupFlags & 34) == 34 ? this.mListPadding.top : 0;
        while (i2 > i3 && i >= 0) {
            View viewMakeRow = makeRow(i, i2, false);
            if (viewMakeRow != null) {
                view = viewMakeRow;
            }
            i2 = this.mReferenceView.getTop() - this.mVerticalSpacing;
            this.mFirstPosition = i;
            i -= this.mNumColumns;
        }
        if (this.mStackFromBottom) {
            this.mFirstPosition = Math.max(0, i + 1);
        }
        setVisibleRangeHint(this.mFirstPosition, (this.mFirstPosition + getChildCount()) - 1);
        return view;
    }

    private View fillFromTop(int i) {
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mSelectedPosition);
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mItemCount - 1);
        if (this.mFirstPosition < 0) {
            this.mFirstPosition = 0;
        }
        this.mFirstPosition -= this.mFirstPosition % this.mNumColumns;
        return fillDown(this.mFirstPosition, i);
    }

    private View fillFromBottom(int i, int i2) {
        int iMin = (this.mItemCount - 1) - Math.min(Math.max(i, this.mSelectedPosition), this.mItemCount - 1);
        return fillUp((this.mItemCount - 1) - (iMin - (iMin % this.mNumColumns)), i2);
    }

    private View fillSelection(int i, int i2) {
        int i3;
        int iMax;
        int iReconcileSelectedPosition = reconcileSelectedPosition();
        int i4 = this.mNumColumns;
        int i5 = this.mVerticalSpacing;
        if (!this.mStackFromBottom) {
            iMax = iReconcileSelectedPosition - (iReconcileSelectedPosition % i4);
            i3 = -1;
        } else {
            int i6 = (this.mItemCount - 1) - iReconcileSelectedPosition;
            i3 = (this.mItemCount - 1) - (i6 - (i6 % i4));
            iMax = Math.max(0, (i3 - i4) + 1);
        }
        int verticalFadingEdgeLength = getVerticalFadingEdgeLength();
        View viewMakeRow = makeRow(this.mStackFromBottom ? i3 : iMax, getTopSelectionPixel(i, verticalFadingEdgeLength, iMax), true);
        this.mFirstPosition = iMax;
        View view = this.mReferenceView;
        if (!this.mStackFromBottom) {
            fillDown(iMax + i4, view.getBottom() + i5);
            pinToBottom(i2);
            fillUp(iMax - i4, view.getTop() - i5);
            adjustViewsUpOrDown();
        } else {
            offsetChildrenTopAndBottom(getBottomSelectionPixel(i2, verticalFadingEdgeLength, i4, iMax) - view.getBottom());
            fillUp(iMax - 1, view.getTop() - i5);
            pinToTop(i);
            fillDown(i3 + i4, view.getBottom() + i5);
            adjustViewsUpOrDown();
        }
        return viewMakeRow;
    }

    private void pinToTop(int i) {
        int top;
        if (this.mFirstPosition != 0 || (top = i - getChildAt(0).getTop()) >= 0) {
            return;
        }
        offsetChildrenTopAndBottom(top);
    }

    private void pinToBottom(int i) {
        int bottom;
        int childCount = getChildCount();
        if (this.mFirstPosition + childCount != this.mItemCount || (bottom = i - getChildAt(childCount - 1).getBottom()) <= 0) {
            return;
        }
        offsetChildrenTopAndBottom(bottom);
    }

    @Override // android.widget.AbsListView
    int findMotionRow(int i) {
        int childCount = getChildCount();
        if (childCount <= 0) {
            return -1;
        }
        int i2 = this.mNumColumns;
        if (this.mStackFromBottom) {
            for (int i3 = childCount - 1; i3 >= 0; i3 -= i2) {
                if (i >= getChildAt(i3).getTop()) {
                    return this.mFirstPosition + i3;
                }
            }
            return -1;
        }
        for (int i4 = 0; i4 < childCount; i4 += i2) {
            if (i <= getChildAt(i4).getBottom()) {
                return this.mFirstPosition + i4;
            }
        }
        return -1;
    }

    private View fillSpecific(int i, int i2) {
        int i3;
        int iMax;
        View viewFillUp;
        View viewFillDown;
        int i4 = this.mNumColumns;
        if (!this.mStackFromBottom) {
            iMax = i - (i % i4);
            i3 = -1;
        } else {
            int i5 = (this.mItemCount - 1) - i;
            i3 = (this.mItemCount - 1) - (i5 - (i5 % i4));
            iMax = Math.max(0, (i3 - i4) + 1);
        }
        View viewMakeRow = makeRow(this.mStackFromBottom ? i3 : iMax, i2, true);
        this.mFirstPosition = iMax;
        View view = this.mReferenceView;
        if (view == null) {
            return null;
        }
        int i6 = this.mVerticalSpacing;
        if (!this.mStackFromBottom) {
            viewFillUp = fillUp(iMax - i4, view.getTop() - i6);
            adjustViewsUpOrDown();
            viewFillDown = fillDown(iMax + i4, view.getBottom() + i6);
            int childCount = getChildCount();
            if (childCount > 0) {
                correctTooHigh(i4, i6, childCount);
            }
        } else {
            View viewFillDown2 = fillDown(i3 + i4, view.getBottom() + i6);
            adjustViewsUpOrDown();
            View viewFillUp2 = fillUp(iMax - 1, view.getTop() - i6);
            int childCount2 = getChildCount();
            if (childCount2 > 0) {
                correctTooLow(i4, i6, childCount2);
            }
            viewFillUp = viewFillUp2;
            viewFillDown = viewFillDown2;
        }
        return viewMakeRow != null ? viewMakeRow : viewFillUp != null ? viewFillUp : viewFillDown;
    }

    private void correctTooHigh(int i, int i2, int i3) {
        if ((this.mFirstPosition + i3) - 1 != this.mItemCount - 1 || i3 <= 0) {
            return;
        }
        int bottom = ((this.mBottom - this.mTop) - this.mListPadding.bottom) - getChildAt(i3 - 1).getBottom();
        View childAt = getChildAt(0);
        int top = childAt.getTop();
        if (bottom > 0) {
            if (this.mFirstPosition > 0 || top < this.mListPadding.top) {
                if (this.mFirstPosition == 0) {
                    bottom = Math.min(bottom, this.mListPadding.top - top);
                }
                offsetChildrenTopAndBottom(bottom);
                if (this.mFirstPosition > 0) {
                    int i4 = this.mFirstPosition;
                    if (this.mStackFromBottom) {
                        i = 1;
                    }
                    fillUp(i4 - i, childAt.getTop() - i2);
                    adjustViewsUpOrDown();
                }
            }
        }
    }

    private void correctTooLow(int i, int i2, int i3) {
        if (this.mFirstPosition != 0 || i3 <= 0) {
            return;
        }
        int top = getChildAt(0).getTop();
        int i4 = this.mListPadding.top;
        int i5 = (this.mBottom - this.mTop) - this.mListPadding.bottom;
        int iMin = top - i4;
        View childAt = getChildAt(i3 - 1);
        int bottom = childAt.getBottom();
        int i6 = (this.mFirstPosition + i3) - 1;
        if (iMin > 0) {
            if (i6 < this.mItemCount - 1 || bottom > i5) {
                if (i6 == this.mItemCount - 1) {
                    iMin = Math.min(iMin, bottom - i5);
                }
                offsetChildrenTopAndBottom(-iMin);
                if (i6 < this.mItemCount - 1) {
                    if (!this.mStackFromBottom) {
                        i = 1;
                    }
                    fillDown(i6 + i, childAt.getBottom() + i2);
                    adjustViewsUpOrDown();
                }
            }
        }
    }

    private View fillFromSelection(int i, int i2, int i3) {
        int i4;
        int iMax;
        int verticalFadingEdgeLength = getVerticalFadingEdgeLength();
        int i5 = this.mSelectedPosition;
        int i6 = this.mNumColumns;
        int i7 = this.mVerticalSpacing;
        if (!this.mStackFromBottom) {
            iMax = i5 - (i5 % i6);
            i4 = -1;
        } else {
            int i8 = (this.mItemCount - 1) - i5;
            i4 = (this.mItemCount - 1) - (i8 - (i8 % i6));
            iMax = Math.max(0, (i4 - i6) + 1);
        }
        int topSelectionPixel = getTopSelectionPixel(i2, verticalFadingEdgeLength, iMax);
        int bottomSelectionPixel = getBottomSelectionPixel(i3, verticalFadingEdgeLength, i6, iMax);
        View viewMakeRow = makeRow(this.mStackFromBottom ? i4 : iMax, i, true);
        this.mFirstPosition = iMax;
        View view = this.mReferenceView;
        adjustForTopFadingEdge(view, topSelectionPixel, bottomSelectionPixel);
        adjustForBottomFadingEdge(view, topSelectionPixel, bottomSelectionPixel);
        if (!this.mStackFromBottom) {
            fillUp(iMax - i6, view.getTop() - i7);
            adjustViewsUpOrDown();
            fillDown(iMax + i6, view.getBottom() + i7);
        } else {
            fillDown(i4 + i6, view.getBottom() + i7);
            adjustViewsUpOrDown();
            fillUp(iMax - 1, view.getTop() - i7);
        }
        return viewMakeRow;
    }

    private int getBottomSelectionPixel(int i, int i2, int i3, int i4) {
        return (i4 + i3) + (-1) < this.mItemCount + (-1) ? i - i2 : i;
    }

    private void adjustForBottomFadingEdge(View view, int i, int i2) {
        if (view.getBottom() > i2) {
            offsetChildrenTopAndBottom(-Math.min(view.getTop() - i, view.getBottom() - i2));
        }
    }

    private void adjustForTopFadingEdge(View view, int i, int i2) {
        if (view.getTop() < i) {
            offsetChildrenTopAndBottom(Math.min(i - view.getTop(), i2 - view.getBottom()));
        }
    }

    @Override // android.widget.AbsListView
    @RemotableViewMethod
    public void smoothScrollToPosition(int i) {
        super.smoothScrollToPosition(i);
    }

    @Override // android.widget.AbsListView
    @RemotableViewMethod
    public void smoothScrollByOffset(int i) {
        super.smoothScrollByOffset(i);
    }

    private View moveSelection(int i, int i2, int i3) {
        int i4;
        int iMax;
        int i5;
        View viewMakeRow;
        View view;
        int verticalFadingEdgeLength = getVerticalFadingEdgeLength();
        int i6 = this.mSelectedPosition;
        int i7 = this.mNumColumns;
        int i8 = this.mVerticalSpacing;
        if (!this.mStackFromBottom) {
            int i9 = i6 - i;
            iMax = i9 - (i9 % i7);
            i5 = i6 - (i6 % i7);
            i4 = -1;
        } else {
            int i10 = (this.mItemCount - 1) - i6;
            i4 = (this.mItemCount - 1) - (i10 - (i10 % i7));
            int iMax2 = Math.max(0, (i4 - i7) + 1);
            int i11 = (this.mItemCount - 1) - (i6 - i);
            iMax = Math.max(0, (((this.mItemCount - 1) - (i11 - (i11 % i7))) - i7) + 1);
            i5 = iMax2;
        }
        int i12 = i5 - iMax;
        int topSelectionPixel = getTopSelectionPixel(i2, verticalFadingEdgeLength, i5);
        int bottomSelectionPixel = getBottomSelectionPixel(i3, verticalFadingEdgeLength, i7, i5);
        this.mFirstPosition = i5;
        if (i12 > 0) {
            View view2 = this.mReferenceViewInSelectedRow;
            viewMakeRow = makeRow(this.mStackFromBottom ? i4 : i5, (view2 != null ? view2.getBottom() : 0) + i8, true);
            view = this.mReferenceView;
            adjustForBottomFadingEdge(view, topSelectionPixel, bottomSelectionPixel);
        } else if (i12 < 0) {
            View view3 = this.mReferenceViewInSelectedRow;
            viewMakeRow = makeRow(this.mStackFromBottom ? i4 : i5, (view3 == null ? 0 : view3.getTop()) - i8, false);
            view = this.mReferenceView;
            adjustForTopFadingEdge(view, topSelectionPixel, bottomSelectionPixel);
        } else {
            View view4 = this.mReferenceViewInSelectedRow;
            viewMakeRow = makeRow(this.mStackFromBottom ? i4 : i5, view4 != null ? view4.getTop() : 0, true);
            view = this.mReferenceView;
        }
        if (!this.mStackFromBottom) {
            fillUp(i5 - i7, view.getTop() - i8);
            adjustViewsUpOrDown();
            fillDown(i5 + i7, view.getBottom() + i8);
        } else {
            fillDown(i4 + i7, view.getBottom() + i8);
            adjustViewsUpOrDown();
            fillUp(i5 - 1, view.getTop() - i8);
        }
        return viewMakeRow;
    }

    private boolean determineColumns(int i) {
        int i2 = this.mRequestedHorizontalSpacing;
        int i3 = this.mStretchMode;
        int i4 = this.mRequestedColumnWidth;
        int i5 = this.mRequestedNumColumns;
        if (i5 != -1) {
            this.mNumColumns = i5;
        } else if (i4 > 0) {
            this.mNumColumns = (i + i2) / (i4 + i2);
        } else {
            this.mNumColumns = 2;
        }
        if (this.mNumColumns <= 0) {
            this.mNumColumns = 1;
        }
        if (i3 == 0) {
            this.mColumnWidth = i4;
            this.mHorizontalSpacing = i2;
        } else {
            int i6 = this.mNumColumns;
            int i7 = (i - (i6 * i4)) - ((i6 - 1) * i2);
            z = i7 < 0;
            if (i3 == 1) {
                this.mColumnWidth = i4;
                if (i6 > 1) {
                    this.mHorizontalSpacing = i2 + (i7 / (i6 - 1));
                } else {
                    this.mHorizontalSpacing = i2 + i7;
                }
            } else if (i3 == 2) {
                this.mColumnWidth = i4 + (i7 / i6);
                this.mHorizontalSpacing = i2;
            } else if (i3 == 3) {
                this.mColumnWidth = i4;
                if (i6 > 1) {
                    this.mHorizontalSpacing = i2 + (i7 / (i6 + 1));
                } else {
                    this.mHorizontalSpacing = i2 + i7;
                }
            }
        }
        return z;
    }

    @Override // android.widget.AbsListView, android.view.View
    protected void onMeasure(int i, int i2) {
        int measuredHeight;
        int i3;
        int i4;
        int i5;
        super.onMeasure(i, i2);
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        if (mode == 0) {
            int i6 = this.mColumnWidth;
            if (i6 > 0) {
                i4 = i6 + this.mListPadding.left;
                i5 = this.mListPadding.right;
            } else {
                i4 = this.mListPadding.left;
                i5 = this.mListPadding.right;
            }
            size = i4 + i5 + getVerticalScrollbarWidth();
        }
        boolean zDetermineColumns = determineColumns((size - this.mListPadding.left) - this.mListPadding.right);
        int i7 = 0;
        this.mItemCount = this.mAdapter == null ? 0 : this.mAdapter.getCount();
        int i8 = this.mItemCount;
        if (i8 > 0) {
            View viewObtainView = obtainView(0, this.mIsScrap);
            AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) viewObtainView.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = (AbsListView.LayoutParams) generateDefaultLayoutParams();
                viewObtainView.setLayoutParams(layoutParams);
            }
            layoutParams.viewType = this.mAdapter.getItemViewType(0);
            layoutParams.forceAdd = true;
            viewObtainView.measure(getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(this.mColumnWidth, 1073741824), 0, layoutParams.width), getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, 0), 0, layoutParams.height));
            measuredHeight = viewObtainView.getMeasuredHeight();
            combineMeasuredStates(0, viewObtainView.getMeasuredState());
            if (this.mRecycler.shouldRecycleViewType(layoutParams.viewType)) {
                this.mRecycler.addScrapView(viewObtainView, -1);
            }
        } else {
            measuredHeight = 0;
        }
        if (mode2 == 0) {
            size2 = this.mListPadding.top + this.mListPadding.bottom + measuredHeight + (getVerticalFadingEdgeLength() * 2);
        }
        if (mode2 == Integer.MIN_VALUE) {
            int i9 = this.mListPadding.top + this.mListPadding.bottom;
            int i10 = this.mNumColumns;
            while (true) {
                if (i7 >= i8) {
                    size2 = i9;
                    break;
                }
                i9 += measuredHeight;
                i7 += i10;
                if (i7 < i8) {
                    i9 += this.mVerticalSpacing;
                }
                if (i9 >= size2) {
                    break;
                }
            }
        }
        if (mode == Integer.MIN_VALUE && (i3 = this.mRequestedNumColumns) != -1 && ((this.mColumnWidth * i3) + ((i3 - 1) * this.mHorizontalSpacing) + this.mListPadding.left + this.mListPadding.right > size || zDetermineColumns)) {
            size |= 16777216;
        }
        setMeasuredDimension(size, size2);
        this.mWidthMeasureSpec = i;
    }

    @Override // android.view.ViewGroup
    protected void attachLayoutAnimationParameters(View view, ViewGroup.LayoutParams layoutParams, int i, int i2) {
        GridLayoutAnimationController.AnimationParameters animationParameters = (GridLayoutAnimationController.AnimationParameters) layoutParams.layoutAnimationParameters;
        if (animationParameters == null) {
            animationParameters = new GridLayoutAnimationController.AnimationParameters();
            layoutParams.layoutAnimationParameters = animationParameters;
        }
        animationParameters.count = i2;
        animationParameters.index = i;
        animationParameters.columnsCount = this.mNumColumns;
        animationParameters.rowsCount = i2 / this.mNumColumns;
        if (!this.mStackFromBottom) {
            animationParameters.column = i % this.mNumColumns;
            animationParameters.row = i / this.mNumColumns;
        } else {
            int i3 = (i2 - 1) - i;
            int i4 = this.mNumColumns;
            animationParameters.column = (i4 - 1) - (i3 % i4);
            animationParameters.row = (animationParameters.rowsCount - 1) - (i3 / this.mNumColumns);
        }
    }

    @Override // android.widget.AbsListView
    protected void layoutChildren() {
        int i;
        View childAt;
        View childAt2;
        View childAt3;
        View viewFillFromTop;
        boolean z = this.mBlockLayoutRequests;
        if (!z) {
            this.mBlockLayoutRequests = true;
        }
        try {
            super.layoutChildren();
            invalidate();
            if (this.mAdapter == null) {
                resetList();
                invokeOnItemScrollListener();
                if (z) {
                    return;
                } else {
                    return;
                }
            }
            int top = this.mListPadding.top;
            int i2 = (this.mBottom - this.mTop) - this.mListPadding.bottom;
            int childCount = getChildCount();
            switch (this.mLayoutMode) {
                case 1:
                case 3:
                case 4:
                case 5:
                    i = 0;
                    childAt = null;
                    childAt2 = null;
                    childAt3 = null;
                    break;
                case 2:
                    int i3 = this.mNextSelectedPosition - this.mFirstPosition;
                    if (i3 < 0 || i3 >= childCount) {
                        i = 0;
                        childAt = null;
                        childAt2 = null;
                        childAt3 = null;
                    } else {
                        childAt = getChildAt(i3);
                        i = 0;
                        childAt2 = null;
                        childAt3 = null;
                    }
                    break;
                case 6:
                    i = this.mNextSelectedPosition >= 0 ? this.mNextSelectedPosition - this.mSelectedPosition : 0;
                    childAt = null;
                    childAt2 = null;
                    childAt3 = null;
                    break;
                default:
                    int i4 = this.mSelectedPosition - this.mFirstPosition;
                    childAt2 = (i4 < 0 || i4 >= childCount) ? null : getChildAt(i4);
                    childAt3 = getChildAt(0);
                    childAt = null;
                    i = 0;
                    break;
            }
            boolean z2 = this.mDataChanged;
            if (z2) {
                handleDataChanged();
            }
            if (this.mItemCount == 0) {
                resetList();
                invokeOnItemScrollListener();
                if (z) {
                    return;
                }
                this.mBlockLayoutRequests = false;
                return;
            }
            setSelectedPositionInt(this.mNextSelectedPosition);
            int i5 = this.mFirstPosition;
            AbsListView.RecycleBin recycleBin = this.mRecycler;
            if (z2) {
                for (int i6 = 0; i6 < childCount; i6++) {
                    recycleBin.addScrapView(getChildAt(i6), i5 + i6);
                }
            } else {
                recycleBin.fillActiveViews(childCount, i5);
            }
            detachAllViewsFromParent();
            recycleBin.removeSkippedScrap();
            switch (this.mLayoutMode) {
                case 1:
                    this.mFirstPosition = 0;
                    viewFillFromTop = fillFromTop(top);
                    adjustViewsUpOrDown();
                    break;
                case 2:
                    if (childAt != null) {
                        viewFillFromTop = fillFromSelection(childAt.getTop(), top, i2);
                    } else {
                        viewFillFromTop = fillSelection(top, i2);
                    }
                    break;
                case 3:
                    viewFillFromTop = fillUp(this.mItemCount - 1, i2);
                    adjustViewsUpOrDown();
                    break;
                case 4:
                    viewFillFromTop = fillSpecific(this.mSelectedPosition, this.mSpecificTop);
                    break;
                case 5:
                    viewFillFromTop = fillSpecific(this.mSyncPosition, this.mSpecificTop);
                    break;
                case 6:
                    viewFillFromTop = moveSelection(i, top, i2);
                    break;
                default:
                    if (childCount == 0) {
                        if (!this.mStackFromBottom) {
                            setSelectedPositionInt((this.mAdapter == null || isInTouchMode()) ? -1 : 0);
                            viewFillFromTop = fillFromTop(top);
                        } else {
                            int i7 = this.mItemCount - 1;
                            setSelectedPositionInt((this.mAdapter == null || isInTouchMode()) ? -1 : i7);
                            viewFillFromTop = fillFromBottom(i7, i2);
                        }
                    } else if (this.mSelectedPosition >= 0 && this.mSelectedPosition < this.mItemCount) {
                        int i8 = this.mSelectedPosition;
                        if (childAt2 != null) {
                            top = childAt2.getTop();
                        }
                        viewFillFromTop = fillSpecific(i8, top);
                    } else if (this.mFirstPosition < this.mItemCount) {
                        int i9 = this.mFirstPosition;
                        if (childAt3 != null) {
                            top = childAt3.getTop();
                        }
                        viewFillFromTop = fillSpecific(i9, top);
                    } else {
                        viewFillFromTop = fillSpecific(0, top);
                    }
                    break;
            }
            recycleBin.scrapActiveViews();
            if (viewFillFromTop != null) {
                positionSelector(-1, viewFillFromTop);
                this.mSelectedTop = viewFillFromTop.getTop();
            } else if (this.mTouchMode > 0 && this.mTouchMode < 3) {
                View childAt4 = getChildAt(this.mMotionPosition - this.mFirstPosition);
                if (childAt4 != null) {
                    positionSelector(this.mMotionPosition, childAt4);
                }
            } else {
                this.mSelectedTop = 0;
                this.mSelectorRect.setEmpty();
            }
            this.mLayoutMode = 0;
            this.mDataChanged = false;
            if (this.mPositionScrollAfterLayout != null) {
                post(this.mPositionScrollAfterLayout);
                this.mPositionScrollAfterLayout = null;
            }
            this.mNeedSync = false;
            setNextSelectedPositionInt(this.mSelectedPosition);
            updateScrollIndicators();
            if (this.mItemCount > 0) {
                checkSelectionChanged();
            }
            invokeOnItemScrollListener();
            if (z) {
                return;
            }
            this.mBlockLayoutRequests = false;
        } finally {
            if (!z) {
                this.mBlockLayoutRequests = false;
            }
        }
    }

    private View makeAndAddView(int i, int i2, boolean z, int i3, boolean z2, int i4) {
        View activeView;
        if (!this.mDataChanged && (activeView = this.mRecycler.getActiveView(i)) != null) {
            setupChild(activeView, i, i2, z, i3, z2, true, i4);
            return activeView;
        }
        View viewObtainView = obtainView(i, this.mIsScrap);
        setupChild(viewObtainView, i, i2, z, i3, z2, this.mIsScrap[0], i4);
        return viewObtainView;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void setupChild(View view, int i, int i2, boolean z, int i3, boolean z2, boolean z3, int i4) {
        int i5;
        Trace.traceBegin(8L, "setupGridItem");
        boolean z4 = z2 && shouldShowSelector();
        boolean z5 = z4 != view.isSelected();
        int i6 = this.mTouchMode;
        boolean z6 = i6 > 0 && i6 < 3 && this.mMotionPosition == i;
        boolean z7 = z6 != view.isPressed();
        boolean z8 = !z3 || z5 || view.isLayoutRequested();
        AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = (AbsListView.LayoutParams) generateDefaultLayoutParams();
        }
        layoutParams.viewType = this.mAdapter.getItemViewType(i);
        if (z3 && !layoutParams.forceAdd) {
            attachViewToParent(view, i4, layoutParams);
        } else {
            layoutParams.forceAdd = false;
            addViewInLayout(view, i4, layoutParams, true);
        }
        if (z5) {
            view.setSelected(z4);
            if (z4) {
                requestFocus();
            }
        }
        if (z7) {
            view.setPressed(z6);
        }
        if (this.mChoiceMode != 0 && this.mCheckStates != null) {
            if (view instanceof Checkable) {
                ((Checkable) view).setChecked(this.mCheckStates.get(i));
            } else if (getContext().getApplicationInfo().targetSdkVersion >= 11) {
                view.setActivated(this.mCheckStates.get(i));
            }
        }
        if (z8) {
            view.measure(ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(this.mColumnWidth, 1073741824), 0, layoutParams.width), ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, 0), 0, layoutParams.height));
        } else {
            cleanupLayoutState(view);
        }
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        int i7 = z ? i2 : i2 - measuredHeight;
        int absoluteGravity = Gravity.getAbsoluteGravity(this.mGravity, getLayoutDirection()) & 7;
        if (absoluteGravity == 1) {
            i5 = i3 + ((this.mColumnWidth - measuredWidth) / 2);
        } else {
            i5 = absoluteGravity != 5 ? i3 : (i3 + this.mColumnWidth) - measuredWidth;
        }
        if (z8) {
            view.layout(i5, i7, measuredWidth + i5, measuredHeight + i7);
        } else {
            view.offsetLeftAndRight(i5 - view.getLeft());
            view.offsetTopAndBottom(i7 - view.getTop());
        }
        if (this.mCachingStarted) {
            view.setDrawingCacheEnabled(true);
        }
        if (z3 && ((AbsListView.LayoutParams) view.getLayoutParams()).scrappedFromPosition != i) {
            view.jumpDrawablesToCurrentState();
        }
        Trace.traceEnd(8L);
    }

    @Override // android.widget.AdapterView
    public void setSelection(int i) {
        if (!isInTouchMode()) {
            setNextSelectedPositionInt(i);
        } else {
            this.mResurrectToPosition = i;
        }
        this.mLayoutMode = 2;
        if (this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }
        requestLayout();
    }

    @Override // android.widget.AbsListView
    void setSelectionInt(int i) {
        int i2 = this.mNextSelectedPosition;
        if (this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }
        setNextSelectedPositionInt(i);
        layoutChildren();
        int i3 = this.mStackFromBottom ? (this.mItemCount - 1) - this.mNextSelectedPosition : this.mNextSelectedPosition;
        if (this.mStackFromBottom) {
            i2 = (this.mItemCount - 1) - i2;
        }
        int i4 = this.mNumColumns;
        if (i3 / i4 != i2 / i4) {
            awakenScrollBars();
        }
    }

    @Override // android.widget.AbsListView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return commonKey(i, 1, keyEvent);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent) {
        return commonKey(i, i2, keyEvent);
    }

    @Override // android.widget.AbsListView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        return commonKey(i, 1, keyEvent);
    }

    /* JADX WARN: Removed duplicated region for block: B:108:0x0124  */
    /* JADX WARN: Removed duplicated region for block: B:117:0x013f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean commonKey(int r9, int r10, android.view.KeyEvent r11) {
        /*
            Method dump skipped, instruction units count: 418
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.GridView.commonKey(int, int, android.view.KeyEvent):boolean");
    }

    boolean pageScroll(int i) {
        int iMin;
        if (i == 33) {
            iMin = Math.max(0, this.mSelectedPosition - getChildCount());
        } else {
            iMin = i == 130 ? Math.min(this.mItemCount - 1, this.mSelectedPosition + getChildCount()) : -1;
        }
        if (iMin < 0) {
            return false;
        }
        setSelectionInt(iMin);
        invokeOnItemScrollListener();
        awakenScrollBars();
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0023  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    boolean fullScroll(int r5) {
        /*
            r4 = this;
            r0 = 2
            r1 = 0
            r2 = 1
            r3 = 33
            if (r5 != r3) goto L11
            r4.mLayoutMode = r0
            r4.setSelectionInt(r1)
            r4.invokeOnItemScrollListener()
        Lf:
            r1 = r2
            goto L21
        L11:
            r3 = 130(0x82, float:1.82E-43)
            if (r5 != r3) goto L21
            r4.mLayoutMode = r0
            int r5 = r4.mItemCount
            int r5 = r5 - r2
            r4.setSelectionInt(r5)
            r4.invokeOnItemScrollListener()
            goto Lf
        L21:
            if (r1 == 0) goto L26
            r4.awakenScrollBars()
        L26:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.GridView.fullScroll(int):boolean");
    }

    boolean arrowScroll(int i) {
        int iMin;
        int iMax;
        int i2 = this.mSelectedPosition;
        int i3 = this.mNumColumns;
        boolean z = false;
        if (!this.mStackFromBottom) {
            iMax = (i2 / i3) * i3;
            iMin = Math.min((iMax + i3) - 1, this.mItemCount - 1);
        } else {
            iMin = (this.mItemCount - 1) - ((((this.mItemCount - 1) - i2) / i3) * i3);
            iMax = Math.max(0, (iMin - i3) + 1);
        }
        if (i != 17) {
            if (i != 33) {
                if (i != 66) {
                    if (i == 130 && iMin < this.mItemCount - 1) {
                        this.mLayoutMode = 6;
                        setSelectionInt(Math.min(i2 + i3, this.mItemCount - 1));
                        z = true;
                    }
                } else if (i2 < iMin) {
                    this.mLayoutMode = 6;
                    setSelectionInt(Math.min(i2 + 1, this.mItemCount - 1));
                    z = true;
                }
            } else if (iMax > 0) {
                this.mLayoutMode = 6;
                setSelectionInt(Math.max(0, i2 - i3));
                z = true;
            }
        } else if (i2 > iMax) {
            this.mLayoutMode = 6;
            setSelectionInt(Math.max(0, i2 - 1));
            z = true;
        }
        if (z) {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(i));
            invokeOnItemScrollListener();
        }
        if (z) {
            awakenScrollBars();
        }
        return z;
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x0048  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    boolean sequenceScroll(int r9) {
        /*
            r8 = this;
            int r0 = r8.mSelectedPosition
            int r1 = r8.mNumColumns
            int r2 = r8.mItemCount
            boolean r3 = r8.mStackFromBottom
            r4 = 0
            r5 = 1
            if (r3 != 0) goto L18
            int r3 = r0 / r1
            int r3 = r3 * r1
            int r1 = r1 + r3
            int r1 = r1 - r5
            int r6 = r2 + (-1)
            int r1 = java.lang.Math.min(r1, r6)
            goto L29
        L18:
            int r3 = r2 + (-1)
            int r6 = r3 - r0
            int r6 = r6 / r1
            int r6 = r6 * r1
            int r3 = r3 - r6
            int r1 = r3 - r1
            int r1 = r1 + r5
            int r1 = java.lang.Math.max(r4, r1)
            r7 = r3
            r3 = r1
            r1 = r7
        L29:
            r6 = 6
            if (r9 == r5) goto L3d
            r3 = 2
            if (r9 == r3) goto L30
            goto L4c
        L30:
            int r2 = r2 - r5
            if (r0 >= r2) goto L4c
            r8.mLayoutMode = r6
            int r2 = r0 + 1
            r8.setSelectionInt(r2)
            if (r0 != r1) goto L49
            goto L48
        L3d:
            if (r0 <= 0) goto L4c
            r8.mLayoutMode = r6
            int r1 = r0 + (-1)
            r8.setSelectionInt(r1)
            if (r0 != r3) goto L49
        L48:
            r4 = r5
        L49:
            r0 = r4
            r4 = r5
            goto L4d
        L4c:
            r0 = r4
        L4d:
            if (r4 == 0) goto L59
            int r9 = android.view.SoundEffectConstants.getContantForFocusDirection(r9)
            r8.playSoundEffect(r9)
            r8.invokeOnItemScrollListener()
        L59:
            if (r0 == 0) goto L5e
            r8.awakenScrollBars()
        L5e:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.GridView.sequenceScroll(int):boolean");
    }

    @Override // android.widget.AbsListView, android.view.View
    protected void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        int i2 = -1;
        if (z && rect != null) {
            rect.offset(this.mScrollX, this.mScrollY);
            Rect rect2 = this.mTempRect;
            int i3 = Integer.MAX_VALUE;
            int childCount = getChildCount();
            for (int i4 = 0; i4 < childCount; i4++) {
                if (isCandidateSelection(i4, i)) {
                    View childAt = getChildAt(i4);
                    childAt.getDrawingRect(rect2);
                    offsetDescendantRectToMyCoords(childAt, rect2);
                    int distance = getDistance(rect, rect2, i);
                    if (distance < i3) {
                        i2 = i4;
                        i3 = distance;
                    }
                }
            }
        }
        if (i2 >= 0) {
            setSelection(i2 + this.mFirstPosition);
        } else {
            requestLayout();
        }
    }

    private boolean isCandidateSelection(int i, int i2) {
        int iMax;
        int iMax2;
        int childCount = getChildCount();
        int i3 = childCount - 1;
        int i4 = i3 - i;
        if (!this.mStackFromBottom) {
            int i5 = this.mNumColumns;
            iMax = i - (i % i5);
            iMax2 = Math.max((i5 + iMax) - 1, childCount);
        } else {
            int i6 = this.mNumColumns;
            int i7 = i3 - (i4 - (i4 % i6));
            iMax = Math.max(0, (i7 - i6) + 1);
            iMax2 = i7;
        }
        if (i2 == 1) {
            return i == iMax2 && iMax2 == i3;
        }
        if (i2 == 2) {
            return i == iMax && iMax == 0;
        }
        if (i2 == 17) {
            return i == iMax2;
        }
        if (i2 == 33) {
            return iMax2 == i3;
        }
        if (i2 == 66) {
            return i == iMax;
        }
        if (i2 == 130) {
            return iMax == 0;
        }
        throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}.");
    }

    public void setGravity(int i) {
        if (this.mGravity != i) {
            this.mGravity = i;
            requestLayoutIfNecessary();
        }
    }

    public int getGravity() {
        return this.mGravity;
    }

    public void setHorizontalSpacing(int i) {
        if (i != this.mRequestedHorizontalSpacing) {
            this.mRequestedHorizontalSpacing = i;
            requestLayoutIfNecessary();
        }
    }

    public int getHorizontalSpacing() {
        return this.mHorizontalSpacing;
    }

    public int getRequestedHorizontalSpacing() {
        return this.mRequestedHorizontalSpacing;
    }

    public void setVerticalSpacing(int i) {
        if (i != this.mVerticalSpacing) {
            this.mVerticalSpacing = i;
            requestLayoutIfNecessary();
        }
    }

    public int getVerticalSpacing() {
        return this.mVerticalSpacing;
    }

    public void setStretchMode(int i) {
        if (i != this.mStretchMode) {
            this.mStretchMode = i;
            requestLayoutIfNecessary();
        }
    }

    public int getStretchMode() {
        return this.mStretchMode;
    }

    public void setColumnWidth(int i) {
        if (i != this.mRequestedColumnWidth) {
            this.mRequestedColumnWidth = i;
            requestLayoutIfNecessary();
        }
    }

    public int getColumnWidth() {
        return this.mColumnWidth;
    }

    public int getRequestedColumnWidth() {
        return this.mRequestedColumnWidth;
    }

    public void setNumColumns(int i) {
        if (i != this.mRequestedNumColumns) {
            this.mRequestedNumColumns = i;
            requestLayoutIfNecessary();
        }
    }

    @ViewDebug.ExportedProperty
    public int getNumColumns() {
        return this.mNumColumns;
    }

    private void adjustViewsUpOrDown() {
        int childCount = getChildCount();
        if (childCount > 0) {
            int i = 0;
            if (!this.mStackFromBottom) {
                int top = getChildAt(0).getTop() - this.mListPadding.top;
                if (this.mFirstPosition != 0) {
                    top -= this.mVerticalSpacing;
                }
                if (top >= 0) {
                    i = top;
                }
            } else {
                int bottom = getChildAt(childCount - 1).getBottom() - (getHeight() - this.mListPadding.bottom);
                if (this.mFirstPosition + childCount < this.mItemCount) {
                    bottom += this.mVerticalSpacing;
                }
                if (bottom <= 0) {
                    i = bottom;
                }
            }
            if (i != 0) {
                offsetChildrenTopAndBottom(-i);
            }
        }
    }

    @Override // android.widget.AbsListView, android.view.View
    protected int computeVerticalScrollExtent() {
        int childCount = getChildCount();
        if (childCount <= 0) {
            return 0;
        }
        int i = (((childCount + r2) - 1) / this.mNumColumns) * 100;
        View childAt = getChildAt(0);
        int top = childAt.getTop();
        int height = childAt.getHeight();
        if (height > 0) {
            i += (top * 100) / height;
        }
        View childAt2 = getChildAt(childCount - 1);
        int bottom = childAt2.getBottom();
        int height2 = childAt2.getHeight();
        return height2 > 0 ? i - (((bottom - getHeight()) * 100) / height2) : i;
    }

    @Override // android.widget.AbsListView, android.view.View
    protected int computeVerticalScrollOffset() {
        if (this.mFirstPosition >= 0 && getChildCount() > 0) {
            View childAt = getChildAt(0);
            int top = childAt.getTop();
            int height = childAt.getHeight();
            if (height > 0) {
                int i = this.mNumColumns;
                int i2 = ((this.mItemCount + i) - 1) / i;
                return Math.max(((((this.mFirstPosition + (isStackFromBottom() ? (i2 * i) - this.mItemCount : 0)) / i) * 100) - ((top * 100) / height)) + ((int) ((this.mScrollY / getHeight()) * i2 * 100.0f)), 0);
            }
        }
        return 0;
    }

    @Override // android.widget.AbsListView, android.view.View
    protected int computeVerticalScrollRange() {
        int i = ((this.mItemCount + r0) - 1) / this.mNumColumns;
        int iMax = Math.max(i * 100, 0);
        return this.mScrollY != 0 ? iMax + Math.abs((int) ((this.mScrollY / getHeight()) * i * 100.0f)) : iMax;
    }

    @Override // android.widget.AbsListView, android.widget.AdapterView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(GridView.class.getName());
    }

    @Override // android.widget.AbsListView, android.widget.AdapterView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(GridView.class.getName());
        int numColumns = getNumColumns();
        accessibilityNodeInfo.setCollectionInfo(AccessibilityNodeInfo.CollectionInfo.obtain(numColumns, getCount() / numColumns, false));
    }

    @Override // android.widget.AbsListView
    public void onInitializeAccessibilityNodeInfoForItem(View view, int i, AccessibilityNodeInfo accessibilityNodeInfo) {
        int i2;
        int i3;
        super.onInitializeAccessibilityNodeInfoForItem(view, i, accessibilityNodeInfo);
        int count = getCount();
        int numColumns = getNumColumns();
        int i4 = count / numColumns;
        if (!this.mStackFromBottom) {
            i2 = i % numColumns;
            i3 = i / numColumns;
        } else {
            int i5 = (count - 1) - i;
            int i6 = (numColumns - 1) - (i5 % numColumns);
            int i7 = (i4 - 1) - (i5 / numColumns);
            i2 = i6;
            i3 = i7;
        }
        AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) view.getLayoutParams();
        accessibilityNodeInfo.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(i2, 1, i3, 1, (layoutParams == null || layoutParams.viewType == -2) ? false : true));
    }
}
