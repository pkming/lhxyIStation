package org.apache.poi.ddf;

import java.util.Iterator;
import org.apache.poi.hssf.record.RecordFormatException;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class EscherTextboxRecord extends EscherRecord {
    private static final byte[] NO_BYTES = new byte[0];
    public static final String RECORD_DESCRIPTION = "msofbtClientTextbox";
    public static final short RECORD_ID = -4083;
    private byte[] thedata = NO_BYTES;

    @Override // org.apache.poi.ddf.EscherRecord
    public String getRecordName() {
        return "ClientTextbox";
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int fillFields(byte[] bArr, int i, EscherRecordFactory escherRecordFactory) {
        int header = readHeader(bArr, i);
        int i2 = 8;
        if (isContainerRecord()) {
            this.thedata = new byte[0];
            int i3 = i + 8;
            while (header > 0) {
                EscherRecord escherRecordCreateRecord = escherRecordFactory.createRecord(bArr, i3);
                int iFillFields = escherRecordCreateRecord.fillFields(bArr, i3, escherRecordFactory);
                i2 += iFillFields;
                i3 += iFillFields;
                header -= iFillFields;
                getChildRecords().add(escherRecordCreateRecord);
            }
            return i2;
        }
        byte[] bArr2 = new byte[header];
        this.thedata = bArr2;
        System.arraycopy(bArr, i + 8, bArr2, 0, header);
        return header + 8;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int serialize(int i, byte[] bArr, EscherSerializationListener escherSerializationListener) {
        escherSerializationListener.beforeRecordSerialize(i, getRecordId(), this);
        LittleEndian.putShort(bArr, i, getOptions());
        LittleEndian.putShort(bArr, i + 2, getRecordId());
        int length = this.thedata.length;
        Iterator it = getChildRecords().iterator();
        while (it.hasNext()) {
            length += ((EscherRecord) it.next()).getRecordSize();
        }
        LittleEndian.putInt(bArr, i + 4, length);
        byte[] bArr2 = this.thedata;
        int i2 = i + 8;
        System.arraycopy(bArr2, 0, bArr, i2, bArr2.length);
        int length2 = i2 + this.thedata.length;
        Iterator it2 = getChildRecords().iterator();
        while (it2.hasNext()) {
            length2 += ((EscherRecord) it2.next()).serialize(length2, bArr, escherSerializationListener);
        }
        int i3 = length2 - i;
        escherSerializationListener.afterRecordSerialize(length2, getRecordId(), i3, this);
        if (i3 == getRecordSize()) {
            return i3;
        }
        throw new RecordFormatException(new StringBuffer().append(i3).append(" bytes written but getRecordSize() reports ").append(getRecordSize()).toString());
    }

    public byte[] getData() {
        return this.thedata;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int getRecordSize() {
        return this.thedata.length + 8;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public Object clone() {
        return super.clone();
    }

    public String toString() {
        String string;
        String property = System.getProperty("line.separator");
        try {
            if (this.thedata.length != 0) {
                string = new StringBuffer().append(new StringBuffer().append("  Extra Data:").append(property).toString()).append(HexDump.dump(this.thedata, 0L, 0)).toString();
            } else {
                string = "";
            }
        } catch (Exception unused) {
            string = "Error!!";
        }
        return new StringBuffer().append(getClass().getName()).append(":").append(property).append("  isContainer: ").append(isContainerRecord()).append(property).append("  options: 0x").append(HexDump.toHex(getOptions())).append(property).append("  recordId: 0x").append(HexDump.toHex(getRecordId())).append(property).append("  numchildren: ").append(getChildRecords().size()).append(property).append(string).toString();
    }
}
