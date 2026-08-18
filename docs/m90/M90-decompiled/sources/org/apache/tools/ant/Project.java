package org.apache.tools.ant;

import android.hardware.Camera;
import android.text.format.DateFormat;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.Vector;
import java.util.WeakHashMap;
import org.apache.tools.ant.helper.DefaultExecutor;
import org.apache.tools.ant.input.DefaultInputHandler;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.launch.Locator;
import org.apache.tools.ant.types.Description;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceFactory;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.CollectionUtils;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.util.VectorSet;

/* JADX INFO: loaded from: classes3.dex */
public class Project implements ResourceFactory {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    public static final String JAVA_1_0 = "1.0";
    public static final String JAVA_1_1 = "1.1";
    public static final String JAVA_1_2 = "1.2";
    public static final String JAVA_1_3 = "1.3";
    public static final String JAVA_1_4 = "1.4";
    public static final int MSG_DEBUG = 4;
    public static final int MSG_ERR = 0;
    public static final int MSG_INFO = 2;
    public static final int MSG_VERBOSE = 3;
    public static final int MSG_WARN = 1;
    public static final String TOKEN_END = "@";
    public static final String TOKEN_START = "@";
    private static final String VISITED = "VISITED";
    private static final String VISITING = "VISITING";
    private File baseDir;
    private ClassLoader coreLoader;
    private InputStream defaultInputStream;
    private String defaultTarget;
    private String description;
    private FilterSet globalFilterSet;
    private FilterSetCollection globalFilters;
    private InputHandler inputHandler;
    private final ThreadLocal<Boolean> isLoggingMessage;
    private boolean keepGoingMode;
    private volatile BuildListener[] listeners;
    private final Object listenersLock;
    private String name;
    private final Map<ThreadGroup, Task> threadGroupTasks;
    private final Map<Thread, Task> threadTasks;
    private Hashtable<String, Object> references = new AntRefTable();
    private HashMap<String, Object> idReferences = new HashMap<>();
    private Hashtable<String, Target> targets = new Hashtable<>();

    public void inheritIDReferences(Project project) {
    }

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    public void setDefaultInputStream(InputStream inputStream) {
        this.defaultInputStream = inputStream;
    }

    public InputStream getDefaultInputStream() {
        return this.defaultInputStream;
    }

    public InputHandler getInputHandler() {
        return this.inputHandler;
    }

    public Project() {
        FilterSet filterSet = new FilterSet();
        this.globalFilterSet = filterSet;
        filterSet.setProject(this);
        this.globalFilters = new FilterSetCollection(this.globalFilterSet);
        this.listenersLock = new Object();
        this.listeners = new BuildListener[0];
        this.isLoggingMessage = new ThreadLocal<Boolean>() { // from class: org.apache.tools.ant.Project.1
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.lang.ThreadLocal
            public Boolean initialValue() {
                return Boolean.FALSE;
            }
        };
        this.coreLoader = null;
        this.threadTasks = Collections.synchronizedMap(new WeakHashMap());
        this.threadGroupTasks = Collections.synchronizedMap(new WeakHashMap());
        this.inputHandler = null;
        this.defaultInputStream = null;
        this.keepGoingMode = false;
        this.inputHandler = new DefaultInputHandler();
    }

    public Project createSubProject() {
        Project project;
        try {
            project = (Project) getClass().newInstance();
        } catch (Exception unused) {
            project = new Project();
        }
        initSubProject(project);
        return project;
    }

    public void initSubProject(Project project) {
        ComponentHelper.getComponentHelper(project).initSubProject(ComponentHelper.getComponentHelper(this));
        project.setDefaultInputStream(getDefaultInputStream());
        project.setKeepGoingMode(isKeepGoingMode());
        project.setExecutor(getExecutor().getSubProjectExecutor());
    }

    public void init() throws BuildException {
        initProperties();
        ComponentHelper.getComponentHelper(this).initDefaultDefinitions();
    }

    public void initProperties() throws BuildException {
        setJavaVersionProperty();
        setSystemProperties();
        setPropertyInternal(MagicNames.ANT_VERSION, Main.getAntVersion());
        setAntLib();
    }

    private void setAntLib() {
        File classSource = Locator.getClassSource(Project.class);
        if (classSource != null) {
            setPropertyInternal(MagicNames.ANT_LIB, classSource.getAbsolutePath());
        }
    }

    public AntClassLoader createClassLoader(Path path) {
        return AntClassLoader.newAntClassLoader(getClass().getClassLoader(), this, path, true);
    }

    public AntClassLoader createClassLoader(ClassLoader classLoader, Path path) {
        return AntClassLoader.newAntClassLoader(classLoader, this, path, true);
    }

    public void setCoreLoader(ClassLoader classLoader) {
        this.coreLoader = classLoader;
    }

    public ClassLoader getCoreLoader() {
        return this.coreLoader;
    }

    public void addBuildListener(BuildListener buildListener) {
        synchronized (this.listenersLock) {
            for (int i = 0; i < this.listeners.length; i++) {
                if (this.listeners[i] == buildListener) {
                    return;
                }
            }
            BuildListener[] buildListenerArr = new BuildListener[this.listeners.length + 1];
            System.arraycopy(this.listeners, 0, buildListenerArr, 0, this.listeners.length);
            buildListenerArr[this.listeners.length] = buildListener;
            this.listeners = buildListenerArr;
        }
    }

    public void removeBuildListener(BuildListener buildListener) {
        synchronized (this.listenersLock) {
            int i = 0;
            while (true) {
                if (i >= this.listeners.length) {
                    break;
                }
                if (this.listeners[i] == buildListener) {
                    BuildListener[] buildListenerArr = new BuildListener[this.listeners.length - 1];
                    System.arraycopy(this.listeners, 0, buildListenerArr, 0, i);
                    System.arraycopy(this.listeners, i + 1, buildListenerArr, i, (this.listeners.length - i) - 1);
                    this.listeners = buildListenerArr;
                    break;
                }
                i++;
            }
        }
    }

    public Vector<BuildListener> getBuildListeners() {
        Vector<BuildListener> vector;
        synchronized (this.listenersLock) {
            vector = new Vector<>(this.listeners.length);
            for (int i = 0; i < this.listeners.length; i++) {
                vector.add(this.listeners[i]);
            }
        }
        return vector;
    }

    public void log(String str) {
        log(str, 2);
    }

    public void log(String str, int i) {
        log(str, (Throwable) null, i);
    }

    public void log(String str, Throwable th, int i) {
        fireMessageLogged(this, str, th, i);
    }

    public void log(Task task, String str, int i) {
        fireMessageLogged(task, str, (Throwable) null, i);
    }

    public void log(Task task, String str, Throwable th, int i) {
        fireMessageLogged(task, str, th, i);
    }

    public void log(Target target, String str, int i) {
        log(target, str, (Throwable) null, i);
    }

    public void log(Target target, String str, Throwable th, int i) {
        fireMessageLogged(target, str, th, i);
    }

    public FilterSet getGlobalFilterSet() {
        return this.globalFilterSet;
    }

    public void setProperty(String str, String str2) {
        PropertyHelper.getPropertyHelper(this).setProperty(str, (Object) str2, true);
    }

    public void setNewProperty(String str, String str2) {
        PropertyHelper.getPropertyHelper(this).setNewProperty(str, str2);
    }

    public void setUserProperty(String str, String str2) {
        PropertyHelper.getPropertyHelper(this).setUserProperty(str, str2);
    }

    public void setInheritedProperty(String str, String str2) {
        PropertyHelper.getPropertyHelper(this).setInheritedProperty(str, str2);
    }

    private void setPropertyInternal(String str, String str2) {
        PropertyHelper.getPropertyHelper(this).setProperty(str, (Object) str2, false);
    }

    public String getProperty(String str) {
        Object property = PropertyHelper.getPropertyHelper(this).getProperty(str);
        if (property == null) {
            return null;
        }
        return String.valueOf(property);
    }

    public String replaceProperties(String str) throws BuildException {
        return PropertyHelper.getPropertyHelper(this).replaceProperties(null, str, null);
    }

    public String getUserProperty(String str) {
        return (String) PropertyHelper.getPropertyHelper(this).getUserProperty(str);
    }

    public Hashtable<String, Object> getProperties() {
        return PropertyHelper.getPropertyHelper(this).getProperties();
    }

    public Hashtable<String, Object> getUserProperties() {
        return PropertyHelper.getPropertyHelper(this).getUserProperties();
    }

    public Hashtable<String, Object> getInheritedProperties() {
        return PropertyHelper.getPropertyHelper(this).getInheritedProperties();
    }

    public void copyUserProperties(Project project) {
        PropertyHelper.getPropertyHelper(this).copyUserProperties(project);
    }

    public void copyInheritedProperties(Project project) {
        PropertyHelper.getPropertyHelper(this).copyInheritedProperties(project);
    }

    public void setDefaultTarget(String str) {
        setDefault(str);
    }

    public String getDefaultTarget() {
        return this.defaultTarget;
    }

    public void setDefault(String str) {
        if (str != null) {
            setUserProperty(MagicNames.PROJECT_DEFAULT_TARGET, str);
        }
        this.defaultTarget = str;
    }

    public void setName(String str) {
        setUserProperty(MagicNames.PROJECT_NAME, str);
        this.name = str;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public String getDescription() {
        if (this.description == null) {
            this.description = Description.getDescription(this);
        }
        return this.description;
    }

    public void addFilter(String str, String str2) {
        if (str == null) {
            return;
        }
        this.globalFilterSet.addFilter(new FilterSet.Filter(str, str2));
    }

    public Hashtable<String, String> getFilters() {
        return this.globalFilterSet.getFilterHash();
    }

    public void setBasedir(String str) throws BuildException {
        setBaseDir(new File(str));
    }

    public void setBaseDir(File file) throws BuildException {
        File fileNormalize = FILE_UTILS.normalize(file.getAbsolutePath());
        if (!fileNormalize.exists()) {
            throw new BuildException("Basedir " + fileNormalize.getAbsolutePath() + " does not exist");
        }
        if (!fileNormalize.isDirectory()) {
            throw new BuildException("Basedir " + fileNormalize.getAbsolutePath() + " is not a directory");
        }
        this.baseDir = fileNormalize;
        setPropertyInternal(MagicNames.PROJECT_BASEDIR, fileNormalize.getPath());
        log("Project base dir set to: " + this.baseDir, 3);
    }

    public File getBaseDir() {
        if (this.baseDir == null) {
            try {
                setBasedir(".");
            } catch (BuildException e) {
                e.printStackTrace();
            }
        }
        return this.baseDir;
    }

    public void setKeepGoingMode(boolean z) {
        this.keepGoingMode = z;
    }

    public boolean isKeepGoingMode() {
        return this.keepGoingMode;
    }

    public static String getJavaVersion() {
        return JavaEnvUtils.getJavaVersion();
    }

    public void setJavaVersionProperty() throws BuildException {
        String javaVersion = JavaEnvUtils.getJavaVersion();
        setPropertyInternal(MagicNames.ANT_JAVA_VERSION, javaVersion);
        if (!JavaEnvUtils.isAtLeastJavaVersion("1.4")) {
            throw new BuildException("Ant cannot work on Java prior to 1.4");
        }
        log("Detected Java version: " + javaVersion + " in: " + System.getProperty("java.home"), 3);
        log("Detected OS: " + System.getProperty("os.name"), 3);
    }

    public void setSystemProperties() {
        Properties properties = System.getProperties();
        Enumeration<?> enumerationPropertyNames = properties.propertyNames();
        while (enumerationPropertyNames.hasMoreElements()) {
            String str = (String) enumerationPropertyNames.nextElement();
            String property = properties.getProperty(str);
            if (property != null) {
                setPropertyInternal(str, property);
            }
        }
    }

    public void addTaskDefinition(String str, Class<?> cls) throws BuildException {
        ComponentHelper.getComponentHelper(this).addTaskDefinition(str, cls);
    }

    public void checkTaskClass(Class<?> cls) throws BuildException {
        ComponentHelper.getComponentHelper(this).checkTaskClass(cls);
        if (!Modifier.isPublic(cls.getModifiers())) {
            String str = cls + " is not public";
            log(str, 0);
            throw new BuildException(str);
        }
        if (Modifier.isAbstract(cls.getModifiers())) {
            String str2 = cls + " is abstract";
            log(str2, 0);
            throw new BuildException(str2);
        }
        try {
            cls.getConstructor(new Class[0]);
            if (Task.class.isAssignableFrom(cls)) {
                return;
            }
            TaskAdapter.checkTaskClass(cls, this);
        } catch (LinkageError e) {
            String str3 = "Could not load " + cls + ": " + e;
            log(str3, 0);
            throw new BuildException(str3, e);
        } catch (NoSuchMethodException unused) {
            String str4 = "No public no-arg constructor in " + cls;
            log(str4, 0);
            throw new BuildException(str4);
        }
    }

    public Hashtable<String, Class<?>> getTaskDefinitions() {
        return ComponentHelper.getComponentHelper(this).getTaskDefinitions();
    }

    public Map<String, Class<?>> getCopyOfTaskDefinitions() {
        return new HashMap(getTaskDefinitions());
    }

    public void addDataTypeDefinition(String str, Class<?> cls) {
        ComponentHelper.getComponentHelper(this).addDataTypeDefinition(str, cls);
    }

    public Hashtable<String, Class<?>> getDataTypeDefinitions() {
        return ComponentHelper.getComponentHelper(this).getDataTypeDefinitions();
    }

    public Map<String, Class<?>> getCopyOfDataTypeDefinitions() {
        return new HashMap(getDataTypeDefinitions());
    }

    public void addTarget(Target target) throws BuildException {
        addTarget(target.getName(), target);
    }

    public void addTarget(String str, Target target) throws BuildException {
        if (this.targets.get(str) != null) {
            throw new BuildException("Duplicate target: `" + str + "'");
        }
        addOrReplaceTarget(str, target);
    }

    public void addOrReplaceTarget(Target target) {
        addOrReplaceTarget(target.getName(), target);
    }

    public void addOrReplaceTarget(String str, Target target) {
        log(" +Target: " + str, 4);
        target.setProject(this);
        this.targets.put(str, target);
    }

    public Hashtable<String, Target> getTargets() {
        return this.targets;
    }

    public Map<String, Target> getCopyOfTargets() {
        return new HashMap(this.targets);
    }

    public Task createTask(String str) throws BuildException {
        return ComponentHelper.getComponentHelper(this).createTask(str);
    }

    public Object createDataType(String str) throws BuildException {
        return ComponentHelper.getComponentHelper(this).createDataType(str);
    }

    public void setExecutor(Executor executor) {
        addReference(MagicNames.ANT_EXECUTOR_REFERENCE, executor);
    }

    public Executor getExecutor() {
        Object reference = getReference(MagicNames.ANT_EXECUTOR_REFERENCE);
        if (reference == null) {
            String property = getProperty(MagicNames.ANT_EXECUTOR_CLASSNAME);
            if (property == null) {
                property = DefaultExecutor.class.getName();
            }
            log("Attempting to create object of type " + property, 4);
            try {
                try {
                    reference = Class.forName(property, true, this.coreLoader).newInstance();
                } catch (Exception e) {
                    log(e.toString(), 0);
                }
            } catch (ClassNotFoundException unused) {
                reference = Class.forName(property).newInstance();
            } catch (Exception e2) {
                log(e2.toString(), 0);
            }
            if (reference == null) {
                throw new BuildException("Unable to obtain a Target Executor instance.");
            }
            setExecutor((Executor) reference);
        }
        return (Executor) reference;
    }

    public void executeTargets(Vector<String> vector) throws BuildException {
        setUserProperty(MagicNames.PROJECT_INVOKED_TARGETS, CollectionUtils.flattenToString(vector));
        getExecutor().executeTargets(this, (String[]) vector.toArray(new String[vector.size()]));
    }

    public void demuxOutput(String str, boolean z) {
        Task threadTask = getThreadTask(Thread.currentThread());
        if (threadTask == null) {
            log(str, z ? 1 : 2);
        } else if (z) {
            threadTask.handleErrorOutput(str);
        } else {
            threadTask.handleOutput(str);
        }
    }

    public int defaultInput(byte[] bArr, int i, int i2) throws IOException {
        if (this.defaultInputStream != null) {
            System.out.flush();
            return this.defaultInputStream.read(bArr, i, i2);
        }
        throw new EOFException("No input provided for project");
    }

    public int demuxInput(byte[] bArr, int i, int i2) throws IOException {
        Task threadTask = getThreadTask(Thread.currentThread());
        if (threadTask == null) {
            return defaultInput(bArr, i, i2);
        }
        return threadTask.handleInput(bArr, i, i2);
    }

    public void demuxFlush(String str, boolean z) {
        Task threadTask = getThreadTask(Thread.currentThread());
        if (threadTask == null) {
            fireMessageLogged(this, str, z ? 0 : 2);
        } else if (z) {
            threadTask.handleErrorFlush(str);
        } else {
            threadTask.handleFlush(str);
        }
    }

    public void executeTarget(String str) throws BuildException {
        if (str == null) {
            throw new BuildException("No target specified");
        }
        executeSortedTargets(topoSort(str, this.targets, false));
    }

    public void executeSortedTargets(Vector<Target> vector) throws BuildException {
        HashSet hashSet = new HashSet();
        BuildException buildException = null;
        for (Target target : vector) {
            boolean z = true;
            Enumeration<String> dependencies = target.getDependencies();
            while (true) {
                if (!dependencies.hasMoreElements()) {
                    break;
                }
                String strNextElement = dependencies.nextElement();
                if (!hashSet.contains(strNextElement)) {
                    log(target, "Cannot execute '" + target.getName() + "' - '" + strNextElement + "' failed or was not executed.", 0);
                    z = false;
                    break;
                }
            }
            if (z) {
                try {
                    target.performTasks();
                    hashSet.add(target.getName());
                    e = null;
                } catch (RuntimeException e) {
                    e = e;
                    if (!this.keepGoingMode) {
                        throw e;
                    }
                } catch (Throwable th) {
                    e = th;
                    if (!this.keepGoingMode) {
                        throw new BuildException(e);
                    }
                }
                if (e != null) {
                    if (e instanceof BuildException) {
                        log(target, "Target '" + target.getName() + "' failed with message '" + e.getMessage() + "'.", 0);
                        if (buildException == null) {
                            buildException = (BuildException) e;
                        }
                    } else {
                        log(target, "Target '" + target.getName() + "' failed with message '" + e.getMessage() + "'.", 0);
                        e.printStackTrace(System.err);
                        if (buildException == null) {
                            buildException = new BuildException(e);
                        }
                    }
                }
            }
        }
        if (buildException != null) {
            throw buildException;
        }
    }

    public File resolveFile(String str, File file) {
        return FILE_UTILS.resolveFile(file, str);
    }

    public File resolveFile(String str) {
        return FILE_UTILS.resolveFile(this.baseDir, str);
    }

    public static String translatePath(String str) {
        return FileUtils.translatePath(str);
    }

    public void copyFile(String str, String str2) throws IOException {
        FILE_UTILS.copyFile(str, str2);
    }

    public void copyFile(String str, String str2, boolean z) throws IOException {
        FILE_UTILS.copyFile(str, str2, z ? this.globalFilters : null);
    }

    public void copyFile(String str, String str2, boolean z, boolean z2) throws IOException {
        FILE_UTILS.copyFile(str, str2, z ? this.globalFilters : null, z2);
    }

    public void copyFile(String str, String str2, boolean z, boolean z2, boolean z3) throws IOException {
        FILE_UTILS.copyFile(str, str2, z ? this.globalFilters : null, z2, z3);
    }

    public void copyFile(File file, File file2) throws IOException {
        FILE_UTILS.copyFile(file, file2);
    }

    public void copyFile(File file, File file2, boolean z) throws IOException {
        FILE_UTILS.copyFile(file, file2, z ? this.globalFilters : null);
    }

    public void copyFile(File file, File file2, boolean z, boolean z2) throws IOException {
        FILE_UTILS.copyFile(file, file2, z ? this.globalFilters : null, z2);
    }

    public void copyFile(File file, File file2, boolean z, boolean z2, boolean z3) throws IOException {
        FILE_UTILS.copyFile(file, file2, z ? this.globalFilters : null, z2, z3);
    }

    public void setFileLastModified(File file, long j) throws BuildException {
        FILE_UTILS.setFileLastModified(file, j);
        log("Setting modification time for " + file, 3);
    }

    public static boolean toBoolean(String str) {
        return Camera.Parameters.FLASH_MODE_ON.equalsIgnoreCase(str) || "true".equalsIgnoreCase(str) || "yes".equalsIgnoreCase(str);
    }

    public static Project getProject(Object obj) {
        if (obj instanceof ProjectComponent) {
            return ((ProjectComponent) obj).getProject();
        }
        try {
            Method method = obj.getClass().getMethod("getProject", (Class[]) null);
            if (Project.class == method.getReturnType()) {
                return (Project) method.invoke(obj, (Object[]) null);
            }
        } catch (Exception unused) {
        }
        return null;
    }

    public final Vector<Target> topoSort(String str, Hashtable<String, Target> hashtable) throws BuildException {
        return topoSort(new String[]{str}, hashtable, true);
    }

    public final Vector<Target> topoSort(String str, Hashtable<String, Target> hashtable, boolean z) throws BuildException {
        return topoSort(new String[]{str}, hashtable, z);
    }

    public final Vector<Target> topoSort(String[] strArr, Hashtable<String, Target> hashtable, boolean z) throws BuildException {
        VectorSet vectorSet = new VectorSet();
        Hashtable<String, String> hashtable2 = new Hashtable<>();
        Stack<String> stack = new Stack<>();
        int i = 0;
        for (int i2 = 0; i2 < strArr.length; i2++) {
            String str = hashtable2.get(strArr[i2]);
            if (str == null) {
                tsort(strArr[i2], hashtable, hashtable2, stack, vectorSet);
            } else if (str == VISITING) {
                throw new RuntimeException("Unexpected node in visiting state: " + strArr[i2]);
            }
        }
        StringBuffer stringBuffer = new StringBuffer("Build sequence for target(s)");
        while (i < strArr.length) {
            stringBuffer.append(i == 0 ? " `" : ", `").append(strArr[i]).append(DateFormat.QUOTE);
            i++;
        }
        stringBuffer.append(" is " + vectorSet);
        log(stringBuffer.toString(), 3);
        Vector<Target> vector = z ? vectorSet : new Vector<>(vectorSet);
        Enumeration<String> enumerationKeys = hashtable.keys();
        while (enumerationKeys.hasMoreElements()) {
            String strNextElement = enumerationKeys.nextElement();
            String str2 = hashtable2.get(strNextElement);
            if (str2 == null) {
                tsort(strNextElement, hashtable, hashtable2, stack, vector);
            } else if (str2 == VISITING) {
                throw new RuntimeException("Unexpected node in visiting state: " + strNextElement);
            }
        }
        log("Complete build sequence is " + vector, 3);
        return vectorSet;
    }

    private void tsort(String str, Hashtable<String, Target> hashtable, Hashtable<String, String> hashtable2, Stack<String> stack, Vector<Target> vector) throws BuildException {
        hashtable2.put(str, VISITING);
        stack.push(str);
        Target target = hashtable.get(str);
        if (target == null) {
            StringBuilder sb = new StringBuilder("Target \"");
            sb.append(str);
            sb.append("\" does not exist in the project \"");
            sb.append(this.name);
            sb.append("\". ");
            stack.pop();
            if (!stack.empty()) {
                String strPeek = stack.peek();
                sb.append("It is used from target \"");
                sb.append(strPeek);
                sb.append("\".");
            }
            throw new BuildException(new String(sb));
        }
        Enumeration<String> dependencies = target.getDependencies();
        while (dependencies.hasMoreElements()) {
            String strNextElement = dependencies.nextElement();
            String str2 = hashtable2.get(strNextElement);
            if (str2 == null) {
                tsort(strNextElement, hashtable, hashtable2, stack, vector);
            } else if (str2 == VISITING) {
                throw makeCircularException(strNextElement, stack);
            }
        }
        String strPop = stack.pop();
        if (str != strPop) {
            throw new RuntimeException("Unexpected internal error: expected to pop " + str + " but got " + strPop);
        }
        hashtable2.put(str, VISITED);
        vector.addElement(target);
    }

    private static BuildException makeCircularException(String str, Stack<String> stack) {
        String strPop;
        StringBuilder sb = new StringBuilder("Circular dependency: ");
        sb.append(str);
        do {
            strPop = stack.pop();
            sb.append(" <- ");
            sb.append(strPop);
        } while (!strPop.equals(str));
        return new BuildException(sb.toString());
    }

    public void addIdReference(String str, Object obj) {
        this.idReferences.put(str, obj);
    }

    public void addReference(String str, Object obj) {
        Object real = ((AntRefTable) this.references).getReal(str);
        if (real == obj) {
            return;
        }
        if (real != null && !(real instanceof UnknownElement)) {
            log("Overriding previous definition of reference to " + str, 3);
        }
        log("Adding reference: " + str, 4);
        this.references.put(str, obj);
    }

    public Hashtable<String, Object> getReferences() {
        return this.references;
    }

    public boolean hasReference(String str) {
        return this.references.containsKey(str);
    }

    public Map<String, Object> getCopyOfReferences() {
        return new HashMap(this.references);
    }

    public <T> T getReference(String str) {
        T t = (T) this.references.get(str);
        if (t != null) {
            return t;
        }
        if (str.equals(MagicNames.REFID_PROPERTY_HELPER)) {
            return null;
        }
        try {
            if (!PropertyHelper.getPropertyHelper(this).containsProperties(str)) {
                return null;
            }
            log("Unresolvable reference " + str + " might be a misuse of property expansion syntax.", 1);
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    public String getElementName(Object obj) {
        return ComponentHelper.getComponentHelper(this).getElementName(obj);
    }

    public void fireBuildStarted() {
        BuildEvent buildEvent = new BuildEvent(this);
        for (BuildListener buildListener : this.listeners) {
            buildListener.buildStarted(buildEvent);
        }
    }

    public void fireBuildFinished(Throwable th) {
        BuildEvent buildEvent = new BuildEvent(this);
        buildEvent.setException(th);
        for (BuildListener buildListener : this.listeners) {
            buildListener.buildFinished(buildEvent);
        }
        IntrospectionHelper.clearCache();
    }

    public void fireSubBuildStarted() {
        BuildEvent buildEvent = new BuildEvent(this);
        BuildListener[] buildListenerArr = this.listeners;
        for (int i = 0; i < buildListenerArr.length; i++) {
            if (buildListenerArr[i] instanceof SubBuildListener) {
                ((SubBuildListener) buildListenerArr[i]).subBuildStarted(buildEvent);
            }
        }
    }

    public void fireSubBuildFinished(Throwable th) {
        BuildEvent buildEvent = new BuildEvent(this);
        buildEvent.setException(th);
        BuildListener[] buildListenerArr = this.listeners;
        for (int i = 0; i < buildListenerArr.length; i++) {
            if (buildListenerArr[i] instanceof SubBuildListener) {
                ((SubBuildListener) buildListenerArr[i]).subBuildFinished(buildEvent);
            }
        }
    }

    protected void fireTargetStarted(Target target) {
        BuildEvent buildEvent = new BuildEvent(target);
        for (BuildListener buildListener : this.listeners) {
            buildListener.targetStarted(buildEvent);
        }
    }

    protected void fireTargetFinished(Target target, Throwable th) {
        BuildEvent buildEvent = new BuildEvent(target);
        buildEvent.setException(th);
        for (BuildListener buildListener : this.listeners) {
            buildListener.targetFinished(buildEvent);
        }
    }

    protected void fireTaskStarted(Task task) {
        registerThreadTask(Thread.currentThread(), task);
        BuildEvent buildEvent = new BuildEvent(task);
        for (BuildListener buildListener : this.listeners) {
            buildListener.taskStarted(buildEvent);
        }
    }

    protected void fireTaskFinished(Task task, Throwable th) {
        registerThreadTask(Thread.currentThread(), null);
        System.out.flush();
        System.err.flush();
        BuildEvent buildEvent = new BuildEvent(task);
        buildEvent.setException(th);
        for (BuildListener buildListener : this.listeners) {
            buildListener.taskFinished(buildEvent);
        }
    }

    private void fireMessageLoggedEvent(BuildEvent buildEvent, String str, int i) {
        if (str == null) {
            str = String.valueOf(str);
        }
        if (str.endsWith(StringUtils.LINE_SEP)) {
            buildEvent.setMessage(str.substring(0, str.length() - StringUtils.LINE_SEP.length()), i);
        } else {
            buildEvent.setMessage(str, i);
        }
        if (this.isLoggingMessage.get() != Boolean.FALSE) {
            return;
        }
        try {
            this.isLoggingMessage.set(Boolean.TRUE);
            for (BuildListener buildListener : this.listeners) {
                buildListener.messageLogged(buildEvent);
            }
        } finally {
            this.isLoggingMessage.set(Boolean.FALSE);
        }
    }

    protected void fireMessageLogged(Project project, String str, int i) {
        fireMessageLogged(project, str, (Throwable) null, i);
    }

    protected void fireMessageLogged(Project project, String str, Throwable th, int i) {
        BuildEvent buildEvent = new BuildEvent(project);
        buildEvent.setException(th);
        fireMessageLoggedEvent(buildEvent, str, i);
    }

    protected void fireMessageLogged(Target target, String str, int i) {
        fireMessageLogged(target, str, (Throwable) null, i);
    }

    protected void fireMessageLogged(Target target, String str, Throwable th, int i) {
        BuildEvent buildEvent = new BuildEvent(target);
        buildEvent.setException(th);
        fireMessageLoggedEvent(buildEvent, str, i);
    }

    protected void fireMessageLogged(Task task, String str, int i) {
        fireMessageLogged(task, str, (Throwable) null, i);
    }

    protected void fireMessageLogged(Task task, String str, Throwable th, int i) {
        BuildEvent buildEvent = new BuildEvent(task);
        buildEvent.setException(th);
        fireMessageLoggedEvent(buildEvent, str, i);
    }

    public void registerThreadTask(Thread thread, Task task) {
        synchronized (this.threadTasks) {
            if (task != null) {
                this.threadTasks.put(thread, task);
                this.threadGroupTasks.put(thread.getThreadGroup(), task);
            } else {
                this.threadTasks.remove(thread);
                this.threadGroupTasks.remove(thread.getThreadGroup());
            }
        }
    }

    public Task getThreadTask(Thread thread) {
        Task task;
        synchronized (this.threadTasks) {
            task = this.threadTasks.get(thread);
            if (task == null) {
                for (ThreadGroup threadGroup = thread.getThreadGroup(); task == null && threadGroup != null; threadGroup = threadGroup.getParent()) {
                    task = this.threadGroupTasks.get(threadGroup);
                }
            }
        }
        return task;
    }

    private static class AntRefTable extends Hashtable<String, Object> {
        private static final long serialVersionUID = 1;

        AntRefTable() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Object getReal(Object obj) {
            return super.get(obj);
        }

        @Override // java.util.Hashtable, java.util.Dictionary, java.util.Map
        public Object get(Object obj) {
            Object real = getReal(obj);
            if (!(real instanceof UnknownElement)) {
                return real;
            }
            UnknownElement unknownElement = (UnknownElement) real;
            unknownElement.maybeConfigure();
            return unknownElement.getRealThing();
        }
    }

    public final void setProjectReference(Object obj) {
        if (obj instanceof ProjectComponent) {
            ((ProjectComponent) obj).setProject(this);
            return;
        }
        try {
            Method method = obj.getClass().getMethod("setProject", Project.class);
            if (method != null) {
                method.invoke(obj, this);
            }
        } catch (Throwable unused) {
        }
    }

    @Override // org.apache.tools.ant.types.ResourceFactory
    public Resource getResource(String str) {
        return new FileResource(getBaseDir(), str);
    }
}
