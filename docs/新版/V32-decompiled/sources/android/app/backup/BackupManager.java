package android.app.backup;

import android.app.backup.IBackupManager;
import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class BackupManager {
    private static final String TAG = "BackupManager";
    private static IBackupManager sService;
    private Context mContext;

    private static void checkServiceBinder() {
        if (sService == null) {
            sService = IBackupManager.Stub.asInterface(ServiceManager.getService(Context.BACKUP_SERVICE));
        }
    }

    public BackupManager(Context context) {
        this.mContext = context;
    }

    public void dataChanged() {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager != null) {
            try {
                iBackupManager.dataChanged(this.mContext.getPackageName());
            } catch (RemoteException unused) {
                Log.d(TAG, "dataChanged() couldn't connect");
            }
        }
    }

    public static void dataChanged(String str) {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager != null) {
            try {
                iBackupManager.dataChanged(str);
            } catch (RemoteException unused) {
                Log.d(TAG, "dataChanged(pkg) couldn't connect");
            }
        }
    }

    public int requestRestore(RestoreObserver restoreObserver) throws Throwable {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        int iRestorePackage = -1;
        if (iBackupManager != null) {
            RestoreSession restoreSession = null;
            try {
                try {
                    IRestoreSession iRestoreSessionBeginRestoreSession = iBackupManager.beginRestoreSession(this.mContext.getPackageName(), null);
                    if (iRestoreSessionBeginRestoreSession != null) {
                        RestoreSession restoreSession2 = new RestoreSession(this.mContext, iRestoreSessionBeginRestoreSession);
                        try {
                            iRestorePackage = restoreSession2.restorePackage(this.mContext.getPackageName(), restoreObserver);
                            restoreSession = restoreSession2;
                        } catch (RemoteException unused) {
                            restoreSession = restoreSession2;
                            Log.w(TAG, "restoreSelf() unable to contact service");
                            if (restoreSession != null) {
                            }
                            return iRestorePackage;
                        } catch (Throwable th) {
                            th = th;
                            restoreSession = restoreSession2;
                            if (restoreSession != null) {
                                restoreSession.endRestoreSession();
                            }
                            throw th;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (RemoteException unused2) {
            }
            if (restoreSession != null) {
                restoreSession.endRestoreSession();
            }
        }
        return iRestorePackage;
    }

    public RestoreSession beginRestoreSession() {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager == null) {
            return null;
        }
        try {
            IRestoreSession iRestoreSessionBeginRestoreSession = iBackupManager.beginRestoreSession(null, null);
            if (iRestoreSessionBeginRestoreSession != null) {
                return new RestoreSession(this.mContext, iRestoreSessionBeginRestoreSession);
            }
            return null;
        } catch (RemoteException unused) {
            Log.w(TAG, "beginRestoreSession() couldn't connect");
            return null;
        }
    }
}
