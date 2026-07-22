package org.apache.poi.poifs.property;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.apache.poi.poifs.filesystem.BATManaged;
import org.apache.poi.poifs.storage.BlockWritable;
import org.apache.poi.poifs.storage.PropertyBlock;
import org.apache.poi.poifs.storage.RawDataBlockList;

/* JADX INFO: loaded from: classes3.dex */
public class PropertyTable implements BATManaged, BlockWritable {
    private BlockWritable[] _blocks;
    private List _properties;
    private int _start_block;

    public PropertyTable() {
        this._start_block = -2;
        this._properties = new ArrayList();
        addProperty(new RootProperty());
        this._blocks = null;
    }

    public PropertyTable(int i, RawDataBlockList rawDataBlockList) throws IOException {
        this._start_block = -2;
        this._blocks = null;
        List listConvertToProperties = PropertyFactory.convertToProperties(rawDataBlockList.fetchBlocks(i));
        this._properties = listConvertToProperties;
        populatePropertyTree((DirectoryProperty) listConvertToProperties.get(0));
    }

    public void addProperty(Property property) {
        this._properties.add(property);
    }

    public void removeProperty(Property property) {
        this._properties.remove(property);
    }

    public RootProperty getRoot() {
        return (RootProperty) this._properties.get(0);
    }

    public void preWrite() {
        Property[] propertyArr = (Property[]) this._properties.toArray(new Property[0]);
        for (int i = 0; i < propertyArr.length; i++) {
            propertyArr[i].setIndex(i);
        }
        this._blocks = PropertyBlock.createPropertyBlockArray(this._properties);
        for (Property property : propertyArr) {
            property.preWrite();
        }
    }

    public int getStartBlock() {
        return this._start_block;
    }

    private void populatePropertyTree(DirectoryProperty directoryProperty) throws IOException {
        int childIndex = directoryProperty.getChildIndex();
        if (Property.isValidIndex(childIndex)) {
            Stack stack = new Stack();
            stack.push(this._properties.get(childIndex));
            while (!stack.empty()) {
                Property property = (Property) stack.pop();
                directoryProperty.addChild(property);
                if (property.isDirectory()) {
                    populatePropertyTree((DirectoryProperty) property);
                }
                int previousChildIndex = property.getPreviousChildIndex();
                if (Property.isValidIndex(previousChildIndex)) {
                    stack.push(this._properties.get(previousChildIndex));
                }
                int nextChildIndex = property.getNextChildIndex();
                if (Property.isValidIndex(nextChildIndex)) {
                    stack.push(this._properties.get(nextChildIndex));
                }
            }
        }
    }

    @Override // org.apache.poi.poifs.filesystem.BATManaged
    public int countBlocks() {
        BlockWritable[] blockWritableArr = this._blocks;
        if (blockWritableArr == null) {
            return 0;
        }
        return blockWritableArr.length;
    }

    @Override // org.apache.poi.poifs.filesystem.BATManaged
    public void setStartBlock(int i) {
        this._start_block = i;
    }

    @Override // org.apache.poi.poifs.storage.BlockWritable
    public void writeBlocks(OutputStream outputStream) throws IOException {
        if (this._blocks == null) {
            return;
        }
        int i = 0;
        while (true) {
            BlockWritable[] blockWritableArr = this._blocks;
            if (i >= blockWritableArr.length) {
                return;
            }
            blockWritableArr[i].writeBlocks(outputStream);
            i++;
        }
    }
}
