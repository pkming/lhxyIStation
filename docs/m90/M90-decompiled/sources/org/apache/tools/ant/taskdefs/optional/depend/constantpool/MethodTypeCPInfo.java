package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class MethodTypeCPInfo extends ConstantCPInfo {
    private String methodDescriptor;
    private int methodDescriptorIndex;

    public MethodTypeCPInfo() {
        super(16, 1);
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry
    public void read(DataInputStream dataInputStream) throws IOException {
        this.methodDescriptorIndex = dataInputStream.readUnsignedShort();
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry
    public void resolve(ConstantPool constantPool) {
        Utf8CPInfo utf8CPInfo = (Utf8CPInfo) constantPool.getEntry(this.methodDescriptorIndex);
        utf8CPInfo.resolve(constantPool);
        this.methodDescriptor = utf8CPInfo.getValue();
        super.resolve(constantPool);
    }

    public String toString() {
        if (!isResolved()) {
            return "MethodDescriptorIndex: " + this.methodDescriptorIndex;
        }
        return "MethodDescriptor: " + this.methodDescriptor;
    }
}
