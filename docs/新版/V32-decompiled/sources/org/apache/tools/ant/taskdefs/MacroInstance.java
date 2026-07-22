package org.apache.tools.ant.taskdefs;

import android.app.Instrumentation;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicAttribute;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.property.LocalProperties;
import org.apache.tools.ant.taskdefs.MacroDef;

/* JADX INFO: loaded from: classes3.dex */
public class MacroInstance extends Task implements DynamicAttribute, TaskContainer {
    private static final int STATE_EXPECT_BRACKET = 1;
    private static final int STATE_EXPECT_NAME = 2;
    private static final int STATE_NORMAL = 0;
    private Hashtable<String, String> localAttributes;
    private MacroDef macroDef;
    private Map<String, UnknownElement> presentElements;
    private Map<String, String> map = new HashMap();
    private Map<String, MacroDef.TemplateElement> nsElements = null;
    private String text = null;
    private String implicitTag = null;
    private List<Task> unknownElements = new ArrayList();

    public void setMacroDef(MacroDef macroDef) {
        this.macroDef = macroDef;
    }

    public MacroDef getMacroDef() {
        return this.macroDef;
    }

    @Override // org.apache.tools.ant.DynamicAttribute
    public void setDynamicAttribute(String str, String str2) {
        this.map.put(str, str2);
    }

    public Object createDynamicElement(String str) throws BuildException {
        throw new BuildException("Not implemented any more");
    }

    private Map<String, MacroDef.TemplateElement> getNsElements() {
        if (this.nsElements == null) {
            this.nsElements = new HashMap();
            for (Map.Entry<String, MacroDef.TemplateElement> entry : this.macroDef.getElements().entrySet()) {
                this.nsElements.put(entry.getKey(), entry.getValue());
                MacroDef.TemplateElement value = entry.getValue();
                if (value.isImplicit()) {
                    this.implicitTag = value.getName();
                }
            }
        }
        return this.nsElements;
    }

    @Override // org.apache.tools.ant.TaskContainer
    public void addTask(Task task) {
        this.unknownElements.add(task);
    }

    private void processTasks() {
        if (this.implicitTag != null) {
            return;
        }
        Iterator<Task> it = this.unknownElements.iterator();
        while (it.hasNext()) {
            UnknownElement unknownElement = (UnknownElement) it.next();
            String lowerCase = ProjectHelper.extractNameFromComponentName(unknownElement.getTag()).toLowerCase(Locale.ENGLISH);
            if (getNsElements().get(lowerCase) == null) {
                throw new BuildException("unsupported element " + lowerCase);
            }
            if (this.presentElements.get(lowerCase) != null) {
                throw new BuildException("Element " + lowerCase + " already present");
            }
            this.presentElements.put(lowerCase, unknownElement);
        }
    }

    public static class Element implements TaskContainer {
        private List<Task> unknownElements = new ArrayList();

        @Override // org.apache.tools.ant.TaskContainer
        public void addTask(Task task) {
            this.unknownElements.add(task);
        }

        public List<Task> getUnknownElements() {
            return this.unknownElements;
        }
    }

    private String macroSubs(String str, Map<String, String> map) {
        if (str == null) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBuffer2 = null;
        char c = 0;
        for (int i = 0; i < str.length(); i++) {
            char cCharAt = str.charAt(i);
            if (c != 0) {
                if (c != 1) {
                    if (c == 2) {
                        if (cCharAt == '}') {
                            String lowerCase = stringBuffer2.toString().toLowerCase(Locale.ENGLISH);
                            String str2 = map.get(lowerCase);
                            if (str2 == null) {
                                stringBuffer.append("@{");
                                stringBuffer.append(lowerCase);
                                stringBuffer.append("}");
                            } else {
                                stringBuffer.append(str2);
                            }
                            stringBuffer2 = null;
                            c = 0;
                        } else {
                            stringBuffer2.append(cCharAt);
                        }
                    }
                } else if (cCharAt == '{') {
                    stringBuffer2 = new StringBuffer();
                    c = 2;
                } else {
                    if (cCharAt == '@') {
                        stringBuffer.append('@');
                    } else {
                        stringBuffer.append('@');
                        stringBuffer.append(cCharAt);
                    }
                    c = 0;
                }
            } else if (cCharAt == '@') {
                c = 1;
            } else {
                stringBuffer.append(cCharAt);
            }
        }
        if (c == 1) {
            stringBuffer.append('@');
        } else if (c == 2) {
            stringBuffer.append("@{");
            stringBuffer.append(stringBuffer2.toString());
        }
        return stringBuffer.toString();
    }

    public void addText(String str) {
        this.text = str;
    }

    private UnknownElement copy(UnknownElement unknownElement, boolean z) {
        UnknownElement unknownElement2 = new UnknownElement(unknownElement.getTag());
        unknownElement2.setNamespace(unknownElement.getNamespace());
        unknownElement2.setProject(getProject());
        unknownElement2.setQName(unknownElement.getQName());
        unknownElement2.setTaskType(unknownElement.getTaskType());
        unknownElement2.setTaskName(unknownElement.getTaskName());
        unknownElement2.setLocation(this.macroDef.getBackTrace() ? unknownElement.getLocation() : getLocation());
        if (getOwningTarget() == null) {
            Target target = new Target();
            target.setProject(getProject());
            unknownElement2.setOwningTarget(target);
        } else {
            unknownElement2.setOwningTarget(getOwningTarget());
        }
        RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(unknownElement2, unknownElement.getTaskName());
        runtimeConfigurable.setPolyType(unknownElement.getWrapper().getPolyType());
        for (Map.Entry<String, Object> entry : unknownElement.getWrapper().getAttributeMap().entrySet()) {
            runtimeConfigurable.setAttribute(entry.getKey(), macroSubs((String) entry.getValue(), this.localAttributes));
        }
        runtimeConfigurable.addText(macroSubs(unknownElement.getWrapper().getText().toString(), this.localAttributes));
        Enumeration<RuntimeConfigurable> children = unknownElement.getWrapper().getChildren();
        while (children.hasMoreElements()) {
            UnknownElement unknownElement3 = (UnknownElement) children.nextElement().getProxy();
            String taskType = unknownElement3.getTaskType();
            if (taskType != null) {
                taskType = taskType.toLowerCase(Locale.ENGLISH);
            }
            MacroDef.TemplateElement templateElement = getNsElements().get(taskType);
            if (templateElement == null || z) {
                UnknownElement unknownElementCopy = copy(unknownElement3, z);
                runtimeConfigurable.addChild(unknownElementCopy.getWrapper());
                unknownElement2.addChild(unknownElementCopy);
            } else if (templateElement.isImplicit()) {
                if (this.unknownElements.size() == 0 && !templateElement.isOptional()) {
                    throw new BuildException("Missing nested elements for implicit element " + templateElement.getName());
                }
                Iterator<Task> it = this.unknownElements.iterator();
                while (it.hasNext()) {
                    UnknownElement unknownElementCopy2 = copy((UnknownElement) it.next(), true);
                    runtimeConfigurable.addChild(unknownElementCopy2.getWrapper());
                    unknownElement2.addChild(unknownElementCopy2);
                }
            } else {
                UnknownElement unknownElement4 = this.presentElements.get(taskType);
                if (unknownElement4 == null) {
                    if (!templateElement.isOptional()) {
                        throw new BuildException("Required nested element " + templateElement.getName() + " missing");
                    }
                } else {
                    String string = unknownElement4.getWrapper().getText().toString();
                    if (!"".equals(string)) {
                        runtimeConfigurable.addText(macroSubs(string, this.localAttributes));
                    }
                    List<UnknownElement> children2 = unknownElement4.getChildren();
                    if (children2 != null) {
                        Iterator<UnknownElement> it2 = children2.iterator();
                        while (it2.hasNext()) {
                            UnknownElement unknownElementCopy3 = copy(it2.next(), true);
                            runtimeConfigurable.addChild(unknownElementCopy3.getWrapper());
                            unknownElement2.addChild(unknownElementCopy3);
                        }
                    }
                }
            }
        }
        return unknownElement2;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() {
        this.presentElements = new HashMap();
        getNsElements();
        processTasks();
        this.localAttributes = new Hashtable<>();
        HashSet hashSet = new HashSet(this.map.keySet());
        for (MacroDef.Attribute attribute : this.macroDef.getAttributes()) {
            String strMacroSubs = this.map.get(attribute.getName());
            if (strMacroSubs == null && "description".equals(attribute.getName())) {
                strMacroSubs = getDescription();
            }
            if (strMacroSubs == null) {
                strMacroSubs = macroSubs(attribute.getDefault(), this.localAttributes);
            }
            if (strMacroSubs == null) {
                throw new BuildException("required attribute " + attribute.getName() + " not set");
            }
            this.localAttributes.put(attribute.getName(), strMacroSubs);
            hashSet.remove(attribute.getName());
        }
        if (hashSet.contains(Instrumentation.REPORT_KEY_IDENTIFIER)) {
            hashSet.remove(Instrumentation.REPORT_KEY_IDENTIFIER);
        }
        if (this.macroDef.getText() != null) {
            if (this.text == null) {
                String str = this.macroDef.getText().getDefault();
                if (!this.macroDef.getText().getOptional() && str == null) {
                    throw new BuildException("required text missing");
                }
                this.text = str != null ? str : "";
            }
            if (this.macroDef.getText().getTrim()) {
                this.text = this.text.trim();
            }
            this.localAttributes.put(this.macroDef.getText().getName(), this.text);
        } else {
            String str2 = this.text;
            if (str2 != null && !str2.trim().equals("")) {
                throw new BuildException("The \"" + getTaskName() + "\" macro does not support nested text data.");
            }
        }
        if (hashSet.size() != 0) {
            throw new BuildException("Unknown attribute" + (hashSet.size() > 1 ? "s " : " ") + hashSet);
        }
        UnknownElement unknownElementCopy = copy(this.macroDef.getNestedTask(), false);
        unknownElementCopy.init();
        LocalProperties localProperties = LocalProperties.get(getProject());
        localProperties.enterScope();
        try {
            try {
                unknownElementCopy.perform();
            } catch (BuildException e) {
                if (this.macroDef.getBackTrace()) {
                    throw ProjectHelper.addLocationToBuildException(e, getLocation());
                }
                e.setLocation(getLocation());
                throw e;
            }
        } finally {
            this.presentElements = null;
            this.localAttributes = null;
            localProperties.exitScope();
        }
    }
}
