package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.poifs.filesystem.BATManaged;
import org.apache.poi.poifs.filesystem.POIFSDocument;
import org.apache.poi.poifs.property.RootProperty;

/* JADX INFO: loaded from: classes3.dex */
public class SmallBlockTableWriter implements BlockWritable, BATManaged {
    private int _big_block_count;
    private RootProperty _root;
    private BlockAllocationTableWriter _sbat = new BlockAllocationTableWriter();
    private List _small_blocks = new ArrayList();

    public SmallBlockTableWriter(List list, RootProperty rootProperty) {
        this._root = rootProperty;
        Iterator it = list.iterator();
        while (it.hasNext()) {
            POIFSDocument pOIFSDocument = (POIFSDocument) it.next();
            BlockWritable[] smallBlocks = pOIFSDocument.getSmallBlocks();
            if (smallBlocks.length != 0) {
                pOIFSDocument.setStartBlock(this._sbat.allocateSpace(smallBlocks.length));
                for (BlockWritable blockWritable : smallBlocks) {
                    this._small_blocks.add(blockWritable);
                }
            }
        }
        this._sbat.simpleCreateBlocks();
        this._root.setSize(this._small_blocks.size());
        this._big_block_count = SmallDocumentBlock.fill(this._small_blocks);
    }

    public int getSBATBlockCount() {
        return (this._big_block_count + 15) / 16;
    }

    public BlockAllocationTableWriter getSBAT() {
        return this._sbat;
    }

    @Override // org.apache.poi.poifs.filesystem.BATManaged
    public int countBlocks() {
        return this._big_block_count;
    }

    @Override // org.apache.poi.poifs.filesystem.BATManaged
    public void setStartBlock(int i) {
        this._root.setStartBlock(i);
    }

    @Override // org.apache.poi.poifs.storage.BlockWritable
    public void writeBlocks(OutputStream outputStream) throws IOException {
        Iterator it = this._small_blocks.iterator();
        while (it.hasNext()) {
            ((BlockWritable) it.next()).writeBlocks(outputStream);
        }
    }
}
