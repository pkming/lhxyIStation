package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes3.dex */
public class OutputStreamFunneler {
    public static final long DEFAULT_TIMEOUT_MILLIS = 1000;
    private boolean closed;
    private int count;
    private OutputStream out;
    private long timeoutMillis;

    static /* synthetic */ int access$004(OutputStreamFunneler outputStreamFunneler) {
        int i = outputStreamFunneler.count + 1;
        outputStreamFunneler.count = i;
        return i;
    }

    private final class Funnel extends OutputStream {
        private boolean closed;

        private Funnel() {
            this.closed = false;
            synchronized (OutputStreamFunneler.this) {
                OutputStreamFunneler.access$004(OutputStreamFunneler.this);
            }
        }

        @Override // java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
            synchronized (OutputStreamFunneler.this) {
                OutputStreamFunneler.this.dieIfClosed();
                OutputStreamFunneler.this.out.flush();
            }
        }

        @Override // java.io.OutputStream
        public void write(int i) throws IOException {
            synchronized (OutputStreamFunneler.this) {
                OutputStreamFunneler.this.dieIfClosed();
                OutputStreamFunneler.this.out.write(i);
            }
        }

        @Override // java.io.OutputStream
        public void write(byte[] bArr) throws IOException {
            synchronized (OutputStreamFunneler.this) {
                OutputStreamFunneler.this.dieIfClosed();
                OutputStreamFunneler.this.out.write(bArr);
            }
        }

        @Override // java.io.OutputStream
        public void write(byte[] bArr, int i, int i2) throws IOException {
            synchronized (OutputStreamFunneler.this) {
                OutputStreamFunneler.this.dieIfClosed();
                OutputStreamFunneler.this.out.write(bArr, i, i2);
            }
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            OutputStreamFunneler.this.release(this);
        }
    }

    public OutputStreamFunneler(OutputStream outputStream) {
        this(outputStream, 1000L);
    }

    public OutputStreamFunneler(OutputStream outputStream, long j) {
        this.count = 0;
        if (outputStream == null) {
            throw new IllegalArgumentException("OutputStreamFunneler.<init>:  out == null");
        }
        this.out = outputStream;
        this.closed = false;
        setTimeout(j);
    }

    public synchronized void setTimeout(long j) {
        this.timeoutMillis = j;
    }

    public synchronized OutputStream getFunnelInstance() throws IOException {
        dieIfClosed();
        try {
        } finally {
            notifyAll();
        }
        return new Funnel();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void release(Funnel funnel) throws IOException {
        if (!funnel.closed) {
            try {
                long j = this.timeoutMillis;
                if (j > 0) {
                    try {
                        wait(j);
                    } catch (InterruptedException unused) {
                    }
                }
                int i = this.count - 1;
                this.count = i;
                if (i == 0) {
                    close();
                }
                funnel.closed = true;
            } catch (Throwable th) {
                funnel.closed = true;
                throw th;
            }
        }
    }

    private synchronized void close() throws IOException {
        try {
            dieIfClosed();
            this.out.close();
        } finally {
            this.closed = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void dieIfClosed() throws IOException {
        if (this.closed) {
            throw new IOException("The funneled OutputStream has been closed.");
        }
    }
}
