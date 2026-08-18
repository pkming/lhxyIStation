package android.drm;

import android.util.Log;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.UnknownServiceException;
import java.util.Arrays;
import libcore.io.Streams;

/* JADX INFO: loaded from: classes.dex */
public class DrmOutputStream extends OutputStream {
    private static final String TAG = "DrmOutputStream";
    private final DrmManagerClient mClient;
    private final RandomAccessFile mFile;
    private int mSessionId;

    public DrmOutputStream(DrmManagerClient drmManagerClient, RandomAccessFile randomAccessFile, String str) throws IOException {
        this.mSessionId = -1;
        this.mClient = drmManagerClient;
        this.mFile = randomAccessFile;
        int iOpenConvertSession = drmManagerClient.openConvertSession(str);
        this.mSessionId = iOpenConvertSession;
        if (iOpenConvertSession == -1) {
            throw new UnknownServiceException("Failed to open DRM session for " + str);
        }
    }

    public void finish() throws IOException {
        DrmConvertedStatus drmConvertedStatusCloseConvertSession = this.mClient.closeConvertSession(this.mSessionId);
        if (drmConvertedStatusCloseConvertSession.statusCode == 1) {
            this.mFile.seek(drmConvertedStatusCloseConvertSession.offset);
            this.mFile.write(drmConvertedStatusCloseConvertSession.convertedData);
            this.mSessionId = -1;
            return;
        }
        throw new IOException("Unexpected DRM status: " + drmConvertedStatusCloseConvertSession.statusCode);
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.mSessionId == -1) {
            Log.w(TAG, "Closing stream without finishing");
        }
        this.mFile.close();
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) throws IOException {
        Arrays.checkOffsetAndCount(bArr.length, i, i2);
        if (i2 != bArr.length) {
            byte[] bArr2 = new byte[i2];
            System.arraycopy(bArr, i, bArr2, 0, i2);
            bArr = bArr2;
        }
        DrmConvertedStatus drmConvertedStatusConvertData = this.mClient.convertData(this.mSessionId, bArr);
        if (drmConvertedStatusConvertData.statusCode == 1) {
            this.mFile.write(drmConvertedStatusConvertData.convertedData);
            return;
        }
        throw new IOException("Unexpected DRM status: " + drmConvertedStatusConvertData.statusCode);
    }

    @Override // java.io.OutputStream
    public void write(int i) throws IOException {
        Streams.writeSingleByte(this, i);
    }
}
