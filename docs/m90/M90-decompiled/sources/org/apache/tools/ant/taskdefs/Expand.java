package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

/* JADX INFO: loaded from: classes3.dex */
public class Expand extends Task {
    private static final int BUFFER_SIZE = 1024;
    public static final String ERROR_MULTIPLE_MAPPERS = "Cannot define more than one mapper";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    public static final String NATIVE_ENCODING = "native-encoding";
    private File dest;
    private File source;
    private boolean overwrite = true;
    private Mapper mapperElement = null;
    private Vector<PatternSet> patternsets = new Vector<>();
    private Union resources = new Union();
    private boolean resourcesSpecified = false;
    private boolean failOnEmptyArchive = false;
    private boolean stripAbsolutePathSpec = false;
    private boolean scanForUnicodeExtraFields = true;
    private String encoding = "UTF8";

    public void setFailOnEmptyArchive(boolean z) {
        this.failOnEmptyArchive = z;
    }

    public boolean getFailOnEmptyArchive() {
        return this.failOnEmptyArchive;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        if ("expand".equals(getTaskType())) {
            log("!! expand is deprecated. Use unzip instead. !!");
        }
        if (this.source == null && !this.resourcesSpecified) {
            throw new BuildException("src attribute and/or resources must be specified");
        }
        File file = this.dest;
        if (file == null) {
            throw new BuildException("Dest attribute must be specified");
        }
        if (file.exists() && !this.dest.isDirectory()) {
            throw new BuildException("Dest must be a directory.", getLocation());
        }
        File file2 = this.source;
        if (file2 != null) {
            if (file2.isDirectory()) {
                throw new BuildException("Src must not be a directory. Use nested filesets instead.", getLocation());
            }
            if (!this.source.exists()) {
                throw new BuildException("src '" + this.source + "' doesn't exist.");
            }
            if (!this.source.canRead()) {
                throw new BuildException("src '" + this.source + "' cannot be read.");
            }
            expandFile(FILE_UTILS, this.source, this.dest);
        }
        for (Resource resource : this.resources) {
            if (!resource.isExists()) {
                log("Skipping '" + resource.getName() + "' because it doesn't exist.");
            } else {
                FileProvider fileProvider = (FileProvider) resource.as(FileProvider.class);
                if (fileProvider != null) {
                    expandFile(FILE_UTILS, fileProvider.getFile(), this.dest);
                } else {
                    expandResource(resource, this.dest);
                }
            }
        }
    }

    protected void expandFile(FileUtils fileUtils, File file, File file2) throws Throwable {
        ZipFile zipFile;
        boolean z;
        log("Expanding: " + file + " into " + file2, 2);
        FileNameMapper mapper = getMapper();
        if (!file.exists()) {
            throw new BuildException("Unable to expand " + file + " as the file does not exist", getLocation());
        }
        ZipFile zipFile2 = null;
        InputStream inputStream = null;
        try {
            try {
                zipFile = new ZipFile(file, this.encoding, this.scanForUnicodeExtraFields);
                z = true;
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException e) {
            e = e;
        }
        try {
            Enumeration<ZipEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntryNextElement = entries.nextElement();
                log("extracting " + zipEntryNextElement.getName(), 4);
                try {
                    InputStream inputStream2 = zipFile.getInputStream(zipEntryNextElement);
                    try {
                        extractFile(fileUtils, file, file2, inputStream2, zipEntryNextElement.getName(), new Date(zipEntryNextElement.getTime()), zipEntryNextElement.isDirectory(), mapper);
                        FileUtils.close(inputStream2);
                        z = false;
                    } catch (Throwable th2) {
                        th = th2;
                        inputStream = inputStream2;
                        FileUtils.close(inputStream);
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                }
            }
            if (z && getFailOnEmptyArchive()) {
                throw new BuildException("archive '" + file + "' is empty");
            }
            log("expand complete", 3);
            ZipFile.closeQuietly(zipFile);
        } catch (IOException e2) {
            e = e2;
            throw new BuildException("Error while expanding " + file.getPath() + "\n" + e.toString(), e);
        } catch (Throwable th4) {
            th = th4;
            zipFile2 = zipFile;
            ZipFile.closeQuietly(zipFile2);
            throw th;
        }
    }

    protected void expandResource(Resource resource, File file) {
        throw new BuildException("only filesystem based resources are supported by this task.");
    }

    protected FileNameMapper getMapper() {
        Mapper mapper = this.mapperElement;
        if (mapper != null) {
            return mapper.getImplementation();
        }
        return new IdentityMapper();
    }

    protected void extractFile(FileUtils fileUtils, File file, File file2, InputStream inputStream, String str, Date date, boolean z, FileNameMapper fileNameMapper) throws Throwable {
        String[] strArr;
        char c;
        String strSubstring = str;
        if (this.stripAbsolutePathSpec && str.length() > 0 && (strSubstring.charAt(0) == File.separatorChar || strSubstring.charAt(0) == '/' || strSubstring.charAt(0) == '\\')) {
            log("stripped absolute path spec from " + strSubstring, 3);
            strSubstring = strSubstring.substring(1);
        }
        Vector<PatternSet> vector = this.patternsets;
        if (vector != null && vector.size() > 0) {
            String strReplace = strSubstring.replace('/', File.separatorChar).replace('\\', File.separatorChar);
            HashSet hashSet = new HashSet();
            HashSet hashSet2 = new HashSet();
            int size = this.patternsets.size();
            for (int i = 0; i < size; i++) {
                PatternSet patternSetElementAt = this.patternsets.elementAt(i);
                String[] includePatterns = patternSetElementAt.getIncludePatterns(getProject());
                if (includePatterns == null || includePatterns.length == 0) {
                    includePatterns = new String[]{SelectorUtils.DEEP_TREE_MATCH};
                }
                for (String str2 : includePatterns) {
                    String strReplace2 = str2.replace('/', File.separatorChar).replace('\\', File.separatorChar);
                    if (strReplace2.endsWith(File.separator)) {
                        strReplace2 = strReplace2 + SelectorUtils.DEEP_TREE_MATCH;
                    }
                    hashSet.add(strReplace2);
                }
                String[] excludePatterns = patternSetElementAt.getExcludePatterns(getProject());
                if (excludePatterns != null) {
                    for (String str3 : excludePatterns) {
                        String strReplace3 = str3.replace('/', File.separatorChar).replace('\\', File.separatorChar);
                        if (strReplace3.endsWith(File.separator)) {
                            strReplace3 = strReplace3 + SelectorUtils.DEEP_TREE_MATCH;
                        }
                        hashSet2.add(strReplace3);
                    }
                }
            }
            Iterator it = hashSet.iterator();
            boolean zMatchPath = false;
            while (!zMatchPath && it.hasNext()) {
                zMatchPath = SelectorUtils.matchPath((String) it.next(), strReplace);
            }
            Iterator it2 = hashSet2.iterator();
            while (zMatchPath && it2.hasNext()) {
                zMatchPath = !SelectorUtils.matchPath((String) it2.next(), strReplace);
            }
            if (!zMatchPath) {
                log("skipping " + strSubstring + " as it is excluded or not included.", 3);
                return;
            }
        }
        String[] strArrMapFileName = fileNameMapper.mapFileName(strSubstring);
        if (strArrMapFileName == null || strArrMapFileName.length == 0) {
            c = 0;
            strArr = new String[]{strSubstring};
        } else {
            strArr = strArrMapFileName;
            c = 0;
        }
        File fileResolveFile = fileUtils.resolveFile(file2, strArr[c]);
        try {
            if (!this.overwrite && fileResolveFile.exists() && fileResolveFile.lastModified() >= date.getTime()) {
                log("Skipping " + fileResolveFile + " as it is up-to-date", 4);
                return;
            }
            log("expanding " + strSubstring + " to " + fileResolveFile, 3);
            File parentFile = fileResolveFile.getParentFile();
            if (parentFile != null) {
                parentFile.mkdirs();
            }
            if (z) {
                fileResolveFile.mkdirs();
            } else {
                byte[] bArr = new byte[1024];
                FileOutputStream fileOutputStream = null;
                try {
                    FileOutputStream fileOutputStream2 = new FileOutputStream(fileResolveFile);
                    while (true) {
                        try {
                            int i2 = inputStream.read(bArr);
                            if (i2 < 0) {
                                break;
                            } else {
                                fileOutputStream2.write(bArr, 0, i2);
                            }
                        } catch (Throwable th) {
                            th = th;
                            fileOutputStream = fileOutputStream2;
                            FileUtils.close(fileOutputStream);
                            throw th;
                        }
                    }
                    fileOutputStream2.close();
                    FileUtils.close((OutputStream) null);
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            fileUtils.setFileLastModified(fileResolveFile, date.getTime());
        } catch (FileNotFoundException e) {
            log("Unable to expand to file " + fileResolveFile.getPath(), e, 1);
        }
    }

    public void setDest(File file) {
        this.dest = file;
    }

    public void setSrc(File file) {
        this.source = file;
    }

    public void setOverwrite(boolean z) {
        this.overwrite = z;
    }

    public void addPatternset(PatternSet patternSet) {
        this.patternsets.addElement(patternSet);
    }

    public void addFileset(FileSet fileSet) {
        add(fileSet);
    }

    public void add(ResourceCollection resourceCollection) {
        this.resourcesSpecified = true;
        this.resources.add(resourceCollection);
    }

    public Mapper createMapper() throws BuildException {
        if (this.mapperElement != null) {
            throw new BuildException(ERROR_MULTIPLE_MAPPERS, getLocation());
        }
        Mapper mapper = new Mapper(getProject());
        this.mapperElement = mapper;
        return mapper;
    }

    public void add(FileNameMapper fileNameMapper) {
        createMapper().add(fileNameMapper);
    }

    public void setEncoding(String str) {
        internalSetEncoding(str);
    }

    protected void internalSetEncoding(String str) {
        if (NATIVE_ENCODING.equals(str)) {
            str = null;
        }
        this.encoding = str;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setStripAbsolutePathSpec(boolean z) {
        this.stripAbsolutePathSpec = z;
    }

    public void setScanForUnicodeExtraFields(boolean z) {
        internalSetScanForUnicodeExtraFields(z);
    }

    protected void internalSetScanForUnicodeExtraFields(boolean z) {
        this.scanForUnicodeExtraFields = z;
    }

    public boolean getScanForUnicodeExtraFields() {
        return this.scanForUnicodeExtraFields;
    }
}
