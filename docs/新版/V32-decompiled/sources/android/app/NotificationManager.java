package android.app;

import android.app.INotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.UserHandle;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class NotificationManager {
    private static String TAG = "NotificationManager";
    private static boolean localLOGV = false;
    private static INotificationManager sService;
    private Context mContext;

    public static INotificationManager getService() {
        INotificationManager iNotificationManager = sService;
        if (iNotificationManager != null) {
            return iNotificationManager;
        }
        INotificationManager iNotificationManagerAsInterface = INotificationManager.Stub.asInterface(ServiceManager.getService(Context.NOTIFICATION_SERVICE));
        sService = iNotificationManagerAsInterface;
        return iNotificationManagerAsInterface;
    }

    NotificationManager(Context context, Handler handler) {
        this.mContext = context;
    }

    public static NotificationManager from(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void notify(int i, Notification notification) {
        notify(null, i, notification);
    }

    public void notify(String str, int i, Notification notification) {
        int[] iArr = new int[1];
        INotificationManager service = getService();
        String packageName = this.mContext.getPackageName();
        if (notification.sound != null) {
            notification.sound = notification.sound.getCanonicalUri();
            if (StrictMode.vmFileUriExposureEnabled()) {
                notification.sound.checkFileUriExposed("Notification.sound");
            }
        }
        if (localLOGV) {
            Log.v(TAG, packageName + ": notify(" + i + ", " + notification + ")");
        }
        try {
            service.enqueueNotificationWithTag(packageName, this.mContext.getOpPackageName(), str, i, notification, iArr, UserHandle.myUserId());
            if (i != iArr[0]) {
                Log.w(TAG, "notify: id corrupted: sent " + i + ", got back " + iArr[0]);
            }
        } catch (RemoteException unused) {
        }
    }

    public void notifyAsUser(String str, int i, Notification notification, UserHandle userHandle) {
        int[] iArr = new int[1];
        INotificationManager service = getService();
        String packageName = this.mContext.getPackageName();
        if (notification.sound != null) {
            notification.sound = notification.sound.getCanonicalUri();
            if (StrictMode.vmFileUriExposureEnabled()) {
                notification.sound.checkFileUriExposed("Notification.sound");
            }
        }
        if (localLOGV) {
            Log.v(TAG, packageName + ": notify(" + i + ", " + notification + ")");
        }
        try {
            service.enqueueNotificationWithTag(packageName, this.mContext.getOpPackageName(), str, i, notification, iArr, userHandle.getIdentifier());
            if (i != iArr[0]) {
                Log.w(TAG, "notify: id corrupted: sent " + i + ", got back " + iArr[0]);
            }
        } catch (RemoteException unused) {
        }
    }

    public void cancel(int i) {
        cancel(null, i);
    }

    public void cancel(String str, int i) {
        INotificationManager service = getService();
        String packageName = this.mContext.getPackageName();
        if (localLOGV) {
            Log.v(TAG, packageName + ": cancel(" + i + ")");
        }
        try {
            service.cancelNotificationWithTag(packageName, str, i, UserHandle.myUserId());
        } catch (RemoteException unused) {
        }
    }

    public void cancelAsUser(String str, int i, UserHandle userHandle) {
        INotificationManager service = getService();
        String packageName = this.mContext.getPackageName();
        if (localLOGV) {
            Log.v(TAG, packageName + ": cancel(" + i + ")");
        }
        try {
            service.cancelNotificationWithTag(packageName, str, i, userHandle.getIdentifier());
        } catch (RemoteException unused) {
        }
    }

    public void cancelAll() {
        INotificationManager service = getService();
        String packageName = this.mContext.getPackageName();
        if (localLOGV) {
            Log.v(TAG, packageName + ": cancelAll()");
        }
        try {
            service.cancelAllNotifications(packageName, UserHandle.myUserId());
        } catch (RemoteException unused) {
        }
    }
}
