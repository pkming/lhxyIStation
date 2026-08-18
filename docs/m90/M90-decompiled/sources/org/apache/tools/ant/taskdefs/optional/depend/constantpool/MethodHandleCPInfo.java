package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class MethodHandleCPInfo extends ConstantPoolEntry {
    private int nameAndTypeIndex;
    private ConstantPoolEntry reference;
    private int referenceIndex;
    private ReferenceKind referenceKind;

    public enum ReferenceKind {
        REF_getField(1),
        REF_getStatic(2),
        REF_putField(3),
        REF_putStatic(4),
        REF_invokeVirtual(5),
        REF_invokeStatic(6),
        REF_invokeSpecial(7),
        REF_newInvokeSpecial(8),
        REF_invokeInterface(9);

        private final int referenceKind;

        ReferenceKind(int i) {
            this.referenceKind = i;
        }
    }

    public MethodHandleCPInfo() {
        super(15, 1);
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry
    public void read(DataInputStream dataInputStream) throws IOException {
        this.referenceKind = ReferenceKind.values()[dataInputStream.readUnsignedByte() - 1];
        this.referenceIndex = dataInputStream.readUnsignedShort();
    }

    public String toString() {
        if (isResolved()) {
            return "MethodHandle : " + this.reference.toString();
        }
        return "MethodHandle : Reference kind = " + this.referenceKind + "Reference index = " + this.referenceIndex;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry
    public void resolve(ConstantPool constantPool) {
        ConstantPoolEntry entry = constantPool.getEntry(this.referenceIndex);
        this.reference = entry;
        entry.resolve(constantPool);
        super.resolve(constantPool);
    }
}
