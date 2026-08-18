package android.app.backup;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class BackupDataInput {
    int mBackupReader;
    private EntityHeader mHeader = new EntityHeader();
    private boolean mHeaderReady;

    private static native int ctor(FileDescriptor fileDescriptor);

    private static native void dtor(int i);

    private native int readEntityData_native(int i, byte[] bArr, int i2, int i3);

    private native int readNextHeader_native(int i, EntityHeader entityHeader);

    private native int skipEntityData_native(int i);

    private static class EntityHeader {
        int dataSize;
        String key;

        private EntityHeader() {
        }
    }

    public BackupDataInput(FileDescriptor fileDescriptor) {
        Objects.requireNonNull(fileDescriptor);
        int iCtor = ctor(fileDescriptor);
        this.mBackupReader = iCtor;
        if (iCtor == 0) {
            throw new RuntimeException("Native initialization failed with fd=" + fileDescriptor);
        }
    }

    protected void finalize() throws Throwable {
        try {
            dtor(this.mBackupReader);
        } finally {
            super.finalize();
        }
    }

    public boolean readNextHeader() throws IOException {
        int nextHeader_native = readNextHeader_native(this.mBackupReader, this.mHeader);
        if (nextHeader_native == 0) {
            this.mHeaderReady = true;
            return true;
        }
        if (nextHeader_native > 0) {
            this.mHeaderReady = false;
            return false;
        }
        this.mHeaderReady = false;
        throw new IOException("failed: 0x" + Integer.toHexString(nextHeader_native));
    }

    public String getKey() {
        if (this.mHeaderReady) {
            return this.mHeader.key;
        }
        throw new IllegalStateException("Entity header not read");
    }

    public int getDataSize() {
        if (this.mHeaderReady) {
            return this.mHeader.dataSize;
        }
        throw new IllegalStateException("Entity header not read");
    }

    public int readEntityData(byte[] bArr, int i, int i2) throws IOException {
        if (this.mHeaderReady) {
            int entityData_native = readEntityData_native(this.mBackupReader, bArr, i, i2);
            if (entityData_native >= 0) {
                return entityData_native;
            }
            throw new IOException("result=0x" + Integer.toHexString(entityData_native));
        }
        throw new IllegalStateException("Entity header not read");
    }

    public void skipEntityData() throws IOException {
        if (this.mHeaderReady) {
            skipEntityData_native(this.mBackupReader);
            return;
        }
        throw new IllegalStateException("Entity header not read");
    }
}
