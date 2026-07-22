package org.apache.poi.hssf.record;

/* JADX INFO: loaded from: classes3.dex */
public class HorizontalPageBreakRecord extends PageBreakRecord {
    public static final short sid = 27;

    @Override // org.apache.poi.hssf.record.PageBreakRecord, org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 27;
    }

    public HorizontalPageBreakRecord() {
    }

    public HorizontalPageBreakRecord(short s) {
        super(s);
    }

    public HorizontalPageBreakRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public HorizontalPageBreakRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }
}
