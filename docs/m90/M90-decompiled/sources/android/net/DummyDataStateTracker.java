package android.net;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Messenger;
import android.util.Slog;

/* JADX INFO: loaded from: classes.dex */
public class DummyDataStateTracker extends BaseNetworkStateTracker {
    private static final boolean DBG = true;
    private static final String TAG = "DummyDataStateTracker";
    private static final boolean VDBG = false;
    private Handler mTarget;
    private boolean mTeardownRequested = false;
    private boolean mPrivateDnsRouteSet = false;
    private boolean mDefaultRouteSet = false;
    private boolean mIsDefaultOrHipri = false;

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckComplete() {
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckCompleted(boolean z) {
    }

    @Override // android.net.NetworkStateTracker
    public String getTcpBufferSizesPropName() {
        return BaseNetworkStateTracker.PROP_TCP_BUFFER_UNKNOWN;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isAvailable() {
        return true;
    }

    public void releaseWakeLock() {
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setDependencyMet(boolean z) {
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setPolicyDataEnable(boolean z) {
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean setRadio(boolean z) {
        return true;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setUserDataEnable(boolean z) {
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void supplyMessenger(Messenger messenger) {
    }

    public DummyDataStateTracker(int i, String str) {
        this.mNetworkInfo = new NetworkInfo(i);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void startMonitoring(Context context, Handler handler) {
        this.mTarget = handler;
        this.mContext = context;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isPrivateDnsRouteSet() {
        return this.mPrivateDnsRouteSet;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void privateDnsRouteSet(boolean z) {
        this.mPrivateDnsRouteSet = z;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public NetworkInfo getNetworkInfo() {
        return this.mNetworkInfo;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isDefaultRouteSet() {
        return this.mDefaultRouteSet;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void defaultRouteSet(boolean z) {
        this.mDefaultRouteSet = z;
    }

    @Override // android.net.NetworkStateTracker
    public boolean teardown() {
        setDetailedState(NetworkInfo.DetailedState.DISCONNECTING, "disabled", null);
        setDetailedState(NetworkInfo.DetailedState.DISCONNECTED, "disabled", null);
        return true;
    }

    private void setDetailedState(NetworkInfo.DetailedState detailedState, String str, String str2) {
        log("setDetailed state, old =" + this.mNetworkInfo.getDetailedState() + " and new state=" + detailedState);
        this.mNetworkInfo.setDetailedState(detailedState, str, str2);
        this.mTarget.obtainMessage(458752, this.mNetworkInfo).sendToTarget();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setTeardownRequested(boolean z) {
        this.mTeardownRequested = z;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isTeardownRequested() {
        return this.mTeardownRequested;
    }

    @Override // android.net.NetworkStateTracker
    public boolean reconnect() {
        setDetailedState(NetworkInfo.DetailedState.CONNECTING, "enabled", null);
        setDetailedState(NetworkInfo.DetailedState.CONNECTED, "enabled", null);
        setTeardownRequested(false);
        return true;
    }

    public String toString() {
        return new StringBuffer("Dummy data state: none, dummy!").toString();
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
    public void addStackedLink(LinkProperties linkProperties) {
        this.mLinkProperties.addStackedLink(linkProperties);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void removeStackedLink(LinkProperties linkProperties) {
        this.mLinkProperties.removeStackedLink(linkProperties);
    }

    private static void log(String str) {
        Slog.d(TAG, str);
    }

    private static void loge(String str) {
        Slog.e(TAG, str);
    }
}
