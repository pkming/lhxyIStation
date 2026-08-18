package android.view;

import android.app.AppGlobals;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.SparseArray;

/* JADX INFO: loaded from: classes.dex */
public class ViewConfiguration {
    private static final int DEFAULT_LONG_PRESS_TIMEOUT = 500;
    private static final int DOUBLE_TAP_MIN_TIME = 40;
    private static final int DOUBLE_TAP_SLOP = 100;
    private static final int DOUBLE_TAP_TIMEOUT = 300;
    private static final int DOUBLE_TAP_TOUCH_SLOP = 8;
    private static final int EDGE_SLOP = 12;
    private static final int FADING_EDGE_LENGTH = 12;
    private static final int GLOBAL_ACTIONS_KEY_TIMEOUT = 500;
    private static final int HOVER_TAP_SLOP = 20;
    private static final int HOVER_TAP_TIMEOUT = 150;
    private static final int JUMP_TAP_TIMEOUT = 500;
    private static final int KEY_REPEAT_DELAY = 100;

    @Deprecated
    private static final int MAXIMUM_DRAWING_CACHE_SIZE = 1536000;
    private static final int MAXIMUM_FLING_VELOCITY = 8000;
    private static final int MINIMUM_FLING_VELOCITY = 50;
    private static final int OVERFLING_DISTANCE = 6;
    private static final int OVERSCROLL_DISTANCE = 0;
    private static final int PAGING_TOUCH_SLOP = 16;
    private static final int PRESSED_STATE_DURATION = 64;
    private static final int SCROLL_BAR_DEFAULT_DELAY = 300;
    private static final int SCROLL_BAR_FADE_DURATION = 250;
    private static final int SCROLL_BAR_SIZE = 10;
    private static final float SCROLL_FRICTION = 0.015f;
    private static final long SEND_RECURRING_ACCESSIBILITY_EVENTS_INTERVAL_MILLIS = 100;
    private static final int TAP_TIMEOUT = 180;
    private static final int TOUCH_SLOP = 8;
    private static final int WINDOW_TOUCH_SLOP = 16;
    private static final int ZOOM_CONTROLS_TIMEOUT = 3000;
    static final SparseArray<ViewConfiguration> sConfigurations = new SparseArray<>(2);
    private final int mDoubleTapSlop;
    private final int mDoubleTapTouchSlop;
    private final int mEdgeSlop;
    private final int mFadingEdgeLength;
    private final boolean mFadingMarqueeEnabled;
    private final int mMaximumDrawingCacheSize;
    private final int mMaximumFlingVelocity;
    private final int mMinimumFlingVelocity;
    private final int mOverflingDistance;
    private final int mOverscrollDistance;
    private final int mPagingTouchSlop;
    private final int mScrollbarSize;
    private final int mTouchSlop;
    private final int mWindowTouchSlop;
    private boolean sHasPermanentMenuKey;
    private boolean sHasPermanentMenuKeySet;

    public static int getDoubleTapMinTime() {
        return 40;
    }

    @Deprecated
    public static int getDoubleTapSlop() {
        return 100;
    }

    public static int getDoubleTapTimeout() {
        return 300;
    }

    @Deprecated
    public static int getEdgeSlop() {
        return 12;
    }

    @Deprecated
    public static int getFadingEdgeLength() {
        return 12;
    }

    public static long getGlobalActionKeyTimeout() {
        return 500L;
    }

    public static int getHoverTapSlop() {
        return 20;
    }

    public static int getHoverTapTimeout() {
        return 150;
    }

    public static int getJumpTapTimeout() {
        return 500;
    }

    public static int getKeyRepeatDelay() {
        return 100;
    }

    @Deprecated
    public static int getMaximumDrawingCacheSize() {
        return MAXIMUM_DRAWING_CACHE_SIZE;
    }

    @Deprecated
    public static int getMaximumFlingVelocity() {
        return 8000;
    }

    @Deprecated
    public static int getMinimumFlingVelocity() {
        return 50;
    }

    public static int getPressedStateDuration() {
        return 64;
    }

    public static int getScrollBarFadeDuration() {
        return 250;
    }

    @Deprecated
    public static int getScrollBarSize() {
        return 10;
    }

    public static int getScrollDefaultDelay() {
        return 300;
    }

    public static float getScrollFriction() {
        return SCROLL_FRICTION;
    }

    public static long getSendRecurringAccessibilityEventsInterval() {
        return SEND_RECURRING_ACCESSIBILITY_EVENTS_INTERVAL_MILLIS;
    }

    public static int getTapTimeout() {
        return 180;
    }

    @Deprecated
    public static int getTouchSlop() {
        return 8;
    }

    @Deprecated
    public static int getWindowTouchSlop() {
        return 16;
    }

    public static long getZoomControlsTimeout() {
        return 3000L;
    }

    @Deprecated
    public ViewConfiguration() {
        this.mEdgeSlop = 12;
        this.mFadingEdgeLength = 12;
        this.mMinimumFlingVelocity = 50;
        this.mMaximumFlingVelocity = 8000;
        this.mScrollbarSize = 10;
        this.mTouchSlop = 8;
        this.mDoubleTapTouchSlop = 8;
        this.mPagingTouchSlop = 16;
        this.mDoubleTapSlop = 100;
        this.mWindowTouchSlop = 16;
        this.mMaximumDrawingCacheSize = MAXIMUM_DRAWING_CACHE_SIZE;
        this.mOverscrollDistance = 0;
        this.mOverflingDistance = 6;
        this.mFadingMarqueeEnabled = true;
    }

    private ViewConfiguration(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        float f = displayMetrics.density;
        float f2 = configuration.isLayoutSizeAtLeast(4) ? 1.5f * f : f;
        int i = (int) ((12.0f * f2) + 0.5f);
        this.mEdgeSlop = i;
        this.mFadingEdgeLength = i;
        this.mMinimumFlingVelocity = (int) ((50.0f * f) + 0.5f);
        this.mMaximumFlingVelocity = (int) ((8000.0f * f) + 0.5f);
        this.mScrollbarSize = (int) ((f * 10.0f) + 0.5f);
        this.mDoubleTapSlop = (int) ((100.0f * f2) + 0.5f);
        this.mWindowTouchSlop = (int) ((16.0f * f2) + 0.5f);
        Display defaultDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        this.mMaximumDrawingCacheSize = point.x * 4 * point.y;
        this.mOverscrollDistance = (int) ((0.0f * f2) + 0.5f);
        this.mOverflingDistance = (int) ((f2 * 6.0f) + 0.5f);
        if (!this.sHasPermanentMenuKeySet) {
            try {
                this.sHasPermanentMenuKey = !WindowManagerGlobal.getWindowManagerService().hasNavigationBar();
                this.sHasPermanentMenuKeySet = true;
            } catch (RemoteException unused) {
                this.sHasPermanentMenuKey = false;
            }
        }
        this.mFadingMarqueeEnabled = resources.getBoolean(17891348);
        int dimensionPixelSize = resources.getDimensionPixelSize(17104904);
        this.mTouchSlop = dimensionPixelSize;
        this.mPagingTouchSlop = dimensionPixelSize * 2;
        this.mDoubleTapTouchSlop = dimensionPixelSize;
    }

    public static ViewConfiguration get(Context context) {
        int i = (int) (context.getResources().getDisplayMetrics().density * 100.0f);
        SparseArray<ViewConfiguration> sparseArray = sConfigurations;
        ViewConfiguration viewConfiguration = sparseArray.get(i);
        if (viewConfiguration != null) {
            return viewConfiguration;
        }
        ViewConfiguration viewConfiguration2 = new ViewConfiguration(context);
        sparseArray.put(i, viewConfiguration2);
        return viewConfiguration2;
    }

    public int getScaledScrollBarSize() {
        return this.mScrollbarSize;
    }

    public int getScaledFadingEdgeLength() {
        return this.mFadingEdgeLength;
    }

    public static int getLongPressTimeout() {
        return AppGlobals.getIntCoreSetting(Settings.Secure.LONG_PRESS_TIMEOUT, 500);
    }

    public static int getKeyRepeatTimeout() {
        return getLongPressTimeout();
    }

    public int getScaledEdgeSlop() {
        return this.mEdgeSlop;
    }

    public int getScaledTouchSlop() {
        return this.mTouchSlop;
    }

    public int getScaledDoubleTapTouchSlop() {
        return this.mDoubleTapTouchSlop;
    }

    public int getScaledPagingTouchSlop() {
        return this.mPagingTouchSlop;
    }

    public int getScaledDoubleTapSlop() {
        return this.mDoubleTapSlop;
    }

    public int getScaledWindowTouchSlop() {
        return this.mWindowTouchSlop;
    }

    public int getScaledMinimumFlingVelocity() {
        return this.mMinimumFlingVelocity;
    }

    public int getScaledMaximumFlingVelocity() {
        return this.mMaximumFlingVelocity;
    }

    public int getScaledMaximumDrawingCacheSize() {
        return this.mMaximumDrawingCacheSize;
    }

    public int getScaledOverscrollDistance() {
        return this.mOverscrollDistance;
    }

    public int getScaledOverflingDistance() {
        return this.mOverflingDistance;
    }

    public boolean hasPermanentMenuKey() {
        return this.sHasPermanentMenuKey;
    }

    public boolean isFadingMarqueeEnabled() {
        return this.mFadingMarqueeEnabled;
    }
}
