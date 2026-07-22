package org.apache.poi.hssf.util;

/* JADX INFO: loaded from: classes3.dex */
public class AreaReference {
    private CellReference[] cells;
    private int dim;

    public AreaReference(String str) {
        String[] strArrSeperateAreaRefs = seperateAreaRefs(str);
        int length = strArrSeperateAreaRefs.length;
        this.dim = length;
        this.cells = new CellReference[length];
        for (int i = 0; i < this.dim; i++) {
            this.cells[i] = new CellReference(strArrSeperateAreaRefs[i]);
        }
    }

    public int getDim() {
        return this.dim;
    }

    public CellReference[] getCells() {
        return this.cells;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < this.dim; i++) {
            stringBuffer.append(':');
            stringBuffer.append(this.cells[i].toString());
        }
        stringBuffer.deleteCharAt(0);
        return stringBuffer.toString();
    }

    private String[] seperateAreaRefs(String str) {
        str.length();
        int iIndexOf = str.indexOf(58, 0);
        if (iIndexOf == -1) {
            return new String[]{str};
        }
        int iIndexOf2 = str.indexOf("!") + 1;
        return new String[]{new StringBuffer().append(str.substring(0, iIndexOf2)).append(str.substring(iIndexOf2, iIndexOf)).toString(), new StringBuffer().append(str.substring(0, iIndexOf2)).append(str.substring(iIndexOf + 1)).toString()};
    }
}
