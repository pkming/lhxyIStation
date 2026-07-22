package android.app;

import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.app.IActivityManager;
import android.content.ComponentName;
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
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.StrictMode;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: compiled from: ActivityManagerNative.java */
/* JADX INFO: loaded from: classes.dex */
class ActivityManagerProxy implements IActivityManager {
    private IBinder mRemote;

    @Override // android.app.IActivityManager
    public boolean testIsSystemReady() {
        return true;
    }

    public ActivityManagerProxy(IBinder iBinder) {
        this.mRemote = iBinder;
    }

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this.mRemote;
    }

    @Override // android.app.IActivityManager
    public int startActivity(IApplicationThread iApplicationThread, String str, Intent intent, String str2, IBinder iBinder, String str3, int i, int i2, String str4, ParcelFileDescriptor parcelFileDescriptor, Bundle bundle) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread != null ? iApplicationThread.asBinder() : null);
        parcelObtain.writeString(str);
        intent.writeToParcel(parcelObtain, 0);
        parcelObtain.writeString(str2);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeString(str3);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        parcelObtain.writeString(str4);
        if (parcelFileDescriptor != null) {
            parcelObtain.writeInt(1);
            parcelFileDescriptor.writeToParcel(parcelObtain, 1);
        } else {
            parcelObtain.writeInt(0);
        }
        if (bundle != null) {
            parcelObtain.writeInt(1);
            bundle.writeToParcel(parcelObtain, 0);
        } else {
            parcelObtain.writeInt(0);
        }
        this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i3 = parcelObtain2.readInt();
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return i3;
    }

    @Override // android.app.IActivityManager
    public int startActivityAsUser(IApplicationThread iApplicationThread, String str, Intent intent, String str2, IBinder iBinder, String str3, int i, int i2, String str4, ParcelFileDescriptor parcelFileDescriptor, Bundle bundle, int i3) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread != null ? iApplicationThread.asBinder() : null);
        parcelObtain.writeString(str);
        intent.writeToParcel(parcelObtain, 0);
        parcelObtain.writeString(str2);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeString(str3);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        parcelObtain.writeString(str4);
        if (parcelFileDescriptor != null) {
            parcelObtain.writeInt(1);
            parcelFileDescriptor.writeToParcel(parcelObtain, 1);
        } else {
            parcelObtain.writeInt(0);
        }
        if (bundle != null) {
            parcelObtain.writeInt(1);
            bundle.writeToParcel(parcelObtain, 0);
        } else {
            parcelObtain.writeInt(0);
        }
        parcelObtain.writeInt(i3);
        this.mRemote.transact(153, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i4 = parcelObtain2.readInt();
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return i4;
    }

    @Override // android.app.IActivityManager
    public IActivityManager.WaitResult startActivityAndWait(IApplicationThread iApplicationThread, String str, Intent intent, String str2, IBinder iBinder, String str3, int i, int i2, String str4, ParcelFileDescriptor parcelFileDescriptor, Bundle bundle, int i3) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread != null ? iApplicationThread.asBinder() : null);
        parcelObtain.writeString(str);
        intent.writeToParcel(parcelObtain, 0);
        parcelObtain.writeString(str2);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeString(str3);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        parcelObtain.writeString(str4);
        if (parcelFileDescriptor != null) {
            parcelObtain.writeInt(1);
            parcelFileDescriptor.writeToParcel(parcelObtain, 1);
        } else {
            parcelObtain.writeInt(0);
        }
        if (bundle != null) {
            parcelObtain.writeInt(1);
            bundle.writeToParcel(parcelObtain, 0);
        } else {
            parcelObtain.writeInt(0);
        }
        parcelObtain.writeInt(i3);
        this.mRemote.transact(105, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        IActivityManager.WaitResult waitResultCreateFromParcel = IActivityManager.WaitResult.CREATOR.createFromParcel(parcelObtain2);
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return waitResultCreateFromParcel;
    }

    @Override // android.app.IActivityManager
    public int startActivityWithConfig(IApplicationThread iApplicationThread, String str, Intent intent, String str2, IBinder iBinder, String str3, int i, int i2, Configuration configuration, Bundle bundle, int i3) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread != null ? iApplicationThread.asBinder() : null);
        parcelObtain.writeString(str);
        intent.writeToParcel(parcelObtain, 0);
        parcelObtain.writeString(str2);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeString(str3);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        configuration.writeToParcel(parcelObtain, 0);
        if (bundle != null) {
            parcelObtain.writeInt(1);
            bundle.writeToParcel(parcelObtain, 0);
        } else {
            parcelObtain.writeInt(0);
        }
        parcelObtain.writeInt(i3);
        this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i4 = parcelObtain2.readInt();
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return i4;
    }

    @Override // android.app.IActivityManager
    public int startActivityIntentSender(IApplicationThread iApplicationThread, IntentSender intentSender, Intent intent, String str, IBinder iBinder, String str2, int i, int i2, int i3, Bundle bundle) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread != null ? iApplicationThread.asBinder() : null);
        intentSender.writeToParcel(parcelObtain, 0);
        if (intent != null) {
            parcelObtain.writeInt(1);
            intent.writeToParcel(parcelObtain, 0);
        } else {
            parcelObtain.writeInt(0);
        }
        parcelObtain.writeString(str);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeString(str2);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        parcelObtain.writeInt(i3);
        if (bundle != null) {
            parcelObtain.writeInt(1);
            bundle.writeToParcel(parcelObtain, 0);
        } else {
            parcelObtain.writeInt(0);
        }
        this.mRemote.transact(100, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i4 = parcelObtain2.readInt();
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return i4;
    }

    @Override // android.app.IActivityManager
    public boolean startNextMatchingActivity(IBinder iBinder, Intent intent, Bundle bundle) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        intent.writeToParcel(parcelObtain, 0);
        if (bundle != null) {
            parcelObtain.writeInt(1);
            bundle.writeToParcel(parcelObtain, 0);
        } else {
            parcelObtain.writeInt(0);
        }
        this.mRemote.transact(67, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i = parcelObtain2.readInt();
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return i != 0;
    }

    @Override // android.app.IActivityManager
    public boolean finishActivity(IBinder iBinder, int i, Intent intent) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeInt(i);
        if (intent != null) {
            parcelObtain.writeInt(1);
            intent.writeToParcel(parcelObtain, 0);
        } else {
            parcelObtain.writeInt(0);
        }
        this.mRemote.transact(11, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public void finishSubActivity(IBinder iBinder, String str, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(i);
        this.mRemote.transact(32, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean finishActivityAffinity(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(149, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public boolean willActivityBeVisible(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(106, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public Intent registerReceiver(IApplicationThread iApplicationThread, String str, IIntentReceiver iIntentReceiver, IntentFilter intentFilter, String str2, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread != null ? iApplicationThread.asBinder() : null);
        parcelObtain.writeString(str);
        parcelObtain.writeStrongBinder(iIntentReceiver != null ? iIntentReceiver.asBinder() : null);
        intentFilter.writeToParcel(parcelObtain, 0);
        parcelObtain.writeString(str2);
        parcelObtain.writeInt(i);
        this.mRemote.transact(12, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        Intent intentCreateFromParcel = parcelObtain2.readInt() != 0 ? Intent.CREATOR.createFromParcel(parcelObtain2) : null;
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return intentCreateFromParcel;
    }

    @Override // android.app.IActivityManager
    public void unregisterReceiver(IIntentReceiver iIntentReceiver) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iIntentReceiver.asBinder());
        this.mRemote.transact(13, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public int broadcastIntent(IApplicationThread iApplicationThread, Intent intent, String str, IIntentReceiver iIntentReceiver, int i, String str2, Bundle bundle, String str3, int i2, boolean z, boolean z2, int i3) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread != null ? iApplicationThread.asBinder() : null);
        intent.writeToParcel(parcelObtain, 0);
        parcelObtain.writeString(str);
        parcelObtain.writeStrongBinder(iIntentReceiver != null ? iIntentReceiver.asBinder() : null);
        parcelObtain.writeInt(i);
        parcelObtain.writeString(str2);
        parcelObtain.writeBundle(bundle);
        parcelObtain.writeString(str3);
        parcelObtain.writeInt(i2);
        parcelObtain.writeInt(z ? 1 : 0);
        parcelObtain.writeInt(z2 ? 1 : 0);
        parcelObtain.writeInt(i3);
        this.mRemote.transact(14, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i4 = parcelObtain2.readInt();
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return i4;
    }

    @Override // android.app.IActivityManager
    public void unbroadcastIntent(IApplicationThread iApplicationThread, Intent intent, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread != null ? iApplicationThread.asBinder() : null);
        intent.writeToParcel(parcelObtain, 0);
        parcelObtain.writeInt(i);
        this.mRemote.transact(15, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void finishReceiver(IBinder iBinder, int i, String str, Bundle bundle, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeInt(i);
        parcelObtain.writeString(str);
        parcelObtain.writeBundle(bundle);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(16, parcelObtain, parcelObtain2, 1);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void attachApplication(IApplicationThread iApplicationThread) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread.asBinder());
        this.mRemote.transact(17, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void activityIdle(IBinder iBinder, Configuration configuration, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        if (configuration != null) {
            parcelObtain.writeInt(1);
            configuration.writeToParcel(parcelObtain, 0);
        } else {
            parcelObtain.writeInt(0);
        }
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(18, parcelObtain, parcelObtain2, 1);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void activityResumed(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(39, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void activityPaused(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(19, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void activityStopped(IBinder iBinder, Bundle bundle, Bitmap bitmap, CharSequence charSequence) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeBundle(bundle);
        if (bitmap != null) {
            parcelObtain.writeInt(1);
            bitmap.writeToParcel(parcelObtain, 0);
        } else {
            parcelObtain.writeInt(0);
        }
        TextUtils.writeToParcel(charSequence, parcelObtain, 0);
        this.mRemote.transact(20, parcelObtain, parcelObtain2, 1);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void activitySlept(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(123, parcelObtain, parcelObtain2, 1);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void activityDestroyed(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(62, parcelObtain, parcelObtain2, 1);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public String getCallingPackage(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(21, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        String string = parcelObtain2.readString();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return string;
    }

    @Override // android.app.IActivityManager
    public ComponentName getCallingActivity(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(22, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        ComponentName fromParcel = ComponentName.readFromParcel(parcelObtain2);
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return fromParcel;
    }

    @Override // android.app.IActivityManager
    public List getTasks(int i, int i2, IThumbnailReceiver iThumbnailReceiver) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        ArrayList arrayList = null;
        parcelObtain.writeStrongBinder(iThumbnailReceiver != null ? iThumbnailReceiver.asBinder() : null);
        this.mRemote.transact(23, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i3 = parcelObtain2.readInt();
        if (i3 >= 0) {
            arrayList = new ArrayList();
            while (i3 > 0) {
                arrayList.add(ActivityManager.RunningTaskInfo.CREATOR.createFromParcel(parcelObtain2));
                i3--;
            }
        }
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return arrayList;
    }

    @Override // android.app.IActivityManager
    public List<ActivityManager.RecentTaskInfo> getRecentTasks(int i, int i2, int i3) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        parcelObtain.writeInt(i3);
        this.mRemote.transact(60, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        ArrayList arrayListCreateTypedArrayList = parcelObtain2.createTypedArrayList(ActivityManager.RecentTaskInfo.CREATOR);
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return arrayListCreateTypedArrayList;
    }

    @Override // android.app.IActivityManager
    public ActivityManager.TaskThumbnails getTaskThumbnails(int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        this.mRemote.transact(82, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        ActivityManager.TaskThumbnails taskThumbnailsCreateFromParcel = parcelObtain2.readInt() != 0 ? ActivityManager.TaskThumbnails.CREATOR.createFromParcel(parcelObtain2) : null;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return taskThumbnailsCreateFromParcel;
    }

    @Override // android.app.IActivityManager
    public Bitmap getTaskTopThumbnail(int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        this.mRemote.transact(95, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        Bitmap bitmapCreateFromParcel = parcelObtain2.readInt() != 0 ? Bitmap.CREATOR.createFromParcel(parcelObtain2) : null;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return bitmapCreateFromParcel;
    }

    @Override // android.app.IActivityManager
    public List getServices(int i, int i2) throws RemoteException {
        ArrayList arrayList;
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        this.mRemote.transact(81, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i3 = parcelObtain2.readInt();
        if (i3 >= 0) {
            arrayList = new ArrayList();
            while (i3 > 0) {
                arrayList.add(ActivityManager.RunningServiceInfo.CREATOR.createFromParcel(parcelObtain2));
                i3--;
            }
        } else {
            arrayList = null;
        }
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return arrayList;
    }

    @Override // android.app.IActivityManager
    public List<ActivityManager.ProcessErrorStateInfo> getProcessesInErrorState() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(77, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        ArrayList arrayListCreateTypedArrayList = parcelObtain2.createTypedArrayList(ActivityManager.ProcessErrorStateInfo.CREATOR);
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return arrayListCreateTypedArrayList;
    }

    @Override // android.app.IActivityManager
    public List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(83, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        ArrayList arrayListCreateTypedArrayList = parcelObtain2.createTypedArrayList(ActivityManager.RunningAppProcessInfo.CREATOR);
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return arrayListCreateTypedArrayList;
    }

    @Override // android.app.IActivityManager
    public List<ApplicationInfo> getRunningExternalApplications() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(108, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        ArrayList arrayListCreateTypedArrayList = parcelObtain2.createTypedArrayList(ApplicationInfo.CREATOR);
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return arrayListCreateTypedArrayList;
    }

    @Override // android.app.IActivityManager
    public void moveTaskToFront(int i, int i2, Bundle bundle) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        if (bundle != null) {
            parcelObtain.writeInt(1);
            bundle.writeToParcel(parcelObtain, 0);
        } else {
            parcelObtain.writeInt(0);
        }
        this.mRemote.transact(24, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void moveTaskToBack(int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        this.mRemote.transact(25, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean moveActivityTaskToBack(IBinder iBinder, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(75, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z2 = parcelObtain2.readInt() != 0;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z2;
    }

    @Override // android.app.IActivityManager
    public void moveTaskBackwards(int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        this.mRemote.transact(26, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public int createStack(int i, int i2, int i3, float f) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        parcelObtain.writeInt(i3);
        parcelObtain.writeFloat(f);
        this.mRemote.transact(168, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i4 = parcelObtain2.readInt();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return i4;
    }

    @Override // android.app.IActivityManager
    public void moveTaskToStack(int i, int i2, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(169, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void resizeStackBox(int i, float f) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        parcelObtain.writeFloat(f);
        this.mRemote.transact(170, parcelObtain, parcelObtain2, 1);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public List<ActivityManager.StackBoxInfo> getStackBoxes() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(171, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        ArrayList arrayListCreateTypedArrayList = parcelObtain2.createTypedArrayList(ActivityManager.StackBoxInfo.CREATOR);
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return arrayListCreateTypedArrayList;
    }

    @Override // android.app.IActivityManager
    public ActivityManager.StackBoxInfo getStackBoxInfo(int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        this.mRemote.transact(173, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        ActivityManager.StackBoxInfo stackBoxInfoCreateFromParcel = parcelObtain2.readInt() != 0 ? ActivityManager.StackBoxInfo.CREATOR.createFromParcel(parcelObtain2) : null;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return stackBoxInfoCreateFromParcel;
    }

    @Override // android.app.IActivityManager
    public void setFocusedStack(int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        this.mRemote.transact(172, parcelObtain, parcelObtain2, 1);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public int getTaskForActivity(IBinder iBinder, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(27, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i = parcelObtain2.readInt();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return i;
    }

    @Override // android.app.IActivityManager
    public void reportThumbnail(IBinder iBinder, Bitmap bitmap, CharSequence charSequence) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        if (bitmap != null) {
            parcelObtain.writeInt(1);
            bitmap.writeToParcel(parcelObtain, 0);
        } else {
            parcelObtain.writeInt(0);
        }
        TextUtils.writeToParcel(charSequence, parcelObtain, 0);
        this.mRemote.transact(28, parcelObtain, parcelObtain2, 1);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public IActivityManager.ContentProviderHolder getContentProvider(IApplicationThread iApplicationThread, String str, int i, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread != null ? iApplicationThread.asBinder() : null);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(29, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        IActivityManager.ContentProviderHolder contentProviderHolderCreateFromParcel = parcelObtain2.readInt() != 0 ? IActivityManager.ContentProviderHolder.CREATOR.createFromParcel(parcelObtain2) : null;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return contentProviderHolderCreateFromParcel;
    }

    @Override // android.app.IActivityManager
    public IActivityManager.ContentProviderHolder getContentProviderExternal(String str, int i, IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(i);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(141, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        IActivityManager.ContentProviderHolder contentProviderHolderCreateFromParcel = parcelObtain2.readInt() != 0 ? IActivityManager.ContentProviderHolder.CREATOR.createFromParcel(parcelObtain2) : null;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return contentProviderHolderCreateFromParcel;
    }

    @Override // android.app.IActivityManager
    public void publishContentProviders(IApplicationThread iApplicationThread, List<IActivityManager.ContentProviderHolder> list) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread != null ? iApplicationThread.asBinder() : null);
        parcelObtain.writeTypedList(list);
        this.mRemote.transact(30, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean refContentProvider(IBinder iBinder, int i, int i2) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        this.mRemote.transact(31, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public void unstableProviderDied(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(151, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void appNotRespondingViaProvider(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(183, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void removeContentProvider(IBinder iBinder, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(69, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void removeContentProviderExternal(String str, IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(142, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public PendingIntent getRunningServiceControlPanel(ComponentName componentName) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        componentName.writeToParcel(parcelObtain, 0);
        this.mRemote.transact(33, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        PendingIntent pendingIntentOrNullFromParcel = PendingIntent.readPendingIntentOrNullFromParcel(parcelObtain2);
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return pendingIntentOrNullFromParcel;
    }

    @Override // android.app.IActivityManager
    public ComponentName startService(IApplicationThread iApplicationThread, Intent intent, String str, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread != null ? iApplicationThread.asBinder() : null);
        intent.writeToParcel(parcelObtain, 0);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(i);
        this.mRemote.transact(34, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        ComponentName fromParcel = ComponentName.readFromParcel(parcelObtain2);
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return fromParcel;
    }

    @Override // android.app.IActivityManager
    public int stopService(IApplicationThread iApplicationThread, Intent intent, String str, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread != null ? iApplicationThread.asBinder() : null);
        intent.writeToParcel(parcelObtain, 0);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(i);
        this.mRemote.transact(35, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i2 = parcelObtain2.readInt();
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return i2;
    }

    @Override // android.app.IActivityManager
    public boolean stopServiceToken(ComponentName componentName, IBinder iBinder, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        ComponentName.writeToParcel(componentName, parcelObtain);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeInt(i);
        this.mRemote.transact(48, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public void setServiceForeground(ComponentName componentName, IBinder iBinder, int i, Notification notification, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        ComponentName.writeToParcel(componentName, parcelObtain);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeInt(i);
        if (notification != null) {
            parcelObtain.writeInt(1);
            notification.writeToParcel(parcelObtain, 0);
        } else {
            parcelObtain.writeInt(0);
        }
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(74, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public int bindService(IApplicationThread iApplicationThread, IBinder iBinder, Intent intent, String str, IServiceConnection iServiceConnection, int i, int i2) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread != null ? iApplicationThread.asBinder() : null);
        parcelObtain.writeStrongBinder(iBinder);
        intent.writeToParcel(parcelObtain, 0);
        parcelObtain.writeString(str);
        parcelObtain.writeStrongBinder(iServiceConnection.asBinder());
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        this.mRemote.transact(36, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i3 = parcelObtain2.readInt();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return i3;
    }

    @Override // android.app.IActivityManager
    public boolean unbindService(IServiceConnection iServiceConnection) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iServiceConnection.asBinder());
        this.mRemote.transact(37, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public void publishService(IBinder iBinder, Intent intent, IBinder iBinder2) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        intent.writeToParcel(parcelObtain, 0);
        parcelObtain.writeStrongBinder(iBinder2);
        this.mRemote.transact(38, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void unbindFinished(IBinder iBinder, Intent intent, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        intent.writeToParcel(parcelObtain, 0);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(72, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void serviceDoneExecuting(IBinder iBinder, int i, int i2, int i3) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        parcelObtain.writeInt(i3);
        this.mRemote.transact(61, parcelObtain, parcelObtain2, 1);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public IBinder peekService(Intent intent, String str) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        intent.writeToParcel(parcelObtain, 0);
        parcelObtain.writeString(str);
        this.mRemote.transact(85, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        IBinder strongBinder = parcelObtain2.readStrongBinder();
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return strongBinder;
    }

    @Override // android.app.IActivityManager
    public boolean bindBackupAgent(ApplicationInfo applicationInfo, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        applicationInfo.writeToParcel(parcelObtain, 0);
        parcelObtain.writeInt(i);
        this.mRemote.transact(90, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public void clearPendingBackup() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(160, parcelObtain, parcelObtain2, 0);
        parcelObtain2.recycle();
        parcelObtain.recycle();
    }

    @Override // android.app.IActivityManager
    public void backupAgentCreated(String str, IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(91, parcelObtain, parcelObtain2, 0);
        parcelObtain2.recycle();
        parcelObtain.recycle();
    }

    @Override // android.app.IActivityManager
    public void unbindBackupAgent(ApplicationInfo applicationInfo) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        applicationInfo.writeToParcel(parcelObtain, 0);
        this.mRemote.transact(92, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain2.recycle();
        parcelObtain.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean startInstrumentation(ComponentName componentName, String str, int i, Bundle bundle, IInstrumentationWatcher iInstrumentationWatcher, IUiAutomationConnection iUiAutomationConnection, int i2) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        ComponentName.writeToParcel(componentName, parcelObtain);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(i);
        parcelObtain.writeBundle(bundle);
        parcelObtain.writeStrongBinder(iInstrumentationWatcher != null ? iInstrumentationWatcher.asBinder() : null);
        parcelObtain.writeStrongBinder(iUiAutomationConnection != null ? iUiAutomationConnection.asBinder() : null);
        parcelObtain.writeInt(i2);
        this.mRemote.transact(44, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public void finishInstrumentation(IApplicationThread iApplicationThread, int i, Bundle bundle) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread != null ? iApplicationThread.asBinder() : null);
        parcelObtain.writeInt(i);
        parcelObtain.writeBundle(bundle);
        this.mRemote.transact(45, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public Configuration getConfiguration() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(46, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        Configuration configurationCreateFromParcel = Configuration.CREATOR.createFromParcel(parcelObtain2);
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return configurationCreateFromParcel;
    }

    @Override // android.app.IActivityManager
    public void updateConfiguration(Configuration configuration) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        configuration.writeToParcel(parcelObtain, 0);
        this.mRemote.transact(47, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void setRequestedOrientation(IBinder iBinder, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeInt(i);
        this.mRemote.transact(70, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public int getRequestedOrientation(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(71, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i = parcelObtain2.readInt();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return i;
    }

    @Override // android.app.IActivityManager
    public ComponentName getActivityClassForToken(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(49, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        ComponentName fromParcel = ComponentName.readFromParcel(parcelObtain2);
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return fromParcel;
    }

    @Override // android.app.IActivityManager
    public String getPackageForToken(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(50, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        String string = parcelObtain2.readString();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return string;
    }

    @Override // android.app.IActivityManager
    public IIntentSender getIntentSender(int i, String str, IBinder iBinder, String str2, int i2, Intent[] intentArr, String[] strArr, int i3, Bundle bundle, int i4) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        parcelObtain.writeString(str);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeString(str2);
        parcelObtain.writeInt(i2);
        if (intentArr != null) {
            parcelObtain.writeInt(1);
            parcelObtain.writeTypedArray(intentArr, 0);
            parcelObtain.writeStringArray(strArr);
        } else {
            parcelObtain.writeInt(0);
        }
        parcelObtain.writeInt(i3);
        if (bundle != null) {
            parcelObtain.writeInt(1);
            bundle.writeToParcel(parcelObtain, 0);
        } else {
            parcelObtain.writeInt(0);
        }
        parcelObtain.writeInt(i4);
        this.mRemote.transact(63, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        IIntentSender iIntentSenderAsInterface = IIntentSender.Stub.asInterface(parcelObtain2.readStrongBinder());
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return iIntentSenderAsInterface;
    }

    @Override // android.app.IActivityManager
    public void cancelIntentSender(IIntentSender iIntentSender) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iIntentSender.asBinder());
        this.mRemote.transact(64, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public String getPackageForIntentSender(IIntentSender iIntentSender) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iIntentSender.asBinder());
        this.mRemote.transact(65, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        String string = parcelObtain2.readString();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return string;
    }

    @Override // android.app.IActivityManager
    public int getUidForIntentSender(IIntentSender iIntentSender) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iIntentSender.asBinder());
        this.mRemote.transact(93, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i = parcelObtain2.readInt();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return i;
    }

    @Override // android.app.IActivityManager
    public int handleIncomingUser(int i, int i2, int i3, boolean z, boolean z2, String str, String str2) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        parcelObtain.writeInt(i3);
        parcelObtain.writeInt(z ? 1 : 0);
        parcelObtain.writeInt(z2 ? 1 : 0);
        parcelObtain.writeString(str);
        parcelObtain.writeString(str2);
        this.mRemote.transact(94, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i4 = parcelObtain2.readInt();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return i4;
    }

    @Override // android.app.IActivityManager
    public void setProcessLimit(int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        this.mRemote.transact(51, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public int getProcessLimit() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(52, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i = parcelObtain2.readInt();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return i;
    }

    @Override // android.app.IActivityManager
    public void setProcessForeground(IBinder iBinder, int i, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(73, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public int checkPermission(String str, int i, int i2) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        this.mRemote.transact(53, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i3 = parcelObtain2.readInt();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return i3;
    }

    @Override // android.app.IActivityManager
    public boolean clearApplicationUserData(String str, IPackageDataObserver iPackageDataObserver, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        parcelObtain.writeStrongBinder(iPackageDataObserver != null ? iPackageDataObserver.asBinder() : null);
        parcelObtain.writeInt(i);
        this.mRemote.transact(78, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public int checkUriPermission(Uri uri, int i, int i2, int i3) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        uri.writeToParcel(parcelObtain, 0);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        parcelObtain.writeInt(i3);
        this.mRemote.transact(54, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i4 = parcelObtain2.readInt();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return i4;
    }

    @Override // android.app.IActivityManager
    public void grantUriPermission(IApplicationThread iApplicationThread, String str, Uri uri, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread.asBinder());
        parcelObtain.writeString(str);
        uri.writeToParcel(parcelObtain, 0);
        parcelObtain.writeInt(i);
        this.mRemote.transact(55, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void revokeUriPermission(IApplicationThread iApplicationThread, Uri uri, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread.asBinder());
        uri.writeToParcel(parcelObtain, 0);
        parcelObtain.writeInt(i);
        this.mRemote.transact(56, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void takePersistableUriPermission(Uri uri, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        uri.writeToParcel(parcelObtain, 0);
        parcelObtain.writeInt(i);
        this.mRemote.transact(180, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void releasePersistableUriPermission(Uri uri, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        uri.writeToParcel(parcelObtain, 0);
        parcelObtain.writeInt(i);
        this.mRemote.transact(181, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public ParceledListSlice<UriPermission> getPersistedUriPermissions(String str, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(182, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        ParceledListSlice<UriPermission> parceledListSliceCreateFromParcel = ParceledListSlice.CREATOR.createFromParcel(parcelObtain2);
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return parceledListSliceCreateFromParcel;
    }

    @Override // android.app.IActivityManager
    public void showWaitingForDebugger(IApplicationThread iApplicationThread, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread.asBinder());
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(58, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void getMemoryInfo(ActivityManager.MemoryInfo memoryInfo) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(76, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        memoryInfo.readFromParcel(parcelObtain2);
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void unhandledBack() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public ParcelFileDescriptor openContentUri(Uri uri) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        ParcelFileDescriptor parcelFileDescriptorCreateFromParcel = parcelObtain2.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcelObtain2) : null;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return parcelFileDescriptorCreateFromParcel;
    }

    @Override // android.app.IActivityManager
    public void goingToSleep() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(40, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void wakingUp() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(41, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void setLockScreenShown(boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(148, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void setDebugApp(String str, boolean z, boolean z2) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(z ? 1 : 0);
        parcelObtain.writeInt(z2 ? 1 : 0);
        this.mRemote.transact(42, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void setAlwaysFinish(boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(43, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void setActivityController(IActivityController iActivityController) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iActivityController != null ? iActivityController.asBinder() : null);
        this.mRemote.transact(57, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void enterSafeMode() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(66, parcelObtain, null, 0);
        parcelObtain.recycle();
    }

    @Override // android.app.IActivityManager
    public void noteWakeupAlarm(IIntentSender iIntentSender) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        parcelObtain.writeStrongBinder(iIntentSender.asBinder());
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(68, parcelObtain, null, 0);
        parcelObtain.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean killPids(int[] iArr, String str, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeIntArray(iArr);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(80, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z2 = parcelObtain2.readInt() != 0;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z2;
    }

    @Override // android.app.IActivityManager
    public boolean killProcessesBelowForeground(String str) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        this.mRemote.transact(144, parcelObtain, parcelObtain2, 0);
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public void startRunning(String str, String str2, String str3, String str4) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        parcelObtain.writeString(str2);
        parcelObtain.writeString(str3);
        parcelObtain.writeString(str4);
        this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void handleApplicationCrash(IBinder iBinder, ApplicationErrorReport.CrashInfo crashInfo) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        crashInfo.writeToParcel(parcelObtain, 0);
        this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain2.recycle();
        parcelObtain.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean handleApplicationWtf(IBinder iBinder, String str, ApplicationErrorReport.CrashInfo crashInfo) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeString(str);
        crashInfo.writeToParcel(parcelObtain, 0);
        this.mRemote.transact(102, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public void handleApplicationStrictModeViolation(IBinder iBinder, int i, StrictMode.ViolationInfo violationInfo) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeInt(i);
        violationInfo.writeToParcel(parcelObtain, 0);
        this.mRemote.transact(110, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain2.recycle();
        parcelObtain.recycle();
    }

    @Override // android.app.IActivityManager
    public void signalPersistentProcesses(int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        this.mRemote.transact(59, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void killBackgroundProcesses(String str, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(i);
        this.mRemote.transact(103, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void killAllBackgroundProcesses() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(140, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void forceStopPackage(String str, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(i);
        this.mRemote.transact(79, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void getMyMemoryState(ActivityManager.RunningAppProcessInfo runningAppProcessInfo) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(143, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        runningAppProcessInfo.readFromParcel(parcelObtain2);
        parcelObtain2.recycle();
        parcelObtain.recycle();
    }

    @Override // android.app.IActivityManager
    public ConfigurationInfo getDeviceConfigurationInfo() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(84, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        ConfigurationInfo configurationInfoCreateFromParcel = ConfigurationInfo.CREATOR.createFromParcel(parcelObtain2);
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return configurationInfoCreateFromParcel;
    }

    @Override // android.app.IActivityManager
    public boolean profileControl(String str, int i, boolean z, String str2, ParcelFileDescriptor parcelFileDescriptor, int i2) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(z ? 1 : 0);
        parcelObtain.writeInt(i2);
        parcelObtain.writeString(str2);
        if (parcelFileDescriptor != null) {
            parcelObtain.writeInt(1);
            parcelFileDescriptor.writeToParcel(parcelObtain, 1);
        } else {
            parcelObtain.writeInt(0);
        }
        this.mRemote.transact(86, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z2 = parcelObtain2.readInt() != 0;
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return z2;
    }

    @Override // android.app.IActivityManager
    public boolean shutdown(int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        this.mRemote.transact(87, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public void stopAppSwitches() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(88, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain2.recycle();
        parcelObtain.recycle();
    }

    @Override // android.app.IActivityManager
    public void resumeAppSwitches() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(89, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain2.recycle();
        parcelObtain.recycle();
    }

    @Override // android.app.IActivityManager
    public void killApplicationWithAppId(String str, int i, String str2) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(i);
        parcelObtain.writeString(str2);
        this.mRemote.transact(96, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void closeSystemDialogs(String str) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        this.mRemote.transact(97, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public Debug.MemoryInfo[] getProcessMemoryInfo(int[] iArr) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeIntArray(iArr);
        this.mRemote.transact(98, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        Debug.MemoryInfo[] memoryInfoArr = (Debug.MemoryInfo[]) parcelObtain2.createTypedArray(Debug.MemoryInfo.CREATOR);
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return memoryInfoArr;
    }

    @Override // android.app.IActivityManager
    public void killApplicationProcess(String str, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(i);
        this.mRemote.transact(99, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void overridePendingTransition(IBinder iBinder, String str, int i, int i2) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        this.mRemote.transact(101, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean isUserAMonkey() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(104, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public void setUserIsMonkey(boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(166, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void finishHeavyWeightApp() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(109, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean convertFromTranslucent(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(174, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public boolean convertToTranslucent(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(175, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public void setImmersive(IBinder iBinder, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(112, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean isImmersive(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(111, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() == 1;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public boolean isTopActivityImmersive() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(113, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() == 1;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public void crashApplication(int i, int i2, String str, String str2) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        parcelObtain.writeString(str);
        parcelObtain.writeString(str2);
        this.mRemote.transact(114, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public String getProviderMimeType(Uri uri, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        uri.writeToParcel(parcelObtain, 0);
        parcelObtain.writeInt(i);
        this.mRemote.transact(115, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        String string = parcelObtain2.readString();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return string;
    }

    @Override // android.app.IActivityManager
    public IBinder newUriPermissionOwner(String str) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        this.mRemote.transact(116, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        IBinder strongBinder = parcelObtain2.readStrongBinder();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return strongBinder;
    }

    @Override // android.app.IActivityManager
    public void grantUriPermissionFromOwner(IBinder iBinder, int i, String str, Uri uri, int i2) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeInt(i);
        parcelObtain.writeString(str);
        uri.writeToParcel(parcelObtain, 0);
        parcelObtain.writeInt(i2);
        this.mRemote.transact(55, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void revokeUriPermissionFromOwner(IBinder iBinder, Uri uri, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        if (uri != null) {
            parcelObtain.writeInt(1);
            uri.writeToParcel(parcelObtain, 0);
        } else {
            parcelObtain.writeInt(0);
        }
        parcelObtain.writeInt(i);
        this.mRemote.transact(56, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public int checkGrantUriPermission(int i, String str, Uri uri, int i2) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        parcelObtain.writeString(str);
        uri.writeToParcel(parcelObtain, 0);
        parcelObtain.writeInt(i2);
        this.mRemote.transact(119, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i3 = parcelObtain2.readInt();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return i3;
    }

    @Override // android.app.IActivityManager
    public boolean dumpHeap(String str, int i, boolean z, String str2, ParcelFileDescriptor parcelFileDescriptor) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(z ? 1 : 0);
        parcelObtain.writeString(str2);
        if (parcelFileDescriptor != null) {
            parcelObtain.writeInt(1);
            parcelFileDescriptor.writeToParcel(parcelObtain, 1);
        } else {
            parcelObtain.writeInt(0);
        }
        this.mRemote.transact(120, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z2 = parcelObtain2.readInt() != 0;
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return z2;
    }

    @Override // android.app.IActivityManager
    public int startActivities(IApplicationThread iApplicationThread, String str, Intent[] intentArr, String[] strArr, IBinder iBinder, Bundle bundle, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iApplicationThread != null ? iApplicationThread.asBinder() : null);
        parcelObtain.writeString(str);
        parcelObtain.writeTypedArray(intentArr, 0);
        parcelObtain.writeStringArray(strArr);
        parcelObtain.writeStrongBinder(iBinder);
        if (bundle != null) {
            parcelObtain.writeInt(1);
            bundle.writeToParcel(parcelObtain, 0);
        } else {
            parcelObtain.writeInt(0);
        }
        parcelObtain.writeInt(i);
        this.mRemote.transact(121, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i2 = parcelObtain2.readInt();
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return i2;
    }

    @Override // android.app.IActivityManager
    public int getFrontActivityScreenCompatMode() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(124, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i = parcelObtain2.readInt();
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return i;
    }

    @Override // android.app.IActivityManager
    public void setFrontActivityScreenCompatMode(int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        this.mRemote.transact(125, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain2.recycle();
        parcelObtain.recycle();
    }

    @Override // android.app.IActivityManager
    public int getPackageScreenCompatMode(String str) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        this.mRemote.transact(126, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i = parcelObtain2.readInt();
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return i;
    }

    @Override // android.app.IActivityManager
    public void setPackageScreenCompatMode(String str, int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(i);
        this.mRemote.transact(127, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain2.recycle();
        parcelObtain.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean getPackageAskScreenCompat(String str) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        this.mRemote.transact(128, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public void setPackageAskScreenCompat(String str, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeString(str);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(129, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain2.recycle();
        parcelObtain.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean switchUser(int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        this.mRemote.transact(130, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public int stopUser(int i, IStopUserCallback iStopUserCallback) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        parcelObtain.writeStrongInterface(iStopUserCallback);
        this.mRemote.transact(154, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i2 = parcelObtain2.readInt();
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return i2;
    }

    @Override // android.app.IActivityManager
    public UserInfo getCurrentUser() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(145, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        UserInfo userInfoCreateFromParcel = UserInfo.CREATOR.createFromParcel(parcelObtain2);
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return userInfoCreateFromParcel;
    }

    @Override // android.app.IActivityManager
    public boolean isUserRunning(int i, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(122, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z2 = parcelObtain2.readInt() != 0;
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return z2;
    }

    @Override // android.app.IActivityManager
    public int[] getRunningUserIds() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(157, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int[] iArrCreateIntArray = parcelObtain2.createIntArray();
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return iArrCreateIntArray;
    }

    @Override // android.app.IActivityManager
    public boolean removeSubTask(int i, int i2) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        this.mRemote.transact(131, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public boolean removeTask(int i, int i2) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(i2);
        this.mRemote.transact(132, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public void registerProcessObserver(IProcessObserver iProcessObserver) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iProcessObserver != null ? iProcessObserver.asBinder() : null);
        this.mRemote.transact(133, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void unregisterProcessObserver(IProcessObserver iProcessObserver) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iProcessObserver != null ? iProcessObserver.asBinder() : null);
        this.mRemote.transact(134, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean isIntentSenderTargetedToPackage(IIntentSender iIntentSender) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iIntentSender.asBinder());
        this.mRemote.transact(135, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public boolean isIntentSenderAnActivity(IIntentSender iIntentSender) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iIntentSender.asBinder());
        this.mRemote.transact(152, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public Intent getIntentForIntentSender(IIntentSender iIntentSender) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iIntentSender.asBinder());
        this.mRemote.transact(161, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        Intent intentCreateFromParcel = parcelObtain2.readInt() != 0 ? Intent.CREATOR.createFromParcel(parcelObtain2) : null;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return intentCreateFromParcel;
    }

    @Override // android.app.IActivityManager
    public void updatePersistentConfiguration(Configuration configuration) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        configuration.writeToParcel(parcelObtain, 0);
        this.mRemote.transact(136, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public long[] getProcessPss(int[] iArr) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeIntArray(iArr);
        this.mRemote.transact(137, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        long[] jArrCreateLongArray = parcelObtain2.createLongArray();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return jArrCreateLongArray;
    }

    @Override // android.app.IActivityManager
    public void showBootMessage(CharSequence charSequence, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        TextUtils.writeToParcel(charSequence, parcelObtain, 0);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(138, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void dismissKeyguardOnNextActivity() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(139, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void sendBootFastComplete() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(184, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public boolean targetTaskAffinityMatchesActivity(IBinder iBinder, String str) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeString(str);
        this.mRemote.transact(146, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public boolean navigateUpTo(IBinder iBinder, Intent intent, int i, Intent intent2) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        intent.writeToParcel(parcelObtain, 0);
        parcelObtain.writeInt(i);
        if (intent2 != null) {
            parcelObtain.writeInt(1);
            intent2.writeToParcel(parcelObtain, 0);
        } else {
            parcelObtain.writeInt(0);
        }
        this.mRemote.transact(147, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        boolean z = parcelObtain2.readInt() != 0;
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return z;
    }

    @Override // android.app.IActivityManager
    public int getLaunchedFromUid(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(150, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        int i = parcelObtain2.readInt();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return i;
    }

    @Override // android.app.IActivityManager
    public String getLaunchedFromPackage(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(164, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        String string = parcelObtain2.readString();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return string;
    }

    @Override // android.app.IActivityManager
    public void registerUserSwitchObserver(IUserSwitchObserver iUserSwitchObserver) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iUserSwitchObserver != null ? iUserSwitchObserver.asBinder() : null);
        this.mRemote.transact(155, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void unregisterUserSwitchObserver(IUserSwitchObserver iUserSwitchObserver) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iUserSwitchObserver != null ? iUserSwitchObserver.asBinder() : null);
        this.mRemote.transact(156, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void requestBugReport() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(158, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public long inputDispatchingTimedOut(int i, boolean z, String str) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        parcelObtain.writeInt(z ? 1 : 0);
        parcelObtain.writeString(str);
        this.mRemote.transact(159, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        long j = parcelObtain2.readInt();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return j;
    }

    @Override // android.app.IActivityManager
    public Bundle getAssistContextExtras(int i) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        this.mRemote.transact(162, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        Bundle bundle = parcelObtain2.readBundle();
        parcelObtain.recycle();
        parcelObtain2.recycle();
        return bundle;
    }

    @Override // android.app.IActivityManager
    public void reportAssistContextExtras(IBinder iBinder, Bundle bundle) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeBundle(bundle);
        this.mRemote.transact(163, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void killUid(int i, String str) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeInt(i);
        parcelObtain.writeString(str);
        this.mRemote.transact(165, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void hang(IBinder iBinder, boolean z) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        parcelObtain.writeInt(z ? 1 : 0);
        this.mRemote.transact(167, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void reportActivityFullyDrawn(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(177, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void notifyActivityDrawn(IBinder iBinder) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        parcelObtain.writeStrongBinder(iBinder);
        this.mRemote.transact(176, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void restart() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(178, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }

    @Override // android.app.IActivityManager
    public void performIdleMaintenance() throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        parcelObtain.writeInterfaceToken(IActivityManager.descriptor);
        this.mRemote.transact(179, parcelObtain, parcelObtain2, 0);
        parcelObtain2.readException();
        parcelObtain.recycle();
        parcelObtain2.recycle();
    }
}
