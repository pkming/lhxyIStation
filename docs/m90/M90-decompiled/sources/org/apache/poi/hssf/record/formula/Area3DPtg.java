package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.util.SheetReferences;
import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class Area3DPtg extends Ptg {
    private static final int SIZE = 11;
    public static final byte sid = 59;
    private short field_1_index_extern_sheet;
    private short field_2_first_row;
    private short field_3_last_row;
    private short field_4_first_column;
    private short field_5_last_column;
    private BitField rowRelative = new BitField(32768);
    private BitField colRelative = new BitField(16384);

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 0;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        return 11;
    }

    public Area3DPtg() {
    }

    public Area3DPtg(String str, short s) {
        setArea(str);
        setExternSheetIndex(s);
    }

    public Area3DPtg(byte[] bArr, int i) {
        int i2 = i + 1;
        this.field_1_index_extern_sheet = LittleEndian.getShort(bArr, i2 + 0);
        this.field_2_first_row = LittleEndian.getShort(bArr, i2 + 2);
        this.field_3_last_row = LittleEndian.getShort(bArr, i2 + 4);
        this.field_4_first_column = LittleEndian.getShort(bArr, i2 + 6);
        this.field_5_last_column = LittleEndian.getShort(bArr, i2 + 8);
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("AreaPtg\n");
        stringBuffer.append(new StringBuffer().append("Index to Extern Sheet = ").append((int) getExternSheetIndex()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("firstRow = ").append((int) getFirstRow()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("lastRow  = ").append((int) getLastRow()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("firstCol = ").append((int) getFirstColumn()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("lastCol  = ").append((int) getLastColumn()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("firstColRel= ").append(isFirstRowRelative()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("lastColRowRel = ").append(isLastRowRelative()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("firstColRel   = ").append(isFirstColRelative()).toString()).append("\n");
        stringBuffer.append(new StringBuffer().append("lastColRel    = ").append(isLastColRelative()).toString()).append("\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        bArr[i + 0] = (byte) (this.ptgClass + sid);
        LittleEndian.putShort(bArr, i + 1, getExternSheetIndex());
        LittleEndian.putShort(bArr, i + 3, getFirstRow());
        LittleEndian.putShort(bArr, i + 5, getLastRow());
        LittleEndian.putShort(bArr, i + 7, getFirstColumnRaw());
        LittleEndian.putShort(bArr, i + 9, getLastColumnRaw());
    }

    public short getExternSheetIndex() {
        return this.field_1_index_extern_sheet;
    }

    public void setExternSheetIndex(short s) {
        this.field_1_index_extern_sheet = s;
    }

    public short getFirstRow() {
        return this.field_2_first_row;
    }

    public void setFirstRow(short s) {
        this.field_2_first_row = s;
    }

    public short getLastRow() {
        return this.field_3_last_row;
    }

    public void setLastRow(short s) {
        this.field_3_last_row = s;
    }

    public short getFirstColumn() {
        return (short) (this.field_4_first_column & 255);
    }

    public short getFirstColumnRaw() {
        return this.field_4_first_column;
    }

    public boolean isFirstRowRelative() {
        return this.rowRelative.isSet(this.field_4_first_column);
    }

    public boolean isFirstColRelative() {
        return this.colRelative.isSet(this.field_4_first_column);
    }

    public void setFirstColumn(short s) {
        short s2 = (short) (this.field_4_first_column & 65280);
        this.field_4_first_column = s2;
        this.field_4_first_column = (short) ((s & 255) | s2);
    }

    public void setFirstColumnRaw(short s) {
        this.field_4_first_column = s;
    }

    public short getLastColumn() {
        return (short) (this.field_5_last_column & 255);
    }

    public short getLastColumnRaw() {
        return this.field_5_last_column;
    }

    public boolean isLastRowRelative() {
        return this.rowRelative.isSet(this.field_5_last_column);
    }

    public boolean isLastColRelative() {
        return this.colRelative.isSet(this.field_5_last_column);
    }

    public void setLastColumn(short s) {
        short s2 = (short) (this.field_5_last_column & 65280);
        this.field_5_last_column = s2;
        this.field_5_last_column = (short) ((s & 255) | s2);
    }

    public void setLastColumnRaw(short s) {
        this.field_5_last_column = s;
    }

    public void setFirstRowRelative(boolean z) {
        this.field_4_first_column = this.rowRelative.setShortBoolean(this.field_4_first_column, z);
    }

    public void setFirstColRelative(boolean z) {
        this.field_4_first_column = this.colRelative.setShortBoolean(this.field_4_first_column, z);
    }

    public void setLastRowRelative(boolean z) {
        this.field_5_last_column = this.rowRelative.setShortBoolean(this.field_5_last_column, z);
    }

    public void setLastColRelative(boolean z) {
        this.field_5_last_column = this.colRelative.setShortBoolean(this.field_5_last_column, z);
    }

    public void setArea(String str) {
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

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toFormulaString(Workbook workbook) {
        SheetReferences sheetReferences = workbook == null ? null : workbook.getSheetReferences();
        StringBuffer stringBuffer = new StringBuffer();
        if (sheetReferences != null) {
            stringBuffer.append(sheetReferences.getSheetName(this.field_1_index_extern_sheet));
            stringBuffer.append('!');
        }
        stringBuffer.append(new CellReference(getFirstRow(), getFirstColumn(), !isFirstRowRelative(), !isFirstColRelative()).toString());
        stringBuffer.append(':');
        stringBuffer.append(new CellReference(getLastRow(), getLastColumn(), !isLastRowRelative(), !isLastColRelative()).toString());
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        Area3DPtg area3DPtg = new Area3DPtg();
        area3DPtg.field_1_index_extern_sheet = this.field_1_index_extern_sheet;
        area3DPtg.field_2_first_row = this.field_2_first_row;
        area3DPtg.field_3_last_row = this.field_3_last_row;
        area3DPtg.field_4_first_column = this.field_4_first_column;
        area3DPtg.field_5_last_column = this.field_5_last_column;
        area3DPtg.setClass(this.ptgClass);
        return area3DPtg;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Area3DPtg)) {
            return false;
        }
        Area3DPtg area3DPtg = (Area3DPtg) obj;
        return this.field_1_index_extern_sheet == area3DPtg.field_1_index_extern_sheet && this.field_2_first_row == area3DPtg.field_2_first_row && this.field_3_last_row == area3DPtg.field_3_last_row && this.field_4_first_column == area3DPtg.field_4_first_column && this.field_5_last_column == area3DPtg.field_5_last_column;
    }

    public int hashCode() {
        return (((((((this.field_1_index_extern_sheet * 29) + this.field_2_first_row) * 29) + this.field_3_last_row) * 29) + this.field_4_first_column) * 29) + this.field_5_last_column;
    }
}
