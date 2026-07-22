package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class StreamPumper implements Runnable {
    private static final long POLL_INTERVAL = 100;
    private static final int SMALL_BUFFER_SIZE = 128;
    private boolean autoflush;
    private int bufferSize;
    private final boolean closeWhenExhausted;
    private Exception exception;
    private volatile boolean finish;
    private volatile boolean finished;
    private final InputStream is;
    private final OutputStream os;
    private boolean started;
    private final boolean useAvailable;

    public StreamPumper(InputStream inputStream, OutputStream outputStream, boolean z) {
        this(inputStream, outputStream, z, false);
    }

    public StreamPumper(InputStream inputStream, OutputStream outputStream, boolean z, boolean z2) {
        this.autoflush = false;
        this.exception = null;
        this.bufferSize = 128;
        this.started = false;
        this.is = inputStream;
        this.os = outputStream;
        this.closeWhenExhausted = z;
        this.useAvailable = z2;
    }

    public StreamPumper(InputStream inputStream, OutputStream outputStream) {
        this(inputStream, outputStream, false);
    }

    void setAutoflush(boolean z) {
        this.autoflush = z;
    }

    @Override // java.lang.Runnable
    public void run() {
        int i;
        int i2;
        synchronized (this) {
            this.started = true;
        }
        this.finished = false;
        int i3 = this.bufferSize;
        byte[] bArr = new byte[i3];
        do {
            try {
                try {
                    waitForInput(this.is);
                    if (this.finish || Thread.interrupted() || (i2 = this.is.read(bArr)) <= 0 || Thread.interrupted()) {
                        break;
                    }
                    this.os.write(bArr, 0, i2);
                    if (this.autoflush) {
                        this.os.flush();
                    }
                } catch (InterruptedException unused) {
                    if (this.closeWhenExhausted) {
                        FileUtils.close(this.os);
                    }
                    this.finished = true;
                    this.finish = false;
                    synchronized (this) {
                        notifyAll();
                        return;
                    }
                } catch (Exception e) {
                    synchronized (this) {
                        this.exception = e;
                        if (this.closeWhenExhausted) {
                            FileUtils.close(this.os);
                        }
                        this.finished = true;
                        this.finish = false;
                        synchronized (this) {
                            notifyAll();
                            return;
                        }
                    }
                }
            } catch (Throwable th) {
                if (this.closeWhenExhausted) {
                    FileUtils.close(this.os);
                }
                this.finished = true;
                this.finish = false;
                synchronized (this) {
                    notifyAll();
                    throw th;
                }
            }
        } while (!this.finish);
        if (this.finish) {
            while (true) {
                int iAvailable = this.is.available();
                if (iAvailable <= 0 || Thread.interrupted() || (i = this.is.read(bArr, 0, Math.min(iAvailable, i3))) <= 0) {
                    break;
                } else {
                    this.os.write(bArr, 0, i);
                }
            }
        }
        this.os.flush();
        if (this.closeWhenExhausted) {
            FileUtils.close(this.os);
        }
        this.finished = true;
        this.finish = false;
        synchronized (this) {
            notifyAll();
        }
    }

    public boolean isFinished() {
        return this.finished;
    }

    public synchronized void waitFor() throws InterruptedException {
        while (!isFinished()) {
            wait();
        }
    }

    public synchronized void setBufferSize(int i) {
        if (this.started) {
            throw new IllegalStateException("Cannot set buffer size on a running StreamPumper");
        }
        this.bufferSize = i;
    }

    public synchronized int getBufferSize() {
        return this.bufferSize;
    }

    public synchronized Exception getException() {
        return this.exception;
    }

    synchronized void stop() {
        this.finish = true;
        notifyAll();
    }

    private void waitForInput(InputStream inputStream) throws InterruptedException, IOException {
        if (this.useAvailable) {
            while (!this.finish && inputStream.available() == 0) {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                synchronized (this) {
                    wait(POLL_INTERVAL);
                }
            }
        }
    }
}
