package android.view;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pools;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.View;
import android.view.ViewDebug;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.Transformation;
import com.android.internal.R;
import com.android.internal.util.Predicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/* JADX INFO: loaded from: classes.dex */
public abstract class ViewGroup extends View implements ViewParent, ViewManager {
    private static final int ARRAY_CAPACITY_INCREMENT = 12;
    private static final int ARRAY_INITIAL_CAPACITY = 12;
    private static final int CHILD_LEFT_INDEX = 0;
    private static final int CHILD_TOP_INDEX = 1;
    protected static final int CLIP_TO_PADDING_MASK = 34;
    private static final boolean DBG = false;
    public static boolean DEBUG_DRAW = false;
    private static final int FLAG_ADD_STATES_FROM_CHILDREN = 8192;
    static final int FLAG_ALPHA_LOWER_THAN_ONE = 4096;
    static final int FLAG_ALWAYS_DRAWN_WITH_CACHE = 16384;
    private static final int FLAG_ANIMATION_CACHE = 64;
    static final int FLAG_ANIMATION_DONE = 16;
    static final int FLAG_CHILDREN_DRAWN_WITH_CACHE = 32768;
    static final int FLAG_CLEAR_TRANSFORMATION = 256;
    static final int FLAG_CLIP_CHILDREN = 1;
    private static final int FLAG_CLIP_TO_PADDING = 2;
    protected static final int FLAG_DISALLOW_INTERCEPT = 524288;
    static final int FLAG_INVALIDATE_REQUIRED = 4;
    private static final int FLAG_LAYOUT_MODE_WAS_EXPLICITLY_SET = 8388608;
    private static final int FLAG_MASK_FOCUSABILITY = 393216;
    private static final int FLAG_NOTIFY_ANIMATION_LISTENER = 512;
    private static final int FLAG_NOTIFY_CHILDREN_ON_DRAWABLE_STATE_CHANGE = 65536;
    static final int FLAG_OPTIMIZE_INVALIDATE = 128;
    private static final int FLAG_PADDING_NOT_NULL = 32;
    private static final int FLAG_PREVENT_DISPATCH_ATTACHED_TO_WINDOW = 4194304;
    private static final int FLAG_RUN_ANIMATION = 8;
    private static final int FLAG_SPLIT_MOTION_EVENTS = 2097152;
    protected static final int FLAG_SUPPORT_STATIC_TRANSFORMATIONS = 2048;
    protected static final int FLAG_USE_CHILD_DRAWING_ORDER = 1024;
    public static final int FOCUS_AFTER_DESCENDANTS = 262144;
    public static final int FOCUS_BEFORE_DESCENDANTS = 131072;
    public static final int FOCUS_BLOCK_DESCENDANTS = 393216;
    public static final int LAYOUT_MODE_CLIP_BOUNDS = 0;
    public static final int LAYOUT_MODE_OPTICAL_BOUNDS = 1;
    private static final int LAYOUT_MODE_UNDEFINED = -1;
    public static final int PERSISTENT_ALL_CACHES = 3;
    public static final int PERSISTENT_ANIMATION_CACHE = 1;
    public static final int PERSISTENT_NO_CACHE = 0;
    public static final int PERSISTENT_SCROLLING_CACHE = 2;
    private static final String TAG = "ViewGroup";
    private static float[] sDebugLines;
    private static Paint sDebugPaint;
    private Animation.AnimationListener mAnimationListener;
    Paint mCachePaint;
    private boolean mChildAcceptsDrag;

    @ViewDebug.ExportedProperty(category = "layout")
    private int mChildCountWithTransientState;
    private Transformation mChildTransformation;
    private View[] mChildren;
    private int mChildrenCount;
    private DragEvent mCurrentDrag;
    private View mCurrentDragView;
    protected ArrayList<View> mDisappearingChildren;
    private HashSet<View> mDragNotifiedChildren;
    private HoverTarget mFirstHoverTarget;
    private TouchTarget mFirstTouchTarget;
    private View mFocused;

    @ViewDebug.ExportedProperty(flagMapping = {@ViewDebug.FlagToString(equals = 1, mask = 1, name = "CLIP_CHILDREN"), @ViewDebug.FlagToString(equals = 2, mask = 2, name = "CLIP_TO_PADDING"), @ViewDebug.FlagToString(equals = 32, mask = 32, name = "PADDING_NOT_NULL")})
    protected int mGroupFlags;
    private boolean mHoveredSelf;
    RectF mInvalidateRegion;
    Transformation mInvalidationTransformation;

    @ViewDebug.ExportedProperty(category = "events")
    private int mLastTouchDownIndex;

    @ViewDebug.ExportedProperty(category = "events")
    private long mLastTouchDownTime;

    @ViewDebug.ExportedProperty(category = "events")
    private float mLastTouchDownX;

    @ViewDebug.ExportedProperty(category = "events")
    private float mLastTouchDownY;
    private LayoutAnimationController mLayoutAnimationController;
    private boolean mLayoutCalledWhileSuppressed;
    private int mLayoutMode;
    private LayoutTransition.TransitionListener mLayoutTransitionListener;
    private PointF mLocalPoint;
    protected OnHierarchyChangeListener mOnHierarchyChangeListener;
    protected int mPersistentDrawingCache;
    boolean mSuppressLayout;
    private LayoutTransition mTransition;
    private ArrayList<View> mTransitioningViews;
    private ArrayList<View> mVisibilityChangingChildren;
    private static final int[] DESCENDANT_FOCUSABILITY_FLAGS = {131072, 262144, 393216};
    public static int LAYOUT_MODE_DEFAULT = 0;

    public interface OnHierarchyChangeListener {
        void onChildViewAdded(View view, View view2);

        void onChildViewRemoved(View view, View view2);
    }

    private static int sign(int i) {
        return i >= 0 ? 1 : -1;
    }

    protected boolean checkLayoutParams(LayoutParams layoutParams) {
        return layoutParams != null;
    }

    protected LayoutParams generateLayoutParams(LayoutParams layoutParams) {
        return layoutParams;
    }

    protected int getChildDrawingOrder(int i, int i2) {
        return i2;
    }

    protected boolean getChildStaticTransformation(View view, Transformation transformation) {
        return false;
    }

    public boolean onInterceptHoverEvent(MotionEvent motionEvent) {
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override // android.view.View
    protected abstract void onLayout(boolean z, int i, int i2, int i3, int i4);

    boolean onRequestSendAccessibilityEventInternal(View view, AccessibilityEvent accessibilityEvent) {
        return true;
    }

    protected void onSetLayoutParams(View view, LayoutParams layoutParams) {
    }

    @Override // android.view.ViewParent
    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
        return false;
    }

    public boolean shouldDelayChildPressedState() {
        return true;
    }

    public ViewGroup(Context context) {
        super(context);
        this.mLastTouchDownIndex = -1;
        this.mLayoutMode = -1;
        this.mSuppressLayout = false;
        this.mLayoutCalledWhileSuppressed = false;
        this.mChildCountWithTransientState = 0;
        this.mLayoutTransitionListener = new LayoutTransition.TransitionListener() { // from class: android.view.ViewGroup.3
            @Override // android.animation.LayoutTransition.TransitionListener
            public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
                if (i == 3) {
                    ViewGroup.this.startViewTransition(view);
                }
            }

            @Override // android.animation.LayoutTransition.TransitionListener
            public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
                if (ViewGroup.this.mLayoutCalledWhileSuppressed && !layoutTransition.isChangingLayout()) {
                    ViewGroup.this.requestLayout();
                    ViewGroup.this.mLayoutCalledWhileSuppressed = false;
                }
                if (i != 3 || ViewGroup.this.mTransitioningViews == null) {
                    return;
                }
                ViewGroup.this.endViewTransition(view);
            }
        };
        initViewGroup();
    }

    public ViewGroup(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mLastTouchDownIndex = -1;
        this.mLayoutMode = -1;
        this.mSuppressLayout = false;
        this.mLayoutCalledWhileSuppressed = false;
        this.mChildCountWithTransientState = 0;
        this.mLayoutTransitionListener = new LayoutTransition.TransitionListener() { // from class: android.view.ViewGroup.3
            @Override // android.animation.LayoutTransition.TransitionListener
            public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
                if (i == 3) {
                    ViewGroup.this.startViewTransition(view);
                }
            }

            @Override // android.animation.LayoutTransition.TransitionListener
            public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
                if (ViewGroup.this.mLayoutCalledWhileSuppressed && !layoutTransition.isChangingLayout()) {
                    ViewGroup.this.requestLayout();
                    ViewGroup.this.mLayoutCalledWhileSuppressed = false;
                }
                if (i != 3 || ViewGroup.this.mTransitioningViews == null) {
                    return;
                }
                ViewGroup.this.endViewTransition(view);
            }
        };
        initViewGroup();
        initFromAttributes(context, attributeSet);
    }

    public ViewGroup(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mLastTouchDownIndex = -1;
        this.mLayoutMode = -1;
        this.mSuppressLayout = false;
        this.mLayoutCalledWhileSuppressed = false;
        this.mChildCountWithTransientState = 0;
        this.mLayoutTransitionListener = new LayoutTransition.TransitionListener() { // from class: android.view.ViewGroup.3
            @Override // android.animation.LayoutTransition.TransitionListener
            public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i2) {
                if (i2 == 3) {
                    ViewGroup.this.startViewTransition(view);
                }
            }

            @Override // android.animation.LayoutTransition.TransitionListener
            public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i2) {
                if (ViewGroup.this.mLayoutCalledWhileSuppressed && !layoutTransition.isChangingLayout()) {
                    ViewGroup.this.requestLayout();
                    ViewGroup.this.mLayoutCalledWhileSuppressed = false;
                }
                if (i2 != 3 || ViewGroup.this.mTransitioningViews == null) {
                    return;
                }
                ViewGroup.this.endViewTransition(view);
            }
        };
        initViewGroup();
        initFromAttributes(context, attributeSet);
    }

    private boolean debugDraw() {
        return DEBUG_DRAW || (this.mAttachInfo != null && this.mAttachInfo.mDebugLayout);
    }

    private void initViewGroup() {
        if (!debugDraw()) {
            setFlags(128, 128);
        }
        int i = this.mGroupFlags | 1;
        this.mGroupFlags = i;
        int i2 = i | 2;
        this.mGroupFlags = i2;
        int i3 = i2 | 16;
        this.mGroupFlags = i3;
        int i4 = i3 | 64;
        this.mGroupFlags = i4;
        this.mGroupFlags = i4 | 16384;
        if (this.mContext.getApplicationInfo().targetSdkVersion >= 11) {
            this.mGroupFlags |= 2097152;
        }
        setDescendantFocusability(131072);
        this.mChildren = new View[12];
        this.mChildrenCount = 0;
        this.mPersistentDrawingCache = 2;
    }

    private void initFromAttributes(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ViewGroup);
        int indexCount = typedArrayObtainStyledAttributes.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int index = typedArrayObtainStyledAttributes.getIndex(i);
            switch (index) {
                case 0:
                    setClipChildren(typedArrayObtainStyledAttributes.getBoolean(index, true));
                    break;
                case 1:
                    setClipToPadding(typedArrayObtainStyledAttributes.getBoolean(index, true));
                    break;
                case 2:
                    int resourceId = typedArrayObtainStyledAttributes.getResourceId(index, -1);
                    if (resourceId > 0) {
                        setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this.mContext, resourceId));
                    }
                    break;
                case 3:
                    setAnimationCacheEnabled(typedArrayObtainStyledAttributes.getBoolean(index, true));
                    break;
                case 4:
                    setPersistentDrawingCache(typedArrayObtainStyledAttributes.getInt(index, 2));
                    break;
                case 5:
                    setAlwaysDrawnWithCacheEnabled(typedArrayObtainStyledAttributes.getBoolean(index, true));
                    break;
                case 6:
                    setAddStatesFromChildren(typedArrayObtainStyledAttributes.getBoolean(index, false));
                    break;
                case 7:
                    setDescendantFocusability(DESCENDANT_FOCUSABILITY_FLAGS[typedArrayObtainStyledAttributes.getInt(index, 0)]);
                    break;
                case 8:
                    setMotionEventSplittingEnabled(typedArrayObtainStyledAttributes.getBoolean(index, false));
                    break;
                case 9:
                    if (typedArrayObtainStyledAttributes.getBoolean(index, false)) {
                        setLayoutTransition(new LayoutTransition());
                    }
                    break;
                case 10:
                    setLayoutMode(typedArrayObtainStyledAttributes.getInt(index, -1));
                    break;
            }
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    @ViewDebug.ExportedProperty(category = "focus", mapping = {@ViewDebug.IntToString(from = 131072, to = "FOCUS_BEFORE_DESCENDANTS"), @ViewDebug.IntToString(from = 262144, to = "FOCUS_AFTER_DESCENDANTS"), @ViewDebug.IntToString(from = 393216, to = "FOCUS_BLOCK_DESCENDANTS")})
    public int getDescendantFocusability() {
        return this.mGroupFlags & 393216;
    }

    public void setDescendantFocusability(int i) {
        if (i != 131072 && i != 262144 && i != 393216) {
            throw new IllegalArgumentException("must be one of FOCUS_BEFORE_DESCENDANTS, FOCUS_AFTER_DESCENDANTS, FOCUS_BLOCK_DESCENDANTS");
        }
        int i2 = this.mGroupFlags & (-393217);
        this.mGroupFlags = i2;
        this.mGroupFlags = (i & 393216) | i2;
    }

    @Override // android.view.View
    void handleFocusGainInternal(int i, Rect rect) {
        View view = this.mFocused;
        if (view != null) {
            view.unFocus();
            this.mFocused = null;
        }
        super.handleFocusGainInternal(i, rect);
    }

    @Override // android.view.ViewParent
    public void requestChildFocus(View view, View view2) {
        if (getDescendantFocusability() == 393216) {
            return;
        }
        super.unFocus();
        View view3 = this.mFocused;
        if (view3 != view) {
            if (view3 != null) {
                view3.unFocus();
            }
            this.mFocused = view;
        }
        if (this.mParent != null) {
            this.mParent.requestChildFocus(this, view2);
        }
    }

    @Override // android.view.ViewParent
    public void focusableViewAvailable(View view) {
        if (this.mParent == null || getDescendantFocusability() == 393216) {
            return;
        }
        if (!isFocused() || getDescendantFocusability() == 262144) {
            this.mParent.focusableViewAvailable(view);
        }
    }

    @Override // android.view.ViewParent
    public boolean showContextMenuForChild(View view) {
        return this.mParent != null && this.mParent.showContextMenuForChild(view);
    }

    public ActionMode startActionModeForChild(View view, ActionMode.Callback callback) {
        if (this.mParent != null) {
            return this.mParent.startActionModeForChild(view, callback);
        }
        return null;
    }

    @Override // android.view.ViewParent
    public View focusSearch(View view, int i) {
        if (isRootNamespace()) {
            return FocusFinder.getInstance().findNextFocus(this, view, i);
        }
        if (this.mParent != null) {
            return this.mParent.focusSearch(view, i);
        }
        return null;
    }

    @Override // android.view.ViewParent
    public boolean requestSendAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        ViewParent viewParent = this.mParent;
        if (viewParent != null && onRequestSendAccessibilityEvent(view, accessibilityEvent)) {
            return viewParent.requestSendAccessibilityEvent(this, accessibilityEvent);
        }
        return false;
    }

    public boolean onRequestSendAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        if (this.mAccessibilityDelegate != null) {
            return this.mAccessibilityDelegate.onRequestSendAccessibilityEvent(this, view, accessibilityEvent);
        }
        return onRequestSendAccessibilityEventInternal(view, accessibilityEvent);
    }

    @Override // android.view.ViewParent
    public void childHasTransientStateChanged(View view, boolean z) {
        boolean zHasTransientState = hasTransientState();
        if (z) {
            this.mChildCountWithTransientState++;
        } else {
            this.mChildCountWithTransientState--;
        }
        boolean zHasTransientState2 = hasTransientState();
        if (this.mParent == null || zHasTransientState == zHasTransientState2) {
            return;
        }
        try {
            this.mParent.childHasTransientStateChanged(this, zHasTransientState2);
        } catch (AbstractMethodError e) {
            Log.e(TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
        }
    }

    @Override // android.view.View
    public boolean hasTransientState() {
        return this.mChildCountWithTransientState > 0 || super.hasTransientState();
    }

    @Override // android.view.View
    public boolean dispatchUnhandledMove(View view, int i) {
        View view2 = this.mFocused;
        return view2 != null && view2.dispatchUnhandledMove(view, i);
    }

    @Override // android.view.ViewParent
    public void clearChildFocus(View view) {
        this.mFocused = null;
        if (this.mParent != null) {
            this.mParent.clearChildFocus(this);
        }
    }

    @Override // android.view.View
    public void clearFocus() {
        View view = this.mFocused;
        if (view == null) {
            super.clearFocus();
        } else {
            this.mFocused = null;
            view.clearFocus();
        }
    }

    @Override // android.view.View
    void unFocus() {
        View view = this.mFocused;
        if (view == null) {
            super.unFocus();
        } else {
            view.unFocus();
            this.mFocused = null;
        }
    }

    public View getFocusedChild() {
        return this.mFocused;
    }

    @Override // android.view.View
    public boolean hasFocus() {
        return ((this.mPrivateFlags & 2) == 0 && this.mFocused == null) ? false : true;
    }

    @Override // android.view.View
    public View findFocus() {
        if (isFocused()) {
            return this;
        }
        View view = this.mFocused;
        if (view != null) {
            return view.findFocus();
        }
        return null;
    }

    @Override // android.view.View
    public boolean hasFocusable() {
        if ((this.mViewFlags & 12) != 0) {
            return false;
        }
        if (isFocusable()) {
            return true;
        }
        if (getDescendantFocusability() != 393216) {
            int i = this.mChildrenCount;
            View[] viewArr = this.mChildren;
            for (int i2 = 0; i2 < i; i2++) {
                if (viewArr[i2].hasFocusable()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override // android.view.View
    public void addFocusables(ArrayList<View> arrayList, int i, int i2) {
        int size = arrayList.size();
        int descendantFocusability = getDescendantFocusability();
        if (descendantFocusability != 393216) {
            int i3 = this.mChildrenCount;
            View[] viewArr = this.mChildren;
            for (int i4 = 0; i4 < i3; i4++) {
                View view = viewArr[i4];
                if ((view.mViewFlags & 12) == 0) {
                    view.addFocusables(arrayList, i, i2);
                }
            }
        }
        if (descendantFocusability != 262144 || size == arrayList.size()) {
            super.addFocusables(arrayList, i, i2);
        }
    }

    @Override // android.view.View
    public void findViewsWithText(ArrayList<View> arrayList, CharSequence charSequence, int i) {
        super.findViewsWithText(arrayList, charSequence, i);
        int i2 = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i3 = 0; i3 < i2; i3++) {
            View view = viewArr[i3];
            if ((view.mViewFlags & 12) == 0 && (view.mPrivateFlags & 8) == 0) {
                view.findViewsWithText(arrayList, charSequence, i);
            }
        }
    }

    @Override // android.view.View
    public View findViewByAccessibilityIdTraversal(int i) {
        View viewFindViewByAccessibilityIdTraversal = super.findViewByAccessibilityIdTraversal(i);
        if (viewFindViewByAccessibilityIdTraversal != null) {
            return viewFindViewByAccessibilityIdTraversal;
        }
        int i2 = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i3 = 0; i3 < i2; i3++) {
            View viewFindViewByAccessibilityIdTraversal2 = viewArr[i3].findViewByAccessibilityIdTraversal(i);
            if (viewFindViewByAccessibilityIdTraversal2 != null) {
                return viewFindViewByAccessibilityIdTraversal2;
            }
        }
        return null;
    }

    @Override // android.view.View
    public void dispatchWindowFocusChanged(boolean z) {
        super.dispatchWindowFocusChanged(z);
        int i = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i2 = 0; i2 < i; i2++) {
            viewArr[i2].dispatchWindowFocusChanged(z);
        }
    }

    @Override // android.view.View
    public void addTouchables(ArrayList<View> arrayList) {
        super.addTouchables(arrayList);
        int i = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i2 = 0; i2 < i; i2++) {
            View view = viewArr[i2];
            if ((view.mViewFlags & 12) == 0) {
                view.addTouchables(arrayList);
            }
        }
    }

    @Override // android.view.View
    public void makeOptionalFitsSystemWindows() {
        super.makeOptionalFitsSystemWindows();
        int i = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i2 = 0; i2 < i; i2++) {
            viewArr[i2].makeOptionalFitsSystemWindows();
        }
    }

    @Override // android.view.View
    public void dispatchDisplayHint(int i) {
        super.dispatchDisplayHint(i);
        int i2 = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i3 = 0; i3 < i2; i3++) {
            viewArr[i3].dispatchDisplayHint(i);
        }
    }

    protected void onChildVisibilityChanged(View view, int i, int i2) {
        LayoutTransition layoutTransition = this.mTransition;
        if (layoutTransition != null) {
            if (i2 == 0) {
                layoutTransition.showChild(this, view, i);
            } else {
                layoutTransition.hideChild(this, view, i2);
                ArrayList<View> arrayList = this.mTransitioningViews;
                if (arrayList != null && arrayList.contains(view)) {
                    if (this.mVisibilityChangingChildren == null) {
                        this.mVisibilityChangingChildren = new ArrayList<>();
                    }
                    this.mVisibilityChangingChildren.add(view);
                    addDisappearingView(view);
                }
            }
        }
        if (this.mCurrentDrag == null || i2 != 0) {
            return;
        }
        notifyChildOfDrag(view);
    }

    @Override // android.view.View
    protected void dispatchVisibilityChanged(View view, int i) {
        super.dispatchVisibilityChanged(view, i);
        int i2 = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i3 = 0; i3 < i2; i3++) {
            viewArr[i3].dispatchVisibilityChanged(view, i);
        }
    }

    @Override // android.view.View
    public void dispatchWindowVisibilityChanged(int i) {
        super.dispatchWindowVisibilityChanged(i);
        int i2 = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i3 = 0; i3 < i2; i3++) {
            viewArr[i3].dispatchWindowVisibilityChanged(i);
        }
    }

    @Override // android.view.View
    public void dispatchConfigurationChanged(Configuration configuration) {
        super.dispatchConfigurationChanged(configuration);
        int i = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i2 = 0; i2 < i; i2++) {
            viewArr[i2].dispatchConfigurationChanged(configuration);
        }
    }

    @Override // android.view.ViewParent
    public void recomputeViewAttributes(View view) {
        ViewParent viewParent;
        if (this.mAttachInfo == null || this.mAttachInfo.mRecomputeGlobalAttributes || (viewParent = this.mParent) == null) {
            return;
        }
        viewParent.recomputeViewAttributes(this);
    }

    @Override // android.view.View
    void dispatchCollectViewAttributes(View.AttachInfo attachInfo, int i) {
        if ((i & 12) == 0) {
            super.dispatchCollectViewAttributes(attachInfo, i);
            int i2 = this.mChildrenCount;
            View[] viewArr = this.mChildren;
            for (int i3 = 0; i3 < i2; i3++) {
                View view = viewArr[i3];
                view.dispatchCollectViewAttributes(attachInfo, (view.mViewFlags & 12) | i);
            }
        }
    }

    @Override // android.view.ViewParent
    public void bringChildToFront(View view) {
        int iIndexOfChild = indexOfChild(view);
        if (iIndexOfChild >= 0) {
            removeFromArray(iIndexOfChild);
            addInArray(view, this.mChildrenCount);
            view.mParent = this;
            requestLayout();
            invalidate();
        }
    }

    private PointF getLocalPoint() {
        if (this.mLocalPoint == null) {
            this.mLocalPoint = new PointF();
        }
        return this.mLocalPoint;
    }

    @Override // android.view.View
    public boolean dispatchDragEvent(DragEvent dragEvent) {
        View view;
        float f = dragEvent.mX;
        float f2 = dragEvent.mY;
        ViewRootImpl viewRootImpl = getViewRootImpl();
        PointF localPoint = getLocalPoint();
        int i = dragEvent.mAction;
        zDispatchDragEvent = false;
        zDispatchDragEvent = false;
        zDispatchDragEvent = false;
        zDispatchDragEvent = false;
        zDispatchDragEvent = false;
        boolean zDispatchDragEvent = false;
        if (i == 1) {
            this.mCurrentDragView = null;
            this.mCurrentDrag = DragEvent.obtain(dragEvent);
            HashSet<View> hashSet = this.mDragNotifiedChildren;
            if (hashSet == null) {
                this.mDragNotifiedChildren = new HashSet<>();
            } else {
                hashSet.clear();
            }
            this.mChildAcceptsDrag = false;
            int i2 = this.mChildrenCount;
            View[] viewArr = this.mChildren;
            for (int i3 = 0; i3 < i2; i3++) {
                View view2 = viewArr[i3];
                view2.mPrivateFlags2 &= -4;
                if (view2.getVisibility() == 0 && notifyChildOfDrag(viewArr[i3])) {
                    this.mChildAcceptsDrag = true;
                }
            }
            zDispatchDragEvent = this.mChildAcceptsDrag;
        } else if (i == 2) {
            View viewFindFrontmostDroppableChildAt = findFrontmostDroppableChildAt(dragEvent.mX, dragEvent.mY, localPoint);
            if (this.mCurrentDragView != viewFindFrontmostDroppableChildAt) {
                viewRootImpl.setDragFocus(viewFindFrontmostDroppableChildAt);
                int i4 = dragEvent.mAction;
                View view3 = this.mCurrentDragView;
                if (view3 != null) {
                    dragEvent.mAction = 6;
                    view3.dispatchDragEvent(dragEvent);
                    view3.mPrivateFlags2 &= -3;
                    view3.refreshDrawableState();
                }
                this.mCurrentDragView = viewFindFrontmostDroppableChildAt;
                if (viewFindFrontmostDroppableChildAt != null) {
                    dragEvent.mAction = 5;
                    viewFindFrontmostDroppableChildAt.dispatchDragEvent(dragEvent);
                    viewFindFrontmostDroppableChildAt.mPrivateFlags2 |= 2;
                    viewFindFrontmostDroppableChildAt.refreshDrawableState();
                }
                dragEvent.mAction = i4;
            }
            if (viewFindFrontmostDroppableChildAt != null) {
                dragEvent.mX = localPoint.x;
                dragEvent.mY = localPoint.y;
                zDispatchDragEvent = viewFindFrontmostDroppableChildAt.dispatchDragEvent(dragEvent);
                dragEvent.mX = f;
                dragEvent.mY = f2;
            }
        } else if (i == 3) {
            View viewFindFrontmostDroppableChildAt2 = findFrontmostDroppableChildAt(dragEvent.mX, dragEvent.mY, localPoint);
            if (viewFindFrontmostDroppableChildAt2 != null) {
                dragEvent.mX = localPoint.x;
                dragEvent.mY = localPoint.y;
                zDispatchDragEvent = viewFindFrontmostDroppableChildAt2.dispatchDragEvent(dragEvent);
                dragEvent.mX = f;
                dragEvent.mY = f2;
            }
        } else if (i == 4) {
            HashSet<View> hashSet2 = this.mDragNotifiedChildren;
            if (hashSet2 != null) {
                for (View view4 : hashSet2) {
                    view4.dispatchDragEvent(dragEvent);
                    view4.mPrivateFlags2 &= -4;
                    view4.refreshDrawableState();
                }
                this.mDragNotifiedChildren.clear();
                DragEvent dragEvent2 = this.mCurrentDrag;
                if (dragEvent2 != null) {
                    dragEvent2.recycle();
                    this.mCurrentDrag = null;
                }
            }
            if (this.mChildAcceptsDrag) {
                zDispatchDragEvent = true;
            }
        } else if (i == 6 && (view = this.mCurrentDragView) != null) {
            view.dispatchDragEvent(dragEvent);
            view.mPrivateFlags2 &= -3;
            view.refreshDrawableState();
            this.mCurrentDragView = null;
        }
        return !zDispatchDragEvent ? super.dispatchDragEvent(dragEvent) : zDispatchDragEvent;
    }

    View findFrontmostDroppableChildAt(float f, float f2, PointF pointF) {
        int i = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i2 = i - 1; i2 >= 0; i2--) {
            View view = viewArr[i2];
            if (view.canAcceptDrag() && isTransformedTouchPointInView(f, f2, view, pointF)) {
                return view;
            }
        }
        return null;
    }

    boolean notifyChildOfDrag(View view) {
        if (this.mDragNotifiedChildren.contains(view)) {
            return false;
        }
        this.mDragNotifiedChildren.add(view);
        boolean zDispatchDragEvent = view.dispatchDragEvent(this.mCurrentDrag);
        if (!zDispatchDragEvent || view.canAcceptDrag()) {
            return zDispatchDragEvent;
        }
        view.mPrivateFlags2 |= 1;
        view.refreshDrawableState();
        return zDispatchDragEvent;
    }

    @Override // android.view.View
    public void dispatchWindowSystemUiVisiblityChanged(int i) {
        super.dispatchWindowSystemUiVisiblityChanged(i);
        int i2 = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i3 = 0; i3 < i2; i3++) {
            viewArr[i3].dispatchWindowSystemUiVisiblityChanged(i);
        }
    }

    @Override // android.view.View
    public void dispatchSystemUiVisibilityChanged(int i) {
        super.dispatchSystemUiVisibilityChanged(i);
        int i2 = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i3 = 0; i3 < i2; i3++) {
            viewArr[i3].dispatchSystemUiVisibilityChanged(i);
        }
    }

    @Override // android.view.View
    boolean updateLocalSystemUiVisibility(int i, int i2) {
        boolean zUpdateLocalSystemUiVisibility = super.updateLocalSystemUiVisibility(i, i2);
        int i3 = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i4 = 0; i4 < i3; i4++) {
            zUpdateLocalSystemUiVisibility |= viewArr[i4].updateLocalSystemUiVisibility(i, i2);
        }
        return zUpdateLocalSystemUiVisibility;
    }

    @Override // android.view.View
    public boolean dispatchKeyEventPreIme(KeyEvent keyEvent) {
        if ((this.mPrivateFlags & 18) == 18) {
            return super.dispatchKeyEventPreIme(keyEvent);
        }
        View view = this.mFocused;
        if (view == null || (view.mPrivateFlags & 16) != 16) {
            return false;
        }
        return this.mFocused.dispatchKeyEventPreIme(keyEvent);
    }

    @Override // android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onKeyEvent(keyEvent, 1);
        }
        if ((this.mPrivateFlags & 18) == 18) {
            if (super.dispatchKeyEvent(keyEvent)) {
                return true;
            }
        } else {
            View view = this.mFocused;
            if (view != null && (view.mPrivateFlags & 16) == 16 && this.mFocused.dispatchKeyEvent(keyEvent)) {
                return true;
            }
        }
        if (this.mInputEventConsistencyVerifier == null) {
            return false;
        }
        this.mInputEventConsistencyVerifier.onUnhandledEvent(keyEvent, 1);
        return false;
    }

    @Override // android.view.View
    public boolean dispatchKeyShortcutEvent(KeyEvent keyEvent) {
        if ((this.mPrivateFlags & 18) == 18) {
            return super.dispatchKeyShortcutEvent(keyEvent);
        }
        View view = this.mFocused;
        if (view == null || (view.mPrivateFlags & 16) != 16) {
            return false;
        }
        return this.mFocused.dispatchKeyShortcutEvent(keyEvent);
    }

    @Override // android.view.View
    public boolean dispatchTrackballEvent(MotionEvent motionEvent) {
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onTrackballEvent(motionEvent, 1);
        }
        if ((this.mPrivateFlags & 18) == 18) {
            if (super.dispatchTrackballEvent(motionEvent)) {
                return true;
            }
        } else {
            View view = this.mFocused;
            if (view != null && (view.mPrivateFlags & 16) == 16 && this.mFocused.dispatchTrackballEvent(motionEvent)) {
                return true;
            }
        }
        if (this.mInputEventConsistencyVerifier == null) {
            return false;
        }
        this.mInputEventConsistencyVerifier.onUnhandledEvent(motionEvent, 1);
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:41:0x00a5  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x00b1  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0147  */
    /* JADX WARN: Removed duplicated region for block: B:80:0x00b5 A[EDGE_INSN: B:80:0x00b5->B:89:? BREAK  A[LOOP:0: B:8:0x0033->B:43:0x00ac], SYNTHETIC] */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected boolean dispatchHoverEvent(android.view.MotionEvent r20) {
        /*
            Method dump skipped, instruction units count: 331
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.ViewGroup.dispatchHoverEvent(android.view.MotionEvent):boolean");
    }

    private void exitHoverTargets() {
        if (this.mHoveredSelf || this.mFirstHoverTarget != null) {
            long jUptimeMillis = SystemClock.uptimeMillis();
            MotionEvent motionEventObtain = MotionEvent.obtain(jUptimeMillis, jUptimeMillis, 10, 0.0f, 0.0f, 0);
            motionEventObtain.setSource(4098);
            dispatchHoverEvent(motionEventObtain);
            motionEventObtain.recycle();
        }
    }

    private void cancelHoverTarget(View view) {
        HoverTarget hoverTarget = this.mFirstHoverTarget;
        HoverTarget hoverTarget2 = null;
        while (hoverTarget != null) {
            HoverTarget hoverTarget3 = hoverTarget.next;
            if (hoverTarget.child == view) {
                if (hoverTarget2 == null) {
                    this.mFirstHoverTarget = hoverTarget3;
                } else {
                    hoverTarget2.next = hoverTarget3;
                }
                hoverTarget.recycle();
                long jUptimeMillis = SystemClock.uptimeMillis();
                MotionEvent motionEventObtain = MotionEvent.obtain(jUptimeMillis, jUptimeMillis, 10, 0.0f, 0.0f, 0);
                motionEventObtain.setSource(4098);
                view.dispatchHoverEvent(motionEventObtain);
                motionEventObtain.recycle();
                return;
            }
            hoverTarget2 = hoverTarget;
            hoverTarget = hoverTarget3;
        }
    }

    @Override // android.view.View
    protected boolean hasHoveredChild() {
        return this.mFirstHoverTarget != null;
    }

    @Override // android.view.View
    public void addChildrenForAccessibility(ArrayList<View> arrayList) {
        ChildListForAccessibility childListForAccessibilityObtain = ChildListForAccessibility.obtain(this, true);
        try {
            int childCount = childListForAccessibilityObtain.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = childListForAccessibilityObtain.getChildAt(i);
                if ((childAt.mViewFlags & 12) == 0) {
                    if (childAt.includeForAccessibility()) {
                        arrayList.add(childAt);
                    } else {
                        childAt.addChildrenForAccessibility(arrayList);
                    }
                }
            }
        } finally {
            childListForAccessibilityObtain.recycle();
        }
    }

    private static MotionEvent obtainMotionEventNoHistoryOrSelf(MotionEvent motionEvent) {
        return motionEvent.getHistorySize() == 0 ? motionEvent : MotionEvent.obtainNoHistory(motionEvent);
    }

    @Override // android.view.View
    protected boolean dispatchGenericPointerEvent(MotionEvent motionEvent) {
        int i = this.mChildrenCount;
        if (i != 0) {
            View[] viewArr = this.mChildren;
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            boolean zIsChildrenDrawingOrderEnabled = isChildrenDrawingOrderEnabled();
            for (int i2 = i - 1; i2 >= 0; i2--) {
                View view = viewArr[zIsChildrenDrawingOrderEnabled ? getChildDrawingOrder(i, i2) : i2];
                if (canViewReceivePointerEvents(view) && isTransformedTouchPointInView(x, y, view, null) && dispatchTransformedGenericPointerEvent(motionEvent, view)) {
                    return true;
                }
            }
        }
        return super.dispatchGenericPointerEvent(motionEvent);
    }

    @Override // android.view.View
    protected boolean dispatchGenericFocusedEvent(MotionEvent motionEvent) {
        if ((this.mPrivateFlags & 18) == 18) {
            return super.dispatchGenericFocusedEvent(motionEvent);
        }
        View view = this.mFocused;
        if (view == null || (view.mPrivateFlags & 16) != 16) {
            return false;
        }
        return this.mFocused.dispatchGenericMotionEvent(motionEvent);
    }

    private boolean dispatchTransformedGenericPointerEvent(MotionEvent motionEvent, View view) {
        float f = this.mScrollX - view.mLeft;
        float f2 = this.mScrollY - view.mTop;
        if (!view.hasIdentityMatrix()) {
            MotionEvent motionEventObtain = MotionEvent.obtain(motionEvent);
            motionEventObtain.offsetLocation(f, f2);
            motionEventObtain.transform(view.getInverseMatrix());
            boolean zDispatchGenericMotionEvent = view.dispatchGenericMotionEvent(motionEventObtain);
            motionEventObtain.recycle();
            return zDispatchGenericMotionEvent;
        }
        motionEvent.offsetLocation(f, f2);
        boolean zDispatchGenericMotionEvent2 = view.dispatchGenericMotionEvent(motionEvent);
        motionEvent.offsetLocation(-f, -f2);
        return zDispatchGenericMotionEvent2;
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        boolean zOnInterceptTouchEvent;
        boolean z;
        TouchTarget touchTarget;
        TouchTarget touchTargetAddTouchTarget;
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onTouchEvent(motionEvent, 1);
        }
        boolean zDispatchTransformedTouchEvent = false;
        if (onFilterTouchEventForSecurity(motionEvent)) {
            int action = motionEvent.getAction();
            int i = action & 255;
            if (i == 0) {
                cancelAndClearTouchTargets(motionEvent);
                resetTouchState();
            }
            if (i == 0 || this.mFirstTouchTarget != null) {
                if ((this.mGroupFlags & 524288) != 0) {
                    zOnInterceptTouchEvent = false;
                } else {
                    zOnInterceptTouchEvent = onInterceptTouchEvent(motionEvent);
                    motionEvent.setAction(action);
                }
            } else {
                zOnInterceptTouchEvent = true;
            }
            boolean z2 = resetCancelNextUpFlag(this) || i == 3;
            boolean z3 = (this.mGroupFlags & 2097152) != 0;
            PointF pointF = null;
            if (z2 || zOnInterceptTouchEvent || !(i == 0 || ((z3 && i == 5) || i == 7))) {
                z = false;
                touchTarget = null;
            } else {
                int actionIndex = motionEvent.getActionIndex();
                int pointerId = z3 ? 1 << motionEvent.getPointerId(actionIndex) : -1;
                removePointersFromTouchTargets(pointerId);
                int i2 = this.mChildrenCount;
                if (i2 != 0) {
                    float x = motionEvent.getX(actionIndex);
                    float y = motionEvent.getY(actionIndex);
                    View[] viewArr = this.mChildren;
                    boolean zIsChildrenDrawingOrderEnabled = isChildrenDrawingOrderEnabled();
                    int i3 = i2 - 1;
                    touchTargetAddTouchTarget = null;
                    while (true) {
                        if (i3 < 0) {
                            z = false;
                            break;
                        }
                        int childDrawingOrder = zIsChildrenDrawingOrderEnabled ? getChildDrawingOrder(i2, i3) : i3;
                        View view = viewArr[childDrawingOrder];
                        if (canViewReceivePointerEvents(view) && isTransformedTouchPointInView(x, y, view, pointF)) {
                            TouchTarget touchTarget2 = getTouchTarget(view);
                            if (touchTarget2 != null) {
                                touchTarget2.pointerIdBits |= pointerId;
                                z = false;
                                touchTargetAddTouchTarget = touchTarget2;
                                break;
                            }
                            resetCancelNextUpFlag(view);
                            if (dispatchTransformedTouchEvent(motionEvent, false, view, pointerId)) {
                                this.mLastTouchDownTime = motionEvent.getDownTime();
                                this.mLastTouchDownIndex = childDrawingOrder;
                                this.mLastTouchDownX = motionEvent.getX();
                                this.mLastTouchDownY = motionEvent.getY();
                                touchTargetAddTouchTarget = addTouchTarget(view, pointerId);
                                z = true;
                                break;
                            }
                            touchTargetAddTouchTarget = touchTarget2;
                        }
                        i3--;
                        pointF = null;
                    }
                } else {
                    z = false;
                    touchTargetAddTouchTarget = null;
                }
                if (touchTargetAddTouchTarget != null || (touchTarget = this.mFirstTouchTarget) == null) {
                    touchTarget = touchTargetAddTouchTarget;
                } else {
                    while (touchTarget.next != null) {
                        touchTarget = touchTarget.next;
                    }
                    touchTarget.pointerIdBits |= pointerId;
                }
            }
            TouchTarget touchTarget3 = this.mFirstTouchTarget;
            if (touchTarget3 == null) {
                zDispatchTransformedTouchEvent = dispatchTransformedTouchEvent(motionEvent, z2, null, -1);
            } else {
                TouchTarget touchTarget4 = null;
                TouchTarget touchTarget5 = touchTarget3;
                boolean z4 = false;
                while (touchTarget5 != null) {
                    TouchTarget touchTarget6 = touchTarget5.next;
                    if (z && touchTarget5 == touchTarget) {
                        z4 = true;
                    } else {
                        boolean z5 = resetCancelNextUpFlag(touchTarget5.child) || zOnInterceptTouchEvent;
                        if (dispatchTransformedTouchEvent(motionEvent, z5, touchTarget5.child, touchTarget5.pointerIdBits)) {
                            z4 = true;
                        }
                        if (z5) {
                            if (touchTarget4 == null) {
                                this.mFirstTouchTarget = touchTarget6;
                            } else {
                                touchTarget4.next = touchTarget6;
                            }
                            touchTarget5.recycle();
                        }
                        touchTarget5 = touchTarget6;
                    }
                    touchTarget4 = touchTarget5;
                    touchTarget5 = touchTarget6;
                }
                zDispatchTransformedTouchEvent = z4;
            }
            if (z2 || i == 1 || i == 7) {
                resetTouchState();
            } else if (z3 && i == 6) {
                removePointersFromTouchTargets(1 << motionEvent.getPointerId(motionEvent.getActionIndex()));
            }
        }
        if (!zDispatchTransformedTouchEvent && this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onUnhandledEvent(motionEvent, 1);
        }
        return zDispatchTransformedTouchEvent;
    }

    private void resetTouchState() {
        clearTouchTargets();
        resetCancelNextUpFlag(this);
        this.mGroupFlags &= -524289;
    }

    private static boolean resetCancelNextUpFlag(View view) {
        if ((view.mPrivateFlags & 67108864) == 0) {
            return false;
        }
        view.mPrivateFlags &= -67108865;
        return true;
    }

    private void clearTouchTargets() {
        TouchTarget touchTarget = this.mFirstTouchTarget;
        if (touchTarget == null) {
            return;
        }
        while (true) {
            TouchTarget touchTarget2 = touchTarget.next;
            touchTarget.recycle();
            if (touchTarget2 == null) {
                this.mFirstTouchTarget = null;
                return;
            }
            touchTarget = touchTarget2;
        }
    }

    private void cancelAndClearTouchTargets(MotionEvent motionEvent) {
        if (this.mFirstTouchTarget != null) {
            boolean z = false;
            if (motionEvent == null) {
                long jUptimeMillis = SystemClock.uptimeMillis();
                motionEvent = MotionEvent.obtain(jUptimeMillis, jUptimeMillis, 3, 0.0f, 0.0f, 0);
                motionEvent.setSource(4098);
                z = true;
            }
            for (TouchTarget touchTarget = this.mFirstTouchTarget; touchTarget != null; touchTarget = touchTarget.next) {
                resetCancelNextUpFlag(touchTarget.child);
                dispatchTransformedTouchEvent(motionEvent, true, touchTarget.child, touchTarget.pointerIdBits);
            }
            clearTouchTargets();
            if (z) {
                motionEvent.recycle();
            }
        }
    }

    private TouchTarget getTouchTarget(View view) {
        for (TouchTarget touchTarget = this.mFirstTouchTarget; touchTarget != null; touchTarget = touchTarget.next) {
            if (touchTarget.child == view) {
                return touchTarget;
            }
        }
        return null;
    }

    private TouchTarget addTouchTarget(View view, int i) {
        TouchTarget touchTargetObtain = TouchTarget.obtain(view, i);
        touchTargetObtain.next = this.mFirstTouchTarget;
        this.mFirstTouchTarget = touchTargetObtain;
        return touchTargetObtain;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0021  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void removePointersFromTouchTargets(int r6) {
        /*
            r5 = this;
            android.view.ViewGroup$TouchTarget r0 = r5.mFirstTouchTarget
            r1 = 0
        L3:
            if (r0 == 0) goto L24
            android.view.ViewGroup$TouchTarget r2 = r0.next
            int r3 = r0.pointerIdBits
            r3 = r3 & r6
            if (r3 == 0) goto L21
            int r3 = r0.pointerIdBits
            int r4 = ~r6
            r3 = r3 & r4
            r0.pointerIdBits = r3
            int r3 = r0.pointerIdBits
            if (r3 != 0) goto L21
            if (r1 != 0) goto L1b
            r5.mFirstTouchTarget = r2
            goto L1d
        L1b:
            r1.next = r2
        L1d:
            r0.recycle()
            goto L22
        L21:
            r1 = r0
        L22:
            r0 = r2
            goto L3
        L24:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.ViewGroup.removePointersFromTouchTargets(int):void");
    }

    private void cancelTouchTarget(View view) {
        TouchTarget touchTarget = this.mFirstTouchTarget;
        TouchTarget touchTarget2 = null;
        while (touchTarget != null) {
            TouchTarget touchTarget3 = touchTarget.next;
            if (touchTarget.child == view) {
                if (touchTarget2 == null) {
                    this.mFirstTouchTarget = touchTarget3;
                } else {
                    touchTarget2.next = touchTarget3;
                }
                touchTarget.recycle();
                long jUptimeMillis = SystemClock.uptimeMillis();
                MotionEvent motionEventObtain = MotionEvent.obtain(jUptimeMillis, jUptimeMillis, 3, 0.0f, 0.0f, 0);
                motionEventObtain.setSource(4098);
                view.dispatchTouchEvent(motionEventObtain);
                motionEventObtain.recycle();
                return;
            }
            touchTarget2 = touchTarget;
            touchTarget = touchTarget3;
        }
    }

    private static boolean canViewReceivePointerEvents(View view) {
        return (view.mViewFlags & 12) == 0 || view.getAnimation() != null;
    }

    protected boolean isTransformedTouchPointInView(float f, float f2, View view, PointF pointF) {
        float f3 = (f + this.mScrollX) - view.mLeft;
        float f4 = (f2 + this.mScrollY) - view.mTop;
        if (!view.hasIdentityMatrix() && this.mAttachInfo != null) {
            float[] fArr = this.mAttachInfo.mTmpTransformLocation;
            fArr[0] = f3;
            fArr[1] = f4;
            view.getInverseMatrix().mapPoints(fArr);
            float f5 = fArr[0];
            f4 = fArr[1];
            f3 = f5;
        }
        boolean zPointInView = view.pointInView(f3, f4);
        if (zPointInView && pointF != null) {
            pointF.set(f3, f4);
        }
        return zPointInView;
    }

    private boolean dispatchTransformedTouchEvent(MotionEvent motionEvent, boolean z, View view, int i) {
        boolean zDispatchTouchEvent;
        MotionEvent motionEventSplit;
        boolean zDispatchTouchEvent2;
        int action = motionEvent.getAction();
        if (z || action == 3) {
            motionEvent.setAction(3);
            if (view == null) {
                zDispatchTouchEvent = super.dispatchTouchEvent(motionEvent);
            } else {
                zDispatchTouchEvent = view.dispatchTouchEvent(motionEvent);
            }
            motionEvent.setAction(action);
            return zDispatchTouchEvent;
        }
        int pointerIdBits = motionEvent.getPointerIdBits();
        int i2 = i & pointerIdBits;
        if (i2 == 0) {
            return false;
        }
        if (i2 == pointerIdBits) {
            if (view == null || view.hasIdentityMatrix()) {
                if (view == null) {
                    return super.dispatchTouchEvent(motionEvent);
                }
                float f = this.mScrollX - view.mLeft;
                float f2 = this.mScrollY - view.mTop;
                motionEvent.offsetLocation(f, f2);
                boolean zDispatchTouchEvent3 = view.dispatchTouchEvent(motionEvent);
                motionEvent.offsetLocation(-f, -f2);
                return zDispatchTouchEvent3;
            }
            motionEventSplit = MotionEvent.obtain(motionEvent);
        } else {
            motionEventSplit = motionEvent.split(i2);
        }
        if (view == null) {
            zDispatchTouchEvent2 = super.dispatchTouchEvent(motionEventSplit);
        } else {
            motionEventSplit.offsetLocation(this.mScrollX - view.mLeft, this.mScrollY - view.mTop);
            if (!view.hasIdentityMatrix()) {
                motionEventSplit.transform(view.getInverseMatrix());
            }
            zDispatchTouchEvent2 = view.dispatchTouchEvent(motionEventSplit);
        }
        motionEventSplit.recycle();
        return zDispatchTouchEvent2;
    }

    public void setMotionEventSplittingEnabled(boolean z) {
        if (z) {
            this.mGroupFlags |= 2097152;
        } else {
            this.mGroupFlags &= -2097153;
        }
    }

    public boolean isMotionEventSplittingEnabled() {
        return (this.mGroupFlags & 2097152) == 2097152;
    }

    @Override // android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean z) {
        int i = this.mGroupFlags;
        if (z == ((i & 524288) != 0)) {
            return;
        }
        if (z) {
            this.mGroupFlags = i | 524288;
        } else {
            this.mGroupFlags = i & (-524289);
        }
        if (this.mParent != null) {
            this.mParent.requestDisallowInterceptTouchEvent(z);
        }
    }

    @Override // android.view.View
    public boolean requestFocus(int i, Rect rect) {
        int descendantFocusability = getDescendantFocusability();
        if (descendantFocusability == 131072) {
            boolean zRequestFocus = super.requestFocus(i, rect);
            return zRequestFocus ? zRequestFocus : onRequestFocusInDescendants(i, rect);
        }
        if (descendantFocusability == 262144) {
            boolean zOnRequestFocusInDescendants = onRequestFocusInDescendants(i, rect);
            return zOnRequestFocusInDescendants ? zOnRequestFocusInDescendants : super.requestFocus(i, rect);
        }
        if (descendantFocusability == 393216) {
            return super.requestFocus(i, rect);
        }
        throw new IllegalStateException("descendant focusability must be one of FOCUS_BEFORE_DESCENDANTS, FOCUS_AFTER_DESCENDANTS, FOCUS_BLOCK_DESCENDANTS but is " + descendantFocusability);
    }

    protected boolean onRequestFocusInDescendants(int i, Rect rect) {
        int i2;
        int i3;
        int i4 = this.mChildrenCount;
        int i5 = -1;
        if ((i & 2) != 0) {
            i5 = i4;
            i2 = 0;
            i3 = 1;
        } else {
            i2 = i4 - 1;
            i3 = -1;
        }
        View[] viewArr = this.mChildren;
        while (i2 != i5) {
            View view = viewArr[i2];
            if ((view.mViewFlags & 12) == 0 && view.requestFocus(i, rect)) {
                return true;
            }
            i2 += i3;
        }
        return false;
    }

    @Override // android.view.View
    public void dispatchStartTemporaryDetach() {
        super.dispatchStartTemporaryDetach();
        int i = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i2 = 0; i2 < i; i2++) {
            viewArr[i2].dispatchStartTemporaryDetach();
        }
    }

    @Override // android.view.View
    public void dispatchFinishTemporaryDetach() {
        super.dispatchFinishTemporaryDetach();
        int i = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i2 = 0; i2 < i; i2++) {
            viewArr[i2].dispatchFinishTemporaryDetach();
        }
    }

    @Override // android.view.View
    void dispatchAttachedToWindow(View.AttachInfo attachInfo, int i) {
        this.mGroupFlags |= 4194304;
        super.dispatchAttachedToWindow(attachInfo, i);
        this.mGroupFlags &= -4194305;
        int i2 = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i3 = 0; i3 < i2; i3++) {
            View view = viewArr[i3];
            view.dispatchAttachedToWindow(attachInfo, (view.mViewFlags & 12) | i);
        }
    }

    @Override // android.view.View
    void dispatchScreenStateChanged(int i) {
        super.dispatchScreenStateChanged(i);
        int i2 = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i3 = 0; i3 < i2; i3++) {
            viewArr[i3].dispatchScreenStateChanged(i);
        }
    }

    @Override // android.view.View
    boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent accessibilityEvent) {
        boolean zDispatchPopulateAccessibilityEvent;
        boolean zDispatchPopulateAccessibilityEventInternal;
        if (includeForAccessibility() && (zDispatchPopulateAccessibilityEventInternal = super.dispatchPopulateAccessibilityEventInternal(accessibilityEvent))) {
            return zDispatchPopulateAccessibilityEventInternal;
        }
        ChildListForAccessibility childListForAccessibilityObtain = ChildListForAccessibility.obtain(this, true);
        try {
            int childCount = childListForAccessibilityObtain.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = childListForAccessibilityObtain.getChildAt(i);
                if ((childAt.mViewFlags & 12) == 0 && (zDispatchPopulateAccessibilityEvent = childAt.dispatchPopulateAccessibilityEvent(accessibilityEvent))) {
                    return zDispatchPopulateAccessibilityEvent;
                }
            }
            return false;
        } finally {
            childListForAccessibilityObtain.recycle();
        }
    }

    @Override // android.view.View
    void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfoInternal(accessibilityNodeInfo);
        if (this.mAttachInfo != null) {
            ArrayList<View> arrayList = this.mAttachInfo.mTempArrayList;
            arrayList.clear();
            addChildrenForAccessibility(arrayList);
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                accessibilityNodeInfo.addChild(arrayList.get(i));
            }
            arrayList.clear();
        }
    }

    @Override // android.view.View
    void onInitializeAccessibilityEventInternal(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEventInternal(accessibilityEvent);
        accessibilityEvent.setClassName(ViewGroup.class.getName());
    }

    @Override // android.view.ViewParent
    public void notifySubtreeAccessibilityStateChanged(View view, View view2, int i) {
        if (getAccessibilityLiveRegion() != 0) {
            notifyViewAccessibilityStateChangedIfNeeded(i);
        } else if (this.mParent != null) {
            try {
                this.mParent.notifySubtreeAccessibilityStateChanged(this, view2, i);
            } catch (AbstractMethodError e) {
                Log.e("View", this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
            }
        }
    }

    @Override // android.view.View
    void resetSubtreeAccessibilityStateChanged() {
        super.resetSubtreeAccessibilityStateChanged();
        View[] viewArr = this.mChildren;
        int i = this.mChildrenCount;
        for (int i2 = 0; i2 < i; i2++) {
            viewArr[i2].resetSubtreeAccessibilityStateChanged();
        }
    }

    @Override // android.view.View
    void dispatchDetachedFromWindow() {
        cancelAndClearTouchTargets(null);
        exitHoverTargets();
        this.mLayoutCalledWhileSuppressed = false;
        this.mDragNotifiedChildren = null;
        DragEvent dragEvent = this.mCurrentDrag;
        if (dragEvent != null) {
            dragEvent.recycle();
            this.mCurrentDrag = null;
        }
        int i = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i2 = 0; i2 < i; i2++) {
            viewArr[i2].dispatchDetachedFromWindow();
        }
        super.dispatchDetachedFromWindow();
    }

    @Override // android.view.View
    protected void internalSetPadding(int i, int i2, int i3, int i4) {
        super.internalSetPadding(i, i2, i3, i4);
        if ((this.mPaddingLeft | this.mPaddingTop | this.mPaddingRight | this.mPaddingBottom) != 0) {
            this.mGroupFlags |= 32;
        } else {
            this.mGroupFlags &= -33;
        }
    }

    @Override // android.view.View
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> sparseArray) {
        super.dispatchSaveInstanceState(sparseArray);
        int i = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i2 = 0; i2 < i; i2++) {
            View view = viewArr[i2];
            if ((view.mViewFlags & 536870912) != 536870912) {
                view.dispatchSaveInstanceState(sparseArray);
            }
        }
    }

    protected void dispatchFreezeSelfOnly(SparseArray<Parcelable> sparseArray) {
        super.dispatchSaveInstanceState(sparseArray);
    }

    @Override // android.view.View
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        super.dispatchRestoreInstanceState(sparseArray);
        int i = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i2 = 0; i2 < i; i2++) {
            View view = viewArr[i2];
            if ((view.mViewFlags & 536870912) != 536870912) {
                view.dispatchRestoreInstanceState(sparseArray);
            }
        }
    }

    protected void dispatchThawSelfOnly(SparseArray<Parcelable> sparseArray) {
        super.dispatchRestoreInstanceState(sparseArray);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setChildrenDrawingCacheEnabled(boolean z) {
        if (z || (this.mPersistentDrawingCache & 3) != 3) {
            View[] viewArr = this.mChildren;
            int i = this.mChildrenCount;
            for (int i2 = 0; i2 < i; i2++) {
                viewArr[i2].setDrawingCacheEnabled(z);
            }
        }
    }

    @Override // android.view.View
    protected void onAnimationStart() {
        super.onAnimationStart();
        if ((this.mGroupFlags & 64) == 64) {
            int i = this.mChildrenCount;
            View[] viewArr = this.mChildren;
            boolean z = !isHardwareAccelerated();
            for (int i2 = 0; i2 < i; i2++) {
                View view = viewArr[i2];
                if ((view.mViewFlags & 12) == 0) {
                    view.setDrawingCacheEnabled(true);
                    if (z) {
                        view.buildDrawingCache(true);
                    }
                }
            }
            this.mGroupFlags |= 32768;
        }
    }

    @Override // android.view.View
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        int i = this.mGroupFlags;
        if ((i & 64) == 64) {
            this.mGroupFlags = i & (-32769);
            if ((this.mPersistentDrawingCache & 1) == 0) {
                setChildrenDrawingCacheEnabled(false);
            }
        }
    }

    @Override // android.view.View
    Bitmap createSnapshot(Bitmap.Config config, int i, boolean z) {
        int[] iArr;
        int i2 = this.mChildrenCount;
        if (z) {
            iArr = new int[i2];
            for (int i3 = 0; i3 < i2; i3++) {
                View childAt = getChildAt(i3);
                iArr[i3] = childAt.getVisibility();
                if (iArr[i3] == 0) {
                    childAt.setVisibility(4);
                }
            }
        } else {
            iArr = null;
        }
        Bitmap bitmapCreateSnapshot = super.createSnapshot(config, i, z);
        if (z) {
            for (int i4 = 0; i4 < i2; i4++) {
                getChildAt(i4).setVisibility(iArr[i4]);
            }
        }
        return bitmapCreateSnapshot;
    }

    boolean isLayoutModeOptical() {
        return this.mLayoutMode == 1;
    }

    @Override // android.view.View
    Insets computeOpticalInsets() {
        if (isLayoutModeOptical()) {
            int iMax = 0;
            int iMax2 = 0;
            int iMax3 = 0;
            int iMax4 = 0;
            for (int i = 0; i < this.mChildrenCount; i++) {
                View childAt = getChildAt(i);
                if (childAt.getVisibility() == 0) {
                    Insets opticalInsets = childAt.getOpticalInsets();
                    iMax = Math.max(iMax, opticalInsets.left);
                    iMax2 = Math.max(iMax2, opticalInsets.top);
                    iMax3 = Math.max(iMax3, opticalInsets.right);
                    iMax4 = Math.max(iMax4, opticalInsets.bottom);
                }
            }
            return Insets.of(iMax, iMax2, iMax3, iMax4);
        }
        return Insets.NONE;
    }

    private static void fillRect(Canvas canvas, Paint paint, int i, int i2, int i3, int i4) {
        if (i == i3 || i2 == i4) {
            return;
        }
        if (i > i3) {
            i3 = i;
            i = i3;
        }
        if (i2 > i4) {
            i4 = i2;
            i2 = i4;
        }
        canvas.drawRect(i, i2, i3, i4, paint);
    }

    private static void drawCorner(Canvas canvas, Paint paint, int i, int i2, int i3, int i4, int i5) {
        fillRect(canvas, paint, i, i2, i + i3, i2 + (sign(i4) * i5));
        fillRect(canvas, paint, i, i2, i + (i5 * sign(i3)), i2 + i4);
    }

    private int dipsToPixels(int i) {
        return (int) ((i * getContext().getResources().getDisplayMetrics().density) + 0.5f);
    }

    private static void drawRectCorners(Canvas canvas, int i, int i2, int i3, int i4, Paint paint, int i5, int i6) {
        drawCorner(canvas, paint, i, i2, i5, i5, i6);
        int i7 = -i5;
        drawCorner(canvas, paint, i, i4, i5, i7, i6);
        drawCorner(canvas, paint, i3, i2, i7, i5, i6);
        drawCorner(canvas, paint, i3, i4, i7, i7, i6);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void fillDifference(Canvas canvas, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, Paint paint) {
        int i9 = i - i5;
        int i10 = i3 + i7;
        fillRect(canvas, paint, i9, i2 - i6, i10, i2);
        fillRect(canvas, paint, i9, i2, i, i4);
        fillRect(canvas, paint, i3, i2, i10, i4);
        fillRect(canvas, paint, i9, i4, i10, i4 + i8);
    }

    protected void onDebugDrawMargins(Canvas canvas, Paint paint) {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            childAt.getLayoutParams().onDebugDraw(childAt, canvas, paint);
        }
    }

    protected void onDebugDraw(Canvas canvas) {
        Paint debugPaint = getDebugPaint();
        debugPaint.setColor(-65536);
        debugPaint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            Insets opticalInsets = childAt.getOpticalInsets();
            drawRect(canvas, debugPaint, childAt.getLeft() + opticalInsets.left, childAt.getTop() + opticalInsets.top, (childAt.getRight() - opticalInsets.right) - 1, (childAt.getBottom() - opticalInsets.bottom) - 1);
        }
        debugPaint.setColor(Color.argb(63, 255, 0, 255));
        debugPaint.setStyle(Paint.Style.FILL);
        onDebugDrawMargins(canvas, debugPaint);
        debugPaint.setColor(Color.rgb(63, 127, 255));
        debugPaint.setStyle(Paint.Style.FILL);
        int iDipsToPixels = dipsToPixels(8);
        int iDipsToPixels2 = dipsToPixels(1);
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            View childAt2 = getChildAt(i2);
            drawRectCorners(canvas, childAt2.getLeft(), childAt2.getTop(), childAt2.getRight(), childAt2.getBottom(), debugPaint, iDipsToPixels, iDipsToPixels2);
        }
    }

    @Override // android.view.View
    protected void dispatchDraw(Canvas canvas) {
        int iSave;
        boolean zDrawChild;
        int i = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        int i2 = this.mGroupFlags;
        int i3 = 0;
        if ((i2 & 8) != 0 && canAnimate()) {
            boolean z = (this.mGroupFlags & 64) == 64;
            boolean z2 = !isHardwareAccelerated();
            for (int i4 = 0; i4 < i; i4++) {
                View view = viewArr[i4];
                if ((view.mViewFlags & 12) == 0) {
                    attachLayoutAnimationParameters(view, view.getLayoutParams(), i4, i);
                    bindLayoutAnimation(view);
                    if (z) {
                        view.setDrawingCacheEnabled(true);
                        if (z2) {
                            view.buildDrawingCache(true);
                        }
                    }
                }
            }
            LayoutAnimationController layoutAnimationController = this.mLayoutAnimationController;
            if (layoutAnimationController.willOverlap()) {
                this.mGroupFlags |= 128;
            }
            layoutAnimationController.start();
            int i5 = this.mGroupFlags & (-9);
            this.mGroupFlags = i5;
            int i6 = i5 & (-17);
            this.mGroupFlags = i6;
            if (z) {
                this.mGroupFlags = 32768 | i6;
            }
            Animation.AnimationListener animationListener = this.mAnimationListener;
            if (animationListener != null) {
                animationListener.onAnimationStart(layoutAnimationController.getAnimation());
            }
        }
        boolean z3 = (i2 & 34) == 34;
        if (z3) {
            iSave = canvas.save();
            canvas.clipRect(this.mScrollX + this.mPaddingLeft, this.mScrollY + this.mPaddingTop, ((this.mScrollX + this.mRight) - this.mLeft) - this.mPaddingRight, ((this.mScrollY + this.mBottom) - this.mTop) - this.mPaddingBottom);
        } else {
            iSave = 0;
        }
        this.mPrivateFlags &= -65;
        this.mGroupFlags &= -5;
        long drawingTime = getDrawingTime();
        if ((i2 & 1024) == 0) {
            zDrawChild = false;
            while (i3 < i) {
                View view2 = viewArr[i3];
                if ((view2.mViewFlags & 12) == 0 || view2.getAnimation() != null) {
                    zDrawChild |= drawChild(canvas, view2, drawingTime);
                }
                i3++;
            }
        } else {
            zDrawChild = false;
            while (i3 < i) {
                View view3 = viewArr[getChildDrawingOrder(i, i3)];
                if ((view3.mViewFlags & 12) == 0 || view3.getAnimation() != null) {
                    zDrawChild |= drawChild(canvas, view3, drawingTime);
                }
                i3++;
            }
        }
        ArrayList<View> arrayList = this.mDisappearingChildren;
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                zDrawChild |= drawChild(canvas, arrayList.get(size), drawingTime);
            }
        }
        if (debugDraw()) {
            onDebugDraw(canvas);
        }
        if (z3) {
            canvas.restoreToCount(iSave);
        }
        int i7 = this.mGroupFlags;
        if ((i7 & 4) == 4) {
            invalidate(true);
        }
        if ((i7 & 16) == 0 && (i7 & 512) == 0 && this.mLayoutAnimationController.isDone() && !zDrawChild) {
            this.mGroupFlags |= 512;
            post(new Runnable() { // from class: android.view.ViewGroup.1
                @Override // java.lang.Runnable
                public void run() {
                    ViewGroup.this.notifyAnimationListener();
                }
            });
        }
    }

    @Override // android.view.View
    public ViewGroupOverlay getOverlay() {
        if (this.mOverlay == null) {
            this.mOverlay = new ViewGroupOverlay(this.mContext, this);
        }
        return (ViewGroupOverlay) this.mOverlay;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyAnimationListener() {
        int i = this.mGroupFlags & (-513);
        this.mGroupFlags = i;
        this.mGroupFlags = i | 16;
        if (this.mAnimationListener != null) {
            post(new Runnable() { // from class: android.view.ViewGroup.2
                @Override // java.lang.Runnable
                public void run() {
                    ViewGroup.this.mAnimationListener.onAnimationEnd(ViewGroup.this.mLayoutAnimationController.getAnimation());
                }
            });
        }
        int i2 = this.mGroupFlags;
        if ((i2 & 64) == 64) {
            this.mGroupFlags = i2 & (-32769);
            if ((this.mPersistentDrawingCache & 1) == 0) {
                setChildrenDrawingCacheEnabled(false);
            }
        }
        invalidate(true);
    }

    @Override // android.view.View
    protected void dispatchGetDisplayList() {
        int i = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        int i2 = 0;
        while (true) {
            if (i2 >= i) {
                break;
            }
            View view = viewArr[i2];
            if (((view.mViewFlags & 12) == 0 || view.getAnimation() != null) && view.hasStaticLayer()) {
                view.mRecreateDisplayList = (view.mPrivateFlags & Integer.MIN_VALUE) == Integer.MIN_VALUE;
                view.mPrivateFlags = Integer.MAX_VALUE & view.mPrivateFlags;
                view.getDisplayList();
                view.mRecreateDisplayList = false;
            }
            i2++;
        }
        if (this.mOverlay != null) {
            ViewGroup overlayView = this.mOverlay.getOverlayView();
            overlayView.mRecreateDisplayList = (overlayView.mPrivateFlags & Integer.MIN_VALUE) == Integer.MIN_VALUE;
            overlayView.mPrivateFlags &= Integer.MAX_VALUE;
            overlayView.getDisplayList();
            overlayView.mRecreateDisplayList = false;
        }
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        return view.draw(canvas, this, j);
    }

    public boolean getClipChildren() {
        return (this.mGroupFlags & 1) != 0;
    }

    public void setClipChildren(boolean z) {
        if (z != ((this.mGroupFlags & 1) == 1)) {
            setBooleanFlag(1, z);
            for (int i = 0; i < this.mChildrenCount; i++) {
                View childAt = getChildAt(i);
                if (childAt.mDisplayList != null) {
                    childAt.mDisplayList.setClipToBounds(z);
                }
            }
        }
    }

    public void setClipToPadding(boolean z) {
        setBooleanFlag(2, z);
    }

    @Override // android.view.View
    public void dispatchSetSelected(boolean z) {
        View[] viewArr = this.mChildren;
        int i = this.mChildrenCount;
        for (int i2 = 0; i2 < i; i2++) {
            viewArr[i2].setSelected(z);
        }
    }

    @Override // android.view.View
    public void dispatchSetActivated(boolean z) {
        View[] viewArr = this.mChildren;
        int i = this.mChildrenCount;
        for (int i2 = 0; i2 < i; i2++) {
            viewArr[i2].setActivated(z);
        }
    }

    @Override // android.view.View
    protected void dispatchSetPressed(boolean z) {
        View[] viewArr = this.mChildren;
        int i = this.mChildrenCount;
        for (int i2 = 0; i2 < i; i2++) {
            View view = viewArr[i2];
            if (!z || (!view.isClickable() && !view.isLongClickable())) {
                view.setPressed(z);
            }
        }
    }

    @Override // android.view.View
    void dispatchCancelPendingInputEvents() {
        super.dispatchCancelPendingInputEvents();
        View[] viewArr = this.mChildren;
        int i = this.mChildrenCount;
        for (int i2 = 0; i2 < i; i2++) {
            viewArr[i2].dispatchCancelPendingInputEvents();
        }
    }

    protected void setStaticTransformationsEnabled(boolean z) {
        setBooleanFlag(2048, z);
    }

    Transformation getChildTransformation() {
        if (this.mChildTransformation == null) {
            this.mChildTransformation = new Transformation();
        }
        return this.mChildTransformation;
    }

    @Override // android.view.View
    protected View findViewTraversal(int i) {
        View viewFindViewById;
        if (i == this.mID) {
            return this;
        }
        View[] viewArr = this.mChildren;
        int i2 = this.mChildrenCount;
        for (int i3 = 0; i3 < i2; i3++) {
            View view = viewArr[i3];
            if ((view.mPrivateFlags & 8) == 0 && (viewFindViewById = view.findViewById(i)) != null) {
                return viewFindViewById;
            }
        }
        return null;
    }

    @Override // android.view.View
    protected View findViewWithTagTraversal(Object obj) {
        View viewFindViewWithTag;
        if (obj != null && obj.equals(this.mTag)) {
            return this;
        }
        View[] viewArr = this.mChildren;
        int i = this.mChildrenCount;
        for (int i2 = 0; i2 < i; i2++) {
            View view = viewArr[i2];
            if ((view.mPrivateFlags & 8) == 0 && (viewFindViewWithTag = view.findViewWithTag(obj)) != null) {
                return viewFindViewWithTag;
            }
        }
        return null;
    }

    @Override // android.view.View
    protected View findViewByPredicateTraversal(Predicate<View> predicate, View view) {
        View viewFindViewByPredicate;
        if (predicate.apply(this)) {
            return this;
        }
        View[] viewArr = this.mChildren;
        int i = this.mChildrenCount;
        for (int i2 = 0; i2 < i; i2++) {
            View view2 = viewArr[i2];
            if (view2 != view && (view2.mPrivateFlags & 8) == 0 && (viewFindViewByPredicate = view2.findViewByPredicate(predicate)) != null) {
                return viewFindViewByPredicate;
            }
        }
        return null;
    }

    public void addView(View view) {
        addView(view, -1);
    }

    public void addView(View view, int i) {
        LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null && (layoutParams = generateDefaultLayoutParams()) == null) {
            throw new IllegalArgumentException("generateDefaultLayoutParams() cannot return null");
        }
        addView(view, i, layoutParams);
    }

    public void addView(View view, int i, int i2) {
        LayoutParams layoutParamsGenerateDefaultLayoutParams = generateDefaultLayoutParams();
        layoutParamsGenerateDefaultLayoutParams.width = i;
        layoutParamsGenerateDefaultLayoutParams.height = i2;
        addView(view, -1, layoutParamsGenerateDefaultLayoutParams);
    }

    @Override // android.view.ViewManager
    public void addView(View view, LayoutParams layoutParams) {
        addView(view, -1, layoutParams);
    }

    public void addView(View view, int i, LayoutParams layoutParams) {
        requestLayout();
        invalidate(true);
        addViewInner(view, i, layoutParams, false);
    }

    @Override // android.view.ViewManager
    public void updateViewLayout(View view, LayoutParams layoutParams) {
        if (!checkLayoutParams(layoutParams)) {
            throw new IllegalArgumentException("Invalid LayoutParams supplied to " + this);
        }
        if (view.mParent != this) {
            throw new IllegalArgumentException("Given view not a child of " + this);
        }
        view.setLayoutParams(layoutParams);
    }

    public void setOnHierarchyChangeListener(OnHierarchyChangeListener onHierarchyChangeListener) {
        this.mOnHierarchyChangeListener = onHierarchyChangeListener;
    }

    protected void onViewAdded(View view) {
        OnHierarchyChangeListener onHierarchyChangeListener = this.mOnHierarchyChangeListener;
        if (onHierarchyChangeListener != null) {
            onHierarchyChangeListener.onChildViewAdded(this, view);
        }
    }

    protected void onViewRemoved(View view) {
        OnHierarchyChangeListener onHierarchyChangeListener = this.mOnHierarchyChangeListener;
        if (onHierarchyChangeListener != null) {
            onHierarchyChangeListener.onChildViewRemoved(this, view);
        }
    }

    private void clearCachedLayoutMode() {
        if (hasBooleanFlag(8388608)) {
            return;
        }
        this.mLayoutMode = -1;
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        clearCachedLayoutMode();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearCachedLayoutMode();
    }

    protected boolean addViewInLayout(View view, int i, LayoutParams layoutParams) {
        return addViewInLayout(view, i, layoutParams, false);
    }

    protected boolean addViewInLayout(View view, int i, LayoutParams layoutParams, boolean z) {
        view.mParent = null;
        addViewInner(view, i, layoutParams, z);
        view.mPrivateFlags = (view.mPrivateFlags & (-6291457)) | 32;
        return true;
    }

    protected void cleanupLayoutState(View view) {
        view.mPrivateFlags &= -4097;
    }

    private void addViewInner(View view, int i, LayoutParams layoutParams, boolean z) {
        LayoutTransition layoutTransition = this.mTransition;
        if (layoutTransition != null) {
            layoutTransition.cancel(3);
        }
        if (view.getParent() != null) {
            throw new IllegalStateException("The specified child already has a parent. You must call removeView() on the child's parent first.");
        }
        LayoutTransition layoutTransition2 = this.mTransition;
        if (layoutTransition2 != null) {
            layoutTransition2.addChild(this, view);
        }
        if (!checkLayoutParams(layoutParams)) {
            layoutParams = generateLayoutParams(layoutParams);
        }
        if (z) {
            view.mLayoutParams = layoutParams;
        } else {
            view.setLayoutParams(layoutParams);
        }
        if (i < 0) {
            i = this.mChildrenCount;
        }
        addInArray(view, i);
        if (z) {
            view.assignParent(this);
        } else {
            view.mParent = this;
        }
        if (view.hasFocus()) {
            requestChildFocus(view, view.findFocus());
        }
        View.AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null && (this.mGroupFlags & 4194304) == 0) {
            boolean z2 = attachInfo.mKeepScreenOn;
            attachInfo.mKeepScreenOn = false;
            view.dispatchAttachedToWindow(this.mAttachInfo, this.mViewFlags & 12);
            if (attachInfo.mKeepScreenOn) {
                needGlobalAttributesUpdate(true);
            }
            attachInfo.mKeepScreenOn = z2;
        }
        if (view.isLayoutDirectionInherited()) {
            view.resetRtlProperties();
        }
        onViewAdded(view);
        if ((view.mViewFlags & 4194304) == 4194304) {
            this.mGroupFlags |= 65536;
        }
        if (view.hasTransientState()) {
            childHasTransientStateChanged(view, true);
        }
        if (!view.isImportantForAccessibility() || view.getVisibility() == 8) {
            return;
        }
        notifySubtreeAccessibilityStateChangedIfNeeded();
    }

    private void addInArray(View view, int i) {
        View[] viewArr = this.mChildren;
        int i2 = this.mChildrenCount;
        int length = viewArr.length;
        if (i == i2) {
            if (length == i2) {
                View[] viewArr2 = new View[length + 12];
                this.mChildren = viewArr2;
                System.arraycopy(viewArr, 0, viewArr2, 0, length);
                viewArr = this.mChildren;
            }
            int i3 = this.mChildrenCount;
            this.mChildrenCount = i3 + 1;
            viewArr[i3] = view;
            return;
        }
        if (i < i2) {
            if (length == i2) {
                View[] viewArr3 = new View[length + 12];
                this.mChildren = viewArr3;
                System.arraycopy(viewArr, 0, viewArr3, 0, i);
                System.arraycopy(viewArr, i, this.mChildren, i + 1, i2 - i);
                viewArr = this.mChildren;
            } else {
                System.arraycopy(viewArr, i, viewArr, i + 1, i2 - i);
            }
            viewArr[i] = view;
            this.mChildrenCount++;
            int i4 = this.mLastTouchDownIndex;
            if (i4 >= i) {
                this.mLastTouchDownIndex = i4 + 1;
                return;
            }
            return;
        }
        throw new IndexOutOfBoundsException("index=" + i + " count=" + i2);
    }

    private void removeFromArray(int i) {
        View[] viewArr = this.mChildren;
        ArrayList<View> arrayList = this.mTransitioningViews;
        if (arrayList == null || !arrayList.contains(viewArr[i])) {
            viewArr[i].mParent = null;
        }
        int i2 = this.mChildrenCount;
        if (i == i2 - 1) {
            int i3 = i2 - 1;
            this.mChildrenCount = i3;
            viewArr[i3] = null;
        } else if (i >= 0 && i < i2) {
            System.arraycopy(viewArr, i + 1, viewArr, i, (i2 - i) - 1);
            int i4 = this.mChildrenCount - 1;
            this.mChildrenCount = i4;
            viewArr[i4] = null;
        } else {
            throw new IndexOutOfBoundsException();
        }
        int i5 = this.mLastTouchDownIndex;
        if (i5 == i) {
            this.mLastTouchDownTime = 0L;
            this.mLastTouchDownIndex = -1;
        } else if (i5 > i) {
            this.mLastTouchDownIndex = i5 - 1;
        }
    }

    private void removeFromArray(int i, int i2) {
        View[] viewArr = this.mChildren;
        int i3 = this.mChildrenCount;
        int iMax = Math.max(0, i);
        int iMin = Math.min(i3, i2 + iMax);
        if (iMax == iMin) {
            return;
        }
        if (iMin == i3) {
            for (int i4 = iMax; i4 < iMin; i4++) {
                viewArr[i4].mParent = null;
                viewArr[i4] = null;
            }
        } else {
            for (int i5 = iMax; i5 < iMin; i5++) {
                viewArr[i5].mParent = null;
            }
            System.arraycopy(viewArr, iMin, viewArr, iMax, i3 - iMin);
            for (int i6 = i3 - (iMin - iMax); i6 < i3; i6++) {
                viewArr[i6] = null;
            }
        }
        this.mChildrenCount -= iMin - iMax;
    }

    private void bindLayoutAnimation(View view) {
        view.setAnimation(this.mLayoutAnimationController.getAnimationForView(view));
    }

    protected void attachLayoutAnimationParameters(View view, LayoutParams layoutParams, int i, int i2) {
        LayoutAnimationController.AnimationParameters animationParameters = layoutParams.layoutAnimationParameters;
        if (animationParameters == null) {
            animationParameters = new LayoutAnimationController.AnimationParameters();
            layoutParams.layoutAnimationParameters = animationParameters;
        }
        animationParameters.count = i2;
        animationParameters.index = i;
    }

    @Override // android.view.ViewManager
    public void removeView(View view) {
        removeViewInternal(view);
        requestLayout();
        invalidate(true);
    }

    public void removeViewInLayout(View view) {
        removeViewInternal(view);
    }

    public void removeViewsInLayout(int i, int i2) {
        removeViewsInternal(i, i2);
    }

    public void removeViewAt(int i) {
        removeViewInternal(i, getChildAt(i));
        requestLayout();
        invalidate(true);
    }

    public void removeViews(int i, int i2) {
        removeViewsInternal(i, i2);
        requestLayout();
        invalidate(true);
    }

    private void removeViewInternal(View view) {
        int iIndexOfChild = indexOfChild(view);
        if (iIndexOfChild >= 0) {
            removeViewInternal(iIndexOfChild, view);
        }
    }

    private void removeViewInternal(int i, View view) {
        boolean z;
        ArrayList<View> arrayList;
        LayoutTransition layoutTransition = this.mTransition;
        if (layoutTransition != null) {
            layoutTransition.removeChild(this, view);
        }
        if (view == this.mFocused) {
            view.unFocus();
            z = true;
        } else {
            z = false;
        }
        if (view.isAccessibilityFocused()) {
            view.clearAccessibilityFocus();
        }
        cancelTouchTarget(view);
        cancelHoverTarget(view);
        if (view.getAnimation() != null || ((arrayList = this.mTransitioningViews) != null && arrayList.contains(view))) {
            addDisappearingView(view);
        } else if (view.mAttachInfo != null) {
            view.dispatchDetachedFromWindow();
        }
        if (view.hasTransientState()) {
            childHasTransientStateChanged(view, false);
        }
        needGlobalAttributesUpdate(false);
        removeFromArray(i);
        if (z) {
            clearChildFocus(view);
            if (!rootViewRequestFocus()) {
                notifyGlobalFocusCleared(this);
            }
        }
        onViewRemoved(view);
        if (!view.isImportantForAccessibility() || view.getVisibility() == 8) {
            return;
        }
        notifySubtreeAccessibilityStateChangedIfNeeded();
    }

    public void setLayoutTransition(LayoutTransition layoutTransition) {
        LayoutTransition layoutTransition2 = this.mTransition;
        if (layoutTransition2 != null) {
            layoutTransition2.cancel();
            layoutTransition2.removeTransitionListener(this.mLayoutTransitionListener);
        }
        this.mTransition = layoutTransition;
        if (layoutTransition != null) {
            layoutTransition.addTransitionListener(this.mLayoutTransitionListener);
        }
    }

    public LayoutTransition getLayoutTransition() {
        return this.mTransition;
    }

    private void removeViewsInternal(int i, int i2) {
        ArrayList<View> arrayList;
        View view = this.mFocused;
        boolean z = this.mAttachInfo != null;
        View[] viewArr = this.mChildren;
        int i3 = i + i2;
        boolean z2 = false;
        for (int i4 = i; i4 < i3; i4++) {
            View view2 = viewArr[i4];
            LayoutTransition layoutTransition = this.mTransition;
            if (layoutTransition != null) {
                layoutTransition.removeChild(this, view2);
            }
            if (view2 == view) {
                view2.unFocus();
                z2 = true;
            }
            if (view2.isAccessibilityFocused()) {
                view2.clearAccessibilityFocus();
            }
            cancelTouchTarget(view2);
            cancelHoverTarget(view2);
            if (view2.getAnimation() != null || ((arrayList = this.mTransitioningViews) != null && arrayList.contains(view2))) {
                addDisappearingView(view2);
            } else if (z) {
                view2.dispatchDetachedFromWindow();
            }
            if (view2.hasTransientState()) {
                childHasTransientStateChanged(view2, false);
            }
            needGlobalAttributesUpdate(false);
            onViewRemoved(view2);
        }
        removeFromArray(i, i2);
        if (z2) {
            clearChildFocus(view);
            if (rootViewRequestFocus()) {
                return;
            }
            notifyGlobalFocusCleared(view);
        }
    }

    public void removeAllViews() {
        removeAllViewsInLayout();
        requestLayout();
        invalidate(true);
    }

    public void removeAllViewsInLayout() {
        ArrayList<View> arrayList;
        int i = this.mChildrenCount;
        if (i <= 0) {
            return;
        }
        View[] viewArr = this.mChildren;
        this.mChildrenCount = 0;
        View view = this.mFocused;
        boolean z = this.mAttachInfo != null;
        needGlobalAttributesUpdate(false);
        boolean z2 = false;
        for (int i2 = i - 1; i2 >= 0; i2--) {
            View view2 = viewArr[i2];
            LayoutTransition layoutTransition = this.mTransition;
            if (layoutTransition != null) {
                layoutTransition.removeChild(this, view2);
            }
            if (view2 == view) {
                view2.unFocus();
                z2 = true;
            }
            if (view2.isAccessibilityFocused()) {
                view2.clearAccessibilityFocus();
            }
            cancelTouchTarget(view2);
            cancelHoverTarget(view2);
            if (view2.getAnimation() != null || ((arrayList = this.mTransitioningViews) != null && arrayList.contains(view2))) {
                addDisappearingView(view2);
            } else if (z) {
                view2.dispatchDetachedFromWindow();
            }
            if (view2.hasTransientState()) {
                childHasTransientStateChanged(view2, false);
            }
            onViewRemoved(view2);
            view2.mParent = null;
            viewArr[i2] = null;
        }
        if (z2) {
            clearChildFocus(view);
            if (rootViewRequestFocus()) {
                return;
            }
            notifyGlobalFocusCleared(view);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void removeDetachedView(View view, boolean z) {
        ArrayList<View> arrayList;
        LayoutTransition layoutTransition = this.mTransition;
        if (layoutTransition != null) {
            layoutTransition.removeChild(this, view);
        }
        if (view == this.mFocused) {
            view.clearFocus();
        }
        view.clearAccessibilityFocus();
        cancelTouchTarget(view);
        cancelHoverTarget(view);
        if ((z && view.getAnimation() != null) || ((arrayList = this.mTransitioningViews) != null && arrayList.contains(view))) {
            addDisappearingView(view);
        } else if (view.mAttachInfo != null) {
            view.dispatchDetachedFromWindow();
        }
        if (view.hasTransientState()) {
            childHasTransientStateChanged(view, false);
        }
        onViewRemoved(view);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void attachViewToParent(View view, int i, LayoutParams layoutParams) {
        view.mLayoutParams = layoutParams;
        if (i < 0) {
            i = this.mChildrenCount;
        }
        addInArray(view, i);
        view.mParent = this;
        view.mPrivateFlags = (view.mPrivateFlags & (-6291457) & (-32769)) | 32 | Integer.MIN_VALUE;
        this.mPrivateFlags |= Integer.MIN_VALUE;
        if (view.hasFocus()) {
            requestChildFocus(view, view.findFocus());
        }
    }

    protected void detachViewFromParent(View view) {
        removeFromArray(indexOfChild(view));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void detachViewFromParent(int i) {
        removeFromArray(i);
    }

    protected void detachViewsFromParent(int i, int i2) {
        removeFromArray(i, i2);
    }

    protected void detachAllViewsFromParent() {
        int i = this.mChildrenCount;
        if (i <= 0) {
            return;
        }
        View[] viewArr = this.mChildren;
        this.mChildrenCount = 0;
        for (int i2 = i - 1; i2 >= 0; i2--) {
            viewArr[i2].mParent = null;
            viewArr[i2] = null;
        }
    }

    @Override // android.view.ViewParent
    public final void invalidateChild(View view, Rect rect) {
        View.AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            boolean z = (view.mPrivateFlags & 64) == 64;
            Matrix matrix = view.getMatrix();
            int i = view.isOpaque() && !z && view.getAnimation() == null && matrix.isIdentity() ? 4194304 : 2097152;
            if (view.mLayerType != 0) {
                this.mPrivateFlags |= Integer.MIN_VALUE;
                this.mPrivateFlags &= -32769;
                view.mLocalDirtyRect.union(rect);
            }
            int[] iArr = attachInfo.mInvalidateChildLocation;
            iArr[0] = view.mLeft;
            iArr[1] = view.mTop;
            if (!matrix.isIdentity() || (this.mGroupFlags & 2048) != 0) {
                RectF rectF = attachInfo.mTmpTransformRect;
                rectF.set(rect);
                if ((this.mGroupFlags & 2048) != 0) {
                    Transformation transformation = attachInfo.mTmpTransformation;
                    if (getChildStaticTransformation(view, transformation)) {
                        Matrix matrix2 = attachInfo.mTmpMatrix;
                        matrix2.set(transformation.getMatrix());
                        if (!matrix.isIdentity()) {
                            matrix2.preConcat(matrix);
                        }
                        matrix = matrix2;
                    }
                }
                matrix.mapRect(rectF);
                rect.set((int) (rectF.left - 0.5f), (int) (rectF.top - 0.5f), (int) (rectF.right + 0.5f), (int) (rectF.bottom + 0.5f));
            }
            ViewParent viewParentInvalidateChildInParent = this;
            do {
                View view2 = viewParentInvalidateChildInParent instanceof View ? (View) viewParentInvalidateChildInParent : null;
                if (z) {
                    if (view2 != null) {
                        view2.mPrivateFlags |= 64;
                    } else if (viewParentInvalidateChildInParent instanceof ViewRootImpl) {
                        ((ViewRootImpl) viewParentInvalidateChildInParent).mIsAnimating = true;
                    }
                }
                if (view2 != null) {
                    if ((view2.mViewFlags & 12288) != 0 && view2.getSolidColor() == 0) {
                        i = 2097152;
                    }
                    if ((view2.mPrivateFlags & IntentFilter.MATCH_CATEGORY_TYPE) != 2097152) {
                        view2.mPrivateFlags = (view2.mPrivateFlags & (-6291457)) | i;
                    }
                }
                viewParentInvalidateChildInParent = viewParentInvalidateChildInParent.invalidateChildInParent(iArr, rect);
                if (view2 != null) {
                    Matrix matrix3 = view2.getMatrix();
                    if (!matrix3.isIdentity()) {
                        RectF rectF2 = attachInfo.mTmpTransformRect;
                        rectF2.set(rect);
                        matrix3.mapRect(rectF2);
                        rect.set((int) (rectF2.left - 0.5f), (int) (rectF2.top - 0.5f), (int) (rectF2.right + 0.5f), (int) (rectF2.bottom + 0.5f));
                    }
                }
            } while (viewParentInvalidateChildInParent != null);
        }
    }

    @Override // android.view.ViewParent
    public ViewParent invalidateChildInParent(int[] iArr, Rect rect) {
        if ((this.mPrivateFlags & 32) != 32 && (this.mPrivateFlags & 32768) != 32768) {
            return null;
        }
        if ((this.mGroupFlags & 144) != 128) {
            rect.offset(iArr[0] - this.mScrollX, iArr[1] - this.mScrollY);
            if ((this.mGroupFlags & 1) == 0) {
                rect.union(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
            }
            int i = this.mLeft;
            int i2 = this.mTop;
            if ((this.mGroupFlags & 1) == 1 && !rect.intersect(0, 0, this.mRight - i, this.mBottom - i2)) {
                rect.setEmpty();
            }
            this.mPrivateFlags &= -32769;
            iArr[0] = i;
            iArr[1] = i2;
            if (this.mLayerType != 0) {
                this.mPrivateFlags |= Integer.MIN_VALUE;
                this.mLocalDirtyRect.union(rect);
            }
            return this.mParent;
        }
        this.mPrivateFlags &= -32801;
        iArr[0] = this.mLeft;
        iArr[1] = this.mTop;
        if ((this.mGroupFlags & 1) == 1) {
            rect.set(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
        } else {
            rect.union(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
        }
        if (this.mLayerType != 0) {
            this.mPrivateFlags |= Integer.MIN_VALUE;
            this.mLocalDirtyRect.union(rect);
        }
        return this.mParent;
    }

    public void invalidateChildFast(View view, Rect rect) {
        View.AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            if (view.mLayerType != 0) {
                view.mLocalDirtyRect.union(rect);
            }
            int i = view.mLeft;
            int i2 = view.mTop;
            if (!view.getMatrix().isIdentity()) {
                view.transformRect(rect);
            }
            ViewParent viewParentInvalidateChildInParent = this;
            do {
                if (viewParentInvalidateChildInParent instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) viewParentInvalidateChildInParent;
                    if (viewGroup.mLayerType != 0) {
                        viewGroup.invalidate();
                        viewParentInvalidateChildInParent = null;
                    } else {
                        ViewParent viewParentInvalidateChildInParentFast = viewGroup.invalidateChildInParentFast(i, i2, rect);
                        int i3 = viewGroup.mLeft;
                        i2 = viewGroup.mTop;
                        viewParentInvalidateChildInParent = viewParentInvalidateChildInParentFast;
                        i = i3;
                    }
                } else {
                    int[] iArr = attachInfo.mInvalidateChildLocation;
                    iArr[0] = i;
                    iArr[1] = i2;
                    viewParentInvalidateChildInParent = viewParentInvalidateChildInParent.invalidateChildInParent(iArr, rect);
                }
            } while (viewParentInvalidateChildInParent != null);
        }
    }

    protected ViewParent invalidateChildInParentFast(int i, int i2, Rect rect) {
        if ((this.mPrivateFlags & 32) != 32 && (this.mPrivateFlags & 32768) != 32768) {
            return null;
        }
        rect.offset(i - this.mScrollX, i2 - this.mScrollY);
        if ((this.mGroupFlags & 1) == 0) {
            rect.union(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
        }
        if ((this.mGroupFlags & 1) != 0 && !rect.intersect(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop)) {
            return null;
        }
        if (this.mLayerType != 0) {
            this.mLocalDirtyRect.union(rect);
        }
        if (!getMatrix().isIdentity()) {
            transformRect(rect);
        }
        return this.mParent;
    }

    public final void offsetDescendantRectToMyCoords(View view, Rect rect) {
        offsetRectBetweenParentAndChild(view, rect, true, false);
    }

    public final void offsetRectIntoDescendantCoords(View view, Rect rect) {
        offsetRectBetweenParentAndChild(view, rect, false, false);
    }

    void offsetRectBetweenParentAndChild(View view, Rect rect, boolean z, boolean z2) {
        if (view == this) {
            return;
        }
        ViewParent viewParent = view.mParent;
        while (viewParent != null && (viewParent instanceof View) && viewParent != this) {
            if (z) {
                rect.offset(view.mLeft - view.mScrollX, view.mTop - view.mScrollY);
                if (z2) {
                    View view2 = (View) viewParent;
                    rect.intersect(0, 0, view2.mRight - view2.mLeft, view2.mBottom - view2.mTop);
                }
            } else {
                if (z2) {
                    View view3 = (View) viewParent;
                    rect.intersect(0, 0, view3.mRight - view3.mLeft, view3.mBottom - view3.mTop);
                }
                rect.offset(view.mScrollX - view.mLeft, view.mScrollY - view.mTop);
            }
            view = (View) viewParent;
            viewParent = view.mParent;
        }
        if (viewParent != this) {
            throw new IllegalArgumentException("parameter must be a descendant of this view");
        }
        if (z) {
            rect.offset(view.mLeft - view.mScrollX, view.mTop - view.mScrollY);
        } else {
            rect.offset(view.mScrollX - view.mLeft, view.mScrollY - view.mTop);
        }
    }

    public void offsetChildrenTopAndBottom(int i) {
        int i2 = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        boolean z = false;
        for (int i3 = 0; i3 < i2; i3++) {
            View view = viewArr[i3];
            view.mTop += i;
            view.mBottom += i;
            if (view.mDisplayList != null) {
                view.mDisplayList.offsetTopAndBottom(i);
                z = true;
            }
        }
        if (z) {
            invalidateViewProperty(false, false);
        }
    }

    @Override // android.view.ViewParent
    public boolean getChildVisibleRect(View view, Rect rect, Point point) {
        RectF rectF = this.mAttachInfo != null ? this.mAttachInfo.mTmpTransformRect : new RectF();
        rectF.set(rect);
        if (!view.hasIdentityMatrix()) {
            view.getMatrix().mapRect(rectF);
        }
        int i = view.mLeft - this.mScrollX;
        int i2 = view.mTop - this.mScrollY;
        rectF.offset(i, i2);
        if (point != null) {
            if (!view.hasIdentityMatrix()) {
                float[] fArr = this.mAttachInfo != null ? this.mAttachInfo.mTmpTransformLocation : new float[2];
                fArr[0] = point.x;
                fArr[1] = point.y;
                view.getMatrix().mapPoints(fArr);
                point.x = (int) (fArr[0] + 0.5f);
                point.y = (int) (fArr[1] + 0.5f);
            }
            point.x += i;
            point.y += i2;
        }
        if (!rectF.intersect(0.0f, 0.0f, this.mRight - this.mLeft, this.mBottom - this.mTop)) {
            return false;
        }
        if (this.mParent == null) {
            return true;
        }
        rect.set((int) (rectF.left + 0.5f), (int) (rectF.top + 0.5f), (int) (rectF.right + 0.5f), (int) (rectF.bottom + 0.5f));
        return this.mParent.getChildVisibleRect(this, rect, point);
    }

    @Override // android.view.View
    public final void layout(int i, int i2, int i3, int i4) {
        LayoutTransition layoutTransition;
        if (!this.mSuppressLayout && ((layoutTransition = this.mTransition) == null || !layoutTransition.isChangingLayout())) {
            LayoutTransition layoutTransition2 = this.mTransition;
            if (layoutTransition2 != null) {
                layoutTransition2.layoutChange(this);
            }
            super.layout(i, i2, i3, i4);
            return;
        }
        this.mLayoutCalledWhileSuppressed = true;
    }

    protected boolean canAnimate() {
        return this.mLayoutAnimationController != null;
    }

    public void startLayoutAnimation() {
        if (this.mLayoutAnimationController != null) {
            this.mGroupFlags |= 8;
            requestLayout();
        }
    }

    public void scheduleLayoutAnimation() {
        this.mGroupFlags |= 8;
    }

    public void setLayoutAnimation(LayoutAnimationController layoutAnimationController) {
        this.mLayoutAnimationController = layoutAnimationController;
        if (layoutAnimationController != null) {
            this.mGroupFlags |= 8;
        }
    }

    public LayoutAnimationController getLayoutAnimation() {
        return this.mLayoutAnimationController;
    }

    @ViewDebug.ExportedProperty
    public boolean isAnimationCacheEnabled() {
        return (this.mGroupFlags & 64) == 64;
    }

    public void setAnimationCacheEnabled(boolean z) {
        setBooleanFlag(64, z);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public boolean isAlwaysDrawnWithCacheEnabled() {
        return (this.mGroupFlags & 16384) == 16384;
    }

    public void setAlwaysDrawnWithCacheEnabled(boolean z) {
        setBooleanFlag(16384, z);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    protected boolean isChildrenDrawnWithCacheEnabled() {
        return (this.mGroupFlags & 32768) == 32768;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setChildrenDrawnWithCacheEnabled(boolean z) {
        setBooleanFlag(32768, z);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    protected boolean isChildrenDrawingOrderEnabled() {
        return (this.mGroupFlags & 1024) == 1024;
    }

    protected void setChildrenDrawingOrderEnabled(boolean z) {
        setBooleanFlag(1024, z);
    }

    private boolean hasBooleanFlag(int i) {
        return (this.mGroupFlags & i) == i;
    }

    private void setBooleanFlag(int i, boolean z) {
        if (z) {
            this.mGroupFlags = i | this.mGroupFlags;
        } else {
            this.mGroupFlags = (~i) & this.mGroupFlags;
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing", mapping = {@ViewDebug.IntToString(from = 0, to = "NONE"), @ViewDebug.IntToString(from = 1, to = "ANIMATION"), @ViewDebug.IntToString(from = 2, to = "SCROLLING"), @ViewDebug.IntToString(from = 3, to = "ALL")})
    public int getPersistentDrawingCache() {
        return this.mPersistentDrawingCache;
    }

    public void setPersistentDrawingCache(int i) {
        this.mPersistentDrawingCache = i & 3;
    }

    private void setLayoutMode(int i, boolean z) {
        this.mLayoutMode = i;
        setBooleanFlag(8388608, z);
    }

    @Override // android.view.View
    void invalidateInheritedLayoutMode(int i) {
        int i2 = this.mLayoutMode;
        if (i2 == -1 || i2 == i || hasBooleanFlag(8388608)) {
            return;
        }
        setLayoutMode(-1, false);
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            getChildAt(i3).invalidateInheritedLayoutMode(i);
        }
    }

    public int getLayoutMode() {
        if (this.mLayoutMode == -1) {
            setLayoutMode(this.mParent instanceof ViewGroup ? ((ViewGroup) this.mParent).getLayoutMode() : LAYOUT_MODE_DEFAULT, false);
        }
        return this.mLayoutMode;
    }

    public void setLayoutMode(int i) {
        if (this.mLayoutMode != i) {
            invalidateInheritedLayoutMode(i);
            setLayoutMode(i, i != -1);
            requestLayout();
        }
    }

    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    @Override // android.view.View
    protected void debug(int i) {
        super.debug(i);
        if (this.mFocused != null) {
            Log.d("View", debugIndent(i) + "mFocused");
        }
        if (this.mChildrenCount != 0) {
            Log.d("View", debugIndent(i) + "{");
        }
        int i2 = this.mChildrenCount;
        for (int i3 = 0; i3 < i2; i3++) {
            this.mChildren[i3].debug(i + 1);
        }
        if (this.mChildrenCount != 0) {
            Log.d("View", debugIndent(i) + "}");
        }
    }

    public int indexOfChild(View view) {
        int i = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i2 = 0; i2 < i; i2++) {
            if (viewArr[i2] == view) {
                return i2;
            }
        }
        return -1;
    }

    public int getChildCount() {
        return this.mChildrenCount;
    }

    public View getChildAt(int i) {
        if (i < 0 || i >= this.mChildrenCount) {
            return null;
        }
        return this.mChildren[i];
    }

    protected void measureChildren(int i, int i2) {
        int i3 = this.mChildrenCount;
        View[] viewArr = this.mChildren;
        for (int i4 = 0; i4 < i3; i4++) {
            View view = viewArr[i4];
            if ((view.mViewFlags & 12) != 8) {
                measureChild(view, i, i2);
            }
        }
    }

    protected void measureChild(View view, int i, int i2) {
        LayoutParams layoutParams = view.getLayoutParams();
        view.measure(getChildMeasureSpec(i, this.mPaddingLeft + this.mPaddingRight, layoutParams.width), getChildMeasureSpec(i2, this.mPaddingTop + this.mPaddingBottom, layoutParams.height));
    }

    protected void measureChildWithMargins(View view, int i, int i2, int i3, int i4) {
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
        view.measure(getChildMeasureSpec(i, this.mPaddingLeft + this.mPaddingRight + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin + i2, marginLayoutParams.width), getChildMeasureSpec(i3, this.mPaddingTop + this.mPaddingBottom + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin + i4, marginLayoutParams.height));
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x002e  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0034  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static int getChildMeasureSpec(int r5, int r6, int r7) {
        /*
            int r0 = android.view.View.MeasureSpec.getMode(r5)
            int r5 = android.view.View.MeasureSpec.getSize(r5)
            int r5 = r5 - r6
            r6 = 0
            int r5 = java.lang.Math.max(r6, r5)
            r1 = -2
            r2 = -1
            r3 = -2147483648(0xffffffff80000000, float:-0.0)
            r4 = 1073741824(0x40000000, float:2.0)
            if (r0 == r3) goto L28
            if (r0 == 0) goto L25
            if (r0 == r4) goto L1b
            goto L34
        L1b:
            if (r7 < 0) goto L1e
            goto L2a
        L1e:
            if (r7 != r2) goto L22
            r7 = r5
            goto L2a
        L22:
            if (r7 != r1) goto L34
            goto L2e
        L25:
            if (r7 < 0) goto L34
            goto L2a
        L28:
            if (r7 < 0) goto L2c
        L2a:
            r6 = r4
            goto L35
        L2c:
            if (r7 != r2) goto L31
        L2e:
            r7 = r5
            r6 = r3
            goto L35
        L31:
            if (r7 != r1) goto L34
            goto L2e
        L34:
            r7 = r6
        L35:
            int r5 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r6)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.ViewGroup.getChildMeasureSpec(int, int, int):int");
    }

    public void clearDisappearingChildren() {
        ArrayList<View> arrayList = this.mDisappearingChildren;
        if (arrayList != null) {
            arrayList.clear();
            invalidate();
        }
    }

    private void addDisappearingView(View view) {
        ArrayList<View> arrayList = this.mDisappearingChildren;
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.mDisappearingChildren = arrayList;
        }
        arrayList.add(view);
    }

    void finishAnimatingView(View view, Animation animation) {
        ArrayList<View> arrayList = this.mDisappearingChildren;
        if (arrayList != null && arrayList.contains(view)) {
            arrayList.remove(view);
            if (view.mAttachInfo != null) {
                view.dispatchDetachedFromWindow();
            }
            view.clearAnimation();
            this.mGroupFlags |= 4;
        }
        if (animation != null && !animation.getFillAfter()) {
            view.clearAnimation();
        }
        if ((view.mPrivateFlags & 65536) == 65536) {
            view.onAnimationEnd();
            view.mPrivateFlags &= -65537;
            this.mGroupFlags |= 4;
        }
    }

    boolean isViewTransitioning(View view) {
        ArrayList<View> arrayList = this.mTransitioningViews;
        return arrayList != null && arrayList.contains(view);
    }

    public void startViewTransition(View view) {
        if (view.mParent == this) {
            if (this.mTransitioningViews == null) {
                this.mTransitioningViews = new ArrayList<>();
            }
            this.mTransitioningViews.add(view);
        }
    }

    public void endViewTransition(View view) {
        ArrayList<View> arrayList = this.mTransitioningViews;
        if (arrayList != null) {
            arrayList.remove(view);
            ArrayList<View> arrayList2 = this.mDisappearingChildren;
            if (arrayList2 == null || !arrayList2.contains(view)) {
                return;
            }
            arrayList2.remove(view);
            ArrayList<View> arrayList3 = this.mVisibilityChangingChildren;
            if (arrayList3 != null && arrayList3.contains(view)) {
                this.mVisibilityChangingChildren.remove(view);
            } else {
                if (view.mAttachInfo != null) {
                    view.dispatchDetachedFromWindow();
                }
                if (view.mParent != null) {
                    view.mParent = null;
                }
            }
            invalidate();
        }
    }

    public void suppressLayout(boolean z) {
        this.mSuppressLayout = z;
        if (z || !this.mLayoutCalledWhileSuppressed) {
            return;
        }
        requestLayout();
        this.mLayoutCalledWhileSuppressed = false;
    }

    public boolean isLayoutSuppressed() {
        return this.mSuppressLayout;
    }

    @Override // android.view.View
    public boolean gatherTransparentRegion(Region region) {
        boolean z = (this.mPrivateFlags & 512) == 0;
        if (z && region == null) {
            return true;
        }
        super.gatherTransparentRegion(region);
        View[] viewArr = this.mChildren;
        int i = this.mChildrenCount;
        boolean z2 = true;
        for (int i2 = 0; i2 < i; i2++) {
            View view = viewArr[i2];
            if (((view.mViewFlags & 12) == 0 || view.getAnimation() != null) && !view.gatherTransparentRegion(region)) {
                z2 = false;
            }
        }
        return z || z2;
    }

    @Override // android.view.ViewParent
    public void requestTransparentRegion(View view) {
        if (view != null) {
            view.mPrivateFlags |= 512;
            if (this.mParent != null) {
                this.mParent.requestTransparentRegion(this);
            }
        }
    }

    @Override // android.view.View
    protected boolean fitSystemWindows(Rect rect) {
        boolean zFitSystemWindows = super.fitSystemWindows(rect);
        if (!zFitSystemWindows) {
            int i = this.mChildrenCount;
            View[] viewArr = this.mChildren;
            for (int i2 = 0; i2 < i; i2++) {
                zFitSystemWindows = viewArr[i2].fitSystemWindows(rect);
                if (zFitSystemWindows) {
                    break;
                }
            }
        }
        return zFitSystemWindows;
    }

    public Animation.AnimationListener getLayoutAnimationListener() {
        return this.mAnimationListener;
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int i = this.mGroupFlags;
        if ((65536 & i) != 0) {
            if ((i & 8192) != 0) {
                throw new IllegalStateException("addStateFromChildren cannot be enabled if a child has duplicateParentState set to true");
            }
            View[] viewArr = this.mChildren;
            int i2 = this.mChildrenCount;
            for (int i3 = 0; i3 < i2; i3++) {
                View view = viewArr[i3];
                if ((view.mViewFlags & 4194304) != 0) {
                    view.refreshDrawableState();
                }
            }
        }
    }

    @Override // android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        View[] viewArr = this.mChildren;
        int i = this.mChildrenCount;
        for (int i2 = 0; i2 < i; i2++) {
            viewArr[i2].jumpDrawablesToCurrentState();
        }
    }

    @Override // android.view.View
    protected int[] onCreateDrawableState(int i) {
        if ((this.mGroupFlags & 8192) == 0) {
            return super.onCreateDrawableState(i);
        }
        int childCount = getChildCount();
        int length = 0;
        for (int i2 = 0; i2 < childCount; i2++) {
            int[] drawableState = getChildAt(i2).getDrawableState();
            if (drawableState != null) {
                length += drawableState.length;
            }
        }
        int[] iArrOnCreateDrawableState = super.onCreateDrawableState(i + length);
        for (int i3 = 0; i3 < childCount; i3++) {
            int[] drawableState2 = getChildAt(i3).getDrawableState();
            if (drawableState2 != null) {
                iArrOnCreateDrawableState = mergeDrawableStates(iArrOnCreateDrawableState, drawableState2);
            }
        }
        return iArrOnCreateDrawableState;
    }

    public void setAddStatesFromChildren(boolean z) {
        if (z) {
            this.mGroupFlags |= 8192;
        } else {
            this.mGroupFlags &= -8193;
        }
        refreshDrawableState();
    }

    public boolean addStatesFromChildren() {
        return (this.mGroupFlags & 8192) != 0;
    }

    @Override // android.view.ViewParent
    public void childDrawableStateChanged(View view) {
        if ((this.mGroupFlags & 8192) != 0) {
            refreshDrawableState();
        }
    }

    public void setLayoutAnimationListener(Animation.AnimationListener animationListener) {
        this.mAnimationListener = animationListener;
    }

    public void requestTransitionStart(LayoutTransition layoutTransition) {
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl != null) {
            viewRootImpl.requestTransitionStart(layoutTransition);
        }
    }

    @Override // android.view.View
    public boolean resolveRtlPropertiesIfNeeded() {
        boolean zResolveRtlPropertiesIfNeeded = super.resolveRtlPropertiesIfNeeded();
        if (zResolveRtlPropertiesIfNeeded) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt.isLayoutDirectionInherited()) {
                    childAt.resolveRtlPropertiesIfNeeded();
                }
            }
        }
        return zResolveRtlPropertiesIfNeeded;
    }

    @Override // android.view.View
    public boolean resolveLayoutDirection() {
        boolean zResolveLayoutDirection = super.resolveLayoutDirection();
        if (zResolveLayoutDirection) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt.isLayoutDirectionInherited()) {
                    childAt.resolveLayoutDirection();
                }
            }
        }
        return zResolveLayoutDirection;
    }

    @Override // android.view.View
    public boolean resolveTextDirection() {
        boolean zResolveTextDirection = super.resolveTextDirection();
        if (zResolveTextDirection) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt.isTextDirectionInherited()) {
                    childAt.resolveTextDirection();
                }
            }
        }
        return zResolveTextDirection;
    }

    @Override // android.view.View
    public boolean resolveTextAlignment() {
        boolean zResolveTextAlignment = super.resolveTextAlignment();
        if (zResolveTextAlignment) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt.isTextAlignmentInherited()) {
                    childAt.resolveTextAlignment();
                }
            }
        }
        return zResolveTextAlignment;
    }

    @Override // android.view.View
    public void resolvePadding() {
        super.resolvePadding();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.isLayoutDirectionInherited()) {
                childAt.resolvePadding();
            }
        }
    }

    @Override // android.view.View
    protected void resolveDrawables() {
        super.resolveDrawables();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.isLayoutDirectionInherited()) {
                childAt.resolveDrawables();
            }
        }
    }

    @Override // android.view.View
    public void resolveLayoutParams() {
        super.resolveLayoutParams();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).resolveLayoutParams();
        }
    }

    @Override // android.view.View
    public void resetResolvedLayoutDirection() {
        super.resetResolvedLayoutDirection();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.isLayoutDirectionInherited()) {
                childAt.resetResolvedLayoutDirection();
            }
        }
    }

    @Override // android.view.View
    public void resetResolvedTextDirection() {
        super.resetResolvedTextDirection();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.isTextDirectionInherited()) {
                childAt.resetResolvedTextDirection();
            }
        }
    }

    @Override // android.view.View
    public void resetResolvedTextAlignment() {
        super.resetResolvedTextAlignment();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.isTextAlignmentInherited()) {
                childAt.resetResolvedTextAlignment();
            }
        }
    }

    @Override // android.view.View
    public void resetResolvedPadding() {
        super.resetResolvedPadding();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.isLayoutDirectionInherited()) {
                childAt.resetResolvedPadding();
            }
        }
    }

    @Override // android.view.View
    protected void resetResolvedDrawables() {
        super.resetResolvedDrawables();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.isLayoutDirectionInherited()) {
                childAt.resetResolvedDrawables();
            }
        }
    }

    public static class LayoutParams {

        @Deprecated
        public static final int FILL_PARENT = -1;
        public static final int MATCH_PARENT = -1;
        public static final int WRAP_CONTENT = -2;

        @ViewDebug.ExportedProperty(category = "layout", mapping = {@ViewDebug.IntToString(from = -1, to = "MATCH_PARENT"), @ViewDebug.IntToString(from = -2, to = "WRAP_CONTENT")})
        public int height;
        public LayoutAnimationController.AnimationParameters layoutAnimationParameters;

        @ViewDebug.ExportedProperty(category = "layout", mapping = {@ViewDebug.IntToString(from = -1, to = "MATCH_PARENT"), @ViewDebug.IntToString(from = -2, to = "WRAP_CONTENT")})
        public int width;

        public void onDebugDraw(View view, Canvas canvas, Paint paint) {
        }

        public void resolveLayoutDirection(int i) {
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ViewGroup_Layout);
            setBaseAttributes(typedArrayObtainStyledAttributes, 0, 1);
            typedArrayObtainStyledAttributes.recycle();
        }

        public LayoutParams(int i, int i2) {
            this.width = i;
            this.height = i2;
        }

        public LayoutParams(LayoutParams layoutParams) {
            this.width = layoutParams.width;
            this.height = layoutParams.height;
        }

        LayoutParams() {
        }

        protected void setBaseAttributes(TypedArray typedArray, int i, int i2) {
            this.width = typedArray.getLayoutDimension(i, "layout_width");
            this.height = typedArray.getLayoutDimension(i2, "layout_height");
        }

        public String debug(String str) {
            return str + "ViewGroup.LayoutParams={ width=" + sizeToString(this.width) + ", height=" + sizeToString(this.height) + " }";
        }

        protected static String sizeToString(int i) {
            return i == -2 ? "wrap-content" : i == -1 ? "match-parent" : String.valueOf(i);
        }
    }

    public static class MarginLayoutParams extends LayoutParams {
        public static final int DEFAULT_MARGIN_RELATIVE = Integer.MIN_VALUE;
        private static final int DEFAULT_MARGIN_RESOLVED = 0;
        private static final int LAYOUT_DIRECTION_MASK = 3;
        private static final int LEFT_MARGIN_UNDEFINED_MASK = 4;
        private static final int NEED_RESOLUTION_MASK = 32;
        private static final int RIGHT_MARGIN_UNDEFINED_MASK = 8;
        private static final int RTL_COMPATIBILITY_MODE_MASK = 16;
        private static final int UNDEFINED_MARGIN = Integer.MIN_VALUE;

        @ViewDebug.ExportedProperty(category = "layout")
        public int bottomMargin;

        @ViewDebug.ExportedProperty(category = "layout")
        private int endMargin;

        @ViewDebug.ExportedProperty(category = "layout")
        public int leftMargin;

        @ViewDebug.ExportedProperty(category = "layout", flagMapping = {@ViewDebug.FlagToString(equals = 3, mask = 3, name = "LAYOUT_DIRECTION"), @ViewDebug.FlagToString(equals = 4, mask = 4, name = "LEFT_MARGIN_UNDEFINED_MASK"), @ViewDebug.FlagToString(equals = 8, mask = 8, name = "RIGHT_MARGIN_UNDEFINED_MASK"), @ViewDebug.FlagToString(equals = 16, mask = 16, name = "RTL_COMPATIBILITY_MODE_MASK"), @ViewDebug.FlagToString(equals = 32, mask = 32, name = "NEED_RESOLUTION_MASK")})
        byte mMarginFlags;

        @ViewDebug.ExportedProperty(category = "layout")
        public int rightMargin;

        @ViewDebug.ExportedProperty(category = "layout")
        private int startMargin;

        @ViewDebug.ExportedProperty(category = "layout")
        public int topMargin;

        public MarginLayoutParams(Context context, AttributeSet attributeSet) {
            this.startMargin = Integer.MIN_VALUE;
            this.endMargin = Integer.MIN_VALUE;
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ViewGroup_MarginLayout);
            setBaseAttributes(typedArrayObtainStyledAttributes, 0, 1);
            int dimensionPixelSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(2, -1);
            if (dimensionPixelSize >= 0) {
                this.leftMargin = dimensionPixelSize;
                this.topMargin = dimensionPixelSize;
                this.rightMargin = dimensionPixelSize;
                this.bottomMargin = dimensionPixelSize;
            } else {
                int dimensionPixelSize2 = typedArrayObtainStyledAttributes.getDimensionPixelSize(3, Integer.MIN_VALUE);
                this.leftMargin = dimensionPixelSize2;
                if (dimensionPixelSize2 == Integer.MIN_VALUE) {
                    this.mMarginFlags = (byte) (this.mMarginFlags | 4);
                    this.leftMargin = 0;
                }
                int dimensionPixelSize3 = typedArrayObtainStyledAttributes.getDimensionPixelSize(5, Integer.MIN_VALUE);
                this.rightMargin = dimensionPixelSize3;
                if (dimensionPixelSize3 == Integer.MIN_VALUE) {
                    this.mMarginFlags = (byte) (this.mMarginFlags | 8);
                    this.rightMargin = 0;
                }
                this.topMargin = typedArrayObtainStyledAttributes.getDimensionPixelSize(4, 0);
                this.bottomMargin = typedArrayObtainStyledAttributes.getDimensionPixelSize(6, 0);
                this.startMargin = typedArrayObtainStyledAttributes.getDimensionPixelSize(7, Integer.MIN_VALUE);
                this.endMargin = typedArrayObtainStyledAttributes.getDimensionPixelSize(8, Integer.MIN_VALUE);
                if (isMarginRelative()) {
                    this.mMarginFlags = (byte) (this.mMarginFlags | 32);
                }
            }
            boolean zHasRtlSupport = context.getApplicationInfo().hasRtlSupport();
            if (context.getApplicationInfo().targetSdkVersion < 17 || !zHasRtlSupport) {
                this.mMarginFlags = (byte) (this.mMarginFlags | 16);
            }
            this.mMarginFlags = (byte) (this.mMarginFlags | 0);
            typedArrayObtainStyledAttributes.recycle();
        }

        public MarginLayoutParams(int i, int i2) {
            super(i, i2);
            this.startMargin = Integer.MIN_VALUE;
            this.endMargin = Integer.MIN_VALUE;
            byte b = (byte) (this.mMarginFlags | 4);
            this.mMarginFlags = b;
            byte b2 = (byte) (b | 8);
            this.mMarginFlags = b2;
            byte b3 = (byte) (b2 & (-33));
            this.mMarginFlags = b3;
            this.mMarginFlags = (byte) (b3 & (-17));
        }

        public MarginLayoutParams(MarginLayoutParams marginLayoutParams) {
            this.startMargin = Integer.MIN_VALUE;
            this.endMargin = Integer.MIN_VALUE;
            this.width = marginLayoutParams.width;
            this.height = marginLayoutParams.height;
            this.leftMargin = marginLayoutParams.leftMargin;
            this.topMargin = marginLayoutParams.topMargin;
            this.rightMargin = marginLayoutParams.rightMargin;
            this.bottomMargin = marginLayoutParams.bottomMargin;
            this.startMargin = marginLayoutParams.startMargin;
            this.endMargin = marginLayoutParams.endMargin;
            this.mMarginFlags = marginLayoutParams.mMarginFlags;
        }

        public MarginLayoutParams(LayoutParams layoutParams) {
            super(layoutParams);
            this.startMargin = Integer.MIN_VALUE;
            this.endMargin = Integer.MIN_VALUE;
            byte b = (byte) (this.mMarginFlags | 4);
            this.mMarginFlags = b;
            byte b2 = (byte) (b | 8);
            this.mMarginFlags = b2;
            byte b3 = (byte) (b2 & (-33));
            this.mMarginFlags = b3;
            this.mMarginFlags = (byte) (b3 & (-17));
        }

        public void setMargins(int i, int i2, int i3, int i4) {
            this.leftMargin = i;
            this.topMargin = i2;
            this.rightMargin = i3;
            this.bottomMargin = i4;
            byte b = (byte) (this.mMarginFlags & (-5));
            this.mMarginFlags = b;
            this.mMarginFlags = (byte) (b & (-9));
            if (isMarginRelative()) {
                this.mMarginFlags = (byte) (this.mMarginFlags | 32);
            } else {
                this.mMarginFlags = (byte) (this.mMarginFlags & (-33));
            }
        }

        public void setMarginsRelative(int i, int i2, int i3, int i4) {
            this.startMargin = i;
            this.topMargin = i2;
            this.endMargin = i3;
            this.bottomMargin = i4;
            this.mMarginFlags = (byte) (this.mMarginFlags | 32);
        }

        public void setMarginStart(int i) {
            this.startMargin = i;
            this.mMarginFlags = (byte) (this.mMarginFlags | 32);
        }

        public int getMarginStart() {
            int i = this.startMargin;
            if (i != Integer.MIN_VALUE) {
                return i;
            }
            if ((this.mMarginFlags & 32) == 32) {
                doResolveMargins();
            }
            if ((this.mMarginFlags & 3) == 1) {
                return this.rightMargin;
            }
            return this.leftMargin;
        }

        public void setMarginEnd(int i) {
            this.endMargin = i;
            this.mMarginFlags = (byte) (this.mMarginFlags | 32);
        }

        public int getMarginEnd() {
            int i = this.endMargin;
            if (i != Integer.MIN_VALUE) {
                return i;
            }
            if ((this.mMarginFlags & 32) == 32) {
                doResolveMargins();
            }
            if ((this.mMarginFlags & 3) == 1) {
                return this.leftMargin;
            }
            return this.rightMargin;
        }

        public boolean isMarginRelative() {
            return (this.startMargin == Integer.MIN_VALUE && this.endMargin == Integer.MIN_VALUE) ? false : true;
        }

        public void setLayoutDirection(int i) {
            if (i == 0 || i == 1) {
                byte b = this.mMarginFlags;
                if (i != (b & 3)) {
                    byte b2 = (byte) (b & (-4));
                    this.mMarginFlags = b2;
                    this.mMarginFlags = (byte) ((i & 3) | b2);
                    if (isMarginRelative()) {
                        this.mMarginFlags = (byte) (this.mMarginFlags | 32);
                    } else {
                        this.mMarginFlags = (byte) (this.mMarginFlags & (-33));
                    }
                }
            }
        }

        public int getLayoutDirection() {
            return this.mMarginFlags & 3;
        }

        @Override // android.view.ViewGroup.LayoutParams
        public void resolveLayoutDirection(int i) {
            setLayoutDirection(i);
            if (isMarginRelative() && (this.mMarginFlags & 32) == 32) {
                doResolveMargins();
            }
        }

        private void doResolveMargins() {
            int i;
            int i2;
            byte b = this.mMarginFlags;
            if ((b & 16) == 16) {
                if ((b & 4) == 4 && (i2 = this.startMargin) > Integer.MIN_VALUE) {
                    this.leftMargin = i2;
                }
                if ((b & 8) == 8 && (i = this.endMargin) > Integer.MIN_VALUE) {
                    this.rightMargin = i;
                }
            } else {
                if ((b & 3) == 1) {
                    int i3 = this.endMargin;
                    if (i3 <= Integer.MIN_VALUE) {
                        i3 = 0;
                    }
                    this.leftMargin = i3;
                    int i4 = this.startMargin;
                    this.rightMargin = i4 > Integer.MIN_VALUE ? i4 : 0;
                } else {
                    int i5 = this.startMargin;
                    if (i5 <= Integer.MIN_VALUE) {
                        i5 = 0;
                    }
                    this.leftMargin = i5;
                    int i6 = this.endMargin;
                    this.rightMargin = i6 > Integer.MIN_VALUE ? i6 : 0;
                }
            }
            this.mMarginFlags = (byte) (b & (-33));
        }

        public boolean isLayoutRtl() {
            return (this.mMarginFlags & 3) == 1;
        }

        @Override // android.view.ViewGroup.LayoutParams
        public void onDebugDraw(View view, Canvas canvas, Paint paint) {
            Insets opticalInsets = View.isLayoutModeOptical(view.mParent) ? view.getOpticalInsets() : Insets.NONE;
            ViewGroup.fillDifference(canvas, view.getLeft() + opticalInsets.left, view.getTop() + opticalInsets.top, view.getRight() - opticalInsets.right, view.getBottom() - opticalInsets.bottom, this.leftMargin, this.topMargin, this.rightMargin, this.bottomMargin, paint);
        }
    }

    private static final class TouchTarget {
        public static final int ALL_POINTER_IDS = -1;
        private static final int MAX_RECYCLED = 32;
        private static TouchTarget sRecycleBin;
        private static final Object sRecycleLock = new Object[0];
        private static int sRecycledCount;
        public View child;
        public TouchTarget next;
        public int pointerIdBits;

        private TouchTarget() {
        }

        public static TouchTarget obtain(View view, int i) {
            TouchTarget touchTarget;
            synchronized (sRecycleLock) {
                touchTarget = sRecycleBin;
                if (touchTarget == null) {
                    touchTarget = new TouchTarget();
                } else {
                    sRecycleBin = touchTarget.next;
                    sRecycledCount--;
                    touchTarget.next = null;
                }
            }
            touchTarget.child = view;
            touchTarget.pointerIdBits = i;
            return touchTarget;
        }

        public void recycle() {
            synchronized (sRecycleLock) {
                int i = sRecycledCount;
                if (i < 32) {
                    this.next = sRecycleBin;
                    sRecycleBin = this;
                    sRecycledCount = i + 1;
                } else {
                    this.next = null;
                }
                this.child = null;
            }
        }
    }

    private static final class HoverTarget {
        private static final int MAX_RECYCLED = 32;
        private static HoverTarget sRecycleBin;
        private static final Object sRecycleLock = new Object[0];
        private static int sRecycledCount;
        public View child;
        public HoverTarget next;

        private HoverTarget() {
        }

        public static HoverTarget obtain(View view) {
            HoverTarget hoverTarget;
            synchronized (sRecycleLock) {
                hoverTarget = sRecycleBin;
                if (hoverTarget == null) {
                    hoverTarget = new HoverTarget();
                } else {
                    sRecycleBin = hoverTarget.next;
                    sRecycledCount--;
                    hoverTarget.next = null;
                }
            }
            hoverTarget.child = view;
            return hoverTarget;
        }

        public void recycle() {
            synchronized (sRecycleLock) {
                int i = sRecycledCount;
                if (i < 32) {
                    this.next = sRecycleBin;
                    sRecycleBin = this;
                    sRecycledCount = i + 1;
                } else {
                    this.next = null;
                }
                this.child = null;
            }
        }
    }

    static class ChildListForAccessibility {
        private static final int MAX_POOL_SIZE = 32;
        private static final Pools.SynchronizedPool<ChildListForAccessibility> sPool = new Pools.SynchronizedPool<>(32);
        private final ArrayList<View> mChildren = new ArrayList<>();
        private final ArrayList<ViewLocationHolder> mHolders = new ArrayList<>();

        ChildListForAccessibility() {
        }

        public static ChildListForAccessibility obtain(ViewGroup viewGroup, boolean z) {
            ChildListForAccessibility childListForAccessibilityAcquire = sPool.acquire();
            if (childListForAccessibilityAcquire == null) {
                childListForAccessibilityAcquire = new ChildListForAccessibility();
            }
            childListForAccessibilityAcquire.init(viewGroup, z);
            return childListForAccessibilityAcquire;
        }

        public void recycle() {
            clear();
            sPool.release(this);
        }

        public int getChildCount() {
            return this.mChildren.size();
        }

        public View getChildAt(int i) {
            return this.mChildren.get(i);
        }

        public int getChildIndex(View view) {
            return this.mChildren.indexOf(view);
        }

        private void init(ViewGroup viewGroup, boolean z) {
            ArrayList<View> arrayList = this.mChildren;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                arrayList.add(viewGroup.getChildAt(i));
            }
            if (z) {
                ArrayList<ViewLocationHolder> arrayList2 = this.mHolders;
                for (int i2 = 0; i2 < childCount; i2++) {
                    arrayList2.add(ViewLocationHolder.obtain(viewGroup, arrayList.get(i2)));
                }
                Collections.sort(arrayList2);
                for (int i3 = 0; i3 < childCount; i3++) {
                    ViewLocationHolder viewLocationHolder = arrayList2.get(i3);
                    arrayList.set(i3, viewLocationHolder.mView);
                    viewLocationHolder.recycle();
                }
                arrayList2.clear();
            }
        }

        private void clear() {
            this.mChildren.clear();
        }
    }

    static class ViewLocationHolder implements Comparable<ViewLocationHolder> {
        private static final int MAX_POOL_SIZE = 32;
        private static final Pools.SynchronizedPool<ViewLocationHolder> sPool = new Pools.SynchronizedPool<>(32);
        private int mLayoutDirection;
        private final Rect mLocation = new Rect();
        public View mView;

        ViewLocationHolder() {
        }

        public static ViewLocationHolder obtain(ViewGroup viewGroup, View view) {
            ViewLocationHolder viewLocationHolderAcquire = sPool.acquire();
            if (viewLocationHolderAcquire == null) {
                viewLocationHolderAcquire = new ViewLocationHolder();
            }
            viewLocationHolderAcquire.init(viewGroup, view);
            return viewLocationHolderAcquire;
        }

        public void recycle() {
            clear();
            sPool.release(this);
        }

        @Override // java.lang.Comparable
        public int compareTo(ViewLocationHolder viewLocationHolder) {
            if (viewLocationHolder == null || getClass() != viewLocationHolder.getClass()) {
                return 1;
            }
            if (this.mLocation.bottom - viewLocationHolder.mLocation.top <= 0) {
                return -1;
            }
            if (this.mLocation.top - viewLocationHolder.mLocation.bottom >= 0) {
                return 1;
            }
            if (this.mLayoutDirection == 0) {
                int i = this.mLocation.left - viewLocationHolder.mLocation.left;
                if (i != 0) {
                    return i;
                }
            } else {
                int i2 = this.mLocation.right - viewLocationHolder.mLocation.right;
                if (i2 != 0) {
                    return -i2;
                }
            }
            int i3 = this.mLocation.top - viewLocationHolder.mLocation.top;
            if (i3 != 0) {
                return i3;
            }
            int iHeight = this.mLocation.height() - viewLocationHolder.mLocation.height();
            if (iHeight != 0) {
                return -iHeight;
            }
            int iWidth = this.mLocation.width() - viewLocationHolder.mLocation.width();
            return iWidth != 0 ? -iWidth : this.mView.getAccessibilityViewId() - viewLocationHolder.mView.getAccessibilityViewId();
        }

        private void init(ViewGroup viewGroup, View view) {
            Rect rect = this.mLocation;
            view.getDrawingRect(rect);
            viewGroup.offsetDescendantRectToMyCoords(view, rect);
            this.mView = view;
            this.mLayoutDirection = viewGroup.getLayoutDirection();
        }

        private void clear() {
            this.mView = null;
            this.mLocation.set(0, 0, 0, 0);
        }
    }

    private static Paint getDebugPaint() {
        if (sDebugPaint == null) {
            Paint paint = new Paint();
            sDebugPaint = paint;
            paint.setAntiAlias(false);
        }
        return sDebugPaint;
    }

    private static void drawRect(Canvas canvas, Paint paint, int i, int i2, int i3, int i4) {
        if (sDebugLines == null) {
            sDebugLines = new float[16];
        }
        float[] fArr = sDebugLines;
        float f = i;
        fArr[0] = f;
        float f2 = i2;
        fArr[1] = f2;
        float f3 = i3;
        fArr[2] = f3;
        fArr[3] = f2;
        fArr[4] = f3;
        fArr[5] = f2;
        fArr[6] = f3;
        float f4 = i4;
        fArr[7] = f4;
        fArr[8] = f3;
        fArr[9] = f4;
        fArr[10] = f;
        fArr[11] = f4;
        fArr[12] = f;
        fArr[13] = f4;
        fArr[14] = f;
        fArr[15] = f2;
        canvas.drawLines(fArr, paint);
    }
}
