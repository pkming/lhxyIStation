package org.apache.poi.poifs.filesystem;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/* JADX INFO: loaded from: classes3.dex */
public class DocumentOutputStream extends OutputStream {
    private int limit;
    private OutputStream stream;
    private int written = 0;

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
    }

    DocumentOutputStream(OutputStream outputStream, int i) {
        this.stream = outputStream;
        this.limit = i;
    }

    @Override // java.io.OutputStream
    public void write(int i) throws IOException {
        limitCheck(1);
        this.stream.write(i);
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr) throws IOException {
        write(bArr, 0, bArr.length);
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) throws IOException {
        limitCheck(i2);
        this.stream.write(bArr, i, i2);
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.stream.flush();
    }

    void writeFiller(int i, byte b) throws IOException {
        int i2 = this.written;
        if (i > i2) {
            byte[] bArr = new byte[i - i2];
            Arrays.fill(bArr, b);
            this.stream.write(bArr);
        }
    }

    private void limitCheck(int i) throws IOException {
        int i2 = this.written;
        if (i2 + i > this.limit) {
            throw new IOException("tried to write too much data");
        }
        this.written = i2 + i;
    }
}
