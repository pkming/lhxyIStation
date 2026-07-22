package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class AreaPtg extends Ptg {
    private static final int SIZE = 9;
    public static final short sid = 37;
    private short field_1_first_row;
    private short field_2_last_row;
    private short field_3_first_column;
    private short field_4_last_column;
    private BitField rowRelative = new BitField(32768);
    private BitField colRelative = new BitField(16384);
    private BitField column = new BitField(16383);

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 0;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        return 9;
    }

    private AreaPtg() {
    }

    public AreaPtg(String str) {
        AreaReference areaReference = new AreaReference(str);
        setFirstRow((short) areaReference.getCells()[0].getRow());
        setFirstColumn(areaReference.getCells()[0].getCol());
        setLastRow((short) areaReference.getCells()[1].getRow());
        setLastColumn(areaReference.getCells()[1].getCol());
        setFirstColRelative(!areaReference.getCells()[0].isColAbsolute());
        setLastColRelative(!areaReference.getCells()[1].isColAbsolute());
        setFirstRowRelative(!areaReference.getCells()[0].isRowAbsolute());
        setLastRowRelative(!areaReference.getCells()[1].isRowAbsolute());
    }

    public AreaPtg(byte[] bArr, int i) {
        int i2 = i + 1;
        this.field_1_first_row = LittleEndian.getShort(bArr, i2 + 0);
        this.field_2_last_row = LittleEndian.getShort(bArr, i2 + 2);
        this.field_3_first_column = LittleEndian.getShort(bArr, i2 + 4);
        this.field_4_last_column = LittleEndian.getShort(bArr, i2 + 6);
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("AreaPtg\n");
        stringBuffer.append(new StringBuffer().append("firstRow = ").append((int) getFirstRow()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("lastRow  = ").append((int) getLastRow()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("firstCol = ").append((int) getFirstColumn()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("lastCol  = ").append((int) getLastColumn()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("firstColRowRel= ").append(isFirstRowRelative()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("lastColRowRel = ").append(isLastRowRelative()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("firstColRel   = ").append(isFirstColRelative()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("lastColRel    = ").append(isLastColRelative()).toString()).append("\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        bArr[i] = (byte) (this.ptgClass + 37);
        LittleEndian.putShort(bArr, i + 1, this.field_1_first_row);
        LittleEndian.putShort(bArr, i + 3, this.field_2_last_row);
        LittleEndian.putShort(bArr, i + 5, this.field_3_first_column);
        LittleEndian.putShort(bArr, i + 7, this.field_4_last_column);
    }

    public short getFirstRow() {
        return this.field_1_first_row;
    }

    public void setFirstRow(short s) {
        this.field_1_first_row = s;
    }

    public short getLastRow() {
        return this.field_2_last_row;
    }

    public void setLastRow(short s) {
        this.field_2_last_row = s;
    }

    public short getFirstColumn() {
        return this.column.getShortValue(this.field_3_first_column);
    }

    public short getFirstColumnRaw() {
        return this.field_3_first_column;
    }

    public boolean isFirstRowRelative() {
        return this.rowRelative.isSet(this.field_3_first_column);
    }

    public void setFirstRowRelative(boolean z) {
        this.field_3_first_column = this.rowRelative.setShortBoolean(this.field_3_first_column, z);
    }

    public boolean isFirstColRelative() {
        return this.colRelative.isSet(this.field_3_first_column);
    }

    public void setFirstColRelative(boolean z) {
        this.field_3_first_column = this.colRelative.setShortBoolean(this.field_3_first_column, z);
    }

    public void setFirstColumn(short s) {
        this.field_3_first_column = s;
    }

    public void setFirstColumnRaw(short s) {
        this.field_3_first_column = s;
    }

    public short getLastColumn() {
        return this.column.getShortValue(this.field_4_last_column);
    }

    public short getLastColumnRaw() {
        return this.field_4_last_column;
    }

    public boolean isLastRowRelative() {
        return this.rowRelative.isSet(this.field_4_last_column);
    }

    public void setLastRowRelative(boolean z) {
        this.field_4_last_column = this.rowRelative.setShortBoolean(this.field_4_last_column, z);
    }

    public boolean isLastColRelative() {
        return this.colRelative.isSet(this.field_4_last_column);
    }

    public void setLastColRelative(boolean z) {
        this.field_4_last_column = this.colRelative.setShortBoolean(this.field_4_last_column, z);
    }

    public void setLastColumn(short s) {
        this.field_4_last_column = s;
    }

    public void setLastColumnRaw(short s) {
        this.field_4_last_column = s;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toFormulaString(Workbook workbook) {
        return new StringBuffer().append(new CellReference(getFirstRow(), getFirstColumn(), !isFirstRowRelative(), !isFirstColRelative()).toString()).append(":").append(new CellReference(getLastRow(), getLastColumn(), !isLastRowRelative(), !isLastColRelative()).toString()).toString();
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        AreaPtg areaPtg = new AreaPtg();
        areaPtg.field_1_first_row = this.field_1_first_row;
        areaPtg.field_2_last_row = this.field_2_last_row;
        areaPtg.field_3_first_column = this.field_3_first_column;
        areaPtg.field_4_last_column = this.field_4_last_column;
        areaPtg.setClass(this.ptgClass);
        return areaPtg;
    }
}
