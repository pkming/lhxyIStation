package org.apache.poi.hssf.record;

/* JADX INFO: loaded from: classes3.dex */
public class DrawingSelectionRecord extends AbstractEscherHolderRecord {
    public static final short sid = 237;

    @Override // org.apache.poi.hssf.record.AbstractEscherHolderRecord
    protected String getRecordName() {
        return "MSODRAWINGSELECTION";
    }

    @Override // org.apache.poi.hssf.record.AbstractEscherHolderRecord, org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public DrawingSelectionRecord() {
    }

    public DrawingSelectionRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public DrawingSelectionRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }
}
