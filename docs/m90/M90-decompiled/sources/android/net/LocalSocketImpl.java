package android.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import libcore.io.ErrnoException;
import libcore.io.Libcore;
import libcore.io.OsConstants;

/* JADX INFO: loaded from: classes.dex */
class LocalSocketImpl {
    private FileDescriptor fd;
    private SocketInputStream fis;
    private SocketOutputStream fos;
    FileDescriptor[] inboundFileDescriptors;
    private boolean mFdCreatedInternally;
    FileDescriptor[] outboundFileDescriptors;
    private Object readMonitor = new Object();
    private Object writeMonitor = new Object();

    private native FileDescriptor accept(FileDescriptor fileDescriptor, LocalSocketImpl localSocketImpl) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native int available_native(FileDescriptor fileDescriptor) throws IOException;

    private native void bindLocal(FileDescriptor fileDescriptor, String str, int i) throws IOException;

    private native void connectLocal(FileDescriptor fileDescriptor, String str, int i) throws IOException;

    private native int getOption_native(FileDescriptor fileDescriptor, int i) throws IOException;

    private native Credentials getPeerCredentials_native(FileDescriptor fileDescriptor) throws IOException;

    private native void listen_native(FileDescriptor fileDescriptor, int i) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native int pending_native(FileDescriptor fileDescriptor) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native int read_native(FileDescriptor fileDescriptor) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native int readba_native(byte[] bArr, int i, int i2, FileDescriptor fileDescriptor) throws IOException;

    private native void setOption_native(FileDescriptor fileDescriptor, int i, int i2, int i3) throws IOException;

    private native void shutdown(FileDescriptor fileDescriptor, boolean z);

    /* JADX INFO: Access modifiers changed from: private */
    public native void write_native(int i, FileDescriptor fileDescriptor) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native void writeba_native(byte[] bArr, int i, int i2, FileDescriptor fileDescriptor) throws IOException;

    public LocalSocketAddress getSockAddress() throws IOException {
        return null;
    }

    protected boolean supportsUrgentData() {
        return false;
    }

    class SocketInputStream extends InputStream {
        SocketInputStream() {
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            LocalSocketImpl localSocketImpl = LocalSocketImpl.this;
            return localSocketImpl.available_native(localSocketImpl.fd);
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            LocalSocketImpl.this.close();
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            int i;
            synchronized (LocalSocketImpl.this.readMonitor) {
                FileDescriptor fileDescriptor = LocalSocketImpl.this.fd;
                if (fileDescriptor != null) {
                    i = LocalSocketImpl.this.read_native(fileDescriptor);
                } else {
                    throw new IOException("socket closed");
                }
            }
            return i;
        }

        @Override // java.io.InputStream
        public int read(byte[] bArr) throws IOException {
            return read(bArr, 0, bArr.length);
        }

        @Override // java.io.InputStream
        public int read(byte[] bArr, int i, int i2) throws IOException {
            int i3;
            synchronized (LocalSocketImpl.this.readMonitor) {
                FileDescriptor fileDescriptor = LocalSocketImpl.this.fd;
                if (fileDescriptor == null) {
                    throw new IOException("socket closed");
                }
                if (i >= 0 && i2 >= 0 && i + i2 <= bArr.length) {
                    i3 = LocalSocketImpl.this.readba_native(bArr, i, i2, fileDescriptor);
                } else {
                    throw new ArrayIndexOutOfBoundsException();
                }
            }
            return i3;
        }
    }

    class SocketOutputStream extends OutputStream {
        SocketOutputStream() {
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            LocalSocketImpl.this.close();
        }

        @Override // java.io.OutputStream
        public void write(byte[] bArr) throws IOException {
            write(bArr, 0, bArr.length);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bArr, int i, int i2) throws IOException {
            synchronized (LocalSocketImpl.this.writeMonitor) {
                FileDescriptor fileDescriptor = LocalSocketImpl.this.fd;
                if (fileDescriptor == null) {
                    throw new IOException("socket closed");
                }
                if (i >= 0 && i2 >= 0 && i + i2 <= bArr.length) {
                    LocalSocketImpl.this.writeba_native(bArr, i, i2, fileDescriptor);
                } else {
                    throw new ArrayIndexOutOfBoundsException();
                }
            }
        }

        @Override // java.io.OutputStream
        public void write(int i) throws IOException {
            synchronized (LocalSocketImpl.this.writeMonitor) {
                FileDescriptor fileDescriptor = LocalSocketImpl.this.fd;
                if (fileDescriptor != null) {
                    LocalSocketImpl.this.write_native(i, fileDescriptor);
                } else {
                    throw new IOException("socket closed");
                }
            }
        }

        @Override // java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
            FileDescriptor fileDescriptor = LocalSocketImpl.this.fd;
            if (fileDescriptor != null) {
                while (LocalSocketImpl.this.pending_native(fileDescriptor) > 0) {
                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException unused) {
                        return;
                    }
                }
                return;
            }
            throw new IOException("socket closed");
        }
    }

    LocalSocketImpl() {
    }

    LocalSocketImpl(FileDescriptor fileDescriptor) throws IOException {
        this.fd = fileDescriptor;
    }

    public String toString() {
        return super.toString() + " fd:" + this.fd;
    }

    public void create(int i) throws IOException {
        int i2;
        if (this.fd == null) {
            if (i == 1) {
                i2 = OsConstants.SOCK_DGRAM;
            } else if (i == 2) {
                i2 = OsConstants.SOCK_STREAM;
            } else if (i == 3) {
                i2 = OsConstants.SOCK_SEQPACKET;
            } else {
                throw new IllegalStateException("unknown sockType");
            }
            try {
                this.fd = Libcore.os.socket(OsConstants.AF_UNIX, i2, 0);
                this.mFdCreatedInternally = true;
            } catch (ErrnoException e) {
                e.rethrowAsIOException();
            }
        }
    }

    public void close() throws IOException {
        synchronized (this) {
            if (this.fd == null || !this.mFdCreatedInternally) {
                this.fd = null;
                return;
            }
            try {
                Libcore.os.close(this.fd);
            } catch (ErrnoException e) {
                e.rethrowAsIOException();
            }
            this.fd = null;
        }
    }

    protected void connect(LocalSocketAddress localSocketAddress, int i) throws IOException {
        FileDescriptor fileDescriptor = this.fd;
        if (fileDescriptor == null) {
            throw new IOException("socket not created");
        }
        connectLocal(fileDescriptor, localSocketAddress.getName(), localSocketAddress.getNamespace().getId());
    }

    public void bind(LocalSocketAddress localSocketAddress) throws IOException {
        FileDescriptor fileDescriptor = this.fd;
        if (fileDescriptor == null) {
            throw new IOException("socket not created");
        }
        bindLocal(fileDescriptor, localSocketAddress.getName(), localSocketAddress.getNamespace().getId());
    }

    protected void listen(int i) throws IOException {
        FileDescriptor fileDescriptor = this.fd;
        if (fileDescriptor == null) {
            throw new IOException("socket not created");
        }
        listen_native(fileDescriptor, i);
    }

    protected void accept(LocalSocketImpl localSocketImpl) throws IOException {
        FileDescriptor fileDescriptor = this.fd;
        if (fileDescriptor == null) {
            throw new IOException("socket not created");
        }
        localSocketImpl.fd = accept(fileDescriptor, localSocketImpl);
    }

    protected InputStream getInputStream() throws IOException {
        SocketInputStream socketInputStream;
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        synchronized (this) {
            if (this.fis == null) {
                this.fis = new SocketInputStream();
            }
            socketInputStream = this.fis;
        }
        return socketInputStream;
    }

    protected OutputStream getOutputStream() throws IOException {
        SocketOutputStream socketOutputStream;
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        synchronized (this) {
            if (this.fos == null) {
                this.fos = new SocketOutputStream();
            }
            socketOutputStream = this.fos;
        }
        return socketOutputStream;
    }

    protected int available() throws IOException {
        return getInputStream().available();
    }

    protected void shutdownInput() throws IOException {
        FileDescriptor fileDescriptor = this.fd;
        if (fileDescriptor == null) {
            throw new IOException("socket not created");
        }
        shutdown(fileDescriptor, true);
    }

    protected void shutdownOutput() throws IOException {
        FileDescriptor fileDescriptor = this.fd;
        if (fileDescriptor == null) {
            throw new IOException("socket not created");
        }
        shutdown(fileDescriptor, false);
    }

    protected FileDescriptor getFileDescriptor() {
        return this.fd;
    }

    protected void sendUrgentData(int i) throws IOException {
        throw new RuntimeException("not impled");
    }

    public Object getOption(int i) throws IOException {
        FileDescriptor fileDescriptor = this.fd;
        if (fileDescriptor == null) {
            throw new IOException("socket not created");
        }
        if (i == 4102) {
            return 0;
        }
        int option_native = getOption_native(fileDescriptor, i);
        if (i == 4097 || i == 4098) {
            return Integer.valueOf(option_native);
        }
        return Integer.valueOf(option_native);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v6, types: [int] */
    /* JADX WARN: Type inference failed for: r0v7 */
    /* JADX WARN: Type inference failed for: r0v8 */
    /* JADX WARN: Type inference failed for: r2v0, types: [android.net.LocalSocketImpl] */
    public void setOption(int i, Object obj) throws IOException {
        int iIntValue;
        ?? r0;
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        if (obj instanceof Integer) {
            iIntValue = ((Integer) obj).intValue();
            r0 = -1;
        } else if (obj instanceof Boolean) {
            boolean zBooleanValue = ((Boolean) obj).booleanValue();
            iIntValue = 0;
            r0 = zBooleanValue;
        } else {
            throw new IOException("bad value: " + obj);
        }
        setOption_native(this.fd, i, r0, iIntValue);
    }

    public void setFileDescriptorsForSend(FileDescriptor[] fileDescriptorArr) {
        synchronized (this.writeMonitor) {
            this.outboundFileDescriptors = fileDescriptorArr;
        }
    }

    public FileDescriptor[] getAncillaryFileDescriptors() throws IOException {
        FileDescriptor[] fileDescriptorArr;
        synchronized (this.readMonitor) {
            fileDescriptorArr = this.inboundFileDescriptors;
            this.inboundFileDescriptors = null;
        }
        return fileDescriptorArr;
    }

    public Credentials getPeerCredentials() throws IOException {
        return getPeerCredentials_native(this.fd);
    }

    protected void finalize() throws IOException {
        close();
    }
}
