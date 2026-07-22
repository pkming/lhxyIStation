package org.apache.tools.ant.types.resources;

import java.util.Iterator;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;

/* JADX INFO: loaded from: classes3.dex */
public class BCFileSet extends FileSet {
    public BCFileSet() {
    }

    public BCFileSet(FileSet fileSet) {
        super(fileSet);
    }

    @Override // org.apache.tools.ant.types.FileSet, org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
    public Iterator<Resource> iterator() {
        if (isReference()) {
            return ((FileSet) getRef(getProject())).iterator();
        }
        FileResourceIterator fileResourceIterator = new FileResourceIterator(getProject(), getDir());
        fileResourceIterator.addFiles(getDirectoryScanner().getIncludedFiles());
        fileResourceIterator.addFiles(getDirectoryScanner().getIncludedDirectories());
        return fileResourceIterator;
    }

    @Override // org.apache.tools.ant.types.FileSet, org.apache.tools.ant.types.ResourceCollection
    public int size() {
        if (isReference()) {
            return ((FileSet) getRef(getProject())).size();
        }
        return getDirectoryScanner().getIncludedFilesCount() + getDirectoryScanner().getIncludedDirsCount();
    }
}
