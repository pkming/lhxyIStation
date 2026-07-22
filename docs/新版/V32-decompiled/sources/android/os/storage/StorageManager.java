package android.os.storage;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.os.storage.IMountServiceListener;
import android.os.storage.IObbActionListener;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.Preconditions;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes.dex */
public class StorageManager {
    private static final long DEFAULT_FULL_THRESHOLD_BYTES = 1048576;
    private static final long DEFAULT_THRESHOLD_MAX_BYTES = 524288000;
    private static final int DEFAULT_THRESHOLD_PERCENTAGE = 10;
    private static final String TAG = "StorageManager";
    private MountServiceBinderListener mBinderListener;
    private final IMountService mMountService;
    private final ContentResolver mResolver;
    private final Looper mTgtLooper;
    private List<ListenerDelegate> mListeners = new ArrayList();
    private final AtomicInteger mNextNonce = new AtomicInteger(0);
    private final ObbActionListener mObbActionListener = new ObbActionListener();

    private class MountServiceBinderListener extends IMountServiceListener.Stub {
        private MountServiceBinderListener() {
        }

        @Override // android.os.storage.IMountServiceListener
        public void onUsbMassStorageConnectionChanged(boolean z) {
            int size = StorageManager.this.mListeners.size();
            for (int i = 0; i < size; i++) {
                ((ListenerDelegate) StorageManager.this.mListeners.get(i)).sendShareAvailabilityChanged(z);
            }
        }

        @Override // android.os.storage.IMountServiceListener
        public void onStorageStateChanged(String str, String str2, String str3) {
            int size = StorageManager.this.mListeners.size();
            for (int i = 0; i < size; i++) {
                ((ListenerDelegate) StorageManager.this.mListeners.get(i)).sendStorageStateChanged(str, str2, str3);
            }
        }
    }

    private class ObbActionListener extends IObbActionListener.Stub {
        private SparseArray<ObbListenerDelegate> mListeners;

        private ObbActionListener() {
            this.mListeners = new SparseArray<>();
        }

        @Override // android.os.storage.IObbActionListener
        public void onObbResult(String str, int i, int i2) {
            ObbListenerDelegate obbListenerDelegate;
            synchronized (this.mListeners) {
                obbListenerDelegate = this.mListeners.get(i);
                if (obbListenerDelegate != null) {
                    this.mListeners.remove(i);
                }
            }
            if (obbListenerDelegate != null) {
                obbListenerDelegate.sendObbStateChanged(str, i2);
            }
        }

        public int addListener(OnObbStateChangeListener onObbStateChangeListener) {
            ObbListenerDelegate obbListenerDelegate = StorageManager.this.new ObbListenerDelegate(onObbStateChangeListener);
            synchronized (this.mListeners) {
                this.mListeners.put(obbListenerDelegate.nonce, obbListenerDelegate);
            }
            return obbListenerDelegate.nonce;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getNextNonce() {
        return this.mNextNonce.getAndIncrement();
    }

    private class ObbListenerDelegate {
        private final Handler mHandler;
        private final WeakReference<OnObbStateChangeListener> mObbEventListenerRef;
        private final int nonce;

        ObbListenerDelegate(OnObbStateChangeListener onObbStateChangeListener) {
            this.nonce = StorageManager.this.getNextNonce();
            this.mObbEventListenerRef = new WeakReference<>(onObbStateChangeListener);
            this.mHandler = new Handler(StorageManager.this.mTgtLooper) { // from class: android.os.storage.StorageManager.ObbListenerDelegate.1
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    OnObbStateChangeListener listener = ObbListenerDelegate.this.getListener();
                    if (listener == null) {
                        return;
                    }
                    StorageEvent storageEvent = (StorageEvent) message.obj;
                    if (message.what == 3) {
                        ObbStateChangedStorageEvent obbStateChangedStorageEvent = (ObbStateChangedStorageEvent) storageEvent;
                        listener.onObbStateChange(obbStateChangedStorageEvent.path, obbStateChangedStorageEvent.state);
                    } else {
                        Log.e(StorageManager.TAG, "Unsupported event " + message.what);
                    }
                }
            };
        }

        OnObbStateChangeListener getListener() {
            WeakReference<OnObbStateChangeListener> weakReference = this.mObbEventListenerRef;
            if (weakReference == null) {
                return null;
            }
            return weakReference.get();
        }

        void sendObbStateChanged(String str, int i) {
            this.mHandler.sendMessage(StorageManager.this.new ObbStateChangedStorageEvent(str, i).getMessage());
        }
    }

    private class ObbStateChangedStorageEvent extends StorageEvent {
        public final String path;
        public final int state;

        public ObbStateChangedStorageEvent(String str, int i) {
            super(3);
            this.path = str;
            this.state = i;
        }
    }

    private class StorageEvent {
        static final int EVENT_OBB_STATE_CHANGED = 3;
        static final int EVENT_STORAGE_STATE_CHANGED = 2;
        static final int EVENT_UMS_CONNECTION_CHANGED = 1;
        private Message mMessage;

        public StorageEvent(int i) {
            Message messageObtain = Message.obtain();
            this.mMessage = messageObtain;
            messageObtain.what = i;
            this.mMessage.obj = this;
        }

        public Message getMessage() {
            return this.mMessage;
        }
    }

    private class UmsConnectionChangedStorageEvent extends StorageEvent {
        public boolean available;

        public UmsConnectionChangedStorageEvent(boolean z) {
            super(1);
            this.available = z;
        }
    }

    private class StorageStateChangedStorageEvent extends StorageEvent {
        public String newState;
        public String oldState;
        public String path;

        public StorageStateChangedStorageEvent(String str, String str2, String str3) {
            super(2);
            this.path = str;
            this.oldState = str2;
            this.newState = str3;
        }
    }

    private class ListenerDelegate {
        private final Handler mHandler;
        final StorageEventListener mStorageEventListener;

        ListenerDelegate(StorageEventListener storageEventListener) {
            this.mStorageEventListener = storageEventListener;
            this.mHandler = new Handler(StorageManager.this.mTgtLooper) { // from class: android.os.storage.StorageManager.ListenerDelegate.1
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    StorageEvent storageEvent = (StorageEvent) message.obj;
                    if (message.what == 1) {
                        ListenerDelegate.this.mStorageEventListener.onUsbMassStorageConnectionChanged(((UmsConnectionChangedStorageEvent) storageEvent).available);
                    } else if (message.what == 2) {
                        StorageStateChangedStorageEvent storageStateChangedStorageEvent = (StorageStateChangedStorageEvent) storageEvent;
                        ListenerDelegate.this.mStorageEventListener.onStorageStateChanged(storageStateChangedStorageEvent.path, storageStateChangedStorageEvent.oldState, storageStateChangedStorageEvent.newState);
                    } else {
                        Log.e(StorageManager.TAG, "Unsupported event " + message.what);
                    }
                }
            };
        }

        StorageEventListener getListener() {
            return this.mStorageEventListener;
        }

        void sendShareAvailabilityChanged(boolean z) {
            this.mHandler.sendMessage(StorageManager.this.new UmsConnectionChangedStorageEvent(z).getMessage());
        }

        void sendStorageStateChanged(String str, String str2, String str3) {
            this.mHandler.sendMessage(StorageManager.this.new StorageStateChangedStorageEvent(str, str2, str3).getMessage());
        }
    }

    public static StorageManager from(Context context) {
        return (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
    }

    public StorageManager(ContentResolver contentResolver, Looper looper) throws RemoteException {
        this.mResolver = contentResolver;
        this.mTgtLooper = looper;
        IMountService iMountServiceAsInterface = IMountService.Stub.asInterface(ServiceManager.getService("mount"));
        this.mMountService = iMountServiceAsInterface;
        if (iMountServiceAsInterface == null) {
            Log.e(TAG, "Unable to connect to mount service! - is it running yet?");
        }
    }

    public void registerListener(StorageEventListener storageEventListener) {
        if (storageEventListener == null) {
            return;
        }
        synchronized (this.mListeners) {
            if (this.mBinderListener == null) {
                try {
                    MountServiceBinderListener mountServiceBinderListener = new MountServiceBinderListener();
                    this.mBinderListener = mountServiceBinderListener;
                    this.mMountService.registerListener(mountServiceBinderListener);
                } catch (RemoteException unused) {
                    Log.e(TAG, "Register mBinderListener failed");
                    return;
                }
            }
            this.mListeners.add(new ListenerDelegate(storageEventListener));
        }
    }

    public void unregisterListener(StorageEventListener storageEventListener) {
        MountServiceBinderListener mountServiceBinderListener;
        if (storageEventListener == null) {
            return;
        }
        synchronized (this.mListeners) {
            int size = this.mListeners.size();
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                }
                if (this.mListeners.get(i).getListener() == storageEventListener) {
                    this.mListeners.remove(i);
                    break;
                }
                i++;
            }
            if (this.mListeners.size() == 0 && (mountServiceBinderListener = this.mBinderListener) != null) {
                try {
                    this.mMountService.unregisterListener(mountServiceBinderListener);
                } catch (RemoteException unused) {
                    Log.e(TAG, "Unregister mBinderListener failed");
                }
            }
        }
    }

    public void enableUsbMassStorage() {
        try {
            this.mMountService.setUsbMassStorageEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "Failed to enable UMS", e);
        }
    }

    public void disableUsbMassStorage() {
        try {
            this.mMountService.setUsbMassStorageEnabled(false);
        } catch (Exception e) {
            Log.e(TAG, "Failed to disable UMS", e);
        }
    }

    public boolean isUsbMassStorageConnected() {
        try {
            return this.mMountService.isUsbMassStorageConnected();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get UMS connection state", e);
            return false;
        }
    }

    public boolean isUsbMassStorageEnabled() {
        try {
            return this.mMountService.isUsbMassStorageEnabled();
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to get UMS enable state", e);
            return false;
        }
    }

    public boolean mountObb(String str, String str2, OnObbStateChangeListener onObbStateChangeListener) {
        Preconditions.checkNotNull(str, "rawPath cannot be null");
        Preconditions.checkNotNull(onObbStateChangeListener, "listener cannot be null");
        try {
            this.mMountService.mountObb(str, new File(str).getCanonicalPath(), str2, this.mObbActionListener, this.mObbActionListener.addListener(onObbStateChangeListener));
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to mount OBB", e);
            return false;
        } catch (IOException e2) {
            throw new IllegalArgumentException("Failed to resolve path: " + str, e2);
        }
    }

    public boolean unmountObb(String str, boolean z, OnObbStateChangeListener onObbStateChangeListener) {
        Preconditions.checkNotNull(str, "rawPath cannot be null");
        Preconditions.checkNotNull(onObbStateChangeListener, "listener cannot be null");
        try {
            this.mMountService.unmountObb(str, z, this.mObbActionListener, this.mObbActionListener.addListener(onObbStateChangeListener));
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to mount OBB", e);
            return false;
        }
    }

    public boolean isObbMounted(String str) {
        Preconditions.checkNotNull(str, "rawPath cannot be null");
        try {
            return this.mMountService.isObbMounted(str);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to check if OBB is mounted", e);
            return false;
        }
    }

    public String getMountedObbPath(String str) {
        Preconditions.checkNotNull(str, "rawPath cannot be null");
        try {
            return this.mMountService.getMountedObbPath(str);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to find mounted path for OBB", e);
            return null;
        }
    }

    public String getVolumeState(String str) {
        IMountService iMountService = this.mMountService;
        if (iMountService == null) {
            return Environment.MEDIA_REMOVED;
        }
        try {
            return iMountService.getVolumeState(str);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to get volume state", e);
            return null;
        }
    }

    public StorageVolume[] getVolumeList() {
        IMountService iMountService = this.mMountService;
        if (iMountService == null) {
            return new StorageVolume[0];
        }
        try {
            StorageVolume[] volumeList = iMountService.getVolumeList();
            if (volumeList == null) {
                return new StorageVolume[0];
            }
            int length = volumeList.length;
            StorageVolume[] storageVolumeArr = new StorageVolume[length];
            for (int i = 0; i < length; i++) {
                storageVolumeArr[i] = volumeList[i];
            }
            return storageVolumeArr;
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to get volume list", e);
            return null;
        }
    }

    public String[] getVolumePaths() {
        StorageVolume[] volumeList = getVolumeList();
        if (volumeList == null) {
            return null;
        }
        int length = volumeList.length;
        String[] strArr = new String[length];
        for (int i = 0; i < length; i++) {
            strArr[i] = volumeList[i].getPath();
        }
        return strArr;
    }

    public StorageVolume getPrimaryVolume() {
        return getPrimaryVolume(getVolumeList());
    }

    public static StorageVolume getPrimaryVolume(StorageVolume[] storageVolumeArr) {
        for (StorageVolume storageVolume : storageVolumeArr) {
            if (storageVolume.isPrimary()) {
                return storageVolume;
            }
        }
        Log.w(TAG, "No primary storage defined");
        return null;
    }

    public long getStorageLowBytes(File file) {
        return Math.min((file.getTotalSpace() * ((long) Settings.Global.getInt(this.mResolver, Settings.Global.SYS_STORAGE_THRESHOLD_PERCENTAGE, 10))) / 100, Settings.Global.getLong(this.mResolver, Settings.Global.SYS_STORAGE_THRESHOLD_MAX_BYTES, DEFAULT_THRESHOLD_MAX_BYTES));
    }

    public long getStorageFullBytes(File file) {
        return Settings.Global.getLong(this.mResolver, Settings.Global.SYS_STORAGE_FULL_THRESHOLD_BYTES, 1048576L);
    }
}
