package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class MethodRefCPInfo extends ConstantPoolEntry {
    private int classIndex;
    private String methodClassName;
    private String methodName;
    private String methodType;
    private int nameAndTypeIndex;

    public MethodRefCPInfo() {
        super(10, 1);
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry
    public void read(DataInputStream dataInputStream) throws IOException {
        this.classIndex = dataInputStream.readUnsignedShort();
        this.nameAndTypeIndex = dataInputStream.readUnsignedShort();
    }

    public String toString() {
        if (isResolved()) {
            return "Method : Class = " + this.methodClassName + ", name = " + this.methodName + ", type = " + this.methodType;
        }
        return "Method : Class index = " + this.classIndex + ", name and type index = " + this.nameAndTypeIndex;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry
    public void resolve(ConstantPool constantPool) {
        ClassCPInfo classCPInfo = (ClassCPInfo) constantPool.getEntry(this.classIndex);
        classCPInfo.resolve(constantPool);
        this.methodClassName = classCPInfo.getClassName();
        NameAndTypeCPInfo nameAndTypeCPInfo = (NameAndTypeCPInfo) constantPool.getEntry(this.nameAndTypeIndex);
        nameAndTypeCPInfo.resolve(constantPool);
        this.methodName = nameAndTypeCPInfo.getName();
        this.methodType = nameAndTypeCPInfo.getType();
        super.resolve(constantPool);
    }

    public String getMethodClassName() {
        return this.methodClassName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public String getMethodType() {
        return this.methodType;
    }
}
