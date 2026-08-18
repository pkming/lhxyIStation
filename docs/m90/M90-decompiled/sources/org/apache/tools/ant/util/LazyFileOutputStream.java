package org.apache.tools.ant.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes3.dex */
public class LazyFileOutputStream extends OutputStream {
    private boolean alwaysCreate;
    private boolean append;
    private boolean closed;
    private File file;
    private FileOutputStream fos;
    private boolean opened;

    public LazyFileOutputStream(String str) {
        this(str, false);
    }

    public LazyFileOutputStream(String str, boolean z) {
        this(new File(str), z);
    }

    public LazyFileOutputStream(File file) {
        this(file, false);
    }

    public LazyFileOutputStream(File file, boolean z) {
        this(file, z, false);
    }

    public LazyFileOutputStream(File file, boolean z, boolean z2) {
        this.opened = false;
        this.closed = false;
        this.file = file;
        this.append = z;
        this.alwaysCreate = z2;
    }

    public void open() throws IOException {
        ensureOpened();
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public synchronized void close() throws IOException {
        if (this.alwaysCreate && !this.closed) {
            ensureOpened();
        }
        if (this.opened) {
            this.fos.close();
        }
        this.closed = true;
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr) throws IOException {
        write(bArr, 0, bArr.length);
    }

    @Override // java.io.OutputStream
    public synchronized void write(byte[] bArr, int i, int i2) throws IOException {
        ensureOpened();
        this.fos.write(bArr, i, i2);
    }

    @Override // java.io.OutputStream
    public synchronized void write(int i) throws IOException {
        ensureOpened();
        this.fos.write(i);
    }

    private synchronized void ensureOpened() throws IOException {
        if (this.closed) {
            throw new IOException(this.file + " has already been closed.");
        }
        if (!this.opened) {
            this.fos = new FileOutputStream(this.file.getAbsolutePath(), this.append);
            this.opened = true;
        }
    }
}
