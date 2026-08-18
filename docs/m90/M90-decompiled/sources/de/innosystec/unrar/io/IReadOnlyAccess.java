package de.innosystec.unrar.io;

import java.io.IOException;

/* JADX INFO: loaded from: classes2.dex */
public interface IReadOnlyAccess {
    void close() throws IOException;

    long getPosition() throws IOException;

    int read() throws IOException;

    int read(byte[] bArr, int i, int i2) throws IOException;

    int readFully(byte[] bArr, int i) throws IOException;

    void setPosition(long j) throws IOException;
}
