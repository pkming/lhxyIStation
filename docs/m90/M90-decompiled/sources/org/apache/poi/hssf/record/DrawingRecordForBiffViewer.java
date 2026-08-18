package org.apache.poi.hssf.record;

/* JADX INFO: loaded from: classes3.dex */
public class DrawingRecordForBiffViewer extends AbstractEscherHolderRecord {
    public static final short sid = 236;

    @Override // org.apache.poi.hssf.record.AbstractEscherHolderRecord
    protected String getRecordName() {
        return "MSODRAWING";
    }

    @Override // org.apache.poi.hssf.record.AbstractEscherHolderRecord, org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 236;
    }

    public DrawingRecordForBiffViewer() {
    }

    public DrawingRecordForBiffViewer(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public DrawingRecordForBiffViewer(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }
}
