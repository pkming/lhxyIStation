package android.service.wallpaper;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.service.wallpaper.IWallpaperEngine;
import android.service.wallpaper.IWallpaperService;
import android.util.Log;
import android.view.IWindowSession;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import com.android.internal.os.HandlerCaller;
import com.android.internal.view.BaseIWindow;
import com.android.internal.view.BaseSurfaceHolder;
import com.google.android.material.badge.BadgeDrawable;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public abstract class WallpaperService extends Service {
    static final boolean DEBUG = false;
    private static final int DO_ATTACH = 10;
    private static final int DO_DETACH = 20;
    private static final int DO_SET_DESIRED_SIZE = 30;
    private static final int MSG_TOUCH_EVENT = 10040;
    private static final int MSG_UPDATE_SURFACE = 10000;
    private static final int MSG_VISIBILITY_CHANGED = 10010;
    private static final int MSG_WALLPAPER_COMMAND = 10025;
    private static final int MSG_WALLPAPER_OFFSETS = 10020;
    private static final int MSG_WINDOW_MOVED = 10035;
    private static final int MSG_WINDOW_RESIZED = 10030;
    public static final String SERVICE_INTERFACE = "android.service.wallpaper.WallpaperService";
    public static final String SERVICE_META_DATA = "android.service.wallpaper";
    static final String TAG = "WallpaperService";
    private final ArrayList<Engine> mActiveEngines = new ArrayList<>();

    public abstract Engine onCreateEngine();

    static final class WallpaperCommand {
        String action;
        Bundle extras;
        boolean sync;
        int x;
        int y;
        int z;

        WallpaperCommand() {
        }
    }

    public class Engine {
        HandlerCaller mCaller;
        IWallpaperConnection mConnection;
        boolean mCreated;
        int mCurHeight;
        int mCurWidth;
        boolean mDestroyed;
        boolean mDrawingAllowed;
        boolean mFixedSizeAllowed;
        int mFormat;
        int mHeight;
        IWallpaperEngineWrapper mIWallpaperEngine;
        InputChannel mInputChannel;
        WallpaperInputEventReceiver mInputEventReceiver;
        boolean mIsCreating;
        boolean mOffsetMessageEnqueued;
        boolean mOffsetsChanged;
        MotionEvent mPendingMove;
        boolean mPendingSync;
        float mPendingXOffset;
        float mPendingXOffsetStep;
        float mPendingYOffset;
        float mPendingYOffsetStep;
        boolean mReportedVisible;
        IWindowSession mSession;
        boolean mSurfaceCreated;
        int mType;
        boolean mVisible;
        int mWidth;
        IBinder mWindowToken;
        boolean mInitializing = true;
        boolean mScreenOn = true;
        int mWindowFlags = 16;
        int mWindowPrivateFlags = 4;
        int mCurWindowFlags = 16;
        int mCurWindowPrivateFlags = 4;
        final Rect mVisibleInsets = new Rect();
        final Rect mWinFrame = new Rect();
        final Rect mOverscanInsets = new Rect();
        final Rect mContentInsets = new Rect();
        final Configuration mConfiguration = new Configuration();
        final WindowManager.LayoutParams mLayout = new WindowManager.LayoutParams();
        final Object mLock = new Object();
        final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: android.service.wallpaper.WallpaperService.Engine.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) throws Throwable {
                if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                    Engine.this.mScreenOn = true;
                    Engine.this.reportVisibility();
                } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                    Engine.this.mScreenOn = false;
                    Engine.this.reportVisibility();
                }
            }
        };
        final BaseSurfaceHolder mSurfaceHolder = new BaseSurfaceHolder() { // from class: android.service.wallpaper.WallpaperService.Engine.2
            {
                this.mRequestedFormat = 2;
            }

            public boolean onAllowLockCanvas() {
                return Engine.this.mDrawingAllowed;
            }

            public void onRelayoutContainer() {
                Engine.this.mCaller.sendMessage(Engine.this.mCaller.obtainMessage(10000));
            }

            public void onUpdateSurface() {
                Engine.this.mCaller.sendMessage(Engine.this.mCaller.obtainMessage(10000));
            }

            public boolean isCreating() {
                return Engine.this.mIsCreating;
            }

            public void setFixedSize(int i, int i2) {
                if (!Engine.this.mFixedSizeAllowed) {
                    throw new UnsupportedOperationException("Wallpapers currently only support sizing from layout");
                }
                super.setFixedSize(i, i2);
            }

            public void setKeepScreenOn(boolean z) {
                throw new UnsupportedOperationException("Wallpapers do not support keep screen on");
            }
        };
        final BaseIWindow mWindow = new BaseIWindow() { // from class: android.service.wallpaper.WallpaperService.Engine.3
            public void resized(Rect rect, Rect rect2, Rect rect3, Rect rect4, boolean z, Configuration configuration) {
                Engine.this.mCaller.sendMessage(Engine.this.mCaller.obtainMessageI(WallpaperService.MSG_WINDOW_RESIZED, z ? 1 : 0));
            }

            public void moved(int i, int i2) {
                Engine.this.mCaller.sendMessage(Engine.this.mCaller.obtainMessageII(WallpaperService.MSG_WINDOW_MOVED, i, i2));
            }

            public void dispatchAppVisibility(boolean z) {
                if (Engine.this.mIWallpaperEngine.mIsPreview) {
                    return;
                }
                Engine.this.mCaller.sendMessage(Engine.this.mCaller.obtainMessageI(10010, z ? 1 : 0));
            }

            public void dispatchWallpaperOffsets(float f, float f2, float f3, float f4, boolean z) {
                synchronized (Engine.this.mLock) {
                    Engine.this.mPendingXOffset = f;
                    Engine.this.mPendingYOffset = f2;
                    Engine.this.mPendingXOffsetStep = f3;
                    Engine.this.mPendingYOffsetStep = f4;
                    if (z) {
                        Engine.this.mPendingSync = true;
                    }
                    if (!Engine.this.mOffsetMessageEnqueued) {
                        Engine.this.mOffsetMessageEnqueued = true;
                        Engine.this.mCaller.sendMessage(Engine.this.mCaller.obtainMessage(WallpaperService.MSG_WALLPAPER_OFFSETS));
                    }
                }
            }

            public void dispatchWallpaperCommand(String str, int i, int i2, int i3, Bundle bundle, boolean z) {
                synchronized (Engine.this.mLock) {
                    WallpaperCommand wallpaperCommand = new WallpaperCommand();
                    wallpaperCommand.action = str;
                    wallpaperCommand.x = i;
                    wallpaperCommand.y = i2;
                    wallpaperCommand.z = i3;
                    wallpaperCommand.extras = bundle;
                    wallpaperCommand.sync = z;
                    Message messageObtainMessage = Engine.this.mCaller.obtainMessage(WallpaperService.MSG_WALLPAPER_COMMAND);
                    messageObtainMessage.obj = wallpaperCommand;
                    Engine.this.mCaller.sendMessage(messageObtainMessage);
                }
            }
        };

        public Bundle onCommand(String str, int i, int i2, int i3, Bundle bundle, boolean z) {
            return null;
        }

        public void onCreate(SurfaceHolder surfaceHolder) {
        }

        public void onDesiredSizeChanged(int i, int i2) {
        }

        public void onDestroy() {
        }

        public void onOffsetsChanged(float f, float f2, float f3, float f4, int i, int i2) {
        }

        public void onSurfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        }

        public void onSurfaceCreated(SurfaceHolder surfaceHolder) {
        }

        public void onSurfaceDestroyed(SurfaceHolder surfaceHolder) {
        }

        public void onSurfaceRedrawNeeded(SurfaceHolder surfaceHolder) {
        }

        public void onTouchEvent(MotionEvent motionEvent) {
        }

        public void onVisibilityChanged(boolean z) {
        }

        public Engine() {
        }

        final class WallpaperInputEventReceiver extends InputEventReceiver {
            public WallpaperInputEventReceiver(InputChannel inputChannel, Looper looper) {
                super(inputChannel, looper);
            }

            @Override // android.view.InputEventReceiver
            public void onInputEvent(InputEvent inputEvent) {
                boolean z = false;
                try {
                    if ((inputEvent instanceof MotionEvent) && (inputEvent.getSource() & 2) != 0) {
                        Engine.this.dispatchPointer(MotionEvent.obtainNoHistory((MotionEvent) inputEvent));
                        z = true;
                    }
                } finally {
                    finishInputEvent(inputEvent, false);
                }
            }
        }

        public SurfaceHolder getSurfaceHolder() {
            return this.mSurfaceHolder;
        }

        public int getDesiredMinimumWidth() {
            return this.mIWallpaperEngine.mReqWidth;
        }

        public int getDesiredMinimumHeight() {
            return this.mIWallpaperEngine.mReqHeight;
        }

        public boolean isVisible() {
            return this.mReportedVisible;
        }

        public boolean isPreview() {
            return this.mIWallpaperEngine.mIsPreview;
        }

        public void setTouchEventsEnabled(boolean z) throws Throwable {
            this.mWindowFlags = z ? this.mWindowFlags & (-17) : this.mWindowFlags | 16;
            if (this.mCreated) {
                updateSurface(false, false, false);
            }
        }

        public void setOffsetNotificationsEnabled(boolean z) throws Throwable {
            this.mWindowPrivateFlags = z ? this.mWindowPrivateFlags | 4 : this.mWindowPrivateFlags & (-5);
            if (this.mCreated) {
                updateSurface(false, false, false);
            }
        }

        public void setFixedSizeAllowed(boolean z) {
            this.mFixedSizeAllowed = z;
        }

        protected void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            printWriter.print(str);
            printWriter.print("mInitializing=");
            printWriter.print(this.mInitializing);
            printWriter.print(" mDestroyed=");
            printWriter.println(this.mDestroyed);
            printWriter.print(str);
            printWriter.print("mVisible=");
            printWriter.print(this.mVisible);
            printWriter.print(" mScreenOn=");
            printWriter.print(this.mScreenOn);
            printWriter.print(" mReportedVisible=");
            printWriter.println(this.mReportedVisible);
            printWriter.print(str);
            printWriter.print("mCreated=");
            printWriter.print(this.mCreated);
            printWriter.print(" mSurfaceCreated=");
            printWriter.print(this.mSurfaceCreated);
            printWriter.print(" mIsCreating=");
            printWriter.print(this.mIsCreating);
            printWriter.print(" mDrawingAllowed=");
            printWriter.println(this.mDrawingAllowed);
            printWriter.print(str);
            printWriter.print("mWidth=");
            printWriter.print(this.mWidth);
            printWriter.print(" mCurWidth=");
            printWriter.print(this.mCurWidth);
            printWriter.print(" mHeight=");
            printWriter.print(this.mHeight);
            printWriter.print(" mCurHeight=");
            printWriter.println(this.mCurHeight);
            printWriter.print(str);
            printWriter.print("mType=");
            printWriter.print(this.mType);
            printWriter.print(" mWindowFlags=");
            printWriter.print(this.mWindowFlags);
            printWriter.print(" mCurWindowFlags=");
            printWriter.println(this.mCurWindowFlags);
            printWriter.print(" mWindowPrivateFlags=");
            printWriter.print(this.mWindowPrivateFlags);
            printWriter.print(" mCurWindowPrivateFlags=");
            printWriter.println(this.mCurWindowPrivateFlags);
            printWriter.print(str);
            printWriter.print("mVisibleInsets=");
            printWriter.print(this.mVisibleInsets.toShortString());
            printWriter.print(" mWinFrame=");
            printWriter.print(this.mWinFrame.toShortString());
            printWriter.print(" mContentInsets=");
            printWriter.println(this.mContentInsets.toShortString());
            printWriter.print(str);
            printWriter.print("mConfiguration=");
            printWriter.println(this.mConfiguration);
            printWriter.print(str);
            printWriter.print("mLayout=");
            printWriter.println(this.mLayout);
            synchronized (this.mLock) {
                printWriter.print(str);
                printWriter.print("mPendingXOffset=");
                printWriter.print(this.mPendingXOffset);
                printWriter.print(" mPendingXOffset=");
                printWriter.println(this.mPendingXOffset);
                printWriter.print(str);
                printWriter.print("mPendingXOffsetStep=");
                printWriter.print(this.mPendingXOffsetStep);
                printWriter.print(" mPendingXOffsetStep=");
                printWriter.println(this.mPendingXOffsetStep);
                printWriter.print(str);
                printWriter.print("mOffsetMessageEnqueued=");
                printWriter.print(this.mOffsetMessageEnqueued);
                printWriter.print(" mPendingSync=");
                printWriter.println(this.mPendingSync);
                if (this.mPendingMove != null) {
                    printWriter.print(str);
                    printWriter.print("mPendingMove=");
                    printWriter.println(this.mPendingMove);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void dispatchPointer(MotionEvent motionEvent) {
            if (motionEvent.isTouchEvent()) {
                synchronized (this.mLock) {
                    if (motionEvent.getAction() == 2) {
                        this.mPendingMove = motionEvent;
                    } else {
                        this.mPendingMove = null;
                    }
                }
                this.mCaller.sendMessage(this.mCaller.obtainMessageO(WallpaperService.MSG_TOUCH_EVENT, motionEvent));
                return;
            }
            motionEvent.recycle();
        }

        void updateSurface(boolean z, boolean z2, boolean z3) throws Throwable {
            boolean z4;
            boolean z5;
            boolean z6;
            boolean z7;
            if (this.mDestroyed) {
                Log.w(WallpaperService.TAG, "Ignoring updateSurface: destroyed");
            }
            int requestedWidth = this.mSurfaceHolder.getRequestedWidth();
            if (requestedWidth <= 0) {
                requestedWidth = -1;
            }
            int requestedHeight = this.mSurfaceHolder.getRequestedHeight();
            int i = requestedHeight > 0 ? requestedHeight : -1;
            boolean z8 = !this.mCreated;
            boolean z9 = !this.mSurfaceCreated;
            boolean z10 = this.mFormat != this.mSurfaceHolder.getRequestedFormat();
            boolean z11 = (this.mWidth == requestedWidth && this.mHeight == i) ? false : true;
            boolean z12 = this.mType != this.mSurfaceHolder.getRequestedType();
            boolean z13 = (this.mCurWindowFlags == this.mWindowFlags && this.mCurWindowPrivateFlags == this.mWindowPrivateFlags) ? false : true;
            if (!z && !z8 && !z9 && !z10 && !z11 && !z12 && !z13 && !z3 && this.mIWallpaperEngine.mShownReported) {
                return;
            }
            try {
                this.mWidth = requestedWidth;
                this.mHeight = i;
                this.mFormat = this.mSurfaceHolder.getRequestedFormat();
                this.mType = this.mSurfaceHolder.getRequestedType();
                this.mLayout.x = 0;
                this.mLayout.y = 0;
                this.mLayout.width = requestedWidth;
                this.mLayout.height = i;
                this.mLayout.format = this.mFormat;
                int i2 = this.mWindowFlags;
                this.mCurWindowFlags = i2;
                this.mLayout.flags = i2 | 512 | 256 | 8;
                int i3 = this.mWindowPrivateFlags;
                this.mCurWindowPrivateFlags = i3;
                this.mLayout.privateFlags = i3;
                this.mLayout.memoryType = this.mType;
                this.mLayout.token = this.mWindowToken;
                if (!this.mCreated) {
                    this.mLayout.type = this.mIWallpaperEngine.mWindowType;
                    this.mLayout.gravity = BadgeDrawable.TOP_START;
                    this.mLayout.setTitle(WallpaperService.this.getClass().getName());
                    this.mLayout.windowAnimations = 16974324;
                    this.mInputChannel = new InputChannel();
                    IWindowSession iWindowSession = this.mSession;
                    BaseIWindow baseIWindow = this.mWindow;
                    if (iWindowSession.addToDisplay(baseIWindow, baseIWindow.mSeq, this.mLayout, 0, 0, this.mContentInsets, this.mInputChannel) < 0) {
                        Log.w(WallpaperService.TAG, "Failed to add window while updating wallpaper surface.");
                        return;
                    } else {
                        this.mCreated = true;
                        this.mInputEventReceiver = new WallpaperInputEventReceiver(this.mInputChannel, Looper.myLooper());
                    }
                }
                this.mSurfaceHolder.mSurfaceLock.lock();
                this.mDrawingAllowed = true;
                IWindowSession iWindowSession2 = this.mSession;
                BaseIWindow baseIWindow2 = this.mWindow;
                boolean z14 = z11;
                int iRelayout = iWindowSession2.relayout(baseIWindow2, baseIWindow2.mSeq, this.mLayout, this.mWidth, this.mHeight, 0, 0, this.mWinFrame, this.mOverscanInsets, this.mContentInsets, this.mVisibleInsets, this.mConfiguration, this.mSurfaceHolder.mSurface);
                int iWidth = this.mWinFrame.width();
                if (this.mCurWidth != iWidth) {
                    this.mCurWidth = iWidth;
                    z4 = true;
                } else {
                    z4 = z14;
                }
                int iHeight = this.mWinFrame.height();
                if (this.mCurHeight != iHeight) {
                    this.mCurHeight = iHeight;
                    z4 = true;
                }
                this.mSurfaceHolder.setSurfaceFrameSize(iWidth, iHeight);
                this.mSurfaceHolder.mSurfaceLock.unlock();
                if (!this.mSurfaceHolder.mSurface.isValid()) {
                    reportSurfaceDestroyed();
                    return;
                }
                try {
                    this.mSurfaceHolder.ungetCallbacks();
                    if (z9) {
                        this.mIsCreating = true;
                        onSurfaceCreated(this.mSurfaceHolder);
                        SurfaceHolder.Callback[] callbacks = this.mSurfaceHolder.getCallbacks();
                        if (callbacks != null) {
                            for (SurfaceHolder.Callback callback : callbacks) {
                                callback.surfaceCreated(this.mSurfaceHolder);
                            }
                        }
                        z6 = true;
                    } else {
                        z6 = false;
                    }
                    z5 = z3 | (z8 || (iRelayout & 2) != 0);
                    if (z2 || z8 || z9 || z10 || z4) {
                        try {
                            onSurfaceChanged(this.mSurfaceHolder, this.mFormat, this.mCurWidth, this.mCurHeight);
                            SurfaceHolder.Callback[] callbacks2 = this.mSurfaceHolder.getCallbacks();
                            if (callbacks2 != null) {
                                for (SurfaceHolder.Callback callback2 : callbacks2) {
                                    callback2.surfaceChanged(this.mSurfaceHolder, this.mFormat, this.mCurWidth, this.mCurHeight);
                                }
                            }
                            z6 = true;
                        } catch (Throwable th) {
                            th = th;
                            this.mIsCreating = false;
                            this.mSurfaceCreated = true;
                            if (z5) {
                                this.mSession.finishDrawing(this.mWindow);
                            }
                            this.mIWallpaperEngine.reportShown();
                            throw th;
                        }
                    }
                    if (z5) {
                        onSurfaceRedrawNeeded(this.mSurfaceHolder);
                        SurfaceHolder.Callback[] callbacks3 = this.mSurfaceHolder.getCallbacks();
                        if (callbacks3 != null) {
                            for (SurfaceHolder.Callback callback3 : callbacks3) {
                                if (callback3 instanceof SurfaceHolder.Callback2) {
                                    ((SurfaceHolder.Callback2) callback3).surfaceRedrawNeeded(this.mSurfaceHolder);
                                }
                            }
                        }
                    }
                    if (!z6 || this.mReportedVisible) {
                        z7 = false;
                    } else {
                        if (this.mIsCreating) {
                            onVisibilityChanged(true);
                        }
                        z7 = false;
                        onVisibilityChanged(false);
                    }
                    this.mIsCreating = z7;
                    this.mSurfaceCreated = true;
                    if (z5) {
                        this.mSession.finishDrawing(this.mWindow);
                    }
                    this.mIWallpaperEngine.reportShown();
                } catch (Throwable th2) {
                    th = th2;
                    z5 = z3;
                }
            } catch (RemoteException unused) {
            }
        }

        void attach(IWallpaperEngineWrapper iWallpaperEngineWrapper) throws Throwable {
            if (this.mDestroyed) {
                return;
            }
            this.mIWallpaperEngine = iWallpaperEngineWrapper;
            this.mCaller = iWallpaperEngineWrapper.mCaller;
            this.mConnection = iWallpaperEngineWrapper.mConnection;
            this.mWindowToken = iWallpaperEngineWrapper.mWindowToken;
            this.mSurfaceHolder.setSizeFromLayout();
            this.mInitializing = true;
            IWindowSession windowSession = WindowManagerGlobal.getWindowSession();
            this.mSession = windowSession;
            this.mWindow.setSession(windowSession);
            this.mScreenOn = ((PowerManager) WallpaperService.this.getSystemService(Context.POWER_SERVICE)).isScreenOn();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_SCREEN_ON);
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            WallpaperService.this.registerReceiver(this.mReceiver, intentFilter);
            onCreate(this.mSurfaceHolder);
            this.mInitializing = false;
            this.mReportedVisible = false;
            updateSurface(false, false, false);
        }

        void doDesiredSizeChanged(int i, int i2) {
            if (this.mDestroyed) {
                return;
            }
            this.mIWallpaperEngine.mReqWidth = i;
            this.mIWallpaperEngine.mReqHeight = i2;
            onDesiredSizeChanged(i, i2);
            doOffsetsChanged(true);
        }

        void doVisibilityChanged(boolean z) throws Throwable {
            if (this.mDestroyed) {
                return;
            }
            this.mVisible = z;
            reportVisibility();
        }

        void reportVisibility() throws Throwable {
            if (this.mDestroyed) {
                return;
            }
            boolean z = this.mVisible && this.mScreenOn;
            if (this.mReportedVisible != z) {
                this.mReportedVisible = z;
                if (z) {
                    doOffsetsChanged(false);
                    updateSurface(false, false, false);
                }
                onVisibilityChanged(z);
            }
        }

        void doOffsetsChanged(boolean z) {
            float f;
            float f2;
            float f3;
            float f4;
            boolean z2;
            if (this.mDestroyed) {
                return;
            }
            if (z || this.mOffsetsChanged) {
                synchronized (this.mLock) {
                    f = this.mPendingXOffset;
                    f2 = this.mPendingYOffset;
                    f3 = this.mPendingXOffsetStep;
                    f4 = this.mPendingYOffsetStep;
                    z2 = this.mPendingSync;
                    this.mPendingSync = false;
                    this.mOffsetMessageEnqueued = false;
                }
                if (this.mSurfaceCreated) {
                    if (this.mReportedVisible) {
                        int i = this.mIWallpaperEngine.mReqWidth - this.mCurWidth;
                        int i2 = i > 0 ? -((int) ((i * f) + 0.5f)) : 0;
                        int i3 = this.mIWallpaperEngine.mReqHeight - this.mCurHeight;
                        onOffsetsChanged(f, f2, f3, f4, i2, i3 > 0 ? -((int) ((i3 * f2) + 0.5f)) : 0);
                    } else {
                        this.mOffsetsChanged = true;
                    }
                }
                if (z2) {
                    try {
                        this.mSession.wallpaperOffsetsComplete(this.mWindow.asBinder());
                    } catch (RemoteException unused) {
                    }
                }
            }
        }

        void doCommand(WallpaperCommand wallpaperCommand) {
            Bundle bundleOnCommand = !this.mDestroyed ? onCommand(wallpaperCommand.action, wallpaperCommand.x, wallpaperCommand.y, wallpaperCommand.z, wallpaperCommand.extras, wallpaperCommand.sync) : null;
            if (wallpaperCommand.sync) {
                try {
                    this.mSession.wallpaperCommandComplete(this.mWindow.asBinder(), bundleOnCommand);
                } catch (RemoteException unused) {
                }
            }
        }

        void reportSurfaceDestroyed() {
            if (this.mSurfaceCreated) {
                this.mSurfaceCreated = false;
                this.mSurfaceHolder.ungetCallbacks();
                SurfaceHolder.Callback[] callbacks = this.mSurfaceHolder.getCallbacks();
                if (callbacks != null) {
                    for (SurfaceHolder.Callback callback : callbacks) {
                        callback.surfaceDestroyed(this.mSurfaceHolder);
                    }
                }
                onSurfaceDestroyed(this.mSurfaceHolder);
            }
        }

        void detach() {
            if (this.mDestroyed) {
                return;
            }
            this.mDestroyed = true;
            if (this.mVisible) {
                this.mVisible = false;
                onVisibilityChanged(false);
            }
            reportSurfaceDestroyed();
            onDestroy();
            WallpaperService.this.unregisterReceiver(this.mReceiver);
            if (this.mCreated) {
                try {
                    WallpaperInputEventReceiver wallpaperInputEventReceiver = this.mInputEventReceiver;
                    if (wallpaperInputEventReceiver != null) {
                        wallpaperInputEventReceiver.dispose();
                        this.mInputEventReceiver = null;
                    }
                    this.mSession.remove(this.mWindow);
                } catch (RemoteException unused) {
                }
                this.mSurfaceHolder.mSurface.release();
                this.mCreated = false;
                InputChannel inputChannel = this.mInputChannel;
                if (inputChannel != null) {
                    inputChannel.dispose();
                    this.mInputChannel = null;
                }
            }
        }
    }

    class IWallpaperEngineWrapper extends IWallpaperEngine.Stub implements HandlerCaller.Callback {
        private final HandlerCaller mCaller;
        final IWallpaperConnection mConnection;
        Engine mEngine;
        final boolean mIsPreview;
        int mReqHeight;
        int mReqWidth;
        boolean mShownReported;
        final IBinder mWindowToken;
        final int mWindowType;

        IWallpaperEngineWrapper(WallpaperService wallpaperService, IWallpaperConnection iWallpaperConnection, IBinder iBinder, int i, boolean z, int i2, int i3) {
            HandlerCaller handlerCaller = new HandlerCaller(wallpaperService, wallpaperService.getMainLooper(), this, true);
            this.mCaller = handlerCaller;
            this.mConnection = iWallpaperConnection;
            this.mWindowToken = iBinder;
            this.mWindowType = i;
            this.mIsPreview = z;
            this.mReqWidth = i2;
            this.mReqHeight = i3;
            handlerCaller.sendMessage(handlerCaller.obtainMessage(10));
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void setDesiredSize(int i, int i2) {
            this.mCaller.sendMessage(this.mCaller.obtainMessageII(30, i, i2));
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void setVisibility(boolean z) {
            this.mCaller.sendMessage(this.mCaller.obtainMessageI(10010, z ? 1 : 0));
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void dispatchPointer(MotionEvent motionEvent) {
            Engine engine = this.mEngine;
            if (engine != null) {
                engine.dispatchPointer(motionEvent);
            } else {
                motionEvent.recycle();
            }
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void dispatchWallpaperCommand(String str, int i, int i2, int i3, Bundle bundle) {
            Engine engine = this.mEngine;
            if (engine != null) {
                engine.mWindow.dispatchWallpaperCommand(str, i, i2, i3, bundle, false);
            }
        }

        public void reportShown() {
            if (this.mShownReported) {
                return;
            }
            this.mShownReported = true;
            try {
                this.mConnection.engineShown(this);
            } catch (RemoteException e) {
                Log.w(WallpaperService.TAG, "Wallpaper host disappeared", e);
            }
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void destroy() {
            this.mCaller.sendMessage(this.mCaller.obtainMessage(20));
        }

        public void executeMessage(Message message) throws Throwable {
            int i = message.what;
            if (i == 10) {
                try {
                    this.mConnection.attachEngine(this);
                    Engine engineOnCreateEngine = WallpaperService.this.onCreateEngine();
                    this.mEngine = engineOnCreateEngine;
                    WallpaperService.this.mActiveEngines.add(engineOnCreateEngine);
                    engineOnCreateEngine.attach(this);
                    return;
                } catch (RemoteException e) {
                    Log.w(WallpaperService.TAG, "Wallpaper host disappeared", e);
                    return;
                }
            }
            if (i == 20) {
                WallpaperService.this.mActiveEngines.remove(this.mEngine);
                this.mEngine.detach();
                return;
            }
            if (i == 30) {
                this.mEngine.doDesiredSizeChanged(message.arg1, message.arg2);
                return;
            }
            if (i == 10000) {
                this.mEngine.updateSurface(true, false, false);
                return;
            }
            if (i == 10010) {
                this.mEngine.doVisibilityChanged(message.arg1 != 0);
                return;
            }
            if (i == WallpaperService.MSG_WALLPAPER_OFFSETS) {
                this.mEngine.doOffsetsChanged(true);
                return;
            }
            if (i == WallpaperService.MSG_WALLPAPER_COMMAND) {
                this.mEngine.doCommand((WallpaperCommand) message.obj);
                return;
            }
            if (i == WallpaperService.MSG_WINDOW_RESIZED) {
                this.mEngine.updateSurface(true, false, message.arg1 != 0);
                this.mEngine.doOffsetsChanged(true);
                return;
            }
            if (i != WallpaperService.MSG_WINDOW_MOVED) {
                if (i == WallpaperService.MSG_TOUCH_EVENT) {
                    MotionEvent motionEvent = (MotionEvent) message.obj;
                    if (motionEvent.getAction() == 2) {
                        synchronized (this.mEngine.mLock) {
                            if (this.mEngine.mPendingMove == motionEvent) {
                                this.mEngine.mPendingMove = null;
                            } else {
                                z = true;
                            }
                        }
                    }
                    if (!z) {
                        this.mEngine.onTouchEvent(motionEvent);
                    }
                    motionEvent.recycle();
                    return;
                }
                Log.w(WallpaperService.TAG, "Unknown message type " + message.what);
            }
        }
    }

    class IWallpaperServiceWrapper extends IWallpaperService.Stub {
        private final WallpaperService mTarget;

        public IWallpaperServiceWrapper(WallpaperService wallpaperService) {
            this.mTarget = wallpaperService;
        }

        @Override // android.service.wallpaper.IWallpaperService
        public void attach(IWallpaperConnection iWallpaperConnection, IBinder iBinder, int i, boolean z, int i2, int i3) {
            WallpaperService.this.new IWallpaperEngineWrapper(this.mTarget, iWallpaperConnection, iBinder, i, z, i2, i3);
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < this.mActiveEngines.size(); i++) {
            this.mActiveEngines.get(i).detach();
        }
        this.mActiveEngines.clear();
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return new IWallpaperServiceWrapper(this);
    }

    @Override // android.app.Service
    protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print("State of wallpaper ");
        printWriter.print(this);
        printWriter.println(":");
        for (int i = 0; i < this.mActiveEngines.size(); i++) {
            Engine engine = this.mActiveEngines.get(i);
            printWriter.print("  Engine ");
            printWriter.print(engine);
            printWriter.println(":");
            engine.dump("    ", fileDescriptor, printWriter, strArr);
        }
    }
}
