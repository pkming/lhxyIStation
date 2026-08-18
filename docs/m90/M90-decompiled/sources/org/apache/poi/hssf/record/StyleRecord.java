package org.apache.poi.hssf.record;

import de.innosystec.unrar.rarfile.BaseBlock;
import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes3.dex */
public class StyleRecord extends Record {
    public static final short STYLE_BUILT_IN = 1;
    public static final short STYLE_USER_DEFINED = 0;
    public static final short sid = 659;
    private BitField fHighByte;
    private short field_1_xf_index;
    private byte field_2_builtin_style;
    private short field_2_name_length;
    private byte field_3_outline_style_level;
    private byte field_3_string_options;
    private String field_4_name;

    private short setField(int i, int i2, int i3, int i4) {
        return (short) ((i & (~i3)) | ((i2 << i4) & i3));
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public StyleRecord() {
    }

    public StyleRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
    }

    public StyleRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
        if (s != 659) {
            throw new RecordFormatException("NOT A STYLE RECORD");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
        this.fHighByte = new BitField(1);
        this.field_1_xf_index = LittleEndian.getShort(bArr, i + 0);
        if (getType() == 1) {
            this.field_2_builtin_style = bArr[i + 2];
            this.field_3_outline_style_level = bArr[i + 3];
        } else if (getType() == 0) {
            this.field_2_name_length = LittleEndian.getShort(bArr, i + 2);
            byte b = bArr[i + 4];
            this.field_3_string_options = b;
            if (this.fHighByte.isSet(b)) {
                this.field_4_name = StringUtil.getFromUnicode(bArr, i + 5, this.field_2_name_length);
            } else {
                this.field_4_name = StringUtil.getFromCompressedUnicode(bArr, i + 5, this.field_2_name_length);
            }
        }
    }

    public void setIndex(short s) {
        this.field_1_xf_index = s;
    }

    public void setType(short s) {
        this.field_1_xf_index = setField(this.field_1_xf_index, s, 32768, 15);
    }

    public void setXFIndex(short s) {
        this.field_1_xf_index = setField(this.field_1_xf_index, s, 8191, 0);
    }

    public void setNameLength(byte b) {
        this.field_2_name_length = b;
    }

    public void setName(String str) {
        this.field_4_name = str;
    }

    public void setBuiltin(byte b) {
        this.field_2_builtin_style = b;
    }

    public void setOutlineStyleLevel(byte b) {
        this.field_3_outline_style_level = b;
    }

    public short getIndex() {
        return this.field_1_xf_index;
    }

    public short getType() {
        return (short) ((this.field_1_xf_index & BaseBlock.LONG_BLOCK) >> 15);
    }

    public short getXFIndex() {
        return (short) (this.field_1_xf_index & 8191);
    }

    public short getNameLength() {
        return this.field_2_name_length;
    }

    public String getName() {
        return this.field_4_name;
    }

    public byte getBuiltin() {
        return this.field_2_builtin_style;
    }

    public byte getOutlineStyleLevel() {
        return this.field_3_outline_style_level;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[STYLE]\n");
        stringBuffer.append("    .xf_index_raw    = ").append(Integer.toHexString(getIndex())).append("\n");
        stringBuffer.append("        .type        = ").append(Integer.toHexString(getType())).append("\n");
        stringBuffer.append("        .xf_index    = ").append(Integer.toHexString(getXFIndex())).append("\n");
        if (getType() == 1) {
            stringBuffer.append("    .builtin_style   = ").append(Integer.toHexString(getBuiltin())).append("\n");
            stringBuffer.append("    .outline_level   = ").append(Integer.toHexString(getOutlineStyleLevel())).append("\n");
        } else if (getType() == 0) {
            stringBuffer.append("    .name_length     = ").append(Integer.toHexString(getNameLength())).append("\n");
            stringBuffer.append("    .name            = ").append(getName()).append("\n");
        }
        stringBuffer.append("[/STYLE]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        LittleEndian.putShort(bArr, i + 0, sid);
        if (getType() == 1) {
            LittleEndian.putShort(bArr, i + 2, (short) 4);
        } else {
            LittleEndian.putShort(bArr, i + 2, (short) (getRecordSize() - 4));
        }
        LittleEndian.putShort(bArr, i + 4, getIndex());
        if (getType() == 1) {
            bArr[i + 6] = getBuiltin();
            bArr[i + 7] = getOutlineStyleLevel();
        } else {
            LittleEndian.putShort(bArr, i + 6, getNameLength());
            bArr[i + 8] = this.field_3_string_options;
            StringUtil.putCompressedUnicode(getName(), bArr, i + 9);
        }
        return getRecordSize();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        int nameLength;
        if (getType() == 1) {
            return 8;
        }
        if (this.fHighByte.isSet(this.field_3_string_options)) {
            nameLength = getNameLength() * 2;
        } else {
            nameLength = getNameLength();
        }
        return nameLength + 9;
    }
}
