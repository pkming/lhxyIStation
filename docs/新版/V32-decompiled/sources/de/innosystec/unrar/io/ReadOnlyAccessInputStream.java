package de.innosystec.unrar.io;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes2.dex */
public class ReadOnlyAccessInputStream extends InputStream {
    private long curPos;
    private final long endPos;
    private IReadOnlyAccess file;
    private final long startPos;

    public ReadOnlyAccessInputStream(IReadOnlyAccess iReadOnlyAccess, long j, long j2) throws IOException {
        this.file = iReadOnlyAccess;
        this.startPos = j;
        this.curPos = j;
        this.endPos = j2;
        iReadOnlyAccess.setPosition(j);
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (this.curPos == this.endPos) {
            return -1;
        }
        int i = this.file.read();
        this.curPos++;
        return i;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (i2 == 0) {
            return 0;
        }
        long j = this.curPos;
        long j2 = this.endPos;
        if (j == j2) {
            return -1;
        }
        int i3 = this.file.read(bArr, i, (int) Math.min(i2, j2 - j));
        this.curPos += (long) i3;
        return i3;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }
}
