package org.apache.tools.ant.types;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/* JADX INFO: loaded from: classes3.dex */
public class ZipFileSet extends ArchiveFileSet {
    private String encoding;

    public ZipFileSet() {
        this.encoding = null;
    }

    protected ZipFileSet(FileSet fileSet) {
        super(fileSet);
        this.encoding = null;
    }

    protected ZipFileSet(ZipFileSet zipFileSet) {
        super((ArchiveFileSet) zipFileSet);
        this.encoding = null;
        this.encoding = zipFileSet.encoding;
    }

    public void setEncoding(String str) {
        checkZipFileSetAttributesAllowed();
        this.encoding = str;
    }

    public String getEncoding() {
        if (isReference()) {
            AbstractFileSet ref = getRef(getProject());
            if (ref instanceof ZipFileSet) {
                return ((ZipFileSet) ref).getEncoding();
            }
            return null;
        }
        return this.encoding;
    }

    @Override // org.apache.tools.ant.types.ArchiveFileSet
    protected ArchiveScanner newArchiveScanner() {
        ZipScanner zipScanner = new ZipScanner();
        zipScanner.setEncoding(this.encoding);
        return zipScanner;
    }

    @Override // org.apache.tools.ant.types.AbstractFileSet
    protected AbstractFileSet getRef(Project project) {
        dieOnCircularReference(project);
        Object referencedObject = getRefid().getReferencedObject(project);
        if (referencedObject instanceof ZipFileSet) {
            return (AbstractFileSet) referencedObject;
        }
        if (referencedObject instanceof FileSet) {
            ZipFileSet zipFileSet = new ZipFileSet((FileSet) referencedObject);
            configureFileSet(zipFileSet);
            return zipFileSet;
        }
        throw new BuildException(getRefid().getRefId() + " doesn't denote a zipfileset or a fileset");
    }

    @Override // org.apache.tools.ant.types.ArchiveFileSet, org.apache.tools.ant.types.FileSet, org.apache.tools.ant.types.AbstractFileSet, org.apache.tools.ant.types.DataType, org.apache.tools.ant.ProjectComponent
    public Object clone() {
        if (isReference()) {
            return ((ZipFileSet) getRef(getProject())).clone();
        }
        return super.clone();
    }

    private void checkZipFileSetAttributesAllowed() {
        if (getProject() == null || (isReference() && (getRefid().getReferencedObject(getProject()) instanceof ZipFileSet))) {
            checkAttributesAllowed();
        }
    }
}
