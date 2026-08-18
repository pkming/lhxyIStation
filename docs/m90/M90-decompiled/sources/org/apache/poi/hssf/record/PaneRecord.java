package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class PaneRecord extends Record {
    public static final short ACTIVE_PANE_LOWER_LEFT = 2;
    public static final short ACTIVE_PANE_LOWER_RIGHT = 0;
    public static final short ACTIVE_PANE_UPER_LEFT = 3;
    public static final short ACTIVE_PANE_UPPER_RIGHT = 1;
    public static final short sid = 65;
    private short field_1_x;
    private short field_2_y;
    private short field_3_topRow;
    private short field_4_leftColumn;
    private short field_5_activePane;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 14;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 65;
    }

    public PaneRecord() {
    }

    public PaneRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public PaneRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 65) {
            throw new RecordFormatException("Not a Pane record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_x = LittleEndian.getShort(bArr, 0 + i);
        this.field_2_y = LittleEndian.getShort(bArr, 2 + i);
        this.field_3_topRow = LittleEndian.getShort(bArr, 4 + i);
        this.field_4_leftColumn = LittleEndian.getShort(bArr, 6 + i);
        this.field_5_activePane = LittleEndian.getShort(bArr, 8 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[PANE]\n");
        stringBuffer.append("    .x                    = ").append("0x").append(HexDump.toHex(getX())).append(" (").append((int) getX()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .y                    = ").append("0x").append(HexDump.toHex(getY())).append(" (").append((int) getY()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .topRow               = ").append("0x").append(HexDump.toHex(getTopRow())).append(" (").append((int) getTopRow()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .leftColumn           = ").append("0x").append(HexDump.toHex(getLeftColumn())).append(" (").append((int) getLeftColumn()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .activePane           = ").append("0x").append(HexDump.toHex(getActivePane())).append(" (").append((int) getActivePane()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/PANE]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 65);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_x);
        LittleEndian.putShort(bArr, i + 6 + 0, this.field_2_y);
        LittleEndian.putShort(bArr, i + 8 + 0, this.field_3_topRow);
        LittleEndian.putShort(bArr, i + 10 + 0, this.field_4_leftColumn);
        LittleEndian.putShort(bArr, i + 12 + 0, this.field_5_activePane);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        PaneRecord paneRecord = new PaneRecord();
        paneRecord.field_1_x = this.field_1_x;
        paneRecord.field_2_y = this.field_2_y;
        paneRecord.field_3_topRow = this.field_3_topRow;
        paneRecord.field_4_leftColumn = this.field_4_leftColumn;
        paneRecord.field_5_activePane = this.field_5_activePane;
        return paneRecord;
    }

    public short getX() {
        return this.field_1_x;
    }

    public void setX(short s) {
        this.field_1_x = s;
    }

    public short getY() {
        return this.field_2_y;
    }

    public void setY(short s) {
        this.field_2_y = s;
    }

    public short getTopRow() {
        return this.field_3_topRow;
    }

    public void setTopRow(short s) {
        this.field_3_topRow = s;
    }

    public short getLeftColumn() {
        return this.field_4_leftColumn;
    }

    public void setLeftColumn(short s) {
        this.field_4_leftColumn = s;
    }

    public short getActivePane() {
        return this.field_5_activePane;
    }

    public void setActivePane(short s) {
        this.field_5_activePane = s;
    }
}
