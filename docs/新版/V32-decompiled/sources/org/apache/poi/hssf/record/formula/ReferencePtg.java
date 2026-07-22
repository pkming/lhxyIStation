package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class ReferencePtg extends Ptg {
    private static final int SIZE = 5;
    public static final byte sid = 36;
    private short field_1_row;
    private short field_2_col;
    private BitField rowRelative = new BitField(32768);
    private BitField colRelative = new BitField(16384);

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 0;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        return 5;
    }

    private ReferencePtg() {
    }

    public ReferencePtg(String str) {
        CellReference cellReference = new CellReference(str);
        setRow((short) cellReference.getRow());
        setColumn(cellReference.getCol());
        setColRelative(!cellReference.isColAbsolute());
        setRowRelative(!cellReference.isRowAbsolute());
    }

    public ReferencePtg(byte[] bArr, int i) {
        int i2 = i + 1;
        this.field_1_row = LittleEndian.getShort(bArr, i2 + 0);
        this.field_2_col = LittleEndian.getShort(bArr, i2 + 2);
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("[ValueReferencePtg]\n");
        stringBuffer.append("row = ").append((int) getRow()).append("\n");
        stringBuffer.append("col = ").append((int) getColumnRaw()).append("\n");
        stringBuffer.append("rowrelative = ").append(isRowRelative()).append("\n");
        stringBuffer.append("colrelative = ").append(isColRelative()).append("\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        bArr[i] = (byte) (this.ptgClass + 36);
        LittleEndian.putShort(bArr, i + 1, this.field_1_row);
        LittleEndian.putShort(bArr, i + 3, this.field_2_col);
    }

    public void setRow(short s) {
        this.field_1_row = s;
    }

    public short getRow() {
        return this.field_1_row;
    }

    public boolean isRowRelative() {
        return this.rowRelative.isSet(this.field_2_col);
    }

    public void setRowRelative(boolean z) {
        this.field_2_col = this.rowRelative.setShortBoolean(this.field_2_col, z);
    }

    public boolean isColRelative() {
        return this.colRelative.isSet(this.field_2_col);
    }

    public void setColRelative(boolean z) {
        this.field_2_col = this.colRelative.setShortBoolean(this.field_2_col, z);
    }

    public void setColumnRaw(short s) {
        this.field_2_col = s;
    }

    public short getColumnRaw() {
        return this.field_2_col;
    }

    public void setColumn(short s) {
        this.field_2_col = s;
    }

    public short getColumn() {
        return this.rowRelative.setShortBoolean(this.colRelative.setShortBoolean(this.field_2_col, false), false);
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toFormulaString(Workbook workbook) {
        return new CellReference(getRow(), getColumn(), !isRowRelative(), !isColRelative()).toString();
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        ReferencePtg referencePtg = new ReferencePtg();
        referencePtg.field_1_row = this.field_1_row;
        referencePtg.field_2_col = this.field_2_col;
        referencePtg.setClass(this.ptgClass);
        return referencePtg;
    }
}
