package org.apache.tools.ant;

import android.app.Instrumentation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.attribute.EnableAttribute;
import org.apache.tools.ant.taskdefs.MacroDef;
import org.apache.tools.ant.taskdefs.MacroInstance;
import org.apache.tools.ant.util.CollectionUtils;
import org.xml.sax.AttributeList;
import org.xml.sax.helpers.AttributeListImpl;

/* JADX INFO: loaded from: classes3.dex */
public class RuntimeConfigurable implements Serializable {
    private static final Hashtable<String, Object> EMPTY_HASHTABLE = new Hashtable<>(0);
    private static final long serialVersionUID = 1;
    private transient AttributeList attributes;
    private String elementTag = null;
    private List<RuntimeConfigurable> children = null;
    private transient Object wrappedObject = null;
    private transient boolean namespacedAttribute = false;
    private LinkedHashMap<String, Object> attributeMap = null;
    private StringBuffer characters = null;
    private boolean proxyConfigured = false;
    private String polyType = null;
    private String id = null;

    public RuntimeConfigurable(Object obj, String str) {
        setProxy(obj);
        setElementTag(str);
        if (obj instanceof Task) {
            ((Task) obj).setRuntimeConfigurableWrapper(this);
        }
    }

    public synchronized void setProxy(Object obj) {
        this.wrappedObject = obj;
        this.proxyConfigured = false;
    }

    private static class EnableAttributeConsumer {
        public void add(EnableAttribute enableAttribute) {
        }

        private EnableAttributeConsumer() {
        }
    }

    private static class AttributeComponentInformation {
        String componentName;
        boolean restricted;

        private AttributeComponentInformation(String str, boolean z) {
            this.componentName = str;
            this.restricted = z;
        }

        public String getComponentName() {
            return this.componentName;
        }

        public boolean isRestricted() {
            return this.restricted;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private AttributeComponentInformation isRestrictedAttribute(String str, ComponentHelper componentHelper) {
        boolean z = false;
        String str2 = null;
        Object[] objArr = 0;
        Object[] objArr2 = 0;
        Object[] objArr3 = 0;
        Object[] objArr4 = 0;
        if (str.indexOf(58) == -1) {
            return new AttributeComponentInformation(str2, z);
        }
        String strAttrToComponent = attrToComponent(str);
        if (componentHelper.getRestrictedDefinitions(ProjectHelper.nsToComponentName(ProjectHelper.extractUriFromComponentName(strAttrToComponent))) == null) {
            return new AttributeComponentInformation(objArr3 == true ? 1 : 0, z);
        }
        return new AttributeComponentInformation(strAttrToComponent, true);
    }

    public boolean isEnabled(UnknownElement unknownElement) {
        if (!this.namespacedAttribute) {
            return true;
        }
        ComponentHelper componentHelper = ComponentHelper.getComponentHelper(unknownElement.getProject());
        IntrospectionHelper helper = IntrospectionHelper.getHelper(unknownElement.getProject(), EnableAttributeConsumer.class);
        for (int i = 0; i < this.attributeMap.keySet().size(); i++) {
            String str = (String) this.attributeMap.keySet().toArray()[i];
            AttributeComponentInformation attributeComponentInformationIsRestrictedAttribute = isRestrictedAttribute(str, componentHelper);
            if (attributeComponentInformationIsRestrictedAttribute.isRestricted()) {
                String str2 = (String) this.attributeMap.get(str);
                try {
                    EnableAttribute enableAttribute = (EnableAttribute) helper.createElement(unknownElement.getProject(), new EnableAttributeConsumer(), attributeComponentInformationIsRestrictedAttribute.getComponentName());
                    if (enableAttribute != null && !enableAttribute.isEnabled(unknownElement, unknownElement.getProject().replaceProperties(str2))) {
                        return false;
                    }
                } catch (BuildException unused) {
                    throw new BuildException("Unsupported attribute " + attributeComponentInformationIsRestrictedAttribute.getComponentName());
                }
            }
        }
        return true;
    }

    private String attrToComponent(String str) {
        int iLastIndexOf = str.lastIndexOf(58);
        return str.substring(0, str.lastIndexOf(58, iLastIndexOf - 1)) + str.substring(iLastIndexOf);
    }

    synchronized void setCreator(IntrospectionHelper.Creator creator) {
    }

    public synchronized Object getProxy() {
        return this.wrappedObject;
    }

    public synchronized String getId() {
        return this.id;
    }

    public synchronized String getPolyType() {
        return this.polyType;
    }

    public synchronized void setPolyType(String str) {
        this.polyType = str;
    }

    public synchronized void setAttributes(AttributeList attributeList) {
        this.attributes = new AttributeListImpl(attributeList);
        for (int i = 0; i < attributeList.getLength(); i++) {
            setAttribute(attributeList.getName(i), attributeList.getValue(i));
        }
    }

    public synchronized void setAttribute(String str, String str2) {
        if (str.indexOf(58) != -1) {
            this.namespacedAttribute = true;
        }
        setAttribute(str, (Object) str2);
    }

    public synchronized void setAttribute(String str, Object obj) {
        String string = null;
        if (str.equalsIgnoreCase(ProjectHelper.ANT_TYPE)) {
            if (obj != null) {
                string = obj.toString();
            }
            this.polyType = string;
        } else {
            if (this.attributeMap == null) {
                this.attributeMap = new LinkedHashMap<>();
            }
            if (str.equalsIgnoreCase("refid") && !this.attributeMap.isEmpty()) {
                LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
                linkedHashMap.put(str, obj);
                linkedHashMap.putAll(this.attributeMap);
                this.attributeMap = linkedHashMap;
            } else {
                this.attributeMap.put(str, obj);
            }
            if (str.equals(Instrumentation.REPORT_KEY_IDENTIFIER)) {
                if (obj != null) {
                    string = obj.toString();
                }
                this.id = string;
            }
        }
    }

    public synchronized void removeAttribute(String str) {
        this.attributeMap.remove(str);
    }

    public synchronized Hashtable<String, Object> getAttributeMap() {
        return this.attributeMap == null ? EMPTY_HASHTABLE : new Hashtable<>(this.attributeMap);
    }

    public synchronized AttributeList getAttributes() {
        return this.attributes;
    }

    public synchronized void addChild(RuntimeConfigurable runtimeConfigurable) {
        List<RuntimeConfigurable> arrayList = this.children;
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        this.children = arrayList;
        arrayList.add(runtimeConfigurable);
    }

    synchronized RuntimeConfigurable getChild(int i) {
        return this.children.get(i);
    }

    public synchronized Enumeration<RuntimeConfigurable> getChildren() {
        List<RuntimeConfigurable> list;
        list = this.children;
        return list == null ? new CollectionUtils.EmptyEnumeration<>() : Collections.enumeration(list);
    }

    public synchronized void addText(String str) {
        if (str.length() == 0) {
            return;
        }
        StringBuffer stringBuffer = this.characters;
        this.characters = stringBuffer == null ? new StringBuffer(str) : stringBuffer.append(str);
    }

    public synchronized void addText(char[] cArr, int i, int i2) {
        if (i2 == 0) {
            return;
        }
        StringBuffer stringBuffer = this.characters;
        if (stringBuffer == null) {
            stringBuffer = new StringBuffer(i2);
        }
        this.characters = stringBuffer.append(cArr, i, i2);
    }

    public synchronized StringBuffer getText() {
        StringBuffer stringBuffer;
        stringBuffer = this.characters;
        if (stringBuffer == null) {
            stringBuffer = new StringBuffer(0);
        }
        return stringBuffer;
    }

    public synchronized void setElementTag(String str) {
        this.elementTag = str;
    }

    public synchronized String getElementTag() {
        return this.elementTag;
    }

    public void maybeConfigure(Project project) throws BuildException {
        maybeConfigure(project, true);
    }

    public synchronized void maybeConfigure(Project project, boolean z) throws BuildException {
        Object properties;
        if (this.proxyConfigured) {
            return;
        }
        Object proxy = this.wrappedObject;
        if (proxy instanceof TypeAdapter) {
            proxy = ((TypeAdapter) proxy).getProxy();
        }
        IntrospectionHelper helper = IntrospectionHelper.getHelper(project, proxy.getClass());
        ComponentHelper componentHelper = ComponentHelper.getComponentHelper(project);
        LinkedHashMap<String, Object> linkedHashMap = this.attributeMap;
        if (linkedHashMap != null) {
            for (Map.Entry<String, Object> entry : linkedHashMap.entrySet()) {
                String key = entry.getKey();
                if (!isRestrictedAttribute(key, componentHelper).isRestricted()) {
                    Object value = entry.getValue();
                    if (value instanceof Evaluable) {
                        properties = ((Evaluable) value).eval();
                    } else {
                        properties = PropertyHelper.getPropertyHelper(project).parseProperties(value.toString());
                    }
                    if (proxy instanceof MacroInstance) {
                        Iterator<MacroDef.Attribute> it = ((MacroInstance) proxy).getMacroDef().getAttributes().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            MacroDef.Attribute next = it.next();
                            if (next.getName().equals(key)) {
                                if (next.isDoubleExpanding()) {
                                    break;
                                }
                            }
                        }
                    }
                    value = properties;
                    try {
                        helper.setAttribute(project, proxy, key, value);
                    } catch (UnsupportedAttributeException e) {
                        if (!key.equals(Instrumentation.REPORT_KEY_IDENTIFIER)) {
                            if (getElementTag() == null) {
                                throw e;
                            }
                            throw new BuildException(getElementTag() + " doesn't support the \"" + e.getAttribute() + "\" attribute", e);
                        }
                    } catch (BuildException e2) {
                        if (!key.equals(Instrumentation.REPORT_KEY_IDENTIFIER)) {
                            throw e2;
                        }
                    }
                }
            }
        }
        StringBuffer stringBuffer = this.characters;
        if (stringBuffer != null) {
            ProjectHelper.addText(project, this.wrappedObject, stringBuffer.substring(0));
        }
        String str = this.id;
        if (str != null) {
            project.addReference(str, this.wrappedObject);
        }
        this.proxyConfigured = true;
    }

    public void reconfigure(Project project) {
        this.proxyConfigured = false;
        maybeConfigure(project);
    }

    public void applyPreSet(RuntimeConfigurable runtimeConfigurable) {
        LinkedHashMap<String, Object> linkedHashMap = runtimeConfigurable.attributeMap;
        if (linkedHashMap != null) {
            for (String str : linkedHashMap.keySet()) {
                LinkedHashMap<String, Object> linkedHashMap2 = this.attributeMap;
                if (linkedHashMap2 == null || linkedHashMap2.get(str) == null) {
                    setAttribute(str, (String) runtimeConfigurable.attributeMap.get(str));
                }
            }
        }
        String str2 = this.polyType;
        if (str2 == null) {
            str2 = runtimeConfigurable.polyType;
        }
        this.polyType = str2;
        if (runtimeConfigurable.children != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(runtimeConfigurable.children);
            List<RuntimeConfigurable> list = this.children;
            if (list != null) {
                arrayList.addAll(list);
            }
            this.children = arrayList;
        }
        if (runtimeConfigurable.characters != null) {
            StringBuffer stringBuffer = this.characters;
            if (stringBuffer == null || stringBuffer.toString().trim().length() == 0) {
                this.characters = new StringBuffer(runtimeConfigurable.characters.toString());
            }
        }
    }
}
