package de.innosystec.unrar.unpack.ppm;

import android.os.Trace;
import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.unpack.Unpack;
import java.io.IOException;

/* JADX INFO: loaded from: classes2.dex */
public class RangeCoder {
    public static final int BOT = 32768;
    public static final int TOP = 16777216;
    private static final long uintMask = 4294967295L;
    private long code;
    private long low;
    private long range;
    private final SubRange subRange = new SubRange();
    private Unpack unpackRead;

    public SubRange getSubRange() {
        return this.subRange;
    }

    public void initDecoder(Unpack unpack) throws RarException, IOException {
        this.unpackRead = unpack;
        this.code = 0L;
        this.low = 0L;
        this.range = 4294967295L;
        for (int i = 0; i < 4; i++) {
            this.code = ((this.code << 8) | ((long) getChar())) & 4294967295L;
        }
    }

    public int getCurrentCount() {
        long scale = (this.range / this.subRange.getScale()) & 4294967295L;
        this.range = scale;
        return (int) ((this.code - this.low) / scale);
    }

    public long getCurrentShiftCount(int i) {
        long j = this.range >>> i;
        this.range = j;
        return 4294967295L & ((this.code - this.low) / j);
    }

    public void decode() {
        this.low = (this.low + (this.range * this.subRange.getLowCount())) & 4294967295L;
        this.range = (this.range * (this.subRange.getHighCount() - this.subRange.getLowCount())) & 4294967295L;
    }

    private int getChar() throws RarException, IOException {
        return this.unpackRead.getChar();
    }

    public void ariDecNormalize() throws RarException, IOException {
        boolean z = false;
        while (true) {
            long j = this.low;
            long j2 = this.range;
            if (((j + j2) ^ j) >= 16777216) {
                z = j2 < Trace.TRACE_TAG_RS;
                if (!z) {
                    return;
                }
            }
            if (z) {
                this.range = (-j) & 32767 & 4294967295L;
                z = false;
            }
            this.code = ((this.code << 8) | ((long) getChar())) & 4294967295L;
            this.range = (this.range << 8) & 4294967295L;
            this.low = (this.low << 8) & 4294967295L;
        }
    }

    public String toString() {
        return "RangeCoder[\n  low=" + this.low + "\n  code=" + this.code + "\n  range=" + this.range + "\n  subrange=" + this.subRange + "]";
    }

    public static class SubRange {
        private long highCount;
        private long lowCount;
        private long scale;

        public long getHighCount() {
            return this.highCount;
        }

        public void setHighCount(long j) {
            this.highCount = j & 4294967295L;
        }

        public long getLowCount() {
            return this.lowCount & 4294967295L;
        }

        public void setLowCount(long j) {
            this.lowCount = j & 4294967295L;
        }

        public long getScale() {
            return this.scale;
        }

        public void setScale(long j) {
            this.scale = j & 4294967295L;
        }

        public void incScale(int i) {
            setScale(getScale() + ((long) i));
        }

        public String toString() {
            return "SubRange[\n  lowCount=" + this.lowCount + "\n  highCount=" + this.highCount + "\n  scale=" + this.scale + "]";
        }
    }
}
