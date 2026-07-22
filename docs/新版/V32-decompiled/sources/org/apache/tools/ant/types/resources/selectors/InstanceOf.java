package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.types.Resource;

/* JADX INFO: loaded from: classes3.dex */
public class InstanceOf implements ResourceSelector {
    private static final String ONE_ONLY = "Exactly one of class|type must be set.";
    private Class<?> clazz;
    private Project project;
    private String type;
    private String uri;

    public void setProject(Project project) {
        this.project = project;
    }

    public void setClass(Class<?> cls) {
        if (this.clazz != null) {
            throw new BuildException("The class attribute has already been set.");
        }
        this.clazz = cls;
    }

    public void setType(String str) {
        this.type = str;
    }

    public void setURI(String str) {
        this.uri = str;
    }

    public Class<?> getCheckClass() {
        return this.clazz;
    }

    public String getType() {
        return this.type;
    }

    public String getURI() {
        return this.uri;
    }

    @Override // org.apache.tools.ant.types.resources.selectors.ResourceSelector
    public boolean isSelected(Resource resource) {
        Class<?> clsInnerGetTypeClass = this.clazz;
        boolean z = clsInnerGetTypeClass == null;
        String str = this.type;
        if (z == (str == null)) {
            throw new BuildException(ONE_ONLY);
        }
        if (str != null) {
            Project project = this.project;
            if (project == null) {
                throw new BuildException("No project set for InstanceOf ResourceSelector; the type attribute is invalid.");
            }
            AntTypeDefinition definition = ComponentHelper.getComponentHelper(project).getDefinition(ProjectHelper.genComponentName(this.uri, this.type));
            if (definition == null) {
                throw new BuildException("type " + this.type + " not found.");
            }
            try {
                clsInnerGetTypeClass = definition.innerGetTypeClass();
            } catch (ClassNotFoundException e) {
                throw new BuildException(e);
            }
        }
        return clsInnerGetTypeClass.isAssignableFrom(resource.getClass());
    }
}
