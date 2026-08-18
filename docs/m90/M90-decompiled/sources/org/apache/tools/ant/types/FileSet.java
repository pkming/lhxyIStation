package org.apache.tools.ant.types;

import java.util.Iterator;
import org.apache.tools.ant.types.resources.FileResourceIterator;

/* JADX INFO: loaded from: classes3.dex */
public class FileSet extends AbstractFileSet implements ResourceCollection {
    public boolean isFilesystemOnly() {
        return true;
    }

    public FileSet() {
    }

    protected FileSet(FileSet fileSet) {
        super(fileSet);
    }

    @Override // org.apache.tools.ant.types.AbstractFileSet, org.apache.tools.ant.types.DataType, org.apache.tools.ant.ProjectComponent
    public Object clone() {
        if (isReference()) {
            return ((FileSet) getRef(getProject())).clone();
        }
        return super.clone();
    }

    public Iterator<Resource> iterator() {
        if (isReference()) {
            return ((FileSet) getRef(getProject())).iterator();
        }
        return new FileResourceIterator(getProject(), getDir(getProject()), getDirectoryScanner(getProject()).getIncludedFiles());
    }

    public int size() {
        if (isReference()) {
            return ((FileSet) getRef(getProject())).size();
        }
        return getDirectoryScanner(getProject()).getIncludedFilesCount();
    }
}
