package android.bluetooth;

import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothA2dpSink;
import android.bluetooth.IBluetoothStateChangeCallback;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class BluetoothA2dpSink implements BluetoothProfile {
    public static final String ACTION_AUDIO_CONFIG_CHANGED = "android.bluetooth.a2dp-sink.profile.action.AUDIO_CONFIG_CHANGED";
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.a2dp-sink.profile.action.CONNECTION_STATE_CHANGED";
    public static final String ACTION_PLAYING_STATE_CHANGED = "android.bluetooth.a2dp-sink.profile.action.PLAYING_STATE_CHANGED";
    private static final boolean DBG = true;
    public static final String EXTRA_AUDIO_CONFIG = "android.bluetooth.a2dp-sink.profile.extra.AUDIO_CONFIG";
    public static final int STATE_NOT_PLAYING = 11;
    public static final int STATE_PLAYING = 10;
    private static final String TAG = "BluetoothA2dpSink";
    private static final boolean VDBG = false;
    private BluetoothAdapter mAdapter;
    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback;
    private final ServiceConnection mConnection;
    private Context mContext;
    private IBluetoothA2dpSink mService;
    private BluetoothProfile.ServiceListener mServiceListener;

    BluetoothA2dpSink(Context context, BluetoothProfile.ServiceListener serviceListener) {
        IBluetoothStateChangeCallback.Stub stub = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothA2dpSink.1
            @Override // android.bluetooth.IBluetoothStateChangeCallback
            public void onBluetoothStateChange(boolean z) {
                Log.d(BluetoothA2dpSink.TAG, "onBluetoothStateChange: up=" + z);
                if (!z) {
                    synchronized (BluetoothA2dpSink.this.mConnection) {
                        try {
                            BluetoothA2dpSink.this.mService = null;
                            BluetoothA2dpSink.this.mContext.unbindService(BluetoothA2dpSink.this.mConnection);
                        } catch (Exception e) {
                            Log.e(BluetoothA2dpSink.TAG, "", e);
                        }
                    }
                    return;
                }
                synchronized (BluetoothA2dpSink.this.mConnection) {
                    try {
                    } catch (Exception e2) {
                        Log.e(BluetoothA2dpSink.TAG, "", e2);
                    }
                    if (BluetoothA2dpSink.this.mService == null) {
                        BluetoothA2dpSink.this.doBind();
                    }
                }
            }
        };
        this.mBluetoothStateChangeCallback = stub;
        this.mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothA2dpSink.2
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d(BluetoothA2dpSink.TAG, "Proxy object connected");
                BluetoothA2dpSink.this.mService = IBluetoothA2dpSink.Stub.asInterface(iBinder);
                if (BluetoothA2dpSink.this.mServiceListener != null) {
                    BluetoothA2dpSink.this.mServiceListener.onServiceConnected(10, BluetoothA2dpSink.this);
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d(BluetoothA2dpSink.TAG, "Proxy object disconnected");
                BluetoothA2dpSink.this.mService = null;
                if (BluetoothA2dpSink.this.mServiceListener != null) {
                    BluetoothA2dpSink.this.mServiceListener.onServiceDisconnected(10);
                }
            }
        };
        this.mContext = context;
        this.mServiceListener = serviceListener;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mAdapter = defaultAdapter;
        IBluetoothManager bluetoothManager = defaultAdapter.getBluetoothManager();
        if (bluetoothManager != null) {
            try {
                bluetoothManager.registerStateChangeCallback(stub);
            } catch (RemoteException e) {
                Log.e(TAG, "", e);
            }
        }
        doBind();
    }

    boolean doBind() {
        Intent intent = new Intent(IBluetoothA2dpSink.class.getName());
        ComponentName componentNameResolveSystemService = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(componentNameResolveSystemService);
        if (componentNameResolveSystemService != null && this.mContext.bindServiceAsUser(intent, this.mConnection, 0, Process.myUserHandle())) {
            return true;
        }
        Log.e(TAG, "Could not bind to Bluetooth A2DP Service with " + intent);
        return false;
    }

    void close() {
        this.mServiceListener = null;
        IBluetoothManager bluetoothManager = this.mAdapter.getBluetoothManager();
        if (bluetoothManager != null) {
            try {
                bluetoothManager.unregisterStateChangeCallback(this.mBluetoothStateChangeCallback);
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
        }
        synchronized (this.mConnection) {
            if (this.mService != null) {
                try {
                    this.mService = null;
                    this.mContext.unbindService(this.mConnection);
                } catch (Exception e2) {
                    Log.e(TAG, "", e2);
                }
            }
        }
    }

    public void finalize() {
        close();
    }

    public boolean connect(BluetoothDevice bluetoothDevice) {
        log("connect(" + bluetoothDevice + ")");
        if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return this.mService.connect(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return false;
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return false;
    }

    public boolean disconnect(BluetoothDevice bluetoothDevice) {
        log("disconnect(" + bluetoothDevice + ")");
        if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return this.mService.disconnect(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return false;
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return false;
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getConnectedDevices() {
        if (this.mService != null && isEnabled()) {
            try {
                return this.mService.getConnectedDevices();
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return new ArrayList();
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] iArr) {
        if (this.mService != null && isEnabled()) {
            try {
                return this.mService.getDevicesMatchingConnectionStates(iArr);
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return new ArrayList();
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public int getConnectionState(BluetoothDevice bluetoothDevice) {
        if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return this.mService.getConnectionState(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return 0;
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return 0;
    }

    public BluetoothAudioConfig getAudioConfig(BluetoothDevice bluetoothDevice) {
        if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return this.mService.getAudioConfig(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return null;
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return null;
    }

    public static String stateToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? i != 10 ? i != 11 ? "<unknown state " + i + ">" : "not playing" : "playing" : "disconnecting" : "connected" : "connecting" : "disconnected";
    }

    private boolean isEnabled() {
        return this.mAdapter.getState() == 12;
    }

    private boolean isValidDevice(BluetoothDevice bluetoothDevice) {
        return bluetoothDevice != null && BluetoothAdapter.checkBluetoothAddress(bluetoothDevice.getAddress());
    }

    private static void log(String str) {
        Log.d(TAG, str);
    }
}
