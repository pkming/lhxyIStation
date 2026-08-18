package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.FooterRecord;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFFooter {
    String center;
    FooterRecord footerRecord;
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

    protected HSSFFooter(FooterRecord footerRecord) {
        this.footerRecord = footerRecord;
        String footer = footerRecord.getFooter();
        while (footer != null && footer.length() > 1) {
            int length = footer.length();
            char cCharAt = footer.substring(1, 2).charAt(0);
            if (cCharAt == 'C') {
                length = footer.indexOf("&L") >= 0 ? Math.min(length, footer.indexOf("&L")) : length;
                length = footer.indexOf("&R") >= 0 ? Math.min(length, footer.indexOf("&R")) : length;
                this.center = footer.substring(2, length);
                footer = footer.substring(length);
            } else if (cCharAt == 'L') {
                length = footer.indexOf("&C") >= 0 ? Math.min(length, footer.indexOf("&C")) : length;
                length = footer.indexOf("&R") >= 0 ? Math.min(length, footer.indexOf("&R")) : length;
                this.left = footer.substring(2, length);
                footer = footer.substring(length);
            } else if (cCharAt != 'R') {
                footer = null;
            } else {
                length = footer.indexOf("&C") >= 0 ? Math.min(length, footer.indexOf("&C")) : length;
                length = footer.indexOf("&L") >= 0 ? Math.min(length, footer.indexOf("&L")) : length;
                this.right = footer.substring(2, length);
                footer = footer.substring(length);
            }
        }
    }

    public String getLeft() {
        return this.left;
    }

    public void setLeft(String str) {
        this.left = str;
        createFooterString();
    }

    public String getCenter() {
        return this.center;
    }

    public void setCenter(String str) {
        this.center = str;
        createFooterString();
    }

    public String getRight() {
        return this.right;
    }

    public void setRight(String str) {
        this.right = str;
        createFooterString();
    }

    private void createFooterString() {
        FooterRecord footerRecord = this.footerRecord;
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
        footerRecord.setFooter(stringBufferAppend3.append(str3 != null ? str3 : "").toString());
        FooterRecord footerRecord2 = this.footerRecord;
        footerRecord2.setFooterLength((byte) footerRecord2.getFooter().length());
    }

    public static String fontSize(short s) {
        return new StringBuffer().append("&").append((int) s).toString();
    }

    public static String font(String str, String str2) {
        return new StringBuffer().append("&\"").append(str).append(",").append(str2).append("\"").toString();
    }
}
