package org.apache.poi.hssf.usermodel;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFRichTextString implements Comparable {
    public static final short NO_FONT = -1;
    SortedMap formattingRuns;
    String string;

    @Override // java.lang.Comparable
    public int compareTo(Object obj) {
        return 0;
    }

    public HSSFRichTextString() {
        this("");
    }

    public HSSFRichTextString(String str) {
        TreeMap treeMap = new TreeMap();
        this.formattingRuns = treeMap;
        this.string = str;
        treeMap.put(new Integer(0), new Short((short) -1));
    }

    public void applyFont(int i, int i2, short s) {
        if (i > i2) {
            throw new IllegalArgumentException("Start index must be less than end index.");
        }
        if (i < 0 || i2 > length()) {
            throw new IllegalArgumentException("Start and end index not in range.");
        }
        if (i == i2) {
            return;
        }
        Integer num = new Integer(i);
        Integer num2 = new Integer(i2);
        short fontAtIndex = i2 != length() ? getFontAtIndex(i2) : (short) -1;
        this.formattingRuns.subMap(num, num2).clear();
        this.formattingRuns.put(num, new Short(s));
        if (i2 == length() || s == fontAtIndex) {
            return;
        }
        this.formattingRuns.put(num2, new Short(fontAtIndex));
    }

    public void applyFont(int i, int i2, HSSFFont hSSFFont) {
        applyFont(i, i2, hSSFFont.getIndex());
    }

    public void applyFont(HSSFFont hSSFFont) {
        applyFont(0, this.string.length(), hSSFFont);
    }

    public String getString() {
        return this.string;
    }

    public int length() {
        return this.string.length();
    }

    public short getFontAtIndex(int i) {
        if (i < 0 || i >= this.string.length()) {
            throw new ArrayIndexOutOfBoundsException(new StringBuffer().append("Font index ").append(i).append(" out of bounds of string").toString());
        }
        SortedMap sortedMapHeadMap = this.formattingRuns.headMap(new Integer(i + 1));
        if (sortedMapHeadMap.isEmpty()) {
            throw new IllegalStateException("Should not reach here.  No font found.");
        }
        return ((Short) sortedMapHeadMap.get(sortedMapHeadMap.lastKey())).shortValue();
    }

    public int numFormattingRuns() {
        return this.formattingRuns.size();
    }

    public int getIndexOfFormattingRun(int i) {
        return ((Integer) ((Map.Entry[]) this.formattingRuns.entrySet().toArray(new Map.Entry[this.formattingRuns.size()]))[i].getKey()).intValue();
    }

    public short getFontOfFormattingRun(int i) {
        return ((Short) ((Map.Entry[]) this.formattingRuns.entrySet().toArray(new Map.Entry[this.formattingRuns.size()]))[i].getValue()).shortValue();
    }

    public String toString() {
        return this.string;
    }

    public void applyFont(short s) {
        applyFont(0, this.string.length(), s);
    }
}
