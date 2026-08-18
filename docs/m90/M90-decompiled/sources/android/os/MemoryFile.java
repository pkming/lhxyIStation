package android.os;

import android.util.Log;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes.dex */
public class MemoryFile {
    private static final int PROT_READ = 1;
    private static final int PROT_WRITE = 2;
    private static String TAG = "MemoryFile";
    private int mAddress;
    private boolean mAllowPurging = false;
    private FileDescriptor mFD;
    private int mLength;

    private static native void native_close(FileDescriptor fileDescriptor);

    private static native int native_get_size(FileDescriptor fileDescriptor) throws IOException;

    private static native int native_mmap(FileDescriptor fileDescriptor, int i, int i2) throws IOException;

    private static native void native_munmap(int i, int i2) throws IOException;

    private static native FileDescriptor native_open(String str, int i) throws IOException;

    private static native void native_pin(FileDescriptor fileDescriptor, boolean z) throws IOException;

    private static native int native_read(FileDescriptor fileDescriptor, int i, byte[] bArr, int i2, int i3, int i4, boolean z) throws IOException;

    private static native void native_write(FileDescriptor fileDescriptor, int i, byte[] bArr, int i2, int i3, int i4, boolean z) throws IOException;

    public MemoryFile(String str, int i) throws IOException {
        this.mLength = i;
        FileDescriptor fileDescriptorNative_open = native_open(str, i);
        this.mFD = fileDescriptorNative_open;
        if (i > 0) {
            this.mAddress = native_mmap(fileDescriptorNative_open, i, 3);
        } else {
            this.mAddress = 0;
        }
    }

    public void close() {
        deactivate();
        if (isClosed()) {
            return;
        }
        native_close(this.mFD);
    }

    void deactivate() {
        if (isDeactivated()) {
            return;
        }
        try {
            native_munmap(this.mAddress, this.mLength);
            this.mAddress = 0;
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    private boolean isDeactivated() {
        return this.mAddress == 0;
    }

    private boolean isClosed() {
        return !this.mFD.valid();
    }

    protected void finalize() {
        if (isClosed()) {
            return;
        }
        Log.e(TAG, "MemoryFile.finalize() called while ashmem still open");
        close();
    }

    public int length() {
        return this.mLength;
    }

    public boolean isPurgingAllowed() {
        return this.mAllowPurging;
    }

    public synchronized boolean allowPurging(boolean z) throws IOException {
        boolean z2;
        z2 = this.mAllowPurging;
        if (z2 != z) {
            native_pin(this.mFD, !z);
            this.mAllowPurging = z;
        }
        return z2;
    }

    public InputStream getInputStream() {
        return new MemoryInputStream();
    }

    public OutputStream getOutputStream() {
        return new MemoryOutputStream();
    }

    public int readBytes(byte[] bArr, int i, int i2, int i3) throws IOException {
        int i4;
        if (isDeactivated()) {
            throw new IOException("Can't read from deactivated memory file.");
        }
        if (i2 < 0 || i2 > bArr.length || i3 < 0 || i3 > bArr.length - i2 || i < 0 || i > (i4 = this.mLength) || i3 > i4 - i) {
            throw new IndexOutOfBoundsException();
        }
        return native_read(this.mFD, this.mAddress, bArr, i, i2, i3, this.mAllowPurging);
    }

    public void writeBytes(byte[] bArr, int i, int i2, int i3) throws IOException {
        int i4;
        if (isDeactivated()) {
            throw new IOException("Can't write to deactivated memory file.");
        }
        if (i < 0 || i > bArr.length || i3 < 0 || i3 > bArr.length - i || i2 < 0 || i2 > (i4 = this.mLength) || i3 > i4 - i2) {
            throw new IndexOutOfBoundsException();
        }
        native_write(this.mFD, this.mAddress, bArr, i, i2, i3, this.mAllowPurging);
    }

    public FileDescriptor getFileDescriptor() throws IOException {
        return this.mFD;
    }

    public static int getSize(FileDescriptor fileDescriptor) throws IOException {
        return native_get_size(fileDescriptor);
    }

    private class MemoryInputStream extends InputStream {
        private int mMark;
        private int mOffset;
        private byte[] mSingleByte;

        @Override // java.io.InputStream
        public boolean markSupported() {
            return true;
        }

        private MemoryInputStream() {
            this.mMark = 0;
            this.mOffset = 0;
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            if (this.mOffset >= MemoryFile.this.mLength) {
                return 0;
            }
            return MemoryFile.this.mLength - this.mOffset;
        }

        @Override // java.io.InputStream
        public void mark(int i) {
            this.mMark = this.mOffset;
        }

        @Override // java.io.InputStream
        public void reset() throws IOException {
            this.mOffset = this.mMark;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            if (this.mSingleByte == null) {
                this.mSingleByte = new byte[1];
            }
            if (read(this.mSingleByte, 0, 1) != 1) {
                return -1;
            }
            return this.mSingleByte[0];
        }

        @Override // java.io.InputStream
        public int read(byte[] bArr, int i, int i2) throws IOException {
            if (i < 0 || i2 < 0 || i + i2 > bArr.length) {
                throw new IndexOutOfBoundsException();
            }
            int iMin = Math.min(i2, available());
            if (iMin < 1) {
                return -1;
            }
            int bytes = MemoryFile.this.readBytes(bArr, this.mOffset, i, iMin);
            if (bytes > 0) {
                this.mOffset += bytes;
            }
            return bytes;
        }

        @Override // java.io.InputStream
        public long skip(long j) throws IOException {
            if (((long) this.mOffset) + j > MemoryFile.this.mLength) {
                j = MemoryFile.this.mLength - this.mOffset;
            }
            this.mOffset = (int) (((long) this.mOffset) + j);
            return j;
        }
    }

    private class MemoryOutputStream extends OutputStream {
        private int mOffset;
        private byte[] mSingleByte;

        private MemoryOutputStream() {
            this.mOffset = 0;
        }

        @Override // java.io.OutputStream
        public void write(byte[] bArr, int i, int i2) throws IOException {
            MemoryFile.this.writeBytes(bArr, i, this.mOffset, i2);
            this.mOffset += i2;
        }

        @Override // java.io.OutputStream
        public void write(int i) throws IOException {
            if (this.mSingleByte == null) {
                this.mSingleByte = new byte[1];
            }
            byte[] bArr = this.mSingleByte;
            bArr[0] = (byte) i;
            write(bArr, 0, 1);
        }
    }
}
