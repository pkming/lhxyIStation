package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: loaded from: classes3.dex */
public abstract class BigBlock implements BlockWritable {
    abstract void writeData(OutputStream outputStream) throws IOException;

    BigBlock() {
    }

    protected void doWriteData(OutputStream outputStream, byte[] bArr) throws IOException {
        outputStream.write(bArr);
    }

    @Override // org.apache.poi.poifs.storage.BlockWritable
    public void writeBlocks(OutputStream outputStream) throws IOException {
        writeData(outputStream);
    }
}
