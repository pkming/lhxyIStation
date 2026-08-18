package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.ProjectHelper;

/* JADX INFO: loaded from: classes3.dex */
public class TypeFound extends ProjectComponent implements Condition {
    private String name;
    private String uri;

    public void setName(String str) {
        this.name = str;
    }

    public void setURI(String str) {
        this.uri = str;
    }

    protected boolean doesTypeExist(String str) {
        ComponentHelper componentHelper = ComponentHelper.getComponentHelper(getProject());
        String strGenComponentName = ProjectHelper.genComponentName(this.uri, str);
        AntTypeDefinition definition = componentHelper.getDefinition(strGenComponentName);
        if (definition == null) {
            return false;
        }
        boolean z = definition.getExposedClass(getProject()) != null;
        if (!z) {
            log(componentHelper.diagnoseCreationFailure(strGenComponentName, "type"), 3);
        }
        return z;
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        String str = this.name;
        if (str == null) {
            throw new BuildException("No type specified");
        }
        return doesTypeExist(str);
    }
}
