package android.mtp;

import android.content.Context;
import android.os.storage.StorageVolume;

/* JADX INFO: loaded from: classes.dex */
public class MtpStorage {
    private final String mDescription;
    private final long mMaxFileSize;
    private final String mPath;
    private final boolean mRemovable;
    private final long mReserveSpace;
    private final int mStorageId;

    public static int getStorageId(int i) {
        return ((i + 1) << 16) + 1;
    }

    public MtpStorage(StorageVolume storageVolume, Context context) {
        this.mStorageId = storageVolume.getStorageId();
        this.mPath = storageVolume.getPath();
        this.mDescription = context.getResources().getString(storageVolume.getDescriptionId());
        this.mReserveSpace = ((long) storageVolume.getMtpReserveSpace()) * 1024 * 1024;
        this.mRemovable = storageVolume.isRemovable();
        this.mMaxFileSize = storageVolume.getMaxFileSize();
    }

    public final int getStorageId() {
        return this.mStorageId;
    }

    public final String getPath() {
        return this.mPath;
    }

    public final String getDescription() {
        return this.mDescription;
    }

    public final long getReserveSpace() {
        return this.mReserveSpace;
    }

    public final boolean isRemovable() {
        return this.mRemovable;
    }

    public long getMaxFileSize() {
        return this.mMaxFileSize;
    }
}
