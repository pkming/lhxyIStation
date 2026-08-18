package org.apache.tools.ant.taskdefs.optional.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicConfigurator;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class ScriptDefBase extends Task implements DynamicConfigurator {
    private String text;
    private Map nestedElementMap = new HashMap();
    private Map attributes = new HashMap();

    @Override // org.apache.tools.ant.Task
    public void execute() {
        getScript().executeScript(this.attributes, this.nestedElementMap, this);
    }

    private ScriptDef getScript() {
        String taskType = getTaskType();
        Map map = (Map) getProject().getReference(MagicNames.SCRIPT_REPOSITORY);
        if (map == null) {
            throw new BuildException("Script repository not found for " + taskType);
        }
        ScriptDef scriptDef = (ScriptDef) map.get(getTaskType());
        if (scriptDef != null) {
            return scriptDef;
        }
        throw new BuildException("Script definition not found for " + taskType);
    }

    @Override // org.apache.tools.ant.DynamicElement
    public Object createDynamicElement(String str) {
        List arrayList = (List) this.nestedElementMap.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.nestedElementMap.put(str, arrayList);
        }
        Object objCreateNestedElement = getScript().createNestedElement(str);
        arrayList.add(objCreateNestedElement);
        return objCreateNestedElement;
    }

    @Override // org.apache.tools.ant.DynamicAttribute
    public void setDynamicAttribute(String str, String str2) {
        if (!getScript().isAttributeSupported(str)) {
            throw new BuildException("<" + getTaskType() + "> does not support the \"" + str + "\" attribute");
        }
        this.attributes.put(str, str2);
    }

    public void addText(String str) {
        this.text = getProject().replaceProperties(str);
    }

    public String getText() {
        return this.text;
    }

    public void fail(String str) {
        throw new BuildException(str);
    }
}
