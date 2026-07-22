package org.apache.tools.ant;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import org.apache.tools.ant.taskdefs.Definer;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.taskdefs.Typedef;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ComponentHelper {
    private static final String ANT_PROPERTY_TASK = "property";
    private static final String BUILD_SYSCLASSPATH_ONLY = "only";
    public static final String COMPONENT_HELPER_REFERENCE = "ant.ComponentHelper";
    private static final String ERROR_NO_TASK_LIST_LOAD = "Can't load default task list";
    private static final String ERROR_NO_TYPE_LIST_LOAD = "Can't load default type list";
    private static Properties[] defaultDefinitions = new Properties[2];
    private ComponentHelper next;
    private Project project;
    private Map<String, List<AntTypeDefinition>> restrictedDefinitions = new HashMap();
    private final Hashtable<String, AntTypeDefinition> antTypeTable = new Hashtable<>();
    private final Hashtable<String, Class<?>> taskClassDefinitions = new Hashtable<>();
    private boolean rebuildTaskClassDefinitions = true;
    private final Hashtable<String, Class<?>> typeClassDefinitions = new Hashtable<>();
    private boolean rebuildTypeClassDefinitions = true;
    private final HashSet<String> checkedNamespaces = new HashSet<>();
    private Stack<String> antLibStack = new Stack<>();
    private String antLibCurrentUri = null;

    public Project getProject() {
        return this.project;
    }

    public static ComponentHelper getComponentHelper(Project project) {
        if (project == null) {
            return null;
        }
        ComponentHelper componentHelper = (ComponentHelper) project.getReference(COMPONENT_HELPER_REFERENCE);
        if (componentHelper != null) {
            return componentHelper;
        }
        ComponentHelper componentHelper2 = new ComponentHelper();
        componentHelper2.setProject(project);
        project.addReference(COMPONENT_HELPER_REFERENCE, componentHelper2);
        return componentHelper2;
    }

    protected ComponentHelper() {
    }

    public void setNext(ComponentHelper componentHelper) {
        this.next = componentHelper;
    }

    public ComponentHelper getNext() {
        return this.next;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    private synchronized Set<String> getCheckedNamespace() {
        return (Set) this.checkedNamespaces.clone();
    }

    private Map<String, List<AntTypeDefinition>> getRestrictedDefinition() {
        ArrayList arrayList;
        HashMap map = new HashMap();
        synchronized (this.restrictedDefinitions) {
            for (Map.Entry<String, List<AntTypeDefinition>> entry : this.restrictedDefinitions.entrySet()) {
                List<AntTypeDefinition> value = entry.getValue();
                synchronized (value) {
                    arrayList = new ArrayList(value);
                }
                map.put(entry.getKey(), arrayList);
            }
        }
        return map;
    }

    public void initSubProject(ComponentHelper componentHelper) {
        Hashtable hashtable = (Hashtable) componentHelper.antTypeTable.clone();
        synchronized (this.antTypeTable) {
            for (AntTypeDefinition antTypeDefinition : hashtable.values()) {
                this.antTypeTable.put(antTypeDefinition.getName(), antTypeDefinition);
            }
        }
        Set<String> checkedNamespace = componentHelper.getCheckedNamespace();
        synchronized (this) {
            this.checkedNamespaces.addAll(checkedNamespace);
        }
        Map<String, List<AntTypeDefinition>> restrictedDefinition = componentHelper.getRestrictedDefinition();
        synchronized (this.restrictedDefinitions) {
            this.restrictedDefinitions.putAll(restrictedDefinition);
        }
    }

    public Object createComponent(UnknownElement unknownElement, String str, String str2) throws BuildException {
        Object objCreateComponent = createComponent(str2);
        if (objCreateComponent instanceof Task) {
            Task task = (Task) objCreateComponent;
            task.setLocation(unknownElement.getLocation());
            task.setTaskType(str2);
            task.setTaskName(unknownElement.getTaskName());
            task.setOwningTarget(unknownElement.getOwningTarget());
            task.init();
        }
        return objCreateComponent;
    }

    public Object createComponent(String str) {
        AntTypeDefinition definition = getDefinition(str);
        if (definition == null) {
            return null;
        }
        return definition.create(this.project);
    }

    public Class<?> getComponentClass(String str) {
        AntTypeDefinition definition = getDefinition(str);
        if (definition == null) {
            return null;
        }
        return definition.getExposedClass(this.project);
    }

    public AntTypeDefinition getDefinition(String str) {
        checkNamespace(str);
        return this.antTypeTable.get(str);
    }

    public void initDefaultDefinitions() {
        initTasks();
        initTypes();
        new DefaultDefinitions(this).execute();
    }

    public void addTaskDefinition(String str, Class<?> cls) {
        checkTaskClass(cls);
        AntTypeDefinition antTypeDefinition = new AntTypeDefinition();
        antTypeDefinition.setName(str);
        antTypeDefinition.setClassLoader(cls.getClassLoader());
        antTypeDefinition.setClass(cls);
        antTypeDefinition.setAdapterClass(TaskAdapter.class);
        antTypeDefinition.setClassName(cls.getName());
        antTypeDefinition.setAdaptToClass(Task.class);
        updateDataTypeDefinition(antTypeDefinition);
    }

    public void checkTaskClass(Class<?> cls) throws BuildException {
        if (!Modifier.isPublic(cls.getModifiers())) {
            String str = cls + " is not public";
            this.project.log(str, 0);
            throw new BuildException(str);
        }
        if (Modifier.isAbstract(cls.getModifiers())) {
            String str2 = cls + " is abstract";
            this.project.log(str2, 0);
            throw new BuildException(str2);
        }
        try {
            cls.getConstructor((Class[]) null);
            if (Task.class.isAssignableFrom(cls)) {
                return;
            }
            TaskAdapter.checkTaskClass(cls, this.project);
        } catch (NoSuchMethodException unused) {
            String str3 = "No public no-arg constructor in " + cls;
            this.project.log(str3, 0);
            throw new BuildException(str3);
        }
    }

    public Hashtable<String, Class<?>> getTaskDefinitions() {
        synchronized (this.taskClassDefinitions) {
            synchronized (this.antTypeTable) {
                if (this.rebuildTaskClassDefinitions) {
                    this.taskClassDefinitions.clear();
                    for (Map.Entry<String, AntTypeDefinition> entry : this.antTypeTable.entrySet()) {
                        Class<?> exposedClass = entry.getValue().getExposedClass(this.project);
                        if (exposedClass != null && Task.class.isAssignableFrom(exposedClass)) {
                            this.taskClassDefinitions.put(entry.getKey(), entry.getValue().getTypeClass(this.project));
                        }
                    }
                    this.rebuildTaskClassDefinitions = false;
                }
            }
        }
        return this.taskClassDefinitions;
    }

    public Hashtable<String, Class<?>> getDataTypeDefinitions() {
        synchronized (this.typeClassDefinitions) {
            synchronized (this.antTypeTable) {
                if (this.rebuildTypeClassDefinitions) {
                    this.typeClassDefinitions.clear();
                    for (Map.Entry<String, AntTypeDefinition> entry : this.antTypeTable.entrySet()) {
                        Class<?> exposedClass = entry.getValue().getExposedClass(this.project);
                        if (exposedClass != null && !Task.class.isAssignableFrom(exposedClass)) {
                            this.typeClassDefinitions.put(entry.getKey(), entry.getValue().getTypeClass(this.project));
                        }
                    }
                    this.rebuildTypeClassDefinitions = false;
                }
            }
        }
        return this.typeClassDefinitions;
    }

    public List<AntTypeDefinition> getRestrictedDefinitions(String str) {
        List<AntTypeDefinition> list;
        synchronized (this.restrictedDefinitions) {
            list = this.restrictedDefinitions.get(str);
        }
        return list;
    }

    public void addDataTypeDefinition(String str, Class<?> cls) {
        AntTypeDefinition antTypeDefinition = new AntTypeDefinition();
        antTypeDefinition.setName(str);
        antTypeDefinition.setClass(cls);
        updateDataTypeDefinition(antTypeDefinition);
        this.project.log(" +User datatype: " + str + "     " + cls.getName(), 4);
    }

    public void addDataTypeDefinition(AntTypeDefinition antTypeDefinition) {
        if (!antTypeDefinition.isRestrict()) {
            updateDataTypeDefinition(antTypeDefinition);
        } else {
            updateRestrictedDefinition(antTypeDefinition);
        }
    }

    public Hashtable<String, AntTypeDefinition> getAntTypeTable() {
        return this.antTypeTable;
    }

    public Task createTask(String str) throws BuildException {
        Task taskCreateNewTask = createNewTask(str);
        if (taskCreateNewTask != null || !str.equals("property")) {
            return taskCreateNewTask;
        }
        addTaskDefinition("property", Property.class);
        return createNewTask(str);
    }

    private Task createNewTask(String str) throws BuildException {
        Object objCreateComponent;
        Class<?> componentClass = getComponentClass(str);
        if (componentClass == null || !Task.class.isAssignableFrom(componentClass) || (objCreateComponent = createComponent(str)) == null) {
            return null;
        }
        if (!(objCreateComponent instanceof Task)) {
            throw new BuildException("Expected a Task from '" + str + "' but got an instance of " + objCreateComponent.getClass().getName() + " instead");
        }
        Task task = (Task) objCreateComponent;
        task.setTaskType(str);
        task.setTaskName(str);
        this.project.log("   +Task: " + str, 4);
        return task;
    }

    public Object createDataType(String str) throws BuildException {
        return createComponent(str);
    }

    public String getElementName(Object obj) {
        return getElementName(obj, false);
    }

    public String getElementName(Object obj, boolean z) {
        Class<?> cls = obj.getClass();
        String name = cls.getName();
        synchronized (this.antTypeTable) {
            for (AntTypeDefinition antTypeDefinition : this.antTypeTable.values()) {
                if (name.equals(antTypeDefinition.getClassName()) && cls == antTypeDefinition.getExposedClass(this.project)) {
                    String name2 = antTypeDefinition.getName();
                    if (!z) {
                        name2 = "The <" + name2 + "> type";
                    }
                    return name2;
                }
            }
            return getUnmappedElementName(obj.getClass(), z);
        }
    }

    public static String getElementName(Project project, Object obj, boolean z) {
        if (project == null) {
            project = Project.getProject(obj);
        }
        return project == null ? getUnmappedElementName(obj.getClass(), z) : getComponentHelper(project).getElementName(obj, z);
    }

    private static String getUnmappedElementName(Class<?> cls, boolean z) {
        if (z) {
            String name = cls.getName();
            return name.substring(name.lastIndexOf(46) + 1);
        }
        return cls.toString();
    }

    private boolean validDefinition(AntTypeDefinition antTypeDefinition) {
        return (antTypeDefinition.getTypeClass(this.project) == null || antTypeDefinition.getExposedClass(this.project) == null) ? false : true;
    }

    private boolean sameDefinition(AntTypeDefinition antTypeDefinition, AntTypeDefinition antTypeDefinition2) {
        boolean zValidDefinition = validDefinition(antTypeDefinition);
        return (zValidDefinition == validDefinition(antTypeDefinition2)) && (!zValidDefinition || antTypeDefinition.sameDefinition(antTypeDefinition2, this.project));
    }

    private void updateRestrictedDefinition(AntTypeDefinition antTypeDefinition) {
        List<AntTypeDefinition> arrayList;
        String name = antTypeDefinition.getName();
        synchronized (this.restrictedDefinitions) {
            arrayList = this.restrictedDefinitions.get(name);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.restrictedDefinitions.put(name, arrayList);
            }
        }
        synchronized (arrayList) {
            Iterator<AntTypeDefinition> it = arrayList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                } else if (it.next().getClassName().equals(antTypeDefinition.getClassName())) {
                    it.remove();
                    break;
                }
            }
            arrayList.add(antTypeDefinition);
        }
    }

    private void updateDataTypeDefinition(AntTypeDefinition antTypeDefinition) {
        String name = antTypeDefinition.getName();
        synchronized (this.antTypeTable) {
            this.rebuildTaskClassDefinitions = true;
            this.rebuildTypeClassDefinitions = true;
            AntTypeDefinition antTypeDefinition2 = this.antTypeTable.get(name);
            if (antTypeDefinition2 != null) {
                if (sameDefinition(antTypeDefinition, antTypeDefinition2)) {
                    return;
                }
                Class<?> exposedClass = antTypeDefinition2.getExposedClass(this.project);
                this.project.log("Trying to override old definition of " + (exposedClass != null && Task.class.isAssignableFrom(exposedClass) ? "task " : "datatype ") + name, antTypeDefinition.similarDefinition(antTypeDefinition2, this.project) ? 3 : 1);
            }
            this.project.log(" +Datatype " + name + " " + antTypeDefinition.getClassName(), 4);
            this.antTypeTable.put(name, antTypeDefinition);
        }
    }

    public void enterAntLib(String str) {
        this.antLibCurrentUri = str;
        this.antLibStack.push(str);
    }

    public String getCurrentAntlibUri() {
        return this.antLibCurrentUri;
    }

    public void exitAntLib() {
        this.antLibStack.pop();
        this.antLibCurrentUri = this.antLibStack.size() == 0 ? null : this.antLibStack.peek();
    }

    private void initTasks() {
        ClassLoader classLoader = getClassLoader(null);
        Properties defaultDefinitions2 = getDefaultDefinitions(false);
        Enumeration<?> enumerationPropertyNames = defaultDefinitions2.propertyNames();
        while (enumerationPropertyNames.hasMoreElements()) {
            String str = (String) enumerationPropertyNames.nextElement();
            String property = defaultDefinitions2.getProperty(str);
            AntTypeDefinition antTypeDefinition = new AntTypeDefinition();
            antTypeDefinition.setName(str);
            antTypeDefinition.setClassName(property);
            antTypeDefinition.setClassLoader(classLoader);
            antTypeDefinition.setAdaptToClass(Task.class);
            antTypeDefinition.setAdapterClass(TaskAdapter.class);
            this.antTypeTable.put(str, antTypeDefinition);
        }
    }

    private ClassLoader getClassLoader(ClassLoader classLoader) {
        return (this.project.getCoreLoader() == null || BUILD_SYSCLASSPATH_ONLY.equals(this.project.getProperty(MagicNames.BUILD_SYSCLASSPATH))) ? classLoader : this.project.getCoreLoader();
    }

    private static synchronized Properties getDefaultDefinitions(boolean z) throws BuildException {
        char c;
        c = z ? (char) 1 : (char) 0;
        if (defaultDefinitions[c] == null) {
            String str = z ? MagicNames.TYPEDEFS_PROPERTIES_RESOURCE : MagicNames.TASKDEF_PROPERTIES_RESOURCE;
            String str2 = z ? ERROR_NO_TYPE_LIST_LOAD : ERROR_NO_TASK_LIST_LOAD;
            try {
                try {
                    InputStream resourceAsStream = ComponentHelper.class.getResourceAsStream(str);
                    if (resourceAsStream == null) {
                        throw new BuildException(str2);
                    }
                    Properties properties = new Properties();
                    properties.load(resourceAsStream);
                    defaultDefinitions[c] = properties;
                    FileUtils.close(resourceAsStream);
                } catch (IOException e) {
                    throw new BuildException(str2, e);
                }
            } catch (Throwable th) {
                FileUtils.close((InputStream) null);
                throw th;
            }
        }
        return defaultDefinitions[c];
    }

    private void initTypes() {
        ClassLoader classLoader = getClassLoader(null);
        Properties defaultDefinitions2 = getDefaultDefinitions(true);
        Enumeration<?> enumerationPropertyNames = defaultDefinitions2.propertyNames();
        while (enumerationPropertyNames.hasMoreElements()) {
            String str = (String) enumerationPropertyNames.nextElement();
            String property = defaultDefinitions2.getProperty(str);
            AntTypeDefinition antTypeDefinition = new AntTypeDefinition();
            antTypeDefinition.setName(str);
            antTypeDefinition.setClassName(property);
            antTypeDefinition.setClassLoader(classLoader);
            this.antTypeTable.put(str, antTypeDefinition);
        }
    }

    private synchronized void checkNamespace(String str) {
        String strExtractUriFromComponentName = ProjectHelper.extractUriFromComponentName(str);
        if ("".equals(strExtractUriFromComponentName)) {
            strExtractUriFromComponentName = ProjectHelper.ANT_CORE_URI;
        }
        if (strExtractUriFromComponentName.startsWith("antlib:")) {
            if (this.checkedNamespaces.contains(strExtractUriFromComponentName)) {
                return;
            }
            this.checkedNamespaces.add(strExtractUriFromComponentName);
            if (this.antTypeTable.size() == 0) {
                initDefaultDefinitions();
            }
            Typedef typedef = new Typedef();
            typedef.setProject(this.project);
            typedef.init();
            typedef.setURI(strExtractUriFromComponentName);
            typedef.setTaskName(strExtractUriFromComponentName);
            typedef.setResource(Definer.makeResourceFromURI(strExtractUriFromComponentName));
            typedef.setOnError(new Definer.OnError(Definer.OnError.POLICY_IGNORE));
            typedef.execute();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x0194  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x019b  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x01a2  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x01d6  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.lang.String diagnoseCreationFailure(java.lang.String r13, java.lang.String r14) {
        /*
            Method dump skipped, instruction units count: 489
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.ComponentHelper.diagnoseCreationFailure(java.lang.String, java.lang.String):java.lang.String");
    }

    private void printUnknownDefinition(PrintWriter printWriter, String str, String str2) {
        boolean zStartsWith = str.startsWith("antlib:");
        String strExtractUriFromComponentName = ProjectHelper.extractUriFromComponentName(str);
        printWriter.println("Cause: The name is undefined.");
        printWriter.println("Action: Check the spelling.");
        printWriter.println("Action: Check that any custom tasks/types have been declared.");
        printWriter.println("Action: Check that any <presetdef>/<macrodef> declarations have taken place.");
        if (strExtractUriFromComponentName.length() > 0) {
            List<AntTypeDefinition> listFindTypeMatches = findTypeMatches(strExtractUriFromComponentName);
            if (listFindTypeMatches.size() > 0) {
                printWriter.println();
                printWriter.println("The definitions in the namespace " + strExtractUriFromComponentName + " are:");
                Iterator<AntTypeDefinition> it = listFindTypeMatches.iterator();
                while (it.hasNext()) {
                    printWriter.println("    " + ProjectHelper.extractNameFromComponentName(it.next().getName()));
                }
                return;
            }
            printWriter.println("No types or tasks have been defined in this namespace yet");
            if (zStartsWith) {
                printWriter.println();
                printWriter.println("This appears to be an antlib declaration. ");
                printWriter.println("Action: Check that the implementing library exists in one of:");
                printWriter.println(str2);
            }
        }
    }

    private void printClassNotFound(PrintWriter printWriter, String str, boolean z, String str2) {
        printWriter.println("Cause: the class " + str + " was not found.");
        if (z) {
            printWriter.println("        This looks like one of Ant's optional components.");
            printWriter.println("Action: Check that the appropriate optional JAR exists in");
            printWriter.println(str2);
        } else {
            printWriter.println("Action: Check that the component has been correctly declared");
            printWriter.println("        and that the implementing JAR is in one of:");
            printWriter.println(str2);
        }
    }

    private void printNotLoadDependentClass(PrintWriter printWriter, boolean z, NoClassDefFoundError noClassDefFoundError, String str) {
        printWriter.println("Cause: Could not load a dependent class " + noClassDefFoundError.getMessage());
        if (z) {
            printWriter.println("       It is not enough to have Ant's optional JARs");
            printWriter.println("       you need the JAR files that the optional tasks depend upon.");
            printWriter.println("       Ant's optional task dependencies are listed in the manual.");
        } else {
            printWriter.println("       This class may be in a separate JAR that is not installed.");
        }
        printWriter.println("Action: Determine what extra JAR files are needed, and place them in one of:");
        printWriter.println(str);
    }

    private List<AntTypeDefinition> findTypeMatches(String str) {
        ArrayList arrayList = new ArrayList();
        synchronized (this.antTypeTable) {
            for (AntTypeDefinition antTypeDefinition : this.antTypeTable.values()) {
                if (antTypeDefinition.getName().startsWith(str)) {
                    arrayList.add(antTypeDefinition);
                }
            }
        }
        return arrayList;
    }
}
