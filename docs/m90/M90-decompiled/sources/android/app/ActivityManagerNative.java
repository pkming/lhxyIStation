package android.app;

import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.app.IActivityController;
import android.app.IActivityManager;
import android.app.IInstrumentationWatcher;
import android.app.IProcessObserver;
import android.app.IServiceConnection;
import android.app.IStopUserCallback;
import android.app.IThumbnailReceiver;
import android.app.IUiAutomationConnection;
import android.app.IUserSwitchObserver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.UriPermission;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Singleton;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class ActivityManagerNative extends Binder implements IActivityManager {
    private static final Singleton<IActivityManager> gDefault = new Singleton<IActivityManager>() { // from class: android.app.ActivityManagerNative.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.util.Singleton
        public IActivityManager create() {
            return ActivityManagerNative.asInterface(ServiceManager.getService(Context.ACTIVITY_SERVICE));
        }
    };
    static boolean sSystemReady = false;

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this;
    }

    public static IActivityManager asInterface(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IActivityManager iActivityManager = (IActivityManager) iBinder.queryLocalInterface(IActivityManager.descriptor);
        return iActivityManager != null ? iActivityManager : new ActivityManagerProxy(iBinder);
    }

    public static IActivityManager getDefault() {
        return gDefault.get();
    }

    public static boolean isSystemReady() {
        if (!sSystemReady) {
            sSystemReady = getDefault().testIsSystemReady();
        }
        return sSystemReady;
    }

    public static void broadcastStickyIntent(Intent intent, String str, int i) {
        try {
            getDefault().broadcastIntent(null, intent, null, null, -1, null, null, null, -1, false, true, i);
        } catch (RemoteException unused) {
        }
    }

    public static void noteWakeupAlarm(PendingIntent pendingIntent) {
        try {
            getDefault().noteWakeupAlarm(pendingIntent.getTarget());
        } catch (RemoteException unused) {
        }
    }

    public ActivityManagerNative() {
        attachInterface(this, IActivityManager.descriptor);
    }

    @Override // android.os.Binder
    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        int size;
        Intent[] intentArr;
        String[] strArrCreateStringArray;
        switch (i) {
            case 1:
                parcel.enforceInterface(IActivityManager.descriptor);
                startRunning(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString());
                parcel2.writeNoException();
                return true;
            case 2:
                parcel.enforceInterface(IActivityManager.descriptor);
                handleApplicationCrash(parcel.readStrongBinder(), new ApplicationErrorReport.CrashInfo(parcel));
                parcel2.writeNoException();
                return true;
            case 3:
                parcel.enforceInterface(IActivityManager.descriptor);
                int iStartActivity = startActivity(ApplicationThreadNative.asInterface(parcel.readStrongBinder()), parcel.readString(), Intent.CREATOR.createFromParcel(parcel), parcel.readString(), parcel.readStrongBinder(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readInt() != 0 ? parcel.readFileDescriptor() : null, parcel.readInt() != 0 ? Bundle.CREATOR.createFromParcel(parcel) : null);
                parcel2.writeNoException();
                parcel2.writeInt(iStartActivity);
                return true;
            case 4:
                parcel.enforceInterface(IActivityManager.descriptor);
                unhandledBack();
                parcel2.writeNoException();
                return true;
            case 5:
                parcel.enforceInterface(IActivityManager.descriptor);
                ParcelFileDescriptor parcelFileDescriptorOpenContentUri = openContentUri(Uri.parse(parcel.readString()));
                parcel2.writeNoException();
                if (parcelFileDescriptorOpenContentUri != null) {
                    parcel2.writeInt(1);
                    parcelFileDescriptorOpenContentUri.writeToParcel(parcel2, 1);
                } else {
                    parcel2.writeInt(0);
                }
                return true;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 160:
            default:
                return super.onTransact(i, parcel, parcel2, i2);
            case 11:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zFinishActivity = finishActivity(parcel.readStrongBinder(), parcel.readInt(), parcel.readInt() != 0 ? Intent.CREATOR.createFromParcel(parcel) : null);
                parcel2.writeNoException();
                parcel2.writeInt(zFinishActivity ? 1 : 0);
                return true;
            case 12:
                parcel.enforceInterface(IActivityManager.descriptor);
                IBinder strongBinder = parcel.readStrongBinder();
                IApplicationThread iApplicationThreadAsInterface = strongBinder != null ? ApplicationThreadNative.asInterface(strongBinder) : null;
                String string = parcel.readString();
                IBinder strongBinder2 = parcel.readStrongBinder();
                Intent intentRegisterReceiver = registerReceiver(iApplicationThreadAsInterface, string, strongBinder2 != null ? IIntentReceiver.Stub.asInterface(strongBinder2) : null, IntentFilter.CREATOR.createFromParcel(parcel), parcel.readString(), parcel.readInt());
                parcel2.writeNoException();
                if (intentRegisterReceiver != null) {
                    parcel2.writeInt(1);
                    intentRegisterReceiver.writeToParcel(parcel2, 0);
                } else {
                    parcel2.writeInt(0);
                }
                return true;
            case 13:
                parcel.enforceInterface(IActivityManager.descriptor);
                IBinder strongBinder3 = parcel.readStrongBinder();
                if (strongBinder3 == null) {
                    return true;
                }
                unregisterReceiver(IIntentReceiver.Stub.asInterface(strongBinder3));
                parcel2.writeNoException();
                return true;
            case 14:
                parcel.enforceInterface(IActivityManager.descriptor);
                IBinder strongBinder4 = parcel.readStrongBinder();
                IApplicationThread iApplicationThreadAsInterface2 = strongBinder4 != null ? ApplicationThreadNative.asInterface(strongBinder4) : null;
                Intent intentCreateFromParcel = Intent.CREATOR.createFromParcel(parcel);
                String string2 = parcel.readString();
                IBinder strongBinder5 = parcel.readStrongBinder();
                int iBroadcastIntent = broadcastIntent(iApplicationThreadAsInterface2, intentCreateFromParcel, string2, strongBinder5 != null ? IIntentReceiver.Stub.asInterface(strongBinder5) : null, parcel.readInt(), parcel.readString(), parcel.readBundle(), parcel.readString(), parcel.readInt(), parcel.readInt() != 0, parcel.readInt() != 0, parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(iBroadcastIntent);
                return true;
            case 15:
                parcel.enforceInterface(IActivityManager.descriptor);
                IBinder strongBinder6 = parcel.readStrongBinder();
                unbroadcastIntent(strongBinder6 != null ? ApplicationThreadNative.asInterface(strongBinder6) : null, Intent.CREATOR.createFromParcel(parcel), parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 16:
                parcel.enforceInterface(IActivityManager.descriptor);
                IBinder strongBinder7 = parcel.readStrongBinder();
                int i3 = parcel.readInt();
                String string3 = parcel.readString();
                Bundle bundle = parcel.readBundle();
                boolean z = parcel.readInt() != 0;
                if (strongBinder7 != null) {
                    finishReceiver(strongBinder7, i3, string3, bundle, z);
                }
                parcel2.writeNoException();
                return true;
            case 17:
                parcel.enforceInterface(IActivityManager.descriptor);
                IApplicationThread iApplicationThreadAsInterface3 = ApplicationThreadNative.asInterface(parcel.readStrongBinder());
                if (iApplicationThreadAsInterface3 != null) {
                    attachApplication(iApplicationThreadAsInterface3);
                }
                parcel2.writeNoException();
                return true;
            case 18:
                parcel.enforceInterface(IActivityManager.descriptor);
                IBinder strongBinder8 = parcel.readStrongBinder();
                Configuration configurationCreateFromParcel = parcel.readInt() != 0 ? Configuration.CREATOR.createFromParcel(parcel) : null;
                boolean z2 = parcel.readInt() != 0;
                if (strongBinder8 != null) {
                    activityIdle(strongBinder8, configurationCreateFromParcel, z2);
                }
                parcel2.writeNoException();
                return true;
            case 19:
                parcel.enforceInterface(IActivityManager.descriptor);
                activityPaused(parcel.readStrongBinder());
                parcel2.writeNoException();
                return true;
            case 20:
                parcel.enforceInterface(IActivityManager.descriptor);
                activityStopped(parcel.readStrongBinder(), parcel.readBundle(), parcel.readInt() != 0 ? Bitmap.CREATOR.createFromParcel(parcel) : null, TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel));
                parcel2.writeNoException();
                return true;
            case 21:
                parcel.enforceInterface(IActivityManager.descriptor);
                IBinder strongBinder9 = parcel.readStrongBinder();
                String callingPackage = strongBinder9 != null ? getCallingPackage(strongBinder9) : null;
                parcel2.writeNoException();
                parcel2.writeString(callingPackage);
                return true;
            case 22:
                parcel.enforceInterface(IActivityManager.descriptor);
                ComponentName callingActivity = getCallingActivity(parcel.readStrongBinder());
                parcel2.writeNoException();
                ComponentName.writeToParcel(callingActivity, parcel2);
                return true;
            case 23:
                parcel.enforceInterface(IActivityManager.descriptor);
                int i4 = parcel.readInt();
                int i5 = parcel.readInt();
                IBinder strongBinder10 = parcel.readStrongBinder();
                List<ActivityManager.RunningTaskInfo> tasks = getTasks(i4, i5, strongBinder10 != null ? IThumbnailReceiver.Stub.asInterface(strongBinder10) : null);
                parcel2.writeNoException();
                size = tasks != null ? tasks.size() : -1;
                parcel2.writeInt(size);
                for (int i6 = 0; i6 < size; i6++) {
                    tasks.get(i6).writeToParcel(parcel2, 0);
                }
                return true;
            case 24:
                parcel.enforceInterface(IActivityManager.descriptor);
                moveTaskToFront(parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? Bundle.CREATOR.createFromParcel(parcel) : null);
                parcel2.writeNoException();
                return true;
            case 25:
                parcel.enforceInterface(IActivityManager.descriptor);
                moveTaskToBack(parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 26:
                parcel.enforceInterface(IActivityManager.descriptor);
                moveTaskBackwards(parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 27:
                parcel.enforceInterface(IActivityManager.descriptor);
                IBinder strongBinder11 = parcel.readStrongBinder();
                size = strongBinder11 != null ? getTaskForActivity(strongBinder11, parcel.readInt() != 0) : -1;
                parcel2.writeNoException();
                parcel2.writeInt(size);
                return true;
            case 28:
                parcel.enforceInterface(IActivityManager.descriptor);
                reportThumbnail(parcel.readStrongBinder(), parcel.readInt() != 0 ? Bitmap.CREATOR.createFromParcel(parcel) : null, TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel));
                parcel2.writeNoException();
                return true;
            case 29:
                parcel.enforceInterface(IActivityManager.descriptor);
                IActivityManager.ContentProviderHolder contentProvider = getContentProvider(ApplicationThreadNative.asInterface(parcel.readStrongBinder()), parcel.readString(), parcel.readInt(), parcel.readInt() != 0);
                parcel2.writeNoException();
                if (contentProvider != null) {
                    parcel2.writeInt(1);
                    contentProvider.writeToParcel(parcel2, 0);
                } else {
                    parcel2.writeInt(0);
                }
                return true;
            case 30:
                parcel.enforceInterface(IActivityManager.descriptor);
                publishContentProviders(ApplicationThreadNative.asInterface(parcel.readStrongBinder()), parcel.createTypedArrayList(IActivityManager.ContentProviderHolder.CREATOR));
                parcel2.writeNoException();
                return true;
            case 31:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zRefContentProvider = refContentProvider(parcel.readStrongBinder(), parcel.readInt(), parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(zRefContentProvider ? 1 : 0);
                return true;
            case 32:
                parcel.enforceInterface(IActivityManager.descriptor);
                finishSubActivity(parcel.readStrongBinder(), parcel.readString(), parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 33:
                parcel.enforceInterface(IActivityManager.descriptor);
                PendingIntent runningServiceControlPanel = getRunningServiceControlPanel(ComponentName.CREATOR.createFromParcel(parcel));
                parcel2.writeNoException();
                PendingIntent.writePendingIntentOrNullToParcel(runningServiceControlPanel, parcel2);
                return true;
            case 34:
                parcel.enforceInterface(IActivityManager.descriptor);
                ComponentName componentNameStartService = startService(ApplicationThreadNative.asInterface(parcel.readStrongBinder()), Intent.CREATOR.createFromParcel(parcel), parcel.readString(), parcel.readInt());
                parcel2.writeNoException();
                ComponentName.writeToParcel(componentNameStartService, parcel2);
                return true;
            case 35:
                parcel.enforceInterface(IActivityManager.descriptor);
                int iStopService = stopService(ApplicationThreadNative.asInterface(parcel.readStrongBinder()), Intent.CREATOR.createFromParcel(parcel), parcel.readString(), parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(iStopService);
                return true;
            case 36:
                parcel.enforceInterface(IActivityManager.descriptor);
                int iBindService = bindService(ApplicationThreadNative.asInterface(parcel.readStrongBinder()), parcel.readStrongBinder(), Intent.CREATOR.createFromParcel(parcel), parcel.readString(), IServiceConnection.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt(), parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(iBindService);
                return true;
            case 37:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zUnbindService = unbindService(IServiceConnection.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
                parcel2.writeInt(zUnbindService ? 1 : 0);
                return true;
            case 38:
                parcel.enforceInterface(IActivityManager.descriptor);
                publishService(parcel.readStrongBinder(), Intent.CREATOR.createFromParcel(parcel), parcel.readStrongBinder());
                parcel2.writeNoException();
                return true;
            case 39:
                parcel.enforceInterface(IActivityManager.descriptor);
                activityResumed(parcel.readStrongBinder());
                parcel2.writeNoException();
                return true;
            case 40:
                parcel.enforceInterface(IActivityManager.descriptor);
                goingToSleep();
                parcel2.writeNoException();
                return true;
            case 41:
                parcel.enforceInterface(IActivityManager.descriptor);
                wakingUp();
                parcel2.writeNoException();
                return true;
            case 42:
                parcel.enforceInterface(IActivityManager.descriptor);
                setDebugApp(parcel.readString(), parcel.readInt() != 0, parcel.readInt() != 0);
                parcel2.writeNoException();
                return true;
            case 43:
                parcel.enforceInterface(IActivityManager.descriptor);
                setAlwaysFinish(parcel.readInt() != 0);
                parcel2.writeNoException();
                return true;
            case 44:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zStartInstrumentation = startInstrumentation(ComponentName.readFromParcel(parcel), parcel.readString(), parcel.readInt(), parcel.readBundle(), IInstrumentationWatcher.Stub.asInterface(parcel.readStrongBinder()), IUiAutomationConnection.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(zStartInstrumentation ? 1 : 0);
                return true;
            case 45:
                parcel.enforceInterface(IActivityManager.descriptor);
                finishInstrumentation(ApplicationThreadNative.asInterface(parcel.readStrongBinder()), parcel.readInt(), parcel.readBundle());
                parcel2.writeNoException();
                return true;
            case 46:
                parcel.enforceInterface(IActivityManager.descriptor);
                Configuration configuration = getConfiguration();
                parcel2.writeNoException();
                configuration.writeToParcel(parcel2, 0);
                return true;
            case 47:
                parcel.enforceInterface(IActivityManager.descriptor);
                updateConfiguration(Configuration.CREATOR.createFromParcel(parcel));
                parcel2.writeNoException();
                return true;
            case 48:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zStopServiceToken = stopServiceToken(ComponentName.readFromParcel(parcel), parcel.readStrongBinder(), parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(zStopServiceToken ? 1 : 0);
                return true;
            case 49:
                parcel.enforceInterface(IActivityManager.descriptor);
                ComponentName activityClassForToken = getActivityClassForToken(parcel.readStrongBinder());
                parcel2.writeNoException();
                ComponentName.writeToParcel(activityClassForToken, parcel2);
                return true;
            case 50:
                parcel.enforceInterface(IActivityManager.descriptor);
                IBinder strongBinder12 = parcel.readStrongBinder();
                parcel2.writeNoException();
                parcel2.writeString(getPackageForToken(strongBinder12));
                return true;
            case 51:
                parcel.enforceInterface(IActivityManager.descriptor);
                setProcessLimit(parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 52:
                parcel.enforceInterface(IActivityManager.descriptor);
                int processLimit = getProcessLimit();
                parcel2.writeNoException();
                parcel2.writeInt(processLimit);
                return true;
            case 53:
                parcel.enforceInterface(IActivityManager.descriptor);
                int iCheckPermission = checkPermission(parcel.readString(), parcel.readInt(), parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(iCheckPermission);
                return true;
            case 54:
                parcel.enforceInterface(IActivityManager.descriptor);
                int iCheckUriPermission = checkUriPermission(Uri.CREATOR.createFromParcel(parcel), parcel.readInt(), parcel.readInt(), parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(iCheckUriPermission);
                return true;
            case 55:
                parcel.enforceInterface(IActivityManager.descriptor);
                grantUriPermission(ApplicationThreadNative.asInterface(parcel.readStrongBinder()), parcel.readString(), Uri.CREATOR.createFromParcel(parcel), parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 56:
                parcel.enforceInterface(IActivityManager.descriptor);
                revokeUriPermission(ApplicationThreadNative.asInterface(parcel.readStrongBinder()), Uri.CREATOR.createFromParcel(parcel), parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 57:
                parcel.enforceInterface(IActivityManager.descriptor);
                setActivityController(IActivityController.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 58:
                parcel.enforceInterface(IActivityManager.descriptor);
                showWaitingForDebugger(ApplicationThreadNative.asInterface(parcel.readStrongBinder()), parcel.readInt() != 0);
                parcel2.writeNoException();
                return true;
            case 59:
                parcel.enforceInterface(IActivityManager.descriptor);
                signalPersistentProcesses(parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 60:
                parcel.enforceInterface(IActivityManager.descriptor);
                List<ActivityManager.RecentTaskInfo> recentTasks = getRecentTasks(parcel.readInt(), parcel.readInt(), parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeTypedList(recentTasks);
                return true;
            case 61:
                parcel.enforceInterface(IActivityManager.descriptor);
                serviceDoneExecuting(parcel.readStrongBinder(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 62:
                parcel.enforceInterface(IActivityManager.descriptor);
                activityDestroyed(parcel.readStrongBinder());
                parcel2.writeNoException();
                return true;
            case 63:
                parcel.enforceInterface(IActivityManager.descriptor);
                int i7 = parcel.readInt();
                String string4 = parcel.readString();
                IBinder strongBinder13 = parcel.readStrongBinder();
                String string5 = parcel.readString();
                int i8 = parcel.readInt();
                if (parcel.readInt() != 0) {
                    intentArr = (Intent[]) parcel.createTypedArray(Intent.CREATOR);
                    strArrCreateStringArray = parcel.createStringArray();
                } else {
                    intentArr = null;
                    strArrCreateStringArray = null;
                }
                IIntentSender intentSender = getIntentSender(i7, string4, strongBinder13, string5, i8, intentArr, strArrCreateStringArray, parcel.readInt(), parcel.readInt() != 0 ? Bundle.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeStrongBinder(intentSender != null ? intentSender.asBinder() : null);
                return true;
            case 64:
                parcel.enforceInterface(IActivityManager.descriptor);
                cancelIntentSender(IIntentSender.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 65:
                parcel.enforceInterface(IActivityManager.descriptor);
                String packageForIntentSender = getPackageForIntentSender(IIntentSender.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
                parcel2.writeString(packageForIntentSender);
                return true;
            case 66:
                parcel.enforceInterface(IActivityManager.descriptor);
                enterSafeMode();
                parcel2.writeNoException();
                return true;
            case 67:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zStartNextMatchingActivity = startNextMatchingActivity(parcel.readStrongBinder(), Intent.CREATOR.createFromParcel(parcel), parcel.readInt() != 0 ? Bundle.CREATOR.createFromParcel(parcel) : null);
                parcel2.writeNoException();
                parcel2.writeInt(zStartNextMatchingActivity ? 1 : 0);
                return true;
            case 68:
                parcel.enforceInterface(IActivityManager.descriptor);
                noteWakeupAlarm(IIntentSender.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 69:
                parcel.enforceInterface(IActivityManager.descriptor);
                removeContentProvider(parcel.readStrongBinder(), parcel.readInt() != 0);
                parcel2.writeNoException();
                return true;
            case 70:
                parcel.enforceInterface(IActivityManager.descriptor);
                setRequestedOrientation(parcel.readStrongBinder(), parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 71:
                parcel.enforceInterface(IActivityManager.descriptor);
                int requestedOrientation = getRequestedOrientation(parcel.readStrongBinder());
                parcel2.writeNoException();
                parcel2.writeInt(requestedOrientation);
                return true;
            case 72:
                parcel.enforceInterface(IActivityManager.descriptor);
                unbindFinished(parcel.readStrongBinder(), Intent.CREATOR.createFromParcel(parcel), parcel.readInt() != 0);
                parcel2.writeNoException();
                return true;
            case 73:
                parcel.enforceInterface(IActivityManager.descriptor);
                setProcessForeground(parcel.readStrongBinder(), parcel.readInt(), parcel.readInt() != 0);
                parcel2.writeNoException();
                return true;
            case 74:
                parcel.enforceInterface(IActivityManager.descriptor);
                setServiceForeground(ComponentName.readFromParcel(parcel), parcel.readStrongBinder(), parcel.readInt(), parcel.readInt() != 0 ? Notification.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0);
                parcel2.writeNoException();
                return true;
            case 75:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zMoveActivityTaskToBack = moveActivityTaskToBack(parcel.readStrongBinder(), parcel.readInt() != 0);
                parcel2.writeNoException();
                parcel2.writeInt(zMoveActivityTaskToBack ? 1 : 0);
                return true;
            case 76:
                parcel.enforceInterface(IActivityManager.descriptor);
                ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                getMemoryInfo(memoryInfo);
                parcel2.writeNoException();
                memoryInfo.writeToParcel(parcel2, 0);
                return true;
            case 77:
                parcel.enforceInterface(IActivityManager.descriptor);
                List<ActivityManager.ProcessErrorStateInfo> processesInErrorState = getProcessesInErrorState();
                parcel2.writeNoException();
                parcel2.writeTypedList(processesInErrorState);
                return true;
            case 78:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zClearApplicationUserData = clearApplicationUserData(parcel.readString(), IPackageDataObserver.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(zClearApplicationUserData ? 1 : 0);
                return true;
            case 79:
                parcel.enforceInterface(IActivityManager.descriptor);
                forceStopPackage(parcel.readString(), parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 80:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zKillPids = killPids(parcel.createIntArray(), parcel.readString(), parcel.readInt() != 0);
                parcel2.writeNoException();
                parcel2.writeInt(zKillPids ? 1 : 0);
                return true;
            case 81:
                parcel.enforceInterface(IActivityManager.descriptor);
                List<ActivityManager.RunningServiceInfo> services = getServices(parcel.readInt(), parcel.readInt());
                parcel2.writeNoException();
                size = services != null ? services.size() : -1;
                parcel2.writeInt(size);
                for (int i9 = 0; i9 < size; i9++) {
                    services.get(i9).writeToParcel(parcel2, 0);
                }
                return true;
            case 82:
                parcel.enforceInterface(IActivityManager.descriptor);
                ActivityManager.TaskThumbnails taskThumbnails = getTaskThumbnails(parcel.readInt());
                parcel2.writeNoException();
                if (taskThumbnails != null) {
                    parcel2.writeInt(1);
                    taskThumbnails.writeToParcel(parcel2, 0);
                } else {
                    parcel2.writeInt(0);
                }
                return true;
            case 83:
                parcel.enforceInterface(IActivityManager.descriptor);
                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = getRunningAppProcesses();
                parcel2.writeNoException();
                parcel2.writeTypedList(runningAppProcesses);
                return true;
            case 84:
                parcel.enforceInterface(IActivityManager.descriptor);
                ConfigurationInfo deviceConfigurationInfo = getDeviceConfigurationInfo();
                parcel2.writeNoException();
                deviceConfigurationInfo.writeToParcel(parcel2, 0);
                return true;
            case 85:
                parcel.enforceInterface(IActivityManager.descriptor);
                IBinder iBinderPeekService = peekService(Intent.CREATOR.createFromParcel(parcel), parcel.readString());
                parcel2.writeNoException();
                parcel2.writeStrongBinder(iBinderPeekService);
                return true;
            case 86:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zProfileControl = profileControl(parcel.readString(), parcel.readInt(), parcel.readInt() != 0, parcel.readString(), parcel.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(zProfileControl ? 1 : 0);
                return true;
            case 87:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zShutdown = shutdown(parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(zShutdown ? 1 : 0);
                return true;
            case 88:
                parcel.enforceInterface(IActivityManager.descriptor);
                stopAppSwitches();
                parcel2.writeNoException();
                return true;
            case 89:
                parcel.enforceInterface(IActivityManager.descriptor);
                resumeAppSwitches();
                parcel2.writeNoException();
                return true;
            case 90:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zBindBackupAgent = bindBackupAgent(ApplicationInfo.CREATOR.createFromParcel(parcel), parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(zBindBackupAgent ? 1 : 0);
                return true;
            case 91:
                parcel.enforceInterface(IActivityManager.descriptor);
                backupAgentCreated(parcel.readString(), parcel.readStrongBinder());
                parcel2.writeNoException();
                return true;
            case 92:
                parcel.enforceInterface(IActivityManager.descriptor);
                unbindBackupAgent(ApplicationInfo.CREATOR.createFromParcel(parcel));
                parcel2.writeNoException();
                return true;
            case 93:
                parcel.enforceInterface(IActivityManager.descriptor);
                int uidForIntentSender = getUidForIntentSender(IIntentSender.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
                parcel2.writeInt(uidForIntentSender);
                return true;
            case 94:
                parcel.enforceInterface(IActivityManager.descriptor);
                int iHandleIncomingUser = handleIncomingUser(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0, parcel.readInt() != 0, parcel.readString(), parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(iHandleIncomingUser);
                return true;
            case 95:
                parcel.enforceInterface(IActivityManager.descriptor);
                Bitmap taskTopThumbnail = getTaskTopThumbnail(parcel.readInt());
                parcel2.writeNoException();
                if (taskTopThumbnail != null) {
                    parcel2.writeInt(1);
                    taskTopThumbnail.writeToParcel(parcel2, 0);
                } else {
                    parcel2.writeInt(0);
                }
                return true;
            case 96:
                parcel.enforceInterface(IActivityManager.descriptor);
                killApplicationWithAppId(parcel.readString(), parcel.readInt(), parcel.readString());
                parcel2.writeNoException();
                return true;
            case 97:
                parcel.enforceInterface(IActivityManager.descriptor);
                closeSystemDialogs(parcel.readString());
                parcel2.writeNoException();
                return true;
            case 98:
                parcel.enforceInterface(IActivityManager.descriptor);
                Debug.MemoryInfo[] processMemoryInfo = getProcessMemoryInfo(parcel.createIntArray());
                parcel2.writeNoException();
                parcel2.writeTypedArray(processMemoryInfo, 1);
                return true;
            case 99:
                parcel.enforceInterface(IActivityManager.descriptor);
                killApplicationProcess(parcel.readString(), parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 100:
                parcel.enforceInterface(IActivityManager.descriptor);
                int iStartActivityIntentSender = startActivityIntentSender(ApplicationThreadNative.asInterface(parcel.readStrongBinder()), IntentSender.CREATOR.createFromParcel(parcel), parcel.readInt() != 0 ? Intent.CREATOR.createFromParcel(parcel) : null, parcel.readString(), parcel.readStrongBinder(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? Bundle.CREATOR.createFromParcel(parcel) : null);
                parcel2.writeNoException();
                parcel2.writeInt(iStartActivityIntentSender);
                return true;
            case 101:
                parcel.enforceInterface(IActivityManager.descriptor);
                overridePendingTransition(parcel.readStrongBinder(), parcel.readString(), parcel.readInt(), parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 102:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zHandleApplicationWtf = handleApplicationWtf(parcel.readStrongBinder(), parcel.readString(), new ApplicationErrorReport.CrashInfo(parcel));
                parcel2.writeNoException();
                parcel2.writeInt(zHandleApplicationWtf ? 1 : 0);
                return true;
            case 103:
                parcel.enforceInterface(IActivityManager.descriptor);
                killBackgroundProcesses(parcel.readString(), parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 104:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zIsUserAMonkey = isUserAMonkey();
                parcel2.writeNoException();
                parcel2.writeInt(zIsUserAMonkey ? 1 : 0);
                return true;
            case 105:
                parcel.enforceInterface(IActivityManager.descriptor);
                IActivityManager.WaitResult waitResultStartActivityAndWait = startActivityAndWait(ApplicationThreadNative.asInterface(parcel.readStrongBinder()), parcel.readString(), Intent.CREATOR.createFromParcel(parcel), parcel.readString(), parcel.readStrongBinder(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0 ? Bundle.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                parcel2.writeNoException();
                waitResultStartActivityAndWait.writeToParcel(parcel2, 0);
                return true;
            case 106:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zWillActivityBeVisible = willActivityBeVisible(parcel.readStrongBinder());
                parcel2.writeNoException();
                parcel2.writeInt(zWillActivityBeVisible ? 1 : 0);
                return true;
            case 107:
                parcel.enforceInterface(IActivityManager.descriptor);
                int iStartActivityWithConfig = startActivityWithConfig(ApplicationThreadNative.asInterface(parcel.readStrongBinder()), parcel.readString(), Intent.CREATOR.createFromParcel(parcel), parcel.readString(), parcel.readStrongBinder(), parcel.readString(), parcel.readInt(), parcel.readInt(), Configuration.CREATOR.createFromParcel(parcel), parcel.readInt() != 0 ? Bundle.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(iStartActivityWithConfig);
                return true;
            case 108:
                parcel.enforceInterface(IActivityManager.descriptor);
                List<ApplicationInfo> runningExternalApplications = getRunningExternalApplications();
                parcel2.writeNoException();
                parcel2.writeTypedList(runningExternalApplications);
                return true;
            case 109:
                parcel.enforceInterface(IActivityManager.descriptor);
                finishHeavyWeightApp();
                parcel2.writeNoException();
                return true;
            case 110:
                parcel.enforceInterface(IActivityManager.descriptor);
                handleApplicationStrictModeViolation(parcel.readStrongBinder(), parcel.readInt(), new StrictMode.ViolationInfo(parcel));
                parcel2.writeNoException();
                return true;
            case 111:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zIsImmersive = isImmersive(parcel.readStrongBinder());
                parcel2.writeNoException();
                parcel2.writeInt(zIsImmersive ? 1 : 0);
                return true;
            case 112:
                parcel.enforceInterface(IActivityManager.descriptor);
                setImmersive(parcel.readStrongBinder(), parcel.readInt() == 1);
                parcel2.writeNoException();
                return true;
            case 113:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zIsTopActivityImmersive = isTopActivityImmersive();
                parcel2.writeNoException();
                parcel2.writeInt(zIsTopActivityImmersive ? 1 : 0);
                return true;
            case 114:
                parcel.enforceInterface(IActivityManager.descriptor);
                crashApplication(parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readString());
                parcel2.writeNoException();
                return true;
            case 115:
                parcel.enforceInterface(IActivityManager.descriptor);
                String providerMimeType = getProviderMimeType(Uri.CREATOR.createFromParcel(parcel), parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeString(providerMimeType);
                return true;
            case 116:
                parcel.enforceInterface(IActivityManager.descriptor);
                IBinder iBinderNewUriPermissionOwner = newUriPermissionOwner(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeStrongBinder(iBinderNewUriPermissionOwner);
                return true;
            case 117:
                parcel.enforceInterface(IActivityManager.descriptor);
                grantUriPermissionFromOwner(parcel.readStrongBinder(), parcel.readInt(), parcel.readString(), Uri.CREATOR.createFromParcel(parcel), parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 118:
                parcel.enforceInterface(IActivityManager.descriptor);
                IBinder strongBinder14 = parcel.readStrongBinder();
                if (parcel.readInt() != 0) {
                    Uri.CREATOR.createFromParcel(parcel);
                }
                revokeUriPermissionFromOwner(strongBinder14, null, parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 119:
                parcel.enforceInterface(IActivityManager.descriptor);
                int iCheckGrantUriPermission = checkGrantUriPermission(parcel.readInt(), parcel.readString(), Uri.CREATOR.createFromParcel(parcel), parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(iCheckGrantUriPermission);
                return true;
            case 120:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zDumpHeap = dumpHeap(parcel.readString(), parcel.readInt(), parcel.readInt() != 0, parcel.readString(), parcel.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null);
                parcel2.writeNoException();
                parcel2.writeInt(zDumpHeap ? 1 : 0);
                return true;
            case 121:
                parcel.enforceInterface(IActivityManager.descriptor);
                int iStartActivities = startActivities(ApplicationThreadNative.asInterface(parcel.readStrongBinder()), parcel.readString(), (Intent[]) parcel.createTypedArray(Intent.CREATOR), parcel.createStringArray(), parcel.readStrongBinder(), parcel.readInt() != 0 ? Bundle.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(iStartActivities);
                return true;
            case 122:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zIsUserRunning = isUserRunning(parcel.readInt(), parcel.readInt() != 0);
                parcel2.writeNoException();
                parcel2.writeInt(zIsUserRunning ? 1 : 0);
                return true;
            case 123:
                parcel.enforceInterface(IActivityManager.descriptor);
                activitySlept(parcel.readStrongBinder());
                parcel2.writeNoException();
                return true;
            case 124:
                parcel.enforceInterface(IActivityManager.descriptor);
                int frontActivityScreenCompatMode = getFrontActivityScreenCompatMode();
                parcel2.writeNoException();
                parcel2.writeInt(frontActivityScreenCompatMode);
                return true;
            case 125:
                parcel.enforceInterface(IActivityManager.descriptor);
                int i10 = parcel.readInt();
                setFrontActivityScreenCompatMode(i10);
                parcel2.writeNoException();
                parcel2.writeInt(i10);
                return true;
            case 126:
                parcel.enforceInterface(IActivityManager.descriptor);
                int packageScreenCompatMode = getPackageScreenCompatMode(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(packageScreenCompatMode);
                return true;
            case 127:
                parcel.enforceInterface(IActivityManager.descriptor);
                setPackageScreenCompatMode(parcel.readString(), parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 128:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean packageAskScreenCompat = getPackageAskScreenCompat(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(packageAskScreenCompat ? 1 : 0);
                return true;
            case 129:
                parcel.enforceInterface(IActivityManager.descriptor);
                setPackageAskScreenCompat(parcel.readString(), parcel.readInt() != 0);
                parcel2.writeNoException();
                return true;
            case 130:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zSwitchUser = switchUser(parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(zSwitchUser ? 1 : 0);
                return true;
            case 131:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zRemoveSubTask = removeSubTask(parcel.readInt(), parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(zRemoveSubTask ? 1 : 0);
                return true;
            case 132:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zRemoveTask = removeTask(parcel.readInt(), parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(zRemoveTask ? 1 : 0);
                return true;
            case 133:
                parcel.enforceInterface(IActivityManager.descriptor);
                registerProcessObserver(IProcessObserver.Stub.asInterface(parcel.readStrongBinder()));
                return true;
            case 134:
                parcel.enforceInterface(IActivityManager.descriptor);
                unregisterProcessObserver(IProcessObserver.Stub.asInterface(parcel.readStrongBinder()));
                return true;
            case 135:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zIsIntentSenderTargetedToPackage = isIntentSenderTargetedToPackage(IIntentSender.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
                parcel2.writeInt(zIsIntentSenderTargetedToPackage ? 1 : 0);
                return true;
            case 136:
                parcel.enforceInterface(IActivityManager.descriptor);
                updatePersistentConfiguration(Configuration.CREATOR.createFromParcel(parcel));
                parcel2.writeNoException();
                return true;
            case 137:
                parcel.enforceInterface(IActivityManager.descriptor);
                long[] processPss = getProcessPss(parcel.createIntArray());
                parcel2.writeNoException();
                parcel2.writeLongArray(processPss);
                return true;
            case 138:
                parcel.enforceInterface(IActivityManager.descriptor);
                showBootMessage(TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel), parcel.readInt() != 0);
                parcel2.writeNoException();
                return true;
            case 139:
                parcel.enforceInterface(IActivityManager.descriptor);
                dismissKeyguardOnNextActivity();
                parcel2.writeNoException();
                return true;
            case 140:
                parcel.enforceInterface(IActivityManager.descriptor);
                killAllBackgroundProcesses();
                parcel2.writeNoException();
                return true;
            case 141:
                parcel.enforceInterface(IActivityManager.descriptor);
                IActivityManager.ContentProviderHolder contentProviderExternal = getContentProviderExternal(parcel.readString(), parcel.readInt(), parcel.readStrongBinder());
                parcel2.writeNoException();
                if (contentProviderExternal != null) {
                    parcel2.writeInt(1);
                    contentProviderExternal.writeToParcel(parcel2, 0);
                } else {
                    parcel2.writeInt(0);
                }
                return true;
            case 142:
                parcel.enforceInterface(IActivityManager.descriptor);
                removeContentProviderExternal(parcel.readString(), parcel.readStrongBinder());
                parcel2.writeNoException();
                return true;
            case 143:
                parcel.enforceInterface(IActivityManager.descriptor);
                ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
                getMyMemoryState(runningAppProcessInfo);
                parcel2.writeNoException();
                runningAppProcessInfo.writeToParcel(parcel2, 0);
                return true;
            case 144:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zKillProcessesBelowForeground = killProcessesBelowForeground(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(zKillProcessesBelowForeground ? 1 : 0);
                return true;
            case 145:
                parcel.enforceInterface(IActivityManager.descriptor);
                UserInfo currentUser = getCurrentUser();
                parcel2.writeNoException();
                currentUser.writeToParcel(parcel2, 0);
                return true;
            case 146:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zTargetTaskAffinityMatchesActivity = targetTaskAffinityMatchesActivity(parcel.readStrongBinder(), parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(zTargetTaskAffinityMatchesActivity ? 1 : 0);
                return true;
            case 147:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zNavigateUpTo = navigateUpTo(parcel.readStrongBinder(), Intent.CREATOR.createFromParcel(parcel), parcel.readInt(), parcel.readInt() != 0 ? Intent.CREATOR.createFromParcel(parcel) : null);
                parcel2.writeNoException();
                parcel2.writeInt(zNavigateUpTo ? 1 : 0);
                return true;
            case 148:
                parcel.enforceInterface(IActivityManager.descriptor);
                setLockScreenShown(parcel.readInt() != 0);
                parcel2.writeNoException();
                return true;
            case 149:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zFinishActivityAffinity = finishActivityAffinity(parcel.readStrongBinder());
                parcel2.writeNoException();
                parcel2.writeInt(zFinishActivityAffinity ? 1 : 0);
                return true;
            case 150:
                parcel.enforceInterface(IActivityManager.descriptor);
                int launchedFromUid = getLaunchedFromUid(parcel.readStrongBinder());
                parcel2.writeNoException();
                parcel2.writeInt(launchedFromUid);
                return true;
            case 151:
                parcel.enforceInterface(IActivityManager.descriptor);
                unstableProviderDied(parcel.readStrongBinder());
                parcel2.writeNoException();
                return true;
            case 152:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zIsIntentSenderAnActivity = isIntentSenderAnActivity(IIntentSender.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
                parcel2.writeInt(zIsIntentSenderAnActivity ? 1 : 0);
                return true;
            case 153:
                parcel.enforceInterface(IActivityManager.descriptor);
                int iStartActivityAsUser = startActivityAsUser(ApplicationThreadNative.asInterface(parcel.readStrongBinder()), parcel.readString(), Intent.CREATOR.createFromParcel(parcel), parcel.readString(), parcel.readStrongBinder(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0 ? Bundle.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(iStartActivityAsUser);
                return true;
            case 154:
                parcel.enforceInterface(IActivityManager.descriptor);
                int iStopUser = stopUser(parcel.readInt(), IStopUserCallback.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
                parcel2.writeInt(iStopUser);
                return true;
            case 155:
                parcel.enforceInterface(IActivityManager.descriptor);
                registerUserSwitchObserver(IUserSwitchObserver.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 156:
                parcel.enforceInterface(IActivityManager.descriptor);
                unregisterUserSwitchObserver(IUserSwitchObserver.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 157:
                parcel.enforceInterface(IActivityManager.descriptor);
                int[] runningUserIds = getRunningUserIds();
                parcel2.writeNoException();
                parcel2.writeIntArray(runningUserIds);
                return true;
            case 158:
                parcel.enforceInterface(IActivityManager.descriptor);
                requestBugReport();
                parcel2.writeNoException();
                return true;
            case 159:
                parcel.enforceInterface(IActivityManager.descriptor);
                long jInputDispatchingTimedOut = inputDispatchingTimedOut(parcel.readInt(), parcel.readInt() != 0, parcel.readString());
                parcel2.writeNoException();
                parcel2.writeLong(jInputDispatchingTimedOut);
                return true;
            case 161:
                parcel.enforceInterface(IActivityManager.descriptor);
                Intent intentForIntentSender = getIntentForIntentSender(IIntentSender.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
                if (intentForIntentSender != null) {
                    parcel2.writeInt(1);
                    intentForIntentSender.writeToParcel(parcel2, 1);
                } else {
                    parcel2.writeInt(0);
                }
                return true;
            case 162:
                parcel.enforceInterface(IActivityManager.descriptor);
                Bundle assistContextExtras = getAssistContextExtras(parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeBundle(assistContextExtras);
                return true;
            case 163:
                parcel.enforceInterface(IActivityManager.descriptor);
                reportAssistContextExtras(parcel.readStrongBinder(), parcel.readBundle());
                parcel2.writeNoException();
                return true;
            case 164:
                parcel.enforceInterface(IActivityManager.descriptor);
                String launchedFromPackage = getLaunchedFromPackage(parcel.readStrongBinder());
                parcel2.writeNoException();
                parcel2.writeString(launchedFromPackage);
                return true;
            case 165:
                parcel.enforceInterface(IActivityManager.descriptor);
                killUid(parcel.readInt(), parcel.readString());
                parcel2.writeNoException();
                return true;
            case 166:
                parcel.enforceInterface(IActivityManager.descriptor);
                setUserIsMonkey(parcel.readInt() == 1);
                parcel2.writeNoException();
                return true;
            case 167:
                parcel.enforceInterface(IActivityManager.descriptor);
                hang(parcel.readStrongBinder(), parcel.readInt() != 0);
                parcel2.writeNoException();
                return true;
            case 168:
                parcel.enforceInterface(IActivityManager.descriptor);
                int iCreateStack = createStack(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readFloat());
                parcel2.writeNoException();
                parcel2.writeInt(iCreateStack);
                return true;
            case 169:
                parcel.enforceInterface(IActivityManager.descriptor);
                moveTaskToStack(parcel.readInt(), parcel.readInt(), parcel.readInt() != 0);
                parcel2.writeNoException();
                return true;
            case 170:
                parcel.enforceInterface(IActivityManager.descriptor);
                resizeStackBox(parcel.readInt(), parcel.readFloat());
                parcel2.writeNoException();
                return true;
            case 171:
                parcel.enforceInterface(IActivityManager.descriptor);
                List<ActivityManager.StackBoxInfo> stackBoxes = getStackBoxes();
                parcel2.writeNoException();
                parcel2.writeTypedList(stackBoxes);
                return true;
            case 172:
                parcel.enforceInterface(IActivityManager.descriptor);
                setFocusedStack(parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 173:
                parcel.enforceInterface(IActivityManager.descriptor);
                ActivityManager.StackBoxInfo stackBoxInfo = getStackBoxInfo(parcel.readInt());
                parcel2.writeNoException();
                if (stackBoxInfo != null) {
                    parcel2.writeInt(1);
                    stackBoxInfo.writeToParcel(parcel2, 0);
                } else {
                    parcel2.writeInt(0);
                }
                return true;
            case 174:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zConvertFromTranslucent = convertFromTranslucent(parcel.readStrongBinder());
                parcel2.writeNoException();
                parcel2.writeInt(zConvertFromTranslucent ? 1 : 0);
                return true;
            case 175:
                parcel.enforceInterface(IActivityManager.descriptor);
                boolean zConvertToTranslucent = convertToTranslucent(parcel.readStrongBinder());
                parcel2.writeNoException();
                parcel2.writeInt(zConvertToTranslucent ? 1 : 0);
                return true;
            case 176:
                parcel.enforceInterface(IActivityManager.descriptor);
                notifyActivityDrawn(parcel.readStrongBinder());
                parcel2.writeNoException();
                return true;
            case 177:
                parcel.enforceInterface(IActivityManager.descriptor);
                reportActivityFullyDrawn(parcel.readStrongBinder());
                parcel2.writeNoException();
                return true;
            case 178:
                parcel.enforceInterface(IActivityManager.descriptor);
                restart();
                parcel2.writeNoException();
                return true;
            case 179:
                parcel.enforceInterface(IActivityManager.descriptor);
                performIdleMaintenance();
                parcel2.writeNoException();
                return true;
            case 180:
                parcel.enforceInterface(IActivityManager.descriptor);
                takePersistableUriPermission(Uri.CREATOR.createFromParcel(parcel), parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 181:
                parcel.enforceInterface(IActivityManager.descriptor);
                releasePersistableUriPermission(Uri.CREATOR.createFromParcel(parcel), parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 182:
                parcel.enforceInterface(IActivityManager.descriptor);
                ParceledListSlice<UriPermission> persistedUriPermissions = getPersistedUriPermissions(parcel.readString(), parcel.readInt() != 0);
                parcel2.writeNoException();
                persistedUriPermissions.writeToParcel(parcel2, 1);
                return true;
            case 183:
                parcel.enforceInterface(IActivityManager.descriptor);
                appNotRespondingViaProvider(parcel.readStrongBinder());
                parcel2.writeNoException();
                return true;
        }
    }
}
