package org.apache.poi.hssf.record;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.util.LittleEndian;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes3.dex */
public class PaletteRecord extends Record {
    public static final short FIRST_COLOR_INDEX = 8;
    public static final byte STANDARD_PALETTE_SIZE = 56;
    public static final short sid = 146;
    private short field_1_numcolors;
    private List field_2_colors;

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 146;
    }

    public PaletteRecord() {
    }

    public PaletteRecord(short s) {
        super(s, (short) 56, getDefaultData());
    }

    public PaletteRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public PaletteRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 146) {
            throw new RecordFormatException("NOT An Palette RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.field_1_numcolors = LittleEndian.getShort(bArr, i + 0);
        this.field_2_colors = new ArrayList(this.field_1_numcolors);
        for (int i2 = 0; i2 < this.field_1_numcolors; i2++) {
            int i3 = i + 2 + (i2 * 4);
            this.field_2_colors.add(new PColor(bArr[i3 + 0], bArr[i3 + 1], bArr[i3 + 2]));
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[PALETTE]\n");
        stringBuffer.append("  numcolors     = ").append((int) this.field_1_numcolors).append('\n');
        for (int i = 0; i < this.field_1_numcolors; i++) {
            PColor pColor = (PColor) this.field_2_colors.get(i);
            stringBuffer.append("* colornum      = ").append(i).append('\n');
            stringBuffer.append(pColor.toString());
            stringBuffer.append("/*colornum      = ").append(i).append('\n');
        }
        stringBuffer.append("[/PALETTE]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, (short) 146);
        LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        LittleEndian.putShort(bArr, i + 4, this.field_1_numcolors);
        for (int i2 = 0; i2 < this.field_1_numcolors; i2++) {
            ((PColor) this.field_2_colors.get(i2)).serialize(bArr, i + 6 + (i2 * 4));
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return (this.field_1_numcolors * 4) + 6;
    }

    public byte[] getColor(short s) {
        int i = s - 8;
        if (i < 0 || i >= this.field_2_colors.size()) {
            return null;
        }
        PColor pColor = (PColor) this.field_2_colors.get(i);
        return new byte[]{pColor.red, pColor.green, pColor.blue};
    }

    public void setColor(short s, byte b, byte b2, byte b3) {
        int i = s - 8;
        if (i < 0 || i >= 56) {
            return;
        }
        while (this.field_2_colors.size() <= i) {
            this.field_2_colors.add(new PColor((byte) 0, (byte) 0, (byte) 0));
        }
        this.field_2_colors.set(i, new PColor(b, b2, b3));
    }

    public static byte[] getDefaultData() {
        return new byte[]{STANDARD_PALETTE_SIZE, 0, 0, 0, 0, 0, -1, -1, -1, 0, -1, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, 0, -1, -1, 0, 0, -1, 0, -1, 0, 0, -1, -1, 0, -128, 0, 0, 0, 0, -128, 0, 0, 0, 0, -128, 0, -128, -128, 0, 0, -128, 0, -128, 0, 0, -128, -128, 0, -64, -64, -64, 0, -128, -128, -128, 0, -103, -103, -1, 0, -103, TarConstants.LF_CHR, 102, 0, -1, -1, -52, 0, -52, -1, -1, 0, 102, 0, 102, 0, -1, -128, -128, 0, 0, 102, -52, 0, -52, -52, -1, 0, 0, 0, -128, 0, -1, 0, -1, 0, -1, -1, 0, 0, 0, -1, -1, 0, -128, 0, -128, 0, -128, 0, 0, 0, 0, -128, -128, 0, 0, 0, -1, 0, 0, -52, -1, 0, -52, -1, -1, 0, -52, -1, -52, 0, -1, -1, -103, 0, -103, -52, -1, 0, -1, -103, -52, 0, -52, -103, -1, 0, -1, -52, -103, 0, TarConstants.LF_CHR, 102, -1, 0, TarConstants.LF_CHR, -52, -52, 0, -103, -52, 0, 0, -1, -52, 0, 0, -1, -103, 0, 0, -1, 102, 0, 0, 102, 102, -103, 0, -106, -106, -106, 0, 0, TarConstants.LF_CHR, 102, 0, TarConstants.LF_CHR, -103, 102, 0, 0, TarConstants.LF_CHR, 0, 0, TarConstants.LF_CHR, TarConstants.LF_CHR, 0, 0, -103, TarConstants.LF_CHR, 0, 0, -103, TarConstants.LF_CHR, 102, 0, TarConstants.LF_CHR, TarConstants.LF_CHR, -103, 0, TarConstants.LF_CHR, TarConstants.LF_CHR, TarConstants.LF_CHR, 0};
    }
}
