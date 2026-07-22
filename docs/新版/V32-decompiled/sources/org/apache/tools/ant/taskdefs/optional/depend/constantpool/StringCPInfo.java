package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class StringCPInfo extends ConstantCPInfo {
    private int index;

    public StringCPInfo() {
        super(8, 1);
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry
    public void read(DataInputStream dataInputStream) throws IOException {
        this.index = dataInputStream.readUnsignedShort();
        setValue("unresolved");
    }

    public String toString() {
        return "String Constant Pool Entry for " + getValue() + "[" + this.index + "]";
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry
    public void resolve(ConstantPool constantPool) {
        setValue(((Utf8CPInfo) constantPool.getEntry(this.index)).getValue());
        super.resolve(constantPool);
    }
}
