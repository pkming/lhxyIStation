package android.widget;

import android.R;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.IntProperty;
import android.util.MathUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;

/* JADX INFO: loaded from: classes.dex */
class FastScroller {
    private static final int DURATION_CROSS_FADE = 50;
    private static final int DURATION_FADE_IN = 150;
    private static final int DURATION_FADE_OUT = 300;
    private static final int DURATION_RESIZE = 100;
    private static final long FADE_TIMEOUT = 1500;
    private static final int MIN_PAGES = 4;
    private static final int OVERLAY_AT_THUMB = 1;
    private static final int OVERLAY_FLOATING = 0;
    private static final int OVERLAY_POSITION = 5;
    private static final int PREVIEW_BACKGROUND_LEFT = 3;
    private static final int PREVIEW_BACKGROUND_RIGHT = 4;
    private static final int PREVIEW_LEFT = 0;
    private static final int PREVIEW_RIGHT = 1;
    private static final int STATE_DRAGGING = 2;
    private static final int STATE_NONE = 0;
    private static final int STATE_VISIBLE = 1;
    private static final int TEXT_COLOR = 0;
    private static final int THUMB_DRAWABLE = 1;
    private static final int TRACK_DRAWABLE = 2;
    private boolean mAlwaysShow;
    private AnimatorSet mDecorAnimation;
    private boolean mEnabled;
    private int mFirstVisibleItem;
    private boolean mHasPendingDrag;
    private final boolean mHasTrackImage;
    private int mHeaderCount;
    private float mInitialTouchY;
    private boolean mLayoutFromRight;
    private final AbsListView mList;
    private BaseAdapter mListAdapter;
    private boolean mLongList;
    private boolean mMatchDragPosition;
    private final ViewGroupOverlay mOverlay;
    private int mOverlayPosition;
    private AnimatorSet mPreviewAnimation;
    private final ImageView mPreviewImage;
    private final int mPreviewPadding;
    private final int[] mPreviewResId;
    private final TextView mPrimaryText;
    private int mScaledTouchSlop;
    private int mScrollBarStyle;
    private boolean mScrollCompleted;
    private final TextView mSecondaryText;
    private SectionIndexer mSectionIndexer;
    private Object[] mSections;
    private boolean mShowingPreview;
    private boolean mShowingPrimary;
    private int mState;
    private final ImageView mThumbImage;
    private final ImageView mTrackImage;
    private boolean mUpdatingLayout;
    private final int mWidth;
    private static final int[] ATTRS = {R.attr.fastScrollTextColor, R.attr.fastScrollThumbDrawable, R.attr.fastScrollTrackDrawable, R.attr.fastScrollPreviewBackgroundLeft, R.attr.fastScrollPreviewBackgroundRight, R.attr.fastScrollOverlayPosition};
    private static final long TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
    private static Property<View, Integer> LEFT = new IntProperty<View>("left") { // from class: android.widget.FastScroller.4
        @Override // android.util.IntProperty
        public void setValue(View view, int i) {
            view.setLeft(i);
        }

        @Override // android.util.Property
        public Integer get(View view) {
            return Integer.valueOf(view.getLeft());
        }
    };
    private static Property<View, Integer> TOP = new IntProperty<View>("top") { // from class: android.widget.FastScroller.5
        @Override // android.util.IntProperty
        public void setValue(View view, int i) {
            view.setTop(i);
        }

        @Override // android.util.Property
        public Integer get(View view) {
            return Integer.valueOf(view.getTop());
        }
    };
    private static Property<View, Integer> RIGHT = new IntProperty<View>("right") { // from class: android.widget.FastScroller.6
        @Override // android.util.IntProperty
        public void setValue(View view, int i) {
            view.setRight(i);
        }

        @Override // android.util.Property
        public Integer get(View view) {
            return Integer.valueOf(view.getRight());
        }
    };
    private static Property<View, Integer> BOTTOM = new IntProperty<View>("bottom") { // from class: android.widget.FastScroller.7
        @Override // android.util.IntProperty
        public void setValue(View view, int i) {
            view.setBottom(i);
        }

        @Override // android.util.Property
        public Integer get(View view) {
            return Integer.valueOf(view.getBottom());
        }
    };
    private final Rect mTempBounds = new Rect();
    private final Rect mTempMargins = new Rect();
    private final Rect mContainerRect = new Rect();
    private int mCurrentSection = -1;
    private int mScrollbarPosition = -1;
    private final Runnable mDeferStartDrag = new Runnable() { // from class: android.widget.FastScroller.1
        @Override // java.lang.Runnable
        public void run() {
            if (FastScroller.this.mList.isAttachedToWindow()) {
                FastScroller.this.beginDrag();
                FastScroller fastScroller = FastScroller.this;
                FastScroller.this.scrollTo(fastScroller.getPosFromMotionEvent(fastScroller.mInitialTouchY));
            }
            FastScroller.this.mHasPendingDrag = false;
        }
    };
    private final Runnable mDeferHide = new Runnable() { // from class: android.widget.FastScroller.2
        @Override // java.lang.Runnable
        public void run() {
            FastScroller.this.setState(0);
        }
    };
    private final Animator.AnimatorListener mSwitchPrimaryListener = new AnimatorListenerAdapter() { // from class: android.widget.FastScroller.3
        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            FastScroller.this.mShowingPrimary = !r2.mShowingPrimary;
        }
    };

    public FastScroller(AbsListView absListView) {
        int iMax;
        int[] iArr = new int[2];
        this.mPreviewResId = iArr;
        this.mList = absListView;
        ViewGroupOverlay overlay = absListView.getOverlay();
        this.mOverlay = overlay;
        Context context = absListView.getContext();
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Resources resources = context.getResources();
        TypedArray typedArrayObtainStyledAttributes = context.getTheme().obtainStyledAttributes(ATTRS);
        ImageView imageView = new ImageView(context);
        this.mTrackImage = imageView;
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(2);
        if (drawable != null) {
            this.mHasTrackImage = true;
            imageView.setBackground(drawable);
            overlay.add(imageView);
            iMax = Math.max(0, drawable.getIntrinsicWidth());
        } else {
            this.mHasTrackImage = false;
            iMax = 0;
        }
        ImageView imageView2 = new ImageView(context);
        this.mThumbImage = imageView2;
        Drawable drawable2 = typedArrayObtainStyledAttributes.getDrawable(1);
        if (drawable2 != null) {
            imageView2.setImageDrawable(drawable2);
            overlay.add(imageView2);
            iMax = Math.max(iMax, drawable2.getIntrinsicWidth());
        }
        if (drawable2.getIntrinsicWidth() <= 0 || drawable2.getIntrinsicHeight() <= 0) {
            int dimensionPixelSize = resources.getDimensionPixelSize(17104929);
            imageView2.setMinimumWidth(dimensionPixelSize);
            imageView2.setMinimumHeight(resources.getDimensionPixelSize(17104930));
            iMax = Math.max(iMax, dimensionPixelSize);
        }
        this.mWidth = iMax;
        int dimensionPixelSize2 = resources.getDimensionPixelSize(17104926);
        ImageView imageView3 = new ImageView(context);
        this.mPreviewImage = imageView3;
        imageView3.setMinimumWidth(dimensionPixelSize2);
        imageView3.setMinimumHeight(dimensionPixelSize2);
        imageView3.setAlpha(0.0f);
        overlay.add(imageView3);
        int dimensionPixelSize3 = resources.getDimensionPixelSize(17104928);
        this.mPreviewPadding = dimensionPixelSize3;
        int iMax2 = Math.max(0, dimensionPixelSize2 - dimensionPixelSize3);
        TextView textViewCreatePreviewTextView = createPreviewTextView(context, typedArrayObtainStyledAttributes);
        this.mPrimaryText = textViewCreatePreviewTextView;
        textViewCreatePreviewTextView.setMinimumWidth(iMax2);
        textViewCreatePreviewTextView.setMinimumHeight(iMax2);
        overlay.add(textViewCreatePreviewTextView);
        TextView textViewCreatePreviewTextView2 = createPreviewTextView(context, typedArrayObtainStyledAttributes);
        this.mSecondaryText = textViewCreatePreviewTextView2;
        textViewCreatePreviewTextView2.setMinimumWidth(iMax2);
        textViewCreatePreviewTextView2.setMinimumHeight(iMax2);
        overlay.add(textViewCreatePreviewTextView2);
        iArr[0] = typedArrayObtainStyledAttributes.getResourceId(3, 0);
        iArr[1] = typedArrayObtainStyledAttributes.getResourceId(4, 0);
        this.mOverlayPosition = typedArrayObtainStyledAttributes.getInt(5, 0);
        typedArrayObtainStyledAttributes.recycle();
        this.mScrollBarStyle = absListView.getScrollBarStyle();
        this.mScrollCompleted = true;
        this.mState = 1;
        this.mMatchDragPosition = context.getApplicationInfo().targetSdkVersion >= 11;
        getSectionsFromIndexer();
        refreshDrawablePressedState();
        updateLongList(absListView.getChildCount(), absListView.getCount());
        setScrollbarPosition(absListView.getVerticalScrollbarPosition());
        postAutoHide();
    }

    public void remove() {
        this.mOverlay.remove(this.mTrackImage);
        this.mOverlay.remove(this.mThumbImage);
        this.mOverlay.remove(this.mPreviewImage);
        this.mOverlay.remove(this.mPrimaryText);
        this.mOverlay.remove(this.mSecondaryText);
    }

    public void setEnabled(boolean z) {
        if (this.mEnabled != z) {
            this.mEnabled = z;
            onStateDependencyChanged();
        }
    }

    public boolean isEnabled() {
        return this.mEnabled && (this.mLongList || this.mAlwaysShow);
    }

    public void setAlwaysShow(boolean z) {
        if (this.mAlwaysShow != z) {
            this.mAlwaysShow = z;
            onStateDependencyChanged();
        }
    }

    public boolean isAlwaysShowEnabled() {
        return this.mAlwaysShow;
    }

    private void onStateDependencyChanged() {
        if (isEnabled()) {
            if (isAlwaysShowEnabled()) {
                setState(1);
            } else if (this.mState == 1) {
                postAutoHide();
            }
        } else {
            stop();
        }
        this.mList.resolvePadding();
    }

    public void setScrollBarStyle(int i) {
        if (this.mScrollBarStyle != i) {
            this.mScrollBarStyle = i;
            updateLayout();
        }
    }

    public void stop() {
        setState(0);
    }

    /* JADX WARN: Type inference failed for: r0v0 */
    /* JADX WARN: Type inference failed for: r0v1, types: [boolean] */
    /* JADX WARN: Type inference failed for: r0v5 */
    public void setScrollbarPosition(int i) {
        if (i == 0) {
            i = this.mList.isLayoutRtl() ? 1 : 2;
        }
        if (this.mScrollbarPosition != i) {
            this.mScrollbarPosition = i;
            ?? r0 = i == 1 ? 0 : 1;
            this.mLayoutFromRight = r0;
            this.mPreviewImage.setBackgroundResource(this.mPreviewResId[r0]);
            Drawable background = this.mPreviewImage.getBackground();
            if (background != null) {
                Rect rect = this.mTempBounds;
                background.getPadding(rect);
                int i2 = this.mPreviewPadding;
                rect.offset(i2, i2);
                this.mPreviewImage.setPadding(rect.left, rect.top, rect.right, rect.bottom);
            }
            updateLayout();
        }
    }

    public int getWidth() {
        return this.mWidth;
    }

    public void onSizeChanged(int i, int i2, int i3, int i4) {
        updateLayout();
    }

    public void onItemCountChanged(int i) {
        int childCount = this.mList.getChildCount();
        if ((i - childCount > 0) && this.mState != 2) {
            setThumbPos(getPosFromItemCount(this.mList.getFirstVisiblePosition(), childCount, i));
        }
        updateLongList(childCount, i);
    }

    private void updateLongList(int i, int i2) {
        boolean z = i > 0 && i2 / i >= 4;
        if (this.mLongList != z) {
            this.mLongList = z;
            onStateDependencyChanged();
        }
    }

    private TextView createPreviewTextView(Context context, TypedArray typedArray) {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-2, -2);
        Resources resources = context.getResources();
        resources.getDimensionPixelSize(17104926);
        ColorStateList colorStateList = typedArray.getColorStateList(0);
        float dimensionPixelSize = resources.getDimensionPixelSize(17104927);
        TextView textView = new TextView(context);
        textView.setLayoutParams(layoutParams);
        textView.setTextColor(colorStateList);
        textView.setTextSize(0, dimensionPixelSize);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        textView.setGravity(17);
        textView.setAlpha(0.0f);
        textView.setLayoutDirection(this.mList.getLayoutDirection());
        return textView;
    }

    public void updateLayout() {
        if (this.mUpdatingLayout) {
            return;
        }
        this.mUpdatingLayout = true;
        updateContainerRect();
        layoutThumb();
        layoutTrack();
        Rect rect = this.mTempBounds;
        measurePreview(this.mPrimaryText, rect);
        applyLayout(this.mPrimaryText, rect);
        measurePreview(this.mSecondaryText, rect);
        applyLayout(this.mSecondaryText, rect);
        if (this.mPreviewImage != null) {
            rect.left -= this.mPreviewImage.getPaddingLeft();
            rect.top -= this.mPreviewImage.getPaddingTop();
            rect.right += this.mPreviewImage.getPaddingRight();
            rect.bottom += this.mPreviewImage.getPaddingBottom();
            applyLayout(this.mPreviewImage, rect);
        }
        this.mUpdatingLayout = false;
    }

    private void applyLayout(View view, Rect rect) {
        view.layout(rect.left, rect.top, rect.right, rect.bottom);
        view.setPivotX(this.mLayoutFromRight ? rect.right - rect.left : 0.0f);
    }

    private void measurePreview(View view, Rect rect) {
        Rect rect2 = this.mTempMargins;
        rect2.left = this.mPreviewImage.getPaddingLeft();
        rect2.top = this.mPreviewImage.getPaddingTop();
        rect2.right = this.mPreviewImage.getPaddingRight();
        rect2.bottom = this.mPreviewImage.getPaddingBottom();
        if (this.mOverlayPosition == 1) {
            measureViewToSide(view, this.mThumbImage, rect2, rect);
        } else {
            measureFloating(view, rect2, rect);
        }
    }

    private void measureViewToSide(View view, View view2, Rect rect, Rect rect2) {
        int i;
        int i2;
        int i3;
        int right;
        int left;
        if (rect == null) {
            i3 = 0;
            i = 0;
            i2 = 0;
        } else {
            i = rect.left;
            i2 = rect.top;
            i3 = rect.right;
        }
        Rect rect3 = this.mContainerRect;
        int iWidth = rect3.width();
        if (view2 != null) {
            if (this.mLayoutFromRight) {
                iWidth = view2.getLeft();
            } else {
                iWidth -= view2.getRight();
            }
        }
        view.measure(View.MeasureSpec.makeMeasureSpec((iWidth - i) - i3, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
        int measuredWidth = view.getMeasuredWidth();
        if (this.mLayoutFromRight) {
            left = (view2 == null ? rect3.right : view2.getLeft()) - i3;
            right = left - measuredWidth;
        } else {
            right = (view2 == null ? rect3.left : view2.getRight()) + i;
            left = right + measuredWidth;
        }
        rect2.set(right, i2, left, view.getMeasuredHeight() + i2);
    }

    private void measureFloating(View view, Rect rect, Rect rect2) {
        int i;
        int i2;
        int i3;
        if (rect == null) {
            i3 = 0;
            i = 0;
            i2 = 0;
        } else {
            i = rect.left;
            i2 = rect.top;
            i3 = rect.right;
        }
        Rect rect3 = this.mContainerRect;
        int iWidth = rect3.width();
        view.measure(View.MeasureSpec.makeMeasureSpec((iWidth - i) - i3, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
        int iHeight = rect3.height();
        int measuredWidth = view.getMeasuredWidth();
        int i4 = (iHeight / 10) + i2 + rect3.top;
        int measuredHeight = view.getMeasuredHeight() + i4;
        int i5 = ((iWidth - measuredWidth) / 2) + rect3.left;
        rect2.set(i5, i4, measuredWidth + i5, measuredHeight);
    }

    private void updateContainerRect() {
        AbsListView absListView = this.mList;
        absListView.resolvePadding();
        Rect rect = this.mContainerRect;
        rect.left = 0;
        rect.top = 0;
        rect.right = absListView.getWidth();
        rect.bottom = absListView.getHeight();
        int i = this.mScrollBarStyle;
        if (i == 16777216 || i == 0) {
            rect.left += absListView.getPaddingLeft();
            rect.top += absListView.getPaddingTop();
            rect.right -= absListView.getPaddingRight();
            rect.bottom -= absListView.getPaddingBottom();
            if (i == 16777216) {
                int width = getWidth();
                if (this.mScrollbarPosition == 2) {
                    rect.right += width;
                } else {
                    rect.left -= width;
                }
            }
        }
    }

    private void layoutThumb() {
        Rect rect = this.mTempBounds;
        measureViewToSide(this.mThumbImage, null, null, rect);
        applyLayout(this.mThumbImage, rect);
    }

    private void layoutTrack() {
        ImageView imageView = this.mTrackImage;
        ImageView imageView2 = this.mThumbImage;
        Rect rect = this.mContainerRect;
        imageView.measure(View.MeasureSpec.makeMeasureSpec(rect.width(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
        int measuredWidth = imageView.getMeasuredWidth();
        int height = imageView2 != null ? imageView2.getHeight() / 2 : 0;
        int left = imageView2.getLeft() + ((imageView2.getWidth() - measuredWidth) / 2);
        imageView.layout(left, rect.top + height, measuredWidth + left, rect.bottom - height);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setState(int i) {
        this.mList.removeCallbacks(this.mDeferHide);
        if (this.mAlwaysShow && i == 0) {
            i = 1;
        }
        if (i == this.mState) {
            return;
        }
        if (i == 0) {
            transitionToHidden();
        } else if (i == 1) {
            transitionToVisible();
        } else if (i == 2) {
            if (transitionPreviewLayout(this.mCurrentSection)) {
                transitionToDragging();
            } else {
                transitionToVisible();
            }
        }
        this.mState = i;
        refreshDrawablePressedState();
    }

    private void refreshDrawablePressedState() {
        boolean z = this.mState == 2;
        this.mThumbImage.setPressed(z);
        this.mTrackImage.setPressed(z);
    }

    private void transitionToHidden() {
        AnimatorSet animatorSet = this.mDecorAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        Animator duration = groupAnimatorOfFloat(View.ALPHA, 0.0f, this.mThumbImage, this.mTrackImage, this.mPreviewImage, this.mPrimaryText, this.mSecondaryText).setDuration(300L);
        Animator duration2 = groupAnimatorOfFloat(View.TRANSLATION_X, this.mLayoutFromRight ? this.mThumbImage.getWidth() : -this.mThumbImage.getWidth(), this.mThumbImage, this.mTrackImage).setDuration(300L);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.mDecorAnimation = animatorSet2;
        animatorSet2.playTogether(duration, duration2);
        this.mDecorAnimation.start();
        this.mShowingPreview = false;
    }

    private void transitionToVisible() {
        AnimatorSet animatorSet = this.mDecorAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        Animator duration = groupAnimatorOfFloat(View.ALPHA, 1.0f, this.mThumbImage, this.mTrackImage).setDuration(150L);
        Animator duration2 = groupAnimatorOfFloat(View.ALPHA, 0.0f, this.mPreviewImage, this.mPrimaryText, this.mSecondaryText).setDuration(300L);
        Animator duration3 = groupAnimatorOfFloat(View.TRANSLATION_X, 0.0f, this.mThumbImage, this.mTrackImage).setDuration(150L);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.mDecorAnimation = animatorSet2;
        animatorSet2.playTogether(duration, duration2, duration3);
        this.mDecorAnimation.start();
        this.mShowingPreview = false;
    }

    private void transitionToDragging() {
        AnimatorSet animatorSet = this.mDecorAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        Animator duration = groupAnimatorOfFloat(View.ALPHA, 1.0f, this.mThumbImage, this.mTrackImage, this.mPreviewImage).setDuration(150L);
        Animator duration2 = groupAnimatorOfFloat(View.TRANSLATION_X, 0.0f, this.mThumbImage, this.mTrackImage).setDuration(150L);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.mDecorAnimation = animatorSet2;
        animatorSet2.playTogether(duration, duration2);
        this.mDecorAnimation.start();
        this.mShowingPreview = true;
    }

    private void postAutoHide() {
        this.mList.removeCallbacks(this.mDeferHide);
        this.mList.postDelayed(this.mDeferHide, FADE_TIMEOUT);
    }

    public void onScroll(int i, int i2, int i3) {
        if (!isEnabled()) {
            setState(0);
            return;
        }
        if ((i3 - i2 > 0) && this.mState != 2) {
            setThumbPos(getPosFromItemCount(i, i2, i3));
        }
        this.mScrollCompleted = true;
        if (this.mFirstVisibleItem != i) {
            this.mFirstVisibleItem = i;
            if (this.mState != 2) {
                setState(1);
                postAutoHide();
            }
        }
    }

    private void getSectionsFromIndexer() {
        this.mSectionIndexer = null;
        ListAdapter adapter = this.mList.getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) adapter;
            this.mHeaderCount = headerViewListAdapter.getHeadersCount();
            adapter = headerViewListAdapter.getWrappedAdapter();
        }
        if (adapter instanceof ExpandableListConnector) {
            ExpandableListAdapter adapter2 = ((ExpandableListConnector) adapter).getAdapter();
            if (adapter2 instanceof SectionIndexer) {
                SectionIndexer sectionIndexer = (SectionIndexer) adapter2;
                this.mSectionIndexer = sectionIndexer;
                this.mListAdapter = (BaseAdapter) adapter;
                this.mSections = sectionIndexer.getSections();
                return;
            }
            return;
        }
        if (adapter instanceof SectionIndexer) {
            this.mListAdapter = (BaseAdapter) adapter;
            SectionIndexer sectionIndexer2 = (SectionIndexer) adapter;
            this.mSectionIndexer = sectionIndexer2;
            this.mSections = sectionIndexer2.getSections();
            return;
        }
        this.mListAdapter = (BaseAdapter) adapter;
        this.mSections = null;
    }

    public void onSectionsChanged() {
        this.mListAdapter = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0062  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x0066  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0073  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0086  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0097  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void scrollTo(float r14) {
        /*
            Method dump skipped, instruction units count: 250
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.FastScroller.scrollTo(float):void");
    }

    private boolean transitionPreviewLayout(int i) {
        TextView textView;
        TextView textView2;
        Object obj;
        Object[] objArr = this.mSections;
        String string = (objArr == null || i < 0 || i >= objArr.length || (obj = objArr[i]) == null) ? null : obj.toString();
        Rect rect = this.mTempBounds;
        ImageView imageView = this.mPreviewImage;
        if (this.mShowingPrimary) {
            textView = this.mPrimaryText;
            textView2 = this.mSecondaryText;
        } else {
            textView = this.mSecondaryText;
            textView2 = this.mPrimaryText;
        }
        textView2.setText(string);
        measurePreview(textView2, rect);
        applyLayout(textView2, rect);
        AnimatorSet animatorSet = this.mPreviewAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        Animator duration = animateAlpha(textView2, 1.0f).setDuration(50L);
        Animator duration2 = animateAlpha(textView, 0.0f).setDuration(50L);
        duration2.addListener(this.mSwitchPrimaryListener);
        rect.left -= this.mPreviewImage.getPaddingLeft();
        rect.top -= this.mPreviewImage.getPaddingTop();
        rect.right += this.mPreviewImage.getPaddingRight();
        rect.bottom += this.mPreviewImage.getPaddingBottom();
        Animator animatorAnimateBounds = animateBounds(imageView, rect);
        animatorAnimateBounds.setDuration(100L);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.mPreviewAnimation = animatorSet2;
        AnimatorSet.Builder builderWith = animatorSet2.play(duration2).with(duration);
        builderWith.with(animatorAnimateBounds);
        int width = (imageView.getWidth() - imageView.getPaddingLeft()) - imageView.getPaddingRight();
        int width2 = textView2.getWidth();
        if (width2 > width) {
            textView2.setScaleX(width / width2);
            builderWith.with(animateScaleX(textView2, 1.0f).setDuration(100L));
        } else {
            textView2.setScaleX(1.0f);
        }
        int width3 = textView.getWidth();
        if (width3 > width2) {
            builderWith.with(animateScaleX(textView, width2 / width3).setDuration(100L));
        }
        this.mPreviewAnimation.start();
        return !TextUtils.isEmpty(string);
    }

    private void setThumbPos(float f) {
        Rect rect = this.mContainerRect;
        int i = rect.top;
        int i2 = rect.bottom;
        ImageView imageView = this.mTrackImage;
        ImageView imageView2 = this.mThumbImage;
        float top = imageView.getTop();
        float bottom = (f * (imageView.getBottom() - top)) + top;
        imageView2.setTranslationY(bottom - (imageView2.getHeight() / 2));
        if (this.mOverlayPosition != 1) {
            bottom = 0.0f;
        }
        ImageView imageView3 = this.mPreviewImage;
        float height = imageView3.getHeight() / 2.0f;
        float fConstrain = MathUtils.constrain(bottom, i + height, i2 - height) - height;
        imageView3.setTranslationY(fConstrain);
        this.mPrimaryText.setTranslationY(fConstrain);
        this.mSecondaryText.setTranslationY(fConstrain);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getPosFromMotionEvent(float f) {
        Rect rect = this.mContainerRect;
        int i = rect.top;
        int i2 = rect.bottom;
        float top = this.mTrackImage.getTop();
        float bottom = r0.getBottom() - top;
        if (bottom <= 0.0f) {
            return 0.0f;
        }
        return MathUtils.constrain((f - top) / bottom, 0.0f, 1.0f);
    }

    private float getPosFromItemCount(int i, int i2, int i3) {
        int positionForSection;
        Object[] objArr;
        if (this.mSectionIndexer == null || this.mListAdapter == null) {
            getSectionsFromIndexer();
        }
        if (!((this.mSectionIndexer == null || (objArr = this.mSections) == null || objArr.length <= 0) ? false : true) || !this.mMatchDragPosition) {
            return i / (i3 - i2);
        }
        int i4 = this.mHeaderCount;
        int i5 = i - i4;
        if (i5 < 0) {
            return 0.0f;
        }
        int i6 = i3 - i4;
        View childAt = this.mList.getChildAt(0);
        float paddingTop = (childAt == null || childAt.getHeight() == 0) ? 0.0f : (this.mList.getPaddingTop() - childAt.getTop()) / childAt.getHeight();
        int sectionForPosition = this.mSectionIndexer.getSectionForPosition(i5);
        int positionForSection2 = this.mSectionIndexer.getPositionForSection(sectionForPosition);
        int length = this.mSections.length;
        if (sectionForPosition < length - 1) {
            int i7 = sectionForPosition + 1;
            positionForSection = (i7 < length ? this.mSectionIndexer.getPositionForSection(i7) : i6 - 1) - positionForSection2;
        } else {
            positionForSection = i6 - positionForSection2;
        }
        float f = (sectionForPosition + (positionForSection != 0 ? ((i5 + paddingTop) - positionForSection2) / positionForSection : 0.0f)) / length;
        if (i5 <= 0 || i5 + i2 != i6) {
            return f;
        }
        View childAt2 = this.mList.getChildAt(i2 - 1);
        return f + ((1.0f - f) * (((this.mList.getHeight() - this.mList.getPaddingBottom()) - childAt2.getTop()) / childAt2.getHeight()));
    }

    private void cancelFling() {
        MotionEvent motionEventObtain = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
        this.mList.onTouchEvent(motionEventObtain);
        motionEventObtain.recycle();
    }

    private void cancelPendingDrag() {
        this.mList.removeCallbacks(this.mDeferStartDrag);
        this.mHasPendingDrag = false;
    }

    private void startPendingDrag() {
        this.mHasPendingDrag = true;
        this.mList.postDelayed(this.mDeferStartDrag, TAP_TIMEOUT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void beginDrag() {
        setState(2);
        if (this.mListAdapter == null && this.mList != null) {
            getSectionsFromIndexer();
        }
        AbsListView absListView = this.mList;
        if (absListView != null) {
            absListView.requestDisallowInterceptTouchEvent(true);
            this.mList.reportScrollStateChange(1);
        }
        cancelFling();
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x002a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r5) {
        /*
            r4 = this;
            boolean r0 = r4.isEnabled()
            r1 = 0
            if (r0 != 0) goto L8
            return r1
        L8:
            int r0 = r5.getActionMasked()
            r2 = 1
            if (r0 == 0) goto L2e
            if (r0 == r2) goto L2a
            r2 = 2
            if (r0 == r2) goto L18
            r5 = 3
            if (r0 == r5) goto L2a
            goto L51
        L18:
            float r0 = r5.getX()
            float r5 = r5.getY()
            boolean r5 = r4.isPointInside(r0, r5)
            if (r5 != 0) goto L51
            r4.cancelPendingDrag()
            goto L51
        L2a:
            r4.cancelPendingDrag()
            goto L51
        L2e:
            float r0 = r5.getX()
            float r3 = r5.getY()
            boolean r0 = r4.isPointInside(r0, r3)
            if (r0 == 0) goto L51
            android.widget.AbsListView r0 = r4.mList
            boolean r0 = r0.isInScrollingContainer()
            if (r0 != 0) goto L48
            r4.beginDrag()
            return r2
        L48:
            float r5 = r5.getY()
            r4.mInitialTouchY = r5
            r4.startPendingDrag()
        L51:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.FastScroller.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    public boolean onInterceptHoverEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        int actionMasked = motionEvent.getActionMasked();
        if ((actionMasked == 9 || actionMasked == 7) && this.mState == 0 && isPointInside(motionEvent.getX(), motionEvent.getY())) {
            setState(1);
            postAutoHide();
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 1) {
            if (this.mHasPendingDrag) {
                beginDrag();
                float posFromMotionEvent = getPosFromMotionEvent(motionEvent.getY());
                setThumbPos(posFromMotionEvent);
                scrollTo(posFromMotionEvent);
                cancelPendingDrag();
            }
            if (this.mState == 2) {
                AbsListView absListView = this.mList;
                if (absListView != null) {
                    absListView.requestDisallowInterceptTouchEvent(false);
                    this.mList.reportScrollStateChange(0);
                }
                setState(1);
                postAutoHide();
                return true;
            }
        } else if (actionMasked == 2) {
            if (this.mHasPendingDrag && Math.abs(motionEvent.getY() - this.mInitialTouchY) > this.mScaledTouchSlop) {
                setState(2);
                if (this.mListAdapter == null && this.mList != null) {
                    getSectionsFromIndexer();
                }
                AbsListView absListView2 = this.mList;
                if (absListView2 != null) {
                    absListView2.requestDisallowInterceptTouchEvent(true);
                    this.mList.reportScrollStateChange(1);
                }
                cancelFling();
                cancelPendingDrag();
            }
            if (this.mState == 2) {
                float posFromMotionEvent2 = getPosFromMotionEvent(motionEvent.getY());
                setThumbPos(posFromMotionEvent2);
                if (this.mScrollCompleted) {
                    scrollTo(posFromMotionEvent2);
                }
                return true;
            }
        } else if (actionMasked == 3) {
            cancelPendingDrag();
        }
        return false;
    }

    private boolean isPointInside(float f, float f2) {
        return isPointInsideX(f) && (this.mHasTrackImage || isPointInsideY(f2));
    }

    private boolean isPointInsideX(float f) {
        return this.mLayoutFromRight ? f >= ((float) this.mThumbImage.getLeft()) : f <= ((float) this.mThumbImage.getRight());
    }

    private boolean isPointInsideY(float f) {
        float translationY = this.mThumbImage.getTranslationY();
        return f >= ((float) this.mThumbImage.getTop()) + translationY && f <= ((float) this.mThumbImage.getBottom()) + translationY;
    }

    private static Animator groupAnimatorOfFloat(Property<View, Float> property, float f, View... viewArr) {
        AnimatorSet animatorSet = new AnimatorSet();
        AnimatorSet.Builder builderPlay = null;
        for (int length = viewArr.length - 1; length >= 0; length--) {
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(viewArr[length], property, f);
            if (builderPlay == null) {
                builderPlay = animatorSet.play(objectAnimatorOfFloat);
            } else {
                builderPlay.with(objectAnimatorOfFloat);
            }
        }
        return animatorSet;
    }

    private static Animator animateScaleX(View view, float f) {
        return ObjectAnimator.ofFloat(view, View.SCALE_X, f);
    }

    private static Animator animateAlpha(View view, float f) {
        return ObjectAnimator.ofFloat(view, View.ALPHA, f);
    }

    private static Animator animateBounds(View view, Rect rect) {
        return ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofInt(LEFT, rect.left), PropertyValuesHolder.ofInt(TOP, rect.top), PropertyValuesHolder.ofInt(RIGHT, rect.right), PropertyValuesHolder.ofInt(BOTTOM, rect.bottom));
    }
}
