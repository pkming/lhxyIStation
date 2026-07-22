package org.apache.poi.hssf.record;

import android.mtp.MtpConstants;
import java.util.ArrayList;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class ContinueRecord extends Record {
    public static final short sid = 60;
    private byte[] field_1_data;

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 60;
    }

    public ContinueRecord() {
    }

    public ContinueRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public ContinueRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public byte[] serialize() {
        byte[] bArr = new byte[this.field_1_data.length + 4];
        serialize(0, bArr);
        return bArr;
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i, (short) 60);
        LittleEndian.putShort(bArr, i + 2, (short) this.field_1_data.length);
        byte[] bArr2 = this.field_1_data;
        System.arraycopy(bArr2, 0, bArr, i + 4, bArr2.length);
        return this.field_1_data.length + 4;
    }

    public void setData(byte[] bArr) {
        this.field_1_data = bArr;
    }

    public byte[] getData() {
        return this.field_1_data;
    }

    public static byte[] processContinue(byte[] bArr) {
        int length = bArr.length;
        int length2 = MtpConstants.RESPONSE_INVALID_CODE_FORMAT;
        int i = length / MtpConstants.RESPONSE_INVALID_CODE_FORMAT;
        ArrayList arrayList = new ArrayList(i);
        int i2 = 8214;
        for (int i3 = 0; i3 < i; i3++) {
            ContinueRecord continueRecord = new ContinueRecord();
            int iMin = Math.min(MtpConstants.RESPONSE_PARTIAL_DELETION, bArr.length - i2);
            byte[] bArr2 = new byte[iMin];
            System.arraycopy(bArr, i2, bArr2, 0, iMin);
            i2 += iMin;
            continueRecord.setData(bArr2);
            arrayList.add(continueRecord.serialize());
        }
        int length3 = 8214;
        for (int i4 = 0; i4 < i; i4++) {
            length3 += ((byte[]) arrayList.get(i4)).length;
        }
        byte[] bArr3 = new byte[length3];
        System.arraycopy(bArr, 0, bArr3, 0, MtpConstants.RESPONSE_INVALID_CODE_FORMAT);
        for (int i5 = 0; i5 < i; i5++) {
            byte[] bArr4 = (byte[]) arrayList.get(i5);
            System.arraycopy(bArr4, 0, bArr3, length2, bArr4.length);
            length2 += bArr4.length;
        }
        return bArr3;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s) {
        this.field_1_data = bArr;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 60) {
            throw new RecordFormatException("Not a Continue Record");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[CONTINUE RECORD]\n");
        stringBuffer.append("    .id        = ").append(Integer.toHexString(60)).append("\n");
        stringBuffer.append("[/CONTINUE RECORD]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        ContinueRecord continueRecord = new ContinueRecord();
        continueRecord.setData(this.field_1_data);
        return continueRecord;
    }
}
