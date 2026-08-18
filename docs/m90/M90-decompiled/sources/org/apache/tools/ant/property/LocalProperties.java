package org.apache.tools.ant.property;

import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;

/* JADX INFO: loaded from: classes3.dex */
public class LocalProperties extends InheritableThreadLocal<LocalPropertyStack> implements PropertyHelper.PropertyEvaluator, PropertyHelper.PropertySetter {
    public static synchronized LocalProperties get(Project project) {
        LocalProperties localProperties;
        localProperties = (LocalProperties) project.getReference(MagicNames.REFID_LOCAL_PROPERTIES);
        if (localProperties == null) {
            localProperties = new LocalProperties();
            project.addReference(MagicNames.REFID_LOCAL_PROPERTIES, localProperties);
            PropertyHelper.getPropertyHelper(project).add(localProperties);
        }
        return localProperties;
    }

    private LocalProperties() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.lang.ThreadLocal
    public synchronized LocalPropertyStack initialValue() {
        return new LocalPropertyStack();
    }

    private LocalPropertyStack current() {
        return (LocalPropertyStack) get();
    }

    public void addLocal(String str) {
        current().addLocal(str);
    }

    public void enterScope() {
        current().enterScope();
    }

    public void exitScope() {
        current().exitScope();
    }

    public void copy() {
        set(current().copy());
    }

    @Override // org.apache.tools.ant.PropertyHelper.PropertyEvaluator
    public Object evaluate(String str, PropertyHelper propertyHelper) {
        return current().evaluate(str, propertyHelper);
    }

    @Override // org.apache.tools.ant.PropertyHelper.PropertySetter
    public boolean setNew(String str, Object obj, PropertyHelper propertyHelper) {
        return current().setNew(str, obj, propertyHelper);
    }

    @Override // org.apache.tools.ant.PropertyHelper.PropertySetter
    public boolean set(String str, Object obj, PropertyHelper propertyHelper) {
        return current().set(str, obj, propertyHelper);
    }
}
