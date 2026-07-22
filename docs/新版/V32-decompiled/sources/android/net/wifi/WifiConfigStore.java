package android.net.wifi;

import android.content.Context;
import android.content.Intent;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.ProxyProperties;
import android.net.RouteInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WpsResult;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.UserHandle;
import android.security.KeyStore;
import android.text.TextUtils;
import android.util.LocalLog;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class WifiConfigStore {
    private static final boolean DBG = true;
    private static final String DNS_KEY = "dns";
    private static final String EOS = "eos";
    private static final String EXCLUSION_LIST_KEY = "exclusionList";
    private static final String GATEWAY_KEY = "gateway";
    private static final String ID_KEY = "id";
    private static final int IPCONFIG_FILE_VERSION = 2;
    private static final String IP_ASSIGNMENT_KEY = "ipAssignment";
    private static final String LINK_ADDRESS_KEY = "linkAddress";
    private static final String PROXY_HOST_KEY = "proxyHost";
    private static final String PROXY_PAC_FILE = "proxyPac";
    private static final String PROXY_PORT_KEY = "proxyPort";
    private static final String PROXY_SETTINGS_KEY = "proxySettings";
    private static final String SUPPLICANT_CONFIG_FILE = "/data/misc/wifi/wpa_supplicant.conf";
    private static final String TAG = "WifiConfigStore";
    private static final boolean VDBG = false;
    private static final String ipConfigFile = Environment.getDataDirectory() + "/misc/wifi/ipconfig.txt";
    private Context mContext;
    private WifiNative mWifiNative;
    private HashMap<Integer, WifiConfiguration> mConfiguredNetworks = new HashMap<>();
    private HashMap<Integer, Integer> mNetworkIds = new HashMap<>();
    private int mLastPriority = -1;
    private final KeyStore mKeyStore = KeyStore.getInstance();
    private final LocalLog mLocalLog = null;
    private final WpaConfigFileObserver mFileObserver = null;

    WifiConfigStore(Context context, WifiNative wifiNative) {
        this.mContext = context;
        this.mWifiNative = wifiNative;
    }

    class WpaConfigFileObserver extends FileObserver {
        public WpaConfigFileObserver() {
            super(WifiConfigStore.SUPPLICANT_CONFIG_FILE, 8);
        }

        @Override // android.os.FileObserver
        public void onEvent(int i, String str) {
            if (i == 8) {
                new File(WifiConfigStore.SUPPLICANT_CONFIG_FILE);
            }
        }
    }

    void loadAndEnableAllNetworks() throws Throwable {
        log("Loading config and enabling all networks");
        loadConfiguredNetworks();
        enableAllNetworks();
    }

    List<WifiConfiguration> getConfiguredNetworks() {
        ArrayList arrayList = new ArrayList();
        Iterator<WifiConfiguration> it = this.mConfiguredNetworks.values().iterator();
        while (it.hasNext()) {
            arrayList.add(new WifiConfiguration(it.next()));
        }
        return arrayList;
    }

    void enableAllNetworks() {
        boolean z = false;
        for (WifiConfiguration wifiConfiguration : this.mConfiguredNetworks.values()) {
            if (wifiConfiguration != null && wifiConfiguration.status == 1) {
                if (this.mWifiNative.enableNetwork(wifiConfiguration.networkId, false)) {
                    wifiConfiguration.status = 2;
                    z = true;
                } else {
                    loge("Enable network failed on " + wifiConfiguration.networkId);
                }
            }
        }
        if (z) {
            this.mWifiNative.saveConfig();
            sendConfiguredNetworksChangedBroadcast();
        }
    }

    boolean selectNetwork(int i) {
        if (i == -1) {
            return false;
        }
        int i2 = this.mLastPriority;
        if (i2 == -1 || i2 > 1000000) {
            for (WifiConfiguration wifiConfiguration : this.mConfiguredNetworks.values()) {
                if (wifiConfiguration.networkId != -1) {
                    wifiConfiguration.priority = 0;
                    addOrUpdateNetworkNative(wifiConfiguration);
                }
            }
            this.mLastPriority = 0;
        }
        WifiConfiguration wifiConfiguration2 = new WifiConfiguration();
        wifiConfiguration2.networkId = i;
        int i3 = this.mLastPriority + 1;
        this.mLastPriority = i3;
        wifiConfiguration2.priority = i3;
        addOrUpdateNetworkNative(wifiConfiguration2);
        this.mWifiNative.saveConfig();
        enableNetworkWithoutBroadcast(i, true);
        return true;
    }

    NetworkUpdateResult saveNetwork(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration == null || (wifiConfiguration.networkId == -1 && wifiConfiguration.SSID == null)) {
            return new NetworkUpdateResult(-1);
        }
        boolean z = wifiConfiguration.networkId == -1;
        NetworkUpdateResult networkUpdateResultAddOrUpdateNetworkNative = addOrUpdateNetworkNative(wifiConfiguration);
        int networkId = networkUpdateResultAddOrUpdateNetworkNative.getNetworkId();
        if (z && networkId != -1) {
            this.mWifiNative.enableNetwork(networkId, false);
            this.mConfiguredNetworks.get(Integer.valueOf(networkId)).status = 2;
        }
        this.mWifiNative.saveConfig();
        sendConfiguredNetworksChangedBroadcast(wifiConfiguration, networkUpdateResultAddOrUpdateNetworkNative.isNewNetwork() ? 0 : 2);
        return networkUpdateResultAddOrUpdateNetworkNative;
    }

    void updateStatus(int i, NetworkInfo.DetailedState detailedState) {
        WifiConfiguration wifiConfiguration;
        if (i == -1 || (wifiConfiguration = this.mConfiguredNetworks.get(Integer.valueOf(i))) == null) {
            return;
        }
        int i2 = AnonymousClass1.$SwitchMap$android$net$NetworkInfo$DetailedState[detailedState.ordinal()];
        if (i2 == 1) {
            wifiConfiguration.status = 0;
        } else if (i2 == 2 && wifiConfiguration.status == 0) {
            wifiConfiguration.status = 2;
        }
    }

    boolean forgetNetwork(int i) {
        if (this.mWifiNative.removeNetwork(i)) {
            this.mWifiNative.saveConfig();
            removeConfigAndSendBroadcastIfNeeded(i);
            return true;
        }
        loge("Failed to remove network " + i);
        return false;
    }

    int addOrUpdateNetwork(WifiConfiguration wifiConfiguration) {
        NetworkUpdateResult networkUpdateResultAddOrUpdateNetworkNative = addOrUpdateNetworkNative(wifiConfiguration);
        if (networkUpdateResultAddOrUpdateNetworkNative.getNetworkId() != -1) {
            sendConfiguredNetworksChangedBroadcast(this.mConfiguredNetworks.get(Integer.valueOf(networkUpdateResultAddOrUpdateNetworkNative.getNetworkId())), networkUpdateResultAddOrUpdateNetworkNative.isNewNetwork ? 0 : 2);
        }
        return networkUpdateResultAddOrUpdateNetworkNative.getNetworkId();
    }

    boolean removeNetwork(int i) {
        boolean zRemoveNetwork = this.mWifiNative.removeNetwork(i);
        if (zRemoveNetwork) {
            removeConfigAndSendBroadcastIfNeeded(i);
        }
        return zRemoveNetwork;
    }

    private void removeConfigAndSendBroadcastIfNeeded(int i) {
        WifiConfiguration wifiConfiguration = this.mConfiguredNetworks.get(Integer.valueOf(i));
        if (wifiConfiguration != null) {
            if (wifiConfiguration.enterpriseConfig != null) {
                wifiConfiguration.enterpriseConfig.removeKeys(this.mKeyStore);
            }
            this.mConfiguredNetworks.remove(Integer.valueOf(i));
            this.mNetworkIds.remove(Integer.valueOf(configKey(wifiConfiguration)));
            writeIpAndProxyConfigurations();
            sendConfiguredNetworksChangedBroadcast(wifiConfiguration, 1);
        }
    }

    boolean enableNetwork(int i, boolean z) {
        WifiConfiguration wifiConfiguration;
        boolean zEnableNetworkWithoutBroadcast = enableNetworkWithoutBroadcast(i, z);
        if (z) {
            sendConfiguredNetworksChangedBroadcast();
        } else {
            synchronized (this.mConfiguredNetworks) {
                wifiConfiguration = this.mConfiguredNetworks.get(Integer.valueOf(i));
            }
            if (wifiConfiguration != null) {
                sendConfiguredNetworksChangedBroadcast(wifiConfiguration, 2);
            }
        }
        return zEnableNetworkWithoutBroadcast;
    }

    boolean enableNetworkWithoutBroadcast(int i, boolean z) {
        boolean zEnableNetwork = this.mWifiNative.enableNetwork(i, z);
        WifiConfiguration wifiConfiguration = this.mConfiguredNetworks.get(Integer.valueOf(i));
        if (wifiConfiguration != null) {
            wifiConfiguration.status = 2;
        }
        if (z) {
            markAllNetworksDisabledExcept(i);
        }
        return zEnableNetwork;
    }

    void disableAllNetworks() {
        boolean z = false;
        for (WifiConfiguration wifiConfiguration : this.mConfiguredNetworks.values()) {
            if (wifiConfiguration != null && wifiConfiguration.status != 1) {
                if (this.mWifiNative.disableNetwork(wifiConfiguration.networkId)) {
                    wifiConfiguration.status = 1;
                    z = true;
                } else {
                    loge("Disable network failed on " + wifiConfiguration.networkId);
                }
            }
        }
        if (z) {
            sendConfiguredNetworksChangedBroadcast();
        }
    }

    boolean disableNetwork(int i) {
        return disableNetwork(i, 0);
    }

    boolean disableNetwork(int i, int i2) {
        boolean zDisableNetwork = this.mWifiNative.disableNetwork(i);
        WifiConfiguration wifiConfiguration = this.mConfiguredNetworks.get(Integer.valueOf(i));
        if (wifiConfiguration == null || wifiConfiguration.status == 1) {
            wifiConfiguration = null;
        } else {
            wifiConfiguration.status = 1;
            wifiConfiguration.disableReason = i2;
        }
        if (wifiConfiguration != null) {
            sendConfiguredNetworksChangedBroadcast(wifiConfiguration, 2);
        }
        return zDisableNetwork;
    }

    boolean saveConfig() {
        return this.mWifiNative.saveConfig();
    }

    WpsResult startWpsWithPinFromAccessPoint(WpsInfo wpsInfo) {
        WpsResult wpsResult = new WpsResult();
        if (this.mWifiNative.startWpsRegistrar(wpsInfo.BSSID, wpsInfo.pin)) {
            markAllNetworksDisabled();
            wpsResult.status = WpsResult.Status.SUCCESS;
        } else {
            loge("Failed to start WPS pin method configuration");
            wpsResult.status = WpsResult.Status.FAILURE;
        }
        return wpsResult;
    }

    WpsResult startWpsWithPinFromDevice(WpsInfo wpsInfo) {
        WpsResult wpsResult = new WpsResult();
        wpsResult.pin = this.mWifiNative.startWpsPinDisplay(wpsInfo.BSSID);
        if (!TextUtils.isEmpty(wpsResult.pin)) {
            markAllNetworksDisabled();
            wpsResult.status = WpsResult.Status.SUCCESS;
        } else {
            loge("Failed to start WPS pin method configuration");
            wpsResult.status = WpsResult.Status.FAILURE;
        }
        return wpsResult;
    }

    WpsResult startWpsPbc(WpsInfo wpsInfo) {
        WpsResult wpsResult = new WpsResult();
        if (this.mWifiNative.startWpsPbc(wpsInfo.BSSID)) {
            markAllNetworksDisabled();
            wpsResult.status = WpsResult.Status.SUCCESS;
        } else {
            loge("Failed to start WPS push button configuration");
            wpsResult.status = WpsResult.Status.FAILURE;
        }
        return wpsResult;
    }

    LinkProperties getLinkProperties(int i) {
        WifiConfiguration wifiConfiguration = this.mConfiguredNetworks.get(Integer.valueOf(i));
        if (wifiConfiguration != null) {
            return new LinkProperties(wifiConfiguration.linkProperties);
        }
        return null;
    }

    void setLinkProperties(int i, LinkProperties linkProperties) {
        WifiConfiguration wifiConfiguration = this.mConfiguredNetworks.get(Integer.valueOf(i));
        if (wifiConfiguration != null) {
            if (wifiConfiguration.linkProperties != null) {
                linkProperties.setHttpProxy(wifiConfiguration.linkProperties.getHttpProxy());
            }
            wifiConfiguration.linkProperties = linkProperties;
        }
    }

    void clearLinkProperties(int i) {
        WifiConfiguration wifiConfiguration = this.mConfiguredNetworks.get(Integer.valueOf(i));
        if (wifiConfiguration == null || wifiConfiguration.linkProperties == null) {
            return;
        }
        ProxyProperties httpProxy = wifiConfiguration.linkProperties.getHttpProxy();
        wifiConfiguration.linkProperties.clear();
        wifiConfiguration.linkProperties.setHttpProxy(httpProxy);
    }

    ProxyProperties getProxyProperties(int i) {
        LinkProperties linkProperties = getLinkProperties(i);
        if (linkProperties != null) {
            return new ProxyProperties(linkProperties.getHttpProxy());
        }
        return null;
    }

    boolean isUsingStaticIp(int i) {
        WifiConfiguration wifiConfiguration = this.mConfiguredNetworks.get(Integer.valueOf(i));
        return wifiConfiguration != null && wifiConfiguration.ipAssignment == WifiConfiguration.IpAssignment.STATIC;
    }

    private void sendConfiguredNetworksChangedBroadcast(WifiConfiguration wifiConfiguration, int i) {
        Intent intent = new Intent(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
        intent.addFlags(67108864);
        intent.putExtra(WifiManager.EXTRA_MULTIPLE_NETWORKS_CHANGED, false);
        intent.putExtra(WifiManager.EXTRA_WIFI_CONFIGURATION, wifiConfiguration);
        intent.putExtra(WifiManager.EXTRA_CHANGE_REASON, i);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    private void sendConfiguredNetworksChangedBroadcast() {
        Intent intent = new Intent(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
        intent.addFlags(67108864);
        intent.putExtra(WifiManager.EXTRA_MULTIPLE_NETWORKS_CHANGED, true);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    void loadConfiguredNetworks() throws Throwable {
        BufferedReader bufferedReader;
        Throwable th;
        String strListNetworks = this.mWifiNative.listNetworks();
        this.mLastPriority = 0;
        this.mConfiguredNetworks.clear();
        this.mNetworkIds.clear();
        if (strListNetworks == null) {
            return;
        }
        String[] strArrSplit = strListNetworks.split("\n");
        for (int i = 1; i < strArrSplit.length; i++) {
            String[] strArrSplit2 = strArrSplit[i].split("\t");
            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            try {
                wifiConfiguration.networkId = Integer.parseInt(strArrSplit2[0]);
                if (strArrSplit2.length > 3) {
                    if (strArrSplit2[3].indexOf("[CURRENT]") != -1) {
                        wifiConfiguration.status = 0;
                    } else if (strArrSplit2[3].indexOf("[DISABLED]") != -1) {
                        wifiConfiguration.status = 1;
                    } else {
                        wifiConfiguration.status = 2;
                    }
                } else {
                    wifiConfiguration.status = 2;
                }
                readNetworkVariables(wifiConfiguration);
                if (wifiConfiguration.priority > this.mLastPriority) {
                    this.mLastPriority = wifiConfiguration.priority;
                }
                wifiConfiguration.ipAssignment = WifiConfiguration.IpAssignment.DHCP;
                wifiConfiguration.proxySettings = WifiConfiguration.ProxySettings.NONE;
                if (!this.mNetworkIds.containsKey(Integer.valueOf(configKey(wifiConfiguration)))) {
                    this.mConfiguredNetworks.put(Integer.valueOf(wifiConfiguration.networkId), wifiConfiguration);
                    this.mNetworkIds.put(Integer.valueOf(configKey(wifiConfiguration)), Integer.valueOf(wifiConfiguration.networkId));
                }
            } catch (NumberFormatException unused) {
                loge("Failed to read network-id '" + strArrSplit2[0] + "'");
            }
        }
        readIpAndProxyConfigurations();
        sendConfiguredNetworksChangedBroadcast();
        if (this.mNetworkIds.size() == 0) {
            BufferedReader bufferedReader2 = null;
            try {
                try {
                    bufferedReader = new BufferedReader(new FileReader(SUPPLICANT_CONFIG_FILE));
                    do {
                        try {
                        } catch (FileNotFoundException unused2) {
                            bufferedReader2 = bufferedReader;
                            if (bufferedReader2 == null) {
                                return;
                            }
                            bufferedReader2.close();
                        } catch (IOException unused3) {
                            bufferedReader2 = bufferedReader;
                            if (bufferedReader2 == null) {
                                return;
                            }
                            bufferedReader2.close();
                        } catch (Throwable th2) {
                            th = th2;
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException unused4) {
                                }
                            }
                            throw th;
                        }
                    } while (bufferedReader.readLine() != null);
                    bufferedReader.close();
                } catch (IOException unused5) {
                }
            } catch (FileNotFoundException unused6) {
            } catch (IOException unused7) {
            } catch (Throwable th3) {
                bufferedReader = null;
                th = th3;
            }
        }
    }

    private void markAllNetworksDisabledExcept(int i) {
        for (WifiConfiguration wifiConfiguration : this.mConfiguredNetworks.values()) {
            if (wifiConfiguration != null && wifiConfiguration.networkId != i && wifiConfiguration.status != 1) {
                wifiConfiguration.status = 1;
                wifiConfiguration.disableReason = 0;
            }
        }
    }

    private void markAllNetworksDisabled() {
        markAllNetworksDisabledExcept(-1);
    }

    boolean needsUnlockedKeyStore() {
        for (WifiConfiguration wifiConfiguration : this.mConfiguredNetworks.values()) {
            if (wifiConfiguration.allowedKeyManagement.get(2) && wifiConfiguration.allowedKeyManagement.get(3) && wifiConfiguration.enterpriseConfig.needsSoftwareBackedKeyStore()) {
                return true;
            }
        }
        return false;
    }

    private void writeIpAndProxyConfigurations() {
        ArrayList arrayList = new ArrayList();
        Iterator<WifiConfiguration> it = this.mConfiguredNetworks.values().iterator();
        while (it.hasNext()) {
            arrayList.add(new WifiConfiguration(it.next()));
        }
        DelayedDiskWrite.write(arrayList);
    }

    private static class DelayedDiskWrite {
        private static final String TAG = "DelayedDiskWrite";
        private static Handler sDiskWriteHandler;
        private static HandlerThread sDiskWriteHandlerThread;
        private static int sWriteSequence;

        private DelayedDiskWrite() {
        }

        static void write(final List<WifiConfiguration> list) {
            synchronized (DelayedDiskWrite.class) {
                int i = sWriteSequence + 1;
                sWriteSequence = i;
                if (i == 1) {
                    HandlerThread handlerThread = new HandlerThread("WifiConfigThread");
                    sDiskWriteHandlerThread = handlerThread;
                    handlerThread.start();
                    sDiskWriteHandler = new Handler(sDiskWriteHandlerThread.getLooper());
                }
            }
            sDiskWriteHandler.post(new Runnable() { // from class: android.net.wifi.WifiConfigStore.DelayedDiskWrite.1
                @Override // java.lang.Runnable
                public void run() throws Throwable {
                    DelayedDiskWrite.onWriteCalled(list);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Removed duplicated region for block: B:103:0x0207 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:113:0x01b4 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:37:0x010f  */
        /* JADX WARN: Removed duplicated region for block: B:45:0x014e A[Catch: NullPointerException -> 0x0197, IOException -> 0x01dc, all -> 0x0200, TryCatch #0 {all -> 0x0200, blocks: (B:5:0x0018, B:6:0x001f, B:8:0x0025, B:9:0x002b, B:13:0x003f, B:35:0x0103, B:41:0x0116, B:47:0x018a, B:51:0x01b4, B:42:0x011c, B:44:0x012d, B:45:0x014e, B:14:0x0046, B:16:0x0057, B:17:0x006d, B:19:0x0073, B:20:0x0091, B:21:0x0099, B:23:0x009f, B:25:0x00b0, B:27:0x00c9, B:29:0x00cf, B:30:0x00de, B:26:0x00c6, B:31:0x00e2, B:32:0x00ea, B:34:0x00f0, B:50:0x0198, B:66:0x01dc), top: B:94:0x0004 }] */
        /* JADX WARN: Removed duplicated region for block: B:47:0x018a A[Catch: NullPointerException -> 0x0197, IOException -> 0x01dc, all -> 0x0200, TRY_LEAVE, TryCatch #0 {all -> 0x0200, blocks: (B:5:0x0018, B:6:0x001f, B:8:0x0025, B:9:0x002b, B:13:0x003f, B:35:0x0103, B:41:0x0116, B:47:0x018a, B:51:0x01b4, B:42:0x011c, B:44:0x012d, B:45:0x014e, B:14:0x0046, B:16:0x0057, B:17:0x006d, B:19:0x0073, B:20:0x0091, B:21:0x0099, B:23:0x009f, B:25:0x00b0, B:27:0x00c9, B:29:0x00cf, B:30:0x00de, B:26:0x00c6, B:31:0x00e2, B:32:0x00ea, B:34:0x00f0, B:50:0x0198, B:66:0x01dc), top: B:94:0x0004 }] */
        /* JADX WARN: Removed duplicated region for block: B:99:0x0203 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public static void onWriteCalled(java.util.List<android.net.wifi.WifiConfiguration> r13) throws java.lang.Throwable {
            /*
                Method dump skipped, instruction units count: 544
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiConfigStore.DelayedDiskWrite.onWriteCalled(java.util.List):void");
        }

        private static void loge(String str) {
            Log.e(TAG, str);
        }
    }

    /* JADX INFO: renamed from: android.net.wifi.WifiConfigStore$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$net$NetworkInfo$DetailedState;
        static final /* synthetic */ int[] $SwitchMap$android$net$wifi$WifiConfiguration$IpAssignment;
        static final /* synthetic */ int[] $SwitchMap$android$net$wifi$WifiConfiguration$ProxySettings;

        static {
            int[] iArr = new int[WifiConfiguration.ProxySettings.values().length];
            $SwitchMap$android$net$wifi$WifiConfiguration$ProxySettings = iArr;
            try {
                iArr[WifiConfiguration.ProxySettings.STATIC.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$net$wifi$WifiConfiguration$ProxySettings[WifiConfiguration.ProxySettings.PAC.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$net$wifi$WifiConfiguration$ProxySettings[WifiConfiguration.ProxySettings.NONE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$android$net$wifi$WifiConfiguration$ProxySettings[WifiConfiguration.ProxySettings.UNASSIGNED.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            int[] iArr2 = new int[WifiConfiguration.IpAssignment.values().length];
            $SwitchMap$android$net$wifi$WifiConfiguration$IpAssignment = iArr2;
            try {
                iArr2[WifiConfiguration.IpAssignment.STATIC.ordinal()] = 1;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$android$net$wifi$WifiConfiguration$IpAssignment[WifiConfiguration.IpAssignment.DHCP.ordinal()] = 2;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$android$net$wifi$WifiConfiguration$IpAssignment[WifiConfiguration.IpAssignment.UNASSIGNED.ordinal()] = 3;
            } catch (NoSuchFieldError unused7) {
            }
            int[] iArr3 = new int[NetworkInfo.DetailedState.values().length];
            $SwitchMap$android$net$NetworkInfo$DetailedState = iArr3;
            try {
                iArr3[NetworkInfo.DetailedState.CONNECTED.ordinal()] = 1;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[NetworkInfo.DetailedState.DISCONNECTED.ordinal()] = 2;
            } catch (NoSuchFieldError unused9) {
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:110:0x021a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void readIpAndProxyConfigurations() throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 547
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiConfigStore.readIpAndProxyConfigurations():void");
    }

    /* JADX WARN: Removed duplicated region for block: B:99:0x0351  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private android.net.wifi.NetworkUpdateResult addOrUpdateNetworkNative(android.net.wifi.WifiConfiguration r13) {
        /*
            Method dump skipped, instruction units count: 1036
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiConfigStore.addOrUpdateNetworkNative(android.net.wifi.WifiConfiguration):android.net.wifi.NetworkUpdateResult");
    }

    /* JADX WARN: Removed duplicated region for block: B:47:0x00b1  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x0021  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private android.net.wifi.NetworkUpdateResult writeIpAndProxyConfigurationsOnChange(android.net.wifi.WifiConfiguration r13, android.net.wifi.WifiConfiguration r14) {
        /*
            Method dump skipped, instruction units count: 360
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiConfigStore.writeIpAndProxyConfigurationsOnChange(android.net.wifi.WifiConfiguration, android.net.wifi.WifiConfiguration):android.net.wifi.NetworkUpdateResult");
    }

    private LinkProperties copyIpSettingsFromConfig(WifiConfiguration wifiConfiguration) {
        LinkProperties linkProperties = new LinkProperties();
        linkProperties.setInterfaceName(wifiConfiguration.linkProperties.getInterfaceName());
        Iterator<LinkAddress> it = wifiConfiguration.linkProperties.getLinkAddresses().iterator();
        while (it.hasNext()) {
            linkProperties.addLinkAddress(it.next());
        }
        Iterator<RouteInfo> it2 = wifiConfiguration.linkProperties.getRoutes().iterator();
        while (it2.hasNext()) {
            linkProperties.addRoute(it2.next());
        }
        Iterator<InetAddress> it3 = wifiConfiguration.linkProperties.getDnses().iterator();
        while (it3.hasNext()) {
            linkProperties.addDns(it3.next());
        }
        return linkProperties;
    }

    private void readNetworkVariables(WifiConfiguration wifiConfiguration) {
        int i = wifiConfiguration.networkId;
        if (i < 0) {
            return;
        }
        String networkVariable = this.mWifiNative.getNetworkVariable(i, WifiConfiguration.ssidVarName);
        if (!TextUtils.isEmpty(networkVariable)) {
            if (networkVariable.charAt(0) != '\"') {
                wifiConfiguration.SSID = "\"" + WifiSsid.createFromHex(networkVariable).toString() + "\"";
            } else {
                wifiConfiguration.SSID = networkVariable;
            }
        } else {
            wifiConfiguration.SSID = null;
        }
        String networkVariable2 = this.mWifiNative.getNetworkVariable(i, "bssid");
        if (!TextUtils.isEmpty(networkVariable2)) {
            wifiConfiguration.BSSID = networkVariable2;
        } else {
            wifiConfiguration.BSSID = null;
        }
        String networkVariable3 = this.mWifiNative.getNetworkVariable(i, "priority");
        wifiConfiguration.priority = -1;
        if (!TextUtils.isEmpty(networkVariable3)) {
            try {
                wifiConfiguration.priority = Integer.parseInt(networkVariable3);
            } catch (NumberFormatException unused) {
            }
        }
        String networkVariable4 = this.mWifiNative.getNetworkVariable(i, WifiConfiguration.hiddenSSIDVarName);
        wifiConfiguration.hiddenSSID = false;
        if (!TextUtils.isEmpty(networkVariable4)) {
            try {
                wifiConfiguration.hiddenSSID = Integer.parseInt(networkVariable4) != 0;
            } catch (NumberFormatException unused2) {
            }
        }
        String networkVariable5 = this.mWifiNative.getNetworkVariable(i, WifiConfiguration.wepTxKeyIdxVarName);
        wifiConfiguration.wepTxKeyIndex = -1;
        if (!TextUtils.isEmpty(networkVariable5)) {
            try {
                wifiConfiguration.wepTxKeyIndex = Integer.parseInt(networkVariable5);
            } catch (NumberFormatException unused3) {
            }
        }
        for (int i2 = 0; i2 < 4; i2++) {
            String networkVariable6 = this.mWifiNative.getNetworkVariable(i, WifiConfiguration.wepKeyVarNames[i2]);
            if (!TextUtils.isEmpty(networkVariable6)) {
                wifiConfiguration.wepKeys[i2] = networkVariable6;
            } else {
                wifiConfiguration.wepKeys[i2] = null;
            }
        }
        String networkVariable7 = this.mWifiNative.getNetworkVariable(i, WifiConfiguration.pskVarName);
        if (!TextUtils.isEmpty(networkVariable7)) {
            wifiConfiguration.preSharedKey = networkVariable7;
        } else {
            wifiConfiguration.preSharedKey = null;
        }
        String networkVariable8 = this.mWifiNative.getNetworkVariable(wifiConfiguration.networkId, WifiConfiguration.Protocol.varName);
        if (!TextUtils.isEmpty(networkVariable8)) {
            for (String str : networkVariable8.split(" ")) {
                int iLookupString = lookupString(str, WifiConfiguration.Protocol.strings);
                if (iLookupString >= 0) {
                    wifiConfiguration.allowedProtocols.set(iLookupString);
                }
            }
        }
        String networkVariable9 = this.mWifiNative.getNetworkVariable(wifiConfiguration.networkId, WifiConfiguration.KeyMgmt.varName);
        if (!TextUtils.isEmpty(networkVariable9)) {
            for (String str2 : networkVariable9.split(" ")) {
                int iLookupString2 = lookupString(str2, WifiConfiguration.KeyMgmt.strings);
                if (iLookupString2 >= 0) {
                    wifiConfiguration.allowedKeyManagement.set(iLookupString2);
                }
            }
        }
        String networkVariable10 = this.mWifiNative.getNetworkVariable(wifiConfiguration.networkId, WifiConfiguration.AuthAlgorithm.varName);
        if (!TextUtils.isEmpty(networkVariable10)) {
            for (String str3 : networkVariable10.split(" ")) {
                int iLookupString3 = lookupString(str3, WifiConfiguration.AuthAlgorithm.strings);
                if (iLookupString3 >= 0) {
                    wifiConfiguration.allowedAuthAlgorithms.set(iLookupString3);
                }
            }
        }
        String networkVariable11 = this.mWifiNative.getNetworkVariable(wifiConfiguration.networkId, WifiConfiguration.PairwiseCipher.varName);
        if (!TextUtils.isEmpty(networkVariable11)) {
            for (String str4 : networkVariable11.split(" ")) {
                int iLookupString4 = lookupString(str4, WifiConfiguration.PairwiseCipher.strings);
                if (iLookupString4 >= 0) {
                    wifiConfiguration.allowedPairwiseCiphers.set(iLookupString4);
                }
            }
        }
        String networkVariable12 = this.mWifiNative.getNetworkVariable(wifiConfiguration.networkId, WifiConfiguration.GroupCipher.varName);
        if (!TextUtils.isEmpty(networkVariable12)) {
            for (String str5 : networkVariable12.split(" ")) {
                int iLookupString5 = lookupString(str5, WifiConfiguration.GroupCipher.strings);
                if (iLookupString5 >= 0) {
                    wifiConfiguration.allowedGroupCiphers.set(iLookupString5);
                }
            }
        }
        if (wifiConfiguration.enterpriseConfig == null) {
            wifiConfiguration.enterpriseConfig = new WifiEnterpriseConfig();
        }
        HashMap<String, String> fields = wifiConfiguration.enterpriseConfig.getFields();
        for (String str6 : WifiEnterpriseConfig.getSupplicantKeys()) {
            String networkVariable13 = this.mWifiNative.getNetworkVariable(i, str6);
            if (!TextUtils.isEmpty(networkVariable13)) {
                fields.put(str6, removeDoubleQuotes(networkVariable13));
            } else {
                fields.put(str6, "NULL");
            }
        }
        if (wifiConfiguration.enterpriseConfig.migrateOldEapTlsNative(this.mWifiNative, i)) {
            saveConfig();
        }
        wifiConfiguration.enterpriseConfig.migrateCerts(this.mKeyStore);
        wifiConfiguration.enterpriseConfig.initializeSoftwareKeystoreFlag(this.mKeyStore);
    }

    private String removeDoubleQuotes(String str) {
        int length = str.length();
        if (length <= 1 || str.charAt(0) != '\"') {
            return str;
        }
        int i = length - 1;
        return str.charAt(i) == '\"' ? str.substring(1, i) : str;
    }

    private String convertToQuotedString(String str) {
        return "\"" + str + "\"";
    }

    private String makeString(BitSet bitSet, String[] strArr) {
        StringBuffer stringBuffer = new StringBuffer();
        BitSet bitSet2 = bitSet.get(0, strArr.length);
        int iNextSetBit = -1;
        while (true) {
            iNextSetBit = bitSet2.nextSetBit(iNextSetBit + 1);
            if (iNextSetBit == -1) {
                break;
            }
            stringBuffer.append(strArr[iNextSetBit].replace('_', '-')).append(' ');
        }
        if (bitSet2.cardinality() > 0) {
            stringBuffer.setLength(stringBuffer.length() - 1);
        }
        return stringBuffer.toString();
    }

    private int lookupString(String str, String[] strArr) {
        int length = strArr.length;
        String strReplace = str.replace('-', '_');
        for (int i = 0; i < length; i++) {
            if (strReplace.equals(strArr[i])) {
                return i;
            }
        }
        loge("Failed to look-up a string: " + strReplace);
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int configKey(WifiConfiguration wifiConfiguration) {
        String str;
        if (wifiConfiguration.allowedKeyManagement.get(1)) {
            str = wifiConfiguration.SSID + WifiConfiguration.KeyMgmt.strings[1];
        } else if (wifiConfiguration.allowedKeyManagement.get(2) || wifiConfiguration.allowedKeyManagement.get(3)) {
            str = wifiConfiguration.SSID + WifiConfiguration.KeyMgmt.strings[2];
        } else if (wifiConfiguration.wepKeys[0] != null) {
            str = wifiConfiguration.SSID + "WEP";
        } else {
            str = wifiConfiguration.SSID + WifiConfiguration.KeyMgmt.strings[0];
        }
        return str.hashCode();
    }

    void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println(TAG);
        printWriter.println("mLastPriority " + this.mLastPriority);
        printWriter.println("Configured networks");
        Iterator<WifiConfiguration> it = getConfiguredNetworks().iterator();
        while (it.hasNext()) {
            printWriter.println(it.next());
        }
        printWriter.println();
        if (this.mLocalLog != null) {
            printWriter.println("WifiConfigStore - Log Begin ----");
            this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
            printWriter.println("WifiConfigStore - Log End ----");
        }
    }

    public String getConfigFile() {
        return ipConfigFile;
    }

    private void loge(String str) {
        Log.e(TAG, str);
    }

    private void log(String str) {
        Log.d(TAG, str);
    }

    private void localLog(String str) {
        LocalLog localLog = this.mLocalLog;
        if (localLog != null) {
            localLog.log(str);
        }
    }

    private void localLog(String str, int i) {
        WifiConfiguration wifiConfiguration;
        if (this.mLocalLog == null) {
            return;
        }
        synchronized (this.mConfiguredNetworks) {
            wifiConfiguration = this.mConfiguredNetworks.get(Integer.valueOf(i));
        }
        if (wifiConfiguration != null) {
            this.mLocalLog.log(str + " " + wifiConfiguration.getPrintableSsid());
        } else {
            this.mLocalLog.log(str + " " + i);
        }
    }
}
