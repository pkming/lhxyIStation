package android.speech.srec;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public final class UlawEncoderInputStream extends InputStream {
    private static final int MAX_ULAW = 8192;
    private static final int SCALE_BITS = 16;
    private static final String TAG = "UlawEncoderInputStream";
    private InputStream mIn;
    private int mMax;
    private final byte[] mBuf = new byte[1024];
    private int mBufCount = 0;
    private final byte[] mOneByte = new byte[1];

    public static void encode(byte[] bArr, int i, byte[] bArr2, int i2, int i3, int i4) {
        int i5;
        if (i4 <= 0) {
            i4 = 8192;
        }
        int i6 = 536870912 / i4;
        int i7 = 0;
        while (i7 < i3) {
            int i8 = i + 1;
            int i9 = 255;
            int i10 = i8 + 1;
            int i11 = (((bArr[i] & 255) + (bArr[i8] << 8)) * i6) >> 16;
            if (i11 < 0) {
                if (-1 <= i11) {
                    i5 = 127;
                } else if (-31 <= i11) {
                    i5 = ((i11 + 31) >> 1) + 112;
                } else if (-95 <= i11) {
                    i5 = ((i11 + 95) >> 2) + 96;
                } else if (-223 <= i11) {
                    i5 = ((i11 + 223) >> 3) + 80;
                } else if (-479 <= i11) {
                    i5 = ((i11 + 479) >> 4) + 64;
                } else if (-991 <= i11) {
                    i5 = ((i11 + 991) >> 5) + 48;
                } else if (-2015 <= i11) {
                    i5 = ((i11 + 2015) >> 6) + 32;
                } else if (-4063 <= i11) {
                    i5 = ((i11 + 4063) >> 7) + 16;
                } else if (-8159 <= i11) {
                    i5 = ((i11 + 8159) >> 8) + 0;
                } else {
                    i9 = 0;
                }
                i9 = i5;
            } else if (i11 > 0) {
                i9 = i11 <= 30 ? ((30 - i11) >> 1) + 240 : i11 <= 94 ? ((94 - i11) >> 2) + 224 : i11 <= 222 ? ((222 - i11) >> 3) + 208 : i11 <= 478 ? ((478 - i11) >> 4) + 192 : i11 <= 990 ? ((990 - i11) >> 5) + 176 : i11 <= 2014 ? ((2014 - i11) >> 6) + 160 : i11 <= 4062 ? ((4062 - i11) >> 7) + 144 : i11 <= 8158 ? ((8158 - i11) >> 8) + 128 : 128;
            }
            bArr2[i2] = (byte) i9;
            i7++;
            i2++;
            i = i10;
        }
    }

    public static int maxAbsPcm(byte[] bArr, int i, int i2) {
        int i3 = 0;
        int i4 = 0;
        while (i3 < i2) {
            int i5 = i + 1;
            int i6 = i5 + 1;
            int i7 = (bArr[i] & 255) + (bArr[i5] << 8);
            if (i7 < 0) {
                i7 = -i7;
            }
            if (i7 > i4) {
                i4 = i7;
            }
            i3++;
            i = i6;
        }
        return i4;
    }

    public UlawEncoderInputStream(InputStream inputStream, int i) {
        this.mMax = 0;
        this.mIn = inputStream;
        this.mMax = i;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (this.mIn == null) {
            throw new IllegalStateException("not open");
        }
        while (true) {
            int i3 = this.mBufCount;
            if (i3 < 2) {
                InputStream inputStream = this.mIn;
                byte[] bArr2 = this.mBuf;
                int i4 = inputStream.read(bArr2, i3, Math.min(i2 * 2, bArr2.length - i3));
                if (i4 == -1) {
                    return -1;
                }
                this.mBufCount += i4;
            } else {
                int iMin = Math.min(i3 / 2, i2);
                encode(this.mBuf, 0, bArr, i, iMin, this.mMax);
                int i5 = iMin * 2;
                this.mBufCount -= i5;
                for (int i6 = 0; i6 < this.mBufCount; i6++) {
                    byte[] bArr3 = this.mBuf;
                    bArr3[i6] = bArr3[i6 + i5];
                }
                return iMin;
            }
        }
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (read(this.mOneByte, 0, 1) == -1) {
            return -1;
        }
        return this.mOneByte[0] & 255;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        InputStream inputStream = this.mIn;
        if (inputStream != null) {
            this.mIn = null;
            inputStream.close();
        }
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        return (this.mIn.available() + this.mBufCount) / 2;
    }
}
