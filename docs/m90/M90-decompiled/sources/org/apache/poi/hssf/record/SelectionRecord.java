package org.apache.poi.hssf.record;

import java.util.ArrayList;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class SelectionRecord extends Record {
    public static final short sid = 29;
    private byte field_1_pane;
    private int field_2_row_active_cell;
    private short field_3_col_active_cell;
    private short field_4_ref_active_cell;
    private short field_5_num_refs;
    private ArrayList field_6_refs;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 19;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 29;
    }

    public SelectionRecord() {
    }

    public SelectionRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public SelectionRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 29) {
            throw new RecordFormatException("NOT A valid Selection RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_pane = bArr[i + 0];
        this.field_2_row_active_cell = LittleEndian.getUShort(bArr, i + 1);
        this.field_3_col_active_cell = LittleEndian.getShort(bArr, i + 3);
        this.field_4_ref_active_cell = LittleEndian.getShort(bArr, i + 5);
        this.field_5_num_refs = LittleEndian.getShort(bArr, i + 7);
    }

    public void setPane(byte b) {
        this.field_1_pane = b;
    }

    public void setActiveCellRow(int i) {
        this.field_2_row_active_cell = i;
    }

    public void setActiveCellCol(short s) {
        this.field_3_col_active_cell = s;
    }

    public void setActiveCellRef(short s) {
        this.field_4_ref_active_cell = s;
    }

    public void setNumRefs(short s) {
        this.field_5_num_refs = s;
    }

    public byte getPane() {
        return this.field_1_pane;
    }

    public int getActiveCellRow() {
        return this.field_2_row_active_cell;
    }

    public short getActiveCellCol() {
        return this.field_3_col_active_cell;
    }

    public short getActiveCellRef() {
        return this.field_4_ref_active_cell;
    }

    public short getNumRefs() {
        return this.field_5_num_refs;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[SELECTION]\n");
        stringBuffer.append("    .pane            = ").append(Integer.toHexString(getPane())).append("\n");
        stringBuffer.append("    .activecellrow   = ").append(Integer.toHexString(getActiveCellRow())).append("\n");
        stringBuffer.append("    .activecellcol   = ").append(Integer.toHexString(getActiveCellCol())).append("\n");
        stringBuffer.append("    .activecellref   = ").append(Integer.toHexString(getActiveCellRef())).append("\n");
        stringBuffer.append("    .numrefs         = ").append(Integer.toHexString(getNumRefs())).append("\n");
        stringBuffer.append("[/SELECTION]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 29);
        LittleEndian.putShort(bArr, i + 2, (short) 15);
        bArr[i + 4] = getPane();
        LittleEndian.putShort(bArr, i + 5, (short) getActiveCellRow());
        LittleEndian.putShort(bArr, i + 7, getActiveCellCol());
        LittleEndian.putShort(bArr, i + 9, getActiveCellRef());
        LittleEndian.putShort(bArr, i + 11, (short) 1);
        LittleEndian.putShort(bArr, i + 13, (short) getActiveCellRow());
        LittleEndian.putShort(bArr, i + 15, (short) getActiveCellRow());
        bArr[i + 17] = (byte) getActiveCellCol();
        bArr[i + 18] = (byte) getActiveCellCol();
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        SelectionRecord selectionRecord = new SelectionRecord();
        selectionRecord.field_1_pane = this.field_1_pane;
        selectionRecord.field_2_row_active_cell = this.field_2_row_active_cell;
        selectionRecord.field_3_col_active_cell = this.field_3_col_active_cell;
        selectionRecord.field_4_ref_active_cell = this.field_4_ref_active_cell;
        selectionRecord.field_5_num_refs = this.field_5_num_refs;
        selectionRecord.field_6_refs = this.field_6_refs;
        return selectionRecord;
    }
}
