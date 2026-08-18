package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class ObjectLinkRecord extends Record {
    public static final short ANCHOR_ID_CHART_TITLE = 1;
    public static final short ANCHOR_ID_SERIES_OR_POINT = 4;
    public static final short ANCHOR_ID_X_AXIS = 3;
    public static final short ANCHOR_ID_Y_AXIS = 2;
    public static final short ANCHOR_ID_Z_AXIS = 7;
    public static final short sid = 4135;
    private short field_1_anchorId;
    private short field_2_link1;
    private short field_3_link2;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 10;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public ObjectLinkRecord() {
    }

    public ObjectLinkRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public ObjectLinkRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4135) {
            throw new RecordFormatException("Not a ObjectLink record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_anchorId = LittleEndian.getShort(bArr, 0 + i);
        this.field_2_link1 = LittleEndian.getShort(bArr, 2 + i);
        this.field_3_link2 = LittleEndian.getShort(bArr, 4 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[OBJECTLINK]\n");
        stringBuffer.append("    .anchorId             = ").append("0x").append(HexDump.toHex(getAnchorId())).append(" (").append((int) getAnchorId()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .link1                = ").append("0x").append(HexDump.toHex(getLink1())).append(" (").append((int) getLink1()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .link2                = ").append("0x").append(HexDump.toHex(getLink2())).append(" (").append((int) getLink2()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/OBJECTLINK]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_anchorId);
        LittleEndian.putShort(bArr, i + 6 + 0, this.field_2_link1);
        LittleEndian.putShort(bArr, i + 8 + 0, this.field_3_link2);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        ObjectLinkRecord objectLinkRecord = new ObjectLinkRecord();
        objectLinkRecord.field_1_anchorId = this.field_1_anchorId;
        objectLinkRecord.field_2_link1 = this.field_2_link1;
        objectLinkRecord.field_3_link2 = this.field_3_link2;
        return objectLinkRecord;
    }

    public short getAnchorId() {
        return this.field_1_anchorId;
    }

    public void setAnchorId(short s) {
        this.field_1_anchorId = s;
    }

    public short getLink1() {
        return this.field_2_link1;
    }

    public void setLink1(short s) {
        this.field_2_link1 = s;
    }

    public short getLink2() {
        return this.field_3_link2;
    }

    public void setLink2(short s) {
        this.field_3_link2 = s;
    }
}
