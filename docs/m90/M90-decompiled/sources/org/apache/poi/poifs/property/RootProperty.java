package org.apache.poi.poifs.property;

import org.apache.poi.poifs.storage.SmallDocumentBlock;

/* JADX INFO: loaded from: classes3.dex */
public class RootProperty extends DirectoryProperty {
    RootProperty() {
        super("Root Entry");
        setNodeColor((byte) 1);
        setPropertyType((byte) 5);
        setStartBlock(-2);
    }

    protected RootProperty(int i, byte[] bArr, int i2) {
        super(i, bArr, i2);
    }

    @Override // org.apache.poi.poifs.property.Property
    public void setSize(int i) {
        super.setSize(SmallDocumentBlock.calcSize(i));
    }
}
