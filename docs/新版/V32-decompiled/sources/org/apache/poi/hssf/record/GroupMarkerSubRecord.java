package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class GroupMarkerSubRecord extends SubRecord {
    public static final short sid = 6;
    private byte[] reserved;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 6;
    }

    public GroupMarkerSubRecord() {
        this.reserved = new byte[0];
    }

    public GroupMarkerSubRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.reserved = new byte[0];
    }

    public GroupMarkerSubRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.reserved = new byte[0];
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 6) {
            throw new RecordFormatException("Not a Group Marker record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        byte[] bArr2 = new byte[s];
        this.reserved = bArr2;
        System.arraycopy(bArr, i, bArr2, 0, s);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        String property = System.getProperty("line.separator");
        stringBuffer.append(new StringBuffer().append("[ftGmo]").append(property).toString());
        stringBuffer.append("  reserved = ").append(HexDump.toHex(this.reserved)).append(property);
        stringBuffer.append(new StringBuffer().append("[/ftGmo]").append(property).toString());
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 6);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        System.arraycopy(this.reserved, 0, bArr, i + 4, getRecordSize() - 4);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return this.reserved.length + 4;
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        GroupMarkerSubRecord groupMarkerSubRecord = new GroupMarkerSubRecord();
        groupMarkerSubRecord.reserved = new byte[this.reserved.length];
        int i = 0;
        while (true) {
            byte[] bArr = this.reserved;
            if (i >= bArr.length) {
                return groupMarkerSubRecord;
            }
            groupMarkerSubRecord.reserved[i] = bArr[i];
            i++;
        }
    }
}
