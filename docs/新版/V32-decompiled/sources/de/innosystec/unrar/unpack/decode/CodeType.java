package de.innosystec.unrar.unpack.decode;

/* JADX INFO: loaded from: classes2.dex */
public enum CodeType {
    CODE_HUFFMAN,
    CODE_LZ,
    CODE_LZ2,
    CODE_REPEATLZ,
    CODE_CACHELZ,
    CODE_STARTFILE,
    CODE_ENDFILE,
    CODE_VM,
    CODE_VMDATA;

    /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
    public static CodeType[] valuesCustom() {
        CodeType[] codeTypeArrValuesCustom = values();
        int length = codeTypeArrValuesCustom.length;
        CodeType[] codeTypeArr = new CodeType[length];
        System.arraycopy(codeTypeArrValuesCustom, 0, codeTypeArr, 0, length);
        return codeTypeArr;
    }
}
