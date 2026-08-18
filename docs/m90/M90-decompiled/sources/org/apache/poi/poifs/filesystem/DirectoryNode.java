package org.apache.poi.poifs.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.poifs.dev.POIFSViewable;
import org.apache.poi.poifs.property.DirectoryProperty;
import org.apache.poi.poifs.property.DocumentProperty;
import org.apache.poi.poifs.property.Property;

/* JADX INFO: loaded from: classes3.dex */
public class DirectoryNode extends EntryNode implements DirectoryEntry, POIFSViewable {
    private Map _entries;
    private POIFSFileSystem _filesystem;
    private POIFSDocumentPath _path;

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public Object[] getViewableArray() {
        return new Object[0];
    }

    @Override // org.apache.poi.poifs.filesystem.EntryNode, org.apache.poi.poifs.filesystem.Entry
    public boolean isDirectoryEntry() {
        return true;
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public boolean preferArray() {
        return false;
    }

    DirectoryNode(DirectoryProperty directoryProperty, POIFSFileSystem pOIFSFileSystem, DirectoryNode directoryNode) {
        Entry documentNode;
        super(directoryProperty, directoryNode);
        if (directoryNode == null) {
            this._path = new POIFSDocumentPath();
        } else {
            this._path = new POIFSDocumentPath(directoryNode._path, new String[]{directoryProperty.getName()});
        }
        this._filesystem = pOIFSFileSystem;
        this._entries = new HashMap();
        Iterator children = directoryProperty.getChildren();
        while (children.hasNext()) {
            Property property = (Property) children.next();
            if (property.isDirectory()) {
                documentNode = new DirectoryNode((DirectoryProperty) property, this._filesystem, this);
            } else {
                documentNode = new DocumentNode((DocumentProperty) property, this);
            }
            this._entries.put(documentNode.getName(), documentNode);
        }
    }

    public POIFSDocumentPath getPath() {
        return this._path;
    }

    DocumentEntry createDocument(POIFSDocument pOIFSDocument) throws IOException {
        DocumentProperty documentProperty = pOIFSDocument.getDocumentProperty();
        DocumentNode documentNode = new DocumentNode(documentProperty, this);
        ((DirectoryProperty) getProperty()).addChild(documentProperty);
        this._filesystem.addDocument(pOIFSDocument);
        this._entries.put(documentProperty.getName(), documentNode);
        return documentNode;
    }

    boolean changeName(String str, String str2) {
        EntryNode entryNode = (EntryNode) this._entries.get(str);
        if (entryNode == null) {
            return false;
        }
        boolean zChangeName = ((DirectoryProperty) getProperty()).changeName(entryNode.getProperty(), str2);
        if (!zChangeName) {
            return zChangeName;
        }
        this._entries.remove(str);
        this._entries.put(entryNode.getProperty().getName(), entryNode);
        return zChangeName;
    }

    boolean deleteEntry(EntryNode entryNode) {
        boolean zDeleteChild = ((DirectoryProperty) getProperty()).deleteChild(entryNode.getProperty());
        if (zDeleteChild) {
            this._entries.remove(entryNode.getName());
            this._filesystem.remove(entryNode);
        }
        return zDeleteChild;
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public Iterator getEntries() {
        return this._entries.values().iterator();
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public boolean isEmpty() {
        return this._entries.isEmpty();
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public int getEntryCount() {
        return this._entries.size();
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public Entry getEntry(String str) throws FileNotFoundException {
        Entry entry = str != null ? (Entry) this._entries.get(str) : null;
        if (entry != null) {
            return entry;
        }
        throw new FileNotFoundException(new StringBuffer().append("no such entry: \"").append(str).append("\"").toString());
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public DocumentEntry createDocument(String str, InputStream inputStream) throws IOException {
        return createDocument(new POIFSDocument(str, inputStream));
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public DocumentEntry createDocument(String str, int i, POIFSWriterListener pOIFSWriterListener) throws IOException {
        return createDocument(new POIFSDocument(str, i, this._path, pOIFSWriterListener));
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public DirectoryEntry createDirectory(String str) throws IOException {
        DirectoryProperty directoryProperty = new DirectoryProperty(str);
        DirectoryNode directoryNode = new DirectoryNode(directoryProperty, this._filesystem, this);
        ((DirectoryProperty) getProperty()).addChild(directoryProperty);
        this._filesystem.addDirectory(directoryProperty);
        this._entries.put(str, directoryNode);
        return directoryNode;
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public ClassID getStorageClsid() {
        return getProperty().getStorageClsid();
    }

    @Override // org.apache.poi.poifs.filesystem.DirectoryEntry
    public void setStorageClsid(ClassID classID) {
        getProperty().setStorageClsid(classID);
    }

    @Override // org.apache.poi.poifs.filesystem.EntryNode
    protected boolean isDeleteOK() {
        return isEmpty();
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public Iterator getViewableIterator() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(getProperty());
        Iterator it = new TreeMap(this._entries).values().iterator();
        while (it.hasNext()) {
            arrayList.add(it.next());
        }
        return arrayList.iterator();
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public String getShortDescription() {
        return getName();
    }
}
