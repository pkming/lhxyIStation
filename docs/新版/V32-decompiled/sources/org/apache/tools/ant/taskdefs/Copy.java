package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.ResourceFactory;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.FlatFileNameMapper;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.ant.util.LinkedHashtable;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.util.SourceFileScanner;

/* JADX INFO: loaded from: classes3.dex */
public class Copy extends Task {
    private static final String MSG_WHEN_COPYING_EMPTY_RC_TO_FILE = "Cannot perform operation from directory to file.";
    protected Hashtable<File, File> completeDirMap;
    protected Hashtable<String, String[]> dirCopyMap;
    private boolean enableMultipleMappings;
    protected boolean failonerror;
    protected Hashtable<String, String[]> fileCopyMap;
    protected FileUtils fileUtils;
    protected Vector<ResourceCollection> filesets;
    private Vector<FilterChain> filterChains;
    private Vector<FilterSet> filterSets;
    protected boolean filtering;
    protected boolean flatten;
    private boolean force;
    protected boolean forceOverwrite;
    private long granularity;
    protected boolean includeEmpty;
    private String inputEncoding;
    protected Mapper mapperElement;
    private String outputEncoding;
    protected boolean preserveLastModified;
    private boolean quiet;
    protected Vector<ResourceCollection> rcs;
    private Resource singleResource;
    protected int verbosity;
    static final File NULL_FILE_PLACEHOLDER = new File("/NULL_FILE");
    static final String LINE_SEPARATOR = System.getProperty("line.separator");
    protected File file = null;
    protected File destFile = null;
    protected File destDir = null;

    public Copy() {
        Vector<ResourceCollection> vector = new Vector<>();
        this.rcs = vector;
        this.filesets = vector;
        this.enableMultipleMappings = false;
        this.filtering = false;
        this.preserveLastModified = false;
        this.forceOverwrite = false;
        this.flatten = false;
        this.verbosity = 3;
        this.includeEmpty = true;
        this.failonerror = true;
        this.fileCopyMap = new LinkedHashtable();
        this.dirCopyMap = new LinkedHashtable();
        this.completeDirMap = new LinkedHashtable();
        this.mapperElement = null;
        this.filterChains = new Vector<>();
        this.filterSets = new Vector<>();
        this.inputEncoding = null;
        this.outputEncoding = null;
        this.granularity = 0L;
        this.force = false;
        this.quiet = false;
        this.singleResource = null;
        FileUtils fileUtils = FileUtils.getFileUtils();
        this.fileUtils = fileUtils;
        this.granularity = fileUtils.getFileTimestampGranularity();
    }

    protected FileUtils getFileUtils() {
        return this.fileUtils;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setTofile(File file) {
        this.destFile = file;
    }

    public void setTodir(File file) {
        this.destDir = file;
    }

    public FilterChain createFilterChain() {
        FilterChain filterChain = new FilterChain();
        this.filterChains.addElement(filterChain);
        return filterChain;
    }

    public FilterSet createFilterSet() {
        FilterSet filterSet = new FilterSet();
        this.filterSets.addElement(filterSet);
        return filterSet;
    }

    public void setPreserveLastModified(String str) {
        setPreserveLastModified(Project.toBoolean(str));
    }

    public void setPreserveLastModified(boolean z) {
        this.preserveLastModified = z;
    }

    public boolean getPreserveLastModified() {
        return this.preserveLastModified;
    }

    protected Vector<FilterSet> getFilterSets() {
        return this.filterSets;
    }

    protected Vector<FilterChain> getFilterChains() {
        return this.filterChains;
    }

    public void setFiltering(boolean z) {
        this.filtering = z;
    }

    public void setOverwrite(boolean z) {
        this.forceOverwrite = z;
    }

    public void setForce(boolean z) {
        this.force = z;
    }

    public boolean getForce() {
        return this.force;
    }

    public void setFlatten(boolean z) {
        this.flatten = z;
    }

    public void setVerbose(boolean z) {
        this.verbosity = z ? 2 : 3;
    }

    public void setIncludeEmptyDirs(boolean z) {
        this.includeEmpty = z;
    }

    public void setQuiet(boolean z) {
        this.quiet = z;
    }

    public void setEnableMultipleMappings(boolean z) {
        this.enableMultipleMappings = z;
    }

    public boolean isEnableMultipleMapping() {
        return this.enableMultipleMappings;
    }

    public void setFailOnError(boolean z) {
        this.failonerror = z;
    }

    public void addFileset(FileSet fileSet) {
        add(fileSet);
    }

    public void add(ResourceCollection resourceCollection) {
        this.rcs.add(resourceCollection);
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

    public void setEncoding(String str) {
        this.inputEncoding = str;
        if (this.outputEncoding == null) {
            this.outputEncoding = str;
        }
    }

    public String getEncoding() {
        return this.inputEncoding;
    }

    public void setOutputEncoding(String str) {
        this.outputEncoding = str;
    }

    public String getOutputEncoding() {
        return this.outputEncoding;
    }

    public void setGranularity(long j) {
        this.granularity = j;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        ResourceCollection resourceCollection;
        ResourceCollection resourceCollection2;
        int i;
        File file = this.file;
        File file2 = this.destFile;
        File file3 = this.destDir;
        ResourceCollection resourceCollectionElementAt = (file == null && file2 != null && this.rcs.size() == 1) ? this.rcs.elementAt(0) : null;
        try {
            try {
                try {
                    validateAttributes();
                    copySingleFile();
                    HashMap<File, List<String>> map = new HashMap<>();
                    HashMap<File, List<String>> map2 = new HashMap<>();
                    HashSet<File> hashSet = new HashSet<>();
                    ArrayList arrayList = new ArrayList();
                    int size = this.rcs.size();
                    int i2 = 0;
                    while (i2 < size) {
                        ResourceCollection resourceCollectionElementAt2 = this.rcs.elementAt(i2);
                        if ((resourceCollectionElementAt2 instanceof FileSet) && resourceCollectionElementAt2.isFilesystemOnly()) {
                            FileSet fileSet = (FileSet) resourceCollectionElementAt2;
                            try {
                                DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(getProject());
                                File dir = fileSet.getDir(getProject());
                                String[] includedFiles = directoryScanner.getIncludedFiles();
                                i = size;
                                String[] includedDirectories = directoryScanner.getIncludedDirectories();
                                resourceCollection2 = resourceCollectionElementAt;
                                try {
                                    if (!this.flatten && this.mapperElement == null && directoryScanner.isEverythingIncluded() && !fileSet.hasPatterns()) {
                                        this.completeDirMap.put(dir, this.destDir);
                                    }
                                    add(dir, includedFiles, map);
                                    add(dir, includedDirectories, map2);
                                    hashSet.add(dir);
                                } catch (Throwable th) {
                                    th = th;
                                    resourceCollection = resourceCollection2;
                                    this.singleResource = null;
                                    this.file = file;
                                    this.destFile = file2;
                                    this.destDir = file3;
                                    if (resourceCollection != null) {
                                        this.rcs.insertElementAt(resourceCollection, 0);
                                    }
                                    this.fileCopyMap.clear();
                                    this.dirCopyMap.clear();
                                    this.completeDirMap.clear();
                                    throw th;
                                }
                            } catch (BuildException e) {
                                resourceCollection2 = resourceCollectionElementAt;
                                i = size;
                                if (this.failonerror || !getMessage(e).endsWith(DirectoryScanner.DOES_NOT_EXIST_POSTFIX)) {
                                    throw e;
                                }
                                if (!this.quiet) {
                                    log("Warning: " + getMessage(e), 0);
                                }
                            }
                        } else {
                            resourceCollection2 = resourceCollectionElementAt;
                            i = size;
                            if (!resourceCollectionElementAt2.isFilesystemOnly() && !supportsNonFileResources()) {
                                throw new BuildException("Only FileSystem resources are supported.");
                            }
                            for (Resource resource : resourceCollectionElementAt2) {
                                if (!resource.isExists()) {
                                    String str = "Warning: Could not find resource " + resource.toLongString() + " to copy.";
                                    if (!this.failonerror) {
                                        if (!this.quiet) {
                                            log(str, 0);
                                        }
                                    } else {
                                        throw new BuildException(str);
                                    }
                                } else {
                                    File file4 = NULL_FILE_PLACEHOLDER;
                                    String name = resource.getName();
                                    FileProvider fileProvider = (FileProvider) resource.as(FileProvider.class);
                                    if (fileProvider != null) {
                                        FileResource fileResourceAsFileResource = ResourceUtils.asFileResource(fileProvider);
                                        File keyFile = getKeyFile(fileResourceAsFileResource.getBaseDir());
                                        if (fileResourceAsFileResource.getBaseDir() == null) {
                                            name = fileResourceAsFileResource.getFile().getAbsolutePath();
                                        }
                                        file4 = keyFile;
                                    }
                                    if (resource.isDirectory() || fileProvider != null) {
                                        add(file4, name, resource.isDirectory() ? map2 : map);
                                        hashSet.add(file4);
                                    } else {
                                        arrayList.add(resource);
                                    }
                                }
                            }
                        }
                        i2++;
                        size = i;
                        resourceCollectionElementAt = resourceCollection2;
                    }
                    resourceCollection2 = resourceCollectionElementAt;
                    iterateOverBaseDirs(hashSet, map2, map);
                    try {
                        doFileOperations();
                    } catch (BuildException e2) {
                        if (!this.failonerror) {
                            if (!this.quiet) {
                                log("Warning: " + getMessage(e2), 0);
                            }
                        } else {
                            throw e2;
                        }
                    }
                    if (arrayList.size() > 0 || this.singleResource != null) {
                        Map<Resource, String[]> mapScan = scan((Resource[]) arrayList.toArray(new Resource[arrayList.size()]), this.destDir);
                        Resource resource2 = this.singleResource;
                        if (resource2 != null) {
                            mapScan.put(resource2, new String[]{this.destFile.getAbsolutePath()});
                        }
                        try {
                            doResourceOperations(mapScan);
                        } catch (BuildException e3) {
                            if (!this.failonerror) {
                                if (!this.quiet) {
                                    log("Warning: " + getMessage(e3), 0);
                                }
                            } else {
                                throw e3;
                            }
                        }
                    }
                    this.singleResource = null;
                    this.file = file;
                    this.destFile = file2;
                    this.destDir = file3;
                    if (resourceCollection2 != null) {
                        this.rcs.insertElementAt(resourceCollection2, 0);
                    }
                    this.fileCopyMap.clear();
                    this.dirCopyMap.clear();
                    this.completeDirMap.clear();
                } catch (Throwable th2) {
                    th = th2;
                    resourceCollection = resourceCollectionElementAt;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (BuildException e4) {
            ResourceCollection resourceCollection3 = resourceCollectionElementAt;
            if (this.failonerror || !getMessage(e4).equals(MSG_WHEN_COPYING_EMPTY_RC_TO_FILE)) {
                throw e4;
            }
            log("Warning: " + getMessage(e4), 0);
            this.singleResource = null;
            this.file = file;
            this.destFile = file2;
            this.destDir = file3;
            if (resourceCollection3 != null) {
                this.rcs.insertElementAt(resourceCollection3, 0);
            }
            this.fileCopyMap.clear();
            this.dirCopyMap.clear();
            this.completeDirMap.clear();
        }
    }

    private void copySingleFile() {
        File file = this.file;
        if (file != null) {
            if (file.exists()) {
                if (this.destFile == null) {
                    this.destFile = new File(this.destDir, this.file.getName());
                }
                if (this.forceOverwrite || !this.destFile.exists() || this.file.lastModified() - this.granularity > this.destFile.lastModified()) {
                    this.fileCopyMap.put(this.file.getAbsolutePath(), new String[]{this.destFile.getAbsolutePath()});
                    return;
                } else {
                    log(this.file + " omitted as " + this.destFile + " is up to date.", 3);
                    return;
                }
            }
            String str = "Warning: Could not find file " + this.file.getAbsolutePath() + " to copy.";
            if (!this.failonerror) {
                if (this.quiet) {
                    return;
                }
                log(str, 0);
                return;
            }
            throw new BuildException(str);
        }
    }

    private void iterateOverBaseDirs(HashSet<File> hashSet, HashMap<File, List<String>> map, HashMap<File, List<String>> map2) {
        for (File file : hashSet) {
            List<String> list = map2.get(file);
            List<String> list2 = map.get(file);
            String[] strArr = new String[0];
            if (list != null) {
                strArr = (String[]) list.toArray(strArr);
            }
            String[] strArr2 = new String[0];
            if (list2 != null) {
                strArr2 = (String[]) list2.toArray(strArr2);
            }
            if (file == NULL_FILE_PLACEHOLDER) {
                file = null;
            }
            scan(file, this.destDir, strArr, strArr2);
        }
    }

    protected void validateAttributes() throws BuildException {
        if (this.file == null && this.rcs.size() == 0) {
            throw new BuildException("Specify at least one source--a file or a resource collection.");
        }
        File file = this.destFile;
        if (file != null && this.destDir != null) {
            throw new BuildException("Only one of tofile and todir may be set.");
        }
        if (file == null && this.destDir == null) {
            throw new BuildException("One of tofile or todir must be set.");
        }
        File file2 = this.file;
        if (file2 != null && file2.isDirectory()) {
            throw new BuildException("Use a resource collection to copy directories.");
        }
        if (this.destFile != null && this.rcs.size() > 0) {
            if (this.rcs.size() > 1) {
                throw new BuildException("Cannot concatenate multiple files into a single file.");
            }
            ResourceCollection resourceCollectionElementAt = this.rcs.elementAt(0);
            if (!resourceCollectionElementAt.isFilesystemOnly() && !supportsNonFileResources()) {
                throw new BuildException("Only FileSystem resources are supported.");
            }
            if (resourceCollectionElementAt.size() == 0) {
                throw new BuildException(MSG_WHEN_COPYING_EMPTY_RC_TO_FILE);
            }
            if (resourceCollectionElementAt.size() == 1) {
                Resource next = resourceCollectionElementAt.iterator().next();
                FileProvider fileProvider = (FileProvider) next.as(FileProvider.class);
                if (this.file == null) {
                    if (fileProvider != null) {
                        this.file = fileProvider.getFile();
                    } else {
                        this.singleResource = next;
                    }
                    this.rcs.removeElementAt(0);
                } else {
                    throw new BuildException("Cannot concatenate multiple files into a single file.");
                }
            } else {
                throw new BuildException("Cannot concatenate multiple files into a single file.");
            }
        }
        File file3 = this.destFile;
        if (file3 != null) {
            this.destDir = file3.getParentFile();
        }
    }

    protected void scan(File file, File file2, String[] strArr, String[] strArr2) {
        FileNameMapper mapper = getMapper();
        buildMap(file, file2, strArr, mapper, this.fileCopyMap);
        if (this.includeEmpty) {
            buildMap(file, file2, strArr2, mapper, this.dirCopyMap);
        }
    }

    protected Map<Resource, String[]> scan(Resource[] resourceArr, File file) {
        return buildMap(resourceArr, file, getMapper());
    }

    protected void buildMap(File file, File file2, String[] strArr, FileNameMapper fileNameMapper, Hashtable<String, String[]> hashtable) {
        String[] strArrRestrict;
        if (this.forceOverwrite) {
            Vector vector = new Vector();
            for (int i = 0; i < strArr.length; i++) {
                if (fileNameMapper.mapFileName(strArr[i]) != null) {
                    vector.addElement(strArr[i]);
                }
            }
            strArrRestrict = new String[vector.size()];
            vector.copyInto(strArrRestrict);
        } else {
            strArrRestrict = new SourceFileScanner(this).restrict(strArr, file, file2, fileNameMapper, this.granularity);
        }
        for (int i2 = 0; i2 < strArrRestrict.length; i2++) {
            File file3 = new File(file, strArrRestrict[i2]);
            String[] strArrMapFileName = fileNameMapper.mapFileName(strArrRestrict[i2]);
            if (!this.enableMultipleMappings) {
                hashtable.put(file3.getAbsolutePath(), new String[]{new File(file2, strArrMapFileName[0]).getAbsolutePath()});
            } else {
                for (int i3 = 0; i3 < strArrMapFileName.length; i3++) {
                    strArrMapFileName[i3] = new File(file2, strArrMapFileName[i3]).getAbsolutePath();
                }
                hashtable.put(file3.getAbsolutePath(), strArrMapFileName);
            }
        }
    }

    protected Map<Resource, String[]> buildMap(Resource[] resourceArr, final File file, FileNameMapper fileNameMapper) {
        Resource[] resourceArrSelectOutOfDateSources;
        HashMap map = new HashMap();
        if (this.forceOverwrite) {
            Vector vector = new Vector();
            for (int i = 0; i < resourceArr.length; i++) {
                if (fileNameMapper.mapFileName(resourceArr[i].getName()) != null) {
                    vector.addElement(resourceArr[i]);
                }
            }
            resourceArrSelectOutOfDateSources = new Resource[vector.size()];
            vector.copyInto(resourceArrSelectOutOfDateSources);
        } else {
            resourceArrSelectOutOfDateSources = ResourceUtils.selectOutOfDateSources(this, resourceArr, fileNameMapper, new ResourceFactory() { // from class: org.apache.tools.ant.taskdefs.Copy.1
                @Override // org.apache.tools.ant.types.ResourceFactory
                public Resource getResource(String str) {
                    return new FileResource(file, str);
                }
            }, this.granularity);
        }
        for (int i2 = 0; i2 < resourceArrSelectOutOfDateSources.length; i2++) {
            String[] strArrMapFileName = fileNameMapper.mapFileName(resourceArrSelectOutOfDateSources[i2].getName());
            for (String str : strArrMapFileName) {
                if (str == null) {
                    throw new BuildException("Can't copy a resource without a name if the mapper doesn't provide one.");
                }
            }
            if (!this.enableMultipleMappings) {
                map.put(resourceArrSelectOutOfDateSources[i2], new String[]{new File(file, strArrMapFileName[0]).getAbsolutePath()});
            } else {
                for (int i3 = 0; i3 < strArrMapFileName.length; i3++) {
                    strArrMapFileName[i3] = new File(file, strArrMapFileName[i3]).getAbsolutePath();
                }
                map.put(resourceArrSelectOutOfDateSources[i2], strArrMapFileName);
            }
        }
        return map;
    }

    protected void doFileOperations() {
        String str;
        FilterSetCollection filterSetCollection;
        if (this.fileCopyMap.size() > 0) {
            String str2 = "Copying ";
            log("Copying " + this.fileCopyMap.size() + " file" + (this.fileCopyMap.size() == 1 ? "" : "s") + " to " + this.destDir.getAbsolutePath());
            for (Map.Entry<String, String[]> entry : this.fileCopyMap.entrySet()) {
                String key = entry.getKey();
                String[] value = entry.getValue();
                int i = 0;
                while (i < value.length) {
                    String str3 = value[i];
                    if (key.equals(str3)) {
                        log("Skipping self-copy of " + key, this.verbosity);
                        str = str2;
                    } else {
                        try {
                            log(str2 + key + " to " + str3, this.verbosity);
                            filterSetCollection = new FilterSetCollection();
                            if (this.filtering) {
                                filterSetCollection.addFilterSet(getProject().getGlobalFilterSet());
                            }
                            Iterator<FilterSet> it = this.filterSets.iterator();
                            while (it.hasNext()) {
                                filterSetCollection.addFilterSet(it.next());
                            }
                            str = str2;
                        } catch (IOException e) {
                            e = e;
                            str = str2;
                        }
                        try {
                            this.fileUtils.copyFile(new File(key), new File(str3), filterSetCollection, this.filterChains, this.forceOverwrite, this.preserveLastModified, false, this.inputEncoding, this.outputEncoding, getProject(), getForce());
                        } catch (IOException e2) {
                            e = e2;
                            String str4 = "Failed to copy " + key + " to " + str3 + " due to " + getDueTo(e);
                            File file = new File(str3);
                            if (!(e instanceof ResourceUtils.ReadOnlyTargetFileException) && file.exists() && !file.delete()) {
                                str4 = str4 + " and I couldn't delete the corrupt " + str3;
                            }
                            if (this.failonerror) {
                                throw new BuildException(str4, e, getLocation());
                            }
                            log(str4, 0);
                        }
                    }
                    i++;
                    str2 = str;
                }
            }
        }
        if (this.includeEmpty) {
            int i2 = 0;
            for (String[] strArr : this.dirCopyMap.values()) {
                int i3 = i2;
                for (String str5 : strArr) {
                    File file2 = new File(str5);
                    if (!file2.exists()) {
                        if (file2.mkdirs() || file2.isDirectory()) {
                            i3++;
                        } else {
                            log("Unable to create directory " + file2.getAbsolutePath(), 0);
                        }
                    }
                }
                i2 = i3;
            }
            if (i2 > 0) {
                log("Copied " + this.dirCopyMap.size() + " empty director" + (this.dirCopyMap.size() == 1 ? "y" : "ies") + " to " + i2 + " empty director" + (i2 != 1 ? "ies" : "y") + " under " + this.destDir.getAbsolutePath());
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:43:0x015e  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x016d A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void doResourceOperations(java.util.Map<org.apache.tools.ant.types.Resource, java.lang.String[]> r24) {
        /*
            Method dump skipped, instruction units count: 376
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.taskdefs.Copy.doResourceOperations(java.util.Map):void");
    }

    protected boolean supportsNonFileResources() {
        return getClass().equals(Copy.class);
    }

    private static void add(File file, String[] strArr, Map<File, List<String>> map) {
        if (strArr != null) {
            File keyFile = getKeyFile(file);
            List<String> arrayList = map.get(keyFile);
            if (arrayList == null) {
                arrayList = new ArrayList<>(strArr.length);
                map.put(keyFile, arrayList);
            }
            arrayList.addAll(Arrays.asList(strArr));
        }
    }

    private static void add(File file, String str, Map<File, List<String>> map) {
        if (str != null) {
            add(file, new String[]{str}, map);
        }
    }

    private static File getKeyFile(File file) {
        return file == null ? NULL_FILE_PLACEHOLDER : file;
    }

    private FileNameMapper getMapper() {
        Mapper mapper = this.mapperElement;
        if (mapper != null) {
            return mapper.getImplementation();
        }
        if (this.flatten) {
            return new FlatFileNameMapper();
        }
        return new IdentityMapper();
    }

    private String getMessage(Exception exc) {
        return exc.getMessage() == null ? exc.toString() : exc.getMessage();
    }

    private String getDueTo(Exception exc) {
        boolean z = exc.getClass() == IOException.class;
        StringBuffer stringBuffer = new StringBuffer();
        if (!z || exc.getMessage() == null) {
            stringBuffer.append(exc.getClass().getName());
        }
        if (exc.getMessage() != null) {
            if (!z) {
                stringBuffer.append(" ");
            }
            stringBuffer.append(exc.getMessage());
        }
        if (exc.getClass().getName().indexOf("MalformedInput") != -1) {
            String str = LINE_SEPARATOR;
            stringBuffer.append(str);
            stringBuffer.append("This is normally due to the input file containing invalid");
            stringBuffer.append(str);
            stringBuffer.append("bytes for the character encoding used : ");
            String defaultEncoding = this.inputEncoding;
            if (defaultEncoding == null) {
                defaultEncoding = this.fileUtils.getDefaultEncoding();
            }
            stringBuffer.append(defaultEncoding);
            stringBuffer.append(str);
        }
        return stringBuffer.toString();
    }
}
