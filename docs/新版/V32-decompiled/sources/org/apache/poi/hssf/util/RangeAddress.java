package org.apache.poi.hssf.util;

import android.text.format.DateFormat;

/* JADX INFO: loaded from: classes3.dex */
public class RangeAddress {
    static final int MAX_HEIGHT = 66666;
    static final char SO_FORMNAME_ENCLOSURE = '\'';
    static final int WRONG_POS = -1;
    String m_cellFrom;
    String m_cellTo;
    String m_sheetName;

    public RangeAddress(String str) {
        init(str);
    }

    public RangeAddress(int i, int i2, int i3, int i4) {
        init(new StringBuffer().append(numTo26Sys(i)).append(i2).append(":").append(numTo26Sys(i3)).append(i4).toString());
    }

    public String getAddress() {
        String string = this.m_sheetName != null ? new StringBuffer().append("").append(this.m_sheetName).append("!").toString() : "";
        if (this.m_cellFrom == null) {
            return string;
        }
        String string2 = new StringBuffer().append(string).append(this.m_cellFrom).toString();
        if (this.m_cellTo != null) {
            string2 = new StringBuffer().append(string2).append(":").append(this.m_cellTo).toString();
        }
        return string2;
    }

    public String getSheetName() {
        return this.m_sheetName;
    }

    public String getRange() {
        if (this.m_cellFrom == null) {
            return "";
        }
        String string = new StringBuffer().append("").append(this.m_cellFrom).toString();
        if (this.m_cellTo != null) {
            string = new StringBuffer().append(string).append(":").append(this.m_cellTo).toString();
        }
        return string;
    }

    public boolean isCellOk(String str) {
        return (str == null || getYPosition(str) == -1 || getXPosition(str) == -1) ? false : true;
    }

    public boolean isSheetNameOk() {
        return isSheetNameOk(this.m_sheetName);
    }

    private static boolean intern_isSheetNameOk(String str, boolean z) {
        for (int i = 0; i < str.length(); i++) {
            char cCharAt = str.charAt(i);
            if (!Character.isLetterOrDigit(cCharAt) && cCharAt != '_' && (!z || cCharAt != ' ')) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSheetNameOk(String str) {
        if (str == null || str.equals("")) {
            return true;
        }
        return intern_isSheetNameOk(str, true);
    }

    public String getFromCell() {
        return this.m_cellFrom;
    }

    public String getToCell() {
        return this.m_cellTo;
    }

    public int getWidth() {
        String str;
        if (this.m_cellFrom != null && (str = this.m_cellTo) != null) {
            int xPosition = getXPosition(str);
            int xPosition2 = getXPosition(this.m_cellFrom);
            if (xPosition != -1 && xPosition2 != -1) {
                return (xPosition - xPosition2) + 1;
            }
        }
        return 0;
    }

    public int getHeight() {
        String str;
        if (this.m_cellFrom != null && (str = this.m_cellTo) != null) {
            int yPosition = getYPosition(str);
            int yPosition2 = getYPosition(this.m_cellFrom);
            if (yPosition != -1 && yPosition2 != -1) {
                return (yPosition - yPosition2) + 1;
            }
        }
        return 0;
    }

    public void setSize(int i, int i2) {
        if (this.m_cellFrom == null) {
            this.m_cellFrom = "a1";
        }
        int xPosition = getXPosition(this.m_cellFrom);
        int yPosition = getYPosition(this.m_cellFrom);
        this.m_cellTo = numTo26Sys((xPosition + i) - 1);
        this.m_cellTo = new StringBuffer().append(this.m_cellTo).append(String.valueOf((yPosition + i2) - 1)).toString();
    }

    public boolean hasSheetName() {
        return this.m_sheetName != null;
    }

    public boolean hasRange() {
        return (this.m_cellFrom == null || this.m_cellTo == null) ? false : true;
    }

    public boolean hasCell() {
        return this.m_cellFrom != null;
    }

    private void init(String str) {
        String[] url = parseURL(removeString(removeString(str, "$"), "'"));
        this.m_sheetName = url[0];
        String str2 = url[1];
        this.m_cellFrom = str2;
        String str3 = url[2];
        this.m_cellTo = str3;
        if (str3 == null) {
            this.m_cellTo = str2;
        }
        this.m_cellTo = removeString(this.m_cellTo, ".");
    }

    private String[] parseURL(String str) {
        String[] strArr = new String[3];
        int iIndexOf = str.indexOf(58);
        if (iIndexOf >= 0) {
            String strSubstring = str.substring(0, iIndexOf);
            String strSubstring2 = str.substring(iIndexOf + 1);
            int iIndexOf2 = strSubstring.indexOf(33);
            if (iIndexOf2 >= 0) {
                strArr[0] = strSubstring.substring(0, iIndexOf2);
                strArr[1] = strSubstring.substring(iIndexOf2 + 1);
            } else {
                strArr[1] = strSubstring;
            }
            int iIndexOf3 = strSubstring2.indexOf(33);
            if (iIndexOf3 >= 0) {
                strArr[2] = strSubstring2.substring(iIndexOf3 + 1);
            } else {
                strArr[2] = strSubstring2;
            }
        } else {
            int iIndexOf4 = str.indexOf(33);
            if (iIndexOf4 >= 0) {
                strArr[0] = str.substring(0, iIndexOf4);
                strArr[1] = str.substring(iIndexOf4 + 1);
            } else {
                strArr[1] = str;
            }
        }
        return strArr;
    }

    public int getYPosition(String str) {
        String strTrim = str.trim();
        if (strTrim.length() == 0) {
            return -1;
        }
        try {
            int i = Integer.parseInt(getDigitPart(strTrim));
            if (i > MAX_HEIGHT) {
                return -1;
            }
            return i;
        } catch (Exception unused) {
            return -1;
        }
    }

    private static boolean isLetter(String str) {
        if (str.equals("")) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isLetter(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public int getXPosition(String str) {
        filter$(str);
        String charPart = getCharPart(str);
        if (isLetter(charPart) && (charPart.length() == 2 || charPart.length() == 1)) {
            return get26Sys(charPart);
        }
        return -1;
    }

    public String getDigitPart(String str) {
        int firstDigitPosition = getFirstDigitPosition(str);
        return firstDigitPosition >= 0 ? str.substring(firstDigitPosition) : "";
    }

    public String getCharPart(String str) {
        int firstDigitPosition = getFirstDigitPosition(str);
        return firstDigitPosition >= 0 ? str.substring(0, firstDigitPosition) : "";
    }

    private String filter$(String str) {
        String string = "";
        for (int i = 0; i < str.length(); i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt != '$') {
                string = new StringBuffer().append(string).append(cCharAt).toString();
            }
        }
        return string;
    }

    private int getFirstDigitPosition(String str) {
        if (str != null && str.trim().length() == 0) {
            return -1;
        }
        String strTrim = str.trim();
        int length = strTrim.length();
        for (int i = 0; i < length; i++) {
            if (Character.isDigit(strTrim.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public int get26Sys(String str) {
        if (str == "") {
            return -1;
        }
        int numericValue = 0;
        int i = 1;
        for (int length = str.length() - 1; length >= 0; length--) {
            numericValue += ((Character.getNumericValue(str.charAt(length)) - Character.getNumericValue(DateFormat.CAPITAL_AM_PM)) + 1) * i;
            i *= 26;
        }
        return numericValue;
    }

    public String numTo26Sys(int i) {
        String string = "";
        do {
            int i2 = i - 1;
            int i3 = (i2 % 26) + 65;
            i = i2 / 26;
            string = new StringBuffer().append((char) i3).append(string).toString();
        } while (i > 0);
        return string;
    }

    public String replaceString(String str, String str2, String str3) {
        StringBuffer stringBuffer = new StringBuffer(str);
        int iIndexOf = -1;
        while (true) {
            iIndexOf = stringBuffer.toString().indexOf(str2, iIndexOf);
            if (iIndexOf > -1) {
                stringBuffer.replace(iIndexOf, str2.length() + iIndexOf, str3);
            } else {
                return stringBuffer.toString();
            }
        }
    }

    public String removeString(String str, String str2) {
        return replaceString(str, str2, "");
    }
}
