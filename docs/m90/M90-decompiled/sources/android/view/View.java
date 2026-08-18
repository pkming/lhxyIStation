package android.view;

import android.R;
import android.content.ClipData;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.Interpolator;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManagerGlobal;
import android.media.AudioSystem;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatProperty;
import android.util.Log;
import android.util.LongSparseLongArray;
import android.util.Pools;
import android.util.Property;
import android.util.SparseArray;
import android.util.SuperNotCalledException;
import android.util.TypedValue;
import android.view.AccessibilityIterators;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityEventSource;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.ScrollBarDrawable;
import com.android.internal.util.Predicate;
import com.android.internal.view.menu.MenuBuilder;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tools.zip.UnixStat;

/* JADX INFO: loaded from: classes.dex */
public class View implements Drawable.Callback, KeyEvent.Callback, AccessibilityEventSource {
    public static final int ACCESSIBILITY_CURSOR_POSITION_UNDEFINED = -1;
    public static final int ACCESSIBILITY_LIVE_REGION_ASSERTIVE = 2;
    static final int ACCESSIBILITY_LIVE_REGION_DEFAULT = 0;
    public static final int ACCESSIBILITY_LIVE_REGION_NONE = 0;
    public static final int ACCESSIBILITY_LIVE_REGION_POLITE = 1;
    static final int ALL_RTL_PROPERTIES_RESOLVED = 1610678816;
    public static final Property<View, Float> ALPHA;
    static final int CLICKABLE = 16384;
    private static final boolean DBG = false;
    public static final String DEBUG_LAYOUT_PROPERTY = "debug.layout";
    static final int DISABLED = 32;
    public static final int DRAG_FLAG_GLOBAL = 1;
    static final int DRAG_MASK = 3;
    static final int DRAWING_CACHE_ENABLED = 32768;
    public static final int DRAWING_CACHE_QUALITY_AUTO = 0;
    public static final int DRAWING_CACHE_QUALITY_HIGH = 1048576;
    public static final int DRAWING_CACHE_QUALITY_LOW = 524288;
    static final int DRAWING_CACHE_QUALITY_MASK = 1572864;
    static final int DRAW_MASK = 128;
    static final int DUPLICATE_PARENT_STATE = 4194304;
    protected static final int[] EMPTY_STATE_SET;
    static final int ENABLED = 0;
    protected static final int[] ENABLED_FOCUSED_SELECTED_STATE_SET;
    protected static final int[] ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] ENABLED_FOCUSED_STATE_SET;
    protected static final int[] ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET;
    static final int ENABLED_MASK = 32;
    protected static final int[] ENABLED_SELECTED_STATE_SET;
    protected static final int[] ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] ENABLED_STATE_SET;
    protected static final int[] ENABLED_WINDOW_FOCUSED_STATE_SET;
    static final int FADING_EDGE_HORIZONTAL = 4096;
    static final int FADING_EDGE_MASK = 12288;
    static final int FADING_EDGE_NONE = 0;
    static final int FADING_EDGE_VERTICAL = 8192;
    static final int FILTER_TOUCHES_WHEN_OBSCURED = 1024;
    public static final int FIND_VIEWS_WITH_ACCESSIBILITY_NODE_PROVIDERS = 4;
    public static final int FIND_VIEWS_WITH_CONTENT_DESCRIPTION = 2;
    public static final int FIND_VIEWS_WITH_TEXT = 1;
    private static final int FITS_SYSTEM_WINDOWS = 2;
    private static final int FOCUSABLE = 1;
    public static final int FOCUSABLES_ALL = 0;
    public static final int FOCUSABLES_TOUCH_MODE = 1;
    static final int FOCUSABLE_IN_TOUCH_MODE = 262144;
    private static final int FOCUSABLE_MASK = 1;
    protected static final int[] FOCUSED_SELECTED_STATE_SET;
    protected static final int[] FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] FOCUSED_STATE_SET;
    protected static final int[] FOCUSED_WINDOW_FOCUSED_STATE_SET;
    public static final int FOCUS_BACKWARD = 1;
    public static final int FOCUS_DOWN = 130;
    public static final int FOCUS_FORWARD = 2;
    public static final int FOCUS_LEFT = 17;
    public static final int FOCUS_RIGHT = 66;
    public static final int FOCUS_UP = 33;
    public static final int GONE = 8;
    public static final int HAPTIC_FEEDBACK_ENABLED = 268435456;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_AUTO = 0;
    static final int IMPORTANT_FOR_ACCESSIBILITY_DEFAULT = 0;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_NO = 2;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS = 4;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_YES = 1;
    public static final int INVISIBLE = 4;
    public static final int KEEP_SCREEN_ON = 67108864;
    public static final int LAYER_TYPE_HARDWARE = 2;
    public static final int LAYER_TYPE_NONE = 0;
    public static final int LAYER_TYPE_SOFTWARE = 1;
    private static final int LAYOUT_DIRECTION_DEFAULT = 2;
    private static final int[] LAYOUT_DIRECTION_FLAGS;
    public static final int LAYOUT_DIRECTION_INHERIT = 2;
    public static final int LAYOUT_DIRECTION_LOCALE = 3;
    public static final int LAYOUT_DIRECTION_LTR = 0;
    static final int LAYOUT_DIRECTION_RESOLVED_DEFAULT = 0;
    public static final int LAYOUT_DIRECTION_RTL = 1;
    static final int LONG_CLICKABLE = 2097152;
    public static final int MEASURED_HEIGHT_STATE_SHIFT = 16;
    public static final int MEASURED_SIZE_MASK = 16777215;
    public static final int MEASURED_STATE_MASK = -16777216;
    public static final int MEASURED_STATE_TOO_SMALL = 16777216;
    public static final int NAVIGATION_BAR_TRANSIENT = 134217728;
    public static final int NAVIGATION_BAR_TRANSLUCENT = Integer.MIN_VALUE;
    public static final int NAVIGATION_BAR_UNHIDE = 536870912;
    private static final float NONZERO_EPSILON = 0.001f;
    private static final int NOT_FOCUSABLE = 0;
    public static final int NO_ID = -1;
    static final int OPTIONAL_FITS_SYSTEM_WINDOWS = 2048;
    public static final int OVER_SCROLL_ALWAYS = 0;
    public static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 1;
    public static final int OVER_SCROLL_NEVER = 2;
    static final int PARENT_SAVE_DISABLED = 536870912;
    static final int PARENT_SAVE_DISABLED_MASK = 536870912;
    static final int PFLAG2_ACCESSIBILITY_FOCUSED = 67108864;
    static final int PFLAG2_ACCESSIBILITY_LIVE_REGION_MASK = 25165824;
    static final int PFLAG2_ACCESSIBILITY_LIVE_REGION_SHIFT = 23;
    static final int PFLAG2_DRAG_CAN_ACCEPT = 1;
    static final int PFLAG2_DRAG_HOVERED = 2;
    static final int PFLAG2_DRAWABLE_RESOLVED = 1073741824;
    static final int PFLAG2_HAS_TRANSIENT_STATE = Integer.MIN_VALUE;
    static final int PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_MASK = 7340032;
    static final int PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_SHIFT = 20;
    static final int PFLAG2_LAYOUT_DIRECTION_MASK = 12;
    static final int PFLAG2_LAYOUT_DIRECTION_MASK_SHIFT = 2;
    static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED = 32;
    static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED_MASK = 48;
    static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED_RTL = 16;
    static final int PFLAG2_PADDING_RESOLVED = 536870912;
    static final int PFLAG2_SUBTREE_ACCESSIBILITY_STATE_CHANGED = 134217728;
    private static final int[] PFLAG2_TEXT_ALIGNMENT_FLAGS;
    static final int PFLAG2_TEXT_ALIGNMENT_MASK = 57344;
    static final int PFLAG2_TEXT_ALIGNMENT_MASK_SHIFT = 13;
    static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED = 65536;
    private static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED_DEFAULT = 131072;
    static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK = 917504;
    static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK_SHIFT = 17;
    private static final int[] PFLAG2_TEXT_DIRECTION_FLAGS;
    static final int PFLAG2_TEXT_DIRECTION_MASK = 448;
    static final int PFLAG2_TEXT_DIRECTION_MASK_SHIFT = 6;
    static final int PFLAG2_TEXT_DIRECTION_RESOLVED = 512;
    static final int PFLAG2_TEXT_DIRECTION_RESOLVED_DEFAULT = 1024;
    static final int PFLAG2_TEXT_DIRECTION_RESOLVED_MASK = 7168;
    static final int PFLAG2_TEXT_DIRECTION_RESOLVED_MASK_SHIFT = 10;
    static final int PFLAG2_VIEW_QUICK_REJECTED = 268435456;
    static final int PFLAG3_CALLED_SUPER = 16;
    static final int PFLAG3_IS_LAID_OUT = 4;
    static final int PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT = 8;
    static final int PFLAG3_VIEW_IS_ANIMATING_ALPHA = 2;
    static final int PFLAG3_VIEW_IS_ANIMATING_TRANSFORM = 1;
    static final int PFLAG_ACTIVATED = 1073741824;
    static final int PFLAG_ALPHA_SET = 262144;
    static final int PFLAG_ANIMATION_STARTED = 65536;
    private static final int PFLAG_AWAKEN_SCROLL_BARS_ON_ATTACH = 134217728;
    static final int PFLAG_CANCEL_NEXT_UP_EVENT = 67108864;
    static final int PFLAG_DIRTY = 2097152;
    static final int PFLAG_DIRTY_MASK = 6291456;
    static final int PFLAG_DIRTY_OPAQUE = 4194304;
    static final int PFLAG_DRAWABLE_STATE_DIRTY = 1024;
    static final int PFLAG_DRAWING_CACHE_VALID = 32768;
    static final int PFLAG_DRAWN = 32;
    static final int PFLAG_DRAW_ANIMATION = 64;
    static final int PFLAG_FOCUSED = 2;
    static final int PFLAG_FORCE_LAYOUT = 4096;
    static final int PFLAG_HAS_BOUNDS = 16;
    private static final int PFLAG_HOVERED = 268435456;
    static final int PFLAG_INVALIDATED = Integer.MIN_VALUE;
    static final int PFLAG_IS_ROOT_NAMESPACE = 8;
    static final int PFLAG_LAYOUT_REQUIRED = 8192;
    static final int PFLAG_MEASURED_DIMENSION_SET = 2048;
    static final int PFLAG_ONLY_DRAWS_BACKGROUND = 256;
    static final int PFLAG_OPAQUE_BACKGROUND = 8388608;
    static final int PFLAG_OPAQUE_MASK = 25165824;
    static final int PFLAG_OPAQUE_SCROLLBARS = 16777216;
    private static final int PFLAG_PIVOT_EXPLICITLY_SET = 536870912;
    private static final int PFLAG_PREPRESSED = 33554432;
    private static final int PFLAG_PRESSED = 16384;
    static final int PFLAG_REQUEST_TRANSPARENT_REGIONS = 512;
    private static final int PFLAG_SAVE_STATE_CALLED = 131072;
    static final int PFLAG_SCROLL_CONTAINER = 524288;
    static final int PFLAG_SCROLL_CONTAINER_ADDED = 1048576;
    static final int PFLAG_SELECTED = 4;
    static final int PFLAG_SKIP_DRAW = 128;
    static final int PFLAG_WANTS_FOCUS = 1;
    private static final int POPULATING_ACCESSIBILITY_EVENT_TYPES = 172479;
    protected static final int[] PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET;
    protected static final int[] PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_ENABLED_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_ENABLED_SELECTED_STATE_SET;
    protected static final int[] PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_ENABLED_STATE_SET;
    protected static final int[] PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_FOCUSED_SELECTED_STATE_SET;
    protected static final int[] PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_SELECTED_STATE_SET;
    protected static final int[] PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    protected static final int[] PRESSED_STATE_SET;
    protected static final int[] PRESSED_WINDOW_FOCUSED_STATE_SET;
    public static final int PUBLIC_STATUS_BAR_VISIBILITY_MASK = 65535;
    public static final Property<View, Float> ROTATION;
    public static final Property<View, Float> ROTATION_X;
    public static final Property<View, Float> ROTATION_Y;
    static final int SAVE_DISABLED = 65536;
    static final int SAVE_DISABLED_MASK = 65536;
    public static final Property<View, Float> SCALE_X;
    public static final Property<View, Float> SCALE_Y;
    public static final int SCREEN_STATE_OFF = 0;
    public static final int SCREEN_STATE_ON = 1;
    static final int SCROLLBARS_HORIZONTAL = 256;
    static final int SCROLLBARS_INSET_MASK = 16777216;
    public static final int SCROLLBARS_INSIDE_INSET = 16777216;
    public static final int SCROLLBARS_INSIDE_OVERLAY = 0;
    static final int SCROLLBARS_MASK = 768;
    static final int SCROLLBARS_NONE = 0;
    public static final int SCROLLBARS_OUTSIDE_INSET = 50331648;
    static final int SCROLLBARS_OUTSIDE_MASK = 33554432;
    public static final int SCROLLBARS_OUTSIDE_OVERLAY = 33554432;
    static final int SCROLLBARS_STYLE_MASK = 50331648;
    static final int SCROLLBARS_VERTICAL = 512;
    public static final int SCROLLBAR_POSITION_DEFAULT = 0;
    public static final int SCROLLBAR_POSITION_LEFT = 1;
    public static final int SCROLLBAR_POSITION_RIGHT = 2;
    protected static final int[] SELECTED_STATE_SET;
    protected static final int[] SELECTED_WINDOW_FOCUSED_STATE_SET;
    public static final int SOUND_EFFECTS_ENABLED = 134217728;
    public static final int STATUS_BAR_DISABLE_BACK = 4194304;
    public static final int STATUS_BAR_DISABLE_CLOCK = 8388608;
    public static final int STATUS_BAR_DISABLE_EXPAND = 65536;
    public static final int STATUS_BAR_DISABLE_HOME = 2097152;
    public static final int STATUS_BAR_DISABLE_NOTIFICATION_ALERTS = 262144;
    public static final int STATUS_BAR_DISABLE_NOTIFICATION_ICONS = 131072;
    public static final int STATUS_BAR_DISABLE_NOTIFICATION_TICKER = 524288;
    public static final int STATUS_BAR_DISABLE_RECENT = 16777216;
    public static final int STATUS_BAR_DISABLE_SEARCH = 33554432;
    public static final int STATUS_BAR_DISABLE_SYSTEM_INFO = 1048576;
    public static final int STATUS_BAR_HIDDEN = 1;
    public static final int STATUS_BAR_TRANSIENT = 67108864;
    public static final int STATUS_BAR_TRANSLUCENT = 1073741824;
    public static final int STATUS_BAR_UNHIDE = 268435456;
    public static final int STATUS_BAR_VISIBLE = 0;
    public static final int SYSTEM_UI_CLEARABLE_FLAGS = 7;
    public static final int SYSTEM_UI_FLAG_FULLSCREEN = 4;
    public static final int SYSTEM_UI_FLAG_HIDE_NAVIGATION = 2;
    public static final int SYSTEM_UI_FLAG_IMMERSIVE = 2048;
    public static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 4096;
    public static final int SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN = 1024;
    public static final int SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION = 512;
    public static final int SYSTEM_UI_FLAG_LAYOUT_STABLE = 256;
    public static final int SYSTEM_UI_FLAG_LOW_PROFILE = 1;
    public static final int SYSTEM_UI_FLAG_VISIBLE = 0;
    public static final int SYSTEM_UI_LAYOUT_FLAGS = 1536;
    public static final int TEXT_ALIGNMENT_CENTER = 4;
    private static final int TEXT_ALIGNMENT_DEFAULT = 1;
    public static final int TEXT_ALIGNMENT_GRAVITY = 1;
    public static final int TEXT_ALIGNMENT_INHERIT = 0;
    static final int TEXT_ALIGNMENT_RESOLVED_DEFAULT = 1;
    public static final int TEXT_ALIGNMENT_TEXT_END = 3;
    public static final int TEXT_ALIGNMENT_TEXT_START = 2;
    public static final int TEXT_ALIGNMENT_VIEW_END = 6;
    public static final int TEXT_ALIGNMENT_VIEW_START = 5;
    public static final int TEXT_DIRECTION_ANY_RTL = 2;
    private static final int TEXT_DIRECTION_DEFAULT = 0;
    public static final int TEXT_DIRECTION_FIRST_STRONG = 1;
    public static final int TEXT_DIRECTION_INHERIT = 0;
    public static final int TEXT_DIRECTION_LOCALE = 5;
    public static final int TEXT_DIRECTION_LTR = 3;
    static final int TEXT_DIRECTION_RESOLVED_DEFAULT = 1;
    public static final int TEXT_DIRECTION_RTL = 4;
    public static final Property<View, Float> TRANSLATION_X;
    public static final Property<View, Float> TRANSLATION_Y;
    private static final int UNDEFINED_PADDING = Integer.MIN_VALUE;
    protected static final String VIEW_LOG_TAG = "View";
    static final int VIEW_STATE_ACCELERATED = 64;
    static final int VIEW_STATE_ACTIVATED = 32;
    static final int VIEW_STATE_DRAG_CAN_ACCEPT = 256;
    static final int VIEW_STATE_DRAG_HOVERED = 512;
    static final int VIEW_STATE_ENABLED = 8;
    static final int VIEW_STATE_FOCUSED = 4;
    static final int VIEW_STATE_HOVERED = 128;
    static final int[] VIEW_STATE_IDS;
    static final int VIEW_STATE_PRESSED = 16;
    static final int VIEW_STATE_SELECTED = 2;
    private static final int[][] VIEW_STATE_SETS;
    static final int VIEW_STATE_WINDOW_FOCUSED = 1;
    static final int VISIBILITY_MASK = 12;
    public static final int VISIBLE = 0;
    static final int WILL_NOT_CACHE_DRAWING = 131072;
    static final int WILL_NOT_DRAW = 128;
    protected static final int[] WINDOW_FOCUSED_STATE_SET;
    public static final Property<View, Float> X;
    public static final Property<View, Float> Y;
    private static boolean sCompatibilityDone = false;
    private static boolean sIgnoreMeasureCache = false;
    private static int sNextAccessibilityViewId = 0;
    private static final AtomicInteger sNextGeneratedId;
    static final ThreadLocal<Rect> sThreadLocal;
    private static boolean sUseBrokenMakeMeasureSpec = false;
    private int mAccessibilityCursorPosition;
    AccessibilityDelegate mAccessibilityDelegate;
    int mAccessibilityViewId;
    private ViewPropertyAnimator mAnimator;
    AttachInfo mAttachInfo;

    @ViewDebug.ExportedProperty(deepExport = true, prefix = "bg_")
    private Drawable mBackground;
    private int mBackgroundResource;
    private boolean mBackgroundSizeChanged;

    @ViewDebug.ExportedProperty(category = "layout")
    protected int mBottom;
    public boolean mCachingFailed;
    private Rect mClipBounds;
    private CharSequence mContentDescription;
    protected Context mContext;
    protected Animation mCurrentAnimation;
    DisplayList mDisplayList;
    private int[] mDrawableState;
    private Bitmap mDrawingCache;
    private int mDrawingCacheBackgroundColor;
    private ViewTreeObserver mFloatingTreeObserver;
    private HardwareLayer mHardwareLayer;
    private boolean mHasPerformedLongPress;

    @ViewDebug.ExportedProperty(resolveId = true)
    int mID;
    protected final InputEventConsistencyVerifier mInputEventConsistencyVerifier;
    private SparseArray<Object> mKeyedTags;
    private int mLabelForId;
    private boolean mLastIsOpaque;
    Paint mLayerPaint;

    @ViewDebug.ExportedProperty(category = "drawing", mapping = {@ViewDebug.IntToString(from = 0, to = "NONE"), @ViewDebug.IntToString(from = 1, to = "SOFTWARE"), @ViewDebug.IntToString(from = 2, to = "HARDWARE")})
    int mLayerType;
    private Insets mLayoutInsets;
    protected ViewGroup.LayoutParams mLayoutParams;

    @ViewDebug.ExportedProperty(category = "layout")
    protected int mLeft;
    private boolean mLeftPaddingDefined;
    ListenerInfo mListenerInfo;
    Rect mLocalDirtyRect;
    private MatchIdPredicate mMatchIdPredicate;
    private MatchLabelForPredicate mMatchLabelForPredicate;
    private LongSparseLongArray mMeasureCache;

    @ViewDebug.ExportedProperty(category = "measurement")
    int mMeasuredHeight;

    @ViewDebug.ExportedProperty(category = "measurement")
    int mMeasuredWidth;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int mMinHeight;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int mMinWidth;
    private int mNextFocusDownId;
    int mNextFocusForwardId;
    private int mNextFocusLeftId;
    private int mNextFocusRightId;
    private int mNextFocusUpId;
    int mOldHeightMeasureSpec;
    int mOldWidthMeasureSpec;
    private int mOverScrollMode;
    ViewOverlay mOverlay;

    @ViewDebug.ExportedProperty(category = "padding")
    protected int mPaddingBottom;

    @ViewDebug.ExportedProperty(category = "padding")
    protected int mPaddingLeft;

    @ViewDebug.ExportedProperty(category = "padding")
    protected int mPaddingRight;

    @ViewDebug.ExportedProperty(category = "padding")
    protected int mPaddingTop;
    protected ViewParent mParent;
    private CheckForLongPress mPendingCheckForLongPress;
    private CheckForTap mPendingCheckForTap;
    private PerformClick mPerformClick;

    @ViewDebug.ExportedProperty(flagMapping = {@ViewDebug.FlagToString(equals = 4096, mask = 4096, name = "FORCE_LAYOUT"), @ViewDebug.FlagToString(equals = 8192, mask = 8192, name = "LAYOUT_REQUIRED"), @ViewDebug.FlagToString(equals = 32768, mask = 32768, name = "DRAWING_CACHE_INVALID", outputIf = false), @ViewDebug.FlagToString(equals = 32, mask = 32, name = "DRAWN", outputIf = true), @ViewDebug.FlagToString(equals = 32, mask = 32, name = "NOT_DRAWN", outputIf = false), @ViewDebug.FlagToString(equals = 4194304, mask = 6291456, name = "DIRTY_OPAQUE"), @ViewDebug.FlagToString(equals = 2097152, mask = 6291456, name = "DIRTY")})
    int mPrivateFlags;
    int mPrivateFlags2;
    int mPrivateFlags3;
    boolean mRecreateDisplayList;
    private final Resources mResources;

    @ViewDebug.ExportedProperty(category = "layout")
    protected int mRight;
    private boolean mRightPaddingDefined;
    private ScrollabilityCache mScrollCache;

    @ViewDebug.ExportedProperty(category = "scrolling")
    protected int mScrollX;

    @ViewDebug.ExportedProperty(category = "scrolling")
    protected int mScrollY;
    private SendViewScrolledAccessibilityEvent mSendViewScrolledAccessibilityEvent;
    SendViewStateChangedAccessibilityEvent mSendViewStateChangedAccessibilityEvent;
    private boolean mSendingHoverAccessibilityEvents;

    @ViewDebug.ExportedProperty(flagMapping = {@ViewDebug.FlagToString(equals = 1, mask = 1, name = "SYSTEM_UI_FLAG_LOW_PROFILE", outputIf = true), @ViewDebug.FlagToString(equals = 2, mask = 2, name = "SYSTEM_UI_FLAG_HIDE_NAVIGATION", outputIf = true), @ViewDebug.FlagToString(equals = 0, mask = 65535, name = "SYSTEM_UI_FLAG_VISIBLE", outputIf = true)})
    int mSystemUiVisibility;
    protected Object mTag;

    @ViewDebug.ExportedProperty(category = "layout")
    protected int mTop;
    private TouchDelegate mTouchDelegate;
    private int mTouchSlop;
    TransformationInfo mTransformationInfo;
    int mTransientStateCount;
    private Bitmap mUnscaledDrawingCache;
    private UnsetPressedState mUnsetPressedState;

    @ViewDebug.ExportedProperty(category = "padding")
    protected int mUserPaddingBottom;

    @ViewDebug.ExportedProperty(category = "padding")
    int mUserPaddingEnd;

    @ViewDebug.ExportedProperty(category = "padding")
    protected int mUserPaddingLeft;
    int mUserPaddingLeftInitial;

    @ViewDebug.ExportedProperty(category = "padding")
    protected int mUserPaddingRight;
    int mUserPaddingRightInitial;

    @ViewDebug.ExportedProperty(category = "padding")
    int mUserPaddingStart;
    private float mVerticalScrollFactor;
    private int mVerticalScrollbarPosition;

    @ViewDebug.ExportedProperty
    int mViewFlags;
    int mWindowAttachCount;
    private static final int[] VISIBILITY_FLAGS = {0, 4, 8};
    private static final int[] DRAWING_CACHE_QUALITY_FLAGS = {0, 524288, 1048576};

    public interface OnAttachStateChangeListener {
        void onViewAttachedToWindow(View view);

        void onViewDetachedFromWindow(View view);
    }

    public interface OnClickListener {
        void onClick(View view);
    }

    public interface OnCreateContextMenuListener {
        void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo);
    }

    public interface OnDragListener {
        boolean onDrag(View view, DragEvent dragEvent);
    }

    public interface OnFocusChangeListener {
        void onFocusChange(View view, boolean z);
    }

    public interface OnGenericMotionListener {
        boolean onGenericMotion(View view, MotionEvent motionEvent);
    }

    public interface OnHoverListener {
        boolean onHover(View view, MotionEvent motionEvent);
    }

    public interface OnKeyListener {
        boolean onKey(View view, int i, KeyEvent keyEvent);
    }

    public interface OnLayoutChangeListener {
        void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);
    }

    public interface OnLongClickListener {
        boolean onLongClick(View view);
    }

    public interface OnSystemUiVisibilityChangeListener {
        void onSystemUiVisibilityChange(int i);
    }

    public interface OnTouchListener {
        boolean onTouch(View view, MotionEvent motionEvent);
    }

    public static int combineMeasuredStates(int i, int i2) {
        return i | i2;
    }

    private static boolean nonzero(float f) {
        return f < -0.001f || f > 0.001f;
    }

    public boolean checkInputConnectionProxy(View view) {
        return false;
    }

    public void computeScroll() {
    }

    protected void dispatchDraw(Canvas canvas) {
    }

    protected boolean dispatchGenericFocusedEvent(MotionEvent motionEvent) {
        return false;
    }

    protected boolean dispatchGenericPointerEvent(MotionEvent motionEvent) {
        return false;
    }

    protected void dispatchGetDisplayList() {
    }

    protected void dispatchSetActivated(boolean z) {
    }

    protected void dispatchSetPressed(boolean z) {
    }

    protected void dispatchSetSelected(boolean z) {
    }

    public boolean dispatchUnhandledMove(View view, int i) {
        return false;
    }

    @ViewDebug.ExportedProperty(category = "layout")
    public int getBaseline() {
        return -1;
    }

    protected int getBottomPaddingOffset() {
        return 0;
    }

    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return null;
    }

    protected int getLeftPaddingOffset() {
        return 0;
    }

    protected int getRightPaddingOffset() {
        return 0;
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public int getSolidColor() {
        return 0;
    }

    protected int getTopPaddingOffset() {
        return 0;
    }

    protected boolean hasHoveredChild() {
        return false;
    }

    public boolean hasOverlappingRendering() {
        return true;
    }

    boolean hasStaticLayer() {
        return true;
    }

    void invalidateInheritedLayoutMode(int i) {
    }

    public boolean isAccessibilitySelectionExtendable() {
        return false;
    }

    public boolean isInEditMode() {
        return false;
    }

    protected boolean isPaddingOffsetRequired() {
        return false;
    }

    protected boolean isVerticalScrollBarHidden() {
        return false;
    }

    public boolean onCheckIsTextEditor() {
        return false;
    }

    public void onCloseSystemDialogs(String str) {
    }

    protected void onConfigurationChanged(Configuration configuration) {
    }

    protected void onCreateContextMenu(ContextMenu contextMenu) {
    }

    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        return null;
    }

    protected void onDisplayHint(int i) {
    }

    public boolean onDragEvent(DragEvent dragEvent) {
        return false;
    }

    protected void onDraw(Canvas canvas) {
    }

    protected void onFinishInflate() {
    }

    public void onFinishTemporaryDetach() {
    }

    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        return false;
    }

    public void onHoverChanged(boolean z) {
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyLongPress(int i, KeyEvent keyEvent) {
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent) {
        return false;
    }

    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        return false;
    }

    public boolean onKeyShortcut(int i, KeyEvent keyEvent) {
        return false;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
    }

    protected void onOverScrolled(int i, int i2, boolean z, boolean z2) {
    }

    void onPopulateAccessibilityEventInternal(AccessibilityEvent accessibilityEvent) {
    }

    public void onResolveDrawables(int i) {
    }

    public void onRtlPropertiesChanged(int i) {
    }

    public void onScreenStateChanged(int i) {
    }

    protected boolean onSetAlpha(int i) {
        return false;
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
    }

    public boolean onTrackballEvent(MotionEvent motionEvent) {
        return false;
    }

    public void onWindowSystemUiVisibilityChanged(int i) {
    }

    static {
        int[] iArr = {R.attr.state_window_focused, 1, R.attr.state_selected, 2, R.attr.state_focused, 4, R.attr.state_enabled, 8, R.attr.state_pressed, 16, R.attr.state_activated, 32, R.attr.state_accelerated, 64, R.attr.state_hovered, 128, R.attr.state_drag_can_accept, 256, R.attr.state_drag_hovered, 512};
        VIEW_STATE_IDS = iArr;
        if (iArr.length / 2 != com.android.internal.R.styleable.ViewDrawableStates.length) {
            throw new IllegalStateException("VIEW_STATE_IDs array length does not match ViewDrawableStates style array");
        }
        int length = iArr.length;
        int[] iArr2 = new int[length];
        for (int i = 0; i < com.android.internal.R.styleable.ViewDrawableStates.length; i++) {
            int i2 = com.android.internal.R.styleable.ViewDrawableStates[i];
            int i3 = 0;
            while (true) {
                int[] iArr3 = VIEW_STATE_IDS;
                if (i3 < iArr3.length) {
                    if (iArr3[i3] == i2) {
                        int i4 = i * 2;
                        iArr2[i4] = i2;
                        iArr2[i4 + 1] = iArr3[i3 + 1];
                    }
                    i3 += 2;
                }
            }
        }
        VIEW_STATE_SETS = new int[1 << (VIEW_STATE_IDS.length / 2)][];
        int i5 = 0;
        while (true) {
            int[][] iArr4 = VIEW_STATE_SETS;
            if (i5 < iArr4.length) {
                int[] iArr5 = new int[Integer.bitCount(i5)];
                int i6 = 0;
                for (int i7 = 0; i7 < length; i7 += 2) {
                    if ((iArr2[i7 + 1] & i5) != 0) {
                        iArr5[i6] = iArr2[i7];
                        i6++;
                    }
                }
                VIEW_STATE_SETS[i5] = iArr5;
                i5++;
            } else {
                EMPTY_STATE_SET = iArr4[0];
                WINDOW_FOCUSED_STATE_SET = iArr4[1];
                SELECTED_STATE_SET = iArr4[2];
                SELECTED_WINDOW_FOCUSED_STATE_SET = iArr4[3];
                FOCUSED_STATE_SET = iArr4[4];
                FOCUSED_WINDOW_FOCUSED_STATE_SET = iArr4[5];
                FOCUSED_SELECTED_STATE_SET = iArr4[6];
                FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = iArr4[7];
                ENABLED_STATE_SET = iArr4[8];
                ENABLED_WINDOW_FOCUSED_STATE_SET = iArr4[9];
                ENABLED_SELECTED_STATE_SET = iArr4[10];
                ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = iArr4[11];
                ENABLED_FOCUSED_STATE_SET = iArr4[12];
                ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = iArr4[13];
                ENABLED_FOCUSED_SELECTED_STATE_SET = iArr4[14];
                ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = iArr4[15];
                PRESSED_STATE_SET = iArr4[16];
                PRESSED_WINDOW_FOCUSED_STATE_SET = iArr4[17];
                PRESSED_SELECTED_STATE_SET = iArr4[18];
                PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET = iArr4[19];
                PRESSED_FOCUSED_STATE_SET = iArr4[20];
                PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET = iArr4[21];
                PRESSED_FOCUSED_SELECTED_STATE_SET = iArr4[22];
                PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = iArr4[23];
                PRESSED_ENABLED_STATE_SET = iArr4[24];
                PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET = iArr4[25];
                PRESSED_ENABLED_SELECTED_STATE_SET = iArr4[26];
                PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = iArr4[27];
                PRESSED_ENABLED_FOCUSED_STATE_SET = iArr4[28];
                PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = iArr4[29];
                PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET = iArr4[30];
                PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = iArr4[31];
                sThreadLocal = new ThreadLocal<>();
                LAYOUT_DIRECTION_FLAGS = new int[]{0, 1, 2, 3};
                PFLAG2_TEXT_DIRECTION_FLAGS = new int[]{0, 64, 128, 192, 256, 320};
                PFLAG2_TEXT_ALIGNMENT_FLAGS = new int[]{0, 8192, 16384, AudioSystem.DEVICE_OUT_ALL_USB, 32768, UnixStat.LINK_FLAG, 49152};
                sNextGeneratedId = new AtomicInteger(1);
                ALPHA = new FloatProperty<View>("alpha") { // from class: android.view.View.3
                    @Override // android.util.FloatProperty
                    public void setValue(View view, float f) {
                        view.setAlpha(f);
                    }

                    @Override // android.util.Property
                    public Float get(View view) {
                        return Float.valueOf(view.getAlpha());
                    }
                };
                TRANSLATION_X = new FloatProperty<View>("translationX") { // from class: android.view.View.4
                    @Override // android.util.FloatProperty
                    public void setValue(View view, float f) {
                        view.setTranslationX(f);
                    }

                    @Override // android.util.Property
                    public Float get(View view) {
                        return Float.valueOf(view.getTranslationX());
                    }
                };
                TRANSLATION_Y = new FloatProperty<View>("translationY") { // from class: android.view.View.5
                    @Override // android.util.FloatProperty
                    public void setValue(View view, float f) {
                        view.setTranslationY(f);
                    }

                    @Override // android.util.Property
                    public Float get(View view) {
                        return Float.valueOf(view.getTranslationY());
                    }
                };
                X = new FloatProperty<View>("x") { // from class: android.view.View.6
                    @Override // android.util.FloatProperty
                    public void setValue(View view, float f) {
                        view.setX(f);
                    }

                    @Override // android.util.Property
                    public Float get(View view) {
                        return Float.valueOf(view.getX());
                    }
                };
                Y = new FloatProperty<View>("y") { // from class: android.view.View.7
                    @Override // android.util.FloatProperty
                    public void setValue(View view, float f) {
                        view.setY(f);
                    }

                    @Override // android.util.Property
                    public Float get(View view) {
                        return Float.valueOf(view.getY());
                    }
                };
                ROTATION = new FloatProperty<View>("rotation") { // from class: android.view.View.8
                    @Override // android.util.FloatProperty
                    public void setValue(View view, float f) {
                        view.setRotation(f);
                    }

                    @Override // android.util.Property
                    public Float get(View view) {
                        return Float.valueOf(view.getRotation());
                    }
                };
                ROTATION_X = new FloatProperty<View>("rotationX") { // from class: android.view.View.9
                    @Override // android.util.FloatProperty
                    public void setValue(View view, float f) {
                        view.setRotationX(f);
                    }

                    @Override // android.util.Property
                    public Float get(View view) {
                        return Float.valueOf(view.getRotationX());
                    }
                };
                ROTATION_Y = new FloatProperty<View>("rotationY") { // from class: android.view.View.10
                    @Override // android.util.FloatProperty
                    public void setValue(View view, float f) {
                        view.setRotationY(f);
                    }

                    @Override // android.util.Property
                    public Float get(View view) {
                        return Float.valueOf(view.getRotationY());
                    }
                };
                SCALE_X = new FloatProperty<View>("scaleX") { // from class: android.view.View.11
                    @Override // android.util.FloatProperty
                    public void setValue(View view, float f) {
                        view.setScaleX(f);
                    }

                    @Override // android.util.Property
                    public Float get(View view) {
                        return Float.valueOf(view.getScaleX());
                    }
                };
                SCALE_Y = new FloatProperty<View>("scaleY") { // from class: android.view.View.12
                    @Override // android.util.FloatProperty
                    public void setValue(View view, float f) {
                        view.setScaleY(f);
                    }

                    @Override // android.util.Property
                    public Float get(View view) {
                        return Float.valueOf(view.getScaleY());
                    }
                };
                return;
            }
        }
    }

    static class TransformationInfo {
        private Matrix mInverseMatrix;
        private final Matrix mMatrix = new Matrix();
        boolean mMatrixDirty = false;
        private boolean mInverseMatrixDirty = true;
        private boolean mMatrixIsIdentity = true;
        private Camera mCamera = null;
        private Matrix matrix3D = null;
        private int mPrevWidth = -1;
        private int mPrevHeight = -1;

        @ViewDebug.ExportedProperty
        float mRotationY = 0.0f;

        @ViewDebug.ExportedProperty
        float mRotationX = 0.0f;

        @ViewDebug.ExportedProperty
        float mRotation = 0.0f;

        @ViewDebug.ExportedProperty
        float mTranslationX = 0.0f;

        @ViewDebug.ExportedProperty
        float mTranslationY = 0.0f;

        @ViewDebug.ExportedProperty
        float mScaleX = 1.0f;

        @ViewDebug.ExportedProperty
        float mScaleY = 1.0f;

        @ViewDebug.ExportedProperty
        float mPivotX = 0.0f;

        @ViewDebug.ExportedProperty
        float mPivotY = 0.0f;

        @ViewDebug.ExportedProperty
        float mAlpha = 1.0f;
        float mTransitionAlpha = 1.0f;

        TransformationInfo() {
        }
    }

    static class ListenerInfo {
        private CopyOnWriteArrayList<OnAttachStateChangeListener> mOnAttachStateChangeListeners;
        public OnClickListener mOnClickListener;
        protected OnCreateContextMenuListener mOnCreateContextMenuListener;
        private OnDragListener mOnDragListener;
        protected OnFocusChangeListener mOnFocusChangeListener;
        private OnGenericMotionListener mOnGenericMotionListener;
        private OnHoverListener mOnHoverListener;
        private OnKeyListener mOnKeyListener;
        private ArrayList<OnLayoutChangeListener> mOnLayoutChangeListeners;
        protected OnLongClickListener mOnLongClickListener;
        private OnSystemUiVisibilityChangeListener mOnSystemUiVisibilityChangeListener;
        private OnTouchListener mOnTouchListener;

        ListenerInfo() {
        }
    }

    public View(Context context) {
        this.mCurrentAnimation = null;
        this.mRecreateDisplayList = false;
        this.mID = -1;
        this.mAccessibilityViewId = -1;
        this.mAccessibilityCursorPosition = -1;
        this.mTransientStateCount = 0;
        this.mClipBounds = null;
        this.mPaddingLeft = 0;
        this.mPaddingRight = 0;
        this.mLabelForId = -1;
        this.mLeftPaddingDefined = false;
        this.mRightPaddingDefined = false;
        this.mOldWidthMeasureSpec = Integer.MIN_VALUE;
        this.mOldHeightMeasureSpec = Integer.MIN_VALUE;
        this.mDrawableState = null;
        this.mNextFocusLeftId = -1;
        this.mNextFocusRightId = -1;
        this.mNextFocusUpId = -1;
        this.mNextFocusDownId = -1;
        this.mNextFocusForwardId = -1;
        this.mPendingCheckForTap = null;
        this.mTouchDelegate = null;
        this.mDrawingCacheBackgroundColor = 0;
        this.mAnimator = null;
        this.mLayerType = 0;
        this.mInputEventConsistencyVerifier = InputEventConsistencyVerifier.isInstrumentationEnabled() ? new InputEventConsistencyVerifier(this, 0) : null;
        this.mContext = context;
        this.mResources = context != null ? context.getResources() : null;
        this.mViewFlags = 402653184;
        this.mPrivateFlags2 = 140296;
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOverScrollMode(1);
        this.mUserPaddingStart = Integer.MIN_VALUE;
        this.mUserPaddingEnd = Integer.MIN_VALUE;
        if (sCompatibilityDone || context == null) {
            return;
        }
        int i = context.getApplicationInfo().targetSdkVersion;
        sUseBrokenMakeMeasureSpec = i <= 17;
        sIgnoreMeasureCache = i < 19;
        sCompatibilityDone = true;
    }

    public View(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:127:0x0379 A[PHI: r7 r9 r27 r28 r39
      0x0379: PHI (r7v70 int) = 
      (r7v3 int)
      (r7v5 int)
      (r7v6 int)
      (r7v15 int)
      (r7v17 int)
      (r7v18 int)
      (r7v20 int)
      (r7v22 int)
      (r7v23 int)
      (r7v24 int)
      (r7v25 int)
      (r7v26 int)
      (r7v27 int)
      (r7v28 int)
      (r7v29 int)
      (r7v31 int)
      (r7v32 int)
      (r7v33 int)
      (r7v35 int)
      (r7v36 int)
      (r7v37 int)
      (r7v38 int)
      (r7v42 int)
      (r7v43 int)
      (r7v45 int)
      (r7v47 int)
      (r7v47 int)
      (r7v48 int)
      (r7v64 int)
      (r7v71 int)
     binds: [B:124:0x036f, B:122:0x035c, B:121:0x0351, B:111:0x02e8, B:107:0x02d8, B:104:0x02c5, B:101:0x02b5, B:98:0x02a4, B:99:0x02a6, B:91:0x0288, B:89:0x0276, B:88:0x026a, B:87:0x025e, B:86:0x0252, B:84:0x024b, B:81:0x023d, B:78:0x022f, B:75:0x021c, B:72:0x020e, B:70:0x01fa, B:69:0x01ee, B:67:0x01e4, B:64:0x01d6, B:62:0x01ca, B:57:0x01b3, B:52:0x0197, B:53:0x0199, B:48:0x017c, B:94:0x0290, B:7:0x0060] A[DONT_GENERATE, DONT_INLINE]
      0x0379: PHI (r9v60 int) = 
      (r9v3 int)
      (r9v4 int)
      (r9v5 int)
      (r9v14 int)
      (r9v15 int)
      (r9v16 int)
      (r9v17 int)
      (r9v18 int)
      (r9v19 int)
      (r9v20 int)
      (r9v21 int)
      (r9v22 int)
      (r9v23 int)
      (r9v24 int)
      (r9v25 int)
      (r9v26 int)
      (r9v27 int)
      (r9v28 int)
      (r9v29 int)
      (r9v30 int)
      (r9v31 int)
      (r9v32 int)
      (r9v34 int)
      (r9v35 int)
      (r9v37 int)
      (r9v38 int)
      (r9v38 int)
      (r9v39 int)
      (r9v52 int)
      (r9v61 int)
     binds: [B:124:0x036f, B:122:0x035c, B:121:0x0351, B:111:0x02e8, B:107:0x02d8, B:104:0x02c5, B:101:0x02b5, B:98:0x02a4, B:99:0x02a6, B:91:0x0288, B:89:0x0276, B:88:0x026a, B:87:0x025e, B:86:0x0252, B:84:0x024b, B:81:0x023d, B:78:0x022f, B:75:0x021c, B:72:0x020e, B:70:0x01fa, B:69:0x01ee, B:67:0x01e4, B:64:0x01d6, B:62:0x01ca, B:57:0x01b3, B:52:0x0197, B:53:0x0199, B:48:0x017c, B:94:0x0290, B:7:0x0060] A[DONT_GENERATE, DONT_INLINE]
      0x0379: PHI (r27v4 boolean) = 
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v2 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
      (r27v1 boolean)
     binds: [B:124:0x036f, B:122:0x035c, B:121:0x0351, B:111:0x02e8, B:107:0x02d8, B:104:0x02c5, B:101:0x02b5, B:98:0x02a4, B:99:0x02a6, B:91:0x0288, B:89:0x0276, B:88:0x026a, B:87:0x025e, B:86:0x0252, B:84:0x024b, B:81:0x023d, B:78:0x022f, B:75:0x021c, B:72:0x020e, B:70:0x01fa, B:69:0x01ee, B:67:0x01e4, B:64:0x01d6, B:62:0x01ca, B:57:0x01b3, B:52:0x0197, B:53:0x0199, B:48:0x017c, B:94:0x0290, B:7:0x0060] A[DONT_GENERATE, DONT_INLINE]
      0x0379: PHI (r28v5 int) = 
      (r28v2 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
      (r28v1 int)
     binds: [B:124:0x036f, B:122:0x035c, B:121:0x0351, B:111:0x02e8, B:107:0x02d8, B:104:0x02c5, B:101:0x02b5, B:98:0x02a4, B:99:0x02a6, B:91:0x0288, B:89:0x0276, B:88:0x026a, B:87:0x025e, B:86:0x0252, B:84:0x024b, B:81:0x023d, B:78:0x022f, B:75:0x021c, B:72:0x020e, B:70:0x01fa, B:69:0x01ee, B:67:0x01e4, B:64:0x01d6, B:62:0x01ca, B:57:0x01b3, B:52:0x0197, B:53:0x0199, B:48:0x017c, B:94:0x0290, B:7:0x0060] A[DONT_GENERATE, DONT_INLINE]
      0x0379: PHI (r39v4 boolean) = 
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v2 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
      (r39v1 boolean)
     binds: [B:124:0x036f, B:122:0x035c, B:121:0x0351, B:111:0x02e8, B:107:0x02d8, B:104:0x02c5, B:101:0x02b5, B:98:0x02a4, B:99:0x02a6, B:91:0x0288, B:89:0x0276, B:88:0x026a, B:87:0x025e, B:86:0x0252, B:84:0x024b, B:81:0x023d, B:78:0x022f, B:75:0x021c, B:72:0x020e, B:70:0x01fa, B:69:0x01ee, B:67:0x01e4, B:64:0x01d6, B:62:0x01ca, B:57:0x01b3, B:52:0x0197, B:53:0x0199, B:48:0x017c, B:94:0x0290, B:7:0x0060] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:95:0x0292  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public View(android.content.Context r43, android.util.AttributeSet r44, int r45) {
        /*
            Method dump skipped, instruction units count: 1230
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.View.<init>(android.content.Context, android.util.AttributeSet, int):void");
    }

    View() {
        this.mCurrentAnimation = null;
        this.mRecreateDisplayList = false;
        this.mID = -1;
        this.mAccessibilityViewId = -1;
        this.mAccessibilityCursorPosition = -1;
        this.mTransientStateCount = 0;
        this.mClipBounds = null;
        this.mPaddingLeft = 0;
        this.mPaddingRight = 0;
        this.mLabelForId = -1;
        this.mLeftPaddingDefined = false;
        this.mRightPaddingDefined = false;
        this.mOldWidthMeasureSpec = Integer.MIN_VALUE;
        this.mOldHeightMeasureSpec = Integer.MIN_VALUE;
        this.mDrawableState = null;
        this.mNextFocusLeftId = -1;
        this.mNextFocusRightId = -1;
        this.mNextFocusUpId = -1;
        this.mNextFocusDownId = -1;
        this.mNextFocusForwardId = -1;
        this.mPendingCheckForTap = null;
        this.mTouchDelegate = null;
        this.mDrawingCacheBackgroundColor = 0;
        this.mAnimator = null;
        this.mLayerType = 0;
        this.mInputEventConsistencyVerifier = InputEventConsistencyVerifier.isInstrumentationEnabled() ? new InputEventConsistencyVerifier(this, 0) : null;
        this.mResources = null;
    }

    public String toString() {
        String resourcePackageName;
        StringBuilder sb = new StringBuilder(128);
        sb.append(getClass().getName());
        sb.append('{');
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        int i = this.mViewFlags & 12;
        if (i == 0) {
            sb.append('V');
        } else if (i == 4) {
            sb.append('I');
        } else if (i == 8) {
            sb.append('G');
        } else {
            sb.append('.');
        }
        sb.append((this.mViewFlags & 1) == 1 ? 'F' : '.');
        sb.append((this.mViewFlags & 32) == 0 ? DateFormat.DAY : '.');
        sb.append((this.mViewFlags & 128) == 128 ? '.' : 'D');
        sb.append((this.mViewFlags & 256) != 0 ? 'H' : '.');
        sb.append((this.mViewFlags & 512) == 0 ? '.' : 'V');
        sb.append((this.mViewFlags & 16384) != 0 ? 'C' : '.');
        sb.append((this.mViewFlags & 2097152) != 0 ? DateFormat.STANDALONE_MONTH : '.');
        sb.append(' ');
        sb.append((this.mPrivateFlags & 8) != 0 ? 'R' : '.');
        sb.append((this.mPrivateFlags & 2) == 0 ? '.' : 'F');
        sb.append((this.mPrivateFlags & 4) != 0 ? 'S' : '.');
        int i2 = this.mPrivateFlags;
        if ((33554432 & i2) != 0) {
            sb.append('p');
        } else {
            sb.append((i2 & 16384) != 0 ? 'P' : '.');
        }
        sb.append((this.mPrivateFlags & 268435456) == 0 ? '.' : 'H');
        sb.append((this.mPrivateFlags & 1073741824) != 0 ? DateFormat.CAPITAL_AM_PM : '.');
        sb.append((this.mPrivateFlags & Integer.MIN_VALUE) == 0 ? '.' : 'I');
        sb.append((this.mPrivateFlags & 6291456) != 0 ? 'D' : '.');
        sb.append(' ');
        sb.append(this.mLeft);
        sb.append(PhoneNumberUtils.PAUSE);
        sb.append(this.mTop);
        sb.append('-');
        sb.append(this.mRight);
        sb.append(PhoneNumberUtils.PAUSE);
        sb.append(this.mBottom);
        int id = getId();
        if (id != -1) {
            sb.append(" #");
            sb.append(Integer.toHexString(id));
            Resources resources = this.mResources;
            if (id != 0 && resources != null) {
                int i3 = (-16777216) & id;
                if (i3 == 16777216) {
                    resourcePackageName = "android";
                } else if (i3 != 2130706432) {
                    try {
                        resourcePackageName = resources.getResourcePackageName(id);
                    } catch (Resources.NotFoundException unused) {
                    }
                } else {
                    resourcePackageName = Settings.System.SHORTCUT_PATH_TYPE_APP;
                }
                String resourceTypeName = resources.getResourceTypeName(id);
                String resourceEntryName = resources.getResourceEntryName(id);
                sb.append(" ");
                sb.append(resourcePackageName);
                sb.append(":");
                sb.append(resourceTypeName);
                sb.append("/");
                sb.append(resourceEntryName);
            }
        }
        sb.append("}");
        return sb.toString();
    }

    protected void initializeFadingEdge(TypedArray typedArray) {
        initScrollCache();
        this.mScrollCache.fadingEdgeLength = typedArray.getDimensionPixelSize(24, ViewConfiguration.get(this.mContext).getScaledFadingEdgeLength());
    }

    public int getVerticalFadingEdgeLength() {
        ScrollabilityCache scrollabilityCache;
        if (!isVerticalFadingEdgeEnabled() || (scrollabilityCache = this.mScrollCache) == null) {
            return 0;
        }
        return scrollabilityCache.fadingEdgeLength;
    }

    public void setFadingEdgeLength(int i) {
        initScrollCache();
        this.mScrollCache.fadingEdgeLength = i;
    }

    public int getHorizontalFadingEdgeLength() {
        ScrollabilityCache scrollabilityCache;
        if (!isHorizontalFadingEdgeEnabled() || (scrollabilityCache = this.mScrollCache) == null) {
            return 0;
        }
        return scrollabilityCache.fadingEdgeLength;
    }

    public int getVerticalScrollbarWidth() {
        ScrollBarDrawable scrollBarDrawable;
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        if (scrollabilityCache == null || (scrollBarDrawable = scrollabilityCache.scrollBar) == null) {
            return 0;
        }
        int size = scrollBarDrawable.getSize(true);
        return size <= 0 ? scrollabilityCache.scrollBarSize : size;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getHorizontalScrollbarHeight() {
        ScrollBarDrawable scrollBarDrawable;
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        if (scrollabilityCache == null || (scrollBarDrawable = scrollabilityCache.scrollBar) == null) {
            return 0;
        }
        int size = scrollBarDrawable.getSize(false);
        return size <= 0 ? scrollabilityCache.scrollBarSize : size;
    }

    protected void initializeScrollbars(TypedArray typedArray) {
        initScrollCache();
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        if (scrollabilityCache.scrollBar == null) {
            scrollabilityCache.scrollBar = new ScrollBarDrawable();
        }
        boolean z = typedArray.getBoolean(44, true);
        if (!z) {
            scrollabilityCache.state = 1;
        }
        scrollabilityCache.fadeScrollBars = z;
        scrollabilityCache.scrollBarFadeDuration = typedArray.getInt(42, ViewConfiguration.getScrollBarFadeDuration());
        scrollabilityCache.scrollBarDefaultDelayBeforeFade = typedArray.getInt(43, ViewConfiguration.getScrollDefaultDelay());
        scrollabilityCache.scrollBarSize = typedArray.getDimensionPixelSize(0, ViewConfiguration.get(this.mContext).getScaledScrollBarSize());
        scrollabilityCache.scrollBar.setHorizontalTrackDrawable(typedArray.getDrawable(3));
        Drawable drawable = typedArray.getDrawable(1);
        if (drawable != null) {
            scrollabilityCache.scrollBar.setHorizontalThumbDrawable(drawable);
        }
        if (typedArray.getBoolean(5, false)) {
            scrollabilityCache.scrollBar.setAlwaysDrawHorizontalTrack(true);
        }
        Drawable drawable2 = typedArray.getDrawable(4);
        scrollabilityCache.scrollBar.setVerticalTrackDrawable(drawable2);
        Drawable drawable3 = typedArray.getDrawable(2);
        if (drawable3 != null) {
            scrollabilityCache.scrollBar.setVerticalThumbDrawable(drawable3);
        }
        if (typedArray.getBoolean(6, false)) {
            scrollabilityCache.scrollBar.setAlwaysDrawVerticalTrack(true);
        }
        int layoutDirection = getLayoutDirection();
        if (drawable2 != null) {
            drawable2.setLayoutDirection(layoutDirection);
        }
        if (drawable3 != null) {
            drawable3.setLayoutDirection(layoutDirection);
        }
        resolvePadding();
    }

    private void initScrollCache() {
        if (this.mScrollCache == null) {
            this.mScrollCache = new ScrollabilityCache(ViewConfiguration.get(this.mContext), this);
        }
    }

    private ScrollabilityCache getScrollCache() {
        initScrollCache();
        return this.mScrollCache;
    }

    public void setVerticalScrollbarPosition(int i) {
        if (this.mVerticalScrollbarPosition != i) {
            this.mVerticalScrollbarPosition = i;
            computeOpaqueFlags();
            resolvePadding();
        }
    }

    public int getVerticalScrollbarPosition() {
        return this.mVerticalScrollbarPosition;
    }

    ListenerInfo getListenerInfo() {
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo != null) {
            return listenerInfo;
        }
        ListenerInfo listenerInfo2 = new ListenerInfo();
        this.mListenerInfo = listenerInfo2;
        return listenerInfo2;
    }

    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        getListenerInfo().mOnFocusChangeListener = onFocusChangeListener;
    }

    public void addOnLayoutChangeListener(OnLayoutChangeListener onLayoutChangeListener) {
        ListenerInfo listenerInfo = getListenerInfo();
        if (listenerInfo.mOnLayoutChangeListeners == null) {
            listenerInfo.mOnLayoutChangeListeners = new ArrayList();
        }
        if (listenerInfo.mOnLayoutChangeListeners.contains(onLayoutChangeListener)) {
            return;
        }
        listenerInfo.mOnLayoutChangeListeners.add(onLayoutChangeListener);
    }

    public void removeOnLayoutChangeListener(OnLayoutChangeListener onLayoutChangeListener) {
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo == null || listenerInfo.mOnLayoutChangeListeners == null) {
            return;
        }
        listenerInfo.mOnLayoutChangeListeners.remove(onLayoutChangeListener);
    }

    public void addOnAttachStateChangeListener(OnAttachStateChangeListener onAttachStateChangeListener) {
        ListenerInfo listenerInfo = getListenerInfo();
        if (listenerInfo.mOnAttachStateChangeListeners == null) {
            listenerInfo.mOnAttachStateChangeListeners = new CopyOnWriteArrayList();
        }
        listenerInfo.mOnAttachStateChangeListeners.add(onAttachStateChangeListener);
    }

    public void removeOnAttachStateChangeListener(OnAttachStateChangeListener onAttachStateChangeListener) {
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo == null || listenerInfo.mOnAttachStateChangeListeners == null) {
            return;
        }
        listenerInfo.mOnAttachStateChangeListeners.remove(onAttachStateChangeListener);
    }

    public OnFocusChangeListener getOnFocusChangeListener() {
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo != null) {
            return listenerInfo.mOnFocusChangeListener;
        }
        return null;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        if (!isClickable()) {
            setClickable(true);
        }
        getListenerInfo().mOnClickListener = onClickListener;
    }

    public boolean hasOnClickListeners() {
        ListenerInfo listenerInfo = this.mListenerInfo;
        return (listenerInfo == null || listenerInfo.mOnClickListener == null) ? false : true;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        getListenerInfo().mOnLongClickListener = onLongClickListener;
    }

    public void setOnCreateContextMenuListener(OnCreateContextMenuListener onCreateContextMenuListener) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        getListenerInfo().mOnCreateContextMenuListener = onCreateContextMenuListener;
    }

    public boolean performClick() {
        sendAccessibilityEvent(1);
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo == null || listenerInfo.mOnClickListener == null) {
            return false;
        }
        playSoundEffect(0);
        listenerInfo.mOnClickListener.onClick(this);
        return true;
    }

    public boolean callOnClick() {
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo == null || listenerInfo.mOnClickListener == null) {
            return false;
        }
        listenerInfo.mOnClickListener.onClick(this);
        return true;
    }

    public boolean performLongClick() {
        sendAccessibilityEvent(2);
        ListenerInfo listenerInfo = this.mListenerInfo;
        boolean zOnLongClick = (listenerInfo == null || listenerInfo.mOnLongClickListener == null) ? false : listenerInfo.mOnLongClickListener.onLongClick(this);
        if (!zOnLongClick) {
            zOnLongClick = showContextMenu();
        }
        if (zOnLongClick) {
            performHapticFeedback(0);
        }
        return zOnLongClick;
    }

    protected boolean performButtonActionOnTouchDown(MotionEvent motionEvent) {
        return (motionEvent.getButtonState() & 2) != 0 && showContextMenu(motionEvent.getX(), motionEvent.getY(), motionEvent.getMetaState());
    }

    public boolean showContextMenu() {
        return getParent().showContextMenuForChild(this);
    }

    public boolean showContextMenu(float f, float f2, int i) {
        return showContextMenu();
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        ViewParent parent = getParent();
        if (parent == null) {
            return null;
        }
        return parent.startActionModeForChild(this, callback);
    }

    public void setOnKeyListener(OnKeyListener onKeyListener) {
        getListenerInfo().mOnKeyListener = onKeyListener;
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        getListenerInfo().mOnTouchListener = onTouchListener;
    }

    public void setOnGenericMotionListener(OnGenericMotionListener onGenericMotionListener) {
        getListenerInfo().mOnGenericMotionListener = onGenericMotionListener;
    }

    public void setOnHoverListener(OnHoverListener onHoverListener) {
        getListenerInfo().mOnHoverListener = onHoverListener;
    }

    public void setOnDragListener(OnDragListener onDragListener) {
        getListenerInfo().mOnDragListener = onDragListener;
    }

    void handleFocusGainInternal(int i, Rect rect) {
        int i2 = this.mPrivateFlags;
        if ((i2 & 2) == 0) {
            this.mPrivateFlags = i2 | 2;
            View viewFindFocus = this.mAttachInfo != null ? getRootView().findFocus() : null;
            ViewParent viewParent = this.mParent;
            if (viewParent != null) {
                viewParent.requestChildFocus(this, this);
            }
            AttachInfo attachInfo = this.mAttachInfo;
            if (attachInfo != null) {
                attachInfo.mTreeObserver.dispatchOnGlobalFocusChange(viewFindFocus, this);
            }
            onFocusChanged(true, i, rect);
            refreshDrawableState();
        }
    }

    public boolean requestRectangleOnScreen(Rect rect) {
        return requestRectangleOnScreen(rect, false);
    }

    public boolean requestRectangleOnScreen(Rect rect, boolean z) {
        boolean zRequestChildRectangleOnScreen = false;
        if (this.mParent == null) {
            return false;
        }
        AttachInfo attachInfo = this.mAttachInfo;
        RectF rectF = attachInfo != null ? attachInfo.mTmpTransformRect : new RectF();
        rectF.set(rect);
        ViewParent parent = this.mParent;
        View view = this;
        while (parent != null) {
            rect.set((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
            zRequestChildRectangleOnScreen |= parent.requestChildRectangleOnScreen(view, rect, z);
            if (!view.hasIdentityMatrix()) {
                view.getMatrix().mapRect(rectF);
            }
            rectF.offset(view.mLeft, view.mTop);
            if (!(parent instanceof View)) {
                break;
            }
            view = (View) parent;
            rectF.offset(-view.getScrollX(), -view.getScrollY());
            parent = view.getParent();
        }
        return zRequestChildRectangleOnScreen;
    }

    public void clearFocus() {
        clearFocusInternal(true, true);
    }

    void clearFocusInternal(boolean z, boolean z2) {
        ViewParent viewParent;
        int i = this.mPrivateFlags;
        if ((i & 2) != 0) {
            this.mPrivateFlags = i & (-3);
            if (z && (viewParent = this.mParent) != null) {
                viewParent.clearChildFocus(this);
            }
            onFocusChanged(false, 0, null);
            refreshDrawableState();
            if (z) {
                if (z2 && rootViewRequestFocus()) {
                    return;
                }
                notifyGlobalFocusCleared(this);
            }
        }
    }

    void notifyGlobalFocusCleared(View view) {
        AttachInfo attachInfo;
        if (view == null || (attachInfo = this.mAttachInfo) == null) {
            return;
        }
        attachInfo.mTreeObserver.dispatchOnGlobalFocusChange(view, null);
    }

    boolean rootViewRequestFocus() {
        View rootView = getRootView();
        return rootView != null && rootView.requestFocus();
    }

    void unFocus() {
        clearFocusInternal(false, false);
    }

    @ViewDebug.ExportedProperty(category = "focus")
    public boolean hasFocus() {
        return (this.mPrivateFlags & 2) != 0;
    }

    public boolean hasFocusable() {
        return (this.mViewFlags & 12) == 0 && isFocusable();
    }

    protected void onFocusChanged(boolean z, int i, Rect rect) {
        AttachInfo attachInfo;
        AttachInfo attachInfo2;
        if (z) {
            sendAccessibilityEvent(8);
        } else {
            notifyViewAccessibilityStateChangedIfNeeded(0);
        }
        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
        if (!z) {
            if (isPressed()) {
                setPressed(false);
            }
            if (inputMethodManagerPeekInstance != null && (attachInfo2 = this.mAttachInfo) != null && attachInfo2.mHasWindowFocus) {
                inputMethodManagerPeekInstance.focusOut(this);
            }
            onFocusLost();
        } else if (inputMethodManagerPeekInstance != null && (attachInfo = this.mAttachInfo) != null && attachInfo.mHasWindowFocus) {
            inputMethodManagerPeekInstance.focusIn(this);
        }
        invalidate(true);
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo != null && listenerInfo.mOnFocusChangeListener != null) {
            listenerInfo.mOnFocusChangeListener.onFocusChange(this, z);
        }
        AttachInfo attachInfo3 = this.mAttachInfo;
        if (attachInfo3 != null) {
            attachInfo3.mKeyDispatchState.reset(this);
        }
    }

    @Override // android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEvent(int i) {
        if (includeForAccessibility()) {
            AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
            if (accessibilityDelegate != null) {
                accessibilityDelegate.sendAccessibilityEvent(this, i);
            } else {
                sendAccessibilityEventInternal(i);
            }
        }
    }

    public void announceForAccessibility(CharSequence charSequence) {
        if (!AccessibilityManager.getInstance(this.mContext).isEnabled() || this.mParent == null) {
            return;
        }
        AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(16384);
        onInitializeAccessibilityEvent(accessibilityEventObtain);
        accessibilityEventObtain.getText().add(charSequence);
        accessibilityEventObtain.setContentDescription(null);
        this.mParent.requestSendAccessibilityEvent(this, accessibilityEventObtain);
    }

    void sendAccessibilityEventInternal(int i) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            sendAccessibilityEventUnchecked(AccessibilityEvent.obtain(i));
        }
    }

    @Override // android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEventUnchecked(AccessibilityEvent accessibilityEvent) {
        AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            accessibilityDelegate.sendAccessibilityEventUnchecked(this, accessibilityEvent);
        } else {
            sendAccessibilityEventUncheckedInternal(accessibilityEvent);
        }
    }

    void sendAccessibilityEventUncheckedInternal(AccessibilityEvent accessibilityEvent) {
        if (isShown()) {
            onInitializeAccessibilityEvent(accessibilityEvent);
            if ((accessibilityEvent.getEventType() & POPULATING_ACCESSIBILITY_EVENT_TYPES) != 0) {
                dispatchPopulateAccessibilityEvent(accessibilityEvent);
            }
            getParent().requestSendAccessibilityEvent(this, accessibilityEvent);
        }
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            return accessibilityDelegate.dispatchPopulateAccessibilityEvent(this, accessibilityEvent);
        }
        return dispatchPopulateAccessibilityEventInternal(accessibilityEvent);
    }

    boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent accessibilityEvent) {
        onPopulateAccessibilityEvent(accessibilityEvent);
        return false;
    }

    public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            accessibilityDelegate.onPopulateAccessibilityEvent(this, accessibilityEvent);
        } else {
            onPopulateAccessibilityEventInternal(accessibilityEvent);
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            accessibilityDelegate.onInitializeAccessibilityEvent(this, accessibilityEvent);
        } else {
            onInitializeAccessibilityEventInternal(accessibilityEvent);
        }
    }

    void onInitializeAccessibilityEventInternal(AccessibilityEvent accessibilityEvent) {
        CharSequence iterableTextForAccessibility;
        accessibilityEvent.setSource(this);
        accessibilityEvent.setClassName(View.class.getName());
        accessibilityEvent.setPackageName(getContext().getPackageName());
        accessibilityEvent.setEnabled(isEnabled());
        accessibilityEvent.setContentDescription(this.mContentDescription);
        int eventType = accessibilityEvent.getEventType();
        if (eventType == 8) {
            AttachInfo attachInfo = this.mAttachInfo;
            ArrayList<View> arrayList = attachInfo != null ? attachInfo.mTempArrayList : new ArrayList<>();
            getRootView().addFocusables(arrayList, 2, 0);
            accessibilityEvent.setItemCount(arrayList.size());
            accessibilityEvent.setCurrentItemIndex(arrayList.indexOf(this));
            if (this.mAttachInfo != null) {
                arrayList.clear();
                return;
            }
            return;
        }
        if (eventType == 8192 && (iterableTextForAccessibility = getIterableTextForAccessibility()) != null && iterableTextForAccessibility.length() > 0) {
            accessibilityEvent.setFromIndex(getAccessibilitySelectionStart());
            accessibilityEvent.setToIndex(getAccessibilitySelectionEnd());
            accessibilityEvent.setItemCount(iterableTextForAccessibility.length());
        }
    }

    public AccessibilityNodeInfo createAccessibilityNodeInfo() {
        AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            return accessibilityDelegate.createAccessibilityNodeInfo(this);
        }
        return createAccessibilityNodeInfoInternal();
    }

    AccessibilityNodeInfo createAccessibilityNodeInfoInternal() {
        AccessibilityNodeProvider accessibilityNodeProvider = getAccessibilityNodeProvider();
        if (accessibilityNodeProvider != null) {
            return accessibilityNodeProvider.createAccessibilityNodeInfo(-1);
        }
        AccessibilityNodeInfo accessibilityNodeInfoObtain = AccessibilityNodeInfo.obtain(this);
        onInitializeAccessibilityNodeInfo(accessibilityNodeInfoObtain);
        return accessibilityNodeInfoObtain;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            accessibilityDelegate.onInitializeAccessibilityNodeInfo(this, accessibilityNodeInfo);
        } else {
            onInitializeAccessibilityNodeInfoInternal(accessibilityNodeInfo);
        }
    }

    void getBoundsOnScreen(Rect rect) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            return;
        }
        RectF rectF = attachInfo.mTmpTransformRect;
        rectF.set(0.0f, 0.0f, this.mRight - this.mLeft, this.mBottom - this.mTop);
        if (!hasIdentityMatrix()) {
            getMatrix().mapRect(rectF);
        }
        rectF.offset(this.mLeft, this.mTop);
        Object obj = this.mParent;
        while (obj instanceof View) {
            View view = (View) obj;
            rectF.offset(-view.mScrollX, -view.mScrollY);
            if (!view.hasIdentityMatrix()) {
                view.getMatrix().mapRect(rectF);
            }
            rectF.offset(view.mLeft, view.mTop);
            obj = view.mParent;
        }
        if (obj instanceof ViewRootImpl) {
            rectF.offset(0.0f, -((ViewRootImpl) obj).mCurScrollY);
        }
        rectF.offset(this.mAttachInfo.mWindowLeft, this.mAttachInfo.mWindowTop);
        rect.set((int) (rectF.left + 0.5f), (int) (rectF.top + 0.5f), (int) (rectF.right + 0.5f), (int) (rectF.bottom + 0.5f));
    }

    void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo accessibilityNodeInfo) {
        Rect rect = this.mAttachInfo.mTmpInvalRect;
        getDrawingRect(rect);
        accessibilityNodeInfo.setBoundsInParent(rect);
        getBoundsOnScreen(rect);
        accessibilityNodeInfo.setBoundsInScreen(rect);
        Object parentForAccessibility = getParentForAccessibility();
        if (parentForAccessibility instanceof View) {
            accessibilityNodeInfo.setParent((View) parentForAccessibility);
        }
        if (this.mID != -1) {
            View rootView = getRootView();
            if (rootView == null) {
                rootView = this;
            }
            View viewFindLabelForView = rootView.findLabelForView(this, this.mID);
            if (viewFindLabelForView != null) {
                accessibilityNodeInfo.setLabeledBy(viewFindLabelForView);
            }
            if ((this.mAttachInfo.mAccessibilityFetchFlags & 16) != 0 && Resources.resourceHasPackage(this.mID)) {
                try {
                    accessibilityNodeInfo.setViewIdResourceName(getResources().getResourceName(this.mID));
                } catch (Resources.NotFoundException unused) {
                }
            }
        }
        if (this.mLabelForId != -1) {
            View rootView2 = getRootView();
            if (rootView2 == null) {
                rootView2 = this;
            }
            View viewFindViewInsideOutShouldExist = rootView2.findViewInsideOutShouldExist(this, this.mLabelForId);
            if (viewFindViewInsideOutShouldExist != null) {
                accessibilityNodeInfo.setLabelFor(viewFindViewInsideOutShouldExist);
            }
        }
        accessibilityNodeInfo.setVisibleToUser(isVisibleToUser());
        accessibilityNodeInfo.setPackageName(this.mContext.getPackageName());
        accessibilityNodeInfo.setClassName(View.class.getName());
        accessibilityNodeInfo.setContentDescription(getContentDescription());
        accessibilityNodeInfo.setEnabled(isEnabled());
        accessibilityNodeInfo.setClickable(isClickable());
        accessibilityNodeInfo.setFocusable(isFocusable());
        accessibilityNodeInfo.setFocused(isFocused());
        accessibilityNodeInfo.setAccessibilityFocused(isAccessibilityFocused());
        accessibilityNodeInfo.setSelected(isSelected());
        accessibilityNodeInfo.setLongClickable(isLongClickable());
        accessibilityNodeInfo.setLiveRegion(getAccessibilityLiveRegion());
        accessibilityNodeInfo.addAction(4);
        accessibilityNodeInfo.addAction(8);
        if (isFocusable()) {
            if (isFocused()) {
                accessibilityNodeInfo.addAction(2);
            } else {
                accessibilityNodeInfo.addAction(1);
            }
        }
        if (!isAccessibilityFocused()) {
            accessibilityNodeInfo.addAction(64);
        } else {
            accessibilityNodeInfo.addAction(128);
        }
        if (isClickable() && isEnabled()) {
            accessibilityNodeInfo.addAction(16);
        }
        if (isLongClickable() && isEnabled()) {
            accessibilityNodeInfo.addAction(32);
        }
        CharSequence iterableTextForAccessibility = getIterableTextForAccessibility();
        if (iterableTextForAccessibility == null || iterableTextForAccessibility.length() <= 0) {
            return;
        }
        accessibilityNodeInfo.setTextSelection(getAccessibilitySelectionStart(), getAccessibilitySelectionEnd());
        accessibilityNodeInfo.addAction(131072);
        accessibilityNodeInfo.addAction(256);
        accessibilityNodeInfo.addAction(512);
        accessibilityNodeInfo.setMovementGranularities(11);
    }

    private View findLabelForView(View view, int i) {
        if (this.mMatchLabelForPredicate == null) {
            this.mMatchLabelForPredicate = new MatchLabelForPredicate();
        }
        this.mMatchLabelForPredicate.mLabeledId = i;
        return findViewByPredicateInsideOut(view, this.mMatchLabelForPredicate);
    }

    protected boolean isVisibleToUser() {
        return isVisibleToUser(null);
    }

    protected boolean isVisibleToUser(Rect rect) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null || attachInfo.mWindowVisibility != 0) {
            return false;
        }
        Object obj = this;
        while (obj instanceof View) {
            View view = (View) obj;
            if (view.getAlpha() <= 0.0f || view.getTransitionAlpha() <= 0.0f || view.getVisibility() != 0) {
                return false;
            }
            obj = view.mParent;
        }
        Rect rect2 = this.mAttachInfo.mTmpInvalRect;
        Point point = this.mAttachInfo.mPoint;
        if (!getGlobalVisibleRect(rect2, point)) {
            return false;
        }
        if (rect == null) {
            return true;
        }
        rect2.offset(-point.x, -point.y);
        return rect.intersect(rect2);
    }

    public AccessibilityDelegate getAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    public void setAccessibilityDelegate(AccessibilityDelegate accessibilityDelegate) {
        this.mAccessibilityDelegate = accessibilityDelegate;
    }

    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            return accessibilityDelegate.getAccessibilityNodeProvider(this);
        }
        return null;
    }

    public int getAccessibilityViewId() {
        if (this.mAccessibilityViewId == -1) {
            int i = sNextAccessibilityViewId;
            sNextAccessibilityViewId = i + 1;
            this.mAccessibilityViewId = i;
        }
        return this.mAccessibilityViewId;
    }

    public int getAccessibilityWindowId() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mAccessibilityWindowId;
        }
        return -1;
    }

    @ViewDebug.ExportedProperty(category = Context.ACCESSIBILITY_SERVICE)
    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    @RemotableViewMethod
    public void setContentDescription(CharSequence charSequence) {
        CharSequence charSequence2 = this.mContentDescription;
        if (charSequence2 == null) {
            if (charSequence == null) {
                return;
            }
        } else if (charSequence2.equals(charSequence)) {
            return;
        }
        this.mContentDescription = charSequence;
        if ((charSequence != null && charSequence.length() > 0) && getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
            notifySubtreeAccessibilityStateChangedIfNeeded();
        } else {
            notifyViewAccessibilityStateChangedIfNeeded(4);
        }
    }

    @ViewDebug.ExportedProperty(category = Context.ACCESSIBILITY_SERVICE)
    public int getLabelFor() {
        return this.mLabelForId;
    }

    @RemotableViewMethod
    public void setLabelFor(int i) {
        this.mLabelForId = i;
        if (i == -1 || this.mID != -1) {
            return;
        }
        this.mID = generateViewId();
    }

    protected void onFocusLost() {
        resetPressedState();
    }

    private void resetPressedState() {
        if ((this.mViewFlags & 32) != 32 && isPressed()) {
            setPressed(false);
            if (this.mHasPerformedLongPress) {
                return;
            }
            removeLongPressCallback();
        }
    }

    @ViewDebug.ExportedProperty(category = "focus")
    public boolean isFocused() {
        return (this.mPrivateFlags & 2) != 0;
    }

    public View findFocus() {
        if ((this.mPrivateFlags & 2) != 0) {
            return this;
        }
        return null;
    }

    public boolean isScrollContainer() {
        return (this.mPrivateFlags & 1048576) != 0;
    }

    public void setScrollContainer(boolean z) {
        if (z) {
            AttachInfo attachInfo = this.mAttachInfo;
            if (attachInfo != null && (this.mPrivateFlags & 1048576) == 0) {
                attachInfo.mScrollContainers.add(this);
                this.mPrivateFlags |= 1048576;
            }
            this.mPrivateFlags |= 524288;
            return;
        }
        if ((this.mPrivateFlags & 1048576) != 0) {
            this.mAttachInfo.mScrollContainers.remove(this);
        }
        this.mPrivateFlags &= -1572865;
    }

    public int getDrawingCacheQuality() {
        return this.mViewFlags & DRAWING_CACHE_QUALITY_MASK;
    }

    public void setDrawingCacheQuality(int i) {
        setFlags(i, DRAWING_CACHE_QUALITY_MASK);
    }

    public boolean getKeepScreenOn() {
        return (this.mViewFlags & 67108864) != 0;
    }

    public void setKeepScreenOn(boolean z) {
        setFlags(z ? 67108864 : 0, 67108864);
    }

    public int getNextFocusLeftId() {
        return this.mNextFocusLeftId;
    }

    public void setNextFocusLeftId(int i) {
        this.mNextFocusLeftId = i;
    }

    public int getNextFocusRightId() {
        return this.mNextFocusRightId;
    }

    public void setNextFocusRightId(int i) {
        this.mNextFocusRightId = i;
    }

    public int getNextFocusUpId() {
        return this.mNextFocusUpId;
    }

    public void setNextFocusUpId(int i) {
        this.mNextFocusUpId = i;
    }

    public int getNextFocusDownId() {
        return this.mNextFocusDownId;
    }

    public void setNextFocusDownId(int i) {
        this.mNextFocusDownId = i;
    }

    public int getNextFocusForwardId() {
        return this.mNextFocusForwardId;
    }

    public void setNextFocusForwardId(int i) {
        this.mNextFocusForwardId = i;
    }

    public boolean isShown() {
        Object obj;
        View view = this;
        while ((view.mViewFlags & 12) == 0 && (obj = view.mParent) != null) {
            if (!(obj instanceof View)) {
                return true;
            }
            view = (View) obj;
            if (view == null) {
                return false;
            }
        }
        return false;
    }

    protected boolean fitSystemWindows(Rect rect) {
        if ((this.mViewFlags & 2) != 2) {
            return false;
        }
        this.mUserPaddingStart = Integer.MIN_VALUE;
        this.mUserPaddingEnd = Integer.MIN_VALUE;
        ThreadLocal<Rect> threadLocal = sThreadLocal;
        Rect rect2 = threadLocal.get();
        if (rect2 == null) {
            rect2 = new Rect();
            threadLocal.set(rect2);
        }
        boolean zComputeFitSystemWindows = computeFitSystemWindows(rect, rect2);
        this.mUserPaddingLeftInitial = rect2.left;
        this.mUserPaddingRightInitial = rect2.right;
        internalSetPadding(rect2.left, rect2.top, rect2.right, rect2.bottom);
        return zComputeFitSystemWindows;
    }

    protected boolean computeFitSystemWindows(Rect rect, Rect rect2) {
        AttachInfo attachInfo;
        if ((this.mViewFlags & 2048) == 0 || (attachInfo = this.mAttachInfo) == null || ((attachInfo.mSystemUiVisibility & 1536) == 0 && !this.mAttachInfo.mOverscanRequested)) {
            rect2.set(rect);
            rect.set(0, 0, 0, 0);
            return true;
        }
        Rect rect3 = this.mAttachInfo.mOverscanInsets;
        rect2.set(rect3);
        rect.left -= rect3.left;
        rect.top -= rect3.top;
        rect.right -= rect3.right;
        rect.bottom -= rect3.bottom;
        return false;
    }

    public void setFitsSystemWindows(boolean z) {
        setFlags(z ? 2 : 0, 2);
    }

    public boolean getFitsSystemWindows() {
        return (this.mViewFlags & 2) == 2;
    }

    public boolean fitsSystemWindows() {
        return getFitsSystemWindows();
    }

    public void requestFitSystemWindows() {
        ViewParent viewParent = this.mParent;
        if (viewParent != null) {
            viewParent.requestFitSystemWindows();
        }
    }

    public void makeOptionalFitsSystemWindows() {
        setFlags(2048, 2048);
    }

    @ViewDebug.ExportedProperty(mapping = {@ViewDebug.IntToString(from = 0, to = "VISIBLE"), @ViewDebug.IntToString(from = 4, to = "INVISIBLE"), @ViewDebug.IntToString(from = 8, to = "GONE")})
    public int getVisibility() {
        return this.mViewFlags & 12;
    }

    @RemotableViewMethod
    public void setVisibility(int i) {
        setFlags(i, 12);
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.setVisible(i == 0, false);
        }
    }

    @ViewDebug.ExportedProperty
    public boolean isEnabled() {
        return (this.mViewFlags & 32) == 0;
    }

    @RemotableViewMethod
    public void setEnabled(boolean z) {
        if (z == isEnabled()) {
            return;
        }
        setFlags(z ? 0 : 32, 32);
        refreshDrawableState();
        invalidate(true);
        if (z) {
            return;
        }
        cancelPendingInputEvents();
    }

    public void setFocusable(boolean z) {
        if (!z) {
            setFlags(0, 262144);
        }
        setFlags(z ? 1 : 0, 1);
    }

    public void setFocusableInTouchMode(boolean z) {
        setFlags(z ? 262144 : 0, 262144);
        if (z) {
            setFlags(1, 1);
        }
    }

    public void setSoundEffectsEnabled(boolean z) {
        setFlags(z ? 134217728 : 0, 134217728);
    }

    @ViewDebug.ExportedProperty
    public boolean isSoundEffectsEnabled() {
        return 134217728 == (this.mViewFlags & 134217728);
    }

    public void setHapticFeedbackEnabled(boolean z) {
        setFlags(z ? 268435456 : 0, 268435456);
    }

    @ViewDebug.ExportedProperty
    public boolean isHapticFeedbackEnabled() {
        return 268435456 == (this.mViewFlags & 268435456);
    }

    @ViewDebug.ExportedProperty(category = "layout", mapping = {@ViewDebug.IntToString(from = 0, to = "LTR"), @ViewDebug.IntToString(from = 1, to = "RTL"), @ViewDebug.IntToString(from = 2, to = "INHERIT"), @ViewDebug.IntToString(from = 3, to = "LOCALE")})
    public int getRawLayoutDirection() {
        return (this.mPrivateFlags2 & 12) >> 2;
    }

    @RemotableViewMethod
    public void setLayoutDirection(int i) {
        if (getRawLayoutDirection() != i) {
            this.mPrivateFlags2 &= -13;
            resetRtlProperties();
            this.mPrivateFlags2 = ((i << 2) & 12) | this.mPrivateFlags2;
            resolveRtlPropertiesIfNeeded();
            requestLayout();
            invalidate(true);
        }
    }

    @ViewDebug.ExportedProperty(category = "layout", mapping = {@ViewDebug.IntToString(from = 0, to = "RESOLVED_DIRECTION_LTR"), @ViewDebug.IntToString(from = 1, to = "RESOLVED_DIRECTION_RTL")})
    public int getLayoutDirection() {
        if (getContext().getApplicationInfo().targetSdkVersion >= 17) {
            return (this.mPrivateFlags2 & 16) == 16 ? 1 : 0;
        }
        this.mPrivateFlags2 |= 32;
        return 0;
    }

    @ViewDebug.ExportedProperty(category = "layout")
    public boolean isLayoutRtl() {
        return getLayoutDirection() == 1;
    }

    @ViewDebug.ExportedProperty(category = "layout")
    public boolean hasTransientState() {
        return (this.mPrivateFlags2 & Integer.MIN_VALUE) == Integer.MIN_VALUE;
    }

    public void setHasTransientState(boolean z) {
        int i = this.mTransientStateCount;
        int i2 = z ? i + 1 : i - 1;
        this.mTransientStateCount = i2;
        if (i2 < 0) {
            this.mTransientStateCount = 0;
            Log.e(VIEW_LOG_TAG, "hasTransientState decremented below 0: unmatched pair of setHasTransientState calls");
            return;
        }
        if (!(z && i2 == 1) && (z || i2 != 0)) {
            return;
        }
        this.mPrivateFlags2 = (this.mPrivateFlags2 & Integer.MAX_VALUE) | (z ? Integer.MIN_VALUE : 0);
        ViewParent viewParent = this.mParent;
        if (viewParent != null) {
            try {
                viewParent.childHasTransientStateChanged(this, z);
            } catch (AbstractMethodError e) {
                Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
            }
        }
    }

    public boolean isAttachedToWindow() {
        return this.mAttachInfo != null;
    }

    public boolean isLaidOut() {
        return (this.mPrivateFlags3 & 4) == 4;
    }

    public void setWillNotDraw(boolean z) {
        setFlags(z ? 128 : 0, 128);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public boolean willNotDraw() {
        return (this.mViewFlags & 128) == 128;
    }

    public void setWillNotCacheDrawing(boolean z) {
        setFlags(z ? 131072 : 0, 131072);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public boolean willNotCacheDrawing() {
        return (this.mViewFlags & 131072) == 131072;
    }

    @ViewDebug.ExportedProperty
    public boolean isClickable() {
        return (this.mViewFlags & 16384) == 16384;
    }

    public void setClickable(boolean z) {
        setFlags(z ? 16384 : 0, 16384);
    }

    public boolean isLongClickable() {
        return (this.mViewFlags & 2097152) == 2097152;
    }

    public void setLongClickable(boolean z) {
        setFlags(z ? 2097152 : 0, 2097152);
    }

    public void setPressed(boolean z) {
        int i = this.mPrivateFlags;
        boolean z2 = z != ((i & 16384) == 16384);
        if (z) {
            this.mPrivateFlags = i | 16384;
        } else {
            this.mPrivateFlags = i & (-16385);
        }
        if (z2) {
            refreshDrawableState();
        }
        dispatchSetPressed(z);
    }

    public boolean isPressed() {
        return (this.mPrivateFlags & 16384) == 16384;
    }

    public boolean isSaveEnabled() {
        return (this.mViewFlags & 65536) != 65536;
    }

    public void setSaveEnabled(boolean z) {
        setFlags(z ? 0 : 65536, 65536);
    }

    @ViewDebug.ExportedProperty
    public boolean getFilterTouchesWhenObscured() {
        return (this.mViewFlags & 1024) != 0;
    }

    public void setFilterTouchesWhenObscured(boolean z) {
        setFlags(z ? 0 : 1024, 1024);
    }

    public boolean isSaveFromParentEnabled() {
        return (this.mViewFlags & 536870912) != 536870912;
    }

    public void setSaveFromParentEnabled(boolean z) {
        setFlags(z ? 0 : 536870912, 536870912);
    }

    @ViewDebug.ExportedProperty(category = "focus")
    public final boolean isFocusable() {
        return 1 == (this.mViewFlags & 1);
    }

    @ViewDebug.ExportedProperty
    public final boolean isFocusableInTouchMode() {
        return 262144 == (this.mViewFlags & 262144);
    }

    public View focusSearch(int i) {
        ViewParent viewParent = this.mParent;
        if (viewParent != null) {
            return viewParent.focusSearch(this, i);
        }
        return null;
    }

    View findUserSetNextFocus(View view, int i) {
        int i2;
        if (i == 1) {
            final int i3 = this.mID;
            if (i3 == -1) {
                return null;
            }
            return view.findViewByPredicateInsideOut(this, new Predicate<View>() { // from class: android.view.View.2
                public boolean apply(View view2) {
                    return view2.mNextFocusForwardId == i3;
                }
            });
        }
        if (i == 2) {
            int i4 = this.mNextFocusForwardId;
            if (i4 == -1) {
                return null;
            }
            return findViewInsideOutShouldExist(view, i4);
        }
        if (i == 17) {
            int i5 = this.mNextFocusLeftId;
            if (i5 == -1) {
                return null;
            }
            return findViewInsideOutShouldExist(view, i5);
        }
        if (i == 33) {
            int i6 = this.mNextFocusUpId;
            if (i6 == -1) {
                return null;
            }
            return findViewInsideOutShouldExist(view, i6);
        }
        if (i != 66) {
            if (i == 130 && (i2 = this.mNextFocusDownId) != -1) {
                return findViewInsideOutShouldExist(view, i2);
            }
            return null;
        }
        int i7 = this.mNextFocusRightId;
        if (i7 == -1) {
            return null;
        }
        return findViewInsideOutShouldExist(view, i7);
    }

    private View findViewInsideOutShouldExist(View view, int i) {
        if (this.mMatchIdPredicate == null) {
            this.mMatchIdPredicate = new MatchIdPredicate();
        }
        this.mMatchIdPredicate.mId = i;
        View viewFindViewByPredicateInsideOut = view.findViewByPredicateInsideOut(this, this.mMatchIdPredicate);
        if (viewFindViewByPredicateInsideOut == null) {
            Log.w(VIEW_LOG_TAG, "couldn't find view with id " + i);
        }
        return viewFindViewByPredicateInsideOut;
    }

    public ArrayList<View> getFocusables(int i) {
        ArrayList<View> arrayList = new ArrayList<>(24);
        addFocusables(arrayList, i);
        return arrayList;
    }

    public void addFocusables(ArrayList<View> arrayList, int i) {
        addFocusables(arrayList, i, 1);
    }

    public void addFocusables(ArrayList<View> arrayList, int i, int i2) {
        if (arrayList != null && isFocusable()) {
            if ((i2 & 1) == 1 && isInTouchMode() && !isFocusableInTouchMode()) {
                return;
            }
            arrayList.add(this);
        }
    }

    public void findViewsWithText(ArrayList<View> arrayList, CharSequence charSequence, int i) {
        CharSequence charSequence2;
        if (getAccessibilityNodeProvider() != null) {
            if ((i & 4) != 0) {
                arrayList.add(this);
            }
        } else {
            if ((i & 2) == 0 || charSequence == null || charSequence.length() <= 0 || (charSequence2 = this.mContentDescription) == null || charSequence2.length() <= 0) {
                return;
            }
            if (this.mContentDescription.toString().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                arrayList.add(this);
            }
        }
    }

    public ArrayList<View> getTouchables() {
        ArrayList<View> arrayList = new ArrayList<>();
        addTouchables(arrayList);
        return arrayList;
    }

    public void addTouchables(ArrayList<View> arrayList) {
        int i = this.mViewFlags;
        if (((i & 16384) == 16384 || (i & 2097152) == 2097152) && (i & 32) == 0) {
            arrayList.add(this);
        }
    }

    public boolean isAccessibilityFocused() {
        return (this.mPrivateFlags2 & 67108864) != 0;
    }

    public boolean requestAccessibilityFocus() {
        AccessibilityManager accessibilityManager = AccessibilityManager.getInstance(this.mContext);
        if (!accessibilityManager.isEnabled() || !accessibilityManager.isTouchExplorationEnabled() || (this.mViewFlags & 12) != 0) {
            return false;
        }
        int i = this.mPrivateFlags2;
        if ((i & 67108864) == 0) {
            this.mPrivateFlags2 = i | 67108864;
            ViewRootImpl viewRootImpl = getViewRootImpl();
            if (viewRootImpl != null) {
                viewRootImpl.setAccessibilityFocus(this, null);
            }
            invalidate();
            sendAccessibilityEvent(32768);
            return true;
        }
        return false;
    }

    public void clearAccessibilityFocus() {
        View accessibilityFocusedHost;
        clearAccessibilityFocusNoCallbacks();
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl == null || (accessibilityFocusedHost = viewRootImpl.getAccessibilityFocusedHost()) == null || !ViewRootImpl.isViewDescendantOf(accessibilityFocusedHost, this)) {
            return;
        }
        viewRootImpl.setAccessibilityFocus(null, null);
    }

    private void sendAccessibilityHoverEvent(int i) {
        View view = this;
        while (!view.includeForAccessibility()) {
            Object parent = view.getParent();
            if (!(parent instanceof View)) {
                return;
            } else {
                view = (View) parent;
            }
        }
        view.sendAccessibilityEvent(i);
    }

    void clearAccessibilityFocusNoCallbacks() {
        int i = this.mPrivateFlags2;
        if ((67108864 & i) != 0) {
            this.mPrivateFlags2 = i & (-67108865);
            invalidate();
            sendAccessibilityEvent(65536);
        }
    }

    public final boolean requestFocus() {
        return requestFocus(130);
    }

    public final boolean requestFocus(int i) {
        return requestFocus(i, null);
    }

    public boolean requestFocus(int i, Rect rect) {
        return requestFocusNoSearch(i, rect);
    }

    private boolean requestFocusNoSearch(int i, Rect rect) {
        int i2 = this.mViewFlags;
        if ((i2 & 1) != 1 || (i2 & 12) != 0) {
            return false;
        }
        if ((isInTouchMode() && 262144 != (this.mViewFlags & 262144)) || hasAncestorThatBlocksDescendantFocus()) {
            return false;
        }
        handleFocusGainInternal(i, rect);
        return true;
    }

    public final boolean requestFocusFromTouch() {
        ViewRootImpl viewRootImpl;
        if (isInTouchMode() && (viewRootImpl = getViewRootImpl()) != null) {
            viewRootImpl.ensureTouchMode(false);
        }
        return requestFocus(130);
    }

    private boolean hasAncestorThatBlocksDescendantFocus() {
        ViewParent parent = this.mParent;
        while (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            if (viewGroup.getDescendantFocusability() == 393216) {
                return true;
            }
            parent = viewGroup.getParent();
        }
        return false;
    }

    @ViewDebug.ExportedProperty(category = Context.ACCESSIBILITY_SERVICE, mapping = {@ViewDebug.IntToString(from = 0, to = "auto"), @ViewDebug.IntToString(from = 1, to = "yes"), @ViewDebug.IntToString(from = 2, to = "no"), @ViewDebug.IntToString(from = 4, to = "noHideDescendants")})
    public int getImportantForAccessibility() {
        return (this.mPrivateFlags2 & PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_MASK) >> 20;
    }

    public void setAccessibilityLiveRegion(int i) {
        if (i != getAccessibilityLiveRegion()) {
            int i2 = this.mPrivateFlags2 & (-25165825);
            this.mPrivateFlags2 = i2;
            this.mPrivateFlags2 = ((i << 23) & 25165824) | i2;
            notifyViewAccessibilityStateChangedIfNeeded(0);
        }
    }

    public int getAccessibilityLiveRegion() {
        return (this.mPrivateFlags2 & 25165824) >> 23;
    }

    public void setImportantForAccessibility(int i) {
        int importantForAccessibility = getImportantForAccessibility();
        if (i != importantForAccessibility) {
            boolean z = importantForAccessibility == 0 || i == 0;
            boolean z2 = z && includeForAccessibility();
            int i2 = this.mPrivateFlags2 & (-7340033);
            this.mPrivateFlags2 = i2;
            this.mPrivateFlags2 = ((i << 20) & PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_MASK) | i2;
            if (!z || z2 != includeForAccessibility()) {
                notifySubtreeAccessibilityStateChangedIfNeeded();
            } else {
                notifyViewAccessibilityStateChangedIfNeeded(0);
            }
        }
    }

    public boolean isImportantForAccessibility() {
        int i = (this.mPrivateFlags2 & PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_MASK) >> 20;
        if (i == 2 || i == 4) {
            return false;
        }
        for (ViewParent parent = this.mParent; parent instanceof View; parent = parent.getParent()) {
            if (((View) parent).getImportantForAccessibility() == 4) {
                return false;
            }
        }
        return i == 1 || isActionableForAccessibility() || hasListenersForAccessibility() || getAccessibilityNodeProvider() != null || getAccessibilityLiveRegion() != 0;
    }

    public ViewParent getParentForAccessibility() {
        Object obj = this.mParent;
        if (!(obj instanceof View)) {
            return null;
        }
        if (((View) obj).includeForAccessibility()) {
            return this.mParent;
        }
        return this.mParent.getParentForAccessibility();
    }

    public void addChildrenForAccessibility(ArrayList<View> arrayList) {
        if (includeForAccessibility()) {
            arrayList.add(this);
        }
    }

    public boolean includeForAccessibility() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return (attachInfo.mAccessibilityFetchFlags & 8) != 0 || isImportantForAccessibility();
        }
        return false;
    }

    public boolean isActionableForAccessibility() {
        return isClickable() || isLongClickable() || isFocusable();
    }

    private boolean hasListenersForAccessibility() {
        ListenerInfo listenerInfo = getListenerInfo();
        return (this.mTouchDelegate == null && listenerInfo.mOnKeyListener == null && listenerInfo.mOnTouchListener == null && listenerInfo.mOnGenericMotionListener == null && listenerInfo.mOnHoverListener == null && listenerInfo.mOnDragListener == null) ? false : true;
    }

    public void notifyViewAccessibilityStateChangedIfNeeded(int i) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            if (this.mSendViewStateChangedAccessibilityEvent == null) {
                this.mSendViewStateChangedAccessibilityEvent = new SendViewStateChangedAccessibilityEvent();
            }
            this.mSendViewStateChangedAccessibilityEvent.runOrPost(i);
        }
    }

    public void notifySubtreeAccessibilityStateChangedIfNeeded() {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            int i = this.mPrivateFlags2;
            if ((i & 134217728) == 0) {
                this.mPrivateFlags2 = i | 134217728;
                ViewParent viewParent = this.mParent;
                if (viewParent != null) {
                    try {
                        viewParent.notifySubtreeAccessibilityStateChanged(this, this, 1);
                    } catch (AbstractMethodError e) {
                        Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                    }
                }
            }
        }
    }

    void resetSubtreeAccessibilityStateChanged() {
        this.mPrivateFlags2 &= -134217729;
    }

    public boolean performAccessibilityAction(int i, Bundle bundle) {
        AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            return accessibilityDelegate.performAccessibilityAction(this, i, bundle);
        }
        return performAccessibilityActionInternal(i, bundle);
    }

    boolean performAccessibilityActionInternal(int i, Bundle bundle) {
        if (i != 1) {
            if (i != 2) {
                if (i != 4) {
                    if (i != 8) {
                        if (i != 16) {
                            if (i != 32) {
                                if (i != 64) {
                                    if (i != 128) {
                                        if (i != 256) {
                                            if (i != 512) {
                                                if (i != 131072 || getIterableTextForAccessibility() == null) {
                                                    return false;
                                                }
                                                int i2 = bundle != null ? bundle.getInt("ACTION_ARGUMENT_SELECTION_START_INT", -1) : -1;
                                                int i3 = bundle != null ? bundle.getInt("ACTION_ARGUMENT_SELECTION_END_INT", -1) : -1;
                                                if ((getAccessibilitySelectionStart() != i2 || getAccessibilitySelectionEnd() != i3) && i2 == i3) {
                                                    setAccessibilitySelection(i2, i3);
                                                    notifyViewAccessibilityStateChangedIfNeeded(0);
                                                    return true;
                                                }
                                            } else if (bundle != null) {
                                                return traverseAtGranularity(bundle.getInt("ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT"), false, bundle.getBoolean("ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN"));
                                            }
                                        } else if (bundle != null) {
                                            return traverseAtGranularity(bundle.getInt("ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT"), true, bundle.getBoolean("ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN"));
                                        }
                                    } else if (isAccessibilityFocused()) {
                                        clearAccessibilityFocus();
                                        return true;
                                    }
                                } else if (!isAccessibilityFocused()) {
                                    return requestAccessibilityFocus();
                                }
                            } else if (isLongClickable()) {
                                performLongClick();
                                return true;
                            }
                        } else if (isClickable()) {
                            performClick();
                            return true;
                        }
                    } else if (isSelected()) {
                        setSelected(false);
                        return !isSelected();
                    }
                } else if (!isSelected()) {
                    setSelected(true);
                    return isSelected();
                }
            } else if (hasFocus()) {
                clearFocus();
                return !isFocused();
            }
        } else if (!hasFocus()) {
            getViewRootImpl().ensureTouchMode(false);
            return requestFocus();
        }
        return false;
    }

    private boolean traverseAtGranularity(int i, boolean z, boolean z2) {
        AccessibilityIterators.TextSegmentIterator iteratorForGranularity;
        int accessibilitySelectionStart;
        int i2;
        CharSequence iterableTextForAccessibility = getIterableTextForAccessibility();
        if (iterableTextForAccessibility == null || iterableTextForAccessibility.length() == 0 || (iteratorForGranularity = getIteratorForGranularity(i)) == null) {
            return false;
        }
        int accessibilitySelectionEnd = getAccessibilitySelectionEnd();
        if (accessibilitySelectionEnd == -1) {
            accessibilitySelectionEnd = z ? 0 : iterableTextForAccessibility.length();
        }
        int[] iArrFollowing = z ? iteratorForGranularity.following(accessibilitySelectionEnd) : iteratorForGranularity.preceding(accessibilitySelectionEnd);
        if (iArrFollowing == null) {
            return false;
        }
        int i3 = iArrFollowing[0];
        int i4 = iArrFollowing[1];
        if (z2 && isAccessibilitySelectionExtendable()) {
            accessibilitySelectionStart = getAccessibilitySelectionStart();
            if (accessibilitySelectionStart == -1) {
                accessibilitySelectionStart = z ? i3 : i4;
            }
            i2 = z ? i4 : i3;
        } else {
            accessibilitySelectionStart = z ? i4 : i3;
            i2 = accessibilitySelectionStart;
        }
        setAccessibilitySelection(accessibilitySelectionStart, i2);
        sendViewTextTraversedAtGranularityEvent(z ? 256 : 512, i, i3, i4);
        return true;
    }

    public CharSequence getIterableTextForAccessibility() {
        return getContentDescription();
    }

    public int getAccessibilitySelectionStart() {
        return this.mAccessibilityCursorPosition;
    }

    public int getAccessibilitySelectionEnd() {
        return getAccessibilitySelectionStart();
    }

    public void setAccessibilitySelection(int i, int i2) {
        if (i == i2 && i2 == this.mAccessibilityCursorPosition) {
            return;
        }
        if (i >= 0 && i == i2 && i2 <= getIterableTextForAccessibility().length()) {
            this.mAccessibilityCursorPosition = i;
        } else {
            this.mAccessibilityCursorPosition = -1;
        }
        sendAccessibilityEvent(8192);
    }

    private void sendViewTextTraversedAtGranularityEvent(int i, int i2, int i3, int i4) {
        if (this.mParent == null) {
            return;
        }
        AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(131072);
        onInitializeAccessibilityEvent(accessibilityEventObtain);
        onPopulateAccessibilityEvent(accessibilityEventObtain);
        accessibilityEventObtain.setFromIndex(i3);
        accessibilityEventObtain.setToIndex(i4);
        accessibilityEventObtain.setAction(i);
        accessibilityEventObtain.setMovementGranularity(i2);
        this.mParent.requestSendAccessibilityEvent(this, accessibilityEventObtain);
    }

    public AccessibilityIterators.TextSegmentIterator getIteratorForGranularity(int i) {
        CharSequence iterableTextForAccessibility;
        if (i == 1) {
            CharSequence iterableTextForAccessibility2 = getIterableTextForAccessibility();
            if (iterableTextForAccessibility2 == null || iterableTextForAccessibility2.length() <= 0) {
                return null;
            }
            AccessibilityIterators.CharacterTextSegmentIterator characterTextSegmentIterator = AccessibilityIterators.CharacterTextSegmentIterator.getInstance(this.mContext.getResources().getConfiguration().locale);
            characterTextSegmentIterator.initialize(iterableTextForAccessibility2.toString());
            return characterTextSegmentIterator;
        }
        if (i == 2) {
            CharSequence iterableTextForAccessibility3 = getIterableTextForAccessibility();
            if (iterableTextForAccessibility3 == null || iterableTextForAccessibility3.length() <= 0) {
                return null;
            }
            AccessibilityIterators.WordTextSegmentIterator wordTextSegmentIterator = AccessibilityIterators.WordTextSegmentIterator.getInstance(this.mContext.getResources().getConfiguration().locale);
            wordTextSegmentIterator.initialize(iterableTextForAccessibility3.toString());
            return wordTextSegmentIterator;
        }
        if (i != 8 || (iterableTextForAccessibility = getIterableTextForAccessibility()) == null || iterableTextForAccessibility.length() <= 0) {
            return null;
        }
        AccessibilityIterators.ParagraphTextSegmentIterator paragraphTextSegmentIterator = AccessibilityIterators.ParagraphTextSegmentIterator.getInstance();
        paragraphTextSegmentIterator.initialize(iterableTextForAccessibility.toString());
        return paragraphTextSegmentIterator;
    }

    public void dispatchStartTemporaryDetach() {
        clearDisplayList();
        onStartTemporaryDetach();
    }

    public void onStartTemporaryDetach() {
        removeUnsetPressCallback();
        this.mPrivateFlags |= 67108864;
    }

    public void dispatchFinishTemporaryDetach() {
        onFinishTemporaryDetach();
    }

    public KeyEvent.DispatcherState getKeyDispatcherState() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mKeyDispatchState;
        }
        return null;
    }

    public boolean dispatchKeyEventPreIme(KeyEvent keyEvent) {
        return onKeyPreIme(keyEvent.getKeyCode(), keyEvent);
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        InputEventConsistencyVerifier inputEventConsistencyVerifier = this.mInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier != null) {
            inputEventConsistencyVerifier.onKeyEvent(keyEvent, 0);
        }
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo != null && listenerInfo.mOnKeyListener != null && (this.mViewFlags & 32) == 0 && listenerInfo.mOnKeyListener.onKey(this, keyEvent.getKeyCode(), keyEvent)) {
            return true;
        }
        AttachInfo attachInfo = this.mAttachInfo;
        if (keyEvent.dispatch(this, attachInfo != null ? attachInfo.mKeyDispatchState : null, this)) {
            return true;
        }
        InputEventConsistencyVerifier inputEventConsistencyVerifier2 = this.mInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier2 != null) {
            inputEventConsistencyVerifier2.onUnhandledEvent(keyEvent, 0);
        }
        return false;
    }

    public boolean dispatchKeyShortcutEvent(KeyEvent keyEvent) {
        return onKeyShortcut(keyEvent.getKeyCode(), keyEvent);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        ListenerInfo listenerInfo;
        InputEventConsistencyVerifier inputEventConsistencyVerifier = this.mInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier != null) {
            inputEventConsistencyVerifier.onTouchEvent(motionEvent, 0);
        }
        if (onFilterTouchEventForSecurity(motionEvent) && (((listenerInfo = this.mListenerInfo) != null && listenerInfo.mOnTouchListener != null && (this.mViewFlags & 32) == 0 && listenerInfo.mOnTouchListener.onTouch(this, motionEvent)) || onTouchEvent(motionEvent))) {
            return true;
        }
        InputEventConsistencyVerifier inputEventConsistencyVerifier2 = this.mInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier2 != null) {
            inputEventConsistencyVerifier2.onUnhandledEvent(motionEvent, 0);
        }
        return false;
    }

    public boolean onFilterTouchEventForSecurity(MotionEvent motionEvent) {
        return (this.mViewFlags & 1024) == 0 || (motionEvent.getFlags() & 1) == 0;
    }

    public boolean dispatchTrackballEvent(MotionEvent motionEvent) {
        InputEventConsistencyVerifier inputEventConsistencyVerifier = this.mInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier != null) {
            inputEventConsistencyVerifier.onTrackballEvent(motionEvent, 0);
        }
        return onTrackballEvent(motionEvent);
    }

    public boolean dispatchGenericMotionEvent(MotionEvent motionEvent) {
        InputEventConsistencyVerifier inputEventConsistencyVerifier = this.mInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier != null) {
            inputEventConsistencyVerifier.onGenericMotionEvent(motionEvent, 0);
        }
        if ((motionEvent.getSource() & 2) != 0) {
            int action = motionEvent.getAction();
            if (action == 9 || action == 7 || action == 10) {
                if (dispatchHoverEvent(motionEvent)) {
                    return true;
                }
            } else if (dispatchGenericPointerEvent(motionEvent)) {
                return true;
            }
        } else if (dispatchGenericFocusedEvent(motionEvent)) {
            return true;
        }
        if (dispatchGenericMotionEventInternal(motionEvent)) {
            return true;
        }
        InputEventConsistencyVerifier inputEventConsistencyVerifier2 = this.mInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier2 != null) {
            inputEventConsistencyVerifier2.onUnhandledEvent(motionEvent, 0);
        }
        return false;
    }

    private boolean dispatchGenericMotionEventInternal(MotionEvent motionEvent) {
        ListenerInfo listenerInfo = this.mListenerInfo;
        if ((listenerInfo != null && listenerInfo.mOnGenericMotionListener != null && (this.mViewFlags & 32) == 0 && listenerInfo.mOnGenericMotionListener.onGenericMotion(this, motionEvent)) || onGenericMotionEvent(motionEvent)) {
            return true;
        }
        InputEventConsistencyVerifier inputEventConsistencyVerifier = this.mInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier != null) {
            inputEventConsistencyVerifier.onUnhandledEvent(motionEvent, 0);
        }
        return false;
    }

    protected boolean dispatchHoverEvent(MotionEvent motionEvent) {
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo == null || listenerInfo.mOnHoverListener == null || (this.mViewFlags & 32) != 0 || !listenerInfo.mOnHoverListener.onHover(this, motionEvent)) {
            return onHoverEvent(motionEvent);
        }
        return true;
    }

    public final boolean dispatchPointerEvent(MotionEvent motionEvent) {
        if (motionEvent.isTouchEvent()) {
            return dispatchTouchEvent(motionEvent);
        }
        return dispatchGenericMotionEvent(motionEvent);
    }

    public void dispatchWindowFocusChanged(boolean z) {
        onWindowFocusChanged(z);
    }

    public void onWindowFocusChanged(boolean z) {
        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
        if (!z) {
            if (isPressed()) {
                setPressed(false);
            }
            if (inputMethodManagerPeekInstance != null && (this.mPrivateFlags & 2) != 0) {
                inputMethodManagerPeekInstance.focusOut(this);
            }
            removeLongPressCallback();
            removeTapCallback();
            onFocusLost();
        } else if (inputMethodManagerPeekInstance != null && (this.mPrivateFlags & 2) != 0) {
            inputMethodManagerPeekInstance.focusIn(this);
        }
        refreshDrawableState();
    }

    public boolean hasWindowFocus() {
        AttachInfo attachInfo = this.mAttachInfo;
        return attachInfo != null && attachInfo.mHasWindowFocus;
    }

    protected void dispatchVisibilityChanged(View view, int i) {
        onVisibilityChanged(view, i);
    }

    protected void onVisibilityChanged(View view, int i) {
        if (i == 0) {
            if (this.mAttachInfo != null) {
                initialAwakenScrollBars();
            } else {
                this.mPrivateFlags |= 134217728;
            }
        }
    }

    public void dispatchDisplayHint(int i) {
        onDisplayHint(i);
    }

    public void dispatchWindowVisibilityChanged(int i) {
        onWindowVisibilityChanged(i);
    }

    protected void onWindowVisibilityChanged(int i) {
        if (i == 0) {
            initialAwakenScrollBars();
        }
    }

    public int getWindowVisibility() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mWindowVisibility;
        }
        return 8;
    }

    public void getWindowVisibleDisplayFrame(Rect rect) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            try {
                attachInfo.mSession.getDisplayFrame(this.mAttachInfo.mWindow, rect);
                Rect rect2 = this.mAttachInfo.mVisibleInsets;
                rect.left += rect2.left;
                rect.top += rect2.top;
                rect.right -= rect2.right;
                rect.bottom -= rect2.bottom;
                return;
            } catch (RemoteException unused) {
                return;
            }
        }
        DisplayManagerGlobal.getInstance().getRealDisplay(0).getRectSize(rect);
    }

    public void dispatchConfigurationChanged(Configuration configuration) {
        onConfigurationChanged(configuration);
    }

    void dispatchCollectViewAttributes(AttachInfo attachInfo, int i) {
        performCollectViewAttributes(attachInfo, i);
    }

    void performCollectViewAttributes(AttachInfo attachInfo, int i) {
        if ((i & 12) == 0) {
            if ((this.mViewFlags & 67108864) == 67108864) {
                attachInfo.mKeepScreenOn = true;
            }
            attachInfo.mSystemUiVisibility |= this.mSystemUiVisibility;
            ListenerInfo listenerInfo = this.mListenerInfo;
            if (listenerInfo == null || listenerInfo.mOnSystemUiVisibilityChangeListener == null) {
                return;
            }
            attachInfo.mHasSystemUiListeners = true;
        }
    }

    void needGlobalAttributesUpdate(boolean z) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null || attachInfo.mRecomputeGlobalAttributes) {
            return;
        }
        if (z || attachInfo.mKeepScreenOn || attachInfo.mSystemUiVisibility != 0 || attachInfo.mHasSystemUiListeners) {
            attachInfo.mRecomputeGlobalAttributes = true;
        }
    }

    @ViewDebug.ExportedProperty
    public boolean isInTouchMode() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mInTouchMode;
        }
        return ViewRootImpl.isInTouchMode();
    }

    @ViewDebug.CapturedViewProperty
    public final Context getContext() {
        return this.mContext;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (KeyEvent.isConfirmKey(i)) {
            int i2 = this.mViewFlags;
            if ((i2 & 32) == 32) {
                return true;
            }
            if (((i2 & 16384) == 16384 || (i2 & 2097152) == 2097152) && keyEvent.getRepeatCount() == 0) {
                setPressed(true);
                checkForLongClick(0);
                return true;
            }
        }
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (KeyEvent.isConfirmKey(i)) {
            int i2 = this.mViewFlags;
            if ((i2 & 32) == 32) {
                return true;
            }
            if ((i2 & 16384) == 16384 && isPressed()) {
                setPressed(false);
                if (!this.mHasPerformedLongPress) {
                    removeLongPressCallback();
                    return performClick();
                }
            }
        }
        return false;
    }

    public void createContextMenu(ContextMenu contextMenu) {
        ContextMenu.ContextMenuInfo contextMenuInfo = getContextMenuInfo();
        MenuBuilder menuBuilder = (MenuBuilder) contextMenu;
        menuBuilder.setCurrentMenuInfo(contextMenuInfo);
        onCreateContextMenu(contextMenu);
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo != null && listenerInfo.mOnCreateContextMenuListener != null) {
            listenerInfo.mOnCreateContextMenuListener.onCreateContextMenu(contextMenu, this, contextMenuInfo);
        }
        menuBuilder.setCurrentMenuInfo((ContextMenu.ContextMenuInfo) null);
        ViewParent viewParent = this.mParent;
        if (viewParent != null) {
            viewParent.createContextMenu(contextMenu);
        }
    }

    public boolean onHoverEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (!this.mSendingHoverAccessibilityEvents) {
            if ((actionMasked == 9 || actionMasked == 7) && !hasHoveredChild() && pointInView(motionEvent.getX(), motionEvent.getY())) {
                sendAccessibilityHoverEvent(128);
                this.mSendingHoverAccessibilityEvents = true;
            }
        } else if (actionMasked == 10 || (actionMasked == 2 && !pointInView(motionEvent.getX(), motionEvent.getY()))) {
            this.mSendingHoverAccessibilityEvents = false;
            sendAccessibilityHoverEvent(256);
            AttachInfo attachInfo = this.mAttachInfo;
            if (attachInfo != null && !attachInfo.mHasWindowFocus) {
                getViewRootImpl().setAccessibilityFocus(null, null);
            }
        }
        if (!isHoverable()) {
            return false;
        }
        if (actionMasked == 9) {
            setHovered(true);
        } else if (actionMasked == 10) {
            setHovered(false);
        }
        dispatchGenericMotionEventInternal(motionEvent);
        return true;
    }

    private boolean isHoverable() {
        int i = this.mViewFlags;
        if ((i & 32) == 32) {
            return false;
        }
        return (i & 16384) == 16384 || (i & 2097152) == 2097152;
    }

    @ViewDebug.ExportedProperty
    public boolean isHovered() {
        return (this.mPrivateFlags & 268435456) != 0;
    }

    public void setHovered(boolean z) {
        if (z) {
            int i = this.mPrivateFlags;
            if ((i & 268435456) == 0) {
                this.mPrivateFlags = i | 268435456;
                refreshDrawableState();
                onHoverChanged(true);
                return;
            }
            return;
        }
        int i2 = this.mPrivateFlags;
        if ((268435456 & i2) != 0) {
            this.mPrivateFlags = i2 & (-268435457);
            refreshDrawableState();
            onHoverChanged(false);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int i = this.mViewFlags;
        boolean zRequestFocus = false;
        if ((i & 32) == 32) {
            if (motionEvent.getAction() == 1 && (this.mPrivateFlags & 16384) != 0) {
                setPressed(false);
            }
            return (i & 16384) == 16384 || (i & 2097152) == 2097152;
        }
        TouchDelegate touchDelegate = this.mTouchDelegate;
        if (touchDelegate != null && touchDelegate.onTouchEvent(motionEvent)) {
            return true;
        }
        if ((i & 16384) != 16384 && (i & 2097152) != 2097152) {
            return false;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mHasPerformedLongPress = false;
            if (!performButtonActionOnTouchDown(motionEvent)) {
                if (isInScrollingContainer()) {
                    this.mPrivateFlags |= 33554432;
                    if (this.mPendingCheckForTap == null) {
                        this.mPendingCheckForTap = new CheckForTap();
                    }
                    postDelayed(this.mPendingCheckForTap, ViewConfiguration.getTapTimeout());
                } else {
                    setPressed(true);
                    checkForLongClick(0);
                }
            }
        } else if (action == 1) {
            int i2 = this.mPrivateFlags;
            boolean z = (i2 & 33554432) != 0;
            if ((i2 & 16384) != 0 || z) {
                if (isFocusable() && isFocusableInTouchMode() && !isFocused()) {
                    zRequestFocus = requestFocus();
                }
                if (z) {
                    setPressed(true);
                }
                if (!this.mHasPerformedLongPress) {
                    removeLongPressCallback();
                    if (!zRequestFocus) {
                        if (this.mPerformClick == null) {
                            this.mPerformClick = new PerformClick();
                        }
                        if (!post(this.mPerformClick)) {
                            performClick();
                        }
                    }
                }
                if (this.mUnsetPressedState == null) {
                    this.mUnsetPressedState = new UnsetPressedState();
                }
                if (z) {
                    postDelayed(this.mUnsetPressedState, ViewConfiguration.getPressedStateDuration());
                } else if (!post(this.mUnsetPressedState)) {
                    this.mUnsetPressedState.run();
                }
                removeTapCallback();
            }
        } else if (action == 2) {
            if (!pointInView((int) motionEvent.getX(), (int) motionEvent.getY(), this.mTouchSlop)) {
                removeTapCallback();
                if ((this.mPrivateFlags & 16384) != 0) {
                    removeLongPressCallback();
                    setPressed(false);
                }
            }
        } else if (action == 3) {
            setPressed(false);
            removeTapCallback();
            removeLongPressCallback();
        }
        return true;
    }

    public boolean isInScrollingContainer() {
        for (ViewParent parent = getParent(); parent != null && (parent instanceof ViewGroup); parent = parent.getParent()) {
            if (((ViewGroup) parent).shouldDelayChildPressedState()) {
                return true;
            }
        }
        return false;
    }

    private void removeLongPressCallback() {
        CheckForLongPress checkForLongPress = this.mPendingCheckForLongPress;
        if (checkForLongPress != null) {
            removeCallbacks(checkForLongPress);
        }
    }

    private void removePerformClickCallback() {
        PerformClick performClick = this.mPerformClick;
        if (performClick != null) {
            removeCallbacks(performClick);
        }
    }

    private void removeUnsetPressCallback() {
        if ((this.mPrivateFlags & 16384) == 0 || this.mUnsetPressedState == null) {
            return;
        }
        setPressed(false);
        removeCallbacks(this.mUnsetPressedState);
    }

    private void removeTapCallback() {
        CheckForTap checkForTap = this.mPendingCheckForTap;
        if (checkForTap != null) {
            this.mPrivateFlags &= -33554433;
            removeCallbacks(checkForTap);
        }
    }

    public void cancelLongPress() {
        removeLongPressCallback();
        removeTapCallback();
    }

    private void removeSendViewScrolledAccessibilityEventCallback() {
        SendViewScrolledAccessibilityEvent sendViewScrolledAccessibilityEvent = this.mSendViewScrolledAccessibilityEvent;
        if (sendViewScrolledAccessibilityEvent != null) {
            removeCallbacks(sendViewScrolledAccessibilityEvent);
            this.mSendViewScrolledAccessibilityEvent.mIsPending = false;
        }
    }

    public void setTouchDelegate(TouchDelegate touchDelegate) {
        this.mTouchDelegate = touchDelegate;
    }

    public TouchDelegate getTouchDelegate() {
        return this.mTouchDelegate;
    }

    void setFlags(int i, int i2) {
        AttachInfo attachInfo;
        ViewParent viewParent;
        boolean zIsEnabled = AccessibilityManager.getInstance(this.mContext).isEnabled();
        boolean z = zIsEnabled && includeForAccessibility();
        int i3 = this.mViewFlags;
        int i4 = (i2 & i) | ((~i2) & i3);
        this.mViewFlags = i4;
        int i5 = i4 ^ i3;
        if (i5 == 0) {
            return;
        }
        int i6 = this.mPrivateFlags;
        int i7 = i5 & 1;
        if (i7 != 0 && (i6 & 16) != 0) {
            int i8 = i3 & 1;
            if (i8 == 1 && (i6 & 2) != 0) {
                clearFocus();
            } else if (i8 == 0 && (i6 & 2) == 0 && (viewParent = this.mParent) != null) {
                viewParent.focusableViewAvailable(this);
            }
        }
        int i9 = i & 12;
        if (i9 == 0 && (i5 & 12) != 0) {
            this.mPrivateFlags |= 32;
            invalidate(true);
            needGlobalAttributesUpdate(true);
            ViewParent viewParent2 = this.mParent;
            if (viewParent2 != null && this.mBottom > this.mTop && this.mRight > this.mLeft) {
                viewParent2.focusableViewAvailable(this);
            }
        }
        if ((i5 & 8) != 0) {
            needGlobalAttributesUpdate(false);
            requestLayout();
            if ((this.mViewFlags & 12) == 8) {
                if (hasFocus()) {
                    clearFocus();
                }
                clearAccessibilityFocus();
                destroyDrawingCache();
                Object obj = this.mParent;
                if (obj instanceof View) {
                    ((View) obj).invalidate(true);
                }
                this.mPrivateFlags |= 32;
            }
            AttachInfo attachInfo2 = this.mAttachInfo;
            if (attachInfo2 != null) {
                attachInfo2.mViewVisibilityChanged = true;
            }
        }
        if ((i5 & 4) != 0) {
            needGlobalAttributesUpdate(false);
            this.mPrivateFlags |= 32;
            if ((this.mViewFlags & 12) == 4 && getRootView() != this) {
                if (hasFocus()) {
                    clearFocus();
                }
                clearAccessibilityFocus();
            }
            AttachInfo attachInfo3 = this.mAttachInfo;
            if (attachInfo3 != null) {
                attachInfo3.mViewVisibilityChanged = true;
            }
        }
        int i10 = i5 & 12;
        if (i10 != 0) {
            if (i9 != 0) {
                cleanupDraw();
            }
            ViewParent viewParent3 = this.mParent;
            if (viewParent3 instanceof ViewGroup) {
                ((ViewGroup) viewParent3).onChildVisibilityChanged(this, i10, i9);
                ((View) this.mParent).invalidate(true);
            } else if (viewParent3 != null) {
                viewParent3.invalidateChild(this, null);
            }
            dispatchVisibilityChanged(this, i9);
        }
        if ((131072 & i5) != 0) {
            destroyDrawingCache();
        }
        if ((32768 & i5) != 0) {
            destroyDrawingCache();
            this.mPrivateFlags &= -32769;
            invalidateParentCaches();
        }
        if ((DRAWING_CACHE_QUALITY_MASK & i5) != 0) {
            destroyDrawingCache();
            this.mPrivateFlags &= -32769;
        }
        if ((i5 & 128) != 0) {
            if ((this.mViewFlags & 128) != 0) {
                if (this.mBackground != null) {
                    int i11 = this.mPrivateFlags & (-129);
                    this.mPrivateFlags = i11;
                    this.mPrivateFlags = i11 | 256;
                } else {
                    this.mPrivateFlags |= 128;
                }
            } else {
                this.mPrivateFlags &= -129;
            }
            requestLayout();
            invalidate(true);
        }
        if ((67108864 & i5) != 0 && this.mParent != null && (attachInfo = this.mAttachInfo) != null && !attachInfo.mRecomputeGlobalAttributes) {
            this.mParent.recomputeViewAttributes(this);
        }
        if (zIsEnabled) {
            if (i7 == 0 && i10 == 0 && (i5 & 16384) == 0 && (2097152 & i5) == 0) {
                if ((i5 & 32) != 0) {
                    notifyViewAccessibilityStateChangedIfNeeded(0);
                }
            } else if (z != includeForAccessibility()) {
                notifySubtreeAccessibilityStateChangedIfNeeded();
            } else {
                notifyViewAccessibilityStateChangedIfNeeded(0);
            }
        }
    }

    public void bringToFront() {
        ViewParent viewParent = this.mParent;
        if (viewParent != null) {
            viewParent.bringChildToFront(this);
        }
    }

    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            postSendViewScrolledAccessibilityEventCallback();
        }
        this.mBackgroundSizeChanged = true;
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewScrollChanged = true;
        }
    }

    public final ViewParent getParent() {
        return this.mParent;
    }

    public void setScrollX(int i) {
        scrollTo(i, this.mScrollY);
    }

    public void setScrollY(int i) {
        scrollTo(this.mScrollX, i);
    }

    public final int getScrollX() {
        return this.mScrollX;
    }

    public final int getScrollY() {
        return this.mScrollY;
    }

    @ViewDebug.ExportedProperty(category = "layout")
    public final int getWidth() {
        return this.mRight - this.mLeft;
    }

    @ViewDebug.ExportedProperty(category = "layout")
    public final int getHeight() {
        return this.mBottom - this.mTop;
    }

    public void getDrawingRect(Rect rect) {
        rect.left = this.mScrollX;
        rect.top = this.mScrollY;
        rect.right = this.mScrollX + (this.mRight - this.mLeft);
        rect.bottom = this.mScrollY + (this.mBottom - this.mTop);
    }

    public final int getMeasuredWidth() {
        return this.mMeasuredWidth & 16777215;
    }

    public final int getMeasuredWidthAndState() {
        return this.mMeasuredWidth;
    }

    public final int getMeasuredHeight() {
        return this.mMeasuredHeight & 16777215;
    }

    public final int getMeasuredHeightAndState() {
        return this.mMeasuredHeight;
    }

    public final int getMeasuredState() {
        return (this.mMeasuredWidth & (-16777216)) | ((this.mMeasuredHeight >> 16) & (-256));
    }

    public Matrix getMatrix() {
        if (this.mTransformationInfo != null) {
            updateMatrix();
            return this.mTransformationInfo.mMatrix;
        }
        return Matrix.IDENTITY_MATRIX;
    }

    final boolean hasIdentityMatrix() {
        if (this.mTransformationInfo == null) {
            return true;
        }
        updateMatrix();
        return this.mTransformationInfo.mMatrixIsIdentity;
    }

    void ensureTransformationInfo() {
        if (this.mTransformationInfo == null) {
            this.mTransformationInfo = new TransformationInfo();
        }
    }

    private void updateMatrix() {
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo != null && transformationInfo.mMatrixDirty) {
            if ((this.mPrivateFlags & 536870912) == 0 && (this.mRight - this.mLeft != transformationInfo.mPrevWidth || this.mBottom - this.mTop != transformationInfo.mPrevHeight)) {
                transformationInfo.mPrevWidth = this.mRight - this.mLeft;
                transformationInfo.mPrevHeight = this.mBottom - this.mTop;
                transformationInfo.mPivotX = transformationInfo.mPrevWidth / 2.0f;
                transformationInfo.mPivotY = transformationInfo.mPrevHeight / 2.0f;
            }
            transformationInfo.mMatrix.reset();
            if (nonzero(transformationInfo.mRotationX) || nonzero(transformationInfo.mRotationY)) {
                if (transformationInfo.mCamera == null) {
                    transformationInfo.mCamera = new Camera();
                    transformationInfo.matrix3D = new Matrix();
                }
                transformationInfo.mCamera.save();
                transformationInfo.mMatrix.preScale(transformationInfo.mScaleX, transformationInfo.mScaleY, transformationInfo.mPivotX, transformationInfo.mPivotY);
                transformationInfo.mCamera.rotate(transformationInfo.mRotationX, transformationInfo.mRotationY, -transformationInfo.mRotation);
                transformationInfo.mCamera.getMatrix(transformationInfo.matrix3D);
                transformationInfo.matrix3D.preTranslate(-transformationInfo.mPivotX, -transformationInfo.mPivotY);
                transformationInfo.matrix3D.postTranslate(transformationInfo.mPivotX + transformationInfo.mTranslationX, transformationInfo.mPivotY + transformationInfo.mTranslationY);
                transformationInfo.mMatrix.postConcat(transformationInfo.matrix3D);
                transformationInfo.mCamera.restore();
            } else {
                transformationInfo.mMatrix.setTranslate(transformationInfo.mTranslationX, transformationInfo.mTranslationY);
                transformationInfo.mMatrix.preRotate(transformationInfo.mRotation, transformationInfo.mPivotX, transformationInfo.mPivotY);
                transformationInfo.mMatrix.preScale(transformationInfo.mScaleX, transformationInfo.mScaleY, transformationInfo.mPivotX, transformationInfo.mPivotY);
            }
            transformationInfo.mMatrixDirty = false;
            transformationInfo.mMatrixIsIdentity = transformationInfo.mMatrix.isIdentity();
            transformationInfo.mInverseMatrixDirty = true;
        }
    }

    final Matrix getInverseMatrix() {
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo != null) {
            updateMatrix();
            if (transformationInfo.mInverseMatrixDirty) {
                if (transformationInfo.mInverseMatrix == null) {
                    transformationInfo.mInverseMatrix = new Matrix();
                }
                transformationInfo.mMatrix.invert(transformationInfo.mInverseMatrix);
                transformationInfo.mInverseMatrixDirty = false;
            }
            return transformationInfo.mInverseMatrix;
        }
        return Matrix.IDENTITY_MATRIX;
    }

    public float getCameraDistance() {
        ensureTransformationInfo();
        float f = this.mResources.getDisplayMetrics().densityDpi;
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo.mCamera == null) {
            transformationInfo.mCamera = new Camera();
            transformationInfo.matrix3D = new Matrix();
        }
        return -(transformationInfo.mCamera.getLocationZ() * f);
    }

    public void setCameraDistance(float f) {
        invalidateViewProperty(true, false);
        ensureTransformationInfo();
        float f2 = this.mResources.getDisplayMetrics().densityDpi;
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo.mCamera == null) {
            transformationInfo.mCamera = new Camera();
            transformationInfo.matrix3D = new Matrix();
        }
        transformationInfo.mCamera.setLocation(0.0f, 0.0f, (-Math.abs(f)) / f2);
        transformationInfo.mMatrixDirty = true;
        invalidateViewProperty(false, false);
        DisplayList displayList = this.mDisplayList;
        if (displayList != null) {
            displayList.setCameraDistance((-Math.abs(f)) / f2);
        }
        if ((this.mPrivateFlags2 & 268435456) == 268435456) {
            invalidateParentIfNeeded();
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getRotation() {
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo != null) {
            return transformationInfo.mRotation;
        }
        return 0.0f;
    }

    public void setRotation(float f) {
        ensureTransformationInfo();
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo.mRotation != f) {
            invalidateViewProperty(true, false);
            transformationInfo.mRotation = f;
            transformationInfo.mMatrixDirty = true;
            invalidateViewProperty(false, true);
            DisplayList displayList = this.mDisplayList;
            if (displayList != null) {
                displayList.setRotation(f);
            }
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getRotationY() {
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo != null) {
            return transformationInfo.mRotationY;
        }
        return 0.0f;
    }

    public void setRotationY(float f) {
        ensureTransformationInfo();
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo.mRotationY != f) {
            invalidateViewProperty(true, false);
            transformationInfo.mRotationY = f;
            transformationInfo.mMatrixDirty = true;
            invalidateViewProperty(false, true);
            DisplayList displayList = this.mDisplayList;
            if (displayList != null) {
                displayList.setRotationY(f);
            }
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getRotationX() {
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo != null) {
            return transformationInfo.mRotationX;
        }
        return 0.0f;
    }

    public void setRotationX(float f) {
        ensureTransformationInfo();
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo.mRotationX != f) {
            invalidateViewProperty(true, false);
            transformationInfo.mRotationX = f;
            transformationInfo.mMatrixDirty = true;
            invalidateViewProperty(false, true);
            DisplayList displayList = this.mDisplayList;
            if (displayList != null) {
                displayList.setRotationX(f);
            }
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getScaleX() {
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo != null) {
            return transformationInfo.mScaleX;
        }
        return 1.0f;
    }

    public void setScaleX(float f) {
        ensureTransformationInfo();
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo.mScaleX != f) {
            invalidateViewProperty(true, false);
            transformationInfo.mScaleX = f;
            transformationInfo.mMatrixDirty = true;
            invalidateViewProperty(false, true);
            DisplayList displayList = this.mDisplayList;
            if (displayList != null) {
                displayList.setScaleX(f);
            }
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getScaleY() {
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo != null) {
            return transformationInfo.mScaleY;
        }
        return 1.0f;
    }

    public void setScaleY(float f) {
        ensureTransformationInfo();
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo.mScaleY != f) {
            invalidateViewProperty(true, false);
            transformationInfo.mScaleY = f;
            transformationInfo.mMatrixDirty = true;
            invalidateViewProperty(false, true);
            DisplayList displayList = this.mDisplayList;
            if (displayList != null) {
                displayList.setScaleY(f);
            }
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getPivotX() {
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo != null) {
            return transformationInfo.mPivotX;
        }
        return 0.0f;
    }

    public void setPivotX(float f) {
        ensureTransformationInfo();
        TransformationInfo transformationInfo = this.mTransformationInfo;
        boolean z = (this.mPrivateFlags & 536870912) == 536870912;
        if (transformationInfo.mPivotX == f && z) {
            return;
        }
        this.mPrivateFlags |= 536870912;
        invalidateViewProperty(true, false);
        transformationInfo.mPivotX = f;
        transformationInfo.mMatrixDirty = true;
        invalidateViewProperty(false, true);
        DisplayList displayList = this.mDisplayList;
        if (displayList != null) {
            displayList.setPivotX(f);
        }
        if ((this.mPrivateFlags2 & 268435456) == 268435456) {
            invalidateParentIfNeeded();
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getPivotY() {
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo != null) {
            return transformationInfo.mPivotY;
        }
        return 0.0f;
    }

    public void setPivotY(float f) {
        ensureTransformationInfo();
        TransformationInfo transformationInfo = this.mTransformationInfo;
        boolean z = (this.mPrivateFlags & 536870912) == 536870912;
        if (transformationInfo.mPivotY == f && z) {
            return;
        }
        this.mPrivateFlags |= 536870912;
        invalidateViewProperty(true, false);
        transformationInfo.mPivotY = f;
        transformationInfo.mMatrixDirty = true;
        invalidateViewProperty(false, true);
        DisplayList displayList = this.mDisplayList;
        if (displayList != null) {
            displayList.setPivotY(f);
        }
        if ((this.mPrivateFlags2 & 268435456) == 268435456) {
            invalidateParentIfNeeded();
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getAlpha() {
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo != null) {
            return transformationInfo.mAlpha;
        }
        return 1.0f;
    }

    public void setAlpha(float f) {
        ensureTransformationInfo();
        if (this.mTransformationInfo.mAlpha != f) {
            this.mTransformationInfo.mAlpha = f;
            if (onSetAlpha((int) (f * 255.0f))) {
                this.mPrivateFlags |= 262144;
                invalidateParentCaches();
                invalidate(true);
            } else {
                this.mPrivateFlags &= -262145;
                invalidateViewProperty(true, false);
                DisplayList displayList = this.mDisplayList;
                if (displayList != null) {
                    displayList.setAlpha(getFinalAlpha());
                }
            }
        }
    }

    boolean setAlphaNoInvalidation(float f) {
        ensureTransformationInfo();
        if (this.mTransformationInfo.mAlpha == f) {
            return false;
        }
        this.mTransformationInfo.mAlpha = f;
        if (onSetAlpha((int) (f * 255.0f))) {
            this.mPrivateFlags |= 262144;
            return true;
        }
        this.mPrivateFlags &= -262145;
        DisplayList displayList = this.mDisplayList;
        if (displayList == null) {
            return false;
        }
        displayList.setAlpha(getFinalAlpha());
        return false;
    }

    public void setTransitionAlpha(float f) {
        ensureTransformationInfo();
        if (this.mTransformationInfo.mTransitionAlpha != f) {
            this.mTransformationInfo.mTransitionAlpha = f;
            this.mPrivateFlags &= -262145;
            invalidateViewProperty(true, false);
            DisplayList displayList = this.mDisplayList;
            if (displayList != null) {
                displayList.setAlpha(getFinalAlpha());
            }
        }
    }

    private float getFinalAlpha() {
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo != null) {
            return transformationInfo.mAlpha * this.mTransformationInfo.mTransitionAlpha;
        }
        return 1.0f;
    }

    public float getTransitionAlpha() {
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo != null) {
            return transformationInfo.mTransitionAlpha;
        }
        return 1.0f;
    }

    @ViewDebug.CapturedViewProperty
    public final int getTop() {
        return this.mTop;
    }

    public final void setTop(int i) {
        int i2;
        int i3;
        if (i != this.mTop) {
            updateMatrix();
            TransformationInfo transformationInfo = this.mTransformationInfo;
            boolean z = transformationInfo == null || transformationInfo.mMatrixIsIdentity;
            if (z) {
                if (this.mAttachInfo != null) {
                    int i4 = this.mTop;
                    if (i < i4) {
                        i3 = i - i4;
                        i2 = i;
                    } else {
                        i2 = i4;
                        i3 = 0;
                    }
                    invalidate(0, i3, this.mRight - this.mLeft, this.mBottom - i2);
                }
            } else {
                invalidate(true);
            }
            int i5 = this.mRight - this.mLeft;
            int i6 = this.mBottom - this.mTop;
            this.mTop = i;
            DisplayList displayList = this.mDisplayList;
            if (displayList != null) {
                displayList.setTop(i);
            }
            sizeChange(i5, this.mBottom - this.mTop, i5, i6);
            if (!z) {
                if ((this.mPrivateFlags & 536870912) == 0) {
                    this.mTransformationInfo.mMatrixDirty = true;
                }
                this.mPrivateFlags |= 32;
                invalidate(true);
            }
            this.mBackgroundSizeChanged = true;
            invalidateParentIfNeeded();
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.CapturedViewProperty
    public final int getBottom() {
        return this.mBottom;
    }

    public boolean isDirty() {
        return (this.mPrivateFlags & 6291456) != 0;
    }

    public final void setBottom(int i) {
        if (i != this.mBottom) {
            updateMatrix();
            TransformationInfo transformationInfo = this.mTransformationInfo;
            boolean z = transformationInfo == null || transformationInfo.mMatrixIsIdentity;
            if (z) {
                if (this.mAttachInfo != null) {
                    int i2 = this.mBottom;
                    if (i >= i2) {
                        i2 = i;
                    }
                    invalidate(0, 0, this.mRight - this.mLeft, i2 - this.mTop);
                }
            } else {
                invalidate(true);
            }
            int i3 = this.mRight - this.mLeft;
            int i4 = this.mBottom - this.mTop;
            this.mBottom = i;
            DisplayList displayList = this.mDisplayList;
            if (displayList != null) {
                displayList.setBottom(i);
            }
            sizeChange(i3, this.mBottom - this.mTop, i3, i4);
            if (!z) {
                if ((this.mPrivateFlags & 536870912) == 0) {
                    this.mTransformationInfo.mMatrixDirty = true;
                }
                this.mPrivateFlags |= 32;
                invalidate(true);
            }
            this.mBackgroundSizeChanged = true;
            invalidateParentIfNeeded();
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.CapturedViewProperty
    public final int getLeft() {
        return this.mLeft;
    }

    public final void setLeft(int i) {
        int i2;
        int i3;
        if (i != this.mLeft) {
            updateMatrix();
            TransformationInfo transformationInfo = this.mTransformationInfo;
            boolean z = transformationInfo == null || transformationInfo.mMatrixIsIdentity;
            if (z) {
                if (this.mAttachInfo != null) {
                    int i4 = this.mLeft;
                    if (i < i4) {
                        i3 = i - i4;
                        i2 = i;
                    } else {
                        i2 = i4;
                        i3 = 0;
                    }
                    invalidate(i3, 0, this.mRight - i2, this.mBottom - this.mTop);
                }
            } else {
                invalidate(true);
            }
            int i5 = this.mRight - this.mLeft;
            int i6 = this.mBottom - this.mTop;
            this.mLeft = i;
            DisplayList displayList = this.mDisplayList;
            if (displayList != null) {
                displayList.setLeft(i);
            }
            sizeChange(this.mRight - this.mLeft, i6, i5, i6);
            if (!z) {
                if ((this.mPrivateFlags & 536870912) == 0) {
                    this.mTransformationInfo.mMatrixDirty = true;
                }
                this.mPrivateFlags |= 32;
                invalidate(true);
            }
            this.mBackgroundSizeChanged = true;
            invalidateParentIfNeeded();
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.CapturedViewProperty
    public final int getRight() {
        return this.mRight;
    }

    public final void setRight(int i) {
        if (i != this.mRight) {
            updateMatrix();
            TransformationInfo transformationInfo = this.mTransformationInfo;
            boolean z = transformationInfo == null || transformationInfo.mMatrixIsIdentity;
            if (z) {
                if (this.mAttachInfo != null) {
                    int i2 = this.mRight;
                    if (i >= i2) {
                        i2 = i;
                    }
                    invalidate(0, 0, i2 - this.mLeft, this.mBottom - this.mTop);
                }
            } else {
                invalidate(true);
            }
            int i3 = this.mRight - this.mLeft;
            int i4 = this.mBottom - this.mTop;
            this.mRight = i;
            DisplayList displayList = this.mDisplayList;
            if (displayList != null) {
                displayList.setRight(i);
            }
            sizeChange(this.mRight - this.mLeft, i4, i3, i4);
            if (!z) {
                if ((this.mPrivateFlags & 536870912) == 0) {
                    this.mTransformationInfo.mMatrixDirty = true;
                }
                this.mPrivateFlags |= 32;
                invalidate(true);
            }
            this.mBackgroundSizeChanged = true;
            invalidateParentIfNeeded();
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getX() {
        float f = this.mLeft;
        TransformationInfo transformationInfo = this.mTransformationInfo;
        return f + (transformationInfo != null ? transformationInfo.mTranslationX : 0.0f);
    }

    public void setX(float f) {
        setTranslationX(f - this.mLeft);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getY() {
        float f = this.mTop;
        TransformationInfo transformationInfo = this.mTransformationInfo;
        return f + (transformationInfo != null ? transformationInfo.mTranslationY : 0.0f);
    }

    public void setY(float f) {
        setTranslationY(f - this.mTop);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getTranslationX() {
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo != null) {
            return transformationInfo.mTranslationX;
        }
        return 0.0f;
    }

    public void setTranslationX(float f) {
        ensureTransformationInfo();
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo.mTranslationX != f) {
            invalidateViewProperty(true, false);
            transformationInfo.mTranslationX = f;
            transformationInfo.mMatrixDirty = true;
            invalidateViewProperty(false, true);
            DisplayList displayList = this.mDisplayList;
            if (displayList != null) {
                displayList.setTranslationX(f);
            }
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getTranslationY() {
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo != null) {
            return transformationInfo.mTranslationY;
        }
        return 0.0f;
    }

    public void setTranslationY(float f) {
        ensureTransformationInfo();
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo.mTranslationY != f) {
            invalidateViewProperty(true, false);
            transformationInfo.mTranslationY = f;
            transformationInfo.mMatrixDirty = true;
            invalidateViewProperty(false, true);
            DisplayList displayList = this.mDisplayList;
            if (displayList != null) {
                displayList.setTranslationY(f);
            }
            if ((this.mPrivateFlags2 & 268435456) == 268435456) {
                invalidateParentIfNeeded();
            }
        }
    }

    public void getHitRect(Rect rect) {
        AttachInfo attachInfo;
        updateMatrix();
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo == null || transformationInfo.mMatrixIsIdentity || (attachInfo = this.mAttachInfo) == null) {
            rect.set(this.mLeft, this.mTop, this.mRight, this.mBottom);
            return;
        }
        RectF rectF = attachInfo.mTmpTransformRect;
        rectF.set(0.0f, 0.0f, getWidth(), getHeight());
        transformationInfo.mMatrix.mapRect(rectF);
        rect.set(((int) rectF.left) + this.mLeft, ((int) rectF.top) + this.mTop, ((int) rectF.right) + this.mLeft, ((int) rectF.bottom) + this.mTop);
    }

    final boolean pointInView(float f, float f2) {
        return f >= 0.0f && f < ((float) (this.mRight - this.mLeft)) && f2 >= 0.0f && f2 < ((float) (this.mBottom - this.mTop));
    }

    public boolean pointInView(float f, float f2, float f3) {
        float f4 = -f3;
        return f >= f4 && f2 >= f4 && f < ((float) (this.mRight - this.mLeft)) + f3 && f2 < ((float) (this.mBottom - this.mTop)) + f3;
    }

    public void getFocusedRect(Rect rect) {
        getDrawingRect(rect);
    }

    public boolean getGlobalVisibleRect(Rect rect, Point point) {
        int i = this.mRight - this.mLeft;
        int i2 = this.mBottom - this.mTop;
        if (i <= 0 || i2 <= 0) {
            return false;
        }
        rect.set(0, 0, i, i2);
        if (point != null) {
            point.set(-this.mScrollX, -this.mScrollY);
        }
        ViewParent viewParent = this.mParent;
        return viewParent == null || viewParent.getChildVisibleRect(this, rect, point);
    }

    public final boolean getGlobalVisibleRect(Rect rect) {
        return getGlobalVisibleRect(rect, null);
    }

    public final boolean getLocalVisibleRect(Rect rect) {
        AttachInfo attachInfo = this.mAttachInfo;
        Point point = attachInfo != null ? attachInfo.mPoint : new Point();
        if (!getGlobalVisibleRect(rect, point)) {
            return false;
        }
        rect.offset(-point.x, -point.y);
        return true;
    }

    public void offsetTopAndBottom(int i) {
        AttachInfo attachInfo;
        int i2;
        int i3;
        int i4;
        if (i != 0) {
            updateMatrix();
            TransformationInfo transformationInfo = this.mTransformationInfo;
            boolean z = transformationInfo == null || transformationInfo.mMatrixIsIdentity;
            if (!z || this.mDisplayList != null) {
                invalidateViewProperty(false, false);
            } else {
                ViewParent viewParent = this.mParent;
                if (viewParent != null && (attachInfo = this.mAttachInfo) != null) {
                    Rect rect = attachInfo.mTmpInvalRect;
                    if (i < 0) {
                        i2 = this.mTop + i;
                        i3 = this.mBottom;
                        i4 = i;
                    } else {
                        i2 = this.mTop;
                        i3 = this.mBottom + i;
                        i4 = 0;
                    }
                    rect.set(0, i4, this.mRight - this.mLeft, i3 - i2);
                    viewParent.invalidateChild(this, rect);
                }
            }
            this.mTop += i;
            this.mBottom += i;
            DisplayList displayList = this.mDisplayList;
            if (displayList != null) {
                displayList.offsetTopAndBottom(i);
                invalidateViewProperty(false, false);
            } else {
                if (!z) {
                    invalidateViewProperty(false, true);
                }
                invalidateParentIfNeeded();
            }
        }
    }

    public void offsetLeftAndRight(int i) {
        AttachInfo attachInfo;
        int i2;
        int i3;
        if (i != 0) {
            updateMatrix();
            TransformationInfo transformationInfo = this.mTransformationInfo;
            boolean z = transformationInfo == null || transformationInfo.mMatrixIsIdentity;
            if (!z || this.mDisplayList != null) {
                invalidateViewProperty(false, false);
            } else {
                ViewParent viewParent = this.mParent;
                if (viewParent != null && (attachInfo = this.mAttachInfo) != null) {
                    Rect rect = attachInfo.mTmpInvalRect;
                    if (i < 0) {
                        i2 = this.mLeft + i;
                        i3 = this.mRight;
                    } else {
                        i2 = this.mLeft;
                        i3 = this.mRight + i;
                    }
                    rect.set(0, 0, i3 - i2, this.mBottom - this.mTop);
                    viewParent.invalidateChild(this, rect);
                }
            }
            this.mLeft += i;
            this.mRight += i;
            DisplayList displayList = this.mDisplayList;
            if (displayList != null) {
                displayList.offsetLeftAndRight(i);
                invalidateViewProperty(false, false);
            } else {
                if (!z) {
                    invalidateViewProperty(false, true);
                }
                invalidateParentIfNeeded();
            }
        }
    }

    @ViewDebug.ExportedProperty(deepExport = true, prefix = "layout_")
    public ViewGroup.LayoutParams getLayoutParams() {
        return this.mLayoutParams;
    }

    public void setLayoutParams(ViewGroup.LayoutParams layoutParams) {
        Objects.requireNonNull(layoutParams, "Layout parameters cannot be null");
        this.mLayoutParams = layoutParams;
        resolveLayoutParams();
        ViewParent viewParent = this.mParent;
        if (viewParent instanceof ViewGroup) {
            ((ViewGroup) viewParent).onSetLayoutParams(this, layoutParams);
        }
        requestLayout();
    }

    public void resolveLayoutParams() {
        ViewGroup.LayoutParams layoutParams = this.mLayoutParams;
        if (layoutParams != null) {
            layoutParams.resolveLayoutDirection(getLayoutDirection());
        }
    }

    public void scrollTo(int i, int i2) {
        int i3 = this.mScrollX;
        if (i3 == i && this.mScrollY == i2) {
            return;
        }
        int i4 = this.mScrollY;
        this.mScrollX = i;
        this.mScrollY = i2;
        invalidateParentCaches();
        onScrollChanged(this.mScrollX, this.mScrollY, i3, i4);
        if (awakenScrollBars()) {
            return;
        }
        postInvalidateOnAnimation();
    }

    public void scrollBy(int i, int i2) {
        scrollTo(this.mScrollX + i, this.mScrollY + i2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean awakenScrollBars() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        return scrollabilityCache != null && awakenScrollBars(scrollabilityCache.scrollBarDefaultDelayBeforeFade, true);
    }

    private boolean initialAwakenScrollBars() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        return scrollabilityCache != null && awakenScrollBars(scrollabilityCache.scrollBarDefaultDelayBeforeFade * 4, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean awakenScrollBars(int i) {
        return awakenScrollBars(i, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean awakenScrollBars(int i, boolean z) {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        if (scrollabilityCache == null || !scrollabilityCache.fadeScrollBars) {
            return false;
        }
        if (scrollabilityCache.scrollBar == null) {
            scrollabilityCache.scrollBar = new ScrollBarDrawable();
        }
        if (!isHorizontalScrollBarEnabled() && !isVerticalScrollBarEnabled()) {
            return false;
        }
        if (z) {
            postInvalidateOnAnimation();
        }
        if (scrollabilityCache.state == 0) {
            i = Math.max(750, i);
        }
        long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis() + ((long) i);
        scrollabilityCache.fadeStartTime = jCurrentAnimationTimeMillis;
        scrollabilityCache.state = 1;
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mHandler.removeCallbacks(scrollabilityCache);
            this.mAttachInfo.mHandler.postAtTime(scrollabilityCache, jCurrentAnimationTimeMillis);
        }
        return true;
    }

    private boolean skipInvalidate() {
        if ((this.mViewFlags & 12) != 0 && this.mCurrentAnimation == null) {
            ViewParent viewParent = this.mParent;
            if (!(viewParent instanceof ViewGroup) || !((ViewGroup) viewParent).isViewTransitioning(this)) {
                return true;
            }
        }
        return false;
    }

    public void invalidate(Rect rect) {
        if (skipInvalidate()) {
            return;
        }
        int i = this.mPrivateFlags;
        if ((i & 48) == 48 || (i & 32768) == 32768 || (i & Integer.MIN_VALUE) != Integer.MIN_VALUE) {
            int i2 = i & (-32769);
            this.mPrivateFlags = i2;
            int i3 = i2 | Integer.MIN_VALUE;
            this.mPrivateFlags = i3;
            this.mPrivateFlags = i3 | 2097152;
            ViewParent viewParent = this.mParent;
            AttachInfo attachInfo = this.mAttachInfo;
            if (viewParent == null || attachInfo == null) {
                return;
            }
            int i4 = this.mScrollX;
            int i5 = this.mScrollY;
            Rect rect2 = attachInfo.mTmpInvalRect;
            rect2.set(rect.left - i4, rect.top - i5, rect.right - i4, rect.bottom - i5);
            this.mParent.invalidateChild(this, rect2);
        }
    }

    public void invalidate(int i, int i2, int i3, int i4) {
        if (skipInvalidate()) {
            return;
        }
        int i5 = this.mPrivateFlags;
        if ((i5 & 48) == 48 || (i5 & 32768) == 32768 || (i5 & Integer.MIN_VALUE) != Integer.MIN_VALUE) {
            int i6 = i5 & (-32769);
            this.mPrivateFlags = i6;
            int i7 = i6 | Integer.MIN_VALUE;
            this.mPrivateFlags = i7;
            this.mPrivateFlags = i7 | 2097152;
            ViewParent viewParent = this.mParent;
            AttachInfo attachInfo = this.mAttachInfo;
            if (viewParent == null || attachInfo == null || i >= i3 || i2 >= i4) {
                return;
            }
            int i8 = this.mScrollX;
            int i9 = this.mScrollY;
            Rect rect = attachInfo.mTmpInvalRect;
            rect.set(i - i8, i2 - i9, i3 - i8, i4 - i9);
            viewParent.invalidateChild(this, rect);
        }
    }

    public void invalidate() {
        invalidate(true);
    }

    void invalidate(boolean z) {
        if (skipInvalidate()) {
            return;
        }
        int i = this.mPrivateFlags;
        if ((i & 48) == 48 || !((!z || (i & 32768) != 32768) && (i & Integer.MIN_VALUE) == Integer.MIN_VALUE && isOpaque() == this.mLastIsOpaque)) {
            this.mLastIsOpaque = isOpaque();
            int i2 = this.mPrivateFlags & (-33);
            this.mPrivateFlags = i2;
            int i3 = i2 | 2097152;
            this.mPrivateFlags = i3;
            if (z) {
                int i4 = i3 | Integer.MIN_VALUE;
                this.mPrivateFlags = i4;
                this.mPrivateFlags = i4 & (-32769);
            }
            AttachInfo attachInfo = this.mAttachInfo;
            ViewParent viewParent = this.mParent;
            if (viewParent == null || attachInfo == null) {
                return;
            }
            Rect rect = attachInfo.mTmpInvalRect;
            rect.set(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
            viewParent.invalidateChild(this, rect);
        }
    }

    void invalidateViewProperty(boolean z, boolean z2) {
        if (this.mDisplayList == null || (this.mPrivateFlags & 64) == 64) {
            if (z) {
                invalidateParentCaches();
            }
            if (z2) {
                this.mPrivateFlags |= 32;
            }
            invalidate(false);
            return;
        }
        AttachInfo attachInfo = this.mAttachInfo;
        if (this.mParent == null || attachInfo == null) {
            return;
        }
        Rect rect = attachInfo.mTmpInvalRect;
        rect.set(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
        ViewParent viewParent = this.mParent;
        if (viewParent instanceof ViewGroup) {
            ((ViewGroup) viewParent).invalidateChildFast(this, rect);
        } else {
            viewParent.invalidateChild(this, rect);
        }
    }

    void transformRect(Rect rect) {
        if (getMatrix().isIdentity()) {
            return;
        }
        RectF rectF = this.mAttachInfo.mTmpTransformRect;
        rectF.set(rect);
        getMatrix().mapRect(rectF);
        rect.set((int) Math.floor(rectF.left), (int) Math.floor(rectF.top), (int) Math.ceil(rectF.right), (int) Math.ceil(rectF.bottom));
    }

    protected void invalidateParentCaches() {
        Object obj = this.mParent;
        if (obj instanceof View) {
            ((View) obj).mPrivateFlags |= Integer.MIN_VALUE;
        }
    }

    protected void invalidateParentIfNeeded() {
        if (isHardwareAccelerated()) {
            Object obj = this.mParent;
            if (obj instanceof View) {
                ((View) obj).invalidate(true);
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public boolean isOpaque() {
        return (this.mPrivateFlags & 25165824) == 25165824 && getFinalAlpha() >= 1.0f;
    }

    protected void computeOpaqueFlags() {
        int i;
        Drawable drawable = this.mBackground;
        if (drawable != null && drawable.getOpacity() == -1) {
            this.mPrivateFlags |= 8388608;
        } else {
            this.mPrivateFlags &= -8388609;
        }
        int i2 = this.mViewFlags;
        if (((i2 & 512) == 0 && (i2 & 256) == 0) || (i = i2 & 50331648) == 0 || i == 33554432) {
            this.mPrivateFlags |= 16777216;
        } else {
            this.mPrivateFlags &= -16777217;
        }
    }

    protected boolean hasOpaqueScrollbars() {
        return (this.mPrivateFlags & 16777216) == 16777216;
    }

    public Handler getHandler() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mHandler;
        }
        return null;
    }

    public ViewRootImpl getViewRootImpl() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mViewRootImpl;
        }
        return null;
    }

    public boolean post(Runnable runnable) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mHandler.post(runnable);
        }
        ViewRootImpl.getRunQueue().post(runnable);
        return true;
    }

    public boolean postDelayed(Runnable runnable, long j) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mHandler.postDelayed(runnable, j);
        }
        ViewRootImpl.getRunQueue().postDelayed(runnable, j);
        return true;
    }

    public void postOnAnimation(Runnable runnable) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.mChoreographer.postCallback(1, runnable, null);
        } else {
            ViewRootImpl.getRunQueue().post(runnable);
        }
    }

    public void postOnAnimationDelayed(Runnable runnable, long j) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.mChoreographer.postCallbackDelayed(1, runnable, null, j);
        } else {
            ViewRootImpl.getRunQueue().postDelayed(runnable, j);
        }
    }

    public boolean removeCallbacks(Runnable runnable) {
        if (runnable != null) {
            AttachInfo attachInfo = this.mAttachInfo;
            if (attachInfo != null) {
                attachInfo.mHandler.removeCallbacks(runnable);
                attachInfo.mViewRootImpl.mChoreographer.removeCallbacks(1, runnable, null);
            } else {
                ViewRootImpl.getRunQueue().removeCallbacks(runnable);
            }
        }
        return true;
    }

    public void postInvalidate() {
        postInvalidateDelayed(0L);
    }

    public void postInvalidate(int i, int i2, int i3, int i4) {
        postInvalidateDelayed(0L, i, i2, i3, i4);
    }

    public void postInvalidateDelayed(long j) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.dispatchInvalidateDelayed(this, j);
        }
    }

    public void postInvalidateDelayed(long j, int i, int i2, int i3, int i4) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            AttachInfo.InvalidateInfo invalidateInfoObtain = AttachInfo.InvalidateInfo.obtain();
            invalidateInfoObtain.target = this;
            invalidateInfoObtain.left = i;
            invalidateInfoObtain.top = i2;
            invalidateInfoObtain.right = i3;
            invalidateInfoObtain.bottom = i4;
            attachInfo.mViewRootImpl.dispatchInvalidateRectDelayed(invalidateInfoObtain, j);
        }
    }

    public void postInvalidateOnAnimation() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.dispatchInvalidateOnAnimation(this);
        }
    }

    public void postInvalidateOnAnimation(int i, int i2, int i3, int i4) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            AttachInfo.InvalidateInfo invalidateInfoObtain = AttachInfo.InvalidateInfo.obtain();
            invalidateInfoObtain.target = this;
            invalidateInfoObtain.left = i;
            invalidateInfoObtain.top = i2;
            invalidateInfoObtain.right = i3;
            invalidateInfoObtain.bottom = i4;
            attachInfo.mViewRootImpl.dispatchInvalidateRectOnAnimation(invalidateInfoObtain);
        }
    }

    private void postSendViewScrolledAccessibilityEventCallback() {
        if (this.mSendViewScrolledAccessibilityEvent == null) {
            this.mSendViewScrolledAccessibilityEvent = new SendViewScrolledAccessibilityEvent();
        }
        if (this.mSendViewScrolledAccessibilityEvent.mIsPending) {
            return;
        }
        this.mSendViewScrolledAccessibilityEvent.mIsPending = true;
        postDelayed(this.mSendViewScrolledAccessibilityEvent, ViewConfiguration.getSendRecurringAccessibilityEventsInterval());
    }

    public boolean isHorizontalFadingEdgeEnabled() {
        return (this.mViewFlags & 4096) == 4096;
    }

    public void setHorizontalFadingEdgeEnabled(boolean z) {
        if (isHorizontalFadingEdgeEnabled() != z) {
            if (z) {
                initScrollCache();
            }
            this.mViewFlags ^= 4096;
        }
    }

    public boolean isVerticalFadingEdgeEnabled() {
        return (this.mViewFlags & 8192) == 8192;
    }

    public void setVerticalFadingEdgeEnabled(boolean z) {
        if (isVerticalFadingEdgeEnabled() != z) {
            if (z) {
                initScrollCache();
            }
            this.mViewFlags ^= 8192;
        }
    }

    protected float getTopFadingEdgeStrength() {
        return computeVerticalScrollOffset() > 0 ? 1.0f : 0.0f;
    }

    protected float getBottomFadingEdgeStrength() {
        return computeVerticalScrollOffset() + computeVerticalScrollExtent() < computeVerticalScrollRange() ? 1.0f : 0.0f;
    }

    protected float getLeftFadingEdgeStrength() {
        return computeHorizontalScrollOffset() > 0 ? 1.0f : 0.0f;
    }

    protected float getRightFadingEdgeStrength() {
        return computeHorizontalScrollOffset() + computeHorizontalScrollExtent() < computeHorizontalScrollRange() ? 1.0f : 0.0f;
    }

    public boolean isHorizontalScrollBarEnabled() {
        return (this.mViewFlags & 256) == 256;
    }

    public void setHorizontalScrollBarEnabled(boolean z) {
        if (isHorizontalScrollBarEnabled() != z) {
            this.mViewFlags ^= 256;
            computeOpaqueFlags();
            resolvePadding();
        }
    }

    public boolean isVerticalScrollBarEnabled() {
        return (this.mViewFlags & 512) == 512;
    }

    public void setVerticalScrollBarEnabled(boolean z) {
        if (isVerticalScrollBarEnabled() != z) {
            this.mViewFlags ^= 512;
            computeOpaqueFlags();
            resolvePadding();
        }
    }

    protected void recomputePadding() {
        internalSetPadding(this.mUserPaddingLeft, this.mPaddingTop, this.mUserPaddingRight, this.mUserPaddingBottom);
    }

    public void setScrollbarFadingEnabled(boolean z) {
        initScrollCache();
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        scrollabilityCache.fadeScrollBars = z;
        if (z) {
            scrollabilityCache.state = 0;
        } else {
            scrollabilityCache.state = 1;
        }
    }

    public boolean isScrollbarFadingEnabled() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        return scrollabilityCache != null && scrollabilityCache.fadeScrollBars;
    }

    public int getScrollBarDefaultDelayBeforeFade() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        return scrollabilityCache == null ? ViewConfiguration.getScrollDefaultDelay() : scrollabilityCache.scrollBarDefaultDelayBeforeFade;
    }

    public void setScrollBarDefaultDelayBeforeFade(int i) {
        getScrollCache().scrollBarDefaultDelayBeforeFade = i;
    }

    public int getScrollBarFadeDuration() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        return scrollabilityCache == null ? ViewConfiguration.getScrollBarFadeDuration() : scrollabilityCache.scrollBarFadeDuration;
    }

    public void setScrollBarFadeDuration(int i) {
        getScrollCache().scrollBarFadeDuration = i;
    }

    public int getScrollBarSize() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        return scrollabilityCache == null ? ViewConfiguration.get(this.mContext).getScaledScrollBarSize() : scrollabilityCache.scrollBarSize;
    }

    public void setScrollBarSize(int i) {
        getScrollCache().scrollBarSize = i;
    }

    public void setScrollBarStyle(int i) {
        int i2 = this.mViewFlags;
        if (i != (i2 & 50331648)) {
            this.mViewFlags = (i & 50331648) | (i2 & (-50331649));
            computeOpaqueFlags();
            resolvePadding();
        }
    }

    @ViewDebug.ExportedProperty(mapping = {@ViewDebug.IntToString(from = 0, to = "INSIDE_OVERLAY"), @ViewDebug.IntToString(from = 16777216, to = "INSIDE_INSET"), @ViewDebug.IntToString(from = 33554432, to = "OUTSIDE_OVERLAY"), @ViewDebug.IntToString(from = 50331648, to = "OUTSIDE_INSET")})
    public int getScrollBarStyle() {
        return this.mViewFlags & 50331648;
    }

    protected int computeHorizontalScrollRange() {
        return getWidth();
    }

    protected int computeHorizontalScrollOffset() {
        return this.mScrollX;
    }

    protected int computeHorizontalScrollExtent() {
        return getWidth();
    }

    protected int computeVerticalScrollRange() {
        return getHeight();
    }

    protected int computeVerticalScrollOffset() {
        return this.mScrollY;
    }

    protected int computeVerticalScrollExtent() {
        return getHeight();
    }

    public boolean canScrollHorizontally(int i) {
        int iComputeHorizontalScrollOffset = computeHorizontalScrollOffset();
        int iComputeHorizontalScrollRange = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
        if (iComputeHorizontalScrollRange == 0) {
            return false;
        }
        return i < 0 ? iComputeHorizontalScrollOffset > 0 : iComputeHorizontalScrollOffset < iComputeHorizontalScrollRange - 1;
    }

    public boolean canScrollVertically(int i) {
        int iComputeVerticalScrollOffset = computeVerticalScrollOffset();
        int iComputeVerticalScrollRange = computeVerticalScrollRange() - computeVerticalScrollExtent();
        if (iComputeVerticalScrollRange == 0) {
            return false;
        }
        return i < 0 ? iComputeVerticalScrollOffset > 0 : iComputeVerticalScrollOffset < iComputeVerticalScrollRange - 1;
    }

    protected final void onDrawScrollBars(Canvas canvas) {
        int i;
        boolean z;
        int i2;
        int i3;
        int i4;
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        if (scrollabilityCache == null || (i = scrollabilityCache.state) == 0) {
            return;
        }
        if (i == 2) {
            if (scrollabilityCache.interpolatorValues == null) {
                scrollabilityCache.interpolatorValues = new float[1];
            }
            float[] fArr = scrollabilityCache.interpolatorValues;
            if (scrollabilityCache.scrollBarInterpolator.timeToValues(fArr) == Interpolator.Result.FREEZE_END) {
                scrollabilityCache.state = 0;
            } else {
                scrollabilityCache.scrollBar.setAlpha(Math.round(fArr[0]));
            }
            z = true;
        } else {
            scrollabilityCache.scrollBar.setAlpha(255);
            z = false;
        }
        int i5 = this.mViewFlags;
        boolean z2 = (i5 & 256) == 256;
        boolean z3 = (i5 & 512) == 512 && !isVerticalScrollBarHidden();
        if (z3 || z2) {
            int i6 = this.mRight - this.mLeft;
            int i7 = this.mBottom - this.mTop;
            ScrollBarDrawable scrollBarDrawable = scrollabilityCache.scrollBar;
            int i8 = this.mScrollX;
            int i9 = this.mScrollY;
            int i10 = (i5 & 33554432) == 0 ? -1 : 0;
            if (z2) {
                int size = scrollBarDrawable.getSize(false);
                if (size <= 0) {
                    size = scrollabilityCache.scrollBarSize;
                }
                scrollBarDrawable.setParameters(computeHorizontalScrollRange(), computeHorizontalScrollOffset(), computeHorizontalScrollExtent(), false);
                int verticalScrollbarWidth = z3 ? getVerticalScrollbarWidth() : 0;
                int i11 = ((i9 + i7) - size) - (this.mUserPaddingBottom & i10);
                int i12 = i8 + (this.mPaddingLeft & i10);
                int i13 = ((i8 + i6) - (this.mUserPaddingRight & i10)) - verticalScrollbarWidth;
                int i14 = i11 + size;
                i2 = i9;
                i3 = i8;
                onDrawHorizontalScrollBar(canvas, scrollBarDrawable, i12, i11, i13, i14);
                if (z) {
                    invalidate(i12, i11, i13, i14);
                }
            } else {
                i2 = i9;
                i3 = i8;
            }
            if (z3) {
                int size2 = scrollBarDrawable.getSize(true);
                if (size2 <= 0) {
                    size2 = scrollabilityCache.scrollBarSize;
                }
                scrollBarDrawable.setParameters(computeVerticalScrollRange(), computeVerticalScrollOffset(), computeVerticalScrollExtent(), true);
                int i15 = this.mVerticalScrollbarPosition;
                if (i15 == 0) {
                    i15 = isLayoutRtl() ? 1 : 2;
                }
                if (i15 != 1) {
                    i4 = ((i3 + i6) - size2) - (this.mUserPaddingRight & i10);
                } else {
                    i4 = i3 + (this.mUserPaddingLeft & i10);
                }
                int i16 = i4;
                int i17 = i2 + (this.mPaddingTop & i10);
                int i18 = i16 + size2;
                int i19 = (i2 + i7) - (this.mUserPaddingBottom & i10);
                onDrawVerticalScrollBar(canvas, scrollBarDrawable, i16, i17, i18, i19);
                if (z) {
                    invalidate(i16, i17, i18, i19);
                }
            }
        }
    }

    protected void onDrawHorizontalScrollBar(Canvas canvas, Drawable drawable, int i, int i2, int i3, int i4) {
        drawable.setBounds(i, i2, i3, i4);
        drawable.draw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDrawVerticalScrollBar(Canvas canvas, Drawable drawable, int i, int i2, int i3, int i4) {
        drawable.setBounds(i, i2, i3, i4);
        drawable.draw(canvas);
    }

    void assignParent(ViewParent viewParent) {
        if (this.mParent == null) {
            this.mParent = viewParent;
        } else {
            if (viewParent == null) {
                this.mParent = null;
                return;
            }
            throw new RuntimeException("view " + this + " being added, but it already has a parent");
        }
    }

    protected void onAttachedToWindow() {
        if ((this.mPrivateFlags & 512) != 0) {
            this.mParent.requestTransparentRegion(this);
        }
        if ((this.mPrivateFlags & 134217728) != 0) {
            initialAwakenScrollBars();
            this.mPrivateFlags &= -134217729;
        }
        this.mPrivateFlags3 &= -5;
        jumpDrawablesToCurrentState();
        resetSubtreeAccessibilityStateChanged();
        if (isFocused()) {
            InputMethodManager.peekInstance().focusIn(this);
        }
        DisplayList displayList = this.mDisplayList;
        if (displayList != null) {
            displayList.clearDirty();
        }
    }

    public boolean resolveRtlPropertiesIfNeeded() {
        if (!needRtlPropertiesResolution()) {
            return false;
        }
        if (!isLayoutDirectionResolved()) {
            resolveLayoutDirection();
            resolveLayoutParams();
        }
        if (!isTextDirectionResolved()) {
            resolveTextDirection();
        }
        if (!isTextAlignmentResolved()) {
            resolveTextAlignment();
        }
        if (!isDrawablesResolved()) {
            resolveDrawables();
        }
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        onRtlPropertiesChanged(getLayoutDirection());
        return true;
    }

    public void resetRtlProperties() {
        resetResolvedLayoutDirection();
        resetResolvedTextDirection();
        resetResolvedTextAlignment();
        resetResolvedPadding();
        resetResolvedDrawables();
    }

    void dispatchScreenStateChanged(int i) {
        onScreenStateChanged(i);
    }

    private boolean hasRtlSupport() {
        return this.mContext.getApplicationInfo().hasRtlSupport();
    }

    private boolean isRtlCompatibilityMode() {
        return getContext().getApplicationInfo().targetSdkVersion < 17 || !hasRtlSupport();
    }

    private boolean needRtlPropertiesResolution() {
        return (this.mPrivateFlags2 & ALL_RTL_PROPERTIES_RESOLVED) != ALL_RTL_PROPERTIES_RESOLVED;
    }

    public boolean resolveLayoutDirection() {
        this.mPrivateFlags2 &= -49;
        if (hasRtlSupport()) {
            int i = this.mPrivateFlags2;
            int i2 = (i & 12) >> 2;
            if (i2 == 1) {
                this.mPrivateFlags2 = i | 16;
            } else if (i2 == 2) {
                if (!canResolveLayoutDirection()) {
                    return false;
                }
                try {
                    if (!this.mParent.isLayoutDirectionResolved()) {
                        return false;
                    }
                    if (this.mParent.getLayoutDirection() == 1) {
                        this.mPrivateFlags2 |= 16;
                    }
                } catch (AbstractMethodError e) {
                    Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                }
            } else if (i2 == 3 && 1 == TextUtils.getLayoutDirectionFromLocale(Locale.getDefault())) {
                this.mPrivateFlags2 |= 16;
            }
        }
        this.mPrivateFlags2 |= 32;
        return true;
    }

    public boolean canResolveLayoutDirection() {
        if (getRawLayoutDirection() != 2) {
            return true;
        }
        ViewParent viewParent = this.mParent;
        if (viewParent == null) {
            return false;
        }
        try {
            return viewParent.canResolveLayoutDirection();
        } catch (AbstractMethodError e) {
            Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
            return false;
        }
    }

    public void resetResolvedLayoutDirection() {
        this.mPrivateFlags2 &= -49;
    }

    public boolean isLayoutDirectionInherited() {
        return getRawLayoutDirection() == 2;
    }

    public boolean isLayoutDirectionResolved() {
        return (this.mPrivateFlags2 & 32) == 32;
    }

    boolean isPaddingResolved() {
        return (this.mPrivateFlags2 & 536870912) == 536870912;
    }

    public void resolvePadding() {
        int layoutDirection = getLayoutDirection();
        if (!isRtlCompatibilityMode()) {
            if (this.mBackground != null && (!this.mLeftPaddingDefined || !this.mRightPaddingDefined)) {
                ThreadLocal<Rect> threadLocal = sThreadLocal;
                Rect rect = threadLocal.get();
                if (rect == null) {
                    rect = new Rect();
                    threadLocal.set(rect);
                }
                this.mBackground.getPadding(rect);
                if (!this.mLeftPaddingDefined) {
                    this.mUserPaddingLeftInitial = rect.left;
                }
                if (!this.mRightPaddingDefined) {
                    this.mUserPaddingRightInitial = rect.right;
                }
            }
            if (layoutDirection == 1) {
                int i = this.mUserPaddingStart;
                if (i != Integer.MIN_VALUE) {
                    this.mUserPaddingRight = i;
                } else {
                    this.mUserPaddingRight = this.mUserPaddingRightInitial;
                }
                int i2 = this.mUserPaddingEnd;
                if (i2 != Integer.MIN_VALUE) {
                    this.mUserPaddingLeft = i2;
                } else {
                    this.mUserPaddingLeft = this.mUserPaddingLeftInitial;
                }
            } else {
                int i3 = this.mUserPaddingStart;
                if (i3 != Integer.MIN_VALUE) {
                    this.mUserPaddingLeft = i3;
                } else {
                    this.mUserPaddingLeft = this.mUserPaddingLeftInitial;
                }
                int i4 = this.mUserPaddingEnd;
                if (i4 != Integer.MIN_VALUE) {
                    this.mUserPaddingRight = i4;
                } else {
                    this.mUserPaddingRight = this.mUserPaddingRightInitial;
                }
            }
            int i5 = this.mUserPaddingBottom;
            if (i5 < 0) {
                i5 = this.mPaddingBottom;
            }
            this.mUserPaddingBottom = i5;
        }
        internalSetPadding(this.mUserPaddingLeft, this.mPaddingTop, this.mUserPaddingRight, this.mUserPaddingBottom);
        onRtlPropertiesChanged(layoutDirection);
        this.mPrivateFlags2 |= 536870912;
    }

    public void resetResolvedPadding() {
        this.mPrivateFlags2 &= -536870913;
    }

    protected void onDetachedFromWindow() {
        this.mPrivateFlags &= -67108865;
        this.mPrivateFlags3 &= -5;
        removeUnsetPressCallback();
        removeLongPressCallback();
        removePerformClickCallback();
        removeSendViewScrolledAccessibilityEventCallback();
        destroyDrawingCache();
        destroyLayer(false);
        cleanupDraw();
        this.mCurrentAnimation = null;
    }

    private void cleanupDraw() {
        if (this.mAttachInfo != null) {
            DisplayList displayList = this.mDisplayList;
            if (displayList != null) {
                displayList.markDirty();
                this.mAttachInfo.mViewRootImpl.enqueueDisplayList(this.mDisplayList);
            }
            this.mAttachInfo.mViewRootImpl.cancelInvalidate(this);
            return;
        }
        resetDisplayList();
    }

    public boolean executeHardwareAction(Runnable runnable) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null || attachInfo.mHardwareRenderer == null) {
            return false;
        }
        return this.mAttachInfo.mHardwareRenderer.safelyRun(runnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getWindowAttachCount() {
        return this.mWindowAttachCount;
    }

    public IBinder getWindowToken() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mWindowToken;
        }
        return null;
    }

    public WindowId getWindowId() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            return null;
        }
        if (attachInfo.mWindowId == null) {
            try {
                AttachInfo attachInfo2 = this.mAttachInfo;
                attachInfo2.mIWindowId = attachInfo2.mSession.getWindowId(this.mAttachInfo.mWindowToken);
                this.mAttachInfo.mWindowId = new WindowId(this.mAttachInfo.mIWindowId);
            } catch (RemoteException unused) {
            }
        }
        return this.mAttachInfo.mWindowId;
    }

    public IBinder getApplicationWindowToken() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            return null;
        }
        IBinder iBinder = attachInfo.mPanelParentWindowToken;
        return iBinder == null ? attachInfo.mWindowToken : iBinder;
    }

    public Display getDisplay() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mDisplay;
        }
        return null;
    }

    IWindowSession getWindowSession() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mSession;
        }
        return null;
    }

    void dispatchAttachedToWindow(AttachInfo attachInfo, int i) {
        this.mAttachInfo = attachInfo;
        ViewOverlay viewOverlay = this.mOverlay;
        if (viewOverlay != null) {
            viewOverlay.getOverlayView().dispatchAttachedToWindow(attachInfo, i);
        }
        this.mWindowAttachCount++;
        this.mPrivateFlags |= 1024;
        if (this.mFloatingTreeObserver != null) {
            attachInfo.mTreeObserver.merge(this.mFloatingTreeObserver);
            this.mFloatingTreeObserver = null;
        }
        if ((this.mPrivateFlags & 524288) != 0) {
            this.mAttachInfo.mScrollContainers.add(this);
            this.mPrivateFlags |= 1048576;
        }
        performCollectViewAttributes(this.mAttachInfo, i);
        onAttachedToWindow();
        ListenerInfo listenerInfo = this.mListenerInfo;
        CopyOnWriteArrayList copyOnWriteArrayList = listenerInfo != null ? listenerInfo.mOnAttachStateChangeListeners : null;
        if (copyOnWriteArrayList != null && copyOnWriteArrayList.size() > 0) {
            Iterator it = copyOnWriteArrayList.iterator();
            while (it.hasNext()) {
                ((OnAttachStateChangeListener) it.next()).onViewAttachedToWindow(this);
            }
        }
        int i2 = attachInfo.mWindowVisibility;
        if (i2 != 8) {
            onWindowVisibilityChanged(i2);
        }
        if ((this.mPrivateFlags & 1024) != 0) {
            refreshDrawableState();
        }
        needGlobalAttributesUpdate(false);
    }

    void dispatchDetachedFromWindow() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null && attachInfo.mWindowVisibility != 8) {
            onWindowVisibilityChanged(8);
        }
        onDetachedFromWindow();
        ListenerInfo listenerInfo = this.mListenerInfo;
        CopyOnWriteArrayList copyOnWriteArrayList = listenerInfo != null ? listenerInfo.mOnAttachStateChangeListeners : null;
        if (copyOnWriteArrayList != null && copyOnWriteArrayList.size() > 0) {
            Iterator it = copyOnWriteArrayList.iterator();
            while (it.hasNext()) {
                ((OnAttachStateChangeListener) it.next()).onViewDetachedFromWindow(this);
            }
        }
        if ((this.mPrivateFlags & 1048576) != 0) {
            this.mAttachInfo.mScrollContainers.remove(this);
            this.mPrivateFlags &= -1048577;
        }
        this.mAttachInfo = null;
        ViewOverlay viewOverlay = this.mOverlay;
        if (viewOverlay != null) {
            viewOverlay.getOverlayView().dispatchDetachedFromWindow();
        }
    }

    public final void cancelPendingInputEvents() {
        dispatchCancelPendingInputEvents();
    }

    void dispatchCancelPendingInputEvents() {
        this.mPrivateFlags3 &= -17;
        onCancelPendingInputEvents();
        if ((this.mPrivateFlags3 & 16) != 16) {
            throw new SuperNotCalledException("View " + getClass().getSimpleName() + " did not call through to super.onCancelPendingInputEvents()");
        }
    }

    public void onCancelPendingInputEvents() {
        removePerformClickCallback();
        cancelLongPress();
        this.mPrivateFlags3 |= 16;
    }

    public void saveHierarchyState(SparseArray<Parcelable> sparseArray) {
        dispatchSaveInstanceState(sparseArray);
    }

    protected void dispatchSaveInstanceState(SparseArray<Parcelable> sparseArray) {
        if (this.mID == -1 || (this.mViewFlags & 65536) != 0) {
            return;
        }
        this.mPrivateFlags &= -131073;
        Parcelable parcelableOnSaveInstanceState = onSaveInstanceState();
        if ((this.mPrivateFlags & 131072) == 0) {
            throw new IllegalStateException("Derived class did not call super.onSaveInstanceState()");
        }
        if (parcelableOnSaveInstanceState != null) {
            sparseArray.put(this.mID, parcelableOnSaveInstanceState);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        this.mPrivateFlags |= 131072;
        return BaseSavedState.EMPTY_STATE;
    }

    public void restoreHierarchyState(SparseArray<Parcelable> sparseArray) {
        dispatchRestoreInstanceState(sparseArray);
    }

    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        Parcelable parcelable;
        int i = this.mID;
        if (i == -1 || (parcelable = sparseArray.get(i)) == null) {
            return;
        }
        this.mPrivateFlags &= -131073;
        onRestoreInstanceState(parcelable);
        if ((this.mPrivateFlags & 131072) == 0) {
            throw new IllegalStateException("Derived class did not call super.onRestoreInstanceState()");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        this.mPrivateFlags |= 131072;
        if (parcelable != BaseSavedState.EMPTY_STATE && parcelable != null) {
            throw new IllegalArgumentException("Wrong state class, expecting View State but received " + parcelable.getClass().toString() + " instead. This usually happens when two views of different type have the same id in the same hierarchy. This view's id is " + ViewDebug.resolveId(this.mContext, getId()) + ". Make sure other views do not use the same id.");
        }
    }

    public long getDrawingTime() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mDrawingTime;
        }
        return 0L;
    }

    public void setDuplicateParentStateEnabled(boolean z) {
        setFlags(z ? 4194304 : 0, 4194304);
    }

    public boolean isDuplicateParentStateEnabled() {
        return (this.mViewFlags & 4194304) == 4194304;
    }

    public void setLayerType(int i, Paint paint) {
        if (i < 0 || i > 2) {
            throw new IllegalArgumentException("Layer type can only be one of: LAYER_TYPE_NONE, LAYER_TYPE_SOFTWARE or LAYER_TYPE_HARDWARE");
        }
        int i2 = this.mLayerType;
        if (i == i2) {
            if (i == 0 || paint == this.mLayerPaint) {
                return;
            }
            if (paint == null) {
                paint = new Paint();
            }
            this.mLayerPaint = paint;
            invalidateParentCaches();
            invalidate(true);
            return;
        }
        if (i2 == 1) {
            destroyDrawingCache();
        } else if (i2 == 2) {
            destroyLayer(false);
            destroyDrawingCache();
        }
        this.mLayerType = i;
        boolean z = i == 0;
        if (z) {
            paint = null;
        } else if (paint == null) {
            paint = new Paint();
        }
        this.mLayerPaint = paint;
        this.mLocalDirtyRect = z ? null : new Rect();
        invalidateParentCaches();
        invalidate(true);
    }

    public void setLayerPaint(Paint paint) {
        int layerType = getLayerType();
        if (layerType != 0) {
            this.mLayerPaint = paint == null ? new Paint() : paint;
            if (layerType == 2) {
                HardwareLayer hardwareLayer = getHardwareLayer();
                if (hardwareLayer != null) {
                    hardwareLayer.setLayerPaint(paint);
                }
                invalidateViewProperty(false, false);
                return;
            }
            invalidate();
        }
    }

    public int getLayerType() {
        return this.mLayerType;
    }

    public void buildLayer() {
        int i = this.mLayerType;
        if (i == 0) {
            return;
        }
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            throw new IllegalStateException("This view must be attached to a window first");
        }
        if (i != 1) {
            if (i == 2 && attachInfo.mHardwareRenderer != null && attachInfo.mHardwareRenderer.isEnabled() && attachInfo.mHardwareRenderer.validate()) {
                getHardwareLayer();
                if (attachInfo.mTreeObserver.hasOnPreDrawListeners()) {
                    return;
                }
                attachInfo.mViewRootImpl.dispatchFlushHardwareLayerUpdates();
                return;
            }
            return;
        }
        buildDrawingCache(true);
    }

    HardwareLayer getHardwareLayer() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null || attachInfo.mHardwareRenderer == null || !this.mAttachInfo.mHardwareRenderer.isEnabled() || !this.mAttachInfo.mHardwareRenderer.validate()) {
            return null;
        }
        int i = this.mRight - this.mLeft;
        int i2 = this.mBottom - this.mTop;
        if (i != 0 && i2 != 0) {
            if ((this.mPrivateFlags & 32768) == 0 || this.mHardwareLayer == null) {
                HardwareLayer hardwareLayer = this.mHardwareLayer;
                if (hardwareLayer == null) {
                    this.mHardwareLayer = this.mAttachInfo.mHardwareRenderer.createHardwareLayer(i, i2, isOpaque());
                    this.mLocalDirtyRect.set(0, 0, i, i2);
                } else {
                    if ((hardwareLayer.getWidth() != i || this.mHardwareLayer.getHeight() != i2) && this.mHardwareLayer.resize(i, i2)) {
                        this.mLocalDirtyRect.set(0, 0, i, i2);
                    }
                    computeOpaqueFlags();
                    boolean zIsOpaque = isOpaque();
                    if (this.mHardwareLayer.isValid() && this.mHardwareLayer.isOpaque() != zIsOpaque) {
                        this.mHardwareLayer.setOpaque(zIsOpaque);
                        this.mLocalDirtyRect.set(0, 0, i, i2);
                    }
                }
                if (!this.mHardwareLayer.isValid()) {
                    return null;
                }
                this.mHardwareLayer.setLayerPaint(this.mLayerPaint);
                HardwareLayer hardwareLayer2 = this.mHardwareLayer;
                hardwareLayer2.redrawLater(getHardwareLayerDisplayList(hardwareLayer2), this.mLocalDirtyRect);
                ViewRootImpl viewRootImpl = getViewRootImpl();
                if (viewRootImpl != null) {
                    viewRootImpl.pushHardwareLayerUpdate(this.mHardwareLayer);
                }
                this.mLocalDirtyRect.setEmpty();
            }
            return this.mHardwareLayer;
        }
        return null;
    }

    boolean destroyLayer(boolean z) {
        if (this.mHardwareLayer == null) {
            return false;
        }
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null && attachInfo.mHardwareRenderer != null && attachInfo.mHardwareRenderer.isEnabled() && (z || attachInfo.mHardwareRenderer.validate())) {
            attachInfo.mHardwareRenderer.cancelLayerUpdate(this.mHardwareLayer);
            this.mHardwareLayer.destroy();
            this.mHardwareLayer = null;
            invalidate(true);
            invalidateParentCaches();
        }
        return true;
    }

    protected void destroyHardwareResources() {
        resetDisplayList();
        destroyLayer(true);
    }

    public void setDrawingCacheEnabled(boolean z) {
        this.mCachingFailed = false;
        setFlags(z ? 32768 : 0, 32768);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public boolean isDrawingCacheEnabled() {
        return (this.mViewFlags & 32768) == 32768;
    }

    public void outputDirtyFlags(String str, boolean z, int i) {
        Log.d(VIEW_LOG_TAG, str + this + "             DIRTY(" + (this.mPrivateFlags & 6291456) + ") DRAWN(" + (this.mPrivateFlags & 32) + ") CACHE_VALID(" + (this.mPrivateFlags & 32768) + ") INVALIDATED(" + (this.mPrivateFlags & Integer.MIN_VALUE) + ")");
        if (z) {
            this.mPrivateFlags &= i;
        }
        if (this instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) this;
            int childCount = viewGroup.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                viewGroup.getChildAt(i2).outputDirtyFlags(str + "  ", z, i);
            }
        }
    }

    public boolean canHaveDisplayList() {
        AttachInfo attachInfo = this.mAttachInfo;
        return (attachInfo == null || attachInfo.mHardwareRenderer == null) ? false : true;
    }

    public HardwareRenderer getHardwareRenderer() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mHardwareRenderer;
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:60:0x0100  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0104  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private android.view.DisplayList getDisplayList(android.view.DisplayList r17, boolean r18) {
        /*
            Method dump skipped, instruction units count: 281
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.View.getDisplayList(android.view.DisplayList, boolean):android.view.DisplayList");
    }

    private DisplayList getHardwareLayerDisplayList(HardwareLayer hardwareLayer) {
        DisplayList displayList = getDisplayList(hardwareLayer.getDisplayList(), true);
        hardwareLayer.setDisplayList(displayList);
        return displayList;
    }

    public DisplayList getDisplayList() {
        DisplayList displayList = getDisplayList(this.mDisplayList, false);
        this.mDisplayList = displayList;
        return displayList;
    }

    private void clearDisplayList() {
        DisplayList displayList = this.mDisplayList;
        if (displayList != null) {
            displayList.clear();
        }
    }

    private void resetDisplayList() {
        DisplayList displayList = this.mDisplayList;
        if (displayList != null) {
            displayList.reset();
        }
    }

    public Bitmap getDrawingCache() {
        return getDrawingCache(false);
    }

    public Bitmap getDrawingCache(boolean z) {
        int i = this.mViewFlags;
        if ((i & 131072) == 131072) {
            return null;
        }
        if ((i & 32768) == 32768) {
            buildDrawingCache(z);
        }
        return z ? this.mDrawingCache : this.mUnscaledDrawingCache;
    }

    public void destroyDrawingCache() {
        Bitmap bitmap = this.mDrawingCache;
        if (bitmap != null) {
            bitmap.recycle();
            this.mDrawingCache = null;
        }
        Bitmap bitmap2 = this.mUnscaledDrawingCache;
        if (bitmap2 != null) {
            bitmap2.recycle();
            this.mUnscaledDrawingCache = null;
        }
    }

    public void setDrawingCacheBackgroundColor(int i) {
        if (i != this.mDrawingCacheBackgroundColor) {
            this.mDrawingCacheBackgroundColor = i;
            this.mPrivateFlags &= -32769;
        }
    }

    public int getDrawingCacheBackgroundColor() {
        return this.mDrawingCacheBackgroundColor;
    }

    public void buildDrawingCache() {
        buildDrawingCache(false);
    }

    public void buildDrawingCache(boolean z) {
        Canvas canvas;
        if ((this.mPrivateFlags & 32768) != 0) {
            if (z) {
                if (this.mDrawingCache != null) {
                    return;
                }
            } else if (this.mUnscaledDrawingCache != null) {
                return;
            }
        }
        this.mCachingFailed = false;
        int i = this.mRight - this.mLeft;
        int i2 = this.mBottom - this.mTop;
        AttachInfo attachInfo = this.mAttachInfo;
        boolean z2 = true;
        boolean z3 = attachInfo != null && attachInfo.mScalingRequired;
        if (z && z3) {
            i = (int) ((i * attachInfo.mApplicationScale) + 0.5f);
            i2 = (int) ((i2 * attachInfo.mApplicationScale) + 0.5f);
        }
        int i3 = this.mDrawingCacheBackgroundColor;
        boolean z4 = i3 != 0 || isOpaque();
        boolean z5 = attachInfo != null && attachInfo.mUse32BitDrawingCache;
        long j = i * i2 * ((!z4 || z5) ? 4 : 2);
        long scaledMaximumDrawingCacheSize = ViewConfiguration.get(this.mContext).getScaledMaximumDrawingCacheSize();
        if (i <= 0 || i2 <= 0 || j > scaledMaximumDrawingCacheSize) {
            if (i > 0 && i2 > 0) {
                Log.w(VIEW_LOG_TAG, "View too large to fit into drawing cache, needs " + j + " bytes, only " + scaledMaximumDrawingCacheSize + " available");
            }
            destroyDrawingCache();
            this.mCachingFailed = true;
            return;
        }
        Bitmap bitmapCreateBitmap = z ? this.mDrawingCache : this.mUnscaledDrawingCache;
        if (bitmapCreateBitmap == null || bitmapCreateBitmap.getWidth() != i || bitmapCreateBitmap.getHeight() != i2) {
            Bitmap.Config config = (z4 && !z5) ? Bitmap.Config.RGB_565 : Bitmap.Config.ARGB_8888;
            if (bitmapCreateBitmap != null) {
                bitmapCreateBitmap.recycle();
            }
            try {
                bitmapCreateBitmap = Bitmap.createBitmap(this.mResources.getDisplayMetrics(), i, i2, config);
                bitmapCreateBitmap.setDensity(getResources().getDisplayMetrics().densityDpi);
                if (z) {
                    this.mDrawingCache = bitmapCreateBitmap;
                } else {
                    this.mUnscaledDrawingCache = bitmapCreateBitmap;
                }
                if (z4 && z5) {
                    bitmapCreateBitmap.setHasAlpha(false);
                }
                z2 = i3 != 0;
            } catch (OutOfMemoryError unused) {
                if (z) {
                    this.mDrawingCache = null;
                } else {
                    this.mUnscaledDrawingCache = null;
                }
                this.mCachingFailed = true;
                return;
            }
        }
        if (attachInfo != null) {
            canvas = attachInfo.mCanvas;
            if (canvas == null) {
                canvas = new Canvas();
            }
            canvas.setBitmap(bitmapCreateBitmap);
            attachInfo.mCanvas = null;
        } else {
            canvas = new Canvas(bitmapCreateBitmap);
        }
        if (z2) {
            bitmapCreateBitmap.eraseColor(i3);
        }
        computeScroll();
        int iSave = canvas.save();
        if (z && z3) {
            float f = attachInfo.mApplicationScale;
            canvas.scale(f, f);
        }
        canvas.translate(-this.mScrollX, -this.mScrollY);
        this.mPrivateFlags |= 32;
        AttachInfo attachInfo2 = this.mAttachInfo;
        if (attachInfo2 == null || !attachInfo2.mHardwareAccelerated || this.mLayerType != 0) {
            this.mPrivateFlags = 32768 | this.mPrivateFlags;
        }
        int i4 = this.mPrivateFlags;
        if ((i4 & 128) == 128) {
            this.mPrivateFlags = i4 & (-6291457);
            dispatchDraw(canvas);
            ViewOverlay viewOverlay = this.mOverlay;
            if (viewOverlay != null && !viewOverlay.isEmpty()) {
                this.mOverlay.getOverlayView().draw(canvas);
            }
        } else {
            draw(canvas);
        }
        canvas.restoreToCount(iSave);
        canvas.setBitmap(null);
        if (attachInfo != null) {
            attachInfo.mCanvas = canvas;
        }
    }

    Bitmap createSnapshot(Bitmap.Config config, int i, boolean z) {
        Canvas canvas;
        int i2 = this.mRight - this.mLeft;
        int i3 = this.mBottom - this.mTop;
        AttachInfo attachInfo = this.mAttachInfo;
        float f = attachInfo != null ? attachInfo.mApplicationScale : 1.0f;
        int i4 = (int) ((i2 * f) + 0.5f);
        int i5 = (int) ((i3 * f) + 0.5f);
        DisplayMetrics displayMetrics = this.mResources.getDisplayMetrics();
        if (i4 <= 0) {
            i4 = 1;
        }
        if (i5 <= 0) {
            i5 = 1;
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(displayMetrics, i4, i5, config);
        if (bitmapCreateBitmap == null) {
            throw new OutOfMemoryError();
        }
        Resources resources = getResources();
        if (resources != null) {
            bitmapCreateBitmap.setDensity(resources.getDisplayMetrics().densityDpi);
        }
        if (attachInfo != null) {
            canvas = attachInfo.mCanvas;
            if (canvas == null) {
                canvas = new Canvas();
            }
            canvas.setBitmap(bitmapCreateBitmap);
            attachInfo.mCanvas = null;
        } else {
            canvas = new Canvas(bitmapCreateBitmap);
        }
        if (((-16777216) & i) != 0) {
            bitmapCreateBitmap.eraseColor(i);
        }
        computeScroll();
        int iSave = canvas.save();
        canvas.scale(f, f);
        canvas.translate(-this.mScrollX, -this.mScrollY);
        int i6 = this.mPrivateFlags;
        int i7 = (-6291457) & i6;
        this.mPrivateFlags = i7;
        if ((i7 & 128) == 128) {
            dispatchDraw(canvas);
            ViewOverlay viewOverlay = this.mOverlay;
            if (viewOverlay != null && !viewOverlay.isEmpty()) {
                this.mOverlay.getOverlayView().draw(canvas);
            }
        } else {
            draw(canvas);
        }
        this.mPrivateFlags = i6;
        canvas.restoreToCount(iSave);
        canvas.setBitmap(null);
        if (attachInfo != null) {
            attachInfo.mCanvas = canvas;
        }
        return bitmapCreateBitmap;
    }

    protected int getFadeTop(boolean z) {
        int i = this.mPaddingTop;
        return z ? i + getTopPaddingOffset() : i;
    }

    protected int getFadeHeight(boolean z) {
        int topPaddingOffset = this.mPaddingTop;
        if (z) {
            topPaddingOffset += getTopPaddingOffset();
        }
        return ((this.mBottom - this.mTop) - this.mPaddingBottom) - topPaddingOffset;
    }

    public boolean isHardwareAccelerated() {
        AttachInfo attachInfo = this.mAttachInfo;
        return attachInfo != null && attachInfo.mHardwareAccelerated;
    }

    public void setClipBounds(Rect rect) {
        if (rect != null) {
            if (rect.equals(this.mClipBounds)) {
                return;
            }
            Rect rect2 = this.mClipBounds;
            if (rect2 == null) {
                invalidate();
                this.mClipBounds = new Rect(rect);
                return;
            } else {
                invalidate(Math.min(rect2.left, rect.left), Math.min(this.mClipBounds.top, rect.top), Math.max(this.mClipBounds.right, rect.right), Math.max(this.mClipBounds.bottom, rect.bottom));
                this.mClipBounds.set(rect);
                return;
            }
        }
        if (this.mClipBounds != null) {
            invalidate();
            this.mClipBounds = null;
        }
    }

    public Rect getClipBounds() {
        if (this.mClipBounds != null) {
            return new Rect(this.mClipBounds);
        }
        return null;
    }

    private boolean drawAnimation(ViewGroup viewGroup, long j, Animation animation, boolean z) {
        int i = viewGroup.mGroupFlags;
        if (!animation.isInitialized()) {
            animation.initialize(this.mRight - this.mLeft, this.mBottom - this.mTop, viewGroup.getWidth(), viewGroup.getHeight());
            animation.initializeInvalidateRegion(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
            AttachInfo attachInfo = this.mAttachInfo;
            if (attachInfo != null) {
                animation.setListenerHandler(attachInfo.mHandler);
            }
            onAnimationStart();
        }
        Transformation childTransformation = viewGroup.getChildTransformation();
        boolean transformation = animation.getTransformation(j, childTransformation, 1.0f);
        if (z && this.mAttachInfo.mApplicationScale != 1.0f) {
            if (viewGroup.mInvalidationTransformation == null) {
                viewGroup.mInvalidationTransformation = new Transformation();
            }
            childTransformation = viewGroup.mInvalidationTransformation;
            animation.getTransformation(j, childTransformation, 1.0f);
        }
        Transformation transformation2 = childTransformation;
        if (transformation) {
            if (animation.willChangeBounds()) {
                if (viewGroup.mInvalidateRegion == null) {
                    viewGroup.mInvalidateRegion = new RectF();
                }
                RectF rectF = viewGroup.mInvalidateRegion;
                animation.getInvalidateRegion(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop, rectF, transformation2);
                viewGroup.mPrivateFlags |= 64;
                int i2 = this.mLeft + ((int) rectF.left);
                int i3 = this.mTop + ((int) rectF.top);
                viewGroup.invalidate(i2, i3, ((int) (rectF.width() + 0.5f)) + i2, ((int) (rectF.height() + 0.5f)) + i3);
            } else if ((i & 144) == 128) {
                viewGroup.mGroupFlags |= 4;
            } else if ((i & 4) == 0) {
                viewGroup.mPrivateFlags |= 64;
                viewGroup.invalidate(this.mLeft, this.mTop, this.mRight, this.mBottom);
            }
        }
        return transformation;
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x0061  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void setDisplayListProperties(android.view.DisplayList r14) {
        /*
            Method dump skipped, instruction units count: 227
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.View.setDisplayListProperties(android.view.DisplayList):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:103:0x0172  */
    /* JADX WARN: Removed duplicated region for block: B:109:0x017e  */
    /* JADX WARN: Removed duplicated region for block: B:113:0x0185  */
    /* JADX WARN: Removed duplicated region for block: B:116:0x018d  */
    /* JADX WARN: Removed duplicated region for block: B:117:0x0199  */
    /* JADX WARN: Removed duplicated region for block: B:125:0x01b8  */
    /* JADX WARN: Removed duplicated region for block: B:126:0x01bb  */
    /* JADX WARN: Removed duplicated region for block: B:140:0x01f8  */
    /* JADX WARN: Removed duplicated region for block: B:145:0x0209  */
    /* JADX WARN: Removed duplicated region for block: B:146:0x020b  */
    /* JADX WARN: Removed duplicated region for block: B:147:0x020e  */
    /* JADX WARN: Removed duplicated region for block: B:149:0x0212  */
    /* JADX WARN: Removed duplicated region for block: B:158:0x0256  */
    /* JADX WARN: Removed duplicated region for block: B:165:0x0277  */
    /* JADX WARN: Removed duplicated region for block: B:168:0x027e  */
    /* JADX WARN: Removed duplicated region for block: B:169:0x0289  */
    /* JADX WARN: Removed duplicated region for block: B:171:0x028c  */
    /* JADX WARN: Removed duplicated region for block: B:172:0x0292  */
    /* JADX WARN: Removed duplicated region for block: B:175:0x02a1  */
    /* JADX WARN: Removed duplicated region for block: B:206:0x034a  */
    /* JADX WARN: Removed duplicated region for block: B:209:0x034f A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:214:0x0363  */
    /* JADX WARN: Removed duplicated region for block: B:217:0x036d  */
    /* JADX WARN: Removed duplicated region for block: B:233:0x03ba  */
    /* JADX WARN: Removed duplicated region for block: B:240:0x03e6  */
    /* JADX WARN: Removed duplicated region for block: B:259:0x0441  */
    /* JADX WARN: Removed duplicated region for block: B:262:0x0448 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:269:0x045a A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0060  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x0085  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x00bd  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x0100  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0115  */
    /* JADX WARN: Removed duplicated region for block: B:88:0x0147  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x014e  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x015c  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x015f  */
    /* JADX WARN: Removed duplicated region for block: B:98:0x0168  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    boolean draw(android.graphics.Canvas r32, android.view.ViewGroup r33, long r34) {
        /*
            Method dump skipped, instruction units count: 1133
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.View.draw(android.graphics.Canvas, android.view.ViewGroup, long):boolean");
    }

    public void draw(Canvas canvas) {
        float f;
        float f2;
        boolean z;
        boolean z2;
        float f3;
        float f4;
        boolean z3;
        boolean z4;
        int i;
        float f5;
        Drawable drawable;
        AttachInfo attachInfo;
        Rect rect = this.mClipBounds;
        if (rect != null) {
            canvas.clipRect(rect);
        }
        int i2 = this.mPrivateFlags;
        boolean z5 = (6291456 & i2) == 4194304 && ((attachInfo = this.mAttachInfo) == null || !attachInfo.mIgnoreDirtyState);
        this.mPrivateFlags = (i2 & (-6291457)) | 32;
        if (!z5 && (drawable = this.mBackground) != null) {
            int i3 = this.mScrollX;
            int i4 = this.mScrollY;
            if (this.mBackgroundSizeChanged) {
                drawable.setBounds(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
                this.mBackgroundSizeChanged = false;
            }
            if ((i3 | i4) == 0) {
                drawable.draw(canvas);
            } else {
                canvas.translate(i3, i4);
                drawable.draw(canvas);
                canvas.translate(-i3, -i4);
            }
        }
        int i5 = this.mViewFlags;
        boolean z6 = (i5 & 4096) != 0;
        boolean z7 = (i5 & 8192) != 0;
        if (!z7 && !z6) {
            if (!z5) {
                onDraw(canvas);
            }
            dispatchDraw(canvas);
            onDrawScrollBars(canvas);
            ViewOverlay viewOverlay = this.mOverlay;
            if (viewOverlay == null || viewOverlay.isEmpty()) {
                return;
            }
            this.mOverlay.getOverlayView().dispatchDraw(canvas);
            return;
        }
        int leftPaddingOffset = this.mPaddingLeft;
        boolean zIsPaddingOffsetRequired = isPaddingOffsetRequired();
        if (zIsPaddingOffsetRequired) {
            leftPaddingOffset += getLeftPaddingOffset();
        }
        int i6 = this.mScrollX + leftPaddingOffset;
        int rightPaddingOffset = (((this.mRight + i6) - this.mLeft) - this.mPaddingRight) - leftPaddingOffset;
        int fadeTop = getFadeTop(zIsPaddingOffsetRequired) + this.mScrollY;
        int fadeHeight = getFadeHeight(zIsPaddingOffsetRequired) + fadeTop;
        if (zIsPaddingOffsetRequired) {
            rightPaddingOffset += getRightPaddingOffset();
            fadeHeight += getBottomPaddingOffset();
        }
        int i7 = fadeHeight;
        int i8 = rightPaddingOffset;
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        float f6 = scrollabilityCache.fadingEdgeLength;
        int i9 = (int) f6;
        if (z7 && fadeTop + i9 > i7 - i9) {
            i9 = (i7 - fadeTop) / 2;
        }
        if (z6 && i6 + i9 > i8 - i9) {
            i9 = (i8 - i6) / 2;
        }
        int i10 = i9;
        if (z7) {
            float fMax = Math.max(0.0f, Math.min(1.0f, getTopFadingEdgeStrength()));
            boolean z8 = fMax * f6 > 1.0f;
            float fMax2 = Math.max(0.0f, Math.min(1.0f, getBottomFadingEdgeStrength()));
            f2 = fMax2;
            z = z8;
            z2 = fMax2 * f6 > 1.0f;
            f = fMax;
        } else {
            f = 0.0f;
            f2 = 0.0f;
            z = false;
            z2 = false;
        }
        if (z6) {
            float fMax3 = Math.max(0.0f, Math.min(1.0f, getLeftFadingEdgeStrength()));
            boolean z9 = fMax3 * f6 > 1.0f;
            float fMax4 = Math.max(0.0f, Math.min(1.0f, getRightFadingEdgeStrength()));
            f3 = fMax3;
            z3 = z9;
            f4 = fMax4;
            z4 = fMax4 * f6 > 1.0f;
        } else {
            f3 = 0.0f;
            f4 = 0.0f;
            z3 = false;
            z4 = false;
        }
        int saveCount = canvas.getSaveCount();
        int solidColor = getSolidColor();
        if (solidColor == 0) {
            if (z) {
                i = saveCount;
                f5 = 1.0f;
                canvas.saveLayer(i6, fadeTop, i8, fadeTop + i10, null, 4);
            } else {
                i = saveCount;
                f5 = 1.0f;
            }
            if (z2) {
                canvas.saveLayer(i6, i7 - i10, i8, i7, null, 4);
            }
            if (z3) {
                canvas.saveLayer(i6, fadeTop, i6 + i10, i7, null, 4);
            }
            if (z4) {
                canvas.saveLayer(i8 - i10, fadeTop, i8, i7, null, 4);
            }
        } else {
            i = saveCount;
            f5 = 1.0f;
            scrollabilityCache.setFadeColor(solidColor);
        }
        if (!z5) {
            onDraw(canvas);
        }
        dispatchDraw(canvas);
        Paint paint = scrollabilityCache.paint;
        Matrix matrix = scrollabilityCache.matrix;
        Shader shader = scrollabilityCache.shader;
        if (z) {
            matrix.setScale(f5, f6 * f);
            float f7 = i6;
            float f8 = fadeTop;
            matrix.postTranslate(f7, f8);
            shader.setLocalMatrix(matrix);
            canvas.drawRect(f7, f8, i8, fadeTop + i10, paint);
        }
        if (z2) {
            matrix.setScale(f5, f6 * f2);
            matrix.postRotate(180.0f);
            float f9 = i6;
            float f10 = i7;
            matrix.postTranslate(f9, f10);
            shader.setLocalMatrix(matrix);
            canvas.drawRect(f9, i7 - i10, i8, f10, paint);
        }
        if (z3) {
            matrix.setScale(f5, f6 * f3);
            matrix.postRotate(-90.0f);
            float f11 = i6;
            float f12 = fadeTop;
            matrix.postTranslate(f11, f12);
            shader.setLocalMatrix(matrix);
            canvas.drawRect(f11, f12, i6 + i10, i7, paint);
        }
        if (z4) {
            matrix.setScale(f5, f6 * f4);
            matrix.postRotate(90.0f);
            float f13 = i8;
            float f14 = fadeTop;
            matrix.postTranslate(f13, f14);
            shader.setLocalMatrix(matrix);
            canvas.drawRect(i8 - i10, f14, f13, i7, paint);
        }
        canvas.restoreToCount(i);
        onDrawScrollBars(canvas);
        ViewOverlay viewOverlay2 = this.mOverlay;
        if (viewOverlay2 == null || viewOverlay2.isEmpty()) {
            return;
        }
        this.mOverlay.getOverlayView().dispatchDraw(canvas);
    }

    public ViewOverlay getOverlay() {
        if (this.mOverlay == null) {
            this.mOverlay = new ViewOverlay(this.mContext, this);
        }
        return this.mOverlay;
    }

    private static String printFlags(int i) {
        char c = 1;
        String str = "";
        if ((i & 1) == 1) {
            str = "TAKES_FOCUS";
        } else {
            c = 0;
        }
        int i2 = i & 12;
        if (i2 == 4) {
            if (c > 0) {
                str = str + " ";
            }
            return str + "INVISIBLE";
        }
        if (i2 != 8) {
            return str;
        }
        if (c > 0) {
            str = str + " ";
        }
        return str + "GONE";
    }

    private static String printPrivateFlags(int i) {
        int i2 = 1;
        String str = "";
        if ((i & 1) == 1) {
            str = "WANTS_FOCUS";
        } else {
            i2 = 0;
        }
        if ((i & 2) == 2) {
            if (i2 > 0) {
                str = str + " ";
            }
            str = str + "FOCUSED";
            i2++;
        }
        if ((i & 4) == 4) {
            if (i2 > 0) {
                str = str + " ";
            }
            str = str + "SELECTED";
            i2++;
        }
        if ((i & 8) == 8) {
            if (i2 > 0) {
                str = str + " ";
            }
            str = str + "IS_ROOT_NAMESPACE";
            i2++;
        }
        if ((i & 16) == 16) {
            if (i2 > 0) {
                str = str + " ";
            }
            str = str + "HAS_BOUNDS";
            i2++;
        }
        if ((i & 32) != 32) {
            return str;
        }
        if (i2 > 0) {
            str = str + " ";
        }
        return str + "DRAWN";
    }

    public boolean isLayoutRequested() {
        return (this.mPrivateFlags & 4096) == 4096;
    }

    public static boolean isLayoutModeOptical(Object obj) {
        return (obj instanceof ViewGroup) && ((ViewGroup) obj).isLayoutModeOptical();
    }

    private boolean setOpticalFrame(int i, int i2, int i3, int i4) {
        Object obj = this.mParent;
        Insets opticalInsets = obj instanceof View ? ((View) obj).getOpticalInsets() : Insets.NONE;
        Insets opticalInsets2 = getOpticalInsets();
        return setFrame((i + opticalInsets.left) - opticalInsets2.left, (i2 + opticalInsets.top) - opticalInsets2.top, i3 + opticalInsets.left + opticalInsets2.right, i4 + opticalInsets.top + opticalInsets2.bottom);
    }

    public void layout(int i, int i2, int i3, int i4) {
        if ((this.mPrivateFlags3 & 8) != 0) {
            onMeasure(this.mOldWidthMeasureSpec, this.mOldHeightMeasureSpec);
            this.mPrivateFlags3 &= -9;
        }
        int i5 = this.mLeft;
        int i6 = this.mTop;
        int i7 = this.mBottom;
        int i8 = this.mRight;
        boolean opticalFrame = isLayoutModeOptical(this.mParent) ? setOpticalFrame(i, i2, i3, i4) : setFrame(i, i2, i3, i4);
        if (opticalFrame || (this.mPrivateFlags & 8192) == 8192) {
            onLayout(opticalFrame, i, i2, i3, i4);
            this.mPrivateFlags &= -8193;
            ListenerInfo listenerInfo = this.mListenerInfo;
            if (listenerInfo != null && listenerInfo.mOnLayoutChangeListeners != null) {
                ArrayList arrayList = (ArrayList) listenerInfo.mOnLayoutChangeListeners.clone();
                int i9 = 0;
                for (int size = arrayList.size(); i9 < size; size = size) {
                    ((OnLayoutChangeListener) arrayList.get(i9)).onLayoutChange(this, i, i2, i3, i4, i5, i6, i8, i7);
                    i9++;
                }
            }
        }
        this.mPrivateFlags &= -4097;
        this.mPrivateFlags3 |= 4;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean setFrame(int i, int i2, int i3, int i4) {
        TransformationInfo transformationInfo;
        int i5 = this.mLeft;
        if (i5 == i && this.mRight == i3 && this.mTop == i2 && this.mBottom == i4) {
            return false;
        }
        int i6 = this.mPrivateFlags & 32;
        int i7 = this.mRight - i5;
        int i8 = this.mBottom - this.mTop;
        int i9 = i3 - i;
        int i10 = i4 - i2;
        boolean z = (i9 == i7 && i10 == i8) ? false : true;
        invalidate(z);
        this.mLeft = i;
        this.mTop = i2;
        this.mRight = i3;
        this.mBottom = i4;
        DisplayList displayList = this.mDisplayList;
        if (displayList != null) {
            displayList.setLeftTopRightBottom(i, i2, i3, i4);
        }
        int i11 = this.mPrivateFlags | 16;
        this.mPrivateFlags = i11;
        if (z) {
            if ((i11 & 536870912) == 0 && (transformationInfo = this.mTransformationInfo) != null) {
                transformationInfo.mMatrixDirty = true;
            }
            sizeChange(i9, i10, i7, i8);
        }
        if ((this.mViewFlags & 12) == 0) {
            this.mPrivateFlags |= 32;
            invalidate(z);
            invalidateParentCaches();
        }
        this.mPrivateFlags |= i6;
        this.mBackgroundSizeChanged = true;
        notifySubtreeAccessibilityStateChangedIfNeeded();
        return true;
    }

    private void sizeChange(int i, int i2, int i3, int i4) {
        onSizeChanged(i, i2, i3, i4);
        ViewOverlay viewOverlay = this.mOverlay;
        if (viewOverlay != null) {
            viewOverlay.getOverlayView().setRight(i);
            this.mOverlay.getOverlayView().setBottom(i2);
        }
    }

    public Resources getResources() {
        return this.mResources;
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        if (verifyDrawable(drawable)) {
            Rect bounds = drawable.getBounds();
            int i = this.mScrollX;
            int i2 = this.mScrollY;
            invalidate(bounds.left + i, bounds.top + i2, bounds.right + i, bounds.bottom + i2);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        if (!verifyDrawable(drawable) || runnable == null) {
            return;
        }
        long jUptimeMillis = j - SystemClock.uptimeMillis();
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.mChoreographer.postCallbackDelayed(1, runnable, drawable, Choreographer.subtractFrameDelay(jUptimeMillis));
        } else {
            ViewRootImpl.getRunQueue().postDelayed(runnable, jUptimeMillis);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        if (!verifyDrawable(drawable) || runnable == null) {
            return;
        }
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.mChoreographer.removeCallbacks(1, runnable, drawable);
        } else {
            ViewRootImpl.getRunQueue().removeCallbacks(runnable);
        }
    }

    public void unscheduleDrawable(Drawable drawable) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null || drawable == null) {
            return;
        }
        attachInfo.mViewRootImpl.mChoreographer.removeCallbacks(1, null, drawable);
    }

    protected void resolveDrawables() {
        if (isLayoutDirectionResolved() || getRawLayoutDirection() != 2) {
            int layoutDirection = isLayoutDirectionResolved() ? getLayoutDirection() : getRawLayoutDirection();
            Drawable drawable = this.mBackground;
            if (drawable != null) {
                drawable.setLayoutDirection(layoutDirection);
            }
            this.mPrivateFlags2 |= 1073741824;
            onResolveDrawables(layoutDirection);
        }
    }

    protected void resetResolvedDrawables() {
        this.mPrivateFlags2 &= -1073741825;
    }

    private boolean isDrawablesResolved() {
        return (this.mPrivateFlags2 & 1073741824) == 1073741824;
    }

    protected boolean verifyDrawable(Drawable drawable) {
        return drawable == this.mBackground;
    }

    protected void drawableStateChanged() {
        Drawable drawable = this.mBackground;
        if (drawable == null || !drawable.isStateful()) {
            return;
        }
        drawable.setState(getDrawableState());
    }

    public void refreshDrawableState() {
        this.mPrivateFlags |= 1024;
        drawableStateChanged();
        ViewParent viewParent = this.mParent;
        if (viewParent != null) {
            viewParent.childDrawableStateChanged(this);
        }
    }

    public final int[] getDrawableState() {
        int[] iArr = this.mDrawableState;
        if (iArr != null && (this.mPrivateFlags & 1024) == 0) {
            return iArr;
        }
        int[] iArrOnCreateDrawableState = onCreateDrawableState(0);
        this.mDrawableState = iArrOnCreateDrawableState;
        this.mPrivateFlags &= -1025;
        return iArrOnCreateDrawableState;
    }

    protected int[] onCreateDrawableState(int i) {
        int i2 = this.mViewFlags;
        if ((i2 & 4194304) == 4194304) {
            Object obj = this.mParent;
            if (obj instanceof View) {
                return ((View) obj).onCreateDrawableState(i);
            }
        }
        int i3 = this.mPrivateFlags;
        int i4 = (i3 & 16384) != 0 ? 16 : 0;
        if ((i2 & 32) == 0) {
            i4 |= 8;
        }
        if (isFocused()) {
            i4 |= 4;
        }
        if ((i3 & 4) != 0) {
            i4 |= 2;
        }
        if (hasWindowFocus()) {
            i4 |= 1;
        }
        if ((1073741824 & i3) != 0) {
            i4 |= 32;
        }
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null && attachInfo.mHardwareAccelerationRequested && HardwareRenderer.isAvailable()) {
            i4 |= 64;
        }
        if ((268435456 & i3) != 0) {
            i4 |= 128;
        }
        int i5 = this.mPrivateFlags2;
        if ((i5 & 1) != 0) {
            i4 |= 256;
        }
        if ((i5 & 2) != 0) {
            i4 |= 512;
        }
        int[] iArr = VIEW_STATE_SETS[i4];
        if (i == 0) {
            return iArr;
        }
        if (iArr != null) {
            int[] iArr2 = new int[iArr.length + i];
            System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
            return iArr2;
        }
        return new int[i];
    }

    protected static int[] mergeDrawableStates(int[] iArr, int[] iArr2) {
        int length = iArr.length - 1;
        while (length >= 0 && iArr[length] == 0) {
            length--;
        }
        System.arraycopy(iArr2, 0, iArr, length + 1, iArr2.length);
        return iArr;
    }

    public void jumpDrawablesToCurrentState() {
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    @RemotableViewMethod
    public void setBackgroundColor(int i) {
        Drawable drawable = this.mBackground;
        if (drawable instanceof ColorDrawable) {
            ((ColorDrawable) drawable.mutate()).setColor(i);
            computeOpaqueFlags();
            this.mBackgroundResource = 0;
            return;
        }
        setBackground(new ColorDrawable(i));
    }

    @RemotableViewMethod
    public void setBackgroundResource(int i) {
        if (i == 0 || i != this.mBackgroundResource) {
            setBackground(i != 0 ? this.mResources.getDrawable(i) : null);
            this.mBackgroundResource = i;
        }
    }

    public void setBackground(Drawable drawable) {
        setBackgroundDrawable(drawable);
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x00d0  */
    @java.lang.Deprecated
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void setBackgroundDrawable(android.graphics.drawable.Drawable r7) {
        /*
            Method dump skipped, instruction units count: 217
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.View.setBackgroundDrawable(android.graphics.drawable.Drawable):void");
    }

    public Drawable getBackground() {
        return this.mBackground;
    }

    public void setPadding(int i, int i2, int i3, int i4) {
        resetResolvedPadding();
        this.mUserPaddingStart = Integer.MIN_VALUE;
        this.mUserPaddingEnd = Integer.MIN_VALUE;
        this.mUserPaddingLeftInitial = i;
        this.mUserPaddingRightInitial = i3;
        this.mLeftPaddingDefined = true;
        this.mRightPaddingDefined = true;
        internalSetPadding(i, i2, i3, i4);
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0028  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x002a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void internalSetPadding(int r8, int r9, int r10, int r11) {
        /*
            r7 = this;
            r7.mUserPaddingLeft = r8
            r7.mUserPaddingRight = r10
            r7.mUserPaddingBottom = r11
            int r0 = r7.mViewFlags
            r1 = r0 & 768(0x300, float:1.076E-42)
            r2 = 0
            r3 = 1
            if (r1 == 0) goto L41
            r1 = r0 & 512(0x200, float:7.175E-43)
            r4 = 16777216(0x1000000, float:2.3509887E-38)
            if (r1 == 0) goto L33
            r1 = r0 & r4
            if (r1 != 0) goto L1a
            r1 = r2
            goto L1e
        L1a:
            int r1 = r7.getVerticalScrollbarWidth()
        L1e:
            int r5 = r7.mVerticalScrollbarPosition
            if (r5 == 0) goto L2c
            if (r5 == r3) goto L2a
            r6 = 2
            if (r5 == r6) goto L28
            goto L33
        L28:
            int r10 = r10 + r1
            goto L33
        L2a:
            int r8 = r8 + r1
            goto L33
        L2c:
            boolean r5 = r7.isLayoutRtl()
            if (r5 == 0) goto L28
            goto L2a
        L33:
            r1 = r0 & 256(0x100, float:3.59E-43)
            if (r1 == 0) goto L41
            r0 = r0 & r4
            if (r0 != 0) goto L3c
            r0 = r2
            goto L40
        L3c:
            int r0 = r7.getHorizontalScrollbarHeight()
        L40:
            int r11 = r11 + r0
        L41:
            int r0 = r7.mPaddingLeft
            if (r0 == r8) goto L48
            r7.mPaddingLeft = r8
            r2 = r3
        L48:
            int r8 = r7.mPaddingTop
            if (r8 == r9) goto L4f
            r7.mPaddingTop = r9
            r2 = r3
        L4f:
            int r8 = r7.mPaddingRight
            if (r8 == r10) goto L56
            r7.mPaddingRight = r10
            r2 = r3
        L56:
            int r8 = r7.mPaddingBottom
            if (r8 == r11) goto L5d
            r7.mPaddingBottom = r11
            goto L5e
        L5d:
            r3 = r2
        L5e:
            if (r3 == 0) goto L63
            r7.requestLayout()
        L63:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.View.internalSetPadding(int, int, int, int):void");
    }

    public void setPaddingRelative(int i, int i2, int i3, int i4) {
        resetResolvedPadding();
        this.mUserPaddingStart = i;
        this.mUserPaddingEnd = i3;
        this.mLeftPaddingDefined = true;
        this.mRightPaddingDefined = true;
        if (getLayoutDirection() == 1) {
            this.mUserPaddingLeftInitial = i3;
            this.mUserPaddingRightInitial = i;
            internalSetPadding(i3, i2, i, i4);
        } else {
            this.mUserPaddingLeftInitial = i;
            this.mUserPaddingRightInitial = i3;
            internalSetPadding(i, i2, i3, i4);
        }
    }

    public int getPaddingTop() {
        return this.mPaddingTop;
    }

    public int getPaddingBottom() {
        return this.mPaddingBottom;
    }

    public int getPaddingLeft() {
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        return this.mPaddingLeft;
    }

    public int getPaddingStart() {
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        return getLayoutDirection() == 1 ? this.mPaddingRight : this.mPaddingLeft;
    }

    public int getPaddingRight() {
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        return this.mPaddingRight;
    }

    public int getPaddingEnd() {
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        return getLayoutDirection() == 1 ? this.mPaddingLeft : this.mPaddingRight;
    }

    public boolean isPaddingRelative() {
        return (this.mUserPaddingStart == Integer.MIN_VALUE && this.mUserPaddingEnd == Integer.MIN_VALUE) ? false : true;
    }

    Insets computeOpticalInsets() {
        Drawable drawable = this.mBackground;
        return drawable == null ? Insets.NONE : drawable.getOpticalInsets();
    }

    public void resetPaddingToInitialValues() {
        if (isRtlCompatibilityMode()) {
            this.mPaddingLeft = this.mUserPaddingLeftInitial;
            this.mPaddingRight = this.mUserPaddingRightInitial;
            return;
        }
        if (isLayoutRtl()) {
            int i = this.mUserPaddingEnd;
            if (i < 0) {
                i = this.mUserPaddingLeftInitial;
            }
            this.mPaddingLeft = i;
            int i2 = this.mUserPaddingStart;
            if (i2 < 0) {
                i2 = this.mUserPaddingRightInitial;
            }
            this.mPaddingRight = i2;
            return;
        }
        int i3 = this.mUserPaddingStart;
        if (i3 < 0) {
            i3 = this.mUserPaddingLeftInitial;
        }
        this.mPaddingLeft = i3;
        int i4 = this.mUserPaddingEnd;
        if (i4 < 0) {
            i4 = this.mUserPaddingRightInitial;
        }
        this.mPaddingRight = i4;
    }

    public Insets getOpticalInsets() {
        if (this.mLayoutInsets == null) {
            this.mLayoutInsets = computeOpticalInsets();
        }
        return this.mLayoutInsets;
    }

    public void setSelected(boolean z) {
        int i = this.mPrivateFlags;
        if (((i & 4) != 0) != z) {
            this.mPrivateFlags = (i & (-5)) | (z ? 4 : 0);
            if (!z) {
                resetPressedState();
            }
            invalidate(true);
            refreshDrawableState();
            dispatchSetSelected(z);
            notifyViewAccessibilityStateChangedIfNeeded(0);
        }
    }

    @ViewDebug.ExportedProperty
    public boolean isSelected() {
        return (this.mPrivateFlags & 4) != 0;
    }

    public void setActivated(boolean z) {
        int i = this.mPrivateFlags;
        if (((i & 1073741824) != 0) != z) {
            this.mPrivateFlags = (i & (-1073741825)) | (z ? 1073741824 : 0);
            invalidate(true);
            refreshDrawableState();
            dispatchSetActivated(z);
        }
    }

    @ViewDebug.ExportedProperty
    public boolean isActivated() {
        return (this.mPrivateFlags & 1073741824) != 0;
    }

    public ViewTreeObserver getViewTreeObserver() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mTreeObserver;
        }
        if (this.mFloatingTreeObserver == null) {
            this.mFloatingTreeObserver = new ViewTreeObserver();
        }
        return this.mFloatingTreeObserver;
    }

    public View getRootView() {
        View view;
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null && (view = attachInfo.mRootView) != null) {
            return view;
        }
        View view2 = this;
        while (true) {
            Object obj = view2.mParent;
            if (obj == null || !(obj instanceof View)) {
                break;
            }
            view2 = (View) obj;
        }
        return view2;
    }

    public boolean toGlobalMotionEvent(MotionEvent motionEvent) {
        if (this.mAttachInfo == null) {
            return false;
        }
        transformMotionEventToGlobal(motionEvent);
        motionEvent.offsetLocation(r0.mWindowLeft, r0.mWindowTop);
        return true;
    }

    public boolean toLocalMotionEvent(MotionEvent motionEvent) {
        if (this.mAttachInfo == null) {
            return false;
        }
        motionEvent.offsetLocation(-r0.mWindowLeft, -r0.mWindowTop);
        transformMotionEventToLocal(motionEvent);
        return true;
    }

    private void transformMotionEventToLocal(MotionEvent motionEvent) {
        Object obj = this.mParent;
        if (obj instanceof View) {
            ((View) obj).transformMotionEventToLocal(motionEvent);
            motionEvent.offsetLocation(r0.mScrollX, r0.mScrollY);
        } else if (obj instanceof ViewRootImpl) {
            motionEvent.offsetLocation(0.0f, ((ViewRootImpl) obj).mCurScrollY);
        }
        motionEvent.offsetLocation(-this.mLeft, -this.mTop);
        if (hasIdentityMatrix()) {
            return;
        }
        motionEvent.transform(getInverseMatrix());
    }

    private void transformMotionEventToGlobal(MotionEvent motionEvent) {
        if (!hasIdentityMatrix()) {
            motionEvent.transform(getMatrix());
        }
        motionEvent.offsetLocation(this.mLeft, this.mTop);
        Object obj = this.mParent;
        if (obj instanceof View) {
            motionEvent.offsetLocation(-r0.mScrollX, -r0.mScrollY);
            ((View) obj).transformMotionEventToGlobal(motionEvent);
        } else if (obj instanceof ViewRootImpl) {
            motionEvent.offsetLocation(0.0f, -((ViewRootImpl) obj).mCurScrollY);
        }
    }

    public void getLocationOnScreen(int[] iArr) {
        getLocationInWindow(iArr);
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            iArr[0] = iArr[0] + attachInfo.mWindowLeft;
            iArr[1] = iArr[1] + attachInfo.mWindowTop;
        }
    }

    public void getLocationInWindow(int[] iArr) {
        if (iArr == null || iArr.length < 2) {
            throw new IllegalArgumentException("location must be an array of two integers");
        }
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            iArr[1] = 0;
            iArr[0] = 0;
            return;
        }
        float[] fArr = attachInfo.mTmpTransformLocation;
        fArr[1] = 0.0f;
        fArr[0] = 0.0f;
        if (!hasIdentityMatrix()) {
            getMatrix().mapPoints(fArr);
        }
        fArr[0] = fArr[0] + this.mLeft;
        fArr[1] = fArr[1] + this.mTop;
        Object obj = this.mParent;
        while (obj instanceof View) {
            View view = (View) obj;
            fArr[0] = fArr[0] - view.mScrollX;
            fArr[1] = fArr[1] - view.mScrollY;
            if (!view.hasIdentityMatrix()) {
                view.getMatrix().mapPoints(fArr);
            }
            fArr[0] = fArr[0] + view.mLeft;
            fArr[1] = fArr[1] + view.mTop;
            obj = view.mParent;
        }
        if (obj instanceof ViewRootImpl) {
            fArr[1] = fArr[1] - ((ViewRootImpl) obj).mCurScrollY;
        }
        iArr[0] = (int) (fArr[0] + 0.5f);
        iArr[1] = (int) (fArr[1] + 0.5f);
    }

    protected View findViewTraversal(int i) {
        if (i == this.mID) {
            return this;
        }
        return null;
    }

    protected View findViewWithTagTraversal(Object obj) {
        if (obj == null || !obj.equals(this.mTag)) {
            return null;
        }
        return this;
    }

    protected View findViewByPredicateTraversal(Predicate<View> predicate, View view) {
        if (predicate.apply(this)) {
            return this;
        }
        return null;
    }

    public final View findViewById(int i) {
        if (i < 0) {
            return null;
        }
        return findViewTraversal(i);
    }

    final View findViewByAccessibilityId(int i) {
        if (i < 0) {
            return null;
        }
        return findViewByAccessibilityIdTraversal(i);
    }

    public View findViewByAccessibilityIdTraversal(int i) {
        if (getAccessibilityViewId() == i) {
            return this;
        }
        return null;
    }

    public final View findViewWithTag(Object obj) {
        if (obj == null) {
            return null;
        }
        return findViewWithTagTraversal(obj);
    }

    public final View findViewByPredicate(Predicate<View> predicate) {
        return findViewByPredicateTraversal(predicate, null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x001d, code lost:
    
        return r1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final android.view.View findViewByPredicateInsideOut(android.view.View r5, com.android.internal.util.Predicate<android.view.View> r6) {
        /*
            r4 = this;
            r0 = 0
            r1 = r0
        L2:
            android.view.View r1 = r5.findViewByPredicateTraversal(r6, r1)
            if (r1 != 0) goto L1d
            if (r5 != r4) goto Lb
            goto L1d
        Lb:
            android.view.ViewParent r1 = r5.getParent()
            if (r1 == 0) goto L1c
            boolean r2 = r1 instanceof android.view.View
            if (r2 != 0) goto L16
            goto L1c
        L16:
            android.view.View r1 = (android.view.View) r1
            r3 = r1
            r1 = r5
            r5 = r3
            goto L2
        L1c:
            return r0
        L1d:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.View.findViewByPredicateInsideOut(android.view.View, com.android.internal.util.Predicate):android.view.View");
    }

    public void setId(int i) {
        this.mID = i;
        if (i != -1 || this.mLabelForId == -1) {
            return;
        }
        this.mID = generateViewId();
    }

    public void setIsRootNamespace(boolean z) {
        if (z) {
            this.mPrivateFlags |= 8;
        } else {
            this.mPrivateFlags &= -9;
        }
    }

    public boolean isRootNamespace() {
        return (this.mPrivateFlags & 8) != 0;
    }

    @ViewDebug.CapturedViewProperty
    public int getId() {
        return this.mID;
    }

    @ViewDebug.ExportedProperty
    public Object getTag() {
        return this.mTag;
    }

    public void setTag(Object obj) {
        this.mTag = obj;
    }

    public Object getTag(int i) {
        SparseArray<Object> sparseArray = this.mKeyedTags;
        if (sparseArray != null) {
            return sparseArray.get(i);
        }
        return null;
    }

    public void setTag(int i, Object obj) {
        if ((i >>> 24) < 2) {
            throw new IllegalArgumentException("The key must be an application-specific resource id.");
        }
        setKeyedTag(i, obj);
    }

    public void setTagInternal(int i, Object obj) {
        if ((i >>> 24) != 1) {
            throw new IllegalArgumentException("The key must be a framework-specific resource id.");
        }
        setKeyedTag(i, obj);
    }

    private void setKeyedTag(int i, Object obj) {
        if (this.mKeyedTags == null) {
            this.mKeyedTags = new SparseArray<>(2);
        }
        this.mKeyedTags.put(i, obj);
    }

    public void debug() {
        debug(0);
    }

    protected void debug(int i) {
        String strDebug;
        String str = debugIndent(i - 1) + "+ " + this;
        int id = getId();
        if (id != -1) {
            str = str + " (id=" + id + ")";
        }
        Object tag = getTag();
        if (tag != null) {
            str = str + " (tag=" + tag + ")";
        }
        Log.d(VIEW_LOG_TAG, str);
        if ((this.mPrivateFlags & 2) != 0) {
            Log.d(VIEW_LOG_TAG, debugIndent(i) + " FOCUSED");
        }
        Log.d(VIEW_LOG_TAG, debugIndent(i) + "frame={" + this.mLeft + ", " + this.mTop + ", " + this.mRight + ", " + this.mBottom + "} scroll={" + this.mScrollX + ", " + this.mScrollY + "} ");
        if (this.mPaddingLeft != 0 || this.mPaddingTop != 0 || this.mPaddingRight != 0 || this.mPaddingBottom != 0) {
            Log.d(VIEW_LOG_TAG, debugIndent(i) + "padding={" + this.mPaddingLeft + ", " + this.mPaddingTop + ", " + this.mPaddingRight + ", " + this.mPaddingBottom + "}");
        }
        Log.d(VIEW_LOG_TAG, debugIndent(i) + "mMeasureWidth=" + this.mMeasuredWidth + " mMeasureHeight=" + this.mMeasuredHeight);
        String strDebugIndent = debugIndent(i);
        ViewGroup.LayoutParams layoutParams = this.mLayoutParams;
        if (layoutParams == null) {
            strDebug = strDebugIndent + "BAD! no layout params";
        } else {
            strDebug = layoutParams.debug(strDebugIndent);
        }
        Log.d(VIEW_LOG_TAG, strDebug);
        Log.d(VIEW_LOG_TAG, ((debugIndent(i) + "flags={") + printFlags(this.mViewFlags)) + "}");
        Log.d(VIEW_LOG_TAG, ((debugIndent(i) + "privateFlags={") + printPrivateFlags(this.mPrivateFlags)) + "}");
    }

    protected static String debugIndent(int i) {
        int i2 = (i * 2) + 3;
        StringBuilder sb = new StringBuilder(i2 * 2);
        for (int i3 = 0; i3 < i2; i3++) {
            sb.append(' ').append(' ');
        }
        return sb.toString();
    }

    public boolean isInLayout() {
        ViewRootImpl viewRootImpl = getViewRootImpl();
        return viewRootImpl != null && viewRootImpl.isInLayout();
    }

    public void requestLayout() {
        LongSparseLongArray longSparseLongArray = this.mMeasureCache;
        if (longSparseLongArray != null) {
            longSparseLongArray.clear();
        }
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null && attachInfo.mViewRequestingLayout == null) {
            ViewRootImpl viewRootImpl = getViewRootImpl();
            if (viewRootImpl != null && viewRootImpl.isInLayout() && !viewRootImpl.requestLayoutDuringLayout(this)) {
                return;
            } else {
                this.mAttachInfo.mViewRequestingLayout = this;
            }
        }
        int i = this.mPrivateFlags | 4096;
        this.mPrivateFlags = i;
        this.mPrivateFlags = i | Integer.MIN_VALUE;
        ViewParent viewParent = this.mParent;
        if (viewParent != null && !viewParent.isLayoutRequested()) {
            this.mParent.requestLayout();
        }
        AttachInfo attachInfo2 = this.mAttachInfo;
        if (attachInfo2 == null || attachInfo2.mViewRequestingLayout != this) {
            return;
        }
        this.mAttachInfo.mViewRequestingLayout = null;
    }

    public void forceLayout() {
        LongSparseLongArray longSparseLongArray = this.mMeasureCache;
        if (longSparseLongArray != null) {
            longSparseLongArray.clear();
        }
        int i = this.mPrivateFlags | 4096;
        this.mPrivateFlags = i;
        this.mPrivateFlags = i | Integer.MIN_VALUE;
    }

    public final void measure(int i, int i2) {
        boolean zIsLayoutModeOptical = isLayoutModeOptical(this);
        if (zIsLayoutModeOptical != isLayoutModeOptical(this.mParent)) {
            Insets opticalInsets = getOpticalInsets();
            int i3 = opticalInsets.left + opticalInsets.right;
            int i4 = opticalInsets.top + opticalInsets.bottom;
            if (zIsLayoutModeOptical) {
                i3 = -i3;
            }
            i = MeasureSpec.adjust(i, i3);
            if (zIsLayoutModeOptical) {
                i4 = -i4;
            }
            i2 = MeasureSpec.adjust(i2, i4);
        }
        long j = (((long) i) << 32) | (((long) i2) & ExpandableListView.PACKED_POSITION_VALUE_NULL);
        if (this.mMeasureCache == null) {
            this.mMeasureCache = new LongSparseLongArray(2);
        }
        int i5 = this.mPrivateFlags;
        if ((i5 & 4096) == 4096 || i != this.mOldWidthMeasureSpec || i2 != this.mOldHeightMeasureSpec) {
            this.mPrivateFlags = i5 & (-2049);
            resolveRtlPropertiesIfNeeded();
            int iIndexOfKey = (this.mPrivateFlags & 4096) == 4096 ? -1 : this.mMeasureCache.indexOfKey(j);
            if (iIndexOfKey < 0 || sIgnoreMeasureCache) {
                onMeasure(i, i2);
                this.mPrivateFlags3 &= -9;
            } else {
                long jValueAt = this.mMeasureCache.valueAt(iIndexOfKey);
                setMeasuredDimension((int) (jValueAt >> 32), (int) jValueAt);
                this.mPrivateFlags3 |= 8;
            }
            int i6 = this.mPrivateFlags;
            if ((i6 & 2048) != 2048) {
                throw new IllegalStateException("onMeasure() did not set the measured dimension by calling setMeasuredDimension()");
            }
            this.mPrivateFlags = i6 | 8192;
        }
        this.mOldWidthMeasureSpec = i;
        this.mOldHeightMeasureSpec = i2;
        this.mMeasureCache.put(j, (((long) this.mMeasuredWidth) << 32) | (((long) this.mMeasuredHeight) & ExpandableListView.PACKED_POSITION_VALUE_NULL));
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), i), getDefaultSize(getSuggestedMinimumHeight(), i2));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setMeasuredDimension(int i, int i2) {
        boolean zIsLayoutModeOptical = isLayoutModeOptical(this);
        if (zIsLayoutModeOptical != isLayoutModeOptical(this.mParent)) {
            Insets opticalInsets = getOpticalInsets();
            int i3 = opticalInsets.left + opticalInsets.right;
            int i4 = opticalInsets.top + opticalInsets.bottom;
            if (!zIsLayoutModeOptical) {
                i3 = -i3;
            }
            i += i3;
            if (!zIsLayoutModeOptical) {
                i4 = -i4;
            }
            i2 += i4;
        }
        this.mMeasuredWidth = i;
        this.mMeasuredHeight = i2;
        this.mPrivateFlags |= 2048;
    }

    public static int resolveSize(int i, int i2) {
        return resolveSizeAndState(i, i2, 0) & 16777215;
    }

    public static int resolveSizeAndState(int i, int i2, int i3) {
        int mode = MeasureSpec.getMode(i2);
        int size = MeasureSpec.getSize(i2);
        if (mode != Integer.MIN_VALUE) {
            if (mode == 1073741824) {
                i = size;
            }
        } else if (size < i) {
            i = 16777216 | size;
        }
        return i | ((-16777216) & i3);
    }

    public static int getDefaultSize(int i, int i2) {
        int mode = MeasureSpec.getMode(i2);
        return (mode == Integer.MIN_VALUE || mode == 1073741824) ? MeasureSpec.getSize(i2) : i;
    }

    protected int getSuggestedMinimumHeight() {
        Drawable drawable = this.mBackground;
        return drawable == null ? this.mMinHeight : Math.max(this.mMinHeight, drawable.getMinimumHeight());
    }

    protected int getSuggestedMinimumWidth() {
        Drawable drawable = this.mBackground;
        return drawable == null ? this.mMinWidth : Math.max(this.mMinWidth, drawable.getMinimumWidth());
    }

    public int getMinimumHeight() {
        return this.mMinHeight;
    }

    public void setMinimumHeight(int i) {
        this.mMinHeight = i;
        requestLayout();
    }

    public int getMinimumWidth() {
        return this.mMinWidth;
    }

    public void setMinimumWidth(int i) {
        this.mMinWidth = i;
        requestLayout();
    }

    public Animation getAnimation() {
        return this.mCurrentAnimation;
    }

    public void startAnimation(Animation animation) {
        animation.setStartTime(-1L);
        setAnimation(animation);
        invalidateParentCaches();
        invalidate(true);
    }

    public void clearAnimation() {
        Animation animation = this.mCurrentAnimation;
        if (animation != null) {
            animation.detach();
        }
        this.mCurrentAnimation = null;
        invalidateParentIfNeeded();
    }

    public void setAnimation(Animation animation) {
        this.mCurrentAnimation = animation;
        if (animation != null) {
            AttachInfo attachInfo = this.mAttachInfo;
            if (attachInfo != null && !attachInfo.mScreenOn && animation.getStartTime() == -1) {
                animation.setStartTime(AnimationUtils.currentAnimationTimeMillis());
            }
            animation.reset();
        }
    }

    protected void onAnimationStart() {
        this.mPrivateFlags |= 65536;
    }

    protected void onAnimationEnd() {
        this.mPrivateFlags &= -65537;
    }

    public boolean gatherTransparentRegion(Region region) {
        Drawable drawable;
        AttachInfo attachInfo = this.mAttachInfo;
        if (region != null && attachInfo != null) {
            int i = this.mPrivateFlags;
            if ((i & 128) == 0) {
                int[] iArr = attachInfo.mTransparentLocation;
                getLocationInWindow(iArr);
                region.op(iArr[0], iArr[1], (iArr[0] + this.mRight) - this.mLeft, (iArr[1] + this.mBottom) - this.mTop, Region.Op.DIFFERENCE);
            } else if ((i & 256) != 0 && (drawable = this.mBackground) != null) {
                applyDrawableToTransparentRegion(drawable, region);
            }
        }
        return true;
    }

    public void playSoundEffect(int i) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null || attachInfo.mRootCallbacks == null || !isSoundEffectsEnabled()) {
            return;
        }
        this.mAttachInfo.mRootCallbacks.playSoundEffect(i);
    }

    public boolean performHapticFeedback(int i) {
        return performHapticFeedback(i, 0);
    }

    public boolean performHapticFeedback(int i, int i2) {
        if (this.mAttachInfo == null) {
            return false;
        }
        if ((i2 & 1) != 0 || isHapticFeedbackEnabled()) {
            return this.mAttachInfo.mRootCallbacks.performHapticFeedback(i, (i2 & 2) != 0);
        }
        return false;
    }

    public void setSystemUiVisibility(int i) {
        AttachInfo attachInfo;
        if (i != this.mSystemUiVisibility) {
            this.mSystemUiVisibility = i;
            if (this.mParent == null || (attachInfo = this.mAttachInfo) == null || attachInfo.mRecomputeGlobalAttributes) {
                return;
            }
            this.mParent.recomputeViewAttributes(this);
        }
    }

    public int getSystemUiVisibility() {
        return this.mSystemUiVisibility;
    }

    public int getWindowSystemUiVisibility() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mSystemUiVisibility;
        }
        return 0;
    }

    public void dispatchWindowSystemUiVisiblityChanged(int i) {
        onWindowSystemUiVisibilityChanged(i);
    }

    public void setOnSystemUiVisibilityChangeListener(OnSystemUiVisibilityChangeListener onSystemUiVisibilityChangeListener) {
        AttachInfo attachInfo;
        getListenerInfo().mOnSystemUiVisibilityChangeListener = onSystemUiVisibilityChangeListener;
        if (this.mParent == null || (attachInfo = this.mAttachInfo) == null || attachInfo.mRecomputeGlobalAttributes) {
            return;
        }
        this.mParent.recomputeViewAttributes(this);
    }

    public void dispatchSystemUiVisibilityChanged(int i) {
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo == null || listenerInfo.mOnSystemUiVisibilityChangeListener == null) {
            return;
        }
        listenerInfo.mOnSystemUiVisibilityChangeListener.onSystemUiVisibilityChange(i & 65535);
    }

    boolean updateLocalSystemUiVisibility(int i, int i2) {
        int i3 = this.mSystemUiVisibility;
        int i4 = (i & i2) | ((~i2) & i3);
        if (i4 == i3) {
            return false;
        }
        setSystemUiVisibility(i4);
        return true;
    }

    public void setDisabledSystemUiVisibility(int i) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null || attachInfo.mDisabledSystemUiVisibility == i) {
            return;
        }
        this.mAttachInfo.mDisabledSystemUiVisibility = i;
        ViewParent viewParent = this.mParent;
        if (viewParent != null) {
            viewParent.recomputeViewAttributes(this);
        }
    }

    public static class DragShadowBuilder {
        private final WeakReference<View> mView;

        public DragShadowBuilder(View view) {
            this.mView = new WeakReference<>(view);
        }

        public DragShadowBuilder() {
            this.mView = new WeakReference<>(null);
        }

        public final View getView() {
            return this.mView.get();
        }

        public void onProvideShadowMetrics(Point point, Point point2) {
            View view = this.mView.get();
            if (view != null) {
                point.set(view.getWidth(), view.getHeight());
                point2.set(point.x / 2, point.y / 2);
            } else {
                Log.e(View.VIEW_LOG_TAG, "Asked for drag thumb metrics but no view");
            }
        }

        public void onDrawShadow(Canvas canvas) {
            View view = this.mView.get();
            if (view != null) {
                view.draw(canvas);
            } else {
                Log.e(View.VIEW_LOG_TAG, "Asked to draw drag shadow but no view");
            }
        }
    }

    public final boolean startDrag(ClipData clipData, DragShadowBuilder dragShadowBuilder, Object obj, int i) {
        Point point = new Point();
        Point point2 = new Point();
        dragShadowBuilder.onProvideShadowMetrics(point, point2);
        if (point.x < 0 || point.y < 0 || point2.x < 0 || point2.y < 0) {
            throw new IllegalStateException("Drag shadow dimensions must not be negative");
        }
        Surface surface = new Surface();
        try {
            IBinder iBinderPrepareDrag = this.mAttachInfo.mSession.prepareDrag(this.mAttachInfo.mWindow, i, point.x, point.y, surface);
            if (iBinderPrepareDrag == null) {
                return false;
            }
            Canvas canvasLockCanvas = surface.lockCanvas(null);
            try {
                canvasLockCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                dragShadowBuilder.onDrawShadow(canvasLockCanvas);
                surface.unlockCanvasAndPost(canvasLockCanvas);
                ViewRootImpl viewRootImpl = getViewRootImpl();
                viewRootImpl.setLocalDragState(obj);
                viewRootImpl.getLastTouchPoint(point);
                boolean zPerformDrag = this.mAttachInfo.mSession.performDrag(this.mAttachInfo.mWindow, iBinderPrepareDrag, point.x, point.y, point2.x, point2.y, clipData);
                surface.release();
                return zPerformDrag;
            } catch (Throwable th) {
                surface.unlockCanvasAndPost(canvasLockCanvas);
                throw th;
            }
        } catch (Exception e) {
            Log.e(VIEW_LOG_TAG, "Unable to initiate drag", e);
            surface.destroy();
            return false;
        }
    }

    public boolean dispatchDragEvent(DragEvent dragEvent) {
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo == null || listenerInfo.mOnDragListener == null || (this.mViewFlags & 32) != 0 || !listenerInfo.mOnDragListener.onDrag(this, dragEvent)) {
            return onDragEvent(dragEvent);
        }
        return true;
    }

    boolean canAcceptDrag() {
        return (this.mPrivateFlags2 & 1) != 0;
    }

    public void applyDrawableToTransparentRegion(Drawable drawable, Region region) {
        Region transparentRegion = drawable.getTransparentRegion();
        Rect bounds = drawable.getBounds();
        AttachInfo attachInfo = this.mAttachInfo;
        if (transparentRegion != null && attachInfo != null) {
            int right = getRight() - getLeft();
            int bottom = getBottom() - getTop();
            if (bounds.left > 0) {
                transparentRegion.op(0, 0, bounds.left, bottom, Region.Op.UNION);
            }
            if (bounds.right < right) {
                transparentRegion.op(bounds.right, 0, right, bottom, Region.Op.UNION);
            }
            if (bounds.top > 0) {
                transparentRegion.op(0, 0, right, bounds.top, Region.Op.UNION);
            }
            if (bounds.bottom < bottom) {
                transparentRegion.op(0, bounds.bottom, right, bottom, Region.Op.UNION);
            }
            int[] iArr = attachInfo.mTransparentLocation;
            getLocationInWindow(iArr);
            transparentRegion.translate(iArr[0], iArr[1]);
            region.op(transparentRegion, Region.Op.INTERSECT);
            return;
        }
        region.op(bounds, Region.Op.DIFFERENCE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkForLongClick(int i) {
        if ((this.mViewFlags & 2097152) == 2097152) {
            this.mHasPerformedLongPress = false;
            if (this.mPendingCheckForLongPress == null) {
                this.mPendingCheckForLongPress = new CheckForLongPress();
            }
            this.mPendingCheckForLongPress.rememberWindowAttachCount();
            postDelayed(this.mPendingCheckForLongPress, ViewConfiguration.getLongPressTimeout() - i);
        }
    }

    public static View inflate(Context context, int i, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(i, viewGroup);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:36:0x004a  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x004d  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x0057 A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean overScrollBy(int r6, int r7, int r8, int r9, int r10, int r11, int r12, int r13, boolean r14) {
        /*
            r5 = this;
            int r14 = r5.mOverScrollMode
            int r0 = r5.computeHorizontalScrollRange()
            int r1 = r5.computeHorizontalScrollExtent()
            r2 = 0
            r3 = 1
            if (r0 <= r1) goto L10
            r0 = r3
            goto L11
        L10:
            r0 = r2
        L11:
            int r1 = r5.computeVerticalScrollRange()
            int r4 = r5.computeVerticalScrollExtent()
            if (r1 <= r4) goto L1d
            r1 = r3
            goto L1e
        L1d:
            r1 = r2
        L1e:
            if (r14 == 0) goto L27
            if (r14 != r3) goto L25
            if (r0 == 0) goto L25
            goto L27
        L25:
            r0 = r2
            goto L28
        L27:
            r0 = r3
        L28:
            if (r14 == 0) goto L31
            if (r14 != r3) goto L2f
            if (r1 == 0) goto L2f
            goto L31
        L2f:
            r14 = r2
            goto L32
        L31:
            r14 = r3
        L32:
            int r8 = r8 + r6
            if (r0 != 0) goto L36
            r12 = r2
        L36:
            int r9 = r9 + r7
            if (r14 != 0) goto L3a
            r13 = r2
        L3a:
            int r6 = -r12
            int r12 = r12 + r10
            int r7 = -r13
            int r13 = r13 + r11
            if (r8 <= r12) goto L43
            r8 = r12
        L41:
            r6 = r3
            goto L48
        L43:
            if (r8 >= r6) goto L47
            r8 = r6
            goto L41
        L47:
            r6 = r2
        L48:
            if (r9 <= r13) goto L4d
            r9 = r13
        L4b:
            r7 = r3
            goto L52
        L4d:
            if (r9 >= r7) goto L51
            r9 = r7
            goto L4b
        L51:
            r7 = r2
        L52:
            r5.onOverScrolled(r8, r9, r6, r7)
            if (r6 != 0) goto L59
            if (r7 == 0) goto L5a
        L59:
            r2 = r3
        L5a:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.View.overScrollBy(int, int, int, int, int, int, int, int, boolean):boolean");
    }

    public int getOverScrollMode() {
        return this.mOverScrollMode;
    }

    public void setOverScrollMode(int i) {
        if (i != 0 && i != 1 && i != 2) {
            throw new IllegalArgumentException("Invalid overscroll mode " + i);
        }
        this.mOverScrollMode = i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getVerticalScrollFactor() {
        if (this.mVerticalScrollFactor == 0.0f) {
            TypedValue typedValue = new TypedValue();
            if (!this.mContext.getTheme().resolveAttribute(R.attr.listPreferredItemHeight, typedValue, true)) {
                throw new IllegalStateException("Expected theme to define listPreferredItemHeight.");
            }
            this.mVerticalScrollFactor = typedValue.getDimension(this.mContext.getResources().getDisplayMetrics());
        }
        return this.mVerticalScrollFactor;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getHorizontalScrollFactor() {
        return getVerticalScrollFactor();
    }

    @ViewDebug.ExportedProperty(category = "text", mapping = {@ViewDebug.IntToString(from = 0, to = "INHERIT"), @ViewDebug.IntToString(from = 1, to = "FIRST_STRONG"), @ViewDebug.IntToString(from = 2, to = "ANY_RTL"), @ViewDebug.IntToString(from = 3, to = "LTR"), @ViewDebug.IntToString(from = 4, to = "RTL"), @ViewDebug.IntToString(from = 5, to = "LOCALE")})
    public int getRawTextDirection() {
        return (this.mPrivateFlags2 & 448) >> 6;
    }

    public void setTextDirection(int i) {
        if (getRawTextDirection() != i) {
            this.mPrivateFlags2 &= -449;
            resetResolvedTextDirection();
            this.mPrivateFlags2 = ((i << 6) & 448) | this.mPrivateFlags2;
            resolveTextDirection();
            onRtlPropertiesChanged(getLayoutDirection());
            requestLayout();
            invalidate(true);
        }
    }

    @ViewDebug.ExportedProperty(category = "text", mapping = {@ViewDebug.IntToString(from = 0, to = "INHERIT"), @ViewDebug.IntToString(from = 1, to = "FIRST_STRONG"), @ViewDebug.IntToString(from = 2, to = "ANY_RTL"), @ViewDebug.IntToString(from = 3, to = "LTR"), @ViewDebug.IntToString(from = 4, to = "RTL"), @ViewDebug.IntToString(from = 5, to = "LOCALE")})
    public int getTextDirection() {
        return (this.mPrivateFlags2 & PFLAG2_TEXT_DIRECTION_RESOLVED_MASK) >> 10;
    }

    public boolean resolveTextDirection() {
        int textDirection;
        this.mPrivateFlags2 &= -7681;
        if (hasRtlSupport()) {
            int rawTextDirection = getRawTextDirection();
            if (rawTextDirection != 0) {
                if (rawTextDirection == 1 || rawTextDirection == 2 || rawTextDirection == 3 || rawTextDirection == 4 || rawTextDirection == 5) {
                    this.mPrivateFlags2 |= rawTextDirection << 10;
                } else {
                    this.mPrivateFlags2 |= 1024;
                }
            } else {
                if (!canResolveTextDirection()) {
                    this.mPrivateFlags2 |= 1024;
                    return false;
                }
                try {
                    if (!this.mParent.isTextDirectionResolved()) {
                        this.mPrivateFlags2 |= 1024;
                        return false;
                    }
                    try {
                        textDirection = this.mParent.getTextDirection();
                    } catch (AbstractMethodError e) {
                        Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                        textDirection = 3;
                    }
                    if (textDirection == 1 || textDirection == 2 || textDirection == 3 || textDirection == 4 || textDirection == 5) {
                        this.mPrivateFlags2 = (textDirection << 10) | this.mPrivateFlags2;
                    } else {
                        this.mPrivateFlags2 |= 1024;
                    }
                } catch (AbstractMethodError e2) {
                    Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e2);
                    this.mPrivateFlags2 |= 1536;
                    return true;
                }
            }
        } else {
            this.mPrivateFlags2 |= 1024;
        }
        this.mPrivateFlags2 |= 512;
        return true;
    }

    public boolean canResolveTextDirection() {
        if (getRawTextDirection() != 0) {
            return true;
        }
        ViewParent viewParent = this.mParent;
        if (viewParent == null) {
            return false;
        }
        try {
            return viewParent.canResolveTextDirection();
        } catch (AbstractMethodError e) {
            Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
            return false;
        }
    }

    public void resetResolvedTextDirection() {
        int i = this.mPrivateFlags2 & (-7681);
        this.mPrivateFlags2 = i;
        this.mPrivateFlags2 = i | 1024;
    }

    public boolean isTextDirectionInherited() {
        return getRawTextDirection() == 0;
    }

    public boolean isTextDirectionResolved() {
        return (this.mPrivateFlags2 & 512) == 512;
    }

    @ViewDebug.ExportedProperty(category = "text", mapping = {@ViewDebug.IntToString(from = 0, to = "INHERIT"), @ViewDebug.IntToString(from = 1, to = "GRAVITY"), @ViewDebug.IntToString(from = 2, to = "TEXT_START"), @ViewDebug.IntToString(from = 3, to = "TEXT_END"), @ViewDebug.IntToString(from = 4, to = "CENTER"), @ViewDebug.IntToString(from = 5, to = "VIEW_START"), @ViewDebug.IntToString(from = 6, to = "VIEW_END")})
    public int getRawTextAlignment() {
        return (this.mPrivateFlags2 & PFLAG2_TEXT_ALIGNMENT_MASK) >> 13;
    }

    public void setTextAlignment(int i) {
        if (i != getRawTextAlignment()) {
            this.mPrivateFlags2 &= -57345;
            resetResolvedTextAlignment();
            this.mPrivateFlags2 = ((i << 13) & PFLAG2_TEXT_ALIGNMENT_MASK) | this.mPrivateFlags2;
            resolveTextAlignment();
            onRtlPropertiesChanged(getLayoutDirection());
            requestLayout();
            invalidate(true);
        }
    }

    @ViewDebug.ExportedProperty(category = "text", mapping = {@ViewDebug.IntToString(from = 0, to = "INHERIT"), @ViewDebug.IntToString(from = 1, to = "GRAVITY"), @ViewDebug.IntToString(from = 2, to = "TEXT_START"), @ViewDebug.IntToString(from = 3, to = "TEXT_END"), @ViewDebug.IntToString(from = 4, to = "CENTER"), @ViewDebug.IntToString(from = 5, to = "VIEW_START"), @ViewDebug.IntToString(from = 6, to = "VIEW_END")})
    public int getTextAlignment() {
        return (this.mPrivateFlags2 & PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK) >> 17;
    }

    public boolean resolveTextAlignment() {
        int textAlignment;
        this.mPrivateFlags2 &= -983041;
        if (hasRtlSupport()) {
            int rawTextAlignment = getRawTextAlignment();
            switch (rawTextAlignment) {
                case 0:
                    if (!canResolveTextAlignment()) {
                        this.mPrivateFlags2 |= 131072;
                        return false;
                    }
                    try {
                        if (!this.mParent.isTextAlignmentResolved()) {
                            this.mPrivateFlags2 |= 131072;
                            return false;
                        }
                        try {
                            textAlignment = this.mParent.getTextAlignment();
                        } catch (AbstractMethodError e) {
                            Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                            textAlignment = 1;
                        }
                        switch (textAlignment) {
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.mPrivateFlags2 = (textAlignment << 17) | this.mPrivateFlags2;
                                break;
                            default:
                                this.mPrivateFlags2 |= 131072;
                                break;
                        }
                    } catch (AbstractMethodError e2) {
                        Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e2);
                        this.mPrivateFlags2 |= Menu.CATEGORY_SECONDARY;
                        return true;
                    }
                    break;
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    this.mPrivateFlags2 |= rawTextAlignment << 17;
                    break;
                default:
                    this.mPrivateFlags2 |= 131072;
                    break;
            }
        } else {
            this.mPrivateFlags2 |= 131072;
        }
        this.mPrivateFlags2 |= 65536;
        return true;
    }

    public boolean canResolveTextAlignment() {
        if (getRawTextAlignment() != 0) {
            return true;
        }
        ViewParent viewParent = this.mParent;
        if (viewParent == null) {
            return false;
        }
        try {
            return viewParent.canResolveTextAlignment();
        } catch (AbstractMethodError e) {
            Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
            return false;
        }
    }

    public void resetResolvedTextAlignment() {
        int i = this.mPrivateFlags2 & (-983041);
        this.mPrivateFlags2 = i;
        this.mPrivateFlags2 = i | 131072;
    }

    public boolean isTextAlignmentInherited() {
        return getRawTextAlignment() == 0;
    }

    public boolean isTextAlignmentResolved() {
        return (this.mPrivateFlags2 & 65536) == 65536;
    }

    public static int generateViewId() {
        AtomicInteger atomicInteger;
        int i;
        int i2;
        do {
            atomicInteger = sNextGeneratedId;
            i = atomicInteger.get();
            i2 = i + 1;
            if (i2 > 16777215) {
                i2 = 1;
            }
        } while (!atomicInteger.compareAndSet(i, i2));
        return i;
    }

    public static class MeasureSpec {
        public static final int AT_MOST = Integer.MIN_VALUE;
        public static final int EXACTLY = 1073741824;
        private static final int MODE_MASK = -1073741824;
        private static final int MODE_SHIFT = 30;
        public static final int UNSPECIFIED = 0;

        public static int getMode(int i) {
            return i & (-1073741824);
        }

        public static int getSize(int i) {
            return i & 1073741823;
        }

        public static int makeMeasureSpec(int i, int i2) {
            return View.sUseBrokenMakeMeasureSpec ? i + i2 : (i & 1073741823) | (i2 & (-1073741824));
        }

        static int adjust(int i, int i2) {
            return makeMeasureSpec(getSize(i2 + i), getMode(i));
        }

        public static String toString(int i) {
            int mode = getMode(i);
            int size = getSize(i);
            StringBuilder sb = new StringBuilder("MeasureSpec: ");
            if (mode == 0) {
                sb.append("UNSPECIFIED ");
            } else if (mode == 1073741824) {
                sb.append("EXACTLY ");
            } else if (mode == Integer.MIN_VALUE) {
                sb.append("AT_MOST ");
            } else {
                sb.append(mode).append(" ");
            }
            sb.append(size);
            return sb.toString();
        }
    }

    class CheckForLongPress implements Runnable {
        private int mOriginalWindowAttachCount;

        CheckForLongPress() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (View.this.isPressed() && View.this.mParent != null && this.mOriginalWindowAttachCount == View.this.mWindowAttachCount && View.this.performLongClick()) {
                View.this.mHasPerformedLongPress = true;
            }
        }

        public void rememberWindowAttachCount() {
            this.mOriginalWindowAttachCount = View.this.mWindowAttachCount;
        }
    }

    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        @Override // java.lang.Runnable
        public void run() {
            View.this.mPrivateFlags &= -33554433;
            View.this.setPressed(true);
            View.this.checkForLongClick(ViewConfiguration.getTapTimeout());
        }
    }

    private final class PerformClick implements Runnable {
        private PerformClick() {
        }

        @Override // java.lang.Runnable
        public void run() {
            View.this.performClick();
        }
    }

    public void hackTurnOffWindowResizeAnim(boolean z) {
        this.mAttachInfo.mTurnOffWindowResizeAnim = z;
    }

    public ViewPropertyAnimator animate() {
        if (this.mAnimator == null) {
            this.mAnimator = new ViewPropertyAnimator(this);
        }
        return this.mAnimator;
    }

    private final class UnsetPressedState implements Runnable {
        private UnsetPressedState() {
        }

        @Override // java.lang.Runnable
        public void run() {
            View.this.setPressed(false);
        }
    }

    public static class BaseSavedState extends AbsSavedState {
        public static final Parcelable.Creator<BaseSavedState> CREATOR = new Parcelable.Creator<BaseSavedState>() { // from class: android.view.View.BaseSavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public BaseSavedState createFromParcel(Parcel parcel) {
                return new BaseSavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public BaseSavedState[] newArray(int i) {
                return new BaseSavedState[i];
            }
        };

        public BaseSavedState(Parcel parcel) {
            super(parcel);
        }

        public BaseSavedState(Parcelable parcelable) {
            super(parcelable);
        }
    }

    static class AttachInfo {
        int mAccessibilityFetchFlags;
        Drawable mAccessibilityFocusDrawable;
        float mApplicationScale;
        Canvas mCanvas;
        int mDisabledSystemUiVisibility;
        final Display mDisplay;
        long mDrawingTime;
        boolean mForceReportNewAttributes;
        int mGlobalSystemUiVisibility;
        final Handler mHandler;
        boolean mHardwareAccelerated;
        boolean mHardwareAccelerationRequested;
        HardwareCanvas mHardwareCanvas;
        HardwareRenderer mHardwareRenderer;
        boolean mHasNonEmptyGivenInternalInsets;
        boolean mHasSystemUiListeners;
        boolean mHasWindowFocus;
        IWindowId mIWindowId;
        boolean mIgnoreDirtyState;
        boolean mInTouchMode;
        boolean mKeepScreenOn;
        boolean mOverscanRequested;
        IBinder mPanelParentWindowToken;
        boolean mRecomputeGlobalAttributes;
        final Callbacks mRootCallbacks;
        View mRootView;
        boolean mScalingRequired;
        boolean mScreenOn;
        final IWindowSession mSession;
        Surface mSurface;
        int mSystemUiVisibility;
        boolean mTurnOffWindowResizeAnim;
        boolean mUse32BitDrawingCache;
        View mViewRequestingLayout;
        final ViewRootImpl mViewRootImpl;
        boolean mViewScrollChanged;
        boolean mViewVisibilityChanged;
        final IWindow mWindow;
        WindowId mWindowId;
        int mWindowLeft;
        final IBinder mWindowToken;
        int mWindowTop;
        int mWindowVisibility;
        final Rect mOverscanInsets = new Rect();
        final Rect mContentInsets = new Rect();
        final Rect mVisibleInsets = new Rect();
        final ViewTreeObserver.InternalInsetsInfo mGivenInternalInsets = new ViewTreeObserver.InternalInsetsInfo();
        final ArrayList<View> mScrollContainers = new ArrayList<>();
        final KeyEvent.DispatcherState mKeyDispatchState = new KeyEvent.DispatcherState();
        boolean mSetIgnoreDirtyState = false;
        final int[] mTransparentLocation = new int[2];
        final int[] mInvalidateChildLocation = new int[2];
        final float[] mTmpTransformLocation = new float[2];
        final ViewTreeObserver mTreeObserver = new ViewTreeObserver();
        final Rect mTmpInvalRect = new Rect();
        final RectF mTmpTransformRect = new RectF();
        final Matrix mTmpMatrix = new Matrix();
        final Transformation mTmpTransformation = new Transformation();
        final ArrayList<View> mTempArrayList = new ArrayList<>(24);
        int mAccessibilityWindowId = -1;
        boolean mDebugLayout = SystemProperties.getBoolean(View.DEBUG_LAYOUT_PROPERTY, false);
        final Point mPoint = new Point();

        interface Callbacks {
            boolean performHapticFeedback(int i, boolean z);

            void playSoundEffect(int i);
        }

        static class InvalidateInfo {
            private static final int POOL_LIMIT = 10;
            private static final Pools.SynchronizedPool<InvalidateInfo> sPool = new Pools.SynchronizedPool<>(10);
            int bottom;
            int left;
            int right;
            View target;
            int top;

            InvalidateInfo() {
            }

            public static InvalidateInfo obtain() {
                InvalidateInfo invalidateInfoAcquire = sPool.acquire();
                return invalidateInfoAcquire != null ? invalidateInfoAcquire : new InvalidateInfo();
            }

            public void recycle() {
                this.target = null;
                sPool.release(this);
            }
        }

        AttachInfo(IWindowSession iWindowSession, IWindow iWindow, Display display, ViewRootImpl viewRootImpl, Handler handler, Callbacks callbacks) {
            this.mSession = iWindowSession;
            this.mWindow = iWindow;
            this.mWindowToken = iWindow.asBinder();
            this.mDisplay = display;
            this.mViewRootImpl = viewRootImpl;
            this.mHandler = handler;
            this.mRootCallbacks = callbacks;
        }
    }

    private static class ScrollabilityCache implements Runnable {
        public static final int FADING = 2;
        public static final int OFF = 0;
        public static final int ON = 1;
        private static final float[] OPAQUE = {255.0f};
        private static final float[] TRANSPARENT = {0.0f};
        public boolean fadeScrollBars;
        public long fadeStartTime;
        public int fadingEdgeLength;
        public View host;
        public float[] interpolatorValues;
        private int mLastColor;
        public final Matrix matrix;
        public final Paint paint;
        public ScrollBarDrawable scrollBar;
        public int scrollBarSize;
        public Shader shader;
        public final Interpolator scrollBarInterpolator = new Interpolator(1, 2);
        public int state = 0;
        public int scrollBarDefaultDelayBeforeFade = ViewConfiguration.getScrollDefaultDelay();
        public int scrollBarFadeDuration = ViewConfiguration.getScrollBarFadeDuration();

        public ScrollabilityCache(ViewConfiguration viewConfiguration, View view) {
            this.fadingEdgeLength = viewConfiguration.getScaledFadingEdgeLength();
            this.scrollBarSize = viewConfiguration.getScaledScrollBarSize();
            Paint paint = new Paint();
            this.paint = paint;
            this.matrix = new Matrix();
            LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 1.0f, -16777216, 0, Shader.TileMode.CLAMP);
            this.shader = linearGradient;
            paint.setShader(linearGradient);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            this.host = view;
        }

        public void setFadeColor(int i) {
            if (i != this.mLastColor) {
                this.mLastColor = i;
                if (i != 0) {
                    LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 1.0f, i | (-16777216), i & 16777215, Shader.TileMode.CLAMP);
                    this.shader = linearGradient;
                    this.paint.setShader(linearGradient);
                    this.paint.setXfermode(null);
                    return;
                }
                LinearGradient linearGradient2 = new LinearGradient(0.0f, 0.0f, 0.0f, 1.0f, -16777216, 0, Shader.TileMode.CLAMP);
                this.shader = linearGradient2;
                this.paint.setShader(linearGradient2);
                this.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
            if (jCurrentAnimationTimeMillis >= this.fadeStartTime) {
                int i = (int) jCurrentAnimationTimeMillis;
                Interpolator interpolator = this.scrollBarInterpolator;
                interpolator.setKeyFrame(0, i, OPAQUE);
                interpolator.setKeyFrame(1, i + this.scrollBarFadeDuration, TRANSPARENT);
                this.state = 2;
                this.host.invalidate(true);
            }
        }
    }

    private class SendViewScrolledAccessibilityEvent implements Runnable {
        public volatile boolean mIsPending;

        private SendViewScrolledAccessibilityEvent() {
        }

        @Override // java.lang.Runnable
        public void run() {
            View.this.sendAccessibilityEvent(4096);
            this.mIsPending = false;
        }
    }

    public static class AccessibilityDelegate {
        public AccessibilityNodeProvider getAccessibilityNodeProvider(View view) {
            return null;
        }

        public void sendAccessibilityEvent(View view, int i) {
            view.sendAccessibilityEventInternal(i);
        }

        public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
            return view.performAccessibilityActionInternal(i, bundle);
        }

        public void sendAccessibilityEventUnchecked(View view, AccessibilityEvent accessibilityEvent) {
            view.sendAccessibilityEventUncheckedInternal(accessibilityEvent);
        }

        public boolean dispatchPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            return view.dispatchPopulateAccessibilityEventInternal(accessibilityEvent);
        }

        public void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            view.onPopulateAccessibilityEventInternal(accessibilityEvent);
        }

        public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            view.onInitializeAccessibilityEventInternal(accessibilityEvent);
        }

        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
            view.onInitializeAccessibilityNodeInfoInternal(accessibilityNodeInfo);
        }

        public boolean onRequestSendAccessibilityEvent(ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent) {
            return viewGroup.onRequestSendAccessibilityEventInternal(view, accessibilityEvent);
        }

        public AccessibilityNodeInfo createAccessibilityNodeInfo(View view) {
            return view.createAccessibilityNodeInfoInternal();
        }
    }

    private class MatchIdPredicate implements Predicate<View> {
        public int mId;

        private MatchIdPredicate() {
        }

        public boolean apply(View view) {
            return view.mID == this.mId;
        }
    }

    private class MatchLabelForPredicate implements Predicate<View> {
        private int mLabeledId;

        private MatchLabelForPredicate() {
        }

        public boolean apply(View view) {
            return view.mLabelForId == this.mLabeledId;
        }
    }

    private class SendViewStateChangedAccessibilityEvent implements Runnable {
        private int mChangeTypes;
        private long mLastEventTimeMillis;
        private boolean mPosted;
        private boolean mPostedWithDelay;

        private SendViewStateChangedAccessibilityEvent() {
            this.mChangeTypes = 0;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mPosted = false;
            this.mPostedWithDelay = false;
            this.mLastEventTimeMillis = SystemClock.uptimeMillis();
            if (AccessibilityManager.getInstance(View.this.mContext).isEnabled()) {
                AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain();
                accessibilityEventObtain.setEventType(2048);
                accessibilityEventObtain.setContentChangeTypes(this.mChangeTypes);
                View.this.sendAccessibilityEventUnchecked(accessibilityEventObtain);
            }
            this.mChangeTypes = 0;
        }

        public void runOrPost(int i) {
            this.mChangeTypes = i | this.mChangeTypes;
            if (View.this.inLiveRegion()) {
                if (this.mPostedWithDelay) {
                    View.this.removeCallbacks(this);
                    this.mPostedWithDelay = false;
                }
                if (this.mPosted) {
                    return;
                }
                View.this.post(this);
                this.mPosted = true;
                return;
            }
            if (this.mPosted) {
                return;
            }
            long jUptimeMillis = SystemClock.uptimeMillis() - this.mLastEventTimeMillis;
            long sendRecurringAccessibilityEventsInterval = ViewConfiguration.getSendRecurringAccessibilityEventsInterval();
            if (jUptimeMillis >= sendRecurringAccessibilityEventsInterval) {
                View.this.removeCallbacks(this);
                run();
            } else {
                View.this.postDelayed(this, sendRecurringAccessibilityEventsInterval - jUptimeMillis);
                this.mPosted = true;
                this.mPostedWithDelay = true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean inLiveRegion() {
        if (getAccessibilityLiveRegion() != 0) {
            return true;
        }
        for (ViewParent parent = getParent(); parent instanceof View; parent = parent.getParent()) {
            if (((View) parent).getAccessibilityLiveRegion() != 0) {
                return true;
            }
        }
        return false;
    }

    private static void dumpFlags() {
        HashMap mapNewHashMap = Maps.newHashMap();
        try {
            for (Field field : View.class.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                    if (field.getType().equals(Integer.TYPE)) {
                        dumpFlag(mapNewHashMap, field.getName(), field.getInt(null));
                    } else if (field.getType().equals(int[].class)) {
                        int[] iArr = (int[]) field.get(null);
                        for (int i = 0; i < iArr.length; i++) {
                            dumpFlag(mapNewHashMap, field.getName() + "[" + i + "]", iArr[i]);
                        }
                    }
                }
            }
            ArrayList arrayListNewArrayList = Lists.newArrayList();
            arrayListNewArrayList.addAll(mapNewHashMap.keySet());
            Collections.sort(arrayListNewArrayList);
            Iterator it = arrayListNewArrayList.iterator();
            while (it.hasNext()) {
                Log.d(VIEW_LOG_TAG, (String) mapNewHashMap.get((String) it.next()));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void dumpFlag(HashMap<String, String> map, String str, int i) {
        String strReplace = String.format("%32s", Integer.toBinaryString(i)).replace('0', ' ');
        int iIndexOf = str.indexOf(95);
        map.put((iIndexOf > 0 ? str.substring(0, iIndexOf) : str) + strReplace + str, strReplace + " " + str);
    }
}
