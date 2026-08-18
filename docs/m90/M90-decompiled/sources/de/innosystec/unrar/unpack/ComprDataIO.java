package de.innosystec.unrar.unpack;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.Volume;
import de.innosystec.unrar.crc.RarCRC;
import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.io.ReadOnlyAccessInputStream;
import de.innosystec.unrar.rarfile.FileHeader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes2.dex */
public class ComprDataIO {
    private final Archive archive;
    private long curPackRead;
    private long curPackWrite;
    private long curUnpRead;
    private long curUnpWrite;
    private char currentCommand;
    private int decryption;
    private int encryption;
    private InputStream inputStream;
    private int lastPercent;
    private boolean nextVolumeMissing;
    private OutputStream outputStream;
    private long packFileCRC;
    private boolean packVolume;
    private long packedCRC;
    private long processedArcSize;
    private boolean skipUnpCRC;
    private FileHeader subHead;
    private boolean testMode;
    private long totalArcSize;
    private long totalPackRead;
    private long unpArcSize;
    private long unpFileCRC;
    private long unpPackedSize;
    private boolean unpVolume;

    public ComprDataIO(Archive archive) {
        this.archive = archive;
    }

    public void init(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.unpPackedSize = 0L;
        this.testMode = false;
        this.skipUnpCRC = false;
        this.packVolume = false;
        this.unpVolume = false;
        this.nextVolumeMissing = false;
        this.encryption = 0;
        this.decryption = 0;
        this.totalPackRead = 0L;
        this.curUnpWrite = 0L;
        this.curUnpRead = 0L;
        this.curPackWrite = 0L;
        this.curPackRead = 0L;
        this.packedCRC = -1L;
        this.unpFileCRC = -1L;
        this.packFileCRC = -1L;
        this.lastPercent = -1;
        this.subHead = null;
        this.currentCommand = (char) 0;
        this.totalArcSize = 0L;
        this.processedArcSize = 0L;
    }

    public void init(FileHeader fileHeader) throws IOException {
        long positionInFile = fileHeader.getPositionInFile() + ((long) fileHeader.getHeaderSize());
        this.unpPackedSize = fileHeader.getFullPackSize();
        this.inputStream = new ReadOnlyAccessInputStream(this.archive.getRof(), positionInFile, positionInFile + this.unpPackedSize);
        this.subHead = fileHeader;
        this.curUnpRead = 0L;
        this.curPackWrite = 0L;
        this.packedCRC = -1L;
    }

    public int unpRead(byte[] bArr, int i, int i2) throws RarException, IOException {
        int i3 = 0;
        int i4 = 0;
        while (i2 > 0) {
            long j = i2;
            long j2 = this.unpPackedSize;
            i4 = this.inputStream.read(bArr, i, j > j2 ? (int) j2 : i2);
            if (i4 < 0) {
                throw new EOFException();
            }
            if (this.subHead.isSplitAfter()) {
                this.packedCRC = RarCRC.checkCrc((int) this.packedCRC, bArr, i, i4);
            }
            long j3 = i4;
            this.curUnpRead += j3;
            i3 += i4;
            i += i4;
            i2 -= i4;
            this.unpPackedSize -= j3;
            this.archive.bytesReadRead(i4);
            if (this.unpPackedSize != 0 || !this.subHead.isSplitAfter()) {
                break;
            }
            if (!Volume.mergeArchive(this.archive, this)) {
                this.nextVolumeMissing = true;
                return -1;
            }
        }
        return i4 != -1 ? i3 : i4;
    }

    public void unpWrite(byte[] bArr, int i, int i2) throws IOException {
        if (!this.testMode) {
            this.outputStream.write(bArr, i, i2);
        }
        this.curUnpWrite += (long) i2;
        if (this.skipUnpCRC) {
            return;
        }
        if (this.archive.isOldFormat()) {
            this.unpFileCRC = RarCRC.checkOldCrc((short) this.unpFileCRC, bArr, i2);
        } else {
            this.unpFileCRC = RarCRC.checkCrc((int) this.unpFileCRC, bArr, i, i2);
        }
    }

    public void setPackedSizeToRead(long j) {
        this.unpPackedSize = j;
    }

    public void setTestMode(boolean z) {
        this.testMode = z;
    }

    public void setSkipUnpCRC(boolean z) {
        this.skipUnpCRC = z;
    }

    public void setSubHeader(FileHeader fileHeader) {
        this.subHead = fileHeader;
    }

    public long getCurPackRead() {
        return this.curPackRead;
    }

    public void setCurPackRead(long j) {
        this.curPackRead = j;
    }

    public long getCurPackWrite() {
        return this.curPackWrite;
    }

    public void setCurPackWrite(long j) {
        this.curPackWrite = j;
    }

    public long getCurUnpRead() {
        return this.curUnpRead;
    }

    public void setCurUnpRead(long j) {
        this.curUnpRead = j;
    }

    public long getCurUnpWrite() {
        return this.curUnpWrite;
    }

    public void setCurUnpWrite(long j) {
        this.curUnpWrite = j;
    }

    public int getDecryption() {
        return this.decryption;
    }

    public void setDecryption(int i) {
        this.decryption = i;
    }

    public int getEncryption() {
        return this.encryption;
    }

    public void setEncryption(int i) {
        this.encryption = i;
    }

    public boolean isNextVolumeMissing() {
        return this.nextVolumeMissing;
    }

    public void setNextVolumeMissing(boolean z) {
        this.nextVolumeMissing = z;
    }

    public long getPackedCRC() {
        return this.packedCRC;
    }

    public void setPackedCRC(long j) {
        this.packedCRC = j;
    }

    public long getPackFileCRC() {
        return this.packFileCRC;
    }

    public void setPackFileCRC(long j) {
        this.packFileCRC = j;
    }

    public boolean isPackVolume() {
        return this.packVolume;
    }

    public void setPackVolume(boolean z) {
        this.packVolume = z;
    }

    public long getProcessedArcSize() {
        return this.processedArcSize;
    }

    public void setProcessedArcSize(long j) {
        this.processedArcSize = j;
    }

    public long getTotalArcSize() {
        return this.totalArcSize;
    }

    public void setTotalArcSize(long j) {
        this.totalArcSize = j;
    }

    public long getTotalPackRead() {
        return this.totalPackRead;
    }

    public void setTotalPackRead(long j) {
        this.totalPackRead = j;
    }

    public long getUnpArcSize() {
        return this.unpArcSize;
    }

    public void setUnpArcSize(long j) {
        this.unpArcSize = j;
    }

    public long getUnpFileCRC() {
        return this.unpFileCRC;
    }

    public void setUnpFileCRC(long j) {
        this.unpFileCRC = j;
    }

    public boolean isUnpVolume() {
        return this.unpVolume;
    }

    public void setUnpVolume(boolean z) {
        this.unpVolume = z;
    }

    public FileHeader getSubHeader() {
        return this.subHead;
    }
}
