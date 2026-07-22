package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class TabIdRecord extends Record {
    public static final short sid = 317;
    public short[] field_1_tabids;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 317;
    }

    public TabIdRecord() {
    }

    public TabIdRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public TabIdRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 317) {
            throw new RecordFormatException("NOT A TABID RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_tabids = new short[s / 2];
        int i2 = 0;
        while (true) {
            short[] sArr = this.field_1_tabids;
            if (i2 >= sArr.length) {
                return;
            }
            sArr[i2] = LittleEndian.getShort(bArr, (i2 * 2) + i);
            i2++;
        }
    }

    public void setTabIdArray(short[] sArr) {
        this.field_1_tabids = sArr;
    }

    public short[] getTabIdArray() {
        return this.field_1_tabids;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[TABID]\n");
        stringBuffer.append("    .elements        = ").append(this.field_1_tabids.length).append("\n");
        for (int i = 0; i < this.field_1_tabids.length; i++) {
            stringBuffer.append(new StringBuffer().append("    .element_").append(i).append("       = ").toString()).append((int) this.field_1_tabids[i]).append("\n");
        }
        stringBuffer.append("[/TABID]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        short[] tabIdArray = getTabIdArray();
        short length = (short) (tabIdArray.length * 2);
        LittleEndian.putShort(bArr, i + 0, (short) 317);
        LittleEndian.putShort(bArr, i + 2, length);
        int i2 = 4;
        for (int i3 = 0; i3 < length / 2; i3++) {
            LittleEndian.putShort(bArr, i2 + i, tabIdArray[i3]);
            i2 += 2;
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return (getTabIdArray().length * 2) + 4;
    }
}
