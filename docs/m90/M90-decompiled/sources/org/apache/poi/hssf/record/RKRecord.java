package org.apache.poi.hssf.record;

import org.apache.poi.hssf.util.RKUtil;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class RKRecord extends Record implements CellValueRecordInterface {
    public static final short RK_IEEE_NUMBER = 0;
    public static final short RK_IEEE_NUMBER_TIMES_100 = 1;
    public static final short RK_INTEGER = 2;
    public static final short RK_INTEGER_TIMES_100 = 3;
    public static final short sid = 638;
    private int field_1_row;
    private short field_2_col;
    private short field_3_xf_index;
    private int field_4_rk_number;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isInValueSection() {
        return true;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isValue() {
        return true;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setColumn(short s) {
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setRow(int i) {
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setXFIndex(short s) {
    }

    public RKRecord() {
    }

    public RKRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public RKRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 638) {
            throw new RecordFormatException("NOT A valid RK RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_row = LittleEndian.getUShort(bArr, i + 0);
        this.field_2_col = LittleEndian.getShort(bArr, i + 2);
        this.field_3_xf_index = LittleEndian.getShort(bArr, i + 4);
        this.field_4_rk_number = LittleEndian.getInt(bArr, i + 6);
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public int getRow() {
        return this.field_1_row;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public short getColumn() {
        return this.field_2_col;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public short getXFIndex() {
        return this.field_3_xf_index;
    }

    public int getRKField() {
        return this.field_4_rk_number;
    }

    public short getRKType() {
        return (short) (this.field_4_rk_number & 3);
    }

    public double getRKNumber() {
        return RKUtil.decodeNumber(this.field_4_rk_number);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[RK]\n");
        stringBuffer.append("    .row            = ").append(Integer.toHexString(getRow())).append("\n");
        stringBuffer.append("    .col            = ").append(Integer.toHexString(getColumn())).append("\n");
        stringBuffer.append("    .xfindex        = ").append(Integer.toHexString(getXFIndex())).append("\n");
        stringBuffer.append("    .rknumber       = ").append(Integer.toHexString(getRKField())).append("\n");
        stringBuffer.append("        .rktype     = ").append(Integer.toHexString(getRKType())).append("\n");
        stringBuffer.append("        .rknumber   = ").append(getRKNumber()).append("\n");
        stringBuffer.append("[/RK]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        NumberRecord numberRecord = new NumberRecord();
        numberRecord.setColumn(getColumn());
        numberRecord.setRow(getRow());
        numberRecord.setValue(getRKNumber());
        numberRecord.setXFIndex(getXFIndex());
        return numberRecord.serialize(i, bArr);
    }

    public static void main(String[] strArr) {
        int[] iArr = {1072693248, 1079951361, 49382714, 49382715, -52598374};
        double[] dArr = {1.0d, 1.23d, 1.2345678E7d, 123456.78d, -1.3149594E7d};
        for (int i = 0; i < 5; i++) {
            System.out.println(new StringBuffer().append("input = ").append(Integer.toHexString(iArr[i])).append(" -> ").append(dArr[i]).append(": ").append(RKUtil.decodeNumber(iArr[i])).toString());
        }
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public boolean isBefore(CellValueRecordInterface cellValueRecordInterface) {
        if (getRow() > cellValueRecordInterface.getRow()) {
            return false;
        }
        if (getRow() != cellValueRecordInterface.getRow() || getColumn() <= cellValueRecordInterface.getColumn()) {
            return (getRow() == cellValueRecordInterface.getRow() && getColumn() == cellValueRecordInterface.getColumn()) ? false : true;
        }
        return false;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public boolean isAfter(CellValueRecordInterface cellValueRecordInterface) {
        if (getRow() < cellValueRecordInterface.getRow()) {
            return false;
        }
        if (getRow() != cellValueRecordInterface.getRow() || getColumn() >= cellValueRecordInterface.getColumn()) {
            return (getRow() == cellValueRecordInterface.getRow() && getColumn() == cellValueRecordInterface.getColumn()) ? false : true;
        }
        return false;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public boolean isEqual(CellValueRecordInterface cellValueRecordInterface) {
        return getRow() == cellValueRecordInterface.getRow() && getColumn() == cellValueRecordInterface.getColumn();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        RKRecord rKRecord = new RKRecord();
        rKRecord.field_1_row = this.field_1_row;
        rKRecord.field_2_col = this.field_2_col;
        rKRecord.field_3_xf_index = this.field_3_xf_index;
        rKRecord.field_4_rk_number = this.field_4_rk_number;
        return rKRecord;
    }
}
