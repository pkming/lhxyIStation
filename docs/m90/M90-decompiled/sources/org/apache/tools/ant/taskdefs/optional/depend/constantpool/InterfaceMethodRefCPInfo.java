package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class InterfaceMethodRefCPInfo extends ConstantPoolEntry {
    private int classIndex;
    private String interfaceMethodClassName;
    private String interfaceMethodName;
    private String interfaceMethodType;
    private int nameAndTypeIndex;

    public InterfaceMethodRefCPInfo() {
        super(11, 1);
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry
    public void read(DataInputStream dataInputStream) throws IOException {
        this.classIndex = dataInputStream.readUnsignedShort();
        this.nameAndTypeIndex = dataInputStream.readUnsignedShort();
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry
    public void resolve(ConstantPool constantPool) {
        ClassCPInfo classCPInfo = (ClassCPInfo) constantPool.getEntry(this.classIndex);
        classCPInfo.resolve(constantPool);
        this.interfaceMethodClassName = classCPInfo.getClassName();
        NameAndTypeCPInfo nameAndTypeCPInfo = (NameAndTypeCPInfo) constantPool.getEntry(this.nameAndTypeIndex);
        nameAndTypeCPInfo.resolve(constantPool);
        this.interfaceMethodName = nameAndTypeCPInfo.getName();
        this.interfaceMethodType = nameAndTypeCPInfo.getType();
        super.resolve(constantPool);
    }

    public String toString() {
        if (isResolved()) {
            return "InterfaceMethod : Class = " + this.interfaceMethodClassName + ", name = " + this.interfaceMethodName + ", type = " + this.interfaceMethodType;
        }
        return "InterfaceMethod : Class index = " + this.classIndex + ", name and type index = " + this.nameAndTypeIndex;
    }

    public String getInterfaceMethodClassName() {
        return this.interfaceMethodClassName;
    }

    public String getInterfaceMethodName() {
        return this.interfaceMethodName;
    }

    public String getInterfaceMethodType() {
        return this.interfaceMethodType;
    }
}
