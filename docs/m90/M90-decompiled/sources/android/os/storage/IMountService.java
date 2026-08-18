package android.os.storage;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.storage.IMountServiceListener;
import android.os.storage.IMountShutdownObserver;
import android.os.storage.IObbActionListener;

/* JADX INFO: loaded from: classes.dex */
public interface IMountService extends IInterface {
    public static final int ENCRYPTION_STATE_ERROR_INCOMPLETE = -2;
    public static final int ENCRYPTION_STATE_ERROR_UNKNOWN = -1;
    public static final int ENCRYPTION_STATE_NONE = 1;
    public static final int ENCRYPTION_STATE_OK = 0;

    int changeEncryptionPassword(String str) throws RemoteException;

    int createSecureContainer(String str, int i, String str2, String str3, int i2, boolean z) throws RemoteException;

    int decryptStorage(String str) throws RemoteException;

    int destroySecureContainer(String str, boolean z) throws RemoteException;

    int encryptStorage(String str) throws RemoteException;

    int finalizeSecureContainer(String str) throws RemoteException;

    void finishMediaUpdate() throws RemoteException;

    int fixPermissionsSecureContainer(String str, int i, String str2) throws RemoteException;

    int formatVolume(String str) throws RemoteException;

    int getEncryptionState() throws RemoteException;

    String getMountedObbPath(String str) throws RemoteException;

    String getSecureContainerFilesystemPath(String str) throws RemoteException;

    String[] getSecureContainerList() throws RemoteException;

    String getSecureContainerPath(String str) throws RemoteException;

    int[] getStorageUsers(String str) throws RemoteException;

    StorageVolume[] getVolumeList() throws RemoteException;

    String getVolumeState(String str) throws RemoteException;

    boolean isExternalStorageEmulated() throws RemoteException;

    boolean isObbMounted(String str) throws RemoteException;

    boolean isSecureContainerMounted(String str) throws RemoteException;

    boolean isUsbMassStorageConnected() throws RemoteException;

    boolean isUsbMassStorageEnabled() throws RemoteException;

    int mkdirs(String str, String str2) throws RemoteException;

    void mountObb(String str, String str2, String str3, IObbActionListener iObbActionListener, int i) throws RemoteException;

    int mountSecureContainer(String str, String str2, int i) throws RemoteException;

    int mountVolume(String str) throws RemoteException;

    void registerListener(IMountServiceListener iMountServiceListener) throws RemoteException;

    int renameSecureContainer(String str, String str2) throws RemoteException;

    void setUsbMassStorageEnabled(boolean z) throws RemoteException;

    void shutdown(IMountShutdownObserver iMountShutdownObserver) throws RemoteException;

    void unmountObb(String str, boolean z, IObbActionListener iObbActionListener, int i) throws RemoteException;

    int unmountSecureContainer(String str, boolean z) throws RemoteException;

    void unmountVolume(String str, boolean z, boolean z2) throws RemoteException;

    void unregisterListener(IMountServiceListener iMountServiceListener) throws RemoteException;

    int verifyEncryptionPassword(String str) throws RemoteException;

    public static abstract class Stub extends Binder implements IMountService {
        private static final String DESCRIPTOR = "IMountService";
        static final int TRANSACTION_changeEncryptionPassword = 29;
        static final int TRANSACTION_createSecureContainer = 11;
        static final int TRANSACTION_decryptStorage = 27;
        static final int TRANSACTION_destroySecureContainer = 13;
        static final int TRANSACTION_encryptStorage = 28;
        static final int TRANSACTION_finalizeSecureContainer = 12;
        static final int TRANSACTION_finishMediaUpdate = 21;
        static final int TRANSACTION_fixPermissionsSecureContainer = 34;
        static final int TRANSACTION_formatVolume = 8;
        static final int TRANSACTION_getEncryptionState = 32;
        static final int TRANSACTION_getMountedObbPath = 25;
        static final int TRANSACTION_getSecureContainerFilesystemPath = 31;
        static final int TRANSACTION_getSecureContainerList = 19;
        static final int TRANSACTION_getSecureContainerPath = 18;
        static final int TRANSACTION_getStorageUsers = 9;
        static final int TRANSACTION_getVolumeList = 30;
        static final int TRANSACTION_getVolumeState = 10;
        static final int TRANSACTION_isExternalStorageEmulated = 26;
        static final int TRANSACTION_isObbMounted = 24;
        static final int TRANSACTION_isSecureContainerMounted = 16;
        static final int TRANSACTION_isUsbMassStorageConnected = 3;
        static final int TRANSACTION_isUsbMassStorageEnabled = 5;
        static final int TRANSACTION_mkdirs = 35;
        static final int TRANSACTION_mountObb = 22;
        static final int TRANSACTION_mountSecureContainer = 14;
        static final int TRANSACTION_mountVolume = 6;
        static final int TRANSACTION_registerListener = 1;
        static final int TRANSACTION_renameSecureContainer = 17;
        static final int TRANSACTION_setUsbMassStorageEnabled = 4;
        static final int TRANSACTION_shutdown = 20;
        static final int TRANSACTION_unmountObb = 23;
        static final int TRANSACTION_unmountSecureContainer = 15;
        static final int TRANSACTION_unmountVolume = 7;
        static final int TRANSACTION_unregisterListener = 2;
        static final int TRANSACTION_verifyEncryptionPassword = 33;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        private static class Proxy implements IMountService {
            private final IBinder mRemote;

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

            @Override // android.os.storage.IMountService
            public void registerListener(IMountServiceListener iMountServiceListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iMountServiceListener != null ? iMountServiceListener.asBinder() : null);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public void unregisterListener(IMountServiceListener iMountServiceListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iMountServiceListener != null ? iMountServiceListener.asBinder() : null);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public boolean isUsbMassStorageConnected() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public void setUsbMassStorageEnabled(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public boolean isUsbMassStorageEnabled() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int mountVolume(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(6, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public void unmountVolume(String str, boolean z, boolean z2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    int i = 1;
                    parcelObtain.writeInt(z ? 1 : 0);
                    if (!z2) {
                        i = 0;
                    }
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(7, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int formatVolume(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(8, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int[] getStorageUsers(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(9, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createIntArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public String getVolumeState(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(10, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int createSecureContainer(String str, int i, String str2, String str3, int i2, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeString(str3);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(11, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int destroySecureContainer(String str, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(13, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int finalizeSecureContainer(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(12, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int mountSecureContainer(String str, String str2, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(14, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int unmountSecureContainer(String str, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(15, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public boolean isSecureContainerMounted(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(16, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int renameSecureContainer(String str, String str2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    this.mRemote.transact(17, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public String getSecureContainerPath(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(18, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public String[] getSecureContainerList() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createStringArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public void shutdown(IMountShutdownObserver iMountShutdownObserver) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iMountShutdownObserver != null ? iMountShutdownObserver.asBinder() : null);
                    this.mRemote.transact(20, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public void finishMediaUpdate() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(21, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public void mountObb(String str, String str2, String str3, IObbActionListener iObbActionListener, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeString(str3);
                    parcelObtain.writeStrongBinder(iObbActionListener != null ? iObbActionListener.asBinder() : null);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(22, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public void unmountObb(String str, boolean z, IObbActionListener iObbActionListener, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeStrongBinder(iObbActionListener != null ? iObbActionListener.asBinder() : null);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(23, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public boolean isObbMounted(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(24, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public String getMountedObbPath(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(25, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public boolean isExternalStorageEmulated() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(26, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int getEncryptionState() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int decryptStorage(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(27, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int encryptStorage(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(28, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int changeEncryptionPassword(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(29, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int verifyEncryptionPassword(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(33, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public StorageVolume[] getVolumeList() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(30, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return (StorageVolume[]) parcelObtain2.createTypedArray(StorageVolume.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public String getSecureContainerFilesystemPath(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(31, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int fixPermissionsSecureContainer(String str, int i, String str2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str2);
                    this.mRemote.transact(34, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int mkdirs(String str, String str2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    this.mRemote.transact(35, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public static IMountService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IMountService)) {
                return (IMountService) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 34) {
                parcel.enforceInterface(DESCRIPTOR);
                int iFixPermissionsSecureContainer = fixPermissionsSecureContainer(parcel.readString(), parcel.readInt(), parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(iFixPermissionsSecureContainer);
                return true;
            }
            if (i == 35) {
                parcel.enforceInterface(DESCRIPTOR);
                int iMkdirs = mkdirs(parcel.readString(), parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(iMkdirs);
                return true;
            }
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerListener(IMountServiceListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterListener(IMountServiceListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsUsbMassStorageConnected = isUsbMassStorageConnected();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsUsbMassStorageConnected ? 1 : 0);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    setUsbMassStorageEnabled(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsUsbMassStorageEnabled = isUsbMassStorageEnabled();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsUsbMassStorageEnabled ? 1 : 0);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iMountVolume = mountVolume(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iMountVolume);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    unmountVolume(parcel.readString(), parcel.readInt() != 0, parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    int volume = formatVolume(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(volume);
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    int[] storageUsers = getStorageUsers(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeIntArray(storageUsers);
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    String volumeState = getVolumeState(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeString(volumeState);
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iCreateSecureContainer = createSecureContainer(parcel.readString(), parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(iCreateSecureContainer);
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iFinalizeSecureContainer = finalizeSecureContainer(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iFinalizeSecureContainer);
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iDestroySecureContainer = destroySecureContainer(parcel.readString(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(iDestroySecureContainer);
                    return true;
                case 14:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iMountSecureContainer = mountSecureContainer(parcel.readString(), parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(iMountSecureContainer);
                    return true;
                case 15:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iUnmountSecureContainer = unmountSecureContainer(parcel.readString(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(iUnmountSecureContainer);
                    return true;
                case 16:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsSecureContainerMounted = isSecureContainerMounted(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsSecureContainerMounted ? 1 : 0);
                    return true;
                case 17:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iRenameSecureContainer = renameSecureContainer(parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iRenameSecureContainer);
                    return true;
                case 18:
                    parcel.enforceInterface(DESCRIPTOR);
                    String secureContainerPath = getSecureContainerPath(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeString(secureContainerPath);
                    return true;
                case 19:
                    parcel.enforceInterface(DESCRIPTOR);
                    String[] secureContainerList = getSecureContainerList();
                    parcel2.writeNoException();
                    parcel2.writeStringArray(secureContainerList);
                    return true;
                case 20:
                    parcel.enforceInterface(DESCRIPTOR);
                    shutdown(IMountShutdownObserver.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 21:
                    parcel.enforceInterface(DESCRIPTOR);
                    finishMediaUpdate();
                    parcel2.writeNoException();
                    return true;
                case 22:
                    parcel.enforceInterface(DESCRIPTOR);
                    mountObb(parcel.readString(), parcel.readString(), parcel.readString(), IObbActionListener.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 23:
                    parcel.enforceInterface(DESCRIPTOR);
                    unmountObb(parcel.readString(), parcel.readInt() != 0, IObbActionListener.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 24:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsObbMounted = isObbMounted(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsObbMounted ? 1 : 0);
                    return true;
                case 25:
                    parcel.enforceInterface(DESCRIPTOR);
                    String mountedObbPath = getMountedObbPath(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeString(mountedObbPath);
                    return true;
                case 26:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsExternalStorageEmulated = isExternalStorageEmulated();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsExternalStorageEmulated ? 1 : 0);
                    return true;
                case 27:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iDecryptStorage = decryptStorage(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iDecryptStorage);
                    return true;
                case 28:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iEncryptStorage = encryptStorage(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iEncryptStorage);
                    return true;
                case 29:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iChangeEncryptionPassword = changeEncryptionPassword(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iChangeEncryptionPassword);
                    return true;
                case 30:
                    parcel.enforceInterface(DESCRIPTOR);
                    StorageVolume[] volumeList = getVolumeList();
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(volumeList, 1);
                    return true;
                case 31:
                    parcel.enforceInterface(DESCRIPTOR);
                    String secureContainerFilesystemPath = getSecureContainerFilesystemPath(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeString(secureContainerFilesystemPath);
                    return true;
                case 32:
                    parcel.enforceInterface(DESCRIPTOR);
                    int encryptionState = getEncryptionState();
                    parcel2.writeNoException();
                    parcel2.writeInt(encryptionState);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }
}
