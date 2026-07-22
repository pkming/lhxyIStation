package org.apache.tools.tar;

import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class TarArchiveSparseEntry implements TarConstants {
    private boolean isExtended;

    public TarArchiveSparseEntry(byte[] bArr) throws IOException {
        this.isExtended = TarUtils.parseBoolean(bArr, 504);
    }

    public boolean isExtended() {
        return this.isExtended;
    }
}
