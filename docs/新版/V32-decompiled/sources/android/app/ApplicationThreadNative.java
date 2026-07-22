package android.app;

import android.app.IInstrumentationWatcher;
import android.app.IUiAutomationConnection;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Debug;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public abstract class ApplicationThreadNative extends Binder implements IApplicationThread {
    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this;
    }

    public static IApplicationThread asInterface(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IApplicationThread iApplicationThread = (IApplicationThread) iBinder.queryLocalInterface(IApplicationThread.descriptor);
        return iApplicationThread != null ? iApplicationThread : new ApplicationThreadProxy(iBinder);
    }

    public ApplicationThreadNative() {
        attachInterface(this, IApplicationThread.descriptor);
    }

    @Override // android.os.Binder
    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        ParcelFileDescriptor fileDescriptor;
        switch (i) {
            case 1:
                parcel.enforceInterface(IApplicationThread.descriptor);
                schedulePauseActivity(parcel.readStrongBinder(), parcel.readInt() != 0, parcel.readInt() != 0, parcel.readInt());
                return true;
            case 2:
            case 32:
            default:
                return super.onTransact(i, parcel, parcel2, i2);
            case 3:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleStopActivity(parcel.readStrongBinder(), parcel.readInt() != 0, parcel.readInt());
                return true;
            case 4:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleWindowVisibility(parcel.readStrongBinder(), parcel.readInt() != 0);
                return true;
            case 5:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleResumeActivity(parcel.readStrongBinder(), parcel.readInt(), parcel.readInt() != 0);
                return true;
            case 6:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleSendResult(parcel.readStrongBinder(), parcel.createTypedArrayList(ResultInfo.CREATOR));
                return true;
            case 7:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleLaunchActivity(Intent.CREATOR.createFromParcel(parcel), parcel.readStrongBinder(), parcel.readInt(), ActivityInfo.CREATOR.createFromParcel(parcel), Configuration.CREATOR.createFromParcel(parcel), CompatibilityInfo.CREATOR.createFromParcel(parcel), parcel.readInt(), parcel.readBundle(), parcel.createTypedArrayList(ResultInfo.CREATOR), parcel.createTypedArrayList(Intent.CREATOR), parcel.readInt() != 0, parcel.readInt() != 0, parcel.readString(), parcel.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0);
                return true;
            case 8:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleNewIntent(parcel.createTypedArrayList(Intent.CREATOR), parcel.readStrongBinder());
                return true;
            case 9:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleDestroyActivity(parcel.readStrongBinder(), parcel.readInt() != 0, parcel.readInt());
                return true;
            case 10:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleReceiver(Intent.CREATOR.createFromParcel(parcel), ActivityInfo.CREATOR.createFromParcel(parcel), CompatibilityInfo.CREATOR.createFromParcel(parcel), parcel.readInt(), parcel.readString(), parcel.readBundle(), parcel.readInt() != 0, parcel.readInt(), parcel.readInt());
                return true;
            case 11:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleCreateService(parcel.readStrongBinder(), ServiceInfo.CREATOR.createFromParcel(parcel), CompatibilityInfo.CREATOR.createFromParcel(parcel), parcel.readInt());
                return true;
            case 12:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleStopService(parcel.readStrongBinder());
                return true;
            case 13:
                parcel.enforceInterface(IApplicationThread.descriptor);
                bindApplication(parcel.readString(), ApplicationInfo.CREATOR.createFromParcel(parcel), parcel.createTypedArrayList(ProviderInfo.CREATOR), parcel.readInt() != 0 ? new ComponentName(parcel) : null, parcel.readString(), parcel.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0, parcel.readBundle(), IInstrumentationWatcher.Stub.asInterface(parcel.readStrongBinder()), IUiAutomationConnection.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt(), parcel.readInt() != 0, parcel.readInt() != 0, parcel.readInt() != 0, Configuration.CREATOR.createFromParcel(parcel), CompatibilityInfo.CREATOR.createFromParcel(parcel), parcel.readHashMap(null), parcel.readBundle());
                return true;
            case 14:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleExit();
                return true;
            case 15:
                parcel.enforceInterface(IApplicationThread.descriptor);
                requestThumbnail(parcel.readStrongBinder());
                return true;
            case 16:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleConfigurationChanged(Configuration.CREATOR.createFromParcel(parcel));
                return true;
            case 17:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleServiceArgs(parcel.readStrongBinder(), parcel.readInt() != 0, parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? Intent.CREATOR.createFromParcel(parcel) : null);
                return true;
            case 18:
                parcel.enforceInterface(IApplicationThread.descriptor);
                updateTimeZone();
                return true;
            case 19:
                parcel.enforceInterface(IApplicationThread.descriptor);
                processInBackground();
                return true;
            case 20:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleBindService(parcel.readStrongBinder(), Intent.CREATOR.createFromParcel(parcel), parcel.readInt() != 0, parcel.readInt());
                return true;
            case 21:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleUnbindService(parcel.readStrongBinder(), Intent.CREATOR.createFromParcel(parcel));
                return true;
            case 22:
                parcel.enforceInterface(IApplicationThread.descriptor);
                ParcelFileDescriptor fileDescriptor2 = parcel.readFileDescriptor();
                IBinder strongBinder = parcel.readStrongBinder();
                String[] stringArray = parcel.readStringArray();
                if (fileDescriptor2 != null) {
                    dumpService(fileDescriptor2.getFileDescriptor(), strongBinder, stringArray);
                    try {
                        fileDescriptor2.close();
                        break;
                    } catch (IOException unused) {
                    }
                }
                return true;
            case 23:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleRegisteredReceiver(IIntentReceiver.Stub.asInterface(parcel.readStrongBinder()), Intent.CREATOR.createFromParcel(parcel), parcel.readInt(), parcel.readString(), parcel.readBundle(), parcel.readInt() != 0, parcel.readInt() != 0, parcel.readInt(), parcel.readInt());
                return true;
            case 24:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleLowMemory();
                return true;
            case 25:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleActivityConfigurationChanged(parcel.readStrongBinder());
                return true;
            case 26:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleRelaunchActivity(parcel.readStrongBinder(), parcel.createTypedArrayList(ResultInfo.CREATOR), parcel.createTypedArrayList(Intent.CREATOR), parcel.readInt(), parcel.readInt() != 0, parcel.readInt() != 0 ? Configuration.CREATOR.createFromParcel(parcel) : null);
                return true;
            case 27:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleSleeping(parcel.readStrongBinder(), parcel.readInt() != 0);
                return true;
            case 28:
                parcel.enforceInterface(IApplicationThread.descriptor);
                profilerControl(parcel.readInt() != 0, parcel.readString(), parcel.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                return true;
            case 29:
                parcel.enforceInterface(IApplicationThread.descriptor);
                setSchedulingGroup(parcel.readInt());
                return true;
            case 30:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleCreateBackupAgent(ApplicationInfo.CREATOR.createFromParcel(parcel), CompatibilityInfo.CREATOR.createFromParcel(parcel), parcel.readInt());
                return true;
            case 31:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleDestroyBackupAgent(ApplicationInfo.CREATOR.createFromParcel(parcel), CompatibilityInfo.CREATOR.createFromParcel(parcel));
                return true;
            case 33:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleSuicide();
                return true;
            case 34:
                parcel.enforceInterface(IApplicationThread.descriptor);
                dispatchPackageBroadcast(parcel.readInt(), parcel.readStringArray());
                return true;
            case 35:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleCrash(parcel.readString());
                return true;
            case 36:
                parcel.enforceInterface(IApplicationThread.descriptor);
                dumpHeap(parcel.readInt() != 0, parcel.readString(), parcel.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null);
                return true;
            case 37:
                parcel.enforceInterface(IApplicationThread.descriptor);
                ParcelFileDescriptor fileDescriptor3 = parcel.readFileDescriptor();
                IBinder strongBinder2 = parcel.readStrongBinder();
                String string = parcel.readString();
                String[] stringArray2 = parcel.readStringArray();
                if (fileDescriptor3 != null) {
                    dumpActivity(fileDescriptor3.getFileDescriptor(), strongBinder2, string, stringArray2);
                    try {
                        fileDescriptor3.close();
                        break;
                    } catch (IOException unused2) {
                    }
                }
                return true;
            case 38:
                parcel.enforceInterface(IApplicationThread.descriptor);
                clearDnsCache();
                return true;
            case 39:
                parcel.enforceInterface(IApplicationThread.descriptor);
                setHttpProxy(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString());
                return true;
            case 40:
                parcel.enforceInterface(IApplicationThread.descriptor);
                setCoreSettings(parcel.readBundle());
                return true;
            case 41:
                parcel.enforceInterface(IApplicationThread.descriptor);
                updatePackageCompatibilityInfo(parcel.readString(), CompatibilityInfo.CREATOR.createFromParcel(parcel));
                return true;
            case 42:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleTrimMemory(parcel.readInt());
                return true;
            case 43:
                parcel.enforceInterface(IApplicationThread.descriptor);
                fileDescriptor = parcel.readFileDescriptor();
                Debug.MemoryInfo memoryInfoCreateFromParcel = Debug.MemoryInfo.CREATOR.createFromParcel(parcel);
                boolean z = parcel.readInt() != 0;
                boolean z2 = parcel.readInt() != 0;
                boolean z3 = parcel.readInt() != 0;
                String[] stringArray3 = parcel.readStringArray();
                if (fileDescriptor != null) {
                    try {
                        dumpMemInfo(fileDescriptor.getFileDescriptor(), memoryInfoCreateFromParcel, z, z2, z3, stringArray3);
                        try {
                            fileDescriptor.close();
                            break;
                        } catch (IOException unused3) {
                        }
                    } finally {
                        try {
                            fileDescriptor.close();
                            break;
                        } catch (IOException unused4) {
                        }
                    }
                }
                parcel2.writeNoException();
                return true;
            case 44:
                parcel.enforceInterface(IApplicationThread.descriptor);
                ParcelFileDescriptor fileDescriptor4 = parcel.readFileDescriptor();
                String[] stringArray4 = parcel.readStringArray();
                if (fileDescriptor4 != null) {
                    try {
                        dumpGfxInfo(fileDescriptor4.getFileDescriptor(), stringArray4);
                        try {
                            fileDescriptor4.close();
                            break;
                        } catch (IOException unused5) {
                        }
                    } finally {
                        try {
                            fileDescriptor4.close();
                            break;
                        } catch (IOException unused6) {
                        }
                    }
                }
                parcel2.writeNoException();
                return true;
            case 45:
                parcel.enforceInterface(IApplicationThread.descriptor);
                ParcelFileDescriptor fileDescriptor5 = parcel.readFileDescriptor();
                IBinder strongBinder3 = parcel.readStrongBinder();
                String[] stringArray5 = parcel.readStringArray();
                if (fileDescriptor5 != null) {
                    dumpProvider(fileDescriptor5.getFileDescriptor(), strongBinder3, stringArray5);
                    try {
                        fileDescriptor5.close();
                        break;
                    } catch (IOException unused7) {
                    }
                }
                return true;
            case 46:
                parcel.enforceInterface(IApplicationThread.descriptor);
                fileDescriptor = parcel.readFileDescriptor();
                String[] stringArray6 = parcel.readStringArray();
                if (fileDescriptor != null) {
                    try {
                        dumpDbInfo(fileDescriptor.getFileDescriptor(), stringArray6);
                        try {
                            break;
                        } catch (IOException unused8) {
                        }
                    } finally {
                        try {
                            break;
                        } catch (IOException unused9) {
                        }
                    }
                }
                parcel2.writeNoException();
                return true;
            case 47:
                parcel.enforceInterface(IApplicationThread.descriptor);
                unstableProviderDied(parcel.readStrongBinder());
                parcel2.writeNoException();
                return true;
            case 48:
                parcel.enforceInterface(IApplicationThread.descriptor);
                requestAssistContextExtras(parcel.readStrongBinder(), parcel.readStrongBinder(), parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 49:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleTranslucentConversionComplete(parcel.readStrongBinder(), parcel.readInt() == 1);
                parcel2.writeNoException();
                return true;
            case 50:
                parcel.enforceInterface(IApplicationThread.descriptor);
                setProcessState(parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 51:
                parcel.enforceInterface(IApplicationThread.descriptor);
                scheduleInstallProvider(ProviderInfo.CREATOR.createFromParcel(parcel));
                parcel2.writeNoException();
                return true;
        }
    }
}
