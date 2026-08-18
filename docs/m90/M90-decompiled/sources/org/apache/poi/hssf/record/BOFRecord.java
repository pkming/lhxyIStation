package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class BOFRecord extends Record {
    public static final short BUILD = 4307;
    public static final short BUILD_YEAR = 1996;
    public static final short HISTORY_MASK = 65;
    public static final short TYPE_CHART = 32;
    public static final short TYPE_EXCEL_4_MACRO = 64;
    public static final short TYPE_VB_MODULE = 6;
    public static final short TYPE_WORKBOOK = 5;
    public static final short TYPE_WORKSHEET = 16;
    public static final short TYPE_WORKSPACE_FILE = 256;
    public static final short VERSION = 6;
    public static final short sid = 2057;
    private short field_1_version;
    private short field_2_type;
    private short field_3_build;
    private short field_4_year;
    private int field_5_history;
    private int field_6_rversion;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 20;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public BOFRecord() {
    }

    public BOFRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public BOFRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 2057) {
            throw new RecordFormatException("NOT A BOF RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_version = LittleEndian.getShort(bArr, i + 0);
        this.field_2_type = LittleEndian.getShort(bArr, i + 2);
        this.field_3_build = LittleEndian.getShort(bArr, i + 4);
        this.field_4_year = LittleEndian.getShort(bArr, i + 6);
        this.field_5_history = LittleEndian.getInt(bArr, i + 8);
        this.field_6_rversion = LittleEndian.getInt(bArr, i + 12);
    }

    public void setVersion(short s) {
        this.field_1_version = s;
    }

    public void setType(short s) {
        this.field_2_type = s;
    }

    public void setBuild(short s) {
        this.field_3_build = s;
    }

    public void setBuildYear(short s) {
        this.field_4_year = s;
    }

    public void setHistoryBitMask(int i) {
        this.field_5_history = i;
    }

    public void setRequiredVersion(int i) {
        this.field_6_rversion = i;
    }

    public short getVersion() {
        return this.field_1_version;
    }

    public short getType() {
        return this.field_2_type;
    }

    public short getBuild() {
        return this.field_3_build;
    }

    public short getBuildYear() {
        return this.field_4_year;
    }

    public int getHistoryBitMask() {
        return this.field_5_history;
    }

    public int getRequiredVersion() {
        return this.field_6_rversion;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[BOF RECORD]\n");
        stringBuffer.append("    .version         = ").append(Integer.toHexString(getVersion())).append("\n");
        stringBuffer.append("    .type            = ").append(Integer.toHexString(getType())).append("\n");
        stringBuffer.append("    .build           = ").append(Integer.toHexString(getBuild())).append("\n");
        stringBuffer.append("    .buildyear       = ").append((int) getBuildYear()).append("\n");
        stringBuffer.append("    .history         = ").append(Integer.toHexString(getHistoryBitMask())).append("\n");
        stringBuffer.append("    .requiredversion = ").append(Integer.toHexString(getRequiredVersion())).append("\n");
        stringBuffer.append("[/BOF RECORD]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) 16);
        LittleEndian.putShort(bArr, i + 4, getVersion());
        LittleEndian.putShort(bArr, i + 6, getType());
        LittleEndian.putShort(bArr, i + 8, getBuild());
        LittleEndian.putShort(bArr, i + 10, getBuildYear());
        LittleEndian.putInt(bArr, i + 12, getHistoryBitMask());
        LittleEndian.putInt(bArr, i + 16, getRequiredVersion());
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        BOFRecord bOFRecord = new BOFRecord();
        bOFRecord.field_1_version = this.field_1_version;
        bOFRecord.field_2_type = this.field_2_type;
        bOFRecord.field_3_build = this.field_3_build;
        bOFRecord.field_4_year = this.field_4_year;
        bOFRecord.field_5_history = this.field_5_history;
        bOFRecord.field_6_rversion = this.field_6_rversion;
        return bOFRecord;
    }
}
