package org.apache.poi.hssf.record;

import java.util.ArrayList;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class ExtSSTRecord extends Record {
    public static final int DEFAULT_BUCKET_SIZE = 8;
    public static final int MAX_BUCKETS = 128;
    public static final short sid = 255;
    private short field_1_strings_per_bucket;
    private ArrayList field_2_sst_info;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 255;
    }

    public ExtSSTRecord() {
        this.field_1_strings_per_bucket = (short) 8;
        this.field_2_sst_info = new ArrayList();
    }

    public ExtSSTRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.field_1_strings_per_bucket = (short) 8;
    }

    public ExtSSTRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.field_1_strings_per_bucket = (short) 8;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 255) {
            throw new RecordFormatException("NOT An EXTSST RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_2_sst_info = new ArrayList();
        this.field_1_strings_per_bucket = LittleEndian.getShort(bArr, i + 0);
        for (int i2 = 2; i2 < s - i; i2 += 8) {
            byte[] bArr2 = new byte[i + 8];
            System.arraycopy(bArr, i2, bArr2, 0, 8);
            this.field_2_sst_info.add(new ExtSSTInfoSubRecord((short) 0, (short) 8, bArr2));
        }
    }

    public void setNumStringsPerBucket(short s) {
        this.field_1_strings_per_bucket = s;
    }

    public void addInfoRecord(ExtSSTInfoSubRecord extSSTInfoSubRecord) {
        this.field_2_sst_info.add(extSSTInfoSubRecord);
    }

    public short getNumStringsPerBucket() {
        return this.field_1_strings_per_bucket;
    }

    public int getNumInfoRecords() {
        return this.field_2_sst_info.size();
    }

    public ExtSSTInfoSubRecord getInfoRecordAt(int i) {
        return (ExtSSTInfoSubRecord) this.field_2_sst_info.get(i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[EXTSST]\n");
        stringBuffer.append("    .dsst           = ").append(Integer.toHexString(getNumStringsPerBucket())).append("\n");
        stringBuffer.append("    .numInfoRecords = ").append(getNumInfoRecords()).append("\n");
        for (int i = 0; i < getNumInfoRecords(); i++) {
            stringBuffer.append("    .inforecord     = ").append(i).append("\n");
            stringBuffer.append("    .streampos      = ").append(Integer.toHexString(getInfoRecordAt(i).getStreamPos())).append("\n");
            stringBuffer.append("    .sstoffset      = ").append(Integer.toHexString(getInfoRecordAt(i).getBucketSSTOffset())).append("\n");
        }
        stringBuffer.append("[/EXTSST]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 255);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4, this.field_1_strings_per_bucket);
        int iSerialize = 6;
        for (int i2 = 0; i2 < getNumInfoRecords(); i2++) {
            iSerialize += getInfoRecordAt(i2).serialize(iSerialize + i, bArr);
        }
        return iSerialize;
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return (getNumInfoRecords() * 8) + 6;
    }

    public static final int getNumberOfInfoRecsForStrings(int i) {
        int i2 = i / 8;
        if (i % 8 != 0) {
            i2++;
        }
        if (i2 > 128) {
            return 128;
        }
        return i2;
    }

    public static final int getRecordSizeForStrings(int i) {
        return (getNumberOfInfoRecsForStrings(i) * 8) + 6;
    }

    public void setBucketOffsets(int[] iArr, int[] iArr2) {
        this.field_2_sst_info = new ArrayList(iArr.length);
        for (int i = 0; i < iArr.length; i++) {
            ExtSSTInfoSubRecord extSSTInfoSubRecord = new ExtSSTInfoSubRecord();
            extSSTInfoSubRecord.setBucketRecordOffset((short) iArr2[i]);
            extSSTInfoSubRecord.setStreamPos(iArr[i]);
            this.field_2_sst_info.add(extSSTInfoSubRecord);
        }
    }
}
