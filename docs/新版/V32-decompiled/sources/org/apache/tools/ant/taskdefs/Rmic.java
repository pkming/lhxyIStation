package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.util.Vector;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.rmic.RmicAdapter;
import org.apache.tools.ant.taskdefs.rmic.RmicAdapterFactory;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.SourceFileScanner;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.util.facade.FacadeTaskHelper;

/* JADX INFO: loaded from: classes3.dex */
public class Rmic extends MatchingTask {
    public static final String ERROR_BASE_NOT_SET = "base or destdir attribute must be set!";
    public static final String ERROR_LOADING_CAUSED_EXCEPTION = ". Loading caused Exception: ";
    public static final String ERROR_NOT_A_DIR = "base or destdir is not a directory:";
    public static final String ERROR_NOT_DEFINED = ". It is not defined.";
    public static final String ERROR_NOT_FOUND = ". It could not be found.";
    public static final String ERROR_NO_BASE_EXISTS = "base or destdir does not exist: ";
    public static final String ERROR_RMIC_FAILED = "Rmic failed; see the compiler error output for details.";
    public static final String ERROR_UNABLE_TO_VERIFY_CLASS = "Unable to verify class ";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File baseDir;
    private String classname;
    private Path compileClasspath;
    private File destDir;
    private Path extDirs;
    private String idlOpts;
    private String iiopOpts;
    private File sourceBase;
    private String stubVersion;
    private boolean verify = false;
    private boolean filtering = false;
    private boolean iiop = false;
    private boolean idl = false;
    private boolean debug = false;
    private boolean includeAntRuntime = true;
    private boolean includeJavaRuntime = false;
    private Vector compileList = new Vector();
    private AntClassLoader loader = null;
    private String executable = null;
    private boolean listFiles = false;
    private RmicAdapter nestedAdapter = null;
    private FacadeTaskHelper facade = new FacadeTaskHelper("default");

    public void setBase(File file) {
        this.baseDir = file;
    }

    public void setDestdir(File file) {
        this.destDir = file;
    }

    public File getDestdir() {
        return this.destDir;
    }

    public File getOutputDir() {
        if (getDestdir() != null) {
            return getDestdir();
        }
        return getBase();
    }

    public File getBase() {
        return this.baseDir;
    }

    public void setClassname(String str) {
        this.classname = str;
    }

    public String getClassname() {
        return this.classname;
    }

    public void setSourceBase(File file) {
        this.sourceBase = file;
    }

    public File getSourceBase() {
        return this.sourceBase;
    }

    public void setStubVersion(String str) {
        this.stubVersion = str;
    }

    public String getStubVersion() {
        return this.stubVersion;
    }

    public void setFiltering(boolean z) {
        this.filtering = z;
    }

    public boolean getFiltering() {
        return this.filtering;
    }

    public void setDebug(boolean z) {
        this.debug = z;
    }

    public boolean getDebug() {
        return this.debug;
    }

    public synchronized void setClasspath(Path path) {
        Path path2 = this.compileClasspath;
        if (path2 == null) {
            this.compileClasspath = path;
        } else {
            path2.append(path);
        }
    }

    public synchronized Path createClasspath() {
        if (this.compileClasspath == null) {
            this.compileClasspath = new Path(getProject());
        }
        return this.compileClasspath.createPath();
    }

    public void setClasspathRef(Reference reference) {
        createClasspath().setRefid(reference);
    }

    public Path getClasspath() {
        return this.compileClasspath;
    }

    public void setVerify(boolean z) {
        this.verify = z;
    }

    public boolean getVerify() {
        return this.verify;
    }

    public void setIiop(boolean z) {
        this.iiop = z;
    }

    public boolean getIiop() {
        return this.iiop;
    }

    public void setIiopopts(String str) {
        this.iiopOpts = str;
    }

    public String getIiopopts() {
        return this.iiopOpts;
    }

    public void setIdl(boolean z) {
        this.idl = z;
    }

    public boolean getIdl() {
        return this.idl;
    }

    public void setIdlopts(String str) {
        this.idlOpts = str;
    }

    public String getIdlopts() {
        return this.idlOpts;
    }

    public Vector getFileList() {
        return this.compileList;
    }

    public void setIncludeantruntime(boolean z) {
        this.includeAntRuntime = z;
    }

    public boolean getIncludeantruntime() {
        return this.includeAntRuntime;
    }

    public void setIncludejavaruntime(boolean z) {
        this.includeJavaRuntime = z;
    }

    public boolean getIncludejavaruntime() {
        return this.includeJavaRuntime;
    }

    public synchronized void setExtdirs(Path path) {
        Path path2 = this.extDirs;
        if (path2 == null) {
            this.extDirs = path;
        } else {
            path2.append(path);
        }
    }

    public synchronized Path createExtdirs() {
        if (this.extDirs == null) {
            this.extDirs = new Path(getProject());
        }
        return this.extDirs.createPath();
    }

    public Path getExtdirs() {
        return this.extDirs;
    }

    public Vector getCompileList() {
        return this.compileList;
    }

    public void setCompiler(String str) {
        if (str.length() > 0) {
            this.facade.setImplementation(str);
        }
    }

    public String getCompiler() {
        this.facade.setMagicValue(getProject().getProperty("build.rmic"));
        return this.facade.getImplementation();
    }

    public ImplementationSpecificArgument createCompilerArg() {
        ImplementationSpecificArgument implementationSpecificArgument = new ImplementationSpecificArgument();
        this.facade.addImplementationArgument(implementationSpecificArgument);
        return implementationSpecificArgument;
    }

    public String[] getCurrentCompilerArgs() {
        getCompiler();
        return this.facade.getArgs();
    }

    public void setExecutable(String str) {
        this.executable = str;
    }

    public String getExecutable() {
        return this.executable;
    }

    public Path createCompilerClasspath() {
        return this.facade.getImplementationClasspath(getProject());
    }

    public void setListfiles(boolean z) {
        this.listFiles = z;
    }

    public void add(RmicAdapter rmicAdapter) {
        if (this.nestedAdapter != null) {
            throw new BuildException("Can't have more than one rmic adapter");
        }
        this.nestedAdapter = rmicAdapter;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        try {
            this.compileList.clear();
            File outputDir = getOutputDir();
            if (outputDir == null) {
                throw new BuildException(ERROR_BASE_NOT_SET, getLocation());
            }
            if (!outputDir.exists()) {
                throw new BuildException(ERROR_NO_BASE_EXISTS + outputDir, getLocation());
            }
            if (!outputDir.isDirectory()) {
                throw new BuildException(ERROR_NOT_A_DIR + outputDir, getLocation());
            }
            if (this.verify) {
                log("Verify has been turned on.", 3);
            }
            RmicAdapter rmic = this.nestedAdapter;
            if (rmic == null) {
                rmic = RmicAdapterFactory.getRmic(getCompiler(), this, createCompilerClasspath());
            }
            rmic.setRmic(this);
            this.loader = getProject().createClassLoader(rmic.getClasspath());
            if (this.classname == null) {
                scanDir(this.baseDir, getDirectoryScanner(this.baseDir).getIncludedFiles(), rmic.getMapper());
            } else {
                String str = this.classname.replace('.', File.separatorChar) + ".class";
                if (new File(this.baseDir, str).isFile()) {
                    scanDir(this.baseDir, new String[]{str}, rmic.getMapper());
                } else {
                    this.compileList.add(this.classname);
                }
            }
            int size = this.compileList.size();
            if (size > 0) {
                log("RMI Compiling " + size + " class" + (size > 1 ? "es" : "") + " to " + outputDir, 2);
                if (this.listFiles) {
                    for (int i = 0; i < size; i++) {
                        log(this.compileList.get(i).toString());
                    }
                }
                if (!rmic.execute()) {
                    throw new BuildException(ERROR_RMIC_FAILED, getLocation());
                }
            }
            File file = this.sourceBase;
            if (file != null && !outputDir.equals(file) && size > 0) {
                if (this.idl) {
                    log("Cannot determine sourcefiles in idl mode, ", 1);
                    log("sourcebase attribute will be ignored.", 1);
                } else {
                    for (int i2 = 0; i2 < size; i2++) {
                        moveGeneratedFile(outputDir, this.sourceBase, (String) this.compileList.elementAt(i2), rmic);
                    }
                }
            }
        } finally {
            cleanup();
        }
    }

    protected void cleanup() {
        AntClassLoader antClassLoader = this.loader;
        if (antClassLoader != null) {
            antClassLoader.cleanup();
            this.loader = null;
        }
    }

    private void moveGeneratedFile(File file, File file2, String str, RmicAdapter rmicAdapter) throws BuildException {
        String[] strArrMapFileName = rmicAdapter.getMapper().mapFileName(str.replace('.', File.separatorChar) + ".class");
        for (String str2 : strArrMapFileName) {
            if (str2.endsWith(".class")) {
                String str3 = StringUtils.removeSuffix(str2, ".class") + ".java";
                File file3 = new File(file, str3);
                if (file3.exists()) {
                    File file4 = new File(file2, str3);
                    try {
                        if (this.filtering) {
                            FILE_UTILS.copyFile(file3, file4, new FilterSetCollection(getProject().getGlobalFilterSet()));
                        } else {
                            FILE_UTILS.copyFile(file3, file4);
                        }
                        file3.delete();
                    } catch (IOException e) {
                        throw new BuildException("Failed to copy " + file3 + " to " + file4 + " due to " + e.getMessage(), e, getLocation());
                    }
                } else {
                    continue;
                }
            }
        }
    }

    protected void scanDir(File file, String[] strArr, FileNameMapper fileNameMapper) {
        String str;
        if (this.idl) {
            log("will leave uptodate test to rmic implementation in idl mode.", 3);
        } else if (this.iiop && (str = this.iiopOpts) != null && str.indexOf("-always") > -1) {
            log("no uptodate test as -always option has been specified", 3);
        } else {
            strArr = new SourceFileScanner(this).restrict(strArr, file, getOutputDir(), fileNameMapper);
        }
        for (String str2 : strArr) {
            String strReplace = str2.replace(File.separatorChar, '.');
            this.compileList.addElement(strReplace.substring(0, strReplace.lastIndexOf(".class")));
        }
    }

    public boolean isValidRmiRemote(String str) {
        try {
            Class<?> clsLoadClass = this.loader.loadClass(str);
            if (!clsLoadClass.isInterface() || this.iiop || this.idl) {
                return isValidRmiRemote(clsLoadClass);
            }
            return false;
        } catch (ClassNotFoundException unused) {
            log(ERROR_UNABLE_TO_VERIFY_CLASS + str + ERROR_NOT_FOUND, 1);
            return false;
        } catch (NoClassDefFoundError unused2) {
            log(ERROR_UNABLE_TO_VERIFY_CLASS + str + ERROR_NOT_DEFINED, 1);
            return false;
        } catch (Throwable th) {
            log(ERROR_UNABLE_TO_VERIFY_CLASS + str + ERROR_LOADING_CAUSED_EXCEPTION + th.getMessage(), 1);
            return false;
        }
    }

    public Class getRemoteInterface(Class cls) {
        Class<?>[] interfaces;
        if (!Remote.class.isAssignableFrom(cls) || (interfaces = cls.getInterfaces()) == null) {
            return null;
        }
        for (int i = 0; i < interfaces.length; i++) {
            if (Remote.class.isAssignableFrom(interfaces[i])) {
                return interfaces[i];
            }
        }
        return null;
    }

    private boolean isValidRmiRemote(Class cls) {
        return getRemoteInterface(cls) != null;
    }

    public ClassLoader getLoader() {
        return this.loader;
    }

    public class ImplementationSpecificArgument extends org.apache.tools.ant.util.facade.ImplementationSpecificArgument {
        public ImplementationSpecificArgument() {
        }

        public void setCompiler(String str) {
            super.setImplementation(str);
        }
    }
}
