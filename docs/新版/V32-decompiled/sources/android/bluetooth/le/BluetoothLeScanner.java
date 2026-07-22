package android.bluetooth.le;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCallbackWrapper;
import android.bluetooth.IBluetoothGatt;
import android.bluetooth.IBluetoothManager;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes.dex */
public final class BluetoothLeScanner {
    private static final boolean DBG = true;
    private static final String TAG = "BluetoothLeScanner";
    private static final boolean VDBG = false;
    private final IBluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Map<ScanCallback, BleScanCallbackWrapper> mLeScanClients = new HashMap();

    public BluetoothLeScanner(IBluetoothManager iBluetoothManager) {
        this.mBluetoothManager = iBluetoothManager;
    }

    public void startScan(ScanCallback scanCallback) {
        if (scanCallback == null) {
            throw new IllegalArgumentException("callback is null");
        }
        startScan(null, new ScanSettings.Builder().build(), scanCallback);
    }

    public void startScan(List<ScanFilter> list, ScanSettings scanSettings, ScanCallback scanCallback) {
        startScan(list, scanSettings, scanCallback, null);
    }

    private void startScan(List<ScanFilter> list, ScanSettings scanSettings, ScanCallback scanCallback, List<List<ResultStorageDescriptor>> list2) {
        IBluetoothGatt bluetoothGatt;
        BluetoothLeUtils.checkAdapterStateOn(this.mBluetoothAdapter);
        if (scanSettings == null || scanCallback == null) {
            throw new IllegalArgumentException("settings or callback is null");
        }
        synchronized (this.mLeScanClients) {
            if (this.mLeScanClients.containsKey(scanCallback)) {
                postCallbackError(scanCallback, 1);
                return;
            }
            try {
                bluetoothGatt = this.mBluetoothManager.getBluetoothGatt();
            } catch (RemoteException unused) {
                bluetoothGatt = null;
            }
            IBluetoothGatt iBluetoothGatt = bluetoothGatt;
            if (iBluetoothGatt == null) {
                postCallbackError(scanCallback, 3);
            } else if (!isSettingsConfigAllowedForScan(scanSettings)) {
                postCallbackError(scanCallback, 4);
            } else {
                new BleScanCallbackWrapper(iBluetoothGatt, list, scanSettings, scanCallback, list2).startRegisteration();
            }
        }
    }

    public void stopScan(ScanCallback scanCallback) {
        BluetoothLeUtils.checkAdapterStateOn(this.mBluetoothAdapter);
        synchronized (this.mLeScanClients) {
            BleScanCallbackWrapper bleScanCallbackWrapperRemove = this.mLeScanClients.remove(scanCallback);
            if (bleScanCallbackWrapperRemove == null) {
                Log.d(TAG, "could not find callback wrapper");
            } else {
                bleScanCallbackWrapperRemove.stopLeScan();
            }
        }
    }

    public void flushPendingScanResults(ScanCallback scanCallback) {
        BluetoothLeUtils.checkAdapterStateOn(this.mBluetoothAdapter);
        if (scanCallback == null) {
            throw new IllegalArgumentException("callback cannot be null!");
        }
        synchronized (this.mLeScanClients) {
            BleScanCallbackWrapper bleScanCallbackWrapper = this.mLeScanClients.get(scanCallback);
            if (bleScanCallbackWrapper == null) {
                return;
            }
            bleScanCallbackWrapper.flushPendingBatchResults();
        }
    }

    public void startTruncatedScan(List<TruncatedFilter> list, ScanSettings scanSettings, ScanCallback scanCallback) {
        int size = list.size();
        ArrayList arrayList = new ArrayList(size);
        ArrayList arrayList2 = new ArrayList(size);
        for (TruncatedFilter truncatedFilter : list) {
            arrayList.add(truncatedFilter.getFilter());
            arrayList2.add(truncatedFilter.getStorageDescriptors());
        }
        startScan(arrayList, scanSettings, scanCallback, arrayList2);
    }

    public void cleanup() {
        this.mLeScanClients.clear();
    }

    private class BleScanCallbackWrapper extends BluetoothGattCallbackWrapper {
        private static final int REGISTRATION_CALLBACK_TIMEOUT_MILLIS = 2000;
        private IBluetoothGatt mBluetoothGatt;
        private int mClientIf = 0;
        private final List<ScanFilter> mFilters;
        private List<List<ResultStorageDescriptor>> mResultStorages;
        private final ScanCallback mScanCallback;
        private ScanSettings mSettings;

        public BleScanCallbackWrapper(IBluetoothGatt iBluetoothGatt, List<ScanFilter> list, ScanSettings scanSettings, ScanCallback scanCallback, List<List<ResultStorageDescriptor>> list2) {
            this.mBluetoothGatt = iBluetoothGatt;
            this.mFilters = list;
            this.mSettings = scanSettings;
            this.mScanCallback = scanCallback;
            this.mResultStorages = list2;
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
                    Log.e(BluetoothLeScanner.TAG, "application registeration exception", e);
                    BluetoothLeScanner.this.postCallbackError(this.mScanCallback, 3);
                } catch (InterruptedException e2) {
                    Log.e(BluetoothLeScanner.TAG, "application registeration exception", e2);
                    BluetoothLeScanner.this.postCallbackError(this.mScanCallback, 3);
                }
                if (this.mClientIf > 0) {
                    BluetoothLeScanner.this.mLeScanClients.put(this.mScanCallback, this);
                } else {
                    BluetoothLeScanner.this.postCallbackError(this.mScanCallback, 2);
                }
            }
        }

        public void stopLeScan() {
            synchronized (this) {
                int i = this.mClientIf;
                if (i <= 0) {
                    Log.e(BluetoothLeScanner.TAG, "Error state, mLeHandle: " + this.mClientIf);
                    return;
                }
                try {
                    this.mBluetoothGatt.stopScan(i, false);
                    this.mBluetoothGatt.unregisterClient(this.mClientIf);
                } catch (RemoteException e) {
                    Log.e(BluetoothLeScanner.TAG, "Failed to stop scan and unregister", e);
                }
                this.mClientIf = -1;
            }
        }

        void flushPendingBatchResults() {
            synchronized (this) {
                int i = this.mClientIf;
                if (i <= 0) {
                    Log.e(BluetoothLeScanner.TAG, "Error state, mLeHandle: " + this.mClientIf);
                    return;
                }
                try {
                    this.mBluetoothGatt.flushPendingBatchResults(i, false);
                } catch (RemoteException e) {
                    Log.e(BluetoothLeScanner.TAG, "Failed to get pending scan results", e);
                }
            }
        }

        @Override // android.bluetooth.BluetoothGattCallbackWrapper, android.bluetooth.IBluetoothGattCallback
        public void onClientRegistered(int i, int i2) {
            Log.d(BluetoothLeScanner.TAG, "onClientRegistered() - status=" + i + " clientIf=" + i2);
            synchronized (this) {
                if (this.mClientIf == -1) {
                    Log.d(BluetoothLeScanner.TAG, "onClientRegistered LE scan canceled");
                }
                if (i == 0) {
                    this.mClientIf = i2;
                    try {
                        this.mBluetoothGatt.startScan(i2, false, this.mSettings, this.mFilters, this.mResultStorages);
                    } catch (RemoteException e) {
                        Log.e(BluetoothLeScanner.TAG, "fail to start le scan: " + e);
                        this.mClientIf = -1;
                    }
                } else {
                    this.mClientIf = -1;
                }
                notifyAll();
            }
        }

        @Override // android.bluetooth.BluetoothGattCallbackWrapper, android.bluetooth.IBluetoothGattCallback
        public void onScanResult(final ScanResult scanResult) {
            synchronized (this) {
                if (this.mClientIf <= 0) {
                    return;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeScanner.BleScanCallbackWrapper.1
                    @Override // java.lang.Runnable
                    public void run() {
                        BleScanCallbackWrapper.this.mScanCallback.onScanResult(1, scanResult);
                    }
                });
            }
        }

        @Override // android.bluetooth.BluetoothGattCallbackWrapper, android.bluetooth.IBluetoothGattCallback
        public void onBatchScanResults(final List<ScanResult> list) {
            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeScanner.BleScanCallbackWrapper.2
                @Override // java.lang.Runnable
                public void run() {
                    BleScanCallbackWrapper.this.mScanCallback.onBatchScanResults(list);
                }
            });
        }

        @Override // android.bluetooth.BluetoothGattCallbackWrapper, android.bluetooth.IBluetoothGattCallback
        public void onFoundOrLost(final boolean z, final ScanResult scanResult) {
            synchronized (this) {
                if (this.mClientIf <= 0) {
                    return;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeScanner.BleScanCallbackWrapper.3
                    @Override // java.lang.Runnable
                    public void run() {
                        if (z) {
                            BleScanCallbackWrapper.this.mScanCallback.onScanResult(2, scanResult);
                        } else {
                            BleScanCallbackWrapper.this.mScanCallback.onScanResult(4, scanResult);
                        }
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postCallbackError(final ScanCallback scanCallback, final int i) {
        this.mHandler.post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeScanner.1
            @Override // java.lang.Runnable
            public void run() {
                scanCallback.onScanFailed(i);
            }
        });
    }

    private boolean isSettingsConfigAllowedForScan(ScanSettings scanSettings) {
        if (this.mBluetoothAdapter.isOffloadedFilteringSupported()) {
            return true;
        }
        return scanSettings.getCallbackType() == 1 && scanSettings.getReportDelayMillis() == 0;
    }
}
