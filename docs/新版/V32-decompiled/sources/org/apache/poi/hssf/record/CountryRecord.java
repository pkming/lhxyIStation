package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class CountryRecord extends Record {
    public static final short sid = 140;
    private short field_1_default_country;
    private short field_2_current_country;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 8;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 140;
    }

    public CountryRecord() {
    }

    public CountryRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public CountryRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 140) {
            throw new RecordFormatException("NOT A Country RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_default_country = LittleEndian.getShort(bArr, i + 0);
        this.field_2_current_country = LittleEndian.getShort(bArr, i + 2);
    }

    public void setDefaultCountry(short s) {
        this.field_1_default_country = s;
    }

    public void setCurrentCountry(short s) {
        this.field_2_current_country = s;
    }

    public short getDefaultCountry() {
        return this.field_1_default_country;
    }

    public short getCurrentCountry() {
        return this.field_2_current_country;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[COUNTRY]\n");
        stringBuffer.append("    .defaultcountry  = ").append(Integer.toHexString(getDefaultCountry())).append("\n");
        stringBuffer.append("    .currentcountry  = ").append(Integer.toHexString(getCurrentCountry())).append("\n");
        stringBuffer.append("[/COUNTRY]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 140);
        LittleEndian.putShort(bArr, i + 2, (short) 4);
        LittleEndian.putShort(bArr, i + 4, getDefaultCountry());
        LittleEndian.putShort(bArr, i + 6, getCurrentCountry());
        return getRecordSize();
    }
}
