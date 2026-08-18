package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes3.dex */
public class RawDataBlockList extends BlockListImpl {
    public RawDataBlockList(InputStream inputStream) throws IOException {
        ArrayList arrayList = new ArrayList();
        while (true) {
            RawDataBlock rawDataBlock = new RawDataBlock(inputStream);
            if (!rawDataBlock.eof()) {
                arrayList.add(rawDataBlock);
            } else {
                setBlocks((RawDataBlock[]) arrayList.toArray(new RawDataBlock[0]));
                return;
            }
        }
    }
}
