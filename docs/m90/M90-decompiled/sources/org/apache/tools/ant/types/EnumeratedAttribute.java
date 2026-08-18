package org.apache.tools.ant.types;

import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public abstract class EnumeratedAttribute {
    private int index = -1;
    protected String value;

    public abstract String[] getValues();

    protected EnumeratedAttribute() {
    }

    public static EnumeratedAttribute getInstance(Class<? extends EnumeratedAttribute> cls, String str) throws BuildException {
        if (!EnumeratedAttribute.class.isAssignableFrom(cls)) {
            throw new BuildException("You have to provide a subclass from EnumeratedAttribut as clazz-parameter.");
        }
        try {
            EnumeratedAttribute enumeratedAttributeNewInstance = cls.newInstance();
            enumeratedAttributeNewInstance.setValue(str);
            return enumeratedAttributeNewInstance;
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    public final void setValue(String str) throws BuildException {
        int iIndexOfValue = indexOfValue(str);
        if (iIndexOfValue == -1) {
            throw new BuildException(str + " is not a legal value for this attribute");
        }
        this.index = iIndexOfValue;
        this.value = str;
    }

    public final boolean containsValue(String str) {
        return indexOfValue(str) != -1;
    }

    public final int indexOfValue(String str) {
        String[] values = getValues();
        if (values != null && str != null) {
            for (int i = 0; i < values.length; i++) {
                if (str.equals(values[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public final String getValue() {
        return this.value;
    }

    public final int getIndex() {
        return this.index;
    }

    public String toString() {
        return getValue();
    }
}
