package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.poifs.filesystem.BATManaged;
import org.apache.poi.util.IntList;

/* JADX INFO: loaded from: classes3.dex */
public class BlockAllocationTableWriter implements BlockWritable, BATManaged {
    private int _start_block = -2;
    private IntList _entries = new IntList();
    private BATBlock[] _blocks = new BATBlock[0];

    public int createBlocks() {
        int i = 0;
        int i2 = 0;
        while (true) {
            int iCalculateStorageRequirements = BATBlock.calculateStorageRequirements(i + i2 + this._entries.size());
            int iCalculateXBATStorageRequirements = HeaderBlockWriter.calculateXBATStorageRequirements(iCalculateStorageRequirements);
            if (i == iCalculateStorageRequirements && i2 == iCalculateXBATStorageRequirements) {
                int iAllocateSpace = allocateSpace(i);
                allocateSpace(i2);
                simpleCreateBlocks();
                return iAllocateSpace;
            }
            i = iCalculateStorageRequirements;
            i2 = iCalculateXBATStorageRequirements;
        }
    }

    public int allocateSpace(int i) {
        int size = this._entries.size();
        if (i > 0) {
            int i2 = i - 1;
            int i3 = size + 1;
            int i4 = 0;
            while (i4 < i2) {
                this._entries.add(i3);
                i4++;
                i3++;
            }
            this._entries.add(-2);
        }
        return size;
    }

    public int getStartBlock() {
        return this._start_block;
    }

    void simpleCreateBlocks() {
        this._blocks = BATBlock.createBATBlocks(this._entries.toArray());
    }

    @Override // org.apache.poi.poifs.storage.BlockWritable
    public void writeBlocks(OutputStream outputStream) throws IOException {
        int i = 0;
        while (true) {
            BATBlock[] bATBlockArr = this._blocks;
            if (i >= bATBlockArr.length) {
                return;
            }
            bATBlockArr[i].writeBlocks(outputStream);
            i++;
        }
    }

    @Override // org.apache.poi.poifs.filesystem.BATManaged
    public int countBlocks() {
        return this._blocks.length;
    }

    @Override // org.apache.poi.poifs.filesystem.BATManaged
    public void setStartBlock(int i) {
        this._start_block = i;
    }
}
