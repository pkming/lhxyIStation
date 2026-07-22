package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class FieldRefCPInfo extends ConstantPoolEntry {
    private int classIndex;
    private String fieldClassName;
    private String fieldName;
    private String fieldType;
    private int nameAndTypeIndex;

    public FieldRefCPInfo() {
        super(9, 1);
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
        this.fieldClassName = classCPInfo.getClassName();
        NameAndTypeCPInfo nameAndTypeCPInfo = (NameAndTypeCPInfo) constantPool.getEntry(this.nameAndTypeIndex);
        nameAndTypeCPInfo.resolve(constantPool);
        this.fieldName = nameAndTypeCPInfo.getName();
        this.fieldType = nameAndTypeCPInfo.getType();
        super.resolve(constantPool);
    }

    public String toString() {
        if (isResolved()) {
            return "Field : Class = " + this.fieldClassName + ", name = " + this.fieldName + ", type = " + this.fieldType;
        }
        return "Field : Class index = " + this.classIndex + ", name and type index = " + this.nameAndTypeIndex;
    }

    public String getFieldClassName() {
        return this.fieldClassName;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getFieldType() {
        return this.fieldType;
    }
}
