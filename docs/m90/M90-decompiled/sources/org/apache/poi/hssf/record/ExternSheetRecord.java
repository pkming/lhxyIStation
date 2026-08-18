package org.apache.poi.hssf.record;

import java.util.ArrayList;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class ExternSheetRecord extends Record {
    public static final short sid = 23;
    private short field_1_number_of_REF_sturcutres;
    private ArrayList field_2_REF_structures;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 23;
    }

    public ExternSheetRecord() {
        this.field_2_REF_structures = new ArrayList();
    }

    public ExternSheetRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public ExternSheetRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 23) {
            throw new RecordFormatException("NOT An ExternSheet RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_2_REF_structures = new ArrayList();
        this.field_1_number_of_REF_sturcutres = LittleEndian.getShort(bArr, i + 0);
        int i2 = i + 2;
        for (int i3 = 0; i3 < this.field_1_number_of_REF_sturcutres; i3++) {
            ExternSheetSubRecord externSheetSubRecord = new ExternSheetSubRecord((short) 0, (short) 6, bArr, i2);
            i2 += 6;
            this.field_2_REF_structures.add(externSheetSubRecord);
        }
    }

    public void setNumOfREFStructures(short s) {
        this.field_1_number_of_REF_sturcutres = s;
    }

    public short getNumOfREFStructures() {
        return this.field_1_number_of_REF_sturcutres;
    }

    public void addREFRecord(ExternSheetSubRecord externSheetSubRecord) {
        this.field_2_REF_structures.add(externSheetSubRecord);
    }

    public int getNumOfREFRecords() {
        return this.field_2_REF_structures.size();
    }

    public ExternSheetSubRecord getREFRecordAt(int i) {
        return (ExternSheetSubRecord) this.field_2_REF_structures.get(i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[EXTERNSHEET]\n");
        stringBuffer.append("   numOfRefs     = ").append((int) getNumOfREFStructures()).append("\n");
        for (int i = 0; i < getNumOfREFRecords(); i++) {
            stringBuffer.append("refrec         #").append(i).append('\n');
            stringBuffer.append(getREFRecordAt(i).toString());
            stringBuffer.append("----refrec     #").append(i).append('\n');
        }
        stringBuffer.append("[/EXTERNSHEET]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 23);
        LittleEndian.putShort(bArr, i + 2, (short) ((getNumOfREFRecords() * 6) + 2));
        LittleEndian.putShort(bArr, i + 4, getNumOfREFStructures());
        int i2 = 6;
        for (int i3 = 0; i3 < getNumOfREFRecords(); i3++) {
            System.arraycopy(getREFRecordAt(i3).serialize(), 0, bArr, i2 + i, 6);
            i2 += 6;
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return (getNumOfREFRecords() * 6) + 6;
    }
}
