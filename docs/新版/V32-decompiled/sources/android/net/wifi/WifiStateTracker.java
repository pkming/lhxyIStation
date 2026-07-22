package android.net.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.BaseNetworkStateTracker;
import android.net.LinkCapabilities;
import android.net.LinkProperties;
import android.net.LinkQualityInfo;
import android.net.NetworkInfo;
import android.net.NetworkStateTracker;
import android.net.SamplingDataTracker;
import android.net.WifiLinkQualityInfo;
import android.os.Handler;
import android.os.Messenger;
import android.util.Slog;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes.dex */
public class WifiStateTracker extends BaseNetworkStateTracker {
    private static final boolean LOGV = true;
    private static final String NETWORKTYPE = "WIFI";
    private static final String TAG = "WifiStateTracker";
    private Handler mCsHandler;
    private WifiInfo mWifiInfo;
    private WifiManager mWifiManager;
    private BroadcastReceiver mWifiStateReceiver;
    private AtomicBoolean mTeardownRequested = new AtomicBoolean(false);
    private AtomicBoolean mPrivateDnsRouteSet = new AtomicBoolean(false);
    private AtomicBoolean mDefaultRouteSet = new AtomicBoolean(false);
    private NetworkInfo.State mLastState = NetworkInfo.State.UNKNOWN;
    private SamplingDataTracker mSamplingDataTracker = new SamplingDataTracker();

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
    public void setPolicyDataEnable(boolean z) {
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void supplyMessenger(Messenger messenger) {
    }

    public WifiStateTracker(int i, String str) {
        this.mNetworkInfo = new NetworkInfo(i, 0, str, "");
        this.mLinkProperties = new LinkProperties();
        this.mLinkCapabilities = new LinkCapabilities();
        this.mNetworkInfo.setIsAvailable(false);
        setTeardownRequested(false);
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
    public void startMonitoring(Context context, Handler handler) {
        this.mCsHandler = handler;
        this.mContext = context;
        this.mWifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
        this.mWifiStateReceiver = new WifiStateReceiver();
        this.mContext.registerReceiver(this.mWifiStateReceiver, intentFilter);
    }

    @Override // android.net.NetworkStateTracker
    public boolean teardown() {
        this.mTeardownRequested.set(true);
        this.mWifiManager.disconnect();
        return true;
    }

    @Override // android.net.NetworkStateTracker
    public boolean reconnect() {
        this.mTeardownRequested.set(false);
        this.mWifiManager.startWifi();
        return true;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckComplete() {
        this.mWifiManager.captivePortalCheckComplete();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean setRadio(boolean z) {
        this.mWifiManager.setWifiEnabled(z);
        return true;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isAvailable() {
        return this.mNetworkInfo.isAvailable();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setUserDataEnable(boolean z) {
        Slog.w(TAG, "ignoring setUserDataEnable(" + z + ")");
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
    public NetworkInfo getNetworkInfo() {
        return new NetworkInfo(this.mNetworkInfo);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public LinkProperties getLinkProperties() {
        return new LinkProperties(this.mLinkProperties);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public LinkCapabilities getLinkCapabilities() {
        return new LinkCapabilities(this.mLinkCapabilities);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public LinkQualityInfo getLinkQualityInfo() {
        if (this.mNetworkInfo == null) {
            return null;
        }
        WifiLinkQualityInfo wifiLinkQualityInfo = new WifiLinkQualityInfo();
        wifiLinkQualityInfo.setNetworkType(this.mNetworkInfo.getType());
        synchronized (this.mSamplingDataTracker.mSamplingDataLock) {
            this.mSamplingDataTracker.setCommonLinkQualityInfoFields(wifiLinkQualityInfo);
            wifiLinkQualityInfo.setTxGood(this.mSamplingDataTracker.getSampledTxPacketCount());
            wifiLinkQualityInfo.setTxBad(this.mSamplingDataTracker.getSampledTxPacketErrorCount());
        }
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null) {
            wifiLinkQualityInfo.setBssid(wifiInfo.getBSSID());
            int rssi = this.mWifiInfo.getRssi();
            wifiLinkQualityInfo.setRssi(rssi);
            wifiLinkQualityInfo.setNormalizedSignalStrength(WifiManager.calculateSignalLevel(rssi, 100));
        }
        return wifiLinkQualityInfo;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isDefaultRouteSet() {
        return this.mDefaultRouteSet.get();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void defaultRouteSet(boolean z) {
        this.mDefaultRouteSet.set(z);
    }

    private class WifiStateReceiver extends BroadcastReceiver {
        private WifiStateReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                WifiStateTracker.this.mNetworkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                WifiStateTracker.this.mLinkProperties = (LinkProperties) intent.getParcelableExtra("linkProperties");
                if (WifiStateTracker.this.mLinkProperties == null) {
                    WifiStateTracker.this.mLinkProperties = new LinkProperties();
                }
                WifiStateTracker.this.mLinkCapabilities = (LinkCapabilities) intent.getParcelableExtra(WifiManager.EXTRA_LINK_CAPABILITIES);
                if (WifiStateTracker.this.mLinkCapabilities == null) {
                    WifiStateTracker.this.mLinkCapabilities = new LinkCapabilities();
                }
                WifiStateTracker.this.mWifiInfo = (WifiInfo) intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                NetworkInfo.State state = WifiStateTracker.this.mNetworkInfo.getState();
                if (WifiStateTracker.this.mLastState != state || WifiStateTracker.this.mNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK) {
                    WifiStateTracker.this.mLastState = state;
                    WifiStateTracker.this.mSamplingDataTracker.resetSamplingData();
                    WifiStateTracker.this.mCsHandler.obtainMessage(458752, new NetworkInfo(WifiStateTracker.this.mNetworkInfo)).sendToTarget();
                    return;
                }
                return;
            }
            if (intent.getAction().equals(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION)) {
                WifiStateTracker.this.mLinkProperties = (LinkProperties) intent.getParcelableExtra("linkProperties");
                WifiStateTracker.this.mCsHandler.obtainMessage(NetworkStateTracker.EVENT_CONFIGURATION_CHANGED, WifiStateTracker.this.mNetworkInfo).sendToTarget();
            }
        }
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void addStackedLink(LinkProperties linkProperties) {
        this.mLinkProperties.addStackedLink(linkProperties);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void removeStackedLink(LinkProperties linkProperties) {
        this.mLinkProperties.removeStackedLink(linkProperties);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void startSampling(SamplingDataTracker.SamplingSnapshot samplingSnapshot) {
        this.mSamplingDataTracker.startSampling(samplingSnapshot);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void stopSampling(SamplingDataTracker.SamplingSnapshot samplingSnapshot) {
        this.mSamplingDataTracker.stopSampling(samplingSnapshot);
    }
}
