package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class WindowTwoRecord extends Record {
    public static final short sid = 574;
    private BitField arabic;
    private BitField defaultHeader;
    private BitField displayFormulas;
    private BitField displayGridlines;
    private BitField displayGuts;
    private BitField displayRowColHeadings;
    private BitField displayZeros;
    private short field_1_options;
    private short field_2_top_row;
    private short field_3_left_col;
    private int field_4_header_color;
    private short field_5_page_break_zoom;
    private short field_6_normal_zoom;
    private int field_7_reserved;
    private BitField freezePanes;
    private BitField freezePanesNoSplit;
    private BitField paged;
    private BitField savedInPageBreakPreview;
    private BitField selected;

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 22;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 574;
    }

    public WindowTwoRecord() {
        this.displayFormulas = new BitField(1);
        this.displayGridlines = new BitField(2);
        this.displayRowColHeadings = new BitField(4);
        this.freezePanes = new BitField(8);
        this.displayZeros = new BitField(16);
        this.defaultHeader = new BitField(32);
        this.arabic = new BitField(64);
        this.displayGuts = new BitField(128);
        this.freezePanesNoSplit = new BitField(256);
        this.selected = new BitField(512);
        this.paged = new BitField(1024);
        this.savedInPageBreakPreview = new BitField(2048);
    }

    public WindowTwoRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.displayFormulas = new BitField(1);
        this.displayGridlines = new BitField(2);
        this.displayRowColHeadings = new BitField(4);
        this.freezePanes = new BitField(8);
        this.displayZeros = new BitField(16);
        this.defaultHeader = new BitField(32);
        this.arabic = new BitField(64);
        this.displayGuts = new BitField(128);
        this.freezePanesNoSplit = new BitField(256);
        this.selected = new BitField(512);
        this.paged = new BitField(1024);
        this.savedInPageBreakPreview = new BitField(2048);
    }

    public WindowTwoRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.displayFormulas = new BitField(1);
        this.displayGridlines = new BitField(2);
        this.displayRowColHeadings = new BitField(4);
        this.freezePanes = new BitField(8);
        this.displayZeros = new BitField(16);
        this.defaultHeader = new BitField(32);
        this.arabic = new BitField(64);
        this.displayGuts = new BitField(128);
        this.freezePanesNoSplit = new BitField(256);
        this.selected = new BitField(512);
        this.paged = new BitField(1024);
        this.savedInPageBreakPreview = new BitField(2048);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 574) {
            throw new RecordFormatException("NOT A valid WindowTwo RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_options = LittleEndian.getShort(bArr, i + 0);
        this.field_2_top_row = LittleEndian.getShort(bArr, i + 2);
        this.field_3_left_col = LittleEndian.getShort(bArr, i + 4);
        this.field_4_header_color = LittleEndian.getInt(bArr, i + 6);
        if (s > 10) {
            this.field_5_page_break_zoom = LittleEndian.getShort(bArr, i + 10);
            this.field_6_normal_zoom = LittleEndian.getShort(bArr, i + 12);
        }
        if (s > 14) {
            this.field_7_reserved = LittleEndian.getInt(bArr, i + 14);
        }
    }

    public void setOptions(short s) {
        this.field_1_options = s;
    }

    public void setDisplayFormulas(boolean z) {
        this.field_1_options = this.displayFormulas.setShortBoolean(this.field_1_options, z);
    }

    public void setDisplayGridlines(boolean z) {
        this.field_1_options = this.displayGridlines.setShortBoolean(this.field_1_options, z);
    }

    public void setDisplayRowColHeadings(boolean z) {
        this.field_1_options = this.displayRowColHeadings.setShortBoolean(this.field_1_options, z);
    }

    public void setFreezePanes(boolean z) {
        this.field_1_options = this.freezePanes.setShortBoolean(this.field_1_options, z);
    }

    public void setDisplayZeros(boolean z) {
        this.field_1_options = this.displayZeros.setShortBoolean(this.field_1_options, z);
    }

    public void setDefaultHeader(boolean z) {
        this.field_1_options = this.defaultHeader.setShortBoolean(this.field_1_options, z);
    }

    public void setArabic(boolean z) {
        this.field_1_options = this.arabic.setShortBoolean(this.field_1_options, z);
    }

    public void setDisplayGuts(boolean z) {
        this.field_1_options = this.displayGuts.setShortBoolean(this.field_1_options, z);
    }

    public void setFreezePanesNoSplit(boolean z) {
        this.field_1_options = this.freezePanesNoSplit.setShortBoolean(this.field_1_options, z);
    }

    public void setSelected(boolean z) {
        this.field_1_options = this.selected.setShortBoolean(this.field_1_options, z);
    }

    public void setPaged(boolean z) {
        this.field_1_options = this.paged.setShortBoolean(this.field_1_options, z);
    }

    public void setSavedInPageBreakPreview(boolean z) {
        this.field_1_options = this.savedInPageBreakPreview.setShortBoolean(this.field_1_options, z);
    }

    public void setTopRow(short s) {
        this.field_2_top_row = s;
    }

    public void setLeftCol(short s) {
        this.field_3_left_col = s;
    }

    public void setHeaderColor(int i) {
        this.field_4_header_color = i;
    }

    public void setPageBreakZoom(short s) {
        this.field_5_page_break_zoom = s;
    }

    public void setNormalZoom(short s) {
        this.field_6_normal_zoom = s;
    }

    public void setReserved(int i) {
        this.field_7_reserved = i;
    }

    public short getOptions() {
        return this.field_1_options;
    }

    public boolean getDisplayFormulas() {
        return this.displayFormulas.isSet(this.field_1_options);
    }

    public boolean getDisplayGridlines() {
        return this.displayGridlines.isSet(this.field_1_options);
    }

    public boolean getDisplayRowColHeadings() {
        return this.displayRowColHeadings.isSet(this.field_1_options);
    }

    public boolean getFreezePanes() {
        return this.freezePanes.isSet(this.field_1_options);
    }

    public boolean getDisplayZeros() {
        return this.displayZeros.isSet(this.field_1_options);
    }

    public boolean getDefaultHeader() {
        return this.defaultHeader.isSet(this.field_1_options);
    }

    public boolean getArabic() {
        return this.arabic.isSet(this.field_1_options);
    }

    public boolean getDisplayGuts() {
        return this.displayGuts.isSet(this.field_1_options);
    }

    public boolean getFreezePanesNoSplit() {
        return this.freezePanesNoSplit.isSet(this.field_1_options);
    }

    public boolean getSelected() {
        return this.selected.isSet(this.field_1_options);
    }

    public boolean getPaged() {
        return this.paged.isSet(this.field_1_options);
    }

    public boolean getSavedInPageBreakPreview() {
        return this.savedInPageBreakPreview.isSet(this.field_1_options);
    }

    public short getTopRow() {
        return this.field_2_top_row;
    }

    public short getLeftCol() {
        return this.field_3_left_col;
    }

    public int getHeaderColor() {
        return this.field_4_header_color;
    }

    public short getPageBreakZoom() {
        return this.field_5_page_break_zoom;
    }

    public short getNormalZoom() {
        return this.field_6_normal_zoom;
    }

    public int getReserved() {
        return this.field_7_reserved;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[WINDOW2]\n");
        stringBuffer.append("    .options        = ").append(Integer.toHexString(getOptions())).append("\n");
        stringBuffer.append("       .dispformulas= ").append(getDisplayFormulas()).append("\n");
        stringBuffer.append("       .dispgridlins= ").append(getDisplayGridlines()).append("\n");
        stringBuffer.append("       .disprcheadin= ").append(getDisplayRowColHeadings()).append("\n");
        stringBuffer.append("       .freezepanes = ").append(getFreezePanes()).append("\n");
        stringBuffer.append("       .displayzeros= ").append(getDisplayZeros()).append("\n");
        stringBuffer.append("       .defaultheadr= ").append(getDefaultHeader()).append("\n");
        stringBuffer.append("       .arabic      = ").append(getArabic()).append("\n");
        stringBuffer.append("       .displayguts = ").append(getDisplayGuts()).append("\n");
        stringBuffer.append("       .frzpnsnosplt= ").append(getFreezePanesNoSplit()).append("\n");
        stringBuffer.append("       .selected    = ").append(getSelected()).append("\n");
        stringBuffer.append("       .paged       = ").append(getPaged()).append("\n");
        stringBuffer.append("       .svdinpgbrkpv= ").append(getSavedInPageBreakPreview()).append("\n");
        stringBuffer.append("    .toprow         = ").append(Integer.toHexString(getTopRow())).append("\n");
        stringBuffer.append("    .leftcol        = ").append(Integer.toHexString(getLeftCol())).append("\n");
        stringBuffer.append("    .headercolor    = ").append(Integer.toHexString(getHeaderColor())).append("\n");
        stringBuffer.append("    .pagebreakzoom  = ").append(Integer.toHexString(getPageBreakZoom())).append("\n");
        stringBuffer.append("    .normalzoom     = ").append(Integer.toHexString(getNormalZoom())).append("\n");
        stringBuffer.append("    .reserved       = ").append(Integer.toHexString(getReserved())).append("\n");
        stringBuffer.append("[/WINDOW2]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 574);
        LittleEndian.putShort(bArr, i + 2, (short) 18);
        LittleEndian.putShort(bArr, i + 4, getOptions());
        LittleEndian.putShort(bArr, i + 6, getTopRow());
        LittleEndian.putShort(bArr, i + 8, getLeftCol());
        LittleEndian.putInt(bArr, i + 10, getHeaderColor());
        LittleEndian.putShort(bArr, i + 14, getPageBreakZoom());
        LittleEndian.putShort(bArr, i + 16, getNormalZoom());
        LittleEndian.putInt(bArr, i + 18, getReserved());
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        WindowTwoRecord windowTwoRecord = new WindowTwoRecord();
        windowTwoRecord.field_1_options = this.field_1_options;
        windowTwoRecord.field_2_top_row = this.field_2_top_row;
        windowTwoRecord.field_3_left_col = this.field_3_left_col;
        windowTwoRecord.field_4_header_color = this.field_4_header_color;
        windowTwoRecord.field_5_page_break_zoom = this.field_5_page_break_zoom;
        windowTwoRecord.field_6_normal_zoom = this.field_6_normal_zoom;
        windowTwoRecord.field_7_reserved = this.field_7_reserved;
        return windowTwoRecord;
    }
}
