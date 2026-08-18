package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResourceIterator;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.Restrict;
import org.apache.tools.ant.types.resources.Sort;
import org.apache.tools.ant.types.resources.comparators.FileSystem;
import org.apache.tools.ant.types.resources.comparators.ResourceComparator;
import org.apache.tools.ant.types.resources.comparators.Reverse;
import org.apache.tools.ant.types.resources.selectors.Exists;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.selectors.AndSelector;
import org.apache.tools.ant.types.selectors.ContainsRegexpSelector;
import org.apache.tools.ant.types.selectors.ContainsSelector;
import org.apache.tools.ant.types.selectors.DateSelector;
import org.apache.tools.ant.types.selectors.DependSelector;
import org.apache.tools.ant.types.selectors.DepthSelector;
import org.apache.tools.ant.types.selectors.ExtendSelector;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.FilenameSelector;
import org.apache.tools.ant.types.selectors.MajoritySelector;
import org.apache.tools.ant.types.selectors.NoneSelector;
import org.apache.tools.ant.types.selectors.NotSelector;
import org.apache.tools.ant.types.selectors.OrSelector;
import org.apache.tools.ant.types.selectors.PresentSelector;
import org.apache.tools.ant.types.selectors.SelectSelector;
import org.apache.tools.ant.types.selectors.SizeSelector;
import org.apache.tools.ant.types.selectors.modifiedselector.ModifiedSelector;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.SymbolicLinkUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Delete extends MatchingTask {
    private static final ResourceComparator REVERSE_FILESYSTEM = new Reverse(new FileSystem());
    private static final ResourceSelector EXISTS = new Exists();
    private static FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static SymbolicLinkUtils SYMLINK_UTILS = SymbolicLinkUtils.getSymbolicLinkUtils();
    protected File file = null;
    protected File dir = null;
    protected Vector<FileSet> filesets = new Vector<>();
    protected boolean usedMatchingTask = false;
    protected boolean includeEmpty = false;
    private int verbosity = 3;
    private boolean quiet = false;
    private boolean failonerror = true;
    private boolean deleteOnExit = false;
    private boolean removeNotFollowedSymlinks = false;
    private Resources rcs = null;
    private boolean performGc = Os.isFamily(Os.FAMILY_WINDOWS);

    private static class ReverseDirs implements ResourceCollection {
        static final Comparator<Comparable<?>> REVERSE = new Comparator<Comparable<?>>() { // from class: org.apache.tools.ant.taskdefs.Delete.ReverseDirs.1
            @Override // java.util.Comparator
            public int compare(Comparable<?> comparable, Comparable<?> comparable2) {
                return comparable.compareTo(comparable2) * (-1);
            }
        };
        private File basedir;
        private String[] dirs;
        private Project project;

        @Override // org.apache.tools.ant.types.ResourceCollection
        public boolean isFilesystemOnly() {
            return true;
        }

        ReverseDirs(Project project, File file, String[] strArr) {
            this.project = project;
            this.basedir = file;
            this.dirs = strArr;
            Arrays.sort(strArr, REVERSE);
        }

        @Override // org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
        public Iterator<Resource> iterator() {
            return new FileResourceIterator(this.project, this.basedir, this.dirs);
        }

        @Override // org.apache.tools.ant.types.ResourceCollection
        public int size() {
            return this.dirs.length;
        }
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setDir(File file) {
        this.dir = file;
        getImplicitFileSet().setDir(file);
    }

    public void setVerbose(boolean z) {
        if (z) {
            this.verbosity = 2;
        } else {
            this.verbosity = 3;
        }
    }

    public void setQuiet(boolean z) {
        this.quiet = z;
        if (z) {
            this.failonerror = false;
        }
    }

    public void setFailOnError(boolean z) {
        this.failonerror = z;
    }

    public void setDeleteOnExit(boolean z) {
        this.deleteOnExit = z;
    }

    public void setIncludeEmptyDirs(boolean z) {
        this.includeEmpty = z;
    }

    public void setPerformGcOnFailedDelete(boolean z) {
        this.performGc = z;
    }

    public void addFileset(FileSet fileSet) {
        this.filesets.addElement(fileSet);
    }

    public void add(ResourceCollection resourceCollection) {
        if (resourceCollection == null) {
            return;
        }
        if (this.rcs == null) {
            Resources resources = new Resources();
            this.rcs = resources;
            resources.setCache(true);
        }
        this.rcs.add(resourceCollection);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask
    public PatternSet.NameEntry createInclude() {
        this.usedMatchingTask = true;
        return super.createInclude();
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask
    public PatternSet.NameEntry createIncludesFile() {
        this.usedMatchingTask = true;
        return super.createIncludesFile();
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask
    public PatternSet.NameEntry createExclude() {
        this.usedMatchingTask = true;
        return super.createExclude();
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask
    public PatternSet.NameEntry createExcludesFile() {
        this.usedMatchingTask = true;
        return super.createExcludesFile();
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask
    public PatternSet createPatternSet() {
        this.usedMatchingTask = true;
        return super.createPatternSet();
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask
    public void setIncludes(String str) {
        this.usedMatchingTask = true;
        super.setIncludes(str);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask
    public void setExcludes(String str) {
        this.usedMatchingTask = true;
        super.setExcludes(str);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask
    public void setDefaultexcludes(boolean z) {
        this.usedMatchingTask = true;
        super.setDefaultexcludes(z);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask
    public void setIncludesfile(File file) {
        this.usedMatchingTask = true;
        super.setIncludesfile(file);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask
    public void setExcludesfile(File file) {
        this.usedMatchingTask = true;
        super.setExcludesfile(file);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask
    public void setCaseSensitive(boolean z) {
        this.usedMatchingTask = true;
        super.setCaseSensitive(z);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask
    public void setFollowSymlinks(boolean z) {
        this.usedMatchingTask = true;
        super.setFollowSymlinks(z);
    }

    public void setRemoveNotFollowedSymlinks(boolean z) {
        this.removeNotFollowedSymlinks = z;
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask, org.apache.tools.ant.types.selectors.SelectorContainer
    public void addSelector(SelectSelector selectSelector) {
        this.usedMatchingTask = true;
        super.addSelector(selectSelector);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask, org.apache.tools.ant.types.selectors.SelectorContainer
    public void addAnd(AndSelector andSelector) {
        this.usedMatchingTask = true;
        super.addAnd(andSelector);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask, org.apache.tools.ant.types.selectors.SelectorContainer
    public void addOr(OrSelector orSelector) {
        this.usedMatchingTask = true;
        super.addOr(orSelector);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask, org.apache.tools.ant.types.selectors.SelectorContainer
    public void addNot(NotSelector notSelector) {
        this.usedMatchingTask = true;
        super.addNot(notSelector);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask, org.apache.tools.ant.types.selectors.SelectorContainer
    public void addNone(NoneSelector noneSelector) {
        this.usedMatchingTask = true;
        super.addNone(noneSelector);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask, org.apache.tools.ant.types.selectors.SelectorContainer
    public void addMajority(MajoritySelector majoritySelector) {
        this.usedMatchingTask = true;
        super.addMajority(majoritySelector);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask, org.apache.tools.ant.types.selectors.SelectorContainer
    public void addDate(DateSelector dateSelector) {
        this.usedMatchingTask = true;
        super.addDate(dateSelector);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask, org.apache.tools.ant.types.selectors.SelectorContainer
    public void addSize(SizeSelector sizeSelector) {
        this.usedMatchingTask = true;
        super.addSize(sizeSelector);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask, org.apache.tools.ant.types.selectors.SelectorContainer
    public void addFilename(FilenameSelector filenameSelector) {
        this.usedMatchingTask = true;
        super.addFilename(filenameSelector);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask, org.apache.tools.ant.types.selectors.SelectorContainer
    public void addCustom(ExtendSelector extendSelector) {
        this.usedMatchingTask = true;
        super.addCustom(extendSelector);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask, org.apache.tools.ant.types.selectors.SelectorContainer
    public void addContains(ContainsSelector containsSelector) {
        this.usedMatchingTask = true;
        super.addContains(containsSelector);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask, org.apache.tools.ant.types.selectors.SelectorContainer
    public void addPresent(PresentSelector presentSelector) {
        this.usedMatchingTask = true;
        super.addPresent(presentSelector);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask, org.apache.tools.ant.types.selectors.SelectorContainer
    public void addDepth(DepthSelector depthSelector) {
        this.usedMatchingTask = true;
        super.addDepth(depthSelector);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask, org.apache.tools.ant.types.selectors.SelectorContainer
    public void addDepend(DependSelector dependSelector) {
        this.usedMatchingTask = true;
        super.addDepend(dependSelector);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask, org.apache.tools.ant.types.selectors.SelectorContainer
    public void addContainsRegexp(ContainsRegexpSelector containsRegexpSelector) {
        this.usedMatchingTask = true;
        super.addContainsRegexp(containsRegexpSelector);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask, org.apache.tools.ant.types.selectors.SelectorContainer
    public void addModified(ModifiedSelector modifiedSelector) {
        this.usedMatchingTask = true;
        super.addModified(modifiedSelector);
    }

    @Override // org.apache.tools.ant.taskdefs.MatchingTask, org.apache.tools.ant.types.selectors.SelectorContainer
    public void add(FileSelector fileSelector) {
        this.usedMatchingTask = true;
        super.add(fileSelector);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        File file;
        if (this.usedMatchingTask) {
            log("DEPRECATED - Use of the implicit FileSet is deprecated.  Use a nested fileset element instead.", this.quiet ? 3 : this.verbosity);
        }
        if (this.file == null && this.dir == null && this.filesets.size() == 0 && this.rcs == null) {
            throw new BuildException("At least one of the file or dir attributes, or a nested resource collection, must be set.");
        }
        if (this.quiet && this.failonerror) {
            throw new BuildException("quiet and failonerror cannot both be set to true", getLocation());
        }
        File file2 = this.file;
        if (file2 != null) {
            if (file2.exists()) {
                if (this.file.isDirectory()) {
                    log("Directory " + this.file.getAbsolutePath() + " cannot be removed using the file attribute.  Use dir instead.", this.quiet ? 3 : this.verbosity);
                } else {
                    log("Deleting: " + this.file.getAbsolutePath());
                    if (!delete(this.file)) {
                        handle("Unable to delete file " + this.file.getAbsolutePath());
                    }
                }
            } else if (isDanglingSymlink(this.file)) {
                log("Trying to delete file " + this.file.getAbsolutePath() + " which looks like a broken symlink.", this.quiet ? 3 : this.verbosity);
                if (!delete(this.file)) {
                    handle("Unable to delete file " + this.file.getAbsolutePath());
                }
            } else {
                log("Could not find file " + this.file.getAbsolutePath() + " to delete.", this.quiet ? 3 : this.verbosity);
            }
        }
        File file3 = this.dir;
        if (file3 != null && !this.usedMatchingTask) {
            if (file3.exists() && this.dir.isDirectory()) {
                if (this.verbosity == 3) {
                    log("Deleting directory " + this.dir.getAbsolutePath());
                }
                removeDir(this.dir);
            } else if (isDanglingSymlink(this.dir)) {
                log("Trying to delete directory " + this.dir.getAbsolutePath() + " which looks like a broken symlink.", this.quiet ? 3 : this.verbosity);
                if (!delete(this.dir)) {
                    handle("Unable to delete directory " + this.dir.getAbsolutePath());
                }
            }
        }
        Resources resources = new Resources();
        resources.setProject(getProject());
        resources.setCache(true);
        Resources resources2 = new Resources();
        resources2.setProject(getProject());
        resources2.setCache(true);
        FileSet implicitFileSet = null;
        if (this.usedMatchingTask && (file = this.dir) != null && file.isDirectory()) {
            implicitFileSet = getImplicitFileSet();
            implicitFileSet.setProject(getProject());
            this.filesets.add(implicitFileSet);
        }
        int size = this.filesets.size();
        for (int i = 0; i < size; i++) {
            FileSet fileSet = this.filesets.get(i);
            if (fileSet.getProject() == null) {
                log("Deleting fileset with no project specified; assuming executing project", 3);
                fileSet = (FileSet) fileSet.clone();
                fileSet.setProject(getProject());
            }
            final File dir = fileSet.getDir();
            if (fileSet.getErrorOnMissingDir() || (dir != null && dir.exists())) {
                if (dir == null) {
                    throw new BuildException("File or Resource without directory or file specified");
                }
                if (!dir.isDirectory()) {
                    handle("Directory does not exist: " + dir);
                } else {
                    DirectoryScanner directoryScanner = fileSet.getDirectoryScanner();
                    final String[] includedFiles = directoryScanner.getIncludedFiles();
                    resources.add(new ResourceCollection() { // from class: org.apache.tools.ant.taskdefs.Delete.1
                        @Override // org.apache.tools.ant.types.ResourceCollection
                        public boolean isFilesystemOnly() {
                            return true;
                        }

                        @Override // org.apache.tools.ant.types.ResourceCollection
                        public int size() {
                            return includedFiles.length;
                        }

                        @Override // org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
                        public Iterator<Resource> iterator() {
                            return new FileResourceIterator(Delete.this.getProject(), dir, includedFiles);
                        }
                    });
                    if (this.includeEmpty) {
                        resources2.add(new ReverseDirs(getProject(), dir, directoryScanner.getIncludedDirectories()));
                    }
                    if (this.removeNotFollowedSymlinks) {
                        String[] notFollowedSymlinks = directoryScanner.getNotFollowedSymlinks();
                        if (notFollowedSymlinks.length > 0) {
                            int length = notFollowedSymlinks.length;
                            String[] strArr = new String[length];
                            System.arraycopy(notFollowedSymlinks, 0, strArr, 0, notFollowedSymlinks.length);
                            Arrays.sort(strArr, ReverseDirs.REVERSE);
                            for (int i2 = 0; i2 < length; i2++) {
                                try {
                                    SYMLINK_UTILS.deleteSymbolicLink(new File(strArr[i2]), this);
                                } catch (IOException e) {
                                    handle(e);
                                }
                            }
                        }
                    }
                }
            }
        }
        resources.add(resources2);
        if (this.rcs != null) {
            Restrict restrict = new Restrict();
            restrict.add(EXISTS);
            restrict.add(this.rcs);
            Sort sort = new Sort();
            sort.add(REVERSE_FILESYSTEM);
            sort.add(restrict);
            resources.add(sort);
        }
        try {
            try {
                if (resources.isFilesystemOnly()) {
                    Iterator<Resource> it = resources.iterator();
                    while (it.hasNext()) {
                        File file4 = ((FileProvider) it.next().as(FileProvider.class)).getFile();
                        if (file4.exists() && (!file4.isDirectory() || file4.list().length == 0)) {
                            log("Deleting " + file4, this.verbosity);
                            if (!delete(file4) && this.failonerror) {
                                handle("Unable to delete " + (file4.isDirectory() ? "directory " : "file ") + file4);
                            }
                        }
                    }
                } else {
                    handle(getTaskName() + " handles only filesystem resources");
                }
                if (implicitFileSet == null) {
                    return;
                }
            } catch (Throwable th) {
                if (implicitFileSet != null) {
                    this.filesets.remove(implicitFileSet);
                }
                throw th;
            }
        } catch (Exception e2) {
            handle(e2);
            if (implicitFileSet == null) {
                return;
            }
        }
        this.filesets.remove(implicitFileSet);
    }

    private void handle(String str) {
        handle(new BuildException(str));
    }

    private void handle(Exception exc) {
        if (this.failonerror) {
            if (!(exc instanceof BuildException)) {
                throw new BuildException(exc);
            }
            throw ((BuildException) exc);
        }
        log(exc, this.quiet ? 3 : this.verbosity);
    }

    private boolean delete(File file) {
        if (FILE_UTILS.tryHardToDelete(file, this.performGc)) {
            return true;
        }
        if (!this.deleteOnExit) {
            return false;
        }
        log("Failed to delete " + file + ", calling deleteOnExit. This attempts to delete the file when the Ant jvm has exited and might not succeed.", this.quiet ? 3 : 2);
        file.deleteOnExit();
        return true;
    }

    protected void removeDir(File file) {
        String[] list = file.list();
        if (list == null) {
            list = new String[0];
        }
        for (String str : list) {
            File file2 = new File(file, str);
            if (file2.isDirectory()) {
                removeDir(file2);
            } else {
                log("Deleting " + file2.getAbsolutePath(), this.quiet ? 3 : this.verbosity);
                if (!delete(file2)) {
                    handle("Unable to delete file " + file2.getAbsolutePath());
                }
            }
        }
        log("Deleting directory " + file.getAbsolutePath(), this.verbosity);
        if (delete(file)) {
            return;
        }
        handle("Unable to delete directory " + file.getAbsolutePath());
    }

    protected void removeFiles(File file, String[] strArr, String[] strArr2) {
        int i = 0;
        if (strArr.length > 0) {
            log("Deleting " + strArr.length + " files from " + file.getAbsolutePath(), this.quiet ? 3 : this.verbosity);
            for (String str : strArr) {
                File file2 = new File(file, str);
                log("Deleting " + file2.getAbsolutePath(), this.quiet ? 3 : this.verbosity);
                if (!delete(file2)) {
                    handle("Unable to delete file " + file2.getAbsolutePath());
                }
            }
        }
        if (strArr2.length <= 0 || !this.includeEmpty) {
            return;
        }
        for (int length = strArr2.length - 1; length >= 0; length--) {
            File file3 = new File(file, strArr2[length]);
            String[] list = file3.list();
            if (list == null || list.length == 0) {
                log("Deleting " + file3.getAbsolutePath(), this.quiet ? 3 : this.verbosity);
                if (delete(file3)) {
                    i++;
                } else {
                    handle("Unable to delete directory " + file3.getAbsolutePath());
                }
            }
        }
        if (i > 0) {
            log("Deleted " + i + " director" + (i == 1 ? "y" : "ies") + " form " + file.getAbsolutePath(), this.quiet ? 3 : this.verbosity);
        }
    }

    private boolean isDanglingSymlink(File file) {
        try {
            return SYMLINK_UTILS.isDanglingSymbolicLink(file);
        } catch (IOException e) {
            log("Error while trying to detect " + file.getAbsolutePath() + " as broken symbolic link. " + e.getMessage(), this.quiet ? 3 : this.verbosity);
            return false;
        }
    }
}
