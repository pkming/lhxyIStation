package de.innosystec.unrar.io;

import android.app.backup.FullBackup;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/* JADX INFO: loaded from: classes2.dex */
public class ReadOnlyAccessFile extends RandomAccessFile implements IReadOnlyAccess {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    public ReadOnlyAccessFile(File file) throws FileNotFoundException {
        super(file, FullBackup.ROOT_TREE_TOKEN);
    }

    @Override // de.innosystec.unrar.io.IReadOnlyAccess
    public int readFully(byte[] bArr, int i) throws IOException {
        readFully(bArr, 0, i);
        return i;
    }

    @Override // de.innosystec.unrar.io.IReadOnlyAccess
    public long getPosition() throws IOException {
        return getFilePointer();
    }

    @Override // de.innosystec.unrar.io.IReadOnlyAccess
    public void setPosition(long j) throws IOException {
        seek(j);
    }
}
