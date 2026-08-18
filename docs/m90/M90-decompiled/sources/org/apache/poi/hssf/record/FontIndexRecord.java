package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class FontIndexRecord extends Record {
    public static final short sid = 4134;
    private short field_1_fontIndex;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public FontIndexRecord() {
    }

    public FontIndexRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public FontIndexRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 4134) {
            throw new RecordFormatException("Not a FontIndex record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_fontIndex = LittleEndian.getShort(bArr, 0 + i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[FONTX]\n");
        stringBuffer.append("    .fontIndex            = ").append("0x").append(HexDump.toHex(getFontIndex())).append(" (").append((int) getFontIndex()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("[/FONTX]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4 + 0, this.field_1_fontIndex);
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        FontIndexRecord fontIndexRecord = new FontIndexRecord();
        fontIndexRecord.field_1_fontIndex = this.field_1_fontIndex;
        return fontIndexRecord;
    }

    public short getFontIndex() {
        return this.field_1_fontIndex;
    }

    public void setFontIndex(short s) {
        this.field_1_fontIndex = s;
    }
}
