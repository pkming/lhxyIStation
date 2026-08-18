package android.net.wifi;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.backup.IBackupManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.DhcpResults;
import android.net.DhcpStateMachine;
import android.net.InterfaceConfiguration;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.Uri;
import android.net.wifi.WpsResult;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pService;
import android.os.BatteryStats;
import android.os.Bundle;
import android.os.INetworkManagementService;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.Settings;
import android.util.LruCache;
import android.util.TimedRemoteCaller;
import com.android.internal.app.IBatteryStats;
import com.android.internal.util.AsyncChannel;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.android.server.net.BaseNetworkObserver;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes.dex */
public class WifiStateMachine extends StateMachine {
    private static final String ACTION_DELAYED_DRIVER_STOP = "com.android.server.WifiManager.action.DELAYED_DRIVER_STOP";
    private static final String ACTION_REFRESH_BATCHED_SCAN = "com.android.server.WifiManager.action.REFRESH_BATCHED_SCAN";
    private static final String ACTION_START_SCAN = "com.android.server.WifiManager.action.START_SCAN";
    static final int BASE = 131072;
    private static final String BATCHED_SETTING = "batched_settings";
    private static final String BATCHED_WORKSOURCE = "batched_worksource";
    private static final String BSSID_STR = "bssid=";
    static final int CMD_ADD_OR_UPDATE_NETWORK = 131124;
    static final int CMD_BLACKLIST_NETWORK = 131128;
    static final int CMD_BLUETOOTH_ADAPTER_STATE_CHANGE = 131103;
    public static final int CMD_BOOT_COMPLETED = 131206;
    static final int CMD_CAPTIVE_CHECK_COMPLETE = 131092;
    static final int CMD_CLEAR_BLACKLIST = 131129;
    static final int CMD_DELAYED_STOP_DRIVER = 131090;
    public static final int CMD_DISABLE_P2P_REQ = 131204;
    public static final int CMD_DISABLE_P2P_RSP = 131205;
    static final int CMD_DISCONNECT = 131145;
    static final int CMD_DRIVER_START_TIMED_OUT = 131091;
    static final int CMD_ENABLE_ALL_NETWORKS = 131127;
    static final int CMD_ENABLE_BACKGROUND_SCAN = 131163;
    static final int CMD_ENABLE_NETWORK = 131126;
    public static final int CMD_ENABLE_P2P = 131203;
    static final int CMD_ENABLE_RSSI_POLL = 131154;
    static final int CMD_ENABLE_TDLS = 131164;
    static final int CMD_GET_CONFIGURED_NETWORKS = 131131;
    static final int CMD_IP_ADDRESS_REMOVED = 131213;
    static final int CMD_IP_ADDRESS_UPDATED = 131212;
    static final int CMD_NO_NETWORKS_PERIODIC_SCAN = 131160;
    static final int CMD_PING_SUPPLICANT = 131123;
    public static final int CMD_POLL_BATCHED_SCAN = 131209;
    static final int CMD_REASSOCIATE = 131147;
    static final int CMD_RECONNECT = 131146;
    static final int CMD_RELOAD_TLS_AND_RECONNECT = 131214;
    static final int CMD_REMOVE_NETWORK = 131125;
    static final int CMD_REQUEST_AP_CONFIG = 131099;
    static final int CMD_RESET_SUPPLICANT_STATE = 131183;
    static final int CMD_RESPONSE_AP_CONFIG = 131100;
    static final int CMD_RSSI_POLL = 131155;
    static final int CMD_SAVE_CONFIG = 131130;
    static final int CMD_SET_AP_CONFIG = 131097;
    static final int CMD_SET_AP_CONFIG_COMPLETED = 131098;
    public static final int CMD_SET_BATCHED_SCAN = 131207;
    static final int CMD_SET_COUNTRY_CODE = 131152;
    static final int CMD_SET_FREQUENCY_BAND = 131162;
    static final int CMD_SET_HIGH_PERF_MODE = 131149;
    static final int CMD_SET_OPERATIONAL_MODE = 131144;
    static final int CMD_SET_SUSPEND_OPT_ENABLED = 131158;
    static final int CMD_START_AP = 131093;
    static final int CMD_START_AP_FAILURE = 131095;
    static final int CMD_START_AP_SUCCESS = 131094;
    static final int CMD_START_DRIVER = 131085;
    public static final int CMD_START_NEXT_BATCHED_SCAN = 131208;
    static final int CMD_START_PACKET_FILTERING = 131156;
    static final int CMD_START_SCAN = 131143;
    static final int CMD_START_SUPPLICANT = 131083;
    static final int CMD_STATIC_IP_FAILURE = 131088;
    static final int CMD_STATIC_IP_SUCCESS = 131087;
    static final int CMD_STOP_AP = 131096;
    static final int CMD_STOP_DRIVER = 131086;
    static final int CMD_STOP_PACKET_FILTERING = 131157;
    static final int CMD_STOP_SUPPLICANT = 131084;
    static final int CMD_STOP_SUPPLICANT_FAILED = 131089;
    static final int CMD_TETHER_NOTIFICATION_TIMED_OUT = 131102;
    static final int CMD_TETHER_STATE_CHANGE = 131101;
    public static final int CONNECT_MODE = 1;
    private static final boolean DBG = false;
    private static final boolean DEBUG_PARSE = false;
    private static final int DEFAULT_MAX_DHCP_RETRIES = 9;
    private static final String DELAYED_STOP_COUNTER = "DelayedStopCounter";
    private static final String DELIMITER_STR = "====";
    private static final int DRIVER_START_TIME_OUT_MSECS = 10000;
    private static final int DRIVER_STOP_REQUEST = 0;
    private static final String END_STR = "####";
    private static final int FAILURE = -1;
    private static final String FLAGS_STR = "flags=";
    private static final String FREQ_STR = "freq=";
    private static final String ID_STR = "id=";
    private static final String LEVEL_STR = "level=";
    private static final int MAX_RSSI = 256;
    private static final int MIN_INTERVAL_ENABLE_ALL_NETWORKS_MS = 600000;
    private static final int MIN_RSSI = -200;
    static final int MULTICAST_V4 = 0;
    static final int MULTICAST_V6 = 1;
    private static final String NETWORKTYPE = "WIFI";
    private static final int POLL_RSSI_INTERVAL_MSECS = 3000;
    public static final int SCAN_ONLY_MODE = 2;
    public static final int SCAN_ONLY_WITH_WIFI_OFF_MODE = 3;
    private static final int SCAN_REQUEST = 0;
    private static final int SCAN_RESULT_CACHE_SIZE = 80;
    private static final String SSID_STR = "ssid=";
    private static final int SUCCESS = 1;
    private static final int SUPPLICANT_RESTART_INTERVAL_MSECS = 5000;
    private static final int SUPPLICANT_RESTART_TRIES = 5;
    private static final int SUSPEND_DUE_TO_DHCP = 1;
    private static final int SUSPEND_DUE_TO_HIGH_PERF = 2;
    private static final int SUSPEND_DUE_TO_SCREEN = 4;
    private static final int TETHER_NOTIFICATION_TIME_OUT_MSECS = 5000;
    private static final String TSF_STR = "tsf=";
    private static final int UNKNOWN_SCAN_SOURCE = -1;
    private static final Pattern scanResultPattern = Pattern.compile("\t+");
    private AlarmManager mAlarmManager;
    private final boolean mBackgroundScanSupported;
    private int mBatchedScanCsph;
    private PendingIntent mBatchedScanIntervalIntent;
    private long mBatchedScanMinPollTime;
    private int mBatchedScanOwnerUid;
    private final List<BatchedScanResult> mBatchedScanResults;
    private BatchedScanSettings mBatchedScanSettings;
    private WorkSource mBatchedScanWorkSource;
    private final IBatteryStats mBatteryStats;
    private boolean mBluetoothConnectionActive;
    private State mCaptivePortalCheckState;
    private ConnectivityManager mCm;
    private State mConnectModeState;
    private State mConnectedState;
    private Context mContext;
    private final int mDefaultFrameworkScanIntervalMs;
    private State mDefaultState;
    private int mDelayedStopCounter;
    private boolean mDhcpActive;
    private DhcpResults mDhcpResults;
    private final Object mDhcpResultsLock;
    private DhcpStateMachine mDhcpStateMachine;
    private State mDisconnectedState;
    private State mDisconnectingState;
    private int mDriverStartToken;
    private State mDriverStartedState;
    private State mDriverStartingState;
    private final int mDriverStopDelayMs;
    private PendingIntent mDriverStopIntent;
    private State mDriverStoppedState;
    private State mDriverStoppingState;
    private boolean mEnableBackgroundScan;
    private boolean mEnableRssiPolling;
    private int mExpectedBatchedScans;
    private AtomicBoolean mFilteringMulticastV4Packets;
    private AtomicInteger mFrequencyBand;
    private boolean mInDelayedStop;
    private State mInitialState;
    private String mInterfaceName;
    private InterfaceObserver mInterfaceObserver;
    private boolean mIsRunning;
    private State mL2ConnectedState;
    private String mLastBssid;
    private long mLastEnableAllNetworksTime;
    private int mLastNetworkId;
    private final WorkSource mLastRunningWifiUids;
    private String mLastSetCountryCode;
    private int mLastSignalLevel;
    private LinkProperties mLinkProperties;
    private final LinkProperties mNetlinkLinkProperties;
    private NetworkInfo mNetworkInfo;
    private int mNotedBatchedScanCsph;
    private WorkSource mNotedBatchedScanWorkSource;
    private INetworkManagementService mNwService;
    private State mObtainingIpState;
    private int mOperationalMode;
    private final AtomicBoolean mP2pConnected;
    private final boolean mP2pSupported;
    private int mPeriodicScanToken;
    private volatile String mPersistedCountryCode;
    private final String mPrimaryDeviceType;
    private int mReconnectCount;
    private AsyncChannel mReplyChannel;
    private boolean mReportedRunning;
    private int mRssiPollToken;
    private final WorkSource mRunningWifiUids;
    private PendingIntent mScanIntent;
    private State mScanModeState;
    private final LruCache<String, ScanResult> mScanResultCache;
    private boolean mScanResultIsPending;
    private List<ScanResult> mScanResults;
    private WorkSource mScanWorkSource;
    private AtomicBoolean mScreenBroadcastReceived;
    private State mSoftApStartedState;
    private State mSoftApStartingState;
    private int mSupplicantRestartCount;
    private long mSupplicantScanIntervalMs;
    private State mSupplicantStartedState;
    private State mSupplicantStartingState;
    private SupplicantStateTracker mSupplicantStateTracker;
    private int mSupplicantStopFailureToken;
    private State mSupplicantStoppingState;
    private int mSuspendOptNeedsDisabled;
    private PowerManager.WakeLock mSuspendWakeLock;
    private boolean mTemporarilyDisconnectWifi;
    private String mTetherInterfaceName;
    private int mTetherToken;
    private State mTetheredState;
    private State mTetheringState;
    private State mUntetheringState;
    private AtomicBoolean mUserWantsSuspendOpt;
    private State mVerifyingLinkState;
    private State mWaitForP2pDisableState;
    private PowerManager.WakeLock mWakeLock;
    private AsyncChannel mWifiApConfigChannel;
    private final AtomicInteger mWifiApState;
    private WifiConfigStore mWifiConfigStore;
    private WifiInfo mWifiInfo;
    private WifiMonitor mWifiMonitor;
    private WifiNative mWifiNative;
    private AsyncChannel mWifiP2pChannel;
    private WifiP2pManager mWifiP2pManager;
    private final AtomicInteger mWifiState;
    private State mWpsRunningState;

    static /* synthetic */ int access$10408(WifiStateMachine wifiStateMachine) {
        int i = wifiStateMachine.mDelayedStopCounter;
        wifiStateMachine.mDelayedStopCounter = i + 1;
        return i;
    }

    static /* synthetic */ int access$16508(WifiStateMachine wifiStateMachine) {
        int i = wifiStateMachine.mRssiPollToken;
        wifiStateMachine.mRssiPollToken = i + 1;
        return i;
    }

    static /* synthetic */ int access$20904(WifiStateMachine wifiStateMachine) {
        int i = wifiStateMachine.mPeriodicScanToken + 1;
        wifiStateMachine.mPeriodicScanToken = i;
        return i;
    }

    static /* synthetic */ int access$23204(WifiStateMachine wifiStateMachine) {
        int i = wifiStateMachine.mTetherToken + 1;
        wifiStateMachine.mTetherToken = i;
        return i;
    }

    static /* synthetic */ int access$5504(WifiStateMachine wifiStateMachine) {
        int i = wifiStateMachine.mSupplicantRestartCount + 1;
        wifiStateMachine.mSupplicantRestartCount = i;
        return i;
    }

    static /* synthetic */ int access$8704(WifiStateMachine wifiStateMachine) {
        int i = wifiStateMachine.mSupplicantStopFailureToken + 1;
        wifiStateMachine.mSupplicantStopFailureToken = i;
        return i;
    }

    static /* synthetic */ int access$9304(WifiStateMachine wifiStateMachine) {
        int i = wifiStateMachine.mDriverStartToken + 1;
        wifiStateMachine.mDriverStartToken = i;
        return i;
    }

    private class InterfaceObserver extends BaseNetworkObserver {
        private WifiStateMachine mWifiStateMachine;

        InterfaceObserver(WifiStateMachine wifiStateMachine) {
            this.mWifiStateMachine = wifiStateMachine;
        }

        public void addressUpdated(String str, String str2, int i, int i2) {
            if (this.mWifiStateMachine.mInterfaceName.equals(str2)) {
                this.mWifiStateMachine.sendMessage(WifiStateMachine.CMD_IP_ADDRESS_UPDATED, new LinkAddress(str));
            }
        }

        public void addressRemoved(String str, String str2, int i, int i2) {
            if (this.mWifiStateMachine.mInterfaceName.equals(str2)) {
                this.mWifiStateMachine.sendMessage(WifiStateMachine.CMD_IP_ADDRESS_REMOVED, new LinkAddress(str));
            }
        }
    }

    private class TetherStateChange {
        ArrayList<String> active;
        ArrayList<String> available;

        TetherStateChange(ArrayList<String> arrayList, ArrayList<String> arrayList2) {
            this.available = arrayList;
            this.active = arrayList2;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r8v7, types: [android.net.INetworkManagementEventObserver, android.net.wifi.WifiStateMachine$InterfaceObserver] */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public WifiStateMachine(Context context, String str) {
        super("WifiStateMachine");
        this.mP2pConnected = new AtomicBoolean(false);
        this.mTemporarilyDisconnectWifi = false;
        this.mScanResults = new ArrayList();
        this.mBatchedScanResults = new ArrayList();
        this.mBatchedScanOwnerUid = -1;
        this.mExpectedBatchedScans = 0;
        this.mBatchedScanMinPollTime = 0L;
        this.mLastSignalLevel = -1;
        this.mEnableRssiPolling = false;
        this.mEnableBackgroundScan = false;
        this.mRssiPollToken = 0;
        this.mReconnectCount = 0;
        this.mOperationalMode = 1;
        this.mScanResultIsPending = false;
        this.mScanWorkSource = null;
        this.mScreenBroadcastReceived = new AtomicBoolean(false);
        this.mBluetoothConnectionActive = false;
        this.mSupplicantRestartCount = 0;
        this.mSupplicantStopFailureToken = 0;
        this.mTetherToken = 0;
        this.mDriverStartToken = 0;
        this.mPeriodicScanToken = 0;
        this.mDhcpResultsLock = new Object();
        this.mDhcpActive = false;
        this.mFrequencyBand = new AtomicInteger(0);
        this.mFilteringMulticastV4Packets = new AtomicBoolean(true);
        this.mReplyChannel = new AsyncChannel();
        this.mSuspendOptNeedsDisabled = 0;
        this.mUserWantsSuspendOpt = new AtomicBoolean(true);
        this.mInDelayedStop = false;
        this.mDefaultState = new DefaultState();
        this.mInitialState = new InitialState();
        this.mSupplicantStartingState = new SupplicantStartingState();
        this.mSupplicantStartedState = new SupplicantStartedState();
        this.mSupplicantStoppingState = new SupplicantStoppingState();
        this.mDriverStartingState = new DriverStartingState();
        this.mDriverStartedState = new DriverStartedState();
        this.mWaitForP2pDisableState = new WaitForP2pDisableState();
        this.mDriverStoppingState = new DriverStoppingState();
        this.mDriverStoppedState = new DriverStoppedState();
        this.mScanModeState = new ScanModeState();
        this.mConnectModeState = new ConnectModeState();
        this.mL2ConnectedState = new L2ConnectedState();
        this.mObtainingIpState = new ObtainingIpState();
        this.mVerifyingLinkState = new VerifyingLinkState();
        this.mCaptivePortalCheckState = new CaptivePortalCheckState();
        this.mConnectedState = new ConnectedState();
        this.mDisconnectingState = new DisconnectingState();
        this.mDisconnectedState = new DisconnectedState();
        this.mWpsRunningState = new WpsRunningState();
        this.mSoftApStartingState = new SoftApStartingState();
        this.mSoftApStartedState = new SoftApStartedState();
        this.mTetheringState = new TetheringState();
        this.mTetheredState = new TetheredState();
        this.mUntetheringState = new UntetheringState();
        this.mWifiState = new AtomicInteger(1);
        this.mWifiApState = new AtomicInteger(11);
        this.mIsRunning = false;
        this.mReportedRunning = false;
        this.mRunningWifiUids = new WorkSource();
        this.mLastRunningWifiUids = new WorkSource();
        this.mBatchedScanSettings = null;
        this.mBatchedScanWorkSource = null;
        this.mBatchedScanCsph = 0;
        this.mNotedBatchedScanWorkSource = null;
        this.mNotedBatchedScanCsph = 0;
        this.mContext = context;
        this.mInterfaceName = str;
        this.mNetworkInfo = new NetworkInfo(1, 0, NETWORKTYPE, "");
        this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService(BatteryStats.SERVICE_NAME));
        this.mNwService = INetworkManagementService.Stub.asInterface(ServiceManager.getService(Context.NETWORKMANAGEMENT_SERVICE));
        this.mP2pSupported = this.mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT);
        this.mWifiNative = new WifiNative(this.mInterfaceName);
        this.mWifiConfigStore = new WifiConfigStore(context, this.mWifiNative);
        this.mWifiMonitor = new WifiMonitor(this, this.mWifiNative);
        this.mWifiInfo = new WifiInfo();
        this.mSupplicantStateTracker = new SupplicantStateTracker(context, this, this.mWifiConfigStore, getHandler());
        this.mLinkProperties = new LinkProperties();
        this.mNetlinkLinkProperties = new LinkProperties();
        this.mWifiP2pManager = (WifiP2pManager) this.mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        this.mNetworkInfo.setIsAvailable(false);
        this.mLastBssid = null;
        this.mLastNetworkId = -1;
        this.mLastSignalLevel = -1;
        ?? interfaceObserver = new InterfaceObserver(this);
        this.mInterfaceObserver = interfaceObserver;
        try {
            this.mNwService.registerObserver(interfaceObserver);
        } catch (RemoteException e) {
            loge("Couldn't register interface observer: " + e.toString());
        }
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        this.mScanIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_START_SCAN, (Uri) null), 0);
        this.mBatchedScanIntervalIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_REFRESH_BATCHED_SCAN, (Uri) null), 0);
        this.mDefaultFrameworkScanIntervalMs = this.mContext.getResources().getInteger(17694737);
        this.mDriverStopDelayMs = this.mContext.getResources().getInteger(17694738);
        this.mBackgroundScanSupported = this.mContext.getResources().getBoolean(17891352);
        this.mPrimaryDeviceType = this.mContext.getResources().getString(17039386);
        this.mUserWantsSuspendOpt.set(Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.WIFI_SUSPEND_OPTIMIZATIONS_ENABLED, 1) == 1);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: android.net.wifi.WifiStateMachine.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_TETHER_STATE_CHANGE, WifiStateMachine.this.new TetherStateChange(intent.getStringArrayListExtra(ConnectivityManager.EXTRA_AVAILABLE_TETHER), intent.getStringArrayListExtra(ConnectivityManager.EXTRA_ACTIVE_TETHER)));
            }
        }, new IntentFilter(ConnectivityManager.ACTION_TETHER_STATE_CHANGED));
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: android.net.wifi.WifiStateMachine.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                WifiStateMachine.this.startScan(-1, null);
            }
        }, new IntentFilter(ACTION_START_SCAN));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(ACTION_REFRESH_BATCHED_SCAN);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: android.net.wifi.WifiStateMachine.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_SCREEN_ON)) {
                    WifiStateMachine.this.handleScreenStateChanged(true);
                } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                    WifiStateMachine.this.handleScreenStateChanged(false);
                } else if (action.equals(WifiStateMachine.ACTION_REFRESH_BATCHED_SCAN)) {
                    WifiStateMachine.this.startNextBatchedScanAsync();
                }
            }
        }, intentFilter);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: android.net.wifi.WifiStateMachine.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_DELAYED_STOP_DRIVER, intent.getIntExtra(WifiStateMachine.DELAYED_STOP_COUNTER, 0), 0);
            }
        }, new IntentFilter(ACTION_DELAYED_DRIVER_STOP));
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor(Settings.Global.WIFI_SUSPEND_OPTIMIZATIONS_ENABLED), false, new ContentObserver(getHandler()) { // from class: android.net.wifi.WifiStateMachine.5
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                WifiStateMachine.this.mUserWantsSuspendOpt.set(Settings.Global.getInt(WifiStateMachine.this.mContext.getContentResolver(), Settings.Global.WIFI_SUSPEND_OPTIMIZATIONS_ENABLED, 1) == 1);
            }
        });
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: android.net.wifi.WifiStateMachine.6
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_BOOT_COMPLETED);
            }
        }, new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
        this.mScanResultCache = new LruCache<>(80);
        PowerManager powerManager = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = powerManager.newWakeLock(1, getName());
        PowerManager.WakeLock wakeLockNewWakeLock = powerManager.newWakeLock(1, "WifiSuspend");
        this.mSuspendWakeLock = wakeLockNewWakeLock;
        wakeLockNewWakeLock.setReferenceCounted(false);
        addState(this.mDefaultState);
        addState(this.mInitialState, this.mDefaultState);
        addState(this.mSupplicantStartingState, this.mDefaultState);
        addState(this.mSupplicantStartedState, this.mDefaultState);
        addState(this.mDriverStartingState, this.mSupplicantStartedState);
        addState(this.mDriverStartedState, this.mSupplicantStartedState);
        addState(this.mScanModeState, this.mDriverStartedState);
        addState(this.mConnectModeState, this.mDriverStartedState);
        addState(this.mL2ConnectedState, this.mConnectModeState);
        addState(this.mObtainingIpState, this.mL2ConnectedState);
        addState(this.mVerifyingLinkState, this.mL2ConnectedState);
        addState(this.mCaptivePortalCheckState, this.mL2ConnectedState);
        addState(this.mConnectedState, this.mL2ConnectedState);
        addState(this.mDisconnectingState, this.mConnectModeState);
        addState(this.mDisconnectedState, this.mConnectModeState);
        addState(this.mWpsRunningState, this.mConnectModeState);
        addState(this.mWaitForP2pDisableState, this.mSupplicantStartedState);
        addState(this.mDriverStoppingState, this.mSupplicantStartedState);
        addState(this.mDriverStoppedState, this.mSupplicantStartedState);
        addState(this.mSupplicantStoppingState, this.mDefaultState);
        addState(this.mSoftApStartingState, this.mDefaultState);
        addState(this.mSoftApStartedState, this.mDefaultState);
        addState(this.mTetheringState, this.mSoftApStartedState);
        addState(this.mTetheredState, this.mSoftApStartedState);
        addState(this.mUntetheringState, this.mSoftApStartedState);
        setInitialState(this.mInitialState);
        setLogRecSize(2000);
        setLogOnlyTransitions(false);
        start();
        Intent intent = new Intent(WifiManager.WIFI_SCAN_AVAILABLE);
        intent.addFlags(67108864);
        intent.putExtra(WifiManager.EXTRA_SCAN_AVAILABLE, 1);
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    public Messenger getMessenger() {
        return new Messenger(getHandler());
    }

    public boolean syncPingSupplicant(AsyncChannel asyncChannel) {
        Message messageSendMessageSynchronously = asyncChannel.sendMessageSynchronously(CMD_PING_SUPPLICANT);
        boolean z = messageSendMessageSynchronously.arg1 != -1;
        messageSendMessageSynchronously.recycle();
        return z;
    }

    public void startScan(int i, WorkSource workSource) {
        sendMessage(CMD_START_SCAN, i, 0, workSource);
    }

    public void setBatchedScanSettings(BatchedScanSettings batchedScanSettings, int i, int i2, WorkSource workSource) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BATCHED_SETTING, batchedScanSettings);
        bundle.putParcelable(BATCHED_WORKSOURCE, workSource);
        sendMessage(CMD_SET_BATCHED_SCAN, i, i2, bundle);
    }

    public List<BatchedScanResult> syncGetBatchedScanResultsList() {
        ArrayList arrayList;
        synchronized (this.mBatchedScanResults) {
            arrayList = new ArrayList(this.mBatchedScanResults.size());
            Iterator<BatchedScanResult> it = this.mBatchedScanResults.iterator();
            while (it.hasNext()) {
                arrayList.add(new BatchedScanResult(it.next()));
            }
        }
        return arrayList;
    }

    public void requestBatchedScanPoll() {
        sendMessage(CMD_POLL_BATCHED_SCAN);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startBatchedScan() {
        if (this.mBatchedScanSettings == null || this.mDhcpActive) {
            return;
        }
        retrieveBatchedScanData();
        this.mAlarmManager.cancel(this.mBatchedScanIntervalIntent);
        try {
            int i = Integer.parseInt(this.mWifiNative.setBatchedScanSettings(this.mBatchedScanSettings));
            this.mExpectedBatchedScans = i;
            setNextBatchedAlarm(i);
            if (this.mExpectedBatchedScans > 0) {
                noteBatchedScanStart();
            }
        } catch (NumberFormatException e) {
            stopBatchedScan();
            loge("Exception parsing WifiNative.setBatchedScanSettings response " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startNextBatchedScanAsync() {
        sendMessage(CMD_START_NEXT_BATCHED_SCAN);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startNextBatchedScan() {
        retrieveBatchedScanData();
        setNextBatchedAlarm(this.mExpectedBatchedScans);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBatchedScanPollRequest() {
        if (this.mBatchedScanMinPollTime == 0 || this.mBatchedScanSettings == null) {
            return;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j = this.mBatchedScanMinPollTime;
        if (jCurrentTimeMillis > j) {
            startNextBatchedScan();
        } else {
            this.mAlarmManager.setExact(0, j, this.mBatchedScanIntervalIntent);
            this.mBatchedScanMinPollTime = 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean recordBatchedScanSettings(int i, int i2, Bundle bundle) {
        BatchedScanSettings batchedScanSettings = (BatchedScanSettings) bundle.getParcelable(BATCHED_SETTING);
        WorkSource workSource = (WorkSource) bundle.getParcelable(BATCHED_WORKSOURCE);
        if (batchedScanSettings != null) {
            if (batchedScanSettings.equals(this.mBatchedScanSettings)) {
                return false;
            }
        } else if (this.mBatchedScanSettings == null) {
            return false;
        }
        this.mBatchedScanSettings = batchedScanSettings;
        if (workSource == null) {
            workSource = new WorkSource(i);
        }
        this.mBatchedScanWorkSource = workSource;
        this.mBatchedScanCsph = i2;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopBatchedScan() {
        this.mAlarmManager.cancel(this.mBatchedScanIntervalIntent);
        retrieveBatchedScanData();
        this.mWifiNative.setBatchedScanSettings(null);
        noteBatchedScanStop();
    }

    private void setNextBatchedAlarm(int i) {
        if (this.mBatchedScanSettings == null || i < 1) {
            return;
        }
        this.mBatchedScanMinPollTime = System.currentTimeMillis() + ((long) (this.mBatchedScanSettings.scanIntervalSec * 1000));
        if (this.mBatchedScanSettings.maxScansPerBatch < i) {
            i = this.mBatchedScanSettings.maxScansPerBatch;
        }
        int i2 = this.mBatchedScanSettings.scanIntervalSec * i;
        int i3 = SystemProperties.getInt("wifi.batchedScan.pollPeriod", 0);
        if (i3 > 0) {
            i2 = i3;
        }
        this.mAlarmManager.setExact(0, System.currentTimeMillis() + ((long) ((i2 - (this.mBatchedScanSettings.scanIntervalSec / 2)) * 1000)), this.mBatchedScanIntervalIntent);
    }

    private void retrieveBatchedScanData() {
        int i;
        int i2;
        String batchedScanResults = this.mWifiNative.getBatchedScanResults();
        this.mBatchedScanMinPollTime = 0L;
        if (batchedScanResults == null || batchedScanResults.equalsIgnoreCase("OK")) {
            loge("Unexpected BatchedScanResults :" + batchedScanResults);
            return;
        }
        String[] strArrSplit = batchedScanResults.split("\n");
        boolean z = true;
        if (strArrSplit[0].startsWith("scancount=")) {
            try {
                i = Integer.parseInt(strArrSplit[0].substring(10));
            } catch (NumberFormatException unused) {
                loge("scancount parseInt Exception from " + strArrSplit[1]);
                i = 0;
            }
            i2 = 1;
        } else {
            log("scancount not found");
            i = 0;
            i2 = 0;
        }
        if (i == 0) {
            loge("scanCount==0 - aborting");
            return;
        }
        Intent intent = new Intent(WifiManager.BATCHED_SCAN_RESULTS_AVAILABLE_ACTION);
        intent.addFlags(67108864);
        synchronized (this.mBatchedScanResults) {
            this.mBatchedScanResults.clear();
            BatchedScanResult batchedScanResult = new BatchedScanResult();
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            long j = 0;
            int i3 = 0;
            int i4 = 0;
            int i5 = -1;
            int i6 = -1;
            WifiSsid wifiSsidCreateFromAsciiEncoded = null;
            String str = null;
            while (true) {
                if (i2 < strArrSplit.length) {
                    if (strArrSplit[i2].equals("----")) {
                        if (i2 + 1 != strArrSplit.length) {
                            loge("didn't consume " + (strArrSplit.length - i2));
                        }
                        if (this.mBatchedScanResults.size() > 0) {
                            this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
                        }
                        logd("retrieveBatchedScanResults X");
                        return;
                    }
                    if (strArrSplit[i2].equals(END_STR) || strArrSplit[i2].equals(DELIMITER_STR)) {
                        if (str != null) {
                            batchedScanResult.scanResults.add(new ScanResult(wifiSsidCreateFromAsciiEncoded, str, "", i3, i4, j, i5, i6));
                            i3 = 0;
                            i4 = 0;
                            i5 = -1;
                            i6 = -1;
                            wifiSsidCreateFromAsciiEncoded = null;
                            str = null;
                            j = 0;
                        }
                        if (strArrSplit[i2].equals(END_STR)) {
                            if (batchedScanResult.scanResults.size() != 0) {
                                this.mBatchedScanResults.add(batchedScanResult);
                                batchedScanResult = new BatchedScanResult();
                            } else {
                                logd("Found empty batch");
                            }
                        }
                    } else if (strArrSplit[i2].equals("trunc")) {
                        batchedScanResult.truncated = z;
                    } else if (strArrSplit[i2].startsWith(BSSID_STR)) {
                        str = new String(strArrSplit[i2].getBytes(), 6, strArrSplit[i2].length() - 6);
                    } else if (strArrSplit[i2].startsWith(FREQ_STR)) {
                        try {
                            i4 = Integer.parseInt(strArrSplit[i2].substring(5));
                        } catch (NumberFormatException unused2) {
                            loge("Invalid freqency: " + strArrSplit[i2]);
                            i4 = 0;
                        }
                    } else if (strArrSplit[i2].startsWith("age=")) {
                        try {
                            j = 1000 * (jElapsedRealtime - Long.parseLong(strArrSplit[i2].substring(4)));
                        } catch (NumberFormatException unused3) {
                            loge("Invalid timestamp: " + strArrSplit[i2]);
                            j = 0;
                        }
                    } else if (strArrSplit[i2].startsWith(SSID_STR)) {
                        wifiSsidCreateFromAsciiEncoded = WifiSsid.createFromAsciiEncoded(strArrSplit[i2].substring(5));
                    } else if (strArrSplit[i2].startsWith(LEVEL_STR)) {
                        try {
                            int i7 = Integer.parseInt(strArrSplit[i2].substring(6));
                            if (i7 > 0) {
                                i7 -= 256;
                            }
                            i3 = i7;
                        } catch (NumberFormatException unused4) {
                            loge("Invalid level: " + strArrSplit[i2]);
                            i3 = 0;
                        }
                    } else if (strArrSplit[i2].startsWith("dist=")) {
                        try {
                            i5 = Integer.parseInt(strArrSplit[i2].substring(5));
                        } catch (NumberFormatException unused5) {
                            loge("Invalid distance: " + strArrSplit[i2]);
                            i5 = -1;
                        }
                    } else if (strArrSplit[i2].startsWith("distSd=")) {
                        try {
                            i6 = Integer.parseInt(strArrSplit[i2].substring(7));
                        } catch (NumberFormatException unused6) {
                            loge("Invalid distanceSd: " + strArrSplit[i2]);
                            i6 = -1;
                        }
                    } else {
                        loge("Unable to parse batched scan result line: " + strArrSplit[i2]);
                    }
                    i2++;
                } else {
                    String batchedScanResults2 = this.mWifiNative.getBatchedScanResults();
                    if (batchedScanResults2 == null) {
                        loge("Unexpected null BatchedScanResults");
                        return;
                    }
                    strArrSplit = batchedScanResults2.split("\n");
                    if (strArrSplit.length == 0 || strArrSplit[0].equals("ok")) {
                        break;
                    } else {
                        i2 = 0;
                    }
                }
                z = true;
            }
            loge("batch scan results just ended!");
            if (this.mBatchedScanResults.size() > 0) {
                this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void noteScanStart(int i, WorkSource workSource) {
        if (this.mScanWorkSource == null) {
            if (i == -1 && workSource == null) {
                return;
            }
            if (workSource == null) {
                workSource = new WorkSource(i);
            }
            this.mScanWorkSource = workSource;
            try {
                this.mBatteryStats.noteWifiScanStartedFromSource(workSource);
            } catch (RemoteException e) {
                log(e.toString());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void noteScanEnd() {
        WorkSource workSource = this.mScanWorkSource;
        if (workSource != null) {
            try {
                try {
                    this.mBatteryStats.noteWifiScanStoppedFromSource(workSource);
                } catch (RemoteException e) {
                    log(e.toString());
                }
            } finally {
                this.mScanWorkSource = null;
            }
        }
    }

    private void noteBatchedScanStart() {
        WorkSource workSource = this.mNotedBatchedScanWorkSource;
        if (workSource != null && (!workSource.equals(this.mBatchedScanWorkSource) || this.mNotedBatchedScanCsph != this.mBatchedScanCsph)) {
            try {
                try {
                    this.mBatteryStats.noteWifiBatchedScanStoppedFromSource(this.mNotedBatchedScanWorkSource);
                } finally {
                    this.mNotedBatchedScanWorkSource = null;
                    this.mNotedBatchedScanCsph = 0;
                }
            } catch (RemoteException e) {
                log(e.toString());
            }
        }
        try {
            this.mBatteryStats.noteWifiBatchedScanStartedFromSource(this.mBatchedScanWorkSource, this.mBatchedScanCsph);
            this.mNotedBatchedScanWorkSource = this.mBatchedScanWorkSource;
            this.mNotedBatchedScanCsph = this.mBatchedScanCsph;
        } catch (RemoteException e2) {
            log(e2.toString());
        }
    }

    private void noteBatchedScanStop() {
        WorkSource workSource = this.mNotedBatchedScanWorkSource;
        if (workSource != null) {
            try {
                try {
                    this.mBatteryStats.noteWifiBatchedScanStoppedFromSource(workSource);
                } catch (RemoteException e) {
                    log(e.toString());
                }
            } finally {
                this.mNotedBatchedScanWorkSource = null;
                this.mNotedBatchedScanCsph = 0;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startScanNative(int i) {
        this.mWifiNative.scan(i);
        this.mScanResultIsPending = true;
    }

    public void setSupplicantRunning(boolean z) {
        if (z) {
            sendMessage(CMD_START_SUPPLICANT);
        } else {
            sendMessage(CMD_STOP_SUPPLICANT);
        }
    }

    public void setHostApRunning(WifiConfiguration wifiConfiguration, boolean z) {
        if (z) {
            sendMessage(CMD_START_AP, wifiConfiguration);
        } else {
            sendMessage(CMD_STOP_AP);
        }
    }

    public void setWifiApConfiguration(WifiConfiguration wifiConfiguration) {
        this.mWifiApConfigChannel.sendMessage(CMD_SET_AP_CONFIG, wifiConfiguration);
    }

    public WifiConfiguration syncGetWifiApConfiguration() {
        Message messageSendMessageSynchronously = this.mWifiApConfigChannel.sendMessageSynchronously(CMD_REQUEST_AP_CONFIG);
        WifiConfiguration wifiConfiguration = (WifiConfiguration) messageSendMessageSynchronously.obj;
        messageSendMessageSynchronously.recycle();
        return wifiConfiguration;
    }

    public int syncGetWifiState() {
        return this.mWifiState.get();
    }

    public String syncGetWifiStateByName() {
        int i = this.mWifiState.get();
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? i != 4 ? "[invalid state]" : "unknown state" : "enabled" : "enabling" : "disabled" : "disabling";
    }

    public int syncGetWifiApState() {
        return this.mWifiApState.get();
    }

    public String syncGetWifiApStateByName() {
        switch (this.mWifiApState.get()) {
            case 10:
                return "disabling";
            case 11:
                return "disabled";
            case 12:
                return "enabling";
            case 13:
                return "enabled";
            case 14:
                return "failed";
            default:
                return "[invalid state]";
        }
    }

    public WifiInfo syncRequestConnectionInfo() {
        NetworkInfo networkInfo;
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), Settings.Secure.WIFI_CHEAT_ON, 0) != 0 && (networkInfo = this.mNetworkInfo) != null && !networkInfo.isConnected()) {
            checkAndSetConnectivityInstance();
            NetworkInfo networkInfo2 = this.mCm.getNetworkInfo(9);
            if (networkInfo2 != null && networkInfo2.isConnected()) {
                WifiInfo wifiInfo = new WifiInfo();
                LinkProperties linkProperties = this.mCm.getLinkProperties(9);
                wifiInfo.setSSID(WifiSsid.createFromAsciiEncoded("android_wifi"));
                wifiInfo.setBSSID("00:11:22:33:44:55");
                wifiInfo.setNetworkId(1);
                wifiInfo.setRssi(-45);
                wifiInfo.setSupplicantState(SupplicantState.COMPLETED);
                Iterator<InetAddress> it = linkProperties.getAddresses().iterator();
                if (it.hasNext()) {
                    wifiInfo.setInetAddress(it.next());
                }
                wifiInfo.setLinkSpeed(54);
                log("syncRequestConnectionInfo: wifi disconnected, eth connected, cheated");
                log("syncRequestConnectionInfo: WifiInfo = " + wifiInfo);
                return wifiInfo;
            }
        }
        return this.mWifiInfo;
    }

    public DhcpResults syncGetDhcpResults() {
        DhcpResults dhcpResults;
        synchronized (this.mDhcpResultsLock) {
            dhcpResults = new DhcpResults(this.mDhcpResults);
        }
        return dhcpResults;
    }

    public void setDriverStart(boolean z) {
        if (z) {
            sendMessage(CMD_START_DRIVER);
        } else {
            sendMessage(CMD_STOP_DRIVER);
        }
    }

    public void captivePortalCheckComplete() {
        sendMessage(CMD_CAPTIVE_CHECK_COMPLETE);
    }

    public void setOperationalMode(int i) {
        sendMessage(CMD_SET_OPERATIONAL_MODE, i, 0);
    }

    public List<ScanResult> syncGetScanResultsList() {
        ArrayList arrayList;
        synchronized (this.mScanResultCache) {
            arrayList = new ArrayList();
            Iterator<ScanResult> it = this.mScanResults.iterator();
            while (it.hasNext()) {
                arrayList.add(new ScanResult(it.next()));
            }
        }
        return arrayList;
    }

    public void disconnectCommand() {
        sendMessage(CMD_DISCONNECT);
    }

    public void reconnectCommand() {
        sendMessage(CMD_RECONNECT);
    }

    public void reassociateCommand() {
        sendMessage(CMD_REASSOCIATE);
    }

    public void reloadTlsNetworksAndReconnect() {
        sendMessage(CMD_RELOAD_TLS_AND_RECONNECT);
    }

    public int syncAddOrUpdateNetwork(AsyncChannel asyncChannel, WifiConfiguration wifiConfiguration) {
        Message messageSendMessageSynchronously = asyncChannel.sendMessageSynchronously(CMD_ADD_OR_UPDATE_NETWORK, wifiConfiguration);
        int i = messageSendMessageSynchronously.arg1;
        messageSendMessageSynchronously.recycle();
        return i;
    }

    public List<WifiConfiguration> syncGetConfiguredNetworks(AsyncChannel asyncChannel) {
        Message messageSendMessageSynchronously = asyncChannel.sendMessageSynchronously(CMD_GET_CONFIGURED_NETWORKS);
        List<WifiConfiguration> list = (List) messageSendMessageSynchronously.obj;
        messageSendMessageSynchronously.recycle();
        return list;
    }

    public boolean syncRemoveNetwork(AsyncChannel asyncChannel, int i) {
        Message messageSendMessageSynchronously = asyncChannel.sendMessageSynchronously(CMD_REMOVE_NETWORK, i);
        boolean z = messageSendMessageSynchronously.arg1 != -1;
        messageSendMessageSynchronously.recycle();
        return z;
    }

    public boolean syncEnableNetwork(AsyncChannel asyncChannel, int i, boolean z) {
        Message messageSendMessageSynchronously = asyncChannel.sendMessageSynchronously(CMD_ENABLE_NETWORK, i, z ? 1 : 0);
        boolean z2 = messageSendMessageSynchronously.arg1 != -1;
        messageSendMessageSynchronously.recycle();
        return z2;
    }

    public boolean syncDisableNetwork(AsyncChannel asyncChannel, int i) {
        Message messageSendMessageSynchronously = asyncChannel.sendMessageSynchronously(WifiManager.DISABLE_NETWORK, i);
        boolean z = messageSendMessageSynchronously.arg1 != 151570;
        messageSendMessageSynchronously.recycle();
        return z;
    }

    public void addToBlacklist(String str) {
        sendMessage(CMD_BLACKLIST_NETWORK, str);
    }

    public void clearBlacklist() {
        sendMessage(CMD_CLEAR_BLACKLIST);
    }

    public void enableRssiPolling(boolean z) {
        sendMessage(CMD_ENABLE_RSSI_POLL, z ? 1 : 0, 0);
    }

    public void enableBackgroundScanCommand(boolean z) {
        sendMessage(CMD_ENABLE_BACKGROUND_SCAN, z ? 1 : 0, 0);
    }

    public void enableAllNetworks() {
        sendMessage(CMD_ENABLE_ALL_NETWORKS);
    }

    public void startFilteringMulticastV4Packets() {
        this.mFilteringMulticastV4Packets.set(true);
        sendMessage(CMD_START_PACKET_FILTERING, 0, 0);
    }

    public void stopFilteringMulticastV4Packets() {
        this.mFilteringMulticastV4Packets.set(false);
        sendMessage(CMD_STOP_PACKET_FILTERING, 0, 0);
    }

    public void startFilteringMulticastV6Packets() {
        sendMessage(CMD_START_PACKET_FILTERING, 1, 0);
    }

    public void stopFilteringMulticastV6Packets() {
        sendMessage(CMD_STOP_PACKET_FILTERING, 1, 0);
    }

    public void setHighPerfModeEnabled(boolean z) {
        sendMessage(CMD_SET_HIGH_PERF_MODE, z ? 1 : 0, 0);
    }

    public void setCountryCode(String str, boolean z) {
        if (z) {
            this.mPersistedCountryCode = str;
            Settings.Global.putString(this.mContext.getContentResolver(), "wifi_country_code", str);
        }
        sendMessage(CMD_SET_COUNTRY_CODE, str);
        this.mWifiP2pChannel.sendMessage(WifiP2pService.SET_COUNTRY_CODE, str);
    }

    public void setFrequencyBand(int i, boolean z) {
        if (z) {
            Settings.Global.putInt(this.mContext.getContentResolver(), Settings.Global.WIFI_FREQUENCY_BAND, i);
        }
        sendMessage(CMD_SET_FREQUENCY_BAND, i, 0);
    }

    public void enableTdls(String str, boolean z) {
        sendMessage(CMD_ENABLE_TDLS, z ? 1 : 0, 0, str);
    }

    public int getFrequencyBand() {
        return this.mFrequencyBand.get();
    }

    public String getConfigFile() {
        return this.mWifiConfigStore.getConfigFile();
    }

    public void sendBluetoothAdapterStateChange(int i) {
        sendMessage(CMD_BLUETOOTH_ADAPTER_STATE_CHANGE, i, 0);
    }

    public boolean syncSaveConfig(AsyncChannel asyncChannel) {
        Message messageSendMessageSynchronously = asyncChannel.sendMessageSynchronously(CMD_SAVE_CONFIG);
        boolean z = messageSendMessageSynchronously.arg1 != -1;
        messageSendMessageSynchronously.recycle();
        return z;
    }

    public void updateBatteryWorkSource(WorkSource workSource) {
        synchronized (this.mRunningWifiUids) {
            if (workSource != null) {
                try {
                    this.mRunningWifiUids.set(workSource);
                } catch (RemoteException unused) {
                }
            }
            if (this.mIsRunning) {
                if (this.mReportedRunning) {
                    if (this.mLastRunningWifiUids.diff(this.mRunningWifiUids)) {
                        this.mBatteryStats.noteWifiRunningChanged(this.mLastRunningWifiUids, this.mRunningWifiUids);
                        this.mLastRunningWifiUids.set(this.mRunningWifiUids);
                    }
                } else {
                    this.mBatteryStats.noteWifiRunning(this.mRunningWifiUids);
                    this.mLastRunningWifiUids.set(this.mRunningWifiUids);
                    this.mReportedRunning = true;
                }
            } else if (this.mReportedRunning) {
                this.mBatteryStats.noteWifiStopped(this.mLastRunningWifiUids);
                this.mLastRunningWifiUids.clear();
                this.mReportedRunning = false;
            }
            this.mWakeLock.setWorkSource(workSource);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(fileDescriptor, printWriter, strArr);
        this.mSupplicantStateTracker.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("mLinkProperties " + this.mLinkProperties);
        printWriter.println("mWifiInfo " + this.mWifiInfo);
        printWriter.println("mDhcpResults " + this.mDhcpResults);
        printWriter.println("mNetworkInfo " + this.mNetworkInfo);
        printWriter.println("mLastSignalLevel " + this.mLastSignalLevel);
        printWriter.println("mLastBssid " + this.mLastBssid);
        printWriter.println("mLastNetworkId " + this.mLastNetworkId);
        printWriter.println("mReconnectCount " + this.mReconnectCount);
        printWriter.println("mOperationalMode " + this.mOperationalMode);
        printWriter.println("mUserWantsSuspendOpt " + this.mUserWantsSuspendOpt);
        printWriter.println("mSuspendOptNeedsDisabled " + this.mSuspendOptNeedsDisabled);
        printWriter.println("Supplicant status " + this.mWifiNative.status());
        printWriter.println("mEnableBackgroundScan " + this.mEnableBackgroundScan);
        printWriter.println();
        this.mWifiConfigStore.dump(fileDescriptor, printWriter, strArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleScreenStateChanged(boolean z) {
        enableRssiPolling(z);
        if (this.mBackgroundScanSupported) {
            enableBackgroundScanCommand(!z);
        }
        if (z) {
            enableAllNetworks();
        }
        if (this.mUserWantsSuspendOpt.get()) {
            if (z) {
                sendMessage(CMD_SET_SUSPEND_OPT_ENABLED, 0, 0);
            } else {
                this.mSuspendWakeLock.acquire(FileUtils.FAT_FILE_TIMESTAMP_GRANULARITY);
                sendMessage(CMD_SET_SUSPEND_OPT_ENABLED, 1, 0);
            }
        }
        this.mScreenBroadcastReceived.set(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkAndSetConnectivityInstance() {
        if (this.mCm == null) {
            this.mCm = (ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean startTethering(ArrayList<String> arrayList) {
        checkAndSetConnectivityInstance();
        String[] tetherableWifiRegexs = this.mCm.getTetherableWifiRegexs();
        for (String str : arrayList) {
            for (String str2 : tetherableWifiRegexs) {
                if (str.matches(str2)) {
                    try {
                        InterfaceConfiguration interfaceConfig = this.mNwService.getInterfaceConfig(str);
                        if (interfaceConfig != null) {
                            interfaceConfig.setLinkAddress(new LinkAddress(NetworkUtils.numericToInetAddress("192.168.43.1"), 24));
                            interfaceConfig.setInterfaceUp();
                            this.mNwService.setInterfaceConfig(str, interfaceConfig);
                        }
                        if (this.mCm.tether(str) != 0) {
                            loge("Error tethering on " + str);
                            return false;
                        }
                        this.mTetherInterfaceName = str;
                        return true;
                    } catch (Exception e) {
                        loge("Error configuring interface " + str + ", :" + e);
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopTethering() {
        checkAndSetConnectivityInstance();
        try {
            InterfaceConfiguration interfaceConfig = this.mNwService.getInterfaceConfig(this.mTetherInterfaceName);
            if (interfaceConfig != null) {
                interfaceConfig.setLinkAddress(new LinkAddress(NetworkUtils.numericToInetAddress("0.0.0.0"), 0));
                this.mNwService.setInterfaceConfig(this.mTetherInterfaceName, interfaceConfig);
            }
        } catch (Exception e) {
            loge("Error resetting interface " + this.mTetherInterfaceName + ", :" + e);
        }
        if (this.mCm.untether(this.mTetherInterfaceName) != 0) {
            loge("Untether initiate failed!");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isWifiTethered(ArrayList<String> arrayList) {
        checkAndSetConnectivityInstance();
        String[] tetherableWifiRegexs = this.mCm.getTetherableWifiRegexs();
        Iterator<String> it = arrayList.iterator();
        while (true) {
            if (!it.hasNext()) {
                return false;
            }
            String next = it.next();
            for (String str : tetherableWifiRegexs) {
                if (next.matches(str)) {
                    return true;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCountryCode() {
        String string = Settings.Global.getString(this.mContext.getContentResolver(), "wifi_country_code");
        if (string == null || string.isEmpty()) {
            return;
        }
        setCountryCode(string, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFrequencyBand() {
        setFrequencyBand(Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.WIFI_FREQUENCY_BAND, 0), false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSuspendOptimizationsNative(int i, boolean z) {
        if (z) {
            int i2 = (~i) & this.mSuspendOptNeedsDisabled;
            this.mSuspendOptNeedsDisabled = i2;
            if (i2 == 0 && this.mUserWantsSuspendOpt.get()) {
                this.mWifiNative.setSuspendOptimizations(true);
                return;
            }
            return;
        }
        this.mSuspendOptNeedsDisabled = i | this.mSuspendOptNeedsDisabled;
        this.mWifiNative.setSuspendOptimizations(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSuspendOptimizations(int i, boolean z) {
        if (z) {
            this.mSuspendOptNeedsDisabled = (~i) & this.mSuspendOptNeedsDisabled;
        } else {
            this.mSuspendOptNeedsDisabled = i | this.mSuspendOptNeedsDisabled;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWifiState(int i) {
        int i2 = this.mWifiState.get();
        try {
        } catch (RemoteException unused) {
            loge("Failed to note battery stats in wifi");
        }
        if (i == 3) {
            this.mBatteryStats.noteWifiOn();
        } else {
            if (i == 1) {
                this.mBatteryStats.noteWifiOff();
            }
            this.mWifiState.set(i);
            Intent intent = new Intent(WifiManager.WIFI_STATE_CHANGED_ACTION);
            intent.addFlags(67108864);
            intent.putExtra("wifi_state", i);
            intent.putExtra("previous_wifi_state", i2);
            this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }
        this.mWifiState.set(i);
        Intent intent2 = new Intent(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intent2.addFlags(67108864);
        intent2.putExtra("wifi_state", i);
        intent2.putExtra("previous_wifi_state", i2);
        this.mContext.sendStickyBroadcastAsUser(intent2, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWifiApState(int i) {
        int i2 = this.mWifiApState.get();
        try {
        } catch (RemoteException unused) {
            loge("Failed to note battery stats in wifi");
        }
        if (i == 13) {
            this.mBatteryStats.noteWifiOn();
        } else {
            if (i == 11) {
                this.mBatteryStats.noteWifiOff();
            }
            this.mWifiApState.set(i);
            Intent intent = new Intent(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
            intent.addFlags(67108864);
            intent.putExtra("wifi_state", i);
            intent.putExtra("previous_wifi_state", i2);
            this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }
        this.mWifiApState.set(i);
        Intent intent2 = new Intent(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
        intent2.addFlags(67108864);
        intent2.putExtra("wifi_state", i);
        intent2.putExtra("previous_wifi_state", i2);
        this.mContext.sendStickyBroadcastAsUser(intent2, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x004d, code lost:
    
        r5 = java.lang.Integer.parseInt(r5[r6].substring(3)) + 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void setScanResults() {
        /*
            Method dump skipped, instruction units count: 380
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiStateMachine.setScanResults():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fetchRssiAndLinkSpeedNative() {
        int i;
        int i2;
        String strSignalPoll = this.mWifiNative.signalPoll();
        if (strSignalPoll != null) {
            i = -1;
            i2 = -1;
            for (String str : strSignalPoll.split("\n")) {
                String[] strArrSplit = str.split("=");
                if (strArrSplit.length >= 2) {
                    try {
                        if (strArrSplit[0].equals("RSSI")) {
                            i = Integer.parseInt(strArrSplit[1]);
                        } else if (strArrSplit[0].equals("LINKSPEED")) {
                            i2 = Integer.parseInt(strArrSplit[1]);
                        }
                    } catch (NumberFormatException unused) {
                    }
                }
            }
        } else {
            i = -1;
            i2 = -1;
        }
        if (i != -1 && -200 < i && i < 256) {
            if (i > 0) {
                i -= 256;
            }
            this.mWifiInfo.setRssi(i);
            int iCalculateSignalLevel = WifiManager.calculateSignalLevel(i, 5);
            if (iCalculateSignalLevel != this.mLastSignalLevel) {
                sendRssiChangeBroadcast(i);
            }
            this.mLastSignalLevel = iCalculateSignalLevel;
        } else {
            this.mWifiInfo.setRssi(-200);
        }
        if (i2 != -1) {
            this.mWifiInfo.setLinkSpeed(i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fetchPktcntNative(RssiPacketCountInfo rssiPacketCountInfo) {
        String strPktcntPoll = this.mWifiNative.pktcntPoll();
        if (strPktcntPoll != null) {
            for (String str : strPktcntPoll.split("\n")) {
                String[] strArrSplit = str.split("=");
                if (strArrSplit.length >= 2) {
                    try {
                        if (strArrSplit[0].equals("TXGOOD")) {
                            rssiPacketCountInfo.txgood = Integer.parseInt(strArrSplit[1]);
                        } else if (strArrSplit[0].equals("TXBAD")) {
                            rssiPacketCountInfo.txbad = Integer.parseInt(strArrSplit[1]);
                        }
                    } catch (NumberFormatException unused) {
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLinkProperties() {
        LinkProperties linkProperties = new LinkProperties();
        linkProperties.setInterfaceName(this.mInterfaceName);
        linkProperties.setHttpProxy(this.mWifiConfigStore.getProxyProperties(this.mLastNetworkId));
        linkProperties.setLinkAddresses(this.mNetlinkLinkProperties.getLinkAddresses());
        synchronized (this.mDhcpResultsLock) {
            DhcpResults dhcpResults = this.mDhcpResults;
            if (dhcpResults != null && dhcpResults.linkProperties != null) {
                LinkProperties linkProperties2 = this.mDhcpResults.linkProperties;
                Iterator<RouteInfo> it = linkProperties2.getRoutes().iterator();
                while (it.hasNext()) {
                    linkProperties.addRoute(it.next());
                }
                Iterator<InetAddress> it2 = linkProperties2.getDnses().iterator();
                while (it2.hasNext()) {
                    linkProperties.addDns(it2.next());
                }
                linkProperties.setDomains(linkProperties2.getDomains());
            }
        }
        if (linkProperties.equals(this.mLinkProperties)) {
            return;
        }
        this.mLinkProperties = linkProperties;
        if (getNetworkDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
            sendLinkConfigurationChangedBroadcast();
        }
    }

    private void clearLinkProperties() {
        if (!this.mWifiConfigStore.isUsingStaticIp(this.mLastNetworkId)) {
            this.mWifiConfigStore.clearLinkProperties(this.mLastNetworkId);
        }
        synchronized (this.mDhcpResultsLock) {
            DhcpResults dhcpResults = this.mDhcpResults;
            if (dhcpResults != null && dhcpResults.linkProperties != null) {
                this.mDhcpResults.linkProperties.clear();
            }
        }
        this.mNetlinkLinkProperties.clear();
        this.mLinkProperties.clear();
    }

    private int getMaxDhcpRetries() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "wifi_max_dhcp_retry_count", 9);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendScanResultsAvailableBroadcast() {
        noteScanEnd();
        Intent intent = new Intent(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intent.addFlags(67108864);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    private void sendRssiChangeBroadcast(int i) {
        Intent intent = new Intent(WifiManager.RSSI_CHANGED_ACTION);
        intent.addFlags(67108864);
        intent.putExtra(WifiManager.EXTRA_NEW_RSSI, i);
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendNetworkStateChangeBroadcast(String str) {
        Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intent.addFlags(67108864);
        intent.putExtra("networkInfo", new NetworkInfo(this.mNetworkInfo));
        intent.putExtra("linkProperties", new LinkProperties(this.mLinkProperties));
        if (str != null) {
            intent.putExtra("bssid", str);
        }
        if (this.mNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.VERIFYING_POOR_LINK || this.mNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
            intent.putExtra(WifiManager.EXTRA_WIFI_INFO, new WifiInfo(this.mWifiInfo));
        }
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    private void sendLinkConfigurationChangedBroadcast() {
        Intent intent = new Intent(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
        intent.addFlags(67108864);
        intent.putExtra("linkProperties", new LinkProperties(this.mLinkProperties));
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSupplicantConnectionChangedBroadcast(boolean z) {
        Intent intent = new Intent(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        intent.addFlags(67108864);
        intent.putExtra("connected", z);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNetworkDetailedState(NetworkInfo.DetailedState detailedState) {
        if (detailedState != this.mNetworkInfo.getDetailedState()) {
            this.mNetworkInfo.setDetailedState(detailedState, null, this.mWifiInfo.getSSID());
        }
    }

    private NetworkInfo.DetailedState getNetworkDetailedState() {
        return this.mNetworkInfo.getDetailedState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SupplicantState handleSupplicantStateChange(Message message) {
        StateChangeResult stateChangeResult = (StateChangeResult) message.obj;
        SupplicantState supplicantState = stateChangeResult.state;
        this.mWifiInfo.setSupplicantState(supplicantState);
        if (SupplicantState.isConnecting(supplicantState)) {
            this.mWifiInfo.setNetworkId(stateChangeResult.networkId);
        } else {
            this.mWifiInfo.setNetworkId(-1);
        }
        this.mWifiInfo.setBSSID(stateChangeResult.BSSID);
        this.mWifiInfo.setSSID(stateChangeResult.wifiSsid);
        this.mSupplicantStateTracker.sendMessage(Message.obtain(message));
        return supplicantState;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNetworkDisconnect() {
        stopDhcp();
        try {
            this.mNwService.clearInterfaceAddresses(this.mInterfaceName);
            this.mNwService.disableIpv6(this.mInterfaceName);
        } catch (Exception e) {
            loge("Failed to clear addresses or disable ipv6" + e);
        }
        this.mWifiInfo.setInetAddress(null);
        this.mWifiInfo.setBSSID(null);
        this.mWifiInfo.setSSID(null);
        this.mWifiInfo.setNetworkId(-1);
        this.mWifiInfo.setRssi(-200);
        this.mWifiInfo.setLinkSpeed(-1);
        this.mWifiInfo.setMeteredHint(false);
        setNetworkDetailedState(NetworkInfo.DetailedState.DISCONNECTED);
        this.mWifiConfigStore.updateStatus(this.mLastNetworkId, NetworkInfo.DetailedState.DISCONNECTED);
        clearLinkProperties();
        sendNetworkStateChangeBroadcast(this.mLastBssid);
        this.mLastBssid = null;
        this.mLastNetworkId = -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSupplicantConnectionLoss() {
        this.mWifiMonitor.killSupplicant(this.mP2pSupported);
        this.mWifiNative.closeSupplicantConnection();
        sendSupplicantConnectionChangedBroadcast(false);
        setWifiState(1);
    }

    void handlePreDhcpSetup() {
        this.mDhcpActive = true;
        if (!this.mBluetoothConnectionActive) {
            this.mWifiNative.setBluetoothCoexistenceMode(1);
        }
        setSuspendOptimizationsNative(1, false);
        this.mWifiNative.setPowerSave(false);
        stopBatchedScan();
        Message message = new Message();
        message.what = WifiP2pService.BLOCK_DISCOVERY;
        message.arg1 = 1;
        message.arg2 = DhcpStateMachine.CMD_PRE_DHCP_ACTION_COMPLETE;
        message.obj = this.mDhcpStateMachine;
        this.mWifiP2pChannel.sendMessage(message);
    }

    void startDhcp() {
        if (this.mDhcpStateMachine == null) {
            this.mDhcpStateMachine = DhcpStateMachine.makeDhcpStateMachine(this.mContext, this, this.mInterfaceName);
        }
        this.mDhcpStateMachine.registerForPreDhcpNotification();
        this.mDhcpStateMachine.sendMessage(DhcpStateMachine.CMD_START_DHCP);
    }

    void stopDhcp() {
        if (this.mDhcpStateMachine != null) {
            handlePostDhcpSetup();
            this.mDhcpStateMachine.sendMessage(DhcpStateMachine.CMD_STOP_DHCP);
        }
    }

    void handlePostDhcpSetup() {
        setSuspendOptimizationsNative(1, true);
        this.mWifiNative.setPowerSave(true);
        this.mWifiP2pChannel.sendMessage(WifiP2pService.BLOCK_DISCOVERY, 0);
        this.mWifiNative.setBluetoothCoexistenceMode(2);
        this.mDhcpActive = false;
        startBatchedScan();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSuccessfulIpConfiguration(DhcpResults dhcpResults) {
        this.mLastSignalLevel = -1;
        this.mReconnectCount = 0;
        synchronized (this.mDhcpResultsLock) {
            this.mDhcpResults = dhcpResults;
        }
        LinkProperties linkProperties = dhcpResults.linkProperties;
        this.mWifiConfigStore.setLinkProperties(this.mLastNetworkId, new LinkProperties(linkProperties));
        Iterator<InetAddress> it = linkProperties.getAddresses().iterator();
        this.mWifiInfo.setInetAddress(it.hasNext() ? it.next() : null);
        this.mWifiInfo.setMeteredHint(dhcpResults.hasMeteredHint());
        updateLinkProperties();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFailedIpConfiguration() {
        loge("IP configuration failed");
        this.mWifiInfo.setInetAddress(null);
        this.mWifiInfo.setMeteredHint(false);
        int maxDhcpRetries = getMaxDhcpRetries();
        if (maxDhcpRetries > 0) {
            int i = this.mReconnectCount + 1;
            this.mReconnectCount = i;
            if (i > maxDhcpRetries) {
                loge("Failed " + this.mReconnectCount + " times, Disabling " + this.mLastNetworkId);
                this.mWifiConfigStore.disableNetwork(this.mLastNetworkId, 2);
                this.mReconnectCount = 0;
            }
        }
        this.mWifiNative.disconnect();
        this.mWifiNative.reconnect();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSoftApWithConfig(final WifiConfiguration wifiConfiguration) {
        new Thread(new Runnable() { // from class: android.net.wifi.WifiStateMachine.7
            @Override // java.lang.Runnable
            public void run() {
                try {
                    WifiStateMachine.this.mNwService.startAccessPoint(wifiConfiguration, WifiStateMachine.this.mInterfaceName);
                } catch (Exception e) {
                    WifiStateMachine.this.loge("Exception in softap start " + e);
                    try {
                        WifiStateMachine.this.mNwService.stopAccessPoint(WifiStateMachine.this.mInterfaceName);
                        WifiStateMachine.this.mNwService.startAccessPoint(wifiConfiguration, WifiStateMachine.this.mInterfaceName);
                    } catch (Exception e2) {
                        WifiStateMachine.this.loge("Exception in softap re-start " + e2);
                        WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_START_AP_FAILURE);
                        return;
                    }
                }
                WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_START_AP_SUCCESS);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isValidAscii(WifiConfiguration wifiConfiguration) {
        String strReplace = wifiConfiguration.SSID.replace("\"", "");
        for (ScanResult scanResult : this.mScanResults) {
            if (scanResult.SSID.equals(strReplace) && !scanResult.wifiSsid.isValidAscii) {
                wifiConfiguration.originSsid = scanResult.wifiSsid.mAsciiStr;
                return false;
            }
        }
        return true;
    }

    class DefaultState extends State {
        DefaultState() {
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:67:0x01d7 A[FALL_THROUGH, RETURN] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean processMessage(android.os.Message r6) {
            /*
                Method dump skipped, instruction units count: 742
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiStateMachine.DefaultState.processMessage(android.os.Message):boolean");
        }
    }

    class InitialState extends State {
        InitialState() {
        }

        public void enter() throws Throwable {
            WifiNative unused = WifiStateMachine.this.mWifiNative;
            WifiNative.unloadDriver();
            if (WifiStateMachine.this.mWifiP2pChannel == null) {
                WifiStateMachine.this.mWifiP2pChannel = new AsyncChannel();
                WifiStateMachine.this.mWifiP2pChannel.connect(WifiStateMachine.this.mContext, WifiStateMachine.this.getHandler(), WifiStateMachine.this.mWifiP2pManager.getMessenger());
            }
            if (WifiStateMachine.this.mWifiApConfigChannel == null) {
                WifiStateMachine.this.mWifiApConfigChannel = new AsyncChannel();
                WifiApConfigStore wifiApConfigStoreMakeWifiApConfigStore = WifiApConfigStore.makeWifiApConfigStore(WifiStateMachine.this.mContext, WifiStateMachine.this.getHandler());
                wifiApConfigStoreMakeWifiApConfigStore.loadApConfiguration();
                WifiStateMachine.this.mWifiApConfigChannel.connectSync(WifiStateMachine.this.mContext, WifiStateMachine.this.getHandler(), wifiApConfigStoreMakeWifiApConfigStore.getMessenger());
            }
        }

        public boolean processMessage(Message message) {
            int i = message.what;
            if (i != WifiStateMachine.CMD_START_SUPPLICANT) {
                if (i != WifiStateMachine.CMD_START_AP) {
                    return false;
                }
                WifiNative unused = WifiStateMachine.this.mWifiNative;
                if (WifiNative.loadDriver()) {
                    WifiStateMachine.this.setWifiApState(12);
                    WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                    wifiStateMachine.transitionTo(wifiStateMachine.mSoftApStartingState);
                    return false;
                }
                WifiStateMachine.this.loge("Failed to load driver for softap");
                return false;
            }
            WifiNative unused2 = WifiStateMachine.this.mWifiNative;
            if (WifiNative.loadDriver()) {
                try {
                    WifiStateMachine.this.mNwService.wifiFirmwareReload(WifiStateMachine.this.mInterfaceName, "STA");
                } catch (Exception e) {
                    WifiStateMachine.this.loge("Failed to reload STA firmware " + e);
                }
                try {
                    WifiStateMachine.this.mNwService.setInterfaceDown(WifiStateMachine.this.mInterfaceName);
                    WifiStateMachine.this.mNwService.setInterfaceIpv6PrivacyExtensions(WifiStateMachine.this.mInterfaceName, true);
                    WifiStateMachine.this.mNwService.disableIpv6(WifiStateMachine.this.mInterfaceName);
                } catch (RemoteException e2) {
                    WifiStateMachine.this.loge("Unable to change interface settings: " + e2);
                } catch (IllegalStateException e3) {
                    WifiStateMachine.this.loge("Unable to change interface settings: " + e3);
                }
                WifiStateMachine.this.mWifiMonitor.killSupplicant(WifiStateMachine.this.mP2pSupported);
                WifiNative unused3 = WifiStateMachine.this.mWifiNative;
                if (WifiNative.startSupplicant(WifiStateMachine.this.mP2pSupported)) {
                    WifiStateMachine.this.setWifiState(2);
                    WifiStateMachine.this.mWifiMonitor.startMonitoring();
                    WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
                    wifiStateMachine2.transitionTo(wifiStateMachine2.mSupplicantStartingState);
                } else {
                    WifiStateMachine.this.loge("Failed to start supplicant!");
                }
            } else {
                WifiStateMachine.this.loge("Failed to load driver");
            }
            return true;
        }
    }

    class SupplicantStartingState extends State {
        SupplicantStartingState() {
        }

        private void initializeWpsDetails() {
            String str = SystemProperties.get("ro.product.name", "");
            if (!WifiStateMachine.this.mWifiNative.setDeviceName(str)) {
                WifiStateMachine.this.loge("Failed to set device name " + str);
            }
            String str2 = SystemProperties.get("ro.product.manufacturer", "");
            if (!WifiStateMachine.this.mWifiNative.setManufacturer(str2)) {
                WifiStateMachine.this.loge("Failed to set manufacturer " + str2);
            }
            String str3 = SystemProperties.get("ro.product.model", "");
            if (!WifiStateMachine.this.mWifiNative.setModelName(str3)) {
                WifiStateMachine.this.loge("Failed to set model name " + str3);
            }
            String str4 = SystemProperties.get("ro.product.model", "");
            if (!WifiStateMachine.this.mWifiNative.setModelNumber(str4)) {
                WifiStateMachine.this.loge("Failed to set model number " + str4);
            }
            String str5 = SystemProperties.get("ro.serialno", "");
            if (!WifiStateMachine.this.mWifiNative.setSerialNumber(str5)) {
                WifiStateMachine.this.loge("Failed to set serial number " + str5);
            }
            if (!WifiStateMachine.this.mWifiNative.setConfigMethods("physical_display virtual_push_button")) {
                WifiStateMachine.this.loge("Failed to set WPS config methods");
            }
            if (WifiStateMachine.this.mWifiNative.setDeviceType(WifiStateMachine.this.mPrimaryDeviceType)) {
                return;
            }
            WifiStateMachine.this.loge("Failed to set primary device type " + WifiStateMachine.this.mPrimaryDeviceType);
        }

        public boolean processMessage(Message message) throws Throwable {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SUPPLICANT /* 131083 */:
                case WifiStateMachine.CMD_STOP_SUPPLICANT /* 131084 */:
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                case WifiStateMachine.CMD_STOP_AP /* 131096 */:
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiMonitor.SUP_CONNECTION_EVENT /* 147457 */:
                    WifiStateMachine.this.setWifiState(3);
                    WifiStateMachine.this.mSupplicantRestartCount = 0;
                    WifiStateMachine.this.mSupplicantStateTracker.sendMessage(WifiStateMachine.CMD_RESET_SUPPLICANT_STATE);
                    WifiStateMachine.this.mLastBssid = null;
                    WifiStateMachine.this.mLastNetworkId = -1;
                    WifiStateMachine.this.mLastSignalLevel = -1;
                    WifiStateMachine.this.mWifiInfo.setMacAddress(WifiStateMachine.this.mWifiNative.getMacAddress());
                    WifiStateMachine.this.mWifiConfigStore.loadAndEnableAllNetworks();
                    initializeWpsDetails();
                    WifiStateMachine.this.sendSupplicantConnectionChangedBroadcast(true);
                    WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                    wifiStateMachine.transitionTo(wifiStateMachine.mDriverStartedState);
                    return true;
                case WifiMonitor.SUP_DISCONNECTION_EVENT /* 147458 */:
                    if (WifiStateMachine.access$5504(WifiStateMachine.this) <= 5) {
                        WifiStateMachine.this.loge("Failed to setup control channel, restart supplicant");
                        WifiStateMachine.this.mWifiMonitor.killSupplicant(WifiStateMachine.this.mP2pSupported);
                        WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
                        wifiStateMachine2.transitionTo(wifiStateMachine2.mInitialState);
                        WifiStateMachine.this.sendMessageDelayed(WifiStateMachine.CMD_START_SUPPLICANT, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
                    } else {
                        WifiStateMachine.this.loge("Failed " + WifiStateMachine.this.mSupplicantRestartCount + " times to start supplicant, unload driver");
                        WifiStateMachine.this.mSupplicantRestartCount = 0;
                        WifiStateMachine.this.setWifiState(4);
                        WifiStateMachine wifiStateMachine3 = WifiStateMachine.this;
                        wifiStateMachine3.transitionTo(wifiStateMachine3.mInitialState);
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    class SupplicantStartedState extends State {
        SupplicantStartedState() {
        }

        public void enter() {
            WifiStateMachine.this.mNetworkInfo.setIsAvailable(true);
            int integer = WifiStateMachine.this.mContext.getResources().getInteger(17694735);
            WifiStateMachine wifiStateMachine = WifiStateMachine.this;
            wifiStateMachine.mSupplicantScanIntervalMs = Settings.Global.getLong(wifiStateMachine.mContext.getContentResolver(), Settings.Global.WIFI_SUPPLICANT_SCAN_INTERVAL_MS, integer);
            WifiStateMachine.this.mWifiNative.setScanInterval(((int) WifiStateMachine.this.mSupplicantScanIntervalMs) / 1000);
        }

        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_STOP_SUPPLICANT /* 131084 */:
                    if (WifiStateMachine.this.mP2pSupported) {
                        WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                        wifiStateMachine.transitionTo(wifiStateMachine.mWaitForP2pDisableState);
                    } else {
                        WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
                        wifiStateMachine2.transitionTo(wifiStateMachine2.mSupplicantStoppingState);
                    }
                    return true;
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                    WifiStateMachine.this.loge("Failed to start soft AP with a running supplicant");
                    WifiStateMachine.this.setWifiApState(14);
                    return true;
                case WifiStateMachine.CMD_PING_SUPPLICANT /* 131123 */:
                    WifiStateMachine.this.replyToMessage(message, message.what, WifiStateMachine.this.mWifiNative.ping() ? 1 : -1);
                    return true;
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                    WifiStateMachine.this.mOperationalMode = message.arg1;
                    return true;
                case WifiMonitor.SUP_DISCONNECTION_EVENT /* 147458 */:
                    WifiStateMachine.this.loge("Connection lost, restart supplicant");
                    WifiStateMachine.this.handleSupplicantConnectionLoss();
                    WifiStateMachine.this.handleNetworkDisconnect();
                    WifiStateMachine.this.mSupplicantStateTracker.sendMessage(WifiStateMachine.CMD_RESET_SUPPLICANT_STATE);
                    if (WifiStateMachine.this.mP2pSupported) {
                        WifiStateMachine wifiStateMachine3 = WifiStateMachine.this;
                        wifiStateMachine3.transitionTo(wifiStateMachine3.mWaitForP2pDisableState);
                    } else {
                        WifiStateMachine wifiStateMachine4 = WifiStateMachine.this;
                        wifiStateMachine4.transitionTo(wifiStateMachine4.mInitialState);
                    }
                    WifiStateMachine.this.sendMessageDelayed(WifiStateMachine.CMD_START_SUPPLICANT, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
                    return true;
                case WifiMonitor.SCAN_RESULTS_EVENT /* 147461 */:
                    WifiStateMachine.this.setScanResults();
                    WifiStateMachine.this.sendScanResultsAvailableBroadcast();
                    WifiStateMachine.this.mScanResultIsPending = false;
                    return true;
                default:
                    return false;
            }
        }

        public void exit() {
            WifiStateMachine.this.mNetworkInfo.setIsAvailable(false);
        }
    }

    class SupplicantStoppingState extends State {
        SupplicantStoppingState() {
        }

        public void enter() {
            WifiStateMachine.this.handleNetworkDisconnect();
            if (WifiStateMachine.this.mDhcpStateMachine != null) {
                WifiStateMachine.this.mDhcpStateMachine.doQuit();
            }
            WifiStateMachine.this.mWifiMonitor.stopSupplicant();
            WifiStateMachine wifiStateMachine = WifiStateMachine.this;
            wifiStateMachine.sendMessageDelayed(wifiStateMachine.obtainMessage(WifiStateMachine.CMD_STOP_SUPPLICANT_FAILED, WifiStateMachine.access$8704(wifiStateMachine), 0), TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
            WifiStateMachine.this.setWifiState(0);
            WifiStateMachine.this.mSupplicantStateTracker.sendMessage(WifiStateMachine.CMD_RESET_SUPPLICANT_STATE);
        }

        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SUPPLICANT /* 131083 */:
                case WifiStateMachine.CMD_STOP_SUPPLICANT /* 131084 */:
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                case WifiStateMachine.CMD_STOP_AP /* 131096 */:
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiStateMachine.CMD_STOP_SUPPLICANT_FAILED /* 131089 */:
                    if (message.arg1 != WifiStateMachine.this.mSupplicantStopFailureToken) {
                        return true;
                    }
                    WifiStateMachine.this.loge("Timed out on a supplicant stop, kill and proceed");
                    WifiStateMachine.this.handleSupplicantConnectionLoss();
                    WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                    wifiStateMachine.transitionTo(wifiStateMachine.mInitialState);
                    return true;
                case WifiMonitor.SUP_CONNECTION_EVENT /* 147457 */:
                    WifiStateMachine.this.loge("Supplicant connection received while stopping");
                    return true;
                case WifiMonitor.SUP_DISCONNECTION_EVENT /* 147458 */:
                    WifiStateMachine.this.handleSupplicantConnectionLoss();
                    WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
                    wifiStateMachine2.transitionTo(wifiStateMachine2.mInitialState);
                    return true;
                default:
                    return false;
            }
        }
    }

    class DriverStartingState extends State {
        private int mTries;

        DriverStartingState() {
        }

        public void enter() {
            this.mTries = 1;
            WifiStateMachine wifiStateMachine = WifiStateMachine.this;
            wifiStateMachine.sendMessageDelayed(wifiStateMachine.obtainMessage(WifiStateMachine.CMD_DRIVER_START_TIMED_OUT, WifiStateMachine.access$9304(wifiStateMachine), 0), 10000L);
        }

        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_START_SCAN /* 131143 */:
                case WifiStateMachine.CMD_DISCONNECT /* 131145 */:
                case WifiStateMachine.CMD_RECONNECT /* 131146 */:
                case WifiStateMachine.CMD_REASSOCIATE /* 131147 */:
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                case WifiMonitor.NETWORK_CONNECTION_EVENT /* 147459 */:
                case WifiMonitor.NETWORK_DISCONNECTION_EVENT /* 147460 */:
                case WifiMonitor.AUTHENTICATION_FAILURE_EVENT /* 147463 */:
                case WifiMonitor.WPS_OVERLAP_EVENT /* 147466 */:
                case WifiMonitor.ASSOCIATION_REJECTION_EVENT /* 147499 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiStateMachine.CMD_DRIVER_START_TIMED_OUT /* 131091 */:
                    if (message.arg1 == WifiStateMachine.this.mDriverStartToken) {
                        if (this.mTries >= 2) {
                            WifiStateMachine.this.loge("Failed to start driver after " + this.mTries);
                            WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                            wifiStateMachine.transitionTo(wifiStateMachine.mDriverStoppedState);
                        } else {
                            WifiStateMachine.this.loge("Driver start failed, retrying");
                            WifiStateMachine.this.mWakeLock.acquire();
                            WifiStateMachine.this.mWifiNative.startDriver();
                            WifiStateMachine.this.mWakeLock.release();
                            this.mTries++;
                            WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
                            wifiStateMachine2.sendMessageDelayed(wifiStateMachine2.obtainMessage(WifiStateMachine.CMD_DRIVER_START_TIMED_OUT, WifiStateMachine.access$9304(wifiStateMachine2), 0), 10000L);
                        }
                    }
                    return true;
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                    if (SupplicantState.isDriverActive(WifiStateMachine.this.handleSupplicantStateChange(message))) {
                        WifiStateMachine wifiStateMachine3 = WifiStateMachine.this;
                        wifiStateMachine3.transitionTo(wifiStateMachine3.mDriverStartedState);
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    class DriverStartedState extends State {
        DriverStartedState() {
        }

        public void enter() {
            WifiStateMachine.this.mIsRunning = true;
            boolean z = false;
            WifiStateMachine.this.mInDelayedStop = false;
            WifiStateMachine.access$10408(WifiStateMachine.this);
            WifiStateMachine.this.updateBatteryWorkSource(null);
            WifiStateMachine.this.mWifiNative.setBluetoothCoexistenceScanMode(WifiStateMachine.this.mBluetoothConnectionActive);
            WifiStateMachine.this.setCountryCode();
            WifiStateMachine.this.setFrequencyBand();
            WifiStateMachine.this.setNetworkDetailedState(NetworkInfo.DetailedState.DISCONNECTED);
            WifiStateMachine.this.mWifiNative.stopFilteringMulticastV6Packets();
            if (WifiStateMachine.this.mFilteringMulticastV4Packets.get()) {
                WifiStateMachine.this.mWifiNative.startFilteringMulticastV4Packets();
            } else {
                WifiStateMachine.this.mWifiNative.stopFilteringMulticastV4Packets();
            }
            WifiStateMachine.this.mDhcpActive = false;
            WifiStateMachine.this.startBatchedScan();
            if (WifiStateMachine.this.mOperationalMode != 1) {
                WifiStateMachine.this.mWifiNative.disconnect();
                WifiStateMachine.this.mWifiConfigStore.disableAllNetworks();
                if (WifiStateMachine.this.mOperationalMode == 3) {
                    WifiStateMachine.this.setWifiState(1);
                }
                WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                wifiStateMachine.transitionTo(wifiStateMachine.mScanModeState);
            } else {
                WifiStateMachine.this.mWifiConfigStore.enableAllNetworks();
                WifiStateMachine.this.mWifiNative.reconnect();
                WifiStateMachine.this.mWifiNative.status();
                WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
                wifiStateMachine2.transitionTo(wifiStateMachine2.mDisconnectedState);
            }
            if (!WifiStateMachine.this.mScreenBroadcastReceived.get()) {
                WifiStateMachine.this.handleScreenStateChanged(((PowerManager) WifiStateMachine.this.mContext.getSystemService(Context.POWER_SERVICE)).isScreenOn());
            } else {
                WifiNative wifiNative = WifiStateMachine.this.mWifiNative;
                if (WifiStateMachine.this.mSuspendOptNeedsDisabled == 0 && WifiStateMachine.this.mUserWantsSuspendOpt.get()) {
                    z = true;
                }
                wifiNative.setSuspendOptimizations(z);
            }
            WifiStateMachine.this.mWifiNative.setPowerSave(true);
            if (WifiStateMachine.this.mP2pSupported && WifiStateMachine.this.mOperationalMode == 1) {
                WifiStateMachine.this.mWifiP2pChannel.sendMessage(WifiStateMachine.CMD_ENABLE_P2P);
            }
            Intent intent = new Intent(WifiManager.WIFI_SCAN_AVAILABLE);
            intent.addFlags(67108864);
            intent.putExtra(WifiManager.EXTRA_SCAN_AVAILABLE, 3);
            WifiStateMachine.this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }

        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                    if (WifiStateMachine.this.mInDelayedStop) {
                        WifiStateMachine.this.mInDelayedStop = false;
                        WifiStateMachine.access$10408(WifiStateMachine.this);
                        WifiStateMachine.this.mAlarmManager.cancel(WifiStateMachine.this.mDriverStopIntent);
                        if (WifiStateMachine.this.mOperationalMode == 1) {
                            WifiStateMachine.this.mWifiConfigStore.enableAllNetworks();
                        }
                    }
                    return true;
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                    int i = message.arg1;
                    if (!WifiStateMachine.this.mInDelayedStop) {
                        WifiStateMachine.this.mWifiConfigStore.disableAllNetworks();
                        WifiStateMachine.this.mInDelayedStop = true;
                        WifiStateMachine.access$10408(WifiStateMachine.this);
                        WifiStateMachine.this.log("Delayed stop message " + WifiStateMachine.this.mDelayedStopCounter);
                        Intent intent = new Intent(WifiStateMachine.ACTION_DELAYED_DRIVER_STOP, (Uri) null);
                        intent.putExtra(WifiStateMachine.DELAYED_STOP_COUNTER, WifiStateMachine.this.mDelayedStopCounter);
                        WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                        wifiStateMachine.mDriverStopIntent = PendingIntent.getBroadcast(wifiStateMachine.mContext, 0, intent, 134217728);
                        WifiStateMachine.this.mAlarmManager.set(5, System.currentTimeMillis() + ((long) WifiStateMachine.this.mDriverStopDelayMs), WifiStateMachine.this.mDriverStopIntent);
                    }
                    return true;
                case WifiStateMachine.CMD_DELAYED_STOP_DRIVER /* 131090 */:
                    if (message.arg1 == WifiStateMachine.this.mDelayedStopCounter) {
                        if (WifiStateMachine.this.getCurrentState() != WifiStateMachine.this.mDisconnectedState) {
                            WifiStateMachine.this.mWifiNative.disconnect();
                            WifiStateMachine.this.handleNetworkDisconnect();
                        }
                        WifiStateMachine.this.mWakeLock.acquire();
                        WifiStateMachine.this.mWifiNative.stopDriver();
                        WifiStateMachine.this.mWakeLock.release();
                        if (WifiStateMachine.this.mP2pSupported) {
                            WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
                            wifiStateMachine2.transitionTo(wifiStateMachine2.mWaitForP2pDisableState);
                        } else {
                            WifiStateMachine wifiStateMachine3 = WifiStateMachine.this;
                            wifiStateMachine3.transitionTo(wifiStateMachine3.mDriverStoppingState);
                        }
                    }
                    return true;
                case WifiStateMachine.CMD_BLUETOOTH_ADAPTER_STATE_CHANGE /* 131103 */:
                    WifiStateMachine.this.mBluetoothConnectionActive = message.arg1 != 0;
                    WifiStateMachine.this.mWifiNative.setBluetoothCoexistenceScanMode(WifiStateMachine.this.mBluetoothConnectionActive);
                    return true;
                case WifiStateMachine.CMD_START_SCAN /* 131143 */:
                    WifiStateMachine.this.noteScanStart(message.arg1, (WorkSource) message.obj);
                    WifiStateMachine.this.startScanNative(2);
                    return true;
                case WifiStateMachine.CMD_SET_HIGH_PERF_MODE /* 131149 */:
                    if (message.arg1 == 1) {
                        WifiStateMachine.this.setSuspendOptimizationsNative(2, false);
                    } else {
                        WifiStateMachine.this.setSuspendOptimizationsNative(2, true);
                    }
                    return true;
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                    String str = (String) message.obj;
                    if (str != null) {
                        String upperCase = str.toUpperCase(Locale.ROOT);
                        if (WifiStateMachine.this.mLastSetCountryCode == null || !upperCase.equals(WifiStateMachine.this.mLastSetCountryCode)) {
                            if (WifiStateMachine.this.mWifiNative.setCountryCode(upperCase)) {
                                WifiStateMachine.this.mLastSetCountryCode = upperCase;
                            } else {
                                WifiStateMachine.this.loge("Failed to set country code " + upperCase);
                            }
                        }
                    }
                    return true;
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                    if (message.arg1 == 1) {
                        WifiStateMachine.this.mWifiNative.startFilteringMulticastV6Packets();
                    } else if (message.arg1 == 0) {
                        WifiStateMachine.this.mWifiNative.startFilteringMulticastV4Packets();
                    } else {
                        WifiStateMachine.this.loge("Illegal arugments to CMD_START_PACKET_FILTERING");
                    }
                    return true;
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                    if (message.arg1 == 1) {
                        WifiStateMachine.this.mWifiNative.stopFilteringMulticastV6Packets();
                    } else if (message.arg1 == 0) {
                        WifiStateMachine.this.mWifiNative.stopFilteringMulticastV4Packets();
                    } else {
                        WifiStateMachine.this.loge("Illegal arugments to CMD_STOP_PACKET_FILTERING");
                    }
                    return true;
                case WifiStateMachine.CMD_SET_SUSPEND_OPT_ENABLED /* 131158 */:
                    if (message.arg1 == 1) {
                        WifiStateMachine.this.setSuspendOptimizationsNative(4, true);
                        WifiStateMachine.this.mSuspendWakeLock.release();
                    } else {
                        WifiStateMachine.this.setSuspendOptimizationsNative(4, false);
                    }
                    return true;
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                    int i2 = message.arg1;
                    if (WifiStateMachine.this.mWifiNative.setBand(i2)) {
                        WifiStateMachine.this.mFrequencyBand.set(i2);
                        WifiStateMachine.this.mWifiNative.bssFlush();
                        WifiStateMachine.this.startScanNative(2);
                    } else {
                        WifiStateMachine.this.loge("Failed to set frequency band " + i2);
                    }
                    return true;
                case WifiStateMachine.CMD_ENABLE_TDLS /* 131164 */:
                    if (message.obj != null) {
                        WifiStateMachine.this.mWifiNative.startTdls((String) message.obj, message.arg1 == 1);
                    }
                    return true;
                case WifiStateMachine.CMD_SET_BATCHED_SCAN /* 131207 */:
                    if (WifiStateMachine.this.recordBatchedScanSettings(message.arg1, message.arg2, (Bundle) message.obj)) {
                        WifiStateMachine.this.startBatchedScan();
                    }
                    return true;
                default:
                    return false;
            }
        }

        public void exit() {
            WifiStateMachine.this.mIsRunning = false;
            WifiStateMachine.this.updateBatteryWorkSource(null);
            WifiStateMachine.this.mScanResults = new ArrayList();
            WifiStateMachine.this.stopBatchedScan();
            Intent intent = new Intent(WifiManager.WIFI_SCAN_AVAILABLE);
            intent.addFlags(67108864);
            intent.putExtra(WifiManager.EXTRA_SCAN_AVAILABLE, 1);
            WifiStateMachine.this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
            WifiStateMachine.this.noteScanEnd();
            WifiStateMachine.this.mLastSetCountryCode = null;
        }
    }

    class WaitForP2pDisableState extends State {
        private State mTransitionToState;

        WaitForP2pDisableState() {
        }

        public void enter() {
            int i = WifiStateMachine.this.getCurrentMessage().what;
            if (i == WifiStateMachine.CMD_STOP_SUPPLICANT) {
                this.mTransitionToState = WifiStateMachine.this.mSupplicantStoppingState;
            } else if (i == WifiStateMachine.CMD_DELAYED_STOP_DRIVER || i != 147458) {
                this.mTransitionToState = WifiStateMachine.this.mDriverStoppingState;
            } else {
                this.mTransitionToState = WifiStateMachine.this.mInitialState;
            }
            WifiStateMachine.this.mWifiP2pChannel.sendMessage(WifiStateMachine.CMD_DISABLE_P2P_REQ);
        }

        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SUPPLICANT /* 131083 */:
                case WifiStateMachine.CMD_STOP_SUPPLICANT /* 131084 */:
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                case WifiStateMachine.CMD_STOP_AP /* 131096 */:
                case WifiStateMachine.CMD_START_SCAN /* 131143 */:
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                case WifiStateMachine.CMD_DISCONNECT /* 131145 */:
                case WifiStateMachine.CMD_RECONNECT /* 131146 */:
                case WifiStateMachine.CMD_REASSOCIATE /* 131147 */:
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiStateMachine.CMD_DISABLE_P2P_RSP /* 131205 */:
                    WifiStateMachine.this.transitionTo(this.mTransitionToState);
                    return true;
                default:
                    return false;
            }
        }
    }

    class DriverStoppingState extends State {
        DriverStoppingState() {
        }

        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_START_SCAN /* 131143 */:
                case WifiStateMachine.CMD_DISCONNECT /* 131145 */:
                case WifiStateMachine.CMD_RECONNECT /* 131146 */:
                case WifiStateMachine.CMD_REASSOCIATE /* 131147 */:
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                    if (WifiStateMachine.this.handleSupplicantStateChange(message) != SupplicantState.INTERFACE_DISABLED) {
                        return true;
                    }
                    WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                    wifiStateMachine.transitionTo(wifiStateMachine.mDriverStoppedState);
                    return true;
                default:
                    return false;
            }
        }
    }

    class DriverStoppedState extends State {
        DriverStoppedState() {
        }

        public boolean processMessage(Message message) {
            int i = message.what;
            if (i == WifiStateMachine.CMD_START_DRIVER) {
                WifiStateMachine.this.mWakeLock.acquire();
                WifiStateMachine.this.mWifiNative.startDriver();
                WifiStateMachine.this.mWakeLock.release();
                WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                wifiStateMachine.transitionTo(wifiStateMachine.mDriverStartingState);
                return true;
            }
            if (i != 147462) {
                return false;
            }
            if (!SupplicantState.isDriverActive(((StateChangeResult) message.obj).state)) {
                return true;
            }
            WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
            wifiStateMachine2.transitionTo(wifiStateMachine2.mDriverStartedState);
            return true;
        }
    }

    class ScanModeState extends State {
        private int mLastOperationMode;

        ScanModeState() {
        }

        public void enter() {
            this.mLastOperationMode = WifiStateMachine.this.mOperationalMode;
        }

        public boolean processMessage(Message message) throws Throwable {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SCAN /* 131143 */:
                    WifiStateMachine.this.noteScanStart(message.arg1, (WorkSource) message.obj);
                    WifiStateMachine.this.startScanNative(1);
                    return true;
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                    if (message.arg1 != 1) {
                        return true;
                    }
                    if (this.mLastOperationMode == 3) {
                        WifiStateMachine.this.setWifiState(3);
                        WifiStateMachine.this.mWifiConfigStore.loadAndEnableAllNetworks();
                        WifiStateMachine.this.mWifiP2pChannel.sendMessage(WifiStateMachine.CMD_ENABLE_P2P);
                    } else {
                        WifiStateMachine.this.mWifiConfigStore.enableAllNetworks();
                    }
                    WifiStateMachine.this.mWifiNative.reconnect();
                    WifiStateMachine.this.mOperationalMode = 1;
                    WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                    wifiStateMachine.transitionTo(wifiStateMachine.mDisconnectedState);
                    return true;
                default:
                    return false;
            }
        }
    }

    class ConnectModeState extends State {
        ConnectModeState() {
        }

        public boolean processMessage(Message message) {
            WpsResult wpsResultStartWpsPbc;
            int i = message.what;
            switch (i) {
                case WifiStateMachine.CMD_ADD_OR_UPDATE_NETWORK /* 131124 */:
                    WifiConfiguration wifiConfiguration = (WifiConfiguration) message.obj;
                    WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                    wifiStateMachine.replyToMessage(message, WifiStateMachine.CMD_ADD_OR_UPDATE_NETWORK, wifiStateMachine.mWifiConfigStore.addOrUpdateNetwork(wifiConfiguration));
                    return true;
                case WifiStateMachine.CMD_REMOVE_NETWORK /* 131125 */:
                    WifiStateMachine.this.replyToMessage(message, message.what, WifiStateMachine.this.mWifiConfigStore.removeNetwork(message.arg1) ? 1 : -1);
                    return true;
                case WifiStateMachine.CMD_ENABLE_NETWORK /* 131126 */:
                    WifiStateMachine.this.replyToMessage(message, message.what, WifiStateMachine.this.mWifiConfigStore.enableNetwork(message.arg1, message.arg2 == 1) ? 1 : -1);
                    return true;
                case WifiStateMachine.CMD_ENABLE_ALL_NETWORKS /* 131127 */:
                    long jElapsedRealtime = SystemClock.elapsedRealtime();
                    if (jElapsedRealtime - WifiStateMachine.this.mLastEnableAllNetworksTime > 600000) {
                        WifiStateMachine.this.mWifiConfigStore.enableAllNetworks();
                        WifiStateMachine.this.mLastEnableAllNetworksTime = jElapsedRealtime;
                    }
                    return true;
                case WifiStateMachine.CMD_BLACKLIST_NETWORK /* 131128 */:
                    WifiStateMachine.this.mWifiNative.addToBlacklist((String) message.obj);
                    return true;
                case WifiStateMachine.CMD_CLEAR_BLACKLIST /* 131129 */:
                    WifiStateMachine.this.mWifiNative.clearBlacklist();
                    return true;
                case WifiStateMachine.CMD_SAVE_CONFIG /* 131130 */:
                    WifiStateMachine.this.replyToMessage(message, WifiStateMachine.CMD_SAVE_CONFIG, WifiStateMachine.this.mWifiConfigStore.saveConfig() ? 1 : -1);
                    IBackupManager iBackupManagerAsInterface = IBackupManager.Stub.asInterface(ServiceManager.getService(Context.BACKUP_SERVICE));
                    if (iBackupManagerAsInterface != null) {
                        try {
                            iBackupManagerAsInterface.dataChanged("com.android.providers.settings");
                            break;
                        } catch (Exception unused) {
                        }
                    }
                    return true;
                case WifiStateMachine.CMD_GET_CONFIGURED_NETWORKS /* 131131 */:
                    WifiStateMachine.this.replyToMessage(message, message.what, WifiStateMachine.this.mWifiConfigStore.getConfiguredNetworks());
                    return true;
                default:
                    switch (i) {
                        case WifiStateMachine.CMD_DISCONNECT /* 131145 */:
                            WifiStateMachine.this.mWifiNative.disconnect();
                            return true;
                        case WifiStateMachine.CMD_RECONNECT /* 131146 */:
                            WifiStateMachine.this.mWifiNative.reconnect();
                            return true;
                        case WifiStateMachine.CMD_REASSOCIATE /* 131147 */:
                            WifiStateMachine.this.mWifiNative.reassociate();
                            return true;
                        default:
                            switch (i) {
                                case WifiStateMachine.CMD_RELOAD_TLS_AND_RECONNECT /* 131214 */:
                                    if (WifiStateMachine.this.mWifiConfigStore.needsUnlockedKeyStore()) {
                                        WifiStateMachine.this.logd("Reconnecting to give a chance to un-connected TLS networks");
                                        WifiStateMachine.this.mWifiNative.disconnect();
                                        WifiStateMachine.this.mWifiNative.reconnect();
                                    }
                                    return true;
                                case WifiP2pService.DISCONNECT_WIFI_REQUEST /* 143372 */:
                                    if (message.arg1 == 1) {
                                        WifiStateMachine.this.mWifiNative.disconnect();
                                        WifiStateMachine.this.mTemporarilyDisconnectWifi = true;
                                    } else {
                                        WifiStateMachine.this.mWifiNative.reconnect();
                                        WifiStateMachine.this.mTemporarilyDisconnectWifi = false;
                                    }
                                    return true;
                                case WifiMonitor.ASSOCIATION_REJECTION_EVENT /* 147499 */:
                                    WifiStateMachine.this.mSupplicantStateTracker.sendMessage(WifiMonitor.ASSOCIATION_REJECTION_EVENT);
                                    return true;
                                case WifiManager.CONNECT_NETWORK /* 151553 */:
                                    int networkId = message.arg1;
                                    WifiConfiguration wifiConfiguration2 = (WifiConfiguration) message.obj;
                                    if (wifiConfiguration2 != null) {
                                        if (!WifiStateMachine.this.isValidAscii(wifiConfiguration2)) {
                                            wifiConfiguration2.isValidAscii = false;
                                        }
                                        networkId = WifiStateMachine.this.mWifiConfigStore.saveNetwork(wifiConfiguration2).getNetworkId();
                                    }
                                    if (!WifiStateMachine.this.mWifiConfigStore.selectNetwork(networkId) || !WifiStateMachine.this.mWifiNative.reconnect()) {
                                        WifiStateMachine.this.loge("Failed to connect config: " + wifiConfiguration2 + " netId: " + networkId);
                                        WifiStateMachine.this.replyToMessage(message, WifiManager.CONNECT_NETWORK_FAILED, 0);
                                    } else {
                                        WifiStateMachine.this.mSupplicantStateTracker.sendMessage(WifiManager.CONNECT_NETWORK);
                                        WifiStateMachine.this.replyToMessage(message, WifiManager.CONNECT_NETWORK_SUCCEEDED);
                                        WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
                                        wifiStateMachine2.transitionTo(wifiStateMachine2.mDisconnectingState);
                                    }
                                    return true;
                                case WifiManager.FORGET_NETWORK /* 151556 */:
                                    if (WifiStateMachine.this.mWifiConfigStore.forgetNetwork(message.arg1)) {
                                        WifiStateMachine.this.replyToMessage(message, WifiManager.FORGET_NETWORK_SUCCEEDED);
                                    } else {
                                        WifiStateMachine.this.loge("Failed to forget network");
                                        WifiStateMachine.this.replyToMessage(message, WifiManager.FORGET_NETWORK_FAILED, 0);
                                    }
                                    return true;
                                case WifiManager.SAVE_NETWORK /* 151559 */:
                                    WifiConfiguration wifiConfiguration3 = (WifiConfiguration) message.obj;
                                    if (!WifiStateMachine.this.isValidAscii(wifiConfiguration3)) {
                                        wifiConfiguration3.isValidAscii = false;
                                    }
                                    if (WifiStateMachine.this.mWifiConfigStore.saveNetwork(wifiConfiguration3).getNetworkId() != -1) {
                                        WifiStateMachine.this.replyToMessage(message, WifiManager.SAVE_NETWORK_SUCCEEDED);
                                    } else {
                                        WifiStateMachine.this.loge("Failed to save network");
                                        WifiStateMachine.this.replyToMessage(message, WifiManager.SAVE_NETWORK_FAILED, 0);
                                    }
                                    return true;
                                case WifiManager.START_WPS /* 151562 */:
                                    WpsInfo wpsInfo = (WpsInfo) message.obj;
                                    int i2 = wpsInfo.setup;
                                    if (i2 == 0) {
                                        wpsResultStartWpsPbc = WifiStateMachine.this.mWifiConfigStore.startWpsPbc(wpsInfo);
                                    } else if (i2 == 1) {
                                        wpsResultStartWpsPbc = WifiStateMachine.this.mWifiConfigStore.startWpsWithPinFromDevice(wpsInfo);
                                    } else if (i2 == 2) {
                                        wpsResultStartWpsPbc = WifiStateMachine.this.mWifiConfigStore.startWpsWithPinFromAccessPoint(wpsInfo);
                                    } else {
                                        wpsResultStartWpsPbc = new WpsResult(WpsResult.Status.FAILURE);
                                        WifiStateMachine.this.loge("Invalid setup for WPS");
                                    }
                                    if (wpsResultStartWpsPbc.status == WpsResult.Status.SUCCESS) {
                                        WifiStateMachine.this.replyToMessage(message, WifiManager.START_WPS_SUCCEEDED, wpsResultStartWpsPbc);
                                        WifiStateMachine wifiStateMachine3 = WifiStateMachine.this;
                                        wifiStateMachine3.transitionTo(wifiStateMachine3.mWpsRunningState);
                                    } else {
                                        WifiStateMachine.this.loge("Failed to start WPS with config " + wpsInfo.toString());
                                        WifiStateMachine.this.replyToMessage(message, WifiManager.WPS_FAILED, 0);
                                    }
                                    return true;
                                case WifiManager.DISABLE_NETWORK /* 151569 */:
                                    if (WifiStateMachine.this.mWifiConfigStore.disableNetwork(message.arg1, 0)) {
                                        WifiStateMachine.this.replyToMessage(message, WifiManager.DISABLE_NETWORK_SUCCEEDED);
                                    } else {
                                        WifiStateMachine.this.replyToMessage(message, WifiManager.DISABLE_NETWORK_FAILED, 0);
                                    }
                                    return true;
                                default:
                                    switch (i) {
                                        case WifiMonitor.NETWORK_CONNECTION_EVENT /* 147459 */:
                                            WifiStateMachine.this.mLastNetworkId = message.arg1;
                                            WifiStateMachine.this.mLastBssid = (String) message.obj;
                                            WifiStateMachine.this.mWifiInfo.setBSSID(WifiStateMachine.this.mLastBssid);
                                            WifiStateMachine.this.mWifiInfo.setNetworkId(WifiStateMachine.this.mLastNetworkId);
                                            WifiStateMachine.this.setNetworkDetailedState(NetworkInfo.DetailedState.OBTAINING_IPADDR);
                                            WifiStateMachine wifiStateMachine4 = WifiStateMachine.this;
                                            wifiStateMachine4.sendNetworkStateChangeBroadcast(wifiStateMachine4.mLastBssid);
                                            WifiStateMachine wifiStateMachine5 = WifiStateMachine.this;
                                            wifiStateMachine5.transitionTo(wifiStateMachine5.mObtainingIpState);
                                            return true;
                                        case WifiMonitor.NETWORK_DISCONNECTION_EVENT /* 147460 */:
                                            WifiStateMachine.this.handleNetworkDisconnect();
                                            WifiStateMachine wifiStateMachine6 = WifiStateMachine.this;
                                            wifiStateMachine6.transitionTo(wifiStateMachine6.mDisconnectedState);
                                            return true;
                                        default:
                                            switch (i) {
                                                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                                                    SupplicantState supplicantStateHandleSupplicantStateChange = WifiStateMachine.this.handleSupplicantStateChange(message);
                                                    if (!SupplicantState.isDriverActive(supplicantStateHandleSupplicantStateChange)) {
                                                        if (WifiStateMachine.this.mNetworkInfo.getState() != NetworkInfo.State.DISCONNECTED) {
                                                            WifiStateMachine.this.handleNetworkDisconnect();
                                                        }
                                                        WifiStateMachine.this.log("Detected an interface down, restart driver");
                                                        WifiStateMachine wifiStateMachine7 = WifiStateMachine.this;
                                                        wifiStateMachine7.transitionTo(wifiStateMachine7.mDriverStoppedState);
                                                        WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_START_DRIVER);
                                                    } else if (supplicantStateHandleSupplicantStateChange == SupplicantState.DISCONNECTED && WifiStateMachine.this.mNetworkInfo.getState() != NetworkInfo.State.DISCONNECTED) {
                                                        WifiStateMachine.this.handleNetworkDisconnect();
                                                        WifiStateMachine wifiStateMachine8 = WifiStateMachine.this;
                                                        wifiStateMachine8.transitionTo(wifiStateMachine8.mDisconnectedState);
                                                    }
                                                    return true;
                                                case WifiMonitor.AUTHENTICATION_FAILURE_EVENT /* 147463 */:
                                                    WifiStateMachine.this.mSupplicantStateTracker.sendMessage(WifiMonitor.AUTHENTICATION_FAILURE_EVENT);
                                                    return true;
                                                default:
                                                    return false;
                                            }
                                    }
                            }
                    }
            }
        }
    }

    class L2ConnectedState extends State {
        L2ConnectedState() {
        }

        public void enter() {
            WifiStateMachine.access$16508(WifiStateMachine.this);
            if (WifiStateMachine.this.mEnableRssiPolling) {
                WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                wifiStateMachine.sendMessage(WifiStateMachine.CMD_RSSI_POLL, wifiStateMachine.mRssiPollToken, 0);
            }
        }

        public void exit() {
            WifiStateMachine.this.handleNetworkDisconnect();
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:50:0x0189 A[RETURN] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean processMessage(android.os.Message r7) {
            /*
                Method dump skipped, instruction units count: 444
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiStateMachine.L2ConnectedState.processMessage(android.os.Message):boolean");
        }
    }

    class ObtainingIpState extends State {
        ObtainingIpState() {
        }

        public void enter() {
            if (!WifiStateMachine.this.mWifiConfigStore.isUsingStaticIp(WifiStateMachine.this.mLastNetworkId)) {
                WifiStateMachine.this.startDhcp();
                return;
            }
            WifiStateMachine.this.stopDhcp();
            DhcpResults dhcpResults = new DhcpResults(WifiStateMachine.this.mWifiConfigStore.getLinkProperties(WifiStateMachine.this.mLastNetworkId));
            InterfaceConfiguration interfaceConfiguration = new InterfaceConfiguration();
            Iterator<LinkAddress> it = dhcpResults.linkProperties.getLinkAddresses().iterator();
            if (!it.hasNext()) {
                WifiStateMachine.this.loge("Static IP lacks address");
                WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_STATIC_IP_FAILURE);
                return;
            }
            interfaceConfiguration.setLinkAddress(it.next());
            interfaceConfiguration.setInterfaceUp();
            try {
                WifiStateMachine.this.mNwService.setInterfaceConfig(WifiStateMachine.this.mInterfaceName, interfaceConfiguration);
                WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_STATIC_IP_SUCCESS, dhcpResults);
            } catch (RemoteException e) {
                WifiStateMachine.this.loge("Static IP configuration failed: " + e);
                WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_STATIC_IP_FAILURE);
            } catch (IllegalStateException e2) {
                WifiStateMachine.this.loge("Static IP configuration failed: " + e2);
                WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_STATIC_IP_FAILURE);
            }
        }

        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_STATIC_IP_SUCCESS /* 131087 */:
                    WifiStateMachine.this.handleSuccessfulIpConfiguration((DhcpResults) message.obj);
                    WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                    wifiStateMachine.transitionTo(wifiStateMachine.mVerifyingLinkState);
                    return true;
                case WifiStateMachine.CMD_STATIC_IP_FAILURE /* 131088 */:
                    WifiStateMachine.this.handleFailedIpConfiguration();
                    WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
                    wifiStateMachine2.transitionTo(wifiStateMachine2.mDisconnectingState);
                    return true;
                case WifiStateMachine.CMD_START_SCAN /* 131143 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiStateMachine.CMD_SET_HIGH_PERF_MODE /* 131149 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiManager.SAVE_NETWORK /* 151559 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                default:
                    return false;
            }
        }
    }

    class VerifyingLinkState extends State {
        VerifyingLinkState() {
        }

        public void enter() {
            WifiStateMachine.this.log(getName() + " enter");
            WifiStateMachine.this.setNetworkDetailedState(NetworkInfo.DetailedState.VERIFYING_POOR_LINK);
            WifiStateMachine.this.mWifiConfigStore.updateStatus(WifiStateMachine.this.mLastNetworkId, NetworkInfo.DetailedState.VERIFYING_POOR_LINK);
            WifiStateMachine wifiStateMachine = WifiStateMachine.this;
            wifiStateMachine.sendNetworkStateChangeBroadcast(wifiStateMachine.mLastBssid);
        }

        public boolean processMessage(Message message) {
            switch (message.what) {
                case 135189:
                    WifiStateMachine.this.log(getName() + " POOR_LINK_DETECTED: no transition");
                    return true;
                case 135190:
                    WifiStateMachine.this.log(getName() + " GOOD_LINK_DETECTED: transition to captive portal check");
                    WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                    wifiStateMachine.transitionTo(wifiStateMachine.mCaptivePortalCheckState);
                    return true;
                default:
                    return false;
            }
        }
    }

    class CaptivePortalCheckState extends State {
        CaptivePortalCheckState() {
        }

        public void enter() {
            WifiStateMachine.this.log(getName() + " enter");
            WifiStateMachine.this.setNetworkDetailedState(NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK);
            WifiStateMachine.this.mWifiConfigStore.updateStatus(WifiStateMachine.this.mLastNetworkId, NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK);
            WifiStateMachine wifiStateMachine = WifiStateMachine.this;
            wifiStateMachine.sendNetworkStateChangeBroadcast(wifiStateMachine.mLastBssid);
        }

        public boolean processMessage(Message message) {
            if (message.what != WifiStateMachine.CMD_CAPTIVE_CHECK_COMPLETE) {
                return false;
            }
            WifiStateMachine.this.log(getName() + " CMD_CAPTIVE_CHECK_COMPLETE");
            try {
                WifiStateMachine.this.mNwService.enableIpv6(WifiStateMachine.this.mInterfaceName);
            } catch (RemoteException e) {
                WifiStateMachine.this.loge("Failed to enable IPv6: " + e);
            } catch (IllegalStateException e2) {
                WifiStateMachine.this.loge("Failed to enable IPv6: " + e2);
            }
            WifiStateMachine.this.setNetworkDetailedState(NetworkInfo.DetailedState.CONNECTED);
            WifiStateMachine.this.mWifiConfigStore.updateStatus(WifiStateMachine.this.mLastNetworkId, NetworkInfo.DetailedState.CONNECTED);
            WifiStateMachine wifiStateMachine = WifiStateMachine.this;
            wifiStateMachine.sendNetworkStateChangeBroadcast(wifiStateMachine.mLastBssid);
            WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
            wifiStateMachine2.transitionTo(wifiStateMachine2.mConnectedState);
            return true;
        }
    }

    class ConnectedState extends State {
        ConnectedState() {
        }

        public boolean processMessage(Message message) {
            if (message.what != 135189) {
                return false;
            }
            try {
                WifiStateMachine.this.mNwService.disableIpv6(WifiStateMachine.this.mInterfaceName);
            } catch (RemoteException e) {
                WifiStateMachine.this.loge("Failed to disable IPv6: " + e);
            } catch (IllegalStateException e2) {
                WifiStateMachine.this.loge("Failed to disable IPv6: " + e2);
            }
            WifiStateMachine.this.setNetworkDetailedState(NetworkInfo.DetailedState.DISCONNECTED);
            WifiStateMachine.this.mWifiConfigStore.updateStatus(WifiStateMachine.this.mLastNetworkId, NetworkInfo.DetailedState.DISCONNECTED);
            WifiStateMachine wifiStateMachine = WifiStateMachine.this;
            wifiStateMachine.sendNetworkStateChangeBroadcast(wifiStateMachine.mLastBssid);
            WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
            wifiStateMachine2.transitionTo(wifiStateMachine2.mVerifyingLinkState);
            return true;
        }

        public void exit() {
            WifiStateMachine.this.checkAndSetConnectivityInstance();
            WifiStateMachine.this.mCm.requestNetworkTransitionWakelock(getName());
        }
    }

    class DisconnectingState extends State {
        DisconnectingState() {
        }

        public boolean processMessage(Message message) {
            int i = message.what;
            if (i != WifiStateMachine.CMD_SET_OPERATIONAL_MODE) {
                if (i != 147462) {
                    return false;
                }
                WifiStateMachine.this.deferMessage(message);
                WifiStateMachine.this.handleNetworkDisconnect();
                WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                wifiStateMachine.transitionTo(wifiStateMachine.mDisconnectedState);
            } else if (message.arg1 != 1) {
                WifiStateMachine.this.deferMessage(message);
            }
            return true;
        }
    }

    class DisconnectedState extends State {
        private boolean mAlarmEnabled = false;
        private long mFrameworkScanIntervalMs;

        DisconnectedState() {
        }

        private void setScanAlarm(boolean z) {
            if (z == this.mAlarmEnabled) {
                return;
            }
            if (!z) {
                WifiStateMachine.this.mAlarmManager.cancel(WifiStateMachine.this.mScanIntent);
                this.mAlarmEnabled = false;
            } else if (this.mFrameworkScanIntervalMs > 0) {
                AlarmManager alarmManager = WifiStateMachine.this.mAlarmManager;
                long jCurrentTimeMillis = System.currentTimeMillis();
                long j = this.mFrameworkScanIntervalMs;
                alarmManager.setRepeating(0, jCurrentTimeMillis + j, j, WifiStateMachine.this.mScanIntent);
                this.mAlarmEnabled = true;
            }
        }

        public void enter() {
            if (WifiStateMachine.this.mTemporarilyDisconnectWifi) {
                WifiStateMachine.this.mWifiP2pChannel.sendMessage(WifiP2pService.DISCONNECT_WIFI_RESPONSE);
                return;
            }
            this.mFrameworkScanIntervalMs = Settings.Global.getLong(WifiStateMachine.this.mContext.getContentResolver(), Settings.Global.WIFI_FRAMEWORK_SCAN_INTERVAL_MS, WifiStateMachine.this.mDefaultFrameworkScanIntervalMs);
            if (WifiStateMachine.this.mEnableBackgroundScan) {
                if (!WifiStateMachine.this.mScanResultIsPending) {
                    WifiStateMachine.this.mWifiNative.enableBackgroundScan(true);
                }
            } else {
                setScanAlarm(true);
            }
            if (WifiStateMachine.this.mP2pConnected.get() || WifiStateMachine.this.mWifiConfigStore.getConfiguredNetworks().size() != 0) {
                return;
            }
            WifiStateMachine wifiStateMachine = WifiStateMachine.this;
            wifiStateMachine.sendMessageDelayed(wifiStateMachine.obtainMessage(WifiStateMachine.CMD_NO_NETWORKS_PERIODIC_SCAN, WifiStateMachine.access$20904(wifiStateMachine), 0), WifiStateMachine.this.mSupplicantScanIntervalMs);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_REMOVE_NETWORK /* 131125 */:
                case WifiManager.FORGET_NETWORK /* 151556 */:
                    WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                    wifiStateMachine.sendMessageDelayed(wifiStateMachine.obtainMessage(WifiStateMachine.CMD_NO_NETWORKS_PERIODIC_SCAN, WifiStateMachine.access$20904(wifiStateMachine), 0), WifiStateMachine.this.mSupplicantScanIntervalMs);
                    return false;
                case WifiStateMachine.CMD_START_SCAN /* 131143 */:
                    if (WifiStateMachine.this.mEnableBackgroundScan) {
                        WifiStateMachine.this.mWifiNative.enableBackgroundScan(false);
                    }
                    return false;
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                    if (message.arg1 == 1) {
                        return true;
                    }
                    WifiStateMachine.this.mOperationalMode = message.arg1;
                    WifiStateMachine.this.mWifiConfigStore.disableAllNetworks();
                    if (WifiStateMachine.this.mOperationalMode == 3) {
                        WifiStateMachine.this.mWifiP2pChannel.sendMessage(WifiStateMachine.CMD_DISABLE_P2P_REQ);
                        WifiStateMachine.this.setWifiState(1);
                    }
                    WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
                    wifiStateMachine2.transitionTo(wifiStateMachine2.mScanModeState);
                    return true;
                case WifiStateMachine.CMD_RECONNECT /* 131146 */:
                case WifiStateMachine.CMD_REASSOCIATE /* 131147 */:
                    break;
                case WifiStateMachine.CMD_NO_NETWORKS_PERIODIC_SCAN /* 131160 */:
                    if (WifiStateMachine.this.mP2pConnected.get() || message.arg1 != WifiStateMachine.this.mPeriodicScanToken || WifiStateMachine.this.mWifiConfigStore.getConfiguredNetworks().size() != 0) {
                        return true;
                    }
                    WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_START_SCAN, -1, 0, (WorkSource) null);
                    WifiStateMachine wifiStateMachine3 = WifiStateMachine.this;
                    wifiStateMachine3.sendMessageDelayed(wifiStateMachine3.obtainMessage(WifiStateMachine.CMD_NO_NETWORKS_PERIODIC_SCAN, WifiStateMachine.access$20904(wifiStateMachine3), 0), WifiStateMachine.this.mSupplicantScanIntervalMs);
                    return true;
                case WifiStateMachine.CMD_ENABLE_BACKGROUND_SCAN /* 131163 */:
                    WifiStateMachine.this.mEnableBackgroundScan = message.arg1 == 1;
                    if (WifiStateMachine.this.mEnableBackgroundScan) {
                        WifiStateMachine.this.mWifiNative.enableBackgroundScan(true);
                        setScanAlarm(false);
                        return true;
                    }
                    WifiStateMachine.this.mWifiNative.enableBackgroundScan(false);
                    setScanAlarm(true);
                    return true;
                case WifiP2pService.P2P_CONNECTION_CHANGED /* 143371 */:
                    WifiStateMachine.this.mP2pConnected.set(((NetworkInfo) message.obj).isConnected());
                    if (WifiStateMachine.this.mP2pConnected.get()) {
                        WifiStateMachine.this.mWifiNative.setScanInterval(((int) Settings.Global.getLong(WifiStateMachine.this.mContext.getContentResolver(), Settings.Global.WIFI_SCAN_INTERVAL_WHEN_P2P_CONNECTED_MS, WifiStateMachine.this.mContext.getResources().getInteger(17694736))) / 1000);
                    } else if (WifiStateMachine.this.mWifiConfigStore.getConfiguredNetworks().size() == 0) {
                        WifiStateMachine wifiStateMachine4 = WifiStateMachine.this;
                        wifiStateMachine4.sendMessageDelayed(wifiStateMachine4.obtainMessage(WifiStateMachine.CMD_NO_NETWORKS_PERIODIC_SCAN, WifiStateMachine.access$20904(wifiStateMachine4), 0), WifiStateMachine.this.mSupplicantScanIntervalMs);
                    }
                    break;
                case WifiMonitor.NETWORK_DISCONNECTION_EVENT /* 147460 */:
                    return true;
                case WifiMonitor.SCAN_RESULTS_EVENT /* 147461 */:
                    if (WifiStateMachine.this.mEnableBackgroundScan && WifiStateMachine.this.mScanResultIsPending) {
                        WifiStateMachine.this.mWifiNative.enableBackgroundScan(true);
                    }
                    return false;
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                    WifiStateMachine.this.setNetworkDetailedState(WifiInfo.getDetailedStateOf(((StateChangeResult) message.obj).state));
                    return false;
                default:
                    return false;
            }
            if (WifiStateMachine.this.mTemporarilyDisconnectWifi) {
                return true;
            }
            return false;
        }

        public void exit() {
            if (WifiStateMachine.this.mEnableBackgroundScan) {
                WifiStateMachine.this.mWifiNative.enableBackgroundScan(false);
            }
            setScanAlarm(false);
        }
    }

    class WpsRunningState extends State {
        private Message mSourceMessage;

        WpsRunningState() {
        }

        public void enter() {
            this.mSourceMessage = Message.obtain(WifiStateMachine.this.getCurrentMessage());
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_ENABLE_NETWORK /* 131126 */:
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                case WifiStateMachine.CMD_RECONNECT /* 131146 */:
                case WifiStateMachine.CMD_REASSOCIATE /* 131147 */:
                case WifiManager.CONNECT_NETWORK /* 151553 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiMonitor.NETWORK_CONNECTION_EVENT /* 147459 */:
                    WifiStateMachine.this.replyToMessage(this.mSourceMessage, WifiManager.WPS_COMPLETED);
                    this.mSourceMessage.recycle();
                    this.mSourceMessage = null;
                    WifiStateMachine.this.deferMessage(message);
                    WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                    wifiStateMachine.transitionTo(wifiStateMachine.mDisconnectedState);
                    return true;
                case WifiMonitor.NETWORK_DISCONNECTION_EVENT /* 147460 */:
                    WifiStateMachine.this.handleNetworkDisconnect();
                    return true;
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                case WifiMonitor.AUTHENTICATION_FAILURE_EVENT /* 147463 */:
                case WifiMonitor.WPS_SUCCESS_EVENT /* 147464 */:
                case WifiMonitor.ASSOCIATION_REJECTION_EVENT /* 147499 */:
                    return true;
                case WifiMonitor.WPS_FAIL_EVENT /* 147465 */:
                    WifiStateMachine.this.replyToMessage(this.mSourceMessage, WifiManager.WPS_FAILED, message.arg1);
                    this.mSourceMessage.recycle();
                    this.mSourceMessage = null;
                    WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
                    wifiStateMachine2.transitionTo(wifiStateMachine2.mDisconnectedState);
                    return true;
                case WifiMonitor.WPS_OVERLAP_EVENT /* 147466 */:
                    WifiStateMachine.this.replyToMessage(this.mSourceMessage, WifiManager.WPS_FAILED, 3);
                    this.mSourceMessage.recycle();
                    this.mSourceMessage = null;
                    WifiStateMachine wifiStateMachine3 = WifiStateMachine.this;
                    wifiStateMachine3.transitionTo(wifiStateMachine3.mDisconnectedState);
                    return true;
                case WifiMonitor.WPS_TIMEOUT_EVENT /* 147467 */:
                    WifiStateMachine.this.replyToMessage(this.mSourceMessage, WifiManager.WPS_FAILED, 7);
                    this.mSourceMessage.recycle();
                    this.mSourceMessage = null;
                    WifiStateMachine wifiStateMachine4 = WifiStateMachine.this;
                    wifiStateMachine4.transitionTo(wifiStateMachine4.mDisconnectedState);
                    return true;
                case WifiManager.START_WPS /* 151562 */:
                    WifiStateMachine.this.replyToMessage(message, WifiManager.WPS_FAILED, 1);
                    return true;
                case WifiManager.CANCEL_WPS /* 151566 */:
                    if (WifiStateMachine.this.mWifiNative.cancelWps()) {
                        WifiStateMachine.this.replyToMessage(message, WifiManager.CANCEL_WPS_SUCCEDED);
                    } else {
                        WifiStateMachine.this.replyToMessage(message, WifiManager.CANCEL_WPS_FAILED, 0);
                    }
                    WifiStateMachine wifiStateMachine5 = WifiStateMachine.this;
                    wifiStateMachine5.transitionTo(wifiStateMachine5.mDisconnectedState);
                    return true;
                default:
                    return false;
            }
        }

        public void exit() throws Throwable {
            WifiStateMachine.this.mWifiConfigStore.enableAllNetworks();
            WifiStateMachine.this.mWifiConfigStore.loadConfiguredNetworks();
        }
    }

    class SoftApStartingState extends State {
        SoftApStartingState() {
        }

        public void enter() {
            Message currentMessage = WifiStateMachine.this.getCurrentMessage();
            if (currentMessage.what == WifiStateMachine.CMD_START_AP) {
                WifiConfiguration wifiConfiguration = (WifiConfiguration) currentMessage.obj;
                if (wifiConfiguration == null) {
                    WifiStateMachine.this.mWifiApConfigChannel.sendMessage(WifiStateMachine.CMD_REQUEST_AP_CONFIG);
                    return;
                } else {
                    WifiStateMachine.this.mWifiApConfigChannel.sendMessage(WifiStateMachine.CMD_SET_AP_CONFIG, wifiConfiguration);
                    WifiStateMachine.this.startSoftApWithConfig(wifiConfiguration);
                    return;
                }
            }
            throw new RuntimeException("Illegal transition to SoftApStartingState: " + currentMessage);
        }

        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SUPPLICANT /* 131083 */:
                case WifiStateMachine.CMD_STOP_SUPPLICANT /* 131084 */:
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                case WifiStateMachine.CMD_STOP_AP /* 131096 */:
                case WifiStateMachine.CMD_TETHER_STATE_CHANGE /* 131101 */:
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiStateMachine.CMD_START_AP_SUCCESS /* 131094 */:
                    WifiStateMachine.this.setWifiApState(13);
                    WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                    wifiStateMachine.transitionTo(wifiStateMachine.mSoftApStartedState);
                    return true;
                case WifiStateMachine.CMD_START_AP_FAILURE /* 131095 */:
                    WifiStateMachine.this.setWifiApState(14);
                    WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
                    wifiStateMachine2.transitionTo(wifiStateMachine2.mInitialState);
                    return true;
                case WifiStateMachine.CMD_RESPONSE_AP_CONFIG /* 131100 */:
                    WifiConfiguration wifiConfiguration = (WifiConfiguration) message.obj;
                    if (wifiConfiguration != null) {
                        WifiStateMachine.this.startSoftApWithConfig(wifiConfiguration);
                        return true;
                    }
                    WifiStateMachine.this.loge("Softap config is null!");
                    WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_START_AP_FAILURE);
                    return true;
                default:
                    return false;
            }
        }
    }

    class SoftApStartedState extends State {
        SoftApStartedState() {
        }

        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SUPPLICANT /* 131083 */:
                    WifiStateMachine.this.loge("Cannot start supplicant with a running soft AP");
                    WifiStateMachine.this.setWifiState(4);
                    return true;
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                    return true;
                case WifiStateMachine.CMD_STOP_AP /* 131096 */:
                    try {
                        WifiStateMachine.this.mNwService.stopAccessPoint(WifiStateMachine.this.mInterfaceName);
                        break;
                    } catch (Exception unused) {
                        WifiStateMachine.this.loge("Exception in stopAccessPoint()");
                    }
                    WifiStateMachine.this.setWifiApState(11);
                    WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                    wifiStateMachine.transitionTo(wifiStateMachine.mInitialState);
                    return true;
                case WifiStateMachine.CMD_TETHER_STATE_CHANGE /* 131101 */:
                    if (!WifiStateMachine.this.startTethering(((TetherStateChange) message.obj).available)) {
                        return true;
                    }
                    WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
                    wifiStateMachine2.transitionTo(wifiStateMachine2.mTetheringState);
                    return true;
                default:
                    return false;
            }
        }
    }

    class TetheringState extends State {
        TetheringState() {
        }

        public void enter() {
            WifiStateMachine wifiStateMachine = WifiStateMachine.this;
            wifiStateMachine.sendMessageDelayed(wifiStateMachine.obtainMessage(WifiStateMachine.CMD_TETHER_NOTIFICATION_TIMED_OUT, WifiStateMachine.access$23204(wifiStateMachine), 0), TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
        }

        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SUPPLICANT /* 131083 */:
                case WifiStateMachine.CMD_STOP_SUPPLICANT /* 131084 */:
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                case WifiStateMachine.CMD_STOP_AP /* 131096 */:
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiStateMachine.CMD_TETHER_STATE_CHANGE /* 131101 */:
                    if (WifiStateMachine.this.isWifiTethered(((TetherStateChange) message.obj).active)) {
                        WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                        wifiStateMachine.transitionTo(wifiStateMachine.mTetheredState);
                    }
                    return true;
                case WifiStateMachine.CMD_TETHER_NOTIFICATION_TIMED_OUT /* 131102 */:
                    if (message.arg1 == WifiStateMachine.this.mTetherToken) {
                        WifiStateMachine.this.loge("Failed to get tether update, shutdown soft access point");
                        WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
                        wifiStateMachine2.transitionTo(wifiStateMachine2.mSoftApStartedState);
                        WifiStateMachine.this.sendMessageAtFrontOfQueue(WifiStateMachine.CMD_STOP_AP);
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    class TetheredState extends State {
        TetheredState() {
        }

        public boolean processMessage(Message message) {
            int i = message.what;
            if (i == WifiStateMachine.CMD_STOP_AP) {
                WifiStateMachine.this.setWifiApState(10);
                WifiStateMachine.this.stopTethering();
                WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                wifiStateMachine.transitionTo(wifiStateMachine.mUntetheringState);
                WifiStateMachine.this.deferMessage(message);
                return true;
            }
            if (i != WifiStateMachine.CMD_TETHER_STATE_CHANGE) {
                return false;
            }
            if (!WifiStateMachine.this.isWifiTethered(((TetherStateChange) message.obj).active)) {
                WifiStateMachine.this.loge("Tethering reports wifi as untethered!, shut down soft Ap");
                WifiStateMachine.this.setHostApRunning(null, false);
                WifiStateMachine.this.setHostApRunning(null, true);
            }
            return true;
        }
    }

    class UntetheringState extends State {
        UntetheringState() {
        }

        public void enter() {
            WifiStateMachine wifiStateMachine = WifiStateMachine.this;
            wifiStateMachine.sendMessageDelayed(wifiStateMachine.obtainMessage(WifiStateMachine.CMD_TETHER_NOTIFICATION_TIMED_OUT, WifiStateMachine.access$23204(wifiStateMachine), 0), TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
        }

        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SUPPLICANT /* 131083 */:
                case WifiStateMachine.CMD_STOP_SUPPLICANT /* 131084 */:
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                case WifiStateMachine.CMD_STOP_AP /* 131096 */:
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiStateMachine.CMD_TETHER_STATE_CHANGE /* 131101 */:
                    if (WifiStateMachine.this.isWifiTethered(((TetherStateChange) message.obj).active)) {
                        return true;
                    }
                    WifiStateMachine wifiStateMachine = WifiStateMachine.this;
                    wifiStateMachine.transitionTo(wifiStateMachine.mSoftApStartedState);
                    return true;
                case WifiStateMachine.CMD_TETHER_NOTIFICATION_TIMED_OUT /* 131102 */:
                    if (message.arg1 != WifiStateMachine.this.mTetherToken) {
                        return true;
                    }
                    WifiStateMachine.this.loge("Failed to get tether update, force stop access point");
                    WifiStateMachine wifiStateMachine2 = WifiStateMachine.this;
                    wifiStateMachine2.transitionTo(wifiStateMachine2.mSoftApStartedState);
                    return true;
                default:
                    return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void replyToMessage(Message message, int i) {
        if (message.replyTo == null) {
            return;
        }
        Message messageObtainMessageWithArg2 = obtainMessageWithArg2(message);
        messageObtainMessageWithArg2.what = i;
        this.mReplyChannel.replyToMessage(message, messageObtainMessageWithArg2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void replyToMessage(Message message, int i, int i2) {
        if (message.replyTo == null) {
            return;
        }
        Message messageObtainMessageWithArg2 = obtainMessageWithArg2(message);
        messageObtainMessageWithArg2.what = i;
        messageObtainMessageWithArg2.arg1 = i2;
        this.mReplyChannel.replyToMessage(message, messageObtainMessageWithArg2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void replyToMessage(Message message, int i, Object obj) {
        if (message.replyTo == null) {
            return;
        }
        Message messageObtainMessageWithArg2 = obtainMessageWithArg2(message);
        messageObtainMessageWithArg2.what = i;
        messageObtainMessageWithArg2.obj = obj;
        this.mReplyChannel.replyToMessage(message, messageObtainMessageWithArg2);
    }

    private Message obtainMessageWithArg2(Message message) {
        Message messageObtain = Message.obtain();
        messageObtain.arg2 = message.arg2;
        return messageObtain;
    }
}
