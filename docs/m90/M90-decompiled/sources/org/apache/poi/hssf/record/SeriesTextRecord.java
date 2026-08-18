package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes3.dex */
public class SeriesTextRecord extends Record {
    public static final short sid = 4109;
    private short field_1_id;
    private byte field_2_textLength;
    private byte field_3_undocumented;
    private String field_4_text;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public SeriesTextRecord() {
    }

    public SeriesTextRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public SeriesTextRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4109) {
            throw new RecordFormatException("Not a SeriesText record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_id = LittleEndian.getShort(bArr, 0 + i);
        byte b = bArr[2 + i];
        this.field_2_textLength = b;
        this.field_3_undocumented = bArr[3 + i];
        this.field_4_text = StringUtil.getFromUnicodeHigh(bArr, 4 + i, (b * 2) / 2);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[SERIESTEXT]\n");
        stringBuffer.append("    .id                   = ").append("0x").append(HexDump.toHex(getId())).append(" (").append((int) getId()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .textLength           = ").append("0x").append(HexDump.toHex(getTextLength())).append(" (").append((int) getTextLength()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .undocumented         = ").append("0x").append(HexDump.toHex(getUndocumented())).append(" (").append((int) getUndocumented()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .text                 = ").append(" (").append(getText()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/SERIESTEXT]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_id);
        bArr[i + 6 + 0] = this.field_2_textLength;
        bArr[i + 7 + 0] = this.field_3_undocumented;
        StringUtil.putUncompressedUnicodeHigh(this.field_4_text, bArr, i + 8 + 0);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return (this.field_2_textLength * 2) + 8;
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        SeriesTextRecord seriesTextRecord = new SeriesTextRecord();
        seriesTextRecord.field_1_id = this.field_1_id;
        seriesTextRecord.field_2_textLength = this.field_2_textLength;
        seriesTextRecord.field_3_undocumented = this.field_3_undocumented;
        seriesTextRecord.field_4_text = this.field_4_text;
        return seriesTextRecord;
    }

    public short getId() {
        return this.field_1_id;
    }

    public void setId(short s) {
        this.field_1_id = s;
    }

    public byte getTextLength() {
        return this.field_2_textLength;
    }

    public void setTextLength(byte b) {
        this.field_2_textLength = b;
    }

    public byte getUndocumented() {
        return this.field_3_undocumented;
    }

    public void setUndocumented(byte b) {
        this.field_3_undocumented = b;
    }

    public String getText() {
        return this.field_4_text;
    }

    public void setText(String str) {
        this.field_4_text = str;
    }
}
