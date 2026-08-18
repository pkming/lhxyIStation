package org.apache.poi.hssf.record;

import java.awt.Image;
import org.apache.poi.hssf.record.excel.ImdataBiffElement;

/* JADX INFO: loaded from: classes3.dex */
public class ImageRecord extends Record {
    public static final short sid = 127;
    byte[] data1;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 127;
    }

    public ImageRecord() {
    }

    public ImageRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public ImageRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    public ImageRecord(Image image, int i, int i2, int i3, int i4, int i5) {
        ImdataBiffElement imdataBiffElementCreateImdataBiffElement = ImdataBiffElement.createImdataBiffElement(image);
        imdataBiffElementCreateImdataBiffElement.setIndex(i);
        imdataBiffElementCreateImdataBiffElement.setColLeft(i3);
        imdataBiffElementCreateImdataBiffElement.setDxL(20);
        imdataBiffElementCreateImdataBiffElement.setRowTop(i2);
        imdataBiffElementCreateImdataBiffElement.setDyT(20);
        imdataBiffElementCreateImdataBiffElement.setColRight(i5 + 1);
        imdataBiffElementCreateImdataBiffElement.setDxR(65534);
        imdataBiffElementCreateImdataBiffElement.setRowBottom(i4 + 1);
        imdataBiffElementCreateImdataBiffElement.setDyB(65532);
        try {
            this.data1 = imdataBiffElementCreateImdataBiffElement.toBinary();
        } catch (Exception unused) {
            this.data1 = null;
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 127) {
            throw new RecordFormatException("NOT A IMAGE RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        if (bArr != null) {
            this.data1 = bArr;
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[IMAGE]\n");
        stringBuffer.append("    .id         = ").append(Integer.toHexString(127)).append("\n");
        stringBuffer.append("[/IMAGE]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        byte[] bArr2 = this.data1;
        if (bArr2 != null) {
            System.arraycopy(bArr2, 0, bArr, i, bArr2.length);
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        byte[] bArr = this.data1;
        if (bArr == null) {
            return 0;
        }
        return bArr.length;
    }
}
