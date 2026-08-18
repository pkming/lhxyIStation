package org.apache.poi.hssf.record;

import java.util.List;
import java.util.Stack;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class FormulaRecord extends Record implements CellValueRecordInterface, Comparable {
    public static final boolean EXPERIMENTAL_FORMULA_SUPPORT_ENABLED = true;
    public static final short sid = 6;
    private byte[] all_data;
    private int field_1_row;
    private short field_2_column;
    private short field_3_xf;
    private double field_4_value;
    private short field_5_options;
    private int field_6_zero;
    private short field_7_expression_len;
    private Stack field_8_parsed_expr;
    private byte[] value_data;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isInValueSection() {
        return true;
    }

    @Override // org.apache.poi.hssf.record.Record
    public boolean isValue() {
        return true;
    }

    public FormulaRecord() {
        this.field_8_parsed_expr = new Stack();
    }

    public FormulaRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public FormulaRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        try {
            this.field_1_row = LittleEndian.getUShort(bArr, i + 0);
            this.field_2_column = LittleEndian.getShort(bArr, i + 2);
            this.field_3_xf = LittleEndian.getShort(bArr, i + 4);
            int i2 = i + 6;
            this.field_4_value = LittleEndian.getDouble(bArr, i2);
            this.field_5_options = LittleEndian.getShort(bArr, i + 14);
            if (Double.isNaN(this.field_4_value)) {
                byte[] bArr2 = new byte[8];
                this.value_data = bArr2;
                System.arraycopy(bArr, i2, bArr2, 0, 8);
            }
            this.field_6_zero = LittleEndian.getInt(bArr, i + 16);
            this.field_7_expression_len = LittleEndian.getShort(bArr, i + 20);
            this.field_8_parsed_expr = getParsedExpressionTokens(bArr, s, i + 22);
        } catch (UnsupportedOperationException e) {
            this.field_8_parsed_expr = null;
            byte[] bArr3 = new byte[s + 4];
            this.all_data = bArr3;
            LittleEndian.putShort(bArr3, 0, (short) 6);
            LittleEndian.putShort(this.all_data, 2, s);
            System.arraycopy(bArr, i, this.all_data, 4, s);
            System.err.println(new StringBuffer().append("[WARNING] Unknown Ptg ").append(e.getMessage()).append(" at cell (").append(this.field_1_row).append(",").append((int) this.field_2_column).append(")").toString());
        }
    }

    private Stack getParsedExpressionTokens(byte[] bArr, short s, int i) {
        Stack stack = new Stack();
        while (i < s) {
            Ptg ptgCreatePtg = Ptg.createPtg(bArr, i);
            i += ptgCreatePtg.getSize();
            stack.push(ptgCreatePtg);
        }
        return stack;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setRow(int i) {
        this.field_1_row = i;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setColumn(short s) {
        this.field_2_column = s;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setXFIndex(short s) {
        this.field_3_xf = s;
    }

    public void setValue(double d) {
        this.field_4_value = d;
    }

    public void setOptions(short s) {
        this.field_5_options = s;
    }

    public void setExpressionLength(short s) {
        this.field_7_expression_len = s;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public int getRow() {
        return this.field_1_row;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public short getColumn() {
        return this.field_2_column;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public short getXFIndex() {
        return this.field_3_xf;
    }

    public double getValue() {
        return this.field_4_value;
    }

    public short getOptions() {
        return this.field_5_options;
    }

    public short getExpressionLength() {
        return this.field_7_expression_len;
    }

    public void pushExpressionToken(Ptg ptg) {
        this.field_8_parsed_expr.push(ptg);
    }

    public Ptg popExpressionToken() {
        return (Ptg) this.field_8_parsed_expr.pop();
    }

    public Ptg peekExpressionToken() {
        return (Ptg) this.field_8_parsed_expr.peek();
    }

    public int getNumberOfExpressionTokens() {
        Stack stack = this.field_8_parsed_expr;
        if (stack == null) {
            return 0;
        }
        return stack.size();
    }

    public List getParsedExpression() {
        return this.field_8_parsed_expr;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 6) {
            throw new RecordFormatException("NOT A FORMULA RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        byte[] bArr2;
        if (this.field_8_parsed_expr != null) {
            int totalPtgSize = getTotalPtgSize();
            LittleEndian.putShort(bArr, i + 0, (short) 6);
            LittleEndian.putShort(bArr, i + 2, (short) (totalPtgSize + 22));
            LittleEndian.putShort(bArr, i + 4, (short) getRow());
            LittleEndian.putShort(bArr, i + 6, getColumn());
            LittleEndian.putShort(bArr, i + 8, getXFIndex());
            if (Double.isNaN(getValue()) && (bArr2 = this.value_data) != null) {
                System.arraycopy(bArr2, 0, bArr, i + 10, bArr2.length);
            } else {
                LittleEndian.putDouble(bArr, i + 10, this.field_4_value);
            }
            LittleEndian.putShort(bArr, i + 18, getOptions());
            LittleEndian.putInt(bArr, i + 20, 0);
            LittleEndian.putShort(bArr, i + 24, getExpressionLength());
            serializePtgs(bArr, i + 26);
        } else {
            byte[] bArr3 = this.all_data;
            System.arraycopy(bArr3, 0, bArr, i, bArr3.length);
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        if (this.field_8_parsed_expr != null) {
            return getTotalPtgSize() + 26;
        }
        return this.all_data.length;
    }

    private int getTotalPtgSize() {
        List parsedExpression = getParsedExpression();
        int size = 0;
        for (int i = 0; i < parsedExpression.size(); i++) {
            size += ((Ptg) parsedExpression.get(i)).getSize();
        }
        return size;
    }

    private void serializePtgs(byte[] bArr, int i) {
        for (int i2 = 0; i2 < this.field_8_parsed_expr.size(); i2++) {
            Ptg ptg = (Ptg) this.field_8_parsed_expr.get(i2);
            ptg.writeBytes(bArr, i);
            i += ptg.getSize();
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

    @Override // java.lang.Comparable
    public int compareTo(Object obj) {
        CellValueRecordInterface cellValueRecordInterface = (CellValueRecordInterface) obj;
        if (getRow() == cellValueRecordInterface.getRow() && getColumn() == cellValueRecordInterface.getColumn()) {
            return 0;
        }
        if (getRow() < cellValueRecordInterface.getRow()) {
            return -1;
        }
        if (getRow() > cellValueRecordInterface.getRow()) {
            return 1;
        }
        return (getColumn() >= cellValueRecordInterface.getColumn() && getColumn() > cellValueRecordInterface.getColumn()) ? 1 : -1;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CellValueRecordInterface)) {
            return false;
        }
        CellValueRecordInterface cellValueRecordInterface = (CellValueRecordInterface) obj;
        return getRow() == cellValueRecordInterface.getRow() && getColumn() == cellValueRecordInterface.getColumn();
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[FORMULA]\n");
        stringBuffer.append("    .row       = ").append(Integer.toHexString(getRow())).append("\n");
        stringBuffer.append("    .column    = ").append(Integer.toHexString(getColumn())).append("\n");
        stringBuffer.append("    .xf              = ").append(Integer.toHexString(getXFIndex())).append("\n");
        if (Double.isNaN(getValue()) && this.value_data != null) {
            stringBuffer.append("    .value (NaN)     = ").append(HexDump.dump(this.value_data, 0L, 0)).append("\n");
        } else {
            stringBuffer.append("    .value           = ").append(getValue()).append("\n");
        }
        stringBuffer.append("    .options         = ").append((int) getOptions()).append("\n");
        stringBuffer.append("    .zero            = ").append(this.field_6_zero).append("\n");
        stringBuffer.append("    .expressionlength= ").append((int) getExpressionLength()).append("\n");
        if (this.field_8_parsed_expr != null) {
            stringBuffer.append("    .numptgsinarray  = ").append(this.field_8_parsed_expr.size()).append("\n");
            for (int i = 0; i < this.field_8_parsed_expr.size(); i++) {
                stringBuffer.append("Formula ").append(i).append("=").append(this.field_8_parsed_expr.get(i).toString()).append("\n").append(((Ptg) this.field_8_parsed_expr.get(i)).toDebugString()).append("\n");
            }
        } else {
            stringBuffer.append("Formula full data \n").append(HexDump.dump(this.all_data, 0L, 0));
        }
        stringBuffer.append("[/FORMULA]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        FormulaRecord formulaRecord = new FormulaRecord();
        formulaRecord.field_1_row = this.field_1_row;
        formulaRecord.field_2_column = this.field_2_column;
        formulaRecord.field_3_xf = this.field_3_xf;
        formulaRecord.field_4_value = this.field_4_value;
        formulaRecord.field_5_options = this.field_5_options;
        formulaRecord.field_6_zero = this.field_6_zero;
        formulaRecord.field_7_expression_len = this.field_7_expression_len;
        formulaRecord.field_8_parsed_expr = new Stack();
        Stack stack = this.field_8_parsed_expr;
        int size = stack != null ? stack.size() : 0;
        for (int i = 0; i < size; i++) {
            formulaRecord.field_8_parsed_expr.add(i, (Ptg) ((Ptg) this.field_8_parsed_expr.get(i)).clone());
        }
        formulaRecord.value_data = this.value_data;
        formulaRecord.all_data = this.all_data;
        return formulaRecord;
    }
}
