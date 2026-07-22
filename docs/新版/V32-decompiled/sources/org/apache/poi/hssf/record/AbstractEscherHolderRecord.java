package org.apache.poi.hssf.record;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherRecordFactory;
import org.apache.poi.ddf.NullEscherSerializationListener;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public abstract class AbstractEscherHolderRecord extends Record {
    private static final boolean DESERIALISE;
    private List escherRecords;
    private byte[] rawData;

    protected abstract String getRecordName();

    @Override // org.apache.poi.hssf.record.Record
    public abstract short getSid();

    static {
        DESERIALISE = System.getProperty("poi.deserialize.escher") != null;
    }

    public AbstractEscherHolderRecord() {
        this.escherRecords = new ArrayList();
    }

    public AbstractEscherHolderRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public AbstractEscherHolderRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != getSid()) {
            throw new RecordFormatException("Not a Bar record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.escherRecords = new ArrayList();
        if (!DESERIALISE) {
            byte[] bArr2 = new byte[s];
            this.rawData = bArr2;
            System.arraycopy(bArr, i, bArr2, 0, s);
            return;
        }
        EscherRecordFactory defaultEscherRecordFactory = new DefaultEscherRecordFactory();
        int i2 = i;
        while (i2 < i + s) {
            EscherRecord escherRecordCreateRecord = defaultEscherRecordFactory.createRecord(bArr, i2);
            int iFillFields = escherRecordCreateRecord.fillFields(bArr, i2, defaultEscherRecordFactory);
            this.escherRecords.add(escherRecordCreateRecord);
            i2 += iFillFields;
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        String property = System.getProperty("line.separator");
        stringBuffer.append(new StringBuffer().append('[').append(getRecordName()).append(']').append(property).toString());
        Iterator it = this.escherRecords.iterator();
        while (it.hasNext()) {
            stringBuffer.append(((EscherRecord) it.next()).toString());
        }
        stringBuffer.append(new StringBuffer().append("[/").append(getRecordName()).append(']').append(property).toString());
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        byte[] bArr2;
        LittleEndian.putShort(bArr, i + 0, getSid());
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        if (this.escherRecords.size() == 0 && (bArr2 = this.rawData) != null) {
            System.arraycopy(bArr2, 0, bArr, i + 4, bArr2.length);
        } else {
            int iSerialize = i + 4;
            Iterator it = this.escherRecords.iterator();
            while (it.hasNext()) {
                iSerialize += ((EscherRecord) it.next()).serialize(iSerialize, bArr, new NullEscherSerializationListener());
            }
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        byte[] bArr;
        int recordSize = 4;
        if (this.escherRecords.size() == 0 && (bArr = this.rawData) != null) {
            return bArr.length + 4;
        }
        Iterator it = this.escherRecords.iterator();
        while (it.hasNext()) {
            recordSize += ((EscherRecord) it.next()).getRecordSize();
        }
        return recordSize;
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        throw new IllegalStateException("Not implemented yet.");
    }

    public void addEscherRecord(int i, EscherRecord escherRecord) {
        this.escherRecords.add(i, escherRecord);
    }

    public boolean addEscherRecord(EscherRecord escherRecord) {
        return this.escherRecords.add(escherRecord);
    }

    public List getEscherRecords() {
        return this.escherRecords;
    }

    public void clearEscherRecords() {
        this.escherRecords.clear();
    }

    public EscherRecord getEscherRecord(int i) {
        return (EscherRecord) this.escherRecords.get(i);
    }
}
