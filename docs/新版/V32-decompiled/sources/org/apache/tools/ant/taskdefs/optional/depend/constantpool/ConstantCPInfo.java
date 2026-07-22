package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

/* JADX INFO: loaded from: classes3.dex */
public abstract class ConstantCPInfo extends ConstantPoolEntry {
    private Object value;

    protected ConstantCPInfo(int i, int i2) {
        super(i, i2);
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object obj) {
        this.value = obj;
    }
}
