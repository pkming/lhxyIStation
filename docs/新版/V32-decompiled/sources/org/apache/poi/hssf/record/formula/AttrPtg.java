package org.apache.poi.hssf.record.formula;

import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class AttrPtg extends OperationPtg {
    private static final int SIZE = 4;
    public static final byte sid = 25;
    private byte field_1_options;
    private short field_2_data;
    private BitField semiVolatile = new BitField(1);
    private BitField optiIf = new BitField(2);
    private BitField optiChoose = new BitField(4);
    private BitField optGoto = new BitField(8);
    private BitField sum = new BitField(16);
    private BitField baxcel = new BitField(32);
    private BitField space = new BitField(64);

    @Override // org.apache.poi.hssf.record.formula.OperationPtg, org.apache.poi.hssf.record.formula.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 32;
    }

    @Override // org.apache.poi.hssf.record.formula.OperationPtg
    public int getNumberOfOperands() {
        return 1;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public int getSize() {
        return 4;
    }

    @Override // org.apache.poi.hssf.record.formula.OperationPtg
    public int getType() {
        return -1;
    }

    public AttrPtg() {
    }

    public AttrPtg(byte[] bArr, int i) {
        int i2 = i + 1;
        this.field_1_options = bArr[i2 + 0];
        this.field_2_data = LittleEndian.getShort(bArr, i2 + 1);
    }

    public void setOptions(byte b) {
        this.field_1_options = b;
    }

    public byte getOptions() {
        return this.field_1_options;
    }

    public boolean isSemiVolatile() {
        return this.semiVolatile.isSet(getOptions());
    }

    public boolean isOptimizedIf() {
        return this.optiIf.isSet(getOptions());
    }

    public boolean isOptimizedChoose() {
        return this.optiChoose.isSet(getOptions());
    }

    public boolean isGoto() {
        return this.optGoto.isSet(getOptions());
    }

    public boolean isSum() {
        return this.sum.isSet(getOptions());
    }

    public void setSum(boolean z) {
        this.field_1_options = this.sum.setByteBoolean(this.field_1_options, z);
    }

    public void setOptimizedIf(boolean z) {
        this.field_1_options = this.optiIf.setByteBoolean(this.field_1_options, z);
    }

    public void setGoto(boolean z) {
        this.field_1_options = this.optGoto.setByteBoolean(this.field_1_options, z);
    }

    public boolean isBaxcel() {
        return this.baxcel.isSet(getOptions());
    }

    public boolean isSpace() {
        return this.space.isSet(getOptions());
    }

    public void setData(short s) {
        this.field_2_data = s;
    }

    public short getData() {
        return this.field_2_data;
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("AttrPtg\n");
        stringBuffer.append("options=").append((int) this.field_1_options).append("\n");
        stringBuffer.append("data   =").append((int) this.field_2_data).append("\n");
        stringBuffer.append("semi   =").append(isSemiVolatile()).append("\n");
        stringBuffer.append("optimif=").append(isOptimizedIf()).append("\n");
        stringBuffer.append("optchos=").append(isOptimizedChoose()).append("\n");
        stringBuffer.append("isGoto =").append(isGoto()).append("\n");
        stringBuffer.append("isSum  =").append(isSum()).append("\n");
        stringBuffer.append("isBaxce=").append(isBaxcel()).append("\n");
        stringBuffer.append("isSpace=").append(isSpace()).append("\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public void writeBytes(byte[] bArr, int i) {
        bArr[i] = sid;
        bArr[i + 1] = this.field_1_options;
        LittleEndian.putShort(bArr, i + 2, this.field_2_data);
    }

    @Override // org.apache.poi.hssf.record.formula.OperationPtg
    public String toFormulaString(String[] strArr) {
        if (this.space.isSet(this.field_1_options)) {
            return strArr[0];
        }
        if (this.optiIf.isSet(this.field_1_options)) {
            return new StringBuffer().append(toFormulaString((Workbook) null)).append("(").append(strArr[0]).append(")").toString();
        }
        if (this.optGoto.isSet(this.field_1_options)) {
            return new StringBuffer().append(toFormulaString((Workbook) null)).append(strArr[0]).toString();
        }
        return new StringBuffer().append(toFormulaString((Workbook) null)).append("(").append(strArr[0]).append(")").toString();
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public String toFormulaString(Workbook workbook) {
        return this.semiVolatile.isSet(this.field_1_options) ? "ATTR(semiVolatile)" : this.optiIf.isSet(this.field_1_options) ? "IF" : this.optiChoose.isSet(this.field_1_options) ? "CHOOSE" : this.optGoto.isSet(this.field_1_options) ? "" : this.sum.isSet(this.field_1_options) ? "SUM" : this.baxcel.isSet(this.field_1_options) ? "ATTR(baxcel)" : this.space.isSet(this.field_1_options) ? "" : "UNKNOWN ATTRIBUTE";
    }

    @Override // org.apache.poi.hssf.record.formula.Ptg
    public Object clone() {
        AttrPtg attrPtg = new AttrPtg();
        attrPtg.field_1_options = this.field_1_options;
        attrPtg.field_2_data = this.field_2_data;
        return attrPtg;
    }
}
