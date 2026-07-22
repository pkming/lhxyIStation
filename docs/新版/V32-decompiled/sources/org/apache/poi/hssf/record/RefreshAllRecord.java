package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class RefreshAllRecord extends Record {
    public static final short sid = 439;
    private short field_1_refreshall;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public RefreshAllRecord() {
    }

    public RefreshAllRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public RefreshAllRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 439) {
            throw new RecordFormatException("NOT A REFRESHALL RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_refreshall = LittleEndian.getShort(bArr, i + 0);
    }

    public void setRefreshAll(boolean z) {
        if (z) {
            this.field_1_refreshall = (short) 1;
        } else {
            this.field_1_refreshall = (short) 0;
        }
    }

    public boolean getRefreshAll() {
        return this.field_1_refreshall == 1;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[REFRESHALL]\n");
        stringBuffer.append("    .refreshall      = ").append(getRefreshAll()).append("\n");
        stringBuffer.append("[/REFRESHALL]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) 2);
        LittleEndian.putShort(bArr, i + 4, this.field_1_refreshall);
        return getRecordSize();
    }
}
