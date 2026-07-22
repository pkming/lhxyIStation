package android.net.wifi;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.text.TextUtils;
import android.util.LocalLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class WifiNative {
    static final int BLUETOOTH_COEXISTENCE_MODE_DISABLED = 1;
    static final int BLUETOOTH_COEXISTENCE_MODE_ENABLED = 0;
    static final int BLUETOOTH_COEXISTENCE_MODE_SENSE = 2;
    private static final boolean DBG = false;
    private static final int DEFAULT_GROUP_OWNER_INTENT = 6;
    static final int SCAN_WITHOUT_CONNECTION_SETUP = 1;
    static final int SCAN_WITH_CONNECTION_SETUP = 2;
    private int mCmdId;
    public final String mInterfaceName;
    public final String mInterfacePrefix;
    private boolean mSuspendOptEnabled = false;
    private final String mTAG;
    static final Object mLock = new Object();
    private static final LocalLog mLocalLog = new LocalLog(1024);

    private native void closeSupplicantConnectionNative();

    private native boolean connectToSupplicantNative();

    private native boolean doBooleanCommandNative(String str);

    private native int doIntCommandNative(String str);

    private native String doStringCommandNative(String str);

    public static native boolean isDriverLoaded();

    public static native boolean killSupplicant(boolean z);

    public static native boolean loadDriver();

    public static native boolean startSupplicant(boolean z);

    public static native boolean unloadDriver();

    private native String waitForEventNative();

    public WifiNative(String str) {
        this.mInterfaceName = str;
        this.mTAG = "WifiNative-" + str;
        if (!str.equals("p2p0")) {
            this.mInterfacePrefix = "IFNAME=" + str + " ";
        } else {
            this.mInterfacePrefix = "";
        }
    }

    public LocalLog getLocalLog() {
        return mLocalLog;
    }

    private int getNewCmdIdLocked() {
        int i = this.mCmdId;
        this.mCmdId = i + 1;
        return i;
    }

    private void localLog(String str) {
        LocalLog localLog = mLocalLog;
        if (localLog != null) {
            localLog.log(this.mInterfaceName + ": " + str);
        }
    }

    public boolean connectToSupplicant() {
        localLog(this.mInterfacePrefix + "connectToSupplicant");
        return connectToSupplicantNative();
    }

    public void closeSupplicantConnection() {
        localLog(this.mInterfacePrefix + "closeSupplicantConnection");
        closeSupplicantConnectionNative();
    }

    public String waitForEvent() {
        return waitForEventNative();
    }

    private boolean doBooleanCommand(String str) {
        boolean zDoBooleanCommandNative;
        synchronized (mLock) {
            int newCmdIdLocked = getNewCmdIdLocked();
            localLog(newCmdIdLocked + "->" + this.mInterfacePrefix + str);
            zDoBooleanCommandNative = doBooleanCommandNative(this.mInterfacePrefix + str);
            localLog(newCmdIdLocked + "<-" + zDoBooleanCommandNative);
        }
        return zDoBooleanCommandNative;
    }

    private int doIntCommand(String str) {
        int iDoIntCommandNative;
        synchronized (mLock) {
            int newCmdIdLocked = getNewCmdIdLocked();
            localLog(newCmdIdLocked + "->" + this.mInterfacePrefix + str);
            iDoIntCommandNative = doIntCommandNative(this.mInterfacePrefix + str);
            localLog(newCmdIdLocked + "<-" + iDoIntCommandNative);
        }
        return iDoIntCommandNative;
    }

    private String doStringCommand(String str) {
        String strDoStringCommandNative;
        synchronized (mLock) {
            int newCmdIdLocked = getNewCmdIdLocked();
            localLog(newCmdIdLocked + "->" + this.mInterfacePrefix + str);
            strDoStringCommandNative = doStringCommandNative(this.mInterfacePrefix + str);
            localLog(newCmdIdLocked + "<-" + strDoStringCommandNative);
        }
        return strDoStringCommandNative;
    }

    private String doStringCommandWithoutLogging(String str) {
        String strDoStringCommandNative;
        synchronized (mLock) {
            strDoStringCommandNative = doStringCommandNative(this.mInterfacePrefix + str);
        }
        return strDoStringCommandNative;
    }

    public boolean ping() {
        String strDoStringCommand = doStringCommand("PING");
        return strDoStringCommand != null && strDoStringCommand.equals("PONG");
    }

    public boolean scan(int i) {
        if (i == 1) {
            return doBooleanCommand("SCAN TYPE=ONLY");
        }
        if (i == 2) {
            return doBooleanCommand("SCAN");
        }
        throw new IllegalArgumentException("Invalid scan type");
    }

    public boolean stopSupplicant() {
        return doBooleanCommand("TERMINATE");
    }

    public String listNetworks() {
        return doStringCommand("LIST_NETWORKS");
    }

    public int addNetwork() {
        return doIntCommand("ADD_NETWORK");
    }

    public boolean setNetworkVariable(int i, String str, String str2) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return false;
        }
        return doBooleanCommand("SET_NETWORK " + i + " " + str + " " + str2);
    }

    public String getNetworkVariable(int i, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return doStringCommandWithoutLogging("GET_NETWORK " + i + " " + str);
    }

    public boolean removeNetwork(int i) {
        return doBooleanCommand("REMOVE_NETWORK " + i);
    }

    public boolean enableNetwork(int i, boolean z) {
        if (z) {
            return doBooleanCommand("SELECT_NETWORK " + i);
        }
        return doBooleanCommand("ENABLE_NETWORK " + i);
    }

    public boolean disableNetwork(int i) {
        return doBooleanCommand("DISABLE_NETWORK " + i);
    }

    public boolean reconnect() {
        return doBooleanCommand("RECONNECT");
    }

    public boolean reassociate() {
        return doBooleanCommand("REASSOCIATE");
    }

    public boolean disconnect() {
        return doBooleanCommand("DISCONNECT");
    }

    public String status() {
        return doStringCommand("STATUS");
    }

    public String getMacAddress() {
        String strDoStringCommand = doStringCommand("DRIVER MACADDR");
        if (TextUtils.isEmpty(strDoStringCommand)) {
            return null;
        }
        String[] strArrSplit = strDoStringCommand.split(" = ");
        if (strArrSplit.length == 2) {
            return strArrSplit[1];
        }
        return null;
    }

    public String scanResults(int i) {
        return doStringCommandWithoutLogging("BSS RANGE=" + i + "- MASK=0x21987");
    }

    public String setBatchedScanSettings(BatchedScanSettings batchedScanSettings) {
        if (batchedScanSettings == null) {
            return doStringCommand("DRIVER WLS_BATCHING STOP");
        }
        String str = ("DRIVER WLS_BATCHING SET SCANFREQ=" + batchedScanSettings.scanIntervalSec) + " MSCAN=" + batchedScanSettings.maxScansPerBatch;
        if (batchedScanSettings.maxApPerScan != Integer.MAX_VALUE) {
            str = str + " BESTN=" + batchedScanSettings.maxApPerScan;
        }
        if (batchedScanSettings.channelSet != null && !batchedScanSettings.channelSet.isEmpty()) {
            String str2 = str + " CHANNEL=<";
            int i = 0;
            Iterator<String> it = batchedScanSettings.channelSet.iterator();
            while (it.hasNext()) {
                str2 = str2 + (i > 0 ? "," : "") + it.next();
                i++;
            }
            str = str2 + ">";
        }
        if (batchedScanSettings.maxApForDistance != Integer.MAX_VALUE) {
            str = str + " RTT=" + batchedScanSettings.maxApForDistance;
        }
        return doStringCommand(str);
    }

    public String getBatchedScanResults() {
        return doStringCommand("DRIVER WLS_BATCHING GET");
    }

    public boolean startDriver() {
        return doBooleanCommand("DRIVER START");
    }

    public boolean stopDriver() {
        return doBooleanCommand("DRIVER STOP");
    }

    public boolean startFilteringMulticastV4Packets() {
        return doBooleanCommand("DRIVER RXFILTER-STOP") && doBooleanCommand("DRIVER RXFILTER-REMOVE 2") && doBooleanCommand("DRIVER RXFILTER-START");
    }

    public boolean stopFilteringMulticastV4Packets() {
        return doBooleanCommand("DRIVER RXFILTER-STOP") && doBooleanCommand("DRIVER RXFILTER-ADD 2") && doBooleanCommand("DRIVER RXFILTER-START");
    }

    public boolean startFilteringMulticastV6Packets() {
        return doBooleanCommand("DRIVER RXFILTER-STOP") && doBooleanCommand("DRIVER RXFILTER-REMOVE 3") && doBooleanCommand("DRIVER RXFILTER-START");
    }

    public boolean stopFilteringMulticastV6Packets() {
        return doBooleanCommand("DRIVER RXFILTER-STOP") && doBooleanCommand("DRIVER RXFILTER-ADD 3") && doBooleanCommand("DRIVER RXFILTER-START");
    }

    public int getBand() {
        String strDoStringCommand = doStringCommand("DRIVER GETBAND");
        if (!TextUtils.isEmpty(strDoStringCommand)) {
            String[] strArrSplit = strDoStringCommand.split(" ");
            try {
                if (strArrSplit.length == 2) {
                    return Integer.parseInt(strArrSplit[1]);
                }
            } catch (NumberFormatException unused) {
            }
        }
        return -1;
    }

    public boolean setBand(int i) {
        return doBooleanCommand("DRIVER SETBAND " + i);
    }

    public boolean setBluetoothCoexistenceMode(int i) {
        return doBooleanCommand("DRIVER BTCOEXMODE " + i);
    }

    public boolean setBluetoothCoexistenceScanMode(boolean z) {
        if (z) {
            return doBooleanCommand("DRIVER BTCOEXSCAN-START");
        }
        return doBooleanCommand("DRIVER BTCOEXSCAN-STOP");
    }

    public boolean saveConfig() {
        return doBooleanCommand("SAVE_CONFIG");
    }

    public boolean addToBlacklist(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return doBooleanCommand("BLACKLIST " + str);
    }

    public boolean clearBlacklist() {
        return doBooleanCommand("BLACKLIST clear");
    }

    public boolean setSuspendOptimizations(boolean z) {
        if (this.mSuspendOptEnabled == z) {
            return true;
        }
        this.mSuspendOptEnabled = z;
        if (z) {
            return doBooleanCommand("DRIVER SETSUSPENDMODE 1");
        }
        return doBooleanCommand("DRIVER SETSUSPENDMODE 0");
    }

    public boolean setCountryCode(String str) {
        return doBooleanCommand("DRIVER COUNTRY " + str.toUpperCase(Locale.ROOT));
    }

    public void enableBackgroundScan(boolean z) {
        if (z) {
            doBooleanCommand("SET pno 1");
        } else {
            doBooleanCommand("SET pno 0");
        }
    }

    public void setScanInterval(int i) {
        doBooleanCommand("SCAN_INTERVAL " + i);
    }

    public void startTdls(String str, boolean z) {
        if (z) {
            doBooleanCommand("TDLS_DISCOVER " + str);
            doBooleanCommand("TDLS_SETUP " + str);
        } else {
            doBooleanCommand("TDLS_TEARDOWN " + str);
        }
    }

    public String signalPoll() {
        return doStringCommandWithoutLogging("SIGNAL_POLL");
    }

    public String pktcntPoll() {
        return doStringCommand("PKTCNT_POLL");
    }

    public void bssFlush() {
        doBooleanCommand("BSS_FLUSH 0");
    }

    public boolean startWpsPbc(String str) {
        if (TextUtils.isEmpty(str)) {
            return doBooleanCommand("WPS_PBC");
        }
        return doBooleanCommand("WPS_PBC " + str);
    }

    public boolean startWpsPbc(String str, String str2) {
        synchronized (mLock) {
            if (TextUtils.isEmpty(str2)) {
                return doBooleanCommandNative("IFNAME=" + str + " WPS_PBC");
            }
            return doBooleanCommandNative("IFNAME=" + str + " WPS_PBC " + str2);
        }
    }

    public boolean startWpsPinKeypad(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return doBooleanCommand("WPS_PIN any " + str);
    }

    public boolean startWpsPinKeypad(String str, String str2) {
        boolean zDoBooleanCommandNative;
        if (TextUtils.isEmpty(str2)) {
            return false;
        }
        synchronized (mLock) {
            zDoBooleanCommandNative = doBooleanCommandNative("IFNAME=" + str + " WPS_PIN any " + str2);
        }
        return zDoBooleanCommandNative;
    }

    public String startWpsPinDisplay(String str) {
        if (TextUtils.isEmpty(str)) {
            return doStringCommand("WPS_PIN any");
        }
        return doStringCommand("WPS_PIN " + str);
    }

    public String startWpsPinDisplay(String str, String str2) {
        synchronized (mLock) {
            if (TextUtils.isEmpty(str2)) {
                return doStringCommandNative("IFNAME=" + str + " WPS_PIN any");
            }
            return doStringCommandNative("IFNAME=" + str + " WPS_PIN " + str2);
        }
    }

    public boolean startWpsRegistrar(String str, String str2) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return false;
        }
        return doBooleanCommand("WPS_REG " + str + " " + str2);
    }

    public boolean cancelWps() {
        return doBooleanCommand("WPS_CANCEL");
    }

    public boolean setPersistentReconnect(boolean z) {
        return doBooleanCommand("SET persistent_reconnect " + (!z ? 0 : 1));
    }

    public boolean setDeviceName(String str) {
        return doBooleanCommand("SET device_name " + str);
    }

    public boolean setDeviceType(String str) {
        return doBooleanCommand("SET device_type " + str);
    }

    public boolean setConfigMethods(String str) {
        return doBooleanCommand("SET config_methods " + str);
    }

    public boolean setManufacturer(String str) {
        return doBooleanCommand("SET manufacturer " + str);
    }

    public boolean setModelName(String str) {
        return doBooleanCommand("SET model_name " + str);
    }

    public boolean setModelNumber(String str) {
        return doBooleanCommand("SET model_number " + str);
    }

    public boolean setSerialNumber(String str) {
        return doBooleanCommand("SET serial_number " + str);
    }

    public boolean setP2pSsidPostfix(String str) {
        return doBooleanCommand("SET p2p_ssid_postfix " + str);
    }

    public boolean setP2pGroupIdle(String str, int i) {
        boolean zDoBooleanCommandNative;
        synchronized (mLock) {
            zDoBooleanCommandNative = doBooleanCommandNative("IFNAME=" + str + " SET p2p_group_idle " + i);
        }
        return zDoBooleanCommandNative;
    }

    public void setPowerSave(boolean z) {
        if (z) {
            doBooleanCommand("SET ps 1");
        } else {
            doBooleanCommand("SET ps 0");
        }
    }

    public boolean setP2pPowerSave(String str, boolean z) {
        synchronized (mLock) {
            if (z) {
                return doBooleanCommandNative("IFNAME=" + str + " P2P_SET ps 1");
            }
            return doBooleanCommandNative("IFNAME=" + str + " P2P_SET ps 0");
        }
    }

    public boolean setWfdEnable(boolean z) {
        return doBooleanCommand("SET wifi_display " + (z ? "1" : "0"));
    }

    public boolean setWfdDeviceInfo(String str) {
        return doBooleanCommand("WFD_SUBELEM_SET 0 " + str);
    }

    public boolean setConcurrencyPriority(String str) {
        return doBooleanCommand("P2P_SET conc_pref " + str);
    }

    public boolean p2pFind() {
        return doBooleanCommand("P2P_FIND");
    }

    public boolean p2pFind(int i) {
        if (i <= 0) {
            return p2pFind();
        }
        return doBooleanCommand("P2P_FIND " + i);
    }

    public boolean p2pStopFind() {
        return doBooleanCommand("P2P_STOP_FIND");
    }

    public boolean p2pListen() {
        return doBooleanCommand("P2P_LISTEN");
    }

    public boolean p2pListen(int i) {
        if (i <= 0) {
            return p2pListen();
        }
        return doBooleanCommand("P2P_LISTEN " + i);
    }

    public boolean p2pExtListen(boolean z, int i, int i2) {
        if (!z || i2 >= i) {
            return doBooleanCommand("P2P_EXT_LISTEN" + (z ? " " + i + " " + i2 : ""));
        }
        return false;
    }

    public boolean p2pSetChannel(int i, int i2) {
        if (i < 1 || i > 11) {
            if (i != 0) {
                return false;
            }
        } else if (!doBooleanCommand("P2P_SET listen_channel " + i)) {
            return false;
        }
        if (i2 >= 1 && i2 <= 165) {
            int i3 = (i2 <= 14 ? 2407 : 5000) + (i2 * 5);
            return doBooleanCommand("P2P_SET disallow_freq 1000-" + (i3 - 5) + "," + (i3 + 5) + "-6000");
        }
        if (i2 == 0) {
            return doBooleanCommand("P2P_SET disallow_freq \"\"");
        }
        return false;
    }

    public boolean p2pFlush() {
        return doBooleanCommand("P2P_FLUSH");
    }

    public String p2pConnect(WifiP2pConfig wifiP2pConfig, boolean z) {
        if (wifiP2pConfig == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        WpsInfo wpsInfo = wifiP2pConfig.wps;
        arrayList.add(wifiP2pConfig.deviceAddress);
        int i = wpsInfo.setup;
        if (i == 0) {
            arrayList.add("pbc");
        } else if (i == 1) {
            if (TextUtils.isEmpty(wpsInfo.pin)) {
                arrayList.add("pin");
            } else {
                arrayList.add(wpsInfo.pin);
            }
            arrayList.add(Context.DISPLAY_SERVICE);
        } else if (i == 2) {
            arrayList.add(wpsInfo.pin);
            arrayList.add("keypad");
        } else if (i == 3) {
            arrayList.add(wpsInfo.pin);
            arrayList.add("label");
        }
        if (wifiP2pConfig.netId == -2) {
            arrayList.add("persistent");
        }
        if (z) {
            arrayList.add("join");
        } else {
            int i2 = wifiP2pConfig.groupOwnerIntent;
            if (i2 < 0 || i2 > 15) {
                i2 = 6;
            }
            arrayList.add("go_intent=" + i2);
        }
        Iterator it = arrayList.iterator();
        String str = "P2P_CONNECT ";
        while (it.hasNext()) {
            str = str + ((String) it.next()) + " ";
        }
        return doStringCommand(str);
    }

    public boolean p2pCancelConnect() {
        return doBooleanCommand("P2P_CANCEL");
    }

    public boolean p2pProvisionDiscovery(WifiP2pConfig wifiP2pConfig) {
        if (wifiP2pConfig == null) {
            return false;
        }
        int i = wifiP2pConfig.wps.setup;
        if (i == 0) {
            return doBooleanCommand("P2P_PROV_DISC " + wifiP2pConfig.deviceAddress + " pbc");
        }
        if (i == 1) {
            return doBooleanCommand("P2P_PROV_DISC " + wifiP2pConfig.deviceAddress + " keypad");
        }
        if (i != 2) {
            return false;
        }
        return doBooleanCommand("P2P_PROV_DISC " + wifiP2pConfig.deviceAddress + " display");
    }

    public boolean p2pGroupAdd(boolean z) {
        if (z) {
            return doBooleanCommand("P2P_GROUP_ADD persistent");
        }
        return doBooleanCommand("P2P_GROUP_ADD");
    }

    public boolean p2pGroupAdd(int i) {
        return doBooleanCommand("P2P_GROUP_ADD persistent=" + i);
    }

    public boolean p2pGroupRemove(String str) {
        boolean zDoBooleanCommandNative;
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        synchronized (mLock) {
            zDoBooleanCommandNative = doBooleanCommandNative("IFNAME=" + str + " P2P_GROUP_REMOVE " + str);
        }
        return zDoBooleanCommandNative;
    }

    public boolean p2pReject(String str) {
        return doBooleanCommand("P2P_REJECT " + str);
    }

    public boolean p2pInvite(WifiP2pGroup wifiP2pGroup, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (wifiP2pGroup == null) {
            return doBooleanCommand("P2P_INVITE peer=" + str);
        }
        return doBooleanCommand("P2P_INVITE group=" + wifiP2pGroup.getInterface() + " peer=" + str + " go_dev_addr=" + wifiP2pGroup.getOwner().deviceAddress);
    }

    public boolean p2pReinvoke(int i, String str) {
        if (TextUtils.isEmpty(str) || i < 0) {
            return false;
        }
        return doBooleanCommand("P2P_INVITE persistent=" + i + " peer=" + str);
    }

    public String p2pGetSsid(String str) {
        return p2pGetParam(str, "oper_ssid");
    }

    public String p2pGetDeviceAddress() {
        String strStatus = status();
        if (strStatus == null) {
            return "";
        }
        String[] strArrSplit = strStatus.split("\n");
        int length = strArrSplit.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            String str = strArrSplit[i];
            if (str.startsWith("p2p_device_address=")) {
                String[] strArrSplit2 = str.split("=");
                if (strArrSplit2.length == 2) {
                    return strArrSplit2[1];
                }
            } else {
                i++;
            }
        }
        return "";
    }

    public int getGroupCapability(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        String strP2pPeer = p2pPeer(str);
        if (TextUtils.isEmpty(strP2pPeer)) {
            return 0;
        }
        String[] strArrSplit = strP2pPeer.split("\n");
        int length = strArrSplit.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            String str2 = strArrSplit[i];
            if (str2.startsWith("group_capab=")) {
                String[] strArrSplit2 = str2.split("=");
                if (strArrSplit2.length == 2) {
                    try {
                        return Integer.decode(strArrSplit2[1]).intValue();
                    } catch (NumberFormatException unused) {
                        return 0;
                    }
                }
            } else {
                i++;
            }
        }
        return 0;
    }

    public String p2pPeer(String str) {
        return doStringCommand("P2P_PEER " + str);
    }

    private String p2pGetParam(String str, String str2) {
        String strP2pPeer;
        if (str == null || (strP2pPeer = p2pPeer(str)) == null) {
            return null;
        }
        String[] strArrSplit = strP2pPeer.split("\n");
        String str3 = str2 + "=";
        int length = strArrSplit.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            String str4 = strArrSplit[i];
            if (str4.startsWith(str3)) {
                String[] strArrSplit2 = str4.split("=");
                if (strArrSplit2.length == 2) {
                    return strArrSplit2[1];
                }
            } else {
                i++;
            }
        }
        return null;
    }

    public boolean p2pServiceAdd(WifiP2pServiceInfo wifiP2pServiceInfo) {
        Iterator<String> it = wifiP2pServiceInfo.getSupplicantQueryList().iterator();
        while (it.hasNext()) {
            if (!doBooleanCommand("P2P_SERVICE_ADD " + it.next())) {
                return false;
            }
        }
        return true;
    }

    public boolean p2pServiceDel(WifiP2pServiceInfo wifiP2pServiceInfo) {
        String str;
        for (String str2 : wifiP2pServiceInfo.getSupplicantQueryList()) {
            String[] strArrSplit = str2.split(" ");
            if (strArrSplit.length < 2) {
                return false;
            }
            if ("upnp".equals(strArrSplit[0])) {
                str = "P2P_SERVICE_DEL " + str2;
            } else {
                if (!"bonjour".equals(strArrSplit[0])) {
                    return false;
                }
                str = ("P2P_SERVICE_DEL " + strArrSplit[0]) + " " + strArrSplit[1];
            }
            if (!doBooleanCommand(str)) {
                return false;
            }
        }
        return true;
    }

    public boolean p2pServiceFlush() {
        return doBooleanCommand("P2P_SERVICE_FLUSH");
    }

    public String p2pServDiscReq(String str, String str2) {
        return doStringCommand(("P2P_SERV_DISC_REQ " + str) + " " + str2);
    }

    public boolean p2pServDiscCancelReq(String str) {
        return doBooleanCommand("P2P_SERV_DISC_CANCEL_REQ " + str);
    }

    public void setMiracastMode(int i) {
        doBooleanCommand("DRIVER MIRACAST " + i);
    }
}
