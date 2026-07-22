package android.view;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.ActivityManagerNative;
import android.content.ClipDescription;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.LinkQualityInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.util.Slog;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Choreographer;
import android.view.HardwareRenderer;
import android.view.IWindow;
import android.view.InputDevice;
import android.view.InputQueue;
import android.view.KeyCharacterMap;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.accessibility.IAccessibilityInteractionConnection;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Scroller;
import com.android.internal.os.SomeArgs;
import com.android.internal.policy.PolicyManager;
import com.android.internal.view.BaseSurfaceHolder;
import com.android.internal.view.RootViewSurfaceTaker;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;

/* JADX INFO: loaded from: classes.dex */
public final class ViewRootImpl implements ViewParent, View.AttachInfo.Callbacks, HardwareRenderer.HardwareDrawCallbacks {
    private static final boolean DBG = false;
    private static final boolean DEBUG_CONFIGURATION = false;
    private static final boolean DEBUG_DIALOG = false;
    private static final boolean DEBUG_DRAW = false;
    private static final boolean DEBUG_FPS = false;
    private static final boolean DEBUG_IMF = false;
    private static final boolean DEBUG_INPUT_PROCESSING = false;
    private static final boolean DEBUG_INPUT_RESIZE = false;
    private static final boolean DEBUG_LAYOUT = false;
    private static final boolean DEBUG_ORIENTATION = false;
    private static final boolean DEBUG_TRACKBALL = false;
    private static final boolean LOCAL_LOGV = false;
    private static final int MAX_QUEUED_INPUT_EVENT_POOL_SIZE = 10;
    static final int MAX_TRACKBALL_DELAY = 250;
    private static final int MSG_CHECK_FOCUS = 13;
    private static final int MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST = 21;
    private static final int MSG_CLOSE_SYSTEM_DIALOGS = 14;
    private static final int MSG_DIE = 3;
    private static final int MSG_DISPATCH_APP_VISIBILITY = 8;
    private static final int MSG_DISPATCH_DONE_ANIMATING = 22;
    private static final int MSG_DISPATCH_DRAG_EVENT = 15;
    private static final int MSG_DISPATCH_DRAG_LOCATION_EVENT = 16;
    private static final int MSG_DISPATCH_GET_NEW_SURFACE = 9;
    private static final int MSG_DISPATCH_INPUT_EVENT = 7;
    private static final int MSG_DISPATCH_KEY_FROM_IME = 11;
    private static final int MSG_DISPATCH_SCREEN_STATE = 20;
    private static final int MSG_DISPATCH_SYSTEM_UI_VISIBILITY = 17;
    private static final int MSG_FINISH_INPUT_CONNECTION = 12;
    private static final int MSG_FLUSH_LAYER_UPDATES = 25;
    private static final int MSG_INVALIDATE = 1;
    private static final int MSG_INVALIDATE_RECT = 2;
    private static final int MSG_INVALIDATE_WORLD = 23;
    private static final int MSG_PROCESS_INPUT_EVENTS = 19;
    private static final int MSG_RESIZED = 4;
    private static final int MSG_RESIZED_REPORT = 5;
    private static final int MSG_UPDATE_CONFIGURATION = 18;
    private static final int MSG_WINDOW_FOCUS_CHANGED = 6;
    private static final int MSG_WINDOW_MOVED = 24;
    private static final String PROPERTY_MEDIA_DISABLED = "config.disable_media";
    private static final String PROPERTY_PROFILE_RENDERING = "viewroot.profile_rendering";
    private static final String TAG = "ViewRootImpl";
    View mAccessibilityFocusedHost;
    AccessibilityNodeInfo mAccessibilityFocusedVirtualView;
    AccessibilityInteractionConnectionManager mAccessibilityInteractionConnectionManager;
    AccessibilityInteractionController mAccessibilityInteractionController;
    final AccessibilityManager mAccessibilityManager;
    boolean mAdded;
    boolean mAddedTouchMode;
    final View.AttachInfo mAttachInfo;
    AudioManager mAudioManager;
    final String mBasePackageName;
    Choreographer mChoreographer;
    int mClientWindowLayoutFlags;
    boolean mConsumeBatchedInputScheduled;
    final ConsumeBatchedInputRunnable mConsumedBatchedInputRunnable;
    final Context mContext;
    int mCurScrollY;
    View mCurrentDragView;
    private final int mDensity;
    Rect mDirty;
    final Display mDisplay;
    final DisplayAdjustments mDisplayAdjustments;
    ClipDescription mDragDescription;
    boolean mDrawDuringWindowsAnimating;
    boolean mDrawingAllowed;
    FallbackEventHandler mFallbackEventHandler;
    boolean mFirst;
    InputStage mFirstInputStage;
    InputStage mFirstPostImeInputStage;
    boolean mFitSystemWindowsRequested;
    boolean mFlipControllerFallbackKeys;
    private int mFpsNumFrames;
    boolean mFullRedrawNeeded;
    final ViewRootHandler mHandler;
    int mHardwareYOffset;
    boolean mHasHadWindowFocus;
    int mHeight;
    private final SurfaceHolder mHolder;
    InputChannel mInputChannel;
    protected final InputEventConsistencyVerifier mInputEventConsistencyVerifier;
    WindowInputEventReceiver mInputEventReceiver;
    InputQueue mInputQueue;
    InputQueue.Callback mInputQueueCallback;
    final InvalidateOnAnimationRunnable mInvalidateOnAnimationRunnable;
    boolean mIsAnimating;
    boolean mIsCreating;
    boolean mIsDrawing;
    boolean mIsInTraversal;
    boolean mLastOverscanRequested;
    WeakReference<View> mLastScrolledFocus;
    int mLastSystemUiVisibility;
    boolean mLastWasImTarget;
    boolean mLayoutRequested;
    volatile Object mLocalDragState;
    final WindowLeaked mLocation;
    private boolean mMediaDisabled;
    boolean mNewSurfaceNeeded;
    private final int mNoncompatDensity;
    int mPendingInputEventCount;
    QueuedInputEvent mPendingInputEventHead;
    QueuedInputEvent mPendingInputEventTail;
    private ArrayList<LayoutTransition> mPendingTransitions;
    final Region mPreviousTransparentRegion;
    boolean mProcessInputEventsScheduled;
    private boolean mProfile;
    private boolean mProfileRendering;
    private QueuedInputEvent mQueuedInputEventPool;
    private int mQueuedInputEventPoolSize;
    private boolean mRemoved;
    private Choreographer.FrameCallback mRenderProfiler;
    private boolean mRenderProfilingEnabled;
    boolean mReportNextDraw;
    int mResizeAlpha;
    HardwareLayer mResizeBuffer;
    int mResizeBufferDuration;
    long mResizeBufferStartTime;
    final Paint mResizePaint;
    boolean mScrollMayChange;
    int mScrollY;
    Scroller mScroller;
    SendWindowContentChangedAccessibilityEvent mSendWindowContentChangedAccessibilityEvent;
    int mSeq;
    int mSoftInputMode;
    BaseSurfaceHolder mSurfaceHolder;
    SurfaceHolder.Callback2 mSurfaceHolderCallback;
    final int mTargetSdkVersion;
    HashSet<View> mTempHashSet;
    final Rect mTempRect;
    final Thread mThread;
    CompatibilityInfo.Translator mTranslator;
    final Region mTransparentRegion;
    int mTraversalBarrier;
    final TraversalRunnable mTraversalRunnable;
    boolean mTraversalScheduled;
    boolean mUpdateTranformHint;
    View mView;
    final ViewConfiguration mViewConfiguration;
    private int mViewLayoutDirectionInitial;
    int mViewVisibility;
    final Rect mVisRect;
    int mWidth;
    boolean mWillDrawSoon;
    final Rect mWinFrame;
    final W mWindow;
    final IWindowSession mWindowSession;
    boolean mWindowsAnimating;
    static final ThreadLocal<RunQueue> sRunQueues = new ThreadLocal<>();
    static final ArrayList<Runnable> sFirstDrawHandlers = new ArrayList<>();
    static boolean sFirstDrawComplete = false;
    static final ArrayList<ComponentCallbacks> sConfigCallbacks = new ArrayList<>();
    static final Interpolator mResizeInterpolator = new AccelerateDecelerateInterpolator();
    final int[] mTmpLocation = new int[2];
    final TypedValue mTmpValue = new TypedValue();
    final WindowManager.LayoutParams mWindowAttributes = new WindowManager.LayoutParams();
    boolean mAppVisible = true;
    int mOrigWindowType = -1;
    boolean mStopped = false;
    boolean mLastInCompatMode = false;
    final Rect mCurrentDirty = new Rect();
    String mPendingInputEventQueueLengthCounterName = "pq";
    boolean mWindowAttributesChanged = false;
    int mWindowAttributesChangesFlag = 0;
    private final Surface mSurface = new Surface();
    final Rect mPendingOverscanInsets = new Rect();
    final Rect mPendingVisibleInsets = new Rect();
    final Rect mPendingContentInsets = new Rect();
    final ViewTreeObserver.InternalInsetsInfo mLastGivenInsets = new ViewTreeObserver.InternalInsetsInfo();
    final Rect mFitSystemWindowsInsets = new Rect();
    final Configuration mLastConfiguration = new Configuration();
    final Configuration mPendingConfiguration = new Configuration();
    final PointF mDragPoint = new PointF();
    final PointF mLastTouchPoint = new PointF();
    private long mFpsStartTime = -1;
    private long mFpsPrevTime = -1;
    private final ArrayList<DisplayList> mDisplayLists = new ArrayList<>();
    private boolean mInLayout = false;
    ArrayList<View> mLayoutRequesters = new ArrayList<>();
    boolean mHandlingLayoutInLayoutRequest = false;

    @Override // android.view.ViewParent
    public void bringChildToFront(View view) {
    }

    @Override // android.view.ViewParent
    public boolean canResolveLayoutDirection() {
        return true;
    }

    @Override // android.view.ViewParent
    public boolean canResolveTextAlignment() {
        return true;
    }

    @Override // android.view.ViewParent
    public boolean canResolveTextDirection() {
        return true;
    }

    @Override // android.view.ViewParent
    public void childDrawableStateChanged(View view) {
    }

    @Override // android.view.ViewParent
    public void childHasTransientStateChanged(View view, boolean z) {
    }

    @Override // android.view.ViewParent
    public void createContextMenu(ContextMenu contextMenu) {
    }

    @Override // android.view.ViewParent
    public int getLayoutDirection() {
        return 0;
    }

    @Override // android.view.ViewParent
    public ViewParent getParent() {
        return null;
    }

    @Override // android.view.ViewParent
    public ViewParent getParentForAccessibility() {
        return null;
    }

    @Override // android.view.ViewParent
    public int getTextAlignment() {
        return 1;
    }

    @Override // android.view.ViewParent
    public int getTextDirection() {
        return 1;
    }

    @Override // android.view.ViewParent
    public boolean isLayoutDirectionResolved() {
        return true;
    }

    @Override // android.view.ViewParent
    public boolean isTextAlignmentResolved() {
        return true;
    }

    @Override // android.view.ViewParent
    public boolean isTextDirectionResolved() {
        return true;
    }

    @Override // android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean z) {
    }

    @Override // android.view.ViewParent
    public boolean showContextMenuForChild(View view) {
        return false;
    }

    @Override // android.view.ViewParent
    public ActionMode startActionModeForChild(View view, ActionMode.Callback callback) {
        return null;
    }

    static final class SystemUiVisibilityInfo {
        int globalVisibility;
        int localChanges;
        int localValue;
        int seq;

        SystemUiVisibilityInfo() {
        }
    }

    public ViewRootImpl(Context context, Display display) {
        this.mInputEventConsistencyVerifier = InputEventConsistencyVerifier.isInstrumentationEnabled() ? new InputEventConsistencyVerifier(this, 0) : null;
        this.mProfile = false;
        this.mResizePaint = new Paint();
        ViewRootHandler viewRootHandler = new ViewRootHandler();
        this.mHandler = viewRootHandler;
        this.mTraversalRunnable = new TraversalRunnable();
        this.mConsumedBatchedInputRunnable = new ConsumeBatchedInputRunnable();
        this.mInvalidateOnAnimationRunnable = new InvalidateOnAnimationRunnable();
        this.mHolder = new SurfaceHolder() { // from class: android.view.ViewRootImpl.5
            @Override // android.view.SurfaceHolder
            public void addCallback(SurfaceHolder.Callback callback) {
            }

            @Override // android.view.SurfaceHolder
            public Rect getSurfaceFrame() {
                return null;
            }

            @Override // android.view.SurfaceHolder
            public boolean isCreating() {
                return false;
            }

            @Override // android.view.SurfaceHolder
            public Canvas lockCanvas() {
                return null;
            }

            @Override // android.view.SurfaceHolder
            public Canvas lockCanvas(Rect rect) {
                return null;
            }

            @Override // android.view.SurfaceHolder
            public void removeCallback(SurfaceHolder.Callback callback) {
            }

            @Override // android.view.SurfaceHolder
            public void setFixedSize(int i, int i2) {
            }

            @Override // android.view.SurfaceHolder
            public void setFormat(int i) {
            }

            @Override // android.view.SurfaceHolder
            public void setKeepScreenOn(boolean z) {
            }

            @Override // android.view.SurfaceHolder
            public void setSizeFromLayout() {
            }

            @Override // android.view.SurfaceHolder
            public void setType(int i) {
            }

            @Override // android.view.SurfaceHolder
            public void unlockCanvasAndPost(Canvas canvas) {
            }

            @Override // android.view.SurfaceHolder
            public Surface getSurface() {
                return ViewRootImpl.this.mSurface;
            }
        };
        this.mContext = context;
        IWindowSession windowSession = WindowManagerGlobal.getWindowSession();
        this.mWindowSession = windowSession;
        this.mDisplay = display;
        this.mBasePackageName = context.getBasePackageName();
        this.mDisplayAdjustments = display.getDisplayAdjustments();
        this.mThread = Thread.currentThread();
        WindowLeaked windowLeaked = new WindowLeaked(null);
        this.mLocation = windowLeaked;
        windowLeaked.fillInStackTrace();
        this.mWidth = -1;
        this.mHeight = -1;
        this.mDirty = new Rect();
        this.mTempRect = new Rect();
        this.mVisRect = new Rect();
        this.mWinFrame = new Rect();
        W w = new W(this);
        this.mWindow = w;
        this.mTargetSdkVersion = context.getApplicationInfo().targetSdkVersion;
        this.mViewVisibility = 8;
        this.mTransparentRegion = new Region();
        this.mPreviousTransparentRegion = new Region();
        this.mFirst = true;
        this.mAdded = false;
        AccessibilityManager accessibilityManager = AccessibilityManager.getInstance(context);
        this.mAccessibilityManager = accessibilityManager;
        AccessibilityInteractionConnectionManager accessibilityInteractionConnectionManager = new AccessibilityInteractionConnectionManager();
        this.mAccessibilityInteractionConnectionManager = accessibilityInteractionConnectionManager;
        accessibilityManager.addAccessibilityStateChangeListener(accessibilityInteractionConnectionManager);
        View.AttachInfo attachInfo = new View.AttachInfo(windowSession, w, display, this, viewRootHandler, this);
        this.mAttachInfo = attachInfo;
        this.mViewConfiguration = ViewConfiguration.get(context);
        this.mDensity = context.getResources().getDisplayMetrics().densityDpi;
        this.mNoncompatDensity = context.getResources().getDisplayMetrics().noncompatDensityDpi;
        this.mFallbackEventHandler = PolicyManager.makeNewFallbackEventHandler(context);
        this.mChoreographer = Choreographer.getInstance();
        this.mFlipControllerFallbackKeys = context.getResources().getBoolean(17891339);
        attachInfo.mScreenOn = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).isScreenOn();
        loadSystemProperties();
    }

    public static void addFirstDrawHandler(Runnable runnable) {
        ArrayList<Runnable> arrayList = sFirstDrawHandlers;
        synchronized (arrayList) {
            if (!sFirstDrawComplete) {
                arrayList.add(runnable);
            }
        }
    }

    public static void addConfigCallback(ComponentCallbacks componentCallbacks) {
        ArrayList<ComponentCallbacks> arrayList = sConfigCallbacks;
        synchronized (arrayList) {
            arrayList.add(componentCallbacks);
        }
    }

    public void profile() {
        this.mProfile = true;
    }

    static boolean isInTouchMode() {
        IWindowSession iWindowSessionPeekWindowSession = WindowManagerGlobal.peekWindowSession();
        if (iWindowSessionPeekWindowSession == null) {
            return false;
        }
        try {
            return iWindowSessionPeekWindowSession.getInTouchMode();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public void setView(View view, WindowManager.LayoutParams layoutParams, View view2) {
        boolean z;
        synchronized (this) {
            if (this.mView == null) {
                this.mView = view;
                this.mViewLayoutDirectionInitial = view.getRawLayoutDirection();
                this.mFallbackEventHandler.setView(view);
                this.mWindowAttributes.copyFrom(layoutParams);
                if (this.mWindowAttributes.packageName == null) {
                    this.mWindowAttributes.packageName = this.mBasePackageName;
                }
                WindowManager.LayoutParams layoutParams2 = this.mWindowAttributes;
                this.mClientWindowLayoutFlags = layoutParams2.flags;
                setAccessibilityFocus(null, null);
                if (view instanceof RootViewSurfaceTaker) {
                    SurfaceHolder.Callback2 callback2WillYouTakeTheSurface = ((RootViewSurfaceTaker) view).willYouTakeTheSurface();
                    this.mSurfaceHolderCallback = callback2WillYouTakeTheSurface;
                    if (callback2WillYouTakeTheSurface != null) {
                        TakenSurfaceHolder takenSurfaceHolder = new TakenSurfaceHolder();
                        this.mSurfaceHolder = takenSurfaceHolder;
                        takenSurfaceHolder.setFormat(0);
                    }
                }
                CompatibilityInfo compatibilityInfo = this.mDisplayAdjustments.getCompatibilityInfo();
                this.mTranslator = compatibilityInfo.getTranslator();
                this.mDisplayAdjustments.setActivityToken(layoutParams2.token);
                if (this.mSurfaceHolder == null) {
                    enableHardwareAcceleration(layoutParams2);
                }
                CompatibilityInfo.Translator translator = this.mTranslator;
                if (translator != null) {
                    this.mSurface.setCompatibilityTranslator(translator);
                    layoutParams2.backup();
                    this.mTranslator.translateWindowLayout(layoutParams2);
                    z = true;
                } else {
                    z = false;
                }
                if (!compatibilityInfo.supportsScreen()) {
                    layoutParams2.privateFlags |= 128;
                    this.mLastInCompatMode = true;
                }
                this.mSoftInputMode = layoutParams2.softInputMode;
                this.mWindowAttributesChanged = true;
                this.mWindowAttributesChangesFlag = -1;
                this.mAttachInfo.mRootView = view;
                this.mAttachInfo.mScalingRequired = this.mTranslator != null;
                View.AttachInfo attachInfo = this.mAttachInfo;
                CompatibilityInfo.Translator translator2 = this.mTranslator;
                attachInfo.mApplicationScale = translator2 == null ? 1.0f : translator2.applicationScale;
                if (view2 != null) {
                    this.mAttachInfo.mPanelParentWindowToken = view2.getApplicationWindowToken();
                }
                this.mAdded = true;
                requestLayout();
                if ((this.mWindowAttributes.inputFeatures & 2) == 0) {
                    this.mInputChannel = new InputChannel();
                }
                try {
                    try {
                        this.mOrigWindowType = this.mWindowAttributes.type;
                        this.mAttachInfo.mRecomputeGlobalAttributes = true;
                        collectViewAttributes();
                        int iAddToDisplay = this.mWindowSession.addToDisplay(this.mWindow, this.mSeq, this.mWindowAttributes, getHostVisibility(), this.mDisplay.getDisplayId(), this.mAttachInfo.mContentInsets, this.mInputChannel);
                        CompatibilityInfo.Translator translator3 = this.mTranslator;
                        if (translator3 != null) {
                            translator3.translateRectInScreenToAppWindow(this.mAttachInfo.mContentInsets);
                        }
                        this.mPendingOverscanInsets.set(0, 0, 0, 0);
                        this.mPendingContentInsets.set(this.mAttachInfo.mContentInsets);
                        this.mPendingVisibleInsets.set(0, 0, 0, 0);
                        if (iAddToDisplay < 0) {
                            this.mAttachInfo.mRootView = null;
                            this.mAdded = false;
                            this.mFallbackEventHandler.setView(null);
                            unscheduleTraversals();
                            setAccessibilityFocus(null, null);
                            switch (iAddToDisplay) {
                                case -9:
                                    throw new WindowManager.InvalidDisplayException("Unable to add window " + this.mWindow + " -- the specified display can not be found");
                                case -8:
                                    throw new WindowManager.BadTokenException("Unable to add window " + this.mWindow + " -- permission denied for this window type");
                                case -7:
                                    throw new WindowManager.BadTokenException("Unable to add window " + this.mWindow + " -- another window of this type already exists");
                                case -6:
                                    return;
                                case -5:
                                    throw new WindowManager.BadTokenException("Unable to add window -- window " + this.mWindow + " has already been added");
                                case -4:
                                    throw new WindowManager.BadTokenException("Unable to add window -- app for token " + layoutParams2.token + " is exiting");
                                case -3:
                                    throw new WindowManager.BadTokenException("Unable to add window -- token " + layoutParams2.token + " is not for an application");
                                case -2:
                                case -1:
                                    throw new WindowManager.BadTokenException("Unable to add window -- token " + layoutParams2.token + " is not valid; is your activity running?");
                                default:
                                    throw new RuntimeException("Unable to add window -- unknown error code " + iAddToDisplay);
                            }
                        }
                        if (view instanceof RootViewSurfaceTaker) {
                            this.mInputQueueCallback = ((RootViewSurfaceTaker) view).willYouTakeTheInputQueue();
                        }
                        if (this.mInputChannel != null) {
                            if (this.mInputQueueCallback != null) {
                                InputQueue inputQueue = new InputQueue();
                                this.mInputQueue = inputQueue;
                                this.mInputQueueCallback.onInputQueueCreated(inputQueue);
                            }
                            this.mInputEventReceiver = new WindowInputEventReceiver(this.mInputChannel, Looper.myLooper());
                        }
                        view.assignParent(this);
                        this.mAddedTouchMode = (iAddToDisplay & 1) != 0;
                        this.mAppVisible = (iAddToDisplay & 2) != 0;
                        if (this.mAccessibilityManager.isEnabled()) {
                            this.mAccessibilityInteractionConnectionManager.ensureConnection();
                        }
                        if (view.getImportantForAccessibility() == 0) {
                            view.setImportantForAccessibility(1);
                        }
                        CharSequence title = layoutParams2.getTitle();
                        EarlyPostImeInputStage earlyPostImeInputStage = new EarlyPostImeInputStage(new NativePostImeInputStage(new ViewPostImeInputStage(new SyntheticInputStage()), "aq:native-post-ime:" + ((Object) title)));
                        this.mFirstInputStage = new NativePreImeInputStage(new ViewPreImeInputStage(new ImeInputStage(earlyPostImeInputStage, "aq:ime:" + ((Object) title))), "aq:native-pre-ime:" + ((Object) title));
                        this.mFirstPostImeInputStage = earlyPostImeInputStage;
                        this.mPendingInputEventQueueLengthCounterName = "aq:pending:" + ((Object) title);
                    } catch (RemoteException e) {
                        this.mAdded = false;
                        this.mView = null;
                        this.mAttachInfo.mRootView = null;
                        this.mInputChannel = null;
                        this.mFallbackEventHandler.setView(null);
                        unscheduleTraversals();
                        setAccessibilityFocus(null, null);
                        throw new RuntimeException("Adding window failed", e);
                    }
                } finally {
                    if (z) {
                        layoutParams2.restore();
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isInLocalFocusMode() {
        return (this.mWindowAttributes.flags & 268435456) != 0;
    }

    void destroyHardwareResources() {
        invalidateDisplayLists();
        if (this.mAttachInfo.mHardwareRenderer != null) {
            this.mAttachInfo.mHardwareRenderer.destroyHardwareResources(this.mView);
            this.mAttachInfo.mHardwareRenderer.destroy(false);
        }
    }

    void destroyHardwareLayers() {
        if (this.mThread != Thread.currentThread()) {
            if (this.mAttachInfo.mHardwareRenderer == null || !this.mAttachInfo.mHardwareRenderer.isEnabled()) {
                return;
            }
            HardwareRenderer.trimMemory(60);
            return;
        }
        invalidateDisplayLists();
        if (this.mAttachInfo.mHardwareRenderer == null || !this.mAttachInfo.mHardwareRenderer.isEnabled()) {
            return;
        }
        this.mAttachInfo.mHardwareRenderer.destroyLayers(this.mView);
    }

    void pushHardwareLayerUpdate(HardwareLayer hardwareLayer) {
        if (this.mAttachInfo.mHardwareRenderer == null || !this.mAttachInfo.mHardwareRenderer.isEnabled()) {
            return;
        }
        this.mAttachInfo.mHardwareRenderer.pushLayerUpdate(hardwareLayer);
    }

    void flushHardwareLayerUpdates() {
        if (this.mAttachInfo.mHardwareRenderer != null && this.mAttachInfo.mHardwareRenderer.isEnabled() && this.mAttachInfo.mHardwareRenderer.validate()) {
            this.mAttachInfo.mHardwareRenderer.flushLayerUpdates();
        }
    }

    void dispatchFlushHardwareLayerUpdates() {
        this.mHandler.removeMessages(25);
        ViewRootHandler viewRootHandler = this.mHandler;
        viewRootHandler.sendMessageAtFrontOfQueue(viewRootHandler.obtainMessage(25));
    }

    public boolean attachFunctor(int i) {
        if (this.mAttachInfo.mHardwareRenderer == null || !this.mAttachInfo.mHardwareRenderer.isEnabled()) {
            return false;
        }
        return this.mAttachInfo.mHardwareRenderer.attachFunctor(this.mAttachInfo, i);
    }

    public void detachFunctor(int i) {
        if (this.mAttachInfo.mHardwareRenderer != null) {
            this.mAttachInfo.mHardwareRenderer.detachFunctor(i);
        }
    }

    private void enableHardwareAcceleration(WindowManager.LayoutParams layoutParams) {
        this.mAttachInfo.mHardwareAccelerated = false;
        this.mAttachInfo.mHardwareAccelerationRequested = false;
        if (this.mTranslator != null) {
            return;
        }
        boolean z = (layoutParams.flags & 16777216) != 0;
        if (z) {
            String[] strArr = {"softmaker.applications.planmaker", "softmaker.applications.presentations", "com.square_enix.million_cn", "air.com.forthedream.dreamworks"};
            String packageName = this.mView.getContext().getPackageName();
            int i = 0;
            while (true) {
                if (i >= 4) {
                    break;
                }
                if (packageName.indexOf(strArr[i]) != -1) {
                    z = false;
                    break;
                }
                i++;
            }
        }
        if (z && HardwareRenderer.isAvailable()) {
            boolean z2 = (layoutParams.privateFlags & 1) != 0;
            boolean z3 = (layoutParams.privateFlags & 2) != 0;
            if (HardwareRenderer.sRendererDisabled && (!HardwareRenderer.sSystemRendererDisabled || !z3)) {
                if (z2) {
                    this.mAttachInfo.mHardwareAccelerationRequested = true;
                }
            } else {
                if (!HardwareRenderer.sSystemRendererDisabled && Looper.getMainLooper() != Looper.myLooper()) {
                    Log.w("HardwareRenderer", "Attempting to initialize hardware acceleration outside of the main thread, aborting");
                    return;
                }
                if (this.mAttachInfo.mHardwareRenderer != null) {
                    this.mAttachInfo.mHardwareRenderer.destroy(true);
                }
                this.mAttachInfo.mHardwareRenderer = HardwareRenderer.createGlRenderer(2, layoutParams.format != -1);
                if (this.mAttachInfo.mHardwareRenderer != null) {
                    this.mAttachInfo.mHardwareRenderer.setName(layoutParams.getTitle().toString());
                    View.AttachInfo attachInfo = this.mAttachInfo;
                    attachInfo.mHardwareAccelerationRequested = true;
                    attachInfo.mHardwareAccelerated = true;
                }
            }
        }
    }

    public View getView() {
        return this.mView;
    }

    final WindowLeaked getLocation() {
        return this.mLocation;
    }

    void setLayoutParams(WindowManager.LayoutParams layoutParams, boolean z) {
        synchronized (this) {
            int i = this.mWindowAttributes.softInputMode;
            this.mClientWindowLayoutFlags = layoutParams.flags;
            int i2 = this.mWindowAttributes.privateFlags & 128;
            layoutParams.systemUiVisibility = this.mWindowAttributes.systemUiVisibility;
            layoutParams.subtreeSystemUiVisibility = this.mWindowAttributes.subtreeSystemUiVisibility;
            int iCopyFrom = this.mWindowAttributes.copyFrom(layoutParams);
            this.mWindowAttributesChangesFlag = iCopyFrom;
            if ((iCopyFrom & 524288) != 0) {
                this.mAttachInfo.mRecomputeGlobalAttributes = true;
            }
            if (this.mWindowAttributes.packageName == null) {
                this.mWindowAttributes.packageName = this.mBasePackageName;
            }
            WindowManager.LayoutParams layoutParams2 = this.mWindowAttributes;
            layoutParams2.privateFlags = i2 | layoutParams2.privateFlags;
            applyKeepScreenOnFlag(this.mWindowAttributes);
            if (z) {
                this.mSoftInputMode = layoutParams.softInputMode;
                requestLayout();
            }
            if ((layoutParams.softInputMode & 240) == 0) {
                WindowManager.LayoutParams layoutParams3 = this.mWindowAttributes;
                layoutParams3.softInputMode = (layoutParams3.softInputMode & (-241)) | (i & 240);
            }
            this.mWindowAttributesChanged = true;
            this.mUpdateTranformHint = true;
            scheduleTraversals();
        }
    }

    void handleAppVisibility(boolean z) {
        if (this.mAppVisible != z) {
            this.mAppVisible = z;
            scheduleTraversals();
        }
    }

    void handleGetNewSurface() {
        this.mNewSurfaceNeeded = true;
        this.mFullRedrawNeeded = true;
        scheduleTraversals();
    }

    void handleScreenStateChange(boolean z) {
        if (z != this.mAttachInfo.mScreenOn) {
            this.mAttachInfo.mScreenOn = z;
            View view = this.mView;
            if (view != null) {
                view.dispatchScreenStateChanged(z ? 1 : 0);
            }
            if (z) {
                this.mFullRedrawNeeded = true;
                scheduleTraversals();
            }
        }
    }

    @Override // android.view.ViewParent
    public void requestFitSystemWindows() {
        checkThread();
        this.mFitSystemWindowsRequested = true;
        scheduleTraversals();
    }

    @Override // android.view.ViewParent
    public void requestLayout() {
        if (this.mHandlingLayoutInLayoutRequest) {
            return;
        }
        checkThread();
        this.mLayoutRequested = true;
        scheduleTraversals();
    }

    @Override // android.view.ViewParent
    public boolean isLayoutRequested() {
        return this.mLayoutRequested;
    }

    void invalidate() {
        this.mDirty.set(0, 0, this.mWidth, this.mHeight);
        scheduleTraversals();
    }

    void invalidateWorld(View view) {
        view.invalidate();
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                invalidateWorld(viewGroup.getChildAt(i));
            }
        }
    }

    @Override // android.view.ViewParent
    public void invalidateChild(View view, Rect rect) {
        invalidateChildInParent(null, rect);
    }

    @Override // android.view.ViewParent
    public ViewParent invalidateChildInParent(int[] iArr, Rect rect) {
        checkThread();
        if (rect == null) {
            invalidate();
            return null;
        }
        if (rect.isEmpty() && !this.mIsAnimating) {
            return null;
        }
        if (this.mCurScrollY != 0 || this.mTranslator != null) {
            this.mTempRect.set(rect);
            rect = this.mTempRect;
            int i = this.mCurScrollY;
            if (i != 0) {
                rect.offset(0, -i);
            }
            CompatibilityInfo.Translator translator = this.mTranslator;
            if (translator != null) {
                translator.translateRectInAppWindowToScreen(rect);
            }
            if (this.mAttachInfo.mScalingRequired) {
                rect.inset(-1, -1);
            }
        }
        Rect rect2 = this.mDirty;
        if (!rect2.isEmpty() && !rect2.contains(rect)) {
            this.mAttachInfo.mSetIgnoreDirtyState = true;
            this.mAttachInfo.mIgnoreDirtyState = true;
        }
        rect2.union(rect.left, rect.top, rect.right, rect.bottom);
        float f = this.mAttachInfo.mApplicationScale;
        boolean zIntersect = rect2.intersect(0, 0, (int) ((this.mWidth * f) + 0.5f), (int) ((this.mHeight * f) + 0.5f));
        if (!zIntersect) {
            rect2.setEmpty();
        }
        if (!this.mWillDrawSoon && (zIntersect || this.mIsAnimating)) {
            scheduleTraversals();
        }
        return null;
    }

    void setStopped(boolean z) {
        if (this.mStopped != z) {
            this.mStopped = z;
            if (z) {
                return;
            }
            scheduleTraversals();
        }
    }

    @Override // android.view.ViewParent
    public boolean getChildVisibleRect(View view, Rect rect, Point point) {
        if (view != this.mView) {
            throw new RuntimeException("child is not mine, honest!");
        }
        return rect.intersect(0, 0, this.mWidth, this.mHeight);
    }

    int getHostVisibility() {
        if (this.mAppVisible) {
            return this.mView.getVisibility();
        }
        return 8;
    }

    void disposeResizeBuffer() {
        if (this.mResizeBuffer == null || this.mAttachInfo.mHardwareRenderer == null) {
            return;
        }
        this.mAttachInfo.mHardwareRenderer.safelyRun(new Runnable() { // from class: android.view.ViewRootImpl.1
            @Override // java.lang.Runnable
            public void run() {
                ViewRootImpl.this.mResizeBuffer.destroy();
                ViewRootImpl.this.mResizeBuffer = null;
            }
        });
    }

    public void requestTransitionStart(LayoutTransition layoutTransition) {
        ArrayList<LayoutTransition> arrayList = this.mPendingTransitions;
        if (arrayList == null || !arrayList.contains(layoutTransition)) {
            if (this.mPendingTransitions == null) {
                this.mPendingTransitions = new ArrayList<>();
            }
            this.mPendingTransitions.add(layoutTransition);
        }
    }

    void scheduleTraversals() {
        if (this.mTraversalScheduled) {
            return;
        }
        this.mTraversalScheduled = true;
        this.mTraversalBarrier = this.mHandler.getLooper().postSyncBarrier();
        this.mChoreographer.postCallback(2, this.mTraversalRunnable, null);
        scheduleConsumeBatchedInput();
    }

    void unscheduleTraversals() {
        if (this.mTraversalScheduled) {
            this.mTraversalScheduled = false;
            this.mHandler.getLooper().removeSyncBarrier(this.mTraversalBarrier);
            this.mChoreographer.removeCallbacks(2, this.mTraversalRunnable, null);
        }
    }

    void doTraversal() {
        if (this.mTraversalScheduled) {
            this.mTraversalScheduled = false;
            this.mHandler.getLooper().removeSyncBarrier(this.mTraversalBarrier);
            if (this.mProfile) {
                Debug.startMethodTracing("ViewAncestor");
            }
            Trace.traceBegin(8L, "performTraversals");
            try {
                performTraversals();
                Trace.traceEnd(8L);
                if (this.mProfile) {
                    Debug.stopMethodTracing();
                    this.mProfile = false;
                }
            } catch (Throwable th) {
                Trace.traceEnd(8L);
                throw th;
            }
        }
    }

    private void applyKeepScreenOnFlag(WindowManager.LayoutParams layoutParams) {
        if (this.mAttachInfo.mKeepScreenOn) {
            layoutParams.flags |= 128;
        } else {
            layoutParams.flags = (layoutParams.flags & (-129)) | (this.mClientWindowLayoutFlags & 128);
        }
    }

    private boolean collectViewAttributes() {
        View.AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo.mRecomputeGlobalAttributes) {
            attachInfo.mRecomputeGlobalAttributes = false;
            boolean z = attachInfo.mKeepScreenOn;
            attachInfo.mKeepScreenOn = false;
            attachInfo.mSystemUiVisibility = 0;
            attachInfo.mHasSystemUiListeners = false;
            this.mView.dispatchCollectViewAttributes(attachInfo, 0);
            attachInfo.mSystemUiVisibility &= ~attachInfo.mDisabledSystemUiVisibility;
            WindowManager.LayoutParams layoutParams = this.mWindowAttributes;
            attachInfo.mSystemUiVisibility |= getImpliedSystemUiVisibility(layoutParams);
            if (attachInfo.mKeepScreenOn != z || attachInfo.mSystemUiVisibility != layoutParams.subtreeSystemUiVisibility || attachInfo.mHasSystemUiListeners != layoutParams.hasSystemUiListeners) {
                applyKeepScreenOnFlag(layoutParams);
                layoutParams.subtreeSystemUiVisibility = attachInfo.mSystemUiVisibility;
                layoutParams.hasSystemUiListeners = attachInfo.mHasSystemUiListeners;
                this.mView.dispatchWindowSystemUiVisiblityChanged(attachInfo.mSystemUiVisibility);
                return true;
            }
        }
        return false;
    }

    private int getImpliedSystemUiVisibility(WindowManager.LayoutParams layoutParams) {
        int i = (layoutParams.flags & 67108864) != 0 ? 1280 : 0;
        return (layoutParams.flags & 134217728) != 0 ? i | 768 : i;
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0055  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean measureHierarchy(android.view.View r6, android.view.WindowManager.LayoutParams r7, android.content.res.Resources r8, int r9, int r10) {
        /*
            r5 = this;
            int r0 = r7.width
            r1 = 0
            r2 = 1
            r3 = -2
            if (r0 != r3) goto L55
            android.util.DisplayMetrics r0 = r8.getDisplayMetrics()
            r3 = 17104903(0x1050007, float:2.4428262E-38)
            android.util.TypedValue r4 = r5.mTmpValue
            r8.getValue(r3, r4, r2)
            android.util.TypedValue r8 = r5.mTmpValue
            int r8 = r8.type
            r3 = 5
            if (r8 != r3) goto L22
            android.util.TypedValue r8 = r5.mTmpValue
            float r8 = r8.getDimension(r0)
            int r8 = (int) r8
            goto L23
        L22:
            r8 = r1
        L23:
            if (r8 == 0) goto L55
            if (r9 <= r8) goto L55
            int r0 = r7.width
            int r0 = getRootMeasureSpec(r8, r0)
            int r3 = r7.height
            int r3 = getRootMeasureSpec(r10, r3)
            r5.performMeasure(r0, r3)
            int r0 = r6.getMeasuredWidthAndState()
            r4 = 16777216(0x1000000, float:2.3509887E-38)
            r0 = r0 & r4
            if (r0 != 0) goto L41
        L3f:
            r8 = r2
            goto L56
        L41:
            int r8 = r8 + r9
            int r8 = r8 / 2
            int r0 = r7.width
            int r8 = getRootMeasureSpec(r8, r0)
            r5.performMeasure(r8, r3)
            int r8 = r6.getMeasuredWidthAndState()
            r8 = r8 & r4
            if (r8 != 0) goto L55
            goto L3f
        L55:
            r8 = r1
        L56:
            if (r8 != 0) goto L78
            int r8 = r7.width
            int r8 = getRootMeasureSpec(r9, r8)
            int r7 = r7.height
            int r7 = getRootMeasureSpec(r10, r7)
            r5.performMeasure(r8, r7)
            int r7 = r5.mWidth
            int r8 = r6.getMeasuredWidth()
            if (r7 != r8) goto L77
            int r7 = r5.mHeight
            int r6 = r6.getMeasuredHeight()
            if (r7 == r6) goto L78
        L77:
            r1 = r2
        L78:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.measureHierarchy(android.view.View, android.view.WindowManager$LayoutParams, android.content.res.Resources, int, int):boolean");
    }

    /* JADX WARN: Can't wrap try/catch for region: R(30:243|592|244|(1:600)|(10:594|246|247|604|248|249|602|250|251|252)(19:269|(2:271|(1:273))(0)|(2:468|471)(0)|(2:473|477)(0)|(0)|(0)|(0)|501|(0)(0)|514|515|(0)|525|(0)(0)|529|(0)(0)|(0)(0)|561|562)|274|(1:279)(1:278)|(1:281)(1:282)|283|(1:285)|286|598|(7:288|(2:290|291)(1:292)|293|596|294|295|(1:297))(0)|(0)(0)|(0)(0)|(0)|(0)|(0)|501|(0)(0)|514|515|(0)|525|(0)(0)|529|(0)(0)|(0)(0)|561|562) */
    /* JADX WARN: Can't wrap try/catch for region: R(78:7|(1:13)(1:12)|14|(1:16)(1:17)|18|(3:20|(1:22)(1:23)|24)(1:25)|26|(5:28|(1:34)(1:33)|35|(1:37)|38)(2:39|(1:45)(1:44))|(4:47|(1:51)|52|(1:54))|55|(1:60)(1:59)|61|(4:63|(1:65)(6:67|(1:69)|70|(1:72)|73|(4:79|(1:85)(1:84)|86|87)(1:78))|66|87)(1:88)|89|(1:91)|92|(1:94)(1:95)|96|(2:100|(6:102|(3:104|(2:106|612)(1:613)|107)|611|(1:109)|110|(1:112)(1:113))(0))(0)|(5:115|(1:119)|120|(1:122)(1:123)|124)|125|(2:127|(1:129)(1:130))(0)|(1:132)|(1:152)(1:151)|153|(1:159)(1:158)|160|(1:181)(2:165|(1:167)(21:168|(1:174)(1:173)|(3:176|(1:178)|179)|180|(1:471)(1:470)|(1:477)(1:476)|(2:479|(4:481|(1:483)|484|(3:486|572|487)))|(1:489)|(2:491|(5:495|(1:497)(1:498)|499|590|500))|501|(4:503|(1:507)|508|(1:510))(11:511|(1:513)|515|(2:521|(1:524))|525|(1:527)(1:528)|529|(1:534)(1:533)|(1:(1:551)(2:552|(4:556|(2:559|557)|610|560)))(1:(3:540|(4:544|(2:547|545)|609|548)|549))|561|562)|514|515|(4:517|519|521|(1:524))|525|(0)(0)|529|(1:534)(0)|(1:(0)(0))(0)|561|562))|(2:(1:187)(1:186)|188)(1:189)|190|(1:192)|193|586|194|581|195|(1:199)|200|(3:202|(1:204)(1:205)|206)|207|(1:209)(1:210)|211|212|583|213|(1:215)(1:216)|575|217|218|570|219|574|(3:221|(1:330)(30:243|592|244|600|(10:594|246|247|604|248|249|602|250|251|252)(19:269|(2:271|(1:273))(0)|(2:468|471)(0)|(2:473|477)(0)|(0)|(0)|(0)|501|(0)(0)|514|515|(0)|525|(0)(0)|529|(0)(0)|(0)(0)|561|562)|274|(1:279)(1:278)|(1:281)(1:282)|283|(1:285)|286|598|(7:288|(2:290|291)(1:292)|293|596|294|295|(1:297))(0)|(0)(0)|(0)(0)|(0)|(0)|(0)|501|(0)(0)|514|515|(0)|525|(0)(0)|529|(0)(0)|(0)(0)|561|562)|331)(1:332)|(1:334)|(1:342)|(1:344)|(22:346|(5:348|585|349|(28:588|351|352|391|(1:395)|396|(4:398|(1:400)|401|(3:(3:404|(3:406|(1:408)|606)|409)(1:410)|(2:412|(3:414|(1:416)|607))|417)(1:(6:419|(3:421|(1:423)|608)|424|577|425|426)))|430|(2:439|(1:441))|442|(2:465|466)(5:444|(1:446)(1:447)|448|(6:455|(1:457)(1:458)|459|(1:461)|(1:463)|464)(0)|466)|(0)(0)|(0)(0)|(0)|(0)|(0)|501|(0)(0)|514|515|(0)|525|(0)(0)|529|(0)(0)|(0)(0)|561|562)|356)|390|391|(8:393|395|396|(0)|430|(3:432|439|(0))|442|(0)(0))(0)|(0)(0)|(0)(0)|(0)|(0)|(0)|501|(0)(0)|514|515|(0)|525|(0)(0)|529|(0)(0)|(0)(0)|561|562)(2:357|(6:359|(1:361)|362|(1:364)|365|(1:369))(2:370|(3:376|579|377)))|382|390|391|(0)(0)|(0)(0)|(0)(0)|(0)|(0)|(0)|501|(0)(0)|514|515|(0)|525|(0)(0)|529|(0)(0)|(0)(0)|561|562) */
    /* JADX WARN: Code restructure failed: missing block: B:303:0x04af, code lost:
    
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:305:0x04b1, code lost:
    
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:383:0x05e8, code lost:
    
        r23 = r1;
        r18 = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:384:0x05ed, code lost:
    
        r23 = r1;
        r18 = r2;
        r21 = r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:385:0x05f3, code lost:
    
        r25 = r12;
        r24 = r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:386:0x05f8, code lost:
    
        r19 = r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:387:0x05fa, code lost:
    
        r18 = r2;
        r21 = r3;
        r25 = r12;
        r24 = r13;
        r0 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:388:0x0604, code lost:
    
        r18 = r2;
        r21 = r3;
        r25 = r12;
        r24 = r13;
        r0 = false;
        r19 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:389:0x060f, code lost:
    
        r23 = 0;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:113:0x021c  */
    /* JADX WARN: Removed duplicated region for block: B:130:0x0277  */
    /* JADX WARN: Removed duplicated region for block: B:273:0x042e A[Catch: all -> 0x04b3, OutOfMemoryError -> 0x04b6, TryCatch #23 {OutOfMemoryError -> 0x04b6, all -> 0x04b3, blocks: (B:252:0x03ed, B:274:0x0437, B:276:0x0445, B:281:0x0450, B:283:0x045e, B:285:0x0468, B:286:0x046b, B:288:0x0471, B:282:0x045c, B:269:0x0416, B:271:0x0424, B:273:0x042e), top: B:600:0x03dc }] */
    /* JADX WARN: Removed duplicated region for block: B:292:0x047f A[Catch: all -> 0x04af, OutOfMemoryError -> 0x04b1, TryCatch #24 {OutOfMemoryError -> 0x04b1, all -> 0x04af, blocks: (B:291:0x047b, B:293:0x0486, B:292:0x047f), top: B:598:0x046f }] */
    /* JADX WARN: Removed duplicated region for block: B:320:0x04d9 A[Catch: RemoteException -> 0x05e6, TryCatch #6 {RemoteException -> 0x05e6, blocks: (B:295:0x04a1, B:297:0x04a5, B:331:0x04f8, B:334:0x050c, B:336:0x0519, B:338:0x0521, B:340:0x0525, B:344:0x054c, B:346:0x0557, B:357:0x0587, B:359:0x058f, B:361:0x0593, B:362:0x0596, B:364:0x059f, B:365:0x05a2, B:367:0x05ab, B:369:0x05b5, B:370:0x05be, B:372:0x05c6, B:374:0x05ca, B:376:0x05d0, B:377:0x05d3, B:380:0x05e2, B:342:0x052d, B:324:0x04e3, B:326:0x04e7, B:328:0x04ec, B:329:0x04ef, B:318:0x04d5, B:320:0x04d9, B:322:0x04de), top: B:574:0x0386, inners: #14 }] */
    /* JADX WARN: Removed duplicated region for block: B:326:0x04e7 A[Catch: RemoteException -> 0x05e6, TryCatch #6 {RemoteException -> 0x05e6, blocks: (B:295:0x04a1, B:297:0x04a5, B:331:0x04f8, B:334:0x050c, B:336:0x0519, B:338:0x0521, B:340:0x0525, B:344:0x054c, B:346:0x0557, B:357:0x0587, B:359:0x058f, B:361:0x0593, B:362:0x0596, B:364:0x059f, B:365:0x05a2, B:367:0x05ab, B:369:0x05b5, B:370:0x05be, B:372:0x05c6, B:374:0x05ca, B:376:0x05d0, B:377:0x05d3, B:380:0x05e2, B:342:0x052d, B:324:0x04e3, B:326:0x04e7, B:328:0x04ec, B:329:0x04ef, B:318:0x04d5, B:320:0x04d9, B:322:0x04de), top: B:574:0x0386, inners: #14 }] */
    /* JADX WARN: Removed duplicated region for block: B:334:0x050c A[Catch: RemoteException -> 0x05e6, TryCatch #6 {RemoteException -> 0x05e6, blocks: (B:295:0x04a1, B:297:0x04a5, B:331:0x04f8, B:334:0x050c, B:336:0x0519, B:338:0x0521, B:340:0x0525, B:344:0x054c, B:346:0x0557, B:357:0x0587, B:359:0x058f, B:361:0x0593, B:362:0x0596, B:364:0x059f, B:365:0x05a2, B:367:0x05ab, B:369:0x05b5, B:370:0x05be, B:372:0x05c6, B:374:0x05ca, B:376:0x05d0, B:377:0x05d3, B:380:0x05e2, B:342:0x052d, B:324:0x04e3, B:326:0x04e7, B:328:0x04ec, B:329:0x04ef, B:318:0x04d5, B:320:0x04d9, B:322:0x04de), top: B:574:0x0386, inners: #14 }] */
    /* JADX WARN: Removed duplicated region for block: B:336:0x0519 A[Catch: RemoteException -> 0x05e6, TryCatch #6 {RemoteException -> 0x05e6, blocks: (B:295:0x04a1, B:297:0x04a5, B:331:0x04f8, B:334:0x050c, B:336:0x0519, B:338:0x0521, B:340:0x0525, B:344:0x054c, B:346:0x0557, B:357:0x0587, B:359:0x058f, B:361:0x0593, B:362:0x0596, B:364:0x059f, B:365:0x05a2, B:367:0x05ab, B:369:0x05b5, B:370:0x05be, B:372:0x05c6, B:374:0x05ca, B:376:0x05d0, B:377:0x05d3, B:380:0x05e2, B:342:0x052d, B:324:0x04e3, B:326:0x04e7, B:328:0x04ec, B:329:0x04ef, B:318:0x04d5, B:320:0x04d9, B:322:0x04de), top: B:574:0x0386, inners: #14 }] */
    /* JADX WARN: Removed duplicated region for block: B:342:0x052d A[Catch: RemoteException -> 0x05e6, TryCatch #6 {RemoteException -> 0x05e6, blocks: (B:295:0x04a1, B:297:0x04a5, B:331:0x04f8, B:334:0x050c, B:336:0x0519, B:338:0x0521, B:340:0x0525, B:344:0x054c, B:346:0x0557, B:357:0x0587, B:359:0x058f, B:361:0x0593, B:362:0x0596, B:364:0x059f, B:365:0x05a2, B:367:0x05ab, B:369:0x05b5, B:370:0x05be, B:372:0x05c6, B:374:0x05ca, B:376:0x05d0, B:377:0x05d3, B:380:0x05e2, B:342:0x052d, B:324:0x04e3, B:326:0x04e7, B:328:0x04ec, B:329:0x04ef, B:318:0x04d5, B:320:0x04d9, B:322:0x04de), top: B:574:0x0386, inners: #14 }] */
    /* JADX WARN: Removed duplicated region for block: B:344:0x054c A[Catch: RemoteException -> 0x05e6, TryCatch #6 {RemoteException -> 0x05e6, blocks: (B:295:0x04a1, B:297:0x04a5, B:331:0x04f8, B:334:0x050c, B:336:0x0519, B:338:0x0521, B:340:0x0525, B:344:0x054c, B:346:0x0557, B:357:0x0587, B:359:0x058f, B:361:0x0593, B:362:0x0596, B:364:0x059f, B:365:0x05a2, B:367:0x05ab, B:369:0x05b5, B:370:0x05be, B:372:0x05c6, B:374:0x05ca, B:376:0x05d0, B:377:0x05d3, B:380:0x05e2, B:342:0x052d, B:324:0x04e3, B:326:0x04e7, B:328:0x04ec, B:329:0x04ef, B:318:0x04d5, B:320:0x04d9, B:322:0x04de), top: B:574:0x0386, inners: #14 }] */
    /* JADX WARN: Removed duplicated region for block: B:346:0x0557 A[Catch: RemoteException -> 0x05e6, TRY_LEAVE, TryCatch #6 {RemoteException -> 0x05e6, blocks: (B:295:0x04a1, B:297:0x04a5, B:331:0x04f8, B:334:0x050c, B:336:0x0519, B:338:0x0521, B:340:0x0525, B:344:0x054c, B:346:0x0557, B:357:0x0587, B:359:0x058f, B:361:0x0593, B:362:0x0596, B:364:0x059f, B:365:0x05a2, B:367:0x05ab, B:369:0x05b5, B:370:0x05be, B:372:0x05c6, B:374:0x05ca, B:376:0x05d0, B:377:0x05d3, B:380:0x05e2, B:342:0x052d, B:324:0x04e3, B:326:0x04e7, B:328:0x04ec, B:329:0x04ef, B:318:0x04d5, B:320:0x04d9, B:322:0x04de), top: B:574:0x0386, inners: #14 }] */
    /* JADX WARN: Removed duplicated region for block: B:357:0x0587 A[Catch: RemoteException -> 0x05e6, TRY_ENTER, TryCatch #6 {RemoteException -> 0x05e6, blocks: (B:295:0x04a1, B:297:0x04a5, B:331:0x04f8, B:334:0x050c, B:336:0x0519, B:338:0x0521, B:340:0x0525, B:344:0x054c, B:346:0x0557, B:357:0x0587, B:359:0x058f, B:361:0x0593, B:362:0x0596, B:364:0x059f, B:365:0x05a2, B:367:0x05ab, B:369:0x05b5, B:370:0x05be, B:372:0x05c6, B:374:0x05ca, B:376:0x05d0, B:377:0x05d3, B:380:0x05e2, B:342:0x052d, B:324:0x04e3, B:326:0x04e7, B:328:0x04ec, B:329:0x04ef, B:318:0x04d5, B:320:0x04d9, B:322:0x04de), top: B:574:0x0386, inners: #14 }] */
    /* JADX WARN: Removed duplicated region for block: B:393:0x0622  */
    /* JADX WARN: Removed duplicated region for block: B:395:0x062a  */
    /* JADX WARN: Removed duplicated region for block: B:398:0x063a  */
    /* JADX WARN: Removed duplicated region for block: B:441:0x0736  */
    /* JADX WARN: Removed duplicated region for block: B:444:0x074a  */
    /* JADX WARN: Removed duplicated region for block: B:465:0x07b9  */
    /* JADX WARN: Removed duplicated region for block: B:468:0x07bf  */
    /* JADX WARN: Removed duplicated region for block: B:471:0x07c5  */
    /* JADX WARN: Removed duplicated region for block: B:473:0x07c8  */
    /* JADX WARN: Removed duplicated region for block: B:477:0x07cf  */
    /* JADX WARN: Removed duplicated region for block: B:479:0x07d2  */
    /* JADX WARN: Removed duplicated region for block: B:489:0x082c  */
    /* JADX WARN: Removed duplicated region for block: B:491:0x0836  */
    /* JADX WARN: Removed duplicated region for block: B:503:0x0891  */
    /* JADX WARN: Removed duplicated region for block: B:511:0x08a9  */
    /* JADX WARN: Removed duplicated region for block: B:517:0x08c1  */
    /* JADX WARN: Removed duplicated region for block: B:527:0x0904  */
    /* JADX WARN: Removed duplicated region for block: B:528:0x0908  */
    /* JADX WARN: Removed duplicated region for block: B:531:0x0911 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:534:0x0916  */
    /* JADX WARN: Removed duplicated region for block: B:536:0x0919 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:550:0x094b  */
    /* JADX WARN: Removed duplicated region for block: B:551:0x094d  */
    /* JADX WARN: Removed duplicated region for block: B:552:0x0951  */
    /* JADX WARN: Type inference failed for: r1v143 */
    /* JADX WARN: Type inference failed for: r1v21 */
    /* JADX WARN: Type inference failed for: r1v34, types: [android.view.WindowManager$LayoutParams] */
    /* JADX WARN: Type inference failed for: r1v96 */
    /* JADX WARN: Type inference failed for: r23v0 */
    /* JADX WARN: Type inference failed for: r23v1 */
    /* JADX WARN: Type inference failed for: r23v10 */
    /* JADX WARN: Type inference failed for: r23v11 */
    /* JADX WARN: Type inference failed for: r23v12 */
    /* JADX WARN: Type inference failed for: r23v13 */
    /* JADX WARN: Type inference failed for: r23v14 */
    /* JADX WARN: Type inference failed for: r23v15 */
    /* JADX WARN: Type inference failed for: r23v16 */
    /* JADX WARN: Type inference failed for: r23v18 */
    /* JADX WARN: Type inference failed for: r23v19 */
    /* JADX WARN: Type inference failed for: r23v20 */
    /* JADX WARN: Type inference failed for: r23v21 */
    /* JADX WARN: Type inference failed for: r23v22 */
    /* JADX WARN: Type inference failed for: r23v24 */
    /* JADX WARN: Type inference failed for: r23v25 */
    /* JADX WARN: Type inference failed for: r23v26 */
    /* JADX WARN: Type inference failed for: r23v27 */
    /* JADX WARN: Type inference failed for: r23v29 */
    /* JADX WARN: Type inference failed for: r23v3 */
    /* JADX WARN: Type inference failed for: r23v30 */
    /* JADX WARN: Type inference failed for: r23v31 */
    /* JADX WARN: Type inference failed for: r23v32 */
    /* JADX WARN: Type inference failed for: r23v33 */
    /* JADX WARN: Type inference failed for: r23v34 */
    /* JADX WARN: Type inference failed for: r23v35 */
    /* JADX WARN: Type inference failed for: r23v36 */
    /* JADX WARN: Type inference failed for: r23v37 */
    /* JADX WARN: Type inference failed for: r23v38 */
    /* JADX WARN: Type inference failed for: r23v39 */
    /* JADX WARN: Type inference failed for: r23v4 */
    /* JADX WARN: Type inference failed for: r23v40 */
    /* JADX WARN: Type inference failed for: r23v41 */
    /* JADX WARN: Type inference failed for: r23v42 */
    /* JADX WARN: Type inference failed for: r23v43 */
    /* JADX WARN: Type inference failed for: r23v44 */
    /* JADX WARN: Type inference failed for: r23v45 */
    /* JADX WARN: Type inference failed for: r23v5 */
    /* JADX WARN: Type inference failed for: r23v6 */
    /* JADX WARN: Type inference failed for: r23v7 */
    /* JADX WARN: Type inference failed for: r23v8 */
    /* JADX WARN: Type inference failed for: r23v9 */
    /* JADX WARN: Type inference failed for: r32v0, types: [android.view.ViewRootImpl] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void performTraversals() throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 2427
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.performTraversals():void");
    }

    private void handleOutOfResourcesException(Surface.OutOfResourcesException outOfResourcesException) {
        Log.e(TAG, "OutOfResourcesException initializing HW surface", outOfResourcesException);
        try {
            if (!this.mWindowSession.outOfMemory(this.mWindow) && Process.myUid() != 1000) {
                Slog.w(TAG, "No processes killed for memory; killing self");
                Process.killProcess(Process.myPid());
            }
        } catch (RemoteException unused) {
        }
        this.mLayoutRequested = true;
    }

    private void performMeasure(int i, int i2) {
        Trace.traceBegin(8L, "measure");
        try {
            this.mView.measure(i, i2);
        } finally {
            Trace.traceEnd(8L);
        }
    }

    boolean isInLayout() {
        return this.mInLayout;
    }

    boolean requestLayoutDuringLayout(View view) {
        if (view.mParent == null || view.mAttachInfo == null) {
            return true;
        }
        if (!this.mLayoutRequesters.contains(view)) {
            this.mLayoutRequesters.add(view);
        }
        return !this.mHandlingLayoutInLayoutRequest;
    }

    private void performLayout(WindowManager.LayoutParams layoutParams, int i, int i2) {
        ArrayList<View> validLayoutRequesters;
        this.mLayoutRequested = false;
        this.mScrollMayChange = true;
        this.mInLayout = true;
        View view = this.mView;
        Trace.traceBegin(8L, "layout");
        try {
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            this.mInLayout = false;
            if (this.mLayoutRequesters.size() > 0 && (validLayoutRequesters = getValidLayoutRequesters(this.mLayoutRequesters, false)) != null) {
                this.mHandlingLayoutInLayoutRequest = true;
                int size = validLayoutRequesters.size();
                for (int i3 = 0; i3 < size; i3++) {
                    View view2 = validLayoutRequesters.get(i3);
                    Log.w("View", "requestLayout() improperly called by " + view2 + " during layout: running second layout pass");
                    view2.requestLayout();
                }
                measureHierarchy(view, layoutParams, this.mView.getContext().getResources(), i, i2);
                this.mInLayout = true;
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                this.mHandlingLayoutInLayoutRequest = false;
                final ArrayList<View> validLayoutRequesters2 = getValidLayoutRequesters(this.mLayoutRequesters, true);
                if (validLayoutRequesters2 != null) {
                    getRunQueue().post(new Runnable() { // from class: android.view.ViewRootImpl.2
                        @Override // java.lang.Runnable
                        public void run() {
                            int size2 = validLayoutRequesters2.size();
                            for (int i4 = 0; i4 < size2; i4++) {
                                View view3 = (View) validLayoutRequesters2.get(i4);
                                Log.w("View", "requestLayout() improperly called by " + view3 + " during second layout pass: posting in next frame");
                                view3.requestLayout();
                            }
                        }
                    });
                }
            }
            Trace.traceEnd(8L);
            this.mInLayout = false;
        } catch (Throwable th) {
            Trace.traceEnd(8L);
            throw th;
        }
    }

    private ArrayList<View> getValidLayoutRequesters(ArrayList<View> arrayList, boolean z) {
        boolean z2;
        int size = arrayList.size();
        ArrayList<View> arrayList2 = null;
        for (int i = 0; i < size; i++) {
            View view = arrayList.get(i);
            if (view != null && view.mAttachInfo != null && view.mParent != null && (z || (view.mPrivateFlags & 4096) == 4096)) {
                View view2 = view;
                while (true) {
                    if (view2 == null) {
                        z2 = false;
                        break;
                    }
                    if ((view2.mViewFlags & 12) == 8) {
                        z2 = true;
                        break;
                    }
                    view2 = view2.mParent instanceof View ? (View) view2.mParent : null;
                }
                if (!z2) {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList<>();
                    }
                    arrayList2.add(view);
                }
            }
        }
        if (!z) {
            for (int i2 = 0; i2 < size; i2++) {
                View view3 = arrayList.get(i2);
                while (view3 != null && (view3.mPrivateFlags & 4096) != 0) {
                    view3.mPrivateFlags &= -4097;
                    view3 = view3.mParent instanceof View ? (View) view3.mParent : null;
                }
            }
        }
        arrayList.clear();
        return arrayList2;
    }

    @Override // android.view.ViewParent
    public void requestTransparentRegion(View view) {
        checkThread();
        View view2 = this.mView;
        if (view2 == view) {
            view2.mPrivateFlags |= 512;
            this.mWindowAttributesChanged = true;
            this.mWindowAttributesChangesFlag = 0;
            requestLayout();
        }
    }

    private static int getRootMeasureSpec(int i, int i2) {
        if (i2 == -2) {
            return View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE);
        }
        if (i2 == -1) {
            return View.MeasureSpec.makeMeasureSpec(i, 1073741824);
        }
        return View.MeasureSpec.makeMeasureSpec(i2, 1073741824);
    }

    @Override // android.view.HardwareRenderer.HardwareDrawCallbacks
    public void onHardwarePreDraw(HardwareCanvas hardwareCanvas) {
        hardwareCanvas.translate(0.0f, -this.mHardwareYOffset);
    }

    @Override // android.view.HardwareRenderer.HardwareDrawCallbacks
    public void onHardwarePostDraw(HardwareCanvas hardwareCanvas) {
        if (this.mResizeBuffer != null) {
            this.mResizePaint.setAlpha(this.mResizeAlpha);
            hardwareCanvas.drawHardwareLayer(this.mResizeBuffer, 0.0f, this.mHardwareYOffset, this.mResizePaint);
        }
        drawAccessibilityFocusedDrawableIfNeeded(hardwareCanvas);
    }

    void outputDisplayList(View view) {
        DisplayList displayList;
        View.AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null || attachInfo.mHardwareCanvas == null || (displayList = view.getDisplayList()) == null) {
            return;
        }
        this.mAttachInfo.mHardwareCanvas.outputDisplayList(displayList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void profileRendering(boolean z) {
        if (this.mProfileRendering) {
            this.mRenderProfilingEnabled = z;
            Choreographer.FrameCallback frameCallback = this.mRenderProfiler;
            if (frameCallback != null) {
                this.mChoreographer.removeFrameCallback(frameCallback);
            }
            if (this.mRenderProfilingEnabled) {
                if (this.mRenderProfiler == null) {
                    this.mRenderProfiler = new Choreographer.FrameCallback() { // from class: android.view.ViewRootImpl.3
                        @Override // android.view.Choreographer.FrameCallback
                        public void doFrame(long j) {
                            ViewRootImpl.this.mDirty.set(0, 0, ViewRootImpl.this.mWidth, ViewRootImpl.this.mHeight);
                            ViewRootImpl.this.scheduleTraversals();
                            if (ViewRootImpl.this.mRenderProfilingEnabled) {
                                ViewRootImpl.this.mChoreographer.postFrameCallback(ViewRootImpl.this.mRenderProfiler);
                            }
                        }
                    };
                }
                this.mChoreographer.postFrameCallback(this.mRenderProfiler);
                return;
            }
            this.mRenderProfiler = null;
        }
    }

    private void trackFPS() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (this.mFpsStartTime < 0) {
            this.mFpsPrevTime = jCurrentTimeMillis;
            this.mFpsStartTime = jCurrentTimeMillis;
            this.mFpsNumFrames = 0;
            return;
        }
        this.mFpsNumFrames++;
        String hexString = Integer.toHexString(System.identityHashCode(this));
        long j = jCurrentTimeMillis - this.mFpsPrevTime;
        long j2 = jCurrentTimeMillis - this.mFpsStartTime;
        Log.v(TAG, "0x" + hexString + "\tFrame time:\t" + j);
        this.mFpsPrevTime = jCurrentTimeMillis;
        if (j2 > 1000) {
            Log.v(TAG, "0x" + hexString + "\tFPS:\t" + ((this.mFpsNumFrames * 1000.0f) / j2));
            this.mFpsStartTime = jCurrentTimeMillis;
            this.mFpsNumFrames = 0;
        }
    }

    private void performDraw() {
        if (this.mAttachInfo.mScreenOn || this.mReportNextDraw) {
            boolean z = this.mFullRedrawNeeded;
            this.mFullRedrawNeeded = false;
            boolean z2 = this.mUpdateTranformHint;
            this.mUpdateTranformHint = false;
            this.mIsDrawing = true;
            Trace.traceBegin(8L, "draw");
            try {
                draw(z, z2);
                this.mIsDrawing = false;
                Trace.traceEnd(8L);
                if (this.mReportNextDraw) {
                    this.mReportNextDraw = false;
                    if (this.mSurfaceHolder != null && this.mSurface.isValid()) {
                        this.mSurfaceHolderCallback.surfaceRedrawNeeded(this.mSurfaceHolder);
                        SurfaceHolder.Callback[] callbacks = this.mSurfaceHolder.getCallbacks();
                        if (callbacks != null) {
                            for (SurfaceHolder.Callback callback : callbacks) {
                                if (callback instanceof SurfaceHolder.Callback2) {
                                    ((SurfaceHolder.Callback2) callback).surfaceRedrawNeeded(this.mSurfaceHolder);
                                }
                            }
                        }
                    }
                    try {
                        this.mWindowSession.finishDrawing(this.mWindow);
                    } catch (RemoteException unused) {
                    }
                }
            } catch (Throwable th) {
                this.mIsDrawing = false;
                Trace.traceEnd(8L);
                throw th;
            }
        }
    }

    private void draw(boolean z, boolean z2) {
        int currY;
        boolean z3;
        boolean z4;
        int interpolation;
        Surface surface = this.mSurface;
        if (surface.isValid()) {
            if (!sFirstDrawComplete) {
                ArrayList<Runnable> arrayList = sFirstDrawHandlers;
                synchronized (arrayList) {
                    sFirstDrawComplete = true;
                    int size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        this.mHandler.post(sFirstDrawHandlers.get(i));
                    }
                }
            }
            scrollToRectOrFocus(null, false);
            View.AttachInfo attachInfo = this.mAttachInfo;
            if (attachInfo.mViewScrollChanged) {
                attachInfo.mViewScrollChanged = false;
                attachInfo.mTreeObserver.dispatchOnScrollChanged();
            }
            Scroller scroller = this.mScroller;
            boolean z5 = scroller != null && scroller.computeScrollOffset();
            if (z5) {
                currY = this.mScroller.getCurrY();
            } else {
                currY = this.mScrollY;
            }
            if (this.mCurScrollY != currY) {
                this.mCurScrollY = currY;
                z3 = true;
            } else {
                z3 = z;
            }
            float f = attachInfo.mApplicationScale;
            boolean z6 = attachInfo.mScalingRequired;
            if (this.mResizeBuffer != null) {
                long jUptimeMillis = SystemClock.uptimeMillis() - this.mResizeBufferStartTime;
                int i2 = this.mResizeBufferDuration;
                if (jUptimeMillis < i2) {
                    interpolation = 255 - ((int) (mResizeInterpolator.getInterpolation(jUptimeMillis / i2) * 255.0f));
                    z4 = true;
                } else {
                    disposeResizeBuffer();
                    z4 = z5;
                    interpolation = 0;
                }
            } else {
                z4 = z5;
                interpolation = 0;
            }
            Rect rect = this.mDirty;
            if (this.mSurfaceHolder != null) {
                rect.setEmpty();
                if (z4) {
                    Scroller scroller2 = this.mScroller;
                    if (scroller2 != null) {
                        scroller2.abortAnimation();
                    }
                    disposeResizeBuffer();
                    return;
                }
                return;
            }
            if (z3) {
                attachInfo.mIgnoreDirtyState = true;
                rect.set(0, 0, (int) ((this.mWidth * f) + 0.5f), (int) ((this.mHeight * f) + 0.5f));
            }
            invalidateDisplayLists();
            attachInfo.mTreeObserver.dispatchOnDraw();
            if (!rect.isEmpty() || this.mIsAnimating) {
                if (attachInfo.mHardwareRenderer != null && attachInfo.mHardwareRenderer.isEnabled()) {
                    this.mIsAnimating = false;
                    this.mHardwareYOffset = currY;
                    this.mResizeAlpha = interpolation;
                    this.mCurrentDirty.set(rect);
                    rect.setEmpty();
                    attachInfo.mHardwareRenderer.draw(this.mView, attachInfo, this, z4 ? null : this.mCurrentDirty);
                } else {
                    if (attachInfo.mHardwareRenderer != null && !attachInfo.mHardwareRenderer.isEnabled() && attachInfo.mHardwareRenderer.isRequested()) {
                        try {
                            attachInfo.mHardwareRenderer.initializeIfNeeded(this.mWidth, this.mHeight, this.mHolder.getSurface());
                            this.mFullRedrawNeeded = true;
                            scheduleTraversals();
                            return;
                        } catch (Surface.OutOfResourcesException e) {
                            handleOutOfResourcesException(e);
                            return;
                        }
                    }
                    if (!drawSoftware(surface, attachInfo, currY, z6, rect)) {
                        return;
                    }
                }
            }
            if (z4) {
                this.mFullRedrawNeeded = true;
                scheduleTraversals();
            }
            if (z2) {
                this.mFullRedrawNeeded = true;
                scheduleTraversals();
            }
        }
    }

    private boolean drawSoftware(Surface surface, View.AttachInfo attachInfo, int i, boolean z, Rect rect) {
        try {
            int i2 = rect.left;
            int i3 = rect.top;
            int i4 = rect.right;
            int i5 = rect.bottom;
            Canvas canvasLockCanvas = this.mSurface.lockCanvas(rect);
            if (i2 != rect.left || i3 != rect.top || i4 != rect.right || i5 != rect.bottom) {
                attachInfo.mIgnoreDirtyState = true;
            }
            canvasLockCanvas.setDensity(this.mDensity);
            try {
                if (!canvasLockCanvas.isOpaque() || i != 0) {
                    canvasLockCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                }
                rect.setEmpty();
                this.mIsAnimating = false;
                attachInfo.mDrawingTime = SystemClock.uptimeMillis();
                this.mView.mPrivateFlags |= 32;
                try {
                    canvasLockCanvas.translate(0.0f, -i);
                    CompatibilityInfo.Translator translator = this.mTranslator;
                    if (translator != null) {
                        translator.translateCanvas(canvasLockCanvas);
                    }
                    canvasLockCanvas.setScreenDensity(z ? this.mNoncompatDensity : 0);
                    attachInfo.mSetIgnoreDirtyState = false;
                    this.mView.draw(canvasLockCanvas);
                    drawAccessibilityFocusedDrawableIfNeeded(canvasLockCanvas);
                    try {
                        surface.unlockCanvasAndPost(canvasLockCanvas);
                        return true;
                    } catch (IllegalArgumentException e) {
                        Log.e(TAG, "Could not unlock surface", e);
                        this.mLayoutRequested = true;
                        return false;
                    }
                } finally {
                    if (!attachInfo.mSetIgnoreDirtyState) {
                        attachInfo.mIgnoreDirtyState = false;
                    }
                }
            } catch (Throwable th) {
                try {
                    surface.unlockCanvasAndPost(canvasLockCanvas);
                    throw th;
                } catch (IllegalArgumentException e2) {
                    Log.e(TAG, "Could not unlock surface", e2);
                    this.mLayoutRequested = true;
                    return false;
                }
            }
        } catch (Surface.OutOfResourcesException e3) {
            handleOutOfResourcesException(e3);
            return false;
        } catch (IllegalArgumentException e4) {
            Log.e(TAG, "Could not lock surface", e4);
            this.mLayoutRequested = true;
            return false;
        }
    }

    private void drawAccessibilityFocusedDrawableIfNeeded(Canvas canvas) {
        View view;
        Drawable accessibilityFocusedDrawable;
        AccessibilityManager accessibilityManager = AccessibilityManager.getInstance(this.mView.mContext);
        if (!accessibilityManager.isEnabled() || !accessibilityManager.isTouchExplorationEnabled() || (view = this.mAccessibilityFocusedHost) == null || view.mAttachInfo == null || (accessibilityFocusedDrawable = getAccessibilityFocusedDrawable()) == null) {
            return;
        }
        AccessibilityNodeProvider accessibilityNodeProvider = this.mAccessibilityFocusedHost.getAccessibilityNodeProvider();
        Rect rect = this.mView.mAttachInfo.mTmpInvalRect;
        if (accessibilityNodeProvider == null) {
            this.mAccessibilityFocusedHost.getBoundsOnScreen(rect);
        } else {
            AccessibilityNodeInfo accessibilityNodeInfo = this.mAccessibilityFocusedVirtualView;
            if (accessibilityNodeInfo == null) {
                return;
            } else {
                accessibilityNodeInfo.getBoundsInScreen(rect);
            }
        }
        rect.offset(-this.mAttachInfo.mWindowLeft, -this.mAttachInfo.mWindowTop);
        rect.intersect(0, 0, this.mAttachInfo.mViewRootImpl.mWidth, this.mAttachInfo.mViewRootImpl.mHeight);
        accessibilityFocusedDrawable.setBounds(rect);
        accessibilityFocusedDrawable.draw(canvas);
    }

    private Drawable getAccessibilityFocusedDrawable() {
        View.AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            return null;
        }
        if (attachInfo.mAccessibilityFocusDrawable == null) {
            TypedValue typedValue = new TypedValue();
            if (this.mView.mContext.getTheme().resolveAttribute(16843809, typedValue, true)) {
                this.mAttachInfo.mAccessibilityFocusDrawable = this.mView.mContext.getResources().getDrawable(typedValue.resourceId);
            }
        }
        return this.mAttachInfo.mAccessibilityFocusDrawable;
    }

    void invalidateDisplayLists() {
        ArrayList<DisplayList> arrayList = this.mDisplayLists;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            DisplayList displayList = arrayList.get(i);
            if (displayList.isDirty()) {
                displayList.reset();
            }
        }
        arrayList.clear();
    }

    public void setDrawDuringWindowsAnimating(boolean z) {
        this.mDrawDuringWindowsAnimating = z;
        if (z) {
            handleDispatchDoneAnimating();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x00c8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    boolean scrollToRectOrFocus(android.graphics.Rect r7, boolean r8) {
        /*
            Method dump skipped, instruction units count: 248
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.scrollToRectOrFocus(android.graphics.Rect, boolean):boolean");
    }

    public View getAccessibilityFocusedHost() {
        return this.mAccessibilityFocusedHost;
    }

    public AccessibilityNodeInfo getAccessibilityFocusedVirtualView() {
        return this.mAccessibilityFocusedVirtualView;
    }

    void setAccessibilityFocus(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
        AccessibilityNodeInfo accessibilityNodeInfo2 = this.mAccessibilityFocusedVirtualView;
        if (accessibilityNodeInfo2 != null) {
            View view2 = this.mAccessibilityFocusedHost;
            this.mAccessibilityFocusedHost = null;
            this.mAccessibilityFocusedVirtualView = null;
            view2.clearAccessibilityFocusNoCallbacks();
            AccessibilityNodeProvider accessibilityNodeProvider = view2.getAccessibilityNodeProvider();
            if (accessibilityNodeProvider != null) {
                accessibilityNodeInfo2.getBoundsInParent(this.mTempRect);
                view2.invalidate(this.mTempRect);
                accessibilityNodeProvider.performAction(AccessibilityNodeInfo.getVirtualDescendantId(accessibilityNodeInfo2.getSourceNodeId()), 128, null);
            }
            accessibilityNodeInfo2.recycle();
        }
        View view3 = this.mAccessibilityFocusedHost;
        if (view3 != null) {
            view3.clearAccessibilityFocusNoCallbacks();
        }
        this.mAccessibilityFocusedHost = view;
        this.mAccessibilityFocusedVirtualView = accessibilityNodeInfo;
    }

    @Override // android.view.ViewParent
    public void requestChildFocus(View view, View view2) {
        checkThread();
        scheduleTraversals();
    }

    @Override // android.view.ViewParent
    public void clearChildFocus(View view) {
        checkThread();
        scheduleTraversals();
    }

    @Override // android.view.ViewParent
    public void focusableViewAvailable(View view) {
        checkThread();
        View view2 = this.mView;
        if (view2 != null) {
            if (!view2.hasFocus()) {
                view.requestFocus();
                return;
            }
            View viewFindFocus = this.mView.findFocus();
            if ((viewFindFocus instanceof ViewGroup) && ((ViewGroup) viewFindFocus).getDescendantFocusability() == 262144 && isViewDescendantOf(view, viewFindFocus)) {
                view.requestFocus();
            }
        }
    }

    @Override // android.view.ViewParent
    public void recomputeViewAttributes(View view) {
        checkThread();
        if (this.mView == view) {
            this.mAttachInfo.mRecomputeGlobalAttributes = true;
            if (this.mWillDrawSoon) {
                return;
            }
            scheduleTraversals();
        }
    }

    void dispatchDetachedFromWindow() {
        InputQueue inputQueue;
        View view = this.mView;
        if (view != null && view.mAttachInfo != null) {
            if (this.mAttachInfo.mHardwareRenderer != null && this.mAttachInfo.mHardwareRenderer.isEnabled()) {
                this.mAttachInfo.mHardwareRenderer.validate();
            }
            this.mAttachInfo.mTreeObserver.dispatchOnWindowAttachedChange(false);
            this.mView.dispatchDetachedFromWindow();
        }
        this.mAccessibilityInteractionConnectionManager.ensureNoConnection();
        this.mAccessibilityManager.removeAccessibilityStateChangeListener(this.mAccessibilityInteractionConnectionManager);
        removeSendWindowContentChangedCallback();
        destroyHardwareRenderer();
        setAccessibilityFocus(null, null);
        this.mView.assignParent(null);
        this.mView = null;
        this.mAttachInfo.mRootView = null;
        this.mAttachInfo.mSurface = null;
        this.mSurface.release();
        InputQueue.Callback callback = this.mInputQueueCallback;
        if (callback != null && (inputQueue = this.mInputQueue) != null) {
            callback.onInputQueueDestroyed(inputQueue);
            this.mInputQueue.dispose();
            this.mInputQueueCallback = null;
            this.mInputQueue = null;
        }
        WindowInputEventReceiver windowInputEventReceiver = this.mInputEventReceiver;
        if (windowInputEventReceiver != null) {
            windowInputEventReceiver.dispose();
            this.mInputEventReceiver = null;
        }
        try {
            this.mWindowSession.remove(this.mWindow);
        } catch (RemoteException unused) {
        }
        InputChannel inputChannel = this.mInputChannel;
        if (inputChannel != null) {
            inputChannel.dispose();
            this.mInputChannel = null;
        }
        unscheduleTraversals();
    }

    void updateConfiguration(Configuration configuration, boolean z) {
        CompatibilityInfo compatibilityInfo = this.mDisplayAdjustments.getCompatibilityInfo();
        if (!compatibilityInfo.equals(CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO)) {
            Configuration configuration2 = new Configuration(configuration);
            compatibilityInfo.applyToConfiguration(this.mNoncompatDensity, configuration2);
            configuration = configuration2;
        }
        ArrayList<ComponentCallbacks> arrayList = sConfigCallbacks;
        synchronized (arrayList) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                sConfigCallbacks.get(size).onConfigurationChanged(configuration);
            }
        }
        View view = this.mView;
        if (view != null) {
            Configuration configuration3 = view.getResources().getConfiguration();
            if (z || this.mLastConfiguration.diff(configuration3) != 0) {
                int layoutDirection = this.mLastConfiguration.getLayoutDirection();
                int layoutDirection2 = configuration3.getLayoutDirection();
                this.mLastConfiguration.setTo(configuration3);
                if (layoutDirection != layoutDirection2 && this.mViewLayoutDirectionInitial == 2) {
                    this.mView.setLayoutDirection(layoutDirection2);
                }
                this.mView.dispatchConfigurationChanged(configuration3);
            }
        }
        this.mFlipControllerFallbackKeys = this.mContext.getResources().getBoolean(17891339);
    }

    public static boolean isViewDescendantOf(View view, View view2) {
        if (view == view2) {
            return true;
        }
        Object parent = view.getParent();
        return (parent instanceof ViewGroup) && isViewDescendantOf((View) parent, view2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void forceLayout(View view) {
        view.forceLayout();
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                forceLayout(viewGroup.getChildAt(i));
            }
        }
    }

    final class ViewRootHandler extends Handler {
        ViewRootHandler() {
        }

        @Override // android.os.Handler
        public String getMessageName(Message message) {
            switch (message.what) {
                case 1:
                    return "MSG_INVALIDATE";
                case 2:
                    return "MSG_INVALIDATE_RECT";
                case 3:
                    return "MSG_DIE";
                case 4:
                    return "MSG_RESIZED";
                case 5:
                    return "MSG_RESIZED_REPORT";
                case 6:
                    return "MSG_WINDOW_FOCUS_CHANGED";
                case 7:
                    return "MSG_DISPATCH_INPUT_EVENT";
                case 8:
                    return "MSG_DISPATCH_APP_VISIBILITY";
                case 9:
                    return "MSG_DISPATCH_GET_NEW_SURFACE";
                case 10:
                case 23:
                default:
                    return super.getMessageName(message);
                case 11:
                    return "MSG_DISPATCH_KEY_FROM_IME";
                case 12:
                    return "MSG_FINISH_INPUT_CONNECTION";
                case 13:
                    return "MSG_CHECK_FOCUS";
                case 14:
                    return "MSG_CLOSE_SYSTEM_DIALOGS";
                case 15:
                    return "MSG_DISPATCH_DRAG_EVENT";
                case 16:
                    return "MSG_DISPATCH_DRAG_LOCATION_EVENT";
                case 17:
                    return "MSG_DISPATCH_SYSTEM_UI_VISIBILITY";
                case 18:
                    return "MSG_UPDATE_CONFIGURATION";
                case 19:
                    return "MSG_PROCESS_INPUT_EVENTS";
                case 20:
                    return "MSG_DISPATCH_SCREEN_STATE";
                case 21:
                    return "MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST";
                case 22:
                    return "MSG_DISPATCH_DONE_ANIMATING";
                case 24:
                    return "MSG_WINDOW_MOVED";
                case 25:
                    return "MSG_FLUSH_LAYER_UPDATES";
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    ((View) message.obj).invalidate();
                    return;
                case 2:
                    View.AttachInfo.InvalidateInfo invalidateInfo = (View.AttachInfo.InvalidateInfo) message.obj;
                    invalidateInfo.target.invalidate(invalidateInfo.left, invalidateInfo.top, invalidateInfo.right, invalidateInfo.bottom);
                    invalidateInfo.recycle();
                    return;
                case 3:
                    ViewRootImpl.this.doDie();
                    return;
                case 4:
                    SomeArgs someArgs = (SomeArgs) message.obj;
                    if (ViewRootImpl.this.mWinFrame.equals(someArgs.arg1) && ViewRootImpl.this.mPendingOverscanInsets.equals(someArgs.arg5) && ViewRootImpl.this.mPendingContentInsets.equals(someArgs.arg2) && ViewRootImpl.this.mPendingVisibleInsets.equals(someArgs.arg3) && someArgs.arg4 == null) {
                        return;
                    }
                    break;
                case 5:
                    break;
                case 6:
                    if (ViewRootImpl.this.mAdded) {
                        boolean z = message.arg1 != 0;
                        ViewRootImpl.this.mAttachInfo.mHasWindowFocus = z;
                        ViewRootImpl.this.profileRendering(z);
                        if (z) {
                            ViewRootImpl.this.ensureTouchModeLocally(message.arg2 != 0);
                            if (ViewRootImpl.this.mAttachInfo.mHardwareRenderer != null && ViewRootImpl.this.mSurface.isValid()) {
                                ViewRootImpl.this.mFullRedrawNeeded = true;
                                try {
                                    ViewRootImpl.this.mAttachInfo.mHardwareRenderer.initializeIfNeeded(ViewRootImpl.this.mWidth, ViewRootImpl.this.mHeight, ViewRootImpl.this.mHolder.getSurface());
                                } catch (Surface.OutOfResourcesException e) {
                                    Log.e(ViewRootImpl.TAG, "OutOfResourcesException locking surface", e);
                                    try {
                                        if (!ViewRootImpl.this.mWindowSession.outOfMemory(ViewRootImpl.this.mWindow)) {
                                            Slog.w(ViewRootImpl.TAG, "No processes killed for memory; killing self");
                                            Process.killProcess(Process.myPid());
                                        }
                                        break;
                                    } catch (RemoteException unused) {
                                    }
                                    sendMessageDelayed(obtainMessage(message.what, message.arg1, message.arg2), 500L);
                                    return;
                                }
                            }
                            break;
                        }
                        ViewRootImpl viewRootImpl = ViewRootImpl.this;
                        viewRootImpl.mLastWasImTarget = WindowManager.LayoutParams.mayUseInputMethod(viewRootImpl.mWindowAttributes.flags);
                        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
                        if (ViewRootImpl.this.mView != null) {
                            if (z && inputMethodManagerPeekInstance != null && ViewRootImpl.this.mLastWasImTarget && !ViewRootImpl.this.isInLocalFocusMode()) {
                                inputMethodManagerPeekInstance.startGettingWindowFocus(ViewRootImpl.this.mView);
                            }
                            ViewRootImpl.this.mAttachInfo.mKeyDispatchState.reset();
                            ViewRootImpl.this.mView.dispatchWindowFocusChanged(z);
                            ViewRootImpl.this.mAttachInfo.mTreeObserver.dispatchOnWindowFocusChange(z);
                        }
                        if (z) {
                            if (inputMethodManagerPeekInstance != null && ViewRootImpl.this.mLastWasImTarget && !ViewRootImpl.this.isInLocalFocusMode()) {
                                inputMethodManagerPeekInstance.onWindowFocus(ViewRootImpl.this.mView, ViewRootImpl.this.mView.findFocus(), ViewRootImpl.this.mWindowAttributes.softInputMode, !ViewRootImpl.this.mHasHadWindowFocus, ViewRootImpl.this.mWindowAttributes.flags);
                            }
                            ViewRootImpl.this.mWindowAttributes.softInputMode &= -257;
                            ((WindowManager.LayoutParams) ViewRootImpl.this.mView.getLayoutParams()).softInputMode &= -257;
                            ViewRootImpl.this.mHasHadWindowFocus = true;
                        }
                        ViewRootImpl.this.setAccessibilityFocus(null, null);
                        if (ViewRootImpl.this.mView != null && ViewRootImpl.this.mAccessibilityManager.isEnabled() && z) {
                            ViewRootImpl.this.mView.sendAccessibilityEvent(32);
                            return;
                        }
                        return;
                    }
                    return;
                case 7:
                    ViewRootImpl.this.enqueueInputEvent((InputEvent) message.obj, null, 0, true);
                    return;
                case 8:
                    ViewRootImpl.this.handleAppVisibility(message.arg1 != 0);
                    return;
                case 9:
                    ViewRootImpl.this.handleGetNewSurface();
                    return;
                case 10:
                default:
                    return;
                case 11:
                    KeyEvent keyEventChangeFlags = (KeyEvent) message.obj;
                    if ((keyEventChangeFlags.getFlags() & 8) != 0) {
                        keyEventChangeFlags = KeyEvent.changeFlags(keyEventChangeFlags, keyEventChangeFlags.getFlags() & (-9));
                    }
                    ViewRootImpl.this.enqueueInputEvent(keyEventChangeFlags, null, 1, true);
                    return;
                case 12:
                    InputMethodManager inputMethodManagerPeekInstance2 = InputMethodManager.peekInstance();
                    if (inputMethodManagerPeekInstance2 != null) {
                        inputMethodManagerPeekInstance2.reportFinishInputConnection((InputConnection) message.obj);
                        return;
                    }
                    return;
                case 13:
                    InputMethodManager inputMethodManagerPeekInstance3 = InputMethodManager.peekInstance();
                    if (inputMethodManagerPeekInstance3 != null) {
                        inputMethodManagerPeekInstance3.checkFocus();
                        return;
                    }
                    return;
                case 14:
                    if (ViewRootImpl.this.mView != null) {
                        ViewRootImpl.this.mView.onCloseSystemDialogs((String) message.obj);
                        return;
                    }
                    return;
                case 15:
                case 16:
                    DragEvent dragEvent = (DragEvent) message.obj;
                    dragEvent.mLocalState = ViewRootImpl.this.mLocalDragState;
                    ViewRootImpl.this.handleDragEvent(dragEvent);
                    return;
                case 17:
                    ViewRootImpl.this.handleDispatchSystemUiVisibilityChanged((SystemUiVisibilityInfo) message.obj);
                    return;
                case 18:
                    Configuration configuration = (Configuration) message.obj;
                    if (configuration.isOtherSeqNewer(ViewRootImpl.this.mLastConfiguration)) {
                        configuration = ViewRootImpl.this.mLastConfiguration;
                    }
                    ViewRootImpl.this.updateConfiguration(configuration, false);
                    return;
                case 19:
                    ViewRootImpl.this.mProcessInputEventsScheduled = false;
                    ViewRootImpl.this.doProcessInputEvents();
                    return;
                case 20:
                    if (ViewRootImpl.this.mView != null) {
                        ViewRootImpl.this.handleScreenStateChange(message.arg1 == 1);
                        return;
                    }
                    return;
                case 21:
                    ViewRootImpl.this.setAccessibilityFocus(null, null);
                    return;
                case 22:
                    ViewRootImpl.this.handleDispatchDoneAnimating();
                    return;
                case 23:
                    if (ViewRootImpl.this.mView != null) {
                        ViewRootImpl viewRootImpl2 = ViewRootImpl.this;
                        viewRootImpl2.invalidateWorld(viewRootImpl2.mView);
                        return;
                    }
                    return;
                case 24:
                    if (ViewRootImpl.this.mAdded) {
                        int iWidth = ViewRootImpl.this.mWinFrame.width();
                        int iHeight = ViewRootImpl.this.mWinFrame.height();
                        int i = message.arg1;
                        int i2 = message.arg2;
                        ViewRootImpl.this.mWinFrame.left = i;
                        ViewRootImpl.this.mWinFrame.right = i + iWidth;
                        ViewRootImpl.this.mWinFrame.top = i2;
                        ViewRootImpl.this.mWinFrame.bottom = i2 + iHeight;
                        if (ViewRootImpl.this.mView != null) {
                            ViewRootImpl.forceLayout(ViewRootImpl.this.mView);
                        }
                        ViewRootImpl.this.requestLayout();
                        return;
                    }
                    return;
                case 25:
                    ViewRootImpl.this.flushHardwareLayerUpdates();
                    return;
            }
            if (ViewRootImpl.this.mAdded) {
                SomeArgs someArgs2 = (SomeArgs) message.obj;
                Configuration configuration2 = (Configuration) someArgs2.arg4;
                if (configuration2 != null) {
                    ViewRootImpl.this.updateConfiguration(configuration2, false);
                }
                ViewRootImpl.this.mWinFrame.set((Rect) someArgs2.arg1);
                ViewRootImpl.this.mPendingOverscanInsets.set((Rect) someArgs2.arg5);
                ViewRootImpl.this.mPendingContentInsets.set((Rect) someArgs2.arg2);
                ViewRootImpl.this.mPendingVisibleInsets.set((Rect) someArgs2.arg3);
                someArgs2.recycle();
                if (message.what == 5) {
                    ViewRootImpl.this.mReportNextDraw = true;
                }
                if (ViewRootImpl.this.mView != null) {
                    ViewRootImpl.forceLayout(ViewRootImpl.this.mView);
                }
                ViewRootImpl.this.requestLayout();
            }
        }
    }

    boolean ensureTouchMode(boolean z) {
        if (this.mAttachInfo.mInTouchMode == z) {
            return false;
        }
        try {
            if (!isInLocalFocusMode()) {
                this.mWindowSession.setInTouchMode(z);
            }
            return ensureTouchModeLocally(z);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean ensureTouchModeLocally(boolean z) {
        if (this.mAttachInfo.mInTouchMode == z) {
            return false;
        }
        this.mAttachInfo.mInTouchMode = z;
        this.mAttachInfo.mTreeObserver.dispatchOnTouchModeChanged(z);
        return z ? enterTouchMode() : leaveTouchMode();
    }

    private boolean enterTouchMode() {
        View viewFindFocus;
        View view = this.mView;
        if (view == null || !view.hasFocus() || (viewFindFocus = this.mView.findFocus()) == null || viewFindFocus.isFocusableInTouchMode()) {
            return false;
        }
        ViewGroup viewGroupFindAncestorToTakeFocusInTouchMode = findAncestorToTakeFocusInTouchMode(viewFindFocus);
        if (viewGroupFindAncestorToTakeFocusInTouchMode != null) {
            return viewGroupFindAncestorToTakeFocusInTouchMode.requestFocus();
        }
        viewFindFocus.clearFocusInternal(true, false);
        return true;
    }

    private static ViewGroup findAncestorToTakeFocusInTouchMode(View view) {
        ViewParent parent = view.getParent();
        while (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            if (viewGroup.getDescendantFocusability() == 262144 && viewGroup.isFocusableInTouchMode()) {
                return viewGroup;
            }
            if (viewGroup.isRootNamespace()) {
                return null;
            }
            parent = viewGroup.getParent();
        }
        return null;
    }

    private boolean leaveTouchMode() {
        View view = this.mView;
        if (view != null) {
            if (view.hasFocus()) {
                View viewFindFocus = this.mView.findFocus();
                if (!(viewFindFocus instanceof ViewGroup) || ((ViewGroup) viewFindFocus).getDescendantFocusability() != 262144) {
                    return false;
                }
            }
            View viewFocusSearch = focusSearch(null, 130);
            if (viewFocusSearch != null) {
                return viewFocusSearch.requestFocus(130);
            }
        }
        return false;
    }

    abstract class InputStage {
        protected static final int FINISH_HANDLED = 1;
        protected static final int FINISH_NOT_HANDLED = 2;
        protected static final int FORWARD = 0;
        private final InputStage mNext;

        protected int onProcess(QueuedInputEvent queuedInputEvent) {
            return 0;
        }

        public InputStage(InputStage inputStage) {
            this.mNext = inputStage;
        }

        public final void deliver(QueuedInputEvent queuedInputEvent) {
            if ((queuedInputEvent.mFlags & 4) != 0) {
                forward(queuedInputEvent);
            } else if (shouldDropInputEvent(queuedInputEvent)) {
                finish(queuedInputEvent, false);
            } else {
                apply(queuedInputEvent, onProcess(queuedInputEvent));
            }
        }

        protected void finish(QueuedInputEvent queuedInputEvent, boolean z) {
            queuedInputEvent.mFlags |= 4;
            if (z) {
                queuedInputEvent.mFlags |= 8;
            }
            forward(queuedInputEvent);
        }

        protected void forward(QueuedInputEvent queuedInputEvent) {
            onDeliverToNext(queuedInputEvent);
        }

        protected void apply(QueuedInputEvent queuedInputEvent, int i) {
            if (i == 0) {
                forward(queuedInputEvent);
            } else if (i == 1) {
                finish(queuedInputEvent, true);
            } else {
                if (i == 2) {
                    finish(queuedInputEvent, false);
                    return;
                }
                throw new IllegalArgumentException("Invalid result: " + i);
            }
        }

        protected void onDeliverToNext(QueuedInputEvent queuedInputEvent) {
            InputStage inputStage = this.mNext;
            if (inputStage == null) {
                ViewRootImpl.this.finishInputEvent(queuedInputEvent);
            } else {
                inputStage.deliver(queuedInputEvent);
            }
        }

        protected boolean shouldDropInputEvent(QueuedInputEvent queuedInputEvent) {
            if (ViewRootImpl.this.mView == null || !ViewRootImpl.this.mAdded) {
                Slog.w(ViewRootImpl.TAG, "Dropping event due to root view being removed: " + queuedInputEvent.mEvent);
                return true;
            }
            if (ViewRootImpl.this.mAttachInfo.mHasWindowFocus || queuedInputEvent.mEvent.isFromSource(2) || ViewRootImpl.isTerminalInputEvent(queuedInputEvent.mEvent)) {
                return false;
            }
            Slog.w(ViewRootImpl.TAG, "Dropping event due to no window focus: " + queuedInputEvent.mEvent);
            return true;
        }

        void dump(String str, PrintWriter printWriter) {
            InputStage inputStage = this.mNext;
            if (inputStage != null) {
                inputStage.dump(str, printWriter);
            }
        }
    }

    abstract class AsyncInputStage extends InputStage {
        protected static final int DEFER = 3;
        private QueuedInputEvent mQueueHead;
        private int mQueueLength;
        private QueuedInputEvent mQueueTail;
        private final String mTraceCounter;

        public AsyncInputStage(InputStage inputStage, String str) {
            super(inputStage);
            this.mTraceCounter = str;
        }

        protected void defer(QueuedInputEvent queuedInputEvent) {
            queuedInputEvent.mFlags |= 2;
            enqueue(queuedInputEvent);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void forward(QueuedInputEvent queuedInputEvent) {
            queuedInputEvent.mFlags &= -3;
            QueuedInputEvent queuedInputEvent2 = this.mQueueHead;
            if (queuedInputEvent2 == null) {
                super.forward(queuedInputEvent);
                return;
            }
            int deviceId = queuedInputEvent.mEvent.getDeviceId();
            QueuedInputEvent queuedInputEvent3 = null;
            boolean z = false;
            while (queuedInputEvent2 != null && queuedInputEvent2 != queuedInputEvent) {
                if (!z && deviceId == queuedInputEvent2.mEvent.getDeviceId()) {
                    z = true;
                }
                queuedInputEvent3 = queuedInputEvent2;
                queuedInputEvent2 = queuedInputEvent2.mNext;
            }
            if (z) {
                if (queuedInputEvent2 == null) {
                    enqueue(queuedInputEvent);
                    return;
                }
                return;
            }
            if (queuedInputEvent2 != null) {
                queuedInputEvent2 = queuedInputEvent2.mNext;
                dequeue(queuedInputEvent, queuedInputEvent3);
            }
            super.forward(queuedInputEvent);
            QueuedInputEvent queuedInputEvent4 = queuedInputEvent3;
            while (true) {
                QueuedInputEvent queuedInputEvent5 = queuedInputEvent2;
                while (queuedInputEvent5 != null) {
                    if (deviceId == queuedInputEvent5.mEvent.getDeviceId()) {
                        if ((queuedInputEvent5.mFlags & 2) != 0) {
                            return;
                        }
                        queuedInputEvent2 = queuedInputEvent5.mNext;
                        dequeue(queuedInputEvent5, queuedInputEvent4);
                        super.forward(queuedInputEvent5);
                    } else {
                        QueuedInputEvent queuedInputEvent6 = queuedInputEvent5;
                        queuedInputEvent5 = queuedInputEvent5.mNext;
                        queuedInputEvent4 = queuedInputEvent6;
                    }
                }
                return;
            }
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void apply(QueuedInputEvent queuedInputEvent, int i) {
            if (i == 3) {
                defer(queuedInputEvent);
            } else {
                super.apply(queuedInputEvent, i);
            }
        }

        private void enqueue(QueuedInputEvent queuedInputEvent) {
            QueuedInputEvent queuedInputEvent2 = this.mQueueTail;
            if (queuedInputEvent2 == null) {
                this.mQueueHead = queuedInputEvent;
                this.mQueueTail = queuedInputEvent;
            } else {
                queuedInputEvent2.mNext = queuedInputEvent;
                this.mQueueTail = queuedInputEvent;
            }
            int i = this.mQueueLength + 1;
            this.mQueueLength = i;
            Trace.traceCounter(4L, this.mTraceCounter, i);
        }

        private void dequeue(QueuedInputEvent queuedInputEvent, QueuedInputEvent queuedInputEvent2) {
            if (queuedInputEvent2 == null) {
                this.mQueueHead = queuedInputEvent.mNext;
            } else {
                queuedInputEvent2.mNext = queuedInputEvent.mNext;
            }
            if (this.mQueueTail == queuedInputEvent) {
                this.mQueueTail = queuedInputEvent2;
            }
            queuedInputEvent.mNext = null;
            int i = this.mQueueLength - 1;
            this.mQueueLength = i;
            Trace.traceCounter(4L, this.mTraceCounter, i);
        }

        @Override // android.view.ViewRootImpl.InputStage
        void dump(String str, PrintWriter printWriter) {
            printWriter.print(str);
            printWriter.print(getClass().getName());
            printWriter.print(": mQueueLength=");
            printWriter.println(this.mQueueLength);
            super.dump(str, printWriter);
        }
    }

    final class NativePreImeInputStage extends AsyncInputStage implements InputQueue.FinishedInputEventCallback {
        public NativePreImeInputStage(InputStage inputStage, String str) {
            super(inputStage, str);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent queuedInputEvent) {
            if (ViewRootImpl.this.mInputQueue == null || !(queuedInputEvent.mEvent instanceof KeyEvent)) {
                return 0;
            }
            ViewRootImpl.this.mInputQueue.sendInputEvent(queuedInputEvent.mEvent, queuedInputEvent, true, this);
            return 3;
        }

        @Override // android.view.InputQueue.FinishedInputEventCallback
        public void onFinishedInputEvent(Object obj, boolean z) {
            QueuedInputEvent queuedInputEvent = (QueuedInputEvent) obj;
            if (z) {
                finish(queuedInputEvent, true);
            } else {
                forward(queuedInputEvent);
            }
        }
    }

    final class ViewPreImeInputStage extends InputStage {
        public ViewPreImeInputStage(InputStage inputStage) {
            super(inputStage);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent queuedInputEvent) {
            if (queuedInputEvent.mEvent instanceof KeyEvent) {
                return processKeyEvent(queuedInputEvent);
            }
            return 0;
        }

        private int processKeyEvent(QueuedInputEvent queuedInputEvent) {
            return ViewRootImpl.this.mView.dispatchKeyEventPreIme((KeyEvent) queuedInputEvent.mEvent) ? 1 : 0;
        }
    }

    final class ImeInputStage extends AsyncInputStage implements InputMethodManager.FinishedInputEventCallback {
        public ImeInputStage(InputStage inputStage, String str) {
            super(inputStage, str);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent queuedInputEvent) {
            InputMethodManager inputMethodManagerPeekInstance;
            if (!ViewRootImpl.this.mLastWasImTarget || ViewRootImpl.this.isInLocalFocusMode() || (inputMethodManagerPeekInstance = InputMethodManager.peekInstance()) == null) {
                return 0;
            }
            int iDispatchInputEvent = inputMethodManagerPeekInstance.dispatchInputEvent(queuedInputEvent.mEvent, queuedInputEvent, this, ViewRootImpl.this.mHandler);
            if (iDispatchInputEvent == 1) {
                return 1;
            }
            return iDispatchInputEvent == 0 ? 2 : 3;
        }

        @Override // android.view.inputmethod.InputMethodManager.FinishedInputEventCallback
        public void onFinishedInputEvent(Object obj, boolean z) {
            QueuedInputEvent queuedInputEvent = (QueuedInputEvent) obj;
            if (z) {
                finish(queuedInputEvent, true);
            } else {
                forward(queuedInputEvent);
            }
        }
    }

    final class EarlyPostImeInputStage extends InputStage {
        public EarlyPostImeInputStage(InputStage inputStage) {
            super(inputStage);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent queuedInputEvent) {
            if (queuedInputEvent.mEvent instanceof KeyEvent) {
                return processKeyEvent(queuedInputEvent);
            }
            if ((queuedInputEvent.mEvent.getSource() & 2) != 0) {
                return processPointerEvent(queuedInputEvent);
            }
            return 0;
        }

        private int processKeyEvent(QueuedInputEvent queuedInputEvent) {
            KeyEvent keyEvent = (KeyEvent) queuedInputEvent.mEvent;
            if (ViewRootImpl.this.checkForLeavingTouchModeAndConsume(keyEvent)) {
                return 1;
            }
            ViewRootImpl.this.mFallbackEventHandler.preDispatchKeyEvent(keyEvent);
            return 0;
        }

        private int processPointerEvent(QueuedInputEvent queuedInputEvent) {
            MotionEvent motionEvent = (MotionEvent) queuedInputEvent.mEvent;
            SurfaceView.adjustSurfaceViewMotion(motionEvent);
            if (ViewRootImpl.this.mTranslator != null) {
                ViewRootImpl.this.mTranslator.translateEventInScreenToAppWindow(motionEvent);
            }
            int action = motionEvent.getAction();
            if (action == 0 || action == 8) {
                ViewRootImpl.this.ensureTouchMode(true);
            }
            if (ViewRootImpl.this.mCurScrollY != 0) {
                motionEvent.offsetLocation(0.0f, ViewRootImpl.this.mCurScrollY);
            }
            if (!motionEvent.isTouchEvent()) {
                return 0;
            }
            ViewRootImpl.this.mLastTouchPoint.x = motionEvent.getRawX();
            ViewRootImpl.this.mLastTouchPoint.y = motionEvent.getRawY();
            return 0;
        }
    }

    final class NativePostImeInputStage extends AsyncInputStage implements InputQueue.FinishedInputEventCallback {
        public NativePostImeInputStage(InputStage inputStage, String str) {
            super(inputStage, str);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent queuedInputEvent) {
            if (ViewRootImpl.this.mInputQueue == null) {
                return 0;
            }
            ViewRootImpl.this.mInputQueue.sendInputEvent(queuedInputEvent.mEvent, queuedInputEvent, false, this);
            return 3;
        }

        @Override // android.view.InputQueue.FinishedInputEventCallback
        public void onFinishedInputEvent(Object obj, boolean z) {
            QueuedInputEvent queuedInputEvent = (QueuedInputEvent) obj;
            if (z) {
                finish(queuedInputEvent, true);
            } else {
                forward(queuedInputEvent);
            }
        }
    }

    final class ViewPostImeInputStage extends InputStage {
        public ViewPostImeInputStage(InputStage inputStage) {
            super(inputStage);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent queuedInputEvent) {
            if (queuedInputEvent.mEvent instanceof KeyEvent) {
                return processKeyEvent(queuedInputEvent);
            }
            ViewRootImpl.this.handleDispatchDoneAnimating();
            int source = queuedInputEvent.mEvent.getSource();
            if ((source & 2) != 0) {
                return processPointerEvent(queuedInputEvent);
            }
            if ((source & 4) != 0) {
                return processTrackballEvent(queuedInputEvent);
            }
            return processGenericMotionEvent(queuedInputEvent);
        }

        private int processKeyEvent(QueuedInputEvent queuedInputEvent) {
            KeyEvent keyEvent = (KeyEvent) queuedInputEvent.mEvent;
            if (keyEvent.getAction() != 1) {
                ViewRootImpl.this.handleDispatchDoneAnimating();
            }
            if (ViewRootImpl.this.mView.dispatchKeyEvent(keyEvent)) {
                return 1;
            }
            int i = 2;
            if (shouldDropInputEvent(queuedInputEvent)) {
                return 2;
            }
            if (keyEvent.getAction() == 0 && keyEvent.isCtrlPressed() && keyEvent.getRepeatCount() == 0 && !KeyEvent.isModifierKey(keyEvent.getKeyCode())) {
                if (ViewRootImpl.this.mView.dispatchKeyShortcutEvent(keyEvent)) {
                    return 1;
                }
                if (shouldDropInputEvent(queuedInputEvent)) {
                    return 2;
                }
            }
            if (ViewRootImpl.this.mFallbackEventHandler.dispatchKeyEvent(keyEvent)) {
                return 1;
            }
            if (shouldDropInputEvent(queuedInputEvent)) {
                return 2;
            }
            if (keyEvent.getAction() == 0) {
                int keyCode = keyEvent.getKeyCode();
                if (keyCode != 61) {
                    switch (keyCode) {
                        case 19:
                            i = !keyEvent.hasNoModifiers() ? 0 : 33;
                            break;
                        case 20:
                            i = !keyEvent.hasNoModifiers() ? 0 : 130;
                            break;
                        case 21:
                            i = !keyEvent.hasNoModifiers() ? 0 : 17;
                            break;
                        case 22:
                            i = !keyEvent.hasNoModifiers() ? 0 : 66;
                            break;
                        default:
                            i = 0;
                            break;
                    }
                } else if (!keyEvent.hasNoModifiers()) {
                    i = keyEvent.hasModifiers(1) ? 1 : 0;
                }
                if (i != 0) {
                    View viewFindFocus = ViewRootImpl.this.mView.findFocus();
                    if (viewFindFocus != null) {
                        View viewFocusSearch = viewFindFocus.focusSearch(i);
                        if (viewFocusSearch != null && viewFocusSearch != viewFindFocus) {
                            viewFindFocus.getFocusedRect(ViewRootImpl.this.mTempRect);
                            if (ViewRootImpl.this.mView instanceof ViewGroup) {
                                ((ViewGroup) ViewRootImpl.this.mView).offsetDescendantRectToMyCoords(viewFindFocus, ViewRootImpl.this.mTempRect);
                                ((ViewGroup) ViewRootImpl.this.mView).offsetRectIntoDescendantCoords(viewFocusSearch, ViewRootImpl.this.mTempRect);
                            }
                            if (viewFocusSearch.requestFocus(i, ViewRootImpl.this.mTempRect)) {
                                ViewRootImpl.this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(i));
                                return 1;
                            }
                        }
                        if (ViewRootImpl.this.mView.dispatchUnhandledMove(viewFindFocus, i)) {
                            return 1;
                        }
                    } else {
                        View viewFocusSearch2 = ViewRootImpl.this.focusSearch(null, i);
                        if (viewFocusSearch2 != null && viewFocusSearch2.requestFocus(i)) {
                            return 1;
                        }
                    }
                }
            }
            return 0;
        }

        private int processPointerEvent(QueuedInputEvent queuedInputEvent) {
            return ViewRootImpl.this.mView.dispatchPointerEvent((MotionEvent) queuedInputEvent.mEvent) ? 1 : 0;
        }

        private int processTrackballEvent(QueuedInputEvent queuedInputEvent) {
            return ViewRootImpl.this.mView.dispatchTrackballEvent((MotionEvent) queuedInputEvent.mEvent) ? 1 : 0;
        }

        private int processGenericMotionEvent(QueuedInputEvent queuedInputEvent) {
            return ViewRootImpl.this.mView.dispatchGenericMotionEvent((MotionEvent) queuedInputEvent.mEvent) ? 1 : 0;
        }
    }

    final class SyntheticInputStage extends InputStage {
        private final SyntheticJoystickHandler mJoystick;
        private final SyntheticKeyHandler mKeys;
        private final SyntheticTouchNavigationHandler mTouchNavigation;
        private final SyntheticTrackballHandler mTrackball;

        public SyntheticInputStage() {
            super(null);
            this.mTrackball = ViewRootImpl.this.new SyntheticTrackballHandler();
            this.mJoystick = ViewRootImpl.this.new SyntheticJoystickHandler();
            this.mTouchNavigation = ViewRootImpl.this.new SyntheticTouchNavigationHandler();
            this.mKeys = ViewRootImpl.this.new SyntheticKeyHandler();
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent queuedInputEvent) {
            queuedInputEvent.mFlags |= 16;
            if (!(queuedInputEvent.mEvent instanceof MotionEvent)) {
                return ((queuedInputEvent.mEvent instanceof KeyEvent) && this.mKeys.process((KeyEvent) queuedInputEvent.mEvent)) ? 1 : 0;
            }
            MotionEvent motionEvent = (MotionEvent) queuedInputEvent.mEvent;
            int source = motionEvent.getSource();
            if ((source & 4) != 0) {
                this.mTrackball.process(motionEvent);
                return 1;
            }
            if ((source & 16) != 0) {
                this.mJoystick.process(motionEvent);
                return 1;
            }
            if ((source & 2097152) != 2097152) {
                return 0;
            }
            this.mTouchNavigation.process(motionEvent);
            return 1;
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void onDeliverToNext(QueuedInputEvent queuedInputEvent) {
            if ((queuedInputEvent.mFlags & 16) == 0 && (queuedInputEvent.mEvent instanceof MotionEvent)) {
                MotionEvent motionEvent = (MotionEvent) queuedInputEvent.mEvent;
                int source = motionEvent.getSource();
                if ((source & 4) != 0) {
                    this.mTrackball.cancel(motionEvent);
                } else if ((source & 16) != 0) {
                    this.mJoystick.cancel(motionEvent);
                } else if ((source & 2097152) == 2097152) {
                    this.mTouchNavigation.cancel(motionEvent);
                }
            }
            super.onDeliverToNext(queuedInputEvent);
        }
    }

    final class SyntheticTrackballHandler {
        private long mLastTime;
        private final TrackballAxis mX = new TrackballAxis();
        private final TrackballAxis mY = new TrackballAxis();

        SyntheticTrackballHandler() {
        }

        public void process(MotionEvent motionEvent) {
            int i;
            long j;
            int i2;
            int iGenerate;
            int i3;
            int i4;
            long jUptimeMillis = SystemClock.uptimeMillis();
            if (this.mLastTime + 250 < jUptimeMillis) {
                this.mX.reset(0);
                this.mY.reset(0);
                this.mLastTime = jUptimeMillis;
            }
            int action = motionEvent.getAction();
            int metaState = motionEvent.getMetaState();
            if (action == 0) {
                i = 0;
                this.mX.reset(2);
                this.mY.reset(2);
                j = jUptimeMillis;
                i2 = 2;
                ViewRootImpl.this.enqueueInputEvent(new KeyEvent(jUptimeMillis, jUptimeMillis, 0, 23, 0, metaState, -1, 0, 1024, 257));
            } else if (action != 1) {
                i = 0;
                j = jUptimeMillis;
                i2 = 2;
            } else {
                this.mX.reset(2);
                this.mY.reset(2);
                i = 0;
                ViewRootImpl.this.enqueueInputEvent(new KeyEvent(jUptimeMillis, jUptimeMillis, 1, 23, 0, metaState, -1, 0, 1024, 257));
                j = jUptimeMillis;
                i2 = 2;
            }
            float fCollect = this.mX.collect(motionEvent.getX(), motionEvent.getEventTime(), "X");
            float fCollect2 = this.mY.collect(motionEvent.getY(), motionEvent.getEventTime(), "Y");
            float f = 1.0f;
            if (fCollect > fCollect2) {
                iGenerate = this.mX.generate();
                if (iGenerate != 0) {
                    i4 = iGenerate > 0 ? 22 : 21;
                    f = this.mX.acceleration;
                    this.mY.reset(i2);
                    i3 = i4;
                }
                i3 = i;
            } else if (fCollect2 > 0.0f) {
                iGenerate = this.mY.generate();
                if (iGenerate != 0) {
                    i4 = iGenerate > 0 ? 20 : 19;
                    f = this.mY.acceleration;
                    this.mX.reset(i2);
                    i3 = i4;
                }
                i3 = i;
            } else {
                iGenerate = i;
                i3 = iGenerate;
            }
            if (i3 != 0) {
                if (iGenerate < 0) {
                    iGenerate = -iGenerate;
                }
                int i5 = (int) (iGenerate * f);
                if (i5 > iGenerate) {
                    int i6 = iGenerate - 1;
                    ViewRootImpl.this.enqueueInputEvent(new KeyEvent(j, j, 2, i3, i5 - i6, metaState, -1, 0, 1024, 257));
                    iGenerate = i6;
                }
                long jUptimeMillis2 = j;
                while (iGenerate > 0) {
                    iGenerate--;
                    jUptimeMillis2 = SystemClock.uptimeMillis();
                    int i7 = i3;
                    ViewRootImpl.this.enqueueInputEvent(new KeyEvent(jUptimeMillis2, jUptimeMillis2, 0, i7, 0, metaState, -1, 0, 1024, 257));
                    ViewRootImpl.this.enqueueInputEvent(new KeyEvent(jUptimeMillis2, jUptimeMillis2, 1, i7, 0, metaState, -1, 0, 1024, 257));
                }
                this.mLastTime = jUptimeMillis2;
            }
        }

        public void cancel(MotionEvent motionEvent) {
            this.mLastTime = -2147483648L;
            if (ViewRootImpl.this.mView == null || !ViewRootImpl.this.mAdded) {
                return;
            }
            ViewRootImpl.this.ensureTouchMode(false);
        }
    }

    static final class TrackballAxis {
        static final float ACCEL_MOVE_SCALING_FACTOR = 0.025f;
        static final long FAST_MOVE_TIME = 150;
        static final float FIRST_MOVEMENT_THRESHOLD = 0.5f;
        static final float MAX_ACCELERATION = 20.0f;
        static final float SECOND_CUMULATIVE_MOVEMENT_THRESHOLD = 2.0f;
        static final float SUBSEQUENT_INCREMENTAL_MOVEMENT_THRESHOLD = 1.0f;
        int dir;
        int nonAccelMovement;
        float position;
        int step;
        float acceleration = 1.0f;
        long lastMoveTime = 0;

        TrackballAxis() {
        }

        void reset(int i) {
            this.position = 0.0f;
            this.acceleration = 1.0f;
            this.lastMoveTime = 0L;
            this.step = i;
            this.dir = 0;
        }

        float collect(float f, long j, String str) {
            long j2;
            if (f > 0.0f) {
                j2 = (long) (150.0f * f);
                if (this.dir < 0) {
                    this.position = 0.0f;
                    this.step = 0;
                    this.acceleration = 1.0f;
                    this.lastMoveTime = 0L;
                }
                this.dir = 1;
            } else if (f < 0.0f) {
                j2 = (long) ((-f) * 150.0f);
                if (this.dir > 0) {
                    this.position = 0.0f;
                    this.step = 0;
                    this.acceleration = 1.0f;
                    this.lastMoveTime = 0L;
                }
                this.dir = -1;
            } else {
                j2 = 0;
            }
            if (j2 > 0) {
                long j3 = j - this.lastMoveTime;
                this.lastMoveTime = j;
                float f2 = this.acceleration;
                if (j3 < j2) {
                    float f3 = (j2 - j3) * ACCEL_MOVE_SCALING_FACTOR;
                    if (f3 > 1.0f) {
                        f2 *= f3;
                    }
                    if (f2 >= 20.0f) {
                        f2 = 20.0f;
                    }
                    this.acceleration = f2;
                } else {
                    float f4 = (j3 - j2) * ACCEL_MOVE_SCALING_FACTOR;
                    if (f4 > 1.0f) {
                        f2 /= f4;
                    }
                    this.acceleration = f2 > 1.0f ? f2 : 1.0f;
                }
            }
            float f5 = this.position + f;
            this.position = f5;
            return Math.abs(f5);
        }

        int generate() {
            int i = 0;
            this.nonAccelMovement = 0;
            while (true) {
                float f = this.position;
                int i2 = f >= 0.0f ? 1 : -1;
                int i3 = this.step;
                if (i3 != 0) {
                    if (i3 != 1) {
                        if (Math.abs(f) < 1.0f) {
                            return i;
                        }
                        i += i2;
                        this.position -= i2 * 1.0f;
                        float f2 = this.acceleration;
                        float f3 = 1.1f * f2;
                        if (f3 < 20.0f) {
                            f2 = f3;
                        }
                        this.acceleration = f2;
                    } else {
                        if (Math.abs(f) < SECOND_CUMULATIVE_MOVEMENT_THRESHOLD) {
                            return i;
                        }
                        i += i2;
                        this.nonAccelMovement += i2;
                        this.position -= i2 * SECOND_CUMULATIVE_MOVEMENT_THRESHOLD;
                        this.step = 2;
                    }
                } else {
                    if (Math.abs(f) < 0.5f) {
                        return i;
                    }
                    i += i2;
                    this.nonAccelMovement += i2;
                    this.step = 1;
                }
            }
        }
    }

    final class SyntheticJoystickHandler extends Handler {
        private static final int MSG_ENQUEUE_X_AXIS_KEY_REPEAT = 1;
        private static final int MSG_ENQUEUE_Y_AXIS_KEY_REPEAT = 2;
        private int mLastXDirection;
        private int mLastXKeyCode;
        private int mLastYDirection;
        private int mLastYKeyCode;

        private int joystickAxisValueToDirection(float f) {
            if (f >= 0.5f) {
                return 1;
            }
            return f <= -0.5f ? -1 : 0;
        }

        public SyntheticJoystickHandler() {
            super(true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1 || i == 2) {
                KeyEvent keyEvent = (KeyEvent) message.obj;
                KeyEvent keyEventChangeTimeRepeat = KeyEvent.changeTimeRepeat(keyEvent, SystemClock.uptimeMillis(), keyEvent.getRepeatCount() + 1);
                if (ViewRootImpl.this.mAttachInfo.mHasWindowFocus) {
                    ViewRootImpl.this.enqueueInputEvent(keyEventChangeTimeRepeat);
                    Message messageObtainMessage = obtainMessage(message.what, keyEventChangeTimeRepeat);
                    messageObtainMessage.setAsynchronous(true);
                    sendMessageDelayed(messageObtainMessage, ViewConfiguration.getKeyRepeatDelay());
                }
            }
        }

        public void process(MotionEvent motionEvent) {
            update(motionEvent, true);
        }

        public void cancel(MotionEvent motionEvent) {
            update(motionEvent, false);
        }

        private void update(MotionEvent motionEvent, boolean z) {
            int i;
            long j;
            int i2;
            long eventTime = motionEvent.getEventTime();
            int metaState = motionEvent.getMetaState();
            int deviceId = motionEvent.getDeviceId();
            int source = motionEvent.getSource();
            int iJoystickAxisValueToDirection = joystickAxisValueToDirection(motionEvent.getAxisValue(15));
            if (iJoystickAxisValueToDirection == 0) {
                iJoystickAxisValueToDirection = joystickAxisValueToDirection(motionEvent.getX());
            }
            int i3 = iJoystickAxisValueToDirection;
            int iJoystickAxisValueToDirection2 = joystickAxisValueToDirection(motionEvent.getAxisValue(16));
            if (iJoystickAxisValueToDirection2 == 0) {
                iJoystickAxisValueToDirection2 = joystickAxisValueToDirection(motionEvent.getY());
            }
            int i4 = iJoystickAxisValueToDirection2;
            if (i3 != this.mLastXDirection) {
                if (this.mLastXKeyCode != 0) {
                    removeMessages(1);
                    i = i4;
                    j = eventTime;
                    i2 = i3;
                    ViewRootImpl.this.enqueueInputEvent(new KeyEvent(eventTime, eventTime, 1, this.mLastXKeyCode, 0, metaState, deviceId, 0, 1024, source));
                    this.mLastXKeyCode = 0;
                } else {
                    i = i4;
                    j = eventTime;
                    i2 = i3;
                }
                this.mLastXDirection = i2;
                if (i2 != 0 && z) {
                    this.mLastXKeyCode = i2 > 0 ? 22 : 21;
                    KeyEvent keyEvent = new KeyEvent(j, j, 0, this.mLastXKeyCode, 0, metaState, deviceId, 0, 1024, source);
                    ViewRootImpl.this.enqueueInputEvent(keyEvent);
                    Message messageObtainMessage = obtainMessage(1, keyEvent);
                    messageObtainMessage.setAsynchronous(true);
                    sendMessageDelayed(messageObtainMessage, ViewConfiguration.getKeyRepeatTimeout());
                }
            } else {
                i = i4;
                j = eventTime;
            }
            int i5 = i;
            if (i5 != this.mLastYDirection) {
                if (this.mLastYKeyCode != 0) {
                    removeMessages(2);
                    ViewRootImpl.this.enqueueInputEvent(new KeyEvent(j, j, 1, this.mLastYKeyCode, 0, metaState, deviceId, 0, 1024, source));
                    this.mLastYKeyCode = 0;
                }
                this.mLastYDirection = i5;
                if (i5 == 0 || !z) {
                    return;
                }
                this.mLastYKeyCode = i5 > 0 ? 20 : 19;
                KeyEvent keyEvent2 = new KeyEvent(j, j, 0, this.mLastYKeyCode, 0, metaState, deviceId, 0, 1024, source);
                ViewRootImpl.this.enqueueInputEvent(keyEvent2);
                Message messageObtainMessage2 = obtainMessage(2, keyEvent2);
                messageObtainMessage2.setAsynchronous(true);
                sendMessageDelayed(messageObtainMessage2, ViewConfiguration.getKeyRepeatTimeout());
            }
        }
    }

    final class SyntheticTouchNavigationHandler extends Handler {
        private static final float DEFAULT_HEIGHT_MILLIMETERS = 48.0f;
        private static final float DEFAULT_WIDTH_MILLIMETERS = 48.0f;
        private static final float FLING_TICK_DECAY = 0.8f;
        private static final boolean LOCAL_DEBUG = false;
        private static final String LOCAL_TAG = "SyntheticTouchNavigationHandler";
        private static final float MAX_FLING_VELOCITY_TICKS_PER_SECOND = 20.0f;
        private static final float MIN_FLING_VELOCITY_TICKS_PER_SECOND = 6.0f;
        private static final int TICK_DISTANCE_MILLIMETERS = 12;
        private float mAccumulatedX;
        private float mAccumulatedY;
        private int mActivePointerId;
        private float mConfigMaxFlingVelocity;
        private float mConfigMinFlingVelocity;
        private float mConfigTickDistance;
        private boolean mConsumedMovement;
        private int mCurrentDeviceId;
        private boolean mCurrentDeviceSupported;
        private int mCurrentSource;
        private final Runnable mFlingRunnable;
        private float mFlingVelocity;
        private boolean mFlinging;
        private long mLastConfirmKeyTime;
        private float mLastX;
        private float mLastY;
        private int mPendingKeyCode;
        private long mPendingKeyDownTime;
        private int mPendingKeyMetaState;
        private int mPendingKeyRepeatCount;
        private long mStartTime;
        private float mStartX;
        private float mStartY;
        private VelocityTracker mVelocityTracker;

        static /* synthetic */ float access$1432(SyntheticTouchNavigationHandler syntheticTouchNavigationHandler, float f) {
            float f2 = syntheticTouchNavigationHandler.mFlingVelocity * f;
            syntheticTouchNavigationHandler.mFlingVelocity = f2;
            return f2;
        }

        public SyntheticTouchNavigationHandler() {
            super(true);
            this.mCurrentDeviceId = -1;
            this.mActivePointerId = -1;
            this.mPendingKeyCode = 0;
            this.mLastConfirmKeyTime = LinkQualityInfo.UNKNOWN_LONG;
            this.mFlingRunnable = new Runnable() { // from class: android.view.ViewRootImpl.SyntheticTouchNavigationHandler.1
                @Override // java.lang.Runnable
                public void run() {
                    long jUptimeMillis = SystemClock.uptimeMillis();
                    SyntheticTouchNavigationHandler syntheticTouchNavigationHandler = SyntheticTouchNavigationHandler.this;
                    syntheticTouchNavigationHandler.sendKeyDownOrRepeat(jUptimeMillis, syntheticTouchNavigationHandler.mPendingKeyCode, SyntheticTouchNavigationHandler.this.mPendingKeyMetaState);
                    SyntheticTouchNavigationHandler.access$1432(SyntheticTouchNavigationHandler.this, SyntheticTouchNavigationHandler.FLING_TICK_DECAY);
                    if (SyntheticTouchNavigationHandler.this.postFling(jUptimeMillis)) {
                        return;
                    }
                    SyntheticTouchNavigationHandler.this.mFlinging = false;
                    SyntheticTouchNavigationHandler.this.finishKeys(jUptimeMillis);
                }
            };
        }

        public void process(MotionEvent motionEvent) {
            long eventTime = motionEvent.getEventTime();
            int deviceId = motionEvent.getDeviceId();
            int source = motionEvent.getSource();
            if (this.mCurrentDeviceId != deviceId || this.mCurrentSource != source) {
                finishKeys(eventTime);
                finishTracking(eventTime);
                this.mCurrentDeviceId = deviceId;
                this.mCurrentSource = source;
                this.mCurrentDeviceSupported = false;
                InputDevice device = motionEvent.getDevice();
                if (device != null) {
                    InputDevice.MotionRange motionRange = device.getMotionRange(0);
                    InputDevice.MotionRange motionRange2 = device.getMotionRange(1);
                    if (motionRange != null && motionRange2 != null) {
                        this.mCurrentDeviceSupported = true;
                        float resolution = motionRange.getResolution();
                        if (resolution <= 0.0f) {
                            resolution = motionRange.getRange() / 48.0f;
                        }
                        float resolution2 = motionRange2.getResolution();
                        if (resolution2 <= 0.0f) {
                            resolution2 = motionRange2.getRange() / 48.0f;
                        }
                        float f = (resolution + resolution2) * 0.5f * 12.0f;
                        this.mConfigTickDistance = f;
                        this.mConfigMinFlingVelocity = MIN_FLING_VELOCITY_TICKS_PER_SECOND * f;
                        this.mConfigMaxFlingVelocity = f * 20.0f;
                    }
                }
            }
            if (this.mCurrentDeviceSupported) {
                int actionMasked = motionEvent.getActionMasked();
                if (actionMasked == 0) {
                    boolean z = this.mFlinging;
                    finishKeys(eventTime);
                    finishTracking(eventTime);
                    this.mActivePointerId = motionEvent.getPointerId(0);
                    VelocityTracker velocityTrackerObtain = VelocityTracker.obtain();
                    this.mVelocityTracker = velocityTrackerObtain;
                    velocityTrackerObtain.addMovement(motionEvent);
                    this.mStartTime = eventTime;
                    this.mStartX = motionEvent.getX();
                    float y = motionEvent.getY();
                    this.mStartY = y;
                    this.mLastX = this.mStartX;
                    this.mLastY = y;
                    this.mAccumulatedX = 0.0f;
                    this.mAccumulatedY = 0.0f;
                    this.mConsumedMovement = z;
                    return;
                }
                if (actionMasked != 1 && actionMasked != 2) {
                    if (actionMasked != 3) {
                        return;
                    }
                    finishKeys(eventTime);
                    finishTracking(eventTime);
                    return;
                }
                int i = this.mActivePointerId;
                if (i < 0) {
                    return;
                }
                int iFindPointerIndex = motionEvent.findPointerIndex(i);
                if (iFindPointerIndex < 0) {
                    finishKeys(eventTime);
                    finishTracking(eventTime);
                    return;
                }
                this.mVelocityTracker.addMovement(motionEvent);
                float x = motionEvent.getX(iFindPointerIndex);
                float y2 = motionEvent.getY(iFindPointerIndex);
                this.mAccumulatedX += x - this.mLastX;
                this.mAccumulatedY += y2 - this.mLastY;
                this.mLastX = x;
                this.mLastY = y2;
                consumeAccumulatedMovement(eventTime, motionEvent.getMetaState());
                if (actionMasked == 1) {
                    if (this.mConsumedMovement && this.mPendingKeyCode != 0) {
                        this.mVelocityTracker.computeCurrentVelocity(1000, this.mConfigMaxFlingVelocity);
                        if (!startFling(eventTime, this.mVelocityTracker.getXVelocity(this.mActivePointerId), this.mVelocityTracker.getYVelocity(this.mActivePointerId))) {
                            finishKeys(eventTime);
                        }
                    }
                    finishTracking(eventTime);
                }
            }
        }

        public void cancel(MotionEvent motionEvent) {
            if (this.mCurrentDeviceId == motionEvent.getDeviceId() && this.mCurrentSource == motionEvent.getSource()) {
                long eventTime = motionEvent.getEventTime();
                finishKeys(eventTime);
                finishTracking(eventTime);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void finishKeys(long j) {
            cancelFling();
            sendKeyUp(j);
        }

        private void finishTracking(long j) {
            if (this.mActivePointerId >= 0) {
                this.mActivePointerId = -1;
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
            }
        }

        private void consumeAccumulatedMovement(long j, int i) {
            float fAbs = Math.abs(this.mAccumulatedX);
            float fAbs2 = Math.abs(this.mAccumulatedY);
            if (fAbs >= fAbs2) {
                if (fAbs >= this.mConfigTickDistance) {
                    this.mAccumulatedX = consumeAccumulatedMovement(j, i, this.mAccumulatedX, 21, 22);
                    this.mAccumulatedY = 0.0f;
                    this.mConsumedMovement = true;
                    return;
                }
                return;
            }
            if (fAbs2 >= this.mConfigTickDistance) {
                this.mAccumulatedY = consumeAccumulatedMovement(j, i, this.mAccumulatedY, 19, 20);
                this.mAccumulatedX = 0.0f;
                this.mConsumedMovement = true;
            }
        }

        private float consumeAccumulatedMovement(long j, int i, float f, int i2, int i3) {
            while (f <= (-this.mConfigTickDistance)) {
                sendKeyDownOrRepeat(j, i2, i);
                f += this.mConfigTickDistance;
            }
            while (f >= this.mConfigTickDistance) {
                sendKeyDownOrRepeat(j, i3, i);
                f -= this.mConfigTickDistance;
            }
            return f;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendKeyDownOrRepeat(long j, int i, int i2) {
            if (this.mPendingKeyCode != i) {
                sendKeyUp(j);
                this.mPendingKeyDownTime = j;
                this.mPendingKeyCode = i;
                this.mPendingKeyRepeatCount = 0;
            } else {
                this.mPendingKeyRepeatCount++;
            }
            this.mPendingKeyMetaState = i2;
            ViewRootImpl.this.enqueueInputEvent(new KeyEvent(this.mPendingKeyDownTime, j, 0, this.mPendingKeyCode, this.mPendingKeyRepeatCount, this.mPendingKeyMetaState, this.mCurrentDeviceId, 1024, this.mCurrentSource));
        }

        private void sendKeyUp(long j) {
            if (this.mPendingKeyCode != 0) {
                ViewRootImpl.this.enqueueInputEvent(new KeyEvent(this.mPendingKeyDownTime, j, 1, this.mPendingKeyCode, 0, this.mPendingKeyMetaState, this.mCurrentDeviceId, 0, 1024, this.mCurrentSource));
                this.mPendingKeyCode = 0;
            }
        }

        private boolean startFling(long j, float f, float f2) {
            switch (this.mPendingKeyCode) {
                case 19:
                    float f3 = -f2;
                    if (f3 < this.mConfigMinFlingVelocity || Math.abs(f) >= this.mConfigMinFlingVelocity) {
                        return false;
                    }
                    this.mFlingVelocity = f3;
                    break;
                case 20:
                    if (f2 < this.mConfigMinFlingVelocity || Math.abs(f) >= this.mConfigMinFlingVelocity) {
                        return false;
                    }
                    this.mFlingVelocity = f2;
                    break;
                case 21:
                    float f4 = -f;
                    if (f4 < this.mConfigMinFlingVelocity || Math.abs(f2) >= this.mConfigMinFlingVelocity) {
                        return false;
                    }
                    this.mFlingVelocity = f4;
                    break;
                case 22:
                    if (f < this.mConfigMinFlingVelocity || Math.abs(f2) >= this.mConfigMinFlingVelocity) {
                        return false;
                    }
                    this.mFlingVelocity = f;
                    break;
            }
            boolean zPostFling = postFling(j);
            this.mFlinging = zPostFling;
            return zPostFling;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean postFling(long j) {
            float f = this.mFlingVelocity;
            if (f < this.mConfigMinFlingVelocity) {
                return false;
            }
            postAtTime(this.mFlingRunnable, j + ((long) ((this.mConfigTickDistance / f) * 1000.0f)));
            return true;
        }

        private void cancelFling() {
            if (this.mFlinging) {
                removeCallbacks(this.mFlingRunnable);
                this.mFlinging = false;
            }
        }
    }

    final class SyntheticKeyHandler {
        SyntheticKeyHandler() {
        }

        /* JADX WARN: Removed duplicated region for block: B:15:0x0024  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean process(android.view.KeyEvent r17) {
            /*
                r16 = this;
                r0 = r16
                int r1 = r17.getKeyCode()
                r2 = 4
                r3 = 23
                switch(r1) {
                    case 96: goto L1d;
                    case 97: goto L16;
                    case 98: goto L1d;
                    case 99: goto L1d;
                    case 100: goto L16;
                    case 101: goto L1d;
                    default: goto Lc;
                }
            Lc:
                switch(r1) {
                    case 106: goto L14;
                    case 107: goto L14;
                    case 108: goto L14;
                    default: goto Lf;
                }
            Lf:
                switch(r1) {
                    case 188: goto L14;
                    case 189: goto L14;
                    case 190: goto L14;
                    case 191: goto L14;
                    case 192: goto L14;
                    case 193: goto L14;
                    case 194: goto L14;
                    case 195: goto L14;
                    case 196: goto L14;
                    case 197: goto L14;
                    case 198: goto L14;
                    case 199: goto L14;
                    case 200: goto L14;
                    case 201: goto L14;
                    case 202: goto L14;
                    case 203: goto L14;
                    default: goto L12;
                }
            L12:
                r1 = 0
                return r1
            L14:
                r9 = r3
                goto L26
            L16:
                android.view.ViewRootImpl r1 = android.view.ViewRootImpl.this
                boolean r1 = r1.mFlipControllerFallbackKeys
                if (r1 == 0) goto L25
                goto L24
            L1d:
                android.view.ViewRootImpl r1 = android.view.ViewRootImpl.this
                boolean r1 = r1.mFlipControllerFallbackKeys
                if (r1 == 0) goto L24
                goto L25
            L24:
                r2 = r3
            L25:
                r9 = r2
            L26:
                android.view.ViewRootImpl r1 = android.view.ViewRootImpl.this
                android.view.KeyEvent r2 = new android.view.KeyEvent
                long r4 = r17.getDownTime()
                long r6 = r17.getEventTime()
                int r8 = r17.getAction()
                int r10 = r17.getRepeatCount()
                int r11 = r17.getMetaState()
                int r12 = r17.getDeviceId()
                int r13 = r17.getScanCode()
                int r3 = r17.getFlags()
                r14 = r3 | 1024(0x400, float:1.435E-42)
                int r15 = r17.getSource()
                r3 = r2
                r3.<init>(r4, r6, r8, r9, r10, r11, r12, r13, r14, r15)
                r1.enqueueInputEvent(r2)
                r1 = 1
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.SyntheticKeyHandler.process(android.view.KeyEvent):boolean");
        }
    }

    private static boolean isNavigationKey(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == 61 || keyCode == 62 || keyCode == 66 || keyCode == 92 || keyCode == 93 || keyCode == 122 || keyCode == 123) {
            return true;
        }
        switch (keyCode) {
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                return true;
            default:
                return false;
        }
    }

    private static boolean isTypingKey(KeyEvent keyEvent) {
        return keyEvent.getUnicodeChar() > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkForLeavingTouchModeAndConsume(KeyEvent keyEvent) {
        if (!this.mAttachInfo.mInTouchMode) {
            return false;
        }
        int action = keyEvent.getAction();
        if ((action != 0 && action != 2) || (keyEvent.getFlags() & 4) != 0) {
            return false;
        }
        if (isNavigationKey(keyEvent)) {
            return ensureTouchMode(false);
        }
        if (isTypingKey(keyEvent)) {
            ensureTouchMode(false);
        }
        return false;
    }

    void setLocalDragState(Object obj) {
        this.mLocalDragState = obj;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDragEvent(DragEvent dragEvent) {
        if (this.mView != null && this.mAdded) {
            int i = dragEvent.mAction;
            if (i == 6) {
                this.mView.dispatchDragEvent(dragEvent);
            } else {
                if (i == 1) {
                    this.mCurrentDragView = null;
                    this.mDragDescription = dragEvent.mClipDescription;
                } else {
                    dragEvent.mClipDescription = this.mDragDescription;
                }
                if (i == 2 || i == 3) {
                    this.mDragPoint.set(dragEvent.mX, dragEvent.mY);
                    CompatibilityInfo.Translator translator = this.mTranslator;
                    if (translator != null) {
                        translator.translatePointInScreenToAppWindow(this.mDragPoint);
                    }
                    int i2 = this.mCurScrollY;
                    if (i2 != 0) {
                        this.mDragPoint.offset(0.0f, i2);
                    }
                    dragEvent.mX = this.mDragPoint.x;
                    dragEvent.mY = this.mDragPoint.y;
                }
                View view = this.mCurrentDragView;
                boolean zDispatchDragEvent = this.mView.dispatchDragEvent(dragEvent);
                if (view != this.mCurrentDragView) {
                    if (view != null) {
                        try {
                            this.mWindowSession.dragRecipientExited(this.mWindow);
                        } catch (RemoteException unused) {
                            Slog.e(TAG, "Unable to note drag target change");
                        }
                    }
                    if (this.mCurrentDragView != null) {
                        this.mWindowSession.dragRecipientEntered(this.mWindow);
                    }
                }
                if (i == 3) {
                    this.mDragDescription = null;
                    try {
                        Log.i(TAG, "Reporting drop result: " + zDispatchDragEvent);
                        this.mWindowSession.reportDropResult(this.mWindow, zDispatchDragEvent);
                    } catch (RemoteException unused2) {
                        Log.e(TAG, "Unable to report drop result");
                    }
                }
                if (i == 4) {
                    setLocalDragState(null);
                }
            }
        }
        dragEvent.recycle();
    }

    public void handleDispatchSystemUiVisibilityChanged(SystemUiVisibilityInfo systemUiVisibilityInfo) {
        int i;
        if (this.mSeq != systemUiVisibilityInfo.seq) {
            this.mSeq = systemUiVisibilityInfo.seq;
            this.mAttachInfo.mForceReportNewAttributes = true;
            scheduleTraversals();
        }
        if (this.mView == null) {
            return;
        }
        if (systemUiVisibilityInfo.localChanges != 0) {
            this.mView.updateLocalSystemUiVisibility(systemUiVisibilityInfo.localValue, systemUiVisibilityInfo.localChanges);
        }
        if (this.mAttachInfo == null || (i = systemUiVisibilityInfo.globalVisibility & 7) == this.mAttachInfo.mGlobalSystemUiVisibility) {
            return;
        }
        this.mAttachInfo.mGlobalSystemUiVisibility = i;
        this.mView.dispatchSystemUiVisibilityChanged(i);
    }

    public void handleDispatchDoneAnimating() {
        if (this.mWindowsAnimating) {
            this.mWindowsAnimating = false;
            if (!this.mDirty.isEmpty() || this.mIsAnimating || this.mFullRedrawNeeded) {
                scheduleTraversals();
            }
        }
    }

    public void getLastTouchPoint(Point point) {
        point.x = (int) this.mLastTouchPoint.x;
        point.y = (int) this.mLastTouchPoint.y;
    }

    public void setDragFocus(View view) {
        if (this.mCurrentDragView != view) {
            this.mCurrentDragView = view;
        }
    }

    private AudioManager getAudioManager() {
        View view = this.mView;
        if (view == null) {
            throw new IllegalStateException("getAudioManager called when there is no mView");
        }
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) view.getContext().getSystemService(Context.AUDIO_SERVICE);
        }
        return this.mAudioManager;
    }

    public AccessibilityInteractionController getAccessibilityInteractionController() {
        if (this.mView == null) {
            throw new IllegalStateException("getAccessibilityInteractionController called when there is no mView");
        }
        if (this.mAccessibilityInteractionController == null) {
            this.mAccessibilityInteractionController = new AccessibilityInteractionController(this);
        }
        return this.mAccessibilityInteractionController;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private int relayoutWindow(WindowManager.LayoutParams layoutParams, int i, boolean z) throws RemoteException {
        Object[] objArr;
        float f = this.mAttachInfo.mApplicationScale;
        if (layoutParams == null || this.mTranslator == null) {
            objArr = false;
        } else {
            layoutParams.backup();
            this.mTranslator.translateWindowLayout(layoutParams);
            objArr = true;
        }
        this.mPendingConfiguration.seq = 0;
        if (layoutParams != null && this.mOrigWindowType != layoutParams.type && this.mTargetSdkVersion < 14) {
            Slog.w(TAG, "Window type can not be changed after the window is added; ignoring change of " + this.mView);
            layoutParams.type = this.mOrigWindowType;
        }
        int iRelayout = this.mWindowSession.relayout(this.mWindow, this.mSeq, layoutParams, (int) ((this.mView.getMeasuredWidth() * f) + 0.5f), (int) ((this.mView.getMeasuredHeight() * f) + 0.5f), i, z ? 1 : 0, this.mWinFrame, this.mPendingOverscanInsets, this.mPendingContentInsets, this.mPendingVisibleInsets, this.mPendingConfiguration, this.mSurface);
        if (objArr != false) {
            layoutParams.restore();
        }
        CompatibilityInfo.Translator translator = this.mTranslator;
        if (translator != null) {
            translator.translateRectInScreenToAppWinFrame(this.mWinFrame);
            this.mTranslator.translateRectInScreenToAppWindow(this.mPendingOverscanInsets);
            this.mTranslator.translateRectInScreenToAppWindow(this.mPendingContentInsets);
            this.mTranslator.translateRectInScreenToAppWindow(this.mPendingVisibleInsets);
        }
        return iRelayout;
    }

    @Override // android.view.View.AttachInfo.Callbacks
    public void playSoundEffect(int i) {
        checkThread();
        if (this.mMediaDisabled) {
            return;
        }
        try {
            AudioManager audioManager = getAudioManager();
            if (i == 0) {
                audioManager.playSoundEffect(0);
                return;
            }
            if (i == 1) {
                audioManager.playSoundEffect(3);
                return;
            }
            if (i == 2) {
                audioManager.playSoundEffect(1);
            } else if (i == 3) {
                audioManager.playSoundEffect(4);
            } else {
                if (i == 4) {
                    audioManager.playSoundEffect(2);
                    return;
                }
                throw new IllegalArgumentException("unknown effect id " + i + " not defined in " + SoundEffectConstants.class.getCanonicalName());
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "FATAL EXCEPTION when attempting to play sound effect: " + e);
            e.printStackTrace();
        }
    }

    @Override // android.view.View.AttachInfo.Callbacks
    public boolean performHapticFeedback(int i, boolean z) {
        try {
            return this.mWindowSession.performHapticFeedback(this.mWindow, i, z);
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.ViewParent
    public View focusSearch(View view, int i) {
        checkThread();
        if (this.mView instanceof ViewGroup) {
            return FocusFinder.getInstance().findNextFocus((ViewGroup) this.mView, view, i);
        }
        return null;
    }

    public void debug() {
        this.mView.debug();
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String str2 = str + "  ";
        printWriter.print(str);
        printWriter.println("ViewRoot:");
        printWriter.print(str2);
        printWriter.print("mAdded=");
        printWriter.print(this.mAdded);
        printWriter.print(" mRemoved=");
        printWriter.println(this.mRemoved);
        printWriter.print(str2);
        printWriter.print("mConsumeBatchedInputScheduled=");
        printWriter.println(this.mConsumeBatchedInputScheduled);
        printWriter.print(str2);
        printWriter.print("mPendingInputEventCount=");
        printWriter.println(this.mPendingInputEventCount);
        printWriter.print(str2);
        printWriter.print("mProcessInputEventsScheduled=");
        printWriter.println(this.mProcessInputEventsScheduled);
        printWriter.print(str2);
        printWriter.print("mTraversalScheduled=");
        printWriter.print(this.mTraversalScheduled);
        if (this.mTraversalScheduled) {
            printWriter.print(" (barrier=");
            printWriter.print(this.mTraversalBarrier);
            printWriter.println(")");
        } else {
            printWriter.println();
        }
        this.mFirstInputStage.dump(str2, printWriter);
        this.mChoreographer.dump(str, printWriter);
        printWriter.print(str);
        printWriter.println("View Hierarchy:");
        dumpViewHierarchy(str2, printWriter, this.mView);
    }

    private void dumpViewHierarchy(String str, PrintWriter printWriter, View view) {
        ViewGroup viewGroup;
        int childCount;
        printWriter.print(str);
        if (view == null) {
            printWriter.println("null");
            return;
        }
        printWriter.println(view.toString());
        if ((view instanceof ViewGroup) && (childCount = (viewGroup = (ViewGroup) view).getChildCount()) > 0) {
            String str2 = str + "  ";
            for (int i = 0; i < childCount; i++) {
                dumpViewHierarchy(str2, printWriter, viewGroup.getChildAt(i));
            }
        }
    }

    public void dumpGfxInfo(int[] iArr) {
        iArr[1] = 0;
        iArr[0] = 0;
        View view = this.mView;
        if (view != null) {
            getGfxInfo(view, iArr);
        }
    }

    private static void getGfxInfo(View view, int[] iArr) {
        DisplayList displayList = view.mDisplayList;
        iArr[0] = iArr[0] + 1;
        if (displayList != null) {
            iArr[1] = iArr[1] + displayList.getSize();
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                getGfxInfo(viewGroup.getChildAt(i), iArr);
            }
        }
    }

    boolean die(boolean z) {
        if (z && !this.mIsInTraversal) {
            doDie();
            return false;
        }
        if (!this.mIsDrawing) {
            destroyHardwareRenderer();
        } else {
            Log.e(TAG, "Attempting to destroy the window while drawing!\n  window=" + this + ", title=" + ((Object) this.mWindowAttributes.getTitle()));
        }
        this.mHandler.sendEmptyMessage(3);
        return true;
    }

    void doDie() {
        checkThread();
        synchronized (this) {
            if (this.mRemoved) {
                return;
            }
            boolean z = true;
            this.mRemoved = true;
            if (this.mAdded) {
                dispatchDetachedFromWindow();
            }
            if (this.mAdded && !this.mFirst) {
                invalidateDisplayLists();
                destroyHardwareRenderer();
                View view = this.mView;
                if (view != null) {
                    int visibility = view.getVisibility();
                    if (this.mViewVisibility == visibility) {
                        z = false;
                    }
                    if (this.mWindowAttributesChanged || z) {
                        try {
                            if ((relayoutWindow(this.mWindowAttributes, visibility, false) & 2) != 0) {
                                this.mWindowSession.finishDrawing(this.mWindow);
                            }
                        } catch (RemoteException unused) {
                        }
                    }
                    this.mSurface.release();
                }
            }
            this.mAdded = false;
            WindowManagerGlobal.getInstance().doRemoveView(this);
        }
    }

    public void requestUpdateConfiguration(Configuration configuration) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(18, configuration));
    }

    public void loadSystemProperties() {
        this.mHandler.post(new Runnable() { // from class: android.view.ViewRootImpl.4
            @Override // java.lang.Runnable
            public void run() {
                ViewRootImpl.this.mProfileRendering = SystemProperties.getBoolean(ViewRootImpl.PROPERTY_PROFILE_RENDERING, false);
                ViewRootImpl viewRootImpl = ViewRootImpl.this;
                viewRootImpl.profileRendering(viewRootImpl.mAttachInfo.mHasWindowFocus);
                ViewRootImpl.this.mMediaDisabled = SystemProperties.getBoolean(ViewRootImpl.PROPERTY_MEDIA_DISABLED, false);
                if (ViewRootImpl.this.mAttachInfo.mHardwareRenderer != null && ViewRootImpl.this.mAttachInfo.mHardwareRenderer.loadSystemProperties(ViewRootImpl.this.mHolder.getSurface())) {
                    ViewRootImpl.this.invalidate();
                }
                boolean z = SystemProperties.getBoolean(View.DEBUG_LAYOUT_PROPERTY, false);
                if (z != ViewRootImpl.this.mAttachInfo.mDebugLayout) {
                    ViewRootImpl.this.mAttachInfo.mDebugLayout = z;
                    if (ViewRootImpl.this.mHandler.hasMessages(23)) {
                        return;
                    }
                    ViewRootImpl.this.mHandler.sendEmptyMessageDelayed(23, 200L);
                }
            }
        });
    }

    private void destroyHardwareRenderer() {
        View.AttachInfo attachInfo = this.mAttachInfo;
        HardwareRenderer hardwareRenderer = attachInfo.mHardwareRenderer;
        if (hardwareRenderer != null) {
            View view = this.mView;
            if (view != null) {
                hardwareRenderer.destroyHardwareResources(view);
            }
            hardwareRenderer.destroy(true);
            hardwareRenderer.setRequested(false);
            attachInfo.mHardwareRenderer = null;
            attachInfo.mHardwareAccelerated = false;
        }
    }

    public void dispatchFinishInputConnection(InputConnection inputConnection) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(12, inputConnection));
    }

    public void dispatchResized(Rect rect, Rect rect2, Rect rect3, Rect rect4, boolean z, Configuration configuration) {
        Message messageObtainMessage = this.mHandler.obtainMessage(z ? 5 : 4);
        CompatibilityInfo.Translator translator = this.mTranslator;
        if (translator != null) {
            translator.translateRectInScreenToAppWindow(rect);
            this.mTranslator.translateRectInScreenToAppWindow(rect2);
            this.mTranslator.translateRectInScreenToAppWindow(rect3);
            this.mTranslator.translateRectInScreenToAppWindow(rect4);
        }
        SomeArgs someArgsObtain = SomeArgs.obtain();
        boolean z2 = Binder.getCallingPid() == Process.myPid();
        if (z2) {
            rect = new Rect(rect);
        }
        someArgsObtain.arg1 = rect;
        if (z2) {
            rect3 = new Rect(rect3);
        }
        someArgsObtain.arg2 = rect3;
        if (z2) {
            rect4 = new Rect(rect4);
        }
        someArgsObtain.arg3 = rect4;
        if (z2 && configuration != null) {
            configuration = new Configuration(configuration);
        }
        someArgsObtain.arg4 = configuration;
        if (z2) {
            rect2 = new Rect(rect2);
        }
        someArgsObtain.arg5 = rect2;
        messageObtainMessage.obj = someArgsObtain;
        this.mHandler.sendMessage(messageObtainMessage);
    }

    public void dispatchMoved(int i, int i2) {
        if (this.mTranslator != null) {
            PointF pointF = new PointF(i, i2);
            this.mTranslator.translatePointInScreenToAppWindow(pointF);
            i = (int) (((double) pointF.x) + 0.5d);
            i2 = (int) (((double) pointF.y) + 0.5d);
        }
        this.mHandler.sendMessage(this.mHandler.obtainMessage(24, i, i2));
    }

    private static final class QueuedInputEvent {
        public static final int FLAG_DEFERRED = 2;
        public static final int FLAG_DELIVER_POST_IME = 1;
        public static final int FLAG_FINISHED = 4;
        public static final int FLAG_FINISHED_HANDLED = 8;
        public static final int FLAG_RESYNTHESIZED = 16;
        public InputEvent mEvent;
        public int mFlags;
        public QueuedInputEvent mNext;
        public InputEventReceiver mReceiver;

        private QueuedInputEvent() {
        }

        public boolean shouldSkipIme() {
            if ((this.mFlags & 1) != 0) {
                return true;
            }
            InputEvent inputEvent = this.mEvent;
            return (inputEvent instanceof MotionEvent) && inputEvent.isFromSource(2);
        }
    }

    private QueuedInputEvent obtainQueuedInputEvent(InputEvent inputEvent, InputEventReceiver inputEventReceiver, int i) {
        QueuedInputEvent queuedInputEvent = this.mQueuedInputEventPool;
        if (queuedInputEvent != null) {
            this.mQueuedInputEventPoolSize--;
            this.mQueuedInputEventPool = queuedInputEvent.mNext;
            queuedInputEvent.mNext = null;
        } else {
            queuedInputEvent = new QueuedInputEvent();
        }
        queuedInputEvent.mEvent = inputEvent;
        queuedInputEvent.mReceiver = inputEventReceiver;
        queuedInputEvent.mFlags = i;
        return queuedInputEvent;
    }

    private void recycleQueuedInputEvent(QueuedInputEvent queuedInputEvent) {
        queuedInputEvent.mEvent = null;
        queuedInputEvent.mReceiver = null;
        int i = this.mQueuedInputEventPoolSize;
        if (i < 10) {
            this.mQueuedInputEventPoolSize = i + 1;
            queuedInputEvent.mNext = this.mQueuedInputEventPool;
            this.mQueuedInputEventPool = queuedInputEvent;
        }
    }

    void enqueueInputEvent(InputEvent inputEvent) {
        enqueueInputEvent(inputEvent, null, 0, false);
    }

    void enqueueInputEvent(InputEvent inputEvent, InputEventReceiver inputEventReceiver, int i, boolean z) {
        QueuedInputEvent queuedInputEventObtainQueuedInputEvent = obtainQueuedInputEvent(inputEvent, inputEventReceiver, i);
        QueuedInputEvent queuedInputEvent = this.mPendingInputEventTail;
        if (queuedInputEvent == null) {
            this.mPendingInputEventHead = queuedInputEventObtainQueuedInputEvent;
            this.mPendingInputEventTail = queuedInputEventObtainQueuedInputEvent;
        } else {
            queuedInputEvent.mNext = queuedInputEventObtainQueuedInputEvent;
            this.mPendingInputEventTail = queuedInputEventObtainQueuedInputEvent;
        }
        int i2 = this.mPendingInputEventCount + 1;
        this.mPendingInputEventCount = i2;
        Trace.traceCounter(4L, this.mPendingInputEventQueueLengthCounterName, i2);
        if (z) {
            doProcessInputEvents();
        } else {
            scheduleProcessInputEvents();
        }
    }

    private void scheduleProcessInputEvents() {
        if (this.mProcessInputEventsScheduled) {
            return;
        }
        this.mProcessInputEventsScheduled = true;
        Message messageObtainMessage = this.mHandler.obtainMessage(19);
        messageObtainMessage.setAsynchronous(true);
        this.mHandler.sendMessage(messageObtainMessage);
    }

    void doProcessInputEvents() {
        while (true) {
            QueuedInputEvent queuedInputEvent = this.mPendingInputEventHead;
            if (queuedInputEvent == null) {
                break;
            }
            QueuedInputEvent queuedInputEvent2 = queuedInputEvent.mNext;
            this.mPendingInputEventHead = queuedInputEvent2;
            if (queuedInputEvent2 == null) {
                this.mPendingInputEventTail = null;
            }
            queuedInputEvent.mNext = null;
            int i = this.mPendingInputEventCount - 1;
            this.mPendingInputEventCount = i;
            Trace.traceCounter(4L, this.mPendingInputEventQueueLengthCounterName, i);
            deliverInputEvent(queuedInputEvent);
        }
        if (this.mProcessInputEventsScheduled) {
            this.mProcessInputEventsScheduled = false;
            this.mHandler.removeMessages(19);
        }
    }

    private void deliverInputEvent(QueuedInputEvent queuedInputEvent) {
        Trace.traceBegin(8L, "deliverInputEvent");
        try {
            InputEventConsistencyVerifier inputEventConsistencyVerifier = this.mInputEventConsistencyVerifier;
            if (inputEventConsistencyVerifier != null) {
                inputEventConsistencyVerifier.onInputEvent(queuedInputEvent.mEvent, 0);
            }
            InputStage inputStage = queuedInputEvent.shouldSkipIme() ? this.mFirstPostImeInputStage : this.mFirstInputStage;
            if (inputStage != null) {
                inputStage.deliver(queuedInputEvent);
            } else {
                finishInputEvent(queuedInputEvent);
            }
        } finally {
            Trace.traceEnd(8L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishInputEvent(QueuedInputEvent queuedInputEvent) {
        if (queuedInputEvent.mReceiver != null) {
            queuedInputEvent.mReceiver.finishInputEvent(queuedInputEvent.mEvent, (queuedInputEvent.mFlags & 8) != 0);
        } else {
            queuedInputEvent.mEvent.recycleIfNeededAfterDispatch();
        }
        recycleQueuedInputEvent(queuedInputEvent);
    }

    static boolean isTerminalInputEvent(InputEvent inputEvent) {
        if (inputEvent instanceof KeyEvent) {
            return ((KeyEvent) inputEvent).getAction() == 1;
        }
        int action = ((MotionEvent) inputEvent).getAction();
        return action == 1 || action == 3 || action == 10;
    }

    void scheduleConsumeBatchedInput() {
        if (this.mConsumeBatchedInputScheduled) {
            return;
        }
        this.mConsumeBatchedInputScheduled = true;
        this.mChoreographer.postCallback(0, this.mConsumedBatchedInputRunnable, null);
    }

    void unscheduleConsumeBatchedInput() {
        if (this.mConsumeBatchedInputScheduled) {
            this.mConsumeBatchedInputScheduled = false;
            this.mChoreographer.removeCallbacks(0, this.mConsumedBatchedInputRunnable, null);
        }
    }

    void doConsumeBatchedInput(long j) {
        if (this.mConsumeBatchedInputScheduled) {
            this.mConsumeBatchedInputScheduled = false;
            WindowInputEventReceiver windowInputEventReceiver = this.mInputEventReceiver;
            if (windowInputEventReceiver != null && windowInputEventReceiver.consumeBatchedInputEvents(j)) {
                scheduleConsumeBatchedInput();
            }
            doProcessInputEvents();
        }
    }

    final class TraversalRunnable implements Runnable {
        TraversalRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ViewRootImpl.this.doTraversal();
        }
    }

    final class WindowInputEventReceiver extends InputEventReceiver {
        public WindowInputEventReceiver(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }

        @Override // android.view.InputEventReceiver
        public void onInputEvent(InputEvent inputEvent) {
            ViewRootImpl.this.enqueueInputEvent(inputEvent, this, 0, true);
        }

        @Override // android.view.InputEventReceiver
        public void onBatchedInputEventPending() {
            ViewRootImpl.this.scheduleConsumeBatchedInput();
        }

        @Override // android.view.InputEventReceiver
        public void dispose() {
            ViewRootImpl.this.unscheduleConsumeBatchedInput();
            super.dispose();
        }
    }

    final class ConsumeBatchedInputRunnable implements Runnable {
        ConsumeBatchedInputRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ViewRootImpl viewRootImpl = ViewRootImpl.this;
            viewRootImpl.doConsumeBatchedInput(viewRootImpl.mChoreographer.getFrameTimeNanos());
        }
    }

    final class InvalidateOnAnimationRunnable implements Runnable {
        private boolean mPosted;
        private View.AttachInfo.InvalidateInfo[] mTempViewRects;
        private View[] mTempViews;
        private final ArrayList<View> mViews = new ArrayList<>();
        private final ArrayList<View.AttachInfo.InvalidateInfo> mViewRects = new ArrayList<>();

        InvalidateOnAnimationRunnable() {
        }

        public void addView(View view) {
            synchronized (this) {
                this.mViews.add(view);
                postIfNeededLocked();
            }
        }

        public void addViewRect(View.AttachInfo.InvalidateInfo invalidateInfo) {
            synchronized (this) {
                this.mViewRects.add(invalidateInfo);
                postIfNeededLocked();
            }
        }

        public void removeView(View view) {
            synchronized (this) {
                this.mViews.remove(view);
                int size = this.mViewRects.size();
                while (true) {
                    int i = size - 1;
                    if (size <= 0) {
                        break;
                    }
                    View.AttachInfo.InvalidateInfo invalidateInfo = this.mViewRects.get(i);
                    if (invalidateInfo.target == view) {
                        this.mViewRects.remove(i);
                        invalidateInfo.recycle();
                    }
                    size = i;
                }
                if (this.mPosted && this.mViews.isEmpty() && this.mViewRects.isEmpty()) {
                    ViewRootImpl.this.mChoreographer.removeCallbacks(1, this, null);
                    this.mPosted = false;
                }
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            int i;
            int size;
            int size2;
            synchronized (this) {
                this.mPosted = false;
                size = this.mViews.size();
                if (size != 0) {
                    ArrayList<View> arrayList = this.mViews;
                    View[] viewArr = this.mTempViews;
                    if (viewArr == null) {
                        viewArr = new View[size];
                    }
                    this.mTempViews = (View[]) arrayList.toArray(viewArr);
                    this.mViews.clear();
                }
                size2 = this.mViewRects.size();
                if (size2 != 0) {
                    ArrayList<View.AttachInfo.InvalidateInfo> arrayList2 = this.mViewRects;
                    View.AttachInfo.InvalidateInfo[] invalidateInfoArr = this.mTempViewRects;
                    if (invalidateInfoArr == null) {
                        invalidateInfoArr = new View.AttachInfo.InvalidateInfo[size2];
                    }
                    this.mTempViewRects = (View.AttachInfo.InvalidateInfo[]) arrayList2.toArray(invalidateInfoArr);
                    this.mViewRects.clear();
                }
            }
            for (int i2 = 0; i2 < size; i2++) {
                this.mTempViews[i2].invalidate();
                this.mTempViews[i2] = null;
            }
            for (i = 0; i < size2; i++) {
                View.AttachInfo.InvalidateInfo invalidateInfo = this.mTempViewRects[i];
                invalidateInfo.target.invalidate(invalidateInfo.left, invalidateInfo.top, invalidateInfo.right, invalidateInfo.bottom);
                invalidateInfo.recycle();
            }
        }

        private void postIfNeededLocked() {
            if (this.mPosted) {
                return;
            }
            ViewRootImpl.this.mChoreographer.postCallback(1, this, null);
            this.mPosted = true;
        }
    }

    public void dispatchInvalidateDelayed(View view, long j) {
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1, view), j);
    }

    public void dispatchInvalidateRectDelayed(View.AttachInfo.InvalidateInfo invalidateInfo, long j) {
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(2, invalidateInfo), j);
    }

    public void dispatchInvalidateOnAnimation(View view) {
        this.mInvalidateOnAnimationRunnable.addView(view);
    }

    public void dispatchInvalidateRectOnAnimation(View.AttachInfo.InvalidateInfo invalidateInfo) {
        this.mInvalidateOnAnimationRunnable.addViewRect(invalidateInfo);
    }

    public void enqueueDisplayList(DisplayList displayList) {
        this.mDisplayLists.add(displayList);
    }

    public void cancelInvalidate(View view) {
        this.mHandler.removeMessages(1, view);
        this.mHandler.removeMessages(2, view);
        this.mInvalidateOnAnimationRunnable.removeView(view);
    }

    public void dispatchInputEvent(InputEvent inputEvent) {
        Message messageObtainMessage = this.mHandler.obtainMessage(7, inputEvent);
        messageObtainMessage.setAsynchronous(true);
        this.mHandler.sendMessage(messageObtainMessage);
    }

    public void dispatchKeyFromIme(KeyEvent keyEvent) {
        Message messageObtainMessage = this.mHandler.obtainMessage(11, keyEvent);
        messageObtainMessage.setAsynchronous(true);
        this.mHandler.sendMessage(messageObtainMessage);
    }

    public void dispatchUnhandledKey(KeyEvent keyEvent) {
        KeyCharacterMap.FallbackAction fallbackAction;
        if ((keyEvent.getFlags() & 1024) != 0 || (fallbackAction = keyEvent.getKeyCharacterMap().getFallbackAction(keyEvent.getKeyCode(), keyEvent.getMetaState())) == null) {
            return;
        }
        KeyEvent keyEventObtain = KeyEvent.obtain(keyEvent.getDownTime(), keyEvent.getEventTime(), keyEvent.getAction(), fallbackAction.keyCode, keyEvent.getRepeatCount(), fallbackAction.metaState, keyEvent.getDeviceId(), keyEvent.getScanCode(), keyEvent.getFlags() | 1024, keyEvent.getSource(), null);
        fallbackAction.recycle();
        dispatchInputEvent(keyEventObtain);
    }

    public void dispatchAppVisibility(boolean z) {
        Message messageObtainMessage = this.mHandler.obtainMessage(8);
        messageObtainMessage.arg1 = z ? 1 : 0;
        this.mHandler.sendMessage(messageObtainMessage);
    }

    public void dispatchScreenStateChange(boolean z) {
        Message messageObtainMessage = this.mHandler.obtainMessage(20);
        messageObtainMessage.arg1 = z ? 1 : 0;
        this.mHandler.sendMessage(messageObtainMessage);
    }

    public void dispatchGetNewSurface() {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(9));
    }

    public void windowFocusChanged(boolean z, boolean z2) {
        Message messageObtain = Message.obtain();
        messageObtain.what = 6;
        messageObtain.arg1 = z ? 1 : 0;
        messageObtain.arg2 = z2 ? 1 : 0;
        this.mHandler.sendMessage(messageObtain);
    }

    public void dispatchCloseSystemDialogs(String str) {
        Message messageObtain = Message.obtain();
        messageObtain.what = 14;
        messageObtain.obj = str;
        this.mHandler.sendMessage(messageObtain);
    }

    public void dispatchDragEvent(DragEvent dragEvent) {
        int i;
        if (dragEvent.getAction() == 2) {
            i = 16;
            this.mHandler.removeMessages(16);
        } else {
            i = 15;
        }
        this.mHandler.sendMessage(this.mHandler.obtainMessage(i, dragEvent));
    }

    public void dispatchSystemUiVisibilityChanged(int i, int i2, int i3, int i4) {
        SystemUiVisibilityInfo systemUiVisibilityInfo = new SystemUiVisibilityInfo();
        systemUiVisibilityInfo.seq = i;
        systemUiVisibilityInfo.globalVisibility = i2;
        systemUiVisibilityInfo.localValue = i3;
        systemUiVisibilityInfo.localChanges = i4;
        ViewRootHandler viewRootHandler = this.mHandler;
        viewRootHandler.sendMessage(viewRootHandler.obtainMessage(17, systemUiVisibilityInfo));
    }

    public void dispatchDoneAnimating() {
        this.mHandler.sendEmptyMessage(22);
    }

    public void dispatchCheckFocus() {
        if (this.mHandler.hasMessages(13)) {
            return;
        }
        this.mHandler.sendEmptyMessage(13);
    }

    private void postSendWindowContentChangedCallback(View view, int i) {
        if (this.mSendWindowContentChangedAccessibilityEvent == null) {
            this.mSendWindowContentChangedAccessibilityEvent = new SendWindowContentChangedAccessibilityEvent();
        }
        this.mSendWindowContentChangedAccessibilityEvent.runOrPost(view, i);
    }

    private void removeSendWindowContentChangedCallback() {
        SendWindowContentChangedAccessibilityEvent sendWindowContentChangedAccessibilityEvent = this.mSendWindowContentChangedAccessibilityEvent;
        if (sendWindowContentChangedAccessibilityEvent != null) {
            this.mHandler.removeCallbacks(sendWindowContentChangedAccessibilityEvent);
        }
    }

    @Override // android.view.ViewParent
    public boolean requestSendAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        AccessibilityNodeProvider accessibilityNodeProvider;
        if (this.mView == null) {
            return false;
        }
        int eventType = accessibilityEvent.getEventType();
        if (eventType == 32768) {
            long sourceNodeId = accessibilityEvent.getSourceNodeId();
            View viewFindViewByAccessibilityId = this.mView.findViewByAccessibilityId(AccessibilityNodeInfo.getAccessibilityViewId(sourceNodeId));
            if (viewFindViewByAccessibilityId != null && (accessibilityNodeProvider = viewFindViewByAccessibilityId.getAccessibilityNodeProvider()) != null) {
                setAccessibilityFocus(viewFindViewByAccessibilityId, accessibilityNodeProvider.createAccessibilityNodeInfo(AccessibilityNodeInfo.getVirtualDescendantId(sourceNodeId)));
            }
        } else if (eventType == 65536) {
            View viewFindViewByAccessibilityId2 = this.mView.findViewByAccessibilityId(AccessibilityNodeInfo.getAccessibilityViewId(accessibilityEvent.getSourceNodeId()));
            if (viewFindViewByAccessibilityId2 != null && viewFindViewByAccessibilityId2.getAccessibilityNodeProvider() != null) {
                setAccessibilityFocus(null, null);
            }
        }
        this.mAccessibilityManager.sendAccessibilityEvent(accessibilityEvent);
        return true;
    }

    @Override // android.view.ViewParent
    public void notifySubtreeAccessibilityStateChanged(View view, View view2, int i) {
        postSendWindowContentChangedCallback(view2, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getCommonPredecessor(View view, View view2) {
        if (this.mAttachInfo != null) {
            if (this.mTempHashSet == null) {
                this.mTempHashSet = new HashSet<>();
            }
            HashSet<View> hashSet = this.mTempHashSet;
            hashSet.clear();
            while (view != null) {
                hashSet.add(view);
                Object obj = view.mParent;
                view = obj instanceof View ? (View) obj : null;
            }
            while (view2 != null) {
                if (hashSet.contains(view2)) {
                    hashSet.clear();
                    return view2;
                }
                Object obj2 = view2.mParent;
                view2 = obj2 instanceof View ? (View) obj2 : null;
            }
            hashSet.clear();
        }
        return null;
    }

    void checkThread() {
        if (this.mThread != Thread.currentThread()) {
            throw new CalledFromWrongThreadException("Only the original thread that created a view hierarchy can touch its views.");
        }
    }

    @Override // android.view.ViewParent
    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
        boolean zScrollToRectOrFocus = scrollToRectOrFocus(rect, z);
        if (rect != null) {
            this.mTempRect.set(rect);
            this.mTempRect.offset(0, -this.mCurScrollY);
            this.mTempRect.offset(this.mAttachInfo.mWindowLeft, this.mAttachInfo.mWindowTop);
            try {
                this.mWindowSession.onRectangleOnScreenRequested(this.mWindow, this.mTempRect, z);
            } catch (RemoteException unused) {
            }
        }
        return zScrollToRectOrFocus;
    }

    void changeCanvasOpacity(boolean z) {
        Log.d(TAG, "changeCanvasOpacity: opaque=" + z);
    }

    class TakenSurfaceHolder extends BaseSurfaceHolder {
        public void onRelayoutContainer() {
        }

        TakenSurfaceHolder() {
        }

        public boolean onAllowLockCanvas() {
            return ViewRootImpl.this.mDrawingAllowed;
        }

        public void setFormat(int i) {
            ViewRootImpl.this.mView.setSurfaceFormat(i);
        }

        public void setType(int i) {
            ViewRootImpl.this.mView.setSurfaceType(i);
        }

        public void onUpdateSurface() {
            throw new IllegalStateException("Shouldn't be here");
        }

        public boolean isCreating() {
            return ViewRootImpl.this.mIsCreating;
        }

        public void setFixedSize(int i, int i2) {
            throw new UnsupportedOperationException("Currently only support sizing from layout");
        }

        public void setKeepScreenOn(boolean z) {
            ViewRootImpl.this.mView.setSurfaceKeepScreenOn(z);
        }
    }

    static class W extends IWindow.Stub {
        private final WeakReference<ViewRootImpl> mViewAncestor;
        private final IWindowSession mWindowSession;

        W(ViewRootImpl viewRootImpl) {
            this.mViewAncestor = new WeakReference<>(viewRootImpl);
            this.mWindowSession = viewRootImpl.mWindowSession;
        }

        @Override // android.view.IWindow
        public void resized(Rect rect, Rect rect2, Rect rect3, Rect rect4, boolean z, Configuration configuration) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchResized(rect, rect2, rect3, rect4, z, configuration);
            }
        }

        @Override // android.view.IWindow
        public void moved(int i, int i2) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchMoved(i, i2);
            }
        }

        @Override // android.view.IWindow
        public void dispatchAppVisibility(boolean z) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchAppVisibility(z);
            }
        }

        @Override // android.view.IWindow
        public void dispatchScreenState(boolean z) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchScreenStateChange(z);
            }
        }

        @Override // android.view.IWindow
        public void dispatchGetNewSurface() {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchGetNewSurface();
            }
        }

        @Override // android.view.IWindow
        public void windowFocusChanged(boolean z, boolean z2) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.windowFocusChanged(z, z2);
            }
        }

        private static int checkCallingPermission(String str) {
            try {
                return ActivityManagerNative.getDefault().checkPermission(str, Binder.getCallingPid(), Binder.getCallingUid());
            } catch (RemoteException unused) {
                return -1;
            }
        }

        @Override // android.view.IWindow
        public void executeCommand(String str, String str2, ParcelFileDescriptor parcelFileDescriptor) throws Throwable {
            View view;
            ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream;
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl == null || (view = viewRootImpl.mView) == null) {
                return;
            }
            if (checkCallingPermission(Manifest.permission.DUMP) != 0) {
                throw new SecurityException("Insufficient permissions to invoke executeCommand() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            }
            ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream2 = null;
            try {
                try {
                    try {
                        autoCloseOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptor);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e2) {
                    e = e2;
                }
            } catch (Throwable th) {
                th = th;
            }
            try {
                ViewDebug.dispatchCommand(view, str, str2, autoCloseOutputStream);
                autoCloseOutputStream.close();
            } catch (IOException e3) {
                e = e3;
                autoCloseOutputStream2 = autoCloseOutputStream;
                e.printStackTrace();
                if (autoCloseOutputStream2 != null) {
                    autoCloseOutputStream2.close();
                }
            } catch (Throwable th2) {
                th = th2;
                autoCloseOutputStream2 = autoCloseOutputStream;
                if (autoCloseOutputStream2 != null) {
                    try {
                        autoCloseOutputStream2.close();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                    }
                }
                throw th;
            }
        }

        @Override // android.view.IWindow
        public void closeSystemDialogs(String str) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchCloseSystemDialogs(str);
            }
        }

        @Override // android.view.IWindow
        public void dispatchWallpaperOffsets(float f, float f2, float f3, float f4, boolean z) {
            if (z) {
                try {
                    this.mWindowSession.wallpaperOffsetsComplete(asBinder());
                } catch (RemoteException unused) {
                }
            }
        }

        @Override // android.view.IWindow
        public void dispatchWallpaperCommand(String str, int i, int i2, int i3, Bundle bundle, boolean z) {
            if (z) {
                try {
                    this.mWindowSession.wallpaperCommandComplete(asBinder(), null);
                } catch (RemoteException unused) {
                }
            }
        }

        @Override // android.view.IWindow
        public void dispatchDragEvent(DragEvent dragEvent) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchDragEvent(dragEvent);
            }
        }

        @Override // android.view.IWindow
        public void dispatchSystemUiVisibilityChanged(int i, int i2, int i3, int i4) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchSystemUiVisibilityChanged(i, i2, i3, i4);
            }
        }

        @Override // android.view.IWindow
        public void doneAnimating() {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchDoneAnimating();
            }
        }
    }

    public static final class CalledFromWrongThreadException extends AndroidRuntimeException {
        public CalledFromWrongThreadException(String str) {
            super(str);
        }
    }

    static RunQueue getRunQueue() {
        ThreadLocal<RunQueue> threadLocal = sRunQueues;
        RunQueue runQueue = threadLocal.get();
        if (runQueue != null) {
            return runQueue;
        }
        RunQueue runQueue2 = new RunQueue();
        threadLocal.set(runQueue2);
        return runQueue2;
    }

    static final class RunQueue {
        private final ArrayList<HandlerAction> mActions = new ArrayList<>();

        RunQueue() {
        }

        void post(Runnable runnable) {
            postDelayed(runnable, 0L);
        }

        void postDelayed(Runnable runnable, long j) {
            HandlerAction handlerAction = new HandlerAction();
            handlerAction.action = runnable;
            handlerAction.delay = j;
            synchronized (this.mActions) {
                this.mActions.add(handlerAction);
            }
        }

        void removeCallbacks(Runnable runnable) {
            HandlerAction handlerAction = new HandlerAction();
            handlerAction.action = runnable;
            synchronized (this.mActions) {
                while (this.mActions.remove(handlerAction)) {
                }
            }
        }

        void executeActions(Handler handler) {
            synchronized (this.mActions) {
                ArrayList<HandlerAction> arrayList = this.mActions;
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    HandlerAction handlerAction = arrayList.get(i);
                    handler.postDelayed(handlerAction.action, handlerAction.delay);
                }
                arrayList.clear();
            }
        }

        private static class HandlerAction {
            Runnable action;
            long delay;

            private HandlerAction() {
            }

            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null || getClass() != obj.getClass()) {
                    return false;
                }
                Runnable runnable = this.action;
                Runnable runnable2 = ((HandlerAction) obj).action;
                if (runnable != null) {
                    if (runnable.equals(runnable2)) {
                        return true;
                    }
                } else if (runnable2 == null) {
                    return true;
                }
                return false;
            }

            public int hashCode() {
                Runnable runnable = this.action;
                int iHashCode = runnable != null ? runnable.hashCode() : 0;
                long j = this.delay;
                return (iHashCode * 31) + ((int) (j ^ (j >>> 32)));
            }
        }
    }

    final class AccessibilityInteractionConnectionManager implements AccessibilityManager.AccessibilityStateChangeListener {
        AccessibilityInteractionConnectionManager() {
        }

        @Override // android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener
        public void onAccessibilityStateChanged(boolean z) {
            if (z) {
                ensureConnection();
                if (ViewRootImpl.this.mAttachInfo == null || !ViewRootImpl.this.mAttachInfo.mHasWindowFocus) {
                    return;
                }
                ViewRootImpl.this.mView.sendAccessibilityEvent(32);
                View viewFindFocus = ViewRootImpl.this.mView.findFocus();
                if (viewFindFocus == null || viewFindFocus == ViewRootImpl.this.mView) {
                    return;
                }
                viewFindFocus.sendAccessibilityEvent(8);
                return;
            }
            ensureNoConnection();
            ViewRootImpl.this.mHandler.obtainMessage(21).sendToTarget();
        }

        public void ensureConnection() {
            if (ViewRootImpl.this.mAttachInfo != null) {
                if (ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId != -1) {
                    return;
                }
                ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId = ViewRootImpl.this.mAccessibilityManager.addAccessibilityInteractionConnection(ViewRootImpl.this.mWindow, new AccessibilityInteractionConnection(ViewRootImpl.this));
            }
        }

        public void ensureNoConnection() {
            if (ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId != -1) {
                ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId = -1;
                ViewRootImpl.this.mAccessibilityManager.removeAccessibilityInteractionConnection(ViewRootImpl.this.mWindow);
            }
        }
    }

    static final class AccessibilityInteractionConnection extends IAccessibilityInteractionConnection.Stub {
        private final WeakReference<ViewRootImpl> mViewRootImpl;

        AccessibilityInteractionConnection(ViewRootImpl viewRootImpl) {
            this.mViewRootImpl = new WeakReference<>(viewRootImpl);
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findAccessibilityNodeInfoByAccessibilityId(long j, int i, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i2, int i3, long j2, MagnificationSpec magnificationSpec) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().findAccessibilityNodeInfoByAccessibilityIdClientThread(j, i, iAccessibilityInteractionConnectionCallback, i2, i3, j2, magnificationSpec);
            } else {
                try {
                    iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfosResult(null, i);
                } catch (RemoteException unused) {
                }
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void performAccessibilityAction(long j, int i, Bundle bundle, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i3, int i4, long j2) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().performAccessibilityActionClientThread(j, i, bundle, i2, iAccessibilityInteractionConnectionCallback, i3, i4, j2);
            } else {
                try {
                    iAccessibilityInteractionConnectionCallback.setPerformAccessibilityActionResult(false, i2);
                } catch (RemoteException unused) {
                }
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findAccessibilityNodeInfosByViewId(long j, String str, int i, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i2, int i3, long j2, MagnificationSpec magnificationSpec) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().findAccessibilityNodeInfosByViewIdClientThread(j, str, i, iAccessibilityInteractionConnectionCallback, i2, i3, j2, magnificationSpec);
            } else {
                try {
                    iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfoResult(null, i);
                } catch (RemoteException unused) {
                }
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findAccessibilityNodeInfosByText(long j, String str, int i, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i2, int i3, long j2, MagnificationSpec magnificationSpec) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().findAccessibilityNodeInfosByTextClientThread(j, str, i, iAccessibilityInteractionConnectionCallback, i2, i3, j2, magnificationSpec);
            } else {
                try {
                    iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfosResult(null, i);
                } catch (RemoteException unused) {
                }
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findFocus(long j, int i, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i3, int i4, long j2, MagnificationSpec magnificationSpec) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().findFocusClientThread(j, i, i2, iAccessibilityInteractionConnectionCallback, i3, i4, j2, magnificationSpec);
            } else {
                try {
                    iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfoResult(null, i2);
                } catch (RemoteException unused) {
                }
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void focusSearch(long j, int i, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i3, int i4, long j2, MagnificationSpec magnificationSpec) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().focusSearchClientThread(j, i, i2, iAccessibilityInteractionConnectionCallback, i3, i4, j2, magnificationSpec);
            } else {
                try {
                    iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfoResult(null, i2);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    private class SendWindowContentChangedAccessibilityEvent implements Runnable {
        private int mChangeTypes;
        public long mLastEventTimeMillis;
        public View mSource;

        private SendWindowContentChangedAccessibilityEvent() {
            this.mChangeTypes = 0;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (AccessibilityManager.getInstance(ViewRootImpl.this.mContext).isEnabled()) {
                this.mLastEventTimeMillis = SystemClock.uptimeMillis();
                AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain();
                accessibilityEventObtain.setEventType(2048);
                accessibilityEventObtain.setContentChangeTypes(this.mChangeTypes);
                this.mSource.sendAccessibilityEventUnchecked(accessibilityEventObtain);
            } else {
                this.mLastEventTimeMillis = 0L;
            }
            this.mSource.resetSubtreeAccessibilityStateChanged();
            this.mSource = null;
            this.mChangeTypes = 0;
        }

        public void runOrPost(View view, int i) {
            View view2 = this.mSource;
            if (view2 != null) {
                View commonPredecessor = ViewRootImpl.this.getCommonPredecessor(view2, view);
                if (commonPredecessor != null) {
                    view = commonPredecessor;
                }
                this.mSource = view;
                this.mChangeTypes |= i;
                return;
            }
            this.mSource = view;
            this.mChangeTypes = i;
            long jUptimeMillis = SystemClock.uptimeMillis() - this.mLastEventTimeMillis;
            long sendRecurringAccessibilityEventsInterval = ViewConfiguration.getSendRecurringAccessibilityEventsInterval();
            if (jUptimeMillis >= sendRecurringAccessibilityEventsInterval) {
                this.mSource.removeCallbacks(this);
                run();
            } else {
                this.mSource.postDelayed(this, sendRecurringAccessibilityEventsInterval - jUptimeMillis);
            }
        }
    }
}
