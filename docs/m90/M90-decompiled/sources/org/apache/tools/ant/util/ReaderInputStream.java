package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/* JADX INFO: loaded from: classes3.dex */
public class ReaderInputStream extends InputStream {
    private static final int BYTE_MASK = 255;
    private int begin;
    private String encoding;
    private Reader in;
    private byte[] slack;

    @Override // java.io.InputStream
    public boolean markSupported() {
        return false;
    }

    public ReaderInputStream(Reader reader) {
        this.encoding = System.getProperty("file.encoding");
        this.in = reader;
    }

    public ReaderInputStream(Reader reader, String str) {
        this(reader);
        if (str == null) {
            throw new IllegalArgumentException("encoding must not be null");
        }
        this.encoding = str;
    }

    @Override // java.io.InputStream
    public synchronized int read() throws IOException {
        byte b;
        int i;
        if (this.in == null) {
            throw new IOException("Stream Closed");
        }
        byte[] bArr = this.slack;
        if (bArr != null && (i = this.begin) < bArr.length) {
            b = bArr[i];
            int i2 = i + 1;
            this.begin = i2;
            if (i2 == bArr.length) {
                this.slack = null;
            }
        } else {
            byte[] bArr2 = new byte[1];
            if (read(bArr2, 0, 1) <= 0) {
                return -1;
            }
            b = bArr2[0];
        }
        return b & 255;
    }

    @Override // java.io.InputStream
    public synchronized int read(byte[] bArr, int i, int i2) throws IOException {
        if (this.in == null) {
            throw new IOException("Stream Closed");
        }
        if (i2 == 0) {
            return 0;
        }
        while (true) {
            byte[] bArr2 = this.slack;
            if (bArr2 == null) {
                char[] cArr = new char[i2];
                int i3 = this.in.read(cArr);
                if (i3 == -1) {
                    return -1;
                }
                if (i3 > 0) {
                    this.slack = new String(cArr, 0, i3).getBytes(this.encoding);
                    this.begin = 0;
                }
            } else {
                int length = bArr2.length;
                int i4 = this.begin;
                if (i2 > length - i4) {
                    i2 = bArr2.length - i4;
                }
                System.arraycopy(bArr2, i4, bArr, i, i2);
                int i5 = this.begin + i2;
                this.begin = i5;
                if (i5 >= this.slack.length) {
                    this.slack = null;
                }
                return i2;
            }
        }
    }

    @Override // java.io.InputStream
    public synchronized void mark(int i) {
        try {
            this.in.mark(i);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override // java.io.InputStream
    public synchronized int available() throws IOException {
        Reader reader = this.in;
        if (reader == null) {
            throw new IOException("Stream Closed");
        }
        byte[] bArr = this.slack;
        if (bArr != null) {
            return bArr.length - this.begin;
        }
        return reader.ready() ? 1 : 0;
    }

    @Override // java.io.InputStream
    public synchronized void reset() throws IOException {
        Reader reader = this.in;
        if (reader == null) {
            throw new IOException("Stream Closed");
        }
        this.slack = null;
        reader.reset();
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public synchronized void close() throws IOException {
        Reader reader = this.in;
        if (reader != null) {
            reader.close();
            this.slack = null;
            this.in = null;
        }
    }
}
