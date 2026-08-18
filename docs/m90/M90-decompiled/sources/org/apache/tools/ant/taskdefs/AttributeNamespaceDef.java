package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.attribute.AttributeNamespace;

/* JADX INFO: loaded from: classes3.dex */
public final class AttributeNamespaceDef extends AntlibDefinition {
    @Override // org.apache.tools.ant.Task
    public void execute() {
        String strNsToComponentName = ProjectHelper.nsToComponentName(getURI());
        AntTypeDefinition antTypeDefinition = new AntTypeDefinition();
        antTypeDefinition.setName(strNsToComponentName);
        antTypeDefinition.setClassName(AttributeNamespace.class.getName());
        antTypeDefinition.setClass(AttributeNamespace.class);
        antTypeDefinition.setRestrict(true);
        antTypeDefinition.setClassLoader(AttributeNamespace.class.getClassLoader());
        ComponentHelper.getComponentHelper(getProject()).addDataTypeDefinition(antTypeDefinition);
    }
}
