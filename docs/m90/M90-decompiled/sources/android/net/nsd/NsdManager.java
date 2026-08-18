package android.net.nsd;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.AsyncChannel;
import java.util.concurrent.CountDownLatch;

/* JADX INFO: loaded from: classes.dex */
public final class NsdManager {
    public static final String ACTION_NSD_STATE_CHANGED = "android.net.nsd.STATE_CHANGED";
    private static final int BASE = 393216;
    public static final int DISABLE = 393241;
    public static final int DISCOVER_SERVICES = 393217;
    public static final int DISCOVER_SERVICES_FAILED = 393219;
    public static final int DISCOVER_SERVICES_STARTED = 393218;
    public static final int ENABLE = 393240;
    public static final String EXTRA_NSD_STATE = "nsd_state";
    public static final int FAILURE_ALREADY_ACTIVE = 3;
    public static final int FAILURE_INTERNAL_ERROR = 0;
    public static final int FAILURE_MAX_LIMIT = 4;
    private static final int INVALID_LISTENER_KEY = 0;
    public static final int NATIVE_DAEMON_EVENT = 393242;
    public static final int NSD_STATE_DISABLED = 1;
    public static final int NSD_STATE_ENABLED = 2;
    public static final int PROTOCOL_DNS_SD = 1;
    public static final int REGISTER_SERVICE = 393225;
    public static final int REGISTER_SERVICE_FAILED = 393226;
    public static final int REGISTER_SERVICE_SUCCEEDED = 393227;
    public static final int RESOLVE_SERVICE = 393234;
    public static final int RESOLVE_SERVICE_FAILED = 393235;
    public static final int RESOLVE_SERVICE_SUCCEEDED = 393236;
    public static final int SERVICE_FOUND = 393220;
    public static final int SERVICE_LOST = 393221;
    public static final int STOP_DISCOVERY = 393222;
    public static final int STOP_DISCOVERY_FAILED = 393223;
    public static final int STOP_DISCOVERY_SUCCEEDED = 393224;
    private static final String TAG = "NsdManager";
    public static final int UNREGISTER_SERVICE = 393228;
    public static final int UNREGISTER_SERVICE_FAILED = 393229;
    public static final int UNREGISTER_SERVICE_SUCCEEDED = 393230;
    private Context mContext;
    private ServiceHandler mHandler;
    INsdManager mService;
    private int mListenerKey = 1;
    private final SparseArray mListenerMap = new SparseArray();
    private final SparseArray<NsdServiceInfo> mServiceMap = new SparseArray<>();
    private final Object mMapLock = new Object();
    private final AsyncChannel mAsyncChannel = new AsyncChannel();
    private final CountDownLatch mConnected = new CountDownLatch(1);

    public interface DiscoveryListener {
        void onDiscoveryStarted(String str);

        void onDiscoveryStopped(String str);

        void onServiceFound(NsdServiceInfo nsdServiceInfo);

        void onServiceLost(NsdServiceInfo nsdServiceInfo);

        void onStartDiscoveryFailed(String str, int i);

        void onStopDiscoveryFailed(String str, int i);
    }

    public interface RegistrationListener {
        void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i);

        void onServiceRegistered(NsdServiceInfo nsdServiceInfo);

        void onServiceUnregistered(NsdServiceInfo nsdServiceInfo);

        void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i);
    }

    public interface ResolveListener {
        void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i);

        void onServiceResolved(NsdServiceInfo nsdServiceInfo);
    }

    public NsdManager(Context context, INsdManager iNsdManager) {
        this.mService = iNsdManager;
        this.mContext = context;
        init();
    }

    private class ServiceHandler extends Handler {
        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Object listener = NsdManager.this.getListener(message.arg2);
            boolean z = false;
            switch (message.what) {
                case 69632:
                    NsdManager.this.mAsyncChannel.sendMessage(69633);
                    z = true;
                    break;
                case 69634:
                    NsdManager.this.mConnected.countDown();
                    z = true;
                    break;
                case 69636:
                    Log.e(NsdManager.TAG, "Channel lost");
                    z = true;
                    break;
                case NsdManager.DISCOVER_SERVICES_STARTED /* 393218 */:
                    ((DiscoveryListener) listener).onDiscoveryStarted(((NsdServiceInfo) message.obj).getServiceType());
                    break;
                case NsdManager.DISCOVER_SERVICES_FAILED /* 393219 */:
                    ((DiscoveryListener) listener).onStartDiscoveryFailed(NsdManager.this.getNsdService(message.arg2).getServiceType(), message.arg1);
                    z = true;
                    break;
                case NsdManager.SERVICE_FOUND /* 393220 */:
                    ((DiscoveryListener) listener).onServiceFound((NsdServiceInfo) message.obj);
                    break;
                case NsdManager.SERVICE_LOST /* 393221 */:
                    ((DiscoveryListener) listener).onServiceLost((NsdServiceInfo) message.obj);
                    break;
                case NsdManager.STOP_DISCOVERY_FAILED /* 393223 */:
                    ((DiscoveryListener) listener).onStopDiscoveryFailed(NsdManager.this.getNsdService(message.arg2).getServiceType(), message.arg1);
                    z = true;
                    break;
                case NsdManager.STOP_DISCOVERY_SUCCEEDED /* 393224 */:
                    ((DiscoveryListener) listener).onDiscoveryStopped(NsdManager.this.getNsdService(message.arg2).getServiceType());
                    z = true;
                    break;
                case NsdManager.REGISTER_SERVICE_FAILED /* 393226 */:
                    ((RegistrationListener) listener).onRegistrationFailed(NsdManager.this.getNsdService(message.arg2), message.arg1);
                    z = true;
                    break;
                case NsdManager.REGISTER_SERVICE_SUCCEEDED /* 393227 */:
                    ((RegistrationListener) listener).onServiceRegistered((NsdServiceInfo) message.obj);
                    break;
                case NsdManager.UNREGISTER_SERVICE_FAILED /* 393229 */:
                    ((RegistrationListener) listener).onUnregistrationFailed(NsdManager.this.getNsdService(message.arg2), message.arg1);
                    z = true;
                    break;
                case NsdManager.UNREGISTER_SERVICE_SUCCEEDED /* 393230 */:
                    ((RegistrationListener) listener).onServiceUnregistered(NsdManager.this.getNsdService(message.arg2));
                    z = true;
                    break;
                case NsdManager.RESOLVE_SERVICE_FAILED /* 393235 */:
                    ((ResolveListener) listener).onResolveFailed(NsdManager.this.getNsdService(message.arg2), message.arg1);
                    z = true;
                    break;
                case NsdManager.RESOLVE_SERVICE_SUCCEEDED /* 393236 */:
                    ((ResolveListener) listener).onServiceResolved((NsdServiceInfo) message.obj);
                    z = true;
                    break;
                default:
                    Log.d(NsdManager.TAG, "Ignored " + message);
                    z = true;
                    break;
            }
            if (z) {
                NsdManager.this.removeListener(message.arg2);
            }
        }
    }

    private int putListener(Object obj, NsdServiceInfo nsdServiceInfo) {
        int i;
        if (obj == null) {
            return 0;
        }
        synchronized (this.mMapLock) {
            do {
                i = this.mListenerKey;
                this.mListenerKey = i + 1;
            } while (i == 0);
            this.mListenerMap.put(i, obj);
            this.mServiceMap.put(i, nsdServiceInfo);
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Object getListener(int i) {
        Object obj;
        if (i == 0) {
            return null;
        }
        synchronized (this.mMapLock) {
            obj = this.mListenerMap.get(i);
        }
        return obj;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public NsdServiceInfo getNsdService(int i) {
        NsdServiceInfo nsdServiceInfo;
        synchronized (this.mMapLock) {
            nsdServiceInfo = this.mServiceMap.get(i);
        }
        return nsdServiceInfo;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeListener(int i) {
        if (i == 0) {
            return;
        }
        synchronized (this.mMapLock) {
            this.mListenerMap.remove(i);
            this.mServiceMap.remove(i);
        }
    }

    private int getListenerKey(Object obj) {
        synchronized (this.mMapLock) {
            int iIndexOfValue = this.mListenerMap.indexOfValue(obj);
            if (iIndexOfValue == -1) {
                return 0;
            }
            return this.mListenerMap.keyAt(iIndexOfValue);
        }
    }

    private void init() {
        Messenger messenger = getMessenger();
        if (messenger == null) {
            throw new RuntimeException("Failed to initialize");
        }
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        ServiceHandler serviceHandler = new ServiceHandler(handlerThread.getLooper());
        this.mHandler = serviceHandler;
        this.mAsyncChannel.connect(this.mContext, serviceHandler, messenger);
        try {
            this.mConnected.await();
        } catch (InterruptedException unused) {
            Log.e(TAG, "interrupted wait at init");
        }
    }

    public void registerService(NsdServiceInfo nsdServiceInfo, int i, RegistrationListener registrationListener) {
        if (TextUtils.isEmpty(nsdServiceInfo.getServiceName()) || TextUtils.isEmpty(nsdServiceInfo.getServiceType())) {
            throw new IllegalArgumentException("Service name or type cannot be empty");
        }
        if (nsdServiceInfo.getPort() <= 0) {
            throw new IllegalArgumentException("Invalid port number");
        }
        if (registrationListener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        if (i != 1) {
            throw new IllegalArgumentException("Unsupported protocol");
        }
        this.mAsyncChannel.sendMessage(REGISTER_SERVICE, 0, putListener(registrationListener, nsdServiceInfo), nsdServiceInfo);
    }

    public void unregisterService(RegistrationListener registrationListener) {
        int listenerKey = getListenerKey(registrationListener);
        if (listenerKey == 0) {
            throw new IllegalArgumentException("listener not registered");
        }
        if (registrationListener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        this.mAsyncChannel.sendMessage(UNREGISTER_SERVICE, 0, listenerKey);
    }

    public void discoverServices(String str, int i, DiscoveryListener discoveryListener) {
        if (discoveryListener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Service type cannot be empty");
        }
        if (i != 1) {
            throw new IllegalArgumentException("Unsupported protocol");
        }
        NsdServiceInfo nsdServiceInfo = new NsdServiceInfo();
        nsdServiceInfo.setServiceType(str);
        this.mAsyncChannel.sendMessage(DISCOVER_SERVICES, 0, putListener(discoveryListener, nsdServiceInfo), nsdServiceInfo);
    }

    public void stopServiceDiscovery(DiscoveryListener discoveryListener) {
        int listenerKey = getListenerKey(discoveryListener);
        if (listenerKey == 0) {
            throw new IllegalArgumentException("service discovery not active on listener");
        }
        if (discoveryListener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        this.mAsyncChannel.sendMessage(STOP_DISCOVERY, 0, listenerKey);
    }

    public void resolveService(NsdServiceInfo nsdServiceInfo, ResolveListener resolveListener) {
        if (TextUtils.isEmpty(nsdServiceInfo.getServiceName()) || TextUtils.isEmpty(nsdServiceInfo.getServiceType())) {
            throw new IllegalArgumentException("Service name or type cannot be empty");
        }
        if (resolveListener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        this.mAsyncChannel.sendMessage(RESOLVE_SERVICE, 0, putListener(resolveListener, nsdServiceInfo), nsdServiceInfo);
    }

    public void setEnabled(boolean z) {
        try {
            this.mService.setEnabled(z);
        } catch (RemoteException unused) {
        }
    }

    private Messenger getMessenger() {
        try {
            return this.mService.getMessenger();
        } catch (RemoteException unused) {
            return null;
        }
    }
}
