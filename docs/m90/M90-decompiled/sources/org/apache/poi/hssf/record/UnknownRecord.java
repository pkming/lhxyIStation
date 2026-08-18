package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class UnknownRecord extends Record {
    private short sid;
    private byte[] thedata;

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
    }

    public UnknownRecord() {
        this.sid = (short) 0;
        this.thedata = null;
    }

    public UnknownRecord(short s, short s2, byte[] bArr) {
        this.sid = (short) 0;
        this.thedata = null;
        this.sid = s;
        this.thedata = bArr;
    }

    public UnknownRecord(short s, short s2, byte[] bArr, int i) {
        this.sid = (short) 0;
        this.thedata = null;
        this.sid = s;
        byte[] bArr2 = new byte[s2];
        this.thedata = bArr2;
        System.arraycopy(bArr, i, bArr2, 0, s2);
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        if (this.thedata == null) {
            this.thedata = new byte[0];
        }
        LittleEndian.putShort(bArr, i + 0, this.sid);
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
    protected void fillFields(byte[] bArr, short s) {
        this.sid = s;
        this.thedata = bArr;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(new StringBuffer().append("[UNKNOWN RECORD:").append(Integer.toHexString(this.sid)).append("]\n").toString());
        stringBuffer.append("    .id        = ").append(Integer.toHexString(this.sid)).append("\n");
        stringBuffer.append("[/UNKNOWN RECORD]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return this.sid;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        throw new RecordFormatException("Unknown record cannot be constructed via offset -- we need a copy of the data");
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        UnknownRecord unknownRecord = new UnknownRecord();
        unknownRecord.sid = this.sid;
        unknownRecord.thedata = this.thedata;
        return unknownRecord;
    }
}
