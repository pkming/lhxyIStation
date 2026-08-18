package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class PrintSetupRecord extends Record {
    public static final short sid = 161;
    private double field_10_footermargin;
    private short field_11_copies;
    private short field_1_paper_size;
    private short field_2_scale;
    private short field_3_page_start;
    private short field_4_fit_width;
    private short field_5_fit_height;
    private short field_6_options;
    private short field_7_hresolution;
    private short field_8_vresolution;
    private double field_9_headermargin;
    private static final BitField lefttoright = new BitField(1);
    private static final BitField landscape = new BitField(2);
    private static final BitField validsettings = new BitField(4);
    private static final BitField nocolor = new BitField(8);
    private static final BitField draft = new BitField(16);
    private static final BitField notes = new BitField(32);
    private static final BitField noOrientation = new BitField(64);
    private static final BitField usepage = new BitField(128);

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 38;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 161;
    }

    public PrintSetupRecord() {
    }

    public PrintSetupRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public PrintSetupRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 161) {
            throw new RecordFormatException("NOT A valid PrintSetup record RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_paper_size = LittleEndian.getShort(bArr, i + 0);
        this.field_2_scale = LittleEndian.getShort(bArr, i + 2);
        this.field_3_page_start = LittleEndian.getShort(bArr, i + 4);
        this.field_4_fit_width = LittleEndian.getShort(bArr, i + 6);
        this.field_5_fit_height = LittleEndian.getShort(bArr, i + 8);
        this.field_6_options = LittleEndian.getShort(bArr, i + 10);
        this.field_7_hresolution = LittleEndian.getShort(bArr, i + 12);
        this.field_8_vresolution = LittleEndian.getShort(bArr, i + 14);
        this.field_9_headermargin = LittleEndian.getDouble(bArr, i + 16);
        this.field_10_footermargin = LittleEndian.getDouble(bArr, i + 24);
        this.field_11_copies = LittleEndian.getShort(bArr, i + 32);
    }

    public void setPaperSize(short s) {
        this.field_1_paper_size = s;
    }

    public void setScale(short s) {
        this.field_2_scale = s;
    }

    public void setPageStart(short s) {
        this.field_3_page_start = s;
    }

    public void setFitWidth(short s) {
        this.field_4_fit_width = s;
    }

    public void setFitHeight(short s) {
        this.field_5_fit_height = s;
    }

    public void setOptions(short s) {
        this.field_6_options = s;
    }

    public void setLeftToRight(boolean z) {
        this.field_6_options = lefttoright.setShortBoolean(this.field_6_options, z);
    }

    public void setLandscape(boolean z) {
        this.field_6_options = landscape.setShortBoolean(this.field_6_options, z);
    }

    public void setValidSettings(boolean z) {
        this.field_6_options = validsettings.setShortBoolean(this.field_6_options, z);
    }

    public void setNoColor(boolean z) {
        this.field_6_options = nocolor.setShortBoolean(this.field_6_options, z);
    }

    public void setDraft(boolean z) {
        this.field_6_options = draft.setShortBoolean(this.field_6_options, z);
    }

    public void setNotes(boolean z) {
        this.field_6_options = notes.setShortBoolean(this.field_6_options, z);
    }

    public void setNoOrientation(boolean z) {
        this.field_6_options = noOrientation.setShortBoolean(this.field_6_options, z);
    }

    public void setUsePage(boolean z) {
        this.field_6_options = usepage.setShortBoolean(this.field_6_options, z);
    }

    public void setHResolution(short s) {
        this.field_7_hresolution = s;
    }

    public void setVResolution(short s) {
        this.field_8_vresolution = s;
    }

    public void setHeaderMargin(double d) {
        this.field_9_headermargin = d;
    }

    public void setFooterMargin(double d) {
        this.field_10_footermargin = d;
    }

    public void setCopies(short s) {
        this.field_11_copies = s;
    }

    public short getPaperSize() {
        return this.field_1_paper_size;
    }

    public short getScale() {
        return this.field_2_scale;
    }

    public short getPageStart() {
        return this.field_3_page_start;
    }

    public short getFitWidth() {
        return this.field_4_fit_width;
    }

    public short getFitHeight() {
        return this.field_5_fit_height;
    }

    public short getOptions() {
        return this.field_6_options;
    }

    public boolean getLeftToRight() {
        return lefttoright.isSet(this.field_6_options);
    }

    public boolean getLandscape() {
        return landscape.isSet(this.field_6_options);
    }

    public boolean getValidSettings() {
        return validsettings.isSet(this.field_6_options);
    }

    public boolean getNoColor() {
        return nocolor.isSet(this.field_6_options);
    }

    public boolean getDraft() {
        return draft.isSet(this.field_6_options);
    }

    public boolean getNotes() {
        return notes.isSet(this.field_6_options);
    }

    public boolean getNoOrientation() {
        return noOrientation.isSet(this.field_6_options);
    }

    public boolean getUsePage() {
        return usepage.isSet(this.field_6_options);
    }

    public short getHResolution() {
        return this.field_7_hresolution;
    }

    public short getVResolution() {
        return this.field_8_vresolution;
    }

    public double getHeaderMargin() {
        return this.field_9_headermargin;
    }

    public double getFooterMargin() {
        return this.field_10_footermargin;
    }

    public short getCopies() {
        return this.field_11_copies;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[PRINTSETUP]\n");
        stringBuffer.append("    .papersize      = ").append((int) getPaperSize()).append("\n");
        stringBuffer.append("    .scale          = ").append((int) getScale()).append("\n");
        stringBuffer.append("    .pagestart      = ").append((int) getPageStart()).append("\n");
        stringBuffer.append("    .fitwidth       = ").append((int) getFitWidth()).append("\n");
        stringBuffer.append("    .fitheight      = ").append((int) getFitHeight()).append("\n");
        stringBuffer.append("    .options        = ").append((int) getOptions()).append("\n");
        stringBuffer.append("        .ltor       = ").append(getLeftToRight()).append("\n");
        stringBuffer.append("        .landscape  = ").append(getLandscape()).append("\n");
        stringBuffer.append("        .valid      = ").append(getValidSettings()).append("\n");
        stringBuffer.append("        .mono       = ").append(getNoColor()).append("\n");
        stringBuffer.append("        .draft      = ").append(getDraft()).append("\n");
        stringBuffer.append("        .notes      = ").append(getNotes()).append("\n");
        stringBuffer.append("        .noOrientat = ").append(getNoOrientation()).append("\n");
        stringBuffer.append("        .usepage    = ").append(getUsePage()).append("\n");
        stringBuffer.append("    .hresolution    = ").append((int) getHResolution()).append("\n");
        stringBuffer.append("    .vresolution    = ").append((int) getVResolution()).append("\n");
        stringBuffer.append("    .headermargin   = ").append(getHeaderMargin()).append("\n");
        stringBuffer.append("    .footermargin   = ").append(getFooterMargin()).append("\n");
        stringBuffer.append("    .copies         = ").append((int) getCopies()).append("\n");
        stringBuffer.append("[/PRINTSETUP]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 161);
        LittleEndian.putShort(bArr, i + 2, (short) 34);
        LittleEndian.putShort(bArr, i + 4, getPaperSize());
        LittleEndian.putShort(bArr, i + 6, getScale());
        LittleEndian.putShort(bArr, i + 8, getPageStart());
        LittleEndian.putShort(bArr, i + 10, getFitWidth());
        LittleEndian.putShort(bArr, i + 12, getFitHeight());
        LittleEndian.putShort(bArr, i + 14, getOptions());
        LittleEndian.putShort(bArr, i + 16, getHResolution());
        LittleEndian.putShort(bArr, i + 18, getVResolution());
        LittleEndian.putDouble(bArr, i + 20, getHeaderMargin());
        LittleEndian.putDouble(bArr, i + 28, getFooterMargin());
        LittleEndian.putShort(bArr, i + 36, getCopies());
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        PrintSetupRecord printSetupRecord = new PrintSetupRecord();
        printSetupRecord.field_1_paper_size = this.field_1_paper_size;
        printSetupRecord.field_2_scale = this.field_2_scale;
        printSetupRecord.field_3_page_start = this.field_3_page_start;
        printSetupRecord.field_4_fit_width = this.field_4_fit_width;
        printSetupRecord.field_5_fit_height = this.field_5_fit_height;
        printSetupRecord.field_6_options = this.field_6_options;
        printSetupRecord.field_7_hresolution = this.field_7_hresolution;
        printSetupRecord.field_8_vresolution = this.field_8_vresolution;
        printSetupRecord.field_9_headermargin = this.field_9_headermargin;
        printSetupRecord.field_10_footermargin = this.field_10_footermargin;
        printSetupRecord.field_11_copies = this.field_11_copies;
        return printSetupRecord;
    }
}
