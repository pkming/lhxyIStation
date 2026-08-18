package org.apache.poi.poifs.filesystem;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.poifs.dev.POIFSViewable;
import org.apache.poi.poifs.property.DirectoryProperty;
import org.apache.poi.poifs.property.Property;
import org.apache.poi.poifs.property.PropertyTable;
import org.apache.poi.poifs.storage.BATBlock;
import org.apache.poi.poifs.storage.BlockAllocationTableReader;
import org.apache.poi.poifs.storage.BlockAllocationTableWriter;
import org.apache.poi.poifs.storage.BlockList;
import org.apache.poi.poifs.storage.BlockWritable;
import org.apache.poi.poifs.storage.HeaderBlockReader;
import org.apache.poi.poifs.storage.HeaderBlockWriter;
import org.apache.poi.poifs.storage.RawDataBlockList;
import org.apache.poi.poifs.storage.SmallBlockTableReader;
import org.apache.poi.poifs.storage.SmallBlockTableWriter;

/* JADX INFO: loaded from: classes3.dex */
public class POIFSFileSystem implements POIFSViewable {
    private List _documents;
    private PropertyTable _property_table;
    private DirectoryNode _root;

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public String getShortDescription() {
        return "POIFS FileSystem";
    }

    public POIFSFileSystem() {
        this._property_table = new PropertyTable();
        this._documents = new ArrayList();
        this._root = null;
    }

    public POIFSFileSystem(InputStream inputStream) throws IOException {
        this();
        HeaderBlockReader headerBlockReader = new HeaderBlockReader(inputStream);
        RawDataBlockList rawDataBlockList = new RawDataBlockList(inputStream);
        new BlockAllocationTableReader(headerBlockReader.getBATCount(), headerBlockReader.getBATArray(), headerBlockReader.getXBATCount(), headerBlockReader.getXBATIndex(), rawDataBlockList);
        PropertyTable propertyTable = new PropertyTable(headerBlockReader.getPropertyStart(), rawDataBlockList);
        processProperties(SmallBlockTableReader.getSmallDocumentBlocks(rawDataBlockList, propertyTable.getRoot(), headerBlockReader.getSBATStart()), rawDataBlockList, propertyTable.getRoot().getChildren(), null);
    }

    public DocumentEntry createDocument(InputStream inputStream, String str) throws IOException {
        return getRoot().createDocument(str, inputStream);
    }

    public DocumentEntry createDocument(String str, int i, POIFSWriterListener pOIFSWriterListener) throws IOException {
        return getRoot().createDocument(str, i, pOIFSWriterListener);
    }

    public DirectoryEntry createDirectory(String str) throws IOException {
        return getRoot().createDirectory(str);
    }

    public void writeFilesystem(OutputStream outputStream) throws IOException {
        this._property_table.preWrite();
        SmallBlockTableWriter smallBlockTableWriter = new SmallBlockTableWriter(this._documents, this._property_table.getRoot());
        BlockAllocationTableWriter blockAllocationTableWriter = new BlockAllocationTableWriter();
        ArrayList<BATManaged> arrayList = new ArrayList();
        arrayList.addAll(this._documents);
        arrayList.add(this._property_table);
        arrayList.add(smallBlockTableWriter);
        arrayList.add(smallBlockTableWriter.getSBAT());
        for (BATManaged bATManaged : arrayList) {
            int iCountBlocks = bATManaged.countBlocks();
            if (iCountBlocks != 0) {
                bATManaged.setStartBlock(blockAllocationTableWriter.allocateSpace(iCountBlocks));
            }
        }
        int iCreateBlocks = blockAllocationTableWriter.createBlocks();
        HeaderBlockWriter headerBlockWriter = new HeaderBlockWriter();
        BATBlock[] bATBlocks = headerBlockWriter.setBATBlocks(blockAllocationTableWriter.countBlocks(), iCreateBlocks);
        headerBlockWriter.setPropertyStart(this._property_table.getStartBlock());
        headerBlockWriter.setSBATStart(smallBlockTableWriter.getSBAT().getStartBlock());
        headerBlockWriter.setSBATBlockCount(smallBlockTableWriter.getSBATBlockCount());
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(headerBlockWriter);
        arrayList2.addAll(this._documents);
        arrayList2.add(this._property_table);
        arrayList2.add(smallBlockTableWriter);
        arrayList2.add(smallBlockTableWriter.getSBAT());
        arrayList2.add(blockAllocationTableWriter);
        for (BATBlock bATBlock : bATBlocks) {
            arrayList2.add(bATBlock);
        }
        Iterator it = arrayList2.iterator();
        while (it.hasNext()) {
            ((BlockWritable) it.next()).writeBlocks(outputStream);
        }
    }

    public static void main(String[] strArr) throws IOException {
        if (strArr.length != 2) {
            System.err.println("two arguments required: input filename and output filename");
            System.exit(1);
        }
        FileInputStream fileInputStream = new FileInputStream(strArr[0]);
        FileOutputStream fileOutputStream = new FileOutputStream(strArr[1]);
        new POIFSFileSystem(fileInputStream).writeFilesystem(fileOutputStream);
        fileInputStream.close();
        fileOutputStream.close();
    }

    public DirectoryEntry getRoot() {
        if (this._root == null) {
            this._root = new DirectoryNode(this._property_table.getRoot(), this, null);
        }
        return this._root;
    }

    public DocumentInputStream createDocumentInputStream(String str) throws IOException {
        Entry entry = getRoot().getEntry(str);
        if (!entry.isDocumentEntry()) {
            throw new IOException(new StringBuffer().append("Entry '").append(str).append("' is not a DocumentEntry").toString());
        }
        return new DocumentInputStream((DocumentEntry) entry);
    }

    void addDocument(POIFSDocument pOIFSDocument) {
        this._documents.add(pOIFSDocument);
        this._property_table.addProperty(pOIFSDocument.getDocumentProperty());
    }

    void addDirectory(DirectoryProperty directoryProperty) {
        this._property_table.addProperty(directoryProperty);
    }

    void remove(EntryNode entryNode) {
        this._property_table.removeProperty(entryNode.getProperty());
        if (entryNode.isDocumentEntry()) {
            this._documents.remove(((DocumentNode) entryNode).getDocument());
        }
    }

    private void processProperties(BlockList blockList, BlockList blockList2, Iterator it, DirectoryNode directoryNode) throws IOException {
        POIFSDocument pOIFSDocument;
        while (it.hasNext()) {
            Property property = (Property) it.next();
            String name = property.getName();
            DirectoryNode directoryNode2 = directoryNode == null ? (DirectoryNode) getRoot() : directoryNode;
            if (property.isDirectory()) {
                DirectoryNode directoryNode3 = (DirectoryNode) directoryNode2.createDirectory(name);
                directoryNode3.setStorageClsid(property.getStorageClsid());
                processProperties(blockList, blockList2, ((DirectoryProperty) property).getChildren(), directoryNode3);
            } else {
                int startBlock = property.getStartBlock();
                int size = property.getSize();
                if (property.shouldUseSmallBlocks()) {
                    pOIFSDocument = new POIFSDocument(name, blockList.fetchBlocks(startBlock), size);
                } else {
                    pOIFSDocument = new POIFSDocument(name, blockList2.fetchBlocks(startBlock), size);
                }
                directoryNode2.createDocument(pOIFSDocument);
            }
        }
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public Object[] getViewableArray() {
        return preferArray() ? ((POIFSViewable) getRoot()).getViewableArray() : new Object[0];
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public Iterator getViewableIterator() {
        if (!preferArray()) {
            return ((POIFSViewable) getRoot()).getViewableIterator();
        }
        return Collections.EMPTY_LIST.iterator();
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public boolean preferArray() {
        return ((POIFSViewable) getRoot()).preferArray();
    }
}
