package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class VCenterRecord extends Record {
    public static final short sid = 132;
    private short field_1_vcenter;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 132;
    }

    public VCenterRecord() {
    }

    public VCenterRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public VCenterRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 132) {
            throw new RecordFormatException("NOT A VCenter RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_vcenter = LittleEndian.getShort(bArr, i + 0);
    }

    public void setVCenter(boolean z) {
        if (z) {
            this.field_1_vcenter = (short) 1;
        } else {
            this.field_1_vcenter = (short) 0;
        }
    }

    public boolean getVCenter() {
        return this.field_1_vcenter == 1;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[VCENTER]\n");
        stringBuffer.append("    .vcenter        = ").append(getVCenter()).append("\n");
        stringBuffer.append("[/VCENTER]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 132);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, this.field_1_vcenter);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        VCenterRecord vCenterRecord = new VCenterRecord();
        vCenterRecord.field_1_vcenter = this.field_1_vcenter;
        return vCenterRecord;
    }
}
