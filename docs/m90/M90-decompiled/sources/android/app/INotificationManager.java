package android.app;

import android.app.ITransientNotification;
import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.service.notification.INotificationListener;
import android.service.notification.StatusBarNotification;

/* JADX INFO: loaded from: classes.dex */
public interface INotificationManager extends IInterface {
    boolean areNotificationsEnabledForPackage(String str, int i) throws RemoteException;

    void cancelAllNotifications(String str, int i) throws RemoteException;

    void cancelAllNotificationsFromListener(INotificationListener iNotificationListener) throws RemoteException;

    void cancelNotificationFromListener(INotificationListener iNotificationListener, String str, String str2, int i) throws RemoteException;

    void cancelNotificationWithTag(String str, String str2, int i, int i2) throws RemoteException;

    void cancelToast(String str, ITransientNotification iTransientNotification) throws RemoteException;

    void enqueueNotificationWithTag(String str, String str2, String str3, int i, Notification notification, int[] iArr, int i2) throws RemoteException;

    void enqueueToast(String str, ITransientNotification iTransientNotification, int i) throws RemoteException;

    StatusBarNotification[] getActiveNotifications(String str) throws RemoteException;

    StatusBarNotification[] getActiveNotificationsFromListener(INotificationListener iNotificationListener) throws RemoteException;

    StatusBarNotification[] getHistoricalNotifications(String str, int i) throws RemoteException;

    void registerListener(INotificationListener iNotificationListener, ComponentName componentName, int i) throws RemoteException;

    void setNotificationsEnabledForPackage(String str, int i, boolean z) throws RemoteException;

    void unregisterListener(INotificationListener iNotificationListener, int i) throws RemoteException;

    public static abstract class Stub extends Binder implements INotificationManager {
        private static final String DESCRIPTOR = "android.app.INotificationManager";
        static final int TRANSACTION_areNotificationsEnabledForPackage = 7;
        static final int TRANSACTION_cancelAllNotifications = 1;
        static final int TRANSACTION_cancelAllNotificationsFromListener = 13;
        static final int TRANSACTION_cancelNotificationFromListener = 12;
        static final int TRANSACTION_cancelNotificationWithTag = 5;
        static final int TRANSACTION_cancelToast = 3;
        static final int TRANSACTION_enqueueNotificationWithTag = 4;
        static final int TRANSACTION_enqueueToast = 2;
        static final int TRANSACTION_getActiveNotifications = 8;
        static final int TRANSACTION_getActiveNotificationsFromListener = 14;
        static final int TRANSACTION_getHistoricalNotifications = 9;
        static final int TRANSACTION_registerListener = 10;
        static final int TRANSACTION_setNotificationsEnabledForPackage = 6;
        static final int TRANSACTION_unregisterListener = 11;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INotificationManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof INotificationManager)) {
                return (INotificationManager) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    cancelAllNotifications(parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    enqueueToast(parcel.readString(), ITransientNotification.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    cancelToast(parcel.readString(), ITransientNotification.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    String string = parcel.readString();
                    String string2 = parcel.readString();
                    String string3 = parcel.readString();
                    int i3 = parcel.readInt();
                    Notification notificationCreateFromParcel = parcel.readInt() != 0 ? Notification.CREATOR.createFromParcel(parcel) : null;
                    int[] iArrCreateIntArray = parcel.createIntArray();
                    enqueueNotificationWithTag(string, string2, string3, i3, notificationCreateFromParcel, iArrCreateIntArray, parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeIntArray(iArrCreateIntArray);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    cancelNotificationWithTag(parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    setNotificationsEnabledForPackage(parcel.readString(), parcel.readInt(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zAreNotificationsEnabledForPackage = areNotificationsEnabledForPackage(parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zAreNotificationsEnabledForPackage ? 1 : 0);
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    StatusBarNotification[] activeNotifications = getActiveNotifications(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(activeNotifications, 1);
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    StatusBarNotification[] historicalNotifications = getHistoricalNotifications(parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(historicalNotifications, 1);
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerListener(INotificationListener.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt() != 0 ? ComponentName.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterListener(INotificationListener.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    cancelNotificationFromListener(INotificationListener.Stub.asInterface(parcel.readStrongBinder()), parcel.readString(), parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    cancelAllNotificationsFromListener(INotificationListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 14:
                    parcel.enforceInterface(DESCRIPTOR);
                    StatusBarNotification[] activeNotificationsFromListener = getActiveNotificationsFromListener(INotificationListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(activeNotificationsFromListener, 1);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements INotificationManager {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.app.INotificationManager
            public void cancelAllNotifications(String str, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.app.INotificationManager
            public void enqueueToast(String str, ITransientNotification iTransientNotification, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeStrongBinder(iTransientNotification != null ? iTransientNotification.asBinder() : null);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.app.INotificationManager
            public void cancelToast(String str, ITransientNotification iTransientNotification) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeStrongBinder(iTransientNotification != null ? iTransientNotification.asBinder() : null);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.app.INotificationManager
            public void enqueueNotificationWithTag(String str, String str2, String str3, int i, Notification notification, int[] iArr, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeString(str3);
                    parcelObtain.writeInt(i);
                    if (notification != null) {
                        parcelObtain.writeInt(1);
                        notification.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeIntArray(iArr);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    parcelObtain2.readIntArray(iArr);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.app.INotificationManager
            public void cancelNotificationWithTag(String str, String str2, int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.app.INotificationManager
            public void setNotificationsEnabledForPackage(String str, int i, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(6, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.app.INotificationManager
            public boolean areNotificationsEnabledForPackage(String str, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(7, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.app.INotificationManager
            public StatusBarNotification[] getActiveNotifications(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(8, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return (StatusBarNotification[]) parcelObtain2.createTypedArray(StatusBarNotification.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.app.INotificationManager
            public StatusBarNotification[] getHistoricalNotifications(String str, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(9, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return (StatusBarNotification[]) parcelObtain2.createTypedArray(StatusBarNotification.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.app.INotificationManager
            public void registerListener(INotificationListener iNotificationListener, ComponentName componentName, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iNotificationListener != null ? iNotificationListener.asBinder() : null);
                    if (componentName != null) {
                        parcelObtain.writeInt(1);
                        componentName.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(10, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.app.INotificationManager
            public void unregisterListener(INotificationListener iNotificationListener, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iNotificationListener != null ? iNotificationListener.asBinder() : null);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(11, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.app.INotificationManager
            public void cancelNotificationFromListener(INotificationListener iNotificationListener, String str, String str2, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iNotificationListener != null ? iNotificationListener.asBinder() : null);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(12, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.app.INotificationManager
            public void cancelAllNotificationsFromListener(INotificationListener iNotificationListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iNotificationListener != null ? iNotificationListener.asBinder() : null);
                    this.mRemote.transact(13, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.app.INotificationManager
            public StatusBarNotification[] getActiveNotificationsFromListener(INotificationListener iNotificationListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iNotificationListener != null ? iNotificationListener.asBinder() : null);
                    this.mRemote.transact(14, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return (StatusBarNotification[]) parcelObtain2.createTypedArray(StatusBarNotification.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}
