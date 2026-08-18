package android.service.dreams;

import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.ServiceManager;
import android.service.dreams.IDreamManager;
import android.service.dreams.IDreamService;
import android.util.Slog;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.policy.PolicyManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class DreamService extends Service implements Window.Callback {
    public static final String DREAM_META_DATA = "android.service.dream";
    public static final String DREAM_SERVICE = "dreams";
    public static final String SERVICE_INTERFACE = "android.service.dreams.DreamService";
    private boolean mFinished;
    private IDreamManager mSandman;
    private Window mWindow;
    private WindowManager mWindowManager;
    private IBinder mWindowToken;
    private final String TAG = DreamService.class.getSimpleName() + "[" + getClass().getSimpleName() + "]";
    private final Handler mHandler = new Handler();
    private boolean mInteractive = false;
    private boolean mLowProfile = true;
    private boolean mFullscreen = false;
    private boolean mScreenBright = true;
    private boolean mDebug = false;

    private int applyFlags(int i, int i2, int i3) {
        return (i & (~i3)) | (i2 & i3);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        return false;
    }

    @Override // android.view.Window.Callback
    public void onActionModeFinished(ActionMode actionMode) {
    }

    @Override // android.view.Window.Callback
    public void onActionModeStarted(ActionMode actionMode) {
    }

    @Override // android.view.Window.Callback
    public void onAttachedToWindow() {
    }

    @Override // android.view.Window.Callback
    public void onContentChanged() {
    }

    @Override // android.view.Window.Callback
    public boolean onCreatePanelMenu(int i, Menu menu) {
        return false;
    }

    @Override // android.view.Window.Callback
    public View onCreatePanelView(int i) {
        return null;
    }

    @Override // android.view.Window.Callback
    public void onDetachedFromWindow() {
    }

    @Override // android.view.Window.Callback
    public boolean onMenuItemSelected(int i, MenuItem menuItem) {
        return false;
    }

    @Override // android.view.Window.Callback
    public boolean onMenuOpened(int i, Menu menu) {
        return false;
    }

    @Override // android.view.Window.Callback
    public void onPanelClosed(int i, Menu menu) {
    }

    @Override // android.view.Window.Callback
    public boolean onPreparePanel(int i, View view, Menu menu) {
        return false;
    }

    @Override // android.view.Window.Callback
    public boolean onSearchRequested() {
        return false;
    }

    @Override // android.view.Window.Callback
    public void onWindowAttributesChanged(WindowManager.LayoutParams layoutParams) {
    }

    @Override // android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
    }

    @Override // android.view.Window.Callback
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return null;
    }

    public void setDebug(boolean z) {
        this.mDebug = z;
    }

    @Override // android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Finishing on keyEvent");
            }
            safelyFinish();
            return true;
        }
        if (keyEvent.getKeyCode() == 4) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Finishing on back key");
            }
            safelyFinish();
            return true;
        }
        return this.mWindow.superDispatchKeyEvent(keyEvent);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchKeyShortcutEvent(KeyEvent keyEvent) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Finishing on keyShortcutEvent");
            }
            safelyFinish();
            return true;
        }
        return this.mWindow.superDispatchKeyShortcutEvent(keyEvent);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Finishing on touchEvent");
            }
            safelyFinish();
            return true;
        }
        return this.mWindow.superDispatchTouchEvent(motionEvent);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchTrackballEvent(MotionEvent motionEvent) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Finishing on trackballEvent");
            }
            safelyFinish();
            return true;
        }
        return this.mWindow.superDispatchTrackballEvent(motionEvent);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchGenericMotionEvent(MotionEvent motionEvent) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Finishing on genericMotionEvent");
            }
            safelyFinish();
            return true;
        }
        return this.mWindow.superDispatchGenericMotionEvent(motionEvent);
    }

    public WindowManager getWindowManager() {
        return this.mWindowManager;
    }

    public Window getWindow() {
        return this.mWindow;
    }

    public void setContentView(int i) {
        getWindow().setContentView(i);
    }

    public void setContentView(View view) {
        getWindow().setContentView(view);
    }

    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        getWindow().setContentView(view, layoutParams);
    }

    public void addContentView(View view, ViewGroup.LayoutParams layoutParams) {
        getWindow().addContentView(view, layoutParams);
    }

    public View findViewById(int i) {
        return getWindow().findViewById(i);
    }

    public void setInteractive(boolean z) {
        this.mInteractive = z;
    }

    public boolean isInteractive() {
        return this.mInteractive;
    }

    public void setLowProfile(boolean z) {
        this.mLowProfile = z;
        applySystemUiVisibilityFlags(z ? 1 : 0, 1);
    }

    public boolean isLowProfile() {
        return getSystemUiVisibilityFlagValue(1, this.mLowProfile);
    }

    public void setFullscreen(boolean z) {
        this.mFullscreen = z;
        applyWindowFlags(z ? 1024 : 0, 1024);
    }

    public boolean isFullscreen() {
        return this.mFullscreen;
    }

    public void setScreenBright(boolean z) {
        this.mScreenBright = z;
        applyWindowFlags(z ? 128 : 0, 128);
    }

    public boolean isScreenBright() {
        return getWindowFlagValue(128, this.mScreenBright);
    }

    @Override // android.app.Service
    public void onCreate() {
        if (this.mDebug) {
            Slog.v(this.TAG, "onCreate() on thread " + Thread.currentThread().getId());
        }
        super.onCreate();
    }

    public void onDreamingStarted() {
        if (this.mDebug) {
            Slog.v(this.TAG, "onDreamingStarted()");
        }
    }

    public void onDreamingStopped() {
        if (this.mDebug) {
            Slog.v(this.TAG, "onDreamingStopped()");
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (this.mDebug) {
            Slog.v(this.TAG, "onBind() intent = " + intent);
        }
        return new DreamServiceWrapper();
    }

    public final void finish() {
        if (this.mDebug) {
            Slog.v(this.TAG, "finish()");
        }
        finishInternal();
    }

    @Override // android.app.Service
    public void onDestroy() {
        if (this.mDebug) {
            Slog.v(this.TAG, "onDestroy()");
        }
        detach();
        super.onDestroy();
    }

    private void loadSandman() {
        this.mSandman = IDreamManager.Stub.asInterface(ServiceManager.getService(DREAM_SERVICE));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void detach() {
        if (this.mWindow == null) {
            return;
        }
        try {
            onDreamingStopped();
        } catch (Throwable th) {
            Slog.w(this.TAG, "Crashed in onDreamingStopped()", th);
        }
        if (this.mDebug) {
            Slog.v(this.TAG, "detach(): Removing window from window manager");
        }
        try {
            this.mWindowManager.removeViewImmediate(this.mWindow.getDecorView());
            WindowManagerGlobal.getInstance().closeAll(this.mWindowToken, getClass().getName(), "Dream");
        } catch (Throwable th2) {
            Slog.w(this.TAG, "Crashed removing window view", th2);
        }
        this.mWindow = null;
        this.mWindowToken = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void attach(IBinder iBinder) {
        if (this.mWindowToken != null) {
            Slog.e(this.TAG, "attach() called when already attached with token=" + this.mWindowToken);
            return;
        }
        if (this.mDebug) {
            Slog.v(this.TAG, "Attached on thread " + Thread.currentThread().getId());
        }
        if (this.mSandman == null) {
            loadSandman();
        }
        this.mWindowToken = iBinder;
        Window windowMakeNewWindow = PolicyManager.makeNewWindow(this);
        this.mWindow = windowMakeNewWindow;
        windowMakeNewWindow.setCallback(this);
        this.mWindow.requestFeature(1);
        this.mWindow.setBackgroundDrawable(new ColorDrawable(-16777216));
        this.mWindow.setFormat(-1);
        if (this.mDebug) {
            Slog.v(this.TAG, String.format("Attaching window token: %s to window of type %s", iBinder, 2023));
        }
        WindowManager.LayoutParams attributes = this.mWindow.getAttributes();
        attributes.type = 2023;
        attributes.token = iBinder;
        attributes.windowAnimations = 16974329;
        attributes.flags |= 4784385 | (this.mFullscreen ? 1024 : 0) | (this.mScreenBright ? 128 : 0);
        this.mWindow.setAttributes(attributes);
        if (this.mDebug) {
            Slog.v(this.TAG, "Created and attached window: " + this.mWindow);
        }
        this.mWindow.setWindowManager(null, iBinder, "dream", true);
        this.mWindowManager = this.mWindow.getWindowManager();
        if (this.mDebug) {
            Slog.v(this.TAG, "Window added on thread " + Thread.currentThread().getId());
        }
        try {
            applySystemUiVisibilityFlags(this.mLowProfile ? 1 : 0, 1);
            getWindowManager().addView(this.mWindow.getDecorView(), this.mWindow.getAttributes());
            this.mHandler.post(new Runnable() { // from class: android.service.dreams.DreamService.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        DreamService.this.onDreamingStarted();
                    } catch (Throwable th) {
                        Slog.w(DreamService.this.TAG, "Crashed in onDreamingStarted()", th);
                        DreamService.this.safelyFinish();
                    }
                }
            });
        } catch (Throwable th) {
            Slog.w(this.TAG, "Crashed adding window view", th);
            safelyFinish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void safelyFinish() {
        if (this.mDebug) {
            Slog.v(this.TAG, "safelyFinish()");
        }
        try {
            finish();
            if (this.mFinished) {
                return;
            }
            Slog.w(this.TAG, "Bad dream, did not call super.finish()");
            finishInternal();
        } catch (Throwable th) {
            Slog.w(this.TAG, "Crashed in safelyFinish()", th);
            finishInternal();
        }
    }

    private void finishInternal() {
        if (this.mDebug) {
            Slog.v(this.TAG, "finishInternal() mFinished = " + this.mFinished);
        }
        if (this.mFinished) {
            return;
        }
        try {
            this.mFinished = true;
            IDreamManager iDreamManager = this.mSandman;
            if (iDreamManager != null) {
                iDreamManager.finishSelf(this.mWindowToken);
            } else {
                Slog.w(this.TAG, "No dream manager found");
            }
            stopSelf();
        } catch (Throwable th) {
            Slog.w(this.TAG, "Crashed in finishInternal()", th);
        }
    }

    private boolean getWindowFlagValue(int i, boolean z) {
        Window window = this.mWindow;
        return window == null ? z : (i & window.getAttributes().flags) != 0;
    }

    private void applyWindowFlags(int i, int i2) {
        Window window = this.mWindow;
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.flags = applyFlags(attributes.flags, i, i2);
            this.mWindow.setAttributes(attributes);
            this.mWindowManager.updateViewLayout(this.mWindow.getDecorView(), attributes);
        }
    }

    private boolean getSystemUiVisibilityFlagValue(int i, boolean z) {
        Window window = this.mWindow;
        View decorView = window == null ? null : window.getDecorView();
        return decorView == null ? z : (i & decorView.getSystemUiVisibility()) != 0;
    }

    private void applySystemUiVisibilityFlags(int i, int i2) {
        Window window = this.mWindow;
        View decorView = window == null ? null : window.getDecorView();
        if (decorView != null) {
            decorView.setSystemUiVisibility(applyFlags(decorView.getSystemUiVisibility(), i, i2));
        }
    }

    @Override // android.app.Service
    protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(fileDescriptor, printWriter, strArr);
        printWriter.print(this.TAG + ": ");
        if (this.mWindowToken == null) {
            printWriter.println("stopped");
        } else {
            printWriter.println("running (token=" + this.mWindowToken + ")");
        }
        printWriter.println("  window: " + this.mWindow);
        printWriter.print("  flags:");
        if (isInteractive()) {
            printWriter.print(" interactive");
        }
        if (isLowProfile()) {
            printWriter.print(" lowprofile");
        }
        if (isFullscreen()) {
            printWriter.print(" fullscreen");
        }
        if (isScreenBright()) {
            printWriter.print(" bright");
        }
        printWriter.println();
    }

    private class DreamServiceWrapper extends IDreamService.Stub {
        private DreamServiceWrapper() {
        }

        @Override // android.service.dreams.IDreamService
        public void attach(final IBinder iBinder) {
            DreamService.this.mHandler.post(new Runnable() { // from class: android.service.dreams.DreamService.DreamServiceWrapper.1
                @Override // java.lang.Runnable
                public void run() {
                    DreamService.this.attach(iBinder);
                }
            });
        }

        @Override // android.service.dreams.IDreamService
        public void detach() {
            DreamService.this.mHandler.post(new Runnable() { // from class: android.service.dreams.DreamService.DreamServiceWrapper.2
                @Override // java.lang.Runnable
                public void run() {
                    DreamService.this.detach();
                }
            });
        }
    }
}
