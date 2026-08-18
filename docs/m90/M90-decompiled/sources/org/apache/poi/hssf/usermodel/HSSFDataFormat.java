package org.apache.poi.hssf.usermodel;

import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.hssf.record.FormatRecord;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFDataFormat {
    private static Vector builtinFormats;
    private Vector formats = new Vector();
    private boolean movedBuiltins = false;
    private Workbook workbook;

    public HSSFDataFormat(Workbook workbook) {
        this.workbook = workbook;
        if (builtinFormats == null) {
            populateBuiltinFormats();
        }
        for (FormatRecord formatRecord : workbook.getFormats()) {
            if (this.formats.size() < formatRecord.getIndexCode() + 1) {
                this.formats.setSize(formatRecord.getIndexCode() + 1);
            }
            this.formats.set(formatRecord.getIndexCode(), formatRecord.getFormatString());
        }
    }

    private static synchronized void populateBuiltinFormats() {
        Vector vector = new Vector();
        builtinFormats = vector;
        vector.add(0, "General");
        builtinFormats.add(1, "0");
        builtinFormats.add(2, "0.00");
        builtinFormats.add(3, "#,##0");
        builtinFormats.add(4, "#,##0.00");
        builtinFormats.add(5, "($#,##0_);($#,##0)");
        builtinFormats.add(6, "($#,##0_);[Red]($#,##0)");
        builtinFormats.add(7, "($#,##0.00);($#,##0.00)");
        builtinFormats.add(8, "($#,##0.00_);[Red]($#,##0.00)");
        builtinFormats.add(9, "0%");
        builtinFormats.add(10, "0.00%");
        builtinFormats.add(11, "0.00E+00");
        builtinFormats.add(12, "# ?/?");
        builtinFormats.add(13, "# ??/??");
        builtinFormats.add(14, "m/d/yy");
        builtinFormats.add(15, "d-mmm-yy");
        builtinFormats.add(16, "d-mmm");
        builtinFormats.add(17, "mmm-yy");
        builtinFormats.add(18, "h:mm AM/PM");
        builtinFormats.add(19, "h:mm:ss AM/PM");
        builtinFormats.add(20, "h:mm");
        builtinFormats.add(21, "h:mm:ss");
        builtinFormats.add(22, "m/d/yy h:mm");
        builtinFormats.add(23, "0x17");
        builtinFormats.add(24, "0x18");
        builtinFormats.add(25, "0x19");
        builtinFormats.add(26, "0x1a");
        builtinFormats.add(27, "0x1b");
        builtinFormats.add(28, "0x1c");
        builtinFormats.add(29, "0x1d");
        builtinFormats.add(30, "0x1e");
        builtinFormats.add(31, "0x1f");
        builtinFormats.add(32, "0x20");
        builtinFormats.add(33, "0x21");
        builtinFormats.add(34, "0x22");
        builtinFormats.add(35, "0x23");
        builtinFormats.add(36, "0x24");
        builtinFormats.add(37, "(#,##0_);(#,##0)");
        builtinFormats.add(38, "(#,##0_);[Red](#,##0)");
        builtinFormats.add(39, "(#,##0.00_);(#,##0.00)");
        builtinFormats.add(40, "(#,##0.00_);[Red](#,##0.00)");
        builtinFormats.add(41, "_(*#,##0_);_(*(#,##0);_(* \"-\"_);_(@_)");
        builtinFormats.add(42, "_($*#,##0_);_($*(#,##0);_($* \"-\"_);_(@_)");
        builtinFormats.add(43, "_(*#,##0.00_);_(*(#,##0.00);_(*\"-\"??_);_(@_)");
        builtinFormats.add(44, "_($*#,##0.00_);_($*(#,##0.00);_($*\"-\"??_);_(@_)");
        builtinFormats.add(45, "mm:ss");
        builtinFormats.add(46, "[h]:mm:ss");
        builtinFormats.add(47, "mm:ss.0");
        builtinFormats.add(48, "##0.0E+0");
        builtinFormats.add(49, "@");
    }

    public static List getBuiltinFormats() {
        if (builtinFormats == null) {
            populateBuiltinFormats();
        }
        return builtinFormats;
    }

    public static short getBuiltinFormat(String str) {
        if (str.toUpperCase().equals("TEXT")) {
            str = "@";
        }
        if (builtinFormats == null) {
            populateBuiltinFormats();
        }
        for (short s = 0; s <= 49; s = (short) (s + 1)) {
            String str2 = (String) builtinFormats.get(s);
            if (str2 != null && str2.equals(str)) {
                return s;
            }
        }
        return (short) -1;
    }

    public short getFormat(String str) {
        if (str.toUpperCase().equals("TEXT")) {
            str = "@";
        }
        if (!this.movedBuiltins) {
            ListIterator listIterator = builtinFormats.listIterator();
            while (listIterator.hasNext()) {
                this.formats.add(listIterator.nextIndex(), listIterator.next());
            }
            this.movedBuiltins = true;
        }
        ListIterator listIterator2 = this.formats.listIterator();
        while (listIterator2.hasNext()) {
            int iNextIndex = listIterator2.nextIndex();
            if (str.equals(listIterator2.next())) {
                return (short) iNextIndex;
            }
        }
        short format = this.workbook.getFormat(str, true);
        if (this.formats.size() <= format) {
            this.formats.setSize(format + 1);
        }
        this.formats.add(format, str);
        return format;
    }

    public String getFormat(short s) {
        if (this.movedBuiltins) {
            return (String) this.formats.get(s);
        }
        return (String) ((builtinFormats.size() <= s || builtinFormats.get(s) == null) ? this.formats : builtinFormats).get(s);
    }

    public static String getBuiltinFormat(short s) {
        if (builtinFormats == null) {
            populateBuiltinFormats();
        }
        return (String) builtinFormats.get(s);
    }

    public static int getNumberOfBuiltinBuiltinFormats() {
        if (builtinFormats == null) {
            populateBuiltinFormats();
        }
        return builtinFormats.size();
    }
}
