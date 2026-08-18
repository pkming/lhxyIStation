package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class SharedFormulaRecord extends Record {
    public static final short sid = 1212;
    int offset;
    private short size;
    private byte[] thedata;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isInValueSection() {
        return true;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isValue() {
        return true;
    }

    public SharedFormulaRecord() {
        this.size = (short) 0;
        this.thedata = null;
        this.offset = 0;
    }

    public SharedFormulaRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.size = (short) 0;
        this.thedata = null;
        this.offset = 0;
        fillFields(bArr, s2, 0);
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        if (this.thedata == null) {
            this.thedata = new byte[0];
        }
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) this.thedata.length);
        byte[] bArr2 = this.thedata;
        if (bArr2.length > 0) {
            System.arraycopy(bArr2, 0, bArr, i + 4, bArr2.length);
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        byte[] bArr = this.thedata;
        if (bArr != null) {
            return 4 + bArr.length;
        }
        return 4;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 1212) {
            throw new RecordFormatException("Not a valid SharedFormula");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(new StringBuffer().append("[SHARED FORMULA RECORD:").append(Integer.toHexString(1212)).append("]\n").toString());
        stringBuffer.append("    .id        = ").append(Integer.toHexString(1212)).append("\n");
        stringBuffer.append("[/SHARED FORMULA RECORD]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        byte[] bArr2 = new byte[s];
        this.thedata = bArr2;
        System.arraycopy(bArr, 0, bArr2, 0, s);
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        SharedFormulaRecord sharedFormulaRecord = new SharedFormulaRecord();
        sharedFormulaRecord.offset = this.offset;
        sharedFormulaRecord.size = this.size;
        sharedFormulaRecord.thedata = this.thedata;
        return sharedFormulaRecord;
    }
}
