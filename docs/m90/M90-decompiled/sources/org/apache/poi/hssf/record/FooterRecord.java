package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes3.dex */
public class FooterRecord extends Record {
    public static final short sid = 21;
    private byte field_1_footer_len;
    private String field_2_footer;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 21;
    }

    public FooterRecord() {
    }

    public FooterRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public FooterRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 21) {
            throw new RecordFormatException("NOT A FooterRECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        if (s > 0) {
            byte b = bArr[i + 0];
            this.field_1_footer_len = b;
            this.field_2_footer = StringUtil.getFromCompressedUnicode(bArr, i + 3, LittleEndian.ubyteToInt(b));
        }
    }

    public void setFooterLength(byte b) {
        this.field_1_footer_len = b;
    }

    public void setFooter(String str) {
        this.field_2_footer = str;
    }

    public short getFooterLength() {
        return (short) (this.field_1_footer_len & 255);
    }

    public String getFooter() {
        return this.field_2_footer;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[FOOTER]\n");
        stringBuffer.append("    .footerlen      = ").append(Integer.toHexString(getFooterLength())).append("\n");
        stringBuffer.append("    .footer         = ").append(getFooter()).append("\n");
        stringBuffer.append("[/FOOTER]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        int i2 = getFooterLength() > 0 ? 7 : 4;
        LittleEndian.putShort(bArr, i + 0, (short) 21);
        LittleEndian.putShort(bArr, i + 2, (short) ((i2 - 4) + getFooterLength()));
        if (getFooterLength() > 0) {
            bArr[i + 4] = (byte) getFooterLength();
            StringUtil.putCompressedUnicode(getFooter(), bArr, i + 7);
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return (getFooterLength() > 0 ? (short) 7 : (short) 4) + getFooterLength();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        FooterRecord footerRecord = new FooterRecord();
        footerRecord.field_1_footer_len = this.field_1_footer_len;
        footerRecord.field_2_footer = this.field_2_footer;
        return footerRecord;
    }
}
