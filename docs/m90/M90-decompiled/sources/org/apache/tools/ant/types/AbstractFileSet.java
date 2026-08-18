package org.apache.tools.ant.types;

import android.telephony.PhoneNumberUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.selectors.AndSelector;
import org.apache.tools.ant.types.selectors.ContainsRegexpSelector;
import org.apache.tools.ant.types.selectors.ContainsSelector;
import org.apache.tools.ant.types.selectors.DateSelector;
import org.apache.tools.ant.types.selectors.DependSelector;
import org.apache.tools.ant.types.selectors.DepthSelector;
import org.apache.tools.ant.types.selectors.DifferentSelector;
import org.apache.tools.ant.types.selectors.ExtendSelector;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.FilenameSelector;
import org.apache.tools.ant.types.selectors.MajoritySelector;
import org.apache.tools.ant.types.selectors.NoneSelector;
import org.apache.tools.ant.types.selectors.NotSelector;
import org.apache.tools.ant.types.selectors.OrSelector;
import org.apache.tools.ant.types.selectors.PresentSelector;
import org.apache.tools.ant.types.selectors.ReadableSelector;
import org.apache.tools.ant.types.selectors.SelectSelector;
import org.apache.tools.ant.types.selectors.SelectorContainer;
import org.apache.tools.ant.types.selectors.SelectorScanner;
import org.apache.tools.ant.types.selectors.SizeSelector;
import org.apache.tools.ant.types.selectors.TypeSelector;
import org.apache.tools.ant.types.selectors.WritableSelector;
import org.apache.tools.ant.types.selectors.modifiedselector.ModifiedSelector;

/* JADX INFO: loaded from: classes3.dex */
public abstract class AbstractFileSet extends DataType implements Cloneable, SelectorContainer {
    private List<PatternSet> additionalPatterns;
    private boolean caseSensitive;
    private PatternSet defaultPatterns;
    private File dir;
    private DirectoryScanner directoryScanner;
    private boolean errorOnMissingDir;
    private boolean followSymlinks;
    private int maxLevelsOfSymlinks;
    private List<FileSelector> selectors;
    private boolean useDefaultExcludes;

    public AbstractFileSet() {
        this.defaultPatterns = new PatternSet();
        this.additionalPatterns = new ArrayList();
        this.selectors = new ArrayList();
        this.useDefaultExcludes = true;
        this.caseSensitive = true;
        this.followSymlinks = true;
        this.errorOnMissingDir = true;
        this.maxLevelsOfSymlinks = 5;
        this.directoryScanner = null;
    }

    protected AbstractFileSet(AbstractFileSet abstractFileSet) {
        this.defaultPatterns = new PatternSet();
        this.additionalPatterns = new ArrayList();
        this.selectors = new ArrayList();
        this.useDefaultExcludes = true;
        this.caseSensitive = true;
        this.followSymlinks = true;
        this.errorOnMissingDir = true;
        this.maxLevelsOfSymlinks = 5;
        this.directoryScanner = null;
        this.dir = abstractFileSet.dir;
        this.defaultPatterns = abstractFileSet.defaultPatterns;
        this.additionalPatterns = abstractFileSet.additionalPatterns;
        this.selectors = abstractFileSet.selectors;
        this.useDefaultExcludes = abstractFileSet.useDefaultExcludes;
        this.caseSensitive = abstractFileSet.caseSensitive;
        this.followSymlinks = abstractFileSet.followSymlinks;
        this.errorOnMissingDir = abstractFileSet.errorOnMissingDir;
        this.maxLevelsOfSymlinks = abstractFileSet.maxLevelsOfSymlinks;
        setProject(abstractFileSet.getProject());
    }

    @Override // org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) throws BuildException {
        if (this.dir != null || this.defaultPatterns.hasPatterns(getProject())) {
            throw tooManyAttributes();
        }
        if (!this.additionalPatterns.isEmpty()) {
            throw noChildrenAllowed();
        }
        if (!this.selectors.isEmpty()) {
            throw noChildrenAllowed();
        }
        super.setRefid(reference);
    }

    public synchronized void setDir(File file) throws BuildException {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.dir = file;
        this.directoryScanner = null;
    }

    public File getDir() {
        return getDir(getProject());
    }

    public synchronized File getDir(Project project) {
        if (isReference()) {
            return getRef(project).getDir(project);
        }
        dieOnCircularReference();
        return this.dir;
    }

    public synchronized PatternSet createPatternSet() {
        PatternSet patternSet;
        if (isReference()) {
            throw noChildrenAllowed();
        }
        patternSet = new PatternSet();
        this.additionalPatterns.add(patternSet);
        this.directoryScanner = null;
        return patternSet;
    }

    public synchronized PatternSet.NameEntry createInclude() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        this.directoryScanner = null;
        return this.defaultPatterns.createInclude();
    }

    public synchronized PatternSet.NameEntry createIncludesFile() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        this.directoryScanner = null;
        return this.defaultPatterns.createIncludesFile();
    }

    public synchronized PatternSet.NameEntry createExclude() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        this.directoryScanner = null;
        return this.defaultPatterns.createExclude();
    }

    public synchronized PatternSet.NameEntry createExcludesFile() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        this.directoryScanner = null;
        return this.defaultPatterns.createExcludesFile();
    }

    public synchronized void setFile(File file) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        setDir(file.getParentFile());
        createInclude().setName(file.getName());
    }

    public synchronized void setIncludes(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.defaultPatterns.setIncludes(str);
        this.directoryScanner = null;
    }

    public synchronized void appendIncludes(String[] strArr) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        if (strArr != null) {
            for (String str : strArr) {
                this.defaultPatterns.createInclude().setName(str);
            }
            this.directoryScanner = null;
        }
    }

    public synchronized void setExcludes(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.defaultPatterns.setExcludes(str);
        this.directoryScanner = null;
    }

    public synchronized void appendExcludes(String[] strArr) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        if (strArr != null) {
            for (String str : strArr) {
                this.defaultPatterns.createExclude().setName(str);
            }
            this.directoryScanner = null;
        }
    }

    public synchronized void setIncludesfile(File file) throws BuildException {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.defaultPatterns.setIncludesfile(file);
        this.directoryScanner = null;
    }

    public synchronized void setExcludesfile(File file) throws BuildException {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.defaultPatterns.setExcludesfile(file);
        this.directoryScanner = null;
    }

    public synchronized void setDefaultexcludes(boolean z) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.useDefaultExcludes = z;
        this.directoryScanner = null;
    }

    public synchronized boolean getDefaultexcludes() {
        if (isReference()) {
            return getRef(getProject()).getDefaultexcludes();
        }
        dieOnCircularReference();
        return this.useDefaultExcludes;
    }

    public synchronized void setCaseSensitive(boolean z) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.caseSensitive = z;
        this.directoryScanner = null;
    }

    public synchronized boolean isCaseSensitive() {
        if (isReference()) {
            return getRef(getProject()).isCaseSensitive();
        }
        dieOnCircularReference();
        return this.caseSensitive;
    }

    public synchronized void setFollowSymlinks(boolean z) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.followSymlinks = z;
        this.directoryScanner = null;
    }

    public synchronized boolean isFollowSymlinks() {
        if (isReference()) {
            return getRef(getProject()).isCaseSensitive();
        }
        dieOnCircularReference();
        return this.followSymlinks;
    }

    public void setMaxLevelsOfSymlinks(int i) {
        this.maxLevelsOfSymlinks = i;
    }

    public int getMaxLevelsOfSymlinks() {
        return this.maxLevelsOfSymlinks;
    }

    public void setErrorOnMissingDir(boolean z) {
        this.errorOnMissingDir = z;
    }

    public boolean getErrorOnMissingDir() {
        return this.errorOnMissingDir;
    }

    public DirectoryScanner getDirectoryScanner() {
        return getDirectoryScanner(getProject());
    }

    public DirectoryScanner getDirectoryScanner(Project project) {
        DirectoryScanner directoryScanner;
        if (isReference()) {
            return getRef(project).getDirectoryScanner(project);
        }
        dieOnCircularReference();
        synchronized (this) {
            if (this.directoryScanner != null && project == getProject()) {
                directoryScanner = this.directoryScanner;
            } else {
                File file = this.dir;
                if (file == null) {
                    throw new BuildException("No directory specified for " + getDataTypeName() + ".");
                }
                if (!file.exists() && this.errorOnMissingDir) {
                    throw new BuildException(this.dir.getAbsolutePath() + DirectoryScanner.DOES_NOT_EXIST_POSTFIX);
                }
                if (!this.dir.isDirectory() && this.dir.exists()) {
                    throw new BuildException(this.dir.getAbsolutePath() + " is not a directory.");
                }
                DirectoryScanner directoryScanner2 = new DirectoryScanner();
                setupDirectoryScanner(directoryScanner2, project);
                directoryScanner2.setFollowSymlinks(this.followSymlinks);
                directoryScanner2.setErrorOnMissingDir(this.errorOnMissingDir);
                directoryScanner2.setMaxLevelsOfSymlinks(this.maxLevelsOfSymlinks);
                this.directoryScanner = project == getProject() ? directoryScanner2 : this.directoryScanner;
                directoryScanner = directoryScanner2;
            }
        }
        directoryScanner.scan();
        return directoryScanner;
    }

    public void setupDirectoryScanner(FileScanner fileScanner) {
        setupDirectoryScanner(fileScanner, getProject());
    }

    public synchronized void setupDirectoryScanner(FileScanner fileScanner, Project project) {
        if (isReference()) {
            getRef(project).setupDirectoryScanner(fileScanner, project);
            return;
        }
        dieOnCircularReference(project);
        if (fileScanner == null) {
            throw new IllegalArgumentException("ds cannot be null");
        }
        fileScanner.setBasedir(this.dir);
        PatternSet patternSetMergePatterns = mergePatterns(project);
        project.log(getDataTypeName() + ": Setup scanner in dir " + this.dir + " with " + patternSetMergePatterns, 4);
        fileScanner.setIncludes(patternSetMergePatterns.getIncludePatterns(project));
        fileScanner.setExcludes(patternSetMergePatterns.getExcludePatterns(project));
        if (fileScanner instanceof SelectorScanner) {
            ((SelectorScanner) fileScanner).setSelectors(getSelectors(project));
        }
        if (this.useDefaultExcludes) {
            fileScanner.addDefaultExcludes();
        }
        fileScanner.setCaseSensitive(this.caseSensitive);
    }

    protected AbstractFileSet getRef(Project project) {
        return (AbstractFileSet) getCheckedRef(project);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public synchronized boolean hasSelectors() {
        if (isReference()) {
            return getRef(getProject()).hasSelectors();
        }
        dieOnCircularReference();
        return !this.selectors.isEmpty();
    }

    public synchronized boolean hasPatterns() {
        if (isReference() && getProject() != null) {
            return getRef(getProject()).hasPatterns();
        }
        dieOnCircularReference();
        if (this.defaultPatterns.hasPatterns(getProject())) {
            return true;
        }
        Iterator<PatternSet> it = this.additionalPatterns.iterator();
        while (it.hasNext()) {
            if (it.next().hasPatterns(getProject())) {
                return true;
            }
        }
        return false;
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public synchronized int selectorCount() {
        if (isReference()) {
            return getRef(getProject()).selectorCount();
        }
        dieOnCircularReference();
        return this.selectors.size();
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public synchronized FileSelector[] getSelectors(Project project) {
        if (isReference()) {
            return getRef(getProject()).getSelectors(project);
        }
        dieOnCircularReference(project);
        List<FileSelector> list = this.selectors;
        return (FileSelector[]) list.toArray(new FileSelector[list.size()]);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public synchronized Enumeration<FileSelector> selectorElements() {
        if (isReference()) {
            return getRef(getProject()).selectorElements();
        }
        dieOnCircularReference();
        return Collections.enumeration(this.selectors);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public synchronized void appendSelector(FileSelector fileSelector) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        this.selectors.add(fileSelector);
        this.directoryScanner = null;
        setChecked(false);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addSelector(SelectSelector selectSelector) {
        appendSelector(selectSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addAnd(AndSelector andSelector) {
        appendSelector(andSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addOr(OrSelector orSelector) {
        appendSelector(orSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addNot(NotSelector notSelector) {
        appendSelector(notSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addNone(NoneSelector noneSelector) {
        appendSelector(noneSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addMajority(MajoritySelector majoritySelector) {
        appendSelector(majoritySelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addDate(DateSelector dateSelector) {
        appendSelector(dateSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addSize(SizeSelector sizeSelector) {
        appendSelector(sizeSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addDifferent(DifferentSelector differentSelector) {
        appendSelector(differentSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addFilename(FilenameSelector filenameSelector) {
        appendSelector(filenameSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addType(TypeSelector typeSelector) {
        appendSelector(typeSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addCustom(ExtendSelector extendSelector) {
        appendSelector(extendSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addContains(ContainsSelector containsSelector) {
        appendSelector(containsSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addPresent(PresentSelector presentSelector) {
        appendSelector(presentSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addDepth(DepthSelector depthSelector) {
        appendSelector(depthSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addDepend(DependSelector dependSelector) {
        appendSelector(dependSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addContainsRegexp(ContainsRegexpSelector containsRegexpSelector) {
        appendSelector(containsRegexpSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void addModified(ModifiedSelector modifiedSelector) {
        appendSelector(modifiedSelector);
    }

    public void addReadable(ReadableSelector readableSelector) {
        appendSelector(readableSelector);
    }

    public void addWritable(WritableSelector writableSelector) {
        appendSelector(writableSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorContainer
    public void add(FileSelector fileSelector) {
        appendSelector(fileSelector);
    }

    @Override // org.apache.tools.ant.types.DataType
    public String toString() {
        if (isReference()) {
            return getRef(getProject()).toString();
        }
        dieOnCircularReference();
        String[] includedFiles = getDirectoryScanner(getProject()).getIncludedFiles();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < includedFiles.length; i++) {
            if (i > 0) {
                stringBuffer.append(PhoneNumberUtils.WAIT);
            }
            stringBuffer.append(includedFiles[i]);
        }
        return stringBuffer.toString();
    }

    @Override // org.apache.tools.ant.types.DataType, org.apache.tools.ant.ProjectComponent
    public synchronized Object clone() {
        if (isReference()) {
            return getRef(getProject()).clone();
        }
        try {
            AbstractFileSet abstractFileSet = (AbstractFileSet) super.clone();
            abstractFileSet.defaultPatterns = (PatternSet) this.defaultPatterns.clone();
            abstractFileSet.additionalPatterns = new ArrayList(this.additionalPatterns.size());
            Iterator<PatternSet> it = this.additionalPatterns.iterator();
            while (it.hasNext()) {
                abstractFileSet.additionalPatterns.add((PatternSet) it.next().clone());
            }
            abstractFileSet.selectors = new ArrayList(this.selectors);
            return abstractFileSet;
        } catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    public String[] mergeIncludes(Project project) {
        return mergePatterns(project).getIncludePatterns(project);
    }

    public String[] mergeExcludes(Project project) {
        return mergePatterns(project).getExcludePatterns(project);
    }

    public synchronized PatternSet mergePatterns(Project project) {
        if (isReference()) {
            return getRef(project).mergePatterns(project);
        }
        dieOnCircularReference();
        PatternSet patternSet = (PatternSet) this.defaultPatterns.clone();
        int size = this.additionalPatterns.size();
        for (int i = 0; i < size; i++) {
            patternSet.append(this.additionalPatterns.get(i), project);
        }
        return patternSet;
    }

    @Override // org.apache.tools.ant.types.DataType
    protected synchronized void dieOnCircularReference(Stack<Object> stack, Project project) throws BuildException {
        if (isChecked()) {
            return;
        }
        if (isReference()) {
            super.dieOnCircularReference(stack, project);
        } else {
            for (Object obj : this.selectors) {
                if (obj instanceof DataType) {
                    pushAndInvokeCircularReferenceCheck((DataType) obj, stack, project);
                }
            }
            Iterator<PatternSet> it = this.additionalPatterns.iterator();
            while (it.hasNext()) {
                pushAndInvokeCircularReferenceCheck(it.next(), stack, project);
            }
            setChecked(true);
        }
    }
}
