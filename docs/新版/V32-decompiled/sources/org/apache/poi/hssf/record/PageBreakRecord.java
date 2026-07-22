package org.apache.poi.hssf.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.util.LittleEndian;
import org.apache.tools.ant.taskdefs.SQLExec;

/* JADX INFO: loaded from: classes3.dex */
public class PageBreakRecord extends Record {
    public static final short HORIZONTAL_SID = 27;
    public static final short VERTICAL_SID = 26;
    private Map BreakMap;
    private List breaks;
    private short numBreaks;
    public short sid;

    public class Break {
        public short main;
        public short subFrom;
        public short subTo;

        public Break(short s, short s2, short s3) {
            this.main = s;
            this.subFrom = s2;
            this.subTo = s3;
        }
    }

    public PageBreakRecord() {
    }

    public PageBreakRecord(short s) {
        this.sid = s;
    }

    public PageBreakRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.sid = s;
    }

    public PageBreakRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.sid = s;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        short s2 = LittleEndian.getShort(bArr, i + 0);
        setNumBreaks(s2);
        int i2 = 2;
        for (int i3 = 0; i3 < s2; i3++) {
            addBreak((short) (LittleEndian.getShort(bArr, i2 + i) - 1), LittleEndian.getShort(bArr, i2 + 2 + i), LittleEndian.getShort(bArr, i2 + 4 + i));
            i2 += 6;
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return this.sid;
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        int recordSize = getRecordSize();
        LittleEndian.putShort(bArr, i + 0, getSid());
        LittleEndian.putShort(bArr, i + 2, (short) (recordSize - 4));
        LittleEndian.putShort(bArr, i + 4, getNumBreaks());
        Iterator breaksIterator = getBreaksIterator();
        int i2 = 6;
        while (breaksIterator.hasNext()) {
            Break r3 = (Break) breaksIterator.next();
            LittleEndian.putShort(bArr, i + i2, (short) (r3.main + 1));
            int i3 = i2 + 2;
            LittleEndian.putShort(bArr, i + i3, r3.subFrom);
            int i4 = i3 + 2;
            LittleEndian.putShort(bArr, i + i4, r3.subTo);
            i2 = i4 + 2;
        }
        return recordSize;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 27 && s != 26) {
            throw new RecordFormatException(new StringBuffer().append("NOT A HorizontalPageBreak or VerticalPageBreak RECORD!! ").append((int) s).toString());
        }
    }

    public short getNumBreaks() {
        List list = this.breaks;
        return list != null ? (short) list.size() : this.numBreaks;
    }

    public void setNumBreaks(short s) {
        this.numBreaks = s;
    }

    public Iterator getBreaksIterator() {
        List list = this.breaks;
        if (list == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        return list.iterator();
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        String str;
        String str2;
        StringBuffer stringBuffer = new StringBuffer();
        if (getSid() != 27 && getSid() != 26) {
            return new StringBuffer().append("[INVALIDPAGEBREAK]\n     .sid =").append((int) getSid()).append("[INVALIDPAGEBREAK]").toString();
        }
        short sid = getSid();
        String str3 = SQLExec.DelimiterType.ROW;
        if (sid == 27) {
            str = "HORIZONTALPAGEBREAK";
            str2 = "col";
        } else {
            str = "VERTICALPAGEBREAK";
            str3 = "column";
            str2 = SQLExec.DelimiterType.ROW;
        }
        stringBuffer.append(new StringBuffer().append("[").append(str).append("]").toString()).append("\n");
        stringBuffer.append("     .sid        =").append((int) getSid()).append("\n");
        stringBuffer.append("     .numbreaks =").append((int) getNumBreaks()).append("\n");
        Iterator breaksIterator = getBreaksIterator();
        for (int i = 0; i < getNumBreaks(); i++) {
            Break r9 = (Break) breaksIterator.next();
            stringBuffer.append("     .").append(str3).append(" (zero-based) =").append((int) r9.main).append("\n");
            stringBuffer.append("     .").append(str2).append("From    =").append((int) r9.subFrom).append("\n");
            stringBuffer.append("     .").append(str2).append("To      =").append((int) r9.subTo).append("\n");
        }
        stringBuffer.append(new StringBuffer().append("[").append(str).append("]").toString()).append("\n");
        return stringBuffer.toString();
    }

    public void addBreak(short s, short s2, short s3) {
        if (this.breaks == null) {
            this.breaks = new ArrayList(getNumBreaks() + 10);
            this.BreakMap = new HashMap();
        }
        Integer num = new Integer(s);
        Break r1 = (Break) this.BreakMap.get(num);
        if (r1 != null) {
            r1.main = s;
            r1.subFrom = s2;
            r1.subTo = s3;
        } else {
            r1 = new Break(s, s2, s3);
            this.breaks.add(r1);
        }
        this.BreakMap.put(num, r1);
    }

    public void removeBreak(short s) {
        Integer num = new Integer(s);
        this.breaks.remove((Break) this.BreakMap.get(num));
        this.BreakMap.remove(num);
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return (getNumBreaks() * 6) + 6;
    }

    public Break getBreak(short s) {
        return (Break) this.BreakMap.get(new Integer(s));
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        PageBreakRecord pageBreakRecord = new PageBreakRecord(getSid());
        Iterator breaksIterator = getBreaksIterator();
        while (breaksIterator.hasNext()) {
            Break r2 = (Break) breaksIterator.next();
            pageBreakRecord.addBreak(r2.main, r2.subFrom, r2.subTo);
        }
        return pageBreakRecord;
    }
}
