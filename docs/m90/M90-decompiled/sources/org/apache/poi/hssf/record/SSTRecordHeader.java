package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
class SSTRecordHeader {
    int numStrings;
    int numUniqueStrings;

    public SSTRecordHeader(int i, int i2) {
        this.numStrings = i;
        this.numUniqueStrings = i2;
    }

    public int writeSSTHeader(byte[] bArr, int i, int i2) {
        LittleEndian.putShort(bArr, i, (short) 252);
        int i3 = i + 2;
        LittleEndian.putShort(bArr, i3, (short) i2);
        int i4 = i3 + 2;
        LittleEndian.putInt(bArr, i4, this.numStrings);
        int i5 = i4 + 4;
        LittleEndian.putInt(bArr, i5, this.numUniqueStrings);
        return (i5 + 4) - i;
    }
}
