package android.bluetooth;

import android.bluetooth.IBluetooth;
import android.bluetooth.IBluetoothGatt;
import android.bluetooth.IBluetoothManagerCallback;
import android.bluetooth.IBluetoothProfileServiceConnection;
import android.bluetooth.IBluetoothStateChangeCallback;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IBluetoothManager extends IInterface {
    boolean bindBluetoothProfileService(int i, IBluetoothProfileServiceConnection iBluetoothProfileServiceConnection) throws RemoteException;

    boolean disable(boolean z) throws RemoteException;

    boolean enable() throws RemoteException;

    boolean enableNoAutoConnect() throws RemoteException;

    String getAddress() throws RemoteException;

    IBluetoothGatt getBluetoothGatt() throws RemoteException;

    String getName() throws RemoteException;

    boolean isEnabled() throws RemoteException;

    IBluetooth registerAdapter(IBluetoothManagerCallback iBluetoothManagerCallback) throws RemoteException;

    void registerStateChangeCallback(IBluetoothStateChangeCallback iBluetoothStateChangeCallback) throws RemoteException;

    void unbindBluetoothProfileService(int i, IBluetoothProfileServiceConnection iBluetoothProfileServiceConnection) throws RemoteException;

    void unregisterAdapter(IBluetoothManagerCallback iBluetoothManagerCallback) throws RemoteException;

    void unregisterStateChangeCallback(IBluetoothStateChangeCallback iBluetoothStateChangeCallback) throws RemoteException;

    public static abstract class Stub extends Binder implements IBluetoothManager {
        private static final String DESCRIPTOR = "android.bluetooth.IBluetoothManager";
        static final int TRANSACTION_bindBluetoothProfileService = 10;
        static final int TRANSACTION_disable = 8;
        static final int TRANSACTION_enable = 6;
        static final int TRANSACTION_enableNoAutoConnect = 7;
        static final int TRANSACTION_getAddress = 12;
        static final int TRANSACTION_getBluetoothGatt = 9;
        static final int TRANSACTION_getName = 13;
        static final int TRANSACTION_isEnabled = 5;
        static final int TRANSACTION_registerAdapter = 1;
        static final int TRANSACTION_registerStateChangeCallback = 3;
        static final int TRANSACTION_unbindBluetoothProfileService = 11;
        static final int TRANSACTION_unregisterAdapter = 2;
        static final int TRANSACTION_unregisterStateChangeCallback = 4;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBluetoothManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IBluetoothManager)) {
                return (IBluetoothManager) iInterfaceQueryLocalInterface;
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
                    IBluetooth iBluetoothRegisterAdapter = registerAdapter(IBluetoothManagerCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(iBluetoothRegisterAdapter != null ? iBluetoothRegisterAdapter.asBinder() : null);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterAdapter(IBluetoothManagerCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerStateChangeCallback(IBluetoothStateChangeCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterStateChangeCallback(IBluetoothStateChangeCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsEnabled = isEnabled();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsEnabled ? 1 : 0);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zEnable = enable();
                    parcel2.writeNoException();
                    parcel2.writeInt(zEnable ? 1 : 0);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zEnableNoAutoConnect = enableNoAutoConnect();
                    parcel2.writeNoException();
                    parcel2.writeInt(zEnableNoAutoConnect ? 1 : 0);
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zDisable = disable(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(zDisable ? 1 : 0);
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    IBluetoothGatt bluetoothGatt = getBluetoothGatt();
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(bluetoothGatt != null ? bluetoothGatt.asBinder() : null);
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zBindBluetoothProfileService = bindBluetoothProfileService(parcel.readInt(), IBluetoothProfileServiceConnection.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(zBindBluetoothProfileService ? 1 : 0);
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    unbindBluetoothProfileService(parcel.readInt(), IBluetoothProfileServiceConnection.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    String address = getAddress();
                    parcel2.writeNoException();
                    parcel2.writeString(address);
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    String name = getName();
                    parcel2.writeNoException();
                    parcel2.writeString(name);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements IBluetoothManager {
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

            @Override // android.bluetooth.IBluetoothManager
            public IBluetooth registerAdapter(IBluetoothManagerCallback iBluetoothManagerCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBluetoothManagerCallback != null ? iBluetoothManagerCallback.asBinder() : null);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return IBluetooth.Stub.asInterface(parcelObtain2.readStrongBinder());
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothManager
            public void unregisterAdapter(IBluetoothManagerCallback iBluetoothManagerCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBluetoothManagerCallback != null ? iBluetoothManagerCallback.asBinder() : null);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothManager
            public void registerStateChangeCallback(IBluetoothStateChangeCallback iBluetoothStateChangeCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBluetoothStateChangeCallback != null ? iBluetoothStateChangeCallback.asBinder() : null);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothManager
            public void unregisterStateChangeCallback(IBluetoothStateChangeCallback iBluetoothStateChangeCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBluetoothStateChangeCallback != null ? iBluetoothStateChangeCallback.asBinder() : null);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothManager
            public boolean isEnabled() throws RemoteException {
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

            @Override // android.bluetooth.IBluetoothManager
            public boolean enable() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothManager
            public boolean enableNoAutoConnect() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothManager
            public boolean disable(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(8, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothManager
            public IBluetoothGatt getBluetoothGatt() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return IBluetoothGatt.Stub.asInterface(parcelObtain2.readStrongBinder());
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothManager
            public boolean bindBluetoothProfileService(int i, IBluetoothProfileServiceConnection iBluetoothProfileServiceConnection) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeStrongBinder(iBluetoothProfileServiceConnection != null ? iBluetoothProfileServiceConnection.asBinder() : null);
                    this.mRemote.transact(10, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothManager
            public void unbindBluetoothProfileService(int i, IBluetoothProfileServiceConnection iBluetoothProfileServiceConnection) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeStrongBinder(iBluetoothProfileServiceConnection != null ? iBluetoothProfileServiceConnection.asBinder() : null);
                    this.mRemote.transact(11, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothManager
            public String getAddress() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothManager
            public String getName() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}
