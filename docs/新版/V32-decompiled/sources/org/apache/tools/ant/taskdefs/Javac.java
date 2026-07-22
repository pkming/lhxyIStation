package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.taskdefs.compilers.CompilerAdapter;
import org.apache.tools.ant.taskdefs.compilers.CompilerAdapterExtension;
import org.apache.tools.ant.taskdefs.compilers.CompilerAdapterFactory;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.GlobPatternMapper;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.SourceFileScanner;
import org.apache.tools.ant.util.facade.FacadeTaskHelper;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes3.dex */
public class Javac extends MatchingTask {
    private static final String CLASSIC = "classic";
    private static final String EXTJAVAC = "extJavac";
    private static final String FAIL_MSG = "Compile failed; see the compiler error output for details.";
    private static final String JAVAC11 = "javac1.1";
    private static final String JAVAC12 = "javac1.2";
    private static final String JAVAC13 = "javac1.3";
    private static final String JAVAC14 = "javac1.4";
    private static final String JAVAC15 = "javac1.5";
    private static final String JAVAC16 = "javac1.6";
    private static final String JAVAC17 = "javac1.7";
    private static final String JAVAC18 = "javac1.8";
    private static final String JAVAC19 = "javac1.9";
    private static final String MODERN = "modern";
    private Path bootclasspath;
    private Path compileClasspath;
    private Path compileSourcepath;
    private String debugLevel;
    private File destDir;
    private String encoding;
    private String errorProperty;
    private Path extdirs;
    private FacadeTaskHelper facade;
    private Boolean includeAntRuntime;
    private String memoryInitialSize;
    private String memoryMaximumSize;
    private String source;
    private Path src;
    private String targetAttribute;
    private File tmpDir;
    private String updatedProperty;
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final byte[] PACKAGE_INFO_CLASS_HEADER = {-54, -2, -70, -66, 0, 0, 0, TarConstants.LF_LINK, 0, 7, 7, 0, 5, 7, 0, 6, 1, 0, 10, TarConstants.LF_GNUTYPE_SPARSE, 111, 117, 114, 99, 101, 70, 105, 108, 101, 1, 0, 17, 112, 97, 99, 107, 97, TarConstants.LF_PAX_GLOBAL_EXTENDED_HEADER, 101, 45, 105, 110, 102, 111, 46, 106, 97, 118, 97, 1};
    private static final byte[] PACKAGE_INFO_CLASS_FOOTER = {47, 112, 97, 99, 107, 97, TarConstants.LF_PAX_GLOBAL_EXTENDED_HEADER, 101, 45, 105, 110, 102, 111, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, TarConstants.LF_PAX_GLOBAL_EXTENDED_HEADER, 47, 79, 98, 106, 101, 99, 116, 2, 0, 0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 0, 0, 0, 2, 0, 4};
    private boolean debug = false;
    private boolean optimize = false;
    private boolean deprecation = false;
    private boolean depend = false;
    private boolean verbose = false;
    private boolean includeJavaRuntime = false;
    private boolean fork = false;
    private String forkedExecutable = null;
    private boolean nowarn = false;
    protected boolean failOnError = true;
    protected boolean listFiles = false;
    protected File[] compileList = new File[0];
    private Map<String, Long> packageInfos = new HashMap();
    private boolean taskSuccess = true;
    private boolean includeDestClasses = true;
    private CompilerAdapter nestedAdapter = null;
    private boolean createMissingPackageInfoClass = true;

    public Javac() {
        this.facade = null;
        this.facade = new FacadeTaskHelper(assumedJavaVersion());
    }

    private String assumedJavaVersion() {
        return JavaEnvUtils.isJavaVersion("1.4") ? JAVAC14 : JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_5) ? JAVAC15 : JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_6) ? JAVAC16 : JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_7) ? JAVAC17 : JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_8) ? JAVAC18 : JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_9) ? JAVAC19 : CLASSIC;
    }

    public String getDebugLevel() {
        return this.debugLevel;
    }

    public void setDebugLevel(String str) {
        this.debugLevel = str;
    }

    public String getSource() {
        String str = this.source;
        return str != null ? str : getProject().getProperty(MagicNames.BUILD_JAVAC_SOURCE);
    }

    public void setSource(String str) {
        this.source = str;
    }

    public Path createSrc() {
        if (this.src == null) {
            this.src = new Path(getProject());
        }
        return this.src.createPath();
    }

    protected Path recreateSrc() {
        this.src = null;
        return createSrc();
    }

    public void setSrcdir(Path path) {
        Path path2 = this.src;
        if (path2 == null) {
            this.src = path;
        } else {
            path2.append(path);
        }
    }

    public Path getSrcdir() {
        return this.src;
    }

    public void setDestdir(File file) {
        this.destDir = file;
    }

    public File getDestdir() {
        return this.destDir;
    }

    public void setSourcepath(Path path) {
        Path path2 = this.compileSourcepath;
        if (path2 == null) {
            this.compileSourcepath = path;
        } else {
            path2.append(path);
        }
    }

    public Path getSourcepath() {
        return this.compileSourcepath;
    }

    public Path createSourcepath() {
        if (this.compileSourcepath == null) {
            this.compileSourcepath = new Path(getProject());
        }
        return this.compileSourcepath.createPath();
    }

    public void setSourcepathRef(Reference reference) {
        createSourcepath().setRefid(reference);
    }

    public void setClasspath(Path path) {
        Path path2 = this.compileClasspath;
        if (path2 == null) {
            this.compileClasspath = path;
        } else {
            path2.append(path);
        }
    }

    public Path getClasspath() {
        return this.compileClasspath;
    }

    public Path createClasspath() {
        if (this.compileClasspath == null) {
            this.compileClasspath = new Path(getProject());
        }
        return this.compileClasspath.createPath();
    }

    public void setClasspathRef(Reference reference) {
        createClasspath().setRefid(reference);
    }

    public void setBootclasspath(Path path) {
        Path path2 = this.bootclasspath;
        if (path2 == null) {
            this.bootclasspath = path;
        } else {
            path2.append(path);
        }
    }

    public Path getBootclasspath() {
        return this.bootclasspath;
    }

    public Path createBootclasspath() {
        if (this.bootclasspath == null) {
            this.bootclasspath = new Path(getProject());
        }
        return this.bootclasspath.createPath();
    }

    public void setBootClasspathRef(Reference reference) {
        createBootclasspath().setRefid(reference);
    }

    public void setExtdirs(Path path) {
        Path path2 = this.extdirs;
        if (path2 == null) {
            this.extdirs = path;
        } else {
            path2.append(path);
        }
    }

    public Path getExtdirs() {
        return this.extdirs;
    }

    public Path createExtdirs() {
        if (this.extdirs == null) {
            this.extdirs = new Path(getProject());
        }
        return this.extdirs.createPath();
    }

    public void setListfiles(boolean z) {
        this.listFiles = z;
    }

    public boolean getListfiles() {
        return this.listFiles;
    }

    public void setFailonerror(boolean z) {
        this.failOnError = z;
    }

    public void setProceed(boolean z) {
        this.failOnError = !z;
    }

    public boolean getFailonerror() {
        return this.failOnError;
    }

    public void setDeprecation(boolean z) {
        this.deprecation = z;
    }

    public boolean getDeprecation() {
        return this.deprecation;
    }

    public void setMemoryInitialSize(String str) {
        this.memoryInitialSize = str;
    }

    public String getMemoryInitialSize() {
        return this.memoryInitialSize;
    }

    public void setMemoryMaximumSize(String str) {
        this.memoryMaximumSize = str;
    }

    public String getMemoryMaximumSize() {
        return this.memoryMaximumSize;
    }

    public void setEncoding(String str) {
        this.encoding = str;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setDebug(boolean z) {
        this.debug = z;
    }

    public boolean getDebug() {
        return this.debug;
    }

    public void setOptimize(boolean z) {
        this.optimize = z;
    }

    public boolean getOptimize() {
        return this.optimize;
    }

    public void setDepend(boolean z) {
        this.depend = z;
    }

    public boolean getDepend() {
        return this.depend;
    }

    public void setVerbose(boolean z) {
        this.verbose = z;
    }

    public boolean getVerbose() {
        return this.verbose;
    }

    public void setTarget(String str) {
        this.targetAttribute = str;
    }

    public String getTarget() {
        String str = this.targetAttribute;
        return str != null ? str : getProject().getProperty(MagicNames.BUILD_JAVAC_TARGET);
    }

    public void setIncludeantruntime(boolean z) {
        this.includeAntRuntime = Boolean.valueOf(z);
    }

    public boolean getIncludeantruntime() {
        Boolean bool = this.includeAntRuntime;
        if (bool != null) {
            return bool.booleanValue();
        }
        return true;
    }

    public void setIncludejavaruntime(boolean z) {
        this.includeJavaRuntime = z;
    }

    public boolean getIncludejavaruntime() {
        return this.includeJavaRuntime;
    }

    public void setFork(boolean z) {
        this.fork = z;
    }

    public void setExecutable(String str) {
        this.forkedExecutable = str;
    }

    public String getExecutable() {
        return this.forkedExecutable;
    }

    public boolean isForkedJavac() {
        return this.fork || EXTJAVAC.equalsIgnoreCase(getCompiler());
    }

    public String getJavacExecutable() {
        if (this.forkedExecutable == null && isForkedJavac()) {
            this.forkedExecutable = getSystemJavac();
        } else if (this.forkedExecutable != null && !isForkedJavac()) {
            this.forkedExecutable = null;
        }
        return this.forkedExecutable;
    }

    public void setNowarn(boolean z) {
        this.nowarn = z;
    }

    public boolean getNowarn() {
        return this.nowarn;
    }

    public ImplementationSpecificArgument createCompilerArg() {
        ImplementationSpecificArgument implementationSpecificArgument = new ImplementationSpecificArgument();
        this.facade.addImplementationArgument(implementationSpecificArgument);
        return implementationSpecificArgument;
    }

    public String[] getCurrentCompilerArgs() {
        String explicitChoice = this.facade.getExplicitChoice();
        try {
            this.facade.setImplementation(getCompiler());
            String[] args = this.facade.getArgs();
            String altCompilerName = getAltCompilerName(this.facade.getImplementation());
            if (args.length == 0 && altCompilerName != null) {
                this.facade.setImplementation(altCompilerName);
                args = this.facade.getArgs();
            }
            return args;
        } finally {
            this.facade.setImplementation(explicitChoice);
        }
    }

    private String getAltCompilerName(String str) {
        if (JAVAC19.equalsIgnoreCase(str) || JAVAC18.equalsIgnoreCase(str) || JAVAC17.equalsIgnoreCase(str) || JAVAC16.equalsIgnoreCase(str) || JAVAC15.equalsIgnoreCase(str) || JAVAC14.equalsIgnoreCase(str) || JAVAC13.equalsIgnoreCase(str)) {
            return MODERN;
        }
        if (JAVAC12.equalsIgnoreCase(str) || JAVAC11.equalsIgnoreCase(str)) {
            return CLASSIC;
        }
        if (MODERN.equalsIgnoreCase(str)) {
            String strAssumedJavaVersion = assumedJavaVersion();
            if (JAVAC19.equalsIgnoreCase(strAssumedJavaVersion) || JAVAC18.equalsIgnoreCase(strAssumedJavaVersion) || JAVAC17.equalsIgnoreCase(strAssumedJavaVersion) || JAVAC16.equalsIgnoreCase(strAssumedJavaVersion) || JAVAC15.equalsIgnoreCase(strAssumedJavaVersion) || JAVAC14.equalsIgnoreCase(strAssumedJavaVersion) || JAVAC13.equalsIgnoreCase(strAssumedJavaVersion)) {
                return strAssumedJavaVersion;
            }
        }
        if (CLASSIC.equalsIgnoreCase(str)) {
            return assumedJavaVersion();
        }
        if (EXTJAVAC.equalsIgnoreCase(str)) {
            return assumedJavaVersion();
        }
        return null;
    }

    public void setTempdir(File file) {
        this.tmpDir = file;
    }

    public File getTempdir() {
        return this.tmpDir;
    }

    public void setUpdatedProperty(String str) {
        this.updatedProperty = str;
    }

    public void setErrorProperty(String str) {
        this.errorProperty = str;
    }

    public void setIncludeDestClasses(boolean z) {
        this.includeDestClasses = z;
    }

    public boolean isIncludeDestClasses() {
        return this.includeDestClasses;
    }

    public boolean getTaskSuccess() {
        return this.taskSuccess;
    }

    public Path createCompilerClasspath() {
        return this.facade.getImplementationClasspath(getProject());
    }

    public void add(CompilerAdapter compilerAdapter) {
        if (this.nestedAdapter != null) {
            throw new BuildException("Can't have more than one compiler adapter");
        }
        this.nestedAdapter = compilerAdapter;
    }

    public void setCreateMissingPackageInfoClass(boolean z) {
        this.createMissingPackageInfoClass = z;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        checkParameters();
        resetFileLists();
        for (String str : this.src.list()) {
            File fileResolveFile = getProject().resolveFile(str);
            if (!fileResolveFile.exists()) {
                throw new BuildException("srcdir \"" + fileResolveFile.getPath() + "\" does not exist!", getLocation());
            }
            String[] includedFiles = getDirectoryScanner(fileResolveFile).getIncludedFiles();
            File file = this.destDir;
            if (file == null) {
                file = fileResolveFile;
            }
            scanDir(fileResolveFile, file, includedFiles);
        }
        compile();
        if (this.updatedProperty == null || !this.taskSuccess || this.compileList.length == 0) {
            return;
        }
        getProject().setNewProperty(this.updatedProperty, "true");
    }

    protected void resetFileLists() {
        this.compileList = new File[0];
        this.packageInfos = new HashMap();
    }

    protected void scanDir(File file, File file2, String[] strArr) {
        GlobPatternMapper globPatternMapper = new GlobPatternMapper();
        for (String str : findSupportedFileExtensions()) {
            globPatternMapper.setFrom(str);
            globPatternMapper.setTo("*.class");
            File[] fileArrRestrictAsFiles = new SourceFileScanner(this).restrictAsFiles(strArr, file, file2, globPatternMapper);
            if (fileArrRestrictAsFiles.length > 0) {
                lookForPackageInfos(file, fileArrRestrictAsFiles);
                File[] fileArr = this.compileList;
                File[] fileArr2 = new File[fileArr.length + fileArrRestrictAsFiles.length];
                System.arraycopy(fileArr, 0, fileArr2, 0, fileArr.length);
                System.arraycopy(fileArrRestrictAsFiles, 0, fileArr2, this.compileList.length, fileArrRestrictAsFiles.length);
                this.compileList = fileArr2;
            }
        }
    }

    private String[] findSupportedFileExtensions() {
        String compiler = getCompiler();
        CompilerAdapter compiler2 = this.nestedAdapter;
        if (compiler2 == null) {
            compiler2 = CompilerAdapterFactory.getCompiler(compiler, this, createCompilerClasspath());
        }
        String[] supportedFileExtensions = compiler2 instanceof CompilerAdapterExtension ? ((CompilerAdapterExtension) compiler2).getSupportedFileExtensions() : null;
        if (supportedFileExtensions == null) {
            supportedFileExtensions = new String[]{"java"};
        }
        for (int i = 0; i < supportedFileExtensions.length; i++) {
            if (!supportedFileExtensions[i].startsWith("*.")) {
                supportedFileExtensions[i] = "*." + supportedFileExtensions[i];
            }
        }
        return supportedFileExtensions;
    }

    public File[] getFileList() {
        return this.compileList;
    }

    protected boolean isJdkCompiler(String str) {
        return MODERN.equals(str) || CLASSIC.equals(str) || JAVAC19.equals(str) || JAVAC18.equals(str) || JAVAC17.equals(str) || JAVAC16.equals(str) || JAVAC15.equals(str) || JAVAC14.equals(str) || JAVAC13.equals(str) || JAVAC12.equals(str) || JAVAC11.equals(str);
    }

    protected String getSystemJavac() {
        return JavaEnvUtils.getJdkExecutable("javac");
    }

    public void setCompiler(String str) {
        this.facade.setImplementation(str);
    }

    public String getCompiler() {
        String compilerVersion = getCompilerVersion();
        if (!this.fork) {
            return compilerVersion;
        }
        if (isJdkCompiler(compilerVersion)) {
            return EXTJAVAC;
        }
        log("Since compiler setting isn't classic or modern, ignoring fork setting.", 1);
        return compilerVersion;
    }

    public String getCompilerVersion() {
        this.facade.setMagicValue(getProject().getProperty("build.compiler"));
        return this.facade.getImplementation();
    }

    protected void checkParameters() throws BuildException {
        Path path = this.src;
        if (path == null) {
            throw new BuildException("srcdir attribute must be set!", getLocation());
        }
        if (path.size() == 0) {
            throw new BuildException("srcdir attribute must be set!", getLocation());
        }
        File file = this.destDir;
        if (file != null && !file.isDirectory()) {
            throw new BuildException("destination directory \"" + this.destDir + "\" does not exist or is not a directory", getLocation());
        }
        if (this.includeAntRuntime == null && getProject().getProperty(MagicNames.BUILD_SYSCLASSPATH) == null) {
            log(getLocation() + "warning: 'includeantruntime' was not set, defaulting to build.sysclasspath=last; set to false for repeatable builds", 1);
        }
    }

    protected void compile() {
        String compiler = getCompiler();
        if (this.compileList.length > 0) {
            log("Compiling " + this.compileList.length + " source file" + (this.compileList.length == 1 ? "" : "s") + (this.destDir != null ? " to " + this.destDir : ""));
            if (this.listFiles) {
                int i = 0;
                while (true) {
                    File[] fileArr = this.compileList;
                    if (i >= fileArr.length) {
                        break;
                    }
                    log(fileArr[i].getAbsolutePath());
                    i++;
                }
            }
            CompilerAdapter compiler2 = this.nestedAdapter;
            if (compiler2 == null) {
                compiler2 = CompilerAdapterFactory.getCompiler(compiler, this, createCompilerClasspath());
            }
            compiler2.setJavac(this);
            if (compiler2.execute()) {
                if (this.createMissingPackageInfoClass) {
                    try {
                        File fileResolveFile = this.destDir;
                        if (fileResolveFile == null) {
                            fileResolveFile = getProject().resolveFile(this.src.list()[0]);
                        }
                        generateMissingPackageInfoClasses(fileResolveFile);
                        return;
                    } catch (IOException e) {
                        throw new BuildException(e, getLocation());
                    }
                }
                return;
            }
            this.taskSuccess = false;
            if (this.errorProperty != null) {
                getProject().setNewProperty(this.errorProperty, "true");
            }
            if (this.failOnError) {
                throw new BuildException(FAIL_MSG, getLocation());
            }
            log(FAIL_MSG, 0);
        }
    }

    public class ImplementationSpecificArgument extends org.apache.tools.ant.util.facade.ImplementationSpecificArgument {
        public ImplementationSpecificArgument() {
        }

        public void setCompiler(String str) {
            super.setImplementation(str);
        }
    }

    private void lookForPackageInfos(File file, File[] fileArr) {
        for (File file2 : fileArr) {
            if (file2.getName().equals("package-info.java")) {
                String strReplace = FILE_UTILS.removeLeadingPath(file, file2).replace(File.separatorChar, '/');
                if (!strReplace.endsWith("/package-info.java")) {
                    log("anomalous package-info.java path: " + strReplace, 1);
                } else {
                    this.packageInfos.put(strReplace.substring(0, strReplace.length() - 18), new Long(file2.lastModified()));
                }
            }
        }
    }

    private void generateMissingPackageInfoClasses(File file) throws IOException {
        for (Map.Entry<String, Long> entry : this.packageInfos.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();
            File file2 = new File(file, key.replace('/', File.separatorChar));
            file2.mkdirs();
            File file3 = new File(file2, "package-info.class");
            if (!file3.isFile() || file3.lastModified() < value.longValue()) {
                log("Creating empty " + file3);
                FileOutputStream fileOutputStream = new FileOutputStream(file3);
                try {
                    fileOutputStream.write(PACKAGE_INFO_CLASS_HEADER);
                    byte[] bytes = key.getBytes("UTF-8");
                    byte length = (byte) (bytes.length + 13);
                    fileOutputStream.write(length / 256);
                    fileOutputStream.write(length % 256);
                    fileOutputStream.write(bytes);
                    fileOutputStream.write(PACKAGE_INFO_CLASS_FOOTER);
                } finally {
                    fileOutputStream.close();
                }
            }
        }
    }
}
