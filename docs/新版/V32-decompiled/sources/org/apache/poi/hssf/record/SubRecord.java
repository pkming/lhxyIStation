package org.apache.poi.hssf.record;

/* JADX INFO: loaded from: classes3.dex */
public abstract class SubRecord extends Record {
    public SubRecord() {
    }

    public SubRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public SubRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    public static Record createSubRecord(short s, short s2, byte[] bArr, int i) {
        if (s == 0) {
            return new EndSubRecord(s, s2, bArr, i);
        }
        if (s == 6) {
            return new GroupMarkerSubRecord(s, s2, bArr, i);
        }
        if (s == 21) {
            return new CommonObjectDataSubRecord(s, s2, bArr, i);
        }
        return new UnknownRecord(s, s2, bArr, i);
    }
}
