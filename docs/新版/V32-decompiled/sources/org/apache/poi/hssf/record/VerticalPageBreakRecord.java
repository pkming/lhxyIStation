package org.apache.poi.hssf.record;

/* JADX INFO: loaded from: classes3.dex */
public class VerticalPageBreakRecord extends PageBreakRecord {
    public static final short sid = 26;

    @Override // org.apache.poi.hssf.record.PageBreakRecord, org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 26;
    }

    public VerticalPageBreakRecord() {
    }

    public VerticalPageBreakRecord(short s) {
        super(s);
    }

    public VerticalPageBreakRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public VerticalPageBreakRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }
}
