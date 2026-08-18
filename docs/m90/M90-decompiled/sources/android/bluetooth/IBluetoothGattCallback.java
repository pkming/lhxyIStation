package android.bluetooth;

import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanResult;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.RemoteException;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface IBluetoothGattCallback extends IInterface {
    void onBatchScanResults(List<ScanResult> list) throws RemoteException;

    void onCharacteristicRead(String str, int i, int i2, int i3, ParcelUuid parcelUuid, int i4, ParcelUuid parcelUuid2, byte[] bArr) throws RemoteException;

    void onCharacteristicWrite(String str, int i, int i2, int i3, ParcelUuid parcelUuid, int i4, ParcelUuid parcelUuid2) throws RemoteException;

    void onClientConnectionState(int i, int i2, boolean z, String str) throws RemoteException;

    void onClientRegistered(int i, int i2) throws RemoteException;

    void onConfigureMTU(String str, int i, int i2) throws RemoteException;

    void onDescriptorRead(String str, int i, int i2, int i3, ParcelUuid parcelUuid, int i4, ParcelUuid parcelUuid2, int i5, ParcelUuid parcelUuid3, byte[] bArr) throws RemoteException;

    void onDescriptorWrite(String str, int i, int i2, int i3, ParcelUuid parcelUuid, int i4, ParcelUuid parcelUuid2, int i5, ParcelUuid parcelUuid3) throws RemoteException;

    void onExecuteWrite(String str, int i) throws RemoteException;

    void onFoundOrLost(boolean z, ScanResult scanResult) throws RemoteException;

    void onGetCharacteristic(String str, int i, int i2, ParcelUuid parcelUuid, int i3, ParcelUuid parcelUuid2, int i4) throws RemoteException;

    void onGetDescriptor(String str, int i, int i2, ParcelUuid parcelUuid, int i3, ParcelUuid parcelUuid2, int i4, ParcelUuid parcelUuid3) throws RemoteException;

    void onGetIncludedService(String str, int i, int i2, ParcelUuid parcelUuid, int i3, int i4, ParcelUuid parcelUuid2) throws RemoteException;

    void onGetService(String str, int i, int i2, ParcelUuid parcelUuid) throws RemoteException;

    void onMultiAdvertiseCallback(int i, boolean z, AdvertiseSettings advertiseSettings) throws RemoteException;

    void onNotify(String str, int i, int i2, ParcelUuid parcelUuid, int i3, ParcelUuid parcelUuid2, byte[] bArr) throws RemoteException;

    void onReadRemoteRssi(String str, int i, int i2) throws RemoteException;

    void onScanResult(ScanResult scanResult) throws RemoteException;

    void onSearchComplete(String str, int i) throws RemoteException;

    public static abstract class Stub extends Binder implements IBluetoothGattCallback {
        private static final String DESCRIPTOR = "android.bluetooth.IBluetoothGattCallback";
        static final int TRANSACTION_onBatchScanResults = 4;
        static final int TRANSACTION_onCharacteristicRead = 10;
        static final int TRANSACTION_onCharacteristicWrite = 11;
        static final int TRANSACTION_onClientConnectionState = 2;
        static final int TRANSACTION_onClientRegistered = 1;
        static final int TRANSACTION_onConfigureMTU = 18;
        static final int TRANSACTION_onDescriptorRead = 13;
        static final int TRANSACTION_onDescriptorWrite = 14;
        static final int TRANSACTION_onExecuteWrite = 12;
        static final int TRANSACTION_onFoundOrLost = 19;
        static final int TRANSACTION_onGetCharacteristic = 7;
        static final int TRANSACTION_onGetDescriptor = 8;
        static final int TRANSACTION_onGetIncludedService = 6;
        static final int TRANSACTION_onGetService = 5;
        static final int TRANSACTION_onMultiAdvertiseCallback = 17;
        static final int TRANSACTION_onNotify = 15;
        static final int TRANSACTION_onReadRemoteRssi = 16;
        static final int TRANSACTION_onScanResult = 3;
        static final int TRANSACTION_onSearchComplete = 9;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBluetoothGattCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IBluetoothGattCallback)) {
                return (IBluetoothGattCallback) iInterfaceQueryLocalInterface;
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
                    onClientRegistered(parcel.readInt(), parcel.readInt());
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    onClientConnectionState(parcel.readInt(), parcel.readInt(), parcel.readInt() != 0, parcel.readString());
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    onScanResult(parcel.readInt() != 0 ? ScanResult.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    onBatchScanResults(parcel.createTypedArrayList(ScanResult.CREATOR));
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    onGetService(parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    onGetIncludedService(parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    onGetCharacteristic(parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    onGetDescriptor(parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    onSearchComplete(parcel.readString(), parcel.readInt());
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    onCharacteristicRead(parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.createByteArray());
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    onCharacteristicWrite(parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    onExecuteWrite(parcel.readString(), parcel.readInt());
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    onDescriptorRead(parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.createByteArray());
                    return true;
                case 14:
                    parcel.enforceInterface(DESCRIPTOR);
                    onDescriptorWrite(parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 15:
                    parcel.enforceInterface(DESCRIPTOR);
                    onNotify(parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readInt() != 0 ? ParcelUuid.CREATOR.createFromParcel(parcel) : null, parcel.createByteArray());
                    return true;
                case 16:
                    parcel.enforceInterface(DESCRIPTOR);
                    onReadRemoteRssi(parcel.readString(), parcel.readInt(), parcel.readInt());
                    return true;
                case 17:
                    parcel.enforceInterface(DESCRIPTOR);
                    onMultiAdvertiseCallback(parcel.readInt(), parcel.readInt() != 0, parcel.readInt() != 0 ? AdvertiseSettings.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 18:
                    parcel.enforceInterface(DESCRIPTOR);
                    onConfigureMTU(parcel.readString(), parcel.readInt(), parcel.readInt());
                    return true;
                case 19:
                    parcel.enforceInterface(DESCRIPTOR);
                    onFoundOrLost(parcel.readInt() != 0, parcel.readInt() != 0 ? ScanResult.CREATOR.createFromParcel(parcel) : null);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements IBluetoothGattCallback {
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

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onClientRegistered(int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onClientConnectionState(int i, int i2, boolean z, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onScanResult(ScanResult scanResult) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (scanResult != null) {
                        parcelObtain.writeInt(1);
                        scanResult.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(3, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onBatchScanResults(List<ScanResult> list) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeTypedList(list);
                    this.mRemote.transact(4, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onGetService(String str, int i, int i2, ParcelUuid parcelUuid) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (parcelUuid != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(5, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onGetIncludedService(String str, int i, int i2, ParcelUuid parcelUuid, int i3, int i4, ParcelUuid parcelUuid2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (parcelUuid != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i3);
                    parcelObtain.writeInt(i4);
                    if (parcelUuid2 != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid2.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(6, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onGetCharacteristic(String str, int i, int i2, ParcelUuid parcelUuid, int i3, ParcelUuid parcelUuid2, int i4) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (parcelUuid != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i3);
                    if (parcelUuid2 != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid2.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i4);
                    this.mRemote.transact(7, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onGetDescriptor(String str, int i, int i2, ParcelUuid parcelUuid, int i3, ParcelUuid parcelUuid2, int i4, ParcelUuid parcelUuid3) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (parcelUuid != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i3);
                    if (parcelUuid2 != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid2.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i4);
                    if (parcelUuid3 != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid3.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(8, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onSearchComplete(String str, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(9, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onCharacteristicRead(String str, int i, int i2, int i3, ParcelUuid parcelUuid, int i4, ParcelUuid parcelUuid2, byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    if (parcelUuid != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i4);
                    if (parcelUuid2 != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid2.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeByteArray(bArr);
                    this.mRemote.transact(10, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onCharacteristicWrite(String str, int i, int i2, int i3, ParcelUuid parcelUuid, int i4, ParcelUuid parcelUuid2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    if (parcelUuid != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i4);
                    if (parcelUuid2 != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid2.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(11, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onExecuteWrite(String str, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(12, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onDescriptorRead(String str, int i, int i2, int i3, ParcelUuid parcelUuid, int i4, ParcelUuid parcelUuid2, int i5, ParcelUuid parcelUuid3, byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    if (parcelUuid != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i4);
                    if (parcelUuid2 != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid2.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i5);
                    if (parcelUuid3 != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid3.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeByteArray(bArr);
                    this.mRemote.transact(13, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onDescriptorWrite(String str, int i, int i2, int i3, ParcelUuid parcelUuid, int i4, ParcelUuid parcelUuid2, int i5, ParcelUuid parcelUuid3) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    if (parcelUuid != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i4);
                    if (parcelUuid2 != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid2.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i5);
                    if (parcelUuid3 != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid3.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(14, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onNotify(String str, int i, int i2, ParcelUuid parcelUuid, int i3, ParcelUuid parcelUuid2, byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (parcelUuid != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i3);
                    if (parcelUuid2 != null) {
                        parcelObtain.writeInt(1);
                        parcelUuid2.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeByteArray(bArr);
                    this.mRemote.transact(15, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onReadRemoteRssi(String str, int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(16, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onMultiAdvertiseCallback(int i, boolean z, AdvertiseSettings advertiseSettings) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(z ? 1 : 0);
                    if (advertiseSettings != null) {
                        parcelObtain.writeInt(1);
                        advertiseSettings.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(17, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onConfigureMTU(String str, int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(18, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.bluetooth.IBluetoothGattCallback
            public void onFoundOrLost(boolean z, ScanResult scanResult) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    if (scanResult != null) {
                        parcelObtain.writeInt(1);
                        scanResult.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(19, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }
    }
}
