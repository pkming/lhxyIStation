package org.apache.poi.poifs.storage;

import java.io.IOException;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: loaded from: classes3.dex */
public class BlockListImpl implements BlockList {
    private ListManagedBlock[] _blocks = new ListManagedBlock[0];
    private BlockAllocationTableReader _bat = null;

    protected BlockListImpl() {
    }

    protected void setBlocks(ListManagedBlock[] listManagedBlockArr) {
        this._blocks = listManagedBlockArr;
    }

    @Override // org.apache.poi.poifs.storage.BlockList
    public void zap(int i) {
        if (i >= 0) {
            ListManagedBlock[] listManagedBlockArr = this._blocks;
            if (i < listManagedBlockArr.length) {
                listManagedBlockArr[i] = null;
            }
        }
    }

    @Override // org.apache.poi.poifs.storage.BlockList
    public ListManagedBlock remove(int i) throws IOException {
        try {
            ListManagedBlock[] listManagedBlockArr = this._blocks;
            ListManagedBlock listManagedBlock = listManagedBlockArr[i];
            if (listManagedBlock == null) {
                throw new IOException(new StringBuffer().append("block[ ").append(i).append(" ] already removed").toString());
            }
            listManagedBlockArr[i] = null;
            return listManagedBlock;
        } catch (ArrayIndexOutOfBoundsException unused) {
            throw new IOException(new StringBuffer().append("Cannot remove block[ ").append(i).append(" ]; out of range").toString());
        }
    }

    @Override // org.apache.poi.poifs.storage.BlockList
    public ListManagedBlock[] fetchBlocks(int i) throws IOException {
        BlockAllocationTableReader blockAllocationTableReader = this._bat;
        if (blockAllocationTableReader == null) {
            throw new IOException("Improperly initialized list: no block allocation table provided");
        }
        return blockAllocationTableReader.fetchBlocks(i, this);
    }

    @Override // org.apache.poi.poifs.storage.BlockList
    public void setBAT(BlockAllocationTableReader blockAllocationTableReader) throws IOException {
        if (this._bat != null) {
            throw new IOException("Attempt to replace existing BlockAllocationTable");
        }
        this._bat = blockAllocationTableReader;
    }
}
