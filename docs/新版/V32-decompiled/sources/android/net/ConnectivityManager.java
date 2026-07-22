package android.net;

import android.content.Context;
import android.os.Binder;
import android.os.Messenger;
import android.os.RemoteException;
import com.android.internal.util.Preconditions;
import java.net.InetAddress;

/* JADX INFO: loaded from: classes.dex */
public class ConnectivityManager {

    @Deprecated
    public static final String ACTION_BACKGROUND_DATA_SETTING_CHANGED = "android.net.conn.BACKGROUND_DATA_SETTING_CHANGED";
    public static final String ACTION_CAPTIVE_PORTAL_TEST_COMPLETED = "android.net.conn.CAPTIVE_PORTAL_TEST_COMPLETED";
    public static final String ACTION_DATA_ACTIVITY_CHANGE = "android.net.conn.DATA_ACTIVITY_CHANGE";
    public static final String ACTION_TETHER_STATE_CHANGED = "android.net.conn.TETHER_STATE_CHANGED";
    public static final String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String CONNECTIVITY_ACTION_IMMEDIATE = "android.net.conn.CONNECTIVITY_CHANGE_IMMEDIATE";
    public static final int CONNECTIVITY_CHANGE_DELAY_DEFAULT = 3000;

    @Deprecated
    public static final int DEFAULT_NETWORK_PREFERENCE = 1;
    public static final String EXTRA_ACTIVE_TETHER = "activeArray";
    public static final String EXTRA_AVAILABLE_TETHER = "availableArray";
    public static final String EXTRA_DEVICE_TYPE = "deviceType";
    public static final String EXTRA_ERRORED_TETHER = "erroredArray";
    public static final String EXTRA_EXTRA_INFO = "extraInfo";
    public static final String EXTRA_INET_CONDITION = "inetCondition";
    public static final String EXTRA_IS_ACTIVE = "isActive";
    public static final String EXTRA_IS_CAPTIVE_PORTAL = "captivePortal";
    public static final String EXTRA_IS_FAILOVER = "isFailover";

    @Deprecated
    public static final String EXTRA_NETWORK_INFO = "networkInfo";
    public static final String EXTRA_NETWORK_TYPE = "networkType";
    public static final String EXTRA_NO_CONNECTIVITY = "noConnectivity";
    public static final String EXTRA_OTHER_NETWORK_INFO = "otherNetwork";
    public static final String EXTRA_REASON = "reason";
    public static final String INET_CONDITION_ACTION = "android.net.conn.INET_CONDITION_ACTION";
    public static final int MAX_NETWORK_TYPE = 14;
    public static final int MAX_RADIO_TYPE = 14;
    private static final String TAG = "ConnectivityManager";
    public static final int TETHER_ERROR_DISABLE_NAT_ERROR = 9;
    public static final int TETHER_ERROR_ENABLE_NAT_ERROR = 8;
    public static final int TETHER_ERROR_IFACE_CFG_ERROR = 10;
    public static final int TETHER_ERROR_MASTER_ERROR = 5;
    public static final int TETHER_ERROR_NO_ERROR = 0;
    public static final int TETHER_ERROR_SERVICE_UNAVAIL = 2;
    public static final int TETHER_ERROR_TETHER_IFACE_ERROR = 6;
    public static final int TETHER_ERROR_UNAVAIL_IFACE = 4;
    public static final int TETHER_ERROR_UNKNOWN_IFACE = 1;
    public static final int TETHER_ERROR_UNSUPPORTED = 3;
    public static final int TETHER_ERROR_UNTETHER_IFACE_ERROR = 7;
    public static final int TYPE_BLUETOOTH = 7;
    public static final int TYPE_DUMMY = 8;
    public static final int TYPE_ETHERNET = 9;
    public static final int TYPE_MOBILE = 0;
    public static final int TYPE_MOBILE_CBS = 12;
    public static final int TYPE_MOBILE_DUN = 4;
    public static final int TYPE_MOBILE_FOTA = 10;
    public static final int TYPE_MOBILE_HIPRI = 5;
    public static final int TYPE_MOBILE_IA = 14;
    public static final int TYPE_MOBILE_IMS = 11;
    public static final int TYPE_MOBILE_MMS = 2;
    public static final int TYPE_MOBILE_SUPL = 3;
    public static final int TYPE_NONE = -1;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_WIFI_P2P = 13;
    public static final int TYPE_WIMAX = 6;
    private final IConnectivityManager mService;

    public static boolean isNetworkTypeExempt(int i) {
        return i == 2 || i == 3 || i == 5 || i == 14;
    }

    public static boolean isNetworkTypeMobile(int i) {
        if (i == 0 || i == 14 || i == 2 || i == 3 || i == 4 || i == 5) {
            return true;
        }
        switch (i) {
            case 10:
            case 11:
            case 12:
                return true;
            default:
                return false;
        }
    }

    public static boolean isNetworkTypeValid(int i) {
        return i >= 0 && i <= 14;
    }

    public static boolean isNetworkTypeWifi(int i) {
        return i == 1 || i == 13;
    }

    @Deprecated
    public boolean getBackgroundDataSetting() {
        return true;
    }

    @Deprecated
    public void setBackgroundDataSetting(boolean z) {
    }

    public static String getNetworkTypeName(int i) {
        switch (i) {
            case 0:
                return "MOBILE";
            case 1:
                return "WIFI";
            case 2:
                return "MOBILE_MMS";
            case 3:
                return "MOBILE_SUPL";
            case 4:
                return "MOBILE_DUN";
            case 5:
                return "MOBILE_HIPRI";
            case 6:
                return "WIMAX";
            case 7:
                return "BLUETOOTH";
            case 8:
                return "DUMMY";
            case 9:
                return "ETHERNET";
            case 10:
                return "MOBILE_FOTA";
            case 11:
                return "MOBILE_IMS";
            case 12:
                return "MOBILE_CBS";
            case 13:
                return "WIFI_P2P";
            case 14:
                return "MOBILE_IA";
            default:
                return Integer.toString(i);
        }
    }

    public void setNetworkPreference(int i) {
        try {
            this.mService.setNetworkPreference(i);
        } catch (RemoteException unused) {
        }
    }

    public int getNetworkPreference() {
        try {
            return this.mService.getNetworkPreference();
        } catch (RemoteException unused) {
            return -1;
        }
    }

    public NetworkInfo getActiveNetworkInfo() {
        try {
            return this.mService.getActiveNetworkInfo();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public NetworkInfo getActiveNetworkInfoForUid(int i) {
        try {
            return this.mService.getActiveNetworkInfoForUid(i);
        } catch (RemoteException unused) {
            return null;
        }
    }

    public NetworkInfo getNetworkInfo(int i) {
        try {
            return this.mService.getNetworkInfo(i);
        } catch (RemoteException unused) {
            return null;
        }
    }

    public NetworkInfo[] getAllNetworkInfo() {
        try {
            return this.mService.getAllNetworkInfo();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public NetworkInfo getProvisioningOrActiveNetworkInfo() {
        try {
            return this.mService.getProvisioningOrActiveNetworkInfo();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public LinkProperties getActiveLinkProperties() {
        try {
            return this.mService.getActiveLinkProperties();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public LinkProperties getLinkProperties(int i) {
        try {
            return this.mService.getLinkProperties(i);
        } catch (RemoteException unused) {
            return null;
        }
    }

    public boolean setRadios(boolean z) {
        try {
            return this.mService.setRadios(z);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean setRadio(int i, boolean z) {
        try {
            return this.mService.setRadio(i, z);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public int startUsingNetworkFeature(int i, String str) {
        try {
            return this.mService.startUsingNetworkFeature(i, str, new Binder());
        } catch (RemoteException unused) {
            return -1;
        }
    }

    public int stopUsingNetworkFeature(int i, String str) {
        try {
            return this.mService.stopUsingNetworkFeature(i, str);
        } catch (RemoteException unused) {
            return -1;
        }
    }

    public boolean requestRouteToHost(int i, int i2) {
        InetAddress inetAddressIntToInetAddress = NetworkUtils.intToInetAddress(i2);
        if (inetAddressIntToInetAddress == null) {
            return false;
        }
        return requestRouteToHostAddress(i, inetAddressIntToInetAddress);
    }

    public boolean requestRouteToHostAddress(int i, InetAddress inetAddress) {
        try {
            return this.mService.requestRouteToHostAddress(i, inetAddress.getAddress());
        } catch (RemoteException unused) {
            return false;
        }
    }

    public NetworkQuotaInfo getActiveNetworkQuotaInfo() {
        try {
            return this.mService.getActiveNetworkQuotaInfo();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public boolean getMobileDataEnabled() {
        try {
            return this.mService.getMobileDataEnabled();
        } catch (RemoteException unused) {
            return true;
        }
    }

    public void setMobileDataEnabled(boolean z) {
        try {
            this.mService.setMobileDataEnabled(z);
        } catch (RemoteException unused) {
        }
    }

    public ConnectivityManager(IConnectivityManager iConnectivityManager) {
        this.mService = (IConnectivityManager) Preconditions.checkNotNull(iConnectivityManager, "missing IConnectivityManager");
    }

    public static ConnectivityManager from(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public String[] getTetherableIfaces() {
        try {
            return this.mService.getTetherableIfaces();
        } catch (RemoteException unused) {
            return new String[0];
        }
    }

    public String[] getTetheredIfaces() {
        try {
            return this.mService.getTetheredIfaces();
        } catch (RemoteException unused) {
            return new String[0];
        }
    }

    public String[] getTetheringErroredIfaces() {
        try {
            return this.mService.getTetheringErroredIfaces();
        } catch (RemoteException unused) {
            return new String[0];
        }
    }

    public int tether(String str) {
        try {
            return this.mService.tether(str);
        } catch (RemoteException unused) {
            return 2;
        }
    }

    public int untether(String str) {
        try {
            return this.mService.untether(str);
        } catch (RemoteException unused) {
            return 2;
        }
    }

    public boolean isTetheringSupported() {
        try {
            return this.mService.isTetheringSupported();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public String[] getTetherableUsbRegexs() {
        try {
            return this.mService.getTetherableUsbRegexs();
        } catch (RemoteException unused) {
            return new String[0];
        }
    }

    public String[] getTetherableWifiRegexs() {
        try {
            return this.mService.getTetherableWifiRegexs();
        } catch (RemoteException unused) {
            return new String[0];
        }
    }

    public String[] getTetherableBluetoothRegexs() {
        try {
            return this.mService.getTetherableBluetoothRegexs();
        } catch (RemoteException unused) {
            return new String[0];
        }
    }

    public String[] getTetherableEthernetRegexs() {
        try {
            return this.mService.getTetherableEthernetRegexs();
        } catch (RemoteException unused) {
            return new String[0];
        }
    }

    public int setUsbTethering(boolean z) {
        try {
            return this.mService.setUsbTethering(z);
        } catch (RemoteException unused) {
            return 2;
        }
    }

    public int setEthernetTethering(boolean z) {
        try {
            return this.mService.setEthernetTethering(z);
        } catch (RemoteException unused) {
            return 2;
        }
    }

    public int getLastTetherError(String str) {
        try {
            return this.mService.getLastTetherError(str);
        } catch (RemoteException unused) {
            return 2;
        }
    }

    public boolean requestNetworkTransitionWakelock(String str) {
        try {
            this.mService.requestNetworkTransitionWakelock(str);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public void reportInetCondition(int i, int i2) {
        try {
            this.mService.reportInetCondition(i, i2);
        } catch (RemoteException unused) {
        }
    }

    public void setGlobalProxy(ProxyProperties proxyProperties) {
        try {
            this.mService.setGlobalProxy(proxyProperties);
        } catch (RemoteException unused) {
        }
    }

    public ProxyProperties getGlobalProxy() {
        try {
            return this.mService.getGlobalProxy();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public ProxyProperties getProxy() {
        try {
            return this.mService.getProxy();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public void setDataDependency(int i, boolean z) {
        try {
            this.mService.setDataDependency(i, z);
        } catch (RemoteException unused) {
        }
    }

    public boolean isNetworkSupported(int i) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        if (stackTraceElement != null && "com.google.android.setupwizard.BaseActivity".equals(stackTraceElement.getClassName()) && "isWifiOnlyBuild".equals(stackTraceElement.getMethodName())) {
            return false;
        }
        try {
            return this.mService.isNetworkSupported(i);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean isActiveNetworkMetered() {
        try {
            return this.mService.isActiveNetworkMetered();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean updateLockdownVpn() {
        try {
            return this.mService.updateLockdownVpn();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public void captivePortalCheckComplete(NetworkInfo networkInfo) {
        try {
            this.mService.captivePortalCheckComplete(networkInfo);
        } catch (RemoteException unused) {
        }
    }

    public void captivePortalCheckCompleted(NetworkInfo networkInfo, boolean z) {
        try {
            this.mService.captivePortalCheckCompleted(networkInfo, z);
        } catch (RemoteException unused) {
        }
    }

    public void supplyMessenger(int i, Messenger messenger) {
        try {
            this.mService.supplyMessenger(i, messenger);
        } catch (RemoteException unused) {
        }
    }

    public int checkMobileProvisioning(int i) {
        try {
            return this.mService.checkMobileProvisioning(i);
        } catch (RemoteException unused) {
            return -1;
        }
    }

    public String getMobileProvisioningUrl() {
        try {
            return this.mService.getMobileProvisioningUrl();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public String getMobileRedirectedProvisioningUrl() {
        try {
            return this.mService.getMobileRedirectedProvisioningUrl();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public LinkQualityInfo getLinkQualityInfo(int i) {
        try {
            return this.mService.getLinkQualityInfo(i);
        } catch (RemoteException unused) {
            return null;
        }
    }

    public LinkQualityInfo getActiveLinkQualityInfo() {
        try {
            return this.mService.getActiveLinkQualityInfo();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public LinkQualityInfo[] getAllLinkQualityInfo() {
        try {
            return this.mService.getAllLinkQualityInfo();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public void setProvisioningNotificationVisible(boolean z, int i, String str, String str2) {
        try {
            this.mService.setProvisioningNotificationVisible(z, i, str, str2);
        } catch (RemoteException unused) {
        }
    }

    public void setAirplaneMode(boolean z) {
        try {
            this.mService.setAirplaneMode(z);
        } catch (RemoteException unused) {
        }
    }
}
