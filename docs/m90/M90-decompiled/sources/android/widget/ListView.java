package android.widget;

import android.R;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Trace;
import android.util.AttributeSet;
import android.util.MathUtils;
import android.util.SparseBooleanArray;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.RemotableViewMethod;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewRootImpl;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AbsListView;
import android.widget.RemoteViews;
import com.android.internal.util.Predicate;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import org.apache.tools.ant.taskdefs.optional.j2ee.HotDeploymentTool;

/* JADX INFO: loaded from: classes.dex */
@RemoteViews.RemoteView
public class ListView extends AbsListView {
    private static final float MAX_SCROLL_FACTOR = 0.33f;
    private static final int MIN_SCROLL_PREVIEW_PIXELS = 2;
    static final int NO_POSITION = -1;
    private boolean mAreAllItemsSelectable;
    private final ArrowScrollFocusResult mArrowScrollFocusResult;
    Drawable mDivider;
    int mDividerHeight;
    private boolean mDividerIsOpaque;
    private Paint mDividerPaint;
    private FocusSelector mFocusSelector;
    private boolean mFooterDividersEnabled;
    private ArrayList<FixedViewInfo> mFooterViewInfos;
    private boolean mHeaderDividersEnabled;
    private ArrayList<FixedViewInfo> mHeaderViewInfos;
    private boolean mIsCacheColorOpaque;
    private boolean mItemsCanFocus;
    Drawable mOverScrollFooter;
    Drawable mOverScrollHeader;
    private final Rect mTempRect;

    private int getTopSelectionPixel(int i, int i2, int i3) {
        return i3 > 0 ? i + i2 : i;
    }

    @ViewDebug.ExportedProperty(category = HotDeploymentTool.ACTION_LIST)
    protected boolean recycleOnMeasure() {
        return true;
    }

    public class FixedViewInfo {
        public Object data;
        public boolean isSelectable;
        public View view;

        public FixedViewInfo() {
        }
    }

    public ListView(Context context) {
        this(context, null);
    }

    public ListView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.listViewStyle);
    }

    public ListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mHeaderViewInfos = Lists.newArrayList();
        this.mFooterViewInfos = Lists.newArrayList();
        this.mAreAllItemsSelectable = true;
        this.mItemsCanFocus = false;
        this.mTempRect = new Rect();
        this.mArrowScrollFocusResult = new ArrowScrollFocusResult();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, com.android.internal.R.styleable.ListView, i, 0);
        CharSequence[] textArray = typedArrayObtainStyledAttributes.getTextArray(0);
        if (textArray != null) {
            setAdapter((ListAdapter) new ArrayAdapter(context, R.layout.simple_list_item_1, textArray));
        }
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(1);
        if (drawable != null) {
            setDivider(drawable);
        }
        Drawable drawable2 = typedArrayObtainStyledAttributes.getDrawable(5);
        if (drawable2 != null) {
            setOverscrollHeader(drawable2);
        }
        Drawable drawable3 = typedArrayObtainStyledAttributes.getDrawable(6);
        if (drawable3 != null) {
            setOverscrollFooter(drawable3);
        }
        int dimensionPixelSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(2, 0);
        if (dimensionPixelSize != 0) {
            setDividerHeight(dimensionPixelSize);
        }
        this.mHeaderDividersEnabled = typedArrayObtainStyledAttributes.getBoolean(3, true);
        this.mFooterDividersEnabled = typedArrayObtainStyledAttributes.getBoolean(4, true);
        typedArrayObtainStyledAttributes.recycle();
    }

    public int getMaxScrollAmount() {
        return (int) ((this.mBottom - this.mTop) * MAX_SCROLL_FACTOR);
    }

    private void adjustViewsUpOrDown() {
        int childCount = getChildCount();
        if (childCount > 0) {
            int i = 0;
            if (!this.mStackFromBottom) {
                int top = getChildAt(0).getTop() - this.mListPadding.top;
                if (this.mFirstPosition != 0) {
                    top -= this.mDividerHeight;
                }
                if (top >= 0) {
                    i = top;
                }
            } else {
                int bottom = getChildAt(childCount - 1).getBottom() - (getHeight() - this.mListPadding.bottom);
                if (this.mFirstPosition + childCount < this.mItemCount) {
                    bottom += this.mDividerHeight;
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

    public void addHeaderView(View view, Object obj, boolean z) {
        FixedViewInfo fixedViewInfo = new FixedViewInfo();
        fixedViewInfo.view = view;
        fixedViewInfo.data = obj;
        fixedViewInfo.isSelectable = z;
        this.mHeaderViewInfos.add(fixedViewInfo);
        if (this.mAdapter != null) {
            if (!(this.mAdapter instanceof HeaderViewListAdapter)) {
                this.mAdapter = new HeaderViewListAdapter(this.mHeaderViewInfos, this.mFooterViewInfos, this.mAdapter);
            }
            if (this.mDataSetObserver != null) {
                this.mDataSetObserver.onChanged();
            }
        }
    }

    public void addHeaderView(View view) {
        addHeaderView(view, null, true);
    }

    @Override // android.widget.AbsListView
    public int getHeaderViewsCount() {
        return this.mHeaderViewInfos.size();
    }

    public boolean removeHeaderView(View view) {
        boolean z = false;
        if (this.mHeaderViewInfos.size() > 0) {
            if (this.mAdapter != null && ((HeaderViewListAdapter) this.mAdapter).removeHeader(view)) {
                if (this.mDataSetObserver != null) {
                    this.mDataSetObserver.onChanged();
                }
                z = true;
            }
            removeFixedViewInfo(view, this.mHeaderViewInfos);
        }
        return z;
    }

    private void removeFixedViewInfo(View view, ArrayList<FixedViewInfo> arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            if (arrayList.get(i).view == view) {
                arrayList.remove(i);
                return;
            }
        }
    }

    public void addFooterView(View view, Object obj, boolean z) {
        FixedViewInfo fixedViewInfo = new FixedViewInfo();
        fixedViewInfo.view = view;
        fixedViewInfo.data = obj;
        fixedViewInfo.isSelectable = z;
        this.mFooterViewInfos.add(fixedViewInfo);
        if (this.mAdapter != null) {
            if (!(this.mAdapter instanceof HeaderViewListAdapter)) {
                this.mAdapter = new HeaderViewListAdapter(this.mHeaderViewInfos, this.mFooterViewInfos, this.mAdapter);
            }
            if (this.mDataSetObserver != null) {
                this.mDataSetObserver.onChanged();
            }
        }
    }

    public void addFooterView(View view) {
        addFooterView(view, null, true);
    }

    @Override // android.widget.AbsListView
    public int getFooterViewsCount() {
        return this.mFooterViewInfos.size();
    }

    public boolean removeFooterView(View view) {
        boolean z = false;
        if (this.mFooterViewInfos.size() > 0) {
            if (this.mAdapter != null && ((HeaderViewListAdapter) this.mAdapter).removeFooter(view)) {
                if (this.mDataSetObserver != null) {
                    this.mDataSetObserver.onChanged();
                }
                z = true;
            }
            removeFixedViewInfo(view, this.mFooterViewInfos);
        }
        return z;
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
        if (this.mHeaderViewInfos.size() > 0 || this.mFooterViewInfos.size() > 0) {
            this.mAdapter = new HeaderViewListAdapter(this.mHeaderViewInfos, this.mFooterViewInfos, listAdapter);
        } else {
            this.mAdapter = listAdapter;
        }
        this.mOldSelectedPosition = -1;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        super.setAdapter(listAdapter);
        if (this.mAdapter != null) {
            this.mAreAllItemsSelectable = this.mAdapter.areAllItemsEnabled();
            this.mOldItemCount = this.mItemCount;
            this.mItemCount = this.mAdapter.getCount();
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
            if (this.mItemCount == 0) {
                checkSelectionChanged();
            }
        } else {
            this.mAreAllItemsSelectable = true;
            checkFocus();
            checkSelectionChanged();
        }
        requestLayout();
    }

    @Override // android.widget.AbsListView
    void resetList() {
        clearRecycledState(this.mHeaderViewInfos);
        clearRecycledState(this.mFooterViewInfos);
        super.resetList();
        this.mLayoutMode = 0;
    }

    private void clearRecycledState(ArrayList<FixedViewInfo> arrayList) {
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) arrayList.get(i).view.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.recycledHeaderFooter = false;
                }
            }
        }
    }

    private boolean showingTopFadingEdge() {
        return this.mFirstPosition > 0 || getChildAt(0).getTop() > this.mScrollY + this.mListPadding.top;
    }

    private boolean showingBottomFadingEdge() {
        int childCount = getChildCount();
        return (this.mFirstPosition + childCount) - 1 < this.mItemCount - 1 || getChildAt(childCount + (-1)).getBottom() < (this.mScrollY + getHeight()) - this.mListPadding.bottom;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
        int iMax;
        int i;
        int i2;
        int i3 = rect.top;
        rect.offset(view.getLeft(), view.getTop());
        rect.offset(-view.getScrollX(), -view.getScrollY());
        int height = getHeight();
        int scrollY = getScrollY();
        int i4 = scrollY + height;
        int verticalFadingEdgeLength = getVerticalFadingEdgeLength();
        if (showingTopFadingEdge() && (this.mSelectedPosition > 0 || i3 > verticalFadingEdgeLength)) {
            scrollY += verticalFadingEdgeLength;
        }
        int bottom = getChildAt(getChildCount() - 1).getBottom();
        if (showingBottomFadingEdge() && (this.mSelectedPosition < this.mItemCount - 1 || rect.bottom < bottom - verticalFadingEdgeLength)) {
            i4 -= verticalFadingEdgeLength;
        }
        if (rect.bottom > i4 && rect.top > scrollY) {
            if (rect.height() > height) {
                i2 = rect.top - scrollY;
            } else {
                i2 = rect.bottom - i4;
            }
            iMax = Math.min(i2 + 0, bottom - i4);
        } else if (rect.top >= scrollY || rect.bottom >= i4) {
            iMax = 0;
        } else {
            if (rect.height() > height) {
                i = 0 - (i4 - rect.bottom);
            } else {
                i = 0 - (scrollY - rect.top);
            }
            iMax = Math.max(i, getChildAt(0).getTop() - scrollY);
        }
        boolean z2 = iMax != 0;
        if (z2) {
            scrollListItemsBy(-iMax);
            positionSelector(-1, view);
            this.mSelectedTop = view.getTop();
            invalidate();
        }
        return z2;
    }

    @Override // android.widget.AbsListView
    void fillGap(boolean z) {
        int childCount = getChildCount();
        if (z) {
            int listPaddingTop = (this.mGroupFlags & 34) == 34 ? getListPaddingTop() : 0;
            if (childCount > 0) {
                listPaddingTop = this.mDividerHeight + getChildAt(childCount - 1).getBottom();
            }
            fillDown(this.mFirstPosition + childCount, listPaddingTop);
            correctTooHigh(getChildCount());
            return;
        }
        fillUp(this.mFirstPosition - 1, childCount > 0 ? getChildAt(0).getTop() - this.mDividerHeight : getHeight() - ((this.mGroupFlags & 34) == 34 ? getListPaddingBottom() : 0));
        correctTooLow(getChildCount());
    }

    private View fillDown(int i, int i2) {
        int i3 = this.mBottom - this.mTop;
        View view = null;
        if ((this.mGroupFlags & 34) == 34) {
            i3 -= this.mListPadding.bottom;
        }
        int bottom = i2;
        while (true) {
            if (bottom >= i3 || i >= this.mItemCount) {
                break;
            }
            boolean z = i == this.mSelectedPosition;
            View viewMakeAndAddView = makeAndAddView(i, bottom, true, this.mListPadding.left, z);
            bottom = viewMakeAndAddView.getBottom() + this.mDividerHeight;
            if (z) {
                view = viewMakeAndAddView;
            }
            i++;
        }
        setVisibleRangeHint(this.mFirstPosition, (this.mFirstPosition + getChildCount()) - 1);
        return view;
    }

    private View fillUp(int i, int i2) {
        int top;
        int i3;
        View view = null;
        if ((this.mGroupFlags & 34) == 34) {
            i3 = this.mListPadding.top;
            top = i2;
        } else {
            top = i2;
            i3 = 0;
        }
        while (true) {
            if (top <= i3 || i < 0) {
                break;
            }
            boolean z = i == this.mSelectedPosition;
            View viewMakeAndAddView = makeAndAddView(i, top, false, this.mListPadding.left, z);
            top = viewMakeAndAddView.getTop() - this.mDividerHeight;
            if (z) {
                view = viewMakeAndAddView;
            }
            i--;
        }
        this.mFirstPosition = i + 1;
        setVisibleRangeHint(this.mFirstPosition, (this.mFirstPosition + getChildCount()) - 1);
        return view;
    }

    private View fillFromTop(int i) {
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mSelectedPosition);
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mItemCount - 1);
        if (this.mFirstPosition < 0) {
            this.mFirstPosition = 0;
        }
        return fillDown(this.mFirstPosition, i);
    }

    private View fillFromMiddle(int i, int i2) {
        int i3 = i2 - i;
        int iReconcileSelectedPosition = reconcileSelectedPosition();
        View viewMakeAndAddView = makeAndAddView(iReconcileSelectedPosition, i, true, this.mListPadding.left, true);
        this.mFirstPosition = iReconcileSelectedPosition;
        int measuredHeight = viewMakeAndAddView.getMeasuredHeight();
        if (measuredHeight <= i3) {
            viewMakeAndAddView.offsetTopAndBottom((i3 - measuredHeight) / 2);
        }
        fillAboveAndBelow(viewMakeAndAddView, iReconcileSelectedPosition);
        if (!this.mStackFromBottom) {
            correctTooHigh(getChildCount());
        } else {
            correctTooLow(getChildCount());
        }
        return viewMakeAndAddView;
    }

    private void fillAboveAndBelow(View view, int i) {
        int i2 = this.mDividerHeight;
        if (!this.mStackFromBottom) {
            fillUp(i - 1, view.getTop() - i2);
            adjustViewsUpOrDown();
            fillDown(i + 1, view.getBottom() + i2);
        } else {
            fillDown(i + 1, view.getBottom() + i2);
            adjustViewsUpOrDown();
            fillUp(i - 1, view.getTop() - i2);
        }
    }

    private View fillFromSelection(int i, int i2, int i3) {
        int verticalFadingEdgeLength = getVerticalFadingEdgeLength();
        int i4 = this.mSelectedPosition;
        int topSelectionPixel = getTopSelectionPixel(i2, verticalFadingEdgeLength, i4);
        int bottomSelectionPixel = getBottomSelectionPixel(i3, verticalFadingEdgeLength, i4);
        View viewMakeAndAddView = makeAndAddView(i4, i, true, this.mListPadding.left, true);
        if (viewMakeAndAddView.getBottom() > bottomSelectionPixel) {
            viewMakeAndAddView.offsetTopAndBottom(-Math.min(viewMakeAndAddView.getTop() - topSelectionPixel, viewMakeAndAddView.getBottom() - bottomSelectionPixel));
        } else if (viewMakeAndAddView.getTop() < topSelectionPixel) {
            viewMakeAndAddView.offsetTopAndBottom(Math.min(topSelectionPixel - viewMakeAndAddView.getTop(), bottomSelectionPixel - viewMakeAndAddView.getBottom()));
        }
        fillAboveAndBelow(viewMakeAndAddView, i4);
        if (!this.mStackFromBottom) {
            correctTooHigh(getChildCount());
        } else {
            correctTooLow(getChildCount());
        }
        return viewMakeAndAddView;
    }

    private int getBottomSelectionPixel(int i, int i2, int i3) {
        return i3 != this.mItemCount + (-1) ? i - i2 : i;
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

    private View moveSelection(View view, View view2, int i, int i2, int i3) {
        View viewMakeAndAddView;
        View viewMakeAndAddView2;
        int verticalFadingEdgeLength = getVerticalFadingEdgeLength();
        int i4 = this.mSelectedPosition;
        int topSelectionPixel = getTopSelectionPixel(i2, verticalFadingEdgeLength, i4);
        int bottomSelectionPixel = getBottomSelectionPixel(i2, verticalFadingEdgeLength, i4);
        if (i > 0) {
            View viewMakeAndAddView3 = makeAndAddView(i4 - 1, view.getTop(), true, this.mListPadding.left, false);
            int i5 = this.mDividerHeight;
            viewMakeAndAddView = makeAndAddView(i4, viewMakeAndAddView3.getBottom() + i5, true, this.mListPadding.left, true);
            if (viewMakeAndAddView.getBottom() > bottomSelectionPixel) {
                int i6 = -Math.min(Math.min(viewMakeAndAddView.getTop() - topSelectionPixel, viewMakeAndAddView.getBottom() - bottomSelectionPixel), (i3 - i2) / 2);
                viewMakeAndAddView3.offsetTopAndBottom(i6);
                viewMakeAndAddView.offsetTopAndBottom(i6);
            }
            if (!this.mStackFromBottom) {
                fillUp(this.mSelectedPosition - 2, viewMakeAndAddView.getTop() - i5);
                adjustViewsUpOrDown();
                fillDown(this.mSelectedPosition + 1, viewMakeAndAddView.getBottom() + i5);
            } else {
                fillDown(this.mSelectedPosition + 1, viewMakeAndAddView.getBottom() + i5);
                adjustViewsUpOrDown();
                fillUp(this.mSelectedPosition - 2, viewMakeAndAddView.getTop() - i5);
            }
        } else if (i < 0) {
            if (view2 != null) {
                viewMakeAndAddView2 = makeAndAddView(i4, view2.getTop(), true, this.mListPadding.left, true);
            } else {
                viewMakeAndAddView2 = makeAndAddView(i4, view.getTop(), false, this.mListPadding.left, true);
            }
            viewMakeAndAddView = viewMakeAndAddView2;
            if (viewMakeAndAddView.getTop() < topSelectionPixel) {
                viewMakeAndAddView.offsetTopAndBottom(Math.min(Math.min(topSelectionPixel - viewMakeAndAddView.getTop(), bottomSelectionPixel - viewMakeAndAddView.getBottom()), (i3 - i2) / 2));
            }
            fillAboveAndBelow(viewMakeAndAddView, i4);
        } else {
            int top = view.getTop();
            viewMakeAndAddView = makeAndAddView(i4, top, true, this.mListPadding.left, true);
            if (top < i2 && viewMakeAndAddView.getBottom() < i2 + 20) {
                viewMakeAndAddView.offsetTopAndBottom(i2 - viewMakeAndAddView.getTop());
            }
            fillAboveAndBelow(viewMakeAndAddView, i4);
        }
        return viewMakeAndAddView;
    }

    private class FocusSelector implements Runnable {
        private int mPosition;
        private int mPositionTop;

        private FocusSelector() {
        }

        public FocusSelector setup(int i, int i2) {
            this.mPosition = i;
            this.mPositionTop = i2;
            return this;
        }

        @Override // java.lang.Runnable
        public void run() {
            ListView.this.setSelectionFromTop(this.mPosition, this.mPositionTop);
        }
    }

    @Override // android.widget.AbsListView, android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        View focusedChild;
        if (getChildCount() > 0 && (focusedChild = getFocusedChild()) != null) {
            int iIndexOfChild = this.mFirstPosition + indexOfChild(focusedChild);
            int top = focusedChild.getTop() - Math.max(0, focusedChild.getBottom() - (i2 - this.mPaddingTop));
            if (this.mFocusSelector == null) {
                this.mFocusSelector = new FocusSelector();
            }
            post(this.mFocusSelector.setup(iIndexOfChild, top));
        }
        super.onSizeChanged(i, i2, i3, i4);
    }

    @Override // android.widget.AbsListView, android.view.View
    protected void onMeasure(int i, int i2) {
        int measuredWidth;
        int measuredHeight;
        super.onMeasure(i, i2);
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int iCombineMeasuredStates = 0;
        this.mItemCount = this.mAdapter == null ? 0 : this.mAdapter.getCount();
        if (this.mItemCount <= 0 || !(mode == 0 || mode2 == 0)) {
            measuredWidth = 0;
            measuredHeight = 0;
        } else {
            View viewObtainView = obtainView(0, this.mIsScrap);
            measureScrapChild(viewObtainView, 0, i);
            measuredWidth = viewObtainView.getMeasuredWidth();
            measuredHeight = viewObtainView.getMeasuredHeight();
            iCombineMeasuredStates = combineMeasuredStates(0, viewObtainView.getMeasuredState());
            if (recycleOnMeasure() && this.mRecycler.shouldRecycleViewType(((AbsListView.LayoutParams) viewObtainView.getLayoutParams()).viewType)) {
                this.mRecycler.addScrapView(viewObtainView, -1);
            }
        }
        int verticalScrollbarWidth = mode == 0 ? this.mListPadding.left + this.mListPadding.right + measuredWidth + getVerticalScrollbarWidth() : ((-16777216) & iCombineMeasuredStates) | size;
        if (mode2 == 0) {
            size2 = this.mListPadding.top + this.mListPadding.bottom + measuredHeight + (getVerticalFadingEdgeLength() * 2);
        }
        int iMeasureHeightOfChildren = size2;
        if (mode2 == Integer.MIN_VALUE) {
            iMeasureHeightOfChildren = measureHeightOfChildren(i, 0, -1, iMeasureHeightOfChildren, -1);
        }
        setMeasuredDimension(verticalScrollbarWidth, iMeasureHeightOfChildren);
        this.mWidthMeasureSpec = i;
    }

    private void measureScrapChild(View view, int i, int i2) {
        int iMakeMeasureSpec;
        AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = (AbsListView.LayoutParams) generateDefaultLayoutParams();
            view.setLayoutParams(layoutParams);
        }
        layoutParams.viewType = this.mAdapter.getItemViewType(i);
        layoutParams.forceAdd = true;
        int childMeasureSpec = ViewGroup.getChildMeasureSpec(i2, this.mListPadding.left + this.mListPadding.right, layoutParams.width);
        int i3 = layoutParams.height;
        if (i3 > 0) {
            iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i3, 1073741824);
        } else {
            iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        }
        view.measure(childMeasureSpec, iMakeMeasureSpec);
    }

    final int measureHeightOfChildren(int i, int i2, int i3, int i4, int i5) {
        ListAdapter listAdapter = this.mAdapter;
        if (listAdapter == null) {
            return this.mListPadding.top + this.mListPadding.bottom;
        }
        int measuredHeight = this.mListPadding.top + this.mListPadding.bottom;
        int i6 = this.mDividerHeight;
        int i7 = 0;
        if (i6 <= 0 || this.mDivider == null) {
            i6 = 0;
        }
        if (i3 == -1) {
            i3 = listAdapter.getCount() - 1;
        }
        AbsListView.RecycleBin recycleBin = this.mRecycler;
        boolean zRecycleOnMeasure = recycleOnMeasure();
        boolean[] zArr = this.mIsScrap;
        while (i2 <= i3) {
            View viewObtainView = obtainView(i2, zArr);
            measureScrapChild(viewObtainView, i2, i);
            if (i2 > 0) {
                measuredHeight += i6;
            }
            if (zRecycleOnMeasure && recycleBin.shouldRecycleViewType(((AbsListView.LayoutParams) viewObtainView.getLayoutParams()).viewType)) {
                recycleBin.addScrapView(viewObtainView, -1);
            }
            measuredHeight += viewObtainView.getMeasuredHeight();
            if (measuredHeight >= i4) {
                return (i5 < 0 || i2 <= i5 || i7 <= 0 || measuredHeight == i4) ? i4 : i7;
            }
            if (i5 >= 0 && i2 >= i5) {
                i7 = measuredHeight;
            }
            i2++;
        }
        return measuredHeight;
    }

    @Override // android.widget.AbsListView
    int findMotionRow(int i) {
        int childCount = getChildCount();
        if (childCount <= 0) {
            return -1;
        }
        if (this.mStackFromBottom) {
            for (int i2 = childCount - 1; i2 >= 0; i2--) {
                if (i >= getChildAt(i2).getTop()) {
                    return this.mFirstPosition + i2;
                }
            }
            return -1;
        }
        for (int i3 = 0; i3 < childCount; i3++) {
            if (i <= getChildAt(i3).getBottom()) {
                return this.mFirstPosition + i3;
            }
        }
        return -1;
    }

    private View fillSpecific(int i, int i2) {
        View viewFillUp;
        View viewFillDown;
        boolean z = i == this.mSelectedPosition;
        View viewMakeAndAddView = makeAndAddView(i, i2, true, this.mListPadding.left, z);
        this.mFirstPosition = i;
        int i3 = this.mDividerHeight;
        if (!this.mStackFromBottom) {
            viewFillUp = fillUp(i - 1, viewMakeAndAddView.getTop() - i3);
            adjustViewsUpOrDown();
            viewFillDown = fillDown(i + 1, viewMakeAndAddView.getBottom() + i3);
            int childCount = getChildCount();
            if (childCount > 0) {
                correctTooHigh(childCount);
            }
        } else {
            View viewFillDown2 = fillDown(i + 1, viewMakeAndAddView.getBottom() + i3);
            adjustViewsUpOrDown();
            View viewFillUp2 = fillUp(i - 1, viewMakeAndAddView.getTop() - i3);
            int childCount2 = getChildCount();
            if (childCount2 > 0) {
                correctTooLow(childCount2);
            }
            viewFillUp = viewFillUp2;
            viewFillDown = viewFillDown2;
        }
        return z ? viewMakeAndAddView : viewFillUp != null ? viewFillUp : viewFillDown;
    }

    private void correctTooHigh(int i) {
        if ((this.mFirstPosition + i) - 1 != this.mItemCount - 1 || i <= 0) {
            return;
        }
        int bottom = ((this.mBottom - this.mTop) - this.mListPadding.bottom) - getChildAt(i - 1).getBottom();
        View childAt = getChildAt(0);
        int top = childAt.getTop();
        if (bottom > 0) {
            if (this.mFirstPosition > 0 || top < this.mListPadding.top) {
                if (this.mFirstPosition == 0) {
                    bottom = Math.min(bottom, this.mListPadding.top - top);
                }
                offsetChildrenTopAndBottom(bottom);
                if (this.mFirstPosition > 0) {
                    fillUp(this.mFirstPosition - 1, childAt.getTop() - this.mDividerHeight);
                    adjustViewsUpOrDown();
                }
            }
        }
    }

    private void correctTooLow(int i) {
        if (this.mFirstPosition != 0 || i <= 0) {
            return;
        }
        int top = getChildAt(0).getTop();
        int i2 = this.mListPadding.top;
        int i3 = (this.mBottom - this.mTop) - this.mListPadding.bottom;
        int iMin = top - i2;
        View childAt = getChildAt(i - 1);
        int bottom = childAt.getBottom();
        int i4 = (this.mFirstPosition + i) - 1;
        if (iMin > 0) {
            if (i4 < this.mItemCount - 1 || bottom > i3) {
                if (i4 == this.mItemCount - 1) {
                    iMin = Math.min(iMin, bottom - i3);
                }
                offsetChildrenTopAndBottom(-iMin);
                if (i4 < this.mItemCount - 1) {
                    fillDown(i4 + 1, childAt.getBottom() + this.mDividerHeight);
                    adjustViewsUpOrDown();
                    return;
                }
                return;
            }
            if (i4 == this.mItemCount - 1) {
                adjustViewsUpOrDown();
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x008a A[Catch: all -> 0x02a4, TryCatch #1 {all -> 0x02a4, blocks: (B:6:0x000b, B:8:0x0015, B:12:0x0020, B:21:0x0043, B:24:0x004c, B:26:0x0052, B:28:0x005a, B:30:0x0061, B:37:0x0086, B:39:0x008a, B:40:0x008d, B:42:0x0091, B:46:0x009c, B:48:0x00a6, B:50:0x00b1, B:52:0x00bc, B:54:0x00c2, B:55:0x00c5, B:59:0x00ce, B:31:0x0071, B:34:0x007a), top: B:163:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0091 A[Catch: all -> 0x02a4, TRY_LEAVE, TryCatch #1 {all -> 0x02a4, blocks: (B:6:0x000b, B:8:0x0015, B:12:0x0020, B:21:0x0043, B:24:0x004c, B:26:0x0052, B:28:0x005a, B:30:0x0061, B:37:0x0086, B:39:0x008a, B:40:0x008d, B:42:0x0091, B:46:0x009c, B:48:0x00a6, B:50:0x00b1, B:52:0x00bc, B:54:0x00c2, B:55:0x00c5, B:59:0x00ce, B:31:0x0071, B:34:0x007a), top: B:163:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:46:0x009c A[Catch: all -> 0x02a4, TRY_ENTER, TryCatch #1 {all -> 0x02a4, blocks: (B:6:0x000b, B:8:0x0015, B:12:0x0020, B:21:0x0043, B:24:0x004c, B:26:0x0052, B:28:0x005a, B:30:0x0061, B:37:0x0086, B:39:0x008a, B:40:0x008d, B:42:0x0091, B:46:0x009c, B:48:0x00a6, B:50:0x00b1, B:52:0x00bc, B:54:0x00c2, B:55:0x00c5, B:59:0x00ce, B:31:0x0071, B:34:0x007a), top: B:163:0x000b }] */
    /* JADX WARN: Type inference failed for: r1v25 */
    /* JADX WARN: Type inference failed for: r1v26, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r1v29 */
    @Override // android.widget.AbsListView
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void layoutChildren() throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 702
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.ListView.layoutChildren():void");
    }

    private View getAccessibilityFocusedChild() {
        View accessibilityFocusedHost;
        boolean z;
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl == null || (accessibilityFocusedHost = viewRootImpl.getAccessibilityFocusedHost()) == null) {
            return null;
        }
        ViewParent parent = accessibilityFocusedHost.getParent();
        while (true) {
            z = parent instanceof View;
            if (!z || parent == this) {
                break;
            }
            accessibilityFocusedHost = parent;
            parent = parent.getParent();
        }
        if (z) {
            return accessibilityFocusedHost;
        }
        return null;
    }

    private View makeAndAddView(int i, int i2, boolean z, int i3, boolean z2) {
        View activeView;
        if (!this.mDataChanged && (activeView = this.mRecycler.getActiveView(i)) != null) {
            setupChild(activeView, i, i2, z, i3, z2, true);
            return activeView;
        }
        View viewObtainView = obtainView(i, this.mIsScrap);
        setupChild(viewObtainView, i, i2, z, i3, z2, this.mIsScrap[0]);
        return viewObtainView;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void setupChild(View view, int i, int i2, boolean z, int i3, boolean z2, boolean z3) {
        int iMakeMeasureSpec;
        Trace.traceBegin(8L, "setupListItem");
        boolean z4 = z2 && shouldShowSelector();
        boolean z5 = z4 != view.isSelected();
        int i4 = this.mTouchMode;
        boolean z6 = i4 > 0 && i4 < 3 && this.mMotionPosition == i;
        boolean z7 = z6 != view.isPressed();
        boolean z8 = !z3 || z5 || view.isLayoutRequested();
        AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = (AbsListView.LayoutParams) generateDefaultLayoutParams();
        }
        layoutParams.viewType = this.mAdapter.getItemViewType(i);
        if ((z3 && !layoutParams.forceAdd) || (layoutParams.recycledHeaderFooter && layoutParams.viewType == -2)) {
            attachViewToParent(view, z ? -1 : 0, layoutParams);
        } else {
            layoutParams.forceAdd = false;
            if (layoutParams.viewType == -2) {
                layoutParams.recycledHeaderFooter = true;
            }
            addViewInLayout(view, z ? -1 : 0, layoutParams, true);
        }
        if (z5) {
            view.setSelected(z4);
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
            int childMeasureSpec = ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mListPadding.left + this.mListPadding.right, layoutParams.width);
            int i5 = layoutParams.height;
            if (i5 > 0) {
                iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i5, 1073741824);
            } else {
                iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
            }
            view.measure(childMeasureSpec, iMakeMeasureSpec);
        } else {
            cleanupLayoutState(view);
        }
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        int i6 = z ? i2 : i2 - measuredHeight;
        if (z8) {
            view.layout(i3, i6, measuredWidth + i3, measuredHeight + i6);
        } else {
            view.offsetLeftAndRight(i3 - view.getLeft());
            view.offsetTopAndBottom(i6 - view.getTop());
        }
        if (this.mCachingStarted && !view.isDrawingCacheEnabled()) {
            view.setDrawingCacheEnabled(true);
        }
        if (z3 && ((AbsListView.LayoutParams) view.getLayoutParams()).scrappedFromPosition != i) {
            view.jumpDrawablesToCurrentState();
        }
        Trace.traceEnd(8L);
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup
    protected boolean canAnimate() {
        return super.canAnimate() && this.mItemCount > 0;
    }

    @Override // android.widget.AdapterView
    public void setSelection(int i) {
        setSelectionFromTop(i, 0);
    }

    public void setSelectionFromTop(int i, int i2) {
        if (this.mAdapter == null) {
            return;
        }
        if (!isInTouchMode()) {
            i = lookForSelectablePosition(i, true);
            if (i >= 0) {
                setNextSelectedPositionInt(i);
            }
        } else {
            this.mResurrectToPosition = i;
        }
        if (i >= 0) {
            this.mLayoutMode = 4;
            this.mSpecificTop = this.mListPadding.top + i2;
            if (this.mNeedSync) {
                this.mSyncPosition = i;
                this.mSyncRowId = this.mAdapter.getItemId(i);
            }
            if (this.mPositionScroller != null) {
                this.mPositionScroller.stop();
            }
            requestLayout();
        }
    }

    @Override // android.widget.AbsListView
    void setSelectionInt(int i) throws Throwable {
        setNextSelectedPositionInt(i);
        int i2 = this.mSelectedPosition;
        boolean z = true;
        if (i2 < 0 || (i != i2 - 1 && i != i2 + 1)) {
            z = false;
        }
        if (this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }
        layoutChildren();
        if (z) {
            awakenScrollBars();
        }
    }

    @Override // android.widget.AdapterView
    int lookForSelectablePosition(int i, boolean z) {
        ListAdapter listAdapter = this.mAdapter;
        if (listAdapter != null && !isInTouchMode()) {
            int count = listAdapter.getCount();
            if (!this.mAreAllItemsSelectable) {
                if (z) {
                    i = Math.max(0, i);
                    while (i < count && !listAdapter.isEnabled(i)) {
                        i++;
                    }
                } else {
                    i = Math.min(i, count - 1);
                    while (i >= 0 && !listAdapter.isEnabled(i)) {
                        i--;
                    }
                }
            }
            if (i >= 0 && i < count) {
                return i;
            }
        }
        return -1;
    }

    int lookForSelectablePositionAfter(int i, int i2, boolean z) {
        int iMax;
        ListAdapter listAdapter = this.mAdapter;
        if (listAdapter == null || isInTouchMode()) {
            return -1;
        }
        int iLookForSelectablePosition = lookForSelectablePosition(i2, z);
        if (iLookForSelectablePosition != -1) {
            return iLookForSelectablePosition;
        }
        int count = listAdapter.getCount() - 1;
        int iConstrain = MathUtils.constrain(i, -1, count);
        if (z) {
            iMax = Math.min(i2 - 1, count);
            while (iMax > iConstrain && !listAdapter.isEnabled(iMax)) {
                iMax--;
            }
            if (iMax <= iConstrain) {
                return -1;
            }
        } else {
            iMax = Math.max(0, i2 + 1);
            while (iMax < iConstrain && !listAdapter.isEnabled(iMax)) {
                iMax++;
            }
            if (iMax >= iConstrain) {
                return -1;
            }
        }
        return iMax;
    }

    public void setSelectionAfterHeaderView() {
        int size = this.mHeaderViewInfos.size();
        if (size > 0) {
            this.mNextSelectedPosition = 0;
        } else if (this.mAdapter != null) {
            setSelection(size);
        } else {
            this.mNextSelectedPosition = size;
            this.mLayoutMode = 2;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        boolean zDispatchKeyEvent = super.dispatchKeyEvent(keyEvent);
        return (zDispatchKeyEvent || getFocusedChild() == null || keyEvent.getAction() != 0) ? zDispatchKeyEvent : onKeyDown(keyEvent.getKeyCode(), keyEvent);
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

    /* JADX WARN: Removed duplicated region for block: B:108:0x012d  */
    /* JADX WARN: Removed duplicated region for block: B:131:0x0176  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean commonKey(int r9, int r10, android.view.KeyEvent r11) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 424
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.ListView.commonKey(int, int, android.view.KeyEvent):boolean");
    }

    boolean pageScroll(int i) throws Throwable {
        int iMin;
        boolean z;
        int iLookForSelectablePositionAfter;
        if (i != 33) {
            if (i == 130) {
                iMin = Math.min(this.mItemCount - 1, (this.mSelectedPosition + getChildCount()) - 1);
                z = true;
            }
            return false;
        }
        iMin = Math.max(0, (this.mSelectedPosition - getChildCount()) - 1);
        z = false;
        if (iMin >= 0 && (iLookForSelectablePositionAfter = lookForSelectablePositionAfter(this.mSelectedPosition, iMin, z)) >= 0) {
            this.mLayoutMode = 4;
            this.mSpecificTop = this.mPaddingTop + getVerticalFadingEdgeLength();
            if (z && iLookForSelectablePositionAfter > this.mItemCount - getChildCount()) {
                this.mLayoutMode = 3;
            }
            if (!z && iLookForSelectablePositionAfter < getChildCount()) {
                this.mLayoutMode = 1;
            }
            setSelectionInt(iLookForSelectablePositionAfter);
            invokeOnItemScrollListener();
            if (!awakenScrollBars()) {
                invalidate();
            }
            return true;
        }
        return false;
    }

    boolean fullScroll(int i) throws Throwable {
        int i2;
        boolean z = false;
        if (i == 33) {
            if (this.mSelectedPosition != 0) {
                int iLookForSelectablePositionAfter = lookForSelectablePositionAfter(this.mSelectedPosition, 0, true);
                if (iLookForSelectablePositionAfter >= 0) {
                    this.mLayoutMode = 1;
                    setSelectionInt(iLookForSelectablePositionAfter);
                    invokeOnItemScrollListener();
                }
                z = true;
            }
        } else if (i == 130 && this.mSelectedPosition < (i2 = this.mItemCount - 1)) {
            int iLookForSelectablePositionAfter2 = lookForSelectablePositionAfter(this.mSelectedPosition, i2, false);
            if (iLookForSelectablePositionAfter2 >= 0) {
                this.mLayoutMode = 3;
                setSelectionInt(iLookForSelectablePositionAfter2);
                invokeOnItemScrollListener();
            }
            z = true;
        }
        if (z && !awakenScrollBars()) {
            awakenScrollBars();
            invalidate();
        }
        return z;
    }

    private boolean handleHorizontalFocusWithinListItem(int i) {
        View selectedView;
        if (i != 17 && i != 66) {
            throw new IllegalArgumentException("direction must be one of {View.FOCUS_LEFT, View.FOCUS_RIGHT}");
        }
        int childCount = getChildCount();
        if (!this.mItemsCanFocus || childCount <= 0 || this.mSelectedPosition == -1 || (selectedView = getSelectedView()) == null || !selectedView.hasFocus() || !(selectedView instanceof ViewGroup)) {
            return false;
        }
        View viewFindFocus = selectedView.findFocus();
        View viewFindNextFocus = FocusFinder.getInstance().findNextFocus((ViewGroup) selectedView, viewFindFocus, i);
        if (viewFindNextFocus != null) {
            viewFindFocus.getFocusedRect(this.mTempRect);
            offsetDescendantRectToMyCoords(viewFindFocus, this.mTempRect);
            offsetRectIntoDescendantCoords(viewFindNextFocus, this.mTempRect);
            if (viewFindNextFocus.requestFocus(i, this.mTempRect)) {
                return true;
            }
        }
        View viewFindNextFocus2 = FocusFinder.getInstance().findNextFocus((ViewGroup) getRootView(), viewFindFocus, i);
        if (viewFindNextFocus2 != null) {
            return isViewAncestorOf(viewFindNextFocus2, this);
        }
        return false;
    }

    boolean arrowScroll(int i) {
        try {
            this.mInLayout = true;
            boolean zArrowScrollImpl = arrowScrollImpl(i);
            if (zArrowScrollImpl) {
                playSoundEffect(SoundEffectConstants.getContantForFocusDirection(i));
            }
            return zArrowScrollImpl;
        } finally {
            this.mInLayout = false;
        }
    }

    private final int nextSelectedPositionForDirection(View view, int i, int i2) {
        int i3;
        if (i2 == 130) {
            int height = getHeight() - this.mListPadding.bottom;
            if (view == null || view.getBottom() > height) {
                return -1;
            }
            i3 = (i == -1 || i < this.mFirstPosition) ? this.mFirstPosition : i + 1;
        } else {
            int i4 = this.mListPadding.top;
            if (view != null && view.getTop() >= i4) {
                int childCount = (this.mFirstPosition + getChildCount()) - 1;
                i3 = (i == -1 || i > childCount) ? childCount : i - 1;
            }
            return -1;
        }
        if (i3 >= 0 && i3 < this.mAdapter.getCount()) {
            return lookForSelectablePosition(i3, i2 == 130);
        }
        return -1;
    }

    private boolean arrowScrollImpl(int i) {
        View focusedChild;
        if (getChildCount() <= 0) {
            return false;
        }
        View selectedView = getSelectedView();
        int i2 = this.mSelectedPosition;
        int iNextSelectedPositionForDirection = nextSelectedPositionForDirection(selectedView, i2, i);
        int iAmountToScroll = amountToScroll(i, iNextSelectedPositionForDirection);
        View view = null;
        ArrowScrollFocusResult arrowScrollFocusResultArrowScrollFocused = this.mItemsCanFocus ? arrowScrollFocused(i) : null;
        if (arrowScrollFocusResultArrowScrollFocused != null) {
            iNextSelectedPositionForDirection = arrowScrollFocusResultArrowScrollFocused.getSelectedPosition();
            iAmountToScroll = arrowScrollFocusResultArrowScrollFocused.getAmountToScroll();
        }
        boolean z = arrowScrollFocusResultArrowScrollFocused != null;
        if (iNextSelectedPositionForDirection != -1) {
            handleNewSelectionChange(selectedView, i, iNextSelectedPositionForDirection, arrowScrollFocusResultArrowScrollFocused != null);
            setSelectedPositionInt(iNextSelectedPositionForDirection);
            setNextSelectedPositionInt(iNextSelectedPositionForDirection);
            selectedView = getSelectedView();
            if (this.mItemsCanFocus && arrowScrollFocusResultArrowScrollFocused == null && (focusedChild = getFocusedChild()) != null) {
                focusedChild.clearFocus();
            }
            checkSelectionChanged();
            i2 = iNextSelectedPositionForDirection;
            z = true;
        }
        if (iAmountToScroll > 0) {
            if (i != 33) {
                iAmountToScroll = -iAmountToScroll;
            }
            scrollListItemsBy(iAmountToScroll);
            z = true;
        }
        if (this.mItemsCanFocus && arrowScrollFocusResultArrowScrollFocused == null && selectedView != null && selectedView.hasFocus()) {
            View viewFindFocus = selectedView.findFocus();
            if (!isViewAncestorOf(viewFindFocus, this) || distanceToView(viewFindFocus) > 0) {
                viewFindFocus.clearFocus();
            }
        }
        if (iNextSelectedPositionForDirection != -1 || selectedView == null || isViewAncestorOf(selectedView, this)) {
            view = selectedView;
        } else {
            hideSelector();
            this.mResurrectToPosition = -1;
        }
        if (!z) {
            return false;
        }
        if (view != null) {
            positionSelector(i2, view);
            this.mSelectedTop = view.getTop();
        }
        if (!awakenScrollBars()) {
            invalidate();
        }
        invokeOnItemScrollListener();
        return true;
    }

    private void handleNewSelectionChange(View view, int i, int i2, boolean z) {
        View childAt;
        boolean z2;
        if (i2 == -1) {
            throw new IllegalArgumentException("newSelectedPosition needs to be valid");
        }
        int i3 = this.mSelectedPosition - this.mFirstPosition;
        int i4 = i2 - this.mFirstPosition;
        if (i == 33) {
            z2 = true;
            childAt = view;
            view = getChildAt(i4);
            i3 = i4;
            i4 = i3;
        } else {
            childAt = getChildAt(i4);
            z2 = false;
        }
        int childCount = getChildCount();
        if (view != null) {
            view.setSelected(!z && z2);
            measureAndAdjustDown(view, i3, childCount);
        }
        if (childAt != null) {
            childAt.setSelected((z || z2) ? false : true);
            measureAndAdjustDown(childAt, i4, childCount);
        }
    }

    private void measureAndAdjustDown(View view, int i, int i2) {
        int height = view.getHeight();
        measureItem(view);
        if (view.getMeasuredHeight() == height) {
            return;
        }
        relayoutMeasuredItem(view);
        int measuredHeight = view.getMeasuredHeight() - height;
        while (true) {
            i++;
            if (i >= i2) {
                return;
            } else {
                getChildAt(i).offsetTopAndBottom(measuredHeight);
            }
        }
    }

    private void measureItem(View view) {
        int iMakeMeasureSpec;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(-1, -2);
        }
        int childMeasureSpec = ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mListPadding.left + this.mListPadding.right, layoutParams.width);
        int i = layoutParams.height;
        if (i > 0) {
            iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i, 1073741824);
        } else {
            iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        }
        view.measure(childMeasureSpec, iMakeMeasureSpec);
    }

    private void relayoutMeasuredItem(View view) {
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        int i = this.mListPadding.left;
        int top = view.getTop();
        view.layout(i, top, measuredWidth + i, measuredHeight + top);
    }

    private int getArrowScrollPreviewLength() {
        return Math.max(2, getVerticalFadingEdgeLength());
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x008b  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x009d  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:30:0x008b -> B:27:0x0085). Please report as a decompilation issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int amountToScroll(int r7, int r8) {
        /*
            Method dump skipped, instruction units count: 225
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.ListView.amountToScroll(int, int):int");
    }

    private static class ArrowScrollFocusResult {
        private int mAmountToScroll;
        private int mSelectedPosition;

        private ArrowScrollFocusResult() {
        }

        void populate(int i, int i2) {
            this.mSelectedPosition = i;
            this.mAmountToScroll = i2;
        }

        public int getSelectedPosition() {
            return this.mSelectedPosition;
        }

        public int getAmountToScroll() {
            return this.mAmountToScroll;
        }
    }

    private int lookForSelectablePositionOnScreen(int i) {
        int i2 = this.mFirstPosition;
        if (i == 130) {
            int i3 = this.mSelectedPosition != -1 ? this.mSelectedPosition + 1 : i2;
            if (i3 >= this.mAdapter.getCount()) {
                return -1;
            }
            if (i3 < i2) {
                i3 = i2;
            }
            int lastVisiblePosition = getLastVisiblePosition();
            ListAdapter adapter = getAdapter();
            while (i3 <= lastVisiblePosition) {
                if (adapter.isEnabled(i3) && getChildAt(i3 - i2).getVisibility() == 0) {
                    return i3;
                }
                i3++;
            }
        } else {
            int childCount = (getChildCount() + i2) - 1;
            int childCount2 = (this.mSelectedPosition != -1 ? this.mSelectedPosition : getChildCount() + i2) - 1;
            if (childCount2 >= 0 && childCount2 < this.mAdapter.getCount()) {
                if (childCount2 <= childCount) {
                    childCount = childCount2;
                }
                ListAdapter adapter2 = getAdapter();
                while (childCount >= i2) {
                    if (adapter2.isEnabled(childCount) && getChildAt(childCount - i2).getVisibility() == 0) {
                        return childCount;
                    }
                    childCount--;
                }
            }
        }
        return -1;
    }

    private ArrowScrollFocusResult arrowScrollFocused(int i) {
        View viewFindNextFocusFromRect;
        int iLookForSelectablePositionOnScreen;
        View selectedView = getSelectedView();
        if (selectedView != null && selectedView.hasFocus()) {
            viewFindNextFocusFromRect = FocusFinder.getInstance().findNextFocus(this, selectedView.findFocus(), i);
        } else {
            if (i == 130) {
                int arrowScrollPreviewLength = this.mListPadding.top + (this.mFirstPosition > 0 ? getArrowScrollPreviewLength() : 0);
                if (selectedView != null && selectedView.getTop() > arrowScrollPreviewLength) {
                    arrowScrollPreviewLength = selectedView.getTop();
                }
                this.mTempRect.set(0, arrowScrollPreviewLength, 0, arrowScrollPreviewLength);
            } else {
                int height = (getHeight() - this.mListPadding.bottom) - ((this.mFirstPosition + getChildCount()) - 1 < this.mItemCount ? getArrowScrollPreviewLength() : 0);
                if (selectedView != null && selectedView.getBottom() < height) {
                    height = selectedView.getBottom();
                }
                this.mTempRect.set(0, height, 0, height);
            }
            viewFindNextFocusFromRect = FocusFinder.getInstance().findNextFocusFromRect(this, this.mTempRect, i);
        }
        if (viewFindNextFocusFromRect != null) {
            int iPositionOfNewFocus = positionOfNewFocus(viewFindNextFocusFromRect);
            if (this.mSelectedPosition != -1 && iPositionOfNewFocus != this.mSelectedPosition && (iLookForSelectablePositionOnScreen = lookForSelectablePositionOnScreen(i)) != -1 && ((i == 130 && iLookForSelectablePositionOnScreen < iPositionOfNewFocus) || (i == 33 && iLookForSelectablePositionOnScreen > iPositionOfNewFocus))) {
                return null;
            }
            int iAmountToScrollToNewFocus = amountToScrollToNewFocus(i, viewFindNextFocusFromRect, iPositionOfNewFocus);
            int maxScrollAmount = getMaxScrollAmount();
            if (iAmountToScrollToNewFocus < maxScrollAmount) {
                viewFindNextFocusFromRect.requestFocus(i);
                this.mArrowScrollFocusResult.populate(iPositionOfNewFocus, iAmountToScrollToNewFocus);
                return this.mArrowScrollFocusResult;
            }
            if (distanceToView(viewFindNextFocusFromRect) < maxScrollAmount) {
                viewFindNextFocusFromRect.requestFocus(i);
                this.mArrowScrollFocusResult.populate(iPositionOfNewFocus, maxScrollAmount);
                return this.mArrowScrollFocusResult;
            }
        }
        return null;
    }

    private int positionOfNewFocus(View view) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (isViewAncestorOf(view, getChildAt(i))) {
                return this.mFirstPosition + i;
            }
        }
        throw new IllegalArgumentException("newFocus is not a child of any of the children of the list!");
    }

    private boolean isViewAncestorOf(View view, View view2) {
        if (view == view2) {
            return true;
        }
        Object parent = view.getParent();
        return (parent instanceof ViewGroup) && isViewAncestorOf((View) parent, view2);
    }

    private int amountToScrollToNewFocus(int i, View view, int i2) {
        int i3;
        int arrowScrollPreviewLength;
        view.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(view, this.mTempRect);
        if (i == 33) {
            if (this.mTempRect.top < this.mListPadding.top) {
                i3 = this.mListPadding.top - this.mTempRect.top;
                if (i2 <= 0) {
                    return i3;
                }
                arrowScrollPreviewLength = getArrowScrollPreviewLength();
                return i3 + arrowScrollPreviewLength;
            }
            return 0;
        }
        int height = getHeight() - this.mListPadding.bottom;
        if (this.mTempRect.bottom > height) {
            i3 = this.mTempRect.bottom - height;
            if (i2 >= this.mItemCount - 1) {
                return i3;
            }
            arrowScrollPreviewLength = getArrowScrollPreviewLength();
            return i3 + arrowScrollPreviewLength;
        }
        return 0;
    }

    private int distanceToView(View view) {
        view.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(view, this.mTempRect);
        int i = (this.mBottom - this.mTop) - this.mListPadding.bottom;
        if (this.mTempRect.bottom < this.mListPadding.top) {
            return this.mListPadding.top - this.mTempRect.bottom;
        }
        if (this.mTempRect.top > i) {
            return this.mTempRect.top - i;
        }
        return 0;
    }

    private void scrollListItemsBy(int i) {
        int i2;
        offsetChildrenTopAndBottom(i);
        int height = getHeight() - this.mListPadding.bottom;
        int i3 = this.mListPadding.top;
        AbsListView.RecycleBin recycleBin = this.mRecycler;
        if (i < 0) {
            int childCount = getChildCount();
            View childAt = getChildAt(childCount - 1);
            while (childAt.getBottom() < height && (this.mFirstPosition + childCount) - 1 < this.mItemCount - 1) {
                childAt = addViewBelow(childAt, i2);
                childCount++;
            }
            if (childAt.getBottom() < height) {
                offsetChildrenTopAndBottom(height - childAt.getBottom());
            }
            View childAt2 = getChildAt(0);
            while (childAt2.getBottom() < i3) {
                if (recycleBin.shouldRecycleViewType(((AbsListView.LayoutParams) childAt2.getLayoutParams()).viewType)) {
                    recycleBin.addScrapView(childAt2, this.mFirstPosition);
                }
                detachViewFromParent(childAt2);
                childAt2 = getChildAt(0);
                this.mFirstPosition++;
            }
            return;
        }
        View childAt3 = getChildAt(0);
        while (childAt3.getTop() > i3 && this.mFirstPosition > 0) {
            childAt3 = addViewAbove(childAt3, this.mFirstPosition);
            this.mFirstPosition--;
        }
        if (childAt3.getTop() > i3) {
            offsetChildrenTopAndBottom(i3 - childAt3.getTop());
        }
        int childCount2 = getChildCount() - 1;
        View childAt4 = getChildAt(childCount2);
        while (childAt4.getTop() > height) {
            if (recycleBin.shouldRecycleViewType(((AbsListView.LayoutParams) childAt4.getLayoutParams()).viewType)) {
                recycleBin.addScrapView(childAt4, this.mFirstPosition + childCount2);
            }
            detachViewFromParent(childAt4);
            childCount2--;
            childAt4 = getChildAt(childCount2);
        }
    }

    private View addViewAbove(View view, int i) {
        int i2 = i - 1;
        View viewObtainView = obtainView(i2, this.mIsScrap);
        setupChild(viewObtainView, i2, view.getTop() - this.mDividerHeight, false, this.mListPadding.left, false, this.mIsScrap[0]);
        return viewObtainView;
    }

    private View addViewBelow(View view, int i) {
        int i2 = i + 1;
        View viewObtainView = obtainView(i2, this.mIsScrap);
        setupChild(viewObtainView, i2, view.getBottom() + this.mDividerHeight, true, this.mListPadding.left, false, this.mIsScrap[0]);
        return viewObtainView;
    }

    public void setItemsCanFocus(boolean z) {
        this.mItemsCanFocus = z;
        if (z) {
            return;
        }
        setDescendantFocusability(393216);
    }

    public boolean getItemsCanFocus() {
        return this.mItemsCanFocus;
    }

    @Override // android.view.View
    public boolean isOpaque() {
        boolean z = (this.mCachingActive && this.mIsCacheColorOpaque && this.mDividerIsOpaque && hasOpaqueScrollbars()) || super.isOpaque();
        if (z) {
            int i = this.mListPadding != null ? this.mListPadding.top : this.mPaddingTop;
            View childAt = getChildAt(0);
            if (childAt != null && childAt.getTop() <= i) {
                int height = getHeight() - (this.mListPadding != null ? this.mListPadding.bottom : this.mPaddingBottom);
                View childAt2 = getChildAt(getChildCount() - 1);
                if (childAt2 == null || childAt2.getBottom() < height) {
                }
            }
            return false;
        }
        return z;
    }

    @Override // android.widget.AbsListView
    public void setCacheColorHint(int i) {
        boolean z = (i >>> 24) == 255;
        this.mIsCacheColorOpaque = z;
        if (z) {
            if (this.mDividerPaint == null) {
                this.mDividerPaint = new Paint();
            }
            this.mDividerPaint.setColor(i);
        }
        super.setCacheColorHint(i);
    }

    void drawOverscrollHeader(Canvas canvas, Drawable drawable, Rect rect) {
        int minimumHeight = drawable.getMinimumHeight();
        canvas.save();
        canvas.clipRect(rect);
        if (rect.bottom - rect.top < minimumHeight) {
            rect.top = rect.bottom - minimumHeight;
        }
        drawable.setBounds(rect);
        drawable.draw(canvas);
        canvas.restore();
    }

    void drawOverscrollFooter(Canvas canvas, Drawable drawable, Rect rect) {
        int minimumHeight = drawable.getMinimumHeight();
        canvas.save();
        canvas.clipRect(rect);
        if (rect.bottom - rect.top < minimumHeight) {
            rect.bottom = rect.top + minimumHeight;
        }
        drawable.setBounds(rect);
        drawable.draw(canvas);
        canvas.restore();
    }

    @Override // android.widget.AbsListView, android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        boolean z;
        ListAdapter listAdapter;
        int i;
        int i2;
        Drawable drawable;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        boolean z2;
        ListAdapter listAdapter2;
        Paint paint;
        if (this.mCachingStarted) {
            this.mCachingActive = true;
        }
        int i8 = this.mDividerHeight;
        Drawable drawable2 = this.mOverScrollHeader;
        Drawable drawable3 = this.mOverScrollFooter;
        int i9 = drawable2 != null ? 1 : 0;
        boolean z3 = drawable3 != null;
        boolean z4 = i8 > 0 && this.mDivider != null;
        if (z4 || i9 != 0 || z3) {
            Rect rect = this.mTempRect;
            rect.left = this.mPaddingLeft;
            rect.right = (this.mRight - this.mLeft) - this.mPaddingRight;
            int childCount = getChildCount();
            int size = this.mHeaderViewInfos.size();
            int i10 = this.mItemCount;
            int size2 = i10 - this.mFooterViewInfos.size();
            boolean z5 = this.mHeaderDividersEnabled;
            boolean z6 = this.mFooterDividersEnabled;
            int i11 = this.mFirstPosition;
            boolean z7 = this.mAreAllItemsSelectable;
            ListAdapter listAdapter3 = this.mAdapter;
            boolean z8 = isOpaque() && !super.isOpaque();
            if (z8) {
                listAdapter = listAdapter3;
                if (this.mDividerPaint == null && this.mIsCacheColorOpaque) {
                    Paint paint2 = new Paint();
                    this.mDividerPaint = paint2;
                    z = z7;
                    paint2.setColor(getCacheColorHint());
                } else {
                    z = z7;
                }
            } else {
                z = z7;
                listAdapter = listAdapter3;
            }
            Paint paint3 = this.mDividerPaint;
            if ((this.mGroupFlags & 34) == 34) {
                int i12 = this.mListPadding.top;
                i = this.mListPadding.bottom;
                i2 = i12;
            } else {
                i = 0;
                i2 = 0;
            }
            boolean z9 = z3;
            int i13 = ((this.mBottom - this.mTop) - i) + this.mScrollY;
            if (!this.mStackFromBottom) {
                int i14 = this.mScrollY;
                if (childCount > 0 && i14 < 0) {
                    if (i9 != 0) {
                        rect.bottom = 0;
                        rect.top = i14;
                        drawOverscrollHeader(canvas, drawable2, rect);
                    } else if (z4) {
                        rect.bottom = 0;
                        rect.top = -i8;
                        drawDivider(canvas, rect, -1);
                    }
                }
                int bottom = 0;
                int i15 = 0;
                while (i15 < childCount) {
                    int i16 = i11 + i15;
                    boolean z10 = i16 < size;
                    boolean z11 = i16 >= size2;
                    if ((z5 || !z10) && (z6 || !z11)) {
                        bottom = getChildAt(i15).getBottom();
                        i6 = i11;
                        boolean z12 = i15 == childCount + (-1);
                        if (!z4 || bottom >= i13 || (z9 && z12)) {
                            i7 = i13;
                        } else {
                            i7 = i13;
                            int i17 = i16 + 1;
                            z2 = z4;
                            listAdapter2 = listAdapter;
                            if (z || ((listAdapter2.isEnabled(i16) || ((z5 && z10) || (z6 && z11))) && (z12 || listAdapter2.isEnabled(i17) || ((z5 && i17 < size) || (z6 && i17 >= size2))))) {
                                paint = paint3;
                                rect.top = bottom;
                                rect.bottom = bottom + i8;
                                drawDivider(canvas, rect, i15);
                            } else if (z8) {
                                rect.top = bottom;
                                rect.bottom = bottom + i8;
                                paint = paint3;
                                canvas.drawRect(rect, paint);
                            } else {
                                paint = paint3;
                            }
                            i15++;
                            paint3 = paint;
                            listAdapter = listAdapter2;
                            i11 = i6;
                            i13 = i7;
                            z4 = z2;
                        }
                    } else {
                        i7 = i13;
                        i6 = i11;
                    }
                    z2 = z4;
                    listAdapter2 = listAdapter;
                    paint = paint3;
                    i15++;
                    paint3 = paint;
                    listAdapter = listAdapter2;
                    i11 = i6;
                    i13 = i7;
                    z4 = z2;
                }
                int i18 = i11;
                int i19 = this.mBottom + this.mScrollY;
                if (z9 && i18 + childCount == i10 && i19 > bottom) {
                    rect.top = bottom;
                    rect.bottom = i19;
                    drawOverscrollFooter(canvas, drawable3, rect);
                }
            } else {
                boolean z13 = z4;
                Drawable drawable4 = drawable3;
                ListAdapter listAdapter4 = listAdapter;
                int i20 = this.mScrollY;
                if (childCount > 0 && i9 != 0) {
                    rect.top = i20;
                    rect.bottom = getChildAt(0).getTop();
                    drawOverscrollHeader(canvas, drawable2, rect);
                }
                int i21 = i9;
                while (i21 < childCount) {
                    int i22 = i11 + i21;
                    boolean z14 = i22 < size;
                    boolean z15 = i22 >= size2;
                    if ((z5 || !z14) && (z6 || !z15)) {
                        drawable = drawable4;
                        int top = getChildAt(i21).getTop();
                        if (z13) {
                            i3 = i20;
                            i4 = i2;
                            if (top > i4) {
                                boolean z16 = i21 == i9;
                                i5 = i9;
                                int i23 = i22 - 1;
                                if (z || ((listAdapter4.isEnabled(i22) || ((z5 && z14) || (z6 && z15))) && (z16 || listAdapter4.isEnabled(i23) || ((z5 && i23 < size) || (z6 && i23 >= size2))))) {
                                    rect.top = top - i8;
                                    rect.bottom = top;
                                    drawDivider(canvas, rect, i21 - 1);
                                } else if (z8) {
                                    rect.top = top - i8;
                                    rect.bottom = top;
                                    canvas.drawRect(rect, paint3);
                                }
                            } else {
                                i5 = i9;
                            }
                        }
                        i21++;
                        i2 = i4;
                        i20 = i3;
                        drawable4 = drawable;
                        i9 = i5;
                    } else {
                        drawable = drawable4;
                    }
                    i5 = i9;
                    i3 = i20;
                    i4 = i2;
                    i21++;
                    i2 = i4;
                    i20 = i3;
                    drawable4 = drawable;
                    i9 = i5;
                }
                Drawable drawable5 = drawable4;
                int i24 = i20;
                if (childCount > 0 && i24 > 0) {
                    if (z9) {
                        int i25 = this.mBottom;
                        rect.top = i25;
                        rect.bottom = i25 + i24;
                        drawOverscrollFooter(canvas, drawable5, rect);
                    } else if (z13) {
                        rect.top = i13;
                        rect.bottom = i13 + i8;
                        drawDivider(canvas, rect, -1);
                    }
                }
            }
        }
        super.dispatchDraw(canvas);
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View view, long j) {
        boolean zDrawChild = super.drawChild(canvas, view, j);
        if (this.mCachingActive && view.mCachingFailed) {
            this.mCachingActive = false;
        }
        return zDrawChild;
    }

    void drawDivider(Canvas canvas, Rect rect, int i) {
        Drawable drawable = this.mDivider;
        drawable.setBounds(rect);
        drawable.draw(canvas);
    }

    public Drawable getDivider() {
        return this.mDivider;
    }

    public void setDivider(Drawable drawable) {
        if (drawable != null) {
            this.mDividerHeight = drawable.getIntrinsicHeight();
        } else {
            this.mDividerHeight = 0;
        }
        this.mDivider = drawable;
        this.mDividerIsOpaque = drawable == null || drawable.getOpacity() == -1;
        requestLayout();
        invalidate();
    }

    public int getDividerHeight() {
        return this.mDividerHeight;
    }

    public void setDividerHeight(int i) {
        this.mDividerHeight = i;
        requestLayout();
        invalidate();
    }

    public void setHeaderDividersEnabled(boolean z) {
        this.mHeaderDividersEnabled = z;
        invalidate();
    }

    public boolean areHeaderDividersEnabled() {
        return this.mHeaderDividersEnabled;
    }

    public void setFooterDividersEnabled(boolean z) {
        this.mFooterDividersEnabled = z;
        invalidate();
    }

    public boolean areFooterDividersEnabled() {
        return this.mFooterDividersEnabled;
    }

    public void setOverscrollHeader(Drawable drawable) {
        this.mOverScrollHeader = drawable;
        if (this.mScrollY < 0) {
            invalidate();
        }
    }

    public Drawable getOverscrollHeader() {
        return this.mOverScrollHeader;
    }

    public void setOverscrollFooter(Drawable drawable) {
        this.mOverScrollFooter = drawable;
        invalidate();
    }

    public Drawable getOverscrollFooter() {
        return this.mOverScrollFooter;
    }

    @Override // android.widget.AbsListView, android.view.View
    protected void onFocusChanged(boolean z, int i, Rect rect) throws Throwable {
        super.onFocusChanged(z, i, rect);
        ListAdapter listAdapter = this.mAdapter;
        int i2 = 0;
        int i3 = -1;
        if (listAdapter != null && z && rect != null) {
            rect.offset(this.mScrollX, this.mScrollY);
            if (listAdapter.getCount() < getChildCount() + this.mFirstPosition) {
                this.mLayoutMode = 0;
                layoutChildren();
            }
            Rect rect2 = this.mTempRect;
            int childCount = getChildCount();
            int i4 = this.mFirstPosition;
            int i5 = Integer.MAX_VALUE;
            int i6 = -1;
            int top = 0;
            while (i2 < childCount) {
                if (listAdapter.isEnabled(i4 + i2)) {
                    View childAt = getChildAt(i2);
                    childAt.getDrawingRect(rect2);
                    offsetDescendantRectToMyCoords(childAt, rect2);
                    int distance = getDistance(rect, rect2, i);
                    if (distance < i5) {
                        top = childAt.getTop();
                        i6 = i2;
                        i5 = distance;
                    }
                }
                i2++;
            }
            i2 = top;
            i3 = i6;
        }
        if (i3 >= 0) {
            setSelectionFromTop(i3 + this.mFirstPosition, i2);
        } else {
            requestLayout();
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                addHeaderView(getChildAt(i));
            }
            removeAllViews();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected View findViewTraversal(int i) {
        View viewFindViewTraversal = super.findViewTraversal(i);
        if (viewFindViewTraversal == null) {
            View viewFindViewInHeadersOrFooters = findViewInHeadersOrFooters(this.mHeaderViewInfos, i);
            if (viewFindViewInHeadersOrFooters != null) {
                return viewFindViewInHeadersOrFooters;
            }
            viewFindViewTraversal = findViewInHeadersOrFooters(this.mFooterViewInfos, i);
            if (viewFindViewTraversal != null) {
            }
        }
        return viewFindViewTraversal;
    }

    View findViewInHeadersOrFooters(ArrayList<FixedViewInfo> arrayList, int i) {
        View viewFindViewById;
        if (arrayList == null) {
            return null;
        }
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            View view = arrayList.get(i2).view;
            if (!view.isRootNamespace() && (viewFindViewById = view.findViewById(i)) != null) {
                return viewFindViewById;
            }
        }
        return null;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected View findViewWithTagTraversal(Object obj) {
        View viewFindViewWithTagTraversal = super.findViewWithTagTraversal(obj);
        if (viewFindViewWithTagTraversal == null) {
            View viewFindViewWithTagInHeadersOrFooters = findViewWithTagInHeadersOrFooters(this.mHeaderViewInfos, obj);
            if (viewFindViewWithTagInHeadersOrFooters != null) {
                return viewFindViewWithTagInHeadersOrFooters;
            }
            viewFindViewWithTagTraversal = findViewWithTagInHeadersOrFooters(this.mFooterViewInfos, obj);
            if (viewFindViewWithTagTraversal != null) {
            }
        }
        return viewFindViewWithTagTraversal;
    }

    View findViewWithTagInHeadersOrFooters(ArrayList<FixedViewInfo> arrayList, Object obj) {
        View viewFindViewWithTag;
        if (arrayList == null) {
            return null;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            View view = arrayList.get(i).view;
            if (!view.isRootNamespace() && (viewFindViewWithTag = view.findViewWithTag(obj)) != null) {
                return viewFindViewWithTag;
            }
        }
        return null;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected View findViewByPredicateTraversal(Predicate<View> predicate, View view) {
        View viewFindViewByPredicateTraversal = super.findViewByPredicateTraversal(predicate, view);
        if (viewFindViewByPredicateTraversal == null) {
            View viewFindViewByPredicateInHeadersOrFooters = findViewByPredicateInHeadersOrFooters(this.mHeaderViewInfos, predicate, view);
            if (viewFindViewByPredicateInHeadersOrFooters != null) {
                return viewFindViewByPredicateInHeadersOrFooters;
            }
            viewFindViewByPredicateTraversal = findViewByPredicateInHeadersOrFooters(this.mFooterViewInfos, predicate, view);
            if (viewFindViewByPredicateTraversal != null) {
            }
        }
        return viewFindViewByPredicateTraversal;
    }

    View findViewByPredicateInHeadersOrFooters(ArrayList<FixedViewInfo> arrayList, Predicate<View> predicate, View view) {
        View viewFindViewByPredicate;
        if (arrayList == null) {
            return null;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            View view2 = arrayList.get(i).view;
            if (view2 != view && !view2.isRootNamespace() && (viewFindViewByPredicate = view2.findViewByPredicate(predicate)) != null) {
                return viewFindViewByPredicate;
            }
        }
        return null;
    }

    @Deprecated
    public long[] getCheckItemIds() {
        if (this.mAdapter != null && this.mAdapter.hasStableIds()) {
            return getCheckedItemIds();
        }
        if (this.mChoiceMode == 0 || this.mCheckStates == null || this.mAdapter == null) {
            return new long[0];
        }
        SparseBooleanArray sparseBooleanArray = this.mCheckStates;
        int size = sparseBooleanArray.size();
        long[] jArr = new long[size];
        ListAdapter listAdapter = this.mAdapter;
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            if (sparseBooleanArray.valueAt(i2)) {
                jArr[i] = listAdapter.getItemId(sparseBooleanArray.keyAt(i2));
                i++;
            }
        }
        if (i == size) {
            return jArr;
        }
        long[] jArr2 = new long[i];
        System.arraycopy(jArr, 0, jArr2, 0, i);
        return jArr2;
    }

    @Override // android.widget.AbsListView, android.widget.AdapterView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(ListView.class.getName());
    }

    @Override // android.widget.AbsListView, android.widget.AdapterView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(ListView.class.getName());
        accessibilityNodeInfo.setCollectionInfo(AccessibilityNodeInfo.CollectionInfo.obtain(1, getCount(), false));
    }

    @Override // android.widget.AbsListView
    public void onInitializeAccessibilityNodeInfoForItem(View view, int i, AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfoForItem(view, i, accessibilityNodeInfo);
        AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) view.getLayoutParams();
        accessibilityNodeInfo.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(0, 1, i, 1, (layoutParams == null || layoutParams.viewType == -2) ? false : true));
    }
}
