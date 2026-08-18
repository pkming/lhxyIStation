package android.bluetooth;

import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothAvrcpController;
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
public final class BluetoothAvrcpController implements BluetoothProfile {
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.acrcp-controller.profile.action.CONNECTION_STATE_CHANGED";
    private static final boolean DBG = true;
    private static final String TAG = "BluetoothAvrcpController";
    private static final boolean VDBG = false;
    private BluetoothAdapter mAdapter;
    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback;
    private final ServiceConnection mConnection;
    private Context mContext;
    private IBluetoothAvrcpController mService;
    private BluetoothProfile.ServiceListener mServiceListener;

    BluetoothAvrcpController(Context context, BluetoothProfile.ServiceListener serviceListener) {
        IBluetoothStateChangeCallback.Stub stub = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothAvrcpController.1
            @Override // android.bluetooth.IBluetoothStateChangeCallback
            public void onBluetoothStateChange(boolean z) {
                Log.d(BluetoothAvrcpController.TAG, "onBluetoothStateChange: up=" + z);
                if (!z) {
                    synchronized (BluetoothAvrcpController.this.mConnection) {
                        try {
                            BluetoothAvrcpController.this.mService = null;
                            BluetoothAvrcpController.this.mContext.unbindService(BluetoothAvrcpController.this.mConnection);
                        } catch (Exception e) {
                            Log.e(BluetoothAvrcpController.TAG, "", e);
                        }
                    }
                    return;
                }
                synchronized (BluetoothAvrcpController.this.mConnection) {
                    try {
                    } catch (Exception e2) {
                        Log.e(BluetoothAvrcpController.TAG, "", e2);
                    }
                    if (BluetoothAvrcpController.this.mService == null) {
                        BluetoothAvrcpController.this.doBind();
                    }
                }
            }
        };
        this.mBluetoothStateChangeCallback = stub;
        this.mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothAvrcpController.2
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d(BluetoothAvrcpController.TAG, "Proxy object connected");
                BluetoothAvrcpController.this.mService = IBluetoothAvrcpController.Stub.asInterface(iBinder);
                if (BluetoothAvrcpController.this.mServiceListener != null) {
                    BluetoothAvrcpController.this.mServiceListener.onServiceConnected(11, BluetoothAvrcpController.this);
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d(BluetoothAvrcpController.TAG, "Proxy object disconnected");
                BluetoothAvrcpController.this.mService = null;
                if (BluetoothAvrcpController.this.mServiceListener != null) {
                    BluetoothAvrcpController.this.mServiceListener.onServiceDisconnected(11);
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
        Intent intent = new Intent(IBluetoothAvrcpController.class.getName());
        ComponentName componentNameResolveSystemService = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(componentNameResolveSystemService);
        if (componentNameResolveSystemService != null && this.mContext.bindServiceAsUser(intent, this.mConnection, 0, Process.myUserHandle())) {
            return true;
        }
        Log.e(TAG, "Could not bind to Bluetooth AVRCP Controller Service with " + intent);
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

    public void sendPassThroughCmd(BluetoothDevice bluetoothDevice, int i, int i2) {
        Log.d(TAG, "sendPassThroughCmd");
        if (this.mService != null && isEnabled()) {
            try {
                this.mService.sendPassThroughCmd(bluetoothDevice, i, i2);
                return;
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in sendPassThroughCmd()", e);
                return;
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
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
