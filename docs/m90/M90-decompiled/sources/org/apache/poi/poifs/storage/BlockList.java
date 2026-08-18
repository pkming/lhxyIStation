package org.apache.poi.poifs.storage;

import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public interface BlockList {
    ListManagedBlock[] fetchBlocks(int i) throws IOException;

    ListManagedBlock remove(int i) throws IOException;

    void setBAT(BlockAllocationTableReader blockAllocationTableReader) throws IOException;

    void zap(int i);
}
