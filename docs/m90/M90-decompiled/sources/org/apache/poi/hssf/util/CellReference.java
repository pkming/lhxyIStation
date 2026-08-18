package org.apache.poi.hssf.util;

/* JADX INFO: loaded from: classes3.dex */
public class CellReference {
    private int col;
    private boolean colAbs;
    private int row;
    private boolean rowAbs;
    private String sheetName;

    public CellReference(String str) {
        String[] strArrSeparateRefParts = separateRefParts(str);
        this.sheetName = strArrSeparateRefParts[0];
        String strSubstring = strArrSeparateRefParts[1];
        if (strSubstring.charAt(0) == '$') {
            this.colAbs = true;
            strSubstring = strSubstring.substring(1);
        }
        this.col = convertColStringToNum(strSubstring);
        String strSubstring2 = strArrSeparateRefParts[2];
        if (strSubstring2.charAt(0) == '$') {
            this.rowAbs = true;
            strSubstring2 = strSubstring2.substring(1);
        }
        this.row = Integer.parseInt(strSubstring2) - 1;
    }

    public CellReference(int i, int i2) {
        this(i, i2, false, false);
    }

    public CellReference(int i, int i2, boolean z, boolean z2) {
        this.row = i;
        this.col = i2;
        this.rowAbs = z;
        this.colAbs = z2;
    }

    public int getRow() {
        return this.row;
    }

    public short getCol() {
        return (short) this.col;
    }

    public boolean isRowAbsolute() {
        return this.rowAbs;
    }

    public boolean isColAbsolute() {
        return this.colAbs;
    }

    public String getSheetName() {
        return this.sheetName;
    }

    private int convertColStringToNum(String str) {
        str.length();
        int i = 0;
        int numericValue = 0;
        for (int length = str.length() - 1; length > -1; length--) {
            numericValue += i == 0 ? Character.getNumericValue(str.charAt(length)) - 9 : (Character.getNumericValue(r3) - 9) * i * 26;
            i++;
        }
        return numericValue - 1;
    }

    private String[] separateRefParts(String str) {
        String[] strArr = new String[3];
        int iIndexOf = str.indexOf("!");
        if (iIndexOf != -1) {
            strArr[0] = str.substring(0, iIndexOf);
        }
        int i = iIndexOf + 1;
        str.length();
        char[] charArray = str.toCharArray();
        int i2 = charArray[i] == '$' ? i + 1 : i;
        while (i2 < charArray.length && !Character.isDigit(charArray[i2]) && charArray[i2] != '$') {
            i2++;
        }
        strArr[1] = str.substring(i, i2);
        strArr[2] = str.substring(i2);
        return strArr;
    }

    private static String convertNumToColString(int i) {
        int i2 = i % 26;
        int i3 = i / 26;
        char c = (char) (i2 + 65);
        char c2 = (char) (i3 + 64);
        if (i3 == 0) {
            return new StringBuffer().append("").append(c).toString();
        }
        return new StringBuffer().append("").append(c2).append("").append(c).toString();
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.colAbs ? "$" : "");
        stringBuffer.append(convertNumToColString(this.col));
        stringBuffer.append(this.rowAbs ? "$" : "");
        stringBuffer.append(this.row + 1);
        return stringBuffer.toString();
    }
}
