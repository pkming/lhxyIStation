package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class RecalcIdRecord extends Record {
    public static final short sid = 449;
    public short[] field_1_recalcids;
    private boolean isNeeded;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 449;
    }

    public RecalcIdRecord() {
        this.isNeeded = false;
    }

    public RecalcIdRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.isNeeded = false;
    }

    public RecalcIdRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.isNeeded = false;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 449) {
            throw new RecordFormatException("NOT A RECALCID RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_recalcids = new short[s / 2];
        int i2 = 0;
        while (true) {
            short[] sArr = this.field_1_recalcids;
            if (i2 >= sArr.length) {
                return;
            }
            sArr[i2] = LittleEndian.getShort(bArr, (i2 * 2) + i);
            i2++;
        }
    }

    public void setRecalcIdArray(short[] sArr) {
        this.field_1_recalcids = sArr;
    }

    public short[] getRecalcIdArray() {
        return this.field_1_recalcids;
    }

    public void setIsNeeded(boolean z) {
        this.isNeeded = z;
    }

    public boolean isNeeded() {
        return this.isNeeded;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[RECALCID]\n");
        stringBuffer.append("    .elements        = ").append(this.field_1_recalcids.length).append("\n");
        for (int i = 0; i < this.field_1_recalcids.length; i++) {
            stringBuffer.append(new StringBuffer().append("    .element_").append(i).append("       = ").toString()).append((int) this.field_1_recalcids[i]).append("\n");
        }
        stringBuffer.append("[/RECALCID]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        short[] recalcIdArray = getRecalcIdArray();
        short length = (short) (recalcIdArray.length * 2);
        LittleEndian.putShort(bArr, i + 0, (short) 449);
        LittleEndian.putShort(bArr, i + 2, length);
        int i2 = 4;
        for (int i3 = 0; i3 < length / 2; i3++) {
            LittleEndian.putShort(bArr, i2 + i, recalcIdArray[i3]);
            i2 += 2;
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return (getRecalcIdArray().length * 2) + 4;
    }
}
