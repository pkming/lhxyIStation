package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class DrawingRecord extends Record {
    public static final short sid = 236;
    private byte[] recordData;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 236;
    }

    public DrawingRecord() {
    }

    public DrawingRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public DrawingRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 236) {
            throw new RecordFormatException("Not a MSODRAWING record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        if (i == 0 && s == bArr.length) {
            this.recordData = bArr;
            return;
        }
        byte[] bArr2 = new byte[s];
        this.recordData = bArr2;
        System.arraycopy(bArr, i, bArr2, 0, s);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s) {
        this.recordData = bArr;
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        if (this.recordData == null) {
            this.recordData = new byte[0];
        }
        LittleEndian.putShort(bArr, i + 0, (short) 236);
        LittleEndian.putShort(bArr, i + 2, (short) this.recordData.length);
        byte[] bArr2 = this.recordData;
        if (bArr2.length > 0) {
            System.arraycopy(bArr2, 0, bArr, i + 4, bArr2.length);
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        byte[] bArr = this.recordData;
        if (bArr != null) {
            return 4 + bArr.length;
        }
        return 4;
    }

    public byte[] getData() {
        return this.recordData;
    }

    public void setData(byte[] bArr) {
        this.recordData = bArr;
    }
}
