package org.apache.poi.hssf.record;

import org.apache.poi.util.BinaryTree;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes3.dex */
class SSTDeserializer {
    private int charCount;
    private int continuationReadChars;
    private int continueSkipBytes = 0;
    private boolean extendedText;
    private int extensionLength;
    private boolean richText;
    private short runCount;
    private BinaryTree strings;
    private String unfinishedString;
    private boolean wideChar;

    private byte createOptionByte(boolean z, boolean z2, boolean z3) {
        return (byte) ((z ? 1 : 0) + (z3 ? 4 : 0) + (z2 ? 8 : 0));
    }

    public SSTDeserializer(BinaryTree binaryTree) {
        this.strings = binaryTree;
        initVars();
    }

    private void initVars() {
        this.runCount = (short) 0;
        this.continuationReadChars = 0;
        this.unfinishedString = "";
        this.wideChar = false;
        this.richText = false;
        this.extendedText = false;
        this.continueSkipBytes = 0;
    }

    public void manufactureStrings(byte[] bArr, int i) {
        initVars();
        int length = bArr.length;
        while (i < length) {
            int i2 = length - i;
            if (i2 > 0 && i2 < 2) {
                throw new RecordFormatException("Cannot get length of the last string in SSTRecord");
            }
            if (i2 == 2) {
                setContinuationCharsRead(0);
                this.unfinishedString = "";
                return;
            }
            int uShort = LittleEndian.getUShort(bArr, i);
            this.charCount = uShort;
            readStringHeader(bArr, i);
            boolean z = i2 < totalStringSize();
            if (z) {
                int iStringHeaderOverhead = i2 - stringHeaderOverhead();
                uShort = Math.min(uShort, calculateCharCount(iStringHeaderOverhead));
                setContinuationCharsRead(uShort);
                if (uShort == this.charCount) {
                    this.continueSkipBytes = offsetForContinuedRecord(0) - (iStringHeaderOverhead - calculateByteCount(uShort));
                }
            }
            processString(bArr, i, uShort);
            i += totalStringSize();
            if (z) {
                return;
            }
        }
    }

    private void readStringHeader(byte[] bArr, int i) {
        byte b = bArr[i + 2];
        this.wideChar = (b & 1) == 1;
        this.extendedText = (b & 4) == 4;
        boolean z = (b & 8) == 8;
        this.richText = z;
        this.runCount = (short) 0;
        if (z) {
            this.runCount = LittleEndian.getShort(bArr, i + 3);
        }
        this.extensionLength = 0;
        if (this.extendedText) {
            this.extensionLength = LittleEndian.getInt(bArr, i + 3 + (this.richText ? 2 : 0));
        }
    }

    private int processString(byte[] bArr, int i, int i2) {
        int iCalculateByteCount = calculateByteCount(i2) + 3;
        byte[] bArr2 = new byte[iCalculateByteCount];
        LittleEndian.putUShort(bArr2, 0, i2);
        bArr2[2] = bArr[i + 2];
        int i3 = iCalculateByteCount - 3;
        arraycopy(bArr, i + stringHeaderOverhead(), bArr2, 3, i3);
        UnicodeString unicodeString = new UnicodeString((short) 4095, (short) iCalculateByteCount, bArr2);
        setContinuationCharsRead(calculateCharCount(i3));
        if (isStringFinished()) {
            addToStringTable(this.strings, new Integer(this.strings.size()), unicodeString);
        } else {
            this.unfinishedString = unicodeString.getString();
        }
        return i3;
    }

    private boolean isStringFinished() {
        return getContinuationCharsRead() == this.charCount;
    }

    public static void addToStringTable(BinaryTree binaryTree, Integer num, UnicodeString unicodeString) {
        if (unicodeString.isRichText()) {
            unicodeString.setOptionFlags((byte) (unicodeString.getOptionFlags() & (-9)));
        }
        if (unicodeString.isExtendedText()) {
            unicodeString.setOptionFlags((byte) (unicodeString.getOptionFlags() & (-5)));
        }
        boolean z = false;
        while (!z) {
            try {
                binaryTree.put(num, unicodeString);
                z = true;
            } catch (Exception unused) {
                unicodeString.setString(new StringBuffer().append(unicodeString.getString()).append(" ").toString());
            }
        }
    }

    private int calculateCharCount(int i) {
        return i / (this.wideChar ? 2 : 1);
    }

    public void processContinueRecord(byte[] bArr) {
        if (isStringFinished()) {
            int i = this.continueSkipBytes;
            initVars();
            manufactureStrings(bArr, i);
        } else {
            this.wideChar = (bArr[0] & 1) == 1;
            if (stringSpansContinuation(bArr.length - 1)) {
                processEntireContinuation(bArr);
            } else {
                readStringRemainder(bArr);
            }
        }
    }

    private void readStringRemainder(byte[] bArr) {
        int iCalculateByteCount = calculateByteCount(this.charCount - getContinuationCharsRead());
        int i = iCalculateByteCount + 3;
        byte[] bArr2 = new byte[i];
        LittleEndian.putShort(bArr2, 0, (short) (this.charCount - getContinuationCharsRead()));
        bArr2[2] = createOptionByte(this.wideChar, this.richText, this.extendedText);
        arraycopy(bArr, 1, bArr2, 3, iCalculateByteCount);
        UnicodeString unicodeString = new UnicodeString((short) 4095, (short) i, bArr2, this.unfinishedString);
        addToStringTable(this.strings, new Integer(this.strings.size()), unicodeString);
        manufactureStrings(bArr, offsetForContinuedRecord(iCalculateByteCount));
    }

    private int stringSizeInBytes() {
        return calculateByteCount(this.charCount);
    }

    private int totalStringSize() {
        return stringSizeInBytes() + stringHeaderOverhead() + (this.runCount * 4) + this.extensionLength;
    }

    private int stringHeaderOverhead() {
        return (this.richText ? 2 : 0) + 3 + (this.extendedText ? 4 : 0);
    }

    private int offsetForContinuedRecord(int i) {
        int i2 = (this.runCount * 4) + i + this.extensionLength;
        return i != 0 ? i2 + 1 : i2;
    }

    private void processEntireContinuation(byte[] bArr) {
        int length = bArr.length - 1;
        int length2 = bArr.length + 2;
        byte[] bArr2 = new byte[length2];
        int iCalculateCharCount = calculateCharCount(length);
        LittleEndian.putShort(bArr2, 0, (short) iCalculateCharCount);
        arraycopy(bArr, 0, bArr2, 2, bArr.length);
        UnicodeString unicodeString = new UnicodeString((short) 4095, (short) length2, bArr2, this.unfinishedString);
        this.unfinishedString = unicodeString.getString();
        setContinuationCharsRead(getContinuationCharsRead() + iCalculateCharCount);
        if (getContinuationCharsRead() == this.charCount) {
            addToStringTable(this.strings, new Integer(this.strings.size()), unicodeString);
        }
    }

    private boolean stringSpansContinuation(int i) {
        return calculateByteCount(this.charCount - getContinuationCharsRead()) > i;
    }

    int getContinuationCharsRead() {
        return this.continuationReadChars;
    }

    private void setContinuationCharsRead(int i) {
        this.continuationReadChars = i;
    }

    private int calculateByteCount(int i) {
        return i * (this.wideChar ? 2 : 1);
    }

    private void arraycopy(byte[] bArr, int i, byte[] bArr2, int i2, int i3) {
        System.arraycopy(bArr, i, bArr2, i2, i3);
    }

    String getUnfinishedString() {
        return this.unfinishedString;
    }

    boolean isWideChar() {
        return this.wideChar;
    }
}
