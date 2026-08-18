package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.AbstractFileSet;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.util.SourceFileScanner;

/* JADX INFO: loaded from: classes3.dex */
public class ExecuteOn extends ExecTask {
    protected Vector<AbstractFileSet> filesets = new Vector<>();
    private Union resources = null;
    private boolean relative = false;
    private boolean parallel = false;
    private boolean forwardSlash = false;
    protected String type = "file";
    protected Commandline.Marker srcFilePos = null;
    private boolean skipEmpty = false;
    protected Commandline.Marker targetFilePos = null;
    protected Mapper mapperElement = null;
    protected FileNameMapper mapper = null;
    protected File destDir = null;
    private int maxParallel = -1;
    private boolean addSourceFile = true;
    private boolean verbose = false;
    private boolean ignoreMissing = true;
    private boolean force = false;
    protected boolean srcIsFirst = true;

    public void addFileset(FileSet fileSet) {
        this.filesets.addElement(fileSet);
    }

    public void addDirset(DirSet dirSet) {
        this.filesets.addElement(dirSet);
    }

    public void addFilelist(FileList fileList) {
        add(fileList);
    }

    public void add(ResourceCollection resourceCollection) {
        if (this.resources == null) {
            this.resources = new Union();
        }
        this.resources.add(resourceCollection);
    }

    public void setRelative(boolean z) {
        this.relative = z;
    }

    public void setParallel(boolean z) {
        this.parallel = z;
    }

    public void setType(FileDirBoth fileDirBoth) {
        this.type = fileDirBoth.getValue();
    }

    public void setSkipEmptyFilesets(boolean z) {
        this.skipEmpty = z;
    }

    public void setDest(File file) {
        this.destDir = file;
    }

    public void setForwardslash(boolean z) {
        this.forwardSlash = z;
    }

    public void setMaxParallel(int i) {
        this.maxParallel = i;
    }

    public void setAddsourcefile(boolean z) {
        this.addSourceFile = z;
    }

    public void setVerbose(boolean z) {
        this.verbose = z;
    }

    public void setIgnoremissing(boolean z) {
        this.ignoreMissing = z;
    }

    public void setForce(boolean z) {
        this.force = z;
    }

    public Commandline.Marker createSrcfile() {
        if (this.srcFilePos != null) {
            throw new BuildException(getTaskType() + " doesn't support multiple srcfile elements.", getLocation());
        }
        Commandline.Marker markerCreateMarker = this.cmdl.createMarker();
        this.srcFilePos = markerCreateMarker;
        return markerCreateMarker;
    }

    public Commandline.Marker createTargetfile() {
        if (this.targetFilePos != null) {
            throw new BuildException(getTaskType() + " doesn't support multiple targetfile elements.", getLocation());
        }
        Commandline.Marker markerCreateMarker = this.cmdl.createMarker();
        this.targetFilePos = markerCreateMarker;
        this.srcIsFirst = this.srcFilePos != null;
        return markerCreateMarker;
    }

    public Mapper createMapper() throws BuildException {
        if (this.mapperElement != null) {
            throw new BuildException(Expand.ERROR_MULTIPLE_MAPPERS, getLocation());
        }
        Mapper mapper = new Mapper(getProject());
        this.mapperElement = mapper;
        return mapper;
    }

    public void add(FileNameMapper fileNameMapper) {
        createMapper().add(fileNameMapper);
    }

    @Override // org.apache.tools.ant.taskdefs.ExecTask
    protected void checkConfiguration() {
        if ("execon".equals(getTaskName())) {
            log("!! execon is deprecated. Use apply instead. !!");
        }
        super.checkConfiguration();
        if (this.filesets.size() == 0 && this.resources == null) {
            throw new BuildException("no resources specified", getLocation());
        }
        if (this.targetFilePos != null && this.mapperElement == null) {
            throw new BuildException("targetfile specified without mapper", getLocation());
        }
        if (this.destDir != null && this.mapperElement == null) {
            throw new BuildException("dest specified without mapper", getLocation());
        }
        Mapper mapper = this.mapperElement;
        if (mapper != null) {
            this.mapper = mapper.getImplementation();
        }
    }

    @Override // org.apache.tools.ant.taskdefs.ExecTask
    protected ExecuteStreamHandler createHandler() throws BuildException {
        return this.redirectorElement == null ? super.createHandler() : new PumpStreamHandler();
    }

    @Override // org.apache.tools.ant.taskdefs.ExecTask
    protected void setupRedirector() {
        super.setupRedirector();
        this.redirector.setAppendProperties(true);
    }

    @Override // org.apache.tools.ant.taskdefs.ExecTask
    protected void runExec(Execute execute) throws BuildException {
        try {
            try {
                Vector<String> vector = new Vector<>();
                Vector<File> vector2 = new Vector<>();
                int size = this.filesets.size();
                boolean z = false;
                int i = 0;
                int i2 = 0;
                for (int i3 = 0; i3 < size; i3++) {
                    String str = this.type;
                    AbstractFileSet abstractFileSetElementAt = this.filesets.elementAt(i3);
                    if ((abstractFileSetElementAt instanceof DirSet) && !"dir".equals(this.type)) {
                        log("Found a nested dirset but type is " + this.type + ". Temporarily switching to type=\"dir\" on the assumption that you really did mean <dirset> not <fileset>.", 4);
                        str = "dir";
                    }
                    File dir = abstractFileSetElementAt.getDir(getProject());
                    DirectoryScanner directoryScanner = abstractFileSetElementAt.getDirectoryScanner(getProject());
                    if (!"dir".equals(str)) {
                        for (String str2 : getFiles(dir, directoryScanner)) {
                            i++;
                            vector.addElement(str2);
                            vector2.addElement(dir);
                        }
                    }
                    if (!"file".equals(str)) {
                        for (String str3 : getDirs(dir, directoryScanner)) {
                            i2++;
                            vector.addElement(str3);
                            vector2.addElement(dir);
                        }
                    }
                    if (vector.size() == 0 && this.skipEmpty) {
                        logSkippingFileset(str, directoryScanner, dir);
                    } else if (!this.parallel) {
                        int size2 = vector.size();
                        String[] strArr = new String[size2];
                        vector.copyInto(strArr);
                        int i4 = 0;
                        while (i4 < size2) {
                            String[] commandline = getCommandline(strArr[i4], dir);
                            log(Commandline.describeCommand(commandline), 3);
                            execute.setCommandline(commandline);
                            if (this.redirectorElement != null) {
                                setupRedirector();
                                this.redirectorElement.configure(this.redirector, strArr[i4]);
                            }
                            if (this.redirectorElement != null || z) {
                                execute.setStreamHandler(this.redirector.createHandler());
                            }
                            runExecute(execute);
                            i4++;
                            z = true;
                        }
                        vector.removeAllElements();
                        vector2.removeAllElements();
                    }
                }
                Union union = this.resources;
                if (union != null) {
                    for (Resource resource : union) {
                        if (resource.isExists() || !this.ignoreMissing) {
                            File file = null;
                            String name = resource.getName();
                            FileProvider fileProvider = (FileProvider) resource.as(FileProvider.class);
                            if (fileProvider != null) {
                                FileResource fileResourceAsFileResource = ResourceUtils.asFileResource(fileProvider);
                                File baseDir = fileResourceAsFileResource.getBaseDir();
                                if (baseDir == null) {
                                    name = fileResourceAsFileResource.getFile().getAbsolutePath();
                                }
                                file = baseDir;
                            }
                            if (restrict(new String[]{name}, file).length != 0) {
                                if ((!resource.isDirectory() || !resource.isExists()) && !"dir".equals(this.type)) {
                                    i++;
                                } else if (resource.isDirectory() && !"file".equals(this.type)) {
                                    i2++;
                                }
                                vector2.add(file);
                                vector.add(name);
                                if (!this.parallel) {
                                    String[] commandline2 = getCommandline(name, file);
                                    log(Commandline.describeCommand(commandline2), 3);
                                    execute.setCommandline(commandline2);
                                    if (this.redirectorElement != null) {
                                        setupRedirector();
                                        this.redirectorElement.configure(this.redirector, name);
                                    }
                                    if (this.redirectorElement != null || z) {
                                        execute.setStreamHandler(this.redirector.createHandler());
                                    }
                                    runExecute(execute);
                                    vector.removeAllElements();
                                    vector2.removeAllElements();
                                    z = true;
                                }
                            }
                        }
                    }
                }
                if (this.parallel && (vector.size() > 0 || !this.skipEmpty)) {
                    runParallel(execute, vector, vector2);
                    z = true;
                }
                if (z) {
                    log("Applied " + this.cmdl.getExecutable() + " to " + i + " file" + (i != 1 ? "s" : "") + " and " + i2 + " director" + (i2 != 1 ? "ies" : "y") + ".", this.verbose ? 2 : 3);
                }
            } catch (IOException e) {
                throw new BuildException("Execute failed: " + e, e, getLocation());
            }
        } finally {
            logFlush();
            this.redirector.setAppendProperties(false);
            this.redirector.setProperties();
        }
    }

    private void logSkippingFileset(String str, DirectoryScanner directoryScanner, File file) {
        log("Skipping fileset for directory " + file + ". It is " + ((!"dir".equals(str) ? directoryScanner.getIncludedFilesCount() : 0) + ("file".equals(str) ? 0 : directoryScanner.getIncludedDirsCount()) > 0 ? "up to date." : "empty."), this.verbose ? 2 : 3);
    }

    protected String[] getCommandline(String[] strArr, File[] fileArr) {
        String absolutePath;
        String strReplace;
        char c = File.separatorChar;
        Vector vector = new Vector();
        if (this.targetFilePos != null) {
            HashSet hashSet = new HashSet();
            for (String str : strArr) {
                String[] strArrMapFileName = this.mapper.mapFileName(str);
                if (strArrMapFileName != null) {
                    for (int i = 0; i < strArrMapFileName.length; i++) {
                        if (!this.relative) {
                            strReplace = new File(this.destDir, strArrMapFileName[i]).getAbsolutePath();
                        } else {
                            strReplace = strArrMapFileName[i];
                        }
                        if (this.forwardSlash && c != '/') {
                            strReplace = strReplace.replace(c, '/');
                        }
                        if (!hashSet.contains(strReplace)) {
                            vector.addElement(strReplace);
                            hashSet.add(strReplace);
                        }
                    }
                }
            }
        }
        String[] strArr2 = (String[]) vector.toArray(new String[vector.size()]);
        if (!this.addSourceFile) {
            strArr = new String[0];
        }
        String[] commandline = this.cmdl.getCommandline();
        String[] strArr3 = new String[commandline.length + strArr.length + strArr2.length];
        int length = commandline.length;
        Commandline.Marker marker = this.srcFilePos;
        if (marker != null) {
            length = marker.getPosition();
        }
        Commandline.Marker marker2 = this.targetFilePos;
        if (marker2 != null) {
            int position = marker2.getPosition();
            if (length < position || (length == position && this.srcIsFirst)) {
                System.arraycopy(commandline, 0, strArr3, 0, length);
                System.arraycopy(commandline, length, strArr3, strArr.length + length, position - length);
                insertTargetFiles(strArr2, strArr3, strArr.length + position, this.targetFilePos.getPrefix(), this.targetFilePos.getSuffix());
                System.arraycopy(commandline, position, strArr3, strArr.length + position + strArr2.length, commandline.length - position);
            } else {
                System.arraycopy(commandline, 0, strArr3, 0, position);
                insertTargetFiles(strArr2, strArr3, position, this.targetFilePos.getPrefix(), this.targetFilePos.getSuffix());
                System.arraycopy(commandline, position, strArr3, strArr2.length + position, length - position);
                System.arraycopy(commandline, length, strArr3, strArr.length + length + strArr2.length, commandline.length - length);
                length += strArr2.length;
            }
        } else {
            System.arraycopy(commandline, 0, strArr3, 0, length);
            System.arraycopy(commandline, length, strArr3, strArr.length + length, commandline.length - length);
        }
        for (int i2 = 0; i2 < strArr.length; i2++) {
            if (this.relative) {
                absolutePath = strArr[i2];
            } else {
                absolutePath = new File(fileArr[i2], strArr[i2]).getAbsolutePath();
            }
            if (this.forwardSlash && c != '/') {
                absolutePath = absolutePath.replace(c, '/');
            }
            Commandline.Marker marker3 = this.srcFilePos;
            if (marker3 != null && (marker3.getPrefix().length() > 0 || this.srcFilePos.getSuffix().length() > 0)) {
                absolutePath = this.srcFilePos.getPrefix() + absolutePath + this.srcFilePos.getSuffix();
            }
            strArr3[length + i2] = absolutePath;
        }
        return strArr3;
    }

    protected String[] getCommandline(String str, File file) {
        return getCommandline(new String[]{str}, new File[]{file});
    }

    protected String[] getFiles(File file, DirectoryScanner directoryScanner) {
        return restrict(directoryScanner.getIncludedFiles(), file);
    }

    protected String[] getDirs(File file, DirectoryScanner directoryScanner) {
        return restrict(directoryScanner.getIncludedDirectories(), file);
    }

    protected String[] getFilesAndDirs(FileList fileList) {
        return restrict(fileList.getFiles(getProject()), fileList.getDir(getProject()));
    }

    private String[] restrict(String[] strArr, File file) {
        return (this.mapper == null || this.force) ? strArr : new SourceFileScanner(this).restrict(strArr, file, this.destDir, this.mapper);
    }

    protected void runParallel(Execute execute, Vector<String> vector, Vector<File> vector2) throws IOException, BuildException {
        int size = vector.size();
        String[] strArr = new String[size];
        vector.copyInto(strArr);
        File[] fileArr = new File[vector2.size()];
        vector2.copyInto(fileArr);
        if (this.maxParallel <= 0 || size == 0) {
            String[] commandline = getCommandline(strArr, fileArr);
            log(Commandline.describeCommand(commandline), 3);
            execute.setCommandline(commandline);
            if (this.redirectorElement != null) {
                setupRedirector();
                this.redirectorElement.configure(this.redirector, null);
                execute.setStreamHandler(this.redirector.createHandler());
            }
            runExecute(execute);
            return;
        }
        int size2 = vector.size();
        int i = 0;
        while (size2 > 0) {
            int iMin = Math.min(size2, this.maxParallel);
            String[] strArr2 = new String[iMin];
            System.arraycopy(strArr, i, strArr2, 0, iMin);
            File[] fileArr2 = new File[iMin];
            System.arraycopy(fileArr, i, fileArr2, 0, iMin);
            String[] commandline2 = getCommandline(strArr2, fileArr2);
            log(Commandline.describeCommand(commandline2), 3);
            execute.setCommandline(commandline2);
            if (this.redirectorElement != null) {
                setupRedirector();
                this.redirectorElement.configure(this.redirector, null);
            }
            if (this.redirectorElement != null || i > 0) {
                execute.setStreamHandler(this.redirector.createHandler());
            }
            runExecute(execute);
            size2 -= iMin;
            i += iMin;
        }
    }

    private static void insertTargetFiles(String[] strArr, String[] strArr2, int i, String str, String str2) {
        if (str.length() == 0 && str2.length() == 0) {
            System.arraycopy(strArr, 0, strArr2, i, strArr.length);
            return;
        }
        for (int i2 = 0; i2 < strArr.length; i2++) {
            strArr2[i + i2] = str + strArr[i2] + str2;
        }
    }

    public static class FileDirBoth extends EnumeratedAttribute {
        public static final String DIR = "dir";
        public static final String FILE = "file";

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"file", "dir", "both"};
        }
    }
}
