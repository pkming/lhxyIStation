package android.media;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public final class MediaDrm {
    private static final int DRM_EVENT = 200;
    public static final int EVENT_KEY_EXPIRED = 3;
    public static final int EVENT_KEY_REQUIRED = 2;
    public static final int EVENT_PROVISION_REQUIRED = 1;
    public static final int EVENT_VENDOR_DEFINED = 4;
    public static final int KEY_TYPE_OFFLINE = 2;
    public static final int KEY_TYPE_RELEASE = 3;
    public static final int KEY_TYPE_STREAMING = 1;
    public static final String PROPERTY_ALGORITHMS = "algorithms";
    public static final String PROPERTY_DESCRIPTION = "description";
    public static final String PROPERTY_DEVICE_UNIQUE_ID = "deviceUniqueId";
    public static final String PROPERTY_VENDOR = "vendor";
    public static final String PROPERTY_VERSION = "version";
    private static final String TAG = "MediaDrm";
    private EventHandler mEventHandler;
    private int mNativeContext;
    private OnEventListener mOnEventListener;

    public interface OnEventListener {
        void onEvent(MediaDrm mediaDrm, byte[] bArr, int i, int i2, byte[] bArr2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final native byte[] decryptNative(MediaDrm mediaDrm, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native byte[] encryptNative(MediaDrm mediaDrm, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4);

    private static final native boolean isCryptoSchemeSupportedNative(byte[] bArr, String str);

    private final native void native_finalize();

    private static final native void native_init();

    private final native void native_setup(Object obj, byte[] bArr);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native void setCipherAlgorithmNative(MediaDrm mediaDrm, byte[] bArr, String str);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native void setMacAlgorithmNative(MediaDrm mediaDrm, byte[] bArr, String str);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native byte[] signNative(MediaDrm mediaDrm, byte[] bArr, byte[] bArr2, byte[] bArr3);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native boolean verifyNative(MediaDrm mediaDrm, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4);

    public native void closeSession(byte[] bArr);

    public native KeyRequest getKeyRequest(byte[] bArr, byte[] bArr2, String str, int i, HashMap<String, String> map) throws NotProvisionedException;

    public native byte[] getPropertyByteArray(String str);

    public native String getPropertyString(String str);

    public native ProvisionRequest getProvisionRequest();

    public native List<byte[]> getSecureStops();

    public native byte[] openSession() throws NotProvisionedException;

    public native byte[] provideKeyResponse(byte[] bArr, byte[] bArr2) throws DeniedByServerException, NotProvisionedException;

    public native void provideProvisionResponse(byte[] bArr) throws DeniedByServerException;

    public native HashMap<String, String> queryKeyStatus(byte[] bArr);

    public final native void release();

    public native void releaseSecureStops(byte[] bArr);

    public native void removeKeys(byte[] bArr);

    public native void restoreKeys(byte[] bArr, byte[] bArr2);

    public native void setPropertyByteArray(String str, byte[] bArr);

    public native void setPropertyString(String str, String str2);

    public static final boolean isCryptoSchemeSupported(UUID uuid) {
        return isCryptoSchemeSupportedNative(getByteArrayFromUUID(uuid), null);
    }

    public static final boolean isCryptoSchemeSupported(UUID uuid, String str) {
        return isCryptoSchemeSupportedNative(getByteArrayFromUUID(uuid), str);
    }

    private static final byte[] getByteArrayFromUUID(UUID uuid) {
        long mostSignificantBits = uuid.getMostSignificantBits();
        long leastSignificantBits = uuid.getLeastSignificantBits();
        byte[] bArr = new byte[16];
        for (int i = 0; i < 8; i++) {
            int i2 = (7 - i) * 8;
            bArr[i] = (byte) (mostSignificantBits >>> i2);
            bArr[i + 8] = (byte) (leastSignificantBits >>> i2);
        }
        return bArr;
    }

    public MediaDrm(UUID uuid) throws UnsupportedSchemeException {
        Looper looperMyLooper = Looper.myLooper();
        if (looperMyLooper != null) {
            this.mEventHandler = new EventHandler(this, looperMyLooper);
        } else {
            Looper mainLooper = Looper.getMainLooper();
            if (mainLooper != null) {
                this.mEventHandler = new EventHandler(this, mainLooper);
            } else {
                this.mEventHandler = null;
            }
        }
        native_setup(new WeakReference(this), getByteArrayFromUUID(uuid));
    }

    public void setOnEventListener(OnEventListener onEventListener) {
        this.mOnEventListener = onEventListener;
    }

    private class EventHandler extends Handler {
        private MediaDrm mMediaDrm;

        public EventHandler(MediaDrm mediaDrm, Looper looper) {
            super(looper);
            this.mMediaDrm = mediaDrm;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (this.mMediaDrm.mNativeContext == 0) {
                Log.w(MediaDrm.TAG, "MediaDrm went away with unhandled events");
                return;
            }
            if (message.what == 200) {
                Log.i(MediaDrm.TAG, "Drm event (" + message.arg1 + "," + message.arg2 + ")");
                if (MediaDrm.this.mOnEventListener == null || message.obj == null || !(message.obj instanceof Parcel)) {
                    return;
                }
                Parcel parcel = (Parcel) message.obj;
                byte[] bArrCreateByteArray = parcel.createByteArray();
                byte[] bArr = bArrCreateByteArray.length == 0 ? null : bArrCreateByteArray;
                byte[] bArrCreateByteArray2 = parcel.createByteArray();
                MediaDrm.this.mOnEventListener.onEvent(this.mMediaDrm, bArr, message.arg1, message.arg2, bArrCreateByteArray2.length == 0 ? null : bArrCreateByteArray2);
                return;
            }
            Log.e(MediaDrm.TAG, "Unknown message type " + message.what);
        }
    }

    private static void postEventFromNative(Object obj, int i, int i2, Object obj2) {
        EventHandler eventHandler;
        MediaDrm mediaDrm = (MediaDrm) ((WeakReference) obj).get();
        if (mediaDrm == null || (eventHandler = mediaDrm.mEventHandler) == null) {
            return;
        }
        mediaDrm.mEventHandler.sendMessage(eventHandler.obtainMessage(200, i, i2, obj2));
    }

    public static final class KeyRequest {
        private byte[] mData;
        private String mDefaultUrl;

        KeyRequest() {
        }

        public byte[] getData() {
            return this.mData;
        }

        public String getDefaultUrl() {
            return this.mDefaultUrl;
        }
    }

    public static final class ProvisionRequest {
        private byte[] mData;
        private String mDefaultUrl;

        ProvisionRequest() {
        }

        public byte[] getData() {
            return this.mData;
        }

        public String getDefaultUrl() {
            return this.mDefaultUrl;
        }
    }

    public final class CryptoSession {
        private MediaDrm mDrm;
        private byte[] mSessionId;

        CryptoSession(MediaDrm mediaDrm, byte[] bArr, String str, String str2) {
            this.mSessionId = bArr;
            this.mDrm = mediaDrm;
            MediaDrm.setCipherAlgorithmNative(mediaDrm, bArr, str);
            MediaDrm.setMacAlgorithmNative(mediaDrm, bArr, str2);
        }

        public byte[] encrypt(byte[] bArr, byte[] bArr2, byte[] bArr3) {
            return MediaDrm.encryptNative(this.mDrm, this.mSessionId, bArr, bArr2, bArr3);
        }

        public byte[] decrypt(byte[] bArr, byte[] bArr2, byte[] bArr3) {
            return MediaDrm.decryptNative(this.mDrm, this.mSessionId, bArr, bArr2, bArr3);
        }

        public byte[] sign(byte[] bArr, byte[] bArr2) {
            return MediaDrm.signNative(this.mDrm, this.mSessionId, bArr, bArr2);
        }

        public boolean verify(byte[] bArr, byte[] bArr2, byte[] bArr3) {
            return MediaDrm.verifyNative(this.mDrm, this.mSessionId, bArr, bArr2, bArr3);
        }
    }

    public CryptoSession getCryptoSession(byte[] bArr, String str, String str2) {
        return new CryptoSession(this, bArr, str, str2);
    }

    protected void finalize() {
        native_finalize();
    }

    static {
        System.loadLibrary("media_jni");
        native_init();
    }
}
