package android.view.accessibility;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import android.view.IWindow;
import android.view.accessibility.IAccessibilityManager;
import android.view.accessibility.IAccessibilityManagerClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class AccessibilityManager {
    private static final boolean DEBUG = false;
    private static final int DO_SET_STATE = 10;
    private static final String LOG_TAG = "AccessibilityManager";
    public static final int STATE_FLAG_ACCESSIBILITY_ENABLED = 1;
    public static final int STATE_FLAG_TOUCH_EXPLORATION_ENABLED = 2;
    private static AccessibilityManager sInstance;
    static final Object sInstanceSync = new Object();
    final IAccessibilityManagerClient.Stub mClient;
    final Handler mHandler;
    boolean mIsEnabled;
    boolean mIsTouchExplorationEnabled;
    final IAccessibilityManager mService;
    final int mUserId;
    private final CopyOnWriteArrayList<AccessibilityStateChangeListener> mAccessibilityStateChangeListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<TouchExplorationStateChangeListener> mTouchExplorationStateChangeListeners = new CopyOnWriteArrayList<>();

    public interface AccessibilityStateChangeListener {
        void onAccessibilityStateChanged(boolean z);
    }

    public interface TouchExplorationStateChangeListener {
        void onTouchExplorationStateChanged(boolean z);
    }

    class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 10) {
                AccessibilityManager.this.setState(message.arg1);
            } else {
                Log.w(AccessibilityManager.LOG_TAG, "Unknown message type: " + message.what);
            }
        }
    }

    public static AccessibilityManager getInstance(Context context) {
        synchronized (sInstanceSync) {
            if (sInstance == null) {
                sInstance = new AccessibilityManager(context, IAccessibilityManager.Stub.asInterface(ServiceManager.getService(Context.ACCESSIBILITY_SERVICE)), (Binder.getCallingUid() == 1000 || context.checkCallingOrSelfPermission(Manifest.permission.INTERACT_ACROSS_USERS) == 0 || context.checkCallingOrSelfPermission(Manifest.permission.INTERACT_ACROSS_USERS_FULL) == 0) ? -2 : UserHandle.myUserId());
            }
        }
        return sInstance;
    }

    public AccessibilityManager(Context context, IAccessibilityManager iAccessibilityManager, int i) {
        IAccessibilityManagerClient.Stub stub = new IAccessibilityManagerClient.Stub() { // from class: android.view.accessibility.AccessibilityManager.1
            @Override // android.view.accessibility.IAccessibilityManagerClient
            public void setState(int i2) {
                AccessibilityManager.this.mHandler.obtainMessage(10, i2, 0).sendToTarget();
            }
        };
        this.mClient = stub;
        this.mHandler = new MyHandler(context.getMainLooper());
        this.mService = iAccessibilityManager;
        this.mUserId = i;
        try {
            setState(iAccessibilityManager.addClient(stub, i));
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "AccessibilityManagerService is dead", e);
        }
    }

    public boolean isEnabled() {
        boolean z;
        synchronized (this.mHandler) {
            z = this.mIsEnabled;
        }
        return z;
    }

    public boolean isTouchExplorationEnabled() {
        boolean z;
        synchronized (this.mHandler) {
            z = this.mIsTouchExplorationEnabled;
        }
        return z;
    }

    public IAccessibilityManagerClient getClient() {
        return (IAccessibilityManagerClient) this.mClient.asBinder();
    }

    public void sendAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (!this.mIsEnabled) {
            throw new IllegalStateException("Accessibility off. Did you forget to check that?");
        }
        boolean zSendAccessibilityEvent = false;
        try {
            try {
                accessibilityEvent.setEventTime(SystemClock.uptimeMillis());
                long jClearCallingIdentity = Binder.clearCallingIdentity();
                zSendAccessibilityEvent = this.mService.sendAccessibilityEvent(accessibilityEvent, this.mUserId);
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Error during sending " + accessibilityEvent + " ", e);
                if (zSendAccessibilityEvent) {
                }
            }
            if (zSendAccessibilityEvent) {
                accessibilityEvent.recycle();
            }
        } catch (Throwable th) {
            if (zSendAccessibilityEvent) {
                accessibilityEvent.recycle();
            }
            throw th;
        }
    }

    public void interrupt() {
        if (!this.mIsEnabled) {
            throw new IllegalStateException("Accessibility off. Did you forget to check that?");
        }
        try {
            this.mService.interrupt(this.mUserId);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error while requesting interrupt from all services. ", e);
        }
    }

    @Deprecated
    public List<ServiceInfo> getAccessibilityServiceList() {
        List<AccessibilityServiceInfo> installedAccessibilityServiceList = getInstalledAccessibilityServiceList();
        ArrayList arrayList = new ArrayList();
        int size = installedAccessibilityServiceList.size();
        for (int i = 0; i < size; i++) {
            arrayList.add(installedAccessibilityServiceList.get(i).getResolveInfo().serviceInfo);
        }
        return Collections.unmodifiableList(arrayList);
    }

    public List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList() {
        List<AccessibilityServiceInfo> installedAccessibilityServiceList;
        try {
            installedAccessibilityServiceList = this.mService.getInstalledAccessibilityServiceList(this.mUserId);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error while obtaining the installed AccessibilityServices. ", e);
            installedAccessibilityServiceList = null;
        }
        return Collections.unmodifiableList(installedAccessibilityServiceList);
    }

    public List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int i) {
        List<AccessibilityServiceInfo> enabledAccessibilityServiceList;
        try {
            enabledAccessibilityServiceList = this.mService.getEnabledAccessibilityServiceList(i, this.mUserId);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error while obtaining the installed AccessibilityServices. ", e);
            enabledAccessibilityServiceList = null;
        }
        return Collections.unmodifiableList(enabledAccessibilityServiceList);
    }

    public boolean addAccessibilityStateChangeListener(AccessibilityStateChangeListener accessibilityStateChangeListener) {
        return this.mAccessibilityStateChangeListeners.add(accessibilityStateChangeListener);
    }

    public boolean removeAccessibilityStateChangeListener(AccessibilityStateChangeListener accessibilityStateChangeListener) {
        return this.mAccessibilityStateChangeListeners.remove(accessibilityStateChangeListener);
    }

    public boolean addTouchExplorationStateChangeListener(TouchExplorationStateChangeListener touchExplorationStateChangeListener) {
        return this.mTouchExplorationStateChangeListeners.add(touchExplorationStateChangeListener);
    }

    public boolean removeTouchExplorationStateChangeListener(TouchExplorationStateChangeListener touchExplorationStateChangeListener) {
        return this.mTouchExplorationStateChangeListeners.remove(touchExplorationStateChangeListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setState(int i) {
        boolean z = (i & 1) != 0;
        boolean z2 = (i & 2) != 0;
        synchronized (this.mHandler) {
            boolean z3 = this.mIsEnabled;
            boolean z4 = this.mIsTouchExplorationEnabled;
            this.mIsEnabled = z;
            this.mIsTouchExplorationEnabled = z2;
            if (z3 != z) {
                notifyAccessibilityStateChangedLh();
            }
            if (z4 != z2) {
                notifyTouchExplorationStateChangedLh();
            }
        }
    }

    private void notifyAccessibilityStateChangedLh() {
        int size = this.mAccessibilityStateChangeListeners.size();
        for (int i = 0; i < size; i++) {
            this.mAccessibilityStateChangeListeners.get(i).onAccessibilityStateChanged(this.mIsEnabled);
        }
    }

    private void notifyTouchExplorationStateChangedLh() {
        int size = this.mTouchExplorationStateChangeListeners.size();
        for (int i = 0; i < size; i++) {
            this.mTouchExplorationStateChangeListeners.get(i).onTouchExplorationStateChanged(this.mIsTouchExplorationEnabled);
        }
    }

    public int addAccessibilityInteractionConnection(IWindow iWindow, IAccessibilityInteractionConnection iAccessibilityInteractionConnection) {
        try {
            return this.mService.addAccessibilityInteractionConnection(iWindow, iAccessibilityInteractionConnection, this.mUserId);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error while adding an accessibility interaction connection. ", e);
            return -1;
        }
    }

    public void removeAccessibilityInteractionConnection(IWindow iWindow) {
        try {
            this.mService.removeAccessibilityInteractionConnection(iWindow);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error while removing an accessibility interaction connection. ", e);
        }
    }
}
