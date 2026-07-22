package org.apache.poi.ddf;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class EscherContainerRecord extends EscherRecord {
    public static final short BSTORE_CONTAINER = -4095;
    public static final short DGG_CONTAINER = -4096;
    public static final short DG_CONTAINER = -4094;
    public static final short SOLVER_CONTAINER = -4091;
    public static final short SPGR_CONTAINER = -4093;
    public static final short SP_CONTAINER = -4092;
    private List childRecords = new ArrayList();

    @Override // org.apache.poi.ddf.EscherRecord
    public int fillFields(byte[] bArr, int i, EscherRecordFactory escherRecordFactory) {
        int header = readHeader(bArr, i);
        int i2 = 8;
        int i3 = i + 8;
        while (header > 0 && i3 < bArr.length) {
            EscherRecord escherRecordCreateRecord = escherRecordFactory.createRecord(bArr, i3);
            int iFillFields = escherRecordCreateRecord.fillFields(bArr, i3, escherRecordFactory);
            i2 += iFillFields;
            i3 += iFillFields;
            header -= iFillFields;
            getChildRecords().add(escherRecordCreateRecord);
            if (i3 >= bArr.length && header > 0) {
                System.out.println(new StringBuffer().append("WARNING: ").append(header).append(" bytes remaining but no space left").toString());
            }
        }
        return i2;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int serialize(int i, byte[] bArr, EscherSerializationListener escherSerializationListener) {
        escherSerializationListener.beforeRecordSerialize(i, getRecordId(), this);
        LittleEndian.putShort(bArr, i, getOptions());
        LittleEndian.putShort(bArr, i + 2, getRecordId());
        Iterator it = getChildRecords().iterator();
        int recordSize = 0;
        while (it.hasNext()) {
            recordSize += ((EscherRecord) it.next()).getRecordSize();
        }
        LittleEndian.putInt(bArr, i + 4, recordSize);
        int iSerialize = i + 8;
        Iterator it2 = getChildRecords().iterator();
        while (it2.hasNext()) {
            iSerialize += ((EscherRecord) it2.next()).serialize(iSerialize, bArr, escherSerializationListener);
        }
        int i2 = iSerialize - i;
        escherSerializationListener.afterRecordSerialize(iSerialize, getRecordId(), i2, this);
        return i2;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int getRecordSize() {
        Iterator it = getChildRecords().iterator();
        int recordSize = 0;
        while (it.hasNext()) {
            recordSize += ((EscherRecord) it.next()).getRecordSize();
        }
        return recordSize + 8;
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
    public String getRecordName() {
        switch (getRecordId()) {
            case -4096:
                return "DggContainer";
            case -4095:
                return "BStoreContainer";
            case -4094:
                return "DgContainer";
            case -4093:
                return "SpgrContainer";
            case -4092:
                return "SpContainer";
            case -4091:
                return "SolverContainer";
            default:
                return new StringBuffer().append("Container 0x").append(HexDump.toHex(getRecordId())).toString();
        }
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public void display(PrintWriter printWriter, int i) {
        super.display(printWriter, i);
        Iterator it = this.childRecords.iterator();
        while (it.hasNext()) {
            ((EscherRecord) it.next()).display(printWriter, i + 1);
        }
    }

    public void addChildRecord(EscherRecord escherRecord) {
        this.childRecords.add(escherRecord);
    }

    public String toString() {
        String property = System.getProperty("line.separator");
        StringBuffer stringBuffer = new StringBuffer();
        if (getChildRecords().size() > 0) {
            stringBuffer.append(new StringBuffer().append("  children: ").append(property).toString());
            Iterator it = getChildRecords().iterator();
            while (it.hasNext()) {
                stringBuffer.append(((EscherRecord) it.next()).toString());
            }
        }
        return new StringBuffer().append(getClass().getName()).append(" (").append(getRecordName()).append("):").append(property).append("  isContainer: ").append(isContainerRecord()).append(property).append("  options: 0x").append(HexDump.toHex(getOptions())).append(property).append("  recordId: 0x").append(HexDump.toHex(getRecordId())).append(property).append("  numchildren: ").append(getChildRecords().size()).append(property).append(stringBuffer.toString()).toString();
    }

    public EscherSpRecord getChildById(short s) {
        for (EscherRecord escherRecord : this.childRecords) {
            if (escherRecord.getRecordId() == s) {
                return (EscherSpRecord) escherRecord;
            }
        }
        return null;
    }
}
