package android.view;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import com.android.internal.view.BaseIWindow;
import com.google.android.material.badge.BadgeDrawable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/* JADX INFO: loaded from: classes.dex */
public class SurfaceView extends View {
    private static final boolean DEBUG = false;
    static final int GET_NEW_SURFACE_MSG = 2;
    static final int KEEP_SCREEN_ON_MSG = 1;
    private static final String TAG = "SurfaceView";
    static final int UPDATE_WINDOW_MSG = 3;
    static boolean mAdapterMode = false;
    static int mGameSurfaceHeight = 0;
    static int mGameSurfaceWidth = 0;
    public static boolean mGameloftNeedCompat = false;
    public static boolean mMotionEventMayNeedAdjust = false;
    static int mScreenHeight = 0;
    static int mScreenOrientation = -1;
    static int mScreenWidth;
    final ArrayList<SurfaceHolder.Callback> mCallbacks;
    final Configuration mConfiguration;
    final Rect mContentInsets;
    private final ViewTreeObserver.OnPreDrawListener mDrawListener;
    boolean mDrawingStopped;
    int mFormat;
    private boolean mGlobalListenersAdded;
    final Handler mHandler;
    boolean mHaveFrame;
    int mHeight;
    boolean mIsCreating;
    long mLastLockTime;
    int mLastSurfaceHeight;
    int mLastSurfaceWidth;
    final WindowManager.LayoutParams mLayout;
    int mLeft;
    final int[] mLocation;
    final Surface mNewSurface;
    final Rect mOverscanInsets;
    boolean mReportDrawNeeded;
    int mRequestedFormat;
    int mRequestedHeight;
    boolean mRequestedVisible;
    int mRequestedWidth;
    final ViewTreeObserver.OnScrollChangedListener mScrollChangedListener;
    IWindowSession mSession;
    final Surface mSurface;
    boolean mSurfaceCreated;
    final Rect mSurfaceFrame;
    private final SurfaceHolder mSurfaceHolder;
    final ReentrantLock mSurfaceLock;
    int mTop;
    private CompatibilityInfo.Translator mTranslator;
    boolean mUpdateWindowNeeded;
    boolean mViewVisibility;
    boolean mVisible;
    final Rect mVisibleInsets;
    int mWidth;
    final Rect mWinFrame;
    MyWindow mWindow;
    int mWindowType;
    boolean mWindowVisibility;

    public SurfaceView(Context context) {
        super(context);
        this.mCallbacks = new ArrayList<>();
        this.mLocation = new int[2];
        this.mSurfaceLock = new ReentrantLock();
        this.mSurface = new Surface();
        this.mNewSurface = new Surface();
        this.mDrawingStopped = true;
        this.mLayout = new WindowManager.LayoutParams();
        this.mVisibleInsets = new Rect();
        this.mWinFrame = new Rect();
        this.mOverscanInsets = new Rect();
        this.mContentInsets = new Rect();
        this.mConfiguration = new Configuration();
        this.mWindowType = 1001;
        this.mIsCreating = false;
        this.mHandler = new Handler() { // from class: android.view.SurfaceView.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 1) {
                    SurfaceView.this.setKeepScreenOn(message.arg1 != 0);
                } else if (i == 2) {
                    SurfaceView.this.handleGetNewSurface();
                } else {
                    if (i != 3) {
                        return;
                    }
                    SurfaceView.this.updateWindow(false, false);
                }
            }
        };
        this.mScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() { // from class: android.view.SurfaceView.2
            @Override // android.view.ViewTreeObserver.OnScrollChangedListener
            public void onScrollChanged() {
                SurfaceView.this.updateWindow(false, false);
            }
        };
        this.mRequestedVisible = false;
        this.mWindowVisibility = false;
        this.mViewVisibility = false;
        this.mRequestedWidth = -1;
        this.mRequestedHeight = -1;
        this.mRequestedFormat = 4;
        this.mHaveFrame = false;
        this.mSurfaceCreated = false;
        this.mLastLockTime = 0L;
        this.mVisible = false;
        this.mLeft = -1;
        this.mTop = -1;
        this.mWidth = -1;
        this.mHeight = -1;
        this.mFormat = -1;
        this.mSurfaceFrame = new Rect();
        this.mLastSurfaceWidth = -1;
        this.mLastSurfaceHeight = -1;
        this.mDrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: android.view.SurfaceView.3
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                SurfaceView surfaceView = SurfaceView.this;
                surfaceView.mHaveFrame = surfaceView.getWidth() > 0 && SurfaceView.this.getHeight() > 0;
                SurfaceView.this.updateWindow(false, false);
                return true;
            }
        };
        this.mSurfaceHolder = new SurfaceHolder() { // from class: android.view.SurfaceView.4
            private static final String LOG_TAG = "SurfaceHolder";

            @Override // android.view.SurfaceHolder
            @Deprecated
            public void setType(int i) {
            }

            @Override // android.view.SurfaceHolder
            public boolean isCreating() {
                return SurfaceView.this.mIsCreating;
            }

            @Override // android.view.SurfaceHolder
            public void addCallback(SurfaceHolder.Callback callback) {
                synchronized (SurfaceView.this.mCallbacks) {
                    if (!SurfaceView.this.mCallbacks.contains(callback)) {
                        SurfaceView.this.mCallbacks.add(callback);
                    }
                }
            }

            @Override // android.view.SurfaceHolder
            public void removeCallback(SurfaceHolder.Callback callback) {
                synchronized (SurfaceView.this.mCallbacks) {
                    SurfaceView.this.mCallbacks.remove(callback);
                }
            }

            @Override // android.view.SurfaceHolder
            public void setFixedSize(int i, int i2) {
                if (SurfaceView.this.mRequestedWidth == i && SurfaceView.this.mRequestedHeight == i2) {
                    return;
                }
                SurfaceView.this.mRequestedWidth = i;
                SurfaceView.this.mRequestedHeight = i2;
                SurfaceView.this.requestLayout();
            }

            @Override // android.view.SurfaceHolder
            public void setSizeFromLayout() {
                if (SurfaceView.this.mRequestedWidth == -1 && SurfaceView.this.mRequestedHeight == -1) {
                    return;
                }
                SurfaceView surfaceView = SurfaceView.this;
                surfaceView.mRequestedHeight = -1;
                surfaceView.mRequestedWidth = -1;
                SurfaceView.this.requestLayout();
            }

            @Override // android.view.SurfaceHolder
            public void setFormat(int i) {
                if (i == -1) {
                    i = 4;
                }
                SurfaceView.this.mRequestedFormat = i;
                if (SurfaceView.this.mWindow != null) {
                    SurfaceView.this.updateWindow(false, false);
                }
            }

            @Override // android.view.SurfaceHolder
            public void setKeepScreenOn(boolean z) {
                Message messageObtainMessage = SurfaceView.this.mHandler.obtainMessage(1);
                messageObtainMessage.arg1 = z ? 1 : 0;
                SurfaceView.this.mHandler.sendMessage(messageObtainMessage);
            }

            @Override // android.view.SurfaceHolder
            public Canvas lockCanvas() {
                return internalLockCanvas(null);
            }

            @Override // android.view.SurfaceHolder
            public Canvas lockCanvas(Rect rect) {
                return internalLockCanvas(rect);
            }

            private final Canvas internalLockCanvas(Rect rect) {
                Canvas canvasLockCanvas;
                SurfaceView.this.mSurfaceLock.lock();
                if (SurfaceView.this.mDrawingStopped || SurfaceView.this.mWindow == null) {
                    canvasLockCanvas = null;
                } else {
                    try {
                        canvasLockCanvas = SurfaceView.this.mSurface.lockCanvas(rect);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Exception locking surface", e);
                        canvasLockCanvas = null;
                    }
                }
                if (canvasLockCanvas != null) {
                    SurfaceView.this.mLastLockTime = SystemClock.uptimeMillis();
                    return canvasLockCanvas;
                }
                long jUptimeMillis = SystemClock.uptimeMillis();
                long j = SurfaceView.this.mLastLockTime + 100;
                if (j > jUptimeMillis) {
                    try {
                        Thread.sleep(j - jUptimeMillis);
                    } catch (InterruptedException unused) {
                    }
                    jUptimeMillis = SystemClock.uptimeMillis();
                }
                SurfaceView.this.mLastLockTime = jUptimeMillis;
                SurfaceView.this.mSurfaceLock.unlock();
                return null;
            }

            @Override // android.view.SurfaceHolder
            public void unlockCanvasAndPost(Canvas canvas) {
                SurfaceView.this.mSurface.unlockCanvasAndPost(canvas);
                SurfaceView.this.mSurfaceLock.unlock();
            }

            @Override // android.view.SurfaceHolder
            public Surface getSurface() {
                return SurfaceView.this.mSurface;
            }

            @Override // android.view.SurfaceHolder
            public Rect getSurfaceFrame() {
                return SurfaceView.this.mSurfaceFrame;
            }
        };
        init();
    }

    public SurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCallbacks = new ArrayList<>();
        this.mLocation = new int[2];
        this.mSurfaceLock = new ReentrantLock();
        this.mSurface = new Surface();
        this.mNewSurface = new Surface();
        this.mDrawingStopped = true;
        this.mLayout = new WindowManager.LayoutParams();
        this.mVisibleInsets = new Rect();
        this.mWinFrame = new Rect();
        this.mOverscanInsets = new Rect();
        this.mContentInsets = new Rect();
        this.mConfiguration = new Configuration();
        this.mWindowType = 1001;
        this.mIsCreating = false;
        this.mHandler = new Handler() { // from class: android.view.SurfaceView.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 1) {
                    SurfaceView.this.setKeepScreenOn(message.arg1 != 0);
                } else if (i == 2) {
                    SurfaceView.this.handleGetNewSurface();
                } else {
                    if (i != 3) {
                        return;
                    }
                    SurfaceView.this.updateWindow(false, false);
                }
            }
        };
        this.mScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() { // from class: android.view.SurfaceView.2
            @Override // android.view.ViewTreeObserver.OnScrollChangedListener
            public void onScrollChanged() {
                SurfaceView.this.updateWindow(false, false);
            }
        };
        this.mRequestedVisible = false;
        this.mWindowVisibility = false;
        this.mViewVisibility = false;
        this.mRequestedWidth = -1;
        this.mRequestedHeight = -1;
        this.mRequestedFormat = 4;
        this.mHaveFrame = false;
        this.mSurfaceCreated = false;
        this.mLastLockTime = 0L;
        this.mVisible = false;
        this.mLeft = -1;
        this.mTop = -1;
        this.mWidth = -1;
        this.mHeight = -1;
        this.mFormat = -1;
        this.mSurfaceFrame = new Rect();
        this.mLastSurfaceWidth = -1;
        this.mLastSurfaceHeight = -1;
        this.mDrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: android.view.SurfaceView.3
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                SurfaceView surfaceView = SurfaceView.this;
                surfaceView.mHaveFrame = surfaceView.getWidth() > 0 && SurfaceView.this.getHeight() > 0;
                SurfaceView.this.updateWindow(false, false);
                return true;
            }
        };
        this.mSurfaceHolder = new SurfaceHolder() { // from class: android.view.SurfaceView.4
            private static final String LOG_TAG = "SurfaceHolder";

            @Override // android.view.SurfaceHolder
            @Deprecated
            public void setType(int i) {
            }

            @Override // android.view.SurfaceHolder
            public boolean isCreating() {
                return SurfaceView.this.mIsCreating;
            }

            @Override // android.view.SurfaceHolder
            public void addCallback(SurfaceHolder.Callback callback) {
                synchronized (SurfaceView.this.mCallbacks) {
                    if (!SurfaceView.this.mCallbacks.contains(callback)) {
                        SurfaceView.this.mCallbacks.add(callback);
                    }
                }
            }

            @Override // android.view.SurfaceHolder
            public void removeCallback(SurfaceHolder.Callback callback) {
                synchronized (SurfaceView.this.mCallbacks) {
                    SurfaceView.this.mCallbacks.remove(callback);
                }
            }

            @Override // android.view.SurfaceHolder
            public void setFixedSize(int i, int i2) {
                if (SurfaceView.this.mRequestedWidth == i && SurfaceView.this.mRequestedHeight == i2) {
                    return;
                }
                SurfaceView.this.mRequestedWidth = i;
                SurfaceView.this.mRequestedHeight = i2;
                SurfaceView.this.requestLayout();
            }

            @Override // android.view.SurfaceHolder
            public void setSizeFromLayout() {
                if (SurfaceView.this.mRequestedWidth == -1 && SurfaceView.this.mRequestedHeight == -1) {
                    return;
                }
                SurfaceView surfaceView = SurfaceView.this;
                surfaceView.mRequestedHeight = -1;
                surfaceView.mRequestedWidth = -1;
                SurfaceView.this.requestLayout();
            }

            @Override // android.view.SurfaceHolder
            public void setFormat(int i) {
                if (i == -1) {
                    i = 4;
                }
                SurfaceView.this.mRequestedFormat = i;
                if (SurfaceView.this.mWindow != null) {
                    SurfaceView.this.updateWindow(false, false);
                }
            }

            @Override // android.view.SurfaceHolder
            public void setKeepScreenOn(boolean z) {
                Message messageObtainMessage = SurfaceView.this.mHandler.obtainMessage(1);
                messageObtainMessage.arg1 = z ? 1 : 0;
                SurfaceView.this.mHandler.sendMessage(messageObtainMessage);
            }

            @Override // android.view.SurfaceHolder
            public Canvas lockCanvas() {
                return internalLockCanvas(null);
            }

            @Override // android.view.SurfaceHolder
            public Canvas lockCanvas(Rect rect) {
                return internalLockCanvas(rect);
            }

            private final Canvas internalLockCanvas(Rect rect) {
                Canvas canvasLockCanvas;
                SurfaceView.this.mSurfaceLock.lock();
                if (SurfaceView.this.mDrawingStopped || SurfaceView.this.mWindow == null) {
                    canvasLockCanvas = null;
                } else {
                    try {
                        canvasLockCanvas = SurfaceView.this.mSurface.lockCanvas(rect);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Exception locking surface", e);
                        canvasLockCanvas = null;
                    }
                }
                if (canvasLockCanvas != null) {
                    SurfaceView.this.mLastLockTime = SystemClock.uptimeMillis();
                    return canvasLockCanvas;
                }
                long jUptimeMillis = SystemClock.uptimeMillis();
                long j = SurfaceView.this.mLastLockTime + 100;
                if (j > jUptimeMillis) {
                    try {
                        Thread.sleep(j - jUptimeMillis);
                    } catch (InterruptedException unused) {
                    }
                    jUptimeMillis = SystemClock.uptimeMillis();
                }
                SurfaceView.this.mLastLockTime = jUptimeMillis;
                SurfaceView.this.mSurfaceLock.unlock();
                return null;
            }

            @Override // android.view.SurfaceHolder
            public void unlockCanvasAndPost(Canvas canvas) {
                SurfaceView.this.mSurface.unlockCanvasAndPost(canvas);
                SurfaceView.this.mSurfaceLock.unlock();
            }

            @Override // android.view.SurfaceHolder
            public Surface getSurface() {
                return SurfaceView.this.mSurface;
            }

            @Override // android.view.SurfaceHolder
            public Rect getSurfaceFrame() {
                return SurfaceView.this.mSurfaceFrame;
            }
        };
        init();
    }

    public SurfaceView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCallbacks = new ArrayList<>();
        this.mLocation = new int[2];
        this.mSurfaceLock = new ReentrantLock();
        this.mSurface = new Surface();
        this.mNewSurface = new Surface();
        this.mDrawingStopped = true;
        this.mLayout = new WindowManager.LayoutParams();
        this.mVisibleInsets = new Rect();
        this.mWinFrame = new Rect();
        this.mOverscanInsets = new Rect();
        this.mContentInsets = new Rect();
        this.mConfiguration = new Configuration();
        this.mWindowType = 1001;
        this.mIsCreating = false;
        this.mHandler = new Handler() { // from class: android.view.SurfaceView.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i2 = message.what;
                if (i2 == 1) {
                    SurfaceView.this.setKeepScreenOn(message.arg1 != 0);
                } else if (i2 == 2) {
                    SurfaceView.this.handleGetNewSurface();
                } else {
                    if (i2 != 3) {
                        return;
                    }
                    SurfaceView.this.updateWindow(false, false);
                }
            }
        };
        this.mScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() { // from class: android.view.SurfaceView.2
            @Override // android.view.ViewTreeObserver.OnScrollChangedListener
            public void onScrollChanged() {
                SurfaceView.this.updateWindow(false, false);
            }
        };
        this.mRequestedVisible = false;
        this.mWindowVisibility = false;
        this.mViewVisibility = false;
        this.mRequestedWidth = -1;
        this.mRequestedHeight = -1;
        this.mRequestedFormat = 4;
        this.mHaveFrame = false;
        this.mSurfaceCreated = false;
        this.mLastLockTime = 0L;
        this.mVisible = false;
        this.mLeft = -1;
        this.mTop = -1;
        this.mWidth = -1;
        this.mHeight = -1;
        this.mFormat = -1;
        this.mSurfaceFrame = new Rect();
        this.mLastSurfaceWidth = -1;
        this.mLastSurfaceHeight = -1;
        this.mDrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: android.view.SurfaceView.3
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                SurfaceView surfaceView = SurfaceView.this;
                surfaceView.mHaveFrame = surfaceView.getWidth() > 0 && SurfaceView.this.getHeight() > 0;
                SurfaceView.this.updateWindow(false, false);
                return true;
            }
        };
        this.mSurfaceHolder = new SurfaceHolder() { // from class: android.view.SurfaceView.4
            private static final String LOG_TAG = "SurfaceHolder";

            @Override // android.view.SurfaceHolder
            @Deprecated
            public void setType(int i2) {
            }

            @Override // android.view.SurfaceHolder
            public boolean isCreating() {
                return SurfaceView.this.mIsCreating;
            }

            @Override // android.view.SurfaceHolder
            public void addCallback(SurfaceHolder.Callback callback) {
                synchronized (SurfaceView.this.mCallbacks) {
                    if (!SurfaceView.this.mCallbacks.contains(callback)) {
                        SurfaceView.this.mCallbacks.add(callback);
                    }
                }
            }

            @Override // android.view.SurfaceHolder
            public void removeCallback(SurfaceHolder.Callback callback) {
                synchronized (SurfaceView.this.mCallbacks) {
                    SurfaceView.this.mCallbacks.remove(callback);
                }
            }

            @Override // android.view.SurfaceHolder
            public void setFixedSize(int i2, int i22) {
                if (SurfaceView.this.mRequestedWidth == i2 && SurfaceView.this.mRequestedHeight == i22) {
                    return;
                }
                SurfaceView.this.mRequestedWidth = i2;
                SurfaceView.this.mRequestedHeight = i22;
                SurfaceView.this.requestLayout();
            }

            @Override // android.view.SurfaceHolder
            public void setSizeFromLayout() {
                if (SurfaceView.this.mRequestedWidth == -1 && SurfaceView.this.mRequestedHeight == -1) {
                    return;
                }
                SurfaceView surfaceView = SurfaceView.this;
                surfaceView.mRequestedHeight = -1;
                surfaceView.mRequestedWidth = -1;
                SurfaceView.this.requestLayout();
            }

            @Override // android.view.SurfaceHolder
            public void setFormat(int i2) {
                if (i2 == -1) {
                    i2 = 4;
                }
                SurfaceView.this.mRequestedFormat = i2;
                if (SurfaceView.this.mWindow != null) {
                    SurfaceView.this.updateWindow(false, false);
                }
            }

            @Override // android.view.SurfaceHolder
            public void setKeepScreenOn(boolean z) {
                Message messageObtainMessage = SurfaceView.this.mHandler.obtainMessage(1);
                messageObtainMessage.arg1 = z ? 1 : 0;
                SurfaceView.this.mHandler.sendMessage(messageObtainMessage);
            }

            @Override // android.view.SurfaceHolder
            public Canvas lockCanvas() {
                return internalLockCanvas(null);
            }

            @Override // android.view.SurfaceHolder
            public Canvas lockCanvas(Rect rect) {
                return internalLockCanvas(rect);
            }

            private final Canvas internalLockCanvas(Rect rect) {
                Canvas canvasLockCanvas;
                SurfaceView.this.mSurfaceLock.lock();
                if (SurfaceView.this.mDrawingStopped || SurfaceView.this.mWindow == null) {
                    canvasLockCanvas = null;
                } else {
                    try {
                        canvasLockCanvas = SurfaceView.this.mSurface.lockCanvas(rect);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Exception locking surface", e);
                        canvasLockCanvas = null;
                    }
                }
                if (canvasLockCanvas != null) {
                    SurfaceView.this.mLastLockTime = SystemClock.uptimeMillis();
                    return canvasLockCanvas;
                }
                long jUptimeMillis = SystemClock.uptimeMillis();
                long j = SurfaceView.this.mLastLockTime + 100;
                if (j > jUptimeMillis) {
                    try {
                        Thread.sleep(j - jUptimeMillis);
                    } catch (InterruptedException unused) {
                    }
                    jUptimeMillis = SystemClock.uptimeMillis();
                }
                SurfaceView.this.mLastLockTime = jUptimeMillis;
                SurfaceView.this.mSurfaceLock.unlock();
                return null;
            }

            @Override // android.view.SurfaceHolder
            public void unlockCanvasAndPost(Canvas canvas) {
                SurfaceView.this.mSurface.unlockCanvasAndPost(canvas);
                SurfaceView.this.mSurfaceLock.unlock();
            }

            @Override // android.view.SurfaceHolder
            public Surface getSurface() {
                return SurfaceView.this.mSurface;
            }

            @Override // android.view.SurfaceHolder
            public Rect getSurfaceFrame() {
                return SurfaceView.this.mSurfaceFrame;
            }
        };
        init();
    }

    private void testGameloftNeedAdjust() {
        String packageName = getContext().getPackageName();
        int iIndexOf = packageName.indexOf("gameloft");
        if (iIndexOf <= 0) {
            return;
        }
        PackageManager packageManager = getContext().getPackageManager();
        mAdapterMode = Settings.System.getInt(getContext().getContentResolver(), Settings.System.DISPLAY_ADAPTION_ENABLE, 0) == 1;
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            if ((applicationInfo.flags & 524288) == 0 && (applicationInfo.flags & 2048) != 0 && (applicationInfo.flags & 4096) == 0 && iIndexOf >= 0 && mAdapterMode) {
                Display defaultDisplay = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                DisplayInfo displayInfo = new DisplayInfo();
                defaultDisplay.getDisplayInfo(displayInfo);
                mScreenOrientation = displayInfo.rotation;
                mScreenWidth = displayInfo.appWidth;
                mScreenHeight = displayInfo.appHeight;
                mGameSurfaceWidth = 800;
                mGameSurfaceHeight = 480;
                mGameloftNeedCompat = true;
            } else {
                mGameloftNeedCompat = false;
            }
        } catch (PackageManager.NameNotFoundException unused) {
            mGameloftNeedCompat = false;
        }
    }

    private void adjustWindowLayout() {
        if (mGameloftNeedCompat) {
            this.mLayout.x = (mScreenWidth - mGameSurfaceWidth) >> 1;
            this.mLayout.y = (mScreenHeight - mGameSurfaceHeight) >> 1;
            this.mLayout.width = mGameSurfaceWidth;
            this.mLayout.height = mGameSurfaceHeight;
        }
    }

    public static void adjustSurfaceViewMotion(MotionEvent motionEvent) {
        if (mMotionEventMayNeedAdjust) {
            motionEvent.offsetLocation((mGameSurfaceWidth - mScreenWidth) >> 1, (mGameSurfaceHeight - mScreenHeight) >> 1);
        }
    }

    private void init() {
        setWillNotDraw(true);
        mMotionEventMayNeedAdjust = false;
        if (this instanceof GLSurfaceView) {
            testGameloftNeedAdjust();
        }
    }

    public SurfaceHolder getHolder() {
        return this.mSurfaceHolder;
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mParent.requestTransparentRegion(this);
        this.mSession = getWindowSession();
        this.mLayout.token = getWindowToken();
        this.mLayout.setTitle(TAG);
        this.mViewVisibility = getVisibility() == 0;
        if (this.mGlobalListenersAdded) {
            return;
        }
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        viewTreeObserver.addOnScrollChangedListener(this.mScrollChangedListener);
        viewTreeObserver.addOnPreDrawListener(this.mDrawListener);
        this.mGlobalListenersAdded = true;
    }

    @Override // android.view.View
    protected void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        boolean z = i == 0;
        this.mWindowVisibility = z;
        this.mRequestedVisible = z && this.mViewVisibility;
        updateWindow(false, false);
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
        boolean z = i == 0;
        this.mViewVisibility = z;
        boolean z2 = this.mWindowVisibility && z;
        if (z2 != this.mRequestedVisible) {
            requestLayout();
        }
        this.mRequestedVisible = z2;
        updateWindow(false, false);
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
    @Override // android.view.View
    protected void onDetachedFromWindow() {
        if (this.mGlobalListenersAdded) {
            ViewTreeObserver viewTreeObserver = getViewTreeObserver();
            viewTreeObserver.removeOnScrollChangedListener(this.mScrollChangedListener);
            viewTreeObserver.removeOnPreDrawListener(this.mDrawListener);
            this.mGlobalListenersAdded = false;
        }
        this.mRequestedVisible = false;
        updateWindow(false, false);
        this.mHaveFrame = false;
        BaseIWindow baseIWindow = this.mWindow;
        if (baseIWindow != null) {
            try {
                this.mSession.remove(baseIWindow);
            } catch (RemoteException unused) {
            }
            this.mWindow = null;
        }
        this.mSession = null;
        this.mLayout.token = null;
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int i3 = this.mRequestedWidth;
        int iResolveSizeAndState = i3 >= 0 ? resolveSizeAndState(i3, i, 0) : getDefaultSize(0, i);
        int i4 = this.mRequestedHeight;
        setMeasuredDimension(iResolveSizeAndState, i4 >= 0 ? resolveSizeAndState(i4, i2, 0) : getDefaultSize(0, i2));
    }

    @Override // android.view.View
    protected boolean setFrame(int i, int i2, int i3, int i4) {
        boolean frame = super.setFrame(i, i2, i3, i4);
        updateWindow(false, false);
        return frame;
    }

    @Override // android.view.View
    public boolean gatherTransparentRegion(Region region) {
        if (this.mWindowType == 1000) {
            return super.gatherTransparentRegion(region);
        }
        boolean zGatherTransparentRegion = true;
        if ((this.mPrivateFlags & 128) == 0) {
            zGatherTransparentRegion = super.gatherTransparentRegion(region);
        } else if (region != null) {
            int width = getWidth();
            int height = getHeight();
            if (width > 0 && height > 0) {
                getLocationInWindow(this.mLocation);
                int[] iArr = this.mLocation;
                int i = iArr[0];
                int i2 = iArr[1];
                region.op(i, i2, i + width, i2 + height, Region.Op.UNION);
            }
        }
        if (PixelFormat.formatHasAlpha(this.mRequestedFormat)) {
            return false;
        }
        return zGatherTransparentRegion;
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        if (this.mWindowType != 1000 && (this.mPrivateFlags & 128) == 0) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
        super.draw(canvas);
    }

    @Override // android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.mWindowType != 1000 && (this.mPrivateFlags & 128) == 128) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
        super.dispatchDraw(canvas);
    }

    public void setZOrderMediaOverlay(boolean z) {
        this.mWindowType = z ? 1004 : 1001;
    }

    public void setZOrderOnTop(boolean z) {
        if (z) {
            this.mWindowType = 1000;
            this.mLayout.flags |= 131072;
        } else {
            this.mWindowType = 1001;
            this.mLayout.flags &= -131073;
        }
    }

    public void setSecure(boolean z) {
        if (z) {
            this.mLayout.flags |= 8192;
        } else {
            this.mLayout.flags &= -8193;
        }
    }

    public void setWindowType(int i) {
        this.mWindowType = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r12v1, types: [android.view.IWindow, android.view.SurfaceView$MyWindow] */
    /* JADX WARN: Type inference failed for: r13v1, types: [android.view.IWindow, android.view.SurfaceView$MyWindow] */
    /* JADX WARN: Type inference failed for: r15v2, types: [android.view.IWindow, android.view.SurfaceView$MyWindow] */
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
    public void updateWindow(boolean z, boolean z2) {
        int i;
        int i2;
        boolean z3;
        boolean z4;
        boolean z5;
        int iRelayout;
        if (this.mHaveFrame) {
            ViewRootImpl viewRootImpl = getViewRootImpl();
            if (viewRootImpl != null) {
                this.mTranslator = viewRootImpl.mTranslator;
            }
            CompatibilityInfo.Translator translator = this.mTranslator;
            if (translator != null) {
                this.mSurface.setCompatibilityTranslator(translator);
            }
            testGameloftNeedAdjust();
            int width = this.mRequestedWidth;
            if (width <= 0) {
                width = getWidth();
            }
            int height = this.mRequestedHeight;
            if (height <= 0) {
                height = getHeight();
            }
            getLocationInWindow(this.mLocation);
            boolean z6 = this.mWindow == null;
            int i3 = this.mFormat;
            int i4 = this.mRequestedFormat;
            boolean z7 = i3 != i4;
            boolean z8 = (this.mWidth == width && this.mHeight == height) ? false : true;
            boolean z9 = this.mVisible;
            boolean z10 = this.mRequestedVisible;
            boolean z11 = z9 != z10;
            if (!z && !z6 && !z7 && !z8 && !z11) {
                int i5 = this.mLeft;
                int[] iArr = this.mLocation;
                if (i5 == iArr[0] && this.mTop == iArr[1] && !this.mUpdateWindowNeeded && !this.mReportDrawNeeded && !z2) {
                    return;
                }
            }
            try {
                this.mVisible = z10;
                int[] iArr2 = this.mLocation;
                int i6 = iArr2[0];
                this.mLeft = i6;
                this.mTop = iArr2[1];
                this.mWidth = width;
                this.mHeight = height;
                this.mFormat = i4;
                this.mLayout.x = i6;
                this.mLayout.y = this.mTop;
                this.mLayout.width = getWidth();
                this.mLayout.height = getHeight();
                adjustWindowLayout();
                CompatibilityInfo.Translator translator2 = this.mTranslator;
                if (translator2 != null) {
                    translator2.translateLayoutParamsInAppWindowToScreen(this.mLayout);
                }
                this.mLayout.format = this.mRequestedFormat;
                this.mLayout.flags |= 16920;
                if (!getContext().getResources().getCompatibilityInfo().supportsScreen()) {
                    this.mLayout.privateFlags |= 128;
                }
                this.mLayout.privateFlags |= 64;
                if (this.mWindow == null) {
                    Display display = getDisplay();
                    this.mWindow = new MyWindow(this);
                    this.mLayout.type = this.mWindowType;
                    this.mLayout.gravity = BadgeDrawable.TOP_START;
                    IWindowSession iWindowSession = this.mSession;
                    ?? r15 = this.mWindow;
                    iWindowSession.addToDisplayWithoutInputChannel(r15, ((MyWindow) r15).mSeq, this.mLayout, this.mVisible ? 0 : 8, display.getDisplayId(), this.mContentInsets);
                }
                this.mSurfaceLock.lock();
                try {
                    this.mUpdateWindowNeeded = false;
                    boolean z12 = this.mReportDrawNeeded;
                    this.mReportDrawNeeded = false;
                    this.mDrawingStopped = !z10;
                    if (mGameloftNeedCompat) {
                        IWindowSession iWindowSession2 = this.mSession;
                        ?? r13 = this.mWindow;
                        i = width;
                        i2 = height;
                        z3 = z8;
                        iRelayout = iWindowSession2.relayout(r13, ((MyWindow) r13).mSeq, this.mLayout, 800, 480, z10 ? 0 : 8, 2, this.mWinFrame, this.mOverscanInsets, this.mContentInsets, this.mVisibleInsets, this.mConfiguration, this.mNewSurface);
                        z4 = z7;
                        z5 = z11;
                    } else {
                        i = width;
                        i2 = height;
                        z3 = z8;
                        IWindowSession iWindowSession3 = this.mSession;
                        ?? r12 = this.mWindow;
                        z4 = z7;
                        z5 = z11;
                        iRelayout = iWindowSession3.relayout(r12, ((MyWindow) r12).mSeq, this.mLayout, this.mWidth, this.mHeight, z10 ? 0 : 8, 2, this.mWinFrame, this.mOverscanInsets, this.mContentInsets, this.mVisibleInsets, this.mConfiguration, this.mNewSurface);
                    }
                    if ((iRelayout & 2) != 0) {
                        this.mReportDrawNeeded = true;
                    }
                    this.mSurfaceFrame.left = 0;
                    this.mSurfaceFrame.top = 0;
                    CompatibilityInfo.Translator translator3 = this.mTranslator;
                    if (translator3 == null) {
                        this.mSurfaceFrame.right = this.mWinFrame.width();
                        this.mSurfaceFrame.bottom = this.mWinFrame.height();
                    } else {
                        float f = translator3.applicationInvertedScale;
                        this.mSurfaceFrame.right = (int) ((this.mWinFrame.width() * f) + 0.5f);
                        this.mSurfaceFrame.bottom = (int) ((this.mWinFrame.height() * f) + 0.5f);
                    }
                    int i7 = this.mSurfaceFrame.right;
                    int i8 = this.mSurfaceFrame.bottom;
                    boolean z13 = (this.mLastSurfaceWidth == i7 && this.mLastSurfaceHeight == i8) ? false : true;
                    this.mLastSurfaceWidth = i7;
                    this.mLastSurfaceHeight = i8;
                    boolean z14 = z2 | z6 | z12;
                    SurfaceHolder.Callback[] surfaceCallbacks = null;
                    boolean z15 = (iRelayout & 4) != 0;
                    try {
                        if (this.mSurfaceCreated && (z15 || (!z10 && z5))) {
                            this.mSurfaceCreated = false;
                            if (this.mSurface.isValid()) {
                                surfaceCallbacks = getSurfaceCallbacks();
                                for (SurfaceHolder.Callback callback : surfaceCallbacks) {
                                    callback.surfaceDestroyed(this.mSurfaceHolder);
                                }
                            }
                        }
                        this.mSurface.transferFrom(this.mNewSurface);
                        if (z10 && this.mSurface.isValid()) {
                            if (!this.mSurfaceCreated && (z15 || z5)) {
                                this.mSurfaceCreated = true;
                                this.mIsCreating = true;
                                if (surfaceCallbacks == null) {
                                    surfaceCallbacks = getSurfaceCallbacks();
                                }
                                for (SurfaceHolder.Callback callback2 : surfaceCallbacks) {
                                    callback2.surfaceCreated(this.mSurfaceHolder);
                                }
                            }
                            if (z6 || z4 || z3 || z5 || z13) {
                                if (surfaceCallbacks == null) {
                                    surfaceCallbacks = getSurfaceCallbacks();
                                }
                                int length = surfaceCallbacks.length;
                                int i9 = 0;
                                while (i9 < length) {
                                    int i10 = i;
                                    int i11 = i2;
                                    surfaceCallbacks[i9].surfaceChanged(this.mSurfaceHolder, this.mFormat, i10, i11);
                                    i9++;
                                    i = i10;
                                    i2 = i11;
                                }
                            }
                            if (z14) {
                                if (surfaceCallbacks == null) {
                                    surfaceCallbacks = getSurfaceCallbacks();
                                }
                                for (SurfaceHolder.Callback callback3 : surfaceCallbacks) {
                                    if (callback3 instanceof SurfaceHolder.Callback2) {
                                        ((SurfaceHolder.Callback2) callback3).surfaceRedrawNeeded(this.mSurfaceHolder);
                                    }
                                }
                            }
                        }
                        this.mIsCreating = false;
                        if (z14) {
                            this.mSession.finishDrawing(this.mWindow);
                        }
                        this.mSession.performDeferredDestroy(this.mWindow);
                    } catch (Throwable th) {
                        this.mIsCreating = false;
                        if (z14) {
                            this.mSession.finishDrawing(this.mWindow);
                        }
                        this.mSession.performDeferredDestroy(this.mWindow);
                        throw th;
                    }
                } finally {
                    this.mSurfaceLock.unlock();
                }
            } catch (RemoteException unused) {
            }
        }
    }

    private SurfaceHolder.Callback[] getSurfaceCallbacks() {
        SurfaceHolder.Callback[] callbackArr;
        synchronized (this.mCallbacks) {
            callbackArr = new SurfaceHolder.Callback[this.mCallbacks.size()];
            this.mCallbacks.toArray(callbackArr);
        }
        return callbackArr;
    }

    void handleGetNewSurface() {
        updateWindow(false, false);
    }

    public boolean isFixedSize() {
        return (this.mRequestedWidth == -1 && this.mRequestedHeight == -1) ? false : true;
    }

    private static class MyWindow extends BaseIWindow {
        private final WeakReference<SurfaceView> mSurfaceView;
        int mCurWidth = -1;
        int mCurHeight = -1;

        public void dispatchAppVisibility(boolean z) {
        }

        public void executeCommand(String str, String str2, ParcelFileDescriptor parcelFileDescriptor) {
        }

        public MyWindow(SurfaceView surfaceView) {
            this.mSurfaceView = new WeakReference<>(surfaceView);
        }

        public void resized(Rect rect, Rect rect2, Rect rect3, Rect rect4, boolean z, Configuration configuration) {
            SurfaceView surfaceView = this.mSurfaceView.get();
            if (surfaceView != null) {
                surfaceView.mSurfaceLock.lock();
                try {
                    if (z) {
                        surfaceView.mUpdateWindowNeeded = true;
                        surfaceView.mReportDrawNeeded = true;
                        surfaceView.mHandler.sendEmptyMessage(3);
                    } else if (surfaceView.mWinFrame.width() != rect.width() || surfaceView.mWinFrame.height() != rect.height()) {
                        surfaceView.mUpdateWindowNeeded = true;
                        surfaceView.mHandler.sendEmptyMessage(3);
                    }
                } finally {
                    surfaceView.mSurfaceLock.unlock();
                }
            }
        }

        public void dispatchGetNewSurface() {
            SurfaceView surfaceView = this.mSurfaceView.get();
            if (surfaceView != null) {
                surfaceView.mHandler.sendMessage(surfaceView.mHandler.obtainMessage(2));
            }
        }

        public void windowFocusChanged(boolean z, boolean z2) {
            Log.w(SurfaceView.TAG, "Unexpected focus in surface: focus=" + z + ", touchEnabled=" + z2);
        }
    }
}
