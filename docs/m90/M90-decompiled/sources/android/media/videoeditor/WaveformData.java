package android.media.videoeditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class WaveformData {
    private final int mFrameDurationMs;
    private final int mFramesCount;
    private final short[] mGains;

    private WaveformData() throws IOException {
        this.mFrameDurationMs = 0;
        this.mFramesCount = 0;
        this.mGains = null;
    }

    WaveformData(String str) throws Throwable {
        Throwable th;
        FileInputStream fileInputStream;
        if (str == null) {
            throw new IllegalArgumentException("WaveformData : filename is null");
        }
        try {
            fileInputStream = new FileInputStream(new File(str));
            try {
                byte[] bArr = new byte[4];
                fileInputStream.read(bArr, 0, 4);
                int i = 0;
                for (int i2 = 0; i2 < 4; i2++) {
                    i = (i << 8) | (bArr[i2] & 255);
                }
                this.mFrameDurationMs = i;
                byte[] bArr2 = new byte[4];
                fileInputStream.read(bArr2, 0, 4);
                int i3 = 0;
                for (int i4 = 0; i4 < 4; i4++) {
                    i3 = (i3 << 8) | (bArr2[i4] & 255);
                }
                this.mFramesCount = i3;
                this.mGains = new short[i3];
                for (int i5 = 0; i5 < this.mFramesCount; i5++) {
                    this.mGains[i5] = (short) fileInputStream.read();
                }
                fileInputStream.close();
            } catch (Throwable th2) {
                th = th2;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            fileInputStream = null;
        }
    }

    public int getFrameDuration() {
        return this.mFrameDurationMs;
    }

    public int getFramesCount() {
        return this.mFramesCount;
    }

    public short[] getFrameGains() {
        return this.mGains;
    }
}
