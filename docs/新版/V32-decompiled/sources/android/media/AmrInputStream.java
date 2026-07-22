package android.media;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public final class AmrInputStream extends InputStream {
    private static final int SAMPLES_PER_FRAME = 160;
    private static final String TAG = "AmrInputStream";
    private int mGae;
    private InputStream mInputStream;
    private final byte[] mBuf = new byte[320];
    private int mBufIn = 0;
    private int mBufOut = 0;
    private byte[] mOneByte = new byte[1];

    private static native void GsmAmrEncoderCleanup(int i);

    private static native void GsmAmrEncoderDelete(int i);

    private static native int GsmAmrEncoderEncode(int i, byte[] bArr, int i2, byte[] bArr2, int i3) throws IOException;

    private static native void GsmAmrEncoderInitialize(int i);

    private static native int GsmAmrEncoderNew();

    static {
        System.loadLibrary("media_jni");
    }

    public AmrInputStream(InputStream inputStream) {
        this.mInputStream = inputStream;
        int iGsmAmrEncoderNew = GsmAmrEncoderNew();
        this.mGae = iGsmAmrEncoderNew;
        GsmAmrEncoderInitialize(iGsmAmrEncoderNew);
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (read(this.mOneByte, 0, 1) == 1) {
            return this.mOneByte[0] & 255;
        }
        return -1;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (this.mGae == 0) {
            throw new IllegalStateException("not open");
        }
        if (this.mBufOut >= this.mBufIn) {
            this.mBufOut = 0;
            this.mBufIn = 0;
            int i3 = 0;
            while (i3 < 320) {
                int i4 = this.mInputStream.read(this.mBuf, i3, 320 - i3);
                if (i4 == -1) {
                    return -1;
                }
                i3 += i4;
            }
            int i5 = this.mGae;
            byte[] bArr2 = this.mBuf;
            this.mBufIn = GsmAmrEncoderEncode(i5, bArr2, 0, bArr2, 0);
        }
        int i6 = this.mBufIn;
        int i7 = this.mBufOut;
        if (i2 > i6 - i7) {
            i2 = i6 - i7;
        }
        System.arraycopy(this.mBuf, i7, bArr, i, i2);
        this.mBufOut += i2;
        return i2;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        try {
            InputStream inputStream = this.mInputStream;
            if (inputStream != null) {
                inputStream.close();
            }
            this.mInputStream = null;
            try {
                int i = this.mGae;
                if (i != 0) {
                    GsmAmrEncoderCleanup(i);
                }
                try {
                    int i2 = this.mGae;
                    if (i2 != 0) {
                        GsmAmrEncoderDelete(i2);
                    }
                } finally {
                }
            } catch (Throwable th) {
                try {
                    int i3 = this.mGae;
                    if (i3 != 0) {
                        GsmAmrEncoderDelete(i3);
                    }
                    throw th;
                } finally {
                }
            }
        } catch (Throwable th2) {
            this.mInputStream = null;
            try {
                int i4 = this.mGae;
                if (i4 != 0) {
                    GsmAmrEncoderCleanup(i4);
                }
                try {
                    int i5 = this.mGae;
                    if (i5 != 0) {
                        GsmAmrEncoderDelete(i5);
                    }
                    throw th2;
                } finally {
                }
            } catch (Throwable th3) {
                try {
                    int i6 = this.mGae;
                    if (i6 != 0) {
                        GsmAmrEncoderDelete(i6);
                    }
                    throw th3;
                } finally {
                }
            }
        }
    }

    protected void finalize() throws Throwable {
        if (this.mGae == 0) {
            return;
        }
        close();
        throw new IllegalStateException("someone forgot to close AmrInputStream");
    }
}
