package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
class RecordProcessor {
    private int available;
    private byte[] data;
    private int recordOffset;
    private SSTRecordHeader sstRecordHeader;

    public RecordProcessor(byte[] bArr, int i, int i2, int i3) {
        this.data = bArr;
        this.available = i;
        this.sstRecordHeader = new SSTRecordHeader(i2, i3);
    }

    public int getAvailable() {
        return this.available;
    }

    public void writeRecordHeader(int i, int i2, int i3, boolean z) {
        if (z) {
            this.available -= 8;
            this.recordOffset = this.sstRecordHeader.writeSSTHeader(this.data, this.recordOffset + i + i2, i3);
        } else {
            this.recordOffset = writeContinueHeader(this.data, this.recordOffset + i + i2, i3);
        }
    }

    public byte[] writeStringRemainder(boolean z, byte[] bArr, int i, int i2) {
        if (!z) {
            System.arraycopy(bArr, 0, this.data, this.recordOffset + i + i2, bArr.length);
            adjustPointers(bArr.length);
            return bArr;
        }
        System.arraycopy(bArr, 0, this.data, this.recordOffset + i + i2, this.available);
        int length = bArr.length;
        int i3 = this.available;
        byte[] bArr2 = new byte[(length - i3) + 1];
        System.arraycopy(bArr, i3, bArr2, 1, bArr.length - i3);
        bArr2[0] = bArr[0];
        adjustPointers(this.available);
        return bArr2;
    }

    public void writeWholeString(UnicodeString unicodeString, int i, int i2) {
        unicodeString.serialize(this.recordOffset + i + i2, this.data);
        adjustPointers(unicodeString.getRecordSize());
    }

    public byte[] writePartString(UnicodeString unicodeString, int i, int i2) {
        byte[] bArrSerialize = unicodeString.serialize();
        System.arraycopy(bArrSerialize, 0, this.data, this.recordOffset + i + i2, this.available);
        int length = bArrSerialize.length;
        int i3 = this.available;
        byte[] bArr = new byte[(length - i3) + 1];
        System.arraycopy(bArrSerialize, i3, bArr, 1, bArrSerialize.length - i3);
        bArr[0] = bArrSerialize[2];
        this.available = 0;
        return bArr;
    }

    private int writeContinueHeader(byte[] bArr, int i, int i2) {
        LittleEndian.putShort(bArr, i, (short) 60);
        int i3 = i + 2;
        LittleEndian.putShort(bArr, i3, (short) i2);
        return (i3 + 2) - i;
    }

    private void adjustPointers(int i) {
        this.recordOffset += i;
        this.available -= i;
    }

    public int getRecordOffset() {
        return this.recordOffset;
    }
}
