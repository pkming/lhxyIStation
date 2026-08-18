package android.app;

import android.app.backup.IBackupManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IBackupAgent extends IInterface {
    void doBackup(ParcelFileDescriptor parcelFileDescriptor, ParcelFileDescriptor parcelFileDescriptor2, ParcelFileDescriptor parcelFileDescriptor3, int i, IBackupManager iBackupManager) throws RemoteException;

    void doFullBackup(ParcelFileDescriptor parcelFileDescriptor, int i, IBackupManager iBackupManager) throws RemoteException;

    void doRestore(ParcelFileDescriptor parcelFileDescriptor, int i, ParcelFileDescriptor parcelFileDescriptor2, int i2, IBackupManager iBackupManager) throws RemoteException;

    void doRestoreFile(ParcelFileDescriptor parcelFileDescriptor, long j, int i, String str, String str2, long j2, long j3, int i2, IBackupManager iBackupManager) throws RemoteException;

    public static abstract class Stub extends Binder implements IBackupAgent {
        private static final String DESCRIPTOR = "android.app.IBackupAgent";
        static final int TRANSACTION_doBackup = 1;
        static final int TRANSACTION_doFullBackup = 3;
        static final int TRANSACTION_doRestore = 2;
        static final int TRANSACTION_doRestoreFile = 4;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBackupAgent asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IBackupAgent)) {
                return (IBackupAgent) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                doBackup(parcel.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), IBackupManager.Stub.asInterface(parcel.readStrongBinder()));
                return true;
            }
            if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                doRestore(parcel.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), IBackupManager.Stub.asInterface(parcel.readStrongBinder()));
                return true;
            }
            if (i == 3) {
                parcel.enforceInterface(DESCRIPTOR);
                doFullBackup(parcel.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), IBackupManager.Stub.asInterface(parcel.readStrongBinder()));
                return true;
            }
            if (i == 4) {
                parcel.enforceInterface(DESCRIPTOR);
                doRestoreFile(parcel.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null, parcel.readLong(), parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readLong(), parcel.readLong(), parcel.readInt(), IBackupManager.Stub.asInterface(parcel.readStrongBinder()));
                return true;
            }
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            return super.onTransact(i, parcel, parcel2, i2);
        }

        private static class Proxy implements IBackupAgent {
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

            @Override // android.app.IBackupAgent
            public void doBackup(ParcelFileDescriptor parcelFileDescriptor, ParcelFileDescriptor parcelFileDescriptor2, ParcelFileDescriptor parcelFileDescriptor3, int i, IBackupManager iBackupManager) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (parcelFileDescriptor != null) {
                        parcelObtain.writeInt(1);
                        parcelFileDescriptor.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (parcelFileDescriptor2 != null) {
                        parcelObtain.writeInt(1);
                        parcelFileDescriptor2.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (parcelFileDescriptor3 != null) {
                        parcelObtain.writeInt(1);
                        parcelFileDescriptor3.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i);
                    parcelObtain.writeStrongBinder(iBackupManager != null ? iBackupManager.asBinder() : null);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.app.IBackupAgent
            public void doRestore(ParcelFileDescriptor parcelFileDescriptor, int i, ParcelFileDescriptor parcelFileDescriptor2, int i2, IBackupManager iBackupManager) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (parcelFileDescriptor != null) {
                        parcelObtain.writeInt(1);
                        parcelFileDescriptor.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i);
                    if (parcelFileDescriptor2 != null) {
                        parcelObtain.writeInt(1);
                        parcelFileDescriptor2.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeStrongBinder(iBackupManager != null ? iBackupManager.asBinder() : null);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.app.IBackupAgent
            public void doFullBackup(ParcelFileDescriptor parcelFileDescriptor, int i, IBackupManager iBackupManager) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (parcelFileDescriptor != null) {
                        parcelObtain.writeInt(1);
                        parcelFileDescriptor.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i);
                    parcelObtain.writeStrongBinder(iBackupManager != null ? iBackupManager.asBinder() : null);
                    this.mRemote.transact(3, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.app.IBackupAgent
            public void doRestoreFile(ParcelFileDescriptor parcelFileDescriptor, long j, int i, String str, String str2, long j2, long j3, int i2, IBackupManager iBackupManager) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (parcelFileDescriptor != null) {
                        parcelObtain.writeInt(1);
                        parcelFileDescriptor.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeLong(j);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeLong(j2);
                    parcelObtain.writeLong(j3);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeStrongBinder(iBackupManager != null ? iBackupManager.asBinder() : null);
                    this.mRemote.transact(4, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }
    }
}
