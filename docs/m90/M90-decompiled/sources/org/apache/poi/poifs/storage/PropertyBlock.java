package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.poi.poifs.property.Property;

/* JADX INFO: loaded from: classes3.dex */
public class PropertyBlock extends BigBlock {
    private static final int _properties_per_block = 4;
    private Property[] _properties = new Property[4];

    private PropertyBlock(Property[] propertyArr, int i) {
        for (int i2 = 0; i2 < 4; i2++) {
            this._properties[i2] = propertyArr[i2 + i];
        }
    }

    public static BlockWritable[] createPropertyBlockArray(List list) {
        int size = ((list.size() + 4) - 1) / 4;
        int i = size * 4;
        Property[] propertyArr = new Property[i];
        System.arraycopy(list.toArray(new Property[0]), 0, propertyArr, 0, list.size());
        for (int size2 = list.size(); size2 < i; size2++) {
            propertyArr[size2] = new Property() { // from class: org.apache.poi.poifs.storage.PropertyBlock.1
                @Override // org.apache.poi.poifs.property.Property
                public boolean isDirectory() {
                    return false;
                }

                @Override // org.apache.poi.poifs.property.Property
                protected void preWrite() {
                }
            };
        }
        BlockWritable[] blockWritableArr = new BlockWritable[size];
        for (int i2 = 0; i2 < size; i2++) {
            blockWritableArr[i2] = new PropertyBlock(propertyArr, i2 * 4);
        }
        return blockWritableArr;
    }

    @Override // org.apache.poi.poifs.storage.BigBlock
    void writeData(OutputStream outputStream) throws IOException {
        for (int i = 0; i < 4; i++) {
            this._properties[i].writeData(outputStream);
        }
    }
}
