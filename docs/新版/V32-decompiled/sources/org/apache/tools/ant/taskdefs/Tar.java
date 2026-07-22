package org.apache.tools.ant.taskdefs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.ArchiveFileSet;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.ArchiveResource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.TarResource;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.MergingMapper;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.util.SourceFileScanner;
import org.apache.tools.bzip2.CBZip2OutputStream;
import org.apache.tools.tar.TarConstants;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;

/* JADX INFO: loaded from: classes3.dex */
public class Tar extends MatchingTask {
    private static final int BUFFER_SIZE = 8192;
    public static final String FAIL = "fail";
    public static final String GNU = "gnu";
    public static final String OMIT = "omit";
    public static final String TRUNCATE = "truncate";
    public static final String WARN = "warn";
    File baseDir;
    File tarFile;
    private TarLongFileMode longFileMode = new TarLongFileMode();
    Vector filesets = new Vector();
    private Vector resourceCollections = new Vector();
    Vector fileSetFiles = new Vector();
    private boolean longWarningGiven = false;
    private TarCompressionMethod compression = new TarCompressionMethod();

    public TarFileSet createTarFileSet() {
        TarFileSet tarFileSet = new TarFileSet();
        tarFileSet.setProject(getProject());
        this.filesets.addElement(tarFileSet);
        return tarFileSet;
    }

    public void add(ResourceCollection resourceCollection) {
        this.resourceCollections.add(resourceCollection);
    }

    public void setTarfile(File file) {
        this.tarFile = file;
    }

    public void setDestFile(File file) {
        this.tarFile = file;
    }

    public void setBasedir(File file) {
        this.baseDir = file;
    }

    public void setLongfile(String str) {
        log("DEPRECATED - The setLongfile(String) method has been deprecated. Use setLongfile(Tar.TarLongFileMode) instead.");
        TarLongFileMode tarLongFileMode = new TarLongFileMode();
        this.longFileMode = tarLongFileMode;
        tarLongFileMode.setValue(str);
    }

    public void setLongfile(TarLongFileMode tarLongFileMode) {
        this.longFileMode = tarLongFileMode;
    }

    public void setCompression(TarCompressionMethod tarCompressionMethod) {
        this.compression = tarCompressionMethod;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        TarOutputStream tarOutputStream;
        File file = this.tarFile;
        if (file == null) {
            throw new BuildException("tarfile attribute must be set!", getLocation());
        }
        if (file.exists() && this.tarFile.isDirectory()) {
            throw new BuildException("tarfile is a directory!", getLocation());
        }
        if (this.tarFile.exists() && !this.tarFile.canWrite()) {
            throw new BuildException("Can not write to the specified tarfile!", getLocation());
        }
        Vector vector = (Vector) this.filesets.clone();
        try {
            File file2 = this.baseDir;
            if (file2 != null) {
                if (!file2.exists()) {
                    throw new BuildException("basedir does not exist!", getLocation());
                }
                TarFileSet tarFileSet = new TarFileSet(this.fileset);
                tarFileSet.setDir(this.baseDir);
                this.filesets.addElement(tarFileSet);
            }
            if (this.filesets.size() == 0 && this.resourceCollections.size() == 0) {
                throw new BuildException("You must supply either a basedir attribute or some nested resource collections.", getLocation());
            }
            Enumeration enumerationElements = this.filesets.elements();
            boolean zCheck = true;
            while (enumerationElements.hasMoreElements()) {
                zCheck &= check((TarFileSet) enumerationElements.nextElement());
            }
            Enumeration enumerationElements2 = this.resourceCollections.elements();
            while (enumerationElements2.hasMoreElements()) {
                zCheck &= check((ResourceCollection) enumerationElements2.nextElement());
            }
            if (zCheck) {
                log("Nothing to do: " + this.tarFile.getAbsolutePath() + " is up to date.", 2);
                return;
            }
            File parentFile = this.tarFile.getParentFile();
            if (parentFile != null && !parentFile.isDirectory() && !parentFile.mkdirs() && !parentFile.isDirectory()) {
                throw new BuildException("Failed to create missing parent directory for " + this.tarFile);
            }
            log("Building tar: " + this.tarFile.getAbsolutePath(), 2);
            TarOutputStream tarOutputStream2 = null;
            try {
                try {
                    tarOutputStream = new TarOutputStream(this.compression.compress(new BufferedOutputStream(new FileOutputStream(this.tarFile))));
                } catch (Throwable th) {
                    th = th;
                }
            } catch (IOException e) {
                e = e;
            }
            try {
                tarOutputStream.setDebug(true);
                if (this.longFileMode.isTruncateMode()) {
                    tarOutputStream.setLongFileMode(1);
                } else if (this.longFileMode.isFailMode() || this.longFileMode.isOmitMode()) {
                    tarOutputStream.setLongFileMode(0);
                } else if (this.longFileMode.isPosixMode()) {
                    tarOutputStream.setLongFileMode(3);
                } else {
                    tarOutputStream.setLongFileMode(2);
                }
                this.longWarningGiven = false;
                Enumeration enumerationElements3 = this.filesets.elements();
                while (enumerationElements3.hasMoreElements()) {
                    tar((TarFileSet) enumerationElements3.nextElement(), tarOutputStream);
                }
                Enumeration enumerationElements4 = this.resourceCollections.elements();
                while (enumerationElements4.hasMoreElements()) {
                    tar((ResourceCollection) enumerationElements4.nextElement(), tarOutputStream);
                }
                FileUtils.close(tarOutputStream);
            } catch (IOException e2) {
                e = e2;
                throw new BuildException("Problem creating TAR: " + e.getMessage(), e, getLocation());
            } catch (Throwable th2) {
                th = th2;
                tarOutputStream2 = tarOutputStream;
                FileUtils.close(tarOutputStream2);
                throw th;
            }
        } finally {
            this.filesets = vector;
        }
    }

    protected void tarFile(File file, TarOutputStream tarOutputStream, String str, TarFileSet tarFileSet) throws IOException {
        if (file.equals(this.tarFile)) {
            return;
        }
        tarResource(new FileResource(file), tarOutputStream, str, tarFileSet);
    }

    protected void tarResource(Resource resource, TarOutputStream tarOutputStream, String str, TarFileSet tarFileSet) throws IOException {
        boolean preserveLeadingSlashes;
        if (resource.isExists()) {
            if (tarFileSet != null) {
                String fullpath = tarFileSet.getFullpath(getProject());
                if (fullpath.length() > 0) {
                    str = fullpath;
                } else {
                    if (str.length() <= 0) {
                        return;
                    }
                    String prefix = tarFileSet.getPrefix(getProject());
                    if (prefix.length() > 0 && !prefix.endsWith("/")) {
                        prefix = prefix + "/";
                    }
                    str = prefix + str;
                }
                preserveLeadingSlashes = tarFileSet.getPreserveLeadingSlashes();
                if (str.startsWith("/") && !preserveLeadingSlashes) {
                    int length = str.length();
                    if (length <= 1) {
                        return;
                    } else {
                        str = str.substring(1, length);
                    }
                }
            } else {
                preserveLeadingSlashes = false;
            }
            if (resource.isDirectory() && !str.endsWith("/")) {
                str = str + "/";
            }
            if (str.length() >= 100) {
                if (this.longFileMode.isOmitMode()) {
                    log("Omitting: " + str, 2);
                    return;
                } else if (this.longFileMode.isWarnMode()) {
                    log("Entry: " + str + " longer than 100 characters.", 1);
                    if (!this.longWarningGiven) {
                        log("Resulting tar file can only be processed successfully by GNU compatible tar commands", 1);
                        this.longWarningGiven = true;
                    }
                } else if (this.longFileMode.isFailMode()) {
                    throw new BuildException("Entry: " + str + " longer than 100characters.", getLocation());
                }
            }
            TarEntry tarEntry = new TarEntry(str, preserveLeadingSlashes);
            tarEntry.setModTime(resource.getLastModified());
            if (resource instanceof ArchiveResource) {
                tarEntry.setMode(((ArchiveResource) resource).getMode());
                if (resource instanceof TarResource) {
                    TarResource tarResource = (TarResource) resource;
                    tarEntry.setUserName(tarResource.getUserName());
                    tarEntry.setUserId(tarResource.getUid());
                    tarEntry.setGroupName(tarResource.getGroup());
                    tarEntry.setGroupId(tarResource.getGid());
                }
            }
            if (resource.isDirectory()) {
                if (tarFileSet != null && tarFileSet.hasDirModeBeenSet()) {
                    tarEntry.setMode(tarFileSet.getDirMode(getProject()));
                }
            } else {
                if (resource.size() > TarConstants.MAXSIZE) {
                    throw new BuildException("Resource: " + resource + " larger than " + TarConstants.MAXSIZE + " bytes.");
                }
                tarEntry.setSize(resource.getSize());
                if (tarFileSet != null && tarFileSet.hasFileModeBeenSet()) {
                    tarEntry.setMode(tarFileSet.getMode());
                }
            }
            if (tarFileSet != null) {
                if (tarFileSet.hasUserNameBeenSet()) {
                    tarEntry.setUserName(tarFileSet.getUserName());
                }
                if (tarFileSet.hasGroupBeenSet()) {
                    tarEntry.setGroupName(tarFileSet.getGroup());
                }
                if (tarFileSet.hasUserIdBeenSet()) {
                    tarEntry.setUserId(tarFileSet.getUid());
                }
                if (tarFileSet.hasGroupIdBeenSet()) {
                    tarEntry.setGroupId(tarFileSet.getGid());
                }
            }
            InputStream inputStream = null;
            try {
                tarOutputStream.putNextEntry(tarEntry);
                if (!resource.isDirectory()) {
                    inputStream = resource.getInputStream();
                    byte[] bArr = new byte[8192];
                    int i = 0;
                    do {
                        tarOutputStream.write(bArr, 0, i);
                        i = inputStream.read(bArr, 0, 8192);
                    } while (i != -1);
                }
                tarOutputStream.closeEntry();
            } finally {
                FileUtils.close(inputStream);
            }
        }
    }

    protected boolean archiveIsUpToDate(String[] strArr) {
        return archiveIsUpToDate(strArr, this.baseDir);
    }

    protected boolean archiveIsUpToDate(String[] strArr, File file) {
        SourceFileScanner sourceFileScanner = new SourceFileScanner(this);
        MergingMapper mergingMapper = new MergingMapper();
        mergingMapper.setTo(this.tarFile.getAbsolutePath());
        return sourceFileScanner.restrict(strArr, file, null, mergingMapper).length == 0;
    }

    protected boolean archiveIsUpToDate(Resource resource) {
        return SelectorUtils.isOutOfDate(new FileResource(this.tarFile), resource, FileUtils.getFileUtils().getFileTimestampGranularity());
    }

    protected boolean supportsNonFileResources() {
        return getClass().equals(Tar.class);
    }

    protected boolean check(ResourceCollection resourceCollection) {
        if (isFileFileSet(resourceCollection)) {
            FileSet fileSet = (FileSet) resourceCollection;
            return check(fileSet.getDir(getProject()), getFileNames(fileSet));
        }
        if (!resourceCollection.isFilesystemOnly() && !supportsNonFileResources()) {
            throw new BuildException("only filesystem resources are supported");
        }
        boolean zArchiveIsUpToDate = true;
        if (resourceCollection.isFilesystemOnly()) {
            HashSet<File> hashSet = new HashSet();
            HashMap map = new HashMap();
            Iterator<Resource> it = resourceCollection.iterator();
            while (it.hasNext()) {
                FileResource fileResourceAsFileResource = ResourceUtils.asFileResource((FileProvider) it.next().as(FileProvider.class));
                File baseDir = fileResourceAsFileResource.getBaseDir();
                if (baseDir == null) {
                    baseDir = Copy.NULL_FILE_PLACEHOLDER;
                }
                hashSet.add(baseDir);
                Vector vector = (Vector) map.get(baseDir);
                if (vector == null) {
                    vector = new Vector();
                    map.put(baseDir, vector);
                }
                if (baseDir == Copy.NULL_FILE_PLACEHOLDER) {
                    vector.add(fileResourceAsFileResource.getFile().getAbsolutePath());
                } else {
                    vector.add(fileResourceAsFileResource.getName());
                }
            }
            for (File file : hashSet) {
                Vector vector2 = (Vector) map.get(file);
                String[] strArr = (String[]) vector2.toArray(new String[vector2.size()]);
                if (file == Copy.NULL_FILE_PLACEHOLDER) {
                    file = null;
                }
                zArchiveIsUpToDate &= check(file, strArr);
            }
        } else {
            Iterator<Resource> it2 = resourceCollection.iterator();
            while (zArchiveIsUpToDate && it2.hasNext()) {
                zArchiveIsUpToDate = archiveIsUpToDate(it2.next());
            }
        }
        return zArchiveIsUpToDate;
    }

    protected boolean check(File file, String[] strArr) {
        boolean zArchiveIsUpToDate = archiveIsUpToDate(strArr, file);
        for (String str : strArr) {
            if (this.tarFile.equals(new File(file, str))) {
                throw new BuildException("A tar file cannot include itself", getLocation());
            }
        }
        return zArchiveIsUpToDate;
    }

    protected void tar(ResourceCollection resourceCollection, TarOutputStream tarOutputStream) throws IOException {
        ArchiveFileSet archiveFileSet = resourceCollection instanceof ArchiveFileSet ? (ArchiveFileSet) resourceCollection : null;
        if (archiveFileSet != null && archiveFileSet.size() > 1 && archiveFileSet.getFullpath(getProject()).length() > 0) {
            throw new BuildException("fullpath attribute may only be specified for filesets that specify a single file.");
        }
        TarFileSet tarFileSetAsTarFileSet = asTarFileSet(archiveFileSet);
        if (isFileFileSet(resourceCollection)) {
            FileSet fileSet = (FileSet) resourceCollection;
            String[] fileNames = getFileNames(fileSet);
            for (int i = 0; i < fileNames.length; i++) {
                tarFile(new File(fileSet.getDir(getProject()), fileNames[i]), tarOutputStream, fileNames[i].replace(File.separatorChar, '/'), tarFileSetAsTarFileSet);
            }
            return;
        }
        if (resourceCollection.isFilesystemOnly()) {
            Iterator<Resource> it = resourceCollection.iterator();
            while (it.hasNext()) {
                File file = ((FileProvider) it.next().as(FileProvider.class)).getFile();
                tarFile(file, tarOutputStream, file.getName(), tarFileSetAsTarFileSet);
            }
            return;
        }
        for (Resource resource : resourceCollection) {
            tarResource(resource, tarOutputStream, resource.getName(), tarFileSetAsTarFileSet);
        }
    }

    protected static boolean isFileFileSet(ResourceCollection resourceCollection) {
        return (resourceCollection instanceof FileSet) && resourceCollection.isFilesystemOnly();
    }

    protected static String[] getFileNames(FileSet fileSet) {
        DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(fileSet.getProject());
        String[] includedDirectories = directoryScanner.getIncludedDirectories();
        String[] includedFiles = directoryScanner.getIncludedFiles();
        String[] strArr = new String[includedDirectories.length + includedFiles.length];
        System.arraycopy(includedDirectories, 0, strArr, 0, includedDirectories.length);
        System.arraycopy(includedFiles, 0, strArr, includedDirectories.length, includedFiles.length);
        return strArr;
    }

    protected TarFileSet asTarFileSet(ArchiveFileSet archiveFileSet) {
        if (archiveFileSet != null && (archiveFileSet instanceof TarFileSet)) {
            return (TarFileSet) archiveFileSet;
        }
        TarFileSet tarFileSet = new TarFileSet();
        tarFileSet.setProject(getProject());
        if (archiveFileSet != null) {
            tarFileSet.setPrefix(archiveFileSet.getPrefix(getProject()));
            tarFileSet.setFullpath(archiveFileSet.getFullpath(getProject()));
            if (archiveFileSet.hasFileModeBeenSet()) {
                tarFileSet.integerSetFileMode(archiveFileSet.getFileMode(getProject()));
            }
            if (archiveFileSet.hasDirModeBeenSet()) {
                tarFileSet.integerSetDirMode(archiveFileSet.getDirMode(getProject()));
            }
            if (archiveFileSet instanceof org.apache.tools.ant.types.TarFileSet) {
                org.apache.tools.ant.types.TarFileSet tarFileSet2 = (org.apache.tools.ant.types.TarFileSet) archiveFileSet;
                if (tarFileSet2.hasUserNameBeenSet()) {
                    tarFileSet.setUserName(tarFileSet2.getUserName());
                }
                if (tarFileSet2.hasGroupBeenSet()) {
                    tarFileSet.setGroup(tarFileSet2.getGroup());
                }
                if (tarFileSet2.hasUserIdBeenSet()) {
                    tarFileSet.setUid(tarFileSet2.getUid());
                }
                if (tarFileSet2.hasGroupIdBeenSet()) {
                    tarFileSet.setGid(tarFileSet2.getGid());
                }
            }
        }
        return tarFileSet;
    }

    public static class TarFileSet extends org.apache.tools.ant.types.TarFileSet {
        private String[] files;
        private boolean preserveLeadingSlashes;

        public TarFileSet(FileSet fileSet) {
            super(fileSet);
            this.files = null;
            this.preserveLeadingSlashes = false;
        }

        public TarFileSet() {
            this.files = null;
            this.preserveLeadingSlashes = false;
        }

        public String[] getFiles(Project project) {
            if (this.files == null) {
                this.files = Tar.getFileNames(this);
            }
            return this.files;
        }

        public void setMode(String str) {
            setFileMode(str);
        }

        public int getMode() {
            return getFileMode(getProject());
        }

        public void setPreserveLeadingSlashes(boolean z) {
            this.preserveLeadingSlashes = z;
        }

        public boolean getPreserveLeadingSlashes() {
            return this.preserveLeadingSlashes;
        }
    }

    public static class TarLongFileMode extends EnumeratedAttribute {
        public static final String FAIL = "fail";
        public static final String GNU = "gnu";
        public static final String OMIT = "omit";
        public static final String POSIX = "posix";
        public static final String TRUNCATE = "truncate";
        public static final String WARN = "warn";
        private final String[] validModes = {"warn", "fail", "truncate", "gnu", POSIX, "omit"};

        public TarLongFileMode() {
            setValue("warn");
        }

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return this.validModes;
        }

        public boolean isTruncateMode() {
            return "truncate".equalsIgnoreCase(getValue());
        }

        public boolean isWarnMode() {
            return "warn".equalsIgnoreCase(getValue());
        }

        public boolean isGnuMode() {
            return "gnu".equalsIgnoreCase(getValue());
        }

        public boolean isFailMode() {
            return "fail".equalsIgnoreCase(getValue());
        }

        public boolean isOmitMode() {
            return "omit".equalsIgnoreCase(getValue());
        }

        public boolean isPosixMode() {
            return POSIX.equalsIgnoreCase(getValue());
        }
    }

    public static final class TarCompressionMethod extends EnumeratedAttribute {
        private static final String BZIP2 = "bzip2";
        private static final String GZIP = "gzip";
        private static final String NONE = "none";

        public TarCompressionMethod() {
            setValue("none");
        }

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"none", GZIP, BZIP2};
        }

        /* JADX INFO: Access modifiers changed from: private */
        public OutputStream compress(OutputStream outputStream) throws IOException {
            String value = getValue();
            if (GZIP.equals(value)) {
                return new GZIPOutputStream(outputStream);
            }
            if (!BZIP2.equals(value)) {
                return outputStream;
            }
            outputStream.write(66);
            outputStream.write(90);
            return new CBZip2OutputStream(outputStream);
        }
    }
}
