package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class InvokeDynamicCPInfo extends ConstantCPInfo {
    private int bootstrapMethodAttrIndex;
    private NameAndTypeCPInfo nameAndTypeCPInfo;
    private int nameAndTypeIndex;

    public InvokeDynamicCPInfo() {
        super(18, 1);
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry
    public void read(DataInputStream dataInputStream) throws IOException {
        this.bootstrapMethodAttrIndex = dataInputStream.readUnsignedShort();
        this.nameAndTypeIndex = dataInputStream.readUnsignedShort();
    }

    public String toString() {
        if (isResolved()) {
            return "Name = " + this.nameAndTypeCPInfo.getName() + ", type = " + this.nameAndTypeCPInfo.getType();
        }
        return "BootstrapMethodAttrIndex inx = " + this.bootstrapMethodAttrIndex + "NameAndType index = " + this.nameAndTypeIndex;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry
    public void resolve(ConstantPool constantPool) {
        NameAndTypeCPInfo nameAndTypeCPInfo = (NameAndTypeCPInfo) constantPool.getEntry(this.nameAndTypeIndex);
        this.nameAndTypeCPInfo = nameAndTypeCPInfo;
        nameAndTypeCPInfo.resolve(constantPool);
        super.resolve(constantPool);
    }
}
