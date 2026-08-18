package android.app.backup;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class BackupDataOutput {
    int mBackupWriter;

    private static native int ctor(FileDescriptor fileDescriptor);

    private static native void dtor(int i);

    private static native void setKeyPrefix_native(int i, String str);

    private static native int writeEntityData_native(int i, byte[] bArr, int i2);

    private static native int writeEntityHeader_native(int i, String str, int i2);

    public BackupDataOutput(FileDescriptor fileDescriptor) {
        Objects.requireNonNull(fileDescriptor);
        int iCtor = ctor(fileDescriptor);
        this.mBackupWriter = iCtor;
        if (iCtor == 0) {
            throw new RuntimeException("Native initialization failed with fd=" + fileDescriptor);
        }
    }

    public int writeEntityHeader(String str, int i) throws IOException {
        int iWriteEntityHeader_native = writeEntityHeader_native(this.mBackupWriter, str, i);
        if (iWriteEntityHeader_native >= 0) {
            return iWriteEntityHeader_native;
        }
        throw new IOException("result=0x" + Integer.toHexString(iWriteEntityHeader_native));
    }

    public int writeEntityData(byte[] bArr, int i) throws IOException {
        int iWriteEntityData_native = writeEntityData_native(this.mBackupWriter, bArr, i);
        if (iWriteEntityData_native >= 0) {
            return iWriteEntityData_native;
        }
        throw new IOException("result=0x" + Integer.toHexString(iWriteEntityData_native));
    }

    public void setKeyPrefix(String str) {
        setKeyPrefix_native(this.mBackupWriter, str);
    }

    protected void finalize() throws Throwable {
        try {
            dtor(this.mBackupWriter);
        } finally {
            super.finalize();
        }
    }
}
