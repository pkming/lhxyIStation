package org.apache.poi.poifs.storage;

import java.io.IOException;
import org.apache.poi.poifs.property.RootProperty;

/* JADX INFO: loaded from: classes3.dex */
public class SmallBlockTableReader {
    public static BlockList getSmallDocumentBlocks(RawDataBlockList rawDataBlockList, RootProperty rootProperty, int i) throws IOException {
        SmallDocumentBlockList smallDocumentBlockList = new SmallDocumentBlockList(SmallDocumentBlock.extract(rawDataBlockList.fetchBlocks(rootProperty.getStartBlock())));
        new BlockAllocationTableReader(rawDataBlockList.fetchBlocks(i), smallDocumentBlockList);
        return smallDocumentBlockList;
    }
}
