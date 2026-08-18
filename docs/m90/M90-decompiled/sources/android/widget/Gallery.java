package android.widget;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Transformation;
import android.widget.AdapterView;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class Gallery extends AbsSpinner implements GestureDetector.OnGestureListener {
    private static final int SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT = 250;
    private static final String TAG = "Gallery";
    private static final boolean localLOGV = false;
    private int mAnimationDuration;
    private AdapterView.AdapterContextMenuInfo mContextMenuInfo;
    private Runnable mDisableSuppressSelectionChangedRunnable;
    private int mDownTouchPosition;
    private View mDownTouchView;
    private FlingRunnable mFlingRunnable;
    private GestureDetector mGestureDetector;
    private int mGravity;
    private boolean mIsFirstScroll;
    private boolean mIsRtl;
    private int mLeftMost;
    private boolean mReceivedInvokeKeyDown;
    private int mRightMost;
    private int mSelectedCenterOffset;
    private View mSelectedChild;
    private boolean mShouldCallbackDuringFling;
    private boolean mShouldCallbackOnUnselectedItemClick;
    private boolean mShouldStopFling;
    private int mSpacing;
    private boolean mSuppressSelectionChanged;
    private float mUnselectedAlpha;

    @Override // android.view.View
    protected int computeHorizontalScrollExtent() {
        return 1;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchSetSelected(boolean z) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onShowPress(MotionEvent motionEvent) {
    }

    public Gallery(Context context) {
        this(context, null);
    }

    public Gallery(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.galleryStyle);
    }

    public Gallery(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSpacing = 0;
        this.mAnimationDuration = 400;
        this.mFlingRunnable = new FlingRunnable();
        this.mDisableSuppressSelectionChangedRunnable = new Runnable() { // from class: android.widget.Gallery.1
            @Override // java.lang.Runnable
            public void run() {
                Gallery.this.mSuppressSelectionChanged = false;
                Gallery.this.selectionChanged();
            }
        };
        this.mShouldCallbackDuringFling = true;
        this.mShouldCallbackOnUnselectedItemClick = true;
        this.mIsRtl = true;
        GestureDetector gestureDetector = new GestureDetector(context, this);
        this.mGestureDetector = gestureDetector;
        gestureDetector.setIsLongpressEnabled(true);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, com.android.internal.R.styleable.Gallery, i, 0);
        int i2 = typedArrayObtainStyledAttributes.getInt(0, -1);
        if (i2 >= 0) {
            setGravity(i2);
        }
        int i3 = typedArrayObtainStyledAttributes.getInt(1, -1);
        if (i3 > 0) {
            setAnimationDuration(i3);
        }
        setSpacing(typedArrayObtainStyledAttributes.getDimensionPixelOffset(2, 0));
        setUnselectedAlpha(typedArrayObtainStyledAttributes.getFloat(3, 0.5f));
        typedArrayObtainStyledAttributes.recycle();
        this.mGroupFlags |= 1024;
        this.mGroupFlags |= 2048;
    }

    public void setCallbackDuringFling(boolean z) {
        this.mShouldCallbackDuringFling = z;
    }

    public void setCallbackOnUnselectedItemClick(boolean z) {
        this.mShouldCallbackOnUnselectedItemClick = z;
    }

    public void setAnimationDuration(int i) {
        this.mAnimationDuration = i;
    }

    public void setSpacing(int i) {
        this.mSpacing = i;
    }

    public void setUnselectedAlpha(float f) {
        this.mUnselectedAlpha = f;
    }

    @Override // android.view.ViewGroup
    protected boolean getChildStaticTransformation(View view, Transformation transformation) {
        transformation.clear();
        transformation.setAlpha(view == this.mSelectedChild ? 1.0f : this.mUnselectedAlpha);
        return true;
    }

    @Override // android.view.View
    protected int computeHorizontalScrollOffset() {
        return this.mSelectedPosition;
    }

    @Override // android.view.View
    protected int computeHorizontalScrollRange() {
        return this.mItemCount;
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    @Override // android.widget.AbsSpinner, android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mInLayout = true;
        layout(0, false);
        this.mInLayout = false;
    }

    @Override // android.widget.AbsSpinner
    int getChildHeight(View view) {
        return view.getMeasuredHeight();
    }

    void trackMotionScroll(int i) {
        if (getChildCount() == 0) {
            return;
        }
        boolean z = i < 0;
        int limitedMotionScrollAmount = getLimitedMotionScrollAmount(z, i);
        if (limitedMotionScrollAmount != i) {
            this.mFlingRunnable.endFling(false);
            onFinishedMovement();
        }
        offsetChildrenLeftAndRight(limitedMotionScrollAmount);
        detachOffScreenChildren(z);
        if (z) {
            fillToGalleryRight();
        } else {
            fillToGalleryLeft();
        }
        this.mRecycler.clear();
        setSelectionToCenterChild();
        View view = this.mSelectedChild;
        if (view != null) {
            this.mSelectedCenterOffset = (view.getLeft() + (view.getWidth() / 2)) - (getWidth() / 2);
        }
        onScrollChanged(0, 0, 0, 0);
        invalidate();
    }

    int getLimitedMotionScrollAmount(boolean z, int i) {
        View childAt = getChildAt((z != this.mIsRtl ? this.mItemCount - 1 : 0) - this.mFirstPosition);
        if (childAt == null) {
            return i;
        }
        int centerOfView = getCenterOfView(childAt);
        int centerOfGallery = getCenterOfGallery();
        if (z) {
            if (centerOfView <= centerOfGallery) {
                return 0;
            }
        } else if (centerOfView >= centerOfGallery) {
            return 0;
        }
        int i2 = centerOfGallery - centerOfView;
        return z ? Math.max(i2, i) : Math.min(i2, i);
    }

    private void offsetChildrenLeftAndRight(int i) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            getChildAt(childCount).offsetLeftAndRight(i);
        }
    }

    private int getCenterOfGallery() {
        return (((getWidth() - this.mPaddingLeft) - this.mPaddingRight) / 2) + this.mPaddingLeft;
    }

    private static int getCenterOfView(View view) {
        return view.getLeft() + (view.getWidth() / 2);
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x0069 A[PHI: r5 r6
      0x0069: PHI (r5v4 int) = (r5v1 int), (r5v6 int) binds: [B:28:0x0066, B:15:0x0033] A[DONT_GENERATE, DONT_INLINE]
      0x0069: PHI (r6v4 int) = (r6v1 int), (r6v6 int) binds: [B:28:0x0066, B:15:0x0033] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void detachOffScreenChildren(boolean r11) {
        /*
            r10 = this;
            int r0 = r10.getChildCount()
            int r1 = r10.mFirstPosition
            r2 = 0
            if (r11 == 0) goto L36
            int r3 = r10.mPaddingLeft
            r4 = r2
            r5 = r4
            r6 = r5
        Le:
            if (r4 >= r0) goto L31
            boolean r7 = r10.mIsRtl
            if (r7 == 0) goto L18
            int r7 = r0 + (-1)
            int r7 = r7 - r4
            goto L19
        L18:
            r7 = r4
        L19:
            android.view.View r8 = r10.getChildAt(r7)
            int r9 = r8.getRight()
            if (r9 < r3) goto L24
            goto L31
        L24:
            int r5 = r5 + 1
            android.widget.AbsSpinner$RecycleBin r6 = r10.mRecycler
            int r9 = r1 + r7
            r6.put(r9, r8)
            int r4 = r4 + 1
            r6 = r7
            goto Le
        L31:
            boolean r0 = r10.mIsRtl
            if (r0 != 0) goto L69
            goto L6a
        L36:
            int r3 = r10.getWidth()
            int r4 = r10.mPaddingRight
            int r3 = r3 - r4
            int r0 = r0 + (-1)
            r4 = r0
            r5 = r2
            r6 = r5
        L42:
            if (r4 < 0) goto L64
            boolean r7 = r10.mIsRtl
            if (r7 == 0) goto L4b
            int r7 = r0 - r4
            goto L4c
        L4b:
            r7 = r4
        L4c:
            android.view.View r8 = r10.getChildAt(r7)
            int r9 = r8.getLeft()
            if (r9 > r3) goto L57
            goto L64
        L57:
            int r5 = r5 + 1
            android.widget.AbsSpinner$RecycleBin r6 = r10.mRecycler
            int r9 = r1 + r7
            r6.put(r9, r8)
            int r4 = r4 + (-1)
            r6 = r7
            goto L42
        L64:
            boolean r0 = r10.mIsRtl
            if (r0 == 0) goto L69
            goto L6a
        L69:
            r2 = r6
        L6a:
            r10.detachViewsFromParent(r2, r5)
            boolean r0 = r10.mIsRtl
            if (r11 == r0) goto L76
            int r11 = r10.mFirstPosition
            int r11 = r11 + r5
            r10.mFirstPosition = r11
        L76:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.Gallery.detachOffScreenChildren(boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scrollIntoSlots() {
        View view;
        if (getChildCount() == 0 || (view = this.mSelectedChild) == null) {
            return;
        }
        int centerOfGallery = getCenterOfGallery() - getCenterOfView(view);
        if (centerOfGallery != 0) {
            this.mFlingRunnable.startUsingDistance(centerOfGallery);
        } else {
            onFinishedMovement();
        }
    }

    private void onFinishedMovement() {
        if (this.mSuppressSelectionChanged) {
            this.mSuppressSelectionChanged = false;
            super.selectionChanged();
        }
        this.mSelectedCenterOffset = 0;
        invalidate();
    }

    @Override // android.widget.AdapterView
    void selectionChanged() {
        if (this.mSuppressSelectionChanged) {
            return;
        }
        super.selectionChanged();
    }

    private void setSelectionToCenterChild() {
        View view = this.mSelectedChild;
        if (view == null) {
            return;
        }
        int centerOfGallery = getCenterOfGallery();
        if (view.getLeft() > centerOfGallery || view.getRight() < centerOfGallery) {
            int i = Integer.MAX_VALUE;
            int i2 = 0;
            int childCount = getChildCount() - 1;
            while (true) {
                if (childCount < 0) {
                    break;
                }
                View childAt = getChildAt(childCount);
                if (childAt.getLeft() <= centerOfGallery && childAt.getRight() >= centerOfGallery) {
                    i2 = childCount;
                    break;
                }
                int iMin = Math.min(Math.abs(childAt.getLeft() - centerOfGallery), Math.abs(childAt.getRight() - centerOfGallery));
                if (iMin < i) {
                    i2 = childCount;
                    i = iMin;
                }
                childCount--;
            }
            int i3 = this.mFirstPosition + i2;
            if (i3 != this.mSelectedPosition) {
                setSelectedPositionInt(i3);
                setNextSelectedPositionInt(i3);
                checkSelectionChanged();
            }
        }
    }

    @Override // android.widget.AbsSpinner
    void layout(int i, boolean z) {
        this.mIsRtl = isLayoutRtl();
        int i2 = this.mSpinnerPadding.left;
        int i3 = ((this.mRight - this.mLeft) - this.mSpinnerPadding.left) - this.mSpinnerPadding.right;
        if (this.mDataChanged) {
            handleDataChanged();
        }
        if (this.mItemCount == 0) {
            resetList();
            return;
        }
        if (this.mNextSelectedPosition >= 0) {
            setSelectedPositionInt(this.mNextSelectedPosition);
        }
        recycleAllViews();
        detachAllViewsFromParent();
        this.mRightMost = 0;
        this.mLeftMost = 0;
        this.mFirstPosition = this.mSelectedPosition;
        View viewMakeAndAddView = makeAndAddView(this.mSelectedPosition, 0, 0, true);
        viewMakeAndAddView.offsetLeftAndRight(((i2 + (i3 / 2)) - (viewMakeAndAddView.getWidth() / 2)) + this.mSelectedCenterOffset);
        fillToGalleryRight();
        fillToGalleryLeft();
        this.mRecycler.clear();
        invalidate();
        checkSelectionChanged();
        this.mDataChanged = false;
        this.mNeedSync = false;
        setNextSelectedPositionInt(this.mSelectedPosition);
        updateSelectedItemMetadata();
    }

    private void fillToGalleryLeft() {
        if (this.mIsRtl) {
            fillToGalleryLeftRtl();
        } else {
            fillToGalleryLeftLtr();
        }
    }

    private void fillToGalleryLeftRtl() {
        int i;
        int left;
        int i2 = this.mSpacing;
        int i3 = this.mPaddingLeft;
        int childCount = getChildCount();
        int i4 = this.mItemCount;
        View childAt = getChildAt(childCount - 1);
        if (childAt != null) {
            i = this.mFirstPosition + childCount;
            left = childAt.getLeft() - i2;
        } else {
            i = this.mItemCount - 1;
            this.mFirstPosition = i;
            left = (this.mRight - this.mLeft) - this.mPaddingRight;
            this.mShouldStopFling = true;
        }
        while (left > i3 && i < this.mItemCount) {
            left = makeAndAddView(i, i - this.mSelectedPosition, left, false).getLeft() - i2;
            i++;
        }
    }

    private void fillToGalleryLeftLtr() {
        int left;
        int i;
        int i2 = this.mSpacing;
        int i3 = this.mPaddingLeft;
        View childAt = getChildAt(0);
        if (childAt != null) {
            i = this.mFirstPosition - 1;
            left = childAt.getLeft() - i2;
        } else {
            left = (this.mRight - this.mLeft) - this.mPaddingRight;
            this.mShouldStopFling = true;
            i = 0;
        }
        while (left > i3 && i >= 0) {
            View viewMakeAndAddView = makeAndAddView(i, i - this.mSelectedPosition, left, false);
            this.mFirstPosition = i;
            left = viewMakeAndAddView.getLeft() - i2;
            i--;
        }
    }

    private void fillToGalleryRight() {
        if (this.mIsRtl) {
            fillToGalleryRightRtl();
        } else {
            fillToGalleryRightLtr();
        }
    }

    private void fillToGalleryRightRtl() {
        int right;
        int i = this.mSpacing;
        int i2 = (this.mRight - this.mLeft) - this.mPaddingRight;
        int i3 = 0;
        View childAt = getChildAt(0);
        if (childAt != null) {
            i3 = this.mFirstPosition - 1;
            right = childAt.getRight() + i;
        } else {
            right = this.mPaddingLeft;
            this.mShouldStopFling = true;
        }
        while (right < i2 && i3 >= 0) {
            View viewMakeAndAddView = makeAndAddView(i3, i3 - this.mSelectedPosition, right, true);
            this.mFirstPosition = i3;
            right = viewMakeAndAddView.getRight() + i;
            i3--;
        }
    }

    private void fillToGalleryRightLtr() {
        int i;
        int right;
        int i2 = this.mSpacing;
        int i3 = (this.mRight - this.mLeft) - this.mPaddingRight;
        int childCount = getChildCount();
        int i4 = this.mItemCount;
        View childAt = getChildAt(childCount - 1);
        if (childAt != null) {
            i = this.mFirstPosition + childCount;
            right = childAt.getRight() + i2;
        } else {
            i = this.mItemCount - 1;
            this.mFirstPosition = i;
            right = this.mPaddingLeft;
            this.mShouldStopFling = true;
        }
        while (right < i3 && i < i4) {
            right = makeAndAddView(i, i - this.mSelectedPosition, right, true).getRight() + i2;
            i++;
        }
    }

    private View makeAndAddView(int i, int i2, int i3, boolean z) {
        View view;
        if (!this.mDataChanged && (view = this.mRecycler.get(i)) != null) {
            int left = view.getLeft();
            this.mRightMost = Math.max(this.mRightMost, view.getMeasuredWidth() + left);
            this.mLeftMost = Math.min(this.mLeftMost, left);
            setUpChild(view, i2, i3, z);
            return view;
        }
        View view2 = this.mAdapter.getView(i, null, this);
        setUpChild(view2, i2, i3, z);
        return view2;
    }

    private void setUpChild(View view, int i, int i2, boolean z) {
        int i3;
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = (LayoutParams) generateDefaultLayoutParams();
        }
        addViewInLayout(view, z != this.mIsRtl ? -1 : 0, layoutParams, true);
        view.setSelected(i == 0);
        view.measure(ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mSpinnerPadding.left + this.mSpinnerPadding.right, layoutParams.width), ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mSpinnerPadding.top + this.mSpinnerPadding.bottom, layoutParams.height));
        int iCalculateTop = calculateTop(view, true);
        int measuredHeight = view.getMeasuredHeight() + iCalculateTop;
        int measuredWidth = view.getMeasuredWidth();
        if (z) {
            i3 = measuredWidth + i2;
        } else {
            int i4 = i2 - measuredWidth;
            i3 = i2;
            i2 = i4;
        }
        view.layout(i2, iCalculateTop, i3, measuredHeight);
    }

    private int calculateTop(View view, boolean z) {
        int measuredHeight = z ? getMeasuredHeight() : getHeight();
        int measuredHeight2 = z ? view.getMeasuredHeight() : view.getHeight();
        int i = this.mGravity;
        if (i == 16) {
            return this.mSpinnerPadding.top + ((((measuredHeight - this.mSpinnerPadding.bottom) - this.mSpinnerPadding.top) - measuredHeight2) / 2);
        }
        if (i == 48) {
            return this.mSpinnerPadding.top;
        }
        if (i != 80) {
            return 0;
        }
        return (measuredHeight - this.mSpinnerPadding.bottom) - measuredHeight2;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean zOnTouchEvent = this.mGestureDetector.onTouchEvent(motionEvent);
        int action = motionEvent.getAction();
        if (action == 1) {
            onUp();
        } else if (action == 3) {
            onCancel();
        }
        return zOnTouchEvent;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        int i = this.mDownTouchPosition;
        if (i < 0) {
            return false;
        }
        scrollToChild(i - this.mFirstPosition);
        if (!this.mShouldCallbackOnUnselectedItemClick && this.mDownTouchPosition != this.mSelectedPosition) {
            return true;
        }
        performItemClick(this.mDownTouchView, this.mDownTouchPosition, this.mAdapter.getItemId(this.mDownTouchPosition));
        return true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (!this.mShouldCallbackDuringFling) {
            removeCallbacks(this.mDisableSuppressSelectionChangedRunnable);
            if (!this.mSuppressSelectionChanged) {
                this.mSuppressSelectionChanged = true;
            }
        }
        this.mFlingRunnable.startUsingVelocity((int) (-f));
        return true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        this.mParent.requestDisallowInterceptTouchEvent(true);
        if (!this.mShouldCallbackDuringFling) {
            if (this.mIsFirstScroll) {
                if (!this.mSuppressSelectionChanged) {
                    this.mSuppressSelectionChanged = true;
                }
                postDelayed(this.mDisableSuppressSelectionChangedRunnable, 250L);
            }
        } else if (this.mSuppressSelectionChanged) {
            this.mSuppressSelectionChanged = false;
        }
        trackMotionScroll(((int) f) * (-1));
        this.mIsFirstScroll = false;
        return true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onDown(MotionEvent motionEvent) {
        this.mFlingRunnable.stop(false);
        int iPointToPosition = pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
        this.mDownTouchPosition = iPointToPosition;
        if (iPointToPosition >= 0) {
            View childAt = getChildAt(iPointToPosition - this.mFirstPosition);
            this.mDownTouchView = childAt;
            childAt.setPressed(true);
        }
        this.mIsFirstScroll = true;
        return true;
    }

    void onUp() {
        if (this.mFlingRunnable.mScroller.isFinished()) {
            scrollIntoSlots();
        }
        dispatchUnpress();
    }

    void onCancel() {
        onUp();
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onLongPress(MotionEvent motionEvent) {
        if (this.mDownTouchPosition < 0) {
            return;
        }
        performHapticFeedback(0);
        dispatchLongPress(this.mDownTouchView, this.mDownTouchPosition, getItemIdAtPosition(this.mDownTouchPosition));
    }

    private void dispatchPress(View view) {
        if (view != null) {
            view.setPressed(true);
        }
        setPressed(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchUnpress() {
        int childCount = getChildCount();
        while (true) {
            childCount--;
            if (childCount >= 0) {
                getChildAt(childCount).setPressed(false);
            } else {
                setPressed(false);
                return;
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchSetPressed(boolean z) {
        View view = this.mSelectedChild;
        if (view != null) {
            view.setPressed(z);
        }
    }

    @Override // android.view.View
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return this.mContextMenuInfo;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean showContextMenuForChild(View view) {
        int positionForView = getPositionForView(view);
        if (positionForView < 0) {
            return false;
        }
        return dispatchLongPress(view, positionForView, this.mAdapter.getItemId(positionForView));
    }

    @Override // android.view.View
    public boolean showContextMenu() {
        if (!isPressed() || this.mSelectedPosition < 0) {
            return false;
        }
        return dispatchLongPress(getChildAt(this.mSelectedPosition - this.mFirstPosition), this.mSelectedPosition, this.mSelectedRowId);
    }

    private boolean dispatchLongPress(View view, int i, long j) {
        boolean zOnItemLongClick = this.mOnItemLongClickListener != null ? this.mOnItemLongClickListener.onItemLongClick(this, this.mDownTouchView, this.mDownTouchPosition, j) : false;
        if (!zOnItemLongClick) {
            this.mContextMenuInfo = new AdapterView.AdapterContextMenuInfo(view, i, j);
            zOnItemLongClick = super.showContextMenuForChild(this);
        }
        if (zOnItemLongClick) {
            performHapticFeedback(0);
        }
        return zOnItemLongClick;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return keyEvent.dispatch(this, null, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x001e  */
    @Override // android.view.View, android.view.KeyEvent.Callback
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onKeyDown(int r3, android.view.KeyEvent r4) {
        /*
            r2 = this;
            r0 = 66
            r1 = 1
            if (r3 == r0) goto L1e
            switch(r3) {
                case 21: goto L14;
                case 22: goto L9;
                case 23: goto L1e;
                default: goto L8;
            }
        L8:
            goto L20
        L9:
            boolean r0 = r2.moveNext()
            if (r0 == 0) goto L20
            r3 = 3
            r2.playSoundEffect(r3)
            return r1
        L14:
            boolean r0 = r2.movePrevious()
            if (r0 == 0) goto L20
            r2.playSoundEffect(r1)
            return r1
        L1e:
            r2.mReceivedInvokeKeyDown = r1
        L20:
            boolean r3 = super.onKeyDown(r3, r4)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.Gallery.onKeyDown(int, android.view.KeyEvent):boolean");
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (KeyEvent.isConfirmKey(i)) {
            if (this.mReceivedInvokeKeyDown && this.mItemCount > 0) {
                dispatchPress(this.mSelectedChild);
                postDelayed(new Runnable() { // from class: android.widget.Gallery.2
                    @Override // java.lang.Runnable
                    public void run() {
                        Gallery.this.dispatchUnpress();
                    }
                }, ViewConfiguration.getPressedStateDuration());
                performItemClick(getChildAt(this.mSelectedPosition - this.mFirstPosition), this.mSelectedPosition, this.mAdapter.getItemId(this.mSelectedPosition));
            }
            this.mReceivedInvokeKeyDown = false;
            return true;
        }
        return super.onKeyUp(i, keyEvent);
    }

    boolean movePrevious() {
        if (this.mItemCount <= 0 || this.mSelectedPosition <= 0) {
            return false;
        }
        scrollToChild((this.mSelectedPosition - this.mFirstPosition) - 1);
        return true;
    }

    boolean moveNext() {
        if (this.mItemCount <= 0 || this.mSelectedPosition >= this.mItemCount - 1) {
            return false;
        }
        scrollToChild((this.mSelectedPosition - this.mFirstPosition) + 1);
        return true;
    }

    private boolean scrollToChild(int i) {
        View childAt = getChildAt(i);
        if (childAt == null) {
            return false;
        }
        this.mFlingRunnable.startUsingDistance(getCenterOfGallery() - getCenterOfView(childAt));
        return true;
    }

    @Override // android.widget.AdapterView
    void setSelectedPositionInt(int i) {
        super.setSelectedPositionInt(i);
        updateSelectedItemMetadata();
    }

    private void updateSelectedItemMetadata() {
        View view = this.mSelectedChild;
        View childAt = getChildAt(this.mSelectedPosition - this.mFirstPosition);
        this.mSelectedChild = childAt;
        if (childAt == null) {
            return;
        }
        childAt.setSelected(true);
        childAt.setFocusable(true);
        if (hasFocus()) {
            childAt.requestFocus();
        }
        if (view == null || view == childAt) {
            return;
        }
        view.setSelected(false);
        view.setFocusable(false);
    }

    public void setGravity(int i) {
        if (this.mGravity != i) {
            this.mGravity = i;
            requestLayout();
        }
    }

    @Override // android.view.ViewGroup
    protected int getChildDrawingOrder(int i, int i2) {
        int i3 = this.mSelectedPosition - this.mFirstPosition;
        return i3 < 0 ? i2 : i2 == i + (-1) ? i3 : i2 >= i3 ? i2 + 1 : i2;
    }

    @Override // android.view.View
    protected void onFocusChanged(boolean z, int i, Rect rect) {
        View view;
        super.onFocusChanged(z, i, rect);
        if (!z || (view = this.mSelectedChild) == null) {
            return;
        }
        view.requestFocus(i);
        this.mSelectedChild.setSelected(true);
    }

    @Override // android.widget.AbsSpinner, android.widget.AdapterView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(Gallery.class.getName());
    }

    @Override // android.widget.AbsSpinner, android.widget.AdapterView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(Gallery.class.getName());
        accessibilityNodeInfo.setScrollable(this.mItemCount > 1);
        if (isEnabled()) {
            if (this.mItemCount > 0 && this.mSelectedPosition < this.mItemCount - 1) {
                accessibilityNodeInfo.addAction(4096);
            }
            if (!isEnabled() || this.mItemCount <= 0 || this.mSelectedPosition <= 0) {
                return;
            }
            accessibilityNodeInfo.addAction(8192);
        }
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        if (super.performAccessibilityAction(i, bundle)) {
            return true;
        }
        if (i == 4096) {
            if (!isEnabled() || this.mItemCount <= 0 || this.mSelectedPosition >= this.mItemCount - 1) {
                return false;
            }
            return scrollToChild((this.mSelectedPosition - this.mFirstPosition) + 1);
        }
        if (i == 8192 && isEnabled() && this.mItemCount > 0 && this.mSelectedPosition > 0) {
            return scrollToChild((this.mSelectedPosition - this.mFirstPosition) - 1);
        }
        return false;
    }

    private class FlingRunnable implements Runnable {
        private int mLastFlingX;
        private Scroller mScroller;

        public FlingRunnable() {
            this.mScroller = new Scroller(Gallery.this.getContext());
        }

        private void startCommon() {
            Gallery.this.removeCallbacks(this);
        }

        public void startUsingVelocity(int i) {
            if (i == 0) {
                return;
            }
            startCommon();
            int i2 = i < 0 ? Integer.MAX_VALUE : 0;
            this.mLastFlingX = i2;
            this.mScroller.fling(i2, 0, i, 0, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            Gallery.this.post(this);
        }

        public void startUsingDistance(int i) {
            if (i == 0) {
                return;
            }
            startCommon();
            this.mLastFlingX = 0;
            this.mScroller.startScroll(0, 0, -i, 0, Gallery.this.mAnimationDuration);
            Gallery.this.post(this);
        }

        public void stop(boolean z) {
            Gallery.this.removeCallbacks(this);
            endFling(z);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void endFling(boolean z) {
            this.mScroller.forceFinished(true);
            if (z) {
                Gallery.this.scrollIntoSlots();
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            int iMax;
            if (Gallery.this.mItemCount != 0) {
                Gallery.this.mShouldStopFling = false;
                Scroller scroller = this.mScroller;
                boolean zComputeScrollOffset = scroller.computeScrollOffset();
                int currX = scroller.getCurrX();
                int i = this.mLastFlingX - currX;
                if (i > 0) {
                    Gallery gallery = Gallery.this;
                    gallery.mDownTouchPosition = gallery.mIsRtl ? (Gallery.this.mFirstPosition + Gallery.this.getChildCount()) - 1 : Gallery.this.mFirstPosition;
                    iMax = Math.min(((Gallery.this.getWidth() - Gallery.this.mPaddingLeft) - Gallery.this.mPaddingRight) - 1, i);
                } else {
                    Gallery.this.getChildCount();
                    Gallery gallery2 = Gallery.this;
                    gallery2.mDownTouchPosition = gallery2.mIsRtl ? Gallery.this.mFirstPosition : (Gallery.this.mFirstPosition + Gallery.this.getChildCount()) - 1;
                    iMax = Math.max(-(((Gallery.this.getWidth() - Gallery.this.mPaddingRight) - Gallery.this.mPaddingLeft) - 1), i);
                }
                Gallery.this.trackMotionScroll(iMax);
                if (zComputeScrollOffset && !Gallery.this.mShouldStopFling) {
                    this.mLastFlingX = currX;
                    Gallery.this.post(this);
                    return;
                } else {
                    endFling(true);
                    return;
                }
            }
            endFling(true);
        }
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }
    }
}
