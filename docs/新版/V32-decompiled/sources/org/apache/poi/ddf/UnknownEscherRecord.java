package org.apache.poi.ddf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class UnknownEscherRecord extends EscherRecord {
    private static final byte[] NO_BYTES = new byte[0];
    private byte[] thedata = NO_BYTES;
    private List childRecords = new ArrayList();

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
        return i3;
    }

    public byte[] getData() {
        return this.thedata;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int getRecordSize() {
        return this.thedata.length + 8;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public List getChildRecords() {
        return this.childRecords;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public void setChildRecords(List list) {
        this.childRecords = list;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public Object clone() {
        return super.clone();
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public String getRecordName() {
        return new StringBuffer().append("Unknown 0x").append(HexDump.toHex(getRecordId())).toString();
    }

    public String toString() {
        String string;
        String property = System.getProperty("line.separator");
        StringBuffer stringBuffer = new StringBuffer();
        if (getChildRecords().size() > 0) {
            stringBuffer.append(new StringBuffer().append("  children: ").append(property).toString());
            Iterator it = getChildRecords().iterator();
            while (it.hasNext()) {
                stringBuffer.append(((EscherRecord) it.next()).toString());
                stringBuffer.append(property);
            }
        }
        try {
            if (this.thedata.length != 0) {
                string = new StringBuffer().append(new StringBuffer().append("  Extra Data:").append(property).toString()).append(HexDump.dump(this.thedata, 0L, 0)).toString();
            } else {
                string = "";
            }
        } catch (Exception unused) {
            string = "Error!!";
        }
        return new StringBuffer().append(getClass().getName()).append(":").append(property).append("  isContainer: ").append(isContainerRecord()).append(property).append("  options: 0x").append(HexDump.toHex(getOptions())).append(property).append("  recordId: 0x").append(HexDump.toHex(getRecordId())).append(property).append("  numchildren: ").append(getChildRecords().size()).append(property).append(string).append(stringBuffer.toString()).toString();
    }

    public void addChildRecord(EscherRecord escherRecord) {
        getChildRecords().add(escherRecord);
    }
}
