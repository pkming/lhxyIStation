package org.apache.poi.hssf.record;

import java.util.ArrayList;
import org.apache.poi.hssf.util.RKUtil;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class MulRKRecord extends Record {
    public static final short sid = 189;
    private int field_1_row;
    private short field_2_first_col;
    private ArrayList field_3_rks;
    private short field_4_last_col;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 189;
    }

    public MulRKRecord() {
    }

    public MulRKRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public MulRKRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    public int getRow() {
        return this.field_1_row;
    }

    public short getFirstColumn() {
        return this.field_2_first_col;
    }

    public short getLastColumn() {
        return this.field_4_last_col;
    }

    public int getNumColumns() {
        return (this.field_4_last_col - this.field_2_first_col) + 1;
    }

    public short getXFAt(int i) {
        return ((RkRec) this.field_3_rks.get(i)).xf;
    }

    public double getRKNumberAt(int i) {
        return RKUtil.decodeNumber(((RkRec) this.field_3_rks.get(i)).rk);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_row = LittleEndian.getUShort(bArr, i + 0);
        this.field_2_first_col = LittleEndian.getShort(bArr, i + 2);
        ArrayList rKs = parseRKs(bArr, 4, i, s);
        this.field_3_rks = rKs;
        this.field_4_last_col = LittleEndian.getShort(bArr, (rKs.size() * 6) + 4 + i);
    }

    private ArrayList parseRKs(byte[] bArr, int i, int i2, short s) {
        ArrayList arrayList = new ArrayList();
        while (i < s - 2) {
            RkRec rkRec = new RkRec();
            rkRec.xf = LittleEndian.getShort(bArr, i + i2);
            int i3 = i + 2;
            rkRec.rk = LittleEndian.getInt(bArr, i3 + i2);
            i = i3 + 4;
            arrayList.add(rkRec);
        }
        return arrayList;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[MULRK]\n");
        stringBuffer.append("firstcol  = ").append(Integer.toHexString(getFirstColumn())).append("\n");
        stringBuffer.append(" lastcol  = ").append(Integer.toHexString(getLastColumn())).append("\n");
        for (int i = 0; i < getNumColumns(); i++) {
            stringBuffer.append("xf").append(i).append("        = ").append(Integer.toHexString(getXFAt(i))).append("\n");
            stringBuffer.append("rk").append(i).append("        = ").append(getRKNumberAt(i)).append("\n");
        }
        stringBuffer.append("[/MULRK]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 189) {
            throw new RecordFormatException("Not a MulRKRecord!");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        throw new RecordFormatException("Sorry, you can't serialize a MulRK in this release");
    }
}
