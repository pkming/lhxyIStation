package android.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CaptivePortalTracker extends StateMachine {
    public static final String ACTION_NETWORK_CONDITIONS_MEASURED = "android.net.conn.NETWORK_CONDITIONS_MEASURED";
    private static final int CMD_CONNECTIVITY_CHANGE = 1;
    private static final int CMD_DELAYED_CAPTIVE_CHECK = 2;
    private static final int CMD_DETECT_PORTAL = 0;
    private static final boolean DBG = true;
    private static final String DEFAULT_SERVER = "clients3.google.com";
    private static final int DELAYED_CHECK_INTERVAL_MS = 10000;
    public static final String EXTRA_BSSID = "extra_bssid";
    public static final String EXTRA_CELL_ID = "extra_cellid";
    public static final String EXTRA_CONNECTIVITY_TYPE = "extra_connectivity_type";
    public static final String EXTRA_IS_CAPTIVE_PORTAL = "extra_is_captive_portal";
    public static final String EXTRA_NETWORK_TYPE = "extra_network_type";
    public static final String EXTRA_REQUEST_TIMESTAMP_MS = "extra_request_timestamp_ms";
    public static final String EXTRA_RESPONSE_RECEIVED = "extra_response_received";
    public static final String EXTRA_RESPONSE_TIMESTAMP_MS = "extra_response_timestamp_ms";
    public static final String EXTRA_SSID = "extra_ssid";
    private static final String PERMISSION_ACCESS_NETWORK_CONDITIONS = "android.permission.ACCESS_NETWORK_CONDITIONS";
    private static final String SETUP_WIZARD_PACKAGE = "com.google.android.setupwizard";
    private static final int SOCKET_TIMEOUT_MS = 10000;
    private static final String TAG = "CaptivePortalTracker";
    private State mActiveNetworkState;
    private IConnectivityManager mConnService;
    private Context mContext;
    private State mDefaultState;
    private State mDelayedCaptiveCheckState;
    private int mDelayedCheckToken;
    private boolean mDeviceProvisioned;
    private boolean mIsCaptivePortalCheckEnabled;
    private NetworkInfo mNetworkInfo;
    private State mNoActiveNetworkState;
    private ProvisioningObserver mProvisioningObserver;
    private final BroadcastReceiver mReceiver;
    private String mServer;
    private TelephonyManager mTelephonyManager;
    private String mUrl;
    private WifiManager mWifiManager;

    static /* synthetic */ int access$2204(CaptivePortalTracker captivePortalTracker) {
        int i = captivePortalTracker.mDelayedCheckToken + 1;
        captivePortalTracker.mDelayedCheckToken = i;
        return i;
    }

    private CaptivePortalTracker(Context context, IConnectivityManager iConnectivityManager) {
        super(TAG);
        this.mIsCaptivePortalCheckEnabled = false;
        this.mDelayedCheckToken = 0;
        this.mDefaultState = new DefaultState();
        this.mNoActiveNetworkState = new NoActiveNetworkState();
        this.mActiveNetworkState = new ActiveNetworkState();
        this.mDelayedCaptiveCheckState = new DelayedCaptiveCheckState();
        this.mDeviceProvisioned = false;
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: android.net.CaptivePortalTracker.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (!(CaptivePortalTracker.this.mDeviceProvisioned && action.equals("android.net.conn.CONNECTIVITY_CHANGE")) && (CaptivePortalTracker.this.mDeviceProvisioned || !action.equals(ConnectivityManager.CONNECTIVITY_ACTION_IMMEDIATE))) {
                    return;
                }
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                CaptivePortalTracker captivePortalTracker = CaptivePortalTracker.this;
                captivePortalTracker.sendMessage(captivePortalTracker.obtainMessage(1, networkInfo));
            }
        };
        this.mReceiver = broadcastReceiver;
        this.mContext = context;
        this.mConnService = iConnectivityManager;
        this.mTelephonyManager = (TelephonyManager) context.getSystemService("phone");
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mProvisioningObserver = new ProvisioningObserver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION_IMMEDIATE);
        this.mContext.registerReceiver(broadcastReceiver, intentFilter);
        String string = Settings.Global.getString(this.mContext.getContentResolver(), Settings.Global.CAPTIVE_PORTAL_SERVER);
        this.mServer = string;
        if (string == null) {
            this.mServer = DEFAULT_SERVER;
        }
        this.mIsCaptivePortalCheckEnabled = Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.CAPTIVE_PORTAL_DETECTION_ENABLED, 1) == 1;
        addState(this.mDefaultState);
        addState(this.mNoActiveNetworkState, this.mDefaultState);
        addState(this.mActiveNetworkState, this.mDefaultState);
        addState(this.mDelayedCaptiveCheckState, this.mActiveNetworkState);
        setInitialState(this.mNoActiveNetworkState);
    }

    private class ProvisioningObserver extends ContentObserver {
        ProvisioningObserver() {
            super(new Handler());
            CaptivePortalTracker.this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("device_provisioned"), false, this);
            onChange(false);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            CaptivePortalTracker captivePortalTracker = CaptivePortalTracker.this;
            captivePortalTracker.mDeviceProvisioned = Settings.Global.getInt(captivePortalTracker.mContext.getContentResolver(), "device_provisioned", 0) != 0;
        }
    }

    public static CaptivePortalTracker makeCaptivePortalTracker(Context context, IConnectivityManager iConnectivityManager) {
        CaptivePortalTracker captivePortalTracker = new CaptivePortalTracker(context, iConnectivityManager);
        captivePortalTracker.start();
        return captivePortalTracker;
    }

    public void detectCaptivePortal(NetworkInfo networkInfo) {
        sendMessage(obtainMessage(0, networkInfo));
    }

    private class DefaultState extends State {
        private DefaultState() {
        }

        public boolean processMessage(Message message) {
            CaptivePortalTracker.this.log(getName() + message.toString());
            int i = message.what;
            if (i == 0) {
                CaptivePortalTracker.this.notifyPortalCheckComplete((NetworkInfo) message.obj);
            } else if (i != 1 && i != 2) {
                CaptivePortalTracker.this.loge("Ignoring " + message);
            }
            return true;
        }
    }

    private class NoActiveNetworkState extends State {
        private NoActiveNetworkState() {
        }

        public void enter() {
            CaptivePortalTracker.this.setNotificationOff();
            CaptivePortalTracker.this.mNetworkInfo = null;
        }

        public boolean processMessage(Message message) {
            CaptivePortalTracker.this.log(getName() + message.toString());
            if (message.what != 1) {
                return false;
            }
            NetworkInfo networkInfo = (NetworkInfo) message.obj;
            if (networkInfo.getType() != 1) {
                CaptivePortalTracker.this.log(getName() + " not a wifi connectivity change, ignore");
            } else if (networkInfo.isConnected() && CaptivePortalTracker.this.isActiveNetwork(networkInfo)) {
                CaptivePortalTracker.this.mNetworkInfo = networkInfo;
                CaptivePortalTracker captivePortalTracker = CaptivePortalTracker.this;
                captivePortalTracker.transitionTo(captivePortalTracker.mDelayedCaptiveCheckState);
            }
            return true;
        }
    }

    private class ActiveNetworkState extends State {
        private ActiveNetworkState() {
        }

        public boolean processMessage(Message message) {
            if (message.what != 1) {
                return false;
            }
            NetworkInfo networkInfo = (NetworkInfo) message.obj;
            if (networkInfo.isConnected() || networkInfo.getType() != CaptivePortalTracker.this.mNetworkInfo.getType()) {
                if (networkInfo.getType() != CaptivePortalTracker.this.mNetworkInfo.getType() && networkInfo.isConnected() && CaptivePortalTracker.this.isActiveNetwork(networkInfo)) {
                    CaptivePortalTracker.this.log("Active network switched " + networkInfo);
                    CaptivePortalTracker.this.deferMessage(message);
                    CaptivePortalTracker captivePortalTracker = CaptivePortalTracker.this;
                    captivePortalTracker.transitionTo(captivePortalTracker.mNoActiveNetworkState);
                }
            } else {
                CaptivePortalTracker.this.log("Disconnected from active network " + networkInfo);
                CaptivePortalTracker captivePortalTracker2 = CaptivePortalTracker.this;
                captivePortalTracker2.transitionTo(captivePortalTracker2.mNoActiveNetworkState);
            }
            return true;
        }
    }

    private class DelayedCaptiveCheckState extends State {
        private DelayedCaptiveCheckState() {
        }

        public void enter() {
            CaptivePortalTracker captivePortalTracker = CaptivePortalTracker.this;
            Message messageObtainMessage = captivePortalTracker.obtainMessage(2, CaptivePortalTracker.access$2204(captivePortalTracker), 0);
            if (CaptivePortalTracker.this.mDeviceProvisioned) {
                CaptivePortalTracker.this.sendMessageDelayed(messageObtainMessage, 10000L);
            } else {
                CaptivePortalTracker.this.sendMessage(messageObtainMessage);
            }
        }

        public boolean processMessage(Message message) {
            CaptivePortalTracker.this.log(getName() + message.toString());
            boolean z = false;
            if (message.what != 2) {
                return false;
            }
            CaptivePortalTracker.this.setNotificationOff();
            if (message.arg1 == CaptivePortalTracker.this.mDelayedCheckToken) {
                CaptivePortalTracker captivePortalTracker = CaptivePortalTracker.this;
                InetAddress inetAddressLookupHost = captivePortalTracker.lookupHost(captivePortalTracker.mServer);
                if (inetAddressLookupHost != null && CaptivePortalTracker.this.isCaptivePortal(inetAddressLookupHost)) {
                    z = true;
                }
                if (z) {
                    CaptivePortalTracker.this.log("Captive network " + CaptivePortalTracker.this.mNetworkInfo);
                } else {
                    CaptivePortalTracker.this.log("Not captive network " + CaptivePortalTracker.this.mNetworkInfo);
                }
                CaptivePortalTracker captivePortalTracker2 = CaptivePortalTracker.this;
                captivePortalTracker2.notifyPortalCheckCompleted(captivePortalTracker2.mNetworkInfo, z);
                if (!CaptivePortalTracker.this.mDeviceProvisioned) {
                    Intent intent = new Intent(ConnectivityManager.ACTION_CAPTIVE_PORTAL_TEST_COMPLETED);
                    intent.putExtra(ConnectivityManager.EXTRA_IS_CAPTIVE_PORTAL, z);
                    intent.setPackage(CaptivePortalTracker.SETUP_WIZARD_PACKAGE);
                    CaptivePortalTracker.this.mContext.sendBroadcast(intent);
                } else if (z) {
                    try {
                        CaptivePortalTracker.this.mConnService.setProvisioningNotificationVisible(true, CaptivePortalTracker.this.mNetworkInfo.getType(), CaptivePortalTracker.this.mNetworkInfo.getExtraInfo(), CaptivePortalTracker.this.mUrl);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                CaptivePortalTracker captivePortalTracker3 = CaptivePortalTracker.this;
                captivePortalTracker3.transitionTo(captivePortalTracker3.mActiveNetworkState);
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyPortalCheckComplete(NetworkInfo networkInfo) {
        if (networkInfo == null) {
            loge("notifyPortalCheckComplete on null");
            return;
        }
        try {
            log("notifyPortalCheckComplete: ni=" + networkInfo);
            this.mConnService.captivePortalCheckComplete(networkInfo);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyPortalCheckCompleted(NetworkInfo networkInfo, boolean z) {
        if (networkInfo == null) {
            loge("notifyPortalCheckComplete on null");
            return;
        }
        try {
            log("notifyPortalCheckCompleted: captive=" + z + " ni=" + networkInfo);
            this.mConnService.captivePortalCheckCompleted(networkInfo, z);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isActiveNetwork(NetworkInfo networkInfo) {
        try {
            NetworkInfo activeNetworkInfo = this.mConnService.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.getType() == networkInfo.getType();
            }
            return false;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNotificationOff() {
        try {
            NetworkInfo networkInfo = this.mNetworkInfo;
            if (networkInfo != null) {
                this.mConnService.setProvisioningNotificationVisible(false, networkInfo.getType(), null, null);
            }
        } catch (RemoteException e) {
            log("setNotificationOff: " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCaptivePortal(InetAddress inetAddress) throws Throwable {
        long jElapsedRealtime;
        if (!this.mIsCaptivePortalCheckEnabled) {
            return false;
        }
        this.mUrl = "http://" + inetAddress.getHostAddress() + "/generate_204";
        log("Checking " + this.mUrl);
        HttpURLConnection httpURLConnection = null;
        try {
            try {
                HttpURLConnection httpURLConnection2 = (HttpURLConnection) new URL(this.mUrl).openConnection();
                try {
                    try {
                        httpURLConnection2.setInstanceFollowRedirects(false);
                        httpURLConnection2.setConnectTimeout(10000);
                        httpURLConnection2.setReadTimeout(10000);
                        httpURLConnection2.setUseCaches(false);
                        jElapsedRealtime = SystemClock.elapsedRealtime();
                    } catch (IOException e) {
                        e = e;
                        jElapsedRealtime = -1;
                    }
                    try {
                        httpURLConnection2.getInputStream();
                        long jElapsedRealtime2 = SystemClock.elapsedRealtime();
                        int responseCode = httpURLConnection2.getResponseCode();
                        boolean z = responseCode != 204;
                        sendNetworkConditionsBroadcast(true, z, jElapsedRealtime, jElapsedRealtime2);
                        log("isCaptivePortal: ret=" + z + " rspCode=" + responseCode);
                        if (httpURLConnection2 != null) {
                            httpURLConnection2.disconnect();
                        }
                        return z;
                    } catch (IOException e2) {
                        e = e2;
                        httpURLConnection = httpURLConnection2;
                        log("Probably not a portal: exception " + e);
                        if (jElapsedRealtime != -1) {
                            sendFailedCaptivePortalCheckBroadcast(jElapsedRealtime);
                        }
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                        return false;
                    }
                } catch (Throwable th) {
                    th = th;
                    httpURLConnection = httpURLConnection2;
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e3) {
            e = e3;
            jElapsedRealtime = -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public InetAddress lookupHost(String str) {
        try {
            for (InetAddress inetAddress : InetAddress.getAllByName(str)) {
                if (inetAddress instanceof Inet4Address) {
                    return inetAddress;
                }
            }
            sendFailedCaptivePortalCheckBroadcast(SystemClock.elapsedRealtime());
            return null;
        } catch (UnknownHostException unused) {
            sendFailedCaptivePortalCheckBroadcast(SystemClock.elapsedRealtime());
            return null;
        }
    }

    private void sendFailedCaptivePortalCheckBroadcast(long j) {
        sendNetworkConditionsBroadcast(false, false, j, 0L);
    }

    private void sendNetworkConditionsBroadcast(boolean z, boolean z2, long j, long j2) {
        int i = 0;
        if (Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.WIFI_SCAN_ALWAYS_AVAILABLE, 0) == 0) {
            log("Don't send network conditions - lacking user consent.");
            return;
        }
        Intent intent = new Intent(ACTION_NETWORK_CONDITIONS_MEASURED);
        int type = this.mNetworkInfo.getType();
        if (type != 0) {
            if (type != 1) {
                return;
            }
            WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
            if (connectionInfo != null) {
                intent.putExtra(EXTRA_SSID, connectionInfo.getSSID());
                intent.putExtra(EXTRA_BSSID, connectionInfo.getBSSID());
            } else {
                logw("network info is TYPE_WIFI but no ConnectionInfo found");
                return;
            }
        } else {
            intent.putExtra(EXTRA_NETWORK_TYPE, this.mTelephonyManager.getNetworkType());
            List<CellInfo> allCellInfo = this.mTelephonyManager.getAllCellInfo();
            if (allCellInfo == null) {
                return;
            }
            for (CellInfo cellInfo : allCellInfo) {
                if (cellInfo.isRegistered()) {
                    i++;
                    if (i > 1) {
                        log("more than one registered CellInfo.  Can't tell which is active.  Bailing.");
                        return;
                    }
                    if (cellInfo instanceof CellInfoCdma) {
                        intent.putExtra(EXTRA_CELL_ID, ((CellInfoCdma) cellInfo).getCellIdentity());
                    } else if (cellInfo instanceof CellInfoGsm) {
                        intent.putExtra(EXTRA_CELL_ID, ((CellInfoGsm) cellInfo).getCellIdentity());
                    } else if (cellInfo instanceof CellInfoLte) {
                        intent.putExtra(EXTRA_CELL_ID, ((CellInfoLte) cellInfo).getCellIdentity());
                    } else if (cellInfo instanceof CellInfoWcdma) {
                        intent.putExtra(EXTRA_CELL_ID, ((CellInfoWcdma) cellInfo).getCellIdentity());
                    } else {
                        logw("Registered cellinfo is unrecognized");
                        return;
                    }
                }
            }
        }
        intent.putExtra(EXTRA_CONNECTIVITY_TYPE, this.mNetworkInfo.getType());
        intent.putExtra(EXTRA_RESPONSE_RECEIVED, z);
        intent.putExtra(EXTRA_REQUEST_TIMESTAMP_MS, j);
        if (z) {
            intent.putExtra(EXTRA_IS_CAPTIVE_PORTAL, z2);
            intent.putExtra(EXTRA_RESPONSE_TIMESTAMP_MS, j2);
        }
        this.mContext.sendBroadcast(intent, "android.permission.ACCESS_NETWORK_CONDITIONS");
    }
}
