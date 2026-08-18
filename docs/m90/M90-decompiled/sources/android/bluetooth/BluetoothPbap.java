package android.bluetooth;

import android.bluetooth.IBluetoothPbap;
import android.bluetooth.IBluetoothStateChangeCallback;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothPbap {
    private static final boolean DBG = true;
    public static final String PBAP_PREVIOUS_STATE = "android.bluetooth.pbap.intent.PBAP_PREVIOUS_STATE";
    public static final String PBAP_STATE = "android.bluetooth.pbap.intent.PBAP_STATE";
    public static final String PBAP_STATE_CHANGED_ACTION = "android.bluetooth.pbap.intent.action.PBAP_STATE_CHANGED";
    public static final int RESULT_CANCELED = 2;
    public static final int RESULT_FAILURE = 0;
    public static final int RESULT_SUCCESS = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_ERROR = -1;
    private static final String TAG = "BluetoothPbap";
    private static final boolean VDBG = false;
    private BluetoothAdapter mAdapter;
    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback;
    private final ServiceConnection mConnection;
    private final Context mContext;
    private IBluetoothPbap mService;
    private ServiceListener mServiceListener;

    public interface ServiceListener {
        void onServiceConnected(BluetoothPbap bluetoothPbap);

        void onServiceDisconnected();
    }

    public BluetoothPbap(Context context, ServiceListener serviceListener) {
        IBluetoothStateChangeCallback.Stub stub = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothPbap.1
            @Override // android.bluetooth.IBluetoothStateChangeCallback
            public void onBluetoothStateChange(boolean z) {
                Log.d(BluetoothPbap.TAG, "onBluetoothStateChange: up=" + z);
                if (!z) {
                    synchronized (BluetoothPbap.this.mConnection) {
                        try {
                            BluetoothPbap.this.mService = null;
                            BluetoothPbap.this.mContext.unbindService(BluetoothPbap.this.mConnection);
                        } catch (Exception e) {
                            Log.e(BluetoothPbap.TAG, "", e);
                        }
                    }
                    return;
                }
                synchronized (BluetoothPbap.this.mConnection) {
                    try {
                    } catch (Exception e2) {
                        Log.e(BluetoothPbap.TAG, "", e2);
                    }
                    if (BluetoothPbap.this.mService == null) {
                        BluetoothPbap.this.doBind();
                    }
                }
            }
        };
        this.mBluetoothStateChangeCallback = stub;
        this.mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothPbap.2
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                BluetoothPbap.log("Proxy object connected");
                BluetoothPbap.this.mService = IBluetoothPbap.Stub.asInterface(iBinder);
                if (BluetoothPbap.this.mServiceListener != null) {
                    BluetoothPbap.this.mServiceListener.onServiceConnected(BluetoothPbap.this);
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                BluetoothPbap.log("Proxy object disconnected");
                BluetoothPbap.this.mService = null;
                if (BluetoothPbap.this.mServiceListener != null) {
                    BluetoothPbap.this.mServiceListener.onServiceDisconnected();
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
        Intent intent = new Intent(IBluetoothPbap.class.getName());
        ComponentName componentNameResolveSystemService = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(componentNameResolveSystemService);
        if (componentNameResolveSystemService != null && this.mContext.bindServiceAsUser(intent, this.mConnection, 0, Process.myUserHandle())) {
            return true;
        }
        Log.e(TAG, "Could not bind to Bluetooth Pbap Service with " + intent);
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
            java.lang.String r1 = "BluetoothPbap"
            java.lang.String r2 = ""
            android.util.Log.e(r1, r2, r0)     // Catch: java.lang.Throwable -> L39
        L17:
            android.content.ServiceConnection r0 = r5.mConnection     // Catch: java.lang.Throwable -> L39
            monitor-enter(r0)     // Catch: java.lang.Throwable -> L39
            android.bluetooth.IBluetoothPbap r1 = r5.mService     // Catch: java.lang.Throwable -> L36
            r2 = 0
            if (r1 == 0) goto L31
            r5.mService = r2     // Catch: java.lang.Exception -> L29 java.lang.Throwable -> L36
            android.content.Context r1 = r5.mContext     // Catch: java.lang.Exception -> L29 java.lang.Throwable -> L36
            android.content.ServiceConnection r3 = r5.mConnection     // Catch: java.lang.Exception -> L29 java.lang.Throwable -> L36
            r1.unbindService(r3)     // Catch: java.lang.Exception -> L29 java.lang.Throwable -> L36
            goto L31
        L29:
            r1 = move-exception
            java.lang.String r3 = "BluetoothPbap"
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
        throw new UnsupportedOperationException("Method not decompiled: android.bluetooth.BluetoothPbap.close():void");
    }

    public int getState() {
        IBluetoothPbap iBluetoothPbap = this.mService;
        if (iBluetoothPbap != null) {
            try {
                return iBluetoothPbap.getState();
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
        IBluetoothPbap iBluetoothPbap = this.mService;
        if (iBluetoothPbap != null) {
            try {
                return iBluetoothPbap.getClient();
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
        IBluetoothPbap iBluetoothPbap = this.mService;
        if (iBluetoothPbap != null) {
            try {
                return iBluetoothPbap.isConnected(bluetoothDevice);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        log(Log.getStackTraceString(new Throwable()));
        return false;
    }

    public boolean disconnect() {
        log("disconnect()");
        IBluetoothPbap iBluetoothPbap = this.mService;
        if (iBluetoothPbap != null) {
            try {
                iBluetoothPbap.disconnect();
                return true;
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        log(Log.getStackTraceString(new Throwable()));
        return false;
    }

    public static boolean doesClassMatchSink(BluetoothClass bluetoothClass) {
        int deviceClass = bluetoothClass.getDeviceClass();
        return deviceClass == 256 || deviceClass == 260 || deviceClass == 264 || deviceClass == 268;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void log(String str) {
        Log.d(TAG, str);
    }
}
