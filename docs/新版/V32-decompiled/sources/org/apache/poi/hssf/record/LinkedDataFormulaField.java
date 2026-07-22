package org.apache.poi.hssf.record;

import java.util.Iterator;
import java.util.Stack;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class LinkedDataFormulaField implements CustomField {
    Stack formulaTokens = new Stack();

    @Override // org.apache.poi.hssf.record.CustomField
    public int getSize() {
        Iterator it = this.formulaTokens.iterator();
        int size = 0;
        while (it.hasNext()) {
            size += ((Ptg) it.next()).getSize();
        }
        return size + 2;
    }

    @Override // org.apache.poi.hssf.record.CustomField
    public int fillField(byte[] bArr, short s, int i) {
        short s2 = LittleEndian.getShort(bArr, i);
        this.formulaTokens = getParsedExpressionTokens(bArr, s, i + 2);
        return s2 + 2;
    }

    @Override // org.apache.poi.hssf.record.CustomField
    public void toString(StringBuffer stringBuffer) {
        for (int i = 0; i < this.formulaTokens.size(); i++) {
            stringBuffer.append("Formula ").append(i).append("=").append(this.formulaTokens.get(i).toString()).append("\n").append(((Ptg) this.formulaTokens.get(i)).toDebugString()).append("\n");
        }
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        toString(stringBuffer);
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.CustomField
    public int serializeField(int i, byte[] bArr) {
        int size = getSize();
        LittleEndian.putShort(bArr, i, (short) (size - 2));
        int size2 = i + 2;
        for (Ptg ptg : this.formulaTokens) {
            ptg.writeBytes(bArr, size2);
            size2 += ptg.getSize();
        }
        return size;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException unused) {
            return null;
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

    public void setFormulaTokens(Stack stack) {
        this.formulaTokens = (Stack) stack.clone();
    }

    public Stack getFormulaTokens() {
        return (Stack) this.formulaTokens.clone();
    }
}
