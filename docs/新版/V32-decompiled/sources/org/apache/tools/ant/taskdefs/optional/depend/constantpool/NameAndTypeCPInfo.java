package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class NameAndTypeCPInfo extends ConstantPoolEntry {
    private int descriptorIndex;
    private String name;
    private int nameIndex;
    private String type;

    public NameAndTypeCPInfo() {
        super(12, 1);
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry
    public void read(DataInputStream dataInputStream) throws IOException {
        this.nameIndex = dataInputStream.readUnsignedShort();
        this.descriptorIndex = dataInputStream.readUnsignedShort();
    }

    public String toString() {
        if (isResolved()) {
            return "Name = " + this.name + ", type = " + this.type;
        }
        return "Name index = " + this.nameIndex + ", descriptor index = " + this.descriptorIndex;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry
    public void resolve(ConstantPool constantPool) {
        this.name = ((Utf8CPInfo) constantPool.getEntry(this.nameIndex)).getValue();
        this.type = ((Utf8CPInfo) constantPool.getEntry(this.descriptorIndex)).getValue();
        super.resolve(constantPool);
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }
}
