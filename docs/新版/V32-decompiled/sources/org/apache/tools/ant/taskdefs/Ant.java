package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Main;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.ParserSupports;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.VectorSet;

/* JADX INFO: loaded from: classes3.dex */
public class Ant extends Task {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private Project newProject;
    private File dir = null;
    private String antFile = null;
    private String output = null;
    private boolean inheritAll = true;
    private boolean inheritRefs = false;
    private Vector<Property> properties = new Vector<>();
    private Vector<Reference> references = new Vector<>();
    private PrintStream out = null;
    private Vector<PropertySet> propertySets = new Vector<>();
    private Vector<String> targets = new Vector<>();
    private boolean targetAttributeSet = false;
    private boolean useNativeBasedir = false;

    protected String getDefaultBuildFile() {
        return Main.DEFAULT_BUILD_FILENAME;
    }

    public Ant() {
    }

    public Ant(Task task) {
        bindToOwner(task);
    }

    public void setUseNativeBasedir(boolean z) {
        this.useNativeBasedir = z;
    }

    public void setInheritAll(boolean z) {
        this.inheritAll = z;
    }

    public void setInheritRefs(boolean z) {
        this.inheritRefs = z;
    }

    @Override // org.apache.tools.ant.Task
    public void init() {
        Project projectCreateSubProject = getProject().createSubProject();
        this.newProject = projectCreateSubProject;
        projectCreateSubProject.setJavaVersionProperty();
    }

    private void reinit() {
        init();
    }

    private void initializeProject() {
        File fileResolveFile;
        this.newProject.setInputHandler(getProject().getInputHandler());
        Iterator<BuildListener> buildListeners = getBuildListeners();
        while (buildListeners.hasNext()) {
            this.newProject.addBuildListener(buildListeners.next());
        }
        String str = this.output;
        if (str != null) {
            File file = this.dir;
            if (file != null) {
                fileResolveFile = FILE_UTILS.resolveFile(file, str);
            } else {
                fileResolveFile = getProject().resolveFile(this.output);
            }
            try {
                this.out = new PrintStream(new FileOutputStream(fileResolveFile));
                DefaultLogger defaultLogger = new DefaultLogger();
                defaultLogger.setMessageOutputLevel(2);
                defaultLogger.setOutputPrintStream(this.out);
                defaultLogger.setErrorPrintStream(this.out);
                this.newProject.addBuildListener(defaultLogger);
            } catch (IOException unused) {
                log("Ant: Can't set output to " + this.output);
            }
        }
        if (!this.useNativeBasedir) {
            getProject().copyUserProperties(this.newProject);
        } else {
            addAlmostAll(getProject().getUserProperties(), PropertyType.USER);
        }
        if (!this.inheritAll) {
            this.newProject.initProperties();
        } else {
            addAlmostAll(getProject().getProperties(), PropertyType.PLAIN);
        }
        Iterator<PropertySet> it = this.propertySets.iterator();
        while (it.hasNext()) {
            addAlmostAll(it.next().getProperties(), PropertyType.PLAIN);
        }
    }

    @Override // org.apache.tools.ant.Task
    public void handleOutput(String str) {
        Project project = this.newProject;
        if (project != null) {
            project.demuxOutput(str, false);
        } else {
            super.handleOutput(str);
        }
    }

    @Override // org.apache.tools.ant.Task
    public int handleInput(byte[] bArr, int i, int i2) throws IOException {
        Project project = this.newProject;
        if (project != null) {
            return project.demuxInput(bArr, i, i2);
        }
        return super.handleInput(bArr, i, i2);
    }

    @Override // org.apache.tools.ant.Task
    public void handleFlush(String str) {
        Project project = this.newProject;
        if (project != null) {
            project.demuxFlush(str, false);
        } else {
            super.handleFlush(str);
        }
    }

    @Override // org.apache.tools.ant.Task
    public void handleErrorOutput(String str) {
        Project project = this.newProject;
        if (project != null) {
            project.demuxOutput(str, true);
        } else {
            super.handleErrorOutput(str);
        }
    }

    @Override // org.apache.tools.ant.Task
    public void handleErrorFlush(String str) {
        Project project = this.newProject;
        if (project != null) {
            project.demuxFlush(str, true);
        } else {
            super.handleErrorFlush(str);
        }
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        PrintStream printStream;
        BuildException buildExceptionAddLocationToBuildException;
        String defaultTarget;
        File file = this.dir;
        String str = this.antFile;
        VectorSet vectorSet = new VectorSet(this.targets);
        try {
            getNewProject();
            if (this.dir == null && this.inheritAll) {
                this.dir = getProject().getBaseDir();
            }
            initializeProject();
            File file2 = this.dir;
            if (file2 != null) {
                if (!this.useNativeBasedir) {
                    this.newProject.setBaseDir(file2);
                    if (file != null) {
                        this.newProject.setInheritedProperty(MagicNames.PROJECT_BASEDIR, this.dir.getAbsolutePath());
                    }
                }
            } else {
                this.dir = getProject().getBaseDir();
            }
            overrideProperties();
            if (this.antFile == null) {
                this.antFile = getDefaultBuildFile();
            }
            File fileResolveFile = FILE_UTILS.resolveFile(this.dir, this.antFile);
            this.antFile = fileResolveFile.getAbsolutePath();
            log("calling target(s) " + (vectorSet.size() > 0 ? vectorSet.toString() : "[default]") + " in build file " + this.antFile, 3);
            this.newProject.setUserProperty(MagicNames.ANT_FILE, this.antFile);
            String property = getProject().getProperty(MagicNames.ANT_FILE);
            if (property != null && fileResolveFile.equals(getProject().resolveFile(property)) && getOwningTarget() != null && getOwningTarget().getName().equals("")) {
                if (getTaskName().equals("antcall")) {
                    throw new BuildException("antcall must not be used at the top level.");
                }
                throw new BuildException(getTaskName() + " task at the top level must not invoke its own build file.");
            }
            try {
                ProjectHelper.configureProject(this.newProject, fileResolveFile);
                if (vectorSet.size() == 0 && (defaultTarget = this.newProject.getDefaultTarget()) != null) {
                    vectorSet.add(defaultTarget);
                }
                if (this.newProject.getProperty(MagicNames.ANT_FILE).equals(getProject().getProperty(MagicNames.ANT_FILE)) && getOwningTarget() != null) {
                    String name = getOwningTarget().getName();
                    if (vectorSet.contains(name)) {
                        throw new BuildException(getTaskName() + " task calling its own parent target.");
                    }
                    Iterator<E> it = vectorSet.iterator();
                    boolean z = false;
                    while (!z && it.hasNext()) {
                        Target target = getProject().getTargets().get(it.next());
                        z |= target != null && target.dependsOn(name);
                    }
                    if (z) {
                        throw new BuildException(getTaskName() + " task calling a target that depends on its parent target '" + name + "'.");
                    }
                }
                addReferences();
                if (vectorSet.size() > 0 && (vectorSet.size() != 1 || !"".equals(vectorSet.get(0)))) {
                    try {
                        try {
                            log("Entering " + this.antFile + "...", 3);
                            this.newProject.fireSubBuildStarted();
                            this.newProject.executeTargets(vectorSet);
                            log("Exiting " + this.antFile + ".", 3);
                            this.newProject.fireSubBuildFinished(null);
                        } catch (Throwable th) {
                            th = th;
                            buildExceptionAddLocationToBuildException = null;
                            log("Exiting " + this.antFile + ".", 3);
                            this.newProject.fireSubBuildFinished(buildExceptionAddLocationToBuildException);
                            throw th;
                        }
                    } catch (BuildException e) {
                        buildExceptionAddLocationToBuildException = ProjectHelper.addLocationToBuildException(e, getLocation());
                        try {
                            throw buildExceptionAddLocationToBuildException;
                        } catch (Throwable th2) {
                            th = th2;
                            log("Exiting " + this.antFile + ".", 3);
                            this.newProject.fireSubBuildFinished(buildExceptionAddLocationToBuildException);
                            throw th;
                        }
                    }
                }
            } catch (BuildException e2) {
                throw ProjectHelper.addLocationToBuildException(e2, getLocation());
            }
        } finally {
            this.newProject = null;
            Iterator<Property> it2 = this.properties.iterator();
            while (it2.hasNext()) {
                it2.next().setProject(null);
            }
            if (this.output != null && (printStream = this.out) != null) {
                try {
                    printStream.close();
                } catch (Exception unused) {
                }
            }
            this.dir = file;
            this.antFile = str;
        }
    }

    private void overrideProperties() throws Throwable {
        HashSet hashSet = new HashSet();
        for (int size = this.properties.size() - 1; size >= 0; size--) {
            Property property = this.properties.get(size);
            if (property.getName() != null && !property.getName().equals("")) {
                if (hashSet.contains(property.getName())) {
                    this.properties.remove(size);
                } else {
                    hashSet.add(property.getName());
                }
            }
        }
        Enumeration<Property> enumerationElements = this.properties.elements();
        while (enumerationElements.hasMoreElements()) {
            Property propertyNextElement = enumerationElements.nextElement();
            propertyNextElement.setProject(this.newProject);
            propertyNextElement.execute();
        }
        if (!this.useNativeBasedir) {
            getProject().copyInheritedProperties(this.newProject);
        } else {
            addAlmostAll(getProject().getInheritedProperties(), PropertyType.INHERITED);
        }
    }

    private void addReferences() throws BuildException {
        Hashtable hashtable = (Hashtable) getProject().getReferences().clone();
        for (Reference reference : this.references) {
            String refId = reference.getRefId();
            if (refId == null) {
                throw new BuildException("the refid attribute is required for reference elements");
            }
            if (!hashtable.containsKey(refId)) {
                log("Parent project doesn't contain any reference '" + refId + "'", 1);
            } else {
                hashtable.remove(refId);
                String toRefid = reference.getToRefid();
                if (toRefid == null) {
                    toRefid = refId;
                }
                copyReference(refId, toRefid);
            }
        }
        if (this.inheritRefs) {
            Hashtable<String, Object> references = this.newProject.getReferences();
            for (String str : hashtable.keySet()) {
                if (!references.containsKey(str)) {
                    copyReference(str, str);
                    this.newProject.inheritIDReferences(getProject());
                }
            }
        }
    }

    private void copyReference(String str, String str2) {
        Object reference = getProject().getReference(str);
        if (reference == null) {
            log("No object referenced by " + str + ". Can't copy to " + str2, 1);
            return;
        }
        Class<?> cls = reference.getClass();
        try {
            Method method = cls.getMethod("clone", new Class[0]);
            if (method != null) {
                reference = method.invoke(reference, new Object[0]);
                log("Adding clone of reference " + str, 4);
            }
        } catch (Exception unused) {
        }
        if (reference instanceof ProjectComponent) {
            ((ProjectComponent) reference).setProject(this.newProject);
        } else {
            try {
                Method method2 = cls.getMethod("setProject", Project.class);
                if (method2 != null) {
                    method2.invoke(reference, this.newProject);
                }
            } catch (NoSuchMethodException unused2) {
            } catch (Exception e) {
                throw new BuildException("Error setting new project instance for reference with id " + str, e, getLocation());
            }
        }
        this.newProject.addReference(str2, reference);
    }

    private void addAlmostAll(Hashtable<?, ?> hashtable, PropertyType propertyType) {
        Enumeration<?> enumerationKeys = hashtable.keys();
        while (enumerationKeys.hasMoreElements()) {
            String string = enumerationKeys.nextElement().toString();
            if (!MagicNames.PROJECT_BASEDIR.equals(string) && !MagicNames.ANT_FILE.equals(string)) {
                String string2 = hashtable.get(string).toString();
                if (propertyType == PropertyType.PLAIN) {
                    if (this.newProject.getProperty(string) == null) {
                        this.newProject.setNewProperty(string, string2);
                    }
                } else if (propertyType == PropertyType.USER) {
                    this.newProject.setUserProperty(string, string2);
                } else if (propertyType == PropertyType.INHERITED) {
                    this.newProject.setInheritedProperty(string, string2);
                }
            }
        }
    }

    public void setDir(File file) {
        this.dir = file;
    }

    public void setAntfile(String str) {
        this.antFile = str;
    }

    public void setTarget(String str) {
        if (str.equals("")) {
            throw new BuildException("target attribute must not be empty");
        }
        this.targets.add(str);
        this.targetAttributeSet = true;
    }

    public void setOutput(String str) {
        this.output = str;
    }

    public Property createProperty() {
        Property property = new Property(true, getProject());
        property.setProject(getNewProject());
        property.setTaskName(ParserSupports.PROPERTY);
        this.properties.addElement(property);
        return property;
    }

    public void addReference(Reference reference) {
        this.references.addElement(reference);
    }

    public void addConfiguredTarget(TargetElement targetElement) {
        if (this.targetAttributeSet) {
            throw new BuildException("nested target is incompatible with the target attribute");
        }
        String name = targetElement.getName();
        if (name.equals("")) {
            throw new BuildException("target name must not be empty");
        }
        this.targets.add(name);
    }

    public void addPropertyset(PropertySet propertySet) {
        this.propertySets.addElement(propertySet);
    }

    protected Project getNewProject() {
        if (this.newProject == null) {
            reinit();
        }
        return this.newProject;
    }

    private Iterator<BuildListener> getBuildListeners() {
        return getProject().getBuildListeners().iterator();
    }

    public static class Reference extends org.apache.tools.ant.types.Reference {
        private String targetid = null;

        public void setToRefid(String str) {
            this.targetid = str;
        }

        public String getToRefid() {
            return this.targetid;
        }
    }

    public static class TargetElement {
        private String name;

        public void setName(String str) {
            this.name = str;
        }

        public String getName() {
            return this.name;
        }
    }

    private static final class PropertyType {
        private static final PropertyType PLAIN = new PropertyType();
        private static final PropertyType INHERITED = new PropertyType();
        private static final PropertyType USER = new PropertyType();

        private PropertyType() {
        }
    }
}
