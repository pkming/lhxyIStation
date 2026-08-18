package de.innosystec.unrar.unpack.vm;

/* JADX INFO: loaded from: classes2.dex */
public enum VMOpType {
    VM_OPREG(0),
    VM_OPINT(1),
    VM_OPREGMEM(2),
    VM_OPNONE(3);

    private int opType;

    /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
    public static VMOpType[] valuesCustom() {
        VMOpType[] vMOpTypeArrValuesCustom = values();
        int length = vMOpTypeArrValuesCustom.length;
        VMOpType[] vMOpTypeArr = new VMOpType[length];
        System.arraycopy(vMOpTypeArrValuesCustom, 0, vMOpTypeArr, 0, length);
        return vMOpTypeArr;
    }

    VMOpType(int i) {
        this.opType = i;
    }

    public int getOpType() {
        return this.opType;
    }

    public boolean equals(int i) {
        return this.opType == i;
    }

    public static VMOpType findOpType(int i) {
        VMOpType vMOpType = VM_OPREG;
        if (vMOpType.equals(i)) {
            return vMOpType;
        }
        VMOpType vMOpType2 = VM_OPINT;
        if (vMOpType2.equals(i)) {
            return vMOpType2;
        }
        VMOpType vMOpType3 = VM_OPREGMEM;
        if (vMOpType3.equals(i)) {
            return vMOpType3;
        }
        VMOpType vMOpType4 = VM_OPNONE;
        if (vMOpType4.equals(i)) {
            return vMOpType4;
        }
        return null;
    }
}
