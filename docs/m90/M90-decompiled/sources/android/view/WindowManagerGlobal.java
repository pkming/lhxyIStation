package android.view;

import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.opengl.ManagedEGLContext;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.ArraySet;
import android.util.Log;
import android.view.IWindowManager;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.util.FastPrintWriter;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class WindowManagerGlobal {
    public static final int ADD_APP_EXITING = -4;
    public static final int ADD_BAD_APP_TOKEN = -1;
    public static final int ADD_BAD_SUBWINDOW_TOKEN = -2;
    public static final int ADD_DUPLICATE_ADD = -5;
    public static final int ADD_FLAG_APP_VISIBLE = 2;
    public static final int ADD_FLAG_IN_TOUCH_MODE = 1;
    public static final int ADD_INVALID_DISPLAY = -9;
    public static final int ADD_MULTIPLE_SINGLETON = -7;
    public static final int ADD_NOT_APP_TOKEN = -3;
    public static final int ADD_OKAY = 0;
    public static final int ADD_PERMISSION_DENIED = -8;
    public static final int ADD_STARTING_NOT_NEEDED = -6;
    public static final int RELAYOUT_DEFER_SURFACE_DESTROY = 2;
    public static final int RELAYOUT_INSETS_PENDING = 1;
    public static final int RELAYOUT_RES_ANIMATING = 8;
    public static final int RELAYOUT_RES_FIRST_TIME = 2;
    public static final int RELAYOUT_RES_IN_TOUCH_MODE = 1;
    public static final int RELAYOUT_RES_SURFACE_CHANGED = 4;
    private static final String TAG = "WindowManager";
    private static WindowManagerGlobal sDefaultWindowManager;
    private static IWindowManager sWindowManagerService;
    private static IWindowSession sWindowSession;
    private boolean mNeedsEglTerminate;
    private Runnable mSystemPropertyUpdater;
    private final Object mLock = new Object();
    private final ArrayList<View> mViews = new ArrayList<>();
    private final ArrayList<ViewRootImpl> mRoots = new ArrayList<>();
    private final ArrayList<WindowManager.LayoutParams> mParams = new ArrayList<>();
    private final ArraySet<View> mDyingViews = new ArraySet<>();

    private WindowManagerGlobal() {
    }

    public static WindowManagerGlobal getInstance() {
        WindowManagerGlobal windowManagerGlobal;
        synchronized (WindowManagerGlobal.class) {
            if (sDefaultWindowManager == null) {
                sDefaultWindowManager = new WindowManagerGlobal();
            }
            windowManagerGlobal = sDefaultWindowManager;
        }
        return windowManagerGlobal;
    }

    public static IWindowManager getWindowManagerService() {
        IWindowManager iWindowManager;
        synchronized (WindowManagerGlobal.class) {
            if (sWindowManagerService == null) {
                sWindowManagerService = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));
            }
            iWindowManager = sWindowManagerService;
        }
        return iWindowManager;
    }

    public static IWindowSession getWindowSession() {
        IWindowSession iWindowSession;
        synchronized (WindowManagerGlobal.class) {
            if (sWindowSession == null) {
                try {
                    InputMethodManager inputMethodManager = InputMethodManager.getInstance();
                    IWindowManager windowManagerService = getWindowManagerService();
                    sWindowSession = windowManagerService.openSession(inputMethodManager.getClient(), inputMethodManager.getInputContext());
                    ValueAnimator.setDurationScale(windowManagerService.getAnimationScale(2));
                } catch (RemoteException e) {
                    Log.e(TAG, "Failed to open window session", e);
                }
                iWindowSession = sWindowSession;
            } else {
                iWindowSession = sWindowSession;
            }
        }
        return iWindowSession;
    }

    public static IWindowSession peekWindowSession() {
        IWindowSession iWindowSession;
        synchronized (WindowManagerGlobal.class) {
            iWindowSession = sWindowSession;
        }
        return iWindowSession;
    }

    public String[] getViewRootNames() {
        String[] strArr;
        synchronized (this.mLock) {
            int size = this.mRoots.size();
            strArr = new String[size];
            for (int i = 0; i < size; i++) {
                strArr[i] = getWindowName(this.mRoots.get(i));
            }
        }
        return strArr;
    }

    public View getRootView(String str) {
        synchronized (this.mLock) {
            for (int size = this.mRoots.size() - 1; size >= 0; size--) {
                ViewRootImpl viewRootImpl = this.mRoots.get(size);
                if (str.equals(getWindowName(viewRootImpl))) {
                    return viewRootImpl.getView();
                }
            }
            return null;
        }
    }

    public void addView(View view, ViewGroup.LayoutParams layoutParams, Display display, Window window) {
        ViewRootImpl viewRootImpl;
        if (view == null) {
            throw new IllegalArgumentException("view must not be null");
        }
        if (display == null) {
            throw new IllegalArgumentException("display must not be null");
        }
        if (!(layoutParams instanceof WindowManager.LayoutParams)) {
            throw new IllegalArgumentException("Params must be WindowManager.LayoutParams");
        }
        WindowManager.LayoutParams layoutParams2 = (WindowManager.LayoutParams) layoutParams;
        if (window != null) {
            window.adjustLayoutParamsForSubWindow(layoutParams2);
        }
        View view2 = null;
        synchronized (this.mLock) {
            if (this.mSystemPropertyUpdater == null) {
                Runnable runnable = new Runnable() { // from class: android.view.WindowManagerGlobal.1
                    @Override // java.lang.Runnable
                    public void run() {
                        synchronized (WindowManagerGlobal.this.mLock) {
                            for (int size = WindowManagerGlobal.this.mRoots.size() - 1; size >= 0; size--) {
                                ((ViewRootImpl) WindowManagerGlobal.this.mRoots.get(size)).loadSystemProperties();
                            }
                        }
                    }
                };
                this.mSystemPropertyUpdater = runnable;
                SystemProperties.addChangeCallback(runnable);
            }
            int iFindViewLocked = findViewLocked(view, false);
            if (iFindViewLocked >= 0) {
                if (this.mDyingViews.contains(view)) {
                    this.mRoots.get(iFindViewLocked).doDie();
                } else {
                    throw new IllegalStateException("View " + view + " has already been added to the window manager.");
                }
            }
            if (layoutParams2.type >= 1000 && layoutParams2.type <= 1999) {
                int size = this.mViews.size();
                for (int i = 0; i < size; i++) {
                    if (this.mRoots.get(i).mWindow.asBinder() == layoutParams2.token) {
                        view2 = this.mViews.get(i);
                    }
                }
            }
            viewRootImpl = new ViewRootImpl(view.getContext(), display);
            view.setLayoutParams(layoutParams2);
            this.mViews.add(view);
            this.mRoots.add(viewRootImpl);
            this.mParams.add(layoutParams2);
        }
        try {
            viewRootImpl.setView(view, layoutParams2, view2);
        } catch (RuntimeException e) {
            synchronized (this.mLock) {
                int iFindViewLocked2 = findViewLocked(view, false);
                if (iFindViewLocked2 >= 0) {
                    removeViewLocked(iFindViewLocked2, true);
                }
                throw e;
            }
        }
    }

    public void updateViewLayout(View view, ViewGroup.LayoutParams layoutParams) {
        if (view == null) {
            throw new IllegalArgumentException("view must not be null");
        }
        if (!(layoutParams instanceof WindowManager.LayoutParams)) {
            throw new IllegalArgumentException("Params must be WindowManager.LayoutParams");
        }
        WindowManager.LayoutParams layoutParams2 = (WindowManager.LayoutParams) layoutParams;
        view.setLayoutParams(layoutParams2);
        synchronized (this.mLock) {
            int iFindViewLocked = findViewLocked(view, true);
            ViewRootImpl viewRootImpl = this.mRoots.get(iFindViewLocked);
            this.mParams.remove(iFindViewLocked);
            this.mParams.add(iFindViewLocked, layoutParams2);
            viewRootImpl.setLayoutParams(layoutParams2, false);
        }
    }

    public void removeView(View view, boolean z) {
        if (view == null) {
            throw new IllegalArgumentException("view must not be null");
        }
        synchronized (this.mLock) {
            int iFindViewLocked = findViewLocked(view, true);
            View view2 = this.mRoots.get(iFindViewLocked).getView();
            removeViewLocked(iFindViewLocked, z);
            if (view2 != view) {
                throw new IllegalStateException("Calling with view " + view + " but the ViewAncestor is attached to " + view2);
            }
        }
    }

    public void closeAll(IBinder iBinder, String str, String str2) {
        synchronized (this.mLock) {
            int size = this.mViews.size();
            for (int i = 0; i < size; i++) {
                if (iBinder == null || this.mParams.get(i).token == iBinder) {
                    ViewRootImpl viewRootImpl = this.mRoots.get(i);
                    if (str != null) {
                        WindowLeaked windowLeaked = new WindowLeaked(str2 + " " + str + " has leaked window " + viewRootImpl.getView() + " that was originally added here");
                        windowLeaked.setStackTrace(viewRootImpl.getLocation().getStackTrace());
                        Log.e(TAG, "", windowLeaked);
                    }
                    removeViewLocked(i, false);
                }
            }
        }
    }

    private void removeViewLocked(int i, boolean z) {
        InputMethodManager inputMethodManager;
        ViewRootImpl viewRootImpl = this.mRoots.get(i);
        View view = viewRootImpl.getView();
        if (view != null && (inputMethodManager = InputMethodManager.getInstance()) != null) {
            inputMethodManager.windowDismissed(this.mViews.get(i).getWindowToken());
        }
        boolean zDie = viewRootImpl.die(z);
        if (view != null) {
            view.assignParent(null);
            if (zDie) {
                this.mDyingViews.add(view);
            }
        }
    }

    void doRemoveView(ViewRootImpl viewRootImpl) {
        synchronized (this.mLock) {
            int iIndexOf = this.mRoots.indexOf(viewRootImpl);
            if (iIndexOf >= 0) {
                this.mRoots.remove(iIndexOf);
                this.mParams.remove(iIndexOf);
                this.mDyingViews.remove(this.mViews.remove(iIndexOf));
            }
        }
    }

    private int findViewLocked(View view, boolean z) {
        int iIndexOf = this.mViews.indexOf(view);
        if (!z || iIndexOf >= 0) {
            return iIndexOf;
        }
        throw new IllegalArgumentException("View=" + view + " not attached to window manager");
    }

    public void startTrimMemory(int i) {
        if (HardwareRenderer.isAvailable()) {
            if (i >= 80 || (i >= 60 && !ActivityManager.isHighEndGfx())) {
                synchronized (this.mLock) {
                    for (int size = this.mRoots.size() - 1; size >= 0; size--) {
                        this.mRoots.get(size).destroyHardwareResources();
                    }
                }
                this.mNeedsEglTerminate = true;
                HardwareRenderer.startTrimMemory(80);
                return;
            }
            HardwareRenderer.startTrimMemory(i);
        }
    }

    public void endTrimMemory() {
        HardwareRenderer.endTrimMemory();
        if (this.mNeedsEglTerminate) {
            ManagedEGLContext.doTerminate();
            this.mNeedsEglTerminate = false;
        }
    }

    public void trimLocalMemory() {
        synchronized (this.mLock) {
            for (int size = this.mRoots.size() - 1; size >= 0; size--) {
                this.mRoots.get(size).destroyHardwareLayers();
            }
        }
    }

    public void dumpGfxInfo(FileDescriptor fileDescriptor) {
        PrintWriter fastPrintWriter = new FastPrintWriter(new FileOutputStream(fileDescriptor));
        try {
            synchronized (this.mLock) {
                int size = this.mViews.size();
                fastPrintWriter.println("Profile data in ms:");
                for (int i = 0; i < size; i++) {
                    ViewRootImpl viewRootImpl = this.mRoots.get(i);
                    fastPrintWriter.printf("\n\t%s", getWindowName(viewRootImpl));
                    HardwareRenderer hardwareRenderer = viewRootImpl.getView().mAttachInfo.mHardwareRenderer;
                    if (hardwareRenderer != null) {
                        hardwareRenderer.dumpGfxInfo(fastPrintWriter);
                    }
                }
                fastPrintWriter.println("\nView hierarchy:\n");
                int[] iArr = new int[2];
                int i2 = 0;
                int i3 = 0;
                for (int i4 = 0; i4 < size; i4++) {
                    ViewRootImpl viewRootImpl2 = this.mRoots.get(i4);
                    viewRootImpl2.dumpGfxInfo(iArr);
                    fastPrintWriter.printf("  %s\n  %d views, %.2f kB of display lists", getWindowName(viewRootImpl2), Integer.valueOf(iArr[0]), Float.valueOf(iArr[1] / 1024.0f));
                    HardwareRenderer hardwareRenderer2 = viewRootImpl2.getView().mAttachInfo.mHardwareRenderer;
                    if (hardwareRenderer2 != null) {
                        fastPrintWriter.printf(", %d frames rendered", Long.valueOf(hardwareRenderer2.getFrameCount()));
                    }
                    fastPrintWriter.printf("\n\n", new Object[0]);
                    i2 += iArr[0];
                    i3 += iArr[1];
                }
                fastPrintWriter.printf("\nTotal ViewRootImpl: %d\n", Integer.valueOf(size));
                fastPrintWriter.printf("Total Views:        %d\n", Integer.valueOf(i2));
                fastPrintWriter.printf("Total DisplayList:  %.2f kB\n\n", Float.valueOf(i3 / 1024.0f));
            }
        } finally {
            fastPrintWriter.flush();
        }
    }

    private static String getWindowName(ViewRootImpl viewRootImpl) {
        return ((Object) viewRootImpl.mWindowAttributes.getTitle()) + "/" + viewRootImpl.getClass().getName() + '@' + Integer.toHexString(viewRootImpl.hashCode());
    }

    public void setStoppedState(IBinder iBinder, boolean z) {
        synchronized (this.mLock) {
            int size = this.mViews.size();
            for (int i = 0; i < size; i++) {
                if (iBinder == null || this.mParams.get(i).token == iBinder) {
                    this.mRoots.get(i).setStopped(z);
                }
            }
        }
    }

    public void reportNewConfiguration(Configuration configuration) {
        synchronized (this.mLock) {
            int size = this.mViews.size();
            Configuration configuration2 = new Configuration(configuration);
            for (int i = 0; i < size; i++) {
                this.mRoots.get(i).requestUpdateConfiguration(configuration2);
            }
        }
    }

    public void changeCanvasOpacity(IBinder iBinder, boolean z) {
        if (iBinder == null) {
            return;
        }
        synchronized (this.mLock) {
            for (int size = this.mParams.size() - 1; size >= 0; size--) {
                if (this.mParams.get(size).token == iBinder) {
                    this.mRoots.get(size).changeCanvasOpacity(z);
                    return;
                }
            }
        }
    }
}
