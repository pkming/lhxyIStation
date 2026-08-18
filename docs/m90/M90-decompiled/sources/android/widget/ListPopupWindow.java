package android.widget;

import android.R;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.IntProperty;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.android.internal.widget.AutoScrollHelper;

/* JADX INFO: loaded from: classes.dex */
public class ListPopupWindow {
    private static final boolean DEBUG = false;
    private static final int EXPAND_LIST_TIMEOUT = 250;
    public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;
    public static final int INPUT_METHOD_NEEDED = 1;
    public static final int INPUT_METHOD_NOT_NEEDED = 2;
    public static final int MATCH_PARENT = -1;
    public static final int POSITION_PROMPT_ABOVE = 0;
    public static final int POSITION_PROMPT_BELOW = 1;
    private static final String TAG = "ListPopupWindow";
    public static final int WRAP_CONTENT = -2;
    private ListAdapter mAdapter;
    private Context mContext;
    private boolean mDropDownAlwaysVisible;
    private View mDropDownAnchorView;
    private int mDropDownGravity;
    private int mDropDownHeight;
    private int mDropDownHorizontalOffset;
    private DropDownListView mDropDownList;
    private Drawable mDropDownListHighlight;
    private int mDropDownVerticalOffset;
    private boolean mDropDownVerticalOffsetSet;
    private int mDropDownWidth;
    private boolean mForceIgnoreOutsideTouch;
    private Handler mHandler;
    private final ListSelectorHider mHideSelector;
    private AdapterView.OnItemClickListener mItemClickListener;
    private AdapterView.OnItemSelectedListener mItemSelectedListener;
    private int mLayoutDirection;
    int mListItemExpandMaximum;
    private boolean mModal;
    private DataSetObserver mObserver;
    private PopupWindow mPopup;
    private int mPromptPosition;
    private View mPromptView;
    private final ResizePopupRunnable mResizePopupRunnable;
    private final PopupScrollListener mScrollListener;
    private Runnable mShowDropDownRunnable;
    private Rect mTempRect;
    private final PopupTouchInterceptor mTouchInterceptor;

    public ListPopupWindow(Context context) {
        this(context, null, R.attr.listPopupWindowStyle, 0);
    }

    public ListPopupWindow(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.listPopupWindowStyle, 0);
    }

    public ListPopupWindow(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public ListPopupWindow(Context context, AttributeSet attributeSet, int i, int i2) {
        this.mDropDownHeight = -2;
        this.mDropDownWidth = -2;
        this.mDropDownGravity = 0;
        this.mDropDownAlwaysVisible = false;
        this.mForceIgnoreOutsideTouch = false;
        this.mListItemExpandMaximum = Integer.MAX_VALUE;
        this.mPromptPosition = 0;
        this.mResizePopupRunnable = new ResizePopupRunnable();
        this.mTouchInterceptor = new PopupTouchInterceptor();
        this.mScrollListener = new PopupScrollListener();
        this.mHideSelector = new ListSelectorHider();
        this.mHandler = new Handler();
        this.mTempRect = new Rect();
        this.mContext = context;
        PopupWindow popupWindow = new PopupWindow(context, attributeSet, i, i2);
        this.mPopup = popupWindow;
        popupWindow.setInputMethodMode(1);
        this.mLayoutDirection = TextUtils.getLayoutDirectionFromLocale(this.mContext.getResources().getConfiguration().locale);
    }

    public void setAdapter(ListAdapter listAdapter) {
        DataSetObserver dataSetObserver = this.mObserver;
        if (dataSetObserver == null) {
            this.mObserver = new PopupDataSetObserver();
        } else {
            ListAdapter listAdapter2 = this.mAdapter;
            if (listAdapter2 != null) {
                listAdapter2.unregisterDataSetObserver(dataSetObserver);
            }
        }
        this.mAdapter = listAdapter;
        if (listAdapter != null) {
            listAdapter.registerDataSetObserver(this.mObserver);
        }
        DropDownListView dropDownListView = this.mDropDownList;
        if (dropDownListView != null) {
            dropDownListView.setAdapter(this.mAdapter);
        }
    }

    public void setPromptPosition(int i) {
        this.mPromptPosition = i;
    }

    public int getPromptPosition() {
        return this.mPromptPosition;
    }

    public void setModal(boolean z) {
        this.mModal = true;
        this.mPopup.setFocusable(z);
    }

    public boolean isModal() {
        return this.mModal;
    }

    public void setForceIgnoreOutsideTouch(boolean z) {
        this.mForceIgnoreOutsideTouch = z;
    }

    public void setDropDownAlwaysVisible(boolean z) {
        this.mDropDownAlwaysVisible = z;
    }

    public boolean isDropDownAlwaysVisible() {
        return this.mDropDownAlwaysVisible;
    }

    public void setSoftInputMode(int i) {
        this.mPopup.setSoftInputMode(i);
    }

    public int getSoftInputMode() {
        return this.mPopup.getSoftInputMode();
    }

    public void setListSelector(Drawable drawable) {
        this.mDropDownListHighlight = drawable;
    }

    public Drawable getBackground() {
        return this.mPopup.getBackground();
    }

    public void setBackgroundDrawable(Drawable drawable) {
        this.mPopup.setBackgroundDrawable(drawable);
    }

    public void setAnimationStyle(int i) {
        this.mPopup.setAnimationStyle(i);
    }

    public int getAnimationStyle() {
        return this.mPopup.getAnimationStyle();
    }

    public View getAnchorView() {
        return this.mDropDownAnchorView;
    }

    public void setAnchorView(View view) {
        this.mDropDownAnchorView = view;
    }

    public int getHorizontalOffset() {
        return this.mDropDownHorizontalOffset;
    }

    public void setHorizontalOffset(int i) {
        this.mDropDownHorizontalOffset = i;
    }

    public int getVerticalOffset() {
        if (this.mDropDownVerticalOffsetSet) {
            return this.mDropDownVerticalOffset;
        }
        return 0;
    }

    public void setVerticalOffset(int i) {
        this.mDropDownVerticalOffset = i;
        this.mDropDownVerticalOffsetSet = true;
    }

    public void setDropDownGravity(int i) {
        this.mDropDownGravity = i;
    }

    public int getWidth() {
        return this.mDropDownWidth;
    }

    public void setWidth(int i) {
        this.mDropDownWidth = i;
    }

    public void setContentWidth(int i) {
        Drawable background = this.mPopup.getBackground();
        if (background != null) {
            background.getPadding(this.mTempRect);
            this.mDropDownWidth = this.mTempRect.left + this.mTempRect.right + i;
        } else {
            setWidth(i);
        }
    }

    public int getHeight() {
        return this.mDropDownHeight;
    }

    public void setHeight(int i) {
        this.mDropDownHeight = i;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.mItemSelectedListener = onItemSelectedListener;
    }

    public void setPromptView(View view) {
        boolean zIsShowing = isShowing();
        if (zIsShowing) {
            removePromptView();
        }
        this.mPromptView = view;
        if (zIsShowing) {
            show();
        }
    }

    public void postShow() {
        this.mHandler.post(this.mShowDropDownRunnable);
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x005f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void show() {
        /*
            Method dump skipped, instruction units count: 241
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.ListPopupWindow.show():void");
    }

    public void dismiss() {
        this.mPopup.dismiss();
        removePromptView();
        this.mPopup.setContentView(null);
        this.mDropDownList = null;
        this.mHandler.removeCallbacks(this.mResizePopupRunnable);
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        this.mPopup.setOnDismissListener(onDismissListener);
    }

    private void removePromptView() {
        View view = this.mPromptView;
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(this.mPromptView);
            }
        }
    }

    public void setInputMethodMode(int i) {
        this.mPopup.setInputMethodMode(i);
    }

    public int getInputMethodMode() {
        return this.mPopup.getInputMethodMode();
    }

    public void setSelection(int i) {
        DropDownListView dropDownListView = this.mDropDownList;
        if (!isShowing() || dropDownListView == null) {
            return;
        }
        dropDownListView.mListSelectionHidden = false;
        dropDownListView.setSelection(i);
        if (dropDownListView.getChoiceMode() != 0) {
            dropDownListView.setItemChecked(i, true);
        }
    }

    public void clearListSelection() {
        DropDownListView dropDownListView = this.mDropDownList;
        if (dropDownListView != null) {
            dropDownListView.mListSelectionHidden = true;
            dropDownListView.hideSelector();
            dropDownListView.requestLayout();
        }
    }

    public boolean isShowing() {
        return this.mPopup.isShowing();
    }

    public boolean isInputMethodNotNeeded() {
        return this.mPopup.getInputMethodMode() == 2;
    }

    public boolean performItemClick(int i) {
        if (!isShowing()) {
            return false;
        }
        if (this.mItemClickListener == null) {
            return true;
        }
        DropDownListView dropDownListView = this.mDropDownList;
        this.mItemClickListener.onItemClick(dropDownListView, dropDownListView.getChildAt(i - dropDownListView.getFirstVisiblePosition()), i, dropDownListView.getAdapter().getItemId(i));
        return true;
    }

    public Object getSelectedItem() {
        if (isShowing()) {
            return this.mDropDownList.getSelectedItem();
        }
        return null;
    }

    public int getSelectedItemPosition() {
        if (isShowing()) {
            return this.mDropDownList.getSelectedItemPosition();
        }
        return -1;
    }

    public long getSelectedItemId() {
        if (isShowing()) {
            return this.mDropDownList.getSelectedItemId();
        }
        return Long.MIN_VALUE;
    }

    public View getSelectedView() {
        if (isShowing()) {
            return this.mDropDownList.getSelectedView();
        }
        return null;
    }

    public ListView getListView() {
        return this.mDropDownList;
    }

    void setListItemExpandMax(int i) {
        this.mListItemExpandMaximum = i;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (isShowing() && i != 62 && (this.mDropDownList.getSelectedItemPosition() >= 0 || !KeyEvent.isConfirmKey(i))) {
            int selectedItemPosition = this.mDropDownList.getSelectedItemPosition();
            boolean z = !this.mPopup.isAboveAnchor();
            ListAdapter listAdapter = this.mAdapter;
            int i2 = Integer.MAX_VALUE;
            int i3 = Integer.MIN_VALUE;
            if (listAdapter != null) {
                boolean zAreAllItemsEnabled = listAdapter.areAllItemsEnabled();
                int iLookForSelectablePosition = zAreAllItemsEnabled ? 0 : this.mDropDownList.lookForSelectablePosition(0, true);
                int count = zAreAllItemsEnabled ? listAdapter.getCount() - 1 : this.mDropDownList.lookForSelectablePosition(listAdapter.getCount() - 1, false);
                i2 = iLookForSelectablePosition;
                i3 = count;
            }
            if ((z && i == 19 && selectedItemPosition <= i2) || (!z && i == 20 && selectedItemPosition >= i3)) {
                clearListSelection();
                this.mPopup.setInputMethodMode(1);
                show();
                return true;
            }
            this.mDropDownList.mListSelectionHidden = false;
            if (this.mDropDownList.onKeyDown(i, keyEvent)) {
                this.mPopup.setInputMethodMode(2);
                this.mDropDownList.requestFocusFromTouch();
                show();
                if (i == 19 || i == 20 || i == 23 || i == 66) {
                    return true;
                }
            } else if (z && i == 20) {
                if (selectedItemPosition == i3) {
                    return true;
                }
            } else if (!z && i == 19 && selectedItemPosition == i2) {
                return true;
            }
        }
        return false;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (!isShowing() || this.mDropDownList.getSelectedItemPosition() < 0) {
            return false;
        }
        boolean zOnKeyUp = this.mDropDownList.onKeyUp(i, keyEvent);
        if (zOnKeyUp && KeyEvent.isConfirmKey(i)) {
            dismiss();
        }
        return zOnKeyUp;
    }

    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        if (i != 4 || !isShowing()) {
            return false;
        }
        View view = this.mDropDownAnchorView;
        if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
            KeyEvent.DispatcherState keyDispatcherState = view.getKeyDispatcherState();
            if (keyDispatcherState != null) {
                keyDispatcherState.startTracking(keyEvent, this);
            }
            return true;
        }
        if (keyEvent.getAction() != 1) {
            return false;
        }
        KeyEvent.DispatcherState keyDispatcherState2 = view.getKeyDispatcherState();
        if (keyDispatcherState2 != null) {
            keyDispatcherState2.handleUpEvent(keyEvent);
        }
        if (!keyEvent.isTracking() || keyEvent.isCanceled()) {
            return false;
        }
        dismiss();
        return true;
    }

    public View.OnTouchListener createDragToOpenListener(View view) {
        return new ForwardingListener(view) { // from class: android.widget.ListPopupWindow.1
            @Override // android.widget.ListPopupWindow.ForwardingListener
            public ListPopupWindow getPopup() {
                return ListPopupWindow.this;
            }
        };
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    private int buildDropDown() {
        int measuredHeight;
        int i;
        int iMakeMeasureSpec;
        View view;
        if (this.mDropDownList == null) {
            Context context = this.mContext;
            this.mShowDropDownRunnable = new Runnable() { // from class: android.widget.ListPopupWindow.2
                @Override // java.lang.Runnable
                public void run() {
                    View anchorView = ListPopupWindow.this.getAnchorView();
                    if (anchorView == null || anchorView.getWindowToken() == null) {
                        return;
                    }
                    ListPopupWindow.this.show();
                }
            };
            DropDownListView dropDownListView = new DropDownListView(context, !this.mModal);
            this.mDropDownList = dropDownListView;
            Drawable drawable = this.mDropDownListHighlight;
            if (drawable != null) {
                dropDownListView.setSelector(drawable);
            }
            this.mDropDownList.setAdapter(this.mAdapter);
            this.mDropDownList.setOnItemClickListener(this.mItemClickListener);
            this.mDropDownList.setFocusable(true);
            this.mDropDownList.setFocusableInTouchMode(true);
            this.mDropDownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: android.widget.ListPopupWindow.3
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> adapterView, View view2, int i2, long j) {
                    DropDownListView dropDownListView2;
                    if (i2 == -1 || (dropDownListView2 = ListPopupWindow.this.mDropDownList) == null) {
                        return;
                    }
                    dropDownListView2.mListSelectionHidden = false;
                }
            });
            this.mDropDownList.setOnScrollListener(this.mScrollListener);
            AdapterView.OnItemSelectedListener onItemSelectedListener = this.mItemSelectedListener;
            if (onItemSelectedListener != null) {
                this.mDropDownList.setOnItemSelectedListener(onItemSelectedListener);
            }
            DropDownListView dropDownListView2 = this.mDropDownList;
            View view2 = this.mPromptView;
            if (view2 != null) {
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(1);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, 0, 1.0f);
                int i2 = this.mPromptPosition;
                if (i2 == 0) {
                    linearLayout.addView(view2);
                    linearLayout.addView(dropDownListView2, layoutParams);
                } else if (i2 == 1) {
                    linearLayout.addView(dropDownListView2, layoutParams);
                    linearLayout.addView(view2);
                } else {
                    Log.e(TAG, "Invalid hint position " + this.mPromptPosition);
                }
                view2.measure(View.MeasureSpec.makeMeasureSpec(this.mDropDownWidth, Integer.MIN_VALUE), 0);
                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) view2.getLayoutParams();
                measuredHeight = view2.getMeasuredHeight() + layoutParams2.topMargin + layoutParams2.bottomMargin;
                view = linearLayout;
            } else {
                measuredHeight = 0;
                view = dropDownListView2;
            }
            this.mPopup.setContentView(view);
        } else {
            View view3 = this.mPromptView;
            if (view3 != null) {
                LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) view3.getLayoutParams();
                measuredHeight = view3.getMeasuredHeight() + layoutParams3.topMargin + layoutParams3.bottomMargin;
            } else {
                measuredHeight = 0;
            }
        }
        Drawable background = this.mPopup.getBackground();
        if (background != null) {
            background.getPadding(this.mTempRect);
            i = this.mTempRect.top + this.mTempRect.bottom;
            if (!this.mDropDownVerticalOffsetSet) {
                this.mDropDownVerticalOffset = -this.mTempRect.top;
            }
        } else {
            this.mTempRect.setEmpty();
            i = 0;
        }
        int maxAvailableHeight = this.mPopup.getMaxAvailableHeight(getAnchorView(), this.mDropDownVerticalOffset, this.mPopup.getInputMethodMode() == 2);
        if (this.mDropDownAlwaysVisible || this.mDropDownHeight == -1) {
            return maxAvailableHeight + i;
        }
        int i3 = this.mDropDownWidth;
        if (i3 == -2) {
            iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), Integer.MIN_VALUE);
        } else if (i3 == -1) {
            iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), 1073741824);
        } else {
            iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i3, 1073741824);
        }
        int iMeasureHeightOfChildren = this.mDropDownList.measureHeightOfChildren(iMakeMeasureSpec, 0, -1, maxAvailableHeight - measuredHeight, -1);
        if (iMeasureHeightOfChildren > 0) {
            measuredHeight += i;
        }
        return iMeasureHeightOfChildren + measuredHeight;
    }

    public static abstract class ForwardingListener implements View.OnTouchListener, View.OnAttachStateChangeListener {
        private int mActivePointerId;
        private Runnable mDisallowIntercept;
        private boolean mForwarding;
        private final float mScaledTouchSlop;
        private final View mSrc;
        private final int mTapTimeout = ViewConfiguration.getTapTimeout();

        public abstract ListPopupWindow getPopup();

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View view) {
        }

        public ForwardingListener(View view) {
            this.mSrc = view;
            this.mScaledTouchSlop = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
            view.addOnAttachStateChangeListener(this);
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean z = this.mForwarding;
            boolean z2 = !z ? !(onTouchObserved(motionEvent) && onForwardingStarted()) : !onTouchForwarded(motionEvent) && onForwardingStopped();
            this.mForwarding = z2;
            return z2 || z;
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View view) {
            this.mForwarding = false;
            this.mActivePointerId = -1;
            Runnable runnable = this.mDisallowIntercept;
            if (runnable != null) {
                this.mSrc.removeCallbacks(runnable);
            }
        }

        protected boolean onForwardingStarted() {
            ListPopupWindow popup = getPopup();
            if (popup == null || popup.isShowing()) {
                return true;
            }
            popup.show();
            return true;
        }

        protected boolean onForwardingStopped() {
            ListPopupWindow popup = getPopup();
            if (popup == null || !popup.isShowing()) {
                return true;
            }
            popup.dismiss();
            return true;
        }

        /* JADX WARN: Removed duplicated region for block: B:23:0x0041  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private boolean onTouchObserved(android.view.MotionEvent r6) {
            /*
                r5 = this;
                android.view.View r0 = r5.mSrc
                boolean r1 = r0.isEnabled()
                r2 = 0
                if (r1 != 0) goto La
                return r2
            La:
                int r1 = r6.getActionMasked()
                if (r1 == 0) goto L49
                r3 = 1
                if (r1 == r3) goto L41
                r4 = 2
                if (r1 == r4) goto L1a
                r6 = 3
                if (r1 == r6) goto L41
                goto L63
            L1a:
                int r1 = r5.mActivePointerId
                int r1 = r6.findPointerIndex(r1)
                if (r1 < 0) goto L63
                float r4 = r6.getX(r1)
                float r6 = r6.getY(r1)
                float r1 = r5.mScaledTouchSlop
                boolean r6 = r0.pointInView(r4, r6, r1)
                if (r6 != 0) goto L63
                java.lang.Runnable r6 = r5.mDisallowIntercept
                if (r6 == 0) goto L39
                r0.removeCallbacks(r6)
            L39:
                android.view.ViewParent r6 = r0.getParent()
                r6.requestDisallowInterceptTouchEvent(r3)
                return r3
            L41:
                java.lang.Runnable r6 = r5.mDisallowIntercept
                if (r6 == 0) goto L63
                r0.removeCallbacks(r6)
                goto L63
            L49:
                int r6 = r6.getPointerId(r2)
                r5.mActivePointerId = r6
                java.lang.Runnable r6 = r5.mDisallowIntercept
                if (r6 != 0) goto L5b
                android.widget.ListPopupWindow$ForwardingListener$DisallowIntercept r6 = new android.widget.ListPopupWindow$ForwardingListener$DisallowIntercept
                r1 = 0
                r6.<init>()
                r5.mDisallowIntercept = r6
            L5b:
                java.lang.Runnable r6 = r5.mDisallowIntercept
                int r1 = r5.mTapTimeout
                long r3 = (long) r1
                r0.postDelayed(r6, r3)
            L63:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.ListPopupWindow.ForwardingListener.onTouchObserved(android.view.MotionEvent):boolean");
        }

        private boolean onTouchForwarded(MotionEvent motionEvent) {
            DropDownListView dropDownListView;
            View view = this.mSrc;
            ListPopupWindow popup = getPopup();
            if (popup == null || !popup.isShowing() || (dropDownListView = popup.mDropDownList) == null || !dropDownListView.isShown()) {
                return false;
            }
            MotionEvent motionEventObtainNoHistory = MotionEvent.obtainNoHistory(motionEvent);
            view.toGlobalMotionEvent(motionEventObtainNoHistory);
            dropDownListView.toLocalMotionEvent(motionEventObtainNoHistory);
            boolean zOnForwardedEvent = dropDownListView.onForwardedEvent(motionEventObtainNoHistory, this.mActivePointerId);
            motionEventObtainNoHistory.recycle();
            return zOnForwardedEvent;
        }

        private class DisallowIntercept implements Runnable {
            private DisallowIntercept() {
            }

            @Override // java.lang.Runnable
            public void run() {
                ForwardingListener.this.mSrc.getParent().requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    private static class DropDownListView extends ListView {
        private static final int CLICK_ANIM_ALPHA = 128;
        private static final long CLICK_ANIM_DURATION = 150;
        private static final IntProperty<Drawable> DRAWABLE_ALPHA = new IntProperty<Drawable>("alpha") { // from class: android.widget.ListPopupWindow.DropDownListView.1
            @Override // android.util.IntProperty
            public void setValue(Drawable drawable, int i) {
                drawable.setAlpha(i);
            }

            @Override // android.util.Property
            public Integer get(Drawable drawable) {
                return Integer.valueOf(drawable.getAlpha());
            }
        };
        private Animator mClickAnimation;
        private boolean mDrawsInPressedState;
        private boolean mHijackFocus;
        private boolean mListSelectionHidden;
        private AutoScrollHelper.AbsListViewAutoScroller mScrollHelper;

        public DropDownListView(Context context, boolean z) {
            super(context, null, R.attr.dropDownListViewStyle);
            this.mHijackFocus = z;
            setCacheColorHint(0);
        }

        /* JADX WARN: Removed duplicated region for block: B:23:0x0048  */
        /* JADX WARN: Removed duplicated region for block: B:25:0x004d  */
        /* JADX WARN: Removed duplicated region for block: B:29:0x0063  */
        /* JADX WARN: Removed duplicated region for block: B:9:0x0011  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean onForwardedEvent(android.view.MotionEvent r6, int r7) {
            /*
                r5 = this;
                int r0 = r6.getActionMasked()
                r1 = 0
                r2 = 1
                if (r0 == r2) goto L16
                r3 = 2
                if (r0 == r3) goto L14
                r7 = 3
                if (r0 == r7) goto L11
            Le:
                r7 = r1
                r3 = r2
                goto L44
            L11:
                r7 = r1
                r3 = r7
                goto L44
            L14:
                r3 = r2
                goto L17
            L16:
                r3 = r1
            L17:
                int r7 = r6.findPointerIndex(r7)
                if (r7 >= 0) goto L1e
                goto L11
            L1e:
                float r4 = r6.getX(r7)
                int r4 = (int) r4
                float r7 = r6.getY(r7)
                int r7 = (int) r7
                int r7 = r5.pointToPosition(r4, r7)
                r4 = -1
                if (r7 != r4) goto L31
                r7 = r2
                goto L44
            L31:
                int r3 = r5.getFirstVisiblePosition()
                int r3 = r7 - r3
                android.view.View r3 = r5.getChildAt(r3)
                r5.setPressedItem(r3, r7)
                if (r0 != r2) goto Le
                r5.clickPressedItem(r3, r7)
                goto Le
            L44:
                if (r3 == 0) goto L48
                if (r7 == 0) goto L4b
            L48:
                r5.clearPressedItem()
            L4b:
                if (r3 == 0) goto L63
                com.android.internal.widget.AutoScrollHelper$AbsListViewAutoScroller r7 = r5.mScrollHelper
                if (r7 != 0) goto L58
                com.android.internal.widget.AutoScrollHelper$AbsListViewAutoScroller r7 = new com.android.internal.widget.AutoScrollHelper$AbsListViewAutoScroller
                r7.<init>(r5)
                r5.mScrollHelper = r7
            L58:
                com.android.internal.widget.AutoScrollHelper$AbsListViewAutoScroller r7 = r5.mScrollHelper
                r7.setEnabled(r2)
                com.android.internal.widget.AutoScrollHelper$AbsListViewAutoScroller r7 = r5.mScrollHelper
                r7.onTouch(r5, r6)
                goto L6a
            L63:
                com.android.internal.widget.AutoScrollHelper$AbsListViewAutoScroller r6 = r5.mScrollHelper
                if (r6 == 0) goto L6a
                r6.setEnabled(r1)
            L6a:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.ListPopupWindow.DropDownListView.onForwardedEvent(android.view.MotionEvent, int):boolean");
        }

        private void clickPressedItem(final View view, final int i) {
            final long itemIdAtPosition = getItemIdAtPosition(i);
            ObjectAnimator objectAnimatorOfInt = ObjectAnimator.ofInt(this.mSelector, DRAWABLE_ALPHA, 255, 128, 255);
            objectAnimatorOfInt.setDuration(150L);
            objectAnimatorOfInt.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimatorOfInt.addListener(new AnimatorListenerAdapter() { // from class: android.widget.ListPopupWindow.DropDownListView.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    DropDownListView.this.performItemClick(view, i, itemIdAtPosition);
                }
            });
            objectAnimatorOfInt.start();
            Animator animator = this.mClickAnimation;
            if (animator != null) {
                animator.cancel();
            }
            this.mClickAnimation = objectAnimatorOfInt;
        }

        private void clearPressedItem() {
            this.mDrawsInPressedState = false;
            setPressed(false);
            updateSelectorState();
            Animator animator = this.mClickAnimation;
            if (animator != null) {
                animator.cancel();
                this.mClickAnimation = null;
            }
        }

        private void setPressedItem(View view, int i) {
            this.mDrawsInPressedState = true;
            setPressed(true);
            layoutChildren();
            setSelectedPositionInt(i);
            positionSelector(i, view);
            refreshDrawableState();
            Animator animator = this.mClickAnimation;
            if (animator != null) {
                animator.cancel();
                this.mClickAnimation = null;
            }
        }

        @Override // android.widget.AbsListView
        boolean touchModeDrawsInPressedState() {
            return this.mDrawsInPressedState || super.touchModeDrawsInPressedState();
        }

        @Override // android.widget.AbsListView
        View obtainView(int i, boolean[] zArr) {
            View viewObtainView = super.obtainView(i, zArr);
            if (viewObtainView instanceof TextView) {
                ((TextView) viewObtainView).setHorizontallyScrolling(true);
            }
            return viewObtainView;
        }

        @Override // android.view.View
        public boolean isInTouchMode() {
            return (this.mHijackFocus && this.mListSelectionHidden) || super.isInTouchMode();
        }

        @Override // android.view.View
        public boolean hasWindowFocus() {
            return this.mHijackFocus || super.hasWindowFocus();
        }

        @Override // android.view.View
        public boolean isFocused() {
            return this.mHijackFocus || super.isFocused();
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean hasFocus() {
            return this.mHijackFocus || super.hasFocus();
        }
    }

    private class PopupDataSetObserver extends DataSetObserver {
        private PopupDataSetObserver() {
        }

        @Override // android.database.DataSetObserver
        public void onChanged() {
            if (ListPopupWindow.this.isShowing()) {
                ListPopupWindow.this.show();
            }
        }

        @Override // android.database.DataSetObserver
        public void onInvalidated() {
            ListPopupWindow.this.dismiss();
        }
    }

    private class ListSelectorHider implements Runnable {
        private ListSelectorHider() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ListPopupWindow.this.clearListSelection();
        }
    }

    private class ResizePopupRunnable implements Runnable {
        private ResizePopupRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (ListPopupWindow.this.mDropDownList == null || ListPopupWindow.this.mDropDownList.getCount() <= ListPopupWindow.this.mDropDownList.getChildCount() || ListPopupWindow.this.mDropDownList.getChildCount() > ListPopupWindow.this.mListItemExpandMaximum) {
                return;
            }
            ListPopupWindow.this.mPopup.setInputMethodMode(2);
            ListPopupWindow.this.show();
        }
    }

    private class PopupTouchInterceptor implements View.OnTouchListener {
        private PopupTouchInterceptor() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (action == 0 && ListPopupWindow.this.mPopup != null && ListPopupWindow.this.mPopup.isShowing() && x >= 0 && x < ListPopupWindow.this.mPopup.getWidth() && y >= 0 && y < ListPopupWindow.this.mPopup.getHeight()) {
                ListPopupWindow.this.mHandler.postDelayed(ListPopupWindow.this.mResizePopupRunnable, 250L);
                return false;
            }
            if (action != 1) {
                return false;
            }
            ListPopupWindow.this.mHandler.removeCallbacks(ListPopupWindow.this.mResizePopupRunnable);
            return false;
        }
    }

    private class PopupScrollListener implements AbsListView.OnScrollListener {
        @Override // android.widget.AbsListView.OnScrollListener
        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        }

        private PopupScrollListener() {
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i != 1 || ListPopupWindow.this.isInputMethodNotNeeded() || ListPopupWindow.this.mPopup.getContentView() == null) {
                return;
            }
            ListPopupWindow.this.mHandler.removeCallbacks(ListPopupWindow.this.mResizePopupRunnable);
            ListPopupWindow.this.mResizePopupRunnable.run();
        }
    }
}
