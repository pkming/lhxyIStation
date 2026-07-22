package org.apache.poi.hssf.record;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class ObjRecord extends Record {
    public static final short sid = 93;
    private List subrecords;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 93;
    }

    public ObjRecord() {
        this.subrecords = new ArrayList(2);
    }

    public ObjRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public ObjRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 93) {
            throw new RecordFormatException("Not an OBJ record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.subrecords = new ArrayList();
        int i2 = i;
        while (i2 - i < s) {
            short s2 = LittleEndian.getShort(bArr, i2);
            short s3 = LittleEndian.getShort(bArr, i2 + 2);
            this.subrecords.add(SubRecord.createSubRecord(s2, s3, bArr, i2 + 4));
            i2 += s3 + 4;
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[OBJ]\n");
        Iterator it = this.subrecords.iterator();
        while (it.hasNext()) {
            stringBuffer.append(new StringBuffer().append("SUBRECORD: ").append(((Record) it.next()).toString()).toString());
        }
        stringBuffer.append("[/OBJ]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 93);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        int iSerialize = i + 4;
        Iterator it = this.subrecords.iterator();
        while (it.hasNext()) {
            iSerialize += ((Record) it.next()).serialize(iSerialize, bArr);
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        Iterator it = this.subrecords.iterator();
        int recordSize = 0;
        while (it.hasNext()) {
            recordSize += ((Record) it.next()).getRecordSize();
        }
        return recordSize + 4;
    }

    public List getSubRecords() {
        return this.subrecords;
    }

    public void clearSubRecords() {
        this.subrecords.clear();
    }

    public void addSubRecord(int i, Object obj) {
        this.subrecords.add(i, obj);
    }

    public boolean addSubRecord(Object obj) {
        return this.subrecords.add(obj);
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        ObjRecord objRecord = new ObjRecord();
        objRecord.subrecords = new ArrayList();
        Iterator it = this.subrecords.iterator();
        while (it.hasNext()) {
            this.subrecords.add(((Record) it.next()).clone());
        }
        return objRecord;
    }
}
