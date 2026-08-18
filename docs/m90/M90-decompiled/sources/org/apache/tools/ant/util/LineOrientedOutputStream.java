package org.apache.tools.ant.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes3.dex */
public abstract class LineOrientedOutputStream extends OutputStream {
    private static final int CR = 13;
    private static final int INTIAL_SIZE = 132;
    private static final int LF = 10;
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream(132);
    private boolean skip = false;

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
    }

    protected abstract void processLine(String str) throws IOException;

    @Override // java.io.OutputStream
    public final void write(int i) throws IOException {
        byte b = (byte) i;
        if (b == 10 || b == 13) {
            if (!this.skip) {
                processBuffer();
            }
        } else {
            this.buffer.write(i);
        }
        this.skip = b == 13;
    }

    protected void processBuffer() throws IOException {
        try {
            processLine(this.buffer.toByteArray());
        } finally {
            this.buffer.reset();
        }
    }

    protected void processLine(byte[] bArr) throws IOException {
        processLine(new String(bArr));
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.buffer.size() > 0) {
            processBuffer();
        }
        super.close();
    }

    @Override // java.io.OutputStream
    public final void write(byte[] bArr, int i, int i2) throws IOException {
        while (i2 > 0) {
            int i3 = i;
            while (i2 > 0 && bArr[i3] != 10 && bArr[i3] != 13) {
                i3++;
                i2--;
            }
            int i4 = i3 - i;
            if (i4 > 0) {
                this.buffer.write(bArr, i, i4);
            }
            i = i3;
            while (i2 > 0 && (bArr[i] == 10 || bArr[i] == 13)) {
                write(bArr[i]);
                i++;
                i2--;
            }
        }
    }
}
