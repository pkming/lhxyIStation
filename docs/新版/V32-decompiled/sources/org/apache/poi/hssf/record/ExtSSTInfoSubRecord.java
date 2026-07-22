package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class ExtSSTInfoSubRecord extends Record {
    public static final int INFO_SIZE = 8;
    public static final short sid = 4095;
    private int field_1_stream_pos;
    private short field_2_bucket_sst_offset;
    private short field_3_zero;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 8;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 4095;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
    }

    public ExtSSTInfoSubRecord() {
    }

    public ExtSSTInfoSubRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public ExtSSTInfoSubRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_stream_pos = LittleEndian.getInt(bArr, i + 0);
        this.field_2_bucket_sst_offset = LittleEndian.getShort(bArr, i + 4);
        this.field_3_zero = LittleEndian.getShort(bArr, i + 6);
    }

    public void setStreamPos(int i) {
        this.field_1_stream_pos = i;
    }

    public void setBucketRecordOffset(short s) {
        this.field_2_bucket_sst_offset = s;
    }

    public int getStreamPos() {
        return this.field_1_stream_pos;
    }

    public short getBucketSSTOffset() {
        return this.field_2_bucket_sst_offset;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[EXTSST]\n");
        stringBuffer.append("    .streampos      = ").append(Integer.toHexString(getStreamPos())).append("\n");
        stringBuffer.append("    .bucketsstoffset= ").append(Integer.toHexString(getBucketSSTOffset())).append("\n");
        stringBuffer.append("    .zero           = ").append(Integer.toHexString(this.field_3_zero)).append("\n");
        stringBuffer.append("[/EXTSST]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putInt(bArr, i + 0, getStreamPos());
        LittleEndian.putShort(bArr, i + 4, getBucketSSTOffset());
        LittleEndian.putShort(bArr, i + 6, (short) 0);
        return getRecordSize();
    }
}
