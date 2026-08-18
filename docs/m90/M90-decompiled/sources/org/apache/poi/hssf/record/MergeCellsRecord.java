package org.apache.poi.hssf.record;

import java.util.ArrayList;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class MergeCellsRecord extends Record {
    public static final short sid = 229;
    private ArrayList field_2_regions;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public void setNumAreas(short s) {
    }

    public MergeCellsRecord() {
    }

    public MergeCellsRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public MergeCellsRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        short s2 = LittleEndian.getShort(bArr, i + 0);
        this.field_2_regions = new ArrayList(s2 + 10);
        int i2 = 2;
        for (int i3 = 0; i3 < s2; i3++) {
            MergedRegion mergedRegion = new MergedRegion(LittleEndian.getShort(bArr, i2 + i), LittleEndian.getShort(bArr, i2 + 2 + i), LittleEndian.getShort(bArr, i2 + 4 + i), LittleEndian.getShort(bArr, i2 + 6 + i));
            i2 += 8;
            this.field_2_regions.add(mergedRegion);
        }
    }

    public short getNumAreas() {
        ArrayList arrayList = this.field_2_regions;
        if (arrayList == null) {
            return (short) 0;
        }
        return (short) arrayList.size();
    }

    public int addArea(int i, short s, int i2, short s2) {
        if (this.field_2_regions == null) {
            this.field_2_regions = new ArrayList(10);
        }
        this.field_2_regions.add(new MergedRegion(i, i2, s, s2));
        return this.field_2_regions.size() - 1;
    }

    public void removeAreaAt(int i) {
        this.field_2_regions.remove(i);
    }

    public MergedRegion getAreaAt(int i) {
        return (MergedRegion) this.field_2_regions.get(i);
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return (this.field_2_regions.size() * 8) + 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        int recordSize = getRecordSize();
        LittleEndian.putShort(bArr, i + 0, sid);
        LittleEndian.putShort(bArr, i + 2, (short) (recordSize - 4));
        LittleEndian.putShort(bArr, i + 4, getNumAreas());
        int i2 = 6;
        for (int i3 = 0; i3 < getNumAreas(); i3++) {
            MergedRegion areaAt = getAreaAt(i3);
            LittleEndian.putShort(bArr, i + i2, (short) areaAt.row_from);
            int i4 = i2 + 2;
            LittleEndian.putShort(bArr, i + i4, (short) areaAt.row_to);
            int i5 = i4 + 2;
            LittleEndian.putShort(bArr, i + i5, areaAt.col_from);
            int i6 = i5 + 2;
            LittleEndian.putShort(bArr, i + i6, areaAt.col_to);
            i2 = i6 + 2;
        }
        return recordSize;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[MERGEDCELLS]").append("\n");
        stringBuffer.append("     .sid        =").append(FTPReply.ENTERING_EPSV_MODE).append("\n");
        stringBuffer.append("     .numregions =").append((int) getNumAreas()).append("\n");
        for (int i = 0; i < getNumAreas(); i++) {
            MergedRegion mergedRegion = (MergedRegion) this.field_2_regions.get(i);
            stringBuffer.append("     .rowfrom    =").append(mergedRegion.row_from).append("\n");
            stringBuffer.append("     .colfrom    =").append((int) mergedRegion.col_from).append("\n");
            stringBuffer.append("     .rowto      =").append(mergedRegion.row_to).append("\n");
            stringBuffer.append("     .colto      =").append((int) mergedRegion.col_to).append("\n");
        }
        stringBuffer.append("[MERGEDCELLS]").append("\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 229) {
            throw new RecordFormatException(new StringBuffer().append("NOT A MERGEDCELLS RECORD!! ").append((int) s).toString());
        }
    }

    public class MergedRegion {
        public short col_from;
        public short col_to;
        public int row_from;
        public int row_to;

        public MergedRegion(int i, int i2, short s, short s2) {
            this.row_from = i;
            this.row_to = i2;
            this.col_from = s;
            this.col_to = s2;
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        MergeCellsRecord mergeCellsRecord = new MergeCellsRecord();
        mergeCellsRecord.field_2_regions = new ArrayList();
        for (MergedRegion mergedRegion : this.field_2_regions) {
            mergeCellsRecord.addArea(mergedRegion.row_from, mergedRegion.col_from, mergedRegion.row_to, mergedRegion.col_to);
        }
        return mergeCellsRecord;
    }
}
