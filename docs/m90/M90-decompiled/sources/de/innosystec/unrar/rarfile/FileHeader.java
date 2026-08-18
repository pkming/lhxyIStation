package de.innosystec.unrar.rarfile;

import de.innosystec.unrar.io.Raw;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: classes2.dex */
public class FileHeader extends BlockHeader {
    private static final byte NEWLHD_SIZE = 32;
    private static final byte SALT_SIZE = 8;
    private Date aTime;
    private Date arcTime;
    private Date cTime;
    private int fileAttr;
    private final int fileCRC;
    private String fileName;
    private final byte[] fileNameBytes;
    private String fileNameW;
    private final int fileTime;
    private long fullPackSize;
    private long fullUnpackSize;
    private int highPackSize;
    private int highUnpackSize;
    private final HostSystem hostOS;
    private final Log logger;
    private Date mTime;
    private short nameSize;
    private int recoverySectors;
    private final byte[] salt;
    private byte[] subData;
    private int subFlags;
    private byte unpMethod;
    private long unpSize;
    private byte unpVersion;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v35 */
    /* JADX WARN: Type inference failed for: r4v36 */
    public FileHeader(BlockHeader blockHeader, byte[] bArr) {
        byte[] bArr2;
        super(blockHeader);
        this.logger = LogFactory.getLog(FileHeader.class.getName());
        this.salt = new byte[8];
        this.recoverySectors = -1;
        this.unpSize = Raw.readIntLittleEndian(bArr, 0);
        this.hostOS = HostSystem.findHostSystem(bArr[4]);
        this.fileCRC = Raw.readIntLittleEndian(bArr, 5);
        this.fileTime = Raw.readIntLittleEndian(bArr, 9);
        this.unpVersion = (byte) (this.unpVersion | (bArr[13] & 255));
        this.unpMethod = (byte) (this.unpMethod | (bArr[14] & 255));
        this.nameSize = Raw.readShortLittleEndian(bArr, 15);
        this.fileAttr = Raw.readIntLittleEndian(bArr, 17);
        int i = 21;
        if (isLargeBlock()) {
            this.highPackSize = Raw.readIntLittleEndian(bArr, 21);
            this.highUnpackSize = Raw.readIntLittleEndian(bArr, 25);
            i = 29;
        } else {
            this.highPackSize = 0;
            this.highUnpackSize = 0;
            if (this.unpSize == -1) {
                this.unpSize = -1L;
                this.highUnpackSize = Integer.MAX_VALUE;
            }
        }
        long j = this.fullPackSize | ((long) this.highPackSize);
        this.fullPackSize = j;
        long j2 = j << 32;
        this.fullPackSize = j2;
        this.fullPackSize = j2 | ((long) getPackSize());
        long j3 = this.fullUnpackSize | ((long) this.highUnpackSize);
        this.fullUnpackSize = j3;
        long j4 = j3 << 32;
        this.fullUnpackSize = j4;
        this.fullUnpackSize = j4 | this.unpSize;
        short s = this.nameSize;
        int i2 = s > 4096 ? 4096 : s;
        this.nameSize = i2;
        this.fileNameBytes = new byte[i2];
        for (int i3 = 0; i3 < this.nameSize; i3++) {
            this.fileNameBytes[i3] = bArr[i];
            i++;
        }
        if (isFileHeader()) {
            if (isUnicode()) {
                this.fileName = "";
                this.fileNameW = "";
                int i4 = 0;
                while (true) {
                    bArr2 = this.fileNameBytes;
                    if (i4 >= bArr2.length || bArr2[i4] == 0) {
                        break;
                    } else {
                        i4++;
                    }
                }
                byte[] bArr3 = new byte[i4];
                System.arraycopy(bArr2, 0, bArr3, 0, i4);
                this.fileName = new String(bArr3);
                if (i4 != this.nameSize) {
                    this.fileNameW = FileNameDecoder.decode(this.fileNameBytes, i4 + 1);
                }
            } else {
                this.fileName = new String(this.fileNameBytes);
                this.fileNameW = "";
            }
        }
        if (UnrarHeadertype.NewSubHeader.equals(this.headerType)) {
            int i5 = (this.headerSize - 32) - this.nameSize;
            i5 = hasSalt() ? i5 - 8 : i5;
            if (i5 > 0) {
                this.subData = new byte[i5];
                for (int i6 = 0; i6 < i5; i6++) {
                    this.subData[i6] = bArr[i];
                    i++;
                }
            }
            if (NewSubHeaderType.SUBHEAD_TYPE_RR.byteEquals(this.fileNameBytes)) {
                byte[] bArr4 = this.subData;
                this.recoverySectors = bArr4[8] + (bArr4[9] << 8) + (bArr4[10] << 16) + (bArr4[11] << 24);
            }
        }
        if (hasSalt()) {
            for (int i7 = 0; i7 < 8; i7++) {
                this.salt[i7] = bArr[i];
                i++;
            }
        }
        this.mTime = getDateDos(this.fileTime);
    }

    @Override // de.innosystec.unrar.rarfile.BlockHeader, de.innosystec.unrar.rarfile.BaseBlock
    public void print() {
        super.print();
        StringBuilder sb = new StringBuilder();
        sb.append("unpSize: " + getUnpSize());
        sb.append("\nHostOS: " + this.hostOS.name());
        sb.append("\nMDate: " + this.mTime);
        sb.append("\nFileName: " + getFileNameString());
        sb.append("\nunpMethod: " + Integer.toHexString(getUnpMethod()));
        sb.append("\nunpVersion: " + Integer.toHexString(getUnpVersion()));
        sb.append("\nfullpackedsize: " + getFullPackSize());
        sb.append("\nfullunpackedsize: " + getFullUnpackSize());
        sb.append("\nisEncrypted: " + isEncrypted());
        sb.append("\nisfileHeader: " + isFileHeader());
        sb.append("\nisSolid: " + isSolid());
        sb.append("\nisSplitafter: " + isSplitAfter());
        sb.append("\nisSplitBefore:" + isSplitBefore());
        sb.append("\nunpSize: " + getUnpSize());
        sb.append("\ndataSize: " + getDataSize());
        sb.append("\nisUnicode: " + isUnicode());
        sb.append("\nhasVolumeNumber: " + hasVolumeNumber());
        sb.append("\nhasArchiveDataCRC: " + hasArchiveDataCRC());
        sb.append("\nhasSalt: " + hasSalt());
        sb.append("\nhasEncryptVersions: " + hasEncryptVersion());
        sb.append("\nisSubBlock: " + isSubBlock());
        this.logger.info(sb.toString());
    }

    private Date getDateDos(int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, (i >>> 25) + 1980);
        calendar.set(2, ((i >>> 21) & 15) - 1);
        calendar.set(5, (i >>> 16) & 31);
        calendar.set(11, (i >>> 11) & 31);
        calendar.set(12, (i >>> 5) & 63);
        calendar.set(13, (i & 31) * 2);
        return calendar.getTime();
    }

    public Date getArcTime() {
        return this.arcTime;
    }

    public void setArcTime(Date date) {
        this.arcTime = date;
    }

    public Date getATime() {
        return this.aTime;
    }

    public void setATime(Date date) {
        this.aTime = date;
    }

    public Date getCTime() {
        return this.cTime;
    }

    public void setCTime(Date date) {
        this.cTime = date;
    }

    public int getFileAttr() {
        return this.fileAttr;
    }

    public void setFileAttr(int i) {
        this.fileAttr = i;
    }

    public int getFileCRC() {
        return this.fileCRC;
    }

    public byte[] getFileNameByteArray() {
        return this.fileNameBytes;
    }

    public String getFileNameString() {
        return this.fileName;
    }

    public void setFileName(String str) {
        this.fileName = str;
    }

    public String getFileNameW() {
        return this.fileNameW;
    }

    public void setFileNameW(String str) {
        this.fileNameW = str;
    }

    public int getHighPackSize() {
        return this.highPackSize;
    }

    public int getHighUnpackSize() {
        return this.highUnpackSize;
    }

    public HostSystem getHostOS() {
        return this.hostOS;
    }

    public Date getMTime() {
        return this.mTime;
    }

    public void setMTime(Date date) {
        this.mTime = date;
    }

    public short getNameSize() {
        return this.nameSize;
    }

    public int getRecoverySectors() {
        return this.recoverySectors;
    }

    public byte[] getSalt() {
        return this.salt;
    }

    public byte[] getSubData() {
        return this.subData;
    }

    public int getSubFlags() {
        return this.subFlags;
    }

    public byte getUnpMethod() {
        return this.unpMethod;
    }

    public long getUnpSize() {
        return this.unpSize;
    }

    public byte getUnpVersion() {
        return this.unpVersion;
    }

    public long getFullPackSize() {
        return this.fullPackSize;
    }

    public long getFullUnpackSize() {
        return this.fullUnpackSize;
    }

    public String toString() {
        return super.toString();
    }

    public boolean isSplitAfter() {
        return (this.flags & 2) != 0;
    }

    public boolean isSplitBefore() {
        return (this.flags & 1) != 0;
    }

    public boolean isSolid() {
        return (this.flags & 16) != 0;
    }

    public boolean isEncrypted() {
        return (this.flags & 4) != 0;
    }

    public boolean isUnicode() {
        return (this.flags & 512) != 0;
    }

    public boolean isFileHeader() {
        return UnrarHeadertype.FileHeader.equals(this.headerType);
    }

    public boolean hasSalt() {
        return (this.flags & BaseBlock.LHD_SALT) != 0;
    }

    public boolean isLargeBlock() {
        return (this.flags & 256) != 0;
    }

    public boolean isDirectory() {
        return (this.flags & 224) == 224;
    }
}
