package android.media;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public class AudioRecord {
    private static final int AUDIORECORD_ERROR_SETUP_INVALIDCHANNELMASK = -17;
    private static final int AUDIORECORD_ERROR_SETUP_INVALIDFORMAT = -18;
    private static final int AUDIORECORD_ERROR_SETUP_INVALIDSOURCE = -19;
    private static final int AUDIORECORD_ERROR_SETUP_NATIVEINITFAILED = -20;
    private static final int AUDIORECORD_ERROR_SETUP_ZEROFRAMECOUNT = -16;
    public static final int ERROR = -1;
    public static final int ERROR_BAD_VALUE = -2;
    public static final int ERROR_INVALID_OPERATION = -3;
    private static final int NATIVE_EVENT_MARKER = 2;
    private static final int NATIVE_EVENT_NEW_POS = 3;
    public static final int RECORDSTATE_RECORDING = 3;
    public static final int RECORDSTATE_STOPPED = 1;
    public static final int STATE_INITIALIZED = 1;
    public static final int STATE_UNINITIALIZED = 0;
    public static final int SUCCESS = 0;
    private static final String TAG = "android.media.AudioRecord";
    private int mAudioFormat;
    private int mChannelCount;
    private int mChannelMask;
    private Looper mInitializationLooper;
    private int mNativeCallbackCookie;
    private int mNativeRecorderInJavaObj;
    private int mRecordSource;
    private int mRecordingState;
    private int mSampleRate;
    private int mSessionId;
    private int mState;
    private final Object mRecordingStateLock = new Object();
    private OnRecordPositionUpdateListener mPositionListener = null;
    private final Object mPositionListenerLock = new Object();
    private NativeEventHandler mEventHandler = null;
    private int mNativeBufferSizeInBytes = 0;

    public interface OnRecordPositionUpdateListener {
        void onMarkerReached(AudioRecord audioRecord);

        void onPeriodicNotification(AudioRecord audioRecord);
    }

    private final native void native_finalize();

    private final native int native_get_marker_pos();

    private static final native int native_get_min_buff_size(int i, int i2, int i3);

    private final native int native_get_pos_update_period();

    private final native int native_read_in_byte_array(byte[] bArr, int i, int i2);

    private final native int native_read_in_direct_buffer(Object obj, int i);

    private final native int native_read_in_short_array(short[] sArr, int i, int i2);

    private final native void native_release();

    private final native int native_set_marker_pos(int i);

    private final native int native_set_pos_update_period(int i);

    private final native int native_setup(Object obj, int i, int i2, int i3, int i4, int i5, int[] iArr);

    private final native int native_start(int i, int i2);

    private final native void native_stop();

    public AudioRecord(int i, int i2, int i3, int i4, int i5) throws IllegalArgumentException {
        this.mState = 0;
        this.mRecordingState = 1;
        this.mInitializationLooper = null;
        this.mSessionId = 0;
        this.mRecordingState = 1;
        Looper looperMyLooper = Looper.myLooper();
        this.mInitializationLooper = looperMyLooper;
        if (looperMyLooper == null) {
            this.mInitializationLooper = Looper.getMainLooper();
        }
        audioParamCheck(i, i2, i3, i4);
        audioBuffSizeCheck(i5);
        int[] iArr = {0};
        int iNative_setup = native_setup(new WeakReference(this), this.mRecordSource, this.mSampleRate, this.mChannelMask, this.mAudioFormat, this.mNativeBufferSizeInBytes, iArr);
        if (iNative_setup != 0) {
            loge("Error code " + iNative_setup + " when initializing native AudioRecord object.");
        } else {
            this.mSessionId = iArr[0];
            this.mState = 1;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x0040  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void audioParamCheck(int r4, int r5, int r6, int r7) {
        /*
            r3 = this;
            if (r4 < 0) goto L72
            int r0 = android.media.MediaRecorder.getAudioSourceMax()
            if (r4 <= r0) goto Lc
            r0 = 1999(0x7cf, float:2.801E-42)
            if (r4 != r0) goto L72
        Lc:
            r3.mRecordSource = r4
            r4 = 4000(0xfa0, float:5.605E-42)
            if (r5 < r4) goto L59
            r4 = 48000(0xbb80, float:6.7262E-41)
            if (r5 > r4) goto L59
            r3.mSampleRate = r5
            r4 = 16
            r5 = 3
            r0 = 1
            r1 = 2
            if (r6 == r0) goto L40
            if (r6 == r1) goto L40
            r2 = 12
            if (r6 == r5) goto L3b
            if (r6 == r2) goto L3b
            if (r6 == r4) goto L40
            r4 = 48
            if (r6 != r4) goto L33
            r3.mChannelCount = r1
            r3.mChannelMask = r6
            goto L44
        L33:
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException
            java.lang.String r5 = "Unsupported channel configuration."
            r4.<init>(r5)
            throw r4
        L3b:
            r3.mChannelCount = r1
            r3.mChannelMask = r2
            goto L44
        L40:
            r3.mChannelCount = r0
            r3.mChannelMask = r4
        L44:
            if (r7 == r0) goto L56
            if (r7 == r1) goto L53
            if (r7 != r5) goto L4b
            goto L53
        L4b:
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException
            java.lang.String r5 = "Unsupported sample encoding. Should be ENCODING_PCM_8BIT or ENCODING_PCM_16BIT."
            r4.<init>(r5)
            throw r4
        L53:
            r3.mAudioFormat = r7
            goto L58
        L56:
            r3.mAudioFormat = r1
        L58:
            return
        L59:
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.StringBuilder r5 = r6.append(r5)
            java.lang.String r6 = "Hz is not a supported sample rate."
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r5 = r5.toString()
            r4.<init>(r5)
            throw r4
        L72:
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException
            java.lang.String r5 = "Invalid audio source."
            r4.<init>(r5)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.AudioRecord.audioParamCheck(int, int, int, int):void");
    }

    private void audioBuffSizeCheck(int i) {
        if (i % (this.mChannelCount * (this.mAudioFormat == 3 ? 1 : 2)) != 0 || i < 1) {
            throw new IllegalArgumentException("Invalid audio buffer size.");
        }
        this.mNativeBufferSizeInBytes = i;
    }

    public void release() {
        try {
            stop();
        } catch (IllegalStateException unused) {
        }
        native_release();
        this.mState = 0;
    }

    protected void finalize() {
        native_finalize();
    }

    public int getSampleRate() {
        return this.mSampleRate;
    }

    public int getAudioSource() {
        return this.mRecordSource;
    }

    public int getAudioFormat() {
        return this.mAudioFormat;
    }

    public int getChannelConfiguration() {
        return this.mChannelMask;
    }

    public int getChannelCount() {
        return this.mChannelCount;
    }

    public int getState() {
        return this.mState;
    }

    public int getRecordingState() {
        int i;
        synchronized (this.mRecordingStateLock) {
            i = this.mRecordingState;
        }
        return i;
    }

    public int getNotificationMarkerPosition() {
        return native_get_marker_pos();
    }

    public int getPositionNotificationPeriod() {
        return native_get_pos_update_period();
    }

    public static int getMinBufferSize(int i, int i2, int i3) {
        int i4 = 1;
        if (i2 != 1 && i2 != 2) {
            if (i2 == 3 || i2 == 12) {
                i4 = 2;
            } else if (i2 != 16) {
                if (i2 != 48) {
                    loge("getMinBufferSize(): Invalid channel configuration.");
                    return -2;
                }
                i4 = 2;
            }
        }
        if (i3 != 2) {
            loge("getMinBufferSize(): Invalid audio format.");
            return -2;
        }
        int iNative_get_min_buff_size = native_get_min_buff_size(i, i4, i3);
        if (iNative_get_min_buff_size == 0) {
            return -2;
        }
        if (iNative_get_min_buff_size == -1) {
            return -1;
        }
        return iNative_get_min_buff_size;
    }

    public int getAudioSessionId() {
        return this.mSessionId;
    }

    public void startRecording() throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("startRecording() called on an uninitialized AudioRecord.");
        }
        synchronized (this.mRecordingStateLock) {
            if (native_start(0, 0) == 0) {
                this.mRecordingState = 3;
            }
        }
    }

    public void startRecording(MediaSyncEvent mediaSyncEvent) throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("startRecording() called on an uninitialized AudioRecord.");
        }
        synchronized (this.mRecordingStateLock) {
            if (native_start(mediaSyncEvent.getType(), mediaSyncEvent.getAudioSessionId()) == 0) {
                this.mRecordingState = 3;
            }
        }
    }

    public void stop() throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("stop() called on an uninitialized AudioRecord.");
        }
        synchronized (this.mRecordingStateLock) {
            native_stop();
            this.mRecordingState = 1;
        }
    }

    public int read(byte[] bArr, int i, int i2) {
        int i3;
        if (this.mState != 1) {
            return -3;
        }
        if (bArr == null || i < 0 || i2 < 0 || (i3 = i + i2) < 0 || i3 > bArr.length) {
            return -2;
        }
        return native_read_in_byte_array(bArr, i, i2);
    }

    public int read(short[] sArr, int i, int i2) {
        int i3;
        if (this.mState != 1) {
            return -3;
        }
        if (sArr == null || i < 0 || i2 < 0 || (i3 = i + i2) < 0 || i3 > sArr.length) {
            return -2;
        }
        return native_read_in_short_array(sArr, i, i2);
    }

    public int read(ByteBuffer byteBuffer, int i) {
        if (this.mState != 1) {
            return -3;
        }
        if (byteBuffer == null || i < 0) {
            return -2;
        }
        return native_read_in_direct_buffer(byteBuffer, i);
    }

    public void setRecordPositionUpdateListener(OnRecordPositionUpdateListener onRecordPositionUpdateListener) {
        setRecordPositionUpdateListener(onRecordPositionUpdateListener, null);
    }

    public void setRecordPositionUpdateListener(OnRecordPositionUpdateListener onRecordPositionUpdateListener, Handler handler) {
        synchronized (this.mPositionListenerLock) {
            this.mPositionListener = onRecordPositionUpdateListener;
            if (onRecordPositionUpdateListener == null) {
                this.mEventHandler = null;
            } else if (handler != null) {
                this.mEventHandler = new NativeEventHandler(this, handler.getLooper());
            } else {
                this.mEventHandler = new NativeEventHandler(this, this.mInitializationLooper);
            }
        }
    }

    public int setNotificationMarkerPosition(int i) {
        if (this.mState == 0) {
            return -3;
        }
        return native_set_marker_pos(i);
    }

    public int setPositionNotificationPeriod(int i) {
        if (this.mState == 0) {
            return -3;
        }
        return native_set_pos_update_period(i);
    }

    private class NativeEventHandler extends Handler {
        private final AudioRecord mAudioRecord;

        NativeEventHandler(AudioRecord audioRecord, Looper looper) {
            super(looper);
            this.mAudioRecord = audioRecord;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            OnRecordPositionUpdateListener onRecordPositionUpdateListener;
            synchronized (AudioRecord.this.mPositionListenerLock) {
                onRecordPositionUpdateListener = this.mAudioRecord.mPositionListener;
            }
            int i = message.what;
            if (i == 2) {
                if (onRecordPositionUpdateListener != null) {
                    onRecordPositionUpdateListener.onMarkerReached(this.mAudioRecord);
                }
            } else if (i != 3) {
                AudioRecord.loge("Unknown native event type: " + message.what);
            } else if (onRecordPositionUpdateListener != null) {
                onRecordPositionUpdateListener.onPeriodicNotification(this.mAudioRecord);
            }
        }
    }

    private static void postEventFromNative(Object obj, int i, int i2, int i3, Object obj2) {
        NativeEventHandler nativeEventHandler;
        AudioRecord audioRecord = (AudioRecord) ((WeakReference) obj).get();
        if (audioRecord == null || (nativeEventHandler = audioRecord.mEventHandler) == null) {
            return;
        }
        audioRecord.mEventHandler.sendMessage(nativeEventHandler.obtainMessage(i, i2, i3, obj2));
    }

    private static void logd(String str) {
        Log.d(TAG, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loge(String str) {
        Log.e(TAG, str);
    }
}
