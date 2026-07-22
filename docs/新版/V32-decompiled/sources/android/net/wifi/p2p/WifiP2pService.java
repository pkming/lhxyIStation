package android.net.wifi.p2p;

import android.Manifest;
import android.R;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.DhcpResults;
import android.net.DhcpStateMachine;
import android.net.InterfaceConfiguration;
import android.net.LinkAddress;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.wifi.WifiMonitor;
import android.net.wifi.WifiNative;
import android.net.wifi.WifiStateMachine;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.IWifiP2pManager;
import android.net.wifi.p2p.WifiP2pGroupList;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceResponse;
import android.os.Binder;
import android.os.Bundle;
import android.os.INetworkManagementService;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimedRemoteCaller;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.internal.util.AsyncChannel;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/* JADX INFO: loaded from: classes.dex */
public class WifiP2pService extends IWifiP2pManager.Stub {
    private static final String ALLSHARE_CAST_DONGLE_MAC1 = "F8:D0:BD";
    private static final String ALLSHARE_CAST_DONGLE_MAC2 = "D8:57:EF";
    private static final int BASE = 143360;
    public static final int BLOCK_DISCOVERY = 143375;
    private static final boolean DBG = false;
    public static final int DISABLED = 0;
    public static final int DISABLE_P2P_TIMED_OUT = 143366;
    private static final int DISABLE_P2P_WAIT_TIME_MS = 5000;
    public static final int DISCONNECT_WIFI_REQUEST = 143372;
    public static final int DISCONNECT_WIFI_RESPONSE = 143373;
    private static final int DISCOVER_TIMEOUT_S = 120;
    private static final int DROP_WIFI_USER_ACCEPT = 143364;
    private static final int DROP_WIFI_USER_REJECT = 143365;
    public static final int ENABLED = 1;
    public static final int GROUP_CREATING_TIMED_OUT = 143361;
    private static final int GROUP_CREATING_WAIT_TIME_MS = 120000;
    private static final int GROUP_IDLE_TIME_S = 10;
    private static final String NETWORKTYPE = "WIFI_P2P";
    public static final int P2P_CONNECTION_CHANGED = 143371;
    private static final int PEER_CONNECTION_USER_ACCEPT = 143362;
    private static final int PEER_CONNECTION_USER_REJECT = 143363;
    private static final String SERVER_ADDRESS = "192.168.49.1";
    public static final int SET_COUNTRY_CODE = 143376;
    public static final int SET_MIRACAST_MODE = 143374;
    private static final String TAG = "WifiP2pService";
    private static final String WFD_DNSMASQ_PEER = "wlan.wfddnsmasq.peer";
    private static final String WFD_NO_INVITE_DEV_MAC_FILE = "/etc/wfd_blacklist.conf";
    private static final String WFD_P2P_ROLE = "wlan.wfdp2p.role";
    private boolean mAutonomousGroup;
    private Context mContext;
    private DhcpStateMachine mDhcpStateMachine;
    private boolean mDiscoveryBlocked;
    private boolean mDiscoveryStarted;
    private boolean mJoinExistingGroup;
    private String mLastSetCountryCode;
    private String mNoInvitDevMac;
    private String[] mNoInvitDevMacList;
    private Notification mNotification;
    INetworkManagementService mNwService;
    private P2pStateMachine mP2pStateMachine;
    private final boolean mP2pSupported;
    private Properties mProperties;
    private String mServiceDiscReqId;
    private AsyncChannel mWifiChannel;
    private static final Boolean JOIN_GROUP = true;
    private static final Boolean FORM_GROUP = false;
    private static final Boolean RELOAD = true;
    private static final Boolean NO_RELOAD = false;
    private static int mGroupCreatingTimeoutIndex = 0;
    private static int mDisableP2pTimeoutIndex = 0;
    private static final String[] DHCP_RANGE = {"192.168.49.2", "192.168.49.254"};
    private AsyncChannel mReplyChannel = new AsyncChannel();
    private WifiP2pDevice mThisDevice = new WifiP2pDevice();
    private boolean mDiscoveryPostponed = false;
    private boolean mTempoarilyDisconnectedWifi = false;
    private byte mServiceTransactionId = 0;
    private HashMap<Messenger, ClientInfo> mClientInfoList = new HashMap<>();
    private String mInterface = "p2p0";
    private NetworkInfo mNetworkInfo = new NetworkInfo(13, 0, NETWORKTYPE, "");

    static /* synthetic */ byte access$12204(WifiP2pService wifiP2pService) {
        byte b = (byte) (wifiP2pService.mServiceTransactionId + 1);
        wifiP2pService.mServiceTransactionId = b;
        return b;
    }

    static /* synthetic */ int access$1304() {
        int i = mDisableP2pTimeoutIndex + 1;
        mDisableP2pTimeoutIndex = i;
        return i;
    }

    static /* synthetic */ int access$1704() {
        int i = mGroupCreatingTimeoutIndex + 1;
        mGroupCreatingTimeoutIndex = i;
        return i;
    }

    public enum P2pStatus {
        SUCCESS,
        INFORMATION_IS_CURRENTLY_UNAVAILABLE,
        INCOMPATIBLE_PARAMETERS,
        LIMIT_REACHED,
        INVALID_PARAMETER,
        UNABLE_TO_ACCOMMODATE_REQUEST,
        PREVIOUS_PROTOCOL_ERROR,
        NO_COMMON_CHANNEL,
        UNKNOWN_P2P_GROUP,
        BOTH_GO_INTENT_15,
        INCOMPATIBLE_PROVISIONING_METHOD,
        REJECTED_BY_USER,
        UNKNOWN;

        public static P2pStatus valueOf(int i) {
            switch (i) {
                case 0:
                    return SUCCESS;
                case 1:
                    return INFORMATION_IS_CURRENTLY_UNAVAILABLE;
                case 2:
                    return INCOMPATIBLE_PARAMETERS;
                case 3:
                    return LIMIT_REACHED;
                case 4:
                    return INVALID_PARAMETER;
                case 5:
                    return UNABLE_TO_ACCOMMODATE_REQUEST;
                case 6:
                    return PREVIOUS_PROTOCOL_ERROR;
                case 7:
                    return NO_COMMON_CHANNEL;
                case 8:
                    return UNKNOWN_P2P_GROUP;
                case 9:
                    return BOTH_GO_INTENT_15;
                case 10:
                    return INCOMPATIBLE_PROVISIONING_METHOD;
                case 11:
                    return REJECTED_BY_USER;
                default:
                    return UNKNOWN;
            }
        }
    }

    public WifiP2pService(Context context) {
        this.mContext = context;
        this.mP2pSupported = this.mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT);
        this.mThisDevice.primaryDeviceType = this.mContext.getResources().getString(17039386);
        SystemProperties.set(WFD_P2P_ROLE, "");
        SystemProperties.set(WFD_DNSMASQ_PEER, "");
        this.mProperties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(WFD_NO_INVITE_DEV_MAC_FILE));
            this.mProperties.load(fileInputStream);
            fileInputStream.close();
            String property = this.mProperties.getProperty("NO_INVITE_MAC");
            this.mNoInvitDevMac = property;
            if (property != null) {
                String[] strArrSplit = property.split(",");
                this.mNoInvitDevMacList = strArrSplit;
                if (strArrSplit != null) {
                    for (int i = 0; i < this.mNoInvitDevMacList.length; i++) {
                        Slog.d(TAG, "wfd blacklist mac range " + i + ".[" + this.mNoInvitDevMacList[i] + "]");
                    }
                } else {
                    this.mNoInvitDevMacList = new String[]{ALLSHARE_CAST_DONGLE_MAC1, ALLSHARE_CAST_DONGLE_MAC2};
                }
            }
        } catch (IOException unused) {
            Slog.d(TAG, "Could not open wfd blacklist file [/etc/wfd_blacklist.conf]");
            this.mNoInvitDevMacList = new String[]{ALLSHARE_CAST_DONGLE_MAC1, ALLSHARE_CAST_DONGLE_MAC2};
        }
        P2pStateMachine p2pStateMachine = new P2pStateMachine(TAG, this.mP2pSupported);
        this.mP2pStateMachine = p2pStateMachine;
        p2pStateMachine.start();
    }

    public void connectivityServiceReady() {
        this.mNwService = INetworkManagementService.Stub.asInterface(ServiceManager.getService(Context.NETWORKMANAGEMENT_SERVICE));
    }

    private void enforceAccessPermission() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.ACCESS_WIFI_STATE, TAG);
    }

    private void enforceChangePermission() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CHANGE_WIFI_STATE, TAG);
    }

    private void enforceConnectivityInternalPermission() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
    }

    @Override // android.net.wifi.p2p.IWifiP2pManager
    public Messenger getMessenger() {
        enforceAccessPermission();
        enforceChangePermission();
        return new Messenger(this.mP2pStateMachine.getHandler());
    }

    @Override // android.net.wifi.p2p.IWifiP2pManager
    public void setMiracastMode(int i) {
        enforceConnectivityInternalPermission();
        this.mP2pStateMachine.sendMessage(SET_MIRACAST_MODE, i);
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            printWriter.println("Permission Denial: can't dump WifiP2pService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        this.mP2pStateMachine.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("mAutonomousGroup " + this.mAutonomousGroup);
        printWriter.println("mJoinExistingGroup " + this.mJoinExistingGroup);
        printWriter.println("mDiscoveryStarted " + this.mDiscoveryStarted);
        printWriter.println("mNetworkInfo " + this.mNetworkInfo);
        printWriter.println("mTempoarilyDisconnectedWifi " + this.mTempoarilyDisconnectedWifi);
        printWriter.println("mServiceDiscReqId " + this.mServiceDiscReqId);
        printWriter.println();
    }

    private class P2pStateMachine extends StateMachine {
        private DefaultState mDefaultState;
        private FrequencyConflictState mFrequencyConflictState;
        private WifiP2pGroup mGroup;
        private GroupCreatedState mGroupCreatedState;
        private GroupCreatingState mGroupCreatingState;
        private GroupNegotiationState mGroupNegotiationState;
        private final WifiP2pGroupList mGroups;
        private InactiveState mInactiveState;
        private OngoingGroupRemovalState mOngoingGroupRemovalState;
        private P2pDisabledState mP2pDisabledState;
        private P2pDisablingState mP2pDisablingState;
        private P2pEnabledState mP2pEnabledState;
        private P2pEnablingState mP2pEnablingState;
        private P2pNotSupportedState mP2pNotSupportedState;
        private final WifiP2pDeviceList mPeers;
        private final WifiP2pDeviceList mPeersLostDuringConnection;
        private ProvisionDiscoveryState mProvisionDiscoveryState;
        private WifiP2pGroup mSavedP2pGroup;
        private WifiP2pConfig mSavedPeerConfig;
        private UserAuthorizingInviteRequestState mUserAuthorizingInviteRequestState;
        private UserAuthorizingJoinState mUserAuthorizingJoinState;
        private UserAuthorizingNegotiationRequestState mUserAuthorizingNegotiationRequestState;
        private WifiMonitor mWifiMonitor;
        private WifiNative mWifiNative;
        private WifiP2pDevice mWifiP2pDevice;
        private final WifiP2pInfo mWifiP2pInfo;

        P2pStateMachine(String str, boolean z) {
            super(str);
            this.mDefaultState = new DefaultState();
            this.mP2pNotSupportedState = new P2pNotSupportedState();
            this.mP2pDisablingState = new P2pDisablingState();
            this.mP2pDisabledState = new P2pDisabledState();
            this.mP2pEnablingState = new P2pEnablingState();
            this.mP2pEnabledState = new P2pEnabledState();
            this.mInactiveState = new InactiveState();
            this.mGroupCreatingState = new GroupCreatingState();
            this.mUserAuthorizingInviteRequestState = new UserAuthorizingInviteRequestState();
            this.mUserAuthorizingNegotiationRequestState = new UserAuthorizingNegotiationRequestState();
            this.mProvisionDiscoveryState = new ProvisionDiscoveryState();
            this.mGroupNegotiationState = new GroupNegotiationState();
            this.mFrequencyConflictState = new FrequencyConflictState();
            this.mGroupCreatedState = new GroupCreatedState();
            this.mUserAuthorizingJoinState = new UserAuthorizingJoinState();
            this.mOngoingGroupRemovalState = new OngoingGroupRemovalState();
            this.mWifiNative = new WifiNative(WifiP2pService.this.mInterface);
            this.mWifiMonitor = new WifiMonitor(this, this.mWifiNative);
            this.mPeers = new WifiP2pDeviceList();
            this.mPeersLostDuringConnection = new WifiP2pDeviceList();
            this.mGroups = new WifiP2pGroupList(null, new WifiP2pGroupList.GroupDeleteListener() { // from class: android.net.wifi.p2p.WifiP2pService.P2pStateMachine.1
                @Override // android.net.wifi.p2p.WifiP2pGroupList.GroupDeleteListener
                public void onDeleteGroup(int i) {
                    P2pStateMachine.this.mWifiNative.removeNetwork(i);
                    P2pStateMachine.this.mWifiNative.saveConfig();
                    P2pStateMachine.this.sendP2pPersistentGroupsChangedBroadcast();
                }
            });
            this.mWifiP2pInfo = new WifiP2pInfo();
            this.mSavedPeerConfig = new WifiP2pConfig();
            addState(this.mDefaultState);
            addState(this.mP2pNotSupportedState, this.mDefaultState);
            addState(this.mP2pDisablingState, this.mDefaultState);
            addState(this.mP2pDisabledState, this.mDefaultState);
            addState(this.mP2pEnablingState, this.mDefaultState);
            addState(this.mP2pEnabledState, this.mDefaultState);
            addState(this.mInactiveState, this.mP2pEnabledState);
            addState(this.mGroupCreatingState, this.mP2pEnabledState);
            addState(this.mUserAuthorizingInviteRequestState, this.mGroupCreatingState);
            addState(this.mUserAuthorizingNegotiationRequestState, this.mGroupCreatingState);
            addState(this.mProvisionDiscoveryState, this.mGroupCreatingState);
            addState(this.mGroupNegotiationState, this.mGroupCreatingState);
            addState(this.mFrequencyConflictState, this.mGroupCreatingState);
            addState(this.mGroupCreatedState, this.mP2pEnabledState);
            addState(this.mUserAuthorizingJoinState, this.mGroupCreatedState);
            addState(this.mOngoingGroupRemovalState, this.mGroupCreatedState);
            if (z) {
                setInitialState(this.mP2pDisabledState);
            } else {
                setInitialState(this.mP2pNotSupportedState);
            }
            setLogRecSize(50);
            setLogOnlyTransitions(true);
        }

        class DefaultState extends State {
            DefaultState() {
            }

            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            /* JADX WARN: Removed duplicated region for block: B:63:0x024b A[FALL_THROUGH, RETURN] */
            /* JADX WARN: Removed duplicated region for block: B:63:0x024b A[RETURN] */
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public boolean processMessage(android.os.Message r7) {
                /*
                    Method dump skipped, instruction units count: 802
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.p2p.WifiP2pService.P2pStateMachine.DefaultState.processMessage(android.os.Message):boolean");
            }
        }

        class P2pNotSupportedState extends State {
            P2pNotSupportedState() {
            }

            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiP2pManager.DISCOVER_PEERS /* 139265 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.DISCOVER_PEERS_FAILED, 1);
                        return true;
                    case WifiP2pManager.STOP_DISCOVERY /* 139268 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.STOP_DISCOVERY_FAILED, 1);
                        return true;
                    case WifiP2pManager.CONNECT /* 139271 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CONNECT_FAILED, 1);
                        return true;
                    case WifiP2pManager.CANCEL_CONNECT /* 139274 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CANCEL_CONNECT_FAILED, 1);
                        return true;
                    case WifiP2pManager.CREATE_GROUP /* 139277 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CREATE_GROUP_FAILED, 1);
                        return true;
                    case WifiP2pManager.REMOVE_GROUP /* 139280 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.REMOVE_GROUP_FAILED, 1);
                        return true;
                    case WifiP2pManager.ADD_LOCAL_SERVICE /* 139292 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.ADD_LOCAL_SERVICE_FAILED, 1);
                        return true;
                    case WifiP2pManager.REMOVE_LOCAL_SERVICE /* 139295 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.REMOVE_LOCAL_SERVICE_FAILED, 1);
                        return true;
                    case WifiP2pManager.CLEAR_LOCAL_SERVICES /* 139298 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CLEAR_LOCAL_SERVICES_FAILED, 1);
                        return true;
                    case WifiP2pManager.ADD_SERVICE_REQUEST /* 139301 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.ADD_SERVICE_REQUEST_FAILED, 1);
                        return true;
                    case WifiP2pManager.REMOVE_SERVICE_REQUEST /* 139304 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.REMOVE_SERVICE_REQUEST_FAILED, 1);
                        return true;
                    case WifiP2pManager.CLEAR_SERVICE_REQUESTS /* 139307 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CLEAR_SERVICE_REQUESTS_FAILED, 1);
                        return true;
                    case WifiP2pManager.DISCOVER_SERVICES /* 139310 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.DISCOVER_SERVICES_FAILED, 1);
                        return true;
                    case WifiP2pManager.SET_DEVICE_NAME /* 139315 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.SET_DEVICE_NAME_FAILED, 1);
                        return true;
                    case WifiP2pManager.DELETE_PERSISTENT_GROUP /* 139318 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.DELETE_PERSISTENT_GROUP, 1);
                        return true;
                    case WifiP2pManager.SET_WFD_INFO /* 139323 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.SET_WFD_INFO_FAILED, 1);
                        return true;
                    case WifiP2pManager.START_WPS /* 139326 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.START_WPS_FAILED, 1);
                        return true;
                    case WifiP2pManager.START_LISTEN /* 139329 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.START_LISTEN_FAILED, 1);
                        return true;
                    case WifiP2pManager.STOP_LISTEN /* 139332 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.STOP_LISTEN_FAILED, 1);
                        return true;
                    default:
                        return false;
                }
            }
        }

        class P2pDisablingState extends State {
            P2pDisablingState() {
            }

            public void enter() {
                P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                p2pStateMachine.sendMessageDelayed(p2pStateMachine.obtainMessage(WifiP2pService.DISABLE_P2P_TIMED_OUT, WifiP2pService.access$1304(), 0), TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
            }

            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiStateMachine.CMD_ENABLE_P2P /* 131203 */:
                    case WifiStateMachine.CMD_DISABLE_P2P_REQ /* 131204 */:
                        P2pStateMachine.this.deferMessage(message);
                        return true;
                    case WifiP2pService.DISABLE_P2P_TIMED_OUT /* 143366 */:
                        if (WifiP2pService.mGroupCreatingTimeoutIndex != message.arg1) {
                            return true;
                        }
                        P2pStateMachine.this.loge("P2p disable timed out");
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        p2pStateMachine.transitionTo(p2pStateMachine.mP2pDisabledState);
                        return true;
                    case WifiMonitor.SUP_DISCONNECTION_EVENT /* 147458 */:
                        P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                        p2pStateMachine2.transitionTo(p2pStateMachine2.mP2pDisabledState);
                        return true;
                    default:
                        return false;
                }
            }

            public void exit() {
                WifiP2pService.this.mWifiChannel.sendMessage(WifiStateMachine.CMD_DISABLE_P2P_RSP);
            }
        }

        class P2pDisabledState extends State {
            public void enter() {
            }

            P2pDisabledState() {
            }

            public boolean processMessage(Message message) {
                if (message.what != 131203) {
                    return false;
                }
                try {
                    WifiP2pService.this.mNwService.setInterfaceUp(WifiP2pService.this.mInterface);
                } catch (RemoteException e) {
                    P2pStateMachine.this.loge("Unable to change interface settings: " + e);
                } catch (IllegalStateException e2) {
                    P2pStateMachine.this.loge("Unable to change interface settings: " + e2);
                }
                P2pStateMachine.this.mWifiMonitor.startMonitoring();
                P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                p2pStateMachine.transitionTo(p2pStateMachine.mP2pEnablingState);
                return true;
            }
        }

        class P2pEnablingState extends State {
            public void enter() {
            }

            P2pEnablingState() {
            }

            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiStateMachine.CMD_ENABLE_P2P /* 131203 */:
                    case WifiStateMachine.CMD_DISABLE_P2P_REQ /* 131204 */:
                        P2pStateMachine.this.deferMessage(message);
                        return true;
                    case WifiMonitor.SUP_CONNECTION_EVENT /* 147457 */:
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        p2pStateMachine.transitionTo(p2pStateMachine.mInactiveState);
                        return true;
                    case WifiMonitor.SUP_DISCONNECTION_EVENT /* 147458 */:
                        P2pStateMachine.this.loge("P2p socket connection failed");
                        P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                        p2pStateMachine2.transitionTo(p2pStateMachine2.mP2pDisabledState);
                        return true;
                    default:
                        return false;
                }
            }
        }

        class P2pEnabledState extends State {
            P2pEnabledState() {
            }

            public void enter() {
                P2pStateMachine.this.sendP2pStateChangedBroadcast(true);
                WifiP2pService.this.mNetworkInfo.setIsAvailable(true);
                P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
                P2pStateMachine.this.initializeP2pSettings();
            }

            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiStateMachine.CMD_ENABLE_P2P /* 131203 */:
                        return true;
                    case WifiStateMachine.CMD_DISABLE_P2P_REQ /* 131204 */:
                        if (P2pStateMachine.this.mPeers.clear()) {
                            P2pStateMachine.this.sendPeersChangedBroadcast();
                        }
                        if (P2pStateMachine.this.mGroups.clear()) {
                            P2pStateMachine.this.sendP2pPersistentGroupsChangedBroadcast();
                        }
                        P2pStateMachine.this.mWifiMonitor.stopMonitoring();
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        p2pStateMachine.transitionTo(p2pStateMachine.mP2pDisablingState);
                        return true;
                    case WifiP2pManager.DISCOVER_PEERS /* 139265 */:
                        if (WifiP2pService.this.mDiscoveryBlocked) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.DISCOVER_PEERS_FAILED, 2);
                        } else {
                            P2pStateMachine.this.clearSupplicantServiceRequest();
                            if (P2pStateMachine.this.mWifiNative.p2pFind(120)) {
                                P2pStateMachine.this.replyToMessage(message, WifiP2pManager.DISCOVER_PEERS_SUCCEEDED);
                                P2pStateMachine.this.sendP2pDiscoveryChangedBroadcast(true);
                            } else {
                                P2pStateMachine.this.replyToMessage(message, WifiP2pManager.DISCOVER_PEERS_FAILED, 0);
                            }
                        }
                        return true;
                    case WifiP2pManager.STOP_DISCOVERY /* 139268 */:
                        if (P2pStateMachine.this.mWifiNative.p2pStopFind()) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.STOP_DISCOVERY_SUCCEEDED);
                        } else {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.STOP_DISCOVERY_FAILED, 0);
                        }
                        return true;
                    case WifiP2pManager.ADD_LOCAL_SERVICE /* 139292 */:
                        if (P2pStateMachine.this.addLocalService(message.replyTo, (WifiP2pServiceInfo) message.obj)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.ADD_LOCAL_SERVICE_SUCCEEDED);
                        } else {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.ADD_LOCAL_SERVICE_FAILED);
                        }
                        return true;
                    case WifiP2pManager.REMOVE_LOCAL_SERVICE /* 139295 */:
                        P2pStateMachine.this.removeLocalService(message.replyTo, (WifiP2pServiceInfo) message.obj);
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.REMOVE_LOCAL_SERVICE_SUCCEEDED);
                        return true;
                    case WifiP2pManager.CLEAR_LOCAL_SERVICES /* 139298 */:
                        P2pStateMachine.this.clearLocalServices(message.replyTo);
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CLEAR_LOCAL_SERVICES_SUCCEEDED);
                        return true;
                    case WifiP2pManager.ADD_SERVICE_REQUEST /* 139301 */:
                        if (!P2pStateMachine.this.addServiceRequest(message.replyTo, (WifiP2pServiceRequest) message.obj)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.ADD_SERVICE_REQUEST_FAILED);
                        } else {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.ADD_SERVICE_REQUEST_SUCCEEDED);
                        }
                        return true;
                    case WifiP2pManager.REMOVE_SERVICE_REQUEST /* 139304 */:
                        P2pStateMachine.this.removeServiceRequest(message.replyTo, (WifiP2pServiceRequest) message.obj);
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.REMOVE_SERVICE_REQUEST_SUCCEEDED);
                        return true;
                    case WifiP2pManager.CLEAR_SERVICE_REQUESTS /* 139307 */:
                        P2pStateMachine.this.clearServiceRequests(message.replyTo);
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CLEAR_SERVICE_REQUESTS_SUCCEEDED);
                        return true;
                    case WifiP2pManager.DISCOVER_SERVICES /* 139310 */:
                        if (WifiP2pService.this.mDiscoveryBlocked) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.DISCOVER_SERVICES_FAILED, 2);
                        } else if (!P2pStateMachine.this.updateSupplicantServiceRequest()) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.DISCOVER_SERVICES_FAILED, 3);
                        } else if (P2pStateMachine.this.mWifiNative.p2pFind(120)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.DISCOVER_SERVICES_SUCCEEDED);
                        } else {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.DISCOVER_SERVICES_FAILED, 0);
                        }
                        return true;
                    case WifiP2pManager.SET_DEVICE_NAME /* 139315 */:
                        WifiP2pDevice wifiP2pDevice = (WifiP2pDevice) message.obj;
                        if (wifiP2pDevice == null || !P2pStateMachine.this.setAndPersistDeviceName(wifiP2pDevice.deviceName)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.SET_DEVICE_NAME_FAILED, 0);
                        } else {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.SET_DEVICE_NAME_SUCCEEDED);
                        }
                        return true;
                    case WifiP2pManager.DELETE_PERSISTENT_GROUP /* 139318 */:
                        P2pStateMachine.this.mGroups.remove(message.arg1);
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.DELETE_PERSISTENT_GROUP_SUCCEEDED);
                        return true;
                    case WifiP2pManager.SET_WFD_INFO /* 139323 */:
                        WifiP2pWfdInfo wifiP2pWfdInfo = (WifiP2pWfdInfo) message.obj;
                        if (wifiP2pWfdInfo == null || !P2pStateMachine.this.setWfdInfo(wifiP2pWfdInfo)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.SET_WFD_INFO_FAILED, 0);
                        } else {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.SET_WFD_INFO_SUCCEEDED);
                        }
                        return true;
                    case WifiP2pManager.START_LISTEN /* 139329 */:
                        P2pStateMachine.this.mWifiNative.p2pFlush();
                        if (P2pStateMachine.this.mWifiNative.p2pExtListen(true, 500, 500)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.START_LISTEN_SUCCEEDED);
                        } else {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.START_LISTEN_FAILED);
                        }
                        return true;
                    case WifiP2pManager.STOP_LISTEN /* 139332 */:
                        if (P2pStateMachine.this.mWifiNative.p2pExtListen(false, 0, 0)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.STOP_LISTEN_SUCCEEDED);
                        } else {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.STOP_LISTEN_FAILED);
                        }
                        P2pStateMachine.this.mWifiNative.p2pFlush();
                        return true;
                    case WifiP2pManager.SET_CHANNEL /* 139335 */:
                        Bundle bundle = (Bundle) message.obj;
                        if (P2pStateMachine.this.mWifiNative.p2pSetChannel(bundle.getInt("lc", 0), bundle.getInt("oc", 0))) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.SET_CHANNEL_SUCCEEDED);
                        } else {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.SET_CHANNEL_FAILED);
                        }
                        return true;
                    case WifiP2pService.SET_MIRACAST_MODE /* 143374 */:
                        P2pStateMachine.this.mWifiNative.setMiracastMode(message.arg1);
                        return true;
                    case WifiP2pService.BLOCK_DISCOVERY /* 143375 */:
                        boolean z = message.arg1 == 1;
                        if (WifiP2pService.this.mDiscoveryBlocked != z) {
                            WifiP2pService.this.mDiscoveryBlocked = z;
                            if (z && WifiP2pService.this.mDiscoveryStarted) {
                                P2pStateMachine.this.mWifiNative.p2pStopFind();
                                WifiP2pService.this.mDiscoveryPostponed = true;
                            }
                            if (!z && WifiP2pService.this.mDiscoveryPostponed) {
                                WifiP2pService.this.mDiscoveryPostponed = false;
                                P2pStateMachine.this.mWifiNative.p2pFind(120);
                            }
                            if (z) {
                                try {
                                    ((StateMachine) message.obj).sendMessage(message.arg2);
                                } catch (Exception e) {
                                    P2pStateMachine.this.loge("unable to send BLOCK_DISCOVERY response: " + e);
                                }
                            }
                            break;
                        }
                        return true;
                    case WifiP2pService.SET_COUNTRY_CODE /* 143376 */:
                        String upperCase = ((String) message.obj).toUpperCase(Locale.ROOT);
                        if ((WifiP2pService.this.mLastSetCountryCode == null || !upperCase.equals(WifiP2pService.this.mLastSetCountryCode)) && P2pStateMachine.this.mWifiNative.setCountryCode(upperCase)) {
                            WifiP2pService.this.mLastSetCountryCode = upperCase;
                        }
                        return true;
                    case WifiMonitor.SUP_DISCONNECTION_EVENT /* 147458 */:
                        P2pStateMachine.this.loge("Unexpected loss of p2p socket connection");
                        P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                        p2pStateMachine2.transitionTo(p2pStateMachine2.mP2pDisabledState);
                        return true;
                    case WifiMonitor.P2P_DEVICE_FOUND_EVENT /* 147477 */:
                        WifiP2pDevice wifiP2pDevice2 = (WifiP2pDevice) message.obj;
                        if (!WifiP2pService.this.mThisDevice.deviceAddress.equals(wifiP2pDevice2.deviceAddress)) {
                            P2pStateMachine.this.mPeers.updateSupplicantDetails(wifiP2pDevice2);
                            P2pStateMachine.this.sendPeersChangedBroadcast();
                        }
                        return true;
                    case WifiMonitor.P2P_DEVICE_LOST_EVENT /* 147478 */:
                        if (P2pStateMachine.this.mPeers.remove(((WifiP2pDevice) message.obj).deviceAddress) != null) {
                            P2pStateMachine.this.sendPeersChangedBroadcast();
                        }
                        return true;
                    case WifiMonitor.P2P_FIND_STOPPED_EVENT /* 147493 */:
                        P2pStateMachine.this.sendP2pDiscoveryChangedBroadcast(false);
                        return true;
                    case WifiMonitor.P2P_SERV_DISC_RESP_EVENT /* 147494 */:
                        for (WifiP2pServiceResponse wifiP2pServiceResponse : (List) message.obj) {
                            wifiP2pServiceResponse.setSrcDevice(P2pStateMachine.this.mPeers.get(wifiP2pServiceResponse.getSrcDevice().deviceAddress));
                            P2pStateMachine.this.sendServiceResponse(wifiP2pServiceResponse);
                        }
                        return true;
                    default:
                        return false;
                }
            }

            public void exit() {
                P2pStateMachine.this.sendP2pStateChangedBroadcast(false);
                WifiP2pService.this.mNetworkInfo.setIsAvailable(false);
                WifiP2pService.this.mLastSetCountryCode = null;
            }
        }

        class InactiveState extends State {
            InactiveState() {
            }

            public void enter() {
                P2pStateMachine.this.mSavedPeerConfig.invalidate();
            }

            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            public boolean processMessage(Message message) {
                boolean zP2pGroupAdd;
                switch (message.what) {
                    case WifiP2pManager.STOP_DISCOVERY /* 139268 */:
                        if (P2pStateMachine.this.mWifiNative.p2pStopFind()) {
                            P2pStateMachine.this.mWifiNative.p2pFlush();
                            WifiP2pService.this.mServiceDiscReqId = null;
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.STOP_DISCOVERY_SUCCEEDED);
                        } else {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.STOP_DISCOVERY_FAILED, 0);
                        }
                        return true;
                    case WifiP2pManager.CONNECT /* 139271 */:
                        WifiP2pConfig wifiP2pConfig = (WifiP2pConfig) message.obj;
                        if (!P2pStateMachine.this.isConfigInvalid(wifiP2pConfig)) {
                            WifiP2pService.this.mAutonomousGroup = false;
                            P2pStateMachine.this.mWifiNative.p2pStopFind();
                            if (P2pStateMachine.this.reinvokePersistentGroup(wifiP2pConfig)) {
                                P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                                p2pStateMachine.transitionTo(p2pStateMachine.mGroupNegotiationState);
                            } else {
                                P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                                p2pStateMachine2.transitionTo(p2pStateMachine2.mProvisionDiscoveryState);
                            }
                            P2pStateMachine.this.mSavedPeerConfig = wifiP2pConfig;
                            P2pStateMachine.this.mPeers.updateStatus(P2pStateMachine.this.mSavedPeerConfig.deviceAddress, 1);
                            P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                            p2pStateMachine3.mWifiP2pDevice = p2pStateMachine3.getWifiP2pDeviceFromPeers(p2pStateMachine3.mSavedPeerConfig.deviceAddress);
                            P2pStateMachine.this.sendPeersChangedBroadcast();
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CONNECT_SUCCEEDED);
                        } else {
                            P2pStateMachine.this.loge("Dropping connect requeset " + wifiP2pConfig);
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CONNECT_FAILED);
                        }
                        return true;
                    case WifiP2pManager.CREATE_GROUP /* 139277 */:
                        WifiP2pService.this.mAutonomousGroup = true;
                        if (message.arg1 == -2) {
                            int networkId = P2pStateMachine.this.mGroups.getNetworkId(WifiP2pService.this.mThisDevice.deviceAddress);
                            zP2pGroupAdd = networkId != -1 ? P2pStateMachine.this.mWifiNative.p2pGroupAdd(networkId) : P2pStateMachine.this.mWifiNative.p2pGroupAdd(true);
                        } else {
                            zP2pGroupAdd = P2pStateMachine.this.mWifiNative.p2pGroupAdd(false);
                        }
                        if (zP2pGroupAdd) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CREATE_GROUP_SUCCEEDED);
                            P2pStateMachine p2pStateMachine4 = P2pStateMachine.this;
                            p2pStateMachine4.transitionTo(p2pStateMachine4.mGroupNegotiationState);
                        } else {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CREATE_GROUP_FAILED, 0);
                        }
                        return true;
                    case WifiP2pManager.START_LISTEN /* 139329 */:
                        P2pStateMachine.this.mWifiNative.p2pFlush();
                        if (P2pStateMachine.this.mWifiNative.p2pExtListen(true, 500, 500)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.START_LISTEN_SUCCEEDED);
                        } else {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.START_LISTEN_FAILED);
                        }
                        return true;
                    case WifiP2pManager.STOP_LISTEN /* 139332 */:
                        if (P2pStateMachine.this.mWifiNative.p2pExtListen(false, 0, 0)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.STOP_LISTEN_SUCCEEDED);
                        } else {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.STOP_LISTEN_FAILED);
                        }
                        P2pStateMachine.this.mWifiNative.p2pFlush();
                        return true;
                    case WifiP2pManager.SET_CHANNEL /* 139335 */:
                        Bundle bundle = (Bundle) message.obj;
                        if (P2pStateMachine.this.mWifiNative.p2pSetChannel(bundle.getInt("lc", 0), bundle.getInt("oc", 0))) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.SET_CHANNEL_SUCCEEDED);
                        } else {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.SET_CHANNEL_FAILED);
                        }
                        return true;
                    case WifiMonitor.P2P_GO_NEGOTIATION_REQUEST_EVENT /* 147479 */:
                        WifiP2pConfig wifiP2pConfig2 = (WifiP2pConfig) message.obj;
                        if (!P2pStateMachine.this.isConfigInvalid(wifiP2pConfig2)) {
                            P2pStateMachine.this.mSavedPeerConfig = wifiP2pConfig2;
                            WifiP2pService.this.mAutonomousGroup = false;
                            WifiP2pService.this.mJoinExistingGroup = false;
                            P2pStateMachine p2pStateMachine5 = P2pStateMachine.this;
                            p2pStateMachine5.transitionTo(p2pStateMachine5.mUserAuthorizingNegotiationRequestState);
                        } else {
                            P2pStateMachine.this.loge("Dropping GO neg request " + wifiP2pConfig2);
                        }
                        return true;
                    case WifiMonitor.P2P_GROUP_STARTED_EVENT /* 147485 */:
                        P2pStateMachine.this.mGroup = (WifiP2pGroup) message.obj;
                        if (P2pStateMachine.this.mGroup.getNetworkId() == -2) {
                            WifiP2pService.this.mAutonomousGroup = false;
                            P2pStateMachine.this.deferMessage(message);
                            P2pStateMachine p2pStateMachine6 = P2pStateMachine.this;
                            p2pStateMachine6.transitionTo(p2pStateMachine6.mGroupNegotiationState);
                        } else {
                            P2pStateMachine.this.loge("Unexpected group creation, remove " + P2pStateMachine.this.mGroup);
                            P2pStateMachine.this.mWifiNative.p2pGroupRemove(P2pStateMachine.this.mGroup.getInterface());
                        }
                        return true;
                    case WifiMonitor.P2P_INVITATION_RECEIVED_EVENT /* 147487 */:
                        WifiP2pGroup wifiP2pGroup = (WifiP2pGroup) message.obj;
                        WifiP2pDevice owner = wifiP2pGroup.getOwner();
                        if (owner == null) {
                            P2pStateMachine.this.loge("Ignored invitation from null owner");
                        } else {
                            WifiP2pConfig wifiP2pConfig3 = new WifiP2pConfig();
                            wifiP2pConfig3.deviceAddress = wifiP2pGroup.getOwner().deviceAddress;
                            if (!P2pStateMachine.this.isConfigInvalid(wifiP2pConfig3)) {
                                P2pStateMachine.this.mSavedPeerConfig = wifiP2pConfig3;
                                WifiP2pDevice wifiP2pDevice = P2pStateMachine.this.mPeers.get(owner.deviceAddress);
                                if (wifiP2pDevice != null) {
                                    if (wifiP2pDevice.wpsPbcSupported()) {
                                        P2pStateMachine.this.mSavedPeerConfig.wps.setup = 0;
                                    } else if (wifiP2pDevice.wpsKeypadSupported()) {
                                        P2pStateMachine.this.mSavedPeerConfig.wps.setup = 2;
                                    } else if (wifiP2pDevice.wpsDisplaySupported()) {
                                        P2pStateMachine.this.mSavedPeerConfig.wps.setup = 1;
                                    }
                                }
                                WifiP2pService.this.mAutonomousGroup = false;
                                WifiP2pService.this.mJoinExistingGroup = true;
                                P2pStateMachine p2pStateMachine7 = P2pStateMachine.this;
                                p2pStateMachine7.transitionTo(p2pStateMachine7.mUserAuthorizingInviteRequestState);
                            } else {
                                P2pStateMachine.this.loge("Dropping invitation request " + wifiP2pConfig3);
                            }
                        }
                        return true;
                    case WifiMonitor.P2P_PROV_DISC_PBC_REQ_EVENT /* 147489 */:
                    case WifiMonitor.P2P_PROV_DISC_ENTER_PIN_EVENT /* 147491 */:
                    case WifiMonitor.P2P_PROV_DISC_SHOW_PIN_EVENT /* 147492 */:
                        return true;
                    default:
                        return false;
                }
            }
        }

        class GroupCreatingState extends State {
            GroupCreatingState() {
            }

            public void enter() {
                P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                p2pStateMachine.sendMessageDelayed(p2pStateMachine.obtainMessage(WifiP2pService.GROUP_CREATING_TIMED_OUT, WifiP2pService.access$1704(), 0), 120000L);
            }

            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiP2pManager.DISCOVER_PEERS /* 139265 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.DISCOVER_PEERS_FAILED, 2);
                        return true;
                    case WifiP2pManager.CANCEL_CONNECT /* 139274 */:
                        P2pStateMachine.this.mWifiNative.p2pCancelConnect();
                        P2pStateMachine.this.handleGroupCreationFailure();
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        p2pStateMachine.transitionTo(p2pStateMachine.mInactiveState);
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CANCEL_CONNECT_SUCCEEDED);
                        return true;
                    case WifiP2pService.GROUP_CREATING_TIMED_OUT /* 143361 */:
                        if (WifiP2pService.mGroupCreatingTimeoutIndex == message.arg1) {
                            P2pStateMachine.this.handleGroupCreationFailure();
                            P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                            p2pStateMachine2.transitionTo(p2pStateMachine2.mInactiveState);
                        }
                        return true;
                    case WifiMonitor.P2P_DEVICE_LOST_EVENT /* 147478 */:
                        WifiP2pDevice wifiP2pDevice = (WifiP2pDevice) message.obj;
                        if (!P2pStateMachine.this.mSavedPeerConfig.deviceAddress.equals(wifiP2pDevice.deviceAddress)) {
                            return false;
                        }
                        P2pStateMachine.this.mPeersLostDuringConnection.updateSupplicantDetails(wifiP2pDevice);
                        return true;
                    default:
                        return false;
                }
            }
        }

        class UserAuthorizingNegotiationRequestState extends State {
            public void exit() {
            }

            UserAuthorizingNegotiationRequestState() {
            }

            public void enter() {
                P2pStateMachine.this.notifyInvitationReceived();
            }

            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiP2pService.PEER_CONNECTION_USER_ACCEPT /* 143362 */:
                        P2pStateMachine.this.mWifiNative.p2pStopFind();
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        p2pStateMachine.p2pConnectWithPinDisplay(p2pStateMachine.mSavedPeerConfig);
                        P2pStateMachine.this.mPeers.updateStatus(P2pStateMachine.this.mSavedPeerConfig.deviceAddress, 1);
                        P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                        p2pStateMachine2.mWifiP2pDevice = p2pStateMachine2.getWifiP2pDeviceFromPeers(p2pStateMachine2.mSavedPeerConfig.deviceAddress);
                        P2pStateMachine.this.sendPeersChangedBroadcast();
                        P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                        p2pStateMachine3.transitionTo(p2pStateMachine3.mGroupNegotiationState);
                        return true;
                    case WifiP2pService.PEER_CONNECTION_USER_REJECT /* 143363 */:
                        P2pStateMachine p2pStateMachine4 = P2pStateMachine.this;
                        p2pStateMachine4.transitionTo(p2pStateMachine4.mInactiveState);
                        return true;
                    default:
                        return false;
                }
            }
        }

        class UserAuthorizingInviteRequestState extends State {
            public void exit() {
            }

            UserAuthorizingInviteRequestState() {
            }

            public void enter() {
                P2pStateMachine.this.notifyInvitationReceived();
            }

            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiP2pService.PEER_CONNECTION_USER_ACCEPT /* 143362 */:
                        P2pStateMachine.this.mWifiNative.p2pStopFind();
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        if (!p2pStateMachine.reinvokePersistentGroup(p2pStateMachine.mSavedPeerConfig)) {
                            P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                            p2pStateMachine2.p2pConnectWithPinDisplay(p2pStateMachine2.mSavedPeerConfig);
                        }
                        P2pStateMachine.this.mPeers.updateStatus(P2pStateMachine.this.mSavedPeerConfig.deviceAddress, 1);
                        P2pStateMachine.this.sendPeersChangedBroadcast();
                        P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                        p2pStateMachine3.transitionTo(p2pStateMachine3.mGroupNegotiationState);
                        return true;
                    case WifiP2pService.PEER_CONNECTION_USER_REJECT /* 143363 */:
                        P2pStateMachine p2pStateMachine4 = P2pStateMachine.this;
                        p2pStateMachine4.transitionTo(p2pStateMachine4.mInactiveState);
                        return true;
                    default:
                        return false;
                }
            }
        }

        class ProvisionDiscoveryState extends State {
            ProvisionDiscoveryState() {
            }

            public void enter() {
                P2pStateMachine.this.mWifiNative.p2pProvisionDiscovery(P2pStateMachine.this.mSavedPeerConfig);
            }

            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiMonitor.P2P_PROV_DISC_PBC_RSP_EVENT /* 147490 */:
                        if (((WifiP2pProvDiscEvent) message.obj).device.deviceAddress.equals(P2pStateMachine.this.mSavedPeerConfig.deviceAddress) && P2pStateMachine.this.mSavedPeerConfig.wps.setup == 0) {
                            P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                            p2pStateMachine.p2pConnectWithPinDisplay(p2pStateMachine.mSavedPeerConfig);
                            P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                            p2pStateMachine2.transitionTo(p2pStateMachine2.mGroupNegotiationState);
                        }
                        return true;
                    case WifiMonitor.P2P_PROV_DISC_ENTER_PIN_EVENT /* 147491 */:
                        if (((WifiP2pProvDiscEvent) message.obj).device.deviceAddress.equals(P2pStateMachine.this.mSavedPeerConfig.deviceAddress) && P2pStateMachine.this.mSavedPeerConfig.wps.setup == 2) {
                            if (TextUtils.isEmpty(P2pStateMachine.this.mSavedPeerConfig.wps.pin)) {
                                WifiP2pService.this.mJoinExistingGroup = false;
                                P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                                p2pStateMachine3.transitionTo(p2pStateMachine3.mUserAuthorizingNegotiationRequestState);
                            } else {
                                P2pStateMachine p2pStateMachine4 = P2pStateMachine.this;
                                p2pStateMachine4.p2pConnectWithPinDisplay(p2pStateMachine4.mSavedPeerConfig);
                                P2pStateMachine p2pStateMachine5 = P2pStateMachine.this;
                                p2pStateMachine5.transitionTo(p2pStateMachine5.mGroupNegotiationState);
                            }
                        }
                        return true;
                    case WifiMonitor.P2P_PROV_DISC_SHOW_PIN_EVENT /* 147492 */:
                        WifiP2pProvDiscEvent wifiP2pProvDiscEvent = (WifiP2pProvDiscEvent) message.obj;
                        WifiP2pDevice wifiP2pDevice = wifiP2pProvDiscEvent.device;
                        if (wifiP2pDevice.deviceAddress.equals(P2pStateMachine.this.mSavedPeerConfig.deviceAddress) && P2pStateMachine.this.mSavedPeerConfig.wps.setup == 1) {
                            P2pStateMachine.this.mSavedPeerConfig.wps.pin = wifiP2pProvDiscEvent.pin;
                            P2pStateMachine p2pStateMachine6 = P2pStateMachine.this;
                            p2pStateMachine6.p2pConnectWithPinDisplay(p2pStateMachine6.mSavedPeerConfig);
                            P2pStateMachine.this.notifyInvitationSent(wifiP2pProvDiscEvent.pin, wifiP2pDevice.deviceAddress);
                            P2pStateMachine p2pStateMachine7 = P2pStateMachine.this;
                            p2pStateMachine7.transitionTo(p2pStateMachine7.mGroupNegotiationState);
                        }
                        return true;
                    case WifiMonitor.P2P_FIND_STOPPED_EVENT /* 147493 */:
                    case WifiMonitor.P2P_SERV_DISC_RESP_EVENT /* 147494 */:
                    default:
                        return false;
                    case WifiMonitor.P2P_PROV_DISC_FAILURE_EVENT /* 147495 */:
                        P2pStateMachine.this.loge("provision discovery failed");
                        P2pStateMachine.this.handleGroupCreationFailure();
                        P2pStateMachine p2pStateMachine8 = P2pStateMachine.this;
                        p2pStateMachine8.transitionTo(p2pStateMachine8.mInactiveState);
                        return true;
                }
            }
        }

        class GroupNegotiationState extends State {
            public void enter() {
            }

            GroupNegotiationState() {
            }

            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            /* JADX WARN: Removed duplicated region for block: B:41:0x01e0  */
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public boolean processMessage(android.os.Message r6) {
                /*
                    Method dump skipped, instruction units count: 516
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.p2p.WifiP2pService.P2pStateMachine.GroupNegotiationState.processMessage(android.os.Message):boolean");
            }
        }

        class FrequencyConflictState extends State {
            private AlertDialog mFrequencyConflictDialog;

            FrequencyConflictState() {
            }

            public void enter() {
                notifyFrequencyConflict();
            }

            private void notifyFrequencyConflict() {
                P2pStateMachine.this.logd("Notify frequency conflict");
                Resources system = Resources.getSystem();
                AlertDialog.Builder builder = new AlertDialog.Builder(WifiP2pService.this.mContext);
                P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                AlertDialog alertDialogCreate = builder.setMessage(system.getString(17040453, p2pStateMachine.getDeviceName(p2pStateMachine.mSavedPeerConfig.deviceAddress))).setPositiveButton(system.getString(17040498), new DialogInterface.OnClickListener() { // from class: android.net.wifi.p2p.WifiP2pService.P2pStateMachine.FrequencyConflictState.3
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        P2pStateMachine.this.sendMessage(WifiP2pService.DROP_WIFI_USER_ACCEPT);
                    }
                }).setNegativeButton(system.getString(17040446), new DialogInterface.OnClickListener() { // from class: android.net.wifi.p2p.WifiP2pService.P2pStateMachine.FrequencyConflictState.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        P2pStateMachine.this.sendMessage(WifiP2pService.DROP_WIFI_USER_REJECT);
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: android.net.wifi.p2p.WifiP2pService.P2pStateMachine.FrequencyConflictState.1
                    @Override // android.content.DialogInterface.OnCancelListener
                    public void onCancel(DialogInterface dialogInterface) {
                        P2pStateMachine.this.sendMessage(WifiP2pService.DROP_WIFI_USER_REJECT);
                    }
                }).create();
                alertDialogCreate.getWindow().setType(2003);
                alertDialogCreate.show();
                this.mFrequencyConflictDialog = alertDialogCreate;
            }

            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            public boolean processMessage(Message message) {
                int i = message.what;
                if (i != 143373) {
                    switch (i) {
                        case WifiP2pService.DROP_WIFI_USER_ACCEPT /* 143364 */:
                            WifiP2pService.this.mWifiChannel.sendMessage(WifiP2pService.DISCONNECT_WIFI_REQUEST, 1);
                            WifiP2pService.this.mTempoarilyDisconnectedWifi = true;
                            break;
                        case WifiP2pService.DROP_WIFI_USER_REJECT /* 143365 */:
                            P2pStateMachine.this.handleGroupCreationFailure();
                            P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                            p2pStateMachine.transitionTo(p2pStateMachine.mInactiveState);
                            break;
                        default:
                            switch (i) {
                                case WifiMonitor.P2P_GO_NEGOTIATION_SUCCESS_EVENT /* 147481 */:
                                case WifiMonitor.P2P_GROUP_FORMATION_SUCCESS_EVENT /* 147483 */:
                                    P2pStateMachine.this.loge(getName() + "group sucess during freq conflict!");
                                    break;
                                case WifiMonitor.P2P_GO_NEGOTIATION_FAILURE_EVENT /* 147482 */:
                                case WifiMonitor.P2P_GROUP_FORMATION_FAILURE_EVENT /* 147484 */:
                                case WifiMonitor.P2P_GROUP_REMOVED_EVENT /* 147486 */:
                                    break;
                                case WifiMonitor.P2P_GROUP_STARTED_EVENT /* 147485 */:
                                    P2pStateMachine.this.loge(getName() + "group started after freq conflict, handle anyway");
                                    P2pStateMachine.this.deferMessage(message);
                                    P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                                    p2pStateMachine2.transitionTo(p2pStateMachine2.mGroupNegotiationState);
                                    break;
                                default:
                                    return false;
                            }
                            break;
                    }
                } else {
                    P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                    p2pStateMachine3.transitionTo(p2pStateMachine3.mInactiveState);
                    P2pStateMachine p2pStateMachine4 = P2pStateMachine.this;
                    p2pStateMachine4.sendMessage(WifiP2pManager.CONNECT, p2pStateMachine4.mSavedPeerConfig);
                }
                return true;
            }

            public void exit() {
                AlertDialog alertDialog = this.mFrequencyConflictDialog;
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
            }
        }

        class GroupCreatedState extends State {
            GroupCreatedState() {
            }

            public void enter() {
                P2pStateMachine.this.mSavedPeerConfig.invalidate();
                WifiP2pService.this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.CONNECTED, null, null);
                P2pStateMachine.this.updateThisDevice(0);
                if (P2pStateMachine.this.mGroup.isGroupOwner()) {
                    P2pStateMachine.this.setWifiP2pInfoOnGroupFormation(NetworkUtils.numericToInetAddress(WifiP2pService.SERVER_ADDRESS));
                }
                if (WifiP2pService.this.mAutonomousGroup) {
                    P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
                }
            }

            public boolean processMessage(Message message) {
                boolean zStartWpsPinKeypad;
                int networkId;
                boolean z = false;
                switch (message.what) {
                    case WifiStateMachine.CMD_DISABLE_P2P_REQ /* 131204 */:
                        P2pStateMachine.this.sendMessage(WifiP2pManager.REMOVE_GROUP);
                        P2pStateMachine.this.deferMessage(message);
                        return true;
                    case WifiP2pManager.CONNECT /* 139271 */:
                        WifiP2pConfig wifiP2pConfig = (WifiP2pConfig) message.obj;
                        if (P2pStateMachine.this.isConfigInvalid(wifiP2pConfig)) {
                            P2pStateMachine.this.loge("Dropping connect requeset " + wifiP2pConfig);
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CONNECT_FAILED);
                        } else {
                            P2pStateMachine.this.logd("Inviting device : " + wifiP2pConfig.deviceAddress);
                            P2pStateMachine.this.mSavedPeerConfig = wifiP2pConfig;
                            if (P2pStateMachine.this.mWifiNative.p2pInvite(P2pStateMachine.this.mGroup, wifiP2pConfig.deviceAddress)) {
                                P2pStateMachine.this.mPeers.updateStatus(wifiP2pConfig.deviceAddress, 1);
                                P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                                p2pStateMachine.mWifiP2pDevice = p2pStateMachine.getWifiP2pDeviceFromPeers(wifiP2pConfig.deviceAddress);
                                P2pStateMachine.this.sendPeersChangedBroadcast();
                                P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CONNECT_SUCCEEDED);
                            } else {
                                P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CONNECT_FAILED, 0);
                                P2pStateMachine.this.mWifiP2pDevice = null;
                            }
                        }
                        return true;
                    case WifiP2pManager.REMOVE_GROUP /* 139280 */:
                    case WifiMonitor.NETWORK_DISCONNECTION_EVENT /* 147460 */:
                        if (!P2pStateMachine.this.mWifiNative.p2pGroupRemove(P2pStateMachine.this.mGroup.getInterface())) {
                            P2pStateMachine.this.handleGroupRemoved();
                            P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                            p2pStateMachine2.transitionTo(p2pStateMachine2.mInactiveState);
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.REMOVE_GROUP_FAILED, 0);
                        } else {
                            P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                            p2pStateMachine3.transitionTo(p2pStateMachine3.mOngoingGroupRemovalState);
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.REMOVE_GROUP_SUCCEEDED);
                        }
                        return true;
                    case WifiP2pManager.START_WPS /* 139326 */:
                        WpsInfo wpsInfo = (WpsInfo) message.obj;
                        int i = WifiP2pManager.START_WPS_FAILED;
                        if (wpsInfo == null) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.START_WPS_FAILED);
                        } else {
                            if (wpsInfo.setup == 0) {
                                zStartWpsPinKeypad = P2pStateMachine.this.mWifiNative.startWpsPbc(P2pStateMachine.this.mGroup.getInterface(), null);
                            } else if (wpsInfo.pin == null) {
                                String strStartWpsPinDisplay = P2pStateMachine.this.mWifiNative.startWpsPinDisplay(P2pStateMachine.this.mGroup.getInterface());
                                try {
                                    Integer.parseInt(strStartWpsPinDisplay);
                                    P2pStateMachine.this.notifyInvitationSent(strStartWpsPinDisplay, "any");
                                    z = true;
                                } catch (NumberFormatException unused) {
                                }
                                zStartWpsPinKeypad = z;
                            } else {
                                zStartWpsPinKeypad = P2pStateMachine.this.mWifiNative.startWpsPinKeypad(P2pStateMachine.this.mGroup.getInterface(), wpsInfo.pin);
                            }
                            P2pStateMachine p2pStateMachine4 = P2pStateMachine.this;
                            if (zStartWpsPinKeypad) {
                                i = WifiP2pManager.START_WPS_SUCCEEDED;
                            }
                            p2pStateMachine4.replyToMessage(message, i);
                        }
                        return true;
                    case WifiMonitor.P2P_DEVICE_LOST_EVENT /* 147478 */:
                        WifiP2pDevice wifiP2pDevice = (WifiP2pDevice) message.obj;
                        if (!P2pStateMachine.this.mGroup.contains(wifiP2pDevice)) {
                            return false;
                        }
                        P2pStateMachine.this.mPeersLostDuringConnection.updateSupplicantDetails(wifiP2pDevice);
                        return true;
                    case WifiMonitor.P2P_GROUP_STARTED_EVENT /* 147485 */:
                        P2pStateMachine.this.loge("Duplicate group creation event notice, ignore");
                        return true;
                    case WifiMonitor.P2P_GROUP_REMOVED_EVENT /* 147486 */:
                        SystemProperties.set(WifiP2pService.WFD_P2P_ROLE, "");
                        SystemProperties.set(WifiP2pService.WFD_DNSMASQ_PEER, "");
                        if (SystemProperties.get("dhcp.p2p.gateway") != null) {
                            SystemProperties.set("dhcp.p2p.gateway", "");
                        } else {
                            P2pStateMachine.this.loge("gateway not set in the property yet.");
                        }
                        if (SystemProperties.get("dhcp.p2p.ipaddress") != null) {
                            SystemProperties.set("dhcp.p2p.ipaddress", "");
                        } else {
                            P2pStateMachine.this.loge("ipaddress not set in the property yet.");
                        }
                        P2pStateMachine.this.logd("clear interface ip " + WifiP2pService.this.mInterface);
                        try {
                            WifiP2pService.this.mNwService.clearInterfaceAddresses(WifiP2pService.this.mInterface);
                            WifiP2pService.this.mNwService.disableIpv6(P2pStateMachine.this.mGroup.getInterface());
                            break;
                        } catch (Exception e) {
                            P2pStateMachine.this.loge("Failed to clear addresses or disable ipv6" + e);
                        }
                        P2pStateMachine.this.mWifiP2pDevice = null;
                        P2pStateMachine.this.handleGroupRemoved();
                        P2pStateMachine p2pStateMachine5 = P2pStateMachine.this;
                        p2pStateMachine5.transitionTo(p2pStateMachine5.mInactiveState);
                        return true;
                    case WifiMonitor.P2P_INVITATION_RESULT_EVENT /* 147488 */:
                        P2pStatus p2pStatus = (P2pStatus) message.obj;
                        if (p2pStatus != P2pStatus.SUCCESS) {
                            P2pStateMachine.this.loge("Invitation result " + p2pStatus);
                            if (p2pStatus == P2pStatus.UNKNOWN_P2P_GROUP && (networkId = P2pStateMachine.this.mGroup.getNetworkId()) >= 0) {
                                P2pStateMachine p2pStateMachine6 = P2pStateMachine.this;
                                if (!p2pStateMachine6.removeClientFromList(networkId, p2pStateMachine6.mSavedPeerConfig.deviceAddress, false)) {
                                    P2pStateMachine.this.loge("Already removed the client, ignore");
                                } else {
                                    P2pStateMachine p2pStateMachine7 = P2pStateMachine.this;
                                    p2pStateMachine7.sendMessage(WifiP2pManager.CONNECT, p2pStateMachine7.mSavedPeerConfig);
                                }
                            }
                        }
                        return true;
                    case WifiMonitor.P2P_PROV_DISC_PBC_REQ_EVENT /* 147489 */:
                    case WifiMonitor.P2P_PROV_DISC_ENTER_PIN_EVENT /* 147491 */:
                    case WifiMonitor.P2P_PROV_DISC_SHOW_PIN_EVENT /* 147492 */:
                        WifiP2pProvDiscEvent wifiP2pProvDiscEvent = (WifiP2pProvDiscEvent) message.obj;
                        P2pStateMachine.this.mSavedPeerConfig = new WifiP2pConfig();
                        P2pStateMachine.this.mSavedPeerConfig.deviceAddress = wifiP2pProvDiscEvent.device.deviceAddress;
                        if (message.what == 147491) {
                            P2pStateMachine.this.mSavedPeerConfig.wps.setup = 2;
                        } else if (message.what == 147492) {
                            P2pStateMachine.this.mSavedPeerConfig.wps.setup = 1;
                            P2pStateMachine.this.mSavedPeerConfig.wps.pin = wifiP2pProvDiscEvent.pin;
                        } else {
                            P2pStateMachine.this.mSavedPeerConfig.wps.setup = 0;
                        }
                        P2pStateMachine p2pStateMachine8 = P2pStateMachine.this;
                        p2pStateMachine8.transitionTo(p2pStateMachine8.mUserAuthorizingJoinState);
                        return true;
                    case WifiMonitor.AP_STA_DISCONNECTED_EVENT /* 147497 */:
                        WifiP2pDevice wifiP2pDevice2 = (WifiP2pDevice) message.obj;
                        String str = wifiP2pDevice2.deviceAddress;
                        if (str != null) {
                            P2pStateMachine.this.mPeers.updateStatus(str, 3);
                            SystemProperties.set(WifiP2pService.WFD_P2P_ROLE, "");
                            SystemProperties.set(WifiP2pService.WFD_DNSMASQ_PEER, "");
                            P2pStateMachine.this.mWifiP2pDevice = null;
                            if (P2pStateMachine.this.mGroup.removeClient(str)) {
                                if (WifiP2pService.this.mAutonomousGroup || !P2pStateMachine.this.mGroup.isClientListEmpty()) {
                                    P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
                                } else {
                                    P2pStateMachine.this.logd("Client list empty, remove non-persistent p2p group");
                                    P2pStateMachine.this.mWifiNative.p2pGroupRemove(P2pStateMachine.this.mGroup.getInterface());
                                }
                            } else {
                                for (WifiP2pDevice wifiP2pDevice3 : P2pStateMachine.this.mGroup.getClientList()) {
                                }
                            }
                            P2pStateMachine.this.sendPeersChangedBroadcast();
                        } else {
                            P2pStateMachine.this.loge("Disconnect on unknown device: " + wifiP2pDevice2);
                        }
                        return true;
                    case WifiMonitor.AP_STA_CONNECTED_EVENT /* 147498 */:
                        String str2 = ((WifiP2pDevice) message.obj).deviceAddress;
                        P2pStateMachine.this.mWifiNative.setP2pGroupIdle(P2pStateMachine.this.mGroup.getInterface(), 0);
                        if (str2 != null) {
                            if (P2pStateMachine.this.mPeers.get(str2) != null) {
                                P2pStateMachine.this.mGroup.addClient(P2pStateMachine.this.mPeers.get(str2));
                            } else {
                                P2pStateMachine.this.mGroup.addClient(str2);
                            }
                            P2pStateMachine.this.mPeers.updateStatus(str2, 0);
                            P2pStateMachine p2pStateMachine9 = P2pStateMachine.this;
                            p2pStateMachine9.mWifiP2pDevice = p2pStateMachine9.getWifiP2pDeviceFromPeers(str2);
                            SystemProperties.set(WifiP2pService.WFD_P2P_ROLE, "1");
                            P2pStateMachine.this.sendPeersChangedBroadcast();
                        } else {
                            P2pStateMachine.this.loge("Connect on null device address, ignore");
                        }
                        P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
                        return true;
                    case DhcpStateMachine.CMD_POST_DHCP_ACTION /* 196613 */:
                        DhcpResults dhcpResults = (DhcpResults) message.obj;
                        if (message.arg1 == 1 && dhcpResults != null) {
                            P2pStateMachine.this.setWifiP2pInfoOnGroupFormation(dhcpResults.serverAddress);
                            SystemProperties.set(WifiP2pService.WFD_P2P_ROLE, "2");
                            String str3 = SystemProperties.get("dhcp.p2p.gateway");
                            if (str3 != null) {
                                P2pStateMachine.this.logd("gateway=[" + str3 + "]");
                            } else {
                                P2pStateMachine.this.loge("gateway not set in the property yet.");
                            }
                            String str4 = SystemProperties.get("dhcp.p2p.ipaddress");
                            if (str4 != null) {
                                P2pStateMachine.this.logd("ipaddress=[" + str4 + "]");
                            } else {
                                P2pStateMachine.this.loge("gateway not set in the property yet.");
                            }
                            P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
                            P2pStateMachine.this.mWifiNative.setP2pPowerSave(P2pStateMachine.this.mGroup.getInterface(), true);
                        } else {
                            P2pStateMachine.this.loge("DHCP failed");
                            P2pStateMachine.this.mWifiNative.p2pGroupRemove(P2pStateMachine.this.mGroup.getInterface());
                        }
                        return true;
                    default:
                        return false;
                }
            }

            public void exit() {
                P2pStateMachine.this.updateThisDevice(3);
                P2pStateMachine.this.resetWifiP2pInfo();
                WifiP2pService.this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.DISCONNECTED, null, null);
                P2pStateMachine.this.mWifiP2pDevice = null;
                P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
            }
        }

        class UserAuthorizingJoinState extends State {
            public void exit() {
            }

            UserAuthorizingJoinState() {
            }

            public void enter() {
                P2pStateMachine.this.notifyInvitationReceived();
            }

            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiP2pService.PEER_CONNECTION_USER_ACCEPT /* 143362 */:
                        P2pStateMachine.this.mWifiNative.p2pStopFind();
                        if (P2pStateMachine.this.mSavedPeerConfig.wps.setup == 0) {
                            P2pStateMachine.this.mWifiNative.startWpsPbc(P2pStateMachine.this.mGroup.getInterface(), null);
                        } else {
                            P2pStateMachine.this.mWifiNative.startWpsPinKeypad(P2pStateMachine.this.mGroup.getInterface(), P2pStateMachine.this.mSavedPeerConfig.wps.pin);
                        }
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        p2pStateMachine.transitionTo(p2pStateMachine.mGroupCreatedState);
                        return true;
                    case WifiP2pService.PEER_CONNECTION_USER_REJECT /* 143363 */:
                        P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                        p2pStateMachine2.transitionTo(p2pStateMachine2.mGroupCreatedState);
                        return true;
                    case WifiMonitor.P2P_PROV_DISC_PBC_REQ_EVENT /* 147489 */:
                    case WifiMonitor.P2P_PROV_DISC_ENTER_PIN_EVENT /* 147491 */:
                    case WifiMonitor.P2P_PROV_DISC_SHOW_PIN_EVENT /* 147492 */:
                        return true;
                    default:
                        return false;
                }
            }
        }

        class OngoingGroupRemovalState extends State {
            public void enter() {
            }

            OngoingGroupRemovalState() {
            }

            public boolean processMessage(Message message) {
                if (message.what != 139280) {
                    return false;
                }
                P2pStateMachine.this.replyToMessage(message, WifiP2pManager.REMOVE_GROUP_SUCCEEDED);
                return true;
            }
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            super.dump(fileDescriptor, printWriter, strArr);
            printWriter.println("mWifiP2pInfo " + this.mWifiP2pInfo);
            printWriter.println("mGroup " + this.mGroup);
            printWriter.println("mSavedPeerConfig " + this.mSavedPeerConfig);
            printWriter.println("mSavedP2pGroup " + this.mSavedP2pGroup);
            printWriter.println();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendP2pStateChangedBroadcast(boolean z) {
            Intent intent = new Intent(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            intent.addFlags(67108864);
            if (z) {
                intent.putExtra(WifiP2pManager.EXTRA_WIFI_STATE, 2);
            } else {
                intent.putExtra(WifiP2pManager.EXTRA_WIFI_STATE, 1);
            }
            WifiP2pService.this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendP2pDiscoveryChangedBroadcast(boolean z) {
            if (WifiP2pService.this.mDiscoveryStarted == z) {
                return;
            }
            WifiP2pService.this.mDiscoveryStarted = z;
            Intent intent = new Intent(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
            intent.addFlags(67108864);
            intent.putExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, z ? 2 : 1);
            WifiP2pService.this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }

        private void sendThisDeviceChangedBroadcast() {
            Intent intent = new Intent(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
            intent.addFlags(67108864);
            intent.putExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE, new WifiP2pDevice(WifiP2pService.this.mThisDevice));
            WifiP2pService.this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendPeersChangedBroadcast() {
            Intent intent = new Intent(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            intent.putExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST, new WifiP2pDeviceList(this.mPeers));
            intent.addFlags(67108864);
            WifiP2pService.this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendP2pConnectionChangedBroadcast() {
            Intent intent = new Intent(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            intent.addFlags(603979776);
            intent.putExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO, new WifiP2pInfo(this.mWifiP2pInfo));
            intent.putExtra("networkInfo", new NetworkInfo(WifiP2pService.this.mNetworkInfo));
            intent.putExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP, new WifiP2pGroup(this.mGroup));
            if (this.mWifiP2pDevice != null) {
                intent.putExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE, new WifiP2pDevice(this.mWifiP2pDevice));
            } else {
                intent.putExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE, new WifiP2pDevice());
            }
            WifiP2pService.this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
            WifiP2pService.this.mWifiChannel.sendMessage(WifiP2pService.P2P_CONNECTION_CHANGED, new NetworkInfo(WifiP2pService.this.mNetworkInfo));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendP2pPersistentGroupsChangedBroadcast() {
            Intent intent = new Intent(WifiP2pManager.WIFI_P2P_PERSISTENT_GROUPS_CHANGED_ACTION);
            intent.addFlags(67108864);
            WifiP2pService.this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startDhcpServer(String str) {
            try {
                InterfaceConfiguration interfaceConfig = WifiP2pService.this.mNwService.getInterfaceConfig(str);
                interfaceConfig.setLinkAddress(new LinkAddress(NetworkUtils.numericToInetAddress(WifiP2pService.SERVER_ADDRESS), 24));
                interfaceConfig.setInterfaceUp();
                WifiP2pService.this.mNwService.setInterfaceConfig(str, interfaceConfig);
                WifiP2pService.this.mNwService.startTethering(WifiP2pService.DHCP_RANGE);
                logd("Started Dhcp server on " + str);
            } catch (Exception e) {
                loge("Error configuring interface " + str + ", :" + e);
            }
        }

        private void stopDhcpServer(String str) {
            try {
                WifiP2pService.this.mNwService.stopTethering();
                logd("Stopped Dhcp server");
            } catch (Exception e) {
                loge("Error stopping Dhcp server" + e);
            }
        }

        private void notifyP2pEnableFailure() {
            Resources system = Resources.getSystem();
            AlertDialog alertDialogCreate = new AlertDialog.Builder(WifiP2pService.this.mContext).setTitle(system.getString(17040440)).setMessage(system.getString(17040442)).setPositiveButton(system.getString(R.string.ok), (DialogInterface.OnClickListener) null).create();
            alertDialogCreate.getWindow().setType(2003);
            alertDialogCreate.show();
        }

        private void addRowToDialog(ViewGroup viewGroup, int i, String str) {
            Resources system = Resources.getSystem();
            View viewInflate = LayoutInflater.from(WifiP2pService.this.mContext).inflate(17367238, viewGroup, false);
            ((TextView) viewInflate.findViewById(16909186)).setText(system.getString(i));
            ((TextView) viewInflate.findViewById(16908990)).setText(str);
            viewGroup.addView(viewInflate);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void notifyInvitationSent(String str, String str2) {
            Resources system = Resources.getSystem();
            View viewInflate = LayoutInflater.from(WifiP2pService.this.mContext).inflate(17367237, (ViewGroup) null);
            ViewGroup viewGroup = (ViewGroup) viewInflate.findViewById(16909024);
            addRowToDialog(viewGroup, 17040450, getDeviceName(str2));
            addRowToDialog(viewGroup, 17040452, str);
            AlertDialog alertDialogCreate = new AlertDialog.Builder(WifiP2pService.this.mContext).setTitle(system.getString(17040447)).setView(viewInflate).setPositiveButton(system.getString(R.string.ok), (DialogInterface.OnClickListener) null).create();
            alertDialogCreate.getWindow().setType(2003);
            alertDialogCreate.show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void notifyInvitationReceived() {
            Resources.getSystem();
            WpsInfo wpsInfo = this.mSavedPeerConfig.wps;
            addRowToDialog((ViewGroup) LayoutInflater.from(WifiP2pService.this.mContext).inflate(17367237, (ViewGroup) null).findViewById(16909024), 17040449, getDeviceName(this.mSavedPeerConfig.deviceAddress));
            sendMessage(WifiP2pService.PEER_CONNECTION_USER_ACCEPT);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updatePersistentNetworks(boolean z) {
            String[] strArrSplit;
            String strListNetworks = this.mWifiNative.listNetworks();
            if (strListNetworks == null || (strArrSplit = strListNetworks.split("\n")) == null) {
                return;
            }
            if (z) {
                this.mGroups.clear();
            }
            boolean z2 = false;
            for (int i = 1; i < strArrSplit.length; i++) {
                String[] strArrSplit2 = strArrSplit[i].split("\t");
                if (strArrSplit2 != null && strArrSplit2.length >= 4) {
                    String str = strArrSplit2[1];
                    String str2 = strArrSplit2[2];
                    String str3 = strArrSplit2[3];
                    try {
                        int i2 = Integer.parseInt(strArrSplit2[0]);
                        if (str3.indexOf("[CURRENT]") == -1) {
                            if (str3.indexOf("[P2P-PERSISTENT]") == -1) {
                                this.mWifiNative.removeNetwork(i2);
                            } else if (!this.mGroups.contains(i2)) {
                                WifiP2pGroup wifiP2pGroup = new WifiP2pGroup();
                                wifiP2pGroup.setNetworkId(i2);
                                wifiP2pGroup.setNetworkName(str);
                                String networkVariable = this.mWifiNative.getNetworkVariable(i2, "mode");
                                if (networkVariable != null && networkVariable.equals("3")) {
                                    wifiP2pGroup.setIsGroupOwner(true);
                                }
                                if (str2.equalsIgnoreCase(WifiP2pService.this.mThisDevice.deviceAddress)) {
                                    wifiP2pGroup.setOwner(WifiP2pService.this.mThisDevice);
                                } else {
                                    WifiP2pDevice wifiP2pDevice = new WifiP2pDevice();
                                    wifiP2pDevice.deviceAddress = str2;
                                    wifiP2pGroup.setOwner(wifiP2pDevice);
                                }
                                this.mGroups.add(wifiP2pGroup);
                            }
                            z2 = true;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (z || z2) {
                this.mWifiNative.saveConfig();
                sendP2pPersistentGroupsChangedBroadcast();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isConfigInvalid(WifiP2pConfig wifiP2pConfig) {
            return wifiP2pConfig == null || TextUtils.isEmpty(wifiP2pConfig.deviceAddress) || this.mPeers.get(wifiP2pConfig.deviceAddress) == null;
        }

        private WifiP2pDevice fetchCurrentDeviceDetails(WifiP2pConfig wifiP2pConfig) {
            this.mPeers.updateGroupCapability(wifiP2pConfig.deviceAddress, this.mWifiNative.getGroupCapability(wifiP2pConfig.deviceAddress));
            return this.mPeers.get(wifiP2pConfig.deviceAddress);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void p2pConnectWithPinDisplay(WifiP2pConfig wifiP2pConfig) {
            String strP2pConnect = this.mWifiNative.p2pConnect(wifiP2pConfig, fetchCurrentDeviceDetails(wifiP2pConfig).isGroupOwner());
            try {
                Integer.parseInt(strP2pConnect);
                notifyInvitationSent(strP2pConnect, wifiP2pConfig.deviceAddress);
            } catch (NumberFormatException unused) {
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean reinvokePersistentGroup(WifiP2pConfig wifiP2pConfig) {
            int networkId;
            WifiP2pDevice wifiP2pDeviceFetchCurrentDeviceDetails = fetchCurrentDeviceDetails(wifiP2pConfig);
            boolean zIsGroupOwner = wifiP2pDeviceFetchCurrentDeviceDetails.isGroupOwner();
            String strP2pGetSsid = this.mWifiNative.p2pGetSsid(wifiP2pDeviceFetchCurrentDeviceDetails.deviceAddress);
            logd("target ssid is " + strP2pGetSsid + " join:" + zIsGroupOwner);
            if (zIsGroupOwner && wifiP2pDeviceFetchCurrentDeviceDetails.isGroupLimit()) {
                zIsGroupOwner = false;
            } else if (zIsGroupOwner && (networkId = this.mGroups.getNetworkId(wifiP2pDeviceFetchCurrentDeviceDetails.deviceAddress, strP2pGetSsid)) >= 0) {
                return this.mWifiNative.p2pGroupAdd(networkId);
            }
            if (!zIsGroupOwner && wifiP2pDeviceFetchCurrentDeviceDetails.isDeviceLimit()) {
                loge("target device reaches the device limit.");
                return false;
            }
            if (!zIsGroupOwner && wifiP2pDeviceFetchCurrentDeviceDetails.isInvitationCapable()) {
                int networkId2 = -2;
                if (wifiP2pConfig.netId >= 0) {
                    if (wifiP2pConfig.deviceAddress.equals(this.mGroups.getOwnerAddr(wifiP2pConfig.netId))) {
                        networkId2 = wifiP2pConfig.netId;
                    }
                } else {
                    networkId2 = this.mGroups.getNetworkId(wifiP2pDeviceFetchCurrentDeviceDetails.deviceAddress);
                }
                if (networkId2 < 0) {
                    networkId2 = getNetworkIdFromClientList(wifiP2pDeviceFetchCurrentDeviceDetails.deviceAddress);
                    boolean zStartsWith = false;
                    for (int i = 0; i < WifiP2pService.this.mNoInvitDevMacList.length && !(zStartsWith = wifiP2pDeviceFetchCurrentDeviceDetails.deviceAddress.toLowerCase().startsWith(WifiP2pService.this.mNoInvitDevMacList[i].toLowerCase())); i++) {
                        try {
                        } catch (NullPointerException unused) {
                        }
                    }
                    if (zStartsWith) {
                        networkId2 = -1;
                        Slog.d(WifiP2pService.TAG, "allshare p2pclient persistent.");
                    }
                }
                if (networkId2 >= 0) {
                    if (this.mWifiNative.p2pReinvoke(networkId2, wifiP2pDeviceFetchCurrentDeviceDetails.deviceAddress)) {
                        wifiP2pConfig.netId = networkId2;
                        return true;
                    }
                    loge("p2pReinvoke() failed, update networks");
                    updatePersistentNetworks(WifiP2pService.RELOAD.booleanValue());
                }
            }
            return false;
        }

        private int getNetworkIdFromClientList(String str) {
            if (str == null) {
                return -1;
            }
            Iterator<WifiP2pGroup> it = this.mGroups.getGroupList().iterator();
            while (it.hasNext()) {
                int networkId = it.next().getNetworkId();
                String[] clientList = getClientList(networkId);
                if (clientList != null) {
                    for (String str2 : clientList) {
                        if (str.equalsIgnoreCase(str2)) {
                            return networkId;
                        }
                    }
                }
            }
            return -1;
        }

        private String[] getClientList(int i) {
            String networkVariable = this.mWifiNative.getNetworkVariable(i, "p2p_client_list");
            if (networkVariable == null) {
                return null;
            }
            return networkVariable.split(" ");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean removeClientFromList(int i, String str, boolean z) {
            boolean z2;
            StringBuilder sb = new StringBuilder();
            String[] clientList = getClientList(i);
            if (clientList != null) {
                z2 = false;
                for (String str2 : clientList) {
                    if (str2.equalsIgnoreCase(str)) {
                        z2 = true;
                    } else {
                        sb.append(" ");
                        sb.append(str2);
                    }
                }
            } else {
                z2 = false;
            }
            if (sb.length() == 0 && z) {
                this.mGroups.remove(i);
                return true;
            }
            if (!z2) {
                return false;
            }
            if (sb.length() == 0) {
                sb.append("\"\"");
            }
            this.mWifiNative.setNetworkVariable(i, "p2p_client_list", sb.toString());
            this.mWifiNative.saveConfig();
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setWifiP2pInfoOnGroupFormation(InetAddress inetAddress) {
            this.mWifiP2pInfo.groupFormed = true;
            this.mWifiP2pInfo.isGroupOwner = this.mGroup.isGroupOwner();
            this.mWifiP2pInfo.groupOwnerAddress = inetAddress;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void resetWifiP2pInfo() {
            this.mWifiP2pInfo.groupFormed = false;
            this.mWifiP2pInfo.isGroupOwner = false;
            this.mWifiP2pInfo.groupOwnerAddress = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getDeviceName(String str) {
            WifiP2pDevice wifiP2pDevice = this.mPeers.get(str);
            return wifiP2pDevice != null ? wifiP2pDevice.deviceName : str;
        }

        private String getPersistedDeviceName() {
            String string = Settings.Global.getString(WifiP2pService.this.mContext.getContentResolver(), Settings.Global.WIFI_P2P_DEVICE_NAME);
            if (string != null) {
                return string;
            }
            return "Android_" + Settings.Secure.getString(WifiP2pService.this.mContext.getContentResolver(), "android_id").substring(0, 4);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean setAndPersistDeviceName(String str) {
            if (str == null) {
                return false;
            }
            if (this.mWifiNative.setDeviceName(str)) {
                WifiP2pService.this.mThisDevice.deviceName = str;
                this.mWifiNative.setP2pSsidPostfix("-" + WifiP2pService.this.mThisDevice.deviceName);
                Settings.Global.putString(WifiP2pService.this.mContext.getContentResolver(), Settings.Global.WIFI_P2P_DEVICE_NAME, str);
                sendThisDeviceChangedBroadcast();
                return true;
            }
            loge("Failed to set device name " + str);
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean setWfdInfo(WifiP2pWfdInfo wifiP2pWfdInfo) {
            boolean wfdEnable;
            if (!wifiP2pWfdInfo.isWfdEnabled()) {
                wfdEnable = this.mWifiNative.setWfdEnable(false);
            } else {
                wfdEnable = this.mWifiNative.setWfdEnable(true) && this.mWifiNative.setWfdDeviceInfo(wifiP2pWfdInfo.getDeviceInfoHex());
            }
            if (wfdEnable) {
                WifiP2pService.this.mThisDevice.wfdInfo = wifiP2pWfdInfo;
                sendThisDeviceChangedBroadcast();
                return true;
            }
            loge("Failed to set wfd properties");
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void initializeP2pSettings() {
            this.mWifiNative.setPersistentReconnect(true);
            WifiP2pService.this.mThisDevice.deviceName = getPersistedDeviceName();
            this.mWifiNative.setDeviceName(WifiP2pService.this.mThisDevice.deviceName);
            this.mWifiNative.setP2pSsidPostfix("-" + WifiP2pService.this.mThisDevice.deviceName);
            this.mWifiNative.setDeviceType(WifiP2pService.this.mThisDevice.primaryDeviceType);
            this.mWifiNative.setConfigMethods("virtual_push_button physical_display keypad");
            this.mWifiNative.setConcurrencyPriority("sta");
            WifiP2pService.this.mThisDevice.deviceAddress = this.mWifiNative.p2pGetDeviceAddress();
            updateThisDevice(3);
            this.mWifiNative.setWfdEnable(true);
            WifiP2pService.this.mClientInfoList.clear();
            this.mWifiNative.p2pFlush();
            this.mWifiNative.p2pServiceFlush();
            WifiP2pService.this.mServiceTransactionId = (byte) 0;
            WifiP2pService.this.mServiceDiscReqId = null;
            String string = Settings.Global.getString(WifiP2pService.this.mContext.getContentResolver(), "wifi_country_code");
            if (string != null && !string.isEmpty()) {
                WifiP2pService.this.mP2pStateMachine.sendMessage(WifiP2pService.SET_COUNTRY_CODE, string);
            }
            updatePersistentNetworks(WifiP2pService.RELOAD.booleanValue());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateThisDevice(int i) {
            WifiP2pService.this.mThisDevice.status = i;
            sendThisDeviceChangedBroadcast();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleGroupCreationFailure() {
            resetWifiP2pInfo();
            WifiP2pService.this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.FAILED, null, null);
            sendP2pConnectionChangedBroadcast();
            boolean zRemove = this.mPeers.remove(this.mPeersLostDuringConnection);
            if (this.mPeers.remove(this.mSavedPeerConfig.deviceAddress) != null) {
                zRemove = true;
            }
            if (zRemove) {
                sendPeersChangedBroadcast();
            }
            this.mPeersLostDuringConnection.clear();
            WifiP2pService.this.mServiceDiscReqId = null;
            sendMessage(WifiP2pManager.DISCOVER_PEERS);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleGroupRemoved() {
            if (!this.mGroup.isGroupOwner()) {
                WifiP2pService.this.mDhcpStateMachine.sendMessage(DhcpStateMachine.CMD_STOP_DHCP);
                WifiP2pService.this.mDhcpStateMachine.doQuit();
                WifiP2pService.this.mDhcpStateMachine = null;
            } else {
                stopDhcpServer(this.mGroup.getInterface());
            }
            try {
                WifiP2pService.this.mNwService.clearInterfaceAddresses(this.mGroup.getInterface());
            } catch (Exception e) {
                loge("Failed to clear addresses " + e);
            }
            NetworkUtils.resetConnections(this.mGroup.getInterface(), 3);
            this.mWifiNative.setP2pGroupIdle(this.mGroup.getInterface(), 0);
            Iterator<WifiP2pDevice> it = this.mGroup.getClientList().iterator();
            boolean z = false;
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                if (this.mPeers.remove(it.next())) {
                    z = true;
                }
            }
            if (this.mPeers.remove(this.mGroup.getOwner())) {
                z = true;
            }
            if (this.mPeers.remove(this.mPeersLostDuringConnection) ? true : z) {
                sendPeersChangedBroadcast();
            }
            this.mGroup = null;
            this.mPeersLostDuringConnection.clear();
            WifiP2pService.this.mServiceDiscReqId = null;
            if (WifiP2pService.this.mTempoarilyDisconnectedWifi) {
                WifiP2pService.this.mWifiChannel.sendMessage(WifiP2pService.DISCONNECT_WIFI_REQUEST, 0);
                WifiP2pService.this.mTempoarilyDisconnectedWifi = false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void replyToMessage(Message message, int i) {
            if (message.replyTo == null) {
                return;
            }
            Message messageObtainMessage = obtainMessage(message);
            messageObtainMessage.what = i;
            WifiP2pService.this.mReplyChannel.replyToMessage(message, messageObtainMessage);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void replyToMessage(Message message, int i, int i2) {
            if (message.replyTo == null) {
                return;
            }
            Message messageObtainMessage = obtainMessage(message);
            messageObtainMessage.what = i;
            messageObtainMessage.arg1 = i2;
            WifiP2pService.this.mReplyChannel.replyToMessage(message, messageObtainMessage);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void replyToMessage(Message message, int i, Object obj) {
            if (message.replyTo == null) {
                return;
            }
            Message messageObtainMessage = obtainMessage(message);
            messageObtainMessage.what = i;
            messageObtainMessage.obj = obj;
            WifiP2pService.this.mReplyChannel.replyToMessage(message, messageObtainMessage);
        }

        private Message obtainMessage(Message message) {
            Message messageObtain = Message.obtain();
            messageObtain.arg2 = message.arg2;
            return messageObtain;
        }

        protected void logd(String str) {
            Slog.d(WifiP2pService.TAG, str);
        }

        protected void loge(String str) {
            Slog.e(WifiP2pService.TAG, str);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public WifiP2pDevice getWifiP2pDeviceFromPeers(String str) {
            for (WifiP2pDevice wifiP2pDevice : this.mPeers.getDeviceList()) {
                if (comparedMacAddr(wifiP2pDevice.deviceAddress, str)) {
                    return wifiP2pDevice;
                }
            }
            return null;
        }

        private boolean comparedMacAddr(String str, String str2) {
            if (str != null && str2 != null) {
                char[] charArray = str.toCharArray();
                char[] charArray2 = str2.toCharArray();
                int length = charArray.length;
                if (length != charArray2.length) {
                    return false;
                }
                int i = 0;
                for (int i2 = 0; i2 < length; i2++) {
                    if (charArray[i2] == charArray2[i2]) {
                        i++;
                    }
                }
                if (i >= length - 1) {
                    return true;
                }
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean updateSupplicantServiceRequest() {
            clearSupplicantServiceRequest();
            StringBuffer stringBuffer = new StringBuffer();
            Iterator it = WifiP2pService.this.mClientInfoList.values().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                ClientInfo clientInfo = (ClientInfo) it.next();
                for (int i = 0; i < clientInfo.mReqList.size(); i++) {
                    WifiP2pServiceRequest wifiP2pServiceRequest = (WifiP2pServiceRequest) clientInfo.mReqList.valueAt(i);
                    if (wifiP2pServiceRequest != null) {
                        stringBuffer.append(wifiP2pServiceRequest.getSupplicantQuery());
                    }
                }
            }
            if (stringBuffer.length() == 0) {
                return false;
            }
            WifiP2pService.this.mServiceDiscReqId = this.mWifiNative.p2pServDiscReq("00:00:00:00:00:00", stringBuffer.toString());
            return WifiP2pService.this.mServiceDiscReqId != null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearSupplicantServiceRequest() {
            if (WifiP2pService.this.mServiceDiscReqId == null) {
                return;
            }
            this.mWifiNative.p2pServDiscCancelReq(WifiP2pService.this.mServiceDiscReqId);
            WifiP2pService.this.mServiceDiscReqId = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean addServiceRequest(Messenger messenger, WifiP2pServiceRequest wifiP2pServiceRequest) {
            clearClientDeadChannels();
            ClientInfo clientInfo = getClientInfo(messenger, true);
            if (clientInfo == null) {
                return false;
            }
            WifiP2pService.access$12204(WifiP2pService.this);
            if (WifiP2pService.this.mServiceTransactionId == 0) {
                WifiP2pService.access$12204(WifiP2pService.this);
            }
            wifiP2pServiceRequest.setTransactionId(WifiP2pService.this.mServiceTransactionId);
            clientInfo.mReqList.put(WifiP2pService.this.mServiceTransactionId, wifiP2pServiceRequest);
            if (WifiP2pService.this.mServiceDiscReqId == null) {
                return true;
            }
            return updateSupplicantServiceRequest();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeServiceRequest(Messenger messenger, WifiP2pServiceRequest wifiP2pServiceRequest) {
            boolean z = false;
            ClientInfo clientInfo = getClientInfo(messenger, false);
            if (clientInfo == null) {
                return;
            }
            int i = 0;
            while (true) {
                if (i >= clientInfo.mReqList.size()) {
                    break;
                }
                if (wifiP2pServiceRequest.equals(clientInfo.mReqList.valueAt(i))) {
                    clientInfo.mReqList.removeAt(i);
                    z = true;
                    break;
                }
                i++;
            }
            if (z) {
                if (clientInfo.mReqList.size() == 0 && clientInfo.mServList.size() == 0) {
                    WifiP2pService.this.mClientInfoList.remove(clientInfo.mMessenger);
                }
                if (WifiP2pService.this.mServiceDiscReqId == null) {
                    return;
                }
                updateSupplicantServiceRequest();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearServiceRequests(Messenger messenger) {
            ClientInfo clientInfo = getClientInfo(messenger, false);
            if (clientInfo == null || clientInfo.mReqList.size() == 0) {
                return;
            }
            clientInfo.mReqList.clear();
            if (clientInfo.mServList.size() == 0) {
                WifiP2pService.this.mClientInfoList.remove(clientInfo.mMessenger);
            }
            if (WifiP2pService.this.mServiceDiscReqId == null) {
                return;
            }
            updateSupplicantServiceRequest();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean addLocalService(Messenger messenger, WifiP2pServiceInfo wifiP2pServiceInfo) {
            clearClientDeadChannels();
            ClientInfo clientInfo = getClientInfo(messenger, true);
            if (clientInfo == null || !clientInfo.mServList.add(wifiP2pServiceInfo)) {
                return false;
            }
            if (this.mWifiNative.p2pServiceAdd(wifiP2pServiceInfo)) {
                return true;
            }
            clientInfo.mServList.remove(wifiP2pServiceInfo);
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeLocalService(Messenger messenger, WifiP2pServiceInfo wifiP2pServiceInfo) {
            ClientInfo clientInfo = getClientInfo(messenger, false);
            if (clientInfo == null) {
                return;
            }
            this.mWifiNative.p2pServiceDel(wifiP2pServiceInfo);
            clientInfo.mServList.remove(wifiP2pServiceInfo);
            if (clientInfo.mReqList.size() == 0 && clientInfo.mServList.size() == 0) {
                WifiP2pService.this.mClientInfoList.remove(clientInfo.mMessenger);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearLocalServices(Messenger messenger) {
            ClientInfo clientInfo = getClientInfo(messenger, false);
            if (clientInfo == null) {
                return;
            }
            Iterator it = clientInfo.mServList.iterator();
            while (it.hasNext()) {
                this.mWifiNative.p2pServiceDel((WifiP2pServiceInfo) it.next());
            }
            clientInfo.mServList.clear();
            if (clientInfo.mReqList.size() == 0) {
                WifiP2pService.this.mClientInfoList.remove(clientInfo.mMessenger);
            }
        }

        private void clearClientInfo(Messenger messenger) {
            clearLocalServices(messenger);
            clearServiceRequests(messenger);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendServiceResponse(WifiP2pServiceResponse wifiP2pServiceResponse) {
            for (ClientInfo clientInfo : WifiP2pService.this.mClientInfoList.values()) {
                if (((WifiP2pServiceRequest) clientInfo.mReqList.get(wifiP2pServiceResponse.getTransactionId())) != null) {
                    Message messageObtain = Message.obtain();
                    messageObtain.what = WifiP2pManager.RESPONSE_SERVICE;
                    messageObtain.arg1 = 0;
                    messageObtain.arg2 = 0;
                    messageObtain.obj = wifiP2pServiceResponse;
                    try {
                        clientInfo.mMessenger.send(messageObtain);
                    } catch (RemoteException unused) {
                        clearClientInfo(clientInfo.mMessenger);
                        return;
                    }
                }
            }
        }

        private void clearClientDeadChannels() {
            ArrayList arrayList = new ArrayList();
            for (ClientInfo clientInfo : WifiP2pService.this.mClientInfoList.values()) {
                Message messageObtain = Message.obtain();
                messageObtain.what = WifiP2pManager.PING;
                messageObtain.arg1 = 0;
                messageObtain.arg2 = 0;
                messageObtain.obj = null;
                try {
                    clientInfo.mMessenger.send(messageObtain);
                } catch (RemoteException unused) {
                    arrayList.add(clientInfo.mMessenger);
                }
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                clearClientInfo((Messenger) it.next());
            }
        }

        private ClientInfo getClientInfo(Messenger messenger, boolean z) {
            ClientInfo clientInfo = (ClientInfo) WifiP2pService.this.mClientInfoList.get(messenger);
            if (clientInfo != null || !z) {
                return clientInfo;
            }
            ClientInfo clientInfo2 = new ClientInfo(messenger);
            WifiP2pService.this.mClientInfoList.put(messenger, clientInfo2);
            return clientInfo2;
        }
    }

    private class ClientInfo {
        private Messenger mMessenger;
        private SparseArray<WifiP2pServiceRequest> mReqList;
        private List<WifiP2pServiceInfo> mServList;

        private ClientInfo(Messenger messenger) {
            this.mMessenger = messenger;
            this.mReqList = new SparseArray<>();
            this.mServList = new ArrayList();
        }
    }
}
