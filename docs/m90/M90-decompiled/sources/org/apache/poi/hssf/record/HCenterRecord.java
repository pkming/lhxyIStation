package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class HCenterRecord extends Record {
    public static final short sid = 131;
    private short field_1_hcenter;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 131;
    }

    public HCenterRecord() {
    }

    public HCenterRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public HCenterRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 131) {
            throw new RecordFormatException("NOT A HCenter RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_hcenter = LittleEndian.getShort(bArr, i + 0);
    }

    public void setHCenter(boolean z) {
        if (z) {
            this.field_1_hcenter = (short) 1;
        } else {
            this.field_1_hcenter = (short) 0;
        }
    }

    public boolean getHCenter() {
        return this.field_1_hcenter == 1;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[HCENTER]\n");
        stringBuffer.append("    .hcenter        = ").append(getHCenter()).append("\n");
        stringBuffer.append("[/HCENTER]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 131);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, this.field_1_hcenter);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        HCenterRecord hCenterRecord = new HCenterRecord();
        hCenterRecord.field_1_hcenter = this.field_1_hcenter;
        return hCenterRecord;
    }
}
