package org.apache.commons.net.telnet;

import android.opengl.GLES10;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes3.dex */
final class TelnetInputStream extends BufferedInputStream implements Runnable {
    private static final int EOF = -1;
    private static final int WOULD_BLOCK = -2;
    static final int _STATE_CR = 8;
    static final int _STATE_DATA = 0;
    static final int _STATE_DO = 4;
    static final int _STATE_DONT = 5;
    static final int _STATE_IAC = 1;
    static final int _STATE_IAC_SB = 9;
    static final int _STATE_SB = 6;
    static final int _STATE_SE = 7;
    static final int _STATE_WILL = 2;
    static final int _STATE_WONT = 3;
    private int __bytesAvailable;
    private final TelnetClient __client;
    private boolean __hasReachedEOF;
    private IOException __ioException;
    private volatile boolean __isClosed;
    private final int[] __queue;
    private int __queueHead;
    private int __queueTail;
    private boolean __readIsWaiting;
    private int __receiveState;
    private final int[] __suboption;
    private int __suboption_count;
    private final Thread __thread;
    private volatile boolean __threaded;

    @Override // java.io.BufferedInputStream, java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        return false;
    }

    TelnetInputStream(InputStream inputStream, TelnetClient telnetClient, boolean z) {
        super(inputStream);
        this.__suboption = new int[512];
        this.__suboption_count = 0;
        this.__client = telnetClient;
        this.__receiveState = 0;
        this.__isClosed = true;
        this.__hasReachedEOF = false;
        this.__queue = new int[GLES10.GL_EXP2];
        this.__queueHead = 0;
        this.__queueTail = 0;
        this.__bytesAvailable = 0;
        this.__ioException = null;
        this.__readIsWaiting = false;
        this.__threaded = false;
        if (z) {
            this.__thread = new Thread(this);
        } else {
            this.__thread = null;
        }
    }

    TelnetInputStream(InputStream inputStream, TelnetClient telnetClient) {
        this(inputStream, telnetClient, true);
    }

    void _start() {
        if (this.__thread == null) {
            return;
        }
        this.__isClosed = false;
        int priority = Thread.currentThread().getPriority() + 1;
        if (priority > 10) {
            priority = 10;
        }
        this.__thread.setPriority(priority);
        this.__thread.setDaemon(true);
        this.__thread.start();
        this.__threaded = true;
    }

    /* JADX WARN: Removed duplicated region for block: B:132:0x0100 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:84:0x00fb  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int __read(boolean r7) throws java.io.IOException {
        /*
            Method dump skipped, instruction units count: 330
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.net.telnet.TelnetInputStream.__read(boolean):int");
    }

    private boolean __processChar(int i) throws InterruptedException {
        boolean z;
        synchronized (this.__queue) {
            z = this.__bytesAvailable == 0;
            while (this.__bytesAvailable >= this.__queue.length - 1) {
                if (this.__threaded) {
                    this.__queue.notify();
                    try {
                        this.__queue.wait();
                    } catch (InterruptedException e) {
                        throw e;
                    }
                } else {
                    throw new IllegalStateException("Queue is full! Cannot process another character.");
                }
            }
            if (this.__readIsWaiting && this.__threaded) {
                this.__queue.notify();
            }
            int[] iArr = this.__queue;
            int i2 = this.__queueTail;
            iArr[i2] = i;
            this.__bytesAvailable++;
            int i3 = i2 + 1;
            this.__queueTail = i3;
            if (i3 >= iArr.length) {
                this.__queueTail = 0;
            }
        }
        return z;
    }

    /* JADX WARN: Code restructure failed: missing block: B:40:0x005a, code lost:
    
        r7.__readIsWaiting = false;
     */
    @Override // java.io.BufferedInputStream, java.io.FilterInputStream, java.io.InputStream
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int read() throws java.io.IOException {
        /*
            r7 = this;
            int[] r0 = r7.__queue
            monitor-enter(r0)
        L3:
            java.io.IOException r1 = r7.__ioException     // Catch: java.lang.Throwable -> L97
            if (r1 != 0) goto L93
            int r1 = r7.__bytesAvailable     // Catch: java.lang.Throwable -> L97
            r2 = 0
            r3 = 1
            if (r1 != 0) goto L75
            boolean r1 = r7.__hasReachedEOF     // Catch: java.lang.Throwable -> L97
            r4 = -1
            if (r1 == 0) goto L14
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L97
            return r4
        L14:
            boolean r1 = r7.__threaded     // Catch: java.lang.Throwable -> L97
            if (r1 == 0) goto L2f
            int[] r1 = r7.__queue     // Catch: java.lang.Throwable -> L97
            r1.notify()     // Catch: java.lang.Throwable -> L97
            r7.__readIsWaiting = r3     // Catch: java.lang.InterruptedException -> L27 java.lang.Throwable -> L97
            int[] r1 = r7.__queue     // Catch: java.lang.InterruptedException -> L27 java.lang.Throwable -> L97
            r1.wait()     // Catch: java.lang.InterruptedException -> L27 java.lang.Throwable -> L97
            r7.__readIsWaiting = r2     // Catch: java.lang.InterruptedException -> L27 java.lang.Throwable -> L97
            goto L3
        L27:
            java.io.InterruptedIOException r1 = new java.io.InterruptedIOException     // Catch: java.lang.Throwable -> L97
            java.lang.String r2 = "Fatal thread interruption during read."
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L97
            throw r1     // Catch: java.lang.Throwable -> L97
        L2f:
            r7.__readIsWaiting = r3     // Catch: java.lang.Throwable -> L97
            r1 = r3
        L32:
            int r1 = r7.__read(r1)     // Catch: java.io.InterruptedIOException -> L5d java.lang.Throwable -> L97
            r5 = -2
            if (r1 >= 0) goto L3d
            if (r1 == r5) goto L3d
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L97
            return r1
        L3d:
            if (r1 == r5) goto L49
            r7.__processChar(r1)     // Catch: java.lang.InterruptedException -> L43 java.lang.Throwable -> L97
            goto L49
        L43:
            boolean r1 = r7.__isClosed     // Catch: java.lang.Throwable -> L97
            if (r1 == 0) goto L49
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L97
            return r4
        L49:
            int r1 = super.available()     // Catch: java.lang.Throwable -> L97
            if (r1 <= 0) goto L5a
            int r1 = r7.__bytesAvailable     // Catch: java.lang.Throwable -> L97
            int[] r5 = r7.__queue     // Catch: java.lang.Throwable -> L97
            int r5 = r5.length     // Catch: java.lang.Throwable -> L97
            int r5 = r5 - r3
            if (r1 < r5) goto L58
            goto L5a
        L58:
            r1 = r2
            goto L32
        L5a:
            r7.__readIsWaiting = r2     // Catch: java.lang.Throwable -> L97
            goto L3
        L5d:
            r1 = move-exception
            int[] r2 = r7.__queue     // Catch: java.lang.Throwable -> L97
            monitor-enter(r2)     // Catch: java.lang.Throwable -> L97
            r7.__ioException = r1     // Catch: java.lang.Throwable -> L72
            int[] r1 = r7.__queue     // Catch: java.lang.Throwable -> L72
            r1.notifyAll()     // Catch: java.lang.Throwable -> L72
            int[] r1 = r7.__queue     // Catch: java.lang.InterruptedException -> L6f java.lang.Throwable -> L72
            r5 = 100
            r1.wait(r5)     // Catch: java.lang.InterruptedException -> L6f java.lang.Throwable -> L72
        L6f:
            monitor-exit(r2)     // Catch: java.lang.Throwable -> L72
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L97
            return r4
        L72:
            r1 = move-exception
            monitor-exit(r2)     // Catch: java.lang.Throwable -> L72
            throw r1     // Catch: java.lang.Throwable -> L97
        L75:
            int[] r4 = r7.__queue     // Catch: java.lang.Throwable -> L97
            int r5 = r7.__queueHead     // Catch: java.lang.Throwable -> L97
            r6 = r4[r5]     // Catch: java.lang.Throwable -> L97
            int r5 = r5 + r3
            r7.__queueHead = r5     // Catch: java.lang.Throwable -> L97
            int r4 = r4.length     // Catch: java.lang.Throwable -> L97
            if (r5 < r4) goto L83
            r7.__queueHead = r2     // Catch: java.lang.Throwable -> L97
        L83:
            int r1 = r1 - r3
            r7.__bytesAvailable = r1     // Catch: java.lang.Throwable -> L97
            if (r1 != 0) goto L91
            boolean r1 = r7.__threaded     // Catch: java.lang.Throwable -> L97
            if (r1 == 0) goto L91
            int[] r1 = r7.__queue     // Catch: java.lang.Throwable -> L97
            r1.notify()     // Catch: java.lang.Throwable -> L97
        L91:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L97
            return r6
        L93:
            r2 = 0
            r7.__ioException = r2     // Catch: java.lang.Throwable -> L97
            throw r1     // Catch: java.lang.Throwable -> L97
        L97:
            r1 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L97
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.net.telnet.TelnetInputStream.read():int");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }

    @Override // java.io.BufferedInputStream, java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        int i3;
        if (i2 < 1) {
            return 0;
        }
        synchronized (this.__queue) {
            int i4 = this.__bytesAvailable;
            if (i2 > i4) {
                i2 = i4;
            }
        }
        int i5 = read();
        if (i5 == -1) {
            return -1;
        }
        int i6 = i;
        while (true) {
            i3 = i6 + 1;
            bArr[i6] = (byte) i5;
            i2--;
            if (i2 <= 0 || (i5 = read()) == -1) {
                break;
            }
            i6 = i3;
        }
        return i3 - i;
    }

    @Override // java.io.BufferedInputStream, java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        synchronized (this.__queue) {
            if (this.__threaded) {
                return this.__bytesAvailable;
            }
            return this.__bytesAvailable + super.available();
        }
    }

    @Override // java.io.BufferedInputStream, java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        super.close();
        synchronized (this.__queue) {
            this.__hasReachedEOF = true;
            this.__isClosed = true;
            Thread thread = this.__thread;
            if (thread != null && thread.isAlive()) {
                this.__thread.interrupt();
            }
            this.__queue.notifyAll();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:55:0x0051 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @Override // java.lang.Runnable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void run() {
        /*
            r6 = this;
        L0:
            r0 = 0
            r1 = 1
            boolean r2 = r6.__isClosed     // Catch: java.io.IOException -> L42
            if (r2 != 0) goto L4e
            int r2 = r6.__read(r1)     // Catch: java.lang.RuntimeException -> L20 java.io.InterruptedIOException -> L24 java.io.IOException -> L42
            if (r2 >= 0) goto Ld
            goto L4e
        Ld:
            boolean r2 = r6.__processChar(r2)     // Catch: java.lang.InterruptedException -> L12 java.io.IOException -> L42
            goto L18
        L12:
            boolean r2 = r6.__isClosed     // Catch: java.io.IOException -> L42
            if (r2 == 0) goto L17
            goto L4e
        L17:
            r2 = r0
        L18:
            if (r2 == 0) goto L0
            org.apache.commons.net.telnet.TelnetClient r2 = r6.__client     // Catch: java.io.IOException -> L42
            r2.notifyInputListener()     // Catch: java.io.IOException -> L42
            goto L0
        L20:
            super.close()     // Catch: java.io.IOException -> L42
            goto L4e
        L24:
            r2 = move-exception
            int[] r3 = r6.__queue     // Catch: java.io.IOException -> L42
            monitor-enter(r3)     // Catch: java.io.IOException -> L42
            r6.__ioException = r2     // Catch: java.lang.Throwable -> L3f
            int[] r2 = r6.__queue     // Catch: java.lang.Throwable -> L3f
            r2.notifyAll()     // Catch: java.lang.Throwable -> L3f
            int[] r2 = r6.__queue     // Catch: java.lang.InterruptedException -> L37 java.lang.Throwable -> L3f
            r4 = 100
            r2.wait(r4)     // Catch: java.lang.InterruptedException -> L37 java.lang.Throwable -> L3f
            goto L3d
        L37:
            boolean r2 = r6.__isClosed     // Catch: java.lang.Throwable -> L3f
            if (r2 == 0) goto L3d
            monitor-exit(r3)     // Catch: java.lang.Throwable -> L3f
            goto L4e
        L3d:
            monitor-exit(r3)     // Catch: java.lang.Throwable -> L3f
            goto L0
        L3f:
            r2 = move-exception
            monitor-exit(r3)     // Catch: java.lang.Throwable -> L3f
            throw r2     // Catch: java.io.IOException -> L42
        L42:
            r2 = move-exception
            int[] r3 = r6.__queue
            monitor-enter(r3)
            r6.__ioException = r2     // Catch: java.lang.Throwable -> L61
            monitor-exit(r3)     // Catch: java.lang.Throwable -> L61
            org.apache.commons.net.telnet.TelnetClient r2 = r6.__client
            r2.notifyInputListener()
        L4e:
            int[] r2 = r6.__queue
            monitor-enter(r2)
            r6.__isClosed = r1     // Catch: java.lang.Throwable -> L5e
            r6.__hasReachedEOF = r1     // Catch: java.lang.Throwable -> L5e
            int[] r1 = r6.__queue     // Catch: java.lang.Throwable -> L5e
            r1.notify()     // Catch: java.lang.Throwable -> L5e
            monitor-exit(r2)     // Catch: java.lang.Throwable -> L5e
            r6.__threaded = r0
            return
        L5e:
            r0 = move-exception
            monitor-exit(r2)     // Catch: java.lang.Throwable -> L5e
            throw r0
        L61:
            r0 = move-exception
            monitor-exit(r3)     // Catch: java.lang.Throwable -> L61
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.net.telnet.TelnetInputStream.run():void");
    }
}
