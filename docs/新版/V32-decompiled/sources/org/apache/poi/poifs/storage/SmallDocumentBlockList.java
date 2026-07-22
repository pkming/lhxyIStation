package org.apache.poi.poifs.storage;

import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class SmallDocumentBlockList extends BlockListImpl {
    public SmallDocumentBlockList(List list) {
        setBlocks((SmallDocumentBlock[]) list.toArray(new SmallDocumentBlock[0]));
    }
}
