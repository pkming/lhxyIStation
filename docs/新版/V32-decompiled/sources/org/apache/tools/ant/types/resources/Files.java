package org.apache.tools.ant.types.resources;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.selectors.AbstractSelectorContainer;
import org.apache.tools.ant.types.selectors.FileSelector;

/* JADX INFO: loaded from: classes3.dex */
public class Files extends AbstractSelectorContainer implements ResourceCollection {
    private static final Iterator<Resource> EMPTY_ITERATOR = Collections.emptySet().iterator();
    private Vector<PatternSet> additionalPatterns;
    private boolean caseSensitive;
    private PatternSet defaultPatterns;
    private DirectoryScanner ds;
    private boolean followSymlinks;
    private boolean useDefaultExcludes;

    @Override // org.apache.tools.ant.types.ResourceCollection
    public boolean isFilesystemOnly() {
        return true;
    }

    public Files() {
        this.defaultPatterns = new PatternSet();
        this.additionalPatterns = new Vector<>();
        this.useDefaultExcludes = true;
        this.caseSensitive = true;
        this.followSymlinks = true;
        this.ds = null;
    }

    protected Files(Files files) {
        this.defaultPatterns = new PatternSet();
        this.additionalPatterns = new Vector<>();
        this.useDefaultExcludes = true;
        this.caseSensitive = true;
        this.followSymlinks = true;
        this.ds = null;
        this.defaultPatterns = files.defaultPatterns;
        this.additionalPatterns = files.additionalPatterns;
        this.useDefaultExcludes = files.useDefaultExcludes;
        this.caseSensitive = files.caseSensitive;
        this.followSymlinks = files.followSymlinks;
        this.ds = files.ds;
        setProject(files.getProject());
    }

    @Override // org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) throws BuildException {
        if (hasPatterns(this.defaultPatterns)) {
            throw tooManyAttributes();
        }
        if (!this.additionalPatterns.isEmpty()) {
            throw noChildrenAllowed();
        }
        if (hasSelectors()) {
            throw noChildrenAllowed();
        }
        super.setRefid(reference);
    }

    public synchronized PatternSet createPatternSet() {
        PatternSet patternSet;
        if (isReference()) {
            throw noChildrenAllowed();
        }
        patternSet = new PatternSet();
        this.additionalPatterns.addElement(patternSet);
        this.ds = null;
        setChecked(false);
        return patternSet;
    }

    public synchronized PatternSet.NameEntry createInclude() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        this.ds = null;
        return this.defaultPatterns.createInclude();
    }

    public synchronized PatternSet.NameEntry createIncludesFile() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        this.ds = null;
        return this.defaultPatterns.createIncludesFile();
    }

    public synchronized PatternSet.NameEntry createExclude() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        this.ds = null;
        return this.defaultPatterns.createExclude();
    }

    public synchronized PatternSet.NameEntry createExcludesFile() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        this.ds = null;
        return this.defaultPatterns.createExcludesFile();
    }

    public synchronized void setIncludes(String str) {
        checkAttributesAllowed();
        this.defaultPatterns.setIncludes(str);
        this.ds = null;
    }

    public synchronized void appendIncludes(String[] strArr) {
        checkAttributesAllowed();
        if (strArr != null) {
            for (String str : strArr) {
                this.defaultPatterns.createInclude().setName(str);
            }
            this.ds = null;
        }
    }

    public synchronized void setExcludes(String str) {
        checkAttributesAllowed();
        this.defaultPatterns.setExcludes(str);
        this.ds = null;
    }

    public synchronized void appendExcludes(String[] strArr) {
        checkAttributesAllowed();
        if (strArr != null) {
            for (String str : strArr) {
                this.defaultPatterns.createExclude().setName(str);
            }
            this.ds = null;
        }
    }

    public synchronized void setIncludesfile(File file) throws BuildException {
        checkAttributesAllowed();
        this.defaultPatterns.setIncludesfile(file);
        this.ds = null;
    }

    public synchronized void setExcludesfile(File file) throws BuildException {
        checkAttributesAllowed();
        this.defaultPatterns.setExcludesfile(file);
        this.ds = null;
    }

    public synchronized void setDefaultexcludes(boolean z) {
        checkAttributesAllowed();
        this.useDefaultExcludes = z;
        this.ds = null;
    }

    public synchronized boolean getDefaultexcludes() {
        return isReference() ? getRef().getDefaultexcludes() : this.useDefaultExcludes;
    }

    public synchronized void setCaseSensitive(boolean z) {
        checkAttributesAllowed();
        this.caseSensitive = z;
        this.ds = null;
    }

    public synchronized boolean isCaseSensitive() {
        return isReference() ? getRef().isCaseSensitive() : this.caseSensitive;
    }

    public synchronized void setFollowSymlinks(boolean z) {
        checkAttributesAllowed();
        this.followSymlinks = z;
        this.ds = null;
    }

    public synchronized boolean isFollowSymlinks() {
        return isReference() ? getRef().isFollowSymlinks() : this.followSymlinks;
    }

    @Override // org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
    public synchronized Iterator<Resource> iterator() {
        if (isReference()) {
            return getRef().iterator();
        }
        ensureDirectoryScannerSetup();
        this.ds.scan();
        int includedFilesCount = this.ds.getIncludedFilesCount();
        int includedDirsCount = this.ds.getIncludedDirsCount();
        if (includedFilesCount + includedDirsCount == 0) {
            return EMPTY_ITERATOR;
        }
        FileResourceIterator fileResourceIterator = new FileResourceIterator(getProject());
        if (includedFilesCount > 0) {
            fileResourceIterator.addFiles(this.ds.getIncludedFiles());
        }
        if (includedDirsCount > 0) {
            fileResourceIterator.addFiles(this.ds.getIncludedDirectories());
        }
        return fileResourceIterator;
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public synchronized int size() {
        if (isReference()) {
            return getRef().size();
        }
        ensureDirectoryScannerSetup();
        this.ds.scan();
        return this.ds.getIncludedFilesCount() + this.ds.getIncludedDirsCount();
    }

    public synchronized boolean hasPatterns() {
        if (isReference()) {
            return getRef().hasPatterns();
        }
        dieOnCircularReference();
        if (hasPatterns(this.defaultPatterns)) {
            return true;
        }
        Iterator<PatternSet> it = this.additionalPatterns.iterator();
        while (it.hasNext()) {
            if (hasPatterns(it.next())) {
                return true;
            }
        }
        return false;
    }

    @Override // org.apache.tools.ant.types.selectors.AbstractSelectorContainer, org.apache.tools.ant.types.selectors.SelectorContainer
    public synchronized void appendSelector(FileSelector fileSelector) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        super.appendSelector(fileSelector);
        this.ds = null;
    }

    @Override // org.apache.tools.ant.types.selectors.AbstractSelectorContainer, org.apache.tools.ant.types.DataType
    public String toString() {
        if (isReference()) {
            return getRef().toString();
        }
        Iterator<Resource> it = iterator();
        if (!it.hasNext()) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        while (it.hasNext()) {
            if (stringBuffer.length() > 0) {
                stringBuffer.append(File.pathSeparatorChar);
            }
            stringBuffer.append(it.next());
        }
        return stringBuffer.toString();
    }

    @Override // org.apache.tools.ant.types.selectors.AbstractSelectorContainer, org.apache.tools.ant.types.DataType, org.apache.tools.ant.ProjectComponent
    public synchronized Object clone() {
        if (isReference()) {
            return getRef().clone();
        }
        Files files = (Files) super.clone();
        files.defaultPatterns = (PatternSet) this.defaultPatterns.clone();
        files.additionalPatterns = new Vector<>(this.additionalPatterns.size());
        Iterator<PatternSet> it = this.additionalPatterns.iterator();
        while (it.hasNext()) {
            files.additionalPatterns.add((PatternSet) it.next().clone());
        }
        return files;
    }

    public String[] mergeIncludes(Project project) {
        return mergePatterns(project).getIncludePatterns(project);
    }

    public String[] mergeExcludes(Project project) {
        return mergePatterns(project).getExcludePatterns(project);
    }

    public synchronized PatternSet mergePatterns(Project project) {
        if (isReference()) {
            return getRef().mergePatterns(project);
        }
        dieOnCircularReference();
        PatternSet patternSet = new PatternSet();
        patternSet.append(this.defaultPatterns, project);
        int size = this.additionalPatterns.size();
        for (int i = 0; i < size; i++) {
            patternSet.append(this.additionalPatterns.elementAt(i), project);
        }
        return patternSet;
    }

    protected Files getRef() {
        return (Files) getCheckedRef();
    }

    private synchronized void ensureDirectoryScannerSetup() {
        dieOnCircularReference();
        if (this.ds == null) {
            this.ds = new DirectoryScanner();
            PatternSet patternSetMergePatterns = mergePatterns(getProject());
            this.ds.setIncludes(patternSetMergePatterns.getIncludePatterns(getProject()));
            this.ds.setExcludes(patternSetMergePatterns.getExcludePatterns(getProject()));
            this.ds.setSelectors(getSelectors(getProject()));
            if (this.useDefaultExcludes) {
                this.ds.addDefaultExcludes();
            }
            this.ds.setCaseSensitive(this.caseSensitive);
            this.ds.setFollowSymlinks(this.followSymlinks);
        }
    }

    private boolean hasPatterns(PatternSet patternSet) throws Throwable {
        String[] includePatterns = patternSet.getIncludePatterns(getProject());
        return (includePatterns != null && includePatterns.length > 0) || (includePatterns != null && patternSet.getExcludePatterns(getProject()).length > 0);
    }
}
