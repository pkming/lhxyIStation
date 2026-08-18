package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
public class WindowOneRecord extends Record {
    public static final short sid = 61;
    private short field_1_h_hold;
    private short field_2_v_hold;
    private short field_3_width;
    private short field_4_height;
    private short field_5_options;
    private short field_6_selected_tab;
    private short field_7_displayed_tab;
    private short field_8_num_selected_tabs;
    private short field_9_tab_width_ratio;
    private static final BitField hidden = new BitField(1);
    private static final BitField iconic = new BitField(2);
    private static final BitField reserved = new BitField(4);
    private static final BitField hscroll = new BitField(8);
    private static final BitField vscroll = new BitField(16);
    private static final BitField tabs = new BitField(32);

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return 22;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 61;
    }

    public WindowOneRecord() {
    }

    public WindowOneRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public WindowOneRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 61) {
            throw new RecordFormatException("NOT A WINDOW1 RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_h_hold = LittleEndian.getShort(bArr, i + 0);
        this.field_2_v_hold = LittleEndian.getShort(bArr, i + 2);
        this.field_3_width = LittleEndian.getShort(bArr, i + 4);
        this.field_4_height = LittleEndian.getShort(bArr, i + 6);
        this.field_5_options = LittleEndian.getShort(bArr, i + 8);
        this.field_6_selected_tab = LittleEndian.getShort(bArr, i + 10);
        this.field_7_displayed_tab = LittleEndian.getShort(bArr, i + 12);
        this.field_8_num_selected_tabs = LittleEndian.getShort(bArr, i + 14);
        this.field_9_tab_width_ratio = LittleEndian.getShort(bArr, i + 16);
    }

    public void setHorizontalHold(short s) {
        this.field_1_h_hold = s;
    }

    public void setVerticalHold(short s) {
        this.field_2_v_hold = s;
    }

    public void setWidth(short s) {
        this.field_3_width = s;
    }

    public void setHeight(short s) {
        this.field_4_height = s;
    }

    public void setOptions(short s) {
        this.field_5_options = s;
    }

    public void setHidden(boolean z) {
        this.field_5_options = hidden.setShortBoolean(this.field_5_options, z);
    }

    public void setIconic(boolean z) {
        this.field_5_options = iconic.setShortBoolean(this.field_5_options, z);
    }

    public void setDisplayHorizonalScrollbar(boolean z) {
        this.field_5_options = hscroll.setShortBoolean(this.field_5_options, z);
    }

    public void setDisplayVerticalScrollbar(boolean z) {
        this.field_5_options = vscroll.setShortBoolean(this.field_5_options, z);
    }

    public void setDisplayTabs(boolean z) {
        this.field_5_options = tabs.setShortBoolean(this.field_5_options, z);
    }

    public void setSelectedTab(short s) {
        this.field_6_selected_tab = s;
    }

    public void setDisplayedTab(short s) {
        this.field_7_displayed_tab = s;
    }

    public void setNumSelectedTabs(short s) {
        this.field_8_num_selected_tabs = s;
    }

    public void setTabWidthRatio(short s) {
        this.field_9_tab_width_ratio = s;
    }

    public short getHorizontalHold() {
        return this.field_1_h_hold;
    }

    public short getVerticalHold() {
        return this.field_2_v_hold;
    }

    public short getWidth() {
        return this.field_3_width;
    }

    public short getHeight() {
        return this.field_4_height;
    }

    public short getOptions() {
        return this.field_5_options;
    }

    public boolean getHidden() {
        return hidden.isSet(this.field_5_options);
    }

    public boolean getIconic() {
        return iconic.isSet(this.field_5_options);
    }

    public boolean getDisplayHorizontalScrollbar() {
        return hscroll.isSet(this.field_5_options);
    }

    public boolean getDisplayVerticalScrollbar() {
        return vscroll.isSet(this.field_5_options);
    }

    public boolean getDisplayTabs() {
        return tabs.isSet(this.field_5_options);
    }

    public short getSelectedTab() {
        return this.field_6_selected_tab;
    }

    public short getDisplayedTab() {
        return this.field_7_displayed_tab;
    }

    public short getNumSelectedTabs() {
        return this.field_8_num_selected_tabs;
    }

    public short getTabWidthRatio() {
        return this.field_9_tab_width_ratio;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[WINDOW1]\n");
        stringBuffer.append("    .h_hold          = ").append(Integer.toHexString(getHorizontalHold())).append("\n");
        stringBuffer.append("    .v_hold          = ").append(Integer.toHexString(getVerticalHold())).append("\n");
        stringBuffer.append("    .width           = ").append(Integer.toHexString(getWidth())).append("\n");
        stringBuffer.append("    .height          = ").append(Integer.toHexString(getHeight())).append("\n");
        stringBuffer.append("    .options         = ").append(Integer.toHexString(getOptions())).append("\n");
        stringBuffer.append("        .hidden      = ").append(getHidden()).append("\n");
        stringBuffer.append("        .iconic      = ").append(getIconic()).append("\n");
        stringBuffer.append("        .hscroll     = ").append(getDisplayHorizontalScrollbar()).append("\n");
        stringBuffer.append("        .vscroll     = ").append(getDisplayVerticalScrollbar()).append("\n");
        stringBuffer.append("        .tabs        = ").append(getDisplayTabs()).append("\n");
        stringBuffer.append("    .selectedtab     = ").append(Integer.toHexString(getSelectedTab())).append("\n");
        stringBuffer.append("    .displayedtab    = ").append(Integer.toHexString(getDisplayedTab())).append("\n");
        stringBuffer.append("    .numselectedtabs = ").append(Integer.toHexString(getNumSelectedTabs())).append("\n");
        stringBuffer.append("    .tabwidthratio   = ").append(Integer.toHexString(getTabWidthRatio())).append("\n");
        stringBuffer.append("[/WINDOW1]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 61);
        LittleEndian.putShort(bArr, i + 2, (short) 18);
        LittleEndian.putShort(bArr, i + 4, getHorizontalHold());
        LittleEndian.putShort(bArr, i + 6, getVerticalHold());
        LittleEndian.putShort(bArr, i + 8, getWidth());
        LittleEndian.putShort(bArr, i + 10, getHeight());
        LittleEndian.putShort(bArr, i + 12, getOptions());
        LittleEndian.putShort(bArr, i + 14, getSelectedTab());
        LittleEndian.putShort(bArr, i + 16, getDisplayedTab());
        LittleEndian.putShort(bArr, i + 18, getNumSelectedTabs());
        LittleEndian.putShort(bArr, i + 20, getTabWidthRatio());
        return getRecordSize();
    }
}
