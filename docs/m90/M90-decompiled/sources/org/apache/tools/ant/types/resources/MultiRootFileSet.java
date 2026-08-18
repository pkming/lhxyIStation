package org.apache.tools.ant.types.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.AbstractFileSet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

/* JADX INFO: loaded from: classes3.dex */
public class MultiRootFileSet extends AbstractFileSet implements ResourceCollection {
    private Union union;
    private SetType type = SetType.file;
    private boolean cache = true;
    private List<File> baseDirs = new ArrayList();

    public enum SetType {
        file,
        dir,
        both
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public boolean isFilesystemOnly() {
        return true;
    }

    @Override // org.apache.tools.ant.types.AbstractFileSet
    public void setDir(File file) {
        throw new BuildException(getDataTypeName() + " doesn't support the dir attribute");
    }

    public void setType(SetType setType) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.type = setType;
    }

    public synchronized void setCache(boolean z) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.cache = z;
    }

    public void setBaseDirs(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        if (str == null || str.length() <= 0) {
            return;
        }
        for (String str2 : str.split(",")) {
            this.baseDirs.add(getProject().resolveFile(str2));
        }
    }

    public void addConfiguredBaseDir(FileResource fileResource) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        this.baseDirs.add(fileResource.getFile());
    }

    @Override // org.apache.tools.ant.types.AbstractFileSet, org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) {
        if (!this.baseDirs.isEmpty()) {
            throw tooManyAttributes();
        }
        super.setRefid(reference);
    }

    @Override // org.apache.tools.ant.types.AbstractFileSet, org.apache.tools.ant.types.DataType, org.apache.tools.ant.ProjectComponent
    public Object clone() {
        if (isReference()) {
            return ((MultiRootFileSet) getRef(getProject())).clone();
        }
        MultiRootFileSet multiRootFileSet = (MultiRootFileSet) super.clone();
        multiRootFileSet.baseDirs = new ArrayList(this.baseDirs);
        multiRootFileSet.union = null;
        return multiRootFileSet;
    }

    @Override // org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
    public Iterator<Resource> iterator() {
        if (isReference()) {
            return ((MultiRootFileSet) getRef(getProject())).iterator();
        }
        return merge().iterator();
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public int size() {
        if (isReference()) {
            return ((MultiRootFileSet) getRef(getProject())).size();
        }
        return merge().size();
    }

    @Override // org.apache.tools.ant.types.AbstractFileSet, org.apache.tools.ant.types.DataType
    public String toString() {
        if (isReference()) {
            return ((MultiRootFileSet) getRef(getProject())).toString();
        }
        return merge().toString();
    }

    private synchronized Union merge() {
        Union union;
        if (this.cache && (union = this.union) != null) {
            return union;
        }
        Union union2 = new Union();
        setup(union2);
        if (this.cache) {
            this.union = union2;
        }
        return union2;
    }

    private void setup(Union union) {
        Iterator<File> it = this.baseDirs.iterator();
        while (it.hasNext()) {
            union.add(new Worker(this.type, it.next()));
        }
    }

    private static class Worker extends AbstractFileSet implements ResourceCollection {
        private final SetType type;

        @Override // org.apache.tools.ant.types.ResourceCollection
        public boolean isFilesystemOnly() {
            return true;
        }

        private Worker(MultiRootFileSet multiRootFileSet, SetType setType, File file) {
            super(multiRootFileSet);
            this.type = setType;
            setDir(file);
        }

        @Override // org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
        public Iterator<Resource> iterator() {
            DirectoryScanner directoryScanner = getDirectoryScanner(getProject());
            String[] includedFiles = this.type == SetType.file ? directoryScanner.getIncludedFiles() : directoryScanner.getIncludedDirectories();
            if (this.type == SetType.both) {
                String[] includedFiles2 = directoryScanner.getIncludedFiles();
                String[] strArr = new String[includedFiles.length + includedFiles2.length];
                System.arraycopy(includedFiles, 0, strArr, 0, includedFiles.length);
                System.arraycopy(includedFiles2, 0, strArr, includedFiles.length, includedFiles2.length);
                includedFiles = strArr;
            }
            return new FileResourceIterator(getProject(), getDir(getProject()), includedFiles);
        }

        @Override // org.apache.tools.ant.types.ResourceCollection
        public int size() {
            DirectoryScanner directoryScanner = getDirectoryScanner(getProject());
            int includedFilesCount = this.type == SetType.file ? directoryScanner.getIncludedFilesCount() : directoryScanner.getIncludedDirsCount();
            return this.type == SetType.both ? includedFilesCount + directoryScanner.getIncludedFilesCount() : includedFilesCount;
        }
    }
}
