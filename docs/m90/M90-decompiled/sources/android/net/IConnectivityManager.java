package android.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Messenger;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnProfile;

/* JADX INFO: loaded from: classes.dex */
public interface IConnectivityManager extends IInterface {
    void captivePortalCheckComplete(NetworkInfo networkInfo) throws RemoteException;

    void captivePortalCheckCompleted(NetworkInfo networkInfo, boolean z) throws RemoteException;

    int checkMobileProvisioning(int i) throws RemoteException;

    ParcelFileDescriptor establishVpn(VpnConfig vpnConfig) throws RemoteException;

    int findConnectionTypeForIface(String str) throws RemoteException;

    LinkProperties getActiveLinkProperties() throws RemoteException;

    LinkQualityInfo getActiveLinkQualityInfo() throws RemoteException;

    NetworkInfo getActiveNetworkInfo() throws RemoteException;

    NetworkInfo getActiveNetworkInfoForUid(int i) throws RemoteException;

    NetworkQuotaInfo getActiveNetworkQuotaInfo() throws RemoteException;

    LinkQualityInfo[] getAllLinkQualityInfo() throws RemoteException;

    NetworkInfo[] getAllNetworkInfo() throws RemoteException;

    NetworkState[] getAllNetworkState() throws RemoteException;

    ProxyProperties getGlobalProxy() throws RemoteException;

    int getLastTetherError(String str) throws RemoteException;

    LegacyVpnInfo getLegacyVpnInfo() throws RemoteException;

    LinkProperties getLinkProperties(int i) throws RemoteException;

    LinkQualityInfo getLinkQualityInfo(int i) throws RemoteException;

    boolean getMobileDataEnabled() throws RemoteException;

    String getMobileProvisioningUrl() throws RemoteException;

    String getMobileRedirectedProvisioningUrl() throws RemoteException;

    NetworkInfo getNetworkInfo(int i) throws RemoteException;

    int getNetworkPreference() throws RemoteException;

    NetworkInfo getProvisioningOrActiveNetworkInfo() throws RemoteException;

    ProxyProperties getProxy() throws RemoteException;

    String[] getTetherableBluetoothRegexs() throws RemoteException;

    String[] getTetherableEthernetRegexs() throws RemoteException;

    String[] getTetherableIfaces() throws RemoteException;

    String[] getTetherableUsbRegexs() throws RemoteException;

    String[] getTetherableWifiRegexs() throws RemoteException;

    String[] getTetheredIfaces() throws RemoteException;

    String[] getTetheringErroredIfaces() throws RemoteException;

    VpnConfig getVpnConfig() throws RemoteException;

    boolean isActiveNetworkMetered() throws RemoteException;

    boolean isNetworkSupported(int i) throws RemoteException;

    boolean isTetheringSupported() throws RemoteException;

    void markSocketAsUser(ParcelFileDescriptor parcelFileDescriptor, int i) throws RemoteException;

    boolean prepareVpn(String str, String str2) throws RemoteException;

    boolean protectVpn(ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    void reportInetCondition(int i, int i2) throws RemoteException;

    void requestNetworkTransitionWakelock(String str) throws RemoteException;

    boolean requestRouteToHost(int i, int i2) throws RemoteException;

    boolean requestRouteToHostAddress(int i, byte[] bArr) throws RemoteException;

    void setAirplaneMode(boolean z) throws RemoteException;

    void setDataDependency(int i, boolean z) throws RemoteException;

    int setEthernetTethering(boolean z) throws RemoteException;

    void setGlobalProxy(ProxyProperties proxyProperties) throws RemoteException;

    void setMobileDataEnabled(boolean z) throws RemoteException;

    void setNetworkPreference(int i) throws RemoteException;

    void setPolicyDataEnable(int i, boolean z) throws RemoteException;

    void setProvisioningNotificationVisible(boolean z, int i, String str, String str2) throws RemoteException;

    boolean setRadio(int i, boolean z) throws RemoteException;

    boolean setRadios(boolean z) throws RemoteException;

    int setUsbTethering(boolean z) throws RemoteException;

    void startLegacyVpn(VpnProfile vpnProfile) throws RemoteException;

    int startUsingNetworkFeature(int i, String str, IBinder iBinder) throws RemoteException;

    int stopUsingNetworkFeature(int i, String str) throws RemoteException;

    void supplyMessenger(int i, Messenger messenger) throws RemoteException;

    int tether(String str) throws RemoteException;

    int untether(String str) throws RemoteException;

    boolean updateLockdownVpn() throws RemoteException;

    public static abstract class Stub extends Binder implements IConnectivityManager {
        private static final String DESCRIPTOR = "android.net.IConnectivityManager";
        static final int TRANSACTION_captivePortalCheckComplete = 50;
        static final int TRANSACTION_captivePortalCheckCompleted = 51;
        static final int TRANSACTION_checkMobileProvisioning = 54;
        static final int TRANSACTION_establishVpn = 45;
        static final int TRANSACTION_findConnectionTypeForIface = 53;
        static final int TRANSACTION_getActiveLinkProperties = 10;
        static final int TRANSACTION_getActiveLinkQualityInfo = 58;
        static final int TRANSACTION_getActiveNetworkInfo = 4;
        static final int TRANSACTION_getActiveNetworkInfoForUid = 5;
        static final int TRANSACTION_getActiveNetworkQuotaInfo = 13;
        static final int TRANSACTION_getAllLinkQualityInfo = 59;
        static final int TRANSACTION_getAllNetworkInfo = 7;
        static final int TRANSACTION_getAllNetworkState = 12;
        static final int TRANSACTION_getGlobalProxy = 39;
        static final int TRANSACTION_getLastTetherError = 26;
        static final int TRANSACTION_getLegacyVpnInfo = 48;
        static final int TRANSACTION_getLinkProperties = 11;
        static final int TRANSACTION_getLinkQualityInfo = 57;
        static final int TRANSACTION_getMobileDataEnabled = 21;
        static final int TRANSACTION_getMobileProvisioningUrl = 55;
        static final int TRANSACTION_getMobileRedirectedProvisioningUrl = 56;
        static final int TRANSACTION_getNetworkInfo = 6;
        static final int TRANSACTION_getNetworkPreference = 3;
        static final int TRANSACTION_getProvisioningOrActiveNetworkInfo = 8;
        static final int TRANSACTION_getProxy = 41;
        static final int TRANSACTION_getTetherableBluetoothRegexs = 33;
        static final int TRANSACTION_getTetherableEthernetRegexs = 34;
        static final int TRANSACTION_getTetherableIfaces = 28;
        static final int TRANSACTION_getTetherableUsbRegexs = 31;
        static final int TRANSACTION_getTetherableWifiRegexs = 32;
        static final int TRANSACTION_getTetheredIfaces = 29;
        static final int TRANSACTION_getTetheringErroredIfaces = 30;
        static final int TRANSACTION_getVpnConfig = 46;
        static final int TRANSACTION_isActiveNetworkMetered = 14;
        static final int TRANSACTION_isNetworkSupported = 9;
        static final int TRANSACTION_isTetheringSupported = 27;
        static final int TRANSACTION_markSocketAsUser = 1;
        static final int TRANSACTION_prepareVpn = 44;
        static final int TRANSACTION_protectVpn = 43;
        static final int TRANSACTION_reportInetCondition = 38;
        static final int TRANSACTION_requestNetworkTransitionWakelock = 36;
        static final int TRANSACTION_requestRouteToHost = 19;
        static final int TRANSACTION_requestRouteToHostAddress = 20;
        static final int TRANSACTION_setAirplaneMode = 61;
        static final int TRANSACTION_setDataDependency = 42;
        static final int TRANSACTION_setEthernetTethering = 37;
        static final int TRANSACTION_setGlobalProxy = 40;
        static final int TRANSACTION_setMobileDataEnabled = 22;
        static final int TRANSACTION_setNetworkPreference = 2;
        static final int TRANSACTION_setPolicyDataEnable = 23;
        static final int TRANSACTION_setProvisioningNotificationVisible = 60;
        static final int TRANSACTION_setRadio = 16;
        static final int TRANSACTION_setRadios = 15;
        static final int TRANSACTION_setUsbTethering = 35;
        static final int TRANSACTION_startLegacyVpn = 47;
        static final int TRANSACTION_startUsingNetworkFeature = 17;
        static final int TRANSACTION_stopUsingNetworkFeature = 18;
        static final int TRANSACTION_supplyMessenger = 52;
        static final int TRANSACTION_tether = 24;
        static final int TRANSACTION_untether = 25;
        static final int TRANSACTION_updateLockdownVpn = 49;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IConnectivityManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IConnectivityManager)) {
                return (IConnectivityManager) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    markSocketAsUser(parcel.readInt() != 0 ? (ParcelFileDescriptor) ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    setNetworkPreference(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    int networkPreference = getNetworkPreference();
                    parcel2.writeNoException();
                    parcel2.writeInt(networkPreference);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    NetworkInfo activeNetworkInfo = getActiveNetworkInfo();
                    parcel2.writeNoException();
                    if (activeNetworkInfo != null) {
                        parcel2.writeInt(1);
                        activeNetworkInfo.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    NetworkInfo activeNetworkInfoForUid = getActiveNetworkInfoForUid(parcel.readInt());
                    parcel2.writeNoException();
                    if (activeNetworkInfoForUid != null) {
                        parcel2.writeInt(1);
                        activeNetworkInfoForUid.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    NetworkInfo networkInfo = getNetworkInfo(parcel.readInt());
                    parcel2.writeNoException();
                    if (networkInfo != null) {
                        parcel2.writeInt(1);
                        networkInfo.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    NetworkInfo[] allNetworkInfo = getAllNetworkInfo();
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(allNetworkInfo, 1);
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    NetworkInfo provisioningOrActiveNetworkInfo = getProvisioningOrActiveNetworkInfo();
                    parcel2.writeNoException();
                    if (provisioningOrActiveNetworkInfo != null) {
                        parcel2.writeInt(1);
                        provisioningOrActiveNetworkInfo.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsNetworkSupported = isNetworkSupported(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsNetworkSupported ? 1 : 0);
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    LinkProperties activeLinkProperties = getActiveLinkProperties();
                    parcel2.writeNoException();
                    if (activeLinkProperties != null) {
                        parcel2.writeInt(1);
                        activeLinkProperties.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    LinkProperties linkProperties = getLinkProperties(parcel.readInt());
                    parcel2.writeNoException();
                    if (linkProperties != null) {
                        parcel2.writeInt(1);
                        linkProperties.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    NetworkState[] allNetworkState = getAllNetworkState();
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(allNetworkState, 1);
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    NetworkQuotaInfo activeNetworkQuotaInfo = getActiveNetworkQuotaInfo();
                    parcel2.writeNoException();
                    if (activeNetworkQuotaInfo != null) {
                        parcel2.writeInt(1);
                        activeNetworkQuotaInfo.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 14:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsActiveNetworkMetered = isActiveNetworkMetered();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsActiveNetworkMetered ? 1 : 0);
                    return true;
                case 15:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean radios = setRadios(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(radios ? 1 : 0);
                    return true;
                case 16:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean radio = setRadio(parcel.readInt(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(radio ? 1 : 0);
                    return true;
                case 17:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iStartUsingNetworkFeature = startUsingNetworkFeature(parcel.readInt(), parcel.readString(), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeInt(iStartUsingNetworkFeature);
                    return true;
                case 18:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iStopUsingNetworkFeature = stopUsingNetworkFeature(parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iStopUsingNetworkFeature);
                    return true;
                case 19:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zRequestRouteToHost = requestRouteToHost(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zRequestRouteToHost ? 1 : 0);
                    return true;
                case 20:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zRequestRouteToHostAddress = requestRouteToHostAddress(parcel.readInt(), parcel.createByteArray());
                    parcel2.writeNoException();
                    parcel2.writeInt(zRequestRouteToHostAddress ? 1 : 0);
                    return true;
                case 21:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean mobileDataEnabled = getMobileDataEnabled();
                    parcel2.writeNoException();
                    parcel2.writeInt(mobileDataEnabled ? 1 : 0);
                    return true;
                case 22:
                    parcel.enforceInterface(DESCRIPTOR);
                    setMobileDataEnabled(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 23:
                    parcel.enforceInterface(DESCRIPTOR);
                    setPolicyDataEnable(parcel.readInt(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 24:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iTether = tether(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iTether);
                    return true;
                case 25:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iUntether = untether(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iUntether);
                    return true;
                case 26:
                    parcel.enforceInterface(DESCRIPTOR);
                    int lastTetherError = getLastTetherError(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(lastTetherError);
                    return true;
                case 27:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsTetheringSupported = isTetheringSupported();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsTetheringSupported ? 1 : 0);
                    return true;
                case 28:
                    parcel.enforceInterface(DESCRIPTOR);
                    String[] tetherableIfaces = getTetherableIfaces();
                    parcel2.writeNoException();
                    parcel2.writeStringArray(tetherableIfaces);
                    return true;
                case 29:
                    parcel.enforceInterface(DESCRIPTOR);
                    String[] tetheredIfaces = getTetheredIfaces();
                    parcel2.writeNoException();
                    parcel2.writeStringArray(tetheredIfaces);
                    return true;
                case 30:
                    parcel.enforceInterface(DESCRIPTOR);
                    String[] tetheringErroredIfaces = getTetheringErroredIfaces();
                    parcel2.writeNoException();
                    parcel2.writeStringArray(tetheringErroredIfaces);
                    return true;
                case 31:
                    parcel.enforceInterface(DESCRIPTOR);
                    String[] tetherableUsbRegexs = getTetherableUsbRegexs();
                    parcel2.writeNoException();
                    parcel2.writeStringArray(tetherableUsbRegexs);
                    return true;
                case 32:
                    parcel.enforceInterface(DESCRIPTOR);
                    String[] tetherableWifiRegexs = getTetherableWifiRegexs();
                    parcel2.writeNoException();
                    parcel2.writeStringArray(tetherableWifiRegexs);
                    return true;
                case 33:
                    parcel.enforceInterface(DESCRIPTOR);
                    String[] tetherableBluetoothRegexs = getTetherableBluetoothRegexs();
                    parcel2.writeNoException();
                    parcel2.writeStringArray(tetherableBluetoothRegexs);
                    return true;
                case 34:
                    parcel.enforceInterface(DESCRIPTOR);
                    String[] tetherableEthernetRegexs = getTetherableEthernetRegexs();
                    parcel2.writeNoException();
                    parcel2.writeStringArray(tetherableEthernetRegexs);
                    return true;
                case 35:
                    parcel.enforceInterface(DESCRIPTOR);
                    int usbTethering = setUsbTethering(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(usbTethering);
                    return true;
                case 36:
                    parcel.enforceInterface(DESCRIPTOR);
                    requestNetworkTransitionWakelock(parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 37:
                    parcel.enforceInterface(DESCRIPTOR);
                    int ethernetTethering = setEthernetTethering(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(ethernetTethering);
                    return true;
                case 38:
                    parcel.enforceInterface(DESCRIPTOR);
                    reportInetCondition(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 39:
                    parcel.enforceInterface(DESCRIPTOR);
                    ProxyProperties globalProxy = getGlobalProxy();
                    parcel2.writeNoException();
                    if (globalProxy != null) {
                        parcel2.writeInt(1);
                        globalProxy.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 40:
                    parcel.enforceInterface(DESCRIPTOR);
                    setGlobalProxy(parcel.readInt() != 0 ? (ProxyProperties) ProxyProperties.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 41:
                    parcel.enforceInterface(DESCRIPTOR);
                    ProxyProperties proxy = getProxy();
                    parcel2.writeNoException();
                    if (proxy != null) {
                        parcel2.writeInt(1);
                        proxy.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 42:
                    parcel.enforceInterface(DESCRIPTOR);
                    setDataDependency(parcel.readInt(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 43:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zProtectVpn = protectVpn(parcel.readInt() != 0 ? (ParcelFileDescriptor) ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(zProtectVpn ? 1 : 0);
                    return true;
                case 44:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zPrepareVpn = prepareVpn(parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(zPrepareVpn ? 1 : 0);
                    return true;
                case 45:
                    parcel.enforceInterface(DESCRIPTOR);
                    ParcelFileDescriptor parcelFileDescriptorEstablishVpn = establishVpn(parcel.readInt() != 0 ? (VpnConfig) VpnConfig.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    if (parcelFileDescriptorEstablishVpn != null) {
                        parcel2.writeInt(1);
                        parcelFileDescriptorEstablishVpn.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 46:
                    parcel.enforceInterface(DESCRIPTOR);
                    VpnConfig vpnConfig = getVpnConfig();
                    parcel2.writeNoException();
                    if (vpnConfig != null) {
                        parcel2.writeInt(1);
                        vpnConfig.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 47:
                    parcel.enforceInterface(DESCRIPTOR);
                    startLegacyVpn(parcel.readInt() != 0 ? (VpnProfile) VpnProfile.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 48:
                    parcel.enforceInterface(DESCRIPTOR);
                    LegacyVpnInfo legacyVpnInfo = getLegacyVpnInfo();
                    parcel2.writeNoException();
                    if (legacyVpnInfo != null) {
                        parcel2.writeInt(1);
                        legacyVpnInfo.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 49:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zUpdateLockdownVpn = updateLockdownVpn();
                    parcel2.writeNoException();
                    parcel2.writeInt(zUpdateLockdownVpn ? 1 : 0);
                    return true;
                case 50:
                    parcel.enforceInterface(DESCRIPTOR);
                    captivePortalCheckComplete(parcel.readInt() != 0 ? (NetworkInfo) NetworkInfo.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 51:
                    parcel.enforceInterface(DESCRIPTOR);
                    captivePortalCheckCompleted(parcel.readInt() != 0 ? (NetworkInfo) NetworkInfo.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 52:
                    parcel.enforceInterface(DESCRIPTOR);
                    supplyMessenger(parcel.readInt(), parcel.readInt() != 0 ? (Messenger) Messenger.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 53:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iFindConnectionTypeForIface = findConnectionTypeForIface(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iFindConnectionTypeForIface);
                    return true;
                case 54:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iCheckMobileProvisioning = checkMobileProvisioning(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(iCheckMobileProvisioning);
                    return true;
                case 55:
                    parcel.enforceInterface(DESCRIPTOR);
                    String mobileProvisioningUrl = getMobileProvisioningUrl();
                    parcel2.writeNoException();
                    parcel2.writeString(mobileProvisioningUrl);
                    return true;
                case 56:
                    parcel.enforceInterface(DESCRIPTOR);
                    String mobileRedirectedProvisioningUrl = getMobileRedirectedProvisioningUrl();
                    parcel2.writeNoException();
                    parcel2.writeString(mobileRedirectedProvisioningUrl);
                    return true;
                case 57:
                    parcel.enforceInterface(DESCRIPTOR);
                    LinkQualityInfo linkQualityInfo = getLinkQualityInfo(parcel.readInt());
                    parcel2.writeNoException();
                    if (linkQualityInfo != null) {
                        parcel2.writeInt(1);
                        linkQualityInfo.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 58:
                    parcel.enforceInterface(DESCRIPTOR);
                    LinkQualityInfo activeLinkQualityInfo = getActiveLinkQualityInfo();
                    parcel2.writeNoException();
                    if (activeLinkQualityInfo != null) {
                        parcel2.writeInt(1);
                        activeLinkQualityInfo.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 59:
                    parcel.enforceInterface(DESCRIPTOR);
                    LinkQualityInfo[] allLinkQualityInfo = getAllLinkQualityInfo();
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(allLinkQualityInfo, 1);
                    return true;
                case 60:
                    parcel.enforceInterface(DESCRIPTOR);
                    setProvisioningNotificationVisible(parcel.readInt() != 0, parcel.readInt(), parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 61:
                    parcel.enforceInterface(DESCRIPTOR);
                    setAirplaneMode(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements IConnectivityManager {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.net.IConnectivityManager
            public void markSocketAsUser(ParcelFileDescriptor parcelFileDescriptor, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (parcelFileDescriptor != null) {
                        parcelObtain.writeInt(1);
                        parcelFileDescriptor.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void setNetworkPreference(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public int getNetworkPreference() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public NetworkInfo getActiveNetworkInfo() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? NetworkInfo.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public NetworkInfo getActiveNetworkInfoForUid(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? NetworkInfo.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public NetworkInfo getNetworkInfo(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(6, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? NetworkInfo.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public NetworkInfo[] getAllNetworkInfo() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return (NetworkInfo[]) parcelObtain2.createTypedArray(NetworkInfo.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public NetworkInfo getProvisioningOrActiveNetworkInfo() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? NetworkInfo.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean isNetworkSupported(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(9, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public LinkProperties getActiveLinkProperties() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? LinkProperties.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public LinkProperties getLinkProperties(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(11, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? LinkProperties.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public NetworkState[] getAllNetworkState() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return (NetworkState[]) parcelObtain2.createTypedArray(NetworkState.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public NetworkQuotaInfo getActiveNetworkQuotaInfo() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? NetworkQuotaInfo.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean isActiveNetworkMetered() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean setRadios(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(15, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean setRadio(int i, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(16, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public int startUsingNetworkFeature(int i, String str, IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str);
                    parcelObtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(17, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public int stopUsingNetworkFeature(int i, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(18, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean requestRouteToHost(int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(19, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean requestRouteToHostAddress(int i, byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeByteArray(bArr);
                    this.mRemote.transact(20, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean getMobileDataEnabled() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(21, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void setMobileDataEnabled(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(22, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void setPolicyDataEnable(int i, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(23, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public int tether(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(24, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public int untether(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(25, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public int getLastTetherError(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(26, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean isTetheringSupported() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(27, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public String[] getTetherableIfaces() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(28, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createStringArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public String[] getTetheredIfaces() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(29, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createStringArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public String[] getTetheringErroredIfaces() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(30, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createStringArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public String[] getTetherableUsbRegexs() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createStringArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public String[] getTetherableWifiRegexs() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createStringArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public String[] getTetherableBluetoothRegexs() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(33, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createStringArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public String[] getTetherableEthernetRegexs() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(34, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createStringArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public int setUsbTethering(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(35, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void requestNetworkTransitionWakelock(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(36, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public int setEthernetTethering(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(37, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void reportInetCondition(int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(38, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public ProxyProperties getGlobalProxy() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(39, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? ProxyProperties.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void setGlobalProxy(ProxyProperties proxyProperties) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (proxyProperties != null) {
                        parcelObtain.writeInt(1);
                        proxyProperties.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(40, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public ProxyProperties getProxy() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(41, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? ProxyProperties.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void setDataDependency(int i, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(42, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean protectVpn(ParcelFileDescriptor parcelFileDescriptor) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (parcelFileDescriptor != null) {
                        parcelObtain.writeInt(1);
                        parcelFileDescriptor.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(43, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean prepareVpn(String str, String str2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    this.mRemote.transact(44, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public ParcelFileDescriptor establishVpn(VpnConfig vpnConfig) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (vpnConfig != null) {
                        parcelObtain.writeInt(1);
                        vpnConfig.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(45, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public VpnConfig getVpnConfig() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(46, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? (VpnConfig) VpnConfig.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void startLegacyVpn(VpnProfile vpnProfile) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (vpnProfile != null) {
                        parcelObtain.writeInt(1);
                        vpnProfile.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(47, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public LegacyVpnInfo getLegacyVpnInfo() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(48, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? (LegacyVpnInfo) LegacyVpnInfo.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean updateLockdownVpn() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(49, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void captivePortalCheckComplete(NetworkInfo networkInfo) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (networkInfo != null) {
                        parcelObtain.writeInt(1);
                        networkInfo.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(50, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void captivePortalCheckCompleted(NetworkInfo networkInfo, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    int i = 1;
                    if (networkInfo != null) {
                        parcelObtain.writeInt(1);
                        networkInfo.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (!z) {
                        i = 0;
                    }
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(51, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void supplyMessenger(int i, Messenger messenger) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (messenger != null) {
                        parcelObtain.writeInt(1);
                        messenger.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(52, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public int findConnectionTypeForIface(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(53, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public int checkMobileProvisioning(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(54, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public String getMobileProvisioningUrl() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(55, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public String getMobileRedirectedProvisioningUrl() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(56, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public LinkQualityInfo getLinkQualityInfo(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(57, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? LinkQualityInfo.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public LinkQualityInfo getActiveLinkQualityInfo() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(58, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? LinkQualityInfo.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public LinkQualityInfo[] getAllLinkQualityInfo() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(59, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return (LinkQualityInfo[]) parcelObtain2.createTypedArray(LinkQualityInfo.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void setProvisioningNotificationVisible(boolean z, int i, String str, String str2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    this.mRemote.transact(60, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void setAirplaneMode(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(61, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}
