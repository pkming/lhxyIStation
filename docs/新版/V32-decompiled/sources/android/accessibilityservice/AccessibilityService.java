package android.accessibilityservice;

import android.accessibilityservice.IAccessibilityServiceClient;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityInteractionClient;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.os.HandlerCaller;

/* JADX INFO: loaded from: classes.dex */
public abstract class AccessibilityService extends Service {
    public static final int GESTURE_SWIPE_DOWN = 2;
    public static final int GESTURE_SWIPE_DOWN_AND_LEFT = 15;
    public static final int GESTURE_SWIPE_DOWN_AND_RIGHT = 16;
    public static final int GESTURE_SWIPE_DOWN_AND_UP = 8;
    public static final int GESTURE_SWIPE_LEFT = 3;
    public static final int GESTURE_SWIPE_LEFT_AND_DOWN = 10;
    public static final int GESTURE_SWIPE_LEFT_AND_RIGHT = 5;
    public static final int GESTURE_SWIPE_LEFT_AND_UP = 9;
    public static final int GESTURE_SWIPE_RIGHT = 4;
    public static final int GESTURE_SWIPE_RIGHT_AND_DOWN = 12;
    public static final int GESTURE_SWIPE_RIGHT_AND_LEFT = 6;
    public static final int GESTURE_SWIPE_RIGHT_AND_UP = 11;
    public static final int GESTURE_SWIPE_UP = 1;
    public static final int GESTURE_SWIPE_UP_AND_DOWN = 7;
    public static final int GESTURE_SWIPE_UP_AND_LEFT = 13;
    public static final int GESTURE_SWIPE_UP_AND_RIGHT = 14;
    public static final int GLOBAL_ACTION_BACK = 1;
    public static final int GLOBAL_ACTION_HOME = 2;
    public static final int GLOBAL_ACTION_NOTIFICATIONS = 4;
    public static final int GLOBAL_ACTION_QUICK_SETTINGS = 5;
    public static final int GLOBAL_ACTION_RECENTS = 3;
    private static final String LOG_TAG = "AccessibilityService";
    public static final String SERVICE_INTERFACE = "android.accessibilityservice.AccessibilityService";
    public static final String SERVICE_META_DATA = "android.accessibilityservice";
    private int mConnectionId;
    private AccessibilityServiceInfo mInfo;

    public interface Callbacks {
        void onAccessibilityEvent(AccessibilityEvent accessibilityEvent);

        boolean onGesture(int i);

        void onInterrupt();

        boolean onKeyEvent(KeyEvent keyEvent);

        void onServiceConnected();

        void onSetConnectionId(int i);
    }

    public abstract void onAccessibilityEvent(AccessibilityEvent accessibilityEvent);

    protected boolean onGesture(int i) {
        return false;
    }

    public abstract void onInterrupt();

    protected boolean onKeyEvent(KeyEvent keyEvent) {
        return false;
    }

    protected void onServiceConnected() {
    }

    public AccessibilityNodeInfo getRootInActiveWindow() {
        return AccessibilityInteractionClient.getInstance().getRootInActiveWindow(this.mConnectionId);
    }

    public final boolean performGlobalAction(int i) {
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
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
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
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
        this.mInfo = accessibilityServiceInfo;
        sendServiceInfo();
    }

    private void sendServiceInfo() {
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
        AccessibilityServiceInfo accessibilityServiceInfo = this.mInfo;
        if (accessibilityServiceInfo == null || connection == null) {
            return;
        }
        try {
            connection.setServiceInfo(accessibilityServiceInfo);
            this.mInfo = null;
            AccessibilityInteractionClient.getInstance().clearCache();
        } catch (RemoteException e) {
            Log.w(LOG_TAG, "Error while setting AccessibilityServiceInfo", e);
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return new IAccessibilityServiceClientWrapper(this, getMainLooper(), new Callbacks() { // from class: android.accessibilityservice.AccessibilityService.1
            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onServiceConnected() {
                AccessibilityService.this.onServiceConnected();
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onInterrupt() {
                AccessibilityService.this.onInterrupt();
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
                AccessibilityService.this.onAccessibilityEvent(accessibilityEvent);
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onSetConnectionId(int i) {
                AccessibilityService.this.mConnectionId = i;
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public boolean onGesture(int i) {
                return AccessibilityService.this.onGesture(i);
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public boolean onKeyEvent(KeyEvent keyEvent) {
                return AccessibilityService.this.onKeyEvent(keyEvent);
            }
        });
    }

    public static class IAccessibilityServiceClientWrapper extends IAccessibilityServiceClient.Stub implements HandlerCaller.Callback {
        private static final int DO_CLEAR_ACCESSIBILITY_NODE_INFO_CACHE = 50;
        private static final int DO_ON_ACCESSIBILITY_EVENT = 30;
        private static final int DO_ON_GESTURE = 40;
        private static final int DO_ON_INTERRUPT = 20;
        private static final int DO_ON_KEY_EVENT = 60;
        private static final int DO_SET_SET_CONNECTION = 10;
        static final int NO_ID = -1;
        private final Callbacks mCallback;
        private final HandlerCaller mCaller;
        private int mConnectionId;

        public IAccessibilityServiceClientWrapper(Context context, Looper looper, Callbacks callbacks) {
            this.mCallback = callbacks;
            this.mCaller = new HandlerCaller(context, looper, this, true);
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void setConnection(IAccessibilityServiceConnection iAccessibilityServiceConnection, int i) {
            this.mCaller.sendMessage(this.mCaller.obtainMessageIO(10, i, iAccessibilityServiceConnection));
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onInterrupt() {
            this.mCaller.sendMessage(this.mCaller.obtainMessage(20));
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
            this.mCaller.sendMessage(this.mCaller.obtainMessageO(30, accessibilityEvent));
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onGesture(int i) {
            this.mCaller.sendMessage(this.mCaller.obtainMessageI(40, i));
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void clearAccessibilityNodeInfoCache() {
            this.mCaller.sendMessage(this.mCaller.obtainMessage(50));
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onKeyEvent(KeyEvent keyEvent, int i) {
            this.mCaller.sendMessage(this.mCaller.obtainMessageIO(60, i, keyEvent));
        }

        public void executeMessage(Message message) {
            int i = message.what;
            if (i == 10) {
                this.mConnectionId = message.arg1;
                IAccessibilityServiceConnection iAccessibilityServiceConnection = (IAccessibilityServiceConnection) message.obj;
                if (iAccessibilityServiceConnection != null) {
                    AccessibilityInteractionClient.getInstance().addConnection(this.mConnectionId, iAccessibilityServiceConnection);
                    this.mCallback.onSetConnectionId(this.mConnectionId);
                    this.mCallback.onServiceConnected();
                    return;
                } else {
                    AccessibilityInteractionClient.getInstance().removeConnection(this.mConnectionId);
                    AccessibilityInteractionClient.getInstance().clearCache();
                    this.mCallback.onSetConnectionId(-1);
                    return;
                }
            }
            if (i == 20) {
                this.mCallback.onInterrupt();
                return;
            }
            if (i == 30) {
                AccessibilityEvent accessibilityEvent = (AccessibilityEvent) message.obj;
                if (accessibilityEvent != null) {
                    AccessibilityInteractionClient.getInstance().onAccessibilityEvent(accessibilityEvent);
                    this.mCallback.onAccessibilityEvent(accessibilityEvent);
                    accessibilityEvent.recycle();
                    return;
                }
                return;
            }
            if (i == 40) {
                this.mCallback.onGesture(message.arg1);
                return;
            }
            if (i == 50) {
                AccessibilityInteractionClient.getInstance().clearCache();
                return;
            }
            if (i == 60) {
                KeyEvent keyEvent = (KeyEvent) message.obj;
                try {
                    IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
                    if (connection != null) {
                        try {
                            connection.setOnKeyEventResult(this.mCallback.onKeyEvent(keyEvent), message.arg1);
                        } catch (RemoteException unused) {
                        }
                    }
                    return;
                } finally {
                    keyEvent.recycle();
                }
            }
            Log.w(AccessibilityService.LOG_TAG, "Unknown message type " + message.what);
        }
    }
}
