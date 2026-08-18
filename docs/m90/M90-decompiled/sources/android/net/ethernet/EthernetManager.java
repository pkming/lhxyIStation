package android.net.ethernet;

import android.content.ContentResolver;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.ethernet.IEthernetManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class EthernetManager {
    public static final String ETHERNET_CONNECT_MODE_DHCP = "dhcp";
    public static final String ETHERNET_CONNECT_MODE_MANUAL = "manual";
    public static final String ETHERNET_CONNECT_MODE_PPPOE = "pppoe";
    public static final int ETHERNET_DEVICE_SCAN_RESULT_READY = 0;
    public static final String ETHERNET_DISLINKED_ACTION = "android.net.ethernet.ETHERNET_STATE_CHANGE";
    public static final String ETHERNET_INTERFACE_CHANGED_ACTION = "android.net.ethernet.ETHERNET_STATE_CHANGE";
    public static final int ETHERNET_INTERFACT_ADDED = 6;
    public static final int ETHERNET_INTERFACT_REMOVED = 7;
    public static final String ETHERNET_LINKED_ACTION = "android.net.ethernet.ETHERNET_STATE_CHANGE";
    public static final String ETHERNET_STATE_CHANGED_ACTION = "android.net.ethernet.ETHERNET_STATE_CHANGE";
    public static final int ETHERNET_STATE_DISABLED = 0;
    public static final int ETHERNET_STATE_ENABLED = 1;
    public static final int ETHERNET_STATE_UNKNOWN = 2;
    public static final int EVENT_CONFIGURATION_FAILED = 0;
    public static final int EVENT_CONFIGURATION_SUCCEEDED = 1;
    public static final int EVENT_DEVREM = 6;
    public static final int EVENT_DHCP_CONNECT_FAILED = 11;
    public static final int EVENT_DHCP_CONNECT_SUCCESSED = 10;
    public static final int EVENT_DHCP_DISCONNECT_FAILED = 13;
    public static final int EVENT_DHCP_DISCONNECT_SUCCESSED = 12;
    public static final int EVENT_DISCONNECTED = 3;
    public static final int EVENT_ETHERNET_CONNECT_FAILED = 0;
    public static final int EVENT_ETHERNET_CONNECT_SUCCESSED = 1;
    public static final int EVENT_ETHERNET_DISABLED = 9;
    public static final int EVENT_ETHERNET_DISCONNECT_FAILED = 2;
    public static final int EVENT_ETHERNET_DISCONNECT_SUCCESSED = 3;
    public static final int EVENT_ETHERNET_ENABLED = 8;
    public static final int EVENT_INTERFACE_ADDED = 6;
    public static final int EVENT_INTERFACE_REMOVED = 7;
    public static final int EVENT_NEWDEV = 7;
    public static final int EVENT_PHY_LINK_DOWN = 19;
    public static final int EVENT_PHY_LINK_IN = 4;
    public static final int EVENT_PHY_LINK_OUT = 5;
    public static final int EVENT_PHY_LINK_UP = 18;
    public static final int EVENT_PPPOE_CONNECT_FAILED = 20;
    public static final int EVENT_PPPOE_CONNECT_SUCCESSED = 21;
    public static final int EVENT_PPPOE_DISCONNECT_FAILED = 22;
    public static final int EVENT_PPPOE_DISCONNECT_SUCCESSED = 23;
    public static final int EVENT_STATIC_CONNECT_FAILED = 15;
    public static final int EVENT_STATIC_CONNECT_SUCCESSED = 14;
    public static final int EVENT_STATIC_DISCONNECT_FAILED = 17;
    public static final int EVENT_STATIC_DISCONNECT_SUCCESSED = 16;
    public static final String EXTRA_ETHERNET_INFO = "ethernetInfo";
    public static final String EXTRA_ETHERNET_STATE = "ethernet_state";
    public static final String EXTRA_LINK_PROPERTIES = "linkProperties";
    public static final String EXTRA_NETWORK_INFO = "networkInfo";
    public static final String EXTRA_PPPOE_STATE = "ethernet_state";
    public static final String NETWORK_STATE_CHANGED_ACTION = "android.net.ethernet.STATE_CHANGE";
    public static final String PPPOE_STATE_CHANGED_ACTION = "android.net.ethernet.ETHERNET_STATE_CHANGE";
    public static final int PPPOE_STATE_DISABLED = 0;
    public static final int PPPOE_STATE_ENABLED = 1;
    public static final int PPPOE_STATE_EXIT = 4;
    public static final int PPPOE_STATE_STARTED = 2;
    public static final int PPPOE_STATE_STARTING = 1;
    public static final int PPPOE_STATE_STOPED = 0;
    public static final int PPPOE_STATE_UNKWON = 5;
    private static final String TAG = "EthernetManager";
    private static EthernetManager mEthManager;
    private Context mContext;
    IEthernetManager mService;

    public boolean getWifiDisguiseState() {
        return false;
    }

    public boolean isEthIfExist() {
        return true;
    }

    public void setEthIfExist(boolean z) {
    }

    public boolean setInterfaceName(String str) {
        return true;
    }

    public void setWifiDisguise(boolean z) {
    }

    public EthernetManager(Context context, IEthernetManager iEthernetManager) {
        this.mService = null;
        this.mContext = null;
        this.mContext = context;
        this.mService = iEthernetManager;
    }

    public boolean addInterfaceToService(String str) {
        try {
            return this.mService.addInterfaceToService(str);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public void removeInterfaceFromService(String str) {
        try {
            this.mService.removeInterfaceFromService(str);
        } catch (RemoteException unused) {
        }
    }

    public List<EthernetDevInfo> getDeviceList() {
        try {
            return this.mService.getDeviceList();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public EthernetDevInfo getDevInfo() {
        try {
            return this.mService.getDevInfo();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public String getEthernetMode() {
        try {
            return this.mService.getEthernetMode();
        } catch (RemoteException unused) {
            return ETHERNET_CONNECT_MODE_DHCP;
        }
    }

    public void setEthernetMode(String str, EthernetDevInfo ethernetDevInfo) {
        try {
            this.mService.setEthernetMode(str, ethernetDevInfo);
        } catch (RemoteException unused) {
            Log.e(TAG, "setEthernetMode error!");
        }
    }

    public void setEthernetEnabled(boolean z) {
        try {
            this.mService.setEthernetEnabled(z);
        } catch (RemoteException unused) {
            Log.e(TAG, "Cannot set new state.");
        }
    }

    public int getEthernetState() {
        try {
            return this.mService.getEthernetState();
        } catch (RemoteException unused) {
            return 2;
        }
    }

    public boolean getLinkState() {
        try {
            return this.mService.getLinkState();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public synchronized EthernetDevInfo getStaticConfig() {
        try {
        } catch (RemoteException unused) {
            Log.e(TAG, "Cannot get eth config.");
            return null;
        }
        return this.mService.getStaticConfig();
    }

    public synchronized void setStaticConfig(EthernetDevInfo ethernetDevInfo) {
        try {
            this.mService.setStaticConfig(ethernetDevInfo);
        } catch (RemoteException unused) {
            Log.e(TAG, "Set static config failed!");
        }
    }

    public EthernetDevInfo getLoginInfo(String str) {
        try {
            return this.mService.getLoginInfo(str);
        } catch (RemoteException unused) {
            Log.e(TAG, "getLoginInfo failed!");
            return new EthernetDevInfo();
        }
    }

    public void disconnect() {
        try {
            this.mService.disconnect();
        } catch (RemoteException unused) {
            Log.e(TAG, "disconnect failed!");
        }
    }

    public int getPppoeStatus() {
        try {
            return this.mService.getPppoeStatus();
        } catch (RemoteException unused) {
            Log.e(TAG, "get PPPoE State failed!");
            return 5;
        }
    }

    public static synchronized EthernetManager getInstance() {
        if (mEthManager == null) {
            mEthManager = new EthernetManager();
        }
        return mEthManager;
    }

    private EthernetManager() {
        this.mService = null;
        this.mContext = null;
        IBinder service = ServiceManager.getService(Context.ETHERNET_SERVICE);
        if (this.mService == null) {
            this.mService = IEthernetManager.Stub.asInterface(service);
        }
    }

    public boolean isConfigured() {
        return getDevInfo() != null;
    }

    public EthernetDevInfo getSavedConfig() {
        return getStaticConfig();
    }

    public DhcpInfo getDhcpInfo() {
        try {
            return this.mService.getDhcpInfo();
        } catch (RemoteException unused) {
            Log.e(TAG, "getDhcpInfo failed!");
            return null;
        }
    }

    public void updateDevInfo(EthernetDevInfo ethernetDevInfo) {
        setEthernetMode(ethernetDevInfo.getMode(), ethernetDevInfo);
    }

    public List<EthernetDevInfo> getDeviceNameList() {
        return getDeviceList();
    }

    public void setEnabled(boolean z) {
        setEthernetEnabled(z);
    }

    public int getState() {
        return getEthernetState();
    }

    public int getTotalInterface() {
        if (getDeviceList() == null) {
            return 0;
        }
        return getDeviceList().size();
    }

    public boolean isOn() {
        return getEthernetState() == 1;
    }

    public boolean isDhcp() {
        return getEthernetMode().equals(ETHERNET_CONNECT_MODE_DHCP);
    }

    public int checkLink(String str) {
        try {
            return this.mService.checkLink(str) ? 1 : 0;
        } catch (RemoteException unused) {
            return 0;
        }
    }

    public void setDefaultConf() {
        setEthernetMode(ETHERNET_CONNECT_MODE_DHCP, new DhcpInfo());
    }

    public int CheckLink(String str) {
        try {
            return this.mService.checkLink(str) ? 1 : 0;
        } catch (RemoteException unused) {
            return 0;
        }
    }

    public boolean isEthernetConfigured() {
        return isConfigured();
    }

    public void setEthernetMode(String str, DhcpInfo dhcpInfo) {
        Log.d(TAG, " setEthernetMode (" + str + ")");
        ContentResolver contentResolver = this.mContext.getContentResolver();
        long jClearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                if (str.equals(ETHERNET_CONNECT_MODE_DHCP)) {
                    disconnect();
                    Settings.Global.putInt(contentResolver, Settings.Global.ETHERNET_USE_PPPOE, 0);
                    Settings.Global.putString(contentResolver, Settings.Global.ETHERNET_MODE, str);
                } else if (str.equals(ETHERNET_CONNECT_MODE_MANUAL)) {
                    disconnect();
                    Settings.Global.putInt(contentResolver, Settings.Global.ETHERNET_USE_PPPOE, 0);
                    String strAddrToString = addrToString(dhcpInfo.ipAddress);
                    String strAddrToString2 = addrToString(dhcpInfo.gateway);
                    String strAddrToString3 = addrToString(dhcpInfo.netmask);
                    String strAddrToString4 = addrToString(dhcpInfo.dns1);
                    String strAddrToString5 = addrToString(dhcpInfo.dns2);
                    Log.i(TAG, "---ipAddr:" + strAddrToString);
                    Log.i(TAG, "---gwAddr:" + strAddrToString2);
                    Log.i(TAG, "---maskAddr:" + strAddrToString3);
                    Log.i(TAG, "---dns1Addr:" + strAddrToString4);
                    Log.i(TAG, "---dns2Addr:" + strAddrToString5);
                    EthernetDevInfo savedConfig = getSavedConfig();
                    savedConfig.setMode(ETHERNET_CONNECT_MODE_MANUAL);
                    savedConfig.setIpAddress(strAddrToString);
                    savedConfig.setGateWay(strAddrToString2);
                    savedConfig.setNetMask(strAddrToString3);
                    savedConfig.setDns1(strAddrToString4);
                    savedConfig.setDns2(strAddrToString5);
                    this.mService.setStaticConfig(savedConfig);
                }
            } catch (RemoteException unused) {
                Log.e(TAG, "setEthernetMode failed");
            }
        } finally {
            Binder.restoreCallingIdentity(jClearCallingIdentity);
        }
    }

    public String getInterfaceName() {
        Log.d(TAG, " getInterfaceName:");
        return getDevInfo().getIfName();
    }

    public boolean getNetLinkStatus() {
        return getNetLinkStatus(getInterfaceName()) > 0;
    }

    public int getNetLinkStatus(String str) {
        return CheckLink(str);
    }

    static String addrToString(int i) {
        return "" + (i & 255) + "." + ((i >> 8) & 255) + "." + ((i >> 16) & 255) + "." + ((i >> 24) & 255);
    }

    static int parseInetAddr(String str) {
        if (str == null || !str.matches("^\\d+(\\.\\d+){3}$")) {
            return 0;
        }
        int i = 0;
        int i2 = 0;
        for (String str2 : str.split("\\.")) {
            i |= (Integer.parseInt(str2) & 255) << (i2 * 8);
            i2++;
        }
        return i;
    }
}
