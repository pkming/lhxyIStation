package android.net;

import android.content.Context;
import android.net.SamplingDataTracker;
import android.os.Handler;
import android.os.Messenger;

/* JADX INFO: loaded from: classes.dex */
public interface NetworkStateTracker {
    public static final int EVENT_CONFIGURATION_CHANGED = 458753;
    public static final int EVENT_NETWORK_CONNECTED = 458756;
    public static final int EVENT_NETWORK_DISCONNECTED = 458757;
    public static final int EVENT_NETWORK_SUBTYPE_CHANGED = 458755;
    public static final int EVENT_RESTORE_DEFAULT_NETWORK = 458754;
    public static final int EVENT_STATE_CHANGED = 458752;

    void addStackedLink(LinkProperties linkProperties);

    void captivePortalCheckComplete();

    void captivePortalCheckCompleted(boolean z);

    void defaultRouteSet(boolean z);

    LinkCapabilities getLinkCapabilities();

    LinkProperties getLinkProperties();

    LinkQualityInfo getLinkQualityInfo();

    NetworkInfo getNetworkInfo();

    String getNetworkInterfaceName();

    String getTcpBufferSizesPropName();

    boolean isAvailable();

    boolean isDefaultRouteSet();

    boolean isPrivateDnsRouteSet();

    boolean isTeardownRequested();

    void privateDnsRouteSet(boolean z);

    boolean reconnect();

    void removeStackedLink(LinkProperties linkProperties);

    void setDependencyMet(boolean z);

    void setPolicyDataEnable(boolean z);

    boolean setRadio(boolean z);

    void setTeardownRequested(boolean z);

    void setUserDataEnable(boolean z);

    void startMonitoring(Context context, Handler handler);

    void startSampling(SamplingDataTracker.SamplingSnapshot samplingSnapshot);

    void stopSampling(SamplingDataTracker.SamplingSnapshot samplingSnapshot);

    void supplyMessenger(Messenger messenger);

    boolean teardown();
}
