package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.util.RangeAddress;
import org.apache.poi.hssf.util.SheetReferences;
import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class Ref3DPtg extends Ptg {
    private static final int SIZE = 7;
    public static final byte sid = 58;
    private short field_1_index_extern_sheet;
    private short field_2_row;
    private short field_3_column;
    private BitField rowRelative = new BitField(32768);
    private BitField colRelative = new BitField(16384);

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 0;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        return 7;
    }

    public Ref3DPtg() {
    }

    public Ref3DPtg(byte[] bArr, int i) {
        int i2 = i + 1;
        this.field_1_index_extern_sheet = LittleEndian.getShort(bArr, i2 + 0);
        this.field_2_row = LittleEndian.getShort(bArr, i2 + 2);
        this.field_3_column = LittleEndian.getShort(bArr, i2 + 4);
    }

    public Ref3DPtg(String str, short s) {
        CellReference cellReference = new CellReference(str);
        setRow((short) cellReference.getRow());
        setColumn(cellReference.getCol());
        setColRelative(!cellReference.isColAbsolute());
        setRowRelative(!cellReference.isRowAbsolute());
        setExternSheetIndex(s);
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Ref3dPtg\n");
        stringBuffer.append(new StringBuffer().append("Index to Extern Sheet = ").append((int) getExternSheetIndex()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("Row = ").append((int) getRow()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("Col  = ").append((int) getColumn()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("ColRowRel= ").append(isRowRelative()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("ColRel   = ").append(isColRelative()).toString()).append("\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        bArr[i + 0] = (byte) (this.ptgClass + sid);
        LittleEndian.putShort(bArr, i + 1, getExternSheetIndex());
        LittleEndian.putShort(bArr, i + 3, getRow());
        LittleEndian.putShort(bArr, i + 5, getColumnRaw());
    }

    public short getExternSheetIndex() {
        return this.field_1_index_extern_sheet;
    }

    public void setExternSheetIndex(short s) {
        this.field_1_index_extern_sheet = s;
    }

    public short getRow() {
        return this.field_2_row;
    }

    public void setRow(short s) {
        this.field_2_row = s;
    }

    public short getColumn() {
        return (short) (this.field_3_column & 255);
    }

    public short getColumnRaw() {
        return this.field_3_column;
    }

    public boolean isRowRelative() {
        return this.rowRelative.isSet(this.field_3_column);
    }

    public void setRowRelative(boolean z) {
        this.field_3_column = this.rowRelative.setShortBoolean(this.field_3_column, z);
    }

    public boolean isColRelative() {
        return this.colRelative.isSet(this.field_3_column);
    }

    public void setColRelative(boolean z) {
        this.field_3_column = this.colRelative.setShortBoolean(this.field_3_column, z);
    }

    public void setColumn(short s) {
        short s2 = (short) (this.field_3_column & 65280);
        this.field_3_column = s2;
        this.field_3_column = (short) ((s & 255) | s2);
    }

    public void setColumnRaw(short s) {
        this.field_3_column = s;
    }

    public void setArea(String str) {
        String fromCell = new RangeAddress(str).getFromCell();
        setColumn((short) (r0.getXPosition(fromCell) - 1));
        setRow((short) (r0.getYPosition(fromCell) - 1));
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toFormulaString(Workbook workbook) {
        StringBuffer stringBuffer = new StringBuffer();
        SheetReferences sheetReferences = workbook == null ? null : workbook.getSheetReferences();
        if (sheetReferences != null) {
            stringBuffer.append(sheetReferences.getSheetName(this.field_1_index_extern_sheet));
            stringBuffer.append('!');
        }
        stringBuffer.append(new CellReference(getRow(), getColumn(), !isRowRelative(), !isColRelative()).toString());
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        Ref3DPtg ref3DPtg = new Ref3DPtg();
        ref3DPtg.field_1_index_extern_sheet = this.field_1_index_extern_sheet;
        ref3DPtg.field_2_row = this.field_2_row;
        ref3DPtg.field_3_column = this.field_3_column;
        ref3DPtg.setClass(this.ptgClass);
        return ref3DPtg;
    }
}
