package android.os;

import android.app.backup.FullBackup;
import android.os.Parcelable;
import android.util.Log;
import cn.yunzhisheng.asr.JniUscClient;
import dalvik.system.CloseGuard;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.nio.ByteOrder;
import java.util.Objects;
import libcore.io.ErrnoException;
import libcore.io.IoUtils;
import libcore.io.Libcore;
import libcore.io.Memory;
import libcore.io.OsConstants;
import libcore.io.StructStat;

/* JADX INFO: loaded from: classes.dex */
public class ParcelFileDescriptor implements Parcelable, Closeable {
    public static final Parcelable.Creator<ParcelFileDescriptor> CREATOR = new Parcelable.Creator<ParcelFileDescriptor>() { // from class: android.os.ParcelFileDescriptor.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ParcelFileDescriptor createFromParcel(Parcel parcel) {
            return new ParcelFileDescriptor(parcel.readRawFileDescriptor(), parcel.readInt() != 0 ? parcel.readRawFileDescriptor() : null);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ParcelFileDescriptor[] newArray(int i) {
            return new ParcelFileDescriptor[i];
        }
    };
    private static final int MAX_STATUS = 1024;
    public static final int MODE_APPEND = 33554432;
    public static final int MODE_CREATE = 134217728;
    public static final int MODE_READ_ONLY = 268435456;
    public static final int MODE_READ_WRITE = 805306368;
    public static final int MODE_TRUNCATE = 67108864;

    @Deprecated
    public static final int MODE_WORLD_READABLE = 1;

    @Deprecated
    public static final int MODE_WORLD_WRITEABLE = 2;
    public static final int MODE_WRITE_ONLY = 536870912;
    private static final String TAG = "ParcelFileDescriptor";
    private volatile boolean mClosed;
    private FileDescriptor mCommFd;
    private final FileDescriptor mFd;
    private final CloseGuard mGuard;
    private Status mStatus;
    private byte[] mStatusBuf;
    private final ParcelFileDescriptor mWrapped;

    public interface OnCloseListener {
        void onClose(IOException iOException);
    }

    public void releaseResources() {
    }

    public ParcelFileDescriptor(ParcelFileDescriptor parcelFileDescriptor) {
        this.mGuard = CloseGuard.get();
        this.mWrapped = parcelFileDescriptor;
        this.mFd = null;
        this.mCommFd = null;
        this.mClosed = true;
    }

    public ParcelFileDescriptor(FileDescriptor fileDescriptor) {
        this(fileDescriptor, null);
    }

    public ParcelFileDescriptor(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2) {
        CloseGuard closeGuard = CloseGuard.get();
        this.mGuard = closeGuard;
        Objects.requireNonNull(fileDescriptor, "FileDescriptor must not be null");
        this.mWrapped = null;
        this.mFd = fileDescriptor;
        this.mCommFd = fileDescriptor2;
        closeGuard.open(JniUscClient.r);
    }

    public static ParcelFileDescriptor open(File file, int i) throws FileNotFoundException {
        FileDescriptor fileDescriptorOpenInternal = openInternal(file, i);
        if (fileDescriptorOpenInternal == null) {
            return null;
        }
        return new ParcelFileDescriptor(fileDescriptorOpenInternal);
    }

    public static ParcelFileDescriptor open(File file, int i, Handler handler, OnCloseListener onCloseListener) throws IOException {
        if (handler == null) {
            throw new IllegalArgumentException("Handler must not be null");
        }
        if (onCloseListener == null) {
            throw new IllegalArgumentException("Listener must not be null");
        }
        FileDescriptor fileDescriptorOpenInternal = openInternal(file, i);
        if (fileDescriptorOpenInternal == null) {
            return null;
        }
        FileDescriptor[] fileDescriptorArrCreateCommSocketPair = createCommSocketPair();
        ParcelFileDescriptor parcelFileDescriptor = new ParcelFileDescriptor(fileDescriptorOpenInternal, fileDescriptorArrCreateCommSocketPair[0]);
        IoUtils.setBlocking(fileDescriptorArrCreateCommSocketPair[1], true);
        new ListenerBridge(fileDescriptorArrCreateCommSocketPair[1], handler.getLooper(), onCloseListener).start();
        return parcelFileDescriptor;
    }

    private static FileDescriptor openInternal(File file, int i) throws FileNotFoundException {
        if ((805306368 & i) == 0) {
            throw new IllegalArgumentException("Must specify MODE_READ_ONLY, MODE_WRITE_ONLY, or MODE_READ_WRITE");
        }
        return Parcel.openFileDescriptor(file.getPath(), i);
    }

    public static ParcelFileDescriptor dup(FileDescriptor fileDescriptor) throws IOException {
        try {
            return new ParcelFileDescriptor(Libcore.os.dup(fileDescriptor));
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public ParcelFileDescriptor dup() throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = this.mWrapped;
        if (parcelFileDescriptor != null) {
            return parcelFileDescriptor.dup();
        }
        return dup(getFileDescriptor());
    }

    public static ParcelFileDescriptor fromFd(int i) throws IOException {
        FileDescriptor fileDescriptor = new FileDescriptor();
        fileDescriptor.setInt$(i);
        try {
            return new ParcelFileDescriptor(Libcore.os.dup(fileDescriptor));
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public static ParcelFileDescriptor adoptFd(int i) {
        FileDescriptor fileDescriptor = new FileDescriptor();
        fileDescriptor.setInt$(i);
        return new ParcelFileDescriptor(fileDescriptor);
    }

    public static ParcelFileDescriptor fromSocket(Socket socket) {
        FileDescriptor fileDescriptor$ = socket.getFileDescriptor$();
        if (fileDescriptor$ != null) {
            return new ParcelFileDescriptor(fileDescriptor$);
        }
        return null;
    }

    public static ParcelFileDescriptor fromDatagramSocket(DatagramSocket datagramSocket) {
        FileDescriptor fileDescriptor$ = datagramSocket.getFileDescriptor$();
        if (fileDescriptor$ != null) {
            return new ParcelFileDescriptor(fileDescriptor$);
        }
        return null;
    }

    public static ParcelFileDescriptor[] createPipe() throws IOException {
        try {
            FileDescriptor[] fileDescriptorArrPipe = Libcore.os.pipe();
            return new ParcelFileDescriptor[]{new ParcelFileDescriptor(fileDescriptorArrPipe[0]), new ParcelFileDescriptor(fileDescriptorArrPipe[1])};
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public static ParcelFileDescriptor[] createReliablePipe() throws IOException {
        try {
            FileDescriptor[] fileDescriptorArrCreateCommSocketPair = createCommSocketPair();
            FileDescriptor[] fileDescriptorArrPipe = Libcore.os.pipe();
            return new ParcelFileDescriptor[]{new ParcelFileDescriptor(fileDescriptorArrPipe[0], fileDescriptorArrCreateCommSocketPair[0]), new ParcelFileDescriptor(fileDescriptorArrPipe[1], fileDescriptorArrCreateCommSocketPair[1])};
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public static ParcelFileDescriptor[] createSocketPair() throws IOException {
        try {
            FileDescriptor fileDescriptor = new FileDescriptor();
            FileDescriptor fileDescriptor2 = new FileDescriptor();
            Libcore.os.socketpair(OsConstants.AF_UNIX, OsConstants.SOCK_STREAM, 0, fileDescriptor, fileDescriptor2);
            return new ParcelFileDescriptor[]{new ParcelFileDescriptor(fileDescriptor), new ParcelFileDescriptor(fileDescriptor2)};
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public static ParcelFileDescriptor[] createReliableSocketPair() throws IOException {
        try {
            FileDescriptor[] fileDescriptorArrCreateCommSocketPair = createCommSocketPair();
            FileDescriptor fileDescriptor = new FileDescriptor();
            FileDescriptor fileDescriptor2 = new FileDescriptor();
            Libcore.os.socketpair(OsConstants.AF_UNIX, OsConstants.SOCK_STREAM, 0, fileDescriptor, fileDescriptor2);
            return new ParcelFileDescriptor[]{new ParcelFileDescriptor(fileDescriptor, fileDescriptorArrCreateCommSocketPair[0]), new ParcelFileDescriptor(fileDescriptor2, fileDescriptorArrCreateCommSocketPair[1])};
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    private static FileDescriptor[] createCommSocketPair() throws IOException {
        try {
            FileDescriptor fileDescriptor = new FileDescriptor();
            FileDescriptor fileDescriptor2 = new FileDescriptor();
            Libcore.os.socketpair(OsConstants.AF_UNIX, OsConstants.SOCK_STREAM, 0, fileDescriptor, fileDescriptor2);
            IoUtils.setBlocking(fileDescriptor, false);
            IoUtils.setBlocking(fileDescriptor2, false);
            return new FileDescriptor[]{fileDescriptor, fileDescriptor2};
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    @Deprecated
    public static ParcelFileDescriptor fromData(byte[] bArr, String str) throws IOException {
        if (bArr == null) {
            return null;
        }
        MemoryFile memoryFile = new MemoryFile(str, bArr.length);
        if (bArr.length > 0) {
            memoryFile.writeBytes(bArr, 0, 0, bArr.length);
        }
        memoryFile.deactivate();
        FileDescriptor fileDescriptor = memoryFile.getFileDescriptor();
        if (fileDescriptor != null) {
            return new ParcelFileDescriptor(fileDescriptor);
        }
        return null;
    }

    public static int parseMode(String str) {
        if (FullBackup.ROOT_TREE_TOKEN.equals(str)) {
            return 268435456;
        }
        if ("w".equals(str) || "wt".equals(str)) {
            return 738197504;
        }
        if ("wa".equals(str)) {
            return 704643072;
        }
        if ("rw".equals(str)) {
            return 939524096;
        }
        if ("rwt".equals(str)) {
            return 1006632960;
        }
        throw new IllegalArgumentException("Bad mode '" + str + "'");
    }

    public FileDescriptor getFileDescriptor() {
        ParcelFileDescriptor parcelFileDescriptor = this.mWrapped;
        if (parcelFileDescriptor != null) {
            return parcelFileDescriptor.getFileDescriptor();
        }
        return this.mFd;
    }

    public long getStatSize() {
        ParcelFileDescriptor parcelFileDescriptor = this.mWrapped;
        if (parcelFileDescriptor != null) {
            return parcelFileDescriptor.getStatSize();
        }
        try {
            StructStat structStatFstat = Libcore.os.fstat(this.mFd);
            if (!OsConstants.S_ISREG(structStatFstat.st_mode) && !OsConstants.S_ISLNK(structStatFstat.st_mode)) {
                return -1L;
            }
            return structStatFstat.st_size;
        } catch (ErrnoException e) {
            Log.w(TAG, "fstat() failed: " + e);
            return -1L;
        }
    }

    public long seekTo(long j) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = this.mWrapped;
        if (parcelFileDescriptor != null) {
            return parcelFileDescriptor.seekTo(j);
        }
        try {
            return Libcore.os.lseek(this.mFd, j, OsConstants.SEEK_SET);
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public int getFd() {
        ParcelFileDescriptor parcelFileDescriptor = this.mWrapped;
        if (parcelFileDescriptor != null) {
            return parcelFileDescriptor.getFd();
        }
        if (this.mClosed) {
            throw new IllegalStateException("Already closed");
        }
        return this.mFd.getInt$();
    }

    public int detachFd() {
        ParcelFileDescriptor parcelFileDescriptor = this.mWrapped;
        if (parcelFileDescriptor != null) {
            return parcelFileDescriptor.detachFd();
        }
        if (this.mClosed) {
            throw new IllegalStateException("Already closed");
        }
        int fd = getFd();
        Parcel.clearFileDescriptor(this.mFd);
        writeCommStatusAndClose(2, null);
        return fd;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = this.mWrapped;
        if (parcelFileDescriptor != null) {
            try {
                parcelFileDescriptor.close();
                return;
            } finally {
                releaseResources();
            }
        }
        closeWithStatus(0, null);
    }

    public void closeWithError(String str) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = this.mWrapped;
        if (parcelFileDescriptor != null) {
            try {
                parcelFileDescriptor.closeWithError(str);
            } finally {
                releaseResources();
            }
        } else {
            if (str == null) {
                throw new IllegalArgumentException("Message must not be null");
            }
            closeWithStatus(1, str);
        }
    }

    private void closeWithStatus(int i, String str) {
        if (this.mClosed) {
            return;
        }
        this.mClosed = true;
        this.mGuard.close();
        writeCommStatusAndClose(i, str);
        IoUtils.closeQuietly(this.mFd);
        releaseResources();
    }

    private byte[] getOrCreateStatusBuffer() {
        if (this.mStatusBuf == null) {
            this.mStatusBuf = new byte[1024];
        }
        return this.mStatusBuf;
    }

    private void writeCommStatusAndClose(int i, String str) {
        if (this.mCommFd == null) {
            if (str != null) {
                Log.w(TAG, "Unable to inform peer: " + str);
                return;
            }
            return;
        }
        if (i == 2) {
            Log.w(TAG, "Peer expected signal when closed; unable to deliver after detach");
        }
        if (i != -1) {
            try {
                Status commStatus = readCommStatus(this.mCommFd, getOrCreateStatusBuffer());
                this.mStatus = commStatus;
                if (commStatus == null) {
                    try {
                        byte[] orCreateStatusBuffer = getOrCreateStatusBuffer();
                        Memory.pokeInt(orCreateStatusBuffer, 0, i, ByteOrder.BIG_ENDIAN);
                        int i2 = 4;
                        if (str != null) {
                            byte[] bytes = str.getBytes();
                            int iMin = Math.min(bytes.length, orCreateStatusBuffer.length - 4);
                            System.arraycopy(bytes, 0, orCreateStatusBuffer, 4, iMin);
                            i2 = 4 + iMin;
                        }
                        Libcore.os.write(this.mCommFd, orCreateStatusBuffer, 0, i2);
                    } catch (ErrnoException e) {
                        Log.w(TAG, "Failed to report status: " + e);
                    }
                }
            } finally {
                IoUtils.closeQuietly(this.mCommFd);
                this.mCommFd = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Status readCommStatus(FileDescriptor fileDescriptor, byte[] bArr) {
        try {
            int i = Libcore.os.read(fileDescriptor, bArr, 0, bArr.length);
            if (i == 0) {
                return new Status(-2);
            }
            int iPeekInt = Memory.peekInt(bArr, 0, ByteOrder.BIG_ENDIAN);
            if (iPeekInt == 1) {
                return new Status(iPeekInt, new String(bArr, 4, i - 4));
            }
            return new Status(iPeekInt);
        } catch (ErrnoException e) {
            if (e.errno == OsConstants.EAGAIN) {
                return null;
            }
            Log.d(TAG, "Failed to read status; assuming dead: " + e);
            return new Status(-2);
        }
    }

    public boolean canDetectErrors() {
        ParcelFileDescriptor parcelFileDescriptor = this.mWrapped;
        if (parcelFileDescriptor != null) {
            return parcelFileDescriptor.canDetectErrors();
        }
        return this.mCommFd != null;
    }

    public void checkError() throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = this.mWrapped;
        if (parcelFileDescriptor != null) {
            parcelFileDescriptor.checkError();
            return;
        }
        if (this.mStatus == null) {
            FileDescriptor fileDescriptor = this.mCommFd;
            if (fileDescriptor == null) {
                Log.w(TAG, "Peer didn't provide a comm channel; unable to check for errors");
                return;
            }
            this.mStatus = readCommStatus(fileDescriptor, getOrCreateStatusBuffer());
        }
        Status status = this.mStatus;
        if (status != null && status.status != 0) {
            throw this.mStatus.asIOException();
        }
    }

    public static class AutoCloseInputStream extends FileInputStream {
        private final ParcelFileDescriptor mPfd;

        public AutoCloseInputStream(ParcelFileDescriptor parcelFileDescriptor) {
            super(parcelFileDescriptor.getFileDescriptor());
            this.mPfd = parcelFileDescriptor;
        }

        @Override // java.io.FileInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            try {
                this.mPfd.close();
            } finally {
                super.close();
            }
        }
    }

    public static class AutoCloseOutputStream extends FileOutputStream {
        private final ParcelFileDescriptor mPfd;

        public AutoCloseOutputStream(ParcelFileDescriptor parcelFileDescriptor) {
            super(parcelFileDescriptor.getFileDescriptor());
            this.mPfd = parcelFileDescriptor;
        }

        @Override // java.io.FileOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            try {
                this.mPfd.close();
            } finally {
                super.close();
            }
        }
    }

    public String toString() {
        ParcelFileDescriptor parcelFileDescriptor = this.mWrapped;
        if (parcelFileDescriptor != null) {
            return parcelFileDescriptor.toString();
        }
        return "{ParcelFileDescriptor: " + this.mFd + "}";
    }

    protected void finalize() throws Throwable {
        if (this.mWrapped != null) {
            releaseResources();
        }
        CloseGuard closeGuard = this.mGuard;
        if (closeGuard != null) {
            closeGuard.warnIfOpen();
        }
        try {
            if (!this.mClosed) {
                closeWithStatus(3, null);
            }
        } finally {
            super.finalize();
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        ParcelFileDescriptor parcelFileDescriptor = this.mWrapped;
        if (parcelFileDescriptor != null) {
            return parcelFileDescriptor.describeContents();
        }
        return 1;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        ParcelFileDescriptor parcelFileDescriptor = this.mWrapped;
        if (parcelFileDescriptor != null) {
            try {
                parcelFileDescriptor.writeToParcel(parcel, i);
                return;
            } finally {
                releaseResources();
            }
        }
        parcel.writeFileDescriptor(this.mFd);
        if (this.mCommFd != null) {
            parcel.writeInt(1);
            parcel.writeFileDescriptor(this.mCommFd);
        } else {
            parcel.writeInt(0);
        }
        if ((i & 1) == 0 || this.mClosed) {
            return;
        }
        closeWithStatus(-1, null);
    }

    public static class FileDescriptorDetachedException extends IOException {
        private static final long serialVersionUID = 955542466045L;

        public FileDescriptorDetachedException() {
            super("Remote side is detached");
        }
    }

    private static class Status {
        public static final int DEAD = -2;
        public static final int DETACHED = 2;
        public static final int ERROR = 1;
        public static final int LEAKED = 3;
        public static final int OK = 0;
        public static final int SILENCE = -1;
        public final String msg;
        public final int status;

        public Status(int i) {
            this(i, null);
        }

        public Status(int i, String str) {
            this.status = i;
            this.msg = str;
        }

        public IOException asIOException() {
            int i = this.status;
            if (i == -2) {
                return new IOException("Remote side is dead");
            }
            if (i == 0) {
                return null;
            }
            if (i == 1) {
                return new IOException("Remote error: " + this.msg);
            }
            if (i == 2) {
                return new FileDescriptorDetachedException();
            }
            if (i == 3) {
                return new IOException("Remote side was leaked");
            }
            return new IOException("Unknown status: " + this.status);
        }
    }

    private static final class ListenerBridge extends Thread {
        private FileDescriptor mCommFd;
        private final Handler mHandler;

        public ListenerBridge(FileDescriptor fileDescriptor, Looper looper, final OnCloseListener onCloseListener) {
            this.mCommFd = fileDescriptor;
            this.mHandler = new Handler(looper) { // from class: android.os.ParcelFileDescriptor.ListenerBridge.1
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    Status status = (Status) message.obj;
                    onCloseListener.onClose(status != null ? status.asIOException() : null);
                }
            };
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                this.mHandler.obtainMessage(0, ParcelFileDescriptor.readCommStatus(this.mCommFd, new byte[1024])).sendToTarget();
            } finally {
                IoUtils.closeQuietly(this.mCommFd);
                this.mCommFd = null;
            }
        }
    }
}
