package android.app.backup;

import android.os.ParcelFileDescriptor;

/* JADX INFO: loaded from: classes.dex */
public class FullBackupDataOutput {
    private BackupDataOutput mData;

    public FullBackupDataOutput(ParcelFileDescriptor parcelFileDescriptor) {
        this.mData = new BackupDataOutput(parcelFileDescriptor.getFileDescriptor());
    }

    public BackupDataOutput getData() {
        return this.mData;
    }
}
