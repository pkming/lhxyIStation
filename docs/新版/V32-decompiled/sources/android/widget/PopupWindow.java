package android.widget;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class PopupWindow {
    private static final int[] ABOVE_ANCHOR_STATE_SET = {R.attr.state_above_anchor};
    private static final int DEFAULT_ANCHORED_GRAVITY = 8388659;
    public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;
    public static final int INPUT_METHOD_NEEDED = 1;
    public static final int INPUT_METHOD_NOT_NEEDED = 2;
    private boolean mAboveAnchor;
    private Drawable mAboveAnchorBackgroundDrawable;
    private boolean mAllowScrollingAnchorParent;
    private WeakReference<View> mAnchor;
    private int mAnchorXoff;
    private int mAnchorYoff;
    private int mAnchoredGravity;
    private int mAnimationStyle;
    private Drawable mBackground;
    private Drawable mBelowAnchorBackgroundDrawable;
    private boolean mClipToScreen;
    private boolean mClippingEnabled;
    private View mContentView;
    private Context mContext;
    private int[] mDrawingLocation;
    private boolean mFocusable;
    private int mHeight;
    private int mHeightMode;
    private boolean mIgnoreCheekPress;
    private int mInputMethodMode;
    private boolean mIsDropdown;
    private boolean mIsShowing;
    private int mLastHeight;
    private int mLastWidth;
    private boolean mLayoutInScreen;
    private boolean mLayoutInsetDecor;
    private boolean mNotTouchModal;
    private OnDismissListener mOnDismissListener;
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;
    private boolean mOutsideTouchable;
    private int mPopupHeight;
    private View mPopupView;
    private boolean mPopupViewInitialLayoutDirectionInherited;
    private int mPopupWidth;
    private int[] mScreenLocation;
    private int mSoftInputMode;
    private int mSplitTouchEnabled;
    private Rect mTempRect;
    private View.OnTouchListener mTouchInterceptor;
    private boolean mTouchable;
    private int mWidth;
    private int mWidthMode;
    private int mWindowLayoutType;
    private WindowManager mWindowManager;

    public interface OnDismissListener {
        void onDismiss();
    }

    public PopupWindow(Context context) {
        this(context, (AttributeSet) null);
    }

    public PopupWindow(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.popupWindowStyle);
    }

    public PopupWindow(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public PopupWindow(Context context, AttributeSet attributeSet, int i, int i2) {
        int i3 = 0;
        this.mInputMethodMode = 0;
        this.mSoftInputMode = 1;
        this.mTouchable = true;
        this.mOutsideTouchable = false;
        this.mClippingEnabled = true;
        this.mSplitTouchEnabled = -1;
        this.mAllowScrollingAnchorParent = true;
        this.mLayoutInsetDecor = false;
        this.mDrawingLocation = new int[2];
        this.mScreenLocation = new int[2];
        this.mTempRect = new Rect();
        this.mWindowLayoutType = 1000;
        this.mIgnoreCheekPress = false;
        this.mAnimationStyle = -1;
        this.mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() { // from class: android.widget.PopupWindow.1
            @Override // android.view.ViewTreeObserver.OnScrollChangedListener
            public void onScrollChanged() {
                View view = PopupWindow.this.mAnchor != null ? (View) PopupWindow.this.mAnchor.get() : null;
                if (view == null || PopupWindow.this.mPopupView == null) {
                    return;
                }
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) PopupWindow.this.mPopupView.getLayoutParams();
                PopupWindow popupWindow = PopupWindow.this;
                popupWindow.updateAboveAnchor(popupWindow.findDropDownPosition(view, layoutParams, popupWindow.mAnchorXoff, PopupWindow.this.mAnchorYoff, PopupWindow.this.mAnchoredGravity));
                PopupWindow.this.update(layoutParams.x, layoutParams.y, -1, -1, true);
            }
        };
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, com.android.internal.R.styleable.PopupWindow, i, i2);
        this.mBackground = typedArrayObtainStyledAttributes.getDrawable(0);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(1, -1);
        this.mAnimationStyle = resourceId == 16974326 ? -1 : resourceId;
        Drawable drawable = this.mBackground;
        if (drawable instanceof StateListDrawable) {
            StateListDrawable stateListDrawable = (StateListDrawable) drawable;
            int stateDrawableIndex = stateListDrawable.getStateDrawableIndex(ABOVE_ANCHOR_STATE_SET);
            int stateCount = stateListDrawable.getStateCount();
            while (true) {
                if (i3 >= stateCount) {
                    i3 = -1;
                    break;
                } else if (i3 != stateDrawableIndex) {
                    break;
                } else {
                    i3++;
                }
            }
            if (stateDrawableIndex != -1 && i3 != -1) {
                this.mAboveAnchorBackgroundDrawable = stateListDrawable.getStateDrawable(stateDrawableIndex);
                this.mBelowAnchorBackgroundDrawable = stateListDrawable.getStateDrawable(i3);
            } else {
                this.mBelowAnchorBackgroundDrawable = null;
                this.mAboveAnchorBackgroundDrawable = null;
            }
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    public PopupWindow() {
        this((View) null, 0, 0);
    }

    public PopupWindow(View view) {
        this(view, 0, 0);
    }

    public PopupWindow(int i, int i2) {
        this((View) null, i, i2);
    }

    public PopupWindow(View view, int i, int i2) {
        this(view, i, i2, false);
    }

    public PopupWindow(View view, int i, int i2, boolean z) {
        this.mInputMethodMode = 0;
        this.mSoftInputMode = 1;
        this.mTouchable = true;
        this.mOutsideTouchable = false;
        this.mClippingEnabled = true;
        this.mSplitTouchEnabled = -1;
        this.mAllowScrollingAnchorParent = true;
        this.mLayoutInsetDecor = false;
        this.mDrawingLocation = new int[2];
        this.mScreenLocation = new int[2];
        this.mTempRect = new Rect();
        this.mWindowLayoutType = 1000;
        this.mIgnoreCheekPress = false;
        this.mAnimationStyle = -1;
        this.mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() { // from class: android.widget.PopupWindow.1
            @Override // android.view.ViewTreeObserver.OnScrollChangedListener
            public void onScrollChanged() {
                View view2 = PopupWindow.this.mAnchor != null ? (View) PopupWindow.this.mAnchor.get() : null;
                if (view2 == null || PopupWindow.this.mPopupView == null) {
                    return;
                }
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) PopupWindow.this.mPopupView.getLayoutParams();
                PopupWindow popupWindow = PopupWindow.this;
                popupWindow.updateAboveAnchor(popupWindow.findDropDownPosition(view2, layoutParams, popupWindow.mAnchorXoff, PopupWindow.this.mAnchorYoff, PopupWindow.this.mAnchoredGravity));
                PopupWindow.this.update(layoutParams.x, layoutParams.y, -1, -1, true);
            }
        };
        if (view != null) {
            Context context = view.getContext();
            this.mContext = context;
            this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        setContentView(view);
        setWidth(i);
        setHeight(i2);
        setFocusable(z);
    }

    public Drawable getBackground() {
        return this.mBackground;
    }

    public void setBackgroundDrawable(Drawable drawable) {
        this.mBackground = drawable;
    }

    public int getAnimationStyle() {
        return this.mAnimationStyle;
    }

    public void setIgnoreCheekPress() {
        this.mIgnoreCheekPress = true;
    }

    public void setAnimationStyle(int i) {
        this.mAnimationStyle = i;
    }

    public View getContentView() {
        return this.mContentView;
    }

    public void setContentView(View view) {
        if (isShowing()) {
            return;
        }
        this.mContentView = view;
        if (this.mContext == null && view != null) {
            this.mContext = view.getContext();
        }
        if (this.mWindowManager != null || this.mContentView == null) {
            return;
        }
        this.mWindowManager = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public void setTouchInterceptor(View.OnTouchListener onTouchListener) {
        this.mTouchInterceptor = onTouchListener;
    }

    public boolean isFocusable() {
        return this.mFocusable;
    }

    public void setFocusable(boolean z) {
        this.mFocusable = z;
    }

    public int getInputMethodMode() {
        return this.mInputMethodMode;
    }

    public void setInputMethodMode(int i) {
        this.mInputMethodMode = i;
    }

    public void setSoftInputMode(int i) {
        this.mSoftInputMode = i;
    }

    public int getSoftInputMode() {
        return this.mSoftInputMode;
    }

    public boolean isTouchable() {
        return this.mTouchable;
    }

    public void setTouchable(boolean z) {
        this.mTouchable = z;
    }

    public boolean isOutsideTouchable() {
        return this.mOutsideTouchable;
    }

    public void setOutsideTouchable(boolean z) {
        this.mOutsideTouchable = z;
    }

    public boolean isClippingEnabled() {
        return this.mClippingEnabled;
    }

    public void setClippingEnabled(boolean z) {
        this.mClippingEnabled = z;
    }

    public void setClipToScreenEnabled(boolean z) {
        this.mClipToScreen = z;
        setClippingEnabled(!z);
    }

    void setAllowScrollingAnchorParent(boolean z) {
        this.mAllowScrollingAnchorParent = z;
    }

    public boolean isSplitTouchEnabled() {
        Context context;
        int i = this.mSplitTouchEnabled;
        return (i >= 0 || (context = this.mContext) == null) ? i == 1 : context.getApplicationInfo().targetSdkVersion >= 11;
    }

    public void setSplitTouchEnabled(boolean z) {
        this.mSplitTouchEnabled = z ? 1 : 0;
    }

    public boolean isLayoutInScreenEnabled() {
        return this.mLayoutInScreen;
    }

    public void setLayoutInScreenEnabled(boolean z) {
        this.mLayoutInScreen = z;
    }

    public void setLayoutInsetDecor(boolean z) {
        this.mLayoutInsetDecor = z;
    }

    public void setWindowLayoutType(int i) {
        this.mWindowLayoutType = i;
    }

    public int getWindowLayoutType() {
        return this.mWindowLayoutType;
    }

    public void setTouchModal(boolean z) {
        this.mNotTouchModal = !z;
    }

    public void setWindowLayoutMode(int i, int i2) {
        this.mWidthMode = i;
        this.mHeightMode = i2;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public void setHeight(int i) {
        this.mHeight = i;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public void setWidth(int i) {
        this.mWidth = i;
    }

    public boolean isShowing() {
        return this.mIsShowing;
    }

    public void showAtLocation(View view, int i, int i2, int i3) {
        showAtLocation(view.getWindowToken(), i, i2, i3);
    }

    public void showAtLocation(IBinder iBinder, int i, int i2, int i3) {
        if (isShowing() || this.mContentView == null) {
            return;
        }
        unregisterForScrollChanged();
        this.mIsShowing = true;
        this.mIsDropdown = false;
        WindowManager.LayoutParams layoutParamsCreatePopupLayout = createPopupLayout(iBinder);
        layoutParamsCreatePopupLayout.windowAnimations = computeAnimationResource();
        preparePopup(layoutParamsCreatePopupLayout);
        if (i == 0) {
            i = 8388659;
        }
        layoutParamsCreatePopupLayout.gravity = i;
        layoutParamsCreatePopupLayout.x = i2;
        layoutParamsCreatePopupLayout.y = i3;
        int i4 = this.mHeightMode;
        if (i4 < 0) {
            this.mLastHeight = i4;
            layoutParamsCreatePopupLayout.height = i4;
        }
        int i5 = this.mWidthMode;
        if (i5 < 0) {
            this.mLastWidth = i5;
            layoutParamsCreatePopupLayout.width = i5;
        }
        invokePopup(layoutParamsCreatePopupLayout);
    }

    public void showAsDropDown(View view) {
        showAsDropDown(view, 0, 0);
    }

    public void showAsDropDown(View view, int i, int i2) {
        showAsDropDown(view, i, i2, 8388659);
    }

    public void showAsDropDown(View view, int i, int i2, int i3) {
        if (isShowing() || this.mContentView == null) {
            return;
        }
        registerForScrollChanged(view, i, i2, i3);
        this.mIsShowing = true;
        this.mIsDropdown = true;
        WindowManager.LayoutParams layoutParamsCreatePopupLayout = createPopupLayout(view.getWindowToken());
        preparePopup(layoutParamsCreatePopupLayout);
        updateAboveAnchor(findDropDownPosition(view, layoutParamsCreatePopupLayout, i, i2, i3));
        int i4 = this.mHeightMode;
        if (i4 < 0) {
            this.mLastHeight = i4;
            layoutParamsCreatePopupLayout.height = i4;
        }
        int i5 = this.mWidthMode;
        if (i5 < 0) {
            this.mLastWidth = i5;
            layoutParamsCreatePopupLayout.width = i5;
        }
        layoutParamsCreatePopupLayout.windowAnimations = computeAnimationResource();
        invokePopup(layoutParamsCreatePopupLayout);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAboveAnchor(boolean z) {
        if (z != this.mAboveAnchor) {
            this.mAboveAnchor = z;
            if (this.mBackground != null) {
                Drawable drawable = this.mAboveAnchorBackgroundDrawable;
                if (drawable == null) {
                    this.mPopupView.refreshDrawableState();
                } else if (z) {
                    this.mPopupView.setBackgroundDrawable(drawable);
                } else {
                    this.mPopupView.setBackgroundDrawable(this.mBelowAnchorBackgroundDrawable);
                }
            }
        }
    }

    public boolean isAboveAnchor() {
        return this.mAboveAnchor;
    }

    private void preparePopup(WindowManager.LayoutParams layoutParams) {
        View view = this.mContentView;
        if (view == null || this.mContext == null || this.mWindowManager == null) {
            throw new IllegalStateException("You must specify a valid content view by calling setContentView() before attempting to show the popup.");
        }
        if (this.mBackground != null) {
            ViewGroup.LayoutParams layoutParams2 = view.getLayoutParams();
            int i = (layoutParams2 == null || layoutParams2.height != -2) ? -1 : -2;
            PopupViewContainer popupViewContainer = new PopupViewContainer(this.mContext);
            FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(-1, i);
            popupViewContainer.setBackgroundDrawable(this.mBackground);
            popupViewContainer.addView(this.mContentView, layoutParams3);
            this.mPopupView = popupViewContainer;
        } else {
            this.mPopupView = view;
        }
        this.mPopupViewInitialLayoutDirectionInherited = this.mPopupView.getRawLayoutDirection() == 2;
        this.mPopupWidth = layoutParams.width;
        this.mPopupHeight = layoutParams.height;
    }

    private void invokePopup(WindowManager.LayoutParams layoutParams) {
        Context context = this.mContext;
        if (context != null) {
            layoutParams.packageName = context.getPackageName();
        }
        this.mPopupView.setFitsSystemWindows(this.mLayoutInsetDecor);
        setLayoutDirectionFromAnchor();
        this.mWindowManager.addView(this.mPopupView, layoutParams);
    }

    private void setLayoutDirectionFromAnchor() {
        View view;
        WeakReference<View> weakReference = this.mAnchor;
        if (weakReference == null || (view = weakReference.get()) == null || !this.mPopupViewInitialLayoutDirectionInherited) {
            return;
        }
        this.mPopupView.setLayoutDirection(view.getLayoutDirection());
    }

    private WindowManager.LayoutParams createPopupLayout(IBinder iBinder) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = 8388659;
        int i = this.mWidth;
        this.mLastWidth = i;
        layoutParams.width = i;
        int i2 = this.mHeight;
        this.mLastHeight = i2;
        layoutParams.height = i2;
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            layoutParams.format = drawable.getOpacity();
        } else {
            layoutParams.format = -3;
        }
        layoutParams.flags = computeFlags(layoutParams.flags);
        layoutParams.type = this.mWindowLayoutType;
        layoutParams.token = iBinder;
        layoutParams.softInputMode = this.mSoftInputMode;
        layoutParams.setTitle("PopupWindow:" + Integer.toHexString(hashCode()));
        return layoutParams;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x001f A[PHI: r4
      0x001f: PHI (r4v3 int) = (r4v2 int), (r4v19 int) binds: [B:11:0x001d, B:8:0x0017] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int computeFlags(int r4) {
        /*
            r3 = this;
            r0 = -8815129(0xffffffffff797de7, float:-3.316315E38)
            r4 = r4 & r0
            boolean r0 = r3.mIgnoreCheekPress
            if (r0 == 0) goto Lc
            r0 = 32768(0x8000, float:4.5918E-41)
            r4 = r4 | r0
        Lc:
            boolean r0 = r3.mFocusable
            r1 = 131072(0x20000, float:1.83671E-40)
            if (r0 != 0) goto L1a
            r4 = r4 | 8
            int r0 = r3.mInputMethodMode
            r2 = 1
            if (r0 != r2) goto L20
            goto L1f
        L1a:
            int r0 = r3.mInputMethodMode
            r2 = 2
            if (r0 != r2) goto L20
        L1f:
            r4 = r4 | r1
        L20:
            boolean r0 = r3.mTouchable
            if (r0 != 0) goto L26
            r4 = r4 | 16
        L26:
            boolean r0 = r3.mOutsideTouchable
            if (r0 == 0) goto L2d
            r0 = 262144(0x40000, float:3.67342E-40)
            r4 = r4 | r0
        L2d:
            boolean r0 = r3.mClippingEnabled
            if (r0 != 0) goto L33
            r4 = r4 | 512(0x200, float:7.175E-43)
        L33:
            boolean r0 = r3.isSplitTouchEnabled()
            if (r0 == 0) goto L3c
            r0 = 8388608(0x800000, float:1.17549435E-38)
            r4 = r4 | r0
        L3c:
            boolean r0 = r3.mLayoutInScreen
            if (r0 == 0) goto L42
            r4 = r4 | 256(0x100, float:3.59E-43)
        L42:
            boolean r0 = r3.mLayoutInsetDecor
            if (r0 == 0) goto L49
            r0 = 65536(0x10000, float:9.18355E-41)
            r4 = r4 | r0
        L49:
            boolean r0 = r3.mNotTouchModal
            if (r0 == 0) goto L4f
            r4 = r4 | 32
        L4f:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.PopupWindow.computeFlags(int):int");
    }

    private int computeAnimationResource() {
        int i = this.mAnimationStyle;
        if (i != -1) {
            return i;
        }
        if (this.mIsDropdown) {
            return this.mAboveAnchor ? 16974320 : 16974319;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean findDropDownPosition(View view, WindowManager.LayoutParams layoutParams, int i, int i2, int i3) {
        int height = view.getHeight();
        view.getLocationInWindow(this.mDrawingLocation);
        layoutParams.x = this.mDrawingLocation[0] + i;
        layoutParams.y = this.mDrawingLocation[1] + height + i2;
        int absoluteGravity = Gravity.getAbsoluteGravity(i3, view.getLayoutDirection()) & 7;
        if (absoluteGravity == 5) {
            layoutParams.x -= this.mPopupWidth - view.getWidth();
        }
        layoutParams.gravity = 51;
        view.getLocationOnScreen(this.mScreenLocation);
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int i4 = this.mScreenLocation[1] + height + i2;
        View rootView = view.getRootView();
        if (i4 + this.mPopupHeight > rect.bottom || (layoutParams.x + this.mPopupWidth) - rootView.getWidth() > 0) {
            if (this.mAllowScrollingAnchorParent) {
                int scrollX = view.getScrollX();
                int scrollY = view.getScrollY();
                view.requestRectangleOnScreen(new Rect(scrollX, scrollY, this.mPopupWidth + scrollX + i, this.mPopupHeight + scrollY + view.getHeight() + i2), true);
            }
            view.getLocationInWindow(this.mDrawingLocation);
            layoutParams.x = this.mDrawingLocation[0] + i;
            layoutParams.y = this.mDrawingLocation[1] + view.getHeight() + i2;
            if (absoluteGravity == 5) {
                layoutParams.x -= this.mPopupWidth - view.getWidth();
            }
            view.getLocationOnScreen(this.mScreenLocation);
            z = ((rect.bottom - this.mScreenLocation[1]) - view.getHeight()) - i2 < (this.mScreenLocation[1] - i2) - rect.top;
            if (z) {
                layoutParams.gravity = 83;
                layoutParams.y = (rootView.getHeight() - this.mDrawingLocation[1]) + i2;
            } else {
                layoutParams.y = this.mDrawingLocation[1] + view.getHeight() + i2;
            }
        }
        if (this.mClipToScreen) {
            int i5 = rect.right - rect.left;
            int i6 = layoutParams.x + layoutParams.width;
            if (i6 > i5) {
                layoutParams.x -= i6 - i5;
            }
            if (layoutParams.x < rect.left) {
                layoutParams.x = rect.left;
                layoutParams.width = Math.min(layoutParams.width, i5);
            }
            if (z) {
                int i7 = (this.mScreenLocation[1] + i2) - this.mPopupHeight;
                if (i7 < 0) {
                    layoutParams.y += i7;
                }
            } else {
                layoutParams.y = Math.max(layoutParams.y, rect.top);
            }
        }
        layoutParams.gravity |= 268435456;
        return z;
    }

    public int getMaxAvailableHeight(View view) {
        return getMaxAvailableHeight(view, 0);
    }

    public int getMaxAvailableHeight(View view, int i) {
        return getMaxAvailableHeight(view, i, false);
    }

    public int getMaxAvailableHeight(View view, int i, boolean z) {
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int[] iArr = this.mDrawingLocation;
        view.getLocationOnScreen(iArr);
        int i2 = rect.bottom;
        if (z) {
            i2 = view.getContext().getResources().getDisplayMetrics().heightPixels;
        }
        int iMax = Math.max((i2 - (iArr[1] + view.getHeight())) - i, (iArr[1] - rect.top) + i);
        Drawable drawable = this.mBackground;
        if (drawable == null) {
            return iMax;
        }
        drawable.getPadding(this.mTempRect);
        return iMax - (this.mTempRect.top + this.mTempRect.bottom);
    }

    public void dismiss() {
        if (!isShowing() || this.mPopupView == null) {
            return;
        }
        this.mIsShowing = false;
        unregisterForScrollChanged();
        try {
            this.mWindowManager.removeViewImmediate(this.mPopupView);
        } finally {
            View view = this.mPopupView;
            View view2 = this.mContentView;
            if (view != view2 && (view instanceof ViewGroup)) {
                ((ViewGroup) view).removeView(view2);
            }
            this.mPopupView = null;
            OnDismissListener onDismissListener = this.mOnDismissListener;
            if (onDismissListener != null) {
                onDismissListener.onDismiss();
            }
        }
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public void update() {
        if (!isShowing() || this.mContentView == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) this.mPopupView.getLayoutParams();
        boolean z = false;
        int iComputeAnimationResource = computeAnimationResource();
        boolean z2 = true;
        if (iComputeAnimationResource != layoutParams.windowAnimations) {
            layoutParams.windowAnimations = iComputeAnimationResource;
            z = true;
        }
        int iComputeFlags = computeFlags(layoutParams.flags);
        if (iComputeFlags != layoutParams.flags) {
            layoutParams.flags = iComputeFlags;
        } else {
            z2 = z;
        }
        if (z2) {
            setLayoutDirectionFromAnchor();
            this.mWindowManager.updateViewLayout(this.mPopupView, layoutParams);
        }
    }

    public void update(int i, int i2) {
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) this.mPopupView.getLayoutParams();
        update(layoutParams.x, layoutParams.y, i, i2, false);
    }

    public void update(int i, int i2, int i3, int i4) {
        update(i, i2, i3, i4, false);
    }

    public void update(int i, int i2, int i3, int i4, boolean z) {
        if (i3 != -1) {
            this.mLastWidth = i3;
            setWidth(i3);
        }
        if (i4 != -1) {
            this.mLastHeight = i4;
            setHeight(i4);
        }
        if (!isShowing() || this.mContentView == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) this.mPopupView.getLayoutParams();
        int i5 = this.mWidthMode;
        if (i5 >= 0) {
            i5 = this.mLastWidth;
        }
        boolean z2 = true;
        if (i3 != -1 && layoutParams.width != i5) {
            this.mLastWidth = i5;
            layoutParams.width = i5;
            z = true;
        }
        int i6 = this.mHeightMode;
        if (i6 >= 0) {
            i6 = this.mLastHeight;
        }
        if (i4 != -1 && layoutParams.height != i6) {
            this.mLastHeight = i6;
            layoutParams.height = i6;
            z = true;
        }
        if (layoutParams.x != i) {
            layoutParams.x = i;
            z = true;
        }
        if (layoutParams.y != i2) {
            layoutParams.y = i2;
            z = true;
        }
        int iComputeAnimationResource = computeAnimationResource();
        if (iComputeAnimationResource != layoutParams.windowAnimations) {
            layoutParams.windowAnimations = iComputeAnimationResource;
            z = true;
        }
        int iComputeFlags = computeFlags(layoutParams.flags);
        if (iComputeFlags != layoutParams.flags) {
            layoutParams.flags = iComputeFlags;
        } else {
            z2 = z;
        }
        if (z2) {
            setLayoutDirectionFromAnchor();
            this.mWindowManager.updateViewLayout(this.mPopupView, layoutParams);
        }
    }

    public void update(View view, int i, int i2) {
        update(view, false, 0, 0, true, i, i2, this.mAnchoredGravity);
    }

    public void update(View view, int i, int i2, int i3, int i4) {
        update(view, true, i, i2, true, i3, i4, this.mAnchoredGravity);
    }

    private void update(View view, boolean z, int i, int i2, boolean z2, int i3, int i4, int i5) {
        int i6 = i3;
        int i7 = i4;
        if (!isShowing() || this.mContentView == null) {
            return;
        }
        WeakReference<View> weakReference = this.mAnchor;
        boolean z3 = true;
        boolean z4 = z && !(this.mAnchorXoff == i && this.mAnchorYoff == i2);
        if (weakReference == null || weakReference.get() != view || (z4 && !this.mIsDropdown)) {
            registerForScrollChanged(view, i, i2, i5);
        } else if (z4) {
            this.mAnchorXoff = i;
            this.mAnchorYoff = i2;
            this.mAnchoredGravity = i5;
        }
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) this.mPopupView.getLayoutParams();
        if (z2) {
            if (i6 == -1) {
                i6 = this.mPopupWidth;
            } else {
                this.mPopupWidth = i6;
            }
            if (i7 == -1) {
                i7 = this.mPopupHeight;
            } else {
                this.mPopupHeight = i7;
            }
        }
        int i8 = i6;
        int i9 = i7;
        int i10 = layoutParams.x;
        int i11 = layoutParams.y;
        if (z) {
            updateAboveAnchor(findDropDownPosition(view, layoutParams, i, i2, i5));
        } else {
            updateAboveAnchor(findDropDownPosition(view, layoutParams, this.mAnchorXoff, this.mAnchorYoff, this.mAnchoredGravity));
        }
        int i12 = layoutParams.x;
        int i13 = layoutParams.y;
        if (i10 == layoutParams.x && i11 == layoutParams.y) {
            z3 = false;
        }
        update(i12, i13, i8, i9, z3);
    }

    private void unregisterForScrollChanged() {
        WeakReference<View> weakReference = this.mAnchor;
        View view = weakReference != null ? weakReference.get() : null;
        if (view != null) {
            view.getViewTreeObserver().removeOnScrollChangedListener(this.mOnScrollChangedListener);
        }
        this.mAnchor = null;
    }

    private void registerForScrollChanged(View view, int i, int i2, int i3) {
        unregisterForScrollChanged();
        this.mAnchor = new WeakReference<>(view);
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        if (viewTreeObserver != null) {
            viewTreeObserver.addOnScrollChangedListener(this.mOnScrollChangedListener);
        }
        this.mAnchorXoff = i;
        this.mAnchorYoff = i2;
        this.mAnchoredGravity = i3;
    }

    private class PopupViewContainer extends FrameLayout {
        private static final String TAG = "PopupWindow.PopupViewContainer";

        public PopupViewContainer(Context context) {
            super(context);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected int[] onCreateDrawableState(int i) {
            if (PopupWindow.this.mAboveAnchor) {
                int[] iArrOnCreateDrawableState = super.onCreateDrawableState(i + 1);
                View.mergeDrawableStates(iArrOnCreateDrawableState, PopupWindow.ABOVE_ANCHOR_STATE_SET);
                return iArrOnCreateDrawableState;
            }
            return super.onCreateDrawableState(i);
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            KeyEvent.DispatcherState keyDispatcherState;
            if (keyEvent.getKeyCode() == 4) {
                if (getKeyDispatcherState() == null) {
                    return super.dispatchKeyEvent(keyEvent);
                }
                if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
                    KeyEvent.DispatcherState keyDispatcherState2 = getKeyDispatcherState();
                    if (keyDispatcherState2 != null) {
                        keyDispatcherState2.startTracking(keyEvent, this);
                    }
                    return true;
                }
                if (keyEvent.getAction() == 1 && (keyDispatcherState = getKeyDispatcherState()) != null && keyDispatcherState.isTracking(keyEvent) && !keyEvent.isCanceled()) {
                    PopupWindow.this.dismiss();
                    return true;
                }
                return super.dispatchKeyEvent(keyEvent);
            }
            return super.dispatchKeyEvent(keyEvent);
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            if (PopupWindow.this.mTouchInterceptor == null || !PopupWindow.this.mTouchInterceptor.onTouch(this, motionEvent)) {
                return super.dispatchTouchEvent(motionEvent);
            }
            return true;
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (motionEvent.getAction() == 0 && (x < 0 || x >= getWidth() || y < 0 || y >= getHeight())) {
                PopupWindow.this.dismiss();
                return true;
            }
            if (motionEvent.getAction() == 4) {
                PopupWindow.this.dismiss();
                return true;
            }
            return super.onTouchEvent(motionEvent);
        }

        @Override // android.view.View, android.view.accessibility.AccessibilityEventSource
        public void sendAccessibilityEvent(int i) {
            if (PopupWindow.this.mContentView != null) {
                PopupWindow.this.mContentView.sendAccessibilityEvent(i);
            } else {
                super.sendAccessibilityEvent(i);
            }
        }
    }
}
