package android.net.wifi;

import android.content.Context;
import android.net.DhcpInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.WorkSource;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.AsyncChannel;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/* JADX INFO: loaded from: classes.dex */
public class WifiManager {
    public static final String ACTION_PICK_WIFI_NETWORK = "android.net.wifi.PICK_WIFI_NETWORK";
    public static final String ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE = "android.net.wifi.action.REQUEST_SCAN_ALWAYS_AVAILABLE";
    private static final int BASE = 151552;
    public static final String BATCHED_SCAN_RESULTS_AVAILABLE_ACTION = "android.net.wifi.BATCHED_RESULTS";
    public static final int BUSY = 2;
    public static final int CANCEL_WPS = 151566;
    public static final int CANCEL_WPS_FAILED = 151567;
    public static final int CANCEL_WPS_SUCCEDED = 151568;
    public static final int CHANGE_REASON_ADDED = 0;
    public static final int CHANGE_REASON_CONFIG_CHANGE = 2;
    public static final int CHANGE_REASON_REMOVED = 1;
    public static final String CONFIGURED_NETWORKS_CHANGED_ACTION = "android.net.wifi.CONFIGURED_NETWORKS_CHANGE";
    public static final int CONNECT_NETWORK = 151553;
    public static final int CONNECT_NETWORK_FAILED = 151554;
    public static final int CONNECT_NETWORK_SUCCEEDED = 151555;
    public static final int DATA_ACTIVITY_IN = 1;
    public static final int DATA_ACTIVITY_INOUT = 3;
    public static final int DATA_ACTIVITY_NONE = 0;
    public static final int DATA_ACTIVITY_NOTIFICATION = 1;
    public static final int DATA_ACTIVITY_OUT = 2;
    public static final int DISABLE_NETWORK = 151569;
    public static final int DISABLE_NETWORK_FAILED = 151570;
    public static final int DISABLE_NETWORK_SUCCEEDED = 151571;
    public static final int ERROR = 0;
    public static final int ERROR_AUTHENTICATING = 1;
    public static final String EXTRA_BSSID = "bssid";
    public static final String EXTRA_CHANGE_REASON = "changeReason";
    public static final String EXTRA_LINK_CAPABILITIES = "linkCapabilities";
    public static final String EXTRA_LINK_PROPERTIES = "linkProperties";
    public static final String EXTRA_MULTIPLE_NETWORKS_CHANGED = "multipleChanges";
    public static final String EXTRA_NETWORK_INFO = "networkInfo";
    public static final String EXTRA_NEW_RSSI = "newRssi";
    public static final String EXTRA_NEW_STATE = "newState";
    public static final String EXTRA_PREVIOUS_WIFI_AP_STATE = "previous_wifi_state";
    public static final String EXTRA_PREVIOUS_WIFI_STATE = "previous_wifi_state";
    public static final String EXTRA_SCAN_AVAILABLE = "scan_enabled";
    public static final String EXTRA_SUPPLICANT_CONNECTED = "connected";
    public static final String EXTRA_SUPPLICANT_ERROR = "supplicantError";
    public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
    public static final String EXTRA_WIFI_CONFIGURATION = "wifiConfiguration";
    public static final String EXTRA_WIFI_INFO = "wifiInfo";
    public static final String EXTRA_WIFI_STATE = "wifi_state";
    public static final int FORGET_NETWORK = 151556;
    public static final int FORGET_NETWORK_FAILED = 151557;
    public static final int FORGET_NETWORK_SUCCEEDED = 151558;
    public static final int INVALID_ARGS = 8;
    private static final int INVALID_KEY = 0;
    public static final int IN_PROGRESS = 1;
    public static final String LINK_CONFIGURATION_CHANGED_ACTION = "android.net.wifi.LINK_CONFIGURATION_CHANGED";
    private static final int MAX_ACTIVE_LOCKS = 50;
    private static final int MAX_RSSI = -55;
    private static final int MIN_RSSI = -100;
    public static final String NETWORK_IDS_CHANGED_ACTION = "android.net.wifi.NETWORK_IDS_CHANGED";
    public static final String NETWORK_STATE_CHANGED_ACTION = "android.net.wifi.STATE_CHANGE";
    public static final String RSSI_CHANGED_ACTION = "android.net.wifi.RSSI_CHANGED";
    public static final int RSSI_LEVELS = 5;
    public static final int RSSI_PKTCNT_FETCH = 151572;
    public static final int RSSI_PKTCNT_FETCH_FAILED = 151574;
    public static final int RSSI_PKTCNT_FETCH_SUCCEEDED = 151573;
    public static final int SAVE_NETWORK = 151559;
    public static final int SAVE_NETWORK_FAILED = 151560;
    public static final int SAVE_NETWORK_SUCCEEDED = 151561;
    public static final String SCAN_RESULTS_AVAILABLE_ACTION = "android.net.wifi.SCAN_RESULTS";
    public static final int START_WPS = 151562;
    public static final int START_WPS_SUCCEEDED = 151563;
    public static final String SUPPLICANT_CONNECTION_CHANGE_ACTION = "android.net.wifi.supplicant.CONNECTION_CHANGE";
    public static final String SUPPLICANT_STATE_CHANGED_ACTION = "android.net.wifi.supplicant.STATE_CHANGE";
    private static final String TAG = "WifiManager";
    public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    public static final int WIFI_AP_STATE_DISABLED = 11;
    public static final int WIFI_AP_STATE_DISABLING = 10;
    public static final int WIFI_AP_STATE_ENABLED = 13;
    public static final int WIFI_AP_STATE_ENABLING = 12;
    public static final int WIFI_AP_STATE_FAILED = 14;
    public static final int WIFI_FREQUENCY_BAND_2GHZ = 2;
    public static final int WIFI_FREQUENCY_BAND_5GHZ = 1;
    public static final int WIFI_FREQUENCY_BAND_AUTO = 0;
    public static final int WIFI_MODE_FULL = 1;
    public static final int WIFI_MODE_FULL_HIGH_PERF = 3;
    public static final int WIFI_MODE_SCAN_ONLY = 2;
    public static final String WIFI_SCAN_AVAILABLE = "wifi_scan_available";
    public static final String WIFI_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_STATE_CHANGED";
    public static final int WIFI_STATE_DISABLED = 1;
    public static final int WIFI_STATE_DISABLING = 0;
    public static final int WIFI_STATE_ENABLED = 3;
    public static final int WIFI_STATE_ENABLING = 2;
    public static final int WIFI_STATE_UNKNOWN = 4;
    public static final int WPS_AUTH_FAILURE = 6;
    public static final int WPS_COMPLETED = 151565;
    public static final int WPS_FAILED = 151564;
    public static final int WPS_OVERLAP_ERROR = 3;
    public static final int WPS_TIMED_OUT = 7;
    public static final int WPS_TKIP_ONLY_PROHIBITED = 5;
    public static final int WPS_WEP_PROHIBITED = 4;
    private static AsyncChannel sAsyncChannel = null;
    private static CountDownLatch sConnected = null;
    private static HandlerThread sHandlerThread = null;
    private static int sListenerKey = 1;
    private static int sThreadRefCount;
    private int mActiveLockCount;
    private Context mContext;
    IWifiManager mService;
    private static final SparseArray sListenerMap = new SparseArray();
    private static final Object sListenerMapLock = new Object();
    private static final Object sThreadRefLock = new Object();

    public interface ActionListener {
        void onFailure(int i);

        void onSuccess();
    }

    public interface TxPacketCountListener {
        void onFailure(int i);

        void onSuccess(int i);
    }

    public interface WpsListener {
        void onCompletion();

        void onFailure(int i);

        void onStartSuccess(String str);
    }

    public static int calculateSignalLevel(int i, int i2) {
        if (i <= -100) {
            return 0;
        }
        if (i >= MAX_RSSI) {
            return i2 - 1;
        }
        return (int) (((i - (-100)) * (i2 - 1)) / 45.0f);
    }

    public static int compareSignalLevel(int i, int i2) {
        return i - i2;
    }

    static /* synthetic */ int access$508(WifiManager wifiManager) {
        int i = wifiManager.mActiveLockCount;
        wifiManager.mActiveLockCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$510(WifiManager wifiManager) {
        int i = wifiManager.mActiveLockCount;
        wifiManager.mActiveLockCount = i - 1;
        return i;
    }

    public WifiManager(Context context, IWifiManager iWifiManager) {
        this.mContext = context;
        this.mService = iWifiManager;
        init();
    }

    public List<WifiConfiguration> getConfiguredNetworks() {
        try {
            return this.mService.getConfiguredNetworks();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public int addNetwork(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration == null) {
            return -1;
        }
        wifiConfiguration.networkId = -1;
        return addOrUpdateNetwork(wifiConfiguration);
    }

    public int updateNetwork(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration == null || wifiConfiguration.networkId < 0) {
            return -1;
        }
        return addOrUpdateNetwork(wifiConfiguration);
    }

    private int addOrUpdateNetwork(WifiConfiguration wifiConfiguration) {
        try {
            return this.mService.addOrUpdateNetwork(wifiConfiguration);
        } catch (RemoteException unused) {
            return -1;
        }
    }

    public boolean removeNetwork(int i) {
        try {
            return this.mService.removeNetwork(i);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean enableNetwork(int i, boolean z) {
        try {
            return this.mService.enableNetwork(i, z);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean disableNetwork(int i) {
        try {
            return this.mService.disableNetwork(i);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean disconnect() {
        try {
            this.mService.disconnect();
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean reconnect() {
        try {
            this.mService.reconnect();
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean reassociate() {
        try {
            this.mService.reassociate();
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean pingSupplicant() {
        IWifiManager iWifiManager = this.mService;
        if (iWifiManager == null) {
            return false;
        }
        try {
            return iWifiManager.pingSupplicant();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean startScan() {
        try {
            this.mService.startScan(null);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean startScanActive() {
        return startScan();
    }

    public boolean startScan(WorkSource workSource) {
        try {
            this.mService.startScan(workSource);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean requestBatchedScan(BatchedScanSettings batchedScanSettings) {
        try {
            return this.mService.requestBatchedScan(batchedScanSettings, new Binder(), null);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean requestBatchedScan(BatchedScanSettings batchedScanSettings, WorkSource workSource) {
        try {
            return this.mService.requestBatchedScan(batchedScanSettings, new Binder(), workSource);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean isBatchedScanSupported() {
        try {
            return this.mService.isBatchedScanSupported();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public void stopBatchedScan(BatchedScanSettings batchedScanSettings) {
        try {
            this.mService.stopBatchedScan(batchedScanSettings);
        } catch (RemoteException unused) {
        }
    }

    public List<BatchedScanResult> getBatchedScanResults() {
        try {
            return this.mService.getBatchedScanResults(this.mContext.getOpPackageName());
        } catch (RemoteException unused) {
            return null;
        }
    }

    public void pollBatchedScan() {
        try {
            this.mService.pollBatchedScan();
        } catch (RemoteException unused) {
        }
    }

    public WifiInfo getConnectionInfo() {
        try {
            return this.mService.getConnectionInfo();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public List<ScanResult> getScanResults() {
        try {
            return this.mService.getScanResults(this.mContext.getOpPackageName());
        } catch (RemoteException unused) {
            return null;
        }
    }

    public boolean isScanAlwaysAvailable() {
        try {
            return this.mService.isScanAlwaysAvailable();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean saveConfiguration() {
        try {
            return this.mService.saveConfiguration();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public void setCountryCode(String str, boolean z) {
        try {
            this.mService.setCountryCode(str, z);
        } catch (RemoteException unused) {
        }
    }

    public void setFrequencyBand(int i, boolean z) {
        try {
            this.mService.setFrequencyBand(i, z);
        } catch (RemoteException unused) {
        }
    }

    public int getFrequencyBand() {
        try {
            return this.mService.getFrequencyBand();
        } catch (RemoteException unused) {
            return -1;
        }
    }

    public boolean isDualBandSupported() {
        try {
            return this.mService.isDualBandSupported();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public DhcpInfo getDhcpInfo() {
        try {
            return this.mService.getDhcpInfo();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public boolean setWifiEnabled(boolean z) {
        try {
            return this.mService.setWifiEnabled(z);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public int getWifiState() {
        try {
            return this.mService.getWifiEnabledState();
        } catch (RemoteException unused) {
            return 4;
        }
    }

    public boolean isWifiEnabled() {
        return getWifiState() == 3;
    }

    public void getTxPacketCount(TxPacketCountListener txPacketCountListener) {
        validateChannel();
        sAsyncChannel.sendMessage(RSSI_PKTCNT_FETCH, 0, putListener(txPacketCountListener));
    }

    public boolean setWifiApEnabled(WifiConfiguration wifiConfiguration, boolean z) {
        try {
            this.mService.setWifiApEnabled(wifiConfiguration, z);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public int getWifiApState() {
        try {
            return this.mService.getWifiApEnabledState();
        } catch (RemoteException unused) {
            return 14;
        }
    }

    public boolean isWifiApEnabled() {
        return getWifiApState() == 13;
    }

    public WifiConfiguration getWifiApConfiguration() {
        try {
            return this.mService.getWifiApConfiguration();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public boolean setWifiApConfiguration(WifiConfiguration wifiConfiguration) {
        try {
            this.mService.setWifiApConfiguration(wifiConfiguration);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean startWifi() {
        try {
            this.mService.startWifi();
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean stopWifi() {
        try {
            this.mService.stopWifi();
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean addToBlacklist(String str) {
        try {
            this.mService.addToBlacklist(str);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean clearBlacklist() {
        try {
            this.mService.clearBlacklist();
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public void setTdlsEnabled(InetAddress inetAddress, boolean z) {
        try {
            this.mService.enableTdls(inetAddress.getHostAddress(), z);
        } catch (RemoteException unused) {
        }
    }

    public void setTdlsEnabledWithMacAddress(String str, boolean z) {
        try {
            this.mService.enableTdlsWithMacAddress(str, z);
        } catch (RemoteException unused) {
        }
    }

    private static class ServiceHandler extends Handler {
        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Object objRemoveListener = WifiManager.removeListener(message.arg2);
            switch (message.what) {
                case 69632:
                    if (message.arg1 == 0) {
                        WifiManager.sAsyncChannel.sendMessage(69633);
                    } else {
                        Log.e(WifiManager.TAG, "Failed to set up channel connection");
                        AsyncChannel unused = WifiManager.sAsyncChannel = null;
                    }
                    WifiManager.sConnected.countDown();
                    return;
                case 69636:
                    Log.e(WifiManager.TAG, "Channel connection lost");
                    AsyncChannel unused2 = WifiManager.sAsyncChannel = null;
                    getLooper().quit();
                    return;
                case WifiManager.CONNECT_NETWORK_FAILED /* 151554 */:
                case WifiManager.FORGET_NETWORK_FAILED /* 151557 */:
                case WifiManager.SAVE_NETWORK_FAILED /* 151560 */:
                case WifiManager.CANCEL_WPS_FAILED /* 151567 */:
                case WifiManager.DISABLE_NETWORK_FAILED /* 151570 */:
                    if (objRemoveListener != null) {
                        ((ActionListener) objRemoveListener).onFailure(message.arg1);
                        return;
                    }
                    return;
                case WifiManager.CONNECT_NETWORK_SUCCEEDED /* 151555 */:
                case WifiManager.FORGET_NETWORK_SUCCEEDED /* 151558 */:
                case WifiManager.SAVE_NETWORK_SUCCEEDED /* 151561 */:
                case WifiManager.CANCEL_WPS_SUCCEDED /* 151568 */:
                case WifiManager.DISABLE_NETWORK_SUCCEEDED /* 151571 */:
                    if (objRemoveListener != null) {
                        ((ActionListener) objRemoveListener).onSuccess();
                        return;
                    }
                    return;
                case WifiManager.START_WPS_SUCCEEDED /* 151563 */:
                    if (objRemoveListener != null) {
                        ((WpsListener) objRemoveListener).onStartSuccess(((WpsResult) message.obj).pin);
                        synchronized (WifiManager.sListenerMapLock) {
                            WifiManager.sListenerMap.put(message.arg2, objRemoveListener);
                            break;
                        }
                        return;
                    }
                    return;
                case WifiManager.WPS_FAILED /* 151564 */:
                    if (objRemoveListener != null) {
                        ((WpsListener) objRemoveListener).onFailure(message.arg1);
                        return;
                    }
                    return;
                case WifiManager.WPS_COMPLETED /* 151565 */:
                    if (objRemoveListener != null) {
                        ((WpsListener) objRemoveListener).onCompletion();
                        return;
                    }
                    return;
                case WifiManager.RSSI_PKTCNT_FETCH_SUCCEEDED /* 151573 */:
                    if (objRemoveListener != null) {
                        RssiPacketCountInfo rssiPacketCountInfo = (RssiPacketCountInfo) message.obj;
                        if (rssiPacketCountInfo != null) {
                            ((TxPacketCountListener) objRemoveListener).onSuccess(rssiPacketCountInfo.txgood + rssiPacketCountInfo.txbad);
                            return;
                        } else {
                            ((TxPacketCountListener) objRemoveListener).onFailure(0);
                            return;
                        }
                    }
                    return;
                case WifiManager.RSSI_PKTCNT_FETCH_FAILED /* 151574 */:
                    if (objRemoveListener != null) {
                        ((TxPacketCountListener) objRemoveListener).onFailure(message.arg1);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    private static int putListener(Object obj) {
        int i;
        if (obj == null) {
            return 0;
        }
        synchronized (sListenerMapLock) {
            do {
                i = sListenerKey;
                sListenerKey = i + 1;
            } while (i == 0);
            sListenerMap.put(i, obj);
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Object removeListener(int i) {
        Object obj;
        if (i == 0) {
            return null;
        }
        synchronized (sListenerMapLock) {
            SparseArray sparseArray = sListenerMap;
            obj = sparseArray.get(i);
            sparseArray.remove(i);
        }
        return obj;
    }

    private void init() {
        synchronized (sThreadRefLock) {
            int i = sThreadRefCount + 1;
            sThreadRefCount = i;
            if (i == 1) {
                Messenger wifiServiceMessenger = getWifiServiceMessenger();
                if (wifiServiceMessenger == null) {
                    sAsyncChannel = null;
                    return;
                }
                sHandlerThread = new HandlerThread(TAG);
                sAsyncChannel = new AsyncChannel();
                sConnected = new CountDownLatch(1);
                sHandlerThread.start();
                sAsyncChannel.connect(this.mContext, new ServiceHandler(sHandlerThread.getLooper()), wifiServiceMessenger);
                try {
                    sConnected.await();
                } catch (InterruptedException unused) {
                    Log.e(TAG, "interrupted wait at init");
                }
            }
        }
    }

    private void validateChannel() {
        if (sAsyncChannel == null) {
            throw new IllegalStateException("No permission to access and change wifi or a bad initialization");
        }
    }

    public void connect(WifiConfiguration wifiConfiguration, ActionListener actionListener) {
        if (wifiConfiguration == null) {
            throw new IllegalArgumentException("config cannot be null");
        }
        validateChannel();
        sAsyncChannel.sendMessage(CONNECT_NETWORK, -1, putListener(actionListener), wifiConfiguration);
    }

    public void connect(int i, ActionListener actionListener) {
        if (i < 0) {
            throw new IllegalArgumentException("Network id cannot be negative");
        }
        validateChannel();
        sAsyncChannel.sendMessage(CONNECT_NETWORK, i, putListener(actionListener));
    }

    public void save(WifiConfiguration wifiConfiguration, ActionListener actionListener) {
        if (wifiConfiguration == null) {
            throw new IllegalArgumentException("config cannot be null");
        }
        validateChannel();
        sAsyncChannel.sendMessage(SAVE_NETWORK, 0, putListener(actionListener), wifiConfiguration);
    }

    public void forget(int i, ActionListener actionListener) {
        if (i < 0) {
            throw new IllegalArgumentException("Network id cannot be negative");
        }
        validateChannel();
        sAsyncChannel.sendMessage(FORGET_NETWORK, i, putListener(actionListener));
    }

    public void disable(int i, ActionListener actionListener) {
        if (i < 0) {
            throw new IllegalArgumentException("Network id cannot be negative");
        }
        validateChannel();
        sAsyncChannel.sendMessage(DISABLE_NETWORK, i, putListener(actionListener));
    }

    public void startWps(WpsInfo wpsInfo, WpsListener wpsListener) {
        if (wpsInfo == null) {
            throw new IllegalArgumentException("config cannot be null");
        }
        validateChannel();
        sAsyncChannel.sendMessage(START_WPS, 0, putListener(wpsListener), wpsInfo);
    }

    public void cancelWps(ActionListener actionListener) {
        validateChannel();
        sAsyncChannel.sendMessage(CANCEL_WPS, 0, putListener(actionListener));
    }

    public Messenger getWifiServiceMessenger() {
        try {
            return this.mService.getWifiServiceMessenger();
        } catch (RemoteException | SecurityException unused) {
            return null;
        }
    }

    public Messenger getWifiStateMachineMessenger() {
        try {
            return this.mService.getWifiStateMachineMessenger();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public String getConfigFile() {
        try {
            return this.mService.getConfigFile();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public class WifiLock {
        private final IBinder mBinder;
        private boolean mHeld;
        int mLockType;
        private int mRefCount;
        private boolean mRefCounted;
        private String mTag;
        private WorkSource mWorkSource;

        private WifiLock(int i, String str) {
            this.mTag = str;
            this.mLockType = i;
            this.mBinder = new Binder();
            this.mRefCount = 0;
            this.mRefCounted = true;
            this.mHeld = false;
        }

        /* JADX WARN: Removed duplicated region for block: B:30:0x0026 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void acquire() {
            /*
                r7 = this;
                android.os.IBinder r0 = r7.mBinder
                monitor-enter(r0)
                boolean r1 = r7.mRefCounted     // Catch: java.lang.Throwable -> L4f
                r2 = 1
                if (r1 == 0) goto L10
                int r1 = r7.mRefCount     // Catch: java.lang.Throwable -> L4f
                int r1 = r1 + r2
                r7.mRefCount = r1     // Catch: java.lang.Throwable -> L4f
                if (r1 != r2) goto L4d
                goto L14
            L10:
                boolean r1 = r7.mHeld     // Catch: java.lang.Throwable -> L4f
                if (r1 != 0) goto L4d
            L14:
                android.net.wifi.WifiManager r1 = android.net.wifi.WifiManager.this     // Catch: android.os.RemoteException -> L4b java.lang.Throwable -> L4f
                android.net.wifi.IWifiManager r1 = r1.mService     // Catch: android.os.RemoteException -> L4b java.lang.Throwable -> L4f
                android.os.IBinder r3 = r7.mBinder     // Catch: android.os.RemoteException -> L4b java.lang.Throwable -> L4f
                int r4 = r7.mLockType     // Catch: android.os.RemoteException -> L4b java.lang.Throwable -> L4f
                java.lang.String r5 = r7.mTag     // Catch: android.os.RemoteException -> L4b java.lang.Throwable -> L4f
                android.os.WorkSource r6 = r7.mWorkSource     // Catch: android.os.RemoteException -> L4b java.lang.Throwable -> L4f
                r1.acquireWifiLock(r3, r4, r5, r6)     // Catch: android.os.RemoteException -> L4b java.lang.Throwable -> L4f
                android.net.wifi.WifiManager r1 = android.net.wifi.WifiManager.this     // Catch: android.os.RemoteException -> L4b java.lang.Throwable -> L4f
                monitor-enter(r1)     // Catch: android.os.RemoteException -> L4b java.lang.Throwable -> L4f
                android.net.wifi.WifiManager r3 = android.net.wifi.WifiManager.this     // Catch: java.lang.Throwable -> L48
                int r3 = android.net.wifi.WifiManager.access$500(r3)     // Catch: java.lang.Throwable -> L48
                r4 = 50
                if (r3 >= r4) goto L37
                android.net.wifi.WifiManager r3 = android.net.wifi.WifiManager.this     // Catch: java.lang.Throwable -> L48
                android.net.wifi.WifiManager.access$508(r3)     // Catch: java.lang.Throwable -> L48
                monitor-exit(r1)     // Catch: java.lang.Throwable -> L48
                goto L4b
            L37:
                android.net.wifi.WifiManager r3 = android.net.wifi.WifiManager.this     // Catch: java.lang.Throwable -> L48
                android.net.wifi.IWifiManager r3 = r3.mService     // Catch: java.lang.Throwable -> L48
                android.os.IBinder r4 = r7.mBinder     // Catch: java.lang.Throwable -> L48
                r3.releaseWifiLock(r4)     // Catch: java.lang.Throwable -> L48
                java.lang.UnsupportedOperationException r3 = new java.lang.UnsupportedOperationException     // Catch: java.lang.Throwable -> L48
                java.lang.String r4 = "Exceeded maximum number of wifi locks"
                r3.<init>(r4)     // Catch: java.lang.Throwable -> L48
                throw r3     // Catch: java.lang.Throwable -> L48
            L48:
                r3 = move-exception
                monitor-exit(r1)     // Catch: java.lang.Throwable -> L48
                throw r3     // Catch: android.os.RemoteException -> L4b java.lang.Throwable -> L4f
            L4b:
                r7.mHeld = r2     // Catch: java.lang.Throwable -> L4f
            L4d:
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L4f
                return
            L4f:
                r1 = move-exception
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L4f
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiManager.WifiLock.acquire():void");
        }

        /* JADX WARN: Removed duplicated region for block: B:31:0x0020 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void release() {
            /*
                r4 = this;
                android.os.IBinder r0 = r4.mBinder
                monitor-enter(r0)
                boolean r1 = r4.mRefCounted     // Catch: java.lang.Throwable -> L4e
                if (r1 == 0) goto L10
                int r1 = r4.mRefCount     // Catch: java.lang.Throwable -> L4e
                int r1 = r1 + (-1)
                r4.mRefCount = r1     // Catch: java.lang.Throwable -> L4e
                if (r1 != 0) goto L2d
                goto L14
            L10:
                boolean r1 = r4.mHeld     // Catch: java.lang.Throwable -> L4e
                if (r1 == 0) goto L2d
            L14:
                android.net.wifi.WifiManager r1 = android.net.wifi.WifiManager.this     // Catch: android.os.RemoteException -> L2a java.lang.Throwable -> L4e
                android.net.wifi.IWifiManager r1 = r1.mService     // Catch: android.os.RemoteException -> L2a java.lang.Throwable -> L4e
                android.os.IBinder r2 = r4.mBinder     // Catch: android.os.RemoteException -> L2a java.lang.Throwable -> L4e
                r1.releaseWifiLock(r2)     // Catch: android.os.RemoteException -> L2a java.lang.Throwable -> L4e
                android.net.wifi.WifiManager r1 = android.net.wifi.WifiManager.this     // Catch: android.os.RemoteException -> L2a java.lang.Throwable -> L4e
                monitor-enter(r1)     // Catch: android.os.RemoteException -> L2a java.lang.Throwable -> L4e
                android.net.wifi.WifiManager r2 = android.net.wifi.WifiManager.this     // Catch: java.lang.Throwable -> L27
                android.net.wifi.WifiManager.access$510(r2)     // Catch: java.lang.Throwable -> L27
                monitor-exit(r1)     // Catch: java.lang.Throwable -> L27
                goto L2a
            L27:
                r2 = move-exception
                monitor-exit(r1)     // Catch: java.lang.Throwable -> L27
                throw r2     // Catch: android.os.RemoteException -> L2a java.lang.Throwable -> L4e
            L2a:
                r1 = 0
                r4.mHeld = r1     // Catch: java.lang.Throwable -> L4e
            L2d:
                int r1 = r4.mRefCount     // Catch: java.lang.Throwable -> L4e
                if (r1 < 0) goto L33
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L4e
                return
            L33:
                java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch: java.lang.Throwable -> L4e
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L4e
                r2.<init>()     // Catch: java.lang.Throwable -> L4e
                java.lang.String r3 = "WifiLock under-locked "
                java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> L4e
                java.lang.String r3 = r4.mTag     // Catch: java.lang.Throwable -> L4e
                java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> L4e
                java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> L4e
                r1.<init>(r2)     // Catch: java.lang.Throwable -> L4e
                throw r1     // Catch: java.lang.Throwable -> L4e
            L4e:
                r1 = move-exception
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L4e
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiManager.WifiLock.release():void");
        }

        public void setReferenceCounted(boolean z) {
            this.mRefCounted = z;
        }

        public boolean isHeld() {
            boolean z;
            synchronized (this.mBinder) {
                z = this.mHeld;
            }
            return z;
        }

        public void setWorkSource(WorkSource workSource) {
            synchronized (this.mBinder) {
                if (workSource != null) {
                    try {
                        if (workSource.size() == 0) {
                            workSource = null;
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
                boolean zDiff = true;
                if (workSource == null) {
                    this.mWorkSource = null;
                } else {
                    workSource.clearNames();
                    WorkSource workSource2 = this.mWorkSource;
                    if (workSource2 == null) {
                        if (workSource2 == null) {
                            zDiff = false;
                        }
                        this.mWorkSource = new WorkSource(workSource);
                    } else {
                        zDiff = workSource2.diff(workSource);
                        if (zDiff) {
                            this.mWorkSource.set(workSource);
                        }
                    }
                }
                if (zDiff && this.mHeld) {
                    try {
                        WifiManager.this.mService.updateWifiLockWorkSource(this.mBinder, this.mWorkSource);
                    } catch (RemoteException unused) {
                    }
                }
            }
        }

        public String toString() {
            String str;
            synchronized (this.mBinder) {
                str = "WifiLock{ " + Integer.toHexString(System.identityHashCode(this)) + "; " + (this.mHeld ? "held; " : "") + (this.mRefCounted ? "refcounted: refcount = " + this.mRefCount : "not refcounted") + " }";
            }
            return str;
        }

        protected void finalize() throws Throwable {
            super.finalize();
            synchronized (this.mBinder) {
                if (this.mHeld) {
                    try {
                        WifiManager.this.mService.releaseWifiLock(this.mBinder);
                        synchronized (WifiManager.this) {
                            WifiManager.access$510(WifiManager.this);
                        }
                    } catch (RemoteException unused) {
                    }
                }
            }
        }
    }

    public WifiLock createWifiLock(int i, String str) {
        return new WifiLock(i, str);
    }

    public WifiLock createWifiLock(String str) {
        return new WifiLock(1, str);
    }

    public MulticastLock createMulticastLock(String str) {
        return new MulticastLock(str);
    }

    public class MulticastLock {
        private final IBinder mBinder;
        private boolean mHeld;
        private int mRefCount;
        private boolean mRefCounted;
        private String mTag;

        private MulticastLock(String str) {
            this.mTag = str;
            this.mBinder = new Binder();
            this.mRefCount = 0;
            this.mRefCounted = true;
            this.mHeld = false;
        }

        /* JADX WARN: Removed duplicated region for block: B:30:0x0022 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void acquire() {
            /*
                r5 = this;
                android.os.IBinder r0 = r5.mBinder
                monitor-enter(r0)
                boolean r1 = r5.mRefCounted     // Catch: java.lang.Throwable -> L49
                r2 = 1
                if (r1 == 0) goto L10
                int r1 = r5.mRefCount     // Catch: java.lang.Throwable -> L49
                int r1 = r1 + r2
                r5.mRefCount = r1     // Catch: java.lang.Throwable -> L49
                if (r1 != r2) goto L47
                goto L14
            L10:
                boolean r1 = r5.mHeld     // Catch: java.lang.Throwable -> L49
                if (r1 != 0) goto L47
            L14:
                android.net.wifi.WifiManager r1 = android.net.wifi.WifiManager.this     // Catch: android.os.RemoteException -> L45 java.lang.Throwable -> L49
                android.net.wifi.IWifiManager r1 = r1.mService     // Catch: android.os.RemoteException -> L45 java.lang.Throwable -> L49
                android.os.IBinder r3 = r5.mBinder     // Catch: android.os.RemoteException -> L45 java.lang.Throwable -> L49
                java.lang.String r4 = r5.mTag     // Catch: android.os.RemoteException -> L45 java.lang.Throwable -> L49
                r1.acquireMulticastLock(r3, r4)     // Catch: android.os.RemoteException -> L45 java.lang.Throwable -> L49
                android.net.wifi.WifiManager r1 = android.net.wifi.WifiManager.this     // Catch: android.os.RemoteException -> L45 java.lang.Throwable -> L49
                monitor-enter(r1)     // Catch: android.os.RemoteException -> L45 java.lang.Throwable -> L49
                android.net.wifi.WifiManager r3 = android.net.wifi.WifiManager.this     // Catch: java.lang.Throwable -> L42
                int r3 = android.net.wifi.WifiManager.access$500(r3)     // Catch: java.lang.Throwable -> L42
                r4 = 50
                if (r3 >= r4) goto L33
                android.net.wifi.WifiManager r3 = android.net.wifi.WifiManager.this     // Catch: java.lang.Throwable -> L42
                android.net.wifi.WifiManager.access$508(r3)     // Catch: java.lang.Throwable -> L42
                monitor-exit(r1)     // Catch: java.lang.Throwable -> L42
                goto L45
            L33:
                android.net.wifi.WifiManager r3 = android.net.wifi.WifiManager.this     // Catch: java.lang.Throwable -> L42
                android.net.wifi.IWifiManager r3 = r3.mService     // Catch: java.lang.Throwable -> L42
                r3.releaseMulticastLock()     // Catch: java.lang.Throwable -> L42
                java.lang.UnsupportedOperationException r3 = new java.lang.UnsupportedOperationException     // Catch: java.lang.Throwable -> L42
                java.lang.String r4 = "Exceeded maximum number of wifi locks"
                r3.<init>(r4)     // Catch: java.lang.Throwable -> L42
                throw r3     // Catch: java.lang.Throwable -> L42
            L42:
                r3 = move-exception
                monitor-exit(r1)     // Catch: java.lang.Throwable -> L42
                throw r3     // Catch: android.os.RemoteException -> L45 java.lang.Throwable -> L49
            L45:
                r5.mHeld = r2     // Catch: java.lang.Throwable -> L49
            L47:
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L49
                return
            L49:
                r1 = move-exception
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L49
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiManager.MulticastLock.acquire():void");
        }

        /* JADX WARN: Removed duplicated region for block: B:31:0x001e A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void release() {
            /*
                r4 = this;
                android.os.IBinder r0 = r4.mBinder
                monitor-enter(r0)
                boolean r1 = r4.mRefCounted     // Catch: java.lang.Throwable -> L4c
                if (r1 == 0) goto L10
                int r1 = r4.mRefCount     // Catch: java.lang.Throwable -> L4c
                int r1 = r1 + (-1)
                r4.mRefCount = r1     // Catch: java.lang.Throwable -> L4c
                if (r1 != 0) goto L2b
                goto L14
            L10:
                boolean r1 = r4.mHeld     // Catch: java.lang.Throwable -> L4c
                if (r1 == 0) goto L2b
            L14:
                android.net.wifi.WifiManager r1 = android.net.wifi.WifiManager.this     // Catch: android.os.RemoteException -> L28 java.lang.Throwable -> L4c
                android.net.wifi.IWifiManager r1 = r1.mService     // Catch: android.os.RemoteException -> L28 java.lang.Throwable -> L4c
                r1.releaseMulticastLock()     // Catch: android.os.RemoteException -> L28 java.lang.Throwable -> L4c
                android.net.wifi.WifiManager r1 = android.net.wifi.WifiManager.this     // Catch: android.os.RemoteException -> L28 java.lang.Throwable -> L4c
                monitor-enter(r1)     // Catch: android.os.RemoteException -> L28 java.lang.Throwable -> L4c
                android.net.wifi.WifiManager r2 = android.net.wifi.WifiManager.this     // Catch: java.lang.Throwable -> L25
                android.net.wifi.WifiManager.access$510(r2)     // Catch: java.lang.Throwable -> L25
                monitor-exit(r1)     // Catch: java.lang.Throwable -> L25
                goto L28
            L25:
                r2 = move-exception
                monitor-exit(r1)     // Catch: java.lang.Throwable -> L25
                throw r2     // Catch: android.os.RemoteException -> L28 java.lang.Throwable -> L4c
            L28:
                r1 = 0
                r4.mHeld = r1     // Catch: java.lang.Throwable -> L4c
            L2b:
                int r1 = r4.mRefCount     // Catch: java.lang.Throwable -> L4c
                if (r1 < 0) goto L31
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L4c
                return
            L31:
                java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch: java.lang.Throwable -> L4c
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L4c
                r2.<init>()     // Catch: java.lang.Throwable -> L4c
                java.lang.String r3 = "MulticastLock under-locked "
                java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> L4c
                java.lang.String r3 = r4.mTag     // Catch: java.lang.Throwable -> L4c
                java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> L4c
                java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> L4c
                r1.<init>(r2)     // Catch: java.lang.Throwable -> L4c
                throw r1     // Catch: java.lang.Throwable -> L4c
            L4c:
                r1 = move-exception
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L4c
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiManager.MulticastLock.release():void");
        }

        public void setReferenceCounted(boolean z) {
            this.mRefCounted = z;
        }

        public boolean isHeld() {
            boolean z;
            synchronized (this.mBinder) {
                z = this.mHeld;
            }
            return z;
        }

        public String toString() {
            String str;
            synchronized (this.mBinder) {
                str = "MulticastLock{ " + Integer.toHexString(System.identityHashCode(this)) + "; " + (this.mHeld ? "held; " : "") + (this.mRefCounted ? "refcounted: refcount = " + this.mRefCount : "not refcounted") + " }";
            }
            return str;
        }

        protected void finalize() throws Throwable {
            super.finalize();
            setReferenceCounted(false);
            release();
        }
    }

    public boolean isMulticastEnabled() {
        try {
            return this.mService.isMulticastEnabled();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean initializeMulticastFiltering() {
        try {
            this.mService.initializeMulticastFiltering();
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public void captivePortalCheckComplete() {
        try {
            this.mService.captivePortalCheckComplete();
        } catch (RemoteException unused) {
        }
    }

    protected void finalize() throws Throwable {
        AsyncChannel asyncChannel;
        try {
            synchronized (sThreadRefLock) {
                int i = sThreadRefCount - 1;
                sThreadRefCount = i;
                if (i == 0 && (asyncChannel = sAsyncChannel) != null) {
                    asyncChannel.disconnect();
                }
            }
        } finally {
            super.finalize();
        }
    }
}
