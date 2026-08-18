package android.bluetooth;

import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothMap;
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
public final class BluetoothMap implements BluetoothProfile {
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.map.profile.action.CONNECTION_STATE_CHANGED";
    private static final boolean DBG = true;
    public static final int RESULT_CANCELED = 2;
    public static final int RESULT_FAILURE = 0;
    public static final int RESULT_SUCCESS = 1;
    public static final int STATE_ERROR = -1;
    private static final String TAG = "BluetoothMap";
    private static final boolean VDBG = false;
    private BluetoothAdapter mAdapter;
    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback;
    private final ServiceConnection mConnection;
    private final Context mContext;
    private IBluetoothMap mService;
    private BluetoothProfile.ServiceListener mServiceListener;

    BluetoothMap(Context context, BluetoothProfile.ServiceListener serviceListener) {
        IBluetoothStateChangeCallback.Stub stub = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothMap.1
            @Override // android.bluetooth.IBluetoothStateChangeCallback
            public void onBluetoothStateChange(boolean z) {
                Log.d(BluetoothMap.TAG, "onBluetoothStateChange: up=" + z);
                if (!z) {
                    synchronized (BluetoothMap.this.mConnection) {
                        try {
                            BluetoothMap.this.mService = null;
                            BluetoothMap.this.mContext.unbindService(BluetoothMap.this.mConnection);
                        } catch (Exception e) {
                            Log.e(BluetoothMap.TAG, "", e);
                        }
                    }
                    return;
                }
                synchronized (BluetoothMap.this.mConnection) {
                    try {
                    } catch (Exception e2) {
                        Log.e(BluetoothMap.TAG, "", e2);
                    }
                    if (BluetoothMap.this.mService == null) {
                        BluetoothMap.this.doBind();
                    }
                }
            }
        };
        this.mBluetoothStateChangeCallback = stub;
        this.mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothMap.2
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                BluetoothMap.log("Proxy object connected");
                BluetoothMap.this.mService = IBluetoothMap.Stub.asInterface(iBinder);
                if (BluetoothMap.this.mServiceListener != null) {
                    BluetoothMap.this.mServiceListener.onServiceConnected(9, BluetoothMap.this);
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                BluetoothMap.log("Proxy object disconnected");
                BluetoothMap.this.mService = null;
                if (BluetoothMap.this.mServiceListener != null) {
                    BluetoothMap.this.mServiceListener.onServiceDisconnected(9);
                }
            }
        };
        Log.d(TAG, "Create BluetoothMap proxy object");
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
        Intent intent = new Intent(IBluetoothMap.class.getName());
        ComponentName componentNameResolveSystemService = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(componentNameResolveSystemService);
        if (componentNameResolveSystemService != null && this.mContext.bindServiceAsUser(intent, this.mConnection, 0, Process.myUserHandle())) {
            return true;
        }
        Log.e(TAG, "Could not bind to Bluetooth MAP Service with " + intent);
        return false;
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x001a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized void close() {
        /*
            r5 = this;
            monitor-enter(r5)
            android.bluetooth.BluetoothAdapter r0 = r5.mAdapter     // Catch: java.lang.Throwable -> L39
            android.bluetooth.IBluetoothManager r0 = r0.getBluetoothManager()     // Catch: java.lang.Throwable -> L39
            if (r0 == 0) goto L17
            android.bluetooth.IBluetoothStateChangeCallback r1 = r5.mBluetoothStateChangeCallback     // Catch: java.lang.Exception -> Lf java.lang.Throwable -> L39
            r0.unregisterStateChangeCallback(r1)     // Catch: java.lang.Exception -> Lf java.lang.Throwable -> L39
            goto L17
        Lf:
            r0 = move-exception
            java.lang.String r1 = "BluetoothMap"
            java.lang.String r2 = ""
            android.util.Log.e(r1, r2, r0)     // Catch: java.lang.Throwable -> L39
        L17:
            android.content.ServiceConnection r0 = r5.mConnection     // Catch: java.lang.Throwable -> L39
            monitor-enter(r0)     // Catch: java.lang.Throwable -> L39
            android.bluetooth.IBluetoothMap r1 = r5.mService     // Catch: java.lang.Throwable -> L36
            r2 = 0
            if (r1 == 0) goto L31
            r5.mService = r2     // Catch: java.lang.Exception -> L29 java.lang.Throwable -> L36
            android.content.Context r1 = r5.mContext     // Catch: java.lang.Exception -> L29 java.lang.Throwable -> L36
            android.content.ServiceConnection r3 = r5.mConnection     // Catch: java.lang.Exception -> L29 java.lang.Throwable -> L36
            r1.unbindService(r3)     // Catch: java.lang.Exception -> L29 java.lang.Throwable -> L36
            goto L31
        L29:
            r1 = move-exception
            java.lang.String r3 = "BluetoothMap"
            java.lang.String r4 = ""
            android.util.Log.e(r3, r4, r1)     // Catch: java.lang.Throwable -> L36
        L31:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L36
            r5.mServiceListener = r2     // Catch: java.lang.Throwable -> L39
            monitor-exit(r5)
            return
        L36:
            r1 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L36
            throw r1     // Catch: java.lang.Throwable -> L39
        L39:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.bluetooth.BluetoothMap.close():void");
    }

    public int getState() {
        IBluetoothMap iBluetoothMap = this.mService;
        if (iBluetoothMap != null) {
            try {
                return iBluetoothMap.getState();
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return -1;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        log(Log.getStackTraceString(new Throwable()));
        return -1;
    }

    public BluetoothDevice getClient() {
        IBluetoothMap iBluetoothMap = this.mService;
        if (iBluetoothMap != null) {
            try {
                return iBluetoothMap.getClient();
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return null;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        log(Log.getStackTraceString(new Throwable()));
        return null;
    }

    public boolean isConnected(BluetoothDevice bluetoothDevice) {
        IBluetoothMap iBluetoothMap = this.mService;
        if (iBluetoothMap != null) {
            try {
                return iBluetoothMap.isConnected(bluetoothDevice);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        log(Log.getStackTraceString(new Throwable()));
        return false;
    }

    public boolean connect(BluetoothDevice bluetoothDevice) {
        log("connect(" + bluetoothDevice + ")not supported for MAPS");
        return false;
    }

    public boolean disconnect(BluetoothDevice bluetoothDevice) {
        log("disconnect(" + bluetoothDevice + ")");
        if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return this.mService.disconnect(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return false;
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return false;
    }

    public static boolean doesClassMatchSink(BluetoothClass bluetoothClass) {
        int deviceClass = bluetoothClass.getDeviceClass();
        return deviceClass == 256 || deviceClass == 260 || deviceClass == 264 || deviceClass == 268;
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getConnectedDevices() {
        log("getConnectedDevices()");
        if (this.mService != null && isEnabled()) {
            try {
                return this.mService.getConnectedDevices();
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
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
        log("getDevicesMatchingStates()");
        if (this.mService != null && isEnabled()) {
            try {
                return this.mService.getDevicesMatchingConnectionStates(iArr);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
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
        log("getConnectionState(" + bluetoothDevice + ")");
        if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return this.mService.getConnectionState(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
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
            Log.e(TAG, Log.getStackTraceString(new Throwable()));
            return false;
        }
    }

    public int getPriority(BluetoothDevice bluetoothDevice) {
        if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return this.mService.getPriority(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return 0;
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void log(String str) {
        Log.d(TAG, str);
    }

    private boolean isEnabled() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && defaultAdapter.getState() == 12) {
            return true;
        }
        log("Bluetooth is Not enabled");
        return false;
    }

    private boolean isValidDevice(BluetoothDevice bluetoothDevice) {
        return bluetoothDevice != null && BluetoothAdapter.checkBluetoothAddress(bluetoothDevice.getAddress());
    }
}
