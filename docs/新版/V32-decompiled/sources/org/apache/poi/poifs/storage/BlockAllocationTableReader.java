package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.util.IntList;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class BlockAllocationTableReader {
    private IntList _entries;

    public BlockAllocationTableReader(int i, int[] iArr, int i2, int i3, BlockList blockList) throws IOException {
        this();
        if (i <= 0) {
            throw new IOException(new StringBuffer().append("Illegal block count; minimum count is 1, got ").append(i).append(" instead").toString());
        }
        RawDataBlock[] rawDataBlockArr = new RawDataBlock[i];
        int iMin = Math.min(i, iArr.length);
        int i4 = 0;
        while (i4 < iMin) {
            rawDataBlockArr[i4] = (RawDataBlock) blockList.remove(iArr[i4]);
            i4++;
        }
        if (i4 < i) {
            if (i3 < 0) {
                throw new IOException("BAT count exceeds limit, yet XBAT index indicates no valid entries");
            }
            int iEntriesPerXBATBlock = BATBlock.entriesPerXBATBlock();
            int xBATChainOffset = BATBlock.getXBATChainOffset();
            for (int i5 = 0; i5 < i2; i5++) {
                int iMin2 = Math.min(i - i4, iEntriesPerXBATBlock);
                byte[] data = blockList.remove(i3).getData();
                int i6 = 0;
                int i7 = 0;
                while (i6 < iMin2) {
                    rawDataBlockArr[i4] = (RawDataBlock) blockList.remove(LittleEndian.getInt(data, i7));
                    i7 += 4;
                    i6++;
                    i4++;
                }
                i3 = LittleEndian.getInt(data, xBATChainOffset);
                if (i3 == -2) {
                    break;
                }
            }
        }
        if (i4 != i) {
            throw new IOException("Could not find all blocks");
        }
        setEntries(rawDataBlockArr, blockList);
    }

    BlockAllocationTableReader(ListManagedBlock[] listManagedBlockArr, BlockList blockList) throws IOException {
        this();
        setEntries(listManagedBlockArr, blockList);
    }

    BlockAllocationTableReader() {
        this._entries = new IntList();
    }

    ListManagedBlock[] fetchBlocks(int i, BlockList blockList) throws IOException {
        ArrayList arrayList = new ArrayList();
        while (i != -2) {
            arrayList.add(blockList.remove(i));
            i = this._entries.get(i);
        }
        return (ListManagedBlock[]) arrayList.toArray(new ListManagedBlock[0]);
    }

    boolean isUsed(int i) {
        try {
            return this._entries.get(i) != -1;
        } catch (IndexOutOfBoundsException unused) {
            return false;
        }
    }

    int getNextBlockIndex(int i) throws IOException {
        if (isUsed(i)) {
            return this._entries.get(i);
        }
        throw new IOException(new StringBuffer().append("index ").append(i).append(" is unused").toString());
    }

    private void setEntries(ListManagedBlock[] listManagedBlockArr, BlockList blockList) throws IOException {
        int iEntriesPerBlock = BATBlock.entriesPerBlock();
        for (int i = 0; i < listManagedBlockArr.length; i++) {
            byte[] data = listManagedBlockArr[i].getData();
            int i2 = 0;
            for (int i3 = 0; i3 < iEntriesPerBlock; i3++) {
                int i4 = LittleEndian.getInt(data, i2);
                if (i4 == -1) {
                    blockList.zap(this._entries.size());
                }
                this._entries.add(i4);
                i2 += 4;
            }
            listManagedBlockArr[i] = null;
        }
        blockList.setBAT(this);
    }
}
