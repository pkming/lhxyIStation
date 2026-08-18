package org.apache.poi.hssf.usermodel;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: classes3.dex */
public class FontDetails {
    private Map charWidths = new HashMap();
    private String fontName;
    private int height;

    public FontDetails(String str, int i) {
        this.fontName = str;
        this.height = i;
    }

    public String getFontName() {
        return this.fontName;
    }

    public int getHeight() {
        return this.height;
    }

    public void addChar(char c, int i) {
        this.charWidths.put(new Character(c), new Integer(i));
    }

    public int getCharWidth(char c) {
        Integer num = (Integer) this.charWidths.get(new Character(c));
        if (num == null && c != 'W') {
            return getCharWidth('W');
        }
        return num.intValue();
    }

    public void addChars(char[] cArr, int[] iArr) {
        for (int i = 0; i < cArr.length; i++) {
            this.charWidths.put(new Character(cArr[i]), new Integer(iArr[i]));
        }
    }

    public static FontDetails create(String str, Properties properties) {
        String property = properties.getProperty(new StringBuffer().append("font.").append(str).append(".height").toString());
        String property2 = properties.getProperty(new StringBuffer().append("font.").append(str).append(".widths").toString());
        String property3 = properties.getProperty(new StringBuffer().append("font.").append(str).append(".characters").toString());
        FontDetails fontDetails = new FontDetails(str, Integer.parseInt(property));
        String[] strArrSplit = split(property3, ",", -1);
        String[] strArrSplit2 = split(property2, ",", -1);
        if (strArrSplit.length != strArrSplit2.length) {
            throw new RuntimeException(new StringBuffer().append("Number of characters does not number of widths for font ").append(str).toString());
        }
        for (int i = 0; i < strArrSplit2.length; i++) {
            if (strArrSplit[i].length() != 0) {
                fontDetails.addChar(strArrSplit[i].charAt(0), Integer.parseInt(strArrSplit2[i]));
            }
        }
        return fontDetails;
    }

    public int getStringWidth(String str) {
        int charWidth = 0;
        for (int i = 0; i < str.length(); i++) {
            charWidth += getCharWidth(str.charAt(i));
        }
        return charWidth;
    }

    private static String[] split(String str, String str2, int i) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, str2);
        int iCountTokens = stringTokenizer.countTokens();
        if (i != -1 && iCountTokens > i) {
            iCountTokens = i;
        }
        String[] strArr = new String[iCountTokens];
        int i2 = 0;
        while (true) {
            if (!stringTokenizer.hasMoreTokens()) {
                break;
            }
            if (i != -1 && i2 == iCountTokens - 1) {
                StringBuffer stringBuffer = new StringBuffer((str.length() * (iCountTokens - i2)) / iCountTokens);
                while (stringTokenizer.hasMoreTokens()) {
                    stringBuffer.append(stringTokenizer.nextToken());
                    if (stringTokenizer.hasMoreTokens()) {
                        stringBuffer.append(str2);
                    }
                }
                strArr[i2] = stringBuffer.toString().trim();
            } else {
                strArr[i2] = stringTokenizer.nextToken().trim();
                i2++;
            }
        }
        return strArr;
    }
}
