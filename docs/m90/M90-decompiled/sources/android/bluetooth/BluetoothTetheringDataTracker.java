package android.bluetooth;

import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.net.BaseNetworkStateTracker;
import android.net.DhcpResults;
import android.net.LinkCapabilities;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkStateTracker;
import android.net.NetworkUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.AsyncChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothTetheringDataTracker extends BaseNetworkStateTracker {
    private static final boolean DBG = true;
    private static final String NETWORKTYPE = "BLUETOOTH_TETHER";
    private static final String TAG = "BluetoothTethering";
    private static final boolean VDBG = true;
    private static String mRevTetheredIface;
    private static BluetoothTetheringDataTracker sInstance;
    private BluetoothPan mBluetoothPan;
    private BtdtHandler mBtdtHandler;
    private Handler mCsHandler;
    private AtomicBoolean mTeardownRequested = new AtomicBoolean(false);
    private AtomicBoolean mPrivateDnsRouteSet = new AtomicBoolean(false);
    private AtomicInteger mDefaultGatewayAddr = new AtomicInteger(0);
    private AtomicBoolean mDefaultRouteSet = new AtomicBoolean(false);
    private final Object mLinkPropertiesLock = new Object();
    private final Object mNetworkInfoLock = new Object();
    private AtomicReference<AsyncChannel> mAsyncChannel = new AtomicReference<>(null);
    private BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener() { // from class: android.bluetooth.BluetoothTetheringDataTracker.1
        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            BluetoothTetheringDataTracker.this.mBluetoothPan = (BluetoothPan) bluetoothProfile;
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
            BluetoothTetheringDataTracker.this.mBluetoothPan = null;
        }
    };

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckComplete() {
    }

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
    public boolean setRadio(boolean z) {
        return true;
    }

    public int startUsingNetworkFeature(String str, int i, int i2) {
        return -1;
    }

    public int stopUsingNetworkFeature(String str, int i, int i2) {
        return -1;
    }

    private BluetoothTetheringDataTracker() {
        this.mNetworkInfo = new NetworkInfo(7, 0, NETWORKTYPE, "");
        this.mLinkProperties = new LinkProperties();
        this.mLinkCapabilities = new LinkCapabilities();
        this.mNetworkInfo.setIsAvailable(false);
        setTeardownRequested(false);
    }

    public static synchronized BluetoothTetheringDataTracker getInstance() {
        if (sInstance == null) {
            sInstance = new BluetoothTetheringDataTracker();
        }
        return sInstance;
    }

    public Object Clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
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
        Log.d(TAG, "startMonitoring: target: " + handler);
        this.mContext = context;
        this.mCsHandler = handler;
        Log.d(TAG, "startMonitoring: mCsHandler: " + this.mCsHandler);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            defaultAdapter.getProfileProxy(this.mContext, this.mProfileServiceListener, 5);
        }
        this.mBtdtHandler = new BtdtHandler(handler.getLooper(), this);
    }

    @Override // android.net.NetworkStateTracker
    public boolean teardown() {
        this.mTeardownRequested.set(true);
        BluetoothPan bluetoothPan = this.mBluetoothPan;
        if (bluetoothPan != null) {
            Iterator<BluetoothDevice> it = bluetoothPan.getConnectedDevices().iterator();
            while (it.hasNext()) {
                this.mBluetoothPan.disconnect(it.next());
            }
        }
        return true;
    }

    @Override // android.net.NetworkStateTracker
    public boolean reconnect() {
        this.mTeardownRequested.set(false);
        return true;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public synchronized boolean isAvailable() {
        return this.mNetworkInfo.isAvailable();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setUserDataEnable(boolean z) {
        Log.w(TAG, "ignoring setUserDataEnable(" + z + ")");
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setPolicyDataEnable(boolean z) {
        Log.w(TAG, "ignoring setPolicyDataEnable(" + z + ")");
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
        NetworkInfo networkInfo;
        synchronized (this.mNetworkInfoLock) {
            networkInfo = new NetworkInfo(this.mNetworkInfo);
        }
        return networkInfo;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public LinkProperties getLinkProperties() {
        LinkProperties linkProperties;
        synchronized (this.mLinkPropertiesLock) {
            linkProperties = new LinkProperties(this.mLinkProperties);
        }
        return linkProperties;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public LinkCapabilities getLinkCapabilities() {
        return new LinkCapabilities(this.mLinkCapabilities);
    }

    public int getDefaultGatewayAddr() {
        return this.mDefaultGatewayAddr.get();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isDefaultRouteSet() {
        return this.mDefaultRouteSet.get();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void defaultRouteSet(boolean z) {
        this.mDefaultRouteSet.set(z);
    }

    private static short countPrefixLength(byte[] bArr) {
        short s = 0;
        for (byte b : bArr) {
            for (int i = 0; i < 8; i++) {
                if (((1 << i) & b) != 0) {
                    s = (short) (s + 1);
                }
            }
        }
        return s;
    }

    void startReverseTether(final LinkProperties linkProperties) {
        if (linkProperties == null || TextUtils.isEmpty(linkProperties.getInterfaceName())) {
            Log.e(TAG, "attempted to reverse tether with empty interface");
            return;
        }
        synchronized (this.mLinkPropertiesLock) {
            if (this.mLinkProperties.getInterfaceName() != null) {
                Log.e(TAG, "attempted to reverse tether while already in process");
            } else {
                this.mLinkProperties = linkProperties;
                new Thread(new Runnable() { // from class: android.bluetooth.BluetoothTetheringDataTracker.2
                    @Override // java.lang.Runnable
                    public void run() {
                        DhcpResults dhcpResults = new DhcpResults();
                        boolean zRunDhcp = NetworkUtils.runDhcp(linkProperties.getInterfaceName(), dhcpResults);
                        synchronized (BluetoothTetheringDataTracker.this.mLinkPropertiesLock) {
                            if (linkProperties.getInterfaceName() != BluetoothTetheringDataTracker.this.mLinkProperties.getInterfaceName()) {
                                Log.e(BluetoothTetheringDataTracker.TAG, "obsolete DHCP run aborted");
                                return;
                            }
                            if (!zRunDhcp) {
                                Log.e(BluetoothTetheringDataTracker.TAG, "DHCP request error:" + NetworkUtils.getDhcpError());
                                return;
                            }
                            BluetoothTetheringDataTracker.this.mLinkProperties = dhcpResults.linkProperties;
                            synchronized (BluetoothTetheringDataTracker.this.mNetworkInfoLock) {
                                BluetoothTetheringDataTracker.this.mNetworkInfo.setIsAvailable(true);
                                BluetoothTetheringDataTracker.this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.CONNECTED, null, null);
                                if (BluetoothTetheringDataTracker.this.mCsHandler != null) {
                                    BluetoothTetheringDataTracker.this.mCsHandler.obtainMessage(458752, new NetworkInfo(BluetoothTetheringDataTracker.this.mNetworkInfo)).sendToTarget();
                                }
                            }
                        }
                    }
                }).start();
            }
        }
    }

    void stopReverseTether() {
        synchronized (this.mLinkPropertiesLock) {
            if (TextUtils.isEmpty(this.mLinkProperties.getInterfaceName())) {
                Log.e(TAG, "attempted to stop reverse tether with nothing tethered");
                return;
            }
            NetworkUtils.stopDhcp(this.mLinkProperties.getInterfaceName());
            this.mLinkProperties.clear();
            synchronized (this.mNetworkInfoLock) {
                this.mNetworkInfo.setIsAvailable(false);
                this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.DISCONNECTED, null, null);
                Handler handler = this.mCsHandler;
                if (handler != null) {
                    handler.obtainMessage(458752, new NetworkInfo(this.mNetworkInfo)).sendToTarget();
                }
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

    static class BtdtHandler extends Handler {
        private final BluetoothTetheringDataTracker mBtdt;
        private AsyncChannel mStackChannel;

        BtdtHandler(Looper looper, BluetoothTetheringDataTracker bluetoothTetheringDataTracker) {
            super(looper);
            this.mBtdt = bluetoothTetheringDataTracker;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 69632:
                    Log.d(BluetoothTetheringDataTracker.TAG, "got CMD_CHANNEL_HALF_CONNECTED");
                    if (message.arg1 == 0) {
                        AsyncChannel asyncChannel = (AsyncChannel) message.obj;
                        if (!this.mBtdt.mAsyncChannel.compareAndSet(null, asyncChannel)) {
                            Log.e(BluetoothTetheringDataTracker.TAG, "Trying to set mAsyncChannel twice!");
                        } else {
                            asyncChannel.sendMessage(69633);
                        }
                    }
                    break;
                case 69636:
                    Log.d(BluetoothTetheringDataTracker.TAG, "got CMD_CHANNEL_DISCONNECTED");
                    this.mBtdt.stopReverseTether();
                    this.mBtdt.mAsyncChannel.set(null);
                    break;
                case NetworkStateTracker.EVENT_NETWORK_CONNECTED /* 458756 */:
                    LinkProperties linkProperties = (LinkProperties) message.obj;
                    Log.d(BluetoothTetheringDataTracker.TAG, "got EVENT_NETWORK_CONNECTED, " + linkProperties);
                    this.mBtdt.startReverseTether(linkProperties);
                    break;
                case NetworkStateTracker.EVENT_NETWORK_DISCONNECTED /* 458757 */:
                    Log.d(BluetoothTetheringDataTracker.TAG, "got EVENT_NETWORK_DISCONNECTED, " + ((LinkProperties) message.obj));
                    this.mBtdt.stopReverseTether();
                    break;
            }
        }
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void supplyMessenger(Messenger messenger) {
        if (messenger != null) {
            new AsyncChannel().connect(this.mContext, this.mBtdtHandler, messenger);
        }
    }
}
