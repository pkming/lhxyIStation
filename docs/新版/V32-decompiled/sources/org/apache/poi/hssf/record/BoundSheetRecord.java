package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes3.dex */
public class BoundSheetRecord extends Record {
    public static final short sid = 133;
    private int field_1_position_of_BOF;
    private short field_2_option_flags;
    private byte field_3_sheetname_length;
    private byte field_4_compressed_unicode_flag;
    private String field_5_sheetname;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 133;
    }

    public BoundSheetRecord() {
    }

    public BoundSheetRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public BoundSheetRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 133) {
            throw new RecordFormatException("NOT A Bound Sheet RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_position_of_BOF = LittleEndian.getInt(bArr, i + 0);
        this.field_2_option_flags = LittleEndian.getShort(bArr, i + 4);
        byte b = bArr[i + 6];
        this.field_3_sheetname_length = b;
        this.field_4_compressed_unicode_flag = bArr[i + 7];
        int iUbyteToInt = LittleEndian.ubyteToInt(b);
        if ((this.field_4_compressed_unicode_flag & 1) == 1) {
            this.field_5_sheetname = StringUtil.getFromUnicodeHigh(bArr, i + 8, iUbyteToInt);
        } else {
            this.field_5_sheetname = StringUtil.getFromCompressedUnicode(bArr, i + 8, iUbyteToInt);
        }
    }

    public void setPositionOfBof(int i) {
        this.field_1_position_of_BOF = i;
    }

    public void setOptionFlags(short s) {
        this.field_2_option_flags = s;
    }

    public void setSheetnameLength(byte b) {
        this.field_3_sheetname_length = b;
    }

    public void setCompressedUnicodeFlag(byte b) {
        this.field_4_compressed_unicode_flag = b;
    }

    public void setSheetname(String str) {
        if (str == null || str.length() == 0 || str.length() > 31 || str.indexOf("/") > -1 || str.indexOf("\\") > -1 || str.indexOf("?") > -1 || str.indexOf("*") > -1 || str.indexOf("]") > -1 || str.indexOf("[") > -1) {
            throw new IllegalArgumentException("Sheet name cannot be blank, greater than 31 chars, or contain any of /\\*?[]");
        }
        this.field_5_sheetname = str;
    }

    public int getPositionOfBof() {
        return this.field_1_position_of_BOF;
    }

    public short getOptionFlags() {
        return this.field_2_option_flags;
    }

    public byte getSheetnameLength() {
        return this.field_3_sheetname_length;
    }

    public byte getRawSheetnameLength() {
        return (byte) ((this.field_4_compressed_unicode_flag & 1) == 1 ? this.field_3_sheetname_length * 2 : this.field_3_sheetname_length);
    }

    public byte getCompressedUnicodeFlag() {
        return this.field_4_compressed_unicode_flag;
    }

    public String getSheetname() {
        return this.field_5_sheetname;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[BOUNDSHEET]\n");
        stringBuffer.append("    .bof             = ").append(Integer.toHexString(getPositionOfBof())).append("\n");
        stringBuffer.append("    .optionflags     = ").append(Integer.toHexString(getOptionFlags())).append("\n");
        stringBuffer.append("    .sheetname length= ").append(Integer.toHexString(getSheetnameLength())).append("\n");
        stringBuffer.append("    .unicodeflag     = ").append(Integer.toHexString(getCompressedUnicodeFlag())).append("\n");
        stringBuffer.append("    .sheetname       = ").append(getSheetname()).append("\n");
        stringBuffer.append("[/BOUNDSHEET]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 133);
        LittleEndian.putShort(bArr, i + 2, (short) (getRawSheetnameLength() + 8));
        LittleEndian.putInt(bArr, i + 4, getPositionOfBof());
        LittleEndian.putShort(bArr, i + 8, getOptionFlags());
        bArr[i + 10] = getSheetnameLength();
        bArr[i + 11] = getCompressedUnicodeFlag();
        if ((this.field_4_compressed_unicode_flag & 1) == 1) {
            StringUtil.putUncompressedUnicode(getSheetname(), bArr, i + 12);
        } else {
            StringUtil.putCompressedUnicode(getSheetname(), bArr, i + 12);
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return getRawSheetnameLength() + 12;
    }
}
