package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import javax.xml.parsers.SAXParser;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.ejb.EjbJar;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.depend.DependencyAnalyzer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes3.dex */
public class GenericDeploymentTool implements EJBDeploymentTool {
    public static final String ANALYZER_CLASS_FULL = "org.apache.tools.ant.util.depend.bcel.FullAnalyzer";
    public static final String ANALYZER_CLASS_SUPER = "org.apache.tools.ant.util.depend.bcel.AncestorAnalyzer";
    public static final String ANALYZER_FULL = "full";
    public static final String ANALYZER_NONE = "none";
    public static final String ANALYZER_SUPER = "super";
    public static final String DEFAULT_ANALYZER = "super";
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    protected static final String EJB_DD = "ejb-jar.xml";
    public static final int JAR_COMPRESS_LEVEL = 9;
    protected static final String MANIFEST = "META-INF/MANIFEST.MF";
    protected static final String META_DIR = "META-INF/";
    private Set addedfiles;
    private Path classpath;
    private EjbJar.Config config;
    private DependencyAnalyzer dependencyAnalyzer;
    private File destDir;
    private DescriptorHandler handler;
    private Task task;
    private String genericJarSuffix = "-generic.jar";
    private ClassLoader classpathLoader = null;

    protected void addVendorFiles(Hashtable hashtable, String str) {
    }

    protected void checkConfiguration(String str, SAXParser sAXParser) throws BuildException {
    }

    protected void registerKnownDTDs(DescriptorHandler descriptorHandler) {
    }

    public void setDestdir(File file) {
        this.destDir = file;
    }

    protected File getDestDir() {
        return this.destDir;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.EJBDeploymentTool
    public void setTask(Task task) {
        this.task = task;
    }

    protected Task getTask() {
        return this.task;
    }

    protected EjbJar.Config getConfig() {
        return this.config;
    }

    protected boolean usingBaseJarName() {
        return this.config.baseJarName != null;
    }

    public void setGenericJarSuffix(String str) {
        this.genericJarSuffix = str;
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.task.getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspath(Path path) {
        this.classpath = path;
    }

    protected Path getCombinedClasspath() {
        Path path = this.classpath;
        if (this.config.classpath == null) {
            return path;
        }
        if (path == null) {
            return this.config.classpath;
        }
        path.append(this.config.classpath);
        return path;
    }

    protected void log(String str, int i) {
        getTask().log(str, i);
    }

    protected Location getLocation() {
        return getTask().getLocation();
    }

    private void createAnalyzer() {
        String str = this.config.analyzer;
        if (str == null) {
            str = "super";
        }
        if (str.equals("none")) {
            return;
        }
        if (str.equals("super")) {
            str = ANALYZER_CLASS_SUPER;
        } else if (str.equals("full")) {
            str = "org.apache.tools.ant.util.depend.bcel.FullAnalyzer";
        }
        try {
            DependencyAnalyzer dependencyAnalyzer = (DependencyAnalyzer) Class.forName(str).newInstance();
            this.dependencyAnalyzer = dependencyAnalyzer;
            dependencyAnalyzer.addClassPath(new Path(this.task.getProject(), this.config.srcDir.getPath()));
            this.dependencyAnalyzer.addClassPath(this.config.classpath);
        } catch (Exception e) {
            this.dependencyAnalyzer = null;
            this.task.log("Unable to load dependency analyzer: " + str + " - exception: " + e.getMessage(), 1);
        } catch (NoClassDefFoundError e2) {
            this.dependencyAnalyzer = null;
            this.task.log("Unable to load dependency analyzer: " + str + " - dependent class not found: " + e2.getMessage(), 1);
        }
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.EJBDeploymentTool
    public void configure(EjbJar.Config config) {
        this.config = config;
        createAnalyzer();
        this.classpathLoader = null;
    }

    protected void addFileToJar(JarOutputStream jarOutputStream, File file, String str) throws Throwable {
        FileInputStream fileInputStream = null;
        try {
            try {
                if (!this.addedfiles.contains(str)) {
                    FileInputStream fileInputStream2 = new FileInputStream(file);
                    try {
                        jarOutputStream.putNextEntry(new ZipEntry(str.replace('\\', '/')));
                        byte[] bArr = new byte[2048];
                        int i = 0;
                        do {
                            jarOutputStream.write(bArr, 0, i);
                            i = fileInputStream2.read(bArr, 0, 2048);
                        } while (i != -1);
                        this.addedfiles.add(str);
                        fileInputStream = fileInputStream2;
                    } catch (IOException e) {
                        e = e;
                        fileInputStream = fileInputStream2;
                        log("WARNING: IOException while adding entry " + str + " to jarfile from " + file.getPath() + " " + e.getClass().getName() + "-" + e.getMessage(), 1);
                        if (fileInputStream == null) {
                            return;
                        }
                    } catch (Throwable th) {
                        th = th;
                        fileInputStream = fileInputStream2;
                        if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (IOException unused) {
                            }
                        }
                        throw th;
                    }
                }
                if (fileInputStream == null) {
                    return;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e2) {
            e = e2;
        }
        try {
            fileInputStream.close();
        } catch (IOException unused2) {
        }
    }

    protected DescriptorHandler getDescriptorHandler(File file) {
        DescriptorHandler descriptorHandler = new DescriptorHandler(getTask(), file);
        registerKnownDTDs(descriptorHandler);
        for (EjbJar.DTDLocation dTDLocation : getConfig().dtdLocations) {
            descriptorHandler.registerDTD(dTDLocation.getPublicId(), dTDLocation.getLocation());
        }
        return descriptorHandler;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.EJBDeploymentTool
    public void processDescriptor(String str, SAXParser sAXParser) throws Throwable {
        checkConfiguration(str, sAXParser);
        try {
            this.handler = getDescriptorHandler(this.config.srcDir);
            Hashtable ejbFiles = parseEjbFiles(str, sAXParser);
            addSupportClasses(ejbFiles);
            String jarBaseName = getJarBaseName(str);
            String vendorDDPrefix = getVendorDDPrefix(jarBaseName, str);
            File manifestFile = getManifestFile(vendorDDPrefix);
            if (manifestFile != null) {
                ejbFiles.put(MANIFEST, manifestFile);
            }
            ejbFiles.put("META-INF/ejb-jar.xml", new File(this.config.descriptorDir, str));
            addVendorFiles(ejbFiles, vendorDDPrefix);
            checkAndAddDependants(ejbFiles);
            if (this.config.flatDestDir && jarBaseName.length() != 0) {
                int iLastIndexOf = jarBaseName.lastIndexOf(File.separator);
                if (iLastIndexOf == -1) {
                    iLastIndexOf = 0;
                }
                jarBaseName = jarBaseName.substring(iLastIndexOf, jarBaseName.length());
            }
            File vendorOutputJarFile = getVendorOutputJarFile(jarBaseName);
            if (needToRebuild(ejbFiles, vendorOutputJarFile)) {
                log("building " + vendorOutputJarFile.getName() + " with " + String.valueOf(ejbFiles.size()) + " files", 2);
                writeJar(jarBaseName, vendorOutputJarFile, ejbFiles, getPublicId());
            } else {
                log(vendorOutputJarFile.toString() + " is up to date.", 3);
            }
        } catch (IOException e) {
            throw new BuildException("IOException while parsing'" + str + "'.  This probably indicates that the descriptor doesn't exist. Details: " + e.getMessage(), e);
        } catch (SAXException e2) {
            throw new BuildException("SAXException while parsing '" + str + "'. This probably indicates badly-formed XML.  Details: " + e2.getMessage(), e2);
        }
    }

    protected Hashtable parseEjbFiles(String str, SAXParser sAXParser) throws Throwable {
        FileInputStream fileInputStream = null;
        try {
            FileInputStream fileInputStream2 = new FileInputStream(new File(this.config.descriptorDir, str));
            try {
                sAXParser.parse(new InputSource(fileInputStream2), this.handler);
                Hashtable files = this.handler.getFiles();
                try {
                    fileInputStream2.close();
                } catch (IOException unused) {
                }
                return files;
            } catch (Throwable th) {
                th = th;
                fileInputStream = fileInputStream2;
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException unused2) {
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    protected void addSupportClasses(Hashtable hashtable) {
        Project project = this.task.getProject();
        for (FileSet fileSet : this.config.supportFileSets) {
            File dir = fileSet.getDir(project);
            DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(project);
            directoryScanner.scan();
            String[] includedFiles = directoryScanner.getIncludedFiles();
            for (int i = 0; i < includedFiles.length; i++) {
                hashtable.put(includedFiles[i], new File(dir, includedFiles[i]));
            }
        }
    }

    protected String getJarBaseName(String str) {
        int iIndexOf;
        if (this.config.namingScheme.getValue().equals(EjbJar.NamingScheme.BASEJARNAME)) {
            int iLastIndexOf = str.replace('\\', '/').lastIndexOf(47);
            return (iLastIndexOf != -1 ? str.substring(0, iLastIndexOf + 1) : "") + this.config.baseJarName;
        }
        if (this.config.namingScheme.getValue().equals(EjbJar.NamingScheme.DESCRIPTOR)) {
            int iLastIndexOf2 = str.lastIndexOf(File.separator);
            if (iLastIndexOf2 != -1) {
                iIndexOf = str.indexOf(this.config.baseNameTerminator, iLastIndexOf2);
            } else {
                iIndexOf = str.indexOf(this.config.baseNameTerminator);
            }
            if (iIndexOf != -1) {
                return str.substring(0, iIndexOf);
            }
            throw new BuildException("Unable to determine jar name from descriptor \"" + str + "\"");
        }
        if (!this.config.namingScheme.getValue().equals("directory")) {
            return this.config.namingScheme.getValue().equals(EjbJar.NamingScheme.EJB_NAME) ? this.handler.getEjbName() : "";
        }
        String absolutePath = new File(this.config.descriptorDir, str).getAbsolutePath();
        int iLastIndexOf3 = absolutePath.lastIndexOf(File.separator);
        if (iLastIndexOf3 == -1) {
            throw new BuildException("Unable to determine directory name holding descriptor");
        }
        String strSubstring = absolutePath.substring(0, iLastIndexOf3);
        int iLastIndexOf4 = strSubstring.lastIndexOf(File.separator);
        if (iLastIndexOf4 != -1) {
            strSubstring = strSubstring.substring(iLastIndexOf4 + 1);
        }
        return strSubstring;
    }

    public String getVendorDDPrefix(String str, String str2) {
        if (this.config.namingScheme.getValue().equals(EjbJar.NamingScheme.DESCRIPTOR)) {
            return str + this.config.baseNameTerminator;
        }
        if (!this.config.namingScheme.getValue().equals(EjbJar.NamingScheme.BASEJARNAME) && !this.config.namingScheme.getValue().equals(EjbJar.NamingScheme.EJB_NAME) && !this.config.namingScheme.getValue().equals("directory")) {
            return null;
        }
        int iLastIndexOf = str2.replace('\\', '/').lastIndexOf(47);
        return iLastIndexOf == -1 ? "" : str2.substring(0, iLastIndexOf + 1);
    }

    File getVendorOutputJarFile(String str) {
        return new File(this.destDir, str + this.genericJarSuffix);
    }

    protected boolean needToRebuild(Hashtable hashtable, File file) {
        if (!file.exists()) {
            return true;
        }
        long jLastModified = file.lastModified();
        for (File file2 : hashtable.values()) {
            if (jLastModified < file2.lastModified()) {
                log("Build needed because " + file2.getPath() + " is out of date", 3);
                return true;
            }
        }
        return false;
    }

    protected String getPublicId() {
        return this.handler.getPublicId();
    }

    protected File getManifestFile(String str) {
        File file = new File(getConfig().descriptorDir, str + "manifest.mf");
        if (file.exists()) {
            return file;
        }
        if (this.config.manifest != null) {
            return this.config.manifest;
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void writeJar(String str, File file, Hashtable hashtable, String str2) throws BuildException {
        InputStream resourceAsStream;
        JarOutputStream jarOutputStream = null;
        Object[] objArr = 0;
        try {
            try {
                Set set = this.addedfiles;
                if (set == null) {
                    this.addedfiles = new HashSet();
                } else {
                    set.clear();
                }
                if (file.exists()) {
                    file.delete();
                }
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    File file2 = (File) hashtable.get(MANIFEST);
                    try {
                        if (file2 != null && file2.exists()) {
                            resourceAsStream = new FileInputStream(file2);
                        } else {
                            resourceAsStream = getClass().getResourceAsStream("/org/apache/tools/ant/defaultManifest.mf");
                            if (resourceAsStream == null) {
                                throw new BuildException("Could not find default manifest: /org/apache/tools/ant/defaultManifest.mf");
                            }
                        }
                        Manifest manifest = new Manifest(resourceAsStream);
                        if (resourceAsStream != null) {
                            resourceAsStream.close();
                        }
                        JarOutputStream jarOutputStream2 = new JarOutputStream(new FileOutputStream(file), manifest);
                        try {
                            jarOutputStream2.setMethod(8);
                            for (String str3 : hashtable.keySet()) {
                                if (!str3.equals(MANIFEST)) {
                                    File file3 = (File) hashtable.get(str3);
                                    log("adding file '" + str3 + "'", 3);
                                    addFileToJar(jarOutputStream2, file3, str3);
                                    String[] list = file3.getParentFile().list(new InnerClassFilenameFilter(file3.getName()));
                                    if (list != null) {
                                        int length = list.length;
                                        for (int i = 0; i < length; i++) {
                                            int iLastIndexOf = str3.lastIndexOf(file3.getName()) - 1;
                                            if (iLastIndexOf < 0) {
                                                str3 = list[i];
                                            } else {
                                                str3 = str3.substring(0, iLastIndexOf) + File.separatorChar + list[i];
                                            }
                                            file3 = new File(this.config.srcDir, str3);
                                            log("adding innerclass file '" + str3 + "'", 3);
                                            addFileToJar(jarOutputStream2, file3, str3);
                                        }
                                    }
                                }
                            }
                            try {
                                jarOutputStream2.close();
                            } catch (IOException unused) {
                            }
                        } catch (IOException e) {
                            e = e;
                            throw new BuildException("IOException while processing ejb-jar file '" + file.toString() + "'. Details: " + e.getMessage(), e);
                        } catch (Throwable th2) {
                            th = th2;
                            jarOutputStream = jarOutputStream2;
                            if (jarOutputStream != null) {
                                try {
                                    jarOutputStream.close();
                                } catch (IOException unused2) {
                                }
                            }
                            throw th;
                        }
                    } catch (IOException e2) {
                        e = e2;
                        throw new BuildException("Unable to read manifest", e, getLocation());
                    }
                } catch (IOException e3) {
                    e = e3;
                } catch (Throwable th3) {
                    th = th3;
                    if (0 != 0) {
                        (objArr == true ? 1 : 0).close();
                    }
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
            }
        } catch (IOException e4) {
            e = e4;
        }
    }

    protected void checkAndAddDependants(Hashtable hashtable) throws BuildException {
        DependencyAnalyzer dependencyAnalyzer = this.dependencyAnalyzer;
        if (dependencyAnalyzer == null) {
            return;
        }
        dependencyAnalyzer.reset();
        for (String str : hashtable.keySet()) {
            if (str.endsWith(".class")) {
                this.dependencyAnalyzer.addRootClass(str.substring(0, str.length() - 6).replace(File.separatorChar, '/').replace('/', '.'));
            }
        }
        Enumeration<String> classDependencies = this.dependencyAnalyzer.getClassDependencies();
        while (classDependencies.hasMoreElements()) {
            String strNextElement = classDependencies.nextElement();
            String str2 = strNextElement.replace('.', File.separatorChar) + ".class";
            File file = new File(this.config.srcDir, str2);
            if (file.exists()) {
                hashtable.put(str2, file);
                log("dependent class: " + strNextElement + " - " + file, 3);
            }
        }
    }

    protected ClassLoader getClassLoaderForBuild() {
        ClassLoader classLoader = this.classpathLoader;
        if (classLoader != null) {
            return classLoader;
        }
        Path combinedClasspath = getCombinedClasspath();
        if (combinedClasspath == null) {
            this.classpathLoader = getClass().getClassLoader();
        } else {
            this.classpathLoader = getTask().getProject().createClassLoader(combinedClasspath);
        }
        return this.classpathLoader;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.EJBDeploymentTool
    public void validateConfigured() throws BuildException {
        File file = this.destDir;
        if (file == null || !file.isDirectory()) {
            throw new BuildException("A valid destination directory must be specified using the \"destdir\" attribute.", getLocation());
        }
    }
}
