package android.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.SamplingDataTracker;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.util.AsyncChannel;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.net.telnet.TelnetCommand;

/* JADX INFO: loaded from: classes.dex */
public class MobileDataStateTracker extends BaseNetworkStateTracker {
    private static final boolean DBG = true;
    private static final String TAG = "MobileDataStateTracker";
    private static final int UNKNOWN = Integer.MAX_VALUE;
    private static final boolean VDBG = false;
    private static NetworkDataEntry[] mTheoreticalBWTable = {new NetworkDataEntry(2, TelnetCommand.SUSP, 118, Integer.MAX_VALUE), new NetworkDataEntry(1, 48, 40, Integer.MAX_VALUE), new NetworkDataEntry(3, 384, 64, Integer.MAX_VALUE), new NetworkDataEntry(8, 14400, Integer.MAX_VALUE, Integer.MAX_VALUE), new NetworkDataEntry(9, 14400, 5760, Integer.MAX_VALUE), new NetworkDataEntry(10, 14400, 5760, Integer.MAX_VALUE), new NetworkDataEntry(15, 21000, 5760, Integer.MAX_VALUE), new NetworkDataEntry(4, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE), new NetworkDataEntry(7, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE), new NetworkDataEntry(5, 2468, 153, Integer.MAX_VALUE), new NetworkDataEntry(6, 3072, 1800, Integer.MAX_VALUE), new NetworkDataEntry(12, 14700, 1800, Integer.MAX_VALUE), new NetworkDataEntry(11, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE), new NetworkDataEntry(13, 100000, 50000, Integer.MAX_VALUE), new NetworkDataEntry(14, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE)};
    private String mApnType;
    private Context mContext;
    private AsyncChannel mDataConnectionTrackerAc;
    private Handler mHandler;
    private LinkCapabilities mLinkCapabilities;
    private LinkProperties mLinkProperties;
    private PhoneConstants.DataState mMobileDataState;
    private NetworkInfo mNetworkInfo;
    private ITelephony mPhoneService;
    private SignalStrength mSignalStrength;
    private Handler mTarget;
    private boolean mTeardownRequested = false;
    private boolean mPrivateDnsRouteSet = false;
    private boolean mDefaultRouteSet = false;
    protected boolean mUserDataEnabled = true;
    protected boolean mPolicyDataEnabled = true;
    private AtomicBoolean mIsCaptivePortal = new AtomicBoolean(false);
    private SamplingDataTracker mSamplingDataTracker = new SamplingDataTracker();
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() { // from class: android.net.MobileDataStateTracker.1
        @Override // android.telephony.PhoneStateListener
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            MobileDataStateTracker.this.mSignalStrength = signalStrength;
        }
    };

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckComplete() {
    }

    public void releaseWakeLock() {
    }

    public MobileDataStateTracker(int i, String str) {
        this.mNetworkInfo = new NetworkInfo(i, TelephonyManager.getDefault().getNetworkType(), str, TelephonyManager.getDefault().getNetworkTypeName());
        this.mApnType = networkTypeToApnType(i);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void startMonitoring(Context context, Handler handler) {
        this.mTarget = handler;
        this.mContext = context;
        this.mHandler = new MdstHandler(handler.getLooper(), this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ANY_DATA_STATE");
        intentFilter.addAction("android.intent.action.DATA_CONNECTION_CONNECTED_TO_PROVISIONING_APN");
        intentFilter.addAction("android.intent.action.DATA_CONNECTION_FAILED");
        this.mContext.registerReceiver(new MobileDataStateReceiver(), intentFilter);
        this.mMobileDataState = PhoneConstants.DataState.DISCONNECTED;
        ((TelephonyManager) this.mContext.getSystemService("phone")).listen(this.mPhoneStateListener, 256);
    }

    static class MdstHandler extends Handler {
        private MobileDataStateTracker mMdst;

        MdstHandler(Looper looper, MobileDataStateTracker mobileDataStateTracker) {
            super(looper);
            this.mMdst = mobileDataStateTracker;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i != 69632) {
                if (i != 69636) {
                    return;
                }
                this.mMdst.mDataConnectionTrackerAc = null;
            } else if (message.arg1 == 0) {
                this.mMdst.mDataConnectionTrackerAc = (AsyncChannel) message.obj;
            }
        }
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

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLinkProperitesAndCapatilities(Intent intent) {
        LinkProperties linkProperties = (LinkProperties) intent.getParcelableExtra("linkProperties");
        this.mLinkProperties = linkProperties;
        if (linkProperties == null) {
            loge("CONNECTED event did not supply link properties.");
            this.mLinkProperties = new LinkProperties();
        }
        this.mLinkProperties.setMtu(this.mContext.getResources().getInteger(17694789));
        LinkCapabilities linkCapabilities = (LinkCapabilities) intent.getParcelableExtra(WifiManager.EXTRA_LINK_CAPABILITIES);
        this.mLinkCapabilities = linkCapabilities;
        if (linkCapabilities == null) {
            loge("CONNECTED event did not supply link capabilities.");
            this.mLinkCapabilities = new LinkCapabilities();
        }
    }

    private class MobileDataStateReceiver extends BroadcastReceiver {
        private MobileDataStateReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.DATA_CONNECTION_CONNECTED_TO_PROVISIONING_APN")) {
                String stringExtra = intent.getStringExtra("apn");
                String stringExtra2 = intent.getStringExtra("apnType");
                if (TextUtils.equals(MobileDataStateTracker.this.mApnType, stringExtra2)) {
                    MobileDataStateTracker.this.log("Broadcast received: " + intent.getAction() + " apnType=" + stringExtra2 + " apnName=" + stringExtra);
                    MobileDataStateTracker.this.mMobileDataState = PhoneConstants.DataState.CONNECTING;
                    MobileDataStateTracker.this.updateLinkProperitesAndCapatilities(intent);
                    MobileDataStateTracker.this.mNetworkInfo.setIsConnectedToProvisioningNetwork(true);
                    MobileDataStateTracker.this.setDetailedState(NetworkInfo.DetailedState.SUSPENDED, "", stringExtra);
                    return;
                }
                return;
            }
            if (intent.getAction().equals("android.intent.action.ANY_DATA_STATE")) {
                String stringExtra3 = intent.getStringExtra("apnType");
                if (TextUtils.equals(stringExtra3, MobileDataStateTracker.this.mApnType)) {
                    MobileDataStateTracker.this.mNetworkInfo.setIsConnectedToProvisioningNetwork(false);
                    MobileDataStateTracker.this.log("Broadcast received: " + intent.getAction() + " apnType=" + stringExtra3);
                    int subtype = MobileDataStateTracker.this.mNetworkInfo.getSubtype();
                    int networkType = TelephonyManager.getDefault().getNetworkType();
                    MobileDataStateTracker.this.mNetworkInfo.setSubtype(networkType, TelephonyManager.getDefault().getNetworkTypeName());
                    if (networkType != subtype && MobileDataStateTracker.this.mNetworkInfo.isConnected()) {
                        MobileDataStateTracker.this.mTarget.obtainMessage(NetworkStateTracker.EVENT_NETWORK_SUBTYPE_CHANGED, subtype, 0, MobileDataStateTracker.this.mNetworkInfo).sendToTarget();
                    }
                    PhoneConstants.DataState dataStateValueOf = Enum.valueOf(PhoneConstants.DataState.class, intent.getStringExtra("state"));
                    String stringExtra4 = intent.getStringExtra("reason");
                    String stringExtra5 = intent.getStringExtra("apn");
                    MobileDataStateTracker.this.mNetworkInfo.setRoaming(intent.getBooleanExtra("networkRoaming", false));
                    MobileDataStateTracker.this.mNetworkInfo.setIsAvailable(!intent.getBooleanExtra("networkUnvailable", false));
                    MobileDataStateTracker.this.log("Received state=" + dataStateValueOf + ", old=" + MobileDataStateTracker.this.mMobileDataState + ", reason=" + (stringExtra4 == null ? "(unspecified)" : stringExtra4));
                    if (MobileDataStateTracker.this.mMobileDataState != dataStateValueOf) {
                        MobileDataStateTracker.this.mMobileDataState = dataStateValueOf;
                        int i = AnonymousClass2.$SwitchMap$com$android$internal$telephony$PhoneConstants$DataState[dataStateValueOf.ordinal()];
                        if (i == 1) {
                            if (MobileDataStateTracker.this.isTeardownRequested()) {
                                MobileDataStateTracker.this.setTeardownRequested(false);
                            }
                            MobileDataStateTracker.this.setDetailedState(NetworkInfo.DetailedState.DISCONNECTED, stringExtra4, stringExtra5);
                        } else if (i == 2) {
                            MobileDataStateTracker.this.setDetailedState(NetworkInfo.DetailedState.CONNECTING, stringExtra4, stringExtra5);
                        } else if (i == 3) {
                            MobileDataStateTracker.this.setDetailedState(NetworkInfo.DetailedState.SUSPENDED, stringExtra4, stringExtra5);
                        } else if (i == 4) {
                            MobileDataStateTracker.this.updateLinkProperitesAndCapatilities(intent);
                            MobileDataStateTracker.this.setDetailedState(NetworkInfo.DetailedState.CONNECTED, stringExtra4, stringExtra5);
                        }
                        MobileDataStateTracker.this.mSamplingDataTracker.resetSamplingData();
                        return;
                    }
                    if (TextUtils.equals(stringExtra4, "linkPropertiesChanged")) {
                        MobileDataStateTracker.this.mLinkProperties = (LinkProperties) intent.getParcelableExtra("linkProperties");
                        if (MobileDataStateTracker.this.mLinkProperties == null) {
                            MobileDataStateTracker.this.loge("No link property in LINK_PROPERTIES change event.");
                            MobileDataStateTracker.this.mLinkProperties = new LinkProperties();
                        }
                        MobileDataStateTracker.this.mNetworkInfo.setDetailedState(MobileDataStateTracker.this.mNetworkInfo.getDetailedState(), stringExtra4, MobileDataStateTracker.this.mNetworkInfo.getExtraInfo());
                        MobileDataStateTracker.this.mTarget.obtainMessage(NetworkStateTracker.EVENT_CONFIGURATION_CHANGED, MobileDataStateTracker.this.mNetworkInfo).sendToTarget();
                        return;
                    }
                    return;
                }
                return;
            }
            if (!intent.getAction().equals("android.intent.action.DATA_CONNECTION_FAILED")) {
                MobileDataStateTracker.this.log("Broadcast received: ignore " + intent.getAction());
                return;
            }
            String stringExtra6 = intent.getStringExtra("apnType");
            if (TextUtils.equals(stringExtra6, MobileDataStateTracker.this.mApnType)) {
                MobileDataStateTracker.this.mNetworkInfo.setIsConnectedToProvisioningNetwork(false);
                String stringExtra7 = intent.getStringExtra("reason");
                String stringExtra8 = intent.getStringExtra("apn");
                MobileDataStateTracker.this.log(new StringBuilder().append("Broadcast received: ").append(intent.getAction()).append(" reason=").append(stringExtra7).toString() == null ? "null" : stringExtra7);
                MobileDataStateTracker.this.setDetailedState(NetworkInfo.DetailedState.FAILED, stringExtra7, stringExtra8);
                return;
            }
            MobileDataStateTracker mobileDataStateTracker = MobileDataStateTracker.this;
            mobileDataStateTracker.log(String.format("Broadcast received: ACTION_ANY_DATA_CONNECTION_FAILED ignore, mApnType=%s != received apnType=%s", mobileDataStateTracker.mApnType, stringExtra6));
        }
    }

    /* JADX INFO: renamed from: android.net.MobileDataStateTracker$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$PhoneConstants$DataState;

        static {
            int[] iArr = new int[PhoneConstants.DataState.values().length];
            $SwitchMap$com$android$internal$telephony$PhoneConstants$DataState = iArr;
            try {
                iArr[PhoneConstants.DataState.DISCONNECTED.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$PhoneConstants$DataState[PhoneConstants.DataState.CONNECTING.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$PhoneConstants$DataState[PhoneConstants.DataState.SUSPENDED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$PhoneConstants$DataState[PhoneConstants.DataState.CONNECTED.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    private void getPhoneService(boolean z) {
        if (this.mPhoneService == null || z) {
            this.mPhoneService = ITelephony.Stub.asInterface(ServiceManager.getService("phone"));
        }
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isAvailable() {
        return this.mNetworkInfo.isAvailable();
    }

    @Override // android.net.NetworkStateTracker
    public String getTcpBufferSizesPropName() {
        TelephonyManager telephonyManager = new TelephonyManager(this.mContext);
        String str = "evdo";
        switch (telephonyManager.getNetworkType()) {
            case 1:
                str = "gprs";
                break;
            case 2:
                str = "edge";
                break;
            case 3:
                str = "umts";
                break;
            case 4:
                str = "cdma";
                break;
            case 5:
            case 6:
            case 12:
                break;
            case 7:
                str = "1xrtt";
                break;
            case 8:
                str = "hsdpa";
                break;
            case 9:
                str = "hsupa";
                break;
            case 10:
                str = "hspa";
                break;
            case 11:
                str = "iden";
                break;
            case 13:
                str = "lte";
                break;
            case 14:
                str = "ehrpd";
                break;
            case 15:
                str = "hspap";
                break;
            default:
                loge("unknown network type: " + telephonyManager.getNetworkType());
                str = "unknown";
                break;
        }
        return "net.tcp.buffersize." + str;
    }

    @Override // android.net.NetworkStateTracker
    public boolean teardown() {
        setTeardownRequested(true);
        return setEnableApn(this.mApnType, false) != 3;
    }

    public boolean isReady() {
        return this.mDataConnectionTrackerAc != null;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckCompleted(boolean z) {
        if (this.mIsCaptivePortal.getAndSet(z) != z) {
            setEnableFailFastMobileData(z ? 1 : 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDetailedState(NetworkInfo.DetailedState detailedState, String str, String str2) {
        log("setDetailed state, old =" + this.mNetworkInfo.getDetailedState() + " and new state=" + detailedState);
        if (detailedState != this.mNetworkInfo.getDetailedState()) {
            boolean z = this.mNetworkInfo.getState() == NetworkInfo.State.CONNECTING;
            String reason = this.mNetworkInfo.getReason();
            if (z && detailedState == NetworkInfo.DetailedState.CONNECTED && str == null && reason != null) {
                str = reason;
            }
            this.mNetworkInfo.setDetailedState(detailedState, str, str2);
            this.mTarget.obtainMessage(458752, new NetworkInfo(this.mNetworkInfo)).sendToTarget();
        }
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
        setTeardownRequested(false);
        int enableApn = setEnableApn(this.mApnType, true);
        if (enableApn != 0) {
            if (enableApn != 1) {
                if (enableApn == 2 || enableApn == 3) {
                    return false;
                }
                loge("Error in reconnect - unexpected response.");
                return false;
            }
            this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.IDLE, null, null);
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0020, code lost:
    
        r1 = new java.lang.StringBuilder().append("Could not set radio power to ");
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x002b, code lost:
    
        if (r4 == false) goto L16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x002d, code lost:
    
        r4 = android.hardware.Camera.Parameters.FLASH_MODE_ON;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0031, code lost:
    
        r4 = "off";
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0034, code lost:
    
        loge(r1.append(r4).toString());
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x003f, code lost:
    
        return false;
     */
    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean setRadio(boolean r4) {
        /*
            r3 = this;
            r0 = 0
            r3.getPhoneService(r0)
            r1 = r0
        L5:
            r2 = 2
            if (r1 >= r2) goto L20
            com.android.internal.telephony.ITelephony r2 = r3.mPhoneService
            if (r2 != 0) goto L12
            java.lang.String r1 = "Ignoring mobile radio request because could not acquire PhoneService"
            r3.loge(r1)
            goto L20
        L12:
            boolean r4 = r2.setRadio(r4)     // Catch: android.os.RemoteException -> L17
            return r4
        L17:
            if (r1 != 0) goto L1d
            r2 = 1
            r3.getPhoneService(r2)
        L1d:
            int r1 = r1 + 1
            goto L5
        L20:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Could not set radio power to "
            java.lang.StringBuilder r1 = r1.append(r2)
            if (r4 == 0) goto L31
            java.lang.String r4 = "on"
            goto L34
        L31:
            java.lang.String r4 = "off"
        L34:
            java.lang.StringBuilder r4 = r1.append(r4)
            java.lang.String r4 = r4.toString()
            r3.loge(r4)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.MobileDataStateTracker.setRadio(boolean):boolean");
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setUserDataEnable(boolean z) {
        log("setUserDataEnable: E enabled=" + z);
        AsyncChannel asyncChannel = this.mDataConnectionTrackerAc;
        if (asyncChannel != null) {
            asyncChannel.sendMessage(270366, z ? 1 : 0);
            this.mUserDataEnabled = z;
        }
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setPolicyDataEnable(boolean z) {
        log("setPolicyDataEnable(enabled=" + z + ")");
        AsyncChannel asyncChannel = this.mDataConnectionTrackerAc;
        if (asyncChannel != null) {
            asyncChannel.sendMessage(270368, z ? 1 : 0);
            this.mPolicyDataEnabled = z;
        }
    }

    public void setEnableFailFastMobileData(int i) {
        log("setEnableFailFastMobileData(enabled=" + i + ")");
        AsyncChannel asyncChannel = this.mDataConnectionTrackerAc;
        if (asyncChannel != null) {
            asyncChannel.sendMessage(270372, i);
        }
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setDependencyMet(boolean z) {
        Bundle bundleForPair = Bundle.forPair("apnType", this.mApnType);
        try {
            log("setDependencyMet: E met=" + z);
            Message messageObtain = Message.obtain();
            messageObtain.what = 270367;
            messageObtain.arg1 = z ? 1 : 0;
            messageObtain.setData(bundleForPair);
            this.mDataConnectionTrackerAc.sendMessage(messageObtain);
        } catch (NullPointerException e) {
            loge("setDependencyMet: X mAc was null" + e);
        }
    }

    public void enableMobileProvisioning(String str) {
        log("enableMobileProvisioning(url=" + str + ")");
        AsyncChannel asyncChannel = this.mDataConnectionTrackerAc;
        if (asyncChannel != null) {
            Message messageObtain = Message.obtain();
            messageObtain.what = 270373;
            messageObtain.setData(Bundle.forPair("provisioningUrl", str));
            asyncChannel.sendMessage(messageObtain);
        }
    }

    public boolean isProvisioningNetwork() {
        boolean z = false;
        try {
            Message messageObtain = Message.obtain();
            messageObtain.what = 270374;
            messageObtain.setData(Bundle.forPair("apnType", this.mApnType));
            if (this.mDataConnectionTrackerAc.sendMessageSynchronously(messageObtain).arg1 == 1) {
                z = true;
            }
        } catch (NullPointerException e) {
            loge("isProvisioningNetwork: X " + e);
        }
        log("isProvisioningNetwork: retVal=" + z);
        return z;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void addStackedLink(LinkProperties linkProperties) {
        this.mLinkProperties.addStackedLink(linkProperties);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void removeStackedLink(LinkProperties linkProperties) {
        this.mLinkProperties.removeStackedLink(linkProperties);
    }

    public String toString() {
        CharArrayWriter charArrayWriter = new CharArrayWriter();
        PrintWriter printWriter = new PrintWriter(charArrayWriter);
        printWriter.print("Mobile data state: ");
        printWriter.println(this.mMobileDataState);
        printWriter.print("Data enabled: user=");
        printWriter.print(this.mUserDataEnabled);
        printWriter.print(", policy=");
        printWriter.println(this.mPolicyDataEnabled);
        return charArrayWriter.toString();
    }

    private int setEnableApn(String str, boolean z) {
        int i = 0;
        getPhoneService(false);
        while (true) {
            if (i >= 2) {
                break;
            }
            ITelephony iTelephony = this.mPhoneService;
            if (iTelephony == null) {
                loge("Ignoring feature request because could not acquire PhoneService");
                break;
            }
            try {
                if (z) {
                    return iTelephony.enableApnType(str);
                }
                return iTelephony.disableApnType(str);
            } catch (RemoteException unused) {
                if (i == 0) {
                    getPhoneService(true);
                }
                i++;
            }
        }
        loge("Could not " + (z ? "enable" : "disable") + " APN type \"" + str + "\"");
        return 3;
    }

    public static String networkTypeToApnType(int i) {
        if (i == 0) {
            return "default";
        }
        if (i == 14) {
            return "ia";
        }
        if (i == 2) {
            return "mms";
        }
        if (i == 3) {
            return "supl";
        }
        if (i == 4) {
            return "dun";
        }
        if (i == 5) {
            return "hipri";
        }
        switch (i) {
            case 10:
                return "fota";
            case 11:
                return "ims";
            case 12:
                return "cbs";
            default:
                sloge("Error mapping networkType " + i + " to apnType.");
                return null;
        }
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
    public void supplyMessenger(Messenger messenger) {
        new AsyncChannel().connect(this.mContext, this.mHandler, messenger);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str) {
        Slog.d(TAG, this.mApnType + ": " + str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loge(String str) {
        Slog.e(TAG, this.mApnType + ": " + str);
    }

    private static void sloge(String str) {
        Slog.e(TAG, str);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public LinkQualityInfo getLinkQualityInfo() {
        NetworkInfo networkInfo = this.mNetworkInfo;
        if (networkInfo == null || networkInfo.getType() == -1) {
            return null;
        }
        MobileLinkQualityInfo mobileLinkQualityInfo = new MobileLinkQualityInfo();
        mobileLinkQualityInfo.setNetworkType(this.mNetworkInfo.getType());
        this.mSamplingDataTracker.setCommonLinkQualityInfoFields(mobileLinkQualityInfo);
        if (this.mNetworkInfo.getSubtype() != 0) {
            mobileLinkQualityInfo.setMobileNetworkType(this.mNetworkInfo.getSubtype());
            NetworkDataEntry networkDataEntry = getNetworkDataEntry(this.mNetworkInfo.getSubtype());
            if (networkDataEntry != null) {
                mobileLinkQualityInfo.setTheoreticalRxBandwidth(networkDataEntry.downloadBandwidth);
                mobileLinkQualityInfo.setTheoreticalRxBandwidth(networkDataEntry.uploadBandwidth);
                mobileLinkQualityInfo.setTheoreticalLatency(networkDataEntry.latency);
            }
            if (this.mSignalStrength != null) {
                mobileLinkQualityInfo.setNormalizedSignalStrength(getNormalizedSignalStrength(mobileLinkQualityInfo.getMobileNetworkType(), this.mSignalStrength));
            }
        }
        SignalStrength signalStrength = this.mSignalStrength;
        if (signalStrength != null) {
            mobileLinkQualityInfo.setRssi(signalStrength.getGsmSignalStrength());
            mobileLinkQualityInfo.setGsmErrorRate(signalStrength.getGsmBitErrorRate());
            mobileLinkQualityInfo.setCdmaDbm(signalStrength.getCdmaDbm());
            mobileLinkQualityInfo.setCdmaEcio(signalStrength.getCdmaEcio());
            mobileLinkQualityInfo.setEvdoDbm(signalStrength.getEvdoDbm());
            mobileLinkQualityInfo.setEvdoEcio(signalStrength.getEvdoEcio());
            mobileLinkQualityInfo.setEvdoSnr(signalStrength.getEvdoSnr());
            mobileLinkQualityInfo.setLteSignalStrength(signalStrength.getLteSignalStrength());
            mobileLinkQualityInfo.setLteRsrp(signalStrength.getLteRsrp());
            mobileLinkQualityInfo.setLteRsrq(signalStrength.getLteRsrq());
            mobileLinkQualityInfo.setLteRssnr(signalStrength.getLteRssnr());
            mobileLinkQualityInfo.setLteCqi(signalStrength.getLteCqi());
        }
        return mobileLinkQualityInfo;
    }

    static class NetworkDataEntry {
        public int downloadBandwidth;
        public int latency;
        public int networkType;
        public int uploadBandwidth;

        NetworkDataEntry(int i, int i2, int i3, int i4) {
            this.networkType = i;
            this.downloadBandwidth = i2;
            this.uploadBandwidth = i3;
            this.latency = i4;
        }
    }

    private static NetworkDataEntry getNetworkDataEntry(int i) {
        for (NetworkDataEntry networkDataEntry : mTheoreticalBWTable) {
            if (networkDataEntry.networkType == i) {
                return networkDataEntry;
            }
        }
        Slog.e(TAG, "Could not find Theoretical BW entry for " + String.valueOf(i));
        return null;
    }

    private static int getNormalizedSignalStrength(int i, SignalStrength signalStrength) {
        int gsmLevel;
        switch (i) {
            case 1:
            case 2:
            case 3:
            case 8:
            case 9:
            case 10:
            case 15:
                gsmLevel = signalStrength.getGsmLevel();
                break;
            case 4:
            case 7:
                gsmLevel = signalStrength.getCdmaLevel();
                break;
            case 5:
            case 6:
            case 12:
                gsmLevel = signalStrength.getEvdoLevel();
                break;
            case 11:
            case 14:
            default:
                return Integer.MAX_VALUE;
            case 13:
                gsmLevel = signalStrength.getLteLevel();
                break;
        }
        return (gsmLevel * 100) / 5;
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
