package android.bluetooth.le;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCallbackWrapper;
import android.bluetooth.BluetoothUuid;
import android.bluetooth.IBluetoothGatt;
import android.bluetooth.IBluetoothManager;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes.dex */
public final class BluetoothLeAdvertiser {
    private static final int FLAGS_FIELD_BYTES = 3;
    private static final int MANUFACTURER_SPECIFIC_DATA_LENGTH = 2;
    private static final int MAX_ADVERTISING_DATA_BYTES = 31;
    private static final int OVERHEAD_BYTES_PER_FIELD = 2;
    private static final int SERVICE_DATA_UUID_LENGTH = 2;
    private static final String TAG = "BluetoothLeAdvertiser";
    private final IBluetoothManager mBluetoothManager;
    private final Map<AdvertiseCallback, AdvertiseCallbackWrapper> mLeAdvertisers = new HashMap();
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public BluetoothLeAdvertiser(IBluetoothManager iBluetoothManager) {
        this.mBluetoothManager = iBluetoothManager;
    }

    public void startAdvertising(AdvertiseSettings advertiseSettings, AdvertiseData advertiseData, AdvertiseCallback advertiseCallback) {
        startAdvertising(advertiseSettings, advertiseData, null, advertiseCallback);
    }

    public void startAdvertising(AdvertiseSettings advertiseSettings, AdvertiseData advertiseData, AdvertiseData advertiseData2, AdvertiseCallback advertiseCallback) {
        synchronized (this.mLeAdvertisers) {
            BluetoothLeUtils.checkAdapterStateOn(this.mBluetoothAdapter);
            if (advertiseCallback == null) {
                throw new IllegalArgumentException("callback cannot be null");
            }
            if (!this.mBluetoothAdapter.isMultipleAdvertisementSupported() && !this.mBluetoothAdapter.isPeripheralModeSupported()) {
                postStartFailure(advertiseCallback, 5);
                return;
            }
            if (totalBytes(advertiseData, advertiseSettings.isConnectable()) <= 31 && totalBytes(advertiseData2, false) <= 31) {
                if (this.mLeAdvertisers.containsKey(advertiseCallback)) {
                    postStartFailure(advertiseCallback, 3);
                    return;
                }
                try {
                    new AdvertiseCallbackWrapper(advertiseCallback, advertiseData, advertiseData2, advertiseSettings, this.mBluetoothManager.getBluetoothGatt()).startRegisteration();
                    return;
                } catch (RemoteException e) {
                    Log.e(TAG, "Failed to get Bluetooth gatt - ", e);
                    postStartFailure(advertiseCallback, 4);
                    return;
                }
            }
            postStartFailure(advertiseCallback, 1);
        }
    }

    public void stopAdvertising(AdvertiseCallback advertiseCallback) {
        synchronized (this.mLeAdvertisers) {
            BluetoothLeUtils.checkAdapterStateOn(this.mBluetoothAdapter);
            if (advertiseCallback == null) {
                throw new IllegalArgumentException("callback cannot be null");
            }
            AdvertiseCallbackWrapper advertiseCallbackWrapper = this.mLeAdvertisers.get(advertiseCallback);
            if (advertiseCallbackWrapper == null) {
                return;
            }
            advertiseCallbackWrapper.stopAdvertising();
        }
    }

    public void cleanup() {
        this.mLeAdvertisers.clear();
    }

    private int totalBytes(AdvertiseData advertiseData, boolean z) {
        if (advertiseData == null) {
            return 0;
        }
        int iByteLength = z ? 3 : 0;
        if (advertiseData.getServiceUuids() != null) {
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            for (ParcelUuid parcelUuid : advertiseData.getServiceUuids()) {
                if (BluetoothUuid.is16BitUuid(parcelUuid)) {
                    i++;
                } else if (BluetoothUuid.is32BitUuid(parcelUuid)) {
                    i2++;
                } else {
                    i3++;
                }
            }
            if (i != 0) {
                iByteLength += (i * 2) + 2;
            }
            if (i2 != 0) {
                iByteLength += (i2 * 4) + 2;
            }
            if (i3 != 0) {
                iByteLength += (i3 * 16) + 2;
            }
        }
        Iterator<ParcelUuid> it = advertiseData.getServiceData().keySet().iterator();
        while (it.hasNext()) {
            iByteLength += byteLength(advertiseData.getServiceData().get(it.next())) + 4;
        }
        for (int i4 = 0; i4 < advertiseData.getManufacturerSpecificData().size(); i4++) {
            iByteLength += byteLength(advertiseData.getManufacturerSpecificData().valueAt(i4)) + 4;
        }
        if (advertiseData.getIncludeTxPowerLevel()) {
            iByteLength += 3;
        }
        return (!advertiseData.getIncludeDeviceName() || this.mBluetoothAdapter.getName() == null) ? iByteLength : iByteLength + this.mBluetoothAdapter.getName().length() + 2;
    }

    private int byteLength(byte[] bArr) {
        if (bArr == null) {
            return 0;
        }
        return bArr.length;
    }

    private class AdvertiseCallbackWrapper extends BluetoothGattCallbackWrapper {
        private static final int LE_CALLBACK_TIMEOUT_MILLIS = 2000;
        private final AdvertiseCallback mAdvertiseCallback;
        private final AdvertiseData mAdvertisement;
        private final IBluetoothGatt mBluetoothGatt;
        private final AdvertiseData mScanResponse;
        private final AdvertiseSettings mSettings;
        private boolean mIsAdvertising = false;
        private int mClientIf = 0;

        public AdvertiseCallbackWrapper(AdvertiseCallback advertiseCallback, AdvertiseData advertiseData, AdvertiseData advertiseData2, AdvertiseSettings advertiseSettings, IBluetoothGatt iBluetoothGatt) {
            this.mAdvertiseCallback = advertiseCallback;
            this.mAdvertisement = advertiseData;
            this.mScanResponse = advertiseData2;
            this.mSettings = advertiseSettings;
            this.mBluetoothGatt = iBluetoothGatt;
        }

        public void startRegisteration() {
            synchronized (this) {
                if (this.mClientIf == -1) {
                    return;
                }
                try {
                    this.mBluetoothGatt.registerClient(new ParcelUuid(UUID.randomUUID()), this);
                    wait(FileUtils.FAT_FILE_TIMESTAMP_GRANULARITY);
                } catch (RemoteException e) {
                    Log.e(BluetoothLeAdvertiser.TAG, "Failed to start registeration", e);
                } catch (InterruptedException e2) {
                    Log.e(BluetoothLeAdvertiser.TAG, "Failed to start registeration", e2);
                }
                int i = this.mClientIf;
                if (i > 0 && this.mIsAdvertising) {
                    BluetoothLeAdvertiser.this.mLeAdvertisers.put(this.mAdvertiseCallback, this);
                } else if (i <= 0) {
                    BluetoothLeAdvertiser.this.postStartFailure(this.mAdvertiseCallback, 4);
                } else {
                    try {
                        this.mBluetoothGatt.unregisterClient(i);
                        this.mClientIf = -1;
                    } catch (RemoteException e3) {
                        Log.e(BluetoothLeAdvertiser.TAG, "remote exception when unregistering", e3);
                    }
                }
            }
        }

        public void stopAdvertising() {
            synchronized (this) {
                try {
                    this.mBluetoothGatt.stopMultiAdvertising(this.mClientIf);
                    wait(FileUtils.FAT_FILE_TIMESTAMP_GRANULARITY);
                } catch (RemoteException e) {
                    Log.e(BluetoothLeAdvertiser.TAG, "Failed to stop advertising", e);
                } catch (InterruptedException e2) {
                    Log.e(BluetoothLeAdvertiser.TAG, "Failed to stop advertising", e2);
                }
                if (BluetoothLeAdvertiser.this.mLeAdvertisers.containsKey(this.mAdvertiseCallback)) {
                    BluetoothLeAdvertiser.this.mLeAdvertisers.remove(this.mAdvertiseCallback);
                }
            }
        }

        @Override // android.bluetooth.BluetoothGattCallbackWrapper, android.bluetooth.IBluetoothGattCallback
        public void onClientRegistered(int i, int i2) {
            Log.d(BluetoothLeAdvertiser.TAG, "onClientRegistered() - status=" + i + " clientIf=" + i2);
            synchronized (this) {
                if (i == 0) {
                    this.mClientIf = i2;
                    try {
                        this.mBluetoothGatt.startMultiAdvertising(i2, this.mAdvertisement, this.mScanResponse, this.mSettings);
                        return;
                    } catch (RemoteException e) {
                        Log.e(BluetoothLeAdvertiser.TAG, "failed to start advertising", e);
                    }
                }
                this.mClientIf = -1;
                notifyAll();
            }
        }

        @Override // android.bluetooth.BluetoothGattCallbackWrapper, android.bluetooth.IBluetoothGattCallback
        public void onMultiAdvertiseCallback(int i, boolean z, AdvertiseSettings advertiseSettings) {
            synchronized (this) {
                if (!z) {
                    try {
                        this.mBluetoothGatt.unregisterClient(this.mClientIf);
                        this.mClientIf = -1;
                        this.mIsAdvertising = false;
                        BluetoothLeAdvertiser.this.mLeAdvertisers.remove(this.mAdvertiseCallback);
                    } catch (RemoteException e) {
                        Log.e(BluetoothLeAdvertiser.TAG, "remote exception when unregistering", e);
                    }
                } else if (i != 0) {
                    BluetoothLeAdvertiser.this.postStartFailure(this.mAdvertiseCallback, i);
                } else {
                    this.mIsAdvertising = true;
                    BluetoothLeAdvertiser.this.postStartSuccess(this.mAdvertiseCallback, advertiseSettings);
                }
                notifyAll();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postStartFailure(final AdvertiseCallback advertiseCallback, final int i) {
        this.mHandler.post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeAdvertiser.1
            @Override // java.lang.Runnable
            public void run() {
                advertiseCallback.onStartFailure(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postStartSuccess(final AdvertiseCallback advertiseCallback, final AdvertiseSettings advertiseSettings) {
        this.mHandler.post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeAdvertiser.2
            @Override // java.lang.Runnable
            public void run() {
                advertiseCallback.onStartSuccess(advertiseSettings);
            }
        });
    }
}
