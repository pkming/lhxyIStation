package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;

/* JADX INFO: loaded from: classes3.dex */
public class PreSetDef extends AntlibDefinition implements TaskContainer {
    private String name;
    private UnknownElement nestedTask;

    public void setName(String str) {
        this.name = str;
    }

    @Override // org.apache.tools.ant.TaskContainer
    public void addTask(Task task) {
        if (this.nestedTask != null) {
            throw new BuildException("Only one nested element allowed");
        }
        if (!(task instanceof UnknownElement)) {
            throw new BuildException("addTask called with a task that is not an unknown element");
        }
        this.nestedTask = (UnknownElement) task;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() {
        if (this.nestedTask == null) {
            throw new BuildException("Missing nested element");
        }
        if (this.name == null) {
            throw new BuildException("Name not specified");
        }
        this.name = ProjectHelper.genComponentName(getURI(), this.name);
        ComponentHelper componentHelper = ComponentHelper.getComponentHelper(getProject());
        String strGenComponentName = ProjectHelper.genComponentName(this.nestedTask.getNamespace(), this.nestedTask.getTag());
        AntTypeDefinition definition = componentHelper.getDefinition(strGenComponentName);
        if (definition == null) {
            throw new BuildException("Unable to find typedef " + strGenComponentName);
        }
        PreSetDefinition preSetDefinition = new PreSetDefinition(definition, this.nestedTask);
        preSetDefinition.setName(this.name);
        componentHelper.addDataTypeDefinition(preSetDefinition);
        log("defining preset " + this.name, 3);
    }

    public static class PreSetDefinition extends AntTypeDefinition {
        private UnknownElement element;
        private AntTypeDefinition parent;

        @Override // org.apache.tools.ant.AntTypeDefinition
        public Object create(Project project) {
            return this;
        }

        public PreSetDefinition(AntTypeDefinition antTypeDefinition, UnknownElement unknownElement) {
            if (antTypeDefinition instanceof PreSetDefinition) {
                PreSetDefinition preSetDefinition = (PreSetDefinition) antTypeDefinition;
                unknownElement.applyPreSet(preSetDefinition.element);
                antTypeDefinition = preSetDefinition.parent;
            }
            this.parent = antTypeDefinition;
            this.element = unknownElement;
        }

        @Override // org.apache.tools.ant.AntTypeDefinition
        public void setClass(Class cls) {
            throw new BuildException("Not supported");
        }

        @Override // org.apache.tools.ant.AntTypeDefinition
        public void setClassName(String str) {
            throw new BuildException("Not supported");
        }

        @Override // org.apache.tools.ant.AntTypeDefinition
        public String getClassName() {
            return this.parent.getClassName();
        }

        @Override // org.apache.tools.ant.AntTypeDefinition
        public void setAdapterClass(Class cls) {
            throw new BuildException("Not supported");
        }

        @Override // org.apache.tools.ant.AntTypeDefinition
        public void setAdaptToClass(Class cls) {
            throw new BuildException("Not supported");
        }

        @Override // org.apache.tools.ant.AntTypeDefinition
        public void setClassLoader(ClassLoader classLoader) {
            throw new BuildException("Not supported");
        }

        @Override // org.apache.tools.ant.AntTypeDefinition
        public ClassLoader getClassLoader() {
            return this.parent.getClassLoader();
        }

        @Override // org.apache.tools.ant.AntTypeDefinition
        public Class getExposedClass(Project project) {
            return this.parent.getExposedClass(project);
        }

        @Override // org.apache.tools.ant.AntTypeDefinition
        public Class getTypeClass(Project project) {
            return this.parent.getTypeClass(project);
        }

        @Override // org.apache.tools.ant.AntTypeDefinition
        public void checkClass(Project project) {
            this.parent.checkClass(project);
        }

        public Object createObject(Project project) {
            return this.parent.create(project);
        }

        public UnknownElement getPreSets() {
            return this.element;
        }

        @Override // org.apache.tools.ant.AntTypeDefinition
        public boolean sameDefinition(AntTypeDefinition antTypeDefinition, Project project) {
            AntTypeDefinition antTypeDefinition2;
            if (antTypeDefinition != null && antTypeDefinition.getClass() == getClass() && (antTypeDefinition2 = this.parent) != null) {
                PreSetDefinition preSetDefinition = (PreSetDefinition) antTypeDefinition;
                if (antTypeDefinition2.sameDefinition(preSetDefinition.parent, project) && this.element.similar(preSetDefinition.element)) {
                    return true;
                }
            }
            return false;
        }

        @Override // org.apache.tools.ant.AntTypeDefinition
        public boolean similarDefinition(AntTypeDefinition antTypeDefinition, Project project) {
            AntTypeDefinition antTypeDefinition2;
            if (antTypeDefinition != null && antTypeDefinition.getClass().getName().equals(getClass().getName()) && (antTypeDefinition2 = this.parent) != null) {
                PreSetDefinition preSetDefinition = (PreSetDefinition) antTypeDefinition;
                if (antTypeDefinition2.similarDefinition(preSetDefinition.parent, project) && this.element.similar(preSetDefinition.element)) {
                    return true;
                }
            }
            return false;
        }
    }
}
