package android.service.notification;

import android.app.INotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.service.notification.INotificationListener;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public abstract class NotificationListenerService extends Service {
    public static final String SERVICE_INTERFACE = "android.service.notification.NotificationListenerService";
    private INotificationManager mNoMan;
    private final String TAG = NotificationListenerService.class.getSimpleName() + "[" + getClass().getSimpleName() + "]";
    private INotificationListenerWrapper mWrapper = null;

    public abstract void onNotificationPosted(StatusBarNotification statusBarNotification);

    public abstract void onNotificationRemoved(StatusBarNotification statusBarNotification);

    private final INotificationManager getNotificationInterface() {
        if (this.mNoMan == null) {
            this.mNoMan = INotificationManager.Stub.asInterface(ServiceManager.getService(Context.NOTIFICATION_SERVICE));
        }
        return this.mNoMan;
    }

    public final void cancelNotification(String str, String str2, int i) {
        try {
            getNotificationInterface().cancelNotificationFromListener(this.mWrapper, str, str2, i);
        } catch (RemoteException e) {
            Log.v(this.TAG, "Unable to contact notification manager", e);
        }
    }

    public final void cancelAllNotifications() {
        try {
            getNotificationInterface().cancelAllNotificationsFromListener(this.mWrapper);
        } catch (RemoteException e) {
            Log.v(this.TAG, "Unable to contact notification manager", e);
        }
    }

    public StatusBarNotification[] getActiveNotifications() {
        try {
            return getNotificationInterface().getActiveNotificationsFromListener(this.mWrapper);
        } catch (RemoteException e) {
            Log.v(this.TAG, "Unable to contact notification manager", e);
            return null;
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (this.mWrapper == null) {
            this.mWrapper = new INotificationListenerWrapper();
        }
        return this.mWrapper;
    }

    private class INotificationListenerWrapper extends INotificationListener.Stub {
        private INotificationListenerWrapper() {
        }

        @Override // android.service.notification.INotificationListener
        public void onNotificationPosted(StatusBarNotification statusBarNotification) {
            try {
                NotificationListenerService.this.onNotificationPosted(statusBarNotification);
            } catch (Throwable th) {
                Log.w(NotificationListenerService.this.TAG, "Error running onNotificationPosted", th);
            }
        }

        @Override // android.service.notification.INotificationListener
        public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
            try {
                NotificationListenerService.this.onNotificationRemoved(statusBarNotification);
            } catch (Throwable th) {
                Log.w(NotificationListenerService.this.TAG, "Error running onNotificationRemoved", th);
            }
        }
    }
}
