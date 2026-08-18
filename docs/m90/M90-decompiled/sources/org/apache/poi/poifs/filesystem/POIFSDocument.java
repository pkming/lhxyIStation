package org.apache.poi.poifs.filesystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.apache.poi.poifs.dev.POIFSViewable;
import org.apache.poi.poifs.property.DocumentProperty;
import org.apache.poi.poifs.property.Property;
import org.apache.poi.poifs.storage.BlockWritable;
import org.apache.poi.poifs.storage.DocumentBlock;
import org.apache.poi.poifs.storage.ListManagedBlock;
import org.apache.poi.poifs.storage.RawDataBlock;
import org.apache.poi.poifs.storage.SmallDocumentBlock;
import org.apache.poi.util.HexDump;

/* JADX INFO: loaded from: classes3.dex */
public class POIFSDocument implements BATManaged, BlockWritable, POIFSViewable {
    private BigBlockStore _big_store;
    private DocumentProperty _property;
    private int _size;
    private SmallBlockStore _small_store;

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public boolean preferArray() {
        return true;
    }

    public POIFSDocument(String str, RawDataBlock[] rawDataBlockArr, int i) throws IOException {
        this._size = i;
        this._big_store = new BigBlockStore(rawDataBlockArr);
        this._property = new DocumentProperty(str, this._size);
        this._small_store = new SmallBlockStore(new BlockWritable[0]);
        this._property.setDocument(this);
    }

    public POIFSDocument(String str, SmallDocumentBlock[] smallDocumentBlockArr, int i) {
        this._size = i;
        try {
            this._big_store = new BigBlockStore(new RawDataBlock[0]);
        } catch (IOException unused) {
        }
        this._property = new DocumentProperty(str, this._size);
        this._small_store = new SmallBlockStore(smallDocumentBlockArr);
        this._property.setDocument(this);
    }

    public POIFSDocument(String str, ListManagedBlock[] listManagedBlockArr, int i) throws IOException {
        this._size = i;
        DocumentProperty documentProperty = new DocumentProperty(str, this._size);
        this._property = documentProperty;
        documentProperty.setDocument(this);
        if (Property.isSmall(this._size)) {
            this._big_store = new BigBlockStore(new RawDataBlock[0]);
            this._small_store = new SmallBlockStore(listManagedBlockArr);
        } else {
            this._big_store = new BigBlockStore(listManagedBlockArr);
            this._small_store = new SmallBlockStore(new BlockWritable[0]);
        }
    }

    public POIFSDocument(String str, InputStream inputStream) throws IOException {
        DocumentBlock documentBlock;
        ArrayList arrayList = new ArrayList();
        this._size = 0;
        do {
            documentBlock = new DocumentBlock(inputStream);
            int size = documentBlock.size();
            if (size > 0) {
                arrayList.add(documentBlock);
                this._size += size;
            }
        } while (!documentBlock.partiallyRead());
        DocumentBlock[] documentBlockArr = (DocumentBlock[]) arrayList.toArray(new DocumentBlock[0]);
        this._big_store = new BigBlockStore(documentBlockArr);
        DocumentProperty documentProperty = new DocumentProperty(str, this._size);
        this._property = documentProperty;
        documentProperty.setDocument(this);
        if (this._property.shouldUseSmallBlocks()) {
            this._small_store = new SmallBlockStore(SmallDocumentBlock.convert(documentBlockArr, this._size));
            this._big_store = new BigBlockStore(new DocumentBlock[0]);
        } else {
            this._small_store = new SmallBlockStore(new BlockWritable[0]);
        }
    }

    public POIFSDocument(String str, int i, POIFSDocumentPath pOIFSDocumentPath, POIFSWriterListener pOIFSWriterListener) throws IOException {
        this._size = i;
        DocumentProperty documentProperty = new DocumentProperty(str, this._size);
        this._property = documentProperty;
        documentProperty.setDocument(this);
        if (this._property.shouldUseSmallBlocks()) {
            this._small_store = new SmallBlockStore(pOIFSDocumentPath, str, i, pOIFSWriterListener);
            this._big_store = new BigBlockStore(new Object[0]);
        } else {
            this._small_store = new SmallBlockStore(new BlockWritable[0]);
            this._big_store = new BigBlockStore(pOIFSDocumentPath, str, i, pOIFSWriterListener);
        }
    }

    public BlockWritable[] getSmallBlocks() {
        return this._small_store.getBlocks();
    }

    public int getSize() {
        return this._size;
    }

    void read(byte[] bArr, int i) {
        if (this._property.shouldUseSmallBlocks()) {
            SmallDocumentBlock.read(this._small_store.getBlocks(), bArr, i);
        } else {
            DocumentBlock.read(this._big_store.getBlocks(), bArr, i);
        }
    }

    DocumentProperty getDocumentProperty() {
        return this._property;
    }

    @Override // org.apache.poi.poifs.storage.BlockWritable
    public void writeBlocks(OutputStream outputStream) throws IOException {
        this._big_store.writeBlocks(outputStream);
    }

    @Override // org.apache.poi.poifs.filesystem.BATManaged
    public int countBlocks() {
        return this._big_store.countBlocks();
    }

    @Override // org.apache.poi.poifs.filesystem.BATManaged
    public void setStartBlock(int i) {
        this._property.setStartBlock(i);
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public Object[] getViewableArray() {
        String message;
        Object[] objArr = new Object[1];
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BlockWritable[] blocks = null;
            if (this._big_store.isValid()) {
                blocks = this._big_store.getBlocks();
            } else if (this._small_store.isValid()) {
                blocks = this._small_store.getBlocks();
            }
            if (blocks != null) {
                for (BlockWritable blockWritable : blocks) {
                    blockWritable.writeBlocks(byteArrayOutputStream);
                }
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                if (byteArray.length > this._property.getSize()) {
                    int size = this._property.getSize();
                    byte[] bArr = new byte[size];
                    System.arraycopy(byteArray, 0, bArr, 0, size);
                    byteArray = bArr;
                }
                ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                HexDump.dump(byteArray, 0L, byteArrayOutputStream2, 0);
                message = byteArrayOutputStream2.toString();
            } else {
                message = "<NO DATA>";
            }
        } catch (IOException e) {
            message = e.getMessage();
        }
        objArr[0] = message;
        return objArr;
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public Iterator getViewableIterator() {
        return Collections.EMPTY_LIST.iterator();
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public String getShortDescription() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Document: \"").append(this._property.getName()).append("\"");
        stringBuffer.append(" size = ").append(getSize());
        return stringBuffer.toString();
    }

    private class SmallBlockStore {
        private String name;
        private POIFSDocumentPath path;
        private int size;
        private SmallDocumentBlock[] smallBlocks;
        private POIFSWriterListener writer;

        SmallBlockStore(Object[] objArr) {
            this.smallBlocks = new SmallDocumentBlock[objArr.length];
            for (int i = 0; i < objArr.length; i++) {
                this.smallBlocks[i] = (SmallDocumentBlock) objArr[i];
            }
            this.path = null;
            this.name = null;
            this.size = -1;
            this.writer = null;
        }

        SmallBlockStore(POIFSDocumentPath pOIFSDocumentPath, String str, int i, POIFSWriterListener pOIFSWriterListener) {
            this.smallBlocks = new SmallDocumentBlock[0];
            this.path = pOIFSDocumentPath;
            this.name = str;
            this.size = i;
            this.writer = pOIFSWriterListener;
        }

        boolean isValid() {
            return this.smallBlocks.length > 0 || this.writer != null;
        }

        BlockWritable[] getBlocks() {
            if (isValid() && this.writer != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(this.size);
                this.writer.processPOIFSWriterEvent(new POIFSWriterEvent(new DocumentOutputStream(byteArrayOutputStream, this.size), this.path, this.name, this.size));
                this.smallBlocks = SmallDocumentBlock.convert(byteArrayOutputStream.toByteArray(), this.size);
            }
            return this.smallBlocks;
        }
    }

    private class BigBlockStore {
        private DocumentBlock[] bigBlocks;
        private String name;
        private POIFSDocumentPath path;
        private int size;
        private POIFSWriterListener writer;

        BigBlockStore(Object[] objArr) throws IOException {
            this.bigBlocks = new DocumentBlock[objArr.length];
            for (int i = 0; i < objArr.length; i++) {
                if (objArr[i] instanceof DocumentBlock) {
                    this.bigBlocks[i] = (DocumentBlock) objArr[i];
                } else {
                    this.bigBlocks[i] = new DocumentBlock((RawDataBlock) objArr[i]);
                }
            }
            this.path = null;
            this.name = null;
            this.size = -1;
            this.writer = null;
        }

        BigBlockStore(POIFSDocumentPath pOIFSDocumentPath, String str, int i, POIFSWriterListener pOIFSWriterListener) {
            this.bigBlocks = new DocumentBlock[0];
            this.path = pOIFSDocumentPath;
            this.name = str;
            this.size = i;
            this.writer = pOIFSWriterListener;
        }

        boolean isValid() {
            return this.bigBlocks.length > 0 || this.writer != null;
        }

        DocumentBlock[] getBlocks() {
            if (isValid() && this.writer != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(this.size);
                this.writer.processPOIFSWriterEvent(new POIFSWriterEvent(new DocumentOutputStream(byteArrayOutputStream, this.size), this.path, this.name, this.size));
                this.bigBlocks = DocumentBlock.convert(byteArrayOutputStream.toByteArray(), this.size);
            }
            return this.bigBlocks;
        }

        void writeBlocks(OutputStream outputStream) throws IOException {
            if (!isValid()) {
                return;
            }
            if (this.writer != null) {
                DocumentOutputStream documentOutputStream = new DocumentOutputStream(outputStream, this.size);
                this.writer.processPOIFSWriterEvent(new POIFSWriterEvent(documentOutputStream, this.path, this.name, this.size));
                documentOutputStream.writeFiller(countBlocks() * 512, DocumentBlock.getFillByte());
            } else {
                int i = 0;
                while (true) {
                    DocumentBlock[] documentBlockArr = this.bigBlocks;
                    if (i >= documentBlockArr.length) {
                        return;
                    }
                    documentBlockArr[i].writeBlocks(outputStream);
                    i++;
                }
            }
        }

        int countBlocks() {
            if (!isValid()) {
                return 0;
            }
            if (this.writer != null) {
                return ((this.size + 512) - 1) / 512;
            }
            return this.bigBlocks.length;
        }
    }
}
