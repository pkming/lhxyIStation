package android.media;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class AudioTrack {
    public static final int ERROR = -1;
    public static final int ERROR_BAD_VALUE = -2;
    public static final int ERROR_INVALID_OPERATION = -3;
    private static final int ERROR_NATIVESETUP_AUDIOSYSTEM = -16;
    private static final int ERROR_NATIVESETUP_INVALIDCHANNELMASK = -17;
    private static final int ERROR_NATIVESETUP_INVALIDFORMAT = -18;
    private static final int ERROR_NATIVESETUP_INVALIDSTREAMTYPE = -19;
    private static final int ERROR_NATIVESETUP_NATIVEINITFAILED = -20;
    public static final int MODE_STATIC = 0;
    public static final int MODE_STREAM = 1;
    private static final int NATIVE_EVENT_MARKER = 3;
    private static final int NATIVE_EVENT_NEW_POS = 4;
    public static final int PLAYSTATE_PAUSED = 2;
    public static final int PLAYSTATE_PLAYING = 3;
    public static final int PLAYSTATE_STOPPED = 1;
    private static final int SAMPLE_RATE_HZ_MAX = 48000;
    private static final int SAMPLE_RATE_HZ_MIN = 4000;
    public static final int STATE_INITIALIZED = 1;
    public static final int STATE_NO_STATIC_DATA = 2;
    public static final int STATE_UNINITIALIZED = 0;
    public static final int SUCCESS = 0;
    private static final int SUPPORTED_OUT_CHANNELS = 1276;
    private static final String TAG = "android.media.AudioTrack";
    private static final float VOLUME_MAX = 1.0f;
    private static final float VOLUME_MIN = 0.0f;
    private int mAudioFormat;
    private int mChannelConfiguration;
    private int mChannelCount;
    private int mChannels;
    private int mDataLoadMode;
    private NativeEventHandlerDelegate mEventHandlerDelegate;
    private final Looper mInitializationLooper;
    private int mJniData;
    private int mNativeBufferSizeInBytes;
    private int mNativeBufferSizeInFrames;
    private int mNativeTrackInJavaObj;
    private int mPlayState;
    private final Object mPlayStateLock;
    private int mSampleRate;
    private int mSessionId;
    private int mState;
    private int mStreamType;

    public interface OnPlaybackPositionUpdateListener {
        void onMarkerReached(AudioTrack audioTrack);

        void onPeriodicNotification(AudioTrack audioTrack);
    }

    public static float getMaxVolume() {
        return 1.0f;
    }

    public static float getMinVolume() {
        return 0.0f;
    }

    private final native int native_attachAuxEffect(int i);

    private final native void native_finalize();

    private final native void native_flush();

    private final native int native_get_latency();

    private final native int native_get_marker_pos();

    private static final native int native_get_min_buff_size(int i, int i2, int i3);

    private final native int native_get_native_frame_count();

    private static final native int native_get_output_sample_rate(int i);

    private final native int native_get_playback_rate();

    private final native int native_get_pos_update_period();

    private final native int native_get_position();

    private final native int native_get_timestamp(long[] jArr);

    private final native void native_pause();

    private final native void native_release();

    private final native int native_reload_static();

    private final native void native_setAuxEffectSendLevel(float f);

    private final native void native_setVolume(float f, float f2);

    private final native int native_set_loop(int i, int i2, int i3);

    private final native int native_set_marker_pos(int i);

    private final native int native_set_playback_rate(int i);

    private final native int native_set_pos_update_period(int i);

    private final native int native_set_position(int i);

    private final native int native_setup(Object obj, int i, int i2, int i3, int i4, int i5, int i6, int[] iArr);

    private final native void native_start();

    private final native void native_stop();

    private final native int native_write_byte(byte[] bArr, int i, int i2, int i3);

    private final native int native_write_short(short[] sArr, int i, int i2, int i3);

    public AudioTrack(int i, int i2, int i3, int i4, int i5, int i6) throws IllegalArgumentException {
        this(i, i2, i3, i4, i5, i6, 0);
    }

    public AudioTrack(int i, int i2, int i3, int i4, int i5, int i6, int i7) throws IllegalArgumentException {
        this.mState = 0;
        this.mPlayState = 1;
        this.mPlayStateLock = new Object();
        this.mNativeBufferSizeInBytes = 0;
        this.mNativeBufferSizeInFrames = 0;
        this.mChannelCount = 1;
        this.mChannels = 4;
        this.mStreamType = 3;
        this.mDataLoadMode = 1;
        this.mChannelConfiguration = 4;
        this.mAudioFormat = 2;
        this.mSessionId = 0;
        Looper looperMyLooper = Looper.myLooper();
        this.mInitializationLooper = looperMyLooper == null ? Looper.getMainLooper() : looperMyLooper;
        audioParamCheck(i, i2, i3, i4, i6);
        audioBuffSizeCheck(i5);
        if (i7 < 0) {
            throw new IllegalArgumentException("Invalid audio session ID: " + i7);
        }
        int[] iArr = {i7};
        int iNative_setup = native_setup(new WeakReference(this), this.mStreamType, this.mSampleRate, this.mChannels, this.mAudioFormat, this.mNativeBufferSizeInBytes, this.mDataLoadMode, iArr);
        if (iNative_setup != 0) {
            loge("Error code " + iNative_setup + " when initializing AudioTrack.");
            return;
        }
        this.mSessionId = iArr[0];
        if (this.mDataLoadMode == 0) {
            this.mState = 2;
        } else {
            this.mState = 1;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x0053  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0058  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void audioParamCheck(int r6, int r7, int r8, int r9, int r10) {
        /*
            r5 = this;
            r0 = 3
            r1 = 4
            r2 = 1
            r3 = 2
            if (r6 == r1) goto L21
            if (r6 == r0) goto L21
            if (r6 == r3) goto L21
            if (r6 == r2) goto L21
            if (r6 == 0) goto L21
            r4 = 5
            if (r6 == r4) goto L21
            r4 = 6
            if (r6 == r4) goto L21
            r4 = 8
            if (r6 != r4) goto L19
            goto L21
        L19:
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException
            java.lang.String r7 = "Invalid stream type."
            r6.<init>(r7)
            throw r6
        L21:
            r5.mStreamType = r6
            r6 = 4000(0xfa0, float:5.605E-42)
            if (r7 < r6) goto L80
            r6 = 48000(0xbb80, float:6.7262E-41)
            if (r7 > r6) goto L80
            r5.mSampleRate = r7
            r5.mChannelConfiguration = r8
            if (r8 == r2) goto L58
            if (r8 == r3) goto L58
            r6 = 12
            if (r8 == r0) goto L53
            if (r8 == r1) goto L58
            if (r8 == r6) goto L53
            boolean r6 = isMultichannelConfigSupported(r8)
            if (r6 == 0) goto L4b
            r5.mChannels = r8
            int r6 = java.lang.Integer.bitCount(r8)
            r5.mChannelCount = r6
            goto L5c
        L4b:
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException
            java.lang.String r7 = "Unsupported channel configuration."
            r6.<init>(r7)
            throw r6
        L53:
            r5.mChannelCount = r3
            r5.mChannels = r6
            goto L5c
        L58:
            r5.mChannelCount = r2
            r5.mChannels = r1
        L5c:
            if (r9 == r2) goto L6e
            if (r9 == r3) goto L6b
            if (r9 != r0) goto L63
            goto L6b
        L63:
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException
            java.lang.String r7 = "Unsupported sample encoding. Should be ENCODING_PCM_8BIT or ENCODING_PCM_16BIT."
            r6.<init>(r7)
            throw r6
        L6b:
            r5.mAudioFormat = r9
            goto L70
        L6e:
            r5.mAudioFormat = r3
        L70:
            if (r10 == r2) goto L7d
            if (r10 != 0) goto L75
            goto L7d
        L75:
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException
            java.lang.String r7 = "Invalid mode."
            r6.<init>(r7)
            throw r6
        L7d:
            r5.mDataLoadMode = r10
            return
        L80:
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.StringBuilder r7 = r8.append(r7)
            java.lang.String r8 = "Hz is not a supported sample rate."
            java.lang.StringBuilder r7 = r7.append(r8)
            java.lang.String r7 = r7.toString()
            r6.<init>(r7)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.AudioTrack.audioParamCheck(int, int, int, int, int):void");
    }

    private static boolean isMultichannelConfigSupported(int i) {
        if ((i & SUPPORTED_OUT_CHANNELS) != i) {
            loge("Channel configuration features unsupported channels");
            return false;
        }
        if ((i & 12) != 12) {
            loge("Front channels must be present in multichannel configurations");
            return false;
        }
        int i2 = i & 192;
        if (i2 == 0 || i2 == 192) {
            return true;
        }
        loge("Rear channels can't be used independently");
        return false;
    }

    private void audioBuffSizeCheck(int i) {
        int i2 = this.mChannelCount * (this.mAudioFormat == 3 ? 1 : 2);
        if (i % i2 != 0 || i < 1) {
            throw new IllegalArgumentException("Invalid audio buffer size.");
        }
        this.mNativeBufferSizeInBytes = i;
        this.mNativeBufferSizeInFrames = i / i2;
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

    public int getPlaybackRate() {
        return native_get_playback_rate();
    }

    public int getAudioFormat() {
        return this.mAudioFormat;
    }

    public int getStreamType() {
        return this.mStreamType;
    }

    public int getChannelConfiguration() {
        return this.mChannelConfiguration;
    }

    public int getChannelCount() {
        return this.mChannelCount;
    }

    public int getState() {
        return this.mState;
    }

    public int getPlayState() {
        int i;
        synchronized (this.mPlayStateLock) {
            i = this.mPlayState;
        }
        return i;
    }

    @Deprecated
    protected int getNativeFrameCount() {
        return native_get_native_frame_count();
    }

    public int getNotificationMarkerPosition() {
        return native_get_marker_pos();
    }

    public int getPositionNotificationPeriod() {
        return native_get_pos_update_period();
    }

    public int getPlaybackHeadPosition() {
        return native_get_position();
    }

    public int getLatency() {
        return native_get_latency();
    }

    public static int getNativeOutputSampleRate(int i) {
        return native_get_output_sample_rate(i);
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x001d  */
    /* JADX WARN: Removed duplicated region for block: B:15:0x001f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static int getMinBufferSize(int r4, int r5, int r6) {
        /*
            r0 = 3
            r1 = -2
            r2 = 2
            if (r5 == r2) goto L1f
            if (r5 == r0) goto L1d
            r3 = 4
            if (r5 == r3) goto L1f
            r3 = 12
            if (r5 == r3) goto L1d
            r3 = r5 & 1276(0x4fc, float:1.788E-42)
            if (r3 == r5) goto L18
            java.lang.String r4 = "getMinBufferSize(): Invalid channel configuration."
            loge(r4)
            return r1
        L18:
            int r5 = java.lang.Integer.bitCount(r5)
            goto L20
        L1d:
            r5 = r2
            goto L20
        L1f:
            r5 = 1
        L20:
            if (r6 == r2) goto L2a
            if (r6 == r0) goto L2a
            java.lang.String r4 = "getMinBufferSize(): Invalid audio format."
            loge(r4)
            return r1
        L2a:
            r0 = 4000(0xfa0, float:5.605E-42)
            if (r4 < r0) goto L41
            r0 = 48000(0xbb80, float:6.7262E-41)
            if (r4 <= r0) goto L34
            goto L41
        L34:
            int r4 = native_get_min_buff_size(r4, r5, r6)
            if (r4 > 0) goto L40
            java.lang.String r4 = "getMinBufferSize(): error querying hardware"
            loge(r4)
            r4 = -1
        L40:
            return r4
        L41:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "getMinBufferSize(): "
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.StringBuilder r4 = r5.append(r4)
            java.lang.String r5 = " Hz is not a supported sample rate."
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r4 = r4.toString()
            loge(r4)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.AudioTrack.getMinBufferSize(int, int, int):int");
    }

    public int getAudioSessionId() {
        return this.mSessionId;
    }

    public boolean getTimestamp(AudioTimestamp audioTimestamp) {
        if (audioTimestamp == null) {
            throw new IllegalArgumentException();
        }
        long[] jArr = new long[2];
        if (native_get_timestamp(jArr) != 0) {
            return false;
        }
        audioTimestamp.framePosition = jArr[0];
        audioTimestamp.nanoTime = jArr[1];
        return true;
    }

    public void setPlaybackPositionUpdateListener(OnPlaybackPositionUpdateListener onPlaybackPositionUpdateListener) {
        setPlaybackPositionUpdateListener(onPlaybackPositionUpdateListener, null);
    }

    public void setPlaybackPositionUpdateListener(OnPlaybackPositionUpdateListener onPlaybackPositionUpdateListener, Handler handler) {
        if (onPlaybackPositionUpdateListener != null) {
            this.mEventHandlerDelegate = new NativeEventHandlerDelegate(this, onPlaybackPositionUpdateListener, handler);
        } else {
            this.mEventHandlerDelegate = null;
        }
    }

    public int setStereoVolume(float f, float f2) {
        if (this.mState == 0) {
            return -3;
        }
        if (f < getMinVolume()) {
            f = getMinVolume();
        }
        if (f > getMaxVolume()) {
            f = getMaxVolume();
        }
        if (f2 < getMinVolume()) {
            f2 = getMinVolume();
        }
        if (f2 > getMaxVolume()) {
            f2 = getMaxVolume();
        }
        native_setVolume(f, f2);
        return 0;
    }

    public int setVolume(float f) {
        return setStereoVolume(f, f);
    }

    public int setPlaybackRate(int i) {
        if (this.mState != 1) {
            return -3;
        }
        if (i <= 0) {
            return -2;
        }
        return native_set_playback_rate(i);
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

    public int setPlaybackHeadPosition(int i) {
        if (this.mDataLoadMode == 1 || this.mState != 1 || getPlayState() == 3) {
            return -3;
        }
        if (i < 0 || i > this.mNativeBufferSizeInFrames) {
            return -2;
        }
        return native_set_position(i);
    }

    public int setLoopPoints(int i, int i2, int i3) {
        int i4;
        if (this.mDataLoadMode == 1 || this.mState != 1 || getPlayState() == 3) {
            return -3;
        }
        if (i3 != 0 && (i < 0 || i >= (i4 = this.mNativeBufferSizeInFrames) || i >= i2 || i2 > i4)) {
            return -2;
        }
        return native_set_loop(i, i2, i3);
    }

    @Deprecated
    protected void setState(int i) {
        this.mState = i;
    }

    public void play() throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("play() called on uninitialized AudioTrack.");
        }
        synchronized (this.mPlayStateLock) {
            native_start();
            this.mPlayState = 3;
        }
    }

    public void stop() throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("stop() called on uninitialized AudioTrack.");
        }
        synchronized (this.mPlayStateLock) {
            native_stop();
            this.mPlayState = 1;
        }
    }

    public void pause() throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("pause() called on uninitialized AudioTrack.");
        }
        synchronized (this.mPlayStateLock) {
            native_pause();
            this.mPlayState = 2;
        }
    }

    public void flush() {
        if (this.mState == 1) {
            native_flush();
        }
    }

    public int write(byte[] bArr, int i, int i2) {
        int i3;
        if (this.mState == 0) {
            return -3;
        }
        if (bArr == null || i < 0 || i2 < 0 || (i3 = i + i2) < 0 || i3 > bArr.length) {
            return -2;
        }
        int iNative_write_byte = native_write_byte(bArr, i, i2, this.mAudioFormat);
        if (this.mDataLoadMode == 0 && this.mState == 2 && iNative_write_byte > 0) {
            this.mState = 1;
        }
        return iNative_write_byte;
    }

    public int write(short[] sArr, int i, int i2) {
        int i3;
        if (this.mState == 0) {
            return -3;
        }
        if (sArr == null || i < 0 || i2 < 0 || (i3 = i + i2) < 0 || i3 > sArr.length) {
            return -2;
        }
        int iNative_write_short = native_write_short(sArr, i, i2, this.mAudioFormat);
        if (this.mDataLoadMode == 0 && this.mState == 2 && iNative_write_short > 0) {
            this.mState = 1;
        }
        return iNative_write_short;
    }

    public int reloadStaticData() {
        if (this.mDataLoadMode == 1 || this.mState != 1) {
            return -3;
        }
        return native_reload_static();
    }

    public int attachAuxEffect(int i) {
        if (this.mState == 0) {
            return -3;
        }
        return native_attachAuxEffect(i);
    }

    public int setAuxEffectSendLevel(float f) {
        if (this.mState == 0) {
            return -3;
        }
        if (f < getMinVolume()) {
            f = getMinVolume();
        }
        if (f > getMaxVolume()) {
            f = getMaxVolume();
        }
        native_setAuxEffectSendLevel(f);
        return 0;
    }

    private class NativeEventHandlerDelegate {
        private final Handler mHandler;

        NativeEventHandlerDelegate(final AudioTrack audioTrack, final OnPlaybackPositionUpdateListener onPlaybackPositionUpdateListener, Handler handler) {
            Looper looper;
            if (handler == null) {
                looper = AudioTrack.this.mInitializationLooper;
            } else {
                looper = handler.getLooper();
            }
            Looper looper2 = looper;
            if (looper2 != null) {
                this.mHandler = new Handler(looper2) { // from class: android.media.AudioTrack.NativeEventHandlerDelegate.1
                    @Override // android.os.Handler
                    public void handleMessage(Message message) {
                        if (audioTrack == null) {
                            return;
                        }
                        int i = message.what;
                        if (i == 3) {
                            OnPlaybackPositionUpdateListener onPlaybackPositionUpdateListener2 = onPlaybackPositionUpdateListener;
                            if (onPlaybackPositionUpdateListener2 != null) {
                                onPlaybackPositionUpdateListener2.onMarkerReached(audioTrack);
                                return;
                            }
                            return;
                        }
                        if (i != 4) {
                            AudioTrack.loge("Unknown native event type: " + message.what);
                            return;
                        }
                        OnPlaybackPositionUpdateListener onPlaybackPositionUpdateListener3 = onPlaybackPositionUpdateListener;
                        if (onPlaybackPositionUpdateListener3 != null) {
                            onPlaybackPositionUpdateListener3.onPeriodicNotification(audioTrack);
                        }
                    }
                };
            } else {
                this.mHandler = null;
            }
        }

        Handler getHandler() {
            return this.mHandler;
        }
    }

    private static void postEventFromNative(Object obj, int i, int i2, int i3, Object obj2) {
        NativeEventHandlerDelegate nativeEventHandlerDelegate;
        Handler handler;
        AudioTrack audioTrack = (AudioTrack) ((WeakReference) obj).get();
        if (audioTrack == null || (nativeEventHandlerDelegate = audioTrack.mEventHandlerDelegate) == null || (handler = nativeEventHandlerDelegate.getHandler()) == null) {
            return;
        }
        handler.sendMessage(handler.obtainMessage(i, i2, i3, obj2));
    }

    private static void logd(String str) {
        Log.d(TAG, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loge(String str) {
        Log.e(TAG, str);
    }
}
