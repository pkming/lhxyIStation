package com.lianhexinye.m90.common.utils;

import com.lianhexinye.m90.common.utils.FtpUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes2.dex */
public class ProgressInputStream extends InputStream {
    private static final int TEN_KILOBYTES = 10240;
    private InputStream inputStream;
    private FtpUtil.FtpProgressListener listener;
    private File localFile;
    private long progress = 0;
    private long lastUpdate = 0;
    private boolean closed = false;

    public ProgressInputStream(InputStream inputStream, FtpUtil.FtpProgressListener ftpProgressListener, File file) {
        this.inputStream = inputStream;
        this.listener = ftpProgressListener;
        this.localFile = file;
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
        this.listener.onFtpProgress(6, j, 0L, 0L, this.localFile);
        return j;
    }
}
