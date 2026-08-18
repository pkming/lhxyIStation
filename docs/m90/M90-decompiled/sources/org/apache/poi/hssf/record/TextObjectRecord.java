package org.apache.poi.hssf.record;

import android.media.MediaPlayer;
import java.io.UnsupportedEncodingException;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes3.dex */
public class TextObjectRecord extends TextObjectBaseRecord {
    int continueRecordCount;
    HSSFRichTextString str;

    public TextObjectRecord() {
        this.str = new HSSFRichTextString("");
        this.continueRecordCount = 0;
    }

    public TextObjectRecord(short s, short s2, byte[] bArr) {
        super(s, s2, bArr);
        this.str = new HSSFRichTextString("");
        this.continueRecordCount = 0;
    }

    public TextObjectRecord(short s, short s2, byte[] bArr, int i) {
        super(s, s2, bArr, i);
        this.str = new HSSFRichTextString("");
        this.continueRecordCount = 0;
    }

    @Override // org.apache.poi.hssf.record.TextObjectBaseRecord, org.apache.poi.hssf.record.Record
    public int getRecordSize() {
        int iNumFormattingRuns;
        int length = 0;
        if (this.str.length() != 0) {
            length = (this.str.length() * 2) + 1 + 4;
            iNumFormattingRuns = ((this.str.numFormattingRuns() + 1) * 8) + 4;
        } else {
            iNumFormattingRuns = 0;
        }
        return super.getRecordSize() + length + iNumFormattingRuns;
    }

    @Override // org.apache.poi.hssf.record.TextObjectBaseRecord, org.apache.poi.hssf.record.Record
    public int serialize(int i, byte[] bArr) {
        HSSFRichTextString hSSFRichTextString = this.str;
        this.str = new HSSFRichTextString("");
        int iSerialize = super.serialize(i, bArr);
        this.str = hSSFRichTextString;
        int i2 = i + iSerialize;
        if (!hSSFRichTextString.toString().equals("")) {
            ContinueRecord continueRecordCreateContinue1 = createContinue1();
            ContinueRecord continueRecordCreateContinue2 = createContinue2();
            int iSerialize2 = continueRecordCreateContinue1.serialize(i2, bArr);
            int iSerialize3 = iSerialize + iSerialize2 + continueRecordCreateContinue2.serialize(i2 + iSerialize2, bArr);
            if (iSerialize3 == getRecordSize()) {
                return iSerialize3;
            }
            throw new RecordFormatException(new StringBuffer().append(iSerialize3).append(" bytes written but getRecordSize() reports ").append(getRecordSize()).toString());
        }
        if (iSerialize == getRecordSize()) {
            return iSerialize;
        }
        throw new RecordFormatException(new StringBuffer().append(iSerialize).append(" bytes written but getRecordSize() reports ").append(getRecordSize()).toString());
    }

    private ContinueRecord createContinue1() {
        ContinueRecord continueRecord = new ContinueRecord();
        byte[] bArr = new byte[(this.str.length() * 2) + 1];
        try {
            bArr[0] = 1;
            System.arraycopy(this.str.toString().getBytes(MediaPlayer.CHARSET_UTF_16LE), 0, bArr, 1, this.str.length() * 2);
            continueRecord.setData(bArr);
            return continueRecord;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private ContinueRecord createContinue2() {
        ContinueRecord continueRecord = new ContinueRecord();
        byte[] bArr = new byte[(this.str.numFormattingRuns() * 8) + 8];
        int i = 0;
        for (int i2 = 0; i2 < this.str.numFormattingRuns(); i2++) {
            LittleEndian.putShort(bArr, i, (short) this.str.getIndexOfFormattingRun(i2));
            int i3 = i + 2;
            LittleEndian.putShort(bArr, i3, this.str.getFontOfFormattingRun(i2) == -1 ? (short) 0 : this.str.getFontOfFormattingRun(i2));
            i = i3 + 2 + 4;
        }
        LittleEndian.putShort(bArr, i, (short) this.str.length());
        LittleEndian.putShort(bArr, i + 2, (short) 0);
        continueRecord.setData(bArr);
        return continueRecord;
    }

    @Override // org.apache.poi.hssf.record.Record
    public void processContinueRecord(byte[] bArr) {
        if (this.continueRecordCount == 0) {
            processRawString(bArr);
        } else {
            processFontRuns(bArr);
        }
        this.continueRecordCount++;
    }

    private void processFontRuns(byte[] bArr) {
        int i = 0;
        while (true) {
            short s = LittleEndian.getShort(bArr, i);
            int i2 = i + 2;
            short s2 = LittleEndian.getShort(bArr, i2);
            i = i2 + 2 + 4;
            if (s >= this.str.length()) {
                return;
            }
            HSSFRichTextString hSSFRichTextString = this.str;
            hSSFRichTextString.applyFont(s, hSSFRichTextString.length(), s2);
        }
    }

    private void processRawString(byte[] bArr) {
        String str;
        try {
            if (bArr[0] == 0) {
                str = new String(bArr, 1, getTextLength(), StringUtil.getPreferredEncoding());
            } else {
                str = new String(bArr, 1, getTextLength() * 2, MediaPlayer.CHARSET_UTF_16LE);
            }
            this.str = new HSSFRichTextString(str);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public HSSFRichTextString getStr() {
        return this.str;
    }

    public void setStr(HSSFRichTextString hSSFRichTextString) {
        this.str = hSSFRichTextString;
    }

    @Override // org.apache.poi.hssf.record.TextObjectBaseRecord, org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[TXO]\n");
        stringBuffer.append("    .options              = ").append("0x").append(HexDump.toHex(getOptions())).append(" (").append((int) getOptions()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("         .reserved1                = ").append(isReserved1()).append('\n');
        stringBuffer.append("         .HorizontalTextAlignment     = ").append((int) getHorizontalTextAlignment()).append('\n');
        stringBuffer.append("         .VerticalTextAlignment     = ").append((int) getVerticalTextAlignment()).append('\n');
        stringBuffer.append("         .reserved2                = ").append((int) getReserved2()).append('\n');
        stringBuffer.append("         .textLocked               = ").append(isTextLocked()).append('\n');
        stringBuffer.append("         .reserved3                = ").append((int) getReserved3()).append('\n');
        stringBuffer.append("    .textOrientation      = ").append("0x").append(HexDump.toHex(getTextOrientation())).append(" (").append((int) getTextOrientation()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .reserved4            = ").append("0x").append(HexDump.toHex(getReserved4())).append(" (").append((int) getReserved4()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .reserved5            = ").append("0x").append(HexDump.toHex(getReserved5())).append(" (").append((int) getReserved5()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .reserved6            = ").append("0x").append(HexDump.toHex(getReserved6())).append(" (").append((int) getReserved6()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .textLength           = ").append("0x").append(HexDump.toHex(getTextLength())).append(" (").append((int) getTextLength()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .reserved7            = ").append("0x").append(HexDump.toHex(getReserved7())).append(" (").append(getReserved7()).append(" )");
        stringBuffer.append(System.getProperty("line.separator"));
        stringBuffer.append("    .string = ").append(this.str).append('\n');
        stringBuffer.append("[/TXO]\n");
        return stringBuffer.toString();
    }
}
