package android.bluetooth;

import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothManager;
import android.bluetooth.IBluetoothManagerCallback;
import android.bluetooth.IBluetoothStateChangeCallback;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.util.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public final class BluetoothAdapter {
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED";
    public static final String ACTION_DISCOVERY_FINISHED = "android.bluetooth.adapter.action.DISCOVERY_FINISHED";
    public static final String ACTION_DISCOVERY_STARTED = "android.bluetooth.adapter.action.DISCOVERY_STARTED";
    public static final String ACTION_LOCAL_NAME_CHANGED = "android.bluetooth.adapter.action.LOCAL_NAME_CHANGED";
    public static final String ACTION_REQUEST_DISCOVERABLE = "android.bluetooth.adapter.action.REQUEST_DISCOVERABLE";
    public static final String ACTION_REQUEST_ENABLE = "android.bluetooth.adapter.action.REQUEST_ENABLE";
    public static final String ACTION_SCAN_MODE_CHANGED = "android.bluetooth.adapter.action.SCAN_MODE_CHANGED";
    public static final String ACTION_STATE_CHANGED = "android.bluetooth.adapter.action.STATE_CHANGED";
    public static final int ACTIVITY_ENERGY_INFO_CACHED = 0;
    public static final int ACTIVITY_ENERGY_INFO_REFRESHED = 1;
    private static final int ADDRESS_LENGTH = 17;
    public static final String BLUETOOTH_MANAGER_SERVICE = "bluetooth_manager";
    private static final int CONTROLLER_ENERGY_UPDATE_TIMEOUT_MILLIS = 30;
    private static final boolean DBG = true;
    public static final int ERROR = Integer.MIN_VALUE;
    public static final String EXTRA_CONNECTION_STATE = "android.bluetooth.adapter.extra.CONNECTION_STATE";
    public static final String EXTRA_DISCOVERABLE_DURATION = "android.bluetooth.adapter.extra.DISCOVERABLE_DURATION";
    public static final String EXTRA_LOCAL_NAME = "android.bluetooth.adapter.extra.LOCAL_NAME";
    public static final String EXTRA_PREVIOUS_CONNECTION_STATE = "android.bluetooth.adapter.extra.PREVIOUS_CONNECTION_STATE";
    public static final String EXTRA_PREVIOUS_SCAN_MODE = "android.bluetooth.adapter.extra.PREVIOUS_SCAN_MODE";
    public static final String EXTRA_PREVIOUS_STATE = "android.bluetooth.adapter.extra.PREVIOUS_STATE";
    public static final String EXTRA_SCAN_MODE = "android.bluetooth.adapter.extra.SCAN_MODE";
    public static final String EXTRA_STATE = "android.bluetooth.adapter.extra.STATE";
    public static final int SCAN_MODE_CONNECTABLE = 21;
    public static final int SCAN_MODE_CONNECTABLE_DISCOVERABLE = 23;
    public static final int SCAN_MODE_NONE = 20;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_DISCONNECTING = 3;
    public static final int STATE_OFF = 10;
    public static final int STATE_ON = 12;
    public static final int STATE_TURNING_OFF = 13;
    public static final int STATE_TURNING_ON = 11;
    private static final String TAG = "BluetoothAdapter";
    private static final boolean VDBG = false;
    private static BluetoothAdapter sAdapter;
    private static BluetoothLeAdvertiser sBluetoothLeAdvertiser;
    private static BluetoothLeScanner sBluetoothLeScanner;
    private final Map<LeScanCallback, ScanCallback> mLeScanClients;
    private final Object mLock = new Object();
    private final IBluetoothManagerCallback mManagerCallback;
    private final IBluetoothManager mManagerService;
    private final ArrayList<IBluetoothManagerCallback> mProxyServiceStateCallbacks;
    private IBluetooth mService;

    public interface BluetoothStateChangeCallback {
        void onBluetoothStateChange(boolean z);
    }

    public interface LeScanCallback {
        void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bArr);
    }

    public boolean changeApplicationBluetoothState(boolean z, BluetoothStateChangeCallback bluetoothStateChangeCallback) {
        if (bluetoothStateChangeCallback == null) {
        }
        return false;
    }

    public static synchronized BluetoothAdapter getDefaultAdapter() {
        if (sAdapter == null) {
            IBinder service = ServiceManager.getService(BLUETOOTH_MANAGER_SERVICE);
            if (service != null) {
                sAdapter = new BluetoothAdapter(IBluetoothManager.Stub.asInterface(service));
            } else {
                Log.e(TAG, "Bluetooth binder is null");
            }
        }
        return sAdapter;
    }

    BluetoothAdapter(IBluetoothManager iBluetoothManager) {
        IBluetoothManagerCallback.Stub stub = new IBluetoothManagerCallback.Stub() { // from class: android.bluetooth.BluetoothAdapter.1
            @Override // android.bluetooth.IBluetoothManagerCallback
            public void onBluetoothServiceUp(IBluetooth iBluetooth) {
                synchronized (BluetoothAdapter.this.mManagerCallback) {
                    BluetoothAdapter.this.mService = iBluetooth;
                    synchronized (BluetoothAdapter.this.mProxyServiceStateCallbacks) {
                        for (IBluetoothManagerCallback iBluetoothManagerCallback : BluetoothAdapter.this.mProxyServiceStateCallbacks) {
                            if (iBluetoothManagerCallback != null) {
                                try {
                                    iBluetoothManagerCallback.onBluetoothServiceUp(iBluetooth);
                                } catch (Exception e) {
                                    Log.e(BluetoothAdapter.TAG, "", e);
                                }
                            } else {
                                Log.d(BluetoothAdapter.TAG, "onBluetoothServiceUp: cb is null!!!");
                            }
                        }
                    }
                }
            }

            @Override // android.bluetooth.IBluetoothManagerCallback
            public void onBluetoothServiceDown() {
                synchronized (BluetoothAdapter.this.mManagerCallback) {
                    BluetoothAdapter.this.mService = null;
                    if (BluetoothAdapter.this.mLeScanClients != null) {
                        BluetoothAdapter.this.mLeScanClients.clear();
                    }
                    if (BluetoothAdapter.sBluetoothLeAdvertiser != null) {
                        BluetoothAdapter.sBluetoothLeAdvertiser.cleanup();
                    }
                    if (BluetoothAdapter.sBluetoothLeScanner != null) {
                        BluetoothAdapter.sBluetoothLeScanner.cleanup();
                    }
                    synchronized (BluetoothAdapter.this.mProxyServiceStateCallbacks) {
                        for (IBluetoothManagerCallback iBluetoothManagerCallback : BluetoothAdapter.this.mProxyServiceStateCallbacks) {
                            if (iBluetoothManagerCallback != null) {
                                try {
                                    iBluetoothManagerCallback.onBluetoothServiceDown();
                                } catch (Exception e) {
                                    Log.e(BluetoothAdapter.TAG, "", e);
                                }
                            } else {
                                Log.d(BluetoothAdapter.TAG, "onBluetoothServiceDown: cb is null!!!");
                            }
                        }
                    }
                }
            }
        };
        this.mManagerCallback = stub;
        this.mProxyServiceStateCallbacks = new ArrayList<>();
        if (iBluetoothManager == null) {
            throw new IllegalArgumentException("bluetooth manager service is null");
        }
        try {
            this.mService = iBluetoothManager.registerAdapter(stub);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
        this.mManagerService = iBluetoothManager;
        this.mLeScanClients = new HashMap();
    }

    public BluetoothDevice getRemoteDevice(String str) {
        return new BluetoothDevice(str);
    }

    public BluetoothDevice getRemoteDevice(byte[] bArr) {
        if (bArr == null || bArr.length != 6) {
            throw new IllegalArgumentException("Bluetooth address must have 6 bytes");
        }
        return new BluetoothDevice(String.format(Locale.US, "%02X:%02X:%02X:%02X:%02X:%02X", Byte.valueOf(bArr[0]), Byte.valueOf(bArr[1]), Byte.valueOf(bArr[2]), Byte.valueOf(bArr[3]), Byte.valueOf(bArr[4]), Byte.valueOf(bArr[5])));
    }

    public BluetoothLeAdvertiser getBluetoothLeAdvertiser() {
        if (getState() != 12) {
            return null;
        }
        if (!isMultipleAdvertisementSupported() && !isPeripheralModeSupported()) {
            Log.e(TAG, "bluetooth le advertising not supported");
            return null;
        }
        synchronized (this.mLock) {
            if (sBluetoothLeAdvertiser == null) {
                sBluetoothLeAdvertiser = new BluetoothLeAdvertiser(this.mManagerService);
            }
        }
        return sBluetoothLeAdvertiser;
    }

    public BluetoothLeScanner getBluetoothLeScanner() {
        if (getState() != 12) {
            return null;
        }
        synchronized (this.mLock) {
            if (sBluetoothLeScanner == null) {
                sBluetoothLeScanner = new BluetoothLeScanner(this.mManagerService);
            }
        }
        return sBluetoothLeScanner;
    }

    public boolean isEnabled() {
        try {
            synchronized (this.mManagerCallback) {
                IBluetooth iBluetooth = this.mService;
                if (iBluetooth == null) {
                    return false;
                }
                return iBluetooth.isEnabled();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public int getState() {
        try {
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
        synchronized (this.mManagerCallback) {
            IBluetooth iBluetooth = this.mService;
            if (iBluetooth != null) {
                return iBluetooth.getState();
            }
            Log.d(TAG, "" + hashCode() + ": getState() :  mService = null. Returning STATE_OFF");
            return 10;
        }
    }

    public boolean enable() {
        if (isEnabled()) {
            Log.d(TAG, "enable(): BT is already enabled..!");
            return true;
        }
        try {
            return this.mManagerService.enable();
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean disable() {
        try {
            return this.mManagerService.disable(true);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean disable(boolean z) {
        try {
            return this.mManagerService.disable(z);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public String getAddress() {
        try {
            return this.mManagerService.getAddress();
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return null;
        }
    }

    public String getName() {
        try {
            return this.mManagerService.getName();
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return null;
        }
    }

    public boolean configHciSnoopLog(boolean z) {
        try {
            synchronized (this.mManagerCallback) {
                IBluetooth iBluetooth = this.mService;
                if (iBluetooth == null) {
                    return false;
                }
                return iBluetooth.configHciSnoopLog(z);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public ParcelUuid[] getUuids() {
        if (getState() != 12) {
            return null;
        }
        try {
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
        synchronized (this.mManagerCallback) {
            IBluetooth iBluetooth = this.mService;
            if (iBluetooth == null) {
                return null;
            }
            return iBluetooth.getUuids();
        }
    }

    public boolean setName(String str) {
        if (getState() != 12) {
            return false;
        }
        try {
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
        synchronized (this.mManagerCallback) {
            IBluetooth iBluetooth = this.mService;
            if (iBluetooth == null) {
                return false;
            }
            return iBluetooth.setName(str);
        }
    }

    public int getScanMode() {
        if (getState() != 12) {
            return 20;
        }
        try {
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
        synchronized (this.mManagerCallback) {
            IBluetooth iBluetooth = this.mService;
            if (iBluetooth == null) {
                return 20;
            }
            return iBluetooth.getScanMode();
        }
    }

    public boolean setScanMode(int i, int i2) {
        if (getState() != 12) {
            return false;
        }
        try {
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
        synchronized (this.mManagerCallback) {
            IBluetooth iBluetooth = this.mService;
            if (iBluetooth == null) {
                return false;
            }
            return iBluetooth.setScanMode(i, i2);
        }
    }

    public boolean setScanMode(int i) {
        if (getState() != 12) {
            return false;
        }
        return setScanMode(i, getDiscoverableTimeout());
    }

    public int getDiscoverableTimeout() {
        if (getState() != 12) {
            return -1;
        }
        try {
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
        synchronized (this.mManagerCallback) {
            IBluetooth iBluetooth = this.mService;
            if (iBluetooth == null) {
                return -1;
            }
            return iBluetooth.getDiscoverableTimeout();
        }
    }

    public void setDiscoverableTimeout(int i) {
        if (getState() != 12) {
            return;
        }
        try {
            synchronized (this.mManagerCallback) {
                IBluetooth iBluetooth = this.mService;
                if (iBluetooth != null) {
                    iBluetooth.setDiscoverableTimeout(i);
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
    }

    public boolean startDiscovery() {
        if (getState() != 12) {
            return false;
        }
        try {
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
        synchronized (this.mManagerCallback) {
            IBluetooth iBluetooth = this.mService;
            if (iBluetooth == null) {
                return false;
            }
            return iBluetooth.startDiscovery();
        }
    }

    public boolean cancelDiscovery() {
        if (getState() != 12) {
            return false;
        }
        try {
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
        synchronized (this.mManagerCallback) {
            IBluetooth iBluetooth = this.mService;
            if (iBluetooth == null) {
                return false;
            }
            return iBluetooth.cancelDiscovery();
        }
    }

    public boolean isDiscovering() {
        if (getState() != 12) {
            return false;
        }
        try {
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
        synchronized (this.mManagerCallback) {
            IBluetooth iBluetooth = this.mService;
            if (iBluetooth == null) {
                return false;
            }
            return iBluetooth.isDiscovering();
        }
    }

    public boolean isMultipleAdvertisementSupported() {
        if (getState() != 12) {
            return false;
        }
        try {
            return this.mService.isMultiAdvertisementSupported();
        } catch (RemoteException e) {
            Log.e(TAG, "failed to get isMultipleAdvertisementSupported, error: ", e);
            return false;
        }
    }

    public boolean isPeripheralModeSupported() {
        if (getState() != 12) {
            return false;
        }
        try {
            return this.mService.isPeripheralModeSupported();
        } catch (RemoteException e) {
            Log.e(TAG, "failed to get peripheral mode capability: ", e);
            return false;
        }
    }

    public boolean isOffloadedFilteringSupported() {
        if (getState() != 12) {
            return false;
        }
        try {
            return this.mService.isOffloadedFilteringSupported();
        } catch (RemoteException e) {
            Log.e(TAG, "failed to get isOffloadedFilteringSupported, error: ", e);
            return false;
        }
    }

    public boolean isOffloadedScanBatchingSupported() {
        if (getState() != 12) {
            return false;
        }
        try {
            return this.mService.isOffloadedScanBatchingSupported();
        } catch (RemoteException e) {
            Log.e(TAG, "failed to get isOffloadedScanBatchingSupported, error: ", e);
            return false;
        }
    }

    public BluetoothActivityEnergyInfo getControllerActivityEnergyInfo(int i) {
        if (getState() != 12) {
            return null;
        }
        try {
            if (!this.mService.isActivityAndEnergyReportingSupported()) {
                return null;
            }
            synchronized (this) {
                if (i == 1) {
                    this.mService.getActivityEnergyInfoFromController();
                    wait(30L);
                }
                BluetoothActivityEnergyInfo bluetoothActivityEnergyInfoReportActivityInfo = this.mService.reportActivityInfo();
                if (bluetoothActivityEnergyInfoReportActivityInfo.isValid()) {
                    return bluetoothActivityEnergyInfoReportActivityInfo;
                }
                return null;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "getControllerActivityEnergyInfoCallback: " + e);
            return null;
        } catch (InterruptedException e2) {
            Log.e(TAG, "getControllerActivityEnergyInfoCallback wait interrupted: " + e2);
            return null;
        }
    }

    public Set<BluetoothDevice> getBondedDevices() {
        if (getState() != 12) {
            return toDeviceSet(new BluetoothDevice[0]);
        }
        try {
            synchronized (this.mManagerCallback) {
                IBluetooth iBluetooth = this.mService;
                if (iBluetooth == null) {
                    return toDeviceSet(new BluetoothDevice[0]);
                }
                return toDeviceSet(iBluetooth.getBondedDevices());
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return null;
        }
    }

    public int getConnectionState() {
        if (getState() != 12) {
            return 0;
        }
        try {
        } catch (RemoteException e) {
            Log.e(TAG, "getConnectionState:", e);
        }
        synchronized (this.mManagerCallback) {
            IBluetooth iBluetooth = this.mService;
            if (iBluetooth == null) {
                return 0;
            }
            return iBluetooth.getAdapterConnectionState();
        }
    }

    public int getProfileConnectionState(int i) {
        if (getState() != 12) {
            return 0;
        }
        try {
        } catch (RemoteException e) {
            Log.e(TAG, "getProfileConnectionState:", e);
        }
        synchronized (this.mManagerCallback) {
            IBluetooth iBluetooth = this.mService;
            if (iBluetooth == null) {
                return 0;
            }
            return iBluetooth.getProfileConnectionState(i);
        }
    }

    public BluetoothServerSocket listenUsingRfcommOn(int i) throws IOException {
        BluetoothServerSocket bluetoothServerSocket = new BluetoothServerSocket(1, true, true, i);
        int iBindListen = bluetoothServerSocket.mSocket.bindListen();
        if (iBindListen == 0) {
            return bluetoothServerSocket;
        }
        throw new IOException("Error: " + iBindListen);
    }

    public BluetoothServerSocket listenUsingRfcommWithServiceRecord(String str, UUID uuid) throws IOException {
        return createNewRfcommSocketAndRecord(str, uuid, true, true);
    }

    public BluetoothServerSocket listenUsingInsecureRfcommWithServiceRecord(String str, UUID uuid) throws IOException {
        return createNewRfcommSocketAndRecord(str, uuid, false, false);
    }

    public BluetoothServerSocket listenUsingEncryptedRfcommWithServiceRecord(String str, UUID uuid) throws IOException {
        return createNewRfcommSocketAndRecord(str, uuid, false, true);
    }

    private BluetoothServerSocket createNewRfcommSocketAndRecord(String str, UUID uuid, boolean z, boolean z2) throws IOException {
        BluetoothServerSocket bluetoothServerSocket = new BluetoothServerSocket(1, z, z2, new ParcelUuid(uuid));
        bluetoothServerSocket.setServiceName(str);
        int iBindListen = bluetoothServerSocket.mSocket.bindListen();
        if (iBindListen == 0) {
            return bluetoothServerSocket;
        }
        throw new IOException("Error: " + iBindListen);
    }

    public BluetoothServerSocket listenUsingInsecureRfcommOn(int i) throws IOException {
        BluetoothServerSocket bluetoothServerSocket = new BluetoothServerSocket(1, false, false, i);
        int iBindListen = bluetoothServerSocket.mSocket.bindListen();
        if (iBindListen == 0) {
            return bluetoothServerSocket;
        }
        throw new IOException("Error: " + iBindListen);
    }

    public BluetoothServerSocket listenUsingEncryptedRfcommOn(int i) throws IOException {
        BluetoothServerSocket bluetoothServerSocket = new BluetoothServerSocket(1, false, true, i);
        int iBindListen = bluetoothServerSocket.mSocket.bindListen();
        if (iBindListen >= 0) {
            return bluetoothServerSocket;
        }
        throw new IOException("Error: " + iBindListen);
    }

    public static BluetoothServerSocket listenUsingScoOn() throws IOException {
        BluetoothServerSocket bluetoothServerSocket = new BluetoothServerSocket(2, false, false, -1);
        bluetoothServerSocket.mSocket.bindListen();
        return bluetoothServerSocket;
    }

    public Pair<byte[], byte[]> readOutOfBandData() {
        if (getState() != 12) {
        }
        return null;
    }

    public boolean getProfileProxy(Context context, BluetoothProfile.ServiceListener serviceListener, int i) {
        if (context != null && serviceListener != null) {
            if (i == 1) {
                new BluetoothHeadset(context, serviceListener);
                return true;
            }
            if (i == 2) {
                new BluetoothA2dp(context, serviceListener);
                return true;
            }
            if (i == 10) {
                new BluetoothA2dpSink(context, serviceListener);
                return true;
            }
            if (i == 11) {
                new BluetoothAvrcpController(context, serviceListener);
                return true;
            }
            if (i == 4) {
                new BluetoothInputDevice(context, serviceListener);
                return true;
            }
            if (i == 5) {
                new BluetoothPan(context, serviceListener);
                return true;
            }
            if (i == 3) {
                new BluetoothHealth(context, serviceListener);
                return true;
            }
            if (i == 9) {
                new BluetoothMap(context, serviceListener);
                return true;
            }
            if (i == 16) {
                new BluetoothHeadsetClient(context, serviceListener);
                return true;
            }
        }
        return false;
    }

    public void closeProfileProxy(int i, BluetoothProfile bluetoothProfile) {
        if (bluetoothProfile == null) {
            return;
        }
        if (i == 1) {
            ((BluetoothHeadset) bluetoothProfile).close();
            return;
        }
        if (i == 2) {
            ((BluetoothA2dp) bluetoothProfile).close();
            return;
        }
        if (i == 3) {
            ((BluetoothHealth) bluetoothProfile).close();
            return;
        }
        if (i == 4) {
            ((BluetoothInputDevice) bluetoothProfile).close();
            return;
        }
        if (i == 5) {
            ((BluetoothPan) bluetoothProfile).close();
            return;
        }
        if (i != 16) {
            switch (i) {
                case 7:
                    ((BluetoothGatt) bluetoothProfile).close();
                    break;
                case 8:
                    ((BluetoothGattServer) bluetoothProfile).close();
                    break;
                case 9:
                    ((BluetoothMap) bluetoothProfile).close();
                    break;
                case 10:
                    ((BluetoothA2dpSink) bluetoothProfile).close();
                    break;
                case 11:
                    ((BluetoothAvrcpController) bluetoothProfile).close();
                    break;
            }
            return;
        }
        ((BluetoothHeadsetClient) bluetoothProfile).close();
    }

    public boolean enableNoAutoConnect() {
        if (isEnabled()) {
            Log.d(TAG, "enableNoAutoConnect(): BT is already enabled..!");
            return true;
        }
        try {
            return this.mManagerService.enableNoAutoConnect();
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public class StateChangeCallbackWrapper extends IBluetoothStateChangeCallback.Stub {
        private BluetoothStateChangeCallback mCallback;

        StateChangeCallbackWrapper(BluetoothStateChangeCallback bluetoothStateChangeCallback) {
            this.mCallback = bluetoothStateChangeCallback;
        }

        @Override // android.bluetooth.IBluetoothStateChangeCallback
        public void onBluetoothStateChange(boolean z) {
            this.mCallback.onBluetoothStateChange(z);
        }
    }

    private Set<BluetoothDevice> toDeviceSet(BluetoothDevice[] bluetoothDeviceArr) {
        return Collections.unmodifiableSet(new HashSet(Arrays.asList(bluetoothDeviceArr)));
    }

    protected void finalize() throws Throwable {
        try {
            try {
                this.mManagerService.unregisterAdapter(this.mManagerCallback);
            } catch (RemoteException e) {
                Log.e(TAG, "", e);
            }
        } finally {
            super.finalize();
        }
    }

    public static boolean checkBluetoothAddress(String str) {
        if (str == null || str.length() != 17) {
            return false;
        }
        for (int i = 0; i < 17; i++) {
            char cCharAt = str.charAt(i);
            int i2 = i % 3;
            if (i2 == 0 || i2 == 1) {
                if ((cCharAt < '0' || cCharAt > '9') && (cCharAt < 'A' || cCharAt > 'F')) {
                    return false;
                }
            } else if (i2 == 2 && cCharAt != ':') {
                return false;
            }
        }
        return true;
    }

    IBluetoothManager getBluetoothManager() {
        return this.mManagerService;
    }

    IBluetooth getBluetoothService(IBluetoothManagerCallback iBluetoothManagerCallback) {
        synchronized (this.mProxyServiceStateCallbacks) {
            if (iBluetoothManagerCallback == null) {
                Log.w(TAG, "getBluetoothService() called with no BluetoothManagerCallback");
            } else if (!this.mProxyServiceStateCallbacks.contains(iBluetoothManagerCallback)) {
                this.mProxyServiceStateCallbacks.add(iBluetoothManagerCallback);
            }
        }
        return this.mService;
    }

    void removeServiceStateCallback(IBluetoothManagerCallback iBluetoothManagerCallback) {
        synchronized (this.mProxyServiceStateCallbacks) {
            this.mProxyServiceStateCallbacks.remove(iBluetoothManagerCallback);
        }
    }

    @Deprecated
    public boolean startLeScan(LeScanCallback leScanCallback) {
        return startLeScan(null, leScanCallback);
    }

    @Deprecated
    public boolean startLeScan(final UUID[] uuidArr, final LeScanCallback leScanCallback) {
        Log.d(TAG, "startLeScan(): " + uuidArr);
        if (leScanCallback == null) {
            Log.e(TAG, "startLeScan: null callback");
            return false;
        }
        BluetoothLeScanner bluetoothLeScanner = getBluetoothLeScanner();
        if (bluetoothLeScanner == null) {
            Log.e(TAG, "startLeScan: cannot get BluetoothLeScanner");
            return false;
        }
        synchronized (this.mLeScanClients) {
            if (this.mLeScanClients.containsKey(leScanCallback)) {
                Log.e(TAG, "LE Scan has already started");
                return false;
            }
            try {
                if (this.mManagerService.getBluetoothGatt() == null) {
                    return false;
                }
                ScanCallback scanCallback = new ScanCallback() { // from class: android.bluetooth.BluetoothAdapter.2
                    @Override // android.bluetooth.le.ScanCallback
                    public void onScanResult(int i, ScanResult scanResult) {
                        if (i != 1) {
                            Log.e(BluetoothAdapter.TAG, "LE Scan has already started");
                            return;
                        }
                        ScanRecord scanRecord = scanResult.getScanRecord();
                        if (scanRecord == null) {
                            return;
                        }
                        if (uuidArr != null) {
                            ArrayList arrayList = new ArrayList();
                            for (UUID uuid : uuidArr) {
                                arrayList.add(new ParcelUuid(uuid));
                            }
                            List<ParcelUuid> serviceUuids = scanRecord.getServiceUuids();
                            if (serviceUuids == null || !serviceUuids.containsAll(arrayList)) {
                                Log.d(BluetoothAdapter.TAG, "uuids does not match");
                                return;
                            }
                        }
                        leScanCallback.onLeScan(scanResult.getDevice(), scanResult.getRssi(), scanRecord.getBytes());
                    }
                };
                ScanSettings scanSettingsBuild = new ScanSettings.Builder().setCallbackType(1).setScanMode(2).build();
                ArrayList arrayList = new ArrayList();
                if (uuidArr != null && uuidArr.length > 0) {
                    arrayList.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(uuidArr[0])).build());
                }
                bluetoothLeScanner.startScan(arrayList, scanSettingsBuild, scanCallback);
                this.mLeScanClients.put(leScanCallback, scanCallback);
                return true;
            } catch (RemoteException e) {
                Log.e(TAG, "", e);
                return false;
            }
        }
    }

    @Deprecated
    public void stopLeScan(LeScanCallback leScanCallback) {
        Log.d(TAG, "stopLeScan()");
        BluetoothLeScanner bluetoothLeScanner = getBluetoothLeScanner();
        if (bluetoothLeScanner == null) {
            return;
        }
        synchronized (this.mLeScanClients) {
            ScanCallback scanCallbackRemove = this.mLeScanClients.remove(leScanCallback);
            if (scanCallbackRemove == null) {
                Log.d(TAG, "scan not started yet");
            } else {
                bluetoothLeScanner.stopScan(scanCallbackRemove);
            }
        }
    }
}
