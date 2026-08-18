package android.bluetooth;

import android.bluetooth.IBluetoothGattServerCallback;
import android.content.Context;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public final class BluetoothGattServer implements BluetoothProfile {
    private static final int CALLBACK_REG_TIMEOUT = 10000;
    private static final boolean DBG = true;
    private static final String TAG = "BluetoothGattServer";
    private static final boolean VDBG = false;
    private final Context mContext;
    private IBluetoothGatt mService;
    private int mTransport;
    private Object mServerIfLock = new Object();
    private final IBluetoothGattServerCallback mBluetoothGattServerCallback = new IBluetoothGattServerCallback.Stub() { // from class: android.bluetooth.BluetoothGattServer.1
        @Override // android.bluetooth.IBluetoothGattServerCallback
        public void onScanResult(String str, int i, byte[] bArr) {
        }

        @Override // android.bluetooth.IBluetoothGattServerCallback
        public void onServerRegistered(int i, int i2) {
            Log.d(BluetoothGattServer.TAG, "onServerRegistered() - status=" + i + " serverIf=" + i2);
            synchronized (BluetoothGattServer.this.mServerIfLock) {
                if (BluetoothGattServer.this.mCallback != null) {
                    BluetoothGattServer.this.mServerIf = i2;
                    BluetoothGattServer.this.mServerIfLock.notify();
                } else {
                    Log.e(BluetoothGattServer.TAG, "onServerRegistered: mCallback is null");
                }
            }
        }

        @Override // android.bluetooth.IBluetoothGattServerCallback
        public void onServerConnectionState(int i, int i2, boolean z, String str) {
            Log.d(BluetoothGattServer.TAG, "onServerConnectionState() - status=" + i + " serverIf=" + i2 + " device=" + str);
            try {
                BluetoothGattServer.this.mCallback.onConnectionStateChange(BluetoothGattServer.this.mAdapter.getRemoteDevice(str), i, z ? 2 : 0);
            } catch (Exception e) {
                Log.w(BluetoothGattServer.TAG, "Unhandled exception in callback", e);
            }
        }

        @Override // android.bluetooth.IBluetoothGattServerCallback
        public void onServiceAdded(int i, int i2, int i3, ParcelUuid parcelUuid) {
            UUID uuid = parcelUuid.getUuid();
            Log.d(BluetoothGattServer.TAG, "onServiceAdded() - service=" + uuid + "status=" + i);
            BluetoothGattService service = BluetoothGattServer.this.getService(uuid, i3, i2);
            if (service == null) {
                return;
            }
            try {
                BluetoothGattServer.this.mCallback.onServiceAdded(i, service);
            } catch (Exception e) {
                Log.w(BluetoothGattServer.TAG, "Unhandled exception in callback", e);
            }
        }

        @Override // android.bluetooth.IBluetoothGattServerCallback
        public void onCharacteristicReadRequest(String str, int i, int i2, boolean z, int i3, int i4, ParcelUuid parcelUuid, int i5, ParcelUuid parcelUuid2) {
            BluetoothGattCharacteristic characteristic;
            UUID uuid = parcelUuid.getUuid();
            UUID uuid2 = parcelUuid2.getUuid();
            BluetoothDevice remoteDevice = BluetoothGattServer.this.mAdapter.getRemoteDevice(str);
            BluetoothGattService service = BluetoothGattServer.this.getService(uuid, i4, i3);
            if (service == null || (characteristic = service.getCharacteristic(uuid2)) == null) {
                return;
            }
            try {
                BluetoothGattServer.this.mCallback.onCharacteristicReadRequest(remoteDevice, i, i2, characteristic);
            } catch (Exception e) {
                Log.w(BluetoothGattServer.TAG, "Unhandled exception in callback", e);
            }
        }

        @Override // android.bluetooth.IBluetoothGattServerCallback
        public void onDescriptorReadRequest(String str, int i, int i2, boolean z, int i3, int i4, ParcelUuid parcelUuid, int i5, ParcelUuid parcelUuid2, ParcelUuid parcelUuid3) {
            BluetoothGattCharacteristic characteristic;
            BluetoothGattDescriptor descriptor;
            UUID uuid = parcelUuid.getUuid();
            UUID uuid2 = parcelUuid2.getUuid();
            UUID uuid3 = parcelUuid3.getUuid();
            BluetoothDevice remoteDevice = BluetoothGattServer.this.mAdapter.getRemoteDevice(str);
            BluetoothGattService service = BluetoothGattServer.this.getService(uuid, i4, i3);
            if (service == null || (characteristic = service.getCharacteristic(uuid2)) == null || (descriptor = characteristic.getDescriptor(uuid3)) == null) {
                return;
            }
            try {
                BluetoothGattServer.this.mCallback.onDescriptorReadRequest(remoteDevice, i, i2, descriptor);
            } catch (Exception e) {
                Log.w(BluetoothGattServer.TAG, "Unhandled exception in callback", e);
            }
        }

        @Override // android.bluetooth.IBluetoothGattServerCallback
        public void onCharacteristicWriteRequest(String str, int i, int i2, int i3, boolean z, boolean z2, int i4, int i5, ParcelUuid parcelUuid, int i6, ParcelUuid parcelUuid2, byte[] bArr) {
            BluetoothGattCharacteristic characteristic;
            UUID uuid = parcelUuid.getUuid();
            UUID uuid2 = parcelUuid2.getUuid();
            BluetoothDevice remoteDevice = BluetoothGattServer.this.mAdapter.getRemoteDevice(str);
            BluetoothGattService service = BluetoothGattServer.this.getService(uuid, i5, i4);
            if (service == null || (characteristic = service.getCharacteristic(uuid2)) == null) {
                return;
            }
            try {
                BluetoothGattServer.this.mCallback.onCharacteristicWriteRequest(remoteDevice, i, characteristic, z, z2, i2, bArr);
            } catch (Exception e) {
                Log.w(BluetoothGattServer.TAG, "Unhandled exception in callback", e);
            }
        }

        @Override // android.bluetooth.IBluetoothGattServerCallback
        public void onDescriptorWriteRequest(String str, int i, int i2, int i3, boolean z, boolean z2, int i4, int i5, ParcelUuid parcelUuid, int i6, ParcelUuid parcelUuid2, ParcelUuid parcelUuid3, byte[] bArr) {
            BluetoothGattCharacteristic characteristic;
            BluetoothGattDescriptor descriptor;
            UUID uuid = parcelUuid.getUuid();
            UUID uuid2 = parcelUuid2.getUuid();
            UUID uuid3 = parcelUuid3.getUuid();
            BluetoothDevice remoteDevice = BluetoothGattServer.this.mAdapter.getRemoteDevice(str);
            BluetoothGattService service = BluetoothGattServer.this.getService(uuid, i5, i4);
            if (service == null || (characteristic = service.getCharacteristic(uuid2)) == null || (descriptor = characteristic.getDescriptor(uuid3)) == null) {
                return;
            }
            try {
                BluetoothGattServer.this.mCallback.onDescriptorWriteRequest(remoteDevice, i, descriptor, z, z2, i2, bArr);
            } catch (Exception e) {
                Log.w(BluetoothGattServer.TAG, "Unhandled exception in callback", e);
            }
        }

        @Override // android.bluetooth.IBluetoothGattServerCallback
        public void onExecuteWrite(String str, int i, boolean z) {
            Log.d(BluetoothGattServer.TAG, "onExecuteWrite() - device=" + str + ", transId=" + i + "execWrite=" + z);
            BluetoothDevice remoteDevice = BluetoothGattServer.this.mAdapter.getRemoteDevice(str);
            if (remoteDevice == null) {
                return;
            }
            try {
                BluetoothGattServer.this.mCallback.onExecuteWrite(remoteDevice, i, z);
            } catch (Exception e) {
                Log.w(BluetoothGattServer.TAG, "Unhandled exception in callback", e);
            }
        }

        @Override // android.bluetooth.IBluetoothGattServerCallback
        public void onNotificationSent(String str, int i) {
            BluetoothDevice remoteDevice = BluetoothGattServer.this.mAdapter.getRemoteDevice(str);
            if (remoteDevice == null) {
                return;
            }
            try {
                BluetoothGattServer.this.mCallback.onNotificationSent(remoteDevice, i);
            } catch (Exception e) {
                Log.w(BluetoothGattServer.TAG, "Unhandled exception: " + e);
            }
        }

        @Override // android.bluetooth.IBluetoothGattServerCallback
        public void onMtuChanged(String str, int i) {
            Log.d(BluetoothGattServer.TAG, "onMtuChanged() - device=" + str + ", mtu=" + i);
            BluetoothDevice remoteDevice = BluetoothGattServer.this.mAdapter.getRemoteDevice(str);
            if (remoteDevice == null) {
                return;
            }
            try {
                BluetoothGattServer.this.mCallback.onMtuChanged(remoteDevice, i);
            } catch (Exception e) {
                Log.w(BluetoothGattServer.TAG, "Unhandled exception: " + e);
            }
        }
    };
    private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothGattServerCallback mCallback = null;
    private int mServerIf = 0;
    private List<BluetoothGattService> mServices = new ArrayList();

    BluetoothGattServer(Context context, IBluetoothGatt iBluetoothGatt, int i) {
        this.mContext = context;
        this.mService = iBluetoothGatt;
        this.mTransport = i;
    }

    public void close() {
        Log.d(TAG, "close()");
        unregisterCallback();
    }

    boolean registerCallback(BluetoothGattServerCallback bluetoothGattServerCallback) {
        Log.d(TAG, "registerCallback()");
        if (this.mService == null) {
            Log.e(TAG, "GATT service not available");
            return false;
        }
        UUID uuidRandomUUID = UUID.randomUUID();
        Log.d(TAG, "registerCallback() - UUID=" + uuidRandomUUID);
        synchronized (this.mServerIfLock) {
            if (this.mCallback != null) {
                Log.e(TAG, "App can register callback only once");
                return false;
            }
            this.mCallback = bluetoothGattServerCallback;
            try {
                this.mService.registerServer(new ParcelUuid(uuidRandomUUID), this.mBluetoothGattServerCallback);
                try {
                    this.mServerIfLock.wait(10000L);
                } catch (InterruptedException e) {
                    Log.e(TAG, "" + e);
                    this.mCallback = null;
                }
                if (this.mServerIf != 0) {
                    return true;
                }
                this.mCallback = null;
                return false;
            } catch (RemoteException e2) {
                Log.e(TAG, "", e2);
                this.mCallback = null;
                return false;
            }
        }
    }

    private void unregisterCallback() {
        int i;
        Log.d(TAG, "unregisterCallback() - mServerIf=" + this.mServerIf);
        IBluetoothGatt iBluetoothGatt = this.mService;
        if (iBluetoothGatt == null || (i = this.mServerIf) == 0) {
            return;
        }
        try {
            this.mCallback = null;
            iBluetoothGatt.unregisterServer(i);
            this.mServerIf = 0;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
    }

    BluetoothGattService getService(UUID uuid, int i, int i2) {
        for (BluetoothGattService bluetoothGattService : this.mServices) {
            if (bluetoothGattService.getType() == i2 && bluetoothGattService.getInstanceId() == i && bluetoothGattService.getUuid().equals(uuid)) {
                return bluetoothGattService;
            }
        }
        return null;
    }

    public boolean connect(BluetoothDevice bluetoothDevice, boolean z) {
        int i;
        Log.d(TAG, "connect() - device: " + bluetoothDevice.getAddress() + ", auto: " + z);
        IBluetoothGatt iBluetoothGatt = this.mService;
        if (iBluetoothGatt != null && (i = this.mServerIf) != 0) {
            try {
                iBluetoothGatt.serverConnect(i, bluetoothDevice.getAddress(), !z, this.mTransport);
                return true;
            } catch (RemoteException e) {
                Log.e(TAG, "", e);
            }
        }
        return false;
    }

    public void cancelConnection(BluetoothDevice bluetoothDevice) {
        int i;
        Log.d(TAG, "cancelConnection() - device: " + bluetoothDevice.getAddress());
        IBluetoothGatt iBluetoothGatt = this.mService;
        if (iBluetoothGatt == null || (i = this.mServerIf) == 0) {
            return;
        }
        try {
            iBluetoothGatt.serverDisconnect(i, bluetoothDevice.getAddress());
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
    }

    public boolean sendResponse(BluetoothDevice bluetoothDevice, int i, int i2, int i3, byte[] bArr) {
        int i4;
        IBluetoothGatt iBluetoothGatt = this.mService;
        if (iBluetoothGatt != null && (i4 = this.mServerIf) != 0) {
            try {
                iBluetoothGatt.sendResponse(i4, bluetoothDevice.getAddress(), i, i2, i3, bArr);
                return true;
            } catch (RemoteException e) {
                Log.e(TAG, "", e);
            }
        }
        return false;
    }

    public boolean notifyCharacteristicChanged(BluetoothDevice bluetoothDevice, BluetoothGattCharacteristic bluetoothGattCharacteristic, boolean z) {
        BluetoothGattService service;
        if (this.mService == null || this.mServerIf == 0 || (service = bluetoothGattCharacteristic.getService()) == null) {
            return false;
        }
        if (bluetoothGattCharacteristic.getValue() == null) {
            throw new IllegalArgumentException("Chracteristic value is empty. Use BluetoothGattCharacteristic#setvalue to update");
        }
        try {
            this.mService.sendNotification(this.mServerIf, bluetoothDevice.getAddress(), service.getType(), service.getInstanceId(), new ParcelUuid(service.getUuid()), bluetoothGattCharacteristic.getInstanceId(), new ParcelUuid(bluetoothGattCharacteristic.getUuid()), z, bluetoothGattCharacteristic.getValue());
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean addService(BluetoothGattService bluetoothGattService) {
        Log.d(TAG, "addService() - service: " + bluetoothGattService.getUuid());
        if (this.mService != null && this.mServerIf != 0) {
            this.mServices.add(bluetoothGattService);
            try {
                this.mService.beginServiceDeclaration(this.mServerIf, bluetoothGattService.getType(), bluetoothGattService.getInstanceId(), bluetoothGattService.getHandles(), new ParcelUuid(bluetoothGattService.getUuid()), bluetoothGattService.isAdvertisePreferred());
                for (BluetoothGattService bluetoothGattService2 : bluetoothGattService.getIncludedServices()) {
                    this.mService.addIncludedService(this.mServerIf, bluetoothGattService2.getType(), bluetoothGattService2.getInstanceId(), new ParcelUuid(bluetoothGattService2.getUuid()));
                }
                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
                    this.mService.addCharacteristic(this.mServerIf, new ParcelUuid(bluetoothGattCharacteristic.getUuid()), bluetoothGattCharacteristic.getProperties(), ((bluetoothGattCharacteristic.getKeySize() - 7) << 12) + bluetoothGattCharacteristic.getPermissions());
                    for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattCharacteristic.getDescriptors()) {
                        this.mService.addDescriptor(this.mServerIf, new ParcelUuid(bluetoothGattDescriptor.getUuid()), ((bluetoothGattCharacteristic.getKeySize() - 7) << 12) + bluetoothGattDescriptor.getPermissions());
                    }
                }
                this.mService.endServiceDeclaration(this.mServerIf);
                return true;
            } catch (RemoteException e) {
                Log.e(TAG, "", e);
            }
        }
        return false;
    }

    public boolean removeService(BluetoothGattService bluetoothGattService) {
        BluetoothGattService service;
        Log.d(TAG, "removeService() - service: " + bluetoothGattService.getUuid());
        if (this.mService == null || this.mServerIf == 0 || (service = getService(bluetoothGattService.getUuid(), bluetoothGattService.getInstanceId(), bluetoothGattService.getType())) == null) {
            return false;
        }
        try {
            this.mService.removeService(this.mServerIf, bluetoothGattService.getType(), bluetoothGattService.getInstanceId(), new ParcelUuid(bluetoothGattService.getUuid()));
            this.mServices.remove(service);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public void clearServices() {
        int i;
        Log.d(TAG, "clearServices()");
        IBluetoothGatt iBluetoothGatt = this.mService;
        if (iBluetoothGatt == null || (i = this.mServerIf) == 0) {
            return;
        }
        try {
            iBluetoothGatt.clearServices(i);
            this.mServices.clear();
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
    }

    public List<BluetoothGattService> getServices() {
        return this.mServices;
    }

    public BluetoothGattService getService(UUID uuid) {
        for (BluetoothGattService bluetoothGattService : this.mServices) {
            if (bluetoothGattService.getUuid().equals(uuid)) {
                return bluetoothGattService;
            }
        }
        return null;
    }

    @Override // android.bluetooth.BluetoothProfile
    public int getConnectionState(BluetoothDevice bluetoothDevice) {
        throw new UnsupportedOperationException("Use BluetoothManager#getConnectionState instead.");
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getConnectedDevices() {
        throw new UnsupportedOperationException("Use BluetoothManager#getConnectedDevices instead.");
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] iArr) {
        throw new UnsupportedOperationException("Use BluetoothManager#getDevicesMatchingConnectionStates instead.");
    }
}
