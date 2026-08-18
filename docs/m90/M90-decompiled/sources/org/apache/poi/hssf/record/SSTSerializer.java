package org.apache.poi.hssf.record;

import java.util.List;
import java.util.Map;
import org.apache.poi.util.BinaryTree;

/* JADX INFO: loaded from: classes3.dex */
class SSTSerializer {
    int[] bucketAbsoluteOffsets;
    int[] bucketRelativeOffsets;
    private int numStrings;
    private int numUniqueStrings;
    private List recordLengths;
    private SSTRecordHeader sstRecordHeader;
    int startOfRecord;
    int startOfSST;
    private BinaryTree strings;

    public SSTSerializer(List list, BinaryTree binaryTree, int i, int i2) {
        this.recordLengths = list;
        this.strings = binaryTree;
        this.numStrings = i;
        this.numUniqueStrings = i2;
        this.sstRecordHeader = new SSTRecordHeader(i, i2);
        int numberOfInfoRecsForStrings = ExtSSTRecord.getNumberOfInfoRecsForStrings(binaryTree.size());
        this.bucketAbsoluteOffsets = new int[numberOfInfoRecsForStrings];
        this.bucketRelativeOffsets = new int[numberOfInfoRecsForStrings];
    }

    public int serialize(int i, int i2, byte[] bArr) {
        if (calculateUnicodeSize() > 8216) {
            serializeLargeRecord(i, 0, bArr, i2);
        } else {
            serializeSingleSSTRecord(bArr, i2, 0);
        }
        return i;
    }

    public static int calculateUnicodeSize(Map map) {
        int recordSize = 0;
        for (int i = 0; i < map.size(); i++) {
            recordSize += getUnicodeString(map, i).getRecordSize();
        }
        return recordSize;
    }

    public int calculateUnicodeSize() {
        return calculateUnicodeSize(this.strings);
    }

    private void serializeSingleSSTRecord(byte[] bArr, int i, int i2) {
        int i3;
        int recordSize = 12;
        this.sstRecordHeader.writeSSTHeader(bArr, i + 0, (((Integer) this.recordLengths.get(i2)).intValue() + 12) - 4);
        for (int i4 = 0; i4 < this.strings.size(); i4++) {
            if (i4 % 8 == 0 && (i3 = i4 / 8) < 128) {
                this.bucketAbsoluteOffsets[i3] = recordSize;
                this.bucketRelativeOffsets[i3] = recordSize;
            }
            System.arraycopy(getUnicodeString(i4).serialize(), 0, bArr, recordSize + i, getUnicodeString(i4).getRecordSize());
            recordSize += getUnicodeString(i4).getRecordSize();
        }
    }

    private void serializeLargeRecord(int i, int i2, byte[] bArr, int i3) {
        byte[] bArr2;
        int i4;
        this.startOfSST = i3;
        byte[] bArrWritePartString = null;
        int i5 = 0;
        boolean z = true;
        boolean z2 = false;
        int i6 = 0;
        int i7 = i2;
        while (i5 != i) {
            int i8 = i7 + 1;
            int iIntValue = ((Integer) this.recordLengths.get(i7)).intValue();
            RecordProcessor recordProcessor = new RecordProcessor(bArr, iIntValue, this.numStrings, this.numUniqueStrings);
            int i9 = i3 + i5;
            this.startOfRecord = i9;
            recordProcessor.writeRecordHeader(i3, i5, iIntValue, z);
            if (z2) {
                z2 = bArrWritePartString.length > recordProcessor.getAvailable();
                bArrWritePartString = recordProcessor.writeStringRemainder(z2, bArrWritePartString, i3, i5);
            }
            while (true) {
                if (i6 >= this.strings.size()) {
                    bArr2 = bArrWritePartString;
                    break;
                }
                UnicodeString unicodeString = getUnicodeString(i6);
                if (i6 % 8 != 0 || (i4 = i6 / 8) >= 128) {
                    bArr2 = bArrWritePartString;
                } else {
                    this.bucketAbsoluteOffsets[i4] = (i9 + recordProcessor.getRecordOffset()) - this.startOfSST;
                    bArr2 = bArrWritePartString;
                    this.bucketRelativeOffsets[i4] = (recordProcessor.getRecordOffset() + i9) - this.startOfRecord;
                }
                if (unicodeString.getRecordSize() <= recordProcessor.getAvailable()) {
                    recordProcessor.writeWholeString(unicodeString, i3, i5);
                    i6++;
                    bArrWritePartString = bArr2;
                } else if (recordProcessor.getAvailable() >= 3) {
                    i6++;
                    bArrWritePartString = recordProcessor.writePartString(unicodeString, i3, i5);
                    z2 = true;
                }
            }
            bArrWritePartString = bArr2;
            i5 += iIntValue + 4;
            i7 = i8;
            z = false;
        }
    }

    private UnicodeString getUnicodeString(int i) {
        return getUnicodeString(this.strings, i);
    }

    private static UnicodeString getUnicodeString(Map map, int i) {
        return (UnicodeString) map.get(new Integer(i));
    }

    public int getRecordSize() {
        SSTRecordSizeCalculator sSTRecordSizeCalculator = new SSTRecordSizeCalculator(this.strings);
        int recordSize = sSTRecordSizeCalculator.getRecordSize();
        this.recordLengths = sSTRecordSizeCalculator.getRecordLengths();
        return recordSize;
    }

    public List getRecordLengths() {
        return this.recordLengths;
    }

    public int[] getBucketAbsoluteOffsets() {
        return this.bucketAbsoluteOffsets;
    }

    public int[] getBucketRelativeOffsets() {
        return this.bucketRelativeOffsets;
    }
}
