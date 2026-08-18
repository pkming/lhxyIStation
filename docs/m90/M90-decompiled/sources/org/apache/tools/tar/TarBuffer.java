package org.apache.tools.tar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/* JADX INFO: loaded from: classes3.dex */
public class TarBuffer {
    public static final int DEFAULT_BLKSIZE = 10240;
    public static final int DEFAULT_RCDSIZE = 512;
    private final byte[] blockBuffer;
    private final int blockSize;
    private int currBlkIdx;
    private int currRecIdx;
    private boolean debug;
    private InputStream inStream;
    private OutputStream outStream;
    private final int recordSize;
    private final int recsPerBlock;

    public TarBuffer(InputStream inputStream) {
        this(inputStream, 10240);
    }

    public TarBuffer(InputStream inputStream, int i) {
        this(inputStream, i, 512);
    }

    public TarBuffer(InputStream inputStream, int i, int i2) {
        this(inputStream, null, i, i2);
    }

    public TarBuffer(OutputStream outputStream) {
        this(outputStream, 10240);
    }

    public TarBuffer(OutputStream outputStream, int i) {
        this(outputStream, i, 512);
    }

    public TarBuffer(OutputStream outputStream, int i, int i2) {
        this(null, outputStream, i, i2);
    }

    private TarBuffer(InputStream inputStream, OutputStream outputStream, int i, int i2) {
        this.inStream = inputStream;
        this.outStream = outputStream;
        this.debug = false;
        this.blockSize = i;
        this.recordSize = i2;
        int i3 = i / i2;
        this.recsPerBlock = i3;
        this.blockBuffer = new byte[i];
        if (inputStream != null) {
            this.currBlkIdx = -1;
            this.currRecIdx = i3;
        } else {
            this.currBlkIdx = 0;
            this.currRecIdx = 0;
        }
    }

    public int getBlockSize() {
        return this.blockSize;
    }

    public int getRecordSize() {
        return this.recordSize;
    }

    public void setDebug(boolean z) {
        this.debug = z;
    }

    public boolean isEOFRecord(byte[] bArr) {
        int recordSize = getRecordSize();
        for (int i = 0; i < recordSize; i++) {
            if (bArr[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public void skipRecord() throws IOException {
        if (this.debug) {
            System.err.println("SkipRecord: recIdx = " + this.currRecIdx + " blkIdx = " + this.currBlkIdx);
        }
        if (this.inStream == null) {
            throw new IOException("reading (via skip) from an output buffer");
        }
        if (this.currRecIdx < this.recsPerBlock || readBlock()) {
            this.currRecIdx++;
        }
    }

    public byte[] readRecord() throws IOException {
        if (this.debug) {
            System.err.println("ReadRecord: recIdx = " + this.currRecIdx + " blkIdx = " + this.currBlkIdx);
        }
        if (this.inStream == null) {
            if (this.outStream == null) {
                throw new IOException("input buffer is closed");
            }
            throw new IOException("reading from an output buffer");
        }
        if (this.currRecIdx >= this.recsPerBlock && !readBlock()) {
            return null;
        }
        int i = this.recordSize;
        byte[] bArr = new byte[i];
        System.arraycopy(this.blockBuffer, this.currRecIdx * i, bArr, 0, i);
        this.currRecIdx++;
        return bArr;
    }

    private boolean readBlock() throws IOException {
        if (this.debug) {
            System.err.println("ReadBlock: blkIdx = " + this.currBlkIdx);
        }
        if (this.inStream == null) {
            throw new IOException("reading from an output buffer");
        }
        this.currRecIdx = 0;
        int i = this.blockSize;
        int i2 = 0;
        while (true) {
            if (i <= 0) {
                break;
            }
            long j = this.inStream.read(this.blockBuffer, i2, i);
            if (j != -1) {
                i2 = (int) (((long) i2) + j);
                i = (int) (((long) i) - j);
                if (j != this.blockSize && this.debug) {
                    System.err.println("ReadBlock: INCOMPLETE READ " + j + " of " + this.blockSize + " bytes read.");
                }
            } else {
                if (i2 == 0) {
                    return false;
                }
                Arrays.fill(this.blockBuffer, i2, i + i2, (byte) 0);
            }
        }
        this.currBlkIdx++;
        return true;
    }

    public int getCurrentBlockNum() {
        return this.currBlkIdx;
    }

    public int getCurrentRecordNum() {
        return this.currRecIdx - 1;
    }

    public void writeRecord(byte[] bArr) throws IOException {
        if (this.debug) {
            System.err.println("WriteRecord: recIdx = " + this.currRecIdx + " blkIdx = " + this.currBlkIdx);
        }
        if (this.outStream == null) {
            if (this.inStream == null) {
                throw new IOException("Output buffer is closed");
            }
            throw new IOException("writing to an input buffer");
        }
        if (bArr.length != this.recordSize) {
            throw new IOException("record to write has length '" + bArr.length + "' which is not the record size of '" + this.recordSize + "'");
        }
        if (this.currRecIdx >= this.recsPerBlock) {
            writeBlock();
        }
        byte[] bArr2 = this.blockBuffer;
        int i = this.currRecIdx;
        int i2 = this.recordSize;
        System.arraycopy(bArr, 0, bArr2, i * i2, i2);
        this.currRecIdx++;
    }

    public void writeRecord(byte[] bArr, int i) throws IOException {
        if (this.debug) {
            System.err.println("WriteRecord: recIdx = " + this.currRecIdx + " blkIdx = " + this.currBlkIdx);
        }
        if (this.outStream == null) {
            if (this.inStream == null) {
                throw new IOException("Output buffer is closed");
            }
            throw new IOException("writing to an input buffer");
        }
        if (this.recordSize + i > bArr.length) {
            throw new IOException("record has length '" + bArr.length + "' with offset '" + i + "' which is less than the record size of '" + this.recordSize + "'");
        }
        if (this.currRecIdx >= this.recsPerBlock) {
            writeBlock();
        }
        byte[] bArr2 = this.blockBuffer;
        int i2 = this.currRecIdx;
        int i3 = this.recordSize;
        System.arraycopy(bArr, i, bArr2, i2 * i3, i3);
        this.currRecIdx++;
    }

    private void writeBlock() throws IOException {
        if (this.debug) {
            System.err.println("WriteBlock: blkIdx = " + this.currBlkIdx);
        }
        OutputStream outputStream = this.outStream;
        if (outputStream == null) {
            throw new IOException("writing to an input buffer");
        }
        outputStream.write(this.blockBuffer, 0, this.blockSize);
        this.outStream.flush();
        this.currRecIdx = 0;
        this.currBlkIdx++;
        Arrays.fill(this.blockBuffer, (byte) 0);
    }

    void flushBlock() throws IOException {
        if (this.debug) {
            System.err.println("TarBuffer.flushBlock() called.");
        }
        if (this.outStream == null) {
            throw new IOException("writing to an input buffer");
        }
        if (this.currRecIdx > 0) {
            writeBlock();
        }
    }

    public void close() throws IOException {
        if (this.debug) {
            System.err.println("TarBuffer.closeBuffer().");
        }
        if (this.outStream != null) {
            flushBlock();
            if (this.outStream == System.out || this.outStream == System.err) {
                return;
            }
            this.outStream.close();
            this.outStream = null;
            return;
        }
        InputStream inputStream = this.inStream;
        if (inputStream != null) {
            if (inputStream != System.in) {
                this.inStream.close();
            }
            this.inStream = null;
        }
    }
}
