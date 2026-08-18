package org.apache.poi.hssf.record;

import java.util.Iterator;
import java.util.List;
import org.apache.poi.util.BinaryTree;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class SSTRecord extends Record {
    static final int MAX_DATA_SPACE = 8216;
    static final int MAX_RECORD_SIZE = 8228;
    static final int SST_RECORD_OVERHEAD = 12;
    static final int STD_RECORD_OVERHEAD = 4;
    static final int STRING_MINIMAL_OVERHEAD = 3;
    public static final short sid = 252;
    private List _record_lengths;
    int[] bucketAbsoluteOffsets;
    int[] bucketRelativeOffsets;
    private SSTDeserializer deserializer;
    private int field_1_num_strings;
    private int field_2_num_unique_strings;
    private BinaryTree field_3_strings;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 252;
    }

    public SSTRecord() {
        this._record_lengths = null;
        this.field_1_num_strings = 0;
        this.field_2_num_unique_strings = 0;
        this.field_3_strings = new BinaryTree();
        this.deserializer = new SSTDeserializer(this.field_3_strings);
    }

    public SSTRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this._record_lengths = null;
    }

    public SSTRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this._record_lengths = null;
    }

    public int addString(String str) {
        boolean z = false;
        if (str == null) {
            return addString("", false);
        }
        int length = str.length();
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            if (str.charAt(i) > 255) {
                z = true;
                break;
            }
            i++;
        }
        return addString(str, z);
    }

    public int addString(String str, boolean z) {
        this.field_1_num_strings++;
        if (str == null) {
            str = "";
        }
        UnicodeString unicodeString = new UnicodeString();
        unicodeString.setString(str);
        unicodeString.setCharCount((short) str.length());
        unicodeString.setOptionFlags(z ? (byte) 1 : (byte) 0);
        Integer num = (Integer) this.field_3_strings.getKeyForValue(unicodeString);
        if (num != null) {
            return num.intValue();
        }
        int size = this.field_3_strings.size();
        this.field_2_num_unique_strings++;
        SSTDeserializer.addToStringTable(this.field_3_strings, new Integer(size), unicodeString);
        return size;
    }

    public int getNumStrings() {
        return this.field_1_num_strings;
    }

    public int getNumUniqueStrings() {
        return this.field_2_num_unique_strings;
    }

    public void setNumStrings(int i) {
        this.field_1_num_strings = i;
    }

    public void setNumUniqueStrings(int i) {
        this.field_2_num_unique_strings = i;
    }

    public String getString(int i) {
        return ((UnicodeString) this.field_3_strings.get(new Integer(i))).getString();
    }

    public boolean isString16bit(int i) {
        return (((UnicodeString) this.field_3_strings.get(new Integer(i))).getOptionFlags() & 1) == 1;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[SST]\n");
        stringBuffer.append("    .numstrings     = ").append(Integer.toHexString(getNumStrings())).append("\n");
        stringBuffer.append("    .uniquestrings  = ").append(Integer.toHexString(getNumUniqueStrings())).append("\n");
        for (int i = 0; i < this.field_3_strings.size(); i++) {
            stringBuffer.append(new StringBuffer().append("    .string_").append(i).append("      = ").toString()).append(this.field_3_strings.get(new Integer(i)).toString()).append("\n");
        }
        stringBuffer.append("[/SST]\n");
        return stringBuffer.toString();
    }

    public int hashCode() {
        return this.field_2_num_unique_strings;
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        SSTRecord sSTRecord = (SSTRecord) obj;
        return this.field_1_num_strings == sSTRecord.field_1_num_strings && this.field_2_num_unique_strings == sSTRecord.field_2_num_unique_strings && this.field_3_strings.equals(sSTRecord.field_3_strings);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) throws RecordFormatException {
        if (s != 252) {
            throw new RecordFormatException("NOT An SST RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_num_strings = LittleEndian.getInt(bArr, i + 0);
        this.field_2_num_unique_strings = LittleEndian.getInt(bArr, i + 4);
        this.field_3_strings = new BinaryTree();
        SSTDeserializer sSTDeserializer = new SSTDeserializer(this.field_3_strings);
        this.deserializer = sSTDeserializer;
        sSTDeserializer.manufactureStrings(bArr, i + 8);
    }

    Iterator getStrings() {
        return this.field_3_strings.values().iterator();
    }

    int countStrings() {
        return this.field_3_strings.size();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        SSTSerializer sSTSerializer = new SSTSerializer(this._record_lengths, this.field_3_strings, getNumStrings(), getNumUniqueStrings());
        int iSerialize = sSTSerializer.serialize(getRecordSize(), i, bArr);
        this.bucketAbsoluteOffsets = sSTSerializer.getBucketAbsoluteOffsets();
        this.bucketRelativeOffsets = sSTSerializer.getBucketRelativeOffsets();
        return iSerialize;
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        SSTRecordSizeCalculator sSTRecordSizeCalculator = new SSTRecordSizeCalculator(this.field_3_strings);
        int recordSize = sSTRecordSizeCalculator.getRecordSize();
        this._record_lengths = sSTRecordSizeCalculator.getRecordLengths();
        return recordSize;
    }

    SSTDeserializer getDeserializer() {
        return this.deserializer;
    }

    @Override // org.apache.poi.hssf.record.Record
    public void processContinueRecord(byte[] bArr) {
        this.deserializer.processContinueRecord(bArr);
    }

    public ExtSSTRecord createExtSSTRecord(int i) {
        int[] iArr = this.bucketAbsoluteOffsets;
        if (iArr == null || iArr == null) {
            throw new IllegalStateException("SST record has not yet been serialized.");
        }
        ExtSSTRecord extSSTRecord = new ExtSSTRecord();
        extSSTRecord.setNumStringsPerBucket((short) 8);
        int[] iArr2 = (int[]) this.bucketAbsoluteOffsets.clone();
        int[] iArr3 = (int[]) this.bucketRelativeOffsets.clone();
        for (int i2 = 0; i2 < iArr2.length; i2++) {
            iArr2[i2] = iArr2[i2] + i;
        }
        extSSTRecord.setBucketOffsets(iArr2, iArr3);
        return extSSTRecord;
    }

    public int calcExtSSTRecordSize() {
        return ExtSSTRecord.getRecordSizeForStrings(this.field_3_strings.size());
    }
}
