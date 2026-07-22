package de.innosystec.unrar.unpack.vm;

/* JADX INFO: loaded from: classes2.dex */
public enum VMFlags {
    VM_FC(1),
    VM_FZ(2),
    VM_FS(Integer.MIN_VALUE);

    private int flag;

    /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
    public static VMFlags[] valuesCustom() {
        VMFlags[] vMFlagsArrValuesCustom = values();
        int length = vMFlagsArrValuesCustom.length;
        VMFlags[] vMFlagsArr = new VMFlags[length];
        System.arraycopy(vMFlagsArrValuesCustom, 0, vMFlagsArr, 0, length);
        return vMFlagsArr;
    }

    VMFlags(int i) {
        this.flag = i;
    }

    public static VMFlags findFlag(int i) {
        VMFlags vMFlags = VM_FC;
        if (vMFlags.equals(i)) {
            return vMFlags;
        }
        VMFlags vMFlags2 = VM_FS;
        if (vMFlags2.equals(i)) {
            return vMFlags2;
        }
        VMFlags vMFlags3 = VM_FZ;
        if (vMFlags3.equals(i)) {
            return vMFlags3;
        }
        return null;
    }

    public boolean equals(int i) {
        return this.flag == i;
    }

    public int getFlag() {
        return this.flag;
    }
}
