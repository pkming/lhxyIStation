package de.innosystec.unrar.unpack.ppm;

/* JADX INFO: loaded from: classes2.dex */
public enum BlockTypes {
    BLOCK_LZ(0),
    BLOCK_PPM(1);

    private int blockType;

    /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
    public static BlockTypes[] valuesCustom() {
        BlockTypes[] blockTypesArrValuesCustom = values();
        int length = blockTypesArrValuesCustom.length;
        BlockTypes[] blockTypesArr = new BlockTypes[length];
        System.arraycopy(blockTypesArrValuesCustom, 0, blockTypesArr, 0, length);
        return blockTypesArr;
    }

    BlockTypes(int i) {
        this.blockType = i;
    }

    public int getBlockType() {
        return this.blockType;
    }

    public boolean equals(int i) {
        return this.blockType == i;
    }

    public static BlockTypes findBlockType(int i) {
        BlockTypes blockTypes = BLOCK_LZ;
        if (blockTypes.equals(i)) {
            return blockTypes;
        }
        BlockTypes blockTypes2 = BLOCK_PPM;
        if (blockTypes2.equals(i)) {
            return blockTypes2;
        }
        return null;
    }
}
