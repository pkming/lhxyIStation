package android.net.wifi;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import com.android.internal.util.AsyncChannel;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
class WifiApConfigStore extends StateMachine {
    private static final String AP_CONFIG_FILE = Environment.getDataDirectory() + "/misc/wifi/softap.conf";
    private static final int AP_CONFIG_FILE_VERSION = 1;
    private static final String TAG = "WifiApConfigStore";
    private State mActiveState;
    private Context mContext;
    private State mDefaultState;
    private State mInactiveState;
    private AsyncChannel mReplyChannel;
    private WifiConfiguration mWifiApConfig;

    WifiApConfigStore(Context context, Handler handler) {
        super(TAG, handler.getLooper());
        this.mDefaultState = new DefaultState();
        this.mInactiveState = new InactiveState();
        this.mActiveState = new ActiveState();
        this.mWifiApConfig = null;
        this.mReplyChannel = new AsyncChannel();
        this.mContext = context;
        addState(this.mDefaultState);
        addState(this.mInactiveState, this.mDefaultState);
        addState(this.mActiveState, this.mDefaultState);
        setInitialState(this.mInactiveState);
    }

    public static WifiApConfigStore makeWifiApConfigStore(Context context, Handler handler) {
        WifiApConfigStore wifiApConfigStore = new WifiApConfigStore(context, handler);
        wifiApConfigStore.start();
        return wifiApConfigStore;
    }

    class DefaultState extends State {
        DefaultState() {
        }

        public boolean processMessage(Message message) {
            switch (message.what) {
                case 131097:
                case 131098:
                    Log.e(WifiApConfigStore.TAG, "Unexpected message: " + message);
                    break;
                case 131099:
                    WifiApConfigStore.this.mReplyChannel.replyToMessage(message, 131100, WifiApConfigStore.this.mWifiApConfig);
                    break;
                default:
                    Log.e(WifiApConfigStore.TAG, "Failed to handle " + message);
                    break;
            }
            return true;
        }
    }

    class InactiveState extends State {
        InactiveState() {
        }

        public boolean processMessage(Message message) {
            if (message.what != 131097) {
                return false;
            }
            WifiApConfigStore.this.mWifiApConfig = (WifiConfiguration) message.obj;
            WifiApConfigStore wifiApConfigStore = WifiApConfigStore.this;
            wifiApConfigStore.transitionTo(wifiApConfigStore.mActiveState);
            return true;
        }
    }

    class ActiveState extends State {
        ActiveState() {
        }

        public void enter() {
            new Thread(new Runnable() { // from class: android.net.wifi.WifiApConfigStore.ActiveState.1
                @Override // java.lang.Runnable
                public void run() throws Throwable {
                    WifiApConfigStore.this.writeApConfiguration(WifiApConfigStore.this.mWifiApConfig);
                    WifiApConfigStore.this.sendMessage(131098);
                }
            }).start();
        }

        public boolean processMessage(Message message) {
            switch (message.what) {
                case 131097:
                    WifiApConfigStore.this.deferMessage(message);
                    return true;
                case 131098:
                    WifiApConfigStore wifiApConfigStore = WifiApConfigStore.this;
                    wifiApConfigStore.transitionTo(wifiApConfigStore.mInactiveState);
                    return true;
                default:
                    return false;
            }
        }
    }

    void loadApConfiguration() throws Throwable {
        DataInputStream dataInputStream;
        Throwable th;
        WifiConfiguration wifiConfiguration;
        DataInputStream dataInputStream2 = null;
        try {
            try {
                try {
                    wifiConfiguration = new WifiConfiguration();
                    dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(AP_CONFIG_FILE)));
                } catch (IOException unused) {
                    return;
                }
            } catch (IOException unused2) {
            }
        } catch (Throwable th2) {
            dataInputStream = dataInputStream2;
            th = th2;
        }
        try {
        } catch (IOException unused3) {
            dataInputStream2 = dataInputStream;
            setDefaultApConfiguration();
            if (dataInputStream2 == null) {
                return;
            } else {
                dataInputStream2.close();
            }
        } catch (Throwable th3) {
            th = th3;
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException unused4) {
                }
            }
            throw th;
        }
        if (dataInputStream.readInt() != 1) {
            Log.e(TAG, "Bad version on hotspot configuration file, set defaults");
            setDefaultApConfiguration();
            try {
                dataInputStream.close();
                return;
            } catch (IOException unused5) {
                return;
            }
        }
        wifiConfiguration.SSID = dataInputStream.readUTF();
        int i = dataInputStream.readInt();
        wifiConfiguration.allowedKeyManagement.set(i);
        if (i != 0) {
            wifiConfiguration.preSharedKey = dataInputStream.readUTF();
        }
        this.mWifiApConfig = wifiConfiguration;
        dataInputStream.close();
    }

    Messenger getMessenger() {
        return new Messenger(getHandler());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0 */
    /* JADX WARN: Type inference failed for: r0v1 */
    /* JADX WARN: Type inference failed for: r0v11 */
    /* JADX WARN: Type inference failed for: r0v12 */
    /* JADX WARN: Type inference failed for: r0v13 */
    /* JADX WARN: Type inference failed for: r0v2, types: [java.io.DataOutputStream] */
    /* JADX WARN: Type inference failed for: r0v4 */
    /* JADX WARN: Type inference failed for: r0v6 */
    public void writeApConfiguration(WifiConfiguration wifiConfiguration) throws Throwable {
        DataOutputStream dataOutputStream;
        ?? r0 = 0;
        DataOutputStream dataOutputStream2 = null;
        try {
            try {
                try {
                    dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(AP_CONFIG_FILE)));
                } catch (Throwable th) {
                    th = th;
                }
            } catch (IOException e) {
                e = e;
            }
            try {
                dataOutputStream.writeInt(1);
                dataOutputStream.writeUTF(wifiConfiguration.SSID);
                int authType = wifiConfiguration.getAuthType();
                dataOutputStream.writeInt(authType);
                if (authType != 0) {
                    dataOutputStream.writeUTF(wifiConfiguration.preSharedKey);
                }
                dataOutputStream.close();
                r0 = authType;
            } catch (IOException e2) {
                e = e2;
                dataOutputStream2 = dataOutputStream;
                Log.e(TAG, "Error writing hotspot configuration" + e);
                r0 = dataOutputStream2;
                if (dataOutputStream2 != null) {
                    dataOutputStream2.close();
                    r0 = dataOutputStream2;
                }
            } catch (Throwable th2) {
                th = th2;
                r0 = dataOutputStream;
                if (r0 != 0) {
                    try {
                        r0.close();
                    } catch (IOException unused) {
                    }
                }
                throw th;
            }
        } catch (IOException unused2) {
        }
    }

    private void setDefaultApConfiguration() {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = this.mContext.getString(17040439);
        wifiConfiguration.allowedKeyManagement.set(4);
        String string = UUID.randomUUID().toString();
        wifiConfiguration.preSharedKey = string.substring(0, 8) + string.substring(9, 13);
        sendMessage(131097, wifiConfiguration);
    }
}
