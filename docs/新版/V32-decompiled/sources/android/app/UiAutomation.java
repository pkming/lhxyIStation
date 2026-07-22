package android.app;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.accessibilityservice.IAccessibilityServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.display.DisplayManagerGlobal;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityInteractionClient;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/* JADX INFO: loaded from: classes.dex */
public final class UiAutomation {
    private static final int CONNECTION_ID_UNDEFINED = -1;
    private static final long CONNECT_TIMEOUT_MILLIS = 5000;
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "UiAutomation";
    public static final int ROTATION_FREEZE_0 = 0;
    public static final int ROTATION_FREEZE_180 = 2;
    public static final int ROTATION_FREEZE_270 = 3;
    public static final int ROTATION_FREEZE_90 = 1;
    public static final int ROTATION_FREEZE_CURRENT = -1;
    public static final int ROTATION_UNFREEZE = -2;
    private final IAccessibilityServiceClient mClient;
    private boolean mIsConnecting;
    private long mLastEventTimeMillis;
    private OnAccessibilityEventListener mOnAccessibilityEventListener;
    private final IUiAutomationConnection mUiAutomationConnection;
    private boolean mWaitingForEventDelivery;
    private final Object mLock = new Object();
    private final ArrayList<AccessibilityEvent> mEventQueue = new ArrayList<>();
    private int mConnectionId = -1;

    public interface AccessibilityEventFilter {
        boolean accept(AccessibilityEvent accessibilityEvent);
    }

    public interface OnAccessibilityEventListener {
        void onAccessibilityEvent(AccessibilityEvent accessibilityEvent);
    }

    private static float getDegreesForRotation(int i) {
        if (i == 1) {
            return 270.0f;
        }
        if (i != 2) {
            return i != 3 ? 0.0f : 90.0f;
        }
        return 180.0f;
    }

    public UiAutomation(Looper looper, IUiAutomationConnection iUiAutomationConnection) {
        if (looper == null) {
            throw new IllegalArgumentException("Looper cannot be null!");
        }
        if (iUiAutomationConnection == null) {
            throw new IllegalArgumentException("Connection cannot be null!");
        }
        this.mUiAutomationConnection = iUiAutomationConnection;
        this.mClient = new IAccessibilityServiceClientImpl(looper);
    }

    public void connect() {
        synchronized (this.mLock) {
            throwIfConnectedLocked();
            if (this.mIsConnecting) {
                return;
            }
            this.mIsConnecting = true;
            try {
                this.mUiAutomationConnection.connect(this.mClient);
                synchronized (this.mLock) {
                    long jUptimeMillis = SystemClock.uptimeMillis();
                    while (true) {
                        try {
                            if (!isConnectedLocked()) {
                                long jUptimeMillis2 = 5000 - (SystemClock.uptimeMillis() - jUptimeMillis);
                                if (jUptimeMillis2 <= 0) {
                                    throw new RuntimeException("Error while connecting UiAutomation");
                                }
                                try {
                                    this.mLock.wait(jUptimeMillis2);
                                } catch (InterruptedException unused) {
                                }
                            }
                        } finally {
                            this.mIsConnecting = false;
                        }
                    }
                }
            } catch (RemoteException e) {
                throw new RuntimeException("Error while connecting UiAutomation", e);
            }
        }
    }

    public void disconnect() {
        synchronized (this.mLock) {
            if (this.mIsConnecting) {
                throw new IllegalStateException("Cannot call disconnect() while connecting!");
            }
            throwIfNotConnectedLocked();
            this.mConnectionId = -1;
        }
        try {
            this.mUiAutomationConnection.disconnect();
        } catch (RemoteException e) {
            throw new RuntimeException("Error while disconnecting UiAutomation", e);
        }
    }

    public int getConnectionId() {
        int i;
        synchronized (this.mLock) {
            throwIfNotConnectedLocked();
            i = this.mConnectionId;
        }
        return i;
    }

    public void setOnAccessibilityEventListener(OnAccessibilityEventListener onAccessibilityEventListener) {
        synchronized (this.mLock) {
            this.mOnAccessibilityEventListener = onAccessibilityEventListener;
        }
    }

    public final boolean performGlobalAction(int i) {
        IAccessibilityServiceConnection connection;
        synchronized (this.mLock) {
            throwIfNotConnectedLocked();
            connection = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
        }
        if (connection == null) {
            return false;
        }
        try {
            return connection.performGlobalAction(i);
        } catch (RemoteException e) {
            Log.w(LOG_TAG, "Error while calling performGlobalAction", e);
            return false;
        }
    }

    public final AccessibilityServiceInfo getServiceInfo() {
        IAccessibilityServiceConnection connection;
        synchronized (this.mLock) {
            throwIfNotConnectedLocked();
            connection = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
        }
        if (connection == null) {
            return null;
        }
        try {
            return connection.getServiceInfo();
        } catch (RemoteException e) {
            Log.w(LOG_TAG, "Error while getting AccessibilityServiceInfo", e);
            return null;
        }
    }

    public final void setServiceInfo(AccessibilityServiceInfo accessibilityServiceInfo) {
        IAccessibilityServiceConnection connection;
        synchronized (this.mLock) {
            throwIfNotConnectedLocked();
            AccessibilityInteractionClient.getInstance().clearCache();
            connection = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
        }
        if (connection != null) {
            try {
                connection.setServiceInfo(accessibilityServiceInfo);
            } catch (RemoteException e) {
                Log.w(LOG_TAG, "Error while setting AccessibilityServiceInfo", e);
            }
        }
    }

    public AccessibilityNodeInfo getRootInActiveWindow() {
        int i;
        synchronized (this.mLock) {
            throwIfNotConnectedLocked();
            i = this.mConnectionId;
        }
        return AccessibilityInteractionClient.getInstance().getRootInActiveWindow(i);
    }

    public boolean injectInputEvent(InputEvent inputEvent, boolean z) {
        synchronized (this.mLock) {
            throwIfNotConnectedLocked();
        }
        try {
            return this.mUiAutomationConnection.injectInputEvent(inputEvent, z);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error while injecting input event!", e);
            return false;
        }
    }

    public boolean setRotation(int i) {
        synchronized (this.mLock) {
            throwIfNotConnectedLocked();
        }
        if (i == -2 || i == -1 || i == 0 || i == 1 || i == 2 || i == 3) {
            try {
                this.mUiAutomationConnection.setRotation(i);
                return true;
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Error while setting rotation!", e);
                return false;
            }
        }
        throw new IllegalArgumentException("Invalid rotation.");
    }

    public AccessibilityEvent executeAndWaitForEvent(Runnable runnable, AccessibilityEventFilter accessibilityEventFilter, long j) throws TimeoutException {
        AccessibilityEvent accessibilityEventRemove;
        synchronized (this.mLock) {
            throwIfNotConnectedLocked();
            this.mEventQueue.clear();
            this.mWaitingForEventDelivery = true;
        }
        long jUptimeMillis = SystemClock.uptimeMillis();
        runnable.run();
        synchronized (this.mLock) {
            try {
                long jUptimeMillis2 = SystemClock.uptimeMillis();
                while (true) {
                    if (!this.mEventQueue.isEmpty()) {
                        accessibilityEventRemove = this.mEventQueue.remove(0);
                        if (accessibilityEventRemove.getEventTime() >= jUptimeMillis) {
                            if (!accessibilityEventFilter.accept(accessibilityEventRemove)) {
                                accessibilityEventRemove.recycle();
                            }
                        }
                    } else {
                        long jUptimeMillis3 = j - (SystemClock.uptimeMillis() - jUptimeMillis2);
                        if (jUptimeMillis3 <= 0) {
                            throw new TimeoutException("Expected event not received within: " + j + " ms.");
                        }
                        try {
                            this.mLock.wait(jUptimeMillis3);
                        } catch (InterruptedException unused) {
                        }
                    }
                }
            } finally {
                this.mWaitingForEventDelivery = false;
                this.mEventQueue.clear();
                this.mLock.notifyAll();
            }
        }
        return accessibilityEventRemove;
    }

    public void waitForIdle(long j, long j2) throws TimeoutException {
        synchronized (this.mLock) {
            throwIfNotConnectedLocked();
            long jUptimeMillis = SystemClock.uptimeMillis();
            if (this.mLastEventTimeMillis <= 0) {
                this.mLastEventTimeMillis = jUptimeMillis;
            }
            while (true) {
                long jUptimeMillis2 = SystemClock.uptimeMillis();
                if (j2 - (jUptimeMillis2 - jUptimeMillis) <= 0) {
                    throw new TimeoutException("No idle state with idle timeout: " + j + " within global timeout: " + j2);
                }
                long j3 = j - (jUptimeMillis2 - this.mLastEventTimeMillis);
                if (j3 > 0) {
                    try {
                        this.mLock.wait(j3);
                    } catch (InterruptedException unused) {
                    }
                }
            }
        }
    }

    public Bitmap takeScreenshot() {
        float f;
        float f2;
        synchronized (this.mLock) {
            throwIfNotConnectedLocked();
        }
        Display realDisplay = DisplayManagerGlobal.getInstance().getRealDisplay(0);
        Point point = new Point();
        realDisplay.getRealSize(point);
        int i = point.x;
        int i2 = point.y;
        int rotation = realDisplay.getRotation();
        if (rotation == 0) {
            f = i;
            f2 = i2;
        } else {
            if (rotation != 1) {
                if (rotation != 2) {
                    if (rotation != 3) {
                        throw new IllegalArgumentException("Invalid rotation: " + rotation);
                    }
                }
                f = i;
                f2 = i2;
            }
            f = i2;
            f2 = i;
        }
        try {
            Bitmap bitmapTakeScreenshot = this.mUiAutomationConnection.takeScreenshot((int) f, (int) f2);
            if (bitmapTakeScreenshot == null) {
                return null;
            }
            if (rotation != 0) {
                Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmapCreateBitmap);
                canvas.translate(bitmapCreateBitmap.getWidth() / 2, bitmapCreateBitmap.getHeight() / 2);
                canvas.rotate(getDegreesForRotation(rotation));
                canvas.translate((-f) / 2.0f, (-f2) / 2.0f);
                canvas.drawBitmap(bitmapTakeScreenshot, 0.0f, 0.0f, (Paint) null);
                canvas.setBitmap(null);
                bitmapTakeScreenshot = bitmapCreateBitmap;
            }
            bitmapTakeScreenshot.setHasAlpha(false);
            return bitmapTakeScreenshot;
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error while taking screnshot!", e);
            return null;
        }
    }

    public void setRunAsMonkey(boolean z) {
        synchronized (this.mLock) {
            throwIfNotConnectedLocked();
        }
        try {
            ActivityManagerNative.getDefault().setUserIsMonkey(z);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error while setting run as monkey!", e);
        }
    }

    private boolean isConnectedLocked() {
        return this.mConnectionId != -1;
    }

    private void throwIfConnectedLocked() {
        if (this.mConnectionId != -1) {
            throw new IllegalStateException("UiAutomation not connected!");
        }
    }

    private void throwIfNotConnectedLocked() {
        if (!isConnectedLocked()) {
            throw new IllegalStateException("UiAutomation not connected!");
        }
    }

    private class IAccessibilityServiceClientImpl extends AccessibilityService.IAccessibilityServiceClientWrapper {
        public IAccessibilityServiceClientImpl(Looper looper) {
            super(null, looper, new AccessibilityService.Callbacks() { // from class: android.app.UiAutomation.IAccessibilityServiceClientImpl.1
                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public boolean onGesture(int i) {
                    return false;
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void onInterrupt() {
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public boolean onKeyEvent(KeyEvent keyEvent) {
                    return false;
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void onServiceConnected() {
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void onSetConnectionId(int i) {
                    synchronized (uiAutomation.mLock) {
                        uiAutomation.mConnectionId = i;
                        uiAutomation.mLock.notifyAll();
                    }
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
                    synchronized (uiAutomation.mLock) {
                        uiAutomation.mLastEventTimeMillis = accessibilityEvent.getEventTime();
                        if (uiAutomation.mWaitingForEventDelivery) {
                            uiAutomation.mEventQueue.add(AccessibilityEvent.obtain(accessibilityEvent));
                        }
                        uiAutomation.mLock.notifyAll();
                    }
                    OnAccessibilityEventListener onAccessibilityEventListener = uiAutomation.mOnAccessibilityEventListener;
                    if (onAccessibilityEventListener != null) {
                        onAccessibilityEventListener.onAccessibilityEvent(AccessibilityEvent.obtain(accessibilityEvent));
                    }
                }
            });
        }
    }
}
