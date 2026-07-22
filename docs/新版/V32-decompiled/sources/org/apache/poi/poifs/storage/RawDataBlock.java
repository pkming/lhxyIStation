package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.util.IOUtils;

/* JADX INFO: loaded from: classes3.dex */
public class RawDataBlock implements ListManagedBlock {
    private byte[] _data;
    private boolean _eof;

    public RawDataBlock(InputStream inputStream) throws IOException {
        byte[] bArr = new byte[512];
        this._data = bArr;
        int fully = IOUtils.readFully(inputStream, bArr);
        if (fully == -1) {
            this._eof = true;
        } else {
            if (fully != 512) {
                throw new IOException(new StringBuffer().append("Unable to read entire block; ").append(fully).append(new StringBuffer().append(" byte").append(fully == 1 ? "" : "s").toString()).append(" read; expected ").append(512).append(" bytes").toString());
            }
            this._eof = false;
        }
    }

    public boolean eof() throws IOException {
        return this._eof;
    }

    @Override // org.apache.poi.poifs.storage.ListManagedBlock
    public byte[] getData() throws IOException {
        if (eof()) {
            throw new IOException("Cannot return empty data");
        }
        return this._data;
    }
}
