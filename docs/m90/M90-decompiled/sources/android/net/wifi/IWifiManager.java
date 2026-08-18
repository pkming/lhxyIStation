package android.net.wifi;

import android.net.DhcpInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Messenger;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.WorkSource;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface IWifiManager extends IInterface {
    void acquireMulticastLock(IBinder iBinder, String str) throws RemoteException;

    boolean acquireWifiLock(IBinder iBinder, int i, String str, WorkSource workSource) throws RemoteException;

    int addOrUpdateNetwork(WifiConfiguration wifiConfiguration) throws RemoteException;

    void addToBlacklist(String str) throws RemoteException;

    void captivePortalCheckComplete() throws RemoteException;

    void clearBlacklist() throws RemoteException;

    boolean disableNetwork(int i) throws RemoteException;

    void disconnect() throws RemoteException;

    boolean enableNetwork(int i, boolean z) throws RemoteException;

    void enableTdls(String str, boolean z) throws RemoteException;

    void enableTdlsWithMacAddress(String str, boolean z) throws RemoteException;

    List<BatchedScanResult> getBatchedScanResults(String str) throws RemoteException;

    String getConfigFile() throws RemoteException;

    List<WifiConfiguration> getConfiguredNetworks() throws RemoteException;

    WifiInfo getConnectionInfo() throws RemoteException;

    DhcpInfo getDhcpInfo() throws RemoteException;

    int getFrequencyBand() throws RemoteException;

    List<ScanResult> getScanResults(String str) throws RemoteException;

    WifiConfiguration getWifiApConfiguration() throws RemoteException;

    int getWifiApEnabledState() throws RemoteException;

    int getWifiEnabledState() throws RemoteException;

    Messenger getWifiServiceMessenger() throws RemoteException;

    Messenger getWifiStateMachineMessenger() throws RemoteException;

    void initializeMulticastFiltering() throws RemoteException;

    boolean isBatchedScanSupported() throws RemoteException;

    boolean isDualBandSupported() throws RemoteException;

    boolean isMulticastEnabled() throws RemoteException;

    boolean isScanAlwaysAvailable() throws RemoteException;

    boolean pingSupplicant() throws RemoteException;

    void pollBatchedScan() throws RemoteException;

    void reassociate() throws RemoteException;

    void reconnect() throws RemoteException;

    void releaseMulticastLock() throws RemoteException;

    boolean releaseWifiLock(IBinder iBinder) throws RemoteException;

    boolean removeNetwork(int i) throws RemoteException;

    boolean requestBatchedScan(BatchedScanSettings batchedScanSettings, IBinder iBinder, WorkSource workSource) throws RemoteException;

    boolean saveConfiguration() throws RemoteException;

    void setCountryCode(String str, boolean z) throws RemoteException;

    void setFrequencyBand(int i, boolean z) throws RemoteException;

    void setWifiApConfiguration(WifiConfiguration wifiConfiguration) throws RemoteException;

    void setWifiApEnabled(WifiConfiguration wifiConfiguration, boolean z) throws RemoteException;

    boolean setWifiEnabled(boolean z) throws RemoteException;

    void startScan(WorkSource workSource) throws RemoteException;

    void startWifi() throws RemoteException;

    void stopBatchedScan(BatchedScanSettings batchedScanSettings) throws RemoteException;

    void stopWifi() throws RemoteException;

    void updateWifiLockWorkSource(IBinder iBinder, WorkSource workSource) throws RemoteException;

    public static abstract class Stub extends Binder implements IWifiManager {
        private static final String DESCRIPTOR = "android.net.wifi.IWifiManager";
        static final int TRANSACTION_acquireMulticastLock = 27;
        static final int TRANSACTION_acquireWifiLock = 22;
        static final int TRANSACTION_addOrUpdateNetwork = 2;
        static final int TRANSACTION_addToBlacklist = 35;
        static final int TRANSACTION_captivePortalCheckComplete = 40;
        static final int TRANSACTION_clearBlacklist = 36;
        static final int TRANSACTION_disableNetwork = 5;
        static final int TRANSACTION_disconnect = 9;
        static final int TRANSACTION_enableNetwork = 4;
        static final int TRANSACTION_enableTdls = 41;
        static final int TRANSACTION_enableTdlsWithMacAddress = 42;
        static final int TRANSACTION_getBatchedScanResults = 45;
        static final int TRANSACTION_getConfigFile = 39;
        static final int TRANSACTION_getConfiguredNetworks = 1;
        static final int TRANSACTION_getConnectionInfo = 12;
        static final int TRANSACTION_getDhcpInfo = 20;
        static final int TRANSACTION_getFrequencyBand = 17;
        static final int TRANSACTION_getScanResults = 8;
        static final int TRANSACTION_getWifiApConfiguration = 31;
        static final int TRANSACTION_getWifiApEnabledState = 30;
        static final int TRANSACTION_getWifiEnabledState = 14;
        static final int TRANSACTION_getWifiServiceMessenger = 37;
        static final int TRANSACTION_getWifiStateMachineMessenger = 38;
        static final int TRANSACTION_initializeMulticastFiltering = 25;
        static final int TRANSACTION_isBatchedScanSupported = 46;
        static final int TRANSACTION_isDualBandSupported = 18;
        static final int TRANSACTION_isMulticastEnabled = 26;
        static final int TRANSACTION_isScanAlwaysAvailable = 21;
        static final int TRANSACTION_pingSupplicant = 6;
        static final int TRANSACTION_pollBatchedScan = 47;
        static final int TRANSACTION_reassociate = 11;
        static final int TRANSACTION_reconnect = 10;
        static final int TRANSACTION_releaseMulticastLock = 28;
        static final int TRANSACTION_releaseWifiLock = 24;
        static final int TRANSACTION_removeNetwork = 3;
        static final int TRANSACTION_requestBatchedScan = 43;
        static final int TRANSACTION_saveConfiguration = 19;
        static final int TRANSACTION_setCountryCode = 15;
        static final int TRANSACTION_setFrequencyBand = 16;
        static final int TRANSACTION_setWifiApConfiguration = 32;
        static final int TRANSACTION_setWifiApEnabled = 29;
        static final int TRANSACTION_setWifiEnabled = 13;
        static final int TRANSACTION_startScan = 7;
        static final int TRANSACTION_startWifi = 33;
        static final int TRANSACTION_stopBatchedScan = 44;
        static final int TRANSACTION_stopWifi = 34;
        static final int TRANSACTION_updateWifiLockWorkSource = 23;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWifiManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IWifiManager)) {
                return (IWifiManager) iInterfaceQueryLocalInterface;
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
                    List<WifiConfiguration> configuredNetworks = getConfiguredNetworks();
                    parcel2.writeNoException();
                    parcel2.writeTypedList(configuredNetworks);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iAddOrUpdateNetwork = addOrUpdateNetwork(parcel.readInt() != 0 ? WifiConfiguration.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(iAddOrUpdateNetwork);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zRemoveNetwork = removeNetwork(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zRemoveNetwork ? 1 : 0);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zEnableNetwork = enableNetwork(parcel.readInt(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(zEnableNetwork ? 1 : 0);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zDisableNetwork = disableNetwork(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zDisableNetwork ? 1 : 0);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zPingSupplicant = pingSupplicant();
                    parcel2.writeNoException();
                    parcel2.writeInt(zPingSupplicant ? 1 : 0);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    startScan(parcel.readInt() != 0 ? WorkSource.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<ScanResult> scanResults = getScanResults(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeTypedList(scanResults);
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    disconnect();
                    parcel2.writeNoException();
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    reconnect();
                    parcel2.writeNoException();
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    reassociate();
                    parcel2.writeNoException();
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    WifiInfo connectionInfo = getConnectionInfo();
                    parcel2.writeNoException();
                    if (connectionInfo != null) {
                        parcel2.writeInt(1);
                        connectionInfo.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean wifiEnabled = setWifiEnabled(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(wifiEnabled ? 1 : 0);
                    return true;
                case 14:
                    parcel.enforceInterface(DESCRIPTOR);
                    int wifiEnabledState = getWifiEnabledState();
                    parcel2.writeNoException();
                    parcel2.writeInt(wifiEnabledState);
                    return true;
                case 15:
                    parcel.enforceInterface(DESCRIPTOR);
                    setCountryCode(parcel.readString(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 16:
                    parcel.enforceInterface(DESCRIPTOR);
                    setFrequencyBand(parcel.readInt(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 17:
                    parcel.enforceInterface(DESCRIPTOR);
                    int frequencyBand = getFrequencyBand();
                    parcel2.writeNoException();
                    parcel2.writeInt(frequencyBand);
                    return true;
                case 18:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsDualBandSupported = isDualBandSupported();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsDualBandSupported ? 1 : 0);
                    return true;
                case 19:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zSaveConfiguration = saveConfiguration();
                    parcel2.writeNoException();
                    parcel2.writeInt(zSaveConfiguration ? 1 : 0);
                    return true;
                case 20:
                    parcel.enforceInterface(DESCRIPTOR);
                    DhcpInfo dhcpInfo = getDhcpInfo();
                    parcel2.writeNoException();
                    if (dhcpInfo != null) {
                        parcel2.writeInt(1);
                        dhcpInfo.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 21:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsScanAlwaysAvailable = isScanAlwaysAvailable();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsScanAlwaysAvailable ? 1 : 0);
                    return true;
                case 22:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zAcquireWifiLock = acquireWifiLock(parcel.readStrongBinder(), parcel.readInt(), parcel.readString(), parcel.readInt() != 0 ? WorkSource.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(zAcquireWifiLock ? 1 : 0);
                    return true;
                case 23:
                    parcel.enforceInterface(DESCRIPTOR);
                    updateWifiLockWorkSource(parcel.readStrongBinder(), parcel.readInt() != 0 ? WorkSource.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 24:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zReleaseWifiLock = releaseWifiLock(parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeInt(zReleaseWifiLock ? 1 : 0);
                    return true;
                case 25:
                    parcel.enforceInterface(DESCRIPTOR);
                    initializeMulticastFiltering();
                    parcel2.writeNoException();
                    return true;
                case 26:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsMulticastEnabled = isMulticastEnabled();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsMulticastEnabled ? 1 : 0);
                    return true;
                case 27:
                    parcel.enforceInterface(DESCRIPTOR);
                    acquireMulticastLock(parcel.readStrongBinder(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 28:
                    parcel.enforceInterface(DESCRIPTOR);
                    releaseMulticastLock();
                    parcel2.writeNoException();
                    return true;
                case 29:
                    parcel.enforceInterface(DESCRIPTOR);
                    setWifiApEnabled(parcel.readInt() != 0 ? WifiConfiguration.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 30:
                    parcel.enforceInterface(DESCRIPTOR);
                    int wifiApEnabledState = getWifiApEnabledState();
                    parcel2.writeNoException();
                    parcel2.writeInt(wifiApEnabledState);
                    return true;
                case 31:
                    parcel.enforceInterface(DESCRIPTOR);
                    WifiConfiguration wifiApConfiguration = getWifiApConfiguration();
                    parcel2.writeNoException();
                    if (wifiApConfiguration != null) {
                        parcel2.writeInt(1);
                        wifiApConfiguration.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 32:
                    parcel.enforceInterface(DESCRIPTOR);
                    setWifiApConfiguration(parcel.readInt() != 0 ? WifiConfiguration.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 33:
                    parcel.enforceInterface(DESCRIPTOR);
                    startWifi();
                    parcel2.writeNoException();
                    return true;
                case 34:
                    parcel.enforceInterface(DESCRIPTOR);
                    stopWifi();
                    parcel2.writeNoException();
                    return true;
                case 35:
                    parcel.enforceInterface(DESCRIPTOR);
                    addToBlacklist(parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 36:
                    parcel.enforceInterface(DESCRIPTOR);
                    clearBlacklist();
                    parcel2.writeNoException();
                    return true;
                case 37:
                    parcel.enforceInterface(DESCRIPTOR);
                    Messenger wifiServiceMessenger = getWifiServiceMessenger();
                    parcel2.writeNoException();
                    if (wifiServiceMessenger != null) {
                        parcel2.writeInt(1);
                        wifiServiceMessenger.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 38:
                    parcel.enforceInterface(DESCRIPTOR);
                    Messenger wifiStateMachineMessenger = getWifiStateMachineMessenger();
                    parcel2.writeNoException();
                    if (wifiStateMachineMessenger != null) {
                        parcel2.writeInt(1);
                        wifiStateMachineMessenger.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 39:
                    parcel.enforceInterface(DESCRIPTOR);
                    String configFile = getConfigFile();
                    parcel2.writeNoException();
                    parcel2.writeString(configFile);
                    return true;
                case 40:
                    parcel.enforceInterface(DESCRIPTOR);
                    captivePortalCheckComplete();
                    parcel2.writeNoException();
                    return true;
                case 41:
                    parcel.enforceInterface(DESCRIPTOR);
                    enableTdls(parcel.readString(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 42:
                    parcel.enforceInterface(DESCRIPTOR);
                    enableTdlsWithMacAddress(parcel.readString(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 43:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zRequestBatchedScan = requestBatchedScan(parcel.readInt() != 0 ? BatchedScanSettings.CREATOR.createFromParcel(parcel) : null, parcel.readStrongBinder(), parcel.readInt() != 0 ? WorkSource.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(zRequestBatchedScan ? 1 : 0);
                    return true;
                case 44:
                    parcel.enforceInterface(DESCRIPTOR);
                    stopBatchedScan(parcel.readInt() != 0 ? BatchedScanSettings.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 45:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<BatchedScanResult> batchedScanResults = getBatchedScanResults(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeTypedList(batchedScanResults);
                    return true;
                case 46:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsBatchedScanSupported = isBatchedScanSupported();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsBatchedScanSupported ? 1 : 0);
                    return true;
                case 47:
                    parcel.enforceInterface(DESCRIPTOR);
                    pollBatchedScan();
                    parcel2.writeNoException();
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements IWifiManager {
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

            @Override // android.net.wifi.IWifiManager
            public List<WifiConfiguration> getConfiguredNetworks() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createTypedArrayList(WifiConfiguration.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public int addOrUpdateNetwork(WifiConfiguration wifiConfiguration) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (wifiConfiguration != null) {
                        parcelObtain.writeInt(1);
                        wifiConfiguration.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean removeNetwork(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean enableNetwork(int i, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean disableNetwork(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean pingSupplicant() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void startScan(WorkSource workSource) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (workSource != null) {
                        parcelObtain.writeInt(1);
                        workSource.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(7, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public List<ScanResult> getScanResults(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(8, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createTypedArrayList(ScanResult.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void disconnect() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void reconnect() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void reassociate() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public WifiInfo getConnectionInfo() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? WifiInfo.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean setWifiEnabled(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(13, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public int getWifiEnabledState() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void setCountryCode(String str, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(15, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void setFrequencyBand(int i, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(16, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public int getFrequencyBand() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(17, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean isDualBandSupported() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean saveConfiguration() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public DhcpInfo getDhcpInfo() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? DhcpInfo.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean isScanAlwaysAvailable() throws RemoteException {
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

            @Override // android.net.wifi.IWifiManager
            public boolean acquireWifiLock(IBinder iBinder, int i, String str, WorkSource workSource) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str);
                    if (workSource != null) {
                        parcelObtain.writeInt(1);
                        workSource.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(22, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void updateWifiLockWorkSource(IBinder iBinder, WorkSource workSource) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    if (workSource != null) {
                        parcelObtain.writeInt(1);
                        workSource.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(23, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean releaseWifiLock(IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(24, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void initializeMulticastFiltering() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(25, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean isMulticastEnabled() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(26, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void acquireMulticastLock(IBinder iBinder, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(27, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void releaseMulticastLock() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(28, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void setWifiApEnabled(WifiConfiguration wifiConfiguration, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    int i = 1;
                    if (wifiConfiguration != null) {
                        parcelObtain.writeInt(1);
                        wifiConfiguration.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (!z) {
                        i = 0;
                    }
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(29, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public int getWifiApEnabledState() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(30, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public WifiConfiguration getWifiApConfiguration() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? WifiConfiguration.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void setWifiApConfiguration(WifiConfiguration wifiConfiguration) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (wifiConfiguration != null) {
                        parcelObtain.writeInt(1);
                        wifiConfiguration.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(32, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void startWifi() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(33, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void stopWifi() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(34, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void addToBlacklist(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(35, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void clearBlacklist() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(36, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public Messenger getWifiServiceMessenger() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(37, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? Messenger.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public Messenger getWifiStateMachineMessenger() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(38, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? Messenger.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public String getConfigFile() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(39, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void captivePortalCheckComplete() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(40, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void enableTdls(String str, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(41, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void enableTdlsWithMacAddress(String str, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(42, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean requestBatchedScan(BatchedScanSettings batchedScanSettings, IBinder iBinder, WorkSource workSource) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (batchedScanSettings != null) {
                        parcelObtain.writeInt(1);
                        batchedScanSettings.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(iBinder);
                    if (workSource != null) {
                        parcelObtain.writeInt(1);
                        workSource.writeToParcel(parcelObtain, 0);
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

            @Override // android.net.wifi.IWifiManager
            public void stopBatchedScan(BatchedScanSettings batchedScanSettings) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (batchedScanSettings != null) {
                        parcelObtain.writeInt(1);
                        batchedScanSettings.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(44, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public List<BatchedScanResult> getBatchedScanResults(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(45, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createTypedArrayList(BatchedScanResult.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean isBatchedScanSupported() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(46, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void pollBatchedScan() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(47, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}
