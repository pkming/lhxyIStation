package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes3.dex */
public class FormatRecord extends Record {
    public static final short sid = 1054;
    private short field_1_index_code;
    private short field_2_formatstring_len;
    private boolean field_3_unicode_flag;
    private short field_3_unicode_len;
    private String field_4_formatstring;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public FormatRecord() {
    }

    public FormatRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public FormatRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 1054) {
            throw new RecordFormatException("NOT A FORMAT RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_index_code = LittleEndian.getShort(bArr, i + 0);
        short s2 = LittleEndian.getShort(bArr, i + 2);
        this.field_3_unicode_len = s2;
        this.field_2_formatstring_len = s2;
        boolean z = (bArr[i + 4] & 1) != 0;
        this.field_3_unicode_flag = z;
        if (z) {
            this.field_4_formatstring = StringUtil.getFromUnicodeHigh(bArr, i + 5, s2);
        } else {
            this.field_4_formatstring = StringUtil.getFromCompressedUnicode(bArr, i + 5, s2);
        }
    }

    public void setIndexCode(short s) {
        this.field_1_index_code = s;
    }

    public void setFormatStringLength(byte b) {
        short s = b;
        this.field_2_formatstring_len = s;
        this.field_3_unicode_len = s;
    }

    public void setUnicodeFlag(boolean z) {
        this.field_3_unicode_flag = z;
    }

    public void setFormatString(String str) {
        this.field_4_formatstring = str;
    }

    public short getIndexCode() {
        return this.field_1_index_code;
    }

    public short getFormatStringLength() {
        return this.field_3_unicode_flag ? this.field_3_unicode_len : this.field_2_formatstring_len;
    }

    public boolean getUnicodeFlag() {
        return this.field_3_unicode_flag;
    }

    public String getFormatString() {
        return this.field_4_formatstring;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[FORMAT]\n");
        stringBuffer.append("    .indexcode       = ").append(Integer.toHexString(getIndexCode())).append("\n");
        stringBuffer.append("    .unicode length  = ").append(Integer.toHexString(this.field_3_unicode_len)).append("\n");
        stringBuffer.append("    .isUnicode       = ").append(this.field_3_unicode_flag).append("\n");
        stringBuffer.append("    .formatstring    = ").append(getFormatString()).append("\n");
        stringBuffer.append("[/FORMAT]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) ((this.field_3_unicode_flag ? this.field_3_unicode_len * 2 : this.field_3_unicode_len) + 5));
        LittleEndian.putShort(bArr, i + 4, getIndexCode());
        LittleEndian.putShort(bArr, i + 6, this.field_3_unicode_len);
        boolean z = this.field_3_unicode_flag;
        bArr[i + 8] = z ? (byte) 1 : (byte) 0;
        if (z) {
            StringUtil.putUncompressedUnicode(getFormatString(), bArr, i + 9);
        } else {
            StringUtil.putCompressedUnicode(getFormatString(), bArr, i + 9);
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return (this.field_3_unicode_flag ? this.field_3_unicode_len * 2 : this.field_3_unicode_len) + 9;
    }
}
