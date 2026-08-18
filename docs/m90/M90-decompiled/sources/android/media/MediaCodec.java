package android.media;

import android.os.Bundle;
import android.view.Surface;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFErrorConstants;

/* JADX INFO: loaded from: classes.dex */
public final class MediaCodec {
    public static final int BUFFER_FLAG_CODEC_CONFIG = 2;
    public static final int BUFFER_FLAG_END_OF_STREAM = 4;
    public static final int BUFFER_FLAG_SYNC_FRAME = 1;
    public static final int CONFIGURE_FLAG_ENCODE = 1;
    public static final int CRYPTO_MODE_AES_CTR = 1;
    public static final int CRYPTO_MODE_UNENCRYPTED = 0;
    public static final int INFO_OUTPUT_BUFFERS_CHANGED = -3;
    public static final int INFO_OUTPUT_FORMAT_CHANGED = -2;
    public static final int INFO_TRY_AGAIN_LATER = -1;
    public static final String PARAMETER_KEY_REQUEST_SYNC_FRAME = "request-sync";
    public static final String PARAMETER_KEY_SUSPEND = "drop-input-frames";
    public static final String PARAMETER_KEY_VIDEO_BITRATE = "video-bitrate";
    public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT = 1;
    public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING = 2;
    private int mNativeContext;

    private final native ByteBuffer[] getBuffers(boolean z);

    private final native Map<String, Object> getOutputFormatNative();

    private final native void native_configure(String[] strArr, Object[] objArr, Surface surface, MediaCrypto mediaCrypto, int i);

    private final native void native_finalize();

    private static final native void native_init();

    private final native void native_setup(String str, boolean z, boolean z2);

    private final native void setParameters(String[] strArr, Object[] objArr);

    public final native Surface createInputSurface();

    public final native int dequeueInputBuffer(long j);

    public final native int dequeueOutputBuffer(BufferInfo bufferInfo, long j);

    public final native void flush();

    public final native String getName();

    public final native void queueInputBuffer(int i, int i2, int i3, long j, int i4) throws CryptoException;

    public final native void queueSecureInputBuffer(int i, int i2, CryptoInfo cryptoInfo, long j, int i3) throws CryptoException;

    public final native void release();

    public final native void releaseOutputBuffer(int i, boolean z);

    public final native void setVideoScalingMode(int i);

    public final native void signalEndOfInputStream();

    public final native void start();

    public final native void stop();

    public static final class BufferInfo {
        public int flags;
        public int offset;
        public long presentationTimeUs;
        public int size;

        public void set(int i, int i2, long j, int i3) {
            this.offset = i;
            this.size = i2;
            this.presentationTimeUs = j;
            this.flags = i3;
        }
    }

    public static MediaCodec createDecoderByType(String str) {
        return new MediaCodec(str, true, false);
    }

    public static MediaCodec createEncoderByType(String str) {
        return new MediaCodec(str, true, true);
    }

    public static MediaCodec createByCodecName(String str) {
        return new MediaCodec(str, false, false);
    }

    private MediaCodec(String str, boolean z, boolean z2) {
        native_setup(str, z, z2);
    }

    protected void finalize() {
        native_finalize();
    }

    public void configure(MediaFormat mediaFormat, Surface surface, MediaCrypto mediaCrypto, int i) {
        String[] strArr;
        Object[] objArr;
        Map<String, Object> map = mediaFormat.getMap();
        if (mediaFormat != null) {
            String[] strArr2 = new String[map.size()];
            Object[] objArr2 = new Object[map.size()];
            int i2 = 0;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                strArr2[i2] = entry.getKey();
                objArr2[i2] = entry.getValue();
                i2++;
            }
            objArr = objArr2;
            strArr = strArr2;
        } else {
            strArr = null;
            objArr = null;
        }
        native_configure(strArr, objArr, surface, mediaCrypto, i);
    }

    public static final class CryptoException extends RuntimeException {
        public static final int ERROR_KEY_EXPIRED = 2;
        public static final int ERROR_NO_KEY = 1;
        public static final int ERROR_RESOURCE_BUSY = 3;
        private int mErrorCode;

        public CryptoException(int i, String str) {
            super(str);
            this.mErrorCode = i;
        }

        public int getErrorCode() {
            return this.mErrorCode;
        }
    }

    public static final class CryptoInfo {
        public byte[] iv;
        public byte[] key;
        public int mode;
        public int[] numBytesOfClearData;
        public int[] numBytesOfEncryptedData;
        public int numSubSamples;

        public void set(int i, int[] iArr, int[] iArr2, byte[] bArr, byte[] bArr2, int i2) {
            this.numSubSamples = i;
            this.numBytesOfClearData = iArr;
            this.numBytesOfEncryptedData = iArr2;
            this.key = bArr;
            this.iv = bArr2;
            this.mode = i2;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.numSubSamples + " subsamples, key [");
            int i = 0;
            while (true) {
                byte[] bArr = this.key;
                if (i >= bArr.length) {
                    break;
                }
                sb.append("0123456789abcdef".charAt((bArr[i] & 240) >> 4));
                sb.append("0123456789abcdef".charAt(this.key[i] & HSSFErrorConstants.ERROR_VALUE));
                i++;
            }
            sb.append("], iv [");
            for (int i2 = 0; i2 < this.key.length; i2++) {
                sb.append("0123456789abcdef".charAt((this.iv[i2] & 240) >> 4));
                sb.append("0123456789abcdef".charAt(this.iv[i2] & HSSFErrorConstants.ERROR_VALUE));
            }
            sb.append("], clear ");
            sb.append(Arrays.toString(this.numBytesOfClearData));
            sb.append(", encrypted ");
            sb.append(Arrays.toString(this.numBytesOfEncryptedData));
            return sb.toString();
        }
    }

    public final MediaFormat getOutputFormat() {
        return new MediaFormat(getOutputFormatNative());
    }

    public ByteBuffer[] getInputBuffers() {
        return getBuffers(true);
    }

    public ByteBuffer[] getOutputBuffers() {
        return getBuffers(false);
    }

    public final void setParameters(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        String[] strArr = new String[bundle.size()];
        Object[] objArr = new Object[bundle.size()];
        int i = 0;
        for (String str : bundle.keySet()) {
            strArr[i] = str;
            objArr[i] = bundle.get(str);
            i++;
        }
        setParameters(strArr, objArr);
    }

    public MediaCodecInfo getCodecInfo() {
        return MediaCodecList.getCodecInfoAt(MediaCodecList.findCodecByName(getName()));
    }

    static {
        System.loadLibrary("media_jni");
        native_init();
    }
}
