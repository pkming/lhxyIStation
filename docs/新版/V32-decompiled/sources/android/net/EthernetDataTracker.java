package android.net;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetManager;
import android.os.Handler;
import android.os.INetworkManagementService;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import com.android.server.net.BaseNetworkObserver;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes.dex */
public class EthernetDataTracker extends BaseNetworkStateTracker {
    private static final int CONN_TRY_TIME_OUT = 50;
    private static final boolean DEBUG = true;
    private static final int ERROR_CODE_AUTH_FAILED = 257;
    private static final int ERROR_CODE_ENTRY = 256;
    private static final int ERROR_CODE_USER_STOP = 258;
    private static final String ETHERNET_CONNECT_MODE_DHCP = "dhcp";
    private static final String ETHERNET_CONNECT_MODE_MANUAL = "manual";
    private static final String ETHERNET_CONNECT_MODE_PPPOE = "pppoe";
    public static final int MAX_CONN_TRY_TIMES = 3;
    private static final String NETWORKTYPE = "ETHERNET";
    private static final String PPPOE_CLOSE_PROTOCOL = "pppoe_close";
    public static final int PPPOE_CONNECT_CMD = 0;
    public static final int PPPOE_DISCONNECT_CMD = 1;
    public static final int PPPOE_IDLE_CMD = 1;
    private static final String PPPOE_PROTOCOL = "pppoe";
    private static final String PPP_STATE_GONE = "gone";
    private static final String PPP_STATE_RUNNING = "running";
    private static final int SUCCESS_CODE_ENTRY = 0;
    private static final int SUCCESS_CONNECT = 1;
    private static final String SVC_START_CMD = "ctl.start";
    private static final String SVC_STATE_CMD_PREFIX = "init.svc.";
    private static final String SVC_STATE_RUNNING = "running";
    private static final String SVC_STATE_STOPPED = "stopped";
    private static final String TAG = "EthernetDataTracker";
    private static boolean disconnecting = false;
    private static DhcpResults mDhcpResults = null;
    private static String mIface = "";
    private static EthernetDataTracker mInstance = null;
    private static boolean mLinkIn = false;
    private static boolean mLinkUp = false;
    private static boolean mReady = false;
    public static int mTry = 3;
    private static String sIfaceMatch = "";
    private static Object sLock = new Object();
    private Handler mCsHandler;
    private EthernetManager mEthManager;
    private InterfaceObserver mInterfaceObserver;
    private INetworkManagementService mNMService;
    private AtomicBoolean mTeardownRequested = new AtomicBoolean(false);
    private AtomicBoolean mPrivateDnsRouteSet = new AtomicBoolean(false);
    private AtomicInteger mDefaultGatewayAddr = new AtomicInteger(0);
    private AtomicBoolean mDefaultRouteSet = new AtomicBoolean(false);
    private final String SYS_NET = "/sys/class/net/";
    private String mHwaddr = "";
    private int prefixLength = 0;
    private PPPoEHandler mPppoeHandler = new PPPoEHandler();
    private int mState = 0;

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckComplete() {
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckCompleted(boolean z) {
    }

    @Override // android.net.NetworkStateTracker
    public String getTcpBufferSizesPropName() {
        return BaseNetworkStateTracker.PROP_TCP_BUFFER_WIFI;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setDependencyMet(boolean z) {
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean setRadio(boolean z) {
        return true;
    }

    public int startUsingNetworkFeature(String str, int i, int i2) {
        return -1;
    }

    public int stopUsingNetworkFeature(String str, int i, int i2) {
        return -1;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void supplyMessenger(Messenger messenger) {
    }

    public class PPPoEHandler implements Runnable {
        private static final String TAG = "PPPoEHandler";
        private Handler mHandler;

        public PPPoEHandler() {
        }

        @Override // java.lang.Runnable
        public void run() {
            EthernetDataTracker.this.mState = 5;
            Log.d(TAG, "PPPoE Thread started!");
            while (true) {
                if (EthernetDataTracker.this.mState == getState()) {
                    try {
                        Thread.sleep(50L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    EthernetDataTracker.this.mState = getState();
                    Log.i(TAG, "mLinkIn = " + EthernetDataTracker.mLinkIn + " mTry = " + EthernetDataTracker.mTry + " disconnecting = " + EthernetDataTracker.disconnecting + " mState = " + EthernetDataTracker.this.mState);
                    int i = EthernetDataTracker.this.mState;
                    if (i != 0) {
                        if (i == 1) {
                            Log.d(TAG, "PPPoE STARTING------------------");
                            try {
                                Thread.sleep(50L);
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        } else if (i == 2) {
                            Log.d(TAG, "PPPoE STARTED------------------");
                            EthernetDevInfo ethernetDevInfo = new EthernetDevInfo();
                            ethernetDevInfo.setIfName("ppp0");
                            ethernetDevInfo.setIpAddress(SystemProperties.get("net.ppp0.local-ip"));
                            ethernetDevInfo.setGateWay(SystemProperties.get("net.ppp0.remote-ip"));
                            ethernetDevInfo.setDns1(SystemProperties.get("net.ppp0.dns1"));
                            ethernetDevInfo.setDns2(SystemProperties.get("net.ppp0.dns2"));
                            DhcpResults unused = EthernetDataTracker.mDhcpResults = EthernetDataTracker.this.getIpConfigure(ethernetDevInfo);
                            EthernetDataTracker.this.mLinkProperties = EthernetDataTracker.mDhcpResults.linkProperties;
                            EthernetDataTracker.this.mLinkProperties.setInterfaceName("ppp0");
                            EthernetDataTracker.this.mNetworkInfo.setIsAvailable(true);
                            EthernetDataTracker.this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.CONNECTED, null, null);
                            EthernetDataTracker.this.mCsHandler.obtainMessage(458752, EthernetDataTracker.this.mNetworkInfo).sendToTarget();
                            EthernetDataTracker.this.sendNetStateBroadcast(21);
                            EthernetDataTracker.mTry = 3;
                        }
                    } else if (!EthernetDataTracker.mLinkIn || EthernetDataTracker.mTry == 0) {
                        Log.d(TAG, "PPPoE DISCONNECTED------------------");
                        Log.d(TAG, "PPPoE Disconnected");
                        if (!EthernetDataTracker.mLinkIn || EthernetDataTracker.disconnecting) {
                            if (EthernetDataTracker.disconnecting) {
                                boolean unused2 = EthernetDataTracker.disconnecting = false;
                            }
                            EthernetDataTracker.this.sendNetStateBroadcast(23);
                        } else {
                            EthernetDataTracker.this.sendNetStateBroadcast(20);
                        }
                        EthernetDataTracker.mTry = 3;
                    } else if (EthernetDataTracker.mTry != 0) {
                        try {
                            Log.d(TAG, "Connect pppoe try again " + ((3 - EthernetDataTracker.mTry) * 2) + "s later");
                            try {
                                Thread.sleep((3 - EthernetDataTracker.mTry) * 2000);
                            } catch (InterruptedException unused3) {
                            }
                        } catch (Exception e3) {
                            e3.printStackTrace();
                        }
                        if (EthernetDataTracker.mLinkIn) {
                            connect(EthernetDataTracker.mIface, EthernetDataTracker.this.mEthManager.getLoginInfo("pppoe").getUsername());
                            EthernetDataTracker.mTry--;
                        }
                    }
                }
            }
        }

        private int getState() {
            if (EthernetDataTracker.SVC_STATE_STOPPED.equals(SystemProperties.get("init.svc.pppoe"))) {
                return 0;
            }
            if ("running".equals(SystemProperties.get("init.svc.pppoe")) && SystemProperties.get("net.pppoe.reason").isEmpty()) {
                return 1;
            }
            return ("running".equals(SystemProperties.get("init.svc.pppoe")) && "running".equals(SystemProperties.get("net.pppoe.reason"))) ? 2 : 5;
        }

        public void connect(String str, String str2) throws InterruptedException {
            Log.d(TAG, "connect");
            Log.d(TAG, "SystemProperties.setpppoe:" + str + " " + str2);
            SystemProperties.set(EthernetDataTracker.SVC_START_CMD, "pppoe:" + str + " " + str2);
        }

        public boolean disconnect() {
            if (isStoped()) {
                return true;
            }
            Log.d(TAG, "disconnect");
            boolean unused = EthernetDataTracker.disconnecting = true;
            SystemProperties.set(EthernetDataTracker.SVC_START_CMD, EthernetDataTracker.PPPOE_CLOSE_PROTOCOL);
            SystemProperties.set("net.pppoe.reason", EthernetDataTracker.PPP_STATE_GONE);
            EthernetDataTracker.mTry = 0;
            return isStoped();
        }

        public boolean isConnect() {
            return "running".equals(SystemProperties.get("init.svc.pppoe")) && "running".equals(SystemProperties.get("net.pppoe.reason"));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isStoped() {
            return !"running".equals(SystemProperties.get("init.svc.pppoe"));
        }
    }

    private class InterfaceObserver extends BaseNetworkObserver {
        private EthernetDataTracker mTracker;

        InterfaceObserver(EthernetDataTracker ethernetDataTracker) {
            this.mTracker = ethernetDataTracker;
        }

        public void interfaceStatusChanged(String str, boolean z) {
            Log.d(EthernetDataTracker.TAG, "Interface status changed: " + str + (z ? "up" : "down"));
        }

        public void interfaceLinkStateChanged(String str, boolean z) {
            if (EthernetDataTracker.mIface.equals(str)) {
                Log.d(EthernetDataTracker.TAG, "Interface " + str + " link " + (z ? "in" : "out"));
                boolean unused = EthernetDataTracker.mLinkIn = z;
                this.mTracker.mNetworkInfo.setIsAvailable(z);
                if (z) {
                    EthernetDataTracker.this.sendEthStateBroadcast(4);
                    this.mTracker.reconnect();
                } else {
                    EthernetDataTracker.this.sendEthStateBroadcast(5);
                    this.mTracker.disconnect();
                }
            }
        }

        public void interfaceAdded(String str) {
            this.mTracker.interfaceAdded(str);
        }

        public void interfaceRemoved(String str) {
            this.mTracker.interfaceRemoved(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void interfaceAdded(String str) {
        Log.d(TAG, "interfaceAdded " + str);
        if (!this.mEthManager.addInterfaceToService(str)) {
            Log.w(TAG, "add iface[" + str + "] to ethernet list failed.");
            return;
        }
        if (!str.matches(sIfaceMatch)) {
            Log.w(TAG, "iface[" + str + "] not match!");
            return;
        }
        Log.d(TAG, "Adding " + str);
        sendEthStateBroadcast(6);
        synchronized (this) {
            if (mIface.isEmpty()) {
                Log.d(TAG, "update mIface[" + str + "]");
                mIface = str;
                try {
                    this.mNMService.setInterfaceUp(str);
                } catch (Exception e) {
                    Log.e(TAG, "Error upping interface " + str + ": " + e);
                }
                reconnect();
                this.mNetworkInfo.setIsAvailable(true);
                this.mCsHandler.obtainMessage(NetworkStateTracker.EVENT_CONFIGURATION_CHANGED, this.mNetworkInfo).sendToTarget();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void interfaceRemoved(String str) {
        Log.d(TAG, "interface removed[" + str + "]");
        this.mEthManager.removeInterfaceFromService(str);
        if (!str.equals(mIface)) {
            Log.w(TAG, "removed inteface[" + str + "] not match!");
            return;
        }
        sendEthStateBroadcast(7);
        Log.d(TAG, "Removing " + str);
        disconnect();
        mIface = "";
    }

    private EthernetDataTracker() {
        this.mNetworkInfo = new NetworkInfo(9, 0, NETWORKTYPE, "");
        this.mLinkProperties = new LinkProperties();
        this.mLinkCapabilities = new LinkCapabilities();
        mDhcpResults = new DhcpResults();
        try {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            if (Settings.Secure.getInt(contentResolver, Settings.Secure.PPPOE_AUTO_CONN) != 0) {
                return;
            }
            Settings.Secure.putInt(contentResolver, Settings.Secure.PPPOE_ENABLE, 0);
            Settings.Global.putInt(contentResolver, Settings.Global.ETHERNET_USE_PPPOE, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized EthernetDataTracker getInstance() {
        if (mInstance == null) {
            mInstance = new EthernetDataTracker();
        }
        return mInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendNetStateBroadcast(int i) {
        Intent intent = new Intent(EthernetManager.NETWORK_STATE_CHANGED_ACTION);
        intent.addFlags(603979776);
        intent.putExtra("networkInfo", this.mNetworkInfo);
        intent.putExtra("linkProperties", new LinkProperties(this.mLinkProperties));
        intent.putExtra("ethernet_state", i);
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendEthStateBroadcast(int i) {
        Intent intent = new Intent("android.net.ethernet.ETHERNET_STATE_CHANGE");
        intent.addFlags(603979776);
        intent.putExtra("ethernet_state", i);
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    public void connect() {
        if (mReady) {
            Log.d(TAG, "mLinkIn is " + mLinkIn + ", Enable is " + this.mEthManager.getEthernetState() + ", mIface " + mIface);
            if (this.mEthManager.getEthernetState() != 1) {
                Log.d(TAG, "Current Ethernet is disabled!");
                return;
            }
            if (!mLinkIn) {
                Log.d(TAG, "Current inteface link out!");
                return;
            }
            Log.d(TAG, "Current interface link in!");
            if (this.mEthManager.getEthernetMode().equals("dhcp")) {
                try {
                    this.mNMService.clearInterfaceAddresses(mIface);
                    NetworkUtils.resetConnections(mIface, 0);
                } catch (RemoteException e) {
                    Log.e(TAG, "ERROR: " + e);
                }
                if (SystemProperties.get("dhcp." + mIface + ".result").equals("ok")) {
                    NetworkUtils.stopDhcp(mIface);
                    sendNetStateBroadcast(3);
                }
                Log.d(TAG, "DHCP Mode: connecting and running dhcp.");
                runDhcp();
                return;
            }
            if (this.mEthManager.getEthernetMode().equals("manual")) {
                NetworkUtils.stopDhcp(mIface);
                EthernetDevInfo staticConfig = this.mEthManager.getStaticConfig();
                if (staticConfig == null) {
                    Log.e(TAG, "get configuration failed.");
                    mDhcpResults.clear();
                    sendNetStateBroadcast(0);
                    return;
                }
                DhcpResults ipConfigure = getIpConfigure(staticConfig);
                mDhcpResults = ipConfigure;
                this.mLinkProperties = ipConfigure.linkProperties;
                this.mLinkProperties.setInterfaceName(mIface);
                InterfaceConfiguration interfaceConfiguration = new InterfaceConfiguration();
                NetworkUtils.numericToInetAddress(staticConfig.getIpAddress());
                interfaceConfiguration.setLinkAddress(new LinkAddress(NetworkUtils.numericToInetAddress(staticConfig.getIpAddress()), this.prefixLength));
                interfaceConfiguration.setInterfaceUp();
                try {
                    this.mNMService.setInterfaceConfig(mIface, interfaceConfiguration);
                    Log.d(TAG, "Manual Mode: connecting and confgure static ip address.");
                    this.mNetworkInfo.setIsAvailable(true);
                    this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.CONNECTED, null, null);
                    this.mCsHandler.obtainMessage(458752, this.mNetworkInfo).sendToTarget();
                    sendNetStateBroadcast(1);
                    return;
                } catch (Exception e2) {
                    Log.e(TAG, "ERROR: " + e2);
                    sendNetStateBroadcast(0);
                    return;
                }
            }
            if (this.mEthManager.getEthernetMode().equals("pppoe")) {
                Log.d(TAG, "PPPoE Mode: connecting and start thread");
                try {
                    this.mPppoeHandler.connect(mIface, this.mEthManager.getLoginInfo("pppoe").getUsername().replace('\"', ' ').trim());
                } catch (Exception e3) {
                    Log.d(TAG, "Call PPPoEHandler.connect eroor: " + e3);
                }
            }
        }
    }

    public void disconnect() {
        if (mReady) {
            Log.d(TAG, "disconnect");
            String ethernetMode = this.mEthManager.getEthernetMode();
            if (ethernetMode.equals("pppoe")) {
                this.mPppoeHandler.disconnect();
                while (!this.mPppoeHandler.isStoped()) {
                    Log.d(TAG, "disconnect pppoe wating, sleep 500ms");
                    try {
                        Thread.sleep(500L);
                    } catch (Exception unused) {
                        Log.e(TAG, "Thread error!");
                    }
                    if (this.mPppoeHandler.isConnect()) {
                        break;
                    }
                }
            }
            NetworkUtils.stopDhcp(mIface);
            this.mLinkProperties.clear();
            this.mNetworkInfo.setIsAvailable(false);
            this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.DISCONNECTED, null, this.mHwaddr);
            mDhcpResults.clear();
            this.mCsHandler.obtainMessage(NetworkStateTracker.EVENT_CONFIGURATION_CHANGED, this.mNetworkInfo).sendToTarget();
            this.mCsHandler.obtainMessage(458752, this.mNetworkInfo).sendToTarget();
            if (!ethernetMode.equals("pppoe")) {
                sendNetStateBroadcast(3);
            }
            INetworkManagementService iNetworkManagementServiceAsInterface = INetworkManagementService.Stub.asInterface(ServiceManager.getService(Context.NETWORKMANAGEMENT_SERVICE));
            if (this.mEthManager.getState() != 0) {
                try {
                    iNetworkManagementServiceAsInterface.clearInterfaceAddresses(mIface);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to clear addresses or disable ipv6" + e);
                }
            }
        }
    }

    private void runDhcp() {
        new Thread(new Runnable() { // from class: android.net.EthernetDataTracker.1
            @Override // java.lang.Runnable
            public void run() {
                Log.d(EthernetDataTracker.TAG, "Start DHCP thread!");
                EthernetDataTracker.mDhcpResults.clear();
                if (NetworkUtils.runDhcp(EthernetDataTracker.mIface, EthernetDataTracker.mDhcpResults)) {
                    Log.d(EthernetDataTracker.TAG, "DHCP Success\ndhcpResults = " + EthernetDataTracker.mDhcpResults.toString());
                    EthernetDataTracker.this.mLinkProperties = EthernetDataTracker.mDhcpResults.linkProperties;
                    EthernetDataTracker.this.mNetworkInfo.setIsAvailable(true);
                    EthernetDataTracker.this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.CONNECTED, null, EthernetDataTracker.this.mHwaddr);
                    EthernetDataTracker.this.mCsHandler.obtainMessage(458752, EthernetDataTracker.this.mNetworkInfo).sendToTarget();
                    EthernetDataTracker.this.sendNetStateBroadcast(1);
                    return;
                }
                Log.e(EthernetDataTracker.TAG, "DHCP request error:" + NetworkUtils.getDhcpError());
                EthernetDataTracker.this.sendNetStateBroadcast(0);
            }
        }).start();
    }

    @Override // android.net.NetworkStateTracker
    public boolean teardown() {
        this.mTeardownRequested.set(true);
        disconnect();
        return true;
    }

    @Override // android.net.NetworkStateTracker
    public boolean reconnect() {
        if (mLinkIn) {
            this.mTeardownRequested.set(false);
            connect();
        }
        return mLinkIn;
    }

    public static String getMaskFromIp(String str) {
        String strSubstring;
        Integer numValueOf;
        if (str == null || (strSubstring = str.substring(0, str.indexOf("."))) == null || (numValueOf = Integer.valueOf(strSubstring)) == null) {
            return "255.255.255.255";
        }
        int iIntValue = numValueOf.intValue();
        return (iIntValue >= 128 || iIntValue <= 0) ? iIntValue < 192 ? "255.255.0.0" : iIntValue < 224 ? "255.255.255.0" : "255.255.255.255" : "255.0.0.0";
    }

    public DhcpResults getIpConfigure(EthernetDevInfo ethernetDevInfo) {
        InetAddress inetAddressNumericToInetAddress;
        DhcpResults dhcpResults = new DhcpResults();
        if (ethernetDevInfo == null) {
            return null;
        }
        if (ethernetDevInfo.getNetMask() == null || ethernetDevInfo.getNetMask().matches("")) {
            inetAddressNumericToInetAddress = NetworkUtils.numericToInetAddress(getMaskFromIp(ethernetDevInfo.getIpAddress()));
        } else {
            inetAddressNumericToInetAddress = NetworkUtils.numericToInetAddress(ethernetDevInfo.getNetMask());
        }
        this.prefixLength = NetworkUtils.netmaskIntToPrefixLength(NetworkUtils.inetAddressToInt((Inet4Address) inetAddressNumericToInetAddress));
        if (ethernetDevInfo.getGateWay() != null && !ethernetDevInfo.getGateWay().matches("")) {
            NetworkUtils.numericToInetAddress(ethernetDevInfo.getGateWay());
            dhcpResults.addGateway(ethernetDevInfo.getGateWay());
        } else {
            NetworkUtils.numericToInetAddress("0.0.0.0");
        }
        dhcpResults.addLinkAddress(ethernetDevInfo.getIpAddress(), this.prefixLength);
        dhcpResults.addDns(ethernetDevInfo.getDns1());
        return dhcpResults;
    }

    public DhcpResults getDhcpResults() {
        return mDhcpResults;
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x00d4, code lost:
    
        android.net.EthernetDataTracker.mIface = r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x00d6, code lost:
    
        r6.mNMService.setInterfaceUp(r3);
        android.util.Log.d(android.net.EthernetDataTracker.TAG, "Set interface(" + r3 + ") up!");
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x00f8, code lost:
    
        r7 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x00f9, code lost:
    
        android.util.Log.e(android.net.EthernetDataTracker.TAG, "Error upping interface " + r3 + ": " + r7);
     */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0188  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x01bf  */
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
    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void startMonitoring(android.content.Context r7, android.os.Handler r8) {
        /*
            Method dump skipped, instruction units count: 455
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.EthernetDataTracker.startMonitoring(android.content.Context, android.os.Handler):void");
    }

    public String getActiveIface() {
        return mIface;
    }

    public boolean getLinkState() {
        return mLinkIn;
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x00af  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x00b2  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x00c4 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:50:0x008c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean checkLink(java.lang.String r8) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 222
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.EthernetDataTracker.checkLink(java.lang.String):boolean");
    }

    public int getPppoeStatus() {
        return this.mState;
    }

    public Object Clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setTeardownRequested(boolean z) {
        this.mTeardownRequested.set(z);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isTeardownRequested() {
        return this.mTeardownRequested.get();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public synchronized boolean isAvailable() {
        return this.mNetworkInfo.isAvailable();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setUserDataEnable(boolean z) {
        Log.w(TAG, "ignoring setUserDataEnable(" + z + ")");
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setPolicyDataEnable(boolean z) {
        Log.w(TAG, "ignoring setPolicyDataEnable(" + z + ")");
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isPrivateDnsRouteSet() {
        return this.mPrivateDnsRouteSet.get();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void privateDnsRouteSet(boolean z) {
        this.mPrivateDnsRouteSet.set(z);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public synchronized NetworkInfo getNetworkInfo() {
        return this.mNetworkInfo;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public synchronized LinkProperties getLinkProperties() {
        return new LinkProperties(this.mLinkProperties);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public LinkCapabilities getLinkCapabilities() {
        return new LinkCapabilities(this.mLinkCapabilities);
    }

    public int getDefaultGatewayAddr() {
        return this.mDefaultGatewayAddr.get();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isDefaultRouteSet() {
        return this.mDefaultRouteSet.get();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void defaultRouteSet(boolean z) {
        this.mDefaultRouteSet.set(z);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void addStackedLink(LinkProperties linkProperties) {
        this.mLinkProperties.addStackedLink(linkProperties);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void removeStackedLink(LinkProperties linkProperties) {
        this.mLinkProperties.removeStackedLink(linkProperties);
    }
}
