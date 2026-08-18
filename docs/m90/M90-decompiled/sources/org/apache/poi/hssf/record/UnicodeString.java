package org.apache.poi.hssf.record;

import java.io.UnsupportedEncodingException;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes3.dex */
public class UnicodeString extends Record implements Comparable {
    public static final short sid = 4095;
    private final int EXT_BIT;
    private final int RICH_TEXT_BIT;
    private short field_1_charCount;
    private byte field_2_optionflags;
    private String field_3_string;

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s, int i) {
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 4095;
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void validateSid(short s) {
    }

    public UnicodeString() {
        this.RICH_TEXT_BIT = 8;
        this.EXT_BIT = 4;
    }

    public int hashCode() {
        String str = this.field_3_string;
        return this.field_1_charCount + (str != null ? str.hashCode() : 0);
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        UnicodeString unicodeString = (UnicodeString) obj;
        return this.field_1_charCount == unicodeString.field_1_charCount && this.field_2_optionflags == unicodeString.field_2_optionflags && this.field_3_string.equals(unicodeString.field_3_string);
    }

    public UnicodeString(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.RICH_TEXT_BIT = 8;
        this.EXT_BIT = 4;
    }

    public UnicodeString(short s, short s2, byte[] bArr, String str) {
        this(s, s2, bArr);
        this.field_3_string = new StringBuffer().append(str).append(this.field_3_string).toString();
        setCharCount();
    }

    @Override // org.apache.poi.hssf.record.Record
    protected void fillFields(byte[] bArr, short s) {
        this.field_1_charCount = LittleEndian.getShort(bArr, 0);
        byte b = bArr[2];
        this.field_2_optionflags = b;
        if ((b & 1) == 0) {
            try {
                this.field_3_string = new String(bArr, 3, getCharCount(), StringUtil.getPreferredEncoding());
                return;
            } catch (UnsupportedEncodingException e) {
                String message = e.getMessage();
                if (message == null) {
                    message = e.toString();
                }
                throw new RuntimeException(message);
            }
        }
        int charCount = getCharCount();
        char[] cArr = new char[charCount];
        for (int i = 0; i < charCount; i++) {
            cArr[i] = (char) LittleEndian.getShort(bArr, (i * 2) + 3);
        }
        this.field_3_string = new String(cArr);
    }

    public short getCharCount() {
        return this.field_1_charCount;
    }

    public void setCharCount(short s) {
        this.field_1_charCount = s;
    }

    public void setCharCount() {
        this.field_1_charCount = (short) this.field_3_string.length();
    }

    public byte getOptionFlags() {
        return this.field_2_optionflags;
    }

    public void setOptionFlags(byte b) {
        this.field_2_optionflags = b;
    }

    public String getString() {
        return this.field_3_string;
    }

    public void setString(String str) {
        this.field_3_string = str;
        if (getCharCount() < this.field_3_string.length()) {
            setCharCount();
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        return getString();
    }

    public String getDebugInfo() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[UNICODESTRING]\n");
        stringBuffer.append("    .charcount       = ").append(Integer.toHexString(getCharCount())).append("\n");
        stringBuffer.append("    .optionflags     = ").append(Integer.toHexString(getOptionFlags())).append("\n");
        stringBuffer.append("    .string          = ").append(getString()).append("\n");
        stringBuffer.append("[/UNICODESTRING]\n");
        return stringBuffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        getOptionFlags();
        LittleEndian.putShort(bArr, i + 0, getCharCount());
        bArr[i + 2] = getOptionFlags();
        try {
            String str = new String(getString().getBytes("Unicode"), "Unicode");
            if (getOptionFlags() == 0) {
                StringUtil.putCompressedUnicode(str, bArr, i + 3);
            } else {
                StringUtil.putUncompressedUnicode(str, bArr, i + 3);
            }
        } catch (Exception unused) {
            if (getOptionFlags() == 0) {
                StringUtil.putCompressedUnicode(getString(), bArr, i + 3);
            } else {
                StringUtil.putUncompressedUnicode(getString(), bArr, i + 3);
            }
        }
        return getRecordSize();
    }

    private boolean isUncompressedUnicode() {
        return (getOptionFlags() & 1) == 1;
    }

    @Override // org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        return (getString().length() * (isUncompressedUnicode() ? 2 : 1)) + 3;
    }

    @Override // java.lang.Comparable
    public int compareTo(Object obj) {
        return getString().compareTo(((UnicodeString) obj).getString());
    }

    public boolean isRichText() {
        return (getOptionFlags() & 8) != 0;
    }

    int maxBrokenLength(int i) {
        if (!isUncompressedUnicode()) {
            return i;
        }
        int i2 = i - 3;
        if (i2 % 2 == 1) {
            i2--;
        }
        return i2 + 3;
    }

    public boolean isExtendedText() {
        return (getOptionFlags() & 4) != 0;
    }
}
