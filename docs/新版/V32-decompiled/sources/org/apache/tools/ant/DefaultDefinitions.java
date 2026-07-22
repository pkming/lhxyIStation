package org.apache.tools.ant;

/* JADX INFO: loaded from: classes3.dex */
public final class DefaultDefinitions {
    private static final String IF_NAMESPACE = "ant:if";
    private static final String OATA = "org.apache.tools.ant.";
    private static final String UNLESS_NAMESPACE = "ant:unless";
    private final ComponentHelper componentHelper;

    public DefaultDefinitions(ComponentHelper componentHelper) {
        this.componentHelper = componentHelper;
    }

    public void execute() {
        attributeNamespaceDef(IF_NAMESPACE);
        attributeNamespaceDef(UNLESS_NAMESPACE);
        ifUnlessDef("true", "IfTrueAttribute");
        ifUnlessDef("set", "IfSetAttribute");
        ifUnlessDef("blank", "IfBlankAttribute");
    }

    private void attributeNamespaceDef(String str) {
        AntTypeDefinition antTypeDefinition = new AntTypeDefinition();
        antTypeDefinition.setName(ProjectHelper.nsToComponentName(str));
        antTypeDefinition.setClassName("org.apache.tools.ant.attribute.AttributeNamespace");
        antTypeDefinition.setClassLoader(getClass().getClassLoader());
        antTypeDefinition.setRestrict(true);
        this.componentHelper.addDataTypeDefinition(antTypeDefinition);
    }

    private void ifUnlessDef(String str, String str2) {
        String str3 = "org.apache.tools.ant.attribute." + str2;
        componentDef(IF_NAMESPACE, str, str3);
        componentDef(UNLESS_NAMESPACE, str, str3 + "$Unless");
    }

    private void componentDef(String str, String str2, String str3) {
        AntTypeDefinition antTypeDefinition = new AntTypeDefinition();
        ProjectHelper.genComponentName(str, str2);
        antTypeDefinition.setName(ProjectHelper.genComponentName(str, str2));
        antTypeDefinition.setClassName(str3);
        antTypeDefinition.setClassLoader(getClass().getClassLoader());
        antTypeDefinition.setRestrict(true);
        this.componentHelper.addDataTypeDefinition(antTypeDefinition);
    }
}
