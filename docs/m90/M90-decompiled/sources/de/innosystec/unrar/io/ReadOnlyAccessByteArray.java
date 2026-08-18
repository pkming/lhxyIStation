package de.innosystec.unrar.io;

import java.io.EOFException;
import java.io.IOException;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
public class ReadOnlyAccessByteArray implements IReadOnlyAccess {
    private byte[] file;
    private int positionInFile;

    @Override // de.innosystec.unrar.io.IReadOnlyAccess
    public void close() throws IOException {
    }

    public ReadOnlyAccessByteArray(byte[] bArr) {
        Objects.requireNonNull(bArr, "file must not be null!!");
        this.file = bArr;
        this.positionInFile = 0;
    }

    @Override // de.innosystec.unrar.io.IReadOnlyAccess
    public long getPosition() throws IOException {
        return this.positionInFile;
    }

    @Override // de.innosystec.unrar.io.IReadOnlyAccess
    public void setPosition(long j) throws IOException {
        if (j < this.file.length && j >= 0) {
            this.positionInFile = (int) j;
            return;
        }
        throw new EOFException();
    }

    @Override // de.innosystec.unrar.io.IReadOnlyAccess
    public int read() throws IOException {
        byte[] bArr = this.file;
        int i = this.positionInFile;
        this.positionInFile = i + 1;
        return bArr[i];
    }

    @Override // de.innosystec.unrar.io.IReadOnlyAccess
    public int read(byte[] bArr, int i, int i2) throws IOException {
        int iMin = Math.min(i2, this.file.length - this.positionInFile);
        System.arraycopy(this.file, this.positionInFile, bArr, i, iMin);
        this.positionInFile += iMin;
        return iMin;
    }

    @Override // de.innosystec.unrar.io.IReadOnlyAccess
    public int readFully(byte[] bArr, int i) throws IOException {
        Objects.requireNonNull(bArr, "buffer must not be null");
        if (i == 0) {
            throw new IllegalArgumentException("cannot read 0 bytes ;-)");
        }
        int iMin = Math.min(i, (this.file.length - this.positionInFile) - 1);
        System.arraycopy(this.file, this.positionInFile, bArr, 0, iMin);
        this.positionInFile += iMin;
        return iMin;
    }
}
