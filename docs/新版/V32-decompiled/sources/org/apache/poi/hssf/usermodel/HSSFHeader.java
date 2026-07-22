package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.HeaderRecord;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFHeader {
    String center;
    HeaderRecord headerRecord;
    String left;
    String right;

    public static String date() {
        return "&D";
    }

    public static String file() {
        return "&F";
    }

    public static String numPages() {
        return "&N";
    }

    public static String page() {
        return "&P";
    }

    public static String tab() {
        return "&A";
    }

    public static String time() {
        return "&T";
    }

    protected HSSFHeader(HeaderRecord headerRecord) {
        this.headerRecord = headerRecord;
        String header = headerRecord.getHeader();
        while (header != null && header.length() > 1) {
            int length = header.length();
            char cCharAt = header.substring(1, 2).charAt(0);
            if (cCharAt == 'C') {
                length = header.indexOf("&L") >= 0 ? Math.min(length, header.indexOf("&L")) : length;
                length = header.indexOf("&R") >= 0 ? Math.min(length, header.indexOf("&R")) : length;
                this.center = header.substring(2, length);
                header = header.substring(length);
            } else if (cCharAt == 'L') {
                length = header.indexOf("&C") >= 0 ? Math.min(length, header.indexOf("&C")) : length;
                length = header.indexOf("&R") >= 0 ? Math.min(length, header.indexOf("&R")) : length;
                this.left = header.substring(2, length);
                header = header.substring(length);
            } else if (cCharAt != 'R') {
                header = null;
            } else {
                length = header.indexOf("&C") >= 0 ? Math.min(length, header.indexOf("&C")) : length;
                length = header.indexOf("&L") >= 0 ? Math.min(length, header.indexOf("&L")) : length;
                this.right = header.substring(2, length);
                header = header.substring(length);
            }
        }
    }

    public String getLeft() {
        return this.left;
    }

    public void setLeft(String str) {
        this.left = str;
        createHeaderString();
    }

    public String getCenter() {
        return this.center;
    }

    public void setCenter(String str) {
        this.center = str;
        createHeaderString();
    }

    public String getRight() {
        return this.right;
    }

    public void setRight(String str) {
        this.right = str;
        createHeaderString();
    }

    private void createHeaderString() {
        HeaderRecord headerRecord = this.headerRecord;
        StringBuffer stringBufferAppend = new StringBuffer().append("&C");
        String str = this.center;
        if (str == null) {
            str = "";
        }
        StringBuffer stringBufferAppend2 = stringBufferAppend.append(str).append("&L");
        String str2 = this.left;
        if (str2 == null) {
            str2 = "";
        }
        StringBuffer stringBufferAppend3 = stringBufferAppend2.append(str2).append("&R");
        String str3 = this.right;
        headerRecord.setHeader(stringBufferAppend3.append(str3 != null ? str3 : "").toString());
        HeaderRecord headerRecord2 = this.headerRecord;
        headerRecord2.setHeaderLength((byte) headerRecord2.getHeader().length());
    }

    public static String fontSize(short s) {
        return new StringBuffer().append("&").append((int) s).toString();
    }

    public static String font(String str, String str2) {
        return new StringBuffer().append("&\"").append(str).append(",").append(str2).append("\"").toString();
    }
}
