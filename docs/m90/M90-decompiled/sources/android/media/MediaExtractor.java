package android.media;

import android.media.MediaCodec;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public final class MediaExtractor {
    public static final int SAMPLE_FLAG_ENCRYPTED = 2;
    public static final int SAMPLE_FLAG_SYNC = 1;
    public static final int SEEK_TO_CLOSEST_SYNC = 2;
    public static final int SEEK_TO_NEXT_SYNC = 1;
    public static final int SEEK_TO_PREVIOUS_SYNC = 0;
    private int mNativeContext;

    private native Map<String, Object> getFileFormatNative();

    private native Map<String, Object> getTrackFormatNative(int i);

    private final native void native_finalize();

    private static final native void native_init();

    private final native void native_setup();

    private final native void setDataSource(String str, String[] strArr, String[] strArr2) throws IOException;

    public native boolean advance();

    public native long getCachedDuration();

    public native boolean getSampleCryptoInfo(MediaCodec.CryptoInfo cryptoInfo);

    public native int getSampleFlags();

    public native long getSampleTime();

    public native int getSampleTrackIndex();

    public final native int getTrackCount();

    public native boolean hasCacheReachedEndOfStream();

    public native int readSampleData(ByteBuffer byteBuffer, int i);

    public final native void release();

    public native void seekTo(long j, int i);

    public native void selectTrack(int i);

    public final native void setDataSource(DataSource dataSource) throws IOException;

    public final native void setDataSource(FileDescriptor fileDescriptor, long j, long j2) throws IOException;

    public native void unselectTrack(int i);

    public MediaExtractor() {
        native_setup();
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x0057  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void setDataSource(android.content.Context r8, android.net.Uri r9, java.util.Map<java.lang.String, java.lang.String> r10) throws java.io.IOException {
        /*
            r7 = this;
            java.lang.String r0 = r9.getScheme()
            if (r0 == 0) goto L62
            java.lang.String r1 = "file"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto Lf
            goto L62
        Lf:
            r0 = 0
            android.content.ContentResolver r8 = r8.getContentResolver()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L52 java.lang.SecurityException -> L55
            java.lang.String r1 = "r"
            android.content.res.AssetFileDescriptor r0 = r8.openAssetFileDescriptor(r9, r1)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L52 java.lang.SecurityException -> L55
            if (r0 != 0) goto L23
            if (r0 == 0) goto L22
            r0.close()
        L22:
            return
        L23:
            long r1 = r0.getDeclaredLength()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L52 java.lang.SecurityException -> L55
            r3 = 0
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 >= 0) goto L35
            java.io.FileDescriptor r8 = r0.getFileDescriptor()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L52 java.lang.SecurityException -> L55
            r7.setDataSource(r8)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L52 java.lang.SecurityException -> L55
            goto L45
        L35:
            java.io.FileDescriptor r2 = r0.getFileDescriptor()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L52 java.lang.SecurityException -> L55
            long r3 = r0.getStartOffset()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L52 java.lang.SecurityException -> L55
            long r5 = r0.getDeclaredLength()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L52 java.lang.SecurityException -> L55
            r1 = r7
            r1.setDataSource(r2, r3, r5)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L52 java.lang.SecurityException -> L55
        L45:
            if (r0 == 0) goto L4a
            r0.close()
        L4a:
            return
        L4b:
            r8 = move-exception
            if (r0 == 0) goto L51
            r0.close()
        L51:
            throw r8
        L52:
            if (r0 == 0) goto L5a
            goto L57
        L55:
            if (r0 == 0) goto L5a
        L57:
            r0.close()
        L5a:
            java.lang.String r8 = r9.toString()
            r7.setDataSource(r8, r10)
            return
        L62:
            java.lang.String r8 = r9.getPath()
            r7.setDataSource(r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaExtractor.setDataSource(android.content.Context, android.net.Uri, java.util.Map):void");
    }

    public final void setDataSource(String str, Map<String, String> map) throws IOException {
        String[] strArr;
        String[] strArr2 = null;
        if (map != null) {
            strArr2 = new String[map.size()];
            strArr = new String[map.size()];
            int i = 0;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                strArr2[i] = entry.getKey();
                strArr[i] = entry.getValue();
                i++;
            }
        } else {
            strArr = null;
        }
        setDataSource(str, strArr2, strArr);
    }

    public final void setDataSource(String str) throws IOException {
        setDataSource(str, (String[]) null, (String[]) null);
    }

    public final void setDataSource(FileDescriptor fileDescriptor) throws IOException {
        setDataSource(fileDescriptor, 0L, 576460752303423487L);
    }

    protected void finalize() {
        native_finalize();
    }

    public Map<UUID, byte[]> getPsshInfo() {
        Map<String, Object> fileFormatNative = getFileFormatNative();
        if (fileFormatNative == null || !fileFormatNative.containsKey("pssh")) {
            return null;
        }
        ByteBuffer byteBuffer = (ByteBuffer) fileFormatNative.get("pssh");
        byteBuffer.order(ByteOrder.nativeOrder());
        byteBuffer.rewind();
        fileFormatNative.remove("pssh");
        HashMap map = new HashMap();
        while (byteBuffer.remaining() > 0) {
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
            UUID uuid = new UUID(byteBuffer.getLong(), byteBuffer.getLong());
            byteBuffer.order(ByteOrder.nativeOrder());
            byte[] bArr = new byte[byteBuffer.getInt()];
            byteBuffer.get(bArr);
            map.put(uuid, bArr);
        }
        return map;
    }

    public MediaFormat getTrackFormat(int i) {
        return new MediaFormat(getTrackFormatNative(i));
    }

    static {
        System.loadLibrary("media_jni");
        native_init();
    }
}
