package com.lianhexinye.m90.common.utils;

import com.lianhexinye.m90.common.utils.FtpUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes2.dex */
public class ProgressFtpInputStream extends InputStream {
    private static final int TEN_KILOBYTES = 10240;
    private long fileLength;
    private InputStream inputStream;
    private FtpUtil.FtpTcpProgressListener listener;
    private File localFile;
    private long progress = 0;
    private long lastUpdate = 0;
    private boolean closed = false;
    private int progressPercent = 0;

    public ProgressFtpInputStream(InputStream inputStream, FtpUtil.FtpTcpProgressListener ftpTcpProgressListener, File file) {
        this.inputStream = inputStream;
        this.listener = ftpTcpProgressListener;
        this.localFile = file;
        this.fileLength = file.length();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        return incrementCounterAndUpdateDisplay(this.inputStream.read());
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        return incrementCounterAndUpdateDisplay(this.inputStream.read(bArr, i, i2));
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        super.close();
        if (this.closed) {
            throw new IOException("already closed");
        }
        this.closed = true;
    }

    private int incrementCounterAndUpdateDisplay(int i) {
        if (i > 0) {
            this.progress += (long) i;
        }
        this.lastUpdate = maybeUpdateDisplay(this.progress, this.lastUpdate);
        return i;
    }

    private long maybeUpdateDisplay(long j, long j2) {
        if (j - j2 <= 10240) {
            return j2;
        }
        int i = (int) ((100 * j) / this.fileLength);
        this.progressPercent = i;
        this.listener.onFtpTcpProgress(6, i, this.localFile);
        return j;
    }
}
