package org.apache.poi.hssf.record;

import android.mtp.MtpConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
class SSTRecordSizeCalculator {
    private Map strings;
    private UnicodeString unistr = null;
    private int stringReminant = 0;
    private int unipos = 0;
    private boolean isRemainingString = false;
    private int totalBytesWritten = 0;
    private boolean finished = false;
    private boolean firstRecord = true;
    private int totalWritten = 0;
    private int recordSize = 0;
    private List recordLengths = new ArrayList();
    private int pos = 0;

    public SSTRecordSizeCalculator(Map map) {
        this.strings = map;
    }

    public int getRecordSize() {
        initVars();
        int iCalculateUnicodeSize = SSTSerializer.calculateUnicodeSize(this.strings);
        if (iCalculateUnicodeSize > 8216) {
            return sizeOverContinuation(iCalculateUnicodeSize);
        }
        int i = iCalculateUnicodeSize + 12;
        this.recordLengths.add(new Integer(iCalculateUnicodeSize));
        return i;
    }

    public List getRecordLengths() {
        return this.recordLengths;
    }

    private int sizeOverContinuation(int i) {
        while (!this.finished) {
            this.recordSize = 0;
            this.pos = 0;
            if (this.firstRecord) {
                addMaxLengthRecordSize();
            } else {
                this.pos = 0;
                int i2 = (i - this.totalBytesWritten) + (this.isRemainingString ? 1 : 0);
                int iMin = Math.min(MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED, i2);
                if (iMin == i2) {
                    this.finished = true;
                }
                this.recordSize = iMin + 4;
                this.recordLengths.add(new Integer(iMin));
                this.pos = 4;
            }
            if (this.isRemainingString) {
                calcReminant();
            }
            calcRemainingStrings();
            this.totalWritten += this.recordSize;
        }
        return this.totalWritten;
    }

    private void addMaxLengthRecordSize() {
        this.recordSize = 8228;
        this.pos = 12;
        this.firstRecord = false;
        this.recordLengths.add(new Integer(this.recordSize - 4));
    }

    private void calcRemainingStrings() {
        while (this.unipos < this.strings.size()) {
            int i = 8228 - this.pos;
            UnicodeString unicodeString = (UnicodeString) this.strings.get(new Integer(this.unipos));
            this.unistr = unicodeString;
            if (unicodeString.getRecordSize() > i) {
                if (i >= 3) {
                    int iMaxBrokenLength = this.unistr.maxBrokenLength(i);
                    this.totalBytesWritten += iMaxBrokenLength;
                    this.stringReminant = (this.unistr.getRecordSize() - iMaxBrokenLength) + 1;
                    if (i != iMaxBrokenLength) {
                        int i2 = this.recordSize - (i - iMaxBrokenLength);
                        List list = this.recordLengths;
                        list.set(list.size() - 1, new Integer(i2 - 4));
                        this.recordSize = i2;
                    }
                    this.isRemainingString = true;
                    this.unipos++;
                    return;
                }
                int i3 = this.recordSize - i;
                List list2 = this.recordLengths;
                list2.set(list2.size() - 1, new Integer(i3 - 4));
                this.recordSize = i3;
                return;
            }
            this.totalBytesWritten += this.unistr.getRecordSize();
            this.pos += this.unistr.getRecordSize();
            this.unipos++;
        }
    }

    private void calcReminant() {
        int i = this.pos;
        int i2 = 8228 - i;
        int i3 = this.stringReminant;
        if (i3 <= i2) {
            this.totalBytesWritten += i3 - 1;
            this.pos = i + i3;
            this.isRemainingString = false;
            return;
        }
        int iMaxBrokenLength = this.unistr.maxBrokenLength(i2);
        if (i2 != iMaxBrokenLength) {
            int i4 = this.recordSize - (i2 - iMaxBrokenLength);
            List list = this.recordLengths;
            list.set(list.size() - 1, new Integer(i4 - 4));
            this.recordSize = i4;
        }
        int i5 = iMaxBrokenLength - 1;
        this.totalBytesWritten += i5;
        this.pos += iMaxBrokenLength;
        this.stringReminant -= i5;
        this.isRemainingString = true;
    }

    private void initVars() {
        this.unistr = null;
        this.stringReminant = 0;
        this.unipos = 0;
        this.isRemainingString = false;
        this.totalBytesWritten = 0;
        this.finished = false;
        this.firstRecord = true;
        this.totalWritten = 0;
    }
}
