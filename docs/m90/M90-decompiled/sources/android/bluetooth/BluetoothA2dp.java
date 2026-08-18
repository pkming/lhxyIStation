package android.bluetooth;

import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothA2dp;
import android.bluetooth.IBluetoothStateChangeCallback;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class BluetoothA2dp implements BluetoothProfile {
    public static final String ACTION_AVRCP_CONNECTION_STATE_CHANGED = "android.bluetooth.a2dp.profile.action.AVRCP_CONNECTION_STATE_CHANGED";
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED";
    public static final String ACTION_PLAYING_STATE_CHANGED = "android.bluetooth.a2dp.profile.action.PLAYING_STATE_CHANGED";
    private static final boolean DBG = true;
    public static final int STATE_NOT_PLAYING = 11;
    public static final int STATE_PLAYING = 10;
    private static final String TAG = "BluetoothA2dp";
    private static final boolean VDBG = false;
    private BluetoothAdapter mAdapter;
    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback;
    private final ServiceConnection mConnection;
    private Context mContext;
    private IBluetoothA2dp mService;
    private BluetoothProfile.ServiceListener mServiceListener;

    BluetoothA2dp(Context context, BluetoothProfile.ServiceListener serviceListener) {
        IBluetoothStateChangeCallback.Stub stub = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothA2dp.1
            @Override // android.bluetooth.IBluetoothStateChangeCallback
            public void onBluetoothStateChange(boolean z) {
                Log.d(BluetoothA2dp.TAG, "onBluetoothStateChange: up=" + z);
                if (!z) {
                    synchronized (BluetoothA2dp.this.mConnection) {
                        try {
                            BluetoothA2dp.this.mService = null;
                            BluetoothA2dp.this.mContext.unbindService(BluetoothA2dp.this.mConnection);
                        } catch (Exception e) {
                            Log.e(BluetoothA2dp.TAG, "", e);
                        }
                    }
                    return;
                }
                synchronized (BluetoothA2dp.this.mConnection) {
                    try {
                    } catch (Exception e2) {
                        Log.e(BluetoothA2dp.TAG, "", e2);
                    }
                    if (BluetoothA2dp.this.mService == null) {
                        BluetoothA2dp.this.doBind();
                    }
                }
            }
        };
        this.mBluetoothStateChangeCallback = stub;
        this.mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothA2dp.2
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d(BluetoothA2dp.TAG, "Proxy object connected");
                BluetoothA2dp.this.mService = IBluetoothA2dp.Stub.asInterface(iBinder);
                if (BluetoothA2dp.this.mServiceListener != null) {
                    BluetoothA2dp.this.mServiceListener.onServiceConnected(2, BluetoothA2dp.this);
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d(BluetoothA2dp.TAG, "Proxy object disconnected");
                BluetoothA2dp.this.mService = null;
                if (BluetoothA2dp.this.mServiceListener != null) {
                    BluetoothA2dp.this.mServiceListener.onServiceDisconnected(2);
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
        Intent intent = new Intent(IBluetoothA2dp.class.getName());
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

    public boolean setPriority(BluetoothDevice bluetoothDevice, int i) {
        log("setPriority(" + bluetoothDevice + ", " + i + ")");
        if (this.mService == null || !isEnabled() || !isValidDevice(bluetoothDevice)) {
            if (this.mService == null) {
                Log.w(TAG, "Proxy not attached to service");
            }
            return false;
        }
        if (i != 0 && i != 100) {
            return false;
        }
        try {
            return this.mService.setPriority(bluetoothDevice, i);
        } catch (RemoteException unused) {
            Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            return false;
        }
    }

    public int getPriority(BluetoothDevice bluetoothDevice) {
        if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return this.mService.getPriority(bluetoothDevice);
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

    public boolean isAvrcpAbsoluteVolumeSupported() {
        Log.d(TAG, "isAvrcpAbsoluteVolumeSupported");
        if (this.mService != null && isEnabled()) {
            try {
                return this.mService.isAvrcpAbsoluteVolumeSupported();
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in isAvrcpAbsoluteVolumeSupported()", e);
                return false;
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return false;
    }

    public void adjustAvrcpAbsoluteVolume(int i) {
        Log.d(TAG, "adjustAvrcpAbsoluteVolume");
        if (this.mService != null && isEnabled()) {
            try {
                this.mService.adjustAvrcpAbsoluteVolume(i);
                return;
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in adjustAvrcpAbsoluteVolume()", e);
                return;
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
    }

    public void setAvrcpAbsoluteVolume(int i) {
        Log.d(TAG, "setAvrcpAbsoluteVolume");
        if (this.mService != null && isEnabled()) {
            try {
                this.mService.setAvrcpAbsoluteVolume(i);
                return;
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in setAvrcpAbsoluteVolume()", e);
                return;
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
    }

    public boolean isA2dpPlaying(BluetoothDevice bluetoothDevice) {
        if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return this.mService.isA2dpPlaying(bluetoothDevice);
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

    public boolean shouldSendVolumeKeys(BluetoothDevice bluetoothDevice) {
        ParcelUuid[] uuids;
        if (!isEnabled() || !isValidDevice(bluetoothDevice) || (uuids = bluetoothDevice.getUuids()) == null) {
            return false;
        }
        for (ParcelUuid parcelUuid : uuids) {
            if (BluetoothUuid.isAvrcpTarget(parcelUuid)) {
                return true;
            }
        }
        return false;
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
