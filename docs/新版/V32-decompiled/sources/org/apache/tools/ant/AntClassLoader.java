package org.apache.tools.ant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.apache.tools.ant.launch.Locator;
import org.apache.tools.ant.taskdefs.Definer;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.CollectionUtils;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.LoaderUtils;
import org.apache.tools.ant.util.ReflectUtil;
import org.apache.tools.ant.util.VectorSet;
import org.apache.tools.zip.ZipLong;

/* JADX INFO: loaded from: classes3.dex */
public class AntClassLoader extends ClassLoader implements SubBuildListener {
    private static final int BUFFER_SIZE = 8192;
    private static final ZipLong EOCD_SIG;
    private static final int NUMBER_OF_STRINGS = 256;
    private static final ZipLong SINGLE_SEGMENT_SPLIT_MARKER;
    private static Class<?> subClassToLoad;
    private boolean ignoreBase;
    private boolean isContextLoaderSaved;
    private Hashtable<File, JarFile> jarFiles;
    private Vector<String> loaderPackages;
    private ClassLoader parent;
    private boolean parentFirst;
    private Vector<File> pathComponents;
    private Project project;
    private ClassLoader savedContextLoader;
    private Vector<String> systemPackages;
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static Map<String, String> pathMap = Collections.synchronizedMap(new HashMap());
    private static final Class<?>[] CONSTRUCTOR_ARGS = {ClassLoader.class, Project.class, Path.class, Boolean.TYPE};

    @Override // org.apache.tools.ant.BuildListener
    public void buildStarted(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.BuildListener
    public void messageLogged(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.SubBuildListener
    public void subBuildStarted(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.BuildListener
    public void targetFinished(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.BuildListener
    public void targetStarted(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.BuildListener
    public void taskFinished(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.BuildListener
    public void taskStarted(BuildEvent buildEvent) {
    }

    static {
        subClassToLoad = null;
        if (JavaEnvUtils.isAtLeastJavaVersion(JavaEnvUtils.JAVA_1_5)) {
            try {
                subClassToLoad = Class.forName("org.apache.tools.ant.loader.AntClassLoader5");
            } catch (ClassNotFoundException unused) {
            }
        }
        EOCD_SIG = new ZipLong(101010256L);
        SINGLE_SEGMENT_SPLIT_MARKER = new ZipLong(808471376L);
    }

    private class ResourceEnumeration implements Enumeration<URL> {
        private URL nextResource;
        private int pathElementsIndex = 0;
        private String resourceName;

        ResourceEnumeration(String str) {
            this.resourceName = str;
            findNextResource();
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return this.nextResource != null;
        }

        @Override // java.util.Enumeration
        public URL nextElement() {
            URL url = this.nextResource;
            if (url == null) {
                throw new NoSuchElementException();
            }
            findNextResource();
            return url;
        }

        private void findNextResource() {
            URL resourceURL = null;
            while (this.pathElementsIndex < AntClassLoader.this.pathComponents.size() && resourceURL == null) {
                try {
                    resourceURL = AntClassLoader.this.getResourceURL((File) AntClassLoader.this.pathComponents.elementAt(this.pathElementsIndex), this.resourceName);
                    this.pathElementsIndex++;
                } catch (BuildException unused) {
                }
            }
            this.nextResource = resourceURL;
        }
    }

    public AntClassLoader(ClassLoader classLoader, Project project, Path path) throws Throwable {
        this.pathComponents = new VectorSet();
        this.parentFirst = true;
        this.systemPackages = new Vector<>();
        this.loaderPackages = new Vector<>();
        this.ignoreBase = false;
        this.parent = null;
        this.jarFiles = new Hashtable<>();
        this.savedContextLoader = null;
        this.isContextLoaderSaved = false;
        setParent(classLoader);
        setClassPath(path);
        setProject(project);
    }

    public AntClassLoader() {
        this.pathComponents = new VectorSet();
        this.parentFirst = true;
        this.systemPackages = new Vector<>();
        this.loaderPackages = new Vector<>();
        this.ignoreBase = false;
        this.parent = null;
        this.jarFiles = new Hashtable<>();
        this.savedContextLoader = null;
        this.isContextLoaderSaved = false;
        setParent(null);
    }

    public AntClassLoader(Project project, Path path) throws Throwable {
        this.pathComponents = new VectorSet();
        this.parentFirst = true;
        this.systemPackages = new Vector<>();
        this.loaderPackages = new Vector<>();
        this.ignoreBase = false;
        this.parent = null;
        this.jarFiles = new Hashtable<>();
        this.savedContextLoader = null;
        this.isContextLoaderSaved = false;
        setParent(null);
        setProject(project);
        setClassPath(path);
    }

    public AntClassLoader(ClassLoader classLoader, Project project, Path path, boolean z) {
        this(project, path);
        if (classLoader != null) {
            setParent(classLoader);
        }
        setParentFirst(z);
        addJavaLibraries();
    }

    public AntClassLoader(Project project, Path path, boolean z) {
        this(null, project, path, z);
    }

    public AntClassLoader(ClassLoader classLoader, boolean z) {
        this.pathComponents = new VectorSet();
        this.parentFirst = true;
        this.systemPackages = new Vector<>();
        this.loaderPackages = new Vector<>();
        this.ignoreBase = false;
        this.parent = null;
        this.jarFiles = new Hashtable<>();
        this.savedContextLoader = null;
        this.isContextLoaderSaved = false;
        setParent(classLoader);
        this.project = null;
        this.parentFirst = z;
    }

    public void setProject(Project project) {
        this.project = project;
        if (project != null) {
            project.addBuildListener(this);
        }
    }

    public void setClassPath(Path path) throws Throwable {
        this.pathComponents.removeAllElements();
        if (path != null) {
            for (String str : path.concatSystemClasspath(Definer.OnError.POLICY_IGNORE).list()) {
                try {
                    addPathElement(str);
                } catch (BuildException unused) {
                }
            }
        }
    }

    public void setParent(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = AntClassLoader.class.getClassLoader();
        }
        this.parent = classLoader;
    }

    public void setParentFirst(boolean z) {
        this.parentFirst = z;
    }

    protected void log(String str, int i) {
        Project project = this.project;
        if (project != null) {
            project.log(str, i);
        }
    }

    public void setThreadContextLoader() {
        if (this.isContextLoaderSaved) {
            throw new BuildException("Context loader has not been reset");
        }
        if (LoaderUtils.isContextLoaderAvailable()) {
            this.savedContextLoader = LoaderUtils.getContextClassLoader();
            Project project = this.project;
            LoaderUtils.setContextClassLoader((project == null || !"only".equals(project.getProperty(MagicNames.BUILD_SYSCLASSPATH))) ? this : getClass().getClassLoader());
            this.isContextLoaderSaved = true;
        }
    }

    public void resetThreadContextLoader() {
        if (LoaderUtils.isContextLoaderAvailable() && this.isContextLoaderSaved) {
            LoaderUtils.setContextClassLoader(this.savedContextLoader);
            this.savedContextLoader = null;
            this.isContextLoaderSaved = false;
        }
    }

    public void addPathElement(String str) throws Throwable {
        Project project = this.project;
        try {
            addPathFile(project != null ? project.resolveFile(str) : new File(str));
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    public void addPathComponent(File file) {
        if (this.pathComponents.contains(file)) {
            return;
        }
        this.pathComponents.addElement(file);
    }

    protected void addPathFile(File file) throws Throwable {
        JarFile jarFile;
        if (!this.pathComponents.contains(file)) {
            this.pathComponents.addElement(file);
        }
        if (file.isDirectory()) {
            return;
        }
        String str = file.getAbsolutePath() + file.lastModified() + "-" + file.length();
        String value = pathMap.get(str);
        if (value == null) {
            JarFile jarFile2 = null;
            try {
                jarFile = new JarFile(file);
            } catch (Throwable th) {
                th = th;
            }
            try {
                Manifest manifest = jarFile.getManifest();
                if (manifest != null) {
                    value = manifest.getMainAttributes().getValue(Attributes.Name.CLASS_PATH);
                    jarFile.close();
                    if (value == null) {
                        value = "";
                    }
                    pathMap.put(str, value);
                } else {
                    jarFile.close();
                    return;
                }
            } catch (Throwable th2) {
                th = th2;
                jarFile2 = jarFile;
                if (jarFile2 != null) {
                    jarFile2.close();
                }
                throw th;
            }
        }
        if ("".equals(value)) {
            return;
        }
        URL fileURL = FILE_UTILS.getFileURL(file);
        StringTokenizer stringTokenizer = new StringTokenizer(value);
        while (stringTokenizer.hasMoreTokens()) {
            String strNextToken = stringTokenizer.nextToken();
            URL url = new URL(fileURL, strNextToken);
            if (!url.getProtocol().equals("file")) {
                log("Skipping jar library " + strNextToken + " since only relative URLs are supported by this loader", 3);
            } else {
                File file2 = new File(Locator.decodeUri(url.getFile()));
                if (file2.exists() && !isInPath(file2)) {
                    addPathFile(file2);
                }
            }
        }
    }

    public String getClasspath() {
        StringBuilder sb = new StringBuilder();
        Enumeration<File> enumerationElements = this.pathComponents.elements();
        boolean z = true;
        while (enumerationElements.hasMoreElements()) {
            if (z) {
                z = false;
            } else {
                sb.append(System.getProperty("path.separator"));
            }
            sb.append(enumerationElements.nextElement().getAbsolutePath());
        }
        return sb.toString();
    }

    public synchronized void setIsolated(boolean z) {
        this.ignoreBase = z;
    }

    public static void initializeClass(Class<?> cls) {
        Constructor<?>[] declaredConstructors = cls.getDeclaredConstructors();
        if (declaredConstructors == null || declaredConstructors.length <= 0 || declaredConstructors[0] == null) {
            return;
        }
        try {
            declaredConstructors[0].newInstance(new String[256]);
        } catch (Exception unused) {
        }
    }

    public void addSystemPackageRoot(String str) {
        this.systemPackages.addElement(str + (str.endsWith(".") ? "" : "."));
    }

    public void addLoaderPackageRoot(String str) {
        this.loaderPackages.addElement(str + (str.endsWith(".") ? "" : "."));
    }

    public Class<?> forceLoadClass(String str) throws ClassNotFoundException {
        log("force loading " + str, 4);
        Class<?> clsFindLoadedClass = findLoadedClass(str);
        return clsFindLoadedClass == null ? findClass(str) : clsFindLoadedClass;
    }

    public Class<?> forceLoadSystemClass(String str) throws ClassNotFoundException {
        log("force system loading " + str, 4);
        Class<?> clsFindLoadedClass = findLoadedClass(str);
        return clsFindLoadedClass == null ? findBaseClass(str) : clsFindLoadedClass;
    }

    @Override // java.lang.ClassLoader
    public InputStream getResourceAsStream(String str) {
        InputStream inputStreamLoadBaseResource = isParentFirst(str) ? loadBaseResource(str) : null;
        if (inputStreamLoadBaseResource != null) {
            log("ResourceStream for " + str + " loaded from parent loader", 4);
        } else {
            inputStreamLoadBaseResource = loadResource(str);
            if (inputStreamLoadBaseResource != null) {
                log("ResourceStream for " + str + " loaded from ant loader", 4);
            }
        }
        if (inputStreamLoadBaseResource == null && !isParentFirst(str)) {
            if (this.ignoreBase) {
                inputStreamLoadBaseResource = getRootLoader() != null ? getRootLoader().getResourceAsStream(str) : null;
            } else {
                inputStreamLoadBaseResource = loadBaseResource(str);
            }
            if (inputStreamLoadBaseResource != null) {
                log("ResourceStream for " + str + " loaded from parent loader", 4);
            }
        }
        if (inputStreamLoadBaseResource == null) {
            log("Couldn't load ResourceStream for " + str, 4);
        }
        return inputStreamLoadBaseResource;
    }

    private InputStream loadResource(String str) {
        Enumeration<File> enumerationElements = this.pathComponents.elements();
        InputStream resourceStream = null;
        while (enumerationElements.hasMoreElements() && resourceStream == null) {
            resourceStream = getResourceStream(enumerationElements.nextElement(), str);
        }
        return resourceStream;
    }

    private InputStream loadBaseResource(String str) {
        ClassLoader classLoader = this.parent;
        return classLoader == null ? super.getResourceAsStream(str) : classLoader.getResourceAsStream(str);
    }

    private InputStream getResourceStream(File file, String str) {
        try {
            JarFile jarFile = this.jarFiles.get(file);
            if (jarFile == null && file.isDirectory()) {
                File file2 = new File(file, str);
                if (file2.exists()) {
                    return new FileInputStream(file2);
                }
            } else {
                if (jarFile == null) {
                    if (!file.exists()) {
                        return null;
                    }
                    this.jarFiles.put(file, new JarFile(file));
                    jarFile = this.jarFiles.get(file);
                }
                JarEntry jarEntry = jarFile.getJarEntry(str);
                if (jarEntry != null) {
                    return jarFile.getInputStream(jarEntry);
                }
            }
        } catch (Exception e) {
            log("Ignoring Exception " + e.getClass().getName() + ": " + e.getMessage() + " reading resource " + str + " from " + file, 3);
        }
        return null;
    }

    private boolean isParentFirst(String str) {
        boolean z = this.parentFirst;
        Enumeration<String> enumerationElements = this.systemPackages.elements();
        while (true) {
            if (!enumerationElements.hasMoreElements()) {
                break;
            }
            if (str.startsWith(enumerationElements.nextElement())) {
                z = true;
                break;
            }
        }
        Enumeration<String> enumerationElements2 = this.loaderPackages.elements();
        while (enumerationElements2.hasMoreElements()) {
            if (str.startsWith(enumerationElements2.nextElement())) {
                return false;
            }
        }
        return z;
    }

    private ClassLoader getRootLoader() {
        ClassLoader classLoader = getClass().getClassLoader();
        while (classLoader != null && classLoader.getParent() != null) {
            classLoader = classLoader.getParent();
        }
        return classLoader;
    }

    @Override // java.lang.ClassLoader
    public URL getResource(String str) {
        URL resourceURL;
        if (isParentFirst(str)) {
            ClassLoader classLoader = this.parent;
            resourceURL = classLoader == null ? super.getResource(str) : classLoader.getResource(str);
        } else {
            resourceURL = null;
        }
        if (resourceURL != null) {
            log("Resource " + str + " loaded from parent loader", 4);
        } else {
            Enumeration<File> enumerationElements = this.pathComponents.elements();
            while (enumerationElements.hasMoreElements() && resourceURL == null) {
                resourceURL = getResourceURL(enumerationElements.nextElement(), str);
                if (resourceURL != null) {
                    log("Resource " + str + " loaded from ant loader", 4);
                }
            }
        }
        if (resourceURL == null && !isParentFirst(str)) {
            if (this.ignoreBase) {
                resourceURL = getRootLoader() != null ? getRootLoader().getResource(str) : null;
            } else {
                ClassLoader classLoader2 = this.parent;
                resourceURL = classLoader2 == null ? super.getResource(str) : classLoader2.getResource(str);
            }
            if (resourceURL != null) {
                log("Resource " + str + " loaded from parent loader", 4);
            }
        }
        if (resourceURL == null) {
            log("Couldn't load Resource " + str, 4);
        }
        return resourceURL;
    }

    public Enumeration<URL> getNamedResources(String str) throws IOException {
        return findResources(str, false);
    }

    @Override // java.lang.ClassLoader
    protected Enumeration<URL> findResources(String str) throws IOException {
        return findResources(str, true);
    }

    protected Enumeration<URL> findResources(String str, boolean z) throws IOException {
        Enumeration<URL> emptyEnumeration;
        ResourceEnumeration resourceEnumeration = new ResourceEnumeration(str);
        ClassLoader classLoader = this.parent;
        if (classLoader != null && (!z || classLoader != getParent())) {
            emptyEnumeration = this.parent.getResources(str);
        } else {
            emptyEnumeration = new CollectionUtils.EmptyEnumeration<>();
        }
        if (isParentFirst(str)) {
            return CollectionUtils.append(emptyEnumeration, resourceEnumeration);
        }
        if (this.ignoreBase) {
            return getRootLoader() == null ? resourceEnumeration : CollectionUtils.append(resourceEnumeration, getRootLoader().getResources(str));
        }
        return CollectionUtils.append(resourceEnumeration, emptyEnumeration);
    }

    protected URL getResourceURL(File file, String str) {
        try {
            JarFile jarFile = this.jarFiles.get(file);
            if (jarFile == null && file.isDirectory()) {
                File file2 = new File(file, str);
                if (file2.exists()) {
                    try {
                        return FILE_UTILS.getFileURL(file2);
                    } catch (MalformedURLException unused) {
                        return null;
                    }
                }
            } else {
                if (jarFile == null) {
                    if (!file.exists()) {
                        return null;
                    }
                    if (!isZip(file)) {
                        String str2 = "CLASSPATH element " + file + " is not a JAR.";
                        log(str2, 1);
                        System.err.println(str2);
                        return null;
                    }
                    this.jarFiles.put(file, new JarFile(file));
                    jarFile = this.jarFiles.get(file);
                }
                JarEntry jarEntry = jarFile.getJarEntry(str);
                if (jarEntry != null) {
                    try {
                        return new URL("jar:" + FILE_UTILS.getFileURL(file) + "!/" + jarEntry);
                    } catch (MalformedURLException unused2) {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            String str3 = "Unable to obtain resource from " + file + ": ";
            log(str3 + e, 1);
            System.err.println(str3);
            e.printStackTrace();
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x00a6 A[Catch: all -> 0x00ac, TRY_LEAVE, TryCatch #2 {, blocks: (B:3:0x0001, B:7:0x0009, B:10:0x0010, B:21:0x00a6, B:14:0x005e, B:12:0x0037, B:17:0x0080, B:19:0x0084, B:24:0x00ab), top: B:32:0x0001, inners: #0, #1 }] */
    @Override // java.lang.ClassLoader
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected synchronized java.lang.Class<?> loadClass(java.lang.String r5, boolean r6) throws java.lang.ClassNotFoundException {
        /*
            r4 = this;
            monitor-enter(r4)
            java.lang.Class r0 = r4.findLoadedClass(r5)     // Catch: java.lang.Throwable -> Lac
            if (r0 == 0) goto L9
            monitor-exit(r4)
            return r0
        L9:
            boolean r0 = r4.isParentFirst(r5)     // Catch: java.lang.Throwable -> Lac
            r1 = 4
            if (r0 == 0) goto L5e
            java.lang.Class r0 = r4.findBaseClass(r5)     // Catch: java.lang.ClassNotFoundException -> L37 java.lang.Throwable -> Lac
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.ClassNotFoundException -> L37 java.lang.Throwable -> Lac
            r2.<init>()     // Catch: java.lang.ClassNotFoundException -> L37 java.lang.Throwable -> Lac
            java.lang.String r3 = "Class "
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.ClassNotFoundException -> L37 java.lang.Throwable -> Lac
            java.lang.StringBuilder r2 = r2.append(r5)     // Catch: java.lang.ClassNotFoundException -> L37 java.lang.Throwable -> Lac
            java.lang.String r3 = " loaded from parent loader "
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.ClassNotFoundException -> L37 java.lang.Throwable -> Lac
            java.lang.String r3 = "(parentFirst)"
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.ClassNotFoundException -> L37 java.lang.Throwable -> Lac
            java.lang.String r2 = r2.toString()     // Catch: java.lang.ClassNotFoundException -> L37 java.lang.Throwable -> Lac
            r4.log(r2, r1)     // Catch: java.lang.ClassNotFoundException -> L37 java.lang.Throwable -> Lac
            goto La4
        L37:
            java.lang.Class r0 = r4.findClass(r5)     // Catch: java.lang.Throwable -> Lac
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lac
            r2.<init>()     // Catch: java.lang.Throwable -> Lac
            java.lang.String r3 = "Class "
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> Lac
            java.lang.StringBuilder r5 = r2.append(r5)     // Catch: java.lang.Throwable -> Lac
            java.lang.String r2 = " loaded from ant loader "
            java.lang.StringBuilder r5 = r5.append(r2)     // Catch: java.lang.Throwable -> Lac
            java.lang.String r2 = "(parentFirst)"
            java.lang.StringBuilder r5 = r5.append(r2)     // Catch: java.lang.Throwable -> Lac
            java.lang.String r5 = r5.toString()     // Catch: java.lang.Throwable -> Lac
            r4.log(r5, r1)     // Catch: java.lang.Throwable -> Lac
            goto La4
        L5e:
            java.lang.Class r0 = r4.findClass(r5)     // Catch: java.lang.ClassNotFoundException -> L7f java.lang.Throwable -> Lac
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.ClassNotFoundException -> L7f java.lang.Throwable -> Lac
            r2.<init>()     // Catch: java.lang.ClassNotFoundException -> L7f java.lang.Throwable -> Lac
            java.lang.String r3 = "Class "
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.ClassNotFoundException -> L7f java.lang.Throwable -> Lac
            java.lang.StringBuilder r2 = r2.append(r5)     // Catch: java.lang.ClassNotFoundException -> L7f java.lang.Throwable -> Lac
            java.lang.String r3 = " loaded from ant loader"
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.ClassNotFoundException -> L7f java.lang.Throwable -> Lac
            java.lang.String r2 = r2.toString()     // Catch: java.lang.ClassNotFoundException -> L7f java.lang.Throwable -> Lac
            r4.log(r2, r1)     // Catch: java.lang.ClassNotFoundException -> L7f java.lang.Throwable -> Lac
            goto La4
        L7f:
            r0 = move-exception
            boolean r2 = r4.ignoreBase     // Catch: java.lang.Throwable -> Lac
            if (r2 != 0) goto Lab
            java.lang.Class r0 = r4.findBaseClass(r5)     // Catch: java.lang.Throwable -> Lac
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lac
            r2.<init>()     // Catch: java.lang.Throwable -> Lac
            java.lang.String r3 = "Class "
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> Lac
            java.lang.StringBuilder r5 = r2.append(r5)     // Catch: java.lang.Throwable -> Lac
            java.lang.String r2 = " loaded from parent loader"
            java.lang.StringBuilder r5 = r5.append(r2)     // Catch: java.lang.Throwable -> Lac
            java.lang.String r5 = r5.toString()     // Catch: java.lang.Throwable -> Lac
            r4.log(r5, r1)     // Catch: java.lang.Throwable -> Lac
        La4:
            if (r6 == 0) goto La9
            r4.resolveClass(r0)     // Catch: java.lang.Throwable -> Lac
        La9:
            monitor-exit(r4)
            return r0
        Lab:
            throw r0     // Catch: java.lang.Throwable -> Lac
        Lac:
            r5 = move-exception
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.AntClassLoader.loadClass(java.lang.String, boolean):java.lang.Class");
    }

    private String getClassFilename(String str) {
        return str.replace('.', '/') + ".class";
    }

    protected Class<?> defineClassFromData(File file, byte[] bArr, String str) throws IOException {
        definePackage(file, str);
        ProtectionDomain protectionDomain = Project.class.getProtectionDomain();
        return defineClass(str, bArr, 0, bArr.length, new ProtectionDomain(new CodeSource(FILE_UTILS.getFileURL(file), getCertificates(file, getClassFilename(str))), protectionDomain.getPermissions(), this, protectionDomain.getPrincipals()));
    }

    protected void definePackage(File file, String str) throws IOException {
        int iLastIndexOf = str.lastIndexOf(46);
        if (iLastIndexOf == -1) {
            return;
        }
        String strSubstring = str.substring(0, iLastIndexOf);
        if (getPackage(strSubstring) != null) {
            return;
        }
        Manifest jarManifest = getJarManifest(file);
        if (jarManifest == null) {
            definePackage(strSubstring, null, null, null, null, null, null, null);
        } else {
            definePackage(file, strSubstring, jarManifest);
        }
    }

    private Manifest getJarManifest(File file) throws IOException {
        JarFile jarFile;
        if (file.isDirectory() || (jarFile = this.jarFiles.get(file)) == null) {
            return null;
        }
        return jarFile.getManifest();
    }

    private Certificate[] getCertificates(File file, String str) throws IOException {
        JarFile jarFile;
        JarEntry jarEntry;
        if (file.isDirectory() || (jarFile = this.jarFiles.get(file)) == null || (jarEntry = jarFile.getJarEntry(str)) == null) {
            return null;
        }
        return jarEntry.getCertificates();
    }

    protected void definePackage(File file, String str, Manifest manifest) {
        String value;
        String value2;
        String value3;
        String value4;
        String value5;
        String value6;
        String value7;
        URL url;
        Attributes attributes = manifest.getAttributes(str.replace('.', '/') + "/");
        if (attributes != null) {
            value2 = attributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
            value3 = attributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
            value4 = attributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
            value5 = attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
            value6 = attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
            value7 = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            value = attributes.getValue(Attributes.Name.SEALED);
        } else {
            value = null;
            value2 = null;
            value3 = null;
            value4 = null;
            value5 = null;
            value6 = null;
            value7 = null;
        }
        Attributes mainAttributes = manifest.getMainAttributes();
        if (mainAttributes != null) {
            if (value2 == null) {
                value2 = mainAttributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
            }
            if (value3 == null) {
                value3 = mainAttributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
            }
            if (value4 == null) {
                value4 = mainAttributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
            }
            if (value5 == null) {
                value5 = mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
            }
            if (value6 == null) {
                value6 = mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
            }
            if (value7 == null) {
                value7 = mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            }
            if (value == null) {
                value = mainAttributes.getValue(Attributes.Name.SEALED);
            }
        }
        String str2 = value6;
        String str3 = value7;
        String str4 = value3;
        String str5 = value5;
        String str6 = value4;
        String str7 = value2;
        if (value == null || !value.equalsIgnoreCase("true")) {
            url = null;
        } else {
            try {
                url = new URL(FileUtils.getFileUtils().toURI(file.getAbsolutePath()));
            } catch (MalformedURLException unused) {
                url = null;
            }
        }
        definePackage(str, str7, str6, str4, str5, str3, str2, url);
    }

    private Class<?> getClassFromStream(InputStream inputStream, String str, File file) throws IOException, SecurityException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[8192];
        while (true) {
            int i = inputStream.read(bArr, 0, 8192);
            if (i != -1) {
                byteArrayOutputStream.write(bArr, 0, i);
            } else {
                return defineClassFromData(file, byteArrayOutputStream.toByteArray(), str);
            }
        }
    }

    @Override // java.lang.ClassLoader
    public Class<?> findClass(String str) throws ClassNotFoundException {
        log("Finding class " + str, 4);
        return findClassInComponents(str);
    }

    protected boolean isInPath(File file) {
        return this.pathComponents.contains(file);
    }

    private Class<?> findClassInComponents(String str) throws ClassNotFoundException {
        String classFilename = getClassFilename(str);
        Enumeration<File> enumerationElements = this.pathComponents.elements();
        while (enumerationElements.hasMoreElements()) {
            File fileNextElement = enumerationElements.nextElement();
            InputStream resourceStream = null;
            try {
                try {
                    resourceStream = getResourceStream(fileNextElement, classFilename);
                } catch (IOException e) {
                    log("Exception reading component " + fileNextElement + " (reason: " + e.getMessage() + ")", 3);
                } catch (SecurityException e2) {
                    throw e2;
                }
                if (resourceStream != null) {
                    log("Loaded from " + fileNextElement + " " + classFilename, 4);
                    return getClassFromStream(resourceStream, str, fileNextElement);
                }
                continue;
            } finally {
                FileUtils.close(resourceStream);
            }
        }
        throw new ClassNotFoundException(str);
    }

    private Class<?> findBaseClass(String str) throws ClassNotFoundException {
        ClassLoader classLoader = this.parent;
        return classLoader == null ? findSystemClass(str) : classLoader.loadClass(str);
    }

    public synchronized void cleanup() {
        Enumeration<JarFile> enumerationElements = this.jarFiles.elements();
        while (enumerationElements.hasMoreElements()) {
            try {
                enumerationElements.nextElement().close();
            } catch (IOException unused) {
            }
        }
        this.jarFiles = new Hashtable<>();
        Project project = this.project;
        if (project != null) {
            project.removeBuildListener(this);
        }
        this.project = null;
    }

    public ClassLoader getConfiguredParent() {
        return this.parent;
    }

    @Override // org.apache.tools.ant.BuildListener
    public void buildFinished(BuildEvent buildEvent) {
        cleanup();
    }

    @Override // org.apache.tools.ant.SubBuildListener
    public void subBuildFinished(BuildEvent buildEvent) {
        if (buildEvent.getProject() == this.project) {
            cleanup();
        }
    }

    public void addJavaLibraries() {
        Enumeration<String> enumerationElements = JavaEnvUtils.getJrePackages().elements();
        while (enumerationElements.hasMoreElements()) {
            addSystemPackageRoot(enumerationElements.nextElement());
        }
    }

    public String toString() {
        return "AntClassLoader[" + getClasspath() + "]";
    }

    public static AntClassLoader newAntClassLoader(ClassLoader classLoader, Project project, Path path, boolean z) {
        Class<?> cls = subClassToLoad;
        return cls != null ? (AntClassLoader) ReflectUtil.newInstance(cls, CONSTRUCTOR_ARGS, new Object[]{classLoader, project, path, Boolean.valueOf(z)}) : new AntClassLoader(classLoader, project, path, z);
    }

    private static boolean isZip(File file) throws IOException {
        byte[] bArr = new byte[4];
        if (!readFully(file, bArr)) {
            return false;
        }
        ZipLong zipLong = new ZipLong(bArr);
        return ZipLong.LFH_SIG.equals(zipLong) || EOCD_SIG.equals(zipLong) || ZipLong.DD_SIG.equals(zipLong) || SINGLE_SEGMENT_SPLIT_MARKER.equals(zipLong);
    }

    private static boolean readFully(File file, byte[] bArr) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            int length = bArr.length;
            int i = 0;
            while (i != length) {
                int i2 = fileInputStream.read(bArr, i, length - i);
                if (i2 == -1) {
                    break;
                }
                i += i2;
            }
            return i == length;
        } finally {
            fileInputStream.close();
        }
    }
}
