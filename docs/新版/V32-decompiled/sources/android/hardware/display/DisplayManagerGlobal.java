package android.hardware.display;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.IDisplayManager;
import android.hardware.display.IDisplayManagerCallback;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.DisplayAdjustments;
import android.view.DisplayInfo;
import android.view.Surface;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class DisplayManagerGlobal {
    private static final boolean DEBUG = false;
    public static final int EVENT_DISPLAY_ADDED = 1;
    public static final int EVENT_DISPLAY_CHANGED = 2;
    public static final int EVENT_DISPLAY_REMOVED = 3;
    private static final String TAG = "DisplayManager";
    private static final boolean USE_CACHE = false;
    private static DisplayManagerGlobal sInstance;
    private DisplayManagerCallback mCallback;
    private int[] mDisplayIdCache;
    private final IDisplayManager mDm;
    private int mWifiDisplayScanNestCount;
    private final Object mLock = new Object();
    private final ArrayList<DisplayListenerDelegate> mDisplayListeners = new ArrayList<>();
    private final SparseArray<DisplayInfo> mDisplayInfoCache = new SparseArray<>();

    private DisplayManagerGlobal(IDisplayManager iDisplayManager) {
        this.mDm = iDisplayManager;
    }

    public static DisplayManagerGlobal getInstance() {
        DisplayManagerGlobal displayManagerGlobal;
        IBinder service;
        synchronized (DisplayManagerGlobal.class) {
            if (sInstance == null && (service = ServiceManager.getService(Context.DISPLAY_SERVICE)) != null) {
                sInstance = new DisplayManagerGlobal(IDisplayManager.Stub.asInterface(service));
            }
            displayManagerGlobal = sInstance;
        }
        return displayManagerGlobal;
    }

    public DisplayInfo getDisplayInfo(int i) {
        try {
            synchronized (this.mLock) {
                DisplayInfo displayInfo = this.mDm.getDisplayInfo(i);
                if (displayInfo == null) {
                    return null;
                }
                registerCallbackIfNeededLocked();
                return displayInfo;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Could not get display information from display manager.", e);
            return null;
        }
    }

    public int[] getDisplayIds() {
        int[] displayIds;
        try {
            synchronized (this.mLock) {
                displayIds = this.mDm.getDisplayIds();
                registerCallbackIfNeededLocked();
            }
            return displayIds;
        } catch (RemoteException e) {
            Log.e(TAG, "Could not get display ids from display manager.", e);
            return new int[]{0};
        }
    }

    public Display getCompatibleDisplay(int i, DisplayAdjustments displayAdjustments) {
        DisplayInfo displayInfo = getDisplayInfo(i);
        if (displayInfo == null) {
            return null;
        }
        return new Display(this, i, displayInfo, displayAdjustments);
    }

    public Display getRealDisplay(int i) {
        return getCompatibleDisplay(i, DisplayAdjustments.DEFAULT_DISPLAY_ADJUSTMENTS);
    }

    public Display getRealDisplay(int i, IBinder iBinder) {
        return getCompatibleDisplay(i, new DisplayAdjustments(iBinder));
    }

    public void registerDisplayListener(DisplayManager.DisplayListener displayListener, Handler handler) {
        if (displayListener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        synchronized (this.mLock) {
            if (findDisplayListenerLocked(displayListener) < 0) {
                this.mDisplayListeners.add(new DisplayListenerDelegate(displayListener, handler));
                registerCallbackIfNeededLocked();
            }
        }
    }

    public void unregisterDisplayListener(DisplayManager.DisplayListener displayListener) {
        if (displayListener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        synchronized (this.mLock) {
            int iFindDisplayListenerLocked = findDisplayListenerLocked(displayListener);
            if (iFindDisplayListenerLocked >= 0) {
                this.mDisplayListeners.get(iFindDisplayListenerLocked).clearEvents();
                this.mDisplayListeners.remove(iFindDisplayListenerLocked);
            }
        }
    }

    private int findDisplayListenerLocked(DisplayManager.DisplayListener displayListener) {
        int size = this.mDisplayListeners.size();
        for (int i = 0; i < size; i++) {
            if (this.mDisplayListeners.get(i).mListener == displayListener) {
                return i;
            }
        }
        return -1;
    }

    private void registerCallbackIfNeededLocked() {
        if (this.mCallback == null) {
            DisplayManagerCallback displayManagerCallback = new DisplayManagerCallback();
            this.mCallback = displayManagerCallback;
            try {
                this.mDm.registerCallback(displayManagerCallback);
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to register callback with display manager service.", e);
                this.mCallback = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDisplayEvent(int i, int i2) {
        synchronized (this.mLock) {
            int size = this.mDisplayListeners.size();
            for (int i3 = 0; i3 < size; i3++) {
                this.mDisplayListeners.get(i3).sendDisplayEvent(i, i2);
            }
        }
    }

    public void startWifiDisplayScan() {
        synchronized (this.mLock) {
            int i = this.mWifiDisplayScanNestCount;
            this.mWifiDisplayScanNestCount = i + 1;
            if (i == 0) {
                registerCallbackIfNeededLocked();
                try {
                    this.mDm.startWifiDisplayScan();
                } catch (RemoteException e) {
                    Log.e(TAG, "Failed to scan for Wifi displays.", e);
                }
            }
        }
    }

    public void stopWifiDisplayScan() {
        synchronized (this.mLock) {
            int i = this.mWifiDisplayScanNestCount - 1;
            this.mWifiDisplayScanNestCount = i;
            if (i == 0) {
                try {
                    this.mDm.stopWifiDisplayScan();
                } catch (RemoteException e) {
                    Log.e(TAG, "Failed to scan for Wifi displays.", e);
                }
            } else if (i < 0) {
                Log.wtf(TAG, "Wifi display scan nest count became negative: " + this.mWifiDisplayScanNestCount);
                this.mWifiDisplayScanNestCount = 0;
            }
        }
    }

    public void connectWifiDisplay(String str) {
        if (str == null) {
            throw new IllegalArgumentException("deviceAddress must not be null");
        }
        try {
            this.mDm.connectWifiDisplay(str);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to connect to Wifi display " + str + ".", e);
        }
    }

    public void pauseWifiDisplay() {
        try {
            this.mDm.pauseWifiDisplay();
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to pause Wifi display.", e);
        }
    }

    public void resumeWifiDisplay() {
        try {
            this.mDm.resumeWifiDisplay();
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to resume Wifi display.", e);
        }
    }

    public void disconnectWifiDisplay() {
        try {
            this.mDm.disconnectWifiDisplay();
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to disconnect from Wifi display.", e);
        }
    }

    public void renameWifiDisplay(String str, String str2) {
        if (str == null) {
            throw new IllegalArgumentException("deviceAddress must not be null");
        }
        try {
            this.mDm.renameWifiDisplay(str, str2);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to rename Wifi display " + str + " with alias " + str2 + ".", e);
        }
    }

    public void forgetWifiDisplay(String str) {
        if (str == null) {
            throw new IllegalArgumentException("deviceAddress must not be null");
        }
        try {
            this.mDm.forgetWifiDisplay(str);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to forget Wifi display.", e);
        }
    }

    public WifiDisplayStatus getWifiDisplayStatus() {
        try {
            return this.mDm.getWifiDisplayStatus();
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to get Wifi display status.", e);
            return new WifiDisplayStatus();
        }
    }

    public VirtualDisplay createVirtualDisplay(Context context, String str, int i, int i2, int i3, Surface surface, int i4) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("name must be non-null and non-empty");
        }
        if (i <= 0 || i2 <= 0 || i3 <= 0) {
            throw new IllegalArgumentException("width, height, and densityDpi must be greater than 0");
        }
        if (surface == null) {
            throw new IllegalArgumentException("surface must not be null");
        }
        Binder binder = new Binder();
        try {
            int iCreateVirtualDisplay = this.mDm.createVirtualDisplay(binder, context.getPackageName(), str, i, i2, i3, surface, i4);
            if (iCreateVirtualDisplay < 0) {
                Log.e(TAG, "Could not create virtual display: " + str);
                return null;
            }
            Display realDisplay = getRealDisplay(iCreateVirtualDisplay);
            if (realDisplay == null) {
                Log.wtf(TAG, "Could not obtain display info for newly created virtual display: " + str);
                try {
                    this.mDm.releaseVirtualDisplay(binder);
                } catch (RemoteException unused) {
                }
                return null;
            }
            return new VirtualDisplay(this, realDisplay, binder);
        } catch (RemoteException e) {
            Log.e(TAG, "Could not create virtual display: " + str, e);
            return null;
        }
    }

    public void releaseVirtualDisplay(IBinder iBinder) {
        try {
            this.mDm.releaseVirtualDisplay(iBinder);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to release virtual display.", e);
        }
    }

    private final class DisplayManagerCallback extends IDisplayManagerCallback.Stub {
        private DisplayManagerCallback() {
        }

        @Override // android.hardware.display.IDisplayManagerCallback
        public void onDisplayEvent(int i, int i2) {
            DisplayManagerGlobal.this.handleDisplayEvent(i, i2);
        }
    }

    private static final class DisplayListenerDelegate extends Handler {
        public final DisplayManager.DisplayListener mListener;

        public DisplayListenerDelegate(DisplayManager.DisplayListener displayListener, Handler handler) {
            super(handler != null ? handler.getLooper() : Looper.myLooper(), null, true);
            this.mListener = displayListener;
        }

        public void sendDisplayEvent(int i, int i2) {
            sendMessage(obtainMessage(i2, i, 0));
        }

        public void clearEvents() {
            removeCallbacksAndMessages(null);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                this.mListener.onDisplayAdded(message.arg1);
            } else if (i == 2) {
                this.mListener.onDisplayChanged(message.arg1);
            } else {
                if (i != 3) {
                    return;
                }
                this.mListener.onDisplayRemoved(message.arg1);
            }
        }
    }

    public int getDisplayParameter(int i, int i2, int i3, int i4) {
        try {
            return this.mDm.getDisplayParameter(i, i2, i3, i4);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to get display parameter", e);
            return -1;
        }
    }

    public int setDisplayParameter(int i, int i2, int i3, int i4, int i5) {
        try {
            return this.mDm.setDisplayParameter(i, i2, i3, i4, i5);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to set display parameter.", e);
            return -1;
        }
    }
}
