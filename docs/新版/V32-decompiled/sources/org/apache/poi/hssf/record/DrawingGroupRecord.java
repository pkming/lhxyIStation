package org.apache.poi.hssf.record;

/* JADX INFO: loaded from: classes3.dex */
public class DrawingGroupRecord extends AbstractEscherHolderRecord {
    public static final short sid = 235;

    @Override // org.apache.poi.hssf.record.AbstractEscherHolderRecord
    protected String getRecordName() {
        return "MSODRAWINGGROUP";
    }

    @Override // org.apache.poi.hssf.record.AbstractEscherHolderRecord, org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public DrawingGroupRecord() {
    }

    public DrawingGroupRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public DrawingGroupRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }
}
