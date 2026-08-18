package android.media;

import android.media.MediaCodec;
import dalvik.system.CloseGuard;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class MediaMuxer {
    private static final int MUXER_STATE_INITIALIZED = 0;
    private static final int MUXER_STATE_STARTED = 1;
    private static final int MUXER_STATE_STOPPED = 2;
    private static final int MUXER_STATE_UNINITIALIZED = -1;
    private final CloseGuard mCloseGuard;
    private int mLastTrackIndex;
    private int mNativeContext;
    private int mNativeObject;
    private int mState;

    private static native int nativeAddTrack(int i, String[] strArr, Object[] objArr);

    private static native void nativeRelease(int i);

    private static native void nativeSetLocation(int i, int i2, int i3);

    private static native void nativeSetOrientationHint(int i, int i2);

    private static native int nativeSetup(FileDescriptor fileDescriptor, int i);

    private static native void nativeStart(int i);

    private static native void nativeStop(int i);

    private static native void nativeWriteSampleData(int i, int i2, ByteBuffer byteBuffer, int i3, int i4, long j, int i5);

    static {
        System.loadLibrary("media_jni");
    }

    public static final class OutputFormat {
        public static final int MUXER_OUTPUT_MPEG_4 = 0;

        private OutputFormat() {
        }
    }

    public MediaMuxer(String str, int i) throws Throwable {
        this.mState = -1;
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mLastTrackIndex = -1;
        if (str == null) {
            throw new IllegalArgumentException("path must not be null");
        }
        if (i != 0) {
            throw new IllegalArgumentException("format is invalid");
        }
        FileOutputStream fileOutputStream = null;
        try {
            FileOutputStream fileOutputStream2 = new FileOutputStream(new File(str));
            try {
                this.mNativeObject = nativeSetup(fileOutputStream2.getFD(), i);
                this.mState = 0;
                closeGuard.open("release");
                fileOutputStream2.close();
            } catch (Throwable th) {
                th = th;
                fileOutputStream = fileOutputStream2;
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void setOrientationHint(int i) {
        if (i != 0 && i != 90 && i != 180 && i != 270) {
            throw new IllegalArgumentException("Unsupported angle: " + i);
        }
        if (this.mState == 0) {
            nativeSetOrientationHint(this.mNativeObject, i);
            return;
        }
        throw new IllegalStateException("Can't set rotation degrees due to wrong state.");
    }

    public void setLocation(float f, float f2) {
        int i;
        int i2 = (int) (((double) (f * 10000.0f)) + 0.5d);
        int i3 = (int) (((double) (10000.0f * f2)) + 0.5d);
        if (i2 > 900000 || i2 < -900000) {
            throw new IllegalArgumentException("Latitude: " + f + " out of range.");
        }
        if (i3 > 1800000 || i3 < -1800000) {
            throw new IllegalArgumentException("Longitude: " + f2 + " out of range");
        }
        if (this.mState == 0 && (i = this.mNativeObject) != 0) {
            nativeSetLocation(i, i2, i3);
            return;
        }
        throw new IllegalStateException("Can't set location due to wrong state.");
    }

    public void start() {
        int i = this.mNativeObject;
        if (i == 0) {
            throw new IllegalStateException("Muxer has been released!");
        }
        if (this.mState == 0) {
            nativeStart(i);
            this.mState = 1;
            return;
        }
        throw new IllegalStateException("Can't start due to wrong state.");
    }

    public void stop() {
        if (this.mState == 1) {
            nativeStop(this.mNativeObject);
            this.mState = 2;
            return;
        }
        throw new IllegalStateException("Can't stop due to wrong state.");
    }

    protected void finalize() throws Throwable {
        try {
            CloseGuard closeGuard = this.mCloseGuard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
            }
            int i = this.mNativeObject;
            if (i != 0) {
                nativeRelease(i);
                this.mNativeObject = 0;
            }
        } finally {
            super.finalize();
        }
    }

    public int addTrack(MediaFormat mediaFormat) {
        if (mediaFormat == null) {
            throw new IllegalArgumentException("format must not be null.");
        }
        if (this.mState != 0) {
            throw new IllegalStateException("Muxer is not initialized.");
        }
        if (this.mNativeObject == 0) {
            throw new IllegalStateException("Muxer has been released!");
        }
        Map<String, Object> map = mediaFormat.getMap();
        int size = map.size();
        if (size > 0) {
            String[] strArr = new String[size];
            Object[] objArr = new Object[size];
            int i = 0;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                strArr[i] = entry.getKey();
                objArr[i] = entry.getValue();
                i++;
            }
            int iNativeAddTrack = nativeAddTrack(this.mNativeObject, strArr, objArr);
            if (this.mLastTrackIndex >= iNativeAddTrack) {
                throw new IllegalArgumentException("Invalid format.");
            }
            this.mLastTrackIndex = iNativeAddTrack;
            return iNativeAddTrack;
        }
        throw new IllegalArgumentException("format must not be empty.");
    }

    public void writeSampleData(int i, ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        if (i < 0 || i > this.mLastTrackIndex) {
            throw new IllegalArgumentException("trackIndex is invalid");
        }
        if (byteBuffer == null) {
            throw new IllegalArgumentException("byteBuffer must not be null");
        }
        if (bufferInfo == null) {
            throw new IllegalArgumentException("bufferInfo must not be null");
        }
        if (bufferInfo.size < 0 || bufferInfo.offset < 0 || bufferInfo.offset + bufferInfo.size > byteBuffer.capacity() || bufferInfo.presentationTimeUs < 0) {
            throw new IllegalArgumentException("bufferInfo must specify a valid buffer offset, size and presentation time");
        }
        int i2 = this.mNativeObject;
        if (i2 == 0) {
            throw new IllegalStateException("Muxer has been released!");
        }
        if (this.mState != 1) {
            throw new IllegalStateException("Can't write, muxer is not started");
        }
        nativeWriteSampleData(i2, i, byteBuffer, bufferInfo.offset, bufferInfo.size, bufferInfo.presentationTimeUs, bufferInfo.flags);
    }

    public void release() {
        if (this.mState == 1) {
            stop();
        }
        int i = this.mNativeObject;
        if (i != 0) {
            nativeRelease(i);
            this.mNativeObject = 0;
            this.mCloseGuard.close();
        }
        this.mState = -1;
    }
}
